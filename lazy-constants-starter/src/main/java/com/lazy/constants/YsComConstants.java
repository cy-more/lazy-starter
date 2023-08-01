package com.lazy.constants;

/**
 * @author ：cy
 * @description ：公共常量
 * @date ：2022/9/23 17:55
 */
public final class YsComConstants {

    /**
     * 已确认异常Code
     */
    public static final String ERROR_BIZ_CODE = "comm.system_exception";

    /**
     * 已确认异常Code
     */
    public static final String ERROR_UNKNOWN_CODE = "system_exception";

    /**
     * db 删除标记-未删除
     */
    public static final int DB_IS_DELETE_UN = 0;
    /**
     * db 删除标记-已删除
     */
    public static final int DB_IS_DELETE_DO = 1;

    /**
     * 历史记录
     */
    public static final int IS_HISTORY = 1;

    private YsComConstants() {
    }
}
