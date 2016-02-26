package org.adtran.yang.parser.helpers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YangErrorManager {

	static public class Error {
		public final static int SEVERITY_INFO = 0;
		public final static int SEVERITY_WARNING = 1;
		public final static int SEVERITY_ERROR = 2;

		private String[] tseverity = { "INFO", "WARNING", "ERROR" };

		private String module;
		private int line;
		private int column;
		private String messageId;
		private int severity;

		
		public Error(String m, int l, int c, String mi, int s) {
			if (m.contains(File.separator))
				module = m.substring(m.lastIndexOf(File.separatorChar) + 1);
			else
				module = m;
			line = l;
			column = c;
			messageId = mi;
			severity = s;
		}

		public String toString() {
			return module + "#(" + line + "," + column + ")#"
					+ tseverity[severity] + "#" + messageId;
		}

		public String getModule() {
			return module;
		}

		public int getLine() {
			return line;
		}

		public int getColumn() {
			return column;
		}

		public String getMessageId() {
			return messageId;
		}

		public int getSeverity() {
			return severity;
		}

		public boolean isWarning() {
			return getSeverity() == 1;
		}
	}

	static private class ErrorsComparator implements Comparator {

		// @Override
		public int compare(Object o0, Object o1) {
			Error e0 = (Error) o0, e1 = (Error) o1;
			if (e0.severity != e1.severity)
				return e0.getSeverity() - e1.getSeverity();
			else if (e0.module.compareTo(e1.module) != 0)
				return e0.module.compareTo(e1.module);
			else if (e0.line != e1.line)
				return e0.line - e1.line;
			else if (e0.column != e1.column)
				return e0.column - e1.column;
			return 0;
		}

	}

	static private ErrorsComparator errorComp = new ErrorsComparator();

	static private TreeSet<Error> errors = new TreeSet<Error>(errorComp);

	static public ResourceBundle messages;

	// static protected Properties properties = new Properties(
	// "jyang.parser.resources");

	// @SuppressWarnings("static-access")
	static public ResourceBundle getMessages() {
		return messages;
	}

	// static public Properties getProperties() {
	// return properties;
	// }

	static public void init() {

		messages = null;
		errors = new TreeSet<Error>(errorComp);

		try {
//			YangErrorManager.class.getClassLoader().getResourceAsStream("MessagesBundle");
			messages = ResourceBundle.getBundle("MessagesBundle");
		} catch (MissingResourceException mre) {
			try {
				messages = ResourceBundle
						.getBundle("org.adtran.yang.parser.helpers.MessagesBundle.properties");

			} catch (MissingResourceException mre1) {
				Logger
						.getLogger("")
						.logp(
								Level.SEVERE,
								"Yang Error Manager",
								"static initialization",
								"Can not read  MessagesBundle (messages properties files)",
								mre1);
			}
		}
	}

	static private String module;

	static public void setCurrentModule(String m) {
		module = m;
		if (module.contains(File.separator))
			module = m.substring(m.lastIndexOf(File.separatorChar) + 1);
		if (module.endsWith(".yang"))
			module = module.substring(0,module.lastIndexOf(".yang"));
	}

	static public void addInfo(String module, int line, int col, String mess,
			Object... parameters) {
		_add(module, line, col, MessageFormat.format(YangErrorManager.messages
				.getString(mess), parameters), Error.SEVERITY_INFO);
	}

	static public void addWarning(String module, int line, int col,
			String mess, Object... parameters) {
		_add(module, line, col, MessageFormat.format(YangErrorManager.messages
				.getString(mess), parameters), Error.SEVERITY_WARNING);
	}

	static public void addError(String module, int line, int col, String mess,
			Object... parameters) {
		_add(module, line, col, MessageFormat.format(YangErrorManager.messages
				.getString(mess), parameters), Error.SEVERITY_ERROR);
	}

	static private void _add(String module, int line, int col, String mess,
			int sev) {
		Error error = new Error(module, line, col, mess, sev);
		errors.add(error);
	}

	static public void print(OutputStream out) throws IOException {
		for (Iterator<Error> i = errors.iterator(); i.hasNext();) {
			out.write((i.next().toString() + "\n").getBytes());
		}
	}

	public static TreeSet<Error> getErrors() {
		return errors;
	}

	public static void removeFromLine(int l) {
		for (Iterator<Error> i = errors.iterator(); i.hasNext();) {
			Error err = i.next();
			if (err.getLine() > l) {
				errors.remove(err);
			}
		}
	}

	public static void cleanWarning(String s) {
		if (s.compareTo(module) != 0) {
			TreeSet<Error> nerrors = new TreeSet<Error>(errorComp);
			for (Error e : errors) {
				String em = e.getModule().substring(0, e.getModule().lastIndexOf(".yang"));
				if (s.compareTo(em) != 0)
					nerrors.add(e);
			}
			errors = nerrors;
		}
	}

	public static void supressWarning() {
		TreeSet<Error> nerrors = new TreeSet<Error>(errorComp);
		for (Error e : errors) {
			if (!e.isWarning())
				nerrors.add(e);
		}
		errors = nerrors;
		
	}
}
