package org.example.config;

import org.example.repository.CampaignRepository;
import org.example.service.CampaignService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
public class AppConfig {

    @Bean
    public CampaignService campaignService(CampaignRepository campaignRepository) {
        return new CampaignService(campaignRepository);
    }
}
