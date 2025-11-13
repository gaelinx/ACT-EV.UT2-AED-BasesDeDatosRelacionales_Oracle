import java.sql.*;

public class ConexionOracle {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "sys as sysdba";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void crearTablas(){
        String[] sqlSequences = {
                "CREATE SEQUENCE seq_proveedor START WITH 1 INCREMENT BY 1",
                "CREATE SEQUENCE seq_producto START WITH 1 INCREMENT BY 1",
                "CREATE SEQUENCE seq_venta START WITH 1 INCREMENT BY 1"
        };

        String sqlProveedores = """
            CREATE TABLE PROVEEDORES (
                id_proveedor NUMBER PRIMARY KEY,
                nombre VARCHAR2(100) NOT NULL UNIQUE,
                telefono VARCHAR2(20),
                email VARCHAR2(100),
                direccion VARCHAR2(200)
            )
        """;

        String sqlProductos = """
            CREATE TABLE PRODUCTOS (
                id_producto NUMBER PRIMARY KEY,
                nombre VARCHAR2(100) NOT NULL,
                categoria VARCHAR2(50) NOT NULL,
                id_proveedor NUMBER NOT NULL,
                precio_costo NUMBER(10,2) NOT NULL,
                precio_venta NUMBER(10,2) NOT NULL,
                stock_actual NUMBER DEFAULT 0 NOT NULL,
                CONSTRAINT fk_proveedor FOREIGN KEY (id_proveedor) 
                    REFERENCES PROVEEDORES(id_proveedor)
            )
        """;

        String sqlVentas = """
            CREATE TABLE VENTAS (
                id_venta NUMBER PRIMARY KEY,
                id_producto NUMBER NOT NULL,
                fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                cantidad NUMBER NOT NULL,
                precio_costo_momento NUMBER(10,2) NOT NULL,
                precio_venta_momento NUMBER(10,2) NOT NULL,
                ganancia_unitaria NUMBER(10,2) NOT NULL,
                ganancia_total NUMBER(10,2) NOT NULL,
                CONSTRAINT fk_producto FOREIGN KEY (id_producto) 
                    REFERENCES PRODUCTOS(id_producto)
            )
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Crear secuencias (ignorar error si ya existen)
            for (String sqlSeq : sqlSequences) {
                try {
                    stmt.execute(sqlSeq);
                } catch (SQLException e) {
                    // Ignorar error si la secuencia ya existe
                    if (!e.getMessage().contains("ORA-00955")) {
                        throw e;
                    }
                }
            }

            // Crear tablas (ignorar error si ya existen)
            try {
                stmt.execute(sqlProveedores);
            } catch (SQLException e) {
                if (!e.getMessage().contains("ORA-00955")) {
                    throw e;
                }
            }

            try {
                stmt.execute(sqlProductos);
            } catch (SQLException e) {
                if (!e.getMessage().contains("ORA-00955")) {
                    throw e;
                }
            }

            try {
                stmt.execute(sqlVentas);
            } catch (SQLException e) {
                if (!e.getMessage().contains("ORA-00955")) {
                    throw e;
                }
            }

            System.out.println("Tablas y secuencias creadas/verificadas correctamente");

        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }

    public static void eliminarTablas() {
        String[] sqlDrop = {
                "DROP TABLE VENTAS CASCADE CONSTRAINTS",
                "DROP TABLE PRODUCTOS CASCADE CONSTRAINTS",
                "DROP TABLE PROVEEDORES CASCADE CONSTRAINTS",
                "DROP SEQUENCE seq_venta",
                "DROP SEQUENCE seq_producto",
                "DROP SEQUENCE seq_proveedor"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : sqlDrop) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    // Ignorar errores si no existen
                }
            }

            System.out.println("Tablas y secuencias eliminadas correctamente");

        } catch (SQLException e) {
            System.err.println("Error al eliminar tablas: " + e.getMessage());
        }
    }
}
