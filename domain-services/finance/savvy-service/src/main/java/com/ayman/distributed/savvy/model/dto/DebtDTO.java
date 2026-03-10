package com.ayman.distributed.savvy.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DebtDTO {
    private long id;
    private String title;
    private String description;
    private Integer amount;
    private LocalDate dueDate;
}
