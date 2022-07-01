package com.kmreport.model.metrics;

import com.kmreport.model.MeterMetric;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.List;

public class KafkaServerMetrics extends KafkaMetrics {
    /**
     * kafka.server下的metrics的统一前缀
     */
    private static final String PRE_KAFKA_SERVER = "kafka.server";

    /**
     * kafka.server下的BrokerTopicMetrics类型
     */
    private static final String BROKER_TOPIC_METRICS = "BrokerTopicMetrics";

    /**
     * kafka.server下的Partition类型
     */
    private static final String PARTITION = "Partition";

    /**
     * kafka集群消息入站总bytes
     */
    public static final String BYTES_IN_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec";

    /**
     * kafka集群消息出战总bytes
     */
    public static final String BYTES_OUT_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec";

    /**
     * kafka集群入站消息
     */
    public static final String MESSAGES_IN_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec";

    public static MeterMetric getBytesInPerSec(List<MBeanServerConnection> connections, String topic) {
        return getBrokerTopicMeterMetrics(connections, "BytesInPerSec", topic);
    }

    public static MeterMetric getBytesOutPerSec(List<MBeanServerConnection> connections, String topic) {
        return getBrokerTopicMeterMetrics(connections, "BytesOutPerSec", topic);
    }

    public static MeterMetric getBytesRejectedPerSec(List<MBeanServerConnection> connections, String topic) {
        return getBrokerTopicMeterMetrics(connections, "BytesRejectedPerSec", topic);
    }

    public static MeterMetric getFailedFetchRequestsPerSec(List<MBeanServerConnection> connections, String topic) {
        return getBrokerTopicMeterMetrics(connections, "FailedFetchRequestsPerSec", topic);
    }

    public static MeterMetric getFailedProduceRequestsPerSec(List<MBeanServerConnection> connections, String topic) {
        return getBrokerTopicMeterMetrics(connections, "FailedProduceRequestsPerSec", topic);
    }

    public static MeterMetric getMessagesInPerSec(List<MBeanServerConnection> connections, String topic) {
        return getBrokerTopicMeterMetrics(connections, "MessagesInPerSec", topic);
    }

    /**
     * 获取BrokerTopicMetrics
     *
     * @param connections {@link MBeanServerConnection}
     * @param name        objectName
     * @param topic       topic名称
     * @return {@link MeterMetric}
     */
    private static MeterMetric getBrokerTopicMeterMetrics(List<MBeanServerConnection> connections, String name, String topic) {
        MeterMetric meterMetrics = new MeterMetric();
        for (MBeanServerConnection connection : connections) {
            meterMetrics = meterMetrics.summation(getMeterMetric(connection, getObjectName(BROKER_TOPIC_METRICS, name, topic)));
        }
        return meterMetrics;
    }

    /**
     * 查询kafka.server下的metric
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
                objectName = new ObjectName(PRE_KAFKA_SERVER + ":type=" + type + ",name=" + name + ",topic=" + topic);
            } else {
                objectName = new ObjectName(PRE_KAFKA_SERVER + ":type=" + type + ",name=" + name);
            }
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return objectName;
    }
}
