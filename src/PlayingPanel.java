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
	private MyButton home_button;
	private JProgressBar pbar;
	private Thread countdown;
	
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
		this.setBackground(new Color(167,133,95));
		
		JPanel center_panel=new JPanel();
		center_panel.setLayout(new GridLayout(1,2));
		this.add(center_panel,BorderLayout.CENTER);

		south_panel=new JPanel(new GridLayout(1,3));
		south_panel.setBorder(new EmptyBorder(20,0,20,0));
		south_panel.setBackground(new Color(167,133,95));
		
		cl_panel=new CenterPanel(name1,pointXY); cl_panel.addMouseListener(this);
		cr_panel=new CenterPanel(name2,pointXY); cr_panel.addMouseListener(this);
		center_panel.add(cl_panel);center_panel.add(cr_panel);
		
		JTextArea tips=new JTextArea();
		tips.setEditable(false);
		tips.setFont(new Font("幼圆",0,20));
		tips.append("-图片共有五处不同\n-擦亮双眼找出它们\n-限时30秒  祝好运");
		tips.setOpaque(false);
		tips.setBorder(new EmptyBorder(0,10,0,10));
		south_panel.add(tips);

		pbar=new JProgressBar(0,30);
		pbar.setBackground(new Color(167,133,95));
		pbar.setBorder(new EmptyBorder(5,0,5,0));
		pbar.setValue(30);
		pbar.setForeground(Color.RED);
		south_panel.add(pbar);
		
		countdown=new Thread(new Runnable(){
			
			public void run()
			{
				int count=30;
				PlayFrame play_frame=PF;
				while(count>=0)
				{
					try{Thread.sleep(1000);}
					catch(Exception e){}
					pbar.setValue(count--);
					String str=new String().format("时间剩余 %d s", pbar.getValue());
					pbar.setString(str);
					pbar.setStringPainted(true);
					
				}
				
				//超时后显示对话框，然后回到主菜单
				JOptionPane.showMessageDialog(play_frame, "时间到咯。想一想，不充钱你会变得更强么？","超时",JOptionPane.PLAIN_MESSAGE);
				play_frame.getContentPane().removeAll();
				((JPanel)(play_frame.getContentPane())).updateUI();
				play_frame.getContentPane().repaint();
				play_frame.initCover();
			}
		});
		countdown.start();//开始倒数线程
		
		
		home_button=new MyButton("主菜单");
		home_button.addActionListener(this);
		JPanel button_panel=new JPanel();
		button_panel.setOpaque(false);
		button_panel.add(home_button);
		south_panel.add(button_panel);
		
		this.add(south_panel,BorderLayout.SOUTH);

		ImageIcon icon=new ImageIcon("Images\\title.gif");
		JLabel label=new JLabel(icon);
		label.setBorder(new EmptyBorder(30,0,30,0));
		this.add(label,BorderLayout.NORTH);
		
		frame.setSize(1024,640);
	}
	
	public void actionPerformed(ActionEvent ae) {
		MusicPlayer mp = new MusicPlayer("Music\\click.wav");//开启一个线程播放点击音效
		mp.start(false);
		
		//如果游戏中途点击返回主菜单按钮，则停止倒数线程
		countdown.stop();
		this.frame.getContentPane().removeAll();
		((JPanel)(this.frame.getContentPane())).updateUI();
		this.frame.getContentPane().repaint();
		this.frame.initCover();
	}

	public void mouseClicked(MouseEvent me) {
		
		MusicPlayer mp = new MusicPlayer("Music\\click.wav");//开启一个线程播放点击音效
		mp.start(false);
		
		//传入点击时的坐标并重新绘制面板
		cl_panel.userXY[0]=(int)(me.getX());cl_panel.userXY[1]=(int)(me.getY());
		cr_panel.userXY[0]=(int)(me.getX());cr_panel.userXY[1]=(int)(me.getY());
		
		cl_panel.judge();
		cr_panel.judge();
		cl_panel.repaint();
		cr_panel.repaint();
		
		//五处不同全找出来之后，显示通关对话框，然后返回主菜单
		if(cl_panel.user_correct.size()==10)
		{
			//终止倒数线程，显示对话框通过并回主菜单
			countdown.stop();
			JOptionPane.showMessageDialog(frame, "一看就是老江湖了，不同处居然全都找了出来","通关",JOptionPane.PLAIN_MESSAGE,new ImageIcon("look.png"));
			
			this.frame.getContentPane().removeAll();
			((JPanel)(this.frame.getContentPane())).updateUI();
			
			this.frame.getContentPane().repaint();
			this.frame.initCover();
		}
		
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
	
	//构造方法，对变量进行赋值
	CenterPanel(String name,double[] XY)
	{
		this.name=name;
		Image image;
		image=Toolkit.getDefaultToolkit().getImage("D:\\DIYdata\\"+name);
		
		File file=new File("D:\\DIYdata\\"+name);
		if(!file.exists()) image=Toolkit.getDefaultToolkit().getImage("DefaultData\\"+name);
		imageCopy=image.getScaledInstance(512, 360, Image.SCALE_DEFAULT);
		this.XY=XY;
		userXY=new double[2]; userXY[0]=-256; userXY[1]=-256;
		user_correct=new ArrayList<Double>();
		
	}
	
	public void judge() {
		Point p0=new Point((int)userXY[0],(int)userXY[1]);//玩家点击出的坐标点
		
		//判断是否正确
		boolean isCorrect=false;
		int i;
		for(i=0;i<XY.length;i+=2)
		{
			Point p=new Point((int)XY[i],(int)XY[i+1]);
			if(p.distance(p0)<=13) 
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
			if(p.distance(p0)<=13) 
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
		}
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
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(5));
			g2d.setColor(Color.RED);

			//绘制玩家已点击的正确之处（用画圆圈表示）
			for(int j=0;j<user_correct.size();j+=2)
				g2d.drawArc((int)(double)(user_correct.get(j)-12), (int)(double)(user_correct.get(j+1)-12), 30, 30, 0, 360);
			
			//绘制两张图片的分割线
			g2d.setStroke(new BasicStroke(5)); 
			g2d.setColor(Color.WHITE);
			g2d.drawLine(this.getSize().width, 0, this.getSize().width, this.getSize().height);

			g2d.dispose();
		}
		
	}
}
