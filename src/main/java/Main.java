import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static class ShellContext {
        Path currentDirectory = Path.of(System.getProperty("user.dir"));
        final String[] pathDirectories = System.getenv("PATH").split(File.pathSeparator);
        final Map<String, Command> registry = new HashMap<>();

        public Path findExecutableInPath(String commandName) {
            for (String dir : pathDirectories) {
                Path filePath = Path.of(dir, commandName);
                if (Files.isRegularFile(filePath) && Files.isExecutable(filePath)) {
                    return filePath;
                }
            }
            return null;
        }
    }

    public interface Command {

        boolean execute(String[] tokens, String rawInput, ShellContext context);
    }


    public static class ExitCommand implements Command {
        @Override
        public boolean execute(String[] tokens, String rawInput, ShellContext context) {
            return false; // Detiene el bucle principal
        }
    }

    public static class EchoCommand implements Command {
        @Override
        public boolean execute(String[] tokens, String rawInput, ShellContext context) {
            System.out.println(rawInput.substring(5));
            return true;
        }
    }

    public static class PwdCommand implements Command {
        @Override
        public boolean execute(String[] tokens, String rawInput, ShellContext context) {
            System.out.println(context.currentDirectory);
            return true;
        }
    }

    public static class CdCommand implements Command {
        @Override
        public boolean execute(String[] tokens, String rawInput, ShellContext context) {
            if (tokens.length < 2 || tokens[1].equals("~")) {
                context.currentDirectory = Paths.get(System.getenv("HOME"));
                return true;
            }

            String target = rawInput.substring(3).trim();
            Path newPath = context.currentDirectory.resolve(target).normalize();

            if (Files.isDirectory(newPath)) {
                context.currentDirectory = newPath;
            } else {
                System.out.println("cd: " + target + ": No such file or directory");
            }
            return true;
        }
    }

    public static class TypeCommand implements Command {
        @Override
        public boolean execute(String[] tokens, String rawInput, ShellContext context) {
            if (tokens.length < 2) return true;
            String targetCommand = tokens[1];

            if (context.registry.containsKey(targetCommand)) {
                System.out.println(targetCommand + " is a shell builtin");
                return true;
            }

            Path executablePath = context.findExecutableInPath(targetCommand);
            if (executablePath != null) {
                System.out.println(targetCommand + " is " + executablePath);
            } else {
                System.out.println(targetCommand + ": not found");
            }
            return true;
        }
    }

    public static void main(String[] args) {
        ShellContext context = new ShellContext();

        context.registry.put("exit", new ExitCommand());
        context.registry.put("echo", new EchoCommand());
        context.registry.put("pwd", new PwdCommand());
        context.registry.put("cd", new CdCommand());
        context.registry.put("type", new TypeCommand());

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            while (running) {
                System.out.print("$ ");
                String input = scanner.nextLine().trim();

                if (input.isBlank()) continue;

                String[] tokens = input.split("\\s+");
                String commandName = tokens[0];

                Command command = context.registry.get(commandName);

                if (command != null) {
                    running = command.execute(tokens, input, context);
                } else {
                    executeExternalCommand(tokens, context);
                }
            }
        }
    }

    private static void executeExternalCommand(String[] tokens, ShellContext context) {
        String programName = tokens[0];
        Path executablePath = context.findExecutableInPath(programName);

        if (executablePath == null) {
            System.out.println(programName + ": command not found");
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(tokens);
            pb.directory(context.currentDirectory.toFile());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            System.out.println("Error executing command: " + e.getMessage());
        }
    }
}