package ru.itis.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.api.entity.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByTravelId(Long travelId);

    void deleteByTravelId(Long travelId);
}
