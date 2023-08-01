package com.lazy.cache.support;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author cy
 * @Date 2023/6/3 11:34
 * @Description: SpEL工具
 */
@Component
public class YsCacheExpressionEvaluator {

    /**
     * spelCache 缓存
     */
    private final Map<String, String> spelCache = new ConcurrentHashMap<>(64);

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 获取cache Key
     * @param name
     * @param methodContext
     * @return
     */
    public String getValUnCache(String name, MethodBasedEvaluationContext methodContext){
        if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
            return null;
        }
        Object val = parser.parseExpression(name).getValue(methodContext);
        if (val != null && org.apache.commons.lang3.StringUtils.isNotBlank(val.toString())){
            return val.toString();
        }
        return null;
    }
    public String getVal(String name, MethodBasedEvaluationContext methodContext){
        if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
            return null;
        }
        String spelCacheKey = name + methodContext.toString();
        String val = spelCache.get(spelCacheKey);
        if (val == null) {
            val = getValUnCache(name, methodContext);
            spelCache.put(spelCacheKey, val);
        }
        return val;
    }
}
