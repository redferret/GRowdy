
package growdy.testUtils;

import rowdy.RowdyBuilder;
import rowdy.RowdyLexer;
import rowdy.Terminal;
import rowdy.Language;
import rowdy.Node;
import rowdy.exceptions.ParseException;
import rowdy.exceptions.SyntaxException;
import java.io.IOException;
import static org.junit.Assert.*;
import static growdy.GRowdy.*;

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
          RowdyBuilder.getBuilder(growdy);
  
  public static Node getTestProgram(String testProgram) {
    try {
      parser.parse(testProgram);
      try {
        builder.buildAs(parser, GR);
      } catch (SyntaxException e) {
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
