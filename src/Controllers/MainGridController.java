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
import java.util.List;
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
import db.DB;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class MainGridController extends CController {

	private ArrayList<Integer> IdsByIndex = new ArrayList<Integer>();
	private Desktop desktop = Desktop.getDesktop();
	private Map<Integer, FileItemContainer> files;
	private int selectedTreeItemId;
	BorderPane border;
	GridPane mainGrid;
	
	private int ItemsSellectedPage = 0;
	private int ItemsPerPage = 3;
	private int ItemsPages = 0;
	

	public void run() {

		border = new BorderPane();
		HBox hbox = addHBox();
		border.setTop(hbox);
		
		border.setLeft(getTreeCategory());
		//border.setRight(getTreeCategory());

		border.setCenter(addGridPane());
		//border.setRight(addGridPane());
		// border.setRight(addFlowPane());

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
	public TreeView<MyTreeNote> getTreeCategory() {

		// Node rootIcon = new ImageView(new
		// Image(getClass().getResourceAsStream("folder_16.png")));

		final TreeItem<MyTreeNote> rootItem = new TreeItem<MyTreeNote>(new MyTreeNote(1, "Main"));
		rootItem.setExpanded(true);

		for (Map.Entry entry : getCategoryChild(0).entrySet()) {
			IdsByIndex.add((int) entry.getKey());
			TreeItem<MyTreeNote> firstElementh = new TreeItem<MyTreeNote>((MyTreeNote) entry.getValue());
			firstElementh.setExpanded(false);
			for (Map.Entry entryDeeper : getCategoryChild((int) entry.getKey()).entrySet()) {
				IdsByIndex.add((int) entryDeeper.getKey());
				TreeItem<MyTreeNote> item = new TreeItem<MyTreeNote>((MyTreeNote) entryDeeper.getValue());
				firstElementh.getChildren().add(item);
			}
			rootItem.getChildren().add(firstElementh);
		}

		TreeView<MyTreeNote> tree = new TreeView<MyTreeNote>(rootItem);

		tree.setCellFactory(tv -> {
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
				if (e.getClickCount() == 2 && !cell.isEmpty()) {
					MyTreeNote file = cell.getItem();

					selectedTreeItemId = file.getId();
					/*
					 * System.out.println(file.getId());
					 * System.out.println("11"+CUserRules.get(0, "MainGrid"));
					 * System.out.println("11"+CUserRules.get(1, "MainGrid"));
					 * System.out.println("11"+CUserRules.get(2, "MainGrid"));
					 */
				}
			});
			return cell;
		});

		return tree;
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
					if(!isHavePremissionToAddFile(CUser.getId())){
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
						//fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("AllFormatss", "xls"));
						
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

									SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy hh:mm:ss");
									
									
									PreparedStatement stmt = DB.conn.prepareStatement(
											"INSERT INTO files (author, category,title,originalTitle,originalExt,size,date) VALUES (?,?,?,?,?,?,?);");

									stmt.setInt(1, CUser.getId());
									stmt.setInt(2, selectedTreeItemId);
									stmt.setString(3, newFileTitle);
									stmt.setString(4, file.getName());
									stmt.setString(5, extension);
									stmt.setInt(6, (int)file.length());
									stmt.setString(7,ft.format(new Date()));
									stmt.execute();

									border.setCenter(addGridPane());
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

	public AnchorPane addGridPane() {
		mainGrid = new GridPane();
		mainGrid.setHgap(10);
		mainGrid.setVgap(10);
		mainGrid.setGridLinesVisible(true);
		mainGrid.setPadding(new Insets(0, 0, 0, 0));
		getPlaneOfFiles();

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(100);
		mainGrid.getColumnConstraints().add(column1);
		
		
		
		
		
		AnchorPane anchor = new AnchorPane();
		Pagination pagination = new Pagination(this.ItemsPages, this.ItemsSellectedPage);
		pagination.setPageFactory(new Callback<Integer, Node>() {
			 
            public Node call(Integer pageIndex) {
                if (pageIndex >= ItemsPages) {
                    return null;
                } else {
                	ItemsSellectedPage = pageIndex;
                	System.out.println();
                    return getPlaneOfFiles();
                }
            }
        });
        anchor.getChildren().addAll(pagination);
        mainGrid.add(anchor, 0, 1);
        return anchor;
		//return mainGrid;
	}
	
	private FlowPane getPlaneOfFiles(){

		
		int FilesCount;
		try {

			Integer start = (this.ItemsSellectedPage) * this.ItemsPerPage;
			if (!CUserRules.get("Actions.FullAccessToFiles")) {
				FilesCount = FilesModel.getFilesCount(CUser.getId());
				this.files = FilesModel.getFilesByAuthor(CUser.getId(), start, this.ItemsPerPage);
			} else {
				FilesCount = FilesModel.getAllFilesCount();
				this.files = FilesModel.getAllFiles(start, this.ItemsPerPage);
			}
			this.ItemsPages = (FilesCount - 1) / this.ItemsPerPage + 1;
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		FlowPane PhotosPlane = new FlowPane();
		
		PhotosPlane.setBorder(new Border(new BorderStroke(Color.BLACK, 
	           BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		PhotosPlane.setHgap(10);
		PhotosPlane.setVgap(10);
		
		
		for (Map.Entry file : this.files.entrySet()) {
			FileItemContainer item = (FileItemContainer) file.getValue();	
			item.getFileId();
			Image image = new Image("file:" + FilesModel.getPathToFile(item.getFileId()));
			ImageView iv2 = new ImageView();
			iv2.setImage(image);
			iv2.setFitWidth(200);
			iv2.setFitHeight(100);
			iv2.setPreserveRatio(false);
			iv2.setSmooth(true);
			iv2.setCache(true);
			iv2.setId("image_mains_" + item.getFileId());
			iv2.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
					openFile(new File(FilesModel.getPathToFile(iv2.getId().replaceAll("image_mains_", ""))));
				}
			});
			PhotosPlane.getChildren().add(iv2);	
		}
		return PhotosPlane;
	}
	private boolean isHavePremissionToAddFile(int userId){
		if(FilesModel.getUserUpoadSize2day(userId) > 10240000/2)
			return false;
		return true;
	}

	public class MyTreeNote {
		private String title;
		private Integer id;

		public MyTreeNote(int id, String title) {
			this.title = title;
			this.id = id;
		}

		public String toString() {
			return this.title;
		}

		public Integer getId() {
			return id;
		}

		public String getTitle() {
			return this.title;
		}
	}

}
