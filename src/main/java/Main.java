import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        List<String> builtins = List.of("echo", "exit", "type", "pwd", "cd");
        Path pathdirectory = Path.of(System.getProperty("user.dir"));

        System.out.print("$ ");
        String command = scanner.nextLine();

        while (!command.equals("exit")) {
            // 1. Manejo del builtin 'type'
            if (command.startsWith("type ")) {
                String commandType = command.substring(5);

                if (builtins.contains(commandType)) {
                    System.out.println(commandType + " is a shell builtin");
                } else {
                    String path = System.getenv("PATH");
                    if (path != null) {
                        String[] directories = path.split(File.pathSeparator);
                        boolean found = false;

                        for (String directory : directories) {
                            Path filePath = Path.of(directory, commandType);

                            if (Files.exists(filePath) && Files.isExecutable(filePath)) {
                                System.out.println(commandType + " is " + filePath);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            System.out.println(commandType + ": not found");
                        }
                    }
                }
            }
            // 2. Manejo del builtin 'echo'
            else if (command.startsWith("echo ")) {
                System.out.println(command.substring(5));
            }
            else if(command.equals("pwd")) {
                System.out.println(pathdirectory);
            }else if (command.startsWith("cd ")) {
                pathdirectory = Path.of(command.substring(3));
                if (Files.exists(pathdirectory) && Files.isExecutable(pathdirectory)) {
                    System.setProperty("user.dir", pathdirectory.toString());
                //}else if (Files.exists(pathdirectory)) {
                //    System.out.println("La ruta existe pero es un archivo, no un directorio");
                } else {
                    System.out.println("cd: /non-existing-directory: No such file or directory");
                }
            }
            // 3. Fallback: Si no es builtin, intentamos ejecutarlo como programa externo
            else if (!command.isBlank()) {
                // Separar el comando de sus argumentos (ej: "custom_exe arg1 arg2")
                String[] tokens = command.split(" ");
                String programName = tokens[0]; // El primer elemento es el programa

                String path = System.getenv("PATH");
                boolean found = false;

                if (path != null) {
                    String[] directories = path.split(File.pathSeparator);

                    for (String directory : directories) {
                        Path filePath = Path.of(directory, programName);

                        if (Files.exists(filePath) && Files.isExecutable(filePath)) {
                            found = true;

                            // ¡Aquí ocurre la magia de la ejecución!
                            // Le pasamos el arreglo 'tokens' completo al ProcessBuilder
                            ProcessBuilder pb = new ProcessBuilder(tokens);

                            // Conectamos la salida del programa a nuestra consola
                            pb.inheritIO();

                            // Iniciamos el subproceso y esperamos a que termine
                            Process process = pb.start();
                            process.waitFor();

                            break; // Salimos del for porque ya lo encontramos y ejecutamos
                        }
                    }
                }

                // Si recorrimos todo el PATH y no estaba:
                if (!found) {
                    System.out.println(command + ": command not found");
                }
            }
            System.out.print("$ ");
            command = scanner.nextLine();
        }
    }
}