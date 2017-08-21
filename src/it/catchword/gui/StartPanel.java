package it.catchword.gui;

import it.catchword.engine.GameEngine;
import it.catchword.engine.GameObject;
import it.catchword.entity.Game;
import it.catchword.entity.impl.DistributedGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StartPanel extends JDialog implements GameObject{


    //Bottoni della gui
    private JPanel contentPane;
    private JPanel channelListPanel;
    private JPanel joinChannelPanel;
    private JTextField usernameField;
    private JTextField channelNameField;
    private JButton joinChannelButton;
    private JTextField hostnameField;
    private JLabel channel1;
    private JLabel channel2;
    private JLabel channel3;
    private JLabel channel4;
    private JLabel channel5;
    private JLabel channel6;
    private JLabel backgroundImage;
    JLabel[] channelLabel = new JLabel[6];
    private Game game;
    private boolean isRunning = true;

    public StartPanel() {
        setResizable(false);
        setContentPane(contentPane);
        setModal(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        backgroundImage.setIcon(new ImageIcon("resources/startPanel.png"));
        backgroundImage.add(channelListPanel);
        backgroundImage.add(joinChannelPanel);
        backgroundImage.add(joinChannelButton);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        usernameField.setBorder(null);
        usernameField.setForeground(new Color(142,48,0));

        hostnameField.setBorder(null);
        hostnameField.setForeground(new Color(142,48,0));

        channelNameField.setBorder(null);
        channelNameField.setForeground(new Color(142,48,0));

        joinChannelButton.setBorder(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        game = new DistributedGame();

        channelLabel[0] = channel1; channelLabel[1] = channel2; channelLabel[2] = channel3; channelLabel[3] = channel4;
        channelLabel[4] = channel5; channelLabel[5] = channel6;



        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                channelNameField.setText(((JLabel)e.getComponent()).getText());
            }
        };

        joinChannelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startChannelPanel();
            }
        });

        channel1.addMouseListener(listener);
        channel2.addMouseListener(listener);
        channel3.addMouseListener(listener);
        channel4.addMouseListener(listener);
        channel5.addMouseListener(listener);
        channel6.addMouseListener(listener);

        KeyAdapter listener1 = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    startChannelPanel();
            }
        };
        channelNameField.addKeyListener(listener1);
        usernameField.addKeyListener(listener1);
        hostnameField.addKeyListener(listener1);
    }

    private void startChannelPanel() {
        if(!usernameField.getText().equals("") && !channelNameField.getText().equals("")&& !hostnameField.getText().equals("")) {
            game.joinChannel(usernameField.getText(), channelNameField.getText(), hostnameField.getText());
            new ChannelPanel(game);
            dispose();
            GameEngine.removeGraphic(this);
        }
    }


    private void onCancel() {
    // add your code here if necessary
        dispose();
        isRunning = false;
    }

    private void refreshChannels(){
        List<String> channels = game.getChannels();

        for (int i = 0; i<channels.size(); i++){
            channelLabel[i].setText(channels.get(i));
        }
    }



    @Override
    public boolean update() {
        refreshChannels();
        return isRunning;
    }

}
