
package growdy;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rowdy.Language;
import rowdy.Node;
import rowdy.NonTerminal;
import rowdy.ProductionRule;
import rowdy.RowdyBuilder;
import rowdy.RowdyLexer;
import rowdy.exceptions.ParseException;
import rowdy.exceptions.SyntaxException;

/**
 *
 * @author Richard
 */
public class GRowdy {

  public static final String specialSym = "< > | => ( ) ;";
  public static final String[] terms = {"ID", "CONST", "<", ">", "(", ")", "|", 
    "BEGIN", "TERMINAL", "SPECIAL", "=>", ";"};
  
  public static final int ID = 0, CONST = 1, LEFTCARET = 2, RIGHTCARET = 3,
          LEFTPAREN = 4, RIGHTPAREN = 5, OR = 6, BEGIN = 7, TERMINAL = 8, 
          SPECIAL = 9, IS = 10, SEMICOLON = 11;
  
  public static final int GR = 100, GRAMMAR = 101, TERMINAL_BODY = 102,
          SPECIAL_DEF = 103, TERMINAL_DEFS = 104, TERMINAL_DEF = 105, 
          ATOMIC = 106, GRAMMAR_BODY = 107,NONTERMINAL_DEFS = 108,OR_OPT = 109,
          NONTERMINAL_DEF = 110,NONTERMINAL_PARAMS = 111,TERMINAL_PARAMS = 112;
          
  public static final int PRule_GR = 0,PRule_GRAMMAR = 1,
          PRule_TERMINAL_BODY = 2,PRule_SPECIAL_DEF = 3, 
          PRule_TERMINAL_DEFS = 4, PRule_TERMINAL_DEF = 5, 
          PRule_ATOMIC = 6, PRule_GRAMMAR_BODY = 7,
          PRule_NONTERMINAL_DEFS = 8,PRule_OR_OPT = 9,
          PRule_NONTERMINAL_DEF = 10,PRule_NONTERMINAL_PARAMS = 11,
          PRule_TERMINAL_PARAMS = 12;
  
  public static final NonTerminal[] nonterminals = {
    
  };
  
  
  public static final ProductionRule[] grammarRules = {
  };
  
  private static final Language growdyLang = Language.build(grammarRules, terms, nonterminals);
  private static final RowdyLexer parser = new RowdyLexer(terms, specialSym, 0, 1);
  private static final RowdyBuilder builder = RowdyBuilder.getBuilder(growdyLang);
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      parser.parse("example");
      builder.build(parser);
      Node program = builder.getProgram();
    } catch (IOException | ParseException | SyntaxException ex) {
      Logger.getLogger(GRowdy.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
}
