package Core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DB;

public class CUser {
	private static String username;
	private static String FIO;
	private static int UserType;
	private static int id;

	public static void loginByUsername(String login) {
		try {
			PreparedStatement stmt = DB.conn
					.prepareStatement("SELECT id,username, rights,FIO FROM users WHERE username=?");
			stmt.setString(1, login);
			ResultSet rs = stmt.executeQuery();
			CUser.username = rs.getString("username");
			CUser.FIO = rs.getString("FIO");
			CUser.UserType = rs.getInt("rights");
			CUser.id = rs.getInt("id");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getUsername() {
		return CUser.username;
	}

	public static int getId() {
		if (UserType == 0)
			return 0;
		return CUser.id;
	}

	public static int getUserStatus() {
		return CUser.UserType;
	}

	public static String getFIO() {
		return CUser.FIO;
	}

	public static void setUserGuest() {
		CUser.username = "";
		CUser.FIO = "Гость";
		CUser.UserType = 0;
	}
}
