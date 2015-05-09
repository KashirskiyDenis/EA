package ru.Library_2_Synchronization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

	private static Connection connectionOracle;
	private static Connection connectionMySQl;

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
	}

	public List<String> getFioFromOracle() throws Exception {
		String sql = "SELECT UPPER(fam || ' ' || name || ' ' || otch) AS FIO FROM sotr WHERE uvolen = 'Í'";
		PreparedStatement pstatment = connectionOracle.prepareStatement(sql);
		ResultSet result = pstatment.executeQuery();
		List<String> list = new ArrayList<String>();
		while (result.next()) {
			list.add(result.getString("FIO").toUpperCase());
		}
		return list;
	}

	public List<String> getFioFromOracleUvolen() throws Exception {
		String sql = "SELECT UPPER(fam || ' ' || name || ' ' || otch) AS FIO FROM sotr WHERE uvolen = 'Ä'";
		PreparedStatement pstatment = connectionOracle.prepareStatement(sql);
		ResultSet result = pstatment.executeQuery();
		List<String> list = new ArrayList<String>();
		while (result.next()) {
			list.add(result.getString("FIO").toString().toUpperCase());
		}
		return list;
	}

	public List<String> getFioFromMySQL() throws Exception {
		String sql = "SELECT UPPER(fio) AS fio FROM reader";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		ResultSet result = pstatment.executeQuery();
		List<String> list = new ArrayList<String>();
		while (result.next()) {
			list.add(result.getString("fio").toString().toUpperCase());
		}
		return list;
	}

	public void addNewReader(String fio, String login, String password)
			throws Exception {
		String sql = "INSERT INTO reader (fio, login, password) VALUES (?, ?, ?)";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setString(1, fio);
		pstatment.setString(2, login);
		pstatment.setString(3, password);
		pstatment.executeUpdate();
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

	public void deleteReader(String fio) throws Exception {
		int id = getIdReaderByFio(fio);

		String sql = "DELETE FROM reader WHERE id = ? AND "
				+ "id NOT IN (SELECT idREader FROM bookonhands WHERE status = 0)";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.setInt(1, id);
		pstatment.executeUpdate();
	}
	
	public void updatePenalty() throws Exception {
		String sql = "UPDATE bookonhands"
				+ "SET penalty = (SELECT price*0.003 FROM book WHERE id = idbook)"
				+ "WHERE date_in < CURDATE();";
		PreparedStatement pstatment = connectionMySQl.prepareStatement(sql);
		pstatment.executeUpdate();
	}
}
