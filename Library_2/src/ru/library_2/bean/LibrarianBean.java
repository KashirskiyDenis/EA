package ru.library_2.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import ru.library_2.entities.*;

@ManagedBean(name="librarianBean")
@SessionScoped
public class LibrarianBean {
	
	@ManagedProperty(value = "#{dbBean}")
	private DataBase db;
	
	private List<BookOnHand> listBookOnHand = new ArrayList<BookOnHand>();
	private String fio;
	private String title;
	private String dOut;
	private String dIn;

	public List<BookOnHand> getListBookOnHand() {
		return listBookOnHand;
	}

	public void setListBookOnHand(List<BookOnHand> listBookOnHand) {
		this.listBookOnHand = listBookOnHand;
	}

	public void setDb(DataBase db) {
		this.db = db;
		try {
			this.listBookOnHand = db.getBooksOnHand();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getdOut() {
		return dOut;
	}

	public void setdOut(String dOut) {
		this.dOut = dOut;
	}

	public String getdIn() {
		return dIn;
	}

	public void setdIn(String dIn) {
		this.dIn = dIn;
	}

	public String takeBook() {
		try {
			db.takeBook(fio, title, dOut, dIn);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		this.fio = "";
		this.title = "";
		this.dOut = "";
		this.dIn = "";
		return "pageOfLibrarian";
	}

	public String returnBook() {
		
		return null;
	}
}
