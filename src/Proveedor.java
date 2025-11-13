import java.sql.*;

public class Proveedor {
    public void insertar(String nombre, String telefono, String email, String direccion) {
        String sql = """
            INSERT INTO PROVEEDORES (id_proveedor, nombre, telefono, email, direccion)
            VALUES (seq_proveedor.NEXTVAL, ?, ?, ?, ?)
        """;

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, email);
            pstmt.setString(4, direccion);

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("✓ Proveedor insertado correctamente. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
        }
    }

    public void listar() {
        String sql = "SELECT * FROM PROVEEDORES ORDER BY id_proveedor";

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n" + "=".repeat(120));
            System.out.printf("%-5s %-30s %-20s %-30s %-35s%n",
                    "ID", "NOMBRE", "TELÉFONO", "EMAIL", "DIRECCIÓN");
            System.out.println("=".repeat(120));

            boolean hayRegistros = false;
            while (rs.next()) {
                hayRegistros = true;
                System.out.printf("%-5d %-30s %-20s %-30s %-35s%n",
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion")
                );
            }

            if (!hayRegistros) {
                System.out.println("No hay proveedores registrados");
            }
            System.out.println("=".repeat(120) + "\n");

        } catch (SQLException e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
        }
    }

    public void actualizar(int id, String nombre, String telefono, String email, String direccion) {
        if (!existe(id)) {
            System.out.println("No existe proveedor con ID: " + id);
            return;
        }

        String sql = """
            UPDATE PROVEEDORES 
            SET nombre = ?, telefono = ?, email = ?, direccion = ?
            WHERE id_proveedor = ?
        """;

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, email);
            pstmt.setString(4, direccion);
            pstmt.setInt(5, id);

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Proveedor actualizado correctamente. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
        }
    }

    public void eliminar(int id) {
        if (!existe(id)) {
            System.out.println("No existe proveedor con ID: " + id);
            return;
        }

        if (tieneProductos(id)) {
            System.out.println("No se puede eliminar: el proveedor tiene productos asociados");
            return;
        }

        String sql = "DELETE FROM PROVEEDORES WHERE id_proveedor = ?";

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Proveedor eliminado correctamente. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
        }
    }

    public boolean existe(int id) {
        String sql = "SELECT 1 FROM PROVEEDORES WHERE id_proveedor = ?";

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar proveedor: " + e.getMessage());
            return false;
        }
    }

    private boolean tieneProductos(int idProveedor) {
        String sql = "SELECT COUNT(*) as total FROM PRODUCTOS WHERE id_proveedor = ?";

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProveedor);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar productos: " + e.getMessage());
        }
        return false;
    }
}
