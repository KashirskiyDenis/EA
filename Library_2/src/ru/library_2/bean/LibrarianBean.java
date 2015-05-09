package ru.library_2.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import ru.library_2.entities.*;

@ManagedBean(name = "librarianBean")
@SessionScoped
public class LibrarianBean {

	@ManagedProperty(value = "#{dbBean}")
	private DataBase db;

	private ArrayList<BookOnHand> listBookOnHand = new ArrayList<BookOnHand>();
	private String fio;
	private String title;
	private String dOut;
	private String dIn;
	private String message = "";

	public ArrayList<BookOnHand> getListBookOnHand() {
		return listBookOnHand;
	}

	public void setListBookOnHand(ArrayList<BookOnHand> listBookOnHand) {
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void returnBook(ActionEvent ae) {
		UIComponent component = ae.getComponent();
		Map<String, Object> params = component.getAttributes();
		String tmp =  (String) params.get("name");
		String att [] = tmp.split("-");
		try {
			int id = Integer.parseInt(att[0]);
			int idBook = Integer.parseInt(att[1]);
			db.returnBook(id);
			db.addAmountBook(idBook);
			this.listBookOnHand = db.getBooksOnHand();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public String takeBook() {
		if (message.length() != 0)
			return "pageOfLibrarian";
		try {
			db.takeBook(fio, title, dOut, dIn);
			this.listBookOnHand = db.getBooksOnHand();
			this.fio = null;
			this.title = null;
			this.dOut = null;
			this.dIn = null;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "pageOfLibrarian";
	}

	public void check() {
		message = "";		
		if (fio == null || title == null || dOut == null || dIn == null)
			return;

		Integer id = null;
		try {
			id = db.getIdReaderByFio(fio);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (id == null || id == -1) {
			message = "Пользователь не найден";
			return;
		}

		Integer idBook = null;
		try {
			idBook = db.getIdBookByTitle(title);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (idBook == -1 || idBook == null) {
			message = "Книга не найдена";
			return;
		}

		Boolean bool = null;
		try {
			bool = db.bookINnStorage(idBook);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (bool == null || !bool) {
			String date = null;
			try {
				date = db.getDateReturn(idBook);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			message = "Книги нет в хранилище. Примерная дата возврата: " + date;
			return;
		}
		Boolean value = null;
		try {
			value = db.checkRareBook(idBook);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (value) {
			long a = getDifferenceDateInDay(dOut, dIn);
			if (a > 7) {
				message = "Книга редкая. Максимальный срок выдачи 7 дней.";
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static long getDifferenceDateInDay(String date1, String date2) {
		if (date1.equals(date2))
			return 0;
		String mas1[] = date1.split("-");
		String mas2[] = date2.split("-");

		Date d1 = new Date(Integer.parseInt(mas1[0]),
				Integer.parseInt(mas1[1]) - 1, Integer.parseInt(mas1[2]));
		Date d2 = new Date(Integer.parseInt(mas2[0]),
				Integer.parseInt(mas2[1]) - 1, Integer.parseInt(mas2[2]));
		long col = d1.getTime() - d2.getTime();
		col = (long) col / 86400000;
		return Math.abs(col);
	}
}
