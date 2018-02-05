
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
  public static final String[] terms = {"ID", "literal", "<", ">", "(", ")", "|", 
    "BEGIN", "TERMINAL", "SPECIAL", "=>", ";", "id", "constant"};
  
  public static final int ID = 0, CONST = 1, LEFTCARET = 2, RIGHTCARET = 3,
          LEFTPAREN = 4, RIGHTPAREN = 5, OR = 6, BEGIN = 7, TERMINAL = 8, 
          SPECIAL = 9, IS = 10, SEMICOLON = 11, IDENT = 12, CONSTANT = 13;
  
  public static final int GR = 100, GRAMMAR = 101, TERMINAL_BODY = 102,
          SPECIAL_DEF = 103, TERMINAL_DEFS = 104, TERMINAL_DEF = 105, 
          ATOMIC = 106, GRAMMAR_BODY = 107,NONTERMINAL_DEFS = 108,OR_OPT = 109,
          NONTERMINAL_DEF = 110,NONTERMINAL_PARAMS = 111,TERMINAL_PARAMS = 112,
          ID_LIST = 113;
          
  public static final int PRule_GR = 0,PRule_GRAMMAR = 1,
          PRule_TERMINAL_BODY = 2,PRule_SPECIAL_DEF = 3, 
          PRule_TERMINAL_DEFS = 4, PRule_TERMINAL_DEF = 5, 
          PRule_ATOMIC = 6, PRule_GRAMMAR_BODY = 7,
          PRule_NONTERMINAL_DEFS = 8,PRule_OR_OPT = 9,
          PRule_NONTERMINAL_DEF = 10,PRule_NONTERMINAL_PARAMS = 11,
          PRule_TERMINAL_PARAMS = 12, PRule_IDENT = 13, PRule_CONSTANT = 14,
          PRule_CONST = 15, PRule_ID_LIST = 16, PRule_END = 100;
  
  public static final NonTerminal[] nonterminals = {
    new NonTerminal("prog", GR, 
            new int[][]{{ID, PRule_GR}}),
    new NonTerminal("grammar", GRAMMAR, 
            new int[][]{{LEFTCARET, PRule_GRAMMAR}}),
    new NonTerminal("term-body", TERMINAL_BODY, 
            new int[][]{{LEFTCARET, PRule_TERMINAL_BODY}}),
    new NonTerminal("special", SPECIAL_DEF, 
            new int[][]{{LEFTCARET, PRule_SPECIAL_DEF}}),
    new NonTerminal("term-defs", TERMINAL_DEFS, 
            new int[][]{{ID, PRule_TERMINAL_DEFS}}),
    new NonTerminal("term-def", TERMINAL_DEF, 
            new int[][]{{ID, PRule_TERMINAL_DEF}}),
    new NonTerminal("atomic", ATOMIC, 
            new int[][]{{IDENT, PRule_IDENT}, {CONSTANT, PRule_CONSTANT},
                        {CONST, PRule_CONST}}),
    new NonTerminal("grammar-body", GRAMMAR_BODY, 
            new int[][]{{LEFTCARET, PRule_GRAMMAR_BODY}}),
    new NonTerminal("nonterm-defs", NONTERMINAL_DEFS, 
            new int[][]{{ID, PRule_NONTERMINAL_DEFS}}),
    new NonTerminal("or-opt", OR_OPT, 
            new int[][]{{OR, PRule_OR_OPT}}),
    new NonTerminal("id-list", ID_LIST, 
            new int[][]{{ID, PRule_ID_LIST}}),
    new NonTerminal("nonterm-def", NONTERMINAL_DEF, 
            new int[][]{{ID, PRule_NONTERMINAL_DEF}}),
    new NonTerminal("nonterm-params", NONTERMINAL_PARAMS, 
            new int[][]{{LEFTPAREN, PRule_NONTERMINAL_PARAMS}}),
    new NonTerminal("term-params", TERMINAL_PARAMS, 
            new int[][]{{ID, PRule_TERMINAL_PARAMS}}),
  };
  
  
  public static final ProductionRule[] grammarRules = {
    new ProductionRule(PRule_GR, 
            new int[]{ID, GRAMMAR}),
    new ProductionRule(PRule_GRAMMAR, 
            new int[]{TERMINAL_BODY, GRAMMAR_BODY}),
    
    
    new ProductionRule(PRule_TERMINAL_BODY, 
            new int[]{LEFTCARET, TERMINAL, RIGHTCARET, TERMINAL_DEFS, SPECIAL_DEF}),
    new ProductionRule(PRule_SPECIAL_DEF, 
            new int[]{LEFTCARET, SPECIAL, RIGHTCARET, IS, CONST, SEMICOLON}),
    new ProductionRule(PRule_TERMINAL_DEFS, 
            new int[]{TERMINAL_DEF, SEMICOLON, TERMINAL_DEFS,}),
    new ProductionRule(PRule_TERMINAL_DEF, 
            new int[]{ID, IS, ATOMIC}),
    
    new ProductionRule(PRule_IDENT, 
            new int[]{IDENT}),
    new ProductionRule(PRule_CONSTANT, 
            new int[]{CONSTANT}),
    new ProductionRule(PRule_CONST, 
            new int[]{CONST}),
    
    
    new ProductionRule(PRule_GRAMMAR_BODY, 
            new int[]{LEFTCARET, BEGIN, RIGHTCARET, NONTERMINAL_DEFS}),
    new ProductionRule(PRule_NONTERMINAL_DEFS, 
            new int[]{NONTERMINAL_DEF, SEMICOLON, NONTERMINAL_DEFS}),
    new ProductionRule(PRule_OR_OPT, 
            new int[]{OR, ID_LIST, OR_OPT}),
    new ProductionRule(PRule_NONTERMINAL_DEF, 
            new int[]{ID, NONTERMINAL_PARAMS, IS, ID_LIST, OR_OPT}),
    new ProductionRule(PRule_ID_LIST, 
            new int[]{ID, ID_LIST}),
    
    new ProductionRule(PRule_NONTERMINAL_PARAMS, 
            new int[]{LEFTPAREN, TERMINAL_PARAMS, RIGHTPAREN}),
    new ProductionRule(PRule_TERMINAL_PARAMS, 
            new int[]{ID, TERMINAL_PARAMS}),
    
    new ProductionRule(PRule_END, 
            new int[]{})
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
      builder.buildAs(parser, GR);
      Node program = builder.getProgram();
    } catch (IOException | ParseException | SyntaxException ex) {
      Logger.getLogger(GRowdy.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
}
