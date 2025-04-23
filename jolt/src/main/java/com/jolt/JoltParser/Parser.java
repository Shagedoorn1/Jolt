package com.jolt.JoltParser;

import com.jolt.JoltAST.*;
import com.jolt.JoltLexer.Token;
import com.jolt.JoltLexer.TokenType;
import java.util.*;
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Program parse() {
        List<Stmt> statements = new ArrayList<>();
        
        while (!isAtEnd()) {
            statements.add(statement());
        }
        
        return new Program(statements);
    }

    // Statement parsing
    private Stmt statement() {
        if (match(TokenType.LET)) {
            return varDecl();
        }
        if (match(TokenType.PRINT)) {
            return printStmt();
        }
        if (match(TokenType.IF)) {
            return ifStmt();
        }
        if (match(TokenType.WHILE)) {
            return whileStmt();
        }
        return expressionStmt();
    }

    // Variable declaration
    private Stmt varDecl() {
        Token varType = previous();
        Token name = consume(TokenType.IDENT, "Expect variable name.");
        consume(TokenType.ASSIGN, "Expect '=' after variable name.");
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new VarDecl(varType.lexeme, name.lexeme, value);
    }

    // Print statement
    private Stmt printStmt() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after print statement.");
        return new PrintStmt(value);
    }

    // If statement
    private Stmt ifStmt() {
        consume(TokenType.LPAREN, "Expect '(' after if.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Expect ')' after condition.");
        List<Stmt> thenBranch = block();
        List<Stmt> elseBranch = new ArrayList<>();
        
        if (match(TokenType.ELSE)) {
            elseBranch = block();
        }

        return new IfStmt(condition, thenBranch, elseBranch);
    }

    // While statement
    private Stmt whileStmt() {
        consume(TokenType.LPAREN, "Expect '(' after while.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Expect ')' after condition.");
        List<Stmt> body = block();
        return new WhileStmt(condition, body);
    }

    // Expression statement (an expression followed by a semicolon)
    private Stmt expressionStmt() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new PrintStmt(expr); // Change to appropriate statement type
    }

    // Block of statements
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        consume(TokenType.LBRACE, "Expect '{' before block.");
        
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(statement());
        }

        consume(TokenType.RBRACE, "Expect '}' after block.");
        return statements;
    }

    // Expression parsing
    private Expr expression() {
        return equality(); // Start with equality operator
    }

    // Equality ( ==, !=)
    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.EQ, TokenType.NEQ)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new BinaryExpr(expr, operator.lexeme, right);
        }

        return expr;
    }

    // Comparison ( >, <, >=, <=)
    private Expr comparison() {
        Expr expr = term();

        while (match(TokenType.GT, TokenType.LT, TokenType.GTE, TokenType.LTE)) {
            Token operator = previous();
            Expr right = term();
            expr = new BinaryExpr(expr, operator.lexeme, right);
        }

        return expr;
    }

    // Term ( +, -)
    private Expr term() {
        Expr expr = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new BinaryExpr(expr, operator.lexeme, right);
        }

        return expr;
    }

    // Factor ( *, /)
    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.MULT, TokenType.DIV)) {
            Token operator = previous();
            Expr right = unary();
            expr = new BinaryExpr(expr, operator.lexeme, right);
        }

        return expr;
    }

    // Unary ( !, -)
    private Expr unary() {
        if (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = unary();
            return new BinaryExpr(null, operator.lexeme, right); // Example of unary application
        }

        return primary();
    }

    // Primary ( literals, variables, parentheses, etc.)
    private Expr primary() {
        if (match(TokenType.NUMBER)) {
            return new Literal(Double.parseDouble(previous().lexeme));
        }
        if (match(TokenType.IDENT)) {
            return new Identifier(previous().lexeme);
        }
        if (match(TokenType.LPAREN)) {
            Expr expr = expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return expr;
        }

        throw new ParseException("Expect expression.");
    }

    // Helper methods
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new ParseException(message);
    }

}
