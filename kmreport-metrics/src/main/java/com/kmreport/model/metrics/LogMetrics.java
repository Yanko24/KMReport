package com.kmreport.model.metrics;

import com.kmreport.model.PartitionValueMetric;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.List;

public class LogMetrics extends KafkaMetrics {
    /**
     * kafka.log下的metrics的统一前缀
     */
    private static final String PRE_KAFKA_LOG = "kafka.log";

    /**
     * kafka.log下的Log类型
     */
    private static final String LOG = "Log";

    /**
     * kafka.log下的LogManager类型
     */
    private static final String LOG_MANAGER = "LogManager";

    public static PartitionValueMetric getLogEndOffset(List<MBeanServerConnection> connections, String topic) {
        return getLogPartitionValueMetrics(connections, "LogEndOffset", topic, "max");
    }

    public static PartitionValueMetric getLogStartOffset(List<MBeanServerConnection> connections, String topic) {
        return getLogPartitionValueMetrics(connections, "LogStartOffset", topic, "min");
    }

    public static PartitionValueMetric getNumLogSegments(List<MBeanServerConnection> connections, String topic) {
        return getLogPartitionValueMetrics(connections, "NumLogSegments", topic, "sum");
    }

    public static PartitionValueMetric getSize(List<MBeanServerConnection> connections, String topic) {
        return getLogPartitionValueMetrics(connections, "Size", topic, "sum");
    }

    public static Long getOfflineLogDirectoryCount(List<MBeanServerConnection> connections) {
        return getLogManagerValueMetrics(connections, "OfflineLogDirectoryCount", null);
    }

    /**
     * 获取LogMetrics
     *
     * @param connections {@link MBeanServerConnection}
     * @param name        objectName
     * @param topic       topic名称
     * @param operation   操作符号，max/min/sum分别对应metric的取值方式
     * @return {@link PartitionValueMetric}
     */
    private static PartitionValueMetric getLogPartitionValueMetrics(List<MBeanServerConnection> connections, String name, String topic, String operation) {
        PartitionValueMetric partitionValueMetric = new PartitionValueMetric();
        for (MBeanServerConnection connection : connections) {
            switch (operation) {
                case "max":
                    partitionValueMetric = partitionValueMetric.maximum(getPartitionValueMetric(connection, getObjectName(LOG, name, topic)));
                    break;
                case "min":
                    partitionValueMetric = partitionValueMetric.minimum(getPartitionValueMetric(connection, getObjectName(LOG, name, topic)));
                    break;
                case "sum":
                    partitionValueMetric = partitionValueMetric.summation(getPartitionValueMetric(connection, getObjectName(LOG, name, topic)));
                    break;
            }
        }
        return partitionValueMetric;
    }

    private static Long getLogManagerValueMetrics(List<MBeanServerConnection> connections, String name, String topic) {
        Long attributes = 0L;
        for (MBeanServerConnection connection : connections) {
            Long attribute = getValueMetric(connection, getObjectName(LOG_MANAGER, name, topic));
            attributes += attribute;
        }
        return attributes;
    }

    /**
     * 查询kafka.log下的metric
     *
     * @param type  type类型
     * @param name  metric名称
     * @param topic topic名称
     * @return {@link ObjectName}
     */
    public static ObjectName getObjectName(String type, String name, String topic) {
        ObjectName objectName = null;
        try {
            if (topic != null) {
                objectName = new ObjectName(PRE_KAFKA_LOG + ":type=" + type + ",name=" + name + ",topic=" + topic + ",partition=*");
            } else {
                objectName = new ObjectName(PRE_KAFKA_LOG + ":type=" + type + ",name=" + name);
            }
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return objectName;
    }
}
