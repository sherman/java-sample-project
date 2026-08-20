package com.github.sherman.server.grpc;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import io.grpc.BindableService;
import io.grpc.ClientInterceptor;
import io.grpc.netty.NettyServerBuilder;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

/** Registers application gRPC services and server options with Guice. */
public class GrpcServicesModule extends AbstractModule {

    private final List<BindableService> grpcServices;

    @Nullable
    private final Consumer<NettyServerBuilder> grpcServerConfigurer;

    public GrpcServicesModule(
        List<BindableService> grpcServices,
        @Nullable Consumer<NettyServerBuilder> grpcServerConfigurer
    ) {
        this.grpcServices = grpcServices;
        this.grpcServerConfigurer = grpcServerConfigurer;
    }

    @Override
    protected void configure() {
        var serviceBinder = Multibinder.newSetBinder(binder(), BindableService.class);
        grpcServices.forEach(service -> serviceBinder.addBinding().toInstance(service));

        var configurerKey = Key.get(
            new TypeLiteral<Consumer<NettyServerBuilder>>() {
            },
            GrpcServerOptions.class
        );

        OptionalBinder.newOptionalBinder(binder(), configurerKey);
        if (grpcServerConfigurer != null) {
            bind(configurerKey).toInstance(grpcServerConfigurer);
        }

        // Make injecting these sets safe when an application has no interceptors.
        Multibinder.newSetBinder(binder(), ClientInterceptor.class);
        Multibinder.newSetBinder(binder(), PrioritizedServerInterceptor.class);
    }
}
