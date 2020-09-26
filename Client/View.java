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

    private JLabel ipLabel;
    private JTextField ip;

    private JLabel portLabel;
    private JTextField port;

    private JTextArea chatArea;
    private JTextArea message;

    private JButton sendFileButton;
    private JButton sendButton;
    private JButton loginButton;
    private JButton logoutButton;

    private JFileChooser fileChooser;
    private File file;

    public View () {
        frame = new JFrame("De La Salle Usap");

        frame.setSize(520, 620);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        username = new JTextField();
        username.setBounds(50, 55, 300, 26);
        frame.add(username);

        ip = new JTextField();
        ip.setBounds(50, 110, 200, 26);
        frame.add(ip);

        port = new JTextField();
        port.setBounds(260, 110, 190, 26);
        frame.add(port);

        loginButton = new JButton("Login");
        loginButton.setBounds(360, 55, 90, 25);
        frame.add(loginButton);

        userLabel = new JLabel("Username");
        userLabel.setBounds(50,30,200,26);
        userLabel.setFont(new Font("Sans Serif", Font.BOLD, 13));
        frame.add(userLabel);

        ipLabel = new JLabel("IP Address");
        ipLabel.setBounds(50, 85, 200, 26);
        ipLabel.setFont(new Font("Sans Serif", Font.BOLD, 13));
        frame.add(ipLabel);

        portLabel = new JLabel("Port");
        portLabel.setBounds(260, 85, 150, 26);
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
        scroll.setBounds(50, 150, 400, 320);
        frame.add(scroll);

        message = new JTextArea();
        message.setEditable(false);

        JScrollPane scroll2 = new JScrollPane(message);
        scroll2.setBounds(50, 480, 300, 56);
        message.setLineWrap(true);
        frame.add(scroll2);

        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        sendFileButton = new JButton("Send file");
        sendFileButton.setBounds(360, 480, 90, 25);
        sendFileButton.setEnabled(false);
        frame.add(sendFileButton);

        sendButton = new JButton("Send");
        sendButton.setBounds(360, 510, 89, 25);
        sendButton.setEnabled(false);
        frame.add(sendButton);

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

    public String getIP() {
        return ip.getText();
    }

    public int getPort() {
        int temp = -1;
        try {
            temp = Integer.parseInt(this.port.getText());
            return temp;
        }
        catch(Exception e) {
            return temp;
        }
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setChatArea(String text) {
        this.chatArea.setText(text);
    }

    public void sendFile() {
        fileChooser.showOpenDialog(null);
    }

    public File getFile() {
        return new File(fileChooser.getCurrentDirectory(), fileChooser.getSelectedFile().getName());
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void saveFile() {
        int r = fileChooser.showSaveDialog(null);

        if(r == JFileChooser.APPROVE_OPTION) {
            file = getFile();
            setFile(file);
        }
    }

    public void login() {
        String username = getUsername();
        this.username.setText(username);

        this.username.setEditable(false);
        this.ip.setEditable(false);
        this.port.setEditable(false);
        this.loginButton.setVisible(false);

        this.userLabel.setText("Welcome " + username);
        this.userLabel.setVisible(true);
        this.logoutButton.setVisible(true);

        this.message.setEditable(true);
        this.sendFileButton.setEnabled(true);
        this.sendButton.setEnabled(true);
    }

    public void logout() {
        this.userLabel.setText("Username");

        this.username.setEditable(true);
        this.username.setText("");
        this.ip.setEditable(true);
        this.port.setEditable(true);
        this.port.setText("");
        this.loginButton.setVisible(true);
        this.logoutButton.setVisible(false);

        this.chatArea.setText("");
        this.message.setText("");
        this.message.setEditable(false);
        this.sendFileButton.setEnabled(false);
        this.sendButton.setEnabled(false);
    }

    public void message(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    public void addButtonActionListener(ActionListener listener) {
        sendFileButton.addActionListener(listener);
        sendButton.addActionListener(listener);
        loginButton.addActionListener(listener);
        logoutButton.addActionListener(listener);
    }
}