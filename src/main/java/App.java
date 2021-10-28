import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lib.DAO.PatientDAO;
import lib.Models.Patient;
import java.util.Scanner;


public class App {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadStartMenu();
    }

    private static void loadStartMenu() {
        String menuOption;
        do {
            System.out.print("Pick an option\n[1] - Insert patient\t[2] - Generate XML with all patients\t[3] - Exit\nOption: ");
            menuOption = scanner.nextLine();

            switch (menuOption) {
                case "1" -> loadInsertMenu();
                case "2" -> {
                    PatientDAO.generateXML();
                    loadStartMenu();
                }
                case "3" -> System.exit(0);
                default -> System.out.println(ANSI_RED + "Invalid option" + ANSI_RESET);
            }

        } while (!menuOption.equalsIgnoreCase("1") && !menuOption.equalsIgnoreCase("2"));
    }

    private static void loadInsertMenu() {
        do {
            String name, birthDate, email, zipCode;
            System.out.println("Please fill up with patient data");
            System.out.print("Name: ");
            name = scanner.nextLine();
            System.out.print("Birth date (dd/mm/yyyy): ");
            birthDate = scanner.nextLine();
            System.out.print("Email: ");
            email = scanner.nextLine();

            //following code is to validate zip code entered using ViaCEP API.
            String address = "";
            boolean hasError = true;
            do {
                System.out.print("Zip code: ");
                zipCode = scanner.nextLine();
                if (zipCode.isBlank() || zipCode.length() != 8) {
                    System.out.println(ANSI_RED + "Invalid zip code format, please enter a valid one\n" + ANSI_RESET);
                    continue;
                }
                HttpResponse<JsonNode> request = Unirest.get("https://viacep.com.br/ws/" + zipCode + "/json/").asJson();
                JSONObject jsonObject = request.getBody().getObject();
                hasError = jsonObject.optBoolean("erro");
                if (hasError)
                    System.out.println(ANSI_RED + "\nNo address found with that zip code, please provide a valid one" + ANSI_RESET);
                else
                    address = "Logradouro: " + jsonObject.getString("logradouro") + ", Bairro: " + jsonObject.getString("bairro")
                            + ", Cidade: " + jsonObject.getString("localidade") + ", Estado: " + jsonObject.getString("uf");
            } while (hasError);

            Patient newPatient = new Patient(name, birthDate, email, address);
            PatientDAO.insertPatient(newPatient); //saves new patient in database
            System.out.println(ANSI_GREEN + "Patient inserted successfully" + ANSI_RESET);

            System.out.print("\nWould you like to create another patient? (Y|N): ");
        } while (scanner.nextLine().equalsIgnoreCase("y"));
        PatientDAO.generateXML();
        loadStartMenu();
    }

}
