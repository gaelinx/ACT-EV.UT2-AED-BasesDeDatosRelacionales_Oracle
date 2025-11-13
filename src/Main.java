import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static Proveedor proveedor = new Proveedor();
    private static Producto producto = new Producto();
    private static Venta venta = new Venta();

    public static void main(String[] args) {
        // Crear tablas al iniciar
        ConexionOracle.crearTablas();

        boolean continuar = true;

        while (continuar) {
            mostrarMenu();

            try {
                int opcion = Integer.parseInt(sc.nextLine());

                switch (opcion) {
                    case 1 -> menuInsertarProveedor();
                    case 2 -> proveedor.listar();
                    case 3 -> menuActualizarProveedor();
                    case 4 -> menuEliminarProveedor();
                    case 5 -> menuInsertarProducto();
                    case 6 -> producto.listar();
                    case 7 -> menuActualizarProducto();
                    case 8 -> menuEliminarProducto();
                    case 9 -> menuBuscarProducto();
                    case 10 -> menuBuscarPorCategoria();
                    case 11 -> menuStockBajo();
                    case 12 -> producto.contar();
                    case 13 -> menuRegistrarVenta();
                    case 14 -> venta.listar();
                    case 15 -> DatosEjemplo.insertar();
                    case 0 -> {
                        System.out.println("\n¡Hasta pronto!");
                        continuar = false;
                    }
                    default -> System.out.println("Opción no válida");
                }

            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido");
            }
        }

        sc.close();
    }

    public static void mostrarMenu(){
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║     GESTOR DE INVENTARIO - PRODUCTOS TIENDA        ║");
        System.out.println("║       Base de Datos: Oracle ( NO Embebida)         ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║  PROVEEDORES:                                      ║");
        System.out.println("║   1. Insertar proveedor                            ║");
        System.out.println("║   2. Listar proveedores                            ║");
        System.out.println("║   3. Actualizar proveedor                          ║");
        System.out.println("║   4. Eliminar proveedor                            ║");
        System.out.println("║                                                    ║");
        System.out.println("║  PRODUCTOS:                                        ║");
        System.out.println("║   5. Insertar producto                             ║");
        System.out.println("║   6. Listar productos                              ║");
        System.out.println("║   7. Actualizar producto                           ║");
        System.out.println("║   8. Eliminar producto                             ║");
        System.out.println("║                                                    ║");
        System.out.println("║  CONSULTAS:                                        ║");
        System.out.println("║   9. Buscar producto por nombre                    ║");
        System.out.println("║  10. Buscar producto por Categoria                 ║");
        System.out.println("║  11. Productos con stock bajo                      ║");
        System.out.println("║  12. Contar total de productos                     ║");
        System.out.println("║                                                    ║");
        System.out.println("║  VENTAS:                                           ║");
        System.out.println("║  13. Registrar venta                               ║");
        System.out.println("║  14. Listar ventas                                 ║");
        System.out.println("║                                                    ║");
        System.out.println("║  15. Insertar datos de ejemplo                     ║");
        System.out.println("║   0. Salir                                         ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    private static void menuInsertarProveedor() {
        System.out.print("Nombre del proveedor: ");
        String nombre = sc.nextLine();

        System.out.print("Teléfono: ");
        String telefono = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Dirección: ");
        String direccion = sc.nextLine();

        proveedor.insertar(nombre, telefono, email, direccion);
    }

    private static void menuActualizarProveedor() {
        System.out.print("ID del proveedor a actualizar: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Nuevo teléfono: ");
        String telefono = sc.nextLine();

        System.out.print("Nuevo email: ");
        String email = sc.nextLine();

        System.out.print("Nueva dirección: ");
        String direccion = sc.nextLine();

        proveedor.actualizar(id, nombre, telefono, email, direccion);
    }

    private static void menuEliminarProveedor() {
        System.out.print("ID del proveedor a eliminar: ");
        int id = Integer.parseInt(sc.nextLine());

        proveedor.eliminar(id);
    }


    private static void menuInsertarProducto() {
        proveedor.listar();

        System.out.print("Nombre del producto: ");
        String nombre = sc.nextLine();

        System.out.print("Categoría: ");
        String categoria = sc.nextLine();

        System.out.print("ID del proveedor: ");
        int idProveedor = Integer.parseInt(sc.nextLine());

        System.out.print("Precio costo (€): ");
        double precioCosto = Double.parseDouble(sc.nextLine());

        System.out.print("Precio venta (€): ");
        double precioVenta = Double.parseDouble(sc.nextLine());

        System.out.print("Stock inicial: ");
        int stock = Integer.parseInt(sc.nextLine());

        producto.insertar(nombre, categoria, idProveedor, precioCosto, precioVenta, stock);
    }

    private static void menuActualizarProducto() {
        System.out.print("ID del producto a actualizar: ");
        int id = Integer.parseInt(sc.nextLine());

        proveedor.listar();

        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Nueva categoría: ");
        String categoria = sc.nextLine();

        System.out.print("ID del proveedor: ");
        int idProveedor = Integer.parseInt(sc.nextLine());

        System.out.print("Nuevo precio costo (€): ");
        double precioCosto = Double.parseDouble(sc.nextLine());

        System.out.print("Nuevo precio venta (€): ");
        double precioVenta = Double.parseDouble(sc.nextLine());

        System.out.print("Nuevo stock: ");
        int stock = Integer.parseInt(sc.nextLine());

        producto.actualizar(id, nombre, categoria, idProveedor, precioCosto, precioVenta, stock);
    }

    private static void menuEliminarProducto() {
        System.out.print("ID del producto a eliminar: ");
        int id = Integer.parseInt(sc.nextLine());

        producto.eliminar(id);
    }

    private static void menuBuscarProducto() {
        System.out.print("Ingrese término de búsqueda: ");
        String termino = sc.nextLine();

        producto.buscarPorNombre(termino);
    }

    private static void menuBuscarPorCategoria() {
        System.out.print("Ingrese categoría (Lacteos, Bebidas, Panaderia, Alimentacion): ");
        String categoria = sc.nextLine();

        producto.buscarPorCategoria(categoria);
    }

    private static void menuStockBajo() {
        System.out.print("Stock mínimo para alerta: ");
        int stockMinimo = Integer.parseInt(sc.nextLine());

        producto.stockBajo(stockMinimo);
    }

    private static void menuRegistrarVenta() {
        System.out.print("ID del producto vendido: ");
        int idProducto = Integer.parseInt(sc.nextLine());

        System.out.print("Cantidad vendida: ");
        int cantidad = Integer.parseInt(sc.nextLine());

        venta.registrar(idProducto, cantidad);
    }
}
