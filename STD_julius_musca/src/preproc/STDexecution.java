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
 * �ݒ�t�@�C���FSTDexecution.conf
 *
 */
public class STDexecution {

	public static String TARGET_NAME;

	public static String targetDirectory;


	public STDexecution() {
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		// commit push test


		// �ݒ�t�@�C���̓ǂݍ���
		FileReader in = null;
		BufferedReader br = null;

		try {
			in = new FileReader("STDexecution.conf");
			br = new BufferedReader(in);

			TARGET_NAME = br.readLine();
			targetDirectory = br.readLine();




		} catch (FileNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} finally {
			try {
				in.close();
				br.close();
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

		STDexecution stdDexecution = new STDexecution();
		ArrayList<Ipu> ipuArraylist = new ArrayList<Ipu>();

		// targetFile����ǂݍ��݁AID,match,end,DPscore���擾
		System.out.println("step1:setFrom_onlyDetect");
		ipuArraylist = stdDexecution.setFrom_onlyDetect(TARGET_NAME, targetDirectory);

		// hashMAP��ǂݍ���ŁAipu,start���擾
		System.out.println("step2:setFrom_hash");
		HashFromOffline.setFrom_hash(ipuArraylist);

		// ������
		// syllReject�@�\����������\��
		// match==end�Ȃ�Arraylist���珜�O����A�Ȃ�

		// ipu���Ń\�[�g
		System.out.println("step3:sort in ipu");
		Collections.sort(ipuArraylist, new IpuComparator());

		// �F������(*.jout)����startFrame��endFrame���擾
		System.out.println("step4:setFrom_jout");
		FrameSearch.setFrom_jout(ipuArraylist);


		// �����łЂƂ܂����ʂ��e�L�X�g�o�́i��̃X�R�A���K���Ŏg�p�j
		System.out.println("step5:textout");
		Collections.sort(ipuArraylist, new IdComparator());	//ID����
		stdDexecution.ouputText_ID_ipu_startFrame_outFrame_DPscore(ipuArraylist);
		//

		// wav�t�@�C����ǂݍ���ŁA�؂蔲���ăf�B���N�g���ɏo��
		System.out.println("step6:output_wavfile");
		MakeWaveFile.execute_wavCut(ipuArraylist, TARGET_NAME);



		// �f�o�b�O�p
		System.out.println(TARGET_NAME);
		for(Ipu ipuObject : ipuArraylist){
			System.out.println(ipuObject.get_all_withDelimiter(","));
		}
		System.out.println("�s��" + ipuArraylist.size());
	}



	/**
	 * �Ώۂ̖��O���Z�b�g�ithreshold�Ȃǂ̏��j
	 * @param target
	 */
	private static void set_targetName(String target){
		TARGET_NAME = target;
	}


	/**
	 * onlyDetect�t�@�C������ǂݍ����ipu�����X�g������
	 * @param inputfilename
	 * @return
	 */
	private ArrayList<Ipu> setFrom_onlyDetect(String inputfilename, String targetDirectory) {

		// inputfilename�i����threshold�Ƃ��j��targetname�Ɏw�肷��B
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
					fileNumber = str.split(",")[0];			// ID�擾
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
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} finally {
			try {
				inputBufferedReader.close();
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}

		return ipuArrayList;
	}


	/**
	 * Frame���o���I������Ƃ���܂ł�txt�ɏo��
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
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} finally {
			try {
				outFileWriter.close();
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}
	}
}
