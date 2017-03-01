package Containers;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.image.ImageView;

public class FileItemContainer {
	private int fileId;
	private int authorId;
	private int categoryId;
	private int typeId;
	private int size;
	private String date = "";
	private String title = "";
	private String originalTitle = "";
	private String ext = "";
	private String icon = "";
	private ImageView rawIcon;

	protected void init(ResultSet rs){
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
			e.printStackTrace();
		}
	}
	
	public FileItemContainer(ResultSet rs,ImageView rawIcon) {
		init(rs);
		this.rawIcon=rawIcon;
	}
	
	
	public FileItemContainer(ResultSet rs) {
		init(rs);
	}

	public int getFileId() {
		return fileId;
	}

	public int getAuthorId() {
		return authorId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public int getTypeId() {
		return typeId;
	}

	public int getSize() {
		return size;
	}

	public String getDate() {
		return date;
	}

	public String getTitle() {
		return title;
	}

	public String getOriginalTitle() {
		return originalTitle;
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
}
