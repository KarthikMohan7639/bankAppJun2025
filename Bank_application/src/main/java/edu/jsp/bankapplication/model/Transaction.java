package edu.jsp.bankapplication.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	private double amount;
	private LocalDateTime date;
	private String type; //CREDIT,DEBIT
	
	@ManyToOne
	@JoinColumn
	private   Account account;
	
	
	
	
	

}
