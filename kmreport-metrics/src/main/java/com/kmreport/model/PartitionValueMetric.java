package com.kmreport.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
@AllArgsConstructor
public class PartitionValueMetric {
    @Builder.Default
    private Map<Integer, Long> value = new HashMap<>();

    public PartitionValueMetric() {
        this.value = new HashMap<>();
    }

    /**
     * 将metrics进行累加
     *
     * @param partitionValueMetric {@link PartitionValueMetric}
     * @return {@link PartitionValueMetric}
     */
    public PartitionValueMetric summation(PartitionValueMetric partitionValueMetric) {
        Map<Integer, Long> values = new HashMap<>();
        values = partitionValueMetric.getValue();
        for (Integer partition : value.keySet()) {
            values.put(partition, values.get(partition) + value.get(partition));
        }
        return PartitionValueMetric.builder()
                .value(values)
                .build();
    }

    /**
     * 求metrics中的最大值
     *
     * @param partitionValueMetric {@link PartitionValueMetric}
     * @return {@link PartitionValueMetric}
     */
    public PartitionValueMetric maximum(PartitionValueMetric partitionValueMetric) {
        Map<Integer, Long> values = partitionValueMetric.getValue();
        for (Integer partition : value.keySet()) {
            values.put(partition, values.get(partition) > value.get(partition) ? values.get(partition) : value.get(partition));
        }
        return PartitionValueMetric.builder()
                .value(values)
                .build();
    }

    /**
     * 求metrics中的最小值
     *
     * @param partitionValueMetric {@link PartitionValueMetric}
     * @return {@link PartitionValueMetric}
     */
    public PartitionValueMetric minimum(PartitionValueMetric partitionValueMetric) {
        Map<Integer, Long> values = new HashMap<>();
        values = partitionValueMetric.getValue();
        for (Integer partition : value.keySet()) {
            values.put(partition, values.get(partition) < value.get(partition) ? values.get(partition) : value.get(partition));
        }
        return PartitionValueMetric.builder()
                .value(values)
                .build();
    }
}
