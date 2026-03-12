package Servidor;

import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import com.tienda.grpc.CarritoServiceGrpc;
import com.tienda.grpc.Producto;
import com.tienda.grpc.ListaProductos;
import com.tienda.grpc.Empty;
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
        gui.cargarInventario(inventario);
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

    @Override
    public void listarProductos(Empty request, StreamObserver<ListaProductos> responseObserver) {
        ListaProductos.Builder lista = ListaProductos.newBuilder();
        for (Producto p : catalogo.values()) {
            Producto productoConStock = Producto.newBuilder()
                    .setId(p.getId())
                    .setNombre(p.getNombre())
                    .setPrecio(p.getPrecio())
                    .setCantidad(inventario.getOrDefault(p.getId(), 0))
                    .build();
            lista.addProductos(productoConStock);
        }
        responseObserver.onNext(lista.build());
        responseObserver.onCompleted();
    }

    @Override
    public void procesarCarrito(CarritoRequest request, StreamObserver<CarritoResponse> responseObserver) {
        System.out.println("Procesando carrito del usuario: " + request.getUsuarioId());

        //Validar carrito vacío
        if (request.getItemsCount() == 0) {
            CarritoResponse error = CarritoResponse.newBuilder().setEstado("CARRITO_VACIO").setTransaccionId("N/A")
                    .setTotalNeto(0).setImpuestos(0).setTotalPagar(0).build();

            responseObserver.onNext(error);
            responseObserver.onCompleted();
            return;
        }

        double subtotal = 0;

        //Validar productos del carrito
        for (Producto p : request.getItemsList()) {
            if (p.getCantidad() <= 0 || p.getPrecio() <= 0) {
                CarritoResponse error = CarritoResponse.newBuilder()
                        .setEstado("DATOS_INVALIDOS").setTransaccionId("N/A").setTotalNeto(0).setImpuestos(0).setTotalPagar(0).build();

                responseObserver.onNext(error);
                responseObserver.onCompleted();
                return;
            }

            int stockActual = inventario.getOrDefault(p.getId(), 0);

            if (stockActual < p.getCantidad()) {

                CarritoResponse error = CarritoResponse.newBuilder()
                        .setEstado("SIN_STOCK")
                        .setTransaccionId("N/A")
                        .setTotalNeto(0)
                        .setImpuestos(0)
                        .setTotalPagar(0)
                        .build();

                responseObserver.onNext(error);
                responseObserver.onCompleted();
                return;
            }

            subtotal += p.getPrecio() * p.getCantidad();
        }

        //Descontar inventario
        for (Producto p : request.getItemsList()) {
            inventario.computeIfPresent(p.getId(),
                    (k, v) -> v - p.getCantidad());
            gui.actualizarStock(p.getId(), inventario.get(p.getId()));
        }

        double impuestos = subtotal * 0.16;
        double total = subtotal + impuestos;

        CarritoResponse response = CarritoResponse.newBuilder().setTransaccionId(UUID.randomUUID().toString())
                .setTotalNeto(subtotal).setImpuestos(impuestos).setTotalPagar(total)
                .setEstado("EXITOSO").build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}