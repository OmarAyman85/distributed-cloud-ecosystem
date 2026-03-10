package com.ayman.distributed.savvy.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Represents a savings goal or account for the user.
 */
@Entity
@Data
@Table(name = "saving")
public class Saving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    
    private String description;
    
    private Integer targetAmount;
    
    private Integer currentAmount;
    
    private LocalDate targetDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
