package semantic;

import parser.clases.*;
import tokens.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tabla {
    private Map<String, Object> tablaSimbolos; // TablaSimbolosLocal
    private Tabla tablaPadre; // TablaSimbolosPadre

    // Contructor
    public Tabla(Tabla tablaPadre){
        this.tablaPadre = tablaPadre;
        this.tablaSimbolos = new HashMap<>();
    }

    // Getter
    public Map<String, Object> getTablaSimbolos(){
        return this.tablaSimbolos;
    }

    //Función para comprobar si la variable está definida en el Hashmap
    public boolean siEstaDefinida(String varNombre) {
        if (tablaSimbolos.containsKey(varNombre)) {
            return true;
        } else if (tablaPadre != null) {
            return tablaPadre.siEstaDefinida(varNombre);
        } else {
            return false;
        }
    }

    //Función para obtener el Nombre de la Variable
    // Necesitamos el nombre ya que sera la key del value en los HashMaps (tablas de simbolos)
    private String getNombreVariable(Expression expression) {
        if (expression instanceof ExprVariable) {
            return ((ExprVariable) expression).getName().getLexema();
        }
        return null;
    }

    //Función para agregar variable al HashMap
    public void declararEnTabla(String varNombre, Object valor, Tabla tablaLocal) {
        switch (valor.getClass().getSimpleName()){
            case "ExprVariable":
                Expression var = (Expression) valor;
                String nombreVariable = getNombreVariable(var);
                tablaSimbolos.put(varNombre, tablaLocal.retornarValor(nombreVariable));
                break;

            case "ExprBinary":
                Expression expr = (Expression) valor;
                valor = getValor(expr, tablaLocal);
                tablaSimbolos.put(varNombre, valor);
                break;

            default:
                tablaSimbolos.put(varNombre, valor);
                break;
        }
    }

    //Función para retornar el valor de la variable si está en el Hashmap
    public Object retornarValor(String varNombre) {

        //Se le asigna el valor que está asociado al nombre de la Variable a buscar
        Object valor = tablaSimbolos.get(varNombre);

        // El valor es diferente de null
        // o existe la llave en la tabla de símbolos
        if (valor != null || tablaSimbolos.containsKey(varNombre)) {
            if(valor instanceof ExprLiteral){
                return ((ExprLiteral)valor).getValue();
            }else if(valor instanceof ExprBinary){
                return valor;
            }else{
                //Retorna el valor asociado
                return valor;
            }
        } else if (tablaPadre != null) {
            // Si el valor es nulo, significa que no está en el Hashmap, por lo que se busca en el Hashmap del padre
            return tablaPadre.retornarValor(varNombre);
        } else {
            //Retorna null en caso de que no esté en ningún Hashmap, lo que significaría que no está declarada la variable.
            return null;
        }
    }

    //Función para obtener el valor de una expresión
    // (ExprVariable o ExprLiteral).
    public Object getValor(Expression expression, Tabla tablaLocal) {
        if (expression instanceof ExprVariable) {
            String varNombre = getNombreVariable(expression);
            Object valor = tablaLocal.retornarValor(varNombre);
            if(valor instanceof String || valor instanceof Number){
                return valor;
            }else if(valor instanceof ExprVariable){
                Expression var = (Expression) valor;
                String nombreVariable = getNombreVariable(var);
                return tablaLocal.retornarValor(nombreVariable);
            }else if(valor instanceof ExprBinary){
                Expression expr = (Expression) valor;
                valor = getValor(expr, tablaLocal);
                return valor;
            }else{
                return valor;
            }
        } else if (expression instanceof ExprLiteral) {
            return ((ExprLiteral) expression).getValue();
        } else if (expression instanceof ExprBinary) {
            return evaluarExpresionBinaria((ExprBinary) expression, tablaLocal);
        } else if(expression instanceof ExprCallFunction){
            // Caso especial para los returns de las funciones
            return evaluarLlamadaFuncion((ExprCallFunction) expression, tablaLocal);
        }
        return null;
    }

    // Función para  una expresión binaria y retornar su resultado
    public Object evaluarExpresionBinaria(ExprBinary expresionBinaria, Tabla tablaLocal) {
        Expression exprIzquierda = expresionBinaria.getLeft();
        Expression exprDerecha = expresionBinaria.getRight();

        // Obtener los valores de las expresiones izquierda y derecha
        Object valIzq = getValor(exprIzquierda, tablaLocal);
        Object valDer = getValor(exprDerecha, tablaLocal);

        // Realizar la operación binaria según el operador
        switch (expresionBinaria.getOperator().getTipo()) {
            case PLUS:
                return operacionBinaria(valIzq, valDer, "+");
            case MINUS:
                return operacionBinaria(valIzq, valDer, "-");
            case STAR:
                return operacionBinaria(valIzq, valDer, "*");
            case SLASH:
                return operacionBinaria(valIzq, valDer, "/");

            default:
                // Manejo de error para operadores no compatibles
                reportarError("Operador no compatible en la expresión binaria");
                return null;
        }
    }

    // Funcion para evaluar una exprbinaria
    private Object operacionBinaria(Object valIzq, Object valDer, String operador){
        if(valIzq instanceof Number && valDer instanceof Number){
            switch (operador){
                case "+":
                    return ((Number) valIzq).doubleValue() + ((Number) valDer).doubleValue();
                case "-":
                    return ((Number) valIzq).doubleValue() - ((Number) valDer).doubleValue();
                case "*":
                    return ((Number) valIzq).doubleValue() * ((Number) valDer).doubleValue();
                case "/":
                    return ((Number) valIzq).doubleValue() / ((Number) valDer).doubleValue();
                default:
                    reportarError("No se pueden operar valores de tipos no compatibles");
                    return null;
            }
        } else if(valIzq instanceof String && valDer instanceof String){
            switch (operador){
                case "+":
                    return String.valueOf(valIzq) + String.valueOf(valDer);
                default:
                    reportarError("No se pueden sumar valores de tipos no compatibles");
                    return null;
            }
        } else {
            reportarError("No se pueden operar valores de tipos no compatibles");
            return null;
        }
    }

    //Funcion para evaluar una expresion de llamada a funcion
    public Object evaluarLlamadaFuncion(ExprCallFunction expresionLlamadaFuncion, Tabla tablaLocal){
        String nombreFuncion = ((ExprVariable) expresionLlamadaFuncion.getCallee()).getName().getLexema();
        StmtFunction declaracionFuncion = (StmtFunction) tablaLocal.retornarValor(nombreFuncion);
        List<Token> parametros = declaracionFuncion.getParameters();
        Statement primeraInstruccion = declaracionFuncion.getBody().getStatements().get(0);

        for(int i = 0; i < expresionLlamadaFuncion.getArguments().size(); i++){
            String paramNombre = parametros.get(i).getLexema();

            Expression arguVal = expresionLlamadaFuncion.getArguments().get(i);
            tablaLocal.declararEnTabla(paramNombre, arguVal, tablaLocal);

        }

        if(primeraInstruccion instanceof StmtReturn && ((StmtReturn) primeraInstruccion).getExpression() instanceof ExprBinary){
            return evaluarExpresionBinaria( (ExprBinary) ((StmtReturn) primeraInstruccion).getExpression(), tablaLocal);
        } else if(tablaLocal.getTablaSimbolos().containsKey("return")){
            return tablaLocal.getTablaSimbolos().get("return");
        }

        return null;
    }


    // Función para escribir el mensaje de Error
    private void reportarError(String mensaje) {
        System.out.println("Error Semántico: " + mensaje);
    }

}
