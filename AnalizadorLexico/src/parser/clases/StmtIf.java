package parser.clases;
public class StmtIf extends Statement {
    final Expression condition;
    final Statement thenBranch;
    final Statement elseBranch;

    public StmtIf(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public final Expression getCondition() {
        return condition;
    }

    public final Statement getThenBranch() {
        return thenBranch;
    }

    public final Statement getElseBranch() {
        return elseBranch;
    }
}
