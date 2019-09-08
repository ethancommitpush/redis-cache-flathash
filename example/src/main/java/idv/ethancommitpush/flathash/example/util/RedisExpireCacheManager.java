package idv.ethancommitpush.flathash.example.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ReflectionUtils;

import idv.ethancommitpush.flathash.FlatHashRedisCache;
import idv.ethancommitpush.flathash.FlatHashRedisCacheManager;
import idv.ethancommitpush.flathash.FlatHashRedisCacheWriter;
import idv.ethancommitpush.flathash.example.annotation.CacheExpire;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Redis cache manager */
@Slf4j
public class RedisExpireCacheManager extends FlatHashRedisCacheManager implements ApplicationContextAware {
	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}

	private Map<String, RedisCacheConfiguration> initialCacheConfiguration = new LinkedHashMap<>();

	public RedisExpireCacheManager(FlatHashRedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
		super(cacheWriter, defaultCacheConfiguration);
	}

	/**
 	* Redis cache manager method to read configure in initializing phase
 	* For CacheExpire to access the private variable initialCacheConfiguration in super class
 	* Thus reinitialize initialCacheConfiguration and re-define this method
 	*/
	@Override
	protected Collection<FlatHashRedisCache> loadCaches() {
		List<FlatHashRedisCache> caches = new LinkedList<>();
		for (Map.Entry<String, RedisCacheConfiguration> entry : initialCacheConfiguration.entrySet()) {
			caches.add(super.createRedisCache(entry.getKey(), entry.getValue()));
		}
		return caches;
	}

	/** Prep works before cache initializing, bind custom annotation: CacheExpire **/
	@Override
	public void afterPropertiesSet() {
		String[] beanNames = ctx.getBeanNamesForType(Object.class);
		for (String beanName : beanNames) {
			final Class<?> clazz = ctx.getType(beanName);
			filterCacheExpireMethods(clazz);
		}
		super.afterPropertiesSet();
	}

	private void filterCacheExpireMethods(final Class<?> clazz) {
		ReflectionUtils.doWithMethods(clazz, method -> {
			//Run this part when components managed by the spring framework using CacheExpire annotation
			ReflectionUtils.makeAccessible(method);
			CacheExpire cacheExpire = AnnotationUtils.findAnnotation(method, CacheExpire.class);
			Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
			Caching caching = AnnotationUtils.findAnnotation(method, Caching.class);
			CacheConfig cacheConfig = AnnotationUtils.findAnnotation(clazz, CacheConfig.class);

			List<String> cacheNames = getCacheNames(cacheable, caching, cacheConfig);
			handleCacheExpire(cacheNames, cacheExpire);
		}, method -> null != AnnotationUtils.findAnnotation(method, CacheExpire.class));
	}

	/** Handle methods using @CacheExpire **/
	private void handleCacheExpire(List<String> cacheNames, CacheExpire cacheExpire) {
		for (String cacheName : cacheNames) {
			Optional.ofNullable(cacheName)
			.filter(Objects::nonNull)
			.filter(name ->  !"".equals(name.trim()))
			.ifPresent(name -> {
				long expire = cacheExpire.expire();
				log.info("cacheName: {}, expire: {}", cacheName, expire);
				if (expire < 0) {
					log.warn("{} use default expiration.", cacheName);
					return;
				}
				// Configure of cache
				RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
						.entryTtl(Duration.ofSeconds(expire))
						.disableCachingNullValues()
						// .prefixKeysWith(cacheName)
						.serializeKeysWith(RedisSerializationContext
								.SerializationPair.fromSerializer(keySerializer()))
						.serializeValuesWith(RedisSerializationContext
								.SerializationPair.fromSerializer(valueSerializer()));
				initialCacheConfiguration.put(cacheName, config);
			});
		}
	}

	/** List cache names under all methods using @Cacheable @Caching @CacheConfig (set by (value=)) **/
    private List<String> getCacheNames(Cacheable cacheable, Caching caching, CacheConfig cacheConfig) {
        List<String> list = new ArrayList<>();
        // Cacheable
        if (cacheable != null) {
            String[] cacheNames = cacheable.cacheNames();
            if (cacheNames.length > 0) {
                list.addAll(Arrays.asList(cacheNames));
            }
        }
        if (list.size() > 0) {
            return list;
        }

        // Caching
        if (caching != null) {
            Cacheable[] cacheables = caching.cacheable();
            for (Cacheable cache : cacheables) {
                String[] cacheNames = cache.cacheNames();
                if (cacheNames.length > 0) {
                    list.addAll(Arrays.asList(cacheNames));
                }
            }
        }
        if (list.size() > 0) {
            return list;
        }

        // CacheConfig
        if (cacheConfig != null) {
            String[] cacheNames = cacheConfig.cacheNames();
            if (cacheNames.length > 0) {
                list.addAll(Arrays.asList(cacheNames));
            }
        }
        return list;
    }

	public static StringRedisSerializer keySerializer() {
		return new StringRedisSerializer();
	}

	public static GenericJackson2JsonRedisSerializer valueSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

}