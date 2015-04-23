package ru.Library_2_Synchronization;

import java.util.List;
import java.util.Random;

public class Program {

	private DataBase db;
	private List<String> listFioFromOracle;
	private List<String> listFioFromMySQL;

	public Program() {
		db = new DataBase();
	}

	public void synchronization() {
		try {
			listFioFromOracle = db.getFioFromOracle();
			if (listFioFromOracle == null)
				System.out.println("listFioFromOracle is null --> 1");

			listFioFromMySQL = db.getFioFromMySQL();
			if (listFioFromOracle == null)
				System.out.println("listFioFromOracle is null");

			for (String fio : listFioFromOracle) {
				if (!listFioFromMySQL.contains(fio)) {
					String login = Transliterator.transliterate(fio);
					String password = getNewPassword();
					try {
						db.addNewReader(fio, login, password);
					} catch (Exception e) {
						System.out.println(e.getMessage()
								+ " can't insert".toUpperCase());
					}
				}
			}

			listFioFromOracle = db.getFioFromOracleUvolen();
			if (listFioFromOracle == null)
				System.out.println("listFioFromOracle is null --> 2");

			for (String fio : listFioFromOracle) {
				if (listFioFromMySQL.contains(fio))
					try {
						db.deleteReader(fio);
					} catch (Exception e) {
						System.out.println(e.getMessage()
								+ " can't delete".toUpperCase());
					}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String getNewPassword() {
		StringBuilder password = new StringBuilder();
		Random rnd = new Random();
		for (int i = 0; i < 8; i++) {
			password.append(rnd.nextInt(10));
		}

		return password.toString();
	}
}
