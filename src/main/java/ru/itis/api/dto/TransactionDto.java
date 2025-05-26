package ru.itis.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.itis.api.dictionary.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TransactionDto {
    private Long id;
    private BigDecimal totalCost;
    private String description;
    private LocalDateTime createdAt;
    private Category category;
}
