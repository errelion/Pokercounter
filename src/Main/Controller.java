package Main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import java.util.ArrayList;

public class Controller {


    //Initialisierung aller Elemente

    @FXML
    TextField player0TextField, player1TextField, player2TextField, player3TextField, player4TextField, player5TextField, player6TextField, player7TextField;

    @FXML
    Label player0Cash, player1Cash, player2Cash, player3Cash, player4Cash, player5Cash, player6Cash, player7Cash;

    @FXML
    Label player0Bet, player1Bet, player2Bet, player3Bet, player4Bet, player5Bet, player6Bet, player7Bet;

    @FXML
    Label potLabel, betLabel, playerTurnLabel, lastRaiseLabel, roundCounterLabel, errorMessage;

    @FXML
    Button player0Wins, player1Wins, player2Wins, player3Wins, player4Wins, player5Wins, player6Wins, player7Wins;

    @FXML
    Button startButton, betCallButton, checkFoldButton;

    @FXML
    TextField cashPlayersTextField, smallBlindTextField, betTextField;

    //Bündeln von Elementen zu Arrays / ArrayLists

    @FXML
    private ArrayList<TextField> playersTextField;

    @FXML
    private ArrayList<Label> playersCash;

    @FXML
    private ArrayList<Label> playersBet;

    @FXML
    private ArrayList<Button> playersWin;



    //Deklarieren der Spielrelevanten Variablen
    private int players = 0;
    private int initPlayers = 0;
    private boolean gameStarted = false;
    private boolean firstRound = true;

    //Deklarieren der Rundenrelevanten Variablen
    private int round = 1;
    private int playerSmallBlind = 1;
    private int playerBigBlind = 2;
    private int playerTurn = 3;
    private int smallBlind = 0;
    private int bigBlind = 0;
    private int lastRaise = 2;
    private int checks = 0;
    private boolean canCheck = true;
    private boolean canBet = true;
    private boolean isShowdown = false;
    private boolean[] playersLost = new boolean[8];
    boolean[] inGame = new boolean[8];
    boolean[] allIn = new boolean[8];



    //Deklarieren der Action Events

    @FXML
    protected void startButtonPressed(){

        if (!gameStarted) {
            gameStarted = true;

            for (TextField player : playersTextField) {
                if (!player.getText().equals("")) players++;
                player.setEditable(false);
            }

            for (int i = 0; i < players; i++) {
                playersCash.get(i).setText(cashPlayersTextField.getText());
                playersBet.get(i).setText("0");
                playersWin.get(i).setText("Gewinnt");
            }

            for (int i=0; i<playersLost.length; i++){
                playersLost[i] = false;
                allIn[i] = false;
            }

            smallBlind = Integer.parseInt(smallBlindTextField.getText());
            bigBlind = 2*smallBlind;

            initPlayers = players;

            newRound(0,0);
        }

    }


    private int playersIngame = players;


    //Zurücksetzen der Spieldaten

    protected void newRound(int winningPlayer, int cash){

        errorMessage.setText("");
        int counter=0;

        for(int i=0; i<initPlayers; i++){
            if (!playersLost[i]){
                inGame[i] = true;
                counter++;
            }
            playersBet.get(i).setText("0");
            playersTextField.get(i).setEffect(null);
        }


        if (firstRound) {
            playerTurnLabel.setText(playersTextField.get(playerTurn).getText());
            firstRound = false;
            canCheck = false;
            canBet = false;
            lastRaiseLabel.setText(playersTextField.get(playerBigBlind).getText());
        }else{
            nextBlinds();
            lastRaise=playerBigBlind;

            int oldCash=Integer.parseInt(playersCash.get(winningPlayer).getText());
            int newCash=oldCash+cash;
            playersCash.get(winningPlayer).setText(Integer.toString(newCash));

            for(Button knöpfle:playersWin){
                knöpfle.setText("Gewinnt");
            }
        }



        for(int i=0; i<initPlayers; i++){
            if (playersCash.get(i).getText().equals("0")) {
                playersLost[i] = true;
                inGame[i] = false;
                players--;
                playersWin.get(i).setText("Verloren");
            }
        }


        checkFoldButton.setText("Fold");
        betCallButton.setText("Call");

        canBet=false;
        canCheck=false;
        isShowdown=false;

        round = 1;
        playersIngame = players;
        playerTurn=playerBigBlind+1;
        while (playerTurn==initPlayers||playersLost[playerTurn]==true){
            if (playerTurn==initPlayers) playerTurn = 0;
            else playerTurn++;
        }

        playerTurnLabel.setText(playersTextField.get(playerTurn).getText());
        roundCounterLabel.setText("1");
        betLabel.setText("0");
        potLabel.setText("0");


        bet(playerSmallBlind, smallBlind);
        bet(playerBigBlind, bigBlind);
    }

    protected void nextRound(){
        for (int i=0; i<initPlayers; i++){
            if (!playersLost[i]){
                playersBet.get(i).setText("0");
            }
        }

        betLabel.setText("0");
        betCallButton.setText("Bet");
        checkFoldButton.setText("Check");
        betTextField.setPromptText("Bet");
        checks = 0;

        canBet=true;
        canCheck=true;

        playerTurn = lastRaise;
        playerTurnLabel.setText(playersTextField.get(playerTurn).getText());
        round++;
        roundCounterLabel.setText(Integer.toString(round));
    }

    protected void nextPlayer(){

        boolean allAllIn = true;
        for(int i=0; i<initPlayers; i++){
            if (inGame[i]==true) allAllIn = false;
        }

        if (allAllIn){
            isShowdown = true;
            errorMessage.setText("Showdown!");
        }else {
            do {
                playerTurn++;
                if (playerTurn >= players) {
                    playerTurn = 0;
                }
            } while (!(!playersLost[playerTurn] && inGame[playerTurn]));

            playerTurnLabel.setText(playersTextField.get(playerTurn).getText());


            if (checks == 0 || !canCheck) isNextRound();
        }
    }

    protected void nextBlinds(){

        do{
            playerSmallBlind++;
            if (playerSmallBlind>=players) {
                playerSmallBlind = 0;
            }
        }while (!(!playersLost[playerSmallBlind]&&inGame[playerSmallBlind]));

        int handle = playerSmallBlind;

        do{
            handle++;
            if (handle>=players) {
                handle = 0;
            }
        }while (!(!playersLost[handle]&&inGame[handle]));

        playerBigBlind=handle;
        lastRaise=playerBigBlind;
        lastRaiseLabel.setText(playersTextField.get(lastRaise).getText());
    }

    protected void isNextRound(){
        String handle = playersBet.get(playerTurn).getText();
        boolean nextRound = true;
        for (int i=0; i<initPlayers; i++){
            if (!playersLost[i]&&(inGame[i]||allIn[i])){            //Todo Fettes TODO!
                if (!handle.equals(playersBet.get(i).getText())) nextRound = false;
            }
        }

        if (nextRound){

            if (round==3){
                errorMessage.setText("Showdown!");
                isShowdown = true;
            }else {
                nextRound();
            }
        }

    }

    protected void bet(int player, int val){

        if (val<Integer.parseInt(playersCash.get(player).getText())) {
            int newCash = Integer.parseInt(playersCash.get(player).getText()) - val;
            int newBet = Integer.parseInt(playersBet.get(player).getText()) + val;
            playersBet.get(player).setText(Integer.toString(newBet));
            playersCash.get(player).setText(Integer.toString(newCash));
            int ante = Integer.parseInt(playersBet.get(player).getText());
            betLabel.setText(Integer.toString(ante));
        }

        else{
            val = Integer.parseInt(playersCash.get(player).getText());
            int bet = Integer.parseInt(playersBet.get(player).getText());
            int newBet = val+bet;
            int ante = Integer.parseInt(betLabel.getText());

            playersWin.get(player).setText("All in!");

            if (newBet>ante) betLabel.setText(Integer.toString(newBet));
            playersCash.get(player).setText("0");
            playersBet.get(player).setText(Integer.toString(newBet));

            inGame[player]=false;
            allIn[player]=true;
            playersIngame--;
        }

        betTextField.setText("");
        int pot = Integer.parseInt(potLabel.getText())+val;
        potLabel.setText(Integer.toString(pot));
    }




    @FXML
    protected void betCallButtonPressed(){

        if(!isShowdown) {
            int bet;

            errorMessage.setText("");

            if (betTextField.getText().equals("") && canBet) {
                errorMessage.setText("Muss etwas eingeben");
            } else {
                if (canBet) {

                    bet = Integer.parseInt(betTextField.getText());
                    betCallButton.setText("Call");
                    checkFoldButton.setText("Fold");

                    lastRaiseLabel.setText(playersTextField.get(playerTurn).getText());
                    lastRaise = playerTurn;
                    canBet = false;
                    canCheck = false;

                    betTextField.setPromptText("Raise");

                } else {
                    bet = Integer.parseInt(betLabel.getText()) - Integer.parseInt(playersBet.get(playerTurn).getText());
                }

                bet(playerTurn, bet);

                nextPlayer();
            }
        }
    }

    @FXML
    protected void raiseButtonPressed(){

        if (!isShowdown) {
            if (Integer.parseInt(betTextField.getText()) < (Integer.parseInt(betLabel.getText()) + bigBlind)) {
                errorMessage.setText("Muss mindestens Big Blind raisen");
            } else {
                errorMessage.setText("");
                int bet = Integer.parseInt(betTextField.getText());
                bet(playerTurn, bet);
                lastRaise = playerTurn;
                canBet = false;
                canCheck = false;

                lastRaiseLabel.setText(playersTextField.get(playerTurn).getText());

                betCallButton.setText("Call");
                checkFoldButton.setText("Fold");


                nextPlayer();
            }
        }
    }

    @FXML
    protected void checkFoldButtonPressed(){

        if (!isShowdown) {

            errorMessage.setText("");
            if (canCheck) {
                checks++;

                if (checks == playersIngame){
                    if (round==3){
                        errorMessage.setText("Showdown!");
                        isShowdown = true;
                    }else{
                        nextRound();
                    }
                }
                else nextPlayer();

            }

            else{
                inGame[playerTurn] = false;
                playersIngame--;
                playersTextField.get(playerTurn).setEffect(new GaussianBlur());
                nextPlayer();
            }

        }
    }

    @FXML
    protected void player0WinsPressed(){
        if(!playersLost[0]&&(inGame[0]||allIn[0])) {
            int win = Integer.parseInt(potLabel.getText());
            newRound(0, win);
        }else{
            errorMessage.setText("Spieler kann nicht gewinnen!");
        }
    }

    @FXML
    protected void player1WinsPressed(){
        if(!playersLost[1]&&(inGame[1]||allIn[1])) {
            int win = Integer.parseInt(potLabel.getText());
            newRound(1, win);
        }else{
            errorMessage.setText("Spieler kann nicht gewinnen!");
        }
    }

    @FXML
    protected void player2WinsPressed(){
        if(!playersLost[2]&&(inGame[2]||allIn[2])) {
            int win = Integer.parseInt(potLabel.getText());
            newRound(2, win);
        }else{
            errorMessage.setText("Spieler kann nicht gewinnen!");
        }
    }

    @FXML
    protected void player3WinsPressed(){
        if(!playersLost[3]&&(inGame[3]||allIn[3])) {
            int win = Integer.parseInt(potLabel.getText());
            newRound(3, win);
        }else{
            errorMessage.setText("Spieler kann nicht gewinnen!");
        }
    }

    @FXML
    protected void player4WinsPressed(){
        if(!playersLost[4]&&(inGame[4]||allIn[4])) {
            int win = Integer.parseInt(potLabel.getText());
            newRound(4, win);
        }else{
            errorMessage.setText("Spieler kann nicht gewinnen!");
        }
    }

    @FXML
    protected void player5WinsPressed(){
        if(!playersLost[5]&&(inGame[5]||allIn[5])) {
            int win = Integer.parseInt(potLabel.getText());
            newRound(5, win);
        }else{
            errorMessage.setText("Spieler kann nicht gewinnen!");
        }
    }

    @FXML
    protected void player6WinsPressed(){
        if(!playersLost[6]&&(inGame[6]||allIn[6])) {
            int win = Integer.parseInt(potLabel.getText());
            newRound(6, win);
        }else{
            errorMessage.setText("Spieler kann nicht gewinnen!");
        }
    }

    @FXML
    protected void player7WinsPressed(){
        if(!playersLost[7]&&(inGame[7]||allIn[7])) {
            int win = Integer.parseInt(potLabel.getText());
            newRound(7, win);
        }else{
            errorMessage.setText("Spieler kann nicht gewinnen!");
        }
    }
}