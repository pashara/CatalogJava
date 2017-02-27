package Models;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Containers.FileItemContainer;
import Core.CUser;
import Core.CUserRules;
import db.DB;

public class FilesModel {
	public static String dataDir = "data//";

	/*
	 * doubleSlash = separator
	 * 
	 * @Return Full directory path
	 */
	public static String createFolderIsNotExist(String path) {
		File folder = new File(System.getProperty("user.dir") + File.separator + path.replace("//", File.separator));
		if (!folder.exists()) {
			folder.mkdir();
		}
		return System.getProperty("user.dir") + File.separator + path.replace("//", File.separator);
	}

	public static String getPathToFile(Integer id, String title) {
		return System.getProperty("user.dir") + File.separator + dataDir.replace("//", File.separator) + id.toString()
				+ File.separator + title;
	}

	/**
	 * Returns filepath by FileID
	 *
	 * @param  FileId  - FileID
	 * @return Full filepath
	 */
	public static String getPathToFile(String FileId) {
		return getPathToFile(Integer.valueOf(FileId));
	}
	
	
	
	/**
	 * Returns filepath by FileID
	 *
	 * @param  FileId  - FileID
	 * @return Full filepath
	 */
	public static String getPathToFile(Integer FileId) {
		ResultSet ImageInfo = DB.exSelect("select * from files where id ="+FileId);
		String title = null;
		Integer authorId = null;
		try {
			title = ImageInfo.getString("title");
			authorId = ImageInfo.getInt("author");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(CUser.getId() == authorId || CUserRules.get("Actions.FullAccessToFiles")){
			if(title.equals(null)){
				throw new NullPointerException();
			}
		}else{
			throw new NullPointerException();
		}
		
		return System.getProperty("user.dir") + File.separator + dataDir.replace("//", File.separator) + authorId.toString()
				+ File.separator + title;
	}
	

	/*
	 * @override Try to find file EXTENTION in base from child to parent 0 -
	 * deny 1 - go2Next 2 - allowed
	 */
	public static int isGoodExt(int id, String type) throws ClassNotFoundException, SQLException {
		if (id == 0)
			return 0;
		ResultSet resSet = DB.exSelect("select * from categories where id = " + id);

		String DeniedTypes = resSet.getString("DeniedTypes").toLowerCase();
		String AllowedTypes = resSet.getString("AllowedTypes").toLowerCase();

		String[] AllowedTypesArray = AllowedTypes.split(",");
		String[] DeniedTypesArray = DeniedTypes.split(",");

		boolean hasAllowedType = Arrays.asList(AllowedTypesArray).contains(type);
		boolean hasAllowedAllTypes = Arrays.asList(AllowedTypesArray).contains("*");

		// boolean hasDeniedAllTypes =
		// Arrays.asList(DeniedTypesArray).contains("-");
		boolean hasDeniedType = Arrays.asList(DeniedTypesArray).contains(type);

		if ((hasAllowedAllTypes || hasAllowedType) && !hasDeniedType) { // Все
																		// (разрешено
																		// или
																		// разрешен
																		// этот
																		// элемент)
																		// и не
																		// запрешен
			return 2;
		} else if (hasDeniedType) { // Если файл по любому запрешщен
			return 0;
		} else if (!hasAllowedAllTypes && !hasDeniedType) { // Если еще не
															// известно,
															// разрешен он или
															// нет, то мы идём
															// глубже
			return isGoodExt(resSet.getInt("parent"), type);
		}

		return 0;
	}
	
	
	public static Map<Integer, FileItemContainer> getFilesByAuthor(int id){
		try {
			return getFilesByAuthor(id,-1,-1);
		}catch(NullPointerException e){
			throw new NullPointerException();
		}
	}
	
	public static int getAllFilesCount(){
		try {
			PreparedStatement stmt;
			ResultSet rs = null;
			stmt = DB.conn
				.prepareStatement("SELECT COUNT(*) as count FROM files");
			rs = stmt.executeQuery();
			return rs.getInt("count");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException();
		}
	}
	public static int getFilesCount(int id){
		try {
			PreparedStatement stmt;
			ResultSet rs = null;
			stmt = DB.conn
				.prepareStatement("SELECT COUNT(*) as count FROM files WHERE author=?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			return rs.getInt("count");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException();
		}
	}
	
	public static String getExt(String filename){
		String extension = "";
		int index = filename.lastIndexOf('.');
		int position = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
		if (index > position) {
			extension = filename.substring(index + 1);
		}
		return extension;
	}
	
	public static Map<Integer, FileItemContainer> getFilesByAuthor(int id, int offset, int limit){
		PreparedStatement stmt;
		ResultSet rs = null;
		Map<Integer, FileItemContainer> Result = new HashMap<Integer, FileItemContainer>();
		try {
			String LimitCondition = (offset > -1 && limit > 0 )?"LIMIT "+offset+","+limit:"";
			stmt = DB.conn
				.prepareStatement("SELECT * FROM files WHERE author=? "+LimitCondition);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			for(;rs.next();){
				FileItemContainer file = new FileItemContainer(rs.getInt("id"), rs.getInt("author"), rs.getString("author"),
						rs.getString("originalExt"), rs.getString("date"));
				Result.put(rs.getInt("id"),file);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException();
		}
		return Result;
	}
	
	public static Map<Integer, FileItemContainer> getAllFiles(int offset, int limit){
		PreparedStatement stmt;
		ResultSet rs = null;
		Map<Integer, FileItemContainer> Result = new HashMap<Integer, FileItemContainer>();
		try {
			String LimitCondition = (offset > -1 && limit > 0 )?"LIMIT "+offset+","+limit:"";
			stmt = DB.conn
				.prepareStatement("SELECT * FROM files "+LimitCondition);
			rs = stmt.executeQuery();

			for(;rs.next();){
				FileItemContainer file = new FileItemContainer(rs.getInt("id"), rs.getInt("author"), rs.getString("author"),
						rs.getString("originalExt"), rs.getString("date"));
				Result.put(rs.getInt("id"),file);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException();
		}
		return Result;
	}
	
	
	
	public static int getUserUpoadSize2day(int userId){
		PreparedStatement stmt;
		ResultSet rs = null;
		SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy");;
				
				
		try {
			stmt = DB.conn
				.prepareStatement("SELECT SUM(size) as sum FROM files WHERE (date >= ? AND date <= ?) AND author = ?");
			stmt.setString(1, ft.format(new Date())+" 00:00:00");
			stmt.setString(2, ft.format(new Date())+" 23:59:59");
			stmt.setInt(3, userId);
			rs = stmt.executeQuery();
			return rs.getInt("sum");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//date > "27-02-2017 00:00:00" AND date < "27-02-2017 10:00:00"
		return 0;
	}
}

