import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MovableElement extends JPanel
{
	MovableElement[] elems; // Elementele cu care se verifică coliziunea.
	JLabel txt = new JLabel(); // Cuvântul
	int index, arrLen; // Numărul elementului în elems[]; numărul de elemente în elems[]
	boolean enabled=true; // Dictează dacă elementul poate fi mutat sau nu.
	int width, height, dX, dY, absoluteX, absoluteY; // Coordonate & dimensiuni.
	
	MovableElement(String s, int nr, MovableElement[] others, int l)
	{
		index = nr;
		elems = others;
		arrLen = l;
		width = 24*s.length(); // Lățimea elementului, în funcție de textul conținut.
		height = 50;
		setPreferredSize(new Dimension(width,height));
		dX = 8*s.length(); // Distanțele între locația cursorului și poziția
		dY = 100;		   // elementului pe ecran.
		setBackground(Color.LIGHT_GRAY);
		txt.setText(s);
		txt.setFont(new Font("Arial",Font.ITALIC,30));
		txt.setBackground(Color.BLACK);
		txt.setForeground(Color.RED);
		setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
		add(txt);

		DragListener dL = new DragListener();
		this.addMouseMotionListener(dL);
		
	}
	
	// Verifică dacă elementul este tras cu cursorul.
	public class DragListener extends MouseMotionAdapter
	{
		public void mouseDragged(MouseEvent e)
		{
			if(enabled)
			{
				int x = e.getXOnScreen(), y = e.getYOnScreen(); // Coordonatele cursorului pe ecran.
				absoluteX = x-dX; absoluteY = y-dY; // Schimbă coordonatele elementului astfel încât
				setLocation(absoluteX,absoluteY);	// cursorul să apară în centrul elementului.
				for(int i=0; i<arrLen; i++)	// Verifică dacă elementul se intersectează cu alt element.
					if(i!=index && intersects(elems[i]))
						if(absoluteX < elems[i].absoluteX)
							snap(elems[index],elems[i],'s'); // Lipește elementul la stânga.
						else
							snap(elems[index],elems[i],'d'); // Lipește elementul la dreapta.
			}
		}
	}
	
	// Rectangle.intersects(Rectangle r), adaptat.
	public boolean intersects(MovableElement r)
	{
		int tw = this.width;
		int th = this.height;
		int rw = r.width;
		int rh = r.height;
		if(rw<=0 || rh<=0 || tw<= 0 || th<=0)
			return false;
		int tx = this.absoluteX;
		int ty = this.absoluteY;
		int rx = r.absoluteX;
		int ry = r.absoluteY;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		return ((rw<rx || rw>tx) &&
				(rh<ry || rh>ty) &&
				(tw<tx || tw>rx) &&
				(th<ty || th>ry));
	}
	
	// Lipește toMove la stânga sau la dreapta lui target.
	public void snap(MovableElement toMove, MovableElement target, char dir)
	{
		toMove.absoluteX=target.absoluteX+(dir=='s'?-toMove.width+1:target.width-1);
		toMove.absoluteY=target.absoluteY;
		toMove.setLocation(toMove.absoluteX,toMove.absoluteY);
	}
}
