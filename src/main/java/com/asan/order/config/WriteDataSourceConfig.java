package com.asan.order.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.asan.order.repository",
    entityManagerFactoryRef = "writeEntityManagerFactory",
    transactionManagerRef = "writeTransactionManager")
public class WriteDataSourceConfig {

  @Primary
  @Bean(name = "writeDataSource")
  public DataSource writeDataSource(
      @Value("${app.datasource.write.url}") String url,
      @Value("${app.datasource.write.driver-class-name}") String driverClassName,
      @Value("${app.datasource.write.username}") String username,
      @Value("${app.datasource.write.password}") String password) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setUrl(url);
    dataSource.setDriverClassName(driverClassName);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return dataSource;
  }

  @Primary
  @Bean(name = "writeEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean writeEntityManagerFactory(
      @Qualifier("writeDataSource") DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setDataSource(dataSource);
    entityManagerFactory.setPackagesToScan("com.asan.order.entity");
    entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    entityManagerFactory.setJpaProperties(hibernateProperties());
    return entityManagerFactory;
  }

  @Primary
  @Bean(name = "writeTransactionManager")
  public PlatformTransactionManager writeTransactionManager(
      @Qualifier("writeEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory.getObject());
  }

  private Properties hibernateProperties() {
    Properties properties = new Properties();
    properties.put("hibernate.hbm2ddl.auto", "update");
    return properties;
  }
}
