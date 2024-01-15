package parser.clases;

import semantic.Tabla;

public abstract class Expression {
    public abstract  Object resolver (Tabla tabla);
}
