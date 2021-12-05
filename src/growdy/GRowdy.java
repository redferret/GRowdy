
package growdy;

import growdy.exceptions.AmbiguousGrammarException;
import java.io.IOException;
import java.io.FileNotFoundException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;

/**
 * GRowdy is the connection between all the parts of parsing, defining, and
 * building a language. GRowdy can build a Grammar to define and build a
 * grammar. Using an already built grammar it will take a source file and parse
 * it using an instance of RowdyLexer and return a fully built parse tree.
 *
 * @author Richard DeSilvey
 */
public class GRowdy {

  private final Language language;
  private final RowdyLexer parser;
  private final RowdyBuilder builder;
  private final Grammar grObject;
  
  private GRowdy(Grammar grObject, NodeFactory factory) {
    // Get the grammar as a resource (Deserialize) and use it to fill in
    // everything needed for a Language and a Lexer
    this.grObject = grObject;
    ProductionRule[] grammarRules = grObject.getGrammarRules();
    NonTerminal[] nonterminals = grObject.getNonterminals();
    String[] terms = grObject.getTerms();
    String specialSym = grObject.getSpecialSym();
    
    language = Language.build(grammarRules, terms, nonterminals);
    parser = new RowdyLexer(terms, specialSym);
    builder = RowdyBuilder.getBuilder(language, factory);
  }
  
  public static GRowdy getInstance(Grammar grObject, NodeFactory factory) {
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
   * If running a language through a command prompt or directly interpreting you
   * can execute pieces of your parse tree based on the grammar of your
   * language.
   *
   * @param sourceCode The code to execute
   * @param programNode The non-terminal id to build, ie. GrammarConstants.STMT_LIST
   * @throws ParseException
   * @throws SyntaxException
   */
  public void buildFromString(String sourceCode, int programNode) throws ParseException, SyntaxException, AmbiguousGrammarException {
    parser.parseLine(sourceCode);
    builder.buildAs(parser, programNode);
  }
  
}
