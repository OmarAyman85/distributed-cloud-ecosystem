package com.ayman.distributed.savvy.services.stats;

import com.ayman.distributed.savvy.dto.GraphDTO;
import com.ayman.distributed.savvy.dto.StatsDTO;

public interface StatsService {

    GraphDTO getChartData();
    StatsDTO getStats();
}
