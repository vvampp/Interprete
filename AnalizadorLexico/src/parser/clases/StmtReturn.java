package parser.clases;

public class StmtReturn extends Statement {
    final Expression value;

    public StmtReturn(Expression value) {
        this.value = value;
    }

    public final Expression getExpression() {
        return value;
    }

}
