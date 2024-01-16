package parser.clases;

import parser.clases.Expression;
import parser.clases.Statement;

public class StmtLoop extends Statement {
    final Expression condition;
    final Statement body;

    public StmtLoop(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    public final Expression getCondition() {
        return condition;
    }

    public final Statement getBody() {
        return body;
    }

}
