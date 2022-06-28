package com.yankee.kmreport.jmx;

import com.yankee.kmreport.bean.KafkaMetrics;
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
    private static final Logger LOG = LoggerFactory.getLogger(JmxConnection.class);
    private MBeanServerConnection conn;
    private String jmxURL;
    private String jmxBroker;
    private boolean newKafkaVersion = false;

    public JmxConnection(String jmxBroker, Boolean newKafkaVersion) {
        this.jmxBroker = jmxBroker;
        this.newKafkaVersion = newKafkaVersion;
    }

    public boolean init() {
        jmxURL = "service:jmx:rmi:///jndi/rmi://" + jmxBroker + "/jmxrmi";
        LOG.info("init jmx, jmxUrl: {}, and begin to connect it", jmxURL);
        try {
            JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);
            JMXConnector connector = JMXConnectorFactory.connect(serviceURL, null);
            conn = connector.getMBeanServerConnection();
            if (conn == null) {
                LOG.error("get connection return null!");
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
     * @param kafkaMetrics jmxMetrics枚举类
     * @return object
     */
    private Object getAttribute(KafkaMetrics kafkaMetrics) {
        String objName = kafkaMetrics.getObjName();
        String objAttribute = kafkaMetrics.getObjAttribute();
        return getAttribute(objName, objAttribute);
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
        if (conn == null) {
            LOG.error("jmx connection is null");
            return null;
        }
        try {
            return conn.getAttribute(objName, objAttribute);
        } catch (MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException |
                 IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取kafka的partition总数
     *
     * @return partition总数
     */
    public Integer getPartitionCount() {
        Object attribute = getAttribute(KafkaMetrics.PARTITION_COUNT);
        if (attribute != null) {
            return (Integer) attribute;
        }
        return 0;
    }

    /**
     * 获取kafka节点消息入站的byte大小
     *
     * @return 入站消息byte大小
     */
    public Long getByteInPerSec() {
        Object attribute = getAttribute(KafkaMetrics.BYTES_IN_PER_SEC);
        if (attribute != null) {
            return (Long) attribute;
        }
        return 0L;
    }

    /**
     * 获取kafka节点某个topic消息入站的byte大小
     *
     * @param topic topic名称
     * @return 入站消息byte大小
     */
    public Long getByteInPerSec(String topic) {
        Object attribute = getAttribute(KafkaMetrics.BYTES_IN_PER_SEC.getObjName() + ",topic=" + topic,
                KafkaMetrics.BYTES_IN_PER_SEC.getObjAttribute());
        if (attribute != null) {
            return (Long) attribute;
        }
        return 0L;
    }

    /**
     * 获取kafka节点消息出站的byte大小
     *
     * @return 出站消息byte大小
     */
    public Long getByteOutPerSec() {
        Object attribute = getAttribute(KafkaMetrics.BYTES_OUT_PER_SEC);
        if (attribute != null) {
            return (Long) attribute;
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
        Object attribute = getAttribute(KafkaMetrics.BYTES_OUT_PER_SEC.getObjName() + ",topic=" + topic,
                KafkaMetrics.BYTES_OUT_PER_SEC.getObjAttribute());
        if (attribute != null) {
            return (Long) attribute;
        }
        return 0L;
    }

    /**
     * 获取kafka节点的消息入站总数
     *
     * @return 消息数量
     */
    public Long getMessagesInCountPerSec() {
        Object attribute = getAttribute(KafkaMetrics.MESSAGES_IN_COUNT_PER_SEC);
        if (attribute != null) {
            return (Long) attribute;
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
        Object attribute = getAttribute(KafkaMetrics.MESSAGES_IN_COUNT_PER_SEC.getObjName() + ",topic=" + topic,
                KafkaMetrics.MESSAGES_IN_COUNT_PER_SEC.getObjAttribute());
        if (attribute != null) {
            return (Long) attribute;
        }
        return 0L;
    }

    /**
     * 获取kafka节点的tps
     *
     * @return tps
     */
    public Double getMessagesInTpsPerSec() {
        Object attribute = getAttribute(KafkaMetrics.MESSAGES_IN_TPS_PER_SEC);
        if (attribute != null) {
            return (Double) attribute;
        }
        return 0D;
    }

    /**
     * 获取kafka节点某个topic的tps
     *
     * @param topic topic名称
     * @return tps
     */
    public Double getMessagesInTpsPerSec(String topic) {
        Object attribute = getAttribute(KafkaMetrics.MESSAGES_IN_TPS_PER_SEC.getObjName() + ",topic=" + topic,
                KafkaMetrics.MESSAGES_IN_TPS_PER_SEC.getObjAttribute());
        if (attribute != null) {
            return (Double) attribute;
        }
        return 0D;
    }

    /**
     * 获取某个topic的的每个partition所对应的logSize(即offset)
     *
     * @param topic topic名称
     * @return map
     */
    public Map<Integer, Long> getTopicEndOffset(String topic) {
        Set<ObjectName> objs = getEndOffsetObjects(topic);
        if (objs == null) {
            return null;
        }
        Map<Integer, Long> map = new HashMap<>();
        for (ObjectName objName : objs) {
            int partId = getParId(objName);
            Object val = getAttribute(objName, "Value");
            if (val != null) {
                map.put(partId, (Long) val);
            }
        }
        return map;
    }

    private int getParId(ObjectName objName) {
        if (newKafkaVersion) {
            String s = objName.getKeyProperty("partition");
            return Integer.parseInt(s);
        } else {
            String s = objName.getKeyProperty("name");
            int to = s.lastIndexOf("-LogEndOffset");
            String s1 = s.substring(0, to);
            int from = s1.lastIndexOf("-") + 1;
            String ss = s.substring(from, to);
            return Integer.parseInt(ss);
        }
    }

    private Set<ObjectName> getEndOffsetObjects(String topicName) {
        String objectName;
        if (newKafkaVersion) {
            objectName = "kafka.log:type=Log,name=LogEndOffset,topic=" + topicName + ",partition=*";
        } else {
            objectName = "\"kafka.log\":type=\"Log\",name=\"" + topicName + "-*-LogEndOffset\"";
        }
        ObjectName objName = null;
        Set<ObjectName> objectNames = null;
        try {
            objName = new ObjectName(objectName);
            objectNames = conn.queryNames(objName, null);
        } catch (MalformedObjectNameException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return objectNames;
    }
}