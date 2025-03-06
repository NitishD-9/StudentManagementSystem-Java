import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentManagementSystem extends JFrame {
    private final JTextField rollNoField;
    private final JTextField genderField;
    private final JTextField dobField;
    private Connection connection;
    private final JTextField removeField;
    private final JTextField searchField;

    private final JTextField nameField;
    private final JTextField ageField;
    private final JTextField courseField;
    private final JTextArea outputArea;

    public StudentManagementSystem() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:student.db");
            createStudentsTable();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        setTitle("Student Management System");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 1, 20, 25));
        inputPanel.add(new JLabel("Name: "));
        nameField = new JTextField();
        nameField.setBounds(10,10,10,10);
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Age: "));
        ageField = new JTextField();
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Course: "));
        courseField = new JTextField();
        inputPanel.add(courseField);
        inputPanel.add(new JLabel("Roll No: "));
        rollNoField = new JTextField();
        inputPanel.add(rollNoField);
        inputPanel.add(new JLabel("Gender: "));
        genderField = new JTextField();
        inputPanel.add(genderField);
        inputPanel.add(new JLabel("Date of Birth: "));
        dobField = new JTextField();
        inputPanel.add(dobField);
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> addStudent());
        inputPanel.add(addButton);

        outputArea = new JTextArea();

        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        outputArea.setSize(50,50);
        setVisible(true);

        //remove student
        JPanel removePanel = new JPanel();
        removePanel.setLayout(new FlowLayout());
        removePanel.add(new JLabel("Remove Student by Name: "));
        removeField = new JTextField(15);
        removePanel.add(removeField);
        JButton removeButton = new JButton("Remove");
        //@Override
        removeButton.addActionListener(e -> {
            String nameToRemove = removeField.getText().trim();
            if (!nameToRemove.isEmpty()) {
                removeStudent(nameToRemove);
            } else {
                JOptionPane.showMessageDialog(StudentManagementSystem.this,
                        "Please enter a valid name to remove.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        removePanel.add(removeButton);

        add(removePanel, BorderLayout.SOUTH);

        setVisible(true);

        //search student
        JPanel searchPanel=new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.add(new JLabel("Enter a Name "));
        searchField=new JTextField(15);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchField);

        searchButton.addActionListener(e -> {
            String nameToSearch = searchField.getText().trim();
            if (!nameToSearch.isEmpty()) {
                searchStudent(nameToSearch);
            } else {
                JOptionPane.showMessageDialog(StudentManagementSystem.this,
                        "Please enter a name to search.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        searchPanel.add(searchButton);
        add(searchPanel,BorderLayout.EAST);
        setVisible(true);
        //Display all students
        JPanel displayPanel=new JPanel();
        displayPanel.setLayout(new FlowLayout());
        JButton displayAllButton = new JButton("Display All Students");

        displayAllButton.addActionListener(e -> displayAllStudents());
        //displayAllButton.setBounds(60,60,60,60);
        displayPanel.add(displayAllButton);
        add(displayPanel, BorderLayout.WEST);
        setVisible(true);

    }

    private void addStudent() {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String course = courseField.getText();
        int rollNo = Integer.parseInt(rollNoField.getText());
        String gender = genderField.getText();
        String dob = dobField.getText();

        String sql = "INSERT INTO student (roll_no, name, age, gender, dob, course) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, rollNo);
            statement.setString(2, name);
            statement.setInt(3, age);
            statement.setString(4, gender);
            statement.setString(5, dob);
            statement.setString(6, course);
            statement.executeUpdate();

            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void removeStudent(String nameToRemove) {
        String sql = "DELETE FROM student WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nameToRemove);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                displayStudents();
                removeField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Student not found with the given name.",
                        "Student Not Found",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayStudents() {
        outputArea.setText("");
        String sql = "SELECT name, age, course FROM student ";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String course = resultSet.getString("course");
                outputArea.append("Name: " + name + "\n");
                outputArea.append("Age: " + age + "\n");
                outputArea.append("Course: " + course + "\n\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        courseField.setText("");
    }
    private void createStudentsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS student (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "roll_no INTEGER," +
                "name TEXT," +
                "age INTEGER," +
                "gender TEXT," +
                "dob TEXT," +
                "course TEXT)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void searchStudent(String nameToSearch){
        outputArea.setText("");
        String sql = "SELECT name, age, course, gender, dob, rollno  FROM student WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nameToSearch);
            ResultSet resultSet = statement.executeQuery();
            boolean found = false;
            while (resultSet.next()) {
                found = true;
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String course = resultSet.getString("course");
                String gender= resultSet.getString("gender");
                String dob=resultSet.getString("dob");
                int roll_no =resultSet.getInt("roll_no");
                outputArea.append("Name: " + name + "\n");
                outputArea.append("Age: " + age + "\n");
                outputArea.append("Course: " + course + "\n");
                outputArea.append("Gender: " +gender+"\n");
                outputArea.append("DOB: "+dob+"\n");
                outputArea.append("Roll no: "+roll_no+"\n");
            }
            if (!found) {
                outputArea.setText("No student found with the name: " + nameToSearch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void displayAllStudents() {
        outputArea.setText("");
        String sql = "SELECT * FROM student";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int rollNo = resultSet.getInt("roll_no");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                String dob = resultSet.getString("dob");
                String course = resultSet.getString("course");
                outputArea.append("Roll No: " + rollNo + "\n");
                outputArea.append("Name: " + name + "\n");
                outputArea.append("Age: " + age + "\n");
                outputArea.append("Gender: " + gender + "\n");
                outputArea.append("Date of Birth: " + dob + "\n");
                outputArea.append("Course: " + course + "\n\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentManagementSystem::new);
    }
}