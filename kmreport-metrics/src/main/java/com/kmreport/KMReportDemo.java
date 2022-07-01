package com.kmreport;

import cn.hutool.setting.dialect.Props;
import com.kmreport.jmx.JmxConnectionForCluster;
import com.kmreport.jmx.KafkaMBeanServerConnection;
import com.kmreport.model.BrokerIdentity;
import com.kmreport.model.KafkaMetricsInfo;
import com.kmreport.model.metrics.BrokerMetrics;
import com.kmreport.model.metrics.KafkaMetrics;
import com.kmreport.report.MetricsReport;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.common.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KMReportDemo {
    private static final Logger log = LoggerFactory.getLogger(KMReportDemo.class);

    private static final String fileName = "jmx.properties";

    /**
     * SchedulerThreadPool中空闲的线程数量
     */
    private static final String corePoolSize;

    /**
     * Scheduler调度器，延迟开始的时间（单位：秒）
     */
    private static final Long initialDelay;

    /**
     * 周期性调度的间隔时间（单位：秒）
     */
    private static final Long period;

    /**
     * 需要获取metrics的topic
     */
    private static final String[] topics;

    /**
     * kafka集群的brokers
     */
    private static final List<BrokerIdentity> brokerIdentities = new ArrayList<>();

    static {
        // 读取配置文件
        Props properties = new Props(fileName);
        // 获取scheduler相关配置
        corePoolSize = properties.getProperty("core.pool.size");
        initialDelay = properties.getLong("initial.delay");
        period = properties.getLong("period");
        // 获取配置的topic
        String topicList = properties.getProperty("topic.list");
        topics = topicList.split(",");
        // 获取配置的bootstrap-servers和jmxPort
        Integer jmxPort = properties.getInt("jmx.port");
        String bootstrapServers = properties.getProperty("bootstrap.servers");
        try {
            // 通过kafka-clients获取broker信息
            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            Collection<Node> nodes = KafkaAdminClient.create(props).describeCluster().nodes().get();
            nodes.forEach(node -> brokerIdentities.add(BrokerIdentity.builder().id(node.id()).host(node.host()).jmxPort(jmxPort).build()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<MBeanServerConnection> connections = KafkaMBeanServerConnection.init(brokerIdentities);

        try {
            // 初始化JmxConnection连接
            Boolean init = JmxConnectionForCluster.init(brokerIdentities);
            // 存放每个topic的指标
            Map<String, Map<String, KafkaMetricsInfo>> kafkaMetricsInfoMap = new HashMap<>();

            ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(Integer.parseInt(corePoolSize));
            scheduledThreadPool.scheduleAtFixedRate(() -> {
                if (init) {
                    for (String topic : topics) {
                        // 获取topic对应的指标
                        // Long byteInPerSec = JmxConnectionForCluster.getByteInPerSec(topic);
                        // Long byteOutPerSec = JmxConnectionForCluster.getByteOutPerSec(topic);
                        // Long messagesInCountPerSec = JmxConnectionForCluster.getMessagesInCountPerSec(topic);
                        // Double messagesInTpsPerSec = JmxConnectionForCluster.getMessagesInTpsPerSec(topic);
                        // Map<Integer, Long> endOffset = JmxConnectionForCluster.getLogEndOffset(topic);

                        BrokerMetrics brokerMetrics = KafkaMetrics.getBrokerMetrics(connections, topic);
                        log.info("Broker的监控指标为：{}", brokerMetrics);
                        System.out.println(brokerMetrics);

                        // 构造kafkaMetricsInfo
                        Map<String, KafkaMetricsInfo> currentKafkaMetricsInfo = new HashMap<>();
                        // currentKafkaMetricsInfo.put("ByteInPerSec",
                        //         KafkaMetricsInfo.builder().metricsValue(byteInPerSec).build());
                        // currentKafkaMetricsInfo.put("ByteOutPerSec",
                        //         KafkaMetricsInfo.builder().metricsValue(byteOutPerSec).build());
                        // currentKafkaMetricsInfo.put("MessagesInCountPerSec",
                        //         KafkaMetricsInfo.builder().metricsValue(messagesInCountPerSec).build());
                        // currentKafkaMetricsInfo.put("MessagesInTpsPerSec",
                        //         KafkaMetricsInfo.builder().metricsValue(messagesInTpsPerSec).build());

                        // 获取kafka集群中上一次该topic的相关指标
                        Map<String, KafkaMetricsInfo> preKafkaMetricsInfo = kafkaMetricsInfoMap.getOrDefault(topic,
                                new HashMap<>());
                        log.info("kafka集群中【{}】上一次监控指标信息 => {}", topic, preKafkaMetricsInfo);
                        // 添加当前的指标到map中
                        kafkaMetricsInfoMap.put(topic, currentKafkaMetricsInfo);
                        log.info("kakfa集群中【{}】当前监控指标 => {}", topic, currentKafkaMetricsInfo);

                        // 告警线程
                        MetricsReport metricsReport = new MetricsReport(topic, preKafkaMetricsInfo, currentKafkaMetricsInfo);
                        Thread thread = new Thread(metricsReport);
                        thread.start();
                    }
                } else {
                    log.error("init jmxConnection error");
                }
            }, initialDelay, period, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}