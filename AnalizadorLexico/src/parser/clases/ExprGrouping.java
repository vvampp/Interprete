package parser.clases;

public class ExprGrouping extends Expression {
    final Expression expression;

    public ExprGrouping(Expression expression) {
        this.expression = expression;
    }
}
