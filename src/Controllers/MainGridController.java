package Controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import Containers.FileItemContainer;
import Core.*;
import Models.FilesModel;
import db.DB;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

public class MainGridController extends CController {

	private ArrayList<Integer> IdsByIndex = new ArrayList<Integer>();
	private Desktop desktop = Desktop.getDesktop();
	private Map<Integer, FileItemContainer> files;
	private int selectedTreeItemId = 1;
	BorderPane border;
	GridPane mainGrid;

	private int ItemsSellectedPage = 0;
	private int ItemsPerPage = 10;
	private int ItemsPages = 0;
	private Pagination pagination = new Pagination();
	private AnchorPane mainAncorPane = new AnchorPane();

	final ObservableList<FileItemContainer> data = FXCollections.observableArrayList();

	private Node createDataTable(int pageIndex) {
		ItemsSellectedPage = pageIndex;
		pagination.setCurrentPageIndex(pageIndex);
		return new BorderPane(getPlaneOfFiles());
	}

	public void run() {

		border = new BorderPane();
		HBox hbox = addHBox();
		border.setTop(hbox);

		border.setLeft(getTreeCategory());
		// border.setRight(getTreeCategory());

		pagination.setPageFactory(this::createDataTable);

		AnchorPane.setTopAnchor(pagination, 10.0);
		AnchorPane.setRightAnchor(pagination, 10.0);
		AnchorPane.setBottomAnchor(pagination, 10.0);
		AnchorPane.setLeftAnchor(pagination, 10.0);
		mainAncorPane.getChildren().addAll(pagination);
		border.setCenter(mainAncorPane);

		if (scene == null)
			scene = new Scene(border, 800, 600);

		primaryStage.setTitle("MainGrid:" + CUser.getFIO());
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.show();
		primaryStage.setX(50);
		primaryStage.setY(50);
	}

	@SuppressWarnings("rawtypes")
	public TreeTableView<MyTreeNote> getTreeCategory() {
		// Node rootIcon = new ImageView(new
		// Image(getClass().getResourceAsStream("folder_16.png")));

		final TreeItem<MyTreeNote> root = new TreeItem<>(new MyTreeNote(0, "Root"));
		root.setExpanded(true);

		for (Map.Entry entry : getCategoryChild(0).entrySet()) {
			TreeItem<MyTreeNote> firstElementh = new TreeItem<MyTreeNote>((MyTreeNote) entry.getValue());
			firstElementh.setExpanded(false);
			for (Map.Entry entryDeeper : getCategoryChild((int) entry.getKey()).entrySet()) {
				IdsByIndex.add((int) entryDeeper.getKey());
				TreeItem<MyTreeNote> item = new TreeItem<MyTreeNote>((MyTreeNote) entryDeeper.getValue());
				firstElementh.getChildren().add(item);
			}
			root.getChildren().add(firstElementh);
		}

		TreeTableColumn<MyTreeNote, String> column = new TreeTableColumn<>("Column");
		column.setPrefWidth(150);

		column.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<MyTreeNote, String> param) -> new ReadOnlyStringWrapper(
						param.getValue().getValue().getTitle()));

		final TreeTableView<MyTreeNote> treeTableView = new TreeTableView<>(root);
		treeTableView.getColumns().add(column);
		treeTableView.setPrefWidth(152);
		treeTableView.setShowRoot(true);

		treeTableView.setRowFactory(tv -> {
			TreeTableRow<MyTreeNote> row = new TreeTableRow<>();
			row.setOnMouseClicked(event -> {
				if ((!row.isEmpty()) && selectedTreeItemId != row.getItem().getId()) {
					selectedTreeItemId = row.getItem().getId();
					System.out.println("SELECTEEED:"+selectedTreeItemId);
					createDataTable(0);
					mainAncorPane.getChildren().add(pagination);
					//mainAncorPane.clearConstraints(tv);
					border.setCenter(mainAncorPane);
				}
			});
			return row;
		});

		return treeTableView;
	}

	private Map<Integer, MyTreeNote> getCategoryChild(int parent) {
		ResultSet resSet = DB.exSelect("select * from categories where parent = " + parent);
		Map<Integer, MyTreeNote> Result = new HashMap<Integer, MyTreeNote>();
		try {
			while (resSet.next()) {
				MyTreeNote data = new MyTreeNote(resSet.getInt("id"), resSet.getString("title"));
				Result.put(Integer.valueOf(resSet.getString("id")), data);
			}
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Result;
	}

	private void openFile(File file) {
		try {
			desktop.open(file);
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
					if (!isHavePremissionToAddFile(CUser.getId())) {
						return;
					}
					if (selectedTreeItemId > 0) {

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
						 * 0 - файл не открыт 1 - файл не того формата 2 -
						 * сработала отмена
						 */
						int isOpenedFile = 0;
						do {
							File file = fileChooser.showOpenDialog(primaryStage);
							if (file != null) {
								CApplication.LastFilePath = file.getParent();
								String extension = FilesModel.getExt(file.getName());
								if (!CValidations.isGoodExt(selectedTreeItemId, extension)) {
									isOpenedFile = 0;
									continue;
								} else {
									isOpenedFile = 1;
								}
								String newFileTitle = CApplication.getTimestampString() + "." + extension;
								File dest = new File(
										FilesModel.createFolderIsNotExist("//data//" + CUser.getId() + "//")
												+ newFileTitle);

								try {
									copyFileUsingJava7Files(file, dest);

									SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

									PreparedStatement stmt = DB.conn.prepareStatement(
											"INSERT INTO files (author, category,title,originalTitle,originalExt,size,date) VALUES (?,?,?,?,?,?,?);");

									stmt.setInt(1, CUser.getId());
									stmt.setInt(2, selectedTreeItemId);
									stmt.setString(3, newFileTitle);
									stmt.setString(4, file.getName());
									stmt.setString(5, extension);
									stmt.setInt(6, (int) file.length());
									stmt.setString(7, ft.format(new Date()));
									stmt.execute();

									border.setCenter(createDataTable(isOpenedFile));
								} catch (IOException e1) {
									e1.printStackTrace();
								} catch (SQLException e1) {
									e1.printStackTrace();
								}

							} else {
								isOpenedFile = 1;
							}
						} while (isOpenedFile == 0);

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

	private static void copyFileUsingJava7Files(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath());
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

			Integer start = (this.ItemsSellectedPage) * this.ItemsPerPage;
			
			
			
			if (!CUserRules.get("Actions.FullAccessToFiles")) {		
				FilesCount = FilesModel.getFilesByConditions("COUNT(*) as count",selectedTreeItemId,CUser.getId(),(Core.ConditionType)null,-1,-1).getInt("count");
				this.files = FilesModel.getFilesMapByConditions("f.*",selectedTreeItemId,-1,(Core.ConditionType)null,start, this.ItemsPerPage);
			} else {
				FilesCount = FilesModel.getFilesByConditions("COUNT(*) as count",selectedTreeItemId,-1,(Core.ConditionType)null,-1,-1).getInt("count");
				this.files = FilesModel.getFilesMapByConditions("f.*",selectedTreeItemId,-1,(Core.ConditionType)null,start, this.ItemsPerPage);
			}
			this.ItemsPages = (FilesCount - 1) / this.ItemsPerPage + 1;
			pagination.setPageCount(ItemsPages);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.clear();
		for (Map.Entry file : this.files.entrySet()) {
			FileItemContainer item = (FileItemContainer) file.getValue();

			Image image = null;

			if (item.getIcon().equals("IMAGE")) {
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

	private boolean isHavePremissionToAddFile(int userId) {
		if (FilesModel.getUserUpoadSize2day(userId) > 10240000 / 2)
			return false;
		return true;
	}

	 public class MyTreeNote {

	        private SimpleStringProperty name;
	        private SimpleIntegerProperty id;
	        private MyTreeNote(int id, String title) {
	            this.id = new SimpleIntegerProperty(id);
	            this.name = new SimpleStringProperty(title);
	        }
	        public Integer getId() {
	            return id.get();
	        }
	        public String getTitle() {
	            return name.get();
	        }
	    }
	 
	 
	

}
