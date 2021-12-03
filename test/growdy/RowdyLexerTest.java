
package growdy;

import growdy.exceptions.ParseException;
import java.io.IOException;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
/**
 *
 * @author Richard
 */
public class RowdyLexerTest {
   
  private RowdyLexer lexer;
  private final String[] reserved = {"add", "+", "-"};
  private final String operators = "+ -";
  
  public RowdyLexerTest() {
  }
  
  @Before
  public void setUp() throws IOException {
    lexer = new RowdyLexer(reserved, operators);
    try {
      lexer.parseLine("add a + 25 - 1");
    } catch (ParseException ex) {
      fail("Lexer failed to lex");
    }
  }

  @After
  public void tearDown() {
  }

  @Test
  public void tokenCountTest() {
    Integer numberOfTokens = lexer.tokenCount();
    Integer expectedCount = 7;
    assertEquals("The number of Tokens is incorrect", expectedCount, numberOfTokens);
  }

  /**
   * Test of hasToken method, of class RowdyLexer.
   */
  @Test
  public void testHasToken() {
    Boolean hasToken = lexer.hasToken();
    assertTrue("The number of Tokens is incorrect", hasToken);
  }

  /**
   * Test of getToken method, of class RowdyLexer.
   */
  @Test
  public void testGetToken() {
    Token token;
    Integer[] expectedIds = {0, 0, 1, 1, 2, 1, 200};
    for (Integer expectedId : expectedIds){
      token = lexer.getToken();
      Integer tokenId = token.getID();
      assertEquals("Token id mismatch", expectedId, tokenId);
    }

  }

  /**
   * Test of parse method, of class RowdyLexer.
   * @throws java.lang.Exception
   */
  public void testParse() throws Exception {
  }

  /**
   * Test of parseLine method, of class RowdyLexer.
   */
  public void testParseLine() {
  }

  /**
   * Test of parseCode method, of class RowdyLexer.
   */
  public void testParseCode() {
  }

  /**
   * Test of tokenCount method, of class RowdyLexer.
   */
  public void testTokenCount() {
  }
  
}
