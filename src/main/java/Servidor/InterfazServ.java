package Servidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Map;


public class InterfazServ extends JFrame {
    private DefaultTableModel model;

    public InterfazServ() {
        setTitle("Monitor de Inventario GRPC");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel(new Object[]{"ID Producto", "Stock Disponible"}, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table));
        setVisible(true);
    }

    public void cargarInventario(Map<String, Integer> inventario) {
        model.setRowCount(0);
        for (Map.Entry<String, Integer> entry : inventario.entrySet()) {
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    /**
     * Actualiza el stock de un producto específico
     */
    public void actualizarStock(String idProducto, int nuevoStock) {
        for (int i = 0; i < model.getRowCount(); i++) {
            String id = (String) model.getValueAt(i, 0);
            if (id.equals(idProducto)) {
                model.setValueAt(nuevoStock, i, 1);
                return;
            }
        }
        // Si no existe en la tabla lo agrega
        model.addRow(new Object[]{idProducto, nuevoStock});
    }
}