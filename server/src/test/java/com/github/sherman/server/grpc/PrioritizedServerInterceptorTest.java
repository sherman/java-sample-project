package com.github.sherman.server.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class PrioritizedServerInterceptorTest {

    @Test
    void comparesInterceptorsByNumericPriority() {
        ServerInterceptor origin = new NoOpServerInterceptor();
        var interceptors = new ArrayList<>(List.of(
            new PrioritizedServerInterceptor(InterceptorPriority.AUTH, origin),
            new PrioritizedServerInterceptor(InterceptorPriority.LAST, origin),
            new PrioritizedServerInterceptor(InterceptorPriority.FIRST, origin),
            new PrioritizedServerInterceptor(InterceptorPriority.NONE, origin)
        ));

        interceptors.sort(null);

        assertEquals(
            List.of(
                InterceptorPriority.LAST,
                InterceptorPriority.NONE,
                InterceptorPriority.FIRST,
                InterceptorPriority.AUTH
            ),
            interceptors.stream().map(PrioritizedServerInterceptor::getPriority).toList()
        );
    }

    @Test
    void delegatesInterceptionToOrigin() {
        var invoked = new AtomicBoolean();
        var headers = new Metadata();
        ServerCall.Listener<Object> expectedListener = new ServerCall.Listener<>() {
        };
        ServerInterceptor origin = new ServerInterceptor() {
            @Override
            @SuppressWarnings("unchecked")
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
                ServerCall<ReqT, RespT> call,
                Metadata actualHeaders,
                ServerCallHandler<ReqT, RespT> next
            ) {
                invoked.set(true);
                assertSame(headers, actualHeaders);
                return (ServerCall.Listener<ReqT>) expectedListener;
            }
        };
        var interceptor = new PrioritizedServerInterceptor(InterceptorPriority.FIRST, origin);

        var listener = interceptor.<Object, Object>interceptCall(null, headers, null);

        assertTrue(invoked.get());
        assertSame(expectedListener, listener);
    }

    private static final class NoOpServerInterceptor implements ServerInterceptor {

        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
        ) {
            return new ServerCall.Listener<>() {
            };
        }
    }
}
