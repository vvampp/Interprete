package parser.clases;

import semantic.Tabla;

public class StmtPrint extends Statement {
    final Expression expression;

    public StmtPrint(Expression expression) {
        this.expression = expression;
    }

    public final Expression getExpression(){
        return this.expression;
    }

}
