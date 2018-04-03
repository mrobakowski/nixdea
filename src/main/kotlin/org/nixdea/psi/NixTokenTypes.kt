package org.nixdea.psi

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.nixdea.psi.NixTypes.*

fun tokenSetOf(vararg tokens: IElementType) = TokenSet.create(*tokens)

val NIX_KEYWORDS = tokenSetOf(
        ASSERT,
        ELSE,
        IF,
        IMPORT,
        IN,
        INHERIT,
        IS,
        REC,
        NAMED,
        OR_KW,
        THEN,
        WITH,
        LET
)

val NIX_OPERATORS = tokenSetOf(
        AND,
        ASSIGN,
        COLON,
        COMMA,
        CONCAT,
        DIVIDE,
        DOT,
        EQ,
        GEQ,
        GT,
        IMPL,
        LEQ,
        LT,
        MINUS,
        NEQ,
        NOT,
        OR,
        PLUS,
        TIMES,
        UPDATE
)

val NIX_STR_TOKENS = tokenSetOf(
        SPATH,
        IND_STR,
        DOUBLE_QUOTE,
        PATH,
        SINGLE_QUOTE_TWICE,
        HPATH,
        STR,
        URI
)

val NIX_COMMENT = tokenSetOf(
        MCOMMENT, SCOMMENT
)

val NIX_OTHER_TOKENS = tokenSetOf(
        BOOL,
        DOLLAR_CURLY,
        ELLIPSIS,
        ID,
        INT,
        LBRAC,
        LCURLY,
        LPAREN,
        MCOMMENT,
        RBRAC,
        RCURLY,
        RPAREN,
        SCOMMENT,
        SEMI
)