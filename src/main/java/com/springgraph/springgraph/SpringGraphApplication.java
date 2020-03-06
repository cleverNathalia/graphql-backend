package com.springgraph.springgraph;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;


@SpringBootApplication
public class SpringGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphApplication.class, args);

	}

	@Autowired
	PeopleService peopleService;

	@PostConstruct
	public void dummy(){

		peopleService.createPeople(1234, "Jane Johnson", "1 Koos street", "0928377464");
		peopleService.createPeople(4567, "John Doe", "", "");
	}

}

//----------ENTITY

@Entity
 class People implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "address", nullable = false)
	private String address;
	@Column(name = "phone")
	private String phone;

	//----------GETTERS & SETTERS


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}

//----------REPOSITORY / INTERFACE

@Repository
 interface PeopleRepository extends JpaRepository<People, Integer> {
}

//----------QUERY

@Component
 class PeopleQuery implements GraphQLQueryResolver {
	@Autowired
	private PeopleService peopleService;

	public List<People> peoples(final int count) {
		return this.peopleService.getAllPeople(count);
	}

	public Optional<People> people(final int id) {

			return this.peopleService.getPeople(id);

	}

	public Optional<People> getPerson(String role){
		Optional<People> person;

		if(role.equals("admin")){
			person = this.peopleService.getPeople(1);
		}else{
			person = this.peopleService.getPeople(2);
		}

		return person;
	}

}

//----------MUTATION

@Component
 class PeopleMutation implements GraphQLMutationResolver {
	@Autowired
	private PeopleService peopleService;
	public People createPeople(final int id, final String name, final String address, final String phone) {
		return this.peopleService.createPeople(id, name, address, phone);
	}
}

//----------SERVICE

@Service
 class PeopleService {

	private final PeopleRepository peopleRepository ;

	public PeopleService(final PeopleRepository peopleRepository) {
		this.peopleRepository = peopleRepository ;
	}
	@Transactional
	public People createPeople(final int id, final String name, final String address, final String phone) {
		final People people = new People();
		people.setId(id);
		people.setName(name);
		people.setAddress(address);
		people.setPhone(phone);
		return this.peopleRepository.save(people);
	}
	@Transactional(readOnly = true)
	public List<People> getAllPeople(final int count) {
		return this.peopleRepository.findAll();
	}
	@Transactional(readOnly = true)
	public Optional<People> getPeople(final int id) {
		return this.peopleRepository.findById(id);
	}

}
