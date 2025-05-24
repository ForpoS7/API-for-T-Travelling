package ru.itis.api.dto;

import io.swagger.v3.oas.annotations.media.Content;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelParticipantsDto {
    private Long id;
    private String name;
    private Double totalBudget;
    private LocalDate dateOfBegin;
    private LocalDate dateOfEnd;
    private UserDto creator;
    private List<UserDto> participants;

    public TravelParticipantsDto(Long id, String name, Double totalBudget, LocalDate dateOfBegin,
                                 LocalDate dateOfEnd, UserDto creator) {
        this.id = id;
        this.name = name;
        this.totalBudget = totalBudget;
        this.dateOfBegin = dateOfBegin;
        this.dateOfEnd = dateOfEnd;
        this.creator = creator;
    }
}
