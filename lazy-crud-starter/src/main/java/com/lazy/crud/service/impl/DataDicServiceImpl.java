package com.lazy.crud.service.impl;

import com.lazy.crud.service.DataDicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * 数据字典查询 扩展口
 * @author cy
 * @since 2021-10-11
 */
@Slf4j
@Service
public class DataDicServiceImpl implements DataDicService {

    /**
     * 查询字典
     * @param table
     * @param column
     * @return
     */
    @Override
    public Map<String, String> getErpDataDicMap(String table, String column){
        log.warn("DataDicService接口未实现，转意功能不生效");
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getErpDataDicMapForCode(String table, String column) {
        log.warn("DataDicService接口未实现，转意功能不生效");
        return Collections.emptyMap();
    }

}
