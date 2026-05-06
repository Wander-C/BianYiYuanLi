parser grammar SysYParser;

options { tokenVocab=SysYLexer; } //使用 SysYLexer

program
   : compUnit
   ;

compUnit
    : (decl | funcDef)+EOF
    ;

decl
    : constDecl
    | varDecl
    ;

constDecl
    : 'const' bType constDef (',' constDef)* ';'
    ;

bType
    : 'int'
    ;

constDef
    : IDENT ('[' constExp ']')* '=' constInitVal
    ;

constInitVal
    : constExp
    | '{' (constInitVal (',' constInitVal)*)? '}'
    ;

varDecl
    : bType varDef (',' varDef)* ';'
    ;

varDef
    : IDENT ('[' constExp ']')*
    | IDENT ('[' constExp ']')* '=' initVal
    ;

initVal
    : exp
    | '{' (initVal (',' initVal)*)? '}'
    ;

funcDef
    : funcType IDENT '(' funcFParams? ')' block
    ;

funcType
    : 'void'
    | 'int'
    ;

funcFParams
    : funcFParam (',' funcFParam)*
    ;

funcFParam
    : bType IDENT ('[' ']' ('[' exp ']')*)?
    ;

block
    : '{' blockItem* '}'
    ;

blockItem
    : decl
    | stmt
    ;

stmt
    : lVal '=' exp ';'                       # assignStmt
    | exp? ';'                               # expStmt
    | block                                  # blockStmt
    | 'if' '(' cond ')' stmt ('else' stmt)?  # ifStmt
    | 'while' '(' cond ')' stmt              # whileStmt
    | 'break' ';'                            # breakStmt
    | 'continue' ';'                         # continueStmt
    | 'return' exp? ';'                      # returnStmt
    ;

exp
   : L_PAREN exp R_PAREN
   | lVal
   | number
   | IDENT L_PAREN funcRParams? R_PAREN
   | unaryOp exp
   | exp (MUL | DIV | MOD) exp
   | exp (PLUS | MINUS) exp
   ;

cond
   : exp
   | cond (LT | GT | LE | GE) cond
   | cond (EQ | NEQ) cond
   | cond AND cond
   | cond OR cond
   ;

lVal
   : IDENT (L_BRACKT exp R_BRACKT)*
   ;

number
   : INTEGER_CONST
   ;


unaryOp
   : PLUS
   | MINUS
   | NOT
   ;

funcRParams
   : param (COMMA param)*
   ;

param
   : exp
   ;



constExp
   : exp
   ;