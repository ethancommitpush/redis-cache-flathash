package idv.ethancommitpush.flathash.example.annotation;

//import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
/**
 * Defining annotation CacheExpire
 */
public @interface CacheExpire {
//	/** expire time, default 60s */
//	@AliasFor("expire")
//	long value() default 60L;
//
//	/** expire time, default 60s */
//	@AliasFor("value")
//	long expire() default 60L;
	long expire() default 60L;
}