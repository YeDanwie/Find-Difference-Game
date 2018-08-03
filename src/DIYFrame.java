import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DIYFrame extends JFrame implements ActionListener{
	private JButton button_left,button_right,goback,finish;
	private JPanel panel,panel_south;
	private static MyPanel panel_left,panel_right;
	private static JTextArea tips,message;
	private Image imageCopy;
	private String source1_path,source1_name,source2_path,source2_name;
	
	//玩家导入关卡的窗体初始化
	DIYFrame()
	{
		super("导入图片素材");
		this.setSize(1024, 450);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BorderLayout());
		
		panel=new JPanel(new GridLayout(1,2));
		this.getContentPane().add(panel,BorderLayout.CENTER);
		
		panel_left=new MyPanel(); panel_right=new MyPanel();
		
		
		button_left=new JButton("选择图1"); button_right=new JButton("选择图2");
		panel_left.add(button_left); panel_right.add(button_right);
		button_left.addActionListener(this); button_right.addActionListener(this);
		
		panel.add(panel_left);
		panel.add(panel_right);
		
		panel_south=new JPanel(new GridLayout(1,3));
		
		tips=new JTextArea();
		panel_south.add(tips);
		tips.setEditable(false);
		tips.setFont(new Font("幼圆",0,18));
		tips.append("-请依次导入两张图片\n-图片长宽比大致为3：2\n-且图中有五处不同");
		
		message=new JTextArea();
		message.setEditable(false);
		message.setFont(new Font("华文彩云",0,18));
		panel_south.add(message);
		
		
		goback=new JButton("撤销"); finish=new JButton("完成");
		goback.addActionListener(this); finish.addActionListener(this);
		JPanel buttonPanel=new JPanel(); buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(goback); buttonPanel.add(finish);
		panel_south.add(buttonPanel);
		
		this.getContentPane().add(panel_south,BorderLayout.SOUTH);
		this.setResizable(false);
		this.validate();
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae) {
		//若事件源是选择图片的按钮
		if(ae.getSource().equals(button_left)||ae.getSource().equals(button_right))
		{
			JFileChooser chooser=new JFileChooser();
			FileNameExtensionFilter filter=new FileNameExtensionFilter("JPG & PNG Images","jpg","png");
			chooser.setFileFilter(filter);//限制玩家所选文件的格式
			
			//如果玩家没有选择文件，则直接返回
			if(chooser.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
			
			//获取玩家选择的图片
			Image image=Toolkit.getDefaultToolkit().createImage(chooser.getSelectedFile().getAbsolutePath());
			imageCopy=image.getScaledInstance(512, 360, Image.SCALE_DEFAULT);
			
			//将玩家选择的图片绘制出来
			if(ae.getSource().equals(button_left))
			{
				source1_path=chooser.getSelectedFile().getAbsolutePath();
				source1_name=chooser.getSelectedFile().getName();
				
				panel_left.remove(button_left);
				panel_left.image=imageCopy;
				panel_left.picsShowed=true;
				panel_left.repaint();
			}
			else if(ae.getSource().equals(button_right))
			{
				source2_path=chooser.getSelectedFile().getAbsolutePath();
				source2_name=chooser.getSelectedFile().getName();
				
				panel_right.remove(button_right);
				panel_right.image=imageCopy;
				panel_right.picsShowed=true;
				panel_right.repaint();
			}
			
			//显示文字提示玩家 点击不同处
			if(panel_left.picsShowed&&panel_right.picsShowed)
			{
				message.setText("开始点击不同处...");
			}
		}
		//事件源是撤销按钮
		else if(ae.getSource().equals(goback))
		{
			int num=MyPanel.pList.size();
			if(num>0)
			{
				MyPanel.pList.remove(num-1);//把上一个导入的坐标删去

				panel_left.repaint();
				panel_right.repaint();
			}
		}
		//事件源是完成按钮
		else if(ae.getSource().equals(finish))
		{
			int num=MyPanel.pList.size();
			if(num!=5)//点击的不同处不是五个
			{
				JOptionPane.showMessageDialog(this, "你所选的不同处不是五个","提示",JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			//点击的不同处是五个，把图片和不同处坐标保存到文件夹
			double[] pointXY=new double[10]; int count=0;
			for(Point p:MyPanel.pList)
			{
				pointXY[count++]=p.getX();
				pointXY[count++]=p.getY();
			}
			
			DIYdata cas=new DIYdata();
			try { cas.save(source1_path,source1_name,source2_path,source2_name,pointXY);} 
			catch (Exception e) { e.printStackTrace(); }
			
			JOptionPane.showMessageDialog(this, "图片素材导入完成！\n已存入到D盘下的DIYdata文件夹","提示",JOptionPane.PLAIN_MESSAGE);
			this.dispose();
		}
		
	}
	
	//自定义面板样式
	public static class MyPanel extends JPanel implements MouseListener{
		private Image image;
		private static ArrayList<Point> pList=new ArrayList<Point>();//存储不同处的坐标
		public boolean picsShowed=false;//标志图片是否已显示
		
		MyPanel()
		{
			super();
			super.addMouseListener(this);
			this.image=null;
			pList=new ArrayList<Point>();
		}
		
		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2d=(Graphics2D)g.create();
			
			
			g2d.drawImage(this.image, 0, 0, this);//绘制图片
			
			//画笔抗锯齿和设置粗细、颜色
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(5)); 
			g2d.setColor(Color.RED);
			
			//以不同处坐标点为圆心，半径为12画圆圈
			for(Point p:pList)
				g2d.drawArc((int)(p.getX()-12), (int)(p.getY()-12), 30, 30, 0, 360);
			
			//画出两张图片的分割线
			g2d.setStroke(new BasicStroke(5)); 
			g2d.setColor(Color.WHITE);
			g2d.drawLine(this.getSize().width, 0, this.getSize().width, this.getSize().height);
			
			g2d.dispose();
		}

		public void mouseClicked(MouseEvent e) 
		{
			MusicPlayer mp = new MusicPlayer("Music\\click.wav");//开启一个线程播放点击音效
			mp.start(false);
			
			//如果两张图片均已显示出来（玩家开始点击不同处），将坐标保存起来
			if(panel_left.picsShowed&&panel_right.picsShowed)
			{
				Point p=new Point(e.getX(),e.getY());
				pList.add(p);	
			}
			
			//将新保存的坐标绘制出来
			if(panel_left.getComponentCount()==0) panel_left.repaint();
			if(panel_right.getComponentCount()==0) panel_right.repaint();
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
}


