package parser.clases;

import tokens.Token;

public class ExprAssign extends Expression {
    final Token name;
    final Expression value;

    public ExprAssign(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }

    public Expression getValue(){
        return value;
    }

    public Expression getVariable(){
        return value;
    }

    public Token getName(){
        return name;
    }
}
