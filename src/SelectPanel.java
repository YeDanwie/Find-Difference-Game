import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SelectPanel extends JPanel implements ActionListener{
	private PlayFrame frame;
	private MyButton pre_button,enter_button,next_button,goback_button;
	private MyPanel bg_panel;
	private JPanel button_panel;
	ArrayList<String> name1,name2,nameA,nameB;
	ArrayList<double[]> XY,DXY;
	
	//选择关卡的面板初始化
	SelectPanel(PlayFrame frame)
	{
		super(new BorderLayout());
		this.frame=frame;
		
		//获取玩家自己导入的关卡和自带的默认关卡
		DIYdata diy_data=new DIYdata();
		name1=new ArrayList<String>();
		name2=new ArrayList<String>();
		XY=new ArrayList<double[]>();
		diy_data.get(1,name1, name2, XY); //玩家导入关卡
		nameA=new ArrayList<String>();
		nameB=new ArrayList<String>();
		DXY=new ArrayList<double[]>();
		diy_data.get(0, nameA, nameB, DXY); //自带关卡
		
		bg_panel=new MyPanel(name1,nameA);
		super.add(bg_panel,BorderLayout.CENTER);
		
		pre_button=new MyButton("上一关"); pre_button.addActionListener(this);
		enter_button=new MyButton("进入游戏"); enter_button.addActionListener(this);
		next_button=new MyButton("下一关"); next_button.addActionListener(this);
		goback_button=new MyButton("返回"); goback_button.addActionListener(this);
		
		button_panel=new JPanel();
		button_panel.setBackground(new Color(211,211,211));
		button_panel.add(pre_button);
		button_panel.add(enter_button);
		button_panel.add(next_button);
		button_panel.add(goback_button);
		
		super.add(button_panel,BorderLayout.SOUTH);
	}
	
	public void actionPerformed(ActionEvent ae) {
		//事件源是 上一关 按钮
		if(ae.getSource().equals(pre_button))
		{
			Thread thread=new Thread(new Runnable(){
				public void run()
				{
					PlayMusic click=new PlayMusic("Music\\click.wav");
					click.play();
				}
			});
			thread.start();//开启一个线程播放点击音效

			bg_panel.order--;//关卡序数-1
			//令关卡序数在合法范围内
			if(bg_panel.order<0)
			{
				bg_panel.order=(name1.size()+nameA.size())-1;
				bg_panel.repaint();
			}
			else
			{
				bg_panel.repaint();
			}
		}
		//事件源是开始游戏按钮
		else if(ae.getSource().equals(enter_button))
		{
			Thread thread=new Thread(new Runnable(){
				public void run()
				{
					PlayMusic click=new PlayMusic("Music\\click.wav");
					click.play();
				}
			});
			thread.start();//开启一个线程播放点击音效
			
			//更新选择关卡的面板，进入到玩家所选的关卡开始游戏
			frame.getContentPane().removeAll();
			((JPanel)(frame.getContentPane())).updateUI();
			int index=bg_panel.order;
			if(index<=(name1.size()-1))
				frame.getContentPane().add(new PlayingPanel(frame,name1.get(index),name2.get(index),XY.get(index)));
			else 
			{
				index-=(name1.size());
				frame.getContentPane().add(new PlayingPanel(frame,nameA.get(index),nameB.get(index),DXY.get(index)));
			}
			frame.getContentPane().repaint();
		}
		//事件源是下一关按钮
		else if(ae.getSource().equals(next_button))
		{
			Thread thread=new Thread(new Runnable(){
				public void run()
				{
					PlayMusic click=new PlayMusic("Music\\click.wav");
					click.play();
				}
			});
			thread.start();//播放点击音效
			
			//关卡序数+1
			bg_panel.order++;
			//令关卡序数在何合法范围内
			if(bg_panel.order>(name1.size()+nameA.size()-1))
			{
				bg_panel.order=0;
				bg_panel.repaint();
			}
			else
			{
				bg_panel.repaint();
			}
		}
		//事件源是返回主菜单按钮
		else if(ae.getSource().equals(goback_button))
		{
			Thread thread=new Thread(new Runnable(){
				public void run()
				{
					PlayMusic click=new PlayMusic("Music\\click.wav");
					click.play();
				}
			});
			thread.start();//播放点击音效
			
			//更新面板，进入到主菜单面板
			this.frame.getContentPane().removeAll();
			((JPanel)(this.frame.getContentPane())).updateUI();
			this.frame.getContentPane().repaint();
			this.frame.initCover();
		}
	}
}

//自定义面板样式
class MyPanel extends JPanel{
	private ArrayList<String> diy_image,default_image;
	public int order; //标志哪张图
	
	//构造方法，为变量赋值
	MyPanel(ArrayList<String> diy_image,ArrayList<String> default_image)
	{
		this.diy_image=diy_image;
		this.default_image=default_image;
		order=0;
	}
	
	@Override
	public void paint(Graphics g)
	{
		Image bg=Toolkit.getDefaultToolkit().getImage("Images\\bg.jpg");
		g.drawImage(bg, 0, 0, this);
		
		//绘制玩家导入的图片
		if(diy_image.size()!=0&&order<diy_image.size())
		{	
			Image pic=Toolkit.getDefaultToolkit().getImage("D:\\DIYdata\\"+diy_image.get(order));
			
			//居中绘制图片
			g.drawImage(pic, (this.getWidth())/2-pic.getWidth(this)/2, (this.getHeight())/2-pic.getHeight(this)/2, this);
		}
		//绘制自带图片
		else 
		{
			Image pic=Toolkit.getDefaultToolkit().getImage("DefaultData\\"+default_image.get(order-diy_image.size()));
			
			//居中绘制图片
			g.drawImage(pic, (this.getWidth())/2-pic.getWidth(this)/2, (this.getHeight())/2-pic.getHeight(this)/2, this);
		}
		
	}
}
