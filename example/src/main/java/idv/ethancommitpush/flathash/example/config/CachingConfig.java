package idv.ethancommitpush.flathash.example.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

import idv.ethancommitpush.flathash.example.util.MultiCacheResolver;

@Configuration
/**
 * Add @EnableCaching annotation in order to enable @Cacheable @CachePut @CacheEvict annotations
 * proxyTargetClass set to false result in using jdk proxy, otherwise using cglib proxy
 */
@EnableCaching(proxyTargetClass = true)
public class CachingConfig extends CachingConfigurerSupport {

	@Autowired
    private CacheManager redisCacheManager;

	@Override
	public CacheResolver cacheResolver() {
		CacheManager redisCustomCacheManager = redisCacheManager;
		List<CacheManager> list = new ArrayList<>();
		//sequence represents the priority in the cacheManager
		list.add(redisCustomCacheManager);
		return new MultiCacheResolver(list);
	}

	/**
 	* Default key generator<br>
 	* If used @Cacheable @CachePut @CacheEvict annotations without setting key, then use this to generate key<br>
 	* Recommended to assign the key for custom usage<br>
 	* e.g., @Cacheable(value="", key="{#root.methodName, #id}")
 	*/
	@Override
	public KeyGenerator keyGenerator() {
		return (o, method, objects) -> {
			StringBuilder sb = new StringBuilder(32);
			sb.append(o.getClass().getSimpleName());
			sb.append(".");
			sb.append(method.getName());
			if (objects.length > 0) {
				sb.append(":");
			}
			String sp = "";
			for (Object object : objects) {
				sb.append(sp);
				sb.append(object == null ? "NULL" : object.toString());
				sp = ".";
			}
			return sb.toString();
		};
	}

}