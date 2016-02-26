package org.adtran.yang.pojo.main;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.adtran.yang.parser.yang2java.jyang;

/**
 * This entry point for this yang2javapojo converter.
 * @author brampras
 *
 */
public class MainApp {

	public static void main(String[] args) {
		jyang parser = new jyang(args);
		// do convert pojo using the xml transformer
		if(parser.isParsingOk()){
			MainApp app = new MainApp();
			app.generatePOJOs(parser.getOutputFileName());
		}
		
	}
	
	private void generatePOJOs(String outputfilename) {
		try {
			outputfilename = "nmsInventory1.xml";
			ClassLoader classLoader = getClass().getClassLoader();
			File xslFile = new File(classLoader.getResource("yang2javaXSL.xsl").getFile());
			File generatedYinFile = new File("c:\\tmp\\"+outputfilename);
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("yang2javaXSL.xsl");
			System.out.println(is);
			Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(is));
			/*String outFile = generatedYinFile.getName().substring(0, 1).toUpperCase();
			transformer.transform(new StreamSource(generatedYinFile), new StreamResult(new File("D:\\Stuff\\xsltTest\\"+outFile)));
			System.out.println("Completed POJO generation!!");*/
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}


}
