package com.lazy.exception;

import lombok.AllArgsConstructor;

/**
 * @author ：cy
 * @description：错误码
 * @date ：2021/10/13 11:10
 */
@AllArgsConstructor
public enum ResultCode {

    /**
     * 操作成功
     */
    OK("success", ""),

    /**
     * 操作失败
     */
    FAIL("-1", "系统繁忙，请稍后再试"),

    /**
     * 未登录或登录过期
     */
    UNAUTHORIZED("401", "认证未通过"),

    /**
     * 未登录或登录过期
     */
    UNAUTHORIZED_EXP("4011", "token已过期"),

    /**
     * JWT验证失败
     */
    ILLEGAL_JWT_FORMAT("402", "JWT验证失败"),

    /**
     * 操作者无权限访问接口
     * Forbidden
     */
    FORBIDDEN("403", "无权限访问"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR("500", "Internal Server Error"),

    /**
     * feign请求失败
     */
    FEIGN_HTTP_ERROR("11000", "feign请求失败"),

    /**
     * 参数非法
     */
    ILLEGAL_ARGUMENT("40000", "参数非法"),

    /**
     * 参数缺失
     */
    MISSING_ARGUMENT("40001", "参数缺失"),

    /**
     * 请求参数类型错误
     */
    TYPE_MISMATCH_ARGUMENT("40002", "参数类型错误"),

    /**
     * 用户不存在
     */
    ILLEGAL_USER("40003", "用户不存在"),

    /**
     * 用户凭据不受信任
     */
    ENABLE_USER("40003", "用户凭据不受信任"),

    /**
     * 空指针异常
     */
    NPE("50000", "空指针异常"),

    /**
     * 数据库异常
     */
    JDBC_GENERIC("50210", "数据库异常"),

    /**
     * 需确认的异常
     */
    CONFIRM("confirm", "待确认"),
    ;

    private final String code;

    private final String message;

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}

