package presentacion;

import parser.ASDR;
import parser.Parser;
import parser.clases.Statement;
import scanner.Scanner;
import tokens.Token;
import semantic.Semantico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Interprete {

    static boolean existenErrores = false;

    public static void main(String[] args) throws IOException {
        if(args.length > 1) {
            System.out.println("Uso correcto: interprete [archivo.txt]");

            // Convención defininida en el archivo "system.h" de UNIX
            System.exit(64);
        } else if(args.length == 1){
            ejecutarArchivo(args[0]);
        } else{
            ejecutarPrompt();
        }
    }
    //Ejecucion de programa por medio de la IDE
    private static void ejecutarArchivo(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        List <Statement> arbol = ejecutar(new String(bytes, Charset.defaultCharset()));

        // Se indica que existe un error
        if(existenErrores) System.exit(65);

        if(arbol != null){
            Semantico analizadorSemantico = new Semantico();
            analizadorSemantico.analizar(arbol, analizadorSemantico.tablaRaiz);
        }

    }

    //Ejecucion del programa por medio de PowerShell
    private static void ejecutarPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.print(">>> ");
            String linea = reader.readLine();
            if(linea == null) break; // Presionar Ctrl + D
            List<Statement> arbol = ejecutar(linea);
            existenErrores = false;

            if(arbol != null){
                Semantico analizadorSemantico = new Semantico();
                analizadorSemantico.analizar(arbol, analizadorSemantico.tablaRaiz);
            }
        }
    }

    private static List <Statement> ejecutar(String source) {
        try {
            Scanner scanner = new Scanner(source); //Analizador Lexico obtiene el texto desde el string
            List<Token> tokens = scanner.scan();

            // for(tokens.Token token : tokens){
            //     System.out.println(token);
            // }
            Parser parser = new ASDR(tokens); //Analizador Sintactico
            return parser.parse();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    El método error se puede usar desde las distintas clases
    para reportar los errores:
    Interprete.error(....);
     */
    public static void error(int linea, String posicion , String mensaje){
        reportar(linea, posicion, mensaje);
    }

    private static void reportar(int linea, String posicion, String mensaje){
        System.err.println(
                "[linea " + linea + "] Error " + posicion + ": " + mensaje
        );
        existenErrores = true;
    }


}
