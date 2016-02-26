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
import java.math.*;
import java.util.regex.Pattern;

public class YangBuiltInTypes {

	public static final String ytrue = "true";
	public static final String yfalse = "false";

	public static final String binary = "binary";
	public static final String bits = "bits";
	public static final String yboolean = "boolean";
	public static final String decimal64 = "decimal64";
	public static final String empty = "empty";
	public static final String enumeration = "enumeration";
	public static final String identityref = "identityref";
	public static final String instanceidentifier = "instance-identifier";
	public static final String int8 = "int8";
	public static final String int16 = "int16";
	public static final String int32 = "int32";
	public static final String int64 = "int64";
	public static final String leafref = "leafref";
	public static final String string = "string";
	public static final String uint8 = "uint8";
	public static final String uint16 = "uint16";
	public static final String uint32 = "uint32";
	public static final String uint64 = "uint64";
	public static final String union = "union";

	// Removed build-in type
	// public static final String float32 = "float32";
	// public static final String float64 = "float64";
	// public static final String keyref = "keyref";

	public static final int int8lb = -128;
	public static final int int8ub = 127;
	public static final int int16lb = -32768;
	public static final int int16ub = 32767;
	public static final int int32lb = -2147483648;
	public static final int int32ub = 2147483647;
	public static final BigInteger int64lb = new BigInteger(
			"-9223372036854775808");
	public static final BigInteger int64ub = new BigInteger(
			"9223372036854775807");
	public static final int uint8lb = 0;
	public static final int uint16lb = 0;
	public static final int uint32lb = 0;
	public static final BigInteger uint64lb = new BigInteger("0");
	public static final int uint8ub = 255;
	public static final int uint16ub = 65535;
	public static final BigInteger uint32ub = new BigInteger("4294967295");
	public static final BigInteger uint64ub = new BigInteger(
			"18446744073709551615");

	public static final BigDecimal dec64_1lb = new BigDecimal(
			"-922337203685477580.8");
	public static final BigDecimal dec64_1ub = new BigDecimal(
			"922337203685477580.8");
	public static final BigDecimal dec64_2lb = new BigDecimal(
			"-92233720368547758.08");
	public static final BigDecimal dec64_2ub = new BigDecimal(
			"92233720368547758.08");
	public static final BigDecimal dec64_3lb = new BigDecimal(
			"-9223372036854775.808");
	public static final BigDecimal dec64_3ub = new BigDecimal(
			"9223372036854775.808");
	public static final BigDecimal dec64_4lb = new BigDecimal(
			"-922337203685477.5808");
	public static final BigDecimal dec64_4ub = new BigDecimal(
			"922337203685477.5808");
	public static final BigDecimal dec64_5lb = new BigDecimal(
			"-92233720368547.75808");
	public static final BigDecimal dec64_5ub = new BigDecimal(
			"92233720368547.75808");
	public static final BigDecimal dec64_6lb = new BigDecimal(
			"-9223372036854.775808");
	public static final BigDecimal dec64_6ub = new BigDecimal(
			"9223372036854.775808");
	public static final BigDecimal dec64_7lb = new BigDecimal(
			"-922337203685.4775808");
	public static final BigDecimal dec64_7ub = new BigDecimal(
			"922337203685.4775808");
	public static final BigDecimal dec64_8lb = new BigDecimal(
			"-92233720368.54775808");
	public static final BigDecimal dec64_8ub = new BigDecimal(
			"92233720368.54775808");
	public static final BigDecimal dec64_9lb = new BigDecimal(
			"-9223372036.854775808");
	public static final BigDecimal dec64_9ub = new BigDecimal(
			"9223372036.854775808");
	public static final BigDecimal dec64_10lb = new BigDecimal(
			"-922337203.6854775808");
	public static final BigDecimal dec64_10ub = new BigDecimal(
			"922337203.6854775808");
	public static final BigDecimal dec64_11lb = new BigDecimal(
			"-92233720.36854775808");
	public static final BigDecimal dec64_11ub = new BigDecimal(
			"92233720.36854775808");
	public static final BigDecimal dec64_12lb = new BigDecimal(
			"-9223372.036854775808");
	public static final BigDecimal dec64_12ub = new BigDecimal(
			"9223372.036854775808");
	public static final BigDecimal dec64_13lb = new BigDecimal(
			"-922337.2036854775808");
	public static final BigDecimal dec64_13ub = new BigDecimal(
			"922337.2036854775808");
	public static final BigDecimal dec64_14lb = new BigDecimal(
			"-92233.72036854775808");
	public static final BigDecimal dec64_14ub = new BigDecimal(
			"92233.72036854775808");
	public static final BigDecimal dec64_15lb = new BigDecimal(
			"-9223.372036854775808");
	public static final BigDecimal dec64_15ub = new BigDecimal(
			"9223.372036854775808");
	public static final BigDecimal dec64_16lb = new BigDecimal(
			"-922.3372036854775808");
	public static final BigDecimal dec64_16ub = new BigDecimal(
			"922.3372036854775808");
	public static final BigDecimal dec64_17lb = new BigDecimal(
			"-92.23372036854775808");
	public static final BigDecimal dec64_17ub = new BigDecimal(
			"92.23372036854775808");
	public static final BigDecimal dec64_18lb = new BigDecimal(
			"-9.223372036854775808");
	public static final BigDecimal dec64_18ub = new BigDecimal(
			"9.223372036854775808");

	public static final int idlength = 63;

	public static final String config = "true";
	public static final String mandatory = "false";

	public String canonizeString(String s) {
		String result = s;
		result = result.trim();
		return result;
	}

	public static boolean isNumber(String s) {
		return isInteger(s) || isFloat(s);
	}

	public static boolean isInteger(String t) {
		if (t == null)
			return false;
		if (t.compareTo(int8) == 0 || t.compareTo(int16) == 0
				|| t.compareTo(int32) == 0 || t.compareTo(int64) == 0
				|| t.compareTo(uint8) == 0 || t.compareTo(uint16) == 0
				|| t.compareTo(uint32) == 0 || t.compareTo(uint64) == 0)
			return true;
		else
			return false;
	}

	public static boolean isFloat(String t) {
		if (t == null)
			return false;
		// if (t.compareTo(float32) == 0 || t.compareTo(float64) == 0)
		if (t.compareTo(decimal64) == 0)
			return true;
		else
			return false;
	}

	public static boolean isBuiltIn(String t) {
		if (t == null)
			return false;
		if (t.compareTo(int8) == 0
				|| t.compareTo(int16) == 0
				|| t.compareTo(int32) == 0
				|| t.compareTo(int64) == 0
				|| t.compareTo(uint8) == 0
				|| t.compareTo(uint16) == 0
				|| t.compareTo(uint32) == 0
				|| t.compareTo(uint64) == 0
				// || t.compareTo(float32) == 0 || t.compareTo(float64) == 0
				|| t.compareTo(string) == 0
				|| t.compareTo(yboolean) == 0
				|| t.compareTo(enumeration) == 0
				|| t.compareTo(bits) == 0
				|| t.compareTo(binary) == 0
				// || t.compareTo(keyref) == 0
				|| t.compareTo(empty) == 0 || t.compareTo(union) == 0
				|| t.compareTo(decimal64) == 0 || t.compareTo(identityref) == 0
				|| t.compareTo(instanceidentifier) == 0
				|| t.compareTo(leafref) == 0)
			return true;
		else
			return false;
	}

	public static String concat(String s) {
		int state = 0;
		String withes = "";
		String result = "";
		int i = 0;
		boolean eof = i == s.length();
		while (!eof) {
			char c = s.charAt(i++);
			switch (state) {
			case 0: {
				if (c == '\"')
					state = 2;
				else if (c == '\'')
					state = 1;
				break;
			}
			case 1: {
				if (c != '\'')
					result += String.valueOf(c);
				else
					state = 5;
				break;
			}
			case 2: {
				if (c == ' ' | c == '\t') {
					withes += String.valueOf(c);
					state = 3;
				} else if (c == '\n') {
					result += "\n";
					state = 4;
				} else if (c == '\\')
					state = 10;
				else if (c == '\"')
					state = 5;
				else
					result += c;
				break;
			}
			case 3: {
				if (c == ' ' | c == '\t')
					withes += String.valueOf(c);
				else if (c == '\n') {
					result += "\n";
					withes = "";
					state = 4;
				} else if (c == '\"') {
					state = 5;
				} else {
					result += withes;
					result += String.valueOf(c);
					withes = "";
					state = 2;
				}
				break;
			}
			case 4: {
				if (c == '\"')
					state = 5;
				else if (c != ' ' && c != '\t') {
					result += String.valueOf(c);
					state = 2;
				}
				break;
			}
			case 5: {
				if (c == '/')
					state = 6;
				else if (c == '+')
					state = 0;
				break;
			}
			case 6: {
				if (c == '/')
					state = 7;
				else if (c == '*')
					state = 8;
				break;
			}
			case 7: {
				if (c == '\n')
					state = 5;
				break;
			}
			case 8: {
				if (c == '*')
					state = 9;
				break;
			}
			case 9: {
				if (c == '/')
					state = 5;
				else
					state = 8;
				break;
			}
			case 10: {
				if (c == ' ' | c == '\t') {
					withes += String.valueOf(c);
					result += '\\';
					state = 2;
				} else if (c == 't') {
					result += '\t';
					state = 2;
				} else if (c == 'n') {
					result += '\n';
					state = 2;
				} else if (c == '\\') {
					result += '\\';
					state = 2;
				} else {
					result += "\\" + c;
					state = 2;
				}
			}
			}
			eof = i == s.length();
		}

		return result;
	}

	public static String removeQuotesAndTrim(String qs) {
		String s = new String(qs);
		s = s.trim();
		if (s.length() > 0) {
			if (s.charAt(0) == '\"')
				s = s.substring(1, s.length());
			if (s.charAt(s.length() - 1) == '\"')
				s = s.substring(0, s.length() - 1);
			s = s.trim();
		}
		return s;
	}

	public static String removeQuotes(String qs) {
		String s = new String(qs);
		s = s.trim();
		if (s.length() > 0) {
			if (s.charAt(0) == '\"')
				s = s.substring(1, s.length());
			if (s.charAt(s.length() - 1) == '\"')
				s = s.substring(0, s.length() - 1);
		}
		return s;
	}
}
