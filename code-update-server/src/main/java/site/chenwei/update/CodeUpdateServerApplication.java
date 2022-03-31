package site.chenwei.update;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "site.chenwei.update.mapper")
public class CodeUpdateServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeUpdateServerApplication.class, args);
    }

}
