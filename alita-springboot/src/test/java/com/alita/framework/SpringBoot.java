package com.alita.framework;

import com.alita.framework.platform.banner.PlatformBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-04-24 21:13:15
 */
@SpringBootApplication(scanBasePackages = {"com.alita.framework.platform"})
public class SpringBoot {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringBoot.class);
        application.setBanner(new PlatformBanner());
        application.run(args);
    }
}
