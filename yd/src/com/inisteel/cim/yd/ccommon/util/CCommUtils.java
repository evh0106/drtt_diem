/**
 * @(#)CCommUtils
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 *
 * @description      야드관리 공통 Utils
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.yd.ccommon.util;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.inisteel.cim.cm.message.MessageSenderAuto;
import com.inisteel.cim.yd.message.MessageSenderTalk;
import com.inisteel.cim.common.exception.AppRuntimeException;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.yd.ccommon.util.CConstant;

import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.query.QueryService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

import java.util.LinkedHashMap;

/**
 * [A] 클래스명 : 야드관리 공통 Utils
 *
 */

public class CCommUtils {
	
	private static Logger logger = new Logger("yd");

	private boolean bDebugFlag=false;
	
	// 야드(설비+BED.No) => 조업(설비)
	public static final Hashtable h_hstEqpGpMatch = new Hashtable();
	// 조업(설비)        => 야드(설비+BED.No)
	public static final Hashtable h_hRvsstEqpGpMatch = new Hashtable();
	
	static {
		
		//==============================================================
		// 야드와 조업의 설비구분 매칭값
		//==============================================================
		h_hstEqpGpMatch.put("JGFE0106", "ECC06");  //#1HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("JGFE0105", "ECC05");  //#1HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("JGFE0104", "ECC04");  //#1HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("JGFE0103", "ECC03");  //#1HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("JGFE0102", "ECC02");  //#1HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("JGFE0101", "ECC01");  //#1HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("JGST0101", "TATI");   //C열연 정정 #1HOT FINAL Coil Station
		h_hstEqpGpMatch.put("JGHE0101", "ECC" );   //C열연 정정 #1HOT FINAL  Enter Coil Car 
		h_hstEqpGpMatch.put("JGFD0108", "DCC08");  //#1HOT FINAL 출측8번지
		h_hstEqpGpMatch.put("JGFD0109", "DCC09");  //#1HOT FINAL 출측9번지
		h_hstEqpGpMatch.put("JGFD0110", "DCC10");  //#1HOT FINAL 출측10번지
		h_hstEqpGpMatch.put("JGFD0111", "DCC11");  //#1HOT FINAL 출측11번지
		
		h_hstEqpGpMatch.put("JFFE0201", "K2-01");  //#2HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("JFFE0202", "K2-02");  //#2HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("JFFE0203", "K2-03");  //#2HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("JFFE0204", "K2-04");  //#2HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("JFFE0205", "K2-05");  //#2HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("JFFE0206", "K2-06");  //#2HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("JFFE0207", "K2-07");  //#2HOT FINAL 입측7번지
		h_hstEqpGpMatch.put("JFFE0208", "K2-08");  //#2HOT FINAL 입측8번지
		h_hstEqpGpMatch.put("JFFE0209", "K2-09");  //#2HOT FINAL 입측9번지
		h_hstEqpGpMatch.put("JFFE0210", "K2-10");  //#2HOT FINAL 입측10번지
		h_hstEqpGpMatch.put("JFFE0211", "K2-11");  //#2HOT FINAL 입측11번지
		h_hstEqpGpMatch.put("JFFE0212", "K2-12");  //#2HOT FINAL 입측12번지
		h_hstEqpGpMatch.put("JFCD0101", "CR01");   //#2HOT FINAL 크래들롤
		
		h_hstEqpGpMatch.put("JDFE0301", "K3-01");  //#3HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("JDFE0302", "K3-02");  //#3HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("JDFE0303", "K3-03");  //#3HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("JDFE0304", "K3-04");  //#3HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("JDFE0305", "K3-05");  //#3HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("JDFE0306", "K3-06");  //#3HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("JDFE0307", "K3-07");  //#3HOT FINAL 입측7번지
		h_hstEqpGpMatch.put("JDFE0308", "K3-08");  //#3HOT FINAL 입측8번지
		h_hstEqpGpMatch.put("JDFE0309", "K3-09");  //#3HOT FINAL 입측9번지
		h_hstEqpGpMatch.put("JDFE0310", "K3-10");  //#3HOT FINAL 입측10번지
		h_hstEqpGpMatch.put("JDFE0311", "K3-11");  //#3HOT FINAL 입측11번지
		h_hstEqpGpMatch.put("JDFE0312", "K3-12");  //#3HOT FINAL 입측12번지
		h_hstEqpGpMatch.put("JDFE0313", "K3-13");  //#3HOT FINAL 입측13번지
		h_hstEqpGpMatch.put("JDFE0314", "K3-14");  //#3HOT FINAL 입측14번지
		h_hstEqpGpMatch.put("JDFE0315", "K3-15");  //#3HOT FINAL 입측15번지
		h_hstEqpGpMatch.put("JDFE0316", "K3-16");  //#3HOT FINAL 입측16번지
		h_hstEqpGpMatch.put("JDFE0317", "K3-17");  //#3HOT FINAL 입측17번지
		h_hstEqpGpMatch.put("JDFE0318", "K3-18");  //#3HOT FINAL 입측18번지
		h_hstEqpGpMatch.put("JDFE0319", "K3-19");  //#3HOT FINAL 입측19번지
		h_hstEqpGpMatch.put("JDFE0320", "K3-20");  //#3HOT FINAL 입측20번지
		h_hstEqpGpMatch.put("JDCD0101", "CR01");   //#3HOT FINAL 크래들롤

		h_hstEqpGpMatch.put("JHKE0106", "ECC06");  //C열연 정정 SPM1 입측6번지
		h_hstEqpGpMatch.put("JHKE0105", "ECC05");  //C열연 정정 SPM1 입측5번지
		h_hstEqpGpMatch.put("JHKE0104", "ECC04");  //C열연 정정 SPM1 입측4번지
		h_hstEqpGpMatch.put("JHKE0103", "ECC03");  //C열연 정정 SPM1 입측3번지
		h_hstEqpGpMatch.put("JHKE0102", "ECC02");  //C열연 정정 SPM1 입측2번지
		h_hstEqpGpMatch.put("JHKE0101", "ECC01");  //C열연 정정 SPM1 입측1번지
		h_hstEqpGpMatch.put("JHNT0101", "ENT");    //C열연 정정 SPM1 Enter Coil Car
		h_hstEqpGpMatch.put("JHKD0101", "DCC01");  //C열연 정정 SPM1 출측1번지
		h_hstEqpGpMatch.put("JHKD0102", "DCC02");  //C열연 정정 SPM1 출측2번지
		h_hstEqpGpMatch.put("JHKD0103", "DCC03");  //C열연 정정 SPM1 출측3번지
		h_hstEqpGpMatch.put("JHKD0104", "DCC04");  //C열연 정정 SPM1 출측4번지
		h_hstEqpGpMatch.put("JHKD0105", "DCC05");  //C열연 정정 SPM1 출측5번지
		h_hstEqpGpMatch.put("JHKD0106", "DCC06");  //C열연 정정 SPM1 출측6번지
		h_hstEqpGpMatch.put("JHKD0107", "DCC07");  //C열연 정정 SPM1 출측7번지
		h_hstEqpGpMatch.put("JHKD0108", "DCC08");  //C열연 정정 SPM1 출측8번지
		h_hstEqpGpMatch.put("JHKD0109", "DCC09");  //C열연 정정 SPM1 출측9번지
		h_hstEqpGpMatch.put("JHKD0110", "DCC10");  //C열연 정정 SPM1 출측10번지
		h_hstEqpGpMatch.put("JHKD0111", "DCC11");  //C열연 정정 SPM1 출측11번지
		
		h_hstEqpGpMatch.put("JEKE0206", "ECC06");  //C열연 정정 SPM2 입측6번지
		h_hstEqpGpMatch.put("JEKE0205", "ECC05");  //C열연 정정 SPM2 입측5번지
		h_hstEqpGpMatch.put("JEKE0204", "ECC04");  //C열연 정정 SPM2 입측4번지
		h_hstEqpGpMatch.put("JEKE0203", "ECC03");  //C열연 정정 SPM2 입측3번지
		h_hstEqpGpMatch.put("JEKE0202", "ECC02");  //C열연 정정 SPM2 입측2번지
		h_hstEqpGpMatch.put("JEKE0201", "ECC01");  //C열연 정정 SPM2 입측1번지
		h_hstEqpGpMatch.put("JENT0201", "ENT");    //C열연 정정 SPM2 Enter Coil Car
		h_hstEqpGpMatch.put("JEKD0201", "DCC01");  //C열연 정정 SPM2 출측1번지	
		h_hstEqpGpMatch.put("JEKD0202", "DCC02");  //C열연 정정 SPM2 출측2번지
		h_hstEqpGpMatch.put("JEKD0203", "DCC03");  //C열연 정정 SPM2 출측3번지
		h_hstEqpGpMatch.put("JEKD0204", "DCC04");  //C열연 정정 SPM2 출측4번지
		h_hstEqpGpMatch.put("JEKD0205", "DCC05");  //C열연 정정 SPM2 출측5번지
		h_hstEqpGpMatch.put("JEKD0206", "DCC06");  //C열연 정정 SPM2 출측6번지
		h_hstEqpGpMatch.put("JEKD0207", "DCC07");  //C열연 정정 SPM2 출측7번지
		h_hstEqpGpMatch.put("JEKD0208", "DCC08");  //C열연 정정 SPM2 출측8번지
		h_hstEqpGpMatch.put("JEKD0209", "DCC09");  //C열연 정정 SPM2 출측9번지
		h_hstEqpGpMatch.put("JEKD0210", "DCC10");  //C열연 정정 SPM2 출측10번지
		h_hstEqpGpMatch.put("JEKD0211", "DCC11");  //C열연 정정 SPM2 출측11번지

		h_hstEqpGpMatch.put("JCKE0312", "ECC12");  //C열연 정정 SPM3 입측12번지
		h_hstEqpGpMatch.put("JCKE0311", "ECC11");  //C열연 정정 SPM3 입측11번지
		h_hstEqpGpMatch.put("JCKE0310", "ECC10");  //C열연 정정 SPM3 입측10번지
		h_hstEqpGpMatch.put("JCKE0309", "ECC09");  //C열연 정정 SPM3 입측9번지
		h_hstEqpGpMatch.put("JCKE0308", "ECC08");  //C열연 정정 SPM3 입측8번지
		h_hstEqpGpMatch.put("JCKE0307", "ECC07");  //C열연 정정 SPM3 입측7번지
		h_hstEqpGpMatch.put("JCKE0306", "ECC06");  //C열연 정정 SPM3 입측6번지
		h_hstEqpGpMatch.put("JCKE0305", "ECC05");  //C열연 정정 SPM3 입측5번지
		h_hstEqpGpMatch.put("JCKE0304", "ECC04");  //C열연 정정 SPM3 입측4번지
		h_hstEqpGpMatch.put("JCKE0303", "ECC03");  //C열연 정정 SPM3 입측3번지
		h_hstEqpGpMatch.put("JCKE0302", "ECC02");  //C열연 정정 SPM3 입측2번지
		h_hstEqpGpMatch.put("JCKE0301", "ECC01");  //C열연 정정 SPM3 입측1번지
		h_hstEqpGpMatch.put("JCKD0301", "DCC01");  //C열연 정정 SPM3 출측1번지
		h_hstEqpGpMatch.put("JCKD0302", "DCC02");  //C열연 정정 SPM3 출측2번지
		h_hstEqpGpMatch.put("JCKD0303", "DCC03");  //C열연 정정 SPM3 출측3번지
		h_hstEqpGpMatch.put("JCKD0304", "DCC04");  //C열연 정정 SPM3 출측4번지
		h_hstEqpGpMatch.put("JCKD0305", "DCC05");  //C열연 정정 SPM3 출측5번지
		h_hstEqpGpMatch.put("JCKD0306", "DCC06");  //C열연 정정 SPM3 출측6번지
		h_hstEqpGpMatch.put("JCKD0307", "DCC07");  //C열연 정정 SPM3 출측7번지
		h_hstEqpGpMatch.put("JCKD0308", "DCC08");  //C열연 정정 SPM3 출측8번지
		h_hstEqpGpMatch.put("JCKD0309", "DCC09");  //C열연 정정 SPM3 출측9번지
		h_hstEqpGpMatch.put("JCKD0310", "DCC10");  //C열연 정정 SPM3 출측10번지
		h_hstEqpGpMatch.put("JCKD0311", "DCC11");  //C열연 정정 SPM3 출측11번지

		h_hstEqpGpMatch.put("JBKE0412", "ECC12");  //C열연 정정 SPM4 입측12번지
		h_hstEqpGpMatch.put("JBKE0411", "ECC11");  //C열연 정정 SPM4 입측11번지
		h_hstEqpGpMatch.put("JBKE0410", "ECC10");  //C열연 정정 SPM4 입측10번지
		h_hstEqpGpMatch.put("JBKE0409", "ECC09");  //C열연 정정 SPM4 입측9번지
		h_hstEqpGpMatch.put("JBKE0408", "ECC08");  //C열연 정정 SPM4 입측8번지
		h_hstEqpGpMatch.put("JBKE0407", "ECC07");  //C열연 정정 SPM4 입측7번지
		h_hstEqpGpMatch.put("JBKE0406", "ECC06");  //C열연 정정 SPM4 입측6번지
		h_hstEqpGpMatch.put("JBKE0405", "ECC05");  //C열연 정정 SPM4 입측5번지
		h_hstEqpGpMatch.put("JBKE0404", "ECC04");  //C열연 정정 SPM4 입측4번지
		h_hstEqpGpMatch.put("JBKE0403", "ECC03");  //C열연 정정 SPM4 입측3번지
		h_hstEqpGpMatch.put("JBKE0402", "ECC02");  //C열연 정정 SPM4 입측2번지
		h_hstEqpGpMatch.put("JBKE0401", "ECC01");  //C열연 정정 SPM4 입측1번지
		h_hstEqpGpMatch.put("JBKD0401", "DCC01");  //C열연 정정 SPM4 출측1번지
		h_hstEqpGpMatch.put("JBKD0402", "DCC02");  //C열연 정정 SPM4 출측2번지
		h_hstEqpGpMatch.put("JBKD0403", "DCC03");  //C열연 정정 SPM4 출측3번지
		h_hstEqpGpMatch.put("JBKD0404", "DCC04");  //C열연 정정 SPM4 출측4번지
		h_hstEqpGpMatch.put("JBKD0405", "DCC05");  //C열연 정정 SPM4 출측5번지
		h_hstEqpGpMatch.put("JBKD0406", "DCC06");  //C열연 정정 SPM4 출측6번지
		h_hstEqpGpMatch.put("JBKD0407", "DCC07");  //C열연 정정 SPM4 출측7번지
		h_hstEqpGpMatch.put("JBKD0408", "DCC08");  //C열연 정정 SPM4 출측8번지
		h_hstEqpGpMatch.put("JBKD0409", "DCC09");  //C열연 정정 SPM4 출측9번지
		h_hstEqpGpMatch.put("JBKD0410", "DCC10");  //C열연 정정 SPM4 출측10번지
		h_hstEqpGpMatch.put("JBKD0411", "DCC11");  //C열연 정정 SPM4 출측11번지
 
		h_hstEqpGpMatch.put("JAKE0512", "ECC12");  //C열연 정정 SPM5 입측12번지
		h_hstEqpGpMatch.put("JAKE0511", "ECC11");  //C열연 정정 SPM5 입측11번지
		h_hstEqpGpMatch.put("JAKE0510", "ECC10");  //C열연 정정 SPM5 입측10번지
		h_hstEqpGpMatch.put("JAKE0509", "ECC09");  //C열연 정정 SPM5 입측9번지
		h_hstEqpGpMatch.put("JAKE0508", "ECC08");  //C열연 정정 SPM5 입측8번지
		h_hstEqpGpMatch.put("JAKE0507", "ECC07");  //C열연 정정 SPM5 입측7번지
		h_hstEqpGpMatch.put("JAKE0506", "ECC06");  //C열연 정정 SPM5 입측6번지
		h_hstEqpGpMatch.put("JAKE0505", "ECC05");  //C열연 정정 SPM5 입측5번지
		h_hstEqpGpMatch.put("JAKE0504", "ECC04");  //C열연 정정 SPM5 입측4번지
		h_hstEqpGpMatch.put("JAKE0503", "ECC03");  //C열연 정정 SPM5 입측3번지
		h_hstEqpGpMatch.put("JAKE0502", "ECC02");  //C열연 정정 SPM5 입측2번지
		h_hstEqpGpMatch.put("JAKE0501", "ECC01");  //C열연 정정 SPM5 입측1번지
		h_hstEqpGpMatch.put("JAKD0501", "DCC01");  //C열연 정정 SPM5 출측1번지
		h_hstEqpGpMatch.put("JAKD0502", "DCC02");  //C열연 정정 SPM5 출측2번지
		h_hstEqpGpMatch.put("JAKD0503", "DCC03");  //C열연 정정 SPM5 출측3번지
		h_hstEqpGpMatch.put("JAKD0504", "DCC04");  //C열연 정정 SPM5 출측4번지
		h_hstEqpGpMatch.put("JAKD0505", "DCC05");  //C열연 정정 SPM5 출측5번지
		h_hstEqpGpMatch.put("JAKD0506", "DCC06");  //C열연 정정 SPM5 출측6번지
		h_hstEqpGpMatch.put("JAKD0507", "DCC07");  //C열연 정정 SPM5 출측7번지
		h_hstEqpGpMatch.put("JAKD0508", "DCC08");  //C열연 정정 SPM5 출측8번지
		h_hstEqpGpMatch.put("JAKD0509", "DCC09");  //C열연 정정 SPM5 출측9번지
		h_hstEqpGpMatch.put("JAKD0510", "DCC10");  //C열연 정정 SPM5 출측10번지
		h_hstEqpGpMatch.put("JAKD0511", "DCC11");  //C열연 정정 SPM5 출측11번지
		
		h_hstEqpGpMatch.put("JCFE0406", "ECC06");  //#4HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("JCFE0405", "ECC05");  //#4HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("JCFE0404", "ECC04");  //#4HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("JCFE0403", "ECC03");  //#4HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("JCFE0402", "ECC02");  //#4HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("JCFE0401", "ECC01");  //#4HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("JCFD0406", "DCC06");  //#4HOT FINAL 출측8번지
		h_hstEqpGpMatch.put("JCFD0407", "DCC07");  //#4HOT FINAL 출측9번지
		h_hstEqpGpMatch.put("JCFD0408", "DCC08");  //#4HOT FINAL 출측10번지
		h_hstEqpGpMatch.put("JCFD0409", "DCC09");  //#4HOT FINAL 출측11번지
		
		h_hstEqpGpMatch.put("JBFE0501", "K5-01");  //#5HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("JBFE0502", "K5-02");  //#5HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("JBFE0503", "K5-03");  //#5HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("JBFE0504", "K5-04");  //#5HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("JBFE0505", "K5-05");  //#5HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("JBFE0506", "K5-06");  //#5HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("JBFE0507", "K5-07");  //#5HOT FINAL 입측7번지
		h_hstEqpGpMatch.put("JBFE0508", "K5-08");  //#5HOT FINAL 입측8번지
		h_hstEqpGpMatch.put("JBFE0509", "K5-09");  //#5HOT FINAL 입측9번지
		h_hstEqpGpMatch.put("JBFE0510", "K5-10");  //#5HOT FINAL 입측10번지
		h_hstEqpGpMatch.put("JBFE0511", "K5-11");  //#5HOT FINAL 입측11번지
		h_hstEqpGpMatch.put("JBFE0512", "K5-12");  //#5HOT FINAL 입측12번지
		h_hstEqpGpMatch.put("JBFE0513", "K5-13");  //#5HOT FINAL 입측13번지
		h_hstEqpGpMatch.put("JBFE0514", "K5-14");  //#5HOT FINAL 입측14번지
		h_hstEqpGpMatch.put("JBFE0515", "K5-15");  //#5HOT FINAL 입측15번지
		h_hstEqpGpMatch.put("JBFE0516", "K5-16");  //#5HOT FINAL 입측16번지
		h_hstEqpGpMatch.put("JBFE0517", "K5-17");  //#5HOT FINAL 입측17번지
		h_hstEqpGpMatch.put("JBFE0518", "K5-18");  //#5HOT FINAL 입측18번지
		h_hstEqpGpMatch.put("JBFE0519", "K5-19");  //#5HOT FINAL 입측19번지
		h_hstEqpGpMatch.put("JBFE0520", "K5-20");  //#5HOT FINAL 입측20번지
		
		h_hstEqpGpMatch.put("JBGF0101", "G1-01");  //B동 지포장 1번지
		h_hstEqpGpMatch.put("JBGF0102", "G1-02");  //B동 지포장 2번지
		h_hstEqpGpMatch.put("JBGF0103", "G1-03");  //B동 지포장 3번지
		h_hstEqpGpMatch.put("JBGF0104", "G1-04");  //B동 지포장 4번지
		h_hstEqpGpMatch.put("JBGF0105", "G1-05");  //B동 지포장 5번지
		h_hstEqpGpMatch.put("JBGF0106", "G1-06");  //B동 지포장 6번지

		h_hstEqpGpMatch.put("JCGF0101", "G2-01");  //C동 지포장 1번지
		h_hstEqpGpMatch.put("JCGF0102", "G2-02");  //C동 지포장 2번지
		h_hstEqpGpMatch.put("JCGF0103", "G2-03");  //C동 지포장 3번지
		h_hstEqpGpMatch.put("JCGF0104", "G2-04");  //C동 지포장 4번지
		h_hstEqpGpMatch.put("JCGF0105", "G2-05");  //C동 지포장 5번지
		h_hstEqpGpMatch.put("JCGF0106", "G2-06");  //C동 지포장 6번지

		h_hstEqpGpMatch.put("JEGF0101", "G3-01");  //E동 지포장 1번지
		h_hstEqpGpMatch.put("JEGF0102", "G3-02");  //E동 지포장 2번지
		h_hstEqpGpMatch.put("JEGF0103", "G3-03");  //E동 지포장 3번지
		h_hstEqpGpMatch.put("JEGF0104", "G3-04");  //E동 지포장 4번지
		h_hstEqpGpMatch.put("JEGF0105", "G3-05");  //E동 지포장 5번지
		h_hstEqpGpMatch.put("JEGF0106", "G3-06");  //E동 지포장 6번지

		h_hstEqpGpMatch.put("JHGF0101", "G4-01");  //H동 지포장 1번지
		h_hstEqpGpMatch.put("JHGF0102", "G4-02");  //H동 지포장 2번지
		h_hstEqpGpMatch.put("JHGF0103", "G4-03");  //H동 지포장 3번지
		h_hstEqpGpMatch.put("JHGF0104", "G4-04");  //H동 지포장 4번지
		h_hstEqpGpMatch.put("JHGF0105", "G4-05");  //H동 지포장 5번지
		h_hstEqpGpMatch.put("JHGF0106", "G4-06");  //H동 지포장 6번지	
		
		
		h_hstEqpGpMatch.put("JFGF0104", "G5-04");  //H동 지포장 4번지
		h_hstEqpGpMatch.put("JFGF0105", "G5-05");  //H동 지포장 5번지
		h_hstEqpGpMatch.put("JFGF0106", "G5-06");  //H동 지포장 6번지
		h_hstEqpGpMatch.put("JFGF0107", "G5-07");  //H동 지포장 7번지
		h_hstEqpGpMatch.put("JFGF0108", "G5-08");  //H동 지포장 8번지
		h_hstEqpGpMatch.put("JFGF0109", "G5-09");  //H동 지포장 9번지	
		
		h_hstEqpGpMatch.put("JFTE0101", "T1-01");  //#텔리스코프 보급 입측1번지
		
		//==============================================================
		// 조업과 야드의 설비구분 매칭값(이상함:결속대만 사용함)
		//==============================================================
		
		h_hRvsstEqpGpMatch.put("ECC06", "JGFE0106");  //#1HOT FINAL 입측6번지
		h_hRvsstEqpGpMatch.put("ECC05", "JGFE0105");  //#1HOT FINAL 입측5번지
		h_hRvsstEqpGpMatch.put("ECC04", "JGFE0104");  //#1HOT FINAL 입측4번지
		h_hRvsstEqpGpMatch.put("ECC03", "JGFE0103");  //#1HOT FINAL 입측3번지
		h_hRvsstEqpGpMatch.put("ECC02", "JGFE0102");  //#1HOT FINAL 입측2번지
		h_hRvsstEqpGpMatch.put("ECC01", "JGFE0101");  //#1HOT FINAL 입측1번지
		h_hRvsstEqpGpMatch.put("TATI" , "JGST0101");  //C열연 정정 #1HOT FINAL Coil Station
		h_hRvsstEqpGpMatch.put("ECC"  , "JGHE0101");  //C열연 정정 #1HOT FINAL  Enter Coil Car 
		h_hRvsstEqpGpMatch.put("DCC08", "JGFD0108");  //#1HOT FINAL 출측8번지
		h_hRvsstEqpGpMatch.put("DCC09", "JGFD0109");  //#1HOT FINAL 출측9번지
		h_hRvsstEqpGpMatch.put("DCC10", "JGFD0110");  //#1HOT FINAL 출측10번지
		h_hRvsstEqpGpMatch.put("DCC11", "JGFD0111");  //#1HOT FINAL 출측11번지
		
		h_hRvsstEqpGpMatch.put("K2-01", "JFFE0201");  //#2HOT FINAL 입측1번지
		h_hRvsstEqpGpMatch.put("K2-02", "JFFE0202");  //#2HOT FINAL 입측2번지
		h_hRvsstEqpGpMatch.put("K2-03", "JFFE0203");  //#2HOT FINAL 입측3번지
		h_hRvsstEqpGpMatch.put("K2-04", "JFFE0204");  //#2HOT FINAL 입측4번지
		h_hRvsstEqpGpMatch.put("K2-05", "JFFE0205");  //#2HOT FINAL 입측5번지
		h_hRvsstEqpGpMatch.put("K2-06", "JFFE0206");  //#2HOT FINAL 입측6번지
		h_hRvsstEqpGpMatch.put("K2-07", "JFFE0207");  //#2HOT FINAL 입측7번지
		h_hRvsstEqpGpMatch.put("K2-08", "JFFE0208");  //#2HOT FINAL 입측8번지
		h_hRvsstEqpGpMatch.put("K2-09", "JFFE0209");  //#2HOT FINAL 입측9번지
		h_hRvsstEqpGpMatch.put("K2-10", "JFFE0210");  //#2HOT FINAL 입측10번지
		h_hRvsstEqpGpMatch.put("K2-11", "JFFE0211");  //#2HOT FINAL 입측11번지
		h_hRvsstEqpGpMatch.put("K2-12", "JFFE0212");  //#2HOT FINAL 입측12번지
		
		h_hRvsstEqpGpMatch.put("K3-01", "JDFE0301");  //#3HOT FINAL 입측1번지
		h_hRvsstEqpGpMatch.put("K3-02", "JDFE0302");  //#3HOT FINAL 입측2번지
		h_hRvsstEqpGpMatch.put("K3-03", "JDFE0303");  //#3HOT FINAL 입측3번지
		h_hRvsstEqpGpMatch.put("K3-04", "JDFE0304");  //#3HOT FINAL 입측4번지
		h_hRvsstEqpGpMatch.put("K3-05", "JDFE0305");  //#3HOT FINAL 입측5번지
		h_hRvsstEqpGpMatch.put("K3-06", "JDFE0306");  //#3HOT FINAL 입측6번지
		h_hRvsstEqpGpMatch.put("K3-07", "JDFE0307");  //#3HOT FINAL 입측7번지
		h_hRvsstEqpGpMatch.put("K3-08", "JDFE0308");  //#3HOT FINAL 입측8번지
		h_hRvsstEqpGpMatch.put("K3-09", "JDFE0309");  //#3HOT FINAL 입측9번지
		h_hRvsstEqpGpMatch.put("K3-10", "JDFE0310");  //#3HOT FINAL 입측10번지
		h_hRvsstEqpGpMatch.put("K3-11", "JDFE0311");  //#3HOT FINAL 입측11번지
		h_hRvsstEqpGpMatch.put("K3-12", "JDFE0312");  //#3HOT FINAL 입측12번지
		h_hRvsstEqpGpMatch.put("K3-13", "JDFE0313");  //#3HOT FINAL 입측13번지
		h_hRvsstEqpGpMatch.put("K3-14", "JDFE0314");  //#3HOT FINAL 입측14번지
		h_hRvsstEqpGpMatch.put("K3-15", "JDFE0315");  //#3HOT FINAL 입측15번지
		h_hRvsstEqpGpMatch.put("K3-16", "JDFE0316");  //#3HOT FINAL 입측16번지
		h_hRvsstEqpGpMatch.put("K3-17", "JDFE0317");  //#3HOT FINAL 입측17번지
		h_hRvsstEqpGpMatch.put("K3-18", "JDFE0318");  //#3HOT FINAL 입측18번지
		h_hRvsstEqpGpMatch.put("K3-19", "JDFE0319");  //#3HOT FINAL 입측19번지
		h_hRvsstEqpGpMatch.put("K3-20", "JDFE0320");  //#3HOT FINAL 입측20번지
		
		h_hRvsstEqpGpMatch.put("ECC06", "JHKE0106");  //C열연 정정 SPM1 입측6번지
		h_hRvsstEqpGpMatch.put("ECC05", "JHKE0105");  //C열연 정정 SPM1 입측5번지
		h_hRvsstEqpGpMatch.put("ECC04", "JHKE0104");  //C열연 정정 SPM1 입측4번지
		h_hRvsstEqpGpMatch.put("ECC03", "JHKE0103");  //C열연 정정 SPM1 입측3번지
		h_hRvsstEqpGpMatch.put("ECC02", "JHKE0102");  //C열연 정정 SPM1 입측2번지
		h_hRvsstEqpGpMatch.put("ECC01", "JHKE0101");  //C열연 정정 SPM1 입측1번지
		h_hRvsstEqpGpMatch.put("ENT"  , "JHNT0101");  //C열연 정정 SPM1 Enter Coil Car
		h_hRvsstEqpGpMatch.put("DCC01", "JHKD0101");  //C열연 정정 SPM1 출측1번지		
		h_hRvsstEqpGpMatch.put("DCC08", "JHKD0108");  //C열연 정정 SPM1 출측8번지
		h_hRvsstEqpGpMatch.put("DCC09", "JHKD0109");  //C열연 정정 SPM1 출측9번지
		h_hRvsstEqpGpMatch.put("DCC10", "JHKD0110");  //C열연 정정 SPM1 출측10번지
		h_hRvsstEqpGpMatch.put("DCC11", "JHKD0111");  //C열연 정정 SPM1 출측11번지
		
		h_hRvsstEqpGpMatch.put("ECC06", "JEKE0206");  //C열연 정정 SPM2 입측6번지
		h_hRvsstEqpGpMatch.put("ECC05", "JEKE0205");  //C열연 정정 SPM2 입측5번지
		h_hRvsstEqpGpMatch.put("ECC04", "JEKE0204");  //C열연 정정 SPM2 입측4번지
		h_hRvsstEqpGpMatch.put("ECC03", "JEKE0203");  //C열연 정정 SPM2 입측3번지
		h_hRvsstEqpGpMatch.put("ECC02", "JEKE0202");  //C열연 정정 SPM2 입측2번지
		h_hRvsstEqpGpMatch.put("ECC01", "JEKE0201");  //C열연 정정 SPM2 입측1번지
		h_hRvsstEqpGpMatch.put("ENT"  , "JENT0201");  //C열연 정정 SPM2 Enter Coil Car 
		h_hRvsstEqpGpMatch.put("DCC01", "JEKD0201");  //C열연 정정 SPM2 출측1번지
		h_hRvsstEqpGpMatch.put("DCC08", "JEKD0208");  //C열연 정정 SPM2 출측8번지
		h_hRvsstEqpGpMatch.put("DCC09", "JEKD0209");  //C열연 정정 SPM2 출측9번지
		h_hRvsstEqpGpMatch.put("DCC10", "JEKD0210");  //C열연 정정 SPM2 출측10번지
		h_hRvsstEqpGpMatch.put("DCC11", "JEKD0211");  //C열연 정정 SPM2 출측11번지
		
//		//이슈ID:11763
//		h_hRvsstEqpGpMatch.put("K5-01", "JAFE0501");  //#5HOT FINAL 입측1번지
//		h_hRvsstEqpGpMatch.put("K5-02", "JAFE0502");  //#5HOT FINAL 입측2번지
//		h_hRvsstEqpGpMatch.put("K5-03", "JAFE0503");  //#5HOT FINAL 입측3번지
//		h_hRvsstEqpGpMatch.put("K5-04", "JAFE0504");  //#5HOT FINAL 입측4번지
//		h_hRvsstEqpGpMatch.put("K5-05", "JAFE0505");  //#5HOT FINAL 입측5번지
//		h_hRvsstEqpGpMatch.put("K5-06", "JAFE0506");  //#5HOT FINAL 입측6번지
//		h_hRvsstEqpGpMatch.put("K5-07", "HAFE0507");  //#5HOT FINAL 입측7번지
//		h_hRvsstEqpGpMatch.put("K5-08", "HAFE0508");  //#5HOT FINAL 입측8번지
//		h_hRvsstEqpGpMatch.put("K5-09", "HAFE0509");  //#5HOT FINAL 입측9번지
//		h_hRvsstEqpGpMatch.put("K5-10", "HAFE0510");  //#5HOT FINAL 입측10번지
//		h_hRvsstEqpGpMatch.put("K5-11", "HAFE0511");  //#5HOT FINAL 입측11번지
//		h_hRvsstEqpGpMatch.put("K5-12", "HAFE0512");  //#5HOT FINAL 입측12번지
//		h_hRvsstEqpGpMatch.put("K5-13", "HAFE0513");  //#5HOT FINAL 입측13번지
//		h_hRvsstEqpGpMatch.put("K5-14", "HAFE0514");  //#5HOT FINAL 입측14번지
//		h_hRvsstEqpGpMatch.put("K5-15", "HAFE0515");  //#5HOT FINAL 입측15번지
//		h_hRvsstEqpGpMatch.put("K5-16", "HAFE0516");  //#5HOT FINAL 입측16번지
//		h_hRvsstEqpGpMatch.put("K5-17", "HAFE0517");  //#5HOT FINAL 입측17번지
//		h_hRvsstEqpGpMatch.put("K5-18", "HAFE0518");  //#5HOT FINAL 입측18번지
//		h_hRvsstEqpGpMatch.put("K5-19", "HAFE0519");  //#5HOT FINAL 입측19번지
//		h_hRvsstEqpGpMatch.put("K5-20", "HAFE0520");  //#5HOT FINAL 입측20번지
		
		
		h_hRvsstEqpGpMatch.put("K5-01", "JBFE0501");  //#5HOT FINAL 입측1번지
		h_hRvsstEqpGpMatch.put("K5-02", "JBFE0502");  //#5HOT FINAL 입측2번지
		h_hRvsstEqpGpMatch.put("K5-03", "JBFE0503");  //#5HOT FINAL 입측3번지
		h_hRvsstEqpGpMatch.put("K5-04", "JBFE0504");  //#5HOT FINAL 입측4번지
		h_hRvsstEqpGpMatch.put("K5-05", "JBFE0505");  //#5HOT FINAL 입측5번지
		h_hRvsstEqpGpMatch.put("K5-06", "JBFE0506");  //#5HOT FINAL 입측6번지
		h_hRvsstEqpGpMatch.put("K5-07", "JBFE0507");  //#5HOT FINAL 입측7번지
		h_hRvsstEqpGpMatch.put("K5-08", "JBFE0508");  //#5HOT FINAL 입측8번지
		h_hRvsstEqpGpMatch.put("K5-09", "JBFE0509");  //#5HOT FINAL 입측9번지
		h_hRvsstEqpGpMatch.put("K5-10", "JBFE0510");  //#5HOT FINAL 입측10번지
		h_hRvsstEqpGpMatch.put("K5-11", "JBFE0511");  //#5HOT FINAL 입측11번지
		h_hRvsstEqpGpMatch.put("K5-12", "JBFE0512");  //#5HOT FINAL 입측12번지
		h_hRvsstEqpGpMatch.put("K5-13", "JBFE0513");  //#5HOT FINAL 입측13번지
		h_hRvsstEqpGpMatch.put("K5-14", "JBFE0514");  //#5HOT FINAL 입측14번지
		h_hRvsstEqpGpMatch.put("K5-15", "JBFE0515");  //#5HOT FINAL 입측15번지
		h_hRvsstEqpGpMatch.put("K5-16", "JBFE0516");  //#5HOT FINAL 입측16번지
		h_hRvsstEqpGpMatch.put("K5-17", "JBFE0517");  //#5HOT FINAL 입측17번지
		h_hRvsstEqpGpMatch.put("K5-18", "JBFE0518");  //#5HOT FINAL 입측18번지
		h_hRvsstEqpGpMatch.put("K5-19", "JBFE0519");  //#5HOT FINAL 입측19번지
		h_hRvsstEqpGpMatch.put("K5-20", "JBFE0520");  //#5HOT FINAL 입측20번지
		
		// 151210 hun 지포장 코드 추가
		h_hRvsstEqpGpMatch.put("G1-01", "JBGF0101");  //B동 지포장 1번지
		h_hRvsstEqpGpMatch.put("G1-02", "JBGF0102");  //B동 지포장 2번지
		h_hRvsstEqpGpMatch.put("G1-03", "JBGF0103");  //B동 지포장 3번지
		h_hRvsstEqpGpMatch.put("G1-04", "JBGF0104");  //B동 지포장 4번지
		h_hRvsstEqpGpMatch.put("G1-05", "JBGF0105");  //B동 지포장 5번지
		h_hRvsstEqpGpMatch.put("G1-06", "JBGF0106");  //B동 지포장 6번지
		
		h_hRvsstEqpGpMatch.put("G2-01", "JCGF0101");  //C동 지포장 1번지
		h_hRvsstEqpGpMatch.put("G2-02", "JCGF0102");  //C동 지포장 2번지
		h_hRvsstEqpGpMatch.put("G2-03", "JCGF0103");  //C동 지포장 3번지
		h_hRvsstEqpGpMatch.put("G2-04", "JCGF0104");  //C동 지포장 4번지
		h_hRvsstEqpGpMatch.put("G2-05", "JCGF0105");  //C동 지포장 5번지
		h_hRvsstEqpGpMatch.put("G2-06", "JCGF0106");  //C동 지포장 6번지
		
		h_hRvsstEqpGpMatch.put("G3-01", "JEGF0101");  //E동 지포장 1번지
		h_hRvsstEqpGpMatch.put("G3-02", "JEGF0102");  //E동 지포장 2번지
		h_hRvsstEqpGpMatch.put("G3-03", "JEGF0103");  //E동 지포장 3번지
		h_hRvsstEqpGpMatch.put("G3-04", "JEGF0104");  //E동 지포장 4번지
		h_hRvsstEqpGpMatch.put("G3-05", "JEGF0105");  //E동 지포장 5번지
		h_hRvsstEqpGpMatch.put("G3-06", "JEGF0106");  //E동 지포장 6번지
		
		h_hRvsstEqpGpMatch.put("G4-01", "JHGF0101");  //H동 지포장 1번지
		h_hRvsstEqpGpMatch.put("G4-02", "JHGF0102");  //H동 지포장 2번지
		h_hRvsstEqpGpMatch.put("G4-03", "JHGF0103");  //H동 지포장 3번지
		h_hRvsstEqpGpMatch.put("G4-04", "JHGF0104");  //H동 지포장 4번지
		h_hRvsstEqpGpMatch.put("G4-05", "JHGF0105");  //H동 지포장 5번지
		h_hRvsstEqpGpMatch.put("G4-06", "JHGF0106");  //H동 지포장 6번지
		
		h_hRvsstEqpGpMatch.put("G5-04", "JFGF0104");  //H동 지포장 4번지
		h_hRvsstEqpGpMatch.put("G5-05", "JFGF0105");  //H동 지포장 5번지
		h_hRvsstEqpGpMatch.put("G5-06", "JFGF0106");  //H동 지포장 6번지
		h_hRvsstEqpGpMatch.put("G5-07", "JFGF0107");  //H동 지포장 7번지
		h_hRvsstEqpGpMatch.put("G5-08", "JFGF0108");  //H동 지포장 8번지
		h_hRvsstEqpGpMatch.put("G5-09", "JFGF0109");  //H동 지포장 9번지
		
		h_hRvsstEqpGpMatch.put("T1-02", "JFTD0101");  //F동 텔레스코프 추출 번지
	
	}
	
	/**
	 * 문자열이 null 일때 임의의 문자열을 반환한다.
	 * @param value
	 * @param defaultValue
	 * @return String
	 */
	public String nvl(String value, String defaultValue) {
		return (value == null || "".equals(value) || "NULL".equals(value.trim().toUpperCase())) ? defaultValue : value;
	}

	public String nvl(Object o, String defaultValue) {
		return (o == null) ? defaultValue : o.toString();
	}

	/**
	 * 문자열이 null 일때 ""을 반환한다.
	 * @param value
	 * @return String
	 */
	public String trim(String value) {
		return (value == null || "NULL".equals(value.trim().toUpperCase())) ? "" : value.trim();
	}

	/**
	 * Object가 null 일때 true를 반환한다.
	 * @param obj
	 * @return boolean
	 */
	public boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof String) {
			if ("".equals(obj)) {
				return true;
			}
		} else if (obj instanceof JDTORecord) {
			if (((JDTORecord)obj).size() <= 0) {
				return true;
			}
		} else if (obj instanceof JDTORecord[]) {
			if (((JDTORecord[])obj).length <= 0) {
				return true;
			}
		} else if (obj instanceof JDTORecordSet) {
			if (((JDTORecordSet)obj).size() <= 0) {
				return true;
			}
		} else if (obj instanceof Object[]) {
			if (((Object[])obj).length <= 0) {
				return true;
			}
		} else if (obj instanceof Object[][]) {
			if (((Object[][])obj).length <= 0) {
				return true;
			}
		}

		return false;
	}
	

	
	/**
	 * 페이징 처리 변수 가져오기
	 */
	public int[] getCurrRow(GridData gdData) throws Exception {
		return getCurrRow(gridDataTojdtoRecord(gdData));
	}

	public int[] getCurrRow(JDTORecord record) {
		int viewRows  = Integer.parseInt(nvl(record.getFieldString("viewRows"), "10")); //ROW 갯수
		int currpage  = Integer.parseInt(nvl(record.getFieldString("viewPage"), "1")); //현재 페이지
		int startRow = (currpage - 1) * viewRows + 1;
		int endRow   = currpage * viewRows;

		return new int[]{startRow, endRow};
	}

	//해쉬맵의 내용을 GridData의 파라미터로 담는다.
	public GridData hashMapToGridData(HashMap inMap) throws Exception {
		GridData returnGridData = new GridData();

		if (inMap == null || inMap.isEmpty()) {
			return returnGridData;
		}

		java.util.Set set = inMap.keySet();
		java.util.Iterator iterator = set.iterator();

		String key = "";
		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnGridData.addParam(key, nvl(inMap.get(key), ""));
		}

		return returnGridData;
	}

	//해쉬맵의 내용을 JDTORecord로 담는다.
	public JDTORecord hashMapTojdtoRecord(HashMap inMap) throws Exception {
		JDTORecord returnJRecord = JDTORecordFactory.getInstance().create();

		if (inMap == null || inMap.isEmpty()) {
			return returnJRecord;
		}

		java.util.Set set = inMap.keySet();
		java.util.Iterator iterator = set.iterator();

		String key = "";
		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnJRecord.addField(key, nvl(inMap.get(key), ""));
		}

		return returnJRecord;
	}

	//해쉬맵의 내용을 JDTORecord로 담는다.
	public JDTORecord gridDataTojdtoRecord(GridData gdData) throws Exception {
		JDTORecord rowJrecord = JDTORecordFactory.getInstance().create();

		if (gdData == null) {
			return rowJrecord;
		}

		String params[] = gdData.getParamNames();
		for (int ii = 0; ii < params.length; ii++) {
			rowJrecord.addField(params[ii], nvl(gdData.getParam(params[ii]), ""));
		}

		return rowJrecord;
	}

	//해쉬맵의 내용을 JDTORecord로 담는다.
	public HashMap jdtoRecordTohashMap(JDTORecord inJRecord) throws Exception {
		HashMap returnMap = new HashMap();

		if (inJRecord == null || inJRecord.size() == 0) {
			return returnMap;
		}

		java.util.Iterator iterator = inJRecord.iterateName();

		String key = "";
		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnMap.put(key, nvl(inJRecord.getField(key), ""));
		}

		return returnMap;
	}

	//List의 JDTORecord를 HashMap으로 변환한다.
	public List listJdtoRecordTohashMap(List inDataList) throws Exception {
		List returnList = new ArrayList();

		if (inDataList == null || inDataList.isEmpty()) {
			return returnList;
		}

		for (int ii = 0; ii < inDataList.size(); ii++) {
			returnList.add(jdtoRecordTohashMap((JDTORecord)inDataList.get(ii)));
		}

		return returnList;
	}

	/**
	 * 입력값을 원하는 포멧으로 변화하는 메소드
	 * @param no
	 * @param formatter
	 * @return
	 */
	public String format(String no, String formatter) {
		try {
			return format(Double.parseDouble(no), formatter);
		} catch (Exception e) {
			return "";
		}
	}

	public String format(int no, String formatter) {
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(float no, String formatter) {
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(long no, String formatter) {
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(double no, String formatter) {
		DecimalFormat df = new DecimalFormat(formatter);
		return df.format(no);
	}

	public String format(int no, int len) {
		DecimalFormat df = new DecimalFormat(addStr(len, "0"));
		return df.format(no);
	}

	public String formatMaxNo(int no, int maxNo) {
		DecimalFormat df = new DecimalFormat(addStr((String.valueOf(maxNo)).length(), "0"));
		return df.format(no);
	}

	public double trunc(double val, int digit) {
		double val2 = 0.0;
		if (val > 0) {
			val2 = Math.floor(val * Math.pow(10, digit));
		} else if (val < 0) {
			val2 = Math.ceil(val * Math.pow(10, digit));
		}
		return val2 / Math.pow(10, digit);
	}

	public float trunc(float val, int digit) {
		double val2 = 0.0;
		if (val > 0) {
			val2 = Math.floor(val * Math.pow(10, digit));
		} else if (val < 0) {
			val2 = Math.ceil(val * Math.pow(10, digit));
		}
		return (float)(val2 / Math.pow(10, digit));
	}

	public double round(double val, int digit) {
		return Math.round(val * Math.pow(10, digit)) / Math.pow(10, digit);
	}

	public float round(float val, int digit) {
		return (float)(Math.round(val * Math.pow(10, digit)) / Math.pow(10, digit));
	}

 //************************************** WISEGRID **************************************

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값 List를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, List dataList) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataList, null, null);
	}

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값JDTORecord를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataJrecord, null, null);
	}

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값 List를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, List dataList, GridData gdReq) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataList, gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecordSet dataSet, GridData gdReq) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataSet.toList(), gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecordSet dataSet, GridData gdReq, String numberType) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataSet.toList(), gdReq, numberType);
	}

	/**
	 * WiseGrid 관련 Helper Method
	 * 디비 결과값JDTORecord를 GridData로 변환한다.
	 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord, GridData gdReq) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataJrecord, gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, List dataList, GridData gdReq, String numberType) throws Exception {
		/*
		 * DataType
		 * C - t_checkbox
		 * L - t_combo
		 * N - t_number
		 * T - t_text
		 * D - t_date
		 * I - t_imagetext
		 * R - t_radio
		 */
		JDTORecord dataJrecord  = null;
		GridHeader[] gridHeader = returnGrid.getHeaders();
		String headerName		= "";
		String dataType			= "";
		String headerNameVal	= "";
		String headerNameChar	= "";

		if (dataList == null || dataList.isEmpty()) {
			returnGrid.addParam("TOTALCOUNT", "0");
			//returnGrid.addParam("SELECT_MSG", MessageHelper.getUserMessage("MSG0103", new String[]{""}, ""));
		} else {
			String totCount = "0";
			for (int ii = 0; ii < gridHeader.length; ii++) {
				headerName = gridHeader[ii].getID();
				dataType = gridHeader[ii].getDataType();

				/*
				 * 컬럼에 맞게 데이타를 세팅한다.
				 * SEQNO, CHECK, CRUD은  디비에서 가져오지 않는다. 화면에서 가져오는 값이다. 화면에 없다면..패스.
				 */
				if ("SEQNO".equals(headerName)) {
					for (int kk = 0; kk < dataList.size(); kk++) {
						returnGrid.getHeader("SEQNO").addValue(String.valueOf(kk + 1), "");
					}
				} else if ("CHECK".equals(headerName)) {
					for (int kk = 0; kk < dataList.size(); kk++) {
						returnGrid.getHeader("CHECK").addValue("0", "");
					}
				} else if ("CRUD".equals(headerName)) {
					for (int kk = 0; kk < dataList.size(); kk++) {
						returnGrid.getHeader("CRUD").addValue("R", "R");
					}
				} else {
					for (int jj = 0; jj < dataList.size(); jj++) {
						dataJrecord = (JDTORecord)dataList.get(jj);

						/*
						 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
						 */
						if ("0".equals(totCount)) {
							totCount = StringHelper.evl(dataJrecord.getFieldString("TOTALCOUNT"), "0");
						}

						headerNameVal = StringHelper.evl(dataJrecord.getFieldString(headerName), "");
						if (!"".equals(headerNameVal)) {
							headerNameChar = headerNameVal.substring(0, 1);
						}

						if ("L".equals(dataType)) {
							//t_combo 일때...
							returnGrid.getHeader(headerName).addSelectedHiddenValue(headerNameVal);
						} else if ("C".equals(dataType) || "R".equals(dataType)) {
							//t_checkbox, t_radio 일때...
							//0, 1 이 아니면 에러가 남....에러가 나지 않도록...세팅함
							if ("0".equals(headerNameChar) || "1".equals(headerNameChar)) {
								returnGrid.getHeader(headerName).addValue(headerNameVal, "");
							} else {
								//언체크로 세팅(0)
								returnGrid.getHeader(headerName).addValue("0", "");
							}
						} else if ("D".equals(dataType)) {
							//t_date 일때...YYYYMMDD 형식이 아니면 에러가 떨어짐.
							if (headerNameVal.length() > 10) {
								returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal.substring(0, 10), "-", ""), "");
							} else if (headerNameVal.length() == 10 || headerNameVal.length() == 8) {
								returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal, "-", ""), "");
							} else {
								returnGrid.getHeader(headerName).addValue("", "");
							}
						} else if ("I".equals(dataType)) {
							//t_imagetext 일때...
							returnGrid.getHeader(headerName).addValue(headerNameVal, headerNameVal, 0);
						} else if ("N".equals(dataType)) {
							//t_number 일때 값이 0이면  space를 전송한다.
							if (!"number".equals(numberType)) {
								if (!"0".equals(headerNameVal)) {
									returnGrid.getHeader(headerName).addValue(headerNameVal, "");
								} else {
									returnGrid.getHeader(headerName).addValue("", "");
								}
							} else {
								returnGrid.getHeader(headerName).addValue(headerNameVal, "");
							}
						} else {
							returnGrid.getHeader(headerName).addValue(headerNameVal, "");
						}
					}//for
				}//if
			}//for

			/*
			 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
			 */
			//total row 세팅..
			returnGrid.addParam("TOTALCOUNT", totCount);
		}

		/*
		 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
		 */
		if (gdReq != null) {
			String params[] = gdReq.getParamNames();

			for (int ii=0; ii<params.length; ii++) {
				returnGrid.addParam(params[ii], StringHelper.evl(gdReq.getParam(params[ii]), ""));
			}
		}

		return returnGrid;
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord dataJrecord, GridData gdReq, String numberType) throws Exception {
		GridHeader[] gridHeader = returnGrid.getHeaders();
		String headerName		= "";
		String dataType			= "";
		String headerNameVal	= "";
		String headerNameChar	= "";

		/*
		 * 컬럼에 맞게 데이타를 세팅한다.
		 * SEQ_NO, SELECTED은 따로 생성한다. 이 두개의 컬럼은 디비에서 가져오지 않는다.
		 */
		if (dataJrecord == null || dataJrecord.size() == 0) {
			returnGrid.addParam("TOTALCOUNT", "0");
		} else {
			for (int ii = 0; ii < gridHeader.length; ii++) {
				headerName = gridHeader[ii].getID();
				dataType = gridHeader[ii].getDataType();

				/*
				 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
				 */
				if (ii == 0) {
					//total row 세팅..
					returnGrid.addParam("TOTALCOUNT", StringHelper.evl(dataJrecord.getFieldString("TOTALCOUNT"), "0"));
				}

				/*
				 * SEQNO, CHECK, CRUD은  디비에서 가져오지 않는다. 화면에서 가져오는 값이다. 화면에 없다면..패스.
				 */
				if ("SEQNO".equals(headerName)) {
					returnGrid.getHeader("SEQNO").addValue("1", "");
				} else if ("CHECK".equals(headerName)) {
					returnGrid.getHeader("CHECK").addValue("0", "");
				} else if ("CRUD".equals(headerName)) {
					returnGrid.getHeader("CRUD").addValue("R", "R");
				} else {
					/*
					 * 컬럼에 맞게 데이타를 세팅한다.
					 */
					headerNameVal = StringHelper.evl(dataJrecord.getFieldString(headerName), "");
					if (!"".equals(headerNameVal)) {
						headerNameChar = headerNameVal.substring(0, 1);
					}

					if ("L".equals(dataType)) {
						//t_combo 일때...
						returnGrid.getHeader(headerName).addSelectedHiddenValue(headerNameVal);
					} else if ("C".equals(dataType) || "R".equals(dataType)) {
						//t_checkbox, t_radio 일때...
						//0, 1 이 아니면 에러가 남....에러가 나지 않도록...세팅함
						if ("0".equals(headerNameChar) || "1".equals(headerNameChar)) {
							returnGrid.getHeader(headerName).addValue(headerNameChar, "");
						} else {
							//언체크로 세팅(0)
							returnGrid.getHeader(headerName).addValue("0", "");
						}
					} else if ("D".equals(dataType)) {
						//t_date 일때...YYYYMMDD 형식이 아니면 에러가 떨어짐.
						if (headerNameVal.length() > 10) {
							returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal.substring(0, 10), "-", ""), "");
						} else if (headerNameVal.length() == 10 || headerNameVal.length() == 8) {
							returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(headerNameVal, "-", ""), "");
						} else {
							returnGrid.getHeader(headerName).addValue("", "");
						}
					} else if ("I".equals(dataType)) {
						//t_imagetext 일때...
						returnGrid.getHeader(headerName).addValue(headerNameVal, headerNameVal, 0);
					} else if ("N".equals(dataType)) {
						//t_number 일때 값이 0이면  space를 전송한다.
						if (!"number".equals(numberType)) {
							if (!"0".equals(headerNameVal)) {
								returnGrid.getHeader(headerName).addValue(headerNameVal, "");
							} else {
								returnGrid.getHeader(headerName).addValue("", "");
							}
						} else {
							returnGrid.getHeader(headerName).addValue(headerNameVal, "");
						}
					} else {
						returnGrid.getHeader(headerName).addValue(headerNameVal, "");
					}
				}
			}//for
		}

		/*
		 * JSP에서 요청된 파리미터를 그대로 리턴 그리드에 담는다.
		 */
		if (gdReq != null) {
			String params[] = gdReq.getParamNames();

			for (int ii = 0; ii < params.length; ii++) {
				returnGrid.addParam(params[ii], StringHelper.evl(gdReq.getParam(params[ii]), ""));
			}
		}

		return returnGrid;
	}


	/**
	 * Pk로 조회된 값을 GridData의 파리미터로 세팅한다.
	 * jsp에서는 event="EndQuery()"에서 GridObj.GetParam(key)으로 받으면 된다.
	 */
	public GridData jdtoRecordToGridParam(JDTORecord dataJrecord) throws Exception {
		GridData returnGrid = new GridData();
		java.util.Iterator iterator = dataJrecord.iterateName();
		String key = "";

		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnGrid.addParam(key, String.valueOf(dataJrecord.getField(key)));
		}

		return returnGrid;
	}

	/**
	 * Pk로 조회된 값을 GridData의 파리미터로 세팅한다.(기존의 Grid에 추가하고싶을때)
	 * jsp에서는 event="EndQuery()"에서 GridObj.GetParam(key)으로 받으면 된다.
	 */
	public GridData jdtoRecordToGridParam(JDTORecord dataJrecord, GridData returnGrid) throws Exception {
		java.util.Iterator iterator = dataJrecord.iterateName();
		String key = "";

		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnGrid.addParam(key, String.valueOf(dataJrecord.getField(key)));
		}

		return returnGrid;
	}

	/**
	 * GridData 의 내용을 List로 변환한다.
	 */
	public List GridDataToList(GridData dataGrid) throws Exception {
		List returnList = new ArrayList();
		JDTORecord rowJrecord = null;

		if (dataGrid == null) {
			return returnList;
		}

		GridHeader[] gridHeaders = dataGrid.getHeaders();

		for (int ii = 0; ii < gridHeaders[0].getRowCount(); ii++) {
			rowJrecord = JDTORecordFactory.getInstance().create();
			for (int jj = 0; jj < gridHeaders.length; jj++) {
				rowJrecord.addField(gridHeaders[jj].getID(), StringHelper.evl(gridHeaders[jj].getValue(ii), "").trim());
			}

			returnList.add(rowJrecord);
		}

		return returnList;
	}

	/**
	 * GridData의 PARAM 정보를 JDTORecord 으로 변환하여 리턴한다.(GridData의 조회 조건을 가져오기위해 사용)
	 *
	 * @param inDto
	 * @return
	 */
	public JDTORecord genParamToJDTORecord(GridData inDto) {
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();
		boolean isUpperKey = false;

		try {
			if (inDto.getParam("set_upper") != null && inDto.getParam("set_upper").equals("true")) {
				isUpperKey = true;
			}

			outRecord = JDTORecordFactory.getInstance().create();
			String params[] = inDto.getParamNames();
			for (int ii = 0; ii < params.length; ii++) {
				String key = (String) params[ii];
				String value = StringHelper.nvl(inDto.getParam(params[ii]), "");

				// DBAssistant 에 전달할 JDTORecord를 설정합니다.
				outRecord.setField((isUpperKey) ? key.toUpperCase() : key, value);
			}
		} catch (Exception e) {
			logger.println(LogLevel.DEBUG, e.getMessage());
		}

		return outRecord;
	}

	/**
	 * GridData의 입력/수정/삭제 정보를 JDTORecord [] 으로 변환하여 리턴한다. (GridData의 입력/수정/삭제
	 * 항목을 가져오기위해 사용)
	 *
	 * @param inDto
	 * @return
	 */
	public JDTORecord[] genGridToJDTORecord(GridData inDto) throws Exception {
		boolean isUpperKey = false;

		if (inDto.getParam("set_upper") != null && inDto.getParam("set_upper").equals("true")) {
			isUpperKey = true;
		}

		GridHeader[] ghs = inDto.getHeaders();
		int hCount = ghs.length;
		int rCount = 0;
		if (hCount > 0) {
			rCount = ghs[0].getRowCount();
		}
		JDTORecord[] jdtoAl = new JDTORecord[rCount];
		logger.println(LogLevel.DEBUG, "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG, "헤더갯수:" + hCount);
		logger.println(LogLevel.DEBUG, "Row갯수:" + rCount);

		logger.println(LogLevel.DEBUG, "========== GridData inDto ROW DATA ===========");
		for (int ii = 0; ii < rCount; ii++) {
			GridHeader chHeader = inDto.getHeader("CHECK");
			boolean Checked = (Integer.parseInt(chHeader.getValue(ii)) == 1) ? true : false;
			if (Checked) {
				JDTORecord jDto = genParamToJDTORecord(inDto);

				for (int jj = 0; jj < hCount; jj++) {
					String key = ghs[jj].getID();
					String rValue = "";

					if (ghs[jj].getDataType().equals(OperateGridData.t_combo)) {
						rValue = StringHelper.evl(ghs[jj].getComboHiddenValues()[ghs[jj].getSelectedIndex(ii)], "");
					} else {
						rValue = StringHelper.evl(ghs[jj].getValue(ii), "");
					}

					jDto.setField((isUpperKey) ? key.toUpperCase() : key, rValue);
				}
				jdtoAl[ii] = jDto;
			}
		}

		logger.println(LogLevel.DEBUG, "========== JDTORecord START ===========");
		for (int mm = 0; mm < jdtoAl.length; mm++) {
			logger.println(LogLevel.DEBUG, jdtoAl[mm].toString());
		}
		logger.println(LogLevel.DEBUG, "========== JDTORecord END ===========");

		return jdtoAl;
	}

	/**
	 * executeBatch를 사용했을 경우 성공여부를 리턴하는 메소드
	 * 사용 예)
	 * int[] results = new CommonDAO.executeBatch(?, ?);
	 * if (isBatchSuccess(results)) { 성공 } else { 실패 };
	 */
	public boolean isBatchSuccess(int[] results) {
		if (results == null || results.length == 0) {
			return false;
		}

		boolean result = true;

		for (int ii = 0; ii < results.length; ii++) {
			if (results[ii] == -3) {
				result = false;
				break;
			}
		}

		return result;
	}

	/**
	 * 20070402의 날짜포멧을 원하는 구분자로 바꾸고 싶을때..
	 * 사용 예)
	 * CmnUtil.addDateGubunStr("20070405", "-")
	 * 6자리일때도 가능하게 추가(200705 -> 2007-05)
	 */
	public String addDateGubunStr(String src, String gubun) {
		String temp = StringHelper.replaceStr(StringHelper.replaceStr(src, "/", ""), ".", "");
		if (temp.length() == 8) {
			return temp.substring(0, 4) + gubun + temp.substring(4, 6) + gubun + temp.substring(6);
		} else if (temp.length() == 6) {
			return temp.substring(0, 4) + gubun + temp.substring(4, 6);
		} else {
			return src;
		}
	}

	/**
	 * HH24:MI:SS의 시간포멧을 원하는 구분자로 바꾸고 싶을때..
	 * 사용 예)
	 * CmnUtil.addTimeGubunStr("HH24:MI:SS", " ")
	 *
	 */
	public String addTimeGubunStr(String src, String gubun) {
		String temp = StringHelper.replaceStr(StringHelper.replaceStr(src, ":", ""), " ", "");
		if (temp.length() == 6) {
			return temp.substring(0, 2) + gubun + temp.substring(2, 4) + gubun + temp.substring(4,6);
		} else {
			return src;
		}
	}

	/**
	 *메서드명 : getCalsDate
	 *메서드 기능 : 원하는 시점의 날짜를 찾는다.
	 *PARAM : string, int
	 *     getCalsDate(0, 1) :오늘
	 *     getCalsDate(1, 1) :년, -1(1년전 오늘),-2(2년전 오늘)
	 *     getCalsDate(2, 1) :개월, -1(1개월전 오늘),-2(2개월전 오늘), 1(1개월후 오늘)
	 *     getCalsDate(3 or 4 or 8,1) :주, -1(일주일전 같은요일), 1(1주일후 같은요일)
	 *     getCalsDate(5 or 6 or 7,1) :하루, -1(오늘부터 하루전), 1(오늘부터 하루후)
	 *     getCalsDate(9, 1) :12시간, -1(12시간전) 1(12시간후) 2(24시간후
	 *PARAM fmtStr : 출력을 원하는 날짜 형식 ex) "yyyyMMdd", "yyyy-MM-dd"
	 *RETURN VALUE : string
	 */
	public String getCalsDate(String yyddtt, int y, int z, String fmtStr) {
		int yy = Integer.parseInt(yyddtt.substring(0, 4));     // 일시에서  년도
		int mm = Integer.parseInt(yyddtt.substring(4, 6)) - 1; // 월은 0부터 11로 0은 1월 ~ 11은 12월이다. 그래서 월에서 1을 뺀다.
		int dd = Integer.parseInt(yyddtt.substring(6, 8));     // 일시에서 일

		Calendar cal = Calendar.getInstance(Locale.KOREAN);
		cal.set(yy, mm, dd);
		cal.add(y, z);
		Date currentTime = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(fmtStr, Locale.KOREAN);
		String timestr = formatter.format(currentTime);

		return timestr;
	}

	/**
	 *메서드명 : getHour
	 *메서드 기능 : 두일자의 차이의 구하고자 하는 day, hour, minute, second를 얻는다.
	 *PARAM sDt: 시작일자
	 *PARAM eDt: 종료일자
	 *PARAM type : 구하고자 하는 type(day, hour, minute, second)
	 *RETURN VALUE : string
	*/
	public String getHour(String sDt, String eDt, String type) throws Exception {
		String time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date1 = sdf.parse(sDt);
		Date date2 = sdf.parse(eDt);

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(date1);
		c2.setTime(date2);

		long intervalMilli = c2.getTimeInMillis() - c1.getTimeInMillis();
		long second = 1000;
		long minute = second * 60;
		long hour = minute * 60;
		long day = hour * 24;

		if ("second".equals(type)) {
			time = String.valueOf(intervalMilli / hour);
		} else if ("minute".equals(type)) {
			time = String.valueOf(intervalMilli / minute);
		} else if ("hour".equals(type)) {
			time = String.valueOf(intervalMilli / hour);
		} else if ("day".equals(type)) {
			time = String.valueOf(intervalMilli / day);
		}

		return time;
	}

	/**
	 *메서드명 : getDecimal
	 *메서드 기능 : String으로 받은 수치 소수점을 찍어서 반환한다.
	 *PARAM strData : 받은 수치 데이터
	 *PARAM strDecimal : 소수점을 찍어줄 자리수
	 *RETURN VALUE : string
	*/
	public String getDecimal(String strData, String strDecimal) {
		try {
			String temData1 = String.valueOf(Integer.parseInt(strData.substring(0, strData.length()-Integer.parseInt(strDecimal))));
			String temData2 = String.valueOf(Integer.parseInt(strData.substring(strData.length()-Integer.parseInt(strDecimal),strData.length())));

			return temData1 + "." + temData2;
		} catch (Exception e) {
			return strData;
		}
	}

	/**
	 *메서드명 : setAddDate
	 *메서드 기능 : String으로 받은 날짜를 int로 넘어온 날짜로 더한다.
	 *PARAM pDate : 년월일을 더할 기준값.
	 *PARAM pYy, pMm,pDd, pHh, pMi : Int형으로 더할 년월일시분
	 *RETURN VALUE : string
	*/
	static public String setAddDate(String pDate, int pYy, int pMm, int pDd, int pHh, int pMi) {
		int yy = 0;
		int mm = 0;
		int dd = 0;
		int hh = 0;
		int mi = 0;

		String result = "";

		if (pDate.length() == 14) {
			yy = Integer.parseInt(pDate.substring( 0,  4));
			mm = Integer.parseInt(pDate.substring( 4,  6));
			dd = Integer.parseInt(pDate.substring( 6,  8));
			hh = Integer.parseInt(pDate.substring( 8, 10));
			mi = Integer.parseInt(pDate.substring(10, 12));

			DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

			//Calendar 에서는 1월부터 12월을 주소값으로 0부터 11까지 가지고 있으므로 실제 월에서 -1을 해준다.
			mm--;

			Calendar cal = Calendar.getInstance(Locale.KOREAN);

			//기준일로 세팅
			cal.set(yy, mm, dd, hh, mi);

			//기준일에 파라미터로 넘어온 년월일시분을 더해준다.
			cal.add(Calendar.YEAR  , pYy);
			cal.add(Calendar.MONTH , pMm);
			cal.add(Calendar.DATE  , pDd);
			cal.add(Calendar.HOUR  , pHh);
			cal.add(Calendar.MINUTE, pMi);

			result = format.format(cal.getTime()) + "00";
		}

		return result;
	}

	 /**
	 * 수치데이터 확인
	 * @param gdReq
	 * @return gdRes
	 */
	public String getInterFlag(String intData) {
		try {
			new Integer(intData);

			return "Y";
		} catch (Exception e) {
			return "N";
		}
	}

	/**
	 * 길이 만큼 Char 추가
	 * @param len 추가할 길이
	 * @param chr 추가 Char
	 * @return 가공하여 Return
	 */
	public String addStr(int len, String chr) {
		StringBuffer sb = new StringBuffer();

		for (int ii = 0; ii < len; ii++) {
			sb = sb.append(chr);
		}

		return substr(sb.toString(), 0, len);
	}

	/**
	 * String 길이 만큼 우측에 Char 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @param chr 추가 Char
	 * @return 가공하여 Return
	 */
	public String getRPad(String src, int len, String chr) {
		String ret = "";                  //Return String
		int sLen = src.getBytes().length; //원본 길이

		if (sLen < len) {
			ret = src + addStr(len - sLen, chr);
		} else if (sLen > len) {
			ret = substr(src, 0, len);
		} else {
			ret = src;
		}

		return ret;
	}

	/**
	 * String 길이 만큼 좌측에 Char 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @param chr 추가 Char
	 * @return 가공하여 Return
	 */
	public String getLPad(String src, int len, String chr) {
		String ret = "";                  //Return String
		int sLen = src.getBytes().length; //원본 길이

		if (sLen < len) {
			ret = addStr(len - sLen, chr) + src;
		} else if (sLen > len) {
			ret = substr(src, sLen - len, len);
		} else {
			ret = src;
		}

		return ret;
	}

	/**
	 * String 길이 만큼 우측에 " " 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @return 가공하여 Return
	 */
	public String getRPadSpc(String src, int len) {
		return getRPad(src, len, " ");
	}

	/**
	 * String 길이 만큼 좌측에 "0" 추가
	 * @param src 현재 값
	 * @param len 가공후 길이
	 * @return 가공하여 Return
	 */
	public String getLPadZero(String src, int len) {
		return getLPad(src, len, "0");
	}

	/* Date Format : "yyyyMMddHHmmss" */
	public String getDateTime14() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyyMMddHHmmss");
	}

	/* Date Format : "yyyy-MM-ddHH:mm:ss" */
	public String getDateTime18() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyy-MM-ddHH:mm:ss");
	}

	/* Date Format : "yyyy-MM-dd HH:mm:ss" */
	public String getDateTime19() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");
	}

	/* Date Format : "yyyyMMdd" */
	public String getDate8() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyyMMdd");
	}

	/* Date Format : "yyyy-MM-dd" */
	public String getDate10() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyy-MM-dd");
	}

	/* Date Format : "HHmmss" */
	public String getTime6() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmss");
	}

	/* Date Format : "HH:mm:ss" */
	public String getTime8() {
		return DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HH:mm:ss");
	}

	/**
	 * 한글을 2byte로 계산하여 길이 구하기
	 * @param String str
	 * @return
	 */
	public int getLength(String str) {
		return str.getBytes().length;
	}

	/**
	 * Char 단위 substr
	 * @param String strLine
	 * @param int start
	 * @param int len
	 * @return String
	 */
	public String substr(String strLine, int start, int  len) {
		byte[] bytes = strLine.getBytes();

		if (bytes == null || bytes.length <= start || len <= 0) {
			return "";
		}

		byte[] rbytes = new byte[len];

		for (int ii = 0; ii < len; ii++) {
			rbytes[ii] = bytes[start + ii];
		}

		return new String(rbytes);
	}

	/**
	 * Char 단위 substr
	 * @param String strLine
	 * @param int start
	 * @return
	 */
	public String substr(String strLine, int start) {
		return substr(strLine, start, strLine.getBytes().length);
	}

	/**
	 * String Array를 String으로 변환
	 * @param String[] arrStr
	 * @return String
	 */
	public String toString(String[] arrStr) {
		StringBuffer sb = new StringBuffer();
		int aLen = arrStr.length;
		
		if (aLen > 0) {
			sb = sb.append(arrStr[0]);
		}

		for (int ii = 1; ii < aLen; ii++) {
			sb = sb.append(", " + arrStr[ii]);
		}

		return sb.toString();
	}

	/**
	 *  14자리 12자리 8자리 String 형식의 날짜를 입력받아 날짜 범위가 올바른지 판단*
	 * @param strDate
	 * @return boolean
	 */
	public boolean checkDateFormat(String strDate) {
		int year = 0;
		int mon  = 0;
		int day  = 0;
		int hour = 0;
		int min  = 0;
		int sec  = 0;
		int lastDay = 0;
		Date tmpDate = new Date();

		try {
			if (strDate.length() == 14) {
				year = StringHelper.parseInt(strDate.substring( 0, 4), 0);
				mon  = StringHelper.parseInt(strDate.substring( 4, 6), 0);
				day  = StringHelper.parseInt(strDate.substring( 6, 8), 0);
				hour = StringHelper.parseInt(strDate.substring( 8,10),-1);
				min  = StringHelper.parseInt(strDate.substring(10,12),-1);
				sec  = StringHelper.parseInt(strDate.substring(12,14),-1);
				tmpDate = DateHelper.toUtilDate(year, mon, day, hour, min, sec);
			} else if (strDate.length() == 12) {
				year = StringHelper.parseInt(strDate.substring( 0, 4), 0);
				mon  = StringHelper.parseInt(strDate.substring( 4, 6), 0);
				day  = StringHelper.parseInt(strDate.substring( 6, 8), 0);
				hour = StringHelper.parseInt(strDate.substring( 8,10),-1);
				min  = StringHelper.parseInt(strDate.substring(10,12),-1);
				tmpDate = DateHelper.toUtilDate(year, mon, day, hour, min);
			} else if (strDate.length() == 8) {
				year = StringHelper.parseInt(strDate.substring(0,4),0);
				mon  = StringHelper.parseInt(strDate.substring(4,6),0);
				day  = StringHelper.parseInt(strDate.substring(6,8),0);
				tmpDate = DateHelper.toUtilDate(year, mon, day);
			} else {
				return false;
			}

			lastDay = DateHelper.lastDay(tmpDate);
		} catch (Exception e) {
			return false;
		}

		if (year < 1000) {
			return false;
		} else if (mon < 1 || mon > 12) {
			return false;
		} else if (day < 1 || day > lastDay) {
			return false;
		} else if (hour < 0 || hour > 23) {
			return false;
		} else if (min < 0 || min > 59) {
			return false;
		} else if (sec < 0 || sec > 59) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 해당 값이 있는지를 Check
	 * @param String[] arrStr
	 * @param String str
	 * @return boolean
	 */
	public boolean chkExist(String[] arrStr, String str) {
		boolean chkRst = false;

		if (arrStr != null && !"".equals(str)) {
			int arrCnt = arrStr.length;
		
			for (int ii = 0; ii < arrCnt; ii++) {
				if (arrStr[ii] != null && str.equals(arrStr[ii])) {
					chkRst = true;
					break;
				}
			}
		}

		return chkRst;
	}
	
	/**
	 * 숫자형 문자인지를 Check
	 * @param String str
	 * @return boolean
	 */
	public boolean isNumber(String str) {
		Pattern p = Pattern.compile("[\\d]");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 *      [A] 오퍼레이션명 : Logging 을 위한 ID 생성
	 *
	 *      @return String
	*/
	public String getLogId() {
		return "[J]<" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
	}

	/**
	 *      [A] 오퍼레이션명 : 상위 Method 명, Logging 을 위한 ID 및 수정자를 Set
	 *
	 *      @param JDTORecord jrParam
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String modifier
	 *      @return JDTORecord
	*/
	public JDTORecord getParam(String logId, String methodNm, String modifier) {
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		try {
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			if (!"".equals(modifier)) {
				jrParam.setField("MODIFIER", modifier);	//수정자
			}
		} catch(Exception e) {}
		return jrParam;
	}

	/**
	 *      [A] 오퍼레이션명 : HashMap을 JDTORecord로 변환하고
	 *                       상위 Method 명, Logging 을 위한 ID Set
	 *
	 *      @param JDTORecord jrParam
	 *      @param String logId
	 *      @param String methodNm
	 *      @param HashMap hmReq
	 *      @return JDTORecord
	*/
	public JDTORecord getParam(String logId, String methodNm, HashMap hmReq) {
		//Logging
		printLog(logId, methodNm, "F+");

		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		try {
			jrParam = this.hashMapTojdtoRecord(hmReq);
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
		} catch(Exception e) {}

		return jrParam;
	}

	/**
	 *      [A] 오퍼레이션명 : 1 Line Error Log 만들기
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @return String
	*/
	public String makeErrorLine(String logId, String logMsg) {
		return "\n" + logId + " ■Error■ " + logMsg;
	}

	/**
	 *      [A] 오퍼레이션명 : 1 Line Warning Log 만들기
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @return String
	*/
	public String makeWarnLine(String logId, String logMsg) {
		return logId + " ■Warning■ " + logMsg;
	}

	/**
	 *      [A] 오퍼레이션명 : Error Log 만들기
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String errMsg
	 *      @return String
	*/
	public String makeErrorLog(String logId, String methodNm, String errMsg) {
		return makeErrorLine(logId, "Method  : " + methodNm) + makeErrorLine(logId, "Message : " + errMsg);
	}

	/**
	 *      [A] 오퍼레이션명 : Exception Error Log 만들기
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Exception e
	 *      @return String
	*/
	public String makeErrorLog(String logId, String methodNm, Exception e) {
		return makeErrorLine(logId, "Method  : " + methodNm) + makeErrorLine(logId, "Message : " + e.getMessage());
	}

	
	/**
	 *      [A] 오퍼레이션명 : Debug Logging
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @param String logGp ([시작, 종료] + 구분)
	 *      @return void
	*/
	public void printLog(String logId, String logMsg, String logGp) {
		String prnLog = "";

		if (isEmpty(logGp)) {
			prnLog = logMsg;
		} else if (logGp.endsWith("+")) {
			prnLog = "▼" + logGp + "▼ " + logMsg;
		} else if (logGp.endsWith("-")) {
			prnLog = "▲" + logGp + "▲ " + logMsg;
		} else {
			prnLog = "●" + logGp + "● " + logMsg;
		}

		logger.println(LogLevel.DEBUG, logId + " " + prnLog);
	}
	
	/**
	 *      [A] 오퍼레이션명 : Debug Logging
	 *
	 *      @param String ydGp   야드구분
	 *      @param String logId
	 *      @param String logMsg
	 *      @param String logGp ([시작, 종료] + 구분)
	 *      @return void
	*/	
/*	public void printLog(String ydGp, String logId, String logMsg, String logGp) {
		String prnLog = "";

		if (isEmpty(logGp)) {
			prnLog = logMsg;
		} else if (logGp.endsWith("+")) {
			prnLog = "▼" + logGp + "▼ " + logMsg;
		} else if (logGp.endsWith("-")) {
			prnLog = "▲" + logGp + "▲ " + logMsg;
		} else {
			prnLog = "●" + logGp + "● " + logMsg;
		}
		
		logger.print(LogLevel.DEBUG, "[" + ydGp + "]" +logId + " " + prnLog);
	}	
*/
	/**
	 *      [A] 오퍼레이션명 : Debug Logging
	 *                        Method 시작시 Logging 및 gdReq 에 상위 Method 명, Logging 을 위한 ID 를 Set
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @param String logGp (Main 시작, Sub 시작)
	 *      @return void
	*/
/*	public void printLog(String logId, String logMsg, String logGp, GridData gdReq) {
		//Logging
		printLog(logId, logMsg, logGp);

		if (logGp.endsWith("+")) {
			gdReq.setNavigateValue(logMsg);	//상위 Method 명

			if ("F+".equals(logGp)) {
				gdReq.setIPAddress(logId);	//Logging 을 위한 ID
			}
		}
	}*/

	/**
	 *      [A] 오퍼레이션명 : Debug Logging
	 *                        Method 시작시 Logging 및 gdReq 에 상위 Method 명, Logging 을 위한 ID 를 Set
	 *
	 *      @param String logId
	 *      @param String logMsg
	 *      @param String logGp (Main 시작, Sub 시작)
	 *      @return void
	*/
/*	public void printLog(String logId, String logMsg, String logGp, JDTORecord jrParam) {
		//Logging
		printLog(logId, logMsg, logGp);

		try {
			if (logGp.endsWith("+")) {
				jrParam.setResultMsg(logMsg);		//상위 Method 명
	
				if ("F+".equals(logGp)) {
					jrParam.setResultCode(logId);	//Logging 을 위한 ID
				}
			}
		} catch(Exception e) {}
	}*/

	/**
	 *      [A] 오퍼레이션명 : Default Error Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Object caller
	 *      @param Exception e
	 *      @return void
	*/
	public void printErrorLog(String logId, String methodNm, Object caller, Exception e) {
		logger.println(LogLevel.ERROR, makeErrorLine(logId, "Method  : " + methodNm));
		logger.println(LogLevel.ERROR, caller, "\n", e);
		logger.println(LogLevel.ERROR, logId + " ▲Error▲ " + methodNm);
	}

	/**
	 *      [A] 오퍼레이션명 : Error Message 있는 Error Logging
	 *
	 *      @param String ErrMsg
	 *      @param Object caller
	 *      @param Exception e
	 *      @return void
	*/
	public void printErrorLog(String ErrMsg, Object caller, Exception e) {
		logger.println(LogLevel.ERROR, caller, ErrMsg + "\n", e);
	}

	/**
	 *      [A] 오퍼레이션명 : Error Message 있는 Error Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String ErrMsg
	 *      @param Object caller
	 *      @param Exception e
	 *      @return void
	*/
	public void printErrorLog(String logId, String methodNm, String ErrMsg, Object caller, Exception e) {
		logger.println(LogLevel.ERROR, caller, ErrMsg + "\n", e);
		logger.println(LogLevel.ERROR, logId + " ▲Error▲ " + methodNm);
	}

	/**
	 *      [A] 오퍼레이션명 : Warning Message Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String WarnMsg
	 *      @return void
	*/
	public void printWarnLog(String logId, String methodNm, String WarnMsg) {
		logger.println(LogLevel.WARNING, makeWarnLine(logId, "Method  : " + methodNm));
		logger.println(LogLevel.WARNING, makeWarnLine(logId, "Message : " + WarnMsg ));
	}

	/**
	 *      [A] 오퍼레이션명 : SQL Error Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String jspeed_query_id
	 *      @param Object[] param
	 *      @param Exception e
	 *      @return String
	*/
	public String makeSqlErrorLog(String logId, String methodNm, String jspeed_query_id, Object[] param, Exception e) {
		StringBuffer sb = new StringBuffer();

		try {
			sb = sb.append(makeErrorLine(logId, "Method  : " + methodNm       ));
			sb = sb.append(makeErrorLine(logId, "Message : " + e.getMessage() ));
			sb = sb.append(makeErrorLine(logId, "QueryID : " + jspeed_query_id));

			if (!isEmpty(jspeed_query_id)) {
				sb = sb.append("\n" + QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) {
					int pLen = param.length;
					
					sb = sb.append("\n▩ {");
					for (int ii = 0; ii < pLen; ii++) {
						if (ii > 0) { sb = sb.append(", "); }
						sb = sb.append(ii + "=" + param[ii].toString());
					}
					sb = sb.append("}");
				}
			} else {
				sb = sb.append(makeErrorLine(logId, "jspeed_query_id is null"));
			}
		} catch (Exception ex) {
			sb = sb.append(makeErrorLine(logId, "jSpeed Query Service에 등록되지 않은 jspeed_query_id 입니다."));
		}
		sb = sb.append("\n");

		return sb.toString();
	}

	/**
	 *      [A] 오퍼레이션명 : SQL Error Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String jspeed_query_id
	 *      @param Object[][] param
	 *      @param Exception e
	 *      @return String
	*/
	public String makeSqlErrorLog(String logId, String methodNm, String jspeed_query_id, Object[][] param, Exception e) {
		StringBuffer sb = new StringBuffer();

		try {
			sb = sb.append(makeErrorLine(logId, "Method  : " + methodNm       ));
			sb = sb.append(makeErrorLine(logId, "Message : " + e.getMessage() ));
			sb = sb.append(makeErrorLine(logId, "QueryID : " + jspeed_query_id));

			if (!isEmpty(jspeed_query_id)) {
				sb = sb.append("\n" + QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) {
					int pLen1 = param.length;
					int pLen2 = 0;

					for (int ii = 0; ii < pLen1; ii++) {
						pLen2 = param[ii].length;
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
						for (int jj = 0; jj < pLen2; jj++) {
							if (jj > 0) { sb = sb.append(", "); }
							sb = sb.append(jj + "=" + param[ii][jj].toString());
						}
						sb = sb.append("}");
					}
				}
			} else {
				sb = sb.append(makeErrorLine(logId, "jspeed_query_id is null"));
			}
		} catch (Exception ex) {
			sb = sb.append(makeErrorLine(logId, "jSpeed Query Service에 등록되지 않은 jspeed_query_id 입니다."));
		}
		sb = sb.append("\n");

		return sb.toString();
	}

	/**
	 *      [A] 오퍼레이션명 : SQL Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String jspeed_query_id
	 *      @param Object[] param
	 *      @return void
	*/
	public void printSqlLog(String logId, String logMsg, String jspeed_query_id, Object[] param) {
		try {
			if (!isEmpty(jspeed_query_id)) {
				StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");
				sb = sb.append("\n▩ " + jspeed_query_id + "\n");
				sb = sb.append(QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) {
					int pLen = param.length;

					sb = sb.append("\n▩ {");
					for (int ii = 0; ii < pLen; ii++) {
						if (ii > 0) { sb = sb.append(", "); }
						sb = sb.append(ii + "=" + param[ii].toString());
					}
					sb = sb.append("}");
				}

				sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

				logger.println(LogLevel.DEBUG, sb.toString());
			}
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, logId + " ■□■□■ Error : " + jspeed_query_id  + " << " + logMsg);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : SQL Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String jspeed_query_id
	 *      @param Object[][] param
	 *      @return void
	*/
	public void printSqlLog(String logId, String logMsg, String jspeed_query_id, Object[][] param) {
		try {
			if (!isEmpty(jspeed_query_id)) {
				StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");
				sb = sb.append("\n▩ " + jspeed_query_id + "\n");
				sb = sb.append(QueryService.getInstance().getSQL(jspeed_query_id));

				if (param != null) {
					int pLen1 = param.length;
					int pLen2 = 0;

					for (int ii = 0; ii < pLen1; ii++) {
						pLen2 = param[ii].length;
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
						for (int jj = 0; jj < pLen2; jj++) {
							if (jj > 0) { sb = sb.append(", "); }
							sb = sb.append(jj + "=" + param[ii][jj].toString());
						}
						sb = sb.append("}");
					}
				}

				sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

				logger.println(LogLevel.DEBUG, sb.toString());
			}
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, logId + " ■□■□■ Error : " + jspeed_query_id  + " << " + logMsg);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : DB DML Parameter Logging
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Object[][] param
	 *      @return void
	*/
	public void printParam(String logId, String logMsg, String[][] param) {
		try {
			int pLen1 = param.length;
			int pLen2 = 0;
			StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			for (int ii = 0; ii < pLen1; ii++) {
				pLen2 = param[ii].length;
				sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
				for (int jj = 0; jj < pLen2; jj++) {
					if (jj > 0) { sb = sb.append(", "); }
					sb = sb.append(jj + "=" + param[ii][jj].toString());
				}
				sb = sb.append("}");
			}

			sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : DB DML Parameter Logging - 삭제 대상
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Object[][] param
	 *      @param int[] trtRst
	 *      @return void
	*/
	public void printParam(String logId, String logMsg, Object[][] param, int[] trtRst) {
		try {
			int pLen1 = param.length;
			int pLen2 = 0;
			StringBuffer sb = new StringBuffer("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			for (int ii = 0; ii < pLen1; ii++) {
				pLen2 = param[ii].length;
				sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : {");
				for (int jj = 0; jj < pLen2; jj++) {
					if (jj > 0) { sb = sb.append(", "); }
					sb = sb.append(jj + "=" + param[ii][jj].toString());
				}
				sb = sb.append("}");
			}

			sb = sb.append("\n▩▩ " + logId + " " + logMsg + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Parameter Logging
	 *
	 *      @param String paramNm
	 *      @param Object obj
	 *      @return void
	*/
	public void printParam(String paramNm, Object obj) {
		if (obj == null) { return; }

		try {
			int pLen1 = 0;
			int pLen2 = 0;
			StringBuffer sb = new StringBuffer("\n▩▩ " + paramNm + " ▩▩");

			if (obj instanceof JDTORecord) {
				JDTORecord param = (JDTORecord)obj;
				Iterator itr = param.iterateName();

				String key = "";
				StringBuffer sb1 = new StringBuffer();
				StringBuffer sb2 = new StringBuffer();
				while (itr.hasNext()) {
					key = (String)itr.next();
					Object obj2 = param.getField(key);

					if (obj2 instanceof JDTORecord) {
						JDTORecord param2 = (JDTORecord)obj2;
						sb2 = sb2.append("\n▩ " + key + " : " + param2.toString());
					} else if (obj2 instanceof JDTORecordSet) {
						JDTORecordSet param2 = (JDTORecordSet)obj2;
						pLen2 = param2.size();
						for (int jj = 0; jj < pLen2; jj++) {
							sb2 = sb2.append("\n▩ " + key + "-" + formatMaxNo(jj, pLen2) + " : " + param2.getRecord(jj).toString());
						}
					} else {
						if (sb1.length() > 0) { sb1 = sb1.append(", "); }
						sb1 = sb1.append(key + "=" + obj2.toString());
					}
				}

				if (sb1.length() > 0) {
					sb = sb.append("\n▩ {");
					sb = sb.append(sb1);
					sb = sb.append("}");
				}
				
				if (sb2.length() > 0) {
					sb = sb.append(sb2);
				}
			} else if (obj instanceof JDTORecord[]) {
				JDTORecord[] param = (JDTORecord[])obj;
				pLen1 = param.length;
				for (int ii = 0; ii < pLen1; ii++) {
					sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + param[ii].toString());
				}
			} else if (obj instanceof JDTORecordSet) {
				JDTORecordSet param = (JDTORecordSet)obj;
				pLen1 = param.size();
				for (int ii = 0; ii < pLen1; ii++) {
					sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + param.getRecord(ii).toString());
				}
			} else if (obj instanceof Vector) {
				Vector param = (Vector)obj;
				pLen1 = param.size();
				for (int ii = 0; ii < pLen1; ii++) {
					Object obj2 = param.get(ii);
					if (obj2 instanceof JDTORecord) {
						JDTORecord param2 = (JDTORecord)obj2;
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + param2.toString());
					} else if (obj2 instanceof JDTORecordSet) {
						JDTORecordSet param2 = (JDTORecordSet)obj2;
						pLen2 = param2.size();
						for (int jj = 0; jj < pLen2; jj++) {
							sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + "-" + formatMaxNo(jj, pLen2) + " : " + param2.getRecord(jj).toString());
						}
					} else {
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + obj.toString());
					}
				}
			} else if (obj instanceof Object[]) {
				Object[] param = (Object[])obj;
				pLen1 = param.length;
				sb = sb.append("\n▩ {");
				for (int ii = 0; ii < pLen1; ii++) {
					if (ii > 0) { sb = sb.append(", "); }
					sb = sb.append(ii + "=" + param[ii].toString());
				}
				sb = sb.append("}");
			} else {
				sb = sb.append("\n▩ " + obj.toString());
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Parameter Logging
	 *
	 *      @param String paramNm
	 *      @param Object obj
	 *      @param String prnItm
	 *      @return void
	*/
	public void printParam(String paramNm, Object obj, String prnItm) {
		if (obj == null) { return; }

		try {
			if (prnItm == null || "".equals(prnItm) ||
				!(obj instanceof JDTORecord         ||
			      obj instanceof JDTORecord[]       ||
				  obj instanceof JDTORecordSet      ||
				  obj instanceof Vector)) {
				printParam(paramNm, obj);
				return;
			}

			StringTokenizer st = new StringTokenizer(prnItm, ";");
			int itmCnt = st.countTokens();
			String[] arrItm = new String[itmCnt];
			
			for (int ii = 0; ii < itmCnt; ii++) {
				arrItm[ii] = st.nextToken();
			}

			int pLen1 = 0;
			
			StringBuffer sb = new StringBuffer("\n▩▩ " + paramNm + " ▩▩");
			
			if (obj instanceof JDTORecord) {
				JDTORecord param = (JDTORecord)obj;
				Iterator itr = param.iterateName();

				String key = "";
				StringBuffer sb1 = new StringBuffer();
				StringBuffer sb2 = new StringBuffer();
				while (itr.hasNext()) {
					key = (String)itr.next();
					Object obj2 = param.getField(key);

					if (obj2 instanceof JDTORecordSet) {
						sb2 = sb2.append(this.getParamJs((JDTORecordSet)obj2, arrItm, key, false));
					} else if (obj2 instanceof JDTORecord) {
						sb2 = sb2.append(this.getParamJr((JDTORecord)obj2, arrItm, key));
					} else  {
						if (sb1.length() > 0) { sb1 = sb1.append(", "); }
						sb1 = sb1.append(key + "=" + obj2.toString());
					}
				}

				if (sb1.length() > 0) {
					sb = sb.append("\n▩ {");
					sb = sb.append(sb1);
					sb = sb.append("}");
				}
				
				if (sb2.length() > 0) {
					sb = sb.append(sb2);
				}
			} else if (obj instanceof JDTORecord[]) {
				sb = sb.append(getParamJa((JDTORecord[])obj, arrItm, ""));
			} else if (obj instanceof JDTORecordSet) {
				sb = sb.append(getParamJs((JDTORecordSet)obj, arrItm, "", true));
			} else if (obj instanceof Vector) {
				Vector param = (Vector)obj;
				pLen1 = param.size();
				for (int ii = 0; ii < pLen1; ii++) {
					Object obj2 = param.get(ii);
					if (obj2 instanceof JDTORecordSet) {
						if (ii == 0) {
							sb = sb.append(this.getParamJs((JDTORecordSet)obj2, arrItm, formatMaxNo(ii, pLen1), true));
						} else {
							sb = sb.append(this.getParamJs((JDTORecordSet)obj2, arrItm, formatMaxNo(ii, pLen1), false));
						}
					} else if (obj2 instanceof JDTORecord) {
						sb = sb.append(this.getParamJr((JDTORecord)obj2, arrItm, formatMaxNo(ii, pLen1)));
					} else {
						sb = sb.append("\n▩ " + formatMaxNo(ii, pLen1) + " : " + obj.toString());
					}
				}
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Parameter Logging
	 *
	 *      @param String paramNm
	 *      @param Object obj
	 *      @param String prnItm
	 *      @return void
	*/
	public void printParam(String paramNm, Object[][] obj) {
		if (obj == null) { return; }

		try {
			String itmVal = "";
			int rowCnt = obj.length;
			int itmCnt = obj[0].length;
			int itmLen = 0;
			int[] arrLen = new int[itmCnt];
			
			for (int jj = 0; jj < itmCnt; jj++) {
				arrLen[jj] = 4;
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = obj[ii][jj].toString();
					if (itmVal != null && !"".equals(itmVal)) {
						itmLen = itmVal.getBytes().length;
						if (itmLen > arrLen[jj]) {
							arrLen[jj] = itmLen;
						}
					}
				}
			}

			StringBuffer sb = new StringBuffer("\n▩▩ " + paramNm + " ▩▩");

			sb = sb.append("\n▩ ----- ");

			for (int jj = 0; jj < itmCnt; jj++) {
				sb = sb.append(this.getRPad(String.valueOf(jj), arrLen[jj], "-") + " ");
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				sb = sb.append("\n▩ " + this.format(ii, 3) + " : ");
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = obj[ii][jj].toString();
					if (itmVal == null || "".equals(itmVal)) {
						sb = sb.append(this.getRPad(" ", arrLen[jj] + 1, " "));
					} else {
						sb = sb.append(this.getRPad(itmVal, arrLen[jj] + 1, " "));
					}
				}
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");

			logger.println(LogLevel.DEBUG, sb.toString());
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : JDTORecordSet Parameter Logging을 위한 String 만들기
	 *
	 *      @param JDTORecordSet jsParam
	 *      @param String[] arrItm
	 *      @param String prefix
	 *      @param boolean titleYn
	 *      @return StringBuffer
	*/
	public StringBuffer getParamJs(JDTORecordSet jsParam, String[] arrItm, String prefix, boolean titleYn) {
		StringBuffer sb = new StringBuffer();

		try {
			int itmLen = 0; //항목값길이
			int rowCnt = jsParam.size();
			String itmVal = "";
			JDTORecord jrRow = null;

			int itmCnt = arrItm.length;
			int[] arrLen = new int[itmCnt];

			for (int jj = 0; jj < itmCnt; jj++) {
				arrLen[jj] = arrItm[jj].getBytes().length;
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsParam.getRecord(ii);
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = jrRow.getFieldString(arrItm[jj]);
					if (itmVal != null && !"".equals(itmVal)) {
						itmLen = itmVal.getBytes().length;
						if (itmLen > arrLen[jj]) {
							arrLen[jj] = itmLen;
						}
					}
				}
			}

			//접두사길이 결정
			itmLen = 5;
			String preStr = "";
			if (prefix != null && !"".equals(prefix)) {
				preStr = prefix + "-";
				int len1 = preStr.getBytes().length;
				int len2 = (String.valueOf(rowCnt)).length();
				if (len1 + len2 > 5) {
					sb = sb.append("\n▩ " + prefix);
					preStr = "";
				} else if (len1 + len2 < 5) {
					itmLen = 5 - len1;
				}
			}

			if (titleYn) {
				sb = sb.append("\n▩ Title : ");
	
				for (int jj = 0; jj < itmCnt; jj++) {
					sb = sb.append(this.getRPad(arrItm[jj], arrLen[jj] + 1, " "));
				}
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsParam.getRecord(ii);
				sb = sb.append("\n▩ " + preStr + format(ii, itmLen) + " : ");
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = jrRow.getFieldString(arrItm[jj]);
					if (itmVal == null || "".equals(itmVal)) {
						sb = sb.append(this.getRPad(" ", arrLen[jj] + 1, " "));
					} else {
						sb = sb.append(this.getRPad(jrRow.getFieldString(arrItm[jj]), arrLen[jj] + 1, " "));
					}
				}
			}
			
			return sb;
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sb;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : JDTORecordSet Parameter Logging을 위한 String 만들기
	 *
	 *      @param JDTORecord[] jaParam
	 *      @param String[] arrItm
	 *      @param String prefix
	 *      @return StringBuffer
	*/
	public StringBuffer getParamJa(JDTORecord[] jaParam, String[] arrItm, String prefix) {
		StringBuffer sb = new StringBuffer();

		try {
			int itmLen = 0; //항목값길이
			int rowCnt = jaParam.length;
			String itmVal = "";
			JDTORecord jrRow = null;

			int itmCnt = arrItm.length;
			int[] arrLen = new int[itmCnt];
			
			for (int jj = 0; jj < itmCnt; jj++) {
				arrLen[jj] = arrItm[jj].getBytes().length;
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jaParam[ii];
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = jrRow.getFieldString(arrItm[jj]);
					if (itmVal != null && !"".equals(itmVal)) {
						itmLen = itmVal.getBytes().length;
						if (itmLen > arrLen[jj]) {
							arrLen[jj] = itmLen;
						}
					}
				}
			}

			//접두사길이 결정
			itmLen = 5;
			String preStr = "";
			if (prefix != null && !"".equals(prefix)) {
				preStr = prefix + "-";
				int len1 = preStr.getBytes().length;
				int len2 = (String.valueOf(rowCnt)).length();
				if (len1 + len2 > 5) {
					sb = sb.append("\n▩ " + prefix);
					preStr = "";
				} else if (len1 + len2 < 5) {
					itmLen = 5 - len1;
				}
			}

			sb = sb.append("\n▩ Title : ");

			for (int jj = 0; jj < itmCnt; jj++) {
				sb = sb.append(this.getRPad(arrItm[jj], arrLen[jj] + 1, " "));
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jaParam[ii];
				sb = sb.append("\n▩ " + preStr + format(ii, itmLen) + " : ");
				for (int jj = 0; jj < itmCnt; jj++) {
					itmVal = jrRow.getFieldString(arrItm[jj]);
					if (itmVal == null || "".equals(itmVal)) {
						sb = sb.append(this.getRPad(" ", arrLen[jj] + 1, " "));
					} else {
						sb = sb.append(this.getRPad(jrRow.getFieldString(arrItm[jj]), arrLen[jj] + 1, " "));
					}
				}
			}
			
			return sb;
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sb;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : JDTORecord Parameter Logging을 위한 String 만들기
	 *
	 *      @param JDTORecord[] jaParam
	 *      @param String[] arrItm
	 *      @param String prefix
	 *      @return StringBuffer
	*/
	public StringBuffer getParamJr(JDTORecord jrParam, String[] arrItm, String prefix) {
		StringBuffer sb = new StringBuffer();

		try {
			int itmCnt = arrItm.length;

			sb = sb.append("\n▩ " + prefix + " : {");

			for (int jj = 0; jj < itmCnt; jj++) {
				sb = sb.append(arrItm[jj] + "=" + trim(jrParam.getFieldString(arrItm[jj])));
				if (jj < itmCnt - 1) { sb = sb.append(", "); }
			}

			sb = sb.append("}");
			
			return sb;
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sb;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 수신 전문의 MSG_ID를 추출
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return String
	*/
	public String getMsgId(JDTORecord rcvMsg) {
		String msgId = ""; //인터페이스ID

		try {
			//JMS일 경우는 JMS_TC_CD
			msgId = trim(rcvMsg.getFieldString("JMS_TC_CD"));

			//EAI일 경우는 MSG_ID
			if (isEmpty(msgId)) {
				msgId = trim(rcvMsg.getFieldString("MSG_ID"));
			}

//PIDEV
			// MQ_TC_CD
			if (isEmpty(msgId)) {
				msgId = trim(rcvMsg.getFieldString("MQ_TC_CD"));
			}			
			
			//기타(출하관리 등)일 경우는 TC_CODE
			if (isEmpty(msgId)) {
				msgId = trim(rcvMsg.getFieldString("TC_CODE"));
			}

			return msgId;
		} catch (Exception e) {
			return msgId;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecord jrExt
	 *      @param JDTORecordSet jsAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecord jrExt, JDTORecordSet jsAdd) {
		try {
			//추가할 전문이 없으면 기존 그대로
			if (isEmpty(jsAdd)) {
				return jrExt;
			}

			//Return Data
			JDTORecordSet rtnData = JDTORecordFactory.getInstance().createRecordSet("");

			//기존 전문이 있으면 기존 먼저 추가
			if (!isEmpty(jrExt)) {
				JDTORecordSet extData = (JDTORecordSet)jrExt.getField("SEND_DATA");

				if (!isEmpty(extData)) {
					rtnData.addAll(extData);
				}
			}

			//추가할 전문 추가
			rtnData.addAll(jsAdd);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			jrRtn.addField("SEND_DATA", rtnData);

			return jrRtn;
		} catch (Exception e) {
			return jrExt;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecord jrExt
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecord jrExt, JDTORecord jrAdd) {
		try {
			//추가할 전문이 없으면 기존 그대로
			if (isEmpty(jrAdd)) {
				return jrExt;
			}

			JDTORecordSet addData = null;

			//I/F ID를 먼저 Check
			String msgId = this.getMsgId(jrAdd);

			if (!isEmpty(msgId)) {
				//I/F ID가 존재할 경우는 전문 1건 추가
				addData = JDTORecordFactory.getInstance().createRecordSet("");
				addData.addRecord(jrAdd);
			} else {
				//SEND_DATA로 있을 경우
				addData = (JDTORecordSet)jrAdd.getField("SEND_DATA");
			}

			return addSndData(jrExt, addData);
		} catch (Exception e) {
			return jrExt;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecordSet jsAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecordSet jsAdd) {
		try {
			return addSndData(null, jsAdd);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecord jrAdd) {
		try {
			return addSndData(null, jrAdd);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Grid에서 값 추출하기
	 *
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public String getValue(GridData gdReq, String headerNm, int ii) {
		try {
			String rtnValue;
			if (gdReq.getHeader(headerNm).getDataType().equals(OperateGridData.t_combo)) {
				rtnValue = this.nvl(this.trim(gdReq.getHeader(headerNm).getComboHiddenValues()[gdReq.getHeader(headerNm).getSelectedIndex(ii)]),"");
			} else if (gdReq.getHeader(headerNm).getDataType().equals(OperateGridData.t_number)) {	
				rtnValue = this.nvl(this.trim(gdReq.getHeader(headerNm).getValue(ii)),"0");
			} else {
				rtnValue = this.nvl(this.trim(gdReq.getHeader(headerNm).getValue(ii)),"");
			}
			return rtnValue; 
		} catch (Exception e) {
			return "";
		}
	}
	/**
	 *      [A] 오퍼레이션명 : stringPlusInt 
	 * 
	 * @param  String szPara         // 문자 값
	 *         int intPara           // 숫자 값
	 * @return String			     // 계산결과 문자열
	 */
	public String stringPlusInt(String szPara, int intPara) {
		String szRtnVal = null;
		int intTemp = 0;
		
		try{
		intTemp = Integer.parseInt(szPara) + intPara;
		}catch(Exception e){
		}
		if (intTemp < 10)
			szRtnVal = "00" + intTemp;
		else if (intTemp > 9 && intTemp < 100) 
			szRtnVal = "0" + intTemp;
		return szRtnVal;
	} // end of stringPlusInt
	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNullLong
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return long			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
/*	public long paraRecChkNullLong(JDTORecord recPara, String szFieldName) throws JDTOException {
		long lngRtnVal;
		
		if (recPara.getFieldString(szFieldName) == null)
			lngRtnVal = 0;
		else {
			if (recPara.getFieldString(szFieldName).trim().equals(""))
				lngRtnVal = 0;
			else
				lngRtnVal = Long.parseLong(recPara.getFieldString(szFieldName));
		}
		return lngRtnVal;
	} // end of paraRecChkNullLong
*/	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull_2 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
/*	public double paraRecChkNullDouble(JDTORecord recPara, String szFieldName) throws JDTOException {
		double dlRtnVal = 0;
		if (recPara.getField(szFieldName) == null)
			dlRtnVal = 0;
		else{
			if (recPara.getFieldString(szFieldName).trim().equals(""))
				dlRtnVal = 0;
			else
			dlRtnVal = Double.parseDouble(recPara.getFieldString(szFieldName));
		}
		
		return dlRtnVal;
	} // end of paraRecChkNull	
*/	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNullInt
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return int			         // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
/*	public int paraRecChkNullInt(JDTORecord recPara, String szFieldName) throws JDTOException {
		int intRtnVal;
		
		if (recPara.getFieldString(szFieldName) == null)
			intRtnVal = 0;
		else {
			
			if (recPara.getFieldString(szFieldName).trim().equals(""))
				intRtnVal = 0;
			else
				intRtnVal = recPara.getFieldInt(szFieldName);
		}
		
		return intRtnVal;
	} // end of paraRecChkNull
*/	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	/*public String paraRecChkNull(JDTORecord recPara, String szFieldName) throws JDTOException {
		String szRtnVal = null;
		if (recPara.getField(szFieldName) == null)
			szRtnVal = "";
		else
			szRtnVal = recPara.getFieldString(szFieldName).trim();				//임춘수 2009.04.24 수정 trim() 추가
		
		return szRtnVal;
	}*/
	/**
	 *      [A] 오퍼레이션명 : stringPlusInt2 
	 * 
	 * @param  String szPara         // 문자 값
	 *         int intPara           // 숫자 값
	 * @return String			     // 계산결과 문자열
	 */
	public String stringPlusInt2(String szPara, int intPara) {
		String szRtnVal = null;
		int intTemp = 0;
		try{
		intTemp = Integer.parseInt(szPara) + intPara;
		}catch(Exception e){
		}
		if (intTemp < 10)
			szRtnVal = "0" + intTemp;
		else if (intTemp > 9 && intTemp < 100)
			szRtnVal = "" + intTemp;
		return szRtnVal;
	} // end of stringPlusInt2	
	

//////////////////////////////////////////////////////////////////////////////////////	
// B 열연 추가
/////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * 두개의 JDTORecord를 하나의 JDTORecord로 합치는 method
	 * 
	 * @param map :
	 *            JDTORecord, JDTORecord
	 * 
	 * @return : JDTORecord
	 */
	public JDTORecord mixJDTORecord(JDTORecord a, JDTORecord b) {

		try {
			String key = "";

			Map mMap = b.getMap();
			Set set = mMap.keySet();
			Object[] hmKeys = set.toArray();
			for (int i = 0; i < hmKeys.length; i++) {
				key = (String) hmKeys[i];
				a.setField(key, (String) mMap.get(key));
			}
		} catch (Exception e) {

		}
		return a;
	}	
	/**
	 * LINE IN 작업인지를 판단한다.
     * @param  String
     * @return boolean
     * @throws 
     */	
	public static boolean isLineInWork(String sSchCode)
	{
	   boolean isTrue = false;
	   if ((sSchCode.substring(2,4).equals("DC") 
		 || sSchCode.substring(2,4).equals("FE")   
		 || sSchCode.substring(2,4).equals("KD")   
		 || sSchCode.substring(2,4).equals("HS")   
		 || sSchCode.substring(2,4).equals("KE")) 
		 && sSchCode.substring(6,7).equals("U")) {   
//	      YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode)||  // SPM 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CKTI.equals(sSchCode)||  // SPM Take In
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_EQLI.equals(sSchCode)||  // EQL 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_EQTI.equals(sSchCode)||  // EQL Take In
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CFTI.equals(sSchCode)||  // HFL Take In
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CFLI.equals(sSchCode)||  // HFL 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_SSLI.equals(sSchCode)||    // SCARFING 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CNLI.equals(sSchCode)||	// #2 SPM 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CNTI.equals(sSchCode)||	// #2 SPM Take In
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CHLI.equals(sSchCode)||	// #2 HFL 보급
//	   	  YmCommonConst.NEW_SCH_WORK_KIND_CFSI.equals(sSchCode)	  // HFL 결속대 보급
//	   	 ){
	   		isTrue = true; 	
	   	} 
		return isTrue;
	}	
	/**
     * YJK
	 * 적치단,열의 상,하,좌,우 정보를 포맷에 맞춰 가져온다.
     * TYPE P - +1
     *      M - -1
     * ex) '03' -> '02'
     *
     * @param  String
     * @return String
     * @throws 
     */			 
	public static String changeLayerFormat(String sStr , String sType)
	{	
		java.text.DecimalFormat df = new java.text.DecimalFormat("00");
		  
		long lVal = Long.parseLong(sStr);
		
		if("P".equals(sType)){
			lVal = lVal + 1;
		}else if("M".equals(sType)){
			lVal = lVal - 1;
		}
		
		return df.format(lVal);
	}	
	
    /**
     * 작업근조를 가져온다.
     */
    public static String getWorkDuty() {
        
        int[] date 		= getIntYMDHMS();
        
        String workGroup = "0";
		if(date[3] >= 7 && date[3] <= 15)  {
		    workGroup = "1";
		}else if(date[3] >= 16 && date[3] <= 23)  {
		    workGroup = "2";
		}else {
		    workGroup = "3";
		}		
		return workGroup;
    }
    
    public static int[] getIntYMDHMS() {
        String now = getStringYMDHMS();
        return new int[]{
                Integer.parseInt(now.substring(0,4)),
                Integer.parseInt(now.substring(4,6)),
                Integer.parseInt(now.substring(6,8)),
                Integer.parseInt(now.substring(8,10)),
                Integer.parseInt(now.substring(10,12)),
                Integer.parseInt(now.substring(12,14))};
    }    
    public static String getStringYMDHMS() {
        return getCurDate("yyyyMMddHHmmss");
    }  
    /**
     * YJK
     * 현재일자를 여러형태의 TYPE 으로 리턴한다.
     * ex) yyyy-mm-dd, hh-mm-ss, yyyyMMddhhmmss
     */ 
	public static String getCurDate(String type){
		SimpleDateFormat simpledateformat = new SimpleDateFormat(type);
        Date date = new Date();
        return simpledateformat.format(date);
    } 
    /**
     * 작업근조를 가져온다.
     */
    public static String getWorkParty() {
        
        CommonUtil comUtil = new CommonUtil();
		int[] date 		= getIntYMDHMS();
	    String steam ="";	
	    steam = comUtil.getTeam(date[0],date[1],date[2],date[3]);
		
	    if(steam.equals("")){
	    	steam ="E";
	    }
        return steam ;
        
    }
    
	
	public boolean isNumeric(String str){  

		try  {  

			double d = Double.parseDouble(str);  

		}catch(NumberFormatException nfe){  
			return false;  
		}  
		return true;  

	}	
	//SJH	
	/**
	 * 대차작업지정기준조회1
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
	 */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr) {
		return setWiseGridCombo(obj, hTitle, comboStrArr, 1, "N");
	}
	
	/**
 	 * 대차작업지정기준조회_코일1
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
    */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, int cdVal, String headTextYn) {
		String comboStr = "";
		
		if(comboStrArr != null) {
			
			if("Y".equals(headTextYn)) {
				comboStr = obj + ".AddComboListValue('" + hTitle + "', '', '');";
			}
			
			if(cdVal == 0 || cdVal == 1) {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + comboStrArr[cdVal][ii] + "', '" + comboStrArr[0][ii] + "');";
				}
			}else if(cdVal == 2) { //YD에 쓸수 있게 코드/코드명 형식으로 출력				
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + 
									"[" +comboStrArr[0][ii] + "] " + comboStrArr[1][ii] + "', '" + comboStrArr[0][ii] + "');";
				} 
			}else {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + 
										comboStrArr[0][ii] + " (" + comboStrArr[1][ii] + ")', '" + comboStrArr[0][ii] + "');";
				}
			}
		}
		
		return comboStr;
	}
	
    /**
     * Object Data Default 값을넣어주는 Function 
     * PO
     * @param  Object , String  
     * @return String 
     * @throws Exception
     */
	/* NVL로 대체 20190917
	public String setDataDefault (Object sObj, String sDef) throws Exception {
		
			
		if ( sObj ==null || "".equals(sObj.toString()))  
		{			
			return sDef;			
		}
		return sObj.toString();
	}
	*/
	/**
	 * GridData의 입력/수정/삭제  정보를 JDTORecord [] 으로 변환하여 리턴한다.
	 * (GridData의 입력/수정/삭제 항목을 가져오기위해 사용)
	 * @param inDto
	 * @return
	 */
	public JDTORecord [] genJDTORecordSet(GridData inDto) throws Exception{
		boolean isUpperKey = false;		
//		YDDataUtil yDDataUtil = new YDDataUtil();
		String szUserId = "";
		String szCRUD ="";
		String szydEqpId ="";
		
		if(inDto.getParam("set_upper") != null && inDto.getParam("set_upper").equals("true")){
			isUpperKey = true;
		}
		
		szUserId  = nvl(inDto.getParam("YD_USER_ID"), "");
		szydEqpId = nvl(inDto.getParam("YD_EQP_ID"), "");

		GridHeader [] ghs = inDto.getHeaders();
		int hCount = ghs.length;
		int rCount = 0;
		if(hCount > 0){
			rCount = ghs[0].getRowCount();
		}
		JDTORecord [] jdtoAl = new JDTORecord[rCount];
		logger.println(LogLevel.DEBUG,   "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG,   "헤더갯수:"+hCount);
		logger.println(LogLevel.DEBUG,   "Row갯수:"+rCount);

		logger.println(LogLevel.DEBUG,   "========== GridData inDto ROW DATA ===========");
		for(int i=0;i<rCount;i++){
			GridHeader chHeader = inDto.getHeader("CHECK");
			boolean Checked = (Integer.parseInt(chHeader.getValue(i)) == 1)? true:false;
			if(Checked){
				JDTORecord jDto = JDTORecordFactory.getInstance().create();
				for(int j=0;j<hCount;j++){
					String key = ghs[j].getID();
					String rValue = "";
					String hValue = "";
					if(ghs[j].getDataType().equals(OperateGridData.t_combo)){
						int iSelectedIdx = ghs[j].getSelectedIndex(i);
						if(iSelectedIdx >= 0){
							if(ghs[j].hasComboList()){
								rValue = StringHelper.evl( ghs[j].getComboHiddenValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
								hValue = StringHelper.evl( ghs[j].getComboValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
							}else{
								rValue = StringHelper.evl( ghs[j].getComboHiddenValues()[iSelectedIdx], "");
								hValue = StringHelper.evl( ghs[j].getComboValues()[iSelectedIdx], "");							
							}
						}else{
							rValue = "";
							hValue = "";
						}
							
					}
					else {
						rValue = StringHelper.evl(ghs[j].getValue(i), "");
						hValue = StringHelper.evl(ghs[j].getHiddenValue(i), "");
					}

					jDto.addField((isUpperKey)?key.toUpperCase():key, rValue);
				}
				//수정자 ,등록자 SETTING
				
				szCRUD = nvl(jDto.getField("CRUD"),"");
				
				if("C".equals(szCRUD))
				{
					jDto.setField("REGISTER",szUserId);
				}else if("U".equals(szCRUD)){
					jDto.setField("MODIFIER",szUserId);
				}else {					
				}
				jDto.setField("YD_USER_ID",szUserId);
				
				if(!szydEqpId.equals("")){
					jDto.setField("YD_EQP_ID",szydEqpId);
				}
				  
				jdtoAl[i] = jDto;
			}
		}

		logger.println(LogLevel.DEBUG,   "========== JDTORecord START ===========");
		for(int ss=0;ss<jdtoAl.length;ss++){
			logger.println(LogLevel.DEBUG,   jdtoAl[ss].toString());
		}
		logger.println(LogLevel.DEBUG,   "========== JDTORecord END ===========");

		return jdtoAl;
	}			
	/**
	 * 실수 문자열값 좌우측을 채워넣음
	 * 권오창
	 *
	 * @param strOrg
	 * @param nTotal
	 * @param nFloat
	 * @return
	 * @throws Exception
	 */
	public String FloatLRPAD(String strOrg, int nTotal, int nFloat, char ch) throws Exception
	{
		String szMethodName = "FloatLRPAD";
		String strTemp1 = "";
		String strTemp2 = "";
		int nJisu = nTotal - nFloat;
		int nSosu = nFloat;

		try{
			if(strOrg == null || "".equals(strOrg.trim()))
				return addLeftStr("", nTotal, (char)ch);

			int nIdx = strOrg.indexOf(".");
			if(nIdx <= 0){
				strTemp1 = this.addLeftStr(strOrg.trim(), nJisu, (char)ch);
				strTemp2 = this.addRightStr("0", nSosu, (char)ch);
				if(strTemp1.trim().equals("")){
					return null;
				}

			}else {
				String[] strSplit = strOrg.trim().split("\\.");

				strTemp1 = this.addLeftStr(strSplit[0], nJisu, (char)ch);
				strTemp2 = this.addRightStr(strSplit[1], nSosu, (char)ch);

				if(strTemp1.equals("") || strTemp2.equals("")){
					return null;
				}
			}
		}catch(Exception e){
//			this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
		}finally{
		}

		return 	strTemp1 + strTemp2;
	}

	/**
	 * 문자열 좌측을 지정한 값으로 채워넣음
	 *
	 * @param str
	 * @param len
	 * @param pad
	 * @return
	 * @throws Exception
	 */
	public String addLeftStr(String str, int len, char pad) throws Exception
	{
		String szMethodName = "addLeftStr";
		String result = "";
		int templen = 0;

		try{
			templen = len - str.getBytes().length;
			if(templen >= 0){
				for(int i=0; i<templen; i++)
					str = pad + str;
				result = str;
			}
		}catch(Exception e){
//			this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
		}

		return result;
	}
	/**
	 * 문자열 우측을 지정한 값으로 채워넣음
	 *
	 * @param str
	 * @param len
	 * @param pad
	 * @return
	 * @throws Exception
	 */
	public String addRightStr(String str, int len, char pad) throws Exception
	{
		String szMethodName = "addRightStr";
		String result = "";
		int templen = 0;

		try{
			templen = len - str.getBytes().length;
			if(templen >= 0){
				for(int i=0; i<templen; i++)
					str = str + pad;
				result = str;
			}
		}catch(Exception e){
//			this.putLog(szSessionName, szMethodName, "FloatLRPAD() : " + e.toString() + " : " + e.getMessage(), 4);
		}

		return result;
	}	
	/**
	 * 오퍼레이션명 : Get TC Code
	 *
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public String getTcCode(JDTORecord inRecord){


		String szMsg="";
		String szMethodName="getTcCode";
		String szRcvTcCode="";

		try{
			// 내부인터페이스(JMS Queue)
			szRcvTcCode = inRecord.getFieldString("JMS_TC_CD");

			// 외부인터페이스(L2 EAI)
			if(szRcvTcCode == null){
				szRcvTcCode=inRecord.getFieldString("MSG_ID");
			}

			// 외부인터페이스(RemoteEAI)
			if(szRcvTcCode == null){
				szRcvTcCode=inRecord.getFieldString("TC_CODE");

			}
//PIDEV			
			if(szRcvTcCode == null){
				szRcvTcCode=inRecord.getFieldString("MQ_TC_CD");

			}

			if(szRcvTcCode == null){
				szRcvTcCode="";

			}	// end if

			szRcvTcCode=szRcvTcCode.trim();
			szRcvTcCode=szRcvTcCode.toUpperCase();

		}catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getMessage();
//			this.putLog(szSessionName, szMethodName, szMsg, CConstant.ERROR);

			return null;
		} // end of try-catch

		return szRcvTcCode;


	} // end of getTcCode();	
	

//	/**
//	 * 운송지시 변경 작업(차량스케줄,검수재료,저장품)
//	 * @param recPara
//	 * @param szCaller
//	 * @return
//	 * @throws JDTOException
//	 */
//	public  String transOrdChange(JDTORecord recPara ) throws JDTOException {
//		String logId = recPara.getResultCode();
//		String	szMethodName			= "transOrdChange";
//		String	szOperationName			= "운송지시 변경 작업";
//		String	szMsg					= null;
//		String 	szRtnMsg				= null;
//		YdCarSchDao ydCarSchDao = new YdCarSchDao();
//		
//		JDTORecordSet	rsResult		= null;
//		JDTORecord		recTemp			= null;
//		
//		String	szOLD_TRANS_WORD_DATE		= null;
//		String 	szOLD_TRANS_WORD_SEQNO		= null;
//		String	szNEW_TRANS_WORD_DATE		= null;
//		String 	szNEW_TRANS_WORD_SEQNO		= null;
//		String 	szCHK_GP					= null;
//		YmCommDAO commDao = new YmCommDAO();
//		int 	cnt	=0;
//		int intRtnVal =0;
//		//--------------------------------------------------------------------------------
//		//	운송지시일자, 운송지시순번으로 차량스케줄 조회
//		//--------------------------------------------------------------------------------
//		
//		szMsg = "["+szOperationName+"] --------------- 메소드 시작 ---------------";
//		
// 
//		printLog(logId, szMethodName, "S+");
//		this.printLog(logId, szMsg, "SL");
//		szOLD_TRANS_WORD_DATE			= this.trim(recPara.getFieldString("OLD_TRANS_WORD_DATE")); 
//		szOLD_TRANS_WORD_SEQNO			= this.trim(recPara.getFieldString("OLD_TRANS_WORD_SEQNO"));
//		szNEW_TRANS_WORD_DATE			= this.trim(recPara.getFieldString("NEW_TRANS_WORD_DATE"));
//		szNEW_TRANS_WORD_SEQNO			= this.trim(recPara.getFieldString("NEW_TRANS_WORD_SEQNO"));
//		szCHK_GP						= this.trim(recPara.getFieldString("CHK_GP"));
//
//		recTemp			= JDTORecordFactory.getInstance().create();
//		recTemp.setField("OLD_TRANS_WORD_DATE" , szOLD_TRANS_WORD_DATE);
//		recTemp.setField("OLD_TRANS_WORD_SEQNO", szOLD_TRANS_WORD_SEQNO);
//		recTemp.setField("NEW_TRANS_WORD_DATE" , szNEW_TRANS_WORD_DATE);
//		recTemp.setField("NEW_TRANS_WORD_SEQNO", szNEW_TRANS_WORD_SEQNO);
//		recTemp.setField("MODIFIER"				, "trOChange");
//		
//		//--------------------------------------------------------------------------------
//		//	차량스케줄 운송지시 변경
//		//--------------------------------------------------------------------------------
// 
//		szMsg = "["+szOperationName+"] 차량스케줄 운송지시 변경 시작";
//		this.printLog(logId, szMsg, "▣");
//		
//	
//			//차량스케줄 운송지시 변경
//    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarschTransOrd */
//			/*UPDATE USRYDA.TB_YD_CARSCH
//				SET TRANS_ORD_DATE=:V_NEW_TRANS_WORD_DATE
//				, TRANS_ORD_SEQNO=:V_NEW_TRANS_WORD_SEQNO
//				, MODIFIER =:V_MODIFIER
//				,MOD_DDTT=sysdate
//				WHERE TRANS_ORD_DATE=:V_OLD_TRANS_WORD_DATE
//				 AND TRANS_ORD_SEQNO=:V_OLD_TRANS_WORD_SEQNO*/
//
//			
//			intRtnVal = commDao.update(recTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarschTransOrd", logId, szOperationName, "TB_YD_CARSCH 차량스케줄 운송지시 변경");
//			if(intRtnVal <= 0){
//				szMsg="["+szOperationName+"]"  
//                + " TB_YD_CARSCH UPDATE Error " 
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;
//     
//				this.printLog(logId, szMsg, "SL");
//				return "";
//	 
//			}else{
//				szMsg="["+szOperationName+"]" 
//				+ " TB_YD_CARSCH UPDATE Success " 				
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;    
//				this.printLog(logId, szMsg, "SL");
//
//			}	
//
//		szMsg = "["+szOperationName+"] 차량스케줄 운송지시 변경 완료:"+cnt;
//		this.printLog(logId, szMsg, "▣");
//		
//		//--------------------------------------------------------------------------------
//		 
//		
//		//--------------------------------------------------------------------------------
//		//	검수재료 운송지시 변경
//		//--------------------------------------------------------------------------------
// 
//		szMsg = "["+szOperationName+"] 검수재료 운송지시 변경 시작";
//		this.printLog(logId, szMsg, "▣");	
//		
//		
//		
//		//검수재료 운송지시 변경
//		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdExamTransOrd */
//		/*UPDATE USRYDA.TB_YD_EXAMINATIONCHKLIST
//			SET TRANS_ORD_DATE=:V_NEW_TRANS_WORD_DATE
//			, TRANS_ORD_SEQNO=:V_NEW_TRANS_WORD_SEQNO
//			, MODIFIER =:V_MODIFIER
//			,MOD_DDTT=sysdate
//			, DEL_YN='N'
//			, CHECKING_YN='N'
//			, LABEL_YN=NULL
//			, YD_AB_CD=NULL
//			WHERE TRANS_ORD_DATE=:V_OLD_TRANS_WORD_DATE
//			 AND TRANS_ORD_SEQNO=:V_OLD_TRANS_WORD_SEQNO*/
//
//		
//		intRtnVal = commDao.update(recTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdExamTransOrd", logId, szOperationName, "TB_YD_EXAMINATIONCHKLIST 검수재료 운송지시 변경");
//		if(intRtnVal <= 0){
//			szMsg="["+szOperationName+"]"  
//            + " TB_YD_EXAMINATIONCHKLIST UPDATE Error " 
//			+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//			+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//			+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//			+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;
// 
//			this.printLog(logId, szMsg, "SL");
//			return "";
// 
//		}else{
//			szMsg="["+szOperationName+"]" 
//			+ " TB_YD_EXAMINATIONCHKLIST UPDATE Success " 
//			+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//			+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//			+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//			+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;    
//			this.printLog(logId, szMsg, "SL");
//
//		}	
//		
// 
//		szMsg = "["+szOperationName+"] 검수재료 운송지시 변경 완료:"+cnt;
//		this.printLog(logId, szMsg, "▣");
//		
//		//--------------------------------------------------------------------------------
//		
//		//--------------------------------------------------------------------------------
//		//	재료정보 운송지시 변경
//		//--------------------------------------------------------------------------------
// 
//		szMsg = "["+szOperationName+"] 재료정보 운송지시 변경 시작";
//		this.printLog(logId, szMsg, "▣");
//		
//		if(szCHK_GP.equals("YD")){
//			
//			//재료정보 운송지시 변경
//			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStockTransOrd*/
//			/*	UPDATE USRYDA.TB_YD_STOCK
//				SET TRANS_ORD_DATE=:V_NEW_TRANS_WORD_DATE
//				, TRANS_ORD_SEQNO=:V_NEW_TRANS_WORD_SEQNO
//				, MODIFIER =:V_MODIFIER
//				,MOD_DDTT=sysdate
//				WHERE TRANS_ORD_DATE=:V_OLD_TRANS_WORD_DATE
//				 AND TRANS_ORD_SEQNO=:V_OLD_TRANS_WORD_SEQNO*/
//
//			
//			intRtnVal = commDao.update(recTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStockTransOrd", logId, szOperationName, "TB_YD_STOCK 검수재료 운송지시 변경");
//			if(intRtnVal <= 0){
//				szMsg="["+szOperationName+"]"  
//	            + " TB_YD_STOCK UPDATE Error " 
//	            + " szCHK_GP: "+szCHK_GP    
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;
//	 
//				this.printLog(logId, szMsg, "SL");
//				return "";
//	 
//			}else{
//				szMsg="["+szOperationName+"]" 
//				+ " TB_YD_STOCK UPDATE Success " 
//				+ " szCHK_GP: "+szCHK_GP    
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;    
//				this.printLog(logId, szMsg, "SL");
//
//			}	
//
//		}else{
//			
//			//재료정보 운송지시 변경
//			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStockTransOrd*/
//			/*UPDATE USRYMA.TB_YM_STOCK
//				SET TRANS_WORD_NO=:V_NEW_TRANS_WORD_DATE||:V_NEW_TRANS_WORD_SEQNO
//				    , MODIFIER =:V_MODIFIER
//				    , MOD_DDTT=sysdate
//				WHERE TRANS_WORD_NO=:V_OLD_TRANS_WORD_DATE||:V_OLD_TRANS_WORD_SEQNO*/
//
//			
//			intRtnVal = commDao.update(recTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStockTransOrd", logId, szOperationName, "TB_YD_STOCK 검수재료 운송지시 변경");
//			if(intRtnVal <= 0){
//				szMsg="["+szOperationName+"]"  
//	            + " TB_YD_STOCK UPDATE Error " 
//	            + " szCHK_GP: "+szCHK_GP    
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;
//	 
//				this.printLog(logId, szMsg, "SL");
//				return "";
//	 
//			}else{
//				szMsg="["+szOperationName+"]" 
//				+ " szCHK_GP: "+szCHK_GP    
//				+ " TB_YD_STOCK UPDATE Success " 
//				+ " OLD_TRANS_WORD_DATE: "+szOLD_TRANS_WORD_DATE    
//				+ " OLD_TRANS_WORD_SEQNO: "+szOLD_TRANS_WORD_SEQNO    
//				+ " NEW_TRANS_WORD_DATE: "+szNEW_TRANS_WORD_DATE    
//				+ " NEW_TRANS_WORD_SEQNO: "+szNEW_TRANS_WORD_SEQNO   ;    
//				this.printLog(logId, szMsg, "SL");
//
//			}	
//			
//			
//			
//			
//			/*com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYmStockTransOrd*/
//			cnt = ydCarSchDao.updYdTransOrdChange(recTemp, 3);
//		}
// 
//		szMsg = "["+szOperationName+"] 재료정보 운송지시 변경 완료:"+cnt;
//		this.printLog(logId, szMsg, "▣");
//		
//		//--------------------------------------------------------------------------------
//		
//		
//		//--------------------------------------------------------------------------------
//		
//		szMsg = "["+szOperationName+"] --------------- 메소드 끝 ---------------";
//		 
//		printLog(logId, szMethodName, "S-");
//		this.printLog(logId, szMsg, "SL");
//		return szRtnMsg;
//	}
	/**
	 * GridData의 입력/수정/삭제 정보를 JDTORecord [] 으로 변환하여 리턴한다. (GridData의 입력/수정/삭제
	 * 항목을 가져오기위해 사용) - 추가 : 체크가 되지않고 모든 그리드 정보를 변환
	 * 
	 * @param inDto
	 * @return
	 */
	public JDTORecord[] genGridToJDTORecordAll(GridData inDto) throws Exception {
		boolean isUpperKey = false;
		if (inDto.getParam("set_upper") != null && inDto.getParam("set_upper").equals("true")) {
			isUpperKey = true;
		}
		
		String szUserId= nvl(inDto.getParam("YD_USER_ID"), "");

		GridHeader[] ghs = inDto.getHeaders();
		int hCount = ghs.length;
		int rCount = 0;
		if (hCount > 0) {
			rCount = ghs[0].getRowCount();
		}
		JDTORecord[] jdtoAl = new JDTORecord[rCount];
		logger.println(LogLevel.DEBUG, "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG, "헤더갯수:" + hCount);
		logger.println(LogLevel.DEBUG, "Row갯수:" + rCount);

		logger.println(LogLevel.DEBUG, "========== GridData inDto ROW DATA ===========");
		for (int i = 0; i < rCount; i++) {
			
			JDTORecord jDto = this.genParamToJDTORecord(inDto);

			for (int j = 0; j < hCount; j++) {
				String key = ghs[j].getID();
				String rValue = "";
				String hValue = "";
				
				
				//수정_이현성 [콤보박스일때 문제점 해결하기 위함]
				if (ghs[j].getDataType().equals(OperateGridData.t_combo)) {
					
					int iSelectedIdx = ghs[j].getSelectedIndex(i);
					if(iSelectedIdx >= 0){
						if(ghs[j].hasComboList()){
							rValue = StringHelper.evl( ghs[j].getComboHiddenValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
							hValue = StringHelper.evl( ghs[j].getComboValues(ghs[j].getSelectedComboListKey(i))[iSelectedIdx], "");
						}else{
							rValue = StringHelper.evl( ghs[j].getComboHiddenValues()[iSelectedIdx], "");
							hValue = StringHelper.evl( ghs[j].getComboValues()[iSelectedIdx], "");							
						}
					}else{
						rValue = "";
						hValue = "";
					}
				}
				else {
					rValue = StringHelper.evl(ghs[j].getValue(i), "");
					hValue = StringHelper.evl(ghs[j].getHiddenValue(i), "");
				}

				jDto.setField((isUpperKey) ? key.toUpperCase() : key, rValue);
			}
			jDto.setField("YD_USER_ID",szUserId);
			jdtoAl[i] = jDto;
			
		}

		logger.println(LogLevel.DEBUG, "========== JDTORecord START ===========");
		for (int ss = 0; ss < jdtoAl.length; ss++) {
			logger.println(LogLevel.DEBUG, jdtoAl[ss].toString());
		}
		logger.println(LogLevel.DEBUG, "========== JDTORecord END ===========");

		return jdtoAl;
	}
	
	/**
	 * 오퍼레이션명 : 카드번호로 차량 작업 구분
	 *
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public String getCarMoveYN(String cardNo){
		String carMove = "N";
		if ((cardNo.equals(CConstant.CAR_BAY_TRANS_CARD_NO_1))
   	    			|| (cardNo.equals(CConstant.CAR_BAY_TRANS_CARD_NO_2))
   	    			|| (cardNo.equals(CConstant.CAR_BAY_TRANS_CARD_NO_3))
   	    			|| (cardNo.equals(CConstant.CAR_BAY_TRANS_CARD_NO_4))
   	    			|| (cardNo.equals(CConstant.CAR_BAY_TRANS_CARD_NO_5))) {
			carMove = "Y"; 
		}
		return carMove;


	} // end of getTcCode();
	/**
	 * Object 객체로부터 JDTORecord 의 객체를 생성합니다. Method 클래스의 invoke 메소드를 사용하여 Object
	 * 에 담겨진 데이터를 JDTORecord 의 데이터로 설정합니다. JDTORecord 의 KEY 값은 Object 의 set/get
	 * 펑션명칭과 동일하게 구성됩니다( 대소문자 구분됨 )
	 * 
	 * @param oClass
	 *            Object 객체의 Class
	 * @param oInstance
	 *            입력 Object 객체
	 * @return 출력 JDTORecord 객체
	 * @throws AppRuntimeException
	 */
	public static JDTORecord genJDTO(Class oClass, Object oInstance) throws AppRuntimeException {

		// 출력 JDTORecord 객체
		JDTORecord jdto = null;
		// 입력 Object 클래스가 가지고 있는 메소드 배열
		Method[] oMethodArr = null;

		try {
			// 출력 jdto 객체 생성
			jdto = JDTORecordFactory.getInstance().create();

			// 입력 Class 로부터 메소드 명칭 배열을 취득합니다.
			oMethodArr = oClass.getMethods();

			// 입력 Class 의 메소드 명칭 배열만큼 반복
			for (int ii = 0; ii < oMethodArr.length; ii++) {
				// 입력 Class 의 메소드

				Method oMethod = oMethodArr[ii];
				// 입력 Class 의 메소드명칭
				String methodName = oMethod.getName();

				logger.println(LogLevel.DEBUG, " [ 메소드 명칭 ] " + methodName);

				// 메소드 호출 뒤 반환값을 저장하는 변수
				Object obj = null;

				// JDTORecord의 key
				String key = null;

				// 메소드의 명칭이 get_prefix 경우에 실행
				if (methodName.startsWith("get")) {
					if(!methodName.equals("getClass")){
						// JDTORecord의 key 변수에 get_prefix 를 제외한 값을 설정
						key = methodName.substring(3);
						// oInstance 객체의 set 메소드를 실행(invoke)
						obj = oMethod.invoke(oInstance, null);
						// JDTORecord 에 값을 저장
						jdto.setField(key, obj);
					}
				}
			}
		} catch (Exception e) {
			throw new AppRuntimeException(e.toString(), e);
		} finally {

		}
		// 출력 JDTORecord 객체 반환
		return jdto;
	}	
	
	
	/**
	 * SMS SENDER
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
    public static String updSmsMsgSend(JDTORecord recInPara) throws DAOException {
		
		String szMethodName 		= "updSmsMsgSend";
		String szOperationName 		= "SMS SENDER";
		String szMsg                = "";
		JDTORecord	inRecord 		= null;
		try {
			
			szMsg = "SMS SENDER 시작";
			
			// JDTORecord 생성
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("FROM_SENDER_NAME", new String("야드관리"));	// SMS 보내는 사람 성명
			
			inRecord.setField("FROM_PHONE_NO", recInPara.getFieldString("FROM_PHONE_NO"));	// SMS 보내는 사람 핸드펀번호
			inRecord.setField("TO_PHONE_NO"  , recInPara.getFieldString("TO_PHONE_NO"));	// SMS 받는 사람 핸드펀번호
			inRecord.setField("TO_CONTENT"   , recInPara.getFieldString("TO_CONTENT"));		// SMS 전송 내용
			inRecord.setField("TO_SEND_TIME" , new String(""));								// SMS 전송시간
			
			//---------------------------------------------------------------------
		    MessageSenderAuto    sender = new MessageSenderAuto();
		    inRecord.setField("RECV_ID", "YD00001");
    		inRecord.setField("GROUP_ID", "MMS1");
		    inRecord.setField("PROGRAM_ID", "updSmsMsgSendYD");

		    sender.sendAutoSMS(inRecord);
		    //---------------------------------------------------------------------

			
			szMsg = "SMS SENDER 끝";
			
		}catch(Exception ex) {
			szMsg = "["+szOperationName+"] SMS 송신 ERROR - 메세지 : " + ex.getMessage();
		}
		return CConstant.RETN_CD_SUCCESS;
		
		
	}	// end of updSmsMsgSend										
    
    
    
	/**
	 * TALK SENDER
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
    public static String procTalkMsgSend(JDTORecord rcvMsg) throws DAOException {
		
		String szMethodName 		= "procTalkMsgSend";
		String szOperationName 		= "TALK SENDER";
		String szMsg                = "";
		try {
			
			szMsg = "TALK SENDER 시작";
			
			String sPhoneNum = rcvMsg.getFieldString("PHONE_NUM");
			String sSubject  = rcvMsg.getFieldString("SUBJECT"  );	
		    String sSndMsg   = "[현대제철 공지사항]\n" + rcvMsg.getFieldString("TO_CONTENT");
			
			MessageSenderTalk sender = new MessageSenderTalk();
			
			JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
			recPara1.setField("PHONE_NUM"  , sPhoneNum   ); //전화번호
			recPara1.setField("TMPL_CD"    , "CM1"       );
			recPara1.setField("SND_MSG"    , sSndMsg     ); //전송내용
			recPara1.setField("SUBJECT"    , sSubject    ); //제목?
			recPara1.setField("SMS_SND_NUM", "0416801606");
			recPara1.setField("RECV_ID"    , "YD00001"   ); 
			recPara1.setField("GROUP_ID"   , "KaKao"     );
			recPara1.setField("PROGRAM_ID" , "procTalkMsgSend");
			sender.sendTalk(recPara1);
			
			szMsg = "TALK SENDER 끝";
			
		}catch(Exception ex) {
			szMsg = "["+szOperationName+"] TALK 송신 ERROR - 메세지 : " + ex.getMessage();
		}
		return CConstant.RETN_CD_SUCCESS;
		
		
	}	// end of updSmsMsgSend			
    

	/**
	 *      [A] 오퍼레이션명 : fillSpZr
	 *
	 * @param String szData			// 변환대상 문자열
	 *        int    nLen 			// 변환 후 목적 문자열 길이
	 *        int    nChgMd			// 변환 방식 (0: 숫자열변환, !0: 문자열변환
	 * @return String 				// 변환 완료 된 문자열
	 * @throws
	 */
	public String fillSpZr(String szData, int nLen, int nChgMd) {

		String szFillData="";
		int i=0;
		int nDataLen =0;


		try{
			szFillData= szData.trim();
			nDataLen =szFillData.length();
			if (nDataLen >=nLen)
				return szFillData.substring(0, nLen);

			for(i=nDataLen; i<nLen; i++){
				if(nChgMd==0)
					szFillData="0"+szFillData;
				else
					szFillData+=" ";
			} 

		}catch(Exception e){
			for(i=0;i<nLen;i++){
				if(nChgMd==0) szFillData="0"+szFillData;
				else		  szFillData+=" ";
			} 

		} 

		return szFillData;

	} 

	//=====================================================================
	// 
	// 적치열구분(6) + 적치BED번호(2) + 적치단번호(3) 를 입력받아서 야드별로 구분하여
	// 10자리의 위치정보를 만들어 내는 함수
	//
	// 코일제품   (J) : 6 + 2 + 2
	//=====================================================================
	public String ParsingStkColGpBedLyr(String szStkColGp, String szStkBedNo, String szStkLyrNo){
		// 변수 선언
		String szMsg        = "";
		String szYdGp       = "";
		String szRet        = "";


		// 파라미터 유효성 체크
		if(szStkColGp == null || szStkColGp.equals("") || szStkColGp.trim().length() != 6){
			szMsg = "넘어온 파라미터에서 적치열구분 항목이 유효한 데이터가 아닙니다.";
			return "";
		}

		if(szStkBedNo == null || szStkBedNo.equals("") || szStkBedNo.trim().length() != 2){
			szMsg = "넘어온 파라미터에서 적치BED번호 항목이 유효한 데이터가 아닙니다.";
			return "";
		}

		if(szStkLyrNo == null || szStkLyrNo.equals("") || szStkLyrNo.trim().length() != 3){
			szMsg = "넘어온 파라미터에서 적치단번호 항목이 유효한 데이터가 아닙니다.";
			return "";
		}


		szMsg = "[수신 항목] 적치열구분(" + szStkColGp + ") 적치BED번호(" + szStkBedNo + ") 적치단번호(" + szStkLyrNo + ")";
		

		// 야드값 추출
		szYdGp = szStkColGp.substring(0, 1);

		if("J".equals(szYdGp)){
			// 2열연코일제품야드
			szRet = szStkColGp + szStkBedNo + this.fillSpZr("" + Integer.parseInt(szStkLyrNo), 2, 0);
			szMsg = "편집된 위치정보 : C열연코일제품야드(" + szRet + ")";
		} else {
			szMsg = "*** 적치열구분, 적치BED번호, 적치단 번호를 이용해 위치정보 편성에 실패 ***";
			return "";
		}

		
		return szRet;
	}
	
//PIDEV
	
	//해쉬맵의 내용을 JDTORecord로 담는다.PIDEV
	public HashMap jdtoRecordToLinkedHashMap(JDTORecord inJRecord) throws Exception {
		LinkedHashMap returnMap = new LinkedHashMap();

		if (inJRecord == null || inJRecord.size() == 0) {
			return returnMap;
		}

		java.util.Iterator iterator = inJRecord.iterateName();

		String key = "";
		while (iterator.hasNext()) {
			key = String.valueOf(iterator.next());
			returnMap.put(key, nvl(inJRecord.getField(key), ""));
		}
		return returnMap;
	}

	/**
	 *      [A] 오퍼레이션명 : Logging 을 위한 ID 생성
	 *
	 *      @return String
	*/
	public String getLogIdT() {
		return "[T]<" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
	}
	/**
	 *      [A] 오퍼레이션명 : Logging 을 위한 ID 생성
	 *
	 *      @return String
	*/
	public String getLogIdS() {
		return "[S]<" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
	}
	
}
