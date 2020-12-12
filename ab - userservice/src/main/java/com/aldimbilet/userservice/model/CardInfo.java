package com.aldimbilet.userservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CARDINFO")
public class CardInfo
{
	@Id
	@Column(name = "USER_ID", nullable = false)
	private Long userId;

	@Column(name = "CARDNUMBER", nullable = false, unique = true, length = 50)
	private String cardNumber;
}
