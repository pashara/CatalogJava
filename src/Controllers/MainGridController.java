package Controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import Containers.FileItemContainer;
import Core.CApplication;
import Core.CController;
import Core.CUser;
import Core.CUserRules;
import Core.CValidations;
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
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class MainGridController extends CController {

	private Map<Integer, FileItemContainer> files;
	private int selectedCategoryId = 1;
	private int itemsSellectedPage = 0;
	private int itemsPerPage = 10;
	private int itemsPages = 0;
	private BorderPane border;
	private Pagination pagination = new Pagination();
	private AnchorPane mainAncorPane = new AnchorPane();
	final ObservableList<FileItemContainer> data = FXCollections.observableArrayList();

	private Node createDataTable(int pageIndex) {
		itemsSellectedPage = pageIndex;
		pagination.setCurrentPageIndex(pageIndex);
		return new BorderPane(getPlaneOfFiles());
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
		column.setPercentWidth(100);
		grid.getColumnConstraints().add(column);
		HBox hbox = addHBox();
		border.setTop(hbox);

		border.setLeft(MainGridModel.getTreeCategory(this));
		updateData(); // Load data
		AnchorPane.setTopAnchor(pagination, 10.0);
		AnchorPane.setRightAnchor(pagination, 10.0);
		AnchorPane.setBottomAnchor(pagination, 10.0);
		AnchorPane.setLeftAnchor(pagination, 10.0);
		mainAncorPane.getChildren().addAll(pagination);
		

		TextField fSearch = new TextField();
		grid.add(fSearch, 0,0);
		grid.add(mainAncorPane, 0,1);
		border.setCenter(grid);
		if (scene == null)
			scene = new Scene(border, 800, 600);

		primaryStage.setTitle("MainGrid:" + CUser.getFIO());
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
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

					MainGridModel.insertAndCoppyFile(file, dest, selectedCategoryId, newFileTitle);
					updateData();

				} else {
					isOpenedFile = 1;
				}
			} while (isOpenedFile == 0);

		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TableView getPlaneOfFiles() {

		TableView<FileItemContainer> table = new TableView<FileItemContainer>();
		TableColumn iconCol = new TableColumn();
		TableColumn titleCol = new TableColumn("Имя файла");
		TableColumn editCol = new TableColumn();

		table.setRowFactory(tv -> {
			TableRow<FileItemContainer> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					openFile(new File(FilesModel.getPathToFile(row.getItem().getAuthorId())));
				}
			});
			return row;
		});

		int FilesCount;
		try {

			Integer start = (this.itemsSellectedPage) * this.itemsPerPage;
			if (!CUserRules.get("Actions.FullAccessToFiles")) {
				FilesCount = FilesModel.getFilesByConditions("COUNT(*) as count", selectedCategoryId, CUser.getId(),
						(Core.ConditionType) null, -1, -1).getInt("count");
				this.files = FilesModel.getFilesMapByConditions("f.*", selectedCategoryId, CUser.getId(),
						(Core.ConditionType) null, start, this.itemsPerPage);
			} else {
				int iid = -1;
				FilesCount = FilesModel.getFilesByConditions("COUNT(*) as count", selectedCategoryId, iid,
						(Core.ConditionType) null, -1, -1).getInt("count");
				this.files = FilesModel.getFilesMapByConditions("f.*", selectedCategoryId, iid,
						(Core.ConditionType) null, start, this.itemsPerPage);
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
			Image image = null;
			if (item.getIcon() == null) {
				image = new Image("file:" + FilesModel.getSystemPathToFile("//"));
			} else if (item.getIcon().equals("IMAGE")) {
				image = new Image("file:" + FilesModel.getPathToFile(item.getFileId()));
			} else {
				image = new Image("file:" + FilesModel.getSystemPathToFile(item.getIcon()));
			}
			ImageView iv2 = new ImageView();
			iv2.setImage(image);
			iv2.setFitWidth(100);
			iv2.setFitHeight(100);
			iv2.setPreserveRatio(true);
			iv2.setSmooth(true);
			iv2.setCache(true);
			item.setRawIcon(iv2);
			data.add(item);

			iconCol.setCellValueFactory(new PropertyValueFactory<FileItemContainer, ImageView>("rawIcon"));
			iconCol.setMinWidth(100);
			iconCol.setResizable(false);
			iconCol.setSortable(false);
			iconCol.setPrefWidth(100);

			titleCol.setCellValueFactory(new PropertyValueFactory<FileItemContainer, String>("title"));

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
