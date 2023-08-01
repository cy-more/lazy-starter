package com.lazy.rocketmq.support;

/**
 * @author ：cy
 * @description：mq校验异常
 * @date ：2022/1/25 10:44
 */
public class MqBizException extends RuntimeException{

    /**
     * 错误编码
     */
    private String errorCode = "comm.system_exception";

    /**
     * 构造一个基本异常.
     *
     * @param message
     *            信息描述
     */
    public MqBizException(String message) {
        super(message);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public MqBizException(String errorCode, String message) {
        super(message);
        setErrorCode(errorCode);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public MqBizException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        setErrorCode(errorCode);
    }

    /**
     * 构造一个基本异常.
     *
     * @param message
     *            信息描述
     * @param cause
     *            根异常类（可以存入任何异常）
     */
    public MqBizException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
