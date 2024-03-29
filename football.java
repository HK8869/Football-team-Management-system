import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class football extends JFrame {

    private JTextField nameField, ageField, heightField, weightField, experienceField;

    public football() {
        super("FOOTBALL");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        // Create components
        nameField = new JTextField(20);
        ageField = new JTextField(5);
        heightField = new JTextField(5);
        weightField = new JTextField(5);
        experienceField = new JTextField(10);

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertData();
            }
        });

        JButton viewButton = new JButton("View");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewData();
            }
        });

        JButton modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyData();
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteData();
            }
        });

        // Create layout
        setLayout(new GridLayout(7, 2));

        // Add components to the layout
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Age:"));
        add(ageField);
        add(new JLabel("Height (cm):"));
        add(heightField);
        add(new JLabel("Weight (kg):"));
        add(weightField);
        add(new JLabel("Experience (years):"));
        add(experienceField);
        add(insertButton);
        add(viewButton);
        add(modifyButton);
        add(deleteButton);

        pack();
    }

    private void insertData() {
        String url = "jdbc:mysql://localhost:3306/fms";
        String username = "root";
        String password = "thinkpad";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "INSERT INTO players (Pname, age, height, weight, experience, team_name) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nameField.getText());
                preparedStatement.setInt(2, Integer.parseInt(ageField.getText()));
                preparedStatement.setDouble(3, Double.parseDouble(heightField.getText()));
                preparedStatement.setDouble(4, Double.parseDouble(weightField.getText()));
                preparedStatement.setString(5, experienceField.getText());
                preparedStatement.setString(6, "");

                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data inserted successfully!");
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void viewData() {
        String url = "jdbc:mysql://localhost:3306/fms";
        String username = "root";
        String password = "thinkpad";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT Pname, age, height, weight, experience FROM players";
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);

                // Create a new JFrame for displaying the data in a table
                JFrame viewFrame = new JFrame("Player Data");
                JTable table = new JTable(buildTableModel(resultSet));

                // Add the JTable to the viewFrame
                JScrollPane scrollPane = new JScrollPane(table);
                viewFrame.add(scrollPane);

                // Set properties for the viewFrame
                viewFrame.setSize(600, 400);
                viewFrame.setLocationRelativeTo(null);
                viewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                viewFrame.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void modifyData() {
        String url = "jdbc:mysql://localhost:3306/fms";
        String username = "root";
        String password = "thinkpad";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String nameToUpdate = JOptionPane.showInputDialog(this, "Enter the name to modify:");
            String query = "SELECT * FROM players WHERE Pname = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                preparedStatement.setString(1, nameToUpdate);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Player found, allow modification
                    String newName = JOptionPane.showInputDialog(this, "Enter the new name:", resultSet.getString("Pname"));
                    int newAge = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter the new age:", resultSet.getInt("age")));
                    double newHeight = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter the new height:", resultSet.getDouble("height")));
                    double newWeight = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter the new weight:", resultSet.getDouble("weight")));
                    String newExperience = JOptionPane.showInputDialog(this, "Enter the new experience:", resultSet.getString("experience"));

                    // Update the record
                    resultSet.updateString("Pname", newName);
                    resultSet.updateInt("age", newAge);
                    resultSet.updateDouble("height", newHeight);
                    resultSet.updateDouble("weight", newWeight);
                    resultSet.updateString("experience", newExperience);
                    resultSet.updateRow();
                    JOptionPane.showMessageDialog(this, "Data modified successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Player not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteData() {
        String url = "jdbc:mysql://localhost:3306/fms";
        String username = "root";
        String password = "thinkpad";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String nameToDelete = JOptionPane.showInputDialog(this, "Enter the name to delete:");
            String query = "SELECT * FROM players WHERE Pname = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                preparedStatement.setString(1, nameToDelete);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Player found, allow deletion
                    int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + nameToDelete + "?");
                    if (option == JOptionPane.YES_OPTION) {
                        // Delete the record
                        resultSet.deleteRow();
                        JOptionPane.showMessageDialog(this, "Data deleted successfully!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Player not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Convert ResultSet to DefaultTableModel for JTable
    private static DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        // Create column names
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }

        // Create data for JTable
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        while (resultSet.next()) {
            Object[] rowData = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                rowData[i - 1] = resultSet.getObject(i);
            }
            tableModel.addRow(rowData);
        }

        return tableModel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            football app = new football();
            app.setVisible(true);
        });
    }
}
