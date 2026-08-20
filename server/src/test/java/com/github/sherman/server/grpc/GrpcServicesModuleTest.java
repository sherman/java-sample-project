package com.github.sherman.server.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sherman.server.grpc.testing.GreeterGrpc;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.grpc.BindableService;
import io.grpc.ClientInterceptor;
import io.grpc.netty.NettyServerBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class GrpcServicesModuleTest {

    @Test
    void registersServicesAndEmptyInterceptorSetsWithoutConfigurer() {
        BindableService service = new GreeterGrpc.GreeterImplBase() {
        };
        var injector = Guice.createInjector(new GrpcServicesModule(List.of(service), null));

        assertEquals(Set.of(service), injector.getInstance(bindableServicesKey()));
        assertTrue(injector.getInstance(configurerKey()).isEmpty());
        assertTrue(injector.getInstance(clientInterceptorsKey()).isEmpty());
        assertTrue(injector.getInstance(prioritizedInterceptorsKey()).isEmpty());
    }

    @Test
    void exposesConfiguredServerOptions() {
        Consumer<NettyServerBuilder> configurer = builder -> builder.maxInboundMessageSize(1024);
        var injector = Guice.createInjector(new GrpcServicesModule(List.of(), configurer));

        assertSame(configurer, injector.getInstance(configurerKey()).orElseThrow());
    }

    private static Key<Set<BindableService>> bindableServicesKey() {
        return Key.get(new TypeLiteral<>() {
        });
    }

    private static Key<Optional<Consumer<NettyServerBuilder>>> configurerKey() {
        return Key.get(
            new TypeLiteral<>() {
            },
            GrpcServerOptions.class
        );
    }

    private static Key<Set<ClientInterceptor>> clientInterceptorsKey() {
        return Key.get(new TypeLiteral<>() {
        });
    }

    private static Key<Set<PrioritizedServerInterceptor>> prioritizedInterceptorsKey() {
        return Key.get(new TypeLiteral<>() {
        });
    }
}
