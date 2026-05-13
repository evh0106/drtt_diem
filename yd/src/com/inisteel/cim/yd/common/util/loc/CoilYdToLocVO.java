/**
 * 
 */
package com.inisteel.cim.yd.common.util.loc;

/**
 * @author 임춘수
 *
 */
public class CoilYdToLocVO {
	private String ydStkColGp;							//적치열구분
	private String ydStkBedNo;							//적치베드번호
	private String ydStkLyrNo;							//적치단번호
	private int toPosGrade;							//점수 --> 정렬 시 사용	
	private int ydBedErrCd;								//베드에 적치가능한 지에 대한 에러코드값 (10000인 경우 적치가능)
	private int ydStkLyrGrade;
	
	public int getYdBedErrCd() {
		return ydBedErrCd;
	}
	
	public int getYdStkLyrGrade() {
		return ydStkLyrGrade;
	}

	public void setYdStkLyrGrade(int ydStkLyrGrade) {
		this.ydStkLyrGrade = ydStkLyrGrade;
	}

	public void setYdBedErrCd(int ydBedErrCd) {
		this.ydBedErrCd = ydBedErrCd;
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
	public int getToPosGrade() {
		return toPosGrade;
	}
	public void setToPosGrade(int toPosGrade) {
		this.toPosGrade = toPosGrade;
	}
	
	
	
}
