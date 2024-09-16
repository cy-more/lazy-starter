package com.lazy.entity;

import com.lazy.interfaces.YsTypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author: cy
 * @description: 不做转换
 * @date: 2024-09-16 10:57
 **/
public class YsUnknownTypeHandler implements YsTypeHandler {
    @Override
    public void setParameter(PreparedStatement ps, Object parameter) throws SQLException {
    }
}
