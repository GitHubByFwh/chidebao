package com.itfwh.spm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author:Fwh
 * @date 2022/4/14 20:13
 * @ClassName SupermarketApplication
 * @Description
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan//拦截器生效
@EnableTransactionManagement//开启事物注解支持
@EnableCaching//开启SpringCache注解方式缓存功能
public class ChiDeBaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChiDeBaoApplication.class,args);
        log.info("项目启动成功");
    }
}