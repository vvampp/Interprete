package presentacion;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import scanner.Scanner;
import tokens.*;


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

    private static void ejecutarArchivo(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        ejecutar(new String(bytes, Charset.defaultCharset()));

        // Se indica que existe un error
        if(existenErrores) System.exit(65);
    }

    private static void ejecutarPrompt() throws IOException {
        // Se crea un lector de entrada
        InputStreamReader input = new InputStreamReader(System.in);
        // Se crea un buffer para leer la entrada
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.print(">>> ");
            // Se lee la linea de entrada
            String linea = reader.readLine();
            if(linea == null){
                break; // Presionar Ctrl + D
            }
            // Este caracter en necesario para el analisis en ciertos automatas
            linea += "\n";

            ejecutar(linea);
            existenErrores = false;
        }
    }

    private static void ejecutar(String source) {
        try{
            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.scan();

            // Se pausa el programa para que en caso de erorr
            // no se traslapen los mensajes de error y los tokens
            Thread.sleep(500); // Pausa el programa durante 1 segundo (1000 milisegundos)

            for(Token token : tokens){
                System.out.println(token);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    /*
    El método error se puede usar desde las distintas clases
    para reportar los errores:
    Interprete.error(....);
     */
    public static void error(int linea, String mensaje, String lexema){
        reportar(linea, lexema, mensaje);
    }

    private static void reportar(int linea, String posicion, String mensaje){
        System.err.println(
                "[linea " + linea + "] Error en el lexema [ " + posicion + " ]: " + mensaje
        );
        existenErrores = true;
    }

}
