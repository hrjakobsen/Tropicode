/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.checker.Typestate.BooleanChoice;
import org.tropicode.checker.checker.Typestate.ExceptionPath;
import org.tropicode.checker.checker.TypestateLexer.Token;
import org.tropicode.checker.checker.TypestateLexer.TokenType;
import org.tropicode.checker.checker.exceptions.CheckerException;


/*
 * Parsed grammar for typestates
 * u ::= (u_0 | ... | u_i).u { m_i ; w_i } | end | rec X. u | X
 * w ::= < l_i : u_i > | [u_1, u_2] | u
 * */
@Log4j2
public class TypestateParser {

    public TypestateParser() {
    }

    public Typestate parse(Stack<TypestateLexer.Token> tokens) {
        Typestate parsed = parseU(tokens);
        return parsed;
    }

    private Typestate parseU(Stack<TypestateLexer.Token> tokens) {
        TypestateLexer.Token next = tokens.peek();
        return switch (next.getType()) {
            case BRACKET_OPEN -> parseBranch(tokens);
            case TRY -> parseTry(tokens);
            case PAREN_OPEN -> parseParallel(tokens);
            case END -> parseEnd(tokens);
            case REC -> parseRec(tokens);
            case IDENTIFIER -> parseVariable(tokens);
            default -> throw new CheckerException("Invalid next token " + next.getText());
        };
    }

    private Typestate parseTry(Stack<Token> tokens) {
        TypestateLexer.Token tryToken = tokens.pop();
        ensureToken(TypestateLexer.TokenType.TRY, tryToken);

        Typestate intended = parseU(tokens);

        TypestateLexer.Token except = tokens.pop();
        ensureToken(TokenType.EXCEPT, except);

        Typestate continuation = parseU(tokens);

        return new ExceptionPath(intended, continuation);
    }

    private Typestate parseParallel(Stack<TypestateLexer.Token> tokens) {
        List<Typestate> locals = new ArrayList<>();

        TypestateLexer.Token parOpen = tokens.pop();
        ensureToken(TypestateLexer.TokenType.PAREN_OPEN, parOpen);

        locals.add(parseU(tokens));
        TypestateLexer.TokenType next = tokens.peek().getType();
        if (next == TypestateLexer.TokenType.PIPE) {
            do {
                tokens.pop();
                locals.add(parseU(tokens));
                next = tokens.peek().getType();
            } while (next == TypestateLexer.TokenType.PIPE);
        }

        TypestateLexer.Token parClose = tokens.pop();
        ensureToken(TypestateLexer.TokenType.PAREN_CLOSE, parClose);

        if (tokens.isEmpty() || tokens.peek().getType() != TokenType.DOT) {
            return new Typestate.Parallel(locals, Typestate.END);
        }

        TypestateLexer.Token dot = tokens.pop();
        ensureToken(TypestateLexer.TokenType.DOT, dot);

        Typestate continuation = parseU(tokens);
        return new Typestate.Parallel(locals, continuation);
    }

    private Typestate parseVariable(Stack<TypestateLexer.Token> tokens) {
        TypestateLexer.Token identifier = tokens.pop();
        ensureToken(TypestateLexer.TokenType.IDENTIFIER, identifier);
        return new Typestate.Variable(identifier.getText());
    }

    private Typestate parseRec(Stack<TypestateLexer.Token> tokens) {
        TypestateLexer.Token rec = tokens.pop();
        ensureToken(TypestateLexer.TokenType.REC, rec);

        TypestateLexer.Token identifier = tokens.pop();
        ensureToken(TypestateLexer.TokenType.IDENTIFIER, identifier);

        TypestateLexer.Token dot = tokens.pop();
        ensureToken(TypestateLexer.TokenType.DOT, dot);

        return new Typestate.Recursive(identifier.getText(), parseU(tokens));
    }

    private Typestate parseEnd(Stack<TypestateLexer.Token> tokens) {
        TypestateLexer.Token token = tokens.pop();
        ensureToken(TypestateLexer.TokenType.END, token);
        return Typestate.END;
    }

    private Typestate parseBranch(Stack<TypestateLexer.Token> tokens) {
        TypestateLexer.Token bracketOpen = tokens.pop();
        ensureToken(TypestateLexer.TokenType.BRACKET_OPEN, bracketOpen);

        HashMap<String, Typestate> branches = new HashMap<>();

        TypestateLexer.Token next = tokens.peek();
        while (next.getType() == TypestateLexer.TokenType.IDENTIFIER) {

            TypestateLexer.Token identifier = tokens.pop();
            ensureToken(TypestateLexer.TokenType.IDENTIFIER, identifier);

            TypestateLexer.Token semi = tokens.pop();
            ensureToken(TypestateLexer.TokenType.SEMICOLON, semi);

            Typestate t = parseW(tokens);
            branches.put(identifier.getText(), t);

            next = tokens.peek();
        }

        TypestateLexer.Token bracketClose = tokens.pop();
        ensureToken(TypestateLexer.TokenType.BRACKET_CLOSE, bracketClose);

        return new Typestate.Branch(branches);

    }

    private Typestate parseW(Stack<TypestateLexer.Token> tokens) {
        TypestateLexer.Token next = tokens.peek();
        return switch (next.getType()) {
            case CARET_OPEN -> parseChoice(tokens);
            case SQUARE_BRACKET_OPEN -> parseBooleanChoice(tokens);
            default -> parseU(tokens);
        };
    }

    private Typestate parseChoice(Stack<TypestateLexer.Token> tokens) {
        TypestateLexer.Token caretOpen = tokens.pop();
        ensureToken(TypestateLexer.TokenType.CARET_OPEN, caretOpen);

        HashMap<String, Typestate> choices = new HashMap<>();

        TypestateLexer.Token next = tokens.peek();
        while (next.getType() == TypestateLexer.TokenType.IDENTIFIER) {

            TypestateLexer.Token identifier = tokens.pop();
            ensureToken(TypestateLexer.TokenType.IDENTIFIER, identifier);

            TypestateLexer.Token colon = tokens.pop();
            ensureToken(TypestateLexer.TokenType.COLON, colon);

            Typestate t = parseU(tokens);
            choices.put(identifier.getText(), t);

            next = tokens.peek();
        }

        TypestateLexer.Token caretClose = tokens.pop();
        ensureToken(TypestateLexer.TokenType.CARET_CLOSE, caretClose);

        return new Typestate.Choice(choices);
    }

    private Typestate parseBooleanChoice(Stack<TypestateLexer.Token> tokens) {
        Token square_bracket_open = tokens.pop();
        ensureToken(TokenType.SQUARE_BRACKET_OPEN, square_bracket_open);

        Typestate u1 = parseU(tokens);

        Token comma = tokens.pop();
        ensureToken(TokenType.COMMA, comma);

        Typestate u2 = parseU(tokens);

        Token square_bracket_close = tokens.pop();
        ensureToken(TokenType.SQUARE_BRACKET_CLOSE, square_bracket_close);

        return new BooleanChoice(u1, u2);
    }

    private void ensureToken(TypestateLexer.TokenType expected, TypestateLexer.Token actual) {
        if (actual.getType() != expected) {
            throw new CheckerException(
                "Invalid token encountered while parsing " + expected + " token. Got " + actual.getType()
                    + " with text " + actual.getText());
        }
    }
}
