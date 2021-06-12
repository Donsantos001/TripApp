package ie.cct.cbwa.cashowcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ie.cct*")
public class CaShowcaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaShowcaseApplication.class, args);
	}
}
