package com.lazy.crud.mybatis;

import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ：cy
 * @description：jsonArray转换
 * @date ：2022/7/28 17:04
 */
public class JSONArrayHandler extends BaseTypeHandler<JSONArray> {

    public JSONArray delResult(String jsonSource) throws SQLException {
        if(jsonSource != null){
            JSONArray jsonArray;
            try{
                jsonArray = JSONArray.parseArray(jsonSource);
            }catch (Exception ex){
                throw new SQLException("There is an error converting JSONArray to json format for the content:" + jsonSource);
            }
            return jsonArray;
        }
        return null;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps,
                                    int i,
                                    JSONArray parameter, //需要转换的类型,JSON类型
                                    JdbcType jdbcType) throws SQLException {
        ps.setString(i,parameter.toJSONString());
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return delResult(rs.getString(columnName));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return delResult(rs.getString(columnIndex));
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return delResult(cs.getString(columnIndex));
    }

}
