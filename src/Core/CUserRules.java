package Core;

import java.util.HashMap;
import java.util.Map;

public class CUserRules {

	protected static Map<String, Boolean> ForUsers = new HashMap<String, Boolean>();
	protected static Map<String, Boolean> ForGuests = new HashMap<String, Boolean>();
	protected static Map<String, Boolean> ForAdmins = new HashMap<String, Boolean>();

	public static Boolean get(int i, String action) {
		Map<String, Boolean> Data = new HashMap<String, Boolean>();
		switch (i) {
		case 0:
			Data = ForGuests;
			break;
		case 1:
			Data = ForUsers;
			break;
		case 2:
			Data = ForAdmins;
			break;
		}
		Boolean result = Data.get(action);
		if (result == null)
			return false;

		return Data.get(action);
	}

	public static Boolean get(String action) {
		return get(CUser.getUserStatus(), action);
	}

	public static void init() {
		if (ForUsers.isEmpty()) {
			ForUsers.put("Controller.MainGrid", true);
			ForAdmins.put("Controller.MainGrid", true);
			ForGuests.put("Controller.MainGrid", true);

			/*
			 * Actions.FullAdd - права полного доступа к добавлению файлов в
			 * каталог
			 */
			ForUsers.put("Actions.FullAdd", true);
			ForAdmins.put("Actions.FullAdd", true);
			

			ForAdmins.put("Actions.FullAccessToFiles", true);

		}
	}
}
