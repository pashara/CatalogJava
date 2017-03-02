package Controllers;

import Containers.FileItemContainer;
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

public class FileController extends CController{
	private FileItemContainer data = null;
	private String errorText = "";
	
	
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
			column.setPercentWidth(100 / 3.0);
			gridpane.getColumnConstraints().add(column);
		}
		Label lTitle = new Label("Название");
		TextField fTitle = new TextField();
		fTitle.setPromptText("Введите название файла");
		fTitle.setText(data.getOriginalTitle());
		
		Label lCategory = new Label("Название");
		TextField fCategory = new TextField();
		fCategory.setPromptText("Введите id категории");
		fCategory.setText(data.getCategoryId()+"");
		data.generateRawIcon();
		
		Button bSave = new Button("Сохранить");


		Text ErrorText = new Text(errorText);

		gridpane.add(data.getRawIcon(), 0, 0,1,3);
		gridpane.add(lTitle, 1, 0);
		gridpane.add(fTitle, 2, 0);

		gridpane.add(lCategory, 1, 1);
		gridpane.add(fCategory, 2, 1);

		gridpane.add(bSave, 1, 2);

		// gridpane.add(chRemember, 1, 2);
		gridpane.add(ErrorText, 0, 3,3,1);
		

        dialogStage.setTitle("Edit Person");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(gridpane);
        dialogStage.setWidth(500);
        dialogStage.setHeight(300);
        dialogStage.setScene(scene);
        
        
        bSave.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				data.setOriginalTitle(fTitle.getText());
				if(validateData()){
					data.save();
					dialogStage.close();
				}else{
					
				}
				ErrorText.setText(errorText);

			}
		});
        
		
        // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
        dialogStage.showAndWait();
        
		
		
		
		
	}
	
	private boolean validateData(){
		errorText = "";
		if(data.getOriginalTitle().length() < 3){
			errorText+="Длина названия файла должна быть не менее 3 символов!\n";
		}
		
		if(errorText.length() != 0)
			return false;
		return true;
	}
	
	public void loadData(int id){
		data = new FileItemContainer(id);
	}
}
