package SQLQuiz;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class SQLQuizTest {

    private static Connection connection;

    @BeforeAll
    static void setup() throws ClassNotFoundException, SQLException {
        // Load Driver Class
        Class.forName("com.mysql.cj.jdbc.Driver");

        try {
            // Create a connection to the MySQL database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sql_quiz", "root", "rootroot");

            // Create tables for testing if they do not exist
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS quizzes (id INT AUTO_INCREMENT PRIMARY KEY, question VARCHAR(255), answer VARCHAR(255), level VARCHAR(50))");
                stmt.execute("CREATE TABLE IF NOT EXISTS scores (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), score INT)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAddQuestion() {
        Scanner scanner = new Scanner("Sample Question\nSample Answer\nEasy\n");
        SQLQuiz.addQuestion(connection, scanner);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM quizzes WHERE question='Sample Question'")) {
            assertTrue(rs.next());
            assertEquals("Sample Question", rs.getString("question"));
            assertEquals("Sample Answer", rs.getString("answer"));
            assertEquals("easy", rs.getString("level"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testStoreScore() {
        SQLQuiz.storeScore(connection, "TestUser", 10);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM scores WHERE name='TestUser'")) {
            assertTrue(rs.next());
            assertEquals("TestUser", rs.getString("name"));
            assertEquals(10, rs.getInt("score"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}