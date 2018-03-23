package org.nixdea.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import java.util.Stack;
import static org.nixdea.psi.NixTypes.*;
import static com.intellij.psi.TokenType.*;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public _NixLexer() {
    this((java.io.Reader)null);
  }

  private Stack<Integer> state = new Stack<Integer>();

  public void pushState(Integer sst) {
    state.push(sst);
    yybegin(sst);
  }

  public int popState() {
    int sst;
    try {
      sst = state.pop();
    } catch (Exception e) {
      sst =  YYINITIAL;
    }
    yybegin(topState());
    return sst;
  }

  public int topState() {
    int sst;
    try {
      sst = state.peek();
    } catch (Exception e) {
      sst =  YYINITIAL;
    }
    return sst;
  }

%}

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
ID=[_a-zA-Z][a-zA-Z_0-9'-]*
PATH=[a-zA-Z0-9._\-+]*("/"[a-zA-Z0-9._\-+]+)+
SPATH=<[a-zA-Z0-9._\-+]+("/"[a-zA-Z0-9._\-+]+)*>
HPATH=\~("/"[a-zA-Z0-9._\-+]+)+
URI=[a-zA-Z][a-zA-Z0-9+\-.]*:[a-zA-Z0-9%/?:@&=+$,\-_.!~*']+

%%
<YYINITIAL> {
  {WHITE_SPACE}                        { return WHITE_SPACE; }

  "="                                  { return ASSIGN; }
  "("                                  { return LPAREN; }
  ")"                                  { return RPAREN; }
  "{"                                  { pushState(YYINITIAL); return LCURLY; }
  "}"                                  { popState(); return RCURLY; }
  "["                                  { return LBRAC; }
  "]"                                  { return RBRAC; }
  "${"                                 { pushState(YYINITIAL); return DOLLAR_CURLY; }
  "$"                                  { return DOLLAR; }
  "?"                                  { return IS; }
  "@"                                  { return NAMED; }
  ":"                                  { return COLON; }
  ";"                                  { return SEMI; }
  "&&"                                 { return AND; }
  "||"                                 { return OR; }
  "!"                                  { return NOT; }
  "=="                                 { return EQ; }
  "!="                                 { return NEQ; }
  "<="                                 { return LEQ; }
  ">="                                 { return GEQ; }
  "<"                                  { return LT; }
  ">"                                  { return GT; }
  "+"                                  { return PLUS; }
  "-"                                  { return MINUS; }
  "/"                                  { return DIVIDE; }
  "*"                                  { return TIMES; }
  "++"                                 { return CONCAT; }
  "."                                  { return DOT; }
  ","                                  { return COMMA; }
  "->"                                 { return IMPL; }
  "//"                                 { return UPDATE; }
  "assert"                             { return ASSERT; }
  "if"                                 { return IF; }
  "else"                               { return ELSE; }
  "then"                               { return THEN; }
  "with"                               { return WITH; }
  "let"                                { return LET; }
  "in"                                 { return IN; }
  "rec"                                { return REC; }
  "or"                                 { return OR_KW; }
  "..."                                { return ELLIPSIS; }
  "inherit"                            { return INHERIT; }
  "import"                             { return IMPORT; }
  "\""                                 { pushState(STRING); return QUOT_OPEN; }
  "''"                                 { pushState(IND_STRING); return IND_STRING_OPEN; }

  {SCOMMENT}                           { return SCOMMENT; }
  {MCOMMENT}                           { return MCOMMENT; }
  {INT}                                { return INT; }
  {BOOL}                               { return BOOL; }
  {ID}                                 { return ID; }
  {PATH}                               { return PATH; }
  {SPATH}                              { return SPATH; }
  {HPATH}                              { return HPATH; }
  {URI}                                { return URI; }
}

<STRING> {
  ([^$\"\\]|\$[^\{\"\\])+              { return STR; }
  ([^$\"]|\$[^\{\"])*\$\"              { yypushback(1); return STR; }
  "\\".                                { return STR; }
  "${"                                 { pushState(YYINITIAL); return DOLLAR_CURLY; }
  "\""                                 { popState(); return QUOT_CLOSE; }
}

<IND_STRING> {
  ([^\$\']|\$[^\{\']|\'[^\'\$])+       { return IND_STR; }
  ([^\$\']|\$[^\{\']|\'[^\'\$])*\$\'\' { yypushback(2); return IND_STR; }
  "''$"                                { return IND_STR; }
  "'''"                                { return IND_STR; }
  "''\\".                              { return IND_STR; }
  "$$"                                 { return IND_STR; }
  "${"                                 { pushState(YYINITIAL); return DOLLAR_CURLY; }
  "''"                                 { popState(); return IND_STRING_CLOSE; }
  "'"                                  { return IND_STR; }
}

[^] { return BAD_CHARACTER; }
