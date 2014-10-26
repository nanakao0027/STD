package preproc;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FrameSearch {

	private static FileReader inipuFileReader = null;
	private static BufferedReader bripuBufferedReader = null;



	public FrameSearch() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * matched_syllから切り抜くべき音声のフレーム番号を返します
	 * @param ipuname
	 * @param start
	 * @param match
	 * @param end
	 * @return matchFrame,endFrame
	 */
	private String FrameSearchFromMatchedSyll(String oldipu, String ipuname, String start, String match, String end) {

		String retString = null;
		StringBuilder retBuilder = new StringBuilder();
		StringBuilder tempBuilder = new StringBuilder();

		try {
			String ipunumber = ipuname.split("_")[0];

			if(!ipunumber.equals(oldipu.split("_")[0])) {	// XX-YYが一致してないので新しくファイルを開く	異なるファイル
				//System.out.println("X");
				inipuFileReader = new FileReader("/Users/takada/workspace/SDPWS/matched_syll/" + ipunumber + ".jout");
				//inipuFileReader = new FileReader("/home/data/SDPWS/matched_syll/" + ipunumber + ".jout");
				bripuBufferedReader = new BufferedReader(inipuFileReader);

				// String string = ipuname + ".wav";
				tempBuilder.append(ipuname);
				tempBuilder.append(".wav");

				//System.out.println("in");
				while (!bripuBufferedReader.readLine().endsWith(tempBuilder.toString()));
				//tempBuilder.setLength(0);
				while (!bripuBufferedReader.readLine().startsWith(" ----------------------------------------"));	// 必要な行まで読み飛ばし
				//ystem.out.println("out");
				bripuBufferedReader.mark(10240);

			} else {	// XX-YYは一致してた　→　同ファイル内の違うipu
				if(!ipuname.equals(oldipu)) {	//XX-YY_ZZZZが異なってたら
					//System.out.println("Y");

					// String string = ipuname + ".wav";
					tempBuilder.append(ipuname);
					tempBuilder.append(".wav");

					while (!bripuBufferedReader.readLine().endsWith(tempBuilder.toString()));
					//tempBuilder.setLength(0);
					while (!bripuBufferedReader.readLine().startsWith(" ----------------------------------------"));	// 必要な行まで読み飛ばし
					bripuBufferedReader.mark(10240);
				} else {	// XX-YY_ZZZZまで一致　→　同ファイル内、同ipu
					//System.out.println("Z");
					bripuBufferedReader.reset();
				}

			}


			//			String line = null;
			//			while ((line = br.readLine()) != "=== end forced alignment ===") {
			//				//System.out.println(line);
			//				String[] lineSplit = line.split("[ ,]");	//" "と","で文をsplit
			//			}


			//要確認
			String matchFrame = null;
			String endFrame = null;
			String temp = null;

			Integer intMatch = Integer.valueOf(match);
			Integer intStart = Integer.valueOf(start);
			Integer intEnd = Integer.valueOf(end);

			// match - start 回数分だけ読み飛ばし
			for(int i=0; i < intMatch - intStart; i++)
				bripuBufferedReader.readLine();

			//matchFrame = bripuBufferedReader.readLine().substring(1,5); // 1回以上続く空白でsplit。前側の数字をmatchFrameにする
			temp = bripuBufferedReader.readLine(); // 1回以上続く空白でsplit。前側の数字をmatchFrameにする
			matchFrame = temp.substring(1,5).trim();

			// 上で一個分進んでるので end - match - 1回分読み飛ばす
			for(int i=1; i < intEnd - intMatch; i++)
				bripuBufferedReader.readLine();

			// intEnd == intMatch なら、新しく行を読み込まない
			if(intEnd == intMatch) {
				endFrame = temp.substring(6,10).trim();
			} else {
				endFrame = bripuBufferedReader.readLine().substring(6,10);
				endFrame = endFrame.trim();
			}


			retBuilder.append(matchFrame);
			retBuilder.append(",");
			retBuilder.append(endFrame);

			retString = retBuilder.toString();
			//retBuilder.setLength(0);


		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return retString;

	}


	public static void setFrom_jout(ArrayList<Ipu> ipuArraylist) {
		// TODO 自動生成されたメソッド・スタブ



		FrameSearch frameSearch = new FrameSearch();

		String ipuString = null;
		String startString = null;
		String matchString = null;
		String endString = null;
		String result[] = null;		// matchFrame + endFrame

		String oldipu = "XX-YY_ZZZZ";	// ダミー

		Ipu tempIpu = new Ipu();
		for(int i=0; i<ipuArraylist.size(); i++){

			tempIpu = ipuArraylist.get(i);
			ipuString = tempIpu.get_ipu();
			startString = tempIpu.get_syllStart();
			matchString = tempIpu.get_syllMatch();
			endString = tempIpu.get_syllEnd();
			result = null;		// matchFrame + endFrame

			//System.out.println(hoge[0]);
			System.out.println(ipuString + "," +  startString + "," +  matchString + "," +  endString);


			result = frameSearch.FrameSearchFromMatchedSyll(oldipu, ipuString, startString, matchString, endString).split(",");


			tempIpu.set_frameStart_frameEnd(result[0], result[1]);

			// ここ
			//out.write(IDString + "," + ipuString + "," + result + "¥n");

			oldipu = ipuString;

		}

		try {
			inipuFileReader.close();
			bripuBufferedReader.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
