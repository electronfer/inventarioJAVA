package controlador;

import modelo.Producto;
import modelo.RepositorioProducto;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import vista.Vista;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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
    public List<Producto> listarProductos() {

        List<Producto> listaProductos = (List<Producto>) repositorio.findAll();
        JTable tabla = vista.getTblProductos();
        //DefaultTableModel tableModel = (DefaultTableModel) tabla.getModel();
        
        System.out.println("filas tabla " + tabla.getRowCount());
        System.out.println("filas DB " + listaProductos.size());
        
        for (int i = 0; i < tabla.getRowCount(); i++) {
            tabla.setValueAt("", i, 0);
            tabla.setValueAt(0, i, 1);
            tabla.setValueAt(0, i, 2);
        }
        
        int row = 0;
        for (Producto s : listaProductos){
            tabla.setValueAt(s.getNombre(), row, 0);
            tabla.setValueAt(s.getPrecio(), row, 1);
            tabla.setValueAt(s.getInventario(), row, 2);
            row++;
        }
        
        
        /*
        for (int i = row; i < tabla.getRowCount(); i++) {
            tabla.setValueAt("", row, 0);
            tabla.setValueAt("", row, 1);
            tabla.setValueAt("", row, 2);
        }

        for (Producto s : listaProductos) {
            tableModel.addRow(new Object[]{s.getNombre(), s.getPrecio(), s.getInventario()});
        }
        */
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
        String nombreProducto = (String)tabla.getModel().getValueAt(tabla.getSelectedRow(), 0);
        Long codigoProducto = verificarExistencia(nombreProducto);
        if (codigoProducto != -1L) {
            repositorio.deleteById(codigoProducto);
            return true;
        } else {
            return false;
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
    public boolean agregarProducto(String nombre, double precio, int inventario) {

        Long codigoProducto = verificarExistencia(nombre);
        if (codigoProducto != -1L) {
            return false;
        } else {
            Producto prod = Producto.crearProductos(nombre, precio, inventario);
            repositorio.save(prod);
            return true;
        }
    }

    // Se debe imprimir el nombre del producto con el precio mayor, nombre del producto 
    // con el precio menor y el promedio de los precios de los productos y el valor del inventario  
    public void generarInforme() {

        List<Producto> resultado = (List<Producto>) repositorio.findAll();

        String informe;
        double valorInventario = 0.0;
        int codigo_mayor = Integer.parseInt(resultado.get(1).getCodigo() + "");
        int codigo_menor = Integer.parseInt(resultado.get(1).getCodigo() + "");
        double promedio = 0.0;
        double contProductos = 0;

        for (Producto p : resultado) {
            valorInventario += p.getPrecio() * p.getInventario();
            promedio += p.getPrecio();
            contProductos++;
            // se obtiene el código del producto con el precio mayor
            if (resultado.get(codigo_mayor).getPrecio() < p.getPrecio()) {
                codigo_mayor = Integer.parseInt(p.getCodigo() + "");
            }

            // se obtiene el código del producto con el precio menor
            if (resultado.get(codigo_menor).getPrecio() > p.getPrecio()) {
                codigo_menor = Integer.parseInt(p.getCodigo() + "");
            }
        }

        informe = String.format("Producto precio mayor: %s\n Producto precio menor: %s\n Promedio precios: %.1f\n Valor del inventario: %.1f", resultado.get(codigo_mayor).getNombre(), resultado.get(codigo_menor).getNombre(), promedio / contProductos, valorInventario);
        //JOptionPane.showMessageDialog(vista, informe);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == vista.getBtnAgregarProducto()) {
            boolean estadoTransaccion = agregarProducto(vista.getTxtNombre().getText(),
                    Double.parseDouble(vista.getTxtPrecio().getText()),
                    Integer.parseInt(vista.getTxtPrecio().getText()));

            listarProductos();
        }

        if (e.getSource() == vista.getBtnBorrarProducto()) {
            boolean estadoTransaccion = borrarProducto();
            listarProductos();
            if(estadoTransaccion == true){
                JOptionPane.showMessageDialog(vista,"El producto fue borrado con exitosamente","Informacion",JOptionPane.INFORMATION_MESSAGE); 
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
