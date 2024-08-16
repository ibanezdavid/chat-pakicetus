import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Objects;

public class DynamicGUIChatClient {
    private static final String SERVER_IP = "***.***.***.**"; // Please change the public IP to match yours. In my case it's ***.***.***.**.
    private static final int SERVER_PORT = 9999; // Please change the port to match yours. In my case it's 9999.
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JFrame frame;
    private JPanel cardsPanel;
    private JPanel nicknamePanel;
    private JPanel chatPanel;
    private JTextArea chatArea;
    private JTextField inputField;
    private JTextField nicknameField;
    private String nickname;
    public DynamicGUIChatClient() {
        initializeGUI();
        connectToServer();
    }
    private void initializeGUI() {
        frame = new JFrame("Chat Pakicetus by David Ibáñez");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 750);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/b.png")));
        frame.setIconImage(icon.getImage());
        BackgroundPanel backgroundPanel = new BackgroundPanel("/b.png", 1f);
        frame.setContentPane(backgroundPanel);
        cardsPanel = new JPanel(new CardLayout());
        cardsPanel.setOpaque(false);
        backgroundPanel.add(cardsPanel, BorderLayout.CENTER);
        nicknamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
        nicknamePanel.setOpaque(true);
        nicknamePanel.setBackground(new Color(255,100,100, 50));
        JLabel nicknameLabel = new JLabel("Please tell us your name...");
        nicknameLabel.setPreferredSize(new Dimension(800,75));
        nicknameLabel.setOpaque(true);
        nicknameLabel.setFont(new Font("Courier New", Font.BOLD, 30));
        nicknameLabel.setForeground(Color.black);
        nicknameLabel.setBackground(new Color(255,100,100));
        nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nicknameLabel.setVerticalAlignment(SwingConstants.CENTER);
        nicknameField = new JTextField(40);
        nicknameField.setOpaque(true);
        nicknameField.setPreferredSize(new Dimension(300, 75));
        nicknameField.setForeground(Color.black);
        nicknameField.setFont(new Font("Courier New", Font.BOLD, 30));
        nicknameField.setBackground(new Color(255,150,150));
        nicknameField.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton nicknameButton = new JButton("Let's chat!");
        nicknameButton.setOpaque(true);
        nicknameButton.setPreferredSize(new Dimension(250, 75));
        nicknameButton.setForeground(Color.black);
        nicknameButton.setFont(new Font("Courier New", Font.BOLD, 30));
        nicknameButton.setBackground(new Color(255,255,100));
        nicknameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleNicknameInput();
            }
        });
        nicknamePanel.add(nicknameLabel);
        nicknamePanel.add(nicknameField);
        nicknamePanel.add(nicknameButton);
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setOpaque(false);
        chatArea = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        chatArea.setFont(new Font("Courier New", Font.BOLD, 30));
        chatArea.setOpaque(false);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setBorder(new EmptyBorder(50, 50, 50, 50));
        chatArea.setForeground(new Color(255,255,100));
        chatArea.setBackground(new Color(255,100,100, 100));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setBackground(new Color(255,100,100));
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setBackground(new Color(255,100,100));
        inputField = new JTextField(40);
        inputField.setOpaque(true);
        inputField.setPreferredSize(new Dimension(200, 100));
        inputField.setForeground(Color.black);
        inputField.setFont(new Font("Courier New", Font.BOLD, 30));
        inputField.setBackground(new Color(255,150,150));
        inputField.setBorder(new EmptyBorder(25, 25, 25, 25));
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        chatPanel.add(inputField, BorderLayout.SOUTH);
        cardsPanel.add(nicknamePanel, "nicknamePanel");
        cardsPanel.add(chatPanel, "chatPanel");
        CardLayout cardLayout = (CardLayout) cardsPanel.getLayout();
        cardLayout.show(cardsPanel, "nicknamePanel");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        nicknameField.requestFocus();
    }
    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = (String) in.readObject();
                        appendMessage(message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void handleNicknameInput() {
        String inputNickname = nicknameField.getText().trim();
        if (!inputNickname.isEmpty()) {
            nickname = inputNickname;
            frame.setTitle("Welcome to Chat Pakicetus " + nickname + "!");
            CardLayout cardLayout = (CardLayout) cardsPanel.getLayout();
            cardLayout.show(cardsPanel, "chatPanel");
            inputField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(frame, "Please insert a nickname.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            try {
                out.writeObject(nickname + ": " + message);
                out.flush();
                inputField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DynamicGUIChatClient();
        });
    }
}
