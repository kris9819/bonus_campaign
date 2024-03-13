package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.model.Offer;
import org.example.service.CampaignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@AllArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping("/eligible")
    public List<Offer> getEligibleOffers(@RequestParam String customerUuid,
                                         @RequestParam String country,
                                         @RequestParam LocalDate registrationDate,
                                         @RequestParam BigDecimal depositAmount,
                                         @RequestParam boolean isFirstDeposit) {
        return campaignService.getEligibleOffers(customerUuid, country, registrationDate, depositAmount, isFirstDeposit);
    }
}
