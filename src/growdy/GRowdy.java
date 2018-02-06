
package growdy;

import java.io.IOException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * GRowdy is an object that will take a grammar resource file built using
 * the GRBuilder. The grammar along with a Java source file containing all
 * the IDs for terminals and non-terminals are built and created under
 * lang/ in the same location as the JAR library
 * @author Richard DeSilvey
 */
public class GRowdy {

  private final Language language;
  private final RowdyLexer parser;
  private final RowdyBuilder builder;
  private GRBuilder grObject;
  
  private GRowdy(String grammarFileName) {
    // Get the grammar as a resource (Deserialize) and use it to fill in
    // everything needed for a Language and a Lexer
    
    try {
      try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("lang\\"+grammarFileName+".gr"); 
              ObjectInputStream in = new ObjectInputStream(inputStream)) {
        grObject = (GRBuilder) in.readObject();
      }
      } catch (IOException | ClassNotFoundException i) {
        throw new RuntimeException("There was a problem loading the Grammar: " + i.getLocalizedMessage());
      }
    
    ProductionRule[] grammarRules = grObject.getGrammarRules();
    NonTerminal[] nonterminals = grObject.getNonterminals();
    String[] terms = grObject.getTerms();
    String specialSym = grObject.getSpecialSym();
    int identId = grObject.getIdentId();
    int constId = grObject.getConstId();
    
    language = Language.build(grammarRules, terms, nonterminals);
    parser = new RowdyLexer(terms, specialSym, identId, constId);
    builder = RowdyBuilder.getBuilder(language);
  }
  
  /**
   * Get an instance of your language by passing in the name of the grammar
   * resource file. This file should be packaged inside your interpreters 
   * JAR file.
   * @param grammarFileName The grammar resource file name
   * @return An instance of your language builder
   */
  public static GRowdy getInstance(String grammarFileName) {
    return new GRowdy(grammarFileName);
  }
  
  /**
   * The root node of the program parse tree after a successful build of your
   * source code.
   * @return The parse tree root node
   */
  public Node getProgram() {
    return builder.getProgram();
  }
  
  /**
   * Execute your source files with this method. This will parse the source
   * file given. Multiple source files need to be built and their parse tree
   * returned.
   * @param sourceFile The relative file path with the source file name
   * @throws IOException
   * @throws FileNotFoundException
   * @throws ParseException
   * @throws SyntaxException 
   */
  public void buildFromSource(String sourceFile) throws IOException, FileNotFoundException, ParseException, SyntaxException {
    parser.parseSource(sourceFile);
    builder.buildAs(parser, grObject.getRootTerminalId());
  }
  
  /**
   * If running a language through a command prompt you can execute pieces of
   * your parse tree based on the grammar of your language.
   * @param sourceCode The code to execute
   * @param programNode The non-terminal id to build, ie. STMT_LIST
   * @throws ParseException
   * @throws SyntaxException 
   */
  public void buildFromString(String sourceCode, int programNode) throws ParseException, SyntaxException {
    parser.parseLine(sourceCode);
    builder.buildAs(parser, programNode);
  }
  
}
