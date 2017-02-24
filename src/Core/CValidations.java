package Core;

import java.sql.SQLException;

import Models.FilesModel;

public class CValidations {

	public static boolean isGoodExt(Integer id, String ext) {

		try {
			switch (FilesModel.isGoodExt(id, ext)) {
			case 0:
				return false;
			case 2:
				return true;
			default:
				return false;
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return false;
		}
	}
}
