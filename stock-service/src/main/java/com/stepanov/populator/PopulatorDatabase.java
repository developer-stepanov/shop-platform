package com.stepanov.populator;

import com.stepanov.entity.StockItemEntity;
import com.stepanov.repository.StockRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopulatorDatabase implements ApplicationRunner {

    private final StockRepository stockRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        //guard to avoid duplicates
        if (stockRepository.count() > 0) {
            log.info("Database already populated, skipping");
            return;
        }

        log.info("Populating database with initial stock …");

        stockRepository.save(
                StockItemEntity.builder()
                        .sku("SKU-001")
                        .name("MacBook Pro")
                        .description("Nice laptop!")
                        .unitPrice(new BigDecimal("4500"))
                        .availableQty(10)
                        .build());

        stockRepository.save(
                StockItemEntity.builder()
                        .sku("SKU-002")
                        .name("Iphone 16")
                        .description("Good choice")
                        .unitPrice(new BigDecimal("1100"))
                        .availableQty(10)
                        .build());

        stockRepository.save(
                StockItemEntity.builder()
                        .sku("SKU-003")
                        .name("MacBook Air")
                        .description("Affordable laptop")
                        .unitPrice(new BigDecimal("1500"))
                        .availableQty(10)
                        .build());

        log.info("Finished populating database");
    }
}
