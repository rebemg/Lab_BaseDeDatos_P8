import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PracticaJDBC {

    public static void main(String[] args) throws SQLException {
        try (Connection conn = ConexionBD.conectar()) {
            System.out.println("Conexión exitosa.");

            String insertarEmpleado =
                    "INSERT INTO empleado(nombre, puesto, salario, fecha_contratacion) "
                            + "SELECT ?, ?, ?, CURRENT_DATE "
                            + "WHERE NOT EXISTS (SELECT 1 FROM empleado "
                            + "WHERE nombre = ? AND puesto = ? AND salario = ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertarEmpleado)) {
                ps.setString(1, "Carlos Mendoza");
                ps.setString(2, "Desarrollador");
                ps.setBigDecimal(3, new BigDecimal("45000"));
                ps.setString(4, "Carlos Mendoza");
                ps.setString(5, "Desarrollador");
                ps.setBigDecimal(6, new BigDecimal("45000"));
                if (ps.executeUpdate() == 0) {
                    System.out.println("El empleado ya estaba registrado; no se duplicó.");
                } else {
                    System.out.println("Empleado insertado.");
                }
            }

            System.out.println("\nLISTA DE EMPLEADOS");

            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM empleado")) {
                while (rs.next()) {
                    System.out.println(
                            rs.getInt("id") + " | "
                                    + rs.getString("nombre") + " | "
                                    + rs.getString("puesto") + " | $"
                                    + rs.getBigDecimal("salario"));
                }
            }

            try (Statement st = conn.createStatement()) {
                String crearTabla =
                        "CREATE TABLE IF NOT EXISTS mensajes ("
                                + "id SERIAL PRIMARY KEY, "
                                + "contenido JSONB"
                                + ")";
                st.execute(crearTabla);
                System.out.println("\nTabla mensajes creada.");

                String insertarJson =
                        "INSERT INTO mensajes(contenido) "
                                + "SELECT ?::jsonb "
                                + "WHERE NOT EXISTS (SELECT 1 FROM mensajes "
                                + "WHERE contenido = ?::jsonb)";
                try (PreparedStatement psJson = conn.prepareStatement(insertarJson)) {
                    String mensaje = "{\"usuario\":\"Ana\",\"mensaje\":\"Hola\"}";
                    psJson.setString(1, mensaje);
                    psJson.setString(2, mensaje);
                    if (psJson.executeUpdate() == 0) {
                        System.out.println("El mensaje JSON ya existía; no se duplicó.");
                    } else {
                        System.out.println("Mensaje JSON insertado.");
                    }
                }

                String consultaJson =
                        "SELECT contenido->>'usuario' AS usuario FROM mensajes";
                try (ResultSet rsJson = st.executeQuery(consultaJson)) {
                    System.out.println("\nUSUARIOS EN JSON");
                    while (rsJson.next()) {
                        System.out.println(rsJson.getString("usuario"));
                    }
                }
            }

            System.out.println("\nREGISTRANDO CLIENTE");

            int clienteId;
            String buscarCliente =
                    "SELECT id FROM cliente WHERE nombre = ? AND direccion = ? "
                            + "ORDER BY id LIMIT 1";
            try (PreparedStatement psBuscarCliente = conn.prepareStatement(buscarCliente)) {
                psBuscarCliente.setString(1, "Pedro Hernández");
                psBuscarCliente.setString(2, "Av. Principal 100");
                try (ResultSet rsCliente = psBuscarCliente.executeQuery()) {
                    if (rsCliente.next()) {
                        clienteId = rsCliente.getInt("id");
                        System.out.println("El cliente ya estaba registrado; se reutilizará.");
                    } else {
                        String sqlCliente =
                                "INSERT INTO cliente(nombre, direccion) VALUES (?, ?) RETURNING id";
                        try (PreparedStatement psCliente = conn.prepareStatement(sqlCliente)) {
                            psCliente.setString(1, "Pedro Hernández");
                            psCliente.setString(2, "Av. Principal 100");
                            try (ResultSet rsNuevoCliente = psCliente.executeQuery()) {
                                if (!rsNuevoCliente.next()) {
                                    throw new SQLException(
                                            "No se obtuvo el ID del cliente registrado.");
                                }
                                clienteId = rsNuevoCliente.getInt("id");
                                System.out.println("Cliente registrado.");
                            }
                        }
                    }
                }
            }

            System.out.println("\nGUARDANDO PREFERENCIAS");

            String sqlPreferencias =
                    "UPDATE cliente SET preferencias = ?::jsonb WHERE id = ?";
            try (PreparedStatement psPrefs = conn.prepareStatement(sqlPreferencias)) {
                psPrefs.setString(
                        1,
                        "{\"categoria_favorita\":\"Tecnologia\",\"metodo_pago\":\"Tarjeta\"}");
                psPrefs.setInt(2, clienteId);
                psPrefs.executeUpdate();
            }
            System.out.println("Preferencias guardadas.");

            System.out.println("\nPREFERENCIAS DE CLIENTES");

            String consultaPreferencias =
                    "SELECT nombre, preferencias->>'categoria_favorita' AS categoria "
                            + "FROM cliente WHERE preferencias IS NOT NULL";
            try (Statement stJson = conn.createStatement();
                    ResultSet rsJson = stJson.executeQuery(consultaPreferencias)) {
                while (rsJson.next()) {
                    System.out.println(
                            rsJson.getString("nombre") + " -> "
                                    + rsJson.getString("categoria"));
                }
            }

            System.out.println("\nREGISTRANDO PEDIDO");

            String sqlPedido =
                    "INSERT INTO pedido(cliente_id, fecha) "
                            + "SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM pedido "
                            + "WHERE cliente_id = ? AND fecha = ?)";
            try (PreparedStatement psPedido = conn.prepareStatement(sqlPedido)) {
                psPedido.setInt(1, clienteId);
                java.sql.Date fechaPedido = java.sql.Date.valueOf("2026-10-15");
                psPedido.setDate(2, fechaPedido);
                psPedido.setInt(3, clienteId);
                psPedido.setDate(4, fechaPedido);
                if (psPedido.executeUpdate() == 0) {
                    System.out.println("El pedido ya existía; no se duplicó.");
                } else {
                    System.out.println("Pedido registrado.");
                }
            }

            System.out.println("\nESTADISTICAS DE COMPRAS");

            String estadisticas =
                    "SELECT c.nombre, SUM(dp.cantidad * pr.precio) AS total_compras "
                            + "FROM cliente c "
                            + "JOIN pedido p ON c.id = p.cliente_id "
                            + "JOIN detalle_pedido dp ON p.id = dp.pedido_id "
                            + "JOIN producto pr ON dp.producto_id = pr.id "
                            + "GROUP BY c.id, c.nombre "
                            + "ORDER BY total_compras DESC";
            try (Statement stEst = conn.createStatement();
                    ResultSet rsEst = stEst.executeQuery(estadisticas)) {
                while (rsEst.next()) {
                    System.out.println(
                            rsEst.getString("nombre") + " -> $"
                                    + rsEst.getBigDecimal("total_compras"));
                }
            }
        }
    }
}
