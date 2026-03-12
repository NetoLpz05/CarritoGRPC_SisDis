package Servidor;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CarritoGRPCserver {
    private Server server;
    private final int PORT = 50051;

    public void iniciar() throws Exception {
        InterfazServ gui = new InterfazServ();
        server = ServerBuilder.forPort(PORT).addService(new CarritoServiceImpl(gui)).build().start();

        System.out.println("Servidor GRPC iniciado en el puerto " + PORT);
        server.awaitTermination();
    }

    public static void main(String[] args) throws Exception {

        CarritoGRPCserver servidor = new CarritoGRPCserver();
        servidor.iniciar();

    }
}