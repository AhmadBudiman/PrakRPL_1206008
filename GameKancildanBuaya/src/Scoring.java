import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

public class Scoring extends MIDlet implements CommandListener {

	private Display display;
	private Form form;
	private RecordStore rs;
	private RecordEnumeration re;
	private ChoiceGroup choicegroup;
	private Alert alert;
	private List list;
	private static final int score = 1000;

	//untuk proses entri data
	private Form entri;
	private TextField tfNama, tfhighScorep;

	private final Command cmdKeluar = new Command("Keluar", Command.EXIT, 1);
	private final Command cmdPilih = new Command("Pilih", Command.OK, 1);
	private final Command cmdSimpan = new Command("Simpan", Command.SCREEN, 1);
	private final Command cmdHapus = new Command("Reset", Command.SCREEN, 1);
	private final Command cmdKembali = new Command("Kembali", Command.BACK, 1);

	public Scoring() {
		display = Display.getDisplay(this);

		alert = new Alert(null);
		alert.setTimeout(Alert.FOREVER);

		list = new List(null, Choice.IMPLICIT);

		rs = null;
		// membuat atau membuka record store
		try {
			rs = RecordStore.openRecordStore("contohDB", true);
		} catch (RecordStoreException rse) {
			alert.setString("Record store tidak dapat dibuka. Aplikasi akan dihetikan");
      		alert.setType(AlertType.ERROR);
      		display.setCurrent(alert, null);
      		System.exit(1);
    	}
	}

	public void startApp() {
		entriData();
	}

	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {
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

	public void lihatRecord() {
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
          			int highScorep = dis.readInt();
          			list.append(nama + " [" + highScorep + "]", null);
        		} catch (IOException ioe) {
          			ioe.printStackTrace();
        		}
      		}
	      	list.addCommand(cmdKembali);
	      	list.addCommand(cmdHapus);
	      	list.setCommandListener(this);
	      	display.setCurrent(list);
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
            		rs.deleteRecord(i);
        		} catch (IOException ioe) {
          			ioe.printStackTrace();
        		}
      		}
      		re.rebuild();
      		lihatRecord();
    	} catch (RecordStoreNotOpenException rsnoe) {
      		rsnoe.printStackTrace();
    	} catch (RecordStoreException rse) {
      		rse.printStackTrace();
    	}
	}

	public Form entriData() {
		entri = new Form("Entri Data");
    	tfNama = new TextField("Nama:", "", 25, TextField.ANY);
    	entri.append(tfNama);
    	entri.addCommand(cmdSimpan);
    	entri.addCommand(cmdKembali);
    	entri.setCommandListener(this);
    	display.setCurrent(entri);
    	return entri;
	}

	public void commandAction(Command c, Displayable s) {
		if (c == cmdKeluar) {
			destroyApp(false);
			notifyDestroyed();
		}else if (c == cmdKembali) {
			tfNama.setString("");
			display.setCurrent(entri);
		} else if (c == cmdSimpan) {
      		if (tfNama.getString() == "") {
        		tambahRecord("Noname", score);
        		tfNama.setString("");
        		lihatRecord();
      		} else if (tfNama.getString() != ""){
				tambahRecord(tfNama.getString(), score);
        		tfNama.setString("");
        		lihatRecord();
      		}
    	} else if (c == cmdHapus) {
      		int pos = list.getString(list.getSelectedIndex()).indexOf(" [");
      		String nama = list.getString(list.getSelectedIndex()).substring(0, pos);
      		hapusRecord(nama);
    	}
	}
}
