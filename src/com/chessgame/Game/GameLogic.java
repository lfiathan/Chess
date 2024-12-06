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

public class GameLogic extends GameGenerate {
  public GameLogic() {
    start();
  }

  public static void changeSide() {
    player = !player;
    generateEnemysMoves(board);
    generatePlayersTurnMoves(board);
    checkPlayersLegalMoves();
    checkMate();
  }

  public void selectPiece(int x, int y) {
    if (active == null && board.getPiece(x, y) != null && board.getPiece(x, y).isWhite() == player) {
      active = board.getPiece(x, y);
    }
  }

  public static void checkMate() {
    // Check for insufficient material to win
    if (isDrawByInsufficientMaterial()) {
      JOptionPane.showMessageDialog(null, "Draw: Insufficient material");
      gameOver = true;
      return;
    }

    if (player) {
      for (Piece p : wPieces) {
        if (!p.getMoves().isEmpty()) {
          return;
        }
      }
    } else {
      for (Piece p : bPieces) {
        if (!p.getMoves().isEmpty()) {
          return;
        }
      }
    }
    if (player) {
      if (wk.isInCheck()) {
        JOptionPane.showMessageDialog(null, "check mate " + (!player ? "white" : "black") + " wins");
      } else {
        JOptionPane.showMessageDialog(null, "stalemate ");
      }
    } else {
      if (bk.isInCheck()) {
        JOptionPane.showMessageDialog(null, "check mate " + (!player ? "white" : "black") + " wins");
      } else {
        JOptionPane.showMessageDialog(null, "stalemate ");
      }
    }
    gameOver = true;
  }

  public void move(int x, int y) {
    if (active != null) {
      if (active.makeMove(x, y, board)) {
        tryToPromote(active);
        changeSide();
        active = null;
      }
      drag = false;
    }
  }

  public void tryToPromote(Piece p) {
    if (p instanceof Pawn) {
      if (((Pawn) p).madeToTheEnd()) {
        choosePiece(p, showMessageForPromotion());
      }
    }
  }

  public int showMessageForPromotion() {
    Object[] options = { "Queen", "Rook", "Knight", "Bishop" };

    drag = false;
    return JOptionPane.showOptionDialog(null, "Choose Piece To Promote to", null, 
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
        null, options, options[0]);
  }

  public static void choosePiece(Piece p, int choice) {
    switch (choice) {
      case 0:
        AllPieces.remove(p);
        p = new Queen(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 8 : -8);
        AllPieces.add(p);
        break;
      case 1:
        AllPieces.remove(p);
        p = new Rook(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 5 : -5);
        AllPieces.add(p);
        break;
      case 2:
        AllPieces.remove(p);
        p = new Knight(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 3 : -3);
        AllPieces.add(p);
        break;
      case 3:
        AllPieces.remove(p);
        p = new Bishop(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 3 : -3);
        AllPieces.add(p);
        break;
      default:
        AllPieces.remove(p);
        p = new Queen(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 8 : -8);
        AllPieces.add(p);
        break;
    }
    fillPieces();
  }

  public static boolean isDrawByInsufficientMaterial() {
    List<Piece> remainingPieces = new ArrayList<>(AllPieces);

    // Remove kings from the list
    remainingPieces.removeIf(p -> p instanceof King);

    if (remainingPieces.isEmpty()) {
      return true;
    } else if (remainingPieces.size() == 1) {
      Piece singlePiece = remainingPieces.get(0);
      return (singlePiece instanceof Knight || singlePiece instanceof Bishop);
    }

    return false;
  }
}
