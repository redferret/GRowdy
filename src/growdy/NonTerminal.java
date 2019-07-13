package growdy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The symbol for directing the builder to build out terminal leaf nodes and to
 * give context to the children nodes.
 * @author Richard DeSilvey
 */
public class NonTerminal extends Symbol implements Serializable{

  private List<Hint> hints;

  public NonTerminal(String symbol, int id) {
    this(symbol, id, new int[][]{});
  }
  
  public NonTerminal(String symbol, int id, int[][] hints) {
    super(symbol, id);
    this.hints = new ArrayList<>();
    final int TERMINAL = 0, PRODUCTION_RULE = 1;
    for (int[] hint : hints) {
      Hint h = new Hint(hint[TERMINAL], hint[PRODUCTION_RULE]);
      this.hints.add(h);
    }
  }
  
  @Override
  public NonTerminal copy() {
    List<Hint> copies = new ArrayList<>();
    hints.stream().map((orig) -> orig.copy()).forEachOrdered((copy) -> {
      copies.add(copy);
    });
    
    NonTerminal copy = new NonTerminal(this.symbol, this.id, new int[][]{});
    copy.hints = copies;
    return copy;
  }
  
  public Hint getHint(int id) {
    if (hints == null) {
      return null;
    }
    for (int i = 0; i < hints.size(); i++) {
      if (hints.get(i).getTerminalId() == id) {
        return hints.get(i);
      }
    }
    return null;
  }
}
