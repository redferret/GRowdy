
package growdy;

import growdy.exceptions.AmbiguousGrammarException;
import java.io.IOException;
import java.io.FileNotFoundException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;

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
  private final GRBuilder grObject;
  
  private GRowdy(GRBuilder grObject, NodeFactory factory) {
    // Get the grammar as a resource (Deserialize) and use it to fill in
    // everything needed for a Language and a Lexer
    this.grObject = grObject;
    ProductionRule[] grammarRules = grObject.getGrammarRules();
    NonTerminal[] nonterminals = grObject.getNonterminals();
    String[] terms = grObject.getTerms();
    String specialSym = grObject.getSpecialSym();
    int identId = grObject.getIdentId();
    int constId = grObject.getConstId();
    
    language = Language.build(grammarRules, terms, nonterminals);
    parser = new RowdyLexer(terms, specialSym, identId, constId);
    builder = RowdyBuilder.getBuilder(language, factory);
  }
  
  /**
   * Get an instance of your language by passing in the name of the grammar
   * resource file. This file should be packaged inside your interpreters 
   * JAR file.
   * @param grObject
   * @return An instance of your language builder
   */
  public static GRowdy getInstance(GRBuilder grObject) {
    return getInstance(grObject, (Symbol symbol, int line) -> {
      return new Node(symbol, line);
    });
  }
  
  public static GRowdy getInstance(GRBuilder grObject, NodeFactory factory) {
    return new GRowdy(grObject, factory);
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
  public void buildFromSource(String sourceFile) throws IOException, FileNotFoundException, ParseException, SyntaxException, AmbiguousGrammarException {
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
  public void buildFromString(String sourceCode, int programNode) throws ParseException, SyntaxException, AmbiguousGrammarException {
    parser.parseLine(sourceCode);
    builder.buildAs(parser, programNode);
  }
  
}
