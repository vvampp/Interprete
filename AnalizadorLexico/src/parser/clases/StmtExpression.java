package parser.clases;

import parser.clases.Expression;
import parser.clases.Statement;

public class StmtExpression extends Statement {
    final Expression expression;

    public StmtExpression(Expression expression) {
        this.expression = expression;
    }
    public final Expression getExpression() {
        return expression;
    }
}
