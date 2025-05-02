package com.jolt.JoltParser;

import java.util.ArrayList;
import java.util.List;

import com.jolt.JoltAST.ArrayAccess;
import com.jolt.JoltAST.ArrayLiteral;
import com.jolt.JoltAST.BinaryExpr;
import com.jolt.JoltAST.CallExpr;
import com.jolt.JoltAST.Expr;
import com.jolt.JoltAST.Identifier;
import com.jolt.JoltAST.IfStmt;
import com.jolt.JoltAST.Literal;
import com.jolt.JoltAST.PrintStmt;
import com.jolt.JoltAST.Program;
import com.jolt.JoltAST.Stmt;
import com.jolt.JoltAST.TensorLiteral;
import com.jolt.JoltAST.VarDecl;
import com.jolt.JoltAST.WhileStmt;
import com.jolt.JoltLexer.Token;
import com.jolt.JoltLexer.TokenType;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        for (int i = 0; i<tokens.size(); i++) {
            System.out.println("[DEBUG] tokens: " + tokens.get(i));
        }
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
        if (match(TokenType.LET, TokenType.INT, TokenType.FLOAT, TokenType.ARRAY)) {
            return varDecl();
        }
        if (match(TokenType.TENSOR)) {
            return TensorDecl();
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

        return parsePostfix(primary());
    }

    // Primary ( literals, variables, parentheses, etc.)
    private Expr primary() {
        if (match(TokenType.LBRACKET)) {
            return parseTensorOrArray();
        }

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

    private Expr parsePostfix(Expr expr) {
        while (true) {
            if (match(TokenType.LBRACKET)) {
                Expr index = expression();
                consume(TokenType.RBRACKET, "Expect ']' after index.");
                expr = new ArrayAccess(expr, index);
            } else if (match(TokenType.LPAREN)) {
                List<Expr> args = new ArrayList<>();
                if (!check(TokenType.RPAREN)) {
                    do {
                        args.add(expression());
                    } while (match(TokenType.COMMA));
                }
                consume(TokenType.RPAREN, "Expect ')' after arguments.");
                expr = new CallExpr(expr, args);
            } else {
                break;
            }
        }
        return expr;
    }
    private Expr parseTensor() {
        return parseNestedArray();
    }
    private Stmt TensorDecl(){
        Token name = consume(TokenType.IDENT, "Expect variable name.");
        consume(TokenType.ASSIGN, "Expect '=' after variable name.");
        Expr value = expression(); // should evaluate to a TensorLiteral
        consume(TokenType.SEMICOLON, "Expect ';' after tensor declaration.");
        return new VarDecl("tensor", name.lexeme, value);
    }
    private Expr parseNestedArray() {
        List<Object> elements = new ArrayList<>();

        do {
            if (check(TokenType.LBRACKET)) {
                consume(TokenType.LBRACKET, "Expect '[' in nested array.");
                Expr nested = parseNestedArray();
                consume(TokenType.RBRACKET, "Expect ']' to close nested array.");
                elements.add(nested);
            } else {
                elements.add(expression());
            }
        } while (match(TokenType.COMMA));

        if (elements.stream().allMatch(e -> e instanceof Expr)) {
            @SuppressWarnings("unchecked")
            List<Expr> flat = (List<Expr>) (List<?>) elements;
            return new TensorLiteral(flat); // 1D array
        }

        @SuppressWarnings("unchecked")
        List<Expr> wrapped = elements.stream().map(e -> {
                if (e instanceof Expr expr) return expr;
                throw new ParseException("Invalid element in tensor.");
            }).toList();

        return new TensorLiteral(wrapped);
    }
    private Expr parseArray() {
        List<Expr> elements = new ArrayList<>();

        if (!check(TokenType.RBRACKET)) {
            do { 
                elements.add(expression());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RBRACKET, "Expect ']' after array elements.");

        return new ArrayLiteral(elements);
    }
    private List<Expr> parseArrayElements() {
        List<Expr> elements = new ArrayList<>();
        if (!check(TokenType.RBRACKET)) {
            do {
                elements.add(expression());
            } while (match(TokenType.COMMA));
        }
        return elements;
    }
    
    private Expr parseTensorOrArray() {
        List<Expr> elements = parseArrayElements();

        if (check(TokenType.COMMA) && lookAheadIsLBracket()) {
            List<List<Expr>> rows = new ArrayList<>();
            rows.add(elements);

            while (match(TokenType.COMMA)) {
                consume(TokenType.LBRACKET, "Expect '[' to start new tensor row.");
                List<Expr> row = parseArrayElements();
                rows.add(row);
                consume(TokenType.RBRACKET, "Expect ']' to close tensor row.");
            }
            consume(TokenType.RBRACKET, "Expect ']' to close tensor.");
            return new TensorLiteral(rows);
        }
        consume(TokenType.RBRACKET, "Expect ']' to close array.");
        return new ArrayLiteral(elements);
    }

    private boolean lookAheadIsLBracket() {
        if (current +1 >= tokens.size()) return false;
        return tokens.get(current+1).type == TokenType.LBRACKET;
    }
}
