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

	private String TARGET_NAME;		// 入力ファイル
	private String DATA_DIRECTORY;	// 入力ファイルのディレクトリ
	private String RESULT_DIRECTORY;	// 結果を出力するディレクトリ
	private String OFFLINE_NAME;		// offline.infoの名前
	private String SDPWS_SPEECH_DIRECTORY;	// SDPWSの音声データが置いてあるディレクトリ
	private String SDPWS_MATCHED_SYLL_DIRECTORY;	// SDPWSのjoutが置いてあるディレクトリ
	private String WAV_LIST_DIRECTORY;	// SDPWSのjoutが置いてあるディレクトリ



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

			this.TARGET_NAME = br.readLine();
			this.DATA_DIRECTORY = br.readLine();
			this.RESULT_DIRECTORY = br.readLine();
			this.OFFLINE_NAME = br.readLine();
			this.SDPWS_SPEECH_DIRECTORY = br.readLine();
			this.SDPWS_MATCHED_SYLL_DIRECTORY = br.readLine();
			this.WAV_LIST_DIRECTORY = br.readLine();


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

		String outString;

		try {
			outFileWriter = new FileWriter(new File("Temp", stepString + ".temp"));

			for(Ipu ipu : ipuArraylist){

				outString = ipu.get_ID() + "_" + ipu.get_ipu() + System.getProperty("line.separator");
				outFileWriter.write(outString);

//				StringBuilder stringBuilder = new StringBuilder();
//				stringBuilder.append(ipu.get_ID());
//				stringBuilder.append("_");
//				stringBuilder.append(ipu.get_ipu());
//				stringBuilder.append("\n");
//				outFileWriter.write(stringBuilder.toString());
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
	private void ouputText_ID_ipu_startFrame_outFrame_DPscore(ArrayList<Ipu> ipuArraylist) {


		FileWriter outFileWriter = null;

		try {
			outFileWriter = new FileWriter(new File(this.RESULT_DIRECTORY, "05_Result" + this.TARGET_NAME));

			String hogeString[];
			String outString;

			for(Ipu ipu : ipuArraylist){

				//           0  1     2         3         4         5         6       7
				// get_allで id,ipu,syllstart,syllmatch,syllend,framestart,frameend,DPscoreがとってこれる
				hogeString = ipu.get_all_withDelimiter(",").split(",");
				outString = hogeString[0] + "," + hogeString[1] + "," + hogeString[5] + "," + hogeString[6] + "," + hogeString[7] + "," + System.getProperty("line.separator");
				outFileWriter.write(outString);

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
	 * hashfrom~を実行する
	 * @param ipuArraylist
	 * @param hashFromOffline
	 */
	private void exe_hash(ArrayList<Ipu> ipuArraylist, HashFromOffline hashFromOffline) {
		// コンストラクタを呼び出して"offline.info"から"Hashmap.map"を作り読み込む、既に存在していたらそれを読み込む。
		// HashFromOffline hash = new HashFromOffline(directoryString, offlineString);

		// "syllmatch"から"ipu,start"が得られるので、","でsplitしてipuObjectのセッターに投げる
		String[] hogeStrings;
		for(Ipu ipuObject : ipuArraylist){
			hogeStrings = hashFromOffline.Hashfunc(Integer.valueOf(ipuObject.get_syllMatch())).split(",");
			ipuObject.set_ipu_syllStart(hogeStrings[0], hogeStrings[1]);
		}
	}






	/**
	 * FrameSeaechを実行する
	 * @param ipuArraylist
	 * @param frameSearch
	 */
	private void exe_FrameSearch(ArrayList<Ipu> ipuArraylist, FrameSearch frameSearch) {


		String ipuString = null;
		String startString = null;
		String matchString = null;
		String endString = null;
		String result[] = null;		// matchFrame + endFrame

		String oldipuString = "XX-YY_ZZZZ";	// ダミー

		Ipu tempIpu = new Ipu();
		for(int i=0; i<ipuArraylist.size(); i++){

			// Arraylist内のipuがこれで参照できるし、結果も格納できる
			tempIpu = ipuArraylist.get(i);
			ipuString = tempIpu.get_ipu();
			startString = tempIpu.get_syllStart();
			matchString = tempIpu.get_syllMatch();
			endString = tempIpu.get_syllEnd();
			result = null;		// matchFrame + endFrame

			//System.out.println(hoge[0]);
			System.out.println(ipuString + "," +  startString + "," +  matchString + "," +  endString);

			// joutからstartFrameとendFrameを取得する
			result = frameSearch.FrameSearchFromMatchedSyll(oldipuString, ipuString, startString, matchString, endString).split(",");

			// 結果を格納
			tempIpu.set_frameStart_frameEnd(result[0], result[1]);

			// oldipuを更新
			oldipuString = ipuString;

		}
	}






	/**
	 * 実行
	 * @param ipuArraylist
	 */
	public void exe_wavCut(ArrayList<Ipu> ipuArraylist, MakeWaveFile makeWaveFile) {


		for(Ipu ipu : ipuArraylist){
			makeWaveFile.createWav(ipu.get_ID(), ipu.get_ipu(), ipu.get_frameStart(), ipu.get_frameEnd());
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
		ipuArraylist = stdDexecution.setFrom_onlyDetect(stdDexecution.TARGET_NAME, stdDexecution.DATA_DIRECTORY);

		// hashMAPを読み込んで、ipu,startを取得
		System.out.println("step " + steps++ + " : Transform match-end number to ipu using Hash");
		HashFromOffline hashFromOffline = new HashFromOffline(stdDexecution.DATA_DIRECTORY, stdDexecution.OFFLINE_NAME);
		stdDexecution.exe_hash(ipuArraylist, hashFromOffline);


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
		FrameSearch frameSearch = new FrameSearch(stdDexecution.SDPWS_MATCHED_SYLL_DIRECTORY);
		stdDexecution.exe_FrameSearch(ipuArraylist, frameSearch);


		// ここでひとまず結果をテキスト出力（後のスコア正規化で使用）
		System.out.println("step " + steps++ + " : extout");
		Collections.sort(ipuArraylist, new IdComparator());	//ID昇順
		stdDexecution.ouputText_ID_ipu_startFrame_outFrame_DPscore(ipuArraylist);
		//

		// wavファイルを読み込んで、切り抜いてディレクトリに出力
		System.out.println("step " + steps++ + " : output_wavfile");
		MakeWaveFile makeWaveFile = new MakeWaveFile(stdDexecution.TARGET_NAME, stdDexecution.SDPWS_SPEECH_DIRECTORY, stdDexecution.WAV_LIST_DIRECTORY);
		stdDexecution.exe_wavCut(ipuArraylist, makeWaveFile);



		// デバッグ用
		System.out.println(stdDexecution.TARGET_NAME);
		for(Ipu ipuObject : ipuArraylist){
			System.out.println(ipuObject.get_all_withDelimiter(","));
		}
		System.out.println("Num of canditates : " + ipuArraylist.size());

	}

}
