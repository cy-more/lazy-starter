package com.lazy.entity;


import lombok.Data;

import java.util.Date;

/**
	 *返回类
	**/
@Data
public class ResultMsg<T> {

	private String code = "success";
	private String msg = "";
	private T data;
	private Date reponseTime = new Date();

	private ResultMsg(String code, String msg, T data) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	private ResultMsg(T data) {
		super();
		this.data = data;
	}
	private ResultMsg() {
		super();
	}

	public static <T extends Object> ResultMsg<T> ok(T obj) {
		return new ResultMsg<T>(obj);
	}

	public static <T extends Object> ResultMsg<T> ok() {
		return new ResultMsg<T>(null);
	}

	public static <T extends Object> ResultMsg<T> okMsg(String msg) {
		ResultMsg<T> tResultMsg = new ResultMsg<>(null);
		tResultMsg.setMsg(msg);
		return tResultMsg;
	}


	/**
	 * 请使用   new BizException
	 * @param errorCode
	 * @param errorMsg
	 * @param <T>
	 * @return
	 * @description 次方法仅供GlobalExceptionHandler使用 ，暂不做特殊处理 ，开发人员调用时注意
	 */
	public static <T extends Object> ResultMsg<T> fail(String errorCode, String errorMsg) {
		return new ResultMsg<T>(errorCode, errorMsg, null);
	}

	/**
	 * 请使用  newBizException
	 * @param errorCode
	 * @param errorMsg
	 * @param obj
	 * @param <T>
	 * @return
	 * @description 次方法仅供GlobalExceptionHandler使用 ，暂不做特殊处理 ，开发人员调用时注意
	 */
	public static <T extends Object> ResultMsg<T> fail(String errorCode, String errorMsg, T obj) {
		return new ResultMsg<T>(errorCode, errorMsg, obj);
	}

	public Long getReponseTime() {
		return reponseTime.getTime();
	}

}
