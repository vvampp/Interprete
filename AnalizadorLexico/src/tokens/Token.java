package tokens;

public class Token {
    private TipoToken tipo;
    private String lexema;
    private Object literal;

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
}
