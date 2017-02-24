import java.sql.SQLException;

import Core.CApplication;
import Core.CUserRules;
import Models.FilesModel;
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
		beforeStartApplication();

		launch();

		DB.exSelect("select * from Users");
		DB.CloseDB();
	}

	private static void beforeStartApplication() throws ClassNotFoundException, SQLException {
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
