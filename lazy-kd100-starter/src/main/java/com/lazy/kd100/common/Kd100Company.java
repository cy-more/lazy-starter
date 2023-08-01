package com.lazy.kd100.common;

/**
 * @author ：cy
 * @description：快递公司
 * 太多就没写全
 * @date ：2022/4/18 16:58
 */
public enum Kd100Company {
    //圆通速递
    YUAN_TONG("yuantong", "圆通速递"),
    //韵达快递
    YUN_DA("yunda", "韵达快递"),
    //中通快递
    ZHONG_TONG("zhongtong", "中通快递"),
    //申通快递
    SHEN_TONG("shentong", "申通快递"),
    //邮政快递包裹
    YOUZHENG_GUONEI("youzhengguonei", "邮政快递包裹"),
    //顺丰速运
    SHUN_FENG("shunfeng", "顺丰速运");

    private String code;
    private String desc;

    Kd100Company(String code, String desc) {
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
        for (Kd100Company node : values()) {
            if (node.getCode().equals(k)) {
                return node.getDesc();
            }
        }
        return null;
    }

    public static String getCode(String desc) {
        for (Kd100Company node : values()) {
            if (node.getDesc().equals(desc)) {
                return node.getCode();
            }
        }
        return null;
    }

    public static Kd100Company getEnum(String k) {
        for (Kd100Company node : values()) {
            if (node.getCode().equals(k)) {
                return node;
            }
        }
        return null;
    }

}
