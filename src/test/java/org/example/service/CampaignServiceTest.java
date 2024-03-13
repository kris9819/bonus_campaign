package org.example.service;

import org.example.model.Campaign;
import org.example.model.Condition;
import org.example.model.Offer;
import org.example.repository.CampaignRepository;
import org.example.utils.ConditionType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CampaignServiceTest {

    private final CampaignRepository campaignRepository = Mockito.mock(CampaignRepository.class);

    private final CampaignService campaignService = new CampaignService(campaignRepository);

    @Test
    public void noActiveCampaignsInDatabase() {
        List<Campaign> campaigns = Collections.emptyList();
        when(campaignRepository.findEligibleCampaignsForGivenDate(any(LocalDate.class), any())).thenReturn(campaigns);

        String customerUuid = "abc";
        String country = "Poland";
        LocalDate registrationDate = LocalDate.of(2024, 3, 12);
        BigDecimal depositAmount = BigDecimal.TEN;
        boolean isFirstDeposit = true;

        List<Offer> eligibleOffers = campaignService.getEligibleOffers(customerUuid, country, registrationDate, depositAmount, isFirstDeposit);

        assertThat(eligibleOffers).isEmpty();
    }

    @Test
    public void shouldReturnOfferListForEligibleCustomer() {
        Campaign campaign = createCampaign();
        List<Offer> offerList = List.of(createOffer(campaign));
        campaign.setOffers(offerList);
        List<Condition> conditionList = List.of(createCountryCondition("Poland"));
        campaign.setConditions(conditionList);

        List<Campaign> campaigns = Collections.singletonList(campaign);
        when(campaignRepository.findEligibleCampaignsForGivenDate(any(LocalDate.class), any())).thenReturn(campaigns);

        String customerUuid = "abc";
        String country = "Poland";
        LocalDate registrationDate = LocalDate.of(2024, 3, 12);
        BigDecimal depositAmount = BigDecimal.TEN;
        boolean isFirstDeposit = true;

        List<Offer> eligibleOffers = campaignService.getEligibleOffers(customerUuid, country, registrationDate, depositAmount, isFirstDeposit);

        assertThat(eligibleOffers).hasSize(1);
    }

    @Test
    public void shouldReturnEmptyListForWrongCountryCustomer() {
        Campaign campaign = createCampaign();
        List<Offer> offerList = List.of(createOffer(campaign));
        campaign.setOffers(offerList);
        List<Condition> conditionList = List.of(createCountryCondition("Poland"));
        campaign.setConditions(conditionList);

        List<Campaign> campaigns = Collections.singletonList(campaign);
        when(campaignRepository.findEligibleCampaignsForGivenDate(any(LocalDate.class), any())).thenReturn(campaigns);

        String customerUuid = "abc";
        String country = "US";
        LocalDate registrationDate = LocalDate.of(2024, 3, 12);
        BigDecimal depositAmount = BigDecimal.TEN;
        boolean isFirstDeposit = true;

        List<Offer> eligibleOffers = campaignService.getEligibleOffers(customerUuid, country, registrationDate, depositAmount, isFirstDeposit);

        assertThat(eligibleOffers).isEmpty();
    }

    @Test
    public void shouldReturnEmptyListDepositLoverThanRequired() {
        Campaign campaign = createCampaign();
        List<Offer> offerList = List.of(createOffer(campaign));
        campaign.setOffers(offerList);
        List<Condition> conditionList = List.of(createMinDepositCondition());
        campaign.setConditions(conditionList);

        List<Campaign> campaigns = Collections.singletonList(campaign);
        when(campaignRepository.findEligibleCampaignsForGivenDate(any(LocalDate.class), any())).thenReturn(campaigns);

        String customerUuid = "abc";
        String country = "Poland";
        LocalDate registrationDate = LocalDate.of(2024, 3, 12);
        BigDecimal depositAmount = BigDecimal.ONE;
        boolean isFirstDeposit = true;

        List<Offer> eligibleOffers = campaignService.getEligibleOffers(customerUuid, country, registrationDate, depositAmount, isFirstDeposit);

        assertThat(eligibleOffers).isEmpty();
    }

    @Test
    public void shouldReturnEmptyListForExpiredOffer() {
        Campaign campaign = createCampaign();
        List<Offer> offerList = List.of(createOffer(campaign));
        campaign.setOffers(offerList);
        List<Condition> conditionList = List.of(createRegistrationDateCondition(LocalDate.of(2024, 3, 13)));
        campaign.setConditions(conditionList);

        List<Campaign> campaigns = Collections.singletonList(campaign);
        when(campaignRepository.findEligibleCampaignsForGivenDate(any(LocalDate.class), any())).thenReturn(campaigns);

        String customerUuid = "abc";
        String country = "Poland";
        LocalDate registrationDate = LocalDate.of(2024, 3, 12);
        BigDecimal depositAmount = BigDecimal.TEN;
        boolean isFirstDeposit = true;

        List<Offer> eligibleOffers = campaignService.getEligibleOffers(customerUuid, country, registrationDate, depositAmount, isFirstDeposit);

        assertThat(eligibleOffers).isEmpty();
    }

    @Test
    public void shouldReturnOfferListForFirstDeposit() {
        Campaign campaign = createCampaign();
        List<Offer> offerList = List.of(createOffer(campaign));
        campaign.setOffers(offerList);
        List<Condition> conditionList = List.of(createFirstDepositCondition(true));
        campaign.setConditions(conditionList);

        List<Campaign> campaigns = Collections.singletonList(campaign);
        when(campaignRepository.findEligibleCampaignsForGivenDate(any(LocalDate.class), any())).thenReturn(campaigns);

        String customerUuid = "abc";
        String country = "Poland";
        LocalDate registrationDate = LocalDate.of(2024, 3, 12);
        BigDecimal depositAmount = BigDecimal.TEN;
        boolean isFirstDeposit = true;

        List<Offer> eligibleOffers = campaignService.getEligibleOffers(customerUuid, country, registrationDate, depositAmount, isFirstDeposit);

        assertThat(eligibleOffers).hasSize(1);
    }

    private Campaign createCampaign() {
        Campaign campaign = new Campaign();
        campaign.setUuid("abc");
        campaign.setName("Test Campaign");
        campaign.setStartDate(LocalDate.of(2024, 3, 10));
        campaign.setEndDate(LocalDate.of(2024, 3, 17));
        campaign.setAmount(BigDecimal.ONE);
        return campaign;
    }

    private Offer createOffer(Campaign campaign) {
        Offer offer = new Offer();
        offer.setOfferUuid("test-offer");
        offer.setCampaign(campaign);
        offer.setExpirationDate(LocalDate.of(2024, 3, 17));
        return offer;
    }

    private Condition createCountryCondition(String value) {
        Condition condition = new Condition();
        condition.setName(ConditionType.COUNTRY.name());
        condition.setValue(value);
        return condition;
    }

    private Condition createFirstDepositCondition(boolean value) {
        Condition condition = new Condition();
        condition.setName(ConditionType.IS_FIRST_DEPOSIT.name());
        condition.setValue(String.valueOf(value));
        return condition;
    }

    private Condition createMinDepositCondition() {
        Condition condition = new Condition();
        condition.setName(ConditionType.MIN_DEPOSIT_AMOUNT.name());
        condition.setValue(BigDecimal.TEN.toString());
        return condition;
    }

    private Condition createRegistrationDateCondition(LocalDate localDate) {
        Condition condition = new Condition();
        condition.setName(ConditionType.REGISTRATION_DATE.name());
        condition.setValue(localDate.toString());
        return condition;
    }
}
