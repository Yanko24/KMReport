package com.yankee.kmreport.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JmxConnectionForCluster {
    private static final Logger LOG = LoggerFactory.getLogger(JmxConnectionForCluster.class);
    private static final List<JmxConnection> conns = new ArrayList<>();

    /**
     * 初始化JmxConnection连接
     *
     * @param jmxBrokers      brokers
     * @param newKafkaVersion 是否是kafka新版本
     */
    public static Boolean init(List<String> jmxBrokers, Boolean newKafkaVersion) {
        for (String jmxBroker : jmxBrokers) {
            LOG.info("init jmxConnection [{}]", jmxBroker);
            JmxConnection conn = new JmxConnection(jmxBroker, newKafkaVersion);
            boolean init = conn.init();
            if (!init) {
                LOG.error("init jmxConnection error");
                return false;
            }
            conns.add(conn);
        }
        return true;
    }

    /**
     * 获取kafka集群的partition总数
     *
     * @return kafka集群的patition总数
     */
    public static Integer getPartitionCount() {
        Integer value = 0;
        for (JmxConnection conn : conns) {
            Integer temp = conn.getPartitionCount();
            value += temp;
        }
        return value;
    }

    /**
     * 获取kafka集群某个topic消息入站的byte大小
     *
     * @param topic topic名称
     * @return 入站消息byte大小
     */
    public static Long getByteInPerSec(String topic) {
        Long value = 0L;
        for (JmxConnection conn : conns) {
            Long temp = conn.getByteInPerSec(topic);
            value += temp;
        }
        return value;
    }

    /**
     * 获取kafka集群某个topic消息出站的byte大小
     *
     * @param topic topic名称
     * @return 出站消息byte大小
     */
    public static Long getByteOutPerSec(String topic) {
        Long value = 0L;
        for (JmxConnection conn : conns) {
            Long temp = conn.getByteOutPerSec(topic);
            value += temp;
        }
        return value;
    }

    /**
     * 获取kafka集群中某个topic的消息入站总数
     *
     * @param topic topic名称
     * @return 消息数量
     */
    public static Long getMessagesInCountPerSec(String topic) {
        Long value = 0L;
        for (JmxConnection conn : conns) {
            Long temp = conn.getMessagesInCountPerSec(topic);
            value += temp;
        }
        return value;
    }

    /**
     * 获取kafka集群中某个topic的tps
     *
     * @param topic topic名称
     * @return tps
     */
    public static Double getMessagesInTpsPerSec(String topic) {
        Double value = 0D;
        for (JmxConnection conn : conns) {
            Double temp = conn.getMessagesInTpsPerSec(topic);
            value += temp;
        }
        return value;
    }

    public static Map<Integer, Long> getEndOffset(String topicName) {
        Map<Integer, Long> map = new HashMap<>();
        for (JmxConnection conn : conns) {
            Map<Integer, Long> tmp = conn.getTopicEndOffset(topicName);
            if (tmp == null) {
                LOG.warn("get topic endoffset return null, topic {}", topicName);
                continue;
            }
            for (Integer parId : tmp.keySet()) {//change if bigger
                if (!map.containsKey(parId) || (map.containsKey(parId) && (tmp.get(parId) > map.get(parId)))) {
                    map.put(parId, tmp.get(parId));
                }
            }
        }
        return map;
    }
}
