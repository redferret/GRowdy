
package growdy;

import rowdy.Language;
import rowdy.NonTerminal;
import rowdy.ProductionRule;
import rowdy.RowdyBuilder;
import rowdy.RowdyLexer;

/**
 *
 * @author Richard
 */
public class GRowdy {

  public static final String specialSym = "< > | => ( )";
  public static final String[] terms = 
      {"ID", "CONST", "<", ">", "(", ")", "|", "BEGIN", "TERMINAL", "=>"};
  
  public static final int ID = 0, CONST = 1, LEFTCARET = 2, RIGHTCARET = 3,
          LEFTPAREN = 4, RIGHTPAREN = 5, OR = 6, BEGIN = 7, TERMINAL = 8,
          IS = 9;
  
  
  public static final NonTerminal[] nonterminals = {
    
  };
  
  
  public static final ProductionRule[] grammarRules = {
  };
  
  private final Language growdyLang = Language.build(grammarRules, terms, nonterminals);
  private final RowdyLexer parser = new RowdyLexer(terms, specialSym, 0, 1);
  private final RowdyBuilder builder = RowdyBuilder.getBuilder(growdyLang);
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    
  }
  
}
