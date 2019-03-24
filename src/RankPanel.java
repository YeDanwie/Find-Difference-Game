import javax.swing.*;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

public class RankPanel extends JPanel implements ActionListener{
	private PlayFrame frame;
	private MyButton home_button;
	JTextArea print,tips;
	ArrayList<String> list;
	
	RankPanel(PlayFrame frame)
	{	
		super(new BorderLayout());
		this.frame=frame;
		
		home_button=new MyButton("返回");
		home_button.addActionListener(this);
		JPanel button_panel=new JPanel();
		button_panel.setBackground(new Color(225,255,255));
		button_panel.setBorder(new EmptyBorder(20,50,30,50));
		button_panel.add(home_button);
		this.add(button_panel,BorderLayout.SOUTH);
		
		JTextArea title=new JTextArea(); //最上方标题
		title.setEditable(false);
		title.setFont(new Font("幼圆",Font.BOLD,65));
		title.setForeground(new Color(255,105,180)); 
		title.setBackground(new Color(225,255,255));
		title.setBorder(new EmptyBorder(35,410,0,0));
		title.setText("排行榜");
		this.add(title, BorderLayout.NORTH);
		
		list=new ArrayList<String>();
		
		File rank=new File("DefaultData\\\\rank_list.txt");
		try
		{
			BufferedReader br=new BufferedReader(new FileReader(rank));
			String line;
			while((line=br.readLine())!=null)
			{
				list.add(line);	
			}
			br.close();
		}catch(Exception e) {e.printStackTrace();}

		JPanel center_panel=new JPanel();
		center_panel.setLayout(new BorderLayout());
		center_panel.setBackground(new Color(225,255,255));
		
		int NO;
		print=new JTextArea();	
		print.setEditable(false);
		print.setFont(new Font("楷体",0,35));
		print.setOpaque(false);
		print.setBorder(new EmptyBorder(20,230,50,0));
		print.setText("名次     分数     等级     名字\n");
		for(int i=0;i<list.size();i++)
		{
			NO=i+1;
			print.append(" "+NO+"        ");
			print.append(list.get(i));
			print.append("\n");
		}	
		center_panel.add(print,BorderLayout.CENTER);
		
	    tips=new JTextArea();
	    tips.setEditable(false);
	    tips.setOpaque(false);
	    tips.setFont(new Font("黑体",0,20));
	    tips.setBorder(new EmptyBorder(0,200,0,0));
	    tips.setText("                     --------------------\n"
	    +"               ----- 排行榜记录前五名玩家 -----\n"+"          ------------- 分数段对应等级 -------------\n"
	    +"     反应迟钝（0-9）  熟能生巧（10-16）  眼明手快（17-23）  \r\n"
	    +"小菜一碟（24-29）  三只眼（30-34）  千里眼（35-39）  神眼（40）");
	    center_panel.add(tips,BorderLayout.SOUTH);
	    
	    this.add(center_panel,BorderLayout.CENTER);
	}
	
	
	
	public void actionPerformed(ActionEvent ae) {
		Thread thread=new Thread(new Runnable(){
			public void run()
			{
				PlayMusic click=new PlayMusic("Music\\click.wav");
				click.play();
			}
		});
		thread.start();//开启线程播放点击音效
		
		this.frame.getContentPane().removeAll();
		((JPanel)(this.frame.getContentPane())).updateUI();
		this.frame.getContentPane().repaint();
		this.frame.initCover();
	}

}


