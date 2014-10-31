package preproc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * DTWの結果ファイル(.detect or threshold0.XX)をjuliusに渡すための音声ファイルリストに変換する
 *
 * 構成
 * ルートディレクトリ ------ 実行ファイル(.class? .sh?)
 *                　 |-- 設定ファイル(preprocExe.conf)
 *                　 |-- データディレクトリ------- .detectファイル
 *                                　      |--- offline.info
 *
 * STDexecution.confの書き方
 * 1行目:.detectファイルを指定
 * 2行目:.detectファイルがあるディレクトリを指定
 */
public class Execution {

	public static String TARGET_NAME;		// 入力ファイル
	public static String DATA_DIRECTORY;	// 入力ファイルのディレクトリ
	public static String RESULT_DIRECTORY;	// 結果を出力するディレクトリ
	public static String OFFLINE_NAME;		// offline.infoの名前
	public static String SDPWS_SPEECH_DIRECTORY;	// SDPWSの音声データが置いてあるディレクトリ
	public static String SDPWS_MATCHED_SYLL_DIRECTORY;	// SDPWSのjoutが置いてあるディレクトリ
	public static String WAV_LIST_DIRECTORY;	// SDPWSのjoutが置いてあるディレクトリ


	/**
	 * コンストラクタ
	 * 設定ファイルの読み込み
	 */
	public Execution(String inputfilename) {


		// 設定ファイルの読み込み
		FileReader in = null;
		BufferedReader br = null;

		try {
			in = new FileReader(inputfilename);
			br = new BufferedReader(in);

			TARGET_NAME = br.readLine();
			DATA_DIRECTORY = br.readLine();
			RESULT_DIRECTORY = br.readLine();
			OFFLINE_NAME = br.readLine();
			SDPWS_SPEECH_DIRECTORY = br.readLine();
			SDPWS_MATCHED_SYLL_DIRECTORY = br.readLine();
			WAV_LIST_DIRECTORY = br.readLine();


		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				in.close();
				br.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}






	/**
	 * onlyDetectファイルから読み込んでipuをリスト化する
	 * @param inputfilename
	 * @return
	 */
	private ArrayList<Ipu> setFrom_onlyDetect(String inputfilename, String targetDirectory) {

		// inputfilename（多分thresholdとか）をtargetnameに指定する。
		//set_targetName(inputfilename);

		ArrayList<Ipu> ipuArrayList = new ArrayList<Ipu>();
		BufferedReader inputBufferedReader = null;
		File directory = new File(targetDirectory);
		File file = new File(directory, inputfilename);

		try {
			inputBufferedReader = new BufferedReader(new FileReader(file));

			String fileNumber = null;
			String[] hogeString = null;
			String str = inputBufferedReader.readLine();
			while(str != null){
				if(str.split(",")[0].matches("[0-9][0-9][0-9]")) {
					fileNumber = str.split(",")[0];			// ID
				}
				else if(str.split(" ")[0].matches("total_processing_time:="))
					break; // 終了
				else if(str.split(" ")[0].matches("processing_time:="))
					;//飛ばす
				else {
					hogeString = str.split(" ");
					//							ID			match			end				DPScore
					ipuArrayList.add(new Ipu(fileNumber, hogeString[1], hogeString[3], hogeString[5]));
					//out.write(fileNumber + "_" + str.split(" ")[1] + "_" + str.split(" ")[3] + "\n");
				}
				str = inputBufferedReader.readLine();
			}

		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				inputBufferedReader.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		return ipuArrayList;
	}



	/**
	 * IDとipuをテキストにアウト
	 * @param ipuArraylist
	 * @param stepString
	 */
	private void outputText_ipu(ArrayList<Ipu> ipuArraylist, String stepString) {


		FileWriter outFileWriter = null;

		try {
			outFileWriter = new FileWriter(new File("Temp", stepString + ".temp"));

			for(Ipu ipu : ipuArraylist){
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(ipu.get_ID());
				stringBuilder.append("_");
				stringBuilder.append(ipu.get_ipu());
				stringBuilder.append("\n");
				outFileWriter.write(stringBuilder.toString());
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				outFileWriter.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}




	/**
	 * IDとipuとstartFrameとoutFrameとDPscoreをテキストにアウト
	 * @param ipuArraylist
	 * @param stepString
	 */
	private void ouputText_ID_ipu_startFrame_outFrame_DPscore(ArrayList<Ipu> ipuArraylist, String targetString) {


		FileWriter outFileWriter = null;

		try {
			outFileWriter = new FileWriter(new File(RESULT_DIRECTORY, targetString));

			for(Ipu ipu : ipuArraylist){
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(ipu.get_ID());
				stringBuilder.append(",");
				stringBuilder.append(ipu.get_ipu());
				stringBuilder.append(",");
				stringBuilder.append(ipu.get_frameStart());
				stringBuilder.append(",");
				stringBuilder.append(ipu.get_frameEnd());
				stringBuilder.append(",");
				stringBuilder.append(ipu.get_dpScore());
				stringBuilder.append("\n");
				outFileWriter.write(stringBuilder.toString());
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				outFileWriter.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}




	/**
	 * @param args[0]
	 */
	public static void main(String[] args) {

		// .conf が指定されてなかったらエラー出して終了
		if(args.length==0) {
			System.out.println("Please assign .conf file");
			return;
		}

		int steps=0;
		String confString = args[0];

		// 設定ファイルの読み込み、ArrayList初期化
		System.out.println("step " + steps++ + " : Use " + confString);
		Execution stdDexecution = new Execution(confString);
		ArrayList<Ipu> ipuArraylist = new ArrayList<Ipu>();

		// targetFileから読み込み、ID,match,end,DPscoreを取得
		System.out.println("step " + steps++ + " : ipuArrayList setup from Detect file");
		ipuArraylist = stdDexecution.setFrom_onlyDetect(TARGET_NAME, DATA_DIRECTORY);

		// hashMAPを読み込んで、ipu,startを取得
		System.out.println("step " + steps++ + " : Transform match-end number to ipu using Hash");
		HashFromOffline.setFrom_hash(ipuArraylist, DATA_DIRECTORY, OFFLINE_NAME);

		// ここでsyllReject機能を実装する予定
		// match==endならArraylistから除外する、など

		// ipu順でソート
		System.out.println("step " + steps++ + " : Sorting in ipu");
	    Collections.sort(ipuArraylist, new IpuComparator());

		// ipuをテキストとして出力
		System.out.println("step " + steps++ + " : ipu output to ./Temp");
		stdDexecution.outputText_ipu(ipuArraylist, "step" + steps);

		// ipuを一意にして出力
		//System.out.println("step5 : Uniq ipu output to ./" + RESULT_DIRECTORY);
		//stdDexecution.trasToUniqList(ipuArraylist);

		/**
		 * 言語モデルを使ってjuliusを動かす場合は要らない　2014/10/25
		 */

		// 認識結果(*.jout)からstartFrameとendFrameを取得
		System.out.println("step " + steps++ + " : Get start-end Frames from jout");
		FrameSearch.setFrom_jout(ipuArraylist, SDPWS_MATCHED_SYLL_DIRECTORY);


		// ここでひとまず結果をテキスト出力（後のスコア正規化で使用）
		System.out.println("step " + steps++ + " : extout");
		Collections.sort(ipuArraylist, new IdComparator());	//ID昇順
		stdDexecution.ouputText_ID_ipu_startFrame_outFrame_DPscore(ipuArraylist, "05_Result" + TARGET_NAME);
		//

		// wavファイルを読み込んで、切り抜いてディレクトリに出力
		System.out.println("step " + steps++ + " : output_wavfile");
		MakeWaveFile.execute_wavCut(ipuArraylist, TARGET_NAME, SDPWS_SPEECH_DIRECTORY, WAV_LIST_DIRECTORY);



		// デバッグ用
		System.out.println(TARGET_NAME);
		for(Ipu ipuObject : ipuArraylist){
			System.out.println(ipuObject.get_all_withDelimiter(","));
		}
		System.out.println("Num of canditates : " + ipuArraylist.size());


	}
}
