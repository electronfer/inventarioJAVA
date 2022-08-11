package controlador;

import modelo.Producto;
import modelo.RepositorioProducto;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vista.Vista;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*

// Código para crear la base de datos en MySQL
create database testdb;
use testdb;

CREATE TABLE Productos (
    codigo int AUTO_INCREMENT,
    nombre varchar(255),
    precio double,
    inventario int,
    primary key(codigo)
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

        System.out.println("filas tabla " + tabla.getRowCount());
        System.out.println("filas DB " + listaProductos.size());

        for (int i = 0; i < tabla.getRowCount(); i++) {
            tabla.setValueAt("", i, 0);
            tabla.setValueAt(0, i, 1);
            tabla.setValueAt(0, i, 2);
        }

        int row = 0;
        for (Producto s : listaProductos) {
            tabla.setValueAt(s.getNombre(), row, 0);
            tabla.setValueAt(s.getPrecio(), row, 1);
            tabla.setValueAt(s.getInventario(), row, 2);
            row++;
        }
        return listaProductos;
    }

    // Se verifica si el producto se encuentra en la base de datos, en caso tal que si se encuentre
    // se retorna el codigo (Id) del producto, sino se encuentra se retorna -1
    public Long verificarExistencia(String nombre) {
        List<Producto> resultado = listarProductos();

        for (Producto producto : resultado) {
            if (producto.getNombre().equals(nombre)) {
                return producto.getCodigo();
            }
        }
        return -1L;
    }

    // Se borra el producto si se encuentra en la tabla de productos seleccionada en la interfaz
    public boolean borrarProducto() {

        JTable tabla = vista.getTblProductos();
        String nombreProducto = (String) tabla.getModel().getValueAt(tabla.getSelectedRow(), 0);

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
        int codigo_mayor = Integer.parseInt(listaProductos.get(1).getCodigo() + "");
        int codigo_menor = Integer.parseInt(listaProductos.get(1).getCodigo() + "");
        double promedio = 0.0;
        double contProductos = 0;

        for (Producto p : listaProductos) {
            valorInventario += p.getPrecio() * p.getInventario();
            promedio += p.getPrecio();
            contProductos++;

            // se obtiene el código del producto con el precio mayor
            if (listaProductos.get(codigo_mayor).getPrecio() < p.getPrecio()) {
                codigo_mayor = Integer.parseInt(p.getCodigo() + "");
            }

            // se obtiene el código del producto con el precio menor
            if (listaProductos.get(codigo_menor).getPrecio() > p.getPrecio()) {
                codigo_menor = Integer.parseInt(p.getCodigo() + "");
            }
        }
        String productoMayor = repositorio.findById(Long.parseLong(codigo_mayor + "")).get().getNombre();
        String productoMenor = repositorio.findById(Long.parseLong(codigo_menor + "")).get().getNombre();
        informe = String.format("Producto precio mayor: %s\n Producto precio menor: %s\n Promedio precios: %.1f\n Valor del inventario: %.1f", productoMayor, productoMenor, promedio / contProductos, valorInventario);
        JOptionPane.showMessageDialog(vista, informe, "Informe", JOptionPane.INFORMATION_MESSAGE);
    }

    // @TODO Agregar funcionalidad completa
    // Se agregan productos a la base de datos
    public boolean agregarProducto(String nombre, double precio, int inventario) {

        Long codigoProducto = verificarExistencia(nombre);
        List<Producto> listaProductos = listarProductos();
        JTable tabla = vista.getTblProductos();
        DefaultTableModel tableModel = (DefaultTableModel) tabla.getModel();
        for (Producto s : listaProductos) {
            tableModel.addRow(new Object[]{s.getNombre(), s.getPrecio(), s.getInventario()});
        }
        if (codigoProducto != -1L) {
            return false;
        } else {
            Producto prod = Producto.crearProductos(nombre, precio, inventario);
            repositorio.save(prod);
            return true;
        }
    }

    /*
    // Si se puede actualizar producto se debe retornar true, en otro caso false
    public boolean actualizarProducto(String nombre, double precio, int inventario) {
        Long codigoProducto = verificarExistencia(nombre);
        if ( codigoProducto != -1L) {
            listaProductos.replace(codigo, new Producto(codigo, nombre, precio, inventario));
            return true;
        } else {
            return false;
        }

    }
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vista.getBtnAgregarProducto()) {
            if (vista.getTxtNombre().getText().equals("") || vista.getTxtPrecio().getText().equals("") || vista.getTxtInventario().getText().equals("")) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son oblogatorios", "Informacion", JOptionPane.WARNING_MESSAGE);
            } else {
                boolean estadoTransaccion = agregarProducto(vista.getTxtNombre().getText(),
                        Double.parseDouble(vista.getTxtPrecio().getText()),
                        Integer.parseInt(vista.getTxtPrecio().getText()));
                if (estadoTransaccion == true) {
                    JOptionPane.showMessageDialog(vista, "El producto fue agregado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            listarProductos();
        }

        if (e.getSource() == vista.getBtnBorrarProducto()) {
            boolean estadoTransaccion = borrarProducto();
            listarProductos();
            if (estadoTransaccion == true) {
                JOptionPane.showMessageDialog(vista, "El producto fue borrado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if (e.getSource() == vista.getBtnInformes()) {
            generarInforme();
            listarProductos();
        }
        /*
        if (e.getSource() == vista.getBtnActualizaProducto()) {
            boolean estadoTransaccion = actualizarProducto(vista.getTxtNombre().getText(),
                    Double.parseDouble(vista.getTxtPrecio().getText()),
                    Integer.parseInt(vista.getTxtPrecio().getText()));
            listar();

        }
         */
    }
}
