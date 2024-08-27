import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
    	System.out.println("QUINE-MCCLUSKEY METHOD SIMULATION");
    	System.out.println("");

        while (true) {
        	System.out.println("=========================================================================");
        	System.out.println("");
            System.out.print("Enter minterms (space or comma separated): ");
            String minterms = sc.nextLine();

            System.out.print("Would you like to enter custom variables? (Enter \"Y\" to continue): ");
            String variableString = "";
            String variableInput = sc.nextLine();
            if (variableInput.toLowerCase().equals("y")) {
                System.out.print("Enter variables (space or comma separated): ");
                variableString = sc.nextLine();
            } else {
                System.out.println("Default variables used. (A, B, C, D, E, F, G, H, I, J)");
            }

            System.out.print("Would you like to enter don't care conditions? (Enter \"Y\" to continue): ");
            String dontCares = "";
            String dcInput = sc.nextLine();
            if (dcInput.toLowerCase().equals("y")) {
                System.out.print("Enter don't cares conditions (space or comma separated): ");
                dontCares = sc.nextLine();
            } else {
            	System.out.println("No don't care conditions entered.");
            }

            QuineMcCluskey s = new QuineMcCluskey(minterms, dontCares);

            System.out.println();
            s.solve();
            s.printResults(convertVariables(variableString));

            System.out.print("Do you want to continue? (Enter \"Y\" to continue): ");
            String continueInput = sc.nextLine();
            if (!continueInput.toLowerCase().equals("y")) {
                System.out.println("Program terminated.");
                break;// Exit the loop if the user enters "no"
            }
        }

        sc.close();
    }

    public static String[] convertVariables(String s){
        s = s.replace(",", " "); //for comma-delimited inputs

        String[] defaultVariables = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        if (s.trim().equals("")) { // if empty
            return defaultVariables;
        }

        String[] a = s.trim().split(" +");
        String[] t = new String[a.length]; // array of minterms

        for (int i = 0; i < t.length; i++) {
            try { // until it reaches outside bounds; sets default variables for the rest if not 10 variables
                defaultVariables[i] = a[i];
            } catch (Exception e) {
                throw new RuntimeException("Invalid input. Please try again.");
            }
        }
        return defaultVariables;
    }
}
