package com.ayman.distributed.savvy.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class InvestmentDTO {
    private long id;
    private String title;
    private String description;
    private String symbol;
    private Integer amountInvested;
    private Integer currentValue;
    private LocalDate date;
}
