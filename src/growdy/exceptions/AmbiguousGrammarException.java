
package growdy.exceptions;

import growdy.NonTerminal;
import java.util.Arrays;

/**
 *
 * @author Richard
 */
public class AmbiguousGrammarException extends Throwable {

  public AmbiguousGrammarException(NonTerminal ... nonterminals) {
    super("Ambiguous Grammar detected for the NonTerminals " + Arrays.toString(nonterminals));
  }
  
}
