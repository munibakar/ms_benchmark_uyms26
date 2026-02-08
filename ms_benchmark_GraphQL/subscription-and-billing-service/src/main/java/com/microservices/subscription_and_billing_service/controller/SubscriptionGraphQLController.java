package com.microservices.subscription_and_billing_service.controller;

import com.microservices.subscription_and_billing_service.dto.request.AddPaymentMethodRequest;
import com.microservices.subscription_and_billing_service.dto.request.CancelSubscriptionRequest;
import com.microservices.subscription_and_billing_service.dto.request.SubscribeRequest;
import com.microservices.subscription_and_billing_service.dto.response.*;
import com.microservices.subscription_and_billing_service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Subscription and Billing Service - GraphQL Controller
 * Tüm GraphQL query ve mutation'larını handle eder
 */
@Controller
public class SubscriptionGraphQLController {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionGraphQLController.class);

    private final SubscriptionService subscriptionService;
    private final SubscriptionPlanService subscriptionPlanService;
    private final PaymentService paymentService;
    private final BillingService billingService;

    public SubscriptionGraphQLController(SubscriptionService subscriptionService,
            SubscriptionPlanService subscriptionPlanService,
            PaymentService paymentService,
            BillingService billingService) {
        this.subscriptionService = subscriptionService;
        this.subscriptionPlanService = subscriptionPlanService;
        this.paymentService = paymentService;
        this.billingService = billingService;
    }

    // ============== Subscription Queries ==============

    /**
     * GraphQL Query: getMySubscription
     * Kullanıcının aktif aboneliğini getir
     */
    @QueryMapping
    public SubscriptionResponse getMySubscription(@Argument String userId) {
        log.info("GraphQL Query: getMySubscription for userId: {}", userId);
        return subscriptionService.getActiveSubscription(userId);
    }

    /**
     * GraphQL Query: getMySubscriptions
     * Kullanıcının tüm aboneliklerini getir
     */
    @QueryMapping
    public List<SubscriptionResponse> getMySubscriptions(@Argument String userId) {
        log.info("GraphQL Query: getMySubscriptions for userId: {}", userId);
        return subscriptionService.getAllSubscriptions(userId);
    }

    // ============== Subscription Plan Queries ==============

    /**
     * GraphQL Query: getAllPlans
     * Tüm aktif abonelik planlarını listele
     */
    @QueryMapping
    public List<SubscriptionPlanResponse> getAllPlans() {
        log.info("GraphQL Query: getAllPlans");
        return subscriptionPlanService.getAllActivePlans();
    }

    // ============== Payment Method Queries ==============

    /**
     * GraphQL Query: getPaymentMethods
     * Kullanıcının ödeme yöntemlerini listele
     */
    @QueryMapping
    public List<PaymentMethodResponse> getPaymentMethods(@Argument String userId) {
        log.info("GraphQL Query: getPaymentMethods for userId: {}", userId);
        return paymentService.getPaymentMethods(userId);
    }

    // ============== Billing Queries ==============

    /**
     * GraphQL Query: getBillingHistory
     * Kullanıcının fatura geçmişini getir
     */
    @QueryMapping
    public List<BillingHistoryResponse> getBillingHistory(@Argument String userId) {
        log.info("GraphQL Query: getBillingHistory for userId: {}", userId);
        return billingService.getBillingHistory(userId);
    }

    /**
     * GraphQL Query: getSuccessfulPayments
     * Başarılı ödemeleri getir
     */
    @QueryMapping
    public List<BillingHistoryResponse> getSuccessfulPayments(@Argument String userId) {
        log.info("GraphQL Query: getSuccessfulPayments for userId: {}", userId);
        return billingService.getSuccessfulPayments(userId);
    }

    // ============== Subscription Mutations ==============

    /**
     * GraphQL Mutation: subscribe
     * Yeni abonelik satın al
     */
    @MutationMapping
    public SubscriptionResponse subscribe(@Argument String userId, @Argument("input") SubscribeInput input) {
        log.info("GraphQL Mutation: subscribe for userId: {} with plan: {}", userId, input.planName());

        SubscribeRequest request = SubscribeRequest.builder()
                .planName(input.planName())
                .billingCycle(input.billingCycle())
                .paymentMethodId(input.paymentMethodId())
                .build();

        return subscriptionService.subscribe(userId, request);
    }

    /**
     * GraphQL Mutation: cancelSubscription
     * Aboneliği iptal et
     */
    @MutationMapping
    public SubscriptionResponse cancelSubscription(@Argument String userId,
            @Argument("input") CancelSubscriptionInput input) {
        log.info("GraphQL Mutation: cancelSubscription for userId: {}", userId);

        CancelSubscriptionRequest request = CancelSubscriptionRequest.builder()
                .reason(input != null ? input.reason() : null)
                .immediate(input != null && input.immediate() != null ? input.immediate() : false)
                .build();

        return subscriptionService.cancelSubscription(userId, request);
    }

    // ============== Payment Method Mutations ==============

    /**
     * GraphQL Mutation: addPaymentMethod
     * Yeni ödeme yöntemi ekle
     */
    @MutationMapping
    public PaymentMethodResponse addPaymentMethod(@Argument String userId,
            @Argument("input") AddPaymentMethodInput input) {
        log.info("GraphQL Mutation: addPaymentMethod for userId: {}", userId);

        AddPaymentMethodRequest request = AddPaymentMethodRequest.builder()
                .cardHolderName(input.cardHolderName())
                .cardNumber(input.cardNumber())
                .expiryMonth(input.expiryMonth())
                .expiryYear(input.expiryYear())
                .cvv(input.cvv())
                .cardBrand(input.cardBrand())
                .setAsDefault(input.setAsDefault() != null ? input.setAsDefault() : false)
                .build();

        return paymentService.addPaymentMethod(userId, request);
    }

    /**
     * GraphQL Mutation: deletePaymentMethod
     * Ödeme yöntemini sil
     */
    @MutationMapping
    public Boolean deletePaymentMethod(@Argument String userId, @Argument Long id) {
        log.info("GraphQL Mutation: deletePaymentMethod {} for userId: {}", id, userId);

        paymentService.deletePaymentMethod(userId, id);
        return true;
    }

    // ============== Federation Resolvers ==============

    /**
     * Federation Resolver: User.subscription
     */
    @org.springframework.graphql.data.method.annotation.SchemaMapping(typeName = "User")
    public SubscriptionResponse subscription(User user) {
        log.info("Federation Resolver: Resolving subscription for userId: {}", user.userId());
        return subscriptionService.getActiveSubscription(user.userId());
    }

    /**
     * Federation Resolver: User.billingHistory
     */
    @org.springframework.graphql.data.method.annotation.SchemaMapping(typeName = "User")
    public List<BillingHistoryResponse> billingHistory(User user) {
        log.info("Federation Resolver: Resolving billingHistory for userId: {}", user.userId());
        return billingService.getBillingHistory(user.userId());
    }

    // ============== Input Records ==============

    /**
     * Federation User Entity Stub
     */
    public record User(String userId) {
    }

    public record SubscribeInput(
            String planName,
            String billingCycle,
            Long paymentMethodId) {
    }

    public record CancelSubscriptionInput(
            String reason,
            Boolean immediate) {
    }

    public record AddPaymentMethodInput(
            String cardHolderName,
            String cardNumber,
            String expiryMonth,
            String expiryYear,
            String cvv,
            String cardBrand,
            Boolean setAsDefault) {
    }
}
