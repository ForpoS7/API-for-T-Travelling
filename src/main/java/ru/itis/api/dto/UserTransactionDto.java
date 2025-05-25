package ru.itis.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserTransactionDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private BigDecimal shareAmount;
    private Boolean isRepaid;
}
