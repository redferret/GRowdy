package growdy;

import java.util.ArrayList;

/**
 * Tree Node for a parse tree
 * @param <T>
 * @param <D>
 */
public abstract class Node<T extends Node, D extends Object> {

  protected ArrayList<T> children;
  protected final Symbol symbol;
  protected Boolean seqActive;
  protected final int line;
  protected boolean trimmable;

  public Node(Symbol symbol, int lineNumber) {
    children = new ArrayList<>();
    this.symbol = symbol;
    seqActive = false;
    this.line = lineNumber;
    trimmable = false;
  }

  public boolean isTrimmable() {
    return trimmable;
  }

  public void setTrimmable(boolean trimmable) {
    this.trimmable = trimmable;
  }
  
  public Boolean isSeqActive() {
    return seqActive;
  }

  public void setSeqActive(Boolean seqActive) {
    this.seqActive = seqActive;
  }
  
  public int getLine() {
    return line;
  }

  public void add(T child) {
    children.add(child);
  }

  public Symbol symbol() {
    return symbol;
  }

  public boolean hasSymbols() {
    return !children.isEmpty();
  }
  
  public boolean isEmpty() {
    return children.isEmpty();
  }

  public T getLeftMost(boolean includeTerminals) {
    if (children.isEmpty()) {
      return null;
    }
    for (int c = 0; c < children.size(); c++) {
      if (children.get(c) != null) {
        if (!includeTerminals && children.get(c).symbol() instanceof NonTerminal) {
          return children.get(c);
        } else if (includeTerminals && children.get(c).symbol() instanceof Terminal){
          return children.get(c);
        }
      }
    }
    return null;
  }
  
  public T getLeftMost() {
    return getLeftMost(false);
  }

  /**
   * Gets the child node with the id, only returns the first occurance of the
   * found child.
   *
   * @param id The child's ID to search for, null if nothing found
   * @return The found child, null if nothing was found
   */
  public T get(int id) {
    return get(id, 0, false);
  }
  
  public T get(int id, int occur) {
    return get(id, occur, false);
  }
  
  public T get(int id, boolean throwException) {
    return get(id, 0, throwException);
  }

  /**
   * Gets the child node with the id. Since there could be duplicates or
   * multiple child nodes with the same ID, occur will tell the method to skip a
   * certain number of occurrences of the given ID. If occur is 1 then it will
   * skip the first occurrence of the search.
   *
   * @param id The id to search for
   * @param occur The number of times to skip a duplicate
   * @param throwException
   * @return The child node of this parent, null if it doesn't exist.
   */
  public T get(int id, int occur, boolean throwException) throws RuntimeException {
    for (int c = 0; c < children.size(); c++) {
      if (children.get(c).symbol().id() == id
              && occur == 0) {
        return children.get(c);
      } else if (children.get(c).symbol().id() == id
              && occur > 0) {
        occur--;
      }
    }
    if (throwException) {
      throw new RuntimeException("The id '" + id
              + "' could not be found for the node '" + symbol + "' on line "
              + line);
    } else {
      return null;
    }
  }

  public void setChildren(ArrayList<T> children) {
    this.children = children;
  }
  
  public ArrayList<T> getAll() {
    return children;
  }
  
  /**
   * Performs a hard copy of all the children nodes of this parent and a
   * hard copy of all it's children's children and so on.
   * @return A hard copy of the subtrees for this root
   */
  public ArrayList<T> copyChildren() {
    ArrayList<T> copies = new ArrayList<>();
    children.stream().map((orig) -> orig.copy()).forEachOrdered((copy) -> {
      copies.add((T) copy);
    });
    return copies;
  }
  
  public Symbol copySymbol() {
    Symbol cSymbol = null;
    if (this.symbol instanceof Terminal) {
      cSymbol = ((Terminal)symbol).copy();
    } else if (this.symbol instanceof NonTerminal) {
      cSymbol = ((NonTerminal)symbol).copy();
    }
    return cSymbol;
  }
  
  public abstract D execute(D leftValue);
  
  public abstract T copy();

  @Override
  public String toString() {
    return symbol.toString() + (children.isEmpty() ? "" : children.toString());
  }
}
