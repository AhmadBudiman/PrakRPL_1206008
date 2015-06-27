import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.rms.*;

public class KancildanBuaya extends MIDlet implements PlayerListener, CommandListener{

	private Play play;
	public Display display;
	private static Player player;
	private VolumeControl vc;
	private Command cmdPlay = new Command("Play", Command.OK, 1);
	private Command cmdBack = new Command("Back", Command.BACK, 2);
	private Image imgBantuan, imgTentang, imgKeluar, imgMain, imgSkor;
	private Image imgPembuat, imgSplash;
	List lstUtama;
	Alert altPembuat, altSplash;

	public Alert alert;
  	private List list;
  	private RecordStore rs;
  	private RecordEnumeration re;


	public KancildanBuaya() {
		play = new Play(this, display, player);
		display = Display.getDisplay(this);
	}
//menampilkan splash
	public void Splash(){
		try{
			imgSplash = Image.createImage("/images/game.png");
		}catch(Exception e){
			System.out.print(e);
		}

		altSplash = new Alert(null, "", imgSplash, AlertType.INFO);
		altSplash.setTimeout(4000);
	}

    public void SplashScreenPembuat(){
		try{
			imgPembuat = Image.createImage("/images/bguus.png");
		}catch(Exception e){
			System.out.print(e);
		}

		play.TampilRecord();

		altPembuat = new Alert(null, "", imgPembuat, null);
		altPembuat.setTimeout(7000);
		altPembuat.setTicker(new Ticker("Uus Muhamad Husni Tamyiz 23209357"));
	}

	public void startApp() {
        SplashScreenPembuat();
        ProgramUtama();
		display.setCurrent(altPembuat, lstUtama);
	}

	public void ProgramUtama() {

		
                lstUtama = new List("Menu Utama", List.IMPLICIT);
		lstUtama.append("  Main", imgMain);
                lstUtama.append("  Skor", imgSkor);
		lstUtama.append("  Bantuan", imgBantuan);
		lstUtama.append("  Tentang Permainan", imgTentang);
		lstUtama.append("  Keluar", imgKeluar);

                lstUtama.setCommandListener(this);
		StopMedia();
	}

	public void Help(){
		Form frmHelp = new Form("Help");

		String strHelp = "Gunakan Tombol Untuk Menggerakan Kancil agar terhindar dari Buaya";
		frmHelp.append(strHelp);
		frmHelp.addCommand(cmdBack);
		frmHelp.setCommandListener(this);
		display.setCurrent(frmHelp);
	}

	public void About(){
		Form frmAbout = new Form("About");

		String strAbout = "Tugas Aplikasi Mobile Oleh Uus Muhamad Husni Tamyiz 23209357";
		frmAbout.append(strAbout);
		frmAbout.addCommand(cmdBack);
		frmAbout.setCommandListener(this);
		display.setCurrent(frmAbout);
	}

	public void pauseApp(){}

	public void destroyApp(boolean unconditional) {
		if(player != null){
			player.close();
		}
	}

	public void commandAction(Command c, Displayable s) {
		if(c == List.SELECT_COMMAND){
			switch(lstUtama.getSelectedIndex()){
				case 0:	//Play
					try {
						playMediaWAV("/sounds/003.wav");
					} catch (Exception e) {
						e.printStackTrace();
					}
					play.setFullScreenMode(true);
					Splash();
					display.setCurrent(altSplash, play);
					new Thread(play).start();
					break;
                case 1:
                    play.TampilRecord();
                    break;
				case 2:
					Help();
					break;
				case 3:
					About();
					break;
				case 4:
					exitMIDlet();
					break;
			}
		}
		if(c == cmdBack){
			ProgramUtama();
			display.setCurrent(lstUtama);
		}
	}

	public void exitMIDlet() {
		destroyApp(false);
		notifyDestroyed();
	}

	public void playMediaWAV(String file) throws Exception {
		player = Manager.createPlayer(getClass().getResourceAsStream(file), "audio/x-wav");

		player.addPlayerListener(this);

		player.setLoopCount(-1);
		player.prefetch();
		player.realize();

		vc = (VolumeControl)player.getControl("VolumeControl");
		if (vc != null)
			vc.setLevel(30);


		player.start();
	}

	public void StopMedia(){
		try{
			player.stop();
			player.deallocate();
			player.close();
			player = null;
		}catch(Exception e){
			System.out.print(e);
		}
	}

	public void playerUpdate(Player player, String event, Object eventData) {
		if(event.equals(PlayerListener.STARTED) &&
		  new Long(0L).equals((Long)eventData)) {
		} else if(event.equals(PlayerListener.CLOSED)) {
		}
	}
};


