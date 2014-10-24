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
 * 設定ファイル：STDexecution.conf
 *
 */
public class STDexecution {

	public static String TARGET_NAME;

	public static String targetDirectory;


	public STDexecution() {
		// TODO 自動生成されたコンストラクター・スタブ
		// commit push test


		// 設定ファイルの読み込み
		FileReader in = null;
		BufferedReader br = null;

		try {
			in = new FileReader("STDexecution.conf");
			br = new BufferedReader(in);

			TARGET_NAME = br.readLine();
			targetDirectory = br.readLine();




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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		STDexecution stdDexecution = new STDexecution();
		ArrayList<Ipu> ipuArraylist = new ArrayList<Ipu>();

		// targetFileから読み込み、ID,match,end,DPscoreを取得
		System.out.println("step1:setFrom_onlyDetect");
		ipuArraylist = stdDexecution.setFrom_onlyDetect(TARGET_NAME, targetDirectory);

		// hashMAPを読み込んで、ipu,startを取得
		System.out.println("step2:setFrom_hash");
		HashFromOffline.setFrom_hash(ipuArraylist);

		// ここで
		// syllReject機能を実装する予定
		// match==endならArraylistから除外する、など

		// ipu順でソート
		System.out.println("step3:sort in ipu");
		Collections.sort(ipuArraylist, new IpuComparator());

		// 認識結果(*.jout)からstartFrameとendFrameを取得
		System.out.println("step4:setFrom_jout");
		FrameSearch.setFrom_jout(ipuArraylist);


		// ここでひとまず結果をテキスト出力（後のスコア正規化で使用）
		System.out.println("step5:textout");
		Collections.sort(ipuArraylist, new IdComparator());	//ID昇順
		stdDexecution.ouputText_ID_ipu_startFrame_outFrame_DPscore(ipuArraylist);
		//

		// wavファイルを読み込んで、切り抜いてディレクトリに出力
		System.out.println("step6:output_wavfile");
		MakeWaveFile.execute_wavCut(ipuArraylist, TARGET_NAME);



		// デバッグ用
		System.out.println(TARGET_NAME);
		for(Ipu ipuObject : ipuArraylist){
			System.out.println(ipuObject.get_all_withDelimiter(","));
		}
		System.out.println("行数" + ipuArraylist.size());
	}



	/**
	 * 対象の名前をセット（thresholdなどの情報）
	 * @param target
	 */
	private static void set_targetName(String target){
		TARGET_NAME = target;
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
					fileNumber = str.split(",")[0];			// ID取得
				}
				else if(str.split(" ")[0].matches("processing_time:="));
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
	 * Frameが出し終わったところまででtxtに出力
	 * @param ipuArraylist
	 */
	private void ouputText_ID_ipu_startFrame_outFrame_DPscore(ArrayList<Ipu> ipuArraylist) {


		FileWriter outFileWriter = null;

		try {
			outFileWriter = new FileWriter(new File("ID_ipu_startFrame_outFrame_DPscore-" + TARGET_NAME + ".txt"));

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
}
