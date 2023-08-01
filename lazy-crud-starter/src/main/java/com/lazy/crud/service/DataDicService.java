package com.lazy.crud.service;

import java.util.Map;

/**
 * 数据字典接口 结果推荐缓存
 * @author cy
 * @since 2021-10-11
 */
public interface DataDicService {

    /**
     * 查询字典
     * @param table
     * @param column
     * @return
     */
    Map<String, String> getErpDataDicMap(String table, String column);

    Map<String, String> getErpDataDicMapForCode(String table, String column);
}
