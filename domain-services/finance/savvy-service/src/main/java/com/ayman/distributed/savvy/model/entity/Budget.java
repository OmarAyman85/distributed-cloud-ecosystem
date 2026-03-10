package com.ayman.distributed.savvy.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @Column(name = "budget_limit")
    private Integer budgetLimit;

    @Enumerated(EnumType.STRING)
    private Period period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public enum Period {
        WEEKLY,
        MONTHLY,
        YEARLY
    }
}
