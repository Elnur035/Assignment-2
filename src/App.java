
import java.sql.*;


public class App {

    private final String databaseUrl = "jdbc:postgresql://localhost/library";
    private final String username = "new_user";
    private final String password = "pass123";

    public Connection connectToDatabase() throws SQLException {
        System.out.println("Connection is established");
        return DriverManager.getConnection(databaseUrl, username, password);
    }

    public void closeDBConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection Closed.");
        }
    }

    public void executeSQLQuery(Connection connection, String query) throws SQLException {
        Statement statement = connection.createStatement();

        if (query.trim().toLowerCase().startsWith("select")) {
            ResultSet resultSet = statement.executeQuery(query);
            displayResultSet(resultSet);
        } else {
            int rowsAffected = statement.executeUpdate(query);
            System.out.println(rowsAffected + " row(s) affected.");
        }

        statement.close();
    }

    
    private void displayResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(resultSet.getString(i) + "\t");
            }
            System.out.println();
        }
    }

    //transaction management

    
public void placeOrder(Connection connection,int id, int customerId, int bookId, Date orderDate, double cost) throws SQLException {
    // Check if stock is available
    if (isStockAvailable(connection, bookId)) {
        // If stock is available, proceed with the order
        String insertOrderQuery = "INSERT INTO Orders (id,customer_id, book_id, date, cost) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertOrderStatement = connection.prepareStatement(insertOrderQuery)) {
            insertOrderStatement.setInt(1, id);
            insertOrderStatement.setInt(2, customerId);
            insertOrderStatement.setInt(3, bookId);
            insertOrderStatement.setDate(4, orderDate);
            insertOrderStatement.setDouble(5, cost);

            int rowsAffected = insertOrderStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Order placed successfully.");

                // Update available stock
                updateAvailableStock(connection, bookId);
            }
        }
    } else {
        System.out.println("Sorry, the selected book is out of stock.");
    }
}

private boolean isStockAvailable(Connection connection, int bookId) throws SQLException {
    String selectStockQuery = "SELECT available_stock FROM Books WHERE id = ?";
    try (PreparedStatement selectStockStatement = connection.prepareStatement(selectStockQuery)) {
        selectStockStatement.setInt(1, bookId);
        ResultSet resultSet = selectStockStatement.executeQuery();

        if (resultSet.next()) {
            int availableStock = resultSet.getInt("available_stock");
            return availableStock > 0;
        }

        return false;
    }
}

private void updateAvailableStock(Connection connection, int bookId) throws SQLException {
    String updateStockQuery = "UPDATE Books SET available_stock = available_stock - 1 WHERE id = ?";
    try (PreparedStatement updateStockStatement = connection.prepareStatement(updateStockQuery)) {
        updateStockStatement.setInt(1, bookId);
        updateStockStatement.executeUpdate();
    }
}


// metadata


public void displayDatabaseInfo(Connection connection) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    System.out.println("Database Name: " + metaData.getDatabaseProductName());
    System.out.println("Database Version: " + metaData.getDatabaseProductVersion());

    ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
    while (tables.next()) {
        String tableName = tables.getString("TABLE_NAME");
        System.out.println("Table Name: " + tableName);
        displayTableInfo(connection, tableName);
    }
}

private void displayTableInfo(Connection connection, String tableName) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();

    // Display table columns
    ResultSet columns = metaData.getColumns(null, null, tableName, null);
    System.out.println("Columns for table " + tableName + ":\n");

    while (columns.next()) {
        String columnName = columns.getString("COLUMN_NAME");
        String columnType = columns.getString("TYPE_NAME");
        System.out.println("Column: " + columnName + ", Type: " + columnType);
    }

    // Display primary key information
    ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
    System.out.println("Primary Key(s) for table " + tableName + ":\n");

    while (primaryKeys.next()) {
        String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
        System.out.println("Primary Key Column: " + primaryKeyColumnName);
    }

    // Display foreign key information
    ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);
    System.out.println("Foreign Key(s) referencing table " + tableName + ":");

    while (foreignKeys.next()) {
        String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
        String pkTableName = foreignKeys.getString("PKTABLE_NAME");
        String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");

        System.out.println("Foreign Key Column: " + fkColumnName);
        System.out.println("Referencing Primary Key in Table: " + pkTableName);
        System.out.println("Referencing Primary Key Column: " + pkColumnName);
        System.out.println();
    }
}



    public static void main(String[] args) throws Exception {
        App app=new App();
        Connection connection=app.connectToDatabase();

        String query = "CREATE TABLE Authors(" +
       "id INTEGER PRIMARY KEY," +
       "first_name VARCHAR(255)," +
       "second_name VARCHAR(255)" +
       ");";

       String insertQuery = "INSERT INTO Authors (id, first_name, second_name) VALUES\n" +
        "(1, 'John', 'Doe')," +
        "(2, 'Jane', 'Smith')," +
        "(3, 'Robert', 'Johnson');";

        String select_query="select * from authors;";

        String createBooksTableQuery = "CREATE TABLE Books ("
        + "id INTEGER PRIMARY KEY,"
        + "name VARCHAR(255),"
        + "publication_date DATE,"
        + "price NUMERIC,"
        + "available_stock INTEGER,"
        + "rate NUMERIC,"
        + "author_id INTEGER,"
        + "FOREIGN KEY (author_id) REFERENCES Authors(id)"
        + ");";

        String insertBooksQuery = "INSERT INTO Books (id, name, publication_date, price, available_stock, rate, author_id) VALUES\n" +
        "(1, 'Book1', '2022-01-01', 19.99, 50, 4.5, 1)," +
        "(2, 'Book2', '2022-02-15', 24.99, 30, 4.0, 2)," +
        "(3, 'Book3', '2022-03-20', 14.99, 3, 4.8, 3)," +
        "(4, 'Book4', '2022-04-10', 29.99, 2, 4.2, 1);";


        String createCustomersTableQuery = "CREATE TABLE Customers (" +
        "id INTEGER PRIMARY KEY," +
        "first_name VARCHAR(255)," +
        "second_name VARCHAR(255)," +
        "street VARCHAR(255)," +
        "city VARCHAR(255)," +
        "phone_number VARCHAR(255)," +
        "postal_code VARCHAR(255)," +
        "email VARCHAR(255)" +
        ");";

        String insertCustomersQuery = "INSERT INTO Customers (id, first_name, second_name, street, city, phone_number, postal_code, email) VALUES" +
        "(1, 'Alice', 'Johnson', '123 Main St', 'Cityville', '555-1234', '12345', 'alice@email.com')," +
        "(2, 'Bob', 'Miller', '456 Oak St', 'Townburg', '555-5678', '67890', 'bob@email.com')," +
        "(3, 'Charlie', 'Smith', '789 Pine St', 'Villagetown', '555-9876', '98765', 'charlie@email.com');";


        String createOrdersTableQuery = "CREATE TABLE Orders (" +
                "id INTEGER PRIMARY KEY," +
                "customer_id INTEGER," +
                "book_id INTEGER," +
                "date DATE," +
                "cost NUMERIC," +
                "FOREIGN KEY (customer_id) REFERENCES Customers(id)," +
                "FOREIGN KEY (book_id) REFERENCES Books(id)"+
                ");";

                String insertOrdersQuery ="INSERT INTO Orders (id, customer_id, book_id, date, cost) VALUES " +
                "(1, 1, 1, '2022-05-01', 19.99), " +
                "(2, 2, 3, '2022-05-05', 14.99), " +
                "(3, 3, 2, '2022-05-10', 24.99), " +
                "(4, 1, 4, '2022-05-15', 29.99);";

        String update_order="update Orders set customer_id=2 where id=4;";

        String select_order="select * from orders";

        String delete_order="delete from orders where id=4;";

    
        //transaction management

        // let's add new order to see if stock is avaiable to make order or not
        int id=4;
        int customerId = 3; 
        int bookId = 4; 
        Date orderDate = Date.valueOf("2023-12-02"); 
        double cost = 24.99;


        //app.placeOrder(connection,id,customerId,bookId,orderDate,cost);

       //app.executeSQLQuery(connection, "select * from books;");

       app.displayDatabaseInfo(connection);


        app.closeDBConnection(connection);

    }
}
