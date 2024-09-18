package com.lazy.crud.mybatis;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.lazy.annotation.YsCondition;
import com.lazy.crud.constants.CrudConstant;
import com.lazy.crud.service.DataDicService;
import com.lazy.entity.YsPageDTO;
import com.lazy.entity.YsPageTemplate;
import com.lazy.entity.YsPageVO;
import com.lazy.exception.BizException;
import com.lazy.interfaces.YsTypeHandler;
import com.lazy.utils.YsBeanUtil;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ：cy
 * @description：通用mybatis-service
 * @date ：2021/9/27 19:37
 */
public class YsServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    /**
     * 扩展 字典
     */
    @Autowired
    public DataDicService dataDicService;

    @Autowired
    @Qualifier("caffeineCache")
    Cache<Object,Object> caffeineCache;

    @Autowired
    @Qualifier("taskExecutor")
    Executor taskExecutor;

    /**
     * 字段转意
     * code->name
     * @param table
     * @param column
     * @param val
     * @return
     */
    public String transName(String table, String column, Object val){
        if (val == null){
            return null;
        }
        return dataDicService.getErpDataDicMap(table, column).get(String.valueOf(val));
    }
    /**
     * 字段转意
     * name->code
     * @param table
     * @param column
     * @param val
     * @return
     */
    public String transNameForCode(String table, String column, Object val){
        return dataDicService.getErpDataDicMapForCode(table, column).get(String.valueOf(val));
    }

    /**
     * 字段转意 table默认为当前entity
     * @param column
     * @param val
     * @return
     */
    public String transName(String column, Object val){
        return transName(TableInfoHelper.getTableInfo(entityClass).getTableName(), column, val);
    }
    public String transNameForCode(String column, Object val){
        return transNameForCode(TableInfoHelper.getTableInfo(entityClass).getTableName(), column, val);
    }
    public String transNameForCodeWithWarn(String column, Object val){
        return transNameForCodeWithWarn(TableInfoHelper.getTableInfo(entityClass).getTableName(), column, val);
    }
    public String transNameForCodeWithWarn(String tableName, String column, Object val){
        String code = transNameForCode(tableName, column, val);
        if (StringUtils.isEmpty(code)){
            throw new IllegalArgumentException("不存在的枚举值：column:" + column + ",val:" + val);
        }
        return code;
    }

    /**
     * page 全字段组装条件
     * @param condition
     * @return
     */
    public QueryWrapper<T> getConditionWrapper(Object condition){
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (condition == null){
            return queryWrapper;
        }

        initConditionWrapperByYsCondition(condition, queryWrapper);
        return queryWrapper;
    }

    /**
     * 根据注解装载条件
     * @param condition
     * @param queryWrapper
     */
    private void initConditionWrapperByYsCondition(Object condition, QueryWrapper<T> queryWrapper){
        //遍历字段
        Field[] fields = condition.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //解析注解
            YsCondition ysCondition = field.getDeclaredAnnotation(YsCondition.class);
            //无注解不赋予条件
            if (ObjectUtil.isEmpty(ysCondition)){
                continue;
            }
            //获取对应po字段
            String column = StringUtils.isEmpty(ysCondition.column()) ? getColumnName(field.getName()) : ysCondition.column();

            //判断是否做排序
            switch (ysCondition.orderType()){
                case ASC:
                    queryWrapper.orderByAsc(column);
                    break;
                case DESC:
                    queryWrapper.orderByDesc(column);
                    break;
                default:
                    break;
            }

            //获取值
            Object value;
            try {
                value = field.get(condition);
                if (ObjectUtil.isNotEmpty(value)){
                    Class<? extends YsTypeHandler> typeHandlerClazz = ysCondition.typeHandler();
                    YsTypeHandler typeHandler = (YsTypeHandler) caffeineCache.get(CrudConstant.TYPE_HANDLER + typeHandlerClazz.getName(), k -> {
                        try {
                            return typeHandlerClazz.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            String errorMsg = "typeHandler初始化异常,error:" + e.getMessage();
                            log.error(errorMsg, e);
                            throw new BizException(errorMsg);
                        }
                    });
                    value = typeHandler.setParameter(value);
                }
            } catch (SQLException | IllegalAccessException e) {
                String errorMsg = "分页注解字段获取失败,error:" + e.getMessage();
                log.error(errorMsg, e);
                throw new BizException(errorMsg);
            }
            //为空跳过
            if (ysCondition.ignoreNullValue() && ObjectUtil.isEmpty(value)){
                //赋予默认值
                value = ysCondition.defaultValue();
                if (ObjectUtil.isEmpty(value)) {
                    continue;
                }
            }

            //为字段赋予条件
            switch (ysCondition.type()){
                case EQ:
                    queryWrapper.eq(column, value);
                    break;
                case LIKE:
                    queryWrapper.like(column, value);
                    break;
                case IN:
                    if (value instanceof Collection){
                        Collection<?> collectVal = (Collection<?>) value;
                        queryWrapper.in(column, collectVal);
                    }else {
                        queryWrapper.in(column, value);
                    }
                    break;
                case GE:
                    queryWrapper.ge(column, value);
                    break;
                case LE:
                    queryWrapper.le(column, value);
                    break;
                case GT:
                    queryWrapper.gt(column, value);
                    break;
                case LT:
                    queryWrapper.lt(column, value);
                    break;
                case ISNULL:
                    Boolean booleanValue = value instanceof Boolean ? (Boolean)value :
                            (value instanceof String ? Boolean.getBoolean((String) value) : null);
                    if (null == booleanValue) {
                        throw new BizException("是否为空的查询条件参数类型必须为Boolean/String,字段：" + column);
                    }else if (booleanValue) {
                        queryWrapper.isNull(column);
                    }else {
                        queryWrapper.isNotNull(column);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * page yspage组装page
     * @param ysPageDTO
     * @return
     */
    public Page<T> getPage(YsPageDTO<?> ysPageDTO){
        Page<T> page = new Page<>();
        page.setCurrent(ysPageDTO.getCurrent());
        page.setSize(ysPageDTO.getSize());
        page.setSearchCount(ysPageDTO.isSearchCount());
        if (ObjectUtil.isNotEmpty(ysPageDTO.getOrders())) {
            page.setOrders(ysPageDTO.getOrders().stream()
                    .map(item -> new OrderItem(getColumnName(item.getColumn()), item.isAsc()))
                    .collect(Collectors.toList()));
        }
        return page;
    }

    /**
     * 分页 扩展：结果处理
     * @param ysPageDTO
     * @param <V>
     * @return
     */
    public <V> YsPageVO<V> ysPageForVo(YsPageDTO<?> ysPageDTO, Class<? extends V> clazz) {
        return ysPageForVo(ysPageDTO, t -> YsBeanUtil.toBean(t, clazz));
    }
    public <V> YsPageVO<V> ysPageForVo(YsPageDTO<?> ysPageDTO, Function<? super T, ? extends V> mapper){
        YsPageVO<T> ysPage = ysPage(ysPageDTO);
        List<V> records = ysPage.getRecords().stream().map(mapper).collect(Collectors.toList());
        return new YsPageVO<V>(records, ysPage.getTotal());
    }

    /**
     * yspage 专用分页page
     * @param ysPageDTO
     * @return
     */
    public YsPageVO<T> ysPage(YsPageDTO<?> ysPageDTO){
        return ysPage(getPage(ysPageDTO), getConditionWrapper(ysPageDTO.getCondition()));
    }

    /**
     * yspage 专用分页page 自定义参数
     * @param page
     * @param queryWrapper
     * @return
     */
    public YsPageVO<T> ysPage(Page<T> page, Wrapper<T> queryWrapper){
        Page<T> resultPage = super.page(page, queryWrapper);
        YsPageVO<T> ysPageVO = new YsPageVO<>();
        BeanUtil.copyProperties(resultPage, ysPageVO, false);
        return ysPageVO;
    }

    /**
     * 分页
     * @param ysPageDTO
     * @param clazz
     * @param <V> 返回类型
     * @return
     */
    public <V> YsPageVO<V> ysPage(YsPageDTO<?> ysPageDTO, Class<V> clazz){
        return ysPage(ysPageDTO, t -> YsBeanUtil.toBean(t, clazz));
    }
    public <V> YsPageVO<V> ysPage(YsPageDTO<?> ysPageDTO, Function<? super T, ? extends V> mapper){
        YsPageVO<T> ysPage = ysPage(ysPageDTO);
        List<V> records = ysPage.getRecords().stream().map(mapper).collect(Collectors.toList());
        return new YsPageVO<>(records, ysPage.getTotal());
    }

    /**
     * YsPageTemplate优化，添加异步count查询
     * 适用于耗时查询
     * @param dto
     * @param template
     * @param <M>
     * @param <V>
     * @return
     */
    public <M extends Serializable, V> YsPageVO<V> ysPage(YsPageDTO<M> dto, YsPageTemplate<M, V> template){
        //查询数量
        CompletableFuture<Long> countFuture = null;
        if (dto.isSearchCount()){
            countFuture = CompletableFuture.supplyAsync(() -> {
                Integer snCount = template.tPageCount(dto);
                return snCount == null ? 0L : snCount;
            }, taskExecutor)
            .exceptionally((Throwable e) -> {
                log.error("数量查询异常：" + e.getMessage(), e);
                return null;
            });
        }

        YsPageVO<V> pageVO = new YsPageVO<>();
        //查询
        List<V> recordList = template.tPageList(dto);
        //转意
        recordList.forEach(template::tTransName);
        pageVO.setRecords(recordList);

        //装载数量
        try {
            pageVO.setTotal(countFuture == null ? null : countFuture.get());
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return pageVO;
    }

    /**
     * 获取数据库字段
     * @param fieldName
     * @return
     */
    @SuppressWarnings({"all"})
    public String getColumnName(String fieldName){
        //获取字段kv
        Map<String, String> fieldMap = (Map<String, String>) caffeineCache.get(CrudConstant.FIELD_DIC + entityClass,
                k -> {
                    TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
                    Map<String, String> columList = tableInfo.getFieldList()
                            .stream()
                            .collect(Collectors.toMap(TableFieldInfo::getProperty, TableFieldInfo::getColumn));
                    columList.put(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
                    return columList;
                });

        return fieldMap.get(fieldName);
    }

    /**
     * 获取字段名
     * @param func
     * @return
     * @param <T>
     */
    public <T> String getFieldName(SFunction<T, ?> func) {
        //序列化Lambda
        String name = LambdaUtils.extract(func).getImplMethodName();
        return PropertyNamer.methodToProperty(name);
    }
}
