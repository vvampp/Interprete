package parser;

import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     *
     *
     *
     */



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
                //EXPR_STMT();

            case FOR:
                //FOR_STMT();

            case IF:
                 //IF_STMT();

            case PRINT:
                //PRINT_STMT();

            case RETURN:
                //RETURN_STMT();

            case WHILE:
                //WHILE_STMT();

            case LEFT_BRACE:
                //BLOCK();

            default:
                this.hayErrores = true;
                System.out.println("Error en la lexema "+ this.preanalisis.getLexema());
                return;
        }
    }

    // EXPR_STMT -> EXPRESSION ;
    public void EXPR_STMT(){
        //EXPRESSION();
        match(TipoToken.SEMICOLON);
    }

    // FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    public void FOR_STMT(){
        if(preanalisis.getTipo() == TipoToken.FOR){
            match(TipoToken.FOR);
            match(TipoToken.LEFT_PAREN);
            //FOR_STMT_1();
            //FOR_STMT_2();
            //FOR_STMT_3();
            match(TipoToken.RIGHT_PAREN);
            //STATEMENT();
        }else{
            this.hayErrores = true;
            System.out.println("Error en el primer elemento del for de la lexema "+this.preanalisis.getLexema());
        }
    }

}
