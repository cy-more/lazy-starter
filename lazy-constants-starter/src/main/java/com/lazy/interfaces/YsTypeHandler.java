package com.lazy.interfaces;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author: cy
 * @description:
 * @date: 2024-09-16 10:37
 **/
public interface YsTypeHandler {

    void setParameter(PreparedStatement ps, Object parameter) throws SQLException;
}
