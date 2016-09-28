package beans;

import models.Producto;
import models.Usuario;
import org.hibernate.tuple.entity.EntityMetamodel;
import wrappers.GestorProductos;
import wrappers.GestorUsuarios;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import java.util.*;

/**
 * Created by forte on 26/09/16.
 */
@ManagedBean
@SessionScoped
public class CarritoBean {
    Producto actual;
    Long cantidad;
    HashMap<Producto,Long> productos;

    public CarritoBean() {
        productos = new HashMap<>();
    }

    public String agregarACarrito() {
        if(actual != null) {
            //hacer toda
            productos.put(actual,cantidad);

            cantidad = null;
            actual = null;

            System.out.println("Items en el carrito: ");
            for(Producto p : productos.keySet()) {
                System.out.println(p.getNombre() + " : " + p.getCantidad());
            }
        }

        return "productosDisponibles";
    }

    public String formCarrito(Producto p) {
        actual = p;

        return "agregarProducto";
    }

    public void validarExistencia(ComponentSystemEvent event) {

        UIComponent components = event.getComponent();

        //get email value from component
        UIInput productoIdInput = (UIInput)components.findComponent("productoIdHidden");
        String rawProductoId = productoIdInput.getLocalValue() == null ? "" : productoIdInput.getLocalValue().toString();

        //get password value from component
        UIInput cantidadInput = (UIInput)components.findComponent("inputCantidad");
        String rawCantidad = cantidadInput.getLocalValue() == null ? "" : cantidadInput.getLocalValue().toString();

        Producto found;
        try {
            found = GestorProductos.getById(Long.parseLong(rawProductoId));
        } catch (NumberFormatException e) {
            found = null;
        }

        if(found == null) {
            System.out.println("NO SE ENCUENTRA");
            //no encontro nada
            FacesMessage message = new FacesMessage("Fallo.",
                    "No se encontro producto");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null,message);

            fc.renderResponse();
        }
        else {
            Long cantidad;
            try {
                cantidad = Long.parseLong(rawCantidad);
            } catch (NumberFormatException e) {
                cantidad = 0L;
            }

            if(found.getCantidad() < cantidad) {
                System.out.println("NO HAY EXISTENCIA SUFICIENTE PARA ID:" + found.getId());
                //no hay suficiente
                FacesMessage message = new FacesMessage("Fallo en operacion.",
                        "No hay existencia suficiente");
                message.setSeverity(FacesMessage.SEVERITY_WARN);

                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(null,message);

                fc.renderResponse();
            }
        }
    }

    public Producto getActual() {
        return actual;
    }

    public void setActual(Producto actual) {
        this.actual = actual;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public List<Map.Entry<Producto, Long>> getProductos() {
        Set<Map.Entry<Producto, Long>> productSet = productos.entrySet();

        return new ArrayList<>(productSet);
    }

    public void setProductos(HashMap<Producto, Long> productos) {
        this.productos = productos;
    }
}
