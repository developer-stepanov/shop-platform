package com.stepanov.repository;

import com.stepanov.entity.StockItemEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface StockRepository extends JpaRepository<StockItemEntity, UUID> {

    StockItemEntity findBySku(String sku);

    List<StockItemEntity> findBySkuIn(Collection<String> skus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockItemEntity s where s.sku = :sku")
    StockItemEntity lockStockItemBySku(@Param("sku") String sku);

}
