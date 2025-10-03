package com.minipay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class MinipayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinipayApplication.class, args);
    }

    @Bean
    public CommandLineRunner logSwaggerPaths(
            WebServerApplicationContext webServerAppCtx,
            Environment env,
            @Value("${springdoc.swagger-ui.path:/swagger-ui.html}") String swaggerUiPath
    ) {
        return args -> {
            try {
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                int port = webServerAppCtx.getWebServer().getPort();
                String contextPath = env.getProperty("server.servlet.context-path", "");

                String swaggerUrl = String.format("http://%s:%d%s%s", hostAddress, port, contextPath, swaggerUiPath);

                System.out.println("------------------------------------------------------");
                System.out.println("Swagger UI available at: " + swaggerUrl);
                System.out.println("------------------------------------------------------");
            } catch (Exception e) {
                log.info("Failed to determine Swagger URL: " + e.getMessage());
            }


        };
    }

}
