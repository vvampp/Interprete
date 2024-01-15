package parser;
import parser.clases.Statement;

import java.util.List;

public interface Parser {
    List<Statement> parse();
}
