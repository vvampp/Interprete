package parser.clases;

import tokens.Token;

import java.util.List;

public class StmtFunction extends Statement {
    final Token name;
    final List<Token> params;
    final StmtBlock body;

    public StmtFunction(Token name, List<Token> params, StmtBlock body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }
    public final Token getName() {
        return name;
    }

    public final List<Token> getParams() {
        return params;
    }

    public final List<Token> getParameters() {
        return params;
    }

    public final StmtBlock getBody() {
        return body;
    }
}
