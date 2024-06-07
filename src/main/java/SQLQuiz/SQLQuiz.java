package SQLQuiz;

import java.sql.*;
import java.util.Scanner;

public class SQLQuiz {

    public static void main(String[] args) throws ClassNotFoundException {

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/sql_quiz";
        String username = "root";
        String password = "rootroot";

        // Load Driver Class
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Get a Connection
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Scanner scanner = new Scanner(System.in);
            boolean quiz_ended = false;

            // Loop for quiz
            while (!quiz_ended) {
                System.out.println("SQL Quiz: ");
                System.out.println("1. Take Quiz | 2. Add question ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    System.out.println("What is your name?");
                    String userName = scanner.nextLine();
                    System.out.println("Choose a level. Easy, Medium, Hard?");
                    String levelChosen = scanner.nextLine().toLowerCase();
                    String readQuery = "SELECT * FROM quizzes WHERE level = ?";
                    int totalScore = 0;
                    try (PreparedStatement readStatement = connection.prepareStatement(readQuery)) {
                        readStatement.setString(1, levelChosen);
                        try (ResultSet resultSet = readStatement.executeQuery()) {
                            // Process result of data in table with level condition
                            while (resultSet.next()) {
                                System.out.println("Question: " + resultSet.getString("question"));
                                String userAnswer = scanner.nextLine();
                                String correctAnswer = resultSet.getString("answer");

                                // Compare user Answer to Correct Answer in table
                                if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                                    System.out.println("Correct Answer!");

                                    // Keep track of score
                                    totalScore += 1;
                                } else {
                                    System.out.println("Wrong. Correct Answer is: " + correctAnswer);
                                }
                            }
                        }

                        // After it goes through all questions, print total score and end game
                        System.out.println("Total Score: " + totalScore);
                        System.out.println("----------------------------");
                        storeScore(connection, userName, totalScore);
                        System.out.println("----------------------------");
                        displayScores(connection);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (choice == 2) {
                    addQuestion(connection, scanner);
                } else {
                    System.out.println("Can only pick 1 or 2.");
                    quiz_ended = true;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addQuestion(Connection connection, Scanner scanner) {
        // Adding questions
        System.out.println("What question do you want to add?");
        String question = scanner.nextLine();
        System.out.println("What is the answer?");
        String answer = scanner.nextLine();
        System.out.println("What is the difficulty level: Easy, Medium, Hard?");
        String level = scanner.nextLine().toLowerCase();

        String createQuery = "INSERT INTO quizzes(question, answer, level) VALUES(?, ?, ?)";
        try (PreparedStatement createStatement = connection.prepareStatement(createQuery)) {
            createStatement.setString(1, question);
            createStatement.setString(2, answer);
            createStatement.setString(3, level);
            createStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void storeScore(Connection connection, String name, int score) {

        String insertQuery = "INSERT INTO scores(name, score) VALUES (?, ?)";
        try (PreparedStatement createStatement = connection.prepareStatement(insertQuery)) {
            createStatement.setString(1, name);
            createStatement.setInt(2, score);
            createStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayScores (Connection connection) {
        String selectQuery = "SELECT name, score FROM scores ORDER BY score DESC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {
            System.out.println("Leaderboard:");
            int rank = 1;
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int score = resultSet.getInt("score");
                System.out.println(rank + ". " + name + " - " + score);
                rank++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}