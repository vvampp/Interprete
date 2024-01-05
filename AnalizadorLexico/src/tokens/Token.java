package tokens;

public class Token {
    public TipoToken tipo;
    public String lexema;
    public Object literal;

    public Token(){
        this.literal = null;
    }

    public Token(TipoToken tipo, String lexema) {
        this();
        this.tipo = tipo;
        this.lexema = lexema;
    }

    public Token(TipoToken tipo, String lexema, Object literal) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.literal = literal;
    }

    public String toString() {
        return "<" + this.tipo + " " + this.lexema + " " + this.literal + ">";
    }

    public TipoToken getTipo() {
        return this.tipo;
    }

    public String getLexema() {
        return this.lexema;
    }
}
