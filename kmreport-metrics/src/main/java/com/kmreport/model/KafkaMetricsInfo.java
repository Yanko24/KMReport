package com.kmreport.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMetricsInfo {
    /**
     * metrics value
     */
    private Number metricsValue;

    /**
     * metrics未变动次数
     */
    @Builder.Default
    private Integer count = 0;
}
