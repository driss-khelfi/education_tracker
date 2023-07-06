import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/students";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "msq.575:MRN!";
    private static boolean reverseOrder = false;

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            Statement statement = connection.createStatement();

            Scanner scanner = new Scanner(System.in);

            System.out.print("Connexion à la base de données réussie, appuyez sur entrée pour continuer");
            scanner.nextLine();

            boolean exit = false;
            while (!exit) {
                System.out.println("Options :");
                System.out.println("a. Ajouter une ligne");
                System.out.println("m. Modifier une ligne");
                System.out.println("s. Supprimer une ligne");
                System.out.println("l. Lire une ligne");
                System.out.println("d. Lire la base de donnée");
                System.out.println("r. Rechercher une ligne");
                System.out.println("t. Trier la base de donnée");
                System.out.println("c. Fermer la table");
                System.out.println("q. Quitter");

                String input = scanner.nextLine();

                switch (input.toLowerCase()) {
                    case "a":
                        addData(statement, scanner);
                        break;
                    case "m":
                        updateData(statement, scanner);
                        break;
                    case "s":
                        deleteData(statement, scanner);
                        break;
                    case "d":
                        displayData(statement);
                        break;
                    case "l":
                        readData(statement, scanner);
                        break;
                    case "r":
                        searchData(statement, scanner);
                        break;
                    case "t":
                        sortData(statement, scanner);
                        break;


                    case "c":
                        exit = true;
                        break;
                    case "q":
                        exit = true;
                        closeResources(connection, statement);
                        break;
                    default:
                        System.out.println("Option invalide.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addData(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("Ajout de données");

        // Demander à l'utilisateur de saisir les valeurs des colonnes
        System.out.print("Prénom : ");
        String firstName = scanner.nextLine();
        System.out.print("Nom : ");
        String lastName = scanner.nextLine();
        System.out.print("Âge : ");
        int age = scanner.nextInt();
        System.out.print("Notes : ");
        float grades = scanner.nextInt();
        scanner.nextLine(); // Consommer la fin de ligne

        // Préparer la requête paramétrée
        String query = "INSERT INTO students (first_name, last_name, age, grades) VALUES (?, ?, ?, ?)";
        java.sql.PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query);

        // Affecter les valeurs des paramètres
        preparedStatement.setString(1, firstName);
        preparedStatement.setString(2, lastName);
        preparedStatement.setInt(3, age);
        preparedStatement.setFloat(4, grades);

        // Exécuter la requête
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Ligne ajoutée avec succès !");
        } else {
            System.out.println("Échec de l'ajout de la ligne.");
        }
    }
    private static void readData(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("Lecture de données");

        // Demander à l'utilisateur de saisir l'ID de la ligne à lire
        System.out.print("ID de la ligne à lire : ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consommer la fin de ligne

        // Préparer la requête SELECT avec la clause WHERE
        String query = "SELECT * FROM students WHERE id = ?";
        java.sql.PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query);

        // Affecter la valeur du paramètre
        preparedStatement.setInt(1, id);

        // Exécuter la requête
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            // Récupérer les valeurs des colonnes de la ligne
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            int age = resultSet.getInt("age");
            String grades = resultSet.getString("grades");

            // Afficher les valeurs
            System.out.println("Prénom : " + firstName);
            System.out.println("Nom : " + lastName);
            System.out.println("Âge : " + age);
            System.out.println("Notes : " + grades);
        } else {
            System.out.println("Aucune ligne trouvée avec l'ID spécifié.");
        }

        resultSet.close();
    }



    private static void updateData(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("Mise à jour de données");

        // Demander à l'utilisateur de saisir l'ID de la ligne à modifier
        System.out.print("ID de la ligne à modifier : ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consommer la fin de ligne

        // Demander à l'utilisateur de saisir les nouvelles valeurs des colonnes
        System.out.print("Nouveau prénom : ");
        String newFirstName = scanner.nextLine();
        System.out.print("Nouveau nom : ");
        String newLastName = scanner.nextLine();
        System.out.print("Nouvel âge : ");
        int newAge = scanner.nextInt();
        System.out.print("Nouvelle note : ");
        float newGrades = scanner.nextFloat();
        scanner.nextLine(); // Consommer la fin de ligne

        // Préparer la requête paramétrée
        String query = "UPDATE students SET first_name = ?, last_name = ?, age = ?, grades = ? WHERE id = ?";
        java.sql.PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query);

        // Affecter les valeurs des paramètres
        preparedStatement.setString(1, newFirstName);
        preparedStatement.setString(2, newLastName);
        preparedStatement.setInt(3, newAge);
        preparedStatement.setFloat(4, newGrades);
        preparedStatement.setInt(5, id);

        // Exécuter la requête
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Ligne mise à jour avec succès !");
        } else {
            System.out.println("Échec de la mise à jour de la ligne.");
        }
    }


    private static void deleteData(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("Suppression de données");

        // Demander à l'utilisateur de saisir l'ID de la ligne à supprimer
        System.out.print("ID de la ligne à supprimer : ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consommer la fin de ligne

        // Préparer la requête DELETE avec la clause WHERE
        String query = "DELETE FROM students WHERE id = ?";
        java.sql.PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query);

        // Affecter la valeur du paramètre
        preparedStatement.setInt(1, id);

        // Exécuter la requête
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Ligne supprimée avec succès !");
        } else {
            System.out.println("Échec de la suppression de la ligne.");
        }
    }


    private static void displayData(Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM students");

        // Obtention du nombre de colonnes
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Affichage des en-têtes de colonne
        for (int i = 1; i <= columnCount; i++) {
            System.out.printf("%-15s", metaData.getColumnName(i));
        }
        System.out.println();

        // Affichage des lignes de données
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-15s", resultSet.getString(i));
            }
            System.out.println();
        }

        resultSet.close();
    }


    private static void searchData(Statement statement, Scanner scanner) throws SQLException {
        // Logique de recherche des données
    }

    private static void sortData(Statement statement, Scanner scanner) throws SQLException {
        // Logique de tri de donnée
        System.out.println("Options :");
        System.out.println("n. Noms par ordre alphabétique");
        System.out.println("p. Prénoms par ordre alphabétique");
        System.out.println("a. Âge par ordre croissant");
        System.out.println("g. Notes par ordre croissant");
        String input2 = scanner.nextLine();
        switch (input2.toLowerCase()) {
            case "p":
                System.out.print("Trier les prénoms dans l'ordre inverse ? (o pour oui /n pour non) : ");
                String reverseInput0 = scanner.nextLine();
                boolean reverseOrder0 = reverseInput0.equalsIgnoreCase("o");
                sortLastName(statement, scanner, reverseOrder0);

                break;
            case "n":
                System.out.print("Trier les noms dans l'ordre inverse ? (o pour oui /n pour non) : ");
                String reverseInput1 = scanner.nextLine();
                boolean reverseOrder1 = reverseInput1.equalsIgnoreCase("o");
                sortLastName(statement, scanner, reverseOrder1);
                break;
            case "a":
                System.out.print("Trier par age dans l'ordre décroissant ? (o pour oui /n pour non) : ");
                String reverseInput2 = scanner.nextLine();
                boolean reverseOrder2 = reverseInput2.equalsIgnoreCase("o");
                sortAge(statement, scanner, reverseOrder2);
                break;
            case "g":
                System.out.print("Trier par notes dans l'ordre décroissant ? (o pour oui /n pour non) : ");
                String reverseInput3 = scanner.nextLine();
                boolean reverseOrder3 = reverseInput3.equalsIgnoreCase("o");
                sortGrades(statement, scanner, reverseOrder3);
                break;
            default:
                System.out.println("Option invalide.");
                break;
        }
    }


    private static void sortFirstName(Statement statement, Scanner scanner) throws SQLException {
        // Logique de tri des prénoms par ordre alphabétique
        String order = reverseOrder ? "DESC" : "ASC";  // Détermine l'ordre du tri en fonction de l'option
        ResultSet resultSet = statement.executeQuery("SELECT * FROM students ORDER BY first_name, last_name, age");

        // Affichage des résultats du tri
        displaySortedData(resultSet);
    }

    private static void sortLastName(Statement statement, Scanner scanner, boolean reverseOrder) throws SQLException {
        String order = reverseOrder ? "DESC" : "ASC";  // Détermine l'ordre du tri en fonction de l'option

        String query = "SELECT * FROM students ORDER BY last_name " + order + ", first_name";

        ResultSet resultSet = statement.executeQuery(query);
        displaySortedData(resultSet);
    }



    private static void sortAge(Statement statement, Scanner scanner, boolean reverseOrder) throws SQLException {
        // Logique de tri des âges par ordre croissant
        String order = reverseOrder ? "DESC" : "ASC";  // Détermine l'ordre du tri en fonction de l'option
        ResultSet resultSet = statement.executeQuery("SELECT * FROM students ORDER BY age, last_name, first_name");

        // Affichage des résultats du tri
        displaySortedData(resultSet);

    }

    private static void sortGrades(Statement statement, Scanner scanner, boolean reverseOrder) throws SQLException {
        // Logique de tri des notes par ordre croissant
        String order = reverseOrder ? "DESC" : "ASC";  // Détermine l'ordre du tri en fonction de l'option
        ResultSet resultSet = statement.executeQuery("SELECT * FROM students ORDER BY grades, last_name, first_name");

        // Affichage des résultats du tri
        displaySortedData(resultSet);
    }

    private static void displaySortedData(ResultSet resultSet) throws SQLException {
        // Obtention du nombre de colonnes
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Affichage des en-têtes de colonne
        for (int i = 1; i <= columnCount; i++) {
            System.out.printf("%-15s", metaData.getColumnName(i));
        }
        System.out.println();

        // Affichage des lignes de données
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-15s", resultSet.getString(i));
            }
            System.out.println();
        }

        resultSet.close();
    }


    private static void closeResources(Connection connection, Statement statement) {
        try {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
