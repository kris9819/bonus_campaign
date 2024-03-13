package org.example.repository;

import org.example.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}
