package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * H2 (C열연정정L2) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcH2 {

	// YDH2L001	C열연정정 SPM1 입측Line-In 실적(권하시점)
	// YDH2L003	C열연정정 SPM1 출측Line-Off실적(권상시점)
	// YDH2L004	C열연정정 SPM1 Take-Out   실적(권상시점)
	// YDH2L011	C열연정정 HFL 입측Line-In 실적(권하시점)
	// YDH2L013	C열연정정 HFL 출측Line-Off실적(권상시점)
	// YDH2L014	C열연정정 HFL Take-Out   실적(권상시점)
	// YDH2L021	C열연정정 SPM2 입측Line-In 실적(권하시점)
	// YDH2L023	C열연정정 SPM2 출측Line-Off실적(권상시점)
	// YDH2L024	C열연정정 SPM2 Take-Out   실적(권상시점)

	//클래스명
	private static final String szClassName  = MakeTcH2.class.getName();


	
	/**
	 * YDH2L001 : 정정 SPM1 입측Line-In실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L001(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L001
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L001";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 정정 SPM1 입측Line-In실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 				  = 19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L001() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP == null || szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L001");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L001() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "[YDH2L001] 정정 SPM1 입측Line-In실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		return outRecSet.size();

	} // end of makeH2L001()



	
	
	/**
	 * YDH2L003 : 정정 SPM1 출측Line-Off실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L003(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L003
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L003";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM1 정정출측Line-Off실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L003() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			ydUtils.putLog(szClassName, szMethodName, "\n 111111\n"+szSEARCHKEY, 4);
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L003");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L003() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "정정출측 SPM1 Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L003()
	

	/**
	 * YDH2L004 : C열연정정 SPM1 Take-Out실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L004(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L004
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L004";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM1 Take-Out실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L004() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L004");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L004() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "정정 SPM1 출측Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L004()
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * YDH2L011 : 정정 HFL 입측Line-In실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L011(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L011
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L011";
		String szMsg              = "";
		String szOperationName    = "C열연정정 L2 정정 HFL 입측Line-In실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 				  = 19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L005() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP == null || szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L011");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L005() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "[YDH2L011] 정정입측Line-In실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		return outRecSet.size();

	} // end of makeH2L005()



	
	
	/**
	 * YDH2L013 : 정정 HFL 출측Line-Off실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L013(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L013
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L013";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 정정 HFL 출측Line-Off실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L006() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L013");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L006() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "정정 HFL 출측Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L006()
	

	/**
	 * makeH2L014 : C열연정정 HFL Take-Out 실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L014(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L014
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L014";
		String szMsg              = "";
		String szOperationName    = "C열연정정 HFL Take-Out실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L007() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L014");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L007() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "C열연정정 HFL Take-Out실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L007()	
	
	
	
	
	
	
	/**
	 * YDH2L021 : 정정 SPM2 입측Line-In실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L021(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L021
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L021";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 정정 SPM2 입측Line-In실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 				  = 19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L008() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP == null || szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L021");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L008() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "[YDH2L021] 정정 SPM2 입측Line-In실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		return outRecSet.size();

	} // end of makeH2L008()



	
	
	/**
	 * YDH2L023 : 정정 SPM2 출측Line-Off실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L023(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L023
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L023";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM2 정정출측Line-Off실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L009() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L023");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L009() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "정정출측 SPM2 Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L009()
	

	/**
	 * YDH2L024 : C열연정정 SPM2 Take-Out실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L024(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L024
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L024";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM2 Take-Out실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L010() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L024");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L010() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "정정 SPM2 출측Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L010()
	
//C증설	
	//---------------------------------------------------------------------------	
	
	/**
	 * YDH2L031 : 정정 SPM3 입측Line-In실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L031(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L001
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L031";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 정정 SPM3 입측Line-In실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 				  = 19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L031() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP == null || szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L031");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L031() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "[YDH2L031] 정정 SPM3 입측Line-In실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		return outRecSet.size();

	} // end of makeH2L031()


	
	/**
	 * YDH2L033 : 정정 SPM3 출측Line-Off실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L033(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L003
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L033";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM3 정정출측Line-Off실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L033() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			ydUtils.putLog(szClassName, szMethodName, "\n 111111\n"+szSEARCHKEY, 4);
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L033");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L033() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "정정출측 SPM3 Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L033()
	

	/**
	 * YDH2L034 : C열연정정 SPM3 Take-Out실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L034(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L004
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L034";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM3 Take-Out실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L034() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L034");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L034() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "정정 SPM3 출측Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L034()
	
	
			
	/**
	 * YDH2L041 : 정정 SPM4 입측Line-In실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L041(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L001
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L041";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 정정 SPM4 입측Line-In실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 				  = 19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L041() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP == null || szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L041");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L041() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "[YDH2L041] 정정 SPM4 입측Line-In실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		return outRecSet.size();

	} // end of makeH2L041()
	
	
	/**
	 * YDH2L041 : 정정 SPM4 입측Line-In실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L071(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L001
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L071";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 정정 SPM5 입측Line-In실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 				  = 19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L071() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP == null || szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L071");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L071() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "[YDH2L071] 정정 SPM5 입측Line-In실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		return outRecSet.size();

	} // end of makeH2L071()
	
	/**
	 * YDH2L043 : 정정 SPM4 출측Line-Off실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L043(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L003
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L043";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM4 정정출측Line-Off실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L043() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			ydUtils.putLog(szClassName, szMethodName, "\n 111111\n"+szSEARCHKEY, 4);
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L043");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L043() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "정정출측 SPM4 Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L043()

	/**
	 * YDH2L043 : 정정 SPM5 출측Line-Off실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L073(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L003
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L073";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM5 정정출측Line-Off실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L073() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			ydUtils.putLog(szClassName, szMethodName, "\n 111111\n"+szSEARCHKEY, 4);
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L073");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L073() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "정정출측 SPM5 Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L043()

	/**
	 * YDH2L044 : C열연정정 SPM4 Take-Out실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L044(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L004
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L044";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM4 Take-Out실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L044() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L044");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L044() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "정정 SPM4 출측Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L044()
	

	/**
	 * YDH2L074 : C열연정정 SPM5 Take-Out실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L074(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L004
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L074";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 SPM5 Take-Out실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L074() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L074");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L074() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "정정 SPM5 출측Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L074()
	
	
	/**
	 * YDH2L051 : 정정 HFL4 입측Line-In실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L051(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L011
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L051";
		String szMsg              = "";
		String szOperationName    = "C열연정정 L2 정정 HFL4 입측Line-In실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 				  = 19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L051() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP == null || szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L051");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L051() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "[YDH2L051] 정정입측  HFL4 Line-In실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		return outRecSet.size();

	} // end of makeH2L051()



	
	
	/**
	 * YDH2L053 : 정정 HFL4 출측Line-Off실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L053(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L013
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L053";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 정정 HFL4 출측Line-Off실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L053() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L053");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L053() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "정정 HFL4  출측Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L053()
	

	/**
	 * makeH2L054 : C열연정정 HFL4 Take-Out 실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L054(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L014
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L054";
		String szMsg              = "";
		String szOperationName    = "C열연정정 HFL4 Take-Out실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L054() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L054");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L054() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "C열연정정 HFL4 Take-Out실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L054()	
	

	/**
	 * YDH2L061 : 정정 HFL5 입측Line-In실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L061(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L011
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L061";
		String szMsg              = "";
		String szOperationName    = "C열연정정 L2 정정 HFL4 입측Line-In실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 				  = 19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L061() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP == null || szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L061");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L061() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "[YDH2L061] 정정입측  HFL5 Line-In실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			
			return -1;
		}
		return outRecSet.size();

	} // end of makeH2L061()



	
	
	/**
	 * YDH2L063 : 정정 HFL5 출측Line-Off실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L063(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L013
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정입측Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L063";
		String szMsg              = "";
		String szOperationName    = "C열연정정L2 정정 HFL5 출측Line-Off실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L063() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L063");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L063() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
		}catch(Exception e){
			szMsg = "정정 HFL5  출측Line-Off실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L063()
	

	/**
	 * makeH2L064 : C열연정정 HFL5 Take-Out 실적송신
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeH2L064(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1.	전문 ID					MSG_ID					VARCHAR2(8)		YDH2L014
		//		2.	생성일					DATE					VARCHAR2(10)	YYYY-MM-DD
		//		3.	생성시간					TIME					VARCHAR2(8)		24HH-MM-SS
		//		4.	전문구분					MSG_GP					VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
		//		5.	전문길이					MSG_LEN					NUMBER(4)		
		//		6.	임시						TEMP					VARCHAR2(29)		
		//		7.	야드설비ID				YD_EQP_ID				VARCHAR2(6)		"HHKE01" SPM1입측, "HEKE01" SPM2입측, HGKE01" HFL 입측 
		//		8.	야드적치Bed번호			YD_STK_BED_NO			VARCHAR2(2)		정정Converyor No("03"~"01")
		//		9.	재료번호					STL_NO					VARCHAR2(11)	

		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();

		// 변수선언
		String szMethodName       = "makeH2L064";
		String szMsg              = "";
		String szOperationName    = "C열연정정 HFL5 Take-Out실적송신";
		String szSTL_NO           = "";
		String szYD_EQP_ID        = "";
		String szYD_STK_BED_NO    = "";
		String szEQP_GP           = "";
		String szSEARCHKEY        = "";
		
		// TC Length = 60 + 19 = 79
		int nTcLen 					=19;

		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L064() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);
					
			// 항목추출
			szYD_EQP_ID     = ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO");
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");			
			szSEARCHKEY     = szYD_EQP_ID + szYD_STK_BED_NO;
			
			// 설비ID와 BED번호로 설비구분값을 매칭을 시킴
			szEQP_GP = (String)YdCommonUtils.h_hstEqpGpMatch.get(szSEARCHKEY);
			if(szEQP_GP.equals("")){
				szMsg = "설비ID[" + szYD_EQP_ID + "]와 BED번호 [" + szYD_STK_BED_NO + "] 로 매칭되는 설비 구분값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("MSG_ID" , "YDH2L064");
			outRec.setField("DATE"   , YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"   , YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP" , "I");
			outRec.setField("MSG_LEN", YdUtils.fillSpZr("" + nTcLen, 4, 0));
			outRec.setField("TEMP"   , YdUtils.fillSpZr("", 29, 1));

			// 전문편성
			outRec.setField("YD_EQP_ID"    , YdUtils.fillSpZr(szEQP_GP, 6, 1));
			outRec.setField("YD_STK_BED_NO", YdUtils.fillSpZr(szYD_STK_BED_NO, 2, 1));
			outRec.setField("STL_NO"       , YdUtils.fillSpZr(szSTL_NO, 11, 1));
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeH2L064() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);						
		}catch(Exception e){
			szMsg = "C열연정정 HFL5 Take-Out실적송신  데이터 반환 중 예외발생! :: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
			return -1;
		}

		return outRecSet.size();

	} // end of makeH2L064()	
	


	

	
	
	
} // end of class MakeTcH2












