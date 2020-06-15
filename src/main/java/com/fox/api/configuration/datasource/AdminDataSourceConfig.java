package com.fox.api.configuration.datasource;

import com.fox.api.annotation.mapper.AdminMapperConfig;
import com.fox.api.annotation.mapper.UserMapperConfig;
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
 * 管理信息相关数据源
 * @author lusongsong
 */
@Configuration
@MapperScan(basePackages = {"com.fox"}, annotationClass = AdminMapperConfig.class, sqlSessionFactoryRef = "adminSqlSessionFactory")
public class AdminDataSourceConfig extends AbstractDataSourceConfig {

    @Value("${spring.datasource.admin.url}")
    private String url;

    @Value("${spring.datasource.admin.username}")
    private String username;

    @Value("${spring.datasource.admin.password}")
    private String password;

    @Value("${spring.datasource.admin.driver-class-name}")
    private String driverClassName;

    @Value("${mybatis.admin.mapper-location}")
    private String mapperLocation;

    @Bean(name="adminDataSource")
    public DataSource adminDataSource() {
        return dataSourceFactory(driverClassName, url, username, password);
    }

    @Bean(name = "adminSqlTemplate")
    public SqlSessionTemplate adminSqlTemplate() throws Exception {
        return new SqlSessionTemplate((sqlSessionFactory(adminDataSource(), mapperLocation)));
    }

    @Bean(name = "adminSqlSessionFactory")
    public SqlSessionFactory adminSqlSessionFactory() throws Exception {
        return this.sqlSessionFactory(this.adminDataSource(), this.mapperLocation);
    }

    @Bean
    @Qualifier("adminTransactionManager")
    public PlatformTransactionManager adminTransactionManager() {
        return new DataSourceTransactionManager(adminDataSource());
    }
}
