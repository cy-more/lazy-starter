package com.lazy.exception;


import java.util.ArrayList;
import java.util.Collections;

/**
 * 自定义异常
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 4180240084881978922L;

    private static final String ARMS_EXCEPTION_RECORDER_HANDLER_CLASS_NAME =
        "com.navercorp.pinpoint.profiler.context.recorder.ExceptionRecorderHandlerV3";

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
    public BizException(String message) {
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
    public BizException(String errorCode, String message) {
        super(message);
        setErrorCode(errorCode);
    }

    public BizException(ResultCode resultCode, String message) {
        this(resultCode.getCode(), message);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public BizException(String errorCode, String message, Throwable cause) {
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
    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /*
     *  修改suepr的方法实现，因为arms的异常看板不友好，无法直观反应biz异常细节
     * arms agent关于异常记录实现:com.navercorp.pinpoint.profiler.context.recorder.ExceptionRecorderHandlerV3
     *
     * @see java.lang.Throwable#getStackTrace()
     */
    @Override
    public StackTraceElement[] getStackTrace() {
        try {
            String callerClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            if (ARMS_EXCEPTION_RECORDER_HANDLER_CLASS_NAME.equals(callerClassName)) {
                ArrayList<StackTraceElement> newStackTraceElements = new ArrayList<StackTraceElement>();
                Collections.addAll(newStackTraceElements, super.getStackTrace());

                // msg
                StackTraceElement msgStackTraceElement =
                    new StackTraceElement("messgae:" + this.getMessage(), " ", null, 0);
                newStackTraceElements.add(0, msgStackTraceElement);

                return newStackTraceElements.toArray(new StackTraceElement[] {});

            } else {
                return super.getStackTrace();
            }

        } catch (Exception e) {
            return super.getStackTrace();
        }
    }

}
