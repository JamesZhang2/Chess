package model.player;

import model.board.Board;
import model.move.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HumanCLIPlayer extends Player {
    public HumanCLIPlayer(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public Action play(Board board) {
        System.out.println(board.piecesToString(!isWhite));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Move move = null;
        while (move == null) {
            try {
                System.out.print("Your move: ");
                String input = reader.readLine().strip();
                if (input.equalsIgnoreCase("resign")) {
                    return new Action(Action.Type.RESIGN);
                } else if (input.equalsIgnoreCase("draw")
                        || input.equalsIgnoreCase("offer draw")) {
                    return new Action(Action.Type.OFFER_DRAW);
                } else if (input.equalsIgnoreCase("help")) {
                    printHelp();
                    System.out.print("Your move: ");
                } else {
                    move = MoveParser.parse(input, board);
                    System.out.println();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (MalformedMoveException e) {
                System.out.println("The move you entered is malformed. Please try again.");
            } catch (IllegalMoveException e) {
                System.out.println("The move you entered is illegal. Please try again.");
            } catch (AmbiguousMoveException e) {
                System.out.println("The move you entered is ambiguous. Please try again.");
            }
        }
        return new Action(move);
    }

    @Override
    public boolean considerDraw(Board board) {
        System.out.println(board.piecesToString(!isWhite));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("Your opponent offers a draw. Accept draw? (Y/N) ");
            try {
                String input = reader.readLine().strip();
                if (input.equalsIgnoreCase("Y")) {
                    return true;
                } else if (input.equalsIgnoreCase("N")) {
                    return false;
                } else {
                    System.out.println("Please only enter Y or N.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void init(Board board) {
        System.out.println("Initial board:");
        System.out.println(board.piecesToString(!isWhite));
        printHelp();
    }

    @Override
    public void win(Board board) {
        System.out.println("Final board:");
        System.out.println(board.piecesToString(!isWhite));
        System.out.println("You won!");
    }

    @Override
    public void draw(Board board) {
        System.out.println("Final board:");
        System.out.println(board.piecesToString(!isWhite));
        System.out.println("You drew!");
    }

    @Override
    public void lose(Board board) {
        System.out.println("Final board:");
        System.out.println(board.piecesToString(!isWhite));
        System.out.println("You lost!");
    }

    @Override
    public void drawAccepted() {
        System.out.println("Draw accepted!");
    }

    @Override
    public void drawDeclined() {
        System.out.println("Draw declined!");
    }

    /**
     * Print help messages
     */
    public void printHelp() {
        System.out.println("You are playing as " + (isWhite ? "white" : "black") + ".");
        System.out.println("Enter a move using Standard Algebraic Notation (SAN).");
        System.out.println("Type \"resign\" to resign, \"draw\" or \"offer draw\" to offer a draw.");
        System.out.println("Type \"help\" to see this help message again.");
    }
}
