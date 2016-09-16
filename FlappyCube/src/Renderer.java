import java.awt.Graphics;

import javax.swing.JPanel;

public class Renderer extends JPanel {
	
	private static final long serialVersionUID = -5606690746521300613L;
	private FlappyCube cubeObject;
	
	public Renderer(FlappyCube cubeObject) {
		this.cubeObject = cubeObject;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		cubeObject.repaint(g);
	}

}
