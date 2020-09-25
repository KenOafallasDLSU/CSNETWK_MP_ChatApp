import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import static java.lang.Integer.parseInt;

public class View {

    private JFrame frame;

    private JLabel userLabel;
    private JTextField username;

    private JLabel portLabel;
    private JTextField port;

    private JTextArea chatArea;
    private JTextArea message;

    private JButton sendFileButton;
    private JButton sendButton;
    private JButton saveFileButton;
    private JButton loginButton;
    private JButton logoutButton;

    private JFileChooser fileChooser;
    private JLabel fileLabel;

    private File file;
    // private String path;

    public View () {
        frame = new JFrame("De La Salle Usap");

        frame.setSize(520, 600);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        username = new JTextField();
        username.setBounds(50, 55, 200, 26);
        frame.add(username);

        port = new JTextField();
        port.setBounds(260, 55, 90, 26);
        frame.add(port);

        loginButton = new JButton("Login");
        loginButton.setBounds(360, 55, 90, 25);
        frame.add(loginButton);

        userLabel = new JLabel("Username");
        userLabel.setBounds(50,27,200,26);
        userLabel.setFont(new Font("Sans Serif", Font.BOLD, 13));
        frame.add(userLabel);

        portLabel = new JLabel("Port");
        portLabel.setBounds(260, 27, 90, 26);
        portLabel.setFont(new Font("Sans Serif", Font.BOLD, 13));
        frame.add(portLabel);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(360, 55, 90, 25);
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
        scroll2.setBounds(50, 420, 300, 56);
        message.setLineWrap(true);
        frame.add(scroll2);

        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        sendFileButton = new JButton("Send file");
        sendFileButton.setBounds(360, 420, 90, 25);
        sendFileButton.setEnabled(false);
        frame.add(sendFileButton);

        sendButton = new JButton("Send");
        sendButton.setBounds(360, 450, 90, 25);
        sendButton.setEnabled(false);
        frame.add(sendButton);

        fileLabel = new JLabel("");
        fileLabel.setBounds(50, 480, 380, 25);
        frame.add(fileLabel);

        saveFileButton = new JButton("Save file");
        saveFileButton.setBounds(360, 480, 90, 25);
        saveFileButton.setVisible(false);
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

    public int getPort() {
        int temp = -1;
        try {
            temp = Integer.parseInt(this.port.getText());
            // System.out.println(temp);
            return temp;
        }
        catch(Exception e) {
            // System.out.println("fail: " + temp);
            return temp;
        }
//        return parseInt(this.port.getText());
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setChatArea(String text) {
        this.chatArea.setText(text);
    }

    public void setFileLabel(String text) {
        fileLabel.setText(text);

        if(text == null) {
            saveFileButton.setVisible(false);
            saveFileButton.setEnabled(false);
        }
        else {
            saveFileButton.setVisible(true);
            saveFileButton.setEnabled(true);
        }
    }

    // public String getPath() {
    //     return path;
    // }

    // public void setPath(String path) {
    //     this.path = path;
    // }

    public void sendFile() {
        int r = fileChooser.showOpenDialog(null);
        file = fileChooser.getSelectedFile();

        if(r == JFileChooser.APPROVE_OPTION) {
            setFileLabel(file.getName());
        }
    }

    public File getFile() {
        return new File(fileChooser.getCurrentDirectory(), fileChooser.getSelectedFile().getName());
    }

    public void saveFile() {
        int r = fileChooser.showSaveDialog(null);

        if(r == JFileChooser.APPROVE_OPTION) {
            file = getFile();

            // setPath(file.getAbsolutePath());
            setFile(file);

            // message("File saved to " + file.getAbsolutePath());
        }
    }

    public void login() {
        String username = getUsername();
        this.username.setText(username);

        this.username.setEditable(false);
        this.port.setEditable(false);
//        this.username.setVisible(false);
        this.loginButton.setVisible(false);

        this.userLabel.setText("Welcome " + username);
        this.userLabel.setVisible(true);
        this.portLabel.setVisible(false);
        this.logoutButton.setVisible(true);

        this.message.setEditable(true);
        this.sendFileButton.setEnabled(true);
        this.sendButton.setEnabled(true);
    }

    public void logout() {
        this.userLabel.setText("Username");
        this.portLabel.setText("Port");
        this.portLabel.setVisible(true);

        this.username.setEditable(true);
        this.username.setText("");
        this.port.setEditable(true);
        this.port.setText("");
        this.loginButton.setVisible(true);
        this.logoutButton.setVisible(false);

        this.chatArea.setText("");
        this.message.setText("");
        this.message.setEditable(false);
        this.fileLabel.setText("");
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