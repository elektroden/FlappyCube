import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyCube implements ActionListener, MouseListener, KeyListener {
	private final int frame_width = 800;
	private final int frame_height = 800;
	
	private int ticks, yMotion, score;
	private boolean gameOver = false;
	private boolean started;
	
	private Renderer renderer;
	private Rectangle bird;
	private ArrayList<Rectangle> columns;
	private Random rand;
	
	public FlappyCube() {
		JFrame frame = new JFrame();
		renderer = new Renderer(this);
		Timer timer = new Timer(20, this);
		
		rand = new Random();
		
		frame.addKeyListener(this);
		frame.addMouseListener(this);
		frame.add(renderer);
		frame.setSize(frame_width, frame_height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("FlappyCube");
		frame.setResizable(false);
		frame.setVisible(true);
		
		bird = new Rectangle((frame_width/2)-10,(frame_height/2)-10, 20, 20);
		columns = new ArrayList<Rectangle>();
		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		
		timer.start();
	}
	
	public void addColumn(boolean atStart) {
		int blankSpace = 300;
		int columnWidth = 100;
		int columnHeight = 50 + rand.nextInt(300);
		
		if(atStart) {
			columns.add(new Rectangle(frame_width + columnWidth + columns.size() * 300, frame_height - columnHeight - 120, columnWidth, columnHeight));
			columns.add(new Rectangle(frame_width + columnWidth + (columns.size() - 1) * 300, 0, columnWidth, frame_height - columnHeight - blankSpace));
		}
		else {
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, frame_height - columnHeight - 120, columnWidth, columnHeight));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, columnWidth, frame_height - columnHeight - blankSpace));
		}
		
	}
	
	public void cubeJump() {
		if(gameOver) {
			bird = new Rectangle((frame_width/2)-10,(frame_height/2)-10, 20, 20);
			columns.clear();
			yMotion = 0;
			score = 0;
			
			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);
			
			gameOver = false;	
		}
		
		if(!started) {
			started = true;
		}
		else if (!gameOver){
			if(yMotion > 0 ) {
				yMotion = 0;
			}
			yMotion -= 10;
		}
	}
	
	public void paintColumn(Graphics g, Rectangle column) {
		g.setColor(Color.GREEN.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}

	public void repaint(Graphics g) {
		g.setColor(Color.CYAN);
		g.fillRect(0, 0, frame_width, frame_height);
		
		g.setColor(Color.RED);
		g.fillRect(bird.x, bird.y, bird.width, bird.height);
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, frame_height - 120, frame_width, 150);
		
		g.setColor(Color.GREEN);
		g.fillRect(0, frame_height - 120, frame_width, 20);
		
		for (Rectangle column : columns) {
			paintColumn(g, column);
		}
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", 1, 100));
		
		if (!started) {
			g.drawString("Click to start!", 75, frame_height / 2 - 25);
		}
		
		if (gameOver) {
			g.drawString("Game Over!", 125, frame_height / 2 - 25);
			
		}
		
		if (!gameOver && started) {
			g.drawString(String.valueOf(score), frame_width  / 2 - 25, 100 );
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		int speed = 10;
		
		ticks++;
		
		if (started) {
			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);
				
				column.x -= speed;
			}
			
			if (ticks % 2 == 0 && yMotion < 15) {
				yMotion += 2;
			}
			
			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);
				
				if (column.x + column.width < 0) {
					columns.remove(column);
					if (column.y == 0) {
						addColumn(false);
					}
				}
			}
			
			bird.y += yMotion;
			
			for (Rectangle column : columns) {
				if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
					score++;
				}
				
				if (column.intersects(bird)) {
					gameOver = true;
					
					if(bird.x <= column.x) {
						bird.x = column.x - bird.width;
					}
					else {
						if (column.y != 0) {
							bird.y = column.y - bird.height;
						}
						else if (bird.y < column.height) {
							bird.y = column.height;
						}
					}
					
				}
			}
			
			if (bird.y > frame_height - 120 || bird.y < 0) {
				gameOver = true;
			}
			
			if (bird.y + yMotion >= frame_height - 120) {
				bird.y = frame_height - 120 - bird.height;
			}
			
		}
		
		renderer.repaint();
		
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		cubeJump();
	}

	@Override
	public void mouseEntered(MouseEvent me) {}

	@Override
	public void mouseExited(MouseEvent me) {}

	@Override
	public void mousePressed(MouseEvent me) {}

	@Override
	public void mouseReleased(MouseEvent me) {}

	@Override
	public void keyPressed(KeyEvent ke) {}

	@Override
	public void keyReleased(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
			cubeJump();
		}
	}

	@Override
	public void keyTyped(KeyEvent ke) {}

}
