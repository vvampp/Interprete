package semantic;

import parser.clases.*;

import java.util.HashMap;
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
            return valor;
        } else if (tablaPadre != null) {
            // Si el valor es nulo, significa que no está en el Hashmap, por lo que se busca en el Hashmap del padre
            return tablaPadre.retornarValor(varNombre);
        } else {
            //Retorna null en caso de que no esté en ningún Hashmap, lo que significaría que no está declarada la variable.
            return null;
        }
    }

    // Función para obtener el valor de una expresión
    // (ExprVariable)
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
            } else{
                return valor;
            }
        } else if (expression instanceof ExprLiteral) {
            return ((ExprLiteral) expression).getValue();
        }
        return null;
    }




}
