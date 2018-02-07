
package growdy;

import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builds the grammar for your language. Produces two files in the folder
 * called 'lang'.
 * First file is a java source file containing all the ids for terminals
 * and nonterminals
 * Second file is a resource file of your grammar. GRowdy will load this
 * file to build your source files for your language.
 * The first argument is the path to your grammar, second argument is
 * the path you wish to have the resource files built to, this is optional
 * but recommended.
 * @author Richard DeSilvey
 */
public class MainGR {
  
  public static void main(String args[]) {
    if (args.length < 1) {
      throw new IllegalArgumentException("Invalid parameters given, must at "
              + "least provide a source file path to your grammar");
    }
    String fileName = args[0];
    String resourcePath = "";
    String sourcePackage = "";
    if (args.length > 1) {
      resourcePath = args[1];
    }
    if (args.length > 2) {
      sourcePackage = args[2];
    }
    try {
      GRBuilder gr = GRBuilder.buildLanguage(fileName);
      String grammarName = gr.getGrammarName();
      System.out.println(grammarName + " has been built successfully!");
      
      String buildPath = resourcePath + "lang\\";
      String grammarPath = buildPath + grammarName + ".gr";
      String javaSourcePath = buildPath + grammarName + "GrammarConstants.java";
      
      File grammarResource = new File(grammarPath);
      grammarResource.getParentFile().mkdirs();
      
      if (!grammarResource.exists()) {
        System.out.println("creating directory: " + grammarPath);
        grammarResource.createNewFile();
      }
      
      try (FileOutputStream fileOut = new FileOutputStream(grammarResource);
        ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
        out.writeObject(gr);
      }
      
      File javaSourceFile = new File(javaSourcePath);
      javaSourceFile.getParentFile().mkdirs();
      
      if (!javaSourceFile.exists()) {
        System.out.println("creating directory: " + javaSourcePath);
        javaSourceFile.createNewFile();
      }
      
      try (FileWriter javaSourceOut = new FileWriter(javaSourceFile)) {
        javaSourceOut.write(gr.getJavaSourceCode(sourcePackage));
      }
    } catch (IOException | ParseException | SyntaxException ex) {
      Logger.getLogger(GRBuilder.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
