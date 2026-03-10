package Servidor;

import com.google.protobuf.Empty;
import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import com.tienda.grpc.CarritoServiceGrpc;
import com.tienda.grpc.Producto;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CarritoServiceImpl extends CarritoServiceGrpc.CarritoServiceImplBase {
    private static final Map<String, Integer> inventario = new ConcurrentHashMap<>();
    private static final Map<String, Producto> catalogo = new HashMap<>();
    private final InterfazServ gui;

    public CarritoServiceImpl(InterfazServ gui) {
        this.gui = gui;
        inicializarDatos();
    }

    //Método para llenar el inventario y el catálogo de la tienda
    private void inicializarDatos() {

        Producto p1 = Producto.newBuilder().setId("PROD-001").setNombre("Laptop").setPrecio(1200).build();
        Producto p2 = Producto.newBuilder().setId("PROD-002").setNombre("Mouse").setPrecio(25).build();

        catalogo.put(p1.getId(), p1);
        inventario.put(p1.getId(), 10); //10 Laptops

        catalogo.put(p2.getId(), p2);
        inventario.put(p2.getId(), 50); //50 Mouse

    }
    //Método para listar los productos del carrito
    public void listarProductos() {

    }

    @Override
    public void procesarCarrito(CarritoRequest request, StreamObserver<CarritoResponse> responseObserver){
        System.out.println("Procesando carrito para el usuario: "
                + request.getUsuarioId());

        double subtotal = 0;

        /* Iteramos sobre la lista repetida de productos
           definida en el archivo proto */
        for (Producto p : request.getItemsList()) {
            subtotal += p.getPrecio() * p.getCantidad();
        }

        double impuestos = subtotal * 0.16; // IVA del 16%
        double total = subtotal + impuestos;

        /* Construimos la respuesta usando el Builder
           generado por Protobuf */
        CarritoResponse response = CarritoResponse.newBuilder()
                .setTransaccionId(UUID.randomUUID().toString())
                .setTotalNeto(subtotal)
                .setImpuestos(impuestos)
                .setTotalPagar(total)
                .setEstado("EXITOSO")
                .build();

        responseObserver.onNext(response); // Enviamos al cliente
        responseObserver.onCompleted();
    }
}
