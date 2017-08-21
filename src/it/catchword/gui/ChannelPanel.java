package it.catchword.gui;

import it.catchword.config.Constant;
import it.catchword.engine.GameEngine;
import it.catchword.engine.GameObject;
import it.catchword.entity.ChatMessage;
import it.catchword.entity.Game;
import it.catchword.entity.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ChannelPanel extends JDialog implements GameObject {
    private JPanel contentPane;
    private JLabel playerLabel1;
    private JLabel playerLabel2;
    private JLabel playerLabel3;
    private JLabel playerLabel4;
    private JLabel playerLabel5;
    private JLabel playerLabel6;
    private JLabel playerLabel7;
    private JLabel playerLabel8;
    private JLabel backgroundImage;
    private JTextArea chatArea;
    private JTextField chatMessage;
    private JButton sendMessage;
    private JButton startButton;
    private JScrollPane chatAreaScroll;
    private JPanel userListPanel;
    private JPanel socialPanel;
    private JPanel infoPanel;
    JLabel[] playersLabel = new JLabel[8];

    private Game game;
    private boolean isRunning = true;
    List<ChatMessage> messages = new ArrayList<>();
    private int currentChatIndex = 0;


    public ChannelPanel(Game lGame) {
        setResizable(false);
        setContentPane(contentPane);
        setModal(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        backgroundImage.setIcon(new ImageIcon("resources/channelPanel.png"));
        backgroundImage.add(socialPanel);
        backgroundImage.add(infoPanel);

        playerLabel1.setForeground(new Color(142,48,0));
        playerLabel2.setForeground(new Color(142,48,0));
        playerLabel3.setForeground(new Color(142,48,0));
        playerLabel4.setForeground(new Color(142,48,0));
        playerLabel5.setForeground(new Color(142,48,0));
        playerLabel6.setForeground(new Color(142,48,0));

        sendMessage.setBorder(null);
        startButton.setBorder(null);

        chatArea.setOpaque(false);
        chatArea.setBorder(null);
        chatArea.setForeground(new Color(142,48,0));

        chatMessage.setBorder(null);
        chatMessage.setForeground(new Color(142,48,0));

        chatAreaScroll.getViewport().setOpaque(false);
        chatAreaScroll.getViewport().setBorder(null);
        //chatAreaScroll.getViewport().setForeground(new Color(142,48,0));

        //chatAreaScroll.setForeground(new Color(142,48,0));
        chatAreaScroll.setBorder(null);



        this.game = lGame;
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChatMessage();
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.startGame();
            }
        });
        playersLabel[0] = playerLabel1; playersLabel[1] = playerLabel2;
        playersLabel[2] = playerLabel3; playersLabel[3] = playerLabel4;
        playersLabel[4] = playerLabel5; playersLabel[5] = playerLabel6;
        playersLabel[6] = playerLabel7; playersLabel[7] = playerLabel8;

        GameEngine.addGraphic(this);
        chatMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    sendChatMessage();
            }
        });
    }

    private void sendChatMessage(){
        String text = chatMessage.getText();
        if(!text.equals("")){
            game.sendChatMessage(text);
            chatMessage.setText("");
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
        isRunning = false;
    }

    private void refreshPlayers(){
        List<Player> players = game.getAllUser();

        for (int i = 0; i<players.size(); i++){
            playersLabel[i].setText(players.get(i).getUsername());
        }
        for (int i = players.size(); i<6; i++){
            playersLabel[i].setText("");
        }
    }

    @Override
    public boolean update() {
        List<ChatMessage> cMessages = new ArrayList<>();
        cMessages.addAll(game.getChatMessages());
        for(int i = messages.size(); i<cMessages.size(); i++){
            messages.add(cMessages.get(i));
        }
        int i;
        for(i = currentChatIndex; i<messages.size(); i++){
            chatArea.append(messages.get(i).getUsername() + ": "+messages.get(i).getMessage()+"\n");
        }
        currentChatIndex = i;
        JScrollBar vertical = chatAreaScroll.getVerticalScrollBar();
        vertical.setValue( vertical.getMaximum() );


        startButton.setEnabled(game.getLocalUser().isOwner());
        if(game.getStatus() == Constant.GAME_STATUS_WAIT){
            new GamePanel(game);
            dispose();
            GameEngine.removeGraphic(this);
        }

        refreshPlayers();

        return isRunning;
    }
}
