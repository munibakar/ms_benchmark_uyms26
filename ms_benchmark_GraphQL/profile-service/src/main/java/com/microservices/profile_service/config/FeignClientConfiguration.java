package com.microservices.profile_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign Client Configuration
 * 
 * Servisler arası (service-to-service) iletişimde gerekli header'ları ekler.
 * Bu sayede diğer servislerin gateway verification filter'ları geçilebilir.
 */
@Configuration
public class FeignClientConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FeignClientConfiguration.class);

    /**
     * Servisler arası isteklerde Gateway header'ı ekleyen interceptor
     */
    @Bean
    public RequestInterceptor serviceToServiceRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Gateway verification için gerekli header
                template.header("X-Gateway-Request", "true");
                
                // Servisler arası iletişim için ek bilgilendirme header'ı
                template.header("X-Internal-Service", "profile-service");
                
                log.debug("🔗 Feign Request: {} {} - Gateway headers added", 
                         template.method(), template.url());
            }
        };
    }
}
