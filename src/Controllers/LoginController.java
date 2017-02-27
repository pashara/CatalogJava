package Controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Core.CApplication;
import Core.CController;
import Core.CUser;
import Core.MD5;
import Models.FilesModel;
import db.DB;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class LoginController extends CController {
	public void run() {
		primaryStage.setResizable(false);
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 330, 150);

		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(5));
		gridpane.setHgap(5);
		gridpane.setVgap(5);
		gridpane.setAlignment(Pos.CENTER);
		// gridpane.setGridLinesVisible(true);
		for (int i = 0; i < 2; i++) {
			ColumnConstraints column = new ColumnConstraints();
			column.setPercentWidth(100 / 3.0);
			gridpane.getColumnConstraints().add(column);
		}
		Label lLogin = new Label("Логин");
		Label lPassword = new Label("Пароль");
		Label lRemember = new Label("Запомнить");
		// CheckBox chRemember = new CheckBox();
		TextField fLogin = new TextField();
		fLogin.setPromptText("Введите логин");
		PasswordField fPassword = new PasswordField();
		fPassword.setPromptText("Введите пароль");

		Button bLogin = new Button("Войти");
		Button bLoginGuest = new Button("Войти как гость");
		Text ErrorText = new Text();

		gridpane.add(lLogin, 0, 0);
		gridpane.add(lPassword, 0, 1);
		gridpane.add(lRemember, 0, 2);
		gridpane.add(fLogin, 1, 0, 2, 1);
		gridpane.add(fPassword, 1, 1, 2, 1);
		gridpane.add(bLogin, 2, 2);
		gridpane.add(bLoginGuest, 2, 3);

		// gridpane.add(chRemember, 1, 2);
		gridpane.add(ErrorText, 0, 3, 2, 1);

		bLogin.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				try {

					PreparedStatement stmt = DB.conn
							.prepareStatement("SELECT count(*) as COUNT FROM users WHERE username=? AND password=?");
					stmt.setString(1, fLogin.getText());
					stmt.setString(2, MD5.grnerate(fPassword.getText()));
					ResultSet rs = stmt.executeQuery();

					if (rs.getInt("COUNT") == 1) {
						fPassword.setText("");
						CUser.loginByUsername(fLogin.getText());
						CController gridController = new MainGridController();
						gridController.setPrevScene(primaryStage);
						gridController.run();
					} else {
						ErrorText.setText("Неправильный логин или пароль");
					}

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});

		bLoginGuest.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				CUser.setUserGuest();
				CController gridController = new MainGridController();
				gridController.setPrevScene(primaryStage);
				gridController.run();

			}
		});

		fLogin.textProperty().addListener((observable, oldValue, newValue) -> {
			ErrorText.setText("");
		});
		fPassword.textProperty().addListener((observable, oldValue, newValue) -> {
			ErrorText.setText("");
		});
		root.setCenter(gridpane);
		primaryStage.setScene(scene);
		primaryStage.show();

		
		CApplication.daysBeetwen();
		
		 
		CUser.loginByUsername("admin");
		CController gridController = new MainGridController();
		gridController.setPrevScene(primaryStage);
		gridController.run();
		
		
		/*
		 * System.out.println("11"+CUserRules.get(0, "MainGrid"));
		 * System.out.println("11"+CUserRules.get(1, "MainGrid"));
		 * System.out.println("11"+CUserRules.get(2, "MainGrid"));
		 * 
		 * /* primaryStage.setResizable(false); BorderPane root = new
		 * BorderPane();
		 * 
		 * GridPane grid = new GridPane(); grid.setHgap(10); grid.setVgap(10);
		 * grid.setPadding(new Insets(0, 10, 0, 10));
		 * 
		 * for (int i = 0; i < 2; i++) {
		 * 
		 * ColumnConstraints column = new ColumnConstraints();
		 * 
		 * column.setPercentWidth(100 / 3.0);
		 * 
		 * grid.getColumnConstraints().add(column);
		 * 
		 * } Label txtLogin = new Label("Логин"); grid.add(txtLogin, 1, 0);
		 * 
		 * root.setCenter(grid); primaryStage.setTitle("Hello World!");
		 * primaryStage.setScene(scene); primaryStage.show(); /*
		 * 
		 * Button btn = new Button(); btn.setText("Say 'Hello World'");
		 * btn.setOnAction(new EventHandler<ActionEvent>() {
		 * 
		 * @Override public void handle(ActionEvent event) {
		 * btn.setText("Clicked"); System.out.println("Hello World!");
		 * CController gridController = new MainGridController();
		 * gridController.setPrevScene(primaryStage); gridController.run(); }
		 * });
		 * 
		 * StackPane root = new StackPane(); root.getChildren().add(btn);
		 * if(scene == null) scene = new Scene(root, 300, 250);
		 * 
		 * 
		 * primaryStage.setTitle("Hello World!"); primaryStage.setScene(scene);
		 * primaryStage.show();
		 */
	}
}
