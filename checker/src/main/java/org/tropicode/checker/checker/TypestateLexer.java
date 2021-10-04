/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.checker.exceptions.CheckerException;

@Log4j2
public class TypestateLexer {

    private String inputString;

    public TypestateLexer(String s) {
        inputString = s;
    }

    public Stack<Token> getTokens() {
        List<Token> tokens = new ArrayList<>();
        Token next = nextToken();
        while (next != null) {
            tokens.add(next);
            next = nextToken();
        }
        if (!inputString.trim().isEmpty()) {
            throw new CheckerException(
                    "Typestate could not be parsed. Lexer finished with this remaining: "
                            + inputString);
        }
        Stack<Token> tokenStack = new Stack<>();
        Collections.reverse(tokens);
        tokenStack.addAll(tokens);
        return tokenStack;
    }

    private Token nextToken() {
        inputString = inputString.trim();
        for (TokenType token : TokenType.values()) {
            int matchLength = token.getMatchLength(inputString);
            if (matchLength != -1) {
                // A match has been found
                String match = inputString.substring(0, matchLength);
                inputString = inputString.substring(matchLength);
                return new Token(token, match);
            }
        }
        return null;
    }

    public enum TokenType {
        BRACKET_OPEN("\\{"),
        BRACKET_CLOSE("\\}"),
        PAREN_OPEN("\\("),
        PAREN_CLOSE("\\)"),
        SQUARE_BRACKET_OPEN("\\["),
        SQUARE_BRACKET_CLOSE("\\]"),
        PIPE("\\|"),
        CARET_OPEN("<"),
        CARET_CLOSE(">"),
        SEMICOLON(";"),
        COLON(":"),
        END("end"),
        REC("rec"),
        TRY("try"),
        EXCEPT("except"),
        DOT("\\."),
        COMMA(","),
        IDENTIFIER("[a-zA-Z_$][a-zA-Z_$0-9]*");

        private final Pattern pattern;
        public String text;

        TokenType(String pattern) {
            this.pattern = Pattern.compile("^" + pattern);
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getMatchLength(String s) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return matcher.end();
            } else {
                return -1;
            }
        }
    }

    public static class Token {

        private final TokenType type;
        private final String text;

        public Token(TokenType type, String text) {
            this.type = type;
            this.text = text;
        }

        public TokenType getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return type + " [" + text + "]";
        }
    }
}
