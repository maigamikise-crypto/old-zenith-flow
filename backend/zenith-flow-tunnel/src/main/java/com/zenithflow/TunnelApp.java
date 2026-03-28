package com.zenithflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * renren-api
 *
 *
 */
@SpringBootApplication
public class TunnelApp extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TunnelApp.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TunnelApp.class);
	}
}
