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

public class HashFromOffline {

	private HashMap<Integer, String> map = new HashMap<Integer, String>(); // 内部で持つhashテーブル

	
	
	
	/**
	 * コンストラクタ
	 * 予め保存してあるHashテーブルを読み込む
	 */
	private HashFromOffline() {
		try {
			FileInputStream inFile = new FileInputStream("Hashmap.map");
			ObjectInputStream in = new ObjectInputStream(inFile);
			map = (HashMap<Integer, String>) in.readObject();
			in.close();
			inFile.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * パラメータ付きコンストラクタ
	 * param inputfile 入力ファイル
	 * テキストファイルからhashテーブルを作り保存する
	 */
	private HashFromOffline(String inputfile)  {

		BufferedReader br = null;
		// hashmapをオブジェクトとして保存
		ObjectOutputStream out = null;

		try {
			br = new BufferedReader(new FileReader(inputfile));
			// hashMapを作る
			map = CreateHashMap(br);
			out = new ObjectOutputStream(new FileOutputStream("Hashmap.map"));
			out.writeObject(map);

		} catch (FileNotFoundException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (IOException e2) {
			// TODO 自動生成された catch ブロック
			e2.printStackTrace();
		} finally {
			try {
				out.close();
				br.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}



	private HashMap<Integer, String> CreateHashMap(BufferedReader br) {

		HashMap<Integer, String> ret = new HashMap<Integer, String>();

		try {															// (intval2ipu0 size n_ipu := 40497まで)
			while (!br.readLine().split(" ")[0].equals("intval2ipu"));	// 必要な行まで読み飛ばし

			String line = null;
			while ((line = br.readLine()) != null) {
				
				// 0703-0477 23040,23042 ←この形式のテキスト
				String[] lineSplit = line.split("[ ,]");	//" "と","で文をsplit

				// 
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

				//System.out.println(lineSplit[0] + ","  + lineSplit[1] + ","+ lineSplit[2]);

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
	 * Hashmap.mapから読み込み、syllmatchに対応したipuとsyllstartが取得できるのでこれをset
	 * ipuArraylistは参照渡しなので、それのsetメソッドを読み出してsetすればOK
	 * @param ipuArraylist
	 */
	public static void setFrom_hash(ArrayList<Ipu> ipuArraylist){

		// 未実装：ここで.mapファイルが存在しているか確認してどっちのコンストラクタを呼ぶか分岐させること
		
		// コンストラクタからHashmap.mapを読み込む
		HashFromOffline hash = new HashFromOffline();
		String[] hogeStrings;

		// "syllmatch"から"ipu,start"が得られるので、","でsplitしてipuObjectのセッターに投げる
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
		HashFromOffline hogeFromOffline = new HashFromOffline("offline.info");
	}

	

}
