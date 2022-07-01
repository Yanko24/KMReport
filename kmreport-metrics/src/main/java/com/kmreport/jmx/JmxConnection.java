package com.kmreport.jmx;

import com.kmreport.model.BrokerIdentity;
import com.kmreport.model.metrics.KafkaServerMetrics;
import com.kmreport.model.KafkaTopicMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JmxConnection {
    private static final Logger log = LoggerFactory.getLogger(JmxConnection.class);
    private MBeanServerConnection connection;
    private String jmxURL;
    private BrokerIdentity brokerIdentity;

    public JmxConnection(BrokerIdentity brokerIdentity) {
        this.brokerIdentity = brokerIdentity;
    }

    public boolean init() {
        jmxURL = "service:jmx:rmi:///jndi/rmi://" + brokerIdentity.getHost() + ":" + brokerIdentity.getJmxPort() + "/jmxrmi";
        log.info("init jmx, jmxUrl: {}, and begin to connect it", jmxURL);
        try {
            JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);
            JMXConnector connector = JMXConnectorFactory.connect(serviceURL, null);
            connection = connector.getMBeanServerConnection();
            if (connection == null) {
                log.error("get connection return null!");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取其他属性
     *
     * @param objName      obj名称
     * @param objAttribute obj属性
     * @return object
     */
    private Object getAttribute(String objName, String objAttribute) {
        ObjectName objectName;
        try {
            objectName = new ObjectName(objName);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            return null;
        }
        return getAttribute(objectName, objAttribute);
    }

    /**
     * 获取其他属性
     *
     * @param objName      obj名称
     * @param objAttribute obj属性
     * @return object
     */
    private Object getAttribute(ObjectName objName, String objAttribute) {
        if (connection == null) {
            log.error("jmx connection is null");
            return null;
        }
        try {
            return connection.getAttribute(objName, objAttribute);
        } catch (MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException |
                 IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取kafka节点的metrics

    /**
     * 获取kafka节点消息入站的byte大小
     *
     * @return 入站消息byte大小
     */
    public Long getByteInPerSec() {
        Object attribute = getAttribute(KafkaServerMetrics.BYTES_IN_PER_SEC, "Count");
        if (attribute != null) {
            return Long.parseLong(attribute.toString());
        }
        return 0L;
    }

    /**
     * 获取kafka节点消息出站的byte大小
     *
     * @return 出站消息byte大小
     */
    public Long getByteOutPerSec() {
        Object attribute = getAttribute(KafkaServerMetrics.BYTES_OUT_PER_SEC, "Count");
        if (attribute != null) {
            return Long.parseLong(attribute.toString());
        }
        return 0L;
    }

    /**
     * 获取kafka节点的消息入站总数
     *
     * @return 消息数量
     */
    public Long getMessagesInCountPerSec() {
        Object attribute = getAttribute(KafkaServerMetrics.MESSAGES_IN_PER_SEC, "Count");
        if (attribute != null) {
            return Long.parseLong(attribute.toString());
        }
        return 0L;
    }

    /**
     * 获取kafka节点的tps
     *
     * @return tps
     */
    public Double getMessagesInTpsPerSec() {
        Object attribute = getAttribute(KafkaServerMetrics.MESSAGES_IN_PER_SEC, "OneMinuteRate");
        if (attribute != null) {
            return Double.parseDouble(attribute.toString());
        }
        return 0D;
    }

    // 获取kafka中某个topic的metrics

    /**
     * 获取kafka节点某个topic消息入站的byte大小
     *
     * @param topic topic名称
     * @return 入站消息byte大小
     */
    public Long getByteInPerSec(String topic) {
        Object attribute = getAttribute(KafkaTopicMetrics.addTopic(KafkaTopicMetrics.BYTES_IN_PER_SEC, topic), "Count");
        if (attribute != null) {
            return Long.parseLong(attribute.toString());
        }
        return 0L;
    }

    /**
     * 获取kafka节点某个topic消息出站的byte大小
     *
     * @param topic topic名称
     * @return 出站消息byte大小
     */
    public Long getByteOutPerSec(String topic) {
        Object attribute = getAttribute(KafkaTopicMetrics.addTopic(KafkaTopicMetrics.BYTES_OUT_PER_SEC, topic),
                "Count");
        if (attribute != null) {
            return Long.parseLong(attribute.toString());
        }
        return 0L;
    }

    /**
     * 获取kafka节点某个topic的消息入站总数
     *
     * @param topic topic名称
     * @return 消息数量
     */
    public Long getMessagesInCountPerSec(String topic) {
        Object attribute = getAttribute(KafkaTopicMetrics.addTopic(KafkaTopicMetrics.MESSAGES_IN_PER_SEC, topic),
                "Count");
        if (attribute != null) {
            return Long.parseLong(attribute.toString());
        }
        return 0L;
    }

    /**
     * 获取kafka节点某个topic的tps
     *
     * @param topic topic名称
     * @return tps
     */
    public Double getMessagesInTpsPerSec(String topic) {
        Object attribute = getAttribute(KafkaTopicMetrics.addTopic(KafkaTopicMetrics.MESSAGES_IN_PER_SEC, topic),
                "OneMinuteRate");
        if (attribute != null) {
            return Double.parseDouble(attribute.toString());
        }
        return 0D;
    }

    /**
     * 获取某个topic的的每个partition所对应的logSize(即offset)
     *
     * @param topic topic名称
     * @return map
     */
    public Map<Integer, Long> getLogEndOffset(String topic) {
        ObjectName objectName = null;
        Set<ObjectName> objectNames = null;
        try {
            objectName = new ObjectName(KafkaTopicMetrics.LOG_END_OF_OFFSET);
            objectNames = connection.queryNames(objectName, null);
        } catch (MalformedObjectNameException | IOException e) {
            e.printStackTrace();
        }
        Map<Integer, Long> map = new HashMap<>();
        assert objectNames != null;
        for (ObjectName objName : objectNames) {
            Integer partition = Integer.parseInt(objName.getKeyProperty("partition"));
            Object attribute = getAttribute(objName, "Value");
            if (attribute != null) {
                map.put(partition, Long.parseLong(attribute.toString()));
            }
        }
        return map;
    }

    private int getParId(ObjectName objName) {
        String s = objName.getKeyProperty("partition");
        return Integer.parseInt(s);
    }
}