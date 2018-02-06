
package growdy;


import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static growdy.GRConstants.*;

/**
 * This class builds the grammar for your language and will be used as a 
 * resource file for compiling or running your source files written in your
 * programming language.
 * @author Richard DeSilvey
 */
class GRBuilder implements Serializable {

  private final List<ProductionRule> productionRules;
  private final List<NonTerminal> nonterminals;
  private final List<String> termSymbols;
  private final List<String> termNames;
  private String specialSym;
  private String grammarName;
  private int rootNodeId;
  private final int identId = 0;
  private final int constId = 1;
  
  private transient final Language grLang = Language.build(GRConstants.grammarRules, GRConstants.terms, GRConstants.nonterminals);
  private transient final RowdyLexer lexer = new RowdyLexer(GRConstants.terms, GRConstants.specialSym, 0, 1);
  private transient final RowdyBuilder builder = RowdyBuilder.getBuilder(grLang);
  
  private GRBuilder(String grammarSourceFile) throws IOException, FileNotFoundException, ParseException, SyntaxException {
    lexer.parseSource(grammarSourceFile);
    builder.buildAs(lexer, GR);
    
    productionRules = new ArrayList<>();
    nonterminals = new ArrayList<>();
    termSymbols = new ArrayList<>();
    termNames = new ArrayList<>();
    
    build();
  }
  
  private void build() {
    Node termBody = builder.getProgram().get(GRAMMAR).get(TERMINAL_BODY);
    Node nonTermBody = builder.getProgram().get(GRAMMAR).get(GRAMMAR_BODY);
    Node specialSyms = termBody.get(SPECIAL_DEF);
    specialSym = ((Terminal)specialSyms.get(CONST).symbol()).getName();
    Node grammarId = builder.getProgram().get(ID);
    grammarName = ((Terminal)grammarId.symbol()).getName();
    rootNodeId = builder.getProgram().symbol().id();
    
    collectTerminalDefs(termBody.get(TERMINAL_DEFS));
    collectNonTerminals(nonTermBody.get(NONTERMINAL_DEFS));
    buildProductionRules(nonTermBody.get(NONTERMINAL_DEFS));
  }
  
  private void collectTerminalDefs(Node termDefs){
    while (termDefs.hasSymbols()) {
      Node termDef = termDefs.get(TERMINAL_DEF);
      Node termId = termDef.get(ID);
      String idName = ((Terminal)termId.symbol()).getName();
      Terminal atomic = (Terminal)termDef.get(ATOMIC).getLeftMost().symbol();
      switch (atomic.id()) {
        case IDENT:
          termSymbols.add(identId, atomic.getName());
          termNames.add(identId, idName);
          break;
        case CONSTANT:
          termSymbols.add(constId, atomic.getName());
          termNames.add(constId, idName);
          break;
        case CONST:
          termSymbols.add(atomic.getName());
          termNames.add(idName);
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
      String nonTerminalName = ((Terminal)nonTermId.symbol()).getName();
      String nonTerminalRep = nonTerminalName.toLowerCase().replaceAll("_", "-");
      List<int[]> hints = new ArrayList<>();
      
      Node params = nonTermDef.get(NONTERMINAL_PARAMS);
      Node terminals = params.get(TERMINAL_PARAMS);
      while(terminals.hasSymbols()){
        String termName = ((Terminal)terminals.get(ID).symbol()).getName();
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
    int pruleStart = 0;
    while (nonTermDefs.hasSymbols()) {
      Node nonTermDef = nonTermDefs.get(NONTERMINAL_DEF);
      Node terminal = nonTermDef.get(ID, 1);
      String name = ((Terminal)terminal.symbol()).getName();
      int id = termNames.indexOf(name);
      if (id < 0) {
        id = nonterminals.indexOf(name);
      }
      List<Integer> productionSymbols = new ArrayList<>();
      productionSymbols.add(id);
      
      Node idList = nonTermDef.get(ID_LIST);
      while(idList.hasSymbols()) {
        addSymbolId(idList, productionSymbols);
        idList = idList.get(ID_LIST);
      }
      
      createNewProductionRule(pruleStart, productionSymbols);
      pruleStart++;
      productionSymbols = new ArrayList<>();
      
      Node orOpt = nonTermDef.get(OR_OPTION);
      while (orOpt.hasSymbols()) {
        addSymbolId(orOpt, productionSymbols);
        
        idList = orOpt.get(ID_LIST);
        while(idList.hasSymbols()) {
          addSymbolId(idList, productionSymbols);
          idList = idList.get(ID_LIST);
        }
        
        createNewProductionRule(pruleStart, productionSymbols);
        pruleStart++;
        productionSymbols = new ArrayList<>();
        
        orOpt = orOpt.get(OR_OPTION);
      }

      nonTermDefs = nonTermDefs.get(NONTERMINAL_DEFS);
    }
  }

  private void addSymbolId(Node idList, List<Integer> productionSymbols) {
    String name;
    int id;
    Node termListItem = idList.get(ID);
    name = ((Terminal) termListItem.symbol()).getName();
    id = termNames.indexOf(name);
    if (id < 0) {
      id = nonterminals.indexOf(name);
    }
    productionSymbols.add(id);
  }
  
  private void createNewProductionRule(int pruleId, List<Integer> productionSymbols) {
    int[] productionSymbolIds = new int[productionSymbols.size()];
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
   */
  public static GRBuilder buildLanguage(String grammarSourceFile) throws IOException, FileNotFoundException, ParseException, SyntaxException {
    return new GRBuilder(grammarSourceFile);
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
  
  public int getIdentId() {
    return identId;
  }
  
  public int getConstId() {
    return constId;
  }

}
