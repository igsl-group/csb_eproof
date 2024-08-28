package com.hkgov.csb.eproof;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ServletComponentScan
public class CsbEproofApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(CsbEproofApplication.class, args);
	}

		@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(CsbEproofApplication.class);
	}

}
