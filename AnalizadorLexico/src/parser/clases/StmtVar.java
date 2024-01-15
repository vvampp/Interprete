package parser.clases;
import tokens.*;
public class StmtVar extends Statement {
    final Token name;
    final Expression initializer;

    public StmtVar(Token name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    public final Token getName() {
        return name;
    }

    public final Expression getInitializer() {
        return initializer;
    }
}
