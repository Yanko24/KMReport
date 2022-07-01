package com.yankee.kmreport.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class MeterMetric {
    @Builder.Default
    private Long count = 0L;

    @Builder.Default
    private Double fifteenMinuteRate = 0D;

    @Builder.Default
    private Double fiveMinuteRate = 0D;

    @Builder.Default
    private Double oneMinuteRate = 0D;

    @Builder.Default
    private Double meanRate = 0D;

    public MeterMetric() {
        this.count = 0L;
        this.fifteenMinuteRate = 0D;
        this.fiveMinuteRate = 0D;
        this.oneMinuteRate = 0D;
        this.meanRate = 0D;
    }

    /**
     * 将metrics进行累加
     *
     * @param meterMetric {@link MeterMetric}
     * @return {@link MeterMetric}
     */
    public MeterMetric summation(MeterMetric meterMetric) {
        return MeterMetric.builder()
                .count(meterMetric.getCount() + count)
                .fifteenMinuteRate(meterMetric.getFifteenMinuteRate() + fifteenMinuteRate)
                .fiveMinuteRate(meterMetric.getFiveMinuteRate() + fiveMinuteRate)
                .oneMinuteRate(meterMetric.getOneMinuteRate() + oneMinuteRate)
                .meanRate(meterMetric.getMeanRate() + meanRate)
                .build();
    }
}
