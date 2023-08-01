package com.lazy.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ：cy
 * @description：多级缓存公共配置
 * 细化配置：cacheProperties caffeine&redis
 * 优先级 multi > caffeine/redis > default
 * @date ：2022/7/15 16:51
 */
@Data
@ConfigurationProperties(prefix = "spring.cache.multi")
public class YsMultiCacheProperties {

    /**
     * 缓存key的前缀,有配置则判断为使用
     */
    private String keyPrefix;

    /**
     * 缓存更新时通知其他节点的topic名称
     * 不能为空，配置后multi-cache才会生效
     */
    private String topic;

    /**
     * 是否保存null缓存值
     */
    private Boolean isAllowNullValues = true;
}
