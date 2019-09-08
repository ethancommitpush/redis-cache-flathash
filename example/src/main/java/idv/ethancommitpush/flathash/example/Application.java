package idv.ethancommitpush.flathash.example;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import idv.ethancommitpush.flathash.example.model.Customer;
import idv.ethancommitpush.flathash.example.model.Privilege;
import idv.ethancommitpush.flathash.example.model.Product;
import idv.ethancommitpush.flathash.example.service.CustomerService;
import idv.ethancommitpush.flathash.example.service.ProductService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication // Include @Configuration @EnableAutoConfiguration @ComponentScan 三個註解
public class Application implements CommandLineRunner {

	@Autowired
	CustomerService customerService;
	@Autowired
	ProductService productService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		IntStream.range(0, 5)
        .forEach(i -> {
			Customer customer = new Customer();
			customer.setFirstName("FirstName" + (i + 1));
			customer.setLastName("LastName" + (i + 1));
			Privilege privilege = new Privilege();
			privilege.setId(1);
			privilege.setName("Normal");
			customer.setPrivilege(privilege);
			customerService.save(customer);
			Product product = new Product();
			product.setName("Name" + (i + 1));
			product.setDescription("Description" + (i + 1));
			productService.save(product);
        });

		IntStream.range(0, 5)
        .forEach(i -> {
			for (int j = 0; j < 5; j += 1) {
				log.info("call customerService.findById(" + (i + 1) + "): " + customerService.findById(i + 1));
			}
			for (int j = 0; j < 5; j += 1) {
				log.info("call productService.findById(" + (i + 1) + "): " + productService.findById(i + 1));
			}
        });
	}

}