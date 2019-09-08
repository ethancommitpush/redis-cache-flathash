package idv.ethancommitpush.flathash.example.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Resolve multiple caches with resolveCaches method
 */
public class MultiCacheResolver implements CacheResolver, InitializingBean {

	@Nullable
	private List<CacheManager> cacheManagers;

	public MultiCacheResolver(){
    }

	public MultiCacheResolver(List<CacheManager> cacheManagers){
        this.cacheManagers = cacheManagers;
    }

	public void setCacheManagerList(@Nullable List<CacheManager> cacheManagers) {
		this.cacheManagers = cacheManagers;
	}

	public List<CacheManager> getCacheManagers() {
		return cacheManagers;
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(this.cacheManagers, "CacheManager is required");
	}

	/** Resolve multiple cacheManagers sequentially **/
	@Override
	public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
		Collection<String> cacheNames = getCacheNames(context);
		if (cacheNames == null) {
			return Collections.emptyList();
		}
		Collection<Cache> result = new ArrayList<>();
		getCacheManagers()
		.forEach(e -> {
			for (String cacheName : cacheNames) {
				Optional.ofNullable(e.getCache(cacheName))
				.filter(Objects::nonNull)
				.map(c -> result.add(c))
				.orElseThrow(() -> new IllegalArgumentException(
						"Cannot find cache named '" + cacheName + "' for " + context.getOperation()));
			}});
		return result;
	}

	private Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
		return context.getOperation().getCacheNames();
	}

}
