package com.microservice.user_service.grpc.client;

import com.microservice.user_service.dto.response.UserDashboardResponse.PaymentInfo;
import com.microservice.user_service.grpc.proto.GetPaymentsRequest;
import com.microservice.user_service.grpc.proto.PaymentGrpcServiceGrpc;
import com.microservice.user_service.grpc.proto.PaymentListResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC Client for Payment Service
 * 
 * REAL gRPC implementation using @GrpcClient stub
 */
@Service
public class PaymentServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceGrpcClient.class);

    @GrpcClient("payment-service")
    private PaymentGrpcServiceGrpc.PaymentGrpcServiceBlockingStub paymentStub;

    /**
     * gRPC üzerinden kullanıcının son ödemelerini getir
     */
    public List<PaymentInfo> getRecentPayments(String userId) {
        long startTime = System.currentTimeMillis();
        log.info("gRPC Client: Getting payments for userId: {} (REAL gRPC CALL)", userId);

        try {
            // REAL gRPC stub call
            GetPaymentsRequest request = GetPaymentsRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            PaymentListResponse response = paymentStub.getRecentPayments(request);

            // gRPC response'u DTO'ya dönüştür
            List<PaymentInfo> payments = response.getPaymentsList().stream()
                    .map(grpcPayment -> PaymentInfo.builder()
                            .id(grpcPayment.getId())
                            .amount(grpcPayment.getAmount())
                            .status(grpcPayment.getStatus())
                            .paymentDate(grpcPayment.getPaymentDate())
                            .build())
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            log.info("gRPC Client: {} payments retrieved successfully - {}ms (REAL gRPC)", payments.size(), duration);

            return payments;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("gRPC Client: Failed after {}ms - {}", duration, e.getMessage());
            throw new RuntimeException("Failed to get payments via gRPC", e);
        }
    }
}
