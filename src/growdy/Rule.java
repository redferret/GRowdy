
package growdy;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class Rule implements Serializable {
  private final int nonterminalId;
  private boolean trimmable;

  public Rule(int nonterminalId) {
    this.nonterminalId = nonterminalId;
    this.trimmable = false;
  }

  public int getNonterminalId() {
    return nonterminalId;
  }

  public boolean isTrimmable() {
    return trimmable;
  }

  public void markAsTrimmable() {
    this.trimmable = true;
  }
  
}
