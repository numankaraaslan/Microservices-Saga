package com.aldimbilet.userservice.model;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "USER")
// At some point of the development i have decided to name this class with domainname (aldimbilet.com)
// a and b is the initials of the words "aldim" and "bilet"
// Later than i have realised it became "abuser" :D this has nothing to do with any kind of abuse :D
// In turkish, if you apply the same logic, this could also be Abuzer :)
public class ABUser
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "USERNAME", nullable = false, unique = true, length = 50)
	private String username;

	@Column(name = "PASSWORD", nullable = false, length = 200)
	private String password;

	private String passwordConfirm;

	@Column(name = "EMAIL", nullable = false, length = 100)
	private String email;

	@Column(name = "NAME", nullable = false, length = 50)
	private String name;

	@Column(name = "SURNAME", nullable = false, length = 50)
	private String surname;

	@Column(name = "REG_DATE", nullable = false)
	private Date regDate = new Date();

	@ManyToMany(fetch = FetchType.EAGER)
	private List<Role> roles;
}