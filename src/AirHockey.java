import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
public class AirHockey extends JFrame{
	private final boolean RIGHT = true;
	private final boolean LEFT = false;
	public static void main(String[] args){
		new AirHockey();
	}

	int shift = 5;
	public AirHockey(){
	//	setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()-100);
		setSize(1540, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(new Panel());
		setVisible(true);
	}
	class Panel extends JPanel{
		boolean win;
		Puck puck  = new Puck(720, 400, 45,0, 0, Color.WHITE, new Rectangle(0, 0, getWidth(), getHeight()));
		//Puck //puck2 = new Puck(400,   0, 50, -500, 500, Color.GREEN, new Rectangle(0, 0, AirHockey.this.getWidth(), AirHockey.this.getHeight()));
		Player player = new Player(500, 500, 45, Color.GREEN);
		AIPlayer ai = new AIPlayer(1000, 500, 45, Color.RED);
		Timer respawnTimer = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println(win);
				puck.x = getWidth()/2;
				if(win)
					puck.x+=puck.radius*1.5;
				else puck.x-=puck.radius*1.5;
				puck.y = getHeight()/2;
				puck.velocity.j = 0;
				puck.velocity.i = 0;
				puck.color = Color.WHITE;
				respawnTimer.stop();
			}
		});
		Timer t = new Timer(30, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				puck.setBounds(new Rectangle(shift, shift, Panel.this.getWidth()-2*shift, Panel.this.getHeight()-2*shift));
				player.setBounds(new Rectangle(Panel.this.getWidth()/2, shift, (AirHockey.this.getWidth()-2*shift)/2, AirHockey.this.getHeight()-2*shift-46));				
				ai.bounds = (Rectangle) player.bounds.clone();
				ai.bounds.x = shift;
				ai.bounds.width+=shift;
				//if(Math.pow(puck.x-player.x, 2)+Math.pow(puck.y-player.y, 2)>10+Math.pow(puck.radius+player.radius, 2))
				
				player.move((int)(MouseInfo.getPointerInfo().getLocation().getX()-AirHockey.this.getLocation().x), (int)(MouseInfo.getPointerInfo().getLocation().getY()-AirHockey.this.getLocation().y));
				ai.move(puck);
				puck.move();
				
				checkCollision(player, puck);
				checkCollision(ai, puck);
				
				
				if(puck.x>1540+puck.radius){//check if scored
					ai.score++;
					puck.x = 1000000;
					puck.velocity.j = 0;
					win = true;
					respawnTimer.start();
				}
				else if(puck.x<-puck.radius){
					player.score++;
					puck.x = 1000000;
					puck.velocity.j = 0;
					win=false;
					respawnTimer.start();
					
				}
				repaint();
			}
		});
		
		
		public void checkCollision(Player player, Puck puck){
			puck.checkCollision(player);
			///puck.checkCollision(puck2);
			double distance = Math.sqrt(Math.pow(puck.x-player.x, 2)+Math.pow(puck.y-player.y, 2));
			if(distance<Math.sqrt(Math.pow(puck.radius+player.radius, 2)))
			{
				puck.x = (puck.x-player.x)*(puck.radius+player.radius)/distance+player.x;
				puck.y = (puck.y-player.y)*(puck.radius+player.radius)/distance+player.y;
			}
		}
		public Panel(){// Transparent 16 x 16 pixel cursor image.
			BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			// Create a new blank cursor.
			Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			    cursorImg, new Point(0, 0), "blank cursor");

			// Set the blank cursor to the J
			setCursor(blankCursor);
			//addMouseMotionListener(this);
			requestFocus();
			t.start();
		}
		public void paintComponent(Graphics g){
			setBackground(Color.WHITE);
			super.paintComponent(g);
			g.setColor(new Color(0, 0, 0));
			g.fillRect(puck.bounds.x, puck.bounds.y, puck.bounds.width, puck.bounds.height);
			g.fillRect(0, 250, 50, 300);
			g.fillRect(getWidth()-51, 250, 50, 300);
			puck.paint(g);
			//puck2.paint(g);
			player.paint(g);
			ai.paint(g);
			g.setColor(Color.WHITE);
			g.drawLine(getWidth()/2, 0, getWidth()/2, 1000);
			
		}
		
	}
}

class Puck {
	public Vector2d velocity = new Vector2d(0, 0);
	public double x = 0;
	public double y = 0;
	public int radius = 40;
	Color color = Color.WHITE;
	public Rectangle bounds;
	public Puck(int x, int y, int rad, int i, int j, Color c, Rectangle bounds){
		this.x = x;
		this.y = y;
		velocity.i = i;
		velocity.j = j;
		color = c;
		this.bounds = bounds;
		radius = rad;
	}
	public void move(){
		x+=velocity.i*=.98;
		y+=velocity.j*=.98;
		if(x<=radius+bounds.x&&(y<250||y>550)){
			velocity.i = Math.abs(velocity.i); //bounce
			x = radius+bounds.x;
		}
		if(x>=bounds.x+bounds.width-radius&&(y<250||y>550)){    //off
			velocity.i = -Math.abs(velocity.i);//walls
			x = bounds.x+bounds.width-radius;
		}
		if(y<=radius+bounds.y){                 //of
			velocity.j = Math.abs(velocity.j); //bounds
			y = radius+bounds.y;
		}
		if(y>=bounds.y+bounds.height-radius){   //
			velocity.j = -Math.abs(velocity.j);//
			y=bounds.y+bounds.height-radius;
			
		}
		
		
	}
	public void setBounds(Rectangle rect){
		bounds = rect;
	}
	public void paint(Graphics g){
		g.setColor(color);
		g.fillOval((int)x-radius, (int)y-radius, 2*radius, 2*radius);
		g.setColor(Color.BLACK);
		int w = 16;
		g.fillOval((int)x-radius+w, (int)y-radius+w, 2*radius-2*w, 2*radius-2*w);
	}
	
	public boolean checkCollision(Player p){
		if(Math.pow(x-p.x, 2)+Math.pow(y-p.y, 2)<=Math.pow(p.radius+radius, 2)){
			Vector2d collisionDirection = new Vector2d(x-p.x, y-p.y);
			velocity = p.velocity.proj(collisionDirection).plus(velocity.proj(collisionDirection).times(-1)
					.plus(velocity.proj(new Vector2d(collisionDirection.j, -collisionDirection.i)))).times(0.9);
			return true;
		}
		return false;
	}
	public boolean checkCollision(Puck p){
		if(Math.pow(x-p.x, 2)+Math.pow(y-p.y, 2)<=Math.pow(p.radius+radius, 2)){
			Vector2d collisionDirection = new Vector2d(x-p.x, y-p.y);
			velocity = p.velocity.proj(collisionDirection).plus(velocity.proj(collisionDirection).times(-1).plus(velocity.proj(new Vector2d(collisionDirection.j, -collisionDirection.i)))).plus(collisionDirection.getUnitVector().times(2));
			return true;
		}
		return false;
	}
}
class Player{
	public int score = 0;
	public Vector2d velocity = new Vector2d(0, 0);
	public int radius;
	public int x = 0;
	public int y = 0;
	public Rectangle bounds;
	public Color color = Color.WHITE;
	public Player(int x, int y, int r, Color c){
		this.x = x;
		this.y = y;
		color = c;
		radius = r;
	}
	public void move(int x, int y){
	
		if(x<bounds.x+radius)
			x = bounds.x+radius;
		if(x>bounds.x+bounds.width-radius)
			x = bounds.x+bounds.width-radius;
		if(y<bounds.y+radius)
			y = bounds.y+radius;
		if(y>bounds.y+bounds.height-radius)
			y = bounds.y+bounds.height-radius;
		velocity = new Vector2d(x-this.x, y-this.y);
		this.x =x;
		this.y = y;
	}
	
	public void paint(Graphics g){
		g.setColor(color);
		g.fillOval((int)x-radius, (int)y-radius, 2*radius, 2*radius);
		g.setColor(Color.BLACK);
		int w = 16;
		g.fillOval((int)x-radius+w, (int)y-radius+w, 2*radius-2*w, 2*radius-2*w);
	}
	public void setBounds(Rectangle rect){
		bounds = rect;
	}
}
class AIPlayer extends Player{
	public Point dest = new Point(300, 480);
	int randx = 200;
	int randy = 480;
	public AIPlayer(int x, int y, int r, Color c) {
		super(x, y, r, c);
		t.start();
	}
	Timer t = new Timer(1000, new ActionListener(){
		public void actionPerformed(ActionEvent e){
			randx = (int)(Math.random()*300+50);
			randy = (int)(Math.random()*700+50);
		}
	});
	void move(Puck puck){
		int oldx = x;
		int oldy = y;
		
		//dest = (puck.velocity.getMagnitude()>6&&puck.x<720)? new Point(50, (int)puck.y) : new Point((int)puck.x, (int)puck.y);
		if(puck.x>720){
			if(Math.abs(dest.x-x)<20&&Math.abs(dest.y-y)<20){
				randx = (int)(Math.random()*600+50);
				randy = (int)(Math.random()*600+50);
			}
			dest = new Point(randx, randy);
			velocity = new Vector2d(dest.x-x, dest.y-y).getUnitVector().times(2);
			puck.color = Color.WHITE;
		}
		else if(puck.velocity.getMagnitude()>8){
			if(Math.abs(dest.x-x)<20)
				randx = (int)(Math.random()*600+50);
			dest = new Point(randx, (int)puck.y);
			velocity = new Vector2d(dest.x-x, dest.y-y).getUnitVector().times(10);
			puck.color = Color.WHITE;
		}
		else {
			dest = new Point((int)puck.x, (int)puck.y);
			velocity = new Vector2d(dest.x-x, dest.y-y).getUnitVector().times(30);
			puck.color = Color.WHITE;
		}
		velocity.i*=3;
		velocity.j*=3;
		x+=velocity.i;
		y+=velocity.j;
		
		
		if(x<bounds.x+radius){
			x = bounds.x+radius;
			velocity.i = 0;
		}if(x>bounds.x+bounds.width-radius){
			x = bounds.x+bounds.width-radius;
			velocity.i = 0;
		}if(y<bounds.y+radius){
			y = bounds.y+radius;
			velocity.j = 0;
		}if(y>bounds.y+bounds.height-radius){
			y = bounds.y+bounds.height-radius;
			velocity.j = 0;
		}
	}
	
}
