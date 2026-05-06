
lexer grammar SysYLexer;


//Keywords:
CONST:'const';
INT: 'int';
VOID:'void';
IF:'if' ;
ELSE:'else' ;
WHILE:'while';
BREAK:'break';
CONTINUE:'continue';
RETURN:'return';

//Operators
PLUS: '+';
MINUS:'-';
MUL:'*';
DIV:'/';
MOD:'%';
ASSIGN:'=';
EQ:'==';
NEQ:'!=';
LT:'<';
GT:'>';
LE:'<=';
GE:'>=';
NOT:'!';
AND:'&&';
OR:'||';

//Separators:
L_PAREN : '(';
R_PAREN : ')';
L_BRACE : '{';
R_BRACE : '}';
L_BRACKT:'[';
R_BRACKT:']';
COMMA : ',';
SEMICOLON : ';';

//Identifiers:
IDENT:[a-zA-Z_][a-zA-Z_0-9]*;
INTEGER_CONST:   HexInt | OctalInt| DecimalInt;
fragment DecimalInt : [1-9][0-9]* | '0' ;//10机制
fragment OctalInt  : '0'[0-7]+ ;//8进制
fragment HexInt    : '0'[xX][0-9a-fA-F]+ ;//16进制

//忽略
 WS:[ \t\r\n]+ -> skip ;
LINE_COMMENT: '//' .*? '\n' -> skip;
MULTILINE_COMMENT: '/*' .*? '*/' -> skip;
