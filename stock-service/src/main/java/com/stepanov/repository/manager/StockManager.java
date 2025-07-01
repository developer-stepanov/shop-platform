package com.stepanov.repository.manager;

import com.stepanov.entity.StockItem;
import com.stepanov.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StockManager {

    private final StockRepository stockRepository;

    public StockItem getRequiredLockedStockItemBySku(@NonNull String sku) {
        return stockRepository.lockStockItemBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Stock item not found for SKU: %s", sku))
                );
    }
}
