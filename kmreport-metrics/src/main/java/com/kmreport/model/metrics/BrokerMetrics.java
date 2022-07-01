package com.kmreport.model.metrics;

import com.kmreport.model.MeterMetric;
import com.kmreport.model.PartitionValueMetric;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class BrokerMetrics {
    @Builder.Default
    MeterMetric bytesInPerSec = new MeterMetric();

    @Builder.Default
    MeterMetric bytesOutPerSec = new MeterMetric();

    @Builder.Default
    MeterMetric bytesRejectedPerSec = new MeterMetric();

    @Builder.Default
    MeterMetric failedFetchRequestsPerSec = new MeterMetric();

    @Builder.Default
    MeterMetric failedProduceRequestsPerSec = new MeterMetric();

    @Builder.Default
    MeterMetric messagesInPerSec = new MeterMetric();

    @Builder.Default
    PartitionValueMetric logStartOffset = new PartitionValueMetric();

    @Builder.Default
    PartitionValueMetric logEndOffset = new PartitionValueMetric();

    @Builder.Default
    PartitionValueMetric size = new PartitionValueMetric();

    public BrokerMetrics() {
        this.bytesInPerSec = new MeterMetric();
        this.bytesOutPerSec = new MeterMetric();
        this.bytesRejectedPerSec = new MeterMetric();
        this.failedFetchRequestsPerSec = new MeterMetric();
        this.failedProduceRequestsPerSec = new MeterMetric();
        this.messagesInPerSec = new MeterMetric();
        this.logStartOffset = new PartitionValueMetric();
        this.logEndOffset = new PartitionValueMetric();
        this.size = new PartitionValueMetric();
    }
}
