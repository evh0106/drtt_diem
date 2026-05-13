package com.inisteel.cim.yd.ydWkAct.CraneUnloadWkrHd;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
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
import com.inisteel.cim.yd.common.dao.ymEtcDao.YmEtcDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.slabyd.session.SlabYdComm;
import com.inisteel.cim.yd.ydSch.CraneSch.CrnSchSeEJBBean;
import com.inisteel.cim.yd.ydWkReq.IssueWkReq.IssueWrkDmdSeEJBBean;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;


/**
 * 권하실적처리 Session EJB
 *
 * @ejb.bean name="CraneUdHdSeEJB" jndi-name="CraneUdHdSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CraneUdHdSeEJBBean extends BaseSessionBean { 
	
	// Session Name
	private String szSessionName=getClass().getName(); 
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	
	private YdDelegate ydDelegate =new YdDelegate();
	
	private SlabYdComm slabComm = new SlabYdComm();
	
	private EJBConnector ydEjbCon = new EJBConnector("default", this);
	
	// [DEBUG] message flag
	private boolean bDebugFlag=true;
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
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
	
	
	/**
     * 오퍼레이션명 : 통합야드권하실적처리
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY0CrnUdWr(JDTORecord msgRecord) throws DAOException {
		EJBConnector ejbConn 		= null;
		String szMethodName         = "procY0CrnUdWr";
		String szRtnMsg             = "";		
		String szLogMsg             = "";
		String szEjbJndiName 		= "CraneLdHdSeEJB";
		String szEjbMethod 			= "procY0CrnWrkOrdReq";
		JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
		try {
			
			//통합야드권하실적처리
			ejbConn = new EJBConnector("default", "CraneUdHdSeEJB", this);
			outRecord =(JDTORecord)ejbConn.trx("procY0CrnUdWrTX", new Class[] { JDTORecord.class}, new Object[] { msgRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), YdConstant.RETN_CD_FAILURE);
			
			if( sRTN_CD.equals(YdConstant.RETN_CD_FAILURE) ) {			//성공
				szLogMsg = "권하실적 오류 (" + szRtnMsg + ")";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, 1);
				return YdConstant.RETN_CD_FAILURE;
			}

			
			//크레인 작업지시 호출
			szLogMsg = "[통합야드권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

//SJH040002
			
//			ejbConn = new EJBConnector("default", szEjbJndiName, this);			
//			szRtnMsg = (String)ejbConn.trx(szEjbMethod, new  Class[] { JDTORecord.class }, new Object[] { outRecord });

			
			String sMSG_ID	= StringHelper.evl(outRecord.getFieldString("MSG_ID"),"");
			if (sMSG_ID.equals("YDYDJ644")) {			
				//크레인작업지시 송신
				ydDelegate.sendMsg(outRecord);
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			}	

			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || 
				szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || 
				szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
				szLogMsg = "[통합야드권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}else{
				szLogMsg = "[통합야드권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szSessionName + e.getMessage(), e);
		}
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procY0CrnUdWr
	
	
    /**
     * 오퍼레이션명 : 통합야드권하실적처리
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */         
    public JDTORecord procY0CrnUdWrTX(JDTORecord msgRecord)throws JDTOException  {
    	
    	YdDelegate      ydDelegate      = new YdDelegate();
    	YdEqpDao        ydEqpDao        = new YdEqpDao();
    	YdTcarSchDao    ydTcarSchDao    = new YdTcarSchDao();
    	YdCarSchDao     ydCarSchDao     = new YdCarSchDao();
    	YdStockDao      ydStockDao      = new YdStockDao();
    	YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
    	YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
    	YdStkColDao     ydStkColDao     = new YdStkColDao();
    	YdCrnWrkMtlDao  ydCrnWrkMtlDao  = new YdCrnWrkMtlDao(); 
    	
        int intRtnVal = 0;
        
        //DATA SETTING시 사용
        JDTORecord setRecord 				= JDTORecordFactory.getInstance().create();
        
        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord                = JDTORecordFactory.getInstance().create(); ;
        
        //작업예약 업데이트 항목
        JDTORecord bookrecord               = JDTORecordFactory.getInstance().create();
        
        //크레인 스케줄 야드작업진행상태 비교		 (작업예약이 끝났는지 확인하기위해 카운터 값을 가져옴)
        JDTORecord outRecord                = JDTORecordFactory.getInstance().create();         
        
        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 			= JDTORecordFactory.getInstance().create();
        
        JDTORecord recInTemp                = null;
        JDTORecord recOutTemp               = null;
        JDTORecord recInPara                = null;
        
        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
        
        JDTORecord    recSendMsg            = null;
        
        JDTORecordSet rsResult              = null;
        
        String szMsg                        = "";
        String szMethodName                 = "procY0CrnUdWrTX";
        String szOperationName				= "통합야드권하실적처리";
        
        String szTcarEqpId                  = "";
        String szCarEqpId                  	= "";
        
        //크레인 XYZ축 			파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
        String szYD_CRN_XAXIS     			= "";
        String szYD_CRN_YAXIS     			= "";
        String szYD_CRN_ZAXIS     			= "";
        //WBOOK_ID				작업예약 완료 처리시 사용
        String szYdWbookId              	= "";
        String szCrnSchId                   = "";
        String szQuery                      = "";
        
        String szYD_UP_WR_LOC               = "";
        String szYD_DN_WR_LOC               = "";
        String szYD_SCH_CD                  = "";
        String szYD_EQP_ID                  = "";
        String szYD_DN_WR_LAYER             = "";
        String szYD_CAR_SCH_ID              = "";
        String szYD_TCAR_SCH_ID             = "";
        
        //EJB CALL or JMS CALL
        String szIS_EJB_CALL = null;
        
        String szLogMsg = null;
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null || szRcvTcCode.equals("")){
            szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException("<procY0CrnUdWr> Y0ParamCheck " + szMsg);
        }
        
        if(bDebugFlag){
            szMsg="["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
        
        try{
        	
        	szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
        	
	        intRtnVal = this.Y0ParamCheck(msgRecord, getCrnschRecord, 1) ;
	        
	        szCrnSchId 				= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_CRN_SCH_ID");
	        szYD_EQP_ID 			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_EQP_ID");
	        szYD_SCH_CD 			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_SCH_CD");
	        szYD_DN_WR_LOC			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC");
	        szYD_DN_WR_LAYER		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER");
	        
	        if(szCrnSchId.equals("")){
                szMsg = "["+szOperationName+"]'YD_CRN_SCH_ID' Data Error	: 크레인스케줄ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY0CrnUdWr> Y0ParamCheck " + szMsg);
	        }
	        
	        //파라미터 레코드 편집
	        setRecord.setField("YD_CRN_SCH_ID",       	szCrnSchId);
	        setRecord.setField("YD_SCH_CD",           	szYD_SCH_CD);
	        setRecord.setField("YD_DN_WR_LOC",        	szYD_DN_WR_LOC);
	        setRecord.setField("YD_DN_WR_LAYER",      	szYD_DN_WR_LAYER);
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        intRtnVal = this.Y0UpdYdCrnsch(setRecord, 0);
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //	크레인스케줄 작업재료 조회
            //-------------------------------------------------------------------------------------------------------------------
	        intRtnVal = this.Y0GetYdCrnsch(setRecord, getRecSet,3);
	        
	        if(intRtnVal < 0){
				szMsg = "[" + szOperationName + "] 크레인스케줄  Y0GetYdCrnsch failed!!!, ErrorCode:" + intRtnVal;
		        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		        m_ctx.setRollbackOnly();
		        throw new JDTOException("<procY0CrnUdWr> Y0GetYdCrnsch" + szMsg);
			}
	        
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "권하실적 크레인작업재료 : no data found!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            m_ctx.setRollbackOnly();
	            throw new JDTOException("["+szOperationName+"] " + szMsg);
	        }
	        
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();
	        
	        //권상실적위치
        	szYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
	        //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szYdWbookId = ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //	작업진행상태 확인 후 상태가 맞지 않으면 롤백 처리
            //-------------------------------------------------------------------------------------------------------------------
	        if ((!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("2")) && 
	        	(!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("3")) ) {
	        	szMsg = "["+szOperationName+"] 작업진행상태["+ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT")+"]가   권상('2') 또는 권하대기('3')이 아닙니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            m_ctx.setRollbackOnly();
	            throw new JDTOException("["+szOperationName+"] " + szMsg);
	        }
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하지시위치와 권하실적위치가 다르면 권하지시위치의 맵정보를 Clear시킴
	        //-------------------------------------------------------------------------------------------------------------------
	        if (!getRecord.getFieldString("YD_DN_WR_LOC").equals(getRecord.getFieldString("YD_DN_WO_LOC"))) {
                szMsg = "권하실적위치와 권하지시위치가 다른 경우...";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	        	
	        	//크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
	        	if(szYD_CRN_XAXIS.equals("") && szYD_CRN_YAXIS.equals("") && szYD_CRN_ZAXIS.equals("")) {
	        		szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_XAXIS");
	        		szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_YAXIS");
	        		szYD_CRN_ZAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_ZAXIS");
	        	}
	        	
	        	intRtnVal = this.Y0ClearYdStklyr(getRecSet,1) ;
	        	
	        	if(intRtnVal < 0){
					szMsg = "[" + szOperationName + "] 크레인스케줄  Y0ClearYdStklyr failed!!!, ErrorCode:" + intRtnVal;
			        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
			        m_ctx.setRollbackOnly();
			        throw new JDTOException("<procY0CrnUdWr> Y0ClearYdStklyr" + szMsg);
				}
	        }
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하실적LYR와 실제적치단의 최상위 위치가 다르면 권하실적LYR를 변경하고 적치단 정보를 수정한다.
	        //통합야드는 반영안되어 있슴.추후 반영 
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        // 
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //	크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        intRtnVal = this.Y0RegYdStklyr(getRecSet,1) ;
	        
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_DN_WR_LOC",       getCrnschRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setRecord.setField("YD_DN_WR_LAYER",     getCrnschRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        setRecord.setField("YD_DN_WRK_ACT_GP",   getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP") );
	        setRecord.setField("YD_DN_WR_XAXIS",     getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
	        setRecord.setField("YD_DN_WR_YAXIS",     getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
	        setRecord.setField("YD_DN_WR_ZAXIS",     getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
	        setRecord.setField("YD_WRK_PROG_STAT",   getCrnschRecord.getFieldString("YD_WRK_PROG_STAT"));
	        setRecord.setField("YD_DN_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));
	        setRecord.setField("YD_GP",   			 getCrnschRecord.getFieldString("YD_GP"));

	        //--------------------------------------------------------------------------------------------------
	        //	크레인스케줄 등록 시 야드작업계상일자에 등록되는 값을 권하완료시점에 다시 수정, 권하완료 시의 값이 사용됨
	        //	TO_CHAR(SYSDATE - (7 / 24), 'YYYYMMDD')
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.23
	        //--------------------------------------------------------------------------------------------------
	        String szYD_WRK_HDS_DD			= YdUtils.getDefaultHdsDate();
	        
	        setRecord.setField("YD_WRK_HDS_DD",   		szYD_WRK_HDS_DD);
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]의 야드작업계상일자["+szYD_WRK_HDS_DD+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        //--------------------------------------------------------------------------------------------------
	        
	        intRtnVal = this.Y0UpdYdCrnsch(setRecord, 0);
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]에 권하실적정보 수정 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        
            szYD_DN_WR_LOC 		= ydDaoUtils.paraRecChkNull(setRecord, "YD_DN_WR_LOC");
	        szYD_DN_WR_LAYER 	= ydDaoUtils.paraRecChkNull(setRecord, "YD_DN_WR_LAYER");
	        
            szMsg = "저장위치의 설비구분 : " + szYD_DN_WR_LOC.substring(2, 4);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
            szMsg = "스케줄코드의 설비구분 : " + szYD_SCH_CD.substring(2, 4);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
	        
            //-------------------------------------------------------------------------------------------------------------------
	        //	차량스케줄에 차량 상차작업예약ID등록 수정!!
	        //	만약  to위치는 차량인데 차량스케줄코드가 아닌경우...는 직상차로 보고 차량스케줄의 상차작업예약id를 등록한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        if(szYD_DN_WR_LOC.substring(2, 4).equals("PT") && 
	          !szYD_SCH_CD.substring(2, 4).equals("PT")){
	            	
	            //권하위치 적치열구분으로 적치열을 조회하여 운송장비코드를 조회한다.
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_STK_COL_GP", szYD_DN_WR_LOC.substring(0,6));
	            
	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        	intRtnVal = ydStkColDao.getYdStkcol(recInPara, rsResult, 0);
	        	if(intRtnVal <= 0) {
		            szMsg = szYD_DN_WR_LOC.substring(0,6) + "적치열구분이 잘못되었습니다.";
		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		            m_ctx.setRollbackOnly();
		            throw new JDTOException("["+szOperationName+"] <procY0CrnUdWr> getYdStkcol" + szMsg);
	        	}
	        	rsResult.absolute(1);
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setRecord(rsResult.getRecord());
	        	
                //운송장비코드로 차량스케줄을 조회한다.
	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        	intRtnVal = ydCarSchDao.getYdCarsch(recInPara, rsResult, 7);
	        	if(intRtnVal <= 0) {
	                szMsg = ydDaoUtils.paraRecChkNull(recInPara, "TRN_EQP_CD") + "로 생성된 차량 스케줄이 없습니다.";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	                m_ctx.setRollbackOnly();
	                throw new JDTOException("<procY0CrnUdWr> getYdCarsch" + szMsg);
	        	}
	        	rsResult.absolute(1);
	        	recOutTemp = JDTORecordFactory.getInstance().create();
	        	recOutTemp.setRecord(rsResult.getRecord());
	        	
	            //조회된 차량스케줄에 상차작업예약ID를 등록한다.
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID").trim());
	        	recInPara.setField("YD_CARLD_WRK_BOOK_ID", szYdWbookId);
	        	recInPara.setField("YD_EQP_WRK_STAT", "L");
	        	recInPara.setField("YD_CAR_PROG_STAT", "4");//상차개시
	        	recInPara.setField("YD_CARLD_STOP_LOC", szYD_DN_WR_LOC.substring(0,6));
	        	intRtnVal = ydCarSchDao.updYdCarsch(recInPara, 0);
	        	if(intRtnVal <= 0) {
	                szMsg = "직상차용 차량스케줄 상차작업예약 등록시 Error";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                m_ctx.setRollbackOnly();
	                throw new JDTOException("<procY0CrnUdWr> updYdCarsch" + szMsg);
	        	}

	        }
	        
	        //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 차량이므로 차량 스케줄 이송재료 등록 후 차량진행관리 호출
	        //-------------------------------------------------------------------------------------------------------------------
            if(szYD_DN_WR_LOC.substring(2, 4).equals("PT")|| 
               szYD_DN_WR_LOC.substring(2, 4).equals("TR")){
            	
            	intRtnVal = this.Y0SetYdCar(getRecSet, 1) ; 
            	
            	// 차량 작업 진행관리 호출
            	recInTemp = JDTORecordFactory.getInstance().create();
            	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
            	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
            	recInTemp.setField("CAR_LDUD_GP",   "U");
            	recInTemp.setField("YD_DN_WR_LOC",   szYD_DN_WR_LOC);
            	
				szMsg = "차량 작업 진행관리 호출(상차)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.displayRecord(szOperationName, recInTemp);

            	//차량 작업진행관리 호출(상차)
				szYD_CAR_SCH_ID = this.procY0CarWrkStatCtr(recInTemp);
            }
            //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 차량이아니고  권상위치가 차량이면 차량진행관리 호출
	        //-------------------------------------------------------------------------------------------------------------------
            if(!szYD_DN_WR_LOC.substring(2, 4).equals("PT")&& 
               !szYD_DN_WR_LOC.substring(2, 4).equals("TR")){
                
            	//권상실적위치가 차량인 경우
            	if(szYD_UP_WR_LOC.substring(2, 4).equals("PT")|| 
            	   szYD_UP_WR_LOC.substring(2, 4).equals("TR")){
                	
            		recInTemp = JDTORecordFactory.getInstance().create();
                	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
                	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
                	recInTemp.setField("CAR_LDUD_GP",   "L");
                	recInTemp.setField("YD_UP_WR_LOC",   szYD_UP_WR_LOC);
                
					szMsg = "차량 작업 진행관리 호출(하차)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.displayRecord(szOperationName, recInTemp);                	
                	
                	//차량 작업 진행관리 호출(하차)
                	szYD_CAR_SCH_ID = this.procY0CarWrkStatCtr(recInTemp);
            	}
            }
            
	        if(getRecord.getFieldString("YD_WBOOK_ID") == null ||
	           getRecord.getFieldString("YD_WBOOK_ID").equals("")) {
                szMsg = "YD_WBOOK_ID  Data Error	: 작업예약 ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY0CrnUdWr> " + szMsg);
	        }
	        
	        //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업 및 재료, 작업예약 및 재료정보를 Clear
	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인 작업재료 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("MODIFIER",           "YDSYSTEM");
	        intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl_YD_CRN_SCH_ID(setRecord);
	        if(intRtnVal <= 0) {
                szMsg = "크레인스케줄작업재료 삭제중 Error!! Code : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
	        	throw new JDTOException("<procY0CrnUdWr> updYdCrnwrkmtl_YD_CRN_SCH_ID " + szMsg);
	        }
	        
	        //크레인스케줄 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("YD_DN_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss")); //권하완료일시
	        intRtnVal = this.Y0UpdYdCrnsch(setRecord, 0);
	        if(intRtnVal < 0){
                szMsg = "[" + szOperationName + "] 크레인스케줄 삭제처리 에러발생!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY0CrnUdWr> Y1UpdYdCrnsch " + szMsg);
	        }
	        
	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        getRecord = JDTORecordFactory.getInstance().create();
	        getRecord.setField("YD_WBOOK_ID", szYdWbookId);

	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
			intRtnVal = this.Y0GetYdCrnsch(getRecord, getRecSet, 28);
			if(intRtnVal < 0){
                szMsg = "[" + szOperationName + "] 작업예약완료 CHECK 에러발생!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY0CrnUdWr> Y1UpdYdCrnsch " + szMsg);
	        }
	        /*
	         * 작업예약에 존재하는 크레인스케쥴 정보가 없을경우에 작업예약정보를 삭제한다.
	         */	
	        if (getRecSet.size() == 0) {
	        	
	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId) ;
	            bookrecord.setField("DEL_YN",             "Y") ;
	            bookrecord.setField("YD_SCH_PROG_STAT",   "E") ;
	            
	            intRtnVal = this.Y0UpdYdWrkbook(bookrecord, 0);
		        
		        //작업예약재료조회
	            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	            intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(bookrecord, getRecSet, 1);
								
	            //조회한 작업예약재료1매씩 저장품 업데이트
				for( int Loop_i = 1; Loop_i <= getRecSet.size(); Loop_i++ ) {
					getRecSet.absolute(Loop_i);
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setRecord(getRecSet.getRecord());
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
					recInTemp.setField("YD_SCH_CD", "");
					recInTemp.setField("YD_WBOOK_ID", "");
					recInTemp.setField("YD_STK_COL_GP", szYD_DN_WR_LOC.substring(0,6));
					recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, Loop_i-1));
					intRtnVal = ydStockDao.updYdStock(recInTemp, 0);
			        
				}
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("STL_NO",      ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
				recInTemp.setField("DEL_YN",      "Y");
				recInTemp.setField("MODIFIER",    "SYSTEM");
				recInTemp.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID"));
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl1(recInTemp);
		        
	            szMsg = "작업 예약 처리 완료";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	        }else{
	            szMsg = "작업 예약 진행 중";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	        }
	        
	        //-------------------------------------------------------------------------------------------------------------------
		    //	크레인 설비상태 권하완료(4) 셋팅.
	        //-------------------------------------------------------------------------------------------------------------------
            recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	        recInTemp.setField("YD_EQP_STAT", "4");
            intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
            if(intRtnVal <= 0) {
				 szMsg="설비상태 UPDATE 처리시 오류 발생.";
			 	 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			 	 m_ctx.setRollbackOnly();
	   			 throw new JDTOException(szMsg);
	   		}
			
			//설비id로 설비Table조회
	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
						
			/*
			 * 이력테이블등록호출 
			 */
			{
				CrnSchSeEJBBean crnSchSeEJBBean = new CrnSchSeEJBBean();
			
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",             "");
				recInTemp.setField("YD_WBOOK_ID",        szYdWbookId);
				recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId);
				recInTemp.setField("YD_CAR_SCH_ID",      szYD_CAR_SCH_ID);
				recInTemp.setField("YD_TCAR_SCH_ID",     szYD_TCAR_SCH_ID);
				recInTemp.setField("YD_WTCL_TNK_SCH_ID", "");
				crnSchSeEJBBean.procWorkHistoryCreate1(recInTemp);
				
				szMsg="[" + szOperationName + "] 이력테이블등록호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			/*
			 * 크레인 작업지시 요구호출.
			 */
			{
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord(0));
		        
		        //크레인 작업지시 호출
				recInTemp = JDTORecordFactory.getInstance().create();
//SJH03004				
				recInTemp.setField("MSG_ID",           "YDYDJ644");
				
				recInTemp.setField("YD_EQP_ID",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID"));
				recInTemp.setField("YD_EQP_WRK_MODE",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE"));
				recInTemp.setField("YD_WRK_PROG_STAT", "4");
				recInTemp.setField("YD_SCH_CD",        szYD_SCH_CD);
				recInTemp.setField("YD_CRN_SCH_ID",    "");
				recInTemp.setField("YD_CRN_XAXIS",     "");
				recInTemp.setField("YD_CRN_YAXIS",     "");
				recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_SUCCESS);
				
//				String szEjbJndiName 	= "CraneLdHdSeEJB";
//				String szEjbMethod 		= "procY0CrnWrkOrdReq";
//				
//				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//				 * ++++++ 데이타의 정합성 때문에 추가 - 아래 항목을 수정 시에는 수정자에게 문의 +++++++
//				 * 크레인스케줄이 삭제(DEL_YN)필드가 Y로 설정된 후 COMMIT전에 
//				 * 크레인작업지시모듈에서 조회 시 현재 Y로 설정된 동일한 크레인스케줄이 조회가 되어
//				 * 전문편집 모듈에 넘겨주고 다시 조회 시 Y로 COMMIT되는 경우가 생겨서 L2로 크레인작업지시가
//				 * 내려가지 않는 현상이 발생하여 EJB CALL로 변경. 
//				 * 수정자 : 임춘수
//				 * 수정일 : 2009.09.03
//				 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				szIS_EJB_CALL = "Y";
//				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				
//				if( szIS_EJB_CALL.equals("Y")) {
//					
//					szLogMsg = "[통합야드권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//
//					EJBConnector ejbConn = new EJBConnector("default", szEjbJndiName, this);			
//					String szRtnMsg = (String)ejbConn.trx(szEjbMethod, new  Class[] { JDTORecord.class }, 
//																	   new Object[] { recInTemp });
//			
//					if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
//						szLogMsg = "[통합야드권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					}else{
//						szLogMsg = "[통합야드권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
//					}
//
////					ydDelegate.sendMsg(recInTemp);
//					szLogMsg = "[통합야드권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - JMS 전문전송 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					
//				}else{
//					//크레인작업지시 송신
//					ydDelegate.sendMsg(recInTemp);
//					szLogMsg = "[통합야드권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - JMS 전문전송 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				}
			}	
			//------------------------------------------------------------------
	        // 권하 실적시 Flex 실시간 처리
	        //------------------------------------------------------------------
	        JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
		 	recFlex.setField("YD_GP",  YdConstant.YD_GP_INTGR_YARD);
		 	recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
		 	recFlex.setField("YD_UP_WR_LOC", szYD_UP_WR_LOC);
		 	recFlex.setField("YD_DN_WR_LOC", szYD_DN_WR_LOC);
		 	szMsg="Flex 권하 완료 실적 전송";
		 	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    ydUtils.putYdFlexCrnWrk("", recFlex);  
	        
        }catch(Exception e) {
        	szLogMsg = "[통합야드권하실적처리]권하실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }//end of try~catch
        return recInTemp;
    }// end of procY0CrnUdWrTX()
    
	/**
	 * 오퍼레이션명 : 차량 작업 진행관리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procY0CarWrkStatCtr(JDTORecord msgRecord)throws JDTOException  {
		//TC_CODE :YDYDJ630
		
		YdDelegate ydDelegate = new YdDelegate();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao(); 
		YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
		YdCarSchDao     ydCarSchDao     = new YdCarSchDao();  

		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recFirst          = null;
		JDTORecord    recLast           = null;
		
	    int intRtnVal 		   = 0 ;
	    
	    String szMsg           = "";
	    String szMethodName    = "procY0CarWrkStatCtr";
	    
	    String szCAR_LDUD_GP   = "";
	    String szYD_WBOOK_ID   = "";
	    String szYD_CRN_SCH_ID = "";
	    String szFST_CRN_SCH_ID = "";
	    String szLST_CRN_SCH_ID = "";
	    String szYD_SCH_CD      = "";
	    String szYD_GP          = "";
	    String szYD_CAR_SCH_ID  = "";
	    String szYD_DN_WR_LOC   = "";
	    String szYD_UP_WR_LOC   = "";
	    String szYD_CAR_USE_GP  = "";

	    
	    
	    try{
	    	//상하차구분 플래그, 작업예약id, 크레인스케줄id
	    	szCAR_LDUD_GP   = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_LDUD_GP"); 
	    	szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	    	szYD_DN_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC");
	    	szYD_UP_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
	    	
//PIDEV			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "차량 작업 진행관리", "APPPI0", "S", "*");		    		
	    	
	    	//작업예약id로 크레인 스케줄을 조회
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
//	    	recInTemp.setField("YD_EQP_GP", szYD_DN_WR_LOC.substring(2, 4));
	    	
	    	if(szCAR_LDUD_GP.equals("L")){
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 31);
		    	if(intRtnVal <= 0) {
					szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error 상차인경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new JDTOException("<procY0CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}else{
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 32);
		    	if(intRtnVal <= 0) {
					szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error 하차인경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new JDTOException("<procY0CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}
	    	
			szMsg = "rsResult 사이즈 : " + rsResult.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	rsResult.first();
	    	recFirst = JDTORecordFactory.getInstance().create();
	    	recFirst.setRecord(rsResult.getRecord());
	    	szFST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recFirst, "YD_CRN_SCH_ID");
	    	
			szMsg = "첫번째 스케줄id : " + szFST_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	rsResult.last();
	    	recLast = JDTORecordFactory.getInstance().create();
	    	recLast.setRecord(rsResult.getRecord());
	    	szLST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recLast, "YD_CRN_SCH_ID");
	    	
			szMsg = "마지막 스케줄id : " + szLST_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "전문 스케줄id : " + szYD_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	szYD_SCH_CD      = ydDaoUtils.paraRecChkNull(recFirst, "YD_SCH_CD");
	    	szYD_GP          = szYD_SCH_CD.substring(0,1);
	    	
	    	//플래그가 상차인경우
	    	if(szCAR_LDUD_GP.equals("U")) {
	    		//차량스케줄 id를 조회
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
    			recInTemp.setField("PI_YD",    	szYD_GP);		    			
	    		ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    		szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP"); 	
	    		
	    		//출하차량인 경우에만 적용한다.
	    		if(szYD_CAR_USE_GP.equals("G")){
	    			
					szMsg="일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		recInTemp = JDTORecordFactory.getInstance().create();
		    		
		    		//PIDEV
//					if("Y".equals(sApplyYnPI)) {
						recInTemp.setField("MQ_TC_CD",        "M10YDLMJ1083");
//					} else {
//						recInTemp.setField("MSG_ID",        "YDDMR013");
//					}			    		
					
					recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("YD_GP",         szYD_GP);
		
					ydDelegate.sendMsg(recInTemp);
	    		}
	    		
	    		
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			//동일하면 차량스케줄에 상차완료일시 등록, 설비작업상태 = '영차'
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_CAR_PROG_STAT", "5");
	    			recInTemp.setField("YD_EQP_WRK_STAT", "L");
	    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 1);
	    			if(intRtnVal <= 0) {
						szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}
	    			
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			//구내운송
	    			if(szYD_CAR_USE_GP.equals("L")){							//구내운송 - 임춘수 수정 2009.06.15	
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 시작 2009.06.08 임춘수 추가 ----------------------
	    				intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
	    				if( intRtnVal <= 0 ) {
	    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
	    					szMsg="상차완료시 공통테이블 업데이트 처리 실패";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				}else{
	    					szMsg="상차완료시 공통테이블 업데이트 처리 성공";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    				}
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------
	    				
		    			//상차작업완료 송신 YDTSJ008 (구내운송 상차완료)
		    			recInTemp.setField("MSG_ID",        "YDTSJ008");
						szMsg="구내운송 상차작업완료 송신 : YDTSJ008";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}else{
		    			//상차작업완료 송신 YDDMR017 (외판슬라브출하상차완료)
	    				
	    				//PIDEV	    				
//	    				if("Y".equals(sApplyYnPI)) {
							recInTemp.setField("MQ_TC_CD",        "M10YDLMJ1093");
							szMsg="외판슬라브출하상차완료 송신 : M10YDLMJ1093";
//						} else {
//							recInTemp.setField("MSG_ID",        "YDDMR017");
//							szMsg="외판슬라브출하상차완료 송신 : YDDMR017";
//						}
	    				
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}
	    			
	    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
	    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    			recInTemp.setField("YD_GP",         szYD_GP);
	    			
	    			ydDelegate.sendMsg(recInTemp);
	    			
	    			
					szMsg="상차작업완료 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}
	    		
	    	//플래그가 하차인 경우
	    	}else if(szCAR_LDUD_GP.equals("L"))	{
	    		//차량스케줄 id를 조회
	    		recInTemp  = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			rsResult   = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
    			recInTemp.setField("PI_YD",    	szYD_GP);	    			
	    		ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    			
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			recInTemp.setField("YD_GP",         szYD_GP);
    			
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			//동일하면 차량스케줄에 하차완료일시 등록
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_EQP_WRK_STAT", "U");
	    			recInTemp.setField("YD_CAR_PROG_STAT", "E");
	    			recInTemp.setField("DEL_YN",           "N");
	    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 3);
	    			if(intRtnVal <= 0) {
						szMsg="차량스케줄에 하차완료일시  등록시 Error!! Code : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}
	    			
	    			//---------------- 하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 추가
					szMsg="szYD_CAR_SCH_ID = " + szYD_CAR_SCH_ID;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			intRtnVal = YdCommonUtils.procCarUnLoadCmpl(szYD_CAR_SCH_ID);
    				if( intRtnVal <= 0 ) {
    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
    					szMsg="하차완료시 공통테이블 업데이트 처리 실패";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    				}else{
    					szMsg="하차완료시 공통테이블 업데이트 처리 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				}
	    			//-----------------하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 끝
	    			
	    			//하차작업완료 송신 YDTSJ010 - 구내운송 전송
    				recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID",        "YDTSJ010");
	    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
	    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    			recInTemp.setField("YD_GP",         szYD_GP);
	    			ydDelegate.sendMsg(recInTemp);
	    			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    	         * 			진행관리 슬라브소재이송완료실적전송  - YDPTJ001
	    	         * 업무기준 Desc : 1. 하차완료시
	    	         * 스케줄코드 :  1. 차량하차 스케줄 : PT(팔렛트), TR(트레일러), LM(하차)
	    	         * 기능 추가 : 임춘수
	    	         * 일자 : 2009.06.16
	    	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    			recInTemp.setField("MSG_ID",        "YDPTJ001");
	    			ydDelegate.sendMsg(recInTemp);
	    	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    			
					szMsg="하차작업완료 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}
	    		
	    	}else{
				szMsg="상차 및 하차 구분 플래그 Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException("<procY0CarWrkStatCtr> " + szMsg);
	    	}
	
		}catch(Exception e){
	
			szMsg="차량 작업 진행관리 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException("<procY0CarWrkStatCtr> getYdCrnsch" + szMsg);
		}
	
		szMsg="차량 작업 진행관리 처리"+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return szYD_CAR_SCH_ID;
	} //end of procY0CarWrkStatCtr()
    
    /**
     * 오퍼레이션명 : 권상,권하 파라미터 체크
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int Y0ParamCheck (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "";
        String szMethodName                 = "Y0ParamCheck";
        int intRtnVal 	= 1 ;
        
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

			setRecord.setField("YD_DN_WR_LOC"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
			setRecord.setField("YD_DN_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;
 	        
            //전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
	        if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "A") ;
	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "B") ;
	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "M") ;
	        }
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
        	szMsg = "Error : "+ e.getLocalizedMessage();
			throw new JDTOException("<procY0CarWrkStatCtr> Y0ParamCheck" + szMsg);
        }//end of try~catch
        
        return intRtnVal;
        
    }//end of Y0ParamCheck()
    
    /**
     * 오퍼레이션명 : 통합야드 크레인스케줄 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0UpdYdCrnsch(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	int intRtnVal = 0 ;
    	
    	String szMethodName = "Y0UpdYdCrnsch";
    	String szOperationName = "통합야드 크레인스케줄 Update";
    	String szMsg        = "";

		try{
			
			intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);		        
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	                return intRtnVal = -1;
	        	case -1	:
	                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return intRtnVal = -1;
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return intRtnVal = -1;
	        	case -3	:
	                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return intRtnVal = -1;
	        }
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y0UpdYdCrnsch>" + szMsg);
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y0UpdYdCrnsch
    
    /**
     * 오퍼레이션명 : 통합야드 크레인스케줄 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0GetYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMethodName = "Y0GetYdCrnsch";
    	String szOperationName = "통합야드 크레인스케줄 Select";
    	String szMsg        = "";
    	
        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
	        switch (intRtnVal) {
        	case 0	:
                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
                return intRtnVal;
        	case -2	:
                szMsg = "[" + szOperationName + "]parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
                return intRtnVal;
        }
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("[" + szOperationName + "]<Y0GetYdCrnsch> " + szMsg);
        }//end of try~catch

        return intRtnVal ;
    	
    }//end of Y0GetYdCrnsch()
    
    /**
     * 오퍼레이션명 : 통합야드 적치단 Clear
     *  
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0ClearYdStklyr (JDTORecordSet getRecSet, int intGp) throws JDTOException {
    	
    	JDTORecord getRecord = JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
    	
    	int intRtnVal 		= 1;
    	String szMsg 		= "";
    	String szMethodName = "Y0ClearYdStklyr";
    	String szOperationName = "통합야드 적치단 Clear";
    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();
            
    		for(int i=0; i<rowsize ; i++){
    			//권상 지시위치 Clear
                if(intGp == 0) {
        			String szYD_UP_WR_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC") ;
                    String szYD_UP_WR_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER") ;
                    
                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
                    //적치단 설정
                    String szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                    setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                }
                
                //권하 지시위치 Clear
                if(intGp == 1) {
	    			String szYD_DN_WO_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC") ;
	                String szYD_DN_WO_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LAYER") ;
	                
	                setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
	                setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   
	                //적치단 설정
	                String szStkLyr =ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i);
	                setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                }
                
                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                setRecord.setField("STL_NO",              "");
                
                intRtnVal = this.Y0UpdYdStklyr(setRecord, 0);  //적치단의 재료정보 Clear
                
                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for
    		
    		
		}catch(Exception e){
			szMsg = "[" + szOperationName + "]Error : "+ e.getLocalizedMessage();
			throw new JDTOException("[" + szOperationName + "]<Y0ClearYdStklyr> " + szMsg);
	    }//end of try~catch
		
		return  intRtnVal;
    }//end of Y0ClearYdStklyr()
    
    /**
     * 오퍼레이션명 : 적치단 등록
     *  
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0RegYdStklyr (JDTORecordSet getRecSet, int intGp)throws JDTOException {
    	
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	JDTORecord getRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	JDTORecord recOutTemp 			= JDTORecordFactory.getInstance().create();
    	JDTORecordSet rsResult          = null;
    	
    	String szMsg 					= "";
        String szMethodName				= "Y0RegYdStklyr";
        String szOperationName          = "적치단 등록";
        String szYD_MTL_ITEM            = "";
        
        int intRtnVal 					= 0 ;
        
        String szYdWbookId 				= "";
    	
    	try{
    		int rowsize = getRecSet.size();
    		
        	for(int i=0; i<rowsize; i++) {
        		
        		getRecSet.absolute(i+1);
        		getRecord 	= JDTORecordFactory.getInstance().create();
        		getRecord.setRecord(getRecSet.getRecord());
        		
        		//저장품을 조회하여 재표 품목의 값을 조회한다.
        		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
        		intRtnVal = ydStockDao.getYdStock(getRecord, rsResult, 0);
    	        
    	        rsResult.absolute(1);
    	        recOutTemp = JDTORecordFactory.getInstance().create();
    	        recOutTemp.setRecord(rsResult.getRecord());
    	        
    	        szYD_MTL_ITEM 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_MTL_ITEM");
    	        szYdWbookId 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID");
        		
        		//권하 실적위치 등록
        		String szYD_DN_WR_LOC	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
        		String szYD_DN_WR_LAYER	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER") ;
        		String szSTL_NO	 		   = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO") ;
        		
        		setRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6).trim());   
        		setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8).trim());               
                String szStkLyr = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO", 		szStkLyr) ;
                setRecord.setField("STL_NO",              	szSTL_NO);                            
                setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");           
                
                //적치단dao를 호출해서 업데이트를 한다.
                intRtnVal = this.Y0UpdYdStklyr(setRecord, 0); 
    	        

    	        
    	        //저장위치 갱신				공통테이블에 저장위치를 갱신한다.
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
    	        setRecord.setField("YD_MTL_ITEM",       	szYD_MTL_ITEM);
    	        setRecord.setField("YD_DN_WR_LOC",       	ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC")); 

    	        intRtnVal = this.Y0setYdStrLoc(setRecord) ;
    	        
    	        
    	        //차량 하차작업 일 경우			공통테이블에 진도코드를 갱신한다.
    	        if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")|| 
    	           ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){
    	        	
    	        	getRecord.setField("YD_MTL_ITEM",       	szYD_MTL_ITEM);
    	        	//진도코드 갱신
    	        	intRtnVal = this.Y0SetProgCode(getRecord) ;
    	        }
    	        
        	}
		}catch(Exception e){
			szMsg = "Error : "+ e.getLocalizedMessage();
			throw new JDTOException("<Y0ClearYdStklyr> " + szMsg);
	    }//end of try~catch
		
		intRtnVal = 1 ;
		return intRtnVal ;
    
    }//end of Y0RegYdStklyr()
    
    /**
     * 오퍼레이션명 : 차량 Setting
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y0SetYdCar (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
    	
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMethodName 				= "Y0SetYdCar";
    	String szOperationName              = "통합야드 차량 Setting";
    	String szMsg 						= "";
    	String szYD_AIM_YD_GP               = "";
    	String szYD_AM_BAY_GP               = "";
    	
    	long lngYD_MTL_WT                  = 0;
    	int  intYD_MTL_SH                  = 0;
    	long lngYD_EQP_WRK_WT              = 0;
    	int  intYD_EQP_WRK_SH              = 0;
    	
    	int intRtnVal = 0 ;
    	
    	//차량 스케줄 ID
    	String szYD_CAR_SCH_ID = "";
    	
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
	    	
	    	
	    	// 상하차 작업예약 ID로 차량스케줄 조회
//PIDEV_S :병행가동용:PI_YD
	    	setRecord.setField("PI_YD",    	"S");				    	
	    	intRtnVal = this.Y0GetYdCarsch(setRecord, outRecSet, 3) ;
	    	if (intRtnVal <= 0) return -1 ;
	    	
	    	
	    	// 차량스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 차량스케줄 ID를 추출한다
	    	szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
	    	lngYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullLong(getTcarRecord, "YD_EQP_WRK_WT");
	    	intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(getTcarRecord, "YD_EQP_WRK_SH");
	    	
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	setRecord.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID);
	    	setRecord.setField("MODIFIER", 			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
	    	
	    	szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");
	    	szYD_AM_BAY_GP = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_BAY_GP");
	    	
	    	//통합야드
	    	if(szYD_AIM_YD_GP.equals("A")) {
	    		setRecord.setField("ARR_WLOC_CD", "DHY21");
	    		
		    //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
	    	}else if(szYD_AIM_YD_GP.equals("M")) {
	    		setRecord.setField("ARR_WLOC_CD", YdConstant.WLOC_CD_PORT_SLAB_YARD);
	    		
	    	//A후판슬라브	
	    	}else if(szYD_AIM_YD_GP.equals("D")) {
	    		if(szYD_AM_BAY_GP.equals("B")) {
	    			setRecord.setField("ARR_WLOC_CD", "DWY22");
	    		}else{
	    			setRecord.setField("ARR_WLOC_CD", "DKY21");
	    		}
	    		
	    	//후판제품창고	
	    	}else if(szYD_AIM_YD_GP.equals("K")) {
	    		setRecord.setField("ARR_WLOC_CD", "DKY30");
	    	
	    	//통합야드
	    	}else if(szYD_AIM_YD_GP.equals("S")) {
	    		setRecord.setField("ARR_WLOC_CD", "DJY25"); //(비상야드추가)
	    	
	    	//A열연COIL야드
	    	}else if(szYD_AIM_YD_GP.equals("1")) {
	    		setRecord.setField("ARR_WLOC_CD", "D2Y45");
	    	
	    	//B열연SLAB야드	
	    	}else if(szYD_AIM_YD_GP.equals("2")) {
	    		setRecord.setField("ARR_WLOC_CD", "D3Y43");
	    	
	    	//B열연 COIL야드	
	    	}else if(szYD_AIM_YD_GP.equals("3")) {
	    		setRecord.setField("ARR_WLOC_CD", "D3Y42");
	    	
	    	//A열연 SLAB야드	
	    	}else if(szYD_AIM_YD_GP.equals("0")) {
	    		setRecord.setField("ARR_WLOC_CD", "D2Y43");
	    		
	    	}
	    	
	    	intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
	    	if(intRtnVal <= 0 ) {
                szMsg = "권하작업시 차량스케줄에 착지개소코드 등록중 Error!! Code No :" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
                throw new  JDTOException("<procY0CrnUdWr> Y0SetYdCar" + szMsg);
	    	}
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	
	    	int szRowSize = inRecordSet.size(); 

	    	// 권상한 재료만큼 차량스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  
	    	for(int i = 0; i < szRowSize; i++){
	    		
	        	lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(getRecord, "YD_MTL_WT");
	        	intYD_MTL_SH = i + 1;               
	    		
		    	// 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//차량 이송재료 등록 (하차 )
	    		if(intGp == 0) {
	    			setRecord.setField("MODIFIER", 			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",       			"Y");
		    		intRtnVal = this.Y0UpdCarftmvmtl(setRecord, 0) ;
			    	
			    //차량 이송재료 등록 (상차 )	
	    		}else if(intGp == 1) {
	    			setRecord.setField("REGISTER", 			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",        "N");
		    		setRecord.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
		    		setRecord.setField("HCR_GP",	    ydDaoUtils.paraRecChkNull(getRecord,"HCR_GP"));
		    		setRecord.setField("STL_PROG_CD",	ydDaoUtils.paraRecChkNull(getRecord,"STL_PROG_CD"));
		    		setRecord.setField("YD_MTL_ITEM",	ydDaoUtils.paraRecChkNull(getRecord,"YD_MTL_ITEM"));
		    		setRecord.setField("YD_ROUTE_GP",	ydDaoUtils.paraRecChkNull(getRecord,"YD_ROUTE_GP"));
		    		
		    		intRtnVal = this.Y0InsYdCarftmvmtl(setRecord) ;
	    		}
		    	inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	
	    	if(intGp == 1) {
    			//차량스케줄에 등록한다.
    			lngYD_EQP_WRK_WT = lngYD_EQP_WRK_WT + lngYD_MTL_WT;
    			intYD_EQP_WRK_SH = intYD_EQP_WRK_SH + intYD_MTL_SH;
    	    	//setRecord 초기화
    	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("YD_EQP_WRK_WT",       	"" + lngYD_EQP_WRK_WT);
	    		setRecord.setField("YD_EQP_WRK_SH",       	"" + intYD_EQP_WRK_SH);
	    		
	    		intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
    		}
	    	
	    	
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y0SetYdTcar> " + szMsg);
		}//end of try~catch
		
    	return 1 ;
    	
    }//end of Y0SetYdCar()
    
    /**
     * 오퍼레이션명 : 작업예약 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0UpdYdWrkbook(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdWrkbookDao ydWrkbookDao   = new YdWrkbookDao(); 
    	
    	String szMsg = "";
    	String szMethodName = "Y0UpdYdWrkbook";
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydWrkbookDao.updYdWrkbook(msgRecord, intGp);

		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			throw new JDTOException("<Y0UpdYdWrkbook> " + szMsg);
	    }//end of try~catch
		
		return intRtnVal ;
    }//end of Y0UpdYdWrkbook
    
    /**
     * 오퍼레이션명 : 통합야드 적치단 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y0UpdYdStklyr (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	int intRtnVal = 0 ;
    	
    	String szMsg 		= "";
    	String szMethodName = "Y0UpdYdStklyr";
    	String szOperationName              = "통합야드 적치단 Update";
    	
        try{
        	
	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    				/*
    				 * 모음작업시에는 단이 존재하지 않을 수 있으므로 적치단이 존재하지 않더라도
    				 * 업무는 진행이 되도록 아래 부분을 수정
    				 * 수정자 : 임춘수
    				 * 수정일 : 2009.09.21
    				 */
    				szMsg="적치단이 존재하지 않습니다. - 모음작업 시 오류발생 가능 ";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				intRtnVal = 1;
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
			
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y0UpdYdStklyr> " + szMsg);
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y0UpdYdStklyr
    
    /**
     * 오퍼레이션명 : 진도코드갱신 Setting 
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y0SetProgCode (JDTORecord msgRecord) throws JDTOException{
    	
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szCurrProgCd					= "";
    	String szBefoProgCd					= "";
    	
    	String szMsg						= "";
    	String szMethodName					= "Y0SetProgCode";
    	//재료종류별 번호
    	String szStlNo						= "";
    	int intRtnVal 						= 0 ;
    	String szRtnMsg						= null;
    	String szSLAB_WO_RT_CD				= null;
    	String szSCARFING_YN				= null;
    	String szSCARFING_DONE_YN			= null;
    	String szMILL_WO_EXN				= "";
    	String szSTL_APPEAR_GP				= null;
    	String szHCR_GP						= null;
    	String szPT_TB_COMM					= null;
    	//전전진도코드
    	String szBEFOBEFO_PROG_CD			= null;
    	//주문여재구분
    	String szORD_YEOJAE_GP  = 			null;
    	String szCURR_PROG_REG_DDTT  = 			null;
    	String szBEFO_PROG_REG_DDTT  = 			null;
    	String szBEFOBEFO_PROG_REG_DDTT  = 			null;
    	String szCURR_PROG_CD_REG_PGM  = 			null;
    	String szBEFO_PROG_CD_REG_PGM  = 			null;
    	String szBEFOBEFO_PROG_CD_REG_PGM  = 			null;
    	
    	ymCommonDAO dao = ymCommonDAO.getInstance();
	    List FrtoProductList = null;
	    
        try{
        	
        	szStlNo 	= msgRecord.getFieldString("STL_NO") ;

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        	 * 업무기준 : C연주 슬라브야드, 통합야드에 이송하차 완료 시 현재재료진도코드를 판단하여
        	 * 			주편/슬라브공통테이블에 업데이트 처리
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.21
        	 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	szRtnMsg = YdCommonUtils.getPtCommStock(szStlNo, getRecSet);
        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
        		szMsg = "[진도코드갱신 - Y0SetProgCode] 주편/슬라브공통테이블에서 재료[" + szStlNo + "] 조회 시 오류발생 : " + szRtnMsg;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
                throw new JDTOException(szMsg);
        	}
        	
        	getRecSet.first();
        	getRecord = getRecSet.getRecord() ;
        	
        	szPT_TB_COMM  			= ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");
        	//주문여재구분
        	szORD_YEOJAE_GP  		= ydDaoUtils.paraRecChkNull(getRecord, "ORD_YEOJAE_GP");
        	//슬라브지시행선코드
        	szSLAB_WO_RT_CD  		= ydDaoUtils.paraRecChkNull(getRecord, "SLAB_WO_RT_CD");
        	//스카핑여부
        	szSCARFING_YN  			= ydDaoUtils.paraRecChkNull(getRecord, "SCARFING_YN");
        	//스카핑완료여부
        	szSCARFING_DONE_YN  	= ydDaoUtils.paraRecChkNull(getRecord, "SCARFING_DONE_YN");
        	//압연지시여부 - 슬라브에만 적용됨
        	if(szPT_TB_COMM.equals("S")) {
        		szMILL_WO_EXN  		= ydDaoUtils.paraRecChkNull(getRecord, "MILL_WO_EXN");
        	}
        	
        	szSTL_APPEAR_GP  		= ydDaoUtils.paraRecChkNull(getRecord, "STL_APPEAR_GP");
        	
        	szHCR_GP  				= ydDaoUtils.paraRecChkNull(getRecord, "HCR_GP");
        	
        	szMsg = "[진도코드갱신 - Y0SetProgCode] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + "[" + szStlNo + "], ";
        	szMsg += "주문여재구분[" + szORD_YEOJAE_GP + "], 슬라브지시행선코드[" + szSLAB_WO_RT_CD + "], 스카핑여부[" + szSCARFING_YN + "], 스카핑완료여부[" + szSCARFING_DONE_YN + "], 압연지시여부[" + szMILL_WO_EXN + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//현재진도코드
        	szCurrProgCd = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD");
        	//전 진도코드
        	szBefoProgCd = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD");
        	//전전진도코드 = 전 진도코드
        	szBEFOBEFO_PROG_CD = szBefoProgCd;
        	//전전진도코드 = 전진도코드
        	szBefoProgCd = szCurrProgCd;
        	
        	//현재진도코드등록Program
        	szCURR_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD_REG_PGM");
        	//전진도코드등록Program
        	szBEFO_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD_REG_PGM");
        	//전전진도코드등록Program
        	szBEFOBEFO_PROG_CD_REG_PGM = szBEFO_PROG_CD_REG_PGM;
        	szBEFO_PROG_CD_REG_PGM = szCURR_PROG_CD_REG_PGM;
        	szCURR_PROG_CD_REG_PGM = szMethodName;
        	
        	//현재진도등록일시
        	szCURR_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_REG_DDTT");
        	//전진도등록일시
        	szBEFO_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_REG_DDTT");
        	//전전진도등록일시
        	szBEFOBEFO_PROG_REG_DDTT = szBEFO_PROG_REG_DDTT;
        	szBEFO_PROG_REG_DDTT = szCURR_PROG_REG_DDTT;
        	szCURR_PROG_REG_DDTT = YdUtils.getCurDate("yyyyMMddHHmmss");
        	
        	//szCurrProgCd = YdCommonUtils.getCurrProgCd(szPT_TB_COMM, szSLAB_WO_RT_CD, szORD_YEOJAE_GP, szSCARFING_YN, szSCARFING_DONE_YN, szMILL_WO_EXN,"S",szSTL_APPEAR_GP);
			//---------------------------------------------------------------------------------------------------------
			//---------------------------------------------------------------------------------------------------------
        	//공정 함수를 이용한 진도코드 가져오기
        	if(szPT_TB_COMM.equals("B")){
        		//주편 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcd";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szStlNo});
        	}else if (szPT_TB_COMM.equals("S")) {
        		//슬라브 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcdSlab";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szStlNo});
        	}       	

	    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);

	    	szCurrProgCd =StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
			ydUtils.putLog(szSessionName, szMethodName, "IHSF_PM_주편SLAB진도찾기==>>:"+szCurrProgCd, YdConstant.DEBUG);
			//---------------------------------------------------------------------------------------------------------
			//---------------------------------------------------------------------------------------------------------
        	
        	szMsg = "[진도코드갱신 - Y0SetProgCode] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + " 재료[" + szStlNo + "] 수정 후 현재진도코드[" + szCurrProgCd + "], 전진도코드[" + szBefoProgCd + "], 전전진도코드[" + szBEFOBEFO_PROG_CD + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//전 진도코드 = 현재 진도코드,   전전진도코드 = 전진도코드
        	//전 진도코드등록일시 = 현재진도코드등록일시 , 전전진도코드등록일시 = 전진도코드등록일시
			//현재시간
        	setRecord.setField("CURR_PROG_CD", 					szCurrProgCd);
        	setRecord.setField("BEFO_PROG_CD", 					szBefoProgCd);
        	setRecord.setField("BEFOBEFO_PROG_CD", 				szBEFOBEFO_PROG_CD);
			setRecord.setField("CURR_PROG_REG_DDTT", 			szCURR_PROG_REG_DDTT);
			setRecord.setField("BEFO_PROG_REG_DDTT", 			szBEFO_PROG_REG_DDTT);
			setRecord.setField("BEFOBEFO_PROG_REG_DDTT", 		szBEFOBEFO_PROG_REG_DDTT);
			setRecord.setField("CURR_PROG_CD_REG_PGM", 			szCURR_PROG_CD_REG_PGM);
			setRecord.setField("BEFO_PROG_CD_REG_PGM", 			szBEFO_PROG_CD_REG_PGM);
			setRecord.setField("BEFOBEFO_PROG_CD_REG_PGM", 		szBEFOBEFO_PROG_CD_REG_PGM);
			setRecord.setField("FNL_REG_PGM", 					szMethodName);
			setRecord.setField("MODIFIER", 					    "YDSYSTEM");
			

        	//업데이트 항목 추가해야함 ///////////////////////////////////////////////////////
        	if(szPT_TB_COMM.equals("B")){
        		//주편 공통
        		setRecord.setField("MSLAB_NO", szStlNo);
        		intRtnVal = this.Y0UpdPtComm(setRecord,  2);
        	}else if (szPT_TB_COMM.equals("S")) {
        		//슬라브 공통
        		setRecord.setField("SLAB_NO", szStlNo);
        		intRtnVal = this.Y0UpdPtComm(setRecord,  0);
        	}
        	
        	//----------------------------------------------------------------------------------------------------------
			//	이송하차완료 시 각 재료의 목표야드, 목표동, 목표행선, 진도코드를 설정한다
			//	수정자 : 임춘수
			//	수정일 : 2010.01.06
			//----------------------------------------------------------------------------------------------------------
	        	
        	JDTORecord 	  recTemp 			= JDTORecordFactory.getInstance().create();
        	
        	recTemp.setField("PT_TB_COMM", 			szPT_TB_COMM);						//주편/슬라브구분
			recTemp.setField("STL_NO", 				szStlNo);							//재료번호
			recTemp.setField("SLAB_WO_RT_CD", 		szSLAB_WO_RT_CD);					//슬라브지시행선코드
			recTemp.setField("ORD_YEOJAE_GP", 		szORD_YEOJAE_GP);					//주여구분
			recTemp.setField("SCARFING_YN", 		szSCARFING_YN);						//스카핑여부
			recTemp.setField("SCARFING_DONE_YN", 	szSCARFING_DONE_YN);				//스카핑완료여부
			recTemp.setField("MILL_WO_EXN", 		szMILL_WO_EXN);						//압연지시
			recTemp.setField("YD_GP", 				YdConstant.YD_GP_C_SLAB_YARD);		//야드구분
			//항만야드구분을 set하는 기준변경 필요 : 2015.12.30 LeeJY
			//recTemp.setField("YD_GP", 				YdConstant.YD_GP_PORT_SLAB_YARD);		//야드구분
			recTemp.setField("STL_APPEAR_GP", 		szSTL_APPEAR_GP);					//재료외형구분
			recTemp.setField("HCR_GP", 				szHCR_GP);							//HCR구분
			
			String szRetunMsg = YdCommonUtils.uptStockCodeMapping(recTemp);
			
			szMsg="[진도코드갱신 - Y0SetProgCode] 재료["+szStlNo+"]의 속성을 수정 완료 - 메세지 : " + szRetunMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------------------------------------
        	
        }catch(Exception e){
        	szMsg = "[진도코드갱신 - Y0SetProgCode] 오류발생 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException("<Y0SetProgCode> " + szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y0SetProgCode()
    
    /**
     * 오퍼레이션명 : 저장위치 Setting 
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int Y0setYdStrLoc (JDTORecord msgRecord) throws JDTOException{
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= null;
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= null;
    	
    	//현재저장위치
    	String szYdStrLoc					= "";
    	//이전저장위치
    	String szYdStrLocHis1				= "";

    	String szMsg						= "";
    	String szMethodName					= "Y0setYdStrLoc";
    	String szOperationName              = "저장위치 Setting";
    	//재료품목 정의
    	String szYdMtlItem					= "";
    	
    	//크레인스케줄의 정보				현재저장위치를 생성하기 위해...
    	String szYdGp						= "";
    	String szYdBayGp					= "";
    	String szYdEqpId					= "";
    	String szYdStkColNo					= "";
    	String szYdStkBedNo					= "";
    	String szYdStkLyrNo					= "";
    	String szYdDnWrLoc                  = "";
    	String szYD_GP						= null;
    	String szSTL_NO						= null;
    	String szRtnMsg						= null;
    	String szLogMsg						= null;
    	String szPT_TB_COMM					= null;
    	
    	int intRtnVal 						= 0;
        
        try{


            szMsg = "저장위치 Setting ( Y0setYdStrLoc ) ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
            szYdMtlItem = ydDaoUtils.paraRecChkNull(msgRecord,"YD_MTL_ITEM"); 
			if(szYdMtlItem.length() > 1){
				szYdMtlItem = szYdMtlItem.substring(0, 1);
			}
			/*
        	 * 공통테이블의 재료 정보를 조회해서 업데이트를 처리
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.15
        	 */
        	szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "MSLAB_NO");
        	szRtnMsg = YdCommonUtils.getPtCommStock(szSTL_NO, getRecSet);
        	
        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
        		szLogMsg = "[저장위치 Setting - Y0setYdStrLoc]재료[" + szSTL_NO + "]를 공통테이블에서 조회 시 오류발생";
                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
                throw new JDTOException(szLogMsg);
        	}
        	
        	getRecSet.absolute(1);
        	getRecord      = JDTORecordFactory.getInstance().create();
        	getRecord 	   = getRecSet.getRecord();
        	szYdStrLoc 	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC").trim();
        	szYdStrLocHis1 = ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC_HIS1").trim();
        	szPT_TB_COMM   = ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");
        	
        	szYdGp 		   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").trim(); 
        	szYdBayGp 	   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
        	szYdEqpId 	   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP").trim(); 
        	szYdStkColNo   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO").trim(); 
        	szYdStkBedNo   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO").trim(); 
        	szYdStkLyrNo   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_LYR_NO").trim();
        	
        	setRecord = JDTORecordFactory.getInstance().create();
        	setRecord.setField("YD_GP",         szYdGp);
        	setRecord.setField("YD_BAY_GP",     szYdBayGp);
        	setRecord.setField("YD_EQP_GP",     szYdEqpId);
        	setRecord.setField("YD_STK_COL_NO", szYdStkColNo);
        	setRecord.setField("YD_STK_BED_NO", szYdStkBedNo);
        	setRecord.setField("YD_STK_LYR_NO", szYdStkLyrNo);
        	setRecord.setField("FNL_REG_PGM",   "Y0setYdStrLoc");
        	/*
        	 * 권하위치의 야드구분이 다른 경우에는 입고일자와 입고시각을 업데이트 처리
        	 * 주편공통, 슬라브공통 : 입고일자, 입고시각
        	 * PLATE공통 : 입고일자
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.14
        	 */
        	//공통테이블에 저장되어 있는 야드구분
        	szYD_GP	= ydDaoUtils.paraRecChkNull(getRecord,"YD_GP");
        	if( !szYD_GP.equals(szYdGp) ) {
        		String szCurDateTime = YdUtils.getCurDate("yyyyMMddHHmmss");
        		String szRECEIPT_DATE = szCurDateTime.substring(0, 8);
        		String RECEIPT_TIME = szCurDateTime.substring(8);
        		if(szPT_TB_COMM.equals("B") || szPT_TB_COMM.equals("S") ) {
        			setRecord.setField("RECEIPT_DATE", 	szRECEIPT_DATE);				//입고일자
        			setRecord.setField("RECEIPT_TIME", 	RECEIPT_TIME);					//입고시각
        		}else if ( szYdMtlItem.equals("P") ) {
        			setRecord.setField("RECEIPT_DATE", 	szRECEIPT_DATE);				//입고일자
        		}
        	}
        	setRecord.setField("MODIFIER",      "YDSYSTEM");
        	
        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
	        if(szYdStrLoc.equals("")){
	        	setRecord.setField("YD_STR_LOC_HIS1", 	"");
	        }else{
	        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc);
	        }
	        
	        if(szYdStrLocHis1.equals("")){
	        	setRecord.setField("YD_STR_LOC_HIS2", 	"");
	        }else{
	        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1);
	        }

	        //현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우
	        szMsg = "전 저장위치 : " + szYdStrLoc + " , 전전 저장위치 : " + szYdStrLocHis1 + ", 권하실적 단 정보 : " + szYdStkLyrNo;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	if(szPT_TB_COMM.equals("B")){
        		
        		setRecord.setField("MSLAB_NO",    msgRecord.getFieldString("MSLAB_NO")); 
        		setRecord.setField("YD_STR_LOC",  szYdGp+szYdBayGp+szYdEqpId+
        										  szYdStkColNo+szYdStkBedNo+szYdStkLyrNo.substring(1,3));

        		intRtnVal = this.updY0YdStock(setRecord, 2);
        		
        	}else if (szPT_TB_COMM.equals("S")) {
            	
        		setRecord.setField("SLAB_NO",     msgRecord.getFieldString("SLAB_NO")); 
        		setRecord.setField("YD_STR_LOC",  szYdGp+szYdBayGp+szYdEqpId+
        				                          szYdStkColNo+szYdStkBedNo+szYdStkLyrNo.substring(1,3));
        		//슬라브 공통 업데이트
        		intRtnVal = this.updY0YdStock(setRecord,  0);
        		
         	}
        }catch(Exception e){
        	szMsg = "Error : "+ e.getLocalizedMessage();
			throw new JDTOException("<Y0setYdStrLoc> " + szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y0setYdStrLoc()
    
    
    /**
     * 오퍼레이션명 : 통합야드 저장품 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed
     * @throws 
     */
    public int updY0YdStock (JDTORecord msgRecord, int intGp) throws JDTOException{
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "updY0YdStock";
        String szOperationName              = "통합야드 저장품 Update";
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
	            return intRtnVal ;
	        }
        }catch(Exception e){
        	szMsg = "Error : "+ e.getLocalizedMessage();
			throw new JDTOException("<updY0YdStock> " + szMsg);
        }//end of try~catch
        
        return intRtnVal ;
    	
    }//end of updY0YdStock()
    
    /**
     * 오퍼레이션명 : 통합야드 차량 스케줄 Select
     *  
     * @param msgRecord, outRecset, intGp(1:상하차)
     * @return intRtnVal
     * @throws 
     */
    public int Y0GetYdCarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp) throws JDTOException{
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	String szOperationName = "통합야드 차량 스케줄 Select";
    	String szMethodName = "Y0GetYdCarsch";
    	String szMsg        = "";
    	
        try{
        	
        	intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);
	        
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="[" + szOperationName + "] data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="[" + szOperationName + "] parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				if (intRtnVal <= 0) return intRtnVal = -2;
			}
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y0GetYdCarsch> " + szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y0GetYdCarsch
    
    /**
     * 오퍼레이션명 : 통합야드 차량 이송재료 Update
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y0UpdCarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
    	String szOperationName = "통합야드 차량 이송재료 Update";
    	String szMethodName = "Y0UpdCarftmvmtl";
    	String szMsg        = "";
        
        try{
        	
            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found!!";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
		
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y0UpdCarftmvmtl> " + szMsg);
	    }//end of try~catch	
		
		return intRtnVal ;
		
    }//end of Y0UpdCarftmvmtl
    
    /**
     * 오퍼레이션명 : 통합야드 차량이송재료 Insert
     *  
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y0InsYdCarftmvmtl(JDTORecord msgRecord) throws JDTOException{
    	YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
    	
    	int intRtnVal = 0 ;

    	String szMethodName = "Y0InsYdCarftmvmtl";
    	String szMsg        = "";
    	String szOperationName = "통합야드 차량이송재료 Insert";
        try{
        	intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
    		if(intRtnVal == -2) {
				szMsg="[" + szOperationName + "] parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
    		}
        	
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y0InsYdCarftmvmtl> " + szMsg);
        }//end of try~catch
        
        return intRtnVal = 1;
    }//end of Y0InsYdCarftmvmtl
    
    /**
     * 오퍼레이션명 : 통합야드 저장품 Select
     *  
     * @param msgRecord, outRecset, intGp
     * @return outRecset
     * @throws 
     */
    public int Y0GetYdStock (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp) throws JDTOException{
    	YdStockDao ydStockDao = new YdStockDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "";
    	String szMethodName		= "Y0GetYdStock";
    	String szOperationName = "통합야드 저장품 Select";
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
	            return intRtnVal ;
	        }
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
        	szMsg = "Error : "+ e.getLocalizedMessage();
			throw new JDTOException("<Y0GetYdStock> " + szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y0GetYdStock()
    
    /**
     * 오퍼레이션명 : 통합야드 저장품 Update
     *  
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws 
     */
    public int Y0UpdPtComm (JDTORecord msgRecord, int intGp) throws JDTOException{
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	//JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "Y0UpdPtComm";
        String szOperationName = "통합야드 저장품 Update";
        try{
        	//intRtnVal = ydStockDao.updPtComm_LOC(msgRecord, intGp);
        	//임춘수 수정 저장위치 변경이 아닌 진도코드 업데이트 2009.07.21
        	intRtnVal = ydStockDao.updPtComm_PROG_CD(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
        }catch(Exception e){
        	szMsg = "Error : "+ e.getLocalizedMessage();
			throw new JDTOException("<Y0UpdPtComm> " + szMsg);
        }//end of try~catch
        
        
        return intRtnVal ;
    	
    }//end of Y0UpdPtComm()
   
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
     * 오퍼레이션명 : C연주 권하실적처리 (Y1YDL009)
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY1CrnUdWr(JDTORecord msgRecord) throws DAOException {
		EJBConnector ejbConn 		= null;
		String szMethodName         = "procY1CrnUdWr";
		String szRtnMsg             = "";		
		String szLogMsg             = "";
		String szEjbJndiName 	= "CraneLdHdSeEJB";
		String szEjbMethod 		= "procY1CrnWrkOrdReq";
		JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
		try {
			
			//C연주 권하실적처리
			ejbConn = new EJBConnector("default", "CraneUdHdSeEJB", this);
			outRecord =(JDTORecord)ejbConn.trx("procY1CrnUdWrTX", new Class[] { JDTORecord.class}, new Object[] { msgRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), YdConstant.RETN_CD_FAILURE);
			
			if( sRTN_CD.equals(YdConstant.RETN_CD_FAILURE) ) {			//성공
				szLogMsg = "권하처리 오류 (" + szRtnMsg + ")";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, 1);
				return YdConstant.RETN_CD_FAILURE;
			}

			
			//크레인 작업지시 호출
			szLogMsg = "[C연주 권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

//SJH040002
//			ejbConn = new EJBConnector("default", szEjbJndiName, this);			
//			szRtnMsg = (String)ejbConn.trx(szEjbMethod, new  Class[] { JDTORecord.class }, new Object[] { outRecord });

			String sMSG_ID	= StringHelper.evl(outRecord.getFieldString("MSG_ID"),"");
			if (sMSG_ID.equals("YDYDJ640")) {			
				//크레인작업지시 송신
				ydDelegate.sendMsg(outRecord);
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			}	
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || 
				szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || 
				szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
				szLogMsg = "[C연주 권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}else{
				szLogMsg = "[C연주 권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szSessionName + e.getMessage(), e);
		}
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procY1CrnUdWr
	
    /**
     * 오퍼레이션명 : C연주 권하실적처리 (Y1YDL009)
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */         
    public JDTORecord procY1CrnUdWrTX(JDTORecord msgRecord)throws JDTOException  {
    	
    	YdDelegate      ydDelegate      = new YdDelegate();
    	YdEqpDao        ydEqpDao        = new YdEqpDao();
    	YdTcarSchDao    ydTcarSchDao    = new YdTcarSchDao();
    	YdCarSchDao     ydCarSchDao     = new YdCarSchDao();
    	YdStockDao      ydStockDao      = new YdStockDao();
    	YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
    	YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
    	YdStkColDao     ydStkColDao     = new YdStkColDao();
    	YdCrnWrkMtlDao  ydCrnWrkMtlDao  = new YdCrnWrkMtlDao();
    	YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();
    	
        int intRtnVal = 0;
        
        //DATA SETTING시 사용
        JDTORecord setRecord 				= JDTORecordFactory.getInstance().create();
        
        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord                = JDTORecordFactory.getInstance().create(); ;
        
        //작업예약 업데이트 항목
        JDTORecord bookrecord               = JDTORecordFactory.getInstance().create();
        
        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 			= JDTORecordFactory.getInstance().create();
       
        JDTORecord recInTemp                = null;
        JDTORecord recOutTemp               = null;
        JDTORecord recInPara                = null;
        
        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
        
        JDTORecord    recSendMsg            = null;
        JDTORecordSet rsResult              = null;
        
        String szMsg                        = "";
        String szMethodName                 = "procY1CrnUdWrTX";
        String szOperationName              = "C연주권하실적처리";
        String szTcarEqpId                  = "";
        
        //크레인 XYZ축 			파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
        String szYD_CRN_XAXIS     			= "";
        String szYD_CRN_YAXIS     			= "";
        String szYD_CRN_ZAXIS     			= "";
        //WBOOK_ID				작업예약 완료 처리시 사용
        String szYdWbookId              	= "";
        String szCrnSchId                   = "";
        
        String szYD_UP_WR_LOC               = "";
        String szYD_DN_WR_LOC               = "";
        String szYD_SCH_CD                  = "";
        String szYD_EQP_ID                  = "";
        String szYD_DN_WR_LAYER             = "";
        String szSTL_NO      				= "";
        String szYD_CAR_SCH_ID              = "";
        String szYD_TCAR_SCH_ID             = "";
        String szYD_WRK_PROG_STAT           = "";
        
        //-----------------------------------------------------------------------------------------
        //실제Lyr를 검사하여 처리하기 위해 필요한 변수들
        JDTORecordSet getLyrSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
        
        String szYD_STK_COL_GP              = "";
        String szYD_STK_BED_NO              = "";
        String szREAL_TOP_LYR               = "";
        int    intRealTopLyr                = 0;
        int    intYdDnWrLayer               = 0;
        int    rowsize                      = 0;
        //-----------------------------------------------------------------------------------------
        
        //EJB CALL or JMS CALL
        String szIS_EJB_CALL = null;
        
        String szLogMsg = null;
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if( szRcvTcCode==null || szRcvTcCode.equals("") ){
            szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            
            szMsg = YdConstant.RETN_CD_TC_ERROR;
            throw new JDTOException("<procY1CrnUdWr> " + szMsg);
        }
        
        if(bDebugFlag){
            szMsg="["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
        	
        try{
        	szMsg="["+szOperationName+"] 메소드 시작 - 파라미터 확인";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	//=============================================================
        	// Log 테이블 등록 
        	//=============================================================
        	szMsg = "[C연주정정] 권하실적처리 수신";
        	ydUtils.putLogMsg("A", "yd_monitorA", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

        	//-------------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------------
        	ydUtils.displayRecord(szOperationName, msgRecord);
        	
        	szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
        	
	        intRtnVal = this.Y1ParamCheck(msgRecord, getCrnschRecord, 1) ;
	        
	        szCrnSchId 				= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_CRN_SCH_ID");
	        szYD_EQP_ID 			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_EQP_ID");
	        szYD_SCH_CD 			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_SCH_CD");
	        szYD_DN_WR_LOC			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC");
	        szYD_DN_WR_LAYER		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER");
	        szYD_WRK_PROG_STAT		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_WRK_PROG_STAT");
	        
	        if( szCrnSchId.equals("") ) {
                szMsg = "["+szOperationName+"] 크레인스케줄ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY1CrnUdWr> " + szMsg);
	        }
        	
	        //파라미터 레코드 편집
	        setRecord.setField("YD_CRN_SCH_ID",       	szCrnSchId);
	        setRecord.setField("YD_SCH_CD",           	szYD_SCH_CD);
	        setRecord.setField("YD_DN_WR_LOC",        	szYD_DN_WR_LOC);
	        setRecord.setField("YD_DN_WR_LAYER",      	szYD_DN_WR_LAYER);
	        //-------------------------------------------------------------------------------------------------------------------
	        	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        
			intRtnVal = this.Y1UpdYdCrnsch(setRecord, 0);
			
			szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------------------------
            
            //-------------------------------------------------------------------------------------------------------------------
	        //	크레인스케줄 작업재료 조회
            //-------------------------------------------------------------------------------------------------------------------
            szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        intRtnVal = this.Y1GetYdCrnsch(setRecord, getRecSet,3);
	        if(intRtnVal < 0){
				szMsg = "[" + szOperationName + "] 크레인스케줄  Y1GetYdCrnsch failed!!!, ErrorCode:" + intRtnVal;
		        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		        m_ctx.setRollbackOnly();
		        throw new JDTOException("<procY1CrnUdWr> Y1GetYdCrnsch" + szMsg);
			}
	        
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "[" + szOperationName + "] 권하실적 크레인작업재료 : no data found!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            m_ctx.setRollbackOnly();
	            throw new JDTOException("<procY1CrnUdWr> " + szMsg);
	        }
	        
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();
	        
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 완료 - 대상재 건수 : " + getRecSet.size();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
            szYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
	        //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szYdWbookId = ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
            //-------------------------------------------------------------------------------------------------------------------
            
	        //-------------------------------------------------------------------------------------------------------------------
	        //	작업진행상태 확인 후 상태가 맞지 않으면 롤백 처리
            //-------------------------------------------------------------------------------------------------------------------
        	//작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
	        if ((!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("2"))&& 
	        	(!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("3"))) {
	        	szMsg = "["+szOperationName+"] 작업진행상태["+ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT")+"]가   권상('2') 또는 권하대기('3')이 아닙니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            m_ctx.setRollbackOnly();
	            throw new JDTOException("<procY1CrnUdWr> " + szMsg);
	        }
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하지시위치와 권하실적위치가 다르면 권하지시위치의 맵정보를 Clear시킴
	        //-------------------------------------------------------------------------------------------------------------------
	        if (!getRecord.getFieldString("YD_DN_WR_LOC").equals(getRecord.getFieldString("YD_DN_WO_LOC"))) {
                szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	        	
	        	//크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
	        	if(szYD_CRN_XAXIS.equals("") && szYD_CRN_YAXIS.equals("") && szYD_CRN_ZAXIS.equals("")) {
	        		szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_XAXIS");
	        		szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_YAXIS");
	        		szYD_CRN_ZAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_ZAXIS");
	        	}
	        	
	        	intRtnVal = this.Y1ClearYdStklyr(getRecSet,1) ;
	        	if(intRtnVal < 0){
			        m_ctx.setRollbackOnly();
			        throw new JDTOException("<procY1CrnUdWr> Y1ClearYdStklyr" + szMsg);
				}
    	        
    	        szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 완료";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        }
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하실적LYR와 실제적치단의 최상위 위치가 다르면 권하실적LYR를 변경하고 적치단 정보를 수정한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        szYD_STK_COL_GP = szYD_DN_WR_LOC.substring(0, 6);
	        szYD_STK_BED_NO = szYD_DN_WR_LOC.substring(6, 8);
	        intYdDnWrLayer 	= Integer.parseInt(szYD_DN_WR_LAYER);
	        	                
	        recInPara	= JDTORecordFactory.getInstance().create();
	        recInPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	        recInPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
	        
	        intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, getLyrSet, 98);
	        
	        if(intRtnVal < 0){
	        	m_ctx.setRollbackOnly();
	        	throw new JDTOException("<procY1CrnUdWr> getYdStklyr ERROR CODE =" + intRtnVal);
			}
	        
	        getLyrSet.first();
	        recOutTemp = getLyrSet.getRecord();
	        
	        szREAL_TOP_LYR 	= ydDaoUtils.paraRecChkNull(recOutTemp,"REAL_TOP_LYR");
	        intRealTopLyr 	= Integer.parseInt(szREAL_TOP_LYR);
	        
	        if (intYdDnWrLayer != intRealTopLyr) {
	        	
	        	szMsg = "[" + szOperationName + "] 실적적치단[" + intYdDnWrLayer + "]과 실재야드적치단[" + intRealTopLyr + "]이 상이하여 실적적치단 변경 시작";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	
	            szYD_DN_WR_LAYER = szREAL_TOP_LYR;
    	    }
	        //-------------------------------------------------------------------------------------------------------------------	
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //	크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 재료진도,현저장위치를 수정
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        
	        intRtnVal = this.Y1RegYdStklyr(	getRecSet,
	        								szYD_DN_WR_LAYER) ;

	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        //-------------------------------------------------------------------------------------------------------------------
	      
            //------------------------------------------------------------------------------
		    //	동내이적의 권하분리 시 마지막 스케줄이 아닌 스케줄의 TO위치결정방법이 S, T인 경우에는
		    //	해당 베드를 완산베드로 설정을 해서 다음 스케줄에서 TO위치결정 시 베드가 제외되도록 처리
		    //	수정일 : 2010.03.11 - 임춘수
		    //------------------------------------------------------------------------------
            String szYD_TO_LOC_DCSN_MTD			= ydDaoUtils.paraRecChkNull(getRecord,"YD_TO_LOC_DCSN_MTD");
	        szYD_DN_WR_LOC 						= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC");
	        
			if( szYD_TO_LOC_DCSN_MTD.equals("S") || 
				szYD_TO_LOC_DCSN_MTD.equals("T") ) {
				szMsg="[" + szOperationName + "] 권하분리 시 TO위치결정방법이 S, T이고 마지막 스케줄이 아닌 스케줄의 TO위치베드를 완산베드로 설정했으므로 해제 처리 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp 				= JDTORecordFactory.getInstance().create();
	        	recInTemp.setField("YD_STK_COL_GP", 			szYD_DN_WR_LOC.substring(0, 6));
	        	recInTemp.setField("YD_STK_BED_NO", 			szYD_DN_WR_LOC.substring(6));
	        	recInTemp.setField("YD_STK_BED_WHIO_STAT", 		"E");
	        	recInTemp.setField("MODIFIER", 					szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
	        	
	        	String szRtnMsg			= DaoManager.updYdStkbed(recInTemp, 0);
	        	
	        	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	        		szMsg="[" + szOperationName + "] 권하분리 시 TO위치결정방법이 S, T이고 마지막 스케줄이 아닌 스케줄의 TO위치베드를 완산베드로 설정했으므로 해제 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	}else{
	        		szMsg="[" + szOperationName + "] 권하분리 시 TO위치결정방법이 S, T이고 마지막 스케줄이 아닌 스케줄의 TO위치베드를 완산베드로 설정했으므로 해제 처리 시 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        	}
			}
			
    		//------------------------------------------------------------------------------
            
            //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      	szCrnSchId);
	        setRecord.setField("YD_DN_WR_LOC",       	szYD_DN_WR_LOC) ;
	        setRecord.setField("YD_DN_WR_LAYER",     	szYD_DN_WR_LAYER) ;
	        setRecord.setField("YD_DN_WRK_ACT_GP",   	getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP") );
	        setRecord.setField("YD_DN_WR_XAXIS",     	getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
	        setRecord.setField("YD_DN_WR_YAXIS",     	getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
	        setRecord.setField("YD_DN_WR_ZAXIS",     	getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
	        setRecord.setField("YD_WRK_PROG_STAT",   	getCrnschRecord.getFieldString("YD_WRK_PROG_STAT"));
	        setRecord.setField("YD_DN_CMPL_DT",      	YdUtils.getCurDate("yyyyMMddHHmmss"));
	        setRecord.setField("YD_GP",   				getCrnschRecord.getFieldString("YD_GP"));

	      //--------------------------------------------------------------------------------------------------
	        //	크레인스케줄 등록 시 야드작업계상일자에 등록되는 값을 권하완료시점에 다시 수정, 권하완료 시의 값이 사용됨
	        //	TO_CHAR(SYSDATE - (7 / 24), 'YYYYMMDD')
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.23
	        //--------------------------------------------------------------------------------------------------
	        String szYD_WRK_HDS_DD			= YdUtils.getDefaultHdsDate();
	        
	        setRecord.setField("YD_WRK_HDS_DD",  szYD_WRK_HDS_DD);
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]의 야드작업계상일자["+szYD_WRK_HDS_DD+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        //--------------------------------------------------------------------------------------------------
	        
	        intRtnVal = this.Y1UpdYdCrnsch(setRecord, 0);
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            szMsg = "[" + szOperationName + "] 권하실적위치의 설비구분 : " + szYD_DN_WR_LOC.substring(2, 4);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
            szMsg = "[" + szOperationName + "] 스케줄코드의 설비구분 : " + szYD_SCH_CD.substring(2, 4);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
            //-------------------------------------------------------------------------------------------------------------------
            
	        //-------------------------------------------------------------------------------------------------------------------
	        //	차량스케줄에 차량 상차작업예약ID등록 수정!!
	        //	만약  to위치는 차량인데 차량스케줄코드가 아닌경우...는 직상차로 보고 차량스케줄의 상차작업예약id를 등록한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        if(szYD_DN_WR_LOC.substring(2, 4).equals("PT") && 
	          !szYD_SCH_CD.substring(2, 4).equals("PT")){
	            	
	            //권하위치 적치열구분으로 적치열을 조회하여 운송장비코드를 조회한다.
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_STK_COL_GP", szYD_DN_WR_LOC.substring(0,6));
	            
	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        	intRtnVal = ydStkColDao.getYdStkcol(recInPara, rsResult, 0);
	        	if(intRtnVal <= 0) {
		            szMsg = szYD_DN_WR_LOC.substring(0,6) + "적치열구분이 잘못되었습니다.";
		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		            m_ctx.setRollbackOnly();
		            throw new JDTOException("<procY1CrnUdWr> getYdStkcol" + szMsg);
	        	}
	        	rsResult.absolute(1);
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setRecord(rsResult.getRecord());
	        	
	            //운송장비코드로 차량스케줄을 조회한다.
	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        	intRtnVal = ydCarSchDao.getYdCarsch(recInPara, rsResult, 7);
	        	if(intRtnVal <= 0) {
	                szMsg = ydDaoUtils.paraRecChkNull(recInPara, "TRN_EQP_CD") + "로 생성된 차량 스케줄이 없습니다.";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                m_ctx.setRollbackOnly();
	                throw new JDTOException("<procY1CrnUdWr> getYdCarsch" + szMsg);
	        	}
	        	rsResult.absolute(1);
	        	recOutTemp = JDTORecordFactory.getInstance().create();
	        	recOutTemp.setRecord(rsResult.getRecord());
	        	
	            //조회된 차량스케줄에 상차작업예약ID를 등록한다.
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID").trim());
	        	recInPara.setField("YD_CARLD_WRK_BOOK_ID", szYdWbookId);
	        	recInPara.setField("YD_EQP_WRK_STAT", "L");
	        	recInPara.setField("YD_CAR_PROG_STAT", "4");//상차개시
	        	recInPara.setField("YD_CARLD_STOP_LOC", szYD_DN_WR_LOC.substring(0,6));
	        	intRtnVal = ydCarSchDao.updYdCarsch(recInPara, 0);
	        	if(intRtnVal <= 0) {
	                szMsg = "직상차용 차량스케줄 상차작업예약 등록시 Error";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                m_ctx.setRollbackOnly();
	                throw new JDTOException("<procY1CrnUdWr> updYdCarsch" + szMsg);
	        	}
	        }
	        //-------------------------------------------------------------------------------------------------------------------

	        //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 대차이므로 대차 스케줄 이송재료 등록 후 대차스케줄 호출
	        //-------------------------------------------------------------------------------------------------------------------
	        if(szYD_DN_WR_LOC.substring(2, 4).equals("TC")){
            	
	        	//-------------------------------------------------------------------------------------------------------------------
	            //	권하실적위치로 대차설비ID 조합 생성
	            //-------------------------------------------------------------------------------------------------------------------
	        	szTcarEqpId = szYD_DN_WR_LOC.substring(0,1) + "XTC" + szYD_DN_WR_LOC.substring(4,6);
	        	//-------------------------------------------------------------------------------------------------------------------
	        	
	            //-----------------------------------------------------
                //	대차작업지정기준을 BRE Rule에서 조회
                //	수정일 : 1. 2010.02.25 - 임춘수
                //-----------------------------------------------------
                String[] szTCarRule	= YdCommonUtils.getTCarWrkStdRule(szTcarEqpId);
                //-----------------------------------------------------
                recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_WBOOK_ID", 					szYdWbookId);	//작업예약ID
	        	recInPara.setField("YD_WRK_PLAN_TCAR", 				szTcarEqpId);	//작업계획대차
	        	
                if( szTCarRule[0].equals("Y") ) {
                
                	if( szTCarRule[1].equals("D") ) {	//대차 직상차
                		//기준상차동과 실제작업한 상차동이 같을경우
                		if( szTCarRule[2].equals(szYD_DN_WR_LOC.substring(1, 2))){
                			//-----------------------------------------------------
            	        	//	대차작업지정기준이 직상차인 경우에는 하차동을 목표동으로 재설정
            	        	//-----------------------------------------------------
            	        	recInPara.setField("YD_AIM_BAY_GP", 			szTCarRule[3]);		//목표동
            	        	//-----------------------------------------------------
            	        }
	                }
		        }    
                
                intRtnVal = ydWrkbookDao.updYdWrkbook(recInPara, 0);
	        	if(intRtnVal <= 0) {
	    			szMsg = YdConstant.RETN_CD_FAILURE;
	    			m_ctx.setRollbackOnly();
	    			throw new JDTOException("<procY1CrnUdWr> updYdWrkbook" + szMsg);
	    		}
	        	szMsg = "[" + szOperationName + "] [대차 직상차]상차작업예약ID["+szYdWbookId+"]에 대차설비ID["+szTcarEqpId+"]  목표동["+szTCarRule[3]+"] 업데이트 완료";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
	        	//-------------------------------------------------------------------------------------------------------------------
	        	//	권하실적위치로 만들어진 대차설비ID를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 시작
	        	//-------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
	        	
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_EQP_ID", szTcarEqpId);
	        	
	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        	intRtnVal = ydTcarSchDao.getYdTcarsch(recInPara, rsResult, 4);
	        	if(intRtnVal <= 0) {
	                szMsg = "[" + szOperationName + "] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 대차스케줄 조회 시 오류발생 - 반환값 : " + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	                m_ctx.setRollbackOnly();
	                throw new JDTOException(szMsg);
	        	}
	        	
	        	rsResult.absolute(1);
	        	recOutTemp = JDTORecordFactory.getInstance().create();
	        	recOutTemp.setRecord(rsResult.getRecord());
	        	
	        	szMsg = "[" + szOperationName + "] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 완료 - 대상재건수 : " + rsResult.size();
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
                szYD_TCAR_SCH_ID		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
                
                //-------------------------------------------------------------------------------------------------------------------
	        	//	조회된 대차 스케줄에 상차작업예약id를 등록한다.
                //-------------------------------------------------------------------------------------------------------------------
                szMsg = "[" + szOperationName + "] [대차 상차]대차스케줄[" + szYD_TCAR_SCH_ID + "]에 상차작업예약ID["+szYdWbookId+"]를 업데이트 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_TCAR_SCH_ID", 				szYD_TCAR_SCH_ID);
	        	recInPara.setField("YD_CARLD_WRK_BOOK_ID", 			szYdWbookId);
	        	recInPara.setField("YD_EQP_WRK_STAT", 				"L");
	        	recInPara.setField("YD_CAR_PROG_STAT", 				"4");//상차개시
	        	recInPara.setField("YD_CARLD_STOP_LOC", 			szYD_DN_WR_LOC.substring(0,6));
	        	intRtnVal = ydTcarSchDao.updYdTcarsch(recInPara, 0);
	        	if(intRtnVal <= 0) {
	                szMsg = "[" + szOperationName + "] [대차 상차]대차스케줄[" + szYD_TCAR_SCH_ID + "] 상차작업예약["+szYdWbookId+"] 등록시 Error";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                m_ctx.setRollbackOnly();
	                throw new JDTOException("<procY1CrnUdWr> updYdTcarsch" + szMsg);
	        	}
	        	
	        	szMsg = "[" + szOperationName + "] [대차 상차]대차스케줄[" + szYD_TCAR_SCH_ID + "]에 상차작업예약ID["+szYdWbookId+"]를 업데이트 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	//-------------------------------------------------------------------------------------------------------------------

	        	//-------------------------------------------------------------------------------------------------------------------
    		    //	권하실적위치가 대차이므로 대차이송재료 등록
    	        //-------------------------------------------------------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 권하실적위치가 대차이므로 대차이송재료 등록 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            	
            	this.Y1SetYdTcar(getRecSet,szYD_TCAR_SCH_ID) ; 	
            	//-------------------------------------------------------------------------------------------------------------------
            	
            	//-------------------------------------------------------------------------------------------------------------------
    		    //	권하실적위치가 대차이므로 대차스케줄 호출
    	        //-------------------------------------------------------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 권하실적위치가 대차이므로 대차스케줄 호출 시작 - 대차설비ID["+szTcarEqpId+"]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            	
                recSendMsg = JDTORecordFactory.getInstance().create();
            	recSendMsg.setField("MSG_ID", 			"YDYDJ520");
            	recSendMsg.setField("YD_LD_UD_GP", 		"L");
            	recSendMsg.setField("YD_WBOOK_ID", 		szYdWbookId);
            	recSendMsg.setField("YD_EQP_ID", 		szTcarEqpId);
            	
    			// 권하처리 요청 
    			ydEjbCon.trx("TransEqpSchSeEJB", "procY1TcarSch", recSendMsg);

    			szMsg = "[" + szOperationName + "] 권하실적위치가 대차이므로 대차스케줄 호출 완료";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                //-------------------------------------------------------------------------------------------------------------------
            }
            //-------------------------------------------------------------------------------------------------------------------
            
	        //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 차량이므로 차량 스케줄 이송재료 등록 후 차량진행관리 호출
	        //-------------------------------------------------------------------------------------------------------------------
            if(szYD_DN_WR_LOC.substring(2, 4).equals("PT")|| 
               szYD_DN_WR_LOC.substring(2, 4).equals("TR")){
            	
            	intRtnVal = this.Y1SetYdCar(getRecSet, 1) ; 
            	
            	szMsg = "차량이송재료 등록 완료";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            	
            	// 차량 작업 진행관리 호출
            	recInTemp = JDTORecordFactory.getInstance().create();
            	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
            	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
            	recInTemp.setField("CAR_LDUD_GP",   "U");
            	recInTemp.setField("YD_DN_WR_LOC",   szYD_DN_WR_LOC);
            	recInTemp.setField("YD_UP_WR_LOC",   szYD_UP_WR_LOC);
            	
				szMsg = "차량 작업 진행관리 호출(상차)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.displayRecord(szOperationName, recInTemp);

            	//차량 작업진행관리 호출(상차) : 이력테이블 등록을 위해 RETURN값을 차량스케줄ID로 변경
            	szYD_CAR_SCH_ID = this.procY1CarWrkStatCtr(recInTemp);
            	
            }
            //-------------------------------------------------------------------------------------------------------------------
            
            //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 차량이아니고  권상위치가 차량이면 차량진행관리 호출
	        //-------------------------------------------------------------------------------------------------------------------
            if(!szYD_DN_WR_LOC.substring(2, 4).equals("PT")&& 
               !szYD_DN_WR_LOC.substring(2, 4).equals("TR")){
            	
            	//권상실적위치가 차량인 경우
            	if(szYD_UP_WR_LOC.substring(2, 4).equals("PT")|| 
            	   szYD_UP_WR_LOC.substring(2, 4).equals("TR")){
                	
            		recInTemp = JDTORecordFactory.getInstance().create();
                	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
                	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
                	recInTemp.setField("CAR_LDUD_GP",   "L");
                	recInTemp.setField("YD_UP_WR_LOC",   szYD_UP_WR_LOC);
                
					szMsg = "차량 작업 진행관리 호출(하차)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.displayRecord(szOperationName, recInTemp);                	
                	
                	//차량 작업 진행관리 호출(하차) 20090917 김진욱 : 이력테이블 등록을 위해 RETURN값을 차량스케줄ID로 변경
                	szYD_CAR_SCH_ID = this.procY1CarWrkStatCtr(recInTemp);
                }
            }
            //-------------------------------------------------------------------------------------------------------------------
            
            if(getRecord.getFieldString("YD_WBOOK_ID") == null ||
     	       getRecord.getFieldString("YD_WBOOK_ID").equals("")) {
                szMsg = "YD_WBOOK_ID  Data Error	: 작업예약 ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY1CrnUdWr> " + szMsg);
	        }
	        
	        //-------------------------------------------------------------------------------------------------------------------
		    //	2010.04.14 윤재광 인터페에스 송신처리
	        //  각각의 I/F 송신조건은 다시 체크 요망.
	        //-------------------------------------------------------------------------------------------------------------------
            {				
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			생산통제 C열연장입진행실적 전송  - YDCTJ033
		         * 업무기준 Desc : 1. 장입동적치(A동) - 대차동간이적, **************** 다른 설비에서 나오는 경우는 ? *****************
		         * 				  2. 장입동 일반야드 적치
		         * 				  3. 대상재의 야드목표행선 : C2[작업대기(C열연압연)]
		         * 스케줄코드 :  1. 대차하차 스케줄(AATC__LM)
		         * 장입보급진행상태  : 10 - 장입동적치
		         * 기능 추가 : 임춘수
		         * 일자 : 2009.06.18
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        if( ( szYD_SCH_CD.matches("AATC0[1-3]LM")&& 				/* 대차하차 스케줄(AATC01LM, AATC02LM, AATC03LM) */
		        	  szYD_DN_WR_LOC.matches("AA\\d\\d\\d\\d\\d\\d"))){		/* 장입동적치(A동) 일반야드[설비구분 - 숫자두자리] */
		        
			        recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDCTJ033");
					recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
					recInTemp.setField("CHG_SUP_PROG_STAT", "10");
					ydDelegate.sendMsg(recInTemp);
					szMsg = "[권하실적처리]생산통제 C열연장입진행실적[YDCTJ033] 전송 완료 - 장입동적치(A동)";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			생산통제 C열연장입진행실적 전송  - YDCTJ033
		         * 업무기준 Desc : 1. C열연가열로 장입보급Carry-In, 직장입인 경우도 처리 필요.
		         * 				  2. 보급베드 - AAPUP4, ACPUP2
		         * 				  3. 대상재의 야드목표행선 : C2[작업대기(C열연압연)]
		         * 스케줄코드 :  1. C열연가열로 장입보급Carry-In 스케줄
		         * 장입보급진행상태  : 30 - W/B,장입구,디파일러(적치완료)
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        else if( ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP6_LEFT) && 
		        		   szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU6) )		/* 보급CARRY-IN스케줄, B동 보급PICKUP베드(ABPUP6) */
		        		 ||	
		        		 ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP6_RIGHT) && 
		        		   szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU6) )		/* 보급CARRY-IN스케줄, B동 보급PICKUP베드(ABPUP6) */
		        	     ||  
		        		 ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP4_LEFT) && 
		        		   szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU4) )		/* 보급CARRY-IN스케줄, A동 보급PICKUP베드(AAPUP4) */
		        		 ||	
		        		 ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP4_RIGHT) && 
		        		   szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU4) )		/* 보급CARRY-IN스케줄, A동 보급PICKUP베드(AAPUP4) */
		        	     || 
		        	     ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP2_LEFT) && 
		        	       szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU2)	)		/* 보급CARRY-IN스케줄, C동 보급PICKUP베드(ACPUP2) */
		        	     || 
		        	     ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_REFUR_SUP2_RIGHT) && 
		        	       szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU2)	)		/* 보급CARRY-IN스케줄, C동 보급PICKUP베드(ACPUP2) */
		        	     || 
		        	     ( szYD_SCH_CD.startsWith("MADP") )	// 보급CARRY-IN스케줄  : 항만슬라브야드 기능추가 - 2015.12.30 LeeJY
		        ) {
			        recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDCTJ033");
					recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
					recInTemp.setField("CHG_SUP_PROG_STAT", "30");
					ydDelegate.sendMsg(recInTemp);
					szMsg = "[권하실적처리]생산통제 C열연장입진행실적[YDCTJ033] 전송 완료 - C열연가열로 장입보급Carry-In";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	                
		        }else if( ( szYD_SCH_CD.startsWith("ABYD") && 
		        		    szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU6) )		/* B동 이적 */
			        	   ||	
			        	  ( szYD_SCH_CD.startsWith("AAYD") && 
				        	szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU4) )		/* A동 이적 */
					       ||	 
			        	  ( szYD_SCH_CD.startsWith("ACYD") && 
			        	    szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU2)	)		/* C동 이적 */
			        ) {
				        recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID",        "YDCTJ033");
						recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
						recInTemp.setField("CHG_SUP_PROG_STAT", "30");
						ydDelegate.sendMsg(recInTemp);
						szMsg = "[권하실적처리]생산통제 C열연장입진행실적[YDCTJ033] 전송 완료 - C열연가열로 장입보급Carry-In";
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		                
			   }else if(( szYD_SCH_CD.matches("ABTC0[1-3]LM") && 
		        		  szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU6)	)		/* 대차하차스케줄, B동 보급PICKUP베드(ABPUP6) - 직장입인 경우  */
			        	 ||
 		        	    ( szYD_SCH_CD.matches("AATC0[1-3]LM") && 
				          szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU4)	)		/* 대차하차스케줄, A동 보급PICKUP베드(AAPUP4) - 직장입인 경우  */
					     ||  
			        	( szYD_SCH_CD.matches("ACTC0[1-3]LM") && 
			        	  szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU2)	)		/* 대차하차스케줄, C동 보급PICKUP베드(ACPUP2) - 직장입인 경우 */
			     ) {
		        	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDCTJ033");
					recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
					recInTemp.setField("CHG_SUP_PROG_STAT", "30");
					ydDelegate.sendMsg(recInTemp);
					szMsg = "[권하실적처리]생산통제 C열연장입진행실적[YDCTJ033] 전송 완료 - 직장입";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			연주정정Level2 OHC Take-Out 완료 전송  - YDC3L002
		         * 업무기준 Desc : 1. 연주설비의 RT에서 TAKE-OUT 시
		         * 				  2. Roller Table #1 Machine - ADRT01, #2 Machine - ADRT02, #3 Machine - ADRT03
		         * 스케줄코드 :  1. OHC Take-Out 스케줄
		         * 				#1 Machine - ADRT01LM, #2 Machine - ADRT02LM, #3 Machine - ADRT03LM
		         * 기능 추가 : 임춘수
		         * 일자 : 2009.06.16
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        
		        if( ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT1) && 
		        	  szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_01) )			/*#1 Machine OHC TAKE-OUT스케줄 */
			        || 
			        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT2) && 
			          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_02)	)			/*#2 Machine OHC TAKE-OUT스케줄 */
			        || 
			        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT3) && 
			          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_03)	)			/*#3 Machine OHC TAKE-OUT스케줄 */
		            || 
			        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT4) && 
			          szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_04)	)			
				    || 
				    ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT4) && 
				      szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_04)	)			
				    || 
				    ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT4) && 
				      szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_04)	)			
					) {
		        	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDC3L002");
					szMsg = "[권하실적처리]연주정정Level2 OHC Take-Out 완료[YDC3L002] 전송 완료";
					recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
					ydDelegate.sendMsg(recInTemp);
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			C연주2정정Level2 OHC Take-Out 완료 전송  - YDC7L002
		         * 업무기준 Desc : 1. 연주설비의 RT에서 TAKE-OUT 시
		         * 				  2. Roller Table #4 Machine - ADRT04, #5 Machine - ADRT05
		         * 스케줄코드 :  1. OHC Take-Out 스케줄
		         * 				#4 Machine - ADRT04LM, #5 Machine - ADRT05LM
		         * 일자 : 2012.08.08
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        if( ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT7) && 
		        	  szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_07) )		//#4 M/C OHC TAKE-OUT스케줄
			        || 
					( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_OUT8) && 
					  szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_RT_08)	)	    //#5 M/C OHC TAKE-OUT스케줄
		           ) {
		        	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDC7L002");
					recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
					ydDelegate.sendMsg(recInTemp);
					szMsg = "[권상실적처리]연주2정정Level2 OHC Take-Out 완료[YDC7L002] 전송 완료" ;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			연주정정Level2 Carry-In완료 전송  - YDC3L004
		         * 업무기준 Desc : 1. Carry-In(Pickup Bed, Depiler Bed) 완료 시
		         * 				  2. Pickup Bed - ACPUP2, AAPUP4, Depiler Bed - ACDP01, AADP02
		         * 스케줄코드 :  1. Carry-In(Pickup Bed, Depiler Bed) 스케줄
		         * 				AADP02UM, ACDP01UM, AAPU04UM, ACPU02UM
		         * 기능 추가 : 임춘수
		         * 일자 : 2009.06.16
		         * 기능 수정 : 김진욱
		         * 수정일자 : 20090925
		         * 수정내용 : 스케줄코드와는 상관없이 to위치가 보급베드이면 완료전송을 한다.스케줄코드와 비교를한다면 직보급인경우는 처리불가!!
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        if(  szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU2) 	|| /*보급CARRY-IN스케줄, A동 보급PICKUP베드(AAPUP2) */
		        	 szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU4) 	|| /*보급CARRY-IN스케줄, C동 보급PICKUP베드(ACPUP4) */	
		        	 szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU6) 	|| /*보급CARRY-IN스케줄, B동 보급PICKUP베드(ABPUP6) */
		        	 szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU5) 	|| /*보급CARRY-IN스케줄, B동 보급PICKUP베드(ABPUP5) */
			         szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_DP_01)	|| /*M-SCARFING CARRY-IN스케줄, C동 보급DEPILER베드(ACDP01) */
			         szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_DP_02)  || /*연주정정[2차절단] CARRY-IN스케줄, A동 보급DEPILER베드(AADP02) */
			         szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_SB1)    || /*CARRY-IN스케줄 핸드스카핑(ABSB01) */
			         szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PU9)    || //C연주 A동 #2 2차절단 Pickup09
		        	 szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PUA)    || //C연주 A동 #3 2차절단 Pickup10
		        	 szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_PUB)    || //C연주 A동 #2 Scarfer Pickup11
		        	 szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_DP_03)  || //C연주 B동 #2 Scarfer Depiler03
		        	 szYD_DN_WR_LOC.startsWith(YdConstant.EQP_P_DP1)       //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
			         ){ 
			          
				        recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID",        "YDC3L004");
						szMsg = "[권하실적처리]연주정정Level2 Carry-In완료[YDC3L004] 전송 완료";
						//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						if (szYD_DN_WR_LOC.startsWith(YdConstant.EQP_P_DP1)) {
							recInTemp.setField("MSG_ID",        "YDE9L004");
							szMsg = "[권하실적처리]항만정정Level2 Carry-In완료[YDE9L004] 전송 완료";
						}
						recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
						ydDelegate.sendMsg(recInTemp);
						//szMsg = "[권하실적처리]연주정정Level2 Carry-In완료[YDC3L004] 전송 완료";
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


				        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				         * 			연주정정Level2 Carry-In재료정보 전송  - YDC3L005
				         * 업무기준 Desc : 1. Carry-In(M스카핑 Depiler Bed) 완료 시
				         * 				  2. Depiler Bed - ACDP01
				         * 스케줄코드 :  1. M스카핑 Carry-In(Depiler Bed) 스케줄
				         * 				ACDP01UM
				         * 기능 추가 : 임춘수
				         * 일자 : 2009.06.16
				         * 기능수정 : 김진욱
				         * 수정일자 20090925
				         * 수정내용 : Carry-In재료정보를 송신하는 조건 변경 To위치가 보급베드라면 Carry_In재료정보를 송신한다. 기존의 조건에서는 직보급인 경우에는 
				         *          전송이 되지 않기때문에 수정!
				         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				        recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID",        "YDC3L005");
						//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						szMsg = "[권하실적처리]연주정정Level2 Carry-In재료정보[YDC3L005] 전송 완료";
						if (szYD_DN_WR_LOC.startsWith(YdConstant.EQP_P_PU1)) {
							recInTemp.setField("MSG_ID",        "YDE9L005");
							szMsg = "[권하실적처리]항만정정Level2 Carry-In재료정보[YDE9L005] 전송 완료";
						}
						recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
						ydDelegate.sendMsg(recInTemp);
						//szMsg = "[권하실적처리]연주정정Level2 Carry-In재료정보[YDC3L005] 전송 완료";
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			연주정정Level2 OHC Take-In 완료 전송  - YDC3L008
		         * 업무기준 Desc : 1. 연주설비의 RT로 TAKE-IN 시
		         * 				  2. Roller Table #1 Machine - ADRT01, #2 Machine - ADRT02, #3 Machine - ADRT03
		         * 스케줄코드 :  1. OHC Take-in 스케줄
		         * 				#1 Machine - ADRT01UM, #2 Machine - ADRT02UM, #3 Machine - ADRT03UM
		         * 기능 추가 : 임춘수
		         * 일자 : 2009.06.16
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        if( ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_IN1) && 
		        	  szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_RT_01) )			/*#1 Machine OHC TAKE-IN스케줄 */
			        || 
			        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_IN2) && 
			          szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_RT_02)	)			/*#2 Machine OHC TAKE-IN스케줄 */
			        || 
			        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_IN3) && 
			          szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_RT_03)	)			/*#3 Machine OHC TAKE-IN스케줄 */
			        || 
				    ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_IN4) && 
				      szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_RT_04)	)			  
				    || 
				    ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_IN5) && 
				      szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_RT_05)	)
				    || 
				    ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_IN6) && 
				      szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_RT_06)	)  
			        ) {
		        	for(int Loop_i = 1; Loop_i <= getRecSet.size(); Loop_i++) {
		        		getRecSet.absolute(Loop_i);
		        		recInTemp = getRecSet.getRecord();
		        		szSTL_NO = ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
			        	recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID",        "YDC3L008");
						recInTemp.setField("YD_EQP_ID",        szYD_DN_WR_LOC.substring(0, 6));
						recInTemp.setField("YD_STK_BED_NO",    szYD_DN_WR_LOC.substring(6, 8));
						recInTemp.setField("STL_NO",           szSTL_NO);
						ydDelegate.sendMsg(recInTemp);
		        	}
		        	szMsg = "[권하실적처리]연주정정Level2 EMG_Bed재료정보[YDC3L008] 전송 완료";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			연주2정정Level2 OHC Take-In 완료 전송  - YDC7L008
		         * 업무기준 Desc : 1. 연주설비의 RT로 TAKE-IN 시
		         * 				  2. Roller Table #4 Machine - ADRT04, #5 Machine - ADRT05
		         * 스케줄코드 :  1. OHC Take-in 스케줄
		         * 				#4 Machine - ADRT04UM, #5 Machine - ADRT05UM
		         * 일자 : 2012.08.08
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        if( ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_IN7) && 
		        	  szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_RT_07) )			/*#4 Machine OHC TAKE-IN스케줄 */
			        || 
			        ( szYD_SCH_CD.equals(YdConstant.SCH_CD_A_OHC_TAKE_IN8) && 
			          szYD_DN_WR_LOC.startsWith(YdConstant.EQP_A_RT_08)	)			/*#5 Machine OHC TAKE-IN스케줄 */
			        ) {
		        	for(int Loop_i = 1; Loop_i <= getRecSet.size(); Loop_i++) {
		        		getRecSet.absolute(Loop_i);
		        		recInTemp = getRecSet.getRecord();
		        		szSTL_NO = ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
			        	recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID",        "YDC7L008");
						recInTemp.setField("YD_EQP_ID",        szYD_DN_WR_LOC.substring(0, 6));
						recInTemp.setField("YD_STK_BED_NO",    szYD_DN_WR_LOC.substring(6, 8));
						recInTemp.setField("STL_NO",           szSTL_NO);
						ydDelegate.sendMsg(recInTemp);
		        	}
		        	szMsg = "[권하실적처리]연주정정Level2 EMG_Bed재료정보[YDC7L008] 전송 완료";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            }
            //-------------------------------------------------------------------------------------------------------------------
            
            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업 및 재료, 작업예약 및 재료정보를 Clear
	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인 작업재료 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("MODIFIER",           "YDSYSTEM");
	        intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl_YD_CRN_SCH_ID(setRecord);
	        if(intRtnVal <= 0) {
                szMsg = "크레인스케줄작업재료 삭제중 Error!! Code : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
	        	throw new JDTOException("<procY1CrnUdWr> updYdCrnwrkmtl_YD_CRN_SCH_ID " + szMsg);
	        }
	        
	        //크레인스케줄 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("YD_DN_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss")); //권하완료일시
	        intRtnVal = this.Y1UpdYdCrnsch(setRecord, 0);
	        if(intRtnVal < 0){
                szMsg = "[" + szOperationName + "] 크레인스케줄 삭제처리 에러발생!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY1CrnUdWr> Y1UpdYdCrnsch " + szMsg);
	        }
	        
	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        getRecord = JDTORecordFactory.getInstance().create();
	        getRecord.setField("YD_WBOOK_ID", szYdWbookId);

	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
			intRtnVal = this.Y1GetYdCrnsch(getRecord, getRecSet, 28);
			if(intRtnVal < 0){
                szMsg = "[" + szOperationName + "] 작업예약완료 CHECK 에러발생!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY1CrnUdWr> Y1UpdYdCrnsch " + szMsg);
	        }
	        /*
	         * 작업예약에 존재하는 크레인스케쥴 정보가 없을경우에 작업예약정보를 삭제한다.
	         */	
	        if (getRecSet.size() == 0) {
	        	 
	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId) ;
	            bookrecord.setField("DEL_YN",             "Y") ;
	            bookrecord.setField("YD_SCH_PROG_STAT",   "E") ;
	            
	            intRtnVal = this.Y1UpdYdWrkbook(bookrecord, 0);
		        
		        //작업예약재료조회
	            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	            intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(bookrecord, getRecSet, 1);
								
	            //조회한 작업예약재료1매씩 저장품 업데이트
				for( int Loop_i = 1; Loop_i <= getRecSet.size(); Loop_i++ ) {
					getRecSet.absolute(Loop_i);
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setRecord(getRecSet.getRecord());
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
					recInTemp.setField("YD_SCH_CD", "");
					recInTemp.setField("YD_WBOOK_ID", "");
					recInTemp.setField("YD_STK_COL_GP", szYD_DN_WR_LOC.substring(0,6));
					recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, Loop_i-1));
					intRtnVal = ydStockDao.updYdStock(recInTemp, 0);
				}
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("STL_NO",      ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
				recInTemp.setField("DEL_YN",      "Y");
				recInTemp.setField("MODIFIER",    "SYSTEM");
				recInTemp.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID"));
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl1(recInTemp);
		        
	            szMsg = "작업 예약 처리 완료";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	        }else{
	            szMsg = "작업 예약 진행 중";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	        }
	        
        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			연주슬라브야드L2 크레인작업실적응답 전송  - YDY1L005
	         * 업무기준 Desc : 크레인 권하실적처리 성공 후 크레인작업실적응답 전송
	         * 기능 추가 : 임춘수
	         * 일자 : 2009.06.19
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        recInTemp = JDTORecordFactory.getInstance().create(); 
	        recInTemp.setField("MSG_ID"        	 , "YDY1L005");
	        recInTemp.setField("YD_EQP_ID"     	 , szYD_EQP_ID);									//야드설비ID
	        recInTemp.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_DN_CMPL);					//야드작업진행상태
	        recInTemp.setField("YD_SCH_CD"       , szYD_SCH_CD);									//야드스케줄코드
	        recInTemp.setField("YD_CRN_SCH_ID"   , szCrnSchId);										//야드크레인스케줄ID

	        //==================================================================================
	        // 수신전문의 진행상태가 강제권하(5)일시 응답으로는 강제권하값(F)로 내려보내야 됨
	        //==================================================================================	        
	        if(szYD_WRK_PROG_STAT.equals("4")){
	        	recInTemp.setField("YD_L2_WR_GP"     , YdConstant.CRN_WRK_RE_DN_WR);				//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        } else {
	        	recInTemp.setField("YD_L2_WR_GP"     , YdConstant.CRN_WRK_RE_FRCE_DN);				//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하	        	
	        }
	        recInTemp.setField("YD_L3_HD_RS_CD"  , YdConstant.CRN_WRK_RE_CD_NORMAL_HD);				//야드L3처리결과코드
			ydDelegate.sendMsg(recInTemp);
			szMsg = "[" + szOperationName + "] C연주슬라브야드L2 크레인작업실적응답[YDY1L005] 전송 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            
            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인 설비상태 권하완료(4) 셋팅.
	        //-------------------------------------------------------------------------------------------------------------------
            recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	        recInTemp.setField("YD_EQP_STAT", "4");
            intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
            
            if(intRtnVal <= 0) {
				 szMsg="설비상태 UPDATE 처리시 오류 발생.";
			 	 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			 	 m_ctx.setRollbackOnly();
	   			 throw new JDTOException(szMsg);
	   		}
            
	        //설비id로 설비Table조회
	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
						
			/*
			 * 이력테이블등록호출 
			 */
			{
				CrnSchSeEJBBean crnSchSeEJBBean = new CrnSchSeEJBBean();
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",             "");
				recInTemp.setField("YD_WBOOK_ID",        szYdWbookId);
				recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId);
				recInTemp.setField("YD_CAR_SCH_ID",      szYD_CAR_SCH_ID);
				recInTemp.setField("YD_TCAR_SCH_ID",     szYD_TCAR_SCH_ID);
				recInTemp.setField("YD_WTCL_TNK_SCH_ID", "");
				crnSchSeEJBBean.procWorkHistoryCreate(recInTemp);
				
				szMsg="[" + szOperationName + "] 이력테이블등록호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			/*
			 * 크레인 작업지시 요구호출.
			 */
			{
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord(0));
		        
				recInTemp = JDTORecordFactory.getInstance().create();
//SJH03004				
				recInTemp.setField("MSG_ID",           "YDYDJ640");
				
				recInTemp.setField("YD_EQP_ID",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID"));
				recInTemp.setField("YD_EQP_WRK_MODE",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE"));
				recInTemp.setField("YD_WRK_PROG_STAT", "4");
				recInTemp.setField("YD_SCH_CD",        szYD_SCH_CD);
				recInTemp.setField("YD_CRN_SCH_ID",    "");
				recInTemp.setField("YD_CRN_XAXIS",     "");
				recInTemp.setField("YD_CRN_YAXIS",     "");
				recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_SUCCESS);
				
//				String szEjbJndiName 	= "CraneLdHdSeEJB";
//				String szEjbMethod 		= "procY1CrnWrkOrdReq";
//			
//				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//				 * ++++++ 데이타의 정합성 때문에 추가 - 아래 항목을 수정 시에는 수정자에게 문의 +++++++
//				 * 크레인스케줄이 삭제(DEL_YN)필드가 Y로 설정된 후 COMMIT전에 
//				 * 크레인작업지시모듈에서 조회 시 현재 Y로 설정된 동일한 크레인스케줄이 조회가 되어
//				 * 전문편집 모듈에 넘겨주고 다시 조회 시 Y로 COMMIT되는 경우가 생겨서 L2로 크레인작업지시가
//				 * 내려가지 않는 현상이 발생하여 EJB CALL로 변경. 
//				 * 수정자 : 임춘수
//				 * 수정일 : 2009.09.03
//				 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				szIS_EJB_CALL = "Y";
//				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				
//				if( szIS_EJB_CALL.equals("Y")) {
//				
//					szLogMsg = "[" + szOperationName + "] 크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					
//					EJBConnector ejbConn = new EJBConnector("default", szEjbJndiName, this);			
//					String szRtnMsg = (String)ejbConn.trx(szEjbMethod, new  Class[] { JDTORecord.class }, 
//																	   new Object[] { recInTemp });
//					if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
//						szLogMsg = "[" + szOperationName + "] 크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					}else{
//						szLogMsg = "[" + szOperationName + "] 크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
//					}
//					szLogMsg = "[" + szOperationName + "] 크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - JMS 1전문전송 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//
//				}else{
//					//크레인작업지시 송신
//					ydDelegate.sendMsg(recInTemp);
//					szLogMsg = "[" + szOperationName + "] 크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - JMS 전문전송 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				}
			}
			
			//------------------------------------------------------------------
	        // 권하 실적시 Flex 실시간 처리
	        //------------------------------------------------------------------
	        JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
		 	recFlex.setField("YD_GP",  YdConstant.YD_GP_C_SLAB_YARD);
		 	//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
		 	if (szYD_EQP_ID.substring(0,1).equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {
			 	recFlex.setField("YD_GP",  YdConstant.YD_GP_PORT_SLAB_YARD);
		 	}
		 	recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
		 	recFlex.setField("YD_UP_WR_LOC", szYD_UP_WR_LOC);
		 	recFlex.setField("YD_DN_WR_LOC", szYD_DN_WR_LOC);
			szMsg="Flex 권하 완료 실적 전송";
		 	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		ydUtils.putYdFlexCrnWrk("", recFlex);
    		
        }catch(Exception e) {
        	/*
        	 * Exception 발생시에도 작업실적 응답은 송신.
        	 */
        	{
	        	recInTemp = JDTORecordFactory.getInstance().create(); 
		        recInTemp.setField("MSG_ID"        , "YDY1L005");
				szMsg = "[" + szOperationName + "] C연주 슬라브야드L2 크레인작업실적응답[YDY1L005] 전송 완료";
			 	//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
			 	if (szYD_EQP_ID.substring(0,1).equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {
			        recInTemp.setField("MSG_ID"        , "YDE7L005");
					szMsg = "[" + szOperationName + "] 항만 슬라브야드L2 크레인작업실적응답[YDE9L005] 전송 완료";
			 	}
		        recInTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);							//야드설비ID
		        recInTemp.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_DN_CMPL);	//야드작업진행상태
		        recInTemp.setField("YD_SCH_CD"   , szYD_SCH_CD);							//야드스케줄코드
		        recInTemp.setField("YD_CRN_SCH_ID"   , szCrnSchId);							//야드크레인스케줄ID
		        if(szYD_WRK_PROG_STAT.equals("4")){
		        	recInTemp.setField("YD_L2_WR_GP"     , YdConstant.CRN_WRK_RE_DN_WR);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
		        } else {
		        	recInTemp.setField("YD_L2_WR_GP"     , YdConstant.CRN_WRK_RE_FRCE_DN);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하	        	
		        }
		        
		        recInTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);	//야드L3처리결과코드
				ydDelegate.sendMsg(recInTemp);
	
				//szMsg = "[" + szOperationName + "] C연주 슬라브야드L2 크레인작업실적응답[YDY1L005] 전송 완료";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	}	        
        	m_ctx.setRollbackOnly();
        	throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }finally{
        }
        return recInTemp;
    }// end of procY1CrnUdWrTX()
    
    
	/**
	 * 오퍼레이션명 : 차량 작업 진행관리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procY1CarWrkStatCtr(JDTORecord msgRecord)throws JDTOException  {
		//TC_CODE :YDYDJ630
		
		YdDelegate ydDelegate = new YdDelegate();
		YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
		YdCarSchDao     ydCarSchDao     = new YdCarSchDao();  

		JDTORecordSet rsTemp          	= null;
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recFirst          = null;
		JDTORecord    recLast           = null;
		
	    int intRtnVal 		   = 0 ;
	    
	    String szMsg           = "";
	    String szMethodName    = "procY1CarWrkStatCtr";
	    
	    String szCAR_LDUD_GP   = "";
	    String szYD_WBOOK_ID   = "";
	    String szYD_CRN_SCH_ID = "";
	    String szFST_CRN_SCH_ID = "";
	    String szLST_CRN_SCH_ID = "";
	    String szYD_SCH_CD      = "";
	    String szYD_GP          = "";
	    String szYD_CAR_SCH_ID  = "";
	    String szYD_DN_WR_LOC   = "";
	    String szYD_UP_WR_LOC   = "";
	    String szYD_CAR_USE_GP  = "";
	    
	    try{
	    	//상하차구분 플래그, 작업예약id, 크레인스케줄id
	    	szCAR_LDUD_GP   = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_LDUD_GP"); 
	    	szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	    	szYD_DN_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC");
	    	szYD_UP_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
	    	
	    	//작업예약id로 크레인 스케줄을 조회
	    	recInTemp 	= JDTORecordFactory.getInstance().create();
	    	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	    	
	    	if(szCAR_LDUD_GP.equals("L")){
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 31);
		    	if(intRtnVal <= 0) {
					szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error 상차인경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            		throw new JDTOException("<procY1CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}else{
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 32);
		    	if(intRtnVal <= 0) {
					szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error 하차인경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            		throw new JDTOException("<procY1CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}
	    	
			szMsg = "rsResult 사이즈 : " + rsResult.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	rsResult.first();
	    	recFirst = JDTORecordFactory.getInstance().create();
	    	recFirst.setRecord(rsResult.getRecord());
	    	szFST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recFirst, "YD_CRN_SCH_ID");
	    	
			szMsg = "첫번째 스케줄id : " + szFST_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	rsResult.last();
	    	recLast = JDTORecordFactory.getInstance().create();
	    	recLast.setRecord(rsResult.getRecord());
	    	szLST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recLast, "YD_CRN_SCH_ID");
	    	
			szMsg = "마지막 스케줄id : " + szLST_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szMsg = "전문 스케줄id : " + szYD_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	szYD_SCH_CD      = ydDaoUtils.paraRecChkNull(recFirst, "YD_SCH_CD");
	    	szYD_GP          = szYD_SCH_CD.substring(0,1);
	    	
	    	//플래그가 상차인경우
	    	if(szCAR_LDUD_GP.equals("U")) {
	    		//차량스케줄 id를 조회
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    		ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    		szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP"); 	
	    		
	    		//출하차량인 경우에만 적용한다.
	    		if(szYD_CAR_USE_GP.equals("G")){
	    			
					szMsg="일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID",        "YDDMR013");
					
					recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("YD_GP",         szYD_GP);
		
					ydDelegate.sendMsg(recInTemp);
	    		}
	    		
	    		//-----------------------------------------------------------------------------------
	    		//	1.	설비에서 인출 시는 차량매수[6]만큼 상차된 경우에 상차완료처리
	    		//		그 외의 경우는 마지막 크레인스케줄인 경우 처리
	    		//	수정일
	    		//		1. 2010.01.28 - 임춘수
	    		//-----------------------------------------------------------------------------------
	    		
	    		szMsg="권상실적위치["+szYD_UP_WR_LOC+"]로 설비인출[직상차]인 지를 판단 시작 - 상차완료 처리 판단";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		if( szYD_UP_WR_LOC.startsWith(YdConstant.EQP_A_PU1)) {
	    			
	    			//-------------------------------------------------------------------------------------
	    			//	ADPUP1에서 인출인 경우에는 차량매수[6]만큼 상차된 경우 상차완료처리
	    			//-------------------------------------------------------------------------------------
	    			szMsg="설비[권상실적위치:"+szYD_UP_WR_LOC+"]에서 인출 시[직상차]인 경우에는 차량매수[6]만큼 상차되었을 때만 상차완료 처리";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			//-------------------------------------------------------------
	    			//	등록된 차량이송재료 매수를 조회 
	    			//-------------------------------------------------------------
					
					szMsg="차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료를 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
	    			rsTemp			= JDTORecordFactory.getInstance().createRecordSet("");
	    			recInTemp 		= JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
	    			
	    			String szRtnMsg	= DaoManager.getYdCarftmvmtl(recInTemp, rsTemp, 1);
	    			
	    			if( !szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) && 
	    				!szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    				
	    				szMsg="차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료를 조회 시 오류발생 - 메세지 : " + szRtnMsg;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				throw new JDTOException(szMsg);
	    			}
	    			
	    			szMsg="차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료를 조회 완료 - 메세지 : " + szRtnMsg + ", 대상재건수["+rsTemp.size()+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
	    			//-------------------------------------------------------------
					
					
					//-------------------------------------------------------------
	    			//	차량매수[4]만큼 상차된 경우 상차완료처리
	    			//-------------------------------------------------------------
					if( rsTemp.size() >= 4 ) {
						
						//-------------------------------------------------------------
		    			//	상차완료처리
		    			//-------------------------------------------------------------
						//동일하면 차량스케줄에 상차완료일시 등록, 설비작업상태 = '영차'
		    			recInTemp = JDTORecordFactory.getInstance().create();
		    			recInTemp.setField("YD_CAR_PROG_STAT", "5");
		    			recInTemp.setField("YD_EQP_WRK_STAT", "L");
		    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
		    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 1);
		    			if(intRtnVal <= 0) {
							szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}
		    			
		    			szMsg="상차작업예약ID["+szYD_WBOOK_ID+"]와 관련된 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차 완료 등록 처리 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			recInTemp = JDTORecordFactory.getInstance().create();
		    			
		    			//--------------- 상차완료시 공통테이블 업데이트 처리 시작 2009.06.08 임춘수 추가 ----------------------
	    				intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
	    				if( intRtnVal <= 0 ) {
	    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
	    					szMsg="상차완료시 공통테이블 업데이트 처리 실패";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				}else{
	    					szMsg="상차완료시 공통테이블 업데이트 처리 성공";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    				}
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------
	    				
					}else{
						//-------------------------------------------------------------
		    			//	상차완료처리를 하지 않음
		    			//-------------------------------------------------------------
						szMsg="ADPUP1에서 인출인 경우에는 차량매수[4]이상 상차되지 않았으므로 상차완료처리를 하지 않음 - 차량이송재료["+rsTemp.size()+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						//-------------------------------------------------------------
					}
					
	    		}else{
	    		
		    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
		    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
		    			//동일하면 차량스케줄에 상차완료일시 등록, 설비작업상태 = '영차'
		    			recInTemp = JDTORecordFactory.getInstance().create();
		    			recInTemp.setField("YD_CAR_PROG_STAT", "5");
		    			recInTemp.setField("YD_EQP_WRK_STAT", "L");
		    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
		    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 1);
		    			if(intRtnVal <= 0) {
							szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}
		    			
		    			szMsg="상차작업예약ID["+szYD_WBOOK_ID+"]와 관련된 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차 완료 등록 처리 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			recInTemp = JDTORecordFactory.getInstance().create();

		    			if(szYD_CAR_USE_GP.equals("L")){							//구내운송 - 임춘수 수정 2009.06.15	
		    				//--------------- 상차완료시 공통테이블 업데이트 처리 시작 2009.06.08 임춘수 추가 ----------------------
		    				intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
		    				if( intRtnVal <= 0 ) {
		    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
		    					szMsg="상차완료시 공통테이블 업데이트 처리 실패";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    				}else{
		    					szMsg="상차완료시 공통테이블 업데이트 처리 성공";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    				}
		    				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------

		    			}else{
			    			recInTemp.setField("MSG_ID",        "YDDMR017");
							szMsg="외판슬라브출하상차완료 송신 : YDDMR017";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
			    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			    			recInTemp.setField("YD_GP",         szYD_GP);
			    			
			    			ydDelegate.sendMsg(recInTemp);
			    			
							szMsg="상차작업완료 송신 완료";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}

		    		}
		    		
	    		}
	    		//-----------------------------------------------------------------------------------
	    		
	    	//플래그가 하차인 경우
	    	}else if(szCAR_LDUD_GP.equals("L"))	{
	    		//차량스케줄 id를 조회
	    		recInTemp  = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			rsResult   = JDTORecordFactory.getInstance().createRecordSet("");
	    		ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    			
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			recInTemp.setField("YD_GP",         szYD_GP);
    		
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			//동일하면 차량스케줄에 하차완료일시 등록
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_EQP_WRK_STAT", "U");
	    			recInTemp.setField("YD_CAR_PROG_STAT", "E");
	    			recInTemp.setField("DEL_YN",           "N");
	    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 3);
	    			if(intRtnVal <= 0) {
						szMsg="차량스케줄에 하차완료일시  등록시 Error!! Code : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			
	    			//---------------- 하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 추가
	    			intRtnVal = YdCommonUtils.procCarUnLoadCmpl(szYD_CAR_SCH_ID);
    				if( intRtnVal <= 0 ) {
    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
    					szMsg="하차완료시 공통테이블 업데이트 처리 실패";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    				}else{
    					szMsg="하차완료시 공통테이블 업데이트 처리 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				}
	    			//-----------------하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 끝
	    			
	    			//하차작업완료 송신 YDTSJ010 - 구내운송 전송
    				recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID",        "YDTSJ010");
	    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
	    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    			recInTemp.setField("YD_GP",         szYD_GP);
	    			ydDelegate.sendMsg(recInTemp);
	    			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    	         * 			진행관리 슬라브소재이송완료실적전송  - YDPTJ001
	    	         * 업무기준 Desc : 1. 하차완료시
	    	         * 스케줄코드 :  1. 차량하차 스케줄 : PT(팔렛트), TR(트레일러), LM(하차)
	    	         * 기능 추가 : 임춘수
	    	         * 일자 : 2009.06.16
	    	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    			recInTemp.setField("MSG_ID",        "YDPTJ001");
	    			ydDelegate.sendMsg(recInTemp);
	    	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    			
					szMsg="하차작업완료 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}
	    		
	    	}else{
				szMsg="상차 및 하차 구분 플래그 Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    		throw new JDTOException("<procY1CarWrkStatCtr> ERROR!! " + szMsg);
	    	}
	    	
		}catch(Exception e){
	
			szMsg="차량 작업 진행관리 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException("<procY1CarWrkStatCtr> ERROR!! " + szMsg);
		}
	
	
		szMsg="차량 작업 진행관리 처리"+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return szYD_CAR_SCH_ID;
	} //end of procY1CarWrkStatCtr()
    
    /**
     * 오퍼레이션명 : 권상,권하 파라미터 체크
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int Y1ParamCheck (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        int intRtnVal = 1 ;
        
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
			setRecord.setField("YD_DN_WR_LOC"           , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
			setRecord.setField("YD_DN_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;
 
			//전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
	        if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "A") ;
	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "B") ;
	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "M") ;
	        }

	        outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException("<Y1ParamCheck> " + e.getLocalizedMessage());
        }//end of try~catch
        
        return intRtnVal;
        
    }//end of ParamCheck()
    
    /**
     * 오퍼레이션명 : C연주 크레인스케줄 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1UpdYdCrnsch(JDTORecord msgRecord, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	int intRtnVal = 0 ;
    	
    	String szMethodName 	= "Y1UpdYdCrnsch";
    	String szMsg        	= "";
    	String szOperationName	= "C연주 크레인스케줄 Update";
		try{
			
			intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);		        
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return intRtnVal = -1;
	        	case -1	:
	                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return intRtnVal = -1;
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return intRtnVal = -1;
	        	case -3	:
	                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return intRtnVal = -1;
	        }
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y1UpdYdCrnsch> " + szMsg);
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y1UpdYdCrnsch
    
    /**
     * 오퍼레이션명 : C연주 크레인스케줄 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1GetYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	String szOperationName  = "C연주 크레인스케줄 Select";
    	String szMethodName 	= "Y1GetYdCrnsch";
    	String szMsg        	= "";
    	
        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
	        switch (intRtnVal) {
        	case 0	:
                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
                return intRtnVal;
        	case -2	:
                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
                return intRtnVal;
	        }
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<procY1CrnUdWr> Y1GetYdCrnsch" + szMsg);
        }//end of try~catch

        return intRtnVal ;
    	
    }//end of Y1GetYdCrnsch()
    
    /**
     * 오퍼레이션명 : 적치단 Clear
     *  
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1ClearYdStklyr (JDTORecordSet getRecSet, int intGp) throws JDTOException {
    	
    	JDTORecord getRecord = JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
    	
    	int intRtnVal 		= 1;
    	String szMsg 		= "";
    	
    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();
            
    		for(int i=0; i<rowsize ; i++){
    			//권상 지시위치 Clear
                if(intGp == 0) {
        			String szYD_UP_WR_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC") ;
                    String szYD_UP_WR_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER") ;
                    
                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
                    //적치단 설정
                    String szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                    setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                }
                
                //권하 지시위치 Clear
                if(intGp == 1) {
	    			String szYD_DN_WO_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC") ;
	                String szYD_DN_WO_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LAYER") ;
	                
	                setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
	                setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   
	                //적치단 설정
	                String szStkLyr =ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i);
	                setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                }
                
                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                setRecord.setField("STL_NO",              "");
                
                intRtnVal = this.Y1UpdYdStklyr(setRecord, 0);  //적치단의 재료정보 Clear
                
                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for
    		
    		
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException("<Y1ClearYdStklyr> " + szMsg);
	    }//end of try~catch
		
		return  intRtnVal;
    }//end of Y1ClearYdStklyr()
    
    /**
     * 오퍼레이션명 : 적치단 등록
     *  
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1RegYdStklyr(	JDTORecordSet getRecSet, 
    							String sRealLyrNo)throws JDTOException {
    	
    	JDTORecord getRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 			= null;
    	JDTORecord crnRecord 			= null;
    	
    	JDTORecordSet rsResult          = null;
    	JDTORecord recPara 				= null;
    	JDTORecord recTemp 				= null;
    	
    	String szRtnMsg					= null;
    	String szMsg 					= "";
        String szMethodName				= "Y1RegYdStklyr";
        String szOperationName          = "적치단 등록";
        
        int intRtnVal 					= 0 ;

        String szYD_EQP_ID 				= "";
        String szYD_WBOOK_ID			= null ;
        String szYD_CRN_SCH_ID			= null;
        
        String szYD_DN_WR_LOC			= null;
        String szYD_DN_WR_LAYER			= "";
        String szSTL_NO					= "";
        
        String szYD_CAR_USE_GP			= "";
        
        String szStkLyr	= "";
    	
    	try{
			szMsg = "============================= Y1RegYdStklyr() IN =============================";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
    		
    		int rowsize = getRecSet.size();
            
    		//----------------------------------------------------------------------------------------------------------
	        //	권하실적위치가 차상이고 출하차량이면 공통테이블의 야드구분을 *로 등록 처리하기 위해서 차량스케줄 조회
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.17
	        //----------------------------------------------------------------------------------------------------------
    		
    		getRecSet.first();
    		getRecord		= getRecSet.getRecord();
    		
    		recTemp			= JDTORecordFactory.getInstance().create();
	        
    		szYD_DN_WR_LOC	   			= ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
    		szYD_WBOOK_ID 				= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID");
    		
	        if( szYD_DN_WR_LOC.length() > 6  && 
	        	szYD_DN_WR_LOC.substring(2, 4).equals("PT") ) {
	        	
	        	szMsg = "권하실적위치가 차상이므로 차량스케줄 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	
	        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
	        	
	        	recTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	        	
	        	szRtnMsg = DaoManager.getYdCarsch(recTemp, rsResult, 3);
	    	    
	    	    if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    	    	rsResult.first();
	    	    	recTemp		= rsResult.getRecord();
	    	    	
	    	    	szYD_CAR_USE_GP		= ydDaoUtils.paraRecChkNull(recTemp,"YD_CAR_USE_GP") ;
	    	    	
	    	    	szMsg = "권하실적위치가 차상이고 차량스케줄이 존재하므로 차량사용구분을 확인 - 차량사용구분[" + szYD_CAR_USE_GP + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	    }
	    	    
	    	    szMsg = "권하실적위치가 차상이므로 차량스케줄 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        }
	        
	        //----------------------------------------------------------------------------------------------------------
    		boolean isLast	= false;
    		
	        recPara	= JDTORecordFactory.getInstance().create();
	        
        	for(int i=0; i<rowsize; i++) {
        		
        		getRecSet.absolute(i+1);
        		getRecord 	= JDTORecordFactory.getInstance().create();
        		getRecord.setRecord(getRecSet.getRecord());
        		
        		szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(getRecord,"YD_EQP_ID");
    	        szYD_WBOOK_ID 		= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID");
    	        szYD_CRN_SCH_ID 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_CRN_SCH_ID");
        		
        		//권하 실적위치 등록
        		szYD_DN_WR_LOC	   	= ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
        		szYD_DN_WR_LAYER	= sRealLyrNo; //ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER") ;
        		szSTL_NO	 		= ydDaoUtils.paraRecChkNull(getRecord,"STL_NO") ;
        		
        		//크레인에 UPDATE
    			crnRecord = JDTORecordFactory.getInstance().create();
    			crnRecord.setField("YD_STK_COL_GP",       szYD_EQP_ID);    
    			crnRecord.setField("YD_STK_BED_NO",       "01");   
    			crnRecord.setField("YD_STK_LYR_NO",       "00"+(i+1)) ;
                crnRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                crnRecord.setField("STL_NO",              "");
                
                intRtnVal = this.Y1UpdYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE
                
                szMsg = "============================= 적치단 업데이트 처리 =============================";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
    			
    			setRecord = JDTORecordFactory.getInstance().create();
    			setRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6).trim());   
        		setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8).trim());               
                szStkLyr = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO", 		szStkLyr) ;
                setRecord.setField("STL_NO",              	szSTL_NO);
                
                //-------------------------------------------------------------------------------------------------------------
                //	같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 경우에는 권상대기 상태로 변경을 하고
                //	그렇지 않은 경우에는 적치중 상태로 변경한다.
                //	수정자 : 임춘수
                //	수정일 : 2009.12.16
                //-------------------------------------------------------------------------------------------------------------
                szMsg = "같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
                rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
                
                recPara.setField("YD_CRN_SCH_ID",          	szYD_CRN_SCH_ID);
                recPara.setField("YD_WBOOK_ID",            	szYD_WBOOK_ID);
                recPara.setField("STL_NO",              	szSTL_NO);
                
                szRtnMsg		= DaoManager.getYdCrnwrkmtl(recPara, rsResult, 17);
                
                szMsg = "같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
                //-------------------------------------------------------------------------------------------------------------
                if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");				//적치중
                	isLast	= true;
                }else if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", 	"U");				//권상대기
                	isLast	= false;
                }else{
                	szMsg = "다음크레인스케줄의 작업재료를 조회 중 오류발생 - " + szRtnMsg;
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        			setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");				//적치중으로 반영
        			isLast	= true;
                }
                //-------------------------------------------------------------------------------------------------------------
                ydUtils.putLog(szSessionName, szMethodName, "isLast = "+ isLast, YdConstant.DEBUG);
                if(isLast) {
	                /*
	                 * 적치단에 존재하는 해당 저장품정보를 모두 CLEAR한다.
	                 */
	                recTemp			= JDTORecordFactory.getInstance().create();
	                recTemp.setField("STL_NO", szSTL_NO);               
	            	intRtnVal = (new YdStkLyrDao()).updYdStklyrWithStock(recTemp);
                }
                intRtnVal = this.Y1UpdYdStklyr(setRecord, 0); 
    	            	        
    			szMsg = "===========================================================================";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
    	        

    	        
    			szMsg = "============================= 공통테이블에 저장위치를 갱신 =============================";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    			
    	        //저장위치 갱신				공통테이블에 저장위치를 갱신한다.
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
    	        //setRecord.setField("YD_MTL_ITEM",       	szYD_MTL_ITEM);
    	        setRecord.setField("YD_DN_WR_LOC",       	szYD_DN_WR_LOC);
    	        
    	        //----------------------------------------------------------------------------------------------------------
    	        //	권하실적위치가 차상이면 차량사용구분을 전달
    	        //	수정자 : 임춘수
    	        //	수정일 : 2009.12.17
    	        //----------------------------------------------------------------------------------------------------------
    	        setRecord.setField("YD_CAR_USE_GP",       	szYD_CAR_USE_GP); 
    	        //----------------------------------------------------------------------------------------------------------

				szMsg = "<Y1RegYdStklyr> Y1setYdStrLoc()호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
    	        intRtnVal = this.Y1setYdStrLoc(setRecord) ;

    	        ydUtils.displayRecord(szOperationName, setRecord);
    			szMsg = "===============================================================================";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    			
    	        //차량 하차작업 일 경우			공통테이블에 진도코드를 갱신한다.
    	        if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")|| 
    	           ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){
    	        	
    	        	szMsg = "============================= 공통테이블에 진도코드를 갱신과 저장품의 속성 변경 시작 =============================";
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    	        	
    	        	//진도코드 갱신
    	        	intRtnVal = this.Y1SetProgCode(getRecord) ;
    	        	//에러 메시지
    	        	
    	        	szMsg = "============================== 공통테이블에 진도코드를 갱신과 저장품의 속성 변경 완료==============================";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    	        }
        	}
        	
        	
			szMsg = "============================= Y1RegYdStklyr() OUT =============================";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
       	
		}catch(Exception e){
			szMsg = "<Y1RegYdStklyr> Exception : " + e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			throw new JDTOException(szMsg);
	    }//end of try~catch
		
		intRtnVal = 1 ;
		return intRtnVal ;
    
    }//end of Y1RegYdStklyr()
    
    /**
     * 오퍼레이션명 : 대차 Setting
     *  
     * @param  ● inRecordSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public void Y1SetYdTcar (JDTORecordSet inRecordSet, String sTCarId) throws JDTOException{
    	
    	YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
    	
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	int intRtnVal 						= 0 ;
    	String szMethodName 				= "Y1SetYdTcar";
    	String szMsg 						= "";
    	
    	try{
	    	// 크레인스케줄 Data
	    	inRecordSet.first();
	    	getRecord = inRecordSet.getRecord();

	    	//setRecord 초기화
	    	setRecord     = JDTORecordFactory.getInstance().create();
	    	int szRowSize = inRecordSet.size(); 
	    	
	    	// 권하한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권하한 재료를 등록한다.  
	    	for(int i = 0; i < szRowSize; i++){
		    	
	    		setRecord.setField("YD_TCAR_SCH_ID", 	sTCarId);
	    		setRecord.setField("STL_NO",         	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		setRecord.setField("REGISTER", 			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
	    		setRecord.setField("YD_STK_BED_NO",    	ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
	    		setRecord.setField("YD_STK_LYR_NO",    	ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
	    		intRtnVal = this.Y1InsYdTcarftmvmtl(setRecord) ;
	    		if(intRtnVal == -1) {
		    		szMsg = "대차이송재료 등록 처리중 ERROR!!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        		throw new JDTOException("<procY1CarWrkStatCtr> Y1InsYdTcarftmvmtl" + szMsg);
	    		}
		    		
	    		inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	
	    	{
	    		szMsg="대차이송재료 등록 후 대차스케줄에 상차완료시간 및  대차설비상태 영차로 등록";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		//상차인 경우에는 대차스케줄의 대차상태를 영차'L'로 업데이트한다.
	    		setRecord = JDTORecordFactory.getInstance().create();
		    	//대차스케줄ID
	    		setRecord.setField("YD_TCAR_SCH_ID", sTCarId);
	    		//설비작업상태 <L = 영차>
	    		setRecord.setField("YD_EQP_WRK_STAT", "L");
	    		
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
            throw new JDTOException("<procY1CarWrkStatCtr> 대차 이송재료 등록 삭제ERROR!!" + szMsg);
		}//end of try~catch
    	
    }//end of Y1SetYdTcar()
    
    /**
     * 오퍼레이션명 : C연주 차량 Setting
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y1SetYdCar (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
    	
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szOperationName              = "C연주 차량 Setting";
    	String szMethodName 				= "Y1SetYdCar";
    	String szMsg 						= "";
    	String szYD_AIM_YD_GP               = "";
    	String szYD_AM_BAY_GP               = "";
    	
    	long lngYD_MTL_WT                  = 0;
    	int  intYD_MTL_SH                  = 0;
    	long lngYD_EQP_WRK_WT              = 0;
    	int  intYD_EQP_WRK_SH              = 0;
    	
    	int intRtnVal = 0 ;
    	
    	//차량 스케줄 ID
    	String szYD_CAR_SCH_ID = "";
    	
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
	    	
	    	// 상하차 작업예약 ID로 차량스케줄 조회
	    	intRtnVal = this.Y1GetYdCarsch(setRecord, outRecSet, 3) ;
	    	if (intRtnVal <= 0){
	    		szMsg = "차량에서 권하작업 처리시 차량스케쥴 정보 오류발생.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	            throw new  JDTOException(szMsg);
	    	}
	    	
	    	// 차량스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 차량스케줄 ID를 추출한다
	    	szYD_CAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
	    	lngYD_EQP_WRK_WT 	= ydDaoUtils.paraRecChkNullLong(getTcarRecord, "YD_EQP_WRK_WT");
	    	intYD_EQP_WRK_SH 	= ydDaoUtils.paraRecChkNullInt(getTcarRecord, "YD_EQP_WRK_SH");
	    	
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	setRecord.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    	setRecord.setField("MODIFIER", "YDSYSTEM");
	    	
	    	szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");
	    	szYD_AM_BAY_GP = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_BAY_GP");
	    	
	    	//C연주
	    	if(szYD_AIM_YD_GP.equals("A")) {
	    		setRecord.setField("ARR_WLOC_CD", "DHY21");
	    		
			    //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
	    	}else if(szYD_AIM_YD_GP.equals("M")) {
	    		setRecord.setField("ARR_WLOC_CD", YdConstant.WLOC_CD_PORT_SLAB_YARD);
	    		
	    	//A후판슬라브	
	    	}else if(szYD_AIM_YD_GP.equals("D")) {
	    		if(szYD_AM_BAY_GP.equals("B")) {
	    			setRecord.setField("ARR_WLOC_CD", "DWY22");
	    		}else{
	    			setRecord.setField("ARR_WLOC_CD", "DKY21");
	    		}
	    		
	    	//후판제품창고	
	    	}else if(szYD_AIM_YD_GP.equals("K")) {
	    		setRecord.setField("ARR_WLOC_CD", "DKY30");
	    	
	    	//통합야드
	    	}else if(szYD_AIM_YD_GP.equals("S")) {
	    		setRecord.setField("ARR_WLOC_CD", "DJY25");//(비상야드추가)
	    	
	    	//A열연COIL야드
	    	}else if(szYD_AIM_YD_GP.equals("1")) {
	    		setRecord.setField("ARR_WLOC_CD", "D2Y45");
	    	
	    	//B열연SLAB야드	
	    	}else if(szYD_AIM_YD_GP.equals("2")) {
	    		setRecord.setField("ARR_WLOC_CD", "D3Y43");
	    	
	    	//B열연 COIL야드	
	    	}else if(szYD_AIM_YD_GP.equals("3")) {
	    		setRecord.setField("ARR_WLOC_CD", "D3Y42");
	    	
	    	//A열연 SLAB야드	
	    	}else if(szYD_AIM_YD_GP.equals("0")) {
	    		setRecord.setField("ARR_WLOC_CD", "D2Y43");
	    		
	    	}
	    	
	    	intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
	    	if (intRtnVal <= 0){
	    		 szMsg = "권하작업시 차량스케줄에 착지개소코드 등록중 Error!! Code No :" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	            throw new  JDTOException("<procY1CrnUdWr> Y1SetYdCar" + szMsg);
	    	}
	    	
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	
	    	int szRowSize = inRecordSet.size(); 

	    	// 권상한 재료만큼 차량스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  
	    	for(int i = 0; i < szRowSize; i++){
	    		
	        	lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(getRecord, "YD_MTL_WT");
	        	intYD_MTL_SH = i + 1;               
	    		
		    	// 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//차량 이송재료 등록 (하차 )
	    		if(intGp == 0) {
	    			
	    			setRecord.setField("MODIFIER",			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",       			"Y");
		    		intRtnVal = this.Y1UpdCarftmvmtl(setRecord, 0) ;
			    	
			    //차량 이송재료 등록 (상차 )	
	    		}else if(intGp == 1) {
	    			setRecord.setField("REGISTER",			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",        "N");
		    		setRecord.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
		    		setRecord.setField("HCR_GP",	    ydDaoUtils.paraRecChkNull(getRecord,"HCR_GP"));
		    		setRecord.setField("STL_PROG_CD",	ydDaoUtils.paraRecChkNull(getRecord,"STL_PROG_CD"));
		    		setRecord.setField("YD_MTL_ITEM",	ydDaoUtils.paraRecChkNull(getRecord,"YD_MTL_ITEM"));
		    		setRecord.setField("YD_ROUTE_GP",	ydDaoUtils.paraRecChkNull(getRecord,"YD_ROUTE_GP"));
		    		
		    		intRtnVal = this.Y1InsYdCarftmvmtl(setRecord) ;
		    	}
		    	inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	
	    	if(intGp == 1) {
	    		
    			//차량스케줄에 등록한다.
    			lngYD_EQP_WRK_WT = lngYD_EQP_WRK_WT + lngYD_MTL_WT;
    			intYD_EQP_WRK_SH = intYD_EQP_WRK_SH + intYD_MTL_SH;
    	    	//setRecord 초기화
    	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("YD_EQP_WRK_WT",       	"" + lngYD_EQP_WRK_WT);
	    		setRecord.setField("YD_EQP_WRK_SH",       	"" + intYD_EQP_WRK_SH);
	    		
	    		intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
    		}
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
		}//end of try~catch
		
    	return 1 ;
    	
    }//end of Y1SetYdCar()
    
    /**
     * 오퍼레이션명 : 작업예약 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1UpdYdWrkbook(JDTORecord msgRecord, int intGp) throws JDTOException {
    	
    	YdWrkbookDao ydWrkbookDao   = new YdWrkbookDao(); 
    	
    	String szMsg = "";
    	String szMethodName = "Y1UpdYdWrkbook";
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydWrkbookDao.updYdWrkbook(msgRecord, intGp);

		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			throw new JDTOException(szMsg);
	    }//end of try~catch
		
		return intRtnVal ;
    }//end of Y1UpdYdWrkbook
    
    /**
     * 오퍼레이션명 : C연주 적치단 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1UpdYdStklyr (JDTORecord msgRecord, int intGp) throws JDTOException {
    	
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMsg 			= "";
    	String szMethodName 	= "Y1UpdYdStklyr";
    	String szOperationName 	= "C연주 적치단 Update";
    	
        try{
        	
	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    				/*
    				 * 모음작업시에는 단이 존재하지 않을 수 있으므로 적치단이 존재하지 않더라도
    				 * 업무는 진행이 되도록 아래 부분을 수정
    				 * 수정자 : 임춘수
    				 * 수정일 : 2009.09.21
    				 */
    				szMsg="적치단이 존재하지 않습니다. - 모음작업 시 오류발생 가능 ";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				intRtnVal = 1;
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			//return intRtnVal = -1;
    		}
			
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y1UpdYdStklyr> " + szMsg);
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y1UpdYdStklyr
    
    /**
     * 오퍼레이션명 : 진도코드갱신 Setting 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y1SetProgCode (JDTORecord msgRecord) throws JDTOException{
    	
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//공통테이블 정보를 담기위한 값
    	//JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szCurrProgCd					= "";
    	String szBefoProgCd					= "";
    	
    	String szMsg						= "";
    	String szMethodName					= "Y1SetProgCode";
    	//재료품목 정의
    	//String szYdMtlItem					= "";
    	//재료종류별 번호
    	String szStlNo						= "";
    	int intRtnVal 						= 0 ;
    	String szRtnMsg						= null;
    	String szSLAB_WO_RT_CD				= null;
    	String szSCARFING_YN				= null;
    	String szSCARFING_DONE_YN			= null;
    	String szMILL_WO_EXN				= "";
    	String szSTL_APPEAR_GP				= null;
    	String szHCR_GP						= null;
    	String szPT_TB_COMM					= null;
    	//전전진도코드
    	String szBEFOBEFO_PROG_CD			= null;
    	//주문여재구분
    	String szORD_YEOJAE_GP  = 			null;
    	String szCURR_PROG_REG_DDTT  = 			null;
    	String szBEFO_PROG_REG_DDTT  = 			null;
    	String szBEFOBEFO_PROG_REG_DDTT  = 			null;
    	String szCURR_PROG_CD_REG_PGM  = 			null;
    	String szBEFO_PROG_CD_REG_PGM  = 			null;
    	String szBEFOBEFO_PROG_CD_REG_PGM  = 			null;
    	String szYD_GP	="";		
    	ymCommonDAO dao = ymCommonDAO.getInstance();
	    List FrtoProductList = null;
        try{
        	
        	szStlNo = msgRecord.getFieldString("STL_NO") ;

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        	 * 업무기준 : C연주슬라브야드, 통합야드에 이송하차 완료 시 현재재료진도코드를 판단하여
        	 * 			주편/슬라브공통테이블에 업데이트 처리
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.21
        	 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	szRtnMsg = YdCommonUtils.getPtCommStock(szStlNo, getRecSet);
        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
        		szMsg = "[진도코드갱신 - Y1SetProgCode] 주편/슬라브공통테이블에서 재료[" + szStlNo + "] 조회 시 오류발생 : " + szRtnMsg;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
        		throw new JDTOException(szMsg);
        	}
        	
        	getRecSet.first();
        	getRecord = getRecSet.getRecord() ;
        	
        	szPT_TB_COMM  		= ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");
        	//주문여재구분
        	szORD_YEOJAE_GP  	= ydDaoUtils.paraRecChkNull(getRecord, "ORD_YEOJAE_GP");
        	//슬라브지시행선코드
        	szSLAB_WO_RT_CD  	= ydDaoUtils.paraRecChkNull(getRecord, "SLAB_WO_RT_CD");
        	//스카핑여부
        	szSCARFING_YN  		= ydDaoUtils.paraRecChkNull(getRecord, "SCARFING_YN");
        	//스카핑완료여부
        	szSCARFING_DONE_YN  = ydDaoUtils.paraRecChkNull(getRecord, "SCARFING_DONE_YN");
        	//압연지시여부 - 슬라브에만 적용됨
        	if(szPT_TB_COMM.equals("S")) {
        		szMILL_WO_EXN  	= ydDaoUtils.paraRecChkNull(getRecord, "MILL_WO_EXN");
        	}
        	
        	szSTL_APPEAR_GP  	= ydDaoUtils.paraRecChkNull(getRecord, "STL_APPEAR_GP");
        	
        	szHCR_GP  			= ydDaoUtils.paraRecChkNull(getRecord, "HCR_GP");
        	
        	szYD_GP  			= ydDaoUtils.paraRecChkNull(getRecord, "YD_GP");
 
        	
        	szMsg = "[진도코드갱신 - Y1SetProgCode] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + "[" + szStlNo + "], ";
        	szMsg += "주문여재구분[" + szORD_YEOJAE_GP + "],재료구분[" + szSTL_APPEAR_GP + "], 야드구분[" + szYD_GP+ "], 슬라브지시행선코드[" + szSLAB_WO_RT_CD + "], 스카핑여부[" + szSCARFING_YN + "], 스카핑완료여부[" + szSCARFING_DONE_YN + "], 압연지시여부[" + szMILL_WO_EXN + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//현재진도코드
        	szCurrProgCd = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD");
        	//전 진도코드
        	szBefoProgCd = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD");
        	//전전진도코드 = 전 진도코드
        	szBEFOBEFO_PROG_CD = szBefoProgCd;
        	//전전진도코드 = 전진도코드
        	szBefoProgCd = szCurrProgCd;
        	
        	//현재진도코드등록Program
        	szCURR_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD_REG_PGM");
        	//전진도코드등록Program
        	szBEFO_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD_REG_PGM");
        	//전전진도코드등록Program
        	szBEFOBEFO_PROG_CD_REG_PGM = szBEFO_PROG_CD_REG_PGM;
        	szBEFO_PROG_CD_REG_PGM = szCURR_PROG_CD_REG_PGM;
        	szCURR_PROG_CD_REG_PGM = szMethodName;
        	
        	//현재진도등록일시
        	szCURR_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_REG_DDTT");
        	//전진도등록일시
        	szBEFO_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_REG_DDTT");
        	//전전진도등록일시
        	szBEFOBEFO_PROG_REG_DDTT = szBEFO_PROG_REG_DDTT;
        	szBEFO_PROG_REG_DDTT = szCURR_PROG_REG_DDTT;
        	szCURR_PROG_REG_DDTT = YdUtils.getCurDate("yyyyMMddHHmmss");
        	
        	//szCurrProgCd = YdCommonUtils.getCurrProgCd(szPT_TB_COMM, szSLAB_WO_RT_CD, szORD_YEOJAE_GP, szSCARFING_YN, szSCARFING_DONE_YN, szMILL_WO_EXN);
			//---------------------------------------------------------------------------------------------------------
			//---------------------------------------------------------------------------------------------------------
        	//공정 함수를 이용한 진도코드 가져오기
        	if(szPT_TB_COMM.equals("B")){
        		//주편 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcd";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szStlNo});
        	}else if (szPT_TB_COMM.equals("S")) {
        		//슬라브 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcdSlab";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szStlNo});
        	}       	

	    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);

	    	szCurrProgCd =StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
			ydUtils.putLog(szSessionName, szMethodName, "IHSF_PM_주편SLAB진도찾기==>>:"+szCurrProgCd, YdConstant.DEBUG);
			//---------------------------------------------------------------------------------------------------------
			//---------------------------------------------------------------------------------------------------------
        	
        	szMsg = "[진도코드갱신 - Y1SetProgCode] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + " 재료[" + szStlNo + "] 수정 후 현재진도코드[" + szCurrProgCd + "], 전진도코드[" + szBefoProgCd + "], 전전진도코드[" + szBEFOBEFO_PROG_CD + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//전 진도코드 = 현재 진도코드,   전전진도코드 = 전진도코드
        	//전 진도코드등록일시 = 현재진도코드등록일시 , 전전진도코드등록일시 = 전진도코드등록일시
			//현재시간
        	setRecord.setField("CURR_PROG_CD", 					szCurrProgCd);
        	setRecord.setField("BEFO_PROG_CD", 					szBefoProgCd);
        	setRecord.setField("BEFOBEFO_PROG_CD", 				szBEFOBEFO_PROG_CD);
			setRecord.setField("CURR_PROG_REG_DDTT", 			szCURR_PROG_REG_DDTT);
			setRecord.setField("BEFO_PROG_REG_DDTT", 			szBEFO_PROG_REG_DDTT);
			setRecord.setField("BEFOBEFO_PROG_REG_DDTT", 		szBEFOBEFO_PROG_REG_DDTT);
			setRecord.setField("CURR_PROG_CD_REG_PGM", 			szCURR_PROG_CD_REG_PGM);
			setRecord.setField("BEFO_PROG_CD_REG_PGM", 			szBEFO_PROG_CD_REG_PGM);
			setRecord.setField("BEFOBEFO_PROG_CD_REG_PGM", 		szBEFOBEFO_PROG_CD_REG_PGM);
			setRecord.setField("FNL_REG_PGM", 					szMethodName);
			setRecord.setField("MODIFIER", 					    "YDSYSTEM");

        	//업데이트 항목 추가해야함 ///////////////////////////////////////////////////////
        	if(szPT_TB_COMM.equals("B")){
        		//주편 공통
        		setRecord.setField("MSLAB_NO", szStlNo);
        		intRtnVal = this.Y1UpdPtComm(setRecord,  2);
        		
        	}else if (szPT_TB_COMM.equals("S")) {
        		//슬라브 공통
        		setRecord.setField("SLAB_NO", szStlNo);
        		intRtnVal = this.Y1UpdPtComm(setRecord,  0);
        	}
        	
        	//----------------------------------------------------------------------------------------------------------
			//	이송하차완료 시 각 재료의 목표야드, 목표동, 목표행선, 진도코드를 설정한다
			//	수정자 : 임춘수
			//	수정일 : 2010.01.06
			//----------------------------------------------------------------------------------------------------------
	        	
        	JDTORecord 	  recTemp 			= JDTORecordFactory.getInstance().create();
        	
        	recTemp.setField("PT_TB_COMM", 			szPT_TB_COMM);						//주편/슬라브구분
			recTemp.setField("STL_NO", 				szStlNo);							//재료번호
			recTemp.setField("SLAB_WO_RT_CD", 		szSLAB_WO_RT_CD);					//슬라브지시행선코드
			recTemp.setField("ORD_YEOJAE_GP", 		szORD_YEOJAE_GP);					//주여구분
			recTemp.setField("SCARFING_YN", 		szSCARFING_YN);						//스카핑여부
			recTemp.setField("SCARFING_DONE_YN", 	szSCARFING_DONE_YN);				//스카핑완료여부
			recTemp.setField("MILL_WO_EXN", 		szMILL_WO_EXN);						//압연지시
			recTemp.setField("YD_GP", 				YdConstant.YD_GP_C_SLAB_YARD);		//야드구분
			//항만야드구분을 set하는 기준변경 필요 : 2015.12.30 LeeJY
			//recTemp.setField("YD_GP", 				YdConstant.YD_GP_PORT_SLAB_YARD);		//야드구분
			recTemp.setField("STL_APPEAR_GP", 		szSTL_APPEAR_GP);					//재료외형구분
			recTemp.setField("HCR_GP", 				szHCR_GP);							//HCR구분
			
			String szRetunMsg = YdCommonUtils.uptStockCodeMapping(recTemp);
			
			szMsg="[진도코드갱신 - Y1SetProgCode] 재료["+szStlNo+"]의 속성을 수정 완료 - 메세지 : " + szRetunMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------

			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 진행관리 슬라브소재이송완료실적전송  - YDPTJ001
	         * 기능 추가 : 허철호
	         * 일자 : 2013.09.02
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    		szRetunMsg = slabComm.sndYDPTJ001(szSTL_APPEAR_GP, szStlNo);

			szMsg="[진도코드갱신 - Y1SetProgCode] 재료["+szStlNo+"]의 Slab이송완료실적 전송 완료 - 메세지 : " + szRetunMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        }catch(Exception e){
        	szMsg = "[진도코드갱신 - Y1SetProgCode] 오류발생 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y1SetProgCode()
    
    /**
     * 오퍼레이션명 : 저장위치 Setting 
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int Y1setYdStrLoc (JDTORecord msgRecord)throws JDTOException{
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= null;
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= null;
    	
    	//현재저장위치
    	String szYdStrLoc					= "";
    	//이전저장위치
    	String szYdStrLocHis1				= "";

    	String szMsg						= "";
    	String szMethodName					= "Y1setYdStrLoc";
    	String szOperationName              = "저장위치 Setting";
    	//재료품목 정의
    	String szYdMtlItem					= "";
    	
    	//크레인스케줄의 정보				현재저장위치를 생성하기 위해...
    	String szYdGp						= "";
    	String szYdBayGp					= "";
    	String szYdEqpId					= "";
    	String szYdStkColNo					= "";
    	String szYdStkBedNo					= "";
    	String szYdStkLyrNo					= "";
    	String szYdDnWrLoc                  = "";
    	String szYD_GP						= null;
    	String szSTL_NO						= null;
    	String szRtnMsg						= null;
    	String szLogMsg						= null;
    	String szPT_TB_COMM					= null;
    	String szYD_CAR_USE_GP				= null;
    	
    	int intRtnVal 						= 0;
        
        try{

            szMsg = "저장위치 Setting ( Y1setYdStrLoc ) ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
            /*
			szYdMtlItem = ydDaoUtils.paraRecChkNull(msgRecord,"YD_MTL_ITEM"); 
			if(szYdMtlItem.length() > 1){
				szYdMtlItem = szYdMtlItem.substring(0, 1);
			}
			*/
			//--------------------------------------------------------------------------------------------------------
			//	권하실적위치가 차량인 경우 차량사용구분이 넘겨진다.
			//	수정자 : 임춘수
			//	수정일 : 2009.12.17
			//--------------------------------------------------------------------------------------------------------
			szYD_CAR_USE_GP		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_CAR_USE_GP") ;
			//--------------------------------------------------------------------------------------------------------

			/*
        	 * 공통테이블의 재료 정보를 조회해서 업데이트를 처리
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.15
        	 */
        	szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "MSLAB_NO");
        	szRtnMsg = YdCommonUtils.getPtCommStock(szSTL_NO, getRecSet);
        	
        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
        		szLogMsg = "[저장위치 Setting - Y1setYdStrLoc]재료[" + szSTL_NO + "]를 공통테이블에서 조회 시 오류발생";
                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
                
                /**
                 * 2012.06.04 윤재광
                 * 종료된 재료정보처리 때문에 이부분에서 Exception 처리로 에러가 발생하는 케이스
                 * - 스크램재 정보처리 등...
                 * 따라서, 이부분 에러처리를 막음(추후 논의)
                 */
                return 1 ;
                //throw new JDTOException(szLogMsg);
        	}
        	
        	getRecSet.absolute(1);
        	getRecord      	= JDTORecordFactory.getInstance().create();
        	getRecord 	   	= getRecSet.getRecord();
        	szYdStrLoc 	   	= ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC").trim();
        	szYdStrLocHis1 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC_HIS1").trim();
        	szPT_TB_COMM 	= ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");
        	
        	szYdGp 		   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").trim(); 
        	szYdBayGp 	   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
        	szYdEqpId 	   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP").trim(); 
        	szYdStkColNo   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO").trim(); 
        	szYdStkBedNo   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO").trim(); 
        	szYdStkLyrNo   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_LYR_NO").trim();
        	
        	setRecord = JDTORecordFactory.getInstance().create();
        	//--------------------------------------------------------------------------------------------------------
        	//	권하실적위치가 차상이고 출하차량이면 공통테이블의 야드구분을 *로 등록
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.17
        	//--------------------------------------------------------------------------------------------------------
        	if(szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) ) {
        		setRecord.setField("YD_GP",         "*");
        	}else{
        		setRecord.setField("YD_GP",         szYdGp);
        	}
        	//--------------------------------------------------------------------------------------------------------
        	setRecord.setField("YD_BAY_GP",     szYdBayGp);
        	setRecord.setField("YD_EQP_GP",     szYdEqpId);
        	setRecord.setField("YD_STK_COL_NO", szYdStkColNo);
        	setRecord.setField("YD_STK_BED_NO", szYdStkBedNo);
        	setRecord.setField("YD_STK_LYR_NO", szYdStkLyrNo);
        	setRecord.setField("FNL_REG_PGM",   "Y1setYdStrLoc");
        	/*
        	 * 권하위치의 야드구분이 다른 경우에는 입고일자와 입고시각을 업데이트 처리
        	 * 주편공통, 슬라브공통 : 입고일자, 입고시각
        	 * PLATE공통 : 입고일자
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.14
        	 */
        	//공통테이블에 저장되어 있는 야드구분
        	szYD_GP	= ydDaoUtils.paraRecChkNull(getRecord,"YD_GP");
        	if( !szYD_GP.equals(szYdGp) ) {
        		String szCurDateTime 	= YdUtils.getCurDate("yyyyMMddHHmmss");
        		String szRECEIPT_DATE 	= szCurDateTime.substring(0, 8);
        		String RECEIPT_TIME 	= szCurDateTime.substring(8);
        		
        		setRecord.setField("RECEIPT_DATE", 	szRECEIPT_DATE);		//입고일자
        		setRecord.setField("RECEIPT_TIME", 	RECEIPT_TIME);			//입고시각
        		
        	}
        	setRecord.setField("MODIFIER",      "YDSYSTEM");
        	
        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
	        if(szYdStrLoc.equals("")){
	        	setRecord.setField("YD_STR_LOC_HIS1", 	"");
	        }else{
	        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc);
	        }
	        
	        if(szYdStrLocHis1.equals("")){
	        	setRecord.setField("YD_STR_LOC_HIS2", 	"");
	        }else{
	        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1);
	        }
        	
        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우
	        szMsg = "전 저장위치 : " + szYdStrLoc + " , 전전 저장위치 : " + szYdStrLocHis1 + ", 권하실적 단 정보 : " + szYdStkLyrNo;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	if(szPT_TB_COMM.equals("B")){
        		
        		setRecord.setField("MSLAB_NO",   msgRecord.getFieldString("MSLAB_NO")); 
        		setRecord.setField("YD_STR_LOC", szYdGp + szYdBayGp + szYdEqpId +
        										 szYdStkColNo + szYdStkBedNo + szYdStkLyrNo.substring(1,3));

        		intRtnVal = this.updY1YdStock(setRecord, 2);
       		
        		//주편정정마감EVENT (YMCSJ001) 2012.01.05
        		EJBConnector ejbConn2 = new EJBConnector("default","CraneUdHdSeEJB",this);
				ejbConn2.trx("procYMCSJ001",new Class[]{String.class}, new Object[]{msgRecord.getFieldString("MSLAB_NO")});
				
        	}else if (szPT_TB_COMM.equals("S")) {
            	
        		setRecord.setField("SLAB_NO",    msgRecord.getFieldString("SLAB_NO")); 
        		setRecord.setField("YD_STR_LOC",  szYdGp + szYdBayGp + szYdEqpId + 
        				                          szYdStkColNo + szYdStkBedNo + szYdStkLyrNo.substring(1,3));
        		//슬라브 공통 업데이트
        		intRtnVal = this.updY1YdStock(setRecord,  0);
         	}
        	
        }catch(Exception e){
			throw new JDTOException(e.getLocalizedMessage());
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y1setYdStrLoc()
    
    /**
     * 오퍼레이션명 : C연주 저장품 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed
     * @throws 
     */
    public int updY1YdStock (JDTORecord msgRecord, int intGp)throws JDTOException{

    	YdStockDao ydStockDao = new YdStockDao();
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "updY1YdStock";
        String szOperationName              = "C연주 저장품 Update";
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
	            return intRtnVal ;
	        }
        }catch(Exception e){
			throw new JDTOException(e.getLocalizedMessage());
        }//end of try~catch
        
        return intRtnVal ;
    	
    }//end of updY1YdStock()
    
    /**
     * 오퍼레이션명 : C연주 대차 스케줄 Select
     *  
     * @param  ● msgRecord, outRecset, intGp
     * @return ● intRtnVal
     * @throws ● 
     */
    public int Y1GetYdTcarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
    	
    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        
    	String szMethodName 				= "Y1GetYdTcarsch";
    	String szMsg 						= "";
    	String szOperationName              = "C연주 대차 스케줄 Select";
        try{
        	
	        intRtnVal = ydTcarschDao.getYdTcarsch(msgRecord, getRecSet, intGp);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="[" + szOperationName + "] data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="[" + szOperationName + "] parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}

	        outRecset.addAll(getRecSet)  ;  
	        
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
        }
        return intRtnVal ;
    }//end of Y1GetYdTcarsch
    
    /**
     * 오퍼레이션명 : 대차이송재료 Update
     *  
     * @param inRecord, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y1UpdTcarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdTcarFtmvMtlDao ydTcarftmvmtlDao   = new YdTcarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
        
    	String szMethodName 				= "Y1UpdTcarftmvmtl";
    	String szMsg 						= "";
    	String szOperationName              = "C연주 대차 스케줄 Update";
        try{
        	
            intRtnVal = ydTcarftmvmtlDao.updYdTcarftmvmtl(inRecord, intGp) ;
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
			
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
	    }	
		
		return intRtnVal ;
    }//end of Y1UpdTcarftmvmtl
    
    /**
     * 오퍼레이션명 : 대차이송재료 Insert
     *  
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y1InsYdTcarftmvmtl(JDTORecord msgRecord) throws JDTOException {
    	YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();
    	
    	int intRtnVal 			= 0 ;

    	String szMethodName 				= "Y1InsYdTcarftmvmtl";
    	String szMsg 						= "";
    	String szOperationName              = "C연주 대차 스케줄 Insert";
        try{
        	
        	intRtnVal = ydTcarftmvmtlDao.insYdTcarftmvmtl(msgRecord);
    		if(intRtnVal == -2) {
				szMsg="[" + szOperationName + "] parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
    		}
        	
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
        }
        return intRtnVal ;
    	
    }//end of Y1InsYdTcarftmvmtl
    
    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *  
     * @param msgRecord, outRecset, intGp(1:상하차)
     * @return intRtnVal
     * @throws 
     */
    public int Y1GetYdCarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMethodName = "Y1GetYdCarsch";
    	String szMsg        = "";
    	String szOperationName              = "C연주 차량 스케줄 Select";
        try{
        	
        	intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);
	        
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="[" + szOperationName + "] data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="[" + szOperationName + "] parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				if (intRtnVal <= 0) return intRtnVal = -2;
			}
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y1GetYdCarsch
    
    /**
     * 오퍼레이션명 : 차량 이송재료 Update
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y1UpdCarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
    	
    	String szMethodName = "Y1UpdCarftmvmtl";
    	String szMsg        = "";
    	String szOperationName              = "C연주 차량 이송재료 Update";
        try{
        	
            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found!!";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
		
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
	    }//end of try~catch	
		
		return intRtnVal ;
		
    }//end of Y1UpdCarftmvmtl
    
    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *  
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y1InsYdCarftmvmtl(JDTORecord msgRecord)throws JDTOException{
    	YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
    	JDTORecordSet rsTemp          	= null;
    	int intRtnVal = 0 ;

    	String szMethodName = "Y1InsYdCarftmvmtl";
    	String szMsg        = "";
    	String szOperationName              = "C연주 차량이송재료 Insert";
        try{
        	//차량 재료정보 존재 유무 체크 
        	rsTemp			= JDTORecordFactory.getInstance().createRecordSet("");
        	String szRtnMsg	= DaoManager.getYdCarftmvmtl(msgRecord, rsTemp, 0);
        	
        	//대상 재료정보가 존재 안 하는 경우에만 등록 처리 함.(무결성 제약 조건(USRYDA.PK_YD_CARFTMVMTL)에 위배됩니다)
        	if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST)){
	        	intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
	    		if(intRtnVal == -2) {
					szMsg="[" + szOperationName + "] parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal = -1;
	    		}
        	}
        	
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
        }//end of try~catch
        
        return intRtnVal = 1;
    }//end of Y1InsYdCarftmvmtl
    
    /**
     * 오퍼레이션명 : 재료진도코드 변경
     *  
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws 
     */
    public int Y1UpdPtComm (JDTORecord msgRecord, int intGp)throws JDTOException{
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	int intRtnVal 			= -1 ;
        String szMsg            = "";
        String szMethodName     = "Y1UpdPtComm";
        String szOperationName              = "C연주 재료진도코드 변경";
        try{
        	//intRtnVal = ydStockDao.updPtComm_LOC(msgRecord, intGp);
        	//임춘수 수정 저장위치 변경이 아닌 진도코드 업데이트 2009.07.21
        	intRtnVal = ydStockDao.updPtComm_PROG_CD(msgRecord, intGp);
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            }
	            return intRtnVal ;
	        }
        }catch(Exception e){
        	szMsg = "[Y1UpdPtComm - 재료진도코드 변경 ]오류발생 - " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch
        
        
        return intRtnVal ;
    	
    }//end of Y1UpdPtComm()
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 오퍼레이션명 : C연주 비상조업실적등록 (Y1YDL010)
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY1CrnEmgPtopWr(JDTORecord msgRecord)throws JDTOException  {

    	
    	//업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
        //적치단클리어시 업데이트 항목
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();

        int intRtnVal 					= 0 ;
        int intRowsize					= 0 ;
        
        String szMsg              		= "";
        String szMethodName       		= "procY1CrnEmgPtopWr";
        String szOperationName          = "C연주비상조업실적등록";
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        	return;
        }
        
        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

        try{
        	//=============================================================
        	// 권오창
        	// 2009.11.04
        	//
        	// Log 테이블 등록 
        	//=============================================================
        	szMsg = "[C연주정정] 비상조업실적등록 수신";
        	ydUtils.putLogMsg("A", "yd_monitorA", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

	        //파라미터 check
        	//새로 만들어야 하는 부분
	        intRtnVal = this.Y1EmerOperParamCheck(msgRecord, getParamRecord) ;
	        if(intRtnVal == -1) {
                szMsg = "파라미터 Check중 Error	: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }

	        setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
	        setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
	        setCrnschRecord.setField("MODIFIER",              "SYSTEM") ;
	        setCrnschRecord.setField("YD_EQP_ID",             getParamRecord.getFieldString("YD_EQP_ID")) ;
	        setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
	        setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;
	        setCrnschRecord.setField("YD_DN_WR_LOC",          getParamRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setCrnschRecord.setField("YD_DN_WR_LAYER",        getParamRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        
	        //크레인 스케줄의 Insert하기위해 스케줄의 항목의 값을 Setting하고 업데이트한다.
	        intRtnVal = this.Y1InsYdCrnsch(setCrnschRecord) ;
	        if (intRtnVal == 2) {
                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
                return ;
	        }
	        
	        //크레인작업재료 Insert
			setRecord.setField("YD_CRN_SCH_ID", getParamRecord.getFieldString("YD_CRN_SCH_ID")) ;
			setRecord.setField("STL_NO", 		getParamRecord.getFieldString("STL_NO")) ;
			
	        intRtnVal = this.Y1InsYdCrnWrkMtl(setRecord) ;
	        if (intRtnVal == 2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

	        //from위치정리
	        if(getParamRecord.getFieldString("STL_NO").equals("") || getParamRecord.getFieldString("STL_NO") == null) {
                szMsg = "'STL_NO' Data Error	: 재료번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }	        

	        //대상 데이터 SELECT			재료번호로 적치단 조회
	        intRtnVal = this.Y1GetYdStklyr(getParamRecord, getRecSet,3);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

	        intRowsize = getRecSet.size() ;
	        
	        getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(intRowsize == 0){
	            szMsg = "적치단에 등록된 재료번호가 없습니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        }
	        getRecord = getRecSet.getRecord();
		        
	        if(intRowsize > 0){

		        for(int i=0; i<intRowsize; i++) {
		        //클리어셋팅
		        	setRecord = JDTORecordFactory.getInstance().create();
		        	setRecord.setField("YD_STK_COL_GP", 		getRecord.getFieldString("YD_STK_COL_GP")) ;
		        	setRecord.setField("YD_STK_BED_NO", 		getRecord.getFieldString("YD_STK_BED_NO")) ;
		        	setRecord.setField("YD_STK_LYR_NO", 		getRecord.getFieldString("YD_STK_LYR_NO")) ;
		        	setRecord.setField("STL_NO",      		    "") ;
		        	setRecord.setField("MODIFIER",      		"SYSTEM") ;
		        	setRecord.setField("YD_STK_LYR_ACT_STAT", 	"E") ;
		        	setRecord.setField("YD_STK_LYR_MTL_STAT", 	"E") ;

		        	//적치단 업데이트
		        	intRtnVal = this.Y1UpdYdStklyr(setRecord, 0) ;
			        switch (intRtnVal) {
			        	case 0	:
			                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
			        	case -1	:
			                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return ;
			        	case -2	:
			                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return ;
			        	case -3	:
			                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return ;
			        }	        
		        
			        getRecSet.next();
			        getRecord = getRecSet.getRecord();
		        }//end of for
	        }//end of if
	        
	        //적치단에 재료의 실적위치에 실적정보를 등록한다.
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_STK_COL_GP", 		getParamRecord.getFieldString("YD_DN_WR_LOC").substring(0,6)) ;
	        setRecord.setField("YD_STK_BED_NO", 		getParamRecord.getFieldString("YD_DN_WR_LOC").substring(6,8)) ;
	        setRecord.setField("YD_STK_LYR_NO", 		getParamRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        setRecord.setField("MODIFIER", 				"SYSTEM") ;
	        setRecord.setField("STL_NO", 				getParamRecord.getFieldString("STL_NO")) ;
	        setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C") ;
	        intRtnVal = this.Y1UpdYdStklyr(setRecord, 0) ;
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	        	case -1	:
	                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }
	        
		        
	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : C연주슬라브야드L2  크레인작업실적응답 전문 전송(YDY1L005)
	         * 수정자 : 임춘수
	         * 일자 : 2009.06.17
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("MSG_ID"        , "YDY1L005");
	        setRecord.setField("YD_EQP_ID"     , getParamRecord.getFieldString("YD_EQP_ID"));
	        setRecord.setField("YD_SCH_CD",     getParamRecord.getFieldString("YD_SCH_CD")) ;
	        setRecord.setField("YD_CRN_SCH_ID", getParamRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_EMG_PTOP);					//U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        setRecord.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);				//야드L3처리결과코드
			ydDelegate.sendMsg(setRecord);
			szMsg = "[C연주비상조업실적등록]C연주슬라브야드L2 크레인작업실적응답[YDY1L005] 전송 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	            
	        szMsg="C연주 크레인 비상조업  실적 등록 완료";
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	    }catch(Exception e) {
	    	System.out.println("Error :  "+ e.getLocalizedMessage());
	    	throw new JDTOException(e.getLocalizedMessage());
	    }//end of try~catch
	    
    }// end of procY1CrnEmgPtopWr()
    
    /**
     * 오퍼레이션명 : 비상조업실적등록 파라미터 체크
     *  
     * @param  ● msgRecord, outRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1EmerOperParamCheck (JDTORecord msgRecord, JDTORecord outRecord) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "";
        String szMethodName                 = "Y1EmerOperParamCheck";
        int intRtnVal = 0 ;
        
    	try{
            setRecord.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
			setRecord.setField("YD_CRN_SCH_ID"			, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
			setRecord.setField("YD_SCH_CD"        		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
			setRecord.setField("STL_NO"          		, ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")) ;
			setRecord.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
			setRecord.setField("YD_UP_WR_LOC"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
			setRecord.setField("YD_UP_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;
			setRecord.setField("YD_DN_WR_LOC"         	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
			setRecord.setField("YD_DN_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;
    		
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException(e.getLocalizedMessage());
        }//end of try~catch
        
        intRtnVal = 1 ;
        return intRtnVal;
        
    }//end of Y1EmerOperParamCheck()
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Insert
     *  
     * @param  ● msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1InsYdCrnsch(JDTORecord msgRecord) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	String szMsg        = "";
    	String szMethodName = "Y1InsYdCrnsch";
    	
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnschDao.insYdCrnsch(msgRecord);		       
			if(intRtnVal == -2) {
				szMsg = "크레인 스케줄 등록중 Error!! ErrorCode: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException(e.getLocalizedMessage());
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y1InsYdCrnsch
    
    /**
     * 오퍼레이션명 : 크레인작업재료 Insert
     *  
     * @param  ● msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1InsYdCrnWrkMtl(JDTORecord msgRecord) throws JDTOException {
    	YdCrnWrkMtlDao ydCrnwrkmtlDao = new YdCrnWrkMtlDao();

    	String szMsg        = "";
    	String szMethodName = "Y1InsYdCrnWrkMtl";
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnwrkmtlDao.insYdCrnwrkmtl(msgRecord);		        
			if(intRtnVal == -2) {
				szMsg = "크레인 작업재료 삽입 중 Error!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
			}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException(e.getLocalizedMessage());
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y1InsYdCrnWrkMtl
    
    /**
     * 오퍼레이션명 : 적치단 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y1GetYdStklyr (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szMsg = "";
    	String szMethodName = ""; 
    	int intRtnVal = 0 ;
        
        try{

	    	intRtnVal = ydStklyrDao.getYdStklyr(msgRecord, getRecSet, intGp);  
	    	
	    	outRecSet.addAll(getRecSet)  ; 
	        if (intRtnVal <= 0) {
	        	return intRtnVal;
	        }
	        
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException(e.getLocalizedMessage());
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y1GetYdStklyr
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    
	/**
     * 오퍼레이션명 : A후판 크레인 권하실적처리 (Y3YDL009)
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY3CrnUdWr(JDTORecord msgRecord) throws DAOException {
		EJBConnector ejbConn 		= null;
		String szMethodName         = "procY3CrnUdWr";
		String szRtnMsg             = "";		
		String szLogMsg             = "";
		String szEjbJndiName 	= "CraneLdHdSeEJB";
		String szEjbMethod 		= "procY3CrnWrkOrdReq";
		JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
		try {
			
			//통합야드권하실적처리
			ejbConn = new EJBConnector("default", "CraneUdHdSeEJB", this);
			outRecord =(JDTORecord)ejbConn.trx("procY3CrnUdWrTX", new Class[] { JDTORecord.class}, new Object[] { msgRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), YdConstant.RETN_CD_FAILURE);
			
			if( sRTN_CD.equals(YdConstant.RETN_CD_FAILURE) ) {			//성공
				szLogMsg = "권하실적처리 오류  (" + szRtnMsg + ")";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, 1);
				return YdConstant.RETN_CD_FAILURE;
			}

			
			//크레인 작업지시 호출
			szLogMsg = "[A후판 크레인 권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//SJH040002
//			ejbConn = new EJBConnector("default", szEjbJndiName, this);			
//			szRtnMsg = (String)ejbConn.trx(szEjbMethod, new  Class[] { JDTORecord.class }, new Object[] { outRecord });
	
			String sMSG_ID	= StringHelper.evl(outRecord.getFieldString("MSG_ID"),"");
			if (sMSG_ID.equals("YDYDJ641")) {			
				//크레인작업지시 송신
				ydDelegate.sendMsg(outRecord);
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			}	
			
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || 
				szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || 
				szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
				szLogMsg = "[A후판 크레인 권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}else{
				szLogMsg = "[A후판 크레인 권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szSessionName + e.getMessage(), e);
		}
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procY3CrnUdWr
	
    
    /**
     * 오퍼레이션명 : A후판 크레인 권하실적처리 (Y3YDL009)
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */         
    public JDTORecord procY3CrnUdWrTX(JDTORecord msgRecord)throws JDTOException  {
    	
    	YdDelegate      ydDelegate      = new YdDelegate();
    	YdEqpDao        ydEqpDao        = new YdEqpDao();
    	YdTcarSchDao    ydTcarSchDao    = new YdTcarSchDao();
    	YdCarSchDao     ydCarSchDao     = new YdCarSchDao();
    	YdStockDao      ydStockDao      = new YdStockDao();
    	YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
    	YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
    	YdStkColDao     ydStkColDao     = new YdStkColDao();
    	YdCrnWrkMtlDao ydCrnWrkMtlDao 	= new YdCrnWrkMtlDao(); 
    	YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();
    	
        int intRtnVal = 0;
        
        String szOperationName              = "A후판권하실적처리";
        //DATA SETTING시 사용
        JDTORecord setRecord 				= JDTORecordFactory.getInstance().create();
        
        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord                = JDTORecordFactory.getInstance().create(); ;
        
        //작업예약 업데이트 항목
        JDTORecord bookrecord               = JDTORecordFactory.getInstance().create();
        
        //크레인 스케줄 야드작업진행상태 비교		 (작업예약이 끝났는지 확인하기위해 카운터 값을 가져옴)
        JDTORecord outRecord                = JDTORecordFactory.getInstance().create();         
        
        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 			= JDTORecordFactory.getInstance().create();
       
        JDTORecord recInTemp                = null;
        JDTORecord recOutTemp               = null;
        JDTORecord recInPara                = null;
        
        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
        
        JDTORecord    recSendMsg            = null;
        
        JDTORecordSet rsResult              = null;
        
        String szMsg                        = "";
        String szMethodName                 = "procY3CrnUdWrTX";
        
        String szTcarEqpId                  = "";
        String szCarEqpId                  	= "";
        
        //크레인 XYZ축 			파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
        String szYD_CRN_XAXIS     			= "";
        String szYD_CRN_YAXIS     			= "";
        String szYD_CRN_ZAXIS     			= "";
        //WBOOK_ID				작업예약 완료 처리시 사용
        String szYdWbookId              	= "";
        String szCrnSchId                   = "";
        String szQuery                      = "";
        
        String szYD_UP_WR_LOC               = "";
        String szYD_DN_WR_LOC               = "";
        String szYD_SCH_CD                  = "";
        String szYD_EQP_ID                  = "";
        String szYD_DN_WR_LAYER             = "";
        String szYD_WRK_PROG_STAT           = "";
        String szYD_CAR_SCH_ID  			= "";
        String szYD_TCAR_SCH_ID             = "";
        
        //-----------------------------------------------------------------------------------------
	    //실제Lyr를 검사하여 처리하기 위해 필요한 변수들
	    JDTORecordSet getLyrSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
	      
	    String szYD_STK_COL_GP              = "";
	    String szYD_STK_BED_NO              = "";
	    String szREAL_TOP_LYR               = "";
	    int    intRealTopLyr                = 0;
	    int    intYdDnWrLayer               = 0;
	    int    rowsize                      = 0;
        //-----------------------------------------------------------------------------------------
        
        //EJB CALL OR JMS CALL
        String szIS_EJB_CALL				= null;

        String szLogMsg						= null;
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
            szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            
            szMsg = YdConstant.RETN_CD_TC_ERROR;
            throw new JDTOException("<procY3CrnUdWr> " + szMsg);
        }
        
        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
        
        try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[후판슬라브야드] 크레인 권하실적처리 수신";
			ydUtils.putLogMsg("D", "yd_monitorD", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
        	szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
        	
        	//-------------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------------
	        intRtnVal = this.Y3ParamCheck(msgRecord, getCrnschRecord, 1) ;
	        
	        szCrnSchId 				= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_CRN_SCH_ID");
	        szYD_EQP_ID 			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_EQP_ID");
	        szYD_SCH_CD 			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_SCH_CD");
	        szYD_DN_WR_LOC			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC");
	        szYD_DN_WR_LAYER		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER");
	        szYD_WRK_PROG_STAT		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_WRK_PROG_STAT");
	        
	        if( szCrnSchId.equals("") ) {
                szMsg = "["+szOperationName+"] 크레인스케줄ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY3CrnUdWr> " + szMsg);
	        }
        	
	        //파라미터 레코드 편집
	        setRecord.setField("YD_CRN_SCH_ID",       	getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_SCH_CD",           	getCrnschRecord.getFieldString("YD_SCH_CD"));
	        setRecord.setField("YD_DN_WR_LOC",        	getCrnschRecord.getFieldString("YD_DN_WR_LOC"));
	        setRecord.setField("YD_DN_WR_LAYER",      	getCrnschRecord.getFieldString("YD_DN_WR_LAYER"));
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        intRtnVal = this.Y3UpdYdCrnsch(setRecord, 0);
	        
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------------------------
	        
            //-------------------------------------------------------------------------------------------------------------------
	        //	크레인스케줄 작업재료 조회
            //-------------------------------------------------------------------------------------------------------------------
	        intRtnVal = this.Y3GetYdCrnsch(setRecord, getRecSet,3);
	        if(intRtnVal < 0){
				szMsg = "[" + szOperationName + "] 크레인스케줄  Y1GetYdCrnsch failed!!!, ErrorCode:" + intRtnVal;
		        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
		        m_ctx.setRollbackOnly();
		        throw new JDTOException("<procY3CrnUdWr> Y3GetYdCrnsch" + szMsg);
			}
	        
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "권하실적 크레인작업재료 : no data found!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            m_ctx.setRollbackOnly();
	            throw new JDTOException("<procY3CrnUdWr> " + szMsg);
	        }
	        
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();
	        
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 완료 - 대상재 건수 : " + getRecSet.size();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        //권상실적위치
        	szYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
	        //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szYdWbookId = ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //	작업진행상태 확인 후 상태가 맞지 않으면 롤백 처리
            //-------------------------------------------------------------------------------------------------------------------
	        if ((!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("2")) && 
	        	(!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("3"))) {
	        	szMsg = "["+szOperationName+"] 작업진행상태["+ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT")+"]가   권상('2') 또는 권하대기('3')이 아닙니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            m_ctx.setRollbackOnly();
	            throw new JDTOException("<procY3CrnUdWr> " + szMsg);
	        }
	            
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하지시위치와 권하실적위치가 다르면 권하지시위치의 맵정보를 Clear시킴
	        //-------------------------------------------------------------------------------------------------------------------
	        if (!getRecord.getFieldString("YD_DN_WR_LOC").equals(getRecord.getFieldString("YD_DN_WO_LOC"))) {
	        	szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	        	
	        	//크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
	        	if(szYD_CRN_XAXIS.equals("") && szYD_CRN_YAXIS.equals("") && szYD_CRN_ZAXIS.equals("")) {
	        		szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_XAXIS");
	        		szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_YAXIS");
	        		szYD_CRN_ZAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_ZAXIS");
	        	}
	        	
	        	intRtnVal = this.Y3ClearYdStklyr(getRecSet,1) ;
	        	if(intRtnVal < 0){
			        m_ctx.setRollbackOnly();
			        throw new JDTOException("<procY3CrnUdWr> Y3ClearYdStklyr" + szMsg);
				}
    	        
    	        szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 완료";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        }
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하실적LYR와 실제적치단의 최상위 위치가 다르면 권하실적LYR를 변경하고 적치단 정보를 수정한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        szYD_STK_COL_GP = szYD_DN_WR_LOC.substring(0, 6);
	        szYD_STK_BED_NO = szYD_DN_WR_LOC.substring(6, 8);
	        intYdDnWrLayer 	= Integer.parseInt(szYD_DN_WR_LAYER);
	        	                
	        recInPara	= JDTORecordFactory.getInstance().create();
	        recInPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	        recInPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
	        
	        intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, getLyrSet, 98);
	        
	        if(intRtnVal < 0){
	        	m_ctx.setRollbackOnly();
	        	throw new JDTOException("<procY3CrnUdWr> getYdStklyr ERROR CODE =" + intRtnVal);
			}
	        
	        getLyrSet.first();
	        recOutTemp = getLyrSet.getRecord();
	        
	        szREAL_TOP_LYR 	= ydDaoUtils.paraRecChkNull(recOutTemp,"REAL_TOP_LYR");
	        intRealTopLyr 	= Integer.parseInt(szREAL_TOP_LYR);
	        
	        if (intYdDnWrLayer != intRealTopLyr) {
	        	
	        	szMsg = "[" + szOperationName + "] 실적적치단[" + intYdDnWrLayer + "]과 실재야드적치단[" + intRealTopLyr + "]이 상이하여 실적적치단 변경 시작";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	
	            szYD_DN_WR_LAYER = szREAL_TOP_LYR;
    	    }
	        //-------------------------------------------------------------------------------------------------------------------	
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //	크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정 시작";
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        
	        intRtnVal = this.Y3RegYdStklyr(	getRecSet,
	        								szYD_DN_WR_LAYER) ;
	        
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //------------------------------------------------------------------------------
			//	동내이적의 권하분리 시 마지막 스케줄이 아닌 스케줄의 TO위치결정방법이 S, T인 경우에는
			//	해당 베드를 완산베드로 설정을 해서 다음 스케줄에서 TO위치결정 시 베드가 제외되도록 처리
			//	수정일 : 2010.03.11 - 임춘수
			//------------------------------------------------------------------------------
            String szYD_TO_LOC_DCSN_MTD			= ydDaoUtils.paraRecChkNull(getRecord,"YD_TO_LOC_DCSN_MTD");
	        szYD_DN_WR_LOC 						= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC");
	        
	        if( szYD_TO_LOC_DCSN_MTD.equals("S") || 
				szYD_TO_LOC_DCSN_MTD.equals("T") ) {
				szMsg="[" + szOperationName + "] 권하분리 시 TO위치결정방법이 S, T이고 마지막 스케줄이 아닌 스케줄의 TO위치베드를 완산베드로 설정했으므로 해제 처리 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp 				= JDTORecordFactory.getInstance().create();
	        	recInTemp.setField("YD_STK_COL_GP", 			szYD_DN_WR_LOC.substring(0, 6));
	        	recInTemp.setField("YD_STK_BED_NO", 			szYD_DN_WR_LOC.substring(6));
	        	recInTemp.setField("YD_STK_BED_WHIO_STAT", 		"E");
	        	recInTemp.setField("MODIFIER", 					szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
	        	
	        	String szRtnMsg			= DaoManager.updYdStkbed(recInTemp, 0);
	        	
	        	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	        		szMsg="[" + szOperationName + "] 권하분리 시 TO위치결정방법이 S, T이고 마지막 스케줄이 아닌 스케줄의 TO위치베드를 완산베드로 설정했으므로 해제 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	}else{
	        		szMsg="[" + szOperationName + "] 권하분리 시 TO위치결정방법이 S, T이고 마지막 스케줄이 아닌 스케줄의 TO위치베드를 완산베드로 설정했으므로 해제 처리 시 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        	}
			}
			//------------------------------------------------------------------------------
	      
			//-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_DN_WR_LOC",       getCrnschRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setRecord.setField("YD_DN_WR_LAYER",     getCrnschRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        setRecord.setField("YD_DN_WRK_ACT_GP",   getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP") );
	        setRecord.setField("YD_DN_WR_XAXIS",     getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
	        setRecord.setField("YD_DN_WR_YAXIS",     getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
	        setRecord.setField("YD_DN_WR_ZAXIS",     getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
	        setRecord.setField("YD_WRK_PROG_STAT",   getCrnschRecord.getFieldString("YD_WRK_PROG_STAT"));
	        setRecord.setField("YD_DN_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));
	        setRecord.setField("YD_GP",   			 getCrnschRecord.getFieldString("YD_GP"));

	        //--------------------------------------------------------------------------------------------------
	        //	크레인스케줄 등록 시 야드작업계상일자에 등록되는 값을 권하완료시점에 다시 수정, 권하완료 시의 값이 사용됨
	        //	TO_CHAR(SYSDATE - (7 / 24), 'YYYYMMDD')
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.23
	        //--------------------------------------------------------------------------------------------------
	        String szYD_WRK_HDS_DD			= YdUtils.getDefaultHdsDate();
	        
	        setRecord.setField("YD_WRK_HDS_DD",   		szYD_WRK_HDS_DD);
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]의 야드작업계상일자["+szYD_WRK_HDS_DD+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        //--------------------------------------------------------------------------------------------------
	        
            szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]의 권하실적 정보 수정 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        intRtnVal = this.Y3UpdYdCrnsch(setRecord, 0);
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]의 권하실적 정보 수정 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        
            szMsg = "저장위치의 설비구분 : " + szYD_DN_WR_LOC.substring(2, 4);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
            szMsg = "스케줄코드의 설비구분 : " + szYD_SCH_CD.substring(2, 4);
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 

            //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 대차이므로 대차 스케줄 이송재료 등록 후 대차스케줄 호출
	        //-------------------------------------------------------------------------------------------------------------------
	        if(szYD_DN_WR_LOC.substring(2, 4).equals("TC")){
            	
	        	//-------------------------------------------------------------------------------------------------------------------
	            //	권하실적위치로 대차설비ID 조합 생성
	            //-------------------------------------------------------------------------------------------------------------------
	        	szTcarEqpId = szYD_DN_WR_LOC.substring(0,1) + "XTC" + szYD_DN_WR_LOC.substring(4,6);
	        	//-------------------------------------------------------------------------------------------------------------------
	        	
	            //-----------------------------------------------------
                //	대차작업지정기준을 BRE Rule에서 조회
                //	수정일 : 1. 2010.02.25 - 임춘수
                //-----------------------------------------------------
                String[] szTCarRule	= YdCommonUtils.getTCarWrkStdRule(szTcarEqpId);
                //-----------------------------------------------------
                recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_WBOOK_ID", 					szYdWbookId);	//작업예약ID
	        	recInPara.setField("YD_WRK_PLAN_TCAR", 				szTcarEqpId);	//작업계획대차
	        	
                if( szTCarRule[0].equals("Y") ) {
                
                	if( szTCarRule[1].equals("D") ) {	//대차 직상차
                		//기준상차동과 실제작업한 상차동이 같을경우
                		if( szTCarRule[2].equals(szYD_DN_WR_LOC.substring(1, 2))){
                			//-----------------------------------------------------
            	        	//	대차작업지정기준이 직상차인 경우에는 하차동을 목표동으로 재설정
            	        	//-----------------------------------------------------
            	        	recInPara.setField("YD_AIM_BAY_GP", 			szTCarRule[3]);		//목표동
            	        	//-----------------------------------------------------
            	        }
	                }
		        }    
                
                intRtnVal = ydWrkbookDao.updYdWrkbook(recInPara, 0);
	        	if(intRtnVal <= 0) {
	    			szMsg = YdConstant.RETN_CD_FAILURE;
	    			m_ctx.setRollbackOnly();
	    			throw new JDTOException("<procY1CrnUdWr> updYdWrkbook" + szMsg);
	    		}
	        	szMsg = "[" + szOperationName + "] [대차 직상차]상차작업예약ID["+szYdWbookId+"]에 대차설비ID["+szTcarEqpId+"]  목표동["+szTCarRule[3]+"] 업데이트 완료";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
	        	//-------------------------------------------------------------------------------------------------------------------
	        	//	권하실적위치로 만들어진 대차설비ID를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 시작
	        	//-------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
	        	
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_EQP_ID", szTcarEqpId);
	        	
	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        	intRtnVal = ydTcarSchDao.getYdTcarsch(recInPara, rsResult, 4);
	        	if(intRtnVal <= 0) {
	                szMsg = "[" + szOperationName + "] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 대차스케줄 조회 시 오류발생 - 반환값 : " + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	                m_ctx.setRollbackOnly();
	                throw new JDTOException(szMsg);
	        	}
	        	
	        	rsResult.absolute(1);
	        	recOutTemp = JDTORecordFactory.getInstance().create();
	        	recOutTemp.setRecord(rsResult.getRecord());
	        	
	        	szMsg = "[" + szOperationName + "] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 완료 - 대상재건수 : " + rsResult.size();
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
                szYD_TCAR_SCH_ID		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
                
                //-------------------------------------------------------------------------------------------------------------------
	        	//	조회된 대차 스케줄에 상차작업예약id를 등록한다.
                //-------------------------------------------------------------------------------------------------------------------
                szMsg = "[" + szOperationName + "] [대차 상차]대차스케줄[" + szYD_TCAR_SCH_ID + "]에 상차작업예약ID["+szYdWbookId+"]를 업데이트 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_TCAR_SCH_ID", 				szYD_TCAR_SCH_ID);
	        	recInPara.setField("YD_CARLD_WRK_BOOK_ID", 			szYdWbookId);
	        	recInPara.setField("YD_EQP_WRK_STAT", 				"L");
	        	recInPara.setField("YD_CAR_PROG_STAT", 				"4");//상차개시
	        	recInPara.setField("YD_CARLD_STOP_LOC", 			szYD_DN_WR_LOC.substring(0,6));
	        	intRtnVal = ydTcarSchDao.updYdTcarsch(recInPara, 0);
	        	if(intRtnVal <= 0) {
	                szMsg = "[" + szOperationName + "] [대차 상차]대차스케줄[" + szYD_TCAR_SCH_ID + "] 상차작업예약["+szYdWbookId+"] 등록시 Error";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                m_ctx.setRollbackOnly();
	                throw new JDTOException("<procY1CrnUdWr> updYdTcarsch" + szMsg);
	        	}
	        	
	        	szMsg = "[" + szOperationName + "] [대차 상차]대차스케줄[" + szYD_TCAR_SCH_ID + "]에 상차작업예약ID["+szYdWbookId+"]를 업데이트 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	//-------------------------------------------------------------------------------------------------------------------

	        	//-------------------------------------------------------------------------------------------------------------------
    		    //	권하실적위치가 대차이므로 대차이송재료 등록
    	        //-------------------------------------------------------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 권하실적위치가 대차이므로 대차이송재료 등록 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            	
            	this.Y1SetYdTcar(getRecSet,szYD_TCAR_SCH_ID) ; 	
            	//-------------------------------------------------------------------------------------------------------------------
            	
            	//-------------------------------------------------------------------------------------------------------------------
    		    //	권하실적위치가 대차이므로 대차스케줄 호출
    	        //-------------------------------------------------------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 권하실적위치가 대차이므로 대차스케줄 호출 시작 - 대차설비ID["+szTcarEqpId+"]";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            	
                recSendMsg = JDTORecordFactory.getInstance().create();
            	recSendMsg.setField("MSG_ID", 			"YDYDJ522");
            	recSendMsg.setField("YD_LD_UD_GP", 		"L");
            	recSendMsg.setField("YD_WBOOK_ID", 		szYdWbookId);
            	recSendMsg.setField("YD_EQP_ID", 		szTcarEqpId);
            	
    			// 권하처리 요청 
    			ydEjbCon.trx("TransEqpSchSeEJB", "procY3TcarSch", recSendMsg);

    			szMsg = "[" + szOperationName + "] 권하실적위치가 대차이므로 대차스케줄 호출 완료";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                //-------------------------------------------------------------------------------------------------------------------
            }
            //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //	차량스케줄에 차량 상차작업예약ID등록 수정!!
	        //	만약  to위치는 차량인데 차량스케줄코드가 아닌경우...는 직상차로 보고 차량스케줄의 상차작업예약id를 등록한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        if(szYD_DN_WR_LOC.substring(2, 4).equals("PT")&& 
	          !szYD_SCH_CD.substring(2, 4).equals("PT")) {
	            
	            //권하위치 적치열구분으로 적치열을 조회하여 운송장비코드를 조회한다.
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_STK_COL_GP", szYD_DN_WR_LOC.substring(0,6));
	            
	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        	intRtnVal = ydStkColDao.getYdStkcol(recInPara, rsResult, 0);
	        	if(intRtnVal <= 0) {
		            szMsg = szYD_DN_WR_LOC.substring(0,6) + "적치열구분이 잘못되었습니다.";
		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		            m_ctx.setRollbackOnly();
		            throw new JDTOException("<procY3CrnUdWr> getYdStkcol " + szMsg);
	        	}
	        	rsResult.absolute(1);
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setRecord(rsResult.getRecord());
	        	
	            //운송장비코드로 차량스케줄을 조회한다.
	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        	intRtnVal = ydCarSchDao.getYdCarsch(recInPara, rsResult, 7);
	        	if(intRtnVal <= 0) {
	                szMsg = ydDaoUtils.paraRecChkNull(recInPara, "TRN_EQP_CD") + "로 생성된 차량 스케줄이 없습니다.";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                szMsg = YdConstant.RETN_CD_NOTEXIST;
	                m_ctx.setRollbackOnly();
	                throw new JDTOException("<procY3CrnUdWr> getYdCarsch " + szMsg);
	        	}
	        	rsResult.absolute(1);
	        	recOutTemp = JDTORecordFactory.getInstance().create();
	        	recOutTemp.setRecord(rsResult.getRecord());
	        	
	            //조회된 차량스케줄에 상차작업예약ID를 등록한다.
	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID").trim());
	        	recInPara.setField("YD_CARLD_WRK_BOOK_ID", szYdWbookId);
	        	recInPara.setField("YD_EQP_WRK_STAT", "L");
	        	recInPara.setField("YD_CAR_PROG_STAT", "4");//상차개시
	        	recInPara.setField("YD_CARLD_STOP_LOC", szYD_DN_WR_LOC.substring(0,6));
	        	intRtnVal = ydCarSchDao.updYdCarsch(recInPara, 0);
	        	if(intRtnVal <= 0) {
	                szMsg = "직상차용 차량스케줄 상차작업예약 등록시 Error";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                m_ctx.setRollbackOnly();
	                throw new JDTOException("<procY3CrnUdWr> updYdCarsch " + szMsg);
	        	}
	        }

	        //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 차량이므로 차량 스케줄 이송재료 등록 후 차량진행관리 호출
	        //-------------------------------------------------------------------------------------------------------------------
            if(szYD_DN_WR_LOC.substring(2, 4).equals("PT")|| 
               szYD_DN_WR_LOC.substring(2, 4).equals("TR")){
            	
            	intRtnVal = this.Y3SetYdCar(getRecSet, 1) ; 
            	
            	szMsg = "차량이송재료 등록 완료";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            
            	// 차량 작업 진행관리 호출
            	recInTemp = JDTORecordFactory.getInstance().create();
            	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
            	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
            	recInTemp.setField("CAR_LDUD_GP",   "U");
            	recInTemp.setField("YD_DN_WR_LOC",   szYD_DN_WR_LOC);
            	
				szMsg = "차량 작업 진행관리 호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.displayRecord(szOperationName, recInTemp);

            	szYD_CAR_SCH_ID = this.procY3CarWrkStatCtr(recInTemp);
            }
            //-------------------------------------------------------------------------------------------------------------------
            
            //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 차량이아니고  권상위치가 차량이면 차량진행관리 호출
	        //-------------------------------------------------------------------------------------------------------------------
            if(!szYD_DN_WR_LOC.substring(2, 4).equals("PT")&& 
               !szYD_DN_WR_LOC.substring(2, 4).equals("TR")){
            	
            	//권상실적위치가 차량인 경우
            	if(szYD_UP_WR_LOC.substring(2, 4).equals("PT")|| 
            	   szYD_UP_WR_LOC.substring(2, 4).equals("TR")){
                	
            		recInTemp = JDTORecordFactory.getInstance().create();
                	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
                	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
                	recInTemp.setField("CAR_LDUD_GP",   "L");
                	recInTemp.setField("YD_UP_WR_LOC",   szYD_UP_WR_LOC);
                
					szMsg = "차량 작업 진행관리 호출";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.displayRecord(szOperationName, recInTemp);                	
                	
                	//차량 작업 진행관리 호출(하차)
					szYD_CAR_SCH_ID = this.procY3CarWrkStatCtr(recInTemp);
            	}
            }
            //-------------------------------------------------------------------------------------------------------------------
            
            if(getRecord.getFieldString("YD_WBOOK_ID") == null ||
     	       getRecord.getFieldString("YD_WBOOK_ID").equals("")) {
                szMsg = "YD_WBOOK_ID  Data Error	: 작업예약 ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY3CrnUdWr> " + szMsg);
	        }
	        
	        //-------------------------------------------------------------------------------------------------------------------
		    //	2010.04.14 윤재광 인터페에스 송신처리
	        //-------------------------------------------------------------------------------------------------------------------
            {
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			생산통제 A후판장입진행실적 전송  - YDCTJ031
		         * 업무기준 Desc : 1. A후판가열로 장입보급Carry-In
		         * 				  2. 보급베드 - DAPU01
		         * 				  3. 대상재의 야드목표행선 : C3[작업대기(A후판압연)]
		         * 스케줄코드 :  1. A후판가열로 장입보급Carry-In 스케줄
		         * 장입보급진행상태  : 30 - W/B,장입구,디파일러(적치완료)
		         * 기능 추가 : 임춘수
		         * 일자 : 2009.06.18
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        if(szYD_DN_WR_LOC.startsWith(YdConstant.EQP_D_PU1)){		/*보급CARRY-IN스케줄, A동 보급PICKUP베드(DAPU01) */
		        
		        	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDCTJ031");
					recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
					recInTemp.setField("CHG_SUP_PROG_STAT", "30");
					ydDelegate.sendMsg(recInTemp);
					
					szMsg = "[권하실적처리]생산통제 A후판장입진행실적[YDCTJ031] 전송 완료 - A후판가열로 장입보급Carry-In";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			생산통제 A후판장입진행실적 전송  - YDCTJ031
		         * 업무기준 Desc : 1. 장입동 01스판 적치
		         * 				  2. 권상실적위치가 01스판이 아니고 권하실적위치가 01스판인 경우
		         * 장입보급진행상태  : 10 - 장입동적치10스판
		         * 기능 추가 : 임춘수
		         * 일자 : 2009.06.18
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        else if(!szYD_UP_WR_LOC.substring(2, 4).equals("01") && 
		        		 szYD_DN_WR_LOC.matches("DA01\\d\\d\\d\\d")){			/* 권상실적위치가 01스판이 아니고 권하실적위치가 01스판인 경우*/
		        	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",        "YDCTJ031");
					recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
					recInTemp.setField("CHG_SUP_PROG_STAT", "10");
					ydDelegate.sendMsg(recInTemp);
					
					szMsg = "[권하실적처리]생산통제 A후판장입진행실적[YDCTJ031] 전송 완료 - 장입동10스판적치";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        }
            }
            //-------------------------------------------------------------------------------------------------------------------
            
            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업 및 재료, 작업예약 및 재료정보를 Clear
	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인 작업재료 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("MODIFIER",           "YDSYSTEM");
	        intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl_YD_CRN_SCH_ID(setRecord);
	        if(intRtnVal <= 0) {
                szMsg = "크레인스케줄작업재료 삭제중 Error!! Code : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
	        	throw new JDTOException("<procY1CrnUdWr> updYdCrnwrkmtl_YD_CRN_SCH_ID " + szMsg);
	        }
	        
	        //크레인스케줄 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("YD_DN_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss")); //권하완료일시
	        intRtnVal = this.Y3UpdYdCrnsch(setRecord, 0);
	        if(intRtnVal < 0){
                szMsg = "[" + szOperationName + "] 크레인스케줄 삭제처리 에러발생!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY3CrnUdWr> Y3UpdYdCrnsch " + szMsg);
	        }
	        
	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        getRecord = JDTORecordFactory.getInstance().create();
	        getRecord.setField("YD_WBOOK_ID", szYdWbookId);

	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
			intRtnVal = this.Y3GetYdCrnsch(getRecord, getRecSet, 28);
			if(intRtnVal < 0){
                szMsg = "[" + szOperationName + "] 작업예약완료 CHECK 에러발생!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY3CrnUdWr> Y3GetYdCrnsch " + szMsg);
	        }
	        
	        /*
	         * 작업예약에 존재하는 크레인스케쥴 정보가 없을경우에 작업예약정보를 삭제한다.
	         */	
	        if (getRecSet.size() == 0) {
	        	
	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId) ;
	            bookrecord.setField("DEL_YN",             "Y") ;
	            bookrecord.setField("YD_SCH_PROG_STAT",   "E") ;
	            
	            intRtnVal = this.Y3UpdYdWrkbook(bookrecord, 0);
		        		        
		        //작업예약재료조회
	            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	            intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(bookrecord, getRecSet, 1);
				
	            //조회한 작업예약재료1매씩 저장품 업데이트
				for( int Loop_i = 1; Loop_i <= getRecSet.size(); Loop_i++ ) {
					getRecSet.absolute(Loop_i);
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setRecord(getRecSet.getRecord());
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
					recInTemp.setField("YD_SCH_CD", "");
					recInTemp.setField("YD_WBOOK_ID", "");
					recInTemp.setField("YD_STK_COL_GP", szYD_DN_WR_LOC.substring(0,6));
					recInTemp.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6,8));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, Loop_i-1));
					intRtnVal = ydStockDao.updYdStock(recInTemp, 0);
				}
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("STL_NO",      ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
				recInTemp.setField("DEL_YN",      "Y");
				recInTemp.setField("MODIFIER",    "SYSTEM");
				recInTemp.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID"));
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl1(recInTemp);
		        
	            szMsg = "작업 예약 처리 완료";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	            
	            
	        }else{
	        	szMsg = "작업 예약 진행 중";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	        }
	        
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			A후판슬라브야드L2 크레인작업실적응답 전송  - YDY3L005
	         * 업무기준 Desc : 크레인 권하실적처리 성공 후 크레인작업실적응답 전송
	         * 기능 추가 : 임춘수
	         * 일자 : 2009.06.19
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        recInTemp = JDTORecordFactory.getInstance().create(); 
	        recInTemp.setField("MSG_ID"        , "YDY3L005");
	        recInTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);											//야드설비ID
	        recInTemp.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_DN_CMPL);					//야드작업진행상태
	        recInTemp.setField("YD_SCH_CD"   , szYD_SCH_CD);											//야드스케줄코드
	        recInTemp.setField("YD_CRN_SCH_ID"   , szCrnSchId);											//야드크레인스케줄ID
	        //==================================================================================
	        // 수신전문의 진행상태가 강제권하(5)일시 응답으로는 강제권하값(F)로 내려보내야 됨
	        //==================================================================================	        
	        if(szYD_WRK_PROG_STAT.equals("4")){
	        	recInTemp.setField("YD_L2_WR_GP", YdConstant.CRN_WRK_RE_DN_WR);							//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        } else {
	        	recInTemp.setField("YD_L2_WR_GP", YdConstant.CRN_WRK_RE_FRCE_DN);						//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하	        	
	        }	        

	        recInTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);					//야드L3처리결과코드
			ydDelegate.sendMsg(recInTemp);
			szMsg = "[권하실적처리]A후판슬라브야드L2 크레인작업실적응답[YDY3L005] 전송 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            
            /*
			 * 이력테이블등록호출 
			 */
			{
				CrnSchSeEJBBean crnSchSeEJBBean = new CrnSchSeEJBBean();
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",             "");
				recInTemp.setField("YD_WBOOK_ID",        szYdWbookId);
				recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId);
				recInTemp.setField("YD_CAR_SCH_ID",      szYD_CAR_SCH_ID);
				recInTemp.setField("YD_TCAR_SCH_ID",     szYD_TCAR_SCH_ID);
				recInTemp.setField("YD_WTCL_TNK_SCH_ID", "");
				crnSchSeEJBBean.procWorkHistoryCreate(recInTemp);
				
				szMsg="[" + szOperationName + "] 이력테이블등록호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
            
			//-------------------------------------------------------------------------------------------------------------------
		    //	크레인 설비상태 권하완료(4) 셋팅.
	        //-------------------------------------------------------------------------------------------------------------------
            recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	        recInTemp.setField("YD_EQP_STAT", "4");
            intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
            if(intRtnVal <= 0) {
				 szMsg="설비상태 UPDATE 처리시 오류 발생.";
			 	 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	   			 throw new JDTOException(szMsg);
	   		}
            
	        //설비id로 설비Table조회
	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
			
			/*
			 * 크레인 작업지시 요구호출.
			 */
			{
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord(0));
		         
		        //크레인 작업지시 호출
				recInTemp = JDTORecordFactory.getInstance().create();
//SJH03004
				
				recInTemp.setField("MSG_ID",           "YDYDJ641");
				
				recInTemp.setField("YD_EQP_ID",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID"));
				recInTemp.setField("YD_EQP_WRK_MODE",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE"));
				recInTemp.setField("YD_WRK_PROG_STAT", "4");
				recInTemp.setField("YD_SCH_CD",        szYD_SCH_CD);
				recInTemp.setField("YD_CRN_SCH_ID",    "");
				recInTemp.setField("YD_CRN_XAXIS",     "");
				recInTemp.setField("YD_CRN_YAXIS",     "");
				recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_SUCCESS);
				
//				String szEjbJndiName 	= "CraneLdHdSeEJB";
//				String szEjbMethod 		= "procY3CrnWrkOrdReq";
//				
//				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//				 * ++++++ 데이타의 정합성 때문에 추가 - 아래 항목을 수정 시에는 수정자에게 문의 +++++++
//				 * 크레인스케줄이 삭제(DEL_YN)필드가 Y로 설정된 후 COMMIT전에 
//				 * 크레인작업지시모듈에서 조회 시 현재 Y로 설정된 동일한 크레인스케줄이 조회가 되어
//				 * 전문편집 모듈에 넘겨주고 다시 조회 시 Y로 COMMIT되는 경우가 생겨서 L2로 크레인작업지시가
//				 * 내려가지 않는 현상이 발생하여 EJB CALL로 변경. 
//				 * 수정자 : 임춘수
//				 * 수정일 : 2009.09.03
//				 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				szIS_EJB_CALL = "Y";
//				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				
//				if( szIS_EJB_CALL.equals("Y")) {
//					
//					szLogMsg = "[A후판권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//
//					EJBConnector ejbConn = new EJBConnector("default", szEjbJndiName, this);			
//					String szRtnMsg = (String)ejbConn.trx(szEjbMethod, new  Class[] { JDTORecord.class }, 
//																	   new Object[] { recInTemp });
//					if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
//						szLogMsg = "[A후판권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					}else{
//						szLogMsg = "[A후판권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
//						//리턴값이 오류일지라도 롤백 등 후속작업을 하지 않음...
//					}
//					//크레인작업지시 송신
//					szLogMsg = "[" + szOperationName + "] 크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - JMS 전문전송 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//
//				}else{
//					//크레인작업지시 송신
//					ydDelegate.sendMsg(recInTemp);
//					szLogMsg = "[" + szOperationName + "] 크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - JMS 전문전송 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				}
			}	
			
			//------------------------------------------------------------------
	        // 권하 실적시 Flex 실시간 처리
	        //------------------------------------------------------------------
	        JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
		 	recFlex.setField("YD_GP",  YdConstant.YD_GP_A_PLATE_SLAB_YARD);
		 	recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
		 	recFlex.setField("YD_UP_WR_LOC", szYD_UP_WR_LOC);
		 	recFlex.setField("YD_DN_WR_LOC", szYD_DN_WR_LOC);
			szMsg="Flex 권하 완료 실적 전송";
		 	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		ydUtils.putYdFlexCrnWrk("", recFlex);  

        }catch(Exception e) {
        	/*
        	 * Exception 발생시에도 작업실적 응답은 송신.
        	 */
        	{
		        recInTemp = JDTORecordFactory.getInstance().create(); 
		        recInTemp.setField("MSG_ID"          , "YDY3L005");
		        recInTemp.setField("YD_EQP_ID"       , szYD_EQP_ID);					// 야드설비ID
		        recInTemp.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_DN_CMPL);	// 야드작업진행상태
		        recInTemp.setField("YD_SCH_CD"       , szYD_SCH_CD);					// 야드스케줄코드
		        recInTemp.setField("YD_CRN_SCH_ID"   , szCrnSchId);						// 야드크레인스케줄ID
		        if(szYD_WRK_PROG_STAT.equals("4")){
		        	recInTemp.setField("YD_L2_WR_GP", YdConstant.CRN_WRK_RE_DN_WR);			// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
		        } else {
		        	recInTemp.setField("YD_L2_WR_GP", YdConstant.CRN_WRK_RE_FRCE_DN);		// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하	        	
		        }	        
		        recInTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);	// 야드L3처리결과코드
				ydDelegate.sendMsg(recInTemp);
				
				szMsg = "[" + szOperationName + "] A후판 슬라브야드L2 크레인작업실적응답[YDY3L005] 전송 완료";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	}
			szLogMsg = "[A후판권하실적처리]권하실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }finally{
        }
        return recInTemp;
    }// end of procY3CrnUdWrTX()
    
	/**
	 * 오퍼레이션명 : 차량 작업 진행관리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procY3CarWrkStatCtr(JDTORecord msgRecord)throws JDTOException  {
		//TC_CODE :YDYDJ630
		
		YdDelegate ydDelegate = new YdDelegate();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao(); 
		YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
		YdCarSchDao     ydCarSchDao     = new YdCarSchDao();  

		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recFirst          = null;
		JDTORecord    recLast           = null;
		
	    int intRtnVal 		   = 0 ;
	    
	    String szMsg           = "";
	    String szMethodName    = "procY3CarWrkStatCtr";
	    
	    String szQuery         = "";
	    
	    String szCAR_LDUD_GP   = "";
	    String szYD_WBOOK_ID   = "";
	    String szYD_CRN_SCH_ID = "";
	    String szFST_CRN_SCH_ID = "";
	    String szLST_CRN_SCH_ID = "";
	    String szYD_SCH_CD      = "";
	    String szYD_GP          = "";
	    String szYD_CAR_SCH_ID  = "";
	    String szYD_DN_WR_LOC   = "";
	    String szYD_UP_WR_LOC   = "";
	    String szYD_CAR_USE_GP  = "";

	    try{
	    	//상하차구분 플래그, 작업예약id, 크레인스케줄id
	    	szCAR_LDUD_GP   = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_LDUD_GP"); 
	    	szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	    	szYD_DN_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC");
	    	szYD_UP_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
	    	
	    	//작업예약id로 크레인 스케줄을 조회
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	    	
	    	if(szCAR_LDUD_GP.equals("L")){
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 31);
		    	if(intRtnVal <= 0) {
					szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error 상차인경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new JDTOException("<procY3CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}else{
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 32);
		    	if(intRtnVal <= 0) {
					szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error 하차인경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new JDTOException("<procY3CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}
	    	
			szMsg = "rsResult 사이즈 : " + rsResult.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	rsResult.first();
	    	recFirst = JDTORecordFactory.getInstance().create();
	    	recFirst.setRecord(rsResult.getRecord());
	    	szFST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recFirst, "YD_CRN_SCH_ID");
	    	
			szMsg = "첫번째 스케줄id : " + szFST_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	rsResult.last();
	    	recLast = JDTORecordFactory.getInstance().create();
	    	recLast.setRecord(rsResult.getRecord());
	    	szLST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recLast, "YD_CRN_SCH_ID");
	    	
			szMsg = "마지막 스케줄id : " + szLST_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "전문 스케줄id : " + szYD_CRN_SCH_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	szYD_SCH_CD      = ydDaoUtils.paraRecChkNull(recFirst, "YD_SCH_CD");
	    	szYD_GP          = szYD_SCH_CD.substring(0,1);
	    	
	    	//플래그가 상차인경우
	    	if(szCAR_LDUD_GP.equals("U")) {
	    		//차량스케줄 id를 조회
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    		ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    		szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP"); 	

	    		//출하차량인 경우에만 적용한다.
	    		if(szYD_CAR_USE_GP.equals("G")){
	    			
					szMsg="일품 상차실적 송신 YDDMR013 (외판슬라브일품출하상차실적)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID",        "YDDMR013");
					
					recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("YD_GP",         szYD_GP);
		
					ydDelegate.sendMsg(recInTemp);
	    		}
	    		
	    		
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			//동일하면 차량스케줄에 상차완료일시 등록, 설비작업상태 = '영차'
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_CAR_PROG_STAT", "5");
	    			recInTemp.setField("YD_EQP_WRK_STAT", "L");
	    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 1);
	    			if(intRtnVal <= 0) {
						szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}
	    			
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			//구내운송
	    			if(szYD_CAR_USE_GP.equals("L")){							//구내운송 - 임춘수 수정 2009.06.15	
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 시작 2009.06.08 임춘수 추가 ----------------------
	    				intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
	    				if( intRtnVal <= 0 ) {
	    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
	    					szMsg="상차완료시 공통테이블 업데이트 처리 실패";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				}else{
	    					szMsg="상차완료시 공통테이블 업데이트 처리 성공";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				}
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------
	    			 
					
	    			}else{
		    			//상차작업완료 송신 YDDMR017 (외판슬라브출하상차완료)	    			
		    			recInTemp.setField("MSG_ID",        "YDDMR017");
						szMsg="외판슬라브출하상차완료 송신 : YDDMR017";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
		    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
		    			recInTemp.setField("YD_GP",         szYD_GP);
		    			
		    			ydDelegate.sendMsg(recInTemp);
		    			
						szMsg="상차작업완료 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}
	    		}
	    		
	    	//플래그가 하차인 경우
	    	}else if(szCAR_LDUD_GP.equals("L"))	{
	    		//차량스케줄 id를 조회
	    		recInTemp  = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			rsResult   = JDTORecordFactory.getInstance().createRecordSet("");
	    		ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    			
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			recInTemp.setField("YD_GP",         szYD_GP);
    			
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			//동일하면 차량스케줄에 하차완료일시 등록
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_EQP_WRK_STAT", "U");
	    			recInTemp.setField("YD_CAR_PROG_STAT", "E");
	    			recInTemp.setField("DEL_YN",           "N");
	    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 3);
	    			if(intRtnVal <= 0) {
						szMsg="차량스케줄에 하차완료일시  등록시 Error!! Code : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}
	    			
	    			//---------------- 하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 추가
	    			intRtnVal = YdCommonUtils.procCarUnLoadCmpl(szYD_CAR_SCH_ID);
    				if( intRtnVal <= 0 ) {
    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
    					szMsg="하차완료시 공통테이블 업데이트 처리 실패";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    				}else{
    					szMsg="하차완료시 공통테이블 업데이트 처리 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    				}
	    			//-----------------하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 끝
	    			
	    			//하차작업완료 송신 YDTSJ010 - 구내운송 전송
    				recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID",        "YDTSJ010");
	    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
	    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    			recInTemp.setField("YD_GP",         szYD_GP);
	    			ydDelegate.sendMsg(recInTemp);
	    			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    	         * 			진행관리 슬라브소재이송완료실적전송  - YDPTJ001
	    	         * 업무기준 Desc : 1. 하차완료시
	    	         * 스케줄코드 :  1. 차량하차 스케줄 : PT(팔렛트), TR(트레일러), LM(하차)
	    	         * 기능 추가 : 임춘수
	    	         * 일자 : 2009.06.16
	    	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    			recInTemp.setField("MSG_ID",        "YDPTJ001");
	    			ydDelegate.sendMsg(recInTemp);
	    	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    			
					szMsg="하차작업완료 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}
	    		
	    	}else{
				szMsg="상차 및 하차 구분 플래그 Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new JDTOException("<procY1CarWrkStatCtr> ERROR!! " + szMsg);
	    	}
	    	
	    	msgRecord.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

		}catch(Exception e){
	
			szMsg="차량 작업 진행관리 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException("<procY3CarWrkStatCtr> ERROR!! " + szMsg);
		}
	
		szMsg="차량 작업 진행관리 처리"+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return szYD_CAR_SCH_ID;
	} //end of procY3CarWrkStatCtr()
    
    /**
     * 오퍼레이션명 : 권상,권하 파라미터 체크
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int Y3ParamCheck (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        int intRtnVal = 1 ;
        
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

			setRecord.setField("YD_DN_WR_LOC"            	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
			setRecord.setField("YD_DN_WR_LAYER"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;
 
            //전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
	        if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "A") ;
	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "B") ;
	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "M") ;
	        }
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException("<Y3ParamCheck> " + e.getLocalizedMessage());
        }//end of try~catch
        
        return intRtnVal;
        
    }//end of ParamCheck()
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3UpdYdCrnsch(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	int intRtnVal = 0 ;
    	
    	String szMethodName = "Y3UpdYdCrnsch";
    	String szMsg        = "";
    	String szOperationName              = "A후판 크레인스케줄 Update";
		try{
			
			intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);		        
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                return intRtnVal = -1;
	        	case -1	:
	                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return intRtnVal = -1;
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return intRtnVal = -1;
	        	case -3	:
	                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return intRtnVal = -1;
	        }
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y3UpdYdCrnsch> " + szMsg);
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y3UpdYdCrnsch
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3GetYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMethodName = "Y3GetYdCrnsch";
    	String szMsg        = "";
    	String szOperationName              = "A후판 크레인스케줄 Select";
        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
	        switch (intRtnVal) {
        	case 0	:
                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
                return intRtnVal;
        	case -2	:
                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
                return intRtnVal;
        }
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<procY3CrnUdWr> Y3GetYdCrnsch" + szMsg);
        }//end of try~catch

        return intRtnVal ;
    	
    }//end of Y3GetYdCrnsch()
    
    /**
     * 오퍼레이션명 : 적치단 Clear
     *  
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3ClearYdStklyr (JDTORecordSet getRecSet, int intGp) throws JDTOException {
    	
    	JDTORecord getRecord = JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
    	
    	int intRtnVal 		= 1;
    	String szMsg 		= "";
    	String szMethodName = "Y3ClearYdStklyr";
    	
    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();
            
    		for(int i=0; i<rowsize ; i++){
    			//권상 지시위치 Clear
                if(intGp == 0) {
        			String szYD_UP_WR_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC") ;
                    String szYD_UP_WR_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER") ;
                    
                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
                    //적치단 설정
                    String szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                    setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                }
                
                //권하 지시위치 Clear
                if(intGp == 1) {
	    			String szYD_DN_WO_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC") ;
	                String szYD_DN_WO_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LAYER") ;
	                
	                setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
	                setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   
	                //적치단 설정
	                String szStkLyr =ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i);
	                setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                }
                
                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                setRecord.setField("STL_NO",              "");
                
                intRtnVal = this.Y3UpdYdStklyr(setRecord, 0);  //적치단의 재료정보 Clear
                
                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for
    		
    		
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException("<Y1ClearYdStklyr> " + szMsg);
	    }//end of try~catch
		
		return  intRtnVal;
    }//end of Y3ClearYdStklyr()
    
    /**
     * 오퍼레이션명 : 적치단 등록
     *  
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3RegYdStklyr (	JDTORecordSet getRecSet, 
    							String sRealLyrNo)throws JDTOException {
    	
    	JDTORecord getRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 			= null;
    	JDTORecord crnRecord 			= null;
    	
    	JDTORecord recPara 				= null;
    	JDTORecord recTemp 				= null;
    	JDTORecordSet rsResult          = null;
    	
    	String szRtnMsg					= null;
    	String szMsg 					= "";
        String szMethodName				= "Y3RegYdStklyr";
        String szOperationName          = "적치단 등록";
        
        int intRtnVal 					= 0 ;
        
        String szYD_EQP_ID 				= "";
        String szYD_WBOOK_ID 			= "";
        String szYD_CRN_SCH_ID			= null;
        
        String szYD_DN_WR_LOC			= null;
        String szYD_DN_WR_LAYER			= "";
        String szSTL_NO					= "";
    	//차량사용구분
        String szYD_CAR_USE_GP			= null;
        
        String szStkLyr	= "";
        
    	try{
    		int rowsize = getRecSet.size();
            
    		//----------------------------------------------------------------------------------------------------------
	        //	권하실적위치가 차상이고 출하차량이면 공통테이블의 야드구분을 *로 등록 처리하기 위해서 차량스케줄 조회
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.17
	        //----------------------------------------------------------------------------------------------------------
    		getRecSet.first();
    		getRecord		= getRecSet.getRecord();
    		
    		recTemp			= JDTORecordFactory.getInstance().create();
	        
    		szYD_DN_WR_LOC	   			= ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
    		szYD_WBOOK_ID 				= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID");
    		
	        if( szYD_DN_WR_LOC.length() > 6  && 
	        	szYD_DN_WR_LOC.substring(2, 4).equals("PT") ) {
	        	
	        	szMsg = "권하실적위치가 차상이므로 차량스케줄 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	
	        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
	        	
	        	recTemp.setField("YD_CARLD_WRK_BOOK_ID",       	szYD_WBOOK_ID);
	        	
	        	szRtnMsg = DaoManager.getYdCarsch(recTemp, rsResult, 3);
	    	    
	    	    if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    	    	rsResult.first();
	    	    	recTemp		= rsResult.getRecord();
	    	    	
	    	    	szYD_CAR_USE_GP		= ydDaoUtils.paraRecChkNull(recTemp,"YD_CAR_USE_GP") ;
	    	    	
	    	    	szMsg = "권하실적위치가 차상이고 차량스케줄이 존재하므로 차량사용구분을 확인 - 차량사용구분[" + szYD_CAR_USE_GP + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	    }
	    	    
	    	    szMsg = "권하실적위치가 차상이므로 차량스케줄 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        }
	        
	        //----------------------------------------------------------------------------------------------------------
	        boolean isLast	= true;
    		recPara		= JDTORecordFactory.getInstance().create();
    		
        	for(int i=0; i<rowsize; i++) {
        		
        		getRecSet.absolute(i+1);
        		getRecord 	= JDTORecordFactory.getInstance().create();
        		getRecord.setRecord(getRecSet.getRecord());
        		
        		szYD_EQP_ID 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_EQP_ID");
    	        szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID");
    	        szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(getRecord,"YD_CRN_SCH_ID");
    	        
    	        szYD_DN_WR_LOC	    = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
        		szYD_DN_WR_LAYER	= sRealLyrNo; //ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER") ;
        		szSTL_NO	 		= ydDaoUtils.paraRecChkNull(getRecord,"STL_NO") ;
    	        
        		//크레인에 UPDATE
    			crnRecord = JDTORecordFactory.getInstance().create();
    			crnRecord.setField("YD_STK_COL_GP",       szYD_EQP_ID);    
    			crnRecord.setField("YD_STK_BED_NO",       "01");   
    			crnRecord.setField("YD_STK_LYR_NO",       "00"+(i+1)) ;
                crnRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                crnRecord.setField("STL_NO",              "");
                
                intRtnVal = this.Y3UpdYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE
                
                //권하 실적위치 등록
                setRecord = JDTORecordFactory.getInstance().create();
        		setRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6));   
        		setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8));               
                szStkLyr = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO", 		szStkLyr) ;
                setRecord.setField("STL_NO",              	szSTL_NO);                            
                //==========================================================================================
                
                //-------------------------------------------------------------------------------------------------------------
                //	같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 경우에는 권상대기 상태로 변경을 하고
                //	그렇지 않은 경우에는 적치중 상태로 변경한다.
                //	수정자 : 임춘수
                //	수정일 : 2009.12.16
                //-------------------------------------------------------------------------------------------------------------
                
                szMsg = "같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
                rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
                
                recPara.setField("YD_CRN_SCH_ID",          	szYD_CRN_SCH_ID);
                recPara.setField("YD_WBOOK_ID",            	szYD_WBOOK_ID);
                recPara.setField("STL_NO",              	szSTL_NO);
                
                szRtnMsg		= DaoManager.getYdCrnwrkmtl(recPara, rsResult, 17);
                
                szMsg = "같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
                //-------------------------------------------------------------------------------------------------------------
                if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");				//적치중
                	isLast	= true;
                }else if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", 	"U");				//권상대기
                	isLast	= false;
                }else{
                	szMsg = "다음크레인스케줄의 작업재료를 조회 중 오류발생 - " + szRtnMsg;
        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        			
        			setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");				//적치중으로 반영
        			isLast	= true;
                }
                //-------------------------------------------------------------------------------------------------------------
                ydUtils.putLog(szSessionName, szMethodName, "isLast = "+ isLast, YdConstant.DEBUG);
                if(isLast) {
	                /*
	                 * 적치단에 존재하는 해당 저장품정보를 모두 CLEAR한다.
	                 */
	                recTemp			= JDTORecordFactory.getInstance().create();
	                recTemp.setField("STL_NO", szSTL_NO);               
	            	intRtnVal = (new YdStkLyrDao()).updYdStklyrWithStock(recTemp);
                }
                
                //적치단dao를 호출해서 업데이트를 한다.
                intRtnVal = this.Y3UpdYdStklyr(setRecord, 0); 

    	        
				//저장위치 갱신				공통테이블에 저장위치를 갱신한다.
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
				//setRecord.setField("YD_MTL_ITEM",       	szYD_MTL_ITEM);
				setRecord.setField("YD_DN_WR_LOC",       	ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC")); 
    	        
				//----------------------------------------------------------------------------------------------------------
    	        //	권하실적위치가 차상이면 차량사용구분을 전달
    	        //	수정자 : 임춘수
    	        //	수정일 : 2009.12.17
    	        //----------------------------------------------------------------------------------------------------------
				setRecord.setField("YD_CAR_USE_GP",       	szYD_CAR_USE_GP); 
				//----------------------------------------------------------------------------------------------------------
    	        
    	        intRtnVal = this.Y3setYdStrLoc(setRecord) ;
    	        
    	        //차량 하차작업 일 경우			공통테이블에 진도코드를 갱신한다.
    	        if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")|| 
    	           ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){
    	        	
    	        	//getRecord.setField("YD_MTL_ITEM",       	szYD_MTL_ITEM);
    	        	//진도코드 갱신
    	        	intRtnVal = this.Y3SetProgCode(getRecord) ;
    	        }
    		}
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException(szMsg);
	    }//end of try~catch
		
		intRtnVal = 1 ;
		return intRtnVal ;
    
    }//end of Y3RegYdStklyr()
    
    /**
     * 오퍼레이션명 : 차량 Setting
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y3SetYdCar (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
    	
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szOperationName              = "A후판 차량 Setting";
    	String szMethodName 				= "Y3SetYdCar";
    	String szMsg 						= "";
    	String szYD_AIM_YD_GP               = "";
    	String szYD_AM_BAY_GP               = "";
    	
    	long lngYD_MTL_WT                  = 0;
    	int  intYD_MTL_SH                  = 0;
    	long lngYD_EQP_WRK_WT              = 0;
    	int  intYD_EQP_WRK_SH              = 0;
    	
    	int intRtnVal = 0 ;
    	
    	//차량 스케줄 ID
    	String szYD_CAR_SCH_ID = "";
    	
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
	    	
	    	// 상하차 작업예약 ID로 차량스케줄 조회
	    	intRtnVal = this.Y3GetYdCarsch(setRecord, outRecSet, 3) ;
	    	if (intRtnVal <= 0){
	    		szMsg = "차량에서 권하작업 처리시 차량스케쥴 정보 오류발생.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	            throw new  JDTOException(szMsg);
	    	}
	    	
	    	
	    	// 차량스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 차량스케줄 ID를 추출한다
	    	szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
	    	lngYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullLong(getTcarRecord, "YD_EQP_WRK_WT");
	    	intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(getTcarRecord, "YD_EQP_WRK_SH");
	    	
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	setRecord.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    	setRecord.setField("MODIFIER", "YDSYSTEM");
	    	
	    	szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");
	    	szYD_AM_BAY_GP = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_BAY_GP");
	    	
	    	//C연주
	    	if(szYD_AIM_YD_GP.equals("A")) {
	    		setRecord.setField("ARR_WLOC_CD", "DHY21");
	    		
			    //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
	    	}else if(szYD_AIM_YD_GP.equals("M")) {
	    		setRecord.setField("ARR_WLOC_CD", YdConstant.WLOC_CD_PORT_SLAB_YARD);
	    		
	    	//A후판슬라브	
	    	}else if(szYD_AIM_YD_GP.equals("D")) {
	    		if(szYD_AM_BAY_GP.equals("B")) {
	    			setRecord.setField("ARR_WLOC_CD", "DWY22");
	    		}else{
	    			setRecord.setField("ARR_WLOC_CD", "DKY21");
	    		}
	    		
	    	//후판제품창고	
	    	}else if(szYD_AIM_YD_GP.equals("K")) {
	    		setRecord.setField("ARR_WLOC_CD", "DKY30");
	    	
	    	//통합야드
	    	}else if(szYD_AIM_YD_GP.equals("S")) {
	    		setRecord.setField("ARR_WLOC_CD", "DJY25");//(비상야드추가)
	    	
	    	//A열연COIL야드
	    	}else if(szYD_AIM_YD_GP.equals("1")) {
	    		setRecord.setField("ARR_WLOC_CD", "D2Y45");
	    	
	    	//B열연SLAB야드	
	    	}else if(szYD_AIM_YD_GP.equals("2")) {
	    		setRecord.setField("ARR_WLOC_CD", "D3Y43");
	    	
	    	//B열연 COIL야드	
	    	}else if(szYD_AIM_YD_GP.equals("3")) {
	    		setRecord.setField("ARR_WLOC_CD", "D3Y42");
	    	
	    	//A열연 SLAB야드	
	    	}else if(szYD_AIM_YD_GP.equals("0")) {
	    		setRecord.setField("ARR_WLOC_CD", "D2Y43");
	    		
	    	}
	    	
	    	intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
	    	if(intRtnVal <= 0 ) {
                szMsg = "권하작업시 차량스케줄에 착지개소코드 등록중 Error!! Code No :" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
                throw new  JDTOException("<procY3CrnUdWr> Y3SetYdCar" + szMsg);
	    	}
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	
	    	int szRowSize = inRecordSet.size(); 

	    	// 권상한 재료만큼 차량스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  
	    	for(int i = 0; i < szRowSize; i++){
	    		
	        	lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(getRecord, "YD_MTL_WT");
	        	intYD_MTL_SH = i + 1;               
	    		
		    	// 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//차량 이송재료 등록 (하차 )
	    		if(intGp == 0) {
	    			setRecord.setField("MODIFIER",			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",       			"Y");
		    		intRtnVal = this.Y3UpdCarftmvmtl(setRecord, 0) ;
			    	
			    //차량 이송재료 등록 (상차 )	
	    		}else if(intGp == 1) {
	    			setRecord.setField("REGISTER",			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",        "N");
		    		setRecord.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
		    		setRecord.setField("HCR_GP",	    ydDaoUtils.paraRecChkNull(getRecord,"HCR_GP"));
		    		setRecord.setField("STL_PROG_CD",	ydDaoUtils.paraRecChkNull(getRecord,"STL_PROG_CD"));
		    		setRecord.setField("YD_MTL_ITEM",	ydDaoUtils.paraRecChkNull(getRecord,"YD_MTL_ITEM"));
		    		setRecord.setField("YD_ROUTE_GP",	ydDaoUtils.paraRecChkNull(getRecord,"YD_ROUTE_GP"));
		    		
		    		intRtnVal = this.Y3InsYdCarftmvmtl(setRecord) ;
	    		}
		    	inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	
	    	if(intGp == 1) {
    			//차량스케줄에 등록한다.
    			lngYD_EQP_WRK_WT = lngYD_EQP_WRK_WT + lngYD_MTL_WT;
    			intYD_EQP_WRK_SH = intYD_EQP_WRK_SH + intYD_MTL_SH;
    	    	//setRecord 초기화
    	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("YD_EQP_WRK_WT",       	"" + lngYD_EQP_WRK_WT);
	    		setRecord.setField("YD_EQP_WRK_SH",       	"" + intYD_EQP_WRK_SH);
	    		
	    		intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
	    	}
	    }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
		}//end of try~catch
		
    	return 1 ;
    	
    }//end of Y3SetYdCar()
    
    /**
     * 오퍼레이션명 : 작업예약 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3UpdYdWrkbook(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdWrkbookDao ydWrkbookDao   = new YdWrkbookDao(); 
    	
    	String szMsg = "";
    	String szMethodName = "Y3UpdYdWrkbook";
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydWrkbookDao.updYdWrkbook(msgRecord, intGp);

		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
	    }//end of try~catch
		
		return intRtnVal ;
    }//end of Y3UpdYdWrkbook
    
    /**
     * 오퍼레이션명 : 적치단 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3UpdYdStklyr (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	int intRtnVal = 0 ;
    	
    	String szMsg 		= "";
    	String szMethodName = "Y3UpdYdStklyr";
    	String szOperationName              = "A후판 적치단 Update";
        try{
        	
	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    				/*
    				 * 모음작업시에는 단이 존재하지 않을 수 있으므로 적치단이 존재하지 않더라도
    				 * 업무는 진행이 되도록 아래 부분을 수정
    				 * 수정자 : 임춘수
    				 * 수정일 : 2009.09.21
    				 */
    				szMsg="적치단이 존재하지 않습니다. - 모음작업 시 오류발생 가능 ";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				intRtnVal = 1;
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
			
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y3UpdYdStklyr> " + szMsg);
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y3UpdYdStklyr
    
    /**
     * 오퍼레이션명 : 진도코드갱신 Setting 
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y3SetProgCode (JDTORecord msgRecord)throws JDTOException  {
    	
    	
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//공통테이블 정보를 담기위한 값
    	//JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szCurrProgCd					= "";
    	String szBefoProgCd					= "";
    	
    	String szMsg						= "";
    	String szMethodName					= "Y3SetProgCode";
    	//재료품목 정의
    	//String szYdMtlItem					= "";
    	//재료종류별 번호
    	String szStlNo						= "";
    	int intRtnVal 						= 0 ;
    	String szRtnMsg						= null;
    	String szSLAB_WO_RT_CD				= null;
    	String szSCARFING_YN				= null;
    	String szSCARFING_DONE_YN			= null;
    	String szMILL_WO_EXN				= "";
    	String szSTL_APPEAR_GP				= null;
    	String szHCR_GP						= null;
    	String szPT_TB_COMM					= null;
    	//전전진도코드
    	String szBEFOBEFO_PROG_CD			= null;
    	//주문여재구분
    	String szORD_YEOJAE_GP  = 			null;
    	String szCURR_PROG_REG_DDTT  = 			null;
    	String szBEFO_PROG_REG_DDTT  = 			null;
    	String szBEFOBEFO_PROG_REG_DDTT  = 			null;
    	String szCURR_PROG_CD_REG_PGM  = 			null;
    	String szBEFO_PROG_CD_REG_PGM  = 			null;
    	String szBEFOBEFO_PROG_CD_REG_PGM  = 			null;
    	ymCommonDAO dao = ymCommonDAO.getInstance();
	    List FrtoProductList = null;
        try{
        	szStlNo 	= msgRecord.getFieldString("STL_NO") ;

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        	 * 업무기준 : C연주슬라브야드, 통합야드에 이송하차 완료 시 현재재료진도코드를 판단하여
        	 * 			주편/슬라브공통테이블에 업데이트 처리
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.21
        	 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	szRtnMsg = YdCommonUtils.getPtCommStock(szStlNo, getRecSet);
        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
        		szMsg = "[진도코드갱신 - " + szMethodName + "] 주편/슬라브공통테이블에서 재료[" + szStlNo + "] 조회 시 오류발생 : " + szRtnMsg;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
                throw new JDTOException(szMsg);
        	}
        	
        	getRecSet.first();
        	getRecord = getRecSet.getRecord() ;
        	
        	szPT_TB_COMM  		= ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");
        	//주문여재구분
        	szORD_YEOJAE_GP  	= ydDaoUtils.paraRecChkNull(getRecord, "ORD_YEOJAE_GP");
        	//슬라브지시행선코드
        	szSLAB_WO_RT_CD  	= ydDaoUtils.paraRecChkNull(getRecord, "SLAB_WO_RT_CD");
        	//스카핑여부
        	szSCARFING_YN  		= ydDaoUtils.paraRecChkNull(getRecord, "SCARFING_YN");
        	//스카핑완료여부
        	szSCARFING_DONE_YN  = ydDaoUtils.paraRecChkNull(getRecord, "SCARFING_DONE_YN");
        	//압연지시여부 - 슬라브에만 적용됨
        	if(szPT_TB_COMM.equals("S")) {
        		szMILL_WO_EXN  	= ydDaoUtils.paraRecChkNull(getRecord, "MILL_WO_EXN");
        	}
        	
        	szSTL_APPEAR_GP  	= ydDaoUtils.paraRecChkNull(getRecord, "STL_APPEAR_GP");
        	
        	szHCR_GP  			= ydDaoUtils.paraRecChkNull(getRecord, "HCR_GP");
        	
        	szMsg = "[진도코드갱신 - " + szMethodName + "] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + "[" + szStlNo + "], ";
        	szMsg += "주문여재구분[" + szORD_YEOJAE_GP + "], 슬라브지시행선코드[" + szSLAB_WO_RT_CD + "], 스카핑여부[" + szSCARFING_YN + "], 스카핑완료여부[" + szSCARFING_DONE_YN + "], 압연지시여부[" + szMILL_WO_EXN + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//현재진도코드
        	szCurrProgCd = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD");
        	//전 진도코드
        	szBefoProgCd = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD");
        	//전전진도코드 = 전 진도코드
        	szBEFOBEFO_PROG_CD = szBefoProgCd;
        	//전전진도코드 = 전진도코드
        	szBefoProgCd = szCurrProgCd;
        	
        	//현재진도코드등록Program
        	szCURR_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD_REG_PGM");
        	//전진도코드등록Program
        	szBEFO_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD_REG_PGM");
        	//전전진도코드등록Program
        	szBEFOBEFO_PROG_CD_REG_PGM = szBEFO_PROG_CD_REG_PGM;
        	szBEFO_PROG_CD_REG_PGM = szCURR_PROG_CD_REG_PGM;
        	szCURR_PROG_CD_REG_PGM = szMethodName;
        	
        	//현재진도등록일시
        	szCURR_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_REG_DDTT");
        	//전진도등록일시
        	szBEFO_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_REG_DDTT");
        	//전전진도등록일시
        	szBEFOBEFO_PROG_REG_DDTT = szBEFO_PROG_REG_DDTT;
        	szBEFO_PROG_REG_DDTT = szCURR_PROG_REG_DDTT;
        	szCURR_PROG_REG_DDTT = YdUtils.getCurDate("yyyyMMddHHmmss");
        	
        	//szCurrProgCd = YdCommonUtils.getCurrProgCd(szPT_TB_COMM, szSLAB_WO_RT_CD, szORD_YEOJAE_GP, szSCARFING_YN, szSCARFING_DONE_YN, szMILL_WO_EXN);
			//---------------------------------------------------------------------------------------------------------
			//---------------------------------------------------------------------------------------------------------
        	//공정 함수를 이용한 진도코드 가져오기
        	if(szPT_TB_COMM.equals("B")){
        		//주편 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcd";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szStlNo});
        	}else if (szPT_TB_COMM.equals("S")) {
        		//슬라브 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcdSlab";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szStlNo});
        	}       	

	    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);

	    	szCurrProgCd =StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
			ydUtils.putLog(szSessionName, szMethodName, "IHSF_PM_주편SLAB진도찾기==>>:"+szCurrProgCd, YdConstant.DEBUG);
			//---------------------------------------------------------------------------------------------------------
			//---------------------------------------------------------------------------------------------------------
        	szMsg = "[진도코드갱신 - " + szMethodName + "] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + " 재료[" + szStlNo + "] 수정 후 현재진도코드[" + szCurrProgCd + "], 전진도코드[" + szBefoProgCd + "], 전전진도코드[" + szBEFOBEFO_PROG_CD + "]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//전 진도코드 = 현재 진도코드,   전전진도코드 = 전진도코드
        	//전 진도코드등록일시 = 현재진도코드등록일시 , 전전진도코드등록일시 = 전진도코드등록일시
			//현재시간
        	setRecord.setField("CURR_PROG_CD", 					szCurrProgCd);
        	setRecord.setField("BEFO_PROG_CD", 					szBefoProgCd);
        	setRecord.setField("BEFOBEFO_PROG_CD", 				szBEFOBEFO_PROG_CD);
			setRecord.setField("CURR_PROG_REG_DDTT", 			szCURR_PROG_REG_DDTT);
			setRecord.setField("BEFO_PROG_REG_DDTT", 			szBEFO_PROG_REG_DDTT);
			setRecord.setField("BEFOBEFO_PROG_REG_DDTT", 		szBEFOBEFO_PROG_REG_DDTT);
			setRecord.setField("CURR_PROG_CD_REG_PGM", 			szCURR_PROG_CD_REG_PGM);
			setRecord.setField("BEFO_PROG_CD_REG_PGM", 			szBEFO_PROG_CD_REG_PGM);
			setRecord.setField("BEFOBEFO_PROG_CD_REG_PGM", 		szBEFOBEFO_PROG_CD_REG_PGM);
			setRecord.setField("FNL_REG_PGM", 					szMethodName);
			setRecord.setField("MODIFIER", 					    "YDSYSTEM");
			

        	//업데이트 항목 추가해야함 ///////////////////////////////////////////////////////
        	if(szPT_TB_COMM.equals("B")){
        		//주편 공통
        		setRecord.setField("MSLAB_NO", szStlNo);
        		intRtnVal = this.Y3UpdPtComm(setRecord,  2);
        	}else if (szPT_TB_COMM.equals("S")) {
        		//슬라브 공통
        		setRecord.setField("SLAB_NO", szStlNo);
        		intRtnVal = this.Y3UpdPtComm(setRecord,  0);
        	}
        	
        	//----------------------------------------------------------------------------------------------------------
			//	이송하차완료 시 각 재료의 목표야드, 목표동, 목표행선, 진도코드를 설정한다
			//	수정자 : 임춘수
			//	수정일 : 2010.01.06
			//----------------------------------------------------------------------------------------------------------
	        	
        	JDTORecord 	  recTemp 			= JDTORecordFactory.getInstance().create();
        	
        	recTemp.setField("PT_TB_COMM", 			szPT_TB_COMM);						//주편/슬라브구분
			recTemp.setField("STL_NO", 				szStlNo);							//재료번호
			recTemp.setField("SLAB_WO_RT_CD", 		szSLAB_WO_RT_CD);					//슬라브지시행선코드
			recTemp.setField("ORD_YEOJAE_GP", 		szORD_YEOJAE_GP);					//주여구분
			recTemp.setField("SCARFING_YN", 		szSCARFING_YN);						//스카핑여부
			recTemp.setField("SCARFING_DONE_YN", 	szSCARFING_DONE_YN);				//스카핑완료여부
			recTemp.setField("MILL_WO_EXN", 		szMILL_WO_EXN);						//압연지시
			recTemp.setField("YD_GP", 				YdConstant.YD_GP_A_PLATE_SLAB_YARD);		//야드구분
			recTemp.setField("STL_APPEAR_GP", 		szSTL_APPEAR_GP);					//재료외형구분
			recTemp.setField("HCR_GP", 				szHCR_GP);							//HCR구분
			
			String szRetunMsg = YdCommonUtils.uptStockCodeMapping(recTemp);
			
			szMsg="[진도코드갱신 - "+szMethodName+"] 재료["+szStlNo+"]의 속성을 수정 완료 - 메세지 : " + szRetunMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------------------------------------
        	
        }catch(Exception e){
        	szMsg = "[진도코드갱신 - " + szMethodName + "] 오류발생 : " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y3SetProgCode()
    
    /**
     * 오퍼레이션명 : 저장위치 Setting 
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int Y3setYdStrLoc (JDTORecord msgRecord)throws JDTOException{
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
    	String szMethodName					= "Y3setYdStrLoc";
    	//재료품목 정의
    	String szYdMtlItem					= "";
    	//차량사용구분
    	String szYD_CAR_USE_GP				= null;
    	
    	//크레인스케줄의 정보				현재저장위치를 생성하기 위해...
    	String szYdGp						= "";
    	String szYdBayGp					= "";
    	String szYdEqpId					= "";
    	String szYdStkColNo					= "";
    	String szYdStkBedNo					= "";
    	String szYdStkLyrNo					= "";
    	String szYdDnWrLoc                  = "";
    	String szYD_GP						= null;
    	String szSTL_NO						= null;
    	String szRtnMsg						= null;
    	String szLogMsg						= null;
    	String szPT_TB_COMM					= null;
    	
    	int intRtnVal 						= 0 ;
        
        try{
            szMsg = "저장위치 Setting ( Y3setYdStrLoc ) ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
            /*
            szYdMtlItem = ydDaoUtils.paraRecChkNull(msgRecord,"YD_MTL_ITEM"); 
			if(szYdMtlItem.length() > 1){
				szYdMtlItem = szYdMtlItem.substring(0, 1);
			}
			*/
			//--------------------------------------------------------------------------------------------------------
			//	권하실적위치가 차량인 경우 차량사용구분이 넘겨진다.
			//	수정자 : 임춘수
			//	수정일 : 2009.12.17
			//--------------------------------------------------------------------------------------------------------
			szYD_CAR_USE_GP		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_CAR_USE_GP") ;
			//--------------------------------------------------------------------------------------------------------

			/*
        	 * 공통테이블의 재료 정보를 조회해서 업데이트를 처리
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.15
        	 */
        	szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "MSLAB_NO");
        	szRtnMsg = YdCommonUtils.getPtCommStock(szSTL_NO, getRecSet);
        	
        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
        		szLogMsg = "[저장위치 Setting - Y1setYdStrLoc]재료[" + szSTL_NO + "]를 공통테이블에서 조회 시 오류발생";
                ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
                throw new JDTOException(szLogMsg);
        	}
        	
        	getRecSet.absolute(1);
        	getRecord      	= JDTORecordFactory.getInstance().create();
        	getRecord 	   	= getRecSet.getRecord();
        	szYdStrLoc 	   	= ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC").trim();
        	szYdStrLocHis1 	= ydDaoUtils.paraRecChkNull(getRecord,"YD_STR_LOC_HIS1").trim();
        	szPT_TB_COMM 	= ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");
        	
        	szYdGp 		   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").trim(); 
        	szYdBayGp 	   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
        	szYdEqpId 	   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP").trim(); 
        	szYdStkColNo   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_NO").trim(); 
        	szYdStkBedNo   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO").trim(); 
        	szYdStkLyrNo   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_LYR_NO").trim();
        	
        	setRecord = JDTORecordFactory.getInstance().create();
        	//--------------------------------------------------------------------------------------------------------
        	//	권하실적위치가 차상이고 출하차량이면 공통테이블의 야드구분을 *로 등록
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.17
        	//--------------------------------------------------------------------------------------------------------
        	if(szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) ) {
        		setRecord.setField("YD_GP",         "*");
        	}else{
        		setRecord.setField("YD_GP",         szYdGp);
        	}
        	//--------------------------------------------------------------------------------------------------------
        	setRecord.setField("YD_BAY_GP",     szYdBayGp);
        	setRecord.setField("YD_EQP_GP",     szYdEqpId);
        	setRecord.setField("YD_STK_COL_NO", szYdStkColNo);
        	setRecord.setField("YD_STK_BED_NO", szYdStkBedNo);
        	setRecord.setField("YD_STK_LYR_NO", szYdStkLyrNo);
        	setRecord.setField("FNL_REG_PGM",   "Y1setYdStrLoc");
        	/*
        	 * 권하위치의 야드구분이 다른 경우에는 입고일자와 입고시각을 업데이트 처리
        	 * 주편공통, 슬라브공통 : 입고일자, 입고시각
        	 * PLATE공통 : 입고일자
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.14
        	 */
        	//공통테이블에 저장되어 있는 야드구분
        	szYD_GP	= ydDaoUtils.paraRecChkNull(getRecord,"YD_GP");
        	if( !szYD_GP.equals(szYdGp) ) {
        		
        		String szCurDateTime 	= YdUtils.getCurDate("yyyyMMddHHmmss");
        		String szRECEIPT_DATE 	= szCurDateTime.substring(0, 8);
        		String RECEIPT_TIME 	= szCurDateTime.substring(8);
        		
        		setRecord.setField("RECEIPT_DATE", 	szRECEIPT_DATE);	//입고일자
        		setRecord.setField("RECEIPT_TIME", 	RECEIPT_TIME);		//입고시각
        	}
        	setRecord.setField("MODIFIER",      "YDSYSTEM");
        	
        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
	        if(szYdStrLoc.equals("")){
	        	setRecord.setField("YD_STR_LOC_HIS1", 	"");
	        }else{
	        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc);
	        }
	        
	        if(szYdStrLocHis1.equals("")){
	        	setRecord.setField("YD_STR_LOC_HIS2", 	"");
	        }else{
	        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1);
	        }
        	
        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우
	        szMsg = "전 저장위치 : " + szYdStrLoc + " , 전전 저장위치 : " + szYdStrLocHis1 + ", 권하실적 단 정보 : " + szYdStkLyrNo;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	if(szPT_TB_COMM.equals("B")){
        		
        		setRecord.setField("MSLAB_NO",   msgRecord.getFieldString("MSLAB_NO")); 
        		setRecord.setField("YD_STR_LOC", szYdGp + szYdBayGp + szYdEqpId +
        										 szYdStkColNo + szYdStkBedNo + szYdStkLyrNo.substring(1,3));

        		intRtnVal = this.updY3YdStock(setRecord, 2);
       		
        	}else if (szPT_TB_COMM.equals("S")) {
            	
        		setRecord.setField("SLAB_NO",    msgRecord.getFieldString("SLAB_NO")); 
        		setRecord.setField("YD_STR_LOC",  szYdGp + szYdBayGp + szYdEqpId + 
        				                          szYdStkColNo + szYdStkBedNo + szYdStkLyrNo.substring(1,3));
        		//슬라브 공통 업데이트
        		intRtnVal = this.updY3YdStock(setRecord,  0);
         	}
        	
        }catch(Exception e){
			throw new JDTOException(e.getLocalizedMessage());
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y3setYdStrLoc()
    
    /**
     * 오퍼레이션명 : 재료공통 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed
     * @throws 
     */
    public int updY3YdStock (JDTORecord msgRecord, int intGp)throws JDTOException{
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "updY3YdStock";
        String szOperationName              = "A후판 재료공통 Update";
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
	            return intRtnVal ;
	        }
        }catch(Exception e){
        	throw new JDTOException(e.getLocalizedMessage());
        }//end of try~catch
        
        return intRtnVal ;
    	
    }//end of updY3YdStock()
    
    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *  
     * @param msgRecord, outRecset, intGp(1:상하차)
     * @return intRtnVal
     * @throws 
     */
    public int Y3GetYdCarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	String szOperationName              = "A후판 차량 스케줄 Select";
    	String szMethodName = "Y3GetYdCarsch";
    	String szMsg        = "";
    	
        try{
        	
        	intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);
	        
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="[" + szOperationName + "] data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="[" + szOperationName + "] parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				if (intRtnVal <= 0) return intRtnVal = -2;
			}
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y3GetYdCarsch
    
    /**
     * 오퍼레이션명 : 차량 이송재료 Update
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y3UpdCarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
    	
    	String szMethodName = "Y3UpdCarftmvmtl";
    	String szMsg        = "";
    	String szOperationName              = "A후판 차량 이송재료 Update";
        try{
        	
            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found!!";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
	    }//end of try~catch	
		
		return intRtnVal ;
		
    }//end of Y3UpdCarftmvmtl
    
    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *  
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y3InsYdCarftmvmtl(JDTORecord msgRecord)throws JDTOException{
    	YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
    	
    	int intRtnVal = 0 ;
    	String szOperationName              = "A후판 차량이송재료 Insert";
    	String szMethodName = "Y3InsYdCarftmvmtl";
    	String szMsg        = "";
        
        try{
        	intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
    		if(intRtnVal == -2) {
				szMsg="[" + szOperationName + "] parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
    		}
        	
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
        }//end of try~catch
        
        return intRtnVal = 1;
    }//end of Y3InsYdCarftmvmtl
    
    /**
     * 오퍼레이션명 : 재료공통 Update
     *  
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws 
     */
    public int Y3UpdPtComm (JDTORecord msgRecord, int intGp)throws JDTOException{
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "Y3UpdPtComm";
        String szOperationName              = "A후판 재료공통 Update";
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
	            return intRtnVal ;
	        }
        }catch(Exception e){
        	szMsg = "[Y3UpdPtComm - 재료진도코드 변경 ]오류발생 - " + e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new JDTOException(szMsg);
        }//end of try~catch
        
        return intRtnVal ;
    	
    }//end of Y3UpdPtComm()
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 오퍼레이션명 : A후판 비상조업실적등록 (Y3YDL010)
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY3CrnEmgPtopWr(JDTORecord msgRecord)throws JDTOException  {

    	
    	//업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
        //적치단클리어시 업데이트 항목
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();

        int intRtnVal 					= 0 ;
        int intRowsize					= 0 ;
        
        String szMsg              		= "";
        String szMethodName       		= "procY3CrnEmgPtopWr";
        String szOperationName              = "A후판비상조업실적등록";
        
        
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        	return;
        }
        
        	
        
        
        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

       
        
        try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[후판슬라브야드] 비상조업실적등록 수신";
			ydUtils.putLogMsg("D", "yd_monitorD", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);



			
			
	        //파라미터 check
        	//새로 만들어야 하는 부분
	        intRtnVal = this.Y3EmerOperParamCheck(msgRecord, getParamRecord) ;
	        if(intRtnVal == -1) {
                szMsg = "파라미터 Check중 Error	: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        

	        setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
	        setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
	        setCrnschRecord.setField("MODIFIER",              "SYSTEM") ;
	        setCrnschRecord.setField("YD_EQP_ID",             getParamRecord.getFieldString("YD_EQP_ID")) ;
	        setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
	        setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;
	        setCrnschRecord.setField("YD_DN_WR_LOC",          getParamRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setCrnschRecord.setField("YD_DN_WR_LAYER",        getParamRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        
	        
	        //크레인 스케줄의 Insert하기위해 스케줄의 항목의 값을 Setting하고 업데이트한다.
	        intRtnVal = this.Y3InsYdCrnsch(setCrnschRecord) ;
	        if (intRtnVal == 2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

        
	        //크레인작업재료 Insert
			setRecord.setField("YD_CRN_SCH_ID", getParamRecord.getFieldString("YD_CRN_SCH_ID")) ;
			setRecord.setField("STL_NO", 		getParamRecord.getFieldString("STL_NO")) ;
			
	        intRtnVal = this.Y3InsYdCrnWrkMtl(setRecord) ;
	        if (intRtnVal == 2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }



	        //from위치정리
	        if(getParamRecord.getFieldString("STL_NO").equals("") || getParamRecord.getFieldString("STL_NO") == null) {
                szMsg = "'STL_NO' Data Error	: 재료번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }	        


	        //대상 데이터 SELECT			재료번호로 적치단 조회
	        intRtnVal = this.Y3GetYdStklyr(getParamRecord, getRecSet,3);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

	        
	        intRowsize = getRecSet.size() ;
	        
	        getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(intRowsize == 0){
	            szMsg = "적치단에 등록된 재료번호가 없습니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        }
	        getRecord = getRecSet.getRecord();
		        
	        if(intRowsize > 0){

		        for(int i=0; i<intRowsize; i++) {
		        //클리어셋팅
		        	setRecord = JDTORecordFactory.getInstance().create();
		        	setRecord.setField("YD_STK_COL_GP", 		getRecord.getFieldString("YD_STK_COL_GP")) ;
		        	setRecord.setField("YD_STK_BED_NO", 		getRecord.getFieldString("YD_STK_BED_NO")) ;
		        	setRecord.setField("YD_STK_LYR_NO", 		getRecord.getFieldString("YD_STK_LYR_NO")) ;
		        	setRecord.setField("STL_NO",      		    "") ;
		        	setRecord.setField("MODIFIER",      		"SYSTEM") ;
		        	setRecord.setField("YD_STK_LYR_ACT_STAT", 	"E") ;
		        	setRecord.setField("YD_STK_LYR_MTL_STAT", 	"E") ;

//==========================================================================================                    
//                  기준이기 때문에 클리어 되면 안됨
//                  2009.09.25 권오창
//                    
//		        	setRecord.setField("YD_STK_LYR_XAXIS", 		"") ;
//		        	setRecord.setField("YD_STK_LYR_YAXIS", 		"") ;
//		        	setRecord.setField("YD_STK_LYR_ZAXIS", 		"") ;
//==========================================================================================                    

		        	//적치단 업데이트
		        	intRtnVal = this.Y3UpdYdStklyr(setRecord, 0) ;
			        switch (intRtnVal) {
			        	case 0	:
			                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
			        	case -1	:
			                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return ;
			        	case -2	:
			                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return ;
			        	case -3	:
			                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return ;
			        }	        
		        
			        getRecSet.next();
			        getRecord = getRecSet.getRecord();
		        }//end of for
	        }//end of if
	        
	        
	        
	        //적치단에 재료의 실적위치에 실적정보를 등록한다.
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_STK_COL_GP", 		getParamRecord.getFieldString("YD_DN_WR_LOC").substring(0,6)) ;
	        setRecord.setField("YD_STK_BED_NO", 		getParamRecord.getFieldString("YD_DN_WR_LOC").substring(6,8)) ;
	        setRecord.setField("YD_STK_LYR_NO", 		getParamRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        setRecord.setField("MODIFIER", 				"SYSTEM") ;
	        setRecord.setField("STL_NO", 				getParamRecord.getFieldString("STL_NO")) ;
	        setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C") ;
	        intRtnVal = this.Y3UpdYdStklyr(setRecord, 0) ;
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	        	case -1	:
	                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }
	        
	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 업무 : A후판슬라브야드L2 크레인작업실적응답 전문 전송(YDY3L005)
	         * 수정자 : 임춘수
	         * 일자 : 2009.06.19
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("MSG_ID"        , "YDY3L005");
	        setRecord.setField("YD_EQP_ID"     , getParamRecord.getFieldString("YD_EQP_ID"));
	        setRecord.setField("YD_SCH_CD",     getParamRecord.getFieldString("YD_SCH_CD")) ;
	        setRecord.setField("YD_CRN_SCH_ID", getParamRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_EMG_PTOP);				//U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        setRecord.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);			//야드L3처리결과코드
			ydDelegate.sendMsg(setRecord);
			szMsg = "[A후판비상조업실적등록]A후판슬라브야드L2 크레인작업실적응답[YDY3L005] 전송 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        
	            
	            
	        szMsg="A후판 크레인 비상조업  실적 등록 완료";
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	    }catch(Exception e) {
	    	System.out.println("Error :  "+ e.getLocalizedMessage());
	    }//end of try~catch
	    
    }// end of procY3CrnEmgPtopWr()
  
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 비상조업실적등록 파라미터 체크
     *  
     * @param  ● msgRecord, outRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3EmerOperParamCheck (JDTORecord msgRecord, JDTORecord outRecord) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "";
        String szMethodName                 = "Y3EmerOperParamCheck";
        int intRtnVal = 0 ;
        
    	try{
            
    		setRecord.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
			setRecord.setField("YD_CRN_SCH_ID"			, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
			setRecord.setField("YD_SCH_CD"        		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
			setRecord.setField("STL_NO"          		, ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")) ;
			setRecord.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
			setRecord.setField("YD_UP_WR_LOC"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
			setRecord.setField("YD_UP_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;
			setRecord.setField("YD_DN_WR_LOC"         	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
			setRecord.setField("YD_DN_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;

    		
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal;
        }//end of try~catch
        
        intRtnVal = 1 ;
        return intRtnVal;
        
    }//end of Y3EmerOperParamCheck()
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Insert
     *  
     * @param  ● msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3InsYdCrnsch(JDTORecord msgRecord) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	String szMsg        = "";
    	String szMethodName = "Y3InsYdCrnsch";
    	
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnschDao.insYdCrnsch(msgRecord);		       
			if(intRtnVal == -2) {
				szMsg = "크레인 스케줄 등록중 Error!! ErrorCode: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y3InsYdCrnsch
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인작업재료 Insert
     *  
     * @param  ● msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3InsYdCrnWrkMtl(JDTORecord msgRecord) throws JDTOException {
    	YdCrnWrkMtlDao ydCrnwrkmtlDao = new YdCrnWrkMtlDao();

    	String szMsg        = "";
    	String szMethodName = "Y3InsYdCrnWrkMtl";
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnwrkmtlDao.insYdCrnwrkmtl(msgRecord);		        
			if(intRtnVal == -2) {
				szMsg = "크레인 작업재료 삽입 중 Error!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
			}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y3InsYdCrnWrkMtl
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y3GetYdStklyr (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szMsg = "";
    	String szMethodName = ""; 
    	int intRtnVal = 0 ;
        
        try{

	    	intRtnVal = ydStklyrDao.getYdStklyr(msgRecord, getRecSet, intGp);  
	    	
	    	outRecSet.addAll(getRecSet)  ; 
	        if (intRtnVal <= 0) {
	        	return intRtnVal;
	        }
	        
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y3GetYdStklyr
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
	/**
     * 오퍼레이션명 : 후판제품 권하실적처리
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY4CrnUdWr(JDTORecord msgRecord) throws DAOException {
		EJBConnector ejbConn 		= null;
		String szMethodName         = "procY4CrnUdWr";
		String szRtnMsg             = "";		
		String szLogMsg             = "";
		String szEjbJndiName 	= "CraneLdHdSeEJB";
		String szEjbMethod 		= "procY4CrnWrkOrdReq";
		JDTORecord 		outRecord 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord2 	= JDTORecordFactory.getInstance().create();
		

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "Y8크레인권하실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
		
		try {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procY4CrnUdWrTX Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
			msgRecord.setField("LOG_ID", logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			
			//후판제품 권하실적처리
			
			ejbConn = new EJBConnector("default", "CraneUdHdSeEJB", this);

			outRecord =(JDTORecord)ejbConn.trx("procY4CrnUdWrTX", new Class[] { JDTORecord.class}, new Object[] { msgRecord });
			
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), YdConstant.RETN_CD_FAILURE);
			
			if( sRTN_CD.equals(YdConstant.RETN_CD_FAILURE) ) {			//성공
				szLogMsg = "권하실적처리 오류 (" + szRtnMsg + ")";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, 1);
				ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, 1, logId);
				return YdConstant.RETN_CD_FAILURE;
			}
			  
			boolean isAutoBPreSchFlag=false;  //1후판  B동 작업 선 생성(북아웃도착전 미리 생성) 여부
			String sApplyYnPI =ydPICommDAO.ApplyYnPI("",szMethodName,"APPPI2","T","*");
			if(sApplyYnPI.equals("Y")){
				//1후판 B동일 경우, 동일 북아웃존 대상, 입고대기존 통과 제품중 아직 북아웃존 도착 못한 제품 있으면 미리 스케줄 생성해줌. 2023.04.25 임진후 기사 요청.
				szLogMsg = "[후판제품 권하실적처리] 1후판 B동. 동일 북아웃존 스케줄 생성 대상 존재 여부 조회";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				YdPlateCommDAO 	TMPcmmDao 		= new YdPlateCommDAO();
				JDTORecord TMPparams = JDTORecordFactory.getInstance().create();
				JDTORecordSet TMPresults = JDTORecordFactory.getInstance().createRecordSet("plateTmp");
				TMPparams.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID")); 
	        	if( TMPcmmDao.select(TMPparams, TMPresults, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.chkSameBookoutZoneStlNoForSchId") > 0){
	        		
	        		String szPL_MTL_NO=	TMPresults.getRecord(0).getFieldString("STL_NO");
	        		String szPL_TRCK_ZONE_NO=	TMPresults.getRecord(0).getFieldString("YD_BOOK_OUT_LOC");

	    			szLogMsg = "[후판제품 권하실적처리]북아웃 작업지시 선호출"+"- 메소드 콜 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    			ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    			
	    			TMPparams.setField("PL_MTL_NO", szPL_MTL_NO);
	    			TMPparams.setField("PL_TRCK_ZONE_NO", szPL_TRCK_ZONE_NO);
	    			TMPparams.setField("JMS_TC_CD",	"P2YDL002");
	    			
	    			szLogMsg = "[후판제품 권하실적처리]북아웃 작업지시 선호출 "+"szPL_MTL_NO:"+szPL_MTL_NO+" szPL_TRCK_ZONE_NO:"+szPL_TRCK_ZONE_NO;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    			ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    			
	    			szLogMsg = "outRecord2:"+outRecord2;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    			ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    			
	    			ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);
	    			outRecord2 =(JDTORecord)ejbConn.trx("procP2BookOutReq", new Class[] { JDTORecord.class}, new Object[] { TMPparams });
	    			
	    			szLogMsg = "outRecord2:"+outRecord2;  //이값이 NULL임
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    			ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    			
	    			szLogMsg = "[후판제품 권하실적처리]북아웃 작업지시 선호출"+"- 메소드 콜 종료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    			ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	    			
	    			isAutoBPreSchFlag=true;//여기서 미리 북아웃 생성 호출했으면 별도 작업지시 호출 안함.(스케줄MAIN 호출하므로(스케줄 MAIN 로직 내 작업지시 호출 로직 있음))
	        	}
	        	else{
	        		szLogMsg = "[후판제품 권하실적처리]북아웃 작업지시 선호출 대상 미검출:";  
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	    			ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	        	}
			}
			
			if(!isAutoBPreSchFlag){
				//크레인 작업지시 호출
				szLogMsg = "[후판제품 권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				
	
	//SJH040002
	//			ejbConn = new EJBConnector("default", szEjbJndiName, this);			
	//			szRtnMsg = (String)ejbConn.trx(szEjbMethod, new  Class[] { JDTORecord.class }, new Object[] { outRecord });
		
				//B동 입고 작업 권하완료시, 다음판 정보 스케줄 선 생성(같은 북아웃위치일 경우만)
	
				String sMSG_ID	= StringHelper.evl(outRecord.getFieldString("MSG_ID"),"");
				szLogMsg = "sMSG_ID:"+sMSG_ID;  
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);			
				ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);			
				if (sMSG_ID.equals("YDYDJ642")) {			
					//크레인작업지시 송신
					ydDelegate.sendMsg(outRecord);
					szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				}	
				
				
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || 
					szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || 
					szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
					szLogMsg = "[후판제품 권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
				}else{
					szLogMsg = "[후판제품 권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
					return YdConstant.RETN_CD_FAILURE;
				}			
			}
			
			// 2021. 08. 30 [운영반영예정]
			// 이명운책임요청으로 입고가적베드의 적치된 재료를 원위치로 이적작업생성(전문발송)
			String sYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if( !"".equals(sYD_EQP_ID)){
				YdPlateCommDAO 	cmmDao 		= new YdPlateCommDAO();
				JDTORecord params = JDTORecordFactory.getInstance().create();
				JDTORecordSet results = JDTORecordFactory.getInstance().createRecordSet("plateTmp");
	        	params.setField("YD_EQP_ID", sYD_EQP_ID); 

				szLogMsg = "[후판제품 권하실적처리] 입고가적베드 자동이적대상 여부 확인";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	        	if( cmmDao.select(params, results, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.chkRcptTempToPlanStrMoveDong") < 1){
	        		
					szLogMsg = "[후판제품 권하실적처리] 입고가적베드 자동이적 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
					
					JDTORecord send_YDYDJ557 = JDTORecordFactory.getInstance().create();
					send_YDYDJ557.setField("JMS_TC_CD", "YDYDJ557");
					send_YDYDJ557.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));
					ydDelegate.sendMsg(send_YDYDJ557);
					
					szLogMsg = "[후판제품 권하실적처리] 입고가적베드 자동이적 전문발송완료[YDYDJ557]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);
	        	}
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szSessionName + e.getMessage(), e);
		}
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
		szLogMsg = "Y8크레인권하실적 처리(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
		
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procY4CrnUdWr
	
	
    /**
     * 오퍼레이션명 : 후판제품 권하실적처리 (Y4YDL009)
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws JDTOException
     * @ejb.transaction type="RequiresNew"
     */         
    public JDTORecord procY4CrnUdWrTX(JDTORecord msgRecord)throws JDTOException  {
    	
    	YdDelegate      ydDelegate      = new YdDelegate();
    	YdEqpDao        ydEqpDao        = new YdEqpDao();
    	YdStockDao      ydStockDao      = new YdStockDao();
    	YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
    	YdCrnWrkMtlDao  ydCrnWrkMtlDao  = new YdCrnWrkMtlDao(); 
    	YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();
    	YdStkColDao     ydStkColDao     = new YdStkColDao();
    	YdPlateCommDAO  commDao 		= new YdPlateCommDAO();
    	
        int intRtnVal = 0;
        String szOperationName              = "후판제품권하실적처리";
        
        //DATA SETTING시 사용
        JDTORecord setRecord 				= JDTORecordFactory.getInstance().create();
        
        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord                = JDTORecordFactory.getInstance().create(); ;
        
        //작업예약 업데이트 항목
        JDTORecord bookrecord               = JDTORecordFactory.getInstance().create();
        
        //크레인 스케줄 야드작업진행상태 비교		 (작업예약이 끝났는지 확인하기위해 카운터 값을 가져옴)
        JDTORecord outRecord                = JDTORecordFactory.getInstance().create();         
        
        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 			= JDTORecordFactory.getInstance().create();
       
        JDTORecord recInTemp                = null;
        JDTORecord recInTemp1               = null;
        JDTORecord recOutTemp               = null;
        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet getRecSetWb			= null;
        
        JDTORecordSet rsResult              = null;
        
        String szMsg                        = "";
        String szMethodName                 = "procY4CrnUdWrTX";
        
        //크레인 XYZ축 			파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
        String szYD_CRN_XAXIS     			= "";
        String szYD_CRN_YAXIS     			= "";
        String szYD_CRN_ZAXIS     			= "";
        //WBOOK_ID				작업예약 완료 처리시 사용
        String szYdWbookId              	= "";
        String szCrnSchId                   = "";
        
        String szYD_UP_WR_LOC               = "";
        String szYD_SCH_CD                  = "";
        String szYD_EQP_ID                  = "";
        String szYD_DN_WR_LOC               = "";
        String szYD_DN_WR_LAYER				= "";
        String szYD_CAR_SCH_ID				= "";
        String szYD_WRK_PROG_STAT			= "";
        String szSTL_NO						= null;
        String szBOOK_IN                    = "";
        int intYD_EQP_WRK_SH				= 0;
        
		//야드구분
        String szYdGp					= ""; //--2013.02.07 추가 (3기)
        double dYD_MTL_L				= 0;
        
        //AT000 물류시스템 개선 2022.10.27
        YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
        double dblYD_EQP_WRK_W       = 0; 
        String szYD_TO_LOC_GUIDE1          = ""; 
        String szYD_SCH_PRIOR              = ""; 
        
        //-----------------------------------------------------------------------------------------
	    //실제Lyr를 검사하여 처리하기 위해 필요한 변수들
	    JDTORecordSet getLyrSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
	    JDTORecord recInPara = null;
	      
        String szREAL_TOP_LYR               = "";
	    int    intRealTopLyr                = 0;
	    int    intYdDnWrLayer               = 0;
	    int    rowsize                      = 0;
        //-----------------------------------------------------------------------------------------
        
	    int iSP_YD_SHIPSEL_001_callCnt      = 0;
	    
        //EJB CALL or JMS CALL
        String szIS_EJB_CALL = null;
        String szLogMsg = null;
        
        //완산BED 설정 변경
        String szYD_STK_COL_GP = "";
        String szYD_STK_BED_NO = "";
     // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
		boolean isSendToEaiY9           = false;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "Y8크레인권하실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.INFO, logId);

// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
		
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if( szRcvTcCode==null || szRcvTcCode.equals("") ){
            szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            m_ctx.setRollbackOnly();
            throw new JDTOException("<procY1CrnUdWr> " + szMsg);
        }
        
        if(bDebugFlag){
            szMsg="["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        }
        
        try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "["+szOperationName+"] 권하실적처리 수신";
			ydUtils.putLogMsg("K", "yd_monitorK", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//PIDEV			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "후판 권하실적처리 수신", "APPPI0", "T", "*");

        	szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
        	
        	//-------------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------------
	        intRtnVal = this.Y4ParamCheck(msgRecord, getCrnschRecord, 1) ;
	        
	        szCrnSchId 			= getCrnschRecord.getFieldString("YD_CRN_SCH_ID");
	        szYD_EQP_ID 		= getCrnschRecord.getFieldString("YD_EQP_ID");
	        szYD_SCH_CD 		= getCrnschRecord.getFieldString("YD_SCH_CD");
	        szYD_DN_WR_LOC		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC");
	        szYD_DN_WR_LAYER	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER");
	        szYD_WRK_PROG_STAT 	= getCrnschRecord.getFieldString("YD_WRK_PROG_STAT");
	        
	        //야드구분을 설비ID 첫자리로 구분한다.
        	if(szYD_EQP_ID.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) { //--2013.02.07 추가 (3기)
            	szYdGp	= YdConstant.YD_GP_PLATE2_GDS_YARD; //2후판제품창고 'T'
        	} else {
        		szYdGp	= YdConstant.YD_GP_PLATE_GDS_YARD;  //1후판제품창고 'K'
        	}
        	
	        if( szCrnSchId.equals("") ) {
                szMsg = "["+szOperationName+"] 'YD_CRN_SCH_ID' Data Error	: 크레인스케줄ID가 없습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY4CrnUdWr> " + szMsg);
	        }
	        
	        //파라미터 레코드 편집
	        setRecord.setField("YD_CRN_SCH_ID",       	szCrnSchId);
	        setRecord.setField("YD_SCH_CD",           	szYD_SCH_CD);
	        setRecord.setField("YD_DN_WR_LOC",        	getCrnschRecord.getFieldString("YD_DN_WR_LOC"));
	        setRecord.setField("YD_DN_WR_LAYER",      	getCrnschRecord.getFieldString("YD_DN_WR_LAYER"));

	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	        
	        
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// setRecord 에 logId 추가
setRecord.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

            intRtnVal = this.Y4UpdYdCrnsch(setRecord, 0);
            
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			//-------------------------------------------------------------------------------------------------------------------
            
            //-------------------------------------------------------------------------------------------------------------------
	        //	크레인스케줄 작업재료 조회
            //-------------------------------------------------------------------------------------------------------------------
            szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            
////////////////////////////////////////////////////////////////////////////////////////
//2024.09.09 로그 개선  START
//setRecord 에 logId 추가
setRecord.setField("LOG_ID", logId);  // 전문에 있는 logId
//2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

	        intRtnVal = this.Y4GetYdCrnsch(setRecord, getRecSet,3);
	       
	        if(intRtnVal < 0){
	        	m_ctx.setRollbackOnly();
            	throw new JDTOException("<procY4CrnUdWr> Y4GetYdCrnsch ERROR CODE =" + intRtnVal);
            }
	        

	        
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();
	        
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 완료 - 대상재 건수 : " + getRecSet.size();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            
            szYD_UP_WR_LOC 		= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
	        //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szYdWbookId 		= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
	        //권하실적위치
	        szYD_DN_WR_LOC 		= ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC");
	        //권하실적단
	        szYD_DN_WR_LAYER	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER");
	        szYD_TO_LOC_GUIDE1  = ydDaoUtils.paraRecChkNull(getRecord, "YD_TO_LOC_GUIDE");  // AT000 물류시스템 개선 2022.10.27 최종 적치bed
			//제품길이
			dYD_MTL_L 			= ydDaoUtils.paraRecChkNullDouble(getRecord, "YD_MTL_L"); 	     
			dblYD_EQP_WRK_W     = ydDaoUtils.paraRecChkNullDouble(getRecord, "YD_EQP_WRK_MAX_W"); 	// AT000 제품 폭 최대폭  
			szYD_SCH_PRIOR      = ydDaoUtils.paraRecChkNull(getRecord, "YD_SCH_PRIOR"); 	// AT000 우선순위
	        //-------------------------------------------------------------------------------------------------------------------
            
	        //-------------------------------------------------------------------------------------------------------------------
	        //	작업진행상태 확인 후 상태가 맞지 않으면 롤백 처리
            //-------------------------------------------------------------------------------------------------------------------
	        if ((!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("2"))&& 
	        	(!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("3"))) {
	        	szMsg = "["+szOperationName+"] 작업진행상태["+ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT")+"]가   권상('2') 또는 권하대기('3')이 아닙니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
	            m_ctx.setRollbackOnly();
	            //throw new JDTOException("<procY4CrnUdWr> " + szMsg);
	            
	            recInTemp = JDTORecordFactory.getInstance().create();
	            return recInTemp;
	        }
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하지시위치와 권하실적위치가 다르면 권하지시위치의 맵정보를 Clear시킴
	        //-------------------------------------------------------------------------------------------------------------------
	        if (!getRecord.getFieldString("YD_DN_WR_LOC").equals(getRecord.getFieldString("YD_DN_WO_LOC"))) {
	        	szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	
                
	        	//크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
	        	if(szYD_CRN_XAXIS.equals("") && szYD_CRN_YAXIS.equals("") && szYD_CRN_ZAXIS.equals("")) {
	        		szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_XAXIS");
	        		szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_YAXIS");
	        		szYD_CRN_ZAXIS = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC_ZAXIS");
	        	}
	        	
	        	intRtnVal = this.Y4ClearYdStklyr(getRecSet,1) ;
	        	
	        	if(intRtnVal < 0){
	        		m_ctx.setRollbackOnly();
	            	throw new JDTOException("<procY4CrnUdWr> Y4ClearYdStklyr ERROR CODE =" + intRtnVal);
	            }
	        	szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	        }
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하실적LYR와 실제적치단의 최상위 위치가 다르면 권하실적LYR를 변경하고 적치단 정보를 수정한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        szYD_STK_COL_GP = szYD_DN_WR_LOC.substring(0, 6);
	        szYD_STK_BED_NO = szYD_DN_WR_LOC.substring(6, 8);
	        intYdDnWrLayer 	= Integer.parseInt(szYD_DN_WR_LAYER);
	        	                
	        recInPara	= JDTORecordFactory.getInstance().create();
	        recInPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	        recInPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
	        
	        intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, getLyrSet, 98);
	        
	        if(intRtnVal < 0){
	        	m_ctx.setRollbackOnly();
            	throw new JDTOException("<procY4CrnUdWr> getYdStklyr ERROR CODE =" + intRtnVal);
            }
	        
	        getLyrSet.first();
	        recOutTemp = getLyrSet.getRecord();
	        
	        szREAL_TOP_LYR 	= ydDaoUtils.paraRecChkNull(recOutTemp,"REAL_TOP_LYR");
	        intRealTopLyr 	= Integer.parseInt(szREAL_TOP_LYR);
	        
	        if (intYdDnWrLayer != intRealTopLyr) {
	        	
	        	szMsg = "[" + szOperationName + "] 실적적치단[" + intYdDnWrLayer + "]과 실재야드적치단[" + intRealTopLyr + "]이 상이하여 실적적치단 변경 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	        	
	            szYD_DN_WR_LAYER = szREAL_TOP_LYR;
    	    }
	        //-------------------------------------------------------------------------------------------------------------------	
	        
	        //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정
		    //-------------------------------------------------------------------------------------------------------------------	
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// Y4RegYdStklyr call 시  logId 항목 추가 개선
//	        intRtnVal = this.Y4RegYdStklyr(getRecSet,szYD_DN_WR_LAYER) ;
	        intRtnVal = this.Y4RegYdStklyr(getRecSet,szYD_DN_WR_LAYER, logId) ; 
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
	        
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            
          //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      	getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_DN_WR_LOC",       	getCrnschRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setRecord.setField("YD_DN_WR_LAYER",     	szYD_DN_WR_LAYER) ;
	        setRecord.setField("YD_DN_WRK_ACT_GP",   	getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP") );
	        setRecord.setField("YD_DN_WR_XAXIS",     	getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
	        setRecord.setField("YD_DN_WR_YAXIS",     	getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
	        setRecord.setField("YD_DN_WR_ZAXIS",     	getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
	        setRecord.setField("YD_WRK_PROG_STAT",   	getCrnschRecord.getFieldString("YD_WRK_PROG_STAT"));
	        setRecord.setField("YD_DN_CMPL_DT",      	YdUtils.getCurDate("yyyyMMddHHmmss"));
	        setRecord.setField("YD_GP",   				getCrnschRecord.getFieldString("YD_GP"));
	        
	        //--------------------------------------------------------------------------------------------------
	        //	크레인스케줄 등록 시 야드작업계상일자에 등록되는 값을 권하완료시점에 다시 수정, 권하완료 시의 값이 사용됨
	        //	TO_CHAR(SYSDATE - (7 / 24), 'YYYYMMDD')
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.23
	        //--------------------------------------------------------------------------------------------------
	        String szYD_WRK_HDS_DD				= YdUtils.getDefaultHdsDate();
	        
	        setRecord.setField("YD_WRK_HDS_DD",   		szYD_WRK_HDS_DD);
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]의 야드작업계상일자["+szYD_WRK_HDS_DD+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	        //--------------------------------------------------------------------------------------------------
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]에 권하실적정보 수정 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	        
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// setRecord 에 logId 추가
setRecord.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

	        intRtnVal = this.Y4UpdYdCrnsch(setRecord, 0);
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]에 권하실적정보 수정 완료 - 반환값 : " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	        	
            //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 차량이므로 차량 스케줄 이송재료 등록 후 차량진행관리 호출
	        //-------------------------------------------------------------------------------------------------------------------
            if(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(2, 4).equals("PT")|| 
               ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(2, 4).equals("TR")){
            	
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// Y4SetYdCar call 시  logId 항목 추가 개선
//            	intRtnVal = this.Y4SetYdCar(getRecSet, 1) ; 
            	intRtnVal = this.Y4SetYdCar(getRecSet, 1, logId) ; 
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
            	
            	szMsg = "차량이송재료 등록 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            	
            	// 차량 작업 진행관리 호출
            	recInTemp = JDTORecordFactory.getInstance().create();
            	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
            	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
            	recInTemp.setField("CAR_LDUD_GP",   "U");
            	recInTemp.setField("YD_DN_WR_LOC", 	szYD_DN_WR_LOC);
            	
				szMsg = "[" + szOperationName + "] 차량 작업 진행관리 호출";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				ydUtils.displayRecord(szOperationName, recInTemp);

//////////////////				
				JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
				JDTORecord 		outRecord1  = JDTORecordFactory.getInstance().create();
				String szAPPLY_YN 			= "N";
				
				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
					inRecord1.setField("REPR_CD_GP", "T00110");    //차량정보 SKIP
				} else {
					inRecord1.setField("REPR_CD_GP", "K00110");    //차량정보 SKIP
				}
				
				/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
				if(intRtnVal > 0) {
					outResult.first();
					outRecord1  = outResult.getRecord();
					szAPPLY_YN = outRecord1.getFieldString("ITEM1");				
				}
				szLogMsg="차량정보 SKIP 적용 " + szAPPLY_YN ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);				
				ydUtils.putLogNew(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);				

				//크레인스케줄 작업재료
				recInTemp.setField("CRN_WRK_MTLS_SET",getRecSet);
				
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// recInTemp 에 logId 추가
				recInTemp.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

				if (szAPPLY_YN.equals("Y")) {
	            	//차량 작업진행관리 호출(상차)
	            	intRtnVal = this.procY4CarWrkStatCtr(recInTemp);
				} else {
	            	//차량 작업진행관리 호출(상차)
	            	intRtnVal = this.procY4CarWrkStatCtr(recInTemp);
	            	if(intRtnVal < 0){
	            		m_ctx.setRollbackOnly();
	                	throw new JDTOException("<procY4CrnUdWr> procY4CarWrkStatCtr ERROR CODE =" + intRtnVal);
	            	}
				}	
            	szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
            
            }
            
            //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 차량이아니고  권상위치가 차량이면 차량진행관리 호출
	        //-------------------------------------------------------------------------------------------------------------------
            if(!szYD_DN_WR_LOC.substring(2, 4).equals("PT")&& 
               !szYD_DN_WR_LOC.substring(2, 4).equals("TR")){
            	
            	//권상실적위치가 차량인 경우
            	if(szYD_UP_WR_LOC.substring(2, 4).equals("PT")|| 
            	   szYD_UP_WR_LOC.substring(2, 4).equals("TR")){
                	
            		//차량 작업 진행관리 호출(하차)
            		//전사물류개선 2021.1.6 
            		// 차량입고시 - procY4CarWrkStatCtr호출을 권상으로 이동처리한다. 
//            		if(!PlateGdsYdUtil.isPlateNewMoudleApply("DM")){
//            			recInTemp = JDTORecordFactory.getInstance().create();
//            			recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
//            			recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
//            			recInTemp.setField("CAR_LDUD_GP",   "L");
//            			recInTemp.setField("YD_UP_WR_LOC",  szYD_UP_WR_LOC);
//            			
//            			szMsg = "[" + szOperationName + "] 차량 작업 진행관리 호출";
//            			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//            			ydUtils.displayRecord(szOperationName, recInTemp);                	
//            			
//            			//크레인스케줄 작업재료
//            			recInTemp.setField("CRN_WRK_MTLS_SET",getRecSet);
//            			
//            			//차량 작업 진행관리 호출(하차)
//            			intRtnVal = this.procY4CarWrkStatCtr(recInTemp);
//            			if(intRtnVal < 0){
//            				m_ctx.setRollbackOnly();
//            				throw new JDTOException("<procY4CrnUdWr> procY4CarWrkStatCtr ERROR CODE =" + intRtnVal);
//            			}
//            			
//            			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
//            		}
            		
            		if("".equals(szYD_CAR_SCH_ID)){
            			JDTORecordSet 	tmpRst  	= JDTORecordFactory.getInstance().createRecordSet("");
            			recInTemp = JDTORecordFactory.getInstance().create();
            			recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
            			recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
            			if(commDao.select(recInTemp,tmpRst, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getRetnCarSchIdByCrnSchId")>1){
            				szYD_CAR_SCH_ID = tmpRst.getRecord(0).getFieldString("YD_CAR_SCH_ID");
            			}
            		}
            	}
            	 
            }
            
            //-------------------------------------------------------------------------------------------------------
            // 2010.04.22 윤재광 추가.
            // 권하발생시 From위치에 (운송지시대기, 운송대기)인 제품이 없으면 Bed정보를 완산에서 입출고가능으로 변경
            // 2010.04.23 윤재광 추가
            // 권상실적으로 변경
            //-------------------------------------------------------------------------------------------------------
            	
            //YdCommonUtils.procChangeBedTypeForPlateGds(szYD_UP_WR_LOC,szMethodName);
            		
            //-------------------------------------------------------------------------------------------------------
            
             //-------------------------------------------------------------------------------------------------------
            // 2025.07.09 허정욱 추가. - 임진후 기사 요청 RITM1277108
            // 권하 발생시, 권하베드에 차량LOTID 있는 제품이 있다면, BED 정보를 일반베드 -> 완산베드로 변경
            //-------------------------------------------------------------------------------------------------------
            String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "019");//후판 개발 적용여부
            
            if("Y".equals(sApplyYnPI)){
            	szMsg = "carlotId 제품 권하시 완산베드 적용";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                
            	this.procChangeBedTypeForPlateGdsToFull(szYD_DN_WR_LOC,szMethodName,logId);
            }
            
            if(getRecord.getFieldString("YD_WBOOK_ID") == null ||
     	       getRecord.getFieldString("YD_WBOOK_ID").equals("")) {
                szMsg = "YD_WBOOK_ID  Data Error	: 작업예약 ID가 없습니다.";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY4CrnUdWr> " + szMsg);
	        }
	        
            //-------------------------------------------------------------------------------------------------------------------
		    //	2010.04.14 윤재광 인터페에스 송신처리
	        //  각각의 I/F 송신조건은 다시 체크 요망.
	        //-------------------------------------------------------------------------------------------------------------------
            {
	            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			출하관리 후판제품입고작업실적전송  - YDDMR002
		         * 업무기준 Desc : 1. RT입고
		         * 				  2. 차량하차 입고
		         * 				  3. 재료진도코드가 입고대기인 경우에만 전송
		         * 스케줄코드 :  1. RT입고 스케줄
		         * 				2. 차량하차 스케줄  : PT(팔렛트), TR(트레일러), LM(하차)
		         * 기능 추가 : 임춘수
		         * 일자 : 2009.06.15
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        //권상실적위치, 스케줄코드, 크레인스케줄ID
		        szMsg="[" + szOperationName + "] 출하관리 후판제품입고작업실적전송 송신 전 판단 시작 - 권상실적위치["+szYD_UP_WR_LOC+"], 권하실적위치["+szYD_DN_WR_LOC+"], 스케줄코드["+szYD_SCH_CD+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
		        
		        if( ( 
		        	  ( szYD_UP_WR_LOC.substring(2, 4).equals("RT") && 
		        		szYD_SCH_CD.substring(2, 4).equals("RT") && 
		        		szYD_SCH_CD.substring(6, 7).equals("L") )				/* 후판제품 RT 입고*/
		        	  || 
		        	  ( szYD_UP_WR_LOC.substring(2, 4).equals("TF") && 
		        	    szYD_SCH_CD.substring(2, 4).equals("TF") && 
		        	    szYD_SCH_CD.substring(6, 7).equals("L") )				/* 후판제품 TF 입고*/	
		        	  || 
		        	  ( (szYD_UP_WR_LOC.substring(2, 4).equals("PT") || 
		        		 szYD_UP_WR_LOC.substring(2, 4).equals("TR"))
		        		 && 
		        		(szYD_SCH_CD.substring(2, 4).equals("PT") || 
		        		 szYD_SCH_CD.substring(2, 4).equals("TR") ) 
		        		 && 
		        		 szYD_SCH_CD.substring(6, 7).equals("L") ) 				/* 후판제품[K]야드 차량 하차 */
		        	 )			
		        	&& 
		        	szYD_DN_WR_LOC.matches("[KT][A-Z]\\d\\d\\d\\d\\d\\d"))      /*--2013.02.07 수정 (3기) K --> [KT] */
		        {
		        	
		        	recInTemp = JDTORecordFactory.getInstance().create();
		        	
		        	/*
		    		 * 2014.03.25 윤재광
		    		 * 2후판 제품창고 사내절단장 북아웃 요구시 디폴트 저장위치로 TO위치 결정
		    		 */
		        	if("TB010101".equals(szYD_DN_WR_LOC)||
		        	   "TB033101".equals(szYD_DN_WR_LOC)||
		        	   "TB032801".equals(szYD_DN_WR_LOC)  //TB033101->TB032801로 변경 요청. 2022.11.30  후판품질팀 서승범 책임. 1673
		        	   ){
		        		
		        		String sToLoc = "";
		        		
		        		getRecSet.first();
		        		String sJplateStlNo = ydDaoUtils.paraRecChkNull(getRecSet.getRecord(),"STL_NO"); 
		        		
		        		if("TB010101".equals(szYD_DN_WR_LOC)){
		        			sToLoc = "FE0101";
		        		}else{
		        			sToLoc = "FE0201";
		        			
		        			//재료공통 저장위치 초기화
		        			JDTORecord recInTmp	= JDTORecordFactory.getInstance().create();
		        			recInTmp.setField("YD_GP",        		"T");
		        			recInTmp.setField("YD_BAY_GP",    		"C");
		        			recInTmp.setField("YD_EQP_GP",    		"RT");
		        			recInTmp.setField("YD_STK_COL_NO",		"PA");
		        			recInTmp.setField("YD_STK_BED_NO", 	"");
		        			recInTmp.setField("YD_STK_LYR_NO", 	"");
		        			recInTmp.setField("FNL_REG_PGM",  		"PPYDJ004");
		        			recInTmp.setField("MODIFIER",     		"PPYDJ004");
		        			recInTmp.setField("YD_STR_LOC_HIS1", 	"") ;
		        			recInTmp.setField("YD_STR_LOC_HIS2", 	""); 
		        			recInTmp.setField("PLATE_NO",    		sJplateStlNo); 
		        			recInTmp.setField("YD_STR_LOC", 		"TCRTPA");
							
							intRtnVal = ydStockDao.updPtComm_LOC(recInTmp, 1);
		        		}
		        		
		        		recInTemp.setField("MSG_ID", 			"YDPPJ011");
		        		recInTemp.setField("YD_STK_COL_FR", 	sToLoc);		// From적치열
		        		recInTemp.setField("YD_STK_BED_FR", 	"01");			// From적치BED
		        		recInTemp.setField("YD_STK_COL_TO", 	sToLoc);		// TO적치열
		        		recInTemp.setField("YD_STK_BED_TO", 	"01");			// TO적치BED 
		        		recInTemp.setField("YD_EQP_WRK_SH", 	"1");			// 야드설비작업매수
		        		recInTemp.setField("ARR_STL_NO", 		sJplateStlNo);
		                
		                JPlateYdCommonUtils.sendL3YDPPJ011(recInTemp);
		                
		                YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
		                
		                /*
		                 * 적치단에 존재하는 해당 저장품정보를 모두 CLEAR한다.
		                 */
		                recInTemp.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0, 6));    
		            	recInTemp.setField("YD_STK_BED_NO",       szYD_DN_WR_LOC.substring(6));   
		            	//recInTemp.setField("YD_STK_LYR_NO",       "001") ;  //여기 실제 권하실적 위치 기반으로 클리어 필요. 2022.05.20 박종호   2023.11.02 박종호
		            	recInTemp.setField("YD_STK_LYR_NO",       szYD_DN_WR_LAYER) ;  //여기 실제 권하실적 위치 기반으로 클리어 필요. 2022.05.20 박종호   2023.11.02 박종호
		            	recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
		            	recInTemp.setField("STL_NO",              "");
		                
		                intRtnVal = this.Y1UpdYdStklyr(recInTemp, 0);  //크레인 적치단의 재료정보 UPDATE
		                
		            	//후판 정정야드 야드맵에 Data셋팅
	        	    	intRtnVal = ydStklyrDao.updYdStklyrJplateStlNo(recInTemp); 
		        	
		        	}else if("TC010101".equals(szYD_DN_WR_LOC)||"TC010201".equals(szYD_DN_WR_LOC)||
	        			     "TC010301".equals(szYD_DN_WR_LOC)||"TC010303".equals(szYD_DN_WR_LOC)||
	        			     "TC010401".equals(szYD_DN_WR_LOC)||"TC010501".equals(szYD_DN_WR_LOC)||
	        				 "TC010503".equals(szYD_DN_WR_LOC)||
	        				 szYD_DN_WR_LOC.startsWith("TB01")||
	        				 szYD_DN_WR_LOC.startsWith("TB02")||
		    				 szYD_DN_WR_LOC.startsWith("TB03")){
		        		/*
		        		 * #2UT 베드에 적치시 입고실적 미송신
		        		 */
		        	
		        	}else{
		        		// 전사물류개선프로젝트 2021. 1.6
		        		// 제품야드의 차량입고(반품,회송,출고취소)는 입고실적을 보내지 않는다.
		        		JDTORecord tmpParam	= JDTORecordFactory.getInstance().create();
		        		JDTORecordSet tmpResult = JDTORecordFactory.getInstance().createRecordSet("temp");
		        		tmpParam.setField("YD_WBOOK_ID", szYdWbookId);
						if( commDao.select(tmpParam,tmpResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.checkYdCarInStock") < 1){
							
//							String sApplyYnPI_IN = ydPICommDAO.ApplyYnPI("", "후판 권하실적처리 수신", "APPPI0", "*", "*"); 	
//    						if("Y".equals(sApplyYnPI_IN)) {
								recInTemp.setField("MQ_TC_CD"  , "M10YDLMJ1012");									//전문코드
								szMsg="[" + szOperationName + "] 출하관리 후판제품입고작업실적전송 송신 완료(M10YDLMJ1012)";
//    						} else {	
//								recInTemp.setField("MSG_ID"    , "YDDMR002");									//전문코드
//								szMsg="[" + szOperationName + "] 출하관리 후판제품입고작업실적전송 송신 완료(YDDMR002)";
//    						}
							recInTemp.setField("YD_GP"         , szYD_DN_WR_LOC.substring(0, 1));				//입고야드구분
							recInTemp.setField("YD_CRN_SCH_ID" , szCrnSchId);									//크레인스케줄ID
							
							ydDelegate.sendMsg(recInTemp);
														
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
//PIDEV_QM							
//							if("Y".equals(sApplyYnPI_IN)) {
								recInTemp1 = JDTORecordFactory.getInstance().create();
								recInTemp1.setField("JMS_TC_CD"  , "YDQMJ601");									//전문코드
								szMsg="[" + szOperationName + "] 출하관리 품질 후판제품입고작업실적전송 송신 완료(YDQMJ601)";
								recInTemp1.setField("YD_GP"         , szYD_DN_WR_LOC.substring(0, 1));				//입고야드구분
								recInTemp1.setField("YD_CRN_SCH_ID" , szCrnSchId);									//크레인스케줄ID
								
								ydDelegate.sendMsg(recInTemp1);
															
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							
								ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);							
//    						}
						}
		        	}
		        }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        if("TCRTUT01".equals(szYD_DN_WR_LOC)||
			       "TCRTUT02".equals(szYD_DN_WR_LOC)){
	        		/*
		    		 * 2015.09.09 윤재광
		    		 * 2후판 제품창고 #2UT설비 보급시 후판조업L2로 전문송신
		    		 */	
	        		getRecSet.first();
	        		String sJplateStlNo = ydDaoUtils.paraRecChkNull(getRecSet.getRecord(),"STL_NO"); 
	        		
	        		JPlateYdDelegate jDelegate = new JPlateYdDelegate();
	        		
	        		szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					
	        		JDTORecord jrecPara = JDTORecordFactory.getInstance().create();
	        		jrecPara.setField("MSG_ID", 			"YDS1L005");						// BOOK-IN 실적 전송
	        		jrecPara.setField("STL_NO",				sJplateStlNo);						// 재료번호
	        		jrecPara.setField("OPERATION_TYPE",		"1");								// 1:Book In, 2:Book Out
	        		jrecPara.setField("YD_STK_COL_GP",		szYD_DN_WR_LOC.substring(0, 6));	// TO위치
	        		jrecPara.setField("YD_STK_BED_NO", 		szYD_DN_WR_LOC.substring(6));    	// 야드적치BED번호

	        		jDelegate.sendMsg(jrecPara);

					szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 완료>>>>";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					
		        }
		        
		        if("TC010101".equals(szYD_DN_WR_LOC)||"TC010201".equals(szYD_DN_WR_LOC)||
		           "TC010301".equals(szYD_DN_WR_LOC)||"TC010303".equals(szYD_DN_WR_LOC)||
		           "TC010401".equals(szYD_DN_WR_LOC)||"TC010501".equals(szYD_DN_WR_LOC)||
			       "TC010503".equals(szYD_DN_WR_LOC)){
	        		/*
		    		 * 2015.09.09 윤재광
		    		 * 2후판 제품창고 #2UT야드 적치시 후판조업L3로 전문송신
		    		 */	
	        		getRecSet.first();
	        		String sJplateStlNo = ydDaoUtils.paraRecChkNull(getRecSet.getRecord(),"STL_NO"); 
	        		
	        		JPlateYdDelegate jDelegate = new JPlateYdDelegate();
	        		
	        		szMsg = "["+ szOperationName +"] 후판조업으로 저장위치 변경이력 실적 전송 .. 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	        		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					
	        		JDTORecord jrecPara = JDTORecordFactory.getInstance().create();
	        		jrecPara.setField("MSG_ID", 		"YDPPJ011");
	        		jrecPara.setField("YD_STK_COL_FR", 	szYD_UP_WR_LOC.substring(0, 6));	// From적치열
	        		jrecPara.setField("YD_STK_BED_FR", 	szYD_UP_WR_LOC.substring(6));		// From적치BED
	        		jrecPara.setField("YD_STK_COL_TO", 	szYD_DN_WR_LOC.substring(0, 6));	// TO적치열
	        		jrecPara.setField("YD_STK_BED_TO", 	szYD_DN_WR_LOC.substring(6));		// TO적치BED
	        		jrecPara.setField("YD_EQP_WRK_SH", 	"");								// 야드설비작업매수
	        		jrecPara.setField("ARR_STL_NO", 	sJplateStlNo);
	    	        
	        		jDelegate.sendMsg(jrecPara);

					szMsg = "["+ szOperationName +"] 후판조업으로 저장위치 변경이력 실적 전송 .. 완료>>>>";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					
		        }
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			출하관리 후판제품이적작업실적 전송  - YDDMR005
		         * 업무기준 Desc : 1. 후판제품[k]기준으로 동내, 동간 이적 작업(RT로 출고시 무시, RT입고 시에만 전송)
		         * 				  2. 재료진도코드가 입고대기가 아닌 경우에만 전송
		         * 스케줄코드 :  1. 동내스케줄코드[YD], RT 동간이적
		         * 기능 추가 : 임춘수 
		         * 일자 : 2009.06.15
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        if( ( szYD_UP_WR_LOC.substring(2, 4).matches("\\d\\d") && 
		        	  szYD_DN_WR_LOC.substring(2, 4).matches("\\d\\d") )		//일반야드 이적
		        	|| 
		        	( ( szYD_UP_WR_LOC.substring(2, 4).equals("RT") && 
		        		szYD_SCH_CD.substring(2, 4).equals("RT") && 
		        		szYD_SCH_CD.substring(6, 7).equals("L") )				/* 후판제품 RT 입고(동간이적)*/
		        	  || 
		        	  ( szYD_UP_WR_LOC.substring(2, 4).equals("TF") && 
		        	    szYD_SCH_CD.substring(2, 4).equals("TF") && 
		        	    szYD_SCH_CD.substring(6, 7).equals("L") )				/* 후판제품 TF 입고(동간이적)*/	
		        	 )) 
		        {
		        	/*
		    		 * 2014.03.25 윤재광
		    		 * 2후판 제품창고 사내절단장 북아웃 요구시 디폴트 저장위치로 TO위치 결정
		    		 */
		        	if("TB010101".equals(szYD_DN_WR_LOC)||
		        	   "TB033101".equals(szYD_DN_WR_LOC)||
		        	   "TB032801".equals(szYD_DN_WR_LOC)  //TB033101->TB032801로 변경 요청. 2022.11.30  후판품질팀 서승범 책임. 1673
		        	   ){
		        		
		        	}else if("TC010101".equals(szYD_UP_WR_LOC)||"TC010201".equals(szYD_UP_WR_LOC)||
		 		             "TC010301".equals(szYD_UP_WR_LOC)||"TC010303".equals(szYD_UP_WR_LOC)||
				             "TC010401".equals(szYD_UP_WR_LOC)||"TC010501".equals(szYD_UP_WR_LOC)||
					         "TC010503".equals(szYD_UP_WR_LOC)){
		        		
		        		//#2UT -> C동 제품창고로 이적입고시 입고실적전문 송신
		        		if(!szYD_DN_WR_LOC.startsWith("TC010"))
		        		{
		        			recInTemp = JDTORecordFactory.getInstance().create();
		        			
		        			//PIDEV 	
//		        			String sApplyYnPI_IN1 = ydPICommDAO.ApplyYnPI("", "후판 권하실적처리 수신", "APPPI0", "*", "*");
//    						if("Y".equals(sApplyYnPI_IN1)) {
				        		recInTemp.setField("MQ_TC_CD"   , "M10YDLMJ1012");					//전문코드
//    						} else { 
//    							recInTemp.setField("MSG_ID"     , "YDDMR002");									//전문코드
//    						}
    						
			    			recInTemp.setField("YD_GP"          , szYD_DN_WR_LOC.substring(0, 1));				//입고야드구분
			    			recInTemp.setField("YD_CRN_SCH_ID"  , szCrnSchId);									//크레인스케줄ID
			    			
			    			ydDelegate.sendMsg(recInTemp);
			    			
// PIDEV_QM		  
//			    			if("Y".equals(sApplyYnPI_IN1)) {
			    				JDTORecord recInTemp2 			= JDTORecordFactory.getInstance().create();
			    				recInTemp2.setField("JMS_TC_CD"         , "YDQMJ601");	
			    				recInTemp2.setField("JMS_TC_CREATE_DDTT", new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
			    				recInTemp2.setField("YD_GP"             , szYD_DN_WR_LOC.substring(0, 1));				//입고야드구분
				    			recInTemp2.setField("YD_CRN_SCH_ID"     , szCrnSchId);									//크레인스케줄ID
				    			
				    			ydDelegate.sendMsg(recInTemp2);
//			    			}	
		        		}
		        	}else{
			        	recInTemp = JDTORecordFactory.getInstance().create();
			        	
//			        	String sApplyYnPI_IN1 = ydPICommDAO.ApplyYnPI("", "후판 권하실적처리 수신", "APPPI0", "*", "*");
			        	//PIDEV 	
//						if("Y".equals(sApplyYnPI_IN1)) {
							recInTemp.setField("MQ_TC_CD"  , "M10YDLMJ1032");		
//						} else {
//							recInTemp.setField("MSG_ID"    , "YDDMR005");									//전문코드
//						}	
						
		    			recInTemp.setField("YD_CRN_SCH_ID" , szCrnSchId);									//크레인스케줄ID
		    			
		    			ydDelegate.sendMsg(recInTemp);
		    			
						szMsg="[" + szOperationName + "] 출하관리 후판제품이적작업실적 전송 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
		        	}
		        }
		        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            }
            //-------------------------------------------------------------------------------------------------------------------
            
            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업 및 재료, 작업예약 및 재료정보를 Clear
	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인 작업재료 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("MODIFIER",           szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
	        intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl_YD_CRN_SCH_ID(setRecord);
	        if(intRtnVal <= 0) {
                szMsg = "[" + szOperationName + "] 크레인스케줄작업재료 삭제중 Error!! Code : " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                m_ctx.setRollbackOnly();
	        	throw new JDTOException("<procY4CrnUdWr> updYdCrnwrkmtl_YD_CRN_SCH_ID " + szMsg);
	        }
	        
	        //크레인스케줄 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("MODIFIER",           szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// setRecord 에 logId 추가
setRecord.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

	        intRtnVal = this.Y4UpdYdCrnsch(setRecord, 0);
	        
	        if(intRtnVal < 0){
	        	m_ctx.setRollbackOnly();
            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdCrnsch ERROR CODE =" + intRtnVal);
            }
	        
	        //작업예약완료 CHECK
	        getRecSetWb = JDTORecordFactory.getInstance().createRecordSet("temp");
			intRtnVal = this.Y4GetYdCrnsch(getRecord, getRecSetWb, 4);
			
			if(intRtnVal < 0){
				m_ctx.setRollbackOnly();
            	throw new JDTOException("<procY4CrnUdWr> Y4GetYdCrnsch ERROR CODE =" + intRtnVal);
            }
	        
	        /*
	         * 작업예약에 존재하는 크레인스케쥴 정보가 없을경우에 작업예약정보를 삭제한다.
	         */	
	        getRecSetWb.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSetWb.size() == 0){
	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
	            m_ctx.setRollbackOnly();
	            throw new JDTOException("<procY4CrnUdWr> Y4GetYdCrnsch" + szMsg);
	        }
	        outRecord = getRecSetWb.getRecord();
	        
	        int schcnt = outRecord.getFieldInt("SCH_CNT");
	        int endcnt = outRecord.getFieldInt("END_CNT");
	                    
	        if (schcnt == endcnt) {
	        	
	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId) ;
	            bookrecord.setField("DEL_YN",             "Y") ;
	            bookrecord.setField("YD_SCH_PROG_STAT",   "E") ;
	            
	            intRtnVal = this.Y4UpdYdWrkbook(bookrecord, 0);
	            if(intRtnVal < 0){
	            	m_ctx.setRollbackOnly();
	            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdWrkbook ERROR CODE =" + intRtnVal);
	            }
		        
		        //작업예약재료조회
		        szMsg = "[" + szOperationName + "] 작업예약["+szYdWbookId+"]의 재료 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
		        
	            getRecSetWb = JDTORecordFactory.getInstance().createRecordSet("temp");
	            intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(bookrecord, getRecSetWb, 1);
	            if(intRtnVal < 0){
	            	m_ctx.setRollbackOnly();
	            	throw new JDTOException("<procY4CrnUdWr> getYdWrkbookmtl ERROR CODE =" + intRtnVal);
	            }
				
				szMsg = "[" + szOperationName + "] 작업예약["+szYdWbookId+"]의 재료 조회 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	            
		        /*
		         * 작업예약재료매수 셋팅. 이후단에서 사용 -(RT반납대상재가 모두 RT상으로 권하된 후에는 권하위치에서 대상재 삭제처리 기능).
		         */
		        intYD_EQP_WRK_SH = getRecSetWb.size();
		        
	            //조회한 작업예약재료1매씩 저장품 업데이트
				for( int Loop_i = 1; Loop_i <= getRecSetWb.size(); Loop_i++ ) {
					
					getRecSetWb.absolute(Loop_i);
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setRecord(getRecSetWb.getRecord());
					
					szSTL_NO = ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO");

// 현재작업이 가적장에서 북인처리 인지 반납과 구별 하기 위해 CHECK 함 
// 가적장에서 북인처리는 88로 고정됨	
					
					szBOOK_IN = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_UP_COLL_SEQ");
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", 		szSTL_NO);
					recInTemp.setField("YD_SCH_CD", 	"");
					recInTemp.setField("YD_WBOOK_ID", 	"");
					//---------------------------------------------------------------------------------------
					//	RT/TF입고, 차량입고 시 야드입고일자 업데이트
					//---------------------------------------------------------------------------------------
					if( ( szYD_SCH_CD.substring(2, 4).equals("RT")|| 
						  szYD_SCH_CD.substring(2, 4).equals("TF")|| 
						  szYD_SCH_CD.substring(2, 4).equals("PT")||
						  szYD_SCH_CD.substring(2, 4).equals("TR") )
						&& 
						szYD_SCH_CD.substring(6, 7).equals("L") ) {
						recInTemp.setField("YD_RCPT_DATE", 	YdUtils.getCurDate("yyyyMMdd"));
					}
					//---------------------------------------------------------------------------------------
					
					intRtnVal = ydStockDao.updYdStock(recInTemp, 0);
					if(intRtnVal < 0){
						m_ctx.setRollbackOnly();
		            	throw new JDTOException("<procY4CrnUdWr> updYdStock ERROR CODE =" + intRtnVal);
		            }
				}
				
				szMsg = "[" + szOperationName + "] 작업예약재료["+szSTL_NO+"]삭제 처리 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("DEL_YN",      "Y");
				recInTemp.setField("MODIFIER",    szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
				//recInTemp.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID"));
				recInTemp.setField("YD_WBOOK_ID", szYdWbookId); //2013.12.04 수정
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl1(recInTemp);
				if(intRtnVal < 0){
					m_ctx.setRollbackOnly();
	            	throw new JDTOException("<procY4CrnUdWr> updYdWrkbookmtl1 ERROR CODE =" + intRtnVal);
	            }
	            szMsg = "[" + szOperationName + "] 작업예약재료["+szSTL_NO+"]삭제 처리 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	            
	            //--------------------------------------------------------------------------------------------------
	            //	RT반납대상재가 모두 RT상으로 권하된 후에는 권하위치에서 대상재 삭제처리 기능 추가
	            //	수정자 : 임춘수
	            //	수정일 : 2009.11.30
	            //--------------------------------------------------------------------------------------------------
	            //  2013.05.22 - 반납은 "UM", 동간이적은 "UR" 로 정함 --> 통합후 "UR" 은 "DL", "DR" 로 변경
	            if( szYD_DN_WR_LOC.substring(2, 4).equals("RT") && 
	            	szYD_SCH_CD.substring(6, 8).equals("UM")){
	            	
	            	szMsg = "[" + szOperationName + "] RT상으로 권하된 후에는 권하위치에서 대상재 삭제처리 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	            	
	            	recInTemp = JDTORecordFactory.getInstance().create();
	            	
	            	String szYD_DN_WR_LAYER_1	= "";
	            	for(int i = 0; i < intYD_EQP_WRK_SH; i++ ) {
						
	            		//szYD_DN_WR_LAYER_1 = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i);
	            		szYD_DN_WR_LAYER_1 = ydDaoUtils.stringPlusInt("001", i); //RT에 반납대상 모두를 Clear 하기 위해서 1단부터 작업예약갯수만큼의 단까지 
	            		
	            		szMsg = "[" + szOperationName + "] ["+(i + 1)+"] RT상위치[적치열:"+szYD_DN_WR_LOC.substring(0, 6)+", 적치베드:"+szYD_DN_WR_LOC.substring(6)+", 적치단:"+szYD_DN_WR_LAYER_1+"]에서 적치중인 대상재 삭제 시작" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	            		
						recInTemp.setField("YD_STK_COL_GP", 		szYD_DN_WR_LOC.substring(0, 6));
						recInTemp.setField("YD_STK_BED_NO", 		szYD_DN_WR_LOC.substring(6));
						recInTemp.setField("YD_STK_LYR_NO", 		szYD_DN_WR_LAYER_1);
						recInTemp.setField("STL_NO", 				"");
						recInTemp.setField("YD_STK_LYR_MTL_STAT", 	YdConstant.YD_STK_LYR_MTL_STAT_STK_ABLE);
						
						DaoManager.updYdStklyr(recInTemp, 0);
						
						szMsg = "[" + szOperationName + "] ["+(i + 1)+"] RT상위치[적치열:"+szYD_DN_WR_LOC.substring(0, 6)+", 적치베드:"+szYD_DN_WR_LOC.substring(6)+", 적치단:"+szYD_DN_WR_LAYER_1+"]에서 적치중인 대상재 삭제 완료" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	            	}
	            	
	            	szMsg = "[" + szOperationName + "] RT상으로 권하된 후에는 권하위치에서 대상재 삭제처리 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
		            
		            if (szBOOK_IN.equals("88")){
		            	// 가적장 북인은 반납이 아님	
		            } else {
			            //--------------------------------------------------------------------------------------------------
			            //	후판제품반납실적 
			            //--------------------------------------------------------------------------------------------------
			            //szMsg = "[" + szOperationName + "] RT상으로 권하된 후에는 후판조업으로 후판제품반납실적 전송 시작";
			            //ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			            
			            //recInTemp = JDTORecordFactory.getInstance().create();
						//if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.05.22 추가 (3기)
						//	recInTemp.setField("MSG_ID"   , "YDPPJ001"); //2후판반납실적
						//} else {
						//	recInTemp.setField("MSG_ID"   , "YDPRJ001"); //1후판반납실적
						//}			            
			            //recInTemp.setField("YD_CRN_SCH_ID"   	, szCrnSchId);	//야드크레인스케줄ID
			            //ydDelegate.sendMsg(recInTemp);
			            
			            //szMsg = "[" + szOperationName + "] RT상으로 권하된 후에는 후판조업으로 후판제품반납실적 전송 완료";
			            //ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			            //--------------------------------------------------------------------------------------------------
		            }    
		        }
	            
	            
		         // 선별 작업 마지막 인 경우	            
	            if( szYD_SCH_CD.substring(2, 4).equals("SL") ) {
	            	
	    	        //-- 해송 출하 가동 이후 권하실적 발생시 SP_YD_SHIPSEL_001 프로시져 호출 ------------------------------------- 
	    			if(iSP_YD_SHIPSEL_001_callCnt == 0) {
	    				
	    				iSP_YD_SHIPSEL_001_callCnt++; //권하 처리 모듈 마지막 단계에서 호출함으로 두번 호출 않도록 하기 위한 변수+1
	    				
    		    		if( szYD_DN_WR_LOC.substring(2, 4).matches("\\d\\d") ) {
    		    			//후판해송출하적용여부가 'Y'이고 권하위치가 야드일경우 프로시져 호출
    		    			
    		    			//YdPlateCommDAO  commDao 	  = new YdPlateCommDAO();
    		    			
    		    			Object[] inParam = { 
    		    					 "*"
    		    					,szYD_DN_WR_LOC.substring(0,6)
    		    					,szYD_DN_WR_LOC.substring(6,8)
    		    				   };			
    		    			
    		    			int[] inParamIndex = {1,2,3};
    		    			
    		    			JDTORecord record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0004");
    		    			
    		    			if(record == null || record.size() <= 0){
    							szMsg="[권하실적처리] SP_YD_SHIPSEL_001 프로시져 호출시  Error!! " ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
    		    			}
    		    		}
	    		    }
	    	    	//------------------------------------------------------------------------------------------------			            	
	            	
	        	    JDTORecordSet recOutputSet 	= JDTORecordFactory.getInstance().createRecordSet("temp");
	        	    JDTORecord  recInputPara	= JDTORecordFactory.getInstance().create();
	        	    JDTORecord  recOutputPara	= JDTORecordFactory.getInstance().create();
	        	    
	        	    String sDONG = szYD_SCH_CD.substring(1, 2);
	        	    String sGATE = "";
	    			if( szYD_SCH_CD.substring(4, 6).equals("12") ) {
	    				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) {
	    					sGATE = "1";
	    				} else {
	    					sGATE = "3";
	    				}
	            	} else if( szYD_SCH_CD.substring(4, 6).equals("34") ) {
	            		if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) {
	            			sGATE = "2";
	    				} else {
	    					sGATE = "4";
	    				}	            			
	            	} else if( szYD_SCH_CD.substring(4, 6).equals("01") ) {
	            		sGATE = "1";
	            	} else if( szYD_SCH_CD.substring(4, 6).equals("23") ) {
	            		sGATE = "2";
	            	} else if( szYD_SCH_CD.substring(4, 6).equals("46") ) {
	            		sGATE = "3";
	            	}  else {
	            		sGATE = "4";
	            	}
	    			
	    			//----------------------------------------------------------------------
	    			//작업예약으로 해송, 육송 관련 필요한 정보를 조회한다.
	    			recInPara.setField("YD_WBOOK_ID", szYdWbookId);
	    			intRtnVal = commDao.select(recInPara, recOutputSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0116_PIDEV");
	    			
	        	    if(intRtnVal > 0){
	        	    	
	    	        	recOutputSet.first();
	    	        	recOutputPara = recOutputSet.getRecord();
	    	        	
	    	        	//PIDEV 	
	    	        	String sCarGp = "";
//						if("Y".equals(sApplyYnPI)) {
							sCarGp = ydDaoUtils.paraRecChkNull(recOutputPara,"CAR_KIND");
//						} else {
//							sCarGp = ydDaoUtils.paraRecChkNull(recOutputPara,"CARD_NO");
//						}
						
	    	        	if(sCarGp.startsWith("P")) {
	    	        		//작업예약의 Card번호가 'P'로 시작하면 해송선별
	    	        		
	    	        		String szSHIP_CD = ydDaoUtils.paraRecChkNull(recOutputPara,"SHIP_CD");
	    	        		String szSAILNO = ydDaoUtils.paraRecChkNull(recOutputPara,"SAILNO");
	    	        		String szRSHP_HOLD_NO = ydDaoUtils.paraRecChkNull(recOutputPara,"RSHP_HOLD_NO");
	    	        		
	    	        		//TB_YD_STKBED_PLATEINFO의 해당동,GATE 의 선별구분 조회
	    	        		recOutputSet 	= JDTORecordFactory.getInstance().createRecordSet("temp");
	    	        		
	    	        		recInPara.setField("SHIP_CD", szSHIP_CD);
	    	        		recInPara.setField("SAILNO", szSAILNO);
	    	        		recInPara.setField("RSHP_HOLD_NO", szRSHP_HOLD_NO);
	    	    			recInPara.setField("DONG_GP", sDONG);
	    	    			recInPara.setField("GATE", sGATE);
	    	    			
	    	    			intRtnVal = commDao.select(recInPara, recOutputSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0117");	    	        		
	    	        		
	    	        		if(intRtnVal > 0) {
	    	        			
			    	        	recOutputSet.first();
			    	        	recOutputPara = recOutputSet.getRecord();	    	 
			    	        	
			    	        	String sWRK_YN = ydDaoUtils.paraRecChkNull(recOutputPara,"ITEM1");
			    	        	
    							szMsg="[권하실적처리] =====> 해송 선별구분 ITEM1 : " + sWRK_YN;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);			    	        	
    							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);			    	        	
	    	        			
			    	        	if(sWRK_YN.equals("S")) { //선별구분이 'S'이면
			    	        		
			    	        		//해송선별 프로시져 호출
			    	    			Object[] inParam = { 
			    	    					 szYdGp
			    	    					,sDONG
			    	    					,sGATE
			    	    					,szSHIP_CD
			    	    					,szSAILNO
			    	    					,szRSHP_HOLD_NO
			    	    				   };
			    	    			
			    	    			int[] inParamIndex = {1,2,3,4,5,6};
			    	    			
			    	    			JDTORecord record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0003");	
			    	    			
					    			if(record.size() <= 0){
					    				
					    			} else {
					    				
					    				String sOUT_RTN_CODE =  ydDaoUtils.paraRecChkNull(record, "OUT_RTN_CODE");
					    				if(sOUT_RTN_CODE.length() == 14){
					    					YdDelegate ydDelegate1 = new YdDelegate();	
					    					JDTORecord recPara = JDTORecordFactory.getInstance().create();
					    					recPara.setField("JMS_TC_CD", "YDYDJ506");
					    					recPara.setField("YD_SCH_CD", sOUT_RTN_CODE.substring(0,8));
					    					//작업크레인 정보를 설비에 넣어준다. 
					    					recPara.setField("YD_EQP_ID", sOUT_RTN_CODE.substring(8,14) );
					    					
					    					szMsg = "[sOUT_RTN_CODE : "+sOUT_RTN_CODE;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);												
					    					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);												
					    					
					    					ydDelegate1.sendMsg(recPara);
					    				}	
					    			}		 			    	        		
			    	        	}
	    	        		}
   	    			
	    	        	} else {
	    	        		//작업예약의 Card번하가 'P'로 시작하지 않으면 육송선별
	    	        		
	    	        		String szCUST_CD = ydDaoUtils.paraRecChkNull(recOutputPara,"CUST_CD");
	    	        		String szDETAIL_ARR_CD = ydDaoUtils.paraRecChkNull(recOutputPara,"DETAIL_ARR_CD");	    	        		
	    	        		
	    	        		//TB_YD_PLATE_PREDIST의 해당동,GATE 의 선별구분 조회
	    	        		recOutputSet 	= JDTORecordFactory.getInstance().createRecordSet("temp");
	    	        		
	    	        		recInPara.setField("CUST_CD", szCUST_CD);
	    	        		recInPara.setField("DETAIL_ARR_CD", szDETAIL_ARR_CD);
	    	    			recInPara.setField("YD_BAY_GP", sDONG);
	    	    			recInPara.setField("YD_GATE", sGATE);
	    	    			
	    	    			intRtnVal = commDao.select(recInPara, recOutputSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0118");	    	        		
	    	        		
	    	        		if(intRtnVal > 0) {
	    	        			
	    	        			JDTORecord record = null;
	    	        			
			    	        	recOutputSet.first();
			    	        	recOutputPara = recOutputSet.getRecord();	 
			    	        	
			    	        	String sWRK_YN = ydDaoUtils.paraRecChkNull(recOutputPara,"YD_STK_BED_SEL_GP");
			    	        	
    							szMsg="[권하실적처리] =====> 조출 선별구분 YD_STK_BED_SEL_GP : " + sWRK_YN;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				    	        	
    							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);				    	        	
	    	        			
			    	        	if(sWRK_YN.equals("S")) { //선별구분이 'S'이면
			    	        		
			    	        		//조출대상 육송임..
			    	    			//프로시져에 목적지와 상세착지도 파라메터로 넘긴다.
			    	    			Object[] inParam = { 
			    	    					 szYdGp
			    	    					,sDONG
			    	    					,sGATE
			    	    					,szCUST_CD
			    	    					,szDETAIL_ARR_CD
			    	    				   };
			    	    			
			    	    			int[] inParamIndex = {1,2,3,4,5};
			    	    			
			    	    			record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0005_PIDEV");			    	        		
			    	        		
			    	        	} else {
			    	        		
			    	        		//전체 고객사 육송임..
			    	        		
			    	        		//TB_YD_RULE 의 해당동,GATE 의 선별구분 조회
			    	        		recOutputSet 	= JDTORecordFactory.getInstance().createRecordSet("temp");	    	        		
			    	        		
					        	    recInputPara	= JDTORecordFactory.getInstance().create();
					        	    recInputPara.setField("YD_GP", 	  szYdGp );
					        	    recInputPara.setField("GATE", 	  sDONG + sGATE );
					            	
					    	        /*com.inisteel.cim.yd.dao.ydeqpdao.getPlateYdRuleMgtSel*/
					    	        intRtnVal = ydEqpDao.getYdEqp(recInputPara, recOutputSet, 908);
					    	        
					    	        if(intRtnVal > 0){
					    	        	recOutputSet.first();
					    	        	recOutputPara = recOutputSet.getRecord();
						    	        
						    	        sWRK_YN	= ydDaoUtils.paraRecChkNull(recOutputPara,"WRK_YN");
						    	        if(sWRK_YN.equals("Y")) {
						    	        	
					    	    			//고객코드와 상세착지 없이 프로시져 호출
					    	    			Object[] inParam = { 
					    	    					 szYdGp
					    	    					,sDONG
					    	    					,sGATE
					    	    					,""
					    	    					,""
					    	    				   };
					    	    			
					    	    			int[] inParamIndex = {1,2,3,4,5};
					    	    			
					    	    			record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0005_PIDEV");			    	        		

						    	        }	
						    	        
					    	        }	    	        		
			    	        	}
			    	        	
			    	        	if(record != null) {
					    			if(record.size() <= 0){
					    				
					    			} else {
					    				
					    				String sOUT_RTN_CODE =  ydDaoUtils.paraRecChkNull(record, "OUT_RTN_CODE");
					    				if(sOUT_RTN_CODE.length() == 14){
					    					YdDelegate ydDelegate1 = new YdDelegate();	
					    					JDTORecord recPara = JDTORecordFactory.getInstance().create();
					    					recPara.setField("JMS_TC_CD", "YDYDJ506");
					    					recPara.setField("YD_SCH_CD", sOUT_RTN_CODE.substring(0,8));
					    					//작업크레인 정보를 설비에 넣어준다. 
					    					recPara.setField("YD_EQP_ID", sOUT_RTN_CODE.substring(8,14) );
					    					
					    					szMsg = "[sOUT_RTN_CODE : "+sOUT_RTN_CODE;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);												
					    					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);												
					    					
					    					ydDelegate1.sendMsg(recPara);
					    				}	
					    			}	
			    	        	}
	    	        		}
	    	        	}
	        	    }

	    			szMsg = "[" + szOperationName + "] 선별 작업 프로시져 기동 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
		            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);		
	            }
		         // 마지막이 차량입고인 경우	
	            if( (szYD_SCH_CD.substring(2, 4).equals("PT")||szYD_SCH_CD.substring(2, 4).equals("TR")) && 
					szYD_SCH_CD.substring(6, 7).equals("L") ) {

//////////////////	
					JDTORecordSet 	outResult7  	= JDTORecordFactory.getInstance().createRecordSet("");
					JDTORecord 		inRecord7 	= JDTORecordFactory.getInstance().create();
					JDTORecord 		outRecord7  = JDTORecordFactory.getInstance().create();
					String szAPPLY_YN7 			= "N";
					
    				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
    					inRecord7.setField("REPR_CD_GP", "T00200");    //차량입고 반경
    				} else {
    					inRecord7.setField("REPR_CD_GP", "K00200");    //차량입고 반경
    				}
					
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
					intRtnVal = ydEqpDao.getYdEqp(inRecord7, outResult7, 999);
					if(intRtnVal > 0) {
						outResult7.first();
						outRecord7  = outResult7.getRecord();
						szAPPLY_YN7 = outRecord7.getFieldString("ITEM1");				
					}
					szLogMsg="차량정보 SKIP 적용 " + szAPPLY_YN7 ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG);				
					ydUtils.putLogNew(szOperationName, szMethodName, szLogMsg, YdConstant.DEBUG, logId);				
	            	
					if (szAPPLY_YN7.equals("Y")) {
						/*
						 * 2012.02.08 윤재광
						 * 입고시 차량베드에 재료가 남아있는지 체크로직 보완
						 */
						recInTemp1 = JDTORecordFactory.getInstance().create();
						recInTemp1.setField("YD_STK_COL_GP", szYD_UP_WR_LOC);
						recInTemp1.setField("YD_STK_BED_NO", "01");
						
						JDTORecordSet rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
						intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp1, rsGetStkLyr, 6);
						
						if(rsGetStkLyr.size() == 0) {
							recInTemp1 = JDTORecordFactory.getInstance().create();
							recInTemp1.setField("YD_STK_COL_ACT_STAT",    "C"); // 사용가능으로
							recInTemp1.setField("YD_STK_COL_GP",    szYD_UP_WR_LOC.substring(0, 6));
							/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkColActStat*/
							intRtnVal = ydStkColDao.updYdStkColActStat(recInTemp1);
							if(intRtnVal < 0){
					            szMsg = "차량입고후 차량정지위치 사용가능 안됨(확인)";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.WARNING, logId);
				            }
						}
					}	
	            }
	            
	            //--------------------------------------------------------------------------------------------------
	        }else{
	
	            szMsg = "작업 예약 진행 중";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.WARNING, logId);
	            
				//-----------------------------------------------------------------------------------
				//-----------------------------------------------------------------------------------
            	
	            szMsg = "작업 예약 진행 중 - 크레인 작업예약재료 삭제처리 ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.WARNING, logId);
	            
		        //크레인 작업예약재료 삭제처리
		        setRecord = JDTORecordFactory.getInstance().create();
		        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
		        setRecord.setField("YD_WBOOK_ID",      	 szYdWbookId);
		        setRecord.setField("DEL_YN",             "Y");
		        setRecord.setField("MODIFIER",           szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);					
				
				intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0021");
			}
	        
            //---------------------------------------------------------------------------
            // RT 동간이적 권하완료시... 
            //  - 1) 작업예약ID로 작업예약테이블의 TO위치가이드 정보를 읽어온다.
            //  - 2) TO위치가이드로 BOOKOUT 존 코드를 찾는다.
            //  - 3) 목적동RT 가상버퍼에 대상제 삽입 (RT상의 대상제 삭제처리 포함)
            //  - 4) L2로 ROUTING 지시 전송
            //  - 5) 야드저장품 UPDATE - YD_BOOK_OUT_LOC, YD_RCPT_PLN_STR_LOC
            if((szYD_DN_WR_LOC.substring(2, 4).equals("RT") && 
            		(szYD_SCH_CD.substring(6, 8).equals("UR") || 
	            	 szYD_SCH_CD.substring(6, 8).equals("DL") || 
	            	 szYD_SCH_CD.substring(6, 8).equals("DR") ||
	            	 szYD_SCH_CD.substring(6, 8).equals("DM") ||
	            	 szYD_SCH_CD.equals("TCRTUTLM")) // #2UT설비 북아웃(직입고)
	           )||	 
	           "TBRTRAAP".equals(szYD_SCH_CD)//2후판 B동 #2DS 크레인파일링 작업인 경우 추가
	        ) {
            	if(szYD_DN_WR_LOC.startsWith("TCRTUT") ){
            	}else{
            		
	            	recInPara	= JDTORecordFactory.getInstance().create();	            	
	            	//-----------------------------------------------------------------------------------------------------------------
					// RT From 위치(권하실적위치)에서 적치된 제품의 정보를  읽어온다.

	            	getRecSet.last(); //last() 최상단
	    	        recInPara = getRecSet.getRecord();            	
	    	        String szPL_MTL_NO   	= ydDaoUtils.paraRecChkNull(recInPara,"STL_NO");  	  			//최상단 제품번호를 변수에 저장	  
	    	        String szSTL_PROG_CD 	= ydDaoUtils.paraRecChkNull(recInPara,"STL_PROG_CD");			//최상단 제품번호의 재료진도
	    	        
	            	//-----------------------------------------------------------------------------------------------------------------
	    	        
	    	        String szYD_TO_LOC_GUIDE  =  "";
	    	        
	    	        if("TBRTRAAP".equals(szYD_SCH_CD)){//2후판 B동 #2DS 크레인파일링 작업인 경우 추가
	    	        	
	    	        	szYD_TO_LOC_GUIDE= ydDaoUtils.paraRecChkNull(recInPara,"YD_RCPT_PLN_STR_LOC"); 	//최상단제품번호의 예정입고위치
	    	        	
	    	        	szMsg = "[" + szOperationName + "] 최상단재료의 야드To위치Guide 값  YD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	  
			            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	  
			            
	    	        }else{
	    	        	//  - 1) 작업예약ID로 작업예약테이블의 TO위치가이드 정보를 읽어온다.
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		            	recInPara.setField("YD_WBOOK_ID", szYdWbookId);
		            	intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0068");
		            	
		            	if(intRtnVal<=0) {
							szMsg = "[" + szOperationName + "] 작업예약ID:"+szYdWbookId + " 작업예약 테이블 조회 실패!! intRtnVal="+intRtnVal ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	  
				            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	  
				        	m_ctx.setRollbackOnly();
			            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdCrnsch ERROR =" + szMsg);
		            	}
		            	
		    			rsResult.first();
		    			recInPara = rsResult.getRecord();
		    			
		    			szYD_TO_LOC_GUIDE  =  ydDaoUtils.paraRecChkNull(recInPara, "YD_TO_LOC_GUIDE");
		    			
		    			szMsg = "[" + szOperationName + "] 작업예약ID:"+szYdWbookId + " 작업예약 테이블에 야드To위치Guide 값  YD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	  
			            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	  
	    	        }
	            	
	    			
	    			if("".equals(szYD_TO_LOC_GUIDE)) {
						szMsg = "[" + szOperationName + "] 작업예약ID:"+szYdWbookId + " 작업예약 테이블에 야드To위치Guide 값이 Null 입니다!! ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
			            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	  
			        	m_ctx.setRollbackOnly();
		            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdCrnsch ERROR =" + szMsg);
		            }
	    			
	    			if(szYD_TO_LOC_GUIDE.length() != 8) {
						szMsg = "[" + szOperationName + "] 작업예약ID:"+szYdWbookId + " 작업예약 테이블에 야드To위치Guide 값이 8자리가 아닙니다!! YD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
			            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	  
			        	m_ctx.setRollbackOnly();
		            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdCrnsch ERROR =" + szMsg);
		            }	    			
	            	
		            //-----------------------------------------------------------------------------------------------------------------
		            
		          //----------------------------------------------------------------------------------------------------------------------
					//	입고존 도착시 TO위치 결정로직 수행 시작
					//----------------------------------------------------------------------------------------------------------------------
		            String sHmiStat 		= "N";
					String sQuery1			= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
					JDTORecord wbJr 		= (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT03" });
					if (wbJr != null){ 
						sHmiStat	= StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
					}
					if("Y".equals(sHmiStat)){
						
						String sRtGp	= "";
						
						if("C".equals(szYD_SCH_CD.substring(5,6))) {
							sRtGp = "B";	// C R/T  - OFF-LINE
						}else if("B".equals(szYD_SCH_CD.substring(5,6))) { 
							sRtGp = "B";	// B R/T  - ON-LINE
						}else {
							sRtGp = "A";	// A R/T  - ON-LINE
						}	
						
						EJBConnector ejbConn = new EJBConnector("default", "RcptWrkDmdSeEJB", this);

						String szRtnMsg = (String)ejbConn.trx("procPreMainWrkToLocForPlateYd", new  Class[] { String.class, String.class, String.class}, 
     						   new Object[] { szPL_MTL_NO , sRtGp,        szYD_TO_LOC_GUIDE.substring(1,2)});
						
						if(YdConstant.RETN_CD_SUCCESS.equals(szRtnMsg)){
							
							JDTORecordSet rsResultT = JDTORecordFactory.getInstance().createRecordSet("");
							JDTORecord recParaT  	= JDTORecordFactory.getInstance().create();

							//재료번호
							recParaT.setField("STL_NO", szPL_MTL_NO);
							
							//저장품 테이블 조회
							intRtnVal = ydStockDao.getYdStock(recParaT, rsResultT, 0);
							
							rsResultT.first();
							recParaT = rsResultT.getRecord();
							
							//입고예정위치
							szYD_TO_LOC_GUIDE	= ydDaoUtils.paraRecChkNull(recParaT, "YD_RCPT_PLN_STR_LOC"); 
						}
					}
					//----------------------------------------------------------------------------------------------------------------------
					//	입고존 도착시 TO위치 결정로직 수행 종료
					//----------------------------------------------------------------------------------------------------------------------
		            
					//  - 2) TO위치가이드로 BOOKOUT 존 코드를 찾는다.
					//업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준)
		            String sRTN_BOOKOUT_LOC = null;
		            recInPara.setField("YD_GP", 			szYD_TO_LOC_GUIDE.substring(0,1));
		            recInPara.setField("YD_BAY_GP", 		szYD_TO_LOC_GUIDE.substring(1,2));
		            recInPara.setField("YD_STK_BED_NO",     szYD_TO_LOC_GUIDE.substring(6,8));
		            
		            String szPTOP_PLNT_GP = "PB";//디폴트 셋팅
		            
		            //업무기준 : YDB674 (후판제품창고 BOOKOUT_LOC 결정 기준) 야드구분값 셋팅
					if("PA".equals(szPTOP_PLNT_GP)){
						recInPara.setField("YD_GP", 	"K");
					}else{
						recInPara.setField("YD_GP", 	"T");
					}
					
		            String sTrackingGbn = null;
		            
			    	if( GetBreRule6.getYDB674(recInPara) ) {
			    		
			    		sRTN_BOOKOUT_LOC = StringHelper.evl(recInPara.getFieldString("YDB674_RV01_YD_BOOK_OUT_LOC"), "00000"); // 업무기준 YDB674 반환값#1 YD_BOOK_OUT_LOC
			    		
						//************************************************
						//**    2후판 동간이적 목적동 BOOK OUT 위치를 찾는다.   **
						//************************************************	
			    		if("C".equals(szYD_SCH_CD.substring(5,6))) {
							sTrackingGbn = "68";	// C R/T  - OFF-LINE
						}else if("B".equals(szYD_SCH_CD.substring(5,6))) { 
							sTrackingGbn = "66";	// B R/T  - ON-LINE
						}else {
							sTrackingGbn = "67";	// A R/T  - ON-LINE
						}	
						
						sRTN_BOOKOUT_LOC = sTrackingGbn + sRTN_BOOKOUT_LOC.substring(2);
						
						if(szYD_TO_LOC_GUIDE.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "B")) {
							//B동은 A-RT일 경우 Transfer Zone 코드로 변경한다.- 그외는 압연지시수신시 결정된 BOOK-OUT 위치를 그대로 사용한다.
							
							if( "67020".equals(sRTN_BOOKOUT_LOC)) {
								sRTN_BOOKOUT_LOC = "67216";
							} else if( "67010".equals(sRTN_BOOKOUT_LOC)) {
								sRTN_BOOKOUT_LOC = "67206";
							}
						
						} else if (szYD_TO_LOC_GUIDE.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "C")) {
							//C동은 B-RT일 경우 Transfer Zone 코드로 변경한다. - 그외는 압연지시수신시 결정된 BOOK-OUT 위치를 그대로 사용한다.
							
							//C동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < 18,601 이면 02BED -- 2013.11.26 
							if(szYD_TO_LOC_GUIDE.endsWith("01") && dYD_MTL_L < 18601) {
								sRTN_BOOKOUT_LOC = sTrackingGbn + "035";
							}
							
							//#2UT 대상재 QA검사장에서 올릴때...
							if("C".equals(szSTL_PROG_CD)){
								
								if(dYD_MTL_L <= 6200) {
									sRTN_BOOKOUT_LOC = sTrackingGbn + "030";
								}else{
									sRTN_BOOKOUT_LOC = sTrackingGbn + "040";
								}
							}
							
							if("66040".equals(sRTN_BOOKOUT_LOC)) {
								sRTN_BOOKOUT_LOC = "66226"; //2013.11.26 추가됨
							} else if( "66035".equals(sRTN_BOOKOUT_LOC)) {
								sRTN_BOOKOUT_LOC = "66216";
							} else if( "66025".equals(sRTN_BOOKOUT_LOC) || "66030".equals(sRTN_BOOKOUT_LOC)) {
								sRTN_BOOKOUT_LOC = "66206";
							}
							
						} else if (szYD_TO_LOC_GUIDE.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "D")) {
							//D동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < Crane#1 Beam Min Size 이면 
							if(szYD_TO_LOC_GUIDE.endsWith("01") && dYD_MTL_L < 9220) { 
								sRTN_BOOKOUT_LOC = sTrackingGbn + "055"; // A:67055, B:66055
							}						
						} else if (szYD_TO_LOC_GUIDE.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "E")) {
							/*
							 * 24.09.06 임진후 기사 요청. E동 RT 에서  초단척재가 65, 80으로 들어가지않고 70,75 로만 들어감
							 * 이 기준을 단척재에도 적용하게끔 요청.
							 * --> 기준 찾아보니, 초단척 코드기준이 아닌 아래 조건문의 길이 기준으로 적용하고있음.
							 * 
							 * 24.09.20 임진후 기사 재요청. 이렇게 운용하다보니, 단척재가 75 zone, 초단척재가 70 으로 지시받은경우 겹침현상 발생
							 * RT zone 별로 크기는 7M. 단척채 9M 짜리가 들어올시, 센터 맞추면 위아래로 1M씩 초과 
							 * 단척재가 75 로 들어온경우 70 zone 에 1M 침범하여 정지하고 그상태에서 70 존에 재료가 들어오면 재료가 겹친다.
							 * 따라서, 단척재 (6801 ~ 9200) 는 70 zone 으로 가야한다. (어차피 65,80 은 안쓰니 70에 오는건 상관없음)
							 */	
							sApplyYnPI = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "001");
					
							if("Y".equals(sApplyYnPI)){
								//E동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < Crane#1 Beam Min Size 이면 
								if(szYD_TO_LOC_GUIDE.endsWith("01") && dYD_MTL_L < 6801) { 
									sRTN_BOOKOUT_LOC = sTrackingGbn + "075"; // A:67075, B:66075
								} else if ( szYD_TO_LOC_GUIDE.endsWith("01") && (dYD_MTL_L >= 6801 && dYD_MTL_L < 9200)){
									sRTN_BOOKOUT_LOC = sTrackingGbn + "070"; // A:67075, B:66075
								} else if(szYD_TO_LOC_GUIDE.endsWith("04") && dYD_MTL_L < 9200) {
									//E동은 입고예정위 Bed 구분이 = '04' 이고 제품길이 < Crane#1 Beam Min Size - 700 이면  03 Bed
									sRTN_BOOKOUT_LOC = sTrackingGbn + "070"; // A:67070, B:66070
								} else if(szYD_TO_LOC_GUIDE.endsWith("04") && dYD_MTL_L >= 6400) {
									//E동은 입고예정위 Bed 구분이 = '04' 이고 제품길이가 6400 이상일때   03 Bed
									sRTN_BOOKOUT_LOC = sTrackingGbn + "070"; // A:67070, B:66070
								}
							}
							else{
								//E동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < Crane#1 Beam Min Size 이면 
								if(szYD_TO_LOC_GUIDE.endsWith("01") && dYD_MTL_L < 6820) { 
									sRTN_BOOKOUT_LOC = sTrackingGbn + "075"; // A:67075, B:66075
								} else if(szYD_TO_LOC_GUIDE.endsWith("04") && dYD_MTL_L < (6820-700)) {
									//E동은 입고예정위 Bed 구분이 = '04' 이고 제품길이 < Crane#1 Beam Min Size - 700 이면  03 Bed
									sRTN_BOOKOUT_LOC = sTrackingGbn + "070"; // A:67070, B:66070
								} else if(szYD_TO_LOC_GUIDE.endsWith("04") && dYD_MTL_L >= 6400) {
									//E동은 입고예정위 Bed 구분이 = '04' 이고 제품길이가 6400 이상일때   03 Bed
									sRTN_BOOKOUT_LOC = sTrackingGbn + "070"; // A:67070, B:66070
								}
							}
							
							/*
							 * 2016.08.01 윤재광 
							 * 노형준 요청 01베드 위치시 02으로 강제변경 요청
							 */
							if("66065".equals(sRTN_BOOKOUT_LOC)){
								sRTN_BOOKOUT_LOC = "66070";
							}else if("67065".equals(sRTN_BOOKOUT_LOC)){
								sRTN_BOOKOUT_LOC = "67070";
							}
							
						} else if (szYD_TO_LOC_GUIDE.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "F")) {
							//F동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < 11,340 이면 
							//if(sRTN_BOOKOUT_LOC.endsWith("01") && dYD_MTL_L < 11340) {
							//F동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < 12,670 이면 
							//if(szYD_TO_LOC_GUIDE.endsWith("01") && dYD_MTL_L < 12670) {
							//F동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < Crane#1 Beam Min Size - 800 이면  03 Bed
							if(szYD_TO_LOC_GUIDE.endsWith("01") && dYD_MTL_L < (12670-800)) {
								sRTN_BOOKOUT_LOC = sTrackingGbn + "095"; // A:67095, B:66095 <--2013.09.09 66090이 66095 으로 변경
								//sRTN_BOOKOUT_LOC = sTrackingGbn + "090"; // A:67090, B:66090 <--2013.08.02 66095가 66090 으로 변경 
							}
						} else if (szYD_TO_LOC_GUIDE.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD + "G")) {
							//G동은 입고예정위 Bed 구분이 = '01' 이고 제품길이 < 9,220 이면  + '03' 이고 제품길이 > 9,220 이면(2013.11.25 or 조건 추가)
							//if((szYD_TO_LOC_GUIDE.endsWith("01") && dYD_MTL_L < 9220) || (szYD_TO_LOC_GUIDE.endsWith("03") && dYD_MTL_L > 9220)) { //2014.07.31
							if((szYD_TO_LOC_GUIDE.endsWith("01") && dYD_MTL_L < 13820) || (szYD_TO_LOC_GUIDE.endsWith("03") && dYD_MTL_L > 13820)) {
								//sRTN_BOOKOUT_LOC = sTrackingGbn + "110"; // A:67110, B:66110
								sRTN_BOOKOUT_LOC = sTrackingGbn + "115"; // A:67115, B:66115 <--2013.09.11 66110 이 66115 로 변경
							}
							
							/*
							 * 2016.08.01 윤재광 
							 * 노형준 요청 무조건 115로 셋팅
							 */
							sRTN_BOOKOUT_LOC = sRTN_BOOKOUT_LOC.substring(0,2) + "115"; 	
						}
						
			    	} else {
			    		sRTN_BOOKOUT_LOC ="00000";
			    	}		
			    	
		            
	    			if("0000".equals(sRTN_BOOKOUT_LOC)) {
						szMsg = "[" + szOperationName + "] BOOK-OUT 위치를 찾지 못했습니다! " + sRTN_BOOKOUT_LOC;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
			            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	  
			        	m_ctx.setRollbackOnly();
		            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdCrnsch ERROR =" + szMsg);
		            }
	    			
					szMsg = "[" + szOperationName + "] 작업예약ID:"+szYdWbookId + " 작업예약 테이블에 야드To위치Guide 값  YD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE +" 의한 BOOK-OUT 위치 : " + sRTN_BOOKOUT_LOC;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	  
		            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	  
		            
		            
		            String[] arrRT_ZONE_NO = null;
		            //arrRT_ZONE_NO 		= YdCommonUtils.getY4PilingZoneNo2StrLoc(sRTN_BOOKOUT_LOC);
					//56216 을  KBTF02-16 으로 해석하면 안되고 KBRTRA-40 으로 변환되어야 한다. 
					arrRT_ZONE_NO 		= YdCommonUtils.getY4BookOutLoc(sRTN_BOOKOUT_LOC); 		            
		            
		            //RT상의 From 위치 
		            //  szYD_DN_WR_LOC.substring(0, 6) --적치열구분
		            //  szYD_DN_WR_LOC.substring(6) --적치배드번호
		            //RT상의 To 위치
		            //  arrRT_ZONE_NO[0] -- 적치열구분
		            //  arrRT_ZONE_NO[1] -- 적치배드번호	    			
		            
	    			//-----------------------------------------------------------------------------------------------------------------
	    			//  - 3) 목적동RT 가상버퍼에 대상제 삽입 (RT상의 대상제 삭제처리 포함)
		            
					//BOOKOUT 위치로 입력가능한 가상베드의 적치열구분과, 적치Bed를 구한다.
		            recInPara = JDTORecordFactory.getInstance().create();
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					
					recInPara.setField("YD_STK_COL_GP", arrRT_ZONE_NO[0]);
					recInPara.setField("YD_STK_BED_NO", arrRT_ZONE_NO[1]);
					
					/*com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0047*/
					intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0047");				
					
	            	if(intRtnVal<=0) {
						szMsg = "[" + szOperationName + "] BOOKOUT 위치 : "+sRTN_BOOKOUT_LOC + " 의 입력가능한 가상베드 값 조회 실패!!!" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	  
			            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	  
			        	m_ctx.setRollbackOnly();
		            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdCrnsch ERROR =" + szMsg);
	            	}		
	            	
	            	rsResult.first();
	            	recInPara = rsResult.getRecord();
	            	String szTO_LOC_BED_NO = ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");				
		            
		            recInPara.setField("FROM_STK_COL_GP",	szYD_DN_WR_LOC.substring(0, 6)); //RT상의  적치열구분(FROM)
		            recInPara.setField("FROM_BED_NO"	,	szYD_DN_WR_LOC.substring(6));    //RT상의 적치BED(FROM)
	            	//크래인스케줄ID로 크래인작업재료에서 대상제를 구해 목적동RT 가상버퍼에 삽입한다.
	            	recInPara.setField("YD_CRN_SCH_ID"	, 	szCrnSchId);					//크래인 스케줄ID
		            recInPara.setField("TO_STK_COL_GP"	,	arrRT_ZONE_NO[0]);				//북아웃위치 적치열구분(TO)  
		            recInPara.setField("TO_BED_NO"		,	szTO_LOC_BED_NO);   			//북아웃위치 적치BED(TO) 
					
		          //-----------------------------------------------------------------------------------------------------------------
					if(szYD_TO_LOC_GUIDE.startsWith("TC0101")||szYD_TO_LOC_GUIDE.startsWith("TC0102")||
        			   szYD_TO_LOC_GUIDE.startsWith("TC0103")||szYD_TO_LOC_GUIDE.startsWith("TC0104")||
        			   szYD_TO_LOC_GUIDE.startsWith("TC0105")){	
						/*
			    		 * 2015.09.09 윤재광
			    		 * 2후판 제품창고 #2UT야드로 동간이적시 기존 RT TO위치 가상베드에 이적대상 입력하지 않는다.
			    		 */	
		        	}else{
		        		intRtnVal = commDao.update(recInPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0023");
						
						if(intRtnVal < 1) {
							
							szMsg = "[" + szOperationName + "] RT TO위치 가상베드에 이적대상 입력 실패!!!" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	  
				            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	  
				        	m_ctx.setRollbackOnly();
			            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdCrnsch ERROR =" + szMsg);
						}	
					}
					
					intRtnVal = commDao.update(recInPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0024");
					
					if(intRtnVal < 1) {
						szMsg = "[" + szOperationName + "] RT FROM위치  이적대상 삭제 실패!!!" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	  
			            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	  
			        	m_ctx.setRollbackOnly();
		            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdCrnsch ERROR =" + szMsg);
					}			            
		            
	    			//-----------------------------------------------------------------------------------------------------------------
					if(szYD_TO_LOC_GUIDE.startsWith("TC0101")||szYD_TO_LOC_GUIDE.startsWith("TC0102")||
        			   szYD_TO_LOC_GUIDE.startsWith("TC0103")||szYD_TO_LOC_GUIDE.startsWith("TC0104")||
        			   szYD_TO_LOC_GUIDE.startsWith("TC0105")){	
						/*
			    		 * 2015.09.09 윤재광
			    		 * 2후판 제품창고 #2UT야드로 동간이적시 기존 예정위치 정보는 그대로 둔다.
			    		 */	
		        	}else{
					
						//  - 5) 야드저장품 UPDATE - YD_BOOK_OUT_LOC, YD_RCPT_PLN_STR_LOC
		        		//       YD저장품에 PL_RCPT_TRK_NO 설정: 이유 - 동간입고/이적작업시 크레인 파일링 문제발생 (2016.01.22 윤재광 , DB Lock문제로 아래의 쿼리에 포함) 
		        		recInPara.setField("PL_RCPT_TRK_NO"			, 	sTrackingGbn + YdUtils.getCurDate("yyyyMMddHHmmss")); 
			            recInPara.setField("YD_BOOK_OUT_LOC"		,	sRTN_BOOKOUT_LOC); 		//YD_BOOK_OUT_LOC
			            recInPara.setField("YD_RCPT_PLN_STR_LOC"	,	szYD_TO_LOC_GUIDE);     //TO위치가이드
			            recInPara.setField("MODIFIER"				,	szYD_SCH_CD);     		//수정자
			            recInPara.setField("YD_STK_COL_GP"			,	arrRT_ZONE_NO[0]);		//북아웃위치 적치열구분(TO)  
			            recInPara.setField("YD_STK_BED_NO"			,	szTO_LOC_BED_NO);   	//북아웃위치 적치BED(TO) 
						
						intRtnVal = commDao.update(recInPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0013");
						
						if(intRtnVal < 1) {
							
							szMsg = "[" + szOperationName + "] RT TO위치 가상베드에 적치된 대상들의 STOCK 변경 실패!!!" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	  
				            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	  
				        	m_ctx.setRollbackOnly();
			            	throw new JDTOException("<procY4CrnUdWr> Y4UpdYdCrnsch ERROR =" + szMsg);
						}	
			        }
	    			//-----------------------------------------------------------------------------------------------------------------
					//  - 4) L2로 ROUTING 지시 전송
					szMsg= "["+ szOperationName +"] 최상단제품  [" + szPL_MTL_NO + "]에 대한 L2 라우팅지시 [YDY8L006] 송신 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					
					arrRT_ZONE_NO 	= YdCommonUtils.getY4PilingZoneNo2StrLoc(sRTN_BOOKOUT_LOC);
					
					/*
					 * 2후판 #2 RT B동 파일링 권하작업시 R/T상 현재위치 정보 변환
					 * TBAP0201 → TBRTRA15
					 */
					String sStartZone = "";
					
					if("TBAP0201".equals(szYD_DN_WR_LOC)){
						sStartZone = "TBRTRA15";
					}else{
						sStartZone = szYD_DN_WR_LOC;
					}
					
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setField("MSG_ID"			, "YDY8L006"); 	// ROUTING 정보
					recInPara.setField("INFO_GP"		, "3");    		// 3:이적
					recInPara.setField("STL_NO"			, szPL_MTL_NO); // 최상단재료번호
					recInPara.setField("CURR_LOC"		, sStartZone);	//RT상의 현재위치 (FROM) 
					recInPara.setField("AIM_LOC1"		, arrRT_ZONE_NO[0] + arrRT_ZONE_NO[1]);  //재료가 갈 최종 RT샹의 위치
					recInPara.setField("AIM_LOC2"		, "");	
					
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// recInPara 에 logId 추가
					recInPara.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
					ydDelegate.sendMsg(recInPara);		
					
					szMsg= "["+ szOperationName +"] 최상단제품  [" + szPL_MTL_NO + "]에 대한 L2 라우팅지시 [YDY8L006] 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	
					
					//-----------------------------------------------------------------------------------------------------------------					
            	}
            }
            //------------------------------------------------------------------------------------------------------------------------
	        
	        
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			후판제품야드L2 크레인작업실적응답 전송  - YDY4L005
	         * 업무기준 Desc : 크레인 권하실적처리 성공 후 크레인작업실적응답 전송
	         * 기능 추가 : 임춘수
	         * 일자 : 2009.06.19
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	        recInTemp = JDTORecordFactory.getInstance().create(); 
		    // 전사물류개선 2021. 1. 6 추가(Y9시스템 전송여부)
			isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
		        recInTemp.setField("MSG_ID"        		, "YDY8L005");
		     // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
	        	if (isSendToEaiY9){
	        		recInTemp.setField("MSG_ID"        , "YDY9L005");
	        	}
			} else {
		        recInTemp.setField("MSG_ID"        		, "YDY4L005");
			}
			
	        recInTemp.setField("YD_EQP_ID"     		, szYD_EQP_ID);										//야드설비ID
	        recInTemp.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_DN_CMPL);					//야드작업진행상태
	        recInTemp.setField("YD_SCH_CD"   		, szYD_SCH_CD);										//야드스케줄코드
	        recInTemp.setField("YD_CRN_SCH_ID"   	, szCrnSchId);										//야드크레인스케줄ID
	        //==================================================================================
	        // 수신전문의 진행상태가 강제권하(5)일시 응답으로는 강제권하값(F)로 내려보내야 됨
	        //==================================================================================	        
	        if(szYD_WRK_PROG_STAT.equals("4")){
		        recInTemp.setField("YD_L2_WR_GP"	, YdConstant.CRN_WRK_RE_DN_WR);						//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        } else {
	        	recInTemp.setField("YD_L2_WR_GP"	, YdConstant.CRN_WRK_RE_FRCE_DN);					//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하	        	
	        }
	        
	        recInTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);					//야드L3처리결과코드
			
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// recInTemp 에 logId 추가
	        recInTemp.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
			ydDelegate.sendMsg(recInTemp);
			szMsg = "[권하실적처리]후판제품야드L2 크레인작업실적응답[YDY8L005/YDY9L005] 전송 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	
            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인 설비상태 권하완료(4) 셋팅.
	        //-------------------------------------------------------------------------------------------------------------------
            recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	        recInTemp.setField("YD_EQP_STAT", "4");
            intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
            if(intRtnVal < 0){
            	m_ctx.setRollbackOnly();
            	throw new JDTOException("<procY4CrnUdWr> updYdEqp ERROR CODE =" + intRtnVal);
            }
			
	        //설비id로 설비Table조회
	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	        rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	        intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
	        if(intRtnVal < 0){
	        	m_ctx.setRollbackOnly();
            	throw new JDTOException("<procY4CrnUdWr> getYdEqp ERROR CODE =" + intRtnVal);
            }
			
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord(0));
			szYD_EQP_ID  = recOutTemp.getFieldString("YD_EQP_ID");
			String sCrnWrkMode2        = recOutTemp.getFieldString("YD_EQP_WRK_MODE2");  // AT000 물류시스템 개선 2022.10.27
			String szYD_EQP_WRK_MODE  = recOutTemp.getFieldString("YD_EQP_WRK_MODE");  // AT000 물류시스템 개선 2022.10.27
			
			/*
			 * 이력테이블등록호출 szYD_DN_WR_LOC.substring(2, 4).equals("RT")
			 */
			{
				CrnSchSeEJBBean crnSchSeEJBBean = new CrnSchSeEJBBean();
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",             "");
				recInTemp.setField("YD_WBOOK_ID",        szYdWbookId);
				recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId);
				recInTemp.setField("YD_CAR_SCH_ID",      szYD_CAR_SCH_ID);
				recInTemp.setField("YD_TCAR_SCH_ID",     "");
				recInTemp.setField("YD_WTCL_TNK_SCH_ID", "");
				crnSchSeEJBBean.procWorkHistoryCreate(recInTemp);
				
				szMsg="[" + szOperationName + "] 이력테이블등록호출";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
			}
			// AT000 물류시스템 개선 2022.10.27 Start
			  // B동 B3,B4 무인mode RT/TF 입고 시 협폭 2100mm이하 일 경우 중폭 베드에서 정렬 후 최종 TO위치 
				// 권하를 위한 최종 위치 작업예약 정보 등록 szYD_TO_LOC_GUIDE1 김기태
			szMsg = "[권하실적처리] 협폭 2100mm이하 최종 목적 bed 작업지시 생성 시작 최종위치 : " +  szYD_TO_LOC_GUIDE1 + " 크레인운전모드 : " + sCrnWrkMode2 + " 폭 : " + dblYD_EQP_WRK_W;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            
            String szFlag = null;
         //   if(!(PlateGdsYdUtil.isApplyYn("소폭제 중간 BED 사용 신규로직 적용 여부"))){	
            	if ("TBCRB3".equals(szYD_EQP_ID) || "TBCRB4".equals(szYD_EQP_ID)){
            		//소폭재 2250 - => 2100 변경 김상대 주임님요청
					if ("RT".equals(szYD_UP_WR_LOC.substring(2, 4))  && dblYD_EQP_WRK_W <= 2100 ){
						if (!"".equals(szYD_TO_LOC_GUIDE1)){
							rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
							JDTORecord recPara  = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_WBOOK_ID", "1");
							//작업예약ID 생성
							intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult, 1);
							if (intRtnVal < 1) {
								//return;
								throw new JDTOException("[권하실적처리] 작업예약ID 생성 에러(협폭 중간경유 후 최종목적 bed Set)");
							}
							//레코드추출
							//String szYdWbookId1 = szYdWbookId;
							rsResult.first();
							recPara = rsResult.getRecord();
							//작업예약ID
							szYdWbookId = recPara.getFieldString("YD_WBOOK_ID");
								
							szMsg = "[권하실적처리] 협폭 2100mm이하 최종 목적 bed 작업예약 생성 WBOOK_ID : " +  szYdWbookId;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				           			            
							//INSERT할 항목 SET
				            recOutTemp  = JDTORecordFactory.getInstance().create();
				            String szYD_GP     = 	 szYD_DN_WR_LOC.substring(0,1);
				            String szYD_BAY_GP = 	 szYD_DN_WR_LOC.substring(1,2);
				            
                			String sApplyYnPIs = ydPICommDAO.ApplyYnPI("", szOperationName, "APP060", "T", "024"); //1후판 입고 이적/분할 적용 여부
                			
                			if("Y".equals(sApplyYnPIs)){
                				
    							String prefix = (szYD_STK_COL_GP != null && szYD_STK_COL_GP.length() >= 2) ? szYD_STK_COL_GP.substring(0, 2) : ""; //TB

    				            if ("TBCRB3".equals(szYD_EQP_ID)){ // 스케쥴코드 Set
    	                            szYD_SCH_CD = prefix   + "RT11MM"; 
    	                        }
    	                        else
    	                        {
    								/**
    								 * 2025.10.27 추관식
    								 * [입고이적 (B동 3, 4호기 크레인의 경우)] 
    								 * 스케즐 작업 취소하고 다시 생성시, 기존 로직으로 처리
    								 * (4호기 작업을 3호기한테 넘겨줬는데, 스케줄 취소 후 스케줄 재기동시, 조건 확인안하고 4호기 그대로 작업) 
    								 * 
    								 * 스케줄 작업 취소하지 않았을시, 아래와 같음
    								 *  B3			B4
    								 *  대기[W]     대기[W]   >  B4 입고 (RT > 중간경유베드), B3 입고이적  (중간경유베드 > 최종목적지 소폭) 
    								 *  대기     (출하)작업    >  B3 입고 (RT > 중간경유베드), B3 입고이적  (중간경유베드 > 최종목적지 소폭)
    								 *  작업     대기    	   >  B4 입고 (RT > 중간경유베드), B3 입고이적  (중간경유베드 > 최종목적지 소폭)
    								 *  작업     작업    	   >  B4 입고 (RT > 중간경유베드), B3 입고이적  (중간경유베드 > 최종목적지 소폭)
    								 * **/
    								JDTORecord crnPara  = JDTORecordFactory.getInstance().create();
    								JDTORecordSet rsCrnResult  = JDTORecordFactory.getInstance().createRecordSet("");
    								crnPara.setField("YD_WBOOK_ID", szYdWbookId);
    								
    								//com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbook
    								intRtnVal = ydWrkbookDao.getYdWrkbook(crnPara, rsCrnResult, 0); //쿼리에 취소코드 [SCH_CNCL_YN] 추가
    								
    			    				boolean isCancelled;

    								if (intRtnVal < 1) {
    									isCancelled = true; // 취소거나 없거나
    			                        szMsg = "[권하실적처리] B동 Crane - 기존 작업예약이 존재하지 않음";
    			                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    								} else {
        								rsCrnResult.first();
        								JDTORecord resultRowRecord = JDTORecordFactory.getInstance().create();
        								
        								resultRowRecord = rsCrnResult.getRecord();
        								String szSchCnclYn = ydDaoUtils.paraRecChkNull(resultRowRecord, "SCH_CNCL_YN");
        								isCancelled = "Y".equalsIgnoreCase(szSchCnclYn); //취소됐을경우 true, 살아있는 경우 false
    								}

    								boolean isTypeF = (szYD_STK_COL_GP != null && szYD_STK_COL_GP.length() > 5) && szYD_STK_COL_GP.substring(5).equalsIgnoreCase("F");  //F R/T 여부
    								
    								if(!isCancelled) { //취소가 아닐 경우
    									szMsg = "[권하실적처리] B동 Crane - 취소가 아닐경우 [ isCancelled = "+isCancelled+" ]";
    									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
    									
    									JDTORecord crnStatusPara  = JDTORecordFactory.getInstance().create();
    									JDTORecordSet rsCrnStatusResult  = JDTORecordFactory.getInstance().createRecordSet("");
    									
    									//com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCrnStatusConfirm
    									if(commDao.select(crnStatusPara, rsCrnStatusResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCrnStatusConfirm") > 0 ){
    										String crn3Status = null;
    										String crn4Status = null;
    										String crn4OutYn = "N";
    										int nRows = rsCrnStatusResult.size();
    										for( int i=0; i < nRows; i++){
    											JDTORecord recMtl = rsCrnStatusResult.getRecord(i);
    											String ydEqpId = recMtl.getFieldString("YD_EQP_ID");
    											String status = recMtl.getFieldString("YD_WRK_PROG_STAT");
    											if("TBCRB3".equals(ydEqpId)){
    												crn3Status = status;
    											} else if("TBCRB4".equals(ydEqpId)){
    												crn4Status = status; //현재 작업상태
    												if("TBPT40UM".equals(recMtl.getFieldString("YD_SCH_CD"))){  //현재 출하작업 여부
    													crn4OutYn = "Y";
    												}
    											}
    										}

    										//B3가 대기상태이면서, B4가 대기상태가 아닐경우
    										if(crn3Status.equals("W") &&  !crn4Status.equals("W")){
    											szYD_SCH_CD = prefix + "RT11MM";
    										} else if(crn4OutYn == "Y"){ // 4호기가 출하작업
    											szYD_SCH_CD = prefix + "RT11MM";
    										} else {
    											szYD_SCH_CD = prefix + "RT12MM";     
    										}
    									} else {
    										szYD_SCH_CD = isTypeF ? prefix + "RT11MM" : prefix + "RT12MM";
    									}
    								} else {
    									//20250923 : 추관식 > B4호기 크레인이 F R/T 입고를 받고 이적을 진행시 입고이적을 B3호기로 변경
    									szYD_SCH_CD = isTypeF ? prefix + "RT11MM" : prefix + "RT12MM";
    								}
    	                        }
                				
                			} else {
    				            if ("TBCRB3".equals(szYD_EQP_ID)){ // 스케쥴코드 Set
    	                            szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2)   + "RT11MM"; 
    	                        }
    	                        else
    	                        {
    	                            szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2)   + "RT12MM";     
    	                        }
                			}
				            
				            recOutTemp.setField("YD_WBOOK_ID",          szYdWbookId);			            
				            recOutTemp.setField("YD_GP", 		        szYD_GP);
				            recOutTemp.setField("YD_BAY_GP", 	        szYD_BAY_GP);			            
				            recOutTemp.setField("YD_SCH_PRIOR", 	    szYD_SCH_PRIOR);
				            recOutTemp.setField("YD_SCH_CD", 	        szYD_SCH_CD);
				            recOutTemp.setField("YD_AIM_YD_GP", 	    szYD_GP);
				            recOutTemp.setField("YD_AIM_BAY_GP", 	    szYD_BAY_GP);
				            recOutTemp.setField("YD_TO_LOC_DCSN_MTD", 	"F");
				            recOutTemp.setField("YD_TO_LOC_GUIDE", 	    szYD_TO_LOC_GUIDE1);
				            recOutTemp.setField("YD_WRK_PLAN_TCAR", 	szYD_EQP_ID);
				            recOutTemp.setField("REGISTER", 	        "");
							
							szMsg = "[권하실적처리] 협폭 2100mm이하 최종 목적 bed 작업지시 생성 Start  ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
						
							//작업예약 INSERT
							intRtnVal = ydWrkbookDao.insYdWrkbook(recOutTemp);
							if (intRtnVal < 1) {
								szMsg = "[권하실적처리] 작업예약 데이터 등록 중 에러(협폭 중간경유 후 최종목적 bed Set)";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
								ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
								//return;
								throw new JDTOException(szMsg);
							}
							szMsg = "[권하실적처리] 협폭 2100mm이하 최종 목적 bed 작업지시 생성 End  ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        
				            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);				        
					         //조회한 작업예약재료1매씩 저장품 업데이트
							for( int Loop_i = 1; Loop_i <= getRecSetWb.size(); Loop_i++ ) {
									
								getRecSetWb.absolute(Loop_i);
								//INSERT 항목 record 생성
								recOutTemp = JDTORecordFactory.getInstance().create();
								recInTemp = JDTORecordFactory.getInstance().create();
								recOutTemp.setRecord(getRecSetWb.getRecord());
								
								szSTL_NO = ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO");
								szYD_STK_COL_GP = szYD_DN_WR_LOC.substring(0,6);
							    szYD_STK_BED_NO = szYD_DN_WR_LOC.substring(6);
							
								//작업예약재료 정보 SET
								recInTemp.setField("YD_WBOOK_ID",     szYdWbookId);
								recInTemp.setField("YD_STK_COL_GP",   szYD_STK_COL_GP);
								recInTemp.setField("YD_STK_BED_NO",   szYD_STK_BED_NO);
								recInTemp.setField("STL_NO", 		  szSTL_NO);
								recInTemp.setField("YD_UP_COLL_SEQ", "1");
								
								szMsg = "[권하실적처리] 협폭 2100mm이하 최종 목적 bed 작업예약 생성 STL_NO : " +  szSTL_NO;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
									
								//작업예약재료 테이블에 등록한다.
								intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recInTemp);
									
								if (intRtnVal < 1) {
									szMsg = "[권하실적처리] 작업예약 재료 데이터 등록 중 에러(협폭 중간경유 후 최종목적 bed Set)";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
									ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
									//return;
									throw new JDTOException(szMsg);
								}
								else
								{
								  szFlag = "Y";
								}
							}
						}
					}
				}
				szMsg = "[권하실적처리] 협폭 2100mm이하 최종 목적 bed 작업지시 생성 종료" ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
//            }
 
			/*
			 * 크레인 작업지시 요구호출.
			 */
			{
				
//SJH03004
			if ("Y".equals(szFlag)){   // AT000 물류시스템 개선 2022.10.27 입고 중간경유 후 최종 목적 bed 작업지시 편성
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("JMS_TC_CD","YDYDJ506");
				recInTemp.setField("YD_EQP_ID"    	, szYD_EQP_ID);
				recInTemp.setField("YD_SCH_CD"    	, szYD_SCH_CD);
				recInTemp.setField("YD_WBOOK_ID"    , szYdWbookId);	
				//ydDelegate.sendMsg(recInTemp);  //기존 SEND방식으로는 스케줄 생성시점 정합성 문제로 TRX구조로 변경 필요.
				
				if(1==2){  //아래처럼 trx 구조로 호출되도록 변경 필요.
//					String szEjbJndiName 	= "CraneLdHdSeEJB";
//					String szEjbMethod 		= "procY4CrnWrkOrdReq";
					
//					szLogMsg = "[후판제품권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					
//					EJBConnector ejbConn = new EJBConnector("default", szEjbJndiName, this);			
//					String szRtnMsg = (String)ejbConn.trx(szEjbMethod, new  Class[] { JDTORecord.class }, 
//																	   new Object[] { recInTemp });
//					if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
//						szLogMsg = "[후판제품권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					}else{
//						szLogMsg = "[후판제품권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
//					}
				}
				else{
					ydDelegate.sendMsg(recInTemp);  //기존 SEND방식으로는 스케줄 생성시점 정합성 문제로 TRX구조로 변경 필요.
				}
			}
			
			// AT000 물류시스템 개선 2022.10.27 End
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",           "YDYDJ642");
			 //recInTemp.setField("YD_EQP_ID",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID"));  AT000 물류시스템 개선 막음 2022.11.16
		     //recInTemp.setField("YD_EQP_WRK_MODE",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE"));
			recInTemp.setField("YD_EQP_ID",        szYD_EQP_ID);
			recInTemp.setField("YD_EQP_WRK_MODE",  szYD_EQP_WRK_MODE);
			recInTemp.setField("YD_WRK_PROG_STAT", "4");
			recInTemp.setField("YD_SCH_CD",        szYD_SCH_CD);
			recInTemp.setField("YD_CRN_SCH_ID",    "");
			recInTemp.setField("YD_CRN_XAXIS",     "");
			recInTemp.setField("YD_CRN_YAXIS",     "");
			recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_SUCCESS);
//				String szEjbJndiName 	= "CraneLdHdSeEJB";
//				String szEjbMethod 		= "procY4CrnWrkOrdReq";
//				
//				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//				 * ++++++ 데이타의 정합성 때문에 추가 - 아래 항목을 수정 시에는 수정자에게 문의 +++++++
//				 * 크레인스케줄이 삭제(DEL_YN)필드가 Y로 설정된 후 COMMIT전에 
//				 * 크레인작업지시모듈에서 조회 시 현재 Y로 설정된 동일한 크레인스케줄이 조회가 되어
//				 * 전문편집 모듈에 넘겨주고 다시 조회 시 Y로 COMMIT되는 경우가 생겨서 L2로 크레인작업지시가
//				 * 내려가지 않는 현상이 발생하여 EJB CALL로 변경. 
//				 * 수정자 : 임춘수
//				 * 수정일 : 2009.09.03
//				 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				szIS_EJB_CALL = "Y";
//				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				
//				if( szIS_EJB_CALL.equals("Y")) {
//					szLogMsg = "[후판제품권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					
//					EJBConnector ejbConn = new EJBConnector("default", szEjbJndiName, this);			
//					String szRtnMsg = (String)ejbConn.trx(szEjbMethod, new  Class[] { JDTORecord.class }, 
//																	   new Object[] { recInTemp });
//					if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || 
//						szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
//						szLogMsg = "[후판제품권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					}else{
//						szLogMsg = "[후판제품권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
//					}
//					//크레인작업지시 송신
//
//					szLogMsg = "[후판제품권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - JMS 1전문전송 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				}else{
//					//크레인작업지시 송신
//					ydDelegate.sendMsg(recInTemp);
//					szLogMsg = "[후판제품권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - JMS 전문전송 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				}
			}
			
			
	        //-- 해송 출하 가동 이후 권하실적 발생시 SP_YD_SHIPSEL_001 프로시져 호출 ------------------------------------- 
			if(iSP_YD_SHIPSEL_001_callCnt == 0) {
		    		
	    		if( szYD_DN_WR_LOC.substring(2, 4).matches("\\d\\d") ) {
	    			//후판해송출하적용여부가 'Y'이고 권하위치가 야드일경우 프로시져 호출
	    			
	    			//YdPlateCommDAO  commDao 	  = new YdPlateCommDAO();
	    			
	    			Object[] inParam = { 
	    					 "*"
	    					,szYD_DN_WR_LOC.substring(0,6)
	    					,szYD_DN_WR_LOC.substring(6,8)
	    				   };			
	    			
	    			int[] inParamIndex = {1,2,3};
	    			
	    			JDTORecord record = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.callSpQueryId_0004");
	    			
	    			if(record == null || record.size() <= 0){
						szMsg="[권하실적처리] SP_YD_SHIPSEL_001 프로시져 호출시  Error!! " ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
	    			}
	    		}
	    	}
	    	//------------------------------------------------------------------------------------------------			
			
			
			//------------------------------------------------------------------
	        // 권하 실적시 Flex 실시간 처리
	        //------------------------------------------------------------------
	        JDTORecord    recFlex             = JDTORecordFactory.getInstance().create();
	        
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
			 	recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE2_GDS_YARD);
			} else {
			 	recFlex.setField("YD_GP",  YdConstant.YD_GP_PLATE_GDS_YARD);
			}
	        
		 	recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
		 	recFlex.setField("YD_UP_WR_LOC", szYD_UP_WR_LOC);
		 	recFlex.setField("YD_DN_WR_LOC", szYD_DN_WR_LOC);
			szMsg="Flex 권하 완료 실적 전송";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		 	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		 	ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
    		ydUtils.putYdFlexCrnWrk("", recFlex);  
			
            // 전사물류개선 2021. 4. 7
			// L2 : 장치영이사, 조민주부장
			// 출하시 차량 재료의 권하실적 송신
    		try{
    			if( szYD_DN_WR_LOC.substring(2, 4).equals("PT")) {
    				JDTORecord sMsg = JDTORecordFactory.getInstance().create(); 
    				sMsg.setField("MSG_ID"        , "YDY9L010");
    				sMsg.setField("YD_CRN_SCH_ID"     , szCrnSchId);
    				sMsg.setField("CRN_JOB_RST_TYPE"   , "D");			//U:권상실적,P:권하실적
    				ydDelegate.sendMsg(sMsg);
    			}
    			// 2021. 5. 17 권하위치가 야드일경우
    			// 권하위치가 PT가 아닐시 실적위치와 실제위치가 다를경우 L2에 제원정보를 전송한
    			if(szYD_DN_WR_LOC.substring(2, 4).matches("\\d\\d")){
    				
    				if(intYdDnWrLayer != intRealTopLyr){
    					szMsg = "[" + szOperationName + "] 실적적치단[" + intYdDnWrLayer + "]과 실재야드적치단[" + intRealTopLyr + "]이 상이하여 L2에 제원정보를 전송처리한다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
    					
    					JDTORecord sMsg = null;
    					JDTORecord params = JDTORecordFactory.getInstance().create(); 
    					JDTORecordSet rsRecords = JDTORecordFactory.getInstance().createRecordSet("plateComm"); 
    					
    					params.setField("YD_CRN_SCH_ID", szCrnSchId);
    					if(commDao.select(params, rsRecords, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getCrnWrkMtlByCnrSchId") > 0 ){
    						int nRows = rsRecords.size();
    						for( int i=0; i < nRows; i++){
    							sMsg = JDTORecordFactory.getInstance().create(); 
    							sMsg.setField("YD_INFO_SYNC_CD", 	"5");						//1:동,2:SPAN,3:열,4:BED
    							sMsg.setField("YD_GP", 			YdConstant.YD_GP_PLATE2_GDS_YARD);
    							sMsg.setField("YD_STK_COL_GP", 	szYD_DN_WR_LOC.substring(0,6));
    							sMsg.setField("YD_STK_BED_NO", 	"");
    							sMsg.setField("STL_NO", 	rsRecords.getRecord(i).getFieldString("STL_NO")); 

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// sndStockSpecToL2 call 시  sMsg 에 logId SET 추가 개선
    							sMsg.setField("LOG_ID", logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
    							
    							YdCommonUtils.sndStockSpecToL2(sMsg); 
    						}
    					}
    				}
    			}
    		}catch(Exception ex) {}
            
        }catch(Exception e) {
        	/*
        	 * Exception 발생시에도 작업실적 응답은 송신.
        	 */
        	{
		        recInTemp = JDTORecordFactory.getInstance().create(); 
		        
				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)) { //--2013.02.07 추가 (3기)
			        recInTemp.setField("MSG_ID"          , "YDY8L005");
			        recInTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);	// 야드L3처리결과코드
			     // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
			        isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
		        	if (isSendToEaiY9){
		        		recInTemp.setField("MSG_ID"        , "YDY9L005");
		        		recInTemp.setField("YD_L3_MSG"        , "권하실적오류");
		        		recInTemp.setField("YD_L3_HD_RS_CD", "7777");	// 야드L3처리결과코드
		        	}
				} else {
			        recInTemp.setField("MSG_ID"          , "YDY4L005");
				}
				
		        recInTemp.setField("YD_EQP_ID"       , szYD_EQP_ID);						// 야드설비ID
		        recInTemp.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_DN_CMPL);		// 야드작업진행상태
		        recInTemp.setField("YD_SCH_CD"       , szYD_SCH_CD);						// 야드스케줄코드
		        recInTemp.setField("YD_CRN_SCH_ID"   , szCrnSchId);							// 야드크레인스케줄ID
		        if(szYD_WRK_PROG_STAT.equals("4")){
		        	recInTemp.setField("YD_L2_WR_GP", YdConstant.CRN_WRK_RE_DN_WR);			// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
		        } else {
		        	recInTemp.setField("YD_L2_WR_GP", YdConstant.CRN_WRK_RE_FRCE_DN);		// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하	        	
		        }	        
	
				
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// recInTemp 에 logId 추가
		        recInTemp.setField("LOG_ID", logId);  // 전문에 있는 logId
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
				ydDelegate.sendMsg(recInTemp);
				szMsg = "[" + szOperationName + "] A후판 제품야드L2 크레인작업실적응답[YDY4L005] 전송 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        	}
            szLogMsg = "[후판제품권하실적처리]권하실적 처리 에러발생 : " + e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			m_ctx.setRollbackOnly();
        	throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }finally{
        }

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
szLogMsg = "Y8크레인권하실적 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
        
        return recInTemp;
    }// end of procY4CrnUdWrTX()
    
    public JDTORecord procY4CrnUdWrTX2(JDTORecord msgRecord)throws JDTOException  {
    	
    	YdDelegate      ydDelegate      = new YdDelegate();
    	YdEqpDao        ydEqpDao        = new YdEqpDao();
    	YdStockDao      ydStockDao      = new YdStockDao();
    	YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
    	YdCrnWrkMtlDao  ydCrnWrkMtlDao  = new YdCrnWrkMtlDao(); 
    	YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();
    	YdStkColDao     ydStkColDao     = new YdStkColDao();
    	YdPlateCommDAO  commDao 		= new YdPlateCommDAO();
    	
        int intRtnVal = 0;
        String szOperationName              = "후판제품권하실적처리-다음북아웃재료 스케줄 선생성";
        
        //DATA SETTING시 사용
        JDTORecord setRecord 				= JDTORecordFactory.getInstance().create();
        
        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord                = JDTORecordFactory.getInstance().create(); ;
        
        //작업예약 업데이트 항목
        JDTORecord bookrecord               = JDTORecordFactory.getInstance().create();
        
        //크레인 스케줄 야드작업진행상태 비교		 (작업예약이 끝났는지 확인하기위해 카운터 값을 가져옴)
        JDTORecord outRecord                = JDTORecordFactory.getInstance().create();         
        
        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 			= JDTORecordFactory.getInstance().create();
       
        JDTORecord recInTemp                = null;
        JDTORecord recInTemp1               = null;
        JDTORecord recOutTemp               = null;
        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet getRecSetWb			= null;
        
        JDTORecordSet rsResult              = null;
        
        String szMsg                        = "";
        String szMethodName                 = "procY4CrnUdWrTX";
        
        //크레인 XYZ축 			파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
        String szYD_CRN_XAXIS     			= "";
        String szYD_CRN_YAXIS     			= "";
        String szYD_CRN_ZAXIS     			= "";
        //WBOOK_ID				작업예약 완료 처리시 사용
        String szYdWbookId              	= "";
        String szCrnSchId                   = "";
        
        String szYD_UP_WR_LOC               = "";
        String szYD_SCH_CD                  = "";
        String szYD_EQP_ID                  = "";
        String szYD_DN_WR_LOC               = "";
        String szYD_DN_WR_LAYER				= "";
        String szYD_CAR_SCH_ID				= "";
        String szYD_WRK_PROG_STAT			= "";
        String szSTL_NO						= null;
        String szBOOK_IN                    = "";
        int intYD_EQP_WRK_SH				= 0;
        
		//야드구분
        String szYdGp					= ""; //--2013.02.07 추가 (3기)
        double dYD_MTL_L				= 0;
        
        //AT000 물류시스템 개선 2022.10.27
        YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
        double dblYD_EQP_WRK_W       = 0; 
        String szYD_TO_LOC_GUIDE1          = ""; 
        String szYD_SCH_PRIOR              = ""; 
        
        //-----------------------------------------------------------------------------------------
	    //실제Lyr를 검사하여 처리하기 위해 필요한 변수들
	    JDTORecordSet getLyrSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
	    JDTORecord recInPara = null;
	      
        String szREAL_TOP_LYR               = "";
	    int    intRealTopLyr                = 0;
	    int    intYdDnWrLayer               = 0;
	    int    rowsize                      = 0;
        //-----------------------------------------------------------------------------------------
        
	    int iSP_YD_SHIPSEL_001_callCnt      = 0;
	    
        //EJB CALL or JMS CALL
        String szIS_EJB_CALL = null;
        String szLogMsg = null;
        
        //완산BED 설정 변경
        String szYD_STK_COL_GP = "";
        String szYD_STK_BED_NO = "";
     // 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
		boolean isSendToEaiY9           = false;
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if( szRcvTcCode==null || szRcvTcCode.equals("") ){
            szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            m_ctx.setRollbackOnly();
            throw new JDTOException("<procY1CrnUdWr> " + szMsg);
        }
        
        if(bDebugFlag){
            szMsg="["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
        
        try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "["+szOperationName+"] 권하실적처리 수신";
			ydUtils.putLogMsg("K", "yd_monitorK", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			//PIDEV			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "후판 권하실적처리 수신", "APPPI0", "T", "*");

        	szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
        	
        	//-------------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------------
	        intRtnVal = this.Y4ParamCheck(msgRecord, getCrnschRecord, 1) ;
	        
	        szCrnSchId 			= getCrnschRecord.getFieldString("YD_CRN_SCH_ID");
	        szYD_EQP_ID 		= getCrnschRecord.getFieldString("YD_EQP_ID");
	        szYD_SCH_CD 		= getCrnschRecord.getFieldString("YD_SCH_CD");
	        szYD_DN_WR_LOC		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC");
	        szYD_DN_WR_LAYER	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER");
	        szYD_WRK_PROG_STAT 	= getCrnschRecord.getFieldString("YD_WRK_PROG_STAT");
	        
        	
	        if( szCrnSchId.equals("") ) {
                szMsg = "["+szOperationName+"] 'YD_CRN_SCH_ID' Data Error	: 크레인스케줄ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                m_ctx.setRollbackOnly();
                throw new JDTOException("<procY4CrnUdWr> " + szMsg);
	        }
	        
	        //파라미터 레코드 편집
	        setRecord.setField("YD_CRN_SCH_ID",       	szCrnSchId);
	        setRecord.setField("YD_SCH_CD",           	szYD_SCH_CD);
	        setRecord.setField("YD_DN_WR_LOC",        	getCrnschRecord.getFieldString("YD_DN_WR_LOC"));
	        setRecord.setField("YD_DN_WR_LAYER",      	getCrnschRecord.getFieldString("YD_DN_WR_LAYER"));
	      
            
            //-------------------------------------------------------------------------------------------------------------------
	        //	크레인스케줄 작업재료 조회
            //-------------------------------------------------------------------------------------------------------------------
            szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        intRtnVal = this.Y4GetYdCrnsch(setRecord, getRecSet,3);
	       
	        if(intRtnVal < 0){
	        	m_ctx.setRollbackOnly();
            	throw new JDTOException("<procY4CrnUdWr> Y4GetYdCrnsch ERROR CODE =" + intRtnVal);
            }
	          
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();
	        
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 완료 - 대상재 건수 : " + getRecSet.size();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
            szYD_UP_WR_LOC 		= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
	        //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szYdWbookId 		= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
	        //권하실적위치
	        szYD_DN_WR_LOC 		= ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC");
	        //권하실적단
	        szYD_DN_WR_LAYER	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER");
	        szYD_TO_LOC_GUIDE1  = ydDaoUtils.paraRecChkNull(getRecord, "YD_TO_LOC_GUIDE");  // AT000 물류시스템 개선 2022.10.27 최종 적치bed
			//제품길이
			dYD_MTL_L 			= ydDaoUtils.paraRecChkNullDouble(getRecord, "YD_MTL_L"); 	     
			dblYD_EQP_WRK_W     = ydDaoUtils.paraRecChkNullDouble(getRecord, "YD_EQP_WRK_MAX_W"); 	// AT000 제품 폭 최대폭  
			szYD_SCH_PRIOR      = ydDaoUtils.paraRecChkNull(getRecord, "YD_SCH_PRIOR"); 	// AT000 우선순위
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하실적LYR와 실제적치단의 최상위 위치가 다르면 권하실적LYR를 변경하고 적치단 정보를 수정한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        szYD_STK_COL_GP = szYD_DN_WR_LOC.substring(0, 6);
	        szYD_STK_BED_NO = szYD_DN_WR_LOC.substring(6, 8);
	        intYdDnWrLayer 	= Integer.parseInt(szYD_DN_WR_LAYER);
	        	                
	        recInPara	= JDTORecordFactory.getInstance().create();
	        recInPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	        recInPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
	        
	        intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, getLyrSet, 98);
	        
	        if(intRtnVal < 0){
	        	m_ctx.setRollbackOnly();
            	throw new JDTOException("<procY4CrnUdWr> getYdStklyr ERROR CODE =" + intRtnVal);
            }
	        
	        getLyrSet.first();
	        recOutTemp = getLyrSet.getRecord();
	        
	        szREAL_TOP_LYR 	= ydDaoUtils.paraRecChkNull(recOutTemp,"REAL_TOP_LYR");
	        intRealTopLyr 	= Integer.parseInt(szREAL_TOP_LYR);
	        
	        
          //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      	getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("YD_DN_WR_LOC",       	getCrnschRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setRecord.setField("YD_DN_WR_LAYER",     	szYD_DN_WR_LAYER) ;
	        setRecord.setField("YD_DN_WRK_ACT_GP",   	getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP") );
	        setRecord.setField("YD_DN_WR_XAXIS",     	getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
	        setRecord.setField("YD_DN_WR_YAXIS",     	getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
	        setRecord.setField("YD_DN_WR_ZAXIS",     	getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
	        setRecord.setField("YD_WRK_PROG_STAT",   	getCrnschRecord.getFieldString("YD_WRK_PROG_STAT"));
	        setRecord.setField("YD_DN_CMPL_DT",      	YdUtils.getCurDate("yyyyMMddHHmmss"));
	        setRecord.setField("YD_GP",   				getCrnschRecord.getFieldString("YD_GP"));
	        
	        //--------------------------------------------------------------------------------------------------
	        //	크레인스케줄 등록 시 야드작업계상일자에 등록되는 값을 권하완료시점에 다시 수정, 권하완료 시의 값이 사용됨
	        //	TO_CHAR(SYSDATE - (7 / 24), 'YYYYMMDD')
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.23
	        //--------------------------------------------------------------------------------------------------
	        String szYD_WRK_HDS_DD				= YdUtils.getDefaultHdsDate();
	        
	        setRecord.setField("YD_WRK_HDS_DD",   		szYD_WRK_HDS_DD);
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]의 야드작업계상일자["+szYD_WRK_HDS_DD+"]";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	
            //-------------------------------------------------------------------------------------------------------------------
		    //	2010.04.14 윤재광 인터페에스 송신처리
	        //  각각의 I/F 송신조건은 다시 체크 요망.
	        //-------------------------------------------------------------------------------------------------------------------
            {
	            /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			출하관리 후판제품입고작업실적전송  - YDDMR002
		         * 업무기준 Desc : 1. RT입고
		         * 				  2. 차량하차 입고
		         * 				  3. 재료진도코드가 입고대기인 경우에만 전송
		         * 스케줄코드 :  1. RT입고 스케줄
		         * 				2. 차량하차 스케줄  : PT(팔렛트), TR(트레일러), LM(하차)
		         * 기능 추가 : 임춘수
		         * 일자 : 2009.06.15
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		        //권상실적위치, 스케줄코드, 크레인스케줄ID
		        szMsg="[" + szOperationName + "] 출하관리 후판제품입고작업실적전송 송신 전 판단 시작 - 권상실적위치["+szYD_UP_WR_LOC+"], 권하실적위치["+szYD_DN_WR_LOC+"], 스케줄코드["+szYD_SCH_CD+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		        
		        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            }            
        }catch(Exception e) {
            szLogMsg = "[후판제품권하실적처리]권하실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
        	throw new JDTOException(getClass().getName() + e.getMessage(), e);
        }finally{
        }
        return recInTemp;
    }// end of procY4CrnUdWrTX2()    
    
	/**
	 * 오퍼레이션명 : 차량작업진행관리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int procY4CarWrkStatCtr(JDTORecord msgRecord)throws JDTOException  {
		//TC_CODE :YDYDJ630
		
		YdDelegate ydDelegate = new YdDelegate();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao(); 
		YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
		YdCarSchDao     ydCarSchDao     = new YdCarSchDao();  
		YdStockDao      ydStockDao      = new YdStockDao();  
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecordSet rsCrnWrkMtl		= null;
		JDTORecord	  recMtl			= null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recFirst          = null;
		JDTORecord    recLast           = null;
		JDTORecord	  inRecord			= null;
		
	    int intRtnVal 		   			= 0 ;
	    
	    String szMsg           			= "";
	    String szMethodName    			= "procY4CarWrkStatCtr";
	    String szOperationName    		= "차량작업진행관리(후판제품)";
	    
	    String szCAR_LDUD_GP   			= "";
	    String szYD_WBOOK_ID   			= "";
	    String szYD_CRN_SCH_ID 			= "";
	    String szFST_CRN_SCH_ID 		= "";
	    String szLST_CRN_SCH_ID 		= "";
	    String szYD_SCH_CD      		= "";
	    String szYD_GP          		= "";
	    String szYD_CAR_SCH_ID  		= "";
	    String szYD_CAR_USE_GP  		= "";
	    String szYD_DN_WR_LOC   		= "";
	    String szCAR_NO					= null;
	    String szCARD_NO				= null;
	    String szCAR_KIND				= null;
	    String szSPOS_WLOC_CD			= null;
	    String szYD_PNT_CD				= null;
	    String szTRANS_ORD_DATE			= null;
	    String szTRANS_ORD_SEQNO		= null;
	    String sTelNo 					= null;
	    String szCARLD_PNT_CD			= "";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// logid get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg="["+ szOperationName +"] 차량 작업 진행관리 처리"+szMethodName+") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);				
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
	    
	    try{
	    	//--------------------------------------------------------------------------------------------------
	    	//	파라미터 확인
	    	//--------------------------------------------------------------------------------------------------
	    	szMsg="["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);				
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
//PIDEV_S :병행가동용:PI_YD		
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "procY4CarWrkStatCtr", "APPPI0", "T", "*");
			
			if("PIDEV".equals("PIDEV")) {
				intRtnVal = this.procY4CarWrkStatCtr_PIDEV(msgRecord);
				return intRtnVal;
			}
			
	    	//상하차구분 플래그, 작업예약id, 크레인스케줄id
	    	szCAR_LDUD_GP   = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_LDUD_GP"); 
	    	szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	    	szYD_DN_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC");
	    	//--------------------------------------------------------------------------------------------------
	    	
	    	//출하고도화 이후 파라메터로 크레인작업재료를 JDTORecordSet 형식으로 받는다.
	    	rsCrnWrkMtl = (JDTORecordSet)msgRecord.getField("CRN_WRK_MTLS_SET");
	    	
	    	//해송출하 이후 파라메터로 받은 권하실적위치를 가지고 상차포인트를 편집하여 사용한다.
	    	if(!"".equals(szYD_DN_WR_LOC)) {
	    		if(szYD_DN_WR_LOC.length()>=6) {
	    			szCARLD_PNT_CD = szYD_DN_WR_LOC.substring(0,1) + szYD_DN_WR_LOC.substring(4,5) + szYD_DN_WR_LOC.substring(1,2) + szYD_DN_WR_LOC.substring(5,6);
	    		}
	    	}
	    	
	    	
	    	//--------------------------------------------------------------------------------------------------
	    	// 작업예약ID로 크레인 스케줄 조회
	    	//--------------------------------------------------------------------------------------------------
	    	szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
//	    	recInTemp.setField("YD_EQP_GP", szYD_DN_WR_LOC.substring(2, 4));
	    	
	    	if(szCAR_LDUD_GP.equals("L")){
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 31);
		    	if(intRtnVal <= 0) {
					szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄을 조회 중 Error";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
					throw new JDTOException("<procY1CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}else{
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 32);
		    	if(intRtnVal <= 0) {
					szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄을 조회 중 Error";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
					throw new JDTOException("<procY1CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}
	    	
	    	
	    	szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄 조회 성공 - 대상재건수["+rsResult.size()+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	    	
			
	    	rsResult.first();
	    	recFirst = JDTORecordFactory.getInstance().create();
	    	recFirst.setRecord(rsResult.getRecord());
	    	szFST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recFirst, "YD_CRN_SCH_ID");
	    	
	    	rsResult.last();
	    	recLast = JDTORecordFactory.getInstance().create();
	    	recLast.setRecord(rsResult.getRecord());
	    	szLST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recLast, "YD_CRN_SCH_ID");
	    	
	    	szYD_SCH_CD      = ydDaoUtils.paraRecChkNull(recFirst, "YD_SCH_CD");
	    	szYD_GP          = szYD_SCH_CD.substring(0,1);
	    	
	    	
	    	szMsg="["+ szOperationName +"] 파라미터로 전달된 크레인스케줄["+szYD_CRN_SCH_ID+"], 첫번째 크레인스케줄["+szFST_CRN_SCH_ID+"], 마지막 크레인스케줄["+szLST_CRN_SCH_ID+"]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);				
	    	//--------------------------------------------------------------------------------------------------

	    	
	    	//--------------------------------------------------------------------------------------------------
			//	상차/하차 처리
			//--------------------------------------------------------------------------------------------------
	    	
	    	//플래그가 상차인경우
	    	if(szCAR_LDUD_GP.equals("U")) {
	    		//--------------------------------------------------------------------------------------------------
	    		//	상차인 경우 상차작업예약ID로 차량스케줄을 조회
	    		//--------------------------------------------------------------------------------------------------
	    		szMsg="["+ szOperationName +"] 상차인 경우 상차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
//////////////////
    	    	YdEqpDao   ydEqpDao   = new YdEqpDao();
    			JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
    			JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
    			JDTORecord 		outRecord1  = JDTORecordFactory.getInstance().create();
    			String szAPPLY_YN 			= "N";
    			
    			if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP)) {
    				inRecord1.setField("REPR_CD_GP", "K00110");    //차량정보 SKIP
    			} else {
    				inRecord1.setField("REPR_CD_GP", "T00110");    //차량정보 SKIP
    			}
    			
    			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
    			intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
    			if(intRtnVal > 0) {
    				outResult.first();
    				outRecord1  = outResult.getRecord();
    				szAPPLY_YN = outRecord1.getFieldString("ITEM1");				
    			}
    			szMsg="차량정보 SKIP 적용 " + szAPPLY_YN ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    			ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);				
    			ydUtils.putLogNew(szOperationName, szMethodName, szMsg, YdConstant.DEBUG, logId);								

    			if (szAPPLY_YN.equals("Y")) {
//SJH07001:권하시 차량 작업 error 발생시 skip
    				intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
    	    		if (intRtnVal <= 0){
    		    		szMsg = "차량에서 상차작업 처리시 차량스케쥴 정보 오류발생.--> SKIP처리 함";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	   
    		            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	   
    		            return 1;
    		    	}
    			} else {
    				ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
    			}
	    		
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		
	    		szYD_CAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    		szYD_CAR_USE_GP 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP");
	    		szCAR_NO 				= ydDaoUtils.paraRecChkNull(recOutTemp, "CAR_NO");
	    		szCARD_NO				= ydDaoUtils.paraRecChkNull(recOutTemp, "CARD_NO");
	    	    szSPOS_WLOC_CD			= ydDaoUtils.paraRecChkNull(recOutTemp, "SPOS_WLOC_CD");
	    	    szYD_PNT_CD				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PNT_CD1");
	    	    szTRANS_ORD_DATE		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_DATE");
	    	    szTRANS_ORD_SEQNO		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_SEQNO");
	    	    sTelNo					= ydDaoUtils.paraRecChkNull(recOutTemp, "TEL_NO");

	    	    
	    		szMsg="["+ szOperationName +"] 상차인 경우 상차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄["+szYD_CAR_SCH_ID+"], 차량사용구분["+szYD_CAR_USE_GP+"] 조회 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);				
	    		//--------------------------------------------------------------------------------------------------
	    		
	    		
				//--------------------------------------------------------------------------------------------------
    			//첫번째 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
				//--------------------------------------------------------------------------------------------------
	    		if(szFST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			/*
	    			 * 상차개시를 권상실적으로 이동시킴
	    			 */
	    		}
	    		
	    		//--------------------------------------------------------------------------------------------------
	    		//출하차량인 경우에만 적용한다. - 일품 상차실적 송신 YDDMR012 (후판일품출하상차실적)
	    		//--------------------------------------------------------------------------------------------------
	    		if(szYD_CAR_USE_GP.equals("G")){
	    			
	    			/*--이전 방식
	    			//일품 상차실적 송신 YDDMR012 (후판일품출하상차실적)
					szMsg="["+ szOperationName +"] 출하차량 일품 상차실적 송신 YDDMR012 (후판일품출하상차실적) 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID",        "YDDMR012");
					
					recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("YD_GP",         szYD_GP);
		
					ydDelegate.sendMsg(recInTemp);
					
					szMsg="["+ szOperationName +"] 출하차량 일품 상차실적 송신 YDDMR012 (후판일품출하상차실적) 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					*/
					
					//--출하고도화 이후 방식-----------------------------------------------------------------
	    			
					szMsg="["+ szOperationName +"] 출하차량 일품 상차실적 송신 YDDMR012 (후판일품출하상차실적) 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);					
					
					recInTemp = JDTORecordFactory.getInstance().create();
					
	    			for(int ii = 0; ii < rsCrnWrkMtl.size(); ii++) {
	    				
	    				recMtl = rsCrnWrkMtl.getRecord(ii);
	    				
	    				recInTemp.setField("MSG_ID",        "YDDMR012");
	    				recInTemp.setField("YD_GP",         szYD_GP);
	    				recInTemp.setField("TRANS_WORD_DATE",         szTRANS_ORD_DATE);
	    				recInTemp.setField("TRANS_WORD_SEQNO",         szTRANS_ORD_SEQNO);
	    				recInTemp.setField("CAR_NO",         szCAR_NO);
	    				recInTemp.setField("CARD_NO",         szCARD_NO);
	    				recInTemp.setField("GOODS_NO",        recMtl.getFieldString("STL_NO"));
	    				recInTemp.setField("CARLD_PNT_CD",  szCARLD_PNT_CD);
	    				
						if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID) && (ii+1) == rsCrnWrkMtl.size()) {
							//마지막 스케줄 ID이고 작업재료의 마지막일경우
							recInTemp.setField("GOODS_EA","*");
						} else {
							recInTemp.setField("GOODS_EA","1");
						}
	    				
						ydDelegate.sendMsg(recInTemp);
	    			}
					
					szMsg="["+ szOperationName +"] 출하차량 일품 상차실적 송신 YDDMR012 (후판일품출하상차실적) 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	    			
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	    			

					
					for(int ii = 0; ii < rsCrnWrkMtl.size(); ii++) {
						
						recMtl = rsCrnWrkMtl.getRecord(ii);
						
						recInTemp.setField("STL_NO",	recMtl.getFieldString("STL_NO"));
						
	    				//검수 테이블 생성 //////////////////////////////////////////////////////////////
	    				// ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW2
	    				intRtnVal = ydStockDao.updYdStockExa(recInTemp, 0);			
	
	    				if(intRtnVal >0){
	    					szMsg = "수신한 재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록이 되었습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);				
	
	    				}else if(intRtnVal == 0){
	    					szMsg = "수신한  재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록 되었거나  실패 하였습니다.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	    	 			}
	    				///////////////////////////////////////////////////////////////////////////////		
					}
	    		}
	    		//--------------------------------------------------------------------------------------------------
	    		
	    		
	    		//--------------------------------------------------------------------------------------------------
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		//--------------------------------------------------------------------------------------------------
	    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			//--------------------------------------------------------------------------------------------------
	    			//동일하면 차량스케줄에 상차완료일시 등록, 설비작업상태 = '영차'
	    			//--------------------------------------------------------------------------------------------------
	    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_CAR_PROG_STAT", "5");
	    			recInTemp.setField("YD_EQP_WRK_STAT", "L");
	    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 1);
	    			if(intRtnVal <= 0) {
	    				szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 시 오류" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
	    			}
	    			
	    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	    			//--------------------------------------------------------------------------------------------------
	    			
	    			
	    			//--------------------------------------------------------------------------------------------------
	    			//	상차완료 전문 송신
	    			//--------------------------------------------------------------------------------------------------
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			
	    			if(szYD_CAR_USE_GP.equals("L")){							//구내운송 - 임춘수 수정 2009.06.15
	    				//--------------------------------------------------------------------------------------------------
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 시작 2009.06.08 임춘수 추가 ----------------------
	    				//--------------------------------------------------------------------------------------------------
	    				//intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
	    				szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차완료 시 공통 테이블 업데이트 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);				
	    				String szRtnMsg = YdCommonUtils.procCarLoadCmplForPlateGds(szYD_CAR_SCH_ID, YdConstant.YD_STR_LOC_HIS1, szMethodName);
	    				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    					szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차완료 시 공통 테이블 업데이트 성공";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	    				}else{
	    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
	    					szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차완료 시 공통 테이블 업데이트 실패";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
	    				}
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------
	    				
		    			//상차작업완료 송신 YDTSJ008 (구내운송 상차완료)
		    			recInTemp.setField("MSG_ID",        "YDTSJ008");
						szMsg="["+ szOperationName +"] 구내운송 상차작업완료 송신 : YDTSJ008";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
						
						recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
		    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
		    			recInTemp.setField("YD_GP",         szYD_GP);
		    			
		    			ydDelegate.sendMsg(recInTemp);
		    			
						szMsg="["+ szOperationName +"] 상차작업완료 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
						
	    			}else{
    					/*
    	    			 * 01. 후판출하상차완료전문 송신가능여부 체크
    	    			 *     - 야드에 출하대상재가 남아있는지 체크
    	    			 */
    	    			JDTORecordSet rsPara = JDTORecordFactory.getInstance().createRecordSet("");
    	    			JDTORecord recPara   = JDTORecordFactory.getInstance().create();
    	    			
    	    			recPara.setField("CARD_NO", 			szCARD_NO);
    	    			recPara.setField("TRANS_ORD_DATE",  	szTRANS_ORD_DATE);
    	    			recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
    	    			
    	    			int nRtnVal = ydStockDao.getYdStock_DoubleDong(recPara, rsPara, "3");
	    			
    	    			if(nRtnVal <= 0){
	    					return 1;
	    				}
    	    			
    	    			rsPara.first();
    	    			JDTORecord recGetVal = rsPara.getRecord();	
    	    			
    	    			int iResultCnt = ydDaoUtils.paraRecChkNullInt(recGetVal, "CNT");
    	    			
    	    			szMsg="["+ szOperationName +"] 복수동 상차작업여부 대상재 갯수 = " + iResultCnt;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
						
    	    			/*
    	    			 * 02. 없으면  - 상차완료처리 송신
    	    			 */
    	    			if(iResultCnt == 0){
    	    				
	    	    			//상차작업완료 송신 YDDMR016 (후판출하상차완료)
    	    				recPara = JDTORecordFactory.getInstance().create();
    	    				recPara.setField("MSG_ID",    					YdConstant.YDYDJ701);
    	    				recPara.setField(YdConstant.BUFFER_TC_CD, 		"YDDMR016");
    	    				recPara.setField("YD_SCH_CD",     szYD_SCH_CD);
    	    				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    	    				recPara.setField("YD_GP",         szYD_GP);
    	    				recPara.setField("CARLD_PNT_CD",  szCARLD_PNT_CD);
			    			
			    			ydDelegate.sendMsg(recPara);
			    			
							szMsg="["+ szOperationName +"] 상차작업완료 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

    	    			}else{
    	    				
    	    				if(szCARD_NO.startsWith("P")){
    	    					
    	    				}else{
    	    					/*
    	    	    			 * 03. 있으면 - 상차완료처리 SKIP
    	    	    			 *             대신, 현차량에 대한 대기장 입동지시 SMS 문자를 발송한다.
    	    	    			 *                   출하완료전문을 발생시켜 차량자동출발처리한다(DMYDR031).
    	    	    			 *                   추가로 나머지 대상재 출하작업을 진행시킨다.(DMYDR021 - 추가파라미터 필요)
    	    	    			 */	
    	    	    				recPara = JDTORecordFactory.getInstance().create();
    	    	    				recPara.setField("TC_CODE",        		"DMYDR042");									//전문코드
    	    	    				recPara.setField("YD_GP", 				szYD_GP);
    	    	    				recPara.setField("CARD_NO", 			szCARD_NO);
    	    	    				recPara.setField("CAR_NO", 				szCAR_NO);			
    	    	    				recPara.setField("SPOS_WLOC_CD", 		szSPOS_WLOC_CD);
    	    	    				recPara.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD);
    	    	    				recPara.setField("TRANS_ORD_DT", 		szTRANS_ORD_DATE);
    	    	    				recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
    	    	    				
    	    	    				szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    		    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		    					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
    		    					
    		    					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
    		    					ejbConn.trx("rcvPlGdsDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recPara });
    		    					
    		    					szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    		    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		    					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
    		    					
    		    					//---------------------------------------------------------------------------------------------------------
    		    					//육송출하고도화
    		    					//DMYDR061
    		    					recPara = JDTORecordFactory.getInstance().create();
    	    	    				recPara.setField("TC_CODE",        		"DMYDR061");									//전문코드
    	    	    				recPara.setField("CARD_NO", 			szCARD_NO);
    	    	    				recPara.setField("CAR_NO", 				szCAR_NO);			
    	    	    				recPara.setField("TRANS_ORD_DT", 		szTRANS_ORD_DATE);
    	    	    				recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
    	    	    				recPara.setField("DOUBLEDONG_CHECK", 	"Y");//복수동 나머지 출하여부
    	    	    				recPara.setField("YD_CAR_SCH_ID",       szYD_CAR_SCH_ID);
    	    	    				
    	    	    				ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);
    	    	    				
    	    	    				// 전사물류개선 2021. 1. 6 신규로직 분기여부(대기장도착처리)
    	    	    				if(PlateGdsYdUtil.isPlateNewMoudleApply("DM")){
    	    	    					ejbConn.trx("procStandByYdArrivePlate4G", new Class[] { JDTORecord.class }, new Object[] { recPara });
    	    	    				}else{
    	    	    					ejbConn.trx("procStandByYdArrivePlate", new Class[] { JDTORecord.class }, new Object[] { recPara });
    	    	    				}
    	    	    				
    								szMsg="["+ szOperationName +"] 복수동 나머지 출하Lot DMYDR061 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//    								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
    								ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	
    	    				}
	    				}
	    	    	}
	    			//--------------------------------------------------------------------------------------------------
					
					//--------------------------------------------------------------------------------------------------
					//	Pallet가 아닌 출하차량이 상차완료된 경우 차량출발 모듈을 호출
					//	Pallet인 경우에는 조업자가 직접 출발 시킴
					//	수정자 : 임춘수
					//	수정일 : 1) 2009.12.30 - 최초등록 - 출하완료전문 처리 시에 차량출발처리하던 로직을 이곳으로 임시로 옮김
					//			2) 2010.01.14 - 출하완료 시점으로 다시 원상복구시킴 
					//							출하완료전문의 재료외형구분에 *로 설정된 경우에 차량출발처리
					//--------------------------------------------------------------------------------------------------
					
//					if(szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM)){
//						//--------------------------------------------------------------------------------------------------
//						//	E/T Car 나 해송차량인 경우에는 차량출발처리를 자동으로 하지 않는다.
//						//--------------------------------------------------------------------------------------------------
//						if(szCAR_NO.matches("\\d\\d\\d\\d")) {				//E/T Car
//							szMsg= "["+ szOperationName +"] E/T Car[" + szCAR_NO + "], 카드번호[" + szCARD_NO + "]는 자동으로 차량출발처리를 하지 않습니다.";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						}else{
//							
//							recInTemp = JDTORecordFactory.getInstance().create();
//							recInTemp.setField("TC_CODE",        		"DMYDR042");									//전문코드
//							recInTemp.setField("CARD_NO", 				szCARD_NO);
//							recInTemp.setField("CAR_NO", 				szCAR_NO);			
//							recInTemp.setField("SPOS_WLOC_CD", 			szSPOS_WLOC_CD);
//							recInTemp.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD);
//							recInTemp.setField("TRANS_ORD_DT", 			szTRANS_ORD_DATE);
//							recInTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
//							
//							szMsg= "["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 Car번호[" + szCAR_NO + "], 카드번호[" + szCARD_NO + "]은 자동으로 차량출발 모듈 EJB 호출 시작";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//							
//							EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
//							ejbConn.trx("rcvPlGdsDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
//							
//							szMsg= "["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 Car번호[" + szCAR_NO + "], 카드번호[" + szCARD_NO + "]은 자동으로 차량출발 모듈 EJB 호출 완료";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						}
//						//--------------------------------------------------------------------------------------------------
//					}
					
					//--------------------------------------------------------------------------------------------------
	    		}
	    	//--------------------------------------------------------------------------------------------------
	    	//플래그가 하차인 경우
	    	//--------------------------------------------------------------------------------------------------
	    	}else if(szCAR_LDUD_GP.equals("L"))	{
	    		//--------------------------------------------------------------------------------------------------
	    		//	하차인 경우 하차작업예약ID로 차량스케줄을 조회
	    		//--------------------------------------------------------------------------------------------------
	    		szMsg="["+ szOperationName +"] 하차인 경우 하차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
				
	    		recInTemp  = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			rsResult   = JDTORecordFactory.getInstance().createRecordSet("");
    			
    			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		if (intRtnVal <= 0){
		    		szMsg = "차량에서 하차작업 처리시 차량스케쥴 정보 오류발생.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	   
		            /*
		             * 2010.12.25 윤재광 - 차량입고기능을 위해 아래 에러처리 막음.
		             * 차량입고기능은 차량스케쥴 정보 없슴
		             */
		            //throw new  JDTOException(szMsg);
		            return 1;
		    	}
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		
	    		szYD_CAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    		szYD_CAR_USE_GP 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP");
	    		szCAR_NO 				= ydDaoUtils.paraRecChkNull(recOutTemp, "CAR_NO");
	    		szCARD_NO				= ydDaoUtils.paraRecChkNull(recOutTemp, "CARD_NO");
	    	    szSPOS_WLOC_CD			= ydDaoUtils.paraRecChkNull(recOutTemp, "ARR_WLOC_CD");
	    	    szYD_PNT_CD				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PNT_CD1");
	    	    szTRANS_ORD_DATE		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_DATE");
	    	    szTRANS_ORD_SEQNO		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_SEQNO");
	    	    sTelNo					= ydDaoUtils.paraRecChkNull(recOutTemp, "TEL_NO");	    		
	    		
	    		
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			recInTemp.setField("YD_GP",         szYD_GP);
    			
    			szMsg="["+ szOperationName +"] 하차인 경우 하차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	    		//--------------------------------------------------------------------------------------------------
    			
				//--------------------------------------------------------------------------------------------------
	    		//첫번째 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
				//--------------------------------------------------------------------------------------------------
	    		if(szFST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			/*
	    			 * 하차개시를 권상실적으로 이동시킴
	    			 */
	    		}
	    		
	    		//--------------------------------------------------------------------------------------------------
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		//--------------------------------------------------------------------------------------------------
	    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			//--------------------------------------------------------------------------------------------------
	    			//동일하면 차량스케줄에 하차완료일시 등록
	    			//--------------------------------------------------------------------------------------------------
	    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 하차작업예약ID["+szYD_WBOOK_ID+"]의 하차완료 수정 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_EQP_WRK_STAT", "U");
	    			recInTemp.setField("YD_CAR_PROG_STAT", "E");
	    			recInTemp.setField("DEL_YN",           "N");
	    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 3);
	    			if(intRtnVal <= 0) {
						szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 하차작업예약ID["+szYD_WBOOK_ID+"]의 하차완료 수정 시 Error!! Code : " + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
	    			}
	    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 하차작업예약ID["+szYD_WBOOK_ID+"]의 하차완료 수정 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
					//--------------------------------------------------------------------------------------------------
					
					if(YdConstant.YD_CAR_USE_GP_TS.equals(szYD_CAR_USE_GP)){
						//--------------------------------------------------------------------------------------------------
		    			//---------------- 하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 추가
						//--------------------------------------------------------------------------------------------------
		    			szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 하차완료 시 공통 테이블 업데이트 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
						
		    			//intRtnVal = YdCommonUtils.procCarUnLoadCmpl(szYD_CAR_SCH_ID);
						String szRtnMsg = YdCommonUtils.procCarUnLoadCmplForPlateGds(szYD_CAR_SCH_ID, YdConstant.YD_STR_LOC_CURR);
	    				//if( intRtnVal <= 0 ) {
	    				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
	    					szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 하차완료 시 공통 테이블 업데이트 처리 실패";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
	    				}else{
	    					szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 하차완료 시 공통 테이블 업데이트 처리 성공";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
	    				}
		    			//-----------------하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 끝
	 
	    				
						//하차작업완료 송신 YDTSJ010
						recInTemp.setField("MSG_ID",        "YDTSJ010");
						recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
						recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						recInTemp.setField("YD_GP",         szYD_GP);
						ydDelegate.sendMsg(recInTemp);
					}
					

    				szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

    				//---------------------------------------------------------------------------------------------------------
    				JDTORecord recPara   = JDTORecordFactory.getInstance().create();
    				recPara.setField("TC_CODE",        		"DMYDR042");									//전문코드
    				recPara.setField("YD_GP", 				szYD_GP);
    				recPara.setField("CARD_NO", 			szCARD_NO);
    				recPara.setField("CAR_NO", 				szCAR_NO);			
    				recPara.setField("SPOS_WLOC_CD", 		szSPOS_WLOC_CD);
    				recPara.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD);
    				recPara.setField("TRANS_ORD_DT", 		szTRANS_ORD_DATE);
    				recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
					ejbConn.trx("rcvPlGdsDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recPara });
					
					szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
    				//---------------------------------------------------------------------------------------------------------
					
		    	    //--------------------------------------------------------------------
	            	// 전사물류개선 2021. 1. 6 
	            	//  - 다른 하차지 검색하여 복수동상차로직과 동일하게 처리한다. 
	        		//   : 하차지 결정은 대기장도착처리(DMYDR061)에서 처리하도록 한다.
	        		//
	        		//  - 차량스케쥴 재편성하여 대기장도착처리(DMYDR061)
	    			//  - 마지막일 경우 기존과 동일한 방식으로 처리함
	            	//----------------------------------------------------------------------
					if(PlateGdsYdUtil.isPlateNewMoudleApply("DM")){
						if(YdConstant.YD_CAR_USE_GP_DM.equals(szYD_CAR_USE_GP)){

		        			int nTRANS_ORD_SEQNO = 0;
		        			try{ nTRANS_ORD_SEQNO = Integer.parseInt(szTRANS_ORD_SEQNO); }catch(Exception e){nTRANS_ORD_SEQNO=0;};
		        			
		        			// 차량입고(반품) 운송지시번호 999000 보다 큰 건
			        		if(nTRANS_ORD_SEQNO>999000){
			        			// 1. 다음하차정지위치 찾기
			        			
			        			JDTORecord params = JDTORecordFactory.getInstance().create();
			        			JDTORecordSet rsCarStopLoc = JDTORecordFactory.getInstance().createRecordSet("");
			        			YdPlateCommDAO  commDao 	  = new YdPlateCommDAO();
			        			ejbConn = null;
			        			
			        			// 차량에 위에 아직도 재료가 존재하는가?
			        			params.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			        			if( commDao.select(params, rsCarStopLoc, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarUnloadStopLoc") > 0){
			        				 
			        				// 2. DMYDR061 도착전문전송
			    					JDTORecord send_DMYDR061= JDTORecordFactory.getInstance().create();
			    					send_DMYDR061.setField("TC_CODE",        	"DMYDR061");									//전문코드
			    					send_DMYDR061.setField("CARD_NO", 			szCARD_NO);
			    					send_DMYDR061.setField("CAR_NO", 			szCAR_NO);			
		    	    				send_DMYDR061.setField("TRANS_ORD_DT", 		szTRANS_ORD_DATE);
		    	    				send_DMYDR061.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
		    	    				send_DMYDR061.setField("DOUBLEDONG_CHECK", 	"Y");//복수동 나머지 출하여부
		    	    				send_DMYDR061.setField("YD_CAR_SCH_ID",       szYD_CAR_SCH_ID);
		    	    				
		    	    				ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);
		    	    				ejbConn.trx("procStandByYdArrivePlate4G", new Class[] { JDTORecord.class }, new Object[] { send_DMYDR061 });
		    						
									szMsg="[권상실적처리] 복수동하차 나머지 출하Lot DMYDR061 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
									ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);	
			        			}
			        		}
		        		}
					}
	    			
					szMsg="["+ szOperationName +"] 하차작업완료 송신 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	    		}
	    		//--------------------------------------------------------------------------------------------------
	    	}else{
				szMsg="["+ szOperationName +"] 상차 및 하차 구분 플래그 Error";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
				return intRtnVal = -1;
	    	}

		}catch(Exception e){
	
			szMsg="["+ szOperationName +"] 차량 작업 진행관리 처리 Error:" +e.getMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
			throw new JDTOException("<procY4CrnUdWr> =" + szMsg);
		}
	
	
		szMsg="["+ szOperationName +"] 차량 작업 진행관리 처리"+szMethodName+") 완료";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
		return intRtnVal = 1;
	} //end of procY4CarWrkStatCtr()
    
	/**
     * 오퍼레이션명 : 후판 주문외제품 사외이송 상차완료 처리
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     * @ejb.transaction type="RequiresNew"
     */
    public int Y4UpdYdDmFrInfo(String sWbookId) throws JDTOException {
    	// DAO 및 UTIL 객체 생성
		YmEtcDao YmEtcDao     	  = new YmEtcDao();
		// 레코드 선언
		JDTORecord recIn          = null;
		
    	int intRtnVal = 0 ;
    	
    	String szMethodName 		= "Y4UpdYdDmFrInfo";
    	String szMsg        		= "";
    	String szOperationName      = "후판 주문외제품 사외이송 상차완료 처리";
		try{
			
			// 레코드 생성
			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("YD_WBOOK_ID", sWbookId);
			/*
			 * 후판주문외제품 사외이송 정보 셋팅.
			 * 1. 야드 저장품 이송작업완료일자 셋팅(REFUR_CHG_LOT_NO)
			 * 2. 야드 저장위치 정보 변경.
			 * 3. 야드 실적정보 이송실적으로 셋팅.(YD_CAR_USE_GP = 'S')
			 * > 작업예약 ID(szYD_WBOOK_ID)로 검색 후 셋팅.
			 * > 조건 : 저장품 테이블 FRTOMOVE_PLANT_GP IS NOT NULL
			 */
			intRtnVal = YmEtcDao.uptYmEtcDao(recIn, 5);
			intRtnVal = YmEtcDao.uptYmEtcDao(recIn, 6);
			intRtnVal = YmEtcDao.uptYmEtcDao(recIn, 7);
			
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<Y4UpdYdDmFrInfo> MSG =" + szMsg);
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y4UpdYdDmFrInfo
    
    /**
     * 오퍼레이션명 : 권상,권하 파라미터 체크
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int Y4ParamCheck (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        int intRtnVal = 1 ;
        
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
    		
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException("<Y1ParamCheck> " + e.getLocalizedMessage());
        }//end of try~catch
        
        return intRtnVal;
        
    }//end of ParamCheck()
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4UpdYdCrnsch(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	int intRtnVal = 0 ;
    	
    	String szMethodName 		= "Y4UpdYdCrnsch";
    	String szMsg        		= "";
    	String szOperationName      = "후판제품 크레인스케줄 Update";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procY4CrnUdWrTX Method 에서 call 하기전 setRecord.setField("LOG_ID", logId) 추가
//String logId 							= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		try{
			
			intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);		        
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	  
	                return intRtnVal;
	        	case -1	:
	                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	 
	                return intRtnVal;
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	   
	                return intRtnVal;
	        	case -3	:
	                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	 
	                return intRtnVal;
	        }
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	   
            throw new JDTOException("<Y4UpdYdCrnsch> MSG =" + szMsg);
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y4UpdYdCrnsch
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4GetYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMethodName 	= "Y4GetYdCrnsch";
    	String szMsg        	= "";
    	String szOperationName  = "후판제품 크레인스케줄 Select";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// procY4CrnUdWrTX Method 에서 call 하기전 setRecord.setField("LOG_ID", logId) 추가
String logId 							= msgRecord.getFieldString("LOG_ID");	// [T] + 전문일련번호) 형식으로  logId Get
if(ydUtils.isEmpty(logId)) {
	logId                    			= msgRecord.getResultCode();			// 전문으로 부터 logid get
	if(ydUtils.isEmpty(logId)) {
		logId 		= ydUtils.getLogIdNew("T"); 								// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
	}
	else{
		logId 		= "[T]" + logId;
	}
}
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
	        switch (intRtnVal) {
        	case 0	:
                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return intRtnVal;
        	case -2	:
                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                return intRtnVal;
	        }
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
            throw new JDTOException("<Y4GetYdCrnsch> MSG =" + szMsg);
        }//end of try~catch

        return intRtnVal ;
    	
    }//end of Y4GetYdCrnsch()
        
    /**
     * 오퍼레이션명 : 적치단 Clear
     *  
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4ClearYdStklyr (JDTORecordSet getRecSet, int intGp) throws JDTOException {
    	
    	JDTORecord getRecord = JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
    	
    	int intRtnVal 		= 0;
    	String szMsg 		= "";
    	String szMethodName = "Y4ClearYdStklyr";
    	
    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();
            
    		for(int i=0; i<rowsize ; i++){
    			
    			//권상 지시위치 Clear
                if(intGp == 0) {
                	
        			String szYD_UP_WR_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC") ;
                    String szYD_UP_WR_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LAYER") ;
                    
                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
                    //적치단 설정
                    String szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
                    setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                }
                
                //권하 지시위치 Clear
                if(intGp == 1) {
                	
	    			String szYD_DN_WO_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC") ;
	                String szYD_DN_WO_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LAYER") ;
	                
	                setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
	                setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   
	                //적치단 설정
	                String szStkLyr =ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i);
	                setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
                }
                
                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                setRecord.setField("STL_NO",              "");
                
                intRtnVal = this.Y4UpdYdStklyr(setRecord, 0);  //적치단의 재료정보 Clear
                
                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for
    		
		}catch(Exception e){
			new JDTOException("<procY4CrnUdWr> Y4ClearYdStklyr" + e.getLocalizedMessage());
	    }//end of try~catch
		
		return  intRtnVal;
    }//end of Y4ClearYdStklyr()
    
    /**
     * 오퍼레이션명 : 적치단 등록
     *  
     * @param  ● getRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4RegYdStklyr (JDTORecordSet getRecSet, String sRealLyrNo, String logId)throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// Y4RegYdStklyr argument 에 logId 항목 추가 개선
// public int Y4RegYdStklyr (JDTORecordSet getRecSet, String sRealLyrNo)throws JDTOException {
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	JDTORecord getRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 			= JDTORecordFactory.getInstance().create();
    	JDTORecord crnRecord 			= null;
    	
    	JDTORecord recOutTemp 			= JDTORecordFactory.getInstance().create();
    	JDTORecord recTemp 				= null;
    	JDTORecordSet rcResult          = null;
    	JDTORecordSet rsResult          = null;
    	
    	String szRtnMsg					= null;
    	String szMsg 					= "";
        String szMethodName				= "Y4RegYdStklyr";
        String szOperationName          = "적치단 등록";        
        String szYD_MTL_ITEM            = "";
        
        int intRtnVal 					= 0 ;
        
        String szYD_EQP_ID 				= "";
        String szYD_WBOOK_ID 			= "";
        String szYD_DN_WR_LOC			= null;
        String szYD_DN_WR_LAYER	   		= "";
		String szSTL_NO	 		   		= "";
		String sPI_YD                   = "";
        //차량사용구분
        String szYD_CAR_USE_GP			= null;

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
if(ydUtils.isEmpty(logId)) logId 		= ydUtils.getLogIdNew("T"); 			// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
        
szMsg = "적치단 등록(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
        
    	try{
    		int rowsize = getRecSet.size();
    		
    		//----------------------------------------------------------------------------------------------------------
	        //	권하실적위치가 차상이고 출하차량이면 공통테이블의 야드구분을 *로 등록 처리하기 위해서 차량스케줄 조회
	        //	수정자 : 임춘수
	        //	수정일 : 2009.12.17
	        //----------------------------------------------------------------------------------------------------------
    		getRecSet.first();
    		getRecord		= getRecSet.getRecord();
    		
    		recTemp			= JDTORecordFactory.getInstance().create();
	        
    		szYD_DN_WR_LOC	   			= ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
    		szYD_WBOOK_ID 				= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID");
    		if( szYD_DN_WR_LOC.length() > 6 ){
    			sPI_YD = szYD_DN_WR_LOC.substring(0,1);
    		} else{
    			sPI_YD = "*";
    		}
	        if( szYD_DN_WR_LOC.length() > 6  && 
	        	szYD_DN_WR_LOC.substring(2, 4).equals("PT") ) {
	        	
	        	szMsg = "권하실적위치가 차상이므로 차량스케줄 조회 시작";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);				
	        	
	        	rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
	        	
	        	recTemp.setField("YD_CARLD_WRK_BOOK_ID",       	szYD_WBOOK_ID);
	        	
//PIDEV_S :병행가동용:PI_YD
	        	recTemp.setField("PI_YD",    	sPI_YD);	
	        	/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschUDLDWRKBOOKID_PIDEV*/
	        	szRtnMsg = DaoManager.getYdCarsch(recTemp, rsResult, 3);
	    	    
	    	    if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    	    	rsResult.first();
	    	    	recTemp		= rsResult.getRecord();
	    	    	
	    	    	szYD_CAR_USE_GP		= ydDaoUtils.paraRecChkNull(recTemp,"YD_CAR_USE_GP") ;
	    	    	
	    	    	szMsg = "권하실적위치가 차상이고 차량스케줄이 존재하므로 차량사용구분을 확인 - 차량사용구분[" + szYD_CAR_USE_GP + "]";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	    	    }
	    	    
	    	    szMsg = "권하실적위치가 차상이므로 차량스케줄 조회 완료 - 메세지 : " + szRtnMsg;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	        }
	        
	        //----------------------------------------------------------------------------------------------------------
    		
        	for(int i=0; i<rowsize; i++) {
        		
        		getRecSet.absolute(i+1);
        		getRecord 	= JDTORecordFactory.getInstance().create();
        		getRecord.setRecord(getRecSet.getRecord());
        		
        		//저장품을 조회하여 재표 품목의 값을 조회한다.
        		rcResult  = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
	        	recTemp.setField("PI_YD",    	sPI_YD);	        		
        		intRtnVal = ydStockDao.getYdStock(getRecord, rcResult, 0);
    	        
    	        rcResult.absolute(1);
    	        recOutTemp = JDTORecordFactory.getInstance().create();
    	        recOutTemp.setRecord(rcResult.getRecord());
    	        
    	        szYD_EQP_ID   = ydDaoUtils.paraRecChkNull(getRecord,"YD_EQP_ID");
    	        szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(getRecord,"YD_MTL_ITEM");
    	        szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID");
        		
        		//권하 실적위치 등록
        		szYD_DN_WR_LOC	   	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
        		szYD_DN_WR_LAYER	   = sRealLyrNo; //ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER") ;
        		szSTL_NO	 		   = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO") ;
        		
        		//크레인에 UPDATE
    			crnRecord = JDTORecordFactory.getInstance().create();
    			crnRecord.setField("YD_STK_COL_GP",       szYD_EQP_ID);    
    			crnRecord.setField("YD_STK_BED_NO",       "01");   
    			crnRecord.setField("YD_STK_LYR_NO",       "00"+(i+1)) ;
                crnRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                crnRecord.setField("STL_NO",              "");
                
                intRtnVal = this.Y1UpdYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE
                
        		setRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6));   
        		setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8));               
                String szStkLyr = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i);
                setRecord.setField("YD_STK_LYR_NO", 		szStkLyr) ;
                setRecord.setField("STL_NO",              	szSTL_NO);                            
                setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");           
                
                /*
                 * 적치단에 존재하는 해당 저장품정보를 모두 CLEAR한다.
                 */
                recTemp			= JDTORecordFactory.getInstance().create();
                recTemp.setField("STL_NO", szSTL_NO);               
            	intRtnVal = (new YdStkLyrDao()).updYdStklyrWithStock(recTemp);
            	
                //적치단dao를 호출해서 업데이트를 한다.
                intRtnVal = this.Y4UpdYdStklyr(setRecord, 0); 
    	        
    	        
				//저장위치 갱신				공통테이블에 저장위치를 갱신한다.
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
				setRecord.setField("YD_MTL_ITEM",       	szYD_MTL_ITEM);
				setRecord.setField("YD_DN_WR_LOC",       	ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC")); 
    	        
				//----------------------------------------------------------------------------------------------------------
    	        //	권하실적위치가 차상이고 출하차량이면 차량사용구분을 전달
    	        //	수정자 : 임춘수
    	        //	수정일 : 2009.12.17
    	        //----------------------------------------------------------------------------------------------------------
				
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// Y4setYdStrLoc Method에 ([T] + 전문일련번호) 형식으로  logId 넘김
				setRecord.setField("LOG_ID", logId);
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
				setRecord.setField("YD_CAR_USE_GP",       	szYD_CAR_USE_GP); 
				
				//----------------------------------------------------------------------------------------------------------
				
    	        intRtnVal = this.Y4setYdStrLoc(setRecord) ;
        	}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException(szMsg);
	    }//end of try~catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
szMsg = "적치단 등록(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
		
		intRtnVal = 1 ;
		return intRtnVal ;
    
    }//end of Y4RegYdStklyr()
    
    /**
     * 오퍼레이션명 : 차량 Setting
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y4SetYdCar (JDTORecordSet inRecordSet, int intGp, String logId) throws JDTOException{
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// Y4SetYdCar argument 에 logId 항목 추가 개선
// public int Y4SetYdCar (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
// 2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
    	
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szOperationName              = "후판제품 차량 Setting";
    	String szMethodName 				= "Y4SetYdCar";
    	String szMsg 						= "";
    	String szYD_AIM_YD_GP               = "";
    	String szYD_AM_BAY_GP               = "";
    	
    	long lngYD_MTL_WT                  = 0;
    	int  intYD_MTL_SH                  = 0;
    	long lngYD_EQP_WRK_WT              = 0;
    	int  intYD_EQP_WRK_SH              = 0;
    	
    	int intRtnVal = 0 ;
    	
    	//차량 스케줄 ID
    	String szYD_CAR_SCH_ID = "";

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
if(ydUtils.isEmpty(logId)) logId 		= ydUtils.getLogIdNew("T");				// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "후판제품 차량 Setting 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
//2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
    	
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

//////////////////
	    	YdEqpDao   ydEqpDao   = new YdEqpDao();
			JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
			JDTORecord 		outRecord1  = JDTORecordFactory.getInstance().create();
			String szAPPLY_YN 			= "N";
			
			inRecord1.setField("REPR_CD_GP", YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "00110");    //차량정보 SKIP T00110
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
			if(intRtnVal > 0) {
				outResult.first();
				outRecord1  = outResult.getRecord();
				szAPPLY_YN = outRecord1.getFieldString("ITEM1");				
			}
			szMsg="차량정보 SKIP 적용 " + szAPPLY_YN ;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);				
			ydUtils.putLogNew(szOperationName, szMethodName, szMsg, YdConstant.DEBUG, logId);				

			if (szAPPLY_YN.equals("Y")) {
            	/// 상하차 작업예약 ID로 차량스케줄 조회
//PIDEV_S :병행가동용:PI_YD
				setRecord.setField("PI_YD",    	"T");						
		    	intRtnVal = this.Y4GetYdCarsch(setRecord, outRecSet, 3) ;
		    	if (intRtnVal <= 0) return -1 ;
			} else {
		    	// 상하차 작업예약 ID로 차량스케줄 조회
//PIDEV_S :병행가동용:PI_YD
				setRecord.setField("PI_YD",    	"T");
		    	intRtnVal = this.Y4GetYdCarsch(setRecord, outRecSet, 3) ;
		    	if (intRtnVal <= 0){
		    		szMsg = "차량에서 권하작업 처리시 차량스케쥴 정보 오류발생.";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	   
		            throw new  JDTOException(szMsg);
		    	}
			}	
	    	
	    	// 차량스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 차량스케줄 ID를 추출한다
	    	szYD_CAR_SCH_ID  = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
	    	lngYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullLong(getTcarRecord, "YD_EQP_WRK_WT");
	    	intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(getTcarRecord, "YD_EQP_WRK_SH");
	    	
	    	//setRecord 초기화
	    	setRecord 	 	 = JDTORecordFactory.getInstance().create();
	    	setRecord.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    	setRecord.setField("MODIFIER", "YDSYSTEM");
	    	
	    	szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");
	    	szYD_AM_BAY_GP = ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_BAY_GP");
	    	
	    	//C연주
	    	if(szYD_AIM_YD_GP.equals("A")) {
	    		setRecord.setField("ARR_WLOC_CD", "DHY21");
	    		
			//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
	    	}else if(szYD_AIM_YD_GP.equals("M")) {
	    		setRecord.setField("ARR_WLOC_CD", YdConstant.WLOC_CD_PORT_SLAB_YARD);
	    		
	    	//A후판슬라브	
	    	}else if(szYD_AIM_YD_GP.equals("D")) {
	    		if(szYD_AM_BAY_GP.equals("B")) {
	    			setRecord.setField("ARR_WLOC_CD", "DWY22");
	    		}else{
	    			setRecord.setField("ARR_WLOC_CD", "DKY21");
	    		}
	    		
	    	//후판제품창고	
	    	}else if(szYD_AIM_YD_GP.equals("K")) {
	    		setRecord.setField("ARR_WLOC_CD", "DKY30");
	    	
		    //2후판제품창고	
	    	}else if(szYD_AIM_YD_GP.equals(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
	    		setRecord.setField("ARR_WLOC_CD", "DWY26");
	    		
	    	//통합야드
	    	}else if(szYD_AIM_YD_GP.equals("S")) {
	    		setRecord.setField("ARR_WLOC_CD", "DJY25");//(비상야드추가)
	    	
	    	//A열연COIL야드
	    	}else if(szYD_AIM_YD_GP.equals("1")) {
	    		setRecord.setField("ARR_WLOC_CD", "D2Y45");
	    	
	    	//B열연SLAB야드	
	    	}else if(szYD_AIM_YD_GP.equals("2")) {
	    		setRecord.setField("ARR_WLOC_CD", "D3Y43");
	    	
	    	//B열연 COIL야드	
	    	}else if(szYD_AIM_YD_GP.equals("3")) {
	    		setRecord.setField("ARR_WLOC_CD", "D3Y42");
	    	
	    	//A열연 SLAB야드	
	    	}else if(szYD_AIM_YD_GP.equals("0")) {
	    		setRecord.setField("ARR_WLOC_CD", "D2Y43");
	    		
	    	}
	    	
	    	intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
	    	if(intRtnVal < 0 ) {
                szMsg = "권하작업시 차량스케줄에 착지개소코드 등록중 Error!! Code No :" + intRtnVal;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	
                throw new JDTOException("<procY4CrnUdWr> Y4SetYdCar" + szMsg);
	    	}
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	
	    	int szRowSize = inRecordSet.size(); 
	    	
	    	// 권상한 재료만큼 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  
	    	for(int i = 0; i < szRowSize; i++){
	    		
	        	lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(getRecord, "YD_MTL_WT");
	        	intYD_MTL_SH = i + 1;               
	    		
		    	// 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//차량 이송재료 등록 (하차 )
	    		if(intGp == 0) {
	    			
	    			setRecord.setField("MODIFIER",			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",       			"Y");
		    		intRtnVal = this.Y4UpdCarftmvmtl(setRecord, 0) ;
		    		
			    //차량 이송재료 등록 (상차 )	
	    		}else if(intGp == 1) {
	    			setRecord.setField("REGISTER",		szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		    		setRecord.setField("DEL_YN",        "N");
		    		setRecord.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), i)) ;
		    		setRecord.setField("HCR_GP",	    ydDaoUtils.paraRecChkNull(getRecord,"HCR_GP"));
		    		setRecord.setField("STL_PROG_CD",	ydDaoUtils.paraRecChkNull(getRecord,"STL_PROG_CD"));
		    		setRecord.setField("YD_MTL_ITEM",	ydDaoUtils.paraRecChkNull(getRecord,"YD_MTL_ITEM"));
		    		setRecord.setField("YD_ROUTE_GP",	ydDaoUtils.paraRecChkNull(getRecord,"YD_ROUTE_GP"));
		    		
		    		intRtnVal = this.Y4InsYdCarftmvmtl(setRecord) ;
		    		
	    		}
		    	inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	
	    	if(intGp == 1) {
    			//차량스케줄에 등록한다.
    			lngYD_EQP_WRK_WT = lngYD_EQP_WRK_WT + lngYD_MTL_WT;
    			intYD_EQP_WRK_SH = intYD_EQP_WRK_SH + intYD_MTL_SH;
    	    	//setRecord 초기화
    	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("YD_EQP_WRK_WT",       	"" + lngYD_EQP_WRK_WT);
	    		setRecord.setField("YD_EQP_WRK_SH",       	"" + intYD_EQP_WRK_SH);
	    		
	    		intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
    		}
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);	   
            throw new JDTOException("<procY4CrnUdWr> Y4SetYdCar " + szMsg);
		}//end of try~catch

////////////////////////////////////////////////////////////////////////////////////////
//2024.09.09 로그 개선  START
    	szMsg = "후판제품 차량 Setting 처리(" + szMethodName + ") 완료";
    	ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
//2024.09.09 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
    	return 1 ;
    	
    }//end of Y4SetYdCar()
    
    /**
     * 오퍼레이션명 : 작업예약 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4UpdYdWrkbook(JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdWrkbookDao ydWrkbookDao   = new YdWrkbookDao(); 
    	
    	String szMsg = "";
    	String szMethodName = "Y4UpdYdWrkbook";
    	
    	int intRtnVal = 0 ;
        
        try{
        	intRtnVal = ydWrkbookDao.updYdWrkbook(msgRecord, intGp);

		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			throw new JDTOException("<procY4CrnUdWr> Y4UpdYdWrkbook =" + szMsg);
	    }//end of try~catch
		
		return intRtnVal ;
    }//end of Y4UpdYdWrkbook
    
    /**
     * 오퍼레이션명 : 적치단 Update
     *  
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4UpdYdStklyr (JDTORecord msgRecord, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	
    	int intRtnVal = 0 ;
    	String szOperationName      = "후판제품 적치단 Update";
    	String szMsg 				= "";
    	String szMethodName 		= "Y4UpdYdStklyr";
        
        try{
        	
	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal;
    		}
			
		}catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            new JDTOException("<procY4CrnUdWr> Y4UpdYdStklyr" + szMsg);
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y4UpdYdStklyr
    
    /**
     * 오퍼레이션명 : 저장위치 Setting 
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int Y4setYdStrLoc (JDTORecord msgRecord)throws JDTOException{
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
    	String szMethodName					= "Y4setYdStrLoc";
    	//재료품목 정의
    	String szYdMtlItem					= "";
    	//차량사용구분
    	String szYD_CAR_USE_GP				= null;
    	
    	//크레인스케줄의 정보				현재저장위치를 생성하기 위해...
    	String szYdGp						= "";
    	String szYdBayGp					= "";
    	String szYdEqpId					= "";
    	String szYdStkColNo					= "";
    	String szYdStkBedNo					= "";
    	String szYdStkLyrNo					= "";
    	String szYdDnWrLoc                  = "";
    	
    	int intRtnVal 						= 0 ;
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 START
// 기존 putLog -> putLogNew logId 출력 되게 개선
// String logId                    		= msgRecord.getFieldString("LOG_ID");		// [T] + 전문일련번호) 형식으로  logId Get
String logId                    		= ydUtils.getJDTOLogId(msgRecord, "T");		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)
if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 						// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szMsg = "Y8크레인권하실적 처리(" + szMethodName + ") 시작";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
        
        try{
            szMsg = "저장위치 Setting ( Y4setYdStrLoc ) ";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//          ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            
        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
            szYdMtlItem = ydDaoUtils.paraRecChkNull(msgRecord,"YD_MTL_ITEM"); 
			if(szYdMtlItem.length() > 1){
				szYdMtlItem = szYdMtlItem.substring(0, 1);
			}
			//--------------------------------------------------------------------------------------------------------
			//	권하실적위치가 차량인 경우 차량사용구분이 넘겨진다.
			//	수정자 : 임춘수
			//	수정일 : 2009.12.17
			//--------------------------------------------------------------------------------------------------------
			szYD_CAR_USE_GP		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_CAR_USE_GP") ;
			
			//--------------------------------------------------------------------------------------------------------
        	if (szYdMtlItem.equals("P")) {
        		
        	    szMsg = "재료가 PLATE공통인 경우";
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//              ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
        		intRtnVal = this.getY4YdStock(msgRecord, getRecSet, 4);
        	
	        	getRecSet.first();
	        	getRecord 			= getRecSet.getRecord() ;
	        	szYdStrLoc 			= getRecord.getFieldString("YD_STR_LOC") ;
	        	szYdStrLocHis1 		= getRecord.getFieldString("YD_STR_LOC_HIS1") ;
	
	
	        	szYdGp 				= msgRecord.getFieldString("YD_GP"); 
	        	szYdBayGp 			= msgRecord.getFieldString("YD_BAY_GP");
	        	szYdEqpId 			= msgRecord.getFieldString("YD_EQP_GP"); 
	        	szYdStkColNo 		= msgRecord.getFieldString("YD_STK_COL_NO"); 
	        	szYdStkBedNo 		= msgRecord.getFieldString("YD_STK_BED_NO"); 
	        	szYdStkLyrNo		= msgRecord.getFieldString("YD_STK_LYR_NO");
		        szYdDnWrLoc         = msgRecord.getFieldString("YD_DN_WR_LOC");
	        
		        //--------------------------------------------------------------------------------------------------------
	        	//	권하실적위치가 차상이고 출하차량이면 공통테이블의 야드구분을 *로 등록
		        //	수정자 : 임춘수
		        //	수정일 : 2009.12.17
	        	//--------------------------------------------------------------------------------------------------------
	        	
	        	if(szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) ) {
	        		setRecord.setField("YD_GP",         "*");
	        	}else{
	        		setRecord.setField("YD_GP",         szYdGp);
	        	}
	        	
	        	//--------------------------------------------------------------------------------------------------------
		        
	        	setRecord.setField("YD_BAY_GP",     szYdBayGp);
	        	setRecord.setField("YD_EQP_GP",     szYdEqpId);
	        	setRecord.setField("YD_STK_COL_NO", szYdStkColNo);
	        	setRecord.setField("YD_STK_BED_NO", szYdStkBedNo);
	        	setRecord.setField("YD_STK_LYR_NO", szYdStkLyrNo);
	        	setRecord.setField("FNL_REG_PGM",   szMethodName);
	        	setRecord.setField("MODIFIER",      "YDSYSTEM");
        	
	        
	        	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
	        	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc) ;
	        	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1) ;
	        	//현재 저장위치 = 실적위치+실적단 주편일경우와 슬라브일경우
	        	
	        	szMsg = "전 저장위치 : " + szYdStrLoc + " , 전전 저장위치 : " + szYdStrLocHis1 + ", 권하실적 단 정보 : " + szYdStkLyrNo;
// 2024.09.09 기존 putLog -> putLogNew logId 출력 되게 개선
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
	            
         		//수정 20090907 김진욱 : PLATE공통테이블에 현저장위치 자리수가 잘못등록 (야드구분+동구분+설비구분+열번호+베드번호1자리+단번호3자리)
        		setRecord.setField("PLATE_NO",   msgRecord.getFieldString("PLATE_NO")); 
        		setRecord.setField("YD_STR_LOC", szYdGp+szYdBayGp+szYdEqpId+
        										 szYdStkColNo+szYdStkBedNo.substring(1,2)+szYdStkLyrNo);
        		//후판제품공통 업데이트
        		intRtnVal = this.updY4YdStock(setRecord,  1);
        	}
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException(e.getLocalizedMessage());
        }//end of try~catch

////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.09 로그 개선 END
szMsg = "Y8크레인권하실적 처리(" + szMethodName + ") 완료";
ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);
// 2024.09.09 로그 개선 END
////////////////////////////////////////////////////////////////////////////////////////
        
        return intRtnVal ;
        
    }//end of Y4setYdStrLoc()
    
    /**
     * 오퍼레이션명 : 저장품 Select
     *  
     * @param msgRecord, intGp(1:상하차)
     * @return intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws 
     */
    public int getY4YdStock (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
    	YdStockDao ydStockDao = new YdStockDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMsg			= "";
    	String szMethodName		= "getY4YdStock";
    	String szOperationName              = "후판제품 저장품 Select";
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
	            return intRtnVal ;
	        }
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException(szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of getY4YdStock()
    
    /**
     * 오퍼레이션명 : 저장품 Update
     *  
     * @param msgRecord, intGp(0: 대차스케줄ID)
     * @return int execution count(성공), 0:no data found, -1:dup_val_on_index, -2:parameter error, -3:execution failed
     * @throws 
     */
    public int updY4YdStock (JDTORecord msgRecord, int intGp)throws JDTOException{
    	YdStockDao ydStockDao = new YdStockDao();
    	
    	int intRtnVal 			= 0 ;
        String szMsg            = "";
        String szMethodName     = "updY4YdStock";
        String szOperationName              = "후판제품 저장품 Update";
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
	            return intRtnVal ;
	        }
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			throw new JDTOException(e.getLocalizedMessage());
        }//end of try~catch
        
        return intRtnVal ;
    	
    }//end of updY4YdStock()
    
    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *  
     * @param msgRecord, outRecset, intGp(1:상하차)
     * @return intRtnVal
     * @throws 
     */
    public int Y4GetYdCarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMethodName = "Y4GetYdCarsch";
    	String szMsg        = "";
    	String szOperationName              = "후판제품 차량 스케줄 Select";
        try{
        	
        	intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);
	        
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="[" + szOperationName + "] data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="[" + szOperationName + "] parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				if (intRtnVal <= 0) return intRtnVal = -2;
			}
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException(szMsg);
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y4GetYdCarsch
    
    /**
     * 오퍼레이션명 : 차량 이송재료 Update
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y4UpdCarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
    	
    	String szMethodName 		= "Y4UpdCarftmvmtl";
    	String szMsg        		= "";
    	String szOperationName      = "후판제품 차량 이송재료 Update";
        try{
        	
            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found!!";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal;
    		}
		
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            throw new JDTOException("<procY4CrnUdWr> Y4UpdCarftmvmtl" + szMsg);
	    }//end of try~catch	
		
		return intRtnVal ;
		
    }//end of Y4UpdCarftmvmtl
    
    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *  
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y4InsYdCarftmvmtl(JDTORecord msgRecord) throws JDTOException {
    	YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
    	
    	int intRtnVal = 0 ;

    	String szMethodName 		= "Y4InsYdCarftmvmtl";
    	String szMsg        		= "";
    	String szOperationName      = "후판제품 차량이송재료 Insert";
        try{
        	intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
    		if(intRtnVal == -2) {
				szMsg="[" + szOperationName + "] parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal;
    		}
        }catch(Exception e){
            /*
             * 2010.12.15 윤재광 - 예외처리
             * 차량재료 등록시 중복현상이 발생해서 에러발생.
             * 원인 : 상차스케쥴 일부분 수행중 작업취소 후 재 스케쥴 등록 작업. 
             */
        	szMsg = "차량재료 등록시 중복현상이 발생해서 에러 : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            //throw new JDTOException("<procY4CrnUdWr> Y4InsYdCarftmvmtl" + szMsg);
        }//end of try~catch
        
        return intRtnVal;
    }//end of Y4InsYdCarftmvmtl
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * 오퍼레이션명 : 후판제품비상조업실적등록
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY4CrnEmgPtopWr(JDTORecord msgRecord)throws JDTOException  {

    	
    	//업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
        //적치단클리어시 업데이트 항목
        JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        //파라미터 null check 후 받아온 Data
        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();


        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();

        int intRtnVal 					= 0 ;
        int intRowsize					= 0 ;
        
        String szMsg              		= "";
        String szMethodName       		= "procY4CrnEmgPtopWr";
        String szOperationName              = "후판제품비상조업실적등록";
        
        
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        	return;
        }
        
        	
        
        
        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

       
        
        try{



	        //파라미터 check
        	//새로 만들어야 하는 부분
	        intRtnVal = this.Y4EmerOperParamCheck(msgRecord, getParamRecord) ;
	        if(intRtnVal == -1) {
                szMsg = "파라미터 Check중 Error	: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        

	        setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
	        setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
	        setCrnschRecord.setField("MODIFIER",              "SYSTEM") ;
	        setCrnschRecord.setField("YD_EQP_ID",             getParamRecord.getFieldString("YD_EQP_ID")) ;
	        setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
	        setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;
	        setCrnschRecord.setField("YD_DN_WR_LOC",          getParamRecord.getFieldString("YD_DN_WR_LOC")) ;
	        setCrnschRecord.setField("YD_DN_WR_LAYER",        getParamRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        
	        
	        //크레인 스케줄의 Insert하기위해 스케줄의 항목의 값을 Setting하고 업데이트한다.
	        intRtnVal = this.Y4InsYdCrnsch(setCrnschRecord) ;
	        if (intRtnVal == 2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

        
	        //크레인작업재료 Insert
			setRecord.setField("YD_CRN_SCH_ID", getParamRecord.getFieldString("YD_CRN_SCH_ID")) ;
			setRecord.setField("STL_NO", 		getParamRecord.getFieldString("STL_NO")) ;
			
	        intRtnVal = this.Y4InsYdCrnWrkMtl(setRecord) ;
	        if (intRtnVal == 2) {
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }



	        //from위치정리
	        if(getParamRecord.getFieldString("STL_NO").equals("") || getParamRecord.getFieldString("STL_NO") == null) {
                szMsg = "'STL_NO' Data Error	: 재료번호가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }	        


	        //대상 데이터 SELECT			재료번호로 적치단 조회
	        intRtnVal = this.Y4GetYdStklyr(getParamRecord, getRecSet,3);
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!";
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        }

	        
	        intRowsize = getRecSet.size() ;
	        
	        getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(intRowsize == 0){
	            szMsg = "적치단에 등록된 재료번호가 없습니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        }
	        getRecord = getRecSet.getRecord();
		        
	        if(intRowsize > 0){

		        for(int i=0; i<intRowsize; i++) {
		        //클리어셋팅
		        	setRecord = JDTORecordFactory.getInstance().create();
		        	setRecord.setField("YD_STK_COL_GP", 		getRecord.getFieldString("YD_STK_COL_GP")) ;
		        	setRecord.setField("YD_STK_BED_NO", 		getRecord.getFieldString("YD_STK_BED_NO")) ;
		        	setRecord.setField("YD_STK_LYR_NO", 		getRecord.getFieldString("YD_STK_LYR_NO")) ;
		        	setRecord.setField("STL_NO",      		    "") ;
		        	setRecord.setField("MODIFIER",      		"SYSTEM") ;
		        	setRecord.setField("YD_STK_LYR_ACT_STAT", 	"E") ;
		        	setRecord.setField("YD_STK_LYR_MTL_STAT", 	"E") ;
		        	
//==========================================================================================                    
//                  기준이기 때문에 클리어 되면 안됨
//                  2009.09.25 권오창
//                    
//		        	setRecord.setField("YD_STK_LYR_XAXIS", 		"") ;
//		        	setRecord.setField("YD_STK_LYR_YAXIS", 		"") ;
//		        	setRecord.setField("YD_STK_LYR_ZAXIS", 		"") ;
//==========================================================================================                    

		        	//적치단 업데이트
		        	intRtnVal = this.Y4UpdYdStklyr(setRecord, 0) ;
			        switch (intRtnVal) {
			        	case 0	:
			                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
			        	case -1	:
			                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return ;
			        	case -2	:
			                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return ;
			        	case -3	:
			                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return ;
			        }	        
		        
			        getRecSet.next();
			        getRecord = getRecSet.getRecord();
		        }//end of for
	        }//end of if
	        
	        
	        
	        //적치단에 재료의 실적위치에 실적정보를 등록한다.
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_STK_COL_GP", 		getParamRecord.getFieldString("YD_DN_WR_LOC").substring(0,6)) ;
	        setRecord.setField("YD_STK_BED_NO", 		getParamRecord.getFieldString("YD_DN_WR_LOC").substring(6,8)) ;
	        setRecord.setField("YD_STK_LYR_NO", 		getParamRecord.getFieldString("YD_DN_WR_LAYER")) ;
	        setRecord.setField("MODIFIER", 				"SYSTEM") ;
	        setRecord.setField("STL_NO", 				getParamRecord.getFieldString("STL_NO")) ;
	        setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C") ;
	        intRtnVal = this.Y4UpdYdStklyr(setRecord, 0) ;
	        switch (intRtnVal) {
	        	case 0	:
	                szMsg = "[" + szOperationName + "] no data found!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
	        	case -1	:
	                szMsg = "[" + szOperationName + "] dup_val_on_index!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        	case -2	:
	                szMsg = "[" + szOperationName + "] parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
	                return ;
	        	case -3	:
	                szMsg = "[" + szOperationName + "] execution failed!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
	                return ;
	        }
	        
		        
		        
	            
	            
	        szMsg="후판제품 크레인 비상조업  실적 등록 완료";
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

	    }catch(Exception e) {
	    	System.out.println("Error :  "+ e.getLocalizedMessage());
	    }//end of try~catch
	    
    }// end of procY4CrnEmgPtopWr()
  
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 비상조업실적등록 파라미터 체크
     *  
     * @param  ● msgRecord, outRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4EmerOperParamCheck (JDTORecord msgRecord, JDTORecord outRecord) throws JDTOException {
    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        String szMsg                        = "";
        String szMethodName                 = "Y4EmerOperParamCheck";
        int intRtnVal = 0 ;
        
    	try{
            
    		setRecord.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
			setRecord.setField("YD_CRN_SCH_ID"			, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
			setRecord.setField("YD_SCH_CD"        		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
			setRecord.setField("STL_NO"          		, ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")) ;
			setRecord.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
			setRecord.setField("YD_UP_WR_LOC"       	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
			setRecord.setField("YD_UP_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;
			setRecord.setField("YD_DN_WR_LOC"         	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LOC")) ;
			setRecord.setField("YD_DN_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord,"YD_DN_WR_LAYER")) ;

    		
    		outRecord.addRecord(setRecord) ;
            
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal;
        }//end of try~catch
        
        intRtnVal = 1 ;
        return intRtnVal;
        
    }//end of Y4EmerOperParamCheck()
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인스케줄 Insert
     *  
     * @param  ● msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4InsYdCrnsch(JDTORecord msgRecord) throws JDTOException {
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();

    	String szMsg        = "";
    	String szMethodName = "Y4InsYdCrnsch";
    	
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnschDao.insYdCrnsch(msgRecord);		       
			if(intRtnVal == -2) {
				szMsg = "크레인 스케줄 등록중 Error!! ErrorCode: " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y4InsYdCrnsch
    
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 크레인작업재료 Insert
     *  
     * @param  ● msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4InsYdCrnWrkMtl(JDTORecord msgRecord) throws JDTOException {
    	YdCrnWrkMtlDao ydCrnwrkmtlDao = new YdCrnWrkMtlDao();

    	String szMsg        = "";
    	String szMethodName = "Y4InsYdCrnWrkMtl";
    	int intRtnVal = 0 ;

		try{
			
			intRtnVal = ydCrnwrkmtlDao.insYdCrnwrkmtl(msgRecord);		        
			if(intRtnVal == -2) {
				szMsg = "크레인 작업재료 삽입 중 Error!!, ErrorCode:" + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return intRtnVal = -1;
			}
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
		
		return intRtnVal ;
		
    }// end of Y4InsYdCrnWrkMtl
    
    
    
    
    
    
    
    
    /**
     * 오퍼레이션명 : 적치단 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y4GetYdStklyr (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szMsg = "";
    	String szMethodName = ""; 
    	int intRtnVal = 0 ;
        
        try{

	    	intRtnVal = ydStklyrDao.getYdStklyr(msgRecord, getRecSet, intGp);  
	    	
	    	outRecSet.addAll(getRecSet)  ; 
	        if (intRtnVal <= 0) {
	        	return intRtnVal;
	        }
	        
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
	    }//end of try~catch
		
		return intRtnVal ;
		
    }//end of Y4GetYdStklyr
    
    
    
    /**
	 *      [A] 오퍼레이션명 : 주편정정마감EVENT (YMCSJ001)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public int procYMCSJ001(String sGstockId)throws JDTOException  {
		
    	ymCommonDAO dao = ymCommonDAO.getInstance();
		String szMsg="";
		String szMethodName="procYMCSJ001";
		
		String CurrProg_CD="";
		String slab_prod_GP="";
		
		int intRtnVal = 0 ;
		try{
			//주편공통에서 현재 재료상태 확인						
			String queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
			List FrtStockList = dao.getCommonList(queryID, new Object[]{sGstockId});
			
			if(FrtStockList.size()>0)
			{						
		    	JDTORecord FrtStockreq = (JDTORecord)FrtStockList.get(0);				    	
		    	CurrProg_CD = StringHelper.evl(FrtStockreq.getFieldString("RECORD_PROG_STAT"),"");
		    	
		    	if(!CurrProg_CD.equals("3"))
		    	{
			    
		    	 	String sScarfingYn 		= StringHelper.evl(FrtStockreq.getFieldString("SCARFING_YN"),"");
		    	 	String sOrdYeojaeGp 	= StringHelper.evl(FrtStockreq.getFieldString("ORD_YEOJAE_GP"),"");
		    	 	String sSlabCreateGp 	= StringHelper.evl(FrtStockreq.getFieldString("SLAB_CREATE_GP"),"");
		    	 	
		    	 	ydUtils.putLog(szSessionName, szMethodName,"▶▶정정작업 sScarfingYn  ◀◀"+sScarfingYn, YdConstant.DEBUG);
		    	 	ydUtils.putLog(szSessionName, szMethodName,"▶▶정정작업 sOrdYeojaeGp ◀◀"+sOrdYeojaeGp, YdConstant.DEBUG);
		    	 	ydUtils.putLog(szSessionName, szMethodName,"▶▶정정작업 sSlabCreateGp◀◀"+sSlabCreateGp, YdConstant.DEBUG);
		    
		    	    slab_prod_GP = sGstockId.substring(0, 1); //슬라브 생산공장구분
		    	 	
		    	    if(slab_prod_GP.equals("M"))
		    	    {
		    	 	  // 일단 처리 없이 SKIP
		    	    }
		    	    else
		    	    {
		    	    	if("N".equals(sScarfingYn)){	
			    	 		if("G".equals(sSlabCreateGp)&&"2".equals(sOrdYeojaeGp)){	
			    	 		}else{
		    	 			/*
				    	     * 정정마감실적 송신
				    	     * 조건 : Non Scarfing 대상재(단,구입재이면서 여재인것은 제외)
				    	     */
			    			JDTORecord tEndRecord = null;
			    			tEndRecord = JDTORecordFactory.getInstance().create(); 
			    			tEndRecord.setField("JMS_TC_CD", "YMCSJ001");
			    			tEndRecord.setField("JMS_TC_CREATE_DDTT", new String(YdUtils.getCurDate("yyyyMMddHHmmss")));						
			    			tEndRecord.setField("MSLAB_NO",sGstockId);
					
							EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
							ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
							  	  	 new Object[]{tEndRecord});
							
							szMsg="내부IF호출=== 일관제철 B열연 정정마감실적.===";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	 		}
			    	 	}
		    	    	
		    	    }			    	    				    	 						    				    
		    	}
			}
			
			szMsg="주편정정마감EVENT 처리("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
	    }//end of try~catch
		
		return intRtnVal ;
		
	}// end of procYMCSJ001()

    
	/**
	 * 오퍼레이션명 : 차량작업진행관리 _PI
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int procY4CarWrkStatCtr_PIDEV(JDTORecord msgRecord)throws JDTOException  {
		//TC_CODE :YDYDJ630
		
		YdDelegate ydDelegate = new YdDelegate();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao(); 
		YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
		YdCarSchDao     ydCarSchDao     = new YdCarSchDao();  
		YdStockDao      ydStockDao      = new YdStockDao();  
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecordSet rsCrnWrkMtl		= null;
		JDTORecord	  recMtl			= null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recFirst          = null;
		JDTORecord    recLast           = null;
		JDTORecord	  inRecord			= null;
		
	    int intRtnVal 		   			= 0 ;
	    
	    String szMsg           			= "";
	    String szMethodName    			= "procY4CarWrkStatCtr_PIDEV";
	    String szOperationName    		= "차량작업진행관리(후판제품(PI))";
	    
	    String szCAR_LDUD_GP   			= "";
	    String szYD_WBOOK_ID   			= "";
	    String szYD_CRN_SCH_ID 			= "";
	    String szFST_CRN_SCH_ID 		= "";
	    String szLST_CRN_SCH_ID 		= "";
	    String szYD_SCH_CD      		= "";
	    String szYD_GP          		= "";
	    String szYD_CAR_SCH_ID  		= "";
	    String szYD_CAR_USE_GP  		= "";
	    String szYD_DN_WR_LOC   		= "";
	    String szCAR_NO					= null;
	    String szCARD_NO				= null;
	    String szSPOS_WLOC_CD			= null;
	    String szYD_PNT_CD				= null;
	    String szTRANS_ORD_DATE			= null;
	    String szTRANS_ORD_SEQNO		= null;
	    String sTelNo 					= null;
	    String szCARLD_PNT_CD			= "";
	    String sCAR_KIND			    = "";
	    
	    try{
	    	//--------------------------------------------------------------------------------------------------
	    	//	파라미터 확인
	    	//--------------------------------------------------------------------------------------------------
	    	szMsg="["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
	    	//상하차구분 플래그, 작업예약id, 크레인스케줄id
	    	szCAR_LDUD_GP   = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_LDUD_GP"); 
	    	szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	    	szYD_DN_WR_LOC  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC");
	    	//--------------------------------------------------------------------------------------------------
	    	
	    	//출하고도화 이후 파라메터로 크레인작업재료를 JDTORecordSet 형식으로 받는다.
	    	rsCrnWrkMtl = (JDTORecordSet)msgRecord.getField("CRN_WRK_MTLS_SET");
	    	
	    	//해송출하 이후 파라메터로 받은 권하실적위치를 가지고 상차포인트를 편집하여 사용한다.
	    	if(!"".equals(szYD_DN_WR_LOC)) {
	    		if(szYD_DN_WR_LOC.length()>=6) {
	    			szCARLD_PNT_CD = szYD_DN_WR_LOC.substring(0,1) + szYD_DN_WR_LOC.substring(4,5) + szYD_DN_WR_LOC.substring(1,2) + szYD_DN_WR_LOC.substring(5,6);
	    		}
	    	}
	    	
	    	
	    	//--------------------------------------------------------------------------------------------------
	    	// 작업예약ID로 크레인 스케줄 조회
	    	//--------------------------------------------------------------------------------------------------
	    	szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	    	
	    	if(szCAR_LDUD_GP.equals("L")){
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 31);
		    	if(intRtnVal <= 0) {
					szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄을 조회 중 Error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new JDTOException("<procY1CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}else{
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 32);
		    	if(intRtnVal <= 0) {
					szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄을 조회 중 Error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new JDTOException("<procY1CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}
	    	
	    	
	    	szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄 조회 성공 - 대상재건수["+rsResult.size()+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
			
	    	rsResult.first();
	    	recFirst = JDTORecordFactory.getInstance().create();
	    	recFirst.setRecord(rsResult.getRecord());
	    	szFST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recFirst, "YD_CRN_SCH_ID");
	    	
	    	rsResult.last();
	    	recLast = JDTORecordFactory.getInstance().create();
	    	recLast.setRecord(rsResult.getRecord());
	    	szLST_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(recLast, "YD_CRN_SCH_ID");
	    	
	    	szYD_SCH_CD      = ydDaoUtils.paraRecChkNull(recFirst, "YD_SCH_CD");
	    	szYD_GP          = szYD_SCH_CD.substring(0,1);
	    	
	    	
	    	szMsg="["+ szOperationName +"] 파라미터로 전달된 크레인스케줄["+szYD_CRN_SCH_ID+"], 첫번째 크레인스케줄["+szFST_CRN_SCH_ID+"], 마지막 크레인스케줄["+szLST_CRN_SCH_ID+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	//--------------------------------------------------------------------------------------------------

	    	
	    	//--------------------------------------------------------------------------------------------------
			//	상차/하차 처리
			//--------------------------------------------------------------------------------------------------
	    	
	    	//플래그가 상차인경우
	    	if(szCAR_LDUD_GP.equals("U")) {
	    		//--------------------------------------------------------------------------------------------------
	    		//	상차인 경우 상차작업예약ID로 차량스케줄을 조회
	    		//--------------------------------------------------------------------------------------------------
	    		szMsg="["+ szOperationName +"] 상차인 경우 상차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
//////////////////
    	    	YdEqpDao   ydEqpDao   = new YdEqpDao();
    			JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
    			JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
    			JDTORecord 		outRecord1  = JDTORecordFactory.getInstance().create();
    			String szAPPLY_YN 			= "N";
    			
    			if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP)) {
    				inRecord1.setField("REPR_CD_GP", "K00110");    //차량정보 SKIP
    			} else {
    				inRecord1.setField("REPR_CD_GP", "T00110");    //차량정보 SKIP
    			}
    			
    			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
    			intRtnVal = ydEqpDao.getYdEqp(inRecord1, outResult, 999);
    			if(intRtnVal > 0) {
    				outResult.first();
    				outRecord1  = outResult.getRecord();
    				szAPPLY_YN = outRecord1.getFieldString("ITEM1");				
    			}
    			szMsg="차량정보 SKIP 적용 " + szAPPLY_YN ;
    			ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);				

    			if (szAPPLY_YN.equals("Y")) {
//SJH07001:권하시 차량 작업 error 발생시 skip
    				//com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschUDLDWRKBOOKID_PIDEV
//PIDEV_S :병행가동용:PI_YD
    				recInTemp.setField("PI_YD",    	szYD_GP);	    				
    				intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
    	    		if (intRtnVal <= 0){
    		    		szMsg = "차량에서 상차작업 처리시 차량스케쥴 정보 오류발생.--> SKIP처리 함";
    		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	   
    		            return 1;
    		    	}
    			} else {
//PIDEV_S :병행가동용:PI_YD
    				recInTemp.setField("PI_YD",    	szYD_GP);	    				
    				ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
    			}
	    		
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		
	    		szYD_CAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    		szYD_CAR_USE_GP 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP");
	    		szCAR_NO 				= ydDaoUtils.paraRecChkNull(recOutTemp, "CAR_NO");
	    		szCARD_NO				= ydDaoUtils.paraRecChkNull(recOutTemp, "CARD_NO");
	    	    szSPOS_WLOC_CD			= ydDaoUtils.paraRecChkNull(recOutTemp, "SPOS_WLOC_CD");
	    	    szYD_PNT_CD				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PNT_CD1");
	    	    szTRANS_ORD_DATE		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_DATE");
	    	    szTRANS_ORD_SEQNO		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_SEQNO");
	    	    sTelNo					= ydDaoUtils.paraRecChkNull(recOutTemp, "TEL_NO");
	    	    sCAR_KIND				= ydDaoUtils.paraRecChkNull(recOutTemp, "CAR_KIND");

	    	    
	    		szMsg="["+ szOperationName +"] 상차인 경우 상차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄["+szYD_CAR_SCH_ID+"], 차량사용구분["+szYD_CAR_USE_GP+"] 조회 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		//--------------------------------------------------------------------------------------------------
	    		
	    		
	    		//--------------------------------------------------------------------------------------------------
	    		//출하차량인 경우에만 적용한다. - 일품 상차실적 송신 YDDMR012 (후판일품출하상차실적)
	    		//--------------------------------------------------------------------------------------------------
	    		if(szYD_CAR_USE_GP.equals("G")){
	    			
					//--출하고도화 이후 방식-----------------------------------------------------------------
	    			
					szMsg="["+ szOperationName +"] 출하차량 일품 상차실적 송신 M10YDLMJ1082(YDDMR012) (후판일품출하상차실적) 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
					recInTemp = JDTORecordFactory.getInstance().create();
					
	    			for(int ii = 0; ii < rsCrnWrkMtl.size(); ii++) {
	    				
	    				recMtl = rsCrnWrkMtl.getRecord(ii);
//PIDEV			
						recInTemp.setField("MQ_TC_CD"    ,       "M10YDLMJ1082");
	    				recInTemp.setField("YD_GP",              szYD_GP);
	    				recInTemp.setField("TRANS_WORD_DATE",    szTRANS_ORD_DATE);
	    				recInTemp.setField("TRANS_WORD_SEQNO",   szTRANS_ORD_SEQNO);
	    				recInTemp.setField("CAR_NO",             szCAR_NO);
	    				recInTemp.setField("CARD_NO",            szCARD_NO);
	    				recInTemp.setField("GOODS_NO",           recMtl.getFieldString("STL_NO"));
	    				recInTemp.setField("CARLD_PNT_CD",       szCARLD_PNT_CD);
	    				
						if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID) && (ii+1) == rsCrnWrkMtl.size()) {
							//마지막 스케줄 ID이고 작업재료의 마지막일경우
							recInTemp.setField("GOODS_EA","*");
						} else {
							recInTemp.setField("GOODS_EA","1");
						}
	    				
						ydDelegate.sendMsg(recInTemp);
	    			}
					
					szMsg="["+ szOperationName +"] 출하차량 일품 상차실적 송신 M10YDLMJ1082(YDDMR012) (후판일품출하상차실적) 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	    			

					
					for(int ii = 0; ii < rsCrnWrkMtl.size(); ii++) {
						
						recMtl = rsCrnWrkMtl.getRecord(ii);
						
						recInTemp.setField("STL_NO",	recMtl.getFieldString("STL_NO"));
//PIDEV						
	    				//검수 테이블 생성 //////////////////////////////////////////////////////////////
	    				// ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW2_PIDEV
	    				intRtnVal = ydStockDao.updYdStockExa_PIDEV(recInTemp, 0);			
	    				if(intRtnVal >0){
	    					szMsg = "수신한 재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록이 되었습니다.";
	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
	    				}else if(intRtnVal == 0){
	    					szMsg = "수신한  재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록 되었거나  실패 하였습니다.";
	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	 			}
	    				///////////////////////////////////////////////////////////////////////////////		
					}
	    		}
	    		//--------------------------------------------------------------------------------------------------
	    		
	    		
	    		//--------------------------------------------------------------------------------------------------
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		//--------------------------------------------------------------------------------------------------
	    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			//--------------------------------------------------------------------------------------------------
	    			//동일하면 차량스케줄에 상차완료일시 등록, 설비작업상태 = '영차'
	    			//--------------------------------------------------------------------------------------------------
	    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_CAR_PROG_STAT", "5");
	    			recInTemp.setField("YD_EQP_WRK_STAT", "L");
	    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 1);
	    			if(intRtnVal <= 0) {
	    				szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 시 오류" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			
	    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			//--------------------------------------------------------------------------------------------------
	    			
	    			
	    			//--------------------------------------------------------------------------------------------------
	    			//	상차완료 전문 송신
	    			//--------------------------------------------------------------------------------------------------
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			
	    			if(szYD_CAR_USE_GP.equals("L")){							//구내운송 - 임춘수 수정 2009.06.15
	    				//--------------------------------------------------------------------------------------------------
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 시작 2009.06.08 임춘수 추가 ----------------------
	    				//--------------------------------------------------------------------------------------------------
	    				//intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
	    				szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차완료 시 공통 테이블 업데이트 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    				String szRtnMsg = YdCommonUtils.procCarLoadCmplForPlateGds(szYD_CAR_SCH_ID, YdConstant.YD_STR_LOC_HIS1, szMethodName);
	    				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    					szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차완료 시 공통 테이블 업데이트 성공";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    				}else{
	    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
	    					szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차완료 시 공통 테이블 업데이트 실패";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				}
	    				//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------
	    				
		    			//상차작업완료 송신 YDTSJ008 (구내운송 상차완료)
		    			recInTemp.setField("MSG_ID",        "YDTSJ008");
						szMsg="["+ szOperationName +"] 구내운송 상차작업완료 송신 : YDTSJ008";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
		    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
		    			recInTemp.setField("YD_GP",         szYD_GP);
		    			
		    			ydDelegate.sendMsg(recInTemp);
		    			
						szMsg="["+ szOperationName +"] 상차작업완료 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
	    			}else{
    					/*
    	    			 * 01. 후판출하상차완료전문 송신가능여부 체크
    	    			 *     - 야드에 출하대상재가 남아있는지 체크
    	    			 */
    	    			JDTORecordSet rsPara = JDTORecordFactory.getInstance().createRecordSet("");
    	    			JDTORecord recPara   = JDTORecordFactory.getInstance().create();
    	    			
   	    				recPara.setField("CAR_NO", 			    szCAR_NO);	
    	    			recPara.setField("TRANS_ORD_DATE",  	szTRANS_ORD_DATE);
    	    			recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
//PIDEV_S :병행가동용:PI_YD
    					recPara.setField("PI_YD",    	szYD_GP);		
    	    			/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockPlateLotCnt_PIDEV */ 
    	    			int nRtnVal = ydStockDao.getYdStock_DoubleDong(recPara, rsPara, "3");
	    			
    	    			if(nRtnVal <= 0){
	    					return 1;
	    				}
    	    			
    	    			rsPara.first();
    	    			JDTORecord recGetVal = rsPara.getRecord();	
    	    			
    	    			int iResultCnt = ydDaoUtils.paraRecChkNullInt(recGetVal, "CNT");
    	    			
    	    			szMsg="["+ szOperationName +"] 복수동 상차작업여부 대상재 갯수 = " + iResultCnt;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
    	    			/*
    	    			 * 02. 없으면  - 상차완료처리 송신
    	    			 */
    	    			if(iResultCnt == 0){
    	    				
	    	    			//상차작업완료 송신 YDDMR016 (후판출하상차완료)
    	    				recPara = JDTORecordFactory.getInstance().create();
//    	    				recPara.setField("MSG_ID",    					YdConstant.YDYDJ701);
// 							recPara.setField(YdConstant.BUFFER_TC_CD, 		"YDDMR016");
    	    				recPara.setField("MQ_TC_CD"      , "M10YDLMJ1092");
    	    				recPara.setField("YD_SCH_CD"     , szYD_SCH_CD);
    	    				recPara.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
    	    				recPara.setField("YD_GP"         , szYD_GP);
    	    				recPara.setField("CARLD_PNT_CD"  , szCARLD_PNT_CD);
			    			
			    			ydDelegate.sendMsg(recPara);
			    			
							szMsg="["+ szOperationName +"] 상차작업완료 송신 완료";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

    	    			}else{
    	    				
    	    				if(sCAR_KIND.startsWith("P")){
    	    					
    	    				}else{
    	    					/*
    	    	    			 * 03. 있으면 - 상차완료처리 SKIP
    	    	    			 *             대신, 현차량에 대한 대기장 입동지시 SMS 문자를 발송한다.
    	    	    			 *                   출하완료전문을 발생시켜 차량자동출발처리한다(DMYDR031).
    	    	    			 *                   추가로 나머지 대상재 출하작업을 진행시킨다.(DMYDR021 - 추가파라미터 필요)
    	    	    			 */	
    	    	    				recPara = JDTORecordFactory.getInstance().create();
    	    	    				recPara.setField("TC_CODE",        		"DMYDR042");									//전문코드
    	    	    				//DMYDR042 -> M10LMYDJ1082
    	    	    				recPara.setField("YD_GP", 				szYD_GP);
    	    	    				recPara.setField("CARD_NO", 			szCARD_NO);
    	    	    				recPara.setField("CAR_NO", 				szCAR_NO);			
    	    	    				recPara.setField("SPOS_WLOC_CD", 		szSPOS_WLOC_CD);
    	    	    				recPara.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD);
    	    	    				recPara.setField("TRANS_ORD_DT", 		szTRANS_ORD_DATE);
    	    	    				recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
    	    	    				
    	    	    				szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 시작";
    		    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		    					
    		    					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
    		    					ejbConn.trx("rcvPlGdsDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recPara });
    		    					
    		    					szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 완료";
    		    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		    					
    		    					//---------------------------------------------------------------------------------------------------------
    		    					//육송출하고도화
    		    					//DMYDR061
    		    					recPara = JDTORecordFactory.getInstance().create();
    	    	    				recPara.setField("TC_CODE",        		"DMYDR061");									//전문코드
    	    	    				recPara.setField("CARD_NO", 			szCARD_NO);
    	    	    				recPara.setField("CAR_NO", 				szCAR_NO);			
    	    	    				recPara.setField("TRANS_ORD_DT", 		szTRANS_ORD_DATE);
    	    	    				recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
    	    	    				recPara.setField("DOUBLEDONG_CHECK", 	"Y");//복수동 나머지 출하여부
    	    	    				recPara.setField("YD_CAR_SCH_ID",       szYD_CAR_SCH_ID);
    	    	    				
    	    	    				ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);
    	    	    				
    	    	    				// 전사물류개선 2021. 1. 6 신규로직 분기여부(대기장도착처리)
    	    	    				if(PlateGdsYdUtil.isPlateNewMoudleApply("DM")){
    	    	    					ejbConn.trx("procStandByYdArrivePlate4G", new Class[] { JDTORecord.class }, new Object[] { recPara });
    	    	    				}else{
    	    	    					ejbConn.trx("procStandByYdArrivePlate", new Class[] { JDTORecord.class }, new Object[] { recPara });
    	    	    				}
    	    	    				
    								szMsg="["+ szOperationName +"] 복수동 나머지 출하Lot DMYDR061 송신 완료";
    								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
    	    				}
	    				}
	    	    	}
	    		}
	    	//--------------------------------------------------------------------------------------------------
	    	//플래그가 하차인 경우
	    	//--------------------------------------------------------------------------------------------------
	    	}else if(szCAR_LDUD_GP.equals("L"))	{
	    		//--------------------------------------------------------------------------------------------------
	    		//	하차인 경우 하차작업예약ID로 차량스케줄을 조회
	    		//--------------------------------------------------------------------------------------------------
	    		szMsg="["+ szOperationName +"] 하차인 경우 하차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		recInTemp  = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			rsResult   = JDTORecordFactory.getInstance().createRecordSet("");
    			
//PIDEV_S :병행가동용:PI_YD
    			recInTemp.setField("PI_YD",    	szYD_GP);			
    			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		if (intRtnVal <= 0){
		    		szMsg = "차량에서 하차작업 처리시 차량스케쥴 정보 오류발생.";
		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		            return 1;
		    	}
	    		rsResult.absolute(1);
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsResult.getRecord()); 
	    		
	    		szYD_CAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
	    		szYD_CAR_USE_GP 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP");
	    		szCAR_NO 				= ydDaoUtils.paraRecChkNull(recOutTemp, "CAR_NO");
	    		szCARD_NO				= ydDaoUtils.paraRecChkNull(recOutTemp, "CARD_NO");
	    	    szSPOS_WLOC_CD			= ydDaoUtils.paraRecChkNull(recOutTemp, "ARR_WLOC_CD");
	    	    szYD_PNT_CD				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PNT_CD1");
	    	    szTRANS_ORD_DATE		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_DATE");
	    	    szTRANS_ORD_SEQNO		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_SEQNO");
	    	    sTelNo					= ydDaoUtils.paraRecChkNull(recOutTemp, "TEL_NO");	    		
	    		
	    		
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    			recInTemp.setField("YD_GP",         szYD_GP);
    			
    			szMsg="["+ szOperationName +"] 하차인 경우 하차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		//--------------------------------------------------------------------------------------------------
    			
	    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
	    		//--------------------------------------------------------------------------------------------------
	    		if(szLST_CRN_SCH_ID.equals(szYD_CRN_SCH_ID)) {
	    			//--------------------------------------------------------------------------------------------------
	    			//동일하면 차량스케줄에 하차완료일시 등록
	    			//--------------------------------------------------------------------------------------------------
	    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 하차작업예약ID["+szYD_WBOOK_ID+"]의 하차완료 수정 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_EQP_WRK_STAT", "U");
	    			recInTemp.setField("YD_CAR_PROG_STAT", "E");
	    			recInTemp.setField("DEL_YN",           "N");
	    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 3);
	    			if(intRtnVal <= 0) {
						szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 하차작업예약ID["+szYD_WBOOK_ID+"]의 하차완료 수정 시 Error!! Code : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 하차작업예약ID["+szYD_WBOOK_ID+"]의 하차완료 수정 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//--------------------------------------------------------------------------------------------------
					
					if(YdConstant.YD_CAR_USE_GP_TS.equals(szYD_CAR_USE_GP)){
						//--------------------------------------------------------------------------------------------------
		    			//---------------- 하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 추가
						//--------------------------------------------------------------------------------------------------
		    			szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 하차완료 시 공통 테이블 업데이트 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
		    			//intRtnVal = YdCommonUtils.procCarUnLoadCmpl(szYD_CAR_SCH_ID);
						String szRtnMsg = YdCommonUtils.procCarUnLoadCmplForPlateGds(szYD_CAR_SCH_ID, YdConstant.YD_STR_LOC_CURR);
	    				//if( intRtnVal <= 0 ) {
	    				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
	    					szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 하차완료 시 공통 테이블 업데이트 처리 실패";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				}else{
	    					szMsg="["+ szOperationName +"] 구내운송 차량스케줄["+szYD_CAR_SCH_ID+"]의 하차완료 시 공통 테이블 업데이트 처리 성공";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    				}
		    			//-----------------하차완료시 공통테이블 업데이트 시작 2009.06.08 임춘수 끝
	 
	    				
						//하차작업완료 송신 YDTSJ010
						recInTemp.setField("MSG_ID",        "YDTSJ010");
						recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
						recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						recInTemp.setField("YD_GP",         szYD_GP);
						ydDelegate.sendMsg(recInTemp);
					}
					

    				szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

    				//---------------------------------------------------------------------------------------------------------
    				JDTORecord recPara   = JDTORecordFactory.getInstance().create();
    				recPara.setField("TC_CODE",        		"DMYDR042");									//전문코드
    				recPara.setField("YD_GP", 				szYD_GP);
    				recPara.setField("CARD_NO", 			szCARD_NO);
    				recPara.setField("CAR_NO", 				szCAR_NO);			
    				recPara.setField("SPOS_WLOC_CD", 		szSPOS_WLOC_CD);
    				recPara.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD);
    				recPara.setField("TRANS_ORD_DT", 		szTRANS_ORD_DATE);
    				recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
					ejbConn.trx("rcvPlGdsDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recPara });
					
					szMsg= "["+ szOperationName +"] 차량번호[" + szCAR_NO + "]는 자동으로 차량출발 모듈 EJB 호출 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				//---------------------------------------------------------------------------------------------------------
					
		    	    //--------------------------------------------------------------------
	            	// 전사물류개선 2021. 1. 6 
	            	//  - 다른 하차지 검색하여 복수동상차로직과 동일하게 처리한다. 
	        		//   : 하차지 결정은 대기장도착처리(DMYDR061)에서 처리하도록 한다.
	        		//
	        		//  - 차량스케쥴 재편성하여 대기장도착처리(DMYDR061)
	    			//  - 마지막일 경우 기존과 동일한 방식으로 처리함
	            	//----------------------------------------------------------------------
					if(PlateGdsYdUtil.isPlateNewMoudleApply("DM")){
						if(YdConstant.YD_CAR_USE_GP_DM.equals(szYD_CAR_USE_GP)){

		        			int nTRANS_ORD_SEQNO = 0;
		        			try{ nTRANS_ORD_SEQNO = Integer.parseInt(szTRANS_ORD_SEQNO); }catch(Exception e){nTRANS_ORD_SEQNO=0;};
		        			
		        			// 차량입고(반품) 운송지시번호 999000 보다 큰 건
			        		if(nTRANS_ORD_SEQNO>999000){
			        			// 1. 다음하차정지위치 찾기
			        			
			        			JDTORecord params = JDTORecordFactory.getInstance().create();
			        			JDTORecordSet rsCarStopLoc = JDTORecordFactory.getInstance().createRecordSet("");
			        			YdPlateCommDAO  commDao 	  = new YdPlateCommDAO();
			        			ejbConn = null;
			        			
			        			// 차량에 위에 아직도 재료가 존재하는가?
			        			params.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			        			if( commDao.select(params, rsCarStopLoc, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarUnloadStopLoc") > 0){
			        				 
			        				// 2. DMYDR061 도착전문전송
			    					JDTORecord send_DMYDR061= JDTORecordFactory.getInstance().create();
			    					send_DMYDR061.setField("TC_CODE",        	"DMYDR061");									//전문코드
			    					send_DMYDR061.setField("CARD_NO", 			szCARD_NO);
			    					send_DMYDR061.setField("CAR_NO", 			szCAR_NO);			
		    	    				send_DMYDR061.setField("TRANS_ORD_DT", 		szTRANS_ORD_DATE);
		    	    				send_DMYDR061.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
		    	    				send_DMYDR061.setField("DOUBLEDONG_CHECK", 	"Y");//복수동 나머지 출하여부
		    	    				send_DMYDR061.setField("YD_CAR_SCH_ID",       szYD_CAR_SCH_ID);
		    	    				
		    	    				ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);
		    	    				ejbConn.trx("procStandByYdArrivePlate4G", new Class[] { JDTORecord.class }, new Object[] { send_DMYDR061 });
		    						
									szMsg="[권상실적처리] 복수동하차 나머지 출하Lot DMYDR061 송신 완료";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			        			}
			        		}
		        		}
					}
	    			
					szMsg="["+ szOperationName +"] 하차작업완료 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}
	    		//--------------------------------------------------------------------------------------------------
	    	}else{
				szMsg="["+ szOperationName +"] 상차 및 하차 구분 플래그 Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
	    	}

		}catch(Exception e){
	
			szMsg="["+ szOperationName +"] 차량 작업 진행관리 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException("<procY4CrnUdWr> =" + szMsg);
		}
	
	
		szMsg="["+ szOperationName +"] 차량 작업 진행관리 처리"+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	}
	
	/**
     * 후판창고 BED 타입변경 처리 : 완산베드 변경
     * @param sYdLocation
     * @return
     * @throws DAOException
     */
    public String procChangeBedTypeForPlateGdsToFull(String sYdLocation,
                                                        String szMethodName, String logId) throws DAOException {
        int intRtnVal           = 0;
        int intMvCnt            = 0;
        //메세지
        String szMsg            = "";
        String szSessionName    = "후판창고 BED 타입변경 처리 : 완산베드 변경";

        JDTORecord recInTemp    = null;
        JDTORecord recOutTemp   = null;
        JDTORecord recSndTemp   = null;

        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

        YdStkBedDao    ydStkBedDao    = new YdStkBedDao();
        YdStkLyrDao    ydStkLyrDao    = new YdStkLyrDao();
        YdEqpDao       ydEqpDao       = new YdEqpDao();

        JDTORecord recInPara = null;
        JDTORecordSet rsChkBed = null;
        YdPlateCommDAO  commDao       = new YdPlateCommDAO();


		if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T"); 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번
		
		szMsg = "후판창고 BED 타입변경 처리 : 후판제품용선별(" + szMethodName + ") 시작";
		ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.INFO, logId);


        try{
            szMsg = "[" + szSessionName + "] 저장위치 : " + sYdLocation;

            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            if(sYdLocation.length() != 8){
                return YdConstant.RETN_CD_FAILURE;
            }
            String szYD_STK_COL_GP = sYdLocation.substring(0, 6);
            String szYD_STK_BED_NO = sYdLocation.substring(6, 8);
            
            boolean isNormalYd = sYdLocation.substring(2, 4).matches("\\d\\d");
            
            if(!isNormalYd){
            	szMsg = "일반야드가 아닌경우 완산처리 하지 않음";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            	 return YdConstant.RETN_CD_FAILURE;
            }

            recInTemp = JDTORecordFactory.getInstance().create();

            recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
            recInTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
            /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedAll*/
            intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, getRecSet, 313);

            if (intRtnVal <= 0) {
                szMsg = "[" + szSessionName + "] BED정보 조회중 Error!! Code : " + intRtnVal;
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                throw new JDTOException("<procY4CrnUdWr> getYdStkbed :" + szMsg);
            }

            getRecSet.first();
            recOutTemp = JDTORecordFactory.getInstance().create();
            recOutTemp.setRecord(getRecSet.getRecord());

            String szYD_STK_BED_WHIO_STAT   = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_WHIO_STAT");
            String szYD_STK_BED_SEL_GP      = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_SEL_GP");

            szMsg = "[szYD_STK_BED_WHIO_STAT]:"+szYD_STK_BED_WHIO_STAT;
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            szMsg = "[szYD_STK_BED_SEL_GP]:"+szYD_STK_BED_SEL_GP;
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            

            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
            // 출하LOTID COUNT
            /* com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getStlMoveSlCnt */
            intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, getRecSet, 618);

            if (intRtnVal < 0) {
                szMsg = "[" + szSessionName + "] 선별LOT 편성 재료수 조회중 Error!! Code : " + intRtnVal;
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                throw new JDTOException("[" + szSessionName + "] getYdStklyr :" + szMsg);
            }

            getRecSet.first();
            recOutTemp = JDTORecordFactory.getInstance().create();
            recOutTemp.setRecord(getRecSet.getRecord());

            intMvCnt = ydDaoUtils.paraRecChkNullInt(recOutTemp, "MV_CNT");
            szMsg = "[intMvCnt]:"+intMvCnt;
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            if(intMvCnt == 0) {  //MV_CNT: 해당 베드의 CAR_LOT ID가 있는 재료의 개수
            	szMsg = "[" + szSessionName + "] 권하베드 CAR_LOT Id 있는 재료 없음";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
            	return YdConstant.RETN_CD_SUCCESS;
            }
            recInTemp.setField("YD_STK_BED_SEL_GP",       "E");

            if(!szYD_STK_BED_WHIO_STAT.equals(YdConstant.YD_STK_BED_WHIO_FULL)){  //현재 완산베드아니면 완산베드로 변경.
                szMsg = "szYD_STK_COL_GP:"+szYD_STK_COL_GP+" szYD_STK_BED_NO:"+szYD_STK_BED_NO+" 베드의 재료 정보 조회(운송지시대기 존재 여부 확인)";
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

                recInTemp.setField("YD_STK_BED_WHIO_STAT",  "F");
            } 

            recInTemp.setField("YD_USER_ID",              "Y8YDL008");
            
            recInTemp.setField("NEXT_YD_STK_BED_SEL_GP",       "S");


            /*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updPlateYdSelList*/
            intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 400);
            if (intRtnVal < 0) {
                szMsg = "[Jsp Session : "+szSessionName+"] 적치열구분["+szYD_STK_COL_GP+"] 적치BED번호[" + szYD_STK_BED_NO + "]를 완산베드[F]로 수정 시 오류발생 : 루프 계속처리 - 반환값 : " + intRtnVal;
                ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
                throw new JDTOException("<procY4CrnUdWr> updYdStkbed :" + szMsg);
            }

            szMsg = "[Jsp Session : "+szSessionName+"] 적치열구분["+szYD_STK_COL_GP+"] 적치BED번호[" + szYD_STK_BED_NO + "]를 완산베드[F]로 수정 완료 ";
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            //L2 로 적치열 수정된 정보를 내려보내준다.
            recSndTemp =  JDTORecordFactory.getInstance().create();
            if(szYD_STK_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
                recSndTemp.setField("MSG_ID",           "YDY8L001");
                if(PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szYD_STK_COL_GP)){
                    recSndTemp.setField("MSG_ID",           "YDY9L001");
                }

                recSndTemp.setField("YD_GP",            YdConstant.YD_GP_PLATE2_GDS_YARD);
            } else {
                recSndTemp.setField("MSG_ID",           "YDY8L001");
                recSndTemp.setField("YD_GP",            YdConstant.YD_GP_PLATE_GDS_YARD);
            }
            recSndTemp.setField("YD_INFO_SYNC_CD",  "4");
            recSndTemp.setField("YD_STK_COL_GP",    szYD_STK_COL_GP);
            recSndTemp.setField("YD_STK_BED_NO",    szYD_STK_BED_NO);

            szMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 시작" ;
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);

            recSndTemp.setField("LOG_ID", logId);  // 전문에 있는 logId

            ydDelegate.sendMsg(recSndTemp);

            szMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 완료" ;
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.DEBUG, logId);
                
            
        }catch(Exception e) {
            szMsg = "["+szMethodName+"] 후판창고 선별 BED 타입변경  처리시 예외메세지: " + e.getMessage();
            ydUtils.putLogNew(szSessionName, szMethodName, szMsg, YdConstant.ERROR, logId);
        }
        
        return YdConstant.RETN_CD_SUCCESS;
    }
    
} // end of class CraneUdHdSeEJBBean
