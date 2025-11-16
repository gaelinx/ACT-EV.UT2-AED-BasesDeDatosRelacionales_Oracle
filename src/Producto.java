import java.sql.*;

public class Producto {
    public void insertar(String nombre, String categoria, int idProveedor,
                         double precioCosto, double precioVenta, int stock) {
        Proveedor proveedor = new Proveedor();
        if (!proveedor.existe(idProveedor)) {
            System.out.println("No existe proveedor con ID: " + idProveedor);
            return;
        }

        String sql = """
            INSERT INTO PRODUCTOS (id_producto, nombre, categoria, id_proveedor, 
                                   precio_costo, precio_venta, stock_actual)
            VALUES (seq_producto.NEXTVAL, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, categoria);
            pstmt.setInt(3, idProveedor);
            pstmt.setDouble(4, precioCosto);
            pstmt.setDouble(5, precioVenta);
            pstmt.setInt(6, stock);

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Producto insertado correctamente. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
        }
    }

    public void listar() {
        String sql = """
            SELECT p.id_producto, p.nombre, p.categoria, pr.nombre AS proveedor,
                   p.precio_costo, p.precio_venta, p.stock_actual
            FROM PRODUCTOS p
            INNER JOIN PROVEEDORES pr ON p.id_proveedor = pr.id_proveedor
            ORDER BY p.id_producto
        """;

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n" + "=".repeat(120));
            System.out.printf("%-5s %-30s %-15s %-25s %-12s %-12s %-10s%n",
                    "ID", "NOMBRE", "CATEGORÍA", "PROVEEDOR", "P.COSTO", "P.VENTA", "STOCK");
            System.out.println("=".repeat(120));

            boolean hayRegistros = false;
            while (rs.next()) {
                hayRegistros = true;
                System.out.printf("%-5d %-30s %-15s %-25s €%-11.2f €%-11.2f %-10d%n",
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getString("proveedor"),
                        rs.getDouble("precio_costo"),
                        rs.getDouble("precio_venta"),
                        rs.getInt("stock_actual")
                );
            }

            if (!hayRegistros) {
                System.out.println("No hay productos registrados");
            }
            System.out.println("=".repeat(120) + "\n");

        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
    }

    public void actualizar(int id, String nombre, String categoria, int idProveedor,
                           double precioCosto, double precioVenta, int stock) {
        if (!existe(id)) {
            System.out.println("No existe producto con ID: " + id);
            return;
        }

        Proveedor proveedor = new Proveedor();
        if (!proveedor.existe(idProveedor)) {
            System.out.println("No existe proveedor con ID: " + idProveedor);
            return;
        }

        String sql = """
            UPDATE PRODUCTOS 
            SET nombre = ?, categoria = ?, id_proveedor = ?, 
                precio_costo = ?, precio_venta = ?, stock_actual = ?
            WHERE id_producto = ?
        """;

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, categoria);
            pstmt.setInt(3, idProveedor);
            pstmt.setDouble(4, precioCosto);
            pstmt.setDouble(5, precioVenta);
            pstmt.setInt(6, stock);
            pstmt.setInt(7, id);

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Producto actualizado correctamente. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
    }

    public void eliminar(int id) {
        if (!existe(id)) {
            System.out.println("✗ No existe producto con ID: " + id);
            return;
        }

        String sql = "DELETE FROM PRODUCTOS WHERE id_producto = ?";

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Producto eliminado correctamente. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
        }
    }

    public void buscarPorNombre(String termino) {
        String sql = """
            SELECT p.id_producto, p.nombre, p.categoria, pr.nombre AS proveedor,
                   p.precio_costo, p.precio_venta, p.stock_actual
            FROM PRODUCTOS p
            INNER JOIN PROVEEDORES pr ON p.id_proveedor = pr.id_proveedor
            WHERE UPPER(p.nombre) LIKE UPPER(?)
            ORDER BY p.nombre
        """;

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + termino + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n" + "=".repeat(120));
                System.out.printf("%-5s %-30s %-15s %-25s %-12s %-12s %-10s%n",
                        "ID", "NOMBRE", "CATEGORÍA", "PROVEEDOR", "P.COSTO", "P.VENTA", "STOCK");
                System.out.println("=".repeat(120));

                boolean hayRegistros = false;
                while (rs.next()) {
                    hayRegistros = true;
                    System.out.printf("%-5d %-30s %-15s %-25s €%-11.2f €%-11.2f %-10d%n",
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getString("proveedor"),
                            rs.getDouble("precio_costo"),
                            rs.getDouble("precio_venta"),
                            rs.getInt("stock_actual")
                    );
                }

                if (!hayRegistros) {
                    System.out.println("No se encontraron productos con ese término");
                }
                System.out.println("=".repeat(120) + "\n");
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }
    }

    public void buscarPorCategoria(String categoria) {
        String sql = """
            SELECT p.id_producto, p.nombre, p.categoria, pr.nombre AS proveedor,
                   p.precio_costo, p.precio_venta, p.stock_actual
            FROM PRODUCTOS p
            INNER JOIN PROVEEDORES pr ON p.id_proveedor = pr.id_proveedor
            WHERE UPPER(p.categoria) LIKE UPPER(?)
            ORDER BY p.nombre
        """;

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + categoria + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n" + "=".repeat(120));
                System.out.printf("%-5s %-30s %-15s %-25s %-12s %-12s %-10s%n",
                        "ID", "NOMBRE", "CATEGORÍA", "PROVEEDOR", "P.COSTO", "P.VENTA", "STOCK");
                System.out.println("=".repeat(120));

                boolean hayRegistros = false;
                while (rs.next()) {
                    hayRegistros = true;
                    System.out.printf("%-5d %-30s %-15s %-25s €%-11.2f €%-11.2f %-10d%n",
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getString("proveedor"),
                            rs.getDouble("precio_costo"),
                            rs.getDouble("precio_venta"),
                            rs.getInt("stock_actual")
                    );
                }

                if (!hayRegistros) {
                    System.out.println("No se encontraron productos en esa categoría");
                }
                System.out.println("=".repeat(120) + "\n");
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar productos por categoría: " + e.getMessage());
        }
    }

    public void stockBajo(int stockMinimo) {
        String sql = """
            SELECT p.id_producto, p.nombre, p.categoria, pr.nombre AS proveedor,
                   p.precio_costo, p.precio_venta, p.stock_actual
            FROM PRODUCTOS p
            INNER JOIN PROVEEDORES pr ON p.id_proveedor = pr.id_proveedor
            WHERE p.stock_actual < ?
            ORDER BY p.stock_actual
        """;
        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, stockMinimo);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\nPRODUCTOS CON STOCK BAJO");
                System.out.println("=".repeat(120));
                System.out.printf("%-5s %-30s %-15s %-25s %-12s %-12s %-10s%n",
                        "ID", "NOMBRE", "CATEGORÍA", "PROVEEDOR", "P.COSTO", "P.VENTA", "STOCK");
                System.out.println("=".repeat(120));

                boolean hayRegistros = false;
                while (rs.next()) {
                    hayRegistros = true;
                    System.out.printf("%-5d %-30s %-15s %-25s €%-11.2f €%-11.2f %-10d%n",
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getString("proveedor"),
                            rs.getDouble("precio_costo"),
                            rs.getDouble("precio_venta"),
                            rs.getInt("stock_actual")
                    );
                }

                if (!hayRegistros) {
                    System.out.println("No hay productos con stock bajo");
                }
                System.out.println("=".repeat(120) + "\n");
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar stock bajo: " + e.getMessage());
        }
    }

    public void contar() {
        String sql = "SELECT COUNT(*) as total FROM PRODUCTOS";

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("\nTotal de productos registrados: " + total + "\n");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar productos: " + e.getMessage());
        }
    }

    public boolean existe(int id) {
        String sql = "SELECT 1 FROM PRODUCTOS WHERE id_producto = ?";

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar producto: " + e.getMessage());
            return false;
        }
    }
}
