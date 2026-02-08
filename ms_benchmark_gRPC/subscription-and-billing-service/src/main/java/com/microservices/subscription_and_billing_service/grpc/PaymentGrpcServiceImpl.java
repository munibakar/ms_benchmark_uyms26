package com.microservices.subscription_and_billing_service.grpc;

import com.microservices.subscription_and_billing_service.dto.response.PaymentMethodResponse;
import com.microservices.subscription_and_billing_service.grpc.proto.GetPaymentsRequest;
import com.microservices.subscription_and_billing_service.grpc.proto.PaymentGrpcServiceGrpc;
import com.microservices.subscription_and_billing_service.grpc.proto.PaymentListResponse;
import com.microservices.subscription_and_billing_service.grpc.proto.PaymentResponse;
import com.microservices.subscription_and_billing_service.service.PaymentService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC Server Implementation for Payment Service
 * User Service bu endpoint'i gRPC üzerinden çağırır
 */
@GrpcService
public class PaymentGrpcServiceImpl extends PaymentGrpcServiceGrpc.PaymentGrpcServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(PaymentGrpcServiceImpl.class);

    private final PaymentService paymentService;

    public PaymentGrpcServiceImpl(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * gRPC üzerinden kullanıcının son ödemelerini getir
     */
    @Override
    public void getRecentPayments(
            GetPaymentsRequest request,
            StreamObserver<PaymentListResponse> responseObserver) {

        log.info("gRPC: Getting recent payments for userId: {}", request.getUserId());

        try {
            // Mevcut service'i kullan (payment methods döndürüyor, mock payment olarak
            // kullanacağız)
            List<PaymentMethodResponse> paymentMethods = paymentService.getPaymentMethods(request.getUserId());

            // Payment method'ları payment history gibi dönüştür (mock)
            List<PaymentResponse> grpcPayments = paymentMethods.stream()
                    .limit(5) // Son 5 ödeme
                    .map(pm -> PaymentResponse.newBuilder()
                            .setId(pm.getId())
                            .setAmount(29.99) // Mock amount
                            .setStatus("SUCCESS")
                            .setPaymentDate("2026-01-01") // Mock date
                            .build())
                    .collect(Collectors.toList());

            PaymentListResponse grpcResponse = PaymentListResponse.newBuilder()
                    .addAllPayments(grpcPayments)
                    .build();

            log.info("gRPC: {} payments retrieved successfully for userId: {}", grpcPayments.size(),
                    request.getUserId());

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC: Failed to get payments for userId: {}", request.getUserId(), e);
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                            .withDescription("Payments not found: " + e.getMessage())
                            .asRuntimeException());
        }
    }
}
