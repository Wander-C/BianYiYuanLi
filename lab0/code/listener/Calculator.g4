grammar Calculator;

expression
    : '(' expression ')'                  # parenExpression
    | expression op=('*'|'/') expression  # mulDivExpression
    | expression op=('+'|'-') expression  # addSubExpression
    | NUMBER                              # numberExpression
    ;

NUMBER: [0-9]+ ('.' [0-9]+)?;
WS: [ \t\r\n]+ -> skip;