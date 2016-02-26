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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YANG_Type extends SimpleYangNode {

	private String type = null;
	private Vector<YANG_Enum> enums = new Vector<YANG_Enum>();
	private YANG_LeafRefSpecification keyref = null;
	private YANG_BitSpecification bitspec = null;
	private YANG_UnionSpecification yunion = null;
	private YANG_NumericalRestriction numrest = null;
	private YANG_StringRestriction strrest = null;
	private YANG_Base base = null;
	private YANG_Decimal64Spec dec64Spec = null;
	private String instanceIdentifierSpec = null;

	private YANG_TypeDef typedef = null;

	public YANG_TypeDef getTypedef() {
		return typedef;
	}

	public String getInstanceIdentifierSpec() {
		return instanceIdentifierSpec;
	}

	public void setInstanceIdentifierSpec(String instanceIdentifierSpec) {
		this.instanceIdentifierSpec = instanceIdentifierSpec;
	}

	public YANG_Decimal64Spec getDec64Spec() {
		return dec64Spec;
	}

	public void setDec64Spec(YANG_Decimal64Spec dec64Spec) {
		this.dec64Spec = dec64Spec;
		bracked = true;
	}

	public YANG_Base getIdentityRefSpec() {
		return base;
	}

	public void setIdentityRefSpec(YANG_Base base) {
		this.base = base;
		bracked = true;
	}

	private boolean bracked = false;
	private boolean checked;

	public boolean isChecked() {
		return checked;
	}

	public YANG_Type(int id) {
		super(id);
	}

	public YANG_Type(yang p, int id) {
		super(p, id);
	}

	public void setType(String t) {
		type = unquote(t);
	}

	public String getType() {
		return type;
	}

	public void addEnum(YANG_Enum e) {
		enums.add(e);
		bracked = true;
	}

	public Vector<YANG_Enum> getEnums() {
		return enums;
	}

	public void setLeafRef(YANG_LeafRefSpecification k) {
		keyref = k;
		bracked = true;
	}

	public YANG_LeafRefSpecification getLeafRef() {
		return keyref;
	}

	public void setBitSpec(YANG_BitSpecification b) {
		bitspec = b;
		bracked = true;
	}

	public YANG_BitSpecification getBitSpec() {
		return bitspec;
	}

	public void setUnionSpec(YANG_UnionSpecification u) {
		yunion = u;
		bracked = true;
	}

	public YANG_UnionSpecification getUnionSpec() {
		return yunion;
	}

	public void setNumRest(YANG_NumericalRestriction n) {
		numrest = n;
		bracked = true;
	}

	public YANG_NumericalRestriction getNumRest() {
		return numrest;
	}

	public void setStringRest(YANG_StringRestriction r) {
		strrest = r;
		bracked = true;
	}

	public YANG_StringRestriction getStringRest() {
		return strrest;
	}

	public boolean isBracked() {
		return bracked;
	}

	public boolean isPrefixed() {
		return type.indexOf(':') != -1;
	}

	public String getPrefix() {
		if (isPrefixed())
			return type.substring(0, type.indexOf(':'));
		else
			return null;
	}

	public String getSuffix() {
		if (isPrefixed())
			return type.substring(type.indexOf(':') + 1, type.length());
		else
			return type;
	}

	public String[][] getRanges(YangContext context) {
		String[][] ranges = null;
		if (numrest != null) {
			if (numrest instanceof YANG_Range) {
				YANG_Range range = (YANG_Range) numrest;
				ranges = range.getRangeIntervals();
			}
		} else {

			ranges = new String[1][2];
			if (YangBuiltInTypes.isInteger(context.getBuiltInType(this))
					|| YangBuiltInTypes.string.compareTo(context
							.getBuiltInType(this)) == 0
					|| YangBuiltInTypes.binary.compareTo(context
							.getBuiltInType(this)) == 0) {
				ranges[0][0] = "min";
				ranges[0][1] = "max";
			} else if (YangBuiltInTypes.isFloat(context.getBuiltInType(this))) {
				ranges[0][0] = "-INF";
				ranges[0][1] = "INF";
			}
		}
		return ranges;
	}

	private String[][] getLength(YangContext context) {
		String[][] ranges = null;
		if (getStringRest() != null)
			if (getStringRest().getLength() != null) {
				ranges = getStringRest().getLength().getLengthIntervals();
				return ranges;
			}

		ranges = new String[1][2];
		ranges[0][0] = "min";
		ranges[0][1] = "max";

		return ranges;

	}

	public void checkTypeSyntax(YangContext context) {

		if (YangBuiltInTypes.union.compareTo(getType()) == 0)
			if (getUnionSpec() == null)
				YangErrorManager.addError(getFileName(), getLine(), getCol(),
						"union_no_type");

		if (YangBuiltInTypes.leafref.compareTo(getType()) == 0)
			if (getLeafRef() == null)
				YangErrorManager.addError(getFileName(), getLine(), getCol(),
						"leafref_no_path");

		if (YangBuiltInTypes.enumeration.compareTo(getType()) == 0) {
			if (getEnums().size() == 0)
				YangErrorManager.addError(getFileName(), getLine(), getCol(),
						"empty_enumeration");

			checkEnum(context);
		}

		if (YangBuiltInTypes.bits.compareTo(getType()) == 0) {
			if (getBitSpec() == null)
				YangErrorManager.addError(getFileName(), getLine(), getCol(),
						"empty_bits");
			checkBits();
		}

	}

	public void checkTypeSyntax2(YangContext c) {

		if (YangBuiltInTypes.union.compareTo(getType()) == 0)
			if (getUnionSpec() == null)
				System.err.println("@" + getLine() + "." + getCol()
						+ ":union type must have at least one type");

		if (YangBuiltInTypes.leafref.compareTo(getType()) == 0)
			if (getLeafRef() == null)
				System.err.println("@" + getLine() + "." + getCol()
						+ ":keyref type must have at one path");

		if (YangBuiltInTypes.enumeration.compareTo(getType()) == 0) {
			if (getEnums().size() == 0)
				System.err.println("@" + getLine() + "." + getCol()
						+ ":enumeration must have at least one enum");

			checkEnum(c);
		}

		if (YangBuiltInTypes.bits.compareTo(getType()) == 0) {
			if (getBitSpec() == null)
				System.err.println("@" + getLine() + "." + getCol()
						+ ":bits  must have at least one bit");
			checkBits();
		}
	}

	public void check(YangContext context) {

		checkTypeSyntax(context);

		if (context.getBuiltInType(this) == null) {
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"unknown", "type", getType());
			return;
		}
		typedef = context.getTypeDef(this);
		if (typedef != null)
			typedef.setUsed(true);

		if (YangBuiltInTypes.isNumber(context.getBuiltInType(this))) {
			if (getBitSpec() != null)
				YangErrorManager.addError(getBitSpec().getFileName(),
						getBitSpec().getLine(), getBitSpec().getCol(),
						"not_alw", "bit specification", getType());

			if (getEnums().size() != 0)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "enum specification", getType());
			if (getLeafRef() != null)
				YangErrorManager.addError(getLeafRef().getFileName(),
						getLeafRef().getLine(), getLeafRef().getCol(),
						"not_alw", "key reference specification", getType());
			if (getStringRest() != null) {
				YANG_StringRestriction ysr = getStringRest();
				if (ysr.getLength() != null)
					YangErrorManager.addError(filename, getStringRest()
							.getLine(), getStringRest().getCol(), "not_alw",
							"restriction length", getType());
				if (ysr.getPatterns().size() != 0)
					YangErrorManager.addError(filename, getStringRest()
							.getLine(), getStringRest().getCol(), "not_alw",
							"restriction pattern", getType());
			}
			if (getUnionSpec() != null)
				YangErrorManager.addError(getUnionSpec().getFileName(),
						getUnionSpec().getLine(), getUnionSpec().getCol(),
						"not_alw", "union specification", getType());
			if (getInstanceIdentifierSpec() != null)
				YangErrorManager.addError(getFileName(), getLine(), getCol(),
						"not_alw", "require-instance", getType());

			checkRange(context);
		} else if (YangBuiltInTypes.string.compareTo(context
				.getBuiltInType(this)) == 0) {
			if (getBitSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "bit specification", getType());
			if (getEnums().size() != 0)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "enum specification", getType());
			if (getLeafRef() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "key reference specification", getType());
			if (getUnionSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "union specification", getType());
			if (getNumRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "numerical restriction", getType());

			checkStringLength(context);
		} else if (YangBuiltInTypes.yboolean.compareTo(context
				.getBuiltInType(this)) == 0) {
			if (getStringRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "string restriction", getType());
			if (getBitSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "bit specification", getType());
			if (getEnums().size() != 0)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "enum specification", getType());
			if (getLeafRef() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "leaf reference", getType());
			if (getUnionSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "type specification", getType());
			if (getNumRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "numeric restriction", getType());
		} else if (YangBuiltInTypes.enumeration.compareTo(context
				.getBuiltInType(this)) == 0) {
			if (getBitSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "bit specification", getType());
			if (getLeafRef() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "leaf reference", getType());
			if (getStringRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "string restriction", getType());
			if (getUnionSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "type specification", getType());
		} else if (YangBuiltInTypes.bits
				.compareTo(context.getBuiltInType(this)) == 0) {
			if (getStringRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "string restriction", getType());
			if (getEnums().size() != 0)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "enum specification", getType());
			if (getLeafRef() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "leaf reference", getType());
			if (getUnionSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "type specification", getType());
			if (getNumRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "numeric restriction", getType());
		} else if (YangBuiltInTypes.binary.compareTo(context
				.getBuiltInType(this)) == 0) {

			if (getBitSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "bit specification", getType());
			if (getEnums().size() != 0)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "enum specification", getType());
			if (getLeafRef() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "leaf reference", getType());
			if (getUnionSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "type specification", getType());
			if (getNumRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "numeric restriction", getType());
			checkStringLength(context);
		} else if (YangBuiltInTypes.leafref.compareTo(context
				.getBuiltInType(this)) == 0) {
			if (getBitSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "bit specification", getType());
			if (getEnums().size() != 0)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "enum specification", getType());
			if (getUnionSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "type specification", getType());
			if (getNumRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "numeric restriction", getType());
			if (getStringRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "string restriction", getType());

		} else if (YangBuiltInTypes.empty.compareTo(context
				.getBuiltInType(this)) == 0) {
			if (getBitSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "bit specification", getType());
			if (getEnums().size() != 0)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "enum specification", getType());
			if (getUnionSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "type specification", getType());
			if (getNumRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "numeric restriction", getType());
			if (getStringRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "string restriction", getType());
			if (getLeafRef() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "leaf reference", getType());
		} else if (YangBuiltInTypes.union.compareTo(context
				.getBuiltInType(this)) == 0) {
			if (getBitSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "bit specification", getType());
			if (getEnums().size() != 0)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "enum specification", getType());
			if (getNumRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "numeric restriction", getType());
			if (getStringRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "string restriction", getType());
			if (getLeafRef() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "leaf reference", getType());
			if (getUnionSpec() != null)
				checkEmptyUnion(context, new Vector<YANG_Type>(),
						getUnionSpec().getTypes());

		} else if (YangBuiltInTypes.instanceidentifier.compareTo(context
				.getBuiltInType(this)) == 0) {
			if (getBitSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "bit specification", getType());
			if (getEnums().size() != 0)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "enum specification", getType());
			if (getUnionSpec() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "type specification", getType());
			if (getNumRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "numeric restriction", getType());
			if (getStringRest() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "string restriction", getType());
			if (getLeafRef() != null)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"not_alw", "leaf reference", getType());
		}
		checked = true;
	}

	private void checkBits() {
		if (getBitSpec() == null) {
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"empty_bits");
			return;
		}
		YANG_Bit bit = null;
		YANG_BitSpecification bs = getBitSpec();
		YANG_Bit[] bits = new YANG_Bit[bs.getBits().size()];
		BigInteger highest = new BigInteger("0");
		Vector<BigInteger> bitspos = new Vector<BigInteger>();
		String[] bitnames = new String[bs.getBits().size()];

		int i = 0;
		for (Enumeration<YANG_Bit> eb = bs.getBits().elements(); eb
				.hasMoreElements();) {
			bit = eb.nextElement();
			bits[i] = bit;
			bitnames[i++] = bit.getBit();
			if (bit.getPosition() == null) {
				if (highest.compareTo(YangBuiltInTypes.uint32ub
						.add(new BigInteger("1"))) == 0)
					YangErrorManager.addError(bit.getFileName(), bit.getLine(),
							bit.getCol(), "bits_max");
				else {
					bitspos.add(highest);
					highest = highest.add(new BigInteger("1"));
				}
			} else {
				String strpos = YangBuiltInTypes.removeQuotesAndTrim(bit
						.getPosition().getPosition());
				BigInteger biginteger = null;
				try {
					biginteger = new BigInteger(strpos);
				} catch (NumberFormatException e) {
					YangErrorManager.addError(bit.getFileName(), bit.getLine(),
							bit.getCol(), "bits_pos_not_int");
					return;
				}
				if (biginteger.compareTo(YangBuiltInTypes.uint32ub) == 1)
					YangErrorManager.addError(bit.getFileName(), bit.getLine(),
							bit.getCol(), "bits_pos_too_high");
				if (biginteger.compareTo(highest) > 0) {
					highest = biginteger;
					bitspos.add(highest);
					highest = highest.add(new BigInteger("1"));
				} else if (biginteger.add(new BigInteger("1")).compareTo(
						highest) == 0) {
					YangErrorManager.addError(filename, bit.getPosition()
							.getLine(), bit.getPosition().getCol(),
							"dup_value", "position", biginteger, "bit",
							bits[biginteger.subtract(new BigInteger("2"))
									.intValue()].getFileName()
									+ ":"
									+ bits[biginteger.subtract(
											new BigInteger("2")).intValue()]
											.getLine());
					return;
				} else if (biginteger.compareTo(highest) == -1) {
					bitspos.add(biginteger);
				}
			}
		}

	}

	private void checkEnum(YangContext context) {
		int highest = 0;
		int[] enumvalues = new int[getEnums().size()];
		YANG_Enum[] tenums = new YANG_Enum[getEnums().size()];
		String[] enumnames = new String[getEnums().size()];
		int i = 0;
		boolean maxraised = false;
		for (Enumeration<YANG_Enum> ee = getEnums().elements(); ee
				.hasMoreElements();) {
			YANG_Enum yenum = ee.nextElement();
			enumnames[i] = yenum.getEnum();
			tenums[i] = yenum;
			if (yenum.getValue() == null) {
				if (maxraised)
					YangErrorManager.addError(filename, yenum.getLine(), yenum
							.getCol(), "enum_auto_too_big", yenum.getEnum());
				if (highest == 2147483647)
					maxraised = true;
				enumvalues[i++] = (int) highest;
				highest++;
			} else {
				String strenum = yenum.getValue().getValue();
				Integer integer = null;
				try {
					integer = Integer.parseInt(strenum);
				} catch (NumberFormatException n) {
					YangErrorManager.addError(yenum.getFileName(), yenum
							.getLine(), yenum.getCol(), "enum_expr", strenum);
					return;
				}
				if (integer.compareTo(new Integer((int) highest)) >= 0) {
					highest = integer.intValue();
					enumvalues[i++] = (int) highest++;
				} else if (integer.compareTo(new Integer((int) highest)) == -1) {
					enumvalues[i++] = integer.intValue();
				} else if (integer.compareTo(new Integer((int) highest)) == 0) {
					YangErrorManager.addError(yenum.getFileName(), yenum
							.getLine(), yenum.getCol(), "enum_ambigous");
				}
			}
		}
		boolean duplicate = false;
		int dupvalue = 0;
		String dupname = "";
		YANG_Enum dupenum = null;
		YANG_Enum firstenum = null;
		for (int j = 0; j < enumvalues.length && !duplicate; j++)
			for (int k = j + 1; k < enumvalues.length && !duplicate; k++) {
				if (enumvalues[j] == enumvalues[k]) {
					duplicate = true;
					dupvalue = enumvalues[j];
					dupenum = tenums[j].getLine() > tenums[k].getLine() ? tenums[j]
							: tenums[k];
					firstenum = tenums[j].getLine() <= tenums[k].getLine() ? tenums[j]
							: tenums[k];
				}
			}
		if (duplicate) {
			YangErrorManager.addError(dupenum.getFileName(), dupenum.getValue()
					.getLine(), dupenum.getValue().getCol(), "dupp_enum_val",
					dupvalue, getFileName() + ":" + firstenum.getLine());
			// return;
		}
		duplicate = false;
		for (int j = 0; j < enumnames.length && !duplicate; j++)
			for (int k = j + 1; k < enumnames.length && !duplicate; k++) {
				if (enumnames[j].compareTo(enumnames[k]) == 0) {
					duplicate = true;
					dupname = enumnames[j];
					dupvalue = enumvalues[j];
					dupenum = tenums[j].getLine() > tenums[k].getLine() ? tenums[j]
							: tenums[k];
					firstenum = tenums[j].getLine() <= tenums[k].getLine() ? tenums[j]
							: tenums[k];
				}

			}
		if (duplicate) {
			YangErrorManager.addError(filename, dupenum.getLine(), dupenum
					.getCol(), "dupp_enum_name", dupname, getFileName() + ":"
					+ firstenum.getLine());
			return;
		}

	}

	/*
	 * protected void checkPattern(YangContext context) { if (getStringRest() ==
	 * null) return; for (Enumeration<YANG_Pattern> ep =
	 * getStringRest().getPatterns() .elements(); ep.hasMoreElements();) {
	 * 
	 * String pattern = ep.nextElement().getPattern(); String canopattern =
	 * pattern.replaceAll("IsBasicLatin", "InBasicLatin"); try {
	 * Pattern.compile(canopattern); } catch (PatternSyntaxException p) {
	 * YangErrorManager.tadd(getFileName(), getLine(), getCol(), "pattern_exp",
	 * p.getMessage()); } } }
	 */
	/**
	 * check if the range is a restriction
	 * 
	 * @param context
	 */
	protected void checkRange(YangContext context) {

		YANG_Type basetype = null;
		YANG_TypeDef typedef = null;

		// if the type is a number built-in type
		// check its range and the built-in bounds
		if (YangBuiltInTypes.isNumber(getType()))
			checkNumberRange(context, getBuiltInBounds(context));
		else {
			// the type is not a buit-in type
			// check only if there is a range specification
			if (getNumRest() != null) {
				// get the typedef that defines this type
				// must not be null because it is not a built-in type
				typedef = context.getTypeDef(this);
				// get the first range type
				basetype = getFirstRangeDefined(context, typedef);
				// if not null the first range type has ranges
				if (basetype != null)
					checkNumberRange(context, basetype.getRanges(context));
				// if null the first range is a built-in type
				else
					checkNumberRange(context, getRanges(context));

			}
		}
	}

	/**
	 * check if the string length is a restriction. If there is no string
	 * restriction and no length restriction, the method return. Else it looks
	 * for the first typedef that defines this type with a string length
	 * restriction and check length range with. If there is no restriction,
	 * check with itself. If there is no defining typedef, check with itself
	 * 
	 * @param context
	 */
	protected void checkStringLength(YangContext context) {

		YANG_Type basetype = null;
		YANG_TypeDef typedef = null;

		// if the type is a string or binary built-in type
		// check its range and the built-in bounds
		if (YangBuiltInTypes.string.compareTo(getType()) == 0
				|| YangBuiltInTypes.binary.compareTo(getType()) == 0) {

			if (getStringRest() != null) {
				if (getStringRest().getLength() != null) {
					checkStringRange(context, getStringRest().getLength()
							.getLengthIntervals());
				} else
					checkStringRange(context, getBuiltInBounds(context));
			} else
				checkStringRange(context, getBuiltInBounds(context));
		} else {
			// the type is not a buit-in type
			// check only if there is a range specification
			if (getStringRest() != null) {
				if (getStringRest().getLength() != null) {
					// get the typedef that defines this type
					// must not be null because it is not a built-in type
					typedef = context.getTypeDef(this);
					// get the first range type
					basetype = getFirstLengthDefined(context, typedef);
					// if not null the first range type has length
					if (basetype != null)
						checkStringRange(context, basetype.getLength(context));
					// if null the first range is a built-in type
					else
						checkStringRange(context, getLength(context));
				}
			}
		}
	}

	/**
	 * Get the first type with a length restriction from the type used by the
	 * given typedef and to the bases typedef until a built-in type
	 * 
	 * @param context
	 * @param td
	 *            the typedef
	 * @return the first type with a length restriction or null either if the
	 *         typedef has a length restriction or a built-in type is reached.
	 */
	private YANG_Type getFirstLengthDefined(YangContext context, YANG_TypeDef td) {

		YANG_Type basetype = td.getType();
		YANG_TypeDef typedef = null;
		YANG_TypeDef oldtd = null;
		boolean end = false;

		if (basetype.getStringRest() != null) {
			if (basetype.getStringRest().getLength() != null)
				return basetype;
		}
		oldtd = td;
		while (!end) {
			typedef = context.getBaseTypeDef(oldtd);
			if (typedef != null) {
				if (typedef.getType().getStringRest() != null)
					if (typedef.getType().getStringRest().getLength() != null)
						return typedef.getType();

				oldtd = typedef;
			} else
				end = true;
		}
		return null;

	}

	/**
	 * Get the first type with a pattern restriction from the type used by the
	 * given typedef and to the bases typedef until a built-in type
	 * 
	 * @param context
	 * @param td
	 *            the typedef
	 * @return the first type with a pattern restriction or null either if the
	 *         typedef has a length restriction or a built-in type is reached.
	 */
	private YANG_Type getFirstPatternDefined(YangContext context,
			YANG_TypeDef td) {

		YANG_Type basetype = td.getType();
		YANG_TypeDef typedef = null;
		YANG_TypeDef oldtd = null;
		boolean end = false;

		if (basetype.getStringRest() != null) {
			if (basetype.getStringRest().getPatterns().size() != 0)
				return basetype;
		}
		oldtd = td;
		while (!end) {
			typedef = context.getBaseTypeDef(oldtd);
			if (typedef != null) {
				if (typedef.getType().getStringRest() != null)
					if (typedef.getType().getStringRest().getPatterns().size() != 0)
						return typedef.getType();

				oldtd = typedef;
			} else
				end = true;
		}
		return null;

	}

	private YANG_Type getFirstRangeDefined(YangContext context, YANG_TypeDef td) {

		if (td == null)
			return null;
		YANG_Type basetype = td.getType();
		YANG_TypeDef typedef = null;
		YANG_TypeDef oldtd = null;
		boolean end = false;
		if (basetype.getNumRest() != null) {
			return basetype;
		}
		oldtd = td;
		while (!end) {
			typedef = context.getBaseTypeDef(oldtd);
			if (typedef != null) {
				if (typedef.getType().getNumRest() != null)
					return typedef.getType();
				oldtd = typedef;
			} else
				end = true;
		}
		return null;
	}

	/**
	 * check if the range is a restriction of the given range
	 * 
	 * @param context
	 * @param supranges
	 *            the range of the typedef defining this type
	 */
	private void checkNumberRange(YangContext context, String[][] supranges) {
		if (numrest == null)
			return;
		String[][] subranges = getRanges(context);
		checkSubTyping(context, subranges, supranges);

	}

	/**
	 * check if the string range is a restriction of the given range
	 * 
	 * @param context
	 * @param supranges
	 *            the range of the typedef defining this type
	 */
	private void checkStringRange(YangContext context, String[][] supranges) {
		String[][] subranges = getLength(context);
		checkSubTyping(context, subranges, supranges);
	}

	public void checkSubTyping(YangContext context, String[][] subranges,
			String[][] supranges) {
		try {
			checkRangeValues(context, subranges);
			checkRangeValues(context, supranges);
			checkRangeOrder(context, subranges);
			checkRangeOrder(context, supranges);
			subranges = normalizeRange(context, subranges);
			supranges = normalizeRange(context, supranges);

			for (int i = 0; i < subranges.length; i++) {
				boolean inside = false;
				for (int j = 0; j < supranges.length && !inside; j++) {
					if (compareTo(context, subranges[i][0], supranges[j][0]) >= 0
							&& compareTo(context, subranges[i][1],
									supranges[j][1]) <= 0)
						inside = true;
				}
				if (!inside)
					YangErrorManager.addError(getFileName(), getLine(),
							getCol(), "bad_subtyping", subranges[i][0] + ".."
									+ subranges[i][1]);

			}
		} catch (NumberFormatException n) {
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"range_value");
		}

	}

	private String[][] normalizeRange(YangContext context, String[][] r) {
		for (int i = 0; i < r.length; i++)
			for (int j = 0; j < 2; j++)
				if (r[i][j].compareTo("min") == 0)
					r[i][j] = normalizeValue(context, "min");
				else if (r[i][j].compareTo("max") == 0)
					r[i][j] = normalizeValue(context, "max");
		return r;
	}

	private String normalizeValue(YangContext context, String v) {
		if (v.compareTo("min") == 0) {
			if (YangBuiltInTypes.int8.compareTo(context.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.int8lb)).toString();
			else if (YangBuiltInTypes.int16.compareTo(context
					.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.int16lb)).toString();
			else if (YangBuiltInTypes.int32.compareTo(context
					.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.int32lb)).toString();
			else if (YangBuiltInTypes.int64.compareTo(context
					.getBuiltInType(this)) == 0)
				return YangBuiltInTypes.int64lb.toString();
			else if (YangBuiltInTypes.uint8.compareTo(context
					.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.uint8lb)).toString();
			else if (YangBuiltInTypes.uint16.compareTo(context
					.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.uint16lb)).toString();
			else if (YangBuiltInTypes.uint32.compareTo(context
					.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.uint32lb)).toString();
			else if (YangBuiltInTypes.uint64.compareTo(context
					.getBuiltInType(this)) == 0)
				return YangBuiltInTypes.uint64lb.toString();
			else if (YangBuiltInTypes.string.compareTo(context
					.getBuiltInType(this)) == 0)
				return "0";
			else if (YangBuiltInTypes.binary.compareTo(context
					.getBuiltInType(this)) == 0)
				return "0";
			// else if (YangBuiltInTypes.float32.compareTo(context
			// .getBuiltInType(this)) == 0)
			// return ("-INF");
			else if (YangBuiltInTypes.decimal64.compareTo(context
					.getBuiltInType(this)) == 0)
				return ("-INF");

		} else if (v.compareTo("max") == 0) {
			if (YangBuiltInTypes.int8.compareTo(context.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.int8ub)).toString();
			else if (YangBuiltInTypes.int16.compareTo(context
					.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.int16ub)).toString();
			else if (YangBuiltInTypes.int32.compareTo(context
					.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.int32ub)).toString();
			else if (YangBuiltInTypes.int64.compareTo(context
					.getBuiltInType(this)) == 0)
				return YangBuiltInTypes.int64ub.toString();
			else if (YangBuiltInTypes.uint8.compareTo(context
					.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.uint8ub)).toString();
			else if (YangBuiltInTypes.uint16.compareTo(context
					.getBuiltInType(this)) == 0)
				return (new Integer(YangBuiltInTypes.uint16ub)).toString();
			else if (YangBuiltInTypes.uint32.compareTo(context
					.getBuiltInType(this)) == 0)
				return YangBuiltInTypes.uint32ub.toString();
			else if (YangBuiltInTypes.uint64.compareTo(context
					.getBuiltInType(this)) == 0)
				return YangBuiltInTypes.uint64ub.toString();
			else if (YangBuiltInTypes.string.compareTo(context
					.getBuiltInType(this)) == 0)
				return YangBuiltInTypes.uint64ub.toString();
			else if (YangBuiltInTypes.binary.compareTo(context
					.getBuiltInType(this)) == 0)
				return YangBuiltInTypes.uint64ub.toString();
			// else if (YangBuiltInTypes.float32.compareTo(context
			// .getBuiltInType(this)) == 0)
			// return ("INF");
			else if (YangBuiltInTypes.decimal64.compareTo(context
					.getBuiltInType(this)) == 0)
				return ("INF");

		}
		return "";
	}

	public void checkRangeValues(YangContext context, String[][] r) {
		if (YangBuiltInTypes.isInteger(context.getBuiltInType(this))
				|| YangBuiltInTypes.string.compareTo(context
						.getBuiltInType(this)) == 0
				|| YangBuiltInTypes.binary.compareTo(context
						.getBuiltInType(this)) == 0) {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < 2; j++) {
					String bound = r[i][j];
					if (bound.compareTo("min") != 0
							&& bound.compareTo("max") != 0) {
						int radix;
						if (bound.startsWith("0x")) {
							radix = 16;
							bound = bound.substring(2, bound.length());
						} else if (bound.startsWith("-0x")) {
							radix = 16;
							bound = "-" + bound.substring(3, bound.length());
						} else if (bound.startsWith("+0x")) {
							radix = 16;
							bound = bound.substring(3, bound.length());
						} else if (bound.startsWith("0") && bound.length() > 1) {
							radix = 8;
							bound = bound.substring(1, bound.length());
						} else if (bound.startsWith("-0")) {
							radix = 8;
							bound = "-" + bound.substring(2, bound.length());
						} else if (bound.startsWith("+0")) {
							radix = 8;
							bound = bound.substring(2, bound.length());
						} else {
							radix = 10;
							if (bound.startsWith("+"))
								bound = bound.substring(1, bound.length());
						}
						try {
							BigInteger Bi = new BigInteger(bound, radix);
							r[i][j] = Bi.toString();
						} catch (NumberFormatException e) {
							YangErrorManager.addError(getFileName(), getLine(),
									getCol(), "range_not_int", bound);
						}
					}
				}
			}

		} else if (YangBuiltInTypes.isFloat(context.getBuiltInType(this))) {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < 2; j++) {
					String bound = r[i][j];
					if (bound.compareTo("-INF") != 0
							&& bound.compareTo("INF") != 0
							&& bound.compareTo("NaN") != 0
							&& bound.compareTo("min") != 0
							&& bound.compareTo("max") != 0) {
						try {
							new BigDecimal(bound);
						} catch (NumberFormatException e) {
							YangErrorManager.addError(getFileName(), getLine(),
									getCol(), "range_not_float", bound);
						}

					}
				}
			}

		}
	}

	public void checkRangeOrder(YangContext context, String[][] r)
			throws NumberFormatException {

		String bi = r[0][0];
		String bs = r[0][1];
		for (int i = 1; i < r.length; i++) {
			String oldbs = bs;
			if (compareTo(context, bi, bs) == 1)
				YangErrorManager.addError(getFileName(), getLine(), getCol(),
						"bad_range_order", bi + " > " + bs);
			bi = r[i][0];
			bs = r[i][1];
			if (compareTo(context, oldbs, bi) != -1)
				YangErrorManager.addError(getFileName(), getLine(), getCol(),
						"bad_range_order", oldbs + " >= " + bi);
		}
		if (compareTo(context, bi, bs) == 1)
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"bad_range_order", bi + " > " + bs);
	}

	/**
	 * 
	 * @param context
	 * @param bi
	 * @param bs
	 * @return -1 if bi < bs, 0 if bi == bs and 1 if bi > bs
	 */
	private int compareTo(YangContext context, String bi, String bs)
			throws NumberFormatException {

		if (YangBuiltInTypes.isInteger(context.getBuiltInType(this))
				|| YangBuiltInTypes.string.compareTo(context
						.getBuiltInType(this)) == 0
				|| YangBuiltInTypes.binary.compareTo(context
						.getBuiltInType(this)) == 0) {
			if (bi.compareTo("min") == 0) {
				if (bs.compareTo("min") == 0)
					return 0;
				else
					return -1;
			}
			if (bs.compareTo("max") == 0) {
				if (bi.compareTo("max") == 0)
					return 0;
				else
					return -1;
			}
			if (bi.compareTo("max") == 0)
				return 1;
			if (bs.compareTo("min") == 0)
				return 1;
			BigInteger bbi = new BigInteger(bi);
			BigInteger bbs = new BigInteger(bs);
			return bbi.compareTo(bbs);

		} else if (YangBuiltInTypes.isFloat(context.getBuiltInType(this))) {
			if (bi.compareTo("min") == 0 || bi.compareTo("-INF") == 0) {
				if (bs.compareTo("min") == 0 || bs.compareTo("-INF") == 0)
					return 0;
				else
					return -1;
			}
			if (bs.compareTo("max") == 0 || bs.compareTo("INF") == 0) {
				if (bi.compareTo("max") == 0 || bi.compareTo("INF") == 0)
					return 0;
				else
					return -1;
			}
			if (bi.compareTo("max") == 0 || bi.compareTo("INF") == 0)
				return 1;
			if (bs.compareTo("min") == 0 || bs.compareTo("-INF") == 0)
				return 1;
			BigDecimal bbi = new BigDecimal(bi);
			BigDecimal bbs = new BigDecimal(bs);
			return bbi.compareTo(bbs);

		} else
			throw new NumberFormatException();
	}

	public String[][] getBuiltInBounds(YangContext context) {
		String[][] birange = new String[1][2];
		if (YangBuiltInTypes.int8.compareTo(getType()) == 0) {
			birange[0][0] = (new Integer(YangBuiltInTypes.int8lb)).toString();
			birange[0][1] = (new Integer(YangBuiltInTypes.int8ub)).toString();
		} else if (YangBuiltInTypes.int16.compareTo(getType()) == 0) {
			birange[0][0] = (new Integer(YangBuiltInTypes.int16lb)).toString();
			birange[0][1] = (new Integer(YangBuiltInTypes.int16ub)).toString();
		} else if (YangBuiltInTypes.int32.compareTo(getType()) == 0) {
			birange[0][0] = (new Integer(YangBuiltInTypes.int32lb)).toString();
			birange[0][1] = (new Integer(YangBuiltInTypes.int32ub)).toString();
		} else if (YangBuiltInTypes.int64.compareTo(getType()) == 0) {
			birange[0][0] = YangBuiltInTypes.int64lb.toString();
			birange[0][1] = YangBuiltInTypes.int64ub.toString();
		} else if (YangBuiltInTypes.uint8.compareTo(getType()) == 0) {
			birange[0][0] = (new Integer(YangBuiltInTypes.uint8lb)).toString();
			birange[0][1] = (new Integer(YangBuiltInTypes.uint8ub)).toString();
		} else if (YangBuiltInTypes.uint16.compareTo(getType()) == 0) {
			birange[0][0] = (new Integer(YangBuiltInTypes.uint16lb)).toString();
			birange[0][1] = (new Integer(YangBuiltInTypes.uint16ub)).toString();
		} else if (YangBuiltInTypes.uint32.compareTo(getType()) == 0) {
			birange[0][0] = (new Integer(YangBuiltInTypes.uint32lb)).toString();
			birange[0][1] = YangBuiltInTypes.uint32ub.toString();
		} else if (YangBuiltInTypes.uint64.compareTo(getType()) == 0) {
			birange[0][0] = YangBuiltInTypes.uint64lb.toString();
			birange[0][1] = YangBuiltInTypes.uint64ub.toString();
			// } else if (YangBuiltInTypes.float32.compareTo(getType()) == 0) {
			// birange[0][0] = "-INF";
			// birange[0][1] = "INF";
		} else if (YangBuiltInTypes.decimal64.compareTo(getType()) == 0) {
			birange[0][0] = "-INF";
			birange[0][1] = "INF";
		} else if (YangBuiltInTypes.string.compareTo(getType()) == 0) {
			birange[0][0] = "0";
			birange[0][1] = YangBuiltInTypes.uint64ub.toString();
		} else if (YangBuiltInTypes.binary.compareTo(getType()) == 0) {
			birange[0][0] = "0";
			birange[0][1] = YangBuiltInTypes.uint64ub.toString();
		}
		return birange;
	}

	public void checkDefaultValue(YangContext context, YangNode usernode,
			YANG_Default ydefault) {
		String value = ydefault.getDefault();

		if (typedef == null)
			typedef = context.getTypeDef(this);
		if (context.getBuiltInType(this) == null) {
			YangErrorManager.addError(getFileName(), getLine(), getCol(),
					"type_not_found", getType());
			return;
		}

		if (YangBuiltInTypes.isNumber(context.getBuiltInType(this))) {
			String[][] ranges = null;
			YANG_NumericalRestriction lnumrest = null;

			if (getNumRest() != null) {
				ranges = getRanges(context);
				lnumrest = getNumRest();
			} else {
				YANG_TypeDef td = context.getTypeDef(this);
				if (td != null) {
					YANG_Type bt = getFirstRangeDefined(context, td);
					if (bt != null) {
						ranges = bt.getRanges(context);
						lnumrest = bt.getNumRest();
					} else
						ranges = getRanges(context);
				} else {
					ranges = getRanges(context);
				}
			}

			if (YangBuiltInTypes.isInteger(context.getBuiltInType(this))) {
				if (value.compareTo("min") == 0) {
					if (ranges[0][0].compareTo("min") != 0)
						YangErrorManager.addError(filename, getLine(),
								getCol(), "value_not_in_range", "min", context
										.getBuiltInType(this));
				} else if (value.compareTo("max") == 0) {
					if (ranges[ranges.length - 1][1].compareTo("max") != 0)
						YangErrorManager.addError(filename, getLine(),
								getCol(), "value_not_in_range", "max", context
										.getBuiltInType(this));

				} else {
					BigInteger bilb = null;
					BigInteger biub = null;
					try {
						BigInteger bi = new BigInteger(value);
						boolean inside = false;
						for (int i = 0; i < ranges.length && !inside; i++) {
							if (ranges[i][0].compareTo("min") != 0
									&& ranges[i][1].compareTo("max") != 0) {
								bilb = new BigInteger(ranges[i][0]);
								biub = new BigInteger(ranges[i][1]);
							} else if (ranges[i][0].compareTo("min") == 0
									&& ranges[i][1].compareTo("max") != 0) {
								if (YangBuiltInTypes.int8.compareTo(context
										.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int8lb).toString());
								else if (YangBuiltInTypes.int16
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int16lb)
											.toString());
								else if (YangBuiltInTypes.int32
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int32lb)
											.toString());
								else if (YangBuiltInTypes.int64
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = YangBuiltInTypes.int64lb;
								else if (YangBuiltInTypes.uint8
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint8lb)
											.toString());
								else if (YangBuiltInTypes.uint16
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint16lb)
											.toString());
								else if (YangBuiltInTypes.uint32
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint32lb)
											.toString());
								else if (YangBuiltInTypes.uint64
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = YangBuiltInTypes.uint64lb;
								biub = new BigInteger(ranges[i][1]);
							} else if (ranges[i][1].compareTo("max") == 0
									&& ranges[i][0].compareTo("min") != 0) {
								if (YangBuiltInTypes.int8.compareTo(context
										.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int8ub).toString());
								else if (YangBuiltInTypes.int16
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int16ub)
											.toString());
								else if (YangBuiltInTypes.int32
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int32ub)
											.toString());
								else if (YangBuiltInTypes.int64
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = YangBuiltInTypes.int64ub;
								else if (YangBuiltInTypes.uint8
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.uint8ub)
											.toString());
								else if (YangBuiltInTypes.uint16
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.uint16ub)
											.toString());
								else if (YangBuiltInTypes.uint32
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = YangBuiltInTypes.uint32ub;
								else if (YangBuiltInTypes.uint64
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = YangBuiltInTypes.uint64ub;
								bilb = new BigInteger(ranges[i][0]);
							} else {
								if (YangBuiltInTypes.int8.compareTo(context
										.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int8lb).toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int8ub).toString());
								} else if (YangBuiltInTypes.int16
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int16lb)
											.toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int16ub)
											.toString());
								} else if (YangBuiltInTypes.int32
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int32lb)
											.toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int32ub)
											.toString());
								} else if (YangBuiltInTypes.int64
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = YangBuiltInTypes.int64lb;
									biub = YangBuiltInTypes.int64ub;
								} else if (YangBuiltInTypes.uint8
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint8lb)
											.toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.uint8ub)
											.toString());
								} else if (YangBuiltInTypes.uint16
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint16lb)
											.toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.uint16ub)
											.toString());
								} else if (YangBuiltInTypes.uint32
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint32lb)
											.toString());
									biub = YangBuiltInTypes.uint32ub;
								} else if (YangBuiltInTypes.uint64
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = YangBuiltInTypes.uint64lb;
									biub = YangBuiltInTypes.uint64ub;
								}
							}

							inside = (bilb.compareTo(bi) <= 0 && biub
									.compareTo(bi) >= 0);
						}
						if (!inside) {
							if (lnumrest == null) {
								YANG_TypeDef td = this.getTypedef();
								YangErrorManager.addError(filename, ydefault
										.getLine(), ydefault.getCol(),
										"default_match_fail", YangBuiltInTypes
												.removeQuotes(value), td
												.getFileName()
												+ ":" + td.getLine(),
										"range error", "range", getFileName()
												+ ":"
												+ context.getTypeDef(this)
														.getType().getLine());

							} else {
								YANG_Type t = this;
								if (getFirstRangeDefined(context, context
										.getBaseType(this)) != null)
									t = getFirstRangeDefined(context, context
											.getBaseType(this));
								String message = "";
								if (t == this) {
									message = "direct_default_match_fail";
									YangErrorManager.addError(filename,
											ydefault.getLine(), ydefault
													.getCol(), message,
											YangBuiltInTypes
													.removeQuotes(value),
											"range error", "range", lnumrest
													.getFileName()
													+ ":" + lnumrest.getLine());
								} else {
									message = "default_match_fail";
									YANG_TypeDef td = this.getTypedef();
									YangErrorManager.addError(filename,
											lnumrest.getLine(), lnumrest
													.getCol(), message,
											YangBuiltInTypes
													.removeQuotes(value), td
													.getFileName()
													+ ":" + td.getLine(),
											"range error", "range",
											getFileName()
													+ ":"
													+ context.getTypeDef(this)
															.getLine());
								}
							}
						}

					} catch (NumberFormatException ne) {
						YangErrorManager.addError(ydefault.getFileName(),
								ydefault.getLine(), ydefault.getCol(),
								"default_not_int", value, getType());
					}
				}
			} else if (YangBuiltInTypes.isFloat(context.getBuiltInType(this))) {
				int fd = -1;
				if (getDec64Spec() != null)
					if (getDec64Spec().getFractionDigit() != null)
						fd = Integer
								.parseInt(getDec64Spec().getFractionDigit());

				if (fd != -1) {
					int idec = value.indexOf('.');
					if (idec != -1) {
						int nbdec = value.length() - idec - 1;
						if (fd < nbdec)
							YangErrorManager.addError(getFileName(), getLine(),
									getCol(), "too_many_fd", value, fd);

					}
				}

				if (value.compareTo("min") == 0 || value.compareTo("-INF") == 0) {
					if (ranges[0][0].compareTo("min") != 0
							&& ranges[0][0].compareTo("-INF") != 0)
						YangErrorManager.addError(filename, getLine(),
								getCol(), "value_not_in_range", "-INF", context
										.getBuiltInType(this));
				} else if (value.compareTo("max") == 0
						|| value.compareTo("INF") == 0) {
					if (ranges[ranges.length - 1][1].compareTo("max") != 0
							&& ranges[ranges.length - 1][1].compareTo("INF") != 0)
						YangErrorManager.addError(filename, getLine(),
								getCol(), "value_not_in_range", "INF", context
										.getBuiltInType(this));
				} else {
					BigDecimal bdlb = null;
					BigDecimal bdub = null;
					try {
						BigDecimal bd = new BigDecimal(value);
						boolean inside = false;
						for (int i = 0; i < ranges.length && !inside; i++) {
							if ((ranges[i][0].compareTo("min") != 0 && ranges[i][0]
									.compareTo("-INF") != 0)
									&& (ranges[i][1].compareTo("max") != 0 && ranges[i][1]
											.compareTo("INF") != 0)) {
								bdlb = new BigDecimal(ranges[i][0]);
								bdub = new BigDecimal(ranges[i][1]);
								inside = bdlb.compareTo(bd) <= 0
										&& bdub.compareTo(bd) >= 0;
							} else if ((ranges[i][0].compareTo("min") == 0 || ranges[i][0]
									.compareTo("-INF") == 0)
									&& (ranges[i][1].compareTo("max") != 0 && ranges[i][1]
											.compareTo("INF") != 0)) {
								bdub = new BigDecimal(ranges[i][1]);
								inside = bdub.compareTo(bd) >= 0;
							} else if ((ranges[i][0].compareTo("min") != 0 && ranges[i][0]
									.compareTo("-INF") != 0)
									&& (ranges[i][1].compareTo("max") == 0 || ranges[i][1]
											.compareTo("INF") == 0)) {
								bdlb = new BigDecimal(ranges[i][0]);
								inside = bdlb.compareTo(bd) <= 0;
							} else
								inside = true;
						}
						if (!inside) {
							if (lnumrest == null) {
								YANG_TypeDef td = this.getTypedef();
								YangErrorManager.addError(filename, ydefault
										.getLine(), ydefault.getCol(),
										"default_match_fail", YangBuiltInTypes
												.removeQuotes(value), td
												.getFileName()
												+ ":" + td.getLine(),
										"range error", "range", getFileName()
												+ ":"
												+ context.getTypeDef(this)
														.getType().getLine());

							} else {
								YANG_Type t = this;
								if (getFirstRangeDefined(context, context
										.getTypeDef(this)) != this)
									t = getFirstRangeDefined(context, context
											.getTypeDef(this));
								if (t == this) {
									YangErrorManager.addError(filename,
											lnumrest.getLine(), lnumrest
													.getCol(),
											"direct_default_match_fail",
											YangBuiltInTypes
													.removeQuotes(value),
											"range error", "range", this
													.getFileName()
													+ ":" + getLine());
								} else {
									YANG_TypeDef td = this.getTypedef();
									YangErrorManager.addError(filename,
											lnumrest.getLine(), lnumrest
													.getCol(),
											"default_match_fail",
											YangBuiltInTypes
													.removeQuotes(value), td
													.getFileName()
													+ ":" + td.getLine(),
											"range error", "range", this
													.getFileName()
													+ ":" + getLine());
								}
							}
						}
					} catch (NumberFormatException ne) {
						YangErrorManager.addError(ydefault.getFileName(),
								ydefault.getLine(), ydefault.getCol(),
								"default_not_float", value);
					}
				}
			}
		} else if (YangBuiltInTypes.string.compareTo(context
				.getBuiltInType(this)) == 0
				|| YangBuiltInTypes.binary.compareTo(context
						.getBuiltInType(this)) == 0) {
			value = YangBuiltInTypes.removeQuotes(ydefault.getDefault());

			String[][] ranges = null;
			boolean isStringRestricted = false;

			Enumeration<YANG_Pattern> patterns = null;

			if (getStringRest() != null) {
				if (getStringRest().getLength() != null)
					isStringRestricted = true;
				else
					isStringRestricted = false;

				patterns = getStringRest().getPatterns().elements();

			} else
				isStringRestricted = false;

			if (isStringRestricted) {
				ranges = getLength(context);
			} else {
				YANG_TypeDef td = context.getTypeDef(this);
				if (td != null) {
					YANG_Type bt = getFirstLengthDefined(context, td);
					if (bt != null)
						ranges = bt.getLength(context);
					else
						ranges = getLength(context);
				} else {
					ranges = getLength(context);
				}
			}

			BigInteger bi = new BigInteger(new Integer(value.length())
					.toString());
			BigInteger bilb = null;
			BigInteger biub = null;
			boolean inside = false;
			for (int i = 0; i < ranges.length && !inside; i++) {
				if (ranges[i][0].compareTo("min") != 0
						&& ranges[i][1].compareTo("max") != 0) {
					bilb = new BigInteger(ranges[i][0]);
					biub = new BigInteger(ranges[i][1]);
					inside = (bilb.compareTo(bi) <= 0 && biub.compareTo(bi) >= 0);
				} else if (ranges[i][0].compareTo("min") == 0
						&& ranges[i][1].compareTo("max") != 0) {
					biub = new BigInteger(ranges[i][1]);
					inside = biub.compareTo(bi) >= 0;
				} else if (ranges[i][0].compareTo("min") != 0
						&& ranges[i][1].compareTo("max") == 0) {
					bilb = new BigInteger(ranges[i][0]);
					inside = bilb.compareTo(bi) <= 0;
				} else if (ranges[i][0].compareTo("min") == 0
						&& ranges[i][1].compareTo("max") == 0) {
					inside = true;
				}
			}
			if (!inside)
				YangErrorManager.addError(filename, ydefault.getLine(),
						ydefault.getCol(), "direct_default_match_fail",
						YangBuiltInTypes.removeQuotes(value), "length error",
						"length", this.getFileName() + ":");
			// + getStringRest().getLine());

			boolean direct = true;
			YANG_TypeDef indirectTd = null;
			if (patterns != null) {
				if (getStringRest().getPatterns().size() == 0) {
					YANG_TypeDef td = context.getTypeDef(this);
					if (td != null) {
						YANG_Type bt = getFirstPatternDefined(context, td);
						if (bt != null)
							patterns = bt.getStringRest().getPatterns()
									.elements();
					}

				}
			} else {
				direct = false;
				indirectTd = context.getTypeDef(this);
				if (indirectTd != null) {
					YANG_Type bt = getFirstPatternDefined(context, indirectTd);
					if (bt != null)
						patterns = bt.getStringRest().getPatterns().elements();
				}

			}

			if (patterns != null)
				while (patterns.hasMoreElements()) {
					YANG_Pattern pattern = patterns.nextElement();
					if (!pattern.checkExp(value)) {
						if (direct) {
							YangErrorManager.addError(filename, ydefault
									.getLine(), ydefault.getCol(),
									"direct_default_match_fail",
									YangBuiltInTypes.removeQuotes(value),
									"pattern mismatch", "pattern", this
											.getFileName()
											+ ":" + pattern.getLine());
						} else {
							YangErrorManager.addError(filename, ydefault
									.getLine(), ydefault.getCol(),
									"default_match_fail", YangBuiltInTypes
											.removeQuotes(value), indirectTd
											.getFileName()
											+ ":" + indirectTd.getLine(),
									"pattern mismatch", "pattern", this
											.getFileName()
											+ ":" + pattern.getLine());
						}

					}
				}
			if (YangBuiltInTypes.binary.compareTo(context.getBuiltInType(this)) == 0) {

				Pattern path_arg = null;

				try {
					path_arg = Pattern.compile("[A-Z0-9]*");
				} catch (Exception e) {
					e.printStackTrace();
				}
				Matcher m = path_arg.matcher(value);
				if (!m.matches()) {
					YangErrorManager.addError(filename, getLine(), getCol(),
							"bad_binary_expr", value, getType());
				}
			}

		} else if (YangBuiltInTypes.enumeration.compareTo(context
				.getBuiltInType(this)) == 0) {
			value = YangBuiltInTypes.removeQuotesAndTrim(value);
			String[] enums = getFirstEnumDefined(context, this);
			boolean match = false;
			int i = 0;
			while (!match && i < enums.length)
				match = enums[i++].compareTo(value) == 0;
			if (!match)
				YangErrorManager.addError(ydefault.getFileName(), ydefault
						.getLine(), ydefault.getCol(), "default_mismatch",
						value, getType(), getFileName(), getLine());
		} else if (YangBuiltInTypes.bits
				.compareTo(context.getBuiltInType(this)) == 0) {
			value = YangBuiltInTypes.removeQuotesAndTrim(value);
			String[] bv = value.split("\\s");
			
			if (bv.length > getFirstBitDefined(context, this))
				YangErrorManager.addError(ydefault.getFileName(), ydefault
						.getLine(), ydefault.getCol(), "default_mismatch",
						value, getType(), getFileName(), getLine());
			else {
				YANG_BitSpecification bs = getBitSpec();
				boolean allfound = true;
				for (String defbit : bv){
					boolean foundone = false;
					for (YANG_Bit bit : bs.getBits()){
						if (bit.getBit().compareTo(defbit) == 0)
							foundone = true;
					}
					allfound = allfound && foundone;
				}
			}
		} else if (YangBuiltInTypes.union.compareTo(context
				.getBuiltInType(this)) == 0) {
			YANG_Type ut = getFirstUnionDefined(context, this);
			boolean found = false;
			for (Enumeration<YANG_Type> et = ut.getUnionSpec().getTypes()
					.elements(); et.hasMoreElements() && !found;) {
				YANG_Type type = et.nextElement();

				found = found
						|| type.checkUnionDefaultValue(context, ut, ydefault);
			}
			if (!found)
				YangErrorManager.addError(filename, getLine(), getCol(),
						"bad_default_union", value, ut.getFileName(), ut
								.getLine());

		} else if (YangBuiltInTypes.empty.compareTo(context
				.getBuiltInType(this)) == 0) {
			if (value.length() != getFirstBitDefined(context, this))
				YangErrorManager.addError(ydefault.getFileName(), ydefault
						.getLine(), ydefault.getCol(), "default_mismatch",
						value, getType(), getFileName(), getLine());
		} else if (YangBuiltInTypes.yboolean.compareTo(context
				.getBuiltInType(this)) == 0) {
			value = YangBuiltInTypes.removeQuotesAndTrim(value);
			if (value.compareTo("true") != 0 && value.compareTo("false") != 0)
				YangErrorManager.addError(ydefault.getFileName(), ydefault
						.getLine(), ydefault.getCol(), "default_mismatch",
						value, getType(), getFileName(), getLine());
		}

	}

	public boolean checkUnionDefaultValue(YangContext context,
			YangNode usernode, YANG_Default ydefault) {
		String value = ydefault.getDefault();

		if (YangBuiltInTypes.isNumber(context.getBuiltInType(this))) {
			String[][] ranges = null;
			YANG_NumericalRestriction lnumrest = null;

			if (getNumRest() != null) {
				ranges = getRanges(context);
				lnumrest = getNumRest();
			} else {
				YANG_TypeDef td = context.getTypeDef(this);
				if (td != null) {
					YANG_Type bt = getFirstRangeDefined(context, td);
					if (bt != null) {
						ranges = bt.getRanges(context);
						lnumrest = bt.getNumRest();
					} else
						ranges = getRanges(context);
				} else {
					ranges = getRanges(context);
				}
			}

			if (YangBuiltInTypes.isInteger(context.getBuiltInType(this))) {
				if (value.compareTo("min") == 0) {
					if (ranges[0][0].compareTo("min") != 0)
						return false;
				} else if (value.compareTo("max") == 0) {
					if (ranges[ranges.length - 1][1].compareTo("max") != 0)
						return false;

				} else {
					BigInteger bilb = null;
					BigInteger biub = null;
					try {
						BigInteger bi = new BigInteger(value);
						boolean inside = false;
						for (int i = 0; i < ranges.length && !inside; i++) {
							if (ranges[i][0].compareTo("min") != 0
									&& ranges[i][1].compareTo("max") != 0) {
								bilb = new BigInteger(ranges[i][0]);
								biub = new BigInteger(ranges[i][1]);
							} else if (ranges[i][0].compareTo("min") == 0
									&& ranges[i][1].compareTo("max") != 0) {
								if (YangBuiltInTypes.int8.compareTo(context
										.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int8lb).toString());
								else if (YangBuiltInTypes.int16
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int16lb)
											.toString());
								else if (YangBuiltInTypes.int32
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int32lb)
											.toString());
								else if (YangBuiltInTypes.int64
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = YangBuiltInTypes.int64lb;
								else if (YangBuiltInTypes.uint8
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint8lb)
											.toString());
								else if (YangBuiltInTypes.uint16
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint16lb)
											.toString());
								else if (YangBuiltInTypes.uint32
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint32lb)
											.toString());
								else if (YangBuiltInTypes.uint64
										.compareTo(context.getBuiltInType(this)) == 0)
									bilb = YangBuiltInTypes.uint64lb;
								biub = new BigInteger(ranges[i][1]);
							} else if (ranges[i][1].compareTo("max") == 0
									&& ranges[i][0].compareTo("min") != 0) {
								if (YangBuiltInTypes.int8.compareTo(context
										.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int8ub).toString());
								else if (YangBuiltInTypes.int16
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int16ub)
											.toString());
								else if (YangBuiltInTypes.int32
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int32ub)
											.toString());
								else if (YangBuiltInTypes.int64
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = YangBuiltInTypes.int64ub;
								else if (YangBuiltInTypes.uint8
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.uint8ub)
											.toString());
								else if (YangBuiltInTypes.uint16
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.uint16ub)
											.toString());
								else if (YangBuiltInTypes.uint32
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = YangBuiltInTypes.uint32ub;
								else if (YangBuiltInTypes.uint64
										.compareTo(context.getBuiltInType(this)) == 0)
									biub = YangBuiltInTypes.uint64ub;
								bilb = new BigInteger(ranges[i][0]);
							} else {
								if (YangBuiltInTypes.int8.compareTo(context
										.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int8lb).toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int8ub).toString());
								} else if (YangBuiltInTypes.int16
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int16lb)
											.toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int16ub)
											.toString());
								} else if (YangBuiltInTypes.int32
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.int32lb)
											.toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.int32ub)
											.toString());
								} else if (YangBuiltInTypes.int64
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = YangBuiltInTypes.int64lb;
									biub = YangBuiltInTypes.int64ub;
								} else if (YangBuiltInTypes.uint8
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint8lb)
											.toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.uint8ub)
											.toString());
								} else if (YangBuiltInTypes.uint16
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint16lb)
											.toString());
									biub = new BigInteger(new Integer(
											YangBuiltInTypes.uint16ub)
											.toString());
								} else if (YangBuiltInTypes.uint32
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = new BigInteger(new Integer(
											YangBuiltInTypes.uint32lb)
											.toString());
									biub = YangBuiltInTypes.uint32ub;
								} else if (YangBuiltInTypes.uint64
										.compareTo(context.getBuiltInType(this)) == 0) {
									bilb = YangBuiltInTypes.uint64lb;
									biub = YangBuiltInTypes.uint64ub;
								}
							}

							inside = (bilb.compareTo(bi) <= 0 && biub
									.compareTo(bi) >= 0);
						}
						if (!inside)
							return false;
						else
							return true;

					} catch (NumberFormatException ne) {
						return false;
					}
				}
			} else if (YangBuiltInTypes.isFloat(context.getBuiltInType(this))) {

				if (value.compareTo("min") == 0 || value.compareTo("-INF") == 0) {
					if (ranges[0][0].compareTo("min") != 0
							&& ranges[0][0].compareTo("-INF") != 0)
						return false;
				} else if (value.compareTo("max") == 0
						|| value.compareTo("INF") == 0) {
					if (ranges[ranges.length - 1][1].compareTo("max") != 0
							&& ranges[ranges.length - 1][1].compareTo("INF") != 0)
						return false;
				} else {
					BigDecimal bdlb = null;
					BigDecimal bdub = null;
					try {
						BigDecimal bd = new BigDecimal(value);
						boolean inside = false;
						for (int i = 0; i < ranges.length && !inside; i++) {
							if ((ranges[i][0].compareTo("min") != 0 && ranges[i][0]
									.compareTo("-INF") != 0)
									&& (ranges[i][1].compareTo("max") != 0 && ranges[i][1]
											.compareTo("INF") != 0)) {
								bdlb = new BigDecimal(ranges[i][0]);
								bdub = new BigDecimal(ranges[i][1]);
								inside = bdlb.compareTo(bd) <= 0
										&& bdub.compareTo(bd) >= 0;
							} else if ((ranges[i][0].compareTo("min") == 0 || ranges[i][0]
									.compareTo("-INF") == 0)
									&& (ranges[i][1].compareTo("max") != 0 && ranges[i][1]
											.compareTo("INF") != 0)) {
								bdub = new BigDecimal(ranges[i][1]);
								inside = bdub.compareTo(bd) >= 0;
							} else if ((ranges[i][0].compareTo("min") != 0 && ranges[i][0]
									.compareTo("-INF") != 0)
									&& (ranges[i][1].compareTo("max") == 0 || ranges[i][1]
											.compareTo("INF") == 0)) {
								bdlb = new BigDecimal(ranges[i][0]);
								inside = bdlb.compareTo(bd) <= 0;
							} else
								inside = true;
						}
						if (!inside)
							return false;
						else
							return true;
					} catch (NumberFormatException ne) {
						return false;
					}
				}
			}
		} else if (YangBuiltInTypes.string.compareTo(context
				.getBuiltInType(this)) == 0
				|| YangBuiltInTypes.binary.compareTo(context
						.getBuiltInType(this)) == 0) {
			value = YangBuiltInTypes.removeQuotes(ydefault.getDefault());

			String[][] ranges = null;
			boolean isStringRestricted = false;

			Enumeration<YANG_Pattern> patterns = null;

			if (getStringRest() != null) {
				if (getStringRest().getLength() != null)
					isStringRestricted = true;
				else
					isStringRestricted = false;

				patterns = getStringRest().getPatterns().elements();

			} else
				isStringRestricted = false;

			if (isStringRestricted) {
				ranges = getLength(context);
			} else {
				YANG_TypeDef td = context.getTypeDef(this);
				if (td != null) {
					YANG_Type bt = getFirstLengthDefined(context, td);
					if (bt != null)
						ranges = bt.getLength(context);
					else
						ranges = getLength(context);
				} else {
					ranges = getLength(context);
				}
			}

			BigInteger bi = new BigInteger(new Integer(value.length())
					.toString());
			BigInteger bilb = null;
			BigInteger biub = null;
			boolean inside = false;
			for (int i = 0; i < ranges.length && !inside; i++) {
				if (ranges[i][0].compareTo("min") != 0
						&& ranges[i][1].compareTo("max") != 0) {
					bilb = new BigInteger(ranges[i][0]);
					biub = new BigInteger(ranges[i][1]);
					inside = (bilb.compareTo(bi) <= 0 && biub.compareTo(bi) >= 0);
				} else if (ranges[i][0].compareTo("min") == 0
						&& ranges[i][1].compareTo("max") != 0) {
					biub = new BigInteger(ranges[i][1]);
					inside = biub.compareTo(bi) >= 0;
				} else if (ranges[i][0].compareTo("min") != 0
						&& ranges[i][1].compareTo("max") == 0) {
					bilb = new BigInteger(ranges[i][0]);
					inside = bilb.compareTo(bi) <= 0;
				} else if (ranges[i][0].compareTo("min") == 0
						&& ranges[i][1].compareTo("max") == 0) {
					inside = true;
				}
			}
			if (!inside)
				return false;

			boolean direct = true;
			YANG_TypeDef indirectTd = null;
			if (patterns != null) {
				if (getStringRest().getPatterns().size() == 0) {
					YANG_TypeDef td = context.getTypeDef(this);
					if (td != null) {
						YANG_Type bt = getFirstPatternDefined(context, td);
						if (bt != null)
							patterns = bt.getStringRest().getPatterns()
									.elements();
					}

				}
			} else {
				direct = false;
				indirectTd = context.getTypeDef(this);
				if (indirectTd != null) {
					YANG_Type bt = getFirstPatternDefined(context, indirectTd);
					if (bt != null)
						patterns = bt.getStringRest().getPatterns().elements();
				}

			}
			if (patterns != null)
				while (patterns.hasMoreElements()) {
					YANG_Pattern pattern = patterns.nextElement();
					if (!pattern.checkExp(value))
						return false;
				}
			if (YangBuiltInTypes.binary.compareTo(context.getBuiltInType(this)) == 0) {

				Pattern path_arg = null;

				try {
					path_arg = Pattern.compile("[A-Z0-9]*");
				} catch (Exception e) {
					e.printStackTrace();
				}
				Matcher m = path_arg.matcher(value);
				if (!m.matches()) {
					return false;
				}
			}
			return true;

		} else if (YangBuiltInTypes.enumeration.compareTo(context
				.getBuiltInType(this)) == 0) {
			value = YangBuiltInTypes.removeQuotesAndTrim(value);
			String[] enums = getFirstEnumDefined(context, this);
			boolean match = false;
			int i = 0;
			while (!match && i < enums.length)
				match = enums[i++].compareTo(value) == 0;
			if (!match)
				return false;
		} else if (YangBuiltInTypes.bits
				.compareTo(context.getBuiltInType(this)) == 0) {
			value = YangBuiltInTypes.removeQuotesAndTrim(value);
			byte[] bv = value.getBytes();
			boolean binary = true;
			for (int i = 0; i < bv.length && binary; i++)
				binary = bv[i] == '1' || bv[i] == '0';
			if (!binary)
				return false;
			if (value.length() != getFirstBitDefined(context, this))
				return false;

		} else if (YangBuiltInTypes.union.compareTo(context
				.getBuiltInType(this)) == 0) {
			YANG_Type ut = getFirstUnionDefined(context, this);
			boolean found = false;
			for (Enumeration<YANG_Type> et = ut.getUnionSpec().getTypes()
					.elements(); et.hasMoreElements() && !found;) {
				YANG_Type type = et.nextElement();
				found = found
						|| type.checkUnionDefaultValue(context, ut, ydefault);
			}
			if (!found)
				return false;

		} else if (YangBuiltInTypes.empty.compareTo(context
				.getBuiltInType(this)) == 0) {
			return false;
		} else if (YangBuiltInTypes.yboolean.compareTo(context
				.getBuiltInType(this)) == 0) {
			value = YangBuiltInTypes.removeQuotesAndTrim(value);
			if (value.compareTo("true") != 0 && value.compareTo("false") != 0)
				return false;

		}
		return true;
	}

	private YANG_Type getFirstUnionDefined(YangContext context, YANG_Type bt) {

		YANG_Type basetype = bt;
		YANG_TypeDef typedef = null;
		boolean ranged = false;

		if (basetype.getUnionSpec() != null)
			return basetype;
		typedef = context.getTypeDef(basetype);
		while (!ranged) {
			if (typedef.getType().getUnionSpec() != null)
				return typedef.getType();
			else {
				typedef = context.getBaseTypeDef(typedef);
			}
		}
		return null;

	}

	private int getFirstBitDefined(YangContext context, YANG_Type bt) {
		YANG_Type basetype = bt;
		YANG_TypeDef typedef = null;
		boolean found = false;

		if (YangBuiltInTypes.bits.compareTo(basetype.getType()) == 0) {
			found = true;
		}
		if (basetype.getBitSpec() != null) {
			found = true;
		} else {
			typedef = context.getTypeDef(basetype);
			while (!found) {
				if (typedef.getType().getBitSpec() != null) {
					basetype = typedef.getType();
					found = true;
				} else
					typedef = context.getBaseTypeDef(typedef);
			}
		}

		return basetype.getBitSpec().getBits().size();
	}

	private String[] getFirstEnumDefined(YangContext context, YANG_Type bt) {

		YANG_Type basetype = bt;
		YANG_TypeDef typedef = null;
		boolean found = false;

		if (YangBuiltInTypes.enumeration.compareTo(basetype.getType()) == 0) {
			found = true;
		}
		if (basetype.getEnums().size() != 0) {
			found = true;
		} else {
			typedef = context.getTypeDef(basetype);
			while (!found) {
				if (typedef.getType().getEnums().size() != 0) {
					basetype = typedef.getType();
					found = true;
				} else
					typedef = context.getBaseTypeDef(typedef);
			}
		}
		String[] enumsv = new String[basetype.getEnums().size()];
		int i = 0;
		for (Enumeration<YANG_Enum> ee = basetype.getEnums().elements(); ee
				.hasMoreElements();)
			enumsv[i++] = YangBuiltInTypes.removeQuotesAndTrim(ee.nextElement()
					.getEnum());
		return enumsv;

	}

	private void checkEmptyUnion(YangContext context, Vector<YANG_Type> chain,
			Vector<YANG_Type> unions) {
		for (Enumeration<YANG_Type> et = unions.elements(); et
				.hasMoreElements();) {
			boolean empty = false;
			YANG_Type utype = et.nextElement();
			empty = checkRecEmptyUnion(context, chain, utype);
			if (empty) {
				if (context.getTypeDef(utype) != null) {
					YANG_TypeDef td = context.getTypeDef(utype);
					YangErrorManager.addError(utype.getFileName(), utype
							.getLine(), utype.getCol(), "empty_union", td
							.getFileName(), td.getLine());
				} else {
					YangErrorManager.addError(utype.getFileName(), utype
							.getLine(), utype.getCol(), "empty_union", utype
							.getFileName(), utype.getLine());
				}
			}

		}
	}

	private boolean checkRecEmptyUnion(YangContext context,
			Vector<YANG_Type> chain, YANG_Type utype) {

		boolean empty = false;
		if (context.getBuiltInType(utype).compareTo(YangBuiltInTypes.empty) == 0) {
			return true;
		} else if (context.getBuiltInType(utype).compareTo(
				YangBuiltInTypes.union) == 0) {
			if (utype.getUnionSpec() != null) {
				chain.add(utype);
				for (YANG_Type t : utype.getUnionSpec().getTypes())
					empty = empty || checkRecEmptyUnion(context, chain, t);
			} else {
				while (utype.getUnionSpec() == null) {
					YANG_TypeDef suptype = context.getTypeDef(utype);
					// if (!suptype.isCorrect())
					// return;
					utype = suptype.getType();
				}
				if (!chain.contains(utype)) {
					chain.add(utype);
					for (YANG_Type t : utype.getUnionSpec().getTypes())
						empty = empty || checkRecEmptyUnion(context, chain, t);
				}
			}

		}
		return empty;

	}

	public String toString() {
		String result = "";
		result += "type " + type;
		if (bracked) {
			result += "{\n";
			for (Enumeration<YANG_Enum> ee = enums.elements(); ee
					.hasMoreElements();) {
				result += ee.nextElement().toString() + "\n";
			}
			if (keyref != null)
				result += keyref.toString() + "\n";
			if (bitspec != null)
				result += bitspec.toString() + "\n";
			if (yunion != null)
				result += yunion.toString() + "\n";
			if (numrest != null)
				result += numrest.toString() + "\n";
			if (strrest != null)
				result += strrest.toString() + "\n";
			result += "}";
		} else
			result += ";";
		return result;
	}
}
