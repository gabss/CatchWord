package it.catchword.entity.impl;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import it.catchword.config.Constant;
import it.catchword.engine.GameEngine;
import it.catchword.engine.GameObject;
import it.catchword.entity.Board;
import it.catchword.entity.ChatMessage;
import it.catchword.entity.Game;
import it.catchword.entity.Player;
import it.catchword.network.entity.NetworkAdapter;
import it.catchword.network.entity.impl.NetworkAdapterImpl;
import it.catchword.util.Debug;

import java.util.*;

/**
 * Created by Stefano on 28/03/2016.
 */
public class DistributedGame implements Game, GameObject {

    private int status = Constant.GAME_STATUS_INIT;
    private Player localPlayer;
    private List<Player> playersList;
    private NetworkAdapter networkAdapter;
    private Board board;

    private int manches;
    private int mancheTime = Constant.MANCHE_TIME;
    private int multiplier = 5;

    //Variabili per gestione timer/manche
    private long lastTimerUpdate;
    private long[] wordsTimeSpawn = new long[Constant.HOLES_NUMBER];

    public long getStartTime() {
        return startTime;
    }

    private long startTime = 0;

    //Variabili di gioco
    private long lastSpawnTime = 0;
    private long timeoutSpawnWord = 2000;
    private long timeoutWord = 5000;

    List<ChatMessage> chatMessages = new ArrayList<>();

    private Map<String, Integer> history = new HashMap<>();


    @Override
    public List<Player> getAllUser() {
        return playersList;
    }

    @Override
    public Player getLocalUser() {
        return localPlayer;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getWord(int index) {
        return board.getWord(index);
    }

    @Override
    public void removeWord(String word){
        board.removeWord(word);
    }

    @Override
    public void addChatMessage(ChatMessage message) {
        chatMessages.add(message);
    }

    @Override
    public void sendChatMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(localPlayer.getUsername(), message);
        networkAdapter.sendChatMessage(chatMessage);
        chatMessages.add(chatMessage);
    }

    @Override
    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    private int computeScore(String word, long time){
        return     (int) ((word.length() * multiplier) /
                (time / 1000));
    }

    private void orderRank() {
        Collections.sort(playersList, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return -Integer.compare(o1.getScore(), o2.getScore());
            }
        });
    }

    public DistributedGame(){
        networkAdapter = new NetworkAdapterImpl(this);
    }

    @Override
    public List<String> getChannels() {
        return networkAdapter.getChannels();
    }

    @Override
    public int joinChannel(String playerName, String channelName, String networkAddress) {
        localPlayer = new SimplePlayer(playerName);
        playersList = new ArrayList<>();

        long seed = networkAdapter.joinChannel(channelName, localPlayer, networkAddress);
        if(seed >= 0) {
            board = new Board(seed);
            playersList = networkAdapter.getPlayersList();
            GameEngine.addObject(this);
            return 1;
        }
        return 0;
    }

    @Override
    public int startGame() {
        //Almeno devono esserci due giocatori per giocare
        if(playersList.size() >= 2){
            networkAdapter.startGame();
            return Constant.OK;
        }

        return Constant.NOT_ENOUGH_PLAYERS;
    }

    @Override
    public void startGame(long time){
        long now = System.currentTimeMillis();
        Debug.print(now + " :Il gioco dovrà cominciare a: "+(time));
        this.manches = playersList.size() - 1;
        status = Constant.GAME_STATUS_WAIT;
        startTime = time;
    }

    @Override
    public void wordCaught(int index) {

        long removedTime = System.currentTimeMillis();
        long timeDiff =  removedTime - wordsTimeSpawn[index];
        String word = board.getWord(index);
        int score = computeScore(word, timeDiff);
        history.put(board.getWord(index), score);
        networkAdapter.wordCaught(word, score);
        board.removeWord(index);
    }

    @Override
    public int getWordInfo(String word) {
        if(history.containsKey(word))
            return history.get(word);
        return 0;
    }

    @Override
    public void removeLastPlayer() {
        //La manche è a -2 rispetto al lsita dei giocatori, poichè l'aggiornamento della manche viene fatto prima
        //di chiamare la removeLastPlayer
        if(playingPlayer() == manches + 2) {
            Player last = getLastPlayingPlayer();
            last.setStatus(Constant.USER_STATUS_LOSE);
            if(last.getUsername().equals(localPlayer.getUsername()))
                localPlayer.setStatus(Constant.USER_STATUS_LOSE);
            Debug.print(last.getUsername() + " ha perso");
        }
        this.manches = playingPlayer() - 1;
    }

    private int playingPlayer(){
        int result = 0;

        for(Player player : playersList){
            if(player.getStatus() != Constant.USER_STATUS_LOSE)
                result++;
        }

        return result;
    }

    @Override
    public boolean update() {
        long now = System.currentTimeMillis();

        switch(status){
            case Constant.GAME_STATUS_WAIT:
                if(now - startTime >= 0) {
                    Debug.print("Il gioco comincia");
                    status = Constant.GAME_STATUS_ACTIVE;
                    lastTimerUpdate = now;
                    lastSpawnTime = now;
                }
                break;
            case Constant.GAME_STATUS_ACTIVE:
                if(manches == 0) {
                    status = Constant.GAME_STATUS_END;
                    if(playersList.get(0).getUsername().equals(localPlayer.getUsername()))
                        localPlayer.setStatus(Constant.USER_STATUS_WIN);
                    else
                        localPlayer.setStatus(Constant.USER_STATUS_LOSE);
                    Debug.print("Gioco terminato");
                    Debug.print("Ha vinto il giocatore: "+playersList.get(0).getUsername());
                    break;
                }
                spawnNewWord(now);
                checkTimeoutWord(now);
                orderRank();
                if(now - lastTimerUpdate >= 1000){
                    lastTimerUpdate = now;
                    mancheTime -= 1;
                    if(mancheTime == 0){
                        manches -= 1;  //La settiamo a -= 1  perchè. per verificare se qualcuno è crashato, aspettiamo di ricevere i manche end
                        if(manches != 0){
                            //Dobbiamo comunicare il nostro ultimo player all'adapter
                            networkAdapter.notifyMancheEnd();
                            mancheTime = Constant.MANCHE_TIME;
                        }
                    }
                }
                //Controllo sullo stato del local player
                switch (localPlayer.getStatus()){
                    case Constant.USER_STATUS_LOSE:
                        break;

                }
                break;
            default:
                break;

        }

        return true;
    }

    private void spawnNewWord(long time) {
        if(board.getLength() < Constant.MAX_SIMULTANEOUS_WORDS){
            if(time - lastSpawnTime > timeoutSpawnWord){
                int hole = board.spawnWord();
                lastSpawnTime = System.currentTimeMillis();
                wordsTimeSpawn[hole] = lastSpawnTime;
            }
        }
    }

    private void checkTimeoutWord(long time) {
        for(int i=0; i<Constant.HOLES_NUMBER; i++){
            if(wordsTimeSpawn[i] != 0 && time - wordsTimeSpawn[i] > timeoutWord){
                board.removeWord(i);
                wordsTimeSpawn[i] = 0;
            }
        }
    }

    private Player getLastPlayingPlayer() {

        for (int i = playersList.size()-1; i >= 1; i--){
            Player current = playersList.get(i);
            if(current.getStatus() != Constant.USER_STATUS_LOSE){
                return current;
            }
        }

        return null;
    }

    @Override
    public int getManches() {
        return manches;
    }

    public void setManches(int manches) {
        this.manches = manches;
    }

    @Override
    public int getMancheTime() {
        return mancheTime;
    }

    public void setMancheTime(int mancheTime) {
        this.mancheTime = mancheTime;
    }

}
