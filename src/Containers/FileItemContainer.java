package Containers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Core.CApplication;
import Core.CContainer;
import Core.ConditionType;
import Models.FilesModel;
import db.DB;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FileItemContainer implements CContainer {
	private int fileId = -1;
	private int authorId;
	private int categoryId;
	private int typeId;
	private long size;
	private String date = "";
	private String title = "";
	private String originalTitle = "";
	private String ext = "";
	private String icon = "";
	private ImageView rawIcon;

	protected void init(ResultSet rs) {
		try {
			this.fileId = rs.getInt("id");
			this.authorId = rs.getInt("author");
			this.categoryId = rs.getInt("category");
			this.size = rs.getInt("size");
			this.typeId = rs.getInt("typeId");
			this.title = rs.getString("title");
			this.ext = rs.getString("originalExt");
			this.originalTitle = rs.getString("originalTitle");
			this.date = rs.getString("date");
			this.icon = rs.getString("icon");
		} catch (SQLException e) {
			if (CApplication.debug)
				e.printStackTrace();
		}
	}

	/**
	 * Load data from base by file id
	 * 
	 * @param id
	 */
	public FileItemContainer(int id) {
		ConditionType<Integer> SearchCondition = new ConditionType<Integer>(" f.id = ?", id, 1, " AND ");
		ResultSet resultSet = FilesModel.getFilesByConditions("f.*", -1, -1, SearchCondition, -1, -1);
		init(resultSet);
	}

	/**
	 * Load data from ResultSet jdbc
	 * 
	 * @param rs
	 */
	public FileItemContainer(ResultSet resultSet) {
		init(resultSet);
	}

	/**
	 * Allow create empty fileItem to put data in it
	 */
	public FileItemContainer() {
	}

	public int getFileId() {
		return fileId;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int id) {
		authorId = id;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int id) {
		categoryId = id;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int id) {
		typeId = id;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long l) {
		size = l;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String D) {
		date = new String(D);
	}

	public String getTitle() {
		return title;
	}

	public String getOriginalTitle() {
		return originalTitle;
	}


	public String getOriginalTitleCroped() {
		if(originalTitle.length() > 20)
		    return originalTitle.substring(0,20) + "...";
		return originalTitle;
	}

	
	public void setOriginalTitle(String D) {
		originalTitle = new String(D);
	}

	public void setTitle(String D) {
		title = new String(D);
	}

	public void setExt(String D) {
		ext = new String(D);
	}

	public String getExt() {
		return ext;
	}

	public String getIcon() {
		return icon;
	}

	public ImageView getRawIcon() {
		return rawIcon;
	}

	public void setRawIcon(ImageView rawIcon) {
		this.rawIcon = rawIcon;
	}

	public void generateRawIcon() {
		Image image = null;
		if (this.getIcon() == null) {
			image = new Image("file:" + FilesModel.getSystemPathToFile("//"));
		} else if (this.getIcon().equals("IMAGE")) {
			image = new Image("file:" + FilesModel.getPathToFile(this.getFileId()));
		} else {
			image = new Image("file:" + FilesModel.getSystemPathToFile(this.getIcon()));
		}
		ImageView iv2 = new ImageView();
		iv2.setImage(image);
		iv2.setFitWidth(100);
		iv2.setFitHeight(100);
		iv2.setPreserveRatio(true);
		iv2.setSmooth(true);
		iv2.setCache(true);
		this.setRawIcon(iv2);
	}

	public boolean delete() {
		try {
			PreparedStatement stmt = DB.conn.prepareStatement("DELETE FROM files WHERE id= ?");
			stmt.setInt(1, this.getFileId());
			stmt.execute();
			return true;
		} catch (SQLException e) {
			if (CApplication.debug)
				e.printStackTrace();
			return false;
		}
	}

	public boolean save() {
		try {
			PreparedStatement stmt;
			String SQLQuery = null;
			if (this.fileId == -1) {
				SQLQuery = "INSERT INTO files (author, category,title,originalTitle,originalExt,size,typeId,date) VALUES (?,?,?,?,?,?,?,?);";
			} else {
				SQLQuery = "UPDATE files SET author = ? , category = ?, title = ? ,originalTitle = ?,originalExt = ?,size = ?,typeId = ? WHERE id=?";
			}
			stmt = DB.conn.prepareStatement(SQLQuery);
			stmt.setInt(1, this.getAuthorId());
			stmt.setInt(2, this.getCategoryId());
			stmt.setString(3, this.getTitle());
			stmt.setString(4, this.getOriginalTitle());
			stmt.setString(5, this.getExt());
			stmt.setInt(6, (int) this.getSize());
			stmt.setInt(7, (int) this.getTypeId());
			if (this.fileId == -1) {
				stmt.setString(8, new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date()));
			} else {
				stmt.setInt(8, this.getFileId());
			}
			stmt.execute();
			return true;

		} catch (SQLException e) {
			if (CApplication.debug)
				e.printStackTrace();
			return false;
		}
	}
}
