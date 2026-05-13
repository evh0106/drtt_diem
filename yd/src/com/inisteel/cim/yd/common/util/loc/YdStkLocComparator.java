/**
 * 
 */
package com.inisteel.cim.yd.common.util.loc;

import java.util.Comparator;

/**
 * @author 임춘수
 *
 */
public class YdStkLocComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		YdStkLocVO loc0 = (YdStkLocVO)arg0;
		YdStkLocVO loc1 = (YdStkLocVO)arg1;
		
		return loc0.getSeq() - loc1.getSeq();
	}

}
