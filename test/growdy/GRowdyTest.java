
package growdy;

import org.junit.Test;
import static growdy.testUtils.TestUtils.*;
import static growdy.GRConstants.*;

/**
 *
 * @author Richard
 */
public class GRowdyTest {
  
  private static final Node root = getTestProgram("example");
  
  public GRowdyTest() {
  }
  
  @Test
  public void testPRule_GR() {
    testContainsSymbols(root, new int[]{ID, GRAMMAR});
  }
  
  @Test
  public void testGrammar() {
    Node grammar = getFromAndTestNotNull(root, GRAMMAR);
    testContainsSymbols(grammar, new int[]{TERMINAL_BODY, GRAMMAR_BODY});
    
    Node termBody = getFromAndTestNotNull(grammar, TERMINAL_BODY);
    testContainsSymbols(termBody, new int[]{LEFTCARET, TERMINAL, RIGHTCARET, TERMINAL_DEFS, SPECIAL_DEF});
    
    Node termDefs = getFromAndTestNotNull(termBody, TERMINAL_DEFS);
    testContainsSymbols(termDefs, new int[]{TERMINAL_DEF, SEMICOLON, TERMINAL_DEFS});
    
    Node termDef = getFromAndTestNotNull(termDefs, TERMINAL_DEF);
    testContainsSymbols(termDef, new int[]{ID, IS, ATOMIC});
    
    
    Node grammarBody = getFromAndTestNotNull(grammar, GRAMMAR_BODY);
    testContainsSymbols(grammarBody, new int[]{LEFTCARET, BEGIN, RIGHTCARET, NONTERMINAL_DEFS});
    
    Node nontermDefs = getFromAndTestNotNull(grammarBody, NONTERMINAL_DEFS);
    testContainsSymbols(nontermDefs, new int[]{NONTERMINAL_DEF, SEMICOLON, NONTERMINAL_DEFS});
    
    Node nontermDef = getFromAndTestNotNull(nontermDefs, NONTERMINAL_DEF);
    testContainsSymbols(nontermDef, new int[]{ID, NONTERMINAL_PARAMS, IS, ID_LIST, OR_OPTION});
    
    Node params = getFromAndTestNotNull(nontermDef, NONTERMINAL_PARAMS);
    testContainsSymbols(params, new int[]{LEFTPAREN, TERMINAL_PARAMS, RIGHTPAREN});
    Node idList = getFromAndTestNotNull(nontermDef, ID_LIST);
    testContainsSymbols(idList, new int[]{ID, ID_LIST});
    
    Node termParams = getFromAndTestNotNull(params, TERMINAL_PARAMS);
    testContainsSymbols(termParams, new int[]{ID, TERMINAL_PARAMS});
    
  }
  
}
