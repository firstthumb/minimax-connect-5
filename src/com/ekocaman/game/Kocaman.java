package com.ekocaman.game;

import com.nosto.fun.game1.ArenaPosition;
import com.nosto.fun.game1.Piece;
import com.nosto.fun.game1.Player;

import java.util.ArrayList;
import java.util.List;

public class Kocaman implements Player {
    private static final int BOARD_SIZE = 18;
    private static final String AI_NAME = "KOCAMAN";
    private static final int MINIMAX_DEPTH = 2;

    private Piece[][] board;
    private Piece mySeed;
    private Piece oppSeed;

    /**
     * Main AI function
     *
     * @param depth
     * @param player
     * @return
     */
    private int[] minimax(int depth, Piece player) {
        List<int[]> nextMoves = generateMoves();

        int bestScore = (player == mySeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves.isEmpty() || depth == 0) {
            bestScore = evaluate();
        } else {
            for (int[] move : nextMoves) {
                board[move[0]][move[1]] = player;
                if (player == mySeed) {     // mySeed is maximizing player
                    currentScore = minimax(depth - 1, oppSeed)[0];
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                } else {                    // oppSeed is minimizing player
                    currentScore = minimax(depth - 1, mySeed)[0];
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }
                board[move[0]][move[1]] = null;
            }
        }

        return new int[]{bestScore, bestRow, bestCol};
    }

    /**
     * Evaluate the current board status and return a score
     *
     * @return
     */
    private int evaluate() {
        int score = 0;

        // Evaluate score for each of the lines
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // Row jth
                if (j + 4 < BOARD_SIZE) {
                    score += evaluateLine(i, j, i, j + 1, i, j + 2, i, j + 3, i, j + 4);
                }

                // Col ith
                if (i + 4 < BOARD_SIZE) {
                    score += evaluateLine(i, j, i + 1, j, i + 2, j, i + 3, j, i + 4, j);
                }

                // Diagonal
                if (i + 4 < BOARD_SIZE && j + 4 < BOARD_SIZE) {
                    score += evaluateLine(i, j, i + 1, j + 1, i + 2, j + 2, i + 3, j + 3, i + 4, j + 4);
                    score += evaluateLine(i, j + 4, i + 1, j + 3, i + 2, j + 2, i + 3, j + 1, i + 4, j);
                }
            }
        }

        return score;
    }

    /**
     * According to this line (5 (x,y) parameters), give a proper score
     *
     * @param row1
     * @param col1
     * @param row2
     * @param col2
     * @param row3
     * @param col3
     * @param row4
     * @param col4
     * @param row5
     * @param col5
     * @return
     */
    private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3, int row4, int col4, int row5, int col5) {
        int score = 0;

        // First cell
        if (board[row1][col1] == mySeed) {
            score = 1;
        } else if (board[row1][col1] == oppSeed) {
            score = -1;
        }

        // Second cell
        if (board[row2][col2] == mySeed) {
            if (score == 1) {           // cell1 is mySeed
                score = 10;
            } else if (score == -1) {   // cell1 is oppSeed
                return 0;
            } else {                    // cell1 is empty
                score = 1;
            }
        } else if (board[row2][col2] == oppSeed) {
            if (score == -1) {          // cell1 is oppSeed
                score = -10;
            } else if (score == 1) {    // cell1 is mySeed
                return 0;
            } else {                    // cell1 is empty
                score = -1;
            }
        }

        // Third cell
        if (board[row3][col3] == mySeed) {
            if (score > 0) {            // cell1 and/or cell2 is mySeed
                score *= 10;
            } else if (score < 0) {     // cell1 and/or cell2 is oppSeed
                return 0;
            } else {                    // cell1 and cell2 are empty
                score = 1;
            }
        } else if (board[row3][col3] == oppSeed) {
            if (score < 0) {            // cell1 and/or cell2 is oppSeed
                score *= 10;
            } else if (score > 1) {     // cell1 and/or cell2 is mySeed
                return 0;
            } else {                    // cell1 and cell2 are empty
                score = -1;
            }
        }

        // Fourth cell
        if (board[row4][col4] == mySeed) {
            if (score > 0) {            // cell1 and/or cell2 and/or cell3 is mySeed
                score *= 10;
            } else if (score < 0) {     // cell1 and/or cell2 and/or cell3 is oppSeed
                return 0;
            } else {                    // cell1 cell2 and cell3 are empty
                score = 1;
            }
        } else if (board[row4][col4] == oppSeed) {
            if (score < 0) {            // cell1 and/or cell2 and/or cell3 is oppSeed
                score *= 10;
            } else if (score > 1) {     // cell1 and/or cell2 and/or cell3 is mySeed
                return 0;
            } else {                    // cell1 cell2 and cell3 are empty
                score = -1;
            }
        }

        // Fifth cell
        if (board[row5][col5] == mySeed) {
            if (score > 0) {            // cell1 and/or cell2 and/or cell3 and/or cell4 is mySeed
                score *= 10;
            } else if (score < 0) {     // cell1 and/or cell2 and/or cell3 and/or cell4 is oppSeed
                return 0;
            } else {                    // cell1 cell2 cell3 and cell4 are empty
                score = 1;
            }
        } else if (board[row5][col5] == oppSeed) {
            if (score < 0) {            // cell1 and/or cell2 and/or cell3 and/or cell4 is oppSeed
                score *= 10;
            } else if (score > 1) {     // cell1 and/or cell2 and/or cell3 and/or cell4 is mySeed
                return 0;
            } else {                    // cell1 cell2 cell3 and cell4 are empty
                score = -1;
            }
        }

        return score;
    }

    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<int[]>();

        // Gameover
        if (hasWon(mySeed) || hasWon(oppSeed)) {
            return nextMoves;
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == null) {
                    nextMoves.add(new int[]{row, col});
                }
            }
        }

        return nextMoves;
    }

    private boolean hasWon(Piece player) {
        int pattern = 0b0000000000000000000000000;      // 25-bit pattern for the 5x5 cells
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == player) {
                    pattern |= (1 << (row * BOARD_SIZE + col));
                }
            }
        }

        for (int winningPattern : winningPatterns) {
            if ((pattern & winningPattern) == winningPattern) return true;
        }

        return false;
    }

    private int[] winningPatterns = {
            // Rows
            0b1111100000000000000000000,
            0b0000011111000000000000000,
            0b0000000000111110000000000,
            0b0000000000000001111100000,
            0b0000000000000000000011111,

            // Cols
            0b1000010000100001000010000,
            0b0100001000010000100001000,
            0b0010000100001000010000100,
            0b0001000010000100001000010,
            0b0000100001000010000100001,

            // Diagonals
            0b1000001000001000001000001,
            0b0000100010001000100010000,
    };

    @Override
    public void setSide(Piece p) {
        mySeed = p;
        oppSeed = mySeed == Piece.ROUND ? Piece.CROSS : Piece.ROUND;
    }

    @Override
    public Piece getSide() {
        return mySeed;
    }

    @Override
    public ArenaPosition move(Piece[][] board, ArenaPosition last) {
        this.board = board;

        long start = System.currentTimeMillis();
        int[] result = minimax(MINIMAX_DEPTH, mySeed);
        int[] optResult = minimax(MINIMAX_DEPTH, oppSeed);
        long end = System.currentTimeMillis();

        System.out.println("Took " + (end - start) + "ms to decide the move");
        System.out.println("AI Move My Seed (" + result[1] + ", " + result[2] + ") with Score ==> " + result[0]);
        System.out.println("AI Move Opp Seed (" + optResult[1] + ", " + optResult[2] + ") with Score ==> " + optResult[0]);

        if (Math.abs(result[0]) < Math.abs(optResult[0])) {
            // Don't allow opponent to win
            return new ArenaPosition(optResult[1], optResult[2]);
        }

        // Put to the best possible place
        return new ArenaPosition(result[1], result[2]);
    }

    @Override
    public String getName() {
        return AI_NAME;
    }

    @Override
    public String toString() {
        return AI_NAME;
    }
}
