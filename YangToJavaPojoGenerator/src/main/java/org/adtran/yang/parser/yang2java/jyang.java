package org.adtran.yang.parser.yang2java;

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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.*;

import org.adtran.yang.parser.helpers.*;
import org.adtran.yang.parser.tools.*;

public class jyang {

	private Hashtable<String, YANG_Specification> yangsSpecs = new Hashtable<String, YANG_Specification>();
	private boolean parsingOk;
	private boolean warning = false;
	private boolean output = true;
	private static boolean reinit = false;
	private String outputfilename = null;

	public static void main(String[] args) {
		new jyang(args);
	}

	public jyang(String[] args, boolean output) {
		this.output = output;
		parse(args);
	}

	public jyang(String[] args) {
		this(args, true);
	}

	public void parse(String[] args) {
		// Parsing arguments
		if (args.length == 0) {
			System.err.println("Usage: jyang [-h] [-o output_file] [-p paths] "
					+ "[-f output_format] + filename");
			System.exit(-1);
		}
		int i = 0, j;
		char flag;

		String pathstr = new String();
		String format = null;
		while (i < args.length && args[i].startsWith("-")) {
			String arg = args[i++];

			// use this type of check for arguments that require arguments
			if (arg.equals("-o") || arg.equals("--output")) {
				if (i < args.length)
					outputfilename = args[i++];
				else
					System.err
							.println("-o or --output options requires a filename");
			} else if (arg.equals("-p") || arg.equals("--path")) {
				if (i < args.length) {
					pathstr = args[i++];
				} else
					System.err
							.println("-p or --path option requires pathname(s)");
			} else if (arg.equals("-f") || arg.equals("--format")) {
				if (i < args.length) {
					format = args[i++];
				} else
					System.err
							.println("-f or --format option require name format as yin");
			}

			// use this type of check for a series of flag arguments
			else {
				for (j = 1; j < arg.length(); j++) {
					flag = arg.charAt(j);
					switch (flag) {
					case 'h':
						System.out
								.println("Usage : jyang [-h] [-w] [-o output_file] [-p paths] "
										+ "[-f output_format]  filename");
						System.exit(0);
						break;
					case 'w':
						warning = true;
						break;
					default:
						System.err.println("Illegal option " + flag);
						System.exit(-1);
						break;
					}
				}
			}
		}
		if (i == args.length) {
			System.err.println("Usage: jyang [-h] [-o output_file] [-p paths] "
					+ "[-f output_format] + filename");
			System.exit(-1);
		}

		// Input yang specifications
		// Vector<InputStream> specs = new Vector<InputStream>();
		Hashtable<String, InputStream> specs = new Hashtable<String, InputStream>();
		for (int f = i; f < args.length; f++) {
			try {
				FileInputStream specfile = new FileInputStream(args[f]);
				specs.put(args[f], specfile);
			} catch (FileNotFoundException fnf) {
				System.err.println(args[f] + " file not found, ignore it");
			} catch (SecurityException se) {
				System.err.println(args[f] + " file security access because :");
				System.err.println(se.getMessage());
			}
		}
		if (specs.size() == 0) {
			System.err.println("No yang specification file found");
			System.exit(-1);
		}

		// Output file
		PrintStream out = null;
		if (outputfilename != null)
			try {
				out = new PrintStream(outputfilename);
			} catch (FileNotFoundException fnf) {
				System.err.println(fnf.getMessage());
				System.exit(-3);
			} catch (SecurityException se) {
				System.err.println(se.getMessage());
				System.exit(-2);
			}
		else
			out = System.out;

		// Yang files paths
		String envyangpath = null;
		try {
			envyangpath = System.getenv("YANGPATH");
		} catch (NullPointerException np) {
			// Nothing to do, it is legal to not
			// have a YANGPATH environnement variable
		} catch (SecurityException se) {
			System.err
					.println("YANGPATH environment variable ignored because :");
			System.err.println(se.getMessage());
		}

		if (envyangpath != null) {
			if (envyangpath.charAt(envyangpath.length() - 1) != File.pathSeparatorChar)
				pathstr = envyangpath + File.pathSeparator + pathstr;
			else
				pathstr = envyangpath + pathstr;
		}

		int numpaths = 0;
		byte[] bpath = pathstr.getBytes();
		if (bpath.length != 0)
			numpaths++;
		for (int ib = 0; ib < bpath.length; ib++)
			if (bpath[ib] == File.pathSeparatorChar)
				numpaths++;
		String[] paths = new String[numpaths];
		int ip = 0;
		for (int pos = 0; pos < pathstr.length();) {
			int iOnePath = pathstr.indexOf(File.pathSeparator, pos);
			if (iOnePath != -1) {
				paths[ip++] = pathstr.substring(pos, iOnePath);
				pos = iOnePath + 1;
			} else {
				paths[ip++] = pathstr.substring(pos, pathstr.length());
				pos = pathstr.length();
			}
		}
		Hashtable<String, String> pathsHT = new Hashtable<String, String>();
		for (int pos = 0; pos < paths.length; pos++) {
			try {
				File onepath = new File(paths[pos]);
				if (!onepath.canRead())
					System.err.println("The path : " + paths[pos]
							+ " cannot be read, ignore it.");
				else if (paths[pos].compareTo(".") != 0) // We will force the
					// . at the end
					// Force it if it is not given in YANGPAT
					pathsHT.put(paths[pos], paths[pos]);
			} catch (NullPointerException np) {
				System.err.println("Null path name, maybe a trailing \""
						+ File.pathSeparator + "\", ignore it");
			}
		}

		paths = new String[pathsHT.size() + 1];
		int pos = 0;
		for (Enumeration<String> ep = pathsHT.elements(); ep.hasMoreElements();)
			paths[pos++] = ep.nextElement();
		try {
			File currentdir = new File(".");
			if (!currentdir.canRead()) {
				System.err
						.println("The current directory cannot be read, try to ignore it");
			} else
				paths[pos] = ".";
		} catch (NullPointerException npe) {
			// Nothing to do, it could not happen
		}

		// Parse yang specs

		YangErrorManager.init();
		boolean noError = true;
		for (Enumeration<String> ei = specs.keys(); ei.hasMoreElements();) {
			String fname = ei.nextElement();
			YangErrorManager.setCurrentModule(fname);
			if (!reinit) {
				reinit = true;
				new yang(specs.get(fname));
			} else {
				yang.ReInit(specs.get(fname));
			}
			try {
				yang.setFileName(fname);
				YANG_Specification yangspec = yang.Start();
				yangspec.check(paths);
				if (YANG_Specification.isCheckOk()) {
					yangsSpecs.put(yangspec.getName(), yangspec);

					if (format != null) {
						if (format.compareTo("yin") == 0) {
							new Yang2Yin(yangspec, paths, out);
						} else if (format.compareTo("netconf") == 0) {
							new Yang2NetconfHello(yangspec, paths, out);
						} else
							System.err.println("only yin, netconf"
									+ "  formats available");
					}
				} else
					noError = false;

			} catch (ParseException pe) {
				// pe.printStackTrace();
				// System.out.println(pe);
				if (pe.currentToken != null)
					if (pe.currentToken.next != null)
						YangErrorManager.addError(fname,
								pe.currentToken.next.beginLine,
								pe.currentToken.next.beginColumn, "unex_kw",
								pe.currentToken.next.image);
					else
						System.out.println(pe);
				else
					System.out.println(pe);
			}
		}
		try {
			if (!warning)
				YangErrorManager.supressWarning();
			if (output)
				YangErrorManager.print(System.err);
		} catch (IOException e) {
			e.printStackTrace();
		}
		parsingOk = noError;
	}

	public boolean isParsingOk() {
		return parsingOk;
	}

	public Hashtable<String, YANG_Specification> getYangsSpecs() {
		return yangsSpecs;
	}

	public String getOutputFileName() {
		return outputfilename;
	}
}