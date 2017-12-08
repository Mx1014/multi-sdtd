package com.rzt;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.rzt.websocket.serverendpoint.AlarmSituationServerEndpoint;
import com.rzt.websocket.serverendpoint.PersonnelTasksServerEndpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
@ServletComponentScan
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

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        //创建封装对象
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFeatures(SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue);
        return new HttpMessageConverters(converter);
    }
}