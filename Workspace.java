import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Workspace extends JFrame implements ActionListener
{
	JLabel topText = new JLabel();
	JPanel top = new JPanel();
	JPanel playArea = new JPanel();
	JPanel controls = new JPanel();
	JButton nxtLvl = new JButton();
	JButton restart = new JButton();
	JButton check = new JButton();
	BufferedReader input; // Citește frazele din fișier
	MovableElement[] movables = new MovableElement[50]; // Memorează elementele.
	String[] words = new String[50]; // Memorează cuvintele frazei după ce au fost amestecate.
	String[] corrOrder = new String[50]; // Memorează cuvintele frazei în ordinea corectă.
	int len=0, lvl; // Nr. de cuvinte din fraza curentă; nivelul curent.
	
	Workspace()
	{
		// Fereastra
		setTitle("Drag&Drop Quiz");
		setExtendedState(JFrame.MAXIMIZED_BOTH); // Lansează aplicația în full-screen.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.PINK);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		
		
		// Text
		topText.setForeground(Color.BLUE);
		topText.setFont(new Font("Comic sans", Font.BOLD, 30));
		topText.setHorizontalAlignment(JLabel.CENTER);
		topText.setOpaque(false);
		
		top.setPreferredSize(new Dimension(640,60));
		top.setBackground(Color.GRAY);
		top.add(topText);
		top.setVisible(true);
		top.setBorder(BorderFactory.createLineBorder(Color.BLACK,3));
		
		// Zona unde apar cuvintele
		playArea.setBackground(Color.PINK);
		playArea.setLayout(new FlowLayout());
		playArea.setOpaque(true);
		
		// Butoanele
		nxtLvl.setFocusable(false);
		nxtLvl.addActionListener(this);
		restart.setText("Restart");
		restart.setFocusable(false);
		restart.addActionListener(this);
		check.setText("Verifică");
		check.setFocusable(false);
		check.addActionListener(this);
		controls.setLayout(new GridLayout(1,3));
		controls.setPreferredSize(new Dimension(640,50));
		controls.add(nxtLvl);
		controls.add(restart);
		controls.add(check);
		
		add(top, BorderLayout.NORTH);
		add(controls, BorderLayout.SOUTH);
		add(playArea, BorderLayout.CENTER);
		setVisible(true);
		reset();// Inițializează variabile & butoane pentru a începe.
		if(input!=null)
			JOptionPane.showMessageDialog(null,"Programul funcționează exclusiv în modul full-screen","Nu ieși din full-screen",JOptionPane.WARNING_MESSAGE);
	}
	
	// Determină acțiunea butoanelor
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==nxtLvl)
		{
			try
			{
				String line = input.readLine();
				if(line!=null) // Dacă mai sunt fraze nerezolvate, trece la următoarea.
					level(line);
				else  // Dacă nu mai sunt fraze în fișier, determină finalul jocului.
				{
					input.close();
					nxtLvl.setEnabled(false);
					JOptionPane.showMessageDialog(null,"Ai rezolvat corect toate nivelele!","Ai câștigat!",JOptionPane.PLAIN_MESSAGE);
				}
			}
			catch (IOException i) {}
		}
		if(e.getSource()==restart)
			reset();
		if(e.getSource()==check)
		{
			if(Connected())
			{
				if(Correct())
				{
					for(int i=0; i<len; i++) // Colorează elementele cu verde și
					{						 // și le dezactivează mobilitatea.
						movables[i].setBackground(Color.GREEN);
						movables[i].enabled=false;
					}
					JOptionPane.showMessageDialog(null,"Fraza este corectă!","Corect",JOptionPane.INFORMATION_MESSAGE);
					nxtLvl.setEnabled(true);
					check.setEnabled(false);
				}
				else
					JOptionPane.showMessageDialog(null,"Mai încearcă","Greșit",JOptionPane.WARNING_MESSAGE);
			}
			else
				JOptionPane.showMessageDialog(null,"Conectează mai întâi toate cuvintele într-o singură frază!","Cuvintele nu sunt conectate!",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	// Creează următorul nivel.
	public void level(String s)
	{
		check.setEnabled(true);
		lvl++;
		topText.setText("Nivelul " + lvl);
		nxtLvl.setText("Următorul nivel");
		nxtLvl.setEnabled(false); // Nu permite să sari peste un nivel.
		
		playArea.setVisible(false); // Adăugarea/Ștergerea de elemente nu funcționează dacă zona de joc e vizibilă.
		
		for(int i=0; i<len; i++) // Șterge elementele nivelului anterior, dacă există.
			playArea.remove(movables[i]); 
		
		words = s.split(" "); // Împarte fraza în cuvinte.
		corrOrder = words.clone(); // Memorează ordinea corectă.
		len = corrOrder.length; // Nr. de cuvinte
		List<String> toShuffle = Arrays.asList(words); // Conversie pentru a folosi .shuffle().
		Collections.shuffle(toShuffle); // Pune cuvintele într-o ordine aleatorie.
		toShuffle.toArray(words);
		
		for(int i=0; i<len; i++) // Adaugă elementele în zona de joc.
		{
			movables[i] = new MovableElement(words[i],i,movables,len);
			playArea.add(movables[i]);
		}
		playArea.setVisible(true);
	}
	
	// n cuvinte sunt toate conectate dacă există (n-1)*2 intersecții între elemente.
	public boolean Connected()
	{
		int nrConn=0;
		for(int i=0; i<len; i++)
			for(int j=0; j<len; j++)
				if(i!=j && movables[i].intersects(movables[j]))
					nrConn++;
		return nrConn==((len-1)*2)?true:false;
	}
	
	// Verifică ordinea corectă a cuvintelor.
	public boolean Correct()
	{
		int minX=10000, crrnt=-1, k=len;
		for(int i=0; i<len; i++) // Caută elementul cel mai din stânga, primul cuvânt al frazei.
			if(movables[i].absoluteX<minX)
			{
				minX=movables[i].absoluteX;
				crrnt=i;
			}
		while(k!=0) // Compară len elemente ordonate cu corespondentul lor în corrOrder.
		{
			if(!movables[crrnt].txt.getText().equals(corrOrder[len-k])) // Verifică nepotriviri.
				return false;
			for(int i=0; i<len; i++) // Caută elementul din dreapta lui crrnt.
				if(movables[crrnt].intersects(movables[i]) && movables[i].absoluteX>movables[crrnt].absoluteX)
				{
					crrnt=i;
					break;
				}
			k--;
		}
		return true;
	}
	
	// Pregătește variabilele & zona de joc pentru primul nivel.
	public void reset()
	{
		playArea.setVisible(false);
		for(int i=0; i<len; i++)
			playArea.remove(movables[i]);
		topText.setText("Ordonează cuvintele pentru a obține fraze");
		nxtLvl.setText("Începe");
		nxtLvl.setEnabled(true);
		check.setEnabled(false);
		lvl=0;
		if(input!=null)
			try { input.close(); }
			catch (IOException e) {}
		try // Redeschide fișierul cu fraze.
		{
			input = new BufferedReader(new FileReader("Phrases.txt"));
		}
		catch (FileNotFoundException e2)
		{
			JOptionPane.showMessageDialog(null,"Fișierul cu fraze nu a fost găsit.","Eroare",JOptionPane.WARNING_MESSAGE);
			dispose();
		}
		playArea.setVisible(true);
	}
}
