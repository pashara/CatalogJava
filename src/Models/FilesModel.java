package Models;

import java.io.File;
import java.nio.file.AccessDeniedException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

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
			// return 5;
			return isGoodExt(resSet.getInt("parent"), type);
		}

		return 0;
	}
}
