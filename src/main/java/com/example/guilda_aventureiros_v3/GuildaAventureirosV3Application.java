package com.example.guilda_aventureiros_v3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GuildaAventureirosV3Application {

	public static void main(String[] args) {
		SpringApplication.run(GuildaAventureirosV3Application.class, args);
	}

}
