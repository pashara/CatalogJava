package Models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;

import Containers.FileItemContainer;
import Containers.MyTreeNoteContainer;
import Controllers.CategoriesController;
import Controllers.FileController;
import Controllers.LoginController;
import Controllers.MainGridController;
import Core.CApplication;
import Core.CUser;
import Core.CUserRules;
import Core.CValidations;
import db.DB;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.WindowEvent;

public class MainGridModel {

	public static final Logger log = Logger.getLogger(MainGridModel.class);
	public static MainGridController controller;

	public static void insertAndCoppyFile(File file, File dest, int selectedTreeItemId, String newFileTitle,
			String extension) {
		try {
			copyFileUsingJava7Files(file, dest);
			FileItemContainer newFileItem = new FileItemContainer();
			newFileItem.setAuthorId(CUser.getId());
			newFileItem.setCategoryId(selectedTreeItemId);
			newFileItem.setOriginalTitle(file.getName());
			newFileItem.setTitle(newFileTitle);
			newFileItem.setExt(extension);
			newFileItem.setSize(file.length());
			if(CApplication.isItImage(extension)){
				newFileItem.setTypeId(1);
			}else{
				newFileItem.setTypeId(2);
			}
			log.info("Moving file from category Drag`n`Drop");
			newFileItem.save();
		} catch (IOException e1) {
			log.error("Error of coping file by drag`n`drop");
			e1.printStackTrace();
		}
	}

	private static void copyFileUsingJava7Files(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath());
	}

	public static void getTreeCategoryRecursive(final TreeItem<MyTreeNoteContainer> root, int categoryId) {
		for (Map.Entry entry : getCategoryChild(categoryId).entrySet()) {
			TreeItem<MyTreeNoteContainer> firstElementh = new TreeItem<MyTreeNoteContainer>(
					(MyTreeNoteContainer) entry.getValue());
			root.getChildren().add(firstElementh);
			getTreeCategoryRecursive(firstElementh, (int) entry.getKey());
		}
		return;
	}

	private static Map<Integer, MyTreeNoteContainer> getCategoryChild(int parent) {
		ResultSet resSet = DB.exSelect("select * from categories where parent = " + parent + " ORDER BY `order` ASC ");
		Map<Integer, MyTreeNoteContainer> Result = new HashMap<Integer, MyTreeNoteContainer>();
		try {
			while (resSet.next()) {
				MyTreeNoteContainer data = new MyTreeNoteContainer(resSet.getInt("id"), resSet.getInt("parent"),
						resSet.getString("title"));
				Result.put(Integer.valueOf(resSet.getString("id")), data);
			}
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		return Result;
	}

}
