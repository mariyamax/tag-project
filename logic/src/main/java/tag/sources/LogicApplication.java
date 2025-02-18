package tag.sources;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogicApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LogicApplication.class);
        application.run(args);
    }
}