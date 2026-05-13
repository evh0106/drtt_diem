/**
 * @(#)PlateSpecRegSeEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2011/07/13
 * 
 * @description		이클래스는 Plate제원등록 Session EJB 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2011/07/13                    최초 등록
 * V1.01  2013/01/17   조병기       조병기      procPlGdsRetngds 메소드 수정 
 *                                     :저장품제원정보(YDY8L002) 후판제품 L2 로 송신 추가
 * V1.02  2013/03/26   조병기       조병기      개발표준점검에 의한 보완요청사항 수정 (개발표준검증결과서 참조)                                    
 * V1.03  2013/04/05   조병기       조병기      procPl2GdsPrdWr : 2후판 제품생산실적 (PPYDJ004) 추가                                
 */

package com.inisteel.cim.yd.ydStock.StockSpecReg;

import java.util.ArrayList;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ptOsCommDao.PtOsCommDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ymEtcDao.YmEtcDao;
import com.inisteel.cim.yd.common.dao.ydStrCharDao.YdStrCharDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.util.loc.YdToLocDcsnUtil;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.common.util.tcconst.TcConstMgr;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.ydPI.common.M10YdExLm21SenderFaEJBBean;
import com.inisteel.cim.ydPI.common.util.PIYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * Plate제원등록 Session EJB
 *
 * @ejb.bean name="PlateSpecRegSeEJB" jndi-name="PlateSpecRegSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class PlateSpecRegSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName = getClass().getName();
	private YdUtils ydUtils      = new YdUtils();
	private YdDaoUtils ydDaoUtils= new YdDaoUtils();
	private YdTcConst ydTcConst  = new YdTcConst();
	private YdDelegate ydDelegate = new YdDelegate();
	private TcConstMgr tcConstMgr =new TcConstMgr();
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private PIYdUtils     commPiUtils = new PIYdUtils();
	private M10YdExLm21SenderFaEJBBean      M10YdExLm21Sender   = new M10YdExLm21SenderFaEJBBean();
	
	// [DEBUG] message flag
	private static final boolean bDebugFlag=true;
	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판압연사양확정등록 (CTYDJ021)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procPlMillSpecCmmt(JDTORecord msgRecord)throws JDTOException  {
        //저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		
		JDTORecordSet rsOut   = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsOutYd = JDTORecordFactory.getInstance().createRecordSet("");
		
		JDTORecord recSetYd   = null;
		
		String szMsg		  				= "";
		String szMethodName	  				= "procPlMillSpecCmmt";
		String szOperationName              = "후판압연사양확정등록";
		String szPTOP_PLNT_GP 				= "";
		String szCT_MILL_SPEC_WRK_STAT_GP 	= "";
		String szPRPL_MILL_WO_DT 			= "";
		String szSTL_NO						= "";
		
		int intRtnVal = 0;

		JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord  	= JDTORecordFactory.getInstance().create();
		
		
		String szAPPLY_YN 			= "N";
		
		
		String szRcvTcCode=ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode==null){
			szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return ;
		}
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[생산통제] 후판압연사양확정등록 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			// 수신항목[PTOP_PLNT_GP: 조업공장구분]
			szPTOP_PLNT_GP 				= ydDaoUtils.paraRecChkNull(msgRecord,"PTOP_PLNT_GP");
			
			// 수신항목[CT_MILL_SPEC_WRK_STAT_GP: 생산통제사양작업상태구분]
			szCT_MILL_SPEC_WRK_STAT_GP 	= ydDaoUtils.paraRecChkNull(msgRecord,"CT_MILL_SPEC_WRK_STAT_GP");
			
			// 수신항목[PRPL_MILL_WO_DT: 공정계획압연지시일시]
			szPRPL_MILL_WO_DT 			= ydDaoUtils.paraRecChkNull(msgRecord,"PRPL_MILL_WO_DT");
			
			// 후판Plate사양 
			intRtnVal = ydStockDao.getYdStock(msgRecord, rsOut, 76);
			if (intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg = "\n PTOP_PLNT_GP: [" + szPTOP_PLNT_GP +"] \n CT_MILL_SPEC_WRK_STAT_GP: [" + szCT_MILL_SPEC_WRK_STAT_GP + "] \n PRPL_MILL_WO_DT: [" + szPRPL_MILL_WO_DT + "]을 만족하는 결과 값이 없음 \n=================================";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}else{
					szMsg= "getCtPlatspec [후판Plate사양] Error :: PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}	
			}
			
			rsOut.first();
			recSetYd = JDTORecordFactory.getInstance().create();
			
			for(int i =1; i <= rsOut.size(); i++){

				recSetYd = rsOut.getRecord();
				
				// [STL_NO: 재료번호]
				szSTL_NO = ydDaoUtils.paraRecChkNull(recSetYd,"STL_NO");

				ydUtils.putLog(szSessionName, szMethodName, "szSTL_NO" + szSTL_NO, YdConstant.ERROR);

				
				intRtnVal = ydStockDao.getYdStock(recSetYd, rsOutYd, 0);
				if(intRtnVal < 0){
					ydUtils.putLog(szSessionName, szMethodName, "[저장품] Error :: PARAMETER ERROR", 1);
					return ;
				} else if(intRtnVal == 0){
					recSetYd.setField("REGISTER"   	 , "CTYDJ021");		//등록		
					intRtnVal  = this.InsStock(recSetYd);
					if(intRtnVal <0)
					{
						ydUtils.putLog(szSessionName, szMethodName, "[저장품]INSERT Error !" + intRtnVal, 1);
						return ;
					} 
					ydUtils.putLog(szSessionName, szMethodName, szSTL_NO+"::[저장품]INSERT SUCCESS", 3);
					
				} else{

//??				recStock  = rsOutYd.getRecord(0);
//SJH01001  ERROR 발생함
					recSetYd.setField("REG_DDTT"   	 , null);		    //등록		
					recSetYd.setField("MODIFIER"   	 , "CTYDJ021");		//수정자
					intRtnVal = this.UpdYdStock(recSetYd,0);
					if(intRtnVal <0)
					{
						ydUtils.putLog(szSessionName, szMethodName, "[저장품]UPDATE Error !" + intRtnVal, 1);
						return ;
					} 
					ydUtils.putLog(szSessionName, szMethodName, "2)"+szSTL_NO+"::[저장품]UPDATE SUCCESS", 3);
				}
				
				rsOut.next();    //??
				
			} // end of for

//SJH05008
			//------------------------------------------------------------------------------------------------------------
			//	동별저장계획  적용여부
			//------------------------------------------------------------------------------------------------------------
			
			inRecord1.setField("REPR_CD_GP", "K00060");    //동별저장계획  적용 여부
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
			if(intRtnVal > 0) {
				outResult.first();
				outRecord  = outResult.getRecord();
				szAPPLY_YN = outRecord.getFieldString("ITEM1");				
			}
			szMsg="동별저장계획  적용여부 " + szAPPLY_YN ;
			ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);
			if(szAPPLY_YN.equals("Y")) {	
				
				EJBConnector ejbConn = new EJBConnector("default", "PlateSpecRegSeEJB", this);			
				ejbConn.trx("procYdBayLocPln", new Class[]  { JDTORecord.class }
												  , new Object[] { msgRecord  });
				
			}	
			
		}catch(Exception e){
	
			szMsg = "[후판압연사양확정등록수신] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}

		szMsg = "후판압연사양확정등록수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
	}// end of procPlMillSpecCmmt()
	
	/**
	 * 저장품 UPDATE
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int UpdYdStock(JDTORecord inRec, int intGp) throws JDTOException {
		YdStockDao ydStockDao = new YdStockDao();

		String szMethodName	 = "UpdYdStock";
		String szMsg		 = "";
	
		int intRtn = 0;
		
		try{
			intRtn = ydStockDao.updYdStock(inRec, intGp);
			switch(intRtn){
			case 0 :
				ydUtils.putLog(szSessionName, szMethodName, "NO DATA "+intRtn, 1);	   
			    return intRtn = -1;
			case -1	:
				ydUtils.putLog(szSessionName, szMethodName, "DUPLICATE DATA"+intRtn, 1);	   
			    return intRtn = -1;
			case -2	:
			    ydUtils.putLog(szSessionName, szMethodName, "PARAMETER ERROR"+intRtn, 1);	   
			    return intRtn = -1;
			case -3	:
				ydUtils.putLog(szSessionName, szMethodName, "EXECUTION FAILED"+intRtn, 1);	   
			    return intRtn = -1;
		}    	
			
		} catch(Exception e){
			
			szMsg="UpdYdStock[저장품]UPDATE Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		return intRtn;

	} //end of UpdYdStock()
	
	/**
	 * 저장품 INSERT
	 * @param inRec
	 *
	 * @throws JDTOException
	 */
	public int InsStock(JDTORecord inRec) throws JDTOException {
		YdStockDao ydStockDao = new YdStockDao();

		String szMethodName	 = "InsStock";
		String szMsg		 = "";
	
		int intRtn = 0;
		
		try{
			intRtn = ydStockDao.insYdStock(inRec);
			if(intRtn == -2){
				szMsg = "ydStockDao[저장품]INSERT ERROR :: PARAMETER ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtn = -1;
			}
		} catch(Exception e){
			
			szMsg="ydStockDao[저장품]INSERT Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		return intRtn;

	} //end of InsStock()
	
	
	/**
	 * A후판 슬라브분할실적 (PRYDJ003)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procAPlSlabDivWr(JDTORecord inRecord)throws JDTOException  {
		String szMethodName = "procAPlSlabDivWr";
		String szMsg = "[ERROR] 이 전문 사용하지 않음[PRYDJ003]";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	} // end of procAPlSlabDivWr()
	
	/**
	 *      [A] 오퍼레이션명 : A후판 제품생산실적 (PRYDJ004)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlGdsPrdWr(JDTORecord msgRecord)throws JDTOException  {
		
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao     = new YdStockDao();
		
		// 레코드 선언
		JDTORecordSet rsOutRecSet = null;
		JDTORecordSet rsGetStock  = null;
		JDTORecord outRec         = null;
		JDTORecord recIn          = null;
		JDTORecord recEdit        = null;
		JDTORecord recPara        = null;
		JDTORecord recUSRCTAEdit2 = null;
		JDTORecord recInTemp      = null;
		JDTORecord recGetVal      = null;
		
		// 변수 선언
		String szMethodName       = "procAPlGdsPrdWr";
		String szMsg              = "";
		String szOperationName    = "A후판 제품생산실적";
		String szSTL_NO           = "";
		String szPL_RCPT_LN_GP    = "";
		String szPL_WRK_PROC      = "";
		String szPL_RCPT_TRK_NO   = "";
		String szPL_RCPT_DDTT     = "";
		
		String szPilingCd 				= null;
		String szYdRcptPlnStrLoc 		= null;
		String szYdBookOutLoc 			= null;

		YdEqpDao   ydEqpDao   = new YdEqpDao();
		JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord1  = JDTORecordFactory.getInstance().create();
		
		String szAPPLY_YN 				= "N";
		String szAPPLY_YN130			= "N";
		String szAPPLY_YN250			= "N"; // 후판 무상샘플제 T999999999 출하I/F 적용여부(Y: 전송, N:전송안함)
		
		int Str_Count 			  		= 0;
		long lnPL_TOT_ROUTE_CNT   		= 0;
		int intRtnVal             		= 0;
		int index 						= 0;
		
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		if(szRcvTcCode == null){
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
				
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[후판조업] 제품생산실적 수신";
			ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			// 수신받은 전문에서 재료번호 추출
			szSTL_NO 		 = msgRecord.getFieldString("STL_NO");
			szPL_RCPT_LN_GP  = msgRecord.getFieldString("PL_RCPT_LN_GP"); // 후판입고Line구분
			szPL_WRK_PROC    = msgRecord.getFieldString("PL_WRK_PROC");   // 후판공정코드
			
			// 레코드 생성
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			rsGetStock  = JDTORecordFactory.getInstance().createRecordSet("");
			recEdit     = JDTORecordFactory.getInstance().create();

			// PLATE공통 조회  Dao 호출 - [GP : 4]
			//==============================================================================================
			// 2009.11.17 권오창  : 쿼리수정 (172)
			//    com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMMOSCOMM
			// 
			//    조회 후 저장품에 업데이트 시 ORD_GP과 DEST_CD가 없음 
			//    PLATECOMM 과 OSCOMM을 조인걸어서 가져옴
			//==============================================================================================
			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("PLATE_NO", szSTL_NO);
			/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMMOSCOMM */
			intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 172);
			if(intRtnVal < 0){
				szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + szSTL_NO + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + szSTL_NO + ") [" + intRtnVal + "]" + "DO NOT EXIST";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}
			
			rsOutRecSet.first();
			recGetVal = rsOutRecSet.getRecord();
			
			recGetVal.setField("PL_RCPT_LN_GP", szPL_RCPT_LN_GP);
			
			// PLATE공통 테이블에서 읽은 전문 항목편집
			intRtnVal = this.edtPlateComm3G(recGetVal, recEdit, "PA");
			if(intRtnVal < 0){
				szMsg= "PLATECOMM[PLATE공통] 항목 편집 Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//-------------------------------------------------------------------------------------------------------
			//3기 이후 1후판 ON-LINE 이 56, 59 일 수 있기 때문에
			//edtPlateComm 에서 BOOK-OUT LOC 가  59이더라도 1O 이면 56으로 , 56은 AO 이면 59 로 변경한다.
			szYdBookOutLoc 			= ydDaoUtils.paraRecChkNull(recEdit,"YD_BOOK_OUT_LOC");
			if(szYdBookOutLoc.startsWith("59")&&"1O".equals(szPL_WRK_PROC)) {
				//szYdBookOutLoc = "56" + szYdBookOutLoc.substring(2);
				//59000 --> 56000 으로 변경
				szYdBookOutLoc = StringHelper.evl(YdCommonUtils.getY4ChgABookOutLoc(szYdBookOutLoc),"");
			} else if(szYdBookOutLoc.startsWith("56")&&"AO".equals(szPL_WRK_PROC)) {
				//szYdBookOutLoc = "59" + szYdBookOutLoc.substring(2); 
				//56000 --> 59000 으로 변경
				szYdBookOutLoc = StringHelper.evl(YdCommonUtils.getY4ChgCBookOutLoc(szYdBookOutLoc),"");
			}
			recEdit.setField("YD_BOOK_OUT_LOC", szYdBookOutLoc);
			//-------------------------------------------------------------------------------------------------------
			
			/*
			 * PLATE공통에 UPDATE할 레코드.
			 * 상위의 메소드에서는 주문재일 경우 OS공통의 BOOK OUT 및 예정위치를 가져온다.
			 * 신규일경우는 OS공통정보, 수정일 경우는 저장품정보를 PLATE공통에 UPDATE한다.
			 */
			recInTemp  = JDTORecordFactory.getInstance().create();
			
			// 저장품 조회를 해서 존재하면 UPDATE 없으면 INSERT 처리
			intRtnVal = ydStockDao.getYdStock(recEdit, rsGetStock, 0);
			if(intRtnVal < 0){
				szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}else if(intRtnVal == 0){
				
				szMsg = "YD_STOCK[저장품] INSERT :: ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				// INSERT
				recEdit.setField("REGISTER", "PRYDJ004");
				
				/*--------------------------------------------------------------
				 * PLATE 공통 UPDATE 레코드
				 */
				recInTemp.setField("PLATE_NO", 			szSTL_NO);
				recInTemp.setField("YD_PILING_CD", 		ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD"));
				recInTemp.setField("YD_BOOK_OUT_LOC", 	ydDaoUtils.paraRecChkNull(recEdit,"YD_BOOK_OUT_LOC"));
				
				/*--------------------------------------------------------------
				 * 2010.03.02 이영근
				 * 후판창고입고일시, 후판생산실적번호(후판공정코드 + 년월일시분초) 항목 추가 
				 */
				szPL_RCPT_DDTT   = YdUtils.getCurDate("yyyyMMddHHmmss");
				szPL_RCPT_TRK_NO = szPL_WRK_PROC + YdUtils.getCurDate("yyyyMMddHHmmss");
				
				recEdit.setField("PL_RCPT_DDTT",   szPL_RCPT_DDTT);    // 후판창고입고일시                                                  char(14) 'YYYYMMDDHHMMSS'
				recEdit.setField("PL_RCPT_TRK_NO", szPL_RCPT_TRK_NO);  // 후판생산실적번호(후판공정코드 + 년월일시분초) char(16) '1MYYYYMMDDHHMMSS'
				//--------------------------------------------------------------
				
				intRtnVal = ydStockDao.insYdStock(recEdit);
				if(intRtnVal < 0){
					szMsg = "YD_STOCK[저장품] INSERT Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				szMsg = "YD_STOCK[A후판제품생산실적수신] INSERT SUCCESS";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				
				szMsg = "YD_STOCK[저장품] UPDATE ::";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				// UPDATE
				recEdit.setField("MODIFIER", "PRYDJ004");
				
				String sOrdYeojaeGp = ydDaoUtils.paraRecChkNull(recGetVal,"ORD_YEOJAE_GP");
				
				/*------------------------------------------------------------------------*/
				rsGetStock.absolute(1);
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setRecord(rsGetStock.getRecord());
				
				szPilingCd 				= ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD");
				szYdBookOutLoc 			= ydDaoUtils.paraRecChkNull(recEdit,"YD_BOOK_OUT_LOC");
				szYdRcptPlnStrLoc 		= ydDaoUtils.paraRecChkNull(recEdit,"YD_RCPT_PLN_STR_LOC");
				
				ydUtils.putLog(szSessionName, szMethodName, "szPilingCd="+szPilingCd, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "szYdBookOutLoc="+szYdBookOutLoc, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "szYdRcptPlnStrLoc="+szYdRcptPlnStrLoc, YdConstant.DEBUG);
				
				recEdit.setField("YD_PILING_CD",   		szPilingCd);   
				recEdit.setField("YD_BOOK_OUT_LOC",   	szYdBookOutLoc);   
				recEdit.setField("YD_RCPT_PLN_STR_LOC", szYdRcptPlnStrLoc);
				//--------------------------------------------------------------
						
				/*--------------------------------------------------------------
				 * PLATE 공통 UPDATE 레코드
				 */
				recInTemp.setField("PLATE_NO", 			szSTL_NO);
				recInTemp.setField("YD_PILING_CD", 		szPilingCd);
				recInTemp.setField("YD_BOOK_OUT_LOC", 	szYdBookOutLoc);
				/*--------------------------------------------------------------
				 * 2010.03.02 이영근
				 * 후판창고입고일시, 후판생산실적번호(후판공정코드 + 년월일시분초) 항목 추가 
				 */
				szPL_RCPT_DDTT   = YdUtils.getCurDate("yyyyMMddHHmmss");
				szPL_RCPT_TRK_NO = szPL_WRK_PROC + YdUtils.getCurDate("yyyyMMddHHmmss");
				
				recEdit.setField("PL_RCPT_DDTT",   szPL_RCPT_DDTT);    // 후판창고입고일시                                                  char(14) 'YYYYMMDDHHMMSS'
				recEdit.setField("PL_RCPT_TRK_NO", szPL_RCPT_TRK_NO);  // 후판생산실적번호(후판공정코드 + 년월일시분초) char(16) '1MYYYYMMDDHHMMSS'
				//--------------------------------------------------------------
				
				intRtnVal = ydStockDao.updYdStock(recEdit, 0);
				if(intRtnVal <= 0){
					szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				szMsg = "YD_STOCK[A후판제품생산실적수신] UPDATE SUCCESS";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//--------------------------------------------------------------------------------------------------------
			//	후판제품 생산실적 시간 UPDATE
			//--------------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szSTL_NO);
			
			szMsg = "[생산실적 수신 ]["+szSTL_NO+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			intRtnVal = ydStockDao.update_Dm_Time(recPara,1);

			outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
			inRecord1 	= JDTORecordFactory.getInstance().create();
			outRecord1  = JDTORecordFactory.getInstance().create();

			inRecord1.setField("REPR_CD_GP", "T00130");    //시험 시편재
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
			if(intRtnVal > 0) {
				outResult.first();
				outRecord1  	= outResult.getRecord();
				szAPPLY_YN130 	= outRecord1.getFieldString("ITEM1");				
			}
			szMsg="시험 시편재 적용 :" + szAPPLY_YN130 ;
			ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);

			if(szAPPLY_YN130.equals("Y")){

				String sORD_GP 			= ydDaoUtils.paraRecChkNull(recGetVal,"ORD_GP");
				String sORD_TP 			= ydDaoUtils.paraRecChkNull(recGetVal,"ORD_TP");
				String sORD_PATTERN_CD	= ydDaoUtils.paraRecChkNull(recGetVal,"ORD_PATTERN_CD");
				String sOrdYeojaeGp 	= ydDaoUtils.paraRecChkNull(recGetVal,"ORD_YEOJAE_GP");
				
				if("1".equals(sOrdYeojaeGp)&&
				   sORD_TP.equals("TC") && //OSCOMM내 ORDER TYPE (TC:시험생산+시편재)
				   sORD_GP.equals("T") &&  //OSCOMM내 수주구분 (T: 시험생산)
				   sORD_PATTERN_CD.equals("C") &&  //OSCOMM내 주문유형(C:시편재)
				   "1N".equals(szPL_WRK_PROC)) {
					
					//--------------------------------------------------------------------------------------------------------
					//	후판제품공통테이블에 Piling Code와 Book-Out위치 수정
					//--------------------------------------------------------------------------------------------------------
					/*com.inisteel.cim.yd.common.dao.ptPlateCommDao.updPtPlateCommPilingStrLocBookOut*/
					String szRtnMsg = DaoManager.updPtPlateComm(recInTemp, 6);
					
					if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
						szMsg= "후판제품공통테이블에 저장위치.파일링코드.예정위치 등록 시 오류발생 - 메세지 : " + szRtnMsg;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					
					szMsg= "후판제품공통테이블에 YD_PILING_CD, YD_BOOK_OUT_LOC 등록 완료 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
					//--------------------------------------------------------------------------------------------------------
					
					/* 후판 무상샘플제 T999999999 출하I/F 적용여부(Y: 전송, N:전송안함) 호출시작 */ 
					outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
					inRecord1 	= JDTORecordFactory.getInstance().create();
					outRecord1  = JDTORecordFactory.getInstance().create();

					inRecord1.setField("REPR_CD_GP", "T00250");    //시험 시편재
					
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
					intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
					if(intRtnVal > 0) {
						outResult.first();
						outRecord1  	= outResult.getRecord();
						szAPPLY_YN250 	= outRecord1.getFieldString("ITEM1");				
					}
					szMsg="후판 무상샘플제 T999999999 출하I/F 적용 :" + szAPPLY_YN250 ;
					ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);
					/* 후판 무상샘플제 T999999999 출하I/F 적용여부(Y: 전송, N:전송안함) 호출끝 */ 					
					
					if("N".equals(szAPPLY_YN250)){ // 후판 무상샘플제 T999999999 출하I/F 적용여부(Y: 전송, N:전송안함)
						szMsg ="[JSP Session "+ szOperationName +"] - 테스트용 시험재"+szSTL_NO+"용 입고작업실적송신 안함(서윤 매니저 요청)";				
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					}else {
						JDTORecord outRec1  = JDTORecordFactory.getInstance().create();
						String curDate = YdUtils.getCurDate("yyyyMMddHHmmss");
						
						//PIDEV			
//						String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APPPI0", "*", "*");
						
//						if("Y".equals(sApplyYnPI)) {
							
							outRec1.setField("MQ_TC_CD"       	 , "M10YDLMJ1012");
							outRec1.setField("MQ_TC_CREATE_DDTT" , new String(curDate));
							
							outRec1.setField("YD_GP"          	 , YdConstant.YD_GP_PLATE2_GDS_YARD);
							outRec1.setField("DIST_GOODS_GP"  	 , "P");
							outRec1.setField("YARD_GP" 		  	 , "");
							outRec1.setField("GOODS_NO"       	 , szSTL_NO);
							outRec1.setField("STORE_LOC_CD"   	 , YdConstant.YD_GP_PLATE2_GDS_YARD + "999999999");
							
							outRec1.setField("RECEIPT_DATE"   	 , curDate.substring(0, 8));
							outRec1.setField("RECEIPT_TIME"   	 , curDate.substring(8, 14));
							
//						} else {						
//						
//							outRec1.setField("TC_CODE"      		, "YDDMR002");
//							outRec1.setField("TC_CREATE_DDTT"		, new String(curDate));
//							outRec1.setField("GOODS_NO"       		, szSTL_NO);
//							outRec1.setField("RECEIPT_DATE"   		, curDate.substring(0, 8));
//							outRec1.setField("RECEIPT_TIME"   		, curDate.substring(8, 14));
//							outRec1.setField("YD_GP"          		, YdConstant.YD_GP_PLATE2_GDS_YARD);
//							outRec1.setField("STORE_LOC"      		, YdConstant.YD_GP_PLATE2_GDS_YARD + "999999999");
//							outRec1.setField("PROD_ITEM_CODE" 		, "");
//							outRec1.setField("JMS_TC_CD"			, "YDDMR002");
//							outRec1.setField("MultiSend" 			, "Y");
//						
//						}
						
						this.sndJMSInfo(outRec1);
						szMsg ="[JSP Session "+ szOperationName +"] - 테스트용 시험재"+szSTL_NO+"용 입고작업실적송신";				
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
//PIDEV_QM						
//						if("Y".equals(sApplyYnPI)) {
							JDTORecord outRec2  = JDTORecordFactory.getInstance().create();
							outRec2.setField("JMS_TC_CD"      		, "YDQMJ601");
							outRec2.setField("JMS_TC_CREATE_DDTT"	, new String(curDate));
							outRec2.setField("STL_NO"       		, szSTL_NO);

							this.sndJMSInfo(outRec2);
							szMsg ="[JSP Session "+ szOperationName +"] - 품질테스트용 시험재"+szSTL_NO+"용 입고작업실적송신";				
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						}
					}	
					return ;
				}
			}
			//--------------------------------------------------------------------------------------------------------
			//	후판제품공통테이블에 Piling Code와 Book-Out위치 수정
			//--------------------------------------------------------------------------------------------------------
			String szRtnMsg = DaoManager.updPtPlateComm(recInTemp, 0);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg= "후판제품공통테이블에 YD_PILING_CD, YD_BOOK_OUT_LOC 등록 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			szMsg= "후판제품공통테이블에 YD_PILING_CD, YD_BOOK_OUT_LOC 등록 완료 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
			//--------------------------------------------------------------------------------------------------------
			
			
			/*=====================================================================================
			 * 2010.02.24 이영근
			 * PLATE공통 여재구분이 여재이고,정상 온라인입고 대상일 경우 SMS L2로 Production Infomation2 정보 재송신
			 *            
			 * ORD_YEOJAE_GP	: 주문여재구분
			 * YD_PILING_CD		: 여재다운시 변경된 PILING CD
			 =====================================================================================*/	
			String sOrdYeojaeGp = ydDaoUtils.paraRecChkNull(recGetVal,"ORD_YEOJAE_GP");
			String sYdPilingCd	= ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD");
			
			if("2".equals(sOrdYeojaeGp)&&
			   ("1O".equals(szPL_WRK_PROC)||"1N".equals(szPL_WRK_PROC)||"1M".equals(szPL_WRK_PROC))){
				recUSRCTAEdit2     = JDTORecordFactory.getInstance().create();
				
				recUSRCTAEdit2.setField("YD_PILING_CD", sYdPilingCd);  // 야드 PILING CD
				recUSRCTAEdit2.setField("PL_PLATE_NO",  szSTL_NO);     // PLATE NO

				szMsg = "[CT_후판PI2작업지시 (USRCTA.TB_CT_N_PLMILLDIVPLNGDSWO)  Update] YD_PILING_CD :: [" + sYdPilingCd + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szMsg = "[CT_후판PI2작업지시 (USRCTA.TB_CT_N_PLMILLDIVPLNGDSWO)  Update] szSTL_NO :: [" + szSTL_NO + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				intRtnVal = ydStockDao.updateTBCTCOMMON(recUSRCTAEdit2	,1);
				
				if(intRtnVal <= 0){
					szMsg = "CT_후판PI2작업지시 USRCTA.TB_CT_N_PLMILLDIVPLNGDSWO UPDATE Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			
				szMsg = "CT_후판PI2작업지시 USRCTA.TB_CT_N_PLMILLDIVPLNGDSWO UPDATE 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//---------------------------------------------------------------
				
				//Routing Layout 재작업지시 송신
				this.procSmsSend(szSTL_NO	,1);		
				
		    	szMsg = "CT_후판PI2 재작업지시 송신 :: [" + szSTL_NO + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
				
			}

			outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
			inRecord1 	= JDTORecordFactory.getInstance().create();
			outRecord1  = JDTORecordFactory.getInstance().create();
			
			inRecord1.setField("REPR_CD_GP", "T00100");    //EF동 관리
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
			if(intRtnVal > 0) {
				outResult.first();
				outRecord1  = outResult.getRecord();
				szAPPLY_YN = outRecord1.getFieldString("ITEM1");				
			}
			szMsg="EF동 관리 적용 :" + szAPPLY_YN ;
			ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*=====================================================================================
			 * Louting 지시 전문 편집 
			 * 	- 지시 없으면 INSERT
			 *  - 지시 있으면 UPDATE            
			 * Routing 재작업지시 송신(procSmsSend)
			 * 모듈명 : PlateSpecRegSeEJBBean  procSmsSend(String sPlateNo)
			 =====================================================================================*/	
			String sYdBookOutLoc = ydDaoUtils.paraRecChkNull(recEdit,"YD_BOOK_OUT_LOC");

			szMsg="sYdBookOutLoc:" + sYdBookOutLoc + "/////szPL_WRK_PROC:" + szPL_WRK_PROC ;
			ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 검사대 통과시점에 ON-OFF LINE에 따른 저장위치 강제 UPDATE
			 */
			if (szPL_WRK_PROC.equals("1O")||
				szPL_WRK_PROC.equals("1N")||	
				szPL_WRK_PROC.equals("1M")||	
				szPL_WRK_PROC.equals("AO")){ 
				  
				JDTORecordSet rsTemp  	= null;
				rsTemp  	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				
				recPara.setField("STL_NO",         szSTL_NO);
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				
				//적치단정보 조회
				YdStkLyrDao	ydStkLyrDao	= new YdStkLyrDao();
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp, 3);
				
				/*
				 * 2011.10.16 윤재광
				 * 야드맵상으로 저자위치가 없는 대상만 초기화한다.
				 * - 정보반납,반송때문에.
				 */
				if(rsTemp.size() == 0){
					
					String sBayGp = "";
					
					if(szPL_WRK_PROC.equals("1O")){
						sBayGp = "E"; 
					}else if(szPL_WRK_PROC.equals("AO")){
						sBayGp = "D"; 
					}else{
						sBayGp = "F"; 
					}
					
					JDTORecord 	  setRecord 		= JDTORecordFactory.getInstance().create();
					setRecord.setField("YD_GP",        		YdConstant.YD_GP_PLATE2_GDS_YARD);
					setRecord.setField("YD_BAY_GP",    		sBayGp);
					setRecord.setField("YD_EQP_GP",    		"RT");
					setRecord.setField("YD_STK_COL_NO",		"PA");
					setRecord.setField("YD_STK_BED_NO", 	"");
					setRecord.setField("YD_STK_LYR_NO", 	"");
					setRecord.setField("FNL_REG_PGM",  		"PRYDJ004"+szPL_WRK_PROC);
					setRecord.setField("MODIFIER",     		"PRYDJ004"+szPL_WRK_PROC);
					setRecord.setField("YD_STR_LOC_HIS1", 	"") ;
					setRecord.setField("YD_STR_LOC_HIS2", 	""); 
					setRecord.setField("PLATE_NO",    		szSTL_NO); 
					//setRecord.setField("YD_STR_LOC", 		"K"+sBayGp+"RTPA");
					setRecord.setField("YD_STR_LOC", 		YdConstant.YD_GP_PLATE2_GDS_YARD+sBayGp+"RTPA");
					
					intRtnVal = ydStockDao.updPtComm_LOC(setRecord, 1);
				    if (intRtnVal <= 0) {
				        if (intRtnVal == 0) {
				            szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				        } else if (intRtnVal == -2) {
				            szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				        }
				    }
				}
			}
			
			/*=====================================================================================
			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    		 * 업무기준 : A후판제품생산실적수신 시 입고트래킹 파일링 변경정보 송신기능
    		 * 수정자 : 윤 재광
    		 * 수정일자 : 2010.04.27
    		 * 파라미터 : 재료번호,파일링코드,북아웃코드 
    		 * 1:파일링실적, 2: 56000도착, 3:검사대통과[1O],4:검사대통과[1N],5:검사대통과[1M],6:D/S실적,7:기타 
    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			if (szPL_WRK_PROC.equals("1O")){
			
				this.procChangePilingCd(szSTL_NO, sYdPilingCd, sYdBookOutLoc);
			}
			/*=====================================================================================
			
			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    		 * 업무기준 : A후판제품생산실적수신 시 저장품 제원 야드L2로 전송
    		 * 수정자 : 임춘수
    		 * 수정일자 : 2009.08.24
    		 *
    		 * 1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			recInTemp  = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID"         , "YDY8L002");
			recInTemp.setField("YD_INFO_SYNC_CD", "A");							
	    	recInTemp.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recEdit, "STL_NO"));
	    	recInTemp.setField("YD_STK_COL_GP"  , YdConstant.YD_GP_PLATE2_GDS_YARD);
	    	ydDelegate.sendMsg(recInTemp);
		    	
	    	// 전사물류개선 2021. 4. 3 생산실적 발생시 동일하게 Y9도 전송처리한다.
	    	recInTemp.setField("MSG_ID"         , "YDY9L002");
	    	ydDelegate.sendMsg(recInTemp);
	    	
	    	szMsg = "<procAPlGdsPrdWr> A후판제품생산실적수신 시 저장품 제원 야드L2[YDY4L002]로 전송";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

			szMsg = "A후판제품생산실적수신 처리(" + szMethodName + ") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[A후판제품생산실적수신] Exception Error: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	}// end of procAPlGdsPrdWr()
	
	/**
	 *      [A] 오퍼레이션명 :입고트래킹 파일링변경정보 처리.
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procChangePilingCd(	String sOrgPlateNo,
									String sOrgYdPilingCd,
									String sOrgYdBookOutLoc)throws JDTOException  {
		// 변수 선언
		String szMethodName       = "procChangePilingCd";
		String szMsg              = "";
		
		YmEtcDao ydStockDao       = new YmEtcDao();
		
		try{
			szMsg = "파일링변경정보 파라미터 :: STL_NO[" + sOrgPlateNo + "]PILINGCD[" + sOrgYdPilingCd + "]BOOKOUT[" + sOrgYdBookOutLoc + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szMsg = "입고트래킹 파일링변경정보 처리   프로시져 호출";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			JDTORecord recOut = ydStockDao.callSpYmEtcDao_NEW(sOrgPlateNo);
			
			this.procSmsSend(sOrgPlateNo,1);
			
		}catch(Exception e){
			szMsg = "입고트래킹 파일링변경정보 처리   Exception Error: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	}// end of procChangePilingCd()
	
	
	/**
	 *      [A] 오퍼레이션명 : 2후판 입고트래킹 파일링변경정보 처리.
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procChangePilingCd3G(	String sOrgPlateNo,
										String sOrgYdPilingCd,
										String sOrgYdBookOutLoc,
										String sPlWrkProc )throws JDTOException  {
		// 변수 선언
		String szMethodName       = "procChangePilingCd3G";
		String szMsg              = "";
		String szRtnCd			  = "";
		
		JDTORecord recInTemp      = null;
		
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		
		try{
			szMsg = "파일링변경정보 파라미터 :: STL_NO[" + sOrgPlateNo + "] PILINGCD[" + sOrgYdPilingCd + "] BOOKOUT[" + sOrgYdBookOutLoc + "] PL_WRK_PROC["+sPlWrkProc+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 1. 파일링 변경정보 체크 Procedure 호출 
			 */
			szMsg = "2후판 입고트래킹 파일링변경정보 처리   프로시져 호출";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			Object[] inParam = { 
								 sOrgPlateNo
								,sPlWrkProc
			   				   };

			int[] inParamIndex = {1,2};		
	
			//call SP_YD_PLATE_PILING_CHANGE_PB(?,?,?)
			JDTORecord record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0002");
	
			if(record == null || record.size() > 0){
				/*
				 * S : Auto 파일링 SKIP  
				 * 1 : 파일링 명령 (파일링하여 잡고 있어라)
				 * 0 : 해당 제품 위로 AP 가 가진 재료를 내려 놓아라(파일링 END)
				 * 그외 : 에러 처리로 SKIP 처리
				 */
				szRtnCd = ydDaoUtils.paraRecChkNull(record, "OUT_RTN_CODE");
				
				szMsg = "파일링프로시져(SP_YD_PLATE_PILING_CHANGE_PB) 호출 결과  :: [" + szRtnCd + "] ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
				
				if("1".equals(szRtnCd)) {
					
					recInTemp  = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID"         , "YDS1L004");
					recInTemp.setField("OP_ID"			, sOrgPlateNo);							
			    	recInTemp.setField("INSTRUCTION"    , "1"); //pile instruction
			    	if("2O".equals(sPlWrkProc)) {
				    	recInTemp.setField("PILER_ROUTER"   , "1"); //DS#1
			    	 } else if("BO".equals(sPlWrkProc)){
				    	recInTemp.setField("PILER_ROUTER"   , "2"); //DS#2
			    	} else {
			    		recInTemp.setField("PILER_ROUTER"   , "3"); //DS#3
			    	}
			    	
			    	ydDelegate.sendMsg(recInTemp);	
			    	
			    } else if("0".equals(szRtnCd)){

					recInTemp  = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID"         , "YDS1L004");
					recInTemp.setField("OP_ID"			, sOrgPlateNo);							
			    	recInTemp.setField("INSTRUCTION"    , "0"); //release
			    	if("2O".equals(sPlWrkProc)) {
				    	recInTemp.setField("PILER_ROUTER"   , "1"); //DS#1
			    	} else if("BO".equals(sPlWrkProc)){
				    	recInTemp.setField("PILER_ROUTER"   , "2"); //DS#2
			    	} else {
			    		recInTemp.setField("PILER_ROUTER"   , "3"); //DS#3
			    	}
			    	
			    	ydDelegate.sendMsg(recInTemp);	
				} 
						
			}					
			
		}catch(Exception e){
			szMsg = "2후판 입고트래킹 파일링변경정보 처리   Exception Error: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	}// end of procChangePilingCd3G()	
	
	/**
	 *      [A] 오퍼레이션명 :Routing Layout 재작업지시 송신
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procSmsSend(String sPlateNo,int intGbn)throws JDTOException  {
		
		// DAO 및 UTIL 객체 생성
		YmEtcDao ydStockDao     = new YmEtcDao();
		
		// 레코드 선언
		JDTORecordSet rsOutRecSet = null;
		JDTORecord recIn          = null;
		JDTORecord recGetVal      = null;
		
		// 변수 선언
		String szMethodName       = "procSmsSend";
		String szMsg              = "";
		String szOperationName    = "SMS L2 재작업지시";
		int intRtnVal             = 0;
		
		try{
			
			// 레코드 생성
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");

			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("PL_PLATE_NO", sPlateNo);
			intRtnVal = ydStockDao.getYmEtcDao(recIn, rsOutRecSet, intGbn);
			
			if(intRtnVal < 0){
				szMsg = "PLATECOMM[PLATE작업지시] Error :: STL_NO(" + sPlateNo + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "PLATECOMM[PLATE작업지시] Error :: STL_NO(" + sPlateNo + ") [" + intRtnVal + "]" + "DO NOT EXIST";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}
			
			rsOutRecSet.first();
			recGetVal = rsOutRecSet.getRecord();
			
			if(intGbn == 0){
				String sMessage = recGetVal.getFieldString("TL3CRL");
				String sRetVal = ydDelegate.sndSms(sMessage,"PRP2L008");
				
				/*
				 * L2전단서버 분리에 따른 라우팅 재지시
				 */
				sMessage = "0035729108"+sMessage.substring(10);
				sRetVal  = ydDelegate.sndSms(sMessage,"PRP2L008");
				
				szMsg = "ROUTING LAYOUT 재작업지시  처리(" + szMethodName + ") 완료["+sRetVal+"]";
			}else if(intGbn == 1){
				String sMessage = recGetVal.getFieldString("TL3CP2");
				String sRetVal = ydDelegate.sndSms(sMessage,"PRP2L010");
				szMsg = "PRODUCTION INFOMATION 2 재작업지시  처리(" + szMethodName + ") 완료["+sRetVal+"]";
			}
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "SMSD 재작업지시   Exception Error: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	}// end of procSmsSend()
	
	/**
	 * PLATECOMM[PLATE공통] 편집 - procPl2GdsPrdWr 에서 호출 (2후판 전용 => 1,2후판 공통으로 수정)
	 * return  0: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtPlateComm3G(JDTORecord recIn, JDTORecord recSet,String szPTOP_PLNT_GP) throws JDTOException {
		//저장품DAO
		YdStockDao ydStockDao     	= new YdStockDao();
		YdStrCharDao ydStrCharDao 	= new YdStrCharDao();		
		YdPlateCommDAO commDao 	  	= new YdPlateCommDAO();
		PtOsCommDao ptOsCommDao   	= new PtOsCommDao();
		
		JDTORecordSet rsOut    	  	= null;
		JDTORecordSet rsGetStock  	= null;
		JDTORecord recEdit	   	  	= null;
		JDTORecord recTemp		  	= null;
		JDTORecord outRec         	= null;
		JDTORecord outRec1			= null;
		
		String szMethodName	      	= "edtPlateComm3G";
		String szMsg		      	= "";
		String szYD_MTL_W_GP      	= "";
		String szYD_MTL_L_GP      	= "";
		String szORD_YEOJAE_GP    	= "";
		String szYD_PILING_CD     	= ""; 
		String szYD_STRCHAR_GRP_CD 	= "";
		String szYD_BOOK_OUT_LOC   	= "";
		String szYD_RCPT_PLN_STR_LOC = null;
		String szCUST_CD  	      	= "";
		String szSTL_APPEAR_GP 		= "";
		String szYD_AIM_BAY_GP 		= "";
		String szARR_WLOC_CD 		= "";
		int    intRtnVal			= 0;
		
		JDTORecordSet rsResult		= null;
		JDTORecord recInTemp   		= null;
		JDTORecord recOutTemp  		= null;		
		JDTORecord recPara			= null;
		
		String szSTRCHAR_GRP_CUST_CD = "";
		double dblORD_CONV_T  		= 0;
		int	   iORD_EA	            = 0;
		double dblheight 			= 0;
		double dbPLATE_WO_W			= 0;
		
	
		try{
			//-------------------------------------------------------------------------------------------------------------
			//	후판제품의 길이구분/폭구분 구하기
			//-------------------------------------------------------------------------------------------------------------
			recTemp		= JDTORecordFactory.getInstance().create();
			
			recTemp.setField("YD_MTL_L", 		ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_L"));
			recTemp.setField("YD_MTL_W", 		ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_W"));
			//-------------------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------------------
			//	여재구분에 따른 Piling Code, Book-Out위치 설정
			//-------------------------------------------------------------------------------------------------------------
			szORD_YEOJAE_GP = ydDaoUtils.paraRecChkNull(recIn,"ORD_YEOJAE_GP");
			
			if(szORD_YEOJAE_GP.equals("1")){
				
				//-------------------------------------------------------------------------------------------------------------
				//	주문재인 경우 OS공통테이블의 정보를 조회해서  Piling Code, Book-Out위치, 입고예정위치 설정
				//-------------------------------------------------------------------------------------------------------------
				recEdit = JDTORecordFactory.getInstance().create();
				recEdit.setField("ORD_NO",  ydDaoUtils.paraRecChkNull(recIn,"ORD_NO"));
				recEdit.setField("ORD_DTL", ydDaoUtils.paraRecChkNull(recIn,"ORD_DTL"));
				
				rsOut = JDTORecordFactory.getInstance().createRecordSet("");
				//OS공통조회
				intRtnVal = ydStockDao.getYdStock(recEdit, rsOut, 88);
				if(intRtnVal <= 0){
					if(intRtnVal == 0){
						szMsg= "OSCOMM[OSCOMM] Error :: [" + ydDaoUtils.paraRecChkNull(recIn,"ORD_NO") + "]"+"DO NOT EXIST";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return -1;
					}else{
						szMsg= "OSCOMM[OSCOMM] Error :: [" + intRtnVal + "]" + "PARAMETER ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return -1;
					}	
				}
				recEdit = JDTORecordFactory.getInstance().create();
				rsOut.first();
				recEdit.setRecord(rsOut.getRecord());
				
				szYD_PILING_CD	  	= ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD");				//Piling Code
				if(szYD_PILING_CD.length() > 4){
					szYD_STRCHAR_GRP_CD = szYD_PILING_CD.substring(0, 4);
				}

				iORD_EA 			= ydDaoUtils.paraRecChkNullInt(recEdit,"ORD_EA");					//주문매수
				dblORD_CONV_T	  	= ydDaoUtils.paraRecChkNullDouble(recEdit,"ORD_CONV_T");			//주문두께
				
				dblheight = dblORD_CONV_T * iORD_EA;
				
				if(dblheight < 2000){
					recTemp.setField("STRCHAR_CUST_CD_SINGLE"		, "Y");
				} else {
					recTemp.setField("STRCHAR_CUST_CD_SINGLE"		, "N");
				}
				
				ydUtils.putLog(szMethodName, szMethodName, "iORD_EA" + iORD_EA, YdConstant.DEBUG);
				ydUtils.putLog(szMethodName, szMethodName, "dblORD_CONV_T" + dblORD_CONV_T, YdConstant.DEBUG);
				ydUtils.putLog(szMethodName, szMethodName, "dblheight" + dblheight, YdConstant.DEBUG);					
				
		       	//저장속성 READ 대형고객사 CHECK
	        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
	        	recInTemp 	= JDTORecordFactory.getInstance().create();
        		recInTemp.setField("YD_STRCHAR_GRP_CD" 	, szYD_STRCHAR_GRP_CD);

        		/*com.inisteel.cim.yd.dao.ydstrchardao.YdStrcharDao.getYdStrcharRow1*/
		    	intRtnVal = ydStrCharDao.getYdStrchar(recInTemp, rsResult, 300);
				if(intRtnVal <= 0) {
					szYD_MTL_W_GP	= "";		
				} else {
					rsResult.absolute(1);
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setRecord(rsResult.getRecord());
					szSTRCHAR_GRP_CUST_CD	= ydDaoUtils.paraRecChkNull(recOutTemp, "CUST_CD");
				}	
				
				recTemp.setField("STRCHAR_ORD_YEOJAE_GP", szORD_YEOJAE_GP);
				recTemp.setField("STRCHAR_CUST_CD"		, szSTRCHAR_GRP_CUST_CD);				
				recTemp.setField("YD_STRCHAR_GRP_CD" 	, szYD_STRCHAR_GRP_CD);
				
				PlateGdsYdUtil.getWTLGp(recTemp);
				
				szYD_MTL_L_GP			= ydDaoUtils.paraRecChkNull(recTemp,	"YD_MTL_L_GP");	
				szYD_MTL_W_GP			= ydDaoUtils.paraRecChkNull(recTemp,	"YD_MTL_W_GP");
				
				szMsg = "[ 결정된 길이구분["+szYD_MTL_L_GP+"], 폭구분["+szYD_MTL_W_GP+"]s";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				szCUST_CD 		  		= ydDaoUtils.paraRecChkNull(recEdit,"CUST_CD");						//고객사
				
				//-------------------------------------------------------------------------------------
				// 저장품 조회
				rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
				recEdit.setField("STL_NO",  ydDaoUtils.paraRecChkNull(recIn,"PLATE_NO"));
				intRtnVal = ydStockDao.getYdStock(recEdit, rsGetStock, 0);
				
				if(intRtnVal < 0){
					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return -1;
				}else if(intRtnVal == 0){
					//stock이 존재하지 않은 경우
					szYD_BOOK_OUT_LOC 		= "";
					szYD_RCPT_PLN_STR_LOC 	= "";
					
				}else{
					//stock이 존재하는 경우
					rsGetStock.absolute(1);
					outRec = JDTORecordFactory.getInstance().create();
					outRec.setRecord(rsGetStock.getRecord());	
					
					szYD_BOOK_OUT_LOC		= ydDaoUtils.paraRecChkNull(outRec,"YD_BOOK_OUT_LOC");
					szYD_RCPT_PLN_STR_LOC	= ydDaoUtils.paraRecChkNull(outRec,"YD_RCPT_PLN_STR_LOC");
				}
				
				//------------------------------------------------------------------------------------
				// 북아웃위치와 입고예정위치가 ""(잉여판이 나오는경우) 이거나
				// 해당공장 후판 값이 아니면 여기서 해당공장후판 북아웃위치와 입고예정위치를 구한다.
				String sRt1 = "";
				String sRt2 = "";
				String sRt3 = "";
				
				if("PA".equals(szPTOP_PLNT_GP)){
					sRt1 = "56";
					sRt2 = "58";
					sRt3 = "59";
				}else{
					sRt1 = "66";
					sRt2 = "67";
					sRt3 = "68";
				}
				
				if("".equals(szYD_BOOK_OUT_LOC)||"".equals(szYD_RCPT_PLN_STR_LOC)||
				  (!szYD_BOOK_OUT_LOC.startsWith(sRt1)&&!szYD_BOOK_OUT_LOC.startsWith(sRt2)&&!szYD_BOOK_OUT_LOC.startsWith(sRt3))) {
					
					JDTORecordSet outRecSet9  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
					JDTORecordSet outRecSet1  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
					JDTORecord 	  outRec9 	  = JDTORecordFactory.getInstance().create();
					String        szPLAN_DONG = null;
					
					recPara = JDTORecordFactory.getInstance().create();
					
					recPara.setField("ORD_NO",    	ydDaoUtils.paraRecChkNull(recEdit,"ORD_NO"));			
					recPara.setField("ORD_DTL",    	ydDaoUtils.paraRecChkNull(recEdit,"ORD_DTL"));				
					//저장계획 코드 Read
					/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommLocPlanCd_PIDEV*/
					intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet1, 300);
					
					if (intRtnVal <= 0) {
						szMsg = "해당주문 :"+ ydDaoUtils.paraRecChkNull(recEdit,"ORD_NO") + "-" +ydDaoUtils.paraRecChkNull(recEdit,"ORD_DTL") + " 저장계획 코드 Read error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}				
					
					outRecSet1.absolute(1);
					outRec1 = JDTORecordFactory.getInstance().create();
					outRec1 = outRecSet1.getRecord();

					String szLOC_PLAN_CD		= ydDaoUtils.paraRecChkNull(outRec1,"LOC_PLAN_CD"); 
					String szMAIN_TRANS_AREA 	= ydDaoUtils.paraRecChkNull(outRec1,"MAIN_TRANS_AREA");  
					
					String szYD_PILING_CD2 		= ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD");
				
					recPara.setField("YD_PILING_CD",   	szYD_PILING_CD2);			
					recPara.setField("LOC_PLAN_CD",    	szLOC_PLAN_CD);
					recPara.setField("YD_GP", 			YdConstant.YD_GP_PLATE2_GDS_YARD); 	
					recPara.setField("MAIN_TRANS_AREA", szMAIN_TRANS_AREA);
					recPara.setField("PTOP_PLNT_GP",    szPTOP_PLNT_GP); 
					
					/*com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0059*/
					intRtnVal = commDao.select(recPara, outRecSet9, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0059");
					if (intRtnVal <= 0) {
						szMsg = "해당 Piling코드  :"+ ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD") +") Access저장동  Read error!!!:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					} else {
						outRecSet9.absolute(1);
						outRec9 = JDTORecordFactory.getInstance().create();
						outRec9 = outRecSet9.getRecord();			
						
						szPLAN_DONG = ydDaoUtils.paraRecChkNull(outRec9,"DONG");
						
						if(!"".equals(szPLAN_DONG)) {
							
							recTemp		= JDTORecordFactory.getInstance().create();
							
							String sRTN_LOC			= null;
							String sRTN_BOOKOUT_LOC = null;						
							
							//-------------------------------------------------------
							//동이 정해졌으면 그 동에서 적치가능한 LOC 를 구한다.
							recTemp.setField("YD_GP", 			YdConstant.YD_GP_PLATE2_GDS_YARD);
							recTemp.setField("YD_BAY_GP", 		szPLAN_DONG);
							recTemp.setField("YD_PILING_CD", 	szYD_PILING_CD2);
							recTemp.setField("PTOP_PLNT_GP",    szPTOP_PLNT_GP); 
							
							sRTN_LOC = YdToLocDcsnUtil.getYdBayLocPln3G(recTemp);
							
							/*
							 * 2014.10.15 윤재광 - 이명운대리 요청
							 * G동 중척재이하는 무조건 2베드로 셋팅
							 */
							if("G".equals(szPLAN_DONG) && ("M".equals(szYD_PILING_CD2.substring(6,7))||
									                       "S".equals(szYD_PILING_CD2.substring(6,7))||
									                       "U".equals(szYD_PILING_CD2.substring(6,7)))){
								recTemp.setField("YD_STK_BED_NO",   "02");
							}else{
								recTemp.setField("YD_STK_BED_NO",   sRTN_LOC.substring(6,8));
							}
							
							//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준) 야드구분값 셋팅
							if("PA".equals(szPTOP_PLNT_GP)){
								recTemp.setField("YD_GP", 	"K");
							}else{
								recTemp.setField("YD_GP", 	"T");
							}

							//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준)
					    	if( GetBreRule6.getYDB674(recTemp) ) {
					    		sRTN_BOOKOUT_LOC = StringHelper.evl(recTemp.getFieldString("YDB674_RV01_YD_BOOK_OUT_LOC"), "00000"); // 업무기준 YDB674 반환값#1 YD_BOOK_OUT_LOC
					    	} else {
					    		sRTN_BOOKOUT_LOC ="00000";
					    	}		
					    	
					    	szYD_BOOK_OUT_LOC		= sRTN_BOOKOUT_LOC;
					    	szYD_RCPT_PLN_STR_LOC   = sRTN_LOC;
						}
					}					
				}
				
				if(!szYD_RCPT_PLN_STR_LOC.equals("")){
					szYD_AIM_BAY_GP     = szYD_RCPT_PLN_STR_LOC.substring(1, 2);	
				}
				szARR_WLOC_CD           = YdConstant.WLOC_CD_PLATE2_GDS_YARD;
				//-------------------------------------------------------------------------------------------------------------
				
			}else{
				
				//-------------------------------------------------------------------------------------------------------------
				//	여재인 경우
				recTemp.setField("STRCHAR_ORD_YEOJAE_GP", "2");
				recTemp.setField("STRCHAR_CUST_CD"		, "");
				recTemp.setField("YD_STRCHAR_GRP_CD" 	, "M001");
				
				PlateGdsYdUtil.getWTLGp(recTemp);
				
				szYD_MTL_L_GP		= ydDaoUtils.paraRecChkNull(recTemp,	"YD_MTL_L_GP");						//길이구분
				szYD_MTL_W_GP		= ydDaoUtils.paraRecChkNull(recTemp,	"YD_MTL_W_GP");						//폭 구분
				
				szYD_PILING_CD	  	= "M001" + szYD_MTL_W_GP + szYD_MTL_L_GP;
				//-------------------------------------------------------------------------------------------------------------
				
				//-------------------------------------------------------------------------------------------------------------
				//	Piling Code로 동별저장계획에 주문외 정보 조회 - Book-Out위치, 입고예정위치
				//-------------------------------------------------------------------------------------------------------------
				JDTORecordSet outRecSet9 = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				JDTORecord 	  outRec9 	 = null;
				String        szPLAN_DONG = null;
				
				recPara = JDTORecordFactory.getInstance().create();
				String szLOC_PLAN_CD		= "M001"; 	
				String szMAIN_TRANS_AREA 	= "M";
				
				recPara.setField("YD_PILING_CD",   	szYD_PILING_CD);			
				recPara.setField("LOC_PLAN_CD",    	szLOC_PLAN_CD);
				recPara.setField("YD_GP", 			YdConstant.YD_GP_PLATE2_GDS_YARD);
				recPara.setField("MAIN_TRANS_AREA", szMAIN_TRANS_AREA);
				recPara.setField("PTOP_PLNT_GP",    szPTOP_PLNT_GP); 
				
				/*com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0059*/
				intRtnVal = commDao.select(recPara, outRecSet9, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0059");
				
				if (intRtnVal <= 0) {
					szMsg = "해당 Piling코드  :"+ ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD") +") Access저장동  Read error!!!:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else {
					outRecSet9.first();
					outRec9 = JDTORecordFactory.getInstance().create();
					outRec9 = outRecSet9.getRecord();		
					
					szPLAN_DONG = ydDaoUtils.paraRecChkNull(outRec9,"DONG");
					
					if(!"".equals(szPLAN_DONG)) {
						
						recTemp		= JDTORecordFactory.getInstance().create();
						
						String sRTN_LOC			= null;
						String sRTN_BOOKOUT_LOC = null;						
						
						//-------------------------------------------------------
						//동이 정해졌으면 그 동에서 적치가능한 LOC 를 구한다.
						recTemp.setField("YD_GP", 			YdConstant.YD_GP_PLATE2_GDS_YARD);
						recTemp.setField("YD_BAY_GP", 		szPLAN_DONG);
						recTemp.setField("YD_PILING_CD", 	szYD_PILING_CD);
						recTemp.setField("PTOP_PLNT_GP",    szPTOP_PLNT_GP); 
						
						sRTN_LOC = YdToLocDcsnUtil.getYdBayLocPln3G(recTemp);
						
						/*
						 * 2014.10.15 윤재광 - 이명운대리 요청
						 * G동 중척재이하는 무조건 2베드로 셋팅
						 */
						if("G".equals(szPLAN_DONG) && ("M".equals(szYD_PILING_CD.substring(6,7))||
								                       "S".equals(szYD_PILING_CD.substring(6,7))||
								                       "U".equals(szYD_PILING_CD.substring(6,7)))){
							recTemp.setField("YD_STK_BED_NO",   "02");
						}else{
							recTemp.setField("YD_STK_BED_NO",   sRTN_LOC.substring(6,8));
						}
						
						//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준) 야드구분값 셋팅
						if("PA".equals(szPTOP_PLNT_GP)){
							recTemp.setField("YD_GP", 	"K");
						}else{
							recTemp.setField("YD_GP", 	"T");
						}
	
						//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준)
				    	if( GetBreRule6.getYDB674(recTemp) ) {
				    		sRTN_BOOKOUT_LOC = StringHelper.evl(recTemp.getFieldString("YDB674_RV01_YD_BOOK_OUT_LOC"), "00000"); // 업무기준 YDB674 반환값#1 YD_BOOK_OUT_LOC
				    	} else {
				    		sRTN_BOOKOUT_LOC ="00000";
				    	}		
				    	
				    	szYD_BOOK_OUT_LOC		= sRTN_BOOKOUT_LOC;
				    	szYD_RCPT_PLN_STR_LOC   = sRTN_LOC;
					}
				}				

				if(!szYD_RCPT_PLN_STR_LOC.equals("")){
					szYD_AIM_BAY_GP         = szYD_RCPT_PLN_STR_LOC.substring(1, 2);
				}
				szARR_WLOC_CD           = YdConstant.WLOC_CD_PLATE2_GDS_YARD;
			}
			//-------------------------------------------------------------------------------------------------------------
			
			//=============================================================
			// STL_APPEAR_GP 값을 가져오는 부분이 없었음
			// 이상황이면 계속 공백값이 저장품에 들어가기 때문에 추출하여 처리하는 코드 삽입
			//=============================================================
			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(recIn, "STL_APPEAR_GP");
			recSet.setField("STL_APPEAR_GP" 	, szSTL_APPEAR_GP); 		
			recSet.setField("STL_NO"        	, ydDaoUtils.paraRecChkNull(recIn,"PLATE_NO")); 
			
			//=====================================================================================
			// PLATECOMM에서 조업공장구분 항목을 읽어와서 편집 (추가) 
			//=====================================================================================
			recSet.setField("PTOP_PLNT_GP"      , ydDaoUtils.paraRecChkNull(recIn,"PTOP_PLNT_GP"));
			recSet.setField("STL_PROG_CD"   	, ydDaoUtils.paraRecChkNull(recIn,"CURR_PROG_CD"));
			recSet.setField("ORD_NO"        	, ydDaoUtils.paraRecChkNull(recIn,"ORD_NO"));
			recSet.setField("ORD_DTL"       	, ydDaoUtils.paraRecChkNull(recIn,"ORD_DTL"));
			recSet.setField("YD_MTL_T"      	, ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_T"));		
			recSet.setField("YD_MTL_W"      	, ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_W"));
			recSet.setField("YD_MTL_L"      	, ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_L")); 			
			recSet.setField("YD_MTL_WT"     	, ydDaoUtils.paraRecChkNull(recIn,"PL_MEA_GDS_WT")); 
			//====================================================================================
			// 조회해오는 레코드에 항목이 없음
			// recSet.setField("DEST_CD"     		, ydDaoUtils.paraRecChkNull(recIn,"DEST_CD")); 
			//====================================================================================
			recSet.setField("ITEMNAME_CD"     	, ydDaoUtils.paraRecChkNull(recIn,"ITEMNAME_CD")); 
			recSet.setField("DEMANDER_CD"     	, ydDaoUtils.paraRecChkNull(recIn,"DEMANDER_CD")); 
			recSet.setField("ORD_YEOJAE_GP" 	, szORD_YEOJAE_GP);
			recSet.setField("CUST_CD"   		, szCUST_CD);		
			/*
			 * 20110128 YJK 임시셋팅
			 * 야드맵은 'U' 길이그룹코는 사용하지 않는다.
			 */
			if(!szYD_RCPT_PLN_STR_LOC.equals("")){
				if (!szYD_RCPT_PLN_STR_LOC.substring(1,2).equals("E")) {
					if("U0".equals(szYD_MTL_L_GP)){
						szYD_MTL_L_GP = "S0";
					}else if("U1".equals(szYD_MTL_L_GP)){
						szYD_MTL_L_GP = "S1";
					}else if("U2".equals(szYD_MTL_L_GP)){
						szYD_MTL_L_GP = "S2";
					}
				}	
			}
			recSet.setField("YD_MTL_L_GP"   	, szYD_MTL_L_GP);		//야드재료길이구분
			recSet.setField("YD_MTL_W_GP"   	, szYD_MTL_W_GP);		//야드재료폭구분
			recSet.setField("YD_PILING_CD"   	, szYD_PILING_CD);		//Piling코드
			recSet.setField("YD_BOOK_OUT_LOC"   , szYD_BOOK_OUT_LOC);	//야드Book_out위치
			recSet.setField("YD_AIM_YD_GP"		, YdConstant.YD_GP_PLATE2_GDS_YARD);//야드구분	
			recSet.setField("YD_AIM_RT_GP"		, "G3");				//야드목표행선구분	
			recSet.setField("YD_MTL_ITEM"		, "PG");
			recSet.setField("YD_MTL_STAT"		, "2");
			recSet.setField("YD_AIM_BAY_GP"   	, szYD_AIM_BAY_GP);		//목표동
			recSet.setField("ARR_WLOC_CD"     	, szARR_WLOC_CD);		//착지개소코드
			
			recSet.setField("APPEAR_GRADE"      , ydDaoUtils.paraRecChkNull(recIn,"APPEAR_GRADE"));
			recSet.setField("PL_RCPT_LN_GP"     , ydDaoUtils.paraRecChkNull(recIn,"PL_RCPT_LN_GP"));
			recSet.setField("ORD_GP"            , ydDaoUtils.paraRecChkNull(recIn, "ORD_GP"));
			recSet.setField("DEST_CD"           , ydDaoUtils.paraRecChkNull(recIn, "DEST_CD"));
			
			//-------------------------------------------------------------------------------------------------------------
			//	야드입고예정저장위치를 저장품에 업데이트
			//-------------------------------------------------------------------------------------------------------------
			recSet.setField("YD_RCPT_PLN_STR_LOC", szYD_RCPT_PLN_STR_LOC);	//야드입고예정저장위치
			
		} catch(Exception e){
			
			szMsg="PLATECOMM[PLATE공통]항목 편집 Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg); 
		}
		return 0;

	} //end of edtPlateComm3G()
		
	/**
	 * 후판제품반품(DMYDR034)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlGdsRetngds(JDTORecord inRecord)throws JDTOException  {
		String szMethodName = "procPlGdsRetngds";
		String szMsg = "";
		String szOperationName = "후판제품반품";
		String szSTL_NO = "";
		int intRtnVal = 0;
		
		String[] rVal = new String[1];
		
		JDTORecord recStockColumn 		= JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao 		= new YdStockDao();
		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[출하] 후판제품반품 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//수신한 재료번호
			szSTL_NO  = ydDaoUtils.paraRecChkNull(inRecord,"STL_NO");

			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP		재료외형구분
			STL_NO				재료번호
			CURR_PROG_CD		현재진도코드
			ORD_YEOJAE_GP		주문여재구분
			ORD_NO				주문번호
			ORD_DTL				주문행번
			ORD_GP				수주구분
			CUST_CD				고객코드
			DEST_CD				목적지코드
			DEST_TEL_NO			목적지전화번호
			DIST_SHIPASSIGN_GP	출하배선지시구분
			*/
			
			recStockColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
			recStockColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STL_PROG_CD",			ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			recStockColumn.setField("ORD_YEOJAE_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));
			recStockColumn.setField("ORD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO"));
			recStockColumn.setField("ORD_DTL", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL"));
			recStockColumn.setField("ORD_GP", 				ydDaoUtils.paraRecChkNull(inRecord,"ORD_GP"));
			recStockColumn.setField("CUST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD"));
			recStockColumn.setField("DEST_CD", 				ydDaoUtils.paraRecChkNull(inRecord,"DEST_CD"));
			recStockColumn.setField("DEST_TEL_NO", 			ydDaoUtils.paraRecChkNull(inRecord,"DEST_TEL_NO"));
			recStockColumn.setField("DIST_SHIPASSIGN_GP", 	ydDaoUtils.paraRecChkNull(inRecord,"DIST_SHIPASSIGN_GP"));
			recStockColumn.setField("TRANS_ORD_DATE", 		"");
			recStockColumn.setField("TRANS_ORD_SEQNO", 		"");
			recStockColumn.setField("CAR_NO", 				"");
			recStockColumn.setField("CARD_NO", 				"");
			recStockColumn.setField("CAR_LOTID", 			"");
			recStockColumn.setField("DEL_YN", 			    "N");
			recStockColumn.setField("MODIFIER", 			"DMYDR034");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("P",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			//****************************************************************************************************

						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "YD_STOCK[후판제품반품] UPDATE Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[후판제품반품] UPDATE Success",3);
			//****************************************************************************************************
			//======================================================
			// 저장품제원 : 후판제품 L2 로 송신(YDY4L002,YDY8L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"))) { //- 2013.01.17 수정 (3기)
				recResult.setField("MSG_ID"         , "YDY8L002"); //2후판 제품창고
			} else {
				recResult.setField("MSG_ID"         , "YDY4L002"); //1후판 제품창고
			}			
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(recStockColumn, "STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			
			// 2021. 05. 17 전문생성 시점에 PT_PLATE_COMM에 진도코드가 바뀌지 않은 문제가 있어
			// 진도코드를 파라메터로 넘겨 처리함
			recResult.setField("CURR_PROG_CD"  , ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD"));
			
			ydDelegate.sendMsg(recResult);		
			
			// 전사물류개선 2021. 4. 3
			if(PlateGdsYdUtil.isSendToEaiY9_stlNo( ydDaoUtils.paraRecChkNull(inRecord, "STL_NO")) ){
				recResult.setField("MSG_ID"         , "YDY9L002"); //2후판 제품창고
				ydDelegate.sendMsg(recResult);
			}
		
		}catch(Exception e){
			szMsg="[후판제품반품]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg); 

		} // end of try-catch

	} // end of procPlGdsRetngds()
	
	/**
	 * 후판제품목적지변경(DMYDR044)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlGdsDestChgInfo(JDTORecord inRecord)throws JDTOException  {
		
		String szMethodName = "procPlGdsDestChgInfo";
		String szMsg = "";

		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao	= new YdStockDao();
		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=ydUtils.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){
			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{

			recPara.setField("STL_NO", 	       ydDaoUtils.paraRecChkNull(inRecord, "STL_NO")); 		
			recPara.setField("URGENT_DIST_YN", ydDaoUtils.paraRecChkNull(inRecord, "URGENT_DIST_YN"));
			/*
			 * 1. 야드 저장품 긴급재/보류재 변경.
			 */			
			ydStockDao.update_Dm_DestCd(recPara, 1);
			
		}catch(Exception e){
			szMsg="[후판제품목적지변경]Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg); 

		} // end of try-catch

	} // end of procPlGdsDestChgInfo()
	
	/**
	 * 슬라브공통 항목을 야드 저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */

	public int edtSlabCommYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {
		String szMethodName	= "edtSlabCommYdstock";
		String szMsg		= "";

		try{
			recEditRec.setField("STL_NO"		  , ydDaoUtils.paraRecChkNull(inRecord, "SLAB_NO")); 					
			recEditRec.setField("YD_MTL_T"		  , ydDaoUtils.paraRecChkNull(inRecord, "SLAB_T")); 		
			recEditRec.setField("YD_MTL_W"		  , ydDaoUtils.paraRecChkNull(inRecord, "SLAB_W")); 		    
			recEditRec.setField("YD_MTL_L"		  , ydDaoUtils.paraRecChkNull(inRecord, "SLAB_LEN"));		
			recEditRec.setField("YD_MTL_WT"		  , ydDaoUtils.paraRecChkNull(inRecord, "SLAB_WT"));		
			recEditRec.setField("STL_APPEAR_GP"	  , ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP"));				
			recEditRec.setField("STL_PROG_CD"	  , ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD"));				
			recEditRec.setField("ORD_YEOJAE_GP"	  , ydDaoUtils.paraRecChkNull(inRecord, "ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO"		  , ydDaoUtils.paraRecChkNull(inRecord, "ORD_NO")); 			
			recEditRec.setField("ORD_DTL"		  , ydDaoUtils.paraRecChkNull(inRecord, "ORD_DTL")); 				
			recEditRec.setField("SLAB_WO_RT_CD"   , ydDaoUtils.paraRecChkNull(inRecord, "SLAB_WO_RT_CD"));
			recEditRec.setField("HCR_GP"          , ydDaoUtils.paraRecChkNull(inRecord, "HCR_GP"));
			recEditRec.setField("SCARFING_YN"     , ydDaoUtils.paraRecChkNull(inRecord, "SCARFING_YN"));
			recEditRec.setField("PTOP_PLNT_GP"    , ydDaoUtils.paraRecChkNull(inRecord, "PTOP_PLNT_GP"));
			recEditRec.setField("SCARFING_DONE_YN", ydDaoUtils.paraRecChkNull(inRecord, "SCARFING_DONE_YN"));
			recEditRec.setField("YD_STK_LOT_CD"   , ydDaoUtils.paraRecChkNull(inRecord, "STACK_LOT_NO"));
			
		} catch(Exception e){
			szMsg = "[항목편집]Exception Error:" + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg); 
		}
		return 1;
	} //end of edtSlabCommYdstock()
	
	/**
	 * Plate공통 항목을 야드 저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtPlateYdstock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		String szYD_MTL_W_GP= "";
		String szYD_MTL_L_GP= "";
		int    intMtlL		= 0;
		int    intMtlW		= 0;   
		
		try{
			
			intMtlL =Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"PL_MEA_GDS_L"));
			intMtlW =Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"PL_MEA_GDS_W"));
//DONG_INSERT			
//			if(intMtlL <= 6700){
//				szYD_MTL_L_GP = "U";
//			}else 
			if(intMtlL <= 9200){
				szYD_MTL_L_GP = "S";
			}else if(intMtlL <= 14000){
				szYD_MTL_L_GP = "M";
			}else if(intMtlL <= 18000){
				szYD_MTL_L_GP = "L";
			}else if(intMtlL <= 25000){
				szYD_MTL_L_GP = "X";
			}else{
				szYD_MTL_L_GP = "";
			}
			
			if(intMtlW <= 2100){
				szYD_MTL_W_GP = "S";
			}else if(intMtlL <= 3450){
				szYD_MTL_W_GP = "M";
			}else if(intMtlL <= 4800){
				szYD_MTL_W_GP = "L";
			}else{
				szYD_MTL_W_GP = "";
			}
			recEditRec.setField("STL_NO"			 , ydDaoUtils.paraRecChkNull(inRecord, "PLATE_NO"));
			
			recEditRec.setField("YD_STK_LOT_TP"		 , ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LOT_TP"));
			recEditRec.setField("YD_STK_LOT_CD"		 , ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LOT_CD"));
			recEditRec.setField("YD_AIM_YD_GP"		 , ydDaoUtils.paraRecChkNull(inRecord, "YD_GP"));
			recEditRec.setField("YD_AIM_BAY_GP"		 , ydDaoUtils.paraRecChkNull(inRecord, "YD_BAY_GP"));
			recEditRec.setField("YD_MTL_T"			 , ydDaoUtils.paraRecChkNull(inRecord, "PL_MEA_GDS_T"));
			recEditRec.setField("YD_MTL_WT"			 , ydDaoUtils.paraRecChkNull(inRecord, "PL_MEA_GDS_WT"));
			recEditRec.setField("STL_APPEAR_GP"		 , ydDaoUtils.paraRecChkNull(inRecord, "STL_APPEAR_GP"));
			recEditRec.setField("PLNT_PROC_CD"		 , ydDaoUtils.paraRecChkNull(inRecord, "PLNT_PROC_CD"));
			recEditRec.setField("STL_PROG_CD"		 , ydDaoUtils.paraRecChkNull(inRecord, "CURR_PROG_CD"));
			recEditRec.setField("ORD_YEOJAE_GP"		 , ydDaoUtils.paraRecChkNull(inRecord, "ORD_YEOJAE_GP"));
			recEditRec.setField("ORD_NO"			 , ydDaoUtils.paraRecChkNull(inRecord, "ORD_NO"));
			recEditRec.setField("ORD_DTL"			 , ydDaoUtils.paraRecChkNull(inRecord, "ORD_DTL"));
			recEditRec.setField("ITEMNAME_CD"		 , ydDaoUtils.paraRecChkNull(inRecord, "ITEMNAME_CD"));
			recEditRec.setField("OVERALL_STAMP_GRADE", ydDaoUtils.paraRecChkNull(inRecord, "OVERALL_STAMP_GRADE"));
			recEditRec.setField("YD_MTL_W_GP"   	 , szYD_MTL_W_GP);	//야드재료폭구분
			recEditRec.setField("YD_MTL_L_GP"   	 , szYD_MTL_L_GP);	//야드재료길이구분
		} catch(Exception e){

			throw new JDTOException(e.toString()); 
		}
		return 1;

	} //end of edtPlateYdstock()
	
	/**
	 * 오퍼레이션명 : 동별저장계획
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procYdBayLocPln(JDTORecord msgRecord)throws JDTOException  {
	
        //저장품DAO
		PtOsCommDao ptOsCommDao = new PtOsCommDao();	
		YDDataUtil yddatautil 	= new YDDataUtil();
		YdStockDao ydStockDao 	= new YdStockDao();
		
		JDTORecord outRec		= JDTORecordFactory.getInstance().create();
		JDTORecord outRec1		= JDTORecordFactory.getInstance().create();
		JDTORecord outRec2		= JDTORecordFactory.getInstance().create();
		JDTORecord outRec3		= JDTORecordFactory.getInstance().create();
		JDTORecord outRec9		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord	= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsOut 	= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet outRecSet1= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet outRecSet2= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet outRecSet3= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet outRecSet9= JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		
		String szCT_MILL_SPEC_WRK_STAT_GP 	= "";
		String szPRPL_MILL_WO_DT= "";
		
		String szMsg		  	= "";
		String szMethodName	  	= "procYdBayLocPln";
		String szOperationName  = "동별저장계획";
		String szYD_PILING_CD 	= "";
		String szPTOP_PLNT_GP 	= "";
		String szORD_LOC_CNT    = "";
		String szLOC_PLAN_CD 	= "";
		String szPLAN_DONG_TEMP = "";
		String szLenGp 			= "";
		String szPLAN_DONG      = "";
		String szORD_NO			= "";
		String szORD_DTL		= "";
		String szPRIOR_1_ACC_DONG 	= "";
		String szPILING_YD_BAY_GP	= "";
		String szPILING_BAY_CNT = "";
		String sRTN_CD          = "";
		int intRtnVal 			= 0;
		double dblDONG_CAPA     = 0;

		try{

			// 수신항목[PTOP_PLNT_GP: 조업공장구분]
			szPTOP_PLNT_GP 				= ydDaoUtils.paraRecChkNull(msgRecord,"PTOP_PLNT_GP");
			// 수신항목[CT_MILL_SPEC_WRK_STAT_GP: 생산통제사양작업상태구분]
			szCT_MILL_SPEC_WRK_STAT_GP 	= ydDaoUtils.paraRecChkNull(msgRecord,"CT_MILL_SPEC_WRK_STAT_GP");
			// 수신항목[PRPL_MILL_WO_DT: 공정계획압연지시일시]
			szPRPL_MILL_WO_DT 			= ydDaoUtils.paraRecChkNull(msgRecord,"PRPL_MILL_WO_DT");

			szMsg = "szPTOP_PLNT_GP:" + szPTOP_PLNT_GP + "/szCT_MILL_SPEC_WRK_STAT_GP:" + szCT_MILL_SPEC_WRK_STAT_GP + "/szPRPL_MILL_WO_DT:" + szPRPL_MILL_WO_DT;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
			
			
			// 후판Plate사양 을 주문정보 read
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCtPlatspecOrdno*/
			intRtnVal = ydStockDao.getYdStock(msgRecord, rsOut, 607);
			if (intRtnVal <= 0){
				szMsg = "동별 저장계획 할 대상 없음";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}

			
			for(int i =1; i <= rsOut.size(); i++){
				rsOut.absolute(i);
				outRec = JDTORecordFactory.getInstance().create();
				outRec = rsOut.getRecord();
				szORD_NO	= yddatautil.setDataDefault(outRec.getField("ORD_NO"),"");
				szORD_DTL	= yddatautil.setDataDefault(outRec.getField("ORD_DTL"),"");
	
				szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL + " 처리시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				outRecSet1= JDTORecordFactory.getInstance().createRecordSet("retTmp");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("ORD_NO",    	szORD_NO);			
				recPara.setField("ORD_DTL",    	szORD_DTL);			
				//저장계획 코드 Read
				/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommLocPlanCd_PIDEV*/
				intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet1, 300);
				
				if (intRtnVal <= 0) {
					szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL + " 저장계획 코드 Read error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					continue ;
				}
				
				outRecSet1.absolute(1);
				outRec1 = JDTORecordFactory.getInstance().create();
				outRec1 = outRecSet1.getRecord();

				szYD_PILING_CD	= yddatautil.setDataDefault(outRec1.getField("ARG_YD_PILING_CD"),"");
				szLOC_PLAN_CD	= yddatautil.setDataDefault(outRec1.getField("LOC_PLAN_CD"),"");
				//D010S1S2
				if(szYD_PILING_CD.equals("")){
					szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +" YD_PILING_CD error!!! ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					continue ;
				}
				
				outRecSet9= JDTORecordFactory.getInstance().createRecordSet("retTmp");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_PILING_CD",   	szYD_PILING_CD);			
				recPara.setField("LOC_PLAN_CD",    	szLOC_PLAN_CD);			
				
				//Access저장동 READ
				/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommPriorLocPlanAcc*/
				intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet9, 301);
				if (intRtnVal <= 0) {
					szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") Access저장동  Read error!!!:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					continue ;
				}

				outRecSet9.absolute(1);
				outRec9 = JDTORecordFactory.getInstance().create();
				outRec9 = outRecSet9.getRecord();

				szPRIOR_1_ACC_DONG = yddatautil.setDataDefault(outRec9.getField("DONG"),"");  
				
				szLenGp = szYD_PILING_CD.substring(4,5);
				
//길이구분 U,L,X,내수				
				if((szLenGp.equals("U")) || 
				   (szLenGp.equals("L")) || 
				   (szLenGp.equals("X")) || (szLOC_PLAN_CD.substring(0, 1).equals("D")) ) {
					
					szPLAN_DONG = szPRIOR_1_ACC_DONG;
					
					szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 내수/초단척/장척/초장척 계획동:" + szPLAN_DONG; 
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} else {
					
					outRecSet2= JDTORecordFactory.getInstance().createRecordSet("retTmp");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PILING_CD",   	szYD_PILING_CD);			

					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getStklyrWithOrdLocCnt*/
					intRtnVal = ydStockDao.getYdStock(recPara, outRecSet2, 609);
					if (intRtnVal <= 0){
						szMsg = "주문 저장동 코드가 없음 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

						//저장계획 저장율이 낮은 동
						szPLAN_DONG = this.ToLocLowRate(outRecSet9);

						szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					} else {

						
						outRecSet2.absolute(1);
						outRec2 = JDTORecordFactory.getInstance().create();
						outRec2 = outRecSet2.getRecord();

						szORD_LOC_CNT	= yddatautil.setDataDefault(outRec2.getField("CD_VAL"),"");  	// 주문 저장동 코드 
						
						szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 주문 저장동 코드 :" + szORD_LOC_CNT; 
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
						
// 동별 파일링 코드 위치 존재 여부
						outRecSet3= JDTORecordFactory.getInstance().createRecordSet("retTmp");
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_PILING_CD",   	szYD_PILING_CD);			
						
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getStklyrWithSamePilingCd*/
						intRtnVal = ydStockDao.getYdStock(recPara, outRecSet3, 608);
						if (intRtnVal <= 0){
							szMsg = "동별 파일링 코드가  야드에 없음";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//주문 저장동 READ						
							if (szORD_LOC_CNT.equals("1")) {
//주문저장동 수량 = 1			
								szPLAN_DONG = szPRIOR_1_ACC_DONG;
								
								szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장동 1개  계획동:" + szPLAN_DONG; 
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							} else if (szORD_LOC_CNT.equals("2")) {
//주문저장동 수량 = 2			
								szPLAN_DONG = "";
								//저장율이 80미만 SEARCH
								for(int j =1; j <= outRecSet9.size(); j++){
									outRecSet9.absolute(j);
									outRec9 = JDTORecordFactory.getInstance().create();
									outRec9 = outRecSet9.getRecord();
									szPLAN_DONG_TEMP	= ydDaoUtils.paraRecChkNull(outRec9, "DONG");  
									dblDONG_CAPA		= ydDaoUtils.paraRecChkNullDouble(outRec9, "DONG_CAPA");
									
									if(dblDONG_CAPA < 80) {
										szPLAN_DONG = szPLAN_DONG_TEMP; 
										j = outRecSet9.size() + 1;
									}
								}
								if(szPLAN_DONG.equals("")) {
									//저장계획 저장율이 낮은 동
									szPLAN_DONG = this.ToLocLowRate(outRecSet9);
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								} else {
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장율 80 미만 선택 동:" + szPLAN_DONG; 
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								}
							} else {
								
								// 주문저장동 코드 NOT IN ('1','2')	
								//저장계획 저장율이 낮은 동
								szPLAN_DONG = this.ToLocLowRate(outRecSet9);
			
								szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}	
						} else {

							szMsg = "동별 파일링 코드 야드에 있음";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							outRecSet3.absolute(1);
							outRec3 = JDTORecordFactory.getInstance().create();
							outRec3 = outRecSet3.getRecord();
	
							szPILING_YD_BAY_GP	= yddatautil.setDataDefault(outRec3.getField("YD_BAY_GP"),"");       // 동일  PI저장동 
							szPILING_BAY_CNT	= yddatautil.setDataDefault(outRec3.getField("PILING_BAY_CNT"),"");  // 동일  PI위치 동수
							
							szMsg = "동일  PI 저장동 :" + szPILING_YD_BAY_GP + "/동일 PI위치동수 :" + szPILING_BAY_CNT; 
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							
							if (szORD_LOC_CNT.equals("1")) {
//주문저장동 코드 = 1								
								// 동일PI저장동 = 수송별 ACC동 AND 주문저장코드= 동일PI 위치 동수
//								if( (szPILING_YD_BAY_GP.equals(szPRIOR_1_ACC_DONG)) &&  
								if( (szORD_LOC_CNT.equals(szPILING_BAY_CNT)) ) {
									
									szPLAN_DONG = szPILING_YD_BAY_GP;	
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 기존파일링위치 동:" + szPILING_YD_BAY_GP; 
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								} else {
									szPLAN_DONG = szPRIOR_1_ACC_DONG;
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 계획동:" + szPRIOR_1_ACC_DONG; 
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								}  
							
							} else if (szORD_LOC_CNT.equals("2")) {
//주문저장동 코드 = 2								
								double dblORD_LOC_CNT 	= Integer.parseInt(szORD_LOC_CNT);
								double dblPILING_BAY_CNT= Integer.parseInt(szPILING_BAY_CNT);
								
								// 주문저장코드 <= 동일PI위치 동수
								if( dblORD_LOC_CNT <= dblPILING_BAY_CNT ) {
									
									szPLAN_DONG = szPRIOR_1_ACC_DONG;
									
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 계획동:" + szPRIOR_1_ACC_DONG; 
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
								} else {
//최우선순위 저장동 선택
									szPLAN_DONG = "";
									for(int j =1; j <= outRecSet9.size(); j++){
										outRecSet9.absolute(j);
										outRec9 = JDTORecordFactory.getInstance().create();
										outRec9 = outRecSet9.getRecord();
										
										szPLAN_DONG_TEMP	= ydDaoUtils.paraRecChkNull(outRec9, "DONG");  
										dblDONG_CAPA		= ydDaoUtils.paraRecChkNullDouble(outRec9, "DONG_CAPA");
										
										if(dblDONG_CAPA < 80) {
											szPLAN_DONG = szPLAN_DONG_TEMP; 
											j = outRecSet9.size() + 1;
										}
									}
									if(szPLAN_DONG.equals("")) {
										// 저장계획 저장율이 낮은 동
										szPLAN_DONG = this.ToLocLowRate(outRecSet9);
										szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

									} else {
										szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장율 80 미만 선택 동:" + szPLAN_DONG; 
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									}
								}  
							} else {
								// 주문저장동 코드 NOT IN ('1','2')	
								//저장계획 저장율이 낮은 동
								szPLAN_DONG = this.ToLocLowRate(outRecSet9);
			
								szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}	
						}	//szMsg = "동별 파일링 코드 야드에 있음";
					}		//szMsg = "주문 저장동 코드 있음";
				}			//길이구분 U,L,X,내수	
			
				if(!szPLAN_DONG.equals("")) {
					szORD_NO	= yddatautil.setDataDefault(outRec.getField("ORD_NO"),"");
					szORD_DTL	= yddatautil.setDataDefault(outRec.getField("ORD_DTL"),"");

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("ORD_NO"			, szORD_NO);
					recPara.setField("ORD_DTL"			, szORD_DTL);
					recPara.setField("YD_RCPT_STR_LOC"	, "K" + szPLAN_DONG + "000000");
					
					/* com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommYdRecpStrLoc */
					EJBConnector ejbConn = new EJBConnector("default", "PlateSpecRegSeEJB", this);			
					outRecord 	= (JDTORecord)ejbConn.trx("ProcUpdPtOsComm", new Class[]  { JDTORecord.class }
													  , new Object[] { recPara  });
					
					sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					
					if(sRTN_CD.equals("0")){ 
						szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"update error!!! ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						m_ctx.setRollbackOnly();
						return ;
					}	
				}  
			}
		}catch(Exception e){
	
			szMsg = "[동별저장계획] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

		szMsg = "동별저장계획 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	

	} // end of procYdBayLocPln()
		
	/**
	 * 저장계획 저장율이 낮은 동
	 * return  0: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public String ToLocLowRate (JDTORecordSet inRecordSet) throws JDTOException {
		//저장품DAO
		YDDataUtil yddatautil 	= new YDDataUtil();
		JDTORecord outRec   	= null;
		String szMethodName	   	= "ToLocLowRate";
		String szMsg		   	= "";
		String szPLAN_DONG  	= "";
		
		double dblDONG_MIN_CAPA	= 99999;
		double dblDONG_CAPA 	= 0;
		
		try{
			
			for(int i =1; i <= inRecordSet.size(); i++){
				inRecordSet.absolute(i);
				outRec = JDTORecordFactory.getInstance().create();
				outRec	= inRecordSet.getRecord();
				dblDONG_CAPA	= ydDaoUtils.paraRecChkNullDouble(outRec, "DONG_CAPA");
				
				
				if(dblDONG_MIN_CAPA > dblDONG_CAPA) {
					szPLAN_DONG 		= yddatautil.setDataDefault(outRec.getField("DONG"),""); 
					dblDONG_MIN_CAPA	= dblDONG_CAPA;
				}
			}

			szMsg = "계획동:" + szPLAN_DONG; 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			return szPLAN_DONG;
			
		} catch(Exception e){
			
			szMsg="저장계획 저장율이 낮은 동 Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg); 
		}

	} //end of ToLocLowRate()
	
	/**
	 * OS 입고예정위치 UPDATA
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord ProcUpdPtOsComm(JDTORecord inDto) {
		
		int       intRtnVal    	= 0;
		String    szMsg        	= "";
		String    szMethodName 	= "ProcUpdPtOsComm";
		String szOperationName 	= "OS 입고예정위치 UPDATA ";
		
		JDTORecord outRecord	= JDTORecordFactory.getInstance().create(); 
		PtOsCommDao ptOsCommDao = new PtOsCommDao();	
		
		szMsg = "["+szOperationName+"] 메소드 시작 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		try {
			
			
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.updPtOsCommYdRecpStrLoc*/
			intRtnVal = ptOsCommDao.updPtOsComm(inDto, 7);
	        
			if (intRtnVal != 1) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "OS 입고예정위치 UPDATA 수정시 ERROR 발생");	
				return outRecord;
			} // end of if				

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

	}	// end of ProcUpdPtOsComm    	

	/**
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * [A] 오퍼레이션명 : (JMS :JDTORecord 송신처리)
	 * 
	 */
	public void sndJMSInfo (JDTORecord param) throws DAOException {	
		
		JmsQueueSender sender = null;
		String queueName = null;
		JDTORecord insRecord = null; 			
		PropertyService propertyService=null;	
		
		String szMsg		  	= null;
		JDTORecord tcRecord 	= null;	
		JDTORecordSet tcRecSet  = null;
		
		
		try {			
			
			String szTcCode	    	 = ydUtils.getTcCode(param);		

			//PIDEV
			// 전송용 JDTORecord 생성
			tcRecSet =JDTORecordFactory.getInstance().createRecordSet("YDDelegate");						
			
			// nRtc>0 : tcRecSet의 Record Count
			int nRtc = tcConstMgr.makeTc(param, tcRecSet);
			
			if( nRtc<=0){
				szMsg=" TC("+szTcCode+") Data Make Error";
				ydUtils.putLog(szSessionName, "sndJMSInfo", szMsg, YdConstant.ERROR);
				return ;
			}
			
			// TC코드가 맞지 않을때
			if(szTcCode.startsWith("M10")) {
				for(int i = 0; i < nRtc; i++){
					tcRecord =tcRecSet.getRecord(i);
					M10YdExLm21Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(tcRecord));
					szMsg = "rabbit mq 송신 완료 (TC Code="+szTcCode+")";
					ydUtils.putLog(szSessionName, "sndJMSInfo", szMsg, YdConstant.DEBUG);
				}
				return ;
			}			
			//까지			
			
			// 프로퍼티 서비스 인스턴스를 취득합니다.
			propertyService = PropertyService.getInstance();
			
			ydUtils.displayRecord("송신확인", param);
			
			// JDTORecord인스턴스 객체 취득
			insRecord = JDTORecordFactory.getInstance().create();			
					
			String JMS_TC_CD	    	 = StringHelper.evl(param.getFieldString("JMS_TC_CD"), "");				//JMS전문 ID		8
			String Message = "";
			String szWkGp  = JMS_TC_CD.substring(2,4);
//			출하http ->jms 
			// 큐 명칭을 프로퍼티로부터 취득합니다.
			queueName = propertyService.getProperty("common.properties","jms.queue."+szWkGp+"_MDB_QUEUE");	

			
	
			sender = new JmsQueueSender();			
			sender.initQueueService(queueName);		
	
			sender.send(param);
		
		
		}catch (Exception e) {
			
			szMsg = "[sndJMSInfo] sender.send Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, "sndJMSInfo", szMsg, YdConstant.ERROR);
			
		}finally {
			
			try {
				
				sender.closeAll();
				
			} catch (Exception e) {
				
				szMsg = "[sndJMSInfo] sender.closeAll Exception Error:" +e.getMessage();
				ydUtils.putLog(szSessionName, "sndJMSInfo", szMsg, YdConstant.ERROR);	
				
			}
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 2후판 제품생산실적 (PPYDJ004)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procPl2GdsPrdWr(JDTORecord msgRecord)throws JDTOException  {
		
		// DAO 및 UTIL 객체 생성
		YdStockDao ydStockDao     = new YdStockDao();
		YdPlateCommDAO commDao 	  = new YdPlateCommDAO();
		
		// 레코드 선언
		JDTORecordSet rsOutRecSet = null;
		JDTORecordSet rsGetStock  = null;
		JDTORecord outRec         = null;
		JDTORecord recIn          = null;
		JDTORecord recEdit        = null;
		JDTORecord recPara        = null;
		JDTORecord recInTemp      = null;
		JDTORecord recGetVal      = null;
		
		// 변수 선언
		String szMethodName       = "procPl2GdsPrdWr";
		String szMsg              = "";
		String szOperationName    = "2후판 제품생산실적";
		String szSTL_NO           = "";
		String szPL_RCPT_LN_GP    = "";
		String szPL_WRK_PROC      = "";
		String szPL_RCPT_TRK_NO   = "";
		String szPL_RCPT_DDTT     = "";
		
		String szPilingCd 				= null;
		String szYdRcptPlnStrLoc 		= null;
		String szYdBookOutLoc 			= null;
		
		YdEqpDao   ydEqpDao   = new YdEqpDao();
		JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord1  = JDTORecordFactory.getInstance().create();
		JDTORecord      outRec1		= null;
		
		String szAPPLY_YN130			= "N";
		int intRtnVal             		= 0;
		String szAPPLY_YN250			= "N"; // 후판 무상샘플제 T999999999 출하I/F 적용여부(Y: 전송, N:전송안함)
		
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		if(szRcvTcCode == null){
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
				
		try{
			
			// 수신받은 전문에서 재료번호 추출
			szSTL_NO 		 = msgRecord.getFieldString("STL_NO");
			szPL_RCPT_LN_GP  = msgRecord.getFieldString("PL_RCPT_LN_GP"); // 후판입고Line구분
			szPL_WRK_PROC    = msgRecord.getFieldString("PL_WRK_PROC");   // 후판공정코드
			
			// 레코드 생성
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			rsGetStock  = JDTORecordFactory.getInstance().createRecordSet("");
			recEdit     = JDTORecordFactory.getInstance().create();

			// PLATE공통 조회  Dao 호출 - [GP : 4]
			//==============================================================================================
			// 2009.11.17 권오창  : 쿼리수정 (172)
			//    com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMMOSCOMM
			// 
			//    조회 후 저장품에 업데이트 시 ORD_GP과 DEST_CD가 없음 
			//    PLATECOMM 과 OSCOMM을 조인걸어서 가져옴
			//==============================================================================================
			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("PLATE_NO", szSTL_NO);
			/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMMOSCOMM */
			intRtnVal = ydStockDao.getYdStock(recIn, rsOutRecSet, 172);
			if(intRtnVal < 0){
				szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + szSTL_NO + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "PLATECOMM[PLATE공통] Error :: STL_NO(" + szSTL_NO + ") [" + intRtnVal + "]" + "DO NOT EXIST";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}
			
			rsOutRecSet.first();
			recGetVal = rsOutRecSet.getRecord();
			
			recGetVal.setField("PL_RCPT_LN_GP", szPL_RCPT_LN_GP);
			
			// PLATE공통 테이블에서 읽은 전문 항목편집 (주문재,여재에 파일링코드, 북아웃위치, 입고예정위치 등..)
			intRtnVal = this.edtPlateComm3G(recGetVal, recEdit, "PB");
			if(intRtnVal < 0){
				szMsg= "PLATECOMM[PLATE공통] 항목 편집 Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//-------------------------------------------------------------------------------------------------------
			//1후판 ON-LINE 이 66, 67 일 수 있기 때문에
			//edtPlateComm3G 에서 BOOK-OUT LOC 가  66이더라도 BO 이면 67 변경한다.
			szYdBookOutLoc 			= ydDaoUtils.paraRecChkNull(recEdit,"YD_BOOK_OUT_LOC");
			if(szYdBookOutLoc.startsWith("66")&&("BO".equals(szPL_WRK_PROC)||"BM".equals(szPL_WRK_PROC)||"BL".equals(szPL_WRK_PROC))) {
				szYdBookOutLoc = "67" + szYdBookOutLoc.substring(2);
			} 
			recEdit.setField("YD_BOOK_OUT_LOC", szYdBookOutLoc);
			//-------------------------------------------------------------------------------------------------------			
			
			/*
			 * PLATE공통에 UPDATE할 레코드.
			 * 상위의 메소드에서는 주문재일 경우 OS공통의 BOOK OUT 및 예정위치를 가져온다.
			 * 신규일경우는 OS공통정보, 수정일 경우는 저장품정보를 PLATE공통에 UPDATE한다.
			 */
			recInTemp  = JDTORecordFactory.getInstance().create();
			
			// 저장품 조회를 해서 존재하면 UPDATE 없으면 INSERT 처리
			intRtnVal = ydStockDao.getYdStock(recEdit, rsGetStock, 0);
			if(intRtnVal < 0){
				szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}else if(intRtnVal == 0){
				
				szMsg = "YD_STOCK[저장품] INSERT :: ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				// INSERT
				recEdit.setField("REGISTER", "PPYDJ004");
				
				/*--------------------------------------------------------------
				 * PLATE 공통 UPDATE 레코드
				 */
				recInTemp.setField("PLATE_NO", 			szSTL_NO);
				recInTemp.setField("YD_PILING_CD", 		ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD"));
				recInTemp.setField("YD_BOOK_OUT_LOC", 	ydDaoUtils.paraRecChkNull(recEdit,"YD_BOOK_OUT_LOC"));
				
				/*--------------------------------------------------------------
				 * 2010.03.02 이영근
				 * 후판창고입고일시, 후판생산실적번호(후판공정코드 + 년월일시분초) 항목 추가 
				 */
				szPL_RCPT_DDTT   = YdUtils.getCurDate("yyyyMMddHHmmss");
				szPL_RCPT_TRK_NO = szPL_WRK_PROC + YdUtils.getCurDate("yyyyMMddHHmmss");
				
				recEdit.setField("PL_RCPT_DDTT",   szPL_RCPT_DDTT);    // 후판창고입고일시                                                  char(14) 'YYYYMMDDHHMMSS'
				recEdit.setField("PL_RCPT_TRK_NO", szPL_RCPT_TRK_NO);  // 후판생산실적번호(후판공정코드 + 년월일시분초) char(16) '1MYYYYMMDDHHMMSS'
				//--------------------------------------------------------------
				
				intRtnVal = ydStockDao.insYdStock(recEdit);
				if(intRtnVal < 0){
					szMsg = "YD_STOCK[저장품] INSERT Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				szMsg = "YD_STOCK[A후판제품생산실적수신] INSERT SUCCESS";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				
				szMsg = "YD_STOCK[저장품] UPDATE ::";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				// UPDATE
				recEdit.setField("MODIFIER", "PPYDJ004");
				
				String sOrdYeojaeGp = ydDaoUtils.paraRecChkNull(recGetVal,"ORD_YEOJAE_GP");
				
				/*------------------------------------------------------------------------
				 * 2010.03.05 석창화
				 * Update 항목 편집 수정
				 */
				rsGetStock.absolute(1);
				outRec = JDTORecordFactory.getInstance().create();
				outRec.setRecord(rsGetStock.getRecord());
				
				//Stock의 정보를 읽어 온다.
				szPilingCd 				= ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD");
				szYdBookOutLoc 			= ydDaoUtils.paraRecChkNull(recEdit,"YD_BOOK_OUT_LOC");
				szYdRcptPlnStrLoc 		= ydDaoUtils.paraRecChkNull(recEdit,"YD_RCPT_PLN_STR_LOC");
				
				ydUtils.putLog(szSessionName, szMethodName, "szPilingCd="+szPilingCd, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "szYdBookOutLoc="+szYdBookOutLoc, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "szYdRcptPlnStrLoc="+szYdRcptPlnStrLoc, YdConstant.DEBUG);
				
				recEdit.setField("YD_PILING_CD",   		szPilingCd);   
				recEdit.setField("YD_BOOK_OUT_LOC",   	szYdBookOutLoc);   
				recEdit.setField("YD_RCPT_PLN_STR_LOC", szYdRcptPlnStrLoc);
				//--------------------------------------------------------------
						
				/*--------------------------------------------------------------
				 * PLATE 공통 UPDATE 레코드
				 */
				recInTemp.setField("PLATE_NO", 			szSTL_NO);
				recInTemp.setField("YD_PILING_CD", 		szPilingCd);
				recInTemp.setField("YD_BOOK_OUT_LOC", 	szYdBookOutLoc);
					
				/*--------------------------------------------------------------
				 * 2010.03.02 이영근
				 * 후판창고입고일시, 후판생산실적번호(후판공정코드 + 년월일시분초) 항목 추가 
				 */
				szPL_RCPT_DDTT   = YdUtils.getCurDate("yyyyMMddHHmmss");
				szPL_RCPT_TRK_NO = szPL_WRK_PROC + YdUtils.getCurDate("yyyyMMddHHmmss");
				
				recEdit.setField("PL_RCPT_DDTT",   szPL_RCPT_DDTT);    // 후판창고입고일시                                                  char(14) 'YYYYMMDDHHMMSS'
				recEdit.setField("PL_RCPT_TRK_NO", szPL_RCPT_TRK_NO);  // 후판생산실적번호(후판공정코드 + 년월일시분초) char(16) '1MYYYYMMDDHHMMSS'
				//--------------------------------------------------------------
				
				recEdit.setField("SNDBK_RSN_CD", "*"); //Auto-Piling 을 하기 위한 준비작업으로 SNDBK_RSN_CD 를 '*' 로 셋팅한다.
				
				intRtnVal = ydStockDao.updYdStock(recEdit, 0);
				if(intRtnVal <= 0){
					szMsg = "YD_STOCK[저장품] UPDATE Error :: [" + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				szMsg = "YD_STOCK[A후판제품생산실적수신] UPDATE SUCCESS";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//--------------------------------------------------------------------------------------------------------
			//	후판제품 생산실적 시간 UPDATE
			//--------------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szSTL_NO);
			
			szMsg = "[생산실적 수신 ]["+szSTL_NO+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			intRtnVal = ydStockDao.update_Dm_Time(recPara,1);

			outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
			inRecord1 	= JDTORecordFactory.getInstance().create();
			outRecord1  = JDTORecordFactory.getInstance().create();

			inRecord1.setField("REPR_CD_GP", "T00130");    //시험 시편재
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
			if(intRtnVal > 0) {
				outResult.first();
				outRecord1  	= outResult.getRecord();
				szAPPLY_YN130 	= outRecord1.getFieldString("ITEM1");				
			}
			szMsg="시험 시편재 적용 :" + szAPPLY_YN130 ;
			ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);

			if(szAPPLY_YN130.equals("Y")){

				String sORD_GP 			= ydDaoUtils.paraRecChkNull(recGetVal,"ORD_GP");
				String sORD_TP 			= ydDaoUtils.paraRecChkNull(recGetVal,"ORD_TP");
				String sORD_PATTERN_CD	= ydDaoUtils.paraRecChkNull(recGetVal,"ORD_PATTERN_CD");
				String sOrdYeojaeGp 	= ydDaoUtils.paraRecChkNull(recGetVal,"ORD_YEOJAE_GP");
				
				if("1".equals(sOrdYeojaeGp)&&
				   sORD_TP.equals("TC") && 
				   sORD_GP.equals("T") && 
				   sORD_PATTERN_CD.equals("C") && 
				   "2N".equals(szPL_WRK_PROC)) {
					
					//--------------------------------------------------------------------------------------------------------
					//	후판제품공통테이블에 Piling Code와 Book-Out위치 수정
					//--------------------------------------------------------------------------------------------------------
					recInTemp.setField("YD_GP", 			YdConstant.YD_GP_PLATE2_GDS_YARD);
					recInTemp.setField("MODIFIER", 			"PPYDJ004");
					
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0008");
					
					szMsg= "후판제품공통테이블에 YD_PILING_CD, YD_BOOK_OUT_LOC 등록 완료 " ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
					//--------------------------------------------------------------------------------------------------------
					
					/* 후판 무상샘플제 T999999999 출하I/F 적용여부(Y: 전송, N:전송안함) 호출시작 */ 
					outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
					inRecord1 	= JDTORecordFactory.getInstance().create();
					outRecord1  = JDTORecordFactory.getInstance().create();

					inRecord1.setField("REPR_CD_GP", "T00250");    //시험 시편재
					
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
					intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
					if(intRtnVal > 0) {
						outResult.first();
						outRecord1  	= outResult.getRecord();
						szAPPLY_YN250 	= outRecord1.getFieldString("ITEM1");				
					}
					szMsg="후판 무상샘플제 T999999999 출하I/F 적용 :" + szAPPLY_YN250 ;
					ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);
					/* 후판 무상샘플제 T999999999 출하I/F 적용여부(Y: 전송, N:전송안함) 호출끝 */ 					
					
					if("N".equals(szAPPLY_YN250)){ // 후판 무상샘플제 T999999999 출하I/F 적용여부(Y: 전송, N:전송안함)
						szMsg ="[JSP Session "+ szOperationName +"] - 테스트용 시험재"+szSTL_NO+"용 입고작업실적송신 안함(서윤 매니저 요청)";				
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					}else {
						outRec1  = JDTORecordFactory.getInstance().create();
						String curDate = YdUtils.getCurDate("yyyyMMddHHmmss");
						
						//PIDEV			
//						String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APPPI0", "*", "*");
						
//						if("Y".equals(sApplyYnPI)) {
							
							outRec1.setField("MQ_TC_CD"       	 , "M10YDLMJ1012");
							outRec1.setField("MQ_TC_CREATE_DDTT" , new String(curDate));
							
							outRec1.setField("YD_GP"          	 , YdConstant.YD_GP_PLATE2_GDS_YARD);
							outRec1.setField("DIST_GOODS_GP"  	 , "P");
							outRec1.setField("YARD_GP" 		  	 , "");
							outRec1.setField("GOODS_NO"       	 , szSTL_NO);
							outRec1.setField("STORE_LOC_CD"   	 , "T999999999");
							
							outRec1.setField("RECEIPT_DATE"   	 , curDate.substring(0, 8));
							outRec1.setField("RECEIPT_TIME"   	 , curDate.substring(8, 14));
							
//						} else {	
//							
//							outRec1.setField("TC_CODE"      		, "YDDMR002");
//							outRec1.setField("TC_CREATE_DDTT"		, new String(curDate));
//							outRec1.setField("GOODS_NO"       		, szSTL_NO);
//							outRec1.setField("RECEIPT_DATE"   		, curDate.substring(0, 8));
//							outRec1.setField("RECEIPT_TIME"   		, curDate.substring(8, 14));
//							outRec1.setField("YD_GP"          		, YdConstant.YD_GP_PLATE2_GDS_YARD);
//							outRec1.setField("STORE_LOC"      		, "T999999999");
//							outRec1.setField("PROD_ITEM_CODE" 		, "");
//							outRec1.setField("JMS_TC_CD"			, "YDDMR002");
//							outRec1.setField("MultiSend" 			, "Y");
//							
//						}
						
						this.sndJMSInfo(outRec1);
						szMsg ="[JSP Session "+ szOperationName +"] - 테스트용 시험재"+szSTL_NO+"용 입고작업실적송신";				
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
//PIDEV_QM						
//						if("Y".equals(sApplyYnPI)) {
							JDTORecord outRec3  = JDTORecordFactory.getInstance().create();
							outRec3.setField("JMS_TC_CD"      		, "YDQMJ601");
							outRec3.setField("JMS_TC_CREATE_DDTT"	, new String(curDate));
							outRec3.setField("STL_NO"       		, szSTL_NO);

							this.sndJMSInfo(outRec3);
							szMsg ="[JSP Session "+ szOperationName +"] - 품질테스트용 시험재"+szSTL_NO+"용 입고작업실적송신";				
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						}						
					}

					//시험 시편재는 여기서 종료한다.
					return ;
				}
			}
			//--------------------------------------------------------------------------------------------------------
			//	후판제품공통테이블에 Piling Code와 Book-Out위치 수정
			//--------------------------------------------------------------------------------------------------------
			String szRtnMsg = DaoManager.updPtPlateComm(recInTemp, 0);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg= "후판제품공통테이블에 YD_PILING_CD, YD_BOOK_OUT_LOC 등록 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			szMsg= "후판제품공통테이블에 YD_PILING_CD, YD_BOOK_OUT_LOC 등록 완료 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			
			//--------------------------------------------------------------------------------------------------------
			

			
			String sOrdYeojaeGp  = ydDaoUtils.paraRecChkNull(recGetVal,"ORD_YEOJAE_GP");
			String sYdPilingCd	 = ydDaoUtils.paraRecChkNull(recEdit,"YD_PILING_CD");
			String sYdBookOutLoc = ydDaoUtils.paraRecChkNull(recEdit,"YD_BOOK_OUT_LOC");

			szMsg="sYdBookOutLoc:" + sYdBookOutLoc + "/////szPL_WRK_PROC:" + szPL_WRK_PROC ;
			ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 검사대 통과시점에 ON-OFF LINE에 따른 저장위치 강제 UPDATE
			 */
			if (szPL_WRK_PROC.equals("2O")|| // B RT입고
				szPL_WRK_PROC.equals("2N")|| // C RT입고	 
				szPL_WRK_PROC.equals("BO")|| // A RT입고
				szPL_WRK_PROC.equals("2M")|| // 정정분기
				szPL_WRK_PROC.equals("BM")){ // 정정분기
				
				JDTORecordSet rsTemp  	= null;
				rsTemp  	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				
				recPara.setField("STL_NO",         szSTL_NO);
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				
				//적치단정보 조회
				YdStkLyrDao	ydStkLyrDao	= new YdStkLyrDao();
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp, 3);
				
				/*
				 * 2011.10.16 윤재광
				 * 야드맵상으로 저자위치가 없는 대상만 초기화한다.
				 * - 정보반납,반송때문에.
				 */
				if(rsTemp.size() == 0){
					
					String sBayGp = "";
					
					if(szPL_WRK_PROC.equals("2N")){
						sBayGp = "C";
					}else if(szPL_WRK_PROC.equals("2O")){
						sBayGp = "B";
					}else if(szPL_WRK_PROC.equals("BO")){
						sBayGp = "A";
					}else {
						sBayGp = "C";
					}
					
					JDTORecord 	  setRecord 		= JDTORecordFactory.getInstance().create();
					setRecord.setField("YD_GP",        		YdConstant.YD_GP_PLATE2_GDS_YARD);
					setRecord.setField("YD_BAY_GP",    		sBayGp);
					setRecord.setField("YD_EQP_GP",    		"RT");
					setRecord.setField("YD_STK_COL_NO",		"PA");
					setRecord.setField("YD_STK_BED_NO", 	"");
					setRecord.setField("YD_STK_LYR_NO", 	"");
					setRecord.setField("FNL_REG_PGM",  		"PPYDJ004"+szPL_WRK_PROC);
					setRecord.setField("MODIFIER",     		"PPYDJ004"+szPL_WRK_PROC);
					setRecord.setField("YD_STR_LOC_HIS1", 	"") ;
					setRecord.setField("YD_STR_LOC_HIS2", 	""); 
					setRecord.setField("PLATE_NO",    		szSTL_NO); 
					setRecord.setField("YD_STR_LOC", 		YdConstant.YD_GP_PLATE2_GDS_YARD+sBayGp+"RTPA");
					
					intRtnVal = ydStockDao.updPtComm_LOC(setRecord, 1);
				    if (intRtnVal <= 0) {
				        if (intRtnVal == 0) {
				            szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				        } else if (intRtnVal == -2) {
				            szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				        }
				    }
				}
			}
			
			/*=====================================================================================
			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    		 * 업무기준 : 2후판제품생산실적수신 시 파일링지시 송신기능
    		 * 수정자 : 조병기
    		 * 수정일자 : 2013.05.07
    		 * 파라미터 : 재료번호,파일링코드,북아웃코드 
    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			if ("2O".equals(szPL_WRK_PROC) ) {
				//2후판 no1 On-line 입고시만 처리 .. 차후 BO에서도 처리할 수 있도록 수정 예정
				this.procChangePilingCd3G(szSTL_NO, sYdPilingCd, sYdBookOutLoc, szPL_WRK_PROC);
			}

			szMsg = "2후판제품생산실적수신 처리(" + szMethodName + ") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "[2후판제품생산실적수신] Exception Error: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	}// end of procPl2GdsPrdWr()
	
	/**
	 * 오퍼레이션명 : 동별저장계획 취소 3기
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procYdBayLocPlnCncl3G(JDTORecord msgRecord)throws JDTOException  {
		
		String szMsg		  	= "";
		String szMethodName	  	= "procYdBayLocPlnCncl3G";
		String szOperationName  = "동별저장계획취소3기";	
		
		int intRtnVal 			= 0;		
		
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();	
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                            = msgRecord.getFieldString("LOG_ID");   		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 							// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "[동별저장계획취소 처리] (" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		try{

			recPara.setField("PTOP_PLNT_GP"			, msgRecord.getField("PTOP_PLNT_GP"));
			
			intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.deleteQueryId_0001");
			
		}catch(Exception e){
			
			szMsg = "[동별저장계획취소] Exception Error:" +e.getMessage();
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
		}

		szMsg = "[동별저장계획취소 처리] ("+szMethodName+") 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
		
	} // procYdBayLocPlnCncl3G
	
	
	/**
	 * 오퍼레이션명 :  2후판 이상재실적수신 처리
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procPl2AbmtWr(JDTORecord msgRecord)throws JDTOException  {
		
		String szMsg		  	= "";
		String szMethodName	  	= "procPl2AbmtWr";
		//String szOperationName  = " 2후판 이상재실적수신 처리";	
		
		int intRtnVal 			= 0;		
		
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();	
		
		try{
			
			// STL_APPEAR_GP : 재료외형구분 (F:날판, G:Plate)
			String szSTL_APPEAR_GP 	= ydDaoUtils.paraRecChkNull(msgRecord,"STL_APPEAR_GP");
			// PL_MTL_NO : 후판재료번호 (재료외형구분에 따라 날판번호 또는 Plate번호)
			String szPL_MTL_NO 		= ydDaoUtils.paraRecChkNull(msgRecord,"PL_MTL_NO");	
			
			if("F".equals(szSTL_APPEAR_GP)) {
				
				recPara.setField("PL_MPL_NO", szPL_MTL_NO);
				
				intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.deleteQueryId_0001");
				
			} else {

				recPara.setField("STL_NO", szPL_MTL_NO);
				
				intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.deleteQueryId_0002");
				
			}
			
		}catch(Exception e){
			
			szMsg = "[2후판 이상재실적수신 처리] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

		szMsg = "[ 2후판 이상재실적수신 처리] ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // procPl2AbmtWr
	
	/**
	 * 오퍼레이션명 : 동별저장계획 3기
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procYdBayLocPln3GNew(JDTORecord msgRecord)throws JDTOException  {
	
		EJBConnector ejbConn 		= null;
		String szMethodName         = "procYdBayLocPln3GNew";
		String szMsg                = "";
		String szPTOP_PLNT_GP 		= "";
		
		JDTORecord 		outRec 		= null;
		JDTORecordSet rsOut 		= JDTORecordFactory.getInstance().createRecordSet("retTmp");

////////////////////////////////////////////////////////////////////////////////////////
//2024.09.?? 로그 개선  START
//기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                            = msgRecord.getFieldString("LOG_ID");   		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 							// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "동별저장계획 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
//2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		try{
			
			ejbConn = new EJBConnector("default", "PlateSpecRegSeEJB", this);
			rsOut 	= (JDTORecordSet)ejbConn.trx("procYdBayLocPln3GNewTx", new Class[] { JDTORecord.class}, new Object[] { msgRecord });
			
			// 수신항목[PTOP_PLNT_GP: 조업공장구분]
			szPTOP_PLNT_GP 	= ydDaoUtils.paraRecChkNull(msgRecord,"PTOP_PLNT_GP");
			
			for(int i =1; i <= rsOut.size(); i++){
				rsOut.absolute(i);
				outRec = JDTORecordFactory.getInstance().create();
				outRec = rsOut.getRecord();
				// JMS TC CODE
				outRec.setField("JMS_TC_CD", "YDYDJ032");
				// 전문 발생 일시
				outRec.setField("JMS_TC_CREATE_DDTT", ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
				// 수신항목[PTOP_PLNT_GP: 조업공장구분]
				outRec.setField("PTOP_PLNT_GP", szPTOP_PLNT_GP);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// YDYDJ032 전문 처리 하는 procYdBayLocPln3GNewSub Method에 같은 logId 출력되게 하기 위해 logId SET 추가 개선
				outRec.setField("LOG_ID", logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
				
				ydDelegate.sendMsg_NoMakeTc(outRec);
			}	
			
		}catch(Exception e){
	
			szMsg = "[동별저장계획] Exception Error:" +e.getMessage();
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
		}

		szMsg = "동별저장계획 처리("+szMethodName+") 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

	} // end of procYdBayLocPln3GNew()
	
	/**
	 * 오퍼레이션명 : 동별저장계획 3기
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew" 
	 */
	public JDTORecordSet procYdBayLocPln3GNewTx(JDTORecord msgRecord)throws JDTOException  {
	
        //저장품DAO
		JDTORecordSet rsOut 	= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		String szMsg		  	= "";
		String szMethodName	  	= "procYdBayLocPln3GNewTx";
		String szPTOP_PLNT_GP 	= null;
		String szCHG_WO_FR_PNT	= null;
		String szCHG_WO_TO_PNT	= null;
		
		int intRtnVal 			= 0;
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();		

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                            = msgRecord.getFieldString("LOG_ID");   		// [T] + 전문일련번호) 형식으로  logId Get

String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 							// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "동별저장계획 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		try{

			// 수신항목[PTOP_PLNT_GP: 조업공장구분]
			szPTOP_PLNT_GP 	= ydDaoUtils.paraRecChkNull(msgRecord,"PTOP_PLNT_GP");
			
			// 수신항목[CHG_WO_FR_PNT: 가열로장입예정일련번호-From Point]
			szCHG_WO_FR_PNT = ydDaoUtils.paraRecChkNull(msgRecord,"CHG_WO_FR_PNT");

			// 수신항목[CHG_WO_TO_PNT: 가열로장입예정일련번호-To Point]
			szCHG_WO_TO_PNT = ydDaoUtils.paraRecChkNull(msgRecord,"CHG_WO_TO_PNT");

			szMsg = "[동별저장계획] szPTOP_PLNT_GP:" + szPTOP_PLNT_GP +", szCHG_WO_FR_PNT:" + szCHG_WO_FR_PNT + ", szCHG_WO_TO_PNT" + szCHG_WO_TO_PNT;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	
			
			//-----------------------------------------------------------------------------------------------
			
			/*
			 *	1. 압연지시에 해당하는 대상재들에 대한 예정 Plate정보들을 가지고 TB_YD_STOCK 테이블에 Insert/Update 한다. 
			 */
			intRtnVal = commDao.update(msgRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0053");
			
			//-----------------------------------------------------------------------------------------------
			// 2. 주문별로 저장계획을 적용하여 예정 입고 동을 구하고 그 동에서 예정 입고위치을 구하여 STOCK 을 UPDATE 한다.
			
			// 후판_PLATE작업지시로부터 주문번호 행번호를 추출
			intRtnVal = commDao.select(msgRecord, rsOut, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0058");
			if (intRtnVal <= 0){
				szMsg = "동별 저장계획 할 대상 없음";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			}
			
		}catch(Exception e){
	
			szMsg = "[동별저장계획] Exception Error:" +e.getMessage();
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
		}

		szMsg = "동별저장계획 처리("+szMethodName+") 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
		
		return rsOut;

	} // end of procYdBayLocPln3GNewTx()
	
	/**
	 * 오퍼레이션명 : 동별저장계획 3기
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procYdBayLocPln3GNewSub(JDTORecord msgRecord)throws JDTOException  {
	
        //저장품DAO
		PtOsCommDao ptOsCommDao = new PtOsCommDao();	
		YDDataUtil yddatautil 	= new YDDataUtil();
		YdStockDao ydStockDao 	= new YdStockDao();
		
		JDTORecord outRec1		= JDTORecordFactory.getInstance().create();
		JDTORecord outRec2		= JDTORecordFactory.getInstance().create();
		JDTORecord outRec3		= JDTORecordFactory.getInstance().create();
		JDTORecord outRec9		= JDTORecordFactory.getInstance().create();
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord recTemp		= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet outRecSet1= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet outRecSet2= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet outRecSet3= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet outRecSet9= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg		  	= "";
		String szMethodName	  	= "procYdBayLocPln3GNewSub";
		String szOperationName  = "동별저장계획3기";
		String szYD_PILING_CD 	= null;
		String szYD_PILING_CD2 	= null;
		String szMAIN_TRANS_AREA = null;		
		String szPTOP_PLNT_GP 	= null;
		String szYD_GP			= null;
		
		String szORD_LOC_CNT    = "";
		String szLOC_PLAN_CD 	= "";
		String szPLAN_DONG_TEMP = "";
		String szLenGp 			= "";
		String szPLAN_DONG      = "";
		String szORD_NO			= "";
		String szORD_DTL		= "";
		String szPRIOR_1_ACC_DONG 	= "";
		String szPILING_YD_BAY_GP	= "";
		String szPILING_BAY_CNT = "";
		String sRTN_LOC			= null;
		String sRTN_BOOKOUT_LOC = null;
		int intRtnVal 			= 0;
		double dbPLATE_WO_W     = 0;
		double dbPLATE_WO_L     = 0;
		double dblDONG_CAPA     = 0;

////////////////////////////////////////////////////////////////////////////////////////
//2024.09.?? 로그 개선  START
//기존 putLog -> putLogNew logId 출력 되게 개선
//String logId                            = msgRecord.getResultCode(); 				// 전문으로 부터 logid get
//String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

// YDYDJ031(동별저장계획)에서 만들어진 YDYDJ032(동별저장계획sub)전문인 경우 같은 logid 로 출력 하기 위해 
// msgRecord.getResultCode 시 "0000" return
String logId       						= msgRecord.getFieldString("LOG_ID");		 

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "동별저장계획 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);

//2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();		

		try{
			
			szYD_GP 		= YdConstant.YD_GP_PLATE2_GDS_YARD;
			szPTOP_PLNT_GP 	= ydDaoUtils.paraRecChkNull(msgRecord,"PTOP_PLNT_GP");
			szORD_NO		= ydDaoUtils.paraRecChkNull(msgRecord,"ORD_NO"); 
			szORD_DTL		= ydDaoUtils.paraRecChkNull(msgRecord,"ORD_DTL");
			
			szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL + " 처리시작 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			
			outRecSet1= JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("ORD_NO",    	szORD_NO);			
			recPara.setField("ORD_DTL",    	szORD_DTL);			
			//저장계획 코드 Read
			/*com.inisteel.cim.yd.common.dao.ptOsCommDao.getPtOsCommLocPlanCd_PIDEV*/
			intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet1, 300);
			
			if (intRtnVal <= 0) {
				szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL + " 저장계획 코드 Read error!!!, ErrorCode:" + intRtnVal;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				return ;
			}
			
			outRecSet1.absolute(1);
			outRec1 = JDTORecordFactory.getInstance().create();
			outRec1 = outRecSet1.getRecord();

			szYD_PILING_CD		= yddatautil.setDataDefault(outRec1.getField("ARG_YD_PILING_CD"),"");
			szLOC_PLAN_CD		= yddatautil.setDataDefault(outRec1.getField("LOC_PLAN_CD"),"");
			szMAIN_TRANS_AREA 	= yddatautil.setDataDefault(outRec1.getField("MAIN_TRANS_AREA"),"");
			dbPLATE_WO_W		= ydDaoUtils.paraRecChkNullDouble(outRec1, "ORD_CONV_W"); //주문지시폭
			dbPLATE_WO_L		= ydDaoUtils.paraRecChkNullDouble(outRec1, "ORD_CONV_LEN"); //주문지시길이
			
			/*
			 * 2024.09.13 후판동별저장계획 화면 개선요청 임진후 기사 요청 --REQ202408611796
			 * 수출재 신규고객사 추가. 고객사별 개별셋팅을 하기때문에 szLOC_PLAN_CD 는 버리고 파일링코드 앞 4자리 사용
			 * 
			 * */
			YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
			String szORD_GP = szORD_NO.substring(0,1);
			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "002");
			if ("Y".equals(sApplyYnPI) && ( (szORD_GP.equals("E")) || (szORD_GP.equals("F")) ) ){
				szMsg = "신규 동별저장계획기준 해당주문 :"+ szORD_NO + "-" +szORD_DTL +"권역구분["+szLOC_PLAN_CD+ "] 대신 ["+szYD_PILING_CD.substring(0,4)+"]사용";
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				szLOC_PLAN_CD = szYD_PILING_CD.substring(0,4);
			}
			
			
			//D010S1S2
			if(szYD_PILING_CD.equals("")){
				szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +" YD_PILING_CD error!!! ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				return ;
			}
			
			szYD_PILING_CD2 = szYD_PILING_CD;
			{
				outRecSet9= JDTORecordFactory.getInstance().createRecordSet("retTmp");
				recPara.setField("YD_PILING_CD",   	szYD_PILING_CD2);			
				recPara.setField("LOC_PLAN_CD",    	szLOC_PLAN_CD);			
				recPara.setField("YD_GP", 			szYD_GP); 
				recPara.setField("MAIN_TRANS_AREA", szMAIN_TRANS_AREA);
				/* 
				 * 2016.03.21 윤재광 
				 * - 1후판 저장계획 추가에 따른 적용을 위해 추가 PARAM  
				 */
				recPara.setField("PTOP_PLNT_GP",    szPTOP_PLNT_GP); 
				
				//Access저장동 READ
				intRtnVal = commDao.select(recPara, outRecSet9, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0059");
				if (intRtnVal <= 0) {
					szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") Access저장동  Read error!!!:" + intRtnVal;
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					return ;
				}

				outRecSet9.absolute(1);
				outRec9 = JDTORecordFactory.getInstance().create();
				outRec9 = outRecSet9.getRecord();

				szPRIOR_1_ACC_DONG = yddatautil.setDataDefault(outRec9.getField("DONG"),"");  
				
				szLenGp = szYD_PILING_CD.substring(6,7);
				
				//길이구분 U,L,X,내수				
				if((szLenGp.equals("U")) || 
				   (szLenGp.equals("L")) || 
				   (szLenGp.equals("X")) || (szLOC_PLAN_CD.substring(0, 1).equals("D")) ) {
					
					szPLAN_DONG = szPRIOR_1_ACC_DONG;
					
					szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 내수/초단척/장척/초장척 계획동:" + szPLAN_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					
				} else {
					
					outRecSet2= JDTORecordFactory.getInstance().createRecordSet("retTmp");
					recPara.setField("YD_PILING_CD",   	szYD_PILING_CD);			

					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getStklyrWithOrdLocCnt*/
					intRtnVal = ydStockDao.getYdStock(recPara, outRecSet2, 609);
					if (intRtnVal <= 0){
						szMsg = "주문 저장동 코드가 없음 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

						//저장계획 저장율이 낮은 동
						szPLAN_DONG = this.ToLocLowRate(outRecSet9);

						szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
						
					} else {

						
						outRecSet2.absolute(1);
						outRec2 = JDTORecordFactory.getInstance().create();
						outRec2 = outRecSet2.getRecord();

						szORD_LOC_CNT	= yddatautil.setDataDefault(outRec2.getField("CD_VAL"),"");  	// 주문 저장동 코드 
						
						szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 주문 저장동 코드 :" + szORD_LOC_CNT; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					
						
						// 동별 파일링 코드 위치 존재 여부
						outRecSet3= JDTORecordFactory.getInstance().createRecordSet("retTmp");
						recPara.setField("YD_PILING_CD",   	szYD_PILING_CD);			
						
						/*com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0060*/
						intRtnVal = commDao.select(recPara, outRecSet3, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0060");
						
						if (intRtnVal <= 0){
							szMsg = "동별 파일링 코드가  야드에 없음";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
							//주문 저장동 READ						
							if (szORD_LOC_CNT.equals("1")) {
								//주문저장동 수량 = 1			
								szPLAN_DONG = szPRIOR_1_ACC_DONG;
								
								szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장동 1개  계획동:" + szPLAN_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

							} else if (szORD_LOC_CNT.equals("2")) {
								//주문저장동 수량 = 2			
								szPLAN_DONG = "";
								//저장율이 80미만 SEARCH
								for(int j =1; j <= outRecSet9.size(); j++){
									outRecSet9.absolute(j);
									outRec9 = JDTORecordFactory.getInstance().create();
									outRec9 = outRecSet9.getRecord();
									szPLAN_DONG_TEMP	= ydDaoUtils.paraRecChkNull(outRec9, "DONG");  
									dblDONG_CAPA		= ydDaoUtils.paraRecChkNullDouble(outRec9, "DONG_CAPA");
									
									if(dblDONG_CAPA < 80) {
										szPLAN_DONG = szPLAN_DONG_TEMP; 
										j = outRecSet9.size() + 1;
									}
								}
								if(szPLAN_DONG.equals("")) {
									//저장계획 저장율이 낮은 동
									szPLAN_DONG = this.ToLocLowRate(outRecSet9);
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
								} else {
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장율 80 미만 선택 동:" + szPLAN_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
								}
							} else {
								
								// 주문저장동 코드 NOT IN ('1','2')	
								//저장계획 저장율이 낮은 동
								szPLAN_DONG = this.ToLocLowRate(outRecSet9);
			
								szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
							}	
						} else {

							szMsg = "동별 파일링 코드 야드에 있음";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
							
							outRecSet3.absolute(1);
							outRec3 = JDTORecordFactory.getInstance().create();
							outRec3 = outRecSet3.getRecord();
	
							szPILING_YD_BAY_GP	= yddatautil.setDataDefault(outRec3.getField("YD_BAY_GP"),"");       // 동일  PI저장동 
							szPILING_BAY_CNT	= yddatautil.setDataDefault(outRec3.getField("PILING_BAY_CNT"),"");  // 동일  PI위치 동수
							
							szMsg = "동일  PI 저장동 :" + szPILING_YD_BAY_GP + "/동일 PI위치동수 :" + szPILING_BAY_CNT; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

							
							if (szORD_LOC_CNT.equals("1")) {
								//주문저장동 코드 = 1								
								// 동일PI저장동 = 수송별 ACC동 AND 주문저장코드= 동일PI 위치 동수
								// if( (szPILING_YD_BAY_GP.equals(szPRIOR_1_ACC_DONG)) &&  
								if( (szORD_LOC_CNT.equals(szPILING_BAY_CNT)) ) {
									
									szPLAN_DONG = szPILING_YD_BAY_GP;	
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 기존파일링위치 동:" + szPILING_YD_BAY_GP; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
								} else {
									szPLAN_DONG = szPRIOR_1_ACC_DONG;
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 계획동:" + szPRIOR_1_ACC_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
								}  
							
							} else if (szORD_LOC_CNT.equals("2")) {
								//주문저장동 코드 = 2								
								double dblORD_LOC_CNT 	= Integer.parseInt(szORD_LOC_CNT);
								double dblPILING_BAY_CNT= Integer.parseInt(szPILING_BAY_CNT);
								
								// 주문저장코드 <= 동일PI위치 동수
								if( dblORD_LOC_CNT <= dblPILING_BAY_CNT ) {
									
									szPLAN_DONG = szPRIOR_1_ACC_DONG;
									
									szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 계획동:" + szPRIOR_1_ACC_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
							
								} else {
									//최우선순위 저장동 선택
									szPLAN_DONG = "";
									for(int j =1; j <= outRecSet9.size(); j++){
										outRecSet9.absolute(j);
										outRec9 = JDTORecordFactory.getInstance().create();
										outRec9 = outRecSet9.getRecord();
										
										szPLAN_DONG_TEMP	= ydDaoUtils.paraRecChkNull(outRec9, "DONG");  
										dblDONG_CAPA		= ydDaoUtils.paraRecChkNullDouble(outRec9, "DONG_CAPA");
										
										if(dblDONG_CAPA < 80) {
											szPLAN_DONG = szPLAN_DONG_TEMP; 
											j = outRecSet9.size() + 1;
										}
									}
									if(szPLAN_DONG.equals("")) {
										// 저장계획 저장율이 낮은 동
										szPLAN_DONG = this.ToLocLowRate(outRecSet9);
										szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
										ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

									} else {
										szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장율 80 미만 선택 동:" + szPLAN_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
										ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
									}
								}  
							} else {
								// 주문저장동 코드 NOT IN ('1','2')	
								//저장계획 저장율이 낮은 동
								szPLAN_DONG = this.ToLocLowRate(outRecSet9);
			
								szMsg = "해당주문 :"+ szORD_NO + "-" +szORD_DTL +"("+szYD_PILING_CD+") 저장계획 저장율이 낮은동 선택 동:" + szPLAN_DONG; 
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
							}	
						}	//szMsg = "동별 파일링 코드 야드에 있음";
					}		//szMsg = "주문 저장동 코드 있음";
				}			//길이구분 U,L,X,내수	
				
				if(!szPLAN_DONG.equals("")) {
					
					//-------------------------------------------------------
					//동이 정해졌으면 그 동에서 적치가능한 LOC 를 구한다.
					recTemp.setField("YD_GP", 			szYD_GP);
					recTemp.setField("YD_BAY_GP", 		szPLAN_DONG);
					recTemp.setField("YD_PILING_CD", 	szYD_PILING_CD);
					recTemp.setField("PTOP_PLNT_GP",    szPTOP_PLNT_GP); 
					
					sRTN_LOC = YdToLocDcsnUtil.getYdBayLocPln3G(recTemp);
					
					/*
					 * 2014.10.15 윤재광 - 이명운대리 요청
					 * G동 중척재이하는 무조건 2베드로 셋팅
					 */
					if("G".equals(szPLAN_DONG) && ("M".equals(szYD_PILING_CD.substring(6,7))||
							                       "S".equals(szYD_PILING_CD.substring(6,7))||
							                       "U".equals(szYD_PILING_CD.substring(6,7)))){
						recTemp.setField("YD_STK_BED_NO",   "02");
					}else{
						recTemp.setField("YD_STK_BED_NO",   sRTN_LOC.substring(6,8));
					}
					
					//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준) 야드구분값 셋팅
					if("PA".equals(szPTOP_PLNT_GP)){
						recTemp.setField("YD_GP", 	"K");
					}else{
						recTemp.setField("YD_GP", 	"T");
					}
					
					//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준)
			    	if( GetBreRule6.getYDB674(recTemp) ) {
			    		sRTN_BOOKOUT_LOC = StringHelper.evl(recTemp.getFieldString("YDB674_RV01_YD_BOOK_OUT_LOC"), "00000"); // 업무기준 YDB674 반환값#1 YD_BOOK_OUT_LOC
			    	} else {
			    		sRTN_BOOKOUT_LOC ="";
			    	}							
					//-------------------------------------------------------
					
					//-------------------------------------------------------
					recPara.setField("ORD_NO"				, szORD_NO);
					recPara.setField("ORD_DTL"				, szORD_DTL);					
					recPara.setField("YD_RCPT_PLN_STR_LOC"	, sRTN_LOC);
					recPara.setField("YD_BOOK_OUT_LOC"		, sRTN_BOOKOUT_LOC);
					recPara.setField("MODIFIER"				, "YDYDJ031");
					recPara.setField("PTOP_PLNT_GP"			, szPTOP_PLNT_GP); // 대상재중 같은 주문에 1,2후판 대상재 분리해서 적용 (실제로 쿼리에서 재료번호로 구분)
					
					//YD저장품 수정
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0010");
					
					recPara.setField("YD_PILING_CD", 	szYD_PILING_CD);
					//24.08.27 후판2팀 최성윤 매니저 요청. 동별저장계획 탐색시, 후판제품공통에도 piling_cd 및 book-out-loc 업데이트 --REQ202408611413
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jsp.common.Dao.updPtPlateCommForPilingBYOrdNo");
				}  
			}
			
		}catch(Exception e){
	
			szMsg = "[동별저장계획] Exception Error:" +e.getMessage();
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
		}

		szMsg = "동별저장계획 처리("+szMethodName+") 완료";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

	} // end of procYdBayLocPln3GNewSub()
	
//-----------------------------------------------------------------------------	
} // end of class

