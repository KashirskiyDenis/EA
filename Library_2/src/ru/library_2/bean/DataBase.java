package ru.library_2.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import ru.library_2.entities.*;

@ManagedBean(name = "dbBean")
@ApplicationScoped
public class DataBase {

	private static volatile Connection connectionOracle = null;
	private static volatile Connection connectionMySQl = null;

	static {
		/*
		 * try { Class.forName("oracle.jdbc.driver.OracleDriver");
		 * connectionOracle = DriverManager.getConnection(
		 * "jdbc:oracle:thin:@192.168.100.49:1521:STUDENT", "IT15_12",
		 * "123456"); } catch (ClassNotFoundException | SQLException e) {
		 * System.out.println(e.getMessage()); }
		 */
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connectionMySQl = DriverManager.getConnection(
					"jdbc:mysql://db4free.net:3306/entapp", "waka", "123qwe");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int checkUser(String login, String password) throws Exception {
		String sql = "SELECT id FROM reader WHERE login=? AND password=?";
		PreparedStatement pstatement = connectionMySQl.prepareStatement(sql);
		pstatement.setString(1, login);
		pstatement.setString(2, password);
		ResultSet result = pstatement.executeQuery();
		if (result.next())
			return result.getInt("id");
		else
			return -1;
	}

	public User findUser(int id) throws Exception {
		String sql = "SELECT fam, name, otch, name_dolgn, name_otdel, date_rab, salary, uvolen "
				+ "FROM SORT, OTDEL, DOLGN "
				+ "WHERE SORT.kod_dolgn = DOLGN.kod_dolgn AND "
				+ "SORT.kod_otdel = OTDEL.kod_otdel AND " + "SOTR.kod_sotr = ?";
		PreparedStatement pstatement = connectionOracle.prepareStatement(sql);
		pstatement.setInt(1, id);
		ResultSet result = pstatement.executeQuery();
		result.next();

		String fio = result.getString("fam") + " " + result.getString("name")
				+ " " + result.getString("otch");
		String date_rab = result.getString("date_rab");
		String dolgn = result.getString("dolgn");
		String otdel = result.getString("otdel");
		String uvolen = result.getString("uvolen").equalsIgnoreCase("н") ? "Работает"
				: "Уволен";
		double salary = result.getDouble("salary");

		User user = new User(id, fio, date_rab, dolgn, otdel, uvolen, salary);
		return user;
	}

	public List<BookOnHand> getBooksOnHand() throws Exception {
		String sql = "SELECT bookonhands.id as id, title, fio, date_in, penalty "
				+ "FROM bookonhands " + "WHERE return = 0";

		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		ResultSet result = pstatment.executeQuery();
		List<BookOnHand> list = new ArrayList<BookOnHand>();
		while (result.next()) {
			int idRec = result.getInt("id");
			String title = result.getString("title");
			String fio = result.getString("fio");
			String dIn = result.getString("date_in");
			double penalty = result.getDouble("penalty");
			list.add(new BookOnHand(idRec, title, fio, dIn, penalty));
		}

		return list;
	}

	public List<BookOnHand> getBooksOnHand(int id) throws Exception {
		String sql = "SELECT bookonhands.id as id, title, fio, date_in, penalty "
				+ "FROM bookonhands WHERE status = 0 AND idreader = ?";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, id);
		ResultSet result = pstatment.executeQuery();
		List<BookOnHand> list = new ArrayList<BookOnHand>();
		while (result.next()) {
			int idRec = result.getInt("id");
			String title = result.getString("title");
			String fio = result.getString("fio");
			String dIn = result.getString("date_in");
			double penalty = result.getDouble("penalty");
			list.add(new BookOnHand(idRec, title, fio, dIn, penalty));
		}

		return list;
	}

	public void takeBook(String fio, String title, String dOut, String dIn) throws Exception {
		int idReader = getIdReaderByFio(fio);
		if (idReader == -1)
			return;
		int idBook = getIdBookByTitle(title);
		if (idBook == -1)
			return;
		
		String sql = "INSERT INTO bookonhands "
				+ "(idbook, idreader, date_out, date_in) "
				+ "VALUES (?, ?, ?, ?)";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, idBook);
		pstatment.setInt(2, idReader);
		pstatment.setString(3, dOut);
		pstatment.setString(4, dIn);
		pstatment.executeQuery();
	}
	
	public int getIdReaderByFio(String fio) throws Exception {
		String sql = "SELECT id FROM reader WHERE fio = ?";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setString(1, fio);
		ResultSet result = pstatment.executeQuery();
		if (result.next())
			return result.getInt("id");
		return -1;
	}

	public int getIdBookByTitle(String title) throws Exception {
		String sql = "SELECT id FROM book WHERE title = ?";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setString(1, title);
		ResultSet result = pstatment.executeQuery();
		if (result.next())
			return result.getInt("id");
		return -1;
	}
	
	public void returnBook(int id) throws Exception {
		String sql = "UPDATE TABLE bookonhands SET status = 1 "
				+ "WHERE id = ?";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, id);
		pstatment.executeQuery();
	}
	
	private void synchronize() {
		
	}
}
