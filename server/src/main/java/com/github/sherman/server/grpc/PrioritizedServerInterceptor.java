package com.github.sherman.server.grpc;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * Wraps a server interceptor with an ordering priority. Higher priorities are
 * intended to run earlier when a chain of interceptors handles a gRPC request.
 */
public class PrioritizedServerInterceptor
    implements ServerInterceptor, Comparable<PrioritizedServerInterceptor> {

    private final InterceptorPriority priority;
    private final ServerInterceptor origin;

    public PrioritizedServerInterceptor(InterceptorPriority priority, ServerInterceptor origin) {
        this.priority = priority;
        this.origin = origin;
    }

    public InterceptorPriority getPriority() {
        return priority;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next
    ) {
        return origin.interceptCall(call, headers, next);
    }

    @Override
    public int compareTo(PrioritizedServerInterceptor other) {
        return Integer.compare(priority.getValue(), other.priority.getValue());
    }
}
