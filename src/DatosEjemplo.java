import java.sql.*;

public class DatosEjemplo {
    public static void insertar() {
        // Primero insertar proveedores
        String sqlProveedores = """
            INSERT INTO PROVEEDORES (id_proveedor, nombre, telefono, email, direccion)
            VALUES (seq_proveedor.NEXTVAL, ?, ?, ?, ?)
        """;

        Object[][] datosProveedores = {
                {"GR Leche Pascual", "902 500 500", "comercial@lechepascual.es", "Av. Cantabria, Aranda de Duero"},
                {"Coca-Cola European Partners", "900 900 900", "info@cocacolaep.com", "Polígono Industrial, Madrid"},
                {"Bimbo", "916 635 000", "atencion@bimbo.es", "Ctra. Getafe-Villaverde, Madrid"},
                {"Deoleo", "954 647 200", "info@deoleo.com", "Sevilla"},
                {"Danone", "902 202 025", "info@danone.es", "Barcelona"},
                {"SOS Cuétara", "925 280 000", "info@soscuetara.es", "Toledo"},
                {"Mondelez", "900 807 515", "contacto@mondelez.com", "Valls, Tarragona"}
        };

        try (Connection conn = ConexionOracle.getConnection()) {

            // Insertar proveedores
            try (PreparedStatement pstmt = conn.prepareStatement(sqlProveedores)) {
                for (Object[] fila : datosProveedores) {
                    pstmt.setString(1, (String) fila[0]);
                    pstmt.setString(2, (String) fila[1]);
                    pstmt.setString(3, (String) fila[2]);
                    pstmt.setString(4, (String) fila[3]);
                    pstmt.executeUpdate();
                }
            }

            // Luego insertar productos
            String sqlProductos = """
                INSERT INTO PRODUCTOS (id_producto, nombre, categoria, id_proveedor, 
                                      precio_costo, precio_venta, stock_actual)
                VALUES (seq_producto.NEXTVAL, ?, ?, ?, ?, ?, ?)
            """;

            Object[][] datosProductos = {
                    {"Leche Pascual Entera 1L", "Lacteos", 1, 0.85, 1.39, 200},
                    {"Coca-Cola 2L", "Bebidas", 2, 1.20, 2.15, 150},
                    {"Pan Bimbo Blanco 450g", "Panaderia", 3, 0.95, 1.65, 80},
                    {"Aceite Carbonell 1L", "Alimentacion", 4, 3.50, 5.99, 60},
                    {"Agua Font Vella 1.5L", "Bebidas", 5, 0.18, 0.45, 300},
                    {"Arroz SOS 1kg", "Alimentacion", 6, 1.10, 1.89, 120},
                    {"Yogur Danone Natural x4", "Lacteos", 5, 1.35, 2.25, 90},
                    {"Galletas María Fontaneda", "Panaderia", 7, 0.85, 1.45, 100}
            };

            try (PreparedStatement pstmt = conn.prepareStatement(sqlProductos)) {
                for (Object[] fila : datosProductos) {
                    pstmt.setString(1, (String) fila[0]);
                    pstmt.setString(2, (String) fila[1]);
                    pstmt.setInt(3, (Integer) fila[2]);
                    pstmt.setDouble(4, (Double) fila[3]);
                    pstmt.setDouble(5, (Double) fila[4]);
                    pstmt.setInt(6, (Integer) fila[5]);
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Datos de ejemplo insertados correctamente");
            System.out.println("  - 7 proveedores");
            System.out.println("  - 8 productos");

        } catch (SQLException e) {
            System.err.println("Error al insertar datos de ejemplo: " + e.getMessage());
        }
    }
}
