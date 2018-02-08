
package growdy;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A piece to a grammar that contains ids for a builder to consume and use to
 * build a parse tree.
 * @author Richard DeSilvey
 */
public class ProductionRule implements Serializable {
  private final int id;
  private final Rule[] simpleProduction;

  public ProductionRule(int id, Rule[] simpleProduction) {
    this.id = id;
    this.simpleProduction = simpleProduction;
  }

  public int getId() {
    return id;
  }

  public Rule[] getProductionSymbols() {
    return simpleProduction;
  }
 
  @Override
  public String toString() {
    return id + ": " + Arrays.toString(simpleProduction);
  }
}
