/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package growdy;

import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.io.File;
import java.io.FileOutputStream;
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
    if (args.length > 1) {
      resourcePath = args[1];
    }
    try {
      GRBuilder gr = GRBuilder.buildLanguage(fileName);
      String grammarName = gr.getGrammarName();
      System.out.println(grammarName + " has been built successfully!");
      String buildPath = resourcePath + "lang\\" + grammarName + ".gr";
      File grammarResource = new File(buildPath);
      grammarResource.getParentFile().mkdirs();
      if (!grammarResource.exists()) {
        System.out.println("creating directory: " + buildPath);
        grammarResource.createNewFile();
      }
      try (FileOutputStream fileOut = new FileOutputStream(grammarResource);
        ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
        out.writeObject(gr);
      }

    } catch (IOException | ParseException | SyntaxException ex) {
      Logger.getLogger(GRBuilder.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
