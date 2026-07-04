package com.desafiogruporevise.frete_teste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FreteTesteApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreteTesteApplication.class, args);
	}

}
