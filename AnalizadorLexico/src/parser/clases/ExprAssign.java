package parser.clases;
import tokens.*;

public class ExprAssign extends Expression {
    final Token name;
    final Expression value;

    public ExprAssign(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }
}
