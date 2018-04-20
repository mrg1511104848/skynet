package org.skynet.frame.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
	String value() default "";
	String attr() default "";
	boolean needAbsolutePath() default false;
	boolean useText() default true;
	boolean isList() default false;
	boolean isJson() default false;
	boolean isDefault() default false;
	boolean needSave() default true;
	boolean needMD5() default false;
	boolean needZLIB() default false;
	boolean useFirst() default true;
	boolean useOwnText() default false;
}
