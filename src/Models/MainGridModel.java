package Models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Containers.MyTreeNoteContainer;
import Controllers.MainGridController;
import Core.CUser;
import db.DB;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.WindowEvent;

public class MainGridModel {
	public static void insertAndCoppyFile(File file, File dest, int selectedTreeItemId, String newFileTitle,String extension) {
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
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void copyFileUsingJava7Files(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath());
	}

	private static void getTreeCategoryRecursive(TreeItem<MyTreeNoteContainer> root,int categoryId) {
		for (Map.Entry entry : getCategoryChild(categoryId).entrySet()) {
			TreeItem<MyTreeNoteContainer> firstElementh = new TreeItem<MyTreeNoteContainer>(
					(MyTreeNoteContainer) entry.getValue());
			root.getChildren().add(firstElementh);
			getTreeCategoryRecursive(firstElementh,(int) entry.getKey());
		}
		return;
	}

	public static TreeTableView<MyTreeNoteContainer> getTreeCategory(MainGridController controller) {
		final TreeItem<MyTreeNoteContainer> root = new TreeItem<>(new MyTreeNoteContainer(0, "Все файлы"));
		root.setExpanded(true);
		getTreeCategoryRecursive(root,0);
		
		
		TreeTableColumn<MyTreeNoteContainer, String> column = new TreeTableColumn<>("Column");
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
			MenuItem item1 = new MenuItem("Удалить категорию");
			MenuItem item2 = new MenuItem("Доавить Категорию");

			contextMenu.getItems().addAll(item1, item2);
			TreeTableRow<MyTreeNoteContainer> row = new TreeTableRow<MyTreeNoteContainer>() {
				@Override
				public void updateItem(MyTreeNoteContainer item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setContextMenu(null);
					} else {
						// configure context menu with appropriate menu items,
						// depending on value of item
						setContextMenu(contextMenu);
					}
				}
			};

			row.setOnMouseClicked(event -> {
				if ((!row.isEmpty()) && controller.getSelectedTreeItemId() != row.getItem().getId()) {
					controller.setSelectedTreeItemId(row.getItem().getId());
					controller.updateData();

				}
			});
			item2.setOnAction(evt -> {
				// MyTreeNoteContainer item = row.getItem();
				// System.out.println(row.getItem().getId());
			});
			
			

			row.setOnDragDetected(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                // drag was detected, start drag-and-drop gesture
	                TreeItem<MyTreeNoteContainer> selected = (TreeItem<MyTreeNoteContainer>) treeTableView.getSelectionModel().getSelectedItem();
	                // to access your RowContainer use 'selected.getValue()'

	                if (selected != null) {
	                    Dragboard db = treeTableView.startDragAndDrop(TransferMode.ANY);

	                    // create a miniature of the row you're dragging
	                    db.setDragView(row.snapshot(null, null));

	                    // Keep whats being dragged on the clipboard
	                    ClipboardContent content = new ClipboardContent();
	                    content.putString(selected.getValue().getTitle());
	                    db.setContent(content);

	                    event.consume();
	                }
	            }
	        });
	        row.setOnDragOver(new EventHandler<DragEvent>() {
	            @Override
	            public void handle(DragEvent event) {
	                // data is dragged over the target
	                Dragboard db = event.getDragboard();
	                if (event.getDragboard().hasString()){
	                    event.acceptTransferModes(TransferMode.MOVE);
	                }
	                event.consume();
	            }});
	        row.setOnDragDropped(new EventHandler<DragEvent>() {
	            @Override
	            public void handle(DragEvent event) {

	                Dragboard db = event.getDragboard();
	                boolean success = false;
	                if (event.getDragboard().hasString()) {

	                    if (!row.isEmpty()) {
	                        int dropIndex = row.getIndex();
	                        TreeItem<MyTreeNoteContainer> droppedon = row.getTreeItem();

	                        success = true;
	                    }
	                }
	                event.setDropCompleted(success);
	                event.consume();
	            }});
	        
	        
	        
			return row;
		});

		return treeTableView;
	}

	private static Map<Integer, MyTreeNoteContainer> getCategoryChild(int parent) {
		ResultSet resSet = DB.exSelect("select * from categories where parent = " + parent);
		Map<Integer, MyTreeNoteContainer> Result = new HashMap<Integer, MyTreeNoteContainer>();
		try {
			while (resSet.next()) {
				MyTreeNoteContainer data = new MyTreeNoteContainer(resSet.getInt("id"), resSet.getString("title"));
				Result.put(Integer.valueOf(resSet.getString("id")), data);
			}
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		return Result;
	}

}
