------------ PROCEDIMIENTOS ALMACENADOS

USE NOVA_ONLINE;
GO

------------ ALMACEN 1: Productos

CREATE PROCEDURE SP_ProductoExiste                 -- responde si el codigo ya esta registrado
    @Codigo VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT CASE WHEN EXISTS (SELECT 1 FROM Productos WHERE Codigo = @Codigo)
                THEN 1 ELSE 0 END AS Existe;          -- 1 = existe, 0 = no existe
END
GO

CREATE PROCEDURE SP_InsertarProducto               -- da de alta un producto nuevo
    @Codigo VARCHAR(10),
    @Nombre VARCHAR(90),
    @Precio DECIMAL(10,2)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO Productos (Codigo, Nombre, Precio) VALUES (@Codigo, @Nombre, @Precio);
    -- el trigger trg_Inventario_AlInsertarProducto crea el inventario en 0 solo
END
GO

CREATE PROCEDURE SP_ActualizarProducto             -- modifica nombre/precio de un producto existente
    @Codigo VARCHAR(10),
    @Nombre VARCHAR(90),
    @Precio DECIMAL(10,2)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE Productos SET Nombre = @Nombre, Precio = @Precio WHERE Codigo = @Codigo;
END
GO

------------ ALMACEN2: Proveedores

CREATE PROCEDURE SP_ProveedorExiste                -- responde si la cedula juridica ya esta registrada
    @CedulaJuridica VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT CASE WHEN EXISTS (SELECT 1 FROM Proveedores WHERE Cedula_Juridica = @CedulaJuridica)
                THEN 1 ELSE 0 END AS Existe;
END
GO

CREATE PROCEDURE SP_InsertarProveedor              -- da de alta un proveedor nuevo
    @CedulaJuridica VARCHAR(10),
    @Nombre VARCHAR(100),
    @NombreContacto VARCHAR(75),
    @Telefono VARCHAR(8),
    @CorreoElectronico VARCHAR(75),
    @Estado CHAR(1)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO Proveedores (Cedula_Juridica, Nombre, Nombre_Contacto, Telefono, Correo, Estado)
    VALUES (@CedulaJuridica, @Nombre, @NombreContacto, @Telefono, @CorreoElectronico, @Estado);
END
GO

CREATE PROCEDURE SP_ActualizarProveedor            -- modifica los datos de un proveedor existente
    @CedulaJuridica VARCHAR(10),
    @Nombre VARCHAR(100),
    @NombreContacto VARCHAR(75),
    @Telefono VARCHAR(8),
    @CorreoElectronico VARCHAR(75),
    @Estado CHAR(1)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE Proveedores
    SET Nombre = @Nombre,
        Nombre_Contacto = @NombreContacto,
        Telefono = @Telefono,
        Correo = @CorreoElectronico,
        Estado = @Estado
    WHERE Cedula_Juridica = @CedulaJuridica;
END
GO

------------ ALMACEN 3: Compras

CREATE PROCEDURE SP_InsertarCompra                 -- inserta la cabecera de una compra
    @NumeroIngreso VARCHAR(10),
    @FechaCompra VARCHAR(10),                        -- llega como texto yyyyMMdd, se convierte a DATE
    @CedulaJuridica VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO Compras (Numero_Ingreso, Fecha_Compra, Cedula_Juridica)
    VALUES (@NumeroIngreso, CONVERT(DATE, @FechaCompra, 112), @CedulaJuridica); -- 112 = formato yyyymmdd
END
GO

CREATE PROCEDURE SP_InsertarDetalleCompra          -- inserta un producto comprado (uno por llamada)
    @NumeroIngreso VARCHAR(10),
    @CodigoProducto VARCHAR(10),
    @Cantidad VARCHAR(7)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO Detalle_Compra (Numero_Ingreso, Codigo_Producto, Cantidad)
    VALUES (@NumeroIngreso, @CodigoProducto, CAST(@Cantidad AS INT));
    -- el trigger trg_Inventario_AumentarPorCompra suma esto al inventario solo
END
GO

------------ ALMACEN4: Salidas

CREATE PROCEDURE SP_CantidadDisponible             -- consulta cuanto hay disponible de un producto
    @CodigoProducto VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT ISNULL((SELECT Cantidad FROM Inventario WHERE Codigo = @CodigoProducto), 0) AS Disponible;
    -- se usa ANTES de intentar la salida, para responder "CANTIDAD INSUF" sin
    -- llegar siquiera a insertar (el trigger es el respaldo final por si acaso)
END
GO

CREATE PROCEDURE SP_InsertarSalida                 -- inserta la cabecera de una salida/venta
    @NumeroFactura VARCHAR(10),
    @FechaVenta VARCHAR(10)                          -- llega como texto yyyyMMdd
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO Salidas (Numero_Factura, Fecha_Venta)
    VALUES (@NumeroFactura, CONVERT(DATE, @FechaVenta, 112));
END
GO

CREATE PROCEDURE SP_InsertarDetalleSalida          -- inserta un producto que sale (uno por llamada)
    @NumeroFactura VARCHAR(10),
    @CodigoProducto VARCHAR(10),
    @Cantidad VARCHAR(7)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO Detalle_Salida (Numero_Factura, Codigo_Producto, Cantidad)
    VALUES (@NumeroFactura, @CodigoProducto, CAST(@Cantidad AS INT));
    -- el trigger trg_Inventario_DisminuirPorSalida resta esto del inventario,
    -- y si quedara negativo, deshace todo y avisa "CANTIDAD INSUF"
END
GO