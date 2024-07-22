import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Othello {
    int turn;
    int winner;
    int board[][];

    public Othello(String filename) throws Exception {
        File file = new File(filename);
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(file);
        turn = sc.nextInt();
        board = new int[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                board[i][j] = sc.nextInt();
            }
        }
        winner = -1;
    }

    private boolean isValidMove(int x, int y, int color) {
        if (board[x][y] != -1) return false;

        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int dir = 0; dir < 8; dir++) {
            int nx = x + dx[dir], ny = y + dy[dir];
            boolean foundOpponent = false;

            while (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && board[nx][ny] == (color ^ 1)) {
                foundOpponent = true;
                nx += dx[dir];
                ny += dy[dir];
            }

            if (foundOpponent && nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && board[nx][ny] == color) {
                return true;
            }
        }
        return false;
    }

    private void flipTiles(int x, int y, int color) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int dir = 0; dir < 8; dir++) {
            int nx = x + dx[dir], ny = y + dy[dir];
            ArrayList<int[]> tilesToFlip = new ArrayList<>();

            while (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && board[nx][ny] == (color ^ 1)) {
                tilesToFlip.add(new int[]{nx, ny});
                nx += dx[dir];
                ny += dy[dir];
            }

            if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && board[nx][ny] == color) {
                for (int[] tile : tilesToFlip) {
                    board[tile[0]][tile[1]] = color;
                }
            }
        }
    }

    private int minimax(int depth, int color, int alpha, int beta) {
        if (depth == 0) {
            return boardScore();
        }

        boolean hasValidMove = false;

        if (color == turn) {
            int maxScore = Integer.MIN_VALUE;

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (isValidMove(i, j, color)) {
                        hasValidMove = true;
                        int temp = board[i][j];
                        board[i][j] = color;
                        flipTiles(i, j, color);

                        maxScore = Math.max(maxScore, minimax(depth - 1, color ^ 1, alpha, beta));
                        alpha = Math.max(alpha, maxScore);

                        board[i][j] = temp;

                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return hasValidMove ? maxScore : boardScore();
        } else {
            int minScore = Integer.MAX_VALUE;

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (isValidMove(i, j, color)) {
                        hasValidMove = true;
                        int temp = board[i][j];
                        board[i][j] = color;
                        flipTiles(i, j, color);

                        minScore = Math.min(minScore, minimax(depth - 1, color ^ 1, alpha, beta));
                        beta = Math.min(beta, minScore);

                        board[i][j] = temp;

                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return hasValidMove ? minScore : boardScore();
        }
    }

    public int boardScore() {
        int blackTiles = 0;
        int whiteTiles = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) {
                    blackTiles++;
                } else if (board[i][j] == 1) {
                    whiteTiles++;
                }
            }
        }
        return turn == 0 ? blackTiles - whiteTiles : whiteTiles - blackTiles;
    }

    public int bestMove(int k) {
        int maxScore = Integer.MIN_VALUE;
        int bestTile = -1;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(i, j, turn)) {
                    int temp = board[i][j];
                    board[i][j] = turn;
                    flipTiles(i, j, turn);

                    int score = minimax(k - 1, turn ^ 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

                    if (score > maxScore) {
                        maxScore = score;
                        bestTile = i * 8 + j;
                    } else if (score == maxScore && i * 8 + j < bestTile) {
                        bestTile = i * 8 + j;
                    }

                    board[i][j] = temp;
                }
            }
        }
        return bestTile;
    }

    public ArrayList<Integer> fullGame(int k) {
        ArrayList<Integer> moveList = new ArrayList<>();

        while (true) {
            int bestTileIndex = bestMove(k);
            if (bestTileIndex == -1) {
                break;
            }

            int x = bestTileIndex / 8;
            int y = bestTileIndex % 8;
            board[x][y] = turn;
            flipTiles(x, y, turn);

            moveList.add(bestTileIndex);
            turn = turn ^ 1;
        }

        int blackTiles = 0;
        int whiteTiles = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) {
                    blackTiles++;
                } else if (board[i][j] == 1) {
                    whiteTiles++;
                }
            }
        }

        if (blackTiles > whiteTiles) {
            winner = 0;
        } else if (whiteTiles > blackTiles) {
            winner = 1;
        }

        return moveList;
    }

    public int[][] getBoardCopy() {
        int copy[][] = new int[8][8];
        for (int i = 0; i < 8; ++i)
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        return copy;
    }

    public int getWinner() {
        return winner;
    }

    public int getTurn() {
        return turn;
    }

    public static void main(String[] args) {
        try {
            Othello game = new Othello("board.txt");
            int depth = 10;
            ArrayList<Integer> moves = game.fullGame(depth);
            
            assert moves.size() > 0 : "No moves made!";
            assert game.getWinner() == 0 || game.getWinner() == 1 : "Invalid winner!";

            System.out.println("Moves made: ");
            for(int i = 0; i<moves.size(); i++){
                System.out.println(moves.get(i)/8 + " " + moves.get(i)%8);
            }
            System.out.println("Winner: " + (game.getWinner() == 0 ? "Black" : "White"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}