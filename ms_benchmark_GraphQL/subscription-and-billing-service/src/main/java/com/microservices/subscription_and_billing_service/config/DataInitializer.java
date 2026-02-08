package com.microservices.subscription_and_billing_service.config;

import com.microservices.subscription_and_billing_service.entity.SubscriptionPlan;
import com.microservices.subscription_and_billing_service.repository.SubscriptionPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Data Initializer
 * Uygulama başlatıldığında gerekli başlangıç verilerini yükler
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public DataInitializer(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeSubscriptionPlans();
    }

    /**
     * Abonelik planlarını initialize et
     */
    private void initializeSubscriptionPlans() {
        log.info("Checking subscription plans...");

        // Eğer zaten plan varsa, tekrar ekleme
        if (subscriptionPlanRepository.count() > 0) {
            log.info("Subscription plans already exist. Skipping initialization.");
            return;
        }

        log.info("Initializing subscription plans...");

        // BASIC Plan
        SubscriptionPlan basicPlan = SubscriptionPlan.builder()
                .planName("BASIC")
                .displayName("Temel Plan")
                .description("HD kalitede yayın. Tek ekranda izleme imkanı. Reklamsız deneyim.")
                .monthlyPrice(new BigDecimal("99.99"))
                .yearlyPrice(new BigDecimal("999.99"))
                .maxScreens(1)
                .maxProfiles(1)
                .videoQuality("HD")
                .downloadAvailable(false)
                .adsIncluded(false)
                .isActive(true)
                .sortOrder(1)
                .build();

        // STANDARD Plan
        SubscriptionPlan standardPlan = SubscriptionPlan.builder()
                .planName("STANDARD")
                .displayName("Standart Plan")
                .description("Full HD kalitede yayın. 2 ekranda eş zamanlı izleme. İndirme özelliği.")
                .monthlyPrice(new BigDecimal("149.99"))
                .yearlyPrice(new BigDecimal("1499.99"))
                .maxScreens(2)
                .maxProfiles(2)
                .videoQuality("Full HD")
                .downloadAvailable(true)
                .adsIncluded(false)
                .isActive(true)
                .sortOrder(2)
                .build();

        // PREMIUM Plan
        SubscriptionPlan premiumPlan = SubscriptionPlan.builder()
                .planName("PREMIUM")
                .displayName("Premium Plan")
                .description("4K Ultra HD kalitede yayın. 4 ekranda eş zamanlı izleme. İndirme ve özel içerikler.")
                .monthlyPrice(new BigDecimal("199.99"))
                .yearlyPrice(new BigDecimal("1999.99"))
                .maxScreens(4)
                .maxProfiles(5)
                .videoQuality("4K Ultra HD")
                .downloadAvailable(true)
                .adsIncluded(false)
                .isActive(true)
                .sortOrder(3)
                .build();

        // Planları kaydet
        subscriptionPlanRepository.save(basicPlan);
        subscriptionPlanRepository.save(standardPlan);
        subscriptionPlanRepository.save(premiumPlan);

        log.info("Successfully initialized 3 subscription plans: BASIC, STANDARD, PREMIUM");
    }
}

