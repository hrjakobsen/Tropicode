/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     Tropicode is a Java bytecode analyser used to verify object protocols.
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

import Checker.Exceptions.CheckerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TypestateLexer {
    private String inputString;

    public enum TokenType {
        BRACKET_OPEN("\\{"),
        BRACKET_CLOSE("\\}"),
        CARET_OPEN("<"),
        CARET_CLOSE(">"),
        SEMICOLON(";"),
        COLON(":"),
        END("end"),
        REC("rec"),
        DOT("\\."),
        IDENTIFIER("[a-zA-Z_$][a-zA-Z_$0-9]*");

        private final Pattern pattern;
        public String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        TokenType(String pattern) {
            this.pattern = Pattern.compile("^" +pattern);
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
            throw new CheckerException("Typestate could not be parsed. Lexer finished with this remaining: " + inputString);
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

}
