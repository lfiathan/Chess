
package com.chessgame.Game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.chessgame.Boards.*;
import com.chessgame.Pieces.*;

public class GameGenerate {
  public static Board board = new Board();

  static ArrayList<Piece> wPieces = new ArrayList<Piece>();
  static ArrayList<Piece> bPieces = new ArrayList<Piece>();
  static boolean player = true;

  public Piece active = null;
  public static boolean drag = false;
  public static ArrayList<Piece> AllPieces = new ArrayList<Piece>();

  ArrayList<Move> allPossiblesMoves = new ArrayList<Move>();

  static List<Move> allPlayersMove = new ArrayList<Move>();
  public static List<Move> allEnemysMove = new ArrayList<Move>();
  static boolean gameOver = false;

  // Added static King declarations
  static King wk;
  static King bk;

  public GameGenerate() {
    new PieceImages();
    loadFenPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
  }

  public void start() {
    fillPieces();
    generatePlayersTurnMoves(board);
    generateEnemysMoves(board);
    checkPlayersLegalMoves();
  }

  public void draw(Graphics g, int x, int y, JPanel panel) {
    drawBoard(g);
    drawPiece(g, panel);
    drawPossibleMoves(g, panel);
    drag(active, x, y, g, panel);
    drawKingInCheck(player, g, panel);
  }

  public void drawPossibleMoves(Graphics g, JPanel panel) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(new BasicStroke(3));
    if (active != null) {
      active.showMoves(g2, panel);
    }
  }

  public void drawPiece(Graphics g, JPanel panel) {
    for (Piece p : AllPieces) {
      p.draw(g, false, panel);
    }
  }

  public void drag(Piece piece, int x, int y, Graphics g, JPanel panel) {
    if (piece != null && drag == true) {
      piece.draw2(g, player, x, y, panel);
    }
  }

  public static void generatePlayersTurnMoves(Board board) {
    allPlayersMove = new ArrayList<Move>();
    for (Piece p : AllPieces) {
      if (p.isWhite() == player) {
        p.fillAllPseudoLegalMoves(board);
        allPlayersMove.addAll(p.getMoves());
      }
    }
  }

  public static void generateEnemysMoves(Board board) {
    allEnemysMove = new ArrayList<Move>();
    for (Piece p : AllPieces) {
      if (p.isWhite() != player) {
        p.fillAllPseudoLegalMoves(board);
        allEnemysMove.addAll(p.getMoves());
      }
    }
  }

  public void drawKingInCheck(boolean isWhite, Graphics g, JPanel panel) {
    g.setColor(Color.RED);
    if (isWhite && wk.isInCheck()) {
      g.drawRect(wk.getXcord() * Piece.size, wk.getYcord() * Piece.size, Piece.size, Piece.size);
    } else if (bk.isInCheck()) {
      g.drawRect(bk.getXcord() * Piece.size, bk.getYcord() * Piece.size, Piece.size, Piece.size);
    }
    panel.revalidate();
    panel.repaint();
  }

  public void drawBoard(Graphics g) {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if ((i + j) % 2 == 1) {
          g.setColor(new Color(198, 150, 186));
        } else {
          g.setColor(new Color(238, 238, 210));
        }
        g.fillRect(i * Piece.size, j * Piece.size, Piece.size, Piece.size);
      }
    }
  }

  public static void fillPieces() {
    wPieces = new ArrayList<Piece>();
    bPieces = new ArrayList<Piece>();
    for (Piece p : AllPieces) {
      if (p.isWhite()) {
        wPieces.add(p);
      } else {
        bPieces.add(p);
      }
    }
  }

  public void addToBoard(int x, int y, char c, boolean isWhite) {
    switch (String.valueOf(c).toUpperCase()) {
      case "R":
        AllPieces.add(new Rook(x, y, isWhite, board, isWhite ? 5 : -5));
        break;
      case "N":
        AllPieces.add(new Knight(x, y, isWhite, board, isWhite ? 3 : -3));
        break;
      case "B":
        AllPieces.add(new Bishop(x, y, isWhite, board, isWhite ? 3 : -3));
        break;
      case "Q":
        AllPieces.add(new Queen(x, y, isWhite, board, isWhite ? 8 : -8));
        break;
      case "K":
        King king = new King(x, y, isWhite, board, isWhite ? 10 : -10);
        AllPieces.add(king);
        if (isWhite) {
          wk = king;
        } else {
          bk = king;
        }
        break;
      case "P":
        AllPieces.add(new Pawn(x, y, isWhite, board, isWhite ? 1 : -1));
        break;
    }
  }

  public void loadFenPosition(String fenString) {
    String[] parts = fenString.split(" ");
    String position = parts[0];
    int row = 0, col = 0;
    for (char c : position.toCharArray()) {
      if (c == '/') {
        row++;
        col = 0;
      }
      if (Character.isLetter(c)) {
        if (Character.isLowerCase(c)) {
          addToBoard(col, row, c, false);
        } else {
          addToBoard(col, row, c, true);
        }
        col++;
      }
      if (Character.isDigit(c)) {
        col += Integer.parseInt(String.valueOf(c));
      }
    }

    if (parts[1].equals("w")) {
      player = true;
    } else {
      player = false;
    }
  }

  public static void checkPlayersLegalMoves() {
    List<Piece> pieces = null;
    if (player) {
      pieces = wPieces;
    } else {
      pieces = bPieces;
    }
    for (Piece p : pieces) {
      checkLegalMoves(p);
    }
  }

  public static void checkLegalMoves(Piece piece) {
    List<Move> movesToRemove = new ArrayList<Move>();
    Board clonedBoard = board.getNewBoard();
    Piece clonedActive = piece.getClone();

    for (Move move : clonedActive.getMoves()) {
      clonedBoard = board.getNewBoard();
      clonedActive = piece.getClone();

      clonedActive.makeMove(move.getToX(), move.getToY(), clonedBoard);

      List<Piece> enemyPieces = new ArrayList<Piece>();
      Piece king = null;

      if (piece.isWhite()) {
        enemyPieces = bPieces;
        king = wk;
      } else {
        enemyPieces = wPieces;
        king = bk;
      }

      for (Piece enemyP : enemyPieces) {
        Piece clonedEnemyPiece = enemyP.getClone();
        clonedEnemyPiece.fillAllPseudoLegalMoves(clonedBoard);

        for (Move bMove : clonedEnemyPiece.getMoves()) {
          if (!(clonedActive instanceof King) && bMove.getToX() == king.getXcord()
              && bMove.getToY() == king.getYcord()
              && clonedBoard.getGrid()[enemyP.getXcord()][enemyP.getYcord()] == enemyP
              .getValueInTheboard()) {
            movesToRemove.add(move);
          } else if (clonedActive instanceof King) {
            if (bMove.getToX() == clonedActive.getXcord() && bMove.getToY() == clonedActive.getYcord()
                && clonedBoard.getGrid()[enemyP.getXcord()][enemyP.getYcord()] == enemyP
                .getValueInTheboard()) {
              movesToRemove.add(move);
                }
          }
        }
      }
    }

    for (Move move : movesToRemove) {
      piece.getMoves().remove(move);
    }
  }
}
