package com.fox.api.configuration.datasource;

import com.fox.api.annotation.mapper.QuartzMapperConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 计划任务管理数据源
 * @author lusongsong
 */
@Configuration
@MapperScan(basePackages = {"com.fox"}, annotationClass = QuartzMapperConfig.class, sqlSessionFactoryRef = "quartzSqlSessionFactory")
public class QuartzDataSourceConfig extends AbstractDataSourceConfig {

    @Value("${spring.datasource.quartz.url}")
    private String url;

    @Value("${spring.datasource.quartz.username}")
    private String username;

    @Value("${spring.datasource.quartz.password}")
    private String password;

    @Value("${spring.datasource.quartz.driver-class-name}")
    private String driverClassName;

    @Value("${mybatis.quartz.mapper-location}")
    private String mapperLocation;

    @QuartzDataSource
    @Bean(name="quartzDataSource")
    public DataSource quartzDataSource() {
        return dataSourceFactory(driverClassName, url, username, password);
    }

    @Bean(name = "quartzSqlTemplate")
    public SqlSessionTemplate quartzSqlTemplate() throws Exception {
        return new SqlSessionTemplate((sqlSessionFactory(quartzDataSource(), mapperLocation)));
    }

    @Bean(name = "quartzSqlSessionFactory")
    public SqlSessionFactory quartzSqlSessionFactory() throws Exception {
        return this.sqlSessionFactory(this.quartzDataSource(), this.mapperLocation);
    }

    @Bean
    @Qualifier("quartzTransactionManager")
    public PlatformTransactionManager quartzTransactionManager() {
        return new DataSourceTransactionManager(quartzDataSource());
    }
}
