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
import java.util.*;

public class YangContext implements Serializable{

	private YangSpecTypes spectypes = null;
	private YangSpecNode specnodes = null;
	private Hashtable<String, YANG_Type> pendinguniontypes = new Hashtable<String, YANG_Type>();
	private Vector<YANG_Import> imports = null;
	private YANG_Specification spec = null;
	private YANG_Prefix localPrefix = null;

	public YANG_Prefix getLocalPrefix() {
		return localPrefix;
	}

	private YangContext(YangSpecTypes st, YangSpecNode sn,
			Vector<YANG_Import> i, YANG_Specification s) {
		spectypes = st;
		specnodes = sn;
		imports = i;
		spec = s;
	}

	/**
	 * Create a context for a naming scope
	 * 
	 * @param i
	 *            imported modules
	 * @param s
	 *            current yang specification
	 */
	public YangContext(Vector<YANG_Import> i, YANG_Specification s) {
		spectypes = new YangSpecTypes();
		specnodes = new YangSpecNode();
		spec = s;
		imports = i;
	}

	/**
	 * Get imported modules
	 * 
	 * @return a vector of YANG_Import for this context
	 */
	public Vector<YANG_Import> getImports() {
		return imports;
	}

	/**
	 * Add all nodes of the given context to this context.
	 * 
	 * @param sc
	 */
	public void addSubContext(YangContext sc) {
		Hashtable<String, YANG_Body> subnodes = sc.getSpecNodes().getNodes();
		for (Enumeration<String> ek = subnodes.keys(); ek.hasMoreElements();) {
			String k = ek.nextElement();
			YANG_Body body = subnodes.get(k);
			if (body instanceof YANG_TypeDef)
				addTypeDef((YANG_TypeDef) body);
			else if (body instanceof YANG_Grouping)
				addGrouping((YANG_Grouping) body);
			else
				specnodes.addSubNode(k, body);
		}
		// checkTypes();
		pendingUnions();
	}

	/**
	 * Resolve types used inside an union type. Must be called after all
	 * typedefs of a naming scope are added
	 */
	public void pendingUnions() {
		for (Enumeration<String> ek = pendinguniontypes.keys(); ek
				.hasMoreElements();) {
			String k = ek.nextElement();
			YANG_Type ptype = pendinguniontypes.get(k);
			if (spectypes.get(k) == null) {
				YangErrorManager.addError(ptype.getFileName(), ptype.getLine(),
						ptype.getCol(), "unlnow_type", ptype.getType());
			}
		}

	}

	/**
	 * Add a node to this context
	 * 
	 * @param b
	 *            a yang node
	 */
	public void addNode(YANG_Body b) {

		if (b instanceof YANG_TypeDef)
			addTypeDef((YANG_TypeDef) b);
		else if (b instanceof YANG_Grouping)
			addGrouping((YANG_Grouping) b);
		else if (b instanceof YANG_Extension)
			addExtension((YANG_Extension) b);
		else
			specnodes.put(getModuleSpecName() + ":" + b.getBody(), b);
	}

	public void addUsedNode(YANG_Uses u, YANG_Body b) {

		if (b instanceof YANG_TypeDef)
			addTypeDef((YANG_TypeDef) b);
		else if (b instanceof YANG_Grouping)
			addGrouping((YANG_Grouping) b);
		else if (b instanceof YANG_Extension)
			addExtension((YANG_Extension) b);
		else
			specnodes.putUsed(u, getModuleSpecName() + ":" + b.getBody(), b);
	}

	/**
	 * Ask if a node is defined
	 * 
	 * @param n
	 *            the name of the node
	 * @return true if defined in this context;
	 */
	public boolean isNodeDefined(String n) {
		return specnodes.isDefined(getModuleSpecName() + ":" + n);
	}

	/**
	 * Ask if a type is defined in this context.
	 * 
	 * @param t
	 * @return true if the type is in this context, false else.
	 */
	public boolean isTypeDefined(YANG_Type t) {
		String cn = canonicalTypeName(t, new Hashtable<String, YANG_Type>());
		return specnodes.isDefinedAsTypeDef(cn);
	}

	public boolean isTypeDefined(String t) {
		return specnodes.isDefinedAsTypeDef(getModuleSpecName() + ":" + t);
	}

	/**
	 * Ask if an used extension exist in this context.
	 * 
	 * @param u
	 *            an unknown statement
	 * @return true if the extension is defined in this context.
	 */
	public boolean isExtensionDefined(YANG_Unknown u) {
		String prefix = u.getPrefix();
		String suffix = u.getExtension();
		YANG_Type faketype = new YANG_Type(0);
		faketype.setType(prefix + ":" + suffix);
		String fakenametype = canonicalTypeName(faketype,
				new Hashtable<String, YANG_Type>());
		return specnodes.isDefinedAsExtension(fakenametype);
	}

	public YANG_Extension getExtension(YANG_Unknown u) {
		String prefix = u.getPrefix();
		String suffix = u.getExtension();
		YANG_Type faketype = new YANG_Type(0);
		faketype.setType(prefix + ":" + suffix);
		String fakenametype = canonicalTypeName(faketype,
				new Hashtable<String, YANG_Type>());
		return specnodes.getExtension(fakenametype);
	}

	/**
	 * Ask if an used grouping element exist.
	 * 
	 * @param u
	 *            a YANG_Uses element
	 * @return true if the grouping used exist in this context.
	 */
	public boolean isGroupingDefined(YANG_Uses u) {
		String uses = u.getUses();
		if (uses.indexOf(':') != -1) {

			String prefix = uses.substring(0, uses.indexOf(':'));
			String suffix = uses
					.substring(uses.indexOf(':') + 1, uses.length());
			String cn = null;
			cn = canonicalTypeName(prefix, suffix,u.getFileName(), u.getLine(), u.getCol(),
					new Hashtable<String, YANG_Type>());
			return specnodes.isDefinedAsGrouping(cn);
		} else
			return specnodes.isDefinedAsGrouping(getModuleSpecName() + ":"
					+ uses);
	}

	public boolean isGroupingDefined(String g) {
		return specnodes.isDefinedAsGrouping(getModuleSpecName() + ":" + g);
	}

	public YANG_Grouping getUsedGrouping(YANG_Uses u) {
		String uses = u.getUses();
		if (uses.indexOf(':') != -1) {

			String prefix = uses.substring(0, uses.indexOf(':'));
			String suffix = uses
					.substring(uses.indexOf(':') + 1, uses.length());
			String cn = null;
			cn = canonicalTypeName(prefix, suffix, u.getFileName(), u.getLine(), u.getCol(),
					new Hashtable<String, YANG_Type>());
			return specnodes.getUsedGrouping(cn);
		} else {
			return specnodes.getUsedGrouping(getModuleSpecName() + ":" + uses);
		}

	}

	/**
	 * Clone the context. It must be used before going into an substatement
	 */
	public YangContext clone() {
		YangSpecTypes cspectypes = spectypes.clone();
		YangSpecNode cspecnodes = specnodes.clone();
		return new YangContext(cspectypes, cspecnodes, imports, spec);
	}

	/**
	 * 
	 * @param c
	 *            an external context
	 * @return true if all externals types are inside this context
	 */
	public boolean contains(YangContext c) {
		// return spectypes.contains(c.getSpecTypes());
		return specnodes.contains(c.getSpecNodes());
	}

	/**
	 * Get the context specification
	 * 
	 * @return a YANG_Module or a YANG_subModule
	 */
	public YANG_Specification getSpec() {
		return spec;
	}

	/**
	 * Get the module name or the belongs-to submodule name, not the submodule
	 * name.
	 * 
	 * @return a string in the module name space.
	 */
	public String getModuleSpecName() {
		if (spec instanceof YANG_Module)
			return spec.getName();
		else
			return ((YANG_SubModule) spec).getBelong().getBelong();
	}

	private YangSpecTypes getSpecTypes() {
		return spectypes;
	}

	private YangSpecNode getSpecNodes() {
		return specnodes;
	}

	public YANG_Body get(String b) {
		return specnodes.get(getModuleSpecName() + ":" + b);
	}

	public YANG_Body getIdentity(YANG_Base base) {
		String t = "";
		t = canonicalTypeName(base, new Hashtable<String, YANG_Type>());
		return specnodes.getIdentity(t);

	}

	public YANG_Body getFeature(YANG_IfFeature iff) {
		String t = "";
		t = canonicalTypeName(iff, new Hashtable<String, YANG_Type>());
		return specnodes.getFeature(t);
	}

	/**
	 * Merge an external context to the current context.
	 * 
	 * @param c
	 *            externals context
	 */
	public void merge(YangContext c) {
		spectypes.merge(c.getSpecTypes());
		specnodes.merge(c.getSpecNodes());
	}

	public void mergeChecked(YangContext c) {
		spectypes.mergeChecked(c.getSpecTypes());
		specnodes.mergeChecked(c.getSpecNodes());
	}

	/**
	 * Check if all typedefs have a resolved type and there is no circular
	 * references
	 * 
	 * 
	 */
	public void checkTypes() {
		spectypes.check();
	}

	/**
	 * Get the typedef statement that defines the given typedef
	 * 
	 * @param typedef
	 * @return the YANG_TypeDef
	 */
	public YANG_TypeDef getTypeDef(YANG_TypeDef typedef) {
		for (Enumeration<YANG_Body> eb = getSpecNodes().getNodes().elements(); eb
				.hasMoreElements();) {
			YANG_Body body = eb.nextElement();
			if (body instanceof YANG_TypeDef)
				if (((YANG_TypeDef) body).getTypeDef().compareTo(
						typedef.getType().getType()) == 0)
					return (YANG_TypeDef) body;
		}
		return null;

	}

	private void addGrouping(YANG_Grouping g) {

		// Are we trying to redefine a built-in type ?

		if (YangBuiltInTypes.isBuiltIn(g.getGrouping())) {
			YangErrorManager.addError(spec.getName(), g.getLine(), g.getCol(),
					"grouping");
			return;
		}

		specnodes.put(getModuleSpecName() + ":" + g.getGrouping(), g);
	}

	private void addExtension(YANG_Extension e) {

		specnodes.put(getModuleSpecName() + ":" + e.getExtension(), e);
	}

	private void addTypeDef(YANG_TypeDef td) {

		// Are we trying to redefine a built-in type ?

		if (YangBuiltInTypes.isBuiltIn(td.getTypeDef())) {
			YangErrorManager.addError(td.getFileName(), td.getLine(), td.getCol(),
					"illegal_builtin", td.getBody());
			return;
		}

		// Is there a type statement ?

		if (td.getType() == null) {
			YangErrorManager.addError(getSpec().getName(), td.getLine(), td
					.getCol(), "type_expec");
			return;
		}

		YANG_Type type = td.getType();

		String typestr = canonicalTypeName(type, pendinguniontypes);

		specnodes.put(getModuleSpecName() + ":" + td.getTypeDef(), td);
		spectypes.add(getModuleSpecName() + ":" + td.getTypeDef(), typestr, td);

	}

	private String canonicalTypeName(YANG_Type type,
			Hashtable<String, YANG_Type> pendinguniontype) {
		String prefix = null;
		String suffix = null;

		if (type.isPrefixed())
			prefix = type.getPrefix();
		suffix = type.getSuffix();
		String cn = null;
		cn = canonicalTypeName(prefix, suffix, type.getFileName(), type.getLine(), type.getCol(),
				pendinguniontype);
		if (YangBuiltInTypes.union.compareTo(suffix) == 0) {
			YANG_UnionSpecification unionspec = type.getUnionSpec();
			if (unionspec != null)
				pendingUnionTypes(unionspec, pendinguniontype, imports, spec);
			else
				YangErrorManager.addError(type.getFileName(), type.getLine(), type
						.getCol(), "union_no_type");
		}
		return cn;

	}

	private String canonicalTypeName(YANG_IfFeature iff,
			Hashtable<String, YANG_Type> pendinguniontype) {
		String prefix = null;
		String suffix = null;

		if (iff.isPrefixed())
			prefix = iff.getPrefix();
		suffix = iff.getSuffix();
		String cn = null;
		cn = canonicalTypeName(prefix, suffix, iff.getFileName(), iff.getLine(), iff.getCol(),
				pendinguniontype);
		return cn;
	}

	private String canonicalTypeName(YANG_Base base,
			Hashtable<String, YANG_Type> pendinguniontype) {
		String prefix = null;
		String suffix = null;

		if (base.isPrefixed())
			prefix = base.getPrefix();
		suffix = base.getSuffix();
		String cn = null;
		cn = canonicalTypeName(prefix, suffix, base.getFileName(), base.getLine(), base.getCol(),
				pendinguniontype);
		return cn;
	}

	private String canonicalTypeName(String prefix, String suffix, String filename, int line,
			int col, Hashtable<String, YANG_Type> pendinguniontype) {
		String result = null;
		if (prefix == null) {
			if (YangBuiltInTypes.isBuiltIn(suffix))
				return suffix;
			else
				return getModuleSpecName() + ":" + suffix;
		} else {
			if (YangBuiltInTypes.isBuiltIn(suffix))
				YangErrorManager.addError(filename, line, col,
						"illegal_builtin", prefix + ":" + suffix);
			boolean found = false;
			if (getLocalPrefix() != null)
				if (getLocalPrefix().getPrefix().compareTo(prefix) == 0) {
					found = true;
					result = getSpec().getName() + ":" + suffix;
				}
			for (Enumeration<YANG_Import> ei = imports.elements(); ei
					.hasMoreElements()
					&& !found;) {
				YANG_Import impo = ei.nextElement();

				if (impo.getPrefix().getPrefix().compareTo(prefix) == 0) {
					result = impo.getImportedModule() + ":" + suffix;
					found = true;
					impo.setUsed(found);
				}
			}
			if (!found) {
				if (spec instanceof YANG_Module) {
					if (spec.getPrefix().getPrefix().compareTo(prefix) == 0) {
						found = true;
						result = spec.getName() + ":" + suffix;
					}
				}
				if (!found) {
					YangErrorManager.addError(filename, line, col,
							"not_imported_prefix", prefix);
				}
			}
		}
		return result;
	}

	private void pendingUnionTypes(YANG_UnionSpecification us,
			Hashtable<String, YANG_Type> pendinguniontype,
			Vector<YANG_Import> imports, YANG_Specification spec) {

		for (Enumeration<YANG_Type> et = us.getTypes().elements(); et
				.hasMoreElements();) {
			YANG_Type utype = et.nextElement();
			if (!YangBuiltInTypes.isBuiltIn(utype.getType()))
				pendinguniontype.put(
						canonicalTypeName(utype, pendinguniontypes), utype);
			else if (YangBuiltInTypes.union.compareTo(utype.getType()) == 0) {
				YANG_UnionSpecification uspec = utype.getUnionSpec();
				pendingUnionTypes(uspec, pendinguniontype, imports, spec);
			}
		}

	}

	/**
	 * Get the built-in base type of the given type name
	 * 
	 * @param t
	 * @return
	 */
	public String getBuiltInType(YANG_Type type) {
		String t = canonicalTypeName(type, new Hashtable<String, YANG_Type>());
		return getSpecTypes().getBuiltInType(t);
	}

	/**
	 * Get the typedef used to defined the given type
	 * 
	 * @param type
	 * @return typedef
	 */
	public YANG_TypeDef getBaseType(YANG_Type type) {
		String t = canonicalTypeName(type, new Hashtable<String, YANG_Type>());
		return getSpecTypes().getBaseType(t);
	}

	/**
	 * Get the typedef that define the base type of the given typedef
	 * 
	 * @param td
	 *            a typedef
	 * @return the typedef or null if the base type is a built-in type.
	 */
	public YANG_TypeDef getBaseTypeDef(YANG_TypeDef typedef) {
		return getSpecTypes().getBaseType(typedef);
	}

	/**
	 * Get the typedef that defines the given type
	 * 
	 * @param t
	 *            the canonical name of the type
	 * @return the typedef of null if the type is a built-in type
	 */
	public YANG_TypeDef getTypeDef(YANG_Type type) {
		String t = canonicalTypeName(type, new Hashtable<String, YANG_Type>());
		return getSpecTypes().getTypeDef(t);
	}

	/**
	 * Print types resolution and nodes name
	 */
	public String toString() {
		return spectypes.toString() + "\n" + specnodes.toString();
	}

	public void addLocalPrefix(YANG_Prefix prefix) {

		localPrefix = prefix;

	}

	public void removeContext(String module, YangContext importedcontext) {
		specnodes.removeNode(module, importedcontext.getSpecNodes());
		spectypes.removeType(module, importedcontext.getSpecTypes());
		
	}

}
