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

  private final List<Hint> hints;
  private int groupId;
  private boolean trim;

  public NonTerminal(String symbol, int id, int[][] hints) {
    super(symbol, id);
    groupId = 0;
    trim = false;
    this.hints = new ArrayList<>();
    final int TERMINAL = 0, PRODUCTION_RULE = 1;
    for (int[] hint : hints) {
      Hint h = new Hint(hint[TERMINAL], hint[PRODUCTION_RULE]);
      this.hints.add(h);
    }
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public int getGroupId() {
    return groupId;
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

  public boolean isTrimmable() {
    return trim;
  }
  
  public void markAsTrimmable() {
    this.trim = true;
  }
}
