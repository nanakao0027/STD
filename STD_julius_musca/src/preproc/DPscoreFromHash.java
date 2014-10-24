package preproc;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DPscoreFromHash {

	private HashMap<String, String> map = new HashMap<String, String>(); // �����Ŏ���hash�e�[�u��

	
	
	
	/**
	 * �R���X�g���N�^
	 * �\�ߕۑ����Ă���Hash�e�[�u����ǂݍ���
	 */
	public DPscoreFromHash() {
		try {
			FileInputStream inFile = new FileInputStream("DPHashmap.map");
			ObjectInputStream in = new ObjectInputStream(inFile);
			map = (HashMap<String, String>) in.readObject();
			in.close();
			inFile.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * �p�����[�^�t���R���X�g���N�^
	 * param inputfile ���̓t�@�C��
	 * �e�L�X�g�t�@�C������hash�e�[�u�������ۑ�����
	 */
	public DPscoreFromHash(String inputfile)  {

		BufferedReader br = null;

		// �t�@�C�����͂̏���
		try {
			br = new BufferedReader(new FileReader(inputfile));
			// hashMap�����
			map = CreateHashMap(br);
		} catch (FileNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}


		// hashmap���I�u�W�F�N�g�Ƃ��ĕۑ�
		ObjectOutputStream out = null;

		try {
			out = new ObjectOutputStream(new FileOutputStream("DPHashmap.map"));
			out.writeObject(map);

		} catch (FileNotFoundException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		} catch (IOException e2) {
			// TODO �����������ꂽ catch �u���b�N
			e2.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}
	}



	private HashMap<String, String> CreateHashMap(BufferedReader br) {

		HashMap<String, String> ret = new HashMap<String, String>();

		try {				
			
			String splitString[] = null;
			String string = br.readLine();
			StringBuilder builder = new StringBuilder();
			while(string!= null) {
				splitString = string.split(",");
				builder.append(splitString[0]);	// ID
				builder.append(",");
				builder.append(splitString[1]);	// ipu
				builder.append(",");
				builder.append(splitString[2]);	// startFrame
				builder.append(",");
				builder.append(splitString[3]);	// endFrame
				builder.append(",");
											// DPscore
				ret.put(builder.toString(), splitString[4]);
				builder.setLength(0);
				string = br.readLine();
			}
			
			// (intval2ipu0 size n_ipu := 40497�܂�)
//			while (!br.readLine().split(" ")[0].equals("intval2ipu"));	// �K�v�ȍs�܂œǂݔ�΂�
//
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				
//				// 0703-0477 23040,23042 �����̌`���̃e�L�X�g
//				String[] lineSplit = line.split("[ ,]");	//" "��","�ŕ���split
//
//				// 
//				// ��2�`��3���[�h�܂ł̐���key�ɁA��1,2���[�h��l�ɂ���hash�e�[�u�����쐬
//				Integer valueOf = Integer.valueOf(lineSplit[1]);
//				Integer valueOf2 = Integer.valueOf(lineSplit[2]);
//
//				String ipuNumberString = lineSplit[0];
//				ipuNumberString = ipuNumberString.replace("-", "_");
//				ipuNumberString = ipuNumberString.substring(0, 2) + "-" + ipuNumberString.substring(2,ipuNumberString.length());
//				StringBuilder buffer = new StringBuilder();
//				buffer.append(ipuNumberString);
//				buffer.append(",");
//				buffer.append(lineSplit[1]);
//				String buf = buffer.toString();
//
//				//System.out.println(lineSplit[0] + ","  + lineSplit[1] + ","+ lineSplit[2]);
//
//				// end~start�̊Ԃ̐��������ׂēo�^����
//				for (int i = valueOf; i <= valueOf2; i++) {
//					ret.put(i, buf);	// hash�e�[�u���ɓo�^
//				}
//
//			}

		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}


		return ret;
	}



	/**
	 * Hash�֐�
	 * �O����L�[���n���ꂽ��l��Ԃ�
	 * @param key �L�[
	 */
	public String Hashfunc(int key) {
		return map.get(key);
	}
	
	
	/**
	 * DPHashmap.map����ǂݍ��݁Asyllmatch�ɑΉ�����ipu��syllstart���擾�ł���̂ł����set
	 * ipuArraylist�͎Q�Ɠn���Ȃ̂ŁA�����set���\�b�h��ǂݏo����set�����OK
	 * @param ipuArraylist
	 */
	public static void setFrom_hash(ArrayList<Ipu> ipuArraylist){

		// �R���X�g���N�^��DPHashmap.map��ǂݍ���
		DPscoreFromHash hash = new DPscoreFromHash();
		String[] hogeStrings;

		// syllmatch�@���@"ipu,start"��������A","��split����set���\�b�h�ɓ�����
		for(Ipu ipuObject : ipuArraylist){
			hogeStrings = hash.Hashfunc(Integer.valueOf(ipuObject.get_syllMatch())).split(",");
			ipuObject.set_ipu_syllStart(hogeStrings[0], hogeStrings[1]);
		}

		return ;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new DPscoreFromHash("ID_ipu_startFrame_outFrame_DPscore-threshold0.1.txt");
	}

	

}
