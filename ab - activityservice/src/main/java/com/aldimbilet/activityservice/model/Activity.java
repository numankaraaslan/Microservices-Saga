package com.aldimbilet.activityservice.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ACTIVITY")
public class Activity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "NAME", nullable = false, length = 350)
	private String name;

	@Column(name = "ACT_DATE", nullable = false)
	private Date actDate = new Date();

	@Column(name = "NUM_OF_SEATS", nullable = false)
	private int numberOfSeats;
}
