package parser.clases;

import parser.clases.Expression;
import tokens.Token;

public class ExprVariable extends Expression {
    public final Token name;

    public ExprVariable(Token name) {
        this.name = name;
    }

    public Token getName(){
        return name;
    }
}