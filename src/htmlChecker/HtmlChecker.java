package htmlChecker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HtmlChecker extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	JPanel panel;
	JButton button1, button2;
	JTextField text1, text2;
	JTextField loginText;
	JPasswordField passText;
	JLabel label1, label2;
	JLabel loginMessage, loginLabel, passLabel;
	int height=300;
	int width=300;
	int textWidth=20;
	long startTime=System.currentTimeMillis();
	long prevElapsedTime=-1;
	String prevRead;
	java.util.Timer timer=null;
	java.util.TimerTask task=null;
	public HtmlChecker()
	{
		super("HtmlChecker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel=new JPanel();
		button1=new JButton("Sprawdzanie czasowe");
		button2=new JButton("Sprawdzanie zawierania");
		label1=new JLabel("Podaj adres URL");
		label2=new JLabel("Podaj okres/warunek sprawdzania");
		text1=new JTextField(textWidth);
		text2=new JTextField(textWidth);
		loginText=new JTextField(textWidth);
		passText=new JPasswordField(textWidth);
		loginMessage=new JLabel("Wpisz login i hasło (jeśli wymagane na stronie)");
		loginLabel=new JLabel("login:");
		passLabel=new JLabel("hasło:");
		setSize(width, height);
		panel.add(label1);
		panel.add(text1);
		panel.add(label2);
		panel.add(text2);
		panel.add(button1);
		panel.add(button2);
		panel.add(loginMessage);
		panel.add(loginLabel);
		panel.add(loginText);
		panel.add(passLabel);
		panel.add(passText);
		button1.addActionListener(this);
		button2.addActionListener(this);
		text1.addActionListener(this);
		text2.addActionListener(this);
		setContentPane(panel);
		setVisible(true);
	}
	
	/*public HtmlChecker(String message)
	{
		super("HtmlChecker");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		panel=new JPanel();
		label1=new JLabel(message);
		setSize(width, height-100);
		panel.add(label1);
		setContentPane(panel);
		setVisible(true);
	} */
	
	public void actionPerformed(ActionEvent event) 
	{
		try
		{
			Object source=event.getSource();
			String adress=text1.getText();
			if (loginText.getText().length()!=0 && passText.getPassword().length!=0)
            {
                String username=loginText.getText();
                char[] password=passText.getPassword();
                Authenticator.setDefault(new MyAuthenticator(username, password));
            }
			URL url=new URL(adress);
			if (source==button1)
			{
			    if (timer!=null && task!=null)
				{
					task.cancel();
					timer.cancel();
				}
				int time=Integer.parseInt(text2.getText());
				timer = new java.util.Timer();
				task = new java.util.TimerTask() 
				{
					public void run() 
					{
						checkUrl(url, time);
					}
				};
				timer.schedule(task, java.util.Calendar.getInstance().getTime(), 100);
			}
			else if (source==button2 || source==text1 || source==text2)
			{
				if (timer!=null && task!=null)
				{
					task.cancel();
					timer.cancel();
				}
				String condition=text2.getText();
				checkUrl(url, condition);
			}
		}
		catch (MalformedURLException e) 
		{ 
			JOptionPane.showMessageDialog(null, "Zły adres URL!", "HtmlChecker", 0);
		} 
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null, "Zły format danych - nie liczba całkowita!", "HtmlChecker", 0);
		} 
	}
	
	public void checkUrl(URL url, Object tico)
	{
		try
		{
			URLConnection urlConnection=url.openConnection();
			urlConnection.connect();
			String actRead;
			long breakTime=-1;
			long actElapsedTime;
			if (tico.getClass().equals(Integer.class))
				breakTime=Integer.parseInt(tico.toString());
			if (breakTime==-1)
			{
				prevRead=readUrl(urlConnection);
				if (prevRead.contains(tico.toString()))
					JOptionPane.showMessageDialog(null, "Podana fraza jest zawarta w kodzie strony!", "HtmlChecker", 1);
				else
					JOptionPane.showMessageDialog(null, "Podana fraza nie jest zawarta w kodzie strony!", "HtmlChecker", 1);
				return;
			}
			URLConnection urlConnection1=url.openConnection();
			urlConnection1.connect();
			actElapsedTime=(System.currentTimeMillis()-startTime)/1000;
			if (prevElapsedTime==-1)
			{
				prevRead=readUrl(urlConnection);
				prevElapsedTime=actElapsedTime;
				//actElapsedTime=(System.currentTimeMillis()-startTime)/1000;
			}
			else if (actElapsedTime-prevElapsedTime>=breakTime)
			{
				actRead=readUrl(urlConnection1);
				if (actRead.equals(prevRead))
				{
					prevRead=actRead;
					prevElapsedTime=actElapsedTime;
					JOptionPane.showMessageDialog(null, "Nie nastąpiła zmiana w kodzie strony!", "HtmlChecker", 1);
				}
				else
				{
                    if (timer!=null && task!=null)
                    {
                        task.cancel();
                        timer.cancel();
                    }
                    prevElapsedTime=-1;
				    JOptionPane.showMessageDialog(null, "Nastąpiła zmiana w kodzie strony!", "HtmlChecker", 1);
				}
			}
		}
		catch (IOException e) 
		{   
			JOptionPane.showMessageDialog(null, "Nieudane połączenie", "HtmlChecker", 0);
		} 
		return;
	}
	
	public String readUrl(URLConnection urlConnection)
	{
		try
		{
			String adder="", readText="";
			BufferedReader reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			while ((adder=reader.readLine())!=null)
				readText+=adder;
			return readText;
		}
		catch (IOException e) 
		{   
			JOptionPane.showMessageDialog(null, "Błąd I/O", "HtmlChecker", 0);
			return null;
		} 
	}

	public static void main(String args[])
	{
		new HtmlChecker();
	}
}
