package com.lazy.interfaces;

import java.sql.SQLException;

/**
 * @author: cy
 * @description:
 * @date: 2024-09-16 10:37
 **/
public interface YsTypeHandler {

    /**
     * 设置参数
     * @param parameter
     * @throws SQLException
     */
    Object setParameter(Object parameter) throws SQLException;
}
