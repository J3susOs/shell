import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage


        // Captures the user's command in the "command" variable
        Scanner scanner = new Scanner(System.in);
        //String command = scanner.nextLine();

        // Prints the "<command>: command not found" message
        //System.out.println(command + ": command not found");

        while (true) {
            System.out.print("$ ");
            String command = scanner.nextLine();

            // Si recibe el comando de exit el programa se cierra
            if (command.equals("exit ")) {
                System.exit(0);
            }

            if (command.startsWith("echo ")) {
                String texto = command.replace("echo ", "");
                System.out.println(texto);
            } else if (command.startsWith("type ") && (command.endsWith("echo") || command.endsWith("exit") || command.endsWith("type"))) {
                String a = command.replace("type ", "");
                System.out.println(a + " is a shell builtin");
            } else if (command.startsWith("type ")) {
                String a = command.replace("type ", "");
                System.out.println(command + ": not found");
            } else {
                System.out.println(command + ": command not found");
            }

        }

    }
}
