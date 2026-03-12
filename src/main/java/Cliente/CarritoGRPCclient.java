package Cliente;

import com.tienda.grpc.CarritoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CarritoGRPCclient {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext().build();

        CarritoServiceGrpc.CarritoServiceBlockingStub stub =
                CarritoServiceGrpc.newBlockingStub(channel);

        new InterfazCliente(stub);

    }
}