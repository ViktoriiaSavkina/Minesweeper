package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 18;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int score;
    private int countClosedTiles = SIDE * SIDE;
    private boolean isGameStopped;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }
    
    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped == true) {
            restart();
        }
        else { openTile(x, y);  
        }
    }
    
    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y, "");
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.YELLOW);
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
    
    private void countMineNeighbors() {
        for ( int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (gameField[y][x].isMine == true) {
                continue;
                }
                List<GameObject> list = getNeighbors(gameField[y][x]);
                for (GameObject lt : list) {
                    if (lt.isMine == true) {
                        gameField[y][x].countMineNeighbors++;
                    }
                }
            
            } 
        
        }
    }
    
    private void openTile(int x, int y) {
        if (gameField[y][x].isOpen == false && gameField[y][x].isFlag == false && isGameStopped == false) {
        gameField[y][x].isOpen = true;
        setCellColor(x, y, Color.GREEN);
        countClosedTiles--;
            if (gameField[y][x].isMine == true) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
            }
            else { score += 5;
            setScore(score);
                if (countClosedTiles == countMinesOnField) {
                win();
            } else if (gameField[y][x].countMineNeighbors == 0) {
            setCellValue(x, y, "");
            List <GameObject> nei = getNeighbors(gameField[y][x]);
            for (GameObject n : nei) {
                if (!n.isOpen) {
                openTile(n.x, n.y);
                }
            }
            }
                   else { setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                   }  
            }
        }  
    }
    
    private void markTile(int x, int y) {
        if (!isGameStopped == true) {
        if (gameField[y][x].isOpen == false && gameField[y][x].isFlag == false && countFlags != 0) {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.PURPLE);
        }
        else if (gameField[y][x].isOpen == false && gameField[y][x].isFlag == true) {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.YELLOW);
        }
        }
    }
    
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED, "GaMe OvErrrrrr", Color.BLACK, 42);
    }
    
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLUE, "You Are The Champion!\nYour score: " + score, Color.WHITE, 36);
    }
    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }
}