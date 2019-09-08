package idv.ethancommitpush.flathash.example.config;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import idv.ethancommitpush.flathash.FlatHashRedisCacheWriter;
import idv.ethancommitpush.flathash.example.util.RedisExpireCacheManager;
import lombok.extern.slf4j.Slf4j;

/**
 *  Redis configure<br>
 *  Use @EnableConfigurationProperties(RedisProperties.class) to initialize redisConnectionFactory<br>
 *  Use @bean to initialize redisTemplate, thus setting serializer<br>
 *  Use @bean to initialize redisCacheManager, thus setting serializer
 */
@Configuration
//Unabled when there is no spring.redis.host in application.properties, application.yml
@ConditionalOnProperty(name = "spring.redis.host")
@EnableConfigurationProperties(RedisProperties.class)
@Slf4j
public class RedisConfig {
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		// set key serializer
		StringRedisSerializer keySer = RedisExpireCacheManager.keySerializer();
		// set key serializer, otherwise there would be garbled texts in front of the key
		template.setKeySerializer(keySer);
		template.setHashKeySerializer(keySer);

		// jackson serializer
		GenericJackson2JsonRedisSerializer valSer = RedisExpireCacheManager.valueSerializer();
		template.setValueSerializer(valSer);
		template.setHashValueSerializer(valSer);
		// Enabled when either KeySerializer or ValueSerializer is not set, then the correspond KeySerializer or ValueSerializer would use this Serializer
		template.setDefaultSerializer(valSer);

		log.info("redis: {}", connectionFactory);
		LettuceConnectionFactory factory = (LettuceConnectionFactory) connectionFactory;
		log.info("spring.redis.database: {}", factory.getDatabase());
		log.info("spring.redis.host: {}", factory.getHostName());
		log.info("spring.redis.port: {}", factory.getPort());
		log.info("spring.redis.timeout: {}", factory.getTimeout());
		log.info("spring.redis.password: {}", factory.getPassword());

		// factory
		template.setConnectionFactory(connectionFactory);
		template.afterPropertiesSet();
		return template;
	}

	/** Set RedisCacheManager for using cache annotation to deal with redis cache */
	@Bean
	public static CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
		// Initialize RedisCacheWriter
		FlatHashRedisCacheWriter cacheWriter = FlatHashRedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

		// Set default ttl: 30 min
		RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(30))
				// .disableCachingNullValues()
				.serializeKeysWith(RedisSerializationContext
						.SerializationPair.fromSerializer(RedisExpireCacheManager.keySerializer()))
				.serializeValuesWith(RedisSerializationContext
						.SerializationPair.fromSerializer(RedisExpireCacheManager.valueSerializer()));

		return new RedisExpireCacheManager(cacheWriter, defaultCacheConfig);
	}

}