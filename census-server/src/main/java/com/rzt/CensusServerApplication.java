package com.rzt;

import com.rzt.websocket.PushServerEndpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

    @Bean
    public PushServerEndpoint chatRoomServerEndpoint() {
        return new PushServerEndpoint();
    }
}