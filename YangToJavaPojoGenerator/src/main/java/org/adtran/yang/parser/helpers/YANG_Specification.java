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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.rmi.server.ExportException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author nataf
 * 
 *         The abstract class YANG_Specification is a common class for YANG
 *         module or sub-module.
 * 
 */
public abstract class YANG_Specification extends SimpleYangNode {

	static protected Hashtable<String, YANG_Specification> checkedSpecs = new Hashtable<String, YANG_Specification>();
	static protected boolean isCheckOk = true;
	protected Vector<YANG_Header> headers = new Vector<YANG_Header>();
	protected YANG_YangVersion yangversion = null;
	protected YANG_NameSpace namespace = null;
	protected YANG_Prefix prefix = null;
	protected Vector<YANG_Linkage> linkages = new Vector<YANG_Linkage>();
	protected Vector<YANG_Meta> metas = new Vector<YANG_Meta>();
	protected Vector<YANG_Revision> revisions = new Vector<YANG_Revision>();
	protected Vector<YANG_Body> bodies = new Vector<YANG_Body>();

	protected Vector<YANG_Module> importeds = new Vector<YANG_Module>();
	protected Vector<YANG_SubModule> includeds = new Vector<YANG_SubModule>();

	private boolean organization = false, contact = false, description = false,
			reference = false;

	private YangTreeNode schemaTree = null;

	/**
	 * Get the yang schema tree of this yang specification
	 * 
	 * @return a yang schema tree
	 */
	public YangTreeNode getSchemaTree() {
		return schemaTree;
	}

	protected YANG_Specification(int i) {
		super(i);
	}

	protected YANG_Specification(yang p, int i) {
		super(p, i);
	}

	/**
	 * Get the header of this yang specification. An header is either a
	 * namespace, a prefix, a yang version or a belongs-to a module.
	 * 
	 * @return a non-empty vector of header.
	 */
	public Vector<YANG_Header> getHeaders() {
		return headers;
	}

	/**
	 * Get the namespace of this yang specification
	 * 
	 * @return a namespace object.
	 */
	public YANG_NameSpace getNameSpace() {
		return namespace;
	}

	/**
	 * Get the prefix of this yang specification
	 * 
	 * @return a prefix object.
	 */
	public YANG_Prefix getPrefix() {
		return prefix;
	}

	/**
	 * Get the linkage of this yang specification. A linkage is either an import
	 * or an include.
	 * 
	 * @return a vector of linkage
	 */
	public Vector<YANG_Linkage> getLinkages() {
		return linkages;
	}

	/**
	 * Get the version of the yang language
	 * 
	 * @return a yang version object or null if no version was defined.
	 */
	public YANG_YangVersion getYangVersion() {
		return yangversion;
	}

	/**
	 * Get the meta statements or this yang specification
	 * 
	 * @return a vector of meta statements
	 */
	public Vector<YANG_Meta> getMetas() {
		return metas;
	}

	protected void addLinkage(YANG_Linkage l) {
		linkages.add(l);
	}

	protected void addRevision(YANG_Revision r) {
		revisions.add(r);
	}

	/**
	 * Get the revisions versions of this yang specification
	 * 
	 * @return a vector of revisions
	 */
	public Vector<YANG_Revision> getRevisions() {
		return revisions;
	}

	public void addBody(YANG_Body b) {
		bodies.add(b);
	}

	/**
	 * Get the bodies of this yang specification
	 * 
	 * @return a vector of yang bodies
	 */
	public Vector<YANG_Body> getBodies() {
		return bodies;
	}

	protected void addMeta(YANG_Meta m) {

		if (m instanceof YANG_Organization) {
			if (organization)
				YangErrorManager.addError(getFileName(), m.getLine(), m
						.getCol(), "unex_kw", "organization");
			else
				organization = true;
		}
		if (m instanceof YANG_Contact) {
			if (contact)
				YangErrorManager.addError(getFileName(), m.getLine(), m
						.getCol(), "unex_kw", "contact");
			else
				contact = true;
		}
		if (m instanceof YANG_Description) {
			if (description)
				YangErrorManager.addError(getFileName(), m.getLine(), m
						.getCol(), "unex_kw", "description");
			else
				description = true;
		}
		if (m instanceof YANG_Reference) {
			if (reference)
				YangErrorManager.addError(getFileName(), m.getLine(), m
						.getCol(), "unex_kw", "reference");
			else
				reference = true;
		}
		metas.add(m);
	}

	protected void check() {
		String[] path = new String[1];
		path[0] = ".";
		Vector<String> cked = new Vector<String>();
		check(path, cked);

	}

	/**
	 * Is this yang specification checked wihout error
	 * 
	 * @return true if no error are detected.
	 */
	public static boolean isCheckOk() {
		return isCheckOk;
	}

	/**
	 * Check of this specification.
	 * 
	 * @param path
	 *            for yang files used in import or include statements
	 */
	public void check(String[] path) {
		Vector<String> cked = new Vector<String>();
		check(path, cked);
	}

	protected YangContext check(String[] p, Vector<String> checked) {
		YangContext c = checkContext(p, checked);
		checkTreeNode(p);
		return c;

	}

	protected YangContext checkAsExternal(String[] p, Vector<String> checked) {
		YangContext c = checkContext(p, checked);
		return c;
	}

	@SuppressWarnings("unchecked")
	protected YangContext checkContext(String[] p, Vector<String> checkeds) {

		checkHeader(p);
		checkLinkage(p);
		checkRevisions();

		YangContext localcontext = buildSpecContext(p,
				(Vector<String>) checkeds.clone());

		localcontext.pendingUnions();

		localcontext.checkTypes();

		checkBodies(p, checkeds, localcontext);
		if (this instanceof YANG_SubModule) {
			YANG_SubModule submodule = (YANG_SubModule) this;
			localcontext.removeContext(submodule.getBelong().getBelong(),
					localcontext);
		}
		return localcontext;
	}

	private void checkRevisions() {
		Vector<YANG_Revision> duprev = new Vector<YANG_Revision>();
		for (YANG_Revision rev : getRevisions()) {
			rev.check();
			for (YANG_Revision rev2 : getRevisions()) {
				if (rev2 != rev && rev2.getDate().compareTo(rev.getDate()) == 0) {
					if (!duprev.contains(rev)) {
						duprev.add(rev);
						duprev.add(rev2);
						YangErrorManager.addError(getFileName(), rev.getLine(),
								rev.getCol(), "dup_rev", rev2.getLine());
					}
				}
			}
		}

	}

	private void checkBodies(String[] p, Vector<String> ckd, YangContext context) {

		for (Enumeration<YANG_Body> eb = getBodies().elements(); eb
				.hasMoreElements();) {
			YANG_Body body = eb.nextElement();
			YangContext bodycontext = context.clone();

			body.setRootNode(true);
			body.checkBody(bodycontext);
		}
		for (YANG_Body bd : getBodies()) {
			if (bd instanceof YANG_Grouping) {
				YANG_Grouping gping = (YANG_Grouping) bd;
				if (!gping.isUsed())
					YangErrorManager.addWarning(getFileName(), gping.getLine(),
							gping.getCol(), "unused", "grouping", gping
									.getBody());
			} else if (bd instanceof YANG_TypeDef) {
				YANG_TypeDef tdef = (YANG_TypeDef) bd;
				if (!tdef.isUsed())
					YangErrorManager.addWarning(getFileName(), tdef.getLine(),
							tdef.getCol(), "unused", "typedef", tdef.getBody());
			}
		}

		for (YANG_Import impo : getImports()) {
			if (!impo.isUsed())
				YangErrorManager.addWarning(getFileName(), impo.getLine(), impo
						.getCol(), "unused", "import", impo.getName());
		}
		cleanExternalWarning();

	}

	protected abstract void cleanExternalWarning();

	@SuppressWarnings("unchecked")
	public YangContext buildSpecContext(String[] paths, Vector<String> builded) {
		YangContext context = new YangContext(getImports(), this);
		if (getPrefix() != null)
			context.addLocalPrefix(getPrefix());

		YangContext submodulecontext = context.clone();

		if (importeds.size() == 0)
			checkImport(paths);
		if (includeds.size() == 0)
			checkInclude(paths);

		YangContext importedcontext = null;

		for (YANG_Module module : importeds) {
			String importedmodulename = module.getName();
			int line = 0, col = 0;
			for (YANG_Linkage lk : linkages) {
				if (lk.getName().compareTo(module.getName()) == 0) {
					line = lk.getLine();
					col = lk.getCol();
					if (lk instanceof YANG_Import) {
						YANG_Import impo = (YANG_Import) lk;
						if (impo.getRevision() != null)
							importedmodulename += "."
									+ impo.getRevision().getDate();
					}
				}
			}
			if (!builded.contains(importedmodulename)) {

				Vector<String> cks = (Vector<String>) builded.clone();
				importedcontext = module.check(paths, builded);
				context.merge(importedcontext);

			} else
				YangErrorManager.addError(getFileName(), line, col,
						"circ_impo", importedmodulename, getName());
		}

		for (YANG_SubModule submodule : includeds) {
			String includedsubmodulename = submodule.getName();
			int line = 0, col = 0;
			for (YANG_Linkage lk : linkages) {
				if (lk.getName().compareTo(submodule.getName()) == 0) {
					line = lk.getLine();
					col = lk.getCol();
				}
			}
			if (!builded.contains(includedsubmodulename)) {
				Vector<String> cks = (Vector<String>) builded.clone();
				YangContext includedcontext = submodule.check(paths, builded);
				if (this instanceof YANG_SubModule) {
					context.merge(includedcontext);
				} else {
					// context.mergeChecked(includedcontext);
					context.mergeChecked(includedcontext);
				}
			} else
				YangErrorManager.addError(getFileName(), line, col,
						"circ_include", includedsubmodulename, getName());
		}

		YangContext specontext = getThisSpecContext(context);

		builded.add(getName());

		return specontext;
	}

	private YangContext getThisSpecContext(YangContext context) {

		for (Enumeration<YANG_Body> eb = getBodies().elements(); eb
				.hasMoreElements();) {
			YANG_Body body = eb.nextElement();
			if (!(body instanceof YANG_Uses || body instanceof YANG_Augment))
				context.addNode(body);
		}
		return context;
	}

	protected abstract void checkHeader(String[] p);

	public abstract String getName();

	/**
	 * Get imported yang modules
	 * 
	 * @param paths
	 *            file system paths where to find modules
	 * @return a vector of yang specifications
	 */
	public Vector<YANG_Specification> getImportedModules(String[] paths) {
		Vector<YANG_Specification> im = new Vector<YANG_Specification>();
		for (Enumeration<YANG_Linkage> el = getLinkages().elements(); el
				.hasMoreElements();) {
			YANG_Linkage linkage = el.nextElement();
			if (linkage instanceof YANG_Import) {
				YANG_Import imported = (YANG_Import) linkage;
				String importedspecname = imported.getImportedModule();
				YANG_Specification importedspec = getExternal(paths,
						importedspecname, imported.getLine(), imported.getCol());
				im.add(importedspec);

			}
		}
		return im;
	}

	/**
	 * Get import statement of this yang module
	 * 
	 * @return a vector of imports
	 */
	public Vector<YANG_Import> getImports() {
		Vector<YANG_Import> imports = new Vector<YANG_Import>();
		for (Enumeration<YANG_Linkage> el = getLinkages().elements(); el
				.hasMoreElements();) {
			YANG_Linkage linkage = el.nextElement();
			if (linkage instanceof YANG_Import)
				imports.add((YANG_Import) linkage);

		}
		return imports;
	}

	/**
	 * Get include statement of this yang module
	 * 
	 * @return a vector of includes
	 */
	public Vector<YANG_Include> getIncludes() {
		Vector<YANG_Include> includes = new Vector<YANG_Include>();
		for (Enumeration<YANG_Linkage> el = getLinkages().elements(); el
				.hasMoreElements();) {
			YANG_Linkage linkage = el.nextElement();
			if (linkage instanceof YANG_Include)
				includes.add((YANG_Include) linkage);
		}
		return includes;
	}

	/**
	 * Get included yang sub-modules
	 * 
	 * @param paths
	 *            file system paths where to find sub-modules
	 * @return a vector of yang specifications
	 */
	public Vector<YANG_Specification> getIncludedSubModules(String[] paths) {

		Vector<YANG_Specification> is = new Vector<YANG_Specification>();
		for (Enumeration<YANG_Linkage> el = getLinkages().elements(); el
				.hasMoreElements();) {
			YANG_Linkage linkage = el.nextElement();
			if (linkage instanceof YANG_Include) {
				YANG_Include included = (YANG_Include) linkage;
				String includedspecname = included.getIncludedModule();
				YANG_Revision revision = included.getRevision();
				YANG_Specification includedspec = null;
				if (revision != null) {
					String incname = includedspecname;
					includedspecname += "." + revision.getDate();
					includedspec = getExternal(paths, includedspecname,
							incname, included.getLine(), included.getCol());
				} else
					includedspec = getExternal(paths, includedspecname,
							included.getLine(), included.getCol());

				if (includedspec != null) {
					included.setIncludedsubmodule(includedspec);
					is.add(includedspec);
				}
			}
		}
		return is;

	}

	/**
	 * check correctness of include and import statements but do not a check of
	 * these included or imported specifications
	 * 
	 * @param p
	 */
	protected void checkLinkage(String[] paths) {
		checkImport(paths);
		checkInclude(paths);
	}

	/**
	 * Check if imported modules are accessible and have a correct syntax
	 * 
	 * @param paths
	 *            directories paths where find yang modules and submodules
	 * 
	 */
	protected void checkImport(String[] paths) {

		for (YANG_Linkage link : getLinkages()) {
			if (link instanceof YANG_Import) {
				YANG_Import imported = (YANG_Import) link;
				String importedspecname = imported.getImportedModule();
				YANG_Revision revision = imported.getRevision();
				YANG_Specification importedspec = null;
				if (revision != null) {
					String impname = importedspecname;
					importedspecname += "@" + revision.getDate();
					importedspec = getExternal(paths, importedspecname,
							impname, imported.getLine(), imported.getCol());
				} else
					importedspec = getExternal(paths, importedspecname,
							imported.getLine(), imported.getCol());
				if (importedspec != null)
					imported.setImportedmodule(importedspec);
				if (!(importedspec instanceof YANG_Module))
					YangErrorManager.addError(getFileName(),
							imported.getLine(), imported.getCol(),
							"not_module", importedspecname);
				else if (!importeds.contains(importedspec))
					importeds.add((YANG_Module) importedspec);
			}
		}
	}

	protected abstract void checkInclude(String[] paths);

	protected YANG_Specification getExternal(String[] paths,
			String externalmodulename, int line, int col) {
		int i = 0;
		boolean found = false;
		YANG_Specification externalspec = null;
		while (i < paths.length && !found) {
			String directory = paths[i++];
			String yangspecfilename = directory + File.separator
					+ externalmodulename + ".yang";
			if (checkedSpecs.containsKey(externalmodulename))
				return checkedSpecs.get(externalmodulename);
			try {
				File externalfile = new File(yangspecfilename);
				yang.ReInit(new FileInputStream(externalfile));
				found = true;
				YangErrorManager.addWarning(getFileName(), line, col,
						"no_revision", yangspecfilename);

				try {
					yang.setFileName(yangspecfilename);
					externalspec = yang.Start();
				} catch (ParseException pe) {
					if (pe.currentToken != null)
						if (pe.currentToken.next != null)
							YangErrorManager.addError(getFileName(),
									pe.currentToken.next.beginLine,
									pe.currentToken.next.beginColumn,
									"unex_kw", pe.currentToken.next.image);
						else
							System.out.println(pe);
					else
						System.out.println(pe);
				}
			} catch (FileNotFoundException fnf) {
				// nothing to do
				// pass to the next path
			}
		}
		if (!found) {
			i = 0;
			Hashtable<String, int[]> yversions = new Hashtable<String, int[]>();
			while (i < paths.length) {
				File directory = new File(paths[i++]);
				FilenameFilter filter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						Pattern p = Pattern
								.compile("[A-Za-z-0-9_]+@[1-9][0-9]{3}-[0-1][0-9]-[0-3][0-9].yang");
						Matcher m = p.matcher(name);
						return m.matches();
					}
				};
				String[] fileNames = directory.list(filter);
				for (String fileName : fileNames) {
					String[] dates = fileName.split("@");
					String yyyymm[] = dates[1].split("-");
					String dd[] = yyyymm[2].split(".yang");
					int yyyymmdd[] = new int[3];
					yyyymmdd[0] = Integer.parseInt(yyyymm[0]);
					yyyymmdd[1] = Integer.parseInt(yyyymm[1]);
					yyyymmdd[2] = Integer.parseInt(dd[0]);
					yversions.put(paths[i - 1] + File.separator + fileName,
							yyyymmdd);
				}

			}
			// Looks for the more recent version
			if (yversions.size() >= 1)
				found = true;
			if (yversions.size() > 1) {
				int ymax = 0, mmax = 0, dmax = 0;
				String ymdmax = "";
				for (String yfname : yversions.keySet()) {
					for (String yfname2 : yversions.keySet()) {
						if (yfname.compareTo(yfname2) != 0) {
							int ymd[] = yversions.get(yfname);
							int ymd2[] = yversions.get(yfname2);
							if (ymd[0] > ymd2[0]) {
								if (ymd[0] > ymax) {
									ymax = ymd[0];
									mmax = ymd[1];
									dmax = ymd[2];
									ymdmax = yfname;
								} else if (ymd[0] == ymax) {
									if (ymd[1] > mmax) {
										mmax = ymd[1];
										dmax = ymd[2];
										ymdmax = yfname;
									} else if (ymd[1] == mmax) {
										if (ymd[2] > dmax) {
											dmax = ymd[2];
											ymdmax = yfname;
										}
									}
								}
							} else {
								if (ymd2[0] > ymax) {
									ymax = ymd2[0];
									mmax = ymd2[1];
									dmax = ymd2[2];
									ymdmax = yfname2;
								} else if (ymd2[0] == ymax) {
									if (ymd2[1] > mmax) {
										mmax = ymd2[1];
										dmax = ymd2[2];
										ymdmax = yfname2;
									} else if (ymd2[1] == mmax) {
										if (ymd2[2] > dmax) {
											dmax = ymd2[2];
											ymdmax = yfname2;
										}
									}
								}
							}

						}
					}
				}
				externalmodulename = ymdmax;
			} else
				for (String yn : yversions.keySet())
					externalmodulename = yn;
			if (!found)
				YangErrorManager.addError(getFileName(), line, col,
						"file_not_found", externalmodulename);
			else {
				YangErrorManager.addWarning(getFileName(), line, col,
						"revision_choosen", externalmodulename);

				if (checkedSpecs.containsKey(externalmodulename))
					return checkedSpecs.get(externalmodulename);
				try {
					File externalfile = new File(externalmodulename);
					yang.ReInit(new FileInputStream(externalfile));
					found = true;
					try {
						yang.setFileName(externalmodulename);
						externalspec = yang.Start();
					} catch (ParseException pe) {
						if (pe.currentToken != null)
							if (pe.currentToken.next != null)
								YangErrorManager.addError(getFileName(),
										pe.currentToken.next.beginLine,
										pe.currentToken.next.beginColumn,
										"unex_kw", pe.currentToken.next.image);
							else
								System.out.println(pe);
						else
							System.out.println(pe);
					}
				} catch (NullPointerException np) {
				} catch (FileNotFoundException fnf) {
					// nothing to do // pass to the next path }
				}

			}
		}
		if (externalmodulename != null && externalspec != null)
			checkedSpecs.put(externalmodulename, externalspec);
		return externalspec;

	}

	protected YANG_Specification getExternal(String[] paths,
			String revisionmodulename, String external, int line, int col) {
		int i = 0;
		boolean found = false;
		YANG_Specification externalspec = null;
		while (i < paths.length && !found) {
			String directory = paths[i++];
			String yangspecfilename = directory + File.separator
					+ revisionmodulename + ".yang";
			if (checkedSpecs.containsKey(revisionmodulename))
				return checkedSpecs.get(revisionmodulename);
			try {
				File externalfile = new File(yangspecfilename);
				yang.ReInit(new FileInputStream(externalfile));
				found = true;
				try {
					yang.setFileName(yangspecfilename);
					externalspec = yang.Start();
				} catch (ParseException pe) {
					if (pe.currentToken != null)
						if (pe.currentToken.next != null)
							YangErrorManager.addError(getFileName(),
									pe.currentToken.next.beginLine,
									pe.currentToken.next.beginColumn,
									"unex_kw", pe.currentToken.next.image);
						else
							System.out.println(pe);
					else
						System.out.println(pe);
				}
			} catch (NullPointerException np) {
			} catch (FileNotFoundException fnf) {
				// nothing to do
				// pass to the next path
			}
		}

		if (!found)
			YangErrorManager.addError(getFileName(), line, col,
					"file_not_found", revisionmodulename);
		// externalspec = getExternal(paths, external, line, col);
		else if (revisionmodulename != null && externalspec != null)
			checkedSpecs.put(revisionmodulename, externalspec);
		return externalspec;
	}

	private void checkTreeNode(String[] p) {
		Vector<String> builded = new Vector<String>();
		Hashtable<String, YangTreeNode> importedtreenodes = new Hashtable<String, YangTreeNode>();
		builded.add(getName());
		for (YANG_Specification spec : importeds) {
			YangTreeNode iytn = spec.buildTreeNode(p, builded,
					importedtreenodes);
			for (YANG_Import imp : getImports()) {
				if (imp.getImportedModule().compareTo(spec.getName()) == 0)
					importedtreenodes.put(imp.getPrefix().getPrefix(), iytn);
			}
		}
		schemaTree = buildTreeNode(p, builded, importedtreenodes);
		schemaTree.check(this, schemaTree, schemaTree, importedtreenodes);
	}

	private YangTreeNode buildTreeNode(String[] p, Vector<String> builded,
			Hashtable<String, YangTreeNode> importedtreenodes) {

		// for (Enumeration<YANG_Specification> ei = getImportedModules(p)
		// .elements(); ei.hasMoreElements();) {//
		for (YANG_Specification spec : importeds) {
			// YANG_Specification spec = ei.nextElement();
			if (spec != null)
				if (!builded.contains(spec.getName())) {
					builded.add(spec.getName());
					YangTreeNode itn = spec.buildTreeNode(p, builded,
							importedtreenodes);
					importedtreenodes.put(spec.getName(), itn);
				}
		}

		YangTreeNode root = new YangTreeNode();

		for (YANG_Body body : bodies)
			if (body instanceof YANG_DataDef) {
				YANG_DataDef ddef = (YANG_DataDef) body;
				Vector<YangTreeNode> sons = ddef.groupTreeNode(root);
				for (YangTreeNode son : sons)
					root.addChild(son);
			} else if (body instanceof YANG_Rpc) {
				YANG_Rpc rpc = (YANG_Rpc) body;
				YangTreeNode ytnrpc = new YangTreeNode();
				ytnrpc.setNode(rpc);
				ytnrpc.setParent(root);
				root.addChild(ytnrpc);
				if (rpc.getInput() != null) {
					YangTreeNode ytnrpcin = new YangTreeNode();
					IoDataDef ioddef = new IoDataDef(rpc.getInput());
					ytnrpcin.setNode(ioddef);
					ytnrpcin.setParent(ytnrpc);
					ytnrpc.addChild(ytnrpcin);
					for (YANG_DataDef ddef : rpc.getInput().getDataDefs())
						for (YangTreeNode ichild : ddef.groupTreeNode(ytnrpcin))
							ytnrpcin.addChild(ichild);
				} else {
					YANG_Input in = new YANG_Input(0);
					in.setLine(rpc.getLine());
					in.setCol(rpc.getCol());
					in.setFileName(rpc.getFileName());
					IoDataDef ioddef = new IoDataDef(in);
					YangTreeNode ytnin = new YangTreeNode();
					ytnin.setNode(ioddef);
					ytnin.setParent(ytnrpc);
					ytnrpc.addChild(ytnin);
				}
				if (rpc.getOutput() != null) {
					YangTreeNode ytnrpcout = new YangTreeNode();
					IoDataDef ioddef = new IoDataDef(rpc.getOutput());
					ytnrpcout.setNode(ioddef);
					ytnrpcout.setParent(ytnrpc);
					ytnrpc.addChild(ytnrpcout);
					for (YANG_DataDef ddef : rpc.getOutput().getDataDefs())
						for (YangTreeNode ochild : ddef
								.groupTreeNode(ytnrpcout))
							ytnrpcout.addChild(ochild);
				} else {
					YANG_Output out = new YANG_Output(0);
					out.setLine(rpc.getLine());
					out.setCol(rpc.getCol());
					out.setFileName(rpc.getFileName());
					IoDataDef ioddef = new IoDataDef(out);
					YangTreeNode ytnout = new YangTreeNode();
					ytnout.setNode(ioddef);
					ytnout.setParent(ytnrpc);
					ytnrpc.addChild(ytnout);

				}
			} else if (body instanceof YANG_Notification) {
				YANG_Notification notif = (YANG_Notification) body;
				YangTreeNode ytnn = new YangTreeNode();
				ytnn.setNode(notif);
				ytnn.setParent(root);
				root.addChild(ytnn);
				for (YANG_DataDef ddef : notif.getDataDefs())
					for (YangTreeNode child : ddef.groupTreeNode(ytnn))
						ytnn.addChild(child);
			}

		for (YANG_Specification spec : getIncludedSubModules(p)) {
			YangTreeNode includedtreenode = spec.buildTreeNode(p, builded,
					importedtreenodes);
			for (YangTreeNode includednode : includedtreenode.getChilds())
				root.includeNode(includednode);
		}

		Vector<YANG_Augment> vaugs = new Vector<YANG_Augment>();

		int iaug = 0;
		for (YANG_Body body : bodies)
			if (body instanceof YANG_Augment) {
				YANG_Augment aug = (YANG_Augment) body;
				vaugs.add(iaug++, aug);
			}
		String[] taugs = new String[vaugs.size()];

		for (int i = 0; i < vaugs.size(); i++)
			taugs[i] = vaugs.get(i).getAugment();

		for (int i = 0; i < taugs.length; i++)
			for (int j = i + 1; j < taugs.length; j++) {
				String[] ti = taugs[i].split("/");
				String[] tj = taugs[j].split("/");
				if (ti.length > tj.length) {
					String ls = taugs[i];
					YANG_Augment lai = vaugs.get(i);
					YANG_Augment laj = vaugs.get(j);
					taugs[i] = taugs[j];
					taugs[j] = ls;
					vaugs.remove(i);
					vaugs.add(i, laj);
					vaugs.remove(j);
					vaugs.add(j, lai);
				}
			}

		for (int i = 0; i < taugs.length; i++) {
			if (i == 0 && taugs[i].indexOf("/") == -1)
				taugs[i] = "/" + taugs[i];
			YANG_Body augmentedbody = root.getBodyInTree(this, root,
					importedtreenodes, taugs[i]);
			if (augmentedbody == null) {
				YANG_Body augmented = null;
				for (String pref : importedtreenodes.keySet()) {
					YangTreeNode ytn = importedtreenodes.get(pref);
					augmented = ytn.getBodyInTree(this, root,
							importedtreenodes, taugs[i]);
					if (augmented != null)
						augmentedbody = augmented;
				}
				if (augmentedbody == null) {
					YangErrorManager.addError(getFileName(), vaugs.get(i)
							.getLine(), vaugs.get(i).getCol(),
							"augmented_not_found", taugs[i]);
				}
			} else {
				YANG_Augment aug = vaugs.get(i);
				aug.checkAugment(augmentedbody);

				YangTreeNode augmentednode = root.getNodeInTree(this, root,
						importedtreenodes, taugs[i]);
				augmentednode.augments(aug);
			}
		}

		for (YANG_Body body : bodies)
			if (body instanceof YANG_Deviation) {
				YANG_Deviation deviation = (YANG_Deviation) body;
				String deviated = deviation.getDeviation();
				YangTreeNode deviatednode = root.getNodeInTree(this, root,
						importedtreenodes, deviated);
				if (deviatednode == null) {
					YangErrorManager.addError(getFileName(), deviation
							.getLine(), deviation.getCol(),
							"deviate_not_found", deviated);
				} else {
					if (deviation.getDeviateNotSupported() != null) {
						deviatednode.getParent().removeChild(deviatednode);
					}
					for (YANG_DeviateAdd dadd : deviation.getDeviateAdds()) {
						dadd.deviates(deviatednode);
					}
					for (YANG_DeviateReplace drep : deviation
							.getDeviateReplaces()) {
						drep.deviates(deviatednode);
					}
					for (YANG_DeviateDelete ddel : deviation
							.getDeviateDeletes()) {
						ddel.deviates(deviatednode);
					}
				}
			}

		return root;
	}

}
