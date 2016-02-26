package org.adtran.yang.parser.tools;

import java.io.PrintStream;

import org.adtran.yang.parser.helpers.*;

public class Yang2NetconfHello {

	public Yang2NetconfHello(YANG_Specification n, String[] paths,
			PrintStream out) {
		if (n instanceof YANG_Module) {
			YANG_Module module = (YANG_Module) n;
			out.print(module.getNameSpace().getNameSpace());
			out.print("?module=" + module.getName());
			String[] old = { "0000", "00", "00" };
			YANG_Revision last = null;
			for (YANG_Revision rev : module.getRevisions()) {
				String[] date = rev.getDate().split("-");
				if (date[0].compareTo(old[0]) > 0) {
					old = date;
					last = rev;
				} else if (date[0].compareTo(old[0]) == 0) {
					if (date[1].compareTo(old[1]) > 0) {
						old = date;
						last = rev;
					} else if (date[1].compareTo(old[1]) == 0) {
						if (date[2].compareTo(old[2]) > 0) {
							old = date;
							last = rev;
						}
					}
				}
			}
			if (last != null)
				out.print("&revision=" + last.getDate());
			
			String features = "";
			for (YANG_Body b : module.getBodies()) {
				if (b instanceof YANG_Feature) {
					features += ((YANG_Feature) b).getFeature() + ",";
				}
			}
			if (features.length() > 0)
				out.print("&features="
						+ features.substring(0, features.length() - 1));
		
		}
	}
}
