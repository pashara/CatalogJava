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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

public class MainGridModel {
	public static void insertAndCoppyFile(File file,File dest, int selectedTreeItemId, String newFileTitle){
		try {
			copyFileUsingJava7Files(file, dest);

			SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

			PreparedStatement stmt = DB.conn.prepareStatement(
					"INSERT INTO files (author, category,title,originalTitle,originalExt,size,date) VALUES (?,?,?,?,?,?,?);");

			stmt.setInt(1, CUser.getId());
			stmt.setInt(2, selectedTreeItemId);
			stmt.setString(3, newFileTitle);
			stmt.setString(4, file.getName());
			stmt.setString(5, file.getName());
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
	
	
	
	public static TreeTableView<MyTreeNoteContainer> getTreeCategory( MainGridController controller) {
		// Node rootIcon = new ImageView(new
		// Image(getClass().getResourceAsStream("folder_16.png")));

		final TreeItem<MyTreeNoteContainer> root = new TreeItem<>(new MyTreeNoteContainer(0, "Root"));
		root.setExpanded(true);

		for (Map.Entry entry : getCategoryChild(0).entrySet()) {
			TreeItem<MyTreeNoteContainer> firstElementh = new TreeItem<MyTreeNoteContainer>((MyTreeNoteContainer) entry.getValue());
			firstElementh.setExpanded(false);
			for (Map.Entry entryDeeper : getCategoryChild((int) entry.getKey()).entrySet()) {
				TreeItem<MyTreeNoteContainer> item = new TreeItem<MyTreeNoteContainer>((MyTreeNoteContainer) entryDeeper.getValue());
				firstElementh.getChildren().add(item);
			}
			root.getChildren().add(firstElementh);
		}

		TreeTableColumn<MyTreeNoteContainer, String> column = new TreeTableColumn<>("Column");
		column.setPrefWidth(150);

		column.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<MyTreeNoteContainer, String> param) -> new ReadOnlyStringWrapper(
						param.getValue().getValue().getTitle()));

		final TreeTableView<MyTreeNoteContainer> treeTableView = new TreeTableView<>(root);
		treeTableView.getColumns().add(column);
		treeTableView.setPrefWidth(152);
		treeTableView.setShowRoot(true);

		treeTableView.setRowFactory(tv -> {
			TreeTableRow<MyTreeNoteContainer> row = new TreeTableRow<>();
			row.setOnMouseClicked(event -> {
				if ((!row.isEmpty()) && controller.getSelectedTreeItemId() != row.getItem().getId()) {
					controller.setSelectedTreeItemId(row.getItem().getId());
					controller.updateData();

				}
			});
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
