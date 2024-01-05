package parser;

import java.util.List;
import tokens.*;

public class ASDR implements Parser{
    public int i; // Indice de la lista de tokens
    public Token preanalisis; // Token actual
    public final List<Token> tokens; // Lista de tokens tomados del scanner
    public boolean hayErrores; // Bandera de errores


    // Constructor
    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        this.i = 0;
        this.preanalisis = tokens.get(this.i);
        this.hayErrores = false;
    }

    // Metodo para inicar el analisis sintactico
    // y crear el arbol de sintaxis abstracta

    @Override
    public boolean parse(){
        //PROGRAM();

        // Si no hay errores y se llego al final del archivo
        if(!this.hayErrores){
            if (this.preanalisis.getTipo() == TipoToken.EOF) {
                System.out.println("Sin errores");
                return true;
            } else {
                System.out.println("Se encontraron errores");
            }
        }

        return false;
    }

    // Metodo para comparar el token actual con el token esperado
    void match(TipoToken tokenType) {
        if (this.preanalisis.getTipo() == tokenType) {
            i++;
            if (this.i < this.tokens.size()) {
                this.preanalisis = this.tokens.get(i);
            }
        } else {
            this.hayErrores=true;
            System.out.println("Error encontrado en la linea " + this.preanalisis.getLexema()+": Se esperaba " + tokenType + " pero se encontro " + this.preanalisis.getTipo());
        }
    }

    // Metodo para obtener el token anterior
    private Token previous(){
        return this.tokens.get(this.i - 1);
    }

    /*      GRAMATICA      */

    // PROGRAM -> DECLARATION
    public void PROGRAM(){
        switch (this.preanalisis.getTipo()){
            case FUN, VAR,
                    BANG, MINUS,
                    TRUE, FALSE,
                    NULL, NUMBER,
                    STRING, IDENTIFIER,
                    LEFT_PAREN, FOR,
                    IF, PRINT,
                    RETURN, WHILE, LEFT_BRACE:
                DECLARATION();
                break;

            default:
                break;
        }
    }

    /*      DECLARACIONES      */

    /*
    DECLARATION   -> FUN_DECL DECLARATION
                    -> VAR_DECL DECLARATION
                    -> STATEMENT DECLARATION
                    -> E
    */
    public void DECLARATION(){
        switch (this.preanalisis.getTipo()){
            case FUN:
                FUN_DECL();
                DECLARATION();
                break;

            case VAR:
                VAR_DECL();
                DECLARATION();
                break;

            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN, FOR, IF, PRINT, RETURN, WHILE, LEFT_BRACE:
                STATEMENT();
                DECLARATION();
                break;

            default:
                break;
        }
    }

    // FUN_DECL -> fun FUNCTION
    public void FUN_DECL(){
        if(this.preanalisis.tipo == TipoToken.FUN){
            match(TipoToken.FUN);
            //FUNCTION();
        }else{
            this.hayErrores = true;
            System.out.println("Error en la lexema "+ this.preanalisis.lexema + ": Se esperaba un 'fun'");
        }
    }

    // VAR_DECL -> var id VAR_INIT ;
    public void VAR_DECL(){
        if(preanalisis.tipo == TipoToken.VAR){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            Token name = previous();
            if(preanalisis.tipo == TipoToken.EQUAL){
                VAR_INIT();
            }
            match(TipoToken.SEMICOLON);
        }else{
            this.hayErrores=true;
            System.out.println("Error en la lexema "+ preanalisis.lexema + ": Se esperaba un 'var'");
        }
    }

    /*VAR_INIT -> = EXPRESSION
                -> E
    */
    public void VAR_INIT(){
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            //EXPRESSION();
        }
    }

    /*      SENTENCIAS      */

    /*
    STATEMENT -> EXPR_STMT
            -> FOR_STMT
            -> IF_STMT
            -> PRINT_STMT
            -> RETURN_STMT
            -> WHILE_STMT
            -> BLOCK
    */

    public void STATEMENT(){
        switch (this.preanalisis.getTipo()){
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN:
                EXPR_STMT();

            case FOR:
                FOR_STMT();

            case IF:
                 IF_STMT();

            case PRINT:
                PRINT_STMT();

            case RETURN:
                RETURN_STMT();

            case WHILE:
                WHILE_STMT();

            case LEFT_BRACE:
                BLOCK();

            default:
                this.hayErrores = true;
                System.out.println("Error en la lexema "+ this.preanalisis.getLexema());
        }
    }

    // EXPR_STMT -> EXPRESSION ;
    public void EXPR_STMT(){
        //EXPRESSION();
        match(TipoToken.SEMICOLON);
    }

    // FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    public void FOR_STMT(){
        if(this.preanalisis.getTipo() == TipoToken.FOR){
            match(TipoToken.FOR);
            match(TipoToken.LEFT_PAREN);
            FOR_STMT_1();
            FOR_STMT_2();
            FOR_STMT_3();
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
        }else{
            this.hayErrores = true;
            System.out.println("Error en el primer elemento del for de la lexema "+this.preanalisis.getLexema());
        }
    }

    /*FOR_STMT_1 -> VAR_DECL
             -> EXPR_STMT
             -> ;
    */
    public void FOR_STMT_1() {
        switch (this.preanalisis.getTipo()) {
            case VAR:
                VAR_DECL();
                break;

            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN:
                EXPR_STMT();
                break;

            case SEMICOLON:
                match(TipoToken.SEMICOLON);
                break;

            default:
                this.hayErrores = true;
                System.out.println("Error en el primer elemento del for de la lexema " + this.preanalisis.lexema);
                break;
        }
    }

    /*
    FOR_STMT_2 -> EXPRESSION;
               -> ;
    */
    public void FOR_STMT_2(){
        switch (this.preanalisis.getTipo()){
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN:
                //EXPRESSION();
                match(TipoToken.SEMICOLON);

            case SEMICOLON:
                match(TipoToken.SEMICOLON);

            default:
                this.hayErrores = true;
                System.out.println("Error en el segundo elemento del for de la lexema "+ this.preanalisis.lexema);
        }
    }


    /*
    FOR_STMT_3 -> EXPRESSION
                 -> E
     */
    public void FOR_STMT_3(){
        if(this.preanalisis.tipo == TipoToken.BANG ||
                this.preanalisis.tipo == TipoToken.MINUS ||
                this.preanalisis.tipo == TipoToken.TRUE ||
                this.preanalisis.tipo == TipoToken.FALSE ||
                this.preanalisis.tipo == TipoToken.NULL ||
                this.preanalisis.tipo == TipoToken.NUMBER ||
                this.preanalisis.tipo == TipoToken.STRING ||
                this.preanalisis.tipo == TipoToken.IDENTIFIER ||
                this.preanalisis.tipo == TipoToken.LEFT_PAREN){
            //EXPRESSION();
        }
    }

    // IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    public void IF_STMT(){
        if(this.preanalisis.tipo==TipoToken.IF){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            //EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
            if(preanalisis.tipo == TipoToken.ELSE) {
                ELSE_STATEMENT();
            }
        }else{
            this.hayErrores=true;
            System.out.println("Error en la lexema "+ preanalisis.lexema + ": Se esperaba un 'if'");
        }
    }

    /*
    ELSE_STATEMENT -> else STATEMENT
                     -> E
     */
    public void ELSE_STATEMENT(){
        if(this.preanalisis.tipo == TipoToken.ELSE){
            match(TipoToken.ELSE);
            STATEMENT();
        }
    }

    public void PRINT_STMT(){
        if(this.preanalisis.tipo == TipoToken.PRINT){
            match(TipoToken.PRINT);
            //EXPRESSION();
            match(TipoToken.SEMICOLON);
        }else{
            this.hayErrores=true;
            System.out.println("Error en la lexema "+ this.preanalisis.lexema + ": Se esperaba un 'print'");
        }
    }

    // RETURN_STMT -> return RETURN_EXP_OPC ;
    public void RETURN_STMT(){
        if(this.preanalisis.tipo == TipoToken.RETURN){
            match(TipoToken.RETURN);
            if(this.preanalisis.tipo == TipoToken.BANG ||
                    this.preanalisis.tipo == TipoToken.MINUS ||
                    this.preanalisis.tipo == TipoToken.TRUE ||
                    this.preanalisis.tipo == TipoToken.FALSE ||
                    this.preanalisis.tipo == TipoToken.NULL ||
                    this.preanalisis.tipo == TipoToken.NUMBER ||
                    this.preanalisis.tipo == TipoToken.STRING ||
                    this.preanalisis.tipo == TipoToken.IDENTIFIER ||
                    this.preanalisis.tipo == TipoToken.LEFT_PAREN){
                RETURN_EXP_OPC();
            }
            match(TipoToken.SEMICOLON);
        }else{
            this.hayErrores=true;
            System.out.println("Error en la lexema "+ preanalisis.lexema + ": Se esperaba un 'return'");
        }
    }

    public void RETURN_EXP_OPC(){
        if(this.preanalisis.tipo == TipoToken.BANG ||
                this.preanalisis.tipo == TipoToken.MINUS ||
                this.preanalisis.tipo == TipoToken.TRUE ||
                this.preanalisis.tipo == TipoToken.FALSE ||
                this.preanalisis.tipo == TipoToken.NULL ||
                this.preanalisis.tipo == TipoToken.NUMBER ||
                this.preanalisis.tipo == TipoToken.STRING ||
                this.preanalisis.tipo == TipoToken.IDENTIFIER ||
                this.preanalisis.tipo == TipoToken.LEFT_PAREN){
            //EXPRESSION();
        }
    }

    // WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    public void WHILE_STMT(){
        if(this.preanalisis.tipo == TipoToken.WHILE){
            match(TipoToken.WHILE);
            match(TipoToken.LEFT_PAREN);
            // EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
        }else{
            this.hayErrores = true;
            System.out.println("Error en la lexema "+ preanalisis.lexema + ": Se esperaba un 'while'");
        }
    }

    // BLOCK -> { DECLARATION }
    public void BLOCK(){
        if(this.preanalisis.tipo == TipoToken.LEFT_BRACE){
            match(TipoToken.LEFT_BRACE);
            DECLARATION();
            match(TipoToken.RIGHT_BRACE);
        }else{
            this.hayErrores = true;
            System.out.println("Error en la lexema "+ this.preanalisis.lexema + ": Se esperaba un 'LEFT_BRACE'");
        }
    }

    /*      EXPRESIONES      */


}
