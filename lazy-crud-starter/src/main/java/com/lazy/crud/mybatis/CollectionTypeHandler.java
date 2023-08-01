package com.lazy.crud.mybatis;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author ：cy
 * @description：java:list--db:json
 * @date ：2022/7/28 18:37
 */
public class CollectionTypeHandler<T> extends BaseTypeHandler<List<T>> {

    protected Class<T> entityClass = currentModelClass();

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<T> ts, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSONArray.toJSONString(ts));
    }

    @Override
    public List<T> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return delResult(resultSet.getString(s));
    }

    @Override
    public List<T> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return delResult(resultSet.getString(i));
    }

    @Override
    public List<T> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return delResult(callableStatement.getString(i));
    }

    private List<T> delResult(String source) throws SQLException {
        if(source != null){
            List<T> collection;
            try{
                collection = JSONArray.parseArray(source, entityClass);
            }catch (Exception ex){
                throw new SQLException("There is an error converting JSONObject to json format for the content:" + source);
            }
            return collection;
        }
        return null;
    }

    protected Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 0);
    }
}
