USE NOVA_ONLINE;
GO

------------ 2. TRIGGERS


-- ALMACEN1 3.b: al insertar un producto nuevo, se crea su fila en
-- Inventario en 0, sin que Java tenga que hacerlo aparte.

CREATE TRIGGER trg_Inventario_AlInsertarProducto
ON Productos
AFTER INSERT                                   -- se dispara solo, justo despues de cada INSERT
AS
BEGIN
    SET NOCOUNT ON;                              -- evita mensajes extra que confunden al driver JDBC
    INSERT INTO Inventario (Codigo, Cantidad)     -- crea la fila de inventario del producto nuevo
    SELECT Codigo, 0 FROM inserted;                -- "inserted" trae las filas recien insertadas
END
GO

-- ALMACEN3 3.e: al registrar una compra, sumar la cantidad comprada
-- al inventario existente del producto.

CREATE TRIGGER trg_Inventario_AumentarPorCompra
ON Detalle_Compra
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE Inventario                              -- suma lo comprado al inventario
    SET Cantidad = Inventario.Cantidad + i.Cantidad
    FROM Inventario
    INNER JOIN inserted i ON Inventario.Codigo = i.Codigo_Producto; -- corregido: la columna es Codigo_Producto (con guion bajo)
END
GO

-- ALMACEN4 3.e: al registrar una salida, restar la cantidad del
-- inventario. El PDF exige que el inventario NUNCA quede negativo:
-- si eso pasara, se debe "suspender el proceso" -> aqui se revierte
-- toda la transaccion con ROLLBACK + THROW.
-- corregido: la tabla se llama Detalle_Salida (con guion bajo), no DetalleSalida
CREATE TRIGGER trg_Inventario_DisminuirPorSalida
ON Detalle_Salida
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE Inventario                              -- resta lo que salio del inventario
    SET Cantidad = Inventario.Cantidad - i.Cantidad
    FROM Inventario
    INNER JOIN inserted i ON Inventario.Codigo = i.Codigo_Producto; -- corregido: la columna es Codigo_Producto (con guion bajo)

    IF EXISTS (SELECT 1 FROM Inventario WHERE Cantidad < 0)  -- revisa si algun producto quedo en negativo
    BEGIN
        ROLLBACK TRANSACTION;                        -- deshace TODO (la salida, el detalle y esta resta)
        THROW 51000, 'CANTIDAD INSUF', 1;             -- avisa al procedimiento/Java que no habia suficiente
    END
END
GO