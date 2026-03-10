package Cliente;

import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import com.tienda.grpc.CarritoServiceGrpc;
import com.tienda.grpc.Producto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CarritoGRPCclient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        CarritoServiceGrpc.CarritoServiceBlockingStub stub =
                CarritoServiceGrpc.newBlockingStub(channel);

        // Creamos algunos productos usando el Builder
        Producto p1 = Producto.newBuilder()
                .setId("PROD-001")
                .setNombre("Laptop Gamer")
                .setPrecio(1200.00)
                .setCantidad(1)
                .build();

        Producto p2 = Producto.newBuilder()
                .setId("PROD-002")
                .setNombre("Mouse Óptico")
                .setPrecio(25.50)
                .setCantidad(2)
                .build();

        // Construimos la petición del carrito
        CarritoRequest request = CarritoRequest.newBuilder()
                .setUsuarioId("USER-123")
                .addItems(p1) // Agregamos productos individualmente
                .addItems(p2)
                .build();

        // Llamada RPC
        System.out.println("Enviando carrito al servidor...");
        CarritoResponse response = stub.procesarCarrito(request);

        // Imprimimos resultados
        System.out.println("--- Factura Generada ---");
        System.out.println("ID Transacción: " + response.getTransaccionId());
        System.out.println("Subtotal: $" + response.getTotalNeto());
        System.out.println("Impuestos (16%): $" + response.getImpuestos());
        System.out.println("TOTAL A PAGAR: $" + response.getTotalPagar());

        channel.shutdown();
    }
}