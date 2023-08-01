package com.lazy.crud.mybatis;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lazy.crud.util.AESUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ：cy
 * @description：数据加密handler
 * @date ：2021/9/28 13:52
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(value = String.class)
public class AesTypeHandler extends BaseTypeHandler {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, AESUtil.encrypt((String) parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return StringUtils.isBlank(rs.getString(columnName)) ? rs.getString(columnName) : AESUtil.decrypt(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return StringUtils.isBlank(rs.getString(columnIndex)) ? rs.getString(columnIndex) : AESUtil.decrypt(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return StringUtils.isBlank(cs.getString(columnIndex)) ? cs.getString(columnIndex) : AESUtil.decrypt(cs.getString(columnIndex));
    }
}
