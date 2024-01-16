package parser.clases;
import tokens.*;

public class ExprUnary extends Expression{
    final Token operator;
    final Expression right;

    public ExprUnary(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    public Expression getOperand(){
        return right;
    }
}
