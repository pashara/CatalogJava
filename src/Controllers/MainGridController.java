package Controllers;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Core.CController;
import Core.CUser;
import Core.CUserRules;
import db.DB;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;











import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;




public class MainGridController extends CController {
	
	private ArrayList<Integer> IdsByIndex = new ArrayList<Integer>();
	
	
	
	public void run(){

		
		BorderPane border = new BorderPane();
		HBox hbox = addHBox();
		border.setTop(hbox);
		try {
			border.setLeft(getTreeCategory());
			border.setRight(getTreeCategory());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		border.setCenter(addGridPane());
		//border.setRight(addFlowPane());

		
		
		
		if(scene == null)
			scene = new Scene(border, 800, 600);
		

		primaryStage.setTitle("MainGrid:"+CUser.getFIO());
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.show();
		primaryStage.setX(50);
		primaryStage.setY(50);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	public TreeView<MyTreeNote> getTreeCategory() throws ClassNotFoundException, SQLException {

	//	Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("folder_16.png")));
		
		final TreeItem<MyTreeNote> rootItem = new TreeItem<MyTreeNote>(new MyTreeNote(1,"Main"));
		rootItem.setExpanded(true);
		
		for (Map.Entry entry : getCategoryChild(0).entrySet()) {
			IdsByIndex.add((int) entry.getKey());
			TreeItem<MyTreeNote> firstElementh = new TreeItem<MyTreeNote>((MyTreeNote) entry.getValue());
			firstElementh.setExpanded(false);
			for (Map.Entry entryDeeper : getCategoryChild((int) entry.getKey()).entrySet()) {
				IdsByIndex.add((int) entryDeeper.getKey());
				TreeItem<MyTreeNote> item = new TreeItem<MyTreeNote>((MyTreeNote)entryDeeper.getValue());
				firstElementh.getChildren().add(item);
			}
			rootItem.getChildren().add(firstElementh);
		}		
		
		
		//tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
		//	//System.out.println(observable);
		//	//MyTreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();
		//	MyTreeItem<String> selectedItem = (MyTreeItem<String>) tree.getSelectionModel().getSelectedItem();
		//	System.out.println(selectedItem);
		//	/*int index = selectedItem.getParent().getChildren().indexOf(selectedItem);
		//	System.out.println(newValue);
		//	System.out.println(IdsByIndex.get(index));
		//	System.out.println(index);
		//});
		
		
		
		TreeView<MyTreeNote> tree = new TreeView<MyTreeNote>(rootItem);
		
		
		
		tree.setCellFactory(tv ->  {
	        TreeCell<MyTreeNote> cell = new TreeCell<MyTreeNote>() {
	        	 @Override
	        	 protected void updateItem(MyTreeNote item, boolean empty) {
	        	     super.updateItem(item, empty);

	        	     if (empty || item == null) {
	        	         setText(null);
	        	         setGraphic(null);
	        	     } else {
	        	         setText(item.toString());
	        	     }
	        	 }
	        };
	        cell.setOnMouseClicked(e -> {
	            if (e.getClickCount() == 2 && ! cell.isEmpty()) {
	            	MyTreeNote file = cell.getItem();
	            	System.out.println(file.getId());
	            	System.out.println("11"+CUserRules.get(0, "MainGrid"));
	            	System.out.println("11"+CUserRules.get(1, "MainGrid"));
	            	System.out.println("11"+CUserRules.get(2, "MainGrid"));
	            }
	        });
	        return cell ;
	    });
		
		return tree;
	}
	
	
	
	
	

	private Map<Integer, MyTreeNote> getCategoryChild(int parent) throws ClassNotFoundException, SQLException {
		ResultSet resSet = DB.exSelect("select * from categories where parent = "+parent);
		Map<Integer, MyTreeNote> Result = new HashMap<Integer, MyTreeNote>();
		while(resSet.next())
		{
			MyTreeNote data = new MyTreeNote(resSet.getInt("id"), resSet.getString("title"));
			Result.put(Integer.valueOf(resSet.getString("id")),data);
		}
		return Result;
	}
	
	
	private Desktop desktop = Desktop.getDesktop();
	
	private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
            		MainGridController.class.getName()).log(
                    Level.SEVERE, null, ex
                );
        }
    }
	
	
	
	public HBox addHBox() {
	    HBox hbox = new HBox();
	    hbox.setPadding(new Insets(15, 12, 15, 12));
	    hbox.setSpacing(10);
	    hbox.setStyle("-fx-background-color: #336699;");

	    Button bMain = new Button("Главная");
	    bMain.setPrefSize(100, 20);
	    hbox.getChildren().add(bMain);
	    
	    if(CUserRules.get("Actions.FullAdd") ){
		    Button bCreate = new Button("Добавить");
		    bCreate.setPrefSize(100, 20);
		    hbox.getChildren().add(bCreate);
		    
		    
		    
		    bCreate.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					FileChooser fileChooser = new FileChooser();
				    fileChooser.setTitle("Open Resource File");
				    
					
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        openFile(file);
                    }
                    
                    
					
				}
			});
		    
	   }
	    
	    
	    
	    
	    Button bExit = new Button("Выйти");
	    bExit.setPrefSize(100, 20);
	    hbox.getChildren().add(bExit);

	    
	    bExit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				CController loginController = new LoginController();
				loginController.setPrevScene(primaryStage);
				loginController.run();

			}
		});
	    
	    return hbox;
	}
	


	
	
	
	public GridPane addGridPane() {
	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(0, 10, 0, 10));

	    // Category in column 2, row 1
	    Text category = new Text("Sales:");
	    category.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    grid.add(category, 1, 0); 

	    // Title in column 3, row 1
	    Text chartTitle = new Text("Current Year");
	    chartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    grid.add(chartTitle, 2, 0);

	    // Subtitle in columns 2-3, row 2
	    Text chartSubtitle = new Text("Goods and Services");
	    grid.add(chartSubtitle, 1, 1, 2, 1);

	   
	    // Left label in column 1 (bottom), row 3
	    Text goodsPercent = new Text("Goods\n80%");
	    GridPane.setValignment(goodsPercent, VPos.BOTTOM);
	    grid.add(goodsPercent, 0, 2); 

	    // Right label in column 4 (top), row 3
	    Text servicesPercent = new Text("Services\n20%");
	    GridPane.setValignment(servicesPercent, VPos.TOP);
	    grid.add(servicesPercent, 3, 2);

	    return grid;
	}
	
	
	
	
	
	
	
	
	public class MyTreeNote{
		private String title;
		private Integer id;
		public MyTreeNote(int id, String title){
			this.title = title;
			this.id = id;
		}
		public String toString(){
			return this.title;
		}
		public Integer getId(){
			return id;
		}
		public String getTitle(){
			return this.title;
		}
	}
	
	
	
}
