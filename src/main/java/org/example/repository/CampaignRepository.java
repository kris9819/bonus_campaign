package org.example.repository;

import org.example.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Query("SELECT c FROM Campaign c WHERE c.uuid = uuid AND current_date BETWEEN c.startDate AND c.endDate")
    List<Campaign> findEligibleCampaignsForGivenDate(@Param("current_date") LocalDate date, @Param("uuid") String uuid);
}
