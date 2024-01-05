package scanner;
import java.util.ArrayList;
import java.util.HashMap;
import tokens.*;
import java.util.List;
import java.util.Map;
import parser.Interprete;

public class Scanner {
    // Mapa de palabras reservadas asociada a la clase Scanner
    private static final Map<String, TipoToken> palabrasReservadas;

    // Bloques de codigo estatico, se ej ecutan al cargar en memoria la clase
    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);
        palabrasReservadas.put("EOF",  TipoToken.EOF);

    }

    private String source;
    private final List<Token> tokens;

    // Estado del automata
    private int estado;

    public Scanner(){
        this.estado = 0;
        this.tokens = new ArrayList<>();
    }
    public Scanner(String source){
        this();
        this.source = source + " ";
    }
    // Metodo que escanea una linea de codigo o un archivo
    // y retorna una lista de tokens
    public List<Token> scan(){
        String lexema = "";
        char c;
        int numLinea = 1;
        for(int i = 0; i < this.source.length(); i++){
            c = this.source.charAt(i);


            switch (this.estado){
                // Del estado 0 al 12, analizamos los simbolos especiales
                // los estados 2,3,5,6,8,9,11,1 son finales, por lo que no se
                // agregan al switch
                case 0:
                    if(c == '>'){
                        this.estado = 1;
                        lexema += c;
                    }
                    else if(c == '<'){
                        this.estado = 4;
                        lexema += c;
                    }
                    else if(c == '='){
                        this.estado = 7;
                        lexema += c;
                    }
                    else if(c == '!'){
                        this.estado = 10;
                        lexema += c;
                    }
                    else if(Character.isLetter(c)){
                        this.estado = 13;
                        lexema += c;
                    }
                    else if(Character.isDigit(c)){
                        this.estado = 15;
                        lexema += c;
                    }
                    else if(c == '"'){
                        this.estado = 24;
                        lexema += c;
                    }
                    else if(c == '/'){
                        this.estado = 26;
                        lexema += c;
                    }
                    else if(c == '+'){
                        this.estado = 33;
                        lexema += c;
                    }
                    else if(c == '-'){
                        this.estado = 34;
                        lexema += c;
                    }
                    else if(c == '*'){
                        this.estado = 35;
                        lexema += c;
                    }
                    else if(c == '{'){
                        this.estado = 36;
                        lexema += c;
                    }
                    else if(c == '}'){
                        this.estado = 37;
                        lexema += c;
                    }
                    else if(c == '('){
                        this.estado = 38;
                        lexema += c;
                    }
                    else if(c == ')'){
                        this.estado = 39;
                        lexema += c;
                    }
                    else if(c == ','){
                        this.estado = 40;
                        lexema += c;
                    }
                    else if(c == '.'){
                        this.estado = 41;
                        lexema += c;
                    }
                    else if(c == ';'){
                        this.estado = 42;
                        lexema += c;
                    }
                    else if(c=='\n'){
                        numLinea++;
                    }
                    else if(!(c == ' ' || c=='\t')) {
                        if (c == 13) {
                            this.estado = 0;
                            lexema = "";
                        } else {
                            Interprete.error(numLinea, "Caracter introducido no valido", String.valueOf(c));

                        }
                    }
                    break;

                case 1:
                    if(c == '='){
                        lexema += c;
                        this.ingresarToken(TipoToken.GREATER_EQUAL, lexema);
                        // no se decrementa i debido a que sabemos cual fue
                        // el caracter que llego, y no se debe volver a leer
                        // ya que se agrega al lexema
                    }
                    else{
                        this.ingresarToken(TipoToken.GREATER, lexema);
                        i--;
                    }

                    this.estado = 0;
                    lexema = "";
                    break;

                case 4:
                    if(c == '='){
                        lexema += c;
                        this.ingresarToken(TipoToken.LESS_EQUAL, lexema);
                    }
                    else{
                        this.ingresarToken(TipoToken.LESS, lexema);
                        i--;
                    }
                    this.estado = 0;
                    lexema = "";

                    break;

                case 7:
                    if(c == '='){
                        lexema += c;
                        this.ingresarToken(TipoToken.EQUAL_EQUAL, lexema);
                    }
                    else{
                        this.ingresarToken(TipoToken.EQUAL, lexema);
                        i--;
                    }
                    this.estado = 0;
                    lexema = "";

                    break;

                case 10:
                    if(c == '='){
                        lexema += c;
                        this.ingresarToken(TipoToken.BANG_EQUAL, lexema);
                    }
                    else{
                        this.ingresarToken(TipoToken.BANG, lexema);
                        i--;
                    }
                    this.estado = 0;
                    lexema = "";

                    break;

                // Los estados 13 y 14 analizan los identificadores y las
                // palabras reservadas, el estado 14 es final
                case 13:
                    if(Character.isLetterOrDigit(c)){
                        this.estado = 13;
                        lexema += c;
                    }
                    else{
                        // Se verifica si el lexema es una palabra reservada
                        TipoToken tt = palabrasReservadas.get(lexema);

                        // Si no es una palabra reservada, entonces es un
                        // identificador
                        if(tt == null){
                            this.ingresarToken(TipoToken.IDENTIFIER, lexema);
                        }
                        else{
                            this.ingresarToken(tt, lexema);
                        }

                        this.estado = 0;
                        lexema = "";
                        i--;

                    }
                    break;


                // Los estados del 15 al 21 analizan los numeros
                // los estados 21,22,23 son finales
                case 15:
                    if(Character.isDigit(c)){
                        this.estado = 15;
                        lexema += c;
                    }
                    else if(c == '.'){
                        this.estado = 16;
                        lexema += c;
                    }
                    else if(c == 'E'){
                        this.estado = 18;
                        lexema += c;
                    }
                    else{
                        this.ingresarToken(lexema, Integer.valueOf(lexema));
                        this.estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;

                case 16:
                    if(Character.isDigit(c)){
                        this.estado = 17;
                        lexema += c;
                    }
                    // En este punto tenemos un numero de forma x.
                    // por lo que si recibimos cualquier otro caracter
                    // es un error
                    else{
                        Interprete.error(numLinea, "Se esperaba un digito", lexema);
                        return this.tokens;
                    }

                    break;

                case 17:
                    if(Character.isDigit(c)){
                        this.estado = 17;
                        lexema += c;
                    }
                    else if(c == 'E' || c == 'e'){
                        this.estado = 18;
                        lexema += c;
                    }
                    else{
                        this.ingresarToken(lexema, Double.valueOf(lexema));
                        this.estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;

                case 18:
                    if(Character.isDigit(c)){
                        this.estado = 20;
                        lexema += c;
                    }
                    else if(c == '+' || c == '-'){
                        this.estado = 19;
                        lexema += c;
                    }
                    else{
                        Interprete.error(numLinea, "Se esperaba un digito", lexema);
                        return this.tokens;
                    }
                    break;

                case 19:
                    if(Character.isDigit(c)){
                        this.estado = 20;
                        lexema += c;
                    }
                    else{
                        Interprete.error(numLinea, "Se esperaba un digito", lexema);
                        return this.tokens;
                    }
                    break;

                case 20:
                    if(Character.isDigit(c)){
                        this.estado = 20;
                        lexema += c;
                    }
                    else{
                        this.ingresarToken(lexema, Double.valueOf(lexema));
                        this.estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;

                // Los estados del 24 al 25 analizan los strings
                case 24:
                    if(c == '"'){
                        lexema += c;
                        this.ingresarToken(TipoToken.STRING,lexema, lexema.substring(1, lexema.length() - 1));
                        this.estado = 0;
                        lexema = "";
                    }
                    else if(c == '\n'){
                        Interprete.error(numLinea, "No se cerro la cadena. Se esperaba \"", "String");
                        return this.tokens;
                    }
                   else{
                       //this.estado = 24;
                       lexema += c;
                    }
                    break;


                // Los estados del 26 al 32 hacen analisis de comentarios
                // pero no retornan ningun token, los estados 29,31,32 son finales
                case 26:
                    if(c == '*'){
                        this.estado = 27;
                        // ya no es necesario guardar el caracter
                        // debido a que en este caso, ya es la inicializacion
                        // del comentario

                    }
                    else if(c == '/'){
                        this.estado = 30;
                    }
                    else{
                        // si no es un comentario, entonces es una division
                        this.ingresarToken(TipoToken.SLASH, lexema);
                        i--;
                        this.estado = 0;
                        lexema = "";
                    }
                    break;

                case 27:
                    if(c == '*'){
                        this.estado = 28;
                    }
                    /*else{
                        this.estado = 27;
                    }*/
                    break;

                case 28:
                    if(c == '/'){
                        // El comentario se ha cerrado
                        this.estado = 0;
                        lexema = "";
                    }
                    /*else if(c == '*'){
                        this.estado = 28;
                    }*/
                    else{
                        this.estado = 27;
                    }
                    break;

                case 30:
                    while(c != '\n'){
                        i++;
                        c = this.source.charAt(i);
                    }
                    numLinea++;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 33: //+
                    this.ingresarToken(TipoToken.PLUS, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 34://-
                    this.ingresarToken(TipoToken.MINUS, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 35://*
                    this.ingresarToken(TipoToken.STAR, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 36://{
                    this.ingresarToken(TipoToken.LEFT_BRACE, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 37://}
                    this.ingresarToken(TipoToken.RIGHT_BRACE, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 38://(
                    this.ingresarToken(TipoToken.LEFT_PAREN, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 39://)
                    this.ingresarToken(TipoToken.RIGHT_PAREN, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 40://,
                    this.ingresarToken(TipoToken.COMMA, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 41://.
                    this.ingresarToken(TipoToken.DOT, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
                case 42://;
                    this.ingresarToken(TipoToken.SEMICOLON, lexema);
                    i--;
                    this.estado = 0;
                    lexema = "";
                    break;
            }
        }

        // Si el automata quedo esperando el cierre de un comentario
        // pero la entrada termino, significa error
        if(this.estado == 27 || this.estado == 28){
            Interprete.error(numLinea, "No se cerro el comentario [Se esperaba */].", "Comentario[/*]");
        }
        this.ingresarToken(TipoToken.EOF,"EOF");

        return this.tokens;
    }

    private void ingresarToken(TipoToken tipo, String lexema){
        Token t = new Token(tipo, lexema);
        this.tokens.add(t);
    }
    private void ingresarToken(String lexema, Object literal){
        Token t = new Token(TipoToken.NUMBER, lexema, literal);
        this.tokens.add(t);
    }

    private void ingresarToken(TipoToken tipo,String lexema, Object literal){
        Token t = new Token(TipoToken.STRING, lexema, literal);
        this.tokens.add(t);
    }

}
