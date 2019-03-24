import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class PlayingPanel extends JPanel implements ActionListener,MouseListener{
	private PlayFrame frame;//游戏面板在这个窗体上显示
	private final PlayFrame PF;//在倒计时线程里使用 
	private String name1,name2;
	double[] pointXY;//图片不同处的坐标，每两个值为一个坐标
	private JPanel south_panel;
	CenterPanel cl_panel,cr_panel;
	private MyButton home_button,show_button;
	private JProgressBar pbar;
	private Thread countdown;
	JTextArea score_print;
	
	//游戏面板的初始化
	PlayingPanel(PlayFrame frame,String name1,String name2,double[] pointXY)
	{
		PF=frame;
		this.frame=frame;
		this.name1=name1;
		this.name2=name2;
		this.pointXY=pointXY;
		
		this.addMouseListener(this);
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(225,255,255));
		
		JPanel center_panel=new JPanel();
		center_panel.setLayout(new GridLayout(1,2));
		center_panel.setBackground(new Color(225,255,255));
		this.add(center_panel,BorderLayout.CENTER);

		south_panel=new JPanel(new GridLayout(1,4));
		south_panel.setBorder(new EmptyBorder(20,0,20,0));
		south_panel.setBackground(new Color(225,255,255));
		
		cl_panel=new CenterPanel(name1,pointXY); cl_panel.addMouseListener(this);
		cr_panel=new CenterPanel(name2,pointXY); cr_panel.addMouseListener(this);
		center_panel.add(cl_panel);center_panel.add(cr_panel);
		
		JTextArea tips=new JTextArea();
		tips.setEditable(false);
		tips.setFont(new Font("楷体",0,22));
		tips.append("-图片共有五处不同\n-限时50秒");
		tips.setOpaque(false); //透明
		tips.setBorder(new EmptyBorder(12,10,12,10));
		south_panel.add(tips);

		pbar=new JProgressBar(0,50);
		pbar.setBackground(new Color(225,255,255));
		pbar.setBorder(new EmptyBorder(20,0,20,0));
		pbar.setValue(50);
		pbar.setForeground(new Color(255,105,180));
		south_panel.add(pbar);
		
		countdown=new Thread(new Runnable(){
			
			public void run()
			{
				int count=50;
				PlayFrame play_frame=PF;
				while(count>=0 && cl_panel.score<5)  //计时结束 和 找出5处后 结束
				{		
					try{Thread.sleep(1000);}
					catch(Exception e){}
					pbar.setValue(count--);
					String str=new String().format("时间剩余 %d s", pbar.getValue());
					pbar.setString(str);
					pbar.setStringPainted(true);
					String s=new String().format("%d",cl_panel.score);//输出分数
					score_print.setText(s);					
				}
				
				//结束后显示对话框，然后回到主菜单
				String s=new String().format("得分：  %d ", cl_panel.score);
				JOptionPane.showMessageDialog(play_frame, s,"结束",JOptionPane.PLAIN_MESSAGE);
				play_frame.getContentPane().removeAll();
				((JPanel)(play_frame.getContentPane())).updateUI();
				play_frame.getContentPane().repaint();
				play_frame.initCover();
			}
		});
		countdown.start();//开始倒数线程
     	
		home_button=new MyButton("主菜单");
		home_button.addActionListener(this);

		show_button=new MyButton("显示所有答案");
		show_button.addActionListener(this);
		
		JPanel button_panel=new JPanel();
		button_panel.setOpaque(false);
		button_panel.add(home_button);
		button_panel.add(show_button);
		
		south_panel.add(button_panel);
		
		
		this.add(south_panel,BorderLayout.SOUTH);
	
		score_print=new JTextArea();
		score_print.setFont(new Font("黑体",0,60));
		score_print.setForeground(new Color(255,160,122)); 
		score_print.setOpaque(false); //透明
		score_print.setEditable(false);
		score_print.setBorder(new EmptyBorder(30,495,35,495));
		score_print.setText("0");
		this.add(score_print,BorderLayout.NORTH);
								
		frame.setSize(1024,640);
		
		PF.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent we)
			{
				countdown.stop();
			}
		});
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(home_button))
		{
		Thread thread=new Thread(new Runnable(){
			public void run()
			{
				PlayMusic click=new PlayMusic("Music\\click.wav");
				click.play();
			}
		});
		thread.start();//开启线程播放点击音效
		
		//如果游戏中途点击返回主菜单按钮，则停止倒数线程
		countdown.stop();
		this.frame.getContentPane().removeAll();
		((JPanel)(this.frame.getContentPane())).updateUI();
		this.frame.getContentPane().repaint();
		this.frame.initCover();
		}
		else //显示所有不同处
		{
			Thread thread=new Thread(new Runnable(){
				public void run()
				{
					PlayMusic click=new PlayMusic("Music\\click.wav");
					click.play();
				}
			});
			thread.start();//开启线程播放点击音效
			
			countdown.stop();
			int i;
			for(i=0;i<cl_panel.XY.length;i+=2)
			{
					cl_panel.user_correct.add(cl_panel.XY[i]);
					cr_panel.user_correct.add(cl_panel.XY[i]);
					cl_panel.user_correct.add(cl_panel.XY[i+1]);
					cr_panel.user_correct.add(cl_panel.XY[i+1]);
			}
			cl_panel.repaint();
			cr_panel.repaint();
			
			show_button.setEnabled(false);
		}
		
	}

	public void mouseClicked(MouseEvent me) {
		
		Thread thread1=new Thread(new Runnable(){
			public void run()
			{
				PlayMusic click=new PlayMusic("Music\\click.wav");
				click.play();
			}
		});
		thread1.start();//点击时播放点击点击音效
						
		//传入点击时的坐标并重新绘制面板
		cl_panel.userXY[0]=(int)(me.getX());cl_panel.userXY[1]=(int)(me.getY());
		cr_panel.userXY[0]=(int)(me.getX());cr_panel.userXY[1]=(int)(me.getY());
		cl_panel.repaint(); cr_panel.repaint();
			
	}

	//改变鼠标外形为红色箭头
	public void mouseEntered(MouseEvent arg0) {
		Image mouse_image=Toolkit.getDefaultToolkit().getImage("Images\\red_mouse.png");
		Image mouse=mouse_image.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		Cursor cursor=Toolkit.getDefaultToolkit().createCustomCursor(mouse, new java.awt.Point(0,0), "mouse");
		this.setCursor(cursor);
	}

	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}
}

//自定义面板
class CenterPanel extends JPanel
{
	String name;
	Image imageCopy;
	double[] XY,userXY;
	ArrayList<Double> user_correct;
	int score; //得分
	
	//构造方法，对变量进行赋值
	CenterPanel(String name,double[] XY)
	{
		this.name=name;
		this.XY=XY;
		Image image;
		
		File file=new File("D:\\DIYdata\\"+name);
		if(!file.exists()) image=Toolkit.getDefaultToolkit().getImage("DefaultData\\"+name);
		else image=Toolkit.getDefaultToolkit().getImage("D:\\DIYdata\\"+name);
		imageCopy=image.getScaledInstance(512, 360, Image.SCALE_DEFAULT);
		
		userXY=new double[2]; userXY[0]=-256; userXY[1]=-256;
		user_correct=new ArrayList<Double>();
		score=0;
	}
	
	//绘制面板
	@Override
	public void paint(Graphics g)
	{
		if(name!=null)
		{
			Graphics2D g2d=(Graphics2D) g.create();

			g2d.drawImage(imageCopy,0, 0, this);//绘制图片
			
			//画笔抗锯齿及设置画笔的粗细、颜色
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
			g2d.setStroke(new BasicStroke(5));
			g2d.setColor(Color.RED);
			
			
			Point p0=new Point((int)userXY[0],(int)userXY[1]);//玩家点击出的坐标点
			
			//判断是否正确
			boolean isCorrect=false;
			int i;
			for(i=0;i<XY.length;i+=2)
			{
				Point p=new Point((int)XY[i],(int)XY[i+1]);
				if(p.distance(p0)<=30) 
				{
					isCorrect=true;
					break;
				}
			}
			
			//判断是否已存在（之前已经点击正确了）
			boolean exist=false;
			for(int j=0;j<user_correct.size();j+=2)
			{
				Point p=new Point((int)(double)(user_correct.get(j)),(int)(double)(user_correct.get(j+1)));
				if(p.distance(p0)<=30) 
				{
					exist=true;
					break;
				}
			}
			
			//如果玩家点击的是 新的不同处，则将点加入到user_correct
			if(isCorrect&&exist==false) 
			{
				user_correct.add(XY[i]);
				user_correct.add(XY[i+1]);
				score++;
			}

			//绘制玩家已点击的正确之处（用画圆圈表示）
			for(int j=0;j<user_correct.size();j+=2)
				g2d.drawArc((int)(double)(user_correct.get(j)-20), (int)(double)(user_correct.get(j+1)-20), 40, 40, 0, 360);
			
			//绘制两张图片的分割线
			g2d.setStroke(new BasicStroke(2)); 
			g2d.setColor(Color.WHITE);
			g2d.drawLine(this.getSize().width, 0, this.getSize().width, this.getSize().height);
			g2d.drawLine(0, 0, 0, this.getSize().height);
			g2d.dispose();
		}
		
	}
}
