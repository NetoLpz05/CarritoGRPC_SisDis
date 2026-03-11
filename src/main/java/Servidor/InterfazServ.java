package Servidor;

import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import io.grpc.stub.StreamObserver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class InterfazServ extends JFrame {
    private DefaultTableModel model;

    public InterfazServ() {
        setTitle("Monitor de Inventario gRPC");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel(new Object[]{"ID Producto", "Stock Disponible"}, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table));
        setVisible(true);
    }

    public void procesarCarrito(){

    }
}
