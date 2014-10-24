package preproc;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.*;


public class MakeWaveFile {

	// �f�B���N�g���̎w��ȂǂɎg��
	private static String TARGET_STRING;


	// wav�t�@�C�����������f�B���N�g���̃��[�g�B���Ƃ�ipu(07-01�Ƃ�)�𖖔��ɂ������ăf�B���N�g���w��
	private static final String ROOTDICSTRING_STRING = "/Users/takada/SDPWSspeech/";	// ���[�J���p
	//	private static final String ROOTDICSTRING_STRING = "/home/takada/newSTD/SDPWSspeech/";	// �T�[�o�p

	// �o�̓t�@�C���̃��[�g�f�B���N�g�����w�肷�邽�߂̕�����B���̂��Ƃ�TARGET_STRING������
	//private static final String OUTPUTROOTDICSTRING_STRING = "/Users/takada/Documents/workspace/newSTD/wav-mfcc-Result/";	// ���[�J���p
	private static final String OUTPUTROOTDICSTRING_STRING = "/Users/takada/Documents/workspace/newSTD/test/";	// ���[�J���p
	//	private static final String OUTPUTROOTDICSTRING_STRING = "/home/takada/newSTD/wavOuput-root/"; // �T�[�o�p





	public void createWav(String ID, String ipu, String start, String end)  {

		StringBuilder tempBuilder = new StringBuilder();

		
		//if(end.equals("pute") || start.equals("pute")) {
			//System.out.println(ID + " , " + ipu + " , " + start + " , " + end);
			//return;
		//}
		
		
		//�@����26�N7��31���@���}�����@�����ȊO�������烊�W�F�N�g
		//���肷��p�^�[���𐶐�
        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(start);
        if(!m.find()) {
        	System.out.println("start");
        	return;
        }
        m = p.matcher(end);
        if(!m.find()) {
        	System.out.println("end");
        	return;
        }

		// �Ώ�wav�̃p�X
		tempBuilder.append(ROOTDICSTRING_STRING);	// rootDirectoryPATH
		tempBuilder.append(ipu.substring(0,5));		// XX-YY �܂�
		tempBuilder.append("/");					// 
		tempBuilder.append(ipu);					// XX-YY/XX-YY_ZZZZ
		tempBuilder.append(".wav");					// XX-YY/XX-YY_ZZZZ.wav
		String audioFilePathString = tempBuilder.toString();
		tempBuilder.setLength(0);





		byte[] temp = null;
		AudioInputStream inAis = null;
		AudioFormat frmt = null;	
		try {
			// wav�ǂݍ���
			inAis = AudioSystem.getAudioInputStream(new File(audioFilePathString));
			frmt = inAis.getFormat();	// format�̓ǂݏo��

			// �f�[�^�����̓ǂݍ���
			temp = new byte[inAis.available()];
			inAis.read(temp);
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} finally {
			// temp�Ƀf�[�^��ǂݍ���
			try {
				inAis.close();
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}

		// �����w�肵�ăR�s�[�Aend���Ȃ�������wav�t�@�C���𒴂��Ă��܂����Ƃ�����̂ŁA�����Ȃ��ꍇ�̂ݎ��s�Aelse�Ŗ��̃t�@�C�������o�͂���return
		byte[] data = null;
		if(temp.length > Integer.valueOf(end)*320)
			if(Integer.valueOf(start)*320 < Integer.valueOf(end)*320) {
				//System.out.println(temp.length +  "," + Integer.valueOf(start)*320 + "," + Integer.valueOf(end)*320);
				data = Arrays.copyOfRange(temp, Integer.valueOf(start)*320, (Integer.valueOf(end)+1)*320);
			}
		else {
			System.out.println("path:" + audioFilePathString + ", " + "temp length:" + temp.length + ", " + "endFrame:" + Integer.valueOf(end)*320);
			System.out.println("info:" + ID + ", " + ipu + "," + start + "," + end);
			return;
		}


		InputStream in = null;
		AudioInputStream ais = null; //�t���[���̐�
		// �o��
		try {
			// ��������
			in = new ByteArrayInputStream(data);
			ais = new AudioInputStream(in,	frmt, data.length/2);


			// �I�[�f�B�I�t�@�C���̎�ނ��w��
			AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;

			// �o�̓f�B���N�g��
			tempBuilder.append(OUTPUTROOTDICSTRING_STRING);
			tempBuilder.append(TARGET_STRING);
			tempBuilder.append("/wavDirectory");
			File wavDirectory = new File(tempBuilder.toString());
			tempBuilder.setLength(0);

			// �o�̓t�@�C����
			tempBuilder.append(ID);
			tempBuilder.append("_");
			tempBuilder.append(ipu.substring(0,10));
			tempBuilder.append("_");
			tempBuilder.append(start);
			tempBuilder.append("_");
			tempBuilder.append(end);
			tempBuilder.append(".wav");
			File outputFile = new File(wavDirectory,tempBuilder.toString());
			tempBuilder.setLength(0);


			AudioSystem.write(ais, targetType, outputFile);
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} finally {
			try {
				in.close();
				ais.close();
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}

		// mfcclist, wavToMfcclist�����
		try {
			ListMake(ID, ipu, start, end);
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

		return;
	}


	private static void ListMake(String ID, String ipu, String start, String end) throws IOException {

		StringBuilder tempBuilder = new StringBuilder();


		// Mfcc��list
		// �o�͐�
		File mfccDirectory = new File(OUTPUTROOTDICSTRING_STRING + TARGET_STRING + "/mfcclist");
		File mfccFile = new File(mfccDirectory, ID + ".txt");

		// �������ޓ��e�@mfcc��PATH
		String mfcc = OUTPUTROOTDICSTRING_STRING + TARGET_STRING + "/mfcc/";
		tempBuilder.append(mfcc);
		tempBuilder.append(ID);
		tempBuilder.append("_");
		tempBuilder.append(ipu.substring(0,10));
		tempBuilder.append("_");
		tempBuilder.append(start);
		tempBuilder.append("_");
		tempBuilder.append(end);
		tempBuilder.append(".mfc");
		tempBuilder.append("\n");
		String mfccString =  tempBuilder.toString();
		tempBuilder.setLength(0);

		if(mfccFile.exists()){
			FileWriter filewriter = new FileWriter(mfccFile, true);

			filewriter.write(mfccString);
			filewriter.close();
			// �P��ID�ɑΉ������ꗗ�t�@�C�����Ȃ���΍��B
		}else{
			FileWriter filewriter = new FileWriter(mfccFile);

			filewriter.write(mfccString);
			filewriter.close();
		}





		// MfccSpacePlus��list
		// �o�͐�
		File mfccSpacePluslistDirectory = new File(OUTPUTROOTDICSTRING_STRING + TARGET_STRING + "/mfccSpacePluslist");
		File mfccSpacePluslistFile = new File(mfccSpacePluslistDirectory, ID + ".txt");

		// �������ޓ��e�@mfccSpacePlus��PATH
		String mfccSpacePlus = OUTPUTROOTDICSTRING_STRING + TARGET_STRING + "/mfccSpacePlus/";
		tempBuilder.append(mfccSpacePlus);
		tempBuilder.append(ID);
		tempBuilder.append("_");
		tempBuilder.append(ipu.substring(0,10));
		tempBuilder.append("_");
		tempBuilder.append(start);
		tempBuilder.append("_");
		tempBuilder.append(end);
		tempBuilder.append(".mfc");
		tempBuilder.append("\n");
		String mfccSpacePlusString =  tempBuilder.toString();
		tempBuilder.setLength(0);

		if(mfccSpacePluslistFile.exists()){
			FileWriter filewriter = new FileWriter(mfccSpacePluslistFile, true);

			filewriter.write(mfccSpacePlusString);
			filewriter.close();
			// �P��ID�ɑΉ������ꗗ�t�@�C�����Ȃ���΍��B
		}else{
			FileWriter filewriter = new FileWriter(mfccSpacePluslistFile);

			filewriter.write(mfccSpacePlusString);
			filewriter.close();
		}



		// wav to mfcc �̃��X�g
		File wavToMfccDirectory = new File(OUTPUTROOTDICSTRING_STRING + TARGET_STRING + "/wavToMfcc");
		File wavToMfccFile = new File(wavToMfccDirectory, ID + ".txt");
		tempBuilder.append(OUTPUTROOTDICSTRING_STRING);
		tempBuilder.append(TARGET_STRING);
		tempBuilder.append("/wavDirectory/");
		String wavDirectoryPATHString =tempBuilder.toString();
		tempBuilder.setLength(0);


		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(wavDirectoryPATHString);
		stringBuilder.append(ID);
		stringBuilder.append("_");
		stringBuilder.append(ipu.substring(0,10));
		stringBuilder.append("_");
		stringBuilder.append(start);
		stringBuilder.append("_");
		stringBuilder.append(end);
		stringBuilder.append(".wav");
		stringBuilder.append("	");
		stringBuilder.append(mfccString);
		//wavtomfcc
		String string = stringBuilder.toString();
		if(wavToMfccFile.exists()){
			FileWriter filewriter = new FileWriter(wavToMfccFile, true);

			filewriter.write(string);
			filewriter.close();
			// �P��ID�ɑΉ������ꗗ�t�@�C�����Ȃ���΍��B
		}else{
			FileWriter filewriter = new FileWriter(wavToMfccFile);

			filewriter.write(string);
			filewriter.close();
		}
	}



	/**
	 * ���s
	 * @param ipuArraylist
	 */
	public static void execute_wavCut(ArrayList<Ipu> ipuArraylist, String targetString) {

		TARGET_STRING = targetString;
		MakeWaveFile hoge = new MakeWaveFile();

		for(Ipu ipu : ipuArraylist){
			//System.out.println(IDString);
			hoge.createWav(ipu.get_ID(), ipu.get_ipu(), ipu.get_frameStart(), ipu.get_frameEnd());
			//createWavSpaceFramePlused(IDString,ipuString, matchString,endString);
		}
	}


	public static void main(String[] args) {

		FileReader inFileReader = null;
		BufferedReader brBufferedReader = null;

		String target = "best1_229.detect";
		TARGET_STRING = target;
		try {
			inFileReader = new FileReader(new File("ID_ipu_startFrame_outFrame_DPscore-" + target + ".txt"));
			brBufferedReader = new BufferedReader(inFileReader);
			System.out.println("Processing:" + inFileReader.toString());

			String hoge[] = null;
			String buf = null;
			MakeWaveFile hogeMakeWaveFile = new MakeWaveFile();
			buf = brBufferedReader.readLine();

			while(buf != null) {
				hoge = buf.split(",");
				if(Integer.valueOf(hoge[0]) > 231)
					hogeMakeWaveFile.createWav(hoge[0], hoge[1], hoge[2], hoge[3]);

				buf = brBufferedReader.readLine();
			}

			//			for (int i = 0; i < 100000; i++) {
			//
			//				hoge = buf.split(",");
			//
			//				hogeMakeWaveFile.createWav(hoge[0], hoge[1], hoge[2], hoge[3]);
			//
			//				buf = brBufferedReader.readLine();
			//			}

		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} finally {
			try {
				inFileReader.close();
				brBufferedReader.close();
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}


	}
}
