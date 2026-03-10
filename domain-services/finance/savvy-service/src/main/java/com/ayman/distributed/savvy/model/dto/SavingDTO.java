package com.ayman.distributed.savvy.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SavingDTO {
    private long id;
    private String title;
    private String description;
    private Integer targetAmount;
    private Integer currentAmount;
    private LocalDate targetDate;
}
