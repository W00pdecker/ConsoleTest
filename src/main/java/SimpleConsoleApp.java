
import java.sql.*;
import java.util.Random;

public class SimpleConsoleApp {
    static Connection connection;
    static Statement statement;
    static DatabaseMetaData dbm;
    private static final String url = "jdbc:mysql://localhost:3306/test";
    private static final String user = "root";
    private static final String pass = "S594mqer";


    public static void main(String[] args) {
        try {
        connection = DriverManager.getConnection(url, user, pass);
            statement = connection.createStatement();
            dbm = connection.getMetaData();
            doThing(args);
            connection.close();
        } catch (SQLException e) {
            System.out.println("SQLException occurred");
        }

    }

    // creating table
    private static void createTable() throws SQLException {
            String sql = "CREATE TABLE Persons" +
                    "(id INTEGER not NULL AUTO_INCREMENT, " +
                    " fullname VARCHAR(255), " +
                    " birth DATE, " +
                    " sex CHAR(1), " +
                    "PRIMARY KEY (id))";
            statement.executeUpdate(sql);
            System.out.println("Table created");
    }
    //inserting row in table with arguments as values
    private static void addSingleRow(String[] args) throws SQLException {
        String sql = "INSERT INTO Persons( FULLNAME, BIRTH, SEX) " +
                "VALUES ('" + args[1] + "', '"
                + args[2] + "', '"
                + args[3] + "')";
        statement.executeUpdate(sql);
        System.out.println("Row added");
    }

    // selecting rows with distinct values
    private static void selectUnics() throws SQLException {
        String sql = "SELECT DISTINCT  FULLNAME, BIRTH, SEX, ROUND(DATEDIFF(CURDATE(), BIRTH) / 365) AS AGE FROM persons order by FULLNAME";
        ResultSet result = statement.executeQuery(sql);
        ResultSetMetaData rsmd = result.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (result.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = result.getString(i);
                System.out.print(columnValue);
            }
            System.out.println();
        }
    }
    //filling table with one million random entries and one hundred of ordered ones
    private static void fillTable() throws SQLException {
        String[] maleNames = new String[] {"Alexander", "Boris", "Daniil", "Gennady", "Evgeny", "Felix", "Igor", "Leonid", "Mikhail", "Nikolai"};
        String[] maleSurnames = new String[] {"Pavlov", "Elkin", "Alchibaev", "Tarkovsky", "Karpov", "Pushkin", "Molchanov", "Parfenov", "Safonenkov", "Romanov"};
        String[] femaleNames = new String[] {"Alexandra", "Bella", "Daria", "Galina", "Evgeniya", "Fatima", "Ilona", "Larisa", "Mariya", "Nelly"};
        String[] femaleSurnames = new String[] {"Charina", "Rappoport", "Kogan", "Albina", "Chetvertak", "Davlyatshina", "Davydova", "Semenova", "Novik", "Uvarova"};
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            if (rand.nextBoolean()) {
                String sql = "INSERT INTO persons (fullname, birth, sex) VALUES ('"
                        + maleNames[(int) (Math.random() * 10)] + "_"
                        + maleSurnames[(int) (Math.random() * 10)]
                        + "', DATE_ADD('1900-01-01', INTERVAL FLOOR(RAND() * 36525) DAY), 'm')";
                statement.executeUpdate(sql);
            }
            else {
                String sql = "INSERT INTO persons (fullname, birth, sex) VALUES ('"
                        + femaleNames[(int) (Math.random() * 10)] + "_"
                        + femaleSurnames[(int) (Math.random() * 10)]
                        + "', DATE_ADD('1900-01-01', INTERVAL FLOOR(RAND() * 36525) DAY), 'w')";
                statement.executeUpdate(sql);
            }
        }
        for (int i = 0; i < 100; i++) {
            String sql = "INSERT INTO persons (fullname, birth, sex) VALUES ('"
                    + maleNames[5] + "_"
                    + maleSurnames[(int) (Math.random() * 10)]
                    + "', DATE_ADD('1900-01-01', INTERVAL FLOOR(RAND() * 36525) DAY), 'm')";
            statement.executeUpdate(sql);
        }
        System.out.println("Table filled with random stuff");
    }

    //executing query and noting time it took to execute
    private static void executeOrder66() throws SQLException {

            String sql = "SELECT DISTINCT  FULLNAME, BIRTH, SEX, ROUND(DATEDIFF(CURDATE(), BIRTH) / 365) AS AGE " +
                        "FROM persons  " +
                    "WHERE FULLNAME LIKE 'F%' AND SEX LIKE 'm' " +
                    "order by FULLNAME";
            long start = System.currentTimeMillis();
            ResultSet result = statement.executeQuery(sql);
            long end = System.currentTimeMillis();
            ResultSetMetaData rsmd = result.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (result.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = result.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println();
            }
            long duration = end - start;
            System.out.println("Execution time: " + duration + " ms");
    }

    //service method: dropping table
    private static void clear() throws SQLException {
        statement.executeUpdate("DROP table Persons");
        System.out.println("Table cleared");
    }

    //service method: checking is there table Persons
    private static boolean checkTable() throws SQLException {
        ResultSet tables = dbm.getTables(null, null, "Persons", null);
        return tables.next();
    }

    //this method processes the arguments by calling the desired method
    private static void doThing(String[] args) throws SQLException {
        switch (args[0]) {
            case "1" -> {if (!checkTable()) createTable();
            else System.out.println("Table already exists");}
            case "2" -> {if (checkTable()) addSingleRow(args);
            else System.out.println("Create table first");}
            case "3" -> {if (checkTable()) selectUnics();
            else System.out.println("Create table first");}
            case "4" -> {if (checkTable()) fillTable();
            else System.out.println("Create table first");}
            case "5" -> {if (checkTable()) executeOrder66();
            else System.out.println("Create table first");}
            case "clear" -> {if (checkTable()) clear();
            else System.out.println("There's no table");}
            default -> System.out.println("Invalid argument");
        }
    }
}


