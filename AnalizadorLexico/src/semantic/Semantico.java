package semantic;

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

}
