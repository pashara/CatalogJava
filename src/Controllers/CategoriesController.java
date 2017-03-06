package Controllers;

import Containers.FileItemContainer;
import Containers.MyTreeNoteContainer;
import Core.CController;
import Core.CUser;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CategoriesController extends CController {
	private MyTreeNoteContainer data = null;
	private String errorText = "";

	
	public MyTreeNoteContainer getCategoryObject(){
		return data;
	}
	public void run() {
		// Создаём диалоговое окно Stage.
		Stage dialogStage = new Stage();
		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(5));
		gridpane.setHgap(5);
		gridpane.setVgap(5);
		gridpane.setAlignment(Pos.CENTER);
		// gridpane.setGridLinesVisible(true);
		for (int i = 0; i < 2; i++) {
			ColumnConstraints column = new ColumnConstraints();
			column.setPercentWidth(100 / 2.0);
			gridpane.getColumnConstraints().add(column);
		}
		Label lTitle = new Label("Название *");
		TextField fTitle = new TextField();
		fTitle.setPromptText("Введите название категории");
		fTitle.setText(data.getTitle());

		Label lAllowedTypes = new Label("Разрешенные форматы");
		TextField fAllowedTypes = new TextField();
		fAllowedTypes.setPromptText("Введите через запятую без пробелов разрешенные форматы");
		fAllowedTypes.setText(data.getAllowedTypes() + "");

		Label lDeniedTypes = new Label("Запрещенные форматы");
		TextField fDeniedTypes = new TextField();
		fDeniedTypes.setPromptText("Введите через запятую без пробелов разрешенные форматы");
		fDeniedTypes.setText(data.getDeniedTypes() + "");

		Button bSave = new Button("Сохранить");

		Text ErrorText = new Text(errorText);

		gridpane.add(lTitle, 0, 0);
		gridpane.add(fTitle, 1, 0);

		gridpane.add(lAllowedTypes, 0, 1);
		gridpane.add(fAllowedTypes, 1, 1);

		gridpane.add(lDeniedTypes, 0, 2);
		gridpane.add(fDeniedTypes, 1, 2);

		gridpane.add(bSave, 0, 3);

		gridpane.add(ErrorText, 0, 4, 3, 1);

		dialogStage.setTitle("Редактирование категории:id:"+data.getId());
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(gridpane);
		dialogStage.setWidth(400);
		dialogStage.setHeight(200);
		dialogStage.setScene(scene);

		bSave.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				data.setTitle(fTitle.getText());
				data.setAllowedTypes(fAllowedTypes.getText());
				data.setDeniedTypes(fDeniedTypes.getText());
				if (validateData()) {
					data.save();
					dialogStage.close();
				} else {

				}
				ErrorText.setText(errorText);

			}
		});

		dialogStage.showAndWait();

	}

	private boolean validateData() {
		errorText = "";
		if (data.getTitle().length() < 3) {
			errorText += "Длина названия должна быть не менее 3 символов!\n";
		}

		if (errorText.length() != 0)
			return false;
		return true;
	}

	public void loadData(int id) {
		data = new MyTreeNoteContainer(id);
	}

}
