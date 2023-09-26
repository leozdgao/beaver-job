package me.leozdgao.beaver.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Worker 应用启动器（Only for Test）
 * @author leozdgao
 */
@RestController
@SpringBootApplication(scanBasePackages = {"me.leozdgao.beaver.agent.*"})
public class WorkerApplication {
    @GetMapping("/health")
    public String health() {
        return "success";
    }

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }
}
