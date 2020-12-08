package com.sap.cf.odata.spring.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.service.relational.DataSourceConfig;
import org.springframework.cloud.util.UriInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

import com.sap.hana.cloud.hcp.service.common.HANAServiceInfo;
//import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories
@Profile("cloud")
@ComponentScan(basePackages = "com.sap.cf")
public class CloudConfig extends AbstractCloudConfig {

	private static final String HANA_SVC = "hana-schema-svc";

	private static final Logger LOG = LoggerFactory.getLogger(CloudConfig.class);

	/**
	 * Create dataSource bean from SAP CF
	 * 
	 * @return dataSource dataSoruce created from HANA Service.
	 */
	@Bean
	@Primary
	public DataSource dataSource() {
		DataSource dataSource = null;
		try {
		    List<String> dataSourceNames = Arrays.asList("BasicDbcpPooledDataSourceCreator",
	                "TomcatJdbcPooledDataSourceCreator", "HikariCpPooledDataSourceCreator",
	                "TomcatDbcpPooledDataSourceCreator");
	        DataSourceConfig dbConfig = new DataSourceConfig(dataSourceNames);
			
		dataSource = connectionFactory().dataSource(dbConfig);
		} catch (CloudException ex) {
			LOG.error(" ", ex);
		}
		return dataSource;
	}
//	@Bean
//	@Profile("cloud")
//	public DataSource dataSource() {
//
//	    HANAServiceInfo serviceInfo = (HANAServiceInfo) cloud().getServiceInfo("hanaservice2");
//
//	    String host = serviceInfo.getHost();
//	    int port = serviceInfo.getPort();
//
//	    String username = serviceInfo.getUserName();
//	    String password = serviceInfo.getPassword();
//	    String schema = serviceInfo.getUserName(); // The schemaname matches the username
//
//	    String url = new UriInfo("jdbc:sap", host, port, null, null, null,
//	            "currentschema=" + schema + "&encrypt=true&validateCertificate=true").toString();
//
//	    return DataSourceBuilder.create().type(HikariDataSource.class)
//	            .driverClassName(com.sap.db.jdbc.Driver.class.getName())
//	            .url(url)
//	            .username(username)
//	            .password(password)
//	            .build();
//		
//	}

	/**
	 * Create Eclipselink EMF from the dataSource bean. JPAvendor and datasource
	 * will be set here. rest will be taken from persistence.xml
	 * 
	 * @return EntityManagerFactory
	 */
	@Bean
	public EntityManagerFactory entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean springEMF = new LocalContainerEntityManagerFactoryBean();
		springEMF.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
		springEMF.setDataSource(dataSource());
		springEMF.afterPropertiesSet();
		return springEMF.getObject();

	}


	/**
	 * Registers OData servlet bean with Spring Application context to handle
	 * ODataRequests.
	 * 
	 * @return
	 */
	@Bean
	public ServletRegistrationBean odataServlet() {

		ServletRegistrationBean odataServRegstration = new ServletRegistrationBean(new CXFNonSpringJaxrsServlet(),
				"/odata.svc/*");
		Map<String, String> initParameters = new HashMap<>();
		initParameters.put("javax.ws.rs.Application", "org.apache.olingo.odata2.core.rest.app.ODataApplication");
		initParameters.put("org.apache.olingo.odata2.service.factory",
				"com.sap.cf.odata.spring.context.JPAServiceFactory");
		odataServRegstration.setInitParameters(initParameters);

		return odataServRegstration;

	}

}
