package ru.itis.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import ru.itis.api.dictionary.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal totalCost;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<UserTransaction> users;

    @ManyToOne
    @JoinColumn(name="travel_id")
    private Travel travel;

    @ManyToOne
    @JoinColumn(name="creator_id")
    private User creator;
}
