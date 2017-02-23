package Core;

import Controllers.LoginController;
import javafx.stage.Stage;

public class CApplication {
	public static Stage stage;
	
	public static void run(){
		CController loginForm = new LoginController();
		loginForm.run();
	}
	
}
