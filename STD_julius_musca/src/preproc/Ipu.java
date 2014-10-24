package preproc;
import java.util.Comparator;

public class Ipu {
	
	private String IDString;
	private String ipuString;
	private String syllStartString;
	private String syllMatchString;
	private String syllEndString;
	private String frameStartString;
	private String frameEndString;
	private String dpScoreString;
	//private String amScoreString;

	
	/**
	 * �R���X�g���N�^ onlySDPWSdetect����ǂݍ��ނƂ��Ɏg�p����
	 * @param IDString
	 * @param matchString
	 * @param endString
	 * @param dpString
	 */
	Ipu(String IDString,String matchString, String endString, String dpString){
		this.IDString = IDString;
		this.syllMatchString = matchString;
		this.syllEndString = endString;
		this.dpScoreString = dpString;
	}
	
	public Ipu() {
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	/**
	 * ipu��syll��syllStart���͗p�Bhash����ǂݍ��ނƂ��Ɏg���Ǝv���B
	 * @param ipuString
	 * @param startString
	 */
	public void set_ipu_syllStart(String ipuString, String startString) {
		this.ipuString = ipuString;
		this.syllStartString = startString;
	}
	
	/**
	 * frameStart��frameEnd���͗p�B.jout����ǂݍ��ނƂ��Ɏg���Ǝv���B
	 * @param startString
	 * @param endString
	 */
	public void set_frameStart_frameEnd(String startString, String endString) {
		this.frameStartString = startString;
		this.frameEndString = endString;
	}
	
	// �ȍ~�A�t�B�[���h�擾�p
	public String get_ID() {
		return IDString;
	}
	public String get_ipu() {
		return ipuString;
	}
	public String get_syllStart() {
		return syllStartString;
	}
	public String get_syllMatch() {
		return syllMatchString;
	}
	public String get_syllEnd() {
		return syllEndString;
	}
	public String get_frameStart() {
		return frameStartString;
	}
	public String get_frameEnd() {
		return frameEndString;
	}
	public String get_dpScore() {
		return dpScoreString;
	}
	public String get_all_withDelimiter(String delimiter) {
		StringBuilder buf = new StringBuilder();
		buf.append(get_ID());
		buf.append(delimiter);
		
		buf.append(get_ipu());
		buf.append(delimiter);
		
		buf.append(get_syllStart());
		buf.append(delimiter);
		
		buf.append(get_syllMatch());
		buf.append(delimiter);
		
		buf.append(get_syllEnd());
		buf.append(delimiter);
		
		buf.append(get_frameStart());
		buf.append(delimiter);
		
		buf.append(get_frameEnd());
		buf.append(delimiter);
		
		buf.append(get_dpScore());
		return buf.toString();
	}
	
}


/**
 * �\�[�g�̍ۂɎg�p����Bipu�\�[�g�p
 * @author takada
 *
 */
class IpuComparator implements Comparator<Ipu> {

	@Override
	public int compare(Ipu fs1, Ipu fs2) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return fs1.get_ipu().compareTo(fs2.get_ipu());
	}

}

/**
 * �\�[�g�̍ۂɎg�p����BID�\�[�g�p
 * @author takada
 *
 */
class IdComparator implements Comparator<Ipu> {

	@Override
	public int compare(Ipu fs1, Ipu fs2) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return fs1.get_ID().compareTo(fs2.get_ID());
	}

}




