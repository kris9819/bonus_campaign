package org.example.repository;

import org.example.model.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
