package com.kmreport.model.metrics;

import com.kmreport.model.MeterMetric;
import com.kmreport.model.PartitionValueMetric;

import javax.management.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class KafkaMetrics {
    /**
     * 获取metrics的["Count", "FifteenMinuteRate", "FiveMinuteRate", "OneMinuteRate", "MeanRate"]5个指标的值
     *
     * @param connection {@link MBeanServerConnection}
     * @param objectName {@link ObjectName}
     * @return {@link MeterMetric}
     */
    protected static MeterMetric getMeterMetric(MBeanServerConnection connection, ObjectName objectName) {
        // 默认构造全部值为0
        MeterMetric meterMetric = new MeterMetric();
        try {
            AttributeList attributesList = connection.getAttributes(objectName, new String[]{"Count", "FifteenMinuteRate", "FiveMinuteRate", "OneMinuteRate", "MeanRate"});
            List<Attribute> attributes = attributesList.asList();
            for (Attribute attribute : attributes) {
                switch (attribute.getName()) {
                    case "Count":
                        meterMetric.setCount(Long.parseLong(attribute.getValue().toString()));
                        break;
                    case "FifteenMinuteRate":
                        meterMetric.setFifteenMinuteRate(Double.parseDouble(attribute.getValue().toString()));
                        break;
                    case "FiveMinuteRate":
                        meterMetric.setFiveMinuteRate(Double.parseDouble(attribute.getValue().toString()));
                        break;
                    case "OneMinuteRate":
                        meterMetric.setOneMinuteRate(Double.parseDouble(attribute.getValue().toString()));
                        break;
                    case "MeanRate":
                        meterMetric.setMeanRate(Double.parseDouble(attribute.getValue().toString()));
                }
            }
        } catch (ReflectionException | IOException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException ignored) {
        }
        return meterMetric;
    }

    /**
     * 获取单个value值的metric
     *
     * @param connection {@link MBeanServerConnection}
     * @param objectName {@link ObjectName}
     * @return value
     */
    protected static Long getValueMetric(MBeanServerConnection connection, ObjectName objectName) {
        // 初始化属性的值
        long attribute = 0L;
        try {
            // 获取objectName对应的attribute的值
            attribute = Long.parseLong(connection.getAttribute(objectName, "Value").toString());
        } catch (ReflectionException | AttributeNotFoundException | MBeanException | IOException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException ignored) {
        }
        return attribute;
    }

    /**
     * 获取和分区有关的value值的metric
     *
     * @param connection {@link MBeanServerConnection}
     * @param objectName {@link ObjectName}
     * @return {@link PartitionValueMetric}
     */
    protected static PartitionValueMetric getPartitionValueMetric(MBeanServerConnection connection, ObjectName objectName) {
        PartitionValueMetric partitionValueMetric = new PartitionValueMetric();
        // 初始化属性的值
        long attribute = 0L;
        // 保存partition对应的多个ObjectName
        Set<ObjectName> objectNames = null;
        // 保存分区和值的映射关系（partition -> attribute)
        Map<Integer, Long> map = new HashMap<>();
        try {
            objectNames = connection.queryNames(objectName, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert objectNames != null;
        for (ObjectName name : objectNames) {
            try {
                // 获取分区的ID
                Integer partition = Integer.parseInt(name.getKeyProperty("partition"));
                // 获取objectName对应的attribute的值
                attribute = Long.parseLong(connection.getAttribute(name, "Value").toString());
                map.put(partition, attribute);
            } catch (ReflectionException | AttributeNotFoundException | MBeanException | IOException e) {
                e.printStackTrace();
            } catch (InstanceNotFoundException ignore) {
            }
        }
        partitionValueMetric.setValue(map);
        return partitionValueMetric;
    }

    public static BrokerMetrics getBrokerMetrics(List<MBeanServerConnection> connections, String topic) {
        return BrokerMetrics.builder()
                .bytesInPerSec(KafkaServerMetrics.getBytesInPerSec(connections, topic))
                .bytesOutPerSec(KafkaServerMetrics.getBytesOutPerSec(connections, topic))
                .bytesRejectedPerSec(KafkaServerMetrics.getBytesRejectedPerSec(connections, topic))
                .failedFetchRequestsPerSec(KafkaServerMetrics.getFailedFetchRequestsPerSec(connections, topic))
                .failedProduceRequestsPerSec(KafkaServerMetrics.getFailedProduceRequestsPerSec(connections, topic))
                .messagesInPerSec(KafkaServerMetrics.getMessagesInPerSec(connections, topic))
                .logStartOffset(LogMetrics.getLogStartOffset(connections, topic))
                .logEndOffset(LogMetrics.getLogEndOffset(connections, topic))
                .size(LogMetrics.getSize(connections, topic))
                .build();
    }
}
