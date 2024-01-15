package parser.clases;
import tokens.*;
public class ExprVariable extends Expression {
    public final Token name;

    public ExprVariable(Token name) {
        this.name = name;
    }
}