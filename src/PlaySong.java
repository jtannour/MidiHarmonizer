

import org.jfugue.Player;

public class PlaySong implements Runnable{

	Thread myThread;
	Player player;
	String musicString = "";
	boolean playNow = false;

	
	public PlaySong(){
		start();
	}
	@Override
	public void run() {

		while(true){
	
			if(playNow == true){
				playSong();
				playNow = false;
			}
			
		}
		
	}
	
	public void start(){
		myThread = new Thread(this);
		myThread.start();
		player = new Player();
	}
	
	public void setMusicString(String musicString, boolean play){
		this.musicString = musicString;
		this.playNow = play;
	}

	public void playSong(){
		player.play(musicString);
	}
	

	
	public Player getPlayer(){
		return player;
	}

}
