package preproc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class HashFromOffline {

	private HashMap<Integer, String> map = new HashMap<Integer, String>(); // 内部で持つhashテーブル

	// offline.infoから作成するhashファイル。名前はこれに固定。
	// ？？他のoffline.infoを扱う場合にはまた変更が必要
	private String HASH_NAME; //="Hashmap.map";




	/**
	 * パラメータ付きコンストラクタ
	 * @param offlinefilename 入力ファイル
	 * @param directoryString 入力ファイルのあるディレクトリ
	 * offline.info から hashテーブルを作り保存する
	 */
	HashFromOffline(String directoryString, String offlinefilename) {

		HASH_NAME=offlinefilename.replace("info", "map");

		BufferedReader br = null;
		// hashmapをオブジェクトとして保存
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		FileInputStream inFile = null;
		File offlineFile = new File(directoryString, offlinefilename);
		File hashFile = new File(directoryString, HASH_NAME);
		boolean existBoolean = false;	// Hashmap.mapが存在しているか確認、ストリームを閉じるときにも使う


		try {

			// データディレクトリに HASH_NAMEがあるか確認
			// 　存在する　→　それを読み込む
			// 　存在しない　→　新しく作る
			File existCheckFile = hashFile;
			existBoolean = existCheckFile.exists();
			if(existBoolean){
				System.out.println("Reading " + HASH_NAME);
				// Hashmap.mapをoutputFIleから読み込む
				inFile = new FileInputStream(hashFile);
				in = new ObjectInputStream(inFile);
				map = (HashMap<Integer, String>) in.readObject();
			}
			else {
				System.out.println("Creating new " + HASH_NAME);
				br = new BufferedReader(new FileReader(offlineFile)); //offline.infoを読み込み
				// Hasmmapを作る
				map = CreateHashMap(br);
				// Hasmmapを書き出し
				out = new ObjectOutputStream(new FileOutputStream(hashFile));
				out.writeObject(map);
			}

		} catch (FileNotFoundException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (IOException e2) {
			// TODO 自動生成された catch ブロック
			e2.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				if(existBoolean){
					inFile.close();
					in.close();
				} else {
					out.close();
					br.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}




	/**
	 * HASHMAPを作る
	 * @param br
	 * @return
	 */
	private HashMap<Integer, String> CreateHashMap(BufferedReader br) {

		HashMap<Integer, String> ret = new HashMap<Integer, String>();

		try {	// (intval2ipu0 size n_ipu := 40497まで)
			while (!br.readLine().split(" ")[0].equals("intval2ipu"));	// 必要な行まで読み飛ばし

			String line = null;
			while ((line = br.readLine()) != null) {

				// 0703-0477 23040,23042 ←この形式のテキスト
				String[] lineSplit = line.split("[ ,]");	//" "と","で文をsplit

				// 第2～第3ワードまでの数をkeyに、第1,2ワードを値にしてhashテーブルを作成
				Integer valueOf = Integer.valueOf(lineSplit[1]);
				Integer valueOf2 = Integer.valueOf(lineSplit[2]);
				String ipuNumberString = lineSplit[0];
				ipuNumberString = ipuNumberString.replace("-", "_");
				ipuNumberString = ipuNumberString.substring(0, 2) + "-" + ipuNumberString.substring(2,ipuNumberString.length());
				StringBuilder buffer = new StringBuilder();
				buffer.append(ipuNumberString);
				buffer.append(",");
				buffer.append(lineSplit[1]);
				String buf = buffer.toString();

				//System.out.println(lineSplit[0] + "," + lineSplit[1] + ","+ lineSplit[2]);

				// end~startの間の数字をすべて登録する
				for (int i = valueOf; i <= valueOf2; i++) {
					ret.put(i, buf);	// hashテーブルに登録
				}
			}

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return ret;
	}




	/**
	 * Hash関数
	 * 外からキーが渡されたら値を返す
	 * @param key キー
	 */
	public String Hashfunc(int key) {
		return map.get(key);
	}






	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new HashFromOffline("preprocData", "");
	}
}