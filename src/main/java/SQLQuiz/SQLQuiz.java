package SQLQuiz;

import java.sql.*;
import java.util.Scanner;

public class SQLQuiz {
    public static void main(String[] args) throws ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/sql_quiz";
        String username = "root";
        String password = "rootroot";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("SQL Quiz: ");
            System.out.println("1. Take Quiz | 2. Add question ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            boolean quiz_ended = false;
            while (!quiz_ended) {
                if (choice == 1) {
                    System.out.println("Choose a level. Easy, Medium, Hard?");
                    String levelChosen = scanner.nextLine().toLowerCase();
                    String readQuery = "SELECT * FROM quizzes WHERE level = ?";
                    int totalScore = 0;
                    try (PreparedStatement readStatement = connection.prepareStatement(readQuery)) {
                        readStatement.setString(1, levelChosen);
                        try (ResultSet resultSet = readStatement.executeQuery()) {
                            while (resultSet.next()) {
                                System.out.println("Question: " + resultSet.getString("question"));
                                String userAnswer = scanner.nextLine();
                                String correctAnswer = resultSet.getString("answer");
                                if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                                    System.out.println("Correct Answer!");
                                    totalScore += 1;
                                } else {
                                    System.out.println("Wrong. Correct Answer is: " + correctAnswer);
                                }
                            }
                        }
                        System.out.println("Total Score: " + totalScore);
                        quiz_ended = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (choice == 2) {
                    System.out.println("What question do you want to add?");
                    String question = scanner.nextLine();
                    System.out.println("What is the answer?");
                    String answer = scanner.nextLine();
                    System.out.println("What is the difficulty level?");
                    String level = scanner.nextLine();
                    String createQuery = "INSERT INTO quizzes(question, answer, level) VALUES(?, ?, ?)";
                    try (PreparedStatement createStatement = connection.prepareStatement(createQuery)) {
                        createStatement.setString(1, question);
                        createStatement.setString(2, answer);
                        createStatement.setString(3, level);
                        createStatement.executeUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("Can only pick 1 or 2.");
                    quiz_ended = true;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}