import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.rms.*;

public class Play extends Canvas implements Runnable, PlayerListener, CommandListener{

	private KancildanBuaya midlet;
	private Display display;
	private int y = 50;
	private int px = 0;
	private int py = 180;
	private boolean yes = true;
	private String bgImgN = "/images/latar.png";
	private Image bgImg = null;
	private int randRange = 1; //buaya yang datang
	private int[] buayaX = new int[randRange];
	private int[] buayaY = new int[randRange];
	private int xScreen = 245;
	private int yScreen = 290;
	private int yBg = 389;
	private int hKan = 40;
	private int wKan = 30;
	private int buayaAmount = 0;
	private int whbuaya = 30;
	private boolean first = true;
	private int minIndex = 0;
	private Random random;
	private boolean gameover = false;
	private Player player;
	private VolumeControl vc;
	public int gauge = 1000;
	private final Command cmdBack = new Command("Back", Command.BACK, 0);
	private final Command cmdStop = new Command("Stop", Command.STOP, 1);
	private Form form;
	private RecordStore rs;
	private RecordEnumeration re;
	private ChoiceGroup choicegroup;
	private Alert alert;
	private List list=new List("Highscore",List.IMPLICIT);

	//untuk proses entri data
	private Form entri;
	private TextField tfNama, tfhighScorep;

	private final Command cmdKeluar = new Command("Keluar", Command.EXIT, 1);
	private final Command cmdTampil = new Command("Tampil", Command.OK, 1);
	private final Command cmdSimpan = new Command("Simpan", Command.OK, 1);
	private final Command cmdHapus = new Command("Hapus", Command.SCREEN, 1);
	private final Command cmdHapusAll = new Command("Hapus Semua", Command.SCREEN, 2);

	//KONSTRUKTOR
	//INSTAN DARI CLASS PLAY.JAVA
	public Play(KancildanBuaya midlet, Display display, Player player) {
		createDB();
		//IMPORT java.util.random;
		//Untuk mengacak munculnya buaya ke layar
		random = new Random(); //Meregister Object random
		this.midlet = midlet;
		this.display = display; //Menampilkan permainan ke layar
		this.player = player; //Mengaktifkan suara mutimedia (midi/wav)

		try {
			//Mengambil gambar dari sebuah folder
			//Selalu menggunakan TRY{} agar bisa dibuka
			bgImg = Image.createImage(bgImgN);
		} catch (java.io.IOException e) {
		}

		addCommand(cmdBack); //Menambahkan fungsi cmdBack ke layar
		addCommand(cmdStop); //Menambahkan fungsi cmdStop ke layar
		setCommandListener(this); //Mengaktifkan Command cmdBack dan cmdStop
	}

	protected void paint(Graphics g) {
		//Mengatur ukuran layar pada permainan
		//(x1, y1) - (x2 y2)
		g.setClip(0,0,xScreen,yScreen);
		//Gambar background ditampilkan
		//Gambar background digulung agar tampak berjalan kebawah
		g.drawImage(bgImg,0,-(y + 1),Graphics.TOP|Graphics.LEFT);

		//Mengatur ukuran gambar background
		if(bgImg.getHeight() == 580){
			//Menggambar background mulai dari sudut kiri atas
			g.drawImage(bgImg,0,0,Graphics.TOP|Graphics.LEFT);
		}

		if(gameover != true){
			//Menampilkan Score disudut kanan atas
			g.drawString("Skor: " + gauge, getWidth() - 80, 0, Graphics.TOP | Graphics.LEFT);
		}
		//Jika gameover, score ditempatkan di tengah layar
		//dan menghapus tulisan score dari sudut kanan atas
		if(gameover == true){

			midlet.StopMedia();
			entriData();
		}

		int xpic;
		int ypic;
		//Membuat kondisi apabila kancil dimakan buaya
                // Jika termakan maka game over
		if((shooted() == true)||(gauge <= 0)){
			gameover = true;
			g.setClip((px - 2),(py - 2),70,70);
			xpic = 100;
			ypic = 500;
			g.drawImage(bgImg,(px - 2) - xpic,-(ypic - (py - 2)),Graphics.TOP|Graphics.LEFT);
		}else{
			if((buayaY[minIndex] >= (yScreen - 15))||(first == true)){
				acakBuaya();
				if(first == true){
					first = false;
				}
			}else{
				for(int i=0;i<buayaAmount;i++) {
					buayaY[i]++;
					if(buayaY[i] > yScreen){
						gauge = gauge + 3;
					}
				}
			}
			xpic = 75;
			ypic = 500;
			for(int i=0;i<buayaAmount;i++) {
				if(buayaY[i] <= (yScreen - 15)){
					g.setClip(buayaX[i],buayaY[i],whbuaya,whbuaya);
					g.drawImage(bgImg,buayaX[i] - xpic,-(ypic - buayaY[i]),Graphics.TOP|Graphics.LEFT);
				}
			}
//Clipping Gambar
			g.setClip(px,py,hKan,wKan);
			xpic = 25;
			ypic = 500;
			g.drawImage(bgImg,px - xpic,-(ypic - py), Graphics.TOP|Graphics.LEFT);
		}
	}
	//Menangani peristiwa yang terjadi yang dilakukan oleh user
	//Misal penanganan terhadap keyboard
	//ketika ditekan. Panah atas, bawah, kanan atau kiri untuk
	//menghindari buaya

	protected void keyPressed(int keyCode) {
		switch (keyCode) {
			case -1: // Kancil bergerak ke atas sejauh 1 langkah
				py = py - 10;
				if(py < 0){
					py = py + 10;
				}
		    	break;
                        case -2: // Kancil bergerak ke bawah sejauh 1 langkah
				py = py + 10;
				if(py > (yScreen - hKan)){
					py = py - 10;
				}
			break;
                        case -3: // Kancil bergerak ke kiri sejauh 1 langkah
				px = px - 10;
				if(px < 0){
					px = px + 10;
				}
			break;
                        case -4: // Kancil bergerak ke kanan sejauh 1 langkah
				px = px + 10;
				if(px > (xScreen - wKan)){
					px = px - 10;
				}
			break;
		}
	}
	//Permainan akan selalu dijalankan selama
	//belum terjadi gameover
    public void run() {
		while (gameover == false) {
			try {
				synchronized (this) {
					if(y <= 0){
						y = yBg - yScreen - 100;
					}else{
						y--;
					}
					Thread.sleep(10);
					gauge = gauge + 1;
					repaint();
				}
			} catch (Exception ie) {
			}
		}
		//Jika buaya mengenai kancil, maka gameover diaktifkan
		//dan buaya dinonaktifkan selama 1 detik
		if(gameover == true){
			buayaAmount = 0;
			try {
				Thread.sleep(0); //1000 ms = 1 detik
				player.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

	//Untuk mengacak tampilnya buaya
	public void acakBuaya(){
		buayaAmount = random.nextInt() % randRange;
		if(buayaAmount < 1){
			buayaAmount = randRange;
		}
		random.setSeed(xScreen);
		for(int i=0;i<buayaAmount;i++){
			int xBuaya = random.nextInt() % (xScreen - 15);
			int yBuaya = random.nextInt() % (yScreen - 40);
			if(xBuaya < 0){
				xBuaya = xBuaya + (xScreen - 15);
			}
			buayaX[i] = xBuaya;
			buayaY[i] = yBuaya - (yScreen - 40);
		}
		minIndex = 0;
		int min = buayaY[minIndex];
		for(int i=1;i<buayaAmount;i++) {
			if(min > buayaY[i]){
				min = buayaY[i];
				minIndex = i;
			}
		}
	}

	//Penanganan apabila Kancil termakan Buaya
	boolean shooted(){
		boolean res = false;
		int i = 0;
		//Menangani apabila buaya memakan kancil dari sisi kanan
		while((res == false) && (i < buayaAmount)) {
			if((inBuayaRange(buayaX[i] + 12, buayaY[i])) == true){
				res = true;
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//Menangani apabila buaya memakan kancil dari sisi kiri
			if((inBuayaRange(buayaX[i] - 12, buayaY[i])) == true){
				res = true;
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//Menangani apabila buaya memakan dari sisi depan
			if((inBuayaRange(buayaX[i], buayaY[i])) == true){
				res = true;
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			i++;
		}
		return res;
	}
	//Mengatur posisi buaya
	boolean inBuayaRange(double x, double y){
		boolean res = false;
		if((x >= px) && (x <= (px + (wKan / 2))) && (y >= py) && (y <= (py + hKan))){
			res = true;
		}
		return res;
	}
	//Fungsi untuk menjalankan file suara wav
	private void playMedia(String file)
		throws Exception {
		player = Manager.createPlayer(getClass().getResourceAsStream(file), "audio/x-wav");

		player.addPlayerListener(this);

		//player.setLoopCount(-1);
		player.prefetch();
		player.realize();

		vc = (VolumeControl)player.getControl("VolumeControl");
		if (vc != null)
			vc.setLevel(30);


		player.start();
	}
	//Menghentikan file suara wav
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
	//Untuk mengupdate file suara yang dijalankan
	public void playerUpdate(Player player, String event, Object eventData) {
		if(event.equals(PlayerListener.STARTED) &&
		  new Long(0L).equals((Long)eventData)) {
		} else if(event.equals(PlayerListener.CLOSED)) {
		}
	}

	public void tambahRecord(String nama, int highScore) {
        byte[] temp = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
			dos.writeUTF(nama);
            dos.writeInt(highScore);
            temp = baos.toByteArray();
		} catch (IOException ioe) {
      		ioe.printStackTrace();
		}
		try {
      		rs.addRecord(temp, 0, temp.length);
		} catch (RecordStoreNotOpenException rsnoe) {
			rsnoe.printStackTrace();
		} catch (RecordStoreException rse) {
      		rse.printStackTrace();
		}
	}

	public void TampilRecord() {
	    byte[] temp = null;
	    list.setTitle("Daftar Record");
	    list.deleteAll();

	    try {
	      re = rs.enumerateRecords(null, null, false);
	      while (re.hasNextElement()) {
	        int i = re.nextRecordId();
	        temp = rs.getRecord(i);
	        ByteArrayInputStream bais = new ByteArrayInputStream(temp);
	        DataInputStream dis = new DataInputStream(bais);
	        try {
	          String nama = dis.readUTF();
	          int nilai = dis.readInt();
	          list.append(nama + " [" + nilai + "]", null);
	        } catch (IOException ioe) {
	          ioe.printStackTrace();
	        }
	      }
	      list.addCommand(cmdBack);
	      list.addCommand(cmdHapus);
	      list.addCommand(cmdHapusAll);
	      list.setCommandListener(this);
	      midlet.display.setCurrent(list);
	    } catch (InvalidRecordIDException invID) {
	      invID.printStackTrace();
	    } catch (RecordStoreNotOpenException rsnoe) {
	      rsnoe.printStackTrace();
	    } catch (RecordStoreException rse) {
	      rse.printStackTrace();
	    }
	}

	public void hapusRecord(String nama) {
		byte[] temp = null;
    	try {
      		re = rs.enumerateRecords(null, null, false);
      		while (re.hasNextElement()) {
        		int i = re.nextRecordId();
        		temp = rs.getRecord(i);
        		ByteArrayInputStream bais = new ByteArrayInputStream(temp);
        		DataInputStream dis = new DataInputStream(bais);
        		try {
                    String vNama = dis.readUTF();
          			if (vNama.equals(nama)) {
            			rs.deleteRecord(i);
           				break;
          			}
        		} catch (IOException ioe) {
                    ioe.printStackTrace();
        		}
      		}
      		re.rebuild();
      		TampilRecord();
    	} catch (RecordStoreNotOpenException rsnoe) {
      		rsnoe.printStackTrace();
    	} catch (RecordStoreException rse) {
      		rse.printStackTrace();
    	}
	}

	public void hapusRecordAll(String nama) {
		byte[] temp = null;
    	try {
      		re = rs.enumerateRecords(null, null, false);
      		while (re.hasNextElement()) {
        		int i = re.nextRecordId();
        		temp = rs.getRecord(i);
        		ByteArrayInputStream bais = new ByteArrayInputStream(temp);
        		DataInputStream dis = new DataInputStream(bais);
        		try {
                    String vNama = dis.readUTF();
            		rs.deleteRecord(i);
        		} catch (IOException ioe) {
                    ioe.printStackTrace();
        		}
      		}
      		re.rebuild();
      		TampilRecord();
    	} catch (RecordStoreNotOpenException rsnoe) {
      		rsnoe.printStackTrace();
    	} catch (RecordStoreException rse) {
      		rse.printStackTrace();
    	}
	}

	public void createDB(){
		rs = null;
	    // membuat atau membuka record store
	    try {
	      	rs = RecordStore.openRecordStore("GameKancildanBuaya", true);
	    } catch (RecordStoreException rse) {
	      	alert = new Alert("Error", "Record store tidak dapat dibuka.", null, AlertType.ERROR);
	      	alert.setTimeout(1000);
	      	display.setCurrent(alert, null);
	      	System.exit(1);
	    }
	}

	public void entriData() {
            entri = new Form("Entri Data");
            tfNama = new TextField("Nama:", "", 25, TextField.ANY);
            entri.append(tfNama);
            entri.addCommand(cmdSimpan);
            entri.setCommandListener(this);
            midlet.display.setCurrent(entri);
            //return entri;
	}

	//Menangani perintah tombol
	public void commandAction(Command c, Displayable d){
		//Jika perintah yang dipilih adalah cmdBack, maka
		//Menu ditampilkan.
		if(c == cmdBack){
			//midlet.ProgramUtama();
			midlet.display.setCurrent(midlet.lstUtama);
		}
		//Jika tombol cmdStop dipilih, maka
		//permainan dihentikan
		else if(c == cmdStop){
			midlet.exitMIDlet();
//			StopMedia();
		}
		else if (c == cmdSimpan) {
            try{
      			if (tfNama.getString() == "") {
                    tambahRecord("Noname", gauge);
                    tfNama.setString("");
      			} else if (tfNama.getString() != ""){
                    tambahRecord(tfNama.getString(), gauge);
                    tfNama.setString("");
      			}else{
			//	Alert alert = new Alert("", "Error, data gagal disimpan!", null, AlertType.INFO);
				alert.setTimeout(1000);
				display.setCurrent(alert);
				}
                TampilRecord();
            }catch(Exception e){
      			//Alert alert = new Alert("", "Error, data gagal disimpan!", null, AlertType.INFO);
                                alert.setTimeout(1000);
				display.setCurrent(alert);
            }
		}
		else if (c == cmdHapus) {
			int pos = list.getString(list.getSelectedIndex()).indexOf(" [");
			String nama = list.getString(list.getSelectedIndex()).substring(0, pos);
			hapusRecord(nama);
		}
		else if (c == cmdHapusAll) {
			int pos = list.getString(list.getSelectedIndex()).indexOf(" [");
			String nama = list.getString(list.getSelectedIndex()).substring(0, pos);
			hapusRecordAll(nama);
		}
	}
}
