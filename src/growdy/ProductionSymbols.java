package growdy;

import java.util.Arrays;

/**
 * The translation from IDs to Symbols are held for the builder
 *
 * @author Richard DeSilvey
 */
public class ProductionSymbols {

  private Symbol[] defs;
  private Rule[] rules;

  public ProductionSymbols() {
    defs = new Symbol[0];
    rules = new Rule[0];
  }

  public ProductionSymbols(Symbol[] defs, Rule[] rules) {
    this.defs = new Symbol[defs.length];
    System.arraycopy(defs, 0, this.defs, 0, defs.length);
    
    this.rules = new Rule[rules.length];
    System.arraycopy(rules, 0, this.rules, 0, rules.length);
  }

  public Symbol[] getSymbols() {
    return defs;
  }
  
  public Rule[] getRules() {
    return rules;
  }

  public String toString() {
    return Arrays.toString(defs);
  }
}
