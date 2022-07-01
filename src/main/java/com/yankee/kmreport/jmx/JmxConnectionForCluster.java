package com.yankee.kmreport.jmx;

import com.yankee.kmreport.model.BrokerIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JmxConnectionForCluster {
    private static final Logger log = LoggerFactory.getLogger(JmxConnectionForCluster.class);
    private static final List<JmxConnection> connections = new ArrayList<>();

    /**
     * 初始化JmxConnection连接
     *
     * @param brokerIdentities      brokers
     */
    public static Boolean init(List<BrokerIdentity> brokerIdentities) {
        for (BrokerIdentity brokerIdentity : brokerIdentities) {
            log.info("init jmxConnection [{}]", brokerIdentity);
            JmxConnection connection = new JmxConnection(brokerIdentity);
            boolean init = connection.init();
            if (!init) {
                log.error("init jmxConnection error");
                return false;
            }
            connections.add(connection);
        }
        return true;
    }

    /**
     * 获取kafka集群某个topic消息入站的byte大小
     *
     * @param topic topic名称
     * @return 入站消息byte大小
     */
    public static Long getByteInPerSec(String topic) {
        Long value = 0L;
        for (JmxConnection connection : connections) {
            Long temp = connection.getByteInPerSec(topic);
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
        for (JmxConnection connection : connections) {
            Long temp = connection.getByteOutPerSec(topic);
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
        for (JmxConnection connection : connections) {
            Long temp = connection.getMessagesInCountPerSec(topic);
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
        for (JmxConnection connection : connections) {
            Double temp = connection.getMessagesInTpsPerSec(topic);
            value += temp;
        }
        return value;
    }

    /**
     * 获取kakfa集群下topic的的每个partition所对应的logSize(即offset)，集群中的这个值取kafka节点中的最大值
     *
     * @param topic topic名称
     * @return map
     */
    public static Map<Integer, Long> getLogEndOffset(String topic) {
        Map<Integer, Long> map = new HashMap<>();
        for (JmxConnection connection : connections) {
            Map<Integer, Long> temp = connection.getLogEndOffset(topic);
            if (temp == null) {
                log.warn("get topic logendoffset return null, topic {}", topic);
                continue;
            }
            for (Integer partition : temp.keySet()) {
                if (!map.containsKey(partition) || (map.containsKey(partition) && (temp.get(partition) > map.get(partition)))) {
                    map.put(partition, temp.get(partition));
                }
            }
        }
        return map;
    }
}
