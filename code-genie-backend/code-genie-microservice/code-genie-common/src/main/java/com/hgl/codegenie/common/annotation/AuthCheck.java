package com.hgl.codegenie.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: AuthCheck
 * Package: com.hgl.codegenie.common.annotation
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 8:41
 */

@Target(ElementType.METHOD) // 注解作用在方法上
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时生效
public @interface AuthCheck {

    /**
     * 必须有某个角色
     */
    String mustRole() default "";
}
