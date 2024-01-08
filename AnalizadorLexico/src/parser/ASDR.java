package parser;
import parser.clases.*;
import tokens.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
        List <Statement> arbol = PROGRAM();

        // Si no hay errores y se llego al final del archivo
        if(!this.hayErrores){
            if (this.preanalisis.tipo == TipoToken.EOF) {
                System.out.println("Sin errores");
                System.out.println(arbol);
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
    public List<Statement> PROGRAM(){
        List<Statement> statements = new ArrayList<>(); //Lista de declaraciones

        switch (this.preanalisis.getTipo()){
            case FUN, VAR,
                    BANG, MINUS,
                    TRUE, FALSE,
                    NULL, NUMBER,
                    STRING, IDENTIFIER,
                    LEFT_PAREN, FOR,
                    IF, PRINT,
                    RETURN, WHILE, LEFT_BRACE:
                DECLARATION(statements);
                break;

            default:
                break;
        }
        return statements;
    }

    /*      DECLARACIONES      */

    /*
    DECLARATION   -> FUN_DECL DECLARATION
                    -> VAR_DECL DECLARATION
                    -> STATEMENT DECLARATION
                    -> E
    */
    public void DECLARATION(List <Statement> statements){
        switch (this.preanalisis.getTipo()){
            case FUN:
                Statement funDecl = FUN_DECL();
                statements.add(funDecl); //Se agrega la declaracion a la lista de declaraciones
                DECLARATION(statements);
                break;

            case VAR:
                Statement varDecl = VAR_DECL();
                statements.add(varDecl); //Se agrega la declaracion a la lista de declaraciones
                DECLARATION(statements);
                break;

            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN, FOR, IF, PRINT, RETURN, WHILE, LEFT_BRACE:
                Statement stmt = STATEMENT();
                statements.add(stmt); //Se agrega la declaracion a la lista de declaraciones
                DECLARATION(statements);
                break;

            default:
                break;
        }
    }

    // FUN_DECL -> fun FUNCTION
    public Statement FUN_DECL(){
        if(this.preanalisis.tipo == TipoToken.FUN){
            match(TipoToken.FUN);
            return FUNCTION();
        }else{
            this.hayErrores = true;
            System.out.println("Error en la lexema "+ this.preanalisis.lexema + ": Se esperaba un 'fun'");
            return null;
        }
    }

    // VAR_DECL -> var id VAR_INIT ;
    public Statement VAR_DECL(){
        Expression initialziaer = null;
        if(preanalisis.tipo == TipoToken.VAR){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            Token name = previous();
            if(preanalisis.tipo == TipoToken.EQUAL){
                initialziaer = VAR_INIT(null);
            }
            match(TipoToken.SEMICOLON);
            return new StmtVar(name, initialziaer);
        }else{
            hayErrores=true;
            System.out.println("Error en la lexema "+ preanalisis.lexema + ": Se esperaba un 'var'");
            return null;
        }
    }

    /*VAR_INIT -> = EXPRESSION
                -> E
    */
    public Expression VAR_INIT(Expression initializer){
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            initializer = EXPRESSION();
        }
        return initializer;
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

    public Statement STATEMENT(){
        // Statement del que surgen todos los demas statements
        switch (this.preanalisis.getTipo()){
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN:
                return EXPR_STMT();

            case FOR:
                return FOR_STMT();

            case IF:
                 return IF_STMT();

            case PRINT:
                return PRINT_STMT();

            case RETURN:
                return RETURN_STMT();

            case WHILE:
                return WHILE_STMT();

            case LEFT_BRACE:
                return BLOCK();

            default:
                this.hayErrores = true;
                System.out.println("Error en la lexema "+ this.preanalisis.getLexema());
                return null;
        }
    }

    // EXPR_STMT -> EXPRESSION ;
    public Statement EXPR_STMT(){
        // Un Stmt expression esta compuesto por una Expression expression
        Expression expr = EXPRESSION();
        match(TipoToken.SEMICOLON);
        return new StmtExpression(expr);
    }

    // FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    public Statement FOR_STMT(){
        // Una sentencia for esta compuesta por una inicializacion, una condicion y un incremento
        // Pero se debe crear un stmt loop el cual solo tiene condition y body
        // La inicializacion esta fuera del for, la condition ya es un objeto, y el incremento esta dentro del body
        // Por lo que se creara un objeto StmtBlock que contenga todo este conjunto
        if(this.preanalisis.getTipo() == TipoToken.FOR){
            match(TipoToken.FOR);
            match(TipoToken.LEFT_PAREN);
            Statement initializer = FOR_STMT_1(); // El primer elemento del for, es el inicializador, por lo que no se toma para crear el objeto StmtLoop
            Expression condition = FOR_STMT_2(); // El segundo elementos del for, es la condicion, por lo que se toma para crear el objeto StmtLoop
            Expression increase = FOR_STMT_3(); // El tercer elemento del for, es el incremento, por lo que no se toma para crear el objeto StmtLoop
            match(TipoToken.RIGHT_PAREN);

            List <Statement> statements = new ArrayList<>(); // Lista de statements que conforman al for

            statements.add(initializer); // Se agrega el inicializador a la lista de statements
            Statement body = STATEMENT(); // Se recupera el statement body

            // Si el incremento es null, el body ya no se tiene que modificar para agregar el incremento
            if(increase == null){
                statements.add(new StmtLoop(condition,body));
            }
            else {
                // Si el incremento no es null, se tiene que modificar el body para agregar el incremento
                // El body es el conjunto del incremento y el cuerpo del for, juntos en un objeto StmtBlock,
                // Pero el StmtBlock solo esta conformado por un arreglo de statements, por lo que se crea un arreglo de statements
                // y para evitar errores, se crea un objeto StmtExpression que contenga al incremento que es una Expression
                statements.add(new StmtLoop(condition, new StmtBlock(Arrays.asList(body, new StmtExpression(increase)))));
            }
            return new StmtBlock(statements); // Se crea un objeto StmtBlock con este arreglo de declaraciones que conforman al for
        }else{
            this.hayErrores = true;
            System.out.println("Error en el primer elemento del for de la lexema "+this.preanalisis.getLexema());
            return null;
        }
    }

    /*FOR_STMT_1 -> VAR_DECL
             -> EXPR_STMT
             -> ;
    */
    public Statement FOR_STMT_1() {
        // Primer elemento del for, la inicializacion, la cual puede ser una declaracion o una expresion
        switch (this.preanalisis.getTipo()) {
            case VAR:
                return VAR_DECL();

            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN:
                return EXPR_STMT();

            case SEMICOLON:
                // Si se va por este caso, quiere decir que la inicializacion fue cadena vacia
                match(TipoToken.SEMICOLON);
                return new StmtExpression(new ExprLiteral(true)); // No se puede devolver unicamente una expression, por lo que se crea una sentencia que la contenga

            default:
                this.hayErrores = true;
                System.out.println("Error en el primer elemento del for de la lexema " + this.preanalisis.getLexema());
                return null;
        }
    }

    /*
    FOR_STMT_2 -> EXPRESSION;
               -> ;
    */
    public Expression FOR_STMT_2(){
        // Segundo elemento del for, la condicion, la cual es una expresion
        switch (this.preanalisis.getTipo()){
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER,
                    STRING, IDENTIFIER, LEFT_PAREN:
                Expression expr = EXPRESSION();
                match(TipoToken.SEMICOLON);
                return new ExprGrouping(expr); //

            case SEMICOLON:
                // Si se va por este caso, quiere decir que la condicion fue cadena vacia
                match(TipoToken.SEMICOLON);
                return new ExprLiteral(true); // Expresion que mejor se identifica con este caso

            default:
                this.hayErrores = true;
                System.out.println("Error en el segundo elemento del for de la lexema "+ this.preanalisis.getLexema());
                return null;
        }

    }


    /*
    FOR_STMT_3 -> EXPRESSION
                 -> E
     */
    public Expression FOR_STMT_3(){
        if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN){
            return EXPRESSION();
        }
        return null;
    }

    // IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    public Statement IF_STMT(){
        // Una sentencia if esta compuesta por una expresion condition,
        // un statement thenBranch
        // y un statement elseBranch
        Statement elseBranch = null; // Rama del if
        if(this.preanalisis.getTipo() == TipoToken.IF){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            Expression condition = EXPRESSION(); // Se recupera la expresion condition
            match(TipoToken.RIGHT_PAREN);
            Statement thenBranch = STATEMENT();  // Se recupera el statement thenBranch
            // Si el preanalisis es un else, se recupera el statement elseBranch
            if(preanalisis.getTipo() == TipoToken.ELSE) {
                // Se recupera la sentencia que se prosigue al thenBranch en el if
                // si la derivacion es nula, se regresa un null
                // en otro caso se regresa la sentencia
                elseBranch = ELSE_STATEMENT(null);
            }
            return new StmtIf(condition, thenBranch, elseBranch);
        }else{
            this.hayErrores=true;
            System.out.println("Error en la lexema "+ preanalisis.getLexema() + ": Se esperaba un 'if'");
            return null;
        }
    }

    /*
    ELSE_STATEMENT -> else STATEMENT
                     -> E
     */
    public Statement ELSE_STATEMENT(Statement elseBranch){
        // Rama del if donde se encuentra el else y un statement elseBranch
        if(this.preanalisis.tipo == TipoToken.ELSE){
            match(TipoToken.ELSE);
            elseBranch = STATEMENT();
            return elseBranch;
        }
        return elseBranch;
    }

    public Statement PRINT_STMT(){
        // Un Stmt print esta compuesto por una Expression expression
        if(this.preanalisis.tipo == TipoToken.PRINT){
            match(TipoToken.PRINT);
            Expression expr = EXPRESSION();
            match(TipoToken.SEMICOLON);
            return new StmtPrint(expr);
        }else{
            this.hayErrores=true;
            System.out.println("Error en la lexema "+ this.preanalisis.getLexema() + ": Se esperaba un 'print'");
            return null;
        }
    }

    // RETURN_STMT -> return RETURN_EXP_OPC ;
    public Statement RETURN_STMT(){
        // El Stmt return esta compuesto de una Expresion value
        Expression value = null; // null por default
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
                // Se recupera la expresion que se regresara en el return
                // si la derivacion es nula, se regresa un null
                // en otro caso se regresa la expresion
                value = RETURN_EXP_OPC(null);
            }
            match(TipoToken.SEMICOLON);
            return new StmtReturn(value);
        }else{
            this.hayErrores=true;
            System.out.println("Error en la lexema "+ preanalisis.getLexema() + ": Se esperaba un 'return'");
            return null;
        }
    }

    public Expression RETURN_EXP_OPC(Expression value){
        // Expresion que se regresara en el return
        if(this.preanalisis.tipo == TipoToken.BANG ||
                this.preanalisis.tipo == TipoToken.MINUS ||
                this.preanalisis.tipo == TipoToken.TRUE ||
                this.preanalisis.tipo == TipoToken.FALSE ||
                this.preanalisis.tipo == TipoToken.NULL ||
                this.preanalisis.tipo == TipoToken.NUMBER ||
                this.preanalisis.tipo == TipoToken.STRING ||
                this.preanalisis.tipo == TipoToken.IDENTIFIER ||
                this.preanalisis.tipo == TipoToken.LEFT_PAREN){
            value = EXPRESSION();
            return value;
        }
        return value;
    }

    // WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    public Statement WHILE_STMT(){
        // Una sentencia while esta compuesta por una expresion condition y un statement body
        if(this.preanalisis.getTipo() == TipoToken.WHILE){
            match(TipoToken.WHILE);
            match(TipoToken.LEFT_PAREN);
            Expression condition = EXPRESSION(); // Se recupera la expresion condition
            match(TipoToken.RIGHT_PAREN);
            Statement body = STATEMENT(); // Se recupera el statement body
            return new StmtLoop(condition, body); // Se regresa un statement loop
        }else{
            this.hayErrores = true;
            System.out.println("Error en la lexema "+ preanalisis.lexema + ": Se esperaba un 'while'");
            return null;
        }
    }

    // BLOCK -> { DECLARATION }
    public Statement BLOCK(){
        // Bloque de de statements, por lo que se tienen que recuperar
        // todas las declaraciones que se encuentren dentro de este bloque
        List < Statement > statementsList = new ArrayList<>();

        if(this.preanalisis.getTipo() == TipoToken.LEFT_BRACE){
            match(TipoToken.LEFT_BRACE);
            DECLARATION(statementsList); // Se recuperan todas las declaraciones que se encuentren dentro del bloque, para que se pueda seguir la rama del arbol
            match(TipoToken.RIGHT_BRACE);
            return new StmtBlock(statementsList); // Se regresa un bloque de statements
        }else{
            this.hayErrores = true;
            System.out.println("Error en la lexema "+ this.preanalisis.getLexema() + ": Se esperaba un 'LEFT_BRACE'");
            return null;
        }
    }

    /*      EXPRESIONES      */


    //Expresiones

    // EXPRESSION -> ASSIGNMENT
    public Expression EXPRESSION(){
        return ASSIGNMENT();
    }

    // ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
    public Expression ASSIGNMENT(){
        Expression expr = LOGIC_OR();
        if(preanalisis.tipo == TipoToken.EQUAL){
            expr = ASSIGNMENT_OPC(expr);
        }
        return expr;
    }

    /*ASSIGNMENT_OPC -> = EXPRESSION
                     -> E */
    public Expression ASSIGNMENT_OPC(Expression value){
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            Token name = previous();
            value = EXPRESSION();
            return new ExprAssign(name, value);
        }
        return value;
    }

    // LOGIC_OR -> LOGIC_AND LOGIC_OR_2
    public Expression LOGIC_OR(){
        Expression expr = LOGIC_AND();
        if(preanalisis.tipo == TipoToken.OR){
            expr = LOGIC_OR_2(expr);
        }
        return expr;
    }

    /*
    LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2
                 -> E
     */
    public Expression LOGIC_OR_2(Expression expr){
        if(preanalisis.tipo == TipoToken.OR){
            match(TipoToken.OR);
            Token operador = previous();
            Expression _expr = LOGIC_AND();
            Expression expb = new ExprLogical(expr, operador, _expr);
            return LOGIC_OR_2(expb);
        }
        return expr;
    }

    // LOGIC_AND -> EQUALITY LOGIC_AND_2
    public Expression LOGIC_AND(){
        Expression expr = EQUALITY();
        if(preanalisis.tipo == TipoToken.AND){
            expr =  LOGIC_AND_2(expr);
        }
        return expr;
    }

    /*
    LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2
                   -> E
    */
    public Expression LOGIC_AND_2(Expression expr){
        Token operador;
        Expression _expr;
        ExprBinary expb;

        switch (this.preanalisis.getTipo()){
            case BANG_EQUAL:
                match(TipoToken.BANG_EQUAL);
                operador = previous();
                _expr = COMPARISON();
                expb = new ExprBinary(expr, operador, _expr);
                return EQUALITY_2(expb);

            case EQUAL_EQUAL:
                match(TipoToken.EQUAL_EQUAL);
                operador = previous();
                _expr = COMPARISON();
                expb = new ExprBinary(expr, operador, _expr);
                return EQUALITY_2(expb);

            default:
                break;
        }
        return expr;
    }

    // EQUALITY -> COMPARISON EQUALITY_2
    public Expression EQUALITY(){
        Expression expr = COMPARISON();
        if(preanalisis.tipo == TipoToken.BANG_EQUAL || preanalisis.tipo == TipoToken.EQUAL_EQUAL){
            expr = EQUALITY_2(expr);
        }
        return expr;
    }

    /*
    EQUALITY_2 -> != COMPARISON EQUALITY_2
                 -> == COMPARISON EQUALITY_2
                 -> E
     */
    public Expression EQUALITY_2(Expression expr){
        Token operador;
        Expression _expr;
        ExprBinary expb;

        switch (this.preanalisis.getTipo()){
            case BANG_EQUAL:
                match(TipoToken.BANG_EQUAL);
                operador = previous();
                _expr = COMPARISON();
                expb = new ExprBinary(expr, operador, _expr);
                return EQUALITY_2(expb);

            case EQUAL_EQUAL:
                match(TipoToken.EQUAL_EQUAL);
                operador = previous();
                _expr = COMPARISON();
                expb = new ExprBinary(expr, operador, _expr);
                return EQUALITY_2(expb);

            default:
                break;

        }
        return expr;
    }

    // COMPARISON -> TERM COMPARISON_2
    public Expression COMPARISON(){
        Expression expr = TERM();
        if(preanalisis.tipo == TipoToken.GREATER ||preanalisis.tipo == TipoToken.GREATER_EQUAL || preanalisis.tipo == TipoToken.LESS || preanalisis.tipo == TipoToken.LESS_EQUAL){
            expr =  COMPARISON_2(expr);
        }
        return expr;
    }

    /*
    COMPARISON_2 -> > TERM COMPARISON_2
                    -> >= TERM COMPARISON_2
                    -> < TERM COMPARISON_2
                    -> <= TERM COMPARISON_2
                    -> E
    */
    public Expression COMPARISON_2(Expression expr){
        Token operador;
        Expression _expr;
        ExprBinary expb;

        switch (this.preanalisis.getTipo()){
            case GREATER:
                match(TipoToken.GREATER);
                operador = previous();
                _expr= TERM();
                expb= new ExprBinary(expr, operador, _expr);
                return COMPARISON_2(expb);

            case GREATER_EQUAL:
                match(TipoToken.GREATER_EQUAL);
                operador = previous();
                _expr= TERM();
                expb= new ExprBinary(expr, operador, _expr);
                return COMPARISON_2(expb);

            case LESS:
                match(TipoToken.LESS);
                operador = previous();
                _expr= TERM();
                expb= new ExprBinary(expr, operador, _expr);
                return COMPARISON_2(expb);

            case LESS_EQUAL:
                match(TipoToken.LESS_EQUAL);
                operador = previous();
                _expr= TERM();
                expb= new ExprBinary(expr, operador, _expr);
                return COMPARISON_2(expb);

            default:
                break;
        }
        return expr;
    }

    // TERM -> FACTOR TERM_2
    public Expression TERM(){
        Expression expr = FACTOR();
        if(preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.PLUS){
            expr = TERM_2(expr);
        }
        return expr;
    }

    /*
    TERM_2 -> - FACTOR TERM_2
            -> + FACTOR TERM_2
            -> E
    */
    public Expression TERM_2(Expression expr){
        Token operador;
        Expression _expr;
        ExprBinary expb;
        switch (this.preanalisis.getTipo()){
            case MINUS:
                match(TipoToken.MINUS);
                operador = previous();
                _expr=FACTOR();
                expb = new ExprBinary(expr, operador, _expr);
                return TERM_2(expb);

            case PLUS:
                match(TipoToken.PLUS);
                operador = previous();
                _expr = FACTOR();
                expb = new ExprBinary(expr, operador, _expr);
                return TERM_2(expb);

            default:
                break;
        }
        return expr;
    }

    // FACTOR -> UNARY FACTOR_2
    public Expression FACTOR(){
        Expression expr = UNARY();
        if(preanalisis.tipo == TipoToken.STAR || preanalisis.tipo == TipoToken.SLASH){
            expr = FACTOR_2(expr);
        }
        return expr;
    }

    /*
    FACTOR_2 -> / UNARY FACTOR_2
                -> * UNARY FACTOR_2
                -> E
    */
    public Expression FACTOR_2(Expression expr){
        Token operador;
        Expression _expr;
        ExprBinary expb;
        switch (this.preanalisis.getTipo()){
            case SLASH:
                match(TipoToken.SLASH);
                operador = previous();
                _expr = UNARY();
                expb = new ExprBinary(expr, operador, _expr);
                return FACTOR_2(expb);

            case STAR:
                match(TipoToken.STAR);
                operador = previous();
                _expr= UNARY();
                expb = new ExprBinary(expr, operador, _expr);
                return FACTOR_2(expb);

            default:
                break;
        }
        return expr;
    }

    /*
    UNARY -> ! UNARY
            -> - UNARY
            -> CALL
    */
    public Expression UNARY(){
        Token operador;
        Expression expr;
        switch (this.preanalisis.getTipo()){
            case BANG:
                match(TipoToken.BANG);
                operador = previous();
                expr = UNARY();
                return new ExprUnary(operador, expr);

            case MINUS:
                match(TipoToken.MINUS);
                operador = previous();
                expr = UNARY();
                return new ExprUnary(operador, expr);

            case TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN:
                return CALL();

            default:
                this.hayErrores = true;
                System.out.println("Error detectado en la lexema "+ this.preanalisis.lexema);
                return null;

        }
    }

    // CALL -> PRIMARY CALL_2
    public Expression CALL(){
        Expression expr = PRIMARY();
        if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            expr = CALL_2(expr);
        }
        return expr;
    }

    /*
    CALL_2 -> ( ARGUMENTS_OPC ) CALL_2
            -> E
    */
    public Expression CALL_2(Expression expr){
        if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            match(TipoToken.LEFT_PAREN);
            List <Expression> arguments = ARGUMENTS_OPC();
            //Argumentos que retorna Arguments_opc()
            match(TipoToken.RIGHT_PAREN);
            ExprCallFunction ecf = new ExprCallFunction(expr, arguments);
            CALL_2(ecf);

        }
        return expr;
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
    public Expression PRIMARY() {
        switch (this.preanalisis.getTipo()) {
            case TRUE:
                match(TipoToken.TRUE);
                return new ExprLiteral(true);

            case FALSE:
                match(TipoToken.FALSE);
                return new ExprLiteral(false);

            case NULL:
                match(TipoToken.NULL);
                return new ExprLiteral(null);

            case NUMBER:
                match(TipoToken.NUMBER);
                Token numero = previous();
                return new ExprLiteral(numero.getLiteral());

            case STRING:
                match(TipoToken.STRING);
                Token cadena = previous();
                return new ExprLiteral(cadena.getLiteral());

            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                Token id = previous();
                return new ExprVariable(id);

            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                Expression expr = EXPRESSION();
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expr);

            default:
                this.hayErrores = true;
                System.out.println("Error detectado en la lexema " + this.preanalisis.lexema);
                return null;
        }
    }

    /*      OTROS      */
    // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
    public Statement FUNCTION(){
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            match(TipoToken.IDENTIFIER);
            Token name = previous();
            match(TipoToken.LEFT_PAREN);
            // Parametros opcionales que puede contener una funcion, retorna una lista de tokens la cual contiene los identificadores de los parametros
            List <Token> parameterList = PARAMETERS_OPC();
            match(TipoToken.RIGHT_PAREN);
            Statement body = BLOCK();
            return new StmtFunction(name, parameterList, (StmtBlock) body);
        }else{
            hayErrores=true;
            System.out.println("Error en la lexema "+ preanalisis.lexema + ": Se esperaba un id");
            return null;
        }
    }

    /*
    PARAMETERS_OPC -> PARAMETERS
                    -> E
    */
    public List <Token> PARAMETERS_OPC(){
        List <Token> parameterList = new ArrayList<>();
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            PARAMETERS(parameterList);
        }
        return parameterList;
    }

    // PARAMETERS -> id PARAMETERS_2
    public void PARAMETERS(List <Token> parameterList){
        if(this.preanalisis.tipo == TipoToken.IDENTIFIER){
            Token paramToken = this.preanalisis;
            match(TipoToken.IDENTIFIER);
            parameterList.add(paramToken);
            PARAMETERS_2(parameterList);
        }else{
            this.hayErrores = true;
            System.out.println("Error en la lexema "+ this.preanalisis.lexema + ": Se esperaba un id");
        }
    }

    /*
    PARAMETERS_2 -> , id PARAMETERS_2
                -> E
    */
    public void PARAMETERS_2(List <Token> parameterList){
        // Es una funcion recursiva, pero modificada a su forma iterativa
        while(this.preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            if(this.preanalisis.tipo == TipoToken.IDENTIFIER){
                Token additionalParamToken = this.preanalisis; // Unicamente se guarda el token de identificador, no se guardan los tokens de coma
                match(TipoToken.IDENTIFIER);
                parameterList.add(additionalParamToken);
            }
        }
    }

    /*
    ARGUMENTS_OPC -> EXPRESSION ARGUMENTS
                    -> E
    */
    public List<Expression> ARGUMENTS_OPC(){
        List <Expression> arguments = new ArrayList<>();
        if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN){
            arguments.add(EXPRESSION());
            ARGUMENTS(arguments);
        }
        return arguments;
    }

    /*
    ARGUMENTS -> , EXPRESSION ARGUMENTS
                -> E
    */
    public void ARGUMENTS(List <Expression> arguments){
        while(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            arguments.add(EXPRESSION());
        }
    }
}