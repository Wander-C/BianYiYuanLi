grammar Expression;

prog: expr EOF ;

expr: left=expr op=('*'|'/') right=expr # MulDiv
    | left=expr op=('+'|'-') right=expr # AddSub
    | '(' expr ')' # Paren
    | INT # Int
    ;

WS: [ \t\n\r]+ -> skip;
INT: [0-9]+ ;