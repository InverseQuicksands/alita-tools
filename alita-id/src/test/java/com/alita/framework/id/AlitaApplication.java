package com.alita.framework.id;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <br>
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 00:14
 **/

@SpringBootApplication(scanBasePackages = {"com.alita.framework.id","com.alita.framework.jdbc"})
public class AlitaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlitaApplication.class, args);
    }
}
