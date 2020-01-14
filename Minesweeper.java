package ex12;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class Minesweeper extends JFrame {
	Cells cells;
	int phase;
	JPanel pane;
	ArrayList<JToggleButton> jtbs;
	final int BUTTON_X=8;
	final int BUTTON_Y=8;
	final int BOMBS=10;
	static final int BOMB=-1;
	static final int NOTHING=0;

	public static void main(String[] args) {
		JFrame w = new Minesweeper("Minesweeper");
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		w.setSize(600, 600);
		w.setVisible(true);
	}

	public Minesweeper(String title) {
		super(title);
		phase = 0;
		cells = new Cells(BUTTON_X,BUTTON_Y,BOMBS);
		jtbs = new ArrayList<JToggleButton>();
		pane = (JPanel) getContentPane();
		pane.setLayout(new GridLayout(BUTTON_X, BUTTON_Y));
		JMenuBar menuBar=new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu=new JMenu("メニュー");
		menuBar.add(fileMenu);
		JMenuItem item;
		item=new JMenuItem(new NewAction());
		fileMenu.add(item);

		for (int i = 0; i < BUTTON_X*BUTTON_Y; i++) {
			JToggleButton jbutton = new JToggleButton();
			jtbs.add(jbutton);
			jbutton.addMouseListener(new ButtonListener(i));
			pane.add(jbutton);
		}

	}
	class NewAction extends AbstractAction{
		NewAction(){
			putValue(Action.NAME,"新しいゲームへ");
			putValue(Action.SHORT_DESCRIPTION,"new game");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			jtbs.removeAll(jtbs);
			cells=new Cells(BUTTON_X,BUTTON_Y,BOMBS);
			pane.removeAll();
			pane = (JPanel) getContentPane();
			pane.setLayout(new GridLayout(BUTTON_X, BUTTON_Y));
			//pane.add(new JButton("test"));
			for (int i = 0; i < BUTTON_X*BUTTON_Y; i++) {
				JToggleButton jbutton = new JToggleButton();
				jtbs.add(jbutton);
				jbutton.addMouseListener(new ButtonListener(i));
				pane.add(jbutton);
			}
			pane.revalidate();
		}

	}

	class ButtonListener extends MouseAdapter {
		int number;
		ButtonListener(int number) {
			this.number = number;
		}

		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1&&!cells.cells.get(number).flg) {//左クリック＆＆フラグなし
				phase = 0;
				//System.out.println("left");
				//System.out.println(number);
				if (!jtbs.get(number).isEnabled()) {//すでに開かれたマスをクリックした時マスの周りにいくつフラグが置いてあるかのカウント
					int x = number / BUTTON_X;
					int y = number % BUTTON_Y;
					int flgCount = 0;
					for (int dx = -1; dx <= 1; dx++) {
						for (int dy = -1; dy <= 1; dy++) {
							int nx = x + dx;
							int ny = y + dy;
							if (0 <= nx && nx < BUTTON_X && 0 <= ny && ny < BUTTON_Y) {
								if (cells.cells.get(nx * BUTTON_X + ny).flg)
									flgCount++;
							}
						}
					}
					//System.out.println(flgCount);
					if (flgCount == cells.cells.get(number).status) {//マスの数字と周りのフラグの数があっていたらフラグ以外の八近傍を開くもしフラグが間違えていたら爆発する
						for (int dx = -1; dx <= 1; dx++) {
							boolean end = false;
							for (int dy = -1; dy <= 1; dy++) {
								int nx = x + dx;
								int ny = y + dy;
								if (0 <= nx && nx < BUTTON_X && 0 <= ny && ny < BUTTON_Y) {
									if (!cells.cells.get(nx * BUTTON_X + ny).flg) {
										if (cells.cells.get(nx * BUTTON_Y + ny).status == BOMB) {
											bomb();
											end = true;
											break;
										}
										if(cells.cells.get(nx*BUTTON_X+ny).status==NOTHING) {
											open0(nx*BUTTON_X+ny);
										}
										jtbs.get(nx * BUTTON_X + ny)
												.setText(String.valueOf(cells.cells.get(nx * BUTTON_X + ny).status));
										jtbs.get(nx * BUTTON_X + ny).setEnabled(false);
									}
								}
							}
							if (end)
								break;
						}
					}
				}
				if (cells.cells.get(number).status == BOMB)
					bomb();
				else if (cells.cells.get(number).status == NOTHING)
					open0(number);
				else {
					jtbs.get(number).setText(String.valueOf(cells.cells.get(number).status));
					jtbs.get(number).setSelected(false);
					jtbs.get(number).setEnabled(false);
				}
				for (int i = 0; i < BUTTON_X*BUTTON_Y; i++) {//爆弾以外のマスすべて開いたかどうか
					if (!jtbs.get(i).isEnabled())
						phase++;
				}
				if (phase == BUTTON_X*BUTTON_Y-BOMBS) {//ゲーム成功処理
					Object[] msg = { "you win" };
					JOptionPane.showConfirmDialog(pane, msg, "Infomation", JOptionPane.DEFAULT_OPTION);

				}
			}
			if (e.getButton() == MouseEvent.BUTTON3 && jtbs.get(number).isEnabled()) {
				//右クリックでフラグをつけ外しする
				if (!cells.cells.get(number).flg) {//ついていなかったら
					//System.out.println("right");
					jtbs.get(number).setText("flag");
					jtbs.get(number).setSelected(true);
					cells.cells.get(number).setFlg(true);
				} else {
					jtbs.get(number).setText("");
					jtbs.get(number).setSelected(false);
					cells.cells.get(number).setFlg(false);
				}
			}
		}

		private void bomb() {//爆弾クリックしてしまった時の処理
			for (int i = 0; i < BUTTON_X*BUTTON_Y; i++) {
				if (cells.cells.get(i).status != BOMB) {
					jtbs.get(i).setText(String.valueOf(cells.cells.get(i).status));
					jtbs.get(i).setEnabled(false);
				} else {
					jtbs.get(i).setText("bomb");
					jtbs.get(i).setSelected(true);
					jtbs.get(i).setEnabled(false);
				}
			}
			Object[] msg = { "you lose" };
			JOptionPane.showConfirmDialog(pane, msg, "Infomation", JOptionPane.DEFAULT_OPTION);

		}

		public void open0(int number) {//０のマスをクリックすると数字の書いてあるマスまで開けるやつ
			if (!cells.cells.get(number).flg) {
				jtbs.get(number).setText(String.valueOf(cells.cells.get(number).status));
				jtbs.get(number).setSelected(false);
				jtbs.get(number).setEnabled(false);
			}
			int x = number / BUTTON_X;
			int y = number % BUTTON_Y;
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					int nx = x + dx;
					int ny = y + dy;
					if (0 <= nx && nx < BUTTON_X && 0 <= ny && ny < BUTTON_Y) {
						if (cells.cells.get(nx * BUTTON_X + ny).status == NOTHING && jtbs.get(nx * BUTTON_X + ny).isEnabled()
								&& !cells.cells.get(nx * BUTTON_X + ny).flg) {
							open0(nx * BUTTON_X + ny);
						} else {
							if (!cells.cells.get(nx * BUTTON_X + ny).flg) {
								jtbs.get(nx * BUTTON_X + ny).setText(String.valueOf(cells.cells.get(nx * BUTTON_X + ny).status));
								jtbs.get(nx * BUTTON_X + ny).setEnabled(false);
							}
						}

					}
				}
			}

			return;
		}
	}

}