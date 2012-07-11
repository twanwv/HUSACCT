package husacct.graphics.util.helpers;

import java.util.ArrayList;

public class PathHelper {
	public static String createCombinedPathHelper(ArrayList<String> paths) {
		String s = "";
		for (String path : paths) {
			if (!path.isEmpty()) {
				s += path + "+";
			}
		}
		if (s.length() > 0) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}
}
