package com.jolt.JoltLexer;

import java.util.*;

public class Lexer {
    private final String input;
    private final List<Token> tokens = new ArrayList<>();
    private int pos = 0;

    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("let", TokenType.LET);
        keywords.put("int", TokenType.INT);
        keywords.put("float", TokenType.FLOAT);
        keywords.put("string", TokenType.STRING);
        keywords.put("tensor", TokenType.TENSOR);
        keywords.put("array", TokenType.ARRAY);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("print", TokenType.PRINT);
        keywords.put("zeta", TokenType.ZETA);
    }

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
            } else if (Character.isDigit(c)) {
                tokenizeNumber();
            } else if (Character.isLetter(c) || c == '_') {
                tokenizeIdentifier();
            } else {
                switch (c) {
                    case '+': addToken(TokenType.PLUS, "+"); break;
                    case '-':
                        if (match('>')) addToken(TokenType.ARROW, "->");
                        else addToken(TokenType.MINUS, "-");
                        break;
                    case '*': addToken(TokenType.MULT, "*"); break;
                    case '/': addToken(TokenType.DIV, "/"); break;
                    case '=':
                        if (match('=')) addToken(TokenType.EQ, "==");
                        else addToken(TokenType.ASSIGN, "=");
                        break;
                        case '!':
                        if (match('=')) addToken(TokenType.NEQ, "!=");
                        break;
                    case '>':
                        if (match('=')) addToken(TokenType.GTE, ">=");
                        else addToken(TokenType.GT, ">");
                        break;
                    case '<':
                        if (match('=')) addToken(TokenType.LTE, "<=");
                        else addToken(TokenType.LT, "<");
                        break;
                    case '(': addToken(TokenType.LPAREN, "("); break;
                    case ')': addToken(TokenType.RPAREN, ")"); break;
                    case '{': addToken(TokenType.LBRACE, "{"); break;
                    case '}': addToken(TokenType.RBRACE, "}"); break;
                    case '[': addToken(TokenType.LBRACKET, "["); break;
                    case ']': addToken(TokenType.RBRACKET, "]"); break;
                    case ';': addToken(TokenType.SEMICOLON, ";"); break;
                    case ',': addToken(TokenType.COMMA, ","); break;
                    case '"': tokenizeString(); break;
                    default:
                        throw new RuntimeException("Unexpected character: " + c);
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }
    private void tokenizeNumber() {
        int start = pos;
        while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            pos++;
        }
        String lexeme = input.substring(start, pos);
        tokens.add(new Token(TokenType.NUMBER, lexeme));
    }
    private void tokenizeIdentifier() {
        int start = pos;
        while (pos < input.length() && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
            pos++;
        }
        String lexeme = input.substring(start, pos);
        TokenType type = keywords.getOrDefault(lexeme, TokenType.IDENT);
        tokens.add(new Token(type, lexeme));
    }

    private void tokenizeString() {
        int start = ++pos;
        while (pos < input.length() && input.charAt(pos) != '"') {
            pos++;
        }
        if (pos >= input.length()) {
            throw new RuntimeException("Unterminated string literal");
        }
        String lexeme = input.substring(start, pos);
        pos++; // Skip closing quote
        tokens.add(new Token(TokenType.STRING_LITERAL, lexeme));
    }

    private boolean match(char expected) {
        if (pos + 1 < input.length() && input.charAt(pos + 1) == expected) {
            pos += 2;
            return true;
        }
        return false;
    }

    private void addToken(TokenType type, String lexeme) {
        tokens.add(new Token(type, lexeme));
        pos++;
    }
}