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

package com.lazy.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ：cy
 * @description：redis缓存
 * 只缓存在redis,可定制超时时间，无其他情况优先考虑@Cacheable
 * 	不更新缓存
 *	存null
 *	前缀：[ys_cache:]
 * @date ：2022/7/20 20:37
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface YsRedisCache {

	/**
	 * 缓存名称
	 * @return name
	 */
	String value() default "";

	/**
	 * 自定义业务key
	 * @return key
	 */
	String[] key() default {};

	/**
	 * 超时时间
	 * @return
	 */
	long timeout() default 0L;

	/**
	 * 超时时间单位
	 * @return
	 */
	TimeUnit unit() default TimeUnit.SECONDS;
}
