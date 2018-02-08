
package growdy;

/**
 *
 * @author Richard
 */
public class GRConstants {
  public static final String specialSym = "< > | => ( ) ; *";
  public static final String[] terms = {"ID", "literal", "<", ">", "(", ")", "|", 
    "BEGIN", "TERMINAL", "SPECIAL", "=>", ";", "id", "constant", "*"};
  
  public static final int ID = 0, CONST = 1, LEFTCARET = 2, RIGHTCARET = 3,
          LEFTPAREN = 4, RIGHTPAREN = 5, OR = 6, BEGIN = 7, TERMINAL = 8, 
          SPECIAL = 9, IS = 10, SEMICOLON = 11, IDENT = 12, CONSTANT = 13,
          STAR = 14;
  
  public static final int GR = 100, GRAMMAR = 101, TERMINAL_BODY = 102,
          SPECIAL_DEF = 103, TERMINAL_DEFS = 104, TERMINAL_DEF = 105, 
          ATOMIC = 106, GRAMMAR_BODY = 107,NONTERMINAL_DEFS = 108,OR_OPTION = 109,
          NONTERMINAL_DEF = 110,NONTERMINAL_PARAMS = 111,TERMINAL_PARAMS = 112,
          ID_LIST = 113, STAR_OPT = 114;
          
  public static final int PRule_GR = 0,PRule_GRAMMAR = 1,
          PRule_TERMINAL_BODY = 2,PRule_SPECIAL_DEF = 3, 
          PRule_TERMINAL_DEFS = 4, PRule_TERMINAL_DEF = 5, 
          PRule_ATOMIC = 6, PRule_GRAMMAR_BODY = 7,
          PRule_NONTERMINAL_DEFS = 8,PRule_OR_OPT = 9,
          PRule_NONTERMINAL_DEF = 10,PRule_NONTERMINAL_PARAMS = 11,
          PRule_TERMINAL_PARAMS = 12, PRule_IDENT = 13, PRule_CONSTANT = 14,
          PRule_CONST = 15, PRule_ID_LIST = 16, PRule_STAR_OPT = 17, 
          PRule_END = 100;
  
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
    new NonTerminal("or-opt", OR_OPTION, 
            new int[][]{{OR, PRule_OR_OPT}}),
    new NonTerminal("id-list", ID_LIST, 
            new int[][]{{ID, PRule_ID_LIST}}),
    new NonTerminal("nonterm-def", NONTERMINAL_DEF, 
            new int[][]{{ID, PRule_NONTERMINAL_DEF}}),
    new NonTerminal("nonterm-params", NONTERMINAL_PARAMS, 
            new int[][]{{LEFTPAREN, PRule_NONTERMINAL_PARAMS}}),
    new NonTerminal("term-params", TERMINAL_PARAMS, 
            new int[][]{{ID, PRule_TERMINAL_PARAMS}}),
    new NonTerminal("star-opt", STAR_OPT,
            new int[][]{{STAR, PRule_STAR_OPT}}),
  };
  
  
  public static final ProductionRule[] grammarRules = {
    new ProductionRule(PRule_GR, 
            new Rule[]{new Rule(ID), new Rule(GRAMMAR)}),
    new ProductionRule(PRule_GRAMMAR, 
            new Rule[]{new Rule(TERMINAL_BODY), new Rule(GRAMMAR_BODY)}),
    
    
    new ProductionRule(PRule_TERMINAL_BODY, 
            new Rule[]{new Rule(LEFTCARET), new Rule(TERMINAL), 
              new Rule(RIGHTCARET), new Rule(TERMINAL_DEFS), new Rule(SPECIAL_DEF)}),
    new ProductionRule(PRule_SPECIAL_DEF, 
            new Rule[]{new Rule(LEFTCARET), new Rule(SPECIAL), new Rule(RIGHTCARET), new Rule(IS), new Rule(CONST), new Rule(SEMICOLON)}),
    new ProductionRule(PRule_TERMINAL_DEFS, 
            new Rule[]{new Rule(TERMINAL_DEF), new Rule(SEMICOLON), new Rule(TERMINAL_DEFS)}),
    new ProductionRule(PRule_TERMINAL_DEF, 
            new Rule[]{new Rule(ID), new Rule(IS), new Rule(ATOMIC)}),
    
    new ProductionRule(PRule_IDENT, 
            new Rule[]{new Rule(IDENT)}),
    new ProductionRule(PRule_CONSTANT, 
            new Rule[]{new Rule(CONSTANT)}),
    new ProductionRule(PRule_CONST, 
            new Rule[]{new Rule(CONST)}),
    
    
    new ProductionRule(PRule_GRAMMAR_BODY, 
            new Rule[]{new Rule(LEFTCARET), new Rule(BEGIN), new Rule(RIGHTCARET), new Rule(NONTERMINAL_DEFS)}),
    new ProductionRule(PRule_NONTERMINAL_DEFS, 
            new Rule[]{new Rule(NONTERMINAL_DEF), new Rule(SEMICOLON), new Rule(NONTERMINAL_DEFS)}),
    new ProductionRule(PRule_OR_OPT, 
            new Rule[]{new Rule(OR), new Rule(ID), new Rule(STAR_OPT), new Rule(ID_LIST), new Rule(OR_OPTION)}),
    new ProductionRule(PRule_NONTERMINAL_DEF, 
            new Rule[]{new Rule(ID), new Rule(NONTERMINAL_PARAMS), new Rule(IS), new Rule(ID), new Rule(STAR_OPT), new Rule(ID_LIST), new Rule(OR_OPTION)}),
    new ProductionRule(PRule_ID_LIST, 
            new Rule[]{new Rule(ID), new Rule(STAR_OPT), new Rule(ID_LIST)}),
    
    new ProductionRule(PRule_NONTERMINAL_PARAMS, 
            new Rule[]{new Rule(LEFTPAREN), new Rule(TERMINAL_PARAMS), new Rule(RIGHTPAREN)}),
    new ProductionRule(PRule_TERMINAL_PARAMS, 
            new Rule[]{new Rule(ID), new Rule(TERMINAL_PARAMS)}),
    
    new ProductionRule(PRule_STAR_OPT,
            new Rule[]{new Rule(STAR)}),
    
    new ProductionRule(PRule_END, 
            new Rule[]{})
  };
}
