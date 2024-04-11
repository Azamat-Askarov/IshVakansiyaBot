package uz.code.ishvakansiyabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   /**   CRON uchun */
public class IshVakansiyaBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(IshVakansiyaBotApplication.class, args);
    }
}

