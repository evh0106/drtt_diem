/*
 * @(#) 2후판정정야드 권하실적처리 Session EJB
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/20
 *
 * @description		권하실적처리 Session EJB
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/20   김현우      김현우       최초작성  
 */

package com.inisteel.cim.yd.jplateyd.session; 

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarFtmvMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ptPlateCommDao.PtPlateCommDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
//-------------------------------------------------------------------------------------------------------------------------
//2024.11.21 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;

/**
 * 권하실적처리 Session EJB
 *
 * @ejb.bean name="JPlateYdCrnUnloadWrkSeEJB" jndi-name="JPlateYdCrnUnloadWrkSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdCrnUnloadWrkSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	// Session Name
	private static final String SZ_SESSION_NAME = JPlateYdCrnUnloadWrkSeEJBBean.class.getName();

	private JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
	private JPlateYdDaoUtils	ydDaoUtils  = new JPlateYdDaoUtils();
//	private JPlateYdDelegate 	ydDelegate  = new JPlateYdDelegate();
	private EJBConnector 	 	ydEjbCon    = new EJBConnector("default", this);

	// [DEBUG] message flag
	private boolean bDebugFlag = true;
	
	private YdPICommDAO   ydPICommDAO   = new YdPICommDAO();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
     * 오퍼레이션명 : 2후판정정 권하실적처리 (Y7YDL009)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY7CrnDnWr(JDTORecord msgRecord) throws DAOException, JDTOException {

    	JPlateYdDelegate      	ydDelegate		= new JPlateYdDelegate();
    	JPlateYdEqpDAO        	ydEqpDao        = new JPlateYdEqpDAO();
    	JPlateYdTcarSchDAO    	ydTcarSchDao    = new JPlateYdTcarSchDAO();
    	JPlateYdStockDAO      	ydStockDao      = new JPlateYdStockDAO();
    	JPlateYdWrkbookDAO    	ydWrkbookDao    = new JPlateYdWrkbookDAO();
    	JPlateYdCrnWrkMtlDAO  	ydCrnWrkMtlDao	= new JPlateYdCrnWrkMtlDAO();
    	JPlateYdWrkbookMtlDAO 	ydWrkbookMtlDao	= new JPlateYdWrkbookMtlDAO();
    	JPlateYdCrnSchDAO 		ydCrnschDao 	= new JPlateYdCrnSchDAO();

        int intRtnVal = 0;

        //DATA SETTING시 사용
        JDTORecord setRecord 		= JDTORecordFactory.getInstance().create();

        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord        = JDTORecordFactory.getInstance().create();

        //작업예약 업데이트 항목
        JDTORecord bookrecord       = JDTORecordFactory.getInstance().create();

        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 	= JDTORecordFactory.getInstance().create();

        JDTORecord recInTemp        = null;
        JDTORecord recOutTemp       = null;
        JDTORecord recInPara        = null;

        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 	= null;

        JDTORecord    recSendMsg	= null;
        JDTORecordSet rsResult      = null;

        String 	szMsg            	= "";
        String 	szRtnMsg            = "";
        String	szCallMsg			= "";
        String 	szSendMsg           = "";
        String 	szMethodName     	= "procY7CrnDnWr";
        String 	szOperationName  	= "2후판정정 권하실적처리";
        String 	szTcarEqpId      	= "";

        //크레인 XYZ축 				파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
        String 	szYdCrnXaxis     	= "";
        String 	szYdCrnYaxis     	= "";
        String 	szYdCrnZaxis     	= "";
        String 	szYdWbookId     	= "";
        String 	szCrnSchId       	= "";

        String 	szYdUpWrLoc      	= "";
        String 	szYdDnWrLoc      	= "";
        String 	szYdSchCd        	= "";
        String 	szYdEqpId        	= "";
        String 	szYdDnWrLayer    	= "";
        String 	szYdTcarSchId    	= "";
        String	szModifier			= "";
        String	szNextSchCallYn		= "N";		// 권하 완료후 다음 스케줄 호출 Flag - 작업예약 삭제후 Set됨
        String	szStlNo				= "";

        String[]	arrStlNo		= {"","","","","" ,"","","","","" ,"","","","",""};
		String[]	arrYdDnWrLoc	= {"","","","","" ,"","","","","" ,"","","","",""};
		String[]	arrYdDnWrLayer	= {"","","","","" ,"","","","","" ,"","","","",""};
		String[]	arrYdDnWrBedNo	= {"","","","","" ,"","","","","" ,"","","","",""};

        String 	szYdStkColGp		= "";
        String 	szYdStkBedNo     	= "";
        String 	szRealTopLyr     	= "";
        String	szArrStlNo			= "";
        String	szTemp				= "";
        String	szYdUpWrkActGp		= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
  		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

  		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
          							
        int		iMtlCnt				= 0;		// 권하실적 재료 갯수

        String 	szRcvTcCode			= ydUtils.getTcCode(msgRecord);
        if (szRcvTcCode==null || "".equals(szRcvTcCode)) {
            szRtnMsg = "TC Code Error (" + szRcvTcCode + ")";
            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
            
            throw new DAOException(szRtnMsg);
        }

        if (bDebugFlag) {
            szMsg = "[" + szOperationName + "] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        }

        try {
        	szMsg = "[" + szOperationName + "] 메소드 시작 - 파라미터 확인 >>>> " + msgRecord.toString();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	//=============================================================
        	// Log 테이블 등록
        	//=============================================================
        	//szMsg = "[2후판정정] 권하실적처리 수신";
        	//ydUtils.putLogMsg("A", "yd_monitorT", szMsg, "", "", "", "I", "A", "I", szRcvTcCode, SZ_SESSION_NAME, szMethodName);

        	//-------------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------------
	        intRtnVal 		= this.paramCheckY7(msgRecord, getCrnschRecord);

	        szCrnSchId 		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_CRN_SCH_ID"	);
	        szYdEqpId 		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_EQP_ID"		);
	        szYdSchCd 		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_SCH_CD"		);
	        szYdDnWrLoc		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC"		);
	        szYdDnWrLayer	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER"	);

	        //szYdEqpId
	        szMsg = "수신 설비ID: " + szYdEqpId;
	        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        
	        // L2에서 전송하는 STL_NO1~15 , YD_DN_WR_LOC1 ~ 15 는 횡행작업일때만 존재함 --> 무조건 존재하는것으로 변경됨, 15건으로 증가
	        for(int ii=0; ii<15; ii++) {
		        arrStlNo[ii]			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "STL_NO" + (ii+1));			// 재료번호1~15			CHAR	11
		        if (!"".equals(arrStlNo[ii])) {
			        arrYdDnWrLoc[ii]	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC" + (ii+1)	);	// 야드권하실적위치1~15	CHAR	8
			        arrYdDnWrLayer[ii]	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER" + (ii+1)	);	// 야드권하실적단1~15		CHAR	3

			        // 권하위치 오류 체크
			        if ("".equals(arrYdDnWrLoc[ii]) || arrYdDnWrLoc[ii].length() != 8) {
			            szRtnMsg = "권하위치 입력 오류 .. " + arrYdDnWrLoc[ii];
			            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				        
				        throw new DAOException(szRtnMsg);
			        }
			        // 적치단 오류 체크
			        if ("".equals(arrYdDnWrLayer[ii]) || arrYdDnWrLayer[ii].length() != 3 || Integer.parseInt(arrYdDnWrLayer[ii]) < 1) {
			            szRtnMsg = "적치단 입력 오류 .. " + arrYdDnWrLayer[ii];
			            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				        
				        throw new DAOException(szRtnMsg);
			        }

			        arrYdDnWrBedNo[ii]	= ydUtils.substr(arrYdDnWrLoc[ii], 6, 2);
			        iMtlCnt ++;
		        }
	        }

            szMsg    = "[" + szOperationName + "] 권하실적처리 대상 매수 >>>> " + Integer.toString(iMtlCnt);
	        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        // SORT - 적치단 ASC , 적치베드 ASC
	        for(int ii=0; ii<iMtlCnt; ii++) {
		        for(int jj=(ii+1); jj<iMtlCnt; jj++) {
		        	if ("".equals(arrStlNo[jj])) {
		        		continue;
		        	}
		        	if (Integer.parseInt(arrYdDnWrLayer[ii])  > Integer.parseInt(arrYdDnWrLayer[jj]) ||
		        	   (Integer.parseInt(arrYdDnWrLayer[ii]) == Integer.parseInt(arrYdDnWrLayer[jj]) &&
		        		Integer.parseInt(arrYdDnWrBedNo[ii])  > Integer.parseInt(arrYdDnWrBedNo[jj]))) {

		        		szTemp 				= arrStlNo[ii];
		    		    arrStlNo[ii]		= arrStlNo[jj];
		    		    arrStlNo[jj]		= szTemp;

		        		szTemp 				= arrYdDnWrLoc[ii];
				        arrYdDnWrLoc[ii]	= arrYdDnWrLoc[jj];
				        arrYdDnWrLoc[jj]	= szTemp;

				        szTemp				= arrYdDnWrLayer[ii];
				        arrYdDnWrLayer[ii]	= arrYdDnWrLayer[jj];
				        arrYdDnWrLayer[jj]	= szTemp;

				        szTemp				= arrYdDnWrBedNo[ii];
				        arrYdDnWrBedNo[ii]	= arrYdDnWrBedNo[jj];
				        arrYdDnWrBedNo[jj]	= szTemp;
		        	}
		        }
	        }

	        // DEBUG .. SORT결과 출력
	        for(int ii=0; ii<iMtlCnt; ii++) {
	            szMsg    = "[" + szOperationName + "] SORT 결과 출력 " + (ii+1) + " >>>> 재료번호 :: " + arrStlNo[ii] + ", 권하위치 :: " + arrYdDnWrLoc[ii] + "-" + arrYdDnWrLayer[ii];
		        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        }

	        szModifier = ydDaoUtils.paraRecModifier(msgRecord);

	        if ("".equals(szCrnSchId)) {
	            szRtnMsg = "크레인스케줄ID가 없습니다.";
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                throw new DAOException(szRtnMsg);
	        }

	        //파라미터 레코드 편집
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID", 	szCrnSchId		);
	        setRecord.setField("YD_SCH_CD", 		szYdSchCd		);
	        setRecord.setField("YD_DN_WR_LOC", 		szYdDnWrLoc		);
	        setRecord.setField("YD_DN_WR_LAYER", 	szYdDnWrLayer	);
	        setRecord.setField("MODIFIER", 			szModifier		);

	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "] 업데이트 - 권하실적위치[" + szYdDnWrLoc + "]와 권하실적단[" + szYdDnWrLayer + "]을 업데이트 시작";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intRtnVal = ydCrnschDao.updCrnDnWr(setRecord);

			szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "] 업데이트 - 권하실적위치[" + szYdDnWrLoc + "]와 권하실적단[" + szYdDnWrLayer + "]을 업데이트 완료";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            //-------------------------------------------------------------------------------------------------------------------
	        //	크레인스케줄 작업재료 조회
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "]작업재료 조회 시작";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        intRtnVal = this.getYdCrnSchY7(setRecord, getRecSet);
	        if (intRtnVal < 0) {
	            szRtnMsg = "크레인스케줄 작업재료 조회 오류 .. " + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
		        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		        
		        throw new DAOException(szRtnMsg);
			}

	        //레코드셋의 사이즈값으로 ErrorCheck
	        if (getRecSet.size() == 0) {
	            szRtnMsg = "권하실적 크레인작업재료  조회 오류 .. " + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	            
	            throw new DAOException(szRtnMsg);
	        }

	        // 조업 L3전송용 재료번호 편집
	        getRecSet.first();
	        for(int ii=0; ii<getRecSet.size(); ii++) {
	        	getRecSet.absolute(ii+1);
		        getRecord = getRecSet.getRecord();
		        if (ii > 0) {
		        	szArrStlNo = szArrStlNo + ";";
		        }
	        	szArrStlNo = szArrStlNo + ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
	        }

	        getRecSet.first();
	        getRecord = getRecSet.getRecord();

	        szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "]작업재료 조회 완료 - 대상재 건수 : " + Integer.toString(getRecSet.size());
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            szYdUpWrLoc 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC"		);
	        szYdWbookId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID"		);		// 작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szStlNo			= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"				);
	        szYdUpWrkActGp 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WRK_ACT_GP"	);		// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)

	        //-------------------------------------------------------------------------------------------------------------------
	        //	작업진행상태 확인 후 상태가 맞지 않으면 롤백 처리
            //-------------------------------------------------------------------------------------------------------------------
        	//작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
	        if (!"2".equals(ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT")) &&
	        	!"3".equals(ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT"))) {

	            szRtnMsg = "작업진행상태[" + ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT") + "]가 권상완료 아님!.";
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	            
	            throw new DAOException(szRtnMsg);
	        }

	        //-------------------------------------------------------------------------------------------------------------------
	        // 실제적치단의 조회
	        //-------------------------------------------------------------------------------------------------------------------
        	if (!"".equals(arrYdDnWrLoc[0])) {
        		szYdDnWrLoc = arrYdDnWrLoc[0];
        		for(int ii=0; ii<15; ii++) {
        			if (!"".equals(arrYdDnWrLoc[ii]) && "01".equals(ydUtils.substr(arrYdDnWrLoc[ii], 6, 2))) {
                		szYdDnWrLoc = arrYdDnWrLoc[ii];
        			}
        		}
        	}
	        szYdStkColGp   = szYdDnWrLoc.substring(0, 6);
	        szYdStkBedNo   = szYdDnWrLoc.substring(6, 8);

	        if (!"BS".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) && !"CN".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

		        szRealTopLyr = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdStkBedNo, szStlNo, szYdUpWrkActGp, "");

		        if ("000".equals(szRealTopLyr)) {
		            szRtnMsg = "권하실적 최상단 검색시 오류발생 :: " + szRealTopLyr;
		            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
		            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		            
		            throw new DAOException(szRtnMsg);
				}

		        // 권하실적 처리시 '01'베드 부터 처리 할수 있도록 보완
		        szCallMsg = JPlateYdCommonUtils.checkDownLoc(szYdStkColGp, szYdStkBedNo, szRealTopLyr);
		        if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szCallMsg)) {
		        	szRtnMsg = szCallMsg;
		            szMsg    = "[" + szOperationName + "] 권하실적  TO위치 체크시 오류 발생 :: " + szRtnMsg;
		            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		            
		        	throw new DAOException(szRtnMsg);
		        }
	        }

	        //-------------------------------------------------------------------------------------------------------------------
	        // 권하지시위치와 권하실적위치가 다르면 권하지시위치의 맵정보를 Clear시킴
	        // 보수장일때도 CLEAR하도록 변경 --> 보수장 권하시 새로운 저장위치 검색하도록 변경
	        // 가스장도 추가
	        //-------------------------------------------------------------------------------------------------------------------
            String sDnWoLoc = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC") + "-" + szYdDnWrLayer;
            String sDnWrLoc = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC") + "-" + szRealTopLyr;

            szMsg  = "[" + szOperationName + "] 권하지시위치 :: " + sDnWoLoc + "  권하실적위치 :: " + sDnWrLoc;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        if ("BS".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "CN".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || !sDnWoLoc.equals(sDnWrLoc)) {

                szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 시작 .. " + "지시::" + sDnWoLoc + ", 실적::" + sDnWrLoc;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        	//크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
	        	if ("".equals(szYdCrnXaxis) && "".equals(szYdCrnYaxis) && "".equals(szYdCrnZaxis)) {
	        		szYdCrnXaxis = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC_XAXIS");
	        		szYdCrnYaxis = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC_YAXIS");
	        		szYdCrnZaxis = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC_ZAXIS");
	        	}

//---------------------------------------------------------------------------------------------
// 2024.12.17 Argument에 logId 추가
//	        	intRtnVal = this.clearYdStklyrY7(getRecSet, szModifier);	// 권하 지시위치 Clear
	        	intRtnVal = this.clearYdStklyrY7(getRecSet, szModifier, logId);	// 권하 지시위치 Clear
//---------------------------------------------------------------------------------------------
	        	if (intRtnVal < 0) {
		            szRtnMsg = "권하 지시위치 Clear시 오류 .. " + Integer.toString(intRtnVal);
		            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
		            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		            
		            throw new DAOException(szRtnMsg);
				}

    	        szMsg = "[" + szOperationName + "]권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 완료";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        }

	        //-------------------------------------------------------------------------------------------------------------------
	        //  권하실적LYR와 실제적치단의 최상위 위치가 다르면 권하실적LYR를 변경하고 적치단 정보를 수정한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        if (!szYdDnWrLayer.equals(szRealTopLyr)) {
	        	szMsg = "[" + szOperationName + "] 실적적치단[" + szYdDnWrLayer + "]과 실제야드적치단[" + szRealTopLyr + "]이 상이하여 실적적치단 변경 시작";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	            szYdDnWrLayer = szRealTopLyr;
    	    }

	        //-------------------------------------------------------------------------------------------------------------------
	        //	크레인작업재료의 적치단을 적치중으로 변경
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경 시작";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//---------------------------------------------------------------------------------------------
// 2024.12.17 Argument에 logId 추가
//	        szRtnMsg = this.regYdStklyrY7(getRecSet, szModifier, arrStlNo, arrYdDnWrLoc, szYdUpWrkActGp, szRealTopLyr);
	        szRtnMsg = this.regYdStklyrY7(getRecSet, szModifier, arrStlNo, arrYdDnWrLoc, szYdUpWrkActGp, szRealTopLyr, logId);
//---------------------------------------------------------------------------------------------
	        if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
		        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경 완료";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        } else {
		        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경 오류 발생 >>>> " + szRtnMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	            
	            throw new DAOException(szRtnMsg);
	        }

            //-------------------------------------------------------------------------------------------------------------------
	        // 보수장일때 적치위치를 다시 조회 (보수장은 권하위치 틀림)
            //-------------------------------------------------------------------------------------------------------------------
	        if ("BS".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "CN".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || !sDnWoLoc.equals(sDnWrLoc)) {

	        	rsResult   = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
		        setRecord  = JDTORecordFactory.getInstance().create();
		        setRecord.setField("STL_NO",	szStlNo);

		        intRtnVal  = ydStockDao.getYdStockWithLoc(setRecord, rsResult);
		        if (intRtnVal > 0) {
		        	rsResult.first();
		        	recOutTemp = JDTORecordFactory.getInstance().create();
		        	recOutTemp.setRecord(rsResult.getRecord());

			        szYdDnWrLoc   = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO");
			        szYdDnWrLayer = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO");
		        }
	        }

            //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 시작";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      	szCrnSchId												);
	        setRecord.setField("YD_DN_WR_LOC",       	szYdDnWrLoc												);
	        setRecord.setField("YD_DN_WR_LAYER",     	szYdDnWrLayer											);
	        setRecord.setField("YD_DN_WRK_ACT_GP",   	getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP")		);
	        setRecord.setField("YD_DN_WR_XAXIS",     	getCrnschRecord.getFieldString("YD_CRN_XAXIS")			);
	        setRecord.setField("YD_DN_WR_YAXIS",     	getCrnschRecord.getFieldString("YD_CRN_YAXIS")			);
	        setRecord.setField("YD_DN_WR_ZAXIS",     	getCrnschRecord.getFieldString("YD_CRN_ZAXIS")			);
	        setRecord.setField("YD_WRK_PROG_STAT",   	getCrnschRecord.getFieldString("YD_WRK_PROG_STAT")		);
	        setRecord.setField("YD_DN_CMPL_DT",      	JPlateYdUtils.getCurDate("yyyyMMddHHmmss")				);
	        setRecord.setField("YD_GP",   				getCrnschRecord.getFieldString("YD_GP")					);
	        setRecord.setField("MODIFIER", 				szModifier												);

	        //--------------------------------------------------------------------------------------------------
	        //	크레인스케줄 등록 시 야드작업계상일자에 등록되는 값을 권하완료시점에 다시 수정, 권하완료 시의 값이 사용됨
	        //	TO_CHAR(SYSDATE - (7 / 24), 'YYYYMMDD')
	        //--------------------------------------------------------------------------------------------------
	        String szYdWrkHdsDd = JPlateYdUtils.getDefaultHdsDate();
	        setRecord.setField("YD_WRK_HDS_DD",  		szYdWrkHdsDd											);

	        szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "]의 야드작업계상일자[" + szYdWrkHdsDd + "]";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        intRtnVal = ydCrnschDao.updCrnDnWr(setRecord);

	        szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 완료";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            szMsg = "[" + szOperationName + "] 권하실적위치의 설비구분 : " + ydUtils.substr(szYdDnWrLoc, 2, 2);
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            szMsg = "[" + szOperationName + "] 권하실적 스케줄코드 : " + szYdSchCd;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 대차일때 대차 스케줄 이송재료 등록 후 대차스케줄 호출
	        //-------------------------------------------------------------------------------------------------------------------
	        if ("TC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) && "TC".equals(ydUtils.substr(szYdSchCd, 2, 2))) {

	            szMsg    = "[" + szOperationName + "] 권하실적위치가 대차";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        	//-------------------------------------------------------------------------------------------------------------------
	            //	권하실적위치로 대차설비ID 조합 생성
	            //-------------------------------------------------------------------------------------------------------------------
	        	szTcarEqpId = szYdDnWrLoc.substring(0,1) + "XTC" + szYdDnWrLoc.substring(4,6);

	            //-----------------------------------------------------
                //	대차작업지정기준을 BRE Rule에서 조회
                //-----------------------------------------------------
                String[] szTCarRule	= JPlateYdCommonUtils.getTCarWrkStdRule(szTcarEqpId);
                //-----------------------------------------------------
                recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_WBOOK_ID", 			szYdWbookId);	//작업예약ID
	        	recInPara.setField("YD_WRK_PLAN_TCAR",		szTcarEqpId);	//작업계획대차

                if ("Y".equals(szTCarRule[0])) {

                	if ("D".equals(szTCarRule[1])) {	//대차 직상차
                		//기준상차동과 실제작업한 상차동이 같을경우
                		if (szTCarRule[2].equals(szYdDnWrLoc.substring(1, 2))) {
                			//-----------------------------------------------------
            	        	//	대차작업지정기준이 직상차인 경우에는 하차동을 목표동으로 재설정
            	        	//-----------------------------------------------------
            	        	recInPara.setField("YD_AIM_BAY_GP",		szTCarRule[3]);		//목표동
            	        }
	                }
		        }
            	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recInPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
                recInPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
              			
                intRtnVal = ydWrkbookDao.updYdWrkbook(recInPara);
	        	if (intRtnVal <= 0) {
		            szRtnMsg = "대차 작업예약 수정시 오류 :: " + Integer.toString(intRtnVal);
		            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	                throw new DAOException(szRtnMsg);
	    		}
	        	szMsg = "[" + szOperationName + "] [대차 직상차]상차작업예약ID[" + szYdWbookId + "]에 대차설비ID[" + szTcarEqpId + "]  목표동[" + szTCarRule[3] + "] 업데이트 완료";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        	//-------------------------------------------------------------------------------------------------------------------
	        	//	권하실적위치로 만들어진 대차설비ID를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 시작
	        	//-------------------------------------------------------------------------------------------------------------------
	        	szMsg = "[" + szOperationName + "] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 시작";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_EQP_ID", szTcarEqpId);

	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
	        	intRtnVal = ydTcarSchDao.getByYdEqpId(recInPara, rsResult);
	        	if (intRtnVal <= 0) {
		        //  szRtnMsg = "대차 상차시 권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 대차스케줄 조회 시 오류발생 - 반환값 : " + Integer.toString(intRtnVal);
			        szRtnMsg = "대차 상차시 설비ID[" + szTcarEqpId + "]로 스케줄 조회 오류발생 - 반환값 : " + Integer.toString(intRtnVal);
		            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	                throw new DAOException(szRtnMsg);
	        	}

	        	rsResult.absolute(1);
	        	recOutTemp = JDTORecordFactory.getInstance().create();
	        	recOutTemp.setRecord(rsResult.getRecord());

	        	szMsg = "[" + szOperationName + "] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 완료 - 대상재건수 : " + Integer.toString(rsResult.size());
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                szYdTcarSchId = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");

                //-------------------------------------------------------------------------------------------------------------------
	        	//	조회된 대차 스케줄에 상차작업예약id를 등록한다.
                //-------------------------------------------------------------------------------------------------------------------
                szMsg = "[" + szOperationName + "] [대차 상차]대차스케줄[" + szYdTcarSchId + "]에 상차작업예약ID[" + szYdWbookId + "]를 업데이트 시작";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_TCAR_SCH_ID", 		szYdTcarSchId					);
	        	recInPara.setField("YD_CARLD_WRK_BOOK_ID", 	szYdWbookId						);
	        	recInPara.setField("YD_EQP_WRK_STAT", 		"L"								);
	        	recInPara.setField("YD_CAR_PROG_STAT", 		"4"								);		// 상차개시
	        	recInPara.setField("YD_CARLD_STOP_LOC", 	szYdDnWrLoc.substring(0,6)		);
	        	recInPara.setField("MODIFIER", 				szModifier						);

	        	intRtnVal = ydTcarSchDao.updYdCarLdUdInfo(recInPara);
	        	if (intRtnVal <= 0) {
		            szRtnMsg = "대차 상차 스케줄[" + szYdTcarSchId + "] 작업예약[" + szYdWbookId + "] 등록 오류발생 :: " + Integer.toString(intRtnVal);
		            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	                throw new DAOException(szRtnMsg);
	        	}

	        	szMsg = "[" + szOperationName + "] [대차 상차]대차스케줄[" + szYdTcarSchId + "]에 상차작업예약ID[" + szYdWbookId + "]를 업데이트 완료 - 반환값 : " + Integer.toString(intRtnVal);
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        	//-------------------------------------------------------------------------------------------------------------------
    		    //	권하실적위치가 대차이므로 대차이송재료 등록
    	        //-------------------------------------------------------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 권하실적위치가 대차이므로 대차이송재료 등록 시작";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            	this.setYdTcarY7(getRecSet,szYdTcarSchId);

            	//-------------------------------------------------------------------------------------------------------------------
    		    //	권하실적위치가 대차이므로 대차스케줄 호출
    	        //-------------------------------------------------------------------------------------------------------------------
            	szMsg = "[" + szOperationName + "] 권하실적위치가 대차이므로 대차스케줄 호출 시작 - 대차설비ID[" + szTcarEqpId + "]";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                recSendMsg = JDTORecordFactory.getInstance().create();
            	recSendMsg.setField("MSG_ID", 			"YDYDJ"			);		// YDYDJ520
            	recSendMsg.setField("YD_LD_UD_GP", 		"L"				);
            	recSendMsg.setField("YD_WBOOK_ID", 		szYdWbookId		);
            	recSendMsg.setField("YD_EQP_ID", 		szTcarEqpId		);
            	recSendMsg.setField("MODIFIER", 		szModifier		);
            	recSendMsg.setField("YD_CRN_SCH_ID", 	szCrnSchId		);		// 크레인작업지시ID 추가

    			// 권하처리 요청
    			szCallMsg = (String)ydEjbCon.trx("JPlateYdTcarSchSeEJB", "procY7TcarSch", recSendMsg);

    			szMsg = "[" + szOperationName + "] 권하실적위치가 대차이므로 대차스케줄 호출 완료 >>>> " + szCallMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            }

            if ("".equals(ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID"))) {
	            szRtnMsg = "작업예약 ID가 없습니다.";
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                
                throw new DAOException(szRtnMsg);
	        }

            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업 및 재료, 작업예약 및 재료정보를 Clear
	        //-------------------------------------------------------------------------------------------------------------------
            szMsg    = "[" + szOperationName + "] 크레인 작업재료 삭제처리 ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        //크레인 작업재료 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID")	);
	        setRecord.setField("DEL_YN",             "Y"												);
	        setRecord.setField("MODIFIER",           szModifier											);
	        
	        intRtnVal = ydCrnWrkMtlDao.delYdCrnWrkMtl(setRecord);
	        if (intRtnVal <= 0) {
	            szRtnMsg = "크레인 작업재료 삭제중 오류 발생!! Code : " + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                
                throw new DAOException(szRtnMsg);
	        }

            szMsg    = "[" + szOperationName + "] 크레인스케줄처리 ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        //크레인스케줄 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID")	);
	        setRecord.setField("DEL_YN",             "Y"												);
	        setRecord.setField("YD_DN_CMPL_DT",      JPlateYdUtils.getCurDate("yyyyMMddHHmmss")			); //권하완료일시
	        setRecord.setField("MODIFIER",           szModifier											);
	        
	        intRtnVal = ydCrnschDao.delYdCrnSch(setRecord);
	        if (intRtnVal < 0) {
	            szRtnMsg = "크레인스케줄 삭제시 오류 발생!!!, ErrorCode:" + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                
                throw new DAOException(szRtnMsg);
	        }

            szMsg    = "[" + szOperationName + "] 작업예약완료 CHECK ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        getRecord = JDTORecordFactory.getInstance().create();
	        getRecord.setField("YD_WBOOK_ID", szYdWbookId);

			intRtnVal = this.getYdCrnSchByWrkIdY7(getRecord, getRecSet);
			if (intRtnVal < 0) {
	            szRtnMsg = "작업예약완료 CHECK 에러발생!!!, ErrorCode:" + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                
                throw new DAOException(szRtnMsg);
	        }

	        // 작업예약에 존재하는 크레인스케쥴 정보가 없을경우에 작업예약정보를 삭제한다.
	        if (getRecSet.size() == 0) {

	            szMsg    = "[" + szOperationName + "] 작업예약정보를 삭제 ---- START";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId	);
	            bookrecord.setField("DEL_YN",             "Y"			);
	            bookrecord.setField("YD_SCH_PROG_STAT",   "E"			);
	            bookrecord.setField("MODIFIER",           szModifier	);

                intRtnVal = ydWrkbookDao.delYdWrkbook(bookrecord);
	        	if (intRtnVal <= 0) {
		            szRtnMsg = "작업예약정보를 삭제시 오류 발생 !!!, ErrorCode:" + Integer.toString(intRtnVal);
		            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	                
	                throw new DAOException(szRtnMsg);
	    		}

		        // 작업예약재료조회
	            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	            intRtnVal = ydWrkbookMtlDao.getYdWrkbookMtlId(bookrecord, getRecSet);

	            // 조회한 작업예약재료1매씩 저장품 업데이트
				for(int ii=1; ii<=getRecSet.size(); ii++) {
					getRecSet.absolute(ii);
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setRecord(getRecSet.getRecord());
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", 		ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO")		);
					recInTemp.setField("MODIFIER",    	szModifier											);
					recInTemp.setField("YD_SCH_CD", 	""													);
					recInTemp.setField("YD_WBOOK_ID", 	""													);
					recInTemp.setField("YD_STK_COL_GP", szYdDnWrLoc.substring(0,6)							);
					recInTemp.setField("YD_STK_BED_NO", szYdDnWrLoc.substring(6,8)							);
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYdDnWrLayer, ii-1)		);
					
					intRtnVal = ydStockDao.updYdStkColInfo(recInTemp);
				}

				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("STL_NO",      ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO")		);
				recInTemp.setField("DEL_YN",      "Y"													);
				recInTemp.setField("MODIFIER",    szModifier											);
				recInTemp.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID")	);
				
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookMtlDelYn(recInTemp);

				szNextSchCallYn = "Y";	// 다음 스케줄 호출 Flag Set

				szMsg    = "[" + szOperationName + "] 작업 예약 처리 완료";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        } else {
	        	szMsg    = "[" + szOperationName + "] 작업 예약 진행 중";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        }

			//------------------------------------------------------------------
			// FROM 위치가 대차일 경우 YD_WRK_PLAN_TCAR(야드작업계획대차) 항목  CLEAR 처리
			//------------------------------------------------------------------
			if ("TC".equals(ydUtils.substr(szYdUpWrLoc, 2, 2)) && !"TC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {
				for(int ii=0; ii<arrStlNo.length; ii++) {
					if (!"".equals(arrStlNo[ii])) {
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("STL_NO",         	arrStlNo[ii]	);
						recInTemp.setField("YD_WRK_PLAN_TCAR", 	""				);
						recInTemp.setField("REGISTER",       	szModifier		);
						recInTemp.setField("MODIFIER",       	szModifier		);

			    		intRtnVal = ydStockDao.updYdWrkPlanTcar(recInTemp);
			    		if (intRtnVal < 1) {
							szMsg = "야드작업계획대차 항목 CLEAR 실패 >>>> " + Integer.toString(intRtnVal);
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    		}
					}
				}
			}

	        //-------------------------------------------------------------------------------------------------------------------
		    //	인터페스 송신처리
	        //  각각의 I/F 송신조건은 다시 체크 요망.
	        //-------------------------------------------------------------------------------------------------------------------
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 후판조업 저장위치변경정보 전송  - YDPPJ011
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            szMsg    = "[" + szOperationName + "] 후판조업 저장위치변경정보 전송 ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("MSG_ID", 			"YDPPJ011"							);
	        recInTemp.setField("YD_STK_COL_FR", 	ydUtils.substr(szYdUpWrLoc,0,6)		);		// From적치열
	        recInTemp.setField("YD_STK_BED_FR", 	ydUtils.substr(szYdUpWrLoc,6,2)		);		// From적치BED
	        recInTemp.setField("YD_STK_COL_TO", 	ydUtils.substr(szYdDnWrLoc,0,6)		);		// TO적치열
	        recInTemp.setField("YD_STK_BED_TO", 	ydUtils.substr(szYdDnWrLoc,6,2)		);		// TO적치BED
	        recInTemp.setField("YD_EQP_WRK_SH", 	""									);		// 야드설비작업매수
	        recInTemp.setField("ARR_STL_NO", 		szArrStlNo							);
	        
	        recInTemp.setField("BOOK_OUT_CRN", 		szYdEqpId							); 		// 아직 테스트전. 우선 막음
	        //REQ202306466285 대상 크레인 추가 강길모책임 요청사항. 조업 변경이력 전송시 크레인 추가
	        
//	        szSendMsg = ydDelegate.sendMsg(recInTemp);

			szMsg = "[" + szOperationName + "] szYdEqpId 확인>>>>" + szYdEqpId;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);	        
	        
	        szSendMsg = JPlateYdCommonUtils.sendL3YDPPJ011(recInTemp);

			szMsg = "[" + szOperationName + "] 후판조업 저장위치변경정보 전송 완료>>>>" + szSendMsg;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			2후판정정야드L2 크레인작업실적응답 전송  - YDY7L005
	         * 업무기준 Desc : 크레인 권하실적처리 성공 후 크레인작업실적응답 전송
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            szMsg    = "[" + szOperationName + "] 크레인작업실적응답 전송 ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("MSG_ID", 			"YDY7L005"									);
	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId									);		// 야드설비ID
	        recInTemp.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_CMPL			);		// 야드작업진행상태
	        recInTemp.setField("YD_SCH_CD", 		szYdSchCd									);		// 야드스케줄코드
	        recInTemp.setField("YD_CRN_SCH_ID", 	szCrnSchId									);		// 야드크레인스케줄ID
        	recInTemp.setField("YD_L2_WR_GP", 		JPlateYdConst.CRN_WRK_RE_DN_WR			);		// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        recInTemp.setField("YD_L3_HD_RS_CD", 	JPlateYdConst.CRN_WRK_RE_CD_NORMAL_HD		);		// 야드L3처리결과코드
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recInTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
	        recInTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
	        szSendMsg = ydDelegate.sendMsg(recInTemp);

			szMsg = "[" + szOperationName + "] 2후판정정야드L2 크레인작업실적응답[YDY7L005] 전송 완료>>>>" + szSendMsg;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 2후판정정야드L2 저장품제원 전송  - YDY7L002
	         * RT - BOOK-IN 권하 완료후
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			if ("RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

				this.procRtBookIn(msgRecord, arrStlNo);
			}

            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인 설비상태 권하완료(4) 셋팅.
	        //-------------------------------------------------------------------------------------------------------------------
            szMsg    = "[" + szOperationName + "] 설비상태 권하완료 변경 ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId	);
	        recInTemp.setField("YD_EQP_STAT", 		"4"			);
	        recInTemp.setField("MODIFIER", 			szModifier	);
	        
            intRtnVal = ydEqpDao.updYdEqpStat(recInTemp);

            if (intRtnVal <= 0) {
	            szRtnMsg = "설비상태 UPDATE 처리시 오류 발생 :: " + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			 	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			 	
			 	throw new DAOException(szRtnMsg);
	   		}

	        //설비id로 설비Table조회
	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYdEqpId);
	        rsResult  = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
	        intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult);

			szMsg = "[" + szOperationName + "] 이력테이블등록호출 ---- START";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// 이력테이블등록호출
	        JPlateYdCrnSchSeEJBBean crnSchSeEJBBean = new JPlateYdCrnSchSeEJBBean();

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",             ""					);
			recInTemp.setField("YD_WBOOK_ID",        szYdWbookId		);
			recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId			);
			recInTemp.setField("YD_CAR_SCH_ID",      ""					);		// szYdCarSchId
			recInTemp.setField("YD_TCAR_SCH_ID",     szYdTcarSchId		);
			recInTemp.setField("YD_WTCL_TNK_SCH_ID", ""					);
			recInTemp.setField("REGISTER",           szModifier			);
			recInTemp.setField("MODIFIER",           szModifier			);
			recInTemp.setField("DEL_YN",             "N"				);
			crnSchSeEJBBean.procWorkHistoryCreate(recInTemp);

			szMsg = "[" + szOperationName + "] 이력테이블등록호출 END";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord(0));

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",           	JPlateYdConst.JMS_TC_WRK_REQ								);		// YDYDJ755 :: 크레인 작업지시요구
			recInTemp.setField("YD_EQP_ID",        	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID")			);
			recInTemp.setField("YD_EQP_WRK_MODE",  	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE")	);
			recInTemp.setField("YD_WRK_PROG_STAT", 	"4"															);
			recInTemp.setField("YD_SCH_CD",        	szYdSchCd													);
			recInTemp.setField("YD_CRN_SCH_ID",    	""															);
			recInTemp.setField("YD_CRN_XAXIS",     	""															);
			recInTemp.setField("YD_CRN_YAXIS",     	""															);
			recInTemp.setField("RTN_CD",     		JPlateYdConst.RETN_CD_SUCCESS);

	        //-------------------------------------------------------------------------------------------------------------------
	        // 해당 스케줄코드로 다음 스케줄기동
	        //-------------------------------------------------------------------------------------------------------------------
			if ("Y".equals(szNextSchCallYn)) {
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_EQP_ID",     szYdEqpId		);		// 설비ID
				recInTemp.setField("YD_SCH_CD",     szYdSchCd		);		// 스케줄코드
				recInTemp.setField("REGISTER",      szModifier		);
				recInTemp.setField("MODIFIER",      szModifier		);
				recInTemp.setField("YD_UP_WR_LOC",	szYdUpWrLoc		);		// 권상위치
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recInTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
				recInTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				        	
				this.procNextYdCrnSch(recInTemp);
			}

        } catch(Exception e) {
        	/*
        	 * Exception 발생시에도 작업실적 응답은 송신.
        	 */
        	if ("".equals(szRtnMsg) || JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        		szRtnMsg = e.getMessage();
        	}
        	if (szRtnMsg != null && szRtnMsg.length() > 100) {
        		szRtnMsg = ydUtils.substr(szRtnMsg, 0, 100);
        	}
			szMsg = "[" + szOperationName + "] 권하실적처리시 Exception 발생 >>>>" + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("MSG_ID", 			"YDY7L005"								);
	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId								);		// 야드설비ID
	        recInTemp.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_CMPL		);		// 야드작업진행상태
	        recInTemp.setField("YD_SCH_CD", 		szYdSchCd								);		// 야드스케줄코드
	        recInTemp.setField("YD_CRN_SCH_ID", 	szCrnSchId								);		// 야드크레인스케줄ID
        	recInTemp.setField("YD_L2_WR_GP", 		JPlateYdConst.CRN_WRK_RE_DN_WR		);		// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        recInTemp.setField("YD_L3_HD_RS_CD", 	JPlateYdConst.CRN_WRK_RE_CD_ERROR		);		// 야드L3처리결과코드
	        recInTemp.setField("YD_L3_MSG",			szRtnMsg								);
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recInTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
	        recInTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
			String sendMsg = ydDelegate.sendMsg(recInTemp);

			szMsg = "[" + szOperationName + "] 2후판정정야드L2 크레인작업실적응답[YDY7L005] 전송 완료 >>>>" + sendMsg;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        //	m_ctx.setRollbackOnly();
        //	throw new DAOException(getClass().getName() + e.getMessage(), e);

        	return szRtnMsg;
        }

	    return JPlateYdConst.RETN_CD_SUCCESS;
    } // end of procY7CrnDnWr()

    /**
     * 오퍼레이션명 : 권하위치가 RT일경우 북인 실적 전송
     *
     * @return ● intRtnVal
     * @throws ● DAOException
     */
    public String procRtBookIn(JDTORecord msgRecord, String[] pArrStlNo) throws DAOException {

    	JPlateYdDelegate      	ydDelegate		= new JPlateYdDelegate();
    	JPlateYdStockDAO      	ydStockDao      = new JPlateYdStockDAO();
    	JPlateYdStkLyrDAO     	ydStkLyrDao     = new JPlateYdStkLyrDAO();
    	JPlateYdCrnWrkMtlDAO  	ydCrnWrkMtlDao	= new JPlateYdCrnWrkMtlDAO();

        int intRtnVal = 0;

        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord 		getRecord   = null;
        JDTORecord 		recPara		= null;
        JDTORecordSet 	rsResult    = null;

        String 	szMsg            	= "";
        String 	szRtnMsg            = "";
        String 	szSendMsg           = "";
        String 	szMethodName     	= "procRtBookIn";
        String 	szOperationName  	= "2후판정정 권하실적처리";

        String	szModifier			= "";
        String	szStlNo				= "";
        String	szCrnSchId			= "";
        String 	szYdStkColGp		= "";
        String 	szYdStkBedNo     	= "";
        String	szStlNoList			= "";

    	try {

			// ------------------------------------------------------------------------
			// 조업 L2 북인 실적 전송 .. 순서가 뒤바뀌는 경우가 존재하여 일괄 전송처리
			// ------------------------------------------------------------------------
    		if (pArrStlNo != null && pArrStlNo.length > 0) {
				szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 일괄전송 .. 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    		for (int ii=0; ii<pArrStlNo.length; ii++) {
	    			if (!"".equals(pArrStlNo[ii])) {
		    			if ("".equals(szStlNoList)) {
		    				szStlNoList = pArrStlNo[ii];
		    			} else {
		    				szStlNoList = szStlNoList + ";" + pArrStlNo[ii];
		    			}
	    			}
	    		}

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 				"YDS1L005");			// BOOK-IN 실적 전송
				recPara.setField("STL_NO",				"");					// 재료번호
				recPara.setField("STL_NO_LIST",			szStlNoList);			// 재료번호 LIST
				recPara.setField("OPERATION_TYPE",		"1");					// 1:Book In, 2:Book Out
				recPara.setField("YD_STK_COL_GP",		szYdStkColGp);			// TO위치
				recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);    		// 야드적치BED번호

				szSendMsg = ydDelegate.sendMsg(recPara);

				szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 완료>>>>"+szSendMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
    		}

	        szCrnSchId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	        szModifier	= ydDaoUtils.paraRecModifier(msgRecord);

	        rsResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
    		recPara  	= JDTORecordFactory.getInstance().create();
    		recPara.setField("YD_CRN_SCH_ID", 	szCrnSchId);

	        intRtnVal = ydCrnWrkMtlDao.getByYdCrnSchIdWithLoc(recPara, rsResult);

	        if (intRtnVal > 0) {

	        	rsResult.first();
				for(int ii=0; ii<rsResult.size(); ii++) {

			        rsResult.absolute(ii+1);
			        getRecord		= JDTORecordFactory.getInstance().create();
			        getRecord 		= rsResult.getRecord();

			        szStlNo			= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
			        szYdStkColGp	= ydDaoUtils.paraRecChkNull(getRecord, "YD_STK_COL_GP");
			        szYdStkBedNo	= ydDaoUtils.paraRecChkNull(getRecord, "YD_STK_BED_NO");

					// ------------------------------------------------------------------------
					// 1. 야드 L2 저장품재원 삭제전문 전송
					// ------------------------------------------------------------------------
					szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 START";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD", 			"YDY7L002");                            // TC-CODE
					recPara.setField("YD_GP", 				JPlateYdConst.YD_GP_F_PLATE_YARD);		// 야드구분
					recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);            				// 야드적치열구분
					recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);    						// 야드적치BED번호
					recPara.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
					recPara.setField("STL_NO", 				szStlNo);	        					// 재료번호
					recPara.setField("MSG_GP", 				"D");	        						// 전문구분
					szRtnMsg = ydDelegate.sendMsg(recPara);

					szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					// ------------------------------------------------------------------------
					// 2. 조업 L2 북인 실적 전송 .. 순서가 뒤바뀌는 경우가 존재하여 일괄 전송처리
					// ------------------------------------------------------------------------
					if (pArrStlNo != null && pArrStlNo.length > 0) {
						szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 일괄전송하여 .. SKIP";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					} else {
						szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 시작";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MSG_ID", 				"YDS1L005");						// BOOK-IN 실적 전송
						recPara.setField("STL_NO",				szStlNo);							// 재료번호
						recPara.setField("OPERATION_TYPE",		"1");								// 1:Book In, 2:Book Out
						recPara.setField("YD_STK_COL_GP",		szYdStkColGp);						// TO위치
						recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);    					// 야드적치BED번호

						szSendMsg = ydDelegate.sendMsg(recPara);

						szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 완료>>>>"+szSendMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}

					// ------------------------------------------------------------------------
					// 3. 재료정보 삭제처리
					// ------------------------------------------------------------------------
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO",				szStlNo);								// 재료번호
					recPara.setField("YD_GP",				JPlateYdConst.YD_GP_F_PLATE_YARD);
					recPara.setField("MODIFIER", 			szModifier);

					intRtnVal = ydStockDao.delYdStock(recPara);
					if (intRtnVal < 0) {
						szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					//	return szRtnMsg;
					}

					// ------------------------------------------------------------------------
					// 4. 저장위치 CLEAR
					// ------------------------------------------------------------------------
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", 				szStlNo);             	// 재료번호
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");             		// 야드적치단재료상태
					recPara.setField("YD_GP",				JPlateYdConst.YD_GP_F_PLATE_YARD);
					recPara.setField("MODIFIER", 			szModifier);

					intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
					if (intRtnVal < 0) {
						szRtnMsg = "저장위치 삭제 처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					//	return szRtnMsg;
					}
				}
	        }

        } catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			//throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
			szMsg    = "["+szOperationName+"] RT 북인 실적 전송시 .. Exception 발생" + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

        }//end of try~catch

        return JPlateYdConst.RETN_CD_SUCCESS;

    }//end of procRtBookIn()

    /**
     * 오퍼레이션명 : 권하완료후 다음 작업예약 존재시 스케줄 기동
     *
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● DAOException
     */
    public int procNextYdCrnSch(JDTORecord msgRecord) throws DAOException {

    	String	szRtnMsg			= "";
		String 	szMsg           	= "";
		String 	szMethodName    	= "procNextYdCrnSch";
		String 	szOperationName 	= "권하후 다음스케줄 기동";

		String	szYdEqpId			= "";		// 크레인설비ID
		String	szYdSchCd			= "";		// 크레인스케줄코드
		String	szYdWbookId			= "";		// 작업예약ID
		String	szRegister			= "";		// 등록자
		String	szModifier			= "";		// 수정자
		String	szYdUpWrLoc			= "";		// 권상위치

        int 	intRtnVal 			= 1;
        int		iCrnSchCnt			= 0;

        JDTORecordSet rsResult      = null;

    	JDTORecord recSchPara 		= null;
    	JDTORecord recPara 			= null;
    	JDTORecord recTemp 			= null;

    	JPlateYdWrkbookDAO 	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
    	JPlateYdCrnSchDAO	ydCrnSchDao		= new JPlateYdCrnSchDAO();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 로그 개선 
  		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "F");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

  		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
    	      						
    	try {

    		szYdEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"		);		// 크레인설비ID
    		szYdSchCd	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"		);		// 크레인스케줄코드
    		szRegister	= ydDaoUtils.paraRecChkNull(msgRecord, "REGISTER"		);		// 등록자
    		szModifier  = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"		);		// 수정자
    		szYdUpWrLoc	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC"	);		// 권상위치

    		for(int ii=0; ii< JPlateYdConst.MAX_CRN_SCH_CNT ; ii++) {

        		// 스케쥴 코드로 크레인작업지시를 조회하여 5건 이하시 다음 스케쥴 기동
        		rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        		recPara  = JDTORecordFactory.getInstance().create();
        		recPara.setField("YD_SCH_CD", 		szYdSchCd);

        		iCrnSchCnt = ydCrnSchDao.getByYdSchCd(recPara, rsResult);
    			if (iCrnSchCnt >= JPlateYdConst.MAX_CRN_SCH_CNT) {
        			szMsg = "[" + szOperationName + "] ----------- 다음 스케줄 기동 SKIP :: " + ii + " .. 스케쥴 건수 .. " + iCrnSchCnt;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        			
    				break;
    			}

    			szMsg = "[" + szOperationName + "] ----------- 다음 스케줄 기동 대상 조회 :: " + ii + "번째 .. 스케줄 :: " + szYdSchCd + ", 권상위치 :: " + szYdUpWrLoc;
    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				//---------------------------------------------
	    		// 다음 스케줄 존재여부 체크 - 스케쥴코드로 작업예약 조회
				//---------------------------------------------
        		rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        		recPara  = JDTORecordFactory.getInstance().create();
        		recPara.setField("YD_SCH_CD", 		szYdSchCd							);		// 스케줄코드
        		recPara.setField("YD_STK_COL_GP",	ydUtils.substr(szYdUpWrLoc,0,6)		);		// 권상위치

        		intRtnVal = ydWrkbookDao.getNextWrkBookIdBySchCd(recPara, rsResult);

				if (intRtnVal > 0) {

					rsResult.first();

	        		recTemp = JDTORecordFactory.getInstance().create();
					recTemp = rsResult.getRecord();
					szYdWbookId = ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID"	);
					szYdSchCd	= ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD"	);

					//-----------------------------------------
					// 다음 스케줄기동
					//-----------------------------------------
					recSchPara 	= JDTORecordFactory.getInstance().create();
					recSchPara.setField("MSG_ID", 		"YDYDJ"			);		// TC코드
					recSchPara.setField("YD_EQP_ID", 	szYdEqpId		);		// 크레인설비ID
					recSchPara.setField("YD_SCH_CD",	szYdSchCd		);		// 크레인스케줄코드
					recSchPara.setField("YD_WBOOK_ID",	szYdWbookId		);		// 작업예약ID
					recSchPara.setField("REGISTER", 	szRegister		);
					recSchPara.setField("MODIFIER", 	szModifier		);
					recSchPara.setField("CHK_FROM_LOC", "N"				);		// 권상위치에 작업예약 존재여부 체크 하지 안도록 SET

					szMsg    = "[" + szOperationName + "] ----------- 다음 스케줄기동 START :: " + szYdWbookId + "," + szYdSchCd;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.16 recSchPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
					recSchPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
					        	
			        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchSeEJB", this);
			        szRtnMsg = (String)ejbConn.trx("procCrnSchMain", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

					szMsg    = "[" + szOperationName + "] ----------- 다음 스케줄기동 END :: " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				} else {

					szMsg    = "[" + szOperationName + "] ----------- 다음 스케줄 기동 대상이 없습니다.";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					break;

				}
    		}	// end for

        } catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			//throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
        	szRtnMsg = "다음 스케줄기동  .. <br>" + szRtnMsg;
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        }//end of try~catch

        return intRtnVal;

    } // end of procNextYdCrnSch()

    /**
     * 오퍼레이션명 : 권상,권하 파라미터 체크
     *
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● DAOException
     */
    public int paramCheckY7(JDTORecord msgRecord, JDTORecord outRecord) throws DAOException {

    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
        int 	intRtnVal  = 1;

    	try {
            setRecord.setField("YD_CRN_SCH_ID"			, ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"));
			setRecord.setField("YD_SCH_CD"        		, ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"));
			setRecord.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord, "MSG_ID"));
			setRecord.setField("DATE"          			, ydDaoUtils.paraRecChkNull(msgRecord, "DATE"));
			setRecord.setField("TIME"          			, ydDaoUtils.paraRecChkNull(msgRecord, "TIME"));
			setRecord.setField("MSG_GP"          		, ydDaoUtils.paraRecChkNull(msgRecord, "MSG_GP"));
			setRecord.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));
			setRecord.setField("YD_EQP_WRK_MODE"        , ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_MODE"));
			setRecord.setField("YD_WRK_PROG_STAT"       , ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT"));
			setRecord.setField("YD_CRN_XAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS"));
			setRecord.setField("YD_CRN_YAXIS"         	, ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS"));
			setRecord.setField("YD_CRN_ZAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_ZAXIS"));
			setRecord.setField("YD_DN_WR_LOC"           , ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC"));
			setRecord.setField("YD_DN_WR_LAYER"         , ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LAYER"));
			setRecord.setField("YD_PILING_GP"         	, ydDaoUtils.paraRecChkNull(msgRecord, "YD_PILING_GP"));
			setRecord.setField("YD_EQP_WRK_SH"         	, ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_SH"));

			for(int ii=1; ii<=15; ii++) {
				setRecord.setField("STL_NO"+ii         	, ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"+ii));			// 재료번호		1 ~ 15
				setRecord.setField("YD_DN_WR_LOC"+ii    , ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC"+ii));		// 야드권하실적위치1 ~ 15
				setRecord.setField("YD_DN_WR_LAYER"+ii	, ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LAYER"+ii));	// 야드권하실적단	1 ~ 15
			}

			setRecord.setField("MODIFIER"         		, ydDaoUtils.paraRecModifier(msgRecord));						// 수정자

			//전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
	        if ("1".equals(setRecord.getFieldString("YD_EQP_WRK_MODE"))) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "A");
	        } else if ("9".equals(setRecord.getFieldString("YD_EQP_WRK_MODE"))) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "B");
	        } else if ("0".equals(setRecord.getFieldString("YD_EQP_WRK_MODE"))) {
	        	setRecord.setField("YD_DN_WRK_ACT_GP", "M");
	        }

	        outRecord.addRecord(setRecord);

        } catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
        }//end of try~catch

        return intRtnVal;

    }//end of paramCheckY7()

    /**
     * 오퍼레이션명 : 2후판정정 크레인스케줄 Select
     *
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int getYdCrnSchY7(JDTORecord msgRecord, JDTORecordSet outRecSet) throws JDTOException {

    	JPlateYdCrnSchDAO ydCrnschDao = new JPlateYdCrnSchDAO();

    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

    	int intRtnVal 			= 0;
    	String szOperationName  = "2후판정정 크레인스케줄 Select";
    	String szMethodName 	= "getYdCrnSchY7";
    	String szMsg        	= "";

        try{

	        intRtnVal = ydCrnschDao.getYdCrnWrkMtl(msgRecord, getRecSet);	// getYdCrnSch (intGp = 3)
	        if (intRtnVal == 0) {
                szMsg = "["+szOperationName+"] no data found!!!, ErrorCode:" + Integer.toString(intRtnVal);
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                return intRtnVal;
	        } else if (intRtnVal == -2) {
                szMsg = "["+szOperationName+"] parameter error!!!, ErrorCode:" + Integer.toString(intRtnVal);
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                return intRtnVal;
	        }
	        outRecSet.addAll(getRecSet);

        } catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
        }//end of try~catch

        return intRtnVal;

    }//end of getYdCrnSchY7()

    /**
     * 오퍼레이션명 : 2후판정정 크레인스케줄 Select [ByWrkId]
     *
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int getYdCrnSchByWrkIdY7(JDTORecord msgRecord, JDTORecordSet outRecSet) throws JDTOException {

    	JPlateYdCrnSchDAO ydCrnschDao = new JPlateYdCrnSchDAO();

    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

    	int intRtnVal 			= 0;
    	String szOperationName  = "2후판정정 크레인스케줄 Select";
    	String szMethodName 	= "getYdCrnSchByWrkIdY7";
    	String szMsg        	= "";

        try{

	        intRtnVal = ydCrnschDao.getByWrkId(msgRecord, getRecSet);	// getYdCrnSch (intGp = 28)
	        if (intRtnVal == 0) {
                szMsg = "["+szOperationName+"] no data found!!!, ErrorCode:" + Integer.toString(intRtnVal);
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                return intRtnVal;
	        } else if (intRtnVal == -2) {
                szMsg = "["+szOperationName+"] parameter error!!!, ErrorCode:" + Integer.toString(intRtnVal);
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                return intRtnVal;
	        }
	        outRecSet.addAll(getRecSet);

        } catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
        }//end of try~catch

        return intRtnVal;

    }//end of getYdCrnSchByWrkIdY7()

    /**
     * 오퍼레이션명 : 적치단 Clear - 권하지시위치와 권하실적 위치가 틀릴경우 호출
     *
     * @param  ● getRecSet
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int clearYdStklyrY7(JDTORecordSet getRecSet, String pMODIFIER) throws JDTOException {

    	JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();

    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 	= null;

    	String 	szMsg 			= "";
    	String 	szMethodName 	= "clearYdStklyrY7";
    	String 	szOperationName = "적치단 Clear";

    	int 	intRtnVal 		= 1;
		String 	szYdDnWoLoc   	= "";
        String 	szYdDnWoLayer 	= "";
        String 	szStlNo 		= "";
        String	szStkLyr		= "";

    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();

    		for(int ii=0; ii<rowsize; ii++) {

                //권하 지시위치 Clear
    			szYdDnWoLoc   	= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC");
                szYdDnWoLayer 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LAYER");
                szStlNo 		= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
                szStkLyr 		= ydDaoUtils.stringPlusInt(szYdDnWoLayer, ii);

                if ("".equals(szStlNo)) {
	                setRecord = JDTORecordFactory.getInstance().create();
	                setRecord.setField("YD_STK_COL_GP", 		szYdDnWoLoc.substring(0,6));
	                setRecord.setField("YD_STK_BED_NO", 		szYdDnWoLoc.substring(6,8));
	                setRecord.setField("YD_STK_LYR_NO",       	szStkLyr);
	                setRecord.setField("YD_STK_LYR_MTL_STAT",	"E");
	                setRecord.setField("STL_NO",              	"");
	                setRecord.setField("MODIFIER",            	pMODIFIER);

	                intRtnVal = ydStkLyrDao.updYdStklyrStat(setRecord);  	//적치단의 재료정보 Clear
                } else {
	                setRecord = JDTORecordFactory.getInstance().create();
	                setRecord.setField("YD_STK_COL_GP", 		szYdDnWoLoc.substring(0,6));
	                setRecord.setField("YD_STK_BED_NO", 		szYdDnWoLoc.substring(6,8));
	                setRecord.setField("STL_NO",              	szStlNo);
	                setRecord.setField("MODIFIER",            	pMODIFIER);
	                setRecord.setField("YD_GP",					JPlateYdConst.YD_GP_F_PLATE_YARD);

	                intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(setRecord);  //적치단의 재료정보 Clear
                }

                if (intRtnVal <= 0) {
    				szMsg = "["+szOperationName+"] 저장위치 적치단 CLEAR시 오류 발생 .. " + Integer.toString(intRtnVal);
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                }

                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for


		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
	    }//end of try~catch

		return  intRtnVal;
    }//end of clearYdStklyrY7()

    /**
     * 오퍼레이션명 : 2후판정정 적치단 Update
     *
     * @param  ● msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int updYdStklyrY7(JDTORecord msgRecord) throws JDTOException {

    	JPlateYdStkLyrDAO ydStklyrDao = new JPlateYdStkLyrDAO();

    	int intRtnVal 			= 0;

    	String szMsg 			= "";
    	String szMethodName 	= "updYdStklyrY7";
    	String szOperationName 	= "2후판정정 적치단 Update";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
    							
        try{

	    	intRtnVal = ydStklyrDao.updYdStklyrStat(msgRecord);  // 적치단의 재료정보 Update
			if (intRtnVal <= 0) {
    			if (intRtnVal == 0) {
    				szMsg = "[" + szOperationName + "] data not found";
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING, logId);
    				
    				szMsg = "적치단이 존재하지 않습니다.";
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    				
    				intRtnVal = 1;
    			} else if (intRtnVal == -1) {
    				szMsg = "duplicate data,";
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    			} else if (intRtnVal == -2) {
    				szMsg = "[" + szOperationName + "] parameter error";
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    			} else if (intRtnVal == -3) {
    				szMsg = "[" + szOperationName + "] execution failed";
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    			}
    		}
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
	    }//end of try~catch

		return intRtnVal;

    } //end of updYdStklyrY7

    /**
     * 오퍼레이션명 : 적치단 등록
     *
     * @param  ● getRecSet, sRealLyrNo
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public String regYdStklyrY7(JDTORecordSet pRecSet, String pMODIFIER, String[] pStlNo, String[] pYdDnWrLoc, String pYdUpWrkActGp, String pTopLyrNo)throws JDTOException {


    	JPlateYdCrnWrkMtlDAO 	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();

    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 	= null;
    	JDTORecord crnRecord 	= null;

    	JDTORecordSet rsResult  = null;
    	JDTORecord recPara 		= null;
    	JDTORecord recTemp 		= null;

    	String 	szRtnMsg		= null;
    	String 	szMsg 			= "";
        String 	szMethodName	= "regYdStklyrY7";
        String 	szOperationName	= "적치단 등록";

        int 	intRtnVal 		= 0;

        String 	szYdEqpId 		= "";
        String 	szYdWbookId		= "";
        String 	szYdCrnSchId	= "";
		String	szYdUpWrLoc	   	= "";
		String	szYdUpWrLayer  	= "";
        String 	szYdDnWrLoc		= "";
        String 	szYdDnWrLayer	= "";
        String 	szStlNo			= "";
        String	szMODIFIER		= pMODIFIER;
		String	szYdStkColGp	= "";
		String	szYdStkBedNo	= "";
		String	szTopLyrNo		= "000";

        JPlateYdStkLyrDAO ydStkLyrDao = new JPlateYdStkLyrDAO();

    	try {
			szMsg = "["+szOperationName+"] ============================= 권하실적 적치단 등록 .. START :: ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szTopLyrNo = pTopLyrNo;
			if (szTopLyrNo == null || "".equals(szTopLyrNo)) {
				szTopLyrNo = "000";
			}

    		int rowsize = pRecSet.size();

    		boolean isLast = false;

        	for(int ii=0; ii<rowsize; ii++) {

        		pRecSet.absolute(ii+1);
        		getRecord = JDTORecordFactory.getInstance().create();
        		getRecord.setRecord(pRecSet.getRecord());

        		szYdEqpId 		= ydDaoUtils.paraRecChkNull(getRecord, "YD_EQP_ID");
        		szYdWbookId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID");
    	        szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_CRN_SCH_ID");

        		//권상 실적위치
        		szYdUpWrLoc	   	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
        		szYdUpWrLayer  	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LAYER");

                szMsg = "["+szOperationName+"] 권상실적 위치 :: " + szYdUpWrLoc + ", 단::" + szYdUpWrLayer;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        		//권하 실적위치 등록
//        		szYdDnWrLoc	   	= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC");
//        		szStlNo	 		= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");

        		szYdDnWrLoc	   	= pYdDnWrLoc[ii];
        		szStlNo	 		= pStlNo[ii];

/*
        		// 권하실적위치로 최상단 조회
        		// L2에서 전송하는 STL_NO1~15 , YD_DN_WR_LOC1 ~ 15 는 횡행작업일때만 존재함 ----> (무조건 존재 , 15건 으로 변경)
        		if (pYdDnWrLoc != null && pStlNo != null) {
	        		for(int kk=0; kk<pYdDnWrLoc.length; kk++) {
	        			if (pStlNo[kk] != null && pStlNo[kk].equals(szStlNo)) {
	                		szYdDnWrLoc	= pYdDnWrLoc[kk];
	        				break;
	        			}
	        		}
        		}
*/

    	        szYdStkColGp   = ydUtils.substr(szYdDnWrLoc, 0, 6);
    	        szYdStkBedNo   = ydUtils.substr(szYdDnWrLoc, 6, 2);

    	        if ("BS".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyBsLoc(szYdStkColGp, szYdStkBedNo, szStlNo);
        	        szYdDnWrLayer = "001";
                    szMsg = "["+szOperationName+"] 보수장일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    	        } else if ("CN".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyCncLoc(szYdStkColGp, szYdStkBedNo, szStlNo);
        	        szYdDnWrLayer = "001";
                    szMsg = "["+szOperationName+"] 가스장일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    	        } else {

	    	        // 저장위치의 최상단 정보 조회
   	        		szYdDnWrLayer = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdStkBedNo, szStlNo, pYdUpWrkActGp, szTopLyrNo);

	                szMsg = "["+szOperationName+"] 권하실적 위치 :: " + szYdDnWrLoc + ", 단::" + szYdDnWrLayer + ", 재료번호::" + szStlNo;
	    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    			if ("".equals(szYdDnWrLayer) || "000".equals(szYdDnWrLayer)) {
	    				szRtnMsg = "저장위치의 최상단 조회 오류 .. " + szYdStkColGp + szYdStkBedNo;
	    				return szRtnMsg;
	    			}
    	        }

        		//크레인에 UPDATE (크레인 적치상태 Clear)
    			crnRecord = JDTORecordFactory.getInstance().create();
    			crnRecord.setField("YD_STK_COL_GP",       	szYdEqpId);
    			crnRecord.setField("YD_STK_BED_NO",       	"01");
    			crnRecord.setField("YD_STK_LYR_NO",       	ydUtils.addLeftStr(Integer.toString(ii+1), 3, '0'));
                crnRecord.setField("YD_STK_LYR_MTL_STAT", 	"E");
                crnRecord.setField("STL_NO",              	"");
                crnRecord.setField("MODIFIER",              szMODIFIER);

                intRtnVal = this.updYdStklyrY7(crnRecord);  //크레인 적치단의 재료정보 UPDATE

                szMsg = "["+szOperationName+"] ============================= 적치단 업데이트 처리 =============================" + intRtnVal;
    			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    			setRecord = JDTORecordFactory.getInstance().create();
    			setRecord.setField("YD_STK_COL_GP",			ydUtils.substr(szYdDnWrLoc,0,6));
        		setRecord.setField("YD_STK_BED_NO",       	ydUtils.substr(szYdDnWrLoc,6,2));
                setRecord.setField("YD_STK_LYR_NO", 		szYdDnWrLayer);
                setRecord.setField("STL_NO",              	szStlNo);
                setRecord.setField("MODIFIER",              szMODIFIER);

                // 권하실적 처리시 해당 저장위치에 다른 재료 권하 예약 되어 있으면 CLEAR 처리함 ()
                szRtnMsg = this.clearDnLocOtherMtl(setRecord);
                if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                    szMsg = "["+szOperationName+"] >>>> 다른 재료 권하 예약 CLEAR 결과 >>>> " + szRtnMsg;
        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
                	return szRtnMsg;
                }

                //-------------------------------------------------------------------------------------------------------------
                //	같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 경우에는 권상대기 상태로 변경을 하고
                //	그렇지 않은 경우에는 적치중 상태로 변경한다.
                //-------------------------------------------------------------------------------------------------------------
                szMsg = "["+szOperationName+"] 같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		        recPara	= JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID",	szYdCrnSchId);
                recPara.setField("YD_WBOOK_ID",     szYdWbookId);
                recPara.setField("STL_NO",          szStlNo);

                rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
                szRtnMsg = ydCrnWrkMtlDao.getGreaterThanCrnSch(recPara, rsResult);		// intGp == 17

                szMsg = "["+szOperationName+"] 같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

                //-------------------------------------------------------------------------------------------------------------
                if (JPlateYdConst.RETN_CD_NOTEXIST.equals(szRtnMsg)) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", "C");					//적치중
                	isLast = true;
                } else if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", "U");					//권상대기
                	isLast = false;
                } else {
                	szMsg = "["+szOperationName+"] 다음크레인스케줄의 작업재료를 조회 중 오류발생 - " + szRtnMsg;
        			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
        			setRecord.setField("YD_STK_LYR_MTL_STAT", "C");					//적치중으로 반영
        			isLast = true;
                }
                //-------------------------------------------------------------------------------------------------------------
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "isLast = "+ Boolean.toString(isLast), JPlateYdConst.DEBUG);
                if (isLast) {
	                // 적치단에 존재하는 해당 저장품정보를 모두 CLEAR한다.
	                recTemp = JDTORecordFactory.getInstance().create();
	                recTemp.setField("STL_NO", 			szStlNo);
	                recTemp.setField("YD_STK_COL_GP",	ydUtils.substr(szYdUpWrLoc,0,6));
	                recTemp.setField("YD_STK_BED_NO",   ydUtils.substr(szYdUpWrLoc,6,2));
	                recTemp.setField("YD_STK_LYR_NO", 	szYdUpWrLayer);
	                recTemp.setField("MODIFIER",        szMODIFIER);
	                recTemp.setField("YD_GP",			JPlateYdConst.YD_GP_F_PLATE_YARD);

	           // 	intRtnVal = ydStkLyrDao.updYdStklyrClear(recTemp);
	                intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recTemp);  	//적치단의 재료정보 Clear
                }

                // 권하위치 적치상태 변경
                intRtnVal = this.updYdStklyrY7(setRecord);

        	}

			szMsg = "["+szOperationName+"] ============================= 권하실적 적치단 등록 .. END";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
	    }//end of try~catch

		return JPlateYdConst.RETN_CD_SUCCESS;

    }//end of regYdStklyrY7()

    /**
     * 오퍼레이션명 : 권하실적 처리시 해당 저장위치에 다른 재료 권하 예약 되어 있으면 CLEAR 처리함 [점유베드 CLEAR 목적]
     *
     * @param  ● inRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public String clearDnLocOtherMtl(JDTORecord inRec) throws JDTOException{

        JPlateYdStkLyrDAO ydStkLyrDao = new JPlateYdStkLyrDAO();

    	JDTORecordSet rsResult  	= null;
    	JDTORecord recPara 			= null;
    	JDTORecord recTemp 			= null;

    	String 	szMsg 				= "";
        String 	szMethodName		= "clearDnLocOtherMtl";
        String 	szOperationName		= "권하위치 다른 작업예약 CLEAR";

        int 	intRtnVal 			= 0;

        String	szStlNo				= "";
        String	szYdStkColGp		= "";
        String	szYdStkBedNo		= "";
        String	szYdStkLyrNo		= "";
        String	szModifier			= "";
        String	szYdGp				= "";
        String	szYdSpanGp			= "";
        String	szYdStkLyrMtlStat	= "";	// 재료적치상태

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
  		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

  		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
      						
    	try {
			szMsg = "[" + szOperationName + "] 권하위치 다른 작업예약 CLEAR .. START";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        szYdStkColGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP"									);
	        szYdSpanGp		= ydUtils.substr(szYdStkColGp, 2, 2													);
	        szYdStkBedNo	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO"									);
	        szYdStkLyrNo	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_LYR_NO"									);
	        szModifier		= ydDaoUtils.paraRecModifier(inRec													);
	        szYdGp			= ydDaoUtils.paraRecChkNull(inRec, "YD_GP", JPlateYdConst.YD_GP_F_PLATE_YARD		);

	        // 설비일때는 SKIP
	        if ("BC".equals(szYdSpanGp) ||"BS".equals(szYdSpanGp) || "RT".equals(szYdSpanGp) || "CN".equals(szYdSpanGp) || "CB".equals(szYdSpanGp)) {
				szMsg = "[" + szOperationName + "] 설비일때  SKIP >>>> " + szYdSpanGp;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
	        	return JPlateYdConst.RETN_CD_SUCCESS;
	        }

			// ----------------------------------------------
			// 해당 저장위치로 적치단 조회
			// ----------------------------------------------
	        recPara = JDTORecordFactory.getInstance().create();
	        recPara.setField("YD_STK_COL_GP",	szYdStkColGp	);
	        recPara.setField("YD_STK_BED_NO",   szYdStkBedNo	);
	        recPara.setField("YD_STK_LYR_NO", 	szYdStkLyrNo	);
	        recPara.setField("MODIFIER",        szModifier		);
	        recPara.setField("YD_GP",			szYdGp			);

			rsResult  = JDTORecordFactory.getInstance().createRecordSet("temp");
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 inRec에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
			inRec.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
			intRtnVal = ydStkLyrDao.getYdStklyr(inRec, rsResult);

			if (intRtnVal > 0) {
				rsResult.first();
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp = rsResult.getRecord();

				szStlNo           = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO"					);
				szYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT"	);

				// 권하예약일때만 Clear 처리
				if (!"D".equals(szYdStkLyrMtlStat)) {
					szMsg = "[" + szOperationName + "] 권하예약 아님으로 .. SKIP >>>> " + szYdStkLyrMtlStat;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		        	return JPlateYdConst.RETN_CD_SUCCESS;
				}

				if ("".equals(szStlNo)) {		// 해당 베드가 점유 상태 일때
			        recPara.setField("YD_STK_LYR_MTL_STAT",	"E");
					recPara.setField("STL_NO", 				"");
	                intRtnVal = ydStkLyrDao.updYdStklyrStat(recPara);  			//적치단의 재료정보 Clear [조건:저장위치(베드)]
				} else {
			        recPara.setField("YD_STK_LYR_MTL_STAT",	"D");
			        recPara.setField("STL_NO",				szStlNo);
		        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
			        recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
			              	
					intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);  	//적치단의 재료정보 Clear [조건:재료번호]
				}

				szMsg = "[" + szOperationName + "] 권하위치 다른 작업예약 CLEAR .. END >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			}

		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
	    }//end of try~catch

    	return JPlateYdConst.RETN_CD_SUCCESS;

    } // end of clearDnLocOtherMtl()

    /**
     * 오퍼레이션명 : 대차 Setting
     *
     * @param  ● inRecordSet, sTCarId
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public void setYdTcarY7(JDTORecordSet inRecordSet, String sTCarId) throws JDTOException{

    	JPlateYdTcarSchDAO ydTcarSchDao = new JPlateYdTcarSchDAO();

    	//Data Setting
    	JDTORecord setRecord 	= JDTORecordFactory.getInstance().create();
    	//data를 받음
//    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	int intRtnVal 			= 0;
    	String szMethodName		= "setYdTcarY7";
    	String szMsg			= "";

    	try {
            // 2후판정정 야드는 대차이송재료 테이블 참조 안함으로 주석처리
            /*

	    	// 크레인스케줄 Data
	    	inRecordSet.first();
	    	getRecord = inRecordSet.getRecord();

	    	//setRecord 초기화
	    	setRecord     = JDTORecordFactory.getInstance().create();
	    	int szRowSize = inRecordSet.size();

	    	// 권하한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권하한 재료를 등록한다.
	    	for(int ii=0; ii<szRowSize; ii++) {

	    		setRecord.setField("YD_TCAR_SCH_ID"	, sTCarId);
	    		setRecord.setField("STL_NO"			, ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"));
	    		setRecord.setField("REGISTER"		, ((szMethodName.length() > 10) ? szMethodName.substring(0, 10) : szMethodName));
	    		setRecord.setField("YD_STK_BED_NO"	, ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8));
	    		setRecord.setField("YD_STK_LYR_NO"	, ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER"), ii));
	    		intRtnVal = this.insYdTcarFtmvMtlY7(setRecord);
	    		if (intRtnVal == -1) {
		    		szMsg = "대차이송재료 등록 처리중 ERROR!!";
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	        		throw new JDTOException("<procY7CarWrkStatCtr> insYdTcarFtmvMtlY7" + szMsg);
	    		}

	    		inRecordSet.next();
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	*/

    		szMsg = "대차이송재료 등록 후 대차스케줄에 상차완료시간 및  대차설비상태 영차로 등록";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		//상차인 경우에는 대차스케줄의 대차상태를 영차'L'로 업데이트한다.
    		setRecord = JDTORecordFactory.getInstance().create();
    		setRecord.setField("YD_TCAR_SCH_ID", 	sTCarId);			//대차스케줄ID
    		setRecord.setField("YD_EQP_WRK_STAT", 	"L");				//설비작업상태 <L = 영차>

    		intRtnVal = ydTcarSchDao.updYdEqpWrkStat(setRecord);
			if (intRtnVal <= 0) {
    			if (intRtnVal == 0) {
    				szMsg = "ydTcarSchDao data not found";
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING);
    			} else if (intRtnVal == -1) {
    				szMsg = "ydTcarSchDao duplicate data,";
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			} else if (intRtnVal == -2) {
    				szMsg = "ydTcarSchDao parameter error";
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			} else if (intRtnVal == -3){
    				szMsg = "ydTcarSchDao execution failed";
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			}
    		}

		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
		}//end of try~catch

    }//end of setYdTcarY7()

    /**
     * 오퍼레이션명 : 대차이송재료 Insert
     *
     * @param msgRecord
     * @return intRtnVal
     * @throws
     */
    public int insYdTcarFtmvMtlY7(JDTORecord msgRecord) throws DAOException {

    	JPlateYdTcarFtmvMtlDAO ydTcarFtmvMtlDao = new JPlateYdTcarFtmvMtlDAO();
    	int 	intRtnVal 		= 0 ;
    	String 	szMethodName 	= "insYdTcarFtmvMtlY7";
    	String 	szMsg 			= "";
    	String 	szOperationName	= "2후판정정 대차 스케줄 Insert";

        try {

        	intRtnVal = ydTcarFtmvMtlDao.insYdTcarFtmvMtl(msgRecord);
    		if (intRtnVal == -2) {
				szMsg = "["+szOperationName+"] parameter error";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return intRtnVal = -1;
    		}

        } catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
        }
        return intRtnVal ;

    }//end of insYdTcarFtmvMtlY7

	/**
     * 오퍼레이션명 : 2후판정정 강제권하요구처리 (Y7YDL011)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return JDTORecord
     * @throws DAOException
	 */
	public JDTORecord procY7OffCrnDnWr(JDTORecord msgRecord) throws DAOException, JDTOException {

		JDTORecord setRecord 	= JDTORecordFactory.getInstance().create();
        String 	szMsg         	= "";
        String 	szMethodName  	= "procY7OffCrnDnWr";
    	String 	szOperationName	= "2후판정정 강제권하요구처리";

		int		oUpCnt			= 0;
    	int 	intRtnVal 		= 0 ;

        // 레코드 선언
		JDTORecord    recPara   = null;
    	JDTORecord    recInPara	= null;
		JDTORecordSet rsResult  = null;

		JPlateYdCrnSchDAO   ydCrnSchDao = new JPlateYdCrnSchDAO();		// 크레인스케쥴DAO
		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();		// 적치단DAO

		try {
			szMsg = "2후판정정 강제권하요구처리 (Y7YDL011) (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//수신 항목 값
			String 	sMsgId  	= ydUtils.getTcCode(msgRecord);
			String 	ydEqpId    	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 		//야드설비ID
			String 	ydSchCd		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");		//야드스케쥴코드
			String 	ydCrnSchId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");	//야드크레인스케쥴ID
			String 	ydDnWrLoc  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC"); 	//야드권하실적위치
			String 	ydCrnXaxis 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS"); 	//야드크레인X축
			String 	ydCrnYaxis 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS"); 	//야드크레인Y축
			String 	ydDnSpanGp	= ydUtils.substr(ydDnWrLoc, 2, 2);							//강제권하 위치의 설비구분
			String 	ydDnColGp	= ydUtils.substr(ydDnWrLoc, 0, 6);							//강제권하 위치의 적치열구분
			String 	modifier   	= ydDaoUtils.paraRecModifier(msgRecord); 					//수정자(Backup Only)
			if ("".equals(modifier)) {
				modifier = sMsgId;
			}

			//크레인작업실적응답 전문 생성용
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE

			setRecord.setField("YD_EQP_ID", 		ydEqpId); 						 	//야드설비ID
			setRecord.setField("YD_L2_WR_GP", 		"J"); 						 		//야드L2실적구분(지시요구)
			setRecord.setField("YD_L3_HD_RS_CD", 	"FU99"); 						 	//야드L3처리결과코드(Error)
			setRecord.setField("YD_L3_MSG", 		"오류:강제권하요구 예상치 못한 오류"); 		//야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "FU01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "FU02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if (ydDnWrLoc.length() < 8) {
				ydL3HdRsCd = "FU04";
				ydL3Msg    = "오류:권하위치 이상";
			} else if (!ydEqpId.substring(0, 2).equals(ydDnWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "FU05";
				ydL3Msg    = "오류:설비-권하위치[" + ydEqpId.substring(0, 2) + "-" + ydDnWrLoc.substring(0, 2) + "] 부적합";
			} else if ("".equals(ydCrnXaxis)) {
				ydL3HdRsCd = "FU06";
				ydL3Msg    = "오류:크레인X축 없음";
			} else if ("".equals(ydCrnYaxis)) {
				ydL3HdRsCd = "FU07";
				ydL3Msg    = "오류:크레인Y축 없음";
			} else if ("".equals(ydSchCd)) {
				ydL3HdRsCd = "FU08";
				ydL3Msg    = "오류:야드스케쥴코드";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "FU09";
				ydL3Msg    = "오류:야드크레인스케쥴ID";
			}

			// RT에서 강제권하시 FCRT01 , FCRT03, FCRT04만 가능하도록 체크
			if ("RT".equals(ydDnColGp)) {
				if (!"FCRT01".equals(ydDnSpanGp) && !"FCRT02".equals(ydDnSpanGp) && !"FCRT03".equals(ydDnSpanGp)) {
					ydL3HdRsCd = "FU11";
					ydL3Msg    = "오류:강제권하 불가한 RT위치";
				}
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD",	ydL3HdRsCd); 	//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 		//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			//---------------------------------------------------------
			// 1.2 보수장/가스장 일때 공베드 재검색
			//---------------------------------------------------------
			String  szYdStkColGp = ydUtils.substr(ydDnWrLoc, 0, 6);
			String  szYdStkBedNo = ydUtils.substr(ydDnWrLoc, 6, 2);
			if ("BS".equals(ydUtils.substr(ydDnWrLoc, 2, 2))) {
				ydDnWrLoc = ydStkLyrDao.getEmptyBsLoc(szYdStkColGp, szYdStkBedNo, "");
	            szMsg     = "["+szOperationName+"] 보수장일때 적치가능 베드 다시 조회 >>>> " + ydDnWrLoc;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else if ("CN".equals(ydUtils.substr(ydDnWrLoc, 2, 2))) {
				ydDnWrLoc = ydStkLyrDao.getEmptyCncLoc(szYdStkColGp, szYdStkBedNo, "");
	            szMsg     = "["+szOperationName+"] 가스장일때 적치가능 베드 다시 조회 >>>> " + ydDnWrLoc;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			/**********************************************************
			* 2. 권하위치, 좌표값, 이적 재료 등을 Check
				 YD_EQP_ID		//야드설비ID
				 YD_UP_WR_LOC	//야드권상실적위치
				 YD_UP_WR_LAYER	//야드권상실적단
				 YD_CRN_XAXIS	//야드크레인X축
				 YD_CRN_YAXIS	//야드크레인Y축
				 YD_EQP_WRK_SH	//야드크레인작업매수
			**********************************************************/
			recPara  = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
			recPara.setField("YD_EQP_ID", 		ydEqpId); 					//야드설비ID
			recPara.setField("YD_STK_COL_GP", 	ydDnWrLoc.substring(0,6)); 	//야드적치열구분
			recPara.setField("YD_STK_BED_NO", 	ydDnWrLoc.substring(6,8)); 	//야드적치Bed번호
			recPara.setField("YD_CRN_XAXIS", 	ydCrnXaxis); 				//야드크레인X축
			recPara.setField("YD_CRN_YAXIS", 	ydCrnYaxis); 				//야드크레인Y축

			//좌표값 에 해당하는 재료정보 조회
			intRtnVal = ydCrnSchDao.getOffCrnDnBed(recPara, rsResult);

			String ydStkLyrNo    = ""; //야드적치단번호
			String xaxisYn       = ""; //X좌표 정합성여부
			String yaxisYn       = ""; //Y좌표 정합성여부
			String ydStkBedXaxis = ""; //야드적치BedX축
			String ydStkBedYaxis = ""; //야드적치BedY축

			if (rsResult != null && rsResult.size() > 0) {
				ydStkLyrNo    	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_LYR_NO");
				xaxisYn       	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "XAXIS_YN");
				yaxisYn       	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YAXIS_YN");
				ydStkBedXaxis 	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_BED_XAXIS");
				ydStkBedYaxis 	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_BED_YAXIS");
				ydDnWrLoc		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_LOC");
				oUpCnt 			= ydDaoUtils.paraRecChkNullInt(rsResult.getRecord(0), "O_UP_CNT");
			}

			if (intRtnVal > 0) {
				if (!"Y".equals(xaxisYn)) {
					//권하위치의 BedX축 허용오차 값내에 크레인X축 값 존재여부를 Check
					ydL3HdRsCd = "FU21";
					ydL3Msg    = "오류:크레인X축[" + ydStkBedXaxis + ":" + ydCrnXaxis + "] 이상";
				} else if (!"Y".equals(yaxisYn)) {
					//권하위치의 BedY축 허용오차 값내에 크레인Y축 값 존재여부를 Check
					ydL3HdRsCd = "FU22";
					ydL3Msg    = "오류:크레인Y축[" + ydStkBedYaxis + ":" + ydCrnYaxis + "] 이상";
				} else if (oUpCnt > 0) {
					//권하위치의 권상예약정보 존재여부 체크
					ydL3HdRsCd = "FU23";
					ydL3Msg    = "오류:권상예약 정보 존재로 오류발생!";
				}
			} else {
				//강제권하 가능 위치정보 미존재
				ydL3HdRsCd = "FU24";
			//	ydL3Msg    = "오류:강제권하 가능 위치정보 미존재! " + ydDnWrLoc;
				ydL3Msg    = "강제권하 가능 베드 선택 오류! " + ydDnWrLoc;
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); 	//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 		//야드L3MESSAGE
				setRecord.setField("RTN_CD", 			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 3. TO위치 변경 처리 .. EJB호출
			**********************************************************/
			szMsg = "2후판정정 강제권하요구처리 .. TO위치 변경 처리 .. EJB호출 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			setRecord.setField("YD_CRN_SCH_ID", 	ydCrnSchId);
			setRecord.setField("YD_EQP_ID", 		ydEqpId);
			setRecord.setField("YD_DN_WO_LOC", 		ydDnWrLoc);								//권하위치
			setRecord.setField("YD_DN_WO_LAYER", 	ydStkLyrNo);
			setRecord.setField("YD_GP", 			JPlateYdConst.YD_GP_F_PLATE_YARD);		//2후판정정야드
			setRecord.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_WO);		//권하지시
			setRecord.setField("YD_DN_WRK_ACT_GP",	JPlateYdConst.YD_PILING_GP_F);   		//야드권하작업수행구분 : 강제권하
			setRecord.setField("MODIFIER", 			modifier);								//수정자

			EJBConnector ejbConn = new EJBConnector("default", this);
			String rtnMsg = (String)ejbConn.trx("JPlateYdCrnReSchSeEJB", "updCrnDnPrsFix", setRecord);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {							//실패
				setRecord.setField("YD_L3_HD_RS_CD", 	"FU98"); 							//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		"오류:"+rtnMsg); 					//야드L3MESSAGE
				setRecord.setField("RTN_CD", 			JPlateYdConst.RETN_CD_FAILURE);
				szMsg = "2후판정정 강제권하요구처리 .. TO위치 변경 처리 .. EJB호출 오류발생>>>>" + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return setRecord;
			}
			szMsg = "2후판정정 강제권하요구처리 .. TO위치 변경 처리 .. EJB호출 종료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			/* 권하위치 변경에서 호출함으로 ... 주석처리함
			//--------------------------------------------------------
			// 9. 크레인작업지시(YDY7L004) 전문 전송
			//--------------------------------------------------------
        	szMsg = "2후판정정 강제권하요구처리 .. 크레인작업지시(YDY7L004) 전문 전송";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//설비 야드설비상태 수정
			recPara.setField("YD_EQP_STAT", 		JPlateYdConst.YD_EQP_STAT_DN_WO); 		//권하작업지시
			recPara.setField("MODIFIER", 			modifier);
			ydEqpDao.updYdEqpStat(recPara);

        	recInPara = JDTORecordFactory.getInstance().create();
    		//작업지시 전문 전송 data setup
			recInPara.setField("MSG_ID", 			"YDY7L004");
        	recInPara.setField("YD_CRN_SCH_ID",    	ydCrnSchId);
        	recInPara.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_WO);
        	recInPara.setField("YD_SCH_CD",        	ydSchCd);
        	recInPara.setField("YD_GP",            	JPlateYdConst.YD_GP_F_PLATE_YARD);
        	recInPara.setField("MODIFIER", 			modifier);

        	//작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
        	recInPara.setField("MSG_GP", 			"U");
        	ydDelegate.sendMsg(recInPara);

			szMsg = "["+ szMethodName + "] 2후판정정 강제권하요구처리 (Y7YDL011) END";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			*/

        	recInPara = JDTORecordFactory.getInstance().create();
        	recInPara.setField("RTN_CD", 		JPlateYdConst.RETN_CD_SUCCESS);
        	recInPara.setField("YD_L3_MSG", 	JPlateYdConst.RETN_CD_SUCCESS);
			return recInPara;

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}

	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/		
	 
	/**
     * 오퍼레이션명 : 1후판정정 권하실적처리 (Y2YDL009)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY2CrnDnWr(JDTORecord msgRecord) throws DAOException, JDTOException {

    	JPlateYdDelegate      	ydDelegate		= new JPlateYdDelegate();
    	JPlateYdEqpDAO        	ydEqpDao        = new JPlateYdEqpDAO();
    	JPlateYdTcarSchDAO    	ydTcarSchDao    = new JPlateYdTcarSchDAO();
    	JPlateYdStockDAO      	ydStockDao      = new JPlateYdStockDAO();
    	JPlateYdWrkbookDAO    	ydWrkbookDao    = new JPlateYdWrkbookDAO();
    	JPlateYdCrnWrkMtlDAO  	ydCrnWrkMtlDao	= new JPlateYdCrnWrkMtlDAO();
    	JPlateYdWrkbookMtlDAO 	ydWrkbookMtlDao	= new JPlateYdWrkbookMtlDAO();
    	JPlateYdCrnSchDAO 		ydCrnschDao 	= new JPlateYdCrnSchDAO();
    	JPlateYdStkLyrDAO     	ydStkLyrDao     = new JPlateYdStkLyrDAO();
    	
        int intRtnVal = 0;

        //DATA SETTING시 사용
        JDTORecord setRecord 		= JDTORecordFactory.getInstance().create();

        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord        = JDTORecordFactory.getInstance().create();

        //작업예약 업데이트 항목
        JDTORecord bookrecord       = JDTORecordFactory.getInstance().create();

        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 	= JDTORecordFactory.getInstance().create();

        JDTORecord recInTemp        = null;
        JDTORecord recOutTemp       = null;
        JDTORecord recInPara        = null;

        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 	= null;

        JDTORecord    recSendMsg	= null;
        JDTORecordSet rsResult      = null;

        String 	szMsg            	= "";
        String 	szRtnMsg            = "";
        String	szCallMsg			= "";
        String 	szSendMsg           = "";
        String 	szMethodName     	= "procY2CrnDnWr";
        String 	szOperationName  	= "1후판정정 권하실적처리";
        String 	szTcarEqpId      	= "";

        //크레인 XYZ축 				파라미터에서 값이 오지 않았을때 지시위치값을 저장해서 실적등록에 사용
        String 	szYdCrnXaxis     	= "";
        String 	szYdCrnYaxis     	= "";
        String 	szYdCrnZaxis     	= "";
        String 	szYdWbookId     	= "";
        String 	szCrnSchId       	= "";

        String 	szYdUpWrLoc      	= "";
        String 	szYdDnWrLoc      	= "";
        String 	szYdSchCd        	= "";
        String 	szYdEqpId        	= "";
        String 	szYdDnWrLayer    	= "";
        String 	szYdTcarSchId    	= "";
        String	szModifier			= "";
        String	szNextSchCallYn		= "N";		// 권하 완료후 다음 스케줄 호출 Flag - 작업예약 삭제후 Set됨
        String	szStlNo				= "";

        String[]	arrStlNo		= {"","","","","" ,"","","","","" ,"","","","",""};
		String[]	arrYdDnWrLoc	= {"","","","","" ,"","","","","" ,"","","","",""};
		String[]	arrYdDnWrLayer	= {"","","","","" ,"","","","","" ,"","","","",""};
		String[]	arrYdDnWrBedNo	= {"","","","","" ,"","","","","" ,"","","","",""};

        String 	szYdStkColGp		= "";
        String 	szYdStkBedNo     	= "";
        String 	szRealTopLyr     	= "";
        String	szArrStlNo			= "";
        String	szTemp				= "";
        String	szYdUpWrkActGp		= "";

        int		iMtlCnt				= 0;		// 권하실적 재료 갯수

        String 	szRcvTcCode			= ydUtils.getTcCode(msgRecord);
        if (szRcvTcCode==null || "".equals(szRcvTcCode)) {
            szRtnMsg = "TC Code Error ("+szRcvTcCode+")";
            szMsg    = "["+szOperationName+"] " + szRtnMsg;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
            throw new DAOException(szRtnMsg);
        }

        if (bDebugFlag) {
            szMsg = "["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
        }

        try {
        	szMsg = "["+szOperationName+"] 메소드 시작 - 파라미터 확인 >>>> " + msgRecord.toString();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


        	//-------------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------------
	        intRtnVal 		= this.paramCheckY7(msgRecord, getCrnschRecord);

	        szCrnSchId 		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_CRN_SCH_ID");
	        szYdEqpId 		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_EQP_ID");
	        szYdSchCd 		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_SCH_CD");
	        szYdDnWrLoc		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC");
	        szYdDnWrLayer	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER");

	        // L2에서 전송하는 15건으로 증가
	        for(int ii=0; ii<15; ii++) {
		        arrStlNo[ii]			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "STL_NO"+(ii+1));			// 재료번호1~15			CHAR	11
		        if (!"".equals(arrStlNo[ii])) {
			        arrYdDnWrLoc[ii]	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC"+(ii+1));	// 야드권하실적위치1~15	CHAR	8
			        arrYdDnWrLayer[ii]	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER"+(ii+1));	// 야드권하실적단1~15		CHAR	3

			        // 권하위치 오류 체크
			        if ("".equals(arrYdDnWrLoc[ii]) || arrYdDnWrLoc[ii].length() != 8) {
			            szRtnMsg = "권하위치 입력 오류 .. " + arrYdDnWrLoc[ii];
			            szMsg    = "["+szOperationName+"] " + szRtnMsg;
				        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				        throw new DAOException(szRtnMsg);
			        }
			        // 적치단 오류 체크
			        if ("".equals(arrYdDnWrLayer[ii]) || arrYdDnWrLayer[ii].length() != 3 || Integer.parseInt(arrYdDnWrLayer[ii]) < 1) {
			            szRtnMsg = "적치단 입력 오류 .. " + arrYdDnWrLayer[ii];
			            szMsg    = "["+szOperationName+"] " + szRtnMsg;
				        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				        throw new DAOException(szRtnMsg);
			        }

			        arrYdDnWrBedNo[ii]	= ydUtils.substr(arrYdDnWrLoc[ii], 6, 2);
			        iMtlCnt ++;
		        }
	        }

            szMsg    = "["+szOperationName+"] 권하실적처리 대상 매수 >>>> " + Integer.toString(iMtlCnt);
	        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        // SORT - 적치단 ASC , 적치베드 ASC
	        for(int ii=0; ii<iMtlCnt; ii++) {
		        for(int jj=(ii+1); jj<iMtlCnt; jj++) {
		        	if ("".equals(arrStlNo[jj])) {
		        		continue;
		        	}
		        	if (Integer.parseInt(arrYdDnWrLayer[ii])  > Integer.parseInt(arrYdDnWrLayer[jj]) ||
		        	   (Integer.parseInt(arrYdDnWrLayer[ii]) == Integer.parseInt(arrYdDnWrLayer[jj]) &&
		        		Integer.parseInt(arrYdDnWrBedNo[ii])  > Integer.parseInt(arrYdDnWrBedNo[jj]))) {

		        		szTemp 				= arrStlNo[ii];
		    		    arrStlNo[ii]		= arrStlNo[jj];
		    		    arrStlNo[jj]		= szTemp;

		        		szTemp 				= arrYdDnWrLoc[ii];
				        arrYdDnWrLoc[ii]	= arrYdDnWrLoc[jj];
				        arrYdDnWrLoc[jj]	= szTemp;

				        szTemp				= arrYdDnWrLayer[ii];
				        arrYdDnWrLayer[ii]	= arrYdDnWrLayer[jj];
				        arrYdDnWrLayer[jj]	= szTemp;

				        szTemp				= arrYdDnWrBedNo[ii];
				        arrYdDnWrBedNo[ii]	= arrYdDnWrBedNo[jj];
				        arrYdDnWrBedNo[jj]	= szTemp;
		        	}
		        }
	        }

	        // DEBUG .. SORT결과 출력
	        for(int ii=0; ii<iMtlCnt; ii++) {
	            szMsg    = "["+szOperationName+"] SORT 결과 출력 " + (ii+1) + " >>>> 재료번호 :: " + arrStlNo[ii] + ", 권하위치 :: " + arrYdDnWrLoc[ii] + "-" + arrYdDnWrLayer[ii];
		        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	        }

	        szModifier = ydDaoUtils.paraRecModifier(msgRecord);

	        if ("".equals(szCrnSchId)) {
	            szRtnMsg = "크레인스케줄ID가 없습니다.";
	            szMsg    = "["+szOperationName+"] " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                throw new DAOException(szRtnMsg);
	        }

	        //파라미터 레코드 편집
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID", 	szCrnSchId);
	        setRecord.setField("YD_SCH_CD", 		szYdSchCd);
	        setRecord.setField("YD_DN_WR_LOC", 		szYdDnWrLoc);
	        setRecord.setField("YD_DN_WR_LAYER", 	szYdDnWrLayer);
	        setRecord.setField("MODIFIER", 			szModifier);

	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg = "["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYdDnWrLoc+"]와 권하실적단["+szYdDnWrLayer+"]을 업데이트 시작";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnDnWr 

            UPDATE TB_YD_CRNSCH
               SET
                   MODIFIER         = :V_MODIFIER
                 , MOD_DDTT         = SYSDATE
                 , YD_WRK_PROG_STAT = NVL(:V_YD_WRK_PROG_STAT, YD_WRK_PROG_STAT)    -- NULL일때 전 상태값 유지
                 , YD_DN_WR_LOC     = :V_YD_DN_WR_LOC
                 , YD_DN_WR_LAYER   = :V_YD_DN_WR_LAYER
                 , YD_DN_WRK_ACT_GP = :V_YD_DN_WRK_ACT_GP
                 , YD_DN_WR_XAXIS   = :V_YD_DN_WR_XAXIS
                 , YD_DN_WR_YAXIS   = :V_YD_DN_WR_YAXIS
                 , YD_DN_WR_ZAXIS   = :V_YD_DN_WR_ZAXIS
                 , YD_DN_CMPL_DT    = TO_DATE(:V_YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS')
                 , YD_WRK_HDS_DD    = :V_YD_WRK_HDS_DD
             WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID

            */ 
			intRtnVal = ydCrnschDao.updCrnDnWr(setRecord);

			szMsg = "["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYdDnWrLoc+"]와 권하실적단["+szYdDnWrLayer+"]을 업데이트 완료";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            //-------------------------------------------------------------------------------------------------------------------
	        //	크레인스케줄 작업재료 조회
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 시작";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
            /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtl 
        	-- 크레인작업재료 조회 (야드L2작업지시 송신용)

        	SELECT Z.*
        	  FROM
        	(
        	    SELECT A.YD_CRN_SCH_ID                                  AS YD_CRN_SCH_ID
        	         , A.REGISTER                                       AS REGISTER
        	         , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')          AS REG_DDTT
        	         , A.MODIFIER                                       AS MODIFIER
        	         , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')          AS MOD_DDTT
        	         , A.DEL_YN                                         AS DEL_YN
        	         , A.YD_WBOOK_ID                                    AS YD_WBOOK_ID
        	         , A.YD_EQP_ID                                      AS YD_EQP_ID
        	         , A.YD_GP                                          AS YD_GP
        	         , A.YD_BAY_GP                                      AS YD_BAY_GP
        	         , A.YD_SCH_CD                                      AS YD_SCH_CD
        	         , A.YD_SCH_ST_GP                                   AS YD_SCH_ST_GP
        	         , A.YD_SCH_REQ_GP                                  AS YD_SCH_REQ_GP
        	         , A.YD_SCH_PRIOR                                   AS YD_SCH_PRIOR
        	         , A.YD_EQP_WRK_STAT                                AS YD_EQP_WRK_STAT
        	         , A.YD_WRK_PROG_STAT                               AS YD_WRK_PROG_STAT
        	         , A.YD_WBOOK_DT                                    AS YD_WBOOK_DT
        	         , TO_CHAR(A.YD_SCH_DT,     'YYYYMMDDHH24MISS')     AS YD_SCH_DT
        	         , TO_CHAR(A.YD_WORD_DT,    'YYYYMMDDHH24MISS')     AS YD_WORD_DT
        	         , TO_CHAR(A.YD_UP_CMPL_DT, 'YYYYMMDDHH24MISS')     AS YD_UP_CMPL_DT
        	         , TO_CHAR(A.YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS')     AS YD_DN_CMPL_DT
        	         , A.YD_WRK_HDS_DD                                  AS YD_WRK_HDS_DD
        	         , A.YD_WRK_DUTY                                    AS YD_WRK_DUTY
        	         , A.YD_WRK_PARTY                                   AS YD_WRK_PARTY
        	         , A.YD_MAIN_WRK_MTL_SH                             AS YD_MAIN_WRK_MTL_SH
        	         , A.YD_AID_WRK_MTL_SH                              AS YD_AID_WRK_MTL_SH
        	         , A.YD_AID_WRK_UPDN_GP                             AS YD_AID_WRK_UPDN_GP
        	         , A.YD_TO_LOC_DCSN_MTD                             AS YD_TO_LOC_DCSN_MTD
        	         , A.YD_TO_LOC_GUIDE                                AS YD_TO_LOC_GUIDE
        	         , A.YD_EQP_WRK_SH                                  AS YD_EQP_WRK_SH
        	         , A.YD_EQP_WRK_WT                                  AS YD_EQP_WRK_WT
        	         , A.YD_EQP_WRK_T                                   AS YD_EQP_WRK_T
        	         , A.YD_EQP_WRK_MAX_W                               AS YD_EQP_WRK_MAX_W
        	         , A.YD_EQP_WRK_MAX_L                               AS YD_EQP_WRK_MAX_L
        	         , A.YD_CRN_SB_CTL_H                                AS YD_CRN_SB_CTL_H
        	         , A.YD_CRN_GRAB_USE_RULE_ID                        AS YD_CRN_GRAB_USE_RULE_ID
        	         , A.YD_UP_WO_LOC                                   AS YD_UP_WO_LOC
        	         , A.YD_UP_WO_LAYER                                 AS YD_UP_WO_LAYER
        	         , A.YD_UP_WO_LOC_XAXIS                             AS YD_UP_WO_LOC_XAXIS
        	         , A.YD_UP_WO_XAXIS_GAP_MAX                         AS YD_UP_WO_XAXIS_GAP_MAX
        	         , A.YD_UP_WO_XAXIS_GAP_MIN                         AS YD_UP_WO_XAXIS_GAP_MIN
        	         , A.YD_UP_WO_LOC_YAXIS                             AS YD_UP_WO_LOC_YAXIS
        	         , A.YD_UP_WO_LOC_YAXIS1                            AS YD_UP_WO_LOC_YAXIS1
        	         , A.YD_UP_WO_LOC_YAXIS2                            AS YD_UP_WO_LOC_YAXIS2
        	         , A.YD_UP_WO_YAXIS_GAP_MAX                         AS YD_UP_WO_YAXIS_GAP_MAX
        	         , A.YD_UP_WO_YAXIS_GAP_MIN                         AS YD_UP_WO_YAXIS_GAP_MIN
        	         , A.YD_UP_WO_LOC_ZAXIS                             AS YD_UP_WO_LOC_ZAXIS
        	         , A.YD_UP_WO_ZAXIS_GAP_MAX                         AS YD_UP_WO_ZAXIS_GAP_MAX
        	         , A.YD_UP_WO_ZAXIS_GAP_MIN                         AS YD_UP_WO_ZAXIS_GAP_MIN
        	    --   , A.YD_DN_WO_LOC                                   AS YD_DN_WO_LOC
        	    --   , A.YD_DN_WO_LAYER                                 AS YD_DN_WO_LAYER

        	         -- 권하지시위치
        	         , CASE WHEN A.YD_EQP_WRK_SH > 1 AND B.YD_STK_LOT_TP IS NOT NULL
        	                                         AND SUBSTR(A.YD_DN_WO_LOC,1,2) <> 'XX'
        	               THEN NVL((SELECT MAX(S.YD_STK_COL_GP || S.YD_STK_BED_NO)
        	                           FROM TB_YD_STKLYR S
        	                          WHERE S.STL_NO = B.STL_NO
        	                            AND S.YD_STK_LYR_MTL_STAT = 'D'
        	                            AND S.DEL_YN = 'N'
        	                        ), (SUBSTR(A.YD_DN_WO_LOC,1,6) || B.YD_STK_LOT_TP))
        	               ELSE A.YD_DN_WO_LOC
        	           END                                              AS YD_DN_WO_LOC

        	         -- 권하지시단
        	         , CASE WHEN A.YD_EQP_WRK_SH > 1 AND B.YD_STK_LOT_TP IS NOT NULL
        	                                         AND SUBSTR(A.YD_DN_WO_LOC,1,2) <> 'XX'
        	               THEN NVL((SELECT MAX(S.YD_STK_LYR_NO)
        	                           FROM TB_YD_STKLYR S
        	                          WHERE S.STL_NO = B.STL_NO
        	                            AND S.YD_STK_LYR_MTL_STAT = 'D'
        	                            AND S.DEL_YN = 'N'
        	                        ), B.YD_STK_LYR_NO)
        	               ELSE A.YD_DN_WO_LAYER
        	           END                                              AS YD_DN_WO_LAYER

        	         , A.YD_DN_WO_LOC_XAXIS                             AS YD_DN_WO_LOC_XAXIS
        	         , A.YD_DN_WO_XAXIS_GAP_MAX                         AS YD_DN_WO_XAXIS_GAP_MAX
        	         , A.YD_DN_WO_XAXIS_GAP_MIN                         AS YD_DN_WO_XAXIS_GAP_MIN
        	         , A.YD_DN_WO_LOC_YAXIS                             AS YD_DN_WO_LOC_YAXIS
        	         , A.YD_DN_WO_LOC_YAXIS1                            AS YD_DN_WO_LOC_YAXIS1
        	         , A.YD_DN_WO_LOC_YAXIS2                            AS YD_DN_WO_LOC_YAXIS2
        	         , A.YD_DN_WO_YAXIS_GAP_MAX                         AS YD_DN_WO_YAXIS_GAP_MAX
        	         , A.YD_DN_WO_YAXIS_GAP_MIN                         AS YD_DN_WO_YAXIS_GAP_MIN
        	         , A.YD_DN_WO_LOC_ZAXIS                             AS YD_DN_WO_LOC_ZAXIS
        	         , A.YD_DN_WO_ZAXIS_GAP_MAX                         AS YD_DN_WO_ZAXIS_GAP_MAX
        	         , A.YD_DN_WO_ZAXIS_GAP_MIN                         AS YD_DN_WO_ZAXIS_GAP_MIN
        	         , A.YD_UP_WR_LOC                                   AS YD_UP_WR_LOC
        	         , A.YD_UP_WR_LAYER                                 AS YD_UP_WR_LAYER
        	         , A.YD_UP_WRK_ACT_GP                               AS YD_UP_WRK_ACT_GP
        	         , A.YD_UP_WR_XAXIS                                 AS YD_UP_WR_XAXIS
        	         , A.YD_UP_WR_YAXIS                                 AS YD_UP_WR_YAXIS
        	         , A.YD_UP_WR_YAXIS1                                AS YD_UP_WR_YAXIS1
        	         , A.YD_UP_WR_YAXIS2                                AS YD_UP_WR_YAXIS2
        	         , A.YD_UP_WR_ZAXIS                                 AS YD_UP_WR_ZAXIS
        	         , A.YD_DN_WR_LOC                                   AS YD_DN_WR_LOC
        	         , A.YD_DN_WR_LAYER                                 AS YD_DN_WR_LAYER
        	         , A.YD_DN_WRK_ACT_GP                               AS YD_DN_WRK_ACT_GP
        	         , A.YD_DN_WR_XAXIS                                 AS YD_DN_WR_XAXIS
        	         , A.YD_DN_WR_YAXIS                                 AS YD_DN_WR_YAXIS
        	         , A.YD_DN_WR_YAXIS1                                AS YD_DN_WR_YAXIS1
        	         , A.YD_DN_WR_YAXIS2                                AS YD_DN_WR_YAXIS2
        	         , A.YD_DN_WR_ZAXIS                                 AS YD_DN_WR_ZAXIS
        	         , B.STL_NO                                         AS STL_NO
        	         , B.YD_AID_WRK_YN                                  AS YD_AID_WRK_YN
        	    --   , B.YD_STK_LYR_NO                                  AS YD_STK_LYR_NO
        	         , B.YD_STK_LOT_TP                                  AS YD_STK_LOT_TP
        	         , B.YD_STK_LOT_CD                                  AS YD_STK_LOT_CD
        	         , B.HCR_GP                                         AS HCR_GP
        	         , B.STL_PROG_CD                                    AS STL_PROG_CD
        	         , B.YD_MTL_ITEM                                    AS YD_MTL_ITEM
        	         , B.YD_ROUTE_GP                                    AS YD_ROUTE_GP
        	         , B.YD_MTL_WT                                      AS YD_MTL_WT
        	         , B.YD_MTL_T                                       AS YD_MTL_T
        	         , B.YD_MTL_W                                       AS YD_MTL_W
        	         , B.YD_MTL_L                                       AS YD_MTL_L
        	         -- 권상지시의 적치단
        	         ,(SELECT MAX(S.YD_STK_LYR_NO)
        	             FROM TB_YD_STKLYR S
        	            WHERE S.STL_NO = B.STL_NO
        	              AND S.YD_STK_LYR_MTL_STAT IN ('C','U')
        	              AND S.YD_STK_COL_GP LIKE A.YD_GP || '%'
        	          )                                                 AS YD_STK_LYR_NO
        	         -- 파일링코드 RTBOOK-IN 일때만 SET : FCRT01UM
        	         , CASE WHEN (SUBSTR(A.YD_SCH_CD,3,2) || SUBSTR(A.YD_SCH_CD,7,2)) = 'RTUM' THEN
        	                    NVL((SELECT MAX(S.YD_PILING_CD)
        	                           FROM TB_YD_STOCK S
        	                          WHERE S.STL_NO = B.STL_NO
        	                            AND S.DEL_YN = 'N'), 'NOPILING')
        	               ELSE ''
        	           END                                              AS YD_PILING_CD
        	      FROM TB_YD_CRNSCH A
        	         ,(SELECT Y.YD_CRN_SCH_ID
        	                , Y.STL_NO
        	                , Y.YD_AID_WRK_YN
        	                , Y.YD_STK_LYR_NO
        	                , Y.YD_STK_LOT_TP
        	                , Y.YD_STK_LOT_CD
        	                , Y.HCR_GP
        	                , (SELECT CURR_PROG_CD FROM TB_PT_PLATECOMM WHERE PLATE_NO=X.STL_NO) AS STL_PROG_CD
        	                , Y.YD_MTL_ITEM
        	                , Y.YD_ROUTE_GP
        	                , X.YD_MTL_T
        	                , X.YD_MTL_W
        	                , X.YD_MTL_L
        	                , X.YD_MTL_WT
        	             FROM TB_YD_SHRSTOCK  X
        	                , TB_YD_CRNWRKMTL Y
        	             WHERE X.STL_NO = Y.STL_NO
        	          ) B
        	     WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
        	       AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
        	) Z
        	ORDER BY Z.YD_DN_WO_LAYER, Z.YD_DN_WO_LOC, Z.STL_NO
        	*/
            intRtnVal = this.getYdCrnSchY7(setRecord, getRecSet);
	        if (intRtnVal < 0) {
	            szRtnMsg = "크레인스케줄 작업재료 조회 오류 .. " + Integer.toString(intRtnVal);
	            szMsg    = "["+szOperationName+"] " + szRtnMsg;
		        ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		        throw new DAOException(szRtnMsg);
			}

	        //레코드셋의 사이즈값으로 ErrorCheck
	        if (getRecSet.size() == 0) {
	            szRtnMsg = "권하실적 크레인작업재료  조회 오류 .. " + Integer.toString(intRtnVal);
	            szMsg    = "["+szOperationName+"] " + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	            throw new DAOException(szRtnMsg);
	        }

	        // 조업 L3전송용 재료번호 편집
	        getRecSet.first();
	        for(int ii=0; ii<getRecSet.size(); ii++) {
	        	getRecSet.absolute(ii+1);
		        getRecord = getRecSet.getRecord();
		        if (ii > 0) {
		        	szArrStlNo = szArrStlNo + ";";
		        }
	        	szArrStlNo = szArrStlNo + ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
	        }

	        getRecSet.first();
	        getRecord = getRecSet.getRecord();

	        szMsg = "["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 완료 - 대상재 건수 : " + Integer.toString(getRecSet.size());
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            szYdUpWrLoc 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
	        szYdWbookId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID");			//작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szStlNo			= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
	        szYdUpWrkActGp 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WRK_ACT_GP");		//야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)

	        //-------------------------------------------------------------------------------------------------------------------
	        //	작업진행상태 확인 후 상태가 맞지 않으면 롤백 처리
            //-------------------------------------------------------------------------------------------------------------------
        	//작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
	        if (!"2".equals(ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT")) &&
	        	!"3".equals(ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT"))) {

	            szRtnMsg = "작업진행상태["+ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT")+"]가 권상완료 아님!.";
	            szMsg    = "["+szOperationName+"] " + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	            throw new DAOException(szRtnMsg);
	        }

	        //-------------------------------------------------------------------------------------------------------------------
	        // 실제적치단의 조회
	        //-------------------------------------------------------------------------------------------------------------------
        	if (!"".equals(arrYdDnWrLoc[0])) {
        		szYdDnWrLoc = arrYdDnWrLoc[0];
        		for(int ii=0; ii<15; ii++) {
        			if (!"".equals(arrYdDnWrLoc[ii]) && "01".equals(ydUtils.substr(arrYdDnWrLoc[ii], 6, 2))) {
                		szYdDnWrLoc = arrYdDnWrLoc[ii];
        			}
        		}
        	}
	        szYdStkColGp   = szYdDnWrLoc.substring(0, 6);
	        szYdStkBedNo   = szYdDnWrLoc.substring(6, 8);

	        if (!"BC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) && !"BS".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) && !"CN".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

		        szRealTopLyr = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdStkBedNo, szStlNo, szYdUpWrkActGp, "");

		        if ("000".equals(szRealTopLyr)) {
		            szRtnMsg = "권하실적 최상단 검색시 오류발생 :: " + szRealTopLyr;
		            szMsg    = "["+szOperationName+"] " + szRtnMsg;
		            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		            throw new DAOException(szRtnMsg);
				}
	        }

	        //-------------------------------------------------------------------------------------------------------------------
	        // 권하지시위치와 권하실적위치가 다르면 권하지시위치의 맵정보를 Clear시킴
	        // 보수장일때도 CLEAR하도록 변경 --> 보수장 권하시 새로운 저장위치 검색하도록 변경
	        // 가스장도 추가
	        //-------------------------------------------------------------------------------------------------------------------
            String sDnWoLoc = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC") + "-" + szYdDnWrLayer;
            String sDnWrLoc = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC") + "-" + szRealTopLyr;

            szMsg  = "["+szOperationName+"] 권하지시위치 :: " + sDnWoLoc + "  권하실적위치 :: " + sDnWrLoc;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        if ("BC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "BS".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "CN".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || !sDnWoLoc.equals(sDnWrLoc)) {

                szMsg = "["+szOperationName+"] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 시작 .. " + "지시::" + sDnWoLoc + ", 실적::" + sDnWrLoc;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	//크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
	        	if ("".equals(szYdCrnXaxis) && "".equals(szYdCrnYaxis) && "".equals(szYdCrnZaxis)) {
	        		szYdCrnXaxis = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC_XAXIS");
	        		szYdCrnYaxis = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC_YAXIS");
	        		szYdCrnZaxis = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC_ZAXIS");
	        	}

	        	intRtnVal = this.clearYdStklyrY2(getRecSet, szModifier);	// 권하 지시위치 Clear
	        	if (intRtnVal < 0) {
		            szRtnMsg = "권하 지시위치 Clear시 오류 .. " + Integer.toString(intRtnVal);
		            szMsg    = "["+szOperationName+"] " + szRtnMsg;
		            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		            throw new DAOException(szRtnMsg);
				}

    	        szMsg = "["+szOperationName+"]권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 완료";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	        }

	        //-------------------------------------------------------------------------------------------------------------------
	        //  권하실적LYR와 실제적치단의 최상위 위치가 다르면 권하실적LYR를 변경하고 적치단 정보를 수정한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        if (!szYdDnWrLayer.equals(szRealTopLyr)) {
	        	szMsg = "["+szOperationName+"] 실적적치단[" + szYdDnWrLayer + "]과 실제야드적치단[" + szRealTopLyr + "]이 상이하여 실적적치단 변경 시작";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	            szYdDnWrLayer = szRealTopLyr;
    	    }

	        //-------------------------------------------------------------------------------------------------------------------
	        //	크레인작업재료의 적치단을 적치중으로 변경
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg = "["+szOperationName+"] 크레인작업재료의 적치단을 적치중으로 변경 시작";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

//---------------------------------------------------------------------------------------------
// 2024.12.06 regYdStklyrY2 method에 argument에 logId 추가 했기 때문에 regYdStklyrY2 call 시 logId 대신 "" 추가   
//	        szRtnMsg = this.regYdStklyrY2(getRecSet, szModifier, arrStlNo, arrYdDnWrLoc, szYdUpWrkActGp, szRealTopLyr);
	        szRtnMsg = this.regYdStklyrY2(getRecSet, szModifier, arrStlNo, arrYdDnWrLoc, szYdUpWrkActGp, szRealTopLyr, "");
//---------------------------------------------------------------------------------------------
	        if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
		        szMsg = "["+szOperationName+"] 크레인작업재료의 적치단을 적치중으로 변경 완료";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	        } else {
		        szMsg = "["+szOperationName+"] 크레인작업재료의 적치단을 적치중으로 변경 오류 발생 >>>> " + szRtnMsg;
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	            throw new DAOException(szRtnMsg);
	        }

            //-------------------------------------------------------------------------------------------------------------------
	        // 보수장일때 적치위치를 다시 조회 (보수장은 권하위치 틀림)
            //-------------------------------------------------------------------------------------------------------------------
	        if ("BC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "BS".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "CN".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || !sDnWoLoc.equals(sDnWrLoc)) {

	        	rsResult   = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
		        setRecord  = JDTORecordFactory.getInstance().create();
		        setRecord.setField("STL_NO"	,	szStlNo);
		        setRecord.setField("YD_GP"	,	JPlateYdConst.YD_GP_P_PLATE_YARD);
		        /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithLoc
		        -- 1후판정정야드재료 정보 조회

		        SELECT
		               A.STL_NO                         AS STL_NO                   -- 재료번호
		             , A.REGISTER                       AS REGISTER                 -- 등록자
		             , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT          -- 등록일시
		             , A.MODIFIER                       AS MODIFIER                 -- 수정자
		             , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT          -- 수정일시
		             , A.DEL_YN                         AS DEL_YN                   -- 삭제유무
		             , A.YD_WBOOK_ID                    AS YD_WBOOK_ID              -- 야드작업예약ID
		             , A.YD_SCH_CD                      AS YD_SCH_CD                -- 야드스케쥴코드
		             , A.PTOP_PLNT_GP                   AS PTOP_PLNT_GP             -- 조업공장구분
		             , A.YD_MTL_ITEM                    AS YD_MTL_ITEM              -- 야드재료품목
		             , A.ITEMNAME_CD                    AS ITEMNAME_CD              -- 품명코드
		             , A.YD_MTL_STAT                    AS YD_MTL_STAT              -- 야드재료상태
		             , A.STL_PROG_CD                    AS STL_PROG_CD              -- 재료진도코드
		             , A.ORD_YEOJAE_GP                  AS ORD_YEOJAE_GP            -- 주문여재구분
		             , A.FRTOMOVE_ORD_DATE              AS FRTOMOVE_ORD_DATE        -- 이송지시일자
		             , A.FRTOMOVE_PLANT_GP              AS FRTOMOVE_PLANT_GP        -- 이송공장구분
		             , A.STL_APPEAR_GP                  AS STL_APPEAR_GP            -- 재료외형구분
		             , A.PLNT_PROC_CD                   AS PLNT_PROC_CD             -- 공장공정코드
		             , A.YD_MTL_T                       AS YD_MTL_T                 -- 야드재료두께
		             , A.YD_MTL_W                       AS YD_MTL_W                 -- 야드재료폭
		             , A.YD_MTL_L                       AS YD_MTL_L                 -- 야드재료길이
		             , A.YD_MTL_WT                      AS YD_MTL_WT                -- 야드재료중량
		             , A.YD_MTL_W_GP                    AS YD_MTL_W_GP              -- 야드재료폭구분
		             , A.YD_MTL_T_GP                    AS YD_MTL_T_GP              -- 야드재료두께구분
		             , A.YD_MTL_L_GP                    AS YD_MTL_L_GP              -- 야드재료길이구분
		             , A.COOL_DONE_GP                   AS COOL_DONE_GP             -- 냉각완료구분
		             , A.REHEAT_SLAB_GP                 AS REHEAT_SLAB_GP           -- 재열재구분
		             , A.TRANS_ORD_DATE                 AS TRANS_ORD_DATE           -- 운송지시일자
		             , A.TRANS_ORD_SEQNO                AS TRANS_ORD_SEQNO          -- 운송지시순번
		             , A.CAR_NO                         AS CAR_NO                   -- 차량번호
		             , A.CARD_NO                        AS CARD_NO                  -- 카드번호
		             , A.ARR_WLOC_CD                    AS ARR_WLOC_CD              -- 착지개소코드
		             , A.YD_FRTOMOVE_YD_GP              AS YD_FRTOMOVE_YD_GP        -- 야드이송야드구분
		             , A.YD_FRTOMOVE_BAY_GP             AS YD_FRTOMOVE_BAY_GP       -- 야드이송동구분
		             , A.URGENT_FRTOMOVE_WORD_GP        AS URGENT_FRTOMOVE_WORD_GP  -- 긴급이송작업지시구분
		             , A.YD_FTMV_MEANS_GP               AS YD_FTMV_MEANS_GP         -- 야드이송수단구분
		             , A.MMATL_FEE_NO                   AS MMATL_FEE_NO             -- 모재료번호
		             , A.YD_WRK_PLAN_CRN                AS YD_WRK_PLAN_CRN          -- 야드작업계획크레인
		             , A.YD_WRK_PLAN_TCAR               AS YD_WRK_PLAN_TCAR         -- 야드작업계획대차
		             , A.YD_CAR_UPP_LOC_CD              AS YD_CAR_UPP_LOC_CD        -- 야드차상위치코드
		             , A.YD_CURR_STR_LOC                AS YD_CURR_STR_LOC          -- 야드현저장위치
		             , A.YD_RCPT_DATE                   AS YD_RCPT_DATE             -- 야드입고일자
		             , A.SNDBK_RSN_CD                   AS SNDBK_RSN_CD             -- 반송원인코드
		             , A.SNDBK_GP                       AS SNDBK_GP                 -- 반송요청구분
		             , A.SNDBK_REGISTER                 AS SNDBK_REGISTER           -- 반송요청자
		             , TO_CHAR(A.SNDBK_REG_DDTT, 'YYYYMMDDHH24MISS') AS SNDBK_REG_DDTT              -- 반송요청일자
		             , A.CAR_LOTID                      AS CAR_LOTID                                -- 차량LotID
		             , TO_CHAR(A.CAR_LOTID_REG_DDTT, 'YYYYMMDDHH24MISS') AS CAR_LOTID_REG_DDTT      -- 차량LotID등록일자
		             , A.DETAIL_ARR_CD                  AS DETAIL_ARR_CD                            -- 상세착지코드
		             , A.YD_FTMV_WRK_CMPL_GP            AS YD_FTMV_WRK_CMPL_GP                      -- 야드이송작업완료구분
		             , TO_CHAR(A.YD_FTMV_WRK_CMPL_DD, 'YYYYMMDDHH24MISS') AS YD_FTMV_WRK_CMPL_DD    -- 야드이송작업완료일자
		             , A.BOOK_OUT_RESN                  AS BOOK_OUT_RESN            -- Book-Out원인
		             , A.BOOK_OUT_DATE                  AS BOOK_OUT_DATE            -- Book-Out일자
		             , A.BOOK_OUT_PROG                  AS BOOK_OUT_PROG            -- Book-Out공정
		             , A.US_MAINTMATL                   AS US_MAINTMATL             -- 상면보수재
		             , A.US_MAINT_SCH_MAKE_YN           AS US_MAINT_SCH_MAKE_YN     -- 상면보수스케줄작성여부
		             , A.US_MAINT_WRK_CMPL_YN           AS US_MAINT_WRK_CMPL_YN     -- 상면보수작업완료여부
		             , A.LS_MAINTMATL                   AS LS_MAINTMATL             -- 하면보수재
		             , A.LS_MAINT_SCH_MAKE_YN           AS LS_MAINT_SCH_MAKE_YN     -- 하면보수스케줄작성여부
		             , A.LS_MAINT_WRK_CMPL_YN           AS LS_MAINT_WRK_CMPL_YN     -- 하면보수작업완료여부
		             , A.CPL_WRK_MTL                    AS CPL_WRK_MTL              -- 냉간교정재
		             , A.CR_CORR_SCH_MAKE_YN            AS CR_CORR_SCH_MAKE_YN      -- 냉간교정스케줄작성여부
		             , A.CR_CORR_WRK_CMPL_YN            AS CR_CORR_WRK_CMPL_YN      -- 냉간교정작업완료여부
		             , A.HTTRT_HPL_MTL                  AS HTTRT_HPL_MTL            -- 열처리교정재
		             , A.HTTRT_CORR_SCH_MAKE_YN         AS HTTRT_CORR_SCH_MAKE_YN   -- 열처리교정스케줄작성여부
		             , A.HTTRT_CORR_WRK_CMPL_YN         AS HTTRT_CORR_WRK_CMPL_YN   -- 열처리교정작업완료여부
		             , A.GAS_WRK_MTL                    AS GAS_WRK_MTL              -- GAS작업재
		             , A.GAS_WRK_SCH_MAKE_YN            AS GAS_WRK_SCH_MAKE_YN      -- Gas작업스케줄작성여부
		             , A.GAS_WRK_WRK_CMPL_YN            AS GAS_WRK_WRK_CMPL_YN      -- Gas작업작업완료여부
		             , A.SHOT_BLST_WRK_MTL              AS SHOT_BLST_WRK_MTL        -- ShortBlast작업재
		             , A.S_BLST_WRK_SCH_MAKE_YN         AS S_BLST_WRK_SCH_MAKE_YN   -- ShortBlast작업스케줄작성여부
		             , A.S_BLST_WRK_WRK_CMPL_YN         AS S_BLST_WRK_WRK_CMPL_YN   -- ShortBlast작업작업완료여부
		             , A.PRESS_WRK_MTL                  AS PRESS_WRK_MTL            -- 프레스교정재
		             , A.PRS_CORR_SCH_MAKE_YN           AS PRS_CORR_SCH_MAKE_YN     -- Press교정스케줄작성여부
		             , A.PRS_CORR_WRK_CMPL_YN           AS PRS_CORR_WRK_CMPL_YN     -- Press교정작업완료여부
		             , A.PL_WR_PRSNT_PROC_CD            AS PL_WR_PRSNT_PROC_CD      -- 후판실적현공정코드
		             -- 저장위치(야드적치단) 정보
		             , B.YD_STK_COL_GP                  AS YD_STK_COL_GP            -- 야드적치열구분
		             , B.YD_STK_BED_NO                  AS YD_STK_BED_NO            -- 야드적치Bed번호
		             , B.YD_STK_LYR_NO                  AS YD_STK_LYR_NO            -- 야드적치단번호
		             , B.YD_STK_LYR_ACT_STAT            AS YD_STK_LYR_ACT_STAT      -- 야드적치단활성상태
		             , B.YD_STK_LYR_MTL_STAT            AS YD_STK_LYR_MTL_STAT      -- 야드적치단재료상태
		             , B.YD_OCPY_BED_GP                 AS YD_OCPY_BED_GP           -- 야드점유구분
		             , B.YD_OCPY_STK_BED_NO             AS YD_OCPY_STK_BED_NO       -- 야드점유적치Bed번호
		             , B.YD_OCPY_STK_LYR_NO             AS YD_OCPY_STK_LYR_NO       -- 야드점유적치단번호
		             -- 보수장 적치필요 베드 정보
		             , NVL(CEIL(A.YD_MTL_W/1000), 1)    AS BS_COL_CNT               -- 재료폭별 적치필요 열수
		             , NVL(CEIL(A.YD_MTL_L/2000), 1)    AS BS_BED_CNT               -- 재료길이별 적치필요 베드수

		             , C.CURR_PROG_CD                   AS CURR_PROG_CD             -- 현재진도코드
		             , C.MTL_STAT_CD                    AS MTL_STAT_CD              -- 재료상태코드
		             , C.PL_MEA_GDS_T                   AS PL_MEA_GDS_T             -- 제촌두께
		             , C.PL_MEA_GDS_W                   AS PL_MEA_GDS_W             -- 제촌폭
		             , C.PL_MEA_GDS_L                   AS PL_MEA_GDS_L             -- 제촌길이
		             , C.PL_MEA_GDS_WT                  AS PL_MEA_GDS_WT            -- 제촌중량

		          FROM TB_YD_SHRSTOCK  A
		             , (SELECT
		                       X.YD_STK_COL_GP
		                     , X.YD_STK_BED_NO
		                     , X.YD_STK_LYR_NO
		                     , X.STL_NO
		                     , X.YD_STK_LYR_ACT_STAT
		                     , X.YD_STK_LYR_MTL_STAT
		                     , X.YD_OCPY_BED_GP
		                     , X.YD_OCPY_STK_BED_NO
		                     , X.YD_OCPY_STK_LYR_NO
		                     , DECODE(X.YD_STK_LYR_MTL_STAT, 'U','C', X.YD_STK_LYR_MTL_STAT) AS MTL_STAT
		                  FROM TB_YD_STKLYR X
		                 WHERE X.STL_NO = :V_STL_NO
		                   AND X.YD_STK_COL_GP LIKE NVL(:V_YD_GP, 'F') || '%'
		                   AND X.DEL_YN = 'N'
		                   AND X.YD_STK_LYR_MTL_STAT IN ('C','U')
		               ) B
		             , VW_YD_SHRSTOCK C
		         WHERE A.STL_NO      = :V_STL_NO
		           AND A.STL_NO      = B.STL_NO(+)
		           AND A.STL_NO      = C.STL_NO(+)
		           AND A.DEL_YN      = 'N'
		           AND B.MTL_STAT(+) = 'C'
		           AND ROWNUM        =  1
		           */
		        intRtnVal  = ydStockDao.getYdStockWithLoc(setRecord, rsResult);
		        if (intRtnVal > 0) {
		        	rsResult.first();
		        	recOutTemp = JDTORecordFactory.getInstance().create();
		        	recOutTemp.setRecord(rsResult.getRecord());

			        szYdDnWrLoc   = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO");
			        szYdDnWrLayer = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO");
		        }
	        }

            //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 시작";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      	szCrnSchId);
	        setRecord.setField("YD_DN_WR_LOC",       	szYdDnWrLoc);
	        setRecord.setField("YD_DN_WR_LAYER",     	szYdDnWrLayer);
	        setRecord.setField("YD_DN_WRK_ACT_GP",   	getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP"));
	        setRecord.setField("YD_DN_WR_XAXIS",     	getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
	        setRecord.setField("YD_DN_WR_YAXIS",     	getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
	        setRecord.setField("YD_DN_WR_ZAXIS",     	getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
	        setRecord.setField("YD_WRK_PROG_STAT",   	getCrnschRecord.getFieldString("YD_WRK_PROG_STAT"));
	        setRecord.setField("YD_DN_CMPL_DT",      	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
	        setRecord.setField("YD_GP",   				getCrnschRecord.getFieldString("YD_GP"));
	        setRecord.setField("MODIFIER", 				szModifier);

	        //--------------------------------------------------------------------------------------------------
	        //	크레인스케줄 등록 시 야드작업계상일자에 등록되는 값을 권하완료시점에 다시 수정, 권하완료 시의 값이 사용됨
	        //	TO_CHAR(SYSDATE - (7 / 24), 'YYYYMMDD')
	        //--------------------------------------------------------------------------------------------------
	        String szYdWrkHdsDd = JPlateYdUtils.getDefaultHdsDate();
	        setRecord.setField("YD_WRK_HDS_DD",  		szYdWrkHdsDd);

	        szMsg = "["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]의 야드작업계상일자["+szYdWrkHdsDd+"]";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnDnWr 

            UPDATE TB_YD_CRNSCH
               SET
                   MODIFIER         = :V_MODIFIER
                 , MOD_DDTT         = SYSDATE
                 , YD_WRK_PROG_STAT = NVL(:V_YD_WRK_PROG_STAT, YD_WRK_PROG_STAT)    -- NULL일때 전 상태값 유지
                 , YD_DN_WR_LOC     = :V_YD_DN_WR_LOC
                 , YD_DN_WR_LAYER   = :V_YD_DN_WR_LAYER
                 , YD_DN_WRK_ACT_GP = :V_YD_DN_WRK_ACT_GP
                 , YD_DN_WR_XAXIS   = :V_YD_DN_WR_XAXIS
                 , YD_DN_WR_YAXIS   = :V_YD_DN_WR_YAXIS
                 , YD_DN_WR_ZAXIS   = :V_YD_DN_WR_ZAXIS
                 , YD_DN_CMPL_DT    = TO_DATE(:V_YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS')
                 , YD_WRK_HDS_DD    = :V_YD_WRK_HDS_DD
             WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
            */ 
	        intRtnVal = ydCrnschDao.updCrnDnWr(setRecord);

	        szMsg = "["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 완료";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
            szMsg = "["+szOperationName+"] 권하실적위치의 설비구분 : " + ydUtils.substr(szYdDnWrLoc, 2, 2);
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
            szMsg = "["+szOperationName+"] 권하실적 스케줄코드 : " + szYdSchCd;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 대차일때 대차 스케줄 이송재료 등록 후 대차스케줄 호출
	        //-------------------------------------------------------------------------------------------------------------------
	        if ("TC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) && "TC".equals(ydUtils.substr(szYdSchCd, 2, 2))) {

	            szMsg    = "["+szOperationName+"] 권하실적위치가 대차";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	//-------------------------------------------------------------------------------------------------------------------
	            //	권하실적위치로 대차설비ID 조합 생성
	            //-------------------------------------------------------------------------------------------------------------------
	        	szTcarEqpId = szYdDnWrLoc.substring(0,1) + "XTC" + szYdDnWrLoc.substring(4,6);

	            //-----------------------------------------------------
                //	대차작업지정기준을 BRE Rule에서 조회
                //-----------------------------------------------------
                String[] szTCarRule	= JPlateYdCommonUtils.getTCarWrkStdRule(szTcarEqpId);
                //-----------------------------------------------------
                recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_WBOOK_ID", 			szYdWbookId);	//작업예약ID
	        	recInPara.setField("YD_WRK_PLAN_TCAR",		szTcarEqpId);	//작업계획대차

                if ("Y".equals(szTCarRule[0])) {

                	if ("D".equals(szTCarRule[1])) {	//대차 직상차
                		//기준상차동과 실제작업한 상차동이 같을경우
                		if (szTCarRule[2].equals(szYdDnWrLoc.substring(1, 2))) {
                			//-----------------------------------------------------
            	        	//	대차작업지정기준이 직상차인 경우에는 하차동을 목표동으로 재설정
            	        	//-----------------------------------------------------
            	        	recInPara.setField("YD_AIM_BAY_GP",		szTCarRule[3]);		//목표동
            	        }
	                }
		        }
                /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbook 
                -- 작업예약 조회 [조건:작업예약ID]

                SELECT A.YD_WBOOK_ID            AS YD_WBOOK_ID
                     , A.REGISTER               AS REGISTER
                     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
                     , A.MODIFIER               AS MODIFIER
                     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
                     , A.DEL_YN                 AS DEL_YN
                     , A.YD_GP                  AS YD_GP
                     , A.YD_BAY_GP              AS YD_BAY_GP
                     , A.YD_SCH_CD              AS YD_SCH_CD
                     , A.YD_SCH_PRIOR           AS YD_SCH_PRIOR
                     , A.YD_SCH_PROG_STAT       AS YD_SCH_PROG_STAT
                     , A.YD_SCH_ST_GP           AS YD_SCH_ST_GP
                     , A.YD_SCH_REQ_GP          AS YD_SCH_REQ_GP
                     , A.YD_AIM_YD_GP           AS YD_AIM_YD_GP
                     , A.YD_AIM_BAY_GP          AS YD_AIM_BAY_GP
                     , A.YD_CTS_RELAY_YN        AS YD_CTS_RELAY_YN
                     , A.YD_CTS_RELAY_BAY_GP    AS YD_CTS_RELAY_BAY_GP
                     , A.YD_TO_LOC_DCSN_MTD     AS YD_TO_LOC_DCSN_MTD
                     , A.YD_TO_LOC_GUIDE        AS YD_TO_LOC_GUIDE
                     , A.YD_WRK_PLAN_TCAR       AS YD_WRK_PLAN_TCAR
                     , A.YD_CAR_USE_GP          AS YD_CAR_USE_GP
                     , A.TRN_EQP_CD             AS TRN_EQP_CD
                     , A.CAR_NO                 AS CAR_NO
                     , A.CARD_NO                AS CARD_NO
                     , (SELECT COUNT(1)
                          FROM TB_YD_CRNSCH B
                         WHERE B.DEL_YN = 'N'
                           AND B.YD_SCH_CD = A.YD_SCH_CD
                       )                        AS REMAIN_SCH_CNT
                     , (SELECT MAX(B.YD_STK_COL_GP) FROM TB_YD_WRKBOOKMTL B
                         WHERE B.YD_WBOOK_ID = A.YD_WBOOK_ID
                       )                        AS YD_STK_COL_GP

                  FROM TB_YD_WRKBOOK A
                 WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID
                 */
                intRtnVal = ydWrkbookDao.updYdWrkbook(recInPara);
	        	if (intRtnVal <= 0) {
		            szRtnMsg = "대차 작업예약 수정시 오류 :: " + Integer.toString(intRtnVal);
		            szMsg    = "["+szOperationName+"] " + szRtnMsg;
	                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	                throw new DAOException(szRtnMsg);
	    		}
	        	szMsg = "["+szOperationName+"] [대차 직상차]상차작업예약ID["+szYdWbookId+"]에 대차설비ID["+szTcarEqpId+"]  목표동["+szTCarRule[3]+"] 업데이트 완료";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	//-------------------------------------------------------------------------------------------------------------------
	        	//	권하실적위치로 만들어진 대차설비ID를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 시작
	        	//-------------------------------------------------------------------------------------------------------------------
	        	szMsg = "["+szOperationName+"] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 시작";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_EQP_ID", szTcarEqpId);

	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
	        	intRtnVal = ydTcarSchDao.getByYdEqpId(recInPara, rsResult);
	        	if (intRtnVal <= 0) {
			        szRtnMsg = "대차 상차시 설비ID[" + szTcarEqpId + "]로 스케줄 조회 오류발생 - 반환값 : " + Integer.toString(intRtnVal);
		            szMsg    = "["+szOperationName+"] " + szRtnMsg;
	                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	                throw new DAOException(szRtnMsg);
	        	}

	        	rsResult.absolute(1);
	        	recOutTemp = JDTORecordFactory.getInstance().create();
	        	recOutTemp.setRecord(rsResult.getRecord());

	        	szMsg = "["+szOperationName+"] [대차 상차]권하실적위치로 만들어진 대차설비ID[" + szTcarEqpId + "]를 사용해서 상차작업예약ID를 업데이트 하기 위해서 조회 완료 - 대상재건수 : " + Integer.toString(rsResult.size());
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

                szYdTcarSchId = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");

                //-------------------------------------------------------------------------------------------------------------------
	        	//	조회된 대차 스케줄에 상차작업예약id를 등록한다.
                //-------------------------------------------------------------------------------------------------------------------
                szMsg = "["+szOperationName+"] [대차 상차]대차스케줄[" + szYdTcarSchId + "]에 상차작업예약ID["+szYdWbookId+"]를 업데이트 시작";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	recInPara = JDTORecordFactory.getInstance().create();
	        	recInPara.setField("YD_TCAR_SCH_ID", 		szYdTcarSchId);
	        	recInPara.setField("YD_CARLD_WRK_BOOK_ID", 	szYdWbookId);
	        	recInPara.setField("YD_EQP_WRK_STAT", 		"L");
	        	recInPara.setField("YD_CAR_PROG_STAT", 		"4");//상차개시
	        	recInPara.setField("YD_CARLD_STOP_LOC", 	szYdDnWrLoc.substring(0,6));
	        	recInPara.setField("MODIFIER", 				szModifier);
	        	
	        	/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdTcarSchDAO.updYdCarLdUdInfo 

	        	UPDATE TB_YD_TCARSCH
	        	   SET MODIFIER             = NVL(:V_MODIFIER, MODIFIER)
	        	     , MOD_DDTT             = SYSDATE
	        	     , YD_CARLD_SCH_REQ_GP  = :V_YD_CARLD_SCH_REQ_GP
	        	     , YD_CARUD_SCH_REQ_GP  = :V_YD_CARUD_SCH_REQ_GP
	        	     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
	        	     , YD_CARLD_STOP_LOC    = :V_YD_CARLD_STOP_LOC
	        	     , YD_CARUD_STOP_LOC    = :V_YD_CARUD_STOP_LOC
	        	     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
	        	 WHERE YD_TCAR_SCH_ID       = :V_YD_TCAR_SCH_ID
	        	*/ 
	        	intRtnVal = ydTcarSchDao.updYdCarLdUdInfo(recInPara);
	        	if (intRtnVal <= 0) {
		            szRtnMsg = "대차 상차 스케줄[" + szYdTcarSchId + "] 작업예약["+szYdWbookId+"] 등록 오류발생 :: " + Integer.toString(intRtnVal);
		            szMsg    = "["+szOperationName+"] " + szRtnMsg;
	                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	                throw new DAOException(szRtnMsg);
	        	}

	        	szMsg = "["+szOperationName+"] [대차 상차]대차스케줄[" + szYdTcarSchId + "]에 상차작업예약ID["+szYdWbookId+"]를 업데이트 완료 - 반환값 : " + Integer.toString(intRtnVal);
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        	//-------------------------------------------------------------------------------------------------------------------
    		    //	권하실적위치가 대차이므로 대차이송재료 등록
                //  대차이송재료 등록 후 대차스케줄에 상차완료시간 및  대차설비상태 영차로 등록
                //  대차스케줄의 대차상태를 영차'L'로 업데이트
    	        //-------------------------------------------------------------------------------------------------------------------
            	szMsg = "["+szOperationName+"] 권하실적위치가 대차이므로 대차이송재료 등록 시작";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
                
            	this.setYdTcarY7(getRecSet,szYdTcarSchId);

            	//-------------------------------------------------------------------------------------------------------------------
    		    //	권하실적위치가 대차이므로 대차스케줄 호출
    	        //-------------------------------------------------------------------------------------------------------------------
            	szMsg = "["+szOperationName+"] 권하실적위치가 대차이므로 대차스케줄 호출 시작 - 대차설비ID["+szTcarEqpId+"]";
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

                recSendMsg = JDTORecordFactory.getInstance().create();
            	recSendMsg.setField("MSG_ID", 			"YDYDJ");					// YDYDJ520
            	recSendMsg.setField("YD_LD_UD_GP", 		"L");
            	recSendMsg.setField("YD_WBOOK_ID", 		szYdWbookId);
            	recSendMsg.setField("YD_EQP_ID", 		szTcarEqpId);
            	recSendMsg.setField("MODIFIER", 		szModifier);
            	recSendMsg.setField("YD_CRN_SCH_ID", 	szCrnSchId);	// 크레인작업지시ID 추가

    			// 권하처리 요청
    			szCallMsg = (String)ydEjbCon.trx("JPlateYdTcarSchSeEJB", "procY7TcarSch", recSendMsg);

    			szMsg = "["+szOperationName+"] 권하실적위치가 대차이므로 대차스케줄 호출 완료 >>>> " + szCallMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
            }

            if ("".equals(ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID"))) {
	            szRtnMsg = "작업예약 ID가 없습니다.";
	            szMsg    = "["+szOperationName+"] " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                throw new DAOException(szRtnMsg);
	        }

            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업 및 재료, 작업예약 및 재료정보를 Clear
	        //-------------------------------------------------------------------------------------------------------------------
            szMsg    = "["+szOperationName+"] 크레인 작업재료 삭제처리 ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        //크레인 작업재료 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("MODIFIER",           szModifier);
	        intRtnVal = ydCrnWrkMtlDao.delYdCrnWrkMtl(setRecord);
	        if (intRtnVal <= 0) {
	            szRtnMsg = "크레인 작업재료 삭제중 오류 발생!! Code : " + Integer.toString(intRtnVal);
	            szMsg    = "["+szOperationName+"] " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                throw new DAOException(szRtnMsg);
	        }

            szMsg    = "["+szOperationName+"] 크레인스케줄처리 ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        //크레인스케줄 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      getCrnschRecord.getFieldString("YD_CRN_SCH_ID"));
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("YD_DN_CMPL_DT",      JPlateYdUtils.getCurDate("yyyyMMddHHmmss")); //권하완료일시
	        setRecord.setField("MODIFIER",           szModifier);
	        intRtnVal = ydCrnschDao.delYdCrnSch(setRecord);
	        if (intRtnVal < 0) {
	            szRtnMsg = "크레인스케줄 삭제시 오류 발생!!!, ErrorCode:" + Integer.toString(intRtnVal);
	            szMsg    = "["+szOperationName+"] " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                throw new DAOException(szRtnMsg);
	        }

            szMsg    = "["+szOperationName+"] 작업예약완료 CHECK ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        getRecord = JDTORecordFactory.getInstance().create();
	        getRecord.setField("YD_WBOOK_ID", szYdWbookId);

			intRtnVal = this.getYdCrnSchByWrkIdY7(getRecord, getRecSet);
			if (intRtnVal < 0) {
	            szRtnMsg = "작업예약완료 CHECK 에러발생!!!, ErrorCode:" + Integer.toString(intRtnVal);
	            szMsg    = "["+szOperationName+"] " + szRtnMsg;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                throw new DAOException(szRtnMsg);
	        }

	        // 작업예약에 존재하는 크레인스케쥴 정보가 없을경우에 작업예약정보를 삭제한다.
	        if (getRecSet.size() == 0) {

	            szMsg    = "["+szOperationName+"] 작업예약정보를 삭제 ---- START";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId);
	            bookrecord.setField("DEL_YN",             "Y");
	            bookrecord.setField("YD_SCH_PROG_STAT",   "E");
	            bookrecord.setField("MODIFIER",           szModifier);

                intRtnVal = ydWrkbookDao.delYdWrkbook(bookrecord);
	        	if (intRtnVal <= 0) {
		            szRtnMsg = "작업예약정보를 삭제시 오류 발생 !!!, ErrorCode:" + Integer.toString(intRtnVal);
		            szMsg    = "["+szOperationName+"] " + szRtnMsg;
	                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	                throw new DAOException(szRtnMsg);
	    		}

		        // 작업예약재료조회
	            getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	            
	            /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getYdWrkbookMtlId 

	            SELECT YD_WBOOK_ID  AS YD_WBOOK_ID
	                  ,STL_NO  AS STL_NO
	                  ,REGISTER  AS REGISTER
	                  ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	                  ,MODIFIER  AS MODIFIER
	                  ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	                  ,DEL_YN  AS DEL_YN
	                  ,YD_STK_COL_GP  AS YD_STK_COL_GP
	                  ,YD_STK_BED_NO  AS YD_STK_BED_NO
	                  ,YD_STK_LYR_NO  AS YD_STK_LYR_NO
	                  ,YD_UP_COLL_SEQ  AS YD_UP_COLL_SEQ
	               FROM TB_YD_WRKBOOKMTL
	             WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
	               AND DEL_YN='N'
	            */	   
	            intRtnVal = ydWrkbookMtlDao.getYdWrkbookMtlId(bookrecord, getRecSet);

	            // 조회한 작업예약재료1매씩 저장품 업데이트
				for(int ii=1; ii<=getRecSet.size(); ii++) {
					getRecSet.absolute(ii);
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setRecord(getRecSet.getRecord());
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", 		ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
					recInTemp.setField("MODIFIER",    	szModifier);
					recInTemp.setField("YD_SCH_CD", 	"");
					recInTemp.setField("YD_WBOOK_ID", 	"");
					recInTemp.setField("YD_STK_COL_GP", szYdDnWrLoc.substring(0,6));
					recInTemp.setField("YD_STK_BED_NO", szYdDnWrLoc.substring(6,8));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYdDnWrLayer, ii-1));
					intRtnVal = ydStockDao.updYdStkColInfo(recInTemp);
				}

				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("STL_NO",      ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
				recInTemp.setField("DEL_YN",      "Y");
				recInTemp.setField("MODIFIER",    szModifier);
				recInTemp.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID"));
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookMtlDelYn(recInTemp);

				szNextSchCallYn = "Y";	// 다음 스케줄 호출 Flag Set

				szMsg    = "["+szOperationName+"] 작업 예약 처리 완료";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	        } else {
	        	szMsg    = "["+szOperationName+"] 작업 예약 진행 중";
	            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	        }

			//------------------------------------------------------------------
			// FROM 위치가 대차일 경우 YD_WRK_PLAN_TCAR(야드작업계획대차) 항목  CLEAR 처리
			//------------------------------------------------------------------
			if ("TC".equals(ydUtils.substr(szYdUpWrLoc, 2, 2)) && !"TC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {
				for(int ii=0; ii<arrStlNo.length; ii++) {
					if (!"".equals(arrStlNo[ii])) {
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("STL_NO",         	arrStlNo[ii]);
						recInTemp.setField("YD_WRK_PLAN_TCAR", 	"");
						recInTemp.setField("REGISTER",       	szModifier);
						recInTemp.setField("MODIFIER",       	szModifier);
						/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdWrkPlanTcar 
						-- 야드작업계획대차 UPDATE (대차하차위치)

						UPDATE TB_YD_SHRSTOCK
						   SET MODIFIER         = :V_MODIFIER
						     , MOD_DDTT         = SYSDATE
						     , YD_WRK_PLAN_TCAR = SUBSTR(:V_YD_WRK_PLAN_TCAR, 1, 6)
						 WHERE STL_NO           = :V_STL_NO
						*/ 
			    		intRtnVal = ydStockDao.updYdWrkPlanTcar(recInTemp);
			    		if (intRtnVal < 1) {
							szMsg = "야드작업계획대차 항목 CLEAR 실패 >>>> " + Integer.toString(intRtnVal);
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			    		}
					}
				}
			}

	        //-------------------------------------------------------------------------------------------------------------------
		    //	인터페스 송신처리
	        //  각각의 I/F 송신조건은 다시 체크 요망.
	        //-------------------------------------------------------------------------------------------------------------------
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 1후판조업 저장위치변경정보 전송  - YDPRJ011
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            szMsg    = "["+szOperationName+"] 1후판조업 저장위치변경정보 전송 ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("MSG_ID", 			"YDPRJ011");
	        recInTemp.setField("YD_STK_COL_FR", 	ydUtils.substr(szYdUpWrLoc,0,6));		// From적치열
	        recInTemp.setField("YD_STK_BED_FR", 	ydUtils.substr(szYdUpWrLoc,6,2));		// From적치BED
	        recInTemp.setField("YD_STK_COL_TO", 	ydUtils.substr(szYdDnWrLoc,0,6));		// TO적치열
	        recInTemp.setField("YD_STK_BED_TO", 	ydUtils.substr(szYdDnWrLoc,6,2));		// TO적치BED
	        recInTemp.setField("YD_EQP_WRK_SH", 	"");									// 야드설비작업매수
	        recInTemp.setField("ARR_STL_NO", 		szArrStlNo);

	        szSendMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recInTemp);

			szMsg = "["+szOperationName+"] 후판조업 저장위치변경정보 전송 완료>>>>" + szSendMsg;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			1후판정정야드L2 크레인작업실적응답 전송  - YDY2L005
	         * 업무기준 Desc : 크레인 권하실적처리 성공 후 크레인작업실적응답 전송
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            szMsg    = "["+szOperationName+"] 크레인작업실적응답 전송 ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("MSG_ID", 			"YDY2L005");
	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId);										//야드설비ID
	        recInTemp.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_CMPL);				//야드작업진행상태
	        recInTemp.setField("YD_SCH_CD", 		szYdSchCd);										//야드스케줄코드
	        recInTemp.setField("YD_CRN_SCH_ID", 	szCrnSchId);									//야드크레인스케줄ID
        	recInTemp.setField("YD_L2_WR_GP", 		JPlateYdConst.CRN_WRK_RE_DN_WR);				//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        recInTemp.setField("YD_L3_HD_RS_CD", 	JPlateYdConst.CRN_WRK_RE_CD_NORMAL_HD);			//야드L3처리결과코드
	        szSendMsg = ydDelegate.sendMsg(recInTemp);

			szMsg = "["+szOperationName+"] 1후판정정야드L2 크레인작업실적응답[YDY2L005] 전송 완료>>>>" + szSendMsg;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 1후판정정야드L2 저장품제원 전송  - YDY2L002
	         * RT - BOOK-IN 권하 완료후
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			if ("RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

				this.procRtBookInY2(msgRecord, arrStlNo);
				
			}
            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인 설비상태 권하완료(4) 셋팅.
	        //-------------------------------------------------------------------------------------------------------------------
            szMsg    = "["+szOperationName+"] 설비상태 권하완료 변경 ---- START";
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

            recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId);
	        recInTemp.setField("YD_EQP_STAT", 		"4");
	        recInTemp.setField("MODIFIER", 			szModifier);
            intRtnVal = ydEqpDao.updYdEqpStat(recInTemp);

            if (intRtnVal <= 0) {
	            szRtnMsg = "설비상태 UPDATE 처리시 오류 발생 :: " + Integer.toString(intRtnVal);
	            szMsg    = "["+szOperationName+"] " + szRtnMsg;
			 	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			 	throw new DAOException(szRtnMsg);
	   		}

	        //설비id로 설비Table조회
	        recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYdEqpId);
	        rsResult  = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
	        intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult);

			szMsg = "["+szOperationName+"] 이력테이블등록호출 ---- START";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 1후판 이력테이블등록호출
	        JPlateYdCrnSchYdPSeEJBBean crnSchYdPSeEJBBean = new JPlateYdCrnSchYdPSeEJBBean();

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",             "");
			recInTemp.setField("YD_WBOOK_ID",        szYdWbookId);
			recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId);
			recInTemp.setField("YD_CAR_SCH_ID",      "");					// szYdCarSchId
			recInTemp.setField("YD_TCAR_SCH_ID",     szYdTcarSchId);
			recInTemp.setField("YD_WTCL_TNK_SCH_ID", "");
			recInTemp.setField("REGISTER",           szModifier);
			recInTemp.setField("MODIFIER",           szModifier);
			recInTemp.setField("DEL_YN",             "N");
			crnSchYdPSeEJBBean.procWorkHistoryCreate(recInTemp);

			szMsg = "["+szOperationName+"] 이력테이블등록호출 END";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord(0));

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",           	"YDYDJX55");			// YDYDJ755 :: 크레인 작업지시요구
			recInTemp.setField("YD_EQP_ID",        	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID"));
			recInTemp.setField("YD_EQP_WRK_MODE",  	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE"));
			recInTemp.setField("YD_WRK_PROG_STAT", 	"4");
			recInTemp.setField("YD_SCH_CD",        	szYdSchCd);
			recInTemp.setField("YD_CRN_SCH_ID",    	"");
			recInTemp.setField("YD_CRN_XAXIS",     	"");
			recInTemp.setField("YD_CRN_YAXIS",     	"");
			recInTemp.setField("RTN_CD",     		JPlateYdConst.RETN_CD_SUCCESS);

	        //-------------------------------------------------------------------------------------------------------------------
	        // 해당 스케줄코드로 다음 스케줄기동
	        //-------------------------------------------------------------------------------------------------------------------
			if ("Y".equals(szNextSchCallYn)) {
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_EQP_ID",     szYdEqpId);				// 설비ID
				recInTemp.setField("YD_SCH_CD",     szYdSchCd);				// 스케줄코드
				recInTemp.setField("REGISTER",      szModifier);
				recInTemp.setField("MODIFIER",      szModifier);
				recInTemp.setField("YD_UP_WR_LOC",	szYdUpWrLoc);			// 권상위치

				this.procNextYdCrnSchY2(recInTemp);
			}

        } catch(Exception e) {
        	/*
        	 * Exception 발생시에도 작업실적 응답은 송신.
        	 */
        	if ("".equals(szRtnMsg) || JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        		szRtnMsg = e.getMessage();
        	}
        	if (szRtnMsg != null && szRtnMsg.length() > 100) {
        		szRtnMsg = ydUtils.substr(szRtnMsg, 0, 100);
        	}
			szMsg = "["+szOperationName+"] 권하실적처리시 Exception 발생 >>>>" + e.getMessage();
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("MSG_ID", 			"YDY2L005");
	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId);								//야드설비ID
	        recInTemp.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_CMPL);		//야드작업진행상태
	        recInTemp.setField("YD_SCH_CD", 		szYdSchCd);								//야드스케줄코드
	        recInTemp.setField("YD_CRN_SCH_ID", 	szCrnSchId);							//야드크레인스케줄ID
        	recInTemp.setField("YD_L2_WR_GP", 		JPlateYdConst.CRN_WRK_RE_DN_WR);		//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        recInTemp.setField("YD_L3_HD_RS_CD", 	JPlateYdConst.CRN_WRK_RE_CD_ERROR);		//야드L3처리결과코드
	        recInTemp.setField("YD_L3_MSG",			szRtnMsg);
			String sendMsg = ydDelegate.sendMsg(recInTemp);

			szMsg = "["+szOperationName+"] 1후판정정야드L2 크레인작업실적응답[YDY2L005] 전송 완료 >>>>"+sendMsg;
            ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

        	return szRtnMsg;
        }

	    return JPlateYdConst.RETN_CD_SUCCESS;
    } // end of procY2CrnDnWr()

	
	/**
     * 오퍼레이션명 : 1후판정정 강제권하요구처리 (Y2YDL011)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return JDTORecord
     * @throws DAOException
	 */
	public JDTORecord procY2OffCrnDnWr(JDTORecord msgRecord) throws DAOException, JDTOException {

		JDTORecord setRecord 	= JDTORecordFactory.getInstance().create();
        String 	szMsg         	= "";
        String 	szMethodName  	= "procY2OffCrnDnWr";
    	String 	szOperationName	= "1후판정정 강제권하요구처리";

		int		oUpCnt			= 0;
    	int 	intRtnVal 		= 0 ;

        // 레코드 선언
		JDTORecord    recPara   = null;
    	JDTORecord    recInPara	= null;
		JDTORecordSet rsResult  = null;

		JPlateYdCrnSchDAO   ydCrnSchDao = new JPlateYdCrnSchDAO();		// 크레인스케쥴DAO
		JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();		// 적치단DAO

		try {
			szMsg = "1후판정정 강제권하요구처리 (Y2YDL011) (" + szMethodName + ") 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//수신 항목 값
			String 	sMsgId  	= ydUtils.getTcCode(msgRecord);
			String 	ydEqpId    	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 		//야드설비ID
			String 	ydSchCd		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");		//야드스케쥴코드
			String 	ydCrnSchId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");	//야드크레인스케쥴ID
			String 	ydDnWrLoc  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC"); 	//야드권하실적위치
			String 	ydCrnXaxis 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS"); 	//야드크레인X축
			String 	ydCrnYaxis 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS"); 	//야드크레인Y축
			String 	ydDnSpanGp	= ydUtils.substr(ydDnWrLoc, 2, 2);							//강제권하 위치의 설비구분
			String 	ydDnColGp	= ydUtils.substr(ydDnWrLoc, 0, 6);							//강제권하 위치의 적치열구분
			String 	modifier   	= ydDaoUtils.paraRecModifier(msgRecord); 					//수정자(Backup Only)
			if ("".equals(modifier)) {
				modifier = sMsgId;
			}

			//크레인작업실적응답 전문 생성용
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE

			setRecord.setField("YD_EQP_ID", 		ydEqpId); 						 	//야드설비ID
			setRecord.setField("YD_L2_WR_GP", 		"J"); 						 		//야드L2실적구분(지시요구)
			setRecord.setField("YD_L3_HD_RS_CD", 	"FU99"); 						 	//야드L3처리결과코드(Error)
			setRecord.setField("YD_L3_MSG", 		"오류:강제권하요구 예상치 못한 오류"); 		//야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "FU01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "FU02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if (ydDnWrLoc.length() < 8) {
				ydL3HdRsCd = "FU04";
				ydL3Msg    = "오류:권하위치 이상";
			} else if (!ydEqpId.substring(0, 2).equals(ydDnWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "FU05";
				ydL3Msg    = "오류:설비-권하위치[" + ydEqpId.substring(0, 2) + "-" + ydDnWrLoc.substring(0, 2) + "] 부적합";
			} else if ("".equals(ydCrnXaxis)) {
				ydL3HdRsCd = "FU06";
				ydL3Msg    = "오류:크레인X축 없음";
			} else if ("".equals(ydCrnYaxis)) {
				ydL3HdRsCd = "FU07";
				ydL3Msg    = "오류:크레인Y축 없음";
			} else if ("".equals(ydSchCd)) {
				ydL3HdRsCd = "FU08";
				ydL3Msg    = "오류:야드스케쥴코드";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "FU09";
				ydL3Msg    = "오류:야드크레인스케쥴ID";
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD",	ydL3HdRsCd); 	//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 		//야드L3MESSAGE
				setRecord.setField("RTN_CD",			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//2019.01.11 강제권하 위치가 PC0281 인 경우 
			//  1) PC0281에 걸린 크레인작업지시를 모두 취소(대기,선택 까지임 권상된 상태는 취소 불가)
			//     - 크레인작업지시 삭제
			//     - 작업예약 삭제
			//     - 크레인작업지시 취소전문 전송
			//  2) PC0281에 걸린 작업예약 삭제 처리       
			if("PC0281".equals(ydDnColGp)) {
				
				String sNEW_MODULE_EFF_YN = "N";
				
				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A016"); //1후판정정야드 강제권하시 PC0281열 크레인스케줄,작업예약 삭제처리 여부
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 강제권하(Y2YDL011)시 PC0281열 크레인스케줄,작업예약 삭제처리 여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
				
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
					
					EJBConnector 		ejbConn 	= null;
					
					String szYdCrnSchId;
					String szYdSchCd;
					String szStlNo;
					String szC_YD_WRK_PROG_STAT;
					String szYD_WBOOK_ID;
					String szYD_SCH_CD;
					String szYD_EQP_ID;
					
					JDTORecord recCheck = null;
					JDTORecord outRecord1 = null;
					
					String 	sRTN_CD					= "";
					String 	sRTN_MSG				= "";
					
					
					//해당 열에 잡힌 크레인 작업지시를 조회하여 작업취소 
					JDTORecord recPara2		= JDTORecordFactory.getInstance().create();
					String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
					JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();
					
					recPara2.setField("YD_STK_COL_GP"	, ydDnColGp);    
					
					JDTORecordSet getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdCrnSchIdByLoc", logId, szMethodName, "해당 열에 잡힌 크레인 작업지시 조회");
					
					if (getRecSet.size() > 0) {
						
						//크레인 작업지시 취소 처리
						for (int ii=0; ii<getRecSet.size(); ii++) {
							
							szYdCrnSchId  	= getRecSet.getRecord(ii).getFieldString("YD_CRN_SCH_ID");
							szYdSchCd 		= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD"); 
							szStlNo			= getRecSet.getRecord(ii).getFieldString("STL_NO"); 
							
							if ("".equals(szYdCrnSchId)) {
								//szMsg = "["+szOperationName+"] 스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
								//ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
								continue;
							}		
							
							recPara2.setField("YD_CRN_SCH_ID", szYdCrnSchId);
							recPara2.setField("YD_SCH_CD",     szYdSchCd);
							recPara2.setField("DEL_YN",        "N");
							recPara2.setField("MODIFIER",      modifier);
							
							/*
							 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
							 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
							 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영
							 */
							rsResult = JDTORecordFactory.getInstance().createRecordSet("temRs");

							intRtnVal = ydCrnSchDao.getCheckYdCrnSchId(recPara2, rsResult);		// intGp == 36

							if (intRtnVal < 1) {
								szMsg = "["+szOperationName+"] 취소 작업을 완료 하였습니다.";
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
								continue;
							}

							rsResult.first();
							recCheck = rsResult.getRecord();

							szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT");

							//2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
							if ("2".equals(szC_YD_WRK_PROG_STAT) || "3".equals(szC_YD_WRK_PROG_STAT) || "4".equals(szC_YD_WRK_PROG_STAT)) {
								szMsg = "["+szOperationName+"] 크레인 작업이 완료되지 않았습니다!!";
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
								continue;
							}

							/*
							 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
							 */
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
							recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
							recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));
							recPara.setField("MODIFIER",      modifier);

							//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
							//스케줄취소
							szMsg = "["+szOperationName+"] 작업지시 취소 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
							outRecord1 	= (JDTORecord)ejbConn.trx("cancelJPlateYdCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

							szMsg = "["+szOperationName+"] ---- 작업지시 취소 종료!! >>>> " + sRTN_CD + " , " + sRTN_MSG;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							
							if ("0".equals(sRTN_CD)) {
								m_ctx.setRollbackOnly();
								setRecord.setField("YD_L3_HD_RS_CD", 	"FU00"); 	//야드L3처리결과코드
								setRecord.setField("YD_L3_MSG", 		sRTN_MSG); 		//야드L3MESSAGE
								setRecord.setField("RTN_CD", 			JPlateYdConst.RETN_CD_FAILURE);
								return setRecord;
							}

							szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
							outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							
							szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

							if ("0".equals(sRTN_CD)) {
								m_ctx.setRollbackOnly();
								setRecord.setField("YD_L3_HD_RS_CD", 	"FU00"); 	//야드L3처리결과코드
								setRecord.setField("YD_L3_MSG", 		sRTN_MSG); 		//야드L3MESSAGE
								setRecord.setField("RTN_CD", 			JPlateYdConst.RETN_CD_FAILURE);
								return setRecord;
							}

							// F?RT??LM :: RT BOOK-OUT일때 재료정보 , 적치위치 Clear
							if ("RT".equals(ydUtils.substr(szYdSchCd,2,2)) && "LM".equals(ydUtils.substr(szYdSchCd,6,2))) {

								recPara = JDTORecordFactory.getInstance().create();
								recPara.setField("YD_CRN_SCH_ID", szYdCrnSchId);
								recPara.setField("YD_SCH_CD",     szYdSchCd);
								recPara.setField("STL_NO",        szStlNo);
								recPara.setField("MODIFIER",      modifier);

								szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 시작 >>>> " + recPara.toString();
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

								ejbConn 		= new EJBConnector("default", this);
								String rtnMsg	= (String)ejbConn.trx("JPlateYdYdPJspSeEJB", "delStockLocOnRt", recPara);

								szMsg = "["+szOperationName+"] ---- RT BOOK-OUT 재료정보 CLEAR 종료 >>>> " + rtnMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							}
						}
					}
					
					//해당 열에 잡힌 크레인 작업예약을 조회하여 작업삭제 
					recPara2.setField("YD_STK_COL_GP"	, ydDnColGp);    
					
					getRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdWrkBookIdByLoc", logId, szMethodName, "해당 열에 잡힌 크레인 작업예약 조회");
					
					if (getRecSet.size() > 0) {
						
						//크레인 작업예약 삭제 처리
						for (int ii=0; ii<getRecSet.size(); ii++) {
							szYD_WBOOK_ID	= getRecSet.getRecord(ii).getFieldString("YD_WBOOK_ID");
							szYD_SCH_CD 	= getRecSet.getRecord(ii).getFieldString("YD_SCH_CD");
							szYD_EQP_ID 	= getRecSet.getRecord(ii).getFieldString("YD_EQP_ID");
		
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("MODIFIER",      	modifier);
							recPara.setField("YD_WBOOK_ID",		szYD_WBOOK_ID);
							recPara.setField("YD_EQP_ID",       szYD_EQP_ID);
							recPara.setField("YD_SCH_CD",     	szYD_SCH_CD);
		
							
							szMsg = "["+szOperationName+"] ---- 작업예약 취소 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
							ejbConn = new EJBConnector("default", "JPlateYdYdPJspSeEJB", this);
							outRecord1 	= (JDTORecord)ejbConn.trx("delJPlateWBook", new Class[] { JDTORecord.class }, new Object[] { recPara });
		
							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							
							szMsg = "["+szOperationName+"] ---- 작업예약 취소 종료!! >>>> " + outRecord1.toString();
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		
							if ("0".equals(sRTN_CD)) {
								m_ctx.setRollbackOnly();
								setRecord.setField("YD_L3_HD_RS_CD", 	"FU00"); 	//야드L3처리결과코드
								setRecord.setField("YD_L3_MSG", 		sRTN_MSG); 		//야드L3MESSAGE
								setRecord.setField("RTN_CD", 			JPlateYdConst.RETN_CD_FAILURE);
								return setRecord;
							}
						}
						
					}
				}
				
			} 
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			

			//---------------------------------------------------------
			// 1.2 보수장/가스장 일때 공베드 재검색
			//---------------------------------------------------------
			String  szYdStkColGp = ydUtils.substr(ydDnWrLoc, 0, 6);
			String  szYdStkBedNo = ydUtils.substr(ydDnWrLoc, 6, 2);
			if ("BC".equals(ydUtils.substr(ydDnWrLoc, 2, 2))) {
				ydDnWrLoc = ydStkLyrDao.getEmptyBsLoc(szYdStkColGp, szYdStkBedNo, "");
	            szMsg     = "["+szOperationName+"] 임가공절단장 적치가능 베드 다시 조회 >>>> " + ydDnWrLoc;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else if ("BS".equals(ydUtils.substr(ydDnWrLoc, 2, 2))) {
				ydDnWrLoc = ydStkLyrDao.getEmptyBsLoc(szYdStkColGp, szYdStkBedNo, "");
	            szMsg     = "["+szOperationName+"] 보수장일때 적치가능 베드 다시 조회 >>>> " + ydDnWrLoc;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else if ("CN".equals(ydUtils.substr(ydDnWrLoc, 2, 2))) {
				ydDnWrLoc = ydStkLyrDao.getEmptyCncLoc(szYdStkColGp, szYdStkBedNo, "");
	            szMsg     = "["+szOperationName+"] 가스장일때 적치가능 베드 다시 조회 >>>> " + ydDnWrLoc;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			/**********************************************************
			* 2. 권하위치, 좌표값, 이적 재료 등을 Check
				 YD_EQP_ID		//야드설비ID
				 YD_UP_WR_LOC	//야드권상실적위치
				 YD_UP_WR_LAYER	//야드권상실적단
				 YD_CRN_XAXIS	//야드크레인X축
				 YD_CRN_YAXIS	//야드크레인Y축
				 YD_EQP_WRK_SH	//야드크레인작업매수
			**********************************************************/
			recPara  = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
			recPara.setField("YD_EQP_ID", 		ydEqpId); 					//야드설비ID
			recPara.setField("YD_STK_COL_GP", 	ydDnWrLoc.substring(0,6)); 	//야드적치열구분
			recPara.setField("YD_STK_BED_NO", 	ydDnWrLoc.substring(6,8)); 	//야드적치Bed번호
			recPara.setField("YD_CRN_XAXIS", 	ydCrnXaxis); 				//야드크레인X축
			recPara.setField("YD_CRN_YAXIS", 	ydCrnYaxis); 				//야드크레인Y축

			//좌표값 에 해당하는 재료정보 조회
			intRtnVal = ydCrnSchDao.getOffCrnDnBed(recPara, rsResult);

			String ydStkLyrNo    = ""; //야드적치단번호
			String xaxisYn       = ""; //X좌표 정합성여부
			String yaxisYn       = ""; //Y좌표 정합성여부
			String ydStkBedXaxis = ""; //야드적치BedX축
			String ydStkBedYaxis = ""; //야드적치BedY축

			if (rsResult != null && rsResult.size() > 0) {
				ydStkLyrNo    	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_LYR_NO");
				xaxisYn       	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "XAXIS_YN");
				yaxisYn       	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YAXIS_YN");
				ydStkBedXaxis 	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_BED_XAXIS");
				ydStkBedYaxis 	= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_BED_YAXIS");
				ydDnWrLoc		= ydDaoUtils.paraRecChkNull(rsResult.getRecord(0), "YD_STK_LOC");
				oUpCnt 			= ydDaoUtils.paraRecChkNullInt(rsResult.getRecord(0), "O_UP_CNT");
			}

			if (intRtnVal > 0) {
				if (!"Y".equals(xaxisYn)) {
					//권하위치의 BedX축 허용오차 값내에 크레인X축 값 존재여부를 Check
					ydL3HdRsCd = "FU21";
					ydL3Msg    = "오류:크레인X축[" + ydStkBedXaxis + ":" + ydCrnXaxis + "] 이상";
				} else if (!"Y".equals(yaxisYn)) {
					//권하위치의 BedY축 허용오차 값내에 크레인Y축 값 존재여부를 Check
					ydL3HdRsCd = "FU22";
					ydL3Msg    = "오류:크레인Y축[" + ydStkBedYaxis + ":" + ydCrnYaxis + "] 이상";
				} else if (oUpCnt > 0) {
					//String szSpanGp = ydUtils.substr(ydDnWrLoc, 2, 2);
					if(!"BS".equals(ydDnSpanGp)&&!"CN".equals(ydDnSpanGp)&&!"CB".equals(ydDnSpanGp)) {
						//보수장, 가스장, 냉각대를 제외한 저장위치는 
						//권하위치의 권상예약정보 존재여부 체크
						ydL3HdRsCd = "FU23";
						ydL3Msg    = "오류:권상예약 정보 존재로 오류발생!";
					}
				}
			} else {
				//강제권하 가능 위치정보 미존재
				ydL3HdRsCd = "FU24";
			//	ydL3Msg    = "오류:강제권하 가능 위치정보 미존재! " + ydDnWrLoc;
				ydL3Msg    = "강제권하 가능 베드 선택 오류! " + ydDnWrLoc;
			}

			if (!"".equals(ydL3Msg)) {
				setRecord.setField("YD_L3_HD_RS_CD", 	ydL3HdRsCd); 	//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		ydL3Msg); 		//야드L3MESSAGE
				setRecord.setField("RTN_CD", 			JPlateYdConst.RETN_CD_FAILURE);
				return setRecord;
			}

			/**********************************************************
			* 3. TO위치 변경 처리 .. EJB호출
			**********************************************************/
			szMsg = "1후판정정 강제권하요구처리 .. TO위치 변경 처리 .. EJB호출 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			setRecord.setField("YD_CRN_SCH_ID", 	ydCrnSchId);
			setRecord.setField("YD_EQP_ID", 		ydEqpId);
			setRecord.setField("YD_DN_WO_LOC", 		ydDnWrLoc);								//권하위치
			setRecord.setField("YD_DN_WO_LAYER", 	ydStkLyrNo);
			setRecord.setField("YD_GP", 			JPlateYdConst.YD_GP_P_PLATE_YARD);		//1후판정정야드
			setRecord.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_WO);		//권하지시
			setRecord.setField("YD_DN_WRK_ACT_GP",	JPlateYdConst.YD_PILING_GP_F);   		//야드권하작업수행구분 : 강제권하
			setRecord.setField("MODIFIER", 			modifier);								//수정자

			EJBConnector ejbConn = new EJBConnector("default", this);
			String rtnMsg = (String)ejbConn.trx("JPlateYdCrnReSchSeEJB", "updCrnDnPrsFixYdP", setRecord);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {							//실패
				setRecord.setField("YD_L3_HD_RS_CD", 	"FU98"); 							//야드L3처리결과코드
				setRecord.setField("YD_L3_MSG", 		"오류:"+rtnMsg); 					//야드L3MESSAGE
				setRecord.setField("RTN_CD", 			JPlateYdConst.RETN_CD_FAILURE);
				szMsg = "1후판정정 강제권하요구처리 .. TO위치 변경 처리 .. EJB호출 오류발생>>>>" + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return setRecord;
			}
			szMsg = "1후판정정 강제권하요구처리 .. TO위치 변경 처리 .. EJB호출 종료";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			/* 권하위치 변경에서 호출함으로 ... 주석처리함*/

        	recInPara = JDTORecordFactory.getInstance().create();
        	recInPara.setField("RTN_CD", 		JPlateYdConst.RETN_CD_SUCCESS);
        	recInPara.setField("YD_L3_MSG", 	JPlateYdConst.RETN_CD_SUCCESS);
			return recInPara;

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	
	   /**
     * 오퍼레이션명 : 1후판정정 적치단 Clear - 권하지시위치와 권하실적 위치가 틀릴경우 호출
     *
     * @param  ● getRecSet
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int clearYdStklyrY2(JDTORecordSet getRecSet, String pMODIFIER) throws JDTOException {

    	JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();

    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 	= null;

    	String 	szMsg 			= "";
    	String 	szMethodName 	= "clearYdStklyrY2";
    	String 	szOperationName = "적치단 Clear";

    	int 	intRtnVal 		= 1;
		String 	szYdDnWoLoc   	= "";
        String 	szYdDnWoLayer 	= "";
        String 	szStlNo 		= "";
        String	szStkLyr		= "";

    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();

    		for(int ii=0; ii<rowsize; ii++) {

                //권하 지시위치 Clear
    			szYdDnWoLoc   	= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC");
                szYdDnWoLayer 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LAYER");
                szStlNo 		= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
                szStkLyr 		= ydDaoUtils.stringPlusInt(szYdDnWoLayer, ii);

                if ("".equals(szStlNo)) {
	                setRecord = JDTORecordFactory.getInstance().create();
	                setRecord.setField("YD_STK_COL_GP", 		szYdDnWoLoc.substring(0,6));
	                setRecord.setField("YD_STK_BED_NO", 		szYdDnWoLoc.substring(6,8));
	                setRecord.setField("YD_STK_LYR_NO",       	szStkLyr);
	                setRecord.setField("YD_STK_LYR_MTL_STAT",	"E");
	                setRecord.setField("STL_NO",              	"");
	                setRecord.setField("MODIFIER",            	pMODIFIER);
	                /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat 

	                UPDATE TB_YD_STKLYR
	                   SET MODIFIER            = :V_MODIFIER
	                     , MOD_DDTT            = SYSDATE
	                     , STL_NO              = :V_STL_NO
	                     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
	                     , YD_OCPY_BED_GP      = ''
	                     , YD_OCPY_STK_BED_NO  = ''
	                     , YD_OCPY_STK_LYR_NO  = ''
	                 WHERE YD_STK_COL_GP       = SUBSTR(:V_YD_STK_COL_GP,1,6)
	                   AND YD_STK_BED_NO       = :V_YD_STK_BED_NO
	                   AND YD_STK_LYR_NO       = :V_YD_STK_LYR_NO
	                */   
	                intRtnVal = ydStkLyrDao.updYdStklyrStat(setRecord);  	//적치단의 재료정보 Clear
                } else {
	                setRecord = JDTORecordFactory.getInstance().create();
	                setRecord.setField("YD_STK_COL_GP", 		szYdDnWoLoc.substring(0,6));
	                setRecord.setField("YD_STK_BED_NO", 		szYdDnWoLoc.substring(6,8));
	                setRecord.setField("STL_NO",              	szStlNo);
	                setRecord.setField("MODIFIER",            	pMODIFIER);
	                setRecord.setField("YD_GP",					JPlateYdConst.YD_GP_P_PLATE_YARD);

	                intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(setRecord);  //적치단의 재료정보 Clear
                }

                if (intRtnVal <= 0) {
    				szMsg = "["+szOperationName+"] 저장위치 적치단 CLEAR시 오류 발생 .. " + Integer.toString(intRtnVal);
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
                }

                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for


		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
	    }//end of try~catch

		return  intRtnVal;
    }//end of clearYdStklyrY2()
    /**
     * 오퍼레이션명 : 1후판정정 권하완료후 다음 작업예약 존재시 스케줄 기동
     *
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● DAOException
     */
    public int procNextYdCrnSchY2(JDTORecord msgRecord) throws DAOException {

    	String	szRtnMsg			= "";
		String 	szMsg           	= "";
		String 	szMethodName    	= "procNextYdCrnSchY2";
		String 	szOperationName 	= "권하후 다음스케줄 기동";

		String	szYdEqpId			= "";		//크레인설비ID
		String	szYdSchCd			= "";		//크레인스케줄코드
		String	szYdWbookId			= "";		//작업예약ID
		String	szRegister			= "";		//등록자
		String	szModifier			= "";		//수정자
		String	szYdUpWrLoc			= "";		//권상위치

        int 	intRtnVal 			= 1;
        int		iCrnSchCnt			= 0;

        JDTORecordSet rsResult      = null;

    	JDTORecord recSchPara 		= null;
    	JDTORecord recPara 			= null;
    	JDTORecord recTemp 			= null;

    	JPlateYdWrkbookDAO 	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
    	JPlateYdCrnSchDAO	ydCrnSchDao		= new JPlateYdCrnSchDAO();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.09 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

    	try {

    		szYdEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"		);		// 크레인설비ID
    		szYdSchCd	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"		);		// 크레인스케줄코드
    		szRegister	= ydDaoUtils.paraRecChkNull(msgRecord, "REGISTER"		);		// 등록자
    		szModifier  = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"		);		// 수정자
    		szYdUpWrLoc	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC"	);		// 권상위치

    		for(int ii=0; ii<JPlateYdConst.MAX_CRN_SCH_CNT; ii++) {

        		// 스케쥴 코드로 크레인작업지시를 조회하여 5건 이하시 다음 스케쥴 기동
        		rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        		recPara  = JDTORecordFactory.getInstance().create();
        		recPara.setField("YD_SCH_CD", 		szYdSchCd);

        		iCrnSchCnt = ydCrnSchDao.getByYdSchCd(recPara, rsResult);
    			if (iCrnSchCnt >= JPlateYdConst.MAX_CRN_SCH_CNT) {
        			szMsg = "[" + szOperationName + "] ----------- 다음 스케줄 기동 SKIP :: " + ii + " .. 스케쥴 건수 .. " + iCrnSchCnt;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        			
    				break;
    			}

    			szMsg = "[" + szOperationName + "] ----------- 다음 스케줄 기동 대상 조회 :: " + ii + "번째 .. 스케줄 :: " + szYdSchCd + ", 권상위치 :: " + szYdUpWrLoc;
    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				//---------------------------------------------
	    		// 다음 스케줄 존재여부 체크 - 스케쥴코드로 작업예약 조회
				//---------------------------------------------
        		rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        		recPara  = JDTORecordFactory.getInstance().create();
        		recPara.setField("YD_SCH_CD", 		szYdSchCd						);		// 스케줄코드
        		recPara.setField("YD_STK_COL_GP",	ydUtils.substr(szYdUpWrLoc,0,6)	);		// 권상위치

        		intRtnVal = ydWrkbookDao.getNextWrkBookIdBySchCd(recPara, rsResult);

				if (intRtnVal > 0) {

					rsResult.first();

	        		recTemp = JDTORecordFactory.getInstance().create();
					recTemp = rsResult.getRecord();
					szYdWbookId = ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID"	);
					szYdSchCd	= ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD"	);

					//-----------------------------------------
					// 다음 스케줄기동
					//-----------------------------------------
					recSchPara 	= JDTORecordFactory.getInstance().create();
					recSchPara.setField("MSG_ID", 		"YDYDJ"		);		// TC코드
					recSchPara.setField("YD_EQP_ID", 	szYdEqpId	);		// 크레인설비ID
					recSchPara.setField("YD_SCH_CD",	szYdSchCd	);		// 크레인스케줄코드
					recSchPara.setField("YD_WBOOK_ID",	szYdWbookId	);		// 작업예약ID
					recSchPara.setField("REGISTER", 	szRegister	);
					recSchPara.setField("MODIFIER", 	szModifier	);
					recSchPara.setField("CHK_FROM_LOC", "N"			);		// 권상위치에 작업예약 존재여부 체크 하지 안도록 SET

					szMsg    = "[" + szOperationName + "] ----------- 다음 스케줄기동 START :: " + szYdWbookId + "," + szYdSchCd;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.09 recSchPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
					recSchPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------

			        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
			        szRtnMsg = (String)ejbConn.trx("procCrnSchMainYdP", new Class[] { JDTORecord.class }, new Object[] { recSchPara });

					szMsg    = "[" + szOperationName + "] ----------- 다음 스케줄기동 END :: " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				} else {

					szMsg    = "[" + szOperationName + "] ----------- 다음 스케줄 기동 대상이 없습니다.";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					break;
 
				} 
    		}	// end for

        } catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			//throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
        	szRtnMsg = "다음 스케줄기동  .. <br>" + szRtnMsg;
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        }//end of try~catch

        return intRtnVal;

    } // end of procNextYdCrnSchY2()
    
    /**
     * 오퍼레이션명 : 1후판정정 권하위치가 RT일경우 북인 실적 전송
     *
     * @return ● intRtnVal
     * @throws ● DAOException
     */
    public String procRtBookInY2(JDTORecord msgRecord, String[] pArrStlNo) throws DAOException {

    	JPlateYdDelegate      	ydDelegate		= new JPlateYdDelegate();
    	JPlateYdStockDAO      	ydStockDao      = new JPlateYdStockDAO();
    	JPlateYdStkLyrDAO     	ydStkLyrDao     = new JPlateYdStkLyrDAO();
    	JPlateYdCrnWrkMtlDAO  	ydCrnWrkMtlDao	= new JPlateYdCrnWrkMtlDAO();

        int intRtnVal = 0;

        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord 		getRecord   = null;
        JDTORecord 		recPara		= null;
        JDTORecordSet 	rsResult    = null;

        String 	szMsg            	= "";
        String 	szRtnMsg            = "";
        String 	szSendMsg           = "";
        String 	szMethodName     	= "procRtBookInY2";
        String 	szOperationName  	= "1후판정정 권하실적처리";

        String	szModifier			= "";
        String	szStlNo				= "";
        String	szCrnSchId			= "";
        String 	szYdStkColGp		= "";
        String 	szYdStkBedNo     	= "";
        String	szStlNoList			= "";
        String  szDnLoc             = "";
        
    	try {

			// ------------------------------------------------------------------------
			// 조업 L2 북인 실적 전송 .. 순서가 뒤바뀌는 경우가 존재하여 일괄 전송처리
			// ------------------------------------------------------------------------
    		if (pArrStlNo != null && pArrStlNo.length > 0) {
				szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 일괄전송 .. 시작";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    		for (int ii=0; ii<pArrStlNo.length; ii++) {
	    			if (!"".equals(pArrStlNo[ii])) {
		    			if ("".equals(szStlNoList)) {
		    				szStlNoList = pArrStlNo[ii];
		    			} else {
		    				szStlNoList = szStlNoList + ";" + pArrStlNo[ii];
		    			}
	    			}
	    		}
	    		
	    		szDnLoc = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC");
	    		
	    		szMsg = " 1 후판전단L2 온라인RT 실적 전송 .. 저장위치>>>>"+szDnLoc;
                ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				recPara = JDTORecordFactory.getInstance().create();
				
				if(szDnLoc.startsWith("PBRTWB")) {
					// 전문송신 없슴
				}else{
				
					if(szDnLoc.startsWith("PBRT")||szDnLoc.startsWith("PART13")) {
						recPara.setField("MSG_ID", 		        "YDP3L501");
						recPara.setField("STL_NO",				"");					// 재료번호
						recPara.setField("STL_NO_LIST",			szStlNoList);			// 재료번호 LIST
						recPara.setField("OPERATION_TYPE",		"1");					// 1:Book In, 2:Book Out
						recPara.setField("YD_STK_COL_GP",		ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC").substring(0,6));			// TO위치
						recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);    		// 야드적치BED번호
						recPara.setField("YD_EQP_ID", 		    ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6));		
	
						szSendMsg = ydDelegate.sendMsg(recPara);
	
						szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 완료>>>>"+szSendMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						
					} else if(szDnLoc.startsWith("PFRT50")) {// 온라인 횡행작업 I/F 반영
						
						String[] pArrBedNo = {"PFRT50","PFRT49","PFRT48"}; 
						
						for (int ii=0; ii<pArrStlNo.length; ii++) {
			    			if (!"".equals(pArrStlNo[ii])) {
			    				
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSend("1", pArrStlNo[ii]
			        	                                                          ,pArrBedNo[ii]
			        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6));
	
			    		        szMsg = "["+ szOperationName +"] 1 후판전단L2 온라인RT BOOK-OUT 실적 전송 .. 완료>>>>"+szSendMsg;
			                    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			    			}
			    		}
						
					} else {
						
			    		for (int ii=0; ii<pArrStlNo.length; ii++) {
			    			if (!"".equals(pArrStlNo[ii])) {
			    				
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSend("1", pArrStlNo[ii]
			        	                                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC").substring(0,6)
			        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6));
	
			    		        szMsg = "["+ szOperationName +"] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>"+szSendMsg;
			                    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			    			}
			    		}
					}	
				}
    		}

	        szCrnSchId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	        szModifier	= ydDaoUtils.paraRecModifier(msgRecord);

	        rsResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
    		recPara  	= JDTORecordFactory.getInstance().create();
    		recPara.setField("YD_CRN_SCH_ID", 	szCrnSchId);

	        intRtnVal = ydCrnWrkMtlDao.getByYdCrnSchIdWithLoc(recPara, rsResult);

	        if (intRtnVal > 0) {

	        	rsResult.first();
				for(int ii=0; ii<rsResult.size(); ii++) {

			        rsResult.absolute(ii+1);
			        getRecord		= JDTORecordFactory.getInstance().create();
			        getRecord 		= rsResult.getRecord();

			        szStlNo			= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
			        szYdStkColGp	= ydDaoUtils.paraRecChkNull(getRecord, "YD_STK_COL_GP");
			        szYdStkBedNo	= ydDaoUtils.paraRecChkNull(getRecord, "YD_STK_BED_NO");
			        
			        if(szYdStkColGp.startsWith("PBRTWB")) {
			        	// ------------------------------------------------------------------------
						// 아래의 1,2,3로직 처리안함.
						// ------------------------------------------------------------------------
			        }else{
						// ------------------------------------------------------------------------
						// 1. 야드 L2 저장품재원 삭제전문 전송
						// ------------------------------------------------------------------------
						szMsg = "[ " +szOperationName + "] 야드L2 저장품제원 전문송신 START";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("JMS_TC_CD", 			"YDY2L002");                            // TC-CODE
						recPara.setField("YD_GP", 				JPlateYdConst.YD_GP_P_PLATE_YARD);		// 야드구분
						recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);            				// 야드적치열구분
						recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);    						// 야드적치BED번호
						recPara.setField("YD_INFO_SYNC_CD", 	"5");									// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
						recPara.setField("STL_NO", 				szStlNo);	        					// 재료번호
						recPara.setField("MSG_GP", 				"D");	        						// 전문구분
						szRtnMsg = ydDelegate.sendMsg(recPara);
	
						szMsg = "[ " +szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	
						// ------------------------------------------------------------------------
						// 2. 조업 L2 북인 실적 전송 .. 순서가 뒤바뀌는 경우가 존재하여 일괄 전송처리
						// ------------------------------------------------------------------------
						if (pArrStlNo != null && pArrStlNo.length > 0) {
							szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 일괄전송하여 .. SKIP";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						} else {
							szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 시작";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	
							recPara = JDTORecordFactory.getInstance().create();
	
							if("PB".equals(ydUtils.substr(szYdStkColGp, 0, 2))||szYdStkColGp.startsWith("PART13")) {
								recPara.setField("MSG_ID", 		"YDP3L501");							//열처리
								recPara.setField("STL_NO",				szStlNo);							// 재료번호
								recPara.setField("OPERATION_TYPE",		"1");								// 1:Book In, 2:Book Out
								recPara.setField("YD_STK_COL_GP",		szYdStkColGp);						// TO위치
								recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);    					// 야드적치BED번호
								recPara.setField("YD_EQP_ID", 		    ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6));		
								szSendMsg = ydDelegate.sendMsg(recPara);
	
								szMsg = "["+ szOperationName +"] RT BOOK-IN 실적 전송 .. 완료>>>>"+szSendMsg;
								ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							} else {
	
								szSendMsg = JPlateYdCommonUtils.procJPlateSmsSend("1", szStlNo, szYdStkColGp,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6));
	
			    		        szMsg = "["+ szOperationName +"] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>"+szSendMsg;
			                    ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
							}				
						}
	
						// ------------------------------------------------------------------------
						// 3. 재료정보 삭제처리
						// ------------------------------------------------------------------------
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO",				szStlNo);								// 재료번호
						recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
						recPara.setField("MODIFIER", 			szModifier);
	
						intRtnVal = ydStockDao.delYdStock(recPara);
						if (intRtnVal < 0) {
							szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						}
			        }
					// ------------------------------------------------------------------------
					// 4. 저장위치 CLEAR
					// ------------------------------------------------------------------------
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", 				szStlNo);             	// 재료번호
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");             		// 야드적치단재료상태
					recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD);
					recPara.setField("MODIFIER", 			szModifier);

					intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
					if (intRtnVal < 0) {
						szRtnMsg = "저장위치 삭제 처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					}
				}
	        }

        } catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			//throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
			szMsg    = "["+szOperationName+"] RT 북인 실적 전송시 .. Exception 발생" + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

        }//end of try~catch

        return JPlateYdConst.RETN_CD_SUCCESS;

    }//end of procRtBookInY2()	
    /**
     * 오퍼레이션명 : 1후판정정 적치단 등록
     *
     * @param  ● getRecSet, sRealLyrNo
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public String regYdStklyrY2(JDTORecordSet pRecSet, String pMODIFIER, String[] pStlNo, String[] pYdDnWrLoc, String pYdUpWrkActGp, String pTopLyrNo, String logId)throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
//2024.12.06 regYdStklyrY2 argument 에 logId 항목 추가 개선
// 	public String regYdStklyrY2(JDTORecordSet pRecSet, String pMODIFIER, String[] pStlNo, String[] pYdDnWrLoc, String pYdUpWrkActGp, String pTopLyrNo)throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////


    	JPlateYdCrnWrkMtlDAO 	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();

    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 	= null;
    	JDTORecord crnRecord 	= null;

    	JDTORecordSet rsResult  = null;
    	JDTORecord recPara 		= null;
    	JDTORecord recTemp 		= null;

    	String 	szRtnMsg		= null;
    	String 	szMsg 			= "";
        String 	szMethodName	= "regYdStklyrY2";
        String 	szOperationName	= "적치단 등록";

        int 	intRtnVal 		= 0;

        String 	szYdEqpId 		= "";
        String 	szYdWbookId		= "";
        String 	szYdCrnSchId	= "";
		String	szYdUpWrLoc	   	= "";
		String	szYdUpWrLayer  	= "";
        String 	szYdDnWrLoc		= "";
        String 	szYdDnWrLayer	= "";
        String 	szStlNo			= "";
        String	szMODIFIER		= pMODIFIER;
		String	szYdStkColGp	= "";
		String	szYdStkBedNo	= "";
		String	szTopLyrNo		= "000";
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 logId 개선 
    	if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    // log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

        JPlateYdStkLyrDAO ydStkLyrDao = new JPlateYdStkLyrDAO();

    	try {
			szMsg = "[" + szOperationName + "] ============================= 권하실적 적치단 등록 .. START :: ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szTopLyrNo = pTopLyrNo;
			if (szTopLyrNo == null || "".equals(szTopLyrNo)) {
				szTopLyrNo = "000";
			}

    		int rowsize = pRecSet.size();

    		boolean isLast = false;

        	for(int ii=0; ii<rowsize; ii++) {

        		pRecSet.absolute(ii+1);
        		getRecord = JDTORecordFactory.getInstance().create();
        		getRecord.setRecord(pRecSet.getRecord());

        		szYdEqpId 		= ydDaoUtils.paraRecChkNull(getRecord, "YD_EQP_ID"		);
        		szYdWbookId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID"	);
    	        szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_CRN_SCH_ID"	);

        		//권상 실적위치
        		szYdUpWrLoc	   	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC"	);
        		szYdUpWrLayer  	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LAYER"	);

                szMsg = "[" + szOperationName + "] 권상실적 위치 :: " + szYdUpWrLoc + ", 단::" + szYdUpWrLayer;
    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        		szYdDnWrLoc	   	= pYdDnWrLoc[ii];
        		szStlNo	 		= pStlNo[ii];

/*
        		// 권하실적위치로 최상단 조회
        		// L2에서 전송하는 STL_NO1~15 , YD_DN_WR_LOC1 ~ 15 는 횡행작업일때만 존재함 ----> (무조건 존재 , 15건 으로 변경)
        		if (pYdDnWrLoc != null && pStlNo != null) {
	        		for(int kk=0; kk<pYdDnWrLoc.length; kk++) {
	        			if (pStlNo[kk] != null && pStlNo[kk].equals(szStlNo)) {
	                		szYdDnWrLoc	= pYdDnWrLoc[kk];
	        				break;
	        			}
	        		}
        		}
*/

    	        szYdStkColGp   = ydUtils.substr(szYdDnWrLoc, 0, 6);
    	        szYdStkBedNo   = ydUtils.substr(szYdDnWrLoc, 6, 2);

    	        if ("BC".equals(ydUtils.substr(szYdStkColGp, 2, 2))||"BS".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyBsLoc(szYdStkColGp, szYdStkBedNo, szStlNo);
        	        szYdDnWrLayer = "001";
                    szMsg = "[" + szOperationName + "] 보수장/임가공 절단장 일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    	        } else if ("CN".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyCncLoc(szYdStkColGp, szYdStkBedNo, szStlNo);
        	        szYdDnWrLayer = "001";
                    szMsg = "[" + szOperationName + "] 가스장일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    	        } else {

	    	        // 저장위치의 최상단 정보 조회
   	        		szYdDnWrLayer = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdStkBedNo, szStlNo, pYdUpWrkActGp, szTopLyrNo);

	                szMsg = "[" + szOperationName + "] 권하실적 위치 :: " + szYdDnWrLoc + ", 단::" + szYdDnWrLayer + ", 재료번호::" + szStlNo;
	    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    			if ("".equals(szYdDnWrLayer) || "000".equals(szYdDnWrLayer)) {
	    				szRtnMsg = "저장위치의 최상단 조회 오류 .. " + szYdStkColGp + szYdStkBedNo;
	    				return szRtnMsg;
	    			}
    	        }

        		// 크레인에 UPDATE (크레인 적치상태 Clear)
    			crnRecord = JDTORecordFactory.getInstance().create();
    			crnRecord.setField("YD_STK_COL_GP",       	szYdEqpId											);
    			crnRecord.setField("YD_STK_BED_NO",       	"01"												);
    			crnRecord.setField("YD_STK_LYR_NO",       	ydUtils.addLeftStr(Integer.toString(ii+1), 3, '0')	);
                crnRecord.setField("YD_STK_LYR_MTL_STAT", 	"E"													);
                crnRecord.setField("STL_NO",              	""													);
                crnRecord.setField("MODIFIER",              szMODIFIER											);
            	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 crnRecord에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
                crnRecord.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
                
                intRtnVal = this.updYdStklyrY7(crnRecord);  // 크레인 적치단의 재료정보 UPDATE

                szMsg = "[" + szOperationName + "] ============================= 적치단 업데이트 처리 =============================" + intRtnVal;
    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    			setRecord = JDTORecordFactory.getInstance().create();
    			setRecord.setField("YD_STK_COL_GP",			ydUtils.substr(szYdDnWrLoc,0,6)	);
        		setRecord.setField("YD_STK_BED_NO",       	ydUtils.substr(szYdDnWrLoc,6,2)	);
                setRecord.setField("YD_STK_LYR_NO", 		szYdDnWrLayer					);
                setRecord.setField("STL_NO",              	szStlNo							);
                setRecord.setField("MODIFIER",              szMODIFIER						);
                setRecord.setField("YD_GP",					ydUtils.substr(szYdEqpId,0,1)	);

                // 권하실적 처리시 해당 저장위치에 다른 재료 권하 예약 되어 있으면 CLEAR 처리함 ()
            	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 setRecord에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
                setRecord.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------

                szRtnMsg = this.clearDnLocOtherMtl(setRecord);
                if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                    szMsg = "[" + szOperationName + "] >>>> 다른 재료 권하 예약 CLEAR 결과 >>>> " + szRtnMsg;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                	return szRtnMsg;
                }

                //-------------------------------------------------------------------------------------------------------------
                // 같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 경우에는 권상대기 상태로 변경을 하고
                // 그렇지 않은 경우에는 적치중 상태로 변경한다.
                //-------------------------------------------------------------------------------------------------------------
                szMsg = "[" + szOperationName + "] 같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 시작";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		        recPara	= JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID",	szYdCrnSchId	);
                recPara.setField("YD_WBOOK_ID",     szYdWbookId	);
                recPara.setField("STL_NO",          szStlNo	);

                rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
            	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
                recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------

                szRtnMsg = ydCrnWrkMtlDao.getGreaterThanCrnSch(recPara, rsResult);		// intGp == 17

                szMsg = "[" + szOperationName + "] 같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 완료 - 메세지 : " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                //-------------------------------------------------------------------------------------------------------------
                if (JPlateYdConst.RETN_CD_NOTEXIST.equals(szRtnMsg)) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", "C");					//적치중
                	isLast = true;
                } else if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", "U");					//권상대기
                	isLast = false;
                } else {
                	szMsg = "[" + szOperationName + "] 다음크레인스케줄의 작업재료를 조회 중 오류발생 - " + szRtnMsg;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
        			setRecord.setField("YD_STK_LYR_MTL_STAT", "C");					//적치중으로 반영
        			isLast = true;
                }
                //-------------------------------------------------------------------------------------------------------------
                
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "isLast = " +  Boolean.toString(isLast), JPlateYdConst.DEBUG, logId);
                if (isLast) {
	                // 적치단에 존재하는 해당 저장품정보를 모두 CLEAR한다.
	                recTemp = JDTORecordFactory.getInstance().create();
	                recTemp.setField("STL_NO", 			szStlNo								);
	                recTemp.setField("YD_STK_COL_GP",	ydUtils.substr(szYdUpWrLoc,0,6)		);
	                recTemp.setField("YD_STK_BED_NO",   ydUtils.substr(szYdUpWrLoc,6,2)		);
	                recTemp.setField("YD_STK_LYR_NO", 	szYdUpWrLayer						);
	                recTemp.setField("MODIFIER",        szMODIFIER							);
	                recTemp.setField("YD_GP",			JPlateYdConst.YD_GP_P_PLATE_YARD	);

	            	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
	                recTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------

	           // 	intRtnVal = ydStkLyrDao.updYdStklyrClear(recTemp);
	                intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recTemp);  	//적치단의 재료정보 Clear
                }

                // 권하위치 적치상태 변경
            	
//-------------------------------------------------------------------------------------------------------------------------
//2024.12.06 setRecord에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
                setRecord.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------

                intRtnVal = this.updYdStklyrY7(setRecord);

        	}

			szMsg = "[" + szOperationName + "] ============================= 권하실적 적치단 등록 .. END";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
	    }//end of try~catch

		return JPlateYdConst.RETN_CD_SUCCESS;

    } // end of regYdStklyrY2()    
    
    
	/**********************************************************
	* 1후판정정야드자동화 신규메소드 추가
	**********************************************************/	
    
	/**
     * 오퍼레이션명 : 1후판정정 권하실적처리 (Y2YDL009) 신규메소드
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY2CrnDnWr2(JDTORecord msgRecord) throws DAOException, JDTOException {
		
    	JPlateYdDelegate      	ydDelegate		= new JPlateYdDelegate();
    	JPlateYdCommDAO 		commDao 		= new JPlateYdCommDAO();
    	YdStockDao  ydStockDao      = new YdStockDao();
// 2024.12.06 신규 logId 사용                         
//    	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
    		
        int intRtnVal = 0;
    	
        String 	szMsg            	= "";
        String 	szRtnMsg            = "";
        String 	szMethodName     	= "procY2CrnDnWr2";
        String 	szOperationName  	= "1후판정정 권하실적처리(신규)";
		
        String 	szYdSchCd        	= "";
        String 	szYdEqpId        	= "";
        String 	szCrnSchId       	= "";
        String 	szYdDnWrLoc      	= "";
        String 	szYdDnWrLayer    	= "";
        String  szYdPilingGp		= "";
        
        String	szTemp				= "";
        String	szModifier			= "";
        String	szArrStlNo			= "";
        
        String 	szYdUpWrLoc      	= "";
        String 	szYdWbookId     	= "";
        String	szStlNo				= "";
        String	szStlNos			= "";
        String	szYdUpWrkActGp		= "";
            
        String 	szYdStkColGp		= "";
        String 	szYdStkBedNo     	= "";
        String 	szRealTopLyr     	= "";
        
        String 	szYdCrnXaxis     	= "";
        String 	szYdCrnYaxis     	= "";
        String 	szYdCrnZaxis     	= "";
        
        String	szNextSchCallYn		= "N";		// 권하 완료후 다음 스케줄 호출 Flag - 작업예약 삭제후 Set됨
        String 	szSendMsg           = "";
        String 	szYdTcarSchId    	= "";
        String szYD_CAR_SCH_ID      = "";
        
        String[]	arrStlNo		= {"","","","","" ,"","","","","" ,"","","","",""};
		String[]	arrYdDnWrLoc	= {"","","","","" ,"","","","","" ,"","","","",""};
		String[]	arrYdDnWrLayer	= {"","","","","" ,"","","","","" ,"","","","",""};
		String[]	arrYdDnWrBedNo	= {"","","","","" ,"","","","","" ,"","","","",""};
		
		String szCARD_NO = null; 

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		        
        //권하실적완료처리호출시에 받아온 파라미터값을 NULL CHECK후 사용
        JDTORecord getCrnschRecord 	= JDTORecordFactory.getInstance().create();

        //DATA SETTING시 사용
        JDTORecord setRecord 		= JDTORecordFactory.getInstance().create();
        
        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord        = JDTORecordFactory.getInstance().create();
        
        JDTORecord recInTemp        = null;
        
        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 	= null;
        JDTORecordSet rsResult      = null;

        
        int		iMtlCnt				= 0;		// 권하실적 재료 갯수
        
        String 	szRcvTcCode			= ydUtils.getTcCode(msgRecord);
        if (szRcvTcCode==null || "".equals(szRcvTcCode)) {
            szRtnMsg = "TC Code Error (" + szRcvTcCode + ")";
            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
            throw new DAOException(szRtnMsg);
        }

        if (bDebugFlag) {
            szMsg = "[" + szOperationName + "] [DEBUG] 전문수신 : TCCODE=" + szRcvTcCode;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        }
        
        try {
        	szMsg = "[" + szOperationName + "] 메소드 시작 - 파라미터 확인 >>>> " + msgRecord.toString();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);


        	//-------------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------------
	        intRtnVal 		= this.paramCheckY7(msgRecord, getCrnschRecord);

	        szCrnSchId 		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_CRN_SCH_ID"	);
	        szYdEqpId 		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_EQP_ID"		);
	        szYdSchCd 		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_SCH_CD"		);
	        szYdDnWrLoc		= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC"		);
	        szYdDnWrLayer	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER"	);
	        szYdPilingGp	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_PILING_GP"		); //S : RT상에 1매씩 분리 권하 **
		
	        // L2에서 전송하는 15건으로 증가
	        for(int ii=0; ii<15; ii++) {
		        arrStlNo[ii]			= ydDaoUtils.paraRecChkNull(getCrnschRecord, "STL_NO" + (ii+1)			);	// 재료번호1~15			CHAR	11
		        if (!"".equals(arrStlNo[ii])) {
			        arrYdDnWrLoc[ii]	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LOC" + (ii+1)	);	// 야드권하실적위치1~15	CHAR	8
			        arrYdDnWrLayer[ii]	= ydDaoUtils.paraRecChkNull(getCrnschRecord, "YD_DN_WR_LAYER" + (ii+1)	);	// 야드권하실적단1~15		CHAR	3

			        // 권하위치 오류 체크
			        if ("".equals(arrYdDnWrLoc[ii]) || arrYdDnWrLoc[ii].length() != 8) {
			            szRtnMsg = "권하위치 입력 오류 .. " + arrYdDnWrLoc[ii];
			            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				        
				        throw new DAOException(szRtnMsg);
			        }
			        
			        // 적치단 오류 체크
			        if ("".equals(arrYdDnWrLayer[ii]) || arrYdDnWrLayer[ii].length() != 3 || Integer.parseInt(arrYdDnWrLayer[ii]) < 1) {
			            szRtnMsg = "적치단 입력 오류 .. " + arrYdDnWrLayer[ii];
			            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				        
				        throw new DAOException(szRtnMsg);
			        }

			        arrYdDnWrBedNo[ii]	= ydUtils.substr(arrYdDnWrLoc[ii], 6, 2);
			        iMtlCnt ++;
		        }
	        }

            szMsg    = "[" + szOperationName + "] 권하실적처리 대상 매수 >>>> " + Integer.toString(iMtlCnt);
	        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        
	        // SORT - 적치단 ASC , 적치베드 ASC
	        for(int ii=0; ii<iMtlCnt; ii++) {
		        for(int jj=(ii+1); jj<iMtlCnt; jj++) {
		        	if ("".equals(arrStlNo[jj])) {
		        		continue;
		        	}
		        	if (Integer.parseInt(arrYdDnWrLayer[ii])  > Integer.parseInt(arrYdDnWrLayer[jj]) ||
		        	   (Integer.parseInt(arrYdDnWrLayer[ii]) == Integer.parseInt(arrYdDnWrLayer[jj]) &&
		        		Integer.parseInt(arrYdDnWrBedNo[ii])  > Integer.parseInt(arrYdDnWrBedNo[jj]))) {

		        		szTemp 				= arrStlNo[ii];
		    		    arrStlNo[ii]		= arrStlNo[jj];
		    		    arrStlNo[jj]		= szTemp;

		        		szTemp 				= arrYdDnWrLoc[ii];
				        arrYdDnWrLoc[ii]	= arrYdDnWrLoc[jj];
				        arrYdDnWrLoc[jj]	= szTemp;

				        szTemp				= arrYdDnWrLayer[ii];
				        arrYdDnWrLayer[ii]	= arrYdDnWrLayer[jj];
				        arrYdDnWrLayer[jj]	= szTemp;

				        szTemp				= arrYdDnWrBedNo[ii];
				        arrYdDnWrBedNo[ii]	= arrYdDnWrBedNo[jj];
				        arrYdDnWrBedNo[jj]	= szTemp;
		        	}
		        }
	        }

	        // DEBUG .. SORT결과 출력
	        for(int ii=0; ii<iMtlCnt; ii++) {
	        	szStlNos += arrStlNo[ii] + ","; //권하실적 처리시 전문으로 전달된 재료번호 리스트
	            szMsg    = "[" + szOperationName + "] SORT 결과 출력 " + (ii+1) + " >>>> 재료번호 :: " + arrStlNo[ii] + ", 권하위치 :: " + arrYdDnWrLoc[ii] + "-" + arrYdDnWrLayer[ii];
		        ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        }
	        
	        szModifier = ydDaoUtils.paraRecModifier(msgRecord);

	        if ("".equals(szCrnSchId)) {
	            szRtnMsg = "크레인스케줄ID가 없습니다.";
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                
                throw new DAOException(szRtnMsg);
	        }
	        
	        //파라미터 레코드 편집
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID", 	szCrnSchId		);
	        setRecord.setField("YD_SCH_CD", 		szYdSchCd		);
	        setRecord.setField("YD_DN_WR_LOC", 		szYdDnWrLoc		);
	        setRecord.setField("YD_DN_WR_LAYER", 	szYdDnWrLayer	);
	        setRecord.setField("MODIFIER", 			szModifier		);

	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "] 업데이트 - 권하실적위치[" + szYdDnWrLoc + "]와 권하실적단[" + szYdDnWrLayer + "]을 업데이트 시작";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            
            /* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnDnWr 

            UPDATE TB_YD_CRNSCH
               SET
                   MODIFIER         = :V_MODIFIER
                 , MOD_DDTT         = SYSDATE
                 , YD_WRK_PROG_STAT = NVL(:V_YD_WRK_PROG_STAT, YD_WRK_PROG_STAT)    -- NULL일때 전 상태값 유지
                 , YD_DN_WR_LOC     = :V_YD_DN_WR_LOC
                 , YD_DN_WR_LAYER   = :V_YD_DN_WR_LAYER
                 , YD_DN_WRK_ACT_GP = :V_YD_DN_WRK_ACT_GP
                 , YD_DN_WR_XAXIS   = :V_YD_DN_WR_XAXIS
                 , YD_DN_WR_YAXIS   = :V_YD_DN_WR_YAXIS
                 , YD_DN_WR_ZAXIS   = :V_YD_DN_WR_ZAXIS
                 , YD_DN_CMPL_DT    = TO_DATE(:V_YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS')
                 , YD_WRK_HDS_DD    = :V_YD_WRK_HDS_DD
             WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
			*/
			intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnDnWr", logId, szMethodName, "크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트");

			szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "] 업데이트 - 권하실적위치[" + szYdDnWrLoc + "]와 권하실적단[" + szYdDnWrLayer + "]을 업데이트 완료";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            
            //-------------------------------------------------------------------------------------------------------------------
	        //	크레인스케줄 작업재료 조회
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "]작업재료 조회 시작";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            //if("S".equals(szYdPilingGp)) {
            	//S : RT상에 1매씩 분리 권하 **
    	        setRecord.setField("STL_NOS", 		szStlNos);  //권하실적 처리시 전문으로 전달된 재료번호 리스트
            	getRecSet = commDao.select(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdCrnWrkMtl2", logId, szMethodName, "크레인스케줄 작업재료 조회 - 권하실적 처리시 전문으로 전달된 재료번호 리스트 대상만 조회 **");
            //} else {
            //	getRecSet = commDao.select(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtl", logId, szMethodName, "크레인스케줄 작업재료 조회");
            //}
            
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if (getRecSet.size() == 0) {
	            szRtnMsg = "권하실적 크레인작업재료  조회 오류 .. 0 건 조회" ;
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	            
	            throw new DAOException(szRtnMsg);
	        }

	        // 조업 L3전송용 재료번호 편집 --YDPRJ011
	        getRecSet.first();
	        for(int ii=0; ii<getRecSet.size(); ii++) {
	        	getRecSet.absolute(ii+1);
		        getRecord = getRecSet.getRecord();
		        if (ii > 0) {
		        	szArrStlNo = szArrStlNo + ";";
		        }
	        	szArrStlNo = szArrStlNo + ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
	        }
            
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();

	        szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "]작업재료 조회 완료 - 대상재 건수 : " + Integer.toString(getRecSet.size());
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            szYdUpWrLoc 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC"		);
	        szYdWbookId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID"		);		// 작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szStlNo			= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"				);
	        szYdUpWrkActGp 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WRK_ACT_GP"	);		// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
	        szCARD_NO		= ydDaoUtils.paraRecChkNull(getRecord, "CARD_NO"			);		// L3 화면에서 만들어진 지시여부 

	        //-------------------------------------------------------------------------------------------------------------------
	        //	작업진행상태 확인 후 상태가 맞지 않으면 롤백 처리
            //-------------------------------------------------------------------------------------------------------------------
        	//작업진행상태 체크(2, 3) 2,3이 아니면 에러 메시지 출력후 리턴
	        if (!"2".equals(ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT")) &&
	        	!"3".equals(ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT"))) {

	            szRtnMsg = "작업진행상태[" + ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT") + "]가 권상완료 아님!.";
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	            
	            throw new DAOException(szRtnMsg);
	        }
            
	        //-------------------------------------------------------------------------------------------------------------------
	        // 실제적치단의 조회
	        //-------------------------------------------------------------------------------------------------------------------
        	if (!"".equals(arrYdDnWrLoc[0])) {
        		szYdDnWrLoc = arrYdDnWrLoc[0];
        		for(int ii=0; ii<15; ii++) {
        			if (!"".equals(arrYdDnWrLoc[ii]) && "01".equals(ydUtils.substr(arrYdDnWrLoc[ii], 6, 2))) {
                		szYdDnWrLoc = arrYdDnWrLoc[ii];
        			}
        		}
        	}
	        szYdStkColGp   = szYdDnWrLoc.substring(0, 6);
	        szYdStkBedNo   = szYdDnWrLoc.substring(6, 8);

	        if (!"BC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) && !"BS".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) && !"CN".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

		        szRealTopLyr = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdStkBedNo, szStlNo, szYdUpWrkActGp, "");

		        if ("000".equals(szRealTopLyr)) {
		            szRtnMsg = "권하실적 최상단 검색시 오류발생 :: " + szRealTopLyr;
		            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
		            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		            throw new DAOException(szRtnMsg);
				}
	        }
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        // 권하지시위치와 권하실적위치가 다르면 권하지시위치의 맵정보를 Clear시킴
	        // 보수장일때도 CLEAR하도록 변경 --> 보수장 권하시 새로운 저장위치 검색하도록 변경
	        // 가스장도 추가
	        //-------------------------------------------------------------------------------------------------------------------
            String sDnWoLoc = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC") + "-" + szYdDnWrLayer;
            String sDnWrLoc = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC") + "-" + szRealTopLyr;

            szMsg  = "[" + szOperationName + "] 권하지시위치 :: " + sDnWoLoc + "  권하실적위치 :: " + sDnWrLoc;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        if ("BC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "BS".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "CN".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || !sDnWoLoc.equals(sDnWrLoc)) {

                szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 시작 .. " + "지시::" + sDnWoLoc + ", 실적::" + sDnWrLoc;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        	// 크레인XYZ축이 없다면 지시정보의 XYZ축을 쓰도록한다.
	        	if ("".equals(szYdCrnXaxis) && "".equals(szYdCrnYaxis) && "".equals(szYdCrnZaxis)) {
	        		szYdCrnXaxis = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC_XAXIS");
	        		szYdCrnYaxis = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC_YAXIS");
	        		szYdCrnZaxis = ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC_ZAXIS");
	        	}

	        	intRtnVal = this.clearYdStklyrY2(getRecSet, szModifier);	// 권하 지시위치 Clear
	        	if (intRtnVal < 0) {
		            szRtnMsg = "권하 지시위치 Clear시 오류 .. " + Integer.toString(intRtnVal);
		            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
		            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		            throw new DAOException(szRtnMsg);
				}

    	        szMsg = "[" + szOperationName + "]권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 완료";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        }
            
	        //-------------------------------------------------------------------------------------------------------------------
	        //  권하실적LYR와 실제적치단의 최상위 위치가 다르면 권하실적LYR를 변경하고 적치단 정보를 수정한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        if (!szYdDnWrLayer.equals(szRealTopLyr)) {
	        	szMsg = "[" + szOperationName + "] 실적적치단[" + szYdDnWrLayer + "]과 실제야드적치단[" + szRealTopLyr + "]이 상이하여 실적적치단 변경 시작";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	            szYdDnWrLayer = szRealTopLyr;
    	    }

	        //-------------------------------------------------------------------------------------------------------------------
	        //	크레인작업재료의 적치단을 적치중으로 변경
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경 시작";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);


//---------------------------------------------------------------------------------------------
// 2024.12.06 regYdStklyrY2 call argument에 logId 추가  
//	        szRtnMsg = this.regYdStklyrY2(getRecSet, szModifier, arrStlNo, arrYdDnWrLoc, szYdUpWrkActGp, szRealTopLyr);
	        szRtnMsg = this.regYdStklyrY2(getRecSet, szModifier, arrStlNo, arrYdDnWrLoc, szYdUpWrkActGp, szRealTopLyr, logId);
//---------------------------------------------------------------------------------------------
	        
	        if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
		        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경 완료";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        } else {
		        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경 오류 발생 >>>> " + szRtnMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	            throw new DAOException(szRtnMsg);
	        }
            
            //-------------------------------------------------------------------------------------------------------------------
	        // 보수장일때 적치위치를 다시 조회 (보수장은 권하위치 틀림)
            //-------------------------------------------------------------------------------------------------------------------
	        if ("BC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "BS".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || "CN".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) || !sDnWoLoc.equals(sDnWrLoc)) {
            
		        setRecord.setField("STL_NO"	,	szStlNo);
		        setRecord.setField("YD_GP"	,	JPlateYdConst.YD_GP_P_PLATE_YARD);

		        rsResult = commDao.select(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithLoc", logId, szMethodName, "보수장일때 적치위치를 다시 조회 (보수장은 권하위치 틀림)");
		        if(rsResult.size() > 0){
		        	szYdDnWrLoc 	= rsResult.getRecord(0).getFieldString("YD_STK_COL_GP") + rsResult.getRecord(0).getFieldString("YD_STK_BED_NO");
		        	szYdDnWrLayer	= rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO");
		        }
	        }
            
            //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 시작";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        setRecord.setField("YD_CRN_SCH_ID",      	szCrnSchId);
	        setRecord.setField("YD_DN_WR_LOC",       	szYdDnWrLoc);
	        setRecord.setField("YD_DN_WR_LAYER",     	szYdDnWrLayer);
	        setRecord.setField("YD_DN_WRK_ACT_GP",   	getCrnschRecord.getFieldString("YD_DN_WRK_ACT_GP"));
	        setRecord.setField("YD_DN_WR_XAXIS",     	getCrnschRecord.getFieldString("YD_CRN_XAXIS"));
	        setRecord.setField("YD_DN_WR_YAXIS",     	getCrnschRecord.getFieldString("YD_CRN_YAXIS"));
	        setRecord.setField("YD_DN_WR_ZAXIS",     	getCrnschRecord.getFieldString("YD_CRN_ZAXIS"));
            if("S".equals(szYdPilingGp)) {
            	//S : RT상에 1매씩 분리 권하 **
            	setRecord.setField("YD_WRK_PROG_STAT",   	""); 
            } else {
            	setRecord.setField("YD_WRK_PROG_STAT",   	getCrnschRecord.getFieldString("YD_WRK_PROG_STAT")); //** L2에서 R일경우 어떻게 올라오는지??
            }
	        setRecord.setField("YD_DN_CMPL_DT",      	JPlateYdUtils.getCurDate("yyyyMMddHHmmss"));
	        setRecord.setField("YD_GP",   				getCrnschRecord.getFieldString("YD_GP"));
	        setRecord.setField("MODIFIER", 				szModifier);

	        //--------------------------------------------------------------------------------------------------
	        //	크레인스케줄 등록 시 야드작업계상일자에 등록되는 값을 권하완료시점에 다시 수정, 권하완료 시의 값이 사용됨
	        //	TO_CHAR(SYSDATE - (7 / 24), 'YYYYMMDD')
	        //--------------------------------------------------------------------------------------------------
	        String szYdWrkHdsDd = JPlateYdUtils.getDefaultHdsDate();
	        setRecord.setField("YD_WRK_HDS_DD",  		szYdWrkHdsDd);

	        szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "]의 야드작업계상일자[" + szYdWrkHdsDd + "]";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        
			intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnDnWr", logId, szMethodName, "크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트");

	        szMsg = "[" + szOperationName + "] 크레인스케줄[" + szCrnSchId + "] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 완료";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            szMsg = "[" + szOperationName + "] 권하실적위치의 설비구분 : " + ydUtils.substr(szYdDnWrLoc, 2, 2);
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            szMsg = "[" + szOperationName + "] 권하실적 스케줄코드 : " + szYdSchCd;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            
           
	        //-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 대차일때 대차 스케줄 이송재료 등록 후 대차스케줄 호출
	        //-------------------------------------------------------------------------------------------------------------------
	        if ("TC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) && "TC".equals(ydUtils.substr(szYdSchCd, 2, 2))) {
            
	            szMsg    = "[" + szOperationName + "] 권하실적위치가 대차";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	            
            }//-------------------------------------------------------------------------------------------------------------------
		    //	권하실적위치가 차량이므로 차량 스케줄 이송재료 등록 후 차량진행관리 호출 --니켈강 출하만 25.07.30 허동수책임 요청 RITM1291473
	        //-------------------------------------------------------------------------------------------------------------------
	        else if(("PT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))|| 
	        		"TR".equals(ydUtils.substr(szYdDnWrLoc, 2, 2)) 
	        		) && "PFPT01UM".equals(szYdSchCd)
	        		){
            	
            	intRtnVal = this.Y2SetYdCar(getRecSet, 1, logId) ; 

            	
            	szMsg = "차량이송재료 등록 완료";
            	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            	
            	// 차량 작업 진행관리 호출
            	recInTemp = JDTORecordFactory.getInstance().create();
            	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
            	recInTemp.setField("YD_WBOOK_ID",   szYdWbookId);
            	recInTemp.setField("CAR_LDUD_GP",   "U");
            	recInTemp.setField("YD_DN_WR_LOC", 	szYdDnWrLoc);
            	
				szMsg = "[" + szOperationName + "] 차량 작업 진행관리 호출";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				ydUtils.displayRecord(szOperationName, recInTemp);
	
				szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID");
				
				//크레인스케줄 작업재료
				recInTemp.setField("CRN_WRK_MTLS_SET",getRecSet);
				
				recInTemp.setField("LOG_ID", logId);  // 전문에 있는 logId


				String sNEW_MODULE_EFF_YN = "N";
				
				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A036"); //1후판정정야드 차량작업진행관리 신규모듈 적용 여부 
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 차량작업진행관리 신규모듈 적용 여부   : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
				
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
					intRtnVal = this.procY2CarWrkStatCtrNew(recInTemp);
				}
				
				else {
					intRtnVal = this.procY2CarWrkStatCtr(recInTemp);
				}
            
            }
	        
	        
	        //니켈강 출하 대상 입고대기인 대상이 니켈강베드로 이적시 입고전문 송신 --허동수 책임 요청  RITM1291473 
	        boolean isNiBed = false;
	        
	        if("PF0101".equals(szYdStkColGp)
               || "PF0102".equals(szYdStkColGp)
               || "PF0103".equals(szYdStkColGp)
               || "PF0199".equals(szYdStkColGp)) {
	        	
	        	szMsg    = "[" + szOperationName + "] 권하위치 ["+szYdStkColGp+"] 는 니켈강출하베드";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                

				String sNEW_MODULE_EFF_YN = "N";
				
				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
				
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A035"); //1후판정정야드 니켈강출하베드 권하시 입고처리 여부
				
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 니켈강출하베드 권하시 입고처리 여부  : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG);
                
				if("Y".equals(sNEW_MODULE_EFF_YN)){
					isNiBed = true;
					
					//공통저장위치 수정
					JDTORecord commRecord = JDTORecordFactory.getInstance().create();
					
					String ydGp    = szYdDnWrLoc.substring(0,1);
					String ydBayGp = szYdDnWrLoc.substring(1,2);
					String ydEqpGp = szYdDnWrLoc.substring(2,4);
					String ydStkColNo = szYdDnWrLoc.substring(4,6);
					
					String ydStrLoc  = ydGp + ydBayGp + ydEqpGp +ydStkColNo +szYdStkBedNo.substring(1,2) + szYdDnWrLayer;
					 /*
                     *  PLATE공통 저장위치 UPDATE
                     */
                    //-- NEW Version ---------------------------------------------------------
					commRecord.setField("YD_GP"				, ydGp);
					commRecord.setField("YD_BAY_GP"			, ydBayGp);
					commRecord.setField("YD_EQP_GP"			, ydEqpGp);
					commRecord.setField("YD_STK_COL_NO"		, ydStkColNo);
					commRecord.setField("YD_STK_BED_NO"		, szYdStkBedNo);
					commRecord.setField("YD_STK_LYR_NO"		, szYdDnWrLayer);
					commRecord.setField("YD_STR_LOC"		, ydStrLoc);
					commRecord.setField("FNL_REG_PGM"		, "Y2CrnDnWr");
					commRecord.setField("MODIFIER"			, "Y2CrnDnWr");
					commRecord.setField("PLATE_NO"			, ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"));

                    /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommLOC */
                    intRtnVal = ydStockDao.updPtComm_LOC(commRecord, 1);
                    
				}
	        }
	        //크레인작업재료중 니켈강 강종이고, 입고대기인 대상은 입고처리.
	        if(isNiBed){
		        getRecSet.first();
		        for(int ii=0; ii<getRecSet.size(); ii++) {
		        	getRecSet.absolute(ii+1);
			        getRecord = getRecSet.getRecord();

		        	String stlNo =  ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
		        	String stlProgCd = ydDaoUtils.paraRecChkNull(getRecord, "STL_PROG_CD");
		        	
		        	szMsg    = "[" + szOperationName + "] 권하재료 ["+stlNo+"] 는 니켈강 대상 및 입고대기 여부 확인 진도코드["+stlProgCd+"]";
	                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	                
	                JDTORecord recPara2		= JDTORecordFactory.getInstance().create();
	    			recPara2.setField("PLATE_NO"	, stlNo);    
	    			JDTORecordSet tempRecSet	= commDao.select(recPara2, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getChk9NiByStlNo", logId, szMethodName, "제품 9%니켈강여부 조회");
	    			if (tempRecSet.size() > 0) {
	    				tempRecSet.absolute(1);
	    				String szIS9NI  	= tempRecSet.getRecord().getFieldString("PL_NI_RMN_MAG_MEA_MTL_ASGN_GP");
	    				
	    				szMsg    = "[" + szOperationName + "] 권하재료 ["+stlNo+"] 니켈강 대상 확인 ["+szIS9NI+"]";
		                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	    				
	    				if("Y".equals(szIS9NI) && YdConstant.PROG_CD_RCPT_WAIT.equals(stlProgCd)){
	    					szMsg    = "[" + szOperationName + "] 권하재료 ["+stlNo+"] 는 니켈강 대상 및 입고대기 이므로 입고전문 송신";
	    	                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	    	                
	    	                JDTORecord outRec1  = JDTORecordFactory.getInstance().create();
							String curDate = YdUtils.getCurDate("yyyyMMddHHmmss");

							outRec1.setField("MQ_TC_CD"       	 , "M10YDLMJ1012");
							outRec1.setField("MQ_TC_CREATE_DDTT" , new String(curDate));
							
							outRec1.setField("YD_GP"          	 , YdConstant.YD_GP_PLATE_JJ_YARD);
							outRec1.setField("DIST_GOODS_GP"  	 , "P");
							outRec1.setField("YARD_GP" 		  	 , "");
							outRec1.setField("GOODS_NO"       	 , stlNo);
							outRec1.setField("STORE_LOC_CD"   	 , szYdDnWrLoc);
							
							outRec1.setField("RECEIPT_DATE"   	 , curDate.substring(0, 8));
							outRec1.setField("RECEIPT_TIME"   	 , curDate.substring(8, 14));
							outRec1.setField("YD_CRN_SCH_ID" , szCrnSchId);	
							
							YdDelegate mqDelegate = new YdDelegate();

		                    mqDelegate.sendMsg(outRec1);

	    				}
	    			}
		        }
	        
	        }
	        
            if ("".equals(ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID"))) {
	            szRtnMsg = "작업예약 ID가 없습니다.";
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                throw new DAOException(szRtnMsg);
	        }
	        
            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업 및 재료, 작업예약 및 재료정보를 Clear
	        //-------------------------------------------------------------------------------------------------------------------
            szMsg    = "[" + szOperationName + "] 크레인 작업재료 삭제처리 ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        //크레인 작업재료 삭제처리
	        setRecord.setField("DEL_YN",             "Y");
	        
            if("S".equals(szYdPilingGp)) {
            	//S : RT상에 1매씩 분리 권하 **
    			intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.delYdCrnWrkMtl2", logId, szMethodName, "크레인 작업재료 삭제처리 - RT상에 1매씩 분리 권하 **");
            } else {
    			intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.delYdCrnWrkMtl", logId, szMethodName, "크레인 작업재료 삭제처리");
            }
            
	        if (intRtnVal <= 0) {
	            szRtnMsg = "크레인 작업재료 삭제중 오류 발생!! Code : " + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                throw new DAOException(szRtnMsg);
	        }
	        
            szMsg    = "[" + szOperationName + "] 크레인스케줄처리 ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        //크레인스케줄 삭제처리
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("YD_DN_CMPL_DT",      JPlateYdUtils.getCurDate("yyyyMMddHHmmss")); //권하완료일시
	        
            if("S".equals(szYdPilingGp)) {
            	//S : RT상에 1매씩 분리 권하 **
    			//intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.delYdCrnSch2", logId, szMethodName, "크레인스케줄 삭제처리 - RT상에 1매씩 분리 권하 **");
            	intRtnVal = 1;
            } else {
    			intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.delYdCrnSch", logId, szMethodName, "크레인스케줄 삭제처리");
            }
            
	        if (intRtnVal < 0) {
	            szRtnMsg = "크레인스케줄 삭제시 오류 발생!!!, ErrorCode:" + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                throw new DAOException(szRtnMsg);
	        }
	        
            szMsg    = "[" + szOperationName + "] 작업예약완료 CHECK ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	        getRecord = JDTORecordFactory.getInstance().create();
	        getRecord.setField("YD_WBOOK_ID", szYdWbookId);
	        
			intRtnVal = this.getYdCrnSchByWrkIdY7(getRecord, getRecSet);
			if (intRtnVal < 0) {
	            szRtnMsg = "작업예약완료 CHECK 에러발생!!!, ErrorCode:" + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                throw new DAOException(szRtnMsg);
	        }

	        // 작업예약에 존재하는 크레인스케쥴 정보가 없을경우에 작업예약정보를 삭제한다.
	        if (getRecSet.size() == 0) {

	            szMsg    = "[" + szOperationName + "] 작업예약정보를 삭제 ---- START";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	            setRecord.setField("YD_WBOOK_ID",        szYdWbookId);
	            setRecord.setField("DEL_YN",             "Y");
	            setRecord.setField("YD_SCH_PROG_STAT",   "E");
	            setRecord.setField("MODIFIER",           szModifier);

                intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.delYdWrkbook", logId, szMethodName, "작업예약정보를 삭제");
                
	        	if (intRtnVal <= 0) {
		            szRtnMsg = "작업예약정보를 삭제시 오류 발생 !!!, ErrorCode:" + Integer.toString(intRtnVal);
		            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
	                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	                throw new DAOException(szRtnMsg);
	    		}

		        // 작업예약재료조회
            	getRecSet = commDao.select(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getYdWrkbookMtlId", logId, szMethodName, "작업예약재료조회");

	            // 조회한 작업예약재료1매씩 저장품 업데이트
				for(int ii = 0; ii < getRecSet.size(); ii++) {
					setRecord.setField("STL_NO", 		getRecSet.getRecord(ii).getFieldString("STL_NO"));
					setRecord.setField("MODIFIER",    	szModifier);
					setRecord.setField("YD_SCH_CD", 	"");
					setRecord.setField("YD_WBOOK_ID", 	"");
					setRecord.setField("YD_STK_COL_GP", szYdDnWrLoc.substring(0,6));
					setRecord.setField("YD_STK_BED_NO", szYdDnWrLoc.substring(6,8));
					setRecord.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt(szYdDnWrLayer, ii));
					intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStkColInfo", logId, szMethodName, "작업예약재료1매씩 저장품 업데이트");
				}

				setRecord.setField("DEL_YN",      "Y");
				setRecord.setField("MODIFIER",    szModifier);
				setRecord.setField("YD_WBOOK_ID", szYdWbookId);
				intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdWrkbookMtlDelYn", logId, szMethodName, "작업예약재료 삭제");

				szNextSchCallYn = "Y";	// 다음 스케줄 호출 Flag Set

				szMsg    = "[" + szOperationName + "] 작업 예약 처리 완료";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        } else {
	        	//작업예약 진행중이라도 주작업이면 해당 재료번호로 작업예약재료 정보를 찾아 DEL_YN = 'Y' 해야 한다.

				setRecord.setField("DEL_YN"		, "Y");
				setRecord.setField("MODIFIER"	, szModifier);
				setRecord.setField("YD_WBOOK_ID", szYdWbookId);
				setRecord.setField("STL_NOS"	, szStlNos);
				intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updYdWrkbookMtlDelYn2", logId, szMethodName, "작업예약재료 삭제2");
	        	
	        	szMsg    = "[" + szOperationName + "] 작업 예약 진행 중";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        }
	        
			//------------------------------------------------------------------
			// FROM 위치가 대차일 경우 YD_WRK_PLAN_TCAR(야드작업계획대차) 항목  CLEAR 처리
			//------------------------------------------------------------------
			if ("TC".equals(ydUtils.substr(szYdUpWrLoc, 2, 2)) && !"TC".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {
				for(int ii=0; ii<arrStlNo.length; ii++) {
					if (!"".equals(arrStlNo[ii])) {
						
						setRecord.setField("STL_NO",         	arrStlNo[ii]);
						setRecord.setField("YD_WRK_PLAN_TCAR", 	"");
						setRecord.setField("REGISTER",       	szModifier);
						setRecord.setField("MODIFIER",       	szModifier);
						/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdWrkPlanTcar 
						-- 야드작업계획대차 UPDATE (대차하차위치)

						UPDATE TB_YD_SHRSTOCK
						   SET MODIFIER         = :V_MODIFIER
						     , MOD_DDTT         = SYSDATE
						     , YD_WRK_PLAN_TCAR = SUBSTR(:V_YD_WRK_PLAN_TCAR, 1, 6)
						 WHERE STL_NO           = :V_STL_NO
						*/ 
						intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdWrkPlanTcar", logId, szMethodName, "FROM 위치가 대차일 경우 YD_WRK_PLAN_TCAR(야드작업계획대차) 항목  CLEAR 처리");
			    		if (intRtnVal < 1) {
							szMsg = "야드작업계획대차 항목 CLEAR 실패 >>>> " + Integer.toString(intRtnVal);
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    		}
					}
				}
			}
			
	        //-------------------------------------------------------------------------------------------------------------------
		    //	인터페스 송신처리
	        //  각각의 I/F 송신조건은 다시 체크 요망.
	        //-------------------------------------------------------------------------------------------------------------------
			
	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 1후판조업 저장위치변경정보 전송  - YDPRJ011
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            szMsg    = "[" + szOperationName + "] 1후판조업 저장위치변경정보 전송 ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            if(ydUtils.substr(szYdUpWrLoc,0,6).equals(ydUtils.substr(szYdDnWrLoc,0,6))) {
            	//권상위치열과 권하위치열이 동일하면 YDPRJ011을 전송하지 않는다.
                szMsg    = "[" + szOperationName + "] 권상위치열과 권하위치열이 동일하면 YDPRJ011을 전송하지 않는다! 권상위치[" + szYdUpWrLoc + "] 권하위치[" + szYdDnWrLoc + "]";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                
            } else if("PCBS".equals(ydUtils.substr(szYdUpWrLoc,0,4)) && "PCBS".equals(ydUtils.substr(szYdDnWrLoc,0,4))) {
            	//권상위치가 #1보수장이고 권하위치도 #1보수장이면 YDPRJ011을 전송하지 않는다.
                szMsg    = "[" + szOperationName + "] 권상위치가 #1보수장이고 권하위치도 #1보수장이면 YDPRJ011을 전송하지 않는다! 권상위치[" + szYdUpWrLoc + "] 권하위치[" + szYdDnWrLoc + "]";
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                
            } else {
		        recInTemp = JDTORecordFactory.getInstance().create();
		        recInTemp.setField("MSG_ID", 			"YDPRJ011"						);
		        recInTemp.setField("YD_STK_COL_FR", 	ydUtils.substr(szYdUpWrLoc,0,6)	);		// From적치열
		        recInTemp.setField("YD_STK_BED_FR", 	ydUtils.substr(szYdUpWrLoc,6,2)	);		// From적치BED
		        recInTemp.setField("YD_STK_COL_TO", 	ydUtils.substr(szYdDnWrLoc,0,6)	);		// TO적치열
		        recInTemp.setField("YD_STK_BED_TO", 	ydUtils.substr(szYdDnWrLoc,6,2)	);		// TO적치BED
		        recInTemp.setField("YD_EQP_WRK_SH", 	""								);		// 야드설비작업매수
		        recInTemp.setField("ARR_STL_NO", 		szArrStlNo						);
		        recInTemp.setField("PL_BOOK_OUT_CRANE",	szYdEqpId						);		//북아웃 작업크레인(예:PBCRB1) chito20230202
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recInTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
		        recInTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
		              	
		        szSendMsg = JPlateYdCommonUtils.sendL3YDPRJ011(recInTemp);

				szMsg = "[" + szOperationName + "] 후판조업 저장위치변경정보 전송 완료>>>>" + szSendMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
            }
            
            if("S".equals(szYdPilingGp)) {
            	//S : RT상에 1매씩 분리 권하 **
            	//작업지시 재전송
            	
    			szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 시작";
    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    			
    			recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("MSG_ID",					"YDY2L004V2"						);		// 크레인 작업지시
    			recInTemp.setField("YD_CRN_SCH_ID",    			szCrnSchId							);
    			recInTemp.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_DN_WO	);		// 권하지시
    			recInTemp.setField("MSG_GP",           			"U"									);
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recInTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
    			recInTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
		              	
    	        szSendMsg = ydDelegate.sendMsg(recInTemp);
    			
    			szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 종료 :: " + szSendMsg;
    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            } else {
	        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 			1후판정정야드L2 크레인작업실적응답 전송  - YDY2L005
		         * 업무기준 Desc : 크레인 권하실적처리 성공 후 크레인작업실적응답 전송
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	            szMsg    = "[" + szOperationName + "] 크레인작업실적응답 전송 ---- START";
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
		        recInTemp = JDTORecordFactory.getInstance().create();
		        recInTemp.setField("MSG_ID", 			"YDY2L005");
		        recInTemp.setField("YD_EQP_ID", 		szYdEqpId);										//야드설비ID
		        recInTemp.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_CMPL);				//야드작업진행상태
		        recInTemp.setField("YD_SCH_CD", 		szYdSchCd);										//야드스케줄코드
		        recInTemp.setField("YD_CRN_SCH_ID", 	szCrnSchId);									//야드크레인스케줄ID
	        	recInTemp.setField("YD_L2_WR_GP", 		JPlateYdConst.CRN_WRK_RE_DN_WR);				//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
		        recInTemp.setField("YD_L3_HD_RS_CD", 	JPlateYdConst.CRN_WRK_RE_CD_NORMAL_HD);			//야드L3처리결과코드
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recInTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
		        recInTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
		              	
		        szSendMsg = ydDelegate.sendMsg(recInTemp);
	
				szMsg = "[" + szOperationName + "] 1후판정정야드L2 크레인작업실적응답[YDY2L005] 전송 완료>>>>" + szSendMsg;
	            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            }
 
        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 1후판정정야드L2 저장품제원 전송  - YDY2L002
	         * RT - BOOK-IN 권하 완료후
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            msgRecord.setField("CARD_NO", szCARD_NO); // L3 화면에서 만들어진 지시여부 
            
			if ("RT".equals(ydUtils.substr(szYdDnWrLoc, 2, 2))) {

    	        if(szYdSchCd.substring(0,2).equals("PB")||szYdDnWrLoc.startsWith("PART13")||szYdDnWrLoc.startsWith("PART9")||szYdDnWrLoc.startsWith("PCRT40") ) {
    	        	//열처리 L2
    	        	
    				String sNEW_MODULE_EFF_YN = "N";

    				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
    				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A008"); //1후판정정야드 열처리L2 Book-In/Book-Out실적 신규모듈적용여부 
    				
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(YDP3L501)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
    	        	
    				if(sNEW_MODULE_EFF_YN.equals("Y")) {
    					//신규 메소드 호출
        				this.procRtBookInY2V2(msgRecord, arrStlNo, szYdPilingGp);
    				} else {
    					//기존 메소드 호출
        				this.procRtBookInY2(msgRecord, arrStlNo);
    				}
//                } else if("PFRT21UM".equals(szYdSchCd)) {
////-------------------------------------------------------------------------------------------------------------------------
//// 2024.12.20 스케쥴코드가 PFRT21UM(F동 2SB 입측 BOOK-IN(0021N)) 경우 YDP2L501 전문 전송 하지 않음 (허정욱 책임) 
//// 2024.12.23 스케쥴코드가 PFRT21UM(F동 2SB 입측 BOOK-IN(0021N)) 경우 YDP2L501 전문 전송 (허정욱 책임) 
////-------------------------------------------------------------------------------------------------------------------------
//                    szMsg    = "[" + szOperationName + "] 스케쥴코드가 PFRT21UM(F동 2SB 입측 BOOK-IN(0021N)) 경우 YDP2L501 전문 전송 하지 않음! 스케쥴코드[" + szYdSchCd + "]";
//                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
//                    
//    	        	
    	        }
    	        //25.02.`8 #2 열처리로 추가사항. 쇼트/열처리 권하시 book-in 실적 전송 
    	        else if (szYdDnWrLoc.startsWith("PART31") || szYdDnWrLoc.startsWith("PART32") || szYdDnWrLoc.startsWith("PART23") || szYdDnWrLoc.startsWith("PFRT21")){
    	        	
    	        	String sNEW_MODULE_EFF_YN = "N";

    				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
    				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A008"); //1후판정정야드 열처리L2 Book-In/Book-Out실적 신규모듈적용여부 
    				
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(YDP3L501)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
    	        	
    				if(sNEW_MODULE_EFF_YN.equals("Y")) {
    					//신규 메소드 호출
        				this.procRtBookInY2V2(msgRecord, arrStlNo, szYdPilingGp);
    				} else {
    					//기존 메소드 호출
        				this.procRtBookInY2(msgRecord, arrStlNo);
    				}
    	        }
    	        
    	        
    	        else {
    	        	//전단 L2
    	        	
    				String sNEW_MODULE_EFF_YN = "N";

    				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
    				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A007"); //1후판정정야드 전단L2 Book-In/Book-Out실적 신규모듈적용여부 
    				
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(YDP2L501)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
    	        	
    				if(sNEW_MODULE_EFF_YN.equals("Y")) {
    					//신규 메소드 호출
        				this.procRtBookInY2V2(msgRecord, arrStlNo, szYdPilingGp);
    				} else {
    					//기존 메소드 호출
        				this.procRtBookInY2(msgRecord, arrStlNo);
    				}
    	        }
				
    	        
			}
            
            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인 설비상태 권하완료(4) 셋팅.
	        //-------------------------------------------------------------------------------------------------------------------
            szMsg    = "[" + szOperationName + "] 설비상태 권하완료 변경 ---- START";
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            setRecord.setField("YD_EQP_ID", 		szYdEqpId);
            setRecord.setField("YD_EQP_STAT", 		"4");
            setRecord.setField("MODIFIER", 			szModifier);
            intRtnVal = commDao.update(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpStat", logId, szMethodName, "크레인 설비상태 권하완료(4) 셋팅");

            if (intRtnVal <= 0) {
	            szRtnMsg = "설비상태 UPDATE 처리시 오류 발생 :: " + Integer.toString(intRtnVal);
	            szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			 	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			 	throw new DAOException(szRtnMsg);
	   		}
            
	        //설비id로 설비Table조회
            setRecord.setField("YD_EQP_ID", szYdEqpId);
	        rsResult = commDao.select(setRecord, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdEqp", logId, szMethodName, "설비id로 설비Table조회");
            
			szMsg = "[" + szOperationName + "] 이력테이블등록호출 ---- START";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// 1후판 이력테이블등록호출
	        JPlateYdCrnSchYdPSeEJBBean crnSchYdPSeEJBBean = new JPlateYdCrnSchYdPSeEJBBean();

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",             ""				);
			recInTemp.setField("YD_WBOOK_ID",        szYdWbookId	);
			recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId		);
			recInTemp.setField("YD_CAR_SCH_ID",      szYD_CAR_SCH_ID);		// szYdCarSchId
			recInTemp.setField("YD_TCAR_SCH_ID",     szYdTcarSchId	);
			recInTemp.setField("YD_WTCL_TNK_SCH_ID", ""				);
			recInTemp.setField("REGISTER",           szModifier		);
			recInTemp.setField("MODIFIER",           szModifier		);
			recInTemp.setField("DEL_YN",             "N"			);
			recInTemp.setField("STL_NOS", 			 szStlNos		);
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recInTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
			recInTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
       	
			crnSchYdPSeEJBBean.procWorkHistoryCreate2(recInTemp);

			szMsg = "[" + szOperationName + "] 이력테이블등록호출 END";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	        
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",           	"YDYDJX55");			// YDYDJ755 :: 크레인 작업지시요구
			recInTemp.setField("YD_EQP_ID",        	rsResult.getRecord(0).getFieldString("YD_EQP_ID"));
			recInTemp.setField("YD_EQP_WRK_MODE",  	rsResult.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			recInTemp.setField("YD_WRK_PROG_STAT", 	"4");
			recInTemp.setField("YD_SCH_CD",        	szYdSchCd);
			recInTemp.setField("YD_CRN_SCH_ID",    	"");
			recInTemp.setField("YD_CRN_XAXIS",     	"");
			recInTemp.setField("YD_CRN_YAXIS",     	"");
			recInTemp.setField("RTN_CD",     		JPlateYdConst.RETN_CD_SUCCESS);

			
	        //-------------------------------------------------------------------------------------------------------------------
	        // 00011 Zone (PBRTWB)에 권하한 경우는 00012 Zone 에 Book-Out 요구 처리 된 것처럼 크레인 스케줄을 생성한다.
	        //-------------------------------------------------------------------------------------------------------------------
			if (szYdDnWrLoc.startsWith("PBRTWB")) {
				
				String sNEW_MODULE_EFF_YN = "N";

				JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
				sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A009"); //1후판정정야드 00011존 권하시 00012 존 Book-Out 자동실행여부
				
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(00012)---[[[ 1후판정정야드신규적용 : " + sNEW_MODULE_EFF_YN + " ]]]---", JPlateYdConst.DEBUG, logId);
	        	
				if(sNEW_MODULE_EFF_YN.equals("Y")) {
					//신규 메소드 호출
				
					szMsg = "[" + szOperationName + "] 00012 Zone 에 Book-Out 요구 처리 된 것처럼 크레인 스케줄을 생성 ==> " + szYdPilingGp;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					
					recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId	);
					recInTemp.setField("REGISTER",           szModifier	);
					recInTemp.setField("MODIFIER",           szModifier	);
					recInTemp.setField("ARR_STL_NO", 		 szArrStlNo	);
					recInTemp.setField("LOG_ID", 		 	 logId		);
					
					if("H".equals(szYdPilingGp)) {
						this.procMake00012ZoneSchNY2(recInTemp);
					} else {
						this.procMake00012ZoneSchY2(recInTemp);
					}
				}
			}

	        //-------------------------------------------------------------------------------------------------------------------
	        // 56020 파일링 명령 시, 파일링 실적처리 IF 호출 
	        //-------------------------------------------------------------------------------------------------------------------
			if(szYdSchCd.equals("PFRTAPLM") || szYdSchCd.equals("PFRTAPUM")){
				EJBConnector ejbConn = null;
				ejbConn = new EJBConnector("default", this);			
				//1후판용 파일링 실적 호출
				//TC CCODE
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("JMS_TC_CD",			"P2YDL001");
				
				int stlCnt=0;
				for(int i=0; i<arrStlNo.length;i++){
					if(!"".equals(arrStlNo[i])){
						stlCnt++;
					}
				}
				//전달할 전문 편집
				for(int Loopi=0; Loopi<stlCnt; Loopi++){
					//재료번호
					//recInTemp.setField("PL_PLATE_NO" + (Loopi+1), arrStlNo[Loopi]);
					recInTemp.setField("PL_PLATE_NO" + (Loopi+1), arrStlNo[stlCnt-(Loopi+1)]);
				}
				
				recInTemp.setField("PL_GDS_PILNG_SH","" + stlCnt);
				
				ejbConn.trx("RcptWrkDmdSeEJB", "procP2PillingWr", recInTemp);
			}
			
			
			
			//호출
			//ejbConn.trx("RcptWrkDmdSeEJB", "procP2PillingWr", recPara);
			
	        //-------------------------------------------------------------------------------------------------------------------
	        // 해당 스케줄코드로 다음 스케줄기동
	        //-------------------------------------------------------------------------------------------------------------------
			if ("Y".equals(szNextSchCallYn)) {
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_EQP_ID",     szYdEqpId	);		// 설비ID
				recInTemp.setField("YD_SCH_CD",     szYdSchCd	);		// 스케줄코드
				recInTemp.setField("REGISTER",      szModifier	);
				recInTemp.setField("MODIFIER",      szModifier	);
				recInTemp.setField("YD_UP_WR_LOC",	szYdUpWrLoc	);		// 권상위치
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.09 recInTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
				recInTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				        	
				this.procNextYdCrnSchY2(recInTemp);
			}
			
    	} catch(Exception e) {
	    	/*
	    	 * Exception 발생시에도 작업실적 응답은 송신.
	    	 */
        	if ("".equals(szRtnMsg) || JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        		szRtnMsg = e.getMessage();
        	}
        	if (szRtnMsg != null && szRtnMsg.length() > 100) {
        		szRtnMsg = ydUtils.substr(szRtnMsg, 0, 100);
        	}
			szMsg = "[" + szOperationName + "] 권하실적처리시 Exception 발생 >>>>" + e.getMessage();
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("MSG_ID", 			"YDY2L005"								);
	        recInTemp.setField("YD_EQP_ID", 		szYdEqpId								);		// 야드설비ID
	        recInTemp.setField("YD_WRK_PROG_STAT", 	JPlateYdConst.YD_EQP_STAT_DN_CMPL		);		// 야드작업진행상태
	        recInTemp.setField("YD_SCH_CD", 		szYdSchCd								);		// 야드스케줄코드
	        recInTemp.setField("YD_CRN_SCH_ID", 	szCrnSchId								);		// 야드크레인스케줄ID
        	recInTemp.setField("YD_L2_WR_GP", 		JPlateYdConst.CRN_WRK_RE_DN_WR		);		// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        recInTemp.setField("YD_L3_HD_RS_CD", 	JPlateYdConst.CRN_WRK_RE_CD_ERROR		);		// 야드L3처리결과코드
	        recInTemp.setField("YD_L3_MSG",			szRtnMsg								);
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recInTemp에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
	        recInTemp.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
			String sendMsg = ydDelegate.sendMsg(recInTemp);

			szMsg = "[" + szOperationName + "] 1후판정정야드L2 크레인작업실적응답[YDY2L005] 전송 완료 >>>>" + sendMsg;
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	return szRtnMsg;
    	}
		
	    return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of procY2CrnDnWr2()
    
    /**
     * 오퍼레이션명 : 1후판정정 권하위치가 RT일경우 북인 실적 전송 - 신규
     *
     * @return ● intRtnVal
     * @throws ● DAOException
     */
    public String procRtBookInY2V2(JDTORecord msgRecord, String[] pArrStlNo, String pYdPilingGp) throws DAOException {

    	JPlateYdDelegate      	ydDelegate		= new JPlateYdDelegate();
    	JPlateYdStockDAO      	ydStockDao      = new JPlateYdStockDAO();
    	JPlateYdStkLyrDAO     	ydStkLyrDao     = new JPlateYdStkLyrDAO();
    	JPlateYdCrnWrkMtlDAO  	ydCrnWrkMtlDao	= new JPlateYdCrnWrkMtlDAO();

        int intRtnVal = 0;

        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord 		getRecord   = null;
        JDTORecord 		recPara		= null;
        JDTORecordSet 	rsResult    = null;

        String 	szMsg            	= "";
        String 	szRtnMsg            = "";
        String 	szSendMsg           = "";
        String 	szMethodName     	= "procRtBookInY2V2";
        String 	szOperationName  	= "1후판정정 BookIn실적처리2";

        String	szModifier			= "";
        String	szStlNo				= "";
        String	szCrnSchId			= "";
        String 	szYdStkColGp		= "";
        String 	szYdStkBedNo     	= "";
        String	szStlNoList			= "";
        String  szDnLoc             = "";
        String  szDelYn				= "";
        
        String szSchCd         		= "";
        
        String  szCARD_NO			= null;
        
        int     iMtlCnt = 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
        
    	try {

			// ------------------------------------------------------------------------
			// 조업 L2 북인 실적 전송 .. 순서가 뒤바뀌는 경우가 존재하여 일괄 전송처리
			// ------------------------------------------------------------------------
    		if (pArrStlNo != null && pArrStlNo.length > 0) {
				szMsg = "[" + szOperationName + "] RT BOOK-IN 실적 일괄전송 .. 시작";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    		for (int ii=0; ii<pArrStlNo.length; ii++) {
	    			if (!"".equals(pArrStlNo[ii])) {
		    			if ("".equals(szStlNoList)) {
		    				szStlNoList = pArrStlNo[ii];
		    			} else {
		    				szStlNoList = szStlNoList + ";" + pArrStlNo[ii];
		    			}
		    			iMtlCnt++;
	    			}
	    		}
	    		
	    		szDnLoc = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC"	);
	    		szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO"		);	//L3 화면에서 만들어진 지시여부 
	    		
	    		szSchCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"		);	// 스케줄코드명
	    		
	    		szMsg = " 1 후판전단L2 온라인RT 실적 전송 .. 저장위치>>>>" + szDnLoc;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				recPara = JDTORecordFactory.getInstance().create();
				
				//if(szDnLoc.startsWith("PBRTWB")) {
					// 전문송신 없슴
				//}else{
				

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 #2 열처리 YDP8L501(Bookin/out complete) 송신 추가 
//			  야드스케쥴코드(YD_SCH_CD) BOOK-IN - PART31UM, PART32UM, PART34UM, PART35UM
// 2024.12.24 권하 실적시 (A동 2SB 출측 BOOK-OUT(PART23LM) AND (권하위치 PART31 OR PART32) YDP8L501(Bookin/out complete) 송신 추가 
//-------------------------------------------------------------------------------------------------------------------------
		        	szMsg = "[" + szOperationName + "] 야드스케쥴코드 [" + szSchCd + "]";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
			        if(ydUtils.is2ndHeatBookInSchdule( szSchCd,  szDnLoc)) {
						
						if("H".equals(pYdPilingGp)) {
							// 횡작업인 경우 1건씩 YDP8L501 전송
							for (int ii=0; ii<pArrStlNo.length; ii++) {
								if (!"".equals(pArrStlNo[ii])) {
									recPara.setField("MSG_ID", 		        "YDP8L501"															);
									recPara.setField("STL_NO",				""																	);	// 재료번호
									recPara.setField("STL_NO_LIST",			pArrStlNo[ii]														);	// 재료번호 1건
									recPara.setField("OPERATION_TYPE",		"1"																	);	// 1 : Book In, 2 : Book Out
									recPara.setField("YD_STK_COL_GP",		ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC").substring(0,6)	);	// TO위치
									recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo														);	// 야드적치BED번호
									recPara.setField("YD_EQP_ID", 		    ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)	);		
									recPara.setField("CARD_NO", 			szCARD_NO															);	// L3 화면에서 만들어진 지시여부 
				        	        
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recPara에 logId 추가 
									recPara.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
									        	        
									szSendMsg = ydDelegate.sendMsg(recPara);
								}
							}
							
						} else {
							recPara.setField("MSG_ID", 		        "YDP8L501");
							recPara.setField("STL_NO",				""																	);	// 재료번호
							recPara.setField("STL_NO_LIST",			szStlNoList															);	// 재료번호 LIST
							recPara.setField("OPERATION_TYPE",		"1"																	);	// 1 : Book In, 2 : Book Out
							recPara.setField("YD_STK_COL_GP",		ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC").substring(0,6)	);	// TO위치
							recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo														);	// 야드적치BED번호
							recPara.setField("YD_EQP_ID", 		    ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)	);		
							recPara.setField("CARD_NO", 			szCARD_NO															);	// L3 화면에서 만들어진 지시여부 
		        	        
//-------------------------------------------------------------------------------------------------------------------------
//2024.11.21 recPara에 logId 추가 
							recPara.setField("LOG_ID", 			logId 									);      // logId
//-------------------------------------------------------------------------------------------------------------------------
		
							szSendMsg = ydDelegate.sendMsg(recPara);
						}
						szMsg = "[" + szOperationName + "] RT BOOK-IN 실적 전송 .. 완료>>>>" + szSendMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					
					
			        } else if(szDnLoc.startsWith("PBRT")||szDnLoc.startsWith("PART13")||szDnLoc.startsWith("PART9")||szDnLoc.startsWith("PCRT40")) {
							
						if("H".equals(pYdPilingGp)) {
							//횡작업인 경우 1건씩 YDP3L501 전송
							for (int ii=0; ii<pArrStlNo.length; ii++) {
								if (!"".equals(pArrStlNo[ii])) {
									recPara.setField("MSG_ID", 		        "YDP3L501V2"														);
									recPara.setField("STL_NO",				""																	);	// 재료번호
									recPara.setField("STL_NO_LIST",			pArrStlNo[ii]														);	// 재료번호 1건
									recPara.setField("OPERATION_TYPE",		"1"																	);	// 1:Book In, 2:Book Out
									recPara.setField("YD_STK_COL_GP",		ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC").substring(0,6)	);	// TO위치
									recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo														);	// 야드적치BED번호
									recPara.setField("YD_EQP_ID", 		    ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)	);		
									recPara.setField("CARD_NO", 			szCARD_NO															);	// L3 화면에서 만들어진 지시여부 
				
									szSendMsg = ydDelegate.sendMsg(recPara);
								}
							}
							
						} else {
							recPara.setField("MSG_ID", 		        "YDP3L501V2"														);
							recPara.setField("STL_NO",				""																	);	// 재료번호
							recPara.setField("STL_NO_LIST",			szStlNoList															);	// 재료번호 LIST
							recPara.setField("OPERATION_TYPE",		"1"																	);	// 1:Book In, 2:Book Out
							recPara.setField("YD_STK_COL_GP",		ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC").substring(0,6)	);	// TO위치
							recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo														);	// 야드적치BED번호
							recPara.setField("YD_EQP_ID", 		    ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)	);		
							recPara.setField("CARD_NO", 			szCARD_NO															);	// L3 화면에서 만들어진 지시여부 
		
							szSendMsg = ydDelegate.sendMsg(recPara);
						}
						szMsg = "[" + szOperationName + "] RT BOOK-IN 실적 전송 .. 완료>>>>" + szSendMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						
					} else if(szDnLoc.startsWith("PFRT50")) {// 56015 존 온라인 횡행작업 I/F 반영 (구시스템 F동 56015) --> 신시스템 안정화 후 삭제필요
						
						String[] pArrBedNo = {"PFRT50","PFRT49","PFRT48"}; 
						
						for (int ii=0; ii<pArrStlNo.length; ii++) {
			    			if (!"".equals(pArrStlNo[ii])) {
			    				
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
			        	                                                          ,pArrBedNo[ii]
			        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
			        	        		                                          ,szCARD_NO);
	
			    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 56015 존 온라인RT BOOK-IN 실적 전송 .. 완료>>>>" + szSendMsg;
			                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    			}
			    		}
						
					} else if(szDnLoc.startsWith("PFRT55")) {// 56020 존 온라인 횡행작업 I/F 반영
						
						//String[] pArrBedNo = {"PFRT55","PFRT50","PFRT49","PFRT48"};
						String[] pArrBedNo = {"PFRT55","PFRT50","PFRT49","PFRT48","PFRT47"}; //파일링시 최대 5매이므로 PFRT47존 추가
						
						for (int ii=0; ii<pArrStlNo.length; ii++) {
			    			if (!"".equals(pArrStlNo[ii])) {
			    				if("PFRTAPUM".equals(szSchCd)){
			    					if(ii==(iMtlCnt-1))  //마지막장만 처리
			    					{
				        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV4("1", pArrStlNo[ii]
				 				        	                                          ,szDnLoc.substring(0,6) //오토파일링시는 56020존(PFRT55 ZONE 전용 ZONE 설정)  //pArrBedNo[ii]
				 				        	        		                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
				 				        	        		                          ,szCARD_NO, szSchCd);
				        	        
				    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 56020 존 온라인RT BOOK-IN 실적 전송 .. 완료>>>>" + szSendMsg;
				                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    					}
			    				}
			    				else
			    				{
				        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
				        	                                                          ,pArrBedNo[ii]
				        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
				        	        		                                          ,szCARD_NO);
				        	        
				    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 56020 존 온라인RT BOOK-IN 실적 전송 .. 완료>>>>" + szSendMsg;
				                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    				}
			    			}
			    		}
						
					} else if(szDnLoc.startsWith("PFRT30")) {// 58020 존 온라인 횡행작업 I/F 반영
						
						String[] pArrBedNo = {"PFRT30","PFRT29","PFRT28"}; // 58020, 58015, 58010 
						
						for (int ii=0; ii<pArrStlNo.length; ii++) {
			    			if (!"".equals(pArrStlNo[ii])) {
			    				
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
			        	                                                          ,pArrBedNo[ii]
			        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
			        	        		                                          ,szCARD_NO);
	
			    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 58020 존 온라인RT BOOK-IN 실적 전송 .. 완료>>>>" + szSendMsg;
			                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    			}
			    		}
						
					} else if(szDnLoc.startsWith("PFRT60")) {// 59020 존 온라인 횡행작업 I/F 반영
						
						String[] pArrBedNo = {"PFRT60","PFRT59","PFRT58"}; // 59020, 59015, 59010 
						
						for (int ii=0; ii<pArrStlNo.length; ii++) {
			    			if (!"".equals(pArrStlNo[ii])) {
			    				
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
			        	                                                          ,pArrBedNo[ii]
			        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
			        	        		                                          ,szCARD_NO);
	
			    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 59020 존 온라인RT BOOK-IN 실적 전송 .. 완료>>>>" + szSendMsg;
			                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    			}
			    		}
						
					} else if(szDnLoc.startsWith("PERT70")) {// 49930 존 온라인 횡행작업 I/F 반영  - #2검사 출측
						
						String sNEW_MODULE_EFF_YN = "N";
						
						JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
						
						sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A019"); //1후판정정야드 E동 RT BOOK-IN시 야드 01BED 위치가 제품창고방향인지 여부  
						
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 E동 RT BOOK-IN시 야드 01BED 위치가 제품창고방향인지 여부  : " + sNEW_MODULE_EFF_YN+ " ]]]---", JPlateYdConst.DEBUG, logId);
						
						if(sNEW_MODULE_EFF_YN.equals("Y")) {
							//01 BED 가 제품창고 방향이다 (F동과 동일) 
						
							String[] pArrBedNo = {"PERT70","PERT69","PERT68"}; 
							
							for (int ii=0; ii<pArrStlNo.length; ii++) {
				    			if (!"".equals(pArrStlNo[ii])) {
				    				
				        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
				        	                                                          ,pArrBedNo[ii]
				        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
				        	        		                                          ,szCARD_NO);
							
				    		        //szMsg = "[" + szOperationName + "] 1 후판전단L2 온라인RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
				                    //ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				    			}
				    		}
						} else {
							//01 BED 가 압연공장 방향이다 (F동과 반대)
							
							if(iMtlCnt == 1) {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PERT70" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							} else if(iMtlCnt == 2) {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[1] ,"PERT70" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PERT69" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							} else {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[2] ,"PERT70" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[1] ,"PERT69" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PERT68" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							}
						}
	    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 49930 존 RT BOOK-IN 실적 전송 .. 완료>>>>" + iMtlCnt;
	                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					} else if(szDnLoc.startsWith("PERT75")) {// 49935 존 온라인 횡행작업 I/F 반영  - #2UT 입측
						
						String sNEW_MODULE_EFF_YN = "N";
						
						JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
						
						sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A019"); //1후판정정야드 E동 RT BOOK-IN시 야드 01BED 위치가 제품창고방향인지 여부  
						
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 E동 RT BOOK-IN시 야드 01BED 위치가 제품창고방향인지 여부  : " + sNEW_MODULE_EFF_YN+ " ]]]---", JPlateYdConst.DEBUG, logId);
						
						if(sNEW_MODULE_EFF_YN.equals("Y")) {
							//01 BED 가 제품창고 방향이다 (F동과 동일) 
						
							String[] pArrBedNo = {"PERT75","PERT70","PERT69","PERT68"}; 
							
							for (int ii=0; ii<pArrStlNo.length; ii++) {
				    			if (!"".equals(pArrStlNo[ii])) {
				    				
				        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
				        	                                                          ,pArrBedNo[ii]
				        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
				        	        		                                          ,szCARD_NO);
							
				    		        //szMsg = "[" + szOperationName + "] 1 후판전단L2 온라인RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
				                    //ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				    			}
				    		}
						} else {
							//01 BED 가 압연공장 방향이다 (F동과 반대)
							
							if(iMtlCnt == 1) {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PERT75" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							} else if(iMtlCnt == 2) {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[1] ,"PERT75" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PERT70" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							} else {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[2] ,"PERT75" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[1] ,"PERT70" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PERT69" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							}
						}
	    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 49935 존 RT BOOK-IN 실적 전송 .. 완료>>>>" + iMtlCnt;
	                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	                    
					} else if(szDnLoc.startsWith("PERT50")) {// 47905 존 온라인 횡행작업 I/F 반영  
						
						String sNEW_MODULE_EFF_YN = "N";  
						
						JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
						
						sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A025"); //1후판정정야드 E동 47905존 BOOK-IN시 야드 01BED 위치가 제품창고방향인지 여부  
						
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 E동 47905존 BOOK-IN시 야드 01BED 위치가 제품창고방향인지 여부  : " + sNEW_MODULE_EFF_YN+ " ]]]---", JPlateYdConst.DEBUG, logId);
						
						if(sNEW_MODULE_EFF_YN.equals("Y")) {
							//01 BED 가 제품창고 방향이다 (F동과 동일) 
						
							String[] pArrBedNo = {"PERT50","PERT49"}; 
							
							for (int ii=0; ii<pArrStlNo.length; ii++) {
				    			if (!"".equals(pArrStlNo[ii])) {
				    				
				        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
				        	                                                          ,pArrBedNo[ii]
				        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
				        	        		                                          ,szCARD_NO);
							
				    		        //szMsg = "[" + szOperationName + "] 1 후판전단L2 온라인RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
				                    //ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				    			}
				    		}
						} else {
							//01 BED 가 압연공장 방향이다 (F동과 반대)
							
							if(iMtlCnt == 1) {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PERT50" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							} else if(iMtlCnt == 2) {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[1] ,"PERT50" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PERT49" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							} else {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[2] ,"PERT50" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[1] ,"PERT49" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        //szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PERT49" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							}
						}
	    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 47905 존 RT BOOK-IN 실적 전송 .. 완료>>>>" + iMtlCnt;
	                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	                    
					} else if(szDnLoc.startsWith("PCRT70")) {// 54020 존  횡행작업 I/F 반영 
						
						String sNEW_MODULE_EFF_YN = "N";
						
						JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
						
						sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A022"); //1후판정정야드 C동 RT BOOK-IN시 야드 01BED 위치가 제품창고방향인지 여부  
						
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn()---[[[ 1후판정정야드 C동 RT BOOK-IN시 야드 01BED 위치가 제품창고방향인지 여부  : " + sNEW_MODULE_EFF_YN+ " ]]]---", JPlateYdConst.DEBUG, logId);
						
						if(sNEW_MODULE_EFF_YN.equals("Y")) {
							//01 BED 가 제품창고 방향이다 (F동과 동일) 
						
							String[] pArrBedNo = {"PCRT70","PCRT69","PCRT68"}; 
							
							for (int ii=0; ii<pArrStlNo.length; ii++) {
				    			if (!"".equals(pArrStlNo[ii])) {
				    				
				        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
				        	                                                          ,pArrBedNo[ii]
				        	        		                                          ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
				        	        		                                          ,szCARD_NO);
							
				    		        //szMsg = "[" + szOperationName + "] 1 후판전단L2 온라인RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
				                    //ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				    			}
				    		}
						} else {
							//01 BED 가 압연공장 방향이다 (F동과 반대)
							
							if(iMtlCnt == 1) {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PCRT70" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							} else if(iMtlCnt == 2) {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[1] ,"PCRT70" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PCRT69" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							} else {
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[2] ,"PCRT70" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[1] ,"PCRT69" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
			        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[0] ,"PCRT68" ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6) ,szCARD_NO);
							}
						}
	    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 54020 존 RT BOOK-IN 실적 전송 .. 완료>>>>" + iMtlCnt;
	                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	                    
					} else {
						
						if("H".equals(pYdPilingGp)) {
							//횡작업인 경우 1건씩 YDP2L501 , YDP2L601 전송
							for (int ii=0; ii<pArrStlNo.length; ii++) {
								if (!"".equals(pArrStlNo[ii])) {

				        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
                                             ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC").substring(0,6)
	                                         ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
	                                         ,szCARD_NO);
									
								}
							}
						} else {
			    				
		        	        //szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", szStlNoList
		        	        //                                                 ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC").substring(0,6)
		        	        //		                                         ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
		        	        //		                                         ,szCARD_NO);
							
							//2019.01.11 전단정정L2는 무조건 1매 단위로 완료실적을 보내달라는 요청
							for (int ii=0; ii<pArrStlNo.length; ii++) {
								if (!"".equals(pArrStlNo[ii])) {

				        	        szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", pArrStlNo[ii]
                                             ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC").substring(0,6)
	                                         ,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6)
	                                         ,szCARD_NO);
									
								}
							}
						}
	    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 RT BOOK-IN 실적 전송 .. 완료>>>>" + szSendMsg;
	                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						
					}	
				}
    		//}

	        szCrnSchId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	        szModifier	= ydDaoUtils.paraRecModifier(msgRecord);

	        rsResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
    		recPara  	= JDTORecordFactory.getInstance().create();
    		recPara.setField("YD_CRN_SCH_ID", 	szCrnSchId);

	        intRtnVal = ydCrnWrkMtlDao.getByYdCrnSchIdWithLoc(recPara, rsResult);

	        if (intRtnVal > 0) {

	        	rsResult.first();
				for(int ii=0; ii<rsResult.size(); ii++) {

			        rsResult.absolute(ii+1);
			        getRecord		= JDTORecordFactory.getInstance().create();
			        getRecord 		= rsResult.getRecord();

			        szStlNo			= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO"			);
			        szYdStkColGp	= ydDaoUtils.paraRecChkNull(getRecord, "YD_STK_COL_GP"	);
			        szYdStkBedNo	= ydDaoUtils.paraRecChkNull(getRecord, "YD_STK_BED_NO"	);
			        szDelYn			= ydDaoUtils.paraRecChkNull(getRecord, "DEL_YN"			);
			        
			        if("N".equals(szDelYn)) {
			        	//권하실적에서 크레인작업재료 DEL_YN = 'Y'로 설정된다.
			        	//1매씩 권하할때 크레인에 남아 있는재료 (DEL_YN ='N') 는 아래 로직을 수행안도록 한다.
			        	continue;
			        }
			        //if(szYdStkColGp.startsWith("PBRTWB")) {
			        	// ------------------------------------------------------------------------
						// 아래의 1,2,3로직 처리안함.
						// ------------------------------------------------------------------------
			        //}else{
						// ------------------------------------------------------------------------
						// 1. 야드 L2 저장품재원 삭제전문 전송
						// ------------------------------------------------------------------------
						szMsg = "[ " + szOperationName + "] 야드L2 저장품제원 전문송신 START";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("JMS_TC_CD", 			"YDY2L002"							);	// TC-CODE
						recPara.setField("YD_GP", 				JPlateYdConst.YD_GP_P_PLATE_YARD	);	// 야드구분
						recPara.setField("YD_STK_COL_GP", 		szYdStkColGp						);	// 야드적치열구분
						recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo						);	// 야드적치BED번호
						recPara.setField("YD_INFO_SYNC_CD", 	"5"									);	// 야드정보동기화코드 [1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)]
						recPara.setField("STL_NO", 				szStlNo								);	// 재료번호
						recPara.setField("MSG_GP", 				"D"									);	// 전문구분

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recPara에 logId 추가 
						recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
											
						szRtnMsg = ydDelegate.sendMsg(recPara);
	
						szMsg = "[ " + szOperationName + "] 야드L2 전문송신 END >>>> " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
						// ------------------------------------------------------------------------
						// 2. 조업 L2 북인 실적 전송 .. 순서가 뒤바뀌는 경우가 존재하여 일괄 전송처리
						// ------------------------------------------------------------------------
						if (pArrStlNo != null && pArrStlNo.length > 0) {
							szMsg = "[" + szOperationName + "] RT BOOK-IN 실적 일괄전송하여 .. SKIP";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						} else {
							szMsg = "[" + szOperationName + "] RT BOOK-IN 실적 전송 .. 시작";
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
							recPara = JDTORecordFactory.getInstance().create();
	
							if("PB".equals(ydUtils.substr(szYdStkColGp, 0, 2))||szYdStkColGp.startsWith("PART13")||szYdStkColGp.startsWith("PART9")||szYdStkColGp.startsWith("PCRT40")) {
								recPara.setField("MSG_ID", 				"YDP3L501V2"		);	// 열처리
								recPara.setField("STL_NO",				szStlNo				);	// 재료번호
								recPara.setField("OPERATION_TYPE",		"1"					);	// 1:Book In, 2:Book Out
								recPara.setField("YD_STK_COL_GP",		szYdStkColGp		);	// TO위치
								recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo		);	// 야드적치BED번호
								recPara.setField("YD_EQP_ID", 		    ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6));	

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recPara에 logId 추가 
								recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
																			
								szSendMsg = ydDelegate.sendMsg(recPara);
	
								szMsg = "[" + szOperationName + "] RT BOOK-IN 실적 전송 .. 완료>>>>" + szSendMsg;
								ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							} else {
	
								szSendMsg = JPlateYdCommonUtils.procJPlateSmsSendV2("1", szStlNo, szYdStkColGp,ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,6));
	
			    		        szMsg = "[" + szOperationName + "] 1 후판전단L2 RT BOOK-OUT 실적 전송 .. 완료>>>>" + szSendMsg;
			                    ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
							}				
						}
	
						// ------------------------------------------------------------------------
						// 3. 재료정보 삭제처리
						// ------------------------------------------------------------------------
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("STL_NO",				szStlNo								);	// 재료번호
						recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD	);
						recPara.setField("MODIFIER", 			szModifier							);
	
						intRtnVal = ydStockDao.delYdStock(recPara);
						if (intRtnVal < 0) {
							szRtnMsg = "재료정보 삭제처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						}
			        //}
					// ------------------------------------------------------------------------
					// 4. 저장위치 CLEAR
					// ------------------------------------------------------------------------
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", 				szStlNo								);	// 재료번호
					recPara.setField("YD_STK_LYR_MTL_STAT", "C"									);	// 야드적치단재료상태
					recPara.setField("YD_GP",				JPlateYdConst.YD_GP_P_PLATE_YARD	);
					recPara.setField("MODIFIER", 			szModifier							);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.21 recPara에 logId 추가 
					recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					
					intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recPara);
					if (intRtnVal < 0) {
						szRtnMsg = "저장위치 삭제 처리시 오류 발생! .... 오류코드 :" + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					}
				}
	        }

        } catch(Exception e) { 
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			//throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
			szMsg    = "[" + szOperationName + "] RT 북인 실적 전송시 .. Exception 발생" + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        }//end of try~catch

        return JPlateYdConst.RETN_CD_SUCCESS;

    } //end of procRtBookInY2v2()	
    
    /**
     * 오퍼레이션명 : 1후판정정 권하완료후  00012 Zone 에 Book-Out 요구 처리 된 것처럼 크레인 스케줄을 생성한다.
     *
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● DAOException
     */
    public int procMake00012ZoneSchY2(JDTORecord msgRecord) throws DAOException {

    	JPlateYdDelegate      	ydDelegate		= new JPlateYdDelegate();
    	JPlateYdCommDAO 		commDao 		= new JPlateYdCommDAO();
    	
    	String	szRtnMsg			= "";
		String 	szMsg           	= "";
    	
		String 	szMethodName    	= "procMake00012ZoneSchY2";
		String 	szOperationName 	= "00012 Zone Book-Out 크레인스케줄 생성";

		String	arrStlNo[]			= null;
		String	szStlNoList			= null;		// 재료번호 List
// 2024.12.09 신규 logId 사용                         
//		String  logId 				= null;
		String  szYdCrnSchId		= null;
		String  szYdWbookId			= null;
		String  szModifier			= null;

    	String	szNewYdWbookId		= null;		// 작업예약ID New
    	String 	szNewYdCrnSchId		= null;		// 스케쥴ID New
		
    	String  szFromLoc			= null;
    	String 	szYdSchCd       	= null;
    	String	szYdEqpId			= null;
    	String  szYdSchPrior		= null;
    	
    	String 	szSendMsg			= "";
    	
        int 	intRtnVal 			= 1;
    	int    intYdUpStkBedXaxis	= 0;					//권상지시위치베드 - 야드적치BedX축
    	int    intYdUpStkBedYaxis	= 0;					//권상지시위치베드 - 야드적치BedY축
    	int	   intYdStkBedXaxisTol  = 0;
    	int	   intYdStkBedYaxisTol  = 0;

        JDTORecordSet rsResult      = null;
        
    	JDTORecord recPara 			= null;
		JDTORecord recSch 			= JDTORecordFactory.getInstance().create();
		JDTORecord recWbook			= JDTORecordFactory.getInstance().create();
		JDTORecord recMtl			= JDTORecordFactory.getInstance().create();
		JDTORecord recL2Msg			= JDTORecordFactory.getInstance().create();
		JDTORecord recTemp			= null;
		
    	//DAO
    	JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.09 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

    	try {

    		szYdCrnSchId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"	);
    		szStlNoList		= ydDaoUtils.paraRecChkNull(msgRecord, "ARR_STL_NO"		);
    		szModifier		= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"		);
    		
// 2024.12.09 신규 logId 사용
/*    		
    		logId			= ydDaoUtils.paraRecChkNull(msgRecord, "LOG_ID");
    		if("".equals(logId)) {
    			logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
    		}
*/    		
    		arrStlNo    = szStlNoList.split(";");
    		
			//------------------------------------------------------------------------------------------------
			// 1.2. 해당 재료 작업예약/스케쥴 존재여부 확인
			//------------------------------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara  = JDTORecordFactory.getInstance().create();
			
			for(int ii=0; ii < arrStlNo.length; ii++) {
				if (!"".equals(arrStlNo[ii])) {
					// ------------------------------------------------------------------------
					// 1.2.1. 작업예약 존재여부 확인
					// ------------------------------------------------------------------------
					recPara.setField("STL_NO", 		arrStlNo[ii]);             	// 재료번호
					recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);
	
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getExistByStlNo", logId, szMethodName, "작업예약 존재여부 확인");
					
					if (rsResult.size() > 0) {
						szRtnMsg = "해당 재료[" + arrStlNo[ii] + "]로 작업예약이 존재!";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
					}
	
					// ------------------------------------------------------------------------
					// 1.2.2. 크레인 작업지시 존재여부 확인
					// ------------------------------------------------------------------------
					recPara.setField("STL_NO", 		arrStlNo[ii]);             	// 재료번호
					recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD);
					
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getExistByStlNo", logId, szMethodName, "크레인 작업지시 존재여부 확인");
					
					if (rsResult.size() > 0) {
						szRtnMsg = "해당 재료" + arrStlNo[ii] + "로 크레인 작업지시 존재!";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
					}
	
					//------------------------------------------------------------------
					// 1.2.3. 현재 저장위치가 1후판 정정야드가 아닐경우 오류로 처리
					//------------------------------------------------------------------
//---------------------------------------------------------------------------------------------
// 2024.12.09 Argument에 logId 추가
//					szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(arrStlNo[ii], JPlateYdConst.YD_GP_P_PLATE_YARD, "N");
					szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(arrStlNo[ii], JPlateYdConst.YD_GP_P_PLATE_YARD, "N", logId);
//---------------------------------------------------------------------------------------------
					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg = "[ " + szOperationName + "] 북아웃시 저장위치 확인 오류! >>>> " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
					}
					
				}
			}

			//------------------------------------------------------------------------------------------------
			//야드재료 등록
			//------------------------------------------------------------------------------------------------
			for(int ii=0; ii < arrStlNo.length; ii++) {			
				if (!"".equals(arrStlNo[ii])) {
					
					recPara.setField("REGISTER", 			szModifier								);		// 등록자
					recPara.setField("MODIFIER", 			szModifier								);		// 등록자
					recPara.setField("STL_NO", 				arrStlNo[ii]							);		// 재료번호
					recPara.setField("BOOK_OUT_RESN",		""										);		// Book-Out원인
					recPara.setField("BOOK_OUT_DATE",     	JPlateYdUtils.getCurDate("yyyyMMdd")	);		// Book-Out일자
					recPara.setField("BOOK_OUT_PROG",  		""										);		// Book-Out공정
					recPara.setField("FRTOMOVE_PLANT_GP",	""										);		// 북인대상재 구분 (이송공장구분 항목사용)
					
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.09 recPara에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
					recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				
					intRtnVal = ydStockDao.insYdStockBookOut(recPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "재료정보 미존재로 오류 발생 .. " + arrStlNo[ii] + ", 오류코드::" + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
					}
				}
			}
			
			//------------------------------------------------------------------------------------------------
    		// 기존 크레인작업지시 조회
			//------------------------------------------------------------------------------------------------
			recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch", logId, szMethodName, "기존 크레인작업지시 조회");
			
        	if (rsResult.size() <= 0) {
				szRtnMsg = "기존 크레인작업지시 조회 오류 :: YD_CRN_SCH_ID : " + szYdCrnSchId;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
			
        	recSch.setRecord(rsResult.getRecord(0)); //크레인 작업지시 저장
        	
			//------------------------------------------------------------------------------------------------
    		// 기존 작업예약 조회
			//------------------------------------------------------------------------------------------------
        	szYdWbookId = recSch.getFieldString("YD_WBOOK_ID");
			recPara.setField("YD_WBOOK_ID", 		szYdWbookId);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbook", logId, szMethodName, "기존 작업예약 조회");
			
        	if (rsResult.size() <= 0) {
				szRtnMsg = "기존 작업예약 조회 오류 :: YD_WBOOK_ID : " + szYdWbookId;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
        	
        	recWbook.setRecord(rsResult.getRecord(0)); //작업예약 저장
        	
			//------------------------------------------------------------------------------------------------
    		// Book-Out 위치 와 스케줄코드 
			//------------------------------------------------------------------------------------------------
			szFromLoc = JPlateYdCommonUtils.getY2RtZoneToLoc("00012");
    		szYdSchCd = szFromLoc + "LM";

			//------------------------------------------------------------------------------------------------
    		// 스케줄기준 조회
			//------------------------------------------------------------------------------------------------
			recPara.setField("YD_SCH_CD", 		szYdSchCd);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getYdSchrule", logId, szMethodName, "스케줄기준 조회");
			
        	if (rsResult.size() <= 0) {
				szRtnMsg = "스케줄기준 조회 조회 오류 :: YD_SCH_CD : " + szYdSchCd;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
        	
        	szYdEqpId = rsResult.getRecord(0).getFieldString("YD_WRK_CRN");
        	szYdSchPrior = rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR");

			//------------------------------------------------------------------------------------------------
    		// 권상위치 좌표 조회
			//------------------------------------------------------------------------------------------------
			recPara.setField("YD_STK_COL_GP", 		szFromLoc);
			recPara.setField("YD_STK_BED_NO", 		"01");
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbed", logId, szMethodName, "권상위치 좌표 조회");
        	
        	if (rsResult.size() <= 0) {
				szRtnMsg = "권상위치 좌표 조회 오류 :: YD_STK_COL_GP : " + szFromLoc;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
        	
        	rsResult.first();
    		recTemp  = rsResult.getRecord();
        	
    		intYdUpStkBedXaxis	 	= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"		); 		// 야드적치BedX축
    		intYdUpStkBedYaxis 		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"		); 		// 야드적치BedY축
    		intYdStkBedXaxisTol		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS_TOL"	); 	
    		intYdStkBedYaxisTol		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS_TOL"	); 	
        	
			//------------------------------------------------------------------------------------------------
			//신규 작업예약ID 생성
			//------------------------------------------------------------------------------------------------
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getSeqId", logId, szMethodName, "신규 작업예약ID 생성");
			if (rsResult.size() <= 0) {
				szRtnMsg = "작업예약 Id를 생성하지 못했습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
    		}
			szNewYdWbookId = rsResult.getRecord(0).getFieldString("YD_WBOOK_ID");
			
			//------------------------------------------------------------------------------------------------
			// 신규작업예약생성
			//------------------------------------------------------------------------------------------------
    		recWbook.setField("YD_WBOOK_ID"		,szNewYdWbookId		);
    		recWbook.setField("YD_SCH_CD"		,szYdSchCd			);
    		recWbook.setField("YD_BAY_GP"		,"C"				);
    		recWbook.setField("YD_TO_LOC_GUIDE"	,"PC"				); 		// ** To위치 
    		recWbook.setField("REGISTER"		,szModifier			);		// 등록자
    		recWbook.setField("MODIFIER"		,szModifier			);		// 수정자
    		
    		intRtnVal = commDao.insert(recWbook, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.insYdWrkbook", logId, szMethodName, "신규작업예약생성");
    		
        	if (intRtnVal <= 0) {
				szRtnMsg = "신규 작업예약 생성 오류 ::" + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
			
        	
			//------------------------------------------------------------------------------------------------
			// 신규 크레인스케줄ID를 할당받는다
			//------------------------------------------------------------------------------------------------
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getSeqId", logId, szMethodName, "신규 크레인스케줄ID를 할당받는다");
			if (rsResult.size() <= 0) {
				szRtnMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
    		}
			szNewYdCrnSchId = rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID");			
        	
			//------------------------------------------------------------------------------------------------
    		// 크레인작업지시 등록  ** 설비코드 FROM위치 좌표 설정해야 함!!!
			//------------------------------------------------------------------------------------------------
			recPara.setField("YD_CRN_SCH_ID"			,szNewYdCrnSchId							);
			recPara.setField("REGISTER"					,szModifier									);		// 등록자
			recPara.setField("MODIFIER"					,szModifier									);		// 수정자
			recPara.setField("YD_WBOOK_ID"				,szNewYdWbookId								);
			recPara.setField("YD_EQP_ID"				,szYdEqpId									);
			recPara.setField("YD_GP"					,JPlateYdConst.YD_GP_P_PLATE_YARD			);
			recPara.setField("YD_BAY_GP"				,"C"										);
			recPara.setField("YD_SCH_CD"				,szYdSchCd									);
			recPara.setField("YD_SCH_REQ_GP"			,recSch.getField("YD_SCH_REQ_GP")			);
			recPara.setField("YD_SCH_PRIOR"				,szYdSchPrior								);
			recPara.setField("YD_WRK_PROG_STAT"			,JPlateYdConst.YD_EQP_STAT_IDLE			);		//크레인이 IDLE인 상태 - 스케줄수행대기
			recPara.setField("YD_WBOOK_DT"				,JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);	
			recPara.setField("YD_SCH_DT"				,JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);	
			recPara.setField("YD_MAIN_WRK_MTL_SH"		,recSch.getField("YD_MAIN_WRK_MTL_SH")		);
			recPara.setField("YD_AID_WRK_MTL_SH"		,recSch.getField("YD_AID_WRK_MTL_SH")		);
			recPara.setField("YD_TO_LOC_DCSN_MTD"		,recSch.getField("YD_TO_LOC_DCSN_MTD")		);
			recPara.setField("YD_TO_LOC_GUIDE"			,"XX010101"									);
			recPara.setField("YD_EQP_WRK_SH"			,recSch.getField("YD_EQP_WRK_SH")			);
			recPara.setField("YD_EQP_WRK_WT"			,recSch.getField("YD_EQP_WRK_WT")			);
			recPara.setField("YD_EQP_WRK_T"				,recSch.getField("YD_EQP_WRK_T")			);
			recPara.setField("YD_EQP_WRK_MAX_W"			,recSch.getField("YD_EQP_WRK_MAX_W")		);
			recPara.setField("YD_EQP_WRK_MAX_L"			,recSch.getField("YD_EQP_WRK_MAX_L")		);
			recPara.setField("YD_CRN_SB_CTL_H"			,recSch.getField("YD_CRN_SB_CTL_H")			);
			recPara.setField("YD_UP_WO_LOC"				,szFromLoc + "01"							);		// 야드권상지시위치
			recPara.setField("YD_UP_WO_LAYER"			,"001"										);		// 야드권상지시단
			recPara.setField("YD_UP_WO_LOC_XAXIS"		,String.valueOf(intYdUpStkBedXaxis)			);		// 야드권상실적X축
			recPara.setField("YD_UP_WO_XAXIS_GAP_MAX"	,String.valueOf(intYdStkBedXaxisTol)		);				
			recPara.setField("YD_UP_WO_XAXIS_GAP_MIN"	,String.valueOf(intYdStkBedXaxisTol)		);				
			recPara.setField("YD_UP_WO_LOC_YAXIS"		,String.valueOf(intYdUpStkBedYaxis)			);		// 야드권상실적Y축
			recPara.setField("YD_UP_WO_LOC_YAXIS1"		,String.valueOf(intYdUpStkBedYaxis)			);		// 야드권상실적Y축1
			recPara.setField("YD_UP_WO_LOC_YAXIS2"		,"0"										);		// 야드권상실적Y축2
			recPara.setField("YD_UP_WO_YAXIS_GAP_MAX"	,String.valueOf(intYdStkBedYaxisTol)		);				
			recPara.setField("YD_UP_WO_YAXIS_GAP_MIN"	,String.valueOf(intYdStkBedYaxisTol)		);				
			recPara.setField("YD_UP_WO_LOC_ZAXIS"		,recSch.getField("YD_UP_WO_LOC_ZAXIS")		);		// 야드권상실적Z축
			recPara.setField("YD_UP_WO_ZAXIS_GAP_MAX"	,recSch.getField("YD_UP_WO_ZAXIS_GAP_MAX")	);	
			recPara.setField("YD_UP_WO_ZAXIS_GAP_MIN"	,recSch.getField("YD_UP_WO_ZAXIS_GAP_MIN")	);	
			recPara.setField("YD_UP_WRK_ACT_GP"			,recSch.getField("YD_UP_WRK_ACT_GP")		);	
			recPara.setField("YD_DN_WO_LOC"				,"XX010101"									);		// 야드권하지시위치
			
        	intRtnVal = commDao.insert(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.insYdCrnsch", logId, szMethodName, "크레인작업지시 등록");
        	if (intRtnVal <= 0) {
				szRtnMsg = "기존 크레인작업지시 등록 오류 ::" + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
        	
			
			for(int ii=0; ii < arrStlNo.length; ii++) {
				if (!"".equals(arrStlNo[ii])) {
					
					//------------------------------------------------------------------------------------------------
		    		// 2.4. 크레인 작업재료 조회
					//------------------------------------------------------------------------------------------------
		        	recPara.setField("YD_CRN_SCH_ID", 	szYdCrnSchId);
		        	recPara.setField("STL_NO", 			arrStlNo[ii]);

					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtl", logId, szMethodName, "크레인 작업재료 조회");
					if (rsResult.size() <= 0) {
						szRtnMsg = "기존 크레인작업재료 조회 .. 재료번호 ::" + arrStlNo[ii] + " >> 크레인스케줄ID ::" + szYdCrnSchId;
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
		        	}
					recMtl.setRecord(rsResult.getRecord(0));
					
					//------------------------------------------------------------------------------------------------
		    		// 2.5. 크레인작업재료 등록
					//------------------------------------------------------------------------------------------------
		        	recMtl.setField("YD_CRN_SCH_ID",	szNewYdCrnSchId	);		// 야드크레인스케쥴ID
        			recMtl.setField("STL_NO",			arrStlNo[ii]	);		// 재료번호
        			recMtl.setField("REGISTER", 		szModifier		);
        			recMtl.setField("MODIFIER",	        szModifier		);
					
        			commDao.insert(recMtl, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.insYdCrnWrkMtl", logId, szMethodName, "크레인작업재료 등록");
        			
					if (rsResult.size() <= 0) {
						szRtnMsg = "기존 크레인작업재료 등록 오류 ::" + arrStlNo[ii] + " >> 크레인스케줄ID ::" + szNewYdCrnSchId;
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
		        	}
					
					//------------------------------------------------------------------------------------------------
		        	// 2.7. 작업예약재료 등록
					//------------------------------------------------------------------------------------------------
		        	recMtl.setField("YD_WBOOK_ID",		szNewYdWbookId	);		// 작업예약ID
        			recMtl.setField("STL_NO",			arrStlNo[ii]	);		// 재료번호
					recMtl.setField("MODIFIER",	        szModifier		);		
        			recMtl.setField("REGISTER", 		szModifier		);
					
        			intRtnVal = commDao.insert(recMtl, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.insYdWrkbookMtl", logId, szMethodName, "작업예약재료 등록");
        			
		        	if (intRtnVal <= 0) {
						szRtnMsg = "기존 작업예약재료 등록 오류 ::" + arrStlNo[ii] + " >> " + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
		        	}
		        	
		        	//------------------------------------------------------------------------------------------------
		        	// 2.10. 저장품에 작업예약ID 수정
					//------------------------------------------------------------------------------------------------
		        	recPara.setField("YD_WBOOK_ID",		szNewYdWbookId	);		// 작업예약ID
		        	recPara.setField("STL_NO",			arrStlNo[ii]	);		// 재료번호
		        	recPara.setField("MODIFIER",	    szModifier		);		// 수정자
		        	recPara.setField("YD_SCH_CD",	    szYdSchCd		);		// 스케쥴코드
		        	
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStockWbook", logId, szMethodName, "저장품에 작업예약ID 수정");
		        	if (intRtnVal <= 0) {
						szRtnMsg = "저장품에 작업예약ID 수정 오류 .. 재료번호::" + arrStlNo[ii];
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
		        	}
		        	
				}
			}
        	
			
			//------------------------------------------------------------------------------------------------
			// 5.크레인 작업지시 전송 [신규 파일링 작업지시] - 권하작업지시로 Set
			//------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        //recL2Msg.setField("MSG_ID",						"YDY2L004");						// 크레인 작업지시
			recL2Msg.setField("MSG_ID",						"YDY2L004V2"						);		// 크레인 작업지시
	        recL2Msg.setField("YD_CRN_SCH_ID",    			szNewYdCrnSchId						);
	        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_IDLE	);		//크레인이 IDLE인 상태 - 스케줄수행대기
	        recL2Msg.setField("MSG_GP",           			"I"									);
        	
//-------------------------------------------------------------------------------------------------------------------------
//2024.12.09 recL2Msg에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
	        recL2Msg.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
	              	
	        szSendMsg = ydDelegate.sendMsg(recL2Msg);

			szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 종료 :: " + szSendMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
        	

        } catch(Exception e) {
        	szRtnMsg = "00012 Zone Book-Out 크레인스케줄 생성  .. <br>" + szRtnMsg;
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        }//end of try~catch

        return intRtnVal;

    } // end of procMake00012ZoneSchY2()
    

    /**
     * 오퍼레이션명 : 1후판정정 횡작업(H) 권하완료후  00012 Zone 에 낱개 단위 Book-Out 요구 처리 된 것처럼 크레인 스케줄을 생성한다.
     *
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● DAOException
     */
    public int procMake00012ZoneSchNY2(JDTORecord msgRecord) throws DAOException {

    	JPlateYdDelegate      	ydDelegate		= new JPlateYdDelegate();
    	JPlateYdCommDAO 		commDao 		= new JPlateYdCommDAO();
    	
    	String	szRtnMsg			= "";
		String 	szMsg           	= "";
    	
		String 	szMethodName    	= "procMake00012ZoneSchNY2";
		String 	szOperationName 	= "00012 Zone Book-Out 낱개 단위 크레인스케줄 생성";

		String	arrStlNo[]			= null;
		String	szStlNoList			= null;		// 재료번호 List
// 2024.12.09 신규 logId 사용                         
//		String  logId 				= null;
		String  szYdCrnSchId		= null;
		String  szYdWbookId			= null;
		String  szModifier			= null;

    	String	szNewYdWbookId		= null;		// 작업예약ID New
    	String 	szNewYdCrnSchId		= null;		// 스케쥴ID New
		
    	String  szFromLoc			= null;
    	String 	szYdSchCd       	= null;
    	String	szYdEqpId			= null;
    	String  szYdSchPrior		= null;
    	
    	String 	szSendMsg			= "";
    	
        int 	intRtnVal 			= 1;
    	int     intYdUpStkBedXaxis	= 0;					//권상지시위치베드 - 야드적치BedX축
    	int     intYdUpStkBedYaxis	= 0;					//권상지시위치베드 - 야드적치BedY축
    	int	    intYdStkBedXaxisTol = 0;
    	int	    intYdStkBedYaxisTol = 0;

        JDTORecordSet rsResult      = null;
        
    	JDTORecord recPara 			= null;
		JDTORecord recSch 			= JDTORecordFactory.getInstance().create();
		JDTORecord recWbook			= JDTORecordFactory.getInstance().create();
		JDTORecord recMtl			= JDTORecordFactory.getInstance().create();
		JDTORecord recL2Msg			= JDTORecordFactory.getInstance().create();
		JDTORecord recTemp			= null;
		
    	//DAO
    	JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.09 로그 개선 
//-------------------------------------------------------------------------------------------------------------------------
        String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

    	try {

    		szYdCrnSchId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
    		szStlNoList		= ydDaoUtils.paraRecChkNull(msgRecord, "ARR_STL_NO");
    		szModifier		= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
    		
// 2024.12.09 신규 logId 사용
/*    		
    		logId			= ydDaoUtils.paraRecChkNull(msgRecord, "LOG_ID");
    		
    		if("".equals(logId)) {
    			logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
    		}
*/    		
    		arrStlNo    = szStlNoList.split(";");
    		
			//------------------------------------------------------------------------------------------------
			// 1.2. 해당 재료 작업예약/스케쥴 존재여부 확인
			//------------------------------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara  = JDTORecordFactory.getInstance().create();
			
			for(int ii=0; ii < arrStlNo.length; ii++) {
				if (!"".equals(arrStlNo[ii])) {
					// ------------------------------------------------------------------------
					// 1.2.1. 작업예약 존재여부 확인
					// ------------------------------------------------------------------------
					recPara.setField("STL_NO", 		arrStlNo[ii]						);		// 재료번호
					recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD	);
	
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getExistByStlNo", logId, szMethodName, "작업예약 존재여부 확인");
					
					if (rsResult.size() > 0) {
						szRtnMsg = "해당 재료[" + arrStlNo[ii] + "]로 작업예약이 존재!";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
					}
	
					// ------------------------------------------------------------------------
					// 1.2.2. 크레인 작업지시 존재여부 확인
					// ------------------------------------------------------------------------
					recPara.setField("STL_NO", 		arrStlNo[ii]						);		// 재료번호
					recPara.setField("YD_GP",		JPlateYdConst.YD_GP_P_PLATE_YARD	);
					
					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getExistByStlNo", logId, szMethodName, "크레인 작업지시 존재여부 확인");
					
					if (rsResult.size() > 0) {
						szRtnMsg = "해당 재료" + arrStlNo[ii] + "로 크레인 작업지시 존재!";
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
					}
	
					//------------------------------------------------------------------
					// 1.2.3. 현재 저장위치가 1후판 정정야드가 아닐경우 오류로 처리
					//------------------------------------------------------------------
//---------------------------------------------------------------------------------------------
// 2024.12.09 Argument에 logId 추가
//					szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(arrStlNo[ii], JPlateYdConst.YD_GP_P_PLATE_YARD, "N");
					szRtnMsg = JPlateYdCommonUtils.checkUpdYdLocYdP(arrStlNo[ii], JPlateYdConst.YD_GP_P_PLATE_YARD, "N", logId);
//---------------------------------------------------------------------------------------------
					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						szMsg = "[ " + szOperationName + "] 북아웃시 저장위치 확인 오류! >>>> " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
					}
					
				}
			}

			//------------------------------------------------------------------------------------------------
			//야드재료 등록
			//------------------------------------------------------------------------------------------------
			for(int ii=0; ii < arrStlNo.length; ii++) {			
				if (!"".equals(arrStlNo[ii])) {
					
					recPara.setField("REGISTER", 			szModifier								);		// 등록자
					recPara.setField("MODIFIER", 			szModifier								);		// 등록자
					recPara.setField("STL_NO", 				arrStlNo[ii]							);		// 재료번호
					recPara.setField("BOOK_OUT_RESN",		""										);		// Book-Out원인
					recPara.setField("BOOK_OUT_DATE",     	JPlateYdUtils.getCurDate("yyyyMMdd")	);		// Book-Out일자
					recPara.setField("BOOK_OUT_PROG",  		""										);		// Book-Out공정
					recPara.setField("FRTOMOVE_PLANT_GP",	""										);		// 북인대상재 구분 (이송공장구분 항목사용)
					
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.09 recPara에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
					recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				
					intRtnVal = ydStockDao.insYdStockBookOut(recPara);
					if (intRtnVal <= 0) {
						szRtnMsg = "재료정보 미존재로 오류 발생 .. " + arrStlNo[ii] + ", 오류코드::" + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
					}
				}
			}
			
			//------------------------------------------------------------------------------------------------
    		// 기존 크레인작업지시 조회
			//------------------------------------------------------------------------------------------------
			recPara.setField("YD_CRN_SCH_ID", 		szYdCrnSchId);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch", logId, szMethodName, "기존 크레인작업지시 조회");
			
        	if (rsResult.size() <= 0) {
				szRtnMsg = "기존 크레인작업지시 조회 오류 :: YD_CRN_SCH_ID : " + szYdCrnSchId;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
			
        	recSch.setRecord(rsResult.getRecord(0)); //크레인 작업지시 저장
        	
			//------------------------------------------------------------------------------------------------
    		// 기존 작업예약 조회
			//------------------------------------------------------------------------------------------------
        	szYdWbookId = recSch.getFieldString("YD_WBOOK_ID");
			recPara.setField("YD_WBOOK_ID", 		szYdWbookId);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbook", logId, szMethodName, "기존 작업예약 조회");
			
        	if (rsResult.size() <= 0) {
				szRtnMsg = "기존 작업예약 조회 오류 :: YD_WBOOK_ID : " + szYdWbookId;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
        	
        	recWbook.setRecord(rsResult.getRecord(0)); //작업예약 저장
        	
			//------------------------------------------------------------------------------------------------
    		// Book-Out 위치 와 스케줄코드 
			//------------------------------------------------------------------------------------------------
			szFromLoc = JPlateYdCommonUtils.getY2RtZoneToLoc("00012");
    		szYdSchCd = szFromLoc + "LM";

			//------------------------------------------------------------------------------------------------
    		// 스케줄기준 조회
			//------------------------------------------------------------------------------------------------
			recPara.setField("YD_SCH_CD", 		szYdSchCd);
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getYdSchrule", logId, szMethodName, "스케줄기준 조회");
			
        	if (rsResult.size() <= 0) {
				szRtnMsg = "스케줄기준 조회 조회 오류 :: YD_SCH_CD : " + szYdSchCd;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
        	
        	szYdEqpId 		= rsResult.getRecord(0).getFieldString("YD_WRK_CRN");
        	szYdSchPrior 	= rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR");

			//------------------------------------------------------------------------------------------------
    		// 권상위치 좌표 조회
			//------------------------------------------------------------------------------------------------
			recPara.setField("YD_STK_COL_GP", 		szFromLoc);
			recPara.setField("YD_STK_BED_NO", 		"01");
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbed", logId, szMethodName, "권상위치 좌표 조회");
        	
        	if (rsResult.size() <= 0) {
				szRtnMsg = "권상위치 좌표 조회 오류 :: YD_STK_COL_GP : " + szFromLoc;
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
        	
        	rsResult.first();
    		recTemp  = rsResult.getRecord();
        	
    		intYdUpStkBedXaxis	 	= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS"		); 		// 야드적치BedX축
    		intYdUpStkBedYaxis 		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS"		); 		// 야드적치BedY축
    		intYdStkBedXaxisTol		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_XAXIS_TOL"	); 	
    		intYdStkBedYaxisTol		= ydDaoUtils.paraRecChkNullInt(recTemp, "YD_STK_BED_YAXIS_TOL"	); 	
        	
			//------------------------------------------------------------------------------------------------
			//신규 작업예약ID 생성
			//------------------------------------------------------------------------------------------------
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getSeqId", logId, szMethodName, "신규 작업예약ID 생성");
			if (rsResult.size() <= 0) {
				szRtnMsg = "작업예약 Id를 생성하지 못했습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
    		}
			szNewYdWbookId = rsResult.getRecord(0).getFieldString("YD_WBOOK_ID");
			
			//------------------------------------------------------------------------------------------------
			// 신규작업예약생성
			//------------------------------------------------------------------------------------------------
    		recWbook.setField("YD_WBOOK_ID"		,szNewYdWbookId	);
    		recWbook.setField("YD_SCH_CD"		,szYdSchCd		);
    		recWbook.setField("YD_BAY_GP"		,"C"			);
    		recWbook.setField("YD_TO_LOC_GUIDE"	,"PC"			); 	// ** To위치 
    		recWbook.setField("REGISTER"		,szModifier		);	// 등록자
    		recWbook.setField("MODIFIER"		,szModifier		);	// 수정자
    		
    		intRtnVal = commDao.insert(recWbook, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.insYdWrkbook", logId, szMethodName, "신규작업예약생성");
    		
        	if (intRtnVal <= 0) {
				szRtnMsg = "신규 작업예약 생성 오류 ::" + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
			
        	
			//------------------------------------------------------------------------------------------------
			// 신규 크레인스케줄ID를 할당받는다
			//------------------------------------------------------------------------------------------------
			rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getSeqId", logId, szMethodName, "신규 크레인스케줄ID를 할당받는다");
			if (rsResult.size() <= 0) {
				szRtnMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
    		}
			szNewYdCrnSchId = rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID");			
        	
			//------------------------------------------------------------------------------------------------
    		// 크레인작업지시 등록  ** 설비코드 FROM위치 좌표 설정해야 함!!!
			//------------------------------------------------------------------------------------------------
			recPara.setField("YD_CRN_SCH_ID"			,szNewYdCrnSchId							);
			recPara.setField("REGISTER"					,szModifier									);		// 등록자
			recPara.setField("MODIFIER"					,szModifier									);		// 수정자
			recPara.setField("YD_WBOOK_ID"				,szNewYdWbookId								);
			recPara.setField("YD_EQP_ID"				,szYdEqpId									);
			recPara.setField("YD_GP"					,JPlateYdConst.YD_GP_P_PLATE_YARD			);
			recPara.setField("YD_BAY_GP"				,"C"										);
			recPara.setField("YD_SCH_CD"				,szYdSchCd									);
			recPara.setField("YD_SCH_REQ_GP"			,recSch.getField("YD_SCH_REQ_GP")			);
			recPara.setField("YD_SCH_PRIOR"				,szYdSchPrior								);
			recPara.setField("YD_WRK_PROG_STAT"			,JPlateYdConst.YD_EQP_STAT_IDLE			);		//크레인이 IDLE인 상태 - 스케줄수행대기
			recPara.setField("YD_WBOOK_DT"				,JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);	
			recPara.setField("YD_SCH_DT"				,JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);	
			recPara.setField("YD_MAIN_WRK_MTL_SH"		,recSch.getField("YD_MAIN_WRK_MTL_SH")		);
			recPara.setField("YD_AID_WRK_MTL_SH"		,recSch.getField("YD_AID_WRK_MTL_SH")		);
			recPara.setField("YD_TO_LOC_DCSN_MTD"		,recSch.getField("YD_TO_LOC_DCSN_MTD")		);
			recPara.setField("YD_TO_LOC_GUIDE"			,"XX010101"									);
			recPara.setField("YD_EQP_WRK_SH"			,recSch.getField("YD_EQP_WRK_SH")			);
			recPara.setField("YD_EQP_WRK_WT"			,recSch.getField("YD_EQP_WRK_WT")			);
			recPara.setField("YD_EQP_WRK_T"				,recSch.getField("YD_EQP_WRK_T")			);
			recPara.setField("YD_EQP_WRK_MAX_W"			,recSch.getField("YD_EQP_WRK_MAX_W")		);
			recPara.setField("YD_EQP_WRK_MAX_L"			,recSch.getField("YD_EQP_WRK_MAX_L")		);
			recPara.setField("YD_CRN_SB_CTL_H"			,recSch.getField("YD_CRN_SB_CTL_H")			);
			recPara.setField("YD_UP_WO_LOC"				,szFromLoc + "01"							);		// 야드권상지시위치
			recPara.setField("YD_UP_WO_LAYER"			,"001"										);		// 야드권상지시단
			recPara.setField("YD_UP_WO_LOC_XAXIS"		,String.valueOf(intYdUpStkBedXaxis)			);		// 야드권상실적X축
			recPara.setField("YD_UP_WO_XAXIS_GAP_MAX"	,String.valueOf(intYdStkBedXaxisTol)		);				
			recPara.setField("YD_UP_WO_XAXIS_GAP_MIN"	,String.valueOf(intYdStkBedXaxisTol)		);				
			recPara.setField("YD_UP_WO_LOC_YAXIS"		,String.valueOf(intYdUpStkBedYaxis)			);		// 야드권상실적Y축
			recPara.setField("YD_UP_WO_LOC_YAXIS1"		,String.valueOf(intYdUpStkBedYaxis)			);		// 야드권상실적Y축1
			recPara.setField("YD_UP_WO_LOC_YAXIS2"		,"0"										);		// 야드권상실적Y축2
			recPara.setField("YD_UP_WO_YAXIS_GAP_MAX"	,String.valueOf(intYdStkBedYaxisTol)		);				
			recPara.setField("YD_UP_WO_YAXIS_GAP_MIN"	,String.valueOf(intYdStkBedYaxisTol)		);				
			recPara.setField("YD_UP_WO_LOC_ZAXIS"		,recSch.getField("YD_UP_WO_LOC_ZAXIS")		);		// 야드권상실적Z축
			recPara.setField("YD_UP_WO_ZAXIS_GAP_MAX"	,recSch.getField("YD_UP_WO_ZAXIS_GAP_MAX")	);	
			recPara.setField("YD_UP_WO_ZAXIS_GAP_MIN"	,recSch.getField("YD_UP_WO_ZAXIS_GAP_MIN")	);	
			recPara.setField("YD_UP_WRK_ACT_GP"			,"N"										);	
			recPara.setField("YD_DN_WO_LOC"				,"XX010101"									);		// 야드권하지시위치
			
        	intRtnVal = commDao.insert(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.insYdCrnsch", logId, szMethodName, "크레인작업지시 등록");
        	if (intRtnVal <= 0) {
				szRtnMsg = "기존 크레인작업지시 등록 오류 ::" + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return 0;
        	}
        	
			
			for(int ii=0; ii < arrStlNo.length; ii++) {
				if (!"".equals(arrStlNo[ii])) {
					
					//------------------------------------------------------------------------------------------------
		    		// 2.4. 기존 크레인 작업재료 조회
					//------------------------------------------------------------------------------------------------
		        	recPara.setField("YD_CRN_SCH_ID", 	szYdCrnSchId);
		        	recPara.setField("STL_NO", 			arrStlNo[ii]);

					rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtl", logId, szMethodName, "크레인 작업재료 조회");
					if (rsResult.size() <= 0) {
						szRtnMsg = "기존 크레인작업재료 조회 .. 재료번호 ::" + arrStlNo[ii] + " >> 크레인스케줄ID ::" + szYdCrnSchId;
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
		        	}
					recMtl.setRecord(rsResult.getRecord(0));
					
					//------------------------------------------------------------------------------------------------
		    		// 2.5. 크레인작업재료 등록
					//------------------------------------------------------------------------------------------------
		        	recMtl.setField("YD_CRN_SCH_ID",	szNewYdCrnSchId	);		// 야드크레인스케쥴ID
        			recMtl.setField("STL_NO",			arrStlNo[ii]	);		// 재료번호
        			recMtl.setField("YD_STK_LOT_CD", 	""				);
        			recMtl.setField("REGISTER", 		szModifier		);
        			recMtl.setField("MODIFIER",	        szModifier		);
					
        			commDao.insert(recMtl, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.insYdCrnWrkMtl", logId, szMethodName, "크레인작업재료 등록");
        			
					if (rsResult.size() <= 0) {
						szRtnMsg = "기존 크레인작업재료 등록 오류 ::" + arrStlNo[ii] + " >> 크레인스케줄ID ::" + szNewYdCrnSchId;
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
		        	}
					
					//------------------------------------------------------------------------------------------------
		        	// 2.7. 작업예약재료 등록
					//------------------------------------------------------------------------------------------------
		        	recMtl.setField("YD_WBOOK_ID",		szNewYdWbookId	);		// 작업예약ID
        			recMtl.setField("STL_NO",			arrStlNo[ii]	);		// 재료번호
					recMtl.setField("MODIFIER",	        szModifier		);		
        			recMtl.setField("REGISTER", 		szModifier		);
					
        			intRtnVal = commDao.insert(recMtl, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.insYdWrkbookMtl", logId, szMethodName, "작업예약재료 등록");
        			
		        	if (intRtnVal <= 0) {
						szRtnMsg = "기존 작업예약재료 등록 오류 ::" + arrStlNo[ii] + " >> " + Integer.toString(intRtnVal);
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
		        	}
		        	
		        	//------------------------------------------------------------------------------------------------
		        	// 2.10. 저장품에 작업예약ID 수정
					//------------------------------------------------------------------------------------------------
		        	recPara.setField("YD_WBOOK_ID",		szNewYdWbookId	);		// 작업예약ID
		        	recPara.setField("STL_NO",			arrStlNo[ii]	);		// 재료번호
		        	recPara.setField("MODIFIER",	    szModifier		);		// 수정자
		        	recPara.setField("YD_SCH_CD",	    szYdSchCd		);		// 스케쥴코드
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStockWbook", logId, szMethodName, "저장품에 작업예약ID 수정");
		        	if (intRtnVal <= 0) {
						szRtnMsg = "저장품에 작업예약ID 수정 오류 .. 재료번호::" + arrStlNo[ii];
						szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						
						return 0;
		        	}
		        	
					//------------------------------------------------------------------------------------------------
					// 5.크레인 작업지시 전송  - 권하작업지시로 Set
					//------------------------------------------------------------------------------------------------
					szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 시작";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			        //recL2Msg.setField("MSG_ID",						"YDY2L004");						// 크레인 작업지시
			        recL2Msg.setField("MSG_ID",						"YDY2L004V2"						);		// 크레인 작업지시
			        recL2Msg.setField("YD_CRN_SCH_ID",    			szNewYdCrnSchId						);
			        recL2Msg.setField("YD_WRK_PROG_STAT", 			JPlateYdConst.YD_EQP_STAT_IDLE	);		//크레인이 IDLE인 상태 - 스케줄수행대기
			        recL2Msg.setField("MSG_GP",           			"I"									);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.09 recL2Msg에 logId 추가  
//-------------------------------------------------------------------------------------------------------------------------
			        recL2Msg.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
			     				
			        szSendMsg = ydDelegate.sendMsg(recL2Msg);
			        
			        if((ii+1) < arrStlNo.length) {
						//------------------------------------------------------------------------------------------------
						//신규 작업예약ID 생성
						//------------------------------------------------------------------------------------------------
						rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getSeqId", logId, szMethodName, "신규 작업예약ID 생성");
						if (rsResult.size() <= 0) {
							szRtnMsg = "작업예약 Id를 생성하지 못했습니다.";
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							
							return 0;
			    		}
						szNewYdWbookId = rsResult.getRecord(0).getFieldString("YD_WBOOK_ID");
		        	
						//------------------------------------------------------------------------------------------------
						// 신규작업예약생성
						//------------------------------------------------------------------------------------------------
			    		recWbook.setField("YD_WBOOK_ID"		,szNewYdWbookId	);
			    		recWbook.setField("YD_SCH_CD"		,szYdSchCd		);
			    		recWbook.setField("YD_BAY_GP"		,"C"			);
			    		recWbook.setField("YD_TO_LOC_GUIDE"	,"PC"			); 		// ** To위치 
			    		recWbook.setField("REGISTER"		,szModifier		);		// 등록자
			    		recWbook.setField("MODIFIER"		,szModifier		);		// 수정자
			    		
			    		intRtnVal = commDao.insert(recWbook, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.insYdWrkbook", logId, szMethodName, "신규작업예약생성");
			    		
			        	if (intRtnVal <= 0) {
							szRtnMsg = "신규 작업예약 생성 오류 ::" + Integer.toString(intRtnVal);
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							
							return 0;
			        	}
						
			        	
						//------------------------------------------------------------------------------------------------
						// 신규 크레인스케줄ID를 할당받는다
						//------------------------------------------------------------------------------------------------
						rsResult = commDao.select(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getSeqId", logId, szMethodName, "신규 크레인스케줄ID를 할당받는다");
						if (rsResult.size() <= 0) {
							szRtnMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							
							return 0;
			    		}
						szNewYdCrnSchId = rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID");			
			        	
						//------------------------------------------------------------------------------------------------
			    		// 크레인작업지시 등록  ** 설비코드 FROM위치 좌표 설정해야 함!!!
						//------------------------------------------------------------------------------------------------
						recPara.setField("YD_CRN_SCH_ID"			,szNewYdCrnSchId							);
						recPara.setField("REGISTER"					,szModifier									);		// 등록자
						recPara.setField("MODIFIER"					,szModifier									);		// 수정자
						recPara.setField("YD_WBOOK_ID"				,szNewYdWbookId								);
						recPara.setField("YD_EQP_ID"				,szYdEqpId									);
						recPara.setField("YD_GP"					,JPlateYdConst.YD_GP_P_PLATE_YARD			);
						recPara.setField("YD_BAY_GP"				,"C"										);
						recPara.setField("YD_SCH_CD"				,szYdSchCd									);
						recPara.setField("YD_SCH_REQ_GP"			,recSch.getField("YD_SCH_REQ_GP")			);
						recPara.setField("YD_SCH_PRIOR"				,szYdSchPrior								);
						recPara.setField("YD_WRK_PROG_STAT"			,JPlateYdConst.YD_EQP_STAT_IDLE			);		//크레인이 IDLE인 상태 - 스케줄수행대기
						recPara.setField("YD_WBOOK_DT"				,JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);	
						recPara.setField("YD_SCH_DT"				,JPlateYdUtils.getCurDate("yyyyMMddHHmmss")	);	
						recPara.setField("YD_MAIN_WRK_MTL_SH"		,recSch.getField("YD_MAIN_WRK_MTL_SH")		);
						recPara.setField("YD_AID_WRK_MTL_SH"		,recSch.getField("YD_AID_WRK_MTL_SH")		);
						recPara.setField("YD_TO_LOC_DCSN_MTD"		,recSch.getField("YD_TO_LOC_DCSN_MTD")		);
						recPara.setField("YD_TO_LOC_GUIDE"			,"XX010101"									);
						recPara.setField("YD_EQP_WRK_SH"			,recSch.getField("YD_EQP_WRK_SH")			);
						recPara.setField("YD_EQP_WRK_WT"			,recSch.getField("YD_EQP_WRK_WT")			);
						recPara.setField("YD_EQP_WRK_T"				,recSch.getField("YD_EQP_WRK_T")			);
						recPara.setField("YD_EQP_WRK_MAX_W"			,recSch.getField("YD_EQP_WRK_MAX_W")		);
						recPara.setField("YD_EQP_WRK_MAX_L"			,recSch.getField("YD_EQP_WRK_MAX_L")		);
						recPara.setField("YD_CRN_SB_CTL_H"			,recSch.getField("YD_CRN_SB_CTL_H")			);
						recPara.setField("YD_UP_WO_LOC"				,szFromLoc + "01"							);		// 야드권상지시위치
						recPara.setField("YD_UP_WO_LAYER"			,"001"										);		// 야드권상지시단
						recPara.setField("YD_UP_WO_LOC_XAXIS"		,String.valueOf(intYdUpStkBedXaxis)			);		// 야드권상실적X축
						recPara.setField("YD_UP_WO_XAXIS_GAP_MAX"	,String.valueOf(intYdStkBedXaxisTol)		);				
						recPara.setField("YD_UP_WO_XAXIS_GAP_MIN"	,String.valueOf(intYdStkBedXaxisTol)		);				
						recPara.setField("YD_UP_WO_LOC_YAXIS"		,String.valueOf(intYdUpStkBedYaxis)			);		// 야드권상실적Y축
						recPara.setField("YD_UP_WO_LOC_YAXIS1"		,String.valueOf(intYdUpStkBedYaxis)			);		// 야드권상실적Y축1
						recPara.setField("YD_UP_WO_LOC_YAXIS2"		,"0"										);		// 야드권상실적Y축2
						recPara.setField("YD_UP_WO_YAXIS_GAP_MAX"	,String.valueOf(intYdStkBedYaxisTol)		);				
						recPara.setField("YD_UP_WO_YAXIS_GAP_MIN"	,String.valueOf(intYdStkBedYaxisTol)		);				
						recPara.setField("YD_UP_WO_LOC_ZAXIS"		,recSch.getField("YD_UP_WO_LOC_ZAXIS")		);		// 야드권상실적Z축
						recPara.setField("YD_UP_WO_ZAXIS_GAP_MAX"	,recSch.getField("YD_UP_WO_ZAXIS_GAP_MAX")	);	
						recPara.setField("YD_UP_WO_ZAXIS_GAP_MIN"	,recSch.getField("YD_UP_WO_ZAXIS_GAP_MIN")	);	
						recPara.setField("YD_UP_WRK_ACT_GP"			,"N"										);	
						recPara.setField("YD_DN_WO_LOC"				,"XX010101"									);		// 야드권하지시위치
						
			        	intRtnVal = commDao.insert(recPara, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.insYdCrnsch", logId, szMethodName, "크레인작업지시 등록");
			        	if (intRtnVal <= 0) {
							szRtnMsg = "기존 크레인작업지시 등록 오류 ::" + Integer.toString(intRtnVal);
							szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							
							return 0;
			        	}
			        }
					
				}
			}
        	
			

			szMsg = "[" + szOperationName + "] >>>> 신규 작업지시 전문 전송 .. 종료 :: " + szSendMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
        	

        } catch(Exception e) {
        	szRtnMsg = "00012 Zone Book-Out 크레인스케줄 생성  .. <br>" + szRtnMsg;
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

        } // end of try~catch

        return intRtnVal;

    } // end of procMake00012ZoneSchNY2()
    

    /**
     * 오퍼레이션명 : 적치단 Clear - 권하지시위치와 권하실적 위치가 틀릴경우 호출
     *
     * @param  ● getRecSet
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int clearYdStklyrY7(JDTORecordSet getRecSet, String pMODIFIER, String logId) throws JDTOException {

    	JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();

    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 	= null;

    	String 	szMsg 			= "";
    	String 	szMethodName 	= "clearYdStklyrY7";
    	String 	szOperationName = "적치단 Clear";

    	int 	intRtnVal 		= 1;
		String 	szYdDnWoLoc   	= "";
        String 	szYdDnWoLayer 	= "";
        String 	szStlNo 		= "";
        String	szStkLyr		= "";

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F"); // log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본

    	try{
    		int rowsize = getRecSet.size();
            getRecSet.first();
            getRecord = getRecSet.getRecord();

    		for(int ii=0; ii<rowsize; ii++) {

                //권하 지시위치 Clear
    			szYdDnWoLoc   	= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC");
                szYdDnWoLayer 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LAYER");
                szStlNo 		= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
                szStkLyr 		= ydDaoUtils.stringPlusInt(szYdDnWoLayer, ii);

                if ("".equals(szStlNo)) {
	                setRecord = JDTORecordFactory.getInstance().create();
	                setRecord.setField("YD_STK_COL_GP", 		szYdDnWoLoc.substring(0,6));
	                setRecord.setField("YD_STK_BED_NO", 		szYdDnWoLoc.substring(6,8));
	                setRecord.setField("YD_STK_LYR_NO",       	szStkLyr);
	                setRecord.setField("YD_STK_LYR_MTL_STAT",	"E");
	                setRecord.setField("STL_NO",              	"");
	                setRecord.setField("MODIFIER",            	pMODIFIER);

	                intRtnVal = ydStkLyrDao.updYdStklyrStat(setRecord);  	//적치단의 재료정보 Clear
                } else {
	                setRecord = JDTORecordFactory.getInstance().create();
	                setRecord.setField("YD_STK_COL_GP", 		szYdDnWoLoc.substring(0,6));
	                setRecord.setField("YD_STK_BED_NO", 		szYdDnWoLoc.substring(6,8));
	                setRecord.setField("STL_NO",              	szStlNo);
	                setRecord.setField("MODIFIER",            	pMODIFIER);
	                setRecord.setField("YD_GP",					JPlateYdConst.YD_GP_F_PLATE_YARD);
					
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 setRecord에 logId 추가 
	                setRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					
	                intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(setRecord);  //적치단의 재료정보 Clear
                }

                if (intRtnVal <= 0) {
    				szMsg = "[" + szOperationName + "] 저장위치 적치단 CLEAR시 오류 발생 .. " + Integer.toString(intRtnVal);
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
                }

                getRecSet.next();
                getRecord = getRecSet.getRecord();
            } //end of for


		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
	    }//end of try~catch

		return  intRtnVal;
    }//end of clearYdStklyrY7()

    /**
     * 오퍼레이션명 : 적치단 등록
     *
     * @param  ● getRecSet, sRealLyrNo
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public String regYdStklyrY7(JDTORecordSet pRecSet, String pMODIFIER, String[] pStlNo, String[] pYdDnWrLoc, String pYdUpWrkActGp, String pTopLyrNo, String logId)throws JDTOException {


    	JPlateYdCrnWrkMtlDAO 	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();

    	JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
    	JDTORecord setRecord 	= null;
    	JDTORecord crnRecord 	= null;

    	JDTORecordSet rsResult  = null;
    	JDTORecord recPara 		= null;
    	JDTORecord recTemp 		= null;

    	String 	szRtnMsg		= null;
    	String 	szMsg 			= "";
        String 	szMethodName	= "regYdStklyrY7";
        String 	szOperationName	= "적치단 등록";

        int 	intRtnVal 		= 0;

        String 	szYdEqpId 		= "";
        String 	szYdWbookId		= "";
        String 	szYdCrnSchId	= "";
		String	szYdUpWrLoc	   	= "";
		String	szYdUpWrLayer  	= "";
        String 	szYdDnWrLoc		= "";
        String 	szYdDnWrLayer	= "";
        String 	szStlNo			= "";
        String	szMODIFIER		= pMODIFIER;
		String	szYdStkColGp	= "";
		String	szYdStkBedNo	= "";
		String	szTopLyrNo		= "000";

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F"); // log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본

        JPlateYdStkLyrDAO ydStkLyrDao = new JPlateYdStkLyrDAO();

    	try {
			szMsg = "[" + szOperationName + "] ============================= 권하실적 적치단 등록 .. START :: ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szTopLyrNo = pTopLyrNo;
			if (szTopLyrNo == null || "".equals(szTopLyrNo)) {
				szTopLyrNo = "000";
			}

    		int rowsize = pRecSet.size();

    		boolean isLast = false;

        	for(int ii=0; ii<rowsize; ii++) {

        		pRecSet.absolute(ii+1);
        		getRecord = JDTORecordFactory.getInstance().create();
        		getRecord.setRecord(pRecSet.getRecord());

        		szYdEqpId 		= ydDaoUtils.paraRecChkNull(getRecord, "YD_EQP_ID");
        		szYdWbookId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID");
    	        szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_CRN_SCH_ID");

        		//권상 실적위치
        		szYdUpWrLoc	   	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
        		szYdUpWrLayer  	= ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LAYER");

                szMsg = "[" + szOperationName + "] 권상실적 위치 :: " + szYdUpWrLoc + ", 단::" + szYdUpWrLayer;
    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        		//권하 실적위치 등록
//        		szYdDnWrLoc	   	= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WR_LOC");
//        		szStlNo	 		= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");

        		szYdDnWrLoc	   	= pYdDnWrLoc[ii];
        		szStlNo	 		= pStlNo[ii];

/*
        		// 권하실적위치로 최상단 조회
        		// L2에서 전송하는 STL_NO1~15 , YD_DN_WR_LOC1 ~ 15 는 횡행작업일때만 존재함 ----> (무조건 존재 , 15건 으로 변경)
        		if (pYdDnWrLoc != null && pStlNo != null) {
	        		for(int kk=0; kk<pYdDnWrLoc.length; kk++) {
	        			if (pStlNo[kk] != null && pStlNo[kk].equals(szStlNo)) {
	                		szYdDnWrLoc	= pYdDnWrLoc[kk];
	        				break;
	        			}
	        		}
        		}
*/

    	        szYdStkColGp   = ydUtils.substr(szYdDnWrLoc, 0, 6);
    	        szYdStkBedNo   = ydUtils.substr(szYdDnWrLoc, 6, 2);

    	        if ("BS".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyBsLoc(szYdStkColGp, szYdStkBedNo, szStlNo);
        	        szYdDnWrLayer = "001";
                    szMsg = "[" + szOperationName + "] 보수장일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    	        } else if ("CN".equals(ydUtils.substr(szYdStkColGp, 2, 2))) {

    	        	szYdDnWrLoc   = ydStkLyrDao.getEmptyCncLoc(szYdStkColGp, szYdStkBedNo, szStlNo);
        	        szYdDnWrLayer = "001";
                    szMsg = "[" + szOperationName + "] 가스장일때 적치가능 베드 다시 조회 >>>> " + szYdDnWrLoc;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    	        } else {

	    	        // 저장위치의 최상단 정보 조회
   	        		szYdDnWrLayer = JPlateYdCommonUtils.getTopLyrNoByColGp(szYdStkColGp, szYdStkBedNo, szStlNo, pYdUpWrkActGp, szTopLyrNo);

	                szMsg = "[" + szOperationName + "] 권하실적 위치 :: " + szYdDnWrLoc + ", 단::" + szYdDnWrLayer + ", 재료번호::" + szStlNo;
	    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    			if ("".equals(szYdDnWrLayer) || "000".equals(szYdDnWrLayer)) {
	    				szRtnMsg = "저장위치의 최상단 조회 오류 .. " + szYdStkColGp + szYdStkBedNo;
	    				return szRtnMsg;
	    			}
    	        }

        		//크레인에 UPDATE (크레인 적치상태 Clear)
    			crnRecord = JDTORecordFactory.getInstance().create();
    			crnRecord.setField("YD_STK_COL_GP",       	szYdEqpId);
    			crnRecord.setField("YD_STK_BED_NO",       	"01");
    			crnRecord.setField("YD_STK_LYR_NO",       	ydUtils.addLeftStr(Integer.toString(ii+1), 3, '0'));
                crnRecord.setField("YD_STK_LYR_MTL_STAT", 	"E");
                crnRecord.setField("STL_NO",              	"");
                crnRecord.setField("MODIFIER",              szMODIFIER);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 crnRecord에 logId 추가 
                crnRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                intRtnVal = this.updYdStklyrY7(crnRecord);  //크레인 적치단의 재료정보 UPDATE

                szMsg = "[" + szOperationName + "] ============================= 적치단 업데이트 처리 =============================" + intRtnVal;
    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    			setRecord = JDTORecordFactory.getInstance().create();
    			setRecord.setField("YD_STK_COL_GP",			ydUtils.substr(szYdDnWrLoc,0,6));
        		setRecord.setField("YD_STK_BED_NO",       	ydUtils.substr(szYdDnWrLoc,6,2));
                setRecord.setField("YD_STK_LYR_NO", 		szYdDnWrLayer);
                setRecord.setField("STL_NO",              	szStlNo);
                setRecord.setField("MODIFIER",              szMODIFIER);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 setRecord에 logId 추가 
                setRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                // 권하실적 처리시 해당 저장위치에 다른 재료 권하 예약 되어 있으면 CLEAR 처리함 ()
                szRtnMsg = this.clearDnLocOtherMtl(setRecord);
                if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                    szMsg = "[" + szOperationName + "] >>>> 다른 재료 권하 예약 CLEAR 결과 >>>> " + szRtnMsg;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
                	return szRtnMsg;
                }

                //-------------------------------------------------------------------------------------------------------------
                //	같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 경우에는 권상대기 상태로 변경을 하고
                //	그렇지 않은 경우에는 적치중 상태로 변경한다.
                //-------------------------------------------------------------------------------------------------------------
                szMsg = "[" + szOperationName + "] 같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 시작";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		        recPara	= JDTORecordFactory.getInstance().create();
                recPara.setField("YD_CRN_SCH_ID",	szYdCrnSchId);
                recPara.setField("YD_WBOOK_ID",     szYdWbookId);
                recPara.setField("STL_NO",          szStlNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recPara에 logId 추가 
                recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                rsResult = JDTORecordFactory.getInstance().createRecordSet("temp");
                szRtnMsg = ydCrnWrkMtlDao.getGreaterThanCrnSch(recPara, rsResult);		// intGp == 17

                szMsg = "[" + szOperationName + "] 같은 작업예약의 다음 크레인스케줄들중에서 해당재료가 크레인작업재료로 등록되어 있는 지 조회 완료 - 메세지 : " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

                //-------------------------------------------------------------------------------------------------------------
                if (JPlateYdConst.RETN_CD_NOTEXIST.equals(szRtnMsg)) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", "C");					//적치중
                	isLast = true;
                } else if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
                	setRecord.setField("YD_STK_LYR_MTL_STAT", "U");					//권상대기
                	isLast = false;
                } else {
                	szMsg = "[" + szOperationName + "] 다음크레인스케줄의 작업재료를 조회 중 오류발생 - " + szRtnMsg;
        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
        			setRecord.setField("YD_STK_LYR_MTL_STAT", "C");					//적치중으로 반영
        			isLast = true;
                }
                //-------------------------------------------------------------------------------------------------------------
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "isLast = " +  Boolean.toString(isLast), JPlateYdConst.DEBUG, logId);
                if (isLast) {
	                // 적치단에 존재하는 해당 저장품정보를 모두 CLEAR한다.
	                recTemp = JDTORecordFactory.getInstance().create();
	                recTemp.setField("STL_NO", 			szStlNo);
	                recTemp.setField("YD_STK_COL_GP",	ydUtils.substr(szYdUpWrLoc,0,6));
	                recTemp.setField("YD_STK_BED_NO",   ydUtils.substr(szYdUpWrLoc,6,2));
	                recTemp.setField("YD_STK_LYR_NO", 	szYdUpWrLayer);
	                recTemp.setField("MODIFIER",        szMODIFIER);
	                recTemp.setField("YD_GP",			JPlateYdConst.YD_GP_F_PLATE_YARD);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 recTemp에 logId 추가 
	                recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

	           // 	intRtnVal = ydStkLyrDao.updYdStklyrClear(recTemp);
	                intRtnVal = ydStkLyrDao.updYdStklyrClearByStlNo(recTemp);  	//적치단의 재료정보 Clear
                }

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 setRecord에 logId 추가 
                setRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

                // 권하위치 적치상태 변경
                intRtnVal = this.updYdStklyrY7(setRecord);

        	}

			szMsg = "[" + szOperationName + "] ============================= 권하실적 적치단 등록 .. END";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_SESSION_NAME + e.getMessage(), e);
	    }//end of try~catch

		return JPlateYdConst.RETN_CD_SUCCESS;

    } //end of regYdStklyrY7()
    
    /**
     * 오퍼레이션명 : 차량 Setting
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y2SetYdCar (JDTORecordSet inRecordSet, int intGp, String logId) throws JDTOException{

    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
    	
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	String szOperationName              = "후판정정 차량 Setting";
    	String szMethodName 				= "Y2SetYdCar";
    	String szMsg 						= "";
    	
    	long lngYD_MTL_WT                  = 0;
    	int  intYD_MTL_SH                  = 0;
    	long lngYD_EQP_WRK_WT              = 0;
    	int  intYD_EQP_WRK_SH              = 0;
    	
    	int intRtnVal = 0 ;
    	
    	//차량 스케줄 ID
    	String szYD_CAR_SCH_ID = "";

    	if(ydLogUtils.isEmpty(logId)) logId 		= ydLogUtils.getLogIdNew("P");				// log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

		szMsg = "후판제품 차량 Setting 처리(" + szMethodName + ") 시작";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    	
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
               
			intRtnVal = this.Y2GetYdCarsch(setRecord, outRecSet, 3) ;
			
	    	if (intRtnVal <= 0) return -1 ;
	    	
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
	    	
	    	setRecord.setField("ARR_WLOC_CD", "DKY23");
	    	
	    	intRtnVal = ydCarSchDao.updYdCarsch(setRecord, 0);
	    	if(intRtnVal < 0 ) {
                szMsg = "권하작업시 차량스케줄에 착지개소코드 등록중 Error!! Code No :" + intRtnVal;
                ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
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
		    		intRtnVal = this.Y2UpdCarftmvmtl(setRecord, 0) ;
		    		
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
		    		
		    		intRtnVal = this.Y2InsYdCarftmvmtl(setRecord) ;
		    		
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
            ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);   
            throw new JDTOException("<procY4CrnUdWr> Y4SetYdCar " + szMsg);
		}//end of try~catch

    	szMsg = "후판제품 차량 Setting 처리(" + szMethodName + ") 완료";
    	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		
    	return 1 ;
    	
    }//end of Y2SetYdCar()
    
    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *  
     * @param msgRecord, outRecset, intGp(1:상하차)
     * @return intRtnVal
     * @throws 
     */
    public int Y2GetYdCarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp)throws JDTOException{
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMethodName = "Y2GetYdCarsch";
    	String szMsg        = "";
    	String szOperationName              = "후판정정 차량 스케줄 Select";
        try{
        	
        	intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);
	        
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="[" + szOperationName + "] data not found";
					ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);   
				}else if(intRtnVal == -2) {
					szMsg="[" + szOperationName + "] parameter error";
					ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);   
				}
				if (intRtnVal <= 0) return intRtnVal = -2;
			}
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);     
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
    public int Y2UpdCarftmvmtl (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
    	
    	String szMethodName 		= "Y2UpdCarftmvmtl";
    	String szMsg        		= "";
    	String szOperationName      = "후판제품 차량 이송재료 Update";
        try{
        	
            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="[" + szOperationName + "] data not found!!";
    				ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.WARNING); 
    			}else if(intRtnVal == -1) {
    				szMsg="duplicate data,";
    				ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR); 
    			}else if(intRtnVal == -2) {
    				szMsg="[" + szOperationName + "] parameter error";
    				ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR); 
    			}else if(intRtnVal == -3){
    				szMsg="[" + szOperationName + "] execution failed";
    				ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR); 
    			}
    			return intRtnVal;
    		}
		
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);   
            throw new JDTOException("<procY4CrnUdWr> Y4UpdCarftmvmtl" + szMsg);
	    }//end of try~catch	
		
		return intRtnVal ;
		
    }//end of Y2UpdCarftmvmtl
    
    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *  
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y2InsYdCarftmvmtl(JDTORecord msgRecord) throws JDTOException {
    	YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
    	
    	int intRtnVal = 0 ;

    	String szMethodName 		= "Y2InsYdCarftmvmtl";
    	String szMsg        		= "";
    	String szOperationName      = "후판제품 차량이송재료 Insert";
        try{
        	intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
    		if(intRtnVal == -2) {
				szMsg="[" + szOperationName + "] parameter error";
				ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR); 
				return intRtnVal;
    		}
        }catch(Exception e){
            /*
             * 2010.12.15 윤재광 - 예외처리
             * 차량재료 등록시 중복현상이 발생해서 에러발생.
             * 원인 : 상차스케쥴 일부분 수행중 작업취소 후 재 스케쥴 등록 작업. 
             */
        	szMsg = "차량재료 등록시 중복현상이 발생해서 에러 : "+ e.getLocalizedMessage();
        	ydLogUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR); 
            //throw new JDTOException("<procY4CrnUdWr> Y4InsYdCarftmvmtl" + szMsg);
        }//end of try~catch
        
        return intRtnVal;
    }//end of Y2InsYdCarftmvmtl
    
    /**
	 * 오퍼레이션명 : 차량작업진행관리 _PI
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int procY2CarWrkStatCtr(JDTORecord msgRecord)throws JDTOException  {
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
	    String szMethodName    			= "procY2CarWrkStatCtr";
	    String szOperationName    		= "차량작업진행관리(후판정정)";
	    
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
	    
	    String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
	    
	    try{
	    	//--------------------------------------------------------------------------------------------------
	    	//	파라미터 확인
	    	//--------------------------------------------------------------------------------------------------
	    	szMsg="["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
	    	 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
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
	    	//251028 김정헌 책임 요청 경남99바9622 상차시 출하 작업 중간에 횡작업 스케줄이 만들어져 (재료 길이 8900, 3600) 한 작업예약의 시작,끝 스케줄로 완료판단하면 안됨
	    	//차량 기준으로 CARFTMVMTL 에 모두 실렷는지 아닌지 판단하는 로직 필요함 
	    	
	    	//--------------------------------------------------------------------------------------------------
	    	// 작업예약ID로 크레인 스케줄 조회
	    	//--------------------------------------------------------------------------------------------------
	    	szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄 조회 시작";
	    	 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	    	
	    	if(szCAR_LDUD_GP.equals("L")){
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 31);
		    	if(intRtnVal <= 0) {
					szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄을 조회 중 Error";
					 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					throw new JDTOException("<procY1CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}else{
	    		intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 32);
		    	if(intRtnVal <= 0) {
					szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄을 조회 중 Error";
					 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					throw new JDTOException("<procY1CarWrkStatCtr> getYdCrnsch" + szMsg);
		    	}
	    	}
	    	
	    	
	    	szMsg="["+ szOperationName +"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인 스케줄 조회 성공 - 대상재건수["+rsResult.size()+"]";
	    	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    	
			
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
	    	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
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
	    		 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);

    			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
	    		if (intRtnVal <= 0){
		    		szMsg = "차량에서 상차작업 처리시 차량스케쥴 정보 오류발생.--> SKIP처리 함";
		    		 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);	   
		            return 1;
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
	    		 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    		//--------------------------------------------------------------------------------------------------
	    		
	    		
	    		//--------------------------------------------------------------------------------------------------
	    		//출하차량인 경우에만 적용한다. - 일품 상차실적 송신 YDDMR012 (후판일품출하상차실적)
	    		//--------------------------------------------------------------------------------------------------
	    		if(szYD_CAR_USE_GP.equals("G")){
	    			
					//--출하고도화 이후 방식-----------------------------------------------------------------
	    			
					szMsg="["+ szOperationName +"] 출하차량 일품 상차실적 송신 M10YDLMJ1082(YDDMR012) (후판일품출하상차실적) 시작";
					 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);	
					
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
					 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);    			

					
					for(int ii = 0; ii < rsCrnWrkMtl.size(); ii++) {
						
						recMtl = rsCrnWrkMtl.getRecord(ii);
						
						recInTemp.setField("STL_NO",	recMtl.getFieldString("STL_NO"));
//PIDEV						
	    				//검수 테이블 생성 //////////////////////////////////////////////////////////////
	    				// ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW2_PIDEV
	    				intRtnVal = ydStockDao.updYdStockExa_PIDEV(recInTemp, 0);			
	    				if(intRtnVal >0){
	    					szMsg = "수신한 재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록이 되었습니다.";
	    					 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	
	    				}else if(intRtnVal == 0){
	    					szMsg = "수신한  재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록 되었거나  실패 하였습니다.";
	    					 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
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
	    			 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("YD_CAR_PROG_STAT", "5");
	    			recInTemp.setField("YD_EQP_WRK_STAT", "L");
	    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 1);
	    			if(intRtnVal <= 0) {
	    				szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 시 오류" + intRtnVal;
	    				 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	    			}
	    			
	    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 완료";
	    			 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    			//--------------------------------------------------------------------------------------------------
	    			
	    			
	    			//--------------------------------------------------------------------------------------------------
	    			//	상차완료 전문 송신
	    			//--------------------------------------------------------------------------------------------------
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    			
	    			if(szYD_CAR_USE_GP.equals("L")){							//구내운송 - 임춘수 수정 2009.06.15
	    				szMsg="["+ szOperationName +"] 구내운송 처리사항 없음";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						
	    			}else{
	    				//상차작업완료 송신 YDDMR016 (후판출하상차완료)
	    				JDTORecord recPara = JDTORecordFactory.getInstance().create();
//	    				recPara.setField("MSG_ID",    					YdConstant.YDYDJ701);
//							recPara.setField(YdConstant.BUFFER_TC_CD, 		"YDDMR016");
	    				recPara.setField("MQ_TC_CD"      , "M10YDLMJ1092");
	    				recPara.setField("YD_SCH_CD"     , szYD_SCH_CD);
	    				recPara.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
	    				recPara.setField("YD_GP"         , szYD_GP);
	    				recPara.setField("CARLD_PNT_CD"  , szCARLD_PNT_CD);
		    			
		    			ydDelegate.sendMsg(recPara);
		    			
						szMsg="["+ szOperationName +"] 상차작업완료 송신 완료";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    	    	}
	    		}
	    	//--------------------------------------------------------------------------------------------------
	    	//플래그가 하차인 경우
	    	//--------------------------------------------------------------------------------------------------
	    	}else if(szCAR_LDUD_GP.equals("L"))	{
	    		szMsg="["+ szOperationName +"] 하차는 처리사항 없음";
	    		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    	}else{
				szMsg="["+ szOperationName +"] 상차 및 하차 구분 플래그 Error";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return intRtnVal = -1;
	    	}

		}catch(Exception e){
	
			szMsg="["+ szOperationName +"] 차량 작업 진행관리 처리 Error:" +e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			throw new JDTOException("<procY4CrnUdWr> =" + szMsg);
		}
	
	
		szMsg="["+ szOperationName +"] 차량 작업 진행관리 처리"+szMethodName+") 완료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		return intRtnVal = 1;
	}
	
	/**
	 * 오퍼레이션명 : 차량작업진행관리 신규모듈(상차완료 인식 방식 개선)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int procY2CarWrkStatCtrNew(JDTORecord msgRecord)throws JDTOException  {
	
		YdDelegate ydDelegate = new YdDelegate();
		YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
		YdCarSchDao     ydCarSchDao     = new YdCarSchDao();  
		YdStockDao      ydStockDao      = new YdStockDao();  
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsResultCarMtl    = null;
		JDTORecordSet rsCrnWrkMtl		= null;
		JDTORecord	  recMtl			= null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recFirst          = null;
		JDTORecord    recLast           = null;
		
	    int intRtnVal 		   			= 0 ;
	    
	    String szMsg           			= "";
	    String szMethodName    			= "procY2CarWrkStatCtrNew";
	    String szOperationName    		= "차량작업진행관리(후판정정)";
	    
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
	    String szTRANS_ORD_DATE			= null;
	    String szTRANS_ORD_SEQNO		= null;
	    String szCARLD_PNT_CD			= "";
	    String sCAR_KIND			    = "";
	    
	    String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
	    
	    try{
	    	//--------------------------------------------------------------------------------------------------
	    	//	파라미터 확인
	    	//--------------------------------------------------------------------------------------------------
	    	szMsg="["+ szOperationName +"] 메소드 시작 - 파라미터 확인";
	    	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
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
	    	//251028 김정헌 책임 요청 경남99바9622 상차시 출하 작업 중간에 횡작업 스케줄이 만들어져 (재료 길이 8900, 3600) 한 작업예약의 시작,끝 스케줄로 완료판단하면 안됨
	    	//차량 기준으로 CARFTMVMTL 에 모두 실렷는지 아닌지 판단하는 로직 필요함 
	    	
	    	if(szCAR_LDUD_GP.equals("L"))	{
	    		szMsg="["+ szOperationName +"] 하차는 처리사항 없음";
	    		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    		return 1;
	    	}else if (!(szCAR_LDUD_GP.equals("L") || szCAR_LDUD_GP.equals("U"))) {
				szMsg="["+ szOperationName +"] 상차 및 하차 구분 플래그 Error";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return intRtnVal = -1;
	    	}
	    	
	    	//--------------------------------------------------------------------------------------------------
    		//	상차인 경우 상차작업예약ID로 차량스케줄을 조회
    		//--------------------------------------------------------------------------------------------------
    		szMsg="["+ szOperationName +"] 상차인 경우 상차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄 조회 시작";
    		 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    	
	    	
    		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp = JDTORecordFactory.getInstance().create();
 			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);

 			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 3);
    		if (intRtnVal <= 0){
	    		szMsg = "차량에서 상차작업 처리시 차량스케쥴 정보 오류발생.--> SKIP처리 함";
	    		 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);	   
	            return 1;
	    	}

	    		
    		rsResult.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult.getRecord()); 
    		
    		szYD_CAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_SCH_ID"); 	
    		szYD_CAR_USE_GP 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_USE_GP");
    		szCAR_NO 				= ydDaoUtils.paraRecChkNull(recOutTemp, "CAR_NO");
    		szCARD_NO				= ydDaoUtils.paraRecChkNull(recOutTemp, "CARD_NO");
    	    szTRANS_ORD_DATE		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_DATE");
    	    szTRANS_ORD_SEQNO		= ydDaoUtils.paraRecChkNull(recOutTemp, "TRANS_ORD_SEQNO");
    	    sCAR_KIND				= ydDaoUtils.paraRecChkNull(recOutTemp, "CAR_KIND");

    		szMsg="["+ szOperationName +"] 상차인 경우 상차작업예약ID["+szYD_WBOOK_ID+"]로 차량스케줄["+szYD_CAR_SCH_ID+"], 차량사용구분["+szYD_CAR_USE_GP+"] 조회 완료";
    		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    		
    		if(!szYD_CAR_USE_GP.equals("G")){
    			szMsg="["+ szOperationName +"] 출하차량 아닌경우 처리사항 없음 ";
        		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        		return 1;
    		}
    		//마지막 상차스케줄인지 검사 
    		//차량스케줄 id 로 차량 작업재료 및 지시일자, 지시번호로 총 상차대상 조회하여, 작업재료에 모두 실린것이 아니라면, 상차완료 아님 
    		
    		//차량스케줄id로 지시번호에 해당하는 재료 갯수 select 
    		recInTemp = JDTORecordFactory.getInstance().create();
 			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
 			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
 			rsResultCarMtl = JDTORecordFactory.getInstance().createRecordSet("");
 			
 			JPlateYdCommDAO  commDao 	= new JPlateYdCommDAO();
 			
 			rsResult = commDao.select(recInTemp, "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getTransStockByCarSchId", logId, szMethodName, "차량스케줄id로 지시 재료 조회");
    		
 			
 			rsResultCarMtl = commDao.select(recInTemp, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarMtlYMins", logId, szMethodName, "차량스케줄id로 차량재료 조회");
 			
 			if(rsResult.size() <= 0 ){
 				szMsg="["+ szOperationName +"] 차량스케줄 id ["+szYD_CAR_SCH_ID+"] 지시재료 없음 ";
        		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        		return -1;
 			}
 			
 			int transOrdCnt = rsResult.size(); //지시재료 갯수
 			int carLdComplteCnt = rsResultCarMtl.size(); // 상차완료한 갯수
 			
 			
 			szMsg="["+ szOperationName +"] 차량스케줄 id ["+szYD_CAR_SCH_ID+"] 지시재료 매수 ["+Integer.toString(transOrdCnt)+"] 상차완료매수 ["+Integer.toString(carLdComplteCnt)+"] ";
    		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
 			
 			
 			boolean isCarLdComplete = false;
 			
 			if(carLdComplteCnt >= transOrdCnt){
 				isCarLdComplete = true;
 			}
 			
 			
    		//--출하고도화 이후 방식-----------------------------------------------------------------
			szMsg="["+ szOperationName +"] 출하차량 일품 상차실적 송신 M10YDLMJ1082(YDDMR012) (후판일품출하상차실적) 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);	
    		
    		
			recInTemp = JDTORecordFactory.getInstance().create();
    		
			for(int ii = 0; ii < rsCrnWrkMtl.size(); ii++) {
				
				recMtl = rsCrnWrkMtl.getRecord(ii);
//PIDEV			
				recInTemp.setField("MQ_TC_CD"    ,       "M10YDLMJ1082");
				recInTemp.setField("YD_GP",              "P");
				recInTemp.setField("TRANS_WORD_DATE",    szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_WORD_SEQNO",   szTRANS_ORD_SEQNO);
				recInTemp.setField("CAR_NO",             szCAR_NO);
				recInTemp.setField("CARD_NO",            szCARD_NO);
				recInTemp.setField("GOODS_NO",           recMtl.getFieldString("STL_NO"));
				recInTemp.setField("CARLD_PNT_CD",       szCARLD_PNT_CD);
				
				if(isCarLdComplete && (ii+1) == rsCrnWrkMtl.size()) {
					//마지막 스케줄 ID이고 작업재료의 마지막일경우
					recInTemp.setField("GOODS_EA","*");
				} else {
					recInTemp.setField("GOODS_EA","1");
				}
				
				ydDelegate.sendMsg(recInTemp);
			}
			szMsg="["+ szOperationName +"] 출하차량 일품 상차실적 송신 M10YDLMJ1082(YDDMR012) (후판일품출하상차실적) 완료";
			 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);    			

			
			for(int ii = 0; ii < rsCrnWrkMtl.size(); ii++) {
				
				recMtl = rsCrnWrkMtl.getRecord(ii);
				
				recInTemp.setField("STL_NO",	recMtl.getFieldString("STL_NO"));
//PIDEV						
				//검수 테이블 생성 //////////////////////////////////////////////////////////////
				// ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW2_PIDEV
				intRtnVal = ydStockDao.updYdStockExa_PIDEV(recInTemp, 0);			
				if(intRtnVal >0){
					szMsg = "수신한 재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록이 되었습니다.";
					 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				}else if(intRtnVal == 0){
					szMsg = "수신한  재료번호 ["+ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO")+"]에 대한 검수 DATA등록 되었거나  실패 하였습니다.";
					 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	 			}
				///////////////////////////////////////////////////////////////////////////////		
			}
    		//--------------------------------------------------------------------------------------------------
	    		
	    		
	 
	    		
    		//--------------------------------------------------------------------------------------------------
    		//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교
    		//--------------------------------------------------------------------------------------------------
    		if(isCarLdComplete) {
    			//--------------------------------------------------------------------------------------------------
    			//동일하면 차량스케줄에 상차완료일시 등록, 설비작업상태 = '영차'
    			//--------------------------------------------------------------------------------------------------
    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 시작";
    			 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				
    			recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("YD_CAR_PROG_STAT", "5");
    			recInTemp.setField("YD_EQP_WRK_STAT", "L");
    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 1);
    			if(intRtnVal <= 0) {
    				szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 시 오류" + intRtnVal;
    				 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    			}
    			
    			szMsg="["+ szOperationName +"] 차량스케줄["+szYD_CAR_SCH_ID+"], 상차작업예약ID["+szYD_WBOOK_ID+"]의 상차완료 수정 완료";
    			 ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    			//--------------------------------------------------------------------------------------------------
    			
    			
    			//--------------------------------------------------------------------------------------------------
    			//	상차완료 전문 송신
    			//--------------------------------------------------------------------------------------------------
    			recInTemp = JDTORecordFactory.getInstance().create();
    			
    			if(szYD_CAR_USE_GP.equals("L")){							
    				szMsg="["+ szOperationName +"] 구내운송 처리사항 없음";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					
    			}else{
    				//상차작업완료 송신 YDDMR016 (후판출하상차완료)
    				JDTORecord recPara = JDTORecordFactory.getInstance().create();
    				recPara.setField("MQ_TC_CD"      , "M10YDLMJ1092");
    				recPara.setField("YD_SCH_CD"     , szYD_SCH_CD);
    				recPara.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
    				recPara.setField("YD_GP"         , szYD_GP);
    				recPara.setField("CARLD_PNT_CD"  , szCARLD_PNT_CD);
	    			
	    			ydDelegate.sendMsg(recPara);
	    			
					szMsg="["+ szOperationName +"] 상차작업완료 송신 완료";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    	    	}
    		}
    		szMsg="["+ szOperationName +"] 차량 작업 진행관리 처리"+szMethodName+") 완료";
    		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    		return intRtnVal = 1;


		}catch(Exception e){
	
			szMsg="["+ szOperationName +"] 차량 작업 진행관리 처리 Error:" +e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			throw new JDTOException("<procY4CrnUdWr> =" + szMsg);
		}
	
	}
	
    
    
} // end of class JPlateYdCrnUnloadWrkSeEJBBean

