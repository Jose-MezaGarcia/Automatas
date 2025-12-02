import java.util.*;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== MENÚ ===");
            System.out.println("1. Crear AFD");
            System.out.println("2. Crear AFN");
            System.out.println("3. Gramática Regular");
            System.out.println("4. Gramática Libre de Contexto (GLC)");
            System.out.println("5. Autómata de Pila (AP)");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");
            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1:
                    AFD.ejecutar(sc);
                    break;

                case 2:
                    AFN.ejecutar(sc);
                    break;

                case 3:
                    MenuGR.ejecutar(sc);
                    break;

                case 4:
                    MenuGLC.ejecutar(sc);
                    break;

                case 5:
                    MenuAP.ejecutar(sc);
                    break;

                case 6:
                    System.out.println("Saliendo...");
                    return;

                default:
                    System.out.println("Opción inválida.");
            }
        }
    }
}
