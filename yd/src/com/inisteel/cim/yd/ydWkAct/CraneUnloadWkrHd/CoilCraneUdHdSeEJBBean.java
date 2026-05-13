package com.inisteel.cim.yd.ydWkAct.CraneUnloadWkrHd;

import java.util.List;

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
import com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.PtStlFrtoMoveDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.loc.CoilYdToLocDcsnUtil;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.yd.ydSch.CraneSch.CoilCrnSchSeEJBBean;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * 권하실적처리 Session EJB
 *
 * @ejb.bean name="CoilCraneUdHdSeEJB" jndi-name="CoilCraneUdHdSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
 */
public class CoilCraneUdHdSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	private YdUtils ydUtils 			= new YdUtils();
	private YdDaoUtils ydDaoUtils 		= new YdDaoUtils();
	private YdTcConst ydTcConst 		= new YdTcConst();
	private YdDelegate ydDelegate 		= new YdDelegate();
	private YdDBAssist ydDBAssist 		= new YdDBAssist();
	private EJBConnector ydEjbCon 		= new EJBConnector("default", this);
	private EJBConnector ydEjbCon2 		= new EJBConnector("default", this);
	private YdTcarSchDao ydTcarSchDao 	= new YdTcarSchDao();
	private SlabYdCommDAO commDao= new SlabYdCommDAO ();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	// [DEBUG] message flag
	private boolean bDebugFlag=true;
	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	

	/**
	 *      [A] 오퍼레이션명 : 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procTest(JDTORecord msgRecord)throws JDTOException  {
		
		
		String szMsg="";
		String szMethodName="procTest";
		
		

		String szRcvTcCode=ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode==null){
			szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return;
		}
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		
		
		//
		//
		//
		//
		//	toDo Something...
		//
		//
		//
		//
		//

		
		szMsg="Test정보수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	}// end of procTest()

	
	
	

	
//	/**
//     * 오퍼레이션명 :  C열연코일야드L2 권하실적처리 (Y5YDL009)
//     *  
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return String
//     * @throws DAOException
//	 */
//	public String procY5CrnUdWr(JDTORecord msgRecord) throws DAOException {
//		EJBConnector ejbConn 		= null;
//		String szMethodName         = "procY5CrnUdWr";
//		String szRtnMsg             = "";		
//		String szLogMsg             = "";
//		String szEjbJndiName 		= "CoilCraneLdHdSeEJB";
//		String szEjbMethod 			= "procY5CrnWrkOrdReq";
//		JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
//		try {
//			
//			//C열연코일야드L2 권하실적처리
//			ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
//			outRecord =(JDTORecord)ejbConn.trx("procY5CrnUdWrTX", new Class[] { JDTORecord.class}, new Object[] { msgRecord });
//			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), YdConstant.RETN_CD_TC_ERROR);
//			
//			if( sRTN_CD.equals(YdConstant.RETN_CD_TC_ERROR) ) {			//성공
//				szLogMsg = "권하실적 오류 (" + szRtnMsg + ")";
//				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, 1);
//				return YdConstant.RETN_CD_TC_ERROR;
//			}
//
//			
//			//크레인 작업지시 호출
//			szLogMsg = "[C열연권하실적처리]크레인 작업지시 호출- 메소드 콜 시작";
//			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//			
//			ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);			
//			szRtnMsg = (String)ejbConn.trx("procY5CrnWrkOrdReq", new Class[] { JDTORecord.class }, new Object[] { outRecord });
//			
//			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
//				szLogMsg = "[C열연권하실적처리]크레인 작업지시 호출 - 메소드 콜 완료";
//				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//			}else{
//				szLogMsg = "[C열연권하실적처리]크레인 작업지시 호출- 메소드 콜 오류발생";
//				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
//			}
//			//크레인작업지시 송신
////			ydDelegate.sendMsg(recInTemp);
//			szLogMsg = "[C열연권하실적처리]크레인 작업지시 호출["+"CoilCraneLdHdSeEJB"+"."+"procY5CrnWrkOrdReq"+"] - JMS 1전문전송 완료";
//			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);			
//			
//		} catch (Exception e) {
//			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
//			throw new DAOException(szSessionName + e.getMessage(), e);
//		}
//		return YdConstant.RETN_CD_SUCCESS;
//	} // end of procY0CrnUdWr
	
//    /**
//     * 오퍼레이션명 : C열연코일야드L2 권하실적처리 (Y5YDL009)
//     * SJH1 
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return String
//     * @throws JDTOException
//     * @ejb.transaction type="RequiresNew"
//     */         
//    public JDTORecord procY5CrnUdWrTX(JDTORecord msgRecord)throws JDTOException  {
//    	
//    	YdDelegate      ydDelegate      = new YdDelegate();
//    	YdEqpDao        ydEqpDao        = new YdEqpDao();
//    	YdTcarSchDao    ydTcarSchDao    = new YdTcarSchDao();
//    	YdCarSchDao     ydCarSchDao     = new YdCarSchDao();
//    	YdStockDao      ydStockDao      = new YdStockDao();
//    	YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
//    	YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao(); 
//    	
//        int intRtnVal = 0;
//        String szOperationName              = "C열연권하실적처리";
//        
//        //DATA SETTING시 사용
//        JDTORecord setRecord 				= JDTORecordFactory.getInstance().create();
//        JDTORecord setRecord2 				= JDTORecordFactory.getInstance().create();
//        //레코드셋에서 레코드값을 읽어 올 때 사용
//        JDTORecord getRecord                = JDTORecordFactory.getInstance().create(); ;
//        
//        //작업예약 업데이트 항목
//        JDTORecord bookrecord               = JDTORecordFactory.getInstance().create();
//        
//        //크레인 스케줄 야드작업진행상태 비교		 (작업예약이 끝났는지 확인하기위해 카운터 값을 가져옴)
//        JDTORecord outRecord                = JDTORecordFactory.getInstance().create();         
//        
//        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
//        JDTORecord getCrnschRecord 			= JDTORecordFactory.getInstance().create();
//        JDTORecord recInTemp                = null;
//        JDTORecord recOutTemp               = null;
//        JDTORecord recInPara                = null;
// 
//        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
//        JDTORecordSet getRecSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
//        JDTORecordSet getRecSet1 			= JDTORecordFactory.getInstance().createRecordSet("temp");
//        JDTORecord outRecord1  				= JDTORecordFactory.getInstance().create(); 
//        JDTORecord recIntTemp  				= JDTORecordFactory.getInstance().create(); 
//        JDTORecord recSendMsg            	= null;
//        
//        JDTORecord inRecord2  				= JDTORecordFactory.getInstance().create(); 
//        JDTORecord outRecord2  				= JDTORecordFactory.getInstance().create();
//        JDTORecord recGetVal       			= null;
//        
//        JDTORecordSet rsResult              = null;
//        JDTORecordSet rsResult2              = null;
//        JDTORecordSet rsWrkBookMtl			= null;
//        
//        String szMsg                        = "";
//        String szMethodName                 = "procY5CrnUdWrTX";
//        String szTcarEqpId                  = "";
//        
//        //크레인 XYZ축 			파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
//        String szYD_CRN_XAXIS     			= "";
//        String szYD_CRN_YAXIS     			= "";
//        String szYD_CRN_ZAXIS     			= "";
//        //WBOOK_ID				작업예약 완료 처리시 사용
//        String szYdWbookId              	= "";
//        String szCrnSchId                   = "";
//        String szYD_UP_WR_LOC               = "";
//        String szYD_SCH_CD                  = "";
//        String szYD_EQP_ID                  = "";
//        String szYD_DN_WR_LOC               = "";
//        String szYD_DN_WR_LAYER				= "";
//        String szSTL_NO                     = "";
//        String szYD_WRK_PROG_STAT           = "";
//        String szYD_TCAR_SCH_ID				= "";
//        String szYD_CAR_SCH_ID				= "";
//        String szRtnVal                     = "";
//        String szYdGp 						= "";
//        String szCurrProgCd					= "";
//		String sRTN_CD						= "";
//		String sTCAR_MOVE_SND				= "N";
//		String szHyscoGp                    = "";
//		String szSTL_APPEAR_GP				= "E";
//		String szYD_AID_WRK_YN              = "";
//		String szYD_DN_WRK_MODE2            = "";
//		String szDnCarFlag	                = "";
//        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
//        if(szRcvTcCode==null){
//            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            
//            recInTemp = JDTORecordFactory.getInstance().create();
//            recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//            return recInTemp;
//        }
//        
//        if(bDebugFlag){
//            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        }
//        
//        try{
//        	
//        	ydUtils.putLog(szSessionName, szMethodName, "▼▼▼▼▼[C열연 코일야드]권하실적처리 START▼▼▼▼▼", YdConstant.INFO);
//			szMsg = "[열연 코일야드L2] 권하실적처리 수신";
//			ydUtils.putLogMsg("H", "yd_monitorH", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//        	//-------------------------------------------------------------------------------------------------------------------
//        	//	파라미터 확인
//        	//-------------------------------------------------------------------------------------------------------------------        	
//        	ydUtils.displayRecord(szOperationName, msgRecord);
//        	
//	        intRtnVal = this.Y5ParamCheckCoil(msgRecord, getCrnschRecord, 1) ;
//	        if(intRtnVal == -1) {
//	            szMsg = "파라미터 Check중 Error	: " + intRtnVal;
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                recInTemp = JDTORecordFactory.getInstance().create();
//                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//                return recInTemp;
//	        }
//        	
//	        //파라미터 레코드 편집
//	        setRecord.setField("YD_CRN_SCH_ID",       	getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
//	        setRecord.setField("YD_SCH_CD",           	getCrnschRecord.getFieldString("YD_SCH_CD"));
//	        setRecord.setField("YD_DN_WR_LOC",        	getCrnschRecord.getFieldString("YD_DN_WR_LOC"));
//	        setRecord.setField("YD_DN_WR_LAYER",      	getCrnschRecord.getFieldString("YD_DN_WR_LAYER"));
//	        setRecord.setField("YD_DN_WRK_MODE2",      	getCrnschRecord.getFieldString("YD_DN_WRK_MODE2"));
//	        
//	        szCrnSchId 			= getCrnschRecord.getFieldString("YD_CRN_SCH_ID");
//	        szYD_EQP_ID 		= getCrnschRecord.getFieldString("YD_EQP_ID");
//	        szYD_SCH_CD 		= getCrnschRecord.getFieldString("YD_SCH_CD");
//	        szYD_WRK_PROG_STAT 	= getCrnschRecord.getFieldString("YD_WRK_PROG_STAT");
//	        szYD_DN_WR_LOC		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC");
//	        szYD_DN_WR_LAYER	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER");
//	        szYD_DN_WRK_MODE2	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WRK_MODE2");
//	        if( szCrnSchId.equals("") ) {
//                szMsg = "["+szOperationName+"] 크레인스케줄ID가 없습니다.";
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                szMsg = YdConstant.RETN_CD_NO_PARAM;
//                //throw new DAOException("<"+szMethodName+"> " + szMsg);
//                recInTemp = JDTORecordFactory.getInstance().create();
//                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//                return recInTemp;
//	        }
//	        
//	        
//	      //########################################################################################
//	        if("002".equals(szYD_DN_WR_LAYER)){
//		        szMsg="▼▼▼▼▼["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]: 2단 권하 시 1단 존재 여부 체크 로직 ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);  
//	            
//	            JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
//	            rsResult2 	= JDTORecordFactory.getInstance().createRecordSet("");
//	            setRecord2.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0 , 6));
//		        setRecord2.setField("YD_STK_BED_NO",        szYD_DN_WR_LOC.substring(6 , 8));
//		        
//	            String sQueryId = "com.inisteel.cim.yd.dao.YdStklyrDao.getYdStklyrDanCheck";
//	            intRtnVal = ydCommDao.select(setRecord2, rsResult2, sQueryId);
//	
//		        if(intRtnVal>0) {
//		        	
//		        	rsResult2.absolute(1);
//		        	recOutTemp = JDTORecordFactory.getInstance().create();
//		        	recOutTemp.setRecord(rsResult2.getRecord());
//		        	
//		        	
//		        	String sLEFT_COIL  = ydDaoUtils.paraRecChkNull(recOutTemp, "LEFT_COIL").trim();
//		        	String sRIGTH_COIL = ydDaoUtils.paraRecChkNull(recOutTemp, "RIGTH_COIL").trim();
//		        	
//		        	szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] sLEFT_COIL:"+sLEFT_COIL+" , sRIGTH_COIL:"+sRIGTH_COIL;
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO); 
//		        	
//		        	//하단에 코일이 하나라도 존재 안 하는 경우 권하처리를 안한다. 
//		        	if("".equals(sLEFT_COIL) || "".equals(sRIGTH_COIL)){
//		        	
//			        	recIntTemp = JDTORecordFactory.getInstance().create(); 
//		    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//		    			recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);						//야드설비ID
//		    			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_DN_WR);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//		    			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//		    			
//		    			recIntTemp.setField("YD_WRK_PROG_STAT", "4");	
//		    			recIntTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);	
//		    			recIntTemp.setField("YD_CRN_SCH_ID"	, getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));	
//		    			
//		    			recIntTemp.setField("YD_L3_MSG"		, "1단에 코일이 존재 하지 않아 권하불가");						//메세지
//		    			ydDelegate.sendMsg(recIntTemp);
//		
//			        	szMsg = "["+szOperationName+"] 1단에 코일이 존재 하지 않아 권하처리를 할 수 없습니다..";
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		                szMsg = YdConstant.RETN_CD_FAILURE;
//		                //throw new DAOException("<"+szMethodName+"> " + szMsg);
//		                recInTemp = JDTORecordFactory.getInstance().create();
//		                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//		                return recInTemp;
//	                
//		        	}
//		        }
//	        }
//	      //########################################################################################
//	        
//	        
//	        
//	        
//	        
//	        
////크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트
//	        szMsg="▼▼▼▼▼["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//	        //크레인스케줄 업데이트	 
//            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch*/
//	        intRtnVal = this.Y5UpdYdCrnschCoil(setRecord, 0);
//	        switch (intRtnVal) {
//	        	case 0	:
//	                szMsg = " Y5UpdYdCrnschCoil no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -1	:
//	                szMsg = " Y5UpdYdCrnschCoil dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -2	:
//	                szMsg = " Y5UpdYdCrnschCoil parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -3	:
//	                szMsg = " Y5UpdYdCrnschCoil execution failed!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        }
//	        //########################################################################################
//	        szMsg="▲▲▲▲▲["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//
//            
//            
//            
////	크레인스케줄 작업재료 조회           
//            szMsg="▼▼▼▼▼["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//	        //대상 데이터 SELECT
//            /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlCoilComm*/
//	        intRtnVal = this.Y5GetYdCrnschCoil(setRecord, getRecSet,400);
//	        switch (intRtnVal) {
//	        	case 0	:
//	                szMsg = " Y5GetYdCrnschCoil data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -2	:
//	                szMsg = " Y5GetYdCrnschCoilparameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        }
//	        
//	        
//	        getRecSet.first();
//	        //레코드셋의 사이즈값으로 ErrorCheck
//	        if(getRecSet.size() == 0){
//	            szMsg = " 크레인스케줄조회처리중 no data fount!!!, ErrorCode:" + intRtnVal;
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            //throw new DAOException("<"+szMethodName+"> " + szMsg);
//	            recInTemp = JDTORecordFactory.getInstance().create();
//                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//                return recInTemp;
//	        }
//	        getRecord = JDTORecordFactory.getInstance().create();
//	        getRecord.setRecord(getRecSet.getRecord());
//	        szSTL_NO = ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
//	        szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(getRecord, "STL_APPEAR_GP");
//	        
//	        szYD_AID_WRK_YN = StringHelper.evl(ydDaoUtils.paraRecChkNull(getRecord, "YD_AID_WRK_YN"),"N"); // 반납시 주작업 보조작업 check
//	        
//	        szMsg = szSTL_NO+ "권하시작함";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//	        
//	        
//	        getRecSet.first();
//	        getRecord = getRecSet.getRecord();
//	        
//	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 완료 - 대상재 건수 : " + getRecSet.size();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	        
//            szYD_DN_WR_LOC 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC");
//            szYD_UP_WR_LOC 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
//	        szYdWbookId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID") ;  //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
//	        szYdGp 			= ydDaoUtils.paraRecChkNull(getRecord, "YD_GP") ;
//	        szCurrProgCd 	= ydDaoUtils.paraRecChkNull(getRecord, "STL_PROG_CD") ;
//	        szHyscoGp 		= ydDaoUtils.paraRecChkNull(getRecord, "HYSCO_TRANS_GP") ;
//	        
//	        //########################################################################################
//            szMsg="▲▲▲▲▲["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//         
//            
//            
//            
////강제 권하
//            szMsg="▼▼▼▼▼["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 강제 권하 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
// 	        if(szYD_WRK_PROG_STAT.equals("5")) {
// 	        	
// 	        	if(szYD_SCH_CD.substring(0,1).equals("J") && 
// 	        	    	szYD_SCH_CD.substring(2,8).equals("PT03UM")){
// 	        		szMsg = "["+szOperationName+"] HYSCO출고는 강제 권하처리를 할 수 있다..";
//                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
// 	        	}else{
// 	        	 
//	 	        	if(szYD_SCH_CD.substring(0,1).equals("J") && 
//	 	        			((getRecord.getFieldString("YD_DN_WR_LOC").substring(1,2).equals("D")) ||
//	 	        			(getRecord.getFieldString("YD_DN_WR_LOC").substring(1,2).equals("E")) ||
//	 	        			(getRecord.getFieldString("YD_DN_WR_LOC").substring(1,2).equals("F")) ||
//	 	        			(getRecord.getFieldString("YD_DN_WR_LOC").substring(1,2).equals("G")) ||
//	 	        			(getRecord.getFieldString("YD_DN_WR_LOC").substring(1,2).equals("H")) )
//	 	        	   ){
//	 	        		
//	 	        		recIntTemp = JDTORecordFactory.getInstance().create(); 
//		        			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//		        			recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);						//야드설비ID
//		        			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_FRCE_DN);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//		        			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//		        			
//		        			recIntTemp.setField("YD_WRK_PROG_STAT", "4");	
//		        			recIntTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);	
//		        			recIntTemp.setField("YD_CRN_SCH_ID"	, getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));	
//		        			
//		        			recIntTemp.setField("YD_L3_MSG"		, "기존 동에서는 강제 권하처리 할수 없음");						//메세지
//		        			ydDelegate.sendMsg(recIntTemp);
//	
//		    	        	szMsg = "["+szOperationName+"] 기존 동에서는 강제 권하처리 할수 없습니다..";
//		                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		                    szMsg = YdConstant.RETN_CD_FAILURE;
//		                    //throw new DAOException("<"+szMethodName+"> " + szMsg);
//		                    recInTemp = JDTORecordFactory.getInstance().create();
//			                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//			                return recInTemp;
//		               
//	 	        	}
// 	        	}
//             
// 	        	if( ( //szYD_SCH_CD.substring(0,1).equals("H") && 
// 	        	     ((getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("0")) ||
//   	        	      (getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("1")) ||
// 	        	      (getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("2")) ||
// 	        	      (getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("3")) ||
// 	        	      (getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("4")) ||
// 	        	      (getRecord.getFieldString("YD_DN_WR_LOC").substring(0,4).equals("HB57"))|| //결로재 강재 권하 처리 로직 추가
// 	        	      (getRecord.getFieldString("YD_DN_WR_LOC").substring(0,4).equals("HB58"))|| //결로재 강재 권하 처리 로직 추가
// 	        	      (getRecord.getFieldString("YD_DN_WR_LOC").substring(0,4).equals("HD57"))|| //결로재 강재 권하 처리 로직 추가
//	        	      (getRecord.getFieldString("YD_DN_WR_LOC").substring(0,4).equals("HD58"))//결로재 강재 권하 처리 로직 추가
// 	        	      ))
// 	        	      
// 	        	     ||(szYD_SCH_CD.substring(0,1).equals("J") && 
// 	        	    	szYD_SCH_CD.substring(2,8).equals("PT03UM") ) //하이스코 출고 인경우        	     	        	     
// 	        	   ) {
// 	        		
// 	        		szRtnVal = this.Y5ForCedUdCoil(szSTL_NO,msgRecord);
// 	        		if (szRtnVal.equals(YdConstant.RETN_CD_SUCCESS)) {
// 	        			
// 	        		} else {
//
// 	        			recIntTemp = JDTORecordFactory.getInstance().create(); 
// 	        			recIntTemp.setField("MSG_ID"        , "YDY5L005");
// 	        			recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);						//야드설비ID
// 	        			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_FRCE_DN);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
// 	        			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
// 	        			
// 	        			recIntTemp.setField("YD_WRK_PROG_STAT", "4");	
// 	        			recIntTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);	
// 	        			recIntTemp.setField("YD_CRN_SCH_ID"	, getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));	
// 	        			
// 	        			recIntTemp.setField("YD_L3_MSG"		, "권하처리 할수없습니다.");						//메세지
// 	        			ydDelegate.sendMsg(recIntTemp);
//
// 	    	        	szMsg = "["+szOperationName+"] 강제 권하처리 할수 없습니다..";
// 	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
// 	                    szMsg = YdConstant.RETN_CD_FAILURE;
// 	                    //throw new DAOException("<"+szMethodName+"> " + szMsg);
// 	                    recInTemp = JDTORecordFactory.getInstance().create();
//		                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//		                return recInTemp;
// 	    	        }
// // 추가	        		
// 	        	} else if( //szYD_SCH_CD.substring(0,1).equals("J") && 
// 		        	       ((getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("0")) || 
// 		 		        	(getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("1")) || 
// 		 		        	(getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("2")) || 
// 		 		        	(getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("3")) || 
// 		 		        	(getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("4")) || 
// 		 		        	(getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("5")) ||
// 		 		        	(getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("6")) ||
// 		 		        	(getRecord.getFieldString("YD_DN_WR_LOC").substring(2,3).equals("7"))      	     	        	     
// 			        	   )) {
// 			        		
// 			        		szRtnVal = this.Y5ForCedUdCoilGds(szSTL_NO,msgRecord);
// 			        		if (szRtnVal.equals(YdConstant.RETN_CD_SUCCESS)) {
// 			        			
// 			        		} else {
//
// 			        			recIntTemp = JDTORecordFactory.getInstance().create(); 
// 			        			recIntTemp.setField("MSG_ID"        , "YDY5L005");
// 			        			recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);						//야드설비ID
// 			        			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_FRCE_DN);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
// 			        			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
// 			        			
// 			        			recIntTemp.setField("YD_WRK_PROG_STAT", "4");	
// 			        			recIntTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);	
// 			        			recIntTemp.setField("YD_CRN_SCH_ID"	, getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));	
// 			        			
// 			        			recIntTemp.setField("YD_L3_MSG"		, "권하처리 할수없습니다.");						//메세지
// 			        			ydDelegate.sendMsg(recIntTemp);
//
// 			    	        	szMsg = "["+szOperationName+"] 강제 권하처리 할수 없습니다..";
// 			                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
// 			                    szMsg = YdConstant.RETN_CD_FAILURE;
// 			                    //throw new DAOException("<"+szMethodName+"> " + szMsg);
// 			                    recInTemp = JDTORecordFactory.getInstance().create();
// 				                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
// 				                return recInTemp;
// 			    	        }
// 			        		
// 	        	} else {
// 	        			
// 	        		if(szYD_SCH_CD.substring(0,1).equals("H") && 
// 	 	        	    	(szYD_SCH_CD.substring(2,8).equals("KD05LM") || szYD_SCH_CD.substring(2,8).equals("DD05LM") ) ){ //스크랩 강제 권하처리(E동 추가) 2020.01.06
// 	        			
// 	        		}else if(szYD_SCH_CD.substring(0,1).equals("H") && 
// 	        				getRecord.getFieldString("YD_DN_WR_LOC").substring(2,4).equals("CR") ){ //시편채취 강제 권하처리
// 	        			
// 	        		}else{ 	 	        	    	
//	         			recIntTemp = JDTORecordFactory.getInstance().create(); 
//	         			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//	         			recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);						//야드설비ID
//	         			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_FRCE_DN);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//	         			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//	         			recIntTemp.setField("YD_L3_MSG"		, "소재창고가 or 야드위치가 아닙니다.");		//메세지
//	         			
//	         			recIntTemp.setField("YD_WRK_PROG_STAT", "4");	
//	         			recIntTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);	
//	         			recIntTemp.setField("YD_CRN_SCH_ID"	, getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));	
//	         			
//	         			ydDelegate.sendMsg(recIntTemp);
//	 	        		
//	 	        		szMsg = "["+szOperationName+"] 강제 권하 할수 없습니다.";
//	 	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	 	                szMsg = YdConstant.RETN_CD_FAILURE;
//	 	                //throw new DAOException("<"+szMethodName+"> " + szMsg);
//	 	                recInTemp = JDTORecordFactory.getInstance().create();
//		                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//		                return recInTemp;
// 	        		}
// 	        	}    
// 	        }
// 	        //########################################################################################
// 	       szMsg="▲▲▲▲▲["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 강제 권하 END ▲▲▲▲▲";
//           ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//           
//           
//           
//
////작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
//	        if ((!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("2")) 
//	        		&& (!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("3")) 
//	        		&& (!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("S"))
//	        ) {
//	            szMsg = "작업진행상태가   권상('2') 또는 권하대기('3') 응답대기(S) 이 아닙니다., ErrorCode:";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            szMsg = YdConstant.RETN_CD_FAILURE;
//                m_ctx.setRollbackOnly();
//                recInTemp = JDTORecordFactory.getInstance().create();
//                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//                return recInTemp;
//	        }
//	        
//	        //-------------------------------------------------------------------------------------------------------------------
//	        //권하지시위치와 권하실적위치가 다르면 권하지시위치의 맵정보를 Clear시킴
//	        //-------------------------------------------------------------------------------------------------------------------
//	        if(( getRecord.getFieldString("YD_DN_WR_LOC").substring(0, 6).equals("XX0101"))) {
//	            szMsg = "권하실적 위치 이상 : " + getRecord.getFieldString("YD_DN_WR_LOC");
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            szMsg = YdConstant.RETN_CRN_STATUS_ERR;
//                m_ctx.setRollbackOnly();
//                recInTemp = JDTORecordFactory.getInstance().create();
//                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//                return recInTemp;
//	        }
//	        if (!getRecord.getFieldString("YD_DN_WR_LOC").equals(getRecord.getFieldString("YD_DN_WO_LOC"))) {
//
//	        	szMsg = "▼▼▼▼▼[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            //########################################################################################
//	            ydUtils.putLog(szSessionName, szMethodName, ""+getRecord.getFieldString("YD_DN_WR_LOC"), YdConstant.INFO);
//	            ydUtils.putLog(szSessionName, szMethodName, ""+getRecord.getFieldString("YD_DN_WO_LOC"), YdConstant.INFO);
//	            
//	        	//크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
//	        	if(szYD_CRN_XAXIS.equals("") && szYD_CRN_YAXIS.equals("") && szYD_CRN_ZAXIS.equals("")) {
//	        		szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_XAXIS");
//	        		szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_YAXIS");
//	        		szYD_CRN_ZAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_ZAXIS");
//	        	}
//	        	
// 	        	if(( getRecord.getFieldString("YD_DN_WO_LOC").substring(0, 6).equals("XX0101"))) {
////(10.05)	        		
//	        	} else {
////권하 지시위치 Clear
//		        	intRtnVal = this.Y5ClearYdStklyrCoil(getRecSet,1) ;
//		        	switch (intRtnVal) {
//			        	case 0	:
//			                szMsg = "[" + szOperationName + "] 권하위치 없습 found!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//			                m_ctx.setRollbackOnly();
//			                recInTemp = JDTORecordFactory.getInstance().create();
//			                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//			                return recInTemp;
//			        	case -1	:
//			                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                m_ctx.setRollbackOnly();
//			                recInTemp = JDTORecordFactory.getInstance().create();
//			                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//			                return recInTemp;
//			        	case -2	:
//			                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//			                m_ctx.setRollbackOnly();
//			                recInTemp = JDTORecordFactory.getInstance().create();
//			                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//			                return recInTemp;
//			        	case -3	:
//			                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                m_ctx.setRollbackOnly();
//			                recInTemp = JDTORecordFactory.getInstance().create();
//			                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//			                return recInTemp;
//			        }
//	        	}	
// 	        	//########################################################################################
//		        szMsg = "▲▲▲▲▲[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear END ▲▲▲▲▲";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	        }
//	        
//	        //-------------------------------------------------------------------------------------------------------------------
//	        
//	        
//	        
//	        
//	        //적치단의 정보등록 수행
//	        if(getRecord.equals(null)) 	                throw new DAOException();
//  
////적치단 권하정보등록	        
//	        szMsg = "▼▼▼▼▼[" + szOperationName + "] 적치단 권하정보등록 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//	        //적치단 권하정보등록  
//	        intRtnVal = this.Y5RegYdStklyrCoil(getRecSet,1) ;
//	        //regYdStklyr메소드에서 Error발생시 Message를 보여준다.
//	        if(intRtnVal<=0) {
//	        	szMsg = "적치단 권하정보등록중 Error!! return 처리 및 Exception처리는 하지않는다.";
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//	        }
//	        //########################################################################################
//	        szMsg = "▲▲▲▲▲[" + szOperationName + "] 적치단 권하정보등록 END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	      
//            
//            
//            
////크레인스케줄 권하위치등록            
//            szMsg = "▼▼▼▼▼[" + szOperationName + "] 크레인스케줄 권하위치등록 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//            
//	        //크레인스케줄 table 업데이트
//	        setRecord = JDTORecordFactory.getInstance().create();
//	        setRecord.setField("YD_CRN_SCH_ID",      	getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
//	        setRecord.setField("YD_DN_WR_LOC",       	getCrnschRecord.getFieldString("YD_DN_WR_LOC")) ;
//	        setRecord.setField("YD_DN_WR_LAYER",     	getCrnschRecord.getFieldString("YD_DN_WR_LAYER")) ;
//	        setRecord.setField("YD_DN_WRK_ACT_GP",   	getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP") );
//	        setRecord.setField("YD_DN_WR_XAXIS",     	getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
//	        setRecord.setField("YD_DN_WR_YAXIS",     	getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
//	        setRecord.setField("YD_DN_WR_ZAXIS",     	getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
//	        setRecord.setField("YD_WRK_PROG_STAT",   	getCrnschRecord.getFieldString("YD_WRK_PROG_STAT"));
//	        setRecord.setField("YD_DN_WRK_MODE2",   	getCrnschRecord.getFieldString("YD_DN_WRK_MODE2"));
//	        setRecord.setField("YD_DN_CMPL_DT",      	YdUtils.getCurDate("yyyyMMddHHmmss"));
//	        setRecord.setField("YD_GP",   				getCrnschRecord.getFieldString("YD_GP"));
//	        
//	        
//	        szYD_DN_WR_LOC 			= ydDaoUtils.paraRecChkNull(setRecord, "YD_DN_WR_LOC");
//	        String szYD_DN_CMPL_DT 	= ydDaoUtils.paraRecChkNull(setRecord, "YD_DN_CMPL_DT");       
//	        
//	        //계상일자
//	        String szYdGp2 = StringHelper.evl(getCrnschRecord.getFieldString("YD_DN_WR_LOC").substring(0, 1),"J");
//	        String szYD_WRK_HDS_DD ="";
//	        if(szYdGp2.equals("J")){
//	        	szYD_WRK_HDS_DD	= YdUtils.getDefaultHdsDate();
//	        }else {
//	        	szYD_WRK_HDS_DD	= YdUtils.getDefaultHdsDate7();
//	        }
//	        setRecord.setField("YD_WRK_HDS_DD",   		szYD_WRK_HDS_DD);
//	        
//	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]에 권하실적정보 수정 시작";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	          
//	        intRtnVal = this.Y5UpdYdCrnschCoil(setRecord, 0);
//	        switch (intRtnVal) {
//	        	case 0	:
//	                szMsg = "procY5CrnUdWr no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -1	:
//	                szMsg = "procY5CrnUdWr dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -2	:
//	                szMsg = "procY5CrnUdWr parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -3	:
//	                szMsg = "procY5CrnUdWr execution failed!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        }
//	        
//	        //########################################################################################
//	        szMsg = "▲▲▲▲▲[" + szOperationName + "] 크레인스케줄 권하위치등록 END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//  
//	        
//            
//	         
//
////크레인 작업재료 삭제처리
//	        szMsg = "▼▼▼▼▼[" + szOperationName + "] 크레인 작업재료 삭제처리 START ▼▼▼▼▼";
//	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	        //########################################################################################
//	        setRecord = JDTORecordFactory.getInstance().create();
//	        setRecord.setField("YD_CRN_SCH_ID",	getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
//	        setRecord.setField("DEL_YN",        "Y");
//	        setRecord.setField("MODIFIER",      "YDSYSTEM");
//	        /*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.updYdCrnwrkmtlYdCrnSchId*/
//	        intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl_YD_CRN_SCH_ID(setRecord);
//	        if(intRtnVal <= 0) {
//	              szMsg = "크레인스케줄작업재료 삭제중 Error!! Code : " + intRtnVal;
//	              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	              m_ctx.setRollbackOnly();
//	              recInTemp = JDTORecordFactory.getInstance().create();
//	              recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	              return recInTemp;
//	        }
//	        //########################################################################################
//	        szMsg = "▲▲▲▲▲[" + szOperationName + "] 크레인 작업재료 삭제처리 END ▲▲▲▲▲";
//	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//                      
//	        
//	        
//	        
// 
//	        
//	        szMsg = "▼▼▼▼▼[" + szOperationName + "] 저장위치의 설비구분 : " + szYD_DN_WR_LOC.substring(2, 4) + "스케줄코드의 설비구분 : " + szYD_SCH_CD.substring(2, 4)+" START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            
// 
//            
//          //만약  to위치는 대차인데 대차스케줄코드가 아닌경우...는 직상차로 보고 대차스케줄의 상차작업예약id를 등록한다.
//	        if((szYD_DN_WR_LOC.substring(2, 4).equals("TC"))){ 
////대차 id로 대차스케줄을 조회한다.
//	        	
//	        	szMsg = "▼▼▼▼▼[" + szOperationName + "] 대차스케줄의 상차작업예약id를 등록 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            
//	            szMsg = " 대차스케줄의 상차작업예약id를 등록한다!! ";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//	            
//	        	szTcarEqpId = "JXTC" + ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(4,6);
//                
//	        	ydUtils.putLog(szSessionName, szMethodName, "대차 id : " + szTcarEqpId, YdConstant.DEBUG);	 
//	        	
//	        	recInPara 	= JDTORecordFactory.getInstance().create();
//	        	recInPara.setField("YD_EQP_ID", szTcarEqpId);
//	        	
//	        	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
//	        	intRtnVal 	= ydTcarSchDao.getYdTcarsch(recInPara, rsResult, 4);
//	        	if(intRtnVal <= 0) {
//	                szMsg = szTcarEqpId + "로 생성된 대차 스케줄이 없습니다.";
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	}
//	        	
//	        	rsResult.absolute(1);
//	        	recOutTemp = JDTORecordFactory.getInstance().create();
//	        	recOutTemp.setRecord(rsResult.getRecord());
//	        	
//	        	
//	        	//조회된 대차 스케줄에 상차작업예약id를 등록한다.
//	        	recInPara = JDTORecordFactory.getInstance().create();
//	        	recInPara.setField("YD_TCAR_SCH_ID"			, ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID").trim());
//	        	recInPara.setField("YD_CARLD_WRK_BOOK_ID"	, szYdWbookId);
//	        	recInPara.setField("YD_EQP_WRK_STAT"		, "L");
//	        	recInPara.setField("YD_CAR_PROG_STAT"		, "4");
//	        	recInPara.setField("YD_CARLD_STOP_LOC"		, szYD_DN_WR_LOC.substring(0,6));
//	        	intRtnVal = ydTcarSchDao.updYdTcarsch(recInPara, 0);
//	        	if(intRtnVal <= 0) {
//	                szMsg = "직상차용 대차스케줄 상차작업예약 등록시 Error";
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	}
//	        	
//	        	
//	        
//	        
//	        
//	        
//		        //만약  to위치는 차량인데 차량스케줄코드가 아닌경우...는 직상차로 보고 차량스케줄의 상차작업예약id를 등록한다.
////		        if(!szYD_SCH_CD.substring(2, 8).equals("TR05MM")&&
////		 	           !szYD_SCH_CD.substring(2, 8).equals("TR06MM")&&
////			           !szYD_SCH_CD.substring(2, 8).equals("TR07MM")&&
////			           !szYD_SCH_CD.substring(2, 8).equals("TR08MM")&&
////			           !szYD_SCH_CD.substring(2, 8).equals("TR57MM")&&
////			           !szYD_SCH_CD.substring(2, 8).equals("TR58MM")
////		        		){
//		        if(!(("TR0".equals(szYD_SCH_CD.substring(2 , 5))||"TR5".equals(szYD_SCH_CD.substring(2 , 5))) && "MM".equals(szYD_SCH_CD.substring(6 , 8)))){	
//			        if((szYD_DN_WR_LOC.substring(2, 4).equals("PT") && (!szYD_SCH_CD.substring(2, 4).equals("PT") && !szYD_SCH_CD.substring(2, 4).equals("TR")&& !szYD_SCH_CD.substring(2, 4).equals("TT"))) ||
//			           (szYD_DN_WR_LOC.substring(2, 4).equals("TR") && (!szYD_SCH_CD.substring(2, 4).equals("PT") && !szYD_SCH_CD.substring(2, 4).equals("TR")&& !szYD_SCH_CD.substring(2, 4).equals("TT")))) {
//			  			            
//			            szMsg = "▼▼▼▼▼[" + szOperationName + "] 차량스케줄의 직상차작업예약id를 등록 START ▼▼▼▼▼";
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//			            //########################################################################################
//		               
//			        	szMsg = "대차차량 id : " + szTcarEqpId;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//		                
//			        	recInPara = JDTORecordFactory.getInstance().create();
//			        	recInPara.setField("YD_EQP_ID", szTcarEqpId);
//			        	
//			        	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//			        	intRtnVal = ydCarSchDao.getYdCarsch(recInPara, rsResult, 8);
//			        	if(intRtnVal <= 0) {
//			                szMsg = szTcarEqpId + "로 생성된 차량 스케줄이 없습니다.";
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                m_ctx.setRollbackOnly();
//			                recInTemp = JDTORecordFactory.getInstance().create();
//			                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//			                return recInTemp;
//			                
//			        	}
//			        	
//			        	rsResult.absolute(1);
//			        	recOutTemp = JDTORecordFactory.getInstance().create();
//			        	recOutTemp.setRecord(rsResult.getRecord());
//			        	
//			        	szMsg = "▲▲▲▲▲[" + szOperationName + "] 차량스케줄의 직상차작업예약id를 등록  END ▲▲▲▲▲";
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//			        	
//			        }
//		        }
//	        
//		        
// 
////대차 및 차량 스케줄 이송재료 Handling    
// 
//		        szMsg = "▼▼▼▼▼[" + szOperationName + "] 대차 및 차량 스케줄 이송재료 Handling START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            
//	        	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschUDLDWRKBOOKID*/
//	        	szYD_TCAR_SCH_ID = this.Y5SetYdTcarCoil(getRecSet, 1) ; 
//	        	
//	        	if(szYD_TCAR_SCH_ID.equals("")) {
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	
//	        	}else{
//	        		szMsg = "대차이송재료 등록 완료";
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	        	}
//	        	szTcarEqpId = "JX" + ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(2,6);
//	        	//대차스케줄 호출
//	        	recSendMsg = JDTORecordFactory.getInstance().create();
//	        	recSendMsg.setField("JMS_TC_CD"		, "YDYDJ521");
//	        	recSendMsg.setField("YD_LD_UD_GP"	, "L");
//	        	recSendMsg.setField("YD_WBOOK_ID"	, szYdWbookId);
//	        	recSendMsg.setField("YD_EQP_ID"		, szTcarEqpId);
//	
//	        	outRecord1 =(JDTORecord)ydEjbCon.trx("CoilTransEqpSchSeEJB", "procY5TcarSch", recSendMsg);
//	   			
//	        	sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//	   			sTCAR_MOVE_SND	= StringHelper.evl(outRecord1.getFieldString("TCAR_MOVE_SND"), "N");
//	   		
//	   			if(sRTN_CD.equals("0")){
//	   				throw new DAOException("<"+szMethodName+"> CraneUdHdSeEJB " + "대차작업지시 이상(procY5TcarSch)");
//	   			}
//	   			// 권하처리 완료 후 이동지시 실적 날리기 위해
//	   			ydUtils.putLog(szSessionName, szMethodName, "권하처리 완료 후 이동지시 실적 sRTN_CD" +  sRTN_CD, YdConstant.DEBUG);		   			
//	   			szMsg = "▲▲▲▲▲[" + szOperationName + "] 대차 및 차량 스케줄 이송재료 Handling  END ▲▲▲▲▲";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            
//	            //########################################################################################
//	   			szMsg = "▲▲▲▲▲[" + szOperationName + "] 대차스케줄의 상차작업예약id를 등록 END ▲▲▲▲▲";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	        }
//	        
//	        
//	        
//
////차량작업 인 경우 
////            if(!szYD_SCH_CD.substring(2, 8).equals("TR05MM")&&
////               !szYD_SCH_CD.substring(2, 8).equals("TR06MM")&&
////               !szYD_SCH_CD.substring(2, 8).equals("TR15MM")&&
////               !szYD_SCH_CD.substring(2, 8).equals("TR16MM")&&
////       		   !szYD_SCH_CD.substring(2, 8).equals("TR07MM")&&
////    		   !szYD_SCH_CD.substring(2, 8).equals("TR08MM")&&
////    		   !szYD_SCH_CD.substring(2, 8).equals("TR17MM")&&
////    		   !szYD_SCH_CD.substring(2, 8).equals("TR18MM")&&
////    		   !szYD_SCH_CD.substring(2, 8).equals("TR57MM")&&
////    		   !szYD_SCH_CD.substring(2, 8).equals("TR58MM")&&
////    		   !szYD_SCH_CD.substring(2, 8).equals("TR67MM")&&
////    		   !szYD_SCH_CD.substring(2, 8).equals("TR68MM")
////            ){
//            if(!("TR".equals(szYD_SCH_CD.substring(2 , 4)) && "MM".equals(szYD_SCH_CD.substring(6 , 8)))){	
//	            //########################################################################################################
//	            //크레인 권하실적 위치가 차량인 경우 - 구내운송:이송상차완료 , 제품출하: 코일제품고간이송상하차완료
//	            //############################################################### #########################################
//	            if(szYD_DN_WR_LOC.substring(2, 4).equals("PT") || szYD_DN_WR_LOC.substring(2, 4).equals("TR")){		      
//		            szMsg = "▼▼▼▼▼[" + szOperationName + "] [상차]크레인 권하실적 위치가 차량인 경우 START ▼▼▼▼▼";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//		            //########################################################################################
//	           	
//	            	intRtnVal = this.Y5SetYdCarCoil(getRecSet, 1) ; 
//	            	if(intRtnVal == -1) {		            
//		                //recInTemp = JDTORecordFactory.getInstance().create();
//		                //recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//		                //return recInTemp;
//		                
//		                szMsg = "차량 작업 진행관리 호출(상차작업)해당 차량 스케줄의 작업예약이 없습니다.";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//						
//		           	} else {
//			        	szMsg = "차량이송재료 등록 완료";
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		           	}
//	            	
//	            	// 차량 작업 진행관리 호출
//	            	recInTemp = JDTORecordFactory.getInstance().create();
//	            	recInTemp.setField("YD_CRN_SCH_ID",	szCrnSchId);
//	            	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
//	            	recInTemp.setField("CAR_LDUD_GP",   "U");
//	            	recInTemp.setField("YD_DN_WR_LOC", 	szYD_DN_WR_LOC);
//	            	recInTemp.setField("YD_UP_WR_LOC", 	szYD_UP_WR_LOC);
//	            	recInTemp.setField("STL_NO", 		szSTL_NO );
//	            	recInTemp.setField("STL_APPEAR_GP", 		szSTL_APPEAR_GP ); //재료외형구분 E;소재 , Y:제품
//	            	
//	
//					szMsg = "차량 작업 진행관리 호출(상차작업)";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//					ydUtils.displayRecord(szOperationName, recInTemp);
//	
//	            	//차량 작업진행관리 호출(상차)
//					szYD_CAR_SCH_ID = this.procY5CarWrkStatCtrCoil(recInTemp);
//	            	if(intRtnVal == -1) {
//	                    szMsg = "차량 작업 진행관리 호출처리 중 Error";
//	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            	}
//	            	//########################################################################################
//	            	
////	            	151111 hun TT 차량 상차 플레그 ( 다음작업요구시 사용 )
//	            	if(szYD_DN_WR_LOC.substring(2, 4).equals("PT")){
//	            		szDnCarFlag = "Y";
//	            	}
//	            	
//	            	szMsg = "▲▲▲▲▲[" + szOperationName + "] [상차]크레인 권하실적 위치가 차량인 경우  END ▲▲▲▲▲";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            }
//	            
//	            //########################################################################################################
//	            //크레인 권상실적 위치가 차량인 경우 - 구내운송:이송하차완료 , 제품출하: 코일제품고간이송상하차완료
//	            //########################################################################################################
//	            if(szYD_UP_WR_LOC.substring(2, 4).equals("PT")|| szYD_UP_WR_LOC.substring(2, 4).equals("TR")){
//		            
//		            szMsg = "▼▼▼▼▼[" + szOperationName + "] [하차]크레인 권상실적 위치가 차량인 경우 START ▼▼▼▼▼";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//		            //########################################################################################
//	        		recInTemp = JDTORecordFactory.getInstance().create();
//	            	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
//	            	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
//	            	recInTemp.setField("CAR_LDUD_GP",   "L");
//	            	recInTemp.setField("YD_DN_WR_LOC", 	szYD_DN_WR_LOC);
//	            	recInTemp.setField("YD_UP_WR_LOC", 	szYD_UP_WR_LOC);
//	            	recInTemp.setField("STL_NO", 		szSTL_NO);
//	            	recInTemp.setField("STL_APPEAR_GP", 		szSTL_APPEAR_GP ); //재료외형구분 E;소재 , Y:제품
//	            
//					szMsg = "차량 작업 진행관리 호출(하차작업)";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//					ydUtils.displayRecord(szOperationName, recInTemp);                	
//	            	
//	            	//차량 작업 진행관리 호출(하차)
//					szYD_CAR_SCH_ID = this.procY5CarWrkStatCtrCoil(recInTemp);
//	            	if(intRtnVal == -1) {
//	                    szMsg = "차량 작업 진행관리 호출처리 중 Error";
//	                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            	}
//	            	//########################################################################################
//	            	szMsg = "▲▲▲▲▲[" + szOperationName + "] [하차]크레인 권상실적 위치가 차량인 경우  END ▲▲▲▲▲";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            	
//	            }
//            }
////??갑자기 외
//            if(getRecord.getFieldString("YD_WBOOK_ID").equals("") || getRecord.getFieldString("YD_WBOOK_ID") == null) {
//                szMsg = "YD_WBOOK_ID  Data Error	: 크레인스케줄 작업예약 ID가 없습니다.";
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                m_ctx.setRollbackOnly();
//                recInTemp = JDTORecordFactory.getInstance().create();
//                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//                return recInTemp;
//	        }
//            
//            
//            
//            
//	        
////조업실적 송신	        
//	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	         * 			열연조업 L3 정정보급완료 실적  - YDHRJ001(보급인 경우는 열연조업L3에만 완료실적을 송신한다.)
//	         * 업무기준 Desc : 1. 코일소재[H] 기준으로 SPM1[HHKE01], HFL[HGFE01], SPM2[HEDEE1], 결속대[HFFE02,HDFE03]
//	         * 					에서 코일제품야드로 출고작업
//	         * 				  2. 코일제품야드 차량하차 스케줄
//	         * 				  3. 재료진도코드가 입고대기(H)인 경우에만 전송
//	         * 스케줄코드 :  1. 코일소재[H] 기준  스케줄코드
//	         * 					SPM1 출고스케줄코드[HHKE01UM]
//	         * 			   		HFL 출고스케줄코드[HGFE01UM]
//	         * 			   		SPM2 출고스케줄코드[HEDE01UM]
//	         * 			                결속대 출고스케줄코드[HDFE03UM, HFFE02UM]
//	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//			
////C증설
//            //권하실적위치, 스케줄코드, 크레인스케줄ID
//	        if(    ( szYD_DN_WR_LOC.startsWith("HBKE04") && szYD_SCH_CD.equals("HBKE01UM") )
//	        	|| ( szYD_DN_WR_LOC.startsWith("HBKD04") && szYD_SCH_CD.equals("HBKD01UM") )
//		        || ( szYD_DN_WR_LOC.startsWith("HBKE04") && szYD_SCH_CD.equals("HBKD02LM") )
//		        
//		        || ( szYD_DN_WR_LOC.startsWith("HAKE05") && szYD_SCH_CD.equals("HAKE01UM") )
//		        || ( szYD_DN_WR_LOC.startsWith("HAKD05") && szYD_SCH_CD.equals("HAKD01UM") )
//		        || ( szYD_DN_WR_LOC.startsWith("HAKE05") && szYD_SCH_CD.equals("HAKD02LM") )
//
//		        || ( szYD_DN_WR_LOC.startsWith("HBFE05") && szYD_SCH_CD.equals("HBFE01UM") )	      
//
//	        	|| ( szYD_DN_WR_LOC.startsWith("HCFE04") && szYD_SCH_CD.equals("HCFE01UM") )
//	        	|| ( szYD_DN_WR_LOC.startsWith("HCFE04") && szYD_SCH_CD.equals("HCFD02LM") )
//
//	        	|| ( szYD_DN_WR_LOC.startsWith("HCKE03") && szYD_SCH_CD.equals("HCKE01UM") )
//	        	|| ( szYD_DN_WR_LOC.startsWith("HCKD03") && szYD_SCH_CD.equals("HCKD01UM") )
//		        || ( szYD_DN_WR_LOC.startsWith("HCKE03") && szYD_SCH_CD.equals("HCKD02LM") )
//
//		        || ( szYD_DN_WR_LOC.startsWith("HDFE03") && szYD_SCH_CD.equals("HDFE01UM") )
//		        || ( szYD_DN_WR_LOC.startsWith("HDCR01") && szYD_SCH_CD.equals("HDYD01MM") ) //크래들롤 추가
//
//	        	|| ( szYD_DN_WR_LOC.startsWith("HEDE01") && szYD_SCH_CD.equals("HEDE01UM") )
//	        	|| ( szYD_DN_WR_LOC.startsWith("HEDD01") && szYD_SCH_CD.equals("HEDD01UM") )
//	         	|| ( szYD_DN_WR_LOC.startsWith("HEDE01") && szYD_SCH_CD.equals("HEDD02LM") )
//	         	
//		        || ( szYD_DN_WR_LOC.startsWith("HFFE02") && szYD_SCH_CD.equals("HFFE01UM") )
//		        || ( szYD_DN_WR_LOC.startsWith("HFCR01") && szYD_SCH_CD.equals("HFYD01MM") ) //크래들롤 추가
//		        
//
//		        || ( szYD_DN_WR_LOC.startsWith("HGFE01") && szYD_SCH_CD.equals("HGFE01UM") )
//	        	|| ( szYD_DN_WR_LOC.startsWith("HGFE01") && szYD_SCH_CD.equals("HGFD02LM") )
//
//	        	|| ( szYD_DN_WR_LOC.startsWith("HHKE01") && szYD_SCH_CD.equals("HHKE01UM") ) 
//	        	|| ( szYD_DN_WR_LOC.startsWith("HHKD01") && szYD_SCH_CD.equals("HHKD01UM") )
//		        || ( szYD_DN_WR_LOC.startsWith("HHKE01") && szYD_SCH_CD.equals("HHKD02LM") )
//		        
////		        151216 hun 지포장 보급완료 추가
////		        || ( szYD_DN_WR_LOC.startsWith("HBGF01") && (szYD_SCH_CD.equals("HBGF01UM") || "TC".equals(szYD_SCH_CD.substring(2 , 4)) ))
////		        || ( szYD_DN_WR_LOC.startsWith("HCGF01") && (szYD_SCH_CD.equals("HCGF01UM") || "TC".equals(szYD_SCH_CD.substring(2 , 4)) ))
////		        || ( szYD_DN_WR_LOC.startsWith("HEGF01") && (szYD_SCH_CD.equals("HEGF01UM") || "TC".equals(szYD_SCH_CD.substring(2 , 4)) ))
////		        || ( szYD_DN_WR_LOC.startsWith("HHGF01") && (szYD_SCH_CD.equals("HHGF01UM") || "TC".equals(szYD_SCH_CD.substring(2 , 4)) ))
//		        
//		        || ( szYD_DN_WR_LOC.startsWith("HBGF01") && (szYD_SCH_CD.startsWith("HBGF01") || "TC".equals(szYD_SCH_CD.substring(2 , 4)) ))
//		        || ( szYD_DN_WR_LOC.startsWith("HCGF01") && (szYD_SCH_CD.startsWith("HCGF01") || "TC".equals(szYD_SCH_CD.substring(2 , 4)) ))
//		        || ( szYD_DN_WR_LOC.startsWith("HEGF01") && (szYD_SCH_CD.startsWith("HEGF01") || "TC".equals(szYD_SCH_CD.substring(2 , 4)) ))
//		        || ( szYD_DN_WR_LOC.startsWith("HHGF01") && (szYD_SCH_CD.startsWith("HHGF01") || "TC".equals(szYD_SCH_CD.substring(2 , 4)) ))
//	        ) {
//	        	szMsg = "▼▼▼▼▼[" + szOperationName + "] 보급인 경우 조업실적 송신시작 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	          //########################################################################################
//	        	recInTemp = JDTORecordFactory.getInstance().create();
//    			recInTemp.setField("MSG_ID",        "YDHRJ001"); 							//열연조업 L3 정정보급완료 실적  전문코드
//    			recInTemp.setField("STL_NO",     	szSTL_NO);								//재료번호
//    			recInTemp.setField("YD_EQP_ID",     szYD_DN_WR_LOC.substring(0,6));			//야드설비id
//    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));			//야드적치베드번호
//    			recInTemp.setField("YD_DN_CMPL_DT", szYD_DN_CMPL_DT);						//야드권하완료일시
//    			recInTemp.setField("TREAT_GP", "1");	
//    			
//    			ydDelegate.sendMsg(recInTemp);
//
//    			szMsg="열연조업 L3 정정보급완료 실적 전송 송신 완료"; 
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				//품질 열연정정입측보급실적----------------------------------------------
//				recInTemp = JDTORecordFactory.getInstance().create();
//				recInTemp.setField("MSG_ID",      	"YDQMJ002"); 							 
//				recInTemp.setField("STL_NO",     	szSTL_NO);	  			//재료번호				
//				ydDelegate.sendMsg(recInTemp);
//
//				szMsg="품질 L3 열연정정입측보급실적 전송 송신 완료5";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//				//-------------------------------------------------------------------
//	        	
//		        if(( szYD_DN_WR_LOC.startsWith("HHKE01") && szYD_SCH_CD.equals("HHKE01UM") ) ||
//		           ( szYD_DN_WR_LOC.startsWith("HHKD01") && szYD_SCH_CD.equals("HHKD01UM") ) ||
//		           ( szYD_DN_WR_LOC.startsWith("HHKE01") && szYD_SCH_CD.equals("HHKD02LM") )  
//				   
//		            ){    	/* 코일소재[H]야드 H동 SPM1 보급 */
//				    recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID",        "YDH2L001");							//전문코드
//	    			recInTemp.setField("STL_NO",     	szSTL_NO);								//재료번호
//	    			recInTemp.setField("YD_EQP_ID",     szYD_DN_WR_LOC.substring(0,6));			//야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));			//야드적치Bed번호 
//	    			ydDelegate.sendMsg(recInTemp);
//	    			
//					szMsg="열연조업 L2  정정 SPM1 보급완료 실적  전송 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
//		        }
//		        		
//		        if(( szYD_DN_WR_LOC.startsWith("HGFE01") && szYD_SCH_CD.equals("HGFE01UM")) ||
//		        		( szYD_DN_WR_LOC.startsWith("HGFE01") && szYD_SCH_CD.equals("HGFD02LM")) 
//			        		){		/* 코일소재[H]야드 G동 HFL 보급 */
//				    recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID",        "YDH2L011");							//전문코드
//	    			recInTemp.setField("STL_NO",     	szSTL_NO);								//재료번호
//	    			recInTemp.setField("YD_EQP_ID",     szYD_DN_WR_LOC.substring(0,6));			//야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));			//야드적치Bed번호 
//	    			ydDelegate.sendMsg(recInTemp);
//	    			
//					szMsg="열연조업 L2  정정 HFL 보급완료 실적  전송 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
//		        }
//		        
//			    if(( szYD_DN_WR_LOC.startsWith("HEDE01") && szYD_SCH_CD.equals("HEDE01UM") )||
//			    		( szYD_DN_WR_LOC.startsWith("HEDD01") && szYD_SCH_CD.equals("HEDD01UM") )||
//			    		( szYD_DN_WR_LOC.startsWith("HEDE01") && szYD_SCH_CD.equals("HEDD02LM") )
//					       ){		/* 코일소재[H]야드 E동 SPM2 보급 */
//				    recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID",        "YDH2L021");							//전문코드
//	    			recInTemp.setField("STL_NO",     	szSTL_NO);								//재료번호
//	    			recInTemp.setField("YD_EQP_ID",     szYD_DN_WR_LOC.substring(0,6));			//야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));			//야드적치Bed번호 
//	    			ydDelegate.sendMsg(recInTemp);
//	    			
//					szMsg="열연조업 L2  정정 SPM2 보급완료 실적  전송 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
//		        }	
////C증설
//		        if(( szYD_DN_WR_LOC.startsWith("HCKE03") && szYD_SCH_CD.equals("HCKE01UM") ) ||
//			       ( szYD_DN_WR_LOC.startsWith("HCKD03") && szYD_SCH_CD.equals("HCKD01UM") ) ||
//			       ( szYD_DN_WR_LOC.startsWith("HCKE03") && szYD_SCH_CD.equals("HCKD02LM") ) 
//					   
//			            ){    	/* 코일소재[H]야드 H동 SPM3 보급 */
//				    recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID",        "YDH2L031");							//전문코드
//	    			recInTemp.setField("STL_NO",     	szSTL_NO);								//재료번호
//	    			recInTemp.setField("YD_EQP_ID",     szYD_DN_WR_LOC.substring(0,6));			//야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));			//야드적치Bed번호 
//	    			ydDelegate.sendMsg(recInTemp);
//	    			
//					szMsg="열연조업 L2  정정 SPM3 보급완료 실적  전송 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
//		        }
//
//			    
//			    if(( szYD_DN_WR_LOC.startsWith("HBKE04") && szYD_SCH_CD.equals("HBKE01UM") ) ||
//		           ( szYD_DN_WR_LOC.startsWith("HBKD04") && szYD_SCH_CD.equals("HBKD01UM") ) ||
//		           ( szYD_DN_WR_LOC.startsWith("HBKE04") && szYD_SCH_CD.equals("HBKD02LM") ) 
//				   
//		            ){    	/* 코일소재[H]야드 H동 SPM4 보급 */
//				    recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID",        "YDH2L041");							//전문코드
//	    			recInTemp.setField("STL_NO",     	szSTL_NO);								//재료번호
//	    			recInTemp.setField("YD_EQP_ID",     szYD_DN_WR_LOC.substring(0,6));			//야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));			//야드적치Bed번호 
//	    			ydDelegate.sendMsg(recInTemp);
//	    			
//					szMsg="열연조업 L2  정정 SPM4 보급완료 실적  전송 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
//				}
//			    
//			    if(( szYD_DN_WR_LOC.startsWith("HAKE05") && szYD_SCH_CD.equals("HAKE01UM") ) ||
//		           ( szYD_DN_WR_LOC.startsWith("HAKD05") && szYD_SCH_CD.equals("HAKD01UM") ) ||
//		           ( szYD_DN_WR_LOC.startsWith("HAKE05") && szYD_SCH_CD.equals("HAKD02LM") ) 
//				   
//		            ){    	/* 코일소재[H]야드 H동 SPM5 보급 */
//				    recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID",        "YDH2L071");							//전문코드
//	    			recInTemp.setField("STL_NO",     	szSTL_NO);								//재료번호
//	    			recInTemp.setField("YD_EQP_ID",     szYD_DN_WR_LOC.substring(0,6));			//야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));			//야드적치Bed번호 
//	    			ydDelegate.sendMsg(recInTemp);
//	    			
//					szMsg="열연조업 L2  정정 SPM5 보급완료 실적  전송 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
//				}
//			    
//		        if(( szYD_DN_WR_LOC.startsWith("HCFE04") && szYD_SCH_CD.equals("HCFE01UM")) ||
//		        		( szYD_DN_WR_LOC.startsWith("HCFE04") && szYD_SCH_CD.equals("HCFD02LM")) 
//			        		){		/* 코일소재[H]야드 G동 HFL 보급 */
//				    recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID",        "YDH2L051");							//전문코드
//	    			recInTemp.setField("STL_NO",     	szSTL_NO);								//재료번호
//	    			recInTemp.setField("YD_EQP_ID",     szYD_DN_WR_LOC.substring(0,6));			//야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));			//야드적치Bed번호 
//	    			ydDelegate.sendMsg(recInTemp);
//	    			
//					szMsg="열연조업 L2  정정 HFL4 보급완료 실적  전송 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
//		        }
//  
//		        //########################################################################################
//		        szMsg = "▲▲▲▲▲[" + szOperationName + "] 보급인 경우 조업실적 송신시  END ▲▲▲▲▲";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	        }
//	        
//	        
//	        
//
//	        szYD_DN_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC");
//	        
//			szMsg = "+++++ 권하 실적 위치 YD_UP_WR_LOC(" + szYD_DN_WR_LOC + ") +++++";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////C증설
//            if(
//               szYD_DN_WR_LOC.startsWith("HBKE04") || 
//               szYD_DN_WR_LOC.startsWith("HAKE05") ||
//               szYD_DN_WR_LOC.startsWith("HBFE05") ||  
//               szYD_DN_WR_LOC.startsWith("HCFE04") || 
//               szYD_DN_WR_LOC.startsWith("HCKE03") || 
//               szYD_DN_WR_LOC.startsWith("HEDE01") ||
//               szYD_DN_WR_LOC.startsWith("HGFE01") || 
//               szYD_DN_WR_LOC.startsWith("HHKE01")  
//                ){
//            	
//            	szMsg = "▼▼▼▼▼[" + szOperationName + "] 보급완료/TAKE-IN 실적 송신시작 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            //########################################################################################
//            	if((szYD_SCH_CD.equals("HBFE03UM"))||
//               	   (szYD_SCH_CD.equals("HBKE03UM"))||
//               	   (szYD_SCH_CD.equals("HAKE03UM"))||
//            	   (szYD_SCH_CD.equals("HCKE03UM"))||
//            	   (szYD_SCH_CD.equals("HCFE03UM"))||
//            	   (szYD_SCH_CD.equals("HEDE03UM"))||
//            	   (szYD_SCH_CD.equals("HGFE03UM"))||
//                   (szYD_SCH_CD.equals("HHKE03UM"))
//            	   ){
//		        	recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID",        "YDHRJ001"); 							//열연조업 L3 정정보급완료 실적  전문코드
//	    			recInTemp.setField("STL_NO",     	szSTL_NO);								//재료번호
//	    			recInTemp.setField("YD_EQP_ID",     szYD_DN_WR_LOC.substring(0,6));			//야드설비id
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));			//야드적치베드번호
//	    			recInTemp.setField("YD_DN_CMPL_DT", szYD_DN_CMPL_DT);						//야드권하완료일시
//	    			recInTemp.setField("TREAT_GP", "5");	
//	    			ydDelegate.sendMsg(recInTemp);
//	
//	    			szMsg="열연조업 L3 Take-In실적 전문 전송 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					
//					//품질 열연정정입측보급실적----------------------------------------------
//					recInTemp = JDTORecordFactory.getInstance().create();
//					recInTemp.setField("MSG_ID",      	"YDQMJ002"); 							 
//					recInTemp.setField("STL_NO",     	szSTL_NO);	  			//재료번호				
//					ydDelegate.sendMsg(recInTemp);
//
//					szMsg="품질 L3 열연정정입측보급실적 전송 송신 완료6";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//					//-------------------------------------------------------------------
//            	}	
//        	// YDH2L011 전문
//	        	recInTemp = JDTORecordFactory.getInstance().create();
//	        	
//	        	if( szYD_DN_WR_LOC.startsWith("HHKE01") && szYD_SCH_CD.equals("HHKE03UM") ){
//	        		recInTemp.setField("MSG_ID"       , "YDH2L001");						// SPM1 전문코드
//	    			recInTemp.setField("YD_EQP_ID"    , szYD_DN_WR_LOC.substring(0,6));		// 야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//	    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//	    			ydDelegate.sendMsg(recInTemp);
//					szMsg = "C열연 SPM1 정정Take-In실적 전문(YDH2L001) 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		        		
//	        	}
//	        	if( szYD_DN_WR_LOC.startsWith("HGFE01") && szYD_SCH_CD.equals("HGFE03UM") ){
//	        		recInTemp.setField("MSG_ID"       , "YDH2L011");						// HFL 전문코드
//	    			recInTemp.setField("YD_EQP_ID"    , szYD_DN_WR_LOC.substring(0,6));		// 야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//	    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//	    			ydDelegate.sendMsg(recInTemp);
//					szMsg = "C열연 HFL 정정Take-In실적 전문(YDH2L011) 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		        		
//	        	}
//	        	if( szYD_DN_WR_LOC.startsWith("HEDE01") && szYD_SCH_CD.equals("HEDE03UM") ){
//	        		recInTemp.setField("MSG_ID"       , "YDH2L021");						// SPM2 전문코드
//	    			recInTemp.setField("YD_EQP_ID"    , szYD_DN_WR_LOC.substring(0,6));		// 야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//	    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//	    			ydDelegate.sendMsg(recInTemp);
//					szMsg = "C열연 SPM2 정정Take-In실적 전문(YDH2L021) 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		        		
//	        	}
////C증설	        	
//	        	if( szYD_DN_WR_LOC.startsWith("HCKE03") && szYD_SCH_CD.equals("HCKE03UM") ){
//	        		recInTemp.setField("MSG_ID"       , "YDH2L031");						// SPM1 전문코드
//	    			recInTemp.setField("YD_EQP_ID"    , szYD_DN_WR_LOC.substring(0,6));		// 야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//	    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//	    			ydDelegate.sendMsg(recInTemp);
//					szMsg = "C열연 SPM1 정정Take-In실적 전문(YDH2L031) 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		        		
//	        	}
//	        	if( szYD_DN_WR_LOC.startsWith("HBKE04") && szYD_SCH_CD.equals("HBKE03UM") ){
//	        		recInTemp.setField("MSG_ID"       , "YDH2L041");						// SPM4 전문코드
//	    			recInTemp.setField("YD_EQP_ID"    , szYD_DN_WR_LOC.substring(0,6));		// 야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//	    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//	    			ydDelegate.sendMsg(recInTemp);
//					szMsg = "C열연 SPM4 정정Take-In실적 전문(YDH2L041) 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		        		
//	        	}
//	        	if( szYD_DN_WR_LOC.startsWith("HAKE05") && szYD_SCH_CD.equals("HAKE03UM") ){
//	        		recInTemp.setField("MSG_ID"       , "YDH2L071");						// SPM5 전문코드
//	    			recInTemp.setField("YD_EQP_ID"    , szYD_DN_WR_LOC.substring(0,6));		// 야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//	    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//	    			ydDelegate.sendMsg(recInTemp);
//					szMsg = "C열연 SPM5 정정Take-In실적 전문(YDH2L071) 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		        		
//	        	}
//	        	if( szYD_DN_WR_LOC.startsWith("HCFE04") && szYD_SCH_CD.equals("HCFE03UM") ){
//	        		recInTemp.setField("MSG_ID"       , "YDH2L051");						// HFL 전문코드
//	    			recInTemp.setField("YD_EQP_ID"    , szYD_DN_WR_LOC.substring(0,6));		// 야드설비ID
//	    			recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//	    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//	    			ydDelegate.sendMsg(recInTemp);
//					szMsg = "C열연 HFL4 정정Take-In실적 전문(YDH2L051) 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		        		
//	        	}
//	        	
//	        	//########################################################################################
//	        	szMsg = "▲▲▲▲▲[" + szOperationName + "] 보급완료/TAKE-IN 실적 송신시작  END ▲▲▲▲▲";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            }
//            
//            
//            
//            
////출하실적송신	
//            
//	        //현진도코드가 입고대기(H)이고 야드가 제품야드(J)인 경우  강관입고대기 추가
//            if(szYdGp.equals("J") && (szCurrProgCd.equals("H") || szCurrProgCd.equals("2")) ){
//            	szMsg = "▼▼▼▼▼[" + szOperationName + "]  출하관리  코일입고작업실적 송신 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            //########################################################################################
// 
//
//            	recInTemp = JDTORecordFactory.getInstance().create();
//	        	
//    			recInTemp.setField("MSG_ID",        "YDDMR001");									//출하관리 코일제품입고작업실적전송:전문코드
//    			recInTemp.setField("YD_GP",     	szYD_DN_WR_LOC.substring(0, 1));				//입고야드구분
//    			recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);									//크레인스케줄ID
//    			
//    			ydDelegate.sendMsg(recInTemp);
//    			
//    			//########################################################################################
//		        szMsg = "▲▲▲▲▲[" + szOperationName + "]  출하관리  코일입고작업실적 송신 END ▲▲▲▲▲";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	        }
//
//            
//            
//	        
////출하관리 코일제품이적작업실적 전송  - YDDMR004
//	        if( (szYD_SCH_CD.substring(0, 1).equals("J"))) {			/* 일반야드 이적 */
//	        	if( (szYD_SCH_CD.substring(2, 4).equals("YD") && szYD_SCH_CD.substring(6).equals("MM") )				/* 일반야드 이적 */
//	        		|| ( szYD_SCH_CD.substring(2, 4).equals("TC") && szYD_SCH_CD.substring(6).equals("MM") ) 
//	        		|| ( szYD_SCH_CD.substring(2, 4).equals("TC") && szYD_SCH_CD.substring(6).equals("LM") ) ) {	/* 동간이적 대차 하차*/
//	        		szMsg = "▼▼▼▼▼[" + szOperationName + "]  출하관리 코일제품이적작업실적 전송 START ▼▼▼▼▼";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//		            //########################################################################################
//	        		
//		       	   	recInTemp = JDTORecordFactory.getInstance().create();		        	
//	    			recInTemp.setField("MSG_ID",        "YDDMR004");									//출하관리 코일제품이적작업실적 전문코드
//	    			recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);									//크레인스케줄ID
//	    			
//	    			ydDelegate.sendMsg(recInTemp);
//	    			
//	    			//########################################################################################
//			        szMsg = "▲▲▲▲▲[" + szOperationName + "]  출하관리 코일제품이적작업실적 전송 END ▲▲▲▲▲";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//		        }
//	        }
//	        
//	        
//	        
//	        
//
////HYSCO재 이적 (소재창고에서 나감)	        
//	        if((szYdGp.equals("H")) && (szHyscoGp.equals("T"))){			
//	        	szMsg = "▼▼▼▼▼[" + szOperationName + "]  하이스코 이송재 이적실적 출하관리 송신 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            //########################################################################################
// 
//	        	recInTemp = JDTORecordFactory.getInstance().create();
//    			recInTemp.setField("MSG_ID",        "YDDMR004");									
//    			recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);									
//    			
//    			ydDelegate.sendMsg(recInTemp);
// 
//    			
//				//########################################################################################
//		        szMsg = "▲▲▲▲▲[" + szOperationName + "]  하이스코 이송재 이적실적 출하관리 송신 END ▲▲▲▲▲";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	        }
//	        
//	         
//	        
////크레인스케줄 삭제처리
//	        szMsg = "▼▼▼▼▼[" + szOperationName + "]  크레인스케줄 삭제처리 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//            
//	        setRecord = JDTORecordFactory.getInstance().create();
//	        setRecord.setField("YD_CRN_SCH_ID",	getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
//	        setRecord.setField("DEL_YN",      	"Y");
//	        /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch*/
//	        intRtnVal = this.Y5UpdYdCrnschCoil(setRecord, 0);
//	        switch (intRtnVal) {
//	        	case 0	:
//	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -1	:
//	                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -2	:
//	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -3	:
//	                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        }
//	        //########################################################################################
//	        szMsg = "▲▲▲▲▲[" + szOperationName + "]  크레인스케줄 삭제처리 END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//
//	        
//	        
//	        
////작업예약삭제	        
//	        szMsg = "▼▼▼▼▼[" + szOperationName + "]  작업예약 종료 처리 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//	        //작업예약완료 CHECK
//	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//	        /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschCNT*/
//			intRtnVal = this.Y5GetYdCrnschCoil(getRecord, getRecSet, 4);
//	        switch (intRtnVal) {
//	        	case 0	:
//	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        	case -2	:
//	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//	                m_ctx.setRollbackOnly();
//	                recInTemp = JDTORecordFactory.getInstance().create();
//	                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//	                return recInTemp;
//	        }
//	        
//			getRecSet.first();
//	        //레코드셋의 사이즈값으로 ErrorCheck
//	        if(getRecSet.size() == 0){
//	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                m_ctx.setRollbackOnly();
//                recInTemp = JDTORecordFactory.getInstance().create();
//                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//                return recInTemp;
//	        }
//	        outRecord = getRecSet.getRecord();
//	        
//	        int schcnt = outRecord.getFieldInt("SCH_CNT");
//	        int endcnt = outRecord.getFieldInt("END_CNT");
//	
//	        szMsg = "▼▼▼▼▼[" + szOperationName + "]  스케줄 완료 여부 (스케줄총카운트)=(처리완료여부)"+schcnt+"="+endcnt+"같은경우 작업예약 삭제처리";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            
//	        if (schcnt == endcnt) {
//	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId) ;
//	            bookrecord.setField("DEL_YN",             "Y") ;
//	            bookrecord.setField("YD_SCH_PROG_STAT",   "E") ;
//	            /*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbook*/
//	            intRtnVal = this.Y5UpdYdWrkbookCoil(bookrecord, 0);
//
//	        }
//	        
//         
//	      
////작업예약재료 삭제
//			recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("STL_NO",      szSTL_NO);
//			recInTemp.setField("DEL_YN",      "Y");
//			recInTemp.setField("MODIFIER",    "SYSTEM");
//			recInTemp.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID"));
//			
//			rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("");
//			
//			szMsg = "작업예약재료 존재 유무";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//			
//			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkBookMtl, 0);			
//			if(intRtnVal>0){
//				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtlDelete2(recInTemp);
//			}
//			//########################################################################################
//	        szMsg = "▲▲▲▲▲[" + szOperationName + "]  작업예약 종료 처리 END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            
//            
////크레인작업실적응답[YDY5L005] 전송	        
//	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	         * 			C열연 크레인작업실적응답 전송  - YDY5L005
//	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	        recInTemp = JDTORecordFactory.getInstance().create(); 
//	        recInTemp.setField("MSG_ID"        		, "YDY5L005");
//	        recInTemp.setField("YD_EQP_ID"     		, szYD_EQP_ID);										//야드설비ID
//	        recInTemp.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_DN_CMPL);					//야드작업진행상태
//	        recInTemp.setField("YD_SCH_CD"   		, szYD_SCH_CD);										//야드스케줄코드
//	        recInTemp.setField("YD_CRN_SCH_ID"   	, szCrnSchId);										//야드크레인스케줄ID
//
//	        //==================================================================================
//	        // 수신전문의 진행상태가 강제권하(5)일시 응답으로는 강제권하값(F)로 내려보내야 됨
//	        //==================================================================================	        
//	        if(szYD_WRK_PROG_STAT.equals("4")){
//		        recInTemp.setField("YD_L2_WR_GP" 	, YdConstant.CRN_WRK_RE_DN_WR);							//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//	        } else {
//	        	recInTemp.setField("YD_L2_WR_GP"    , YdConstant.CRN_WRK_RE_FRCE_DN);							//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하	        	
//	        }	        
//
//	        recInTemp.setField("YD_L3_HD_RS_CD"		, YdConstant.CRN_WRK_RE_CD_NORMAL_HD);					//야드L3처리결과코드
//			ydDelegate.sendMsg(recInTemp);
//			
//			
//			szMsg = "[C열연코일권하실적처리]크레인작업실적응답[YDY5L005] 전송 완료";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//
//            
//            
//            
////대차이동 실적 송신
//            szMsg = "[C열연코일권하실적처리]대차 이동 여부 :" + sTCAR_MOVE_SND ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//            if(sTCAR_MOVE_SND.equals("Y")){
//            	
//	    		recInTemp = JDTORecordFactory.getInstance().create();
//	    		recInTemp.setField("MSG_ID"			, "YDY5L006");
//	    		recInTemp.setField("YD_GP"			, StringHelper.evl(outRecord1.getFieldString("YD_GP"), "0"));
//	    		recInTemp.setField("YD_SCH_CD"		, StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "0"));
//	    		recInTemp.setField("YD_TCAR_SCH_ID"	, StringHelper.evl(outRecord1.getFieldString("YD_TCAR_SCH_ID"), "0"));
//	    		recInTemp.setField("YD_AIM_BAY_GP"	, StringHelper.evl(outRecord1.getFieldString("YD_AIM_BAY_GP"), "0"));
//	    		ydDelegate.sendMsg(recInTemp);
//	    		
//				szMsg="[C열연코일권하실적처리] 대차 출발지시 전송 완료[2]";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//            	
//            }
//           
//            szMsg = "[C열연코일권하실적처리]반납확정구분 :" + szYD_SCH_CD+"-"+szCurrProgCd+"-"+ szYdGp ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//      
//            
//            
////반납실적정보 전송 
//            if( //제품야드로 반품 후 소재야드로 반납 받는 경우
//               ((szYD_SCH_CD.equals("HAYD03UM")||
//               	szYD_SCH_CD.equals("HBYD03UM")||
//            	szYD_SCH_CD.equals("HBYD53UM")||
//           		szYD_SCH_CD.equals("HCYD03UM")||
//           		szYD_SCH_CD.equals("HCYD53UM")||
//           		szYD_SCH_CD.equals("HDYD03UM")||
//           		szYD_SCH_CD.equals("HEYD03UM")||
//        		szYD_SCH_CD.equals("HFYD03UM")||
//        		szYD_SCH_CD.equals("HGYD03UM")||
//        		szYD_SCH_CD.equals("HHYD03UM")||
//        		szYD_SCH_CD.substring(6, 8).equals("UJ")
//        		)&& ( szCurrProgCd.equals("J")|| szCurrProgCd.equals("5")) && szYdGp.equals("H") && szYD_AID_WRK_YN.equals("N")
//        		)
//        		||
//        		( //소재야드에서 직접 반품을 받는 경우
//        		 (szYD_SCH_CD.equals("HAPT04LM")||
//        		 szYD_SCH_CD.equals("HBPT04LM")||
//        		 szYD_SCH_CD.equals("HCPT04LM")||
//        		 szYD_SCH_CD.equals("HDPT04LM")||
//        		 szYD_SCH_CD.equals("HEPT04LM")||
//        		 szYD_SCH_CD.equals("HFPT04LM")||
//        		 szYD_SCH_CD.equals("HGPT04LM")||
//        		 szYD_SCH_CD.equals("HHPT04LM") 
//        		 )&& szYdGp.equals("H")
//        		)           
//        		||
//        		( //소재야드에서 차량동간이적으로 직접 반품을 받는 경우
//        		 ("TR1".equals(szYD_SCH_CD.substring(2 , 5)))
//        		 && szYdGp.equals("H") 
//        		 && (szCurrProgCd.equals("J")|| szCurrProgCd.equals("5"))
//        		)  ){
//            	
//            	szMsg = "▼▼▼▼▼[" + szOperationName + "]  반납실적정보 송신 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            //########################################################################################
//	            
//		        recInTemp = JDTORecordFactory.getInstance().create();
//	        	
//    			recInTemp.setField("MSG_ID",        "YDDMR034");									//반납확정정보:전문코드
//    			recInTemp.setField("STL_NO",     	szSTL_NO);				//제품번호
//    			
//    			ydDelegate.sendMsg(recInTemp);
//    			
//				szMsg="출하관리 코일반납작업실적전송 송신 완료";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				szMsg = "▼▼▼▼▼[" + szOperationName + "]  반납실적정보 송신 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            //########################################################################################
//	        }
//            
//            
//            
////반납 시 조업 메시지 전송
//            if( //소재야드에서 직접 반품을 받는 경우
//           		 ((szYD_SCH_CD.equals("HAPT04LM")||
//                		 szYD_SCH_CD.equals("HBPT04LM")||
//                		 szYD_SCH_CD.equals("HCPT04LM")||
//                		 szYD_SCH_CD.equals("HDPT04LM")||
//                		 szYD_SCH_CD.equals("HEPT04LM")||
//                		 szYD_SCH_CD.equals("HFPT04LM")||
//                		 szYD_SCH_CD.equals("HGPT04LM")||
//                		 szYD_SCH_CD.equals("HHPT04LM") 
//                		 )&& szYdGp.equals("H"))
//	                ||
//	        		( //소재야드에서 차량동간이적으로 직접 반품을 받는 경우
//	        		 ("TR1".equals(szYD_SCH_CD.substring(2 , 5)))
//	        		 && szYdGp.equals("H") 
//	        		 && (szCurrProgCd.equals("J")|| szCurrProgCd.equals("5"))
//	        		)
//                		) {
//            	szMsg = "▼▼▼▼▼[" + szOperationName + "]  반납메시지 조업 등록 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            //########################################################################################
//	            
//	            JDTORecord recPara  = JDTORecordFactory.getInstance().create();
//				recPara.setField("V_STL_NO"      	, szSTL_NO); 
//				
//				CoilGdsJspDao dao2 = new CoilGdsJspDao();
//				/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.insHrShrMsgLog*/
//				intRtnVal = dao2.insHrShrMsgLog(recPara);
//				 
//				szMsg = "▼▼▼▼▼[" + szOperationName + "]  반납메시지 조업 등록 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            //########################################################################################
//            	
//            }
//
//            
//          //반송대상재 정보 초기화
//	        if( szYD_DN_WR_LOC.substring(0,1).equals("H") &&(
//					(szYD_SCH_CD.equals("HDYD04UM")) ||
//					(szYD_SCH_CD.equals("HEYD04UM")) ||
//					(szYD_SCH_CD.equals("HFYD04UM")) ||
//					(szYD_SCH_CD.equals("HGYD04UM")) ||
//					(szYD_SCH_CD.equals("HHYD04UM")))
//				||
//	        		( //소재야드에서 차량동간이적으로 직접 반품을 받는 경우
//	        		 ("TR1".equals(szYD_SCH_CD.substring(2 , 5)))
//	        		 && szYdGp.equals("H") 
//	        		 && (szCurrProgCd.equals("J")|| szCurrProgCd.equals("5"))
//	        		)		
//		        ) {
//		        	szMsg = "▼▼▼▼▼[" + szOperationName + "]  반송대상재 반납사유 정보 초기화 START ▼▼▼▼▼";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//		            //########################################################################################
//	            
//					JDTORecord recPara  = JDTORecordFactory.getInstance().create();
//					recPara.setField("V_STL_NO"      	, szSTL_NO);
//					recPara.setField("V_SNDBK_RSN_CD"   , "");
//					recPara.setField("V_SNDBK_REGISTER" , "");
//					recPara.setField("V_SNDBK_GP"      	, "");
//					recPara.setField("V_YD_AIM_BAY_GP" 	, "");	
//					
//					CoilGdsJspDao dao2 = new CoilGdsJspDao();
//					/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdRetMgt*/
//					intRtnVal = dao2.updCoilYdRetMgt(recPara);
//					
//					//########################################################################################
//			        szMsg = "▲▲▲▲▲[" + szOperationName + "]  반납대상재 정보 초기화 END ▲▲▲▲▲";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//				}
//            
//            
////진행에 송신 :sndJMSInfo	
//			if( szYD_DN_WR_LOC.substring(0,1).equals("H") &&
//					(	szCurrProgCd.equals("J")||szCurrProgCd.equals("5")|| //2016.11.10 5추가
//						szCurrProgCd.equals("B")||
//						szCurrProgCd.equals("F")||
//						szCurrProgCd.equals("G")||
//						szCurrProgCd.equals("H")||
//						szCurrProgCd.equals("Y")
//					)&&
//				((szYD_SCH_CD.equals("HAYD03UM")) ||
//						(szYD_SCH_CD.equals("HBYD03UM")) ||
//						(szYD_SCH_CD.equals("HBYD53UM")) ||
//						(szYD_SCH_CD.equals("HCYD03UM")) ||
//						(szYD_SCH_CD.equals("HCYD53UM")) ||
//						(szYD_SCH_CD.equals("HEYD03UM")) ||
//						(szYD_SCH_CD.equals("HFYD03UM")) ||
//						(szYD_SCH_CD.equals("HGYD03UM")) ||
//						(szYD_SCH_CD.equals("HHYD03UM")) ||
//						(szYD_SCH_CD.substring(6, 8).equals("UJ")) ||
//						(szYD_SCH_CD.equals("HAYD04UM")) ||
//						(szYD_SCH_CD.equals("HBYD04UM")) ||
//						(szYD_SCH_CD.equals("HBYD54UM")) ||
//						(szYD_SCH_CD.equals("HCYD04UM")) ||
//						(szYD_SCH_CD.equals("HCYD54UM")) ||
//						(szYD_SCH_CD.equals("HDYD04UM")) ||
//						(szYD_SCH_CD.equals("HEYD04UM")) ||
//						(szYD_SCH_CD.equals("HFYD04UM")) ||
//						(szYD_SCH_CD.equals("HGYD04UM")) ||
//						(szYD_SCH_CD.equals("HHYD04UM")) ||
//						(szYD_SCH_CD.substring(6, 8).equals("UB")) 
//				 )) {
//				szMsg = "▼▼▼▼▼[" + szOperationName + "]  진행관리 실적전송 송신 START ▼▼▼▼▼";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//	            //########################################################################################
//	 
//				
//				getRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");
//				inRecord2 = JDTORecordFactory.getInstance().create();			
//				inRecord2.setField("STL_NO", szSTL_NO);
//				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarMtlCoilComm*/
//				intRtnVal = ydCarSchDao.getYdCarsch(inRecord2, getRecSet1, 305);	
//				if(intRtnVal < 0) {
//					szMsg = "COIL공통 테이블 조회오류  [Ret : " + intRtnVal + "]";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//				} else if(intRtnVal == 0) {
//					szMsg = "COIL공통 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//				} else{				
//				
//					getRecSet1.first();
//					recGetVal = getRecSet1.getRecord(0);	
//					
//					recInTemp 	= JDTORecordFactory.getInstance().create();
//		
//					recInTemp.setField("JMS_TC_CD"				, "YDPTJ002");
//					recInTemp.setField("JMS_TC_CREATE_DDTT"		, YdUtils.getCurDate("yyyyMMddHHmmss"));				
//					recInTemp.setField("STL_NO"					, szSTL_NO.trim()); // 재료번호
//					recInTemp.setField("ORD_NO"					, ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO")); // 주문번호
//					recInTemp.setField("ORD_DTL"				, ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL"));  // 주문행번
//					recInTemp.setField("PLNT_PROC_CD"			, ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD")); // 공장공정코드
//					recInTemp.setField("STL_APPEAR_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));  // 재료외형구분
//					recInTemp.setField("CURR_PROG_CD"			, ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD"));   // 현재진도코드
//					recInTemp.setField("ORD_YEOJAE_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"));  // 주문여재구분
//					recInTemp.setField("STL_WT"					, ydDaoUtils.paraRecChkNull(recGetVal, "COIL_WT"));   // 재료중량 (COIL중량) 
//					recInTemp.setField("DS_MTL_WT"				, "");		// 설계재료중량
//					recInTemp.setField("MTL_STAT_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "MTL_STAT_GP")); // 재료상태구분
//					recInTemp.setField("RECORD_END_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_END_GP")); // Record 종료구분
//					recInTemp.setField("RECORD_END_GP1"			, "");
//					recInTemp.setField("BEFO_PROG_CD"			, ydDaoUtils.paraRecChkNull(recGetVal, "BEFO_PROG_CD")); // 전진도 코드
//					recInTemp.setField("BEF_ORD_NO"				, ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_NO"));	// 전주문 번호
//					recInTemp.setField("BEF_ORD_DTL"			, ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_DTL"));	// 전주문 행번
//					recInTemp.setField("MMATL_FEE_NO"			, ydDaoUtils.paraRecChkNull(recGetVal, "MMATL_FEE_NO"));	// 모재료번호   
//					recInTemp.setField("ORDERTRANS_MATCH_GP"	, ydDaoUtils.paraRecChkNull(recGetVal, "MATCH_ORDERTRANS_GP"));	// 목전충당구분
//					
//					this.sndJMSInfo(recInTemp);
//				
//				}
//				
//				//########################################################################################
//				szMsg = "▲▲▲▲▲[" + szOperationName + "]  진행관리 실적전송 송신 END ▲▲▲▲▲";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);            
//			}
//			
//			
////수입 시 조업에 TC 전송 
//			szMsg = "▼▼▼▼▼[" + szOperationName + "]  수입 시 조업 TC전송 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//			if(szYD_SCH_CD.equals("HACV01LM") ||
//				szYD_SCH_CD.equals("HBCV01LM") ||
//				szYD_SCH_CD.equals("HCCV01LM") ||
//				szYD_SCH_CD.equals("HDCV01LM") ||
//				szYD_SCH_CD.equals("HECV01LM") ||
//				szYD_SCH_CD.equals("HFCV01LM") ||
//				szYD_SCH_CD.equals("HGCV01LM") ||
//				szYD_SCH_CD.equals("HHCV01LM")){
//				
//				JDTORecord FrtoendRecord = null;
//		    	FrtoendRecord = JDTORecordFactory.getInstance().create(); 
//		    	FrtoendRecord.setField("JMS_TC_CD", "YDHRJ007");
//		    	FrtoendRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));												    					    					    	
//		    	FrtoendRecord.setField("STL_NO", StringHelper.evl(szSTL_NO, "").trim());// 재료번호	 
//		    	EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
//		    	
//		    	ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class}, new Object[]{FrtoendRecord});
//			 
//				
//			}
//            
//            //########################################################################################
//			szMsg = "▲▲▲▲▲[" + szOperationName + "]  수입 시 조업 TC전송  END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            
//            
//            
//			szMsg = "▼▼▼▼▼[" + szOperationName + "]  결로재추출 시 조업 TC전송 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//			if(szYD_SCH_CD.equals("HEHC01UM") ||
//				szYD_SCH_CD.equals("HFHC01UM") ){
//				
//				JDTORecord FrtoendRecord = null;
//		    	FrtoendRecord = JDTORecordFactory.getInstance().create(); 
//		    	FrtoendRecord.setField("JMS_TC_CD", "YDHRJ008");
//		    	FrtoendRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));												    					    					    	
//		    	FrtoendRecord.setField("STL_NO", StringHelper.evl(szSTL_NO, "").trim());// 재료번호	 
//		    	EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
//		    	
//		    	ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class}, new Object[]{FrtoendRecord});			 
//				
//			}            
//            //########################################################################################
//			szMsg = "▲▲▲▲▲[" + szOperationName + "]  결로재추출 시 조업 TC전송  END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            
//			
//            
//            
////설비상태 권하로 변경
//			szMsg = "▼▼▼▼▼[" + szOperationName + "]  설비상태 권하로 변경 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//            recInTemp = JDTORecordFactory.getInstance().create();
//	        recInTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);
//	        recInTemp.setField("YD_EQP_STAT"	, "4");
//            intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
//			if(intRtnVal <= 0) {
//				if(intRtnVal == 0) {
//					szMsg="[" + szOperationName + "] data not found";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//				}else if(intRtnVal == -2) {
//					szMsg="[" + szOperationName + "] parameter error";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				}
//                m_ctx.setRollbackOnly();
//                recInTemp = JDTORecordFactory.getInstance().create();
//                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//                return recInTemp;
//			}
//			//########################################################################################
//			szMsg = "▲▲▲▲▲[" + szOperationName + "]  설비상태 권하로 변경 END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//			
//			
//			
//			
////이력테이블등록호출			
//			szMsg = "▼▼▼▼▼[" + szOperationName + "]  이력테이블등록 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            //########################################################################################
//			recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("MSG_ID",             "");
//			recInTemp.setField("YD_WBOOK_ID",        szYdWbookId);
//			recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId);
//			recInTemp.setField("YD_CAR_SCH_ID",      szYD_CAR_SCH_ID);
//			recInTemp.setField("YD_TCAR_SCH_ID",     szYD_TCAR_SCH_ID);
//			recInTemp.setField("YD_WTCL_TNK_SCH_ID", "");
//			szMsg="이력테이블등록호출";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			ydUtils.displayRecord(szOperationName, recInTemp);
//			
//			CoilCrnSchSeEJBBean crnSchSeEJBBean = new CoilCrnSchSeEJBBean();
//			crnSchSeEJBBean.procWorkHistoryCreate(recInTemp);
// 
//			//########################################################################################
//			szMsg = "▲▲▲▲▲[" + szOperationName + "]  이력테이블등록 END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            
//  
//			
//			
////차량 동간이적 작업 	            
//            szMsg = "▼▼▼▼▼[" + szOperationName + "] 차량 동간이적 작업 START ▼▼▼▼▼";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            
//			//########################################################################################
//			//CHITO 차량동간이적 작업 
//			// 상차작업 인 경우(스케줄 코드로 구분)			
//			// 1. 하차지 차량  작업예약을 생성 한다.
//			// 2. 상차매수를 체크 해서 하차지 작업예약ID로 하차지 스케줄을 호출 한다.  			
//			// 하차작업 인 경우	
//			// 1. 하차매수를 체크 해서 상차지 작업예약ID로 상차지 스케줄을 호출 한다.
//			
//			JDTORecord inRecord = JDTORecordFactory.getInstance().create();
//			inRecord.setField("YD_SCH_CD",           	szYD_SCH_CD);
//			inRecord.setField("STL_NO",           		szSTL_NO);
//			inRecord.setField("YD_USER_ID",           	"trmovesch");
//			inRecord.setField("YD_DN_WR_LOC",           szYD_DN_WR_LOC);
//			inRecord.setField("YD_WBOOK_ID",            szYdWbookId);
//
//
//			//------------------------------------------------------------------------------------------------------------
//			//	신 차량이적 적용
//			//------------------------------------------------------------------------------------------------------------
//			JDTORecordSet 	outResult9  = JDTORecordFactory.getInstance().createRecordSet("");
//			JDTORecord 		inRecord9 	= JDTORecordFactory.getInstance().create();
//			JDTORecord 		outRecord8  = JDTORecordFactory.getInstance().create();
//			String szAPPLY_YN9  = "N";
//			inRecord9.setField("REPR_CD_GP", "H00020");
//			
//			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
//			intRtnVal = ydEqpDao.getYdEqp(inRecord9, outResult9, 999);
//			if(intRtnVal > 0) {
//				outResult9.first();
//				outRecord8  = outResult9.getRecord();
//				szAPPLY_YN9 = outRecord8.getFieldString("ITEM1");				
//			}
//			ydUtils.putLog(szSessionName, szMethodName, "신 차량이적 처리 적용여부 " + szAPPLY_YN9, YdConstant.DEBUG);
//			
//			if(szAPPLY_YN9.equals("Y")){
//				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
//				outRecord = (JDTORecord)ejbConn.trx("traillerMoveSchNew", new Class[] { JDTORecord.class }, new Object[] { inRecord });
//			} else{
//				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
//				outRecord = (JDTORecord)ejbConn.trx("traillerMoveSch", new Class[] { JDTORecord.class }, new Object[] { inRecord });
//			}
//			sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
//			
//			//########################################################################################
//			szMsg = "▲▲▲▲▲[" + szOperationName + "] 차량 동간이적 작업 END ▲▲▲▲▲";
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            
//			 
//			
////공냉재 입고 등록 처리 
////			szMsg = "▼▼▼▼▼[" + szOperationName + "]  공냉재 입고 등록 처리 START ▼▼▼▼▼";
////            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
////            //########################################################################################
////			if(("HAYD03MM".equals(szYD_SCH_CD) ||
////				"HBYD03MM".equals(szYD_SCH_CD) ||
////				"HCYD03MM".equals(szYD_SCH_CD) ||
////				"HDYD03MM".equals(szYD_SCH_CD) ||
////				"HEYD03MM".equals(szYD_SCH_CD) ||
////				"HFYD03MM".equals(szYD_SCH_CD) ||
////				"HGYD03MM".equals(szYD_SCH_CD) ||
////				"HHYD03MM".equals(szYD_SCH_CD))
////			  &&  "N".equals(szYD_AID_WRK_YN)  // N: 주작업 , Y: 보조작업			   
////			){
////			 
////				JDTORecord FrtoendRecord = null;
////		    	FrtoendRecord = JDTORecordFactory.getInstance().create(); 
////		    	FrtoendRecord.setField("YD_USER_ID", "system");
////		    	FrtoendRecord.setField("STL_NO", StringHelper.evl(szSTL_NO, "").trim());// 재료번호											    					    					    	
////		    	FrtoendRecord.setField("YD_AIM_BAY_GP", szYD_SCH_CD.substring(1 , 2) );// 목적동	
////		    	 
////				
////		    	EJBConnector ejbConn = new EJBConnector("default","CoilGdsJspFaEJB",this);		    	
////		    	ejbConn.trx("updCoilYdSendAirCl",new Class[]{JDTORecord.class}, new Object[]{FrtoendRecord});			 
////				
////			}
////            
////            //########################################################################################
////			szMsg = "▲▲▲▲▲[" + szOperationName + "]  공냉재 입고 등록 처리  END ▲▲▲▲▲";
////            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);			
////            
////            
//////Flex 권하 완료 실적 전송
////            szMsg = "▼▼▼▼▼[" + szOperationName + "] Flex 권하 완료 실적 전송 START ▼▼▼▼▼";
////            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
////            //########################################################################################
////			//------------------------------------------------------------------
////	        // 권하 실적시 Flex 실시간 처리
////	        //------------------------------------------------------------------
////	        JDTORecord    recFlex   	= JDTORecordFactory.getInstance().create();
////            if(szYD_SCH_CD.substring(0,1).equals("H")){
////            	recFlex.setField("YD_GP"	, YdConstant.YD_GP_C_HR_COIL_MATL_YARD);
////            }else {
////            	recFlex.setField("YD_GP"	, YdConstant.YD_GP_C_HR_COIL_GDS_YARD);
////            }
////		 	recFlex.setField("YD_EQP_ID"	, szYD_EQP_ID);
////		 	recFlex.setField("YD_UP_WR_LOC"	, szYD_UP_WR_LOC);
////		 	recFlex.setField("YD_DN_WR_LOC"	, szYD_DN_WR_LOC);
////    		ydUtils.putYdFlexCrnWrk("", recFlex);
////    		
////    		//########################################################################################
////    		szMsg = "▲▲▲▲▲[" + szOperationName + "] Flex 권하 완료 실적 전송  END ▲▲▲▲▲";
////            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//            
//            
//            
//			
////크레인 작업지시 호출
//          //########################################################################################
//	        //설비id로 설비Table조회
//	        recInTemp = JDTORecordFactory.getInstance().create();
//	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
//	        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//	        intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
//			if(intRtnVal <= 0) {
//				if(intRtnVal == 0) {
//					szMsg="[" + szOperationName + "] data not found";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//				}else if(intRtnVal == -2) {
//					szMsg="[" + szOperationName + "] parameter error";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				}
//                m_ctx.setRollbackOnly();
//
//                recInTemp = JDTORecordFactory.getInstance().create();
//                recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_FAILURE);
//                return recInTemp;
//			}
//			
//			recOutTemp = JDTORecordFactory.getInstance().create();
//			recOutTemp.setRecord(rsResult.getRecord(0));
//			recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("MSG_ID",           "YDYDJ643");			
//			recInTemp.setField("YD_EQP_ID",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID"));
//			recInTemp.setField("YD_EQP_WRK_MODE",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE"));
//			recInTemp.setField("YD_WRK_PROG_STAT", "4");
//			recInTemp.setField("YD_SCH_CD",        szYD_SCH_CD);
//			recInTemp.setField("YD_CRN_SCH_ID",    "");
//			recInTemp.setField("YD_CRN_XAXIS",     "");
//			recInTemp.setField("YD_CRN_YAXIS",     "");
//			recInTemp.setField("YD_DN_CAR_FLAG",   szDnCarFlag);
//			recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_SUCCESS);
//			//########################################################################################
//			
//    		ydUtils.putLog(szSessionName, szMethodName, "▲▲▲▲▲[C열연 코일야드]권하실적처리 END ▲▲▲▲▲", YdConstant.INFO);   
//    		
//        }catch(Exception e) {
//
//        	String szLogMsg = "[C열연코일권하실적처리]권하실적 처리 에러발생 : " + e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
//            m_ctx.setRollbackOnly();
//            throw new JDTOException("<procY5CrnUdWrTX> " + szMsg);
//       }
//        return recInTemp;
//    }// end of procY5CrnUdWrTX()
    
    
	/**
	 * 오퍼레이션명 : 차량 작업 진행관리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procY5CarWrkStatCtrCoil(JDTORecord msgRecord)throws JDTOException  {
		//TC_CODE :YDYDJ630
		YdStkBedDao ydStkBedDao 			= new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao 			= new YdStkLyrDao();
		YdDelegate ydDelegate 				= new YdDelegate();
		YdCrnSchDao     ydCrnSchDao     	= new YdCrnSchDao();
		YdCarSchDao     ydCarSchDao     	= new YdCarSchDao();  
		PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
		YdStkColDao ydStkColDao 		= new YdStkColDao();
		JDTORecord    recInTemp         	= null;
		JDTORecord    recOutTemp        	= null;
		JDTORecord    recFirst          	= null;
		JDTORecord    recLast           	= null;
		JDTORecord recResult				= null;
		JDTORecordSet rsResultTemp 			= null;
		JDTORecordSet rsResult          	= null;
		YdStockDao ydStockDao 		= new YdStockDao();
		
	    int intRtnVal 		   	= 0 ;
	    String szMsg            = "";
	    String szMethodName     = "procY5CarWrkStatCtrCoil";
	    String szCAR_LDUD_GP    = "";
	    String szYD_WBOOK_ID    = "";
	    String szYD_CRN_SCH_ID  = "";
	    String szFST_CRN_SCH_ID = "";
	    String szLST_CRN_SCH_ID = "";
	    String szYD_SCH_CD      = "";
	    String szYD_GP          = "";
	    String szYD_CAR_SCH_ID  = "";
	    String szYD_CAR_USE_GP  = "";
	    String szYD_DN_WR_LOC   = "";
	    String szYD_UP_WR_LOC   = "";
	    String szSTL_APPEAR_GP  = "";
	    String szSANGCHCHK      = "";
	    String szCMBN_CARLD_YN	= "";
	    String szSTL_NO			= "";
	    String szARR_WLOC_CD	= "";
	    String szTRANS_EQUIPMENT_TYPE = "";
	    try{
	    	//상하차구분 플래그, 작업예약id, 크레인스케줄id
	    	szCAR_LDUD_GP   = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_LDUD_GP"); 
	    	szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	    	szYD_DN_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC");
	    	szYD_UP_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
	    	szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(msgRecord, "STL_APPEAR_GP");
	    	szSTL_NO		= ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");
	    	
//작업예약id로 크레인 스케줄을 조회
	    	recInTemp 	= JDTORecordFactory.getInstance().create();
	    	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
//PIDEV	 
//	    	String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");
			//상차완료유무 체크 ------------------------------------------------------------------
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSangchendChk*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 506);
	    	if(intRtnVal <= 0) {
				szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error2";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException("<procY5CarWrkStatCtr> getYdCrnsch" + szMsg);
	    	}		
	    	
	    	rsResult.first();
	    	recFirst = JDTORecordFactory.getInstance().create();
	    	recFirst.setRecord(rsResult.getRecord());
	    	szSANGCHCHK = ydDaoUtils.paraRecChkNull(recFirst, "SANGCHCHK");
	    	
	    	szMsg = "▼▼▼▼▼플래그 상태 : "+szCAR_LDUD_GP+" 전문 스케줄id : " + szYD_CRN_SCH_ID+" 상차완료유무 : " + szSANGCHCHK+" ▼▼▼▼▼";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	//----------------------------------------------------------------------------------
			
			
	    	
	    	szYD_SCH_CD      = ydDaoUtils.paraRecChkNull(recFirst, "YD_SCH_CD");
	    	szYD_GP          = szYD_SCH_CD.substring(0,1);
	    	

	    	recInTemp 	= JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
			recInTemp.setField("PI_YD",    	"J");					
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschUDLDWRKBOOKID_PIDEV*/    		
    		intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    	if(intRtnVal > 0) {
    		rsResult.absolute(1);
    		recOutTemp 	= JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult.getRecord()); 
    		szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
    		szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP"); 	
    		szTRANS_EQUIPMENT_TYPE		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_EQUIPMENT_TYPE"); //운송장비TYPE P:PDA
	    	}
	    	
	    	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 상차개시 전문 송신 처리 - 구내운송, 출하관리
	         * 업무기준 Desc : 권하지시위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
	         * 				상차검수 or 상차도착이면 상차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
	         * 기능 추가 : 임춘수
	         * 일자 : 2009.07.15
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            //권하지시위치가 차상인 경우 상차개시 전문 송신 처리
//            if(!szYD_SCH_CD.substring(2, 8).equals("TR05MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR06MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR15MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR16MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR07MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR08MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR17MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR18MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR57MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR58MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR67MM")&&
//        		!szYD_SCH_CD.substring(2, 8).equals("TR68MM")
//        		){
            if(!("TR".equals(szYD_SCH_CD.substring(2 , 4)) && "MM".equals(szYD_SCH_CD.substring(6 , 8)))){	
        	//###################################################################################################
            if( szYD_DN_WR_LOC.substring(2, 4).equals("PT") || szYD_DN_WR_LOC.substring(2, 4).equals("TR") ) {
            	//권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
            	recInTemp = JDTORecordFactory.getInstance().create();
            	recInTemp.setField("YD_STK_COL_GP", szYD_DN_WR_LOC.substring(0, 6));
            	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
            	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
            	if( intRtnVal <= 0 ) {
            		szMsg = "[권하실적처리 - 상차개시]차량정지위치[" + szYD_DN_WR_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            	}else{
            		//조회된 차량정지위치에서 운송장비코드를 가져온다.
	            	rsResult.first();
	            	recInTemp = rsResult.getRecord();
	            	//운송장비코드
	        		String  szTRN_EQP_CD 	= ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
	        		szYD_CAR_USE_GP 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
	        		String szCAR_NO 		= ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
	        		String szCARD_NO 		= ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
	        		
	        		szMsg = "[권하실적처리 - 상차개시]차량정지위치[" + szYD_DN_WR_LOC.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	                
	            	//운송장비코드로 차량스케줄조회
	                rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	                recInTemp = JDTORecordFactory.getInstance().create();	            	
	    			recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
	    			recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
	    			recInTemp.setField("STL_NO", szSTL_NO);
	    			     				    			
	    			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
	    			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 433);
	    			if( intRtnVal <= 0 ) {
	    				szMsg = "[권하실적처리 - 상차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            	}else{
	            		//차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
	            		rsResult.absolute(1);
	            		recInTemp = rsResult.getRecord();
	            		
	            		szYD_CAR_SCH_ID 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID"); //차량스케줄ID
	            		String szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT"); //야드차량진행상태
	            		szYD_CAR_USE_GP 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");//차량사용구분
	            		szCMBN_CARLD_YN				= ydDaoUtils.paraRecChkNull(recInTemp, "CMBN_CARLD_YN"); //복수창고 마지막 창고,동 인경우
	            		
	            		szMsg = "[권하실적처리 - 상차개시]차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		                
	            		//상차검수이거나 상차도착일 때 상차개시 전문 송신
	            		if( (szYD_CAR_PROG_STAT.equals("3") || szYD_CAR_PROG_STAT.equals("2")) && !"E".equals(szCMBN_CARLD_YN) ) {
	
	            			YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
	            			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
	            			recInTemp = JDTORecordFactory.getInstance().create();
	            			recInTemp.setField("STL_NO"	, szSTL_NO);
	            			intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, outRecSet, 500);
	            			
	            			if( intRtnVal > 0 ) {
	            				outRecSet.first();
	            				recInTemp = outRecSet.getRecord();
	            				szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recInTemp, "ARR_WLOC_CD");
	            			}
	            			
	            			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            			
	            			//야드구분에 따른 개소코드 반환
	            			//String szARR_WLOC_CD = YdCommonUtils.getWlocCd(szYD_AIM_YD_GP);
            				//차량스케줄 업데이트 - 상차개시
            				recInTemp = JDTORecordFactory.getInstance().create();
            				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);								//차량스케줄ID
            				recInTemp.setField("YD_CAR_PROG_STAT", "4");										//차량진행상태
        	    			recInTemp.setField("YD_EQP_WRK_STAT", "U");											//작업상태
        	    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);							//작업예약ID
        	    			recInTemp.setField("YD_CARLD_ST_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));			//상차개시일시
        	    			recInTemp.setField("MODIFIER",	"YDSYSTEM");										//수정자
        	    			if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송
        	    				recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);								//착지개소코드
        	    			}
        	    			intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
        	    			
        	    			if(intRtnVal <= 0) {
        						szMsg="[권하실적처리]차량스케줄에 상차개시일시 등록시 Error!! Code : " + intRtnVal;
        						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	    			} 
        	    			
        	    			  
        	    			//##############################################################################################
        	    			//구내운송 상차개시  인 경우 (소재 ,제품 인 경우 )
        	    			//##############################################################################################
        	    			recInTemp = JDTORecordFactory.getInstance().create();
	            			if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송
	            				 
		            			//상차작업개시 송신 YDTSJ007 (구내운송 상차개시)
				    			recInTemp.setField("MSG_ID",        "YDTSJ007");					    			
				    			recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD); //착지개소코드
				    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
		    	    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
		    	    			recInTemp.setField("YD_GP",         szYD_GP);
		    	    			ydDelegate.sendMsg(recInTemp);			    	    			
		    	    			
		    	    			//코일제품고간이송상하차개시 송신 YDDMR019(제품인 경우에만 )								 
		    	    			if(szSTL_APPEAR_GP.equals("Y")){
									recInTemp = JDTORecordFactory.getInstance().create();
									recInTemp.setField("MSG_ID",        "YDDMR019"); 
									recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
									recInTemp.setField("YD_GP"			, szYD_GP);
									ydDelegate.sendMsg(recInTemp);
								}	
	            			}else if( szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
	    
	        	    			//코일제품이송 상차개시 전송PDA
	        	    			recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("MSG_ID",        "YDDMR071"); 
								recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
								recInTemp.setField("YD_GP"			, szYD_GP);
								ydDelegate.sendMsg(recInTemp);
	            			}

	            			
	    	    			szMsg="[권하실적처리]상차작업개시 송신 완료";
	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            		}
	            	}
            	}
            }
        	//###################################################################################################
            }
            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	
            if( szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
	            szMsg="일품 상차실적 송신 YDDMR072 (코일일품출하상차실적 송신)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",        "YDDMR072");					
				recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recInTemp.setField("YD_GP",         szYD_GP);
				recInTemp.setField("STL_NO",        szSTL_NO);
	
				if("Y".equals(szSANGCHCHK)){ // 상차완료인 저장품이 ALL일때 처리
					recInTemp.setField("GOODS_EA","*");
	            }else{
	            	recInTemp.setField("GOODS_EA","1");
	            }
				
				ydDelegate.sendMsg(recInTemp);
            }
	    	
	    	
            
	    	
	    	//**************************************************************************************************
	    	//플래그가 상차인경우
	    	//**************************************************************************************************
	    	if(szCAR_LDUD_GP.equals("U")) {
	    		//차량스케줄 id를 조회
	    		
	    		

	    		//출하차량인 경우에만 적용한다.
	    		if(szYD_CAR_USE_GP.equals("G")){
	    			
	    			recInTemp 	= JDTORecordFactory.getInstance().create();
	    			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
    				recInTemp.setField("STL_NO",         ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));
    				/*yd.facilitystatus.facilityinquiry.CraneSchDAO.getYmPoFrtoInfo_PIDEV*/
    				intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 504);
    		    	if(intRtnVal <= 0) {
			    		recInTemp = JDTORecordFactory.getInstance().create();		
						recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
						recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						recInTemp.setField("YD_GP",         szYD_GP);
						recInTemp.setField("STL_NO",        ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));

						if("Y".equals(szSANGCHCHK)){ // 상차완료인 저장품이 ALL일때 처리
							recInTemp.setField("GOODS_EA","*");
	                    }else{
	                    	recInTemp.setField("GOODS_EA","1");
	                    }

    		    	}
//PIDEV    		    	

//    				if("Y".equals(sApplyYnPI)) {
    					// ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW2_PIDEV
        				intRtnVal = ydStockDao.updYdStockExa_PIDEV(recInTemp, 0);	
//    				} else {
//    					//검수 테이블 생성 //////////////////////////////////////////////////////////////
//        				// ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW2
//        				intRtnVal = ydStockDao.updYdStockExa(recInTemp, 0);			
//
//    				}	    		    	
 
    				if(intRtnVal >0){
    					szMsg = "수신한 재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록이 되었습니다.";
    					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

    				}else if(intRtnVal == 0){
    					szMsg = "수신한  재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록 되었거나  실패 하였습니다.";
    					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
    	 			}
    				///////////////////////////////////////////////////////////////////////////////
	    		}
	    		
	    		
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		//if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    		if("Y".equals(szSANGCHCHK)){
//동일하면 차량스케줄에 상차완료일시 등록, 설비작업상태 = '영차'
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_CAR_PROG_STAT",		"5");
	    			recInTemp.setField("YD_EQP_WRK_STAT",		"L");
	    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 	szYD_WBOOK_ID);
	    			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarschYdCarWrkBookId1*/
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 1);
	    			if(intRtnVal <= 0) {
						szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}
	    			
	
	    			//##############################################################################################
	    			//구내운송 상차완료 인 경우 (소재 ,제품 인 경우 )
	    			//##############################################################################################
	    			if( szYD_CAR_USE_GP.equals("L") ) {		
 
	    				
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------
	    				
		    			//상차작업완료 송신 YDTSJ008\
		    			recInTemp.setField("MSG_ID", "YDTSJ008");
		    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
		    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
		    			recInTemp.setField("YD_GP",         szYD_GP);
		    			ydDelegate.sendMsg(recInTemp);
						
		    			//AB재료정보생성 
		    			this.Y5insYmStockCoil(recInTemp);
		    			
		    			szYD_DN_WR_LOC =szYD_DN_WR_LOC.substring(0, 6);
		    			
		    			//다음 대기차량을 입동시키기 위해 상차지 차량위치 정보를 비워주고 다음 차량을 입동 시킨다. 
		    			recInTemp = JDTORecordFactory.getInstance().create();
		    			recInTemp.setField("YD_STK_COL_ACT_STAT"	, "C");
		    			recInTemp.setField("TRN_EQP_CD"				, "");
		    			recInTemp.setField("YD_CAR_USE_GP"			, "");
		    			recInTemp.setField("CAR_NO"					, "");
		    			recInTemp.setField("CARD_NO"				, "");		
		    			recInTemp.setField("YD_STK_COL_GP",   		szYD_DN_WR_LOC);
				        intRtnVal = ydStkColDao.updYdStkcol(recInTemp,0);
				        if(intRtnVal <= 0) {
							szMsg="상차지 차량위치 정보를 비워주는 작업 도중 Error!! Code : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}
				        
				        
				        //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn2 = new EJBConnector("default","CarMvHdSeEJB",this);
						ejbConn2.trx("CarPointinforeg2", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"B","","",szYD_DN_WR_LOC,"","","C"});
				        
				        /*
						 * 적치베드 상태 비활성화등록
						 */
				        
				        szMsg= "[" + szMethodName + "] 출발야드의 적치베드["+szYD_DN_WR_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_STK_BED_WT_MAX", YdConstant.YD_STK_BED_WT_MAX_DEFAULT);
						recInTemp.setField("YD_STK_COL_GP", szYD_DN_WR_LOC);
						recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
						
						intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 0);
						if(intRtnVal <= 0) {
							szMsg="적치베드 상태 비활성화등록 Error!! Code : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}
						
						/*
						 * 적치단 비활성화
						 */
						szMsg= "[" + szMethodName + "] 출발야드의 적치열["+szYD_DN_WR_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_STK_COL_GP", szYD_DN_WR_LOC);
						recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
						recInTemp.setField("STL_NO", "");
						recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
				    	
						intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
						if(intRtnVal <= 0) {
							szMsg="적치단 비활성화  Error!! Code : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}
						
				    	//=======================================================================
			    		// 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
			    		// 수정자    : 권오창
			    		// 수정일자 : 2009.12.10
				    	//=======================================================================
				    	recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_INFO_SYNC_CD", "3");							    // 1:동,2:SPAN,3:열,4:BED
						recInTemp.setField("YD_GP"          , szYD_DN_WR_LOC.substring(0, 1));
						recInTemp.setField("YD_STK_COL_GP"  , szYD_DN_WR_LOC);
						recInTemp.setField("YD_CAR_PROG_STAT", "A");
						recInTemp.setField("YD_EQP_WRK_STAT" , "L");
						szMsg = "[" + szMethodName + "] 영차출발시 시 저장위치 제원 야드L2로 전송";
						
						YdCommonUtils.sndStrPosSpecToL2(recInTemp);	
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						

						
						/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				         * 			구내운송 소재차량Point개폐 전송  - YDTSJ012
				         * 업무기준 Desc : 1. 외판슬라브 출하차량 도착 실적처리 후 구내운송에소재차량Point개폐 전송 전송
				         * 				  2. 출하관리와 구내운송간의 차량point 정보공유를 위해서, 구내운송에서 출하차량이 도착한 point를 사용하지
				         * 					않도록 하기 위해서.
				         * 				  3. +++++++++++ 출발 시는 포인트가 열렸다고 전송 +++++++++++++
				         * 기능 추가 : 임춘수
				         * 일자 : 2009.06.25
				         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID"           	  , "YDTSJ012");
						recInTemp.setField("YD_STK_COL_GP", szYD_DN_WR_LOC);			//적치열구분
						recInTemp.setField("PNT_UNIT_CL_GP", YdConstant.PNT_UNIT_CL_GP_OPEN);									//포인트개폐구분
						ydDelegate.sendMsg(recInTemp);
						szMsg = "[출하차량출발]구내운송 소재차량Point개폐 전송 완료 - PNT_UNIT_CL_GP : " + YdConstant.PNT_UNIT_CL_GP_OPEN;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

						
						//코일제품고간이송상하차완료 송신 YDDMR021(제품인 경우에만 )
						//if(szYD_SCH_CD.substring(0, 1).equals("J")){
						if(szSTL_APPEAR_GP.equals("Y")){
							
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setField("MSG_ID",        "YDDMR021"); // 출하 - 상차완료 YDDMR021
							recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
							ydDelegate.sendMsg(recInTemp);
						}	
						
	    			}else if( szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
	    			    
	    				
	    				recInTemp = JDTORecordFactory.getInstance().create();
	    				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    				recInTemp.setField("STL_NO",         ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));
//PIDEV_S :병행가동용:PI_YD
	    				recInTemp.setField("PI_YD",    	"J");	
	    				intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 504);
	    		    	if(intRtnVal > 0) {
	    		    		szMsg="임가공대상제가 존재 함.222";
	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
	    		    	}else{
	    	    			//코일제품이송 상차완료 전송PDA
	    	    			recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setField("MSG_ID",        "YDDMR073"); 
							recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
							recInTemp.setField("YD_GP"			, szYD_GP);
							ydDelegate.sendMsg(recInTemp);
							
							
							//3. 냉연 이송 상차완료 시점에 소재야드 맵 활성화####################################################
			    			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			    			String sCHK_YN="N";
			    			jrParam.setField("YD_BAY_GP"    , szYD_DN_WR_LOC.substring(1 , 2)   ); //동정보
			    			jrParam.setField("STL_NO"    , szSTL_NO   ); //코일번호 
			    			
			    			JDTORecordSet jsSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    			
			    			jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYML007ChkYN", "SYSTEM", szMethodName, "소재통로냉연이송여부 조회");
			    			
			    			if (jsSch.size() > 0) {
			    				/**********************************************************
			    				* 해당동 사용여부
			    				**********************************************************/
			    				sCHK_YN    		= jsSch.getRecord(0).getFieldString("CHK_YN"   );
			    			}
			    			 
			    			szMsg = "HRYDJ007실적소재통로냉연이송여부:"+sCHK_YN ;
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			    			
			    			if("Y".equals(sCHK_YN)){
			    				recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("STL_NO" 			, ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO")); 			
				 			 
								//수정
								commDao.update(recInTemp, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdHStklyrChange2", "HRYDJ007", szMethodName, "소재야드 맵 활성화 변경");
			    			}
							
							
							//##########################################################################################
							
	    		    	}
						 
						//진행관리 냉연코일이송진행 상태실적   *********************************************
						JDTORecord recPara         = null;
						JDTORecord recGetVal       = null;
						JDTORecordSet rsCoilResult = null;
						recPara = JDTORecordFactory.getInstance().create();			
						rsCoilResult 	= JDTORecordFactory.getInstance().createRecordSet("");
						recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtlMES*/
						intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsCoilResult, 434);		
						if(intRtnVal > 0) {						 	
						
							szMsg = "상차완료 처리 대상 건수  [Ret : " + rsCoilResult.size() + "]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
							
							for(int nIdx=0; nIdx< intRtnVal; nIdx++) {
								recGetVal = rsCoilResult.getRecord(nIdx);
													        
						        //진행관리 냉연코일이송진행 상태실적 
				    			recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("MSG_ID",        "YDPTJ006"); 
								recInTemp.setField("STL_NO", ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
								ydDelegate.sendMsg(recInTemp);
						        
							}
						}
						//진행관리 냉연코일이송진행 상태실적   *********************************************
						
        			}
	    			
	    			
	    			
	    			
	    			
	    			
	    		
	    			if( szYD_CAR_USE_GP.equals("G") ){							//출하관리 
	    				
	    				recInTemp = JDTORecordFactory.getInstance().create();
	    				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    				recInTemp.setField("STL_NO",         ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));
	    				intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 504);
	    		    	if(intRtnVal > 0) {
	    					szMsg="임가공대상제가 존재 함.";
	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
	    					
	    					//육송출하고도화
	    					ymCommonDAO dao = ymCommonDAO.getInstance();
	    					 List chkList = null;
	    					String QueryId 	= "com.inisteel.cim.yd.dao.chklist";
	    					chkList = dao.getCommonList(QueryId, new Object[]{});

	    				    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
	    			    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");	    	
	    			    	ydUtils.putLog(szSessionName, szMethodName, "◑◑◑◑◑  CHK:"+CHK, YdConstant.INFO);
	    			    	
	    			    	
	    					//##############################################################################################
	    	    			//임가공 출하차량  상차완료 인 경우 (제품 인 경우)
	    	    			//##############################################################################################
	    	    			//상차작업완료 송신 YDDMR022 (임가공이송하차완료)
 
	    			    	if(!CHK.equals("Y") ){
			    		    	recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("MSG_ID",        "YDDMR022");
				    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				    			recInTemp.setField("YD_GP",         szYD_GP);
				    			
				    			ydDelegate.sendMsg(recInTemp);
				    			
				    			//-----------------------------------------------------------------------------------------------
				    			//차량 자동출발 처리 
				    			
				    			recInTemp  = JDTORecordFactory.getInstance().create();
								rsResult = JDTORecordFactory.getInstance().createRecordSet("");
								recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
								intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
								if(intRtnVal > 0){
							 
									rsResult.first();
									recResult = rsResult.getRecord();
									String szCAR_NO = ydDaoUtils.paraRecChkNull(recResult,"CAR_NO");
									recInTemp = JDTORecordFactory.getInstance().create();
									recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
									recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
									recInTemp.setField("CAR_NO", 				szCAR_NO);			
									recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"SPOS_WLOC_CD"));
									recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD1"));
									recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
									recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));
									String szCARD_NO = ydDaoUtils.paraRecChkNull(recResult,"CARD_NO");
	
									//E/T Car 나 해송차량인 경우에는 차량출발처리를 자동으로 하지 않는다.
	//								if(szCAR_NO.matches("\\d\\d\\d\\d")) {				//E/T Car
									
									// PIDEV
//									if("N".equals(sApplyYnPI)) {
//										if(szCARD_NO.equals("")){
//											szCARD_NO = "XXXXX";
//										}	
//				    				}	
								
									szMsg= "[procY5CarWrkStatCtr] 차량번호[" + szCAR_NO + "]는 자동차량출발";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
									ejbConn.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
			    			     
								}
								//--------------------------------------------------------------------------------------------------
	    			    	}else{
	    			    		
	    			    		//복수상차 처리 로직
		    		    		recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("STL_NO",  ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO")); 
								recInTemp.setField("YD_GP",   szYD_GP);
								
								EJBConnector ejbConn1 = new EJBConnector("default","RtModRegSeEJB",this);
								String isTemp = (String)ejbConn1.trx("procCmbnCarldYn",
															new  Class[]{JDTORecord.class },
															new Object[]{recInTemp});
								
								if(isTemp.equals(YdConstant.RETN_CD_EXIST)){
		    		    		
									recInTemp = JDTORecordFactory.getInstance().create();
									recInTemp.setField("MSG_ID",        "YDDMR022");
					    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					    			recInTemp.setField("YD_GP",         szYD_GP);
					    			
					    			ydDelegate.sendMsg(recInTemp);	
					    			
					    			
					    			//-----------------------------------------------------------------------------------------------
					    			//차량 자동출발 처리 
					    			
					    			recInTemp  = JDTORecordFactory.getInstance().create();
									rsResult = JDTORecordFactory.getInstance().createRecordSet("");
									recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
									intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
									if(intRtnVal > 0){
								 
										rsResult.first();
										recResult = rsResult.getRecord();
										String szCAR_NO = ydDaoUtils.paraRecChkNull(recResult,"CAR_NO");
										recInTemp = JDTORecordFactory.getInstance().create();
										recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
										recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
										recInTemp.setField("CAR_NO", 				szCAR_NO);			
										recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"SPOS_WLOC_CD"));
										recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD1"));
										recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
										recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));
										String szCARD_NO = ydDaoUtils.paraRecChkNull(recResult,"CARD_NO");
		
										//E/T Car 나 해송차량인 경우에는 차량출발처리를 자동으로 하지 않는다.
		//								if(szCAR_NO.matches("\\d\\d\\d\\d")) {				//E/T Car
										
										// PIDEV
//										if("N".equals(sApplyYnPI)) {
//											
//											if(szCARD_NO.equals("")){
//												szCARD_NO = "XXXXX";
//											}
//											
//										}
										
										szMsg= "[procY5CarWrkStatCtr] 차량번호[" + szCAR_NO + "]는 자동차량출발";
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
										EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
										ejbConn.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				    			     
									}
									//--------------------------------------------------------------------------------------------------
								}
	    			    	}
			    			
	    		    	}else{	    		    		
			    			//##############################################################################################
			    			//출하차량  상차완료 인 경우 (제품 인 경우)
			    			//##############################################################################################
	    		    		
	    		    		if( !szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
		    		    		//육송출하고도화
		    					ymCommonDAO dao = ymCommonDAO.getInstance();
		    					 List chkList = null;
		    					String QueryId 	= "com.inisteel.cim.yd.dao.chklist";
		    					chkList = dao.getCommonList(QueryId, new Object[]{});
	
		    				    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
		    			    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");	    	
		    			    	ydUtils.putLog(szSessionName, szMethodName, "◑◑◑◑◑  CHK:"+CHK, YdConstant.INFO);
		    			    	
		    			    	if(!CHK.equals("Y") ){
		    			    		//상차작업완료 송신 YDDMR015 (코일출하상차완료)
									recInTemp.setField("MSG_ID",        "YDDMR015");
					    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
					    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					    			recInTemp.setField("YD_GP",         szYD_GP);
					    			
					    			ydDelegate.sendMsg(recInTemp);	
					    			
					    			//3. 냉연 이송 상차완료 시점에 소재야드 맵 활성화####################################################
					    			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
					    			String sCHK_YN="N";
					    			jrParam.setField("YD_BAY_GP"    , szYD_DN_WR_LOC.substring(1 , 2)   ); //동정보
					    			jrParam.setField("STL_NO"    , szSTL_NO   ); //코일번호 
					    			
					    			JDTORecordSet jsSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
					    			
					    			jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYML007ChkYN", "SYSTEM", szMethodName, "소재통로냉연이송여부 조회");
					    			
					    			if (jsSch.size() > 0) {
					    				/**********************************************************
					    				* 해당동 사용여부
					    				**********************************************************/
					    				sCHK_YN    		= jsSch.getRecord(0).getFieldString("CHK_YN"   );
					    			}
					    			 
					    			szMsg = "HRYDJ007실적소재통로냉연이송여부:"+sCHK_YN ;
					    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					    			
					    			if("Y".equals(sCHK_YN)){
					    				recInTemp = JDTORecordFactory.getInstance().create();
										recInTemp.setField("STL_NO" 			, ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO")); 			
						 			 
										//수정
										commDao.update(recInTemp, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdHStklyrChange2", "HRYDJ007", szMethodName, "소재야드 맵 활성화 변경");
					    			}
									
									
									//##########################################################################################
		    			    	}else{
			    		    		//복수상차 처리 로직
			    		    		recInTemp = JDTORecordFactory.getInstance().create();
									recInTemp.setField("STL_NO",  ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO")); 
									recInTemp.setField("YD_GP",   szYD_GP);
									
									EJBConnector ejbConn1 = new EJBConnector("default","RtModRegSeEJB",this);
									String isTemp = (String)ejbConn1.trx("procCmbnCarldYn",
																new  Class[]{JDTORecord.class },
																new Object[]{recInTemp});
									
									if(isTemp.equals(YdConstant.RETN_CD_EXIST)){
			    		    		
						    			//상차작업완료 송신 YDDMR015 (코일출하상차완료)
										recInTemp.setField("MSG_ID",        "YDDMR015");
						    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
						    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						    			recInTemp.setField("YD_GP",         szYD_GP);
						    			
						    			ydDelegate.sendMsg(recInTemp);	
						    			
						    			//3. 냉연 이송 상차완료 시점에 소재야드 맵 활성화####################################################
						    			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
						    			String sCHK_YN="N";
						    			jrParam.setField("YD_BAY_GP"    , szYD_DN_WR_LOC.substring(1 , 2)   ); //동정보
						    			jrParam.setField("STL_NO"    , szSTL_NO   ); //코일번호 
						    			
						    			JDTORecordSet jsSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
						    			
						    			jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYML007ChkYN", "SYSTEM", szMethodName, "소재통로냉연이송여부 조회");
						    			
						    			if (jsSch.size() > 0) {
						    				/**********************************************************
						    				* 해당동 사용여부
						    				**********************************************************/
						    				sCHK_YN    		= jsSch.getRecord(0).getFieldString("CHK_YN"   );
						    			}
						    			 
						    			szMsg = "HRYDJ007실적소재통로냉연이송여부:"+sCHK_YN ;
						    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
						    			
						    			if("Y".equals(sCHK_YN)){
						    				recInTemp = JDTORecordFactory.getInstance().create();
											recInTemp.setField("STL_NO" 			, ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO")); 			
							 			 
											//수정
											commDao.update(recInTemp, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdHStklyrChange2", "HRYDJ007", szMethodName, "소재야드 맵 활성화 변경");
						    			}
										
										
										//##########################################################################################
						    			
						    			
						    			//진행관리 냉연코일이송진행 상태실적   *********************************************
										JDTORecord recPara         = null;
										JDTORecord recGetVal       = null;
										JDTORecordSet rsCoilResult = null;
										recPara = JDTORecordFactory.getInstance().create();			
										rsCoilResult 	= JDTORecordFactory.getInstance().createRecordSet("");
										
										recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
										/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtlMES*/
										intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsCoilResult, 434);		
										if(intRtnVal > 0) {						 	
										
											szMsg = "상차완료 처리 대상 건수  [Ret : " + rsCoilResult.size() + "]";
											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
											
											for(int nIdx=0; nIdx<rsCoilResult.size(); nIdx++) {
												recGetVal = rsCoilResult.getRecord(nIdx);
																				        
										        
										        //진행관리 냉연코일이송진행 상태실적 
								    			recInTemp = JDTORecordFactory.getInstance().create();
												recInTemp.setField("MSG_ID",        "YDPTJ006"); 
												recInTemp.setField("STL_NO", ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
												ydDelegate.sendMsg(recInTemp);
										        
											}
										}
										//진행관리 냉연코일이송진행 상태실적   *********************************************
									}
		    			    	}
	    		    		}
	    		    	}
	    		    	 
	    			}
	    			
	    			
					szMsg="상차작업완료 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}
	    		
	    		
	    		
	    		
	    		
	    	//**************************************************************************************************	
	    	//플래그가 하차인 경우
	    	//**************************************************************************************************
	    	}else if(szCAR_LDUD_GP.equals("L"))	{
 
	    		
	    		
    			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	         * 			진행관리 코일소재이송완료실적전송  - YDPTJ002
    	         * 업무기준 Desc : 1. 하차완료시
    	         * 스케줄코드 :  1. 차량하차 스케줄 : PT(팔렛트), TR(트레일러), LM(하차)
    	         * 기능 추가 : 임춘수
    	         * 일자 : 2009.06.16
    	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    		//구내운송 차량이고 소재 이송인 경우 에만 처리 함.
	    		if( szYD_CAR_USE_GP.equals("L") && !szSTL_APPEAR_GP.equals("Y")) {									//구내운송 - 임춘수 수정 2009.06.15
	    			
	    		 			 
    			//----------------------------------------------------------------------------------------------------------
    			// 이송지시테이블 업데이트 - 이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드
    			//----------------------------------------------------------------------------------------------------------
    			recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("STL_NO",        ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));
    			
    			rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
    			intRtnVal = ptStlFrtoMoveDao.getPtStlFrtoMove(recInTemp, rsResultTemp, 0);
				if( intRtnVal <= 0 ) {
					szMsg="[이송지시] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료[" + ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO") + "]가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return intRtnVal;
				} else {
				
				
					rsResultTemp.first();
					recOutTemp = JDTORecordFactory.getInstance().create();
	    			recOutTemp.setRecord(rsResultTemp.getRecord());
	    			recOutTemp.setField("YD_MTL_PLN_STR_TO_LOC_CD", szYD_DN_WR_LOC);
	    			recOutTemp.setField("FRTOMOVE_STAT_CD", "*");
	    			
	    			intRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recOutTemp, 1);
	    			if( intRtnVal <= 0 ) {
	    				szMsg="[이송지시] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 실패";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				return YdConstant.RETN_CD_FAILURE;
	    			}
				}
				
				recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("MSG_ID",        "YDPTJ002");
    			recInTemp.setField("STL_NO",        ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"));
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			ydDelegate.sendMsg(recInTemp);  
    			
    	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	
	    		}
	    		
	    		
	    		
	    		/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	         * 			반품,회송,부분하차인경우 자동출발
    	          +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    		
	    		
	    		//반품,회송,부분하차인경우 여기서 차량출발처리를 한다.
    			if(("H".equals(szYD_SCH_CD.substring(0,1)) || "J".equals(szYD_SCH_CD.substring(0,1)))&& "PT".equals(szYD_SCH_CD.substring(2,4))&& "4LM".equals(szYD_SCH_CD.substring(5,8))	
    				
    			) {   				
    			  
    				recInTemp 	= JDTORecordFactory.getInstance().create();
    		    	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
    		    	recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
    		    	
    				//상차완료유무 체크 ------------------------------------------------------------------
    				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarHachendChk*/
    				intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 607);
    		    	if(intRtnVal <= 0) {
    					szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error2";
    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    					throw new DAOException("<procY5CarWrkStatCtr> getYdCrnsch" + szMsg);
    		    	}		
    		    	
    		    	rsResult.first();
    		    	recFirst = JDTORecordFactory.getInstance().create();
    		    	recFirst.setRecord(rsResult.getRecord());
    		    	String szHACHCHK = ydDaoUtils.paraRecChkNull(recFirst, "HACHCHK");
    		    	
    		    	szMsg = "▼▼▼▼▼플래그 상태 : "+szCAR_LDUD_GP+" 전문 스케줄id : " + szYD_CRN_SCH_ID+" 하차완료유무 : " + szHACHCHK+" ▼▼▼▼▼";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		    	//----------------------------------------------------------------------------------
    				
    				if("Y".equals(szHACHCHK)){
    				
			    		recInTemp  = JDTORecordFactory.getInstance().create();
		    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
						if(intRtnVal > 0){
					 
							rsResult.first();
							JDTORecord recResult2 = rsResult.getRecord();
							
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
							recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult2,"CARD_NO"));
							recInTemp.setField("CAR_NO", 				ydDaoUtils.paraRecChkNull(recResult2,"CAR_NO"));	
							recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult2,"ARR_WLOC_CD"));
							recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult2,"YD_PNT_CD3"));
							recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult2,"TRANS_ORD_DATE"));
							recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult2,"TRANS_ORD_SEQNO"));
		
							szMsg= "차량번호[" + ydDaoUtils.paraRecChkNull(recResult2,"CAR_NO") + "]는 자동차량출발(권하)";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
							ejbConn.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					     
						}
					
    				}
    				
    			}
	    		
	    	}else{
				szMsg="상차 및 하차 구분 플래그 Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException("<procY5CarWrkStatCtr>" + szMsg);
	    	}

		}catch(Exception e){
	
			szMsg="차량 작업 진행관리 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("<procY5CarWrkStatCtr> getYdCrnsch" + szMsg);
		}
	
	
		szMsg="차량 작업 진행관리 처리"+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return szYD_CAR_SCH_ID;
	} //end of procY5CarWrkStatCtr()
    
    
    

    
    
    
    
    /**
     * 오퍼레이션명 : 권상,권하 파라미터 체크
     *  
     * @param  ● msgRecord, outRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5ParamCheckCoil (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "";
        String szMethodName                 = "Y5ParamCheckCoil";
        int intRtnVal = 0 ;
        
    	try{
            
			setRecord.setField("YD_CRN_SCH_ID"			, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
			setRecord.setField("YD_SCH_CD"        		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
			setRecord.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
			setRecord.setField("DATE"          			, ydDaoUtils.paraRecChkNull(msgRecord,"DATE")) ;
			setRecord.setField("TIME"          			, ydDaoUtils.paraRecChkNull(msgRecord,"TIME")) ;
			setRecord.setField("MSG_GP"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP")) ;
			setRecord.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
			setRecord.setField("YD_EQP_WRK_MODE"        , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE")) ;
			setRecord.setField("YD_WRK_PROG_STAT"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT")) ;
			setRecord.setField("YD_CRN_XAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS")) ;
			setRecord.setField("YD_CRN_YAXIS"         	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS")) ;
			setRecord.setField("YD_CRN_ZAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS")) ;
			setRecord.setField("YD_DN_WR_LOC"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
			setRecord.setField("YD_DN_WR_LAYER"        	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;

            //전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
	        if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "A") ;
	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "B") ;
	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "M") ;
	        }
	        
	      // 크레인무인화 추가
	        if("A".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE2"))) {
	        	setRecord.setField("YD_UP_WRK_MODE2", "A") ;
	        	setRecord.setField("YD_DN_WRK_MODE2", "A") ;
	        }else if("R".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE2"))) {
	        	setRecord.setField("YD_UP_WRK_MODE2", "R") ;
	        	setRecord.setField("YD_DN_WRK_MODE2", "R") ;
	        }else if("E".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE2"))) {
	        	setRecord.setField("YD_UP_WRK_MODE2", "E") ;
	        	setRecord.setField("YD_DN_WRK_MODE2", "E") ;
	        }else if("M".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE2"))) {
	        	setRecord.setField("YD_UP_WRK_MODE2", "M") ;
	        	setRecord.setField("YD_DN_WRK_MODE2", "M") ;
	        }
    		
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal;
        }//end of try~catch
        
        intRtnVal = 1 ;
        return intRtnVal;
        
    }//end of Y5ParamCheckCoil()
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5UpdYdCrnschCoil(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	int intRtnVal = 0 ;

		try{
			intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);		        
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y5UpdYdCrnschCoil
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5GetYdCrnschCoil (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
        }//end of try~catch

        return intRtnVal ;
    	
    }//end of Y5GetYdCrnschCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Clear
     *  
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5ClearYdStklyrCoil (JDTORecordSet getRecSet, int intGp) throws JDTOException {
    	
    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord outRecord	= JDTORecordFactory.getInstance().create();
    	//적치단 조회
    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	
    	int intRtnVal 		= 0;
    	String szMsg 		= "";
    	String szMethodName = "Y5ClearYdStklyrCoil";
    	
    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();
            
    		for(int i=0; i<rowsize ; i++){
    			
    			//권상 실적위치 Clear
                if(intGp == 0) {
        			String szYD_UP_WR_LOC	= ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC") ;
                    String szYD_UP_WR_LAYER = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER") ;
                    
                	//권상 실적위치가 2단 일 경우 
                    setRecord.setField("YD_STK_COL_GP",			szYD_UP_WR_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",			szYD_UP_WR_LOC.substring(6,8));   
                    //적치단 설정
                    String szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                    setRecord.setField("YD_STK_LYR_NO",      	szStkLyr) ;
                    setRecord.setField("STL_NO",             	"");
                    setRecord.setField("YD_STK_LYR_MTL_STAT", 	"E");

                    intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1);  //적치단의 재료정보 Clear
                    if(intRtnVal <= 0) {
                    	return intRtnVal ;
                    }
                }
                
                //권하 지시위치 Clear
                if(intGp == 1) {
                	String szYD_DN_WO_LOC	= ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC") ;
                    String szYD_DN_WO_LAYER = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LAYER") ;
                    
                    //권상 실적위치가 2단 일 경우 
                    setRecord.setField("YD_STK_COL_GP",       	szYD_DN_WO_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WO_LOC.substring(6,8));   
                    //적치단 설정
                    String szStkLyr	= ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i);
                    setRecord.setField("YD_STK_LYR_NO",       	szStkLyr) ;

                    intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1);  //적치단의 재료정보 Clear
                }
                    
                //권하처리값 리턴
                if(intRtnVal <= 0) {
                	return intRtnVal ;
                }
                
                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for
    		
    		
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal;
	    }//end of try~catch
		
		intRtnVal = 1 ;
		return  intRtnVal;
    }//end of Y5ClearYdStklyrCoil()
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 등록
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     * 
     */
    public int Y5RegYdStklyrCoil (JDTORecordSet getRecSet, int intGp)throws JDTOException {
    	YdStockDao ydStockDao 			= new YdStockDao();
    	YdStkLyrDao ydStkLyrDao 		= new YdStkLyrDao();
    	
    	//getRecSet의 첫번째 레코드값
    	JDTORecord getRecord 			= JDTORecordFactory.getInstance().create();
    	//업데이트 data 셋팅
    	JDTORecord setRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord recOutTemp  			= null;
    	JDTORecord inRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord recPara 				= JDTORecordFactory.getInstance().create();
    	//outRecSet의 첫번째 레코드값
    	JDTORecord outRecord1 			= JDTORecordFactory.getInstance().create();
    	
    	//적치단 조회
    	JDTORecordSet outRecSet 		= JDTORecordFactory.getInstance().createRecordSet("temp");
    	JDTORecordSet rcResult 			= null;
    	
    	
    	String szMsg 					= "";
        String szMethodName				= "Y5RegYdStklyrCoil";
        String szOperationName          = "적치단 등록";
        String sYD_UP_WR_LOC            = "";
        String szYD_MTL_ITEM 			= "";
        String szL2_SND_YN 				= "";
        int intRtnVal 					= 0 ;
        
        String sSNDBK_REGISTER			= "";
        String sSNDBK_GP_ETC			= "";
        String sSNDBK_GP				= "";
        String szCURR_PROG_CD			= "";
        
    	try{
    		int rowsize = getRecSet.size();
            
    		getRecSet.first();
    		getRecord = JDTORecordFactory.getInstance().create();
    		getRecord.setRecord(getRecSet.getRecord());
    		
    		
    		//저장품을 조회하여 재표 품목의 값을 조회한다.
    		rcResult  = JDTORecordFactory.getInstance().createRecordSet("");
    		intRtnVal = ydStockDao.getYdStock(getRecord, rcResult, 0);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = szMethodName + " getYdStock no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	        	case -2	:
	                szMsg = szMethodName + " getYdStock parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	        }
	        rcResult.absolute(1);
	        recOutTemp = JDTORecordFactory.getInstance().create();
	        recOutTemp.setRecord(rcResult.getRecord());
    		
	        szYD_MTL_ITEM 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_MTL_ITEM");
	        szCURR_PROG_CD 	= ydDaoUtils.paraRecChkNull(getRecord,"CURR_PROG_CD");
//    		szYdWbookId 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID");
    		
	        sSNDBK_REGISTER	= ydDaoUtils.paraRecChkNull(recOutTemp, "SNDBK_REGISTER");
			sSNDBK_GP_ETC	= ydDaoUtils.paraRecChkNull(recOutTemp, "SNDBK_GP_ETC");
			sSNDBK_GP		= ydDaoUtils.paraRecChkNull(recOutTemp, "SNDBK_GP");
    		
        	for(int i=0; i<rowsize; i++) {
        		//권하 실적위치 등록
        		String szYD_DN_WR_LOC	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
        		String szYD_DN_WR_LAYER	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER") ;
        		String szSTL_NO	 		   = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO") ;

				outRecSet	= JDTORecordFactory.getInstance().createRecordSet("YD");
				inRecord 	= JDTORecordFactory.getInstance().create();
				inRecord.setField("STL_NO",   szSTL_NO);
	//getYdStklyr24
//PIDEV_S :병행가동용:PI_YD
				inRecord.setField("PI_YD",    	"J");							
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
				intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 24);
				ydUtils.putLog(szSessionName, szMethodName, "재료번호 건수가 여려개 있는거 clear 건수:"+ intRtnVal, YdConstant.INFO);	
				if (intRtnVal > 0) {
					//적치되어 있는 정보 삭제처리
					outRecSet.first();
					//크레인스케줄의 작업 재료 만큼 루프를 돌아 권상권하 대기 정보를 초기화한다.
					for (int Loop_i = 0; Loop_i < outRecSet.size(); Loop_i++) {
						
						//크레인작업재료 데이터의 레코드를 추출
						outRecord1 = JDTORecordFactory.getInstance().create();
						outRecord1 = outRecSet.getRecord(Loop_i);
					
						//적치단 재료상태가 적치 가능이면 재료 등록
						//적치단 테이블 업데이트
						//적치열구분 = 설비ID
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP"));
						recPara.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_BED_NO"));
						recPara.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_LYR_NO"));
						recPara.setField("YD_STK_LYR_MTL_STAT", "E");
						recPara.setField("STL_NO", 			    "");
						
						
						//업데이트 실행
						//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
						intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 303);
	
						//리턴값 메세지처리
						if(intRtnVal < 1) {
							szMsg = szMethodName + "적치단 업데이트 실행 에러!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
						}
					}	
				} 
        		
        		setRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6));   
        		setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8));
        		
                String szStkLyr = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i);
                
                setRecord.setField("YD_STK_LYR_NO", 		szStkLyr) ;
                setRecord.setField("STL_NO",              	szSTL_NO);                            
                setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");           
                //적치단dao를 호출해서 업데이트를 한다. 
                intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 0); 
    	        switch (intRtnVal) {
		        	case 0	:
		                szMsg = "<Y5RegYdStklyrCoil> Y5UpdYdStklyrCoil no data found!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
		        	case -1	:
		                szMsg = "<Y5RegYdStklyrCoil> Y5UpdYdStklyrCoil dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		        	case -2	:
		                szMsg = "<Y5RegYdStklyrCoil> Y5UpdYdStklyrCoil parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		        	case -3	:
		                szMsg = "<Y5RegYdStklyrCoil> Y5UpdYdStklyrCoil execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		        }

    	        //차량 하차작업 일 경우 공통테이블에 진도코드를 갱신한다.
    	        
    	        if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").equals("")){
    	        	sYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WO_LOC");
    	        } else {
    	        	sYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC");
    	        }
    	        
                szMsg = "차량 하차작업 일 경우 공통 테이블에 진도코드 갱신 YD_UP_WR_LOC : " + sYD_UP_WR_LOC;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
                /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	         * 코일소재일 경우에만 공통테이블을 갱신한다. -
    	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
                String sYD_SCH_CD = ydDaoUtils.paraRecChkNull(getRecord, "YD_SCH_CD");
                
               
    	        if( szYD_DN_WR_LOC.substring(0,1).equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)
    	        	&& (   sYD_UP_WR_LOC.substring(2, 4).equals("PT")
                		|| sYD_UP_WR_LOC.substring(2, 4).equals("TR") ) ){
    	        	
    	        	//차량동간 이적 인 경우 제외 
//    	        	 if(!sYD_SCH_CD.substring(2, 8).equals("TR05MM")&&
//    	        				!sYD_SCH_CD.substring(2, 8).equals("TR06MM")&&
//    	        				!sYD_SCH_CD.substring(2, 8).equals("TR15MM")&&
//    	        				!sYD_SCH_CD.substring(2, 8).equals("TR16MM")&&
//    	                		!sYD_SCH_CD.substring(2, 8).equals("TR07MM")&&
//    	                		!sYD_SCH_CD.substring(2, 8).equals("TR08MM")&&
//    	                		!sYD_SCH_CD.substring(2, 8).equals("TR17MM")&&
//    	                		!sYD_SCH_CD.substring(2, 8).equals("TR18MM")&&
//    	                		!sYD_SCH_CD.substring(2, 8).equals("TR57MM")&&
//    	                		!sYD_SCH_CD.substring(2, 8).equals("TR58MM")&&
//    	                		!sYD_SCH_CD.substring(2, 8).equals("TR67MM")&&
//    	                		!sYD_SCH_CD.substring(2, 8).equals("TR68MM")
//    	        				){
    	        	if(!("TR".equals(sYD_SCH_CD.substring(2 , 4)) && "MM".equals(sYD_SCH_CD.substring(6 , 8)))){	 
	    	        	//진도코드 갱신
	    	        	getRecord.setField("YD_MTL_ITEM",       	szYD_MTL_ITEM);
	    	        	//intRtnVal = this.Y5SetProgCodeCoil(getRecord) ;
	        	        //트렌젝션 분리
	        	        EJBConnector ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
	        	        ejbConn.trx("Y5SetProgCodeCoil", new Class[] { JDTORecord.class }, new Object[] { getRecord });
    	        	 }
         	        	
    	        } 
    	        
				

//반납/반송인 경우 진도를 바꾼다 J->B로 				
				if( (szYD_DN_WR_LOC.substring(0,1).equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD) &&
					((sYD_SCH_CD.equals("HAYD03UM")) ||
							 (sYD_SCH_CD.equals("HBYD03UM")) ||
							 (sYD_SCH_CD.equals("HBYD53UM")) ||
							 (sYD_SCH_CD.equals("HCYD03UM")) ||
							 (sYD_SCH_CD.equals("HCYD53UM")) ||
							 (sYD_SCH_CD.equals("HDYD03UM")) ||
					 (sYD_SCH_CD.equals("HEYD03UM")) ||
					 (sYD_SCH_CD.equals("HFYD03UM")) ||
					 (sYD_SCH_CD.equals("HGYD03UM")) ||
					 (sYD_SCH_CD.equals("HHYD03UM")) ||
					 (sYD_SCH_CD.substring(6, 8).equals("UJ")) ||
					 (sYD_SCH_CD.equals("HAYD04UM")) ||
					 (sYD_SCH_CD.equals("HBYD04UM")) ||
					 (sYD_SCH_CD.equals("HBYD54UM")) ||
					 (sYD_SCH_CD.equals("HCYD04UM")) ||
					 (sYD_SCH_CD.equals("HCYD54UM")) ||
					 (sYD_SCH_CD.equals("HDYD04UM")) ||
					 (sYD_SCH_CD.equals("HEYD04UM")) ||
					 (sYD_SCH_CD.equals("HFYD04UM")) ||
					 (sYD_SCH_CD.equals("HGYD04UM")) ||
					 (sYD_SCH_CD.equals("HHYD04UM")) ||
					 (sYD_SCH_CD.substring(6, 8).equals("UB")) 
					 ))
					||
	        		( //소재야드에서 차량동간이적으로 직접 반품을 받는 경우
	        		 ("TR1".equals(sYD_SCH_CD.substring(2 , 5)))
	        		 && szYD_DN_WR_LOC.substring(0,1).equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)	
	        		 && ("J".equals(szCURR_PROG_CD) || "5".equals(szCURR_PROG_CD))
	        		) 
				
				   ) {

	                szMsg = "<Y5RegYdStklyrCoil> 진도변경처리 함!!";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	  
					
					//진도코드 갱신
    	        	getRecord.setField("YD_MTL_ITEM",       	szYD_MTL_ITEM);  //필요 없지만 기냥
  					
	       	        //트렌젝션 분리
        	        EJBConnector ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
        	        ejbConn.trx("Y5SetProgCodeCoil", new Class[] { JDTORecord.class }, new Object[] { getRecord });
        	        
        	        
        	        
        	        /**********************************************************
        	        * 정정작업메세지이력등록 시작
        	        **********************************************************/
        	        JDTORecord rcvMsgArgs = JDTORecordFactory.getInstance().create();
        	        rcvMsgArgs.setField("COIL_NO"         , szSTL_NO);
        	        rcvMsgArgs.setField("SHEAR_WRK_MSG_GP", sSNDBK_GP);
        	        rcvMsgArgs.setField("MSG_CONTENTS"    , sSNDBK_GP_ETC);
        	        rcvMsgArgs.setField("userid"          , sSNDBK_REGISTER);
//        	        EJBConnector ejbConn2 = new EJBConnector("hsteelApp", "HrCommHdSeEJB", this);
        	        EJBConnector ejbConn2 = new EJBConnector("hsteelApp", "HrCommMgtFaEJB", this);
        	        ejbConn2.trx("insHrShrMsgLog", new Class[] { JDTORecord.class }, new Object[] { rcvMsgArgs });
        	        

        	 
				}    
    	        
    	           
    	        //저장위치 갱신				공통테이블에 저장위치를 갱신한다.H F 10 01 01
				setRecord 	= JDTORecordFactory.getInstance().create();
				setRecord.setField("YD_SCH_CD",       		ydDaoUtils.paraRecChkNull(getRecord, "YD_SCH_CD"));   
				setRecord.setField("YD_GP",       			szYD_DN_WR_LOC.substring(0,1));   
				setRecord.setField("YD_BAY_GP",       		szYD_DN_WR_LOC.substring(1,2));   
				setRecord.setField("YD_EQP_GP",       		szYD_DN_WR_LOC.substring(2,4)); 
				setRecord.setField("YD_STK_COL_NO",       	szYD_DN_WR_LOC.substring(4,6));   
				setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8)); 
				setRecord.setField("YD_STK_LYR_NO", 		szStkLyr) ;
				setRecord.setField("SLAB_NO",       		ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
				setRecord.setField("MSLAB_NO",       		ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
				setRecord.setField("PLATE_NO",       		ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
				setRecord.setField("COIL_NO",       		ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
				setRecord.setField("YD_MTL_ITEM",       	szYD_MTL_ITEM);
				setRecord.setField("YD_DN_WR_LOC",       	ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC")); 
							
				ydUtils.displayRecord(szOperationName, setRecord);
    	        //intRtnVal = this.Y5SetYdStrLocCoil(setRecord) ;
    	        //트렌젝션 분리
    	        EJBConnector ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
    	        ejbConn.trx("Y5SetYdStrLocCoil", new Class[] { JDTORecord.class }, new Object[] { setRecord });
	   
    	        
    	        System.out.println("NO3=========================================================");
    	        
                getRecSet.next();
                getRecord = getRecSet.getRecord();
        	
        	}//end of for
        	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal ;
	    }//end of try~catch
		
		intRtnVal = 1 ;
		return intRtnVal ;
    
    }//end of Y5RegYdStklyrCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차 Setting
     *  
     * @param  ● inRecordSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public String Y5SetYdTcarCoil (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	    	
    	//대차 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	int intRtnVal 						= 0 ;
    	
    	String szMethodName 				= "Y5etYdTcarCoil";
    	String szMsg 						= "";
    	String szOperationName              = "C열연 대차 Setting";
    	//대차 스케줄 ID
    	String szYD_TCAR_SCH_ID 			= "";
	    String sTO_LOC                      = "";
    	
    	int iTC_CURR_CNT = 0;
	    int iTC_CURR_WT  = 0;
   	
    	
    	try{
    		// intGp == 1만 처리됨
    		// 크레인스케줄 Data
	    	inRecordSet.first();
	    	getRecord = inRecordSet.getRecord();

	    	
			setRecord.setField("YD_BOOK_ID"	, ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID")) ;
	    	setRecord.setField("STL_NO"		, ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")) ;
	
	    	//하차 작업 예약 ID	Setting
	    	if(intGp == 0) {
//	    		/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschUD*/
//		    	intRtnVal = ydTcarschDao.getYdTcarsch(setRecord, outRecSet, 201);
//		    	if (intRtnVal <= 0) {
//		            throw new DAOException("<Y5etYdTcarCoil> " +  "상하차 작업예약 ID로 대차스케줄 조회" + intRtnVal + "건수");
//		    	}
				    	
		    //상차 작업 예약 ID	Setting
	    	}else if(intGp == 1) {
	    		/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschLD*/
		    	intRtnVal = ydTcarschDao.getYdTcarsch(setRecord, outRecSet, 200);
		    	if (intRtnVal <= 0) {
		            throw new DAOException("<Y5etYdTcarCoil> " +  "상하차 작업예약 ID로 대차스케줄 조회" + intRtnVal + "건수");
		    	}
	    	}
	    	
	    	
//	    	// 상하차 작업예약 ID로 대차스케줄 조회
//	    	intRtnVal = this.Y5GetYdTcarschCoil(setRecord, outRecSet, 1) ;
//	    	if (intRtnVal <= 0) {
//	            throw new DAOException("<Y5etYdTcarCoil> " +  "상하차 작업예약 ID로 대차스케줄 조회" + intRtnVal + "건수");
//	    	}
//	    	
	    	
	    	// 대차스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 대차스케줄 ID를 추출한다
	    	szYD_TCAR_SCH_ID= ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_TCAR_SCH_ID");
//	    	iTC_CURR_CNT 	= getTcarRecord.getFieldInt("YD_EQP_WRK_SH") + 1;
//	    	iTC_CURR_WT  	= getTcarRecord.getFieldInt("YD_EQP_WRK_WT") + getTcarRecord.getFieldInt("COIL_WT");
	    	
	    	String sYD_AIM_BAY_GP	= ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_AIM_BAY_GP");
	    	String sYD_GP			= ydDaoUtils.paraRecChkNull(getRecord, "YD_GP");
	    	String sYD_EQP_ID		= ydDaoUtils.paraRecChkNull(getTcarRecord, "YD_EQP_ID");
	    	
	    	sTO_LOC  				= sYD_GP + sYD_AIM_BAY_GP+sYD_EQP_ID.substring(2, 6);
	    	
	    	//setRecord 초기화
	    	setRecord 		= JDTORecordFactory.getInstance().create();
	    	int szRowSize 	= inRecordSet.size(); 
	    	
	    	

	    	// 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  

	    	for(int i = 0; i < szRowSize; i++){
		    	
	    		// 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_TCAR_SCH_ID"	,	szYD_TCAR_SCH_ID);
	    		setRecord.setField("STL_NO"			,  	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//대차 이송재료 등록 (하차 )
	    		if(intGp == 0) {
	    			setRecord.setField("MODIFIER"	,	szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN"		,	"Y");
		    		intRtnVal = this.Y5UpdTcarftmvmtlCoil(setRecord, 0) ;
		    		if(intRtnVal == -1) {
	    				szMsg="대차이송재료 삭제 처리중 Error!!";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			throw new DAOException("<Y5etYdTcarCoil> " + szMsg);
		    		}
			    	 
			    //대차 이송재료 등록 (상차 )	
	    		}else if(intGp == 1) {
	    			setRecord.setField("REGISTER"		,	szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("YD_STK_BED_NO"	,   ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO"	,   "001") ;
		    		intRtnVal = this.Y5InsYdTcarftmvmtlCoil(setRecord) ;
		    		if(intRtnVal == -1) {
	    				szMsg="대차이송재료 등록 처리중 Error!!";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	            throw new DAOException("<Y5etYdTcarCoil> " + szMsg);
		    		}
	    		}
		    	
	    		inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	
	    	
	    	if(intGp == 1) {
	    		szMsg="대차이송재료 등록 후 대차스케줄에 상차완료시간 및  대차설비상태 영차로 등록";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		
	    		setRecord = JDTORecordFactory.getInstance().create();
		    	
		    	setRecord.setField("YD_TCAR_SCH_ID"		, szYD_TCAR_SCH_ID);
		    	setRecord.setField("YD_EQP_WRK_STAT"	, "L"); 
//		    	setRecord.setField("YD_EQP_WRK_SH"  	, Integer.toString(iTC_CURR_CNT)); 
//		    	setRecord.setField("YD_EQP_WRK_WT"  	, Integer.toString(iTC_CURR_WT)); 
		    	setRecord.setField("YD_CARUD_STOP_LOC"  , sTO_LOC ); 
	    		
	    		intRtnVal = ydTcarSchDao.updYdTcarsch(setRecord, 0);
				if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="ydTcarSchDao data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	    			}else if(intRtnVal == -1) {
	    				szMsg="ydTcarSchDao duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="ydTcarSchDao parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="ydTcarSchDao execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    		}
	    		
	    	}
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new DAOException("<Y0SetYdTcar> " + szMsg);
		}//end of try~catch
    	
		return szYD_TCAR_SCH_ID;
    	
    }//end of Y5etYdTcarCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차 스케줄 Select
     *  
     * @param  ● msgRecord, outRecset, intGp
     * @return ● intRtnVal
     * @throws ● 
     */
    public int Y5GetYdTcarschCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
	        intRtnVal = ydTcarschDao.getYdTcarsch(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	        	return intRtnVal;
	        }
	        outRecset.addAll(getRecSet)  ;  
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }
        return intRtnVal ;
    }//end of Y5GetYdTcarschCoil
    

    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차이송재료 Update
     *  
     * @param inRecord, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y5UpdTcarftmvmtlCoil (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdTcarFtmvMtlDao ydTcarftmvmtlDao   = new YdTcarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydTcarftmvmtlDao.updYdTcarftmvmtl(inRecord, intGp) ;
            
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -3 ;
			return intRtnVal ;
	    }	
		
		return intRtnVal ;
    }//end of Y5UpdTcarftmvmtlCoil
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차이송재료 Insert
     *  
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y5InsYdTcarftmvmtlCoil(JDTORecord msgRecord){
    	YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();
    	
    	int intRtnVal 			= 0 ;

        
        try{
        	
        	intRtnVal = ydTcarftmvmtlDao.insYdTcarftmvmtl(msgRecord);
        	if(intRtnVal == -2) return intRtnVal ;
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }
        return intRtnVal ;
    	
    }//end of Y5InsYdTcarftmvmtlCoil
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 대차스케줄 Update
     *  
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws 
     */
    public int Y5UpdYdTcarschCoil (JDTORecord msgRecord, int intGp){
    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydTcarschDao.updYdTcarsch(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	        	return -2;
	        }
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return 1 ;
    	
    }//end of Y5UpdYdTcarschCoil

    /**
     * 오퍼레이션명 : 차량 Setting
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y5SetYdCarCoil (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
    	JDTORecord setRecord 		= JDTORecordFactory.getInstance().create();
    	JDTORecord getRecord 		= JDTORecordFactory.getInstance().create();
    	JDTORecord getTcarRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecordSet outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMethodName 		= "Y5SetYdCarCoil";
    	String szMsg 				= "";
    	String szOperationName      = "C열연 차량 Setting";
    	String szYD_CAR_SCH_ID 		= "";
    	long lngYD_MTL_WT           = 0;
    	int  intYD_MTL_SH           = 0;
    	long lngYD_EQP_WRK_WT       = 0;
    	int  intYD_EQP_WRK_SH       = 0;
    	
    	int intRtnVal 				= 0 ;
    	
    	try{
    		
	    	// 크레인스케줄 Data
	    	inRecordSet.first();
	    	getRecord = inRecordSet.getRecord();

	    	
	    	//하차 작업 예약 ID	Setting
	    	if(intGp == 0) {
				setRecord.setField("YD_CARUD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
		    	setRecord.setField("YD_CARLD_WRK_BOOK_ID", "") ;
		    	
		    //상차 작업 예약 ID	Setting
	    	}else if(intGp == 1) {
				setRecord.setField("YD_CARLD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
		    	setRecord.setField("YD_CARUD_WRK_BOOK_ID", "") ;
	    	}
	    	
	    	
	    	// 상하차 작업예약 ID로 대차스케줄 조회
	    	/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschUDLDWRKBOOKID_PIDEV*/

//PIDEV_S :병행가동용:PI_YD
	    	setRecord.setField("PI_YD",    	"J");				    	
	    	intRtnVal = this.Y5GetYdCarschCoil(setRecord, outRecSet, 3) ;
	    	if (intRtnVal <= 0) return -1 ;
	    	
	    	
	    	// 차량스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 차량스케줄 ID를 추출한다
	    	szYD_CAR_SCH_ID  = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
	    	lngYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullLong(getTcarRecord, "YD_EQP_WRK_WT");
	    	intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(getTcarRecord, "YD_EQP_WRK_SH");
	    	
	    	//setRecord 초기화
	    	setRecord		= JDTORecordFactory.getInstance().create();
	    	int szRowSize 	= inRecordSet.size(); 

	    	// 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  

	    	for(int i = 0; i < szRowSize; i++){
		    	// 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_CAR_SCH_ID",	szYD_CAR_SCH_ID);
	    		setRecord.setField("STL_NO",     	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	        	
	    		lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(getRecord, "YD_MTL_WT");
	        	intYD_MTL_SH = i + 1;
	        	
	    		//차량 이송재료 등록 (하차 )
	    		if(intGp == 0) {
	    			setRecord.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO", "001") ;
	    			setRecord.setField("MODIFIER",	szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",	"Y");
		    		/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtl*/
		    		intRtnVal = this.Y5UpdCarftmvmtlCoil(setRecord, 0) ;
			    	switch (intRtnVal) {
			        	case 0	:
			                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
			                return intRtnVal;
			        	case -1	:
			                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        	case -2	:
			                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return intRtnVal;
			        	case -3	:
			                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        	case -4	:
			                szMsg = "[" + szOperationName + "] Exception error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        }
			    	
			    //차량 이송재료 등록 (상차 )	
	    		}else if(intGp == 1) {
	    			setRecord.setField("REGISTER",			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",        "N");
		    		setRecord.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO", "001") ;
		    		setRecord.setField("HCR_GP",	    ydDaoUtils.paraRecChkNull(getRecord,"HCR_GP"));
		    		setRecord.setField("STL_PROG_CD",	ydDaoUtils.paraRecChkNull(getRecord,"STL_PROG_CD"));
		    		setRecord.setField("YD_MTL_ITEM",	ydDaoUtils.paraRecChkNull(getRecord,"YD_MTL_ITEM"));
		    		setRecord.setField("YD_ROUTE_GP",	ydDaoUtils.paraRecChkNull(getRecord,"YD_ROUTE_GP"));
		    		
		    		/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.insYdCarftmvmtl*/  
		    		intRtnVal = this.Y5InsYdCarftmvmtlCoil(setRecord) ;
			    	switch (intRtnVal) {
			        	case -2	:
			                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return intRtnVal;
			        	case -3	:
			                szMsg = "[" + szOperationName + "] Exception error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        }
	    		}
		    	inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	
	    	if(intGp == 1) {
    			//차량스케줄에 등록한다.
    			lngYD_EQP_WRK_WT = lngYD_EQP_WRK_WT + lngYD_MTL_WT;
    			intYD_EQP_WRK_SH = intYD_EQP_WRK_SH + intYD_MTL_SH;
    	    	//setRecord 초기화
    	    	setRecord	= JDTORecordFactory.getInstance().create();
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("YD_EQP_WRK_WT",       	"" + lngYD_EQP_WRK_WT);
	    		setRecord.setField("YD_EQP_WRK_SH",       	"" + intYD_EQP_WRK_SH);
	    		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch*/
	    		intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
	    		switch (intRtnVal) {
		        	case 0	:
		                szMsg = "[" + szOperationName + "] data not found!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);	  
		                return intRtnVal;
		        	case -1	:
		                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return intRtnVal;
		        	case -2	:
		                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return intRtnVal;
		        	case -3	:
		                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return intRtnVal;
		        	case -4	:
		                szMsg = "[" + szOperationName + "] Exception error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return intRtnVal;
		        }
    		}
		    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return intRtnVal = -1 ;
		}//end of try~catch
		
    	return intRtnVal = 1 ;
    	
    }//end of Y5SetYdCarCoil()

    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *  
     * @param msgRecord, outRecset, intGp
     * @return intRtnVal
     * @throws 
     */
    public int Y5GetYdCarschCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) return -2;
	        
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return intRtnVal = -2 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y5GetYdCarschCoil
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 차량 이송재료 Update
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y5UpdCarftmvmtlCoil (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
		
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal ;
	    }//end of try~catch	
		
		return intRtnVal ;
		
    }//end of Y5UpdCarftmvmtlCoil
    

    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *  
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y5InsYdCarftmvmtlCoil(JDTORecord msgRecord){
    	YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
    	
    	int intRtnVal = 0 ;

        
        try{
        	intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
        	if(intRtnVal <= 0) return intRtnVal ;
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
    	
    }//end of Y5InsYdCarftmvmtlCoil


    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 차량스케줄 Update
     *  
     * @param msgRecord, intGp
     * @return getRecSet
     * @throws 
     */
    public JDTORecordSet Y5UpdYdCarschCoil (JDTORecord msgRecord, int intGp){
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "Y5UpdYdCarschCoil";
        String szOperationName              = "C열연 차량스케줄 Update";
        try{
        	
        	intRtnVal = ydCarschDao.updYdCarsch(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return getRecSet ;
    	
    }//end of Y5UpdYdCarschCoil
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 작업예약 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5UpdYdWrkbookCoil (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdWrkbookDao ydWrkbookDao   = new YdWrkbookDao(); 
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydWrkbookDao.updYdWrkbook(msgRecord, intGp);

		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -4 ;
			return intRtnVal ;
	    }//end of try~catch
		
		return intRtnVal ;
    }//end of Y5UpdYdWrkbookCoil
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5UpdYdStklyrCoil (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	int intRtnVal = 0 ;
        
        try{
        	
	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
	    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y5UpdYdStklyrCoil
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y5GetYdStklyrCoil (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
        try{
        	
	        intRtnVal = ydStklyrDao.getYdStklyr(msgRecord, getRecSet, intGp);  
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
        }//end of try~catch

        return intRtnVal ;
    	
    }//end of Y5GetYdStklyrCoil()
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 진도코드갱신 Setting 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     * @ejb.transaction type="RequiresNew"
     */
    public int Y5SetProgCodeCoil (JDTORecord msgRecord){
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  inRecord2 			= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  recGetVal 			= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  recInTemp 			= JDTORecordFactory.getInstance().create();
    	JDTORecordSet getRecSet1 			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szMsg						= "";
    	String szMethodName					= "Y5SetProgCodeCoil";
    	//재료품목 정의
    	//String szYdMtlItem					= "";
    	//재료종류별 번호
    	String szStlNo						= "";
    	int intRtnVal 						= 0 ;
        
        try{
        	
        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
        	//szYdMtlItem = msgRecord.getFieldString("YD_MTL_ITEM").substring(0,1);
        	szStlNo 	= msgRecord.getFieldString("STL_NO") ;
    
        	
			getRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");
			inRecord2 = JDTORecordFactory.getInstance().create();			
			inRecord2.setField("STL_NO", szStlNo);
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarMtlCoilComm*/
			intRtnVal = ydCarSchDao.getYdCarsch(inRecord2, getRecSet1, 305);	
			if(intRtnVal < 0) {
				szMsg = "COIL공통 테이블 조회오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
			} else if(intRtnVal == 0) {
				szMsg = "COIL공통 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
			}				
			
			getRecSet1.first();
			recGetVal = getRecSet1.getRecord(0);	
			String sORD_YEOJAE_GP =	ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP");
			String sCURR_PROG_CD =	ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD");
			
			szMsg = "COIL공통 테이블 sORD_YEOJAE_GP : " + sORD_YEOJAE_GP + "] sCURR_PROG_CD:" +sCURR_PROG_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
			
			//2010.11.04 보류재 인경우 진도변경을 안한다 : 이진형 과장 요청 
			if(!sCURR_PROG_CD.equals("F")){
				if(sORD_YEOJAE_GP.equals("1")){
					setRecord.setField("CURR_PROG_CD", "B");
				} else {
					setRecord.setField("CURR_PROG_CD", "Y");
				}
	        	//전 진도코드 = 현재 진도코드,   전전진도코드 = 전진도코드
				//현재시간
	    		setRecord.setField("COIL_NO", szStlNo);
	     	//	setRecord.setField("CURR_PROG_CD", "B");
				setRecord.setField("CURR_PROG_REG_DDTT", 			YdUtils.getCurDate("yyyyMMddHHmmss"));
				setRecord.setField("FNL_REG_PGM", 					szMethodName);
				setRecord.setField("CURR_PROG_CD_REG_PGM", 		    szMethodName);
				setRecord.setField("MODIFIER", 					    "YDSYSTEM");
				
	
	    		intRtnVal = this.Y5UpdPtCommCoil_PROG_CD(setRecord,  3);
	    		//에러 메시지
			}
	    		
		if(intRtnVal<0) return intRtnVal ;
			
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y5SetProgCodeCoil()
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 코일공통update -저장위치 Setting 
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     * @ejb.transaction type="RequiresNew"
     */
    public int Y5SetYdStrLocCoil (JDTORecord msgRecord){
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//현재저장위치
    	String szYdStrLoc					= "";
    	//이전저장위치
    	String szYdStrLocHis1				= "";

		
    	String szMsg						= "";
    	String szMethodName					= "Y5SetYdStrLocCoil";
    	//재료품목 정의
    	String szYdMtlItem					= "";
    	
    	//크레인스케줄의 정보				현재저장위치를 생성하기 위해...
    	String szYdGp						= "";
    	String szYdBayGp					= "";
    	String szYdEqpId					= "";
    	String szYdStkColNo					= "";
    	String szYdStkBedNo					= "";
    	String szYdStkLyrNo					= "";
    	String szYdSchCd					= "";
    	String szGpackFlag					= "";
    	
    	int intRtnVal 						= 0 ;
        
        try{



        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
//?			szYdMtlItem = msgRecord.getFieldString("YD_MTL_ITEM").substring(0, 1) ;
			

    		//코일 공통
    		intRtnVal = this.Y5GetYdStockCoil(msgRecord, getRecSet, 8);
    		//에러 메시지
    		//
    		if(intRtnVal<0) return intRtnVal ;

        	 
        	getRecSet.first();
        	getRecord 			= getRecSet.getRecord() ;
        	
        	//변경 전 코일 공통 피수 값 출력
			System.out.println("@@@@@@코일공통 조회 결과COIL_NO : " +ydDaoUtils.paraRecChkNull(getRecord, "COIL_NO"));  
			System.out.println("@@@@@@코일공통 조회 결과SKINPASS_YN : " +ydDaoUtils.paraRecChkNull(getRecord, "SKINPASS_YN")); 
			System.out.println("@@@@@@코일공통 조회 결과NEXT_PROC : " +ydDaoUtils.paraRecChkNull(getRecord, "NEXT_PROC"));  
			System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD1 : " +ydDaoUtils.paraRecChkNull(getRecord, "MID_INSPECT_DEFECT_CD1"));  
			System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD2 : " +ydDaoUtils.paraRecChkNull(getRecord, "MID_INSPECT_DEFECT_CD2"));  
			System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD3 : " +ydDaoUtils.paraRecChkNull(getRecord, "MID_INSPECT_DEFECT_CD3")); 
			System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD4 : " +ydDaoUtils.paraRecChkNull(getRecord, "MID_INSPECT_DEFECT_CD4"));  
			System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD5 : " +ydDaoUtils.paraRecChkNull(getRecord, "MID_INSPECT_DEFECT_CD5")); 
			System.out.println("@@@@@@코일공통 조회 결과 지포장여부 : " +ydDaoUtils.paraRecChkNull(getRecord, "GPACK_FLAG")); 
			
        	
        	szYdStrLoc 			= ydDaoUtils.paraRecChkNull(getRecord, "YD_STR_LOC");
        	szYdStrLocHis1 		= ydDaoUtils.paraRecChkNull(getRecord, "YD_STR_LOC_HIS1");

        	szYdGp 				= msgRecord.getFieldString("YD_GP"); 
        	szYdBayGp 			= msgRecord.getFieldString("YD_BAY_GP");
        	szYdEqpId 			= msgRecord.getFieldString("YD_EQP_GP"); 
        	szYdStkColNo 		= msgRecord.getFieldString("YD_STK_COL_NO"); 
        	szYdStkBedNo 		= msgRecord.getFieldString("YD_STK_BED_NO"); 
        	szYdStkLyrNo		= msgRecord.getFieldString("YD_STK_LYR_NO");
        	szYdSchCd			= msgRecord.getFieldString("YD_SCH_CD");
        	szGpackFlag			= ydDaoUtils.paraRecChkNull(getRecord, "GPACK_FLAG");
        	
        	if(szYdStkLyrNo.length()==2){
        		szYdStkLyrNo="0"+szYdStkLyrNo;
        	}
	        
        	setRecord.setField("YD_GP",         szYdGp);
        	setRecord.setField("YD_BAY_GP",     szYdBayGp);
        	setRecord.setField("YD_EQP_GP",     szYdEqpId);
        	setRecord.setField("YD_STK_COL_NO", szYdStkColNo);
        	setRecord.setField("YD_STK_BED_NO", szYdStkBedNo);
        	setRecord.setField("YD_STK_LYR_NO", szYdStkLyrNo);
        	setRecord.setField("FNL_REG_PGM",   "Y5setYdStrLocCoil");
        	setRecord.setField("MODIFIER",      "YDSYSTEM");
        	
        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc) ;
        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1) ;
        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우
        	
        	         	
        	setRecord.setField("COIL_NO",   ydDaoUtils.paraRecChkNull(getRecord, "COIL_NO")); 
    		setRecord.setField("YD_STR_LOC", szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo+szYdStkBedNo+szYdStkLyrNo.substring(1,3)) ;
    		//공통 업데이트
    		intRtnVal = this.Y5UpdPtCommCoil(setRecord,  3);
    		//에러메시지
    		//
    		if(intRtnVal<0) return intRtnVal ;

        	//지포장 재반입
    		if(("HBGF01LM".equals(szYdSchCd)|| "HCGF01LM".equals(szYdSchCd)|| "HEGF01LM".equals(szYdSchCd)||"HFGF01LM".equals(szYdSchCd)|| "HHGF01LM".equals(szYdSchCd))
    			&& "Y".equals(szGpackFlag) && "H".equals(szYdGp)){
    			
    			//저장품의 행선을 GO으로 변경 한다. 
    			setRecord.setField("STL_NO"			, ydDaoUtils.paraRecChkNull(getRecord, "COIL_NO")); 
				setRecord.setField("YD_AIM_RT_GP"		, "G0");
				
				// 목표동 수정
				EJBConnector ejbConn = null;
				ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
				ejbConn.trx("updCoilYdRetCrnReg", new Class[] { JDTORecord.class }, new Object[] { setRecord });
		 	
    		}
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y5SetYdStrLocCoil()
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 저장품 Select
     *  
     * @param msgRecord, outRecset, intGp
     * @return intRtnVal
     * @throws 
     */
    public int Y5GetYdStockCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdStockDao ydStockDao = new YdStockDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "";
    	String szMethodName		= "Y5GetYdStockCoil";
    	String szOperationName              = "C열연 저장품 Select";
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydStockDao.getYdStock(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y5GetYdStockCoil()
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 저장품 Update
     *  
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws 
     */
    public int Y5UpdPtCommCoil (JDTORecord msgRecord, int intGp){
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "Y5UpdPtCommCoil";
        String szOperationName              = "C열연 저장품 Update";
        try{
        	
        	intRtnVal = ydStockDao.updPtComm_LOC(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return  intRtnVal;
    	
    }//end of Y5UpdPtCommCoil()
    
    
    /**
     * 오퍼레이션명 : C열연 저장품 Update
     *  
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws 
     */
    public int Y5UpdPtCommCoil_PROG_CD (JDTORecord msgRecord, int intGp){
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "Y5UpdPtCommCoil_PROG_CD";
        String szOperationName = "C열연 저장품 Update";
        try{
        	
        	intRtnVal = ydStockDao.updPtComm_PROG_CD(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return  intRtnVal;
    	
    }//end of Y5UpdPtCommCoil()
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : C 열연 강제권하 가능 여부
     *  
     * @param JDTORecord
     * @return intRtnVal
     * @throws JDTOException
     */
    public String Y5ForCedUdCoil (String sSTL_NO, JDTORecord msgRecord) throws JDTOException{
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
    	JDTORecord 	  inRecord		= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  inRecord1		= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  outRecord1 	= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  getTcarRecord = JDTORecordFactory.getInstance().create();
    	JDTORecordSet outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("temp");
    	CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
    	
    	String szMethodName 		= "Y5ForCedUdCoil";
    	String szMsg 				= "";
    	String szOperationName      = "C열연 강제 권하 가능 여부";
    	String sYD_DN_WR_LOC 		= "";
    	String sYD_DN_WR_LAYER 		= "";
    	String sYD_STK_COL_GP       = "";
    	String sYD_STK_BED_NO       = "";
    	String sYD_STK_LYR_NO       = "";
    	String szRtnVal             = "";
    	String szYD_GP				= "";
    	
    	int intRtnVal = 0 ;
    	
    	try{

    		sYD_DN_WR_LOC 	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC");
			sYD_DN_WR_LAYER = ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER");
			sYD_STK_COL_GP  = sYD_DN_WR_LOC.substring(0, 6);
			sYD_STK_BED_NO  = sYD_DN_WR_LOC.substring(6, 8);
			sYD_STK_LYR_NO  = sYD_DN_WR_LAYER;
			szYD_GP			= sYD_DN_WR_LOC.substring(0, 1);
			
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("YD_STK_COL_GP" 	, sYD_STK_COL_GP);	
			inRecord.setField("YD_STK_BED_NO" 	, sYD_STK_BED_NO);	
			inRecord.setField("YD_STK_LYR_NO" 	, sYD_STK_LYR_NO);	

			
			outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
			szMsg = "[JSP Session] " + szOperationName +  "적치단 정보조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 0);
			if (intRtnVal == 0){

				szMsg = "저장위치 이상";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szRtnVal = szMsg ;	

			} else if ( intRtnVal > 0 )	{
				inRecord1   	= JDTORecordFactory.getInstance().create();
				outRecSet.first();
				inRecord1 	= outRecSet.getRecord();
				
				String sYD_STK_LYR_MTL_STAT	= ydDaoUtils.paraRecChkNull(inRecord1, "YD_STK_LYR_MTL_STAT"); 
				ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_MTL_STAT:" + sYD_STK_LYR_MTL_STAT, YdConstant.DEBUG);
				if (sYD_STK_LYR_MTL_STAT.equals("E")){
				
				} else {	

					szMsg = "제품이 있거나 예약상태 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return szRtnVal = szMsg ;	
				
				}
				
			}
			
			if(szYD_GP.equals("H")){
				szMsg = "소재야드 CoilLyrBaseCheck 체크";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord1 = ydToLocDcsnUtil.CoilLyrBaseCheck(sSTL_NO, inRecord);
			}else{
				szMsg = "제품야드 CoilGdsLyrBaseCheck 체크";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sSTL_NO, inRecord);
			}
			
			String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			String sRTN_MSG1	= sRTN_MSG.length() > 40 ? sRTN_MSG.substring(0, 40) : sRTN_MSG;
			if (!("1".equals(sRTN_CD))) {
				szMsg = "[JSP Session] " + szOperationName +  "2단적치 기준에 걸립니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szRtnVal = sRTN_MSG1 ;
			}
					    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return szRtnVal = YdConstant.RETN_CD_FAILURE ;
		}//end of try~catch
		
    	return szRtnVal = YdConstant.RETN_CD_SUCCESS ;
    	
    }//end of Y5ForCedUdCoil()

    /**
    * 오퍼레이션명 : C 열연 강제권하 가능 여부
    *  
    * @param JDTORecord
    * @return intRtnVal
    * @throws JDTOException
    */
   public String Y5ForCedUdCoilGds (String sSTL_NO, JDTORecord msgRecord) throws JDTOException{
	YdCarSchDao ydCarSchDao = new YdCarSchDao();
   	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
   	JDTORecord 	  inRecord		= JDTORecordFactory.getInstance().create();
   	JDTORecord 	  inRecord1		= JDTORecordFactory.getInstance().create();
   	JDTORecord 	  outRecord1 	= JDTORecordFactory.getInstance().create();
   	JDTORecord 	  getTcarRecord = JDTORecordFactory.getInstance().create();
   	JDTORecordSet outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("temp");
   	CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
   	
   	String szMethodName 		= "Y5ForCedUdCoilGds";
   	String szMsg 				= "";
   	String szOperationName      = "C열연 ABC 강제 권하 가능 여부";
   	String sYD_DN_WR_LOC 		= "";
   	String sYD_DN_WR_LAYER 		= "";
   	String sYD_STK_COL_GP       = "";
   	String sYD_STK_BED_NO       = "";
   	String sYD_STK_LYR_NO       = "";
   	String szRtnVal             = "";
   	
   	int intRtnVal = 0 ;
   	
   	try{

   		sYD_DN_WR_LOC 	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC");
			sYD_DN_WR_LAYER = ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER");
			sYD_STK_COL_GP  = sYD_DN_WR_LOC.substring(0, 6);
			sYD_STK_BED_NO  = sYD_DN_WR_LOC.substring(6, 8);
			sYD_STK_LYR_NO  = sYD_DN_WR_LAYER;
			
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("YD_STK_COL_GP" 	, sYD_STK_COL_GP);	
			inRecord.setField("YD_STK_BED_NO" 	, sYD_STK_BED_NO);	
			inRecord.setField("YD_STK_LYR_NO" 	, sYD_STK_LYR_NO);	

			
			outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
			szMsg = "[JSP Session] " + szOperationName +  "적치단 정보조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 0);
			if (intRtnVal == 0){

				szMsg = "저장위치 이상";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szRtnVal = szMsg ;	

			} else if ( intRtnVal > 0 )	{
				inRecord1   	= JDTORecordFactory.getInstance().create();
				outRecSet.first();
				inRecord1 	= outRecSet.getRecord();
				
				String sYD_STK_LYR_MTL_STAT	= ydDaoUtils.paraRecChkNull(inRecord1, "YD_STK_LYR_MTL_STAT"); 
				ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_MTL_STAT:" + sYD_STK_LYR_MTL_STAT, YdConstant.DEBUG);
				if (sYD_STK_LYR_MTL_STAT.equals("E")){
				
				} else {	

					szMsg = "제품이 있거나 예약상태 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return szRtnVal = szMsg ;	
				
				}
				
			}
			outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheckABC(sSTL_NO, inRecord);
			String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			String sRTN_MSG1	= sRTN_MSG.length() > 40 ? sRTN_MSG.substring(0, 40) : sRTN_MSG;
			if (!("1".equals(sRTN_CD))) {
				szMsg = "[JSP Session] " + szOperationName +  "2단적치 기준에 걸립니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szRtnVal = sRTN_MSG1 ;
			}
					    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return szRtnVal = YdConstant.RETN_CD_FAILURE ;
		}//end of try~catch
		
   	return szRtnVal = YdConstant.RETN_CD_SUCCESS ;
   	
   }//end of Y5ForCedUdCoil()
    
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
		
		
		try {	
			
			StringBuffer sbf = new StringBuffer();			
			
			// 프로퍼티 서비스 인스턴스를 취득합니다.
			propertyService = PropertyService.getInstance();
			
			// JDTORecord인스턴스 객체 취득
			insRecord = JDTORecordFactory.getInstance().create();			
					
			String JMS_TC_CD	    	 = StringHelper.evl(param.getFieldString("JMS_TC_CD"), "");				//JMS전문 ID		8
			String Message = "";
			String szWkGp  = JMS_TC_CD.substring(2,4);
			
				// 큐 명칭을 프로퍼티로부터 취득합니다.
			queueName = propertyService.getProperty("common.properties","jms.queue."+szWkGp+"_MDB_QUEUE");	
		
			sender = new JmsQueueSender();			
			sender.initQueueService(queueName);		
	
			sender.send(param);

		}catch (Exception e) {
		}finally {
			try {
				sender.closeAll();
			} catch (Exception e) {
			}
		}
	}        
    
	   /**
     * 오퍼레이션명 : 이송시 YM으로 STOCK INSERT  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     * 
     */
    public int Y5insYmStockCoil (JDTORecord msgRecord){
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
    	//Update할 레코드 셋팅
    	JDTORecord 	  inRecord2 			= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  inRecord1 			= JDTORecordFactory.getInstance().create();
    	JDTORecord 	  recOutTemp 			= JDTORecordFactory.getInstance().create();
    	JDTORecordSet getRecSet1 			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szMsg						= "";
    	String szMethodName					= "Y5insYmStockCoil";
    	//재료품목 정의
    	String sYM_STOCK_ID_CNT				= "";
    	//재료종류별 번호
    	String sYD_CAR_SCH_ID				= "";
    	int intRtnVal 						= 0 ;
        String sSTL_NO                      = "";
        String sYD_MTL_ITEM                 = "";
        String sTRANS_ORD_DATE              = "";
        String sTRANS_ORD_SEQNO             = "";
        try{
        	
        	sYD_CAR_SCH_ID	= msgRecord.getFieldString("YD_CAR_SCH_ID") ;
    
        	
			getRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");
			inRecord1 = JDTORecordFactory.getInstance().create();			
			inRecord1.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
			
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarMtlYMins*/
			intRtnVal = ydCarSchDao.getYdCarsch(inRecord1, getRecSet1, 306);	
			if(intRtnVal < 0) {
				szMsg = "차량정보 조회오류  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
			} else if(intRtnVal == 0) {
				szMsg = "차량정보 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
			}				
			
			ydUtils.putLog(szSessionName, szMethodName, "건수" + getRecSet1.size() , YdConstant.WARNING);
			for( int Loop_i = 1; Loop_i <= getRecSet1.size(); Loop_i++ ) {
				getRecSet1.absolute(Loop_i);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(getRecSet1.getRecord());
				
				sYM_STOCK_ID_CNT= ydDaoUtils.paraRecChkNull(recOutTemp, "YM_STOCK_ID_CNT");
				sSTL_NO			= ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO");
				sYD_MTL_ITEM	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_MTL_ITEM");
				sTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_DATE");
				sTRANS_ORD_SEQNO= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_SEQNO");
				
				if( sYM_STOCK_ID_CNT.equals("0")) {
					
					inRecord2 = JDTORecordFactory.getInstance().create();
					inRecord2.setField("V_STOCK_ID"			, sSTL_NO);
					inRecord2.setField("V_STOCK_ITEM"		, sYD_MTL_ITEM);
					inRecord2.setField("V_REGISTER"			, "Csystem");
					inRecord2.setField("TRANS_ORD_DATE"		, sTRANS_ORD_DATE);
					inRecord2.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
					
//			   		intRtnVal = this.insYmStockCoil(inRecord2);
					EJBConnector ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
					ejbConn.trx("insYmStockCoil", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });

				} else {

					inRecord2 = JDTORecordFactory.getInstance().create();
					inRecord2.setField("V_STOCK_ID"			, sSTL_NO);
					inRecord2.setField("V_REGISTER"			, "Csystem");
					inRecord2.setField("TRANS_ORD_DATE"		, sTRANS_ORD_DATE);
					inRecord2.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
					
					EJBConnector ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
					ejbConn.trx("updYmStockCoil", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });

//					intRtnVal = this.updYmStockCoil(inRecord2);
//		    		if(intRtnVal<0) return intRtnVal ;
				}
				
			}	
         	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y5SetProgCodeCoil()
    
    
    
    /**
     * 오퍼레이션명 : ab열연 저장품 insert
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws 
     * @ejb.transaction type="RequiresNew"
     */
    public int insYmStockCoil (JDTORecord msgRecord){
    	CoilGdsJspDao dao = new CoilGdsJspDao();
    	
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "insYmStock";
        String szOperationName = "C열연 저장품 AB 등록";
        try{
        	
        	/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.insYmStockCoil*/
        	intRtnVal = dao.insYmStockCoil(msgRecord);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return  intRtnVal;
    	
    }//end of Y5UpdPtCommCoil()
    
    /**
     * 오퍼레이션명 : ab열연 저장품 update
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws 
     * ejb.transaction type="RequiresNew"
     */
    public int updYmStockCoil (JDTORecord msgRecord){
    	CoilGdsJspDao dao = new CoilGdsJspDao();
    	
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "updYmStockCoil";
        String szOperationName = "C열연 저장품 AB update";
        try{
        	
        	/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updYmStockCoil*/
        	intRtnVal = dao.updYmStockCoil(msgRecord);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
        }//end of try~catch
        
        
        return  intRtnVal;
    	
    }//end of Y5UpdPtCommCoil()
 
    
 
//	/**
//	 * 오퍼레이션명 : C열연코일야드L2 권하위치 변경가능유무 응답 (Y5YDL015)
//	 * 						
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */		
//	public void rcvY5CrnUdWrAns(JDTORecord msgRecord) throws JDTOException {
//		// 레코드 선언
//		JDTORecord recPara         = null;
//		JDTORecordSet rsResult     = null;
//		JDTORecord recGetVal       = null;  
//        JDTORecord getparamRecord  = null;  
//        JDTORecord setCrnschRecord = null;  
//        JDTORecord SndRecord       = null;
//        
//        // DAO 객체 생성
//		YdCrnSchDao ydCrnSchDao    = new YdCrnSchDao();
//		YdDelegate ydDelegate      = new YdDelegate();
//		
//		// 변수 선언
//    	String szMethodName        = "rcvScarfingWrkAnswerYN";
//		String szMsg               = "";
//		String szOperationName     = "권하위치 변경가능유무 응답";
//		String szRcvTcCode         = null;
//		int nRet                   = 0;
//		
//		szRcvTcCode = ydUtils.getTcCode(msgRecord);
//		if(szRcvTcCode == null){
//			szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return ;
//		}
//		
//		if(bDebugFlag){
//			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		}
//		
//		try{
//			//=============================================================
//			// Log 테이블 등록 
//			//=============================================================
//			szMsg = "[열연 코일야드L2] 권하위치 변경가능유무 응답 수신";
//			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//			
//			rsResult     = JDTORecordFactory.getInstance().createRecordSet("");
//	        getparamRecord  = JDTORecordFactory.getInstance().create();
//	        setCrnschRecord = JDTORecordFactory.getInstance().create();
//	        SndRecord = JDTORecordFactory.getInstance().create();
//			
//			// 파라미터 Check
//			ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [1] 파라미터 검사", YdConstant.DEBUG);
//			nRet = this.paramY5YDL015Check(msgRecord, getparamRecord, 0);
//	        if(nRet == -1) {
//                szMsg = "파라미터 Check중 Error	: " + nRet;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                return ;
//	        }       
//	        
//	        // 설비테이블에 야드설비작업Mode 업데이트 
//			ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [2] 설비 테이블 업데이트 처리", YdConstant.DEBUG);	        
//	        setCrnschRecord.setField("YD_EQP_ID"        	, getparamRecord.getFieldString("YD_EQP_ID"));
//	        setCrnschRecord.setField("YD_SCH_CD"        	, getparamRecord.getFieldString("YD_SCH_CD"));
//	        setCrnschRecord.setField("YD_CRN_SCH_ID"        , getparamRecord.getFieldString("YD_CRN_SCH_ID"));
//	        setCrnschRecord.setField("DOWN_LOAD_LOC_CHG_YN" , getparamRecord.getFieldString("DOWN_LOAD_LOC_CHG_YN"));
//	        nRet = this.Y5YDL015UpdYdEqp(setCrnschRecord, 0); 
//	        if(nRet == -1){
//                szMsg = "설비테이블 업데이트 중  Error : (" + getparamRecord.getFieldString("YD_EQP_ID") + ")(" + getparamRecord.getFieldString("YD_EQP_WRK_MODE") + ")" + nRet;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                return ;
//	        }
//
//	            
//	        
//	    }catch(JDTOException e) {
//            szMsg = "JDTOError : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//            return ;	   
//	    }catch(Exception e) {
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//            return ;
//	    } 
//	} 
	

//	/**
//     * 오퍼레이션명 : C열연 권하위치 변경가능유무 응답 파라미터 체크 [hun 2015.06.26]
//     * 
//     * @param  ● msgRecord, outRecord, intGp
//     * @return ● nRtnVal
//     * @throws ● JDTOException
//     */	
//	public int paramY5YDL015Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
//		// 레코드 선언
//		JDTORecord setRecord = null;
//        
//		// 변수 선언
//        String szMethodName  = "paramY5YDL015Check";
//    	String szMsg         = "" ;
//		
//        
//        try{
//        	setRecord = JDTORecordFactory.getInstance().create();
//        	
//        	// 레코드 값 체크 
//			setRecord.setField("YD_EQP_ID"      , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));
//			setRecord.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"));
//			setRecord.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"));
//			setRecord.setField("DOWN_LOAD_LOC_CHG_YN", ydDaoUtils.paraRecChkNull(msgRecord, "DOWN_LOAD_LOC_CHG_YN"));
//			
//			// 레퍼런스 레코드인자에 설정
//			outRecord.setRecord(setRecord);
//        }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//			return -1;
//        } 
//        
//		return 1;
//	} 
	

//	/**
//     * 오퍼레이션명 : 차량작업 예정정보 요구 수신 파라미터 체크 [hun 2015.06.29]
//     * 
//     * @param  ● msgRecord, outRecord, intGp
//     * @return ● nRtnVal
//     * @throws ● JDTOException
//     */	
//	public int paramY5YDL016Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
//		// 레코드 선언
//		JDTORecord setRecord = null;
//        
//		// 변수 선언
//        String szMethodName  = "paramY5YDL016Check";
//    	String szMsg         = "" ;
//		
//        
//        try{
//        	setRecord = JDTORecordFactory.getInstance().create();
//        	
//        	// 레코드 값 체크 
//			setRecord.setField("PT_LOAD_LOC"      , ydDaoUtils.paraRecChkNull(msgRecord, "PT_LOAD_LOC"));
//			
//			// 레퍼런스 레코드인자에 설정
//			outRecord.setRecord(setRecord);
//        }catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//			return -1;
//        } 
//        
//		return 1;
//	} 


//    /**
//     * 오퍼레이션명 : C열연 야드 권하위치 변경가능유무 응답 update
//     * 
//     * @param  ● msgRecord, intGp
//     * @return ● nRtnVal
//     * @throws ● JDTOException
//     */	
//    public int Y5YDL015UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
//    	// DAO객체 생성
//    	YdEqpDao ydEqpDao   = new YdEqpDao();
//    	    	
//    	// 변수 선언
//    	String szMethodName = "Y5YDL015UpdYdEqp";
//    	String szMsg        = "";
//    	int nRet            = 0;
//
//    	try{
//    		nRet = ydEqpDao.updYdEqp(msgRecord, intGp);
//    		
//			switch(nRet){
//				case 0 :
//				    szMsg = "No Data Found!!!, ErrorCode:" + nRet;
//				    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//				    return nRet = -1;
//				case -1	:
//				    szMsg = "Dup_val_on_index!!!, ErrorCode:" + nRet;
//				    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//				    return nRet = -1;
//				case -2	:
//				    szMsg = "Parameter Error!!!, ErrorCode:" + nRet;
//				    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//				    return nRet = -1;
//				case -3	:
//				    szMsg = "Execution Failed!!!, ErrorCode:" + nRet;
//			        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			        return nRet = -1;
//			}    	
//		}catch(Exception e){
//            szMsg = "Error : " + e.getLocalizedMessage();
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//            return nRet = -1;
//        } 
//    	return nRet;
//    } 

    
    
//---------------------------------------------------------------------------	
} // end of class CraneUdHdSeEJBBean
