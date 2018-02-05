GRowdy
Provides a way to build grammars using the GRowdy grammar language. This project requires the use of Rowdy as a library to build and run.

Below is an example of a grammar that will be built into a parse tree and can be used by the Rowdy language objects, Node, RowdyLexer, RowdyBuilder, etc.

```
Rowdy

<TERMINAL>

ID 			=> id;
CONST 		=> constant;
IF 			=> "if";
BECOMES 	=> "=";
AND			=> "and";
OR			=> "or";
LESS		=> "<";
GREATER		=> ">";

<SPECIAL> => "< > |";

<BEGIN>

EXPRESSION 			=> 	BOOL_TERM(ID CONST AND) 
						BOOL_TERM_TAIL(OR);
						
BOOL_TERM			=> 	BOOL_FACTOR(ID CONST) 
						BOOL_FACTOR_TAIL(AND);
						
BOOL_TERM_TAIL		=> OR;
BOOL_FACTOR			=> TERM(ID CONST) TERM_TAIL;
BOOL_FACTOR_TAIL	=> AND;
TERM				=> FACTOR(ID CONST) FACTOR_TAIL;
TERM_TAIL			=>
FACTOR				=> ATOMIC(ID CONST);
FACTOR_TAIL			=> 
ATOMIC				=> ID | CONST;
```
