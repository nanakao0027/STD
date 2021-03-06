package preproc;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class FrameSearch {

	// joutがあるディレクトリ名
	private String joutString;


	private FileReader inipuFileReader = null;
	private BufferedReader bripuBufferedReader = null;
	private Boolean failed=false;



	public FrameSearch(String joutString) {
		// TODO 自動生成されたコンストラクター・スタブ

		this.joutString = joutString;
	}

	/**
	 * matched_syllから切り抜くべき音声のフレーム番号を返します
	 * @param ipuname
	 * @param start
	 * @param match
	 * @param end
	 * @return matchFrame,endFrame
	 */
	public String FrameSearchFromMatchedSyll(String oldipu, String ipuname, String start, String match, String end) {

		String retString = null;
		String hogeString = null;

		StringBuilder retBuilder = new StringBuilder();
		StringBuilder tempBuilder = new StringBuilder();

		try {
			String ipunumber = ipuname.split("_")[0];

			if(!ipunumber.equals(oldipu.split("_")[0])) {	// XX-YYが一致してないので新しくファイルを開く	異なるファイル

				failed=false; // ipuが変わったのでfailedをfalseにリセット

				//System.out.println("X");
				inipuFileReader = new FileReader(joutString + "/" + ipunumber + ".jout");
				//inipuFileReader = new FileReader("/home/data/SDPWS/matched_syll/" + ipunumber + ".jout");
				bripuBufferedReader = new BufferedReader(inipuFileReader);

				// String string = ipuname + ".wav";
				tempBuilder.append(ipuname);
				tempBuilder.append(".wav");

				//System.out.println("in");
				while (!bripuBufferedReader.readLine().endsWith(tempBuilder.toString()));
				//tempBuilder.setLength(0);

				// 必要な行まで読み飛ばし
				bripuBufferedReader.mark(10240);
				while (!(hogeString=bripuBufferedReader.readLine()).startsWith(" ----------------------------------------")) {
					// 認識が失敗している場合があるので0を返す
					if(hogeString.startsWith("<search failed>")){
						failed=true;
						bripuBufferedReader.reset(); //~~~.wavのところまでbufferをもどす
						return "99999,99999";
					}
				}

				//ystem.out.println("out");
				bripuBufferedReader.mark(10240);

			} else {	// XX-YYは一致してた　→　同ファイル内の違うipu
				if(!ipuname.equals(oldipu)) {	//XX-YY_ZZZZが異なってたら
					//System.out.println("Y");

					failed=false; // ipuが変わったのでfailedをfalseにリセット

					// String string = ipuname + ".wav";
					tempBuilder.append(ipuname);
					tempBuilder.append(".wav");

					while (!bripuBufferedReader.readLine().endsWith(tempBuilder.toString()));
					//tempBuilder.setLength(0);

					// 必要な行まで読み飛ばし
					bripuBufferedReader.mark(10240);
					while (!(hogeString=bripuBufferedReader.readLine()).startsWith(" ----------------------------------------")) {
						// 認識が失敗している場合があるので0を返す
						if(hogeString.startsWith("<search failed>")){
							failed=true;
							bripuBufferedReader.reset(); //~~~.wavのところまでbufferをもどす
							return "99999,99999";
						}
					}
					bripuBufferedReader.mark(10240);
				} else {	// XX-YY_ZZZZまで一致　→　同ファイル内、同ipu
					//System.out.println("Z");
					bripuBufferedReader.reset();

					if(failed == true)
						return "99999,99999";
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

			if(failed == true){
				retBuilder.append(0);
				retBuilder.append(",");
				retBuilder.append(0);
				failed = false;
			} else {
				retBuilder.append(matchFrame);
				retBuilder.append(",");
				retBuilder.append(endFrame);
			}

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

}
