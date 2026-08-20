package com.github.sherman.server.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sherman.server.grpc.testing.GreeterGrpc;
import com.github.sherman.server.grpc.testing.HelloReply;
import com.github.sherman.server.grpc.testing.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class GrpcProtoServiceTest {

    @Test
    void generatedProtobufMessageRoundTrips() throws Exception {
        var request = HelloRequest.newBuilder().setName("Codex").build();

        var parsed = HelloRequest.parseFrom(request.toByteArray());

        assertEquals(request, parsed);
    }

    @Test
    void generatedGrpcServiceHandlesRequest() throws Exception {
        var serverName = InProcessServerBuilder.generateName();
        Server server = InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(new GreeterGrpc.GreeterImplBase() {
                @Override
                public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
                    responseObserver.onNext(
                        HelloReply.newBuilder().setMessage("Hello, " + request.getName()).build()
                    );
                    responseObserver.onCompleted();
                }
            })
            .build()
            .start();
        ManagedChannel channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();

        try {
            var response = GreeterGrpc.newBlockingStub(channel)
                .sayHello(HelloRequest.newBuilder().setName("Codex").build());

            assertEquals("Hello, Codex", response.getMessage());
        } finally {
            channel.shutdownNow();
            server.shutdownNow();
            assertTrue(channel.awaitTermination(5, TimeUnit.SECONDS));
            assertTrue(server.awaitTermination(5, TimeUnit.SECONDS));
        }
    }
}
