import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Main {
	public static void main(String[] args)
	{
		PlayFrame pf=new PlayFrame();
		final SelectPanel sp=new SelectPanel(pf);
		pf.initCover();
		final PlayMusic bgm=new PlayMusic("bgm");
		bgm.loop();
		
		pf.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent we)
			{
				bgm.stop();
			}
		});
	}
}
