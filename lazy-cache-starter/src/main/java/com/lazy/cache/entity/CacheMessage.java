package com.lazy.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ：cy
 * @description：缓存更新推送消息
 * @date ：2022/7/18 16:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    String name;

    Object key;
}
