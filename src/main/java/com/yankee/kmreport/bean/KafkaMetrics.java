package com.yankee.kmreport.bean;

public enum KafkaMetrics {
    /**
     * kafka集群中的partition总数
     */
    PARTITION_COUNT("kafka.server:type=ReplicaManager,name=PartitionCount", "Value"),
    /**
     * kafka集群消息入站总bytes，可单独监控每个topic
     */
    BYTES_IN_PER_SEC("kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec", "Count"),
    /**
     * kafka集群消息出战总bytes，可单独监控每个topic
     */
    BYTES_OUT_PER_SEC("kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec", "Count"),
    /**
     * kafka集群入站消息总数，可单独监控每个topic
     */
    MESSAGES_IN_COUNT_PER_SEC("kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec", "Count"),
    /**
     * kafka集群入站消息tps，可单独监控每个topic
     */
    MESSAGES_IN_TPS_PER_SEC("kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec", "OneMinuteRate");

    /**
     * metric名称
     */
    private String objName;

    /**
     * metric属性
     */
    private String objAttribute;

    KafkaMetrics(String name, String attribute) {
        this.objName = name;
        this.objAttribute = attribute;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public String getObjAttribute() {
        return objAttribute;
    }

    public void setObjAttribute(String objAttribute) {
        this.objAttribute = objAttribute;
    }
}
