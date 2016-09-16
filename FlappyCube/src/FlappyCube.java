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
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyCube implements ActionListener, MouseListener, KeyListener {
	private final int frame_width = 800;
	private final int frame_height = 800;
	private final int game_speed = 20; // Lower is faster, Default: 20
	
	private int ticks, yMotion, score;
	private boolean gameOver = false;
	private boolean started;
	
	private Renderer renderer;
	private Rectangle cube;
	private BufferedImage cubeImg;
	private BufferedImage pillarImg;
	private BufferedImage groundImg;
	
	private ArrayList<Rectangle> columns;
	private Random rand;
	
	public FlappyCube() {
		JFrame frame = new JFrame();
		renderer = new Renderer(this);
		Timer timer = new Timer(game_speed, this);
		
		rand = new Random();
		
		frame.addKeyListener(this);
		frame.addMouseListener(this);
		frame.add(renderer);
		frame.setSize(frame_width, frame_height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("FlappyCube");
		frame.setResizable(false);
		frame.setVisible(true);
		
		cube = new Rectangle((frame_width/2)-10,(frame_height/2)-10, 20, 20);
		columns = new ArrayList<Rectangle>();
		
		try {
			cubeImg = ImageIO.read(new File("cube.jpg"));
			pillarImg = ImageIO.read(new File("pillar.jpg"));
			groundImg = ImageIO.read(new File("ground.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		
		timer.start();
	}
	
	private void addColumn(boolean atStart) {
		int blankSpace = 300;
		int columnWidth = 100;
		int columnHeight = 50 + rand.nextInt(300);
		
		if(atStart) {
			columns.add(new Rectangle(frame_width + columnWidth + columns.size() * 300, frame_height - columnHeight - 120, columnWidth, columnHeight + 10));
			columns.add(new Rectangle(frame_width + columnWidth + (columns.size() - 1) * 300, 0, columnWidth, frame_height - columnHeight - blankSpace));
		}
		else {
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, frame_height - columnHeight - 120, columnWidth, columnHeight + 10));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, columnWidth, frame_height - columnHeight - blankSpace));
		}
		
	}
	
	private void cubeJump() {
		if(gameOver) {
			cube = new Rectangle((frame_width/2)-10,(frame_height/2)-10, 20, 20);
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
	
	private void paintColumn(Graphics g, Rectangle column) {
		g.setColor(Color.GREEN.darker());
		g.drawImage(cropImage(pillarImg, column), column.x, column.y, null);
//		g.fillRect(column.x, column.y, column.width, column.height);
	}
	
	private BufferedImage cropImage(BufferedImage src, Rectangle pillar) {
		BufferedImage dest = src.getSubimage(0, 0,  pillar.width, pillar.height);
		return dest;
	}

	protected void repaint(Graphics g) {
		
		// Sky color
		g.setColor(Color.CYAN);
		g.fillRect(0, 0, frame_width, frame_height);
		
		
		// Cube
		g.setColor(Color.RED);
		g.drawImage(cubeImg, cube.x, cube.y, null);
		
		// Ground Color
		g.drawImage(groundImg, 0, frame_height - 120, null);
		
		// Draws the Columns
		for (Rectangle column : columns) {
			paintColumn(g, column);
		}
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", 1, 100));
		
		if (!started) {
			g.drawString("Click to start!", 75, frame_height / 2 - 25);
		}
		
		if (gameOver) {
			g.drawString("Game Over!", 125, frame_height / 2 - 125);
			g.drawString("Score: " + String.valueOf(score), 200, 400 );
			
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
			
			cube.y += yMotion;
			
			// Collision check
			for (Rectangle column : columns) {
				if (column.y == 0 && cube.x + cube.width / 2 > column.x + column.width / 2 - 10 && cube.x + cube.width / 2 < column.x + column.width / 2 + 10) {
					score++;
				}
				
				if (column.intersects(cube)) {
					gameOver = true;
					
					// Cube and column collision (doesn't go through column)
					if(cube.x <= column.x) {
						cube.x = column.x - cube.width;
					}
					else {
						if (column.y != 0) {
							cube.y = column.y - cube.height;
						}
						else if (cube.y < column.height) {
							cube.y = column.height;
						}
					}
					
				}
			}
			
			// Frame and Ground Collision
			if (cube.y > frame_height - 115 || cube.y < 0) {
				gameOver = true;
			}
			
			// Ground collision (doesn't go through ground)
			if (cube.y + yMotion >= frame_height - 110) {
				cube.y = frame_height - 110 - cube.height;
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
