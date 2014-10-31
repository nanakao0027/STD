package preproc;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.*;


public class MakeWaveFile {

	// ディレクトリの指定などに使う
	private String TARGET_STRING;


	// wavファイルが入ったディレクトリのルート。あとでipu(07-01とか)を末尾にくっつけてディレクトリ指定
	public String ROOTDICSTRING_STRING;

	// 出力ファイルのルートディレクトリを指定するための文字列。このあとにTARGET_STRINGが続く
	public String OUTPUTROOTDICSTRING_STRING;


	public MakeWaveFile(String targetString, String rootString, String outString) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.TARGET_STRING = targetString;
		this.ROOTDICSTRING_STRING = rootString;
		this.OUTPUTROOTDICSTRING_STRING = outString;
	}


	public void createWav(String ID, String ipu, String start, String end)  {

		StringBuilder tempBuilder = new StringBuilder();


		//if(end.equals("pute") || start.equals("pute")) {
			//System.out.println(ID + " , " + ipu + " , " + start + " , " + end);
			//return;
		//}

		// FrameSearchでエラー処理したipuをパスする
		// listもつくられない
		if(start.equals("99999") && end.equals("99999"))
			return;


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
		tempBuilder.append("/");
		tempBuilder.append(ipu.substring(0,5));		// XX-YY まで
		tempBuilder.append("/");					//
		tempBuilder.append(ipu);					// XX-YY/XX-YY_ZZZZ
		tempBuilder.append(".wav");					// XX-YY/XX-YY_ZZZZ.wav
		String audioFilePathString = tempBuilder.toString();
		tempBuilder.setLength(0);

		System.out.println(audioFilePathString);




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
			tempBuilder.append("/");
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
				if(in != null)
					in.close();
				if(ais != null)
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


	private void ListMake(String ID, String ipu, String start, String end) throws IOException {

		// StringBuilder tempBuilder = new StringBuilder();


		// Mfccのlist
		// 出力先
		File mfccDirectory = new File(OUTPUTROOTDICSTRING_STRING + "/" + TARGET_STRING + "/mfcclist");
		File mfccFile = new File(mfccDirectory, ID + ".txt");

		// 書き込む内容　mfccのPATH
		String mfcc = OUTPUTROOTDICSTRING_STRING + "/" + TARGET_STRING + "/mfcc/";
//		tempBuilder.append(mfcc);
//		tempBuilder.append(ID);
//		tempBuilder.append("_");
//		tempBuilder.append(ipu.substring(0,10));
//		tempBuilder.append("_");
//		tempBuilder.append(start);
//		tempBuilder.append("_");
//		tempBuilder.append(end);
//		tempBuilder.append(".mfc");

		String mfccString = mfcc + ID + "_" + ipu.substring(0,10) + "_" + start + "_" + end + ".mfc";

		// tempBuilder.append(System.getProperty("line.separator"));
		// tempBuilder.append("\n");
//		String mfccString =  tempBuilder.toString();
//		tempBuilder.setLength(0);

		if(mfccFile.exists()){
			FileWriter filewriter = new FileWriter(mfccFile, true);
			BufferedWriter bfBufferedWriter = new BufferedWriter(filewriter);

			bfBufferedWriter.write(mfccString);
			bfBufferedWriter.newLine();
			bfBufferedWriter.close();
			filewriter.close();
			// 単語IDに対応した一覧ファイルがなければ作る。
		}else{
			FileWriter filewriter = new FileWriter(mfccFile);
			BufferedWriter bfBufferedWriter = new BufferedWriter(filewriter);

			bfBufferedWriter.write(mfccString);
			bfBufferedWriter.newLine();
			bfBufferedWriter.close();
			filewriter.close();
		}



		// wav to mfcc のリスト
		File wavToMfccDirectory = new File(OUTPUTROOTDICSTRING_STRING + "/" + TARGET_STRING + "/wavToMfcc");
		File wavToMfccFile = new File(wavToMfccDirectory, ID + ".txt");
//		tempBuilder.append(OUTPUTROOTDICSTRING_STRING);
//		tempBuilder.append("/");
//		tempBuilder.append(TARGET_STRING);
//		tempBuilder.append("/wavDirectory/");
		String wavDirectoryPATHString = OUTPUTROOTDICSTRING_STRING + "/" + TARGET_STRING + "/wavDirectory/";
//		tempBuilder.setLength(0);


//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append(wavDirectoryPATHString);
//		stringBuilder.append(ID);
//		stringBuilder.append("_");
//		stringBuilder.append(ipu.substring(0,10));
//		stringBuilder.append("_");
//		stringBuilder.append(start);
//		stringBuilder.append("_");
//		stringBuilder.append(end);
//		stringBuilder.append(".wav");
//		stringBuilder.append("	");
//		stringBuilder.append(mfccString);
		//wavtomfcc
//		String string = stringBuilder.toString();
		String string = wavDirectoryPATHString + ID + "_" + ipu.substring(0,10) + "_" + start + "_" + end + ".wav	" + mfccString;
		if(wavToMfccFile.exists()){
			FileWriter filewriter = new FileWriter(wavToMfccFile, true);
			BufferedWriter bfBufferedWriter = new BufferedWriter(filewriter);

			bfBufferedWriter.write(string);
			bfBufferedWriter.newLine();
			bfBufferedWriter.close();
			filewriter.close();
			// 単語IDに対応した一覧ファイルがなければ作る。
		}else{
			FileWriter filewriter = new FileWriter(wavToMfccFile);
			BufferedWriter bfBufferedWriter = new BufferedWriter(filewriter);

			bfBufferedWriter.write(string);
			bfBufferedWriter.newLine();
			bfBufferedWriter.close();
			filewriter.close();
		}
	}






	public static void main(String[] args) {

		FileReader inFileReader = null;
		BufferedReader brBufferedReader = null;

		String ROOTDICSTRING_STRING = "../Data/SDPWS_speech";
		String OUTPUTROOTDICSTRING_STRING = "./wavOutput-root";

		String target = "NTCIR11_best1_229_min000_threshold0.00.detect";
		String TARGET_STRING = target;
		try {
			inFileReader = new FileReader(new File("./preproc_Result", "05_Result" + target));
			brBufferedReader = new BufferedReader(inFileReader);
			// System.out.println("Processing:" + inFileReader.toString());

			String hoge[] = null;
			String buf = null;
			MakeWaveFile hogeMakeWaveFile = new MakeWaveFile(TARGET_STRING, ROOTDICSTRING_STRING, OUTPUTROOTDICSTRING_STRING);
			buf = brBufferedReader.readLine();

			while(buf != null) {
				hoge = buf.split(",");

				// デバッグ
				// if(Integer.valueOf(hoge[0]) > 68)
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
