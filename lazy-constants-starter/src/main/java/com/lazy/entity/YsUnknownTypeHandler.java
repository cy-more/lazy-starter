package com.lazy.entity;

import com.lazy.interfaces.YsTypeHandler;

import java.sql.SQLException;

/**
 * @author: cy
 * @description: 不做转换
 * @date: 2024-09-16 10:57
 **/
public class YsUnknownTypeHandler implements YsTypeHandler {
    @Override
    public Object setParameter(Object parameter) throws SQLException {
        return parameter;
    }
}
