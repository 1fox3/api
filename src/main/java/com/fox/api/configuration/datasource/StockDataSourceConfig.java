package com.fox.api.configuration.datasource;

import com.fox.api.annotation.mapper.StockMapperConfig;
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
 * 股票信息数据源
 * @author lusongsong
 */
@Configuration
@MapperScan(basePackages = {"com.fox"}, annotationClass = StockMapperConfig.class, sqlSessionFactoryRef = "stockSqlSessionFactory")
public class StockDataSourceConfig extends AbstractDataSourceConfig {

    @Value("${spring.datasource.stock.url}")
    private String url;

    @Value("${spring.datasource.stock.username}")
    private String username;

    @Value("${spring.datasource.stock.password}")
    private String password;

    @Value("${spring.datasource.stock.driver-class-name}")
    private String driverClassName;

    @Value("${mybatis.stock.mapper-location}")
    private String mapperLocation;

    @Bean(name="stockDataSource")
    public DataSource stockDataSource() {
        return dataSourceFactory(driverClassName, url, username, password);
    }

    @Bean(name = "stockSqlTemplate")
    public SqlSessionTemplate stockSqlTemplate() throws Exception {
        return new SqlSessionTemplate((sqlSessionFactory(stockDataSource(), mapperLocation)));
    }

    @Bean(name = "stockSqlSessionFactory")
    public SqlSessionFactory stockSqlSessionFactory() throws Exception {
        return this.sqlSessionFactory(this.stockDataSource(), this.mapperLocation);
    }

    @Bean
    @Qualifier("stockTransactionManager")
    public PlatformTransactionManager stockTransactionManager() {
        return new DataSourceTransactionManager(stockDataSource());
    }
}
