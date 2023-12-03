import java.sql.*;


public class App {

    private final String databaseUrl="jdbc:postgresql://localhost/library";
    private final String username="new_user";
    private final String password="pass123";

    public Connection connectToDatabase() throws SQLException{
        System.out.println("Connection is established");
        return DriverManager.getConnection(databaseUrl,username,password);


    }

    public void closeDBConnection(Connection connection) throws SQLException {
        if (connection !=null && !connection.isClosed()){
            connection.close();
            System.out.println("Connection Closed.");


        }


    }
   
    

    public static void main(String[] args) throws Exception {
        
        App app=new App();
        Connection connection=app.connectToDatabase();

       


    

      
        




        app.closeDBConnection(connection);

    }
}
