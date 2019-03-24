import java.applet.AudioClip;  
import java.net.MalformedURLException;   
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
	private Clip clip;
	private AudioInputStream audioInputStream;
	
	PlayMusic(String music_name) 
	{
		this.music_name=music_name;
		
		//播放点击音效		
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
	
	//音频单次播放
    public void play() 
    {
    	try{
    		clip.open(audioInputStream);
    		clip.start();
    		synchronized (clip) { clip.wait(); }
    		clip.close();
    	   }catch(Exception e){}   	
    }  

}  