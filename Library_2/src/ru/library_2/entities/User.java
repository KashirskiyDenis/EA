package ru.library_2.entities;

import java.util.ArrayList;
import java.util.List;

public class User {

	private int id;
	private String fio;
	private String date_rab;
	private String dolgn;
	private String otdel;
	private String uvolen;
	private double salary;
	private List<BookOnHand> listOfBooks = new ArrayList<BookOnHand>();

	public User(int id, String fio, String date_rab, String dolgn,
			String otdel, String uvolen, double salary) {
		this.id = id;
		this.fio = fio;
		this.date_rab = date_rab;
		this.dolgn = dolgn;
		this.otdel = otdel;
		this.uvolen = uvolen;
		this.salary = salary;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate_rab() {
		return date_rab;
	}

	public void setDate_rab(String date_rab) {
		this.date_rab = date_rab;
	}

	public String getDolgn() {
		return dolgn;
	}

	public void setDolgn(String dolgn) {
		this.dolgn = dolgn;
	}

	public String getOtdel() {
		return otdel;
	}

	public void setOtdel(String otdel) {
		this.otdel = otdel;
	}

	public String getUvolen() {
		return uvolen;
	}

	public void setUvolen(String uvolen) {
		this.uvolen = uvolen;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public List<BookOnHand> getListOfBooks() {
		return listOfBooks;
	}

	public void setListOfBooks(List<BookOnHand> listOfBooks) {
		this.listOfBooks = listOfBooks;
	}

	@Override
	public String toString() {
		return "Id = " + id +
				"\nfio = " + fio +
				"\ndate_rab = " + date_rab +
				"\ndolgn = " + dolgn +
				"\notdel = " + otdel +
				"\nuvolen = " + uvolen +
				"\nsalary = " + salary;
	}
}
