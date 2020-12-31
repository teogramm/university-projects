package SudokuInterface;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * SudokuBox represents a box that contains a single number and that the player can interact with. It is the
 * visualisation of a square in the Board
 */
public class SudokuBox extends JPanel {
    private int row;
    private int col;
    private SudokuBoard parent;
    private JTextField textField;
    private Color backgroundColor;
    private JLabel sumlabel = null;
    private Wordoku wordoku;

    /**
     * Creates a new SudokuBox at the specified location.
     * @
     * @param row The row the box is positioned
     * @param col The column the box is positioned
     * @param wordoku A wordoku object that matches numbers to letters
     */
    public SudokuBox(SudokuBoard parent, int row, int col, Wordoku wordoku){
        backgroundColor = Color.white;

        this.wordoku = wordoku;
        this.parent = parent;
        this.row = row;
        this.col = col;

        setLayout(new BorderLayout(0,0));

        textField = new JTextField();
        textField.setFont(new Font("Default", Font.BOLD,textField.getHeight()/2));
        textField.setHorizontalAlignment(JTextField.CENTER);
        ((AbstractDocument)textField.getDocument()).setDocumentFilter(new TextLimitations(1,9));
        textField.setBackground(backgroundColor);
        // Resize the text as the window size changes
        textField.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                textField.setFont(new Font("Courier", Font.BOLD,textField.getHeight()/2));
            }
        });
        // Border is not needed for text field.
        textField.setBorder(null);
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(parent.isHelpMode()){
                    parent.showHints(row,col);
                }
            }
        });
        add(textField);
    }

    /**
     * Sets the sum label to the given number on the given square.
     * @param sum The sum that will be displayed on the given square
     */
    public void setSum(int sum){
        sumlabel = new JLabel(String.valueOf(sum));
        sumlabel.setVerticalAlignment(JTextField.TOP);
        sumlabel.setBackground(backgroundColor);
        sumlabel.setMaximumSize(new Dimension(getWidth(),5));
        sumlabel.setBorder(null);
        sumlabel.setOpaque(true);
        add(sumlabel,BorderLayout.LINE_START);
    }

    /**
     * Sets the square background color
     * @param color A Color object
     */
    public void setColor(Color color) {
        backgroundColor = color;
        textField.setBackground(backgroundColor);
        if(sumlabel != null){
            sumlabel.setBackground(backgroundColor);
        }
    }

    /**
     * Set the value of the square to the specified value, disregarding any rules. This method is used when updating
     * the board.
     * @param v the number to display
     */
    void setValueOverride(int v){
        if(v!=0) {
            // Remove the limitations
            ((AbstractDocument)textField.getDocument()).setDocumentFilter(null);
            if(parent.isWordoku()){
                textField.setText(String.valueOf(wordoku.intToChar(v)));
            }else {
                textField.setText(Integer.toString(v));
            }
            this.disableBox();
        }
    }

    /**
     * Disable text input for the square
     */
    void disableBox(){
        textField.setEditable(false);
        setBackground(Color.lightGray);
        textField.setBackground(Color.lightGray);
    }

    /**
     * This class checks if a move is allowed when the user tries to enter a value into the square,
     */
    class TextLimitations extends DocumentFilter {
        private int lowLimit;
        private int highLimit;

        TextLimitations(int lowLimit, int highLimit){
            if(lowLimit>highLimit){
                throw new IllegalArgumentException("Lower limit must be smaller than higher limit!");
            }
            this.lowLimit = lowLimit;
            this.highLimit = highLimit;
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            parent.placeNumber(row,col,0);
            super.remove(fb,offset,length);
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            replace(fb, offset,string.length(),string, attr);
        }

        @Override
        public void replace(FilterBypass fb,int offset,int length, String text, AttributeSet attrs) throws BadLocationException{
            Integer value;
            // Check the length
            int currentLength = fb.getDocument().getLength();
            // Length after replacement = currentLength of text in box + length of new text - length of text that will
            // be replaced.
            if(currentLength + text.length() - length>1){
                parent.moveNotAllowed();
                return;
            }
            // If we aren't playing wordoku check if input is number
            if(!parent.isWordoku()) {
                try {
                    value = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    parent.moveNotAllowed();
                    return;
                }
            }else {
                char c = text.charAt(0);
                value = wordoku.charToInt(Character.toUpperCase(c));
                if(value == null){
                    parent.moveNotAllowed();
                    return;
                }
            }
            // Check if number is between limits
            if(value < lowLimit || value > highLimit){
                parent.moveNotAllowed();
                return;
            }
            // Try to place the number on the board. If this succeeds, put it on the text field
            if(parent.placeNumber(row,col,value)) {
                // Check if text field has been disabled (for Duidoku)
                if(textField.isEditable()) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            else{
                parent.moveNotAllowed();
            }
        }
    }
}