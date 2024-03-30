package com.lazy.crud.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lazy.crud.mybatis.AesTypeHandler;
import com.lazy.crud.service.DataDicService;
import com.lazy.crud.service.impl.DataDicServiceImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ：cy
 * @description：mybatis配置
 * @date ：2021/9/26 13:58
 */
@Configuration
public class YsMybatisAutoConfiguration {

    @Autowired
    CacheProperties cacheProperties;

    /**
     * mybatisplus拦截器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //添加分页功能
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(DataDicService.class)
    public DataDicService dataDicService(){
        return new DataDicServiceImpl();
    }

    /**
     * db异步线程池配置
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(80);
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("AsyncThread--");
        return executor;
    }

    /**
     * 内存缓存
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "caffeineCache")
    public Cache<Object, Object> caffeineCache(){
        String specification = cacheProperties.getCaffeine().getSpec();
        if (StringUtils.hasText(specification)) {
            return Caffeine.from(specification).build();
        }else {
            //默认
            return Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .maximumSize(10_000)
                    .build();
        }
    }

    @Bean
    @ConditionalOnMissingBean(TypeHandlerRegistry.class)
    public TypeHandlerRegistry typeHandlerRegistry(SqlSessionFactory sqlSessionFactory){
        TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
//        typeHandlerRegistry.register(AesTypeHandler.class);
        return typeHandlerRegistry;
    }

}
