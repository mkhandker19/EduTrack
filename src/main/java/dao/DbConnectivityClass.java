package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import model.Person;
import service.MyLogger;

import java.sql.*;
public class DbConnectivityClass {
    final static String DB_NAME="CSC311_BD_TEMP";
        MyLogger lg= new MyLogger();
    final static String SQL_SERVER_URL = "jdbc:mysql://csc311khandkerserver.mysql.database.azure.com";//update this server name
    private static String csc311khandkerserver;
    final static String DB_URL = SQL_SERVER_URL + "/" + DB_NAME; //update this database name
        final static String USERNAME = "csc311admin";// update this username
        final static String PASSWORD = "farmingdale24@";// update this password


        private final ObservableList<Person> data = FXCollections.observableArrayList();

        // Method to retrieve all data from the database and store it into an observable list to use in the GUI tableview.


        public ObservableList<Person> getData() {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users ";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.isBeforeFirst()) {
                    lg.makeLog("No data");
                }
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String department = resultSet.getString("department");
                    String major = resultSet.getString("major");
                    String email = resultSet.getString("email");
                    String imageURL = resultSet.getString("imageURL");
                    data.add(new Person(id, first_name, last_name, department, major, email, imageURL));
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return data;
        }


        public boolean connectToDatabase() {
            boolean hasRegistredUsers = false;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                //First, connect to MYSQL server and create the database if not created
                Connection conn = DriverManager.getConnection(SQL_SERVER_URL, USERNAME, PASSWORD);
                Statement statement = conn.createStatement();
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS "+DB_NAME+"");
                statement.close();
                conn.close();

                //Second, connect to the database and create the table "users" if cot created
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                statement = conn.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS users (" + "id INT( 10 ) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                        + "first_name VARCHAR(200) NOT NULL," + "last_name VARCHAR(200) NOT NULL,"
                        + "department VARCHAR(200),"
                        + "major VARCHAR(200),"
                        + "email VARCHAR(200) NOT NULL UNIQUE,"
                        + "imageURL VARCHAR(200))";
                statement.executeUpdate(sql);

                //check if we have users in the table users
                statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

                if (resultSet.next()) {
                    int numUsers = resultSet.getInt(1);
                    if (numUsers > 0) {
                        hasRegistredUsers = true;
                    }
                }

                statement.close();
                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return hasRegistredUsers;
        }

        public void queryUserByLastName(String name) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users WHERE last_name = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, name);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String major = resultSet.getString("major");
                    String department = resultSet.getString("department");

                    lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                            + ", Major: " + major + ", Department: " + department);
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void listAllUsers() {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users ";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String department = resultSet.getString("department");
                    String major = resultSet.getString("major");
                    String email = resultSet.getString("email");

                    lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                            + ", Department: " + department + ", Major: " + major + ", Email: " + email);
                }

                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void insertUser(Person person) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "INSERT INTO users (first_name, last_name, department, major, email, imageURL) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, person.getFirstName());
                preparedStatement.setString(2, person.getLastName());
                preparedStatement.setString(3, person.getDepartment());
                preparedStatement.setString(4, person.getMajor());
                preparedStatement.setString(5, person.getEmail());
                preparedStatement.setString(6, person.getImageURL());
                int row = preparedStatement.executeUpdate();
                if (row > 0) {
                    lg.makeLog("A new user was inserted successfully.");
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void editUser(int id, Person p) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "UPDATE users SET first_name=?, last_name=?, department=?, major=?, email=?, imageURL=? WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, p.getFirstName());
                preparedStatement.setString(2, p.getLastName());
                preparedStatement.setString(3, p.getDepartment());
                preparedStatement.setString(4, p.getMajor());
                preparedStatement.setString(5, p.getEmail());
                preparedStatement.setString(6, p.getImageURL());
                preparedStatement.setInt(7, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void deleteRecord(Person person) {
            int id = person.getId();
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "DELETE FROM users WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        //Method to retrieve id from database where it is auto-incremented.
        public int retrieveId(Person p) {
            connectToDatabase();
            int id;
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT id FROM users WHERE email=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, p.getEmail());

                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                id = resultSet.getInt("id");
                preparedStatement.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            lg.makeLog(String.valueOf(id));
            return id;
        }
    public boolean isUsernameExists(String username) {
        connectToDatabase(); // Ensure this initializes the database connection
        try {
            // Establish a connection to the database
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            // SQL query to check for existing username
            String sql = "SELECT COUNT(*) FROM users WHERE first_name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);

            // Execute the query and check the result
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean exists = false;
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0; // Check if any record exists
            }

            // Close resources
            preparedStatement.close();
            conn.close();

            return exists; // Return true if username exists, false otherwise
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of an error
        }
    }
    public boolean registerUser(String username, String password) {
        connectToDatabase();
        try {
            // Check if the username already exists
            if (isUsernameExists(username)) {
                lg.makeLog("Username already exists: " + username);
                return false;
            }

            // Establish connection to the database
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            // SQL query to insert new user
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password); // Directly storing password (not hashed)

            // Execute the query and check if it was successful
            int rowsAffected = preparedStatement.executeUpdate();

            // Close resources
            preparedStatement.close();
            conn.close();

            if (rowsAffected > 0) {
                lg.makeLog("User registered successfully: " + username);
                return true;
            } else {
                lg.makeLog("Failed to register user: " + username);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of any error
        }
    }
    public boolean validateLogin(String username, String password) {
        connectToDatabase(); // Ensure database connection

        try {
            // Establish a connection to the database
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            // SQL query to check if the username and password match
            String sql = "SELECT * FROM users WHERE first_name = ? AND password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isValidUser = resultSet.next(); // If a record is found, the login is valid

            // Close resources
            resultSet.close();
            preparedStatement.close();
            conn.close();

            return isValidUser;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there's an error
        }
    }
}