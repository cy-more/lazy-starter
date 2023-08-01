package com.lazy.web.security.constant;

/**
 * @author ：cy
 * @description：TODO
 * @date ：2022/8/19 11:22
 */
public final class AuthConstants {

    public static final String STRATEGY_NAME = "com.lazy.web.security.handler.YsSecurityContextHolderStrategy";

    /**
     * token jsonKey
     */
    public static final String TOKEN_KEY_USERNAME = "username";
    public static final String TOKEN_KEY_ORGANIZE = "organize";

    private AuthConstants() {
    }
}
