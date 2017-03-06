package Containers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.FilesModel;
import db.DB;

public class MyTreeNoteContainer {

	private String name;
	private Integer id = null;
	private Integer parent = null;
	private String allowedTypes = null;
	private String deniedTypes = null;
	private Integer _FindedCategory = null;
	
	private boolean _recursiveFinder(Integer ActiveCategory){
		ResultSet RequestResult = DB.exSelect("select id,parent from categories WHERE parent ="+ActiveCategory);
		
		try {
			if(RequestResult.getInt("parent") == 0){		//Выход из рекурсии
				return false;
			}
			
			while (RequestResult.next()) {
				if(RequestResult.getInt("id") == _FindedCategory){
					return true;
				}else{
					return _recursiveFinder(RequestResult.getInt("id"));
				}
			}
		} catch (NumberFormatException | SQLException e) {
			return false;
		}
		return false;
	}
	
	
	private void init(int id, int parent, String title, String allowedTypes, String deniedTypes) {
		this.setId(id);
		this.setParent(parent);
		this.setTitle(title);
		this.setAllowedTypes(allowedTypes);
		this.setDeniedTypes(deniedTypes);
	}

	public MyTreeNoteContainer() {

	}

	public MyTreeNoteContainer(int id, int parent, String title) {
		init(id, parent, title,"*","-");
	}

	public MyTreeNoteContainer(int id) {
		ResultSet resSet = DB.exSelect("select * from categories WHERE id = "+id);
		try {
			init(resSet.getInt("id"), resSet.getInt("parent"), resSet.getString("title"), resSet.getString("allowedTypes"), resSet.getString("deniedTypes"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public MyTreeNoteContainer(MyTreeNoteContainer a) {
		load(a);
	}

	public void load(MyTreeNoteContainer a) {
		init(a.getId(), a.getParent(), a.getTitle(),a.getAllowedTypes(),a.getDeniedTypes());
	}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.name = new String(title);
	}

	public void save() {
		PreparedStatement stmt;
		if (this.id != null) {
			try {
				stmt = DB.conn.prepareStatement("UPDATE categories SET title = ?, parent = ?, allowedTypes = ?, deniedTypes = ? WHERE id=" + this.getId());
				stmt.setString(1, this.getTitle());
				stmt.setInt(2, this.getParent());
				stmt.setString(3, this.getAllowedTypes());
				stmt.setString(4, this.getDeniedTypes());
				stmt.execute();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
				try {
					stmt = DB.conn.prepareStatement(
							"INSERT INTO categories (parent, title) VALUES (?,?)");
					stmt.setInt(1, this.getParent());
					stmt.setString(2, this.getTitle());
					stmt.execute();
					
					ResultSet resSet = DB.exSelect("select id from categories ORDER BY id DESC LIMIT 1");
					this.id = resSet.getInt("id");
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	public void delete() {
		PreparedStatement stmt;
		try {

			stmt = DB.conn.prepareStatement(
					"UPDATE categories SET parent=" + this.getParent() + " WHERE parent=" + this.getId());
			stmt.execute();

			FilesModel.updateFilesWhenDeletedCategories(this.getId(), this.getParent());

			stmt = DB.conn.prepareStatement("DELETE FROM categories WHERE id=" + this.getId());
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateParent(int newParent) {
		PreparedStatement stmt;
		// System.out.println("UPDATE categories SET parent = "+newParent+"
		// WHERE id="+this.getId());
		try {
			//this._FindedCategory = this.getId();
			//if(_recursiveFinder(this.getId())){
				stmt = DB.conn
						.prepareStatement("UPDATE categories SET parent = "+this.getParent()+" WHERE parent=" + this.getId());
				stmt.execute();
			//}
			stmt = DB.conn
					.prepareStatement("UPDATE categories SET parent = " + newParent + " WHERE id=" + this.getId());
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}


	public String getAllowedTypes() {
		return allowedTypes;
	}


	public void setAllowedTypes(String allowedTypes) {
		this.allowedTypes = allowedTypes;
	}


	public String getDeniedTypes() {
		return deniedTypes;
	}


	public void setDeniedTypes(String deniedTypes) {
		this.deniedTypes = deniedTypes;
	}

}