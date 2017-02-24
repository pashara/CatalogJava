package Core;

import java.time.ZonedDateTime;

import Controllers.LoginController;
import javafx.stage.Stage;

public class CApplication {
	public static Stage stage;
	public static String LastFilePath = "NULL";

	public static String getTimestampString() {
		return new String(String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli()));
	}

	public static void run() {
		CController loginForm = new LoginController();
		loginForm.run();
	}

}
