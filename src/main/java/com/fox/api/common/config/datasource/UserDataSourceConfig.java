package com.fox.api.common.config.datasource;

import com.fox.api.common.config.mapper.UserMapperConfig;
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

@Configuration
@MapperScan(basePackages = {"com.fox"}, annotationClass = UserMapperConfig.class, sqlSessionFactoryRef = "userSqlSessionFactory")
public class UserDataSourceConfig extends AbstractDataSourceConfig {

    @Value("${spring.datasource.user.url}")
    private String url;

    @Value("${spring.datasource.user.username}")
    private String username;

    @Value("${spring.datasource.user.password}")
    private String password;

    @Value("${spring.datasource.user.driver-class-name}")
    private String driverClassName;

    @Value("${mybatis.user.mapper-location}")
    private String mapperLocation;

    @Bean(name="userDataSource")
    public DataSource userDataSource() {
        return dataSourceFactory(driverClassName, url, username, password);
    }

    @Bean(name = "userSqlTemplate")
    public SqlSessionTemplate userSqlTemplate() throws Exception {
        return new SqlSessionTemplate((sqlSessionFactory(userDataSource(), mapperLocation)));
    }

    @Bean(name = "userSqlSessionFactory")
    public SqlSessionFactory userSqlSessionFactory() throws Exception {
        return this.sqlSessionFactory(this.userDataSource(), this.mapperLocation);
    }

    @Bean
    @Qualifier("userTransactionManager")
    public PlatformTransactionManager userTransactionManager() {
        return new DataSourceTransactionManager(userDataSource());
    }
}
