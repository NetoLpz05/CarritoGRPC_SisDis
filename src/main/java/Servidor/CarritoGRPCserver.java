package Servidor;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class CarritoGRPCserver {
    public static void main(String[] args) throws InterruptedException {
        InterfazServ gui = new InterfazServ();
        Server server = ServerBuilder.forPort(50051).addService(new CarritoServiceImpl(gui)).build();

        try {
            server.start();
            System.out.println("Servidor de Carrito iniciado en el puerto 50051...");
            server.awaitTermination();
        } catch (IOException ex) {
            System.getLogger(CarritoGRPCserver.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}