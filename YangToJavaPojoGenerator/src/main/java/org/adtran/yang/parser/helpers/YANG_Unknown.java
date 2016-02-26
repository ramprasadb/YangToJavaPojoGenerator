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
import java.util.*;

public class YANG_Unknown extends YANG_Body {

	private String prefix = null;
	private String extension = null;
	private String argument = null;

	private boolean bracked = false;

	public YANG_Unknown(int id) {
		super(id);
	}

	public YANG_Unknown(yang p, int id) {
		super(p, id);
	}

	public void setExtension(String p) {
		extension = unquote(p);
	}

	public String getExtension() {
		return extension;
	}

	public String getBody() {
		return getExtension();
	}

	public void setArgument(String a) {
		argument = a;
	}

	public String getArgument() {
		return argument;
	}

	public void setPrefix(String p) {
		prefix = p;
	}

	public String getPrefix() {
		return prefix;
	}

	public void check(YangContext context) {
		if (!context.isExtensionDefined((YANG_Unknown) this)) {
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"unknown", "extension", prefix + ":" + extension);
			return;
		} else {
			YANG_Extension extension = context
					.getExtension((YANG_Unknown) this);
			if (extension.getArgument() != null) {
				if (getArgument() == null) {
					YangErrorManager.addError(getFileName(), getLine(), getCol(),
							"extension_arg", extension.getBody(), extension
									.getArgument().getArgument(), extension
									.getFileName(), extension.getLine());
				}
			} else {
				if (getArgument() != null)
					YangErrorManager.addError(getFileName(), getLine(), getCol(),
							"unexpected_arg", extension.getBody(), extension
									.getArgument().getArgument(), extension
									.getFileName(), extension.getLine());
			}
		}
	}

	public String toString() {
		String result = new String();
		result += prefix + ":" + extension;
		if (argument != null)
			result += " " + argument;
		if (bracked) {
			result += "{\n";
			for (Enumeration<YANG_Unknown> eu = getUnknowns().elements(); eu
					.hasMoreElements();)
				result += eu.nextElement().toString() + "\n";
			result += "}";
		} else
			result += ";";
		return result;
	}
}
