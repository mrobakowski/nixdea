package org.nixdea.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import java.util.Stack;
import static org.nixdea.psi.NixTypes.*;
import static com.intellij.psi.TokenType.*;

%%

%{
  public _NixLexer() {
    this((java.io.Reader)null);
  }
%}

%{
  private Stack<Integer> state;
  public CharSequence yylval_id, yylval_path, yylval_uri, yylval_expr;
  int yychar, yyline, yycolumn;

  public void yy_push_state(Integer sst) {
    state.push(sst);
    yybegin(sst);
  }

  public Integer yy_pop_state() {
    Integer sst;
    try {
      sst = state.pop();
    } catch (Exception e) {
      sst =  YYINITIAL;
    }
    yybegin(yy_top_state());
    return sst;
  }

  public Integer yy_top_state() {
    Integer sst;
    try {
      sst = state.peek();
    } catch (Exception e) {
      sst =  YYINITIAL;
    }
    return sst;
  }

%}

%init{

    this.state = new Stack<Integer>();

%init}

%public
%class _NixLexer
%implements FlexLexer
%function advance
%type IElementType
%xstate STRING IND_STRING
%unicode

EOL=\R
WHITE_SPACE=\s+

SCOMMENT=#[^\r\n]*
MCOMMENT="/"\*([^*]|\*[^/])*\*"/"
INT=[0-9]+
BOOL=(true|false)
ID=[a-zA-Z][a-zA-Z_0-9'-]*
PATH=[a-zA-Z0-9._\-+]*("/"[a-zA-Z0-9._\-+]+)+
SPATH=<[a-zA-Z0-9._\-+]+("/"[a-zA-Z0-9._\-+]+)*>
HPATH=~("/"[a-zA-Z0-9._\-+]+)+
URI=[a-zA-Z][a-zA-Z0-9+\-.]*:[a-zA-Z0-9%/?:@&=+$,\-_.!~*']+

%%
<YYINITIAL> {
  {WHITE_SPACE}                  { return WHITE_SPACE; }

  "="                            { return ASSIGN; }
  "("                            { return LPAREN; }
  ")"                            { return RPAREN; }
  "{"                            { yy_push_state(YYINITIAL); return LCURLY; }
  "}"                            { yy_pop_state(); return RCURLY; }
  "["                            { return LBRAC; }
  "]"                            { return RBRAC; }
  "${"                           { yy_push_state(YYINITIAL); return DOLLAR_CURLY; }
  "$"                            { return DOLLAR; }
  "?"                            { return IS; }
  "@"                            { return NAMED; }
  ":"                            { return COLON; }
  ";"                            { return SEMI; }
  "&&"                           { return AND; }
  "||"                           { return OR; }
  "!"                            { return NOT; }
  "=="                           { return EQ; }
  "!="                           { return NEQ; }
  "<="                           { return LEQ; }
  ">="                           { return GEQ; }
  "<"                            { return LT; }
  ">"                            { return GT; }
  "+"                            { return PLUS; }
  "-"                            { return MINUS; }
  "/"                            { return DIVIDE; }
  "*"                            { return TIMES; }
  "++"                           { return CONCAT; }
  "."                            { return DOT; }
  ","                            { return COMMA; }
  "->"                           { return IMPL; }
  "//"                           { return UPDATE; }
  "assert"                       { return ASSERT; }
  "if"                           { return IF; }
  "else"                         { return ELSE; }
  "then"                         { return THEN; }
  "with"                         { return WITH; }
  "let"                          { return LET; }
  "in"                           { return IN; }
  "rec"                          { return REC; }
  "or"                           { return OR_KW; }
  "..."                          { return ELLIPSIS; }
  "inherit"                      { return INHERIT; }
  "require"                      { return REQUIRE; }
  "requires"                     { return REQUIRES; }
  "import"                       { return IMPORT; }
  "imports"                      { return IMPORTS; }
  "\""                           { yy_push_state(STRING); return QUOT_OPEN; }
  "''"                           { yy_push_state(IND_STRING); return IND_STRING_OPEN; }
  "STR"                          { return STR; }
  "IND_STR"                      { return IND_STR; }

  {SCOMMENT}                     { return SCOMMENT; }
  {MCOMMENT}                     { return MCOMMENT; }
  {INT}                          { return INT; }
  {BOOL}                         { return BOOL; }
  {ID}                           { return ID; }
  {PATH}                         { return PATH; }
  {SPATH}                        { return SPATH; }
  {HPATH}                        { return HPATH; }
  {URI}                          { return URI; }
}

<STRING> {
  ([^\$\"]|\$[^\{\"])+           { yylval_expr = yytext(); return STR; }
//  "$\""                          { yylval_expr = "$"; return STR; } // TODO: somehow make "$" work
  "${"                           { yy_push_state(YYINITIAL); return DOLLAR_CURLY; }
  "\""                           { yy_pop_state(); return QUOT_CLOSE; }
  .                              { zzBufferL = yytext(); }
}

<IND_STRING> {
  ([^\$\']|\$[^\{\']|\'[^\'\$])+ { yylval_expr = yytext(); return IND_STR; }
  "''$"                          { yylval_expr = "$"; return IND_STR; }
  "'''"                          { yylval_expr = "''"; return IND_STR; }
  "''\."                         { yylval_expr = yytext(); return IND_STR; }
  "${"                           { yy_push_state(YYINITIAL); return DOLLAR_CURLY; }
  "''"                           { yy_pop_state(); return IND_STRING_CLOSE; }
  "'"                            { yylval_expr = "'"; return IND_STR; }
  .                              { zzBufferL=yytext(); }
}

[^] { return BAD_CHARACTER; }
