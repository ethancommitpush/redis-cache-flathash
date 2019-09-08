package idv.ethancommitpush.flathash.example.model;

import lombok.Data;

@Data
public class Customer {
	private Integer id;
	private String firstName;
	private String lastName;
	private Privilege privilege;

}
