package com.rzt;

import com.rzt.websocket.serverendpoint.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 统计服务
 */
@ServletComponentScan
@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
@EnableEurekaClient
@EnableFeignClients
@EnableScheduling
@EnableWebSocket

public class CensusServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CensusServerApplication.class, args);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * 告警情况展示WebSocket
     *
     * @return
     */
    @Bean
    public AlarmSituationServerEndpoint AlarmSituation() {
        return new AlarmSituationServerEndpoint();
    }

    /**
     * 任务人员统计WebSocket
     *
     * @return
     */
    @Bean
    public PersonnelTasksServerEndpoint PersonnelTasks() {
        return new PersonnelTasksServerEndpoint();
    }

    /**
     * 地图展示
     *
     * @return
     */
    @Bean
    public MapServerEndpoint Map() {
        return new MapServerEndpoint();
    }

    /**
     * 隐患展示
     *
     * @return
     */
    @Bean
    public historyServerEndpoint History() {
        return new historyServerEndpoint();
    }

    /**
     * 第二版
     *
     * @return
     */
    @Bean
    public ListDataServerEndpoint ListData() {
        return new ListDataServerEndpoint();
    }

    /**
     * 一键告警
     * @return
     */
    @Bean
    public warningMonitorServerEndpoint warningMonitor() {
        return new warningMonitorServerEndpoint();
    }
}