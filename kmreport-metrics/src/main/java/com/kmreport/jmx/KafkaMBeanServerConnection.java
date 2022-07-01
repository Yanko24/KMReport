package com.kmreport.jmx;

import com.kmreport.model.BrokerIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafkaMBeanServerConnection {
    private static final Logger log = LoggerFactory.getLogger(KafkaMBeanServerConnection.class);

    private KafkaMBeanServerConnection() {
    }

    /**
     * 默认配置
     *
     * @return defaultEnvironment
     */
    private static Map<String, String> defaultJmxConnectionEnvironment() {
        Map<String, String> defaultEnvironment = new HashMap<>();
        defaultEnvironment.put("jmx.remote.x.request.waiting.timeout", "3000");
        defaultEnvironment.put("jmx.remote.x.notification.fetch.timeout", "3000");
        defaultEnvironment.put("sun.rmi.transport.connectionTimeout", "3000");
        defaultEnvironment.put("sun.rmi.transport.tcp.handshakeTimeout", "3000");
        defaultEnvironment.put("sun.rmi.transport.tcp.responseTimeout", "3000");
        return defaultEnvironment;
    }

    /**
     * 初始化MBeanServerConnection
     *
     * @param jmxHost jmx主机地址
     * @param jmxPort jmx端口号
     * @return {@link MBeanServerConnection}
     */
    private static MBeanServerConnection init(String jmxHost, Integer jmxPort) {
        String jmxURL = "service:jmx:rmi:///jndi/rmi://" + jmxHost + ":" + jmxPort + "/jmxrmi";
        log.info("init jmx, jmxUrl: {}, and begin to connect it", jmxURL);
        MBeanServerConnection connection = null;
        try {
            JMXServiceURL jmxServiceURL = new JMXServiceURL(jmxURL);
            Map<String, String> environment = new HashMap<>(defaultJmxConnectionEnvironment());
            JMXConnector connect = JMXConnectorFactory.connect(jmxServiceURL, environment);
            connection = connect.getMBeanServerConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 初始化集群的MBeanServerConnection连接
     *
     * @param brokerIdentities brokers
     * @return {@link MBeanServerConnection}
     */
    public static List<MBeanServerConnection> init(List<BrokerIdentity> brokerIdentities) {
        List<MBeanServerConnection> connections = new ArrayList<>();
        brokerIdentities.forEach(brokerIdentity -> {
            log.info("init jmxConnection [{}]", brokerIdentity);
            connections.add(init(brokerIdentity.getHost(), brokerIdentity.getJmxPort()));
        });
        return connections;
    }
}
