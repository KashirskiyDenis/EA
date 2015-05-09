package ru.library_2.entities;

public class BookOnHand {

	private int id;
	private int idBook;
	private String title;
	private String fio;
	private String dIn;
	private double penalty;

	public BookOnHand(int id, int idBook, String title, String fio, String dIn,
			double penalty) {
		this.id = id;
		this.idBook = idBook;
		this.title = title;
		this.fio = fio;
		this.dIn = dIn;
		this.penalty = penalty;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdBook() {
		return idBook;
	}

	public void setIdBook(int idBook) {
		this.idBook = idBook;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getdIn() {
		return dIn;
	}

	public void setdIn(String dIn) {
		this.dIn = dIn;
	}

	public double getPenalty() {
		return penalty;
	}

	public void setPenalty(double penalty) {
		this.penalty = penalty;
	}

}
