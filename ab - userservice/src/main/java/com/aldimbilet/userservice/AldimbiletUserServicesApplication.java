package com.aldimbilet.userservice;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.aldimbilet.userservice.model.ABUser;
import com.aldimbilet.userservice.model.CardInfo;
import com.aldimbilet.userservice.model.Role;
import com.aldimbilet.userservice.repo.CardRepository;
import com.aldimbilet.userservice.repo.RoleRepository;
import com.aldimbilet.userservice.service.UserService;

@SpringBootApplication
public class AldimbiletUserServicesApplication
{
	@Autowired
	UserService userService;

	@Autowired
	CardRepository cardRepo;

	@Autowired
	RoleRepository roleRepository;

	public static void main(String[] args)
	{
		SpringApplication.run(AldimbiletUserServicesApplication.class, args);
	}

	@PostConstruct
	private void saveDefaultUser()
	{
		try
		{
			userService.findByUsername("user");
		}
		catch (Exception e)
		{
			Role role1 = new Role();
			role1.setName("ADMIN");
			roleRepository.save(role1);
			List<Role> roles = new ArrayList<>();
			roles.add(role1);
			ABUser user = new ABUser();
			user.setName("user");
			user.setUsername("user");
			user.setPassword("asd");
			user.setSurname("asd");
			user.setPasswordConfirm("asd");
			user.setEmail("user@email.com");
			user.setRoles(roles);
			userService.save(user);
		}
		try
		{
			cardRepo.findByUserId(userService.findByUsername("user").getId());
		}
		catch (Exception e)
		{
			CardInfo card = new CardInfo();
			card.setUserId((long) 1);
			card.setCardNumber("123123123123");
			cardRepo.save(card);
		}
	}
}
