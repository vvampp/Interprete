package semantic;


import parser.clases.ExprVariable;
import parser.clases.*;

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
                    //analizaSentenciaIf((StmtIf) declaration, tablaLocal);
                    break;
                case "StmtLoop":
                    //analizaSentenciaLoop((StmtLoop) declaration, tablaLocal);
                    break;
                case "StmtPrint":
                    //analizaSentenciaPrint((StmtPrint) declaration, tablaLocal);
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
                //analizarExpresionAsignacion((ExprAssign) expression, tablaLocal);
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
