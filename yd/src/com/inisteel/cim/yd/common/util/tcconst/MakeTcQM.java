package com.inisteel.cim.yd.common.util.tcconst;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * QM (품질) 송신 용 전문 생성
 * @author YHWHman
 *
 */
public class MakeTcQM {
	// YDQMJ002 열연정정입측보급실적	
 
	
	
	//클래스명
	private static final String szClassName  = MakeTcQM.class.getName();

	
	
	/**
	 * YDQMJ002 : 열연정정입측보급실적 
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeQMJ001(JDTORecord inRec, JDTORecordSet outRecSet){
		//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDQMJ002
		//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)             
		//      3   PTOP_PLNT_GP        조업공장구분                     VARCHAR2(2)
		//      4   STL_APPEAR_GP       재료외형구분                     VARCHAR2(1)     
		//      5   STL_NO              재료번호                           VARCHAR2(11)    
 
		YdStockDao ydStockDao 	  = new YdStockDao();
		// 레코드 선언
		JDTORecord outRec         = null;		

		// DAO객체 및 유틸리티객체 생성
		YdDaoUtils ydDaoUtils     = new YdDaoUtils();	
		YdUtils ydUtils           = new YdUtils();
		JDTORecordSet rsGetYdCoilComm = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara 		 = null;
		
		// 변수선언
		String szMethodName       = "makeQMJ001";
		String szMsg              = "";
		String szOperationName    = "열연정정입측보급실적";
		String szSTL_NO           = "";
		String szPTOP_PLNT_GP     = "";
		String szSTL_APPEAR_GP    = "";
		int intRtnVal = 0;
		
		try{			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeQMJ001() IN==========================\n", 4);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
			szSTL_NO        = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
			
			JDTORecord jDtoParM	= JDTORecordFactory.getInstance().create();
			jDtoParM.setField("COIL_NO", szSTL_NO);
			
			// 코일공통 조회
			intRtnVal = ydStockDao.getYdStock(jDtoParM, rsGetYdCoilComm, 8);
			if(intRtnVal <= 0){
				szMsg ="[오류발생]: 코일정보조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 코일공통 조회결과 추출				
			rsGetYdCoilComm.first();
			recPara = rsGetYdCoilComm.getRecord();
			
			// 항목추출							
			szPTOP_PLNT_GP  = ydDaoUtils.paraRecChkNull(recPara, "HR_PLNT_GP");
			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(recPara, "STL_APPEAR_GP");
			 
 			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setField("JMS_TC_CD"         , "YDQMJ002");
			outRec.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));

			// 전문편성			
			outRec.setField("PTOP_PLNT_GP"     , szPTOP_PLNT_GP);
			outRec.setField("STL_APPEAR_GP"    , szSTL_APPEAR_GP);
			outRec.setField("STL_NO"           , szSTL_NO);
		
			// RecordSet에 추가	
			outRecSet.addRecord(outRec);

			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeQMJ001() OUT=========================\n", 4);	
			ydUtils.displayRecord(szOperationName, outRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", 4);	
		}catch(Exception e){
			szMsg = "열연정정입측보급실적 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
	
		return outRecSet.size();
	} // end of makeQMJ001()
	
	
//PIDEV_QM : 신규
	
	/**
	 * YDQMJ601  : 품질 후판입고작업실적
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeQMJ601(JDTORecord inRec, JDTORecordSet outRecSet){
		// 크레인스케줄Dao 객체 생성
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();
		YdUtils ydUtils         = new YdUtils();

		// 레코드 선언
		JDTORecord recPara 		= null;
		JDTORecord outRec 		= null;

		// 조회 결과를 담을 RecordSet생성
		JDTORecordSet rsGetYdCrnsch = JDTORecordFactory.getInstance().createRecordSet("");

		String szMsg            = "";
		String szMethodName     = "makeQMJ601";
		String szOperationName  = "품질 후판입고작업실적";
		
		// 제품번호
		String szSTL_NO 		= "";

		String szSTL_PROG_CD	= "";

		int intRtnVal = 0;

		try{
			// Debug Msg
			ydUtils.putLog(szClassName, szMethodName, "\n=======================IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);	
			ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);

			// 크레인스케줄 조회
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlPlate*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(inRec, rsGetYdCrnsch, 51);
			if(intRtnVal <=0){
				szMsg ="[오류발생]: 크레인스케줄조회 중 오류 ["+intRtnVal+"]" ;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
			}
			
			// 크레인스케줄 조회결과 추출
			for(int i=0; i<intRtnVal; i++){
				outRec  = JDTORecordFactory.getInstance().create();
				recPara = rsGetYdCrnsch.getRecord(i);

				// Debug Msg
				ydUtils.putLog(szClassName, szMethodName, "\n==================조회 후  내용 표시====================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, recPara);
				ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 추가업무 : 재료진도코드가 소재입고대기(2)추가
				 */
				szSTL_PROG_CD = ydDaoUtils.paraRecChkNull(recPara, "CURR_PROG_CD");
				if( !szSTL_PROG_CD.equals("2") && 
					!szSTL_PROG_CD.equals(YdConstant.PROG_CD_RCPT_WAIT) &&	
					!szSTL_PROG_CD.equals(YdConstant.PROG_CD_OVALL_STMP_WAIT) ) {
					szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]가 아니므로 출하관리로 후판입고작업실적 전송불가";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					continue;
				}
				szMsg = "재료진도코드[" + szSTL_PROG_CD + "]가 입고대기[" + YdConstant.PROG_CD_RCPT_WAIT + "]/종합판정대기[" + YdConstant.PROG_CD_OVALL_STMP_WAIT + "]이므로 출하관리로 후판입고작업실적 전송가능";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

				// 제품번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				
				//		1.	인터페이스ID			   JMS_TC_CD				VARCHAR2(8)		YDDMR002
				//		2.	전송일시				   JMS_TC_CREATE_DDTT		VARCHAR2(14)	YYYYMMDDHHMMSS
                //		3.	제품 번호				   STL_NO				    VARCHAR2(11)	STL_NO
				outRec.setField("JMS_TC_CD"          , new String("YDQMJ601"));
				outRec.setField("JMS_TC_CREATE_DDTT" , new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				outRec.setField("STL_NO"       , szSTL_NO);
				
				// RecordSet으로 반환
				outRecSet.addRecord(outRec);

				// Debug MSG
				ydUtils.putLog(szClassName, szMethodName, "\n======================OUT==========================\n", YdConstant.DEBUG);	
				ydUtils.displayRecord(szOperationName, outRec);
				ydUtils.putLog(szClassName, szMethodName, "\n===================================================\n", YdConstant.DEBUG);	

			} // end of for
			
		}catch(Exception e){
			szMsg = "[품질 후판입고작업실적] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);					
			return -1;
		}

		return outRecSet.size();
	} // end of makeQMJ601()	
//---------------------------------------------------------------------------	
} // end of class MakeTcQM
