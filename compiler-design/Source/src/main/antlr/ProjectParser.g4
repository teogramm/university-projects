parser grammar ProjectParser;

@header{
    import org.teogramm.compiler.exprInfo.ExprInfo;
}

options{ tokenVocab = ProjectLexer;}

program     : MAINCLASS ID OPENBRACK PUBLIC STATIC VOID MAIN OPENPAR CLOSEPAR comp_stmt CLOSEBRACK;

comp_stmt returns [ExprInfo info] : OPENBRACK stmt_list CLOSEBRACK;

stmt_list returns [ExprInfo info]  : stmt_list stmt
            |
            ;

stmt returns [ExprInfo info]
            : assign_stmt #stmt_assign
            | for_stmt #stmt_for
            | while_stmt #stmt_while
            | if_stmt #stmt_if
            | comp_stmt #stmt_comp
            | declaration #stmt_decl
            | null_stmt #stmt_null
            | PRINTLN OPENPAR expr CLOSEPAR SEMICOL #stmt_println
            ;

declaration returns [ExprInfo info] : type id_list SEMICOL;

type        : INT_TYPE
            | FLOAT_TYPE
            ;

id_list     : ID COMMA id_list
            | ID
            ;

null_stmt   : SEMICOL;

assign_stmt returns [ExprInfo info]: assign_expr SEMICOL;

assign_expr returns [ExprInfo info] : ID ASSIGN expr;

expr returns [ExprInfo info]
            : assign_expr
            | rval
            ;

for_stmt returns [ExprInfo info]
            : FOR OPENPAR opassign_expr SEMICOL opbool_expr SEMICOL opassign_expr CLOSEPAR stmt;

opassign_expr  returns [ExprInfo info]
             : assign_expr
             |
             ;

opbool_expr returns [ExprInfo info]
            : bool_expr
            |
            ;

while_stmt returns [ExprInfo info]  : WHILE OPENPAR bool_expr CLOSEPAR stmt;

if_stmt returns [ExprInfo info]
            : IF OPENPAR bool_expr CLOSEPAR stmt else_part;

else_part returns [ExprInfo info]
            : ELSE stmt
            |
            ;

bool_expr returns [ExprInfo info]   : expr c_op expr;

c_op        : GT #c_op_gt
            | LT #c_op_lt
            | GEQ #c_op_geq
            | LEQ #c_op_leq
            | EQ #c_op_eq
            | NEQ #c_op_neq
            ;

rval returns [ExprInfo info]
            : rval PLUS term #rval_add
            | rval MINUS term #rval_rem
            | term  #rval_term
            ;

term returns [ExprInfo info]
            : term MULTIPL factor #term_multipl
            | term DIVISION factor #term_div
            | factor    #simplefactor
            ;

factor returns [ExprInfo info]
            : OPENPAR expr CLOSEPAR #factor_par
            | MINUS factor #factor_uminus
            | ID    #factor_var_reference
            | INT   #factor_int
            | FLOAT #factor_float
            ;