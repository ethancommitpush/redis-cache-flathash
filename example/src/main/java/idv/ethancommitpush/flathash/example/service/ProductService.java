package idv.ethancommitpush.flathash.example.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import idv.ethancommitpush.flathash.example.annotation.CacheExpire;
import idv.ethancommitpush.flathash.example.model.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService {

	private AtomicInteger idInc = new AtomicInteger(0);
	private Map<Integer, Product> mockRepostory = new HashMap<Integer, Product>();

	public Product save(Product product) {
		Integer id = idInc.incrementAndGet();
		product.setId(id);
		mockRepostory.put(id, product);
		return product;
	}

	@Cacheable(value="springCache", key="'product:' + #id", unless = "#result == null")
	@CacheExpire(expire = 60)
	public Product findById(Integer id) {
		log.info("inside findById(" + id + ")");
		Product product = mockRepostory.get(id);
		return product;
	}
}