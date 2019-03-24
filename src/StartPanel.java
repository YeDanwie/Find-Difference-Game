import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class StartPanel extends JPanel implements ActionListener,MouseListener{
	private PlayFrame frame;//游戏面板在这个窗体上显示
	private final PlayFrame PF;//在倒计时线程里使用 
	private String name1,name2;
	double[] pointXY;//图片不同处的坐标，每两个值为一个坐标
	private JPanel south_panel,center_panel,north_panel;
	CenterPanel cl_Panel,cr_Panel;
	private MyButton home_button,stop_continue;
	private JProgressBar pbar0,pbar;//pbar0大进度条
	JTextArea score_print; //输出成绩
	private Thread countdown;
	private Thread countdown0; //总线程
	ArrayList<String> name11,name22,nameA,nameB;//name11,name22自带；nameA,nameB导入
	ArrayList<double[]> OXY,DXY;//OXY自带关卡，DXY导入关卡
	
	CenterPanel cl_panel[]=new CenterPanel[8]; //各关卡面板
	CenterPanel cr_panel[]=new CenterPanel[8];
	
	int little_count; //小时间条长度
	int number; //当前关卡数
	int round;//游戏中总关卡数-1
	int correct_number; //已找到不同处的数目
	String[] rank= {"反应迟钝","熟能生巧","眼明手快","小菜一碟","三只眼","千里眼","神眼"};//等级
	ArrayList<String> ranking;//读取的排行
	File rank_list;//前5名排行文件
	int []score= {0,0,0,0,0};//排行榜分数
	boolean stop_flag;
	
	//游戏面板的初始化
	StartPanel(PlayFrame frame)
	{
		PF=frame;
		this.frame=frame;
				
		this.addMouseListener(this);
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(211,211,211));
		
		center_panel=new JPanel();
		center_panel.setLayout(new GridLayout(1,2));
		this.add(center_panel,BorderLayout.CENTER);

		south_panel=new JPanel(new GridLayout(1,3));
		south_panel.setBorder(new EmptyBorder(20,0,20,0));
		south_panel.setBackground(new Color(225,255,255));		
		
		north_panel=new JPanel(new GridLayout(2,1));
		north_panel.setBackground(new Color(225,255,255));
		
		number=0;
		correct_number=0;
		
		DIYdata diy_data=new DIYdata();
		name11=new ArrayList<String>();
		name22=new ArrayList<String>();
		OXY=new ArrayList<double[]>();
		diy_data.get(0,name11, name22, OXY);//读取自带关卡
		nameA=new ArrayList<String>();
		nameB=new ArrayList<String>();
		DXY=new ArrayList<double[]>();
		diy_data.get(1, nameA, nameB, DXY);
	
		int i,t,size,name11_size,nameA_size;
		name11_size=name11.size();
		nameA_size=nameA.size();
		size=name11_size+nameA_size;
		Random rand = new Random();
		boolean flag[]=new boolean[size];
		for(i=0;i<size;i++)
		{
			flag[i]=true;
		}
		
		for(i=0;i<size;i++)  //随机建立关卡面板数组，最多8关
		{
			while(true)
			{
				t=rand.nextInt(size);
				if(flag[t]==true)
				{
					flag[t]=false;
					break;
				}
			}
			if(t<name11_size)
			{
				name1=name11.get(t);
			    name2=name22.get(t);
			    pointXY=OXY.get(t);
			}
			else
			{
				t=t-name11_size;
				name1=nameA.get(t);
			    name2=nameB.get(t);
			    pointXY=DXY.get(t);
			}
		    cl_panel[i]=new CenterPanel(name1,pointXY); cl_panel[i].addMouseListener(this); 
		    cr_panel[i]=new CenterPanel(name2,pointXY); cr_panel[i].addMouseListener(this);
		    if(i==7) break; 
		}
		if(size>=8) round=i;  //加入的关卡数-1
		else round=i-1;
		
		center_panel.add(cl_panel[0]);center_panel.add(cr_panel[0]);
		
		JTextArea tips=new JTextArea();//左下角说明
		tips.setEditable(false);
		tips.setFont(new Font("楷体",0,20));
		tips.append("-图片共有五处不同\n-每关限时30秒\n-一共150秒--最多8关");
		tips.setOpaque(false);
		tips.setBorder(new EmptyBorder(0,10,0,10));
		south_panel.add(tips);

		score_print=new JTextArea(); //最上方分数输出
		score_print.setEditable(false);
		score_print.setFont(new Font("黑体",0,55));
		score_print.setForeground(new Color(255,160,122)); 
		score_print.setOpaque(false); //透明
		score_print.setBorder(new EmptyBorder(10,160,0,0));
		score_print.setText("关数：1            分数：0");
		north_panel.add(score_print);
			
		pbar0=new JProgressBar(0,150);//总时间条
		pbar0.setBackground(new Color(225,255,255));
		pbar0.setBorder(new EmptyBorder(20,0,30,0));
		pbar0.setValue(150);
		pbar0.setForeground(new Color(127,255,0));
		north_panel.add(pbar0);
		
		this.add(north_panel,BorderLayout.NORTH);
		
		pbar=new JProgressBar(0,30);//小时间条
		pbar.setBackground(new Color(225,255,255));
		pbar.setBorder(new EmptyBorder(20,0,20,0));
		pbar.setValue(30);
		pbar.setForeground(new Color(255,105,180));
		south_panel.add(pbar);
		
		ranking=new ArrayList<String>();		
		rank_list=new File("DefaultData\\\\rank_list.txt");  //读取排行榜分数
		try {
			BufferedReader br=new BufferedReader(new FileReader(rank_list));
			String line;
			i=0;
			while((line=br.readLine())!=null)
			{
				String[] data=line.split(" ");
				ranking.add(line);	
				
				score[i++]=Integer.parseInt(data[0]);
			}
			br.close();
		} catch (Exception e) {e.printStackTrace();}
		
		
		countdown0=new Thread(new Runnable(){		//总时间
			public void run()
			{
				int count=150;
				int RANK;
				PlayFrame play_frame=PF;
				while(count>=0 && number<=round)  //计时 或 关卡 结束
				{
					try{Thread.sleep(1000);}
					catch(Exception e){}
					pbar0.setValue(count--);
					pbar0.repaint();
				}			
				
				countdown.stop();
				//总时间完后显示对话框，然后回到主菜单
				if(number<=round)  correct_number+=cl_panel[number].score;
				
				if(correct_number<10) RANK=0;
				else if(correct_number<17) RANK=1;
				else if(correct_number<24) RANK=2;
				else if(correct_number<30) RANK=3;
				else if(correct_number<35) RANK=4;
				else if(correct_number<40) RANK=5;
				else RANK=6;
				String str=new String().format("得分： %d                  等级："+rank[RANK], correct_number);	
				
				if(correct_number>score[4]) //破纪录，计入排行榜
				{
					int flag=4;//取代排名的位置
					int k=4;
					while(--k>=0)
					{
						if(correct_number>score[k]) flag--;
						else break;
					}
					
					String name= JOptionPane.showInputDialog(null, str+"         请输入名字：","破纪录啦！", JOptionPane.PLAIN_MESSAGE);
					if(name==null||name.isEmpty()) name="无名";
					
					try
					{
			        FileWriter fw=new FileWriter(rank_list,false);
			        for(k=0;k<=ranking.size();k++)
			        {
			        	if(k==5) break;
			        	else if(k<flag) fw.append(ranking.get(k));
			        	else if(k==flag) 
			        	{
			        		if(correct_number==40)  //使输出对齐
			        			fw.append(correct_number+"      "+rank[RANK]+"     "+name);
			        		else if(correct_number>29)
			        			fw.append(correct_number+"    "+rank[RANK]+"     "+name);
			        		else if(correct_number>9)
			        			fw.append(correct_number+"    "+rank[RANK]+"   "+name);
			        		else
			        			fw.append(correct_number+"     "+rank[RANK]+"   "+name);
			        		
			        	}
			        	else fw.append(ranking.get(k-1));
			        	fw.append("\n");
			        }
			        fw.flush();
			        fw.close(); 
					}catch (Exception e) {e.printStackTrace();}
					
				}
				else if(number<=round)
				{	
					JOptionPane.showMessageDialog(play_frame, str,"时间到",JOptionPane.PLAIN_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(play_frame, str,"全部关卡结束",JOptionPane.PLAIN_MESSAGE);
				}
				play_frame.getContentPane().removeAll();
				((JPanel)(play_frame.getContentPane())).updateUI();
				play_frame.getContentPane().repaint();
				play_frame.initCover();
			}
		});
				
		countdown=new Thread(new Runnable(){  //小时间
			
			public void run()
			{
				int NO;
				while(true)
				{				
				little_count=30;
				while(little_count>=0 && cl_panel[number].score<5) //计时  或  找出5处后 结束
				{
					try{Thread.sleep(1000);}
					catch(Exception e){}
					pbar.setValue(little_count--);
					String str=new String().format("时间剩余 %d s", pbar.getValue());
					pbar.setString(str);
					pbar.setStringPainted(true);	
					NO=number+1;
					String s=new String().format("关数：%d            分数：%d",NO,correct_number+cl_panel[number].score);
					score_print.setText(s);	
					
				}
				correct_number+=cl_panel[number].score;
		
				if(number==round) //关卡放完则停止
				{
					number++;
					break;
				}
				else//超时或找完后下一关	
				{
				center_panel.remove(cl_panel[number]);
				center_panel.remove(cr_panel[number]);
				center_panel.updateUI();
				number++;			
				center_panel.add(cl_panel[number]);center_panel.add(cr_panel[number]);			
				center_panel.repaint();
				}
				}
			
			}
			
		});
		countdown0.start();//开始倒数线程
		countdown.start();//开始倒数线程
     	
		stop_flag=true;
		home_button=new MyButton("主菜单");
		home_button.addActionListener(this);
		
		stop_continue=new MyButton("暂停/继续");
		stop_continue.addActionListener(this);
		
		JPanel button_panel=new JPanel();
		button_panel.setOpaque(false);
		button_panel.add(home_button);
		
		button_panel.add(stop_continue);
		
		south_panel.add(button_panel);
		
		this.add(south_panel,BorderLayout.SOUTH);
		
		frame.setSize(1024,640);
		
		PF.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent we)
			{
				countdown.stop();
				countdown0.stop();
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
		countdown0.stop();
		this.frame.getContentPane().removeAll();
		((JPanel)(this.frame.getContentPane())).updateUI();
		this.frame.getContentPane().repaint();
		this.frame.initCover();
		}
		
		else
		{
			if(stop_flag==true)
			{
				countdown.suspend();
				countdown0.suspend();
				cl_panel[number].removeMouseListener(this);
				cr_panel[number].removeMouseListener(this);	
				center_panel.setVisible(false);
				stop_flag=false;
			}
			else
			{
				cl_panel[number].addMouseListener(this);
				cr_panel[number].addMouseListener(this);	
				center_panel.setVisible(true);
				countdown.resume();
				countdown0.resume();
				stop_flag=true;
			}
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
		cl_panel[number].userXY[0]=(int)(me.getX());cl_panel[number].userXY[1]=(int)(me.getY());
		cr_panel[number].userXY[0]=(int)(me.getX());cr_panel[number].userXY[1]=(int)(me.getY());
		cl_panel[number].repaint(); cr_panel[number].repaint();
		
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

