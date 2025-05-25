package ru.itis.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelParticipantsDto {
    public TravelParticipantsDto(Long id, String name, BigDecimal totalBudget, LocalDate dateOfBegin,
                                 LocalDate dateOfEnd, UserDto creator) {
        this.id = id;
        this.name = name;
        this.totalBudget = totalBudget;
        this.dateOfBegin = dateOfBegin;
        this.dateOfEnd = dateOfEnd;
        this.creator = creator;
    }
    private Long id;
    private String name;
    private BigDecimal totalBudget;
    private LocalDate dateOfBegin;
    private LocalDate dateOfEnd;
    private UserDto creator;
    private List<UserDto> participants;
}
