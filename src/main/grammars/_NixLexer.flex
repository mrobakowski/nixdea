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

  private Stack<Integer> stateStack = new Stack<Integer>();

  public void pushState(int sst) {
    stateStack.push(yystate());
    yybegin(sst);
  }

  public void popState() {
    if (stateStack.size() > 0) {
        yybegin(stateStack.pop());
    } else {
        // Should we do something here other than staying in the current state?
    }
  }

  public void onReset() {
    stateStack.clear();
  }
%}

%public
%class _NixLexer
%implements FlexLexer
%function advance
%type IElementType
%xstate STRING IND_STRING INSIDE_STRING_INTERPOLATION
%unicode
%eof{
  stateStack.clear();
%eof}

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
<YYINITIAL, INSIDE_STRING_INTERPOLATION> {
  {WHITE_SPACE}                        { return WHITE_SPACE; }

  "="                                  { return ASSIGN; }
  "("                                  { return LPAREN; }
  ")"                                  { return RPAREN; }
  "{"                                  { pushState(yystate()); return LCURLY; }
  "}"                                  { popState(); return RCURLY; }
  "["                                  { return LBRAC; }
  "]"                                  { return RBRAC; }
  "${"                                 { pushState(yystate()); return DOLLAR_CURLY; }
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
  "\""                                 { pushState(STRING); return DOUBLE_QUOTE; }
  "''"                                 { pushState(IND_STRING); return SINGLE_QUOTE_TWICE; }

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
  "${"                                 { pushState(INSIDE_STRING_INTERPOLATION); return DOLLAR_CURLY; }
  "\""                                 { popState(); return DOUBLE_QUOTE; }
}

<IND_STRING> {
  ([^\$\']|\$[^\{\']|\'[^\'\$])+       { return IND_STR; }
  ([^\$\']|\$[^\{\']|\'[^\'\$])*\$\'\' { yypushback(2); return IND_STR; }
  "''$"                                { return IND_STR; }
  "'''"                                { return IND_STR; }
  "''\\".                              { return IND_STR; }
  "$$"                                 { return IND_STR; }
  "${"                                 { pushState(INSIDE_STRING_INTERPOLATION); return DOLLAR_CURLY; }
  "''"                                 { popState(); return SINGLE_QUOTE_TWICE; }
  "'"                                  { return IND_STR; }
}

<YYINITIAL, STRING, IND_STRING> [^] { return BAD_CHARACTER; }
