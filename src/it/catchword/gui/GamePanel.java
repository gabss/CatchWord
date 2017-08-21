package it.catchword.gui;

import it.catchword.config.Constant;
import it.catchword.engine.GameEngine;
import it.catchword.engine.GameObject;
import it.catchword.entity.Game;
import it.catchword.entity.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Map;

public class GamePanel extends JDialog implements GameObject{
    private JPanel contentPane;
    private JLabel backgroundImage;
    private JLabel buco2Label;
    private JLabel buco1Label;
    private JLabel buco4Label;
    private JLabel buco3Label;
    private JLabel buco5Label;
    private JLabel buco6Label;
    private JTextField userText;
    private JLabel firstPositionLabel;
    private JLabel secondPositionLabel;
    private JLabel thirdPositionLabel;
    private JLabel fourthPositionLabel;
    private JLabel fifthPositionLabel;
    private JLabel sixthPositionLabel;
    private JPanel buco2Panel;
    private JPanel buco1Panel;
    private JPanel buco5Panel;
    private JPanel buco3Panel;
    private JPanel buco6Panel;
    private JPanel buco4Panel;
    private JPanel rankPanel;
    private JPanel rankPanelOut;
    private JLabel clockLabel;
    private JLabel winOrLoseLabel;
    private JPanel buchiPanel;
    private JPanel userPanel;
    private JPanel clockPanel;
    //private JProgressBar timerBar;
    //private JLabel mancheLabel;
    private JLabel[] rankLabels = new JLabel[6];
    private JTextArea scoreArea;

    private Game game;
    private List<Player> players;
    private boolean isRunning = true;
    private ImageIcon[] clocks = new ImageIcon[31];
    private ImageIcon[] countdown = new ImageIcon[3];
    private ImageIcon[] winlose = new ImageIcon[2];

    public GamePanel(Game game) {
        setResizable(false);
        setContentPane(contentPane);
        setModal(false);
        pack();
        setVisible(true);
// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        loadCloackImages();
        clockLabel.setIcon(clocks[0]);
        backgroundImage.setIcon(new ImageIcon("resources/gamePanel.png"));



        backgroundImage.add(buchiPanel);
        backgroundImage.add(rankPanelOut);
        backgroundImage.add(userPanel);
        backgroundImage.add(clockPanel);

        userText.setBorder(null);


        this.game = game;
        players = game.getAllUser();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });



        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        GameEngine.addGraphic(this);

        userText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    userText.setText("");
            }
        });

        rankLabels[0] = firstPositionLabel;
        rankLabels[1] = secondPositionLabel;
        rankLabels[2] = thirdPositionLabel;
        rankLabels[3] = fourthPositionLabel;
        rankLabels[4] = fifthPositionLabel;
        rankLabels[5] = sixthPositionLabel;

        userText.requestFocus();
    }

    private void loadCloackImages() {
        countdown[0] = new ImageIcon("resources/one.png");
        countdown[1] = new ImageIcon("resources/two.png");
        countdown[2] = new ImageIcon("resources/three.png");
        winlose[0] = new ImageIcon("resources/youWin.png");
        winlose[1] = new ImageIcon("resources/youLose.png");
        for(int i = 0; i<31; i++){
            if(i<=8)
                clocks[i] = new ImageIcon("resources/clock/000"+(i+1)+".png");
            else
                clocks[i] = new ImageIcon("resources/clock/00"+(i+1)+".png");
        }
    }

    private void onCancel() {
// add your code here if necessary
        isRunning = false;
        dispose();
    }


    boolean gameStarted = false;

    @Override
    public boolean update() {
        long now = System.currentTimeMillis();
        long diff = game.getStartTime() - now;


        if ( diff  < 3000 && diff >0){
            winOrLoseLabel.setIcon(countdown[ ((int)(Math.ceil(diff/1000)))]);
        }




        updateScore();
        updateLabel();

        clockLabel.setIcon(clocks[30-game.getMancheTime()]);
        if(game.getStatus() == Constant.GAME_STATUS_ACTIVE) {
            checkUserText();
            if ( !gameStarted ) {
                winOrLoseLabel.setIcon(null);
                gameStarted = true;
            }
        }

        if(game.getLocalUser().getStatus() == Constant.USER_STATUS_LOSE)
            winOrLoseLabel.setIcon(winlose[1]);
        else if(game.getLocalUser().getStatus() == Constant.USER_STATUS_WIN) {
            winOrLoseLabel.setIcon(winlose[0]);
            userText.setEnabled(false);
        }

        return isRunning;
    }

    private void updateLabel(){
        buco1Label.setText(game.getWord(0));
        buco2Label.setText(game.getWord(1));
        buco3Label.setText(game.getWord(2));
        buco4Label.setText(game.getWord(3));
        buco5Label.setText(game.getWord(4));
        buco6Label.setText(game.getWord(5));
    }

    private void checkUserText() {
        String text = userText.getText();
        if(!text.equals(""))
            for(int i = 0; i< Constant.HOLES_NUMBER; i++){
                if(game.getWord(i).equals(text)){
                    game.wordCaught(i);
                    userText.setText("");
                    break;
                }
            }
    }

    private void updateScore(){
        for(int i = 0; i<players.size(); i++){
            rankLabels[i].setText(players.get(i).getUsername()+": "+players.get(i).getScore());
            if(players.get(i).getStatus() == Constant.USER_STATUS_LOSE){
                Font cFont = rankLabels[i].getFont();
                Map attributes = cFont.getAttributes();
                attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                rankLabels[i].setFont(new Font(attributes));
                if(players.get(i).getUsername().equals(game.getLocalUser().getUsername()))
                    userText.setEnabled(false);
            }
        }
        for(int i = players.size(); i<6; i++){
            rankLabels[i].setText("");
        }
    }



}
