package idv.ethancommitpush.flathash.example.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import idv.ethancommitpush.flathash.example.model.Order;

@Service
public class OrderService {

	private AtomicInteger idInc = new AtomicInteger(0);
	private Map<Integer, Order> mockRepostory = new HashMap<Integer, Order>();

	public Order save(Order order) {
		Integer id = idInc.incrementAndGet();
		order.setId(id);
		mockRepostory.put(id, order);
		return order;
	}

	public Order findById(Integer id) {
		Order order = mockRepostory.get(id);
		return order;
	}
}