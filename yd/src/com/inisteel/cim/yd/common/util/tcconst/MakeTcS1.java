/**
 * @(#)MakeTcS1.java
 * 
 * @version			1.0
 * @author 			조병기
 * @date			2013.05.07
 * 
 * @description		S1 (2후판전단정정L2) 송신 용 전문 생성 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2013.05.07   조병기      조병기      최초 등록
 * 
 */

package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

public class MakeTcS1 {

	// YDS1L001	파일링지시	

	// 클래스명
	private static final String SZ_CLASS_NAME  = MakeTcY8.class.getName();

	/**
	 * YDS1L004 : 파일링지시
	 * @param  JDTORecord inRec 
	 * @return JDTORecordSet outRecSet 
	 *
	 */
	public static int makeS1L004(JDTORecord inRec, JDTORecordSet outRecSet){

		YdUtils ydUtils 			= new YdUtils();
		YdDaoUtils ydDaoUtils 		= new YdDaoUtils();

		JDTORecord outRec 			= null;

		// 변수선언
		String szMethodName 		= "makeS1L004";
		String szMsg 				= "";
		String szOperationName      = "2후판전단정정L2 파일링지시";


		// TC Length =72 /60+12
		int nTcLen 					= 12;

		try{

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================[STEP 1]::makeS1L004==============\n", YdConstant.DEBUG);	

			ydUtils.displayRecord(szOperationName, inRec);

			outRec = JDTORecordFactory.getInstance().create();

			//		1.	전문 ID				MSG_ID				VARCHAR2(8)		YDS1L004
			//		2.	생성일				DATE				VARCHAR2(10)	YYYY-MM-DD
			//		3.	생성시간				TIME				VARCHAR2(8)		24HH-MM-SS
			//		4.	전문구분				MSG_GP				VARCHAR2(1)		I(신규), U(수정), D(취소,삭제), R(재 전송)
			//		5.	전문길이				MSG_LEN				NUMBER  (4)		
			//		6.	임시					TEMP				VARCHAR2(29)	
			//		7.	OP_ID				OP_ID				VARCHAR2(10)	Order plate id (Not null if pile instruction)
			//		8.	INSTRUCTION			INSTRUCTION			VARCHAR2(1)		0:Release , 1:Pile instruction
			//		9.	PILER_ROUTER		PILER_ROUTER		VARCHAR2(1)		1:DS#1, 2:DS#2	
			//																	 
			outRec.setField("MSG_ID"	,"YDS1L004");
			outRec.setField("DATE"		,YdUtils.getCurDate("yyyy-MM-dd"));
			outRec.setField("TIME"		,YdUtils.getCurDate("HH-mm-ss"));
			outRec.setField("MSG_GP"	,"I");
			outRec.setField("MSG_LEN"	,YdUtils.fillSpZr(Integer.toString(nTcLen), 4, 0));
			outRec.setField("TEMP"		,YdUtils.fillSpZr("", 29, 1));
			
			outRec.setField("OP_ID"			,YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "OP_ID")			, 10, 1));
			outRec.setField("INSTRUCTION"	,YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "INSTRUCTION")	,  1, 1));
			outRec.setField("PILER_ROUTER"	,YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inRec, "PILER_ROUTER")	,  1, 1));

			// RecordSet으로 반환
			outRecSet.addRecord(outRec);
			
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================[STEP 2]:: 송신 TC내용 ==============\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);

		}catch(Exception e){
			szMsg = "YDS1L004[2후판전단정정L2 파일링지시] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return outRecSet.size();

	} // end of makeY8L006()	

	//---------------------------------------------------------------------------	
}	// end of class MakeTcS1
