package com.fox.api.common.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

public abstract class AbstractDataSourceConfig {

    protected SqlSessionFactory sqlSessionFactory(DataSource dataSource, String mapperLocation) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        Resource[] resource= resourceResolver.getResources(mapperLocation);
        factoryBean.setMapperLocations(resource);
        return factoryBean.getObject();
    }

    protected DataSource dataSourceFactory(String driveClassName, String url, String userName, String password){
        DruidDataSource datasource = new DruidDataSource();
        datasource.setDriverClassName(driveClassName);
        datasource.setUrl(url);
        datasource.setUsername(userName);
        datasource.setPassword(password);
        datasource.setMaxActive(20);
        datasource.setInitialSize(20);
        return datasource;
    }
}
