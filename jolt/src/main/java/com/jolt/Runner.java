package com.jolt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.jolt.JoltAST.BinaryExpr;
import com.jolt.JoltAST.Expr;
import com.jolt.JoltAST.Identifier;
import com.jolt.JoltAST.IfStmt;
import com.jolt.JoltAST.Literal;
import com.jolt.JoltAST.PrintStmt;
import com.jolt.JoltAST.Program;
import com.jolt.JoltAST.Stmt;
import com.jolt.JoltAST.VarDecl;
import com.jolt.JoltAST.WhileStmt;
import com.jolt.JoltInterpreter.Interpreter;
import com.jolt.JoltLexer.Lexer;
import com.jolt.JoltLexer.Token;
import com.jolt.JoltParser.Parser;

public class Runner {
    public static void main(String[] args) {
        if (args.length != 1) {
                System.out.println("Usage: java Main <filename.jolt>");
                return;
        }
        String filename = args[0];

        // Step 1: Read the contents of the file
        String input = readFile(filename);

        if (input == null) return;
        runFromString(input);
    }

    public static void runFromString(String input) {
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        Program program = parser.parse();
        
        System.out.println("Parsed program");
        printProgram(program);

        Interpreter interpreter = new Interpreter();
        interpreter.interpret(program);
    }
    private static String readFile(String filename) {
        try {
            return Files.readString(Path.of(filename));
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            e.printStackTrace();
            return null;
        }
    }
    
    // This method will print the program's statements in a readable format
    private static void printProgram(Program program) {
        for (Stmt stmt : program.getStatements()) {
            printStmt(stmt, 0);
        }
    }

    // Helper method to print statements with indentation
    private static void printStmt(Stmt stmt, int indent) {
        String indentStr = " ".repeat(indent); // Simple indentation for readability

        if (stmt instanceof VarDecl) {
            VarDecl varDecl = (VarDecl) stmt;
            System.out.println(indentStr + "VarDecl: " + varDecl.getVarType() + " " + varDecl.getName() + " = " + varDecl.getValue());
        } else if (stmt instanceof PrintStmt) {
            PrintStmt printStmt = (PrintStmt) stmt;
            System.out.println(indentStr + "PrintStmt: " + printExpr(printStmt.getExpr()));
        } else if (stmt instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) stmt;
            System.out.println(indentStr + "IfStmt:");
            printExpr(ifStmt.getCondition(), indent + 2);
            System.out.println(indentStr + "Then:");
            printStatements(ifStmt.getThenBranch(), indent + 2);
            if (!ifStmt.getElseBranch().isEmpty()) {
                System.out.println(indentStr + "Else:");
                printStatements(ifStmt.getElseBranch(), indent + 2);
            }
        } else if (stmt instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt) stmt;
            System.out.println(indentStr + "WhileStmt:");
            printExpr(whileStmt.getCondition(), indent + 2);
            printStatements(whileStmt.getBody(), indent + 2);
        } else {
            System.out.println(indentStr + "UnknownStmt: " + stmt.getClass().getSimpleName());
        }
    }

    private static void printStatements(List<Stmt> statements, int indent) {
        for (Stmt stmt : statements) {
            printStmt(stmt, indent);
        }
    }

    private static void printExpr(Expr expr, int indent) {
        String indentStr = " ".repeat(indent);
        if (expr instanceof Literal) {
            System.out.println(indentStr + "Literal: " + ((Literal) expr).getValue());
        } else if (expr instanceof Identifier) {
            System.out.println(indentStr + "Identifier: " + ((Identifier) expr).getName());
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expr;
            System.out.println(indentStr + "BinaryExpr: " + binaryExpr.getOperator());
            printExpr(binaryExpr.getLeft(), indent + 2);
            printExpr(binaryExpr.getRight(), indent + 2);
        } else {
            System.out.println(indentStr + "UnknownExpr: " + expr.getClass().getSimpleName());
        }
    }

    private static String printExpr(Expr expr) {
        if (expr instanceof Literal) {
            return "Literal(" + ((Literal) expr).getValue() + ")";
        } else if (expr instanceof Identifier) {
            return "Identifier(" + ((Identifier) expr).getName() + ")";
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expr;
            return "BinaryExpr(" + binaryExpr.getOperator() + ")";
        }
        return "UnknownExpr";
    }
}
