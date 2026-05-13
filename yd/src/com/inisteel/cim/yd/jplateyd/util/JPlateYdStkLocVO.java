/*
 * @(#) 2후판정정야드 TO위치결정 VO 클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/13
 *
 * @description		2후판정정야드 TO위치결정 VO 클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/13   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.util;

public class JPlateYdStkLocVO {

	private String 	ydStkColGp;							//적치열구분
	private String 	ydStkBedNo;							//적치베드번호
	private String 	ydStkLyrNo;							//적치단번호
	private int    	seq;								//조회순서와 우선순위의 합 --> 정렬 시 사용
	private int    	prior;								//우선순위
	private String 	ydStkBedLGp;						//베드길이구분
	private String 	ydStkBedWGp;						//베드폭구분
	private String 	ydStkBedActStat;					//베드활성상태
	private String 	ydStkBedWhioStat;					//베드입출고구분
	private int    	ydStkBedLyrMax;						//베드단Max
	private int    	ydStkBedWtMax;						//베드중량Max
	private double 	ydStkBedHMax;						//베드높이Max
	private int    	ydStkableBedLyr;					//적치가능한 베드단
	private int    	ydStkableBedWt;						//적치가능한 베드중량
	private double 	ydStkableBedH;						//적치가능한 베드높이
	private String 	ydStkLyrActStat;					//단활성상태
	private String 	ydStkLyrMtlStat;					//단재료상태

	private int 	ydBedErrCd;							//베드에 적치가능한 지에 대한 에러코드값 (10000인 경우 적치가능)

	public int getYdBedErrCd() {
		return ydBedErrCd;
	}
	public void setYdBedErrCd(int ydBedErrCd) {
		this.ydBedErrCd = ydBedErrCd;
	}
	public String getYdStkBedLGp() {
		return ydStkBedLGp;
	}
	public void setYdStkBedLGp(String ydStkBedLGp) {
		this.ydStkBedLGp = ydStkBedLGp;
	}
	public String getYdStkBedWGp() {
		return ydStkBedWGp;
	}
	public void setYdStkBedWGp(String ydStkBedWGp) {
		this.ydStkBedWGp = ydStkBedWGp;
	}
	public String getYdStkBedActStat() {
		return ydStkBedActStat;
	}
	public void setYdStkBedActStat(String ydStkBedActStat) {
		this.ydStkBedActStat = ydStkBedActStat;
	}
	public String getYdStkBedWhioStat() {
		return ydStkBedWhioStat;
	}
	public void setYdStkBedWhioStat(String ydStkBedWhioStat) {
		this.ydStkBedWhioStat = ydStkBedWhioStat;
	}
	public int getYdStkBedLyrMax() {
		return ydStkBedLyrMax;
	}
	public void setYdStkBedLyrMax(int ydStkBedLyrMax) {
		this.ydStkBedLyrMax = ydStkBedLyrMax;
	}
	public int getYdStkBedWtMax() {
		return ydStkBedWtMax;
	}
	public void setYdStkBedWtMax(int ydStkBedWtMax) {
		this.ydStkBedWtMax = ydStkBedWtMax;
	}
	public double getYdStkBedHMax() {
		return ydStkBedHMax;
	}
	public void setYdStkBedHMax(double ydStkBedHMax) {
		this.ydStkBedHMax = ydStkBedHMax;
	}
	public String getYdStkLyrActStat() {
		return ydStkLyrActStat;
	}
	public void setYdStkLyrActStat(String ydStkLyrActStat) {
		this.ydStkLyrActStat = ydStkLyrActStat;
	}
	public String getYdStkLyrMtlStat() {
		return ydStkLyrMtlStat;
	}
	public void setYdStkLyrMtlStat(String ydStkLyrMtlStat) {
		this.ydStkLyrMtlStat = ydStkLyrMtlStat;
	}
	public String getYdStkColGp() {
		return ydStkColGp;
	}
	public void setYdStkColGp(String ydStkColGp) {
		this.ydStkColGp = ydStkColGp;
	}
	public String getYdStkBedNo() {
		return ydStkBedNo;
	}
	public void setYdStkBedNo(String ydStkBedNo) {
		this.ydStkBedNo = ydStkBedNo;
	}
	public String getYdStkLyrNo() {
		return ydStkLyrNo;
	}
	public void setYdStkLyrNo(String ydStkLyrNo) {
		this.ydStkLyrNo = ydStkLyrNo;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getYdStkableBedLyr() {
		return ydStkableBedLyr;
	}
	public void setYdStkableBedLyr(int ydStkableBedLyr) {
		this.ydStkableBedLyr = ydStkableBedLyr;
	}
	public int getYdStkableBedWt() {
		return ydStkableBedWt;
	}
	public void setYdStkableBedWt(int ydStkableBedWt) {
		this.ydStkableBedWt = ydStkableBedWt;
	}
	public double getYdStkableBedH() {
		return ydStkableBedH;
	}
	public void setYdStkableBedH(double ydStkableBedH) {
		this.ydStkableBedH = ydStkableBedH;
	}
	public int getPrior() {
		return prior;
	}
	public void setPrior(int prior) {
		this.prior = prior;
	}
}