
package growdy;

import java.util.List;
import growdy.exceptions.SyntaxException;

/**
 * The builder takes a RowdyLexer instance and pulls tokens to build a parse
 * tree with recursive decent parsing. If no build exceptions occur the 
 * program can be retrieved.
 * @author Richard DeSilvey
 */
public class RowdyBuilder {
  
  private int line;
  private RowdyLexer parser;
  private Token currentToken;
  private final Language language;
  private Node root;
  
  private RowdyBuilder(Language language) {
    line = 1;
    this.language = language;
    
  }
  
  public static RowdyBuilder getBuilder(Language language){
    return new RowdyBuilder(language);
  }
  
  public Node getProgram() {
    return root;
  }
  
  public void buildAs(RowdyLexer parser, int programType) throws SyntaxException {
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
  public void build(Node parent) throws SyntaxException {
    Symbol symbol;
    ProductionSymbols rule;
    List<Node> children = parent.getAll();
    Node current;
    for (int i = 0; i < children.size(); i++) {
      current = children.get(i);
      symbol = current.symbol();
      if (symbol instanceof NonTerminal) {
        if (currentToken == null) break;
        rule = produce((NonTerminal) symbol, currentToken.getID());
        addToNode(current, rule);
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
   * @param rule The production rule.
   */
  public void addToNode(Node parent, ProductionSymbols rule) {
    Symbol[] symbols = rule.getSymbols();
    for (Symbol symbol : symbols) {
      Node node = new Node(symbol, line);
      parent.add(node);
    }
  }
}