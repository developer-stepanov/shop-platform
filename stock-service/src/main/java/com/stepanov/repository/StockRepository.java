package com.stepanov.repository;

import com.stepanov.entity.StockItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface StockRepository extends JpaRepository<StockItem, UUID> {

    StockItem findBySku(String sku);

    List<StockItem> findBySkuIn(Collection<String> skus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockItem s where s.sku = :sku")
    StockItem lockStockItemBySku(@Param("sku") String sku);

}
