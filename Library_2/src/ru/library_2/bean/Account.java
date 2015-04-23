package ru.library_2.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import ru.library_2.entities.*;

@ManagedBean
@SessionScoped
public class Account {

	@ManagedProperty(value = "#{dbBean}")
	private DataBase db;

	private String login;
	private String password;

	private User user;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setDb(DataBase db) {
		this.db = db;
	}

	public String signIn() {
		try {
			int id = db.checkUser(login, password);
			System.out.println(id);
			if (id != -1) {
				String fio = db.getFioById(id);
				user = db.getUserInfo(fio);
				user.setListOfBooks(db.getBooksOnHand(id));
				return "pageOfUser";
			}
			return null;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

}
