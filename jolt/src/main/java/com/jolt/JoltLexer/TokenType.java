package com.jolt.JoltLexer;

public enum TokenType {
    // Keywords
    LET, INT, FLOAT, STRING, TENSOR, ARRAY,
    IF, ELSE, WHILE, PRINT, ZETA,
    // Literals
    IDENT, NUMBER, STRING_LITERAL,

    // Operators
    ASSIGN, PLUS, MINUS, MULT, DIV,
    EQ, NEQ, GT, LT, GTE, LTE,

    // Delimiters
    LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET,
    SEMICOLON, COMMA, ARROW,

    EOF;
}