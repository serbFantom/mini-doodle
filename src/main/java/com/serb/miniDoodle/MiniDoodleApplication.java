package com.serb.miniDoodle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MiniDoodleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniDoodleApplication.class, args);
	}

}
