package org.abx.model;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alex
 */
@Entity
@Table(name="cb_users")
@XmlRootElement
@NamedQueries({
@NamedQuery(name="User.findAll", query="SELECT c FROM User c"),
@NamedQuery(name = "User.findByCredentials", query = "SELECT c FROM User c WHERE c.username = :userName and c.password = :password")})

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
	@SequenceGenerator(name="CB_USERS_USERNAME_GENERATOR", sequenceName="USERNAME")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CB_USERS_USERNAME_GENERATOR")
	private String username;

	private String password;
	
	
	private String textPassword1;
	private String textPassword2;

	public User() {
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
	public String getTextPassword1() {
		return this.textPassword1;
	}

	public void setTextPassword1(String password1) {
		this.textPassword1 = password1;
	}

	public String getTextPassword2() {
		return this.textPassword2;
	}

	public void setTextPassword2(String password2) {
		this.textPassword2 = password2;
	}

}