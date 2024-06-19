/*
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lazy.security.entity;

import org.springframework.security.core.AuthenticationException;

/**
 *
 * @author cy
 */
public class YsAuthBizException extends AuthenticationException {

	/**
	 * 错误编码
	 */
	private String errorCode = "auth_exception";

	/**
	 * 构造一个基本异常.
	 *
	 * @param msg
	 *            信息描述
	 */
	public YsAuthBizException(String msg) {
		super(msg);
	}

	/**
	 * 构造一个基本异常.
	 *
	 * @param errorCode
	 *            错误编码
	 * @param message
	 *            信息描述
	 */
	public YsAuthBizException(String errorCode, String message) {
		super(message);
		setErrorCode(errorCode);
	}

	public YsAuthBizException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
