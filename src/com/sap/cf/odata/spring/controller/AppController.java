package com.sap.cf.odata.spring.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import com.sap.cf.odata.spring.config.CloudConfig;
import com.sap.cf.odata.spring.context.JPAServiceFactory;

/**
 * Main class for this application. extends {@link SpringBootServletInitializer}
 * it making a web application for war deployment.
 *
 */

@SpringBootApplication(scanBasePackages = "com.sap.cf")
public class AppController extends SpringBootServletInitializer {

	// Main method gives control to Spring by invoking run on Spring Application.
	// This enables Spring to Bootstrap the application
	public static void main(String[] args) {
		SpringApplication.run(AppController.class, args);

	}

	// This method adds Configuration class for Spring Application Context builder
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(AppController.class, CloudConfig.class);
	}
}
