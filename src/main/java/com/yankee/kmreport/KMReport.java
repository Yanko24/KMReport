package com.yankee.kmreport;

import com.yankee.kmreport.bean.KafkaMetricsInfo;
import com.yankee.kmreport.jmx.JmxConnectionForCluster;
import com.yankee.kmreport.report.MetricsReport;
import com.yankee.kmreport.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KMReport {
    private static final Logger LOG = LoggerFactory.getLogger(KMReport.class);

    private static final String fileName = "jmx.properties";

    public static void main(String[] args) {
        try {
            // 读取配置文件
            PropertiesUtil propertiesUtil = PropertiesUtil.getInstance(fileName);
            // 获取scheduler相关配置
            String corePoolSize = propertiesUtil.getProperty("core.pool.size");
            long initialDelay = Long.parseLong(propertiesUtil.getProperty("initial.delay"));
            long period = propertiesUtil.getLong("period");
            // 获取配置的topic
            String topicList = propertiesUtil.getProperty("topics");
            String[] topics = topicList.split(",");
            // 获取配置的kafka-broker-jmx地址
            String brokerList = propertiesUtil.getProperty("brokers");
            String[] brokers = brokerList.split(",");
            // 存储brokers
            List<String> jmxBrokers = new ArrayList<>(Arrays.asList(brokers));
            // 初始化JmxConnection连接
            Boolean init = JmxConnectionForCluster.init(jmxBrokers, true);
            // 存放每个topic的指标
            Map<String, Map<String, KafkaMetricsInfo>> kafkaMetricsInfoMap = new HashMap<>();

            ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(Integer.parseInt(corePoolSize));
            scheduledThreadPool.scheduleAtFixedRate(() -> {
                if (init) {
                    for (String topic : topics) {
                        // 获取topic对应的指标
                        Integer partitionCount = JmxConnectionForCluster.getPartitionCount();
                        Long byteInPerSec = JmxConnectionForCluster.getByteInPerSec(topic);
                        Long byteOutPerSec = JmxConnectionForCluster.getByteOutPerSec(topic);
                        Long messagesInCountPerSec = JmxConnectionForCluster.getMessagesInCountPerSec(topic);
                        Double messagesInTpsPerSec = JmxConnectionForCluster.getMessagesInTpsPerSec(topic);

                        // 构造kafkaMetricsInfo
                        Map<String, KafkaMetricsInfo> currentKafkaMetricsInfo = new HashMap<>();
                        currentKafkaMetricsInfo.put("PartitionCount",
                                KafkaMetricsInfo.builder().metricsValue(partitionCount).build());
                        currentKafkaMetricsInfo.put("ByteInPerSec",
                                KafkaMetricsInfo.builder().metricsValue(byteInPerSec).build());
                        currentKafkaMetricsInfo.put("ByteOutPerSec",
                                KafkaMetricsInfo.builder().metricsValue(byteOutPerSec).build());
                        currentKafkaMetricsInfo.put("MessagesInCountPerSec",
                                KafkaMetricsInfo.builder().metricsValue(messagesInCountPerSec).build());
                        currentKafkaMetricsInfo.put("MessagesInTpsPerSec",
                                KafkaMetricsInfo.builder().metricsValue(messagesInTpsPerSec).build());

                        // 获取kafka集群中上一次该topic的相关指标
                        Map<String, KafkaMetricsInfo> preKafkaMetricsInfo = kafkaMetricsInfoMap.getOrDefault(topic,
                                new HashMap<>());
                        LOG.info("kafka集群中【{}】上一次监控指标信息 => {}", topic, preKafkaMetricsInfo);
                        // 添加当前的指标到map中
                        kafkaMetricsInfoMap.put(topic, currentKafkaMetricsInfo);
                        LOG.info("kakfa集群中【{}】当前监控指标 => {}", topic, currentKafkaMetricsInfo);

                        // 告警线程
                        MetricsReport metricsReport = new MetricsReport(topic, preKafkaMetricsInfo, currentKafkaMetricsInfo);
                        Thread thread = new Thread(metricsReport);
                        thread.start();
                    }
                } else {
                    LOG.error("init jmxConnection error");
                }
            }, initialDelay, period, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}