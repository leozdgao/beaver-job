package me.leozdgao.beaver.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Worker 应用启动器（Only for Test）
 * @author leozdgao
 */
@SpringBootApplication(scanBasePackages = {"me.leozdgao.beaver.agent.*"})
public class WorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }
}
