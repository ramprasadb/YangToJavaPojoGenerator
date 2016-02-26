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
import java.text.MessageFormat;
import java.util.*;

public class YANG_Rpc extends TypedefGroupingBody {

	private String rpc = null;
	private YANG_Input input = null;
	private YANG_Output output = null;

	private boolean b_input = false, b_output = false;

	public YANG_Rpc(int id) {
		super(id);
	}

	public YANG_Rpc(yang p, int id) {
		super(p, id);
	}

	public void setRpc(String r) {
		rpc = unquote(r);
	}

	public String getBody() {
		return getRpc();
	}

	public String getRpc() {
		return rpc;
	}

	public void setInput(YANG_Input i) {
		if (!b_input) {
			b_input = true;
			input = i;
		} else
			YangErrorManager.addError(filename, i.getLine(), i.getCol(), "unex_kw",
					"input");
	}

	public YANG_Input getInput() {
		return input;
	}

	public void setOutput(YANG_Output o) {
		if (!b_output) {
			b_output = true;
			output = o;
		} else
			YangErrorManager.addError(filename, o.getLine(), o.getCol(), "unex_kw",
					"output");
	}

	public YANG_Output getOutput() {
		return output;
	}

	public boolean isBracked() {
		return super.isBracked() || b_input || b_output;
	}

	public void check(YangContext context) {
		super.check(context);
		if (input != null)
			input.check(context);
		if (output != null)
			output.check(context);
	}

	public String toString() {
		String result = new String();
		result += "rpc " + rpc;
		if (isBracked()) {
			result += "{\n";
			result += super.toString() + "\n";
			if (b_input)
				result += input.toString() + "\n";
			if (b_output)
				result += output.toString() + "\n";
			result += "}";
		} else
			result += ";";
		return result;
	}
}
