
package growdy;

import growdy.exceptions.AmbiguousGrammarException;
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
 * Builds and defines the grammar for your language. Produces two files in the
 * folder called 'lang'. First file is a java source file containing all the id
 * constants for each terminal and nonterminal for your language. The second
 * file is a resource file of your grammar. GRowdy will load this file to build
 * your source files for your language. The first argument is the path to your
 * grammar, second argument is the path you wish to have the resource files
 * built to. The last argument is the name of the java package that exists for
 * your project.
 *
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
      Grammar gr = Grammar.buildLanguage(fileName);
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
    } catch (IOException | ParseException | SyntaxException | AmbiguousGrammarException ex) {
      Logger.getLogger(Grammar.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
