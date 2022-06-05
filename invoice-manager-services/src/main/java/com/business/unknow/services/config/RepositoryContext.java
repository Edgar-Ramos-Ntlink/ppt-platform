package com.business.unknow.services.config;

import com.business.unknow.services.config.properties.InvoiceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
public class RepositoryContext {

  @Autowired private InvoiceConfig cloud;

  @Bean(name = "invoiceDatasource")
  public HikariDataSource cloudrdbmsDatasource() {

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(cloud.getDataSourceUrl());
    config.setUsername(cloud.getDataSourceUser());
    config.setPassword(cloud.getDataSourcePass());
    config.setMaximumPoolSize(cloud.getMaxiumPoolSize());
    config.setDriverClassName(cloud.getDataSourceClassName());
    return new HikariDataSource(config);
  }

  @Bean(name = "invoiceManagerTemplate")
  @Autowired
  public JdbcTemplate associateEvaluationLogTemplate(
      @Qualifier("invoiceDatasource") DataSource dsSlave) {
    return new JdbcTemplate(dsSlave);
  }
}
