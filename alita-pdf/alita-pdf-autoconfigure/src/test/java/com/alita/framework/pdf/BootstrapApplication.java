package com.alita.framework.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.alita.framework"})
public class BootstrapApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BootstrapApplication.class);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }
}
