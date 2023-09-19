package com.lazy.crud.mybatis;

import com.lazy.entity.YsPageDTO;
import com.lazy.entity.YsPageVO;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：cy
 * @description：分页模板 M：dto泛型，V:vo泛型
 * 待扩展 SQL_CALC_FOUND_ROWS
 * @date ：2021/10/26 9:04
 */
@NoArgsConstructor
public abstract class YsPageTemplate<M extends Serializable, V> {

    public YsPageVO<V> templatePage(YsPageDTO<M> dto){
        //查询
        List<V> vList = tPageList(dto);
        //转意
        vList.forEach(this::tTransName);

        YsPageVO<V> pageVO = new YsPageVO<>();
        pageVO.setRecords(vList);
        if (dto.isSearchCount()) {
            Integer snCount = tPageCount(dto);
            pageVO.setTotal(snCount == null ? 0L : snCount);
        }
        return pageVO;
    }


    /**
     * 数据
     * @param dto
     * @return
     */
    protected abstract List<V> tPageList(YsPageDTO<M> dto);

    /**
     * 数量
     * @param dto
     * @return
     */
    protected Integer tPageCount(YsPageDTO<M> dto){
        return null;
    }

    /**
     * 后加工 如：转意
     * @param pageVO
     */
    protected void tTransName(V pageVO){}
}
