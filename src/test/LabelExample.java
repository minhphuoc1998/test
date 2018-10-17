package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LabelExample extends Frame implements ActionListener
{

	JTextField tf;
	JLabel l;
	JButton b;
	
	public LabelExample()
	{
		tf = new JTextField();
		tf.setBounds(50, 50, 150, 20);
		l = new JLabel();
		l.setBounds(50, 100, 250, 20);
		b = new JButton("Find IP");
		b.setBounds(50, 150, 95, 30);
		b.addActionListener(this);
		add(b);
		add(tf);
		add(l);
		setSize(400, 400);
		setLayout(null);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try
		{
			String host = tf.getText();
			String ip = java.net.InetAddress.getByName(host).getHostAddress();
			l.setText("IP of " + host + " is: " + ip);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		new LabelExample();
	}
	
}
