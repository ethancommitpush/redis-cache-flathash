package idv.ethancommitpush.flathash.example.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import idv.ethancommitpush.flathash.example.annotation.CacheExpire;
import idv.ethancommitpush.flathash.example.model.Customer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {

	private AtomicInteger idInc = new AtomicInteger(0);
	private Map<Integer, Customer> mockRepostory = new HashMap<Integer, Customer>();

	public Customer save(Customer customer) {
		Integer id = idInc.incrementAndGet();
		customer.setId(id);
		mockRepostory.put(id, customer);
		return customer;
	}

	/*
	 * #root.targetClass
	 * class hello.service.CustomerService
	 * #root.target
	 * hello.service.CustomerService@b18c4
	 * #root.methodName
	 * findById
	 * #root.args[0]
	 * 1
	 */
	@Cacheable(value="springCache", key="'customer:' + #id", unless = "#result == null")//result in redis key: springCache::customer:1
//	@Cacheable(value = "springCache", key="{#root.methodName, #id}", unless = "#result == null")//result in redis key: springCache::findById,1
//	@Cacheable(value = "springCache", unless = "#result == null")//result in redis key: springCache::CustomerService.findById:1
	@CacheExpire(expire = 60)
	public Customer findById(Integer id) {
		log.info("inside findById(" + id + ")");
		Customer customer = mockRepostory.get(id);
		return customer;
	}
}