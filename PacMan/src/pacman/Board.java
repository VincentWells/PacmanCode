/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Vincent Wells
 *
 */
class Board {

	char[][] board;
	boolean moreLevels = true;
	private int map = 1;
	private int hx;
	private int hy;
	private boolean levels = false;
	public int getMap() {
		return map;
	}
	public void setMap(int map) {
		this.map = map;
	}

	/**
	 * Here I generate the board for the first time, by converting the map into
	 * a 2D character by character array, which the program will henceforth use
	 * as the playing board. Should only have to run once.
	 * @throws Exception 
	 */
	void initBoard() throws Exception {
		String input = "src/mazes/" + map + ".txt";
		try {
			FileReader f = new FileReader(input);
			BufferedReader buff = new BufferedReader(f);
			int x;
			int y;
			String line = buff.readLine();
			Scanner scan = new Scanner(line);
			x = scan.nextInt();
			y = scan.nextInt();
			hx = scan.nextInt();
			hy = scan.nextInt();
			board = new char[x][y];
			for (int i = 0; i < board.length; i++) {
				String str = buff.readLine();
				for (int j = 0; j < board[0].length; j++) {
					board[i] = str.toCharArray();
				}
			}
			levels = true;
			buff.close();
			scan.close();
		} catch (IOException e) {
			if (levels){
			System.out.println("You have completed all levels sucessfully");
			moreLevels = false;
			}
			else{
				throw NoLevels();
			}
		}
		
	}
	Exception NoLevels(){
		System.err.println("No valid levels. Please check to make sure the directory is valid");
		return null;
	}

	/**
	 * these getter/setter's pass the spawn point's for the Hydra to other methods.
	 * these are specified on the first line of the map .txt file.
	 * @return
	 */
	public int getHx() {
		return hx;
	}

	public void setHx(int hx) {
		this.hx = hx;
	}

	public int getHy() {
		return hy;
	}

	public void setHy(int hy) {
		this.hy = hy;
	}

	/**
	 * Prints the current array board. Will run every turn
	 */
	void printBoard() {
		for (int i = 0; i < board.length; i++) {
			System.out.println();
			for (int j = 0; j < board[0].length; j++) {
				System.out.print(board[i][j]);
			}
		}
		System.out.println();
	}
	/**
	 * Counts the dots on the board to make sure the game hasn't been ended.
	 * @return
	 */
	int dotCount() {
		int count = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == '.') {
					count++;
				}
			}
		}
		return count;
	}
}
/*


*/
