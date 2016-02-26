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
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

public class YangSpecTypes implements Serializable {

	private Hashtable<String, String> deriveds = new Hashtable<String, String>();
	private Hashtable<String, YANG_TypeDef> typedefs = new Hashtable<String, YANG_TypeDef>();

	public YangSpecTypes clone() {
		YangSpecTypes cst = new YangSpecTypes();

		for (Enumeration<String> es = deriveds.keys(); es.hasMoreElements();) {
			String k = es.nextElement();
			String t = deriveds.get(k);
			cst.add(k, t, typedefs.get(k));
		}
		return cst;
	}

	public void add(String derived, String basetype, YANG_TypeDef td) {
		deriveds.put(derived, basetype);
		typedefs.put(derived, td);
	}

	public Enumeration<String> keys() {
		return deriveds.keys();
	}

	public String get(String k) {
		if (deriveds.containsKey(k))
			return deriveds.get(k);
		return null;
	}

	public int getTypesNumber() {
		return deriveds.size();
	}

	/**
	 * Merge external typedef definition
	 * 
	 * @param yst
	 *            external typedefs
	 */
	public void merge(YangSpecTypes yst) {
		for (Enumeration<String> ek = yst.keys(); ek.hasMoreElements();) {
			String k = ek.nextElement();
			if (!deriveds.containsKey(k))
				add(k, yst.get(k), yst.getTypeDef(k));
		}
	}

	public void mergeChecked(YangSpecTypes yst) {
		for (Enumeration<String> ek = yst.keys(); ek.hasMoreElements();) {
			String k = ek.nextElement();
			if (!deriveds.containsKey(k))
				add(k, yst.get(k), yst.getTypeDef(k));
			else
				YangErrorManager.addError(yst.getTypeDef(k).getFileName(), yst
						.getTypeDef(k).getLine(), yst.getTypeDef(k).getCol(),
						"dup_child", yst.getTypeDef(k).getBody(), typedefs.get(
								k).getLine(), typedefs.get(k).getCol());
		}
	}

	/**
	 * Get the typedef of the given type
	 * 
	 * @param k
	 *            the canonical name of the type
	 * @return the typedef (or null if not present)
	 */
	protected YANG_TypeDef getTypeDef(String k) {
		return typedefs.get(k);
	}

	public boolean contains(YangSpecTypes yst) {

		for (Enumeration<String> ek = yst.keys(); ek.hasMoreElements();) {
			String k = ek.nextElement();
			if (!deriveds.containsKey(k))
				return false;
		}
		return true;

	}

	public boolean isEmpty() {
		return deriveds.size() == 0;
	}

	public boolean isDefined(String t) {
		if (!YangBuiltInTypes.isBuiltIn(t))
			return deriveds.containsKey(t);
		return true;
	}

	public void check() {
		for (Enumeration<String> et = deriveds.elements(); et.hasMoreElements();) {
			String basetype = et.nextElement();
			if (!YangBuiltInTypes.isBuiltIn(basetype)) {
				String s = null;
				if (!deriveds.containsKey(basetype)) {
					YANG_TypeDef td = null;
					boolean found = false;
					for (Enumeration<String> ek = deriveds.keys(); ek
							.hasMoreElements()
							&& !found;) {
						s = ek.nextElement();
						if (deriveds.get(s).compareTo(basetype) == 0) {
							found = true;
							td = typedefs.get(s);
						}
					}
					if (td != null) {
						td.setCorrect(false);
						YangErrorManager.addError(td.getFileName(), td
								.getLine(), td.getCol(), "unknown_type", td.getType().getType());
					}
				} /*
				 * else { String der = deriveds.get(basetype); if
				 * (der.compareTo(basetype) == 0) { YANG_TypeDef td =
				 * typedefs.get(basetype); td.setCorrect(false);
				 * YangErrorManager .add(td.getFileName(), td.getLine(),
				 * td.getCol(),
				 * MessageFormat.format(YangErrorManager.messages.getString
				 * ("circ_dep"),basetype));
				 * 
				 * } }
				 */
			}
		}
		for (Enumeration<String> et = deriveds.elements(); et.hasMoreElements();) {
			String basetype = et.nextElement();
			if (!YangBuiltInTypes.isBuiltIn(basetype)) {
				Vector<String> chain = new Vector<String>();
				chain.add(basetype);
				if (deriveds.get(basetype) != null) {
					YANG_TypeDef origin = typedefs.get(deriveds.get(basetype));
					checkChain(origin, chain, deriveds.get(basetype));
				}
			}
		}
	}

	protected boolean checkChain(YANG_TypeDef o, Vector<String> b, String d) {
		if (b.contains(d)) {
			YANG_TypeDef bt = typedefs.get(d);
			if (bt.isCorrect()) {
				bt.setCorrect(false);
				for (Enumeration<String> es = b.elements(); es
						.hasMoreElements();) {
					String tn = es.nextElement();
					YANG_TypeDef td = typedefs.get(tn);
					td.setCorrect(false);
				}

				YangErrorManager.addError(o.getFileName(), o.getLine(), o
						.getCol(), "circ_dep", unprefix(d));

				return false;
			} else
				return false;
		}
		if (!YangBuiltInTypes.isBuiltIn(d)) {
			b.add(d);
			return checkChain(o, b, deriveds.get(d));
		}
		return true;
	}

	private String unprefix(String s) {
		return s.substring(s.indexOf(':') + 1);
	}

	public String getBuiltInType(String t) {
		if (t == null)
			return t;
		else if (YangBuiltInTypes.isBuiltIn(t))
			return t;
		else
			return getBuiltInType(deriveds.get(t));
	}

	/**
	 * Get the typedef that defines the base type of the given type
	 * 
	 * @param t
	 *            the canonical name of the type
	 * @return the typedef or null if the type is a built-in type
	 */
	public YANG_TypeDef getBaseType(String t) {
		if (YangBuiltInTypes.isBuiltIn(t))
			return null;
		String type = deriveds.get(t);
		if (type == null)
			return null;
		return typedefs.get(type);
	}

	/**
	 * Get the typedef that defines the given type
	 * 
	 * @param t
	 *            the canonical name of the type
	 * @return the typedef of null if the type is a built-in type
	 */
	public YANG_TypeDef getDefiningTypeDef(String t) {
		if (YangBuiltInTypes.isBuiltIn(t))
			return null;
		for (Enumeration<String> ek = typedefs.keys(); ek.hasMoreElements();) {
			String k = ek.nextElement();
			if (deriveds.get(k).compareTo(t) == 0)
				return typedefs.get(k);
		}
		return null;

	}

	/**
	 * Get the typedef that define the base type of the given typedef
	 * 
	 * @param td
	 *            a typedef
	 * @return the typedef or null if the base type is a built-in type.
	 */
	public YANG_TypeDef getBaseType(YANG_TypeDef td) {
		if (YangBuiltInTypes.isBuiltIn(td.getType().getType()))
			return null;
		for (Enumeration<String> ek = typedefs.keys(); ek.hasMoreElements();) {
			String k = ek.nextElement();
			if (typedefs.get(k) == td) {
				String b = deriveds.get(k);
				return typedefs.get(b);
			}
		}
		System.err.println("panic in getting base type " + td.getTypeDef());
		System.exit(-1);
		return null;

	}

	public String toString() {
		String result = new String();
		for (Enumeration<String> es = deriveds.keys(); es.hasMoreElements();) {
			String k = es.nextElement();
			result += k + "\t:  " + deriveds.get(k) + "\n";
		}
		return result;
	}

	public void removeType(String module, YangSpecTypes st) {
		YangSpecTypes res = new YangSpecTypes();
		for (Enumeration<String> et = keys(); et.hasMoreElements();) {
			String t = et.nextElement();
			if (!st.deriveds.keySet().contains(t)) {
				res.deriveds.put(t, deriveds.get(t));
				res.typedefs.put(t, typedefs.get(t));
			}
		}
		setSpecTypes(res);

	}

	private void setSpecTypes(YangSpecTypes res) {
		deriveds = res.deriveds;
		typedefs = res.typedefs;

	}
}
