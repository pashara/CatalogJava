package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	public static Connection conn;
	public static Statement statmt;
	public static ResultSet resSet;

	public static ResultSet exSelect(String Query) {
		try {
			return resSet = statmt.executeQuery(Query);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
	public static void Conn() throws ClassNotFoundException, SQLException {
		conn = null;
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:DataBase.s3db");
		statmt = conn.createStatement();
	}

	/*
	 * // --------Заполнение таблицы-------- public static void WriteDB() throws
	 * SQLException { statmt.
	 * execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Petya', 125453); "
	 * ); statmt.
	 * execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Vasya', 321789); "
	 * ); statmt.
	 * execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Masha', 456123); "
	 * );
	 * 
	 * } /* // -------- Вывод таблицы-------- public static void ReadDB() throws
	 * ClassNotFoundException, SQLException { resSet =
	 * statmt.executeQuery("SELECT * FROM Users");
	 * 
	 * System.out.println("OUT:"); while (resSet.next()) { int id =
	 * resSet.getInt("id"); String name = resSet.getString("username"); String
	 * phone = resSet.getString("username"); System.out.println("OUT:");
	 * System.out.println("ID = " + id); System.out.println("name = " + name);
	 * System.out.println("phone = " + phone); System.out.println(); }
	 * 
	 * System.out.println("Таблица выведена"); }
	 */

	// --------Закрытие--------
	public static void CloseDB() throws ClassNotFoundException, SQLException {
		conn.close();
		statmt.close();
		resSet.close();
	}

}
