package com.yankee.kmreport.report;

import cn.hutool.core.util.RuntimeUtil;
import com.yankee.kmreport.model.KafkaMetricsInfo;
import com.yankee.kmreport.util.PropertiesUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MetricsReport implements Runnable {

    private static final String fileName = "jmx.properties";

    private Map<String, KafkaMetricsInfo> preKafkaMetricsInfo;

    private Map<String, KafkaMetricsInfo> currentKafkaMetricsInfo;

    private String topic;

    public MetricsReport(String topic, Map<String, KafkaMetricsInfo> preKafkaMetricsInfo,
                         Map<String, KafkaMetricsInfo> currentKafkaMetricsInfo) {
        this.preKafkaMetricsInfo = preKafkaMetricsInfo;
        this.currentKafkaMetricsInfo = currentKafkaMetricsInfo;
        this.topic = topic;
    }

    @Override
    public void run() {
        // 读取配置文件
        PropertiesUtil propertiesUtil = PropertiesUtil.getInstance(fileName);
        // 获取要监控的指标
        String metricsList = propertiesUtil.getProperty("metrics");
        String[] metrics = metricsList.split(",");
        // 获取循环间隔
        long period = propertiesUtil.getLong("period");
        // 获取监控未变动次数
        int count = propertiesUtil.getInteger("count");
        for (String metric : metrics) {
            KafkaMetricsInfo preKafkaMetrics = preKafkaMetricsInfo.getOrDefault(metric,
                    KafkaMetricsInfo.builder().metricsValue(0).build());
            KafkaMetricsInfo currentkafkaMetrics = currentKafkaMetricsInfo.get(metric);

            // 获取上一次指标的统计次数
            Integer metricsCount = preKafkaMetrics.getCount();

            // 判断count是否大于等于10次，如果是则触发告警
            if (metricsCount >= count) {
                // 调用告警脚本
                String messageHome = propertiesUtil.getProperty("message.home");
                String mobile = propertiesUtil.getProperty("mobile");
                String message = propertiesUtil.getProperty("message").replace("${topic}", topic)
                        .replace("${times}", String.valueOf(period * count))
                        .replace("$metric", metric);
                RuntimeUtil.execForStr(StandardCharsets.UTF_8, "sh", messageHome, mobile, message);
                metricsCount = 0;
            }

            // 判断是否和上一次相等
            // boolean change = preKafkaMetrics.getMetricsValue().equals(currentkafkaMetrics.getMetricsValue());
            // if (change) {
            //     currentkafkaMetrics.setCount(++metricsCount);
            // }
        }
    }
}
