package Controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import Containers.FileItemContainer;
import Containers.MyTreeNoteContainer;
import Core.CApplication;
import Core.CController;
import Core.CUser;
import Core.CUserRules;
import Core.CValidations;
import Core.ConditionType;
import Models.FilesModel;
import Models.MainGridModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class MainGridController extends CController {

	private Map<Integer, FileItemContainer> files;
	private int selectedCategoryId = 6;
	private int itemsSellectedPage = 0;
	private int itemsPerPage = 5;
	private int itemsPages = 0;
	private String searchedText = "";
	private BorderPane border;
	private Pagination pagination = new Pagination();
	private AnchorPane mainAncorPane = new AnchorPane();
	final ObservableList<FileItemContainer> data = FXCollections.observableArrayList();

	private Node createDataTable(int pageIndex) {
		itemsSellectedPage = pageIndex;
		pagination.setCurrentPageIndex(pageIndex);
		return new BorderPane(getCenterTable());
	}

	
	public void updateData() {
		pagination.setPageFactory(this::createDataTable);
	}

	public void run() {
		border = new BorderPane();
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(5);
		grid.setVgap(5);
		ColumnConstraints column = new ColumnConstraints();
		column.setPercentWidth(80);
		grid.getColumnConstraints().add(column);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(20);
		grid.getColumnConstraints().add(column1);
		HBox hbox = addHBox();
		border.setTop(hbox);

		border.setLeft(MainGridModel.getTreeCategory(this));
		updateData(); // Load data
		AnchorPane.setTopAnchor(pagination, 0.0);
		AnchorPane.setRightAnchor(pagination, 0.0);
		AnchorPane.setBottomAnchor(pagination, 0.0);
		AnchorPane.setLeftAnchor(pagination, 0.0);
		mainAncorPane.getChildren().addAll(pagination);
		

		TextField fSearch = new TextField();
		fSearch.setPromptText("Введите строку для поиска");
		grid.add(fSearch, 0,0);
		Button bSearch = new Button("Искать");
		bSearch.setPrefWidth(900);
		grid.add(bSearch, 1,0);
		
		
		bSearch.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				searchedText = new String(fSearch.getText());
				updateData();
			}
		});
		
		
		fSearch.setPadding(new Insets(5));
		grid.add(mainAncorPane, 0,1,2,1);
		border.setCenter(grid);
		if (scene == null)
			scene = new Scene(border, 800, 600);

		primaryStage.setTitle("MainGrid:" + CUser.getFIO());
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);

		primaryStage.setMinWidth(500);
		primaryStage.setMinHeight(400);
		primaryStage.show();
		primaryStage.setX(50);
		primaryStage.setY(50);
	}

	private void openFile(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException ex) {
			Logger.getLogger(MainGridController.class.getName()).log(Level.SEVERE, null, ex);
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

		if (CUserRules.get("Actions.FullAdd")) {
			Button bCreate = new Button("Добавить");
			bCreate.setPrefSize(100, 20);
			hbox.getChildren().add(bCreate);
			bCreate.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					addButtonHandler();
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

	private void addButtonHandler() {
		if (!isHavePremissionToAddFile(CUser.getId())) {
			return;
		}
		if (selectedCategoryId > 0) {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Открыть файл1");
			if (CApplication.LastFilePath.equals("NULL")) {
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			} else {
				fileChooser.setInitialDirectory(new File(CApplication.LastFilePath));
			}
			// fileChooser.setSelectedExtensionFilter(new
			// FileChooser.ExtensionFilter("AllFormatss", "xls"));

			/*
			 * 0 - файл не открыт 1 - файл не того формата 2 - сработала отмена
			 */
			int isOpenedFile = 0;
			do {
				File file = fileChooser.showOpenDialog(primaryStage);
				if (file != null) {
					CApplication.LastFilePath = file.getParent();
					String extension = FilesModel.getExt(file.getName());
					if (!CValidations.isGoodExt(selectedCategoryId, extension)) {
						isOpenedFile = 0;
						continue;
					} else {
						isOpenedFile = 1;
					}
					String newFileTitle = CApplication.getTimestampString() + "." + extension;
					File dest = new File(
							FilesModel.createFolderIsNotExist("//data//" + CUser.getId() + "//") + newFileTitle);

					MainGridModel.insertAndCoppyFile(file, dest, selectedCategoryId, newFileTitle,extension);
					updateData();
				} else {
					isOpenedFile = 1;
				}
			} while (isOpenedFile == 0);

		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TableView getCenterTable() {
		TableView<FileItemContainer> table = new TableView<FileItemContainer>();
		TableColumn iconCol = new TableColumn();
		TableColumn titleCol = new TableColumn("Имя файла");
		TableColumn editCol = new TableColumn();

		table.setRowFactory(tv -> {

			
			
			final ContextMenu contextMenu = new ContextMenu();
			MenuItem item1 = new MenuItem("Редактировать файл");
			MenuItem item2 = new MenuItem("Удалить файл");

			contextMenu.getItems().addAll(item1, item2);

			item2.setOnAction(new EventHandler<ActionEvent>() {
			    public void handle(ActionEvent e) {
			    	Alert alert = new Alert(AlertType.CONFIRMATION);
			    	alert.setTitle("Подтверждение удаления файла");
			    	alert.setHeaderText("Подтвердите удаление файла");
			    	alert.setContentText("Вы действительно хотите удалить данный файл?");

			    	Optional<ButtonType> result = alert.showAndWait();
			    	if (result.get() == ButtonType.OK){
			    		FileItemContainer item = table.getSelectionModel().getSelectedItem();
				    	item.delete();
				    	updateData();
			    	}
			    }
			});
			

			item1.setOnAction(new EventHandler<ActionEvent>() {
			    public void handle(ActionEvent e) {
			    	FileController editFile = new FileController();
					editFile.loadData(table.getSelectionModel().getSelectedItem().getFileId());
					editFile.run();
					updateData();
			    }
			});
			
			
			
			TableRow<FileItemContainer> row = new TableRow<FileItemContainer>() {
				@Override
				public void updateItem(FileItemContainer item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setContextMenu(null);
					} else {
						setContextMenu(contextMenu);
					}
				}
			};
			
			
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					openFile(new File(FilesModel.getPathToFile(row.getItem().getFileId())));
				}
			});
			
			
			
			
			return row;
		});

		int FilesCount;
		try {
			
			ConditionType<String> SearchCondition = null;
			if(!this.searchedText.isEmpty())
				SearchCondition = new ConditionType<String>(" f.originalTitle LIKE ?","%"+this.searchedText+"%",2," AND ");
			Integer start = (this.itemsSellectedPage) * this.itemsPerPage;
			if (!CUserRules.get("Actions.FullAccessToFiles")) {
				FilesCount = FilesModel.getFilesByConditions("COUNT(*) as count", FilesModel.getConditionINCategories(selectedCategoryId), CUser.getId(),
						SearchCondition, -1, -1).getInt("count");
				this.files = FilesModel.getFilesMapByConditions("f.*", FilesModel.getConditionINCategories(selectedCategoryId), CUser.getId(),
						SearchCondition, start, this.itemsPerPage);
			} else {
				int iid = -1;
				FilesCount = FilesModel.getFilesByConditions("COUNT(*) as count", FilesModel.getConditionINCategories(selectedCategoryId), iid,
						SearchCondition, -1, -1).getInt("count");
				this.files = FilesModel.getFilesMapByConditions("f.*", FilesModel.getConditionINCategories(selectedCategoryId), iid,
						SearchCondition, start, this.itemsPerPage);
			}
			this.itemsPages = (FilesCount - 1) / this.itemsPerPage + 1;
			pagination.setPageCount(itemsPages);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		data.clear();
		for (Map.Entry file : this.files.entrySet()) {
			FileItemContainer item = (FileItemContainer) file.getValue();
			item.generateRawIcon();
			data.add(item);

			iconCol.setCellValueFactory(new PropertyValueFactory<FileItemContainer, ImageView>("rawIcon"));
			iconCol.setMinWidth(100);
			iconCol.setResizable(false);
			iconCol.setSortable(false);
			iconCol.setPrefWidth(100);

			titleCol.setCellValueFactory(new PropertyValueFactory<FileItemContainer, String>("originalTitle"));

			titleCol.setMinWidth(20);

			editCol.setCellValueFactory(new PropertyValueFactory<FileItemContainer, String>("authorId"));

			editCol.setMinWidth(160);

		}

		table.getColumns().addAll(iconCol, titleCol, editCol);
		table.setItems(FXCollections.observableArrayList(data));

		return table;
	}

	public void setSelectedTreeItemId(int i) {
		this.selectedCategoryId = i;
	}

	public int getSelectedTreeItemId() {
		return this.selectedCategoryId;
	}

	private boolean isHavePremissionToAddFile(int userId) {
		/*
		 * if (FilesModel.getUserUpoadSize2day(userId) > 10240000 / 2) return
		 * false;
		 */
		return true;
	}
	
	
	
	
	
	
	

}
