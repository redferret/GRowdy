
package growdy;


import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import growdy.exceptions.AmbiguousGrammarException;
import static growdy.GRConstants.*;


/**
 * This class builds and defines the grammar for your language and will be used as a 
 * resource file for compiling or running your source files written in your
 * programming language.
 * @author Richard DeSilvey
 */
public class Grammar implements Serializable {

  private final List<ProductionRule> productionRules;
  private final List<NonTerminal> nonterminals;
  private final List<String> termSymbols;
  private final List<String> termNames;
  private final List<String> nontermNames;
  private String specialSym;
  private String grammarName;
  private final int rootNodeId = 1000;
  
  private transient String javaSource;
  private transient final Language grLang = Language.build(GRConstants.grammarRules, 
          GRConstants.terms, GRConstants.nonterminals);
  private transient final RowdyLexer lexer = new RowdyLexer(GRConstants.terms, GRConstants.specialSym);
  private transient final RowdyBuilder builder = RowdyBuilder.getBuilder(grLang, (Symbol symbol, int line) -> {
      return new Node(symbol, line) {
        @Override
        public Node copy() {
          return null;
        }

        @Override
        public Object execute(Object leftValue) {
          throw new UnsupportedOperationException("Can't execute GR Nodes"); 
        }
      };
    });
  
  private Grammar(String grammarSourceFile) throws IOException, FileNotFoundException, ParseException, SyntaxException, AmbiguousGrammarException {
    lexer.parseSource(grammarSourceFile);
    builder.buildAs(lexer, GR);
    
    productionRules = new ArrayList<>();
    nonterminals = new ArrayList<>();
    nontermNames = new ArrayList<>();
    termSymbols = new ArrayList<>();
    termNames = new ArrayList<>();
    
    build();
    generateJavaSource();
  }
  
  private void generateJavaSource() {
    StringBuilder source = new StringBuilder();
    source.append("public class ").append(grammarName);
    source.append("GrammarConstants {\n");
    source.append("\tpublic static final int \n\t\t");
    int id = 0;
    for (String termName : termNames) {
      source.append(termName.toUpperCase()).append(" = ").append(id++).append(",\n\t\t");
    }
    for (int i = 0; i < nontermNames.size()-1; i++) {
      String nontermName = nontermNames.get(i);
      source.append(nontermName.toUpperCase()).append(" = ").append((1000 + i)).append(",\n\t\t");
    }
    int l = nontermNames.size()-1;
    source.append(nontermNames.get(l)).append(" = ").append((1000 + l)).append(";\n");
    source.append("}");
    javaSource = source.toString();
  }
  
  private void build() {
    Node termBody = builder.getProgram().get(GRAMMAR).get(TERMINAL_BODY);
    Node nonTermBody = builder.getProgram().get(GRAMMAR).get(GRAMMAR_BODY);
    Node specialSyms = termBody.get(SPECIAL_DEF);
    specialSym = ((Terminal)specialSyms.get(CONST).symbol()).getValue();
    Node grammarId = builder.getProgram().get(ID);
    grammarName = ((Terminal)grammarId.symbol()).getValue();
    
    collectTerminalDefs(termBody.get(TERMINAL_DEFS));
    collectNonTerminals(nonTermBody.get(NONTERMINAL_DEFS));
    buildProductionRules(nonTermBody.get(NONTERMINAL_DEFS));
  }
  
  private void collectTerminalDefs(Node termDefs){
    while (termDefs.hasSymbols()) {
      Node termDef = termDefs.get(TERMINAL_DEF);
      Node termId = termDef.get(ID);
      String idName = ((Terminal)termId.symbol()).getValue();
      Terminal atomic = (Terminal)termDef.get(ATOMIC).getLeftMost(true).symbol();
      switch (atomic.id()) {
        case IDENT:
          termSymbols.add(RowdyLexer.IDENTIFIER, atomic.getValue());
          termNames.add(RowdyLexer.IDENTIFIER, idName);
          break;
        case CONSTANT:
          termSymbols.add(RowdyLexer.CONSTANT, atomic.getValue());
          termNames.add(RowdyLexer.CONSTANT, idName);
          break;
        case CONST:
          termSymbols.add(atomic.getValue().replaceAll("\"", ""));
          termNames.add(idName.replaceAll("\"", ""));
          break;
      }
      termDefs = termDefs.get(TERMINAL_DEFS);
    }
  }
  
  private void collectNonTerminals(Node nonTermDefs) {
    int nonTermIdStart = 1000;
    while (nonTermDefs.hasSymbols()) {
      Node nonTermDef = nonTermDefs.get(NONTERMINAL_DEF);
      Node nonTermId = nonTermDef.get(ID);
      String nonTerminalName = ((Terminal)nonTermId.symbol()).getValue();
      nontermNames.add(nonTerminalName);
      String nonTerminalRep = nonTerminalName.toLowerCase().replaceAll("_", "-");
      List<int[]> hints = new ArrayList<>();
      
      Node params = nonTermDef.get(NONTERMINAL_PARAMS);
      Node terminals = params.get(TERMINAL_PARAMS);
      while(terminals.hasSymbols()){
        String termName = ((Terminal)terminals.get(ID).symbol()).getValue();
        int termid = termNames.indexOf(termName);
        hints.add(new int[]{termid, nonTermIdStart});
        terminals = terminals.get(TERMINAL_PARAMS);
      }
      int[][] hintTable = hints.toArray(new int[hints.size()][2]);
      NonTerminal nonTerminal = new NonTerminal(nonTerminalRep, nonTermIdStart, hintTable);
      nonterminals.add(nonTerminal);
      nonTermIdStart++;
      nonTermDefs = nonTermDefs.get(NONTERMINAL_DEFS);
    }
  }
  
  private void buildProductionRules(Node nonTermDefs) {
    int pruleStart = 1000;
    while (nonTermDefs.hasSymbols()) {
      int groupId = 0;
      Node nonTermDef = nonTermDefs.get(NONTERMINAL_DEF);
      Node terminal = nonTermDef.get(ID, 1);
      Node starOpt = nonTermDef.get(STAR_OPT);
      String name = ((Terminal)terminal.symbol()).getValue();
      int id = termNames.indexOf(name);
      Rule rule;
      if (id < 0) {
        id = nontermNames.indexOf(name);
        if (id < 0) {
          throw new RuntimeException("Unknown symbol " + name);
        }
        id += 1000;
        rule = new Rule(id);
        if (starOpt.hasSymbols()){
          rule.markAsTrimmable();
        }
      } else {
        rule = new Rule(id);
      }
      List<Rule> productionSymbols = new ArrayList<>();
      productionSymbols.add(rule);
      
      Node idList = nonTermDef.get(ID_LIST);
      while(idList.hasSymbols()) {
        addSymbolId(idList, groupId, productionSymbols);
        idList = idList.get(ID_LIST);
      }
     
      Node orOpt = nonTermDef.get(OR_OPTION);
      while (orOpt.hasSymbols()) {
        groupId++;
        addSymbolId(orOpt, groupId, productionSymbols);
        
        idList = orOpt.get(ID_LIST);
        while(idList.hasSymbols()) {
          addSymbolId(idList, groupId, productionSymbols);
          idList = idList.get(ID_LIST);
        }
        
        orOpt = orOpt.get(OR_OPTION);
      }

      createNewProductionRule(pruleStart, productionSymbols);
      pruleStart++;
      
      nonTermDefs = nonTermDefs.get(NONTERMINAL_DEFS);
    }
  }

  private void addSymbolId(Node idList, int groupId, List<Rule> productionSymbols) {
    String name;
    int id;
    Rule rule;
    Node termListItem = idList.get(ID);
    Node starOpt = idList.get(STAR_OPT);
    name = ((Terminal) termListItem.symbol()).getValue();
    id = termNames.indexOf(name);
    if (id < 0) {
      id = nontermNames.indexOf(name);
      if (id < 0) {
        throw new RuntimeException("Unknown symbol " + name);
      }
      id += 1000;
      rule = new Rule(id);
      if (starOpt.hasSymbols()){
        rule.markAsTrimmable();
      }
    } else {
      rule = new Rule(id);
    }
    productionSymbols.add(rule);
  }
  
  private void createNewProductionRule(int pruleId, List<Rule> productionSymbols) {
    Rule[] productionSymbolIds = new Rule[productionSymbols.size()];
      for (int i = 0; i < productionSymbolIds.length; i++){
        productionSymbolIds[i] = productionSymbols.get(i);
      }
      ProductionRule prule = new ProductionRule(pruleId, productionSymbolIds);
      productionRules.add(prule);
  }
  
  /**
   * There's no need to directly call this, MainGR will grab a new instance 
   * of this class and serialize it once the grammar for your language is 
   * constructed.
   * @param grammarSourceFile
   * @return
   * @throws IOException
   * @throws FileNotFoundException
   * @throws ParseException
   * @throws SyntaxException 
   * @throws growdy.exceptions.AmbiguousGrammarException 
   */
  public static Grammar buildLanguage(String grammarSourceFile) throws 
          IOException, FileNotFoundException, ParseException, SyntaxException, AmbiguousGrammarException {
    return new Grammar(grammarSourceFile);
  }
  
  public String getJavaSourceCode(String sourcePackage) {
    StringBuilder source = new StringBuilder();
    if (sourcePackage.isEmpty()){
      source.append("package ").append("lang;\n");
    } else {
      source.append("package ").append(sourcePackage).append(".lang;\n");
    }
    source.append(javaSource);
    return source.toString();
  }
  
  public int getRootTerminalId() {
    return rootNodeId;
  }
  
  public String getGrammarName() {
    return grammarName;
  }
  
  public ProductionRule[] getGrammarRules() {
    return productionRules.toArray(new ProductionRule[productionRules.size()]);
  }

  public NonTerminal[] getNonterminals() {
    return nonterminals.toArray(new NonTerminal[nonterminals.size()]);
  }
  public String[] getTerms() {
    return termSymbols.toArray(new String[termSymbols.size()]);
  }

  public String getSpecialSym() {
    return specialSym;
  }

}
