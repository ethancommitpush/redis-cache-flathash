/*
 * Copyright 2017-2019 the original author or authors.
 * Modifications copyright 2019 Yisin Lin.
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
package idv.ethancommitpush.flathash;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * This is a derived work of org.springframework.data.redis.cache.FlatHashRedisCacheWriter.
 * {@link FlatHashRedisCacheWriter} provides low level access to Redis commands ({@code HMSET, HSETNX, HGETALL, EXPIRE,...}) used for
 * caching. <br />
 * The {@link FlatHashRedisCacheWriter} may be shared by multiple cache implementations and is responsible for writing / reading
 * binary data to / from Redis. The implementation honors potential cache lock flags that might be set.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Yisin Lin
 * @since 1.0.0
 */
public interface FlatHashRedisCacheWriter {

	/**
	 * Create new {@link FlatHashRedisCacheWriter} without locking behavior.
	 *
	 * @param connectionFactory must not be {@literal null}.
	 * @return new instance of {@link DefaultFlatHashRedisCacheWriter}.
	 */
	static FlatHashRedisCacheWriter nonLockingRedisCacheWriter(RedisConnectionFactory connectionFactory) {

		Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");

		return new DefaultFlatHashRedisCacheWriter(connectionFactory);
	}

	/**
	 * Create new {@link FlatHashRedisCacheWriter} with locking behavior.
	 *
	 * @param connectionFactory must not be {@literal null}.
	 * @return new instance of {@link DefaultFlatHashRedisCacheWriter}.
	 */
	static FlatHashRedisCacheWriter lockingRedisCacheWriter(RedisConnectionFactory connectionFactory) {

		Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");

		return new DefaultFlatHashRedisCacheWriter(connectionFactory, Duration.ofMillis(50));
	}

	/**
	 * Write the given key/value pair to Redis an set the expiration time if defined.
	 *
	 * @param name The cache name must not be {@literal null}.
	 * @param key The key for the cache entry. Must not be {@literal null}.
	 * @param hashes The hashes stored for the key. Must not be {@literal null}.
	 * @param ttl Optional expiration time. Can be {@literal null}.
	 */
	void put(String name, byte[] key, Map<byte[], byte[]> hashes, @Nullable Duration ttl);

	/**
	 * Get the binary value representation from Redis stored for the given key.
	 *
	 * @param name must not be {@literal null}.
	 * @param key must not be {@literal null}.
	 * @return {@literal null} if key does not exist.
	 */
	@Nullable
	Map<byte[], byte[]> get(String name, byte[] key);

	/**
	 * Write the given value to Redis if the key does not already exist.
	 *
	 * @param name The cache name must not be {@literal null}.
	 * @param key The key for the cache entry. Must not be {@literal null}.
	 * @param hashes The hashes stored for the key. Must not be {@literal null}.
	 * @param ttl Optional expiration time. Can be {@literal null}.
	 * @return {@literal null} if the value has been written, the value stored for the key if it already exists.
	 */
	@Nullable
	Map<byte[], byte[]> putIfAbsent(String name, byte[] key, Map<byte[], byte[]> hashes, @Nullable Duration ttl);

	/**
	 * Remove the given key from Redis.
	 *
	 * @param name The cache name must not be {@literal null}.
	 * @param key The key for the cache entry. Must not be {@literal null}.
	 */
	void remove(String name, byte[] key);

	/**
	 * Remove all keys following the given pattern.
	 *
	 * @param name The cache name must not be {@literal null}.
	 * @param pattern The pattern for the keys to remove. Must not be {@literal null}.
	 */
	void clean(String name, byte[] pattern);
}
