package com.sap.cf.odata.spring.config;



import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;



/**
 * See CloudDatabaseConfig for more detailed comments.
 *
 * Provides a convenient repository, based on JPA (EntityManager, TransactionManager).
 */
@Configuration
@EnableJpaRepositories
@ComponentScan(basePackages = "com.sap.cf")
public class h2dbConfig {

    /**
     * Creates DataSource for an embedded Database (H2).
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    }

    /**
     * Based on a DataSource, provides EntityManager (JPA)
     */
    @Bean(name = "entityManagerFactory")
    public EntityManagerFactory entityManagerFactory() {
//        return EntityManagerFactoryProvider.get(dataSource, Advertisement.class.getPackage().getName());
    	LocalContainerEntityManagerFactoryBean springEMF = new LocalContainerEntityManagerFactoryBean();
//    	
    	springEMF.setPersistenceUnitName("ODataSpring");
//    	
    	springEMF.setPackagesToScan("com.sap.cf");
    	springEMF.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
		springEMF.setDataSource(dataSource());
		springEMF.afterPropertiesSet();
		return springEMF.getObject();
		
		
		
		

    }

//    /**
//     * Based on a EntityManager, provides TransactionManager (JPA)
//     */
//    @Bean(name = "transactionManager")
//    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//    
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