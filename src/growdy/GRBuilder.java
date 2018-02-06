
package growdy;


import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import static growdy.GRConstants.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class builds the grammar for your language and will be used as a 
 * resource file for compiling or running your source files written in your
 * programming language.
 * @author Richard DeSilvey
 */
class GRBuilder implements Serializable {

  private List<ProductionRule> grammarRules;
  private List<NonTerminal> nonterminals;
  private List<String> terms;
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
    
    grammarRules = new ArrayList<>();
    nonterminals = new ArrayList<>();
    terms = new ArrayList<>();
    
    build();
  }
  
  private void build() {
    Node termBody = builder.getProgram().get(GRAMMAR).get(TERMINAL_BODY);
    Node specialSyms = termBody.get(SPECIAL_DEF);
    specialSym = ((Terminal)specialSyms.get(CONST).symbol()).getName();
    Node grammarId = builder.getProgram().get(ID);
    grammarName = ((Terminal)grammarId.symbol()).getName();
    rootNodeId = builder.getProgram().symbol().id();
    
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
    Node grammarId = builder.getProgram().get(ID);
    return ((Terminal)grammarId.symbol()).getName();
  }
  
  public ProductionRule[] getGrammarRules() {
    return grammarRules.toArray(new ProductionRule[grammarRules.size()]);
  }

  public NonTerminal[] getNonterminals() {
    return nonterminals.toArray(new NonTerminal[nonterminals.size()]);
  }
  public String[] getTerms() {
    return terms.toArray(new String[terms.size()]);
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
