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
import Containers.MyTreeNoteContainer;
import Core.ConditionType;
import db.DB;

public class FilesModel {
	public static String dataDir = "data//";

	private boolean _prevCondition = false;

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
	 * @param fileId - FileID
	 */
	public static String getPathToFile(String fileId) {
		return getPathToFile(Integer.valueOf(fileId));
	}

	/**
	 * Returns filepath by FileID
	 *
	 * @param FileId - FileID
	 * @return Full filepath
	 */
	public static String getSystemPathToFile(String filename) {
		return System.getProperty("user.dir") + File.separator + "data" + File.separator + "system" + File.separator
				+ File.separator + filename.replace("//", File.separator);
	}
	private static String STRR = "";
	private static void _recursiveFinder(Integer ActiveCategory){
		ResultSet RequestResult = DB.exSelect("select id,parent from categories WHERE parent ="+ActiveCategory);
		
		try {
			if(RequestResult.getInt("parent") == 0){		//Выход из рекурсии
				return;
			}
			
			while (RequestResult.next()) {

				STRR += RequestResult.getInt("id")+",";
				_recursiveFinder(RequestResult.getInt("id"));
			}
		} catch (NumberFormatException | SQLException e) {
			return;
		}
		//_recursiveFinder()
	}
	
	public static String getConditionINCategories(Integer ActiveCategory) {
		if(ActiveCategory == 0){
			return " > -1";
		}
		STRR = " IN("+ActiveCategory+",";

		_recursiveFinder(ActiveCategory);
		
		STRR = STRR.substring(0, STRR.length()-1); 
		STRR+=") ";
		return STRR;
	}

	public static String getPathToFile(Integer FileId) {
		ResultSet ImageInfo = DB.exSelect("select * from files where id =" + FileId);
		String title = null;
		Integer authorId = null;
		try {
			title = ImageInfo.getString("title");
			authorId = ImageInfo.getInt("author");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if (CUser.getId() == authorId ||
		// CUserRules.get("Actions.FullAccessToFiles")) {
		if (title.equals(null)) {
			throw new NullPointerException();
		}
		// } else {
		// throw new NullPointerException();
		// }

		return System.getProperty("user.dir") + File.separator + dataDir.replace("//", File.separator)
				+ authorId.toString() + File.separator + title;
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

	/*
	 * String : f.COUNT(*) as count : f.*
	 * 
	 */

	private String AddCondition2String(String newCondition, boolean bCondition, String Ampersant) {
		if (bCondition) {
			String WhereCondition = "";
			if (_prevCondition)
				WhereCondition += Ampersant;
			WhereCondition += newCondition;
			_prevCondition = true;
			return WhereCondition;
		} else
			_prevCondition = false;
		return "";
	}

	@SuppressWarnings("rawtypes")
	public static Map<Integer, FileItemContainer> getFilesMapByConditions(String fields, int categoryId, int authorId,
			ConditionType condition, int offset, int limit) {
		Map<Integer, FileItemContainer> Result = new HashMap<Integer, FileItemContainer>();
		ResultSet rs = null;
		rs = _getFilesByConditions(fields, categoryId, authorId, condition, offset, limit);
		try {
			for (; rs.next();) {
				FileItemContainer file = new FileItemContainer(rs);
				Result.put(rs.getInt("id"), file);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Result;
	}

	@SuppressWarnings("rawtypes")
	public static Map<Integer, FileItemContainer> getFilesMapByConditions(String fields, String categoryId, int authorId,
			ConditionType condition, int offset, int limit) {
		Map<Integer, FileItemContainer> Result = new HashMap<Integer, FileItemContainer>();
		ResultSet rs = null;
		rs = _getFilesByConditions(fields, categoryId, authorId, condition, offset, limit);
		try {
			for (; rs.next();) {
				FileItemContainer file = new FileItemContainer(rs);
				Result.put(rs.getInt("id"), file);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Result;
	}

	@SuppressWarnings("rawtypes")
	public static ResultSet getFilesByConditions(String fields, int categoryId, int authorId, ConditionType condition,
			int offset, int limit) {
		ResultSet rs = null;
		rs = _getFilesByConditions(fields, categoryId, authorId, condition, offset, limit);
		return rs;
	}
	@SuppressWarnings("rawtypes")
	public static ResultSet getFilesByConditions(String fields, String categoryId, int authorId, ConditionType condition,
			int offset, int limit) {
		ResultSet rs = null;
		rs = _getFilesByConditions(fields, categoryId, authorId, condition, offset, limit);
		return rs;
	}

	@SuppressWarnings("rawtypes")
	protected static ResultSet _getFilesByConditions(String fields, int categoryId, int authorId,
			ConditionType condition, int offset, int limit) {
		try {
			String WhereCondition = "";
			FilesModel FilesModelObj = new FilesModel();

			WhereCondition += FilesModelObj.AddCondition2String("f.author=" + authorId, (authorId >= 0), " AND ");
			WhereCondition += FilesModelObj.AddCondition2String("f.category=" + categoryId, (categoryId > 0), " AND ");
			if (condition != null)
				WhereCondition += FilesModelObj.AddCondition2String(condition.getConditionString(), (condition != null),
						condition.getAmpersant());
			String LimitCondition = (offset > -1 && limit > 0) ? "LIMIT " + offset + "," + limit : "";

			PreparedStatement stmt;
			ResultSet rs = null;
			String SQLEx = "SELECT " + fields
					+ ", i.icon as icon FROM files f LEFT OUTER JOIN files_icons i ON f.typeId = i.id "
					+ ((WhereCondition.length() > 0) ? " WHERE " + WhereCondition : "") + " " + LimitCondition;
			//System.out.println(SQLEx);
			stmt = DB.conn.prepareStatement(SQLEx);

			int installedValues = 1;

			if (condition != null) {
				switch (condition.getTypeVal()) {
				case 1:
					stmt.setInt(installedValues, (int) condition.getConditionValue());
					break;
				case 2:
					String aaa = new String((String)condition.getConditionValue());
					stmt.setString(installedValues, aaa );
					break;
				case 3:
					break;
				}
				installedValues++;
			}
			rs = stmt.executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException();
		}
	}
	
	

	@SuppressWarnings("rawtypes")
	protected static ResultSet _getFilesByConditions(String fields, String categoryId, int authorId,
			ConditionType condition, int offset, int limit) {
		try {
			String WhereCondition = "";
			FilesModel FilesModelObj = new FilesModel();

			WhereCondition += FilesModelObj.AddCondition2String("f.author=" + authorId, (authorId >= 0), " AND ");
			WhereCondition += FilesModelObj.AddCondition2String("f.category " + categoryId, true, " AND ");
			if (condition != null)
				WhereCondition += FilesModelObj.AddCondition2String(condition.getConditionString(), (condition != null),
						condition.getAmpersant());
			String LimitCondition = (offset > -1 && limit > 0) ? "LIMIT " + offset + "," + limit : "";

			PreparedStatement stmt;
			ResultSet rs = null;
			String SQLEx = "SELECT " + fields
					+ ", i.icon as icon FROM files f LEFT OUTER JOIN files_icons i ON f.typeId = i.id "
					+ ((WhereCondition.length() > 0) ? " WHERE " + WhereCondition : "") + " " + LimitCondition;
			//System.out.println(SQLEx);
			stmt = DB.conn.prepareStatement(SQLEx);

			int installedValues = 1;

			if (condition != null) {
				switch (condition.getTypeVal()) {
				case 1:
					stmt.setInt(installedValues, (int) condition.getConditionValue());
					break;
				case 2:
					//System.out.println("OUT II:"+condition.getConditionValue());
					String aaa = new String((String)condition.getConditionValue());
					stmt.setString(installedValues, aaa );
					break;
				case 3:
					break;
				}
				installedValues++;
			}
			rs = stmt.executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NullPointerException();
		}
	}

	public static String getExt(String filename) {
		String extension = "";
		int index = filename.lastIndexOf('.');
		int position = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
		if (index > position) {
			extension = filename.substring(index + 1);
		}
		return extension;
	}

	public static int getUserUpoadSize2day(int userId) {
		PreparedStatement stmt;
		ResultSet rs = null;
		SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
		;
		try {
			stmt = DB.conn.prepareStatement(
					"SELECT SUM(size) as sum FROM files WHERE (date >= ? AND date <= ?) AND author = ?");
			stmt.setString(1, ft.format(new Date()) + " 00:00:00");
			stmt.setString(2, ft.format(new Date()) + " 23:59:59");
			stmt.setInt(3, userId);
			rs = stmt.executeQuery();
			return rs.getInt("sum");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
