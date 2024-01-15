package parser.clases;

import parser.clases.Expression;
import parser.clases.Statement;
import semantic.Tabla;

public class StmtExpression extends Statement {
    final Expression expression;

    public StmtExpression(Expression expression) {
        this.expression = expression;
    }
    public final Expression getExpression() {
        return expression;
    }

    @Override
    public void ejecutar(Tabla tabla) {

    }
}
