package com.fox.api.configuration.datasource;

import com.fox.api.annotation.mapper.ApiMapperConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * api相关数据源
 * @author lusongsong
 */
@Configuration
@MapperScan(basePackages = {"com.fox"}, annotationClass = ApiMapperConfig.class, sqlSessionFactoryRef = "apiSqlSessionFactory")
public class ApiDataSourceConfig extends AbstractDataSourceConfig {

    @Value("${spring.datasource.api.url}")
    private String url;

    @Value("${spring.datasource.api.username}")
    private String username;

    @Value("${spring.datasource.api.password}")
    private String password;

    @Value("${spring.datasource.api.driver-class-name}")
    private String driverClassName;

    @Value("${mybatis.api.mapper-location}")
    private String mapperLocation;

    @Bean(name="apiDataSource")
    public DataSource apiDataSource() {
        return dataSourceFactory(driverClassName, url, username, password);
    }

    @Bean(name = "apiSqlTemplate")
    public SqlSessionTemplate apiSqlTemplate() throws Exception {
        return new SqlSessionTemplate((sqlSessionFactory(apiDataSource(), mapperLocation)));
    }

    @Bean(name = "apiSqlSessionFactory")
    public SqlSessionFactory apiSqlSessionFactory() throws Exception {
        return this.sqlSessionFactory(this.apiDataSource(), this.mapperLocation);
    }

    @Bean
    @Qualifier("apiTransactionManager")
    public PlatformTransactionManager apiTransactionManager() {
        return new DataSourceTransactionManager(apiDataSource());
    }
}
