
package growdy;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rowdy.Language;
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
