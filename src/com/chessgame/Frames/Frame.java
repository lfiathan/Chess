package com.chessgame.Frame;

import javax.swing.JFrame;
import java.awt.Dimension;

public class Frame extends JFrame {
	private static final int WIDTH = 960;
	private static final int HEIGHT = 960;
	
	public Frame() {
		this.setContentPane(new Panel());
		this.setTitle("Chess");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		this.getContentPane().setPreferredSize(new Dimension(WIDTH,HEIGHT));
		this.pack();
		this.setLocationRelativeTo(null);
	}
}
