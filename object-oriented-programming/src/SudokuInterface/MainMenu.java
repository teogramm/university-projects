package SudokuInterface;

import SudokuLogic.PuzzleDuidoku;
import SudokuLogic.PuzzleKillerSudoku;
import SudokuLogic.PuzzleSudoku;
import SudokuLogic.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The MainMenu class represents the window that shows the main menu of the application. It allows the user to log in
 * start a new game and access the settings menu.
 */
public class MainMenu {
    private JFrame mainWindow;
    private UserManager userManager;
    private int puzzleid;
    private ResourceBundle messages;
    private boolean wordoku;

    public static final int SUDOKU = 0;
    public static final int KILLER = 1;
    public static final int DUIDOKU = 2;

    public MainMenu(){
        mainWindow = new JFrame("Sudoku");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLayout(new BoxLayout(mainWindow.getContentPane(),BoxLayout.Y_AXIS));

        // Set up localisation stuff
        Locale locale = Locale.getDefault();
//        Locale locale = new Locale("el","GR");
        messages = ResourceBundle.getBundle("i18n.Sudoku",locale);

        try {
            userManager = new UserManager();
            mainWindow.add(createUserPanel());
        }catch (IOException e){
            JOptionPane.showMessageDialog(mainWindow,messages.getString("usersNotFunctional"));
        }

        mainWindow.add(new JSeparator(SwingConstants.HORIZONTAL));
        mainWindow.add(createGameButtonsPanel());

        JCheckBox wordokuCheckbox = new JCheckBox("Wordoku");
        wordokuCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Toggle the value of field wordoku when clicking the checkbox
        wordoku = false;
        wordokuCheckbox.addItemListener(itemEvent -> wordoku = !wordoku);
        mainWindow.add(wordokuCheckbox);

        // Center on screen
        mainWindow.setLocationRelativeTo(null);
        // Make the window the right size
        mainWindow.pack();
        mainWindow.setVisible(true);
    }



    /**
     * createUserPanel creates the panel that shows user info and allows the user to log in
     */

    private JPanel createUserPanel(){
        JPanel tempPanel = new JPanel();

        JLabel greeting = new JLabel(messages.getString("loginPrompt"));
        JButton logButton = new JButton(messages.getString("login"));
        JButton registerButton = new JButton(messages.getString("register"));
        JButton statsButton = new JButton(messages.getString("stats"));
        statsButton.setVisible(false);

        statsButton.addActionListener(actionEvent -> {
            showLoggedInUserStats();
        });

        logButton.addActionListener(actionEvent -> {
            // Check if user is logged in so we perform logon/logoff
            // Open a dialog to enter text
            if(userManager.isUserLogin()){
                // If user is logged in, disconnect the current user
                userManager.logoff();
                greeting.setText(messages.getString("loginPrompt"));
                registerButton.setVisible(true);
                statsButton.setVisible(false);
                logButton.setText(messages.getString("login"));
            }
            else{
                String username = JOptionPane.showInputDialog(messages.getString("usernamePrompt"));
                if(username != null && userManager.login(username)){
                    greeting.setText(messages.getString("welcome") + username + " !");
                    logButton.setText(messages.getString("logoff"));
                    registerButton.setVisible(false);
                    statsButton.setVisible(true);
                }
                else {
                    JOptionPane.showMessageDialog(mainWindow,messages.getString("userDoesNotExist"),messages.getString("error"),JOptionPane.ERROR_MESSAGE);
                }
            }
            try {
                userManager.saveToDisc();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        registerButton.addActionListener(actionEvent -> {
            String username = JOptionPane.showInputDialog(messages.getString("usernamePrompt"));
            if(username != null && userManager.addUser(username)){
                userManager.login(username);
                greeting.setText(messages.getString("welcome") + username + " !");
                logButton.setText(messages.getString("logoff"));
                registerButton.setVisible(false);
                statsButton.setVisible(true);
            }
            else{
                JOptionPane.showMessageDialog(mainWindow,messages.getString("usernameTaken"),messages.getString("error"),JOptionPane.ERROR_MESSAGE);
            }
            try {
                userManager.saveToDisc();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainWindow,messages.getString("saveFailed"),messages.getString("error"),JOptionPane.ERROR_MESSAGE);
            }
        });



        tempPanel.add(greeting);
        tempPanel.add(logButton);
        tempPanel.add(registerButton);
        tempPanel.add(statsButton);
        return tempPanel;
    }

    private void showLoggedInUserStats(){
        JDialog stats = new JDialog(mainWindow);
        stats.setLayout(new BoxLayout(stats.getContentPane(),BoxLayout.Y_AXIS));
        stats.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("Duidoku");
        JLabel wins = new JLabel(messages.getString("wins") + ": "+ userManager.getCurrentUserDuidokuWins());
        JLabel defeats = new JLabel(messages.getString("defeats") + ": "+ userManager.getCurrentUserDuidokuDefeats());

        stats.add(title);
        stats.add(wins);
        stats.add(defeats);

        stats.pack();
        stats.setLocationRelativeTo(null);
        stats.setVisible(true);
    }

    /**
     * startPuzzle starts a new GameWindow with the puzzle of the given mode
     * @param mode The game mode that will be played (must be one of the constants defined in
     *             this class)
     */
    private void startPuzzle(int mode){
        try {
            puzzleid = userManager.getUnplayedPuzzle(mode);
            if(puzzleid == -1){
                JOptionPane.showMessageDialog(mainWindow,messages.getString("noPuzzlesAvailable"),messages.getString("error"),JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(mode == KILLER) {
                new GameWindow(MainMenu.this, new PuzzleKillerSudoku(puzzleid),messages, wordoku);
            }
            else if(mode == SUDOKU){
                new GameWindow(MainMenu.this,new PuzzleSudoku(puzzleid),messages, wordoku);
            }
            else{
                throw new IllegalArgumentException("No such mode!");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainWindow,messages.getString("tryAgain"),messages.getString("error"),JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * createGameButtons creates the buttons that let the player select a game mode and start a new game
     */
    private JPanel createGameButtonsPanel(){
        JPanel tempPanel = new JPanel();

        JButton sudoku = new JButton("Sudoku");
        JButton killer = new JButton("Killer Sudoku");
        JButton duidoku = new JButton("Duidoku");

        killer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                startPuzzle(KILLER);
        }});

        sudoku.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                startPuzzle(SUDOKU);
            }
        });

        duidoku.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    new GameWindow(MainMenu.this,new PuzzleDuidoku(),messages, wordoku);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(mainWindow,messages.getString("tryAgain"),messages.getString("error"),JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        tempPanel.add(sudoku);
        tempPanel.add(killer);
        tempPanel.add(duidoku);

        return tempPanel;
    }

    void gameOver(int mode){
        JOptionPane.showMessageDialog(mainWindow,messages.getString("winPrompt"));
        if(mode == SUDOKU || mode == KILLER) {
            userManager.addUserPlayed(mode, puzzleid);
        }
    }

    void duidokuGameOver(boolean playerWon){
        if(playerWon){
            JOptionPane.showMessageDialog(mainWindow,messages.getString("winPrompt"));
        }else{
            JOptionPane.showMessageDialog(mainWindow,messages.getString("lossPrompt"));
        }
        if(userManager.isUserLogin()) {
            userManager.addDuidokuStats(playerWon);
        }
    }
    /**
     * @return The JFrame of the main window
     */
    JFrame getMainWindow() {
        return mainWindow;
    }
}
