package com.lazy.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.IntegerCodec;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ：cy
 * @description：通用分页响应视图
 * @date ：2021/9/27 20:31
 */
@ApiModel("分页结果")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YsPageVO<T> {

    protected List<T> records;

    @JSONField(serializeUsing = IntegerCodec.class)
    protected Long total = 0L;

    public void setTotal(Long total) {
        this.total = total == null ? 0L : total;
    }

}
