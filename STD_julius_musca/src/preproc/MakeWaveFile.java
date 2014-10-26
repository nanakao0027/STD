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

	// ディレクトリの指定などに使う
	private static String TARGET_STRING;


	// wavファイルが入ったディレクトリのルート。あとでipu(07-01とか)を末尾にくっつけてディレクトリ指定
	private static final String ROOTDICSTRING_STRING = "/Users/takada/SDPWSspeech/";	// ローカル用
	//	private static final String ROOTDICSTRING_STRING = "/home/takada/newSTD/SDPWSspeech/";	// サーバ用

	// 出力ファイルのルートディレクトリを指定するための文字列。このあとにTARGET_STRINGが続く
	//private static final String OUTPUTROOTDICSTRING_STRING = "/Users/takada/Documents/workspace/newSTD/wav-mfcc-Result/";	// ローカル用
	private static final String OUTPUTROOTDICSTRING_STRING = "/Users/takada/Documents/workspace/newSTD/test/";	// ローカル用
	//	private static final String OUTPUTROOTDICSTRING_STRING = "/home/takada/newSTD/wavOuput-root/"; // サーバ用





	public void createWav(String ID, String ipu, String start, String end)  {

		StringBuilder tempBuilder = new StringBuilder();


		//if(end.equals("pute") || start.equals("pute")) {
			//System.out.println(ID + " , " + ipu + " , " + start + " , " + end);
			//return;
		//}


		//　平成26年7月31日　応急処理　数字以外が来たらリジェクト
		//判定するパターンを生成
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

		// 対象wavのパス
		tempBuilder.append(ROOTDICSTRING_STRING);	// rootDirectoryPATH
		tempBuilder.append(ipu.substring(0,5));		// XX-YY まで
		tempBuilder.append("/");					//
		tempBuilder.append(ipu);					// XX-YY/XX-YY_ZZZZ
		tempBuilder.append(".wav");					// XX-YY/XX-YY_ZZZZ.wav
		String audioFilePathString = tempBuilder.toString();
		tempBuilder.setLength(0);





		byte[] temp = null;
		AudioInputStream inAis = null;
		AudioFormat frmt = null;
		try {
			// wav読み込み
			inAis = AudioSystem.getAudioInputStream(new File(audioFilePathString));
			frmt = inAis.getFormat();	// formatの読み出し

			// データ部分の読み込み
			temp = new byte[inAis.available()];
			inAis.read(temp);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			// tempにデータを読み込み
			try {
				inAis.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		// 部分指定してコピー、endがなぜか元のwavファイルを超えてしまうことがあるので、超えない場合のみ実行、elseで問題のファイル名を出力してreturn
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
		AudioInputStream ais = null; //フレームの数
		// 出力
		try {
			// 書き込み
			in = new ByteArrayInputStream(data);
			ais = new AudioInputStream(in,	frmt, data.length/2);


			// オーディオファイルの種類を指定
			AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;

			// 出力ディレクトリ
			tempBuilder.append(OUTPUTROOTDICSTRING_STRING);
			tempBuilder.append(TARGET_STRING);
			tempBuilder.append("/wavDirectory");
			File wavDirectory = new File(tempBuilder.toString());
			tempBuilder.setLength(0);

			// 出力ファイル名
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
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				in.close();
				ais.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		// mfcclist, wavToMfcclistを作る
		try {
			ListMake(ID, ipu, start, end);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return;
	}


	private static void ListMake(String ID, String ipu, String start, String end) throws IOException {

		StringBuilder tempBuilder = new StringBuilder();


		// Mfccのlist
		// 出力先
		File mfccDirectory = new File(OUTPUTROOTDICSTRING_STRING + TARGET_STRING + "/mfcclist");
		File mfccFile = new File(mfccDirectory, ID + ".txt");

		// 書き込む内容　mfccのPATH
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
		tempBuilder.append("¥n");
		String mfccString =  tempBuilder.toString();
		tempBuilder.setLength(0);

		if(mfccFile.exists()){
			FileWriter filewriter = new FileWriter(mfccFile, true);

			filewriter.write(mfccString);
			filewriter.close();
			// 単語IDに対応した一覧ファイルがなければ作る。
		}else{
			FileWriter filewriter = new FileWriter(mfccFile);

			filewriter.write(mfccString);
			filewriter.close();
		}





		// MfccSpacePlusのlist
		// 出力先
		File mfccSpacePluslistDirectory = new File(OUTPUTROOTDICSTRING_STRING + TARGET_STRING + "/mfccSpacePluslist");
		File mfccSpacePluslistFile = new File(mfccSpacePluslistDirectory, ID + ".txt");

		// 書き込む内容　mfccSpacePlusのPATH
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
		tempBuilder.append("¥n");
		String mfccSpacePlusString =  tempBuilder.toString();
		tempBuilder.setLength(0);

		if(mfccSpacePluslistFile.exists()){
			FileWriter filewriter = new FileWriter(mfccSpacePluslistFile, true);

			filewriter.write(mfccSpacePlusString);
			filewriter.close();
			// 単語IDに対応した一覧ファイルがなければ作る。
		}else{
			FileWriter filewriter = new FileWriter(mfccSpacePluslistFile);

			filewriter.write(mfccSpacePlusString);
			filewriter.close();
		}



		// wav to mfcc のリスト
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
			// 単語IDに対応した一覧ファイルがなければ作る。
		}else{
			FileWriter filewriter = new FileWriter(wavToMfccFile);

			filewriter.write(string);
			filewriter.close();
		}
	}



	/**
	 * 実行
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
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				inFileReader.close();
				brBufferedReader.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}


	}
}
