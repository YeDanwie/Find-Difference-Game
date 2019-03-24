import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PlayFrame extends JFrame implements ActionListener,MouseListener{
	private MyPanel bg_panel;
	private MyButton start_button,select_button,rank_button,diy_button,exit_button;
	private JPanel button_panel;
	
	//游戏窗体的构造
	PlayFrame()
	{
		super("大家来找茬");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setSize(1024, 680);
		super.setLocationRelativeTo(null);
		super.addMouseListener(this);
		super.getContentPane().setLayout(new BorderLayout());
	}
	
	//主菜单初始化
	public void initCover()
	{
		bg_panel=new MyPanel();
		
		button_panel=new JPanel(new FlowLayout());
		button_panel.setBackground(new Color(211,211,211));
		
		super.getContentPane().add(bg_panel,BorderLayout.CENTER);
		super.getContentPane().add(button_panel,BorderLayout.SOUTH);
		
		start_button=new MyButton("挑战模式");
		start_button.addActionListener(this);
		
		select_button=new MyButton("选关模式");
		select_button.addActionListener(this);
		
		rank_button=new MyButton("排行榜");
		rank_button.addActionListener(this);
		
		diy_button=new MyButton("导入关卡");
		diy_button.addActionListener(this);
		
		exit_button=new MyButton("退出游戏");
		exit_button.addActionListener(this);
			
		
	
		button_panel.add(start_button);
		button_panel.add(select_button);
		button_panel.add(rank_button);
		button_panel.add(diy_button);
		button_panel.add(exit_button);
		
		super.setSize(1024, 670);
		super.setVisible(true);
		super.setResizable(false);
				
	}
	
	public class MyPanel extends JPanel
	{
		//绘制主菜单的封面
		@Override
		public void paint(Graphics g)
		{	
			Image bg=Toolkit.getDefaultToolkit().getImage("Images\\bg.jpg");
			g.drawImage(bg, 0, 0, this);
						
			Image pika=Toolkit.getDefaultToolkit().getImage("Images\\0.gif");
			g.drawImage(pika, 270, 160, this);
			
			Image title=Toolkit.getDefaultToolkit().getImage("Images\\title.gif");
			g.drawImage(title, 350, 115, this);	
		}
	}
	
	
	
	public void actionPerformed(ActionEvent ae) 
	{
		//挑战模式
		if(ae.getSource().equals(start_button))
		{
			Thread thread=new Thread(new Runnable(){
				public void run()
				{
					PlayMusic click=new PlayMusic("Music\\click.wav");
					click.play();
				}
			});
			thread.start();//开启一个线程播放点击音效
		
			//更新面板进入限时关卡面板
			this.getContentPane().removeAll();
			((JPanel)(this.getContentPane())).updateUI();
			this.getContentPane().add(new StartPanel(this));
			this.getContentPane().repaint();
		}							
	    
		//事件源是选择关卡按钮
		else if(ae.getSource().equals(select_button))
		{
			Thread thread=new Thread(new Runnable(){
				public void run()
				{
					PlayMusic click=new PlayMusic("Music\\click.wav");
					click.play();
				}
			});
			thread.start();//开启一个线程播放点击音效
			
			//更新面板进入选择关卡面板
			super.getContentPane().removeAll();
			((JPanel)(super.getContentPane())).updateUI();
			super.getContentPane().add(new SelectPanel(this));
			super.getContentPane().repaint();
		}
		//事件源是排行榜按钮
		else if(ae.getSource().equals(rank_button))
		{
			Thread thread=new Thread(new Runnable(){
				public void run()
				{
					PlayMusic click=new PlayMusic("Music\\click.wav");
					click.play();
				}
			});
			thread.start();//开启一个线程播放点击音效
					
			//更新面板进入选择关卡面板
			super.getContentPane().removeAll();
			((JPanel)(super.getContentPane())).updateUI();
			super.getContentPane().add(new RankPanel(this));
			super.getContentPane().repaint();
		}		
		//事件源是导入关卡按钮
		else if(ae.getSource().equals(diy_button))
		{
			Thread thread=new Thread(new Runnable(){
				public void run()
				{
					PlayMusic click=new PlayMusic("Music\\click.wav");
					click.play();
				}
			});
			thread.start();//开启一个线程播放点击音效
			
			DIYFrame DF = new DIYFrame();//新建一个导入关卡的窗体		
			
		}
		//事件源是退出游戏按钮
		else if(ae.getSource().equals(exit_button))
		{
			this.dispose();//销毁窗体
		}
		
	}

	//在主菜单上点击时播放点击音效
	public void mouseClicked(MouseEvent arg0) {
		Thread thread=new Thread(new Runnable(){
			public void run()
			{
				PlayMusic click=new PlayMusic("Music\\click.wav");
				click.play();
			}
		});
		thread.start();
	}

	//改变鼠标外形为红色箭头
	public void mouseEntered(MouseEvent arg0) 
	{
		Image mouse_image=Toolkit.getDefaultToolkit().getImage("Images\\red_mouse.png");
		Image mouse=mouse_image.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		Cursor cursor=Toolkit.getDefaultToolkit().createCustomCursor(mouse, new java.awt.Point(0,0), "mouse");
		this.setCursor(cursor);
	}

	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}
}

//自定义按钮样式
class MyButton extends JButton
{
	MyButton(String text)
	{
		super.setBackground(new Color(105,105,105));
		super.setForeground(new Color(144,238,144));
		super.setFont(new Font("黑体",0,20));
		super.setText(text);
		super.setFocusPainted(false);
	}
}
