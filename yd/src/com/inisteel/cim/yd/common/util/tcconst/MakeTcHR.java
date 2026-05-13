package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * HR (열연조업) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcHR {
	// YDHRJ001 열연정정보급완료실적	
	// YDHRJ002 열연정정추출완료실적	

	
	
	//클래스명
	private static final String szClassName  = MakeTcHR.class.getName();

	
	
	/**
	 * YDHRJ001 : 열연정정보급완료실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeHRJ001(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDHRJ001
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		//      3   STL_NO              재료번호                           VARCHAR2(11)             
		//      4   YD_DN_RSLT_DT       야드권하완료일시              VARCHAR2(14)
		//      5   EQP_GP              설비구분                           VARCHAR2(6)         보급위치
		//      6.  TREAT_GP            처리구분                           VARCHAR2(1)         1: 보급완료 2:보급취소
		
		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeHRJ001";
		String szMsg              = "";
		String szOperationName    = "열연조업 열연정정보급완료실적";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szYD_DN_CMPL_DT    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		String szTREAT_GP         = "";

		try{			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ001() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
			
			// 항목추출
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szYD_DN_CMPL_DT = ydDaoUtils.paraRecChkNull(inRec, "YD_DN_CMPL_DT");
			szTREAT_GP      = ydDaoUtils.paraRecChkNull(inRec, "TREAT_GP");
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			Object objTemp = null;
			objTemp = YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(objTemp != null){
				szEQP_GP = (String)objTemp;			
			}else{
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 null.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			}
			
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("JMS_TC_CD"         , "YDHRJ001");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 전문편성
			outRec.setField("STL_NO"            , szSTL_NO);
			outRec.setField("YD_DN_RSLT_DT"     , szYD_DN_CMPL_DT);
			outRec.setField("EQP_GP"            , szEQP_GP);
			outRec.setField("TREAT_GP"          , szTREAT_GP);
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ001() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
		}catch(Exception e){
			szMsg = "열연정정보급완료실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
	
		return outRecSet.size();
	} // end of makeHRJ001()
	
	
	
	
	
	/**
	 * YDHRJ002 : 열연정정추출완료실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeHRJ002(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDHRJ002
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		//		3.	STL_NO				재료번호				VARCHAR2(11)
		//		4.	YD_UP_CMPL_DT       야드권상완료일시		VARCHAR2(14)
		//      5.  TREAT_GP            처리구분                           VARCHAR2(1)         3: 추출완료 4:TAKE OUT완료
		
		// 레코드 선언
		JDTORecord outRec         = null;		
		
		// 크레인 스케쥴 DAO객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();
		
		String szMethodName       = "makeHRJ002";
		String szMsg              = "";
		String szOperationName    = "열연조업 열연정정추출완료실적";		
		String szSTL_NO           = "";
		String szYD_UP_CMPL_DT    = "";
		String szTREAT_GP		  = "";
		
		String szISPTOR		  = "";
		String szTAKE_OUT_DT		  = "";
		String szTAKE_OUT_CD		  = "";
		
		try{			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ002() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
			
			// 항목추출
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szYD_UP_CMPL_DT = ydDaoUtils.paraRecChkNull(inRec, "YD_UP_CMPL_DT");
			szTREAT_GP      = ydDaoUtils.paraRecChkNull(inRec, "TREAT_GP");
			
			szISPTOR        = ydDaoUtils.paraRecChkNull(inRec, "YD_ISPTOR");	
			szTAKE_OUT_DT   = ydDaoUtils.paraRecChkNull(inRec, "YD_TAKE_OUT_DT");	
			szTAKE_OUT_CD   = ydDaoUtils.paraRecChkNull(inRec, "YD_TAKE_OUT_CD");	
 
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("JMS_TC_CD"         , "YDHRJ002");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 전문편성
			outRec.setField("STL_NO"            , szSTL_NO);
			outRec.setField("YD_UP_CMPL_DT"     , szYD_UP_CMPL_DT);
			outRec.setField("TREAT_GP"          , szTREAT_GP);
			outRec.setField("YD_ISPTOR"          , szISPTOR);
			outRec.setField("YD_TAKE_OUT_DT"          , szTAKE_OUT_DT);
			outRec.setField("YD_TAKE_OUT_CD"          , szTAKE_OUT_CD);
			
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ002() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "열연정정추출완료실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		
		return outRecSet.size();
	} // end of makeHRJ002()
	
	
	/**
	 * YDHRJ003 : 열연조업 시편채취권상실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeHRJ003(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDHRJ003
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		//		3.	STL_NO				재료번호				VARCHAR2(11)
 
		
		// 레코드 선언
		JDTORecord outRec         = null;		
		
		// 크레인 스케쥴 DAO객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();
		
		String szMethodName       = "makeHRJ003";
		String szMsg              = "";
		String szOperationName    = "열연조업 시편채취권상실적";		
		String szSTL_NO           = "";
		String szYD_UP_CMPL_DT    = "";
		String szTREAT_GP		  = "";
		
		String szISPTOR		  = "";
		String szTAKE_OUT_DT		  = "";
		String szTAKE_OUT_CD		  = "";
		
		try{			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ003() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
			
			// 항목추출
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
 	
 
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("JMS_TC_CD"         , "YDHRJ003");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 전문편성
			outRec.setField("STL_NO"            , szSTL_NO); 
			
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ003() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "열연조업 시편채취권상실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		
		return outRecSet.size();
	} // end of makeHRJ003()
	
	
	/**
	 * YDHRJ005 : 열연정정보급완료실적 -이퀄라이저
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeHRJ005(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDHRJ005
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		//      3   STL_NO              재료번호                           VARCHAR2(11)             
		//      4   YD_DN_RSLT_DT       야드권하완료일시              VARCHAR2(14)
		//      5   EQP_GP              설비구분                           VARCHAR2(6)         보급위치
		//      6.  TREAT_GP            처리구분                           VARCHAR2(1)         1: 보급완료 2:보급취소
		
		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeHRJ005";
		String szMsg              = "";
		String szOperationName    = "열연조업 열연정정보급완료실적 -이퀄라이저";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szYD_DN_CMPL_DT    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		String szTREAT_GP         = "";

		try{			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ005() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
			
			// 항목추출
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szYD_DN_CMPL_DT = ydDaoUtils.paraRecChkNull(inRec, "YD_DN_CMPL_DT");
			szTREAT_GP      = ydDaoUtils.paraRecChkNull(inRec, "TREAT_GP");
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			Object objTemp = null;
			objTemp = YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(objTemp != null){
				szEQP_GP = (String)objTemp;			
			}else{
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 null.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			}
			
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("JMS_TC_CD"         , "YDHRJ005");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 전문편성
			outRec.setField("STL_NO"            , szSTL_NO);
			outRec.setField("YD_DN_RSLT_DT"     , szYD_DN_CMPL_DT);
			outRec.setField("EQP_GP"            , szEQP_GP);
			outRec.setField("TREAT_GP"          , szTREAT_GP);
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ005() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
		}catch(Exception e){
			szMsg = "열연정정보급완료실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
	
		return outRecSet.size();
	} // end of makeHRJ005()
	
	
	
	
	
	/**
	 * YDHRJ006 : 열연정정추출완료실적  -이퀄라이저
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeHRJ006(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDHRJ006
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)
		//		3.	STL_NO				재료번호				VARCHAR2(11)
		//		4.	YD_UP_CMPL_DT       야드권상완료일시		VARCHAR2(14)
		//      5.  TREAT_GP            처리구분                           VARCHAR2(1)         3: 추출완료 4:TAKE OUT완료
		
		// 레코드 선언
		JDTORecord outRec         = null;		
		
		// 크레인 스케쥴 DAO객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();
		
		String szMethodName       = "makeHRJ006";
		String szMsg              = "";
		String szOperationName    = "열연조업 열연정정추출완료실적 -이퀄라이저";		
		String szSTL_NO           = "";
		String szYD_UP_CMPL_DT    = "";
		String szTREAT_GP		  = "";
		
		String szISPTOR		  = "";
		String szTAKE_OUT_DT		  = "";
		String szTAKE_OUT_CD		  = "";
		
		try{			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ006() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
			
			// 항목추출
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szYD_UP_CMPL_DT = ydDaoUtils.paraRecChkNull(inRec, "YD_UP_CMPL_DT");
			szTREAT_GP      = ydDaoUtils.paraRecChkNull(inRec, "TREAT_GP");
			
			szISPTOR        = ydDaoUtils.paraRecChkNull(inRec, "YD_ISPTOR");	
			szTAKE_OUT_DT   = ydDaoUtils.paraRecChkNull(inRec, "YD_TAKE_OUT_DT");	
			szTAKE_OUT_CD   = ydDaoUtils.paraRecChkNull(inRec, "YD_TAKE_OUT_CD");	
 
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("JMS_TC_CD"         , "YDHRJ006");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 전문편성
			outRec.setField("STL_NO"            , szSTL_NO);
			outRec.setField("YD_UP_CMPL_DT"     , szYD_UP_CMPL_DT);
			outRec.setField("TREAT_GP"          , szTREAT_GP);
			outRec.setField("YD_ISPTOR"          , szISPTOR);
			outRec.setField("YD_TAKE_OUT_DT"          , szTAKE_OUT_DT);
			outRec.setField("YD_TAKE_OUT_CD"          , szTAKE_OUT_CD);
			
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeHRJ006() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "열연정정추출완료실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		
		return outRecSet.size();
	} // end of makeHRJ006()
//---------------------------------------------------------------------------	
} // end of class MakeTcHR
