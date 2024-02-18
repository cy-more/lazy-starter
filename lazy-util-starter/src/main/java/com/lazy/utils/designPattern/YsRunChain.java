package com.lazy.utils.designPattern;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author: cy
 * @description: 链式调用
 * @date: 2024-01-04 11:18
 **/
public class YsRunChain<T, R> {

    /**
     * 执行结果
     */
    private R result = null;

    /**
     * 是否继续执行的条件
     */
    private final Function<R, Boolean> condition;

    /**
     * 执行
     * @param function
     * @param dto
     * @return
     */
    public YsRunChain<T, R> run(Function<T, R> function, T dto) {
        if (condition.apply(result)) {
            result = function.apply(dto);
        }
        return this;
    }
    public YsRunChain<T, R> run(Supplier<R> supplier) {
        if (condition.apply(result)) {
            result = supplier.get();
        }
        return this;
    }

    public R getResult() {
        return result;
    }

    public YsRunChain(Function<R, Boolean> condition) {
        this.condition = condition;
    }

    /**
     * 要用这个默认的会显得这个链式很没用
     */
    public YsRunChain(){
        this.condition = r -> true;
    }
}