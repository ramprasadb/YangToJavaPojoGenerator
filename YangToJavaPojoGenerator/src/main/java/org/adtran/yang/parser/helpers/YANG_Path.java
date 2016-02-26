package org.adtran.yang.parser.helpers;

/*
 * Copyright 2008 Emmanuel Nataf, Olivier Festor
 * 
 * This file is part of jyang.

 jyang is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jyang is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with jyang.  If not, see <http://www.gnu.org/licenses/>.

 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YANG_Path extends SimpleYangNode {

	private String path = null;

	private Pattern path_arg = null;

	public YANG_Path(int id) {
		super(id);
		try {
			path_arg = Pattern
					.compile("((/([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*([s*([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*s*=s*([$]this/(../)+(([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*/)*([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*)s*])*)+|(../)*([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*([s*([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*s*=s*([$]this/(../)+(([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*/)*([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*)s*])?(/([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*([s*([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*s*=s*([$]this/(../)+(([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*/)*([_A-Za-z][._A-Za-z0-9]*:)?[_A-Za-z][._A-Za-z0-9]*)s*])*)+)");
			// ((/([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*([s*([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*s*=s*([$]this/(../)+(([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*/)*([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*)s*])*)+|(../)*([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*([s*([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*s*=s*([$]this/(../)+(([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*/)*([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*)s*])?(/([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*([s*([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*s*=s*([$]this/(../)+(([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*/)*([_A-Za-z][._-A-Za-z0-9]*:)?[_A-Za-z][._-A-Za-z0-9]*)s*])*)+)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public YANG_Path(yang p, int id) {
		super(p, id);
	}

	public void setPath(String p) {
		String pa = unquote(p);
		Matcher m = path_arg.matcher(pa);
		path = pa;
	}

	public String getPath() {
		return path;
	}

	public String toString() {
		return "path " + path + ";";
	}

}
