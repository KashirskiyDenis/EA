package ru.library_2.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import ru.library_2.entities.*;

@ManagedBean(name = "dbBean")
@ApplicationScoped
public class DataBase {

	private static volatile Connection connectionOracle = null;
	private static volatile Connection connectionMySQl = null;

	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connectionOracle = DriverManager.getConnection(
					"jdbc:oracle:thin:@192.168.100.49:1521:STUDENT", "IT15_12",
					"123456");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connectionMySQl = DriverManager.getConnection(
					"jdbc:mysql://db4free.net:3306/entapp", "waka", "123qwe");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
/*
		if (connectionMySQl != null && connectionOracle != null) {
			Timer time = new Timer();
			ScheduledTask task = new ScheduledTask();
			long step = 21600000;
			time.schedule(task, 0, step);
		}
		*/
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

	public User getUserInfo(String fio) throws Exception {
		String tmp[] = fio.split(" ");
		String sql = "SELECT kod_sotr, name_dolgn, name_otdel, date_rab, salary, uvolen "
				+ "FROM SOTR, OTDEL, DOLGN "
				+ "WHERE SOTR.kod_dolgn = DOLGN.kod_dolgn AND "
				+ "SOTR.kod_otdel = OTDEL.kod_otdel AND "
				+ "UPPER(SOTR.fam) = ? AND UPPER(SOTR.name) = ? AND UPPER(SOTR.otch) = ?";
		PreparedStatement pstatement = connectionOracle.prepareStatement(sql);
		pstatement.setString(1, tmp[0]);
		pstatement.setString(2, tmp[1]);
		pstatement.setString(3, tmp[2]);
		ResultSet result = pstatement.executeQuery();
		result.next();

		int id = result.getInt("kod_sotr");
		String date_rab = result.getString("date_rab");
		String dolgn = result.getString("name_dolgn");
		String otdel = result.getString("name_otdel");
		String uvolen = result.getString("uvolen").equalsIgnoreCase("н") ? "Работает"
				: "Уволен";
		double salary = result.getDouble("salary");

		User user = new User(id, fio, date_rab, dolgn, otdel, uvolen, salary);
		return user;
	}

	public List<BookOnHand> getBooksOnHand() throws Exception {
		String sql = "SELECT bookonhands.id as id, title, fio, date_in, penalty "
				+ "FROM bookonhands, reader, book "
				+ "WHERE bookonhands.idreader = reader.id AND "
				+ "bookonhands.idbook = book.id AND " + "status = 0";
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
				+ "FROM bookonhands, reader, book "
				+ "WHERE bookonhands.idreader = reader.id AND "
				+ "bookonhands.idbook = book.id AND "
				+ "status = 0 AND idreader = ?";
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

	public void takeBook(String fio, String title, String dOut, String dIn)
			throws Exception {
		int idReader = getIdReaderByFio(fio);
		if (idReader == -1)
			return;
		int idBook = getIdBookByTitle(title);
		if (idBook == -1)
			return;
		if (!bookINnStorage(idBook))
			return;

		String sql = "UPDATE book SET amount = amount - 1 WHERE id = ?";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, idBook);
		pstatment.executeQuery();

		sql = "INSERT INTO bookonhands "
				+ "(idbook, idreader, date_out, date_in) "
				+ "VALUES (?, ?, ?, ?)";
		pstatment = connectionMySQl.prepareStatement(sql);
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

	public String getFioById(int id) throws Exception {
		String sql = "SELECT fio FROM reader WHERE id = ?";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, id);
		ResultSet result = pstatment.executeQuery();
		result.next();

		return result.getString("fio").toUpperCase();
	}

	public void returnBook(int id) throws Exception {
		String sql = "UPDATE TABLE bookonhands SET status = 1 "
				+ "WHERE id = ?";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, id);
		pstatment.executeQuery();
	}

	public boolean bookINnStorage(int id) throws Exception {
		String sql = "SELECT amount FROM book WHERE amount > 0 AND id = ?";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, id);
		ResultSet result = pstatment.executeQuery();
		if (result.next())
			return true;
		return false;
	}

	public String getDateReturn(int id) throws Exception {
		String sql = "SELECT date_in FROM bookohheands WHERE idBook = ? ORDER BY date_in";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, id);
		ResultSet result = pstatment.executeQuery();
		result.next();
		return result.getString("data_in");
	}
	/*
	public List<String> getFioFromOracle() throws Exception {
		String sql = "SELECT UPPER(fam || ' ' || name || ' ' || otch) as fio FROM sotr WHERE uvolen = 'Н'";
		PreparedStatement pstatment = connectionOracle.prepareStatement(sql);
		ResultSet result = pstatment.executeQuery();
		List<String> list = new ArrayList<String>();

		while (result.next()) {
			String tmp = result.getString("fio");
			System.out.println(tmp);
			list.add(result.getString("fio").toUpperCase());
		}
		return list;
	}

	public List<String> getFioFromOracleUvolen() throws Exception {
		String sql = "SELECT UPPER(fam || ' ' || name || ' ' || otch) as fio FROM sotr WHERE uvolen = 'Д'";
		PreparedStatement pstatment = connectionOracle.prepareStatement(sql);
		ResultSet result = pstatment.executeQuery();
		List<String> list = new ArrayList<String>();

		while (result.next()) {
			list.add(result.getString("fio").toString().toUpperCase());
		}
		return list;
	}

	public List<String> getFioFromMySQL() throws Exception {
		String sql = "SELECT UPPER(fio) as fio FROM reader";
		PreparedStatement pstatment = connectionOracle.prepareStatement(sql);
		ResultSet result = pstatment.executeQuery();
		List<String> list = new ArrayList<String>();

		while (result.next()) {
			list.add(result.getString("fio").toString().toUpperCase());
		}
		return list;
	}

	public void addNewReader(String fio, String login, String password)
			throws Exception {
		String sql = "INSERT INTO reader (fio, login, password) VALUES (?, ?, ?, ?)";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setString(1, fio);
		pstatment.setString(2, login);
		pstatment.setString(3, password);
		pstatment.executeQuery();
	}

	public void deleteReader(String fio) throws Exception {
		int id = getIdReaderByFio(fio);

		String sql = "DELETE FROM reader WHERE id = ? AND "
				+ "id NOT IN (SELECT idREader FROM bookonhands WHERE status = 0)";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, id);
		pstatment.executeQuery();
	}
	*/
}
