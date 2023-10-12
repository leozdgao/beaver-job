package me.leozdgao.beaver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhendong.gzd
 */
@RestController
@SpringBootApplication(scanBasePackages = {"me.leozdgao.beaver.adaptor.*"})
public class Application {
    @GetMapping("/health")
    public String health() {
        return "success";
    }

    public static void main(String[] args) throws Exception {
        // 正常而言这里应该是启动 Web 应用
        SpringApplication.run(Application.class, args);
    }
}