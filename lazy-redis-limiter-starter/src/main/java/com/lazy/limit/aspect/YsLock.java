/*
 * Copyright 2002-2016 the original author or authors.
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

package com.lazy.limit.aspect;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ：cy
 * @description：redis锁
 *
 * @date ：2022/7/20 20:37
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface YsLock {
	/**
	 * 锁的名称
	 * @return name
	 */
	String name() default "";
	/**
	 * 锁类型，默认可重入锁
	 * @return lockType
	 */
//	LockType lockType() default LockType.Reentrant;
	/**
	 * 尝试加锁，最多等待时间
	 * @return waitTime
	 */
	long waitTime() default Long.MIN_VALUE;

	/**
	 *上锁以后xxx自动解锁
	 * @return leaseTime
	 */
	long leaseTime() default Long.MIN_VALUE;

	/**
	 * 等待和释放时间单位
	 * @return
	 */
	TimeUnit timeUnit() default TimeUnit.SECONDS;

	/**
	 * 自定义业务key
	 * @return keys
	 */
	String [] keys() default {};

//	/**
//	 * 加锁超时的处理策略
//	 * @return lockTimeoutStrategy
//	 */
//	LockTimeoutStrategy lockTimeoutStrategy() default LockTimeoutStrategy.NO_OPERATION;
//
//	/**
//	 * 自定义加锁超时的处理策略
//	 * @return customLockTimeoutStrategy
//	 */
//	String customLockTimeoutStrategy() default "";
//
//	/**
//	 * 释放锁时已超时的处理策略
//	 * @return releaseTimeoutStrategy
//	 */
//	ReleaseTimeoutStrategy releaseTimeoutStrategy() default ReleaseTimeoutStrategy.NO_OPERATION;
//
//	/**
//	 * 自定义释放锁时已超时的处理策略
//	 * @return customReleaseTimeoutStrategy
//	 */
//	String customReleaseTimeoutStrategy() default "";

}
