package com.lazy.utils.support;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ：cy
 * @description：xls标题信息
 * @date ：2022/6/8 19:41
 */
@Data
@AllArgsConstructor
public class XlsHeader {
    /**
     * 字段名
     */
    String keyName;

    /**
     * 字段类型
     */
    Class<?> keyType;

    /**
     * xls标题
     * @return name
     */
    String value;

    /**
     * 导入时，是否验空
     * @return
     */
    boolean isCheck;
}
