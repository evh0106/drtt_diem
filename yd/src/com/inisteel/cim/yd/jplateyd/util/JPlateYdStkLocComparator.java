/*
 * @(#) 2후판정정야드 TO위치결정 Comparator Class
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

import java.util.Comparator;

public class JPlateYdStkLocComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		JPlateYdStkLocVO loc0 = (JPlateYdStkLocVO)arg0;
		JPlateYdStkLocVO loc1 = (JPlateYdStkLocVO)arg1;

		return loc0.getSeq() - loc1.getSeq();
	}
}
