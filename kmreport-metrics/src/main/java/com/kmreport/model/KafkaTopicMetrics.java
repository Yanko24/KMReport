package com.kmreport.model;

import com.kmreport.model.metrics.KafkaServerMetrics;

public class KafkaTopicMetrics {
    /**
     * kafka集群消息入站总bytes，可单独监控每个topic
     */
    public static final String BYTES_IN_PER_SEC = KafkaServerMetrics.BYTES_IN_PER_SEC + ",topic=@";

    /**
     * kafka集群消息出战总bytes，可单独监控每个topic
     */
    public static final String BYTES_OUT_PER_SEC = KafkaServerMetrics.BYTES_OUT_PER_SEC + ",topic=@";

    /**
     * kafka集群入站消息，可单独监控每个topic
     */
    public static final String MESSAGES_IN_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec,topic=@";

    /**
     * kafka集群中topic的即将写入的最大offset
     */
    public static final String LOG_END_OF_OFFSET = "kafka.log:type=Log,name=LogEndOffset,topic=@,partition=*";

    public static String addTopic(String metric, String topic) {
        return metric.replace("@", topic);
    }
}
