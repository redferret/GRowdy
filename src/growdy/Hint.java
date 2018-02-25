package growdy;

import java.io.Serializable;

/**
 * A hint holds a terminal and a hint value to aid the parser when the program
 * tree is being built. The hint is a production rule based on the current token
 * (terminal).
 *
 * @author Richard DeSilvey
 */
public class Hint  implements Serializable{

  private final int terminalId;
  private final int hint;

  public Hint(int terminalId, int productionRule) {
    this.terminalId = terminalId;
    this.hint = productionRule;
  }

  public Hint copy() {
    return new Hint(this.terminalId, this.hint);
  }
  
  public int getProductionHint() {
    return hint;
  }

  public int getTerminalId() {
    return terminalId;
  }
}
