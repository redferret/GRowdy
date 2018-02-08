
package growdy;

import growdy.exceptions.AmbiguousGrammarException;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import growdy.exceptions.SyntaxException;

/**
 * The builder takes a RowdyLexer instance and pulls tokens to build a parseSource
 tree with recursive decent parsing. If no build exceptions occur the 
 * program can be retrieved.
 * @author Richard DeSilvey
 */
public class RowdyBuilder {
  
  private int line;
  private RowdyLexer parser;
  private Token currentToken;
  private final Language language;
  private Node root;
  private Deque<Token> lookAheadQueue;
  
  private RowdyBuilder(Language language) {
    line = 1;
    this.language = language;
    lookAheadQueue = new ArrayDeque<>();
  }
  
  public static RowdyBuilder getBuilder(Language language){
    return new RowdyBuilder(language);
  }
  
  public Node getProgram() {
    return root;
  }
  
  public void buildAs(RowdyLexer parser, int programType) throws SyntaxException, AmbiguousGrammarException {
    this.parser = parser;
    NonTerminal program = (NonTerminal) language.getSymbol(programType);
    root = new Node(program, 1);
    currentToken = this.parser.getToken();
    if (currentToken == null){
      return;
    }
    consumeEOLN();
    int id = currentToken.getID();
    addToNode(root, produce(program, id));
    build(root);
    if (currentToken != null) {
      throw new SyntaxException("Unexpected token " + currentToken.getSymbol());
    }
  }
  
  private void consumeEOLN() {
    while (currentToken.getID() == 200) {
      if (currentToken.getID() == 200) {
        line++;
      }
      currentToken = this.parser.getToken();
      if (currentToken == null){
        return;
      }
    }
  }

  /**
   * Walks through the tree recursively building on non-terminals. If a syntax
   * error is detected the line number is printed out.
   *
   * @param parent
   * @throws growdy.exceptions.SyntaxException
   */
  public void build(Node parent) throws SyntaxException, AmbiguousGrammarException {
    Symbol symbol;
    ProductionSymbols rule;
    List<Node> children = parent.getAll();
    Node current;
    for (int i = 0; i < children.size(); i++) {
      current = children.get(i);
      symbol = current.symbol();
      if (symbol instanceof NonTerminal) {
        if (currentToken == null) {
          if (!current.hasSymbols() && children.get(i).isTrimmable()) {
            children.remove(i--);
          }
          continue;
        }
        rule = produce((NonTerminal) symbol, currentToken.getID());
        addToNode(current, rule);
        trimChildren(current);
        build(current);
      } else {
        if (symbol.id() != currentToken.getID()) {
          throw new SyntaxException("Syntax error, unexpected token '"
                  + currentToken.getSymbol() + "' on Line " + line);
        }
        children.remove(i);
        Terminal terminal = new Terminal(symbol.getSymbolAsString(), currentToken.getID(), currentToken.getSymbol());
        children.add(i, new Node(terminal, line));
        currentToken = parser.getToken();
        while (currentToken != null && currentToken.getID() == 200) {
          line++;
          currentToken = parser.getToken();
        }
        if (currentToken == null) break;
      }
    }

  }
  
  private void trimChildren(Node current) throws AmbiguousGrammarException {
    List<Node> children = current.getAll();
    ProductionSymbols rule;
    for (int i = 0; i < children.size(); i++) {
      Symbol symbol = children.get(i).symbol();
      if (symbol instanceof NonTerminal){
        rule = produce((NonTerminal) symbol, currentToken.getID());
        if (rule.getSymbols().length == 0 && children.get(i).isTrimmable()){
          children.remove(i--);
        }
      }
    }
    int trimmableCount = 0;
    for (int i = 0; i < children.size(); i++) {
      Symbol symbol = children.get(i).symbol();
      if (symbol instanceof NonTerminal) {
        if (children.get(i).isTrimmable()){
          trimmableCount++;
        }
      }
    }
    if (trimmableCount > 1) {
      throw new AmbiguousGrammarException((NonTerminal)current.symbol());
    }
  }
  
  /**
   * Builds a rule from the given NonTerminal using the id to map onto a hint.
   *
   * @param symbol The NonTerminal for reference
   * @param terminal The id from a token, usually a terminal
   * @return Fetches a production rule from the language's grammar.
   */
  public ProductionSymbols produce(NonTerminal symbol, int terminal) {
    Hint productionHint = symbol.getHint(terminal);
    return language.getProductionSymbols(productionHint);
  }

  /**
   * Adds to the parent node the production rules. Each child is from the
   * production rule.
   *
   * @param parent The parent being added to
   * @param productionRule
   */
  public void addToNode(Node parent, ProductionSymbols productionRule) {
    Symbol[] symbols = productionRule.getSymbols();
    Rule[] rules = productionRule.getRules();
    for (int i = 0; i < symbols.length; i++){
      Symbol symbol = symbols[i];
      Rule rule = rules[i];
      Node node = new Node(symbol, line);
      node.setTrimmable(rule.isTrimmable());
      parent.add(node);
    }
  }
}
