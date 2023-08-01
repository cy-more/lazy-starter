package com.lazy.kd100.common;

/**
 * @author ：cy
 * @description：快递推送状态
 * @date ：2022/4/18 16:58
 */
public enum Kd100DeliveryState {
    //未确认
    NONE("", "无"),
    //在途
    ON_WAY("0", "在途"),
    ON_WAY_COM("1001", "到达派件城市"),
    ON_WAY_TRANS("1002", "干线"),
    ON_WAY_FORWARD("1003", "转递"),
    //揽收
    COLLECT("1", "揽收"),
    COLLECT_OPEN("101", "已下单"),
    COLLECT_WAIT("102", "待揽收"),
    COLLECT_HAV("103", "已揽收"),
    //疑难
    DIFFICULTY("2", "疑难"),
    DIFFICULTY_UN_SIGN("201", "超时未签收"),
    DIFFICULTY_UNDO("202", "超时未更新"),
    DIFFICULTY_REJECT("203", "拒收"),
    DIFFICULTY_EX("204", "派件异常"),
    DIFFICULTY_TIMEOUT("205", "柜或驿站超时未取"),
    DIFFICULTY_OUT("206", "无法联系"),
    DIFFICULTY_BEYOND("207", "超区"),
    DIFFICULTY_RETENTION("208", "滞留"),
    DIFFICULTY_DAMAGE("209", "破损"),
    //签收
    SIGN("3", "签收"),
    SIGN_OWN("301", "本人签收"),
    SIGN_EX("302", "派件异常后签收"),
    SIGN_PROXY("303", "代签"),
    SIGN_POST("304", "投柜或站签收"),
    //退签
    BACK_SIGN("4", "退签"),
    BACK_SIGN_REVOKE("401", "已销单"),
    BACK_SIGN_DENY("14", "拒签"),
    //派件
    DISPATCH("5", "派件"),
    DISPATCH_POST("501", "投柜或驿站"),
    //退回
    RETURN("6", "退回"),
    //转投
    SWITCH("7", "转投"),
    //清关
    CUSTOMS("8", "清关"),
    CUSTOMS_WAIT("10", "待清关"),
    CUSTOMS_ING("11", "清关中"),
    CUSTOMS_HAV("12", "已清关"),
    CUSTOMS_EX("13", "清关异常"),
    //拒签
    DENY_SIGN("14", "拒签");

    private String code;
    private String desc;

    Kd100DeliveryState(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getValue(String k) {
        for (Kd100DeliveryState node : values()) {
            if (node.getCode().equals(k)) {
                return node.getDesc();
            }
        }
        return null;
    }

    public static String getCode(String desc) {
        for (Kd100DeliveryState node : values()) {
            if (node.getDesc().equals(desc)) {
                return node.getCode();
            }
        }
        return null;
    }

    public static Kd100DeliveryState getEnum(String k) {
        for (Kd100DeliveryState node : values()) {
            if (node.getCode().equals(k)) {
                return node;
            }
        }
        return null;
    }

}
