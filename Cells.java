package ex12;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Cells {
	HashSet<Integer> mineButtons;
	ArrayList<Cell> cells;
	int x;
	int y;
	int bombs;

	public Cells(int x,int y,int bombs) {
		this.x=x;
		this.y=y;
		this.bombs=bombs;
		mineButtons = new HashSet();
		cells = new ArrayList();
		Random rand = new Random();
		while (mineButtons.size() < bombs) {
			Integer i = Integer.valueOf(rand.nextInt(x*y));
			mineButtons.add(i);
		}
		for (int i = 0; i < x*y; i++) {
			int j = 0;
			if (mineButtons.contains(i)) {
				j = -1;
				System.out.println(i);
			}
			cells.add(new Cell(j));
		}
		changeStatus();

	}

	public void changeStatus() {
		for (int i = 0; i < x*y; i++) {
			if (cells.get(i).status != -1) {
				if (i % x != 0 && cells.get(i - 1).status == -1) {//left
					cells.get(i).addStatus();
				}
				if ((i + 1) % x != 0 && cells.get(i + 1).status == -1) {//right
					cells.get(i).addStatus();
				}
				if (i > x-1 && cells.get(i - x).status == -1) {//up
					cells.get(i).addStatus();
				}
				if (i < x*y-x && cells.get(i + x).status == -1) {//down
					cells.get(i).addStatus();
				}
				if (i % x != 0 && i > x-1 && cells.get(i - (x+1)).status == -1) {//leftUp
					cells.get(i).addStatus();
				}
				if ((i + 1) % x != 0 && i > x-1 && cells.get(i - (x-1)).status == -1) {//rightUp
					cells.get(i).addStatus();
				}
				if (i % x != 0 && i < x*y-x && cells.get(i + (x-1)).status == -1) {//leftDown
					cells.get(i).addStatus();
				}
				if ((i + 1) % x != 0 && i < x*y-x && cells.get(i + (x+1)).status == -1) {//rightDown
					cells.get(i).addStatus();
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		Cells c = new Cells(8,8,10);
		String s = "";
		for (int i = 0; i < 64; i++) {
			s += Integer.valueOf(c.cells.get(i).status);
			if ((i + 1) % 8 == 0)
				s += "\n";
		}
		System.out.println(s);
	}

	class Cell {
		int status;
		boolean flg;//右の旗が立っているか

		public Cell(int i) {
			this.status = i;
			this.flg=false;
		}

		public void addStatus() {
			this.status++;
		}
		public void setFlg(boolean flg) {
			this.flg=flg;
		}
		public boolean getFlg() {
			return this.flg;
		}

	}

}
