package parser;

import java.util.ArrayList;
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
        PROGRAM();

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
            FUNCTION();
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
                break;

            case FOR:
                FOR_STMT();
                break;

            case IF:
                 IF_STMT();
                 break;

            case PRINT:
                PRINT_STMT();
                break;

            case RETURN:
                RETURN_STMT();
                break;

            case WHILE:
                WHILE_STMT();
                break;

            case LEFT_BRACE:
                BLOCK();
                break;

            default:
                this.hayErrores = true;
                System.out.println("Error en la lexema "+ this.preanalisis.getLexema());
        }
    }

    // EXPR_STMT -> EXPRESSION ;
    public void EXPR_STMT(){
        EXPRESSION();
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
                EXPRESSION();
                match(TipoToken.SEMICOLON);
                break;

            case SEMICOLON:
                match(TipoToken.SEMICOLON);
                break;

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
            EXPRESSION();
        }
    }

    // IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    public void IF_STMT(){
        if(this.preanalisis.tipo==TipoToken.IF){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            EXPRESSION();
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
            EXPRESSION();
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
            EXPRESSION();
        }
    }

    // WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    public void WHILE_STMT(){
        if(this.preanalisis.tipo == TipoToken.WHILE){
            match(TipoToken.WHILE);
            match(TipoToken.LEFT_PAREN);
            EXPRESSION();
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


    //Expresiones

    // EXPRESSION -> ASSIGNMENT
    public void EXPRESSION(){
        ASSIGNMENT();
    }

    // ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
    public void ASSIGNMENT(){
        LOGIC_OR();
        if(preanalisis.tipo == TipoToken.EQUAL){
            ASSIGNMENT_OPC();
        }
    }

    /*ASSIGNMENT_OPC -> = EXPRESSION
                     -> E */
    public void ASSIGNMENT_OPC(){
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            EXPRESSION();
        }
    }

    // LOGIC_OR -> LOGIC_AND LOGIC_OR_2
    public void LOGIC_OR(){
        LOGIC_AND();
        if(preanalisis.tipo == TipoToken.OR){
            LOGIC_OR_2();
        }
    }

    /*
    LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2
                 -> E
     */
    public void LOGIC_OR_2(){
        if(preanalisis.tipo == TipoToken.OR){
            match(TipoToken.OR);
            LOGIC_AND();
            LOGIC_OR_2();
        }
    }

    // LOGIC_AND -> EQUALITY LOGIC_AND_2
    public void LOGIC_AND(){
        EQUALITY();
        if(preanalisis.tipo == TipoToken.AND){
            LOGIC_AND_2();
        }
    }

    /*
    LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2
                   -> E
    */
    public void LOGIC_AND_2(){
        switch (this.preanalisis.getTipo()){
            case BANG_EQUAL:
                match(TipoToken.BANG_EQUAL);
                COMPARISON();
                LOGIC_AND_2();
                break;
            case EQUAL_EQUAL:
                match(TipoToken.EQUAL_EQUAL);
                COMPARISON();
                LOGIC_AND_2();
                break;
            default:
                this.hayErrores=true;
                break;
        }
    }

    // EQUALITY -> COMPARISON EQUALITY_2
    public void EQUALITY(){
        COMPARISON();
        if(preanalisis.tipo == TipoToken.BANG_EQUAL || preanalisis.tipo == TipoToken.EQUAL_EQUAL){
            EQUALITY_2();
        }
    }

    /*
    EQUALITY_2 -> != COMPARISON EQUALITY_2
                 -> == COMPARISON EQUALITY_2
                 -> E
     */
    public void EQUALITY_2(){
        switch (this.preanalisis.getTipo()){
            case BANG_EQUAL:
                match(TipoToken.BANG_EQUAL);
                COMPARISON();
                EQUALITY_2();
                break;

            case EQUAL_EQUAL:
                match(TipoToken.EQUAL_EQUAL);
                COMPARISON();
                EQUALITY_2();
                break;
            default:
                this.hayErrores=true;
                break;
        }

    }

    // COMPARISON -> TERM COMPARISON_2
    public void COMPARISON(){
        TERM();
        if(preanalisis.tipo == TipoToken.GREATER ||preanalisis.tipo == TipoToken.GREATER_EQUAL || preanalisis.tipo == TipoToken.LESS || preanalisis.tipo == TipoToken.LESS_EQUAL){
            COMPARISON_2();
        }
    }

    /*
    COMPARISON_2 -> > TERM COMPARISON_2
                    -> >= TERM COMPARISON_2
                    -> < TERM COMPARISON_2
                    -> <= TERM COMPARISON_2
                    -> E
    */
    public void COMPARISON_2(){
        switch (this.preanalisis.getTipo()){
            case GREATER:
                match(TipoToken.GREATER);
                TERM();
                COMPARISON_2();
                break;

            case GREATER_EQUAL:
                match(TipoToken.GREATER_EQUAL);
                TERM();
                COMPARISON_2();
                break;

            case LESS:
                match(TipoToken.LESS);
                break;

            case LESS_EQUAL:
                match(TipoToken.LESS_EQUAL);
                break;

            default:
                this.hayErrores=true;
                break;
        }
    }

    // TERM -> FACTOR TERM_2
    public void TERM(){
        FACTOR();
        if(preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.PLUS){
            TERM_2();
        }
    }

    /*
    TERM_2 -> - FACTOR TERM_2
            -> + FACTOR TERM_2
            -> E
    */
    public void TERM_2(){
        switch (this.preanalisis.getTipo()){
            case MINUS:
                match(TipoToken.MINUS);
                FACTOR();
                TERM_2();
                break;

            case PLUS:
                match(TipoToken.PLUS);
                FACTOR();
                TERM_2();

                break;

            default:
                break;
        }
    }

    // FACTOR -> UNARY FACTOR_2
    public void FACTOR(){
        UNARY();
        if(preanalisis.tipo == TipoToken.STAR || preanalisis.tipo == TipoToken.SLASH){
            FACTOR_2();
        }
    }

    /*
    FACTOR_2 -> / UNARY FACTOR_2
                -> * UNARY FACTOR_2
                -> E
    */
    public void FACTOR_2(){
        switch (this.preanalisis.getTipo()){
            case SLASH:
                match(TipoToken.SLASH);
                FACTOR_2();

            case STAR:
                match(TipoToken.STAR);
                FACTOR_2();

            default:
                break;
        }
    }

    /*
    UNARY -> ! UNARY
            -> - UNARY
            -> CALL
    */
    public void UNARY(){
        switch (this.preanalisis.getTipo()){
            case BANG:
                match(TipoToken.BANG);
                UNARY();
                break;

            case MINUS:
                match(TipoToken.MINUS);
                UNARY();
                break;

            case TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN:
                CALL();
                break;

            default:
                this.hayErrores = true;
                System.out.println("Error detectado en la lexema "+ this.preanalisis.lexema);
                break;

        }
    }

    // CALL -> PRIMARY CALL_2
    public void CALL(){
        PRIMARY();
        if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            CALL_2();
        }
    }

    /*
    CALL_2 -> ( ARGUMENTS_OPC ) CALL_2
            -> E
    */
    public void CALL_2(){
        if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            match(TipoToken.LEFT_PAREN);
            match(TipoToken.RIGHT_PAREN);
            CALL_2();
        }
    }

    /*
    PRIMARY -> true
            -> false
            -> null
            -> number
            -> string
            -> id
            -> ( EXPRESSION )
    */
    public void PRIMARY() {
        switch (this.preanalisis.getTipo()) {
            case TRUE:
                match(TipoToken.TRUE);
                break;

            case FALSE:
                match(TipoToken.FALSE);
                break;

            case NULL:
                match(TipoToken.NULL);
                break;

            case NUMBER:
                match(TipoToken.NUMBER);
                break;

            case STRING:
                match(TipoToken.STRING);
                break;

            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                break;

            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                EXPRESSION();
                match(TipoToken.RIGHT_PAREN);
                break;

            default:
                this.hayErrores = true;
                System.out.println("Error detectado en la lexema " + this.preanalisis.lexema);

        }
    }

    /*      OTROS      */
    // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
    public void FUNCTION(){
        switch (this.preanalisis.getTipo()){
            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                match(TipoToken.LEFT_PAREN);
                PARAMETERS_OPC();
                match(TipoToken.RIGHT_PAREN);
                break;
            default:
                this.hayErrores=true;
                System.out.println("Error en la lexema "+ preanalisis.lexema + ": Se esperaba un id");
                break;
        }
    }

    /*
    PARAMETERS_OPC -> PARAMETERS
                    -> E
    */
    public void PARAMETERS_OPC(){
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            PARAMETERS();
        }
    }

    // PARAMETERS -> id PARAMETERS_2
    public void PARAMETERS(){
        switch (this.preanalisis.getTipo()) {
            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                PARAMETERS_2();
                break;

            default:
                this.hayErrores = true;
                System.out.println("Error en la lexema "+ this.preanalisis.lexema + ": Se esperaba un id");
                break;
        }
    }

    /*
    PARAMETERS_2 -> , id PARAMETERS_2
                -> E
    */
    public void PARAMETERS_2(){
        // Es una funcion recursiva, pero modificada a su forma iterativa
        while(this.preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            if(this.preanalisis.tipo == TipoToken.IDENTIFIER){
                match(TipoToken.IDENTIFIER);
            }
        }
    }

    /*
    ARGUMENTS_OPC -> EXPRESSION ARGUMENTS
                    -> E
    */
    public void ARGUMENTS_OPC(){
        if(preanalisis.tipo == TipoToken.BANG ||
                preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.TRUE ||
                preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL ||
                preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING ||
                preanalisis.tipo == TipoToken.IDENTIFIER ||
                preanalisis.tipo == TipoToken.LEFT_PAREN){
            ARGUMENTS();
        }
    }

    /*
    ARGUMENTS -> , EXPRESSION ARGUMENTS
                -> E
    */
    public void ARGUMENTS(){
        while(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
        }
    }
}