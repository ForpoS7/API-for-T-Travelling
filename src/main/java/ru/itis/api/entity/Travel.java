package ru.itis.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Travel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_budget", nullable = false)
    private BigDecimal totalBudget;

    @Column(name = "date_of_begin", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dateOfBegin;

    @Column(name = "date_of_end", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dateOfEnd;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
    private List<UserTravel> users = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="creator_id")
    private User creator;

    private Boolean isActive;
}
