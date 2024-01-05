package parser.clases;
public class ExprLiteral extends Expression {
    final Object value;

    public ExprLiteral(Object value) {
        this.value = value;
    }
}
