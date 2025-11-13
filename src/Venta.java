import java.sql.*;

public class Venta {
    public void registrar(int idProducto, int cantidad) {

        Producto producto = new Producto();
        if (!producto.existe(idProducto)) {
            System.out.println("✗ No existe producto con ID: " + idProducto);
            return;
        }

        String sqlSelect = "SELECT precio_costo, precio_venta, stock_actual FROM PRODUCTOS WHERE id_producto = ?";
        String sqlInsertVenta = """
            INSERT INTO VENTAS (id_venta, id_producto, cantidad, precio_costo_momento, 
                               precio_venta_momento, ganancia_unitaria, ganancia_total)
            VALUES (seq_venta.NEXTVAL, ?, ?, ?, ?, ?, ?)
        """;
        String sqlUpdateStock = "UPDATE PRODUCTOS SET stock_actual = stock_actual - ? WHERE id_producto = ?";

        Connection conn = null;
        try {
            conn = ConexionOracle.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Obtener datos del producto
            double precioCosto, precioVenta;
            int stockActual;

            try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
                pstmt.setInt(1, idProducto);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        precioCosto = rs.getDouble("precio_costo");
                        precioVenta = rs.getDouble("precio_venta");
                        stockActual = rs.getInt("stock_actual");

                        if (stockActual < cantidad) {
                            System.out.println("Stock insuficiente. Stock actual: " + stockActual);
                            conn.rollback();
                            return;
                        }
                    } else {
                        conn.rollback();
                        return;
                    }
                }
            }

            // Calcular ganancias
            double gananciaUnitaria = precioVenta - precioCosto;
            double gananciaTotal = gananciaUnitaria * cantidad;

            // Insertar venta
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsertVenta)) {
                pstmt.setInt(1, idProducto);
                pstmt.setInt(2, cantidad);
                pstmt.setDouble(3, precioCosto);
                pstmt.setDouble(4, precioVenta);
                pstmt.setDouble(5, gananciaUnitaria);
                pstmt.setDouble(6, gananciaTotal);
                pstmt.executeUpdate();
            }

            // Actualizar stock
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateStock)) {
                pstmt.setInt(1, cantidad);
                pstmt.setInt(2, idProducto);
                pstmt.executeUpdate();
            }

            conn.commit(); // Confirmar transacción
            System.out.println("Venta registrada correctamente");
            System.out.printf("  Ganancia: €%.2f%n", gananciaTotal);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al registrar venta: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Lista las últimas 20 ventas
     */
    public void listar() {
        // En Oracle usamos ROWNUM o FETCH FIRST (12c+)
        String sql = """
            SELECT v.id_venta, p.nombre AS producto, v.fecha_hora, v.cantidad,
                   v.precio_venta_momento, v.ganancia_total
            FROM VENTAS v
            INNER JOIN PRODUCTOS p ON v.id_producto = p.id_producto
            ORDER BY v.fecha_hora DESC
            FETCH FIRST 20 ROWS ONLY
        """;

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n" + "=".repeat(120));
            System.out.printf("%-5s %-40s %-20s %-10s %-15s %-15s%n",
                    "ID", "PRODUCTO", "FECHA", "CANT.", "PRECIO", "GANANCIA");
            System.out.println("=".repeat(120));

            boolean hayRegistros = false;
            while (rs.next()) {
                hayRegistros = true;
                System.out.printf("%-5d %-40s %-20s %-10d €%-14.2f €%-14.2f%n",
                        rs.getInt("id_venta"),
                        rs.getString("producto"),
                        rs.getTimestamp("fecha_hora").toString(),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_venta_momento"),
                        rs.getDouble("ganancia_total")
                );
            }

            if (!hayRegistros) {
                System.out.println("No hay ventas registradas");
            }
            System.out.println("=".repeat(120) + "\n");

        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }
    }
}
