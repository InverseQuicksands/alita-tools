package com.alita.framework.platform.autoconfigure.data.myatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Indexed;


@Configuration(proxyBeanMethods = false)
@MapperScan(basePackages = {"com.aeolus.framework.platform.dao", "org.quicksand.caterpiller.dao"})
@Indexed
public class AeolusMybatisAutoConfiguration {

}
