/**
 * 
 */
package com.inisteel.cim.yd.common.util.loc;

import java.util.Comparator;

/**
 * @author 임춘수
 *
 */
public class CoilYdToLocComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		CoilYdToLocVO loc0 = (CoilYdToLocVO)arg0;
		CoilYdToLocVO loc1 = (CoilYdToLocVO)arg1;
		
		return loc0.getToPosGrade() - loc1.getToPosGrade();
	}

}
