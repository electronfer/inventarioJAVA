package controlador;

import modelo.Producto;
import modelo.RepositorioProducto;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vista.Vista;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/*

// Código para crear la base de datos en MySQL
create database testdb;
use testdb;

CREATE TABLE Productos (
    codigo int AUTO_INCREMENT NOT NULL PRIMARY KEY,
    nombre varchar(255) NOT NULL,
    precio double NOT NULL,
    inventario int NOT NULL
);

insert into Productos (codigo, nombre, precio, inventario) values (1, "Manzanas", 5000.0, 25);
insert into Productos (codigo, nombre, precio, inventario) values (2, "Limones", 2300.0, 15);
insert into Productos (codigo, nombre, precio, inventario) values (3, "Peras", 2700.0, 33);
insert into Productos (codigo, nombre, precio, inventario) values (4, "Arandanos", 9300.0, 5);
insert into Productos (codigo, nombre, precio, inventario) values (5, "Tomates", 2100.0, 42);
insert into Productos (codigo, nombre, precio, inventario) values (6, "Fresas", 4100.0, 3);
insert into Productos (codigo, nombre, precio, inventario) values (7, "Helado", 4500.0, 41);
insert into Productos (codigo, nombre, precio, inventario) values (8, "Galletas", 500.0, 8);
insert into Productos (codigo, nombre, precio, inventario) values (9, "Chocolates", 3500.0, 80);
insert into Productos (codigo, nombre, precio, inventario) values (10, "Jamon", 15000.0, 10);

SELECT * FROM Productos;
SELECT sum(precio*inventario) as valor_inventario, sum(precio)/count(inventario) as promedio_precios FROM Productos;
 */
public class ControladorProducto implements ActionListener {

    RepositorioProducto repositorio;
    Vista vista;

    public ControladorProducto(RepositorioProducto repositorio, Vista vista) {
        this.repositorio = repositorio;
        this.vista = vista;
        agregarEventos();
        listarProductos();
    }

    private void agregarEventos() {
        vista.getBtnActualizaProducto().addActionListener(this);
        vista.getBtnAgregarProducto().addActionListener(this);
        vista.getBtnBorrarProducto().addActionListener(this);
        vista.getBtnInformes().addActionListener(this);
    }

    // Se listan los productos disponibles en la base de datos
    private List<Producto> listarProductos() {

        List<Producto> listaProductos = (List<Producto>) repositorio.findAll();
        JTable tabla = vista.getTblProductos();
        DefaultTableModel tableModel = (DefaultTableModel) tabla.getModel();

        // Si hay más productos que filas de la tabla en la vista, se agregan para la visualización
        while (listaProductos.size() > tableModel.getRowCount()) {
            tableModel.addRow(new Object[]{"", 0, 0});
        }

        // Si hay más filas de la tabla que productos, se eliminan para la visualización
        while (listaProductos.size() < tableModel.getRowCount()) {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        }

        for (int row = 0; row < listaProductos.size(); row++) {
            tabla.setValueAt(listaProductos.get(row).getNombre(), row, 0);
            tabla.setValueAt(listaProductos.get(row).getPrecio(), row, 1);
            tabla.setValueAt(listaProductos.get(row).getInventario(), row, 2);
        }

        return listaProductos;
    }

    // Se verifica si el producto se encuentra en la base de datos, en caso tal que si se encuentre
    // se retorna el codigo (Id) del producto, sino se encuentra se retorna -1
    public Long verificarExistencia(String nombre) {
        List<Producto> listaProductos = listarProductos();

        for (Producto producto : listaProductos) {
            if (producto.getNombre().equals(nombre)) {
                return producto.getCodigo();
            }
        }
        return -1L;
    }

    // Se borra el producto si se encuentra en la tabla de productos seleccionada en la interfaz
    public boolean borrarProducto() {

        JTable tabla = vista.getTblProductos();
        DefaultTableModel tableModel = (DefaultTableModel) tabla.getModel();
        String nombreProducto = (String) tableModel.getValueAt(tabla.getSelectedRow(), 0);

        // Se verifica primero si existe el producto para proceder a borrarlo
        Long codigoProducto = verificarExistencia(nombreProducto);
        if (codigoProducto != -1L) {
            repositorio.deleteById(codigoProducto);
            return true;
        } else {
            return false;
        }
    }

    // Se debe imprimir el nombre del producto con el precio mayor, nombre del producto 
    // con el precio menor y el promedio de los precios de los productos y el valor del inventario  
    public void generarInforme() {

        List<Producto> listaProductos = listarProductos();

        String informe;
        double valorInventario = 0.0;
        double promedio = 0.0;
        double contProductos = 0;

        Producto pMayor = listaProductos.get(0);
        Producto pMenor = listaProductos.get(0);

        for (int indice = 0; indice < listaProductos.size(); indice++) {

            valorInventario += listaProductos.get(indice).getPrecio() * listaProductos.get(indice).getInventario();
            promedio += listaProductos.get(indice).getPrecio();
            contProductos++;

            // se obtiene el código del producto con el precio mayor
            if (pMayor.getPrecio() < listaProductos.get(indice).getPrecio()) {
                pMayor = listaProductos.get(indice);
            }

            // se obtiene el código del producto con el precio menor
            if (pMenor.getPrecio() > listaProductos.get(indice).getPrecio()) {
                pMenor = listaProductos.get(indice);
            }
        }

        // Impresión del reporte
        String productoMayor = repositorio.findById(Long.parseLong(pMayor.getCodigo() + "")).get().getNombre();
        String productoMenor = repositorio.findById(Long.parseLong(pMenor.getCodigo() + "")).get().getNombre();
        informe = String.format("Producto precio mayor: %s\nProducto precio menor: %s\nPromedio precios: %.1f\n Valor del inventario: %.1f", productoMayor, productoMenor, promedio / contProductos, valorInventario);
        JOptionPane.showMessageDialog(vista, informe, "Informe", JOptionPane.INFORMATION_MESSAGE);
    }

    // @TODO Agregar funcionalidad completa
    // Se agregan productos a la base de datos
    public boolean agregarProducto(String nombre, double precio, int inventario) {

        Long codigoProducto = verificarExistencia(nombre);

        if (codigoProducto != -1L) {
            return false;
        } else {
            // Se guarda el producto en la base de datos
            Producto prod = Producto.crearProductos(nombre, precio, inventario);
            repositorio.save(prod);
            return true;
        }
    }

    // Si se puede actualizar producto se debe retornar true, en otro caso false
    public boolean actualizarProductoParte1() {
        JTable tabla = vista.getTblProductos();
        DefaultTableModel tableModel = (DefaultTableModel) tabla.getModel();
        if (tabla.getSelectedRow() != -1) {
            String nombreProducto = (String) tableModel.getValueAt(tabla.getSelectedRow(), 0);
            Double precioProducto = (Double) tableModel.getValueAt(tabla.getSelectedRow(), 1);
            Integer inventarioProducto = (Integer) tableModel.getValueAt(tabla.getSelectedRow(), 2);

            JFrame frameActualizar = vista.getJFrame1();
            frameActualizar.setVisible(true);

            JTextField tf1 = vista.getTxtNombre1();
            tf1.setText(nombreProducto);
            JTextField tf2 = vista.getTxtPrecio1();
            tf2.setText(precioProducto + "");
            JTextField tf3 = vista.getTxtInventario1();
            tf3.setText(inventarioProducto + "");

            return true;
        } else {
            return false;
        }
    }

    public boolean actualizarProductoParte2() {

        JTable tabla = vista.getTblProductos();
        DefaultTableModel tableModel = (DefaultTableModel) tabla.getModel();
        String nombreProducto = (String) tableModel.getValueAt(tabla.getSelectedRow(), 0);

        Long codigoProducto = verificarExistencia(nombreProducto);

        if (codigoProducto != -1L) {
            Producto p = repositorio.findById(codigoProducto).get();
            p.setPrecio(Double.parseDouble(vista.getTxtPrecio1().getText()));
            p.setInventario(Integer.parseInt(vista.getTxtInventario1().getText()));
            repositorio.save(p);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Procedimiento a realizar cuando se presiona el botón de agregar producto
        if (e.getSource() == vista.getBtnAgregarProducto()) {
            if (vista.getTxtNombre().getText().equals("") || vista.getTxtPrecio().getText().equals("") || vista.getTxtInventario().getText().equals("")) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios", "Informacion", JOptionPane.WARNING_MESSAGE);
            } else {
                boolean estadoTransaccion = agregarProducto(vista.getTxtNombre().getText(),
                        Double.parseDouble(vista.getTxtPrecio().getText()),
                        Integer.parseInt(vista.getTxtInventario().getText()));
                if (estadoTransaccion == true) {
                    JOptionPane.showMessageDialog(vista, "El producto fue agregado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            listarProductos();
        }

        // Procedimiento a realizar cuando se presiona el botón borrar producto
        if (e.getSource() == vista.getBtnBorrarProducto()) {
            boolean estadoTransaccion = borrarProducto();
            listarProductos();
            if (estadoTransaccion == true) {
                JOptionPane.showMessageDialog(vista, "El producto fue borrado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        // Procedimiento a realizar cuando se presiona el botón informe
        if (e.getSource() == vista.getBtnInformes()) {
            generarInforme();
            listarProductos();
        }
        // Procedimiento a realizar cuando se presiona el botón actualizar
        if (e.getSource() == vista.getBtnActualizaProducto()) {
            boolean estadoTransaccion = actualizarProductoParte1();
            if (estadoTransaccion == false) {
                JOptionPane.showMessageDialog(vista, "Seleccione un elemento de la tabla a ser actualizado", "Informacion", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.getSource() == vista.getBtnActualizarProductoJFrameAct()) {
            if (vista.getTxtNombre1().getText().equals("") || vista.getTxtPrecio1().getText().equals("") || vista.getTxtInventario1().getText().equals("")) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios", "Informacion", JOptionPane.WARNING_MESSAGE);
            } else {
                boolean estadoTransaccion = actualizarProductoParte2();
                listarProductos();
                if (estadoTransaccion == true) {
                    JOptionPane.showMessageDialog(vista, "El producto fue actualizado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
}
