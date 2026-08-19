import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        List<String> builtins = List.of("echo", "exit", "type");

        System.out.print("$ ");
        String command = scanner.nextLine();

        while (!command.equals("exit")) {

            if (command.startsWith("type ")) {
                String commandType = command.substring(5);

                if (builtins.contains(commandType)) {
                    System.out.println(commandType + " is a shell builtin");
                } else {
                    String path = System.getenv("PATH");
                    String[] directories = path.split(":");
                    boolean found = false;

                    for (String directory : directories) {
                        Path filePath = Path.of(directory, commandType);

                        if (Files.exists(filePath) && Files.isExecutable(filePath)) {
                            ProcessBuilder pb = new ProcessBuilder(directory, commandType);
                            Process process = pb.start();
                            System.out.println(commandType + " is " + filePath);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        System.out.println(commandType + ": not found");
                    }
                }

            } else if (command.startsWith("echo ")) {
                System.out.println(command.substring(5));

            } else {
                System.out.println(command + ": command not found");
            }

            System.out.print("$ ");
            command = scanner.nextLine();
        }
    }
}
