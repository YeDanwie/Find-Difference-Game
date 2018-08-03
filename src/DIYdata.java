import java.io.*;
import java.util.*;

//用于读取和保存玩家导入的关卡数据（顺带写了一个获取自带关卡数据的函数）
public class DIYdata {
	String source1_path,source1_name;
	String source2_path,source2_name;
	File message;
	double[] pointXY;
	static String data_path="D:\\DIYdata";
	ArrayList<String> data;  
	
	//将文件保存到指定路径
	public void save(String source1_path,String source1_name,String source2_path,String source2_name,double[] pointXY)throws Exception
	{
		this.source1_path=source1_path;
		this.source1_name=source1_name;
		this.source2_path=source2_path;
		this.source2_name=source2_name;
		this.pointXY=pointXY;	
		
		
		File dir=new File(data_path);
		if(!dir.exists()) dir.mkdirs();
		
		File source1=new File(source1_path);
		File copy1=new File(data_path+File.separator+source1_name);
		
		FileInputStream input=new FileInputStream(source1);
		FileOutputStream output=new FileOutputStream(copy1);
		
		
		//复制图片1到指定路径
		byte[] b=new byte[1024*50];
		int k;
        while ((k = input.read(b)) != -1) { output.write(b, 0, k); }
        output.flush();
        output.close();
        input.close();
        
        File source2=new File(source2_path);
		File copy2=new File(data_path+File.separator+source2_name);
        
        input=new FileInputStream(source2);
		output=new FileOutputStream(copy2);
		
		//复制图片2到指定路径
        while ((k = input.read(b)) != -1) { output.write(b, 0, k); }
        output.flush();
        output.close();
        input.close();
        
        //保存图片名称及对应图片不同处的坐标，存入message.txt
        message=new File(data_path+File.separator+"message.txt");
        if(!message.exists()) message.createNewFile();
        FileWriter fw=new FileWriter(message,true);
        fw.append(source1_name+' '+source2_name+' ');
        for(double xy:pointXY) fw.append(xy+" ");
        fw.append("\n");
        fw.close();
	}
	
	//根据key值（0读取自带关卡，1读取玩家导入关卡）将数据赋值给参数
	public void get(int key,ArrayList<String> name1,ArrayList<String> name2,ArrayList<double[]> XY)
	{
		if(key==1) {
			return; //没有玩家导入的关卡(这部分功能暂时弃用)
			//message=new File(data_path+File.separator+"message.txt");
		}
		else message=new File("DefaultData\\message.txt");

		try {
			BufferedReader br=new BufferedReader(new FileReader(message));
			String line;
			while((line=br.readLine())!=null)
			{
				String[] data=line.split(" ");
				name1.add(data[0]); name2.add(data[1]);
	
				double[] xy=new double[10];
				for(int i=2,j=0;i<12;i++,j++)
				{
					xy[j]=Double.valueOf(data[i]);
				}
				XY.add(xy);
			}
		} catch (Exception e) {e.printStackTrace();}
	}
}
