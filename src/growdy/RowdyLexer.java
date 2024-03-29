package growdy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import growdy.exceptions.ParseException;

/**
 * RowdyLexer parses code files and allows a fetch for each individual token when
 * <code>getToken()</code> is called. Once the token is called for it, it is removed from the file
 * stack. The file stack contains groupings of characters which are later
 * interpreted when <code>getToken()</code> is called.
 *
 * @author Richard DeSilvey
 */
public class RowdyLexer {

  private final String SPECIALCHARGROUP;

  public static final int IDENTIFIER = 0, CONSTANT = 1, SPECIAL = -2, UNKNOWN = -1;
  /**
   * The table of all the reserved words/symbols for the language.
   */
  private final HashMap<String, Integer> RESERVED_WORDS;

  /**
   * Each pattern aids in parsing the file and fetching a token.
   */
  private static final Pattern id, number, tokenNumber, whiteSpace, tokenString;

  private List<String> fileStack;

  static {
    // Set up each regular expression
    id = Pattern.compile("([a-z]|[A-Z])+(\\_|\\d|[a-z]|[A-Z])*");
    whiteSpace = Pattern.compile("\\s");
    tokenString = Pattern.compile("\"(.)*\"");
    number = Pattern.compile("-?\\d*\\.?\\d*(L|l|D|d|F|f)?");
    tokenNumber = Pattern.compile("-?\\d+(\\.?\\d+)?(L|l|D|d|F|f)?");
  }
  
  /**
   * Creates a new RowdyLexer
   *
   * @param reserved The reserved words in the language
   * @param operators The string containing all the operators
   */
  public RowdyLexer(String[] reserved, String operators) {

    RESERVED_WORDS = new HashMap<>();
    SPECIALCHARGROUP = operators;

    for (int i = 0; i < reserved.length; i++) {
      if (!reserved[i].isEmpty()) {
        RESERVED_WORDS.put(reserved[i], i);
      }
    }
  }

  /**
   * Parses a code file. New tokens will be generated each time parseSource 
   * is called on a given file.
   *
   * @param fileName The code file being parsed.
   * @throws java.io.IOException
   * @throws java.io.FileNotFoundException
   * @throws growdy.exceptions.ParseException
   */
  public void parseSource(String fileName) throws IOException, FileNotFoundException, ParseException {
    fileStack = parseFile(fileName);
  }

  /**
   * Parses a single line of code
   * @param code
   * @throws ParseException 
   */
  public void parseLine(String code) throws ParseException {
    fileStack = parseCode(code);
  }
  
  /**
   * Gets the contents of a file and adds all relevant groupings of characters
   * to a stack.
   *
   * @param fileName The file name for the datafile
   * @return The stack of grouped characters
   */
  private List<String> parseFile(String fileName) throws FileNotFoundException, 
          IOException,
          ParseException {

    List<String> symbols = new LinkedList<>();
    String line;
    File file = new File(fileName);
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      while ((line = reader.readLine()) != null) {
        symbols.addAll(parseCode(line));
      }
    }
    return symbols;
  }
  
  private List<String> parseCode(String line) throws ParseException {
    List<String> symbols = new LinkedList<>();
    boolean eoln = false;
    char cur;
    String word = "";
    for (int c = 0, prev = 0; c < line.length(); c++) {
      cur = line.charAt(c);

      if (cur == '/') {
        if (line.charAt(c + 1) == '/') {
          break;
        }
      }
      if (id.matcher(Character.toString(cur)).matches()) {
        word += cur;
        do {
          c++;
          if (c >= line.length()) {
            eoln = true;
            break;
          }
          cur = line.charAt(c);
          word += cur;
        } while (id.matcher(word).matches());
      } else if (cur == '\"') {
        word += cur;
        do {
          c++;
          if (c >= line.length()) {
            break;
          }
          cur = line.charAt(c);
          word += cur;
        } while (cur != '\"');
        eoln = true;
      } else if (tokenNumber.matcher(Character.toString(cur)).matches()) {
        word += cur;
        do {
          c++;
          if (c >= line.length()) {
            eoln = true;// Flag adjustments
            break;
          }
          cur = line.charAt(c);
          word += cur;
        } while (number.matcher(word).matches());
      } else if (SPECIALCHARGROUP.contains(Character.toString(cur))
              && cur != ' ') {
        word += cur;
        do {
          c++;
          if (c >= line.length()) {
            eoln = true;// Flag adjustments
            break;
          }
          cur = line.charAt(c);
          word += cur;
        } while (SPECIALCHARGROUP.contains(word) && cur != ' ');
      }

      if (!word.isEmpty()) {
        symbols.add((eoln)? 
          word
        :
          word.substring(0, word.length() - 1));
      }

      if (!whiteSpace.matcher(Character.toString(cur)).matches()) {
        if (c == prev) { // Stop parsing on unknown symbol
          throw new ParseException("Parsing halted, unable"
                  + " to resolve character '" + cur + "'");
        }
        if (c < line.length() && !eoln) {
          prev = c;
          c--;
        }
      }
      eoln = false;
      word = "";
    }

    symbols.add("EOLN");
    return symbols;
  }

  /**
   * Tests if there still exists a token to consume.
   *
   * @return True if there still exists tokens.
   */
  public boolean hasToken() {
    return !fileStack.isEmpty();
  }

  public int tokenCount() {
    return fileStack.size();
  }
  
  /**
   * Fetches a token from the given file.
   *
   * @return The next token, null if there are no more tokens left to consume.
   */
  public Token getToken() {

    if (fileStack.isEmpty()) {
      return null;
    }

    String symbol = fileStack.remove(0);
    Integer tokenId = RESERVED_WORDS.get(symbol);

    if (tokenId == null) {

      switch (symbol) {

        case "EOLN":
          return new Token(200, "EOLN");
        default:
          if (id.matcher(symbol).matches()) {
            return new Token(IDENTIFIER, symbol);
          } else if (tokenString.matcher(symbol).matches()) {
            return new Token(CONSTANT, symbol);
          } else if (tokenNumber.matcher(symbol).matches()) {
            return new Token(CONSTANT, symbol);
          } else {
            if (SPECIALCHARGROUP.contains(symbol)) {
              return new Token(SPECIAL, "Special '" + symbol + "'");
            }
            return new Token(UNKNOWN, "Unknown '" + symbol + "'");
          }
      }

    } else {
      return new Token(tokenId, symbol);
    }

  }

}
