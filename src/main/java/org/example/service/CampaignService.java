package org.example.service;

import lombok.AllArgsConstructor;
import org.example.model.Campaign;
import org.example.model.Condition;
import org.example.model.Offer;
import org.example.repository.CampaignRepository;
import org.example.utils.ConditionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    public List<Offer> getEligibleOffers(String customerUuid, String country, LocalDate registrationDate,
                                         BigDecimal depositAmount, boolean isFirstDeposit) {
        List<Campaign> activeCampaigns = campaignRepository.findEligibleCampaignsForGivenDate(registrationDate, customerUuid);
        List<Offer> eligibleOffers = new ArrayList<>();
        for (Campaign campaign : activeCampaigns) {
            if (isCustomerEligible(campaign, country, registrationDate, depositAmount, isFirstDeposit)) {
                eligibleOffers.addAll(campaign.getOffers());
            }
        }
        return eligibleOffers;
    }

    private boolean isCustomerEligible(Campaign campaign, String country,
                                       LocalDate registrationDate, BigDecimal depositAmount, boolean isFirstDeposit) {
        if (campaign.getOffers().isEmpty()) {
            return false;
        }
        for (Condition condition : campaign.getConditions()) {
            switch (ConditionType.valueOf(condition.getName())) {
                case COUNTRY -> {
                    if (!condition.getValue().equals(country)) {
                        return false;
                    }
                }
                case REGISTRATION_DATE -> {
                    if (registrationDate.isBefore(LocalDate.parse(condition.getValue()))) {
                        return false;
                    }
                }
                case MIN_DEPOSIT_AMOUNT -> {
                    if (depositAmount.compareTo(new BigDecimal(condition.getValue())) < 0) {
                        return false;
                    }
                }
                case IS_FIRST_DEPOSIT -> {
                    if (isFirstDeposit != Boolean.parseBoolean(condition.getValue())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}