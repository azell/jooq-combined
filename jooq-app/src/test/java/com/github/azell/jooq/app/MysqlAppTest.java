package com.github.azell.jooq.app;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_19;
import static java.util.Locale.ENGLISH;
import static org.jooq.SQLDialect.MYSQL_5_7;

import com.github.azell.jooq.transactions.JooqFactory;
import com.github.azell.jooq.transactions.JooqTransactionFactory;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import java.io.IOException;
import java.net.ServerSocket;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class MysqlAppTest extends AppTest {
  @Configuration
  @EnableTransactionManagement
  static class ContextConfiguration {
    @Bean
    public App app(JooqFactory factory) {
      return new App(factory);
    }

    @Bean
    public DataSource dataSource(MysqldConfig config, EmbeddedMysql mysqld) {
      DriverManagerDataSource ds = new DriverManagerDataSource();

      ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
      ds.setUrl(
          String.format(
              ENGLISH,
              "jdbc:mysql://localhost:%d/mydb?useCompression=true&useSSL=false",
              config.getPort()));
      ds.setUsername("root");
      ds.setPassword("");

      return ds;
    }

    @Bean(destroyMethod = "stop")
    public EmbeddedMysql mysqld(MysqldConfig config) {
      return anEmbeddedMysql(config)
          .addSchema("mydb", classPathScript("db/init_schema.sql"))
          .start();
    }

    @Bean
    public MysqldConfig config() throws IOException {
      int port;

      try (ServerSocket socket = new ServerSocket(0)) {
        socket.setReuseAddress(true);
        port = socket.getLocalPort();
      }

      return aMysqldConfig(v5_7_19).withPort(port).build();
    }

    @Bean
    public JooqFactory jooqFactory(DataSource ds) {
      return new JooqTransactionFactory(ds, MYSQL_5_7);
    }

    @Bean
    public SpringLiquibase springLiquibase(DataSource ds) {
      return load(ds);
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource ds) {
      return new DataSourceTransactionManager(ds);
    }
  }
}
