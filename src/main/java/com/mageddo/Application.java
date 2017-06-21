package com.mageddo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableRetry
@EnableCaching
@EnableScheduling
public class Application {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NetworkService pingService;

//    @Scheduled(fixedDelay = 2000)
    public void ping(){
        try{
            final String ping = pingService.ping("google.com");
            logger.error("m=ping, status=suc, ping={}", ping);
        }catch (Exception e){
            logger.error("m=ping, status=err, ping={}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void resolvname(){
        try{
            final String answer = pingService.resolveName("google.com");
            logger.error("status=suc, answer={}", answer);
        }catch (Exception e){
            logger.error("status=err, ping={}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
