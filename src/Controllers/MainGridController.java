package Controllers;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

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
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class MainGridController extends CController {

	private Map<Integer, FileItemContainer> files;
	private int selectedCategoryId = 0;
	private int itemsSellectedPage = 0;
	private int itemsPerPage = 5;
	private int itemsPages = 0;
	private String searchedText = "";
	private BorderPane border;
	private Pagination pagination = new Pagination();
	private AnchorPane mainAncorPane = new AnchorPane();
	final ObservableList<FileItemContainer> data = FXCollections.observableArrayList();
	private TableView<FileItemContainer> filesTable;

	private enum CopyType {
		NULL, File, Category
	};

	CopyType coTy = CopyType.NULL;

	/* NotUpdated */
	private Node createDataTable(int pageIndex) {
		itemsSellectedPage = pageIndex;
		pagination.setCurrentPageIndex(pageIndex);
		return new BorderPane(getDataTable());
	}

	/* NotUpdated */
	public void updateData() {
		pagination.setPageFactory(this::createDataTable);
	}

	/* NotUpdated */
	public void updateCategories() {
		border.setLeft(getTreeCategoryData());
	}

	/* NotUpdated */
	public void run() {
		MainGridModel.controller = this;
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
		HBox hbox = renderHeader();
		border.setTop(hbox);

		this.updateCategories();
		updateData(); // Load data
		AnchorPane.setTopAnchor(pagination, 0.0);
		AnchorPane.setRightAnchor(pagination, 0.0);
		AnchorPane.setBottomAnchor(pagination, 0.0);
		AnchorPane.setLeftAnchor(pagination, 0.0);
		mainAncorPane.getChildren().addAll(pagination);

		TextField fSearch = new TextField();
		fSearch.setPromptText("Введите строку для поиска");
		grid.add(fSearch, 0, 0);
		Button bSearch = new Button("Искать");
		bSearch.setPrefWidth(900);
		grid.add(bSearch, 1, 0);

		bSearch.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				searchedText = new String(fSearch.getText());
				updateData();
			}
		});

		fSearch.setPadding(new Insets(5));
		grid.add(mainAncorPane, 0, 1, 2, 1);
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

	/* NotUpdated */
	public HBox renderHeader() {
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #336699;");

		if (CUserRules.get("Actions.FullAdd")) {
			Button bCreate = new Button("Добавить");
			bCreate.setPrefSize(100, 20);
			hbox.getChildren().add(bCreate);
			bCreate.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					addButtonHandler();
				}
			});
		} else {
			Button bCreate = new Button("Предложить");
			bCreate.setPrefSize(100, 20);
			hbox.getChildren().add(bCreate);
			bCreate.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					addButtonHandler();
				}
			});
		}

		Button bExit = new Button((CUser.getId() > 0) ? "Выйти" : "Войти");
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
		if (!FilesModel.isHavePremissionToAddFile(CUser.getId()))
			return;
		if (selectedCategoryId > 0) {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Открыть файл");
			boolean isOpenedFile = false;
			do {
				if (CApplication.LastFilePath.equals("NULL")) {
					fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
				} else {
					fileChooser.setInitialDirectory(new File(CApplication.LastFilePath));
				}
				File file = fileChooser.showOpenDialog(primaryStage);
				if (file != null) {
					CApplication.LastFilePath = file.getParent();
					String extension = FilesModel.getExt(file.getName());
					if (!CValidations.isGoodExt(selectedCategoryId, extension)) {
						isOpenedFile = false;
						CApplication.alertError("Неподходящее расширение!");
						continue;
					} else {
						isOpenedFile = true;
					}
					String newFileTitle = CApplication.getTimestampString() + "." + extension;
					File dest = new File(
							FilesModel.createFolderIsNotExist("//data//" + CUser.getId() + "//") + newFileTitle);
					if (!CUserRules.get("Actions.FullAdd")) {
						CApplication.tlsSender.setSubject("Добавление файла в каталог");
						CApplication.tlsSender.setText(
								"Я гость, и я хочу добавить данный файл в каталог! ID категории:" + selectedCategoryId);
						CApplication.tlsSender.setFromEmail("serial-i@ya.ru");
						CApplication.tlsSender.setToEmail("pashara1997@gmail.com");
						CApplication.tlsSender.setFileName(file.getName());
						CApplication.tlsSender.setFile(file.getPath());
						CApplication.tlsSender.start();
						CApplication.alert(
								"Загрузка файла началась. Не закрывайте приложение пока файл не отправится на сервер.");
					} else {

						MainGridModel.insertAndCoppyFile(file, dest, selectedCategoryId, newFileTitle, extension);
						updateData();
						/*
						 * CApplication.tlsSender.
						 * setSubject("Новый файл в каталоге");
						 * CApplication.tlsSender.
						 * setText("В каталог добавлен новый файл.");
						 * CApplication.tlsSender.setFromEmail("serial-i@ya.ru")
						 * ; CApplication.tlsSender.setToEmail(
						 * "pashara1997@gmail.com");
						 * CApplication.tlsSender.start();
						 */
					}

				} else {
					isOpenedFile = true;
				}
			} while (!isOpenedFile);

		}
	}

	/**
	 * Generate CategoryTreeBlock
	 * 
	 * @return TreeTableView<MyTreeNoteContainer>
	 */
	public TreeTableView<MyTreeNoteContainer> getTreeCategoryData() {
		final TreeItem<MyTreeNoteContainer> root = new TreeItem<>(new MyTreeNoteContainer(0, 0, "Все файлы"));
		root.setExpanded(true);
		MainGridModel.getTreeCategoryRecursive(root, 0);

		TreeTableColumn<MyTreeNoteContainer, String> column = new TreeTableColumn<>("Колонка");
		column.setPrefWidth(150);

		column.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<MyTreeNoteContainer, String> param) -> new ReadOnlyStringWrapper(
						param.getValue().getValue().getTitle()));

		final TreeTableView<MyTreeNoteContainer> treeTableView = new TreeTableView<>(root);
		treeTableView.getColumns().add(column);
		treeTableView.setPrefWidth(150);
		treeTableView.setShowRoot(true);

		treeTableView.setRowFactory(tv -> {
			final ContextMenu contextMenu = new ContextMenu();

			TreeTableRow<MyTreeNoteContainer> categoryRow = new TreeTableRow<MyTreeNoteContainer>() {
				@Override
				public void updateItem(MyTreeNoteContainer item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setContextMenu(null);
					} else {
						if (CUserRules.get("Categories.EditMenu"))
							setContextMenu(contextMenu);
					}
				}
			};

			if (CUserRules.get("Categories.EditMenu")) {
				MenuItem deleteMenu = new MenuItem("Удалить категорию");
				MenuItem addMenu = new MenuItem("Доавить Категорию");
				MenuItem updateMenu = new MenuItem("Редактировать категорию");
				contextMenu.getItems().addAll(deleteMenu, addMenu, updateMenu);

				addMenu.setOnAction(evt -> {
					TreeItem<MyTreeNoteContainer> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
					if (selectedItem == null)
						return;
					ObservableList<TreeItem<MyTreeNoteContainer>> ELEMENTH = root.getChildren();
					MyTreeNoteContainer newItem = new MyTreeNoteContainer();
					newItem.setParent(selectedItem.getValue().getId());
					newItem.setTitle("Новая категория");

					if (selectedItem == root) { // Если этот элемент идёт сразу
												// после родителя главного
						ELEMENTH = root.getChildren();
					} else {
						ELEMENTH = selectedItem.getChildren();
					}

					int index = ELEMENTH.size() - 1;

					ELEMENTH.add(index + 1, new TreeItem<>(newItem));
					newItem.save();
					selectedItem.setExpanded(true);
					// controller.setSelectedTreeItemId(treeTableView.getSelectionModel().getSelectedItem().getValue().getId());
					// controller.updateData();
				});
				deleteMenu.setOnAction(evt -> {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Подтверждение удаления категории");
					alert.setHeaderText("Подтвердите удаление категории");
					alert.setContentText("Вы действительно хотите удалить данную категорию?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {

						TreeItem<MyTreeNoteContainer> treeItem = categoryRow.getTreeItem();
						ObservableList<TreeItem<MyTreeNoteContainer>> DELETED_ELEMENTH = root.getChildren();
						ObservableList<TreeItem<MyTreeNoteContainer>> DELETED_ELEMENTH_CHILDS = root.getChildren();
						if (treeItem.getParent().equals(root)) { // Если этот
																	// элемент
																	// идёт
																	// сразу
																	// после
																	// родителя
																	// главного
							DELETED_ELEMENTH = root.getChildren();
							DELETED_ELEMENTH_CHILDS = treeItem.getChildren();
						} else {
							DELETED_ELEMENTH = treeItem.getParent().getChildren();
							DELETED_ELEMENTH_CHILDS = treeItem.getChildren();
						}

						for (int i = 0; i < DELETED_ELEMENTH_CHILDS.size(); i++) {
							DELETED_ELEMENTH.add(DELETED_ELEMENTH_CHILDS.get(i));
						}

						treeItem.getValue().delete();
						DELETED_ELEMENTH.remove(treeItem);
						setSelectedTreeItemId(treeTableView.getSelectionModel().getSelectedItem().getValue().getId());
						updateData();
					}
				});

				updateMenu.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						TreeItem<MyTreeNoteContainer> selectedItem = treeTableView.getSelectionModel()
								.getSelectedItem();
						CategoriesController editCategory = new CategoriesController();
						editCategory.loadData(treeTableView.getSelectionModel().getSelectedItem().getValue().getId());
						editCategory.run();
						updateData();
						selectedItem.setValue(editCategory.getCategoryObject());
					}
				});

			}

			categoryRow.setOnMouseClicked(event -> {
				if ((!categoryRow.isEmpty()) && getSelectedTreeItemId() != categoryRow.getItem().getId()) {
					setSelectedTreeItemId(categoryRow.getItem().getId());
					updateData();

				}
			});

			final MyTreeNoteContainer copyFrom = new MyTreeNoteContainer();
			final MyTreeNoteContainer copyTo = new MyTreeNoteContainer();
			if (CUserRules.get("Categories.EditMenu"))
				categoryRow.setOnDragDetected(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						TreeItem<MyTreeNoteContainer> selected = (TreeItem<MyTreeNoteContainer>) treeTableView
								.getSelectionModel().getSelectedItem();
						if (selected != null) {
							Dragboard db = treeTableView.startDragAndDrop(TransferMode.ANY);

							// create a miniature of the row you're dragging
							db.setDragView(categoryRow.snapshot(null, null));

							ClipboardContent content = new ClipboardContent();
							content.putString(selected.getValue().getTitle());
							db.setContent(content);

							coTy = CopyType.Category;
							event.consume();
						}
					}
				});

			categoryRow.setOnDragOver(new EventHandler<DragEvent>() {
				@Override
				public void handle(DragEvent event) {
					if (event.getDragboard().hasString()) {
						event.acceptTransferModes(TransferMode.MOVE);

						event.consume();
					}
				}
			});

			categoryRow.setOnDragDropped(new EventHandler<DragEvent>() {
				@Override
				public void handle(DragEvent event) {
					FileItemContainer selectedFile = getSelectedFileTableItem();
					boolean isAllowMenu;
					if(selectedFile != null){
						isAllowMenu = ((FilesModel.isUsersFile(CUser.getId(), selectedFile.getFileId()))
								|| CUser.isAdmin());
					}else{
						isAllowMenu = CUser.isAdmin();
					}
					boolean success = false;
					System.out.println(isAllowMenu);
					if (event.getDragboard().hasString()) {
						if (!categoryRow.isEmpty()) {
							switch (coTy) {
							case File: // Файл
								if (isAllowMenu) {
									if (CValidations.isGoodExt(categoryRow.getTreeItem().getValue().getId(),
											selectedFile.getExt())) {

										FilesModel.moveFileToCategory(selectedFile.getFileId(),
												categoryRow.getTreeItem().getValue().getId());

										updateData();
										success = true;
									} else {
										return;
									}
								}
								break;
							case Category: // Категория
								if (CUserRules.get("Categories.EditMenu")) {
									copyFrom.load(treeTableView.getSelectionModel().getSelectedItem().getValue());
									copyTo.load(categoryRow.getTreeItem().getValue());

									if (!(copyFrom.getId() == 0 || copyTo.getId() == copyFrom.getId())) {
										System.out.println("Copy " + copyFrom.getTitle() + " to " + copyTo.getTitle());
										copyFrom.updateParent(copyTo.getId());
										updateCategories();
										success = true;
									}
								}
								break;
							}
						}
					}
					event.setDropCompleted(success);
					event.consume();
				}
			});

			return categoryRow;
		});

		return treeTableView;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TableView getDataTable() {
		TableView<FileItemContainer> table = new TableView<FileItemContainer>();
		TableColumn iconCol = new TableColumn();
		TableColumn titleCol = new TableColumn("Имя файла");
		TableColumn uathorId = new TableColumn("Id автора");

		table.setRowFactory(tv -> {
			final ContextMenu contextMenu = new ContextMenu();

			TableRow<FileItemContainer> fileRow = new TableRow<FileItemContainer>() {
				@Override
				public void updateItem(FileItemContainer item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setContextMenu(null);
					} else {
						if (CUserRules.get("Files.EditMenu"))
							setContextMenu(contextMenu);
					}
				}
			};

			fileRow.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!fileRow.isEmpty())) {
					CApplication.openFile(new File(FilesModel.getPathToFile(fileRow.getItem().getFileId())));
				}
			});

			MenuItem EditFile = new MenuItem("Редактировать файл");
			MenuItem DeleteFile = new MenuItem("Удалить файл");
			contextMenu.getItems().addAll(EditFile, DeleteFile);

			DeleteFile.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					FileItemContainer selected = table.getSelectionModel().getSelectedItem();
					boolean isAllowMenu = (CUserRules.get("Files.EditMenu")
							&& FilesModel.isUsersFile(CUser.getId(), selected.getFileId())) || CUser.isAdmin();

					if (isAllowMenu) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Подтверждение удаления файла");
						alert.setHeaderText("Подтвердите удаление файла");
						alert.setContentText("Вы действительно хотите удалить данный файл?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							FileItemContainer item = table.getSelectionModel().getSelectedItem();
							item.delete();
							updateData();
						}
					}
				}
			});

			EditFile.setOnAction(new EventHandler<ActionEvent>() {

				public void handle(ActionEvent e) {
					FileItemContainer selected = table.getSelectionModel().getSelectedItem();
					boolean isAllowMenu = (CUserRules.get("Files.EditMenu")
							&& FilesModel.isUsersFile(CUser.getId(), selected.getFileId())) || CUser.isAdmin();

					if (isAllowMenu) {
						FileController editFile = new FileController();
						editFile.loadData(table.getSelectionModel().getSelectedItem().getFileId());
						editFile.run();
						updateData();
					}
				}
			});
			fileRow.setOnDragDetected(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					FileItemContainer selected = table.getSelectionModel().getSelectedItem();
					boolean isAllowMenu = (CUserRules.get("Files.EditMenu")
							&& FilesModel.isUsersFile(CUser.getId(), selected.getFileId())) || CUser.isAdmin();

					if (isAllowMenu) {
						if (selected != null) {
							Dragboard db = table.startDragAndDrop(TransferMode.ANY);
							db.setDragView(fileRow.snapshot(null, null));
							ClipboardContent content = new ClipboardContent();
							content.putString(selected.getTitle());
							db.setContent(content);
							coTy = CopyType.File;
							event.consume();
						}
					}
				}
			});

			fileRow.setOnDragOver(new EventHandler<DragEvent>() {

				@Override
				public void handle(DragEvent event) {
					if (event.getDragboard().hasString()) {
						event.acceptTransferModes(TransferMode.MOVE);
					}
					event.consume();
				}
			});

			return fileRow;
		});

		int FilesCount;
		try {

			ConditionType<String> SearchCondition = null;
			if (!this.searchedText.isEmpty())
				SearchCondition = new ConditionType<String>(" f.originalTitle LIKE ?", "%" + this.searchedText + "%", 2,
						" AND ");
			Integer start = (this.itemsSellectedPage) * this.itemsPerPage;

			FilesCount = FilesModel.getFilesByConditions("COUNT(*) as count",
					FilesModel.getConditionINCategories(selectedCategoryId), -1, SearchCondition, -1, -1)
					.getInt("count");
			this.files = FilesModel.getFilesMapByConditions("f.*",
					FilesModel.getConditionINCategories(selectedCategoryId), -1, SearchCondition, start,
					this.itemsPerPage);

			this.itemsPages = (FilesCount - 1) / this.itemsPerPage + 1;
			pagination.setPageCount(itemsPages);
		} catch (NullPointerException e) {
			CApplication.alertError(e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			CApplication.alertError(e.getMessage());
			e.printStackTrace();
		}
		data.clear();
		for (Map.Entry file : this.files.entrySet()) {
			FileItemContainer item = (FileItemContainer) file.getValue();
			item.generateRawIcon();
			data.add(item);

			iconCol.setCellValueFactory(new PropertyValueFactory<FileItemContainer, ImageView>("rawIcon"));
			iconCol.setMinWidth(100);
			iconCol.setMaxWidth(100);
			iconCol.setPrefWidth(100);
			iconCol.setResizable(false);
			iconCol.setSortable(false);

			titleCol.setCellValueFactory(new PropertyValueFactory<FileItemContainer, String>("originalTitleCroped"));
			titleCol.setMinWidth(20);
			uathorId.setCellValueFactory(new PropertyValueFactory<FileItemContainer, String>("authorId"));
			uathorId.setMinWidth(160);
		}

		table.getColumns().addAll(iconCol, uathorId, titleCol);
		table.setItems(FXCollections.observableArrayList(data));
		this.filesTable = table;
		return table;
	}

	public void setSelectedTreeItemId(int i) {
		this.selectedCategoryId = i;
	}

	public int getSelectedTreeItemId() {
		return this.selectedCategoryId;
	}

	public FileItemContainer getSelectedFileTableItem() {
		return filesTable.getSelectionModel().getSelectedItem();
	}

}
