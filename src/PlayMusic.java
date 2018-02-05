import java.applet.AudioClip;  
import java.net.MalformedURLException;  
import java.net.URL;  
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JApplet;  

//播放音乐
public class PlayMusic {  
	private String music_name;
	private AudioClip bgm;
	private Clip clip;
	private boolean isLong;//标志是长音频还是短音频，长短音频的播放方式不同
	private AudioInputStream audioInputStream;
	
	PlayMusic(String music_name) 
	{
		this.music_name=music_name;
		
		//播放背景音乐（长音频）
		if(music_name.equals("bgm"))
		{
			isLong=true;
			
			URL url = null;  
			try { url = new URL("file:Music\\bgm.wav"); }   
			catch (MalformedURLException e) {}  
			bgm=JApplet.newAudioClip(url);
		}
		//播放点击音效（短音频）
		else
		{
			isLong=false;
			
			try{
				audioInputStream = AudioSystem.getAudioInputStream(new java.io.File(music_name));
				DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
				clip = (Clip) AudioSystem.getLine(info);
				clip.addLineListener(new LineListener() {
	                public void update(LineEvent e) 
	                {
	                    if (e.getType() == LineEvent.Type.STOP) 
	                    {
	                        synchronized(clip) { clip.notify(); }
	                    }
	                }
	            });
			}catch(Exception e){}
			
		}
	}
	
	//背景音乐循环播放
	public void loop() { bgm.loop(); }
	
	//音频单次播放
    public void play() 
    {
    	if(isLong==true)
    		bgm.play();
    	else 
    	{
    		try{
    			clip.open(audioInputStream);
    			clip.start();
    			synchronized (clip) { clip.wait(); }
    			clip.close();
    		}catch(Exception e){}
    	}
    }  

    //背景音乐停止播放
    public void stop() { bgm.stop(); }
}  