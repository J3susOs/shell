import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
         System.out.print("$ ");

        Scanner input = new Scanner(System.in);

        String comandos = input.nextLine();

        System.out.println( comandos + ", command not found ");

        input.close();
    }
}
