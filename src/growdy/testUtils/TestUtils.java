
package growdy.testUtils;

import growdy.RowdyBuilder;
import growdy.RowdyLexer;
import growdy.Terminal;
import growdy.Language;
import growdy.Node;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.io.IOException;
import static org.junit.Assert.*;
import static growdy.GRConstants.*;
import growdy.Symbol;
import growdy.exceptions.AmbiguousGrammarException;

/**
 *
 * @author Richard
 */
public class TestUtils {
  
  public static final RowdyLexer parser = 
          new RowdyLexer(terms, specialSym, ID, CONST);
  public static final Language growdy = 
          Language.build(grammarRules, terms, nonterminals);
  public static final RowdyBuilder builder = 
          RowdyBuilder.getBuilder(growdy, (Symbol symbol, int line) -> {
            return new Node(symbol, line) {
              @Override
              public Object execute(Object leftValue) {
                return null;
              }

              @Override
              public Node copy() {
                throw new UnsupportedOperationException("Can't Execute GR Nodes");
              }
            };
          });
  
  public static Node getTestProgram(String testProgram) {
    try {
      parser.parseSource(testProgram);
      try {
        builder.buildAs(parser, GR);
      } catch (SyntaxException | AmbiguousGrammarException e) {
        fail("Builder failed to compile: " + e.getLocalizedMessage());
      }
      Node root = builder.getProgram();
      assertNotNull("Root Program is Null", root);
      
      return root;
    } catch (ParseException | IOException ex) {
      fail("Builder failed to compile: " + ex.getLocalizedMessage());
      return null;
    }
  }
  
  public static Node getFromAndTestNotNull(Node from, int id) {
    return getFromAndTestNotNull(from, id, 0);
  }
  
  public static Node getFromAndTestNotNull(Node from, int id, int occur) {
    Node toFetch = from.get(id, occur, false);
    assertNotNull("Node "+from.symbol().toString()+" doesn't contain the given id: " + id, toFetch);
    return toFetch;
  }
  
  public static void testContainsSymbols(Node parent, int[] prules){
    Integer actualLength = parent.getAll().size();
    Integer expectedLength = prules.length;
    assertEquals("The expected number of symbols is incorrect", 
            expectedLength, actualLength);
    for (int prule : prules) {
      getFromAndTestNotNull(parent, prule);
    }
  }
  
  public static void testForTerminal(Node terminal, String expected) {
    assertNotNull("Terminal Node is null", terminal);
    String actual = ((Terminal)terminal.symbol()).getName();
    assertEquals("Terminal's name doesn't match",expected, actual);
  }
  
  public static Node getAndTestSymbol(Node from, int nodeId, String expected){
    Node toFetch = getFromAndTestNotNull(from, nodeId);
    String actual = toFetch.symbol().getSymbolAsString();
    assertEquals("The expected Symbol is incorrect " + nodeId, expected, actual);
    return toFetch;
  }
  
}
