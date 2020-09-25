import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class View {

    private JFrame frame;

    private JLabel label;
    private JTextField username;

    private JTextArea chatArea;
    private JTextArea message;

    private JButton sendFileButton;
    private JButton sendButton;
    private JButton saveFileButton;
    private JButton loginButton;
    private JButton logoutButton;

    private JFileChooser fileChooser;
    private JLabel tempLabel;

    private File file;

    public View () {
        frame = new JFrame("Chat App");

        frame.setSize(520, 600);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        username = new JTextField();
        username.setBounds(50, 50, 300, 26);
        frame.add(username);

        loginButton = new JButton("Login");
        loginButton.setBounds(360, 50, 90, 25);
        frame.add(loginButton);

        label = new JLabel();
        label.setBounds(50,50,300,26);
        label.setFont(new Font("Sans Serif", Font.BOLD, 14));
        label.setVisible(false);
        frame.add(label);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(360, 50, 90, 25);
        logoutButton.setVisible(false);
        frame.add(logoutButton);

        chatArea = new JTextArea();
        chatArea.setLineWrap(true);
        chatArea.setEditable(false);

        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBounds(50, 90, 400, 320);
        frame.add(scroll);

        message = new JTextArea();
        message.setEditable(false);

        JScrollPane scroll2 = new JScrollPane(message);
        scroll2.setBounds(50, 425, 300, 56);
        message.setLineWrap(true);
        frame.add(scroll2);

        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        sendFileButton = new JButton("Send file");
        sendFileButton.setBounds(360, 425, 90, 25);
        sendFileButton.setEnabled(false);
        frame.add(sendFileButton);

        sendButton = new JButton("Send");
        sendButton.setBounds(360, 455, 90, 25);
        sendButton.setEnabled(false);
        frame.add(sendButton);

        tempLabel = new JLabel("");
        tempLabel.setBounds(50, 485, 380, 25);
        frame.add(tempLabel);

        saveFileButton = new JButton("Save file");
        saveFileButton.setBounds(360, 485, 90, 25);
        saveFileButton.setEnabled(false);
        frame.add(saveFileButton);

        frame.revalidate();
        frame.repaint();
    }

    public String getMessage() {
        return message.getText();
    }

    public String getChatArea() {
        return chatArea.getText();
    }

    public String getUsername() {
        return this.username.getText();
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setChatArea(String text) {
        this.chatArea.setText(text);
    }

    public void setTemp(String text) {
        tempLabel.setText(text);

        if(text == null) {
            saveFileButton.setEnabled(false);
        }
        else {
            saveFileButton.setEnabled(true);
        }
    }

    public void sendFile() {
        int r = fileChooser.showOpenDialog(null);
        file = fileChooser.getSelectedFile();

        if(r == JFileChooser.APPROVE_OPTION) {
            setTemp(file.getName());
        }
    }

    public File getFile() {
        return new File(fileChooser.getCurrentDirectory(), fileChooser.getSelectedFile().getName());
    }

    public void saveFile() {
        int r = fileChooser.showSaveDialog(null);

        if(r == JFileChooser.APPROVE_OPTION) {
            file = getFile();
            message("File saved to " + file.getAbsolutePath());
        }
    }

    public void login() {
        String username = getUsername();
        this.username.setText(username);

        this.username.setVisible(false);
        this.loginButton.setVisible(false);

        this.label.setText("Welcome " + username);
        this.label.setVisible(true);
        this.logoutButton.setVisible(true);

        this.message.setEditable(true);
        this.sendFileButton.setEnabled(true);
        this.sendButton.setEnabled(true);
    }

    public void logout() {
        this.label.setText("Welcome ");
        this.label.setVisible(false);

        this.username.setVisible(true);
        this.username.setText("");
        this.loginButton.setVisible(true);

        this.chatArea.setText("");
        this.message.setText("");
        this.message.setEditable(false);
        this.tempLabel.setText("");
        this.sendFileButton.setEnabled(false);
        this.sendButton.setEnabled(false);
        this.saveFileButton.setEnabled(false);
    }

    public void message(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    public void addButtonActionListener(ActionListener listener) {
        sendFileButton.addActionListener(listener);
        sendButton.addActionListener(listener);
        saveFileButton.addActionListener(listener);
        loginButton.addActionListener(listener);
        logoutButton.addActionListener(listener);
    }
}