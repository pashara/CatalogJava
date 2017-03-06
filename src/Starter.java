import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import Core.CApplication;
import Core.CUserRules;
import Models.FilesModel;
import Models.MailModel;
import db.DB;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Admin
 *
 */
public class Starter extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		try {
			beforeStartApplication();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		launch();

		//DB.exSelect("select * from Users");
		DB.CloseDB();
	}

	private static void beforeStartApplication() throws ClassNotFoundException, SQLException, InvocationTargetException {
		DB.Conn();
		CUserRules.init();

		FilesModel.createFolderIsNotExist(FilesModel.dataDir);

	}

	@Override
	public void start(Stage primaryStage) {
		CApplication.stage = primaryStage;
		CApplication.run();
	}

}
