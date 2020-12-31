lexer grammar ProjectLexer;

// Keywords
MAINCLASS   : 'mainclass';
PUBLIC      : 'public';
STATIC      : 'static';
VOID        : 'void';
MAIN        : 'main';

PRINTLN     : 'println';

FLOAT_TYPE  : 'float';
INT_TYPE    : 'int';

FOR         : 'for';
WHILE       : 'while';
IF          : 'if';
ELSE        : 'else';

// Operators
MULTIPL     : '*';
DIVISION    : '/';
PLUS        : '+';
MINUS       : '-';
GT          : '>';
LT          : '<';
GEQ         : '>=';
LEQ         : '<=';
EQ          : '==';
NEQ         : '!=';
ASSIGN      : '=';
OPENPAR     : '(';
CLOSEPAR    : ')';
OPENBRACK   : '{';
CLOSEBRACK  : '}';

SEMICOL     : ';';
COMMA       : ',';


// Identifiers
ID          : LETTER(LETTER|DIGIT|'_')*;
FLOAT       : [0-9]+'.'DIGIT+;
INT         : DIGIT+;

fragment DIGIT  : [0-9];
fragment LETTER : [A-Za-z];

// Whitespace
WSP         : [ \t\r\n] -> skip ;