package semantic;


import parser.clases.ExprVariable;
import parser.clases.*;

import java.util.HashMap;
import java.util.List;

public class Semantico {
    public final Tabla tablaRaiz; // Tabla de simbolos raiz

    // Constructor
    public Semantico(){
        // Se crea un objeto tipo Tabla de Simbolos donde el padre es null, porque esta es la raiz del arbol
        this.tablaRaiz = new Tabla(null);
    }

    public void analizar(List <Statement> arbol, Tabla tablaRaiz){
        // Se manda a analizar el arbol, y se da como parametro la tablaDeSimbolos raiz del arbol
        analizarDeclarations(arbol, tablaRaiz);
    }

    // Función para escribir el mensaje de Error
    private void reportarError(String mensaje) {
        //throw new RuntimeException("Error Semántico: " + mensaje);
        System.out.println("Error Semántico: " + mensaje);
    }

    // SWITCH para Declarations
    // Funcion para analizar las diferentes ramas posibles
    private void analizarDeclarations(List<Statement> declarations, Tabla tablaLocal) {
        for (Statement declaration : declarations) {
            switch (declaration.getClass().getSimpleName()){
                case "StmtVar":
                    analizaDeclaracionVariable((StmtVar) declaration, tablaLocal);
                    break;
                case "StmtFunction":
                    //analizaDeclaracionFuncion((StmtFunction) declaration, tablaLocal);
                    break;
                case "StmtExpression":
                    //analizaSentenciaExpresion((StmtExpression) declaration, tablaLocal);
                    break;
                case "StmtIf":
                    analizaSentenciaIf((StmtIf) declaration, tablaLocal);
                    break;
                case "StmtLoop":
                    //analizaSentenciaLoop((StmtLoop) declaration, tablaLocal);
                    break;
                case "StmtPrint":
                    analizaSentenciaPrint((StmtPrint) declaration, tablaLocal);
                    break;
                case "StmtReturn":
                    //analizaSentenciaReturn((StmtReturn) declaration, tablaLocal);
                    break;
                case "StmtBlock":
                    //analizaSentenciaBlock((StmtBlock) declaration, tablaLocal);
                    break;
                default:
                    break;
            }
        }
    }

    // Este Stmt tiene un id y una expresion, es decir, se esta declarando algo
    // El error SEMANTICO radica en la re-definicion de esta variable
    // Recibe el StmtVar y la tabla de simbolos local
    private void analizaDeclaracionVariable(StmtVar declarationVar, Tabla tablaLocal) {
        String varNombre = declarationVar.getName().getLexema(); // Recuperemos el identificador de la variable

        //Se verifica si el identificador ya ha sido ingresada en la tabla de simbolos propia o en la de su padre/ancestro
        if (tablaLocal.siEstaDefinida(varNombre)) {
            reportarError("Variable '" + varNombre + "' re-definicion.");
        }
        else {
            //Si no está en la tabla de simbolos, quiere decir que no se esta re-definiendo, por lo que se guarda en la tabla de simbolos propia
            tablaLocal.declararEnTabla(varNombre, declarationVar.getInitializer(), tablaLocal);
        }

    }

    // SWTICH para Expressions
    //Función para analizar las Expresiones con los tipos que se tienen
    private void analizaExpression(Expression expression, Tabla tablaLocal) {
        switch (expression.getClass().getSimpleName()){
            case "ExprAssign":
                analizarExpresionAsignacion((ExprAssign) expression, tablaLocal);
                break;
            case "ExprLogical":
                //analizarExpresionLogica((ExprLogical) expression, tablaLocal);
                break;
            case "ExprBinary":
                //analizarExpresionBinaria((ExprBinary) expression, tablaLocal);
                break;
            case "ExprUnary":
                //analizarExpresionUnaria((ExprUnary) expression, tablaLocal);
                break;
            case "ExprCallFunction":
                //analizarExpresionLlamadaFuncion((ExprCallFunction) expression, tablaLocal);
                break;
            case "ExprVariable":
                //analizarExpresionVariable((ExprVariable) expression, tablaLocal);
                break;
            default:
                break;

        }
    }

    //Función para analizar los Prints
    private void analizaSentenciaPrint(StmtPrint imprimirSentencia, Tabla tablaLocal) {
        //Manda a llamar la función para analizar la Expresión que tenga el Print
        analizaExpression(imprimirSentencia.getExpression(), tablaLocal);

        // Obtén el valor de la expresión a imprimir
        Object imprimirValor = tablaLocal.getValor(imprimirSentencia.getExpression(), tablaLocal);

        // determina el tipo de expresión print se tiene
        int tipoPrint = evaluarTipoPrint(imprimirSentencia, tablaLocal);

        // Imprime en la consola el valor de la expresión
        switch (tipoPrint){
            // Number
            case 1:
                double doubleValue = ((Number) imprimirValor).doubleValue();
                if (doubleValue % 1 == 0) {
                    // Si el número tiene decimales 0, imprímelo como entero
                    System.out.println(((Number) imprimirValor).longValue());
                } else {
                    // Si el número tiene decimales, imprímelo como double
                    System.out.println(imprimirValor);
                }
                break;
            // String
            case 2:
                System.out.println(imprimirValor);
                break;

            default:
                break;
        }
    }

    // determina el tipo de expresión print se tiene y retorna un valor entero para el switch de salida
    // en cosola

    private int evaluarTipoPrint(StmtPrint imprimirSentencia, Tabla tablaLocal){
        analizaExpression(imprimirSentencia.getExpression(), tablaLocal);

        // Obtén el valor de la expresión a imprimir
        Object imprimirValor = tablaLocal.getValor(imprimirSentencia.getExpression(), tablaLocal);

        // retorna el valor de la instancia como String para el switch de la función principal
        // de análisis de la sentencia print
        if(imprimirValor instanceof Number)return 1;
        else if(imprimirValor instanceof String) return 2;
        else if(imprimirValor instanceof ExprLiteral) return 3;
        else if(imprimirValor instanceof ExprBinary) return 4;
        else if(imprimirValor instanceof Double) return 1;
        return 0;
    }

    //Función para analizar los If
    private void analizaSentenciaIf(StmtIf ifStatement, Tabla tablaLocal) {

        //Se manda a llamar la función para analizar la expresión para la condición
        analizaExpression(ifStatement.getCondition(), tablaLocal);

        // Se verifica si la condicion evaluada es true o false
        if(analizaCondicion(ifStatement.getCondition(), tablaLocal)){
            //Se manda a llamar la función para analizar el cuerpo del If
            //analizaSentencia(ifStatement.getThenBranch(), tablaLocal);
        }else{
            //Se valida si tiene un else el if para analizarlo
            if (ifStatement.getElseBranch() != null) {
                //analizaSentencia(ifStatement.getElseBranch(), tablaLocal);
            }
        }
    }

    //Función para EVALUAR condiciones y ver si son true o false
    private boolean analizaCondicion(Expression expression, Tabla tablaLocal){
        boolean verdad;
        verdad = true;
        return switch (expression.getClass().getSimpleName()) {
            //Si es una expresión Binaria
            case "ExprBinary" -> {
                //verdad = condicionBinary(expression, tablaLocal);
                yield verdad;
            }
            // Si es una expresión logica
            case "ExprLogical" -> {
                //verdad = condicionLogica(expression, tablaLocal);
                yield verdad;
            }
            // Si es una expresión literal
            case "ExprLiteral" -> {
                //verdad = condicionLiteral(expression, tablaLocal);
                yield verdad;
            }
            default -> false;
        };
    }

    private void analizarExpresionAsignacion(ExprAssign expresionAsignar, Tabla tablaLocal) {

        //Se obtiene el nombre de la variable a la que se está asignando
        String varNombre = expresionAsignar.getName().getLexema();
        //Se obtiene el valor asociado con ese nombre de variable
        Object valor = tablaLocal.retornarValor(varNombre);
        //Se comprueba si el nombre de la variable está en el Hashmap
        boolean definido = tablaLocal.siEstaDefinida(varNombre);

        //Se verifica si esta variable tiene un valor null y no está definido en el Hashmap
        if (valor == null && !definido) {
            reportarError("Error en la asignación: Variable '" + varNombre + "' no declarada previamente.");
        } else {
            //Se manda a analizar el valor de la asignación
            analizaExpression(expresionAsignar.getValue(), tablaLocal);
            // Actualizar el valor en el ámbito local
            tablaLocal.declararEnTabla(varNombre, expresionAsignar.getValue(), tablaLocal);
        }
    }

}
