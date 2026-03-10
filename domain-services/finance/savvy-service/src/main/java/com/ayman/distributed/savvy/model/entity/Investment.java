package com.ayman.distributed.savvy.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Represents a financial investment made by the user.
 */
@Entity
@Data
@Table(name = "investment")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    
    private String description;
    
    private String symbol;
    
    private Integer amountInvested;
    
    private Integer currentValue;
    
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
