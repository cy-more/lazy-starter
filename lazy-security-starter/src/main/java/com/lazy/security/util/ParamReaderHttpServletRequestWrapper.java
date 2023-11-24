package com.lazy.security.util;

import cn.hutool.core.util.ObjectUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：cy
 * @description：自定义表单参数
 * @date ：2022/3/3 17:52
 */
public class ParamReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String , String[]> params = new HashMap<>();
    private final String VALUE_NULL = "null";

    public ParamReaderHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        //将参数表，赋予给当前的Map以便于持有request中的参数
        this.params.putAll(request.getParameterMap());
    }

    @Override
    public String getParameter(String name) {//重写getParameter，代表参数从当前类中的map获取
        String[]values = params.get(name);
        if (ObjectUtil.isEmpty(values) || VALUE_NULL.equals(values[0])){
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = params.get(name);
        if (ObjectUtil.isEmpty(values) || VALUE_NULL.equals(values[0])){
            return null;
        }
        return values;
    }
}
