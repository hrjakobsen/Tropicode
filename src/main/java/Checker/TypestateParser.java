/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package Checker;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;


/*
* Parsed grammar for typestates
* u ::= { m_i ; w_i } | end | rec X. u | X
* w ::= < l_i : u_i > | u
* */
public class TypestateParser {

    public TypestateParser() {
    }

    public Typestate parse(Stack<TypestateLexer.Token> tokens) {
        return parseU(tokens);
    }

    private Typestate parseU(Stack<TypestateLexer.Token> tokens) {
        TypestateLexer.Token next = tokens.peek();
        return switch (next.getType()) {
            case BRACKET_OPEN -> parseBranch(tokens);
            case END -> parseEnd(tokens);
            case REC -> parseRec(tokens);
            case IDENTIFIER -> parseVariable(tokens);
            default -> throw new IllegalArgumentException("Invalid next token " + next.getText());
        };
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

    private void ensureToken(TypestateLexer.TokenType expected, TypestateLexer.Token actual) {
        if (actual.getType() != expected) {
            throw new IllegalArgumentException("Invalid token encountered while parsing " + expected + " token. Got " + actual.getType() + " with text " + actual.getText() );
        }
    }
}
