package Cliente;

import com.tienda.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InterfazCliente extends JFrame {

    private JTable tablaProductos;
    private DefaultTableModel modelProductos;
    private JTable tablaCarrito;
    private DefaultTableModel modelCarrito;
    private CarritoServiceGrpc.CarritoServiceBlockingStub stub;

    public InterfazCliente(CarritoServiceGrpc.CarritoServiceBlockingStub stub) {
        //Conexión GRPC
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        this.stub = stub;

        setTitle("Cliente - Tienda GRPC");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Tabla de los productos
        modelProductos = new DefaultTableModel(new Object[]{"ID","Nombre","Precio","Stock"},0);
        tablaProductos = new JTable(modelProductos);

        //Tabla del carrito
        modelCarrito = new DefaultTableModel(new Object[]{"ID","Nombre","Precio","Cantidad"},0);
        tablaCarrito = new JTable(modelCarrito);
        JPanel panelCentro = new JPanel(new GridLayout(1,2));
        panelCentro.add(new JScrollPane(tablaProductos));
        panelCentro.add(new JScrollPane(tablaCarrito));
        add(panelCentro, BorderLayout.CENTER);

        //Botones y Eventos
        JButton btnCargar = new JButton("Cargar productos");
        JButton btnAgregar = new JButton("Agregar al carrito");
        JButton btnComprar = new JButton("Procesar compra");
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnCargar);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnComprar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> cargarProductos());
        btnAgregar.addActionListener(e -> agregarCarrito());
        btnComprar.addActionListener(e -> procesarCompra());
        setVisible(true);
    }

    private void cargarProductos() {
        modelProductos.setRowCount(0);
        ListaProductos lista = stub.listarProductos(Empty.newBuilder().build());
        for(Producto p : lista.getProductosList()) {
            modelProductos.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getCantidad()
            });
        }
    }

    private void agregarCarrito() {
        int fila = tablaProductos.getSelectedRow();
        if(fila == -1) return;

        String id = modelProductos.getValueAt(fila,0).toString();
        String nombre = modelProductos.getValueAt(fila,1).toString();
        double precio = (double) modelProductos.getValueAt(fila,2);
        String cantStr = JOptionPane.showInputDialog("Cantidad:");
        int cantidad = Integer.parseInt(cantStr);

        modelCarrito.addRow(new Object[]{
                id,nombre,precio,cantidad
        });
    }

    private void procesarCompra() {
        CarritoRequest.Builder carrito = CarritoRequest.newBuilder();
        carrito.setUsuarioId("cliente1");

        for(int i=0;i<modelCarrito.getRowCount();i++){
            Producto p = Producto.newBuilder()
                    .setId(modelCarrito.getValueAt(i,0).toString())
                    .setNombre(modelCarrito.getValueAt(i,1).toString())
                    .setPrecio((double)modelCarrito.getValueAt(i,2))
                    .setCantidad((int)modelCarrito.getValueAt(i,3))
                    .build();
            carrito.addItems(p);
        }

        CarritoResponse respuesta = stub.procesarCarrito(carrito.build());
        JOptionPane.showMessageDialog(this,
                "Total: " + respuesta.getTotalPagar() +
                        "\nEstado: " + respuesta.getEstado());
    }
}