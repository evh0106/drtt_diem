/**
 * @(#)YmCommCarMvSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/22
 * 
 * @description      YM야드공통 차량이동 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/22   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcommon.session; 

import java.util.ArrayList;
import java.util.List;


import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
//import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.bslab.session.BSlabComm;
//import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

/**
 *      [A] 클래스명 : YM야드공통 차량이동 처리
 *
 * @ejb.bean name="YmCommCarMvSeEJB" jndi-name="YmCommCarMvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required" 
*/

public class YmCommCarMvSeEJBSBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private YmComm ymComm = new YmComm();
	private BSlabComm bSlabComm = new BSlabComm();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * [A] 오퍼레이션명 : A,B 열연 개소코드 인지 체크 
	 * @param sWlocCd
	 * @return boolean (true: A,B열연 개소코드)
	 */
	private boolean getABLocationInfo_02(String sWlocCd) {
		
		if (
				YmConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(sWlocCd)	//D2Y43 : A연주-B Cast Slab Yard 
			||	YmConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(sWlocCd)		//D2Y44 : A열연-#1 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(sWlocCd)		//D2Y45 : A열연-#2 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(sWlocCd)		//D3Y41 : B열연-#1 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(sWlocCd)		//D3Y42 : B열연-#2 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_B_HR_SLAB_YARD.equals(sWlocCd)			//D3Y43 : B열연-Slab Yard
			||	YmConstant.WLOC_CD_B_HR_REFUR_SLAB_YARD.equals(sWlocCd)		//D3Y44 : B열연-가열로 Slab Yard
		  ) 
		{
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 야드 발지,착지개소코드로 이송방향 체크
	 * @param sWlocCd, sARR_WLOC_CD, sARR_WLOC_CD, logId
	 * @return String ("AA","AC","CA","CC")
	 */
	private String getABLocationInfo_01(String sSPOS_WLOC_CD, String sARR_WLOC_CD, String logId) {
		
		String szMsg = "<<<< getABLocationInfo_01 결과 : ";
		String sWorkGp;
		
		if (getABLocationInfo_02(sSPOS_WLOC_CD)) {
			//발지개소코드가 AB열연
			
			if (getABLocationInfo_02(sARR_WLOC_CD)) {
				//착지개소가 AB열연이면
				sWorkGp	= "AA"; 
				szMsg += sWorkGp + " AB열연에서 AB열연으로 이송 >>>>"; 
			}
			else {
				//착지개소가 AB열연이 아니면
				sWorkGp	= "AC"; 
				szMsg += sWorkGp + " AB열연에서 일관제철로 이송 >>>>"; 
			}
		} else {
			//발지개소코드가 AB열연이 아니면
			
			if (getABLocationInfo_02(sARR_WLOC_CD)) {
				//착지개소가 AB열연이면
				sWorkGp	= "CA"; 
				szMsg += sWorkGp + " 일관제철에서 AB열연으로 이송 >>>>"; 
			}
			else {
				//착지개소가 AB열연이 아니면
				sWorkGp	= "CC"; 
				szMsg += sWorkGp + " 일관제철에서 일관제철로 이송 >>>>"; 
			}
		}
		commUtils.printLog(logId, szMsg, "SL");
		return sWorkGp;
	}
	
	/**
	 * [A] 오퍼레이션명 : B열연 슬라브의 목적동
	 * @param s_CAR_SCH_ID,szSPOS_WLOC_CD
	 * @return String 
	 */
	private String getSlabAimCd(String s_CAR_SCH_ID, String szSPOS_WLOC_CD, String logId, String methodNm) {
		
		JDTORecordSet rsResult    	= null;
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		String szMsg = "<<<< getSlabAimCd 결과 : ";
		String s_STACK_BAY_GP = "";
		
		try { 
			if ("D3Y43".equals(szSPOS_WLOC_CD)) { //B열연 SLAB야드
				
				jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListSposwlocCD", logId, methodNm, "YD_차량스케줄 테이블에서 차량스케줄ID로 발지개소코드 조회");
				if (rsResult.size() > 0) {
					szSPOS_WLOC_CD = commUtils.trim(rsResult.getRecord(0).getFieldString("SPOS_WLOC_CD"));
				}
			}  
				
			if ("D2Y43".equals(szSPOS_WLOC_CD)) { //B열연 BCast
				
				jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListAimBay_BCast", logId, methodNm, "B-Cast SLAB야드에서 이송시 목적동 조회");
				if (rsResult.size() > 0) {
					s_STACK_BAY_GP = commUtils.nvl(rsResult.getRecord(0).getFieldString("DEST_BAY"),"D");
				}
				
			} else if("DJY25".equals(szSPOS_WLOC_CD)||"DYY15".equals(szSPOS_WLOC_CD)||"BSY01".equals(szSPOS_WLOC_CD)||"BSY02".equals(szSPOS_WLOC_CD)||"BSY03".equals(szSPOS_WLOC_CD)) { //(비상야드추가)
				
				jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListAimBay_Port", logId, methodNm, "통합야드에서 이송시 목적동 조회");
				if (rsResult.size() > 0) {
					s_STACK_BAY_GP = commUtils.nvl(rsResult.getRecord(0).getFieldString("DEST_BAY"),"D");
				}
				
			} else if ("C3S01".equals(szSPOS_WLOC_CD)) { //C3스카핑야드
				
				jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListAimBay_C3Cast", logId, methodNm, "C3야드에서 이송시 목적동 조회");
				if (rsResult.size() > 0) {
					s_STACK_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("DEST_BAY"));
				}
				
			} else if ("DHY21".equals(szSPOS_WLOC_CD)) { //C연주
				
				//해당 차량스케줄에서 WAIT_ARR_GP 항목을 체크한다. (WAIT_ARR_GP 출발실적 발생시 목적동 셋팅)
				//값이 있으면 그 값이 목적동이고 값이 없으면 아래 로직 수행
				jrParam.setField("YD_CAR_SCH_ID"	, s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarSchBySchId", logId, methodNm, "차량스케줄id로 차량스케줄정보 조회");
				if (rsResult.size() > 0) {
					s_STACK_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("WAIT_ARR_GP"));
				}
				
				if ("".equals(s_STACK_BAY_GP)) {
					//먼저 PALLET 조회 화면에서 지정한 목적동이 있는지 확인하여 있으면 그 목적동을 사용한다.
					jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListAimBay_CCast2", logId, methodNm, "C연주야드에서 이송시 YD야드적치열의 YD_STKBED_USG_CD 에 PALLET 조회 화면에서 지정한 목적동 조회");
					if (rsResult.size() > 0) {
						s_STACK_BAY_GP = commUtils.nvl(rsResult.getRecord(0).getFieldString("DEST_BAY"),"D");
					} else {
						
						jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListAimBay_CCast", logId, methodNm, "C연주야드에서 이송시 목적동 조회");
						if (rsResult.size() > 0) {
							s_STACK_BAY_GP = commUtils.nvl(rsResult.getRecord(0).getFieldString("DEST_BAY"),"D");
						}
					}
				}
			}
			
			commUtils.printLog(logId, szMsg + s_STACK_BAY_GP , "SL");
			return s_STACK_BAY_GP;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}		
	}
	
	/**
	 *      [A] 오퍼레이션명 : 포인트지시(YDTSJ011) 전문 생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord makeYDTSJ011(String szTRN_EQP_CD, String szWLOC_CD, String szYD_PNT_CD, String szYD_MSG_NM, String logId)throws DAOException  {
		String methodNm = "포인트지시(YDTSJ011)전문 생성[YmCommCarMvSeEJB.makeYDTSJ011] ";
	    
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= null;
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
			////////////////////////////////////////////////////////////////////////////////////////
			//메세지 이력 관리
			//jrParam.setField("YD_MSG_NM"	, szYD_MSG_NM);
			//jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
			//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipMsgRecode", logId, methodNm, "TB_YM_CARSCH 메세지 이력 관리");
			
			//포인트지시 메세지 전송
			jrTemp = JDTORecordFactory.getInstance().create();

			jrTemp.setResultCode(logId);	//Log ID
			jrTemp.setResultMsg(methodNm);	//Log Method Name
			jrTemp.setField("JMS_TC_CD"				, "YDTSJ011");
			jrTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			jrTemp.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
			jrTemp.setField("WLOC_CD"				, szWLOC_CD);
			jrTemp.setField("YD_PNT_CD"				, commUtils.nvl(szYD_PNT_CD,"0000"));
			jrTemp.setField("PNT_WO_GP"				, "A");
			jrTemp.setField("PNT_WO_DT"				, commUtils.getDateTime14());
			jrTemp.setField("YD_MSG_NM"				, szYD_MSG_NM);
			jrTemp.setField("TRN_WRK_MTL_GP"		, ""); //운송작업재료구분 (C:COIL제품,H:열연COIL소재,S:SLAB,L:냉연COIL소재) -- ??반드시 값을 전송해야 하는지? 한다면 어떻게 알 수 있는지?
			////////////////////////////////////////////////////////////////////////////////////////
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrTemp;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of makeYDTSJ011()		

	/**
	 *      [A] 오퍼레이션명 : 포인트지시(YDTSJ011) 전문 생성 (목적동 포함)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord makeYDTSJ011(String szTRN_EQP_CD, String szWLOC_CD, String szYD_PNT_CD, String szYD_MSG_NM, String szYD_BAY_GP ,String logId)throws DAOException  {
		String methodNm = "포인트지시(YDTSJ011)전문 생성[YmCommCarMvSeEJB.makeYDTSJ011] ";
	    
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= null;
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
			////////////////////////////////////////////////////////////////////////////////////////
			//메세지 이력 관리
			//jrParam.setField("YD_MSG_NM"	, szYD_MSG_NM);
			//jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
			//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipMsgRecode", logId, methodNm, "TB_YM_CARSCH 메세지 이력 관리");
			
			//포인트지시 메세지 전송
			jrTemp = JDTORecordFactory.getInstance().create();

			jrTemp.setResultCode(logId);	//Log ID
			jrTemp.setResultMsg(methodNm);	//Log Method Name
			jrTemp.setField("JMS_TC_CD"				, "YDTSJ011");
			jrTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			jrTemp.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
			jrTemp.setField("WLOC_CD"				, szWLOC_CD);
			jrTemp.setField("YD_PNT_CD"				, commUtils.nvl(szYD_PNT_CD,"0000"));
			//--------------------------------------------------------------------------------------------
			jrTemp.setField("YD_BAY_GP"				, commUtils.nvl(szYD_BAY_GP,""));  //목적동 추가 2020.04.21
			//--------------------------------------------------------------------------------------------
			jrTemp.setField("PNT_WO_GP"				, "A");
			jrTemp.setField("PNT_WO_DT"				, commUtils.getDateTime14());
			jrTemp.setField("YD_MSG_NM"				, szYD_MSG_NM);
			jrTemp.setField("TRN_WRK_MTL_GP"		, ""); //운송작업재료구분 (C:COIL제품,H:열연COIL소재,S:SLAB,L:냉연COIL소재) -- ??반드시 값을 전송해야 하는지? 한다면 어떻게 알 수 있는지?
			////////////////////////////////////////////////////////////////////////////////////////
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrTemp;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of makeYDTSJ011() - 목적동 포함		
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량도착Point요구(TSYDJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ002(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "소재차량도착Point요구[YmCommCarMvSeEJB.rcvTSYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
		
		//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		String msgId = commUtils.getMsgId(rcvMsg); 
        if (msgId==null || msgId.equals("")) {
        	return sndRecord;
        }
        
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "소재차량도착 수신 ", rcvMsg);
			
	    	//수신항목 변수 저장
			String szARR_WLOC_CD 	= commUtils.trim(rcvMsg.getFieldString("WLOC_CD")); 	//착지개소코드
				
			
			if (	YmConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD) //D2Y44 : A열연-#1 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)	//D2Y45 : A열연-#2 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y42 : B열연-#2 제품/소재 Coil Yard
			) {
				String  sAPP055_YN = ymComm.BCoilApplyYn("APP055","3","1"); //구내운송차량 대기장 도착 후 입동지시 여부
				
				if("Y".equals(sAPP055_YN))  {
					//1열연 Coil 야드 신규 소재차량도착Point요구 (대기장 도착 후 입동하는 방식)
					sndRecord = this.rcvTSYDJ002_WL(rcvMsg);
				} else {
					//1열연 Coil 야드 기존 소재차량도착Point요구
					sndRecord = this.rcvTSYDJ002_Comm(rcvMsg);
				}
				
			} else {
				
				//1열연 Slab 야드인 경우
				sndRecord = this.rcvTSYDJ002_Comm(rcvMsg);
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ003()		
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량도착Point요구(TSYDJ002) - 기존소스:TsInfoRegSBean.procMatlCarArrPntRequest
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvTSYDJ002_Comm(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "소재차량도착Point요구[YmCommCarMvSeEJB.rcvTSYDJ002_Comm] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
	    String szMsg           		= "";
	    String msgId				= null;
	    String modifier				= null;
	    
	    String szTRN_EQP_CD    		= "";	//운송장비코드
	    String szWLOC_CD       		= "";	//개소코드
	    String szTRN_WRK_FULLVOID_GP= "";	//운송작업영공구분
	    String szTRN_EQP_GP 		= "";	//PT/TR 구분
	    String szYD_MSG_NM			= "";
	    
	    String s_STACK_YD_GP 		= "";
	    String s_STACK_BAY_GP 		= "";
	    String s_WBOOK_ID 			= "";
	    String s_STACK_COL_GP		= "";
	    String s_YD_PNT_CD			= "0000";
	  
	    JDTORecordSet rsResult    	= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
	    
	    try{

			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "소재차량도착Point요구[TSYDJ002] 수신 ", rcvMsg);
			
	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); //운송장비코드
			szWLOC_CD      			= commUtils.trim(rcvMsg.getFieldString("WLOC_CD")); //개소코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분[상하차구분 E:공차상태(상차작업),F:영차(하차작업)]
			
			//운송장비코드길이가 3자리 이상인지 확인 (substring 전 에러 체크)
			if (szTRN_EQP_CD.length() < 3 ) {
				//throw new Exception("운송장비코드 오류 [" + szTRN_EQP_CD + "] 운송장비 구분(PT/TR)정보가 없습니다.");
				szMsg = "운송장비코드 오류 [" + szTRN_EQP_CD + "] 운송장비 구분(PT/TR)정보가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");

				return sndRecord ;
			}
	    	szTRN_EQP_GP = szTRN_EQP_CD.substring(1, 3);//PT/TR 구분
			
	    	//개소코드가 AB열연 개소코드인지 검사한다.
	    	if (!getABLocationInfo_02(szWLOC_CD)) {
				//throw new Exception("개소코드 오류 [" + szWLOC_CD + "]는 AB열연 개소코드가 아닙니다!");
				szMsg = "개소코드 오류 [" + szWLOC_CD + "]는 AB열연 개소코드가 아닙니다!" ;
				commUtils.printLog(logId, szMsg, "SL");
			
				return sndRecord ;
				
			}			
			
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ002"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
	    	
			//**********************************************************************************	
			// 장비코드로 포인트 재 요구 시 상차예약정지위치 검색 (20090828 정종균 추가 )
			jrParam.setField("WLOC_CD"		, szWLOC_CD);
			jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
			
			JDTORecordSet loadPointList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint2", logId, methodNm, "장비코드로 포인트 재 요구 시 상차예약정지위치 검색");
			
			if (loadPointList.size() > 0) {
				
				if("R".equals(commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))) {
					//이미 예약이 잡혀 있는 경우 포인트 지시 재송신
					szMsg="["+methodNm+"] 이미 예약이 잡혀 있는 경우   ---> 포인트 지시 재송신";
					commUtils.printLog(logId, szMsg, "SL");
					
					s_STACK_COL_GP = commUtils.trim(loadPointList.getRecord(0).getFieldString("STACK_COL_GP"));
					s_STACK_BAY_GP = commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_BAY_GP"));
					s_YD_PNT_CD = commUtils.nvl(loadPointList.getRecord(0).getFieldString("YD_PNT_CD"),"0000");
					
				} else if("L".equals(commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))) {
					//이미 도착한 상태인 경우 여기서 종료
					szMsg="["+methodNm+"] 이미 도착한 상태인 경우 여기서 종료 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					commUtils.printLog(logId, methodNm, "S-");
					return sndRecord;
					
				} else {
					
					szMsg="["+methodNm+"] YD_STK_COL_ACT_STAT : " + commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
					commUtils.printLog(logId, szMsg, "SL");
					
					commUtils.printLog(logId, methodNm, "S-");
					return sndRecord;
				}
				
			} else {
				
				if ("E".equals(szTRN_WRK_FULLVOID_GP)) {
					
					//--------------------------------------------------------------------------------
					// E:공차 --> 상차지 포인트를 찾아 포인트 지시 전송
					
					szMsg="["+methodNm+"] TRN_WRK_FULLVOID_GP가 'E':공차(상차작업) 포인트 요구 처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");

					
					if (YmConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szWLOC_CD)) { 
						//A연주-B Cast Slab Yard (D2Y43) BCast의 경우
						
						szMsg="["+methodNm+"] A연주-B Cast Slab Yard (D2Y43) BCast의 경우 ";
						commUtils.printLog(logId, szMsg, "SL");
						
						String aimBay_BCast = "";
						
						//a1)파레트 도착 목적동검색----------------------------
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListAimbayBCast", logId, methodNm, "파레트 도착 목적동검색");
						if (rsResult.size() > 0) {
							aimBay_BCast = commUtils.trim(rsResult.getRecord(0).getFieldString("AIM_BAY"));
							szMsg="["+methodNm+"] BCast 목적동 검색결과 : " + aimBay_BCast;
							commUtils.printLog(logId, szMsg, "SL");
						}
						
						//a2)상차정지위치 검색-------------------------------
						jrParam.setField("WLOC_CD"		, szWLOC_CD);
						jrParam.setField("YD_GP"		, YmConstant.YD_GP_0);
						jrParam.setField("BAY_GP"		, aimBay_BCast);
						jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint_1", logId, methodNm, "상차정지위치 검색");
						if (rsResult.size() > 0) {
							s_STACK_COL_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							s_YD_PNT_CD = commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_PNT_CD"),"0000");
							szMsg="["+methodNm+"] 상차정지위치 검색 결과 : " + s_STACK_COL_GP + "," + s_YD_PNT_CD;
							commUtils.printLog(logId, szMsg, "SL");
						} else {
							
							//a2-1)모든 포인트가 점유중이면 상차완료된 포인트를 찾음---------------------- 
							jrParam.setField("YD_GP"		, YmConstant.YD_GP_0);
							jrParam.setField("BAY_GP"		, aimBay_BCast);
							jrParam.setField("SECT_GP"		, szTRN_EQP_GP);
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadEndpoint", logId, methodNm, "상차완료된 포인트 검색");
							if (rsResult.size() > 0) {
								s_STACK_COL_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
								s_YD_PNT_CD = commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_PNT_CD"),"0000");
								szMsg="["+methodNm+"] 상차완료된 포인트 검색 결과 : " + s_STACK_COL_GP + "," + s_YD_PNT_CD;
								commUtils.printLog(logId, szMsg, "SL");
							} else {
								
								//a2-1-1)포인트 없으면 포인트 없음으로 지시주고 하위처리 안함------------------------
								szMsg="["+methodNm+"] 상차정지위치 및 상차완료위치 찾지 못함 ";
								commUtils.printLog(logId, szMsg, "SL");
								
								//대기장 포인트 사유 가져오기 (재료외형구분N:바인코일 BIC생산(소형압연)~제품창고 입고 이전(종합판정)
								szYD_MSG_NM = this.getCarMsg("N" ,YmConstant.YD_GP_0 , aimBay_BCast , szTRN_EQP_GP , szWLOC_CD, "", logId, methodNm);
								
								//0000 포인트지시 전송 
								sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, "0000", szYD_MSG_NM, s_STACK_BAY_GP, logId));
								
								commUtils.printLog(logId, methodNm, "S-");
								return sndRecord;
							}
						}
						
					} else if (	YmConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szWLOC_CD) 	//D2Y44 : A열연-#1 제품/소재 Coil Yard
		    				||	YmConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szWLOC_CD)		//D2Y45 : A열연-#2 제품/소재 Coil Yard
		    				||	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szWLOC_CD)		//D3Y41 : B열연-#1 제품/소재 Coil Yard
		    				||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szWLOC_CD)		//D3Y42 : B열연-#2 제품/소재 Coil Yard
																						) {
						//AB열연 Coil 야드인 경우
						szMsg="["+methodNm+"] A열연 COIL야드, B열연 COIL야드 인 경우 (WLOC_CD: " + szWLOC_CD;
						commUtils.printLog(logId, szMsg, "SL");
						
			            //b1)공차의 경우 상차작업예약 ID를 이용하여 목적동을 알아온다------------------
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStoppointE", logId, methodNm, "공차의 경우 상차작업예약 ID를 이용하여 목적동을 알아온다");
						if (rsResult.size() > 0) {
							s_STACK_YD_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
							s_STACK_BAY_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_BAY_GP"));
							s_WBOOK_ID		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));
							szMsg="["+methodNm+"] 검색 결과  >> YD_GP: " + s_STACK_YD_GP + ", BAY_GP: " + s_STACK_BAY_GP + ", WBOOK_ID: " + s_WBOOK_ID;
							commUtils.printLog(logId, szMsg, "SL");
						} else {
							
							if (	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szWLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
							||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szWLOC_CD) //D3Y42 : B열연-#2 제품/소재 Coil Yard
																						) {
								//B열연 COIL 야드인 경우 대상재 조회
								jrParam.setField("WLOC_CD"		, szWLOC_CD);
								jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
								rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtoStl_CoilNewB", logId, methodNm, "B열연 COIL 야드인 경우 대상재 조회");
								if (rsResult.size() > 0) {
									s_STACK_YD_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
									s_STACK_BAY_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));
								}
								
							} else {
								//A열연 COIL 야드인 경우 대상재 조회
								jrParam.setField("WLOC_CD"		, szWLOC_CD);
								jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
								rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtoStl_CoilNewA", logId, methodNm, "A열연 COIL 야드인 경우 대상재 조회");
								if (rsResult.size() > 0) {
									s_STACK_YD_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
									s_STACK_BAY_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));
								}
							}
						}
						
						//b2)상차정지위치 검색------------------------------------------------
						jrParam.setField("WLOC_CD"		, szWLOC_CD);
						jrParam.setField("YD_GP"		, s_STACK_YD_GP);
						jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
						jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint", logId, methodNm, "상차정지위치 검색 - 검색대상재 동에 있는 차량위치정보");
						if (rsResult.size() > 0) {
							s_STACK_COL_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							s_YD_PNT_CD = commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_PNT_CD"),"0000");
							szMsg="["+methodNm+"] 상차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 검색 결과 : " + s_STACK_COL_GP + "," + s_YD_PNT_CD;
							commUtils.printLog(logId, szMsg, "SL");
						} else {
							//b2-1)포인트 없으면 포인트 없음으로 지시주고 하위처리 안함------------------------
							szMsg="["+methodNm+"] 상차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 찾지 못함 ";
							commUtils.printLog(logId, szMsg, "SL");
							
							//대기장 포인트 사유 가져오기 (재료외형구분N:바인코일 BIC생산(소형압연)~제품창고 입고 이전(종합판정)
							szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szWLOC_CD, "", logId, methodNm);
							
							//0000 포인트지시 전송
							sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, "0000", szYD_MSG_NM, s_STACK_BAY_GP, logId));
							
							commUtils.printLog(logId, methodNm, "S-");
							return sndRecord;
						}
					}
					
	    		    // 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
					jrParam.setField("STACK_STAT"	, "L"); //L:상차작업상태
					jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
					jrParam.setField("STACK_COL_GP"	, s_STACK_COL_GP);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
					
					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YmCarPointinforeg("3","",szTRN_EQP_CD,s_STACK_COL_GP,"","","R", logId, methodNm);

					//**********************************************************************************	
					//1-2-3.차량스케쥴 상차출발(1)로 UPDATE
					szMsg="차량스케쥴 상차출발(1)로 UPDATE   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					jrParam.setField("YD_CAR_PROG_STAT"		, "1"				); //차량진행상태 (1:상차출발)
					jrParam.setField("YD_CAR_USE_GP"		, "L"				); //야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT"		, "U"				); //야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("SPOS_WLOC_CD"			, szWLOC_CD			); //발지개소코드(상차지)
					jrParam.setField("YD_PNT_CD"			, s_YD_PNT_CD		); //야드상차포인트코드(발지)
					jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ""				); //야드상차작업예약ID
					jrParam.setField("YD_CARLD_STOP_LOC"	, s_STACK_COL_GP	); //야드하차정지위치
					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD		); //운송장비코드
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchLdByTrnEqpCd", logId, methodNm, "차량스케쥴 상차출발(1)로 UPDATE ");
					
				} else if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
					
					//--------------------------------------------------------------------------------
					// F:영차 --> 하차지 포인트를 찾아 포인트 지시 전송
					
					szMsg="["+methodNm+"] TRN_WRK_FULLVOID_GP가 'F':영차차(하차작업) 포인트 요구  처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					if (	YmConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szWLOC_CD) 	//D2Y44 : A열연-#1 제품/소재 Coil Yard
    				||	YmConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szWLOC_CD)		//D2Y45 : A열연-#2 제품/소재 Coil Yard
    				||	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szWLOC_CD)		//D3Y41 : B열연-#1 제품/소재 Coil Yard
    				||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szWLOC_CD)		//D3Y42 : B열연-#2 제품/소재 Coil Yard
    				//||	YmConstant.WLOC_CD_B_HR_SLAB_YARD.equals(szWLOC_CD)			//D3Y43 : B열연-Slab Yard
																				) {
						
			            //c1)영차의 경우 하차작업예약 ID를 이용하여 목적동을 알아온다------------------
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStoppointF", logId, methodNm, "영차의 경우 하차작업예약 ID를 이용하여 목적동을 알아온다");
						if (rsResult.size() > 0) {
							s_STACK_YD_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
							s_STACK_BAY_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));
							s_WBOOK_ID		= commUtils.trim(rsResult.getRecord(0).getFieldString("WBOOK_ID"));
							szMsg="["+methodNm+"] 검색 결과  >> YD_GP: " + s_STACK_YD_GP + ", BAY_GP: " + s_STACK_BAY_GP + ", WBOOK_ID: " + s_WBOOK_ID;
							commUtils.printLog(logId, szMsg, "SL");
						}
						
						//c2)하차정지위치 검색------------------------------------------------
						jrParam.setField("WLOC_CD"		, szWLOC_CD);
						jrParam.setField("YD_GP"		, s_STACK_YD_GP);
						jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
						jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint", logId, methodNm, "하차정지위치 검색 - 검색대상재 동에 있는 차량위치정보");
						if (rsResult.size() > 0) {
							s_STACK_COL_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							s_YD_PNT_CD = commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_PNT_CD"),"0000");
							szMsg="["+methodNm+"] 하차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 검색 결과 : " + s_STACK_COL_GP + "," + s_YD_PNT_CD;
							commUtils.printLog(logId, szMsg, "SL");
						} else {
							//c2-1)포인트 없으면 포인트 없음으로 지시주고 하위처리 안함------------------------
							szMsg="["+methodNm+"] 하차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 찾지 못함 ";
							commUtils.printLog(logId, szMsg, "SL");
							
							//대기장 포인트 사유 가져오기 (재료외형구분N:바인코일 BIC생산(소형압연)~제품창고 입고 이전(종합판정)
							szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szWLOC_CD, "", logId, methodNm);
							
							//0000 포인트지시 전송
							sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, "0000", szYD_MSG_NM, s_STACK_BAY_GP, logId));
							
							commUtils.printLog(logId, methodNm, "S-");
							return sndRecord;
						}
					} else if ( YmConstant.WLOC_CD_B_HR_SLAB_YARD.equals(szWLOC_CD) ) { //D3Y43 : B열연-Slab Yard
						
						JDTORecordSet rsResult2    	= null;
					    String inCarspec;
					    String outCarspec;
					    
						//야드구분 지정
						if (YmConstant.WLOC_CD_B_HR_SLAB_YARD.equals(szWLOC_CD)) {
							s_STACK_YD_GP 	= "2";	//B열연 SLAB야드
						} else {
							s_STACK_YD_GP 	= "0";  //b-cast
						}
						
						//운송장비코드로 차량스케줄ID 조회
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtostlList_1", logId, methodNm, "운송장비코드로 차량스케줄ID 조회");
						if (rsResult.size() <= 0) {
							throw new Exception("차량스케줄ID(YD_CAR_SCH_ID) 조회 실패!!!");
						}
						String s_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
						
						//차량스케줄 ID로 차량스케줄에서 SPOS_WLOC_CD 조회
						jrParam.setField("YD_CAR_SCH_ID"	, s_CAR_SCH_ID);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarSchBySchId", logId, methodNm, "차량스케줄id로 차량스케줄정보 조회");
						if (rsResult.size() <= 0) {
							throw new Exception("발지개소코드(SPOS_WLOC_CD) 조회 실패!!!");
						}
						String szSPOS_WLOC_CD = commUtils.trim(rsResult.getRecord(0).getFieldString("SPOS_WLOC_CD"));
						
						
						//이송목적동 조회
						s_STACK_BAY_GP = getSlabAimCd(s_CAR_SCH_ID , szSPOS_WLOC_CD , logId , methodNm);
						
						if ("".equals(s_STACK_BAY_GP)) {
							throw new Exception("이송목적동 조회 실패!!!");
						}
						

						szMsg=" 결과 >> 야드구분 : " + s_STACK_YD_GP +", 차량스케줄ID : " + s_CAR_SCH_ID + ", 이송목적동 : " + s_STACK_BAY_GP;
						commUtils.printLog(logId, szMsg, "SL");

						/**********************************************************
						* 1-1-1. 하차정지위치 검색
						**********************************************************/
						szMsg="하차정지위치 검색  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						if (YmConstant.WLOC_CD_B_HR_SLAB_YARD.equals(szWLOC_CD)) {
							//B열연 SLAB야드(D3Y43)
							jrParam.setField("WLOC_CD"		, szWLOC_CD);
							jrParam.setField("YD_GP"		, s_STACK_YD_GP);
							jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
							jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint", logId, methodNm, "B열연 SLAB야드(D3Y43) 하차정지위치 검색 - 이송목적동 차량위치정보");
							
						} else {
							//B-CAST(D2Y43)
							jrParam.setField("WLOC_CD"		, szWLOC_CD);
							jrParam.setField("YD_GP"		, s_STACK_YD_GP);
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppointBCAST", logId, methodNm, "B-CAST(D2Y43) 하차정지위치 검색 ");
						}

						if (rsResult.size() > 0) {
							s_STACK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							s_YD_PNT_CD 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
						} else {
							
							if ("PT".equals(szTRN_EQP_GP)) {
								//포인트 모두 점유상태일때 하차완료된 포인트찾음
								jrParam.setField("YD_GP"		, s_STACK_YD_GP);
								jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
								jrParam.setField("WLOC_CD"		, szWLOC_CD);
								rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListUnloadEndpoint_slab", logId, methodNm, "포인트 모두 점유상태일때 하차완료된 포인트찾음");
								
								if (rsResult.size() > 0) {
									
									jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD);
									rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListCarSpec", logId, methodNm, "YD_차량사양 테이블에서 운송장비코드로 CAR_NO 조회");
									
									inCarspec = commUtils.trim(rsResult2.getRecord(0).getFieldString("CAR_NO"));
									
									for(int ii = 0 ; ii < rsResult.size() ; ii++) {
										
										outCarspec = commUtils.trim(rsResult.getRecord(ii).getFieldString("CAR_NO"));
										
										if (inCarspec.equals(outCarspec)) {
											s_STACK_COL_GP 	= commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_COL_GP"));
											s_YD_PNT_CD		= commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_PNT_CD"));
										}
									}
								}
							}
						}
						
						if (rsResult.size() <= 0 || "".equals(s_YD_PNT_CD)) {
							
							//포인트찾지 못함
							s_STACK_COL_GP	= "XXPTXX";
							s_YD_PNT_CD		= "0000";
							
							//c2-1)포인트 없으면 포인트 없음으로 지시주고 하위처리 안함------------------------
							szMsg="["+methodNm+"] 하차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 찾지 못함 ";
							commUtils.printLog(logId, szMsg, "SL");
							
							//대기장 포인트 사유 가져오기 (재료외형구분N:바인코일 BIC생산(소형압연)~제품창고 입고 이전(종합판정)
							szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szWLOC_CD, "", logId, methodNm);
							
							//0000 포인트지시 전송
							sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, "0000", szYD_MSG_NM, s_STACK_BAY_GP, logId));
							
							commUtils.printLog(logId, methodNm, "S-");
							return sndRecord;
							
						}
						
					}
					
	    		    // 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다. 
					jrParam.setField("STACK_STAT"	, "U"); //U:하차작업상태
					jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
					jrParam.setField("STACK_COL_GP"	, s_STACK_COL_GP);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
					
					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YmCarPointinforeg("3","",szTRN_EQP_CD,s_STACK_COL_GP,"","","R", logId, methodNm);

					
					//**********************************************************************************	
					//1-1-3.차량스케쥴 하차출발(A)로 UPDATE
					szMsg="차량스케쥴 하차출발(A)로 UPDATE   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					jrParam.setField("YD_CAR_PROG_STAT"		, "A"				); //차량진행상태 (A:하차출발)
					jrParam.setField("YD_CAR_USE_GP"		, "L"				); //야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT"		, "L"				); //야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("ARR_WLOC_CD"			, szWLOC_CD			); //착지개소코드
					jrParam.setField("YD_PNT_CD"			, s_YD_PNT_CD		); //야드포인트코드(착지) --> **YD_PNT_CD3 에 저장된다
					jrParam.setField("YD_CARUD_STOP_LOC"	, s_STACK_COL_GP	); //야드하차정지위치
					jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ""				); //야드하차작업예약ID
					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD		); //운송장비코드
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchUdByTrnEqpCd", logId, methodNm, "차량스케쥴 하차출발(A) 업데이트 ");
					
				}
			}
			
			//포인트지시 전송
			sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, s_YD_PNT_CD, szYD_MSG_NM, s_STACK_BAY_GP, logId));
			
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} //end of rcvTSYDJ002_Comm()

	/**
	 *      [A] 오퍼레이션명 : 소재차량도착Point요구(TSYDJ002) - 기존소스:TsInfoRegSBean.procMatlCarArrPntRequest
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvTSYDJ002_WL(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "소재차량도착Point요구WL[YmCommCarMvSeEJB.rcvTSYDJ002_WL] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
	    String szMsg           		= "";
	    String msgId				= null;
	    String modifier				= null;
	    
	    String szTRN_EQP_CD    		= "";	//운송장비코드
	    String szWLOC_CD       		= "";	//개소코드
	    String szTRN_WRK_FULLVOID_GP= "";	//운송작업영공구분
	    String szTRN_EQP_GP 		= "";	//PT/TR 구분
	    String szYD_MSG_NM			= "";
	    
	    String s_STACK_YD_GP 		= "";
	    String s_STACK_BAY_GP 		= "";
	    String s_WBOOK_ID 			= "";
	    String s_STACK_COL_GP		= "";
	    String s_YD_PNT_CD			= "0000";
	    String sWAITLOC_YN			= "";
	  
	    String		sYD_GP			= "";
	    String		sYD_BAY_GP		= "";
	    String		sYD_CARPNT_CD	= "";
	    String		sYD_CAR_SCH_ID	= "";
	    String		sYD_STK_COL_GP	= "";
	    
	    JDTORecordSet rsResult    	= null;
	    JDTORecordSet rsResult2		= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
	    
	    try{

			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "소재차량도착Point요구[TSYDJ002] 수신 ", rcvMsg);
			
	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); //운송장비코드
			szWLOC_CD      			= commUtils.trim(rcvMsg.getFieldString("WLOC_CD")); //개소코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분[상하차구분 E:공차상태(상차작업),F:영차(하차작업)]
			
			//운송장비코드길이가 3자리 이상인지 확인 (substring 전 에러 체크)
			if (szTRN_EQP_CD.length() < 3 ) {
				//throw new Exception("운송장비코드 오류 [" + szTRN_EQP_CD + "] 운송장비 구분(PT/TR)정보가 없습니다.");
				szMsg = "운송장비코드 오류 [" + szTRN_EQP_CD + "] 운송장비 구분(PT/TR)정보가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");

				return sndRecord ;
			}
	    	szTRN_EQP_GP = szTRN_EQP_CD.substring(1, 3);//PT/TR 구분
			
	    	//개소코드가 AB열연 개소코드인지 검사한다.
	    	if (!getABLocationInfo_02(szWLOC_CD)) {
				//throw new Exception("개소코드 오류 [" + szWLOC_CD + "]는 AB열연 개소코드가 아닙니다!");
				szMsg = "개소코드 오류 [" + szWLOC_CD + "]는 AB열연 개소코드가 아닙니다!" ;
				commUtils.printLog(logId, szMsg, "SL");
			
				return sndRecord ;
				
			}			
			
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ002"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
	    	
			//**********************************************************************************	
			// 장비코드로 포인트 재 요구 시 상차예약정지위치 검색 (20090828 정종균 추가 )
			jrParam.setField("WLOC_CD"		, szWLOC_CD);
			jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
			
			JDTORecordSet loadPointList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint2", logId, methodNm, "장비코드로 포인트 재 요구 시 상차예약정지위치 검색");
			
			if (loadPointList.size() > 0) {

				jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
				jrParam.setField("WLOC_CD"	 , szWLOC_CD); 	
				jrParam.setField("YD_PNT_CD" , commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_PNT_CD"))); 
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk2", logId, methodNm, "차량포인트 체크  "); 
				
				if("L".equals(commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) &&
						szTRN_EQP_CD.equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD")) &&
						"0".equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD_WRK_CT"))						
				) {
					//포인트 지시 재송신
					szMsg="["+methodNm+"] 이미 예약이 잡혀 있는 경우   ---> 포인트 지시 재송신";
					commUtils.printLog(logId, szMsg, "SL");
					
					s_STACK_COL_GP = commUtils.trim(loadPointList.getRecord(0).getFieldString("STACK_COL_GP"));
					s_STACK_BAY_GP = commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_BAY_GP"));
					s_YD_PNT_CD = commUtils.nvl(loadPointList.getRecord(0).getFieldString("YD_PNT_CD"),"0000");
					
				} else if("L".equals(commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))) {
					//이미 도착한 상태인 경우 여기서 종료
					szMsg="["+methodNm+"] 이미 도착한 상태인 경우 여기서 종료 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					commUtils.printLog(logId, methodNm, "S-");
					return sndRecord;
					
				} else {
					
					szMsg="["+methodNm+"] YD_STK_COL_ACT_STAT : " + commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
					commUtils.printLog(logId, szMsg, "SL");
					
					commUtils.printLog(logId, methodNm, "S-");
					return sndRecord;
				}
				
				
			} else {
				
		    	jrParam.setField("TRN_EQP_CD",			szTRN_EQP_CD);
		    	jrParam.setField("WLOC_CD",				szWLOC_CD);
				jrParam.setField("TRN_WRK_FULLVOID_GP",	szTRN_WRK_FULLVOID_GP);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getTrnEqpCdWaitChk", logId, methodNm, "구내운송차량의 대기장 도착여부 조회");
				
				if (rsResult.size() <= 0)
				{
					throw new Exception("개소코드 [" + szWLOC_CD + "], 운송설비코드 [" + szTRN_EQP_CD + "], 운송작업영공구분 [" + szTRN_WRK_FULLVOID_GP +"] 의 차량스케줄이 없습니다.");
				}
				else
				{
					s_STACK_BAY_GP		= rsResult.getRecord(0).getFieldString("YD_BAY_GP");
					sWAITLOC_YN			= rsResult.getRecord(0).getFieldString("WAITLOC_YN");	//대기장도착여부
					
					if("N".equals(sWAITLOC_YN))
					{
						//대기장도착 안함
						//포인트지시 전송
						sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, "0000", "차량이 대기장 도착 전 상태 입니다.", s_STACK_BAY_GP, logId));
						
						
						return sndRecord ;
					}
				}
				
				
		    	jrParam.setField("TRN_EQP_CD",			szTRN_EQP_CD);
		    	jrParam.setField("WLOC_CD",				szWLOC_CD);
				jrParam.setField("TRN_WRK_FULLVOID_GP",	szTRN_WRK_FULLVOID_GP);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getTrnEqpCdPointInfo", logId, methodNm, "대기중인 구내운송차량의 카스케줄 정보 조회");
		    	
				if (rsResult.size() <= 0)
				{
					throw new Exception("개소코드 [" + szWLOC_CD + "], 운송설비코드 [" + szTRN_EQP_CD + "], 운송작업영공구분 [" + szTRN_WRK_FULLVOID_GP +"] 의 차량스케줄이 없습니다.");
				}
				else
				{
					//차량이 들어올 포인트 및 기본정보 조회
					sYD_GP				= rsResult.getRecord(0).getFieldString("YD_GP");
					sYD_BAY_GP			= rsResult.getRecord(0).getFieldString("YD_BAY_GP");
					sYD_CARPNT_CD		= rsResult.getRecord(0).getFieldString("YD_CARPNT_CD");		//입동예정 야드카포인트
					sYD_CAR_SCH_ID		= rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID");	//차량스케줄ID
				}

				
				jrParam.setField("YD_CARPNT_CD",	sYD_CARPNT_CD);
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointAllCarSchSelect", logId, methodNm, "TB_YD_CARSCH 조회 - 다음에 입동할차량 조회");
				
				if(rsResult2.size() > 0) {
					
					if( szTRN_EQP_CD.equals(rsResult2.getRecord(0).getFieldString("TRN_EQP_CD")) && "L".equals(rsResult2.getRecord(0).getFieldString("YD_CAR_USE_GP")) ) {
						
						//입동1순위 차량정보와 받은정보의 차량이 같음 && 입동할 차량이 구내운송차량이면
						//입동가능 포인트가 있는지 확인 후 입동처리
						//입동가능 포인트가 없으면 입동불가로 처리
						
			    		//입동가능 포인트 검색
			    		jrParam.setField("YD_CARPNT_CD",	sYD_CARPNT_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTSYdCarPointCHK", logId, methodNm, "입동가능 포인트가 있는지 확인");
						
						if (rsResult.size() <= 0) {
							//입동불가
							commUtils.printLog(logId, "비어있는 구내운송 가능 포인트가 없음 입동불가", "SL");
				    		//szYD_MSG_NM = this.getCarMsg("N", sYD_GP, sYD_BAY_GP, szTRN_EQP_GP, szWLOC_CD, "", logId, methodNm);
							szYD_MSG_NM = "입동가능 포인트 없음";
				    		
						} else {
							//입동가능
							
							String sAPPLY1 = ymComm.BCoilApplyYn("APP003","1","1");
							commUtils.printLog(logId,  "차량ERROR LOG 처리:" + sAPPLY1, "SL");
							
							
							s_YD_PNT_CD		= rsResult.getRecord(0).getFieldString("YD_PNT_CD");
							sYD_STK_COL_GP	= rsResult.getRecord(0).getFieldString("YD_STK_COL_GP");
							
							commUtils.printLog(logId, "구내운송 가능 포인트 : " + s_YD_PNT_CD + "(" + sYD_STK_COL_GP + ") " , "SL");
							
							//차량저장위치 포인트에 입동유무 체크 
							//착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인 
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.stackcolpointchk  
							SELECT  STACK_COL_GP
							  FROM  TB_YM_STACKCOL
							 WHERE  WLOC_CD = :V_WLOC_CD
							   AND  YD_PNT_CD = :V_YD_PNT_CD
							   AND  TRN_EQP_CD IS NULL
							   AND  CAR_NO IS NULL
							   AND  CARD_NO IS NULL
							   AND  SECT_GP = 'PT' */ 
							jrParam.setField("WLOC_CD"	, szWLOC_CD); 	
							jrParam.setField("YD_PNT_CD", s_YD_PNT_CD); 
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.stackcolpointchk", logId, methodNm, "착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인  "); 
							if (rsResult.size() <= 0) {
								throw new Exception("착지개소코드 [" + szWLOC_CD + "], 착지야드포인트코드 [" + s_YD_PNT_CD + "] 로 적치열 조회 결과 이미 입동되어 있는 위치입니다.");
							}
							
							//차량저장위치 점유	  
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01 
							UPDATE  TB_YM_STACKCOL
							   SET  YD_CAR_USE_GP = :V_YD_CAR_USE_GP
							       ,TRN_EQP_CD = :V_TRN_EQP_CD
							       ,CAR_NO = :V_CAR_NO
							       ,CARD_NO = :V_CARD_NO
							 WHERE  WLOC_CD = :V_WLOC_CD
							   AND  YD_PNT_CD = :V_YD_PNT_CD
							   AND  SECT_GP = 'PT' */ 	
							jrParam.setField("YD_CAR_USE_GP", "L"); //L:구내운송차량 , G:출하차량  	--야드차량사용구분
							jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
							jrParam.setField("CAR_NO"		, ""); 
							jrParam.setField("CARD_NO"		, ""); 
							jrParam.setField("WLOC_CD"		, szWLOC_CD); 	
							jrParam.setField("YD_PNT_CD"	, s_YD_PNT_CD); 
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01", logId, methodNm, "차량저장위치 점유 "); //**주의! A열연,B열연 분리 해야함!!
							
							
							//차량포인트통합관리 (1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
							this.YmCarPointinforeg("4","",szTRN_EQP_CD,"",szWLOC_CD,s_YD_PNT_CD,"R", logId, methodNm);
							
							
							//작업예약 없이 차량스케쥴 도착으로 먼저 업데이트...배차차량관리 화면에서 대기장에서 입동으로 보이기 위해...
							if ("F".equals(szTRN_WRK_FULLVOID_GP))	//TRN_WRK_FULLVOID_GP(운송작업영공구분) F:영차 / E:공차
							{
								//1.운송작업영공구분이 F:영차 인 경우 처리
								szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
								commUtils.printLog(logId, szMsg, "SL");
								
								/**********************************************************
								* 1-5. 차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)
								**********************************************************/
								/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt7 
								UPDATE TB_YD_CARSCH
								  SET  MODIFIER = :V_MODIFIER
								      ,MOD_DDTT = SYSDATE
								      ,YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
								      ,YD_PNT_CD3 = :V_YD_PNT_CD3
								      ,YD_CARUD_ARR_DT = SYSDATE
								      ,YD_CARUD_WRK_BOOK_ID= :V_YD_CARUD_WRK_BOOK_ID
								      ,FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
								WHERE TRN_EQP_CD = :V_TRN_EQP_CD
								AND DEL_YN = 'N' */
								jrParam.setField("YD_CARUD_STOP_LOC"	, sYD_STK_COL_GP);
								jrParam.setField("YD_PNT_CD3"			, s_YD_PNT_CD);
								jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, "");
								jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
								jrParam.setField("FRTOMOVE_WORD_NO"		, ""); //이송작업지시번호
								
								commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt7", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
								
								
							} else if ("E".equals(szTRN_WRK_FULLVOID_GP)){
								//2.운송작업영공구분이 E:공차 인 경우 처리
								szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
								commUtils.printLog(logId, szMsg, "SL");
								
								
								/**********************************************************
								* 2-4. 차량스케쥴 update(도착시간, 실제도착위치 업데이트처리)
								**********************************************************/
								jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, "");
								jrParam.setField("ARR_WLOC_CD"			, szWLOC_CD);
								jrParam.setField("YD_PNT_CD1"			, s_YD_PNT_CD);
								jrParam.setField("YD_CARLD_STOP_LOC"	, sYD_STK_COL_GP);
								jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
								jrParam.setField("FRTOMOVE_WORD_NO"		, ""); //이송작업지시번호
								/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt6 
								UPDATE TB_YD_CARSCH
								  SET  MODIFIER = :V_MODIFIER
								      ,MOD_DDTT = SYSDATE
								      ,YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
								      ,ARR_WLOC_CD =:V_ARR_WLOC_CD
								      ,YD_PNT_CD1 = :V_YD_PNT_CD1
								      ,YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC
								      ,YD_CARLD_ARR_DT = SYSDATE
								      ,FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
								WHERE TRN_EQP_CD = :V_TRN_EQP_CD
								AND DEL_YN = 'N' */
								
								commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt6", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
								
							}
							
						}
						
						
						
					} else {
			    		//입동불가
			    		szYD_MSG_NM = this.getCarMsg("N", sYD_GP, sYD_BAY_GP, szTRN_EQP_GP, szWLOC_CD, "", logId, methodNm);
			    	}
				} else {
		    		//입동불가
		    		//szYD_MSG_NM = this.getCarMsg("N", sYD_GP, sYD_BAY_GP, szTRN_EQP_GP, szWLOC_CD, "", logId, methodNm);
					szYD_MSG_NM = "입동 불가";
				}
				
			}

			//포인트지시 전송
			sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, s_YD_PNT_CD, szYD_MSG_NM, s_STACK_BAY_GP, logId));
			
			
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} //end of rcvTSYDJ002_WL()
	
	//----------------------------------------------------------------------------------------
	/**
	 *      [A] 오퍼레이션명 : 소재차량도착(TSYDJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ003(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "소재차량도착[YmCommCarMvSeEJB.rcvTSYDJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
		
		//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		String msgId = commUtils.getMsgId(rcvMsg); 
        if (msgId==null || msgId.equals("")) {
        	return sndRecord;
        }
        
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "소재차량도착 수신 ", rcvMsg);
			
	    	//수신항목 변수 저장
			String szARR_WLOC_CD 	= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 	//착지개소코드
				
			
			if (	YmConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD) //D2Y44 : A열연-#1 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)	//D2Y45 : A열연-#2 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y42 : B열연-#2 제품/소재 Coil Yard
			) {
				String  sAPP055_YN = ymComm.BCoilApplyYn("APP055","3","1"); //구내운송차량 대기장 도착 후 입동지시 여부
				
				if("Y".equals(sAPP055_YN))  {
					//1열연 Coil 야드 신규 도착처리 (대기장 도착 후 입동하는 방식)
					sndRecord = this.rcvTSYDJ003_ABCoil_WL(rcvMsg);
				} else {
					//1열연 Coil 야드 기존 도착처리
					sndRecord = this.rcvTSYDJ003_ABCoil(rcvMsg);
				}
				
			} else {
				
				//AB열연 Slab 야드인 경우
				sndRecord = this.rcvTSYDJ003_ABSlab(rcvMsg);
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ003()		
	
	/**
	 *      [A] 오퍼레이션명 : 1열연 COIL야드 소재차량도착(TSYDJ003) 수신처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ003_ABCoil(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "1열연Coil_소재차량도착수신처리[YmCommCarMvSeEJB.rcvTSYDJ003_ABCoil] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	
	    String szMsg           		= "";

	    String szTRN_EQP_CD; 
	    String szARR_WLOC_CD; 
	    String szARR_YD_PNT_CD; 
	    String szTRN_WRK_FULLVOID_GP; 
	    String szTRN_EQP_STK_CAPA; 
	    String szTRN_EQP_GP;
	    String msgId;
	    String modifier;
	    
	    String sStkClo; 
	    String s_STACK_YD_GP; 
	    String s_STACK_BAY_GP;
	    String s_STACK_LAYER_GP;
		String s_STL_APPEAR_GP;
		String s_YD_CAR_SCH_ID = "";
		String sSchCode;
		String wbook_ID;
		String first_wbook_ID = "";
		String sYD_SCH_PRIOR;
		String sFRTOMOVE_WORD_NO; //이송작업지시 번호
	    
		int iLoadMax 	= 0;
    	int iLoadCur 	= 0;	    	
		
    	long lCarMaxWt	= 0;
    	long lPerWt		= 0;    	
    	long lTotalWt	= 0;
		
	    JDTORecordSet rsResult    	= null;
	    JDTORecordSet rsResult2    	= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= null; //임시  JDTORecord 
		
		List lWrkbookIdList01	= new ArrayList(); //1단 작업예약ID List
		List lWrkbookIdList02	= new ArrayList(); //2단 작업예약ID List
		
		EJBConnector ejbConn;
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
	    
	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= szTRN_EQP_CD.substring(1, 3); //PT/TR 구분
			
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ003"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			
	    	//개소코드가 AB열연 개소코드인지 검사한다.
	    	if (!getABLocationInfo_02(szARR_WLOC_CD)) {
				throw new Exception("개소코드 오류 [" + szARR_WLOC_CD + "]는 AB열연 개소코드가 아닙니다!");
			}			
			
	    	//도착 포인트코드가 대기장(1Z99)인 경우 도착처리 안함
	    	if ("1Z99".equals(szARR_YD_PNT_CD)) {
				throw new Exception("도착포인트코드가 [1Z99]대기장으로 도착처리 안함.");
	    	}
	    	
	    	//차량포인트(TB_YM_CARPOINT)를 개소코드와 야드포인트로 조회하여 도착처리 운송장비 코드와 동일한지 체크한다.
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk 
	    	SELECT YD_CARPNT_CD
	    	      ,YD_STK_COL_ACT_STAT
	    	      ,YD_CAR_USETYPE_GP
	    	      ,YD_GP
	    	      ,YD_BAY_GP
	    	      ,YD_STK_COL_GP
	    	      ,TRN_EQP_CD
	    	      ,CAR_NO
	    	      ,CARD_NO
	    	      ,WLOC_CD
	    	      ,YD_PNT_CD
	    	      ,YD_SPAN_FROM
	    	      ,YD_SPAN_TO
	    	      ,YD_STK_COL_GP2
	    	      ,YD_FRM_YN
	    	FROM   TB_YD_CARPOINT
	    	WHERE  WLOC_CD = :V_WLOC_CD
	    	AND    YD_PNT_CD = :V_YD_PNT_CD */
			jrParam.setField("WLOC_CD"	, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD", szARR_YD_PNT_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk", logId, methodNm, "차량포인트 체크  "); 
			if (rsResult.size() <= 0) {
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 차량포인트에 없는 위치입니다.");	
			} else {
				if ("R".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) {
					//예약일 경우 입력받은 운송장비코드와 동일한지 체크
					if (!szTRN_EQP_CD.equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD"))) {
						throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 로 예약되어 있는데 " + szTRN_EQP_CD + " 로 도착처리 수신되었습니다.");
					}
				} else if ("L".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) {
					//상용중일경우 에러 처리
					throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  이미 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 가 입동되어 있는 위치입니다.");
				}
			}
	    	
			//차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd 
	    	UPDATE  TB_YM_STACKCOL
	    	   SET  CAR_CARD_NO = ''
	    	       ,STACK_STAT = ''
	    	       ,MODIFIER = :V_MODIFIER
	    	       ,MOD_DDTT = SYSDATE
	    	 WHERE  CAR_CARD_NO = :V_CAR_CARD_NO  --TRN_EQP_CD   */
			jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear ");
			
			//차량 포인트 예약으로 잡혀있는정보 Clear
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPlnInfoReSet 
			UPDATE TB_YD_CARPOINT
			   SET YD_STK_COL_ACT_STAT = 'C'
			      ,TRN_EQP_CD = ''
			      ,MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			   AND YD_STK_COL_ACT_STAT = 'R' */
			jrParam.setField("TRN_EQP_CD",szTRN_EQP_CD);  //운송장비코드
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPlnInfoReSet", logId, methodNm, "차량 포인트 예약으로 잡혀있는정보 Clear ");
			
	    	
			//차량저장위치 포인트에 입동유무 체크 
			//착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인 
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.stackcolpointchk  
			SELECT  STACK_COL_GP
			  FROM  TB_YM_STACKCOL
			 WHERE  WLOC_CD = :V_WLOC_CD
			   AND  YD_PNT_CD = :V_YD_PNT_CD
			   AND  TRN_EQP_CD IS NULL
			   AND  CAR_NO IS NULL
			   AND  CARD_NO IS NULL
			   AND  SECT_GP = 'PT' */ 
			jrParam.setField("WLOC_CD"	, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD", szARR_YD_PNT_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.stackcolpointchk", logId, methodNm, "착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인  "); 
			if (rsResult.size() <= 0) {
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 로 적치열 조회 결과 이미 입동되어 있는 위치입니다.");
			}
			
			//차량저장위치 점유	  
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01 
			UPDATE  TB_YM_STACKCOL
			   SET  YD_CAR_USE_GP = :V_YD_CAR_USE_GP
			       ,TRN_EQP_CD = :V_TRN_EQP_CD
			       ,CAR_NO = :V_CAR_NO
			       ,CARD_NO = :V_CARD_NO
			 WHERE  WLOC_CD = :V_WLOC_CD
			   AND  YD_PNT_CD = :V_YD_PNT_CD
			   AND  SECT_GP = 'PT' */ 	
			jrParam.setField("YD_CAR_USE_GP", "L"); //L:구내운송차량 , G:출하차량  	--야드차량사용구분
			jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
			jrParam.setField("CAR_NO"		, ""); 
			jrParam.setField("CARD_NO"		, ""); 
			jrParam.setField("WLOC_CD"		, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD"	, szARR_YD_PNT_CD); 
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01", logId, methodNm, "차량저장위치 점유 "); //**주의! A열연,B열연 분리 해야함!!
			
			
			//차량포인트통합관리 (1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			this.YmCarPointinforeg("4","",szTRN_EQP_CD,"",szARR_WLOC_CD,szARR_YD_PNT_CD,"L", logId, methodNm);
			
			
			//운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListstlQty 
			SELECT  COUNT(B.STL_NO) AS QTY
			  FROM  TB_YD_CARSCH A
			       ,TB_YD_CARFTMVMTL B
			 WHERE  A.DEL_YN = 'N'
			   AND  A.TRN_EQP_CD = :V_TRN_EQP_CD
			   AND  A.YD_CAR_PROG_STAT = 'A' -- 차량진행상태 A:하차출발
			   AND  A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID */ 
			jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListstlQty", logId, methodNm, "운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기  ");
			String szQTY = commUtils.trim(rsResult.getRecord(0).getFieldString("QTY"));
			
			//해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다.
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_Qty 
			UPDATE TB_YM_STACKLAYER
			   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT  
			 WHERE STACK_COL_GP = (SELECT  STACK_COL_GP	    	 		
			                         FROM  TB_YM_STACKCOL
			                        WHERE  WLOC_CD = :V_WLOC_CD
			                          AND  YD_PNT_CD = :V_YD_PNT_CD
			                          AND  SECT_GP = 'PT'
			                      )
			   AND TO_NUMBER(STACK_LAYER_GP) <= :V_QTY */
			jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, "E"); //적치가능
			jrParam.setField("QTY"						, "6"); 
			jrParam.setField("WLOC_CD"					, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD"				, szARR_YD_PNT_CD); 
			if ("0".equals(szQTY)) {
				jrParam.setField("QTY"					, "6"); 
			} else {
				jrParam.setField("QTY"					, szQTY); 
			}
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_Qty", logId, methodNm, "해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다 "); //**주의! A열연,B열연 분리 해야함!!
			
			//실제 도착위치 업데이트 및 영차도착시 맵정보 생성을 위해 
			//개소코드와 야드포인트 코드로 적치열구분 조회 야드구분, 동 정보를 구한다.
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStkColgp 
			SELECT STACK_COL_GP
			  FROM TB_YM_STACKCOL
			 WHERE WLOC_CD = :V_WLOC_CD
			   AND YD_PNT_CD = :V_YD_PNT_CD 
			   AND SECT_GP = 'PT'			*/
			jrParam.setField("WLOC_CD"					, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD"				, szARR_YD_PNT_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStkColgp", logId, methodNm, "개소코드와 야드포인트 코드로 적치열구분 조회"); //**주의! A열연,B열연 분리 해야함!!
			if (rsResult.size() <= 0) {
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 로 적치열 조회 결과 : " + rsResult.size());
			}
			
			sStkClo			= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
			s_STACK_YD_GP	= sStkClo.substring(0, 1);
			s_STACK_BAY_GP  = sStkClo.substring(1, 2);
			
			if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
				
				//**********************************************************************************	
				//1.운송작업영공구분이 F:영차 인 경우 처리
				szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//-----------------------------------------------------------------
				//이송재료 가 STOCK에 존재하지 않으면 STOCK을 생성한다.
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIsExistStock 
				SELECT A.STL_NO
				      ,(SELECT STOCK_ID FROM TB_YM_STOCK WHERE STOCK_ID = A.STL_NO) AS STOCK_ID
				  FROM TB_YD_CARFTMVMTL A
				 WHERE A.DEL_YN = 'N'
				   AND A.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID)
				                            FROM TB_YD_CARSCH
				                           WHERE TRN_EQP_CD = :V_TRN_EQP_CD   
				                             AND DEL_YN = 'N')
				*/
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIsExistStock", logId, methodNm, "이송재료 가 STOCK에 존재하는지 check");
				if (rsResult.size() > 0) {
					
					String sCoilNo = "";
					String sStockMv = "";
					
					for(int ii= 0; ii < rsResult.size() ; ii++) {
						if ("".equals(commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")))) {
							
							sCoilNo = commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO"));
							
							//코일공통의 차공정코드로 이동조건 설정, 리턴 값이 ""이면 코일공통에 존재하지 않는 코일 번호로 Error 처리 한다.
							sStockMv	= ymComm.getStockMv(logId, methodNm, sCoilNo);
							
							//TB_YM_STOCK에 존재 한지 않으면 생성한다.
							jrParam.setField("STOCK_ID" 		, sCoilNo);
							jrParam.setField("STOCK_ITEM" 		, YmConstant.ITEM_CM);
							jrParam.setField("STOCK_MOVE_TERM" 	, sStockMv);
							/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock 
							MERGE INTO TB_YM_STOCK ST USING (
							    SELECT :V_STOCK_ID          AS STOCK_ID                                 --재료번호
							         , :V_MODIFIER          AS MODIFIER         --수정자
							         , SYSDATE              AS MOD_DDTT         --수정일시
							         , 'N'                  AS DEL_YN           --삭제유무
							         , :V_STOCK_ITEM        AS STOCK_ITEM       --저장품 품목
							         , :V_STOCK_MOVE_TERM   AS STOCK_MOVE_TERM  --저장품 이동 조건
							      FROM DUAL
							) DD ON ( ST.STOCK_ID = DD.STOCK_ID)

							WHEN NOT MATCHED THEN
							    INSERT (
							           STOCK_ID             , STOCK_ITEM        , STOCK_MOVE_TERM 
							         , REGISTER             , REG_DDTT          , MODIFIER  
							         , MOD_DDTT             , DEL_YN    
							         )
							    VALUES (
							           :V_STOCK_ID          , DD.STOCK_ITEM     , DD.STOCK_MOVE_TERM 
							         , DD.MODIFIER          , DD.MOD_DDTT       , DD.MODIFIER  
							         , DD.MOD_DDTT          , DD.DEL_YN 
							         )
							WHEN MATCHED THEN 
							    UPDATE SET
							           STOCK_ITEM       = (CASE WHEN KEEPSTOCK_STL_YN='Y' THEN STOCK_ITEM ELSE DD.STOCK_ITEM END) 
							         , STOCK_MOVE_TERM  = DD.STOCK_MOVE_TERM 
							         , MODIFIER         = DD.MODIFIER 
							         , MOD_DDTT         = DD.MOD_DDTT     */     
							commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock", logId, methodNm, "TB_YM_STOCK 생성");
						}
					}
				}
				//-----------------------------------------------------------------
				
				//차량스케쥴ID로 이송재료 조회
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtostlListsangcha 
				SELECT  A.STL_NO
				       ,A.YD_STK_BED_NO
				       ,SUBSTR(A.YD_STK_LYR_NO,2,2) AS STK_LYR
				       ,A.YD_CAR_SCH_ID 
				       ,B.STL_APPEAR_GP
				  FROM  TB_YD_CARFTMVMTL A
				       ,TB_PT_COILCOMM B
				       ,TB_YM_STOCK C
				 WHERE  A.STL_NO=B.COIL_NO
				   AND  A.STL_NO =C.STOCK_ID
				   AND  A.DEL_YN = 'N'
				   AND  A.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID)
				                             FROM TB_YD_CARSCH
				                            WHERE TRN_EQP_CD = :V_TRN_EQP_CD   
				                              AND DEL_YN = 'N')
				 ORDER BY A.YD_STK_LYR_NO */
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtostlListsangcha", logId, methodNm, "차량스케쥴ID로 이송재료 조회");
				if (rsResult.size() <= 0) {
					throw new Exception("영차(F) 도착처리 대상재가 존재 안함");
				} else {
					s_STL_APPEAR_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				}
				
				//스케줄코드 생성  (개소코드로 구분)
				if ("D3Y41".equals(szARR_WLOC_CD)) {
					//이송하차(L)
					sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02LM";
				} else { //D3Y42
					//이송하차(R)
					sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT06LM";
				}
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", sSchCode);
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
		    	
				if (rsResult2 != null && rsResult2.size() > 0) {
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
				} else {
					throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}			
				
				//이송 작업지시 번호 가져오기
				sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo"); 

				//하차대상 갯수 만큼 Looping...
				for(int ii= 0; ii < rsResult.size() ; ii++) {
					
					//작업예약ID생성
					wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
					if ("".equals(first_wbook_ID)) {
						first_wbook_ID = wbook_ID; //첫번째 작업예약 ID 
					}
					
					//단정보 가져오기
					s_STACK_LAYER_GP = commUtils.nvl(rsResult.getRecord(ii).getFieldString("STK_LYR"),"01");
					
					//단정보에 따라 1단 또는 2단 작업예약ID 리스트에 추가
					if ("01".equals(s_STACK_LAYER_GP)) {
						lWrkbookIdList01.add(wbook_ID);
					} else {
						lWrkbookIdList02.add(wbook_ID);
					}
					
					/**********************************************************
					* 1-1. 작업예약(TB_YM_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
					jrParam.setField("YD_GP"			, s_STACK_YD_GP);
					jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP);
					jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_SCH_REQ_GP"	, "C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
					jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
					jrParam.setField("YD_CAR_USE_GP"	, "L"); //야드차량사용구분 L:구내운송
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
					
					/**********************************************************
					* 1-. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
					jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("STACK_COL_GP"		, sStkClo);
					jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_STK_BED_NO")));
					jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult.getRecord(ii).getFieldString("STK_LYR")));
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
					
					/**********************************************************
					* 1-3. TB_YM_STOCK의이송작업지시번호(TRANS_WORD_NO) 등록
					**********************************************************/
					jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
					jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
					
					/**********************************************************
					* 1-4. 영차도착 포인트 적치단(TB_YM_STACKLAYER)에 COIL정보 생성하기
					**********************************************************/
					jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("STACK_COL_GP"		, sStkClo);
					//jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_STK_BED_NO")));
					jrParam.setField("STACK_BED_GP"		, commUtils.format(ii+1, 2)); //Integer.toString(ii+1));
					//jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult.getRecord(ii).getFieldString("STK_LYR")));
					jrParam.setField("STACK_LAYER_GP"	, "01");
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setStackLayer", logId, methodNm, "영차도착 포인트 적치단(TB_YM_STACKLAYER)에 COIL정보 생성하기");
					
					//- 예정정보를 정확히 보내기 위해서 TB_YM_STOCK의 SHEAR_SUPPLY_SEQ 항목에 차상위치를 설정한다.
					jrParam.setField("SHEAR_SUPPLY_SEQ"		, "0"+(ii+1));		//차량적재위치
					jrParam.setField("STOCK_ID"				, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockShearSupplySeq", logId, methodNm, "TB_YM_STOCK의 SHEAR_SUPPLY_SEQ에 차상위치 셋팅");
					
				}
				
				
				
				/**********************************************************
				* 1-5. 차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)
				**********************************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt5 
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
				      ,YD_PNT_CD3 = :V_YD_PNT_CD3
				      ,YD_CARUD_ARR_DT = SYSDATE
				      ,YD_CAR_PROG_STAT = 'B' -- 하차도착
				      ,YD_CARUD_WRK_BOOK_ID= :V_YD_CARUD_WRK_BOOK_ID
				      ,FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
				WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				AND DEL_YN = 'N' */
				jrParam.setField("YD_CARUD_STOP_LOC"	, sStkClo);
				jrParam.setField("YD_PNT_CD3"			, szARR_YD_PNT_CD);
				jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, first_wbook_ID);
				jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
				jrParam.setField("FRTOMOVE_WORD_NO"		, sFRTOMOVE_WORD_NO); //이송작업지시번호
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt5", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
				
				
				
			} else if ("E".equals(szTRN_WRK_FULLVOID_GP)) {
				
				//**********************************************************************************	
				//2.운송작업영공구분이 E:공차 인 경우 처리
				szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//상차대상재 조회 (순위 감안)
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
				jrParam.setField("STACK_COL_GP"	, s_STACK_YD_GP + s_STACK_BAY_GP);
				jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtoStl_CoilNewsangcha", logId, methodNm, "상차대상재 조회 (순위 감안)");
				if (rsResult.size() <= 0) {
					throw new Exception("공차(E) 도착처리 대상재가 존재 안함");
				} else {
					s_STL_APPEAR_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				}
				
				//스케줄코드 생성  (개소코드로 구분)
				if ("D3Y41".equals(szARR_WLOC_CD)) {
					//이송상차(L)
					sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02UM";
				} else {
					//이송상차(R)
					sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT06UM";
				}
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", sSchCode);
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
		    	
				if (rsResult2 != null && rsResult2.size() > 0) {
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
				} else {
					throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}			
				

				//차량장비 MAX 중량정보 설정
				if ("TR".equals(szTRN_EQP_GP)) {
	    			try{
	    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
	    			} catch (Exception e) {
	    				lCarMaxWt = 60000;
	    			}
	    			iLoadMax = 4;	
				} else {
	    			try{
	    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
	    			} catch (Exception e) {
	    				lCarMaxWt = 180000;
	    			}
		    		iLoadMax = 6;	
				}
				
				List lCarStockList	= new ArrayList();
				
				//상차Lot편성 (상차대상재정보를 가지고 Lot를 편성한다.)
				for(int index=0; index < rsResult.size() ; index++) {
					
					//같이 편성할 수 있는 Lot편성 대상재이면 차량Max중량과 비교해서 Over하는지 체크
					lPerWt = Long.parseLong(commUtils.trim(rsResult.getRecord(index).getFieldString("STK_WT")));
					
					//if ("Y".equals(s_STL_APPEAR_GP)) { //제품 상차LOT대상재 편성(차량 중량체크 안함)
					//	lCarStockList.add(rsResult.getRecord(index));
					//	lTotalWt += lPerWt;
					//	iLoadCur++;
		    		//	if (iLoadMax <= iLoadCur) {
		    		//		break;
		    		//	}
					//	
					//} else { //제품 상차LOT대상재 편성(차량 중량체크 )
						if (lCarMaxWt >= lTotalWt + lPerWt) {	
							lCarStockList.add(rsResult.getRecord(index));
							lTotalWt += lPerWt;
							iLoadCur++;
			    			if (iLoadMax <= iLoadCur) {
			    				break;
			    			}
						} else {
							break;
						}
					//}
				}
				
				szMsg= "구내운송차량 : " + szTRN_EQP_CD + " (상차대상중량 : " + lTotalWt + ")";
				commUtils.printLog(logId, szMsg, "SL"); 
				
				//2010.07.14  보안관리팀에 따른 트레일러 4매이상 상차 불가 처리
		    	if (iLoadCur >= 4 && "TR".equals(szTRN_EQP_GP)) {
		    		iLoadCur = 4 ;
		    	}				
				
				
				JDTORecord FrtoProduct3 = null;
				String s_ARR_WLOC_CD 		= "";
				
				//이송 작업지시 번호 가져오기
				sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo"); 
				

				/*******************************************
				 * 상차도에 남아있는 작업예약 삭제
				 *******************************************/
//				String sAPP040 = ymComm.BCoilApplyYn("APP040","3","1");
//				if ("Y".equals(sAPP040)) {
					/*
					SELECT WB.YD_WBOOK_ID
					  FROM TB_YM_WRKBOOK    WB
					     , TB_YM_WRKBOOKMTL WM
					 WHERE WB.TRN_EQP_CD IS NOT NULL
					   AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					   AND (WB.YD_SCH_CD LIKE '3_PT02LM' OR WB.YD_SCH_CD LIKE '3_PT06LM') --이송하차
					   AND WB.DEL_YN = 'N'
					   AND WM.DEL_YN = 'N'
					   AND WM.STACK_COL_GP = :V_STACK_COL_GP
					 */
					jrParam.setField("STACK_COL_GP", sStkClo);
					JDTORecordSet jsDelList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getExistWrkList", logId, methodNm, "상차도 작업예약 조회");
					for (int ii = 0; ii < jsDelList.size(); ++ii) {
						
						jrParam.setField("YD_WBOOK_ID", jsDelList.getRecord(ii).getFieldString("YD_WBOOK_ID"));
						
						//작업예약재료 삭제
						/*
						UPDATE TB_YM_WRKBOOKMTL
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN      = 'N'
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");				
	
						//작업예약 삭제
						/*
						UPDATE TB_YM_WRKBOOK
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN      = 'N'
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK");
					}
//				}
				
				int iSHEAR_SUPPLY_SEQ = 0;
				
				//상차대상 갯수 만큼 Looping... 2단 작업예약  생성
				for(int ii = 0; ii < iLoadCur; ii++) {

					FrtoProduct3 = (JDTORecord)lCarStockList.get(ii);

					//단정보 가져오기
					s_STACK_LAYER_GP = commUtils.nvl(FrtoProduct3.getFieldString("STACK_LAYER_GP"),"01");

					if("02".equals(s_STACK_LAYER_GP)) {
					
						//작업예약ID생성
						wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
						if ("".equals(first_wbook_ID)) {
							first_wbook_ID = wbook_ID; //첫번째 작업예약 ID 
						}

						//2단 작업예약 리스트에 추가
						lWrkbookIdList02.add(wbook_ID);
						
						
						/**********************************************************
						* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("YD_GP"			, s_STACK_YD_GP);
						jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP);
						jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
						jrParam.setField("YD_SCH_REQ_GP"	, "F"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
						jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
						jrParam.setField("YD_CAR_USE_GP"	, "L"); //야드차량사용구분 L:구내운송
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
						
						/**********************************************************
						* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("STOCK_ID"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						jrParam.setField("STACK_COL_GP"		, sStkClo);
						jrParam.setField("STACK_BED_GP"		, commUtils.trim(FrtoProduct3.getFieldString("STACK_BED_GP")));
						jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(FrtoProduct3.getFieldString("STACK_LAYER_GP")));
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
						
						/**********************************************************
						* 2-3. TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
						jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
						
						//차량대상재 테이블에 insert	
						//jrParam.setField("YD_CAR_SCH_ID"	, s_YD_CAR_SCH_ID);
						//jrParam.setField("STL_NO"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insertCarftmtl", logId, methodNm, "차량대상재 테이블에 insert	");
						
						s_ARR_WLOC_CD = commUtils.trim(FrtoProduct3.getFieldString("ARR_WLOC_CD"));
						
						//- 예정정보를 정확히 보내기 위해서 TB_YM_STOCK의 SHEAR_SUPPLY_SEQ 항목에 차상위치를 설정한다.
						jrParam.setField("SHEAR_SUPPLY_SEQ"		, "0"+(++iSHEAR_SUPPLY_SEQ));		//차량적재위치
						jrParam.setField("STOCK_ID"				, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockShearSupplySeq", logId, methodNm, "TB_YM_STOCK의 SHEAR_SUPPLY_SEQ에 차상위치 셋팅");
						
					}
					
				}

				//상차대상 갯수 만큼 Looping... 1단 작업예약  생성
				for(int ii = 0; ii < iLoadCur; ii++) {

					FrtoProduct3 = (JDTORecord)lCarStockList.get(ii);

					//단정보 가져오기
					s_STACK_LAYER_GP = commUtils.nvl(FrtoProduct3.getFieldString("STACK_LAYER_GP"),"01");

					if("01".equals(s_STACK_LAYER_GP)) {
					
						//작업예약ID생성
						wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
						if ("".equals(first_wbook_ID)) {
							first_wbook_ID = wbook_ID; //첫번째 작업예약 ID 
						}

						//1단 작업예약 리스트에 추가
						lWrkbookIdList01.add(wbook_ID);
						
						
						/**********************************************************
						* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("YD_GP"			, s_STACK_YD_GP);
						jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP);
						jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
						jrParam.setField("YD_SCH_REQ_GP"	, "F"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
						jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
						jrParam.setField("YD_CAR_USE_GP"	, "L"); //야드차량사용구분 L:구내운송
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
						
						/**********************************************************
						* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("STOCK_ID"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						jrParam.setField("STACK_COL_GP"		, sStkClo);
						jrParam.setField("STACK_BED_GP"		, commUtils.trim(FrtoProduct3.getFieldString("STACK_BED_GP")));
						jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(FrtoProduct3.getFieldString("STACK_LAYER_GP")));
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
						
						/**********************************************************
						* 2-3. TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
						jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
						
						//차량대상재 테이블에 insert	
						//jrParam.setField("YD_CAR_SCH_ID"	, s_YD_CAR_SCH_ID);
						//jrParam.setField("STL_NO"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insertCarftmtl", logId, methodNm, "차량대상재 테이블에 insert	");
						
						s_ARR_WLOC_CD = commUtils.trim(FrtoProduct3.getFieldString("ARR_WLOC_CD"));
						
						//- 예정정보를 정확히 보내기 위해서 TB_YM_STOCK의 SHEAR_SUPPLY_SEQ 항목에 차상위치를 설정한다.
						jrParam.setField("SHEAR_SUPPLY_SEQ"		, "0"+(++iSHEAR_SUPPLY_SEQ));		//차량적재위치
						jrParam.setField("STOCK_ID"				, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockShearSupplySeq", logId, methodNm, "TB_YM_STOCK의 SHEAR_SUPPLY_SEQ에 차상위치 셋팅");
					}
					
				}
				
				/**********************************************************
				* 2-4. 차량스케쥴 update(도착시간, 실제도착위치 업데이트처리)
				**********************************************************/
				jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, first_wbook_ID);
				jrParam.setField("ARR_WLOC_CD"			, s_ARR_WLOC_CD);
				jrParam.setField("YD_PNT_CD1"			, szARR_YD_PNT_CD);
				jrParam.setField("YD_CARLD_STOP_LOC"	, sStkClo);
				jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
				jrParam.setField("FRTOMOVE_WORD_NO"		, sFRTOMOVE_WORD_NO); //이송작업지시번호
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt4 
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
				      ,ARR_WLOC_CD =:V_ARR_WLOC_CD
				      ,YD_PNT_CD1 = :V_YD_PNT_CD1
				      ,YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC
				      ,YD_CARLD_ARR_DT = SYSDATE
				      ,YD_CAR_PROG_STAT = '2' --상차도착
				      ,FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
				WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				AND DEL_YN = 'N' */
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt4", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
				
			}
			
			/**********************************************************
			* 차량작업 예정정보 송신 (YMA7L008)
			**********************************************************/
			jrParam.setField("SEARCH_FLAG"   , "2");				//1:상차도, 2:차량스케쥴 ID	
			jrParam.setField("YD_CAR_SCH_ID" , s_YD_CAR_SCH_ID); 	//야드차량스케쥴ID
			sndRecord = commUtils.addSndData(sndRecord, ymComm.procCarPlanInfo(jrParam));
			
			
//			String sAPP045 = ymComm.BCoilApplyYn("APP045","3","1");//차량하차시 저장품제원 정보 송신
			
//			if ("Y".equals(sAPP045)) {
				if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
					/**********************************************************
					* 저장품제원(YMA7L002) 전문 생성
					**********************************************************/
					jrParam.setField("YD_INFO_SYNC_CD"		, "3"				); //야드정보동기화코드 (열)
					jrParam.setField("MSG_GP"				, "I"				); //전문구분
					jrParam.setField("STACK_COL_GP"  		, sStkClo			); //야드적치열구분
					jrParam.setField("STACK_BED_GP"  		, ""				); //야드적치Bed번호
					jrParam.setField("YD_GP"          		, "3"				); //야드구분
					jrParam.setField("STOCK_ID"       		, ""				); //재료번호
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L002", jrParam));
				}
//			}
			
			
			/**********************************************************
			* Crane스케줄 호출
			*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
			*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
			**********************************************************/
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn 
			SELECT YD_CARPNT_CD
			      ,YD_STK_COL_ACT_STAT
			      ,YD_CAR_USETYPE_GP
			      ,WLOC_CD
			      ,YD_PNT_CD
			      ,NVL(YD_FRM_YN,'N') AS YD_FRM_YN
			FROM   TB_YD_CARPOINT
			WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP */
			jrParam.setField("YD_STK_COL_GP" , sStkClo); 	
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 "); 
			if ("N".equals(rsResult.getRecord(0).getFieldString("YD_FRM_YN"))) {
				
				//ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				
				//크레인 스케줄 기동 YMYMJ303 호출
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ303"); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
				
				int pcnt = 0;
				
				commUtils.printLog(logId, "[lWrkbookIdList02.size()] 결과 건수: " + lWrkbookIdList02.size() , "LOG");
				commUtils.printLog(logId, "[lWrkbookIdList01.size()] 결과 건수: " + lWrkbookIdList01.size() , "LOG");
				
				//2단 적치된 대상 호출
				for(int ii = 0; ii < lWrkbookIdList02.size(); ii++) {
					
					jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, (String)lWrkbookIdList02.get(ii)); //야드작업예약ID
					
				}
				
				//1단 적치된 대상 호출
				for(int ii = 0; ii < lWrkbookIdList01.size(); ii++) {
					
					jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, (String)lWrkbookIdList01.get(ii)); //야드작업예약ID
					
				}
				
				jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));
				
				sndRecord = commUtils.addSndData(sndRecord, jrCrnSchMsg);
			}
			
			commUtils.printLog(logId, methodNm, "SL");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ003_ABCoil()		
			
	/**
	 *      [A] 오퍼레이션명 : 1열연 COIL야드 소재차량도착(TSYDJ003) 수신처리 -신규(대기장 도착 후 입동 하는 방식)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ003_ABCoil_WL(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "1열연Coil_소재차량도착수신처리WL[YmCommCarMvSeEJB.rcvTSYDJ003_ABCoil_WL] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	
	    String szMsg           		= "";

	    String szTRN_EQP_CD; 
	    String szARR_WLOC_CD; 
	    String szARR_YD_PNT_CD; 
	    String szTRN_WRK_FULLVOID_GP; 
	    String szTRN_EQP_STK_CAPA; 
	    String szTRN_EQP_GP;
	    String msgId;
	    String modifier;
	    
	    String sStkClo; 
	    String s_STACK_YD_GP; 
	    String s_STACK_BAY_GP;
	    String s_STACK_LAYER_GP;
		String s_STL_APPEAR_GP;
		String s_YD_CAR_SCH_ID = "";
		String sSchCode;
		String wbook_ID;
		String first_wbook_ID = "";
		String sYD_SCH_PRIOR;
		String sFRTOMOVE_WORD_NO; //이송작업지시 번호
	    
		int iLoadMax 	= 0;
    	int iLoadCur 	= 0;	    	
		
    	long lCarMaxWt	= 0;
    	long lPerWt		= 0;    	
    	long lTotalWt	= 0;
		
	    JDTORecordSet rsResult    	= null;
	    JDTORecordSet rsResult2    	= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= null; //임시  JDTORecord 
		
		List lWrkbookIdList01	= new ArrayList(); //1단 작업예약ID List
		List lWrkbookIdList02	= new ArrayList(); //2단 작업예약ID List
		
		EJBConnector ejbConn;
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
	    
	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= szTRN_EQP_CD.substring(1, 3); //PT/TR 구분
			
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ003"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			
	    	//개소코드가 AB열연 개소코드인지 검사한다.
	    	if (!getABLocationInfo_02(szARR_WLOC_CD)) {
				throw new Exception("개소코드 오류 [" + szARR_WLOC_CD + "]는 AB열연 개소코드가 아닙니다!");
			}			
			
	    	//도착 포인트코드가 대기장(1Z99)인 경우 도착처리 안함
	    	if ("1Z99".equals(szARR_YD_PNT_CD)) {
				throw new Exception("도착포인트코드가 [1Z99]대기장으로 도착처리 안함.");
	    	}
	    	
	    	//차량포인트(TB_YM_CARPOINT)를 개소코드와 야드포인트로 조회하여 도착처리 운송장비 코드와 동일한지 체크한다.
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk2 
	    	SELECT YD_CARPNT_CD
	    	      ,YD_STK_COL_ACT_STAT
	    	      ,YD_CAR_USETYPE_GP
	    	      ,YD_GP
	    	      ,YD_BAY_GP
	    	      ,YD_STK_COL_GP
	    	      ,TRN_EQP_CD
	    	      ,CAR_NO
	    	      ,CARD_NO
	    	      ,WLOC_CD
	    	      ,YD_PNT_CD
	    	      ,YD_SPAN_FROM
	    	      ,YD_SPAN_TO
	    	      ,YD_STK_COL_GP2
	    	      ,YD_FRM_YN
	    	      ,(SELECT COUNT(*) FROM TB_YM_WRKBOOK WHERE TRN_EQP_CD = :V_TRN_EQP_CD AND DEL_YN = 'N') AS TRN_EQP_CD_WRK_CT
	    	FROM   TB_YD_CARPOINT
	    	WHERE  WLOC_CD = :V_WLOC_CD
	    	AND    YD_PNT_CD = :V_YD_PNT_CD */
			jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
			jrParam.setField("WLOC_CD"	 , szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD" , szARR_YD_PNT_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk2", logId, methodNm, "차량포인트 체크  "); 
			if (rsResult.size() <= 0) {
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 차량포인트에 없는 위치입니다.");	
			} else {
				
				//R:예약, L:사용중(도착불가), N:사용금지, C:비었음(도착가능)
				if ("R".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))
				{
					//예약일 경우 입력받은 운송장비코드와 동일한지 체크
					if (!szTRN_EQP_CD.equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD")))
					{
						throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 로 예약되어 있는데 " + szTRN_EQP_CD + " 로 도착처리 수신되었습니다.");
					}
				}
				else if
				(
					"L".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")) &&
					!szTRN_EQP_CD.equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD"))
				)
				{
					//사용중 + 사용중인 운송장비코드 와 받은 운송장비코드 가 다름 -> 다른차량이 사용중
					if("".equals(commUtils.trim(rsResult.getRecord(0).getFieldString("TRN_EQP_CD")))) {
						throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  이미 " + rsResult.getRecord(0).getFieldString("CAR_NO") + " 가 입동되어 있는 위치입니다.");
					} else {
						throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  이미 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 가 입동되어 있는 위치입니다.");
					}
				}
				else if
				(
					"L".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")) &&
					szTRN_EQP_CD.equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD")) &&
					!"0".equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD_WRK_CT"))
				)
				{
					//사용중 + 사용중인 운송장비코드 와 받은 운송장비코드 가 동일 + 받은운송장비의 작업예약이 존재-> 이미 입동처리된 차량
					throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  이미 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 가 입동되어 있는 위치입니다.2");
				}				
			}
	    	
			//차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd 
	    	UPDATE  TB_YM_STACKCOL
	    	   SET  CAR_CARD_NO = ''
	    	       ,STACK_STAT = ''
	    	       ,MODIFIER = :V_MODIFIER
	    	       ,MOD_DDTT = SYSDATE
	    	 WHERE  CAR_CARD_NO = :V_CAR_CARD_NO  --TRN_EQP_CD   */
			jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear ");
			
			
			//차량위치정보 삭제(상하차개시/완료/도착 위치) 정리
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseGpByTrnEqpCd
			UPDATE  TB_YM_STACKCOL
			   SET  TRN_EQP_CD = ''
			       ,YD_CAR_USE_GP = ''
			       ,MODIFIER = :V_MODIFIER
			       ,MOD_DDTT = SYSDATE
			WHERE TRN_EQP_CD = :V_TRN_EQP_CD  */
			jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseGpByTrnEqpCd", logId, methodNm, "차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");
			
			
			//차량 포인트 예약으로 잡혀있는정보 Clear
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPlnInfoReSet2 
			UPDATE TB_YD_CARPOINT
			   SET YD_STK_COL_ACT_STAT = 'C'
			      ,TRN_EQP_CD = ''
			      ,MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			   AND YD_STK_COL_ACT_STAT IN ( 'R' , 'L' ) */
			jrParam.setField("TRN_EQP_CD",szTRN_EQP_CD);  //운송장비코드
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPlnInfoReSet2", logId, methodNm, "차량 포인트 예약으로 잡혀있는정보 Clear ");
			
	    	
			//차량저장위치 포인트에 입동유무 체크 
			//착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인 
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.stackcolpointchk  
			SELECT  STACK_COL_GP
			  FROM  TB_YM_STACKCOL
			 WHERE  WLOC_CD = :V_WLOC_CD
			   AND  YD_PNT_CD = :V_YD_PNT_CD
			   AND  TRN_EQP_CD IS NULL
			   AND  CAR_NO IS NULL
			   AND  CARD_NO IS NULL
			   AND  SECT_GP = 'PT' */ 
			//jrParam.setField("WLOC_CD"	, szARR_WLOC_CD); 	
			//jrParam.setField("YD_PNT_CD", szARR_YD_PNT_CD); 
			//rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.stackcolpointchk", logId, methodNm, "착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인  "); 
			//if (rsResult.size() <= 0) {
			//	throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 로 적치열 조회 결과 이미 입동되어 있는 위치입니다.");
			//}
			
			//차량저장위치 점유	  
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01 
			UPDATE  TB_YM_STACKCOL
			   SET  YD_CAR_USE_GP = :V_YD_CAR_USE_GP
			       ,TRN_EQP_CD = :V_TRN_EQP_CD
			       ,CAR_NO = :V_CAR_NO
			       ,CARD_NO = :V_CARD_NO
			 WHERE  WLOC_CD = :V_WLOC_CD
			   AND  YD_PNT_CD = :V_YD_PNT_CD
			   AND  SECT_GP = 'PT' */ 	
			jrParam.setField("YD_CAR_USE_GP", "L"); //L:구내운송차량 , G:출하차량  	--야드차량사용구분
			jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
			jrParam.setField("CAR_NO"		, ""); 
			jrParam.setField("CARD_NO"		, ""); 
			jrParam.setField("WLOC_CD"		, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD"	, szARR_YD_PNT_CD); 
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01", logId, methodNm, "차량저장위치 점유 "); //**주의! A열연,B열연 분리 해야함!!
			
			
			//차량포인트통합관리 (1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			this.YmCarPointinforeg("4","",szTRN_EQP_CD,"",szARR_WLOC_CD,szARR_YD_PNT_CD,"L", logId, methodNm);
			
			
			//운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListstlQty 
			SELECT  COUNT(B.STL_NO) AS QTY
			  FROM  TB_YD_CARSCH A
			       ,TB_YD_CARFTMVMTL B
			 WHERE  A.DEL_YN = 'N'
			   AND  A.TRN_EQP_CD = :V_TRN_EQP_CD
			   AND  A.YD_CAR_PROG_STAT = 'A' -- 차량진행상태 A:하차출발
			   AND  A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID */ 
			jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListstlQty", logId, methodNm, "운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기  ");
			String szQTY = commUtils.trim(rsResult.getRecord(0).getFieldString("QTY"));
			
			//해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다.
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_Qty 
			UPDATE TB_YM_STACKLAYER
			   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT  
			 WHERE STACK_COL_GP = (SELECT  STACK_COL_GP	    	 		
			                         FROM  TB_YM_STACKCOL
			                        WHERE  WLOC_CD = :V_WLOC_CD
			                          AND  YD_PNT_CD = :V_YD_PNT_CD
			                          AND  SECT_GP = 'PT'
			                      )
			   AND TO_NUMBER(STACK_LAYER_GP) <= :V_QTY */
			jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, "E"); //적치가능
			jrParam.setField("QTY"						, "6"); 
			jrParam.setField("WLOC_CD"					, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD"				, szARR_YD_PNT_CD); 
			if ("0".equals(szQTY)) {
				jrParam.setField("QTY"					, "6"); 
			} else {
				jrParam.setField("QTY"					, szQTY); 
			}
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_Qty", logId, methodNm, "해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다 "); //**주의! A열연,B열연 분리 해야함!!
			
			//실제 도착위치 업데이트 및 영차도착시 맵정보 생성을 위해 
			//개소코드와 야드포인트 코드로 적치열구분 조회 야드구분, 동 정보를 구한다.
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStkColgp 
			SELECT STACK_COL_GP
			  FROM TB_YM_STACKCOL
			 WHERE WLOC_CD = :V_WLOC_CD
			   AND YD_PNT_CD = :V_YD_PNT_CD 
			   AND SECT_GP = 'PT'			*/
			jrParam.setField("WLOC_CD"					, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD"				, szARR_YD_PNT_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStkColgp", logId, methodNm, "개소코드와 야드포인트 코드로 적치열구분 조회"); //**주의! A열연,B열연 분리 해야함!!
			if (rsResult.size() <= 0) {
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 로 적치열 조회 결과 : " + rsResult.size());
			}
			
			sStkClo			= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
			s_STACK_YD_GP	= sStkClo.substring(0, 1);
			s_STACK_BAY_GP  = sStkClo.substring(1, 2);
			
			if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
				
				//**********************************************************************************	
				//1.운송작업영공구분이 F:영차 인 경우 처리
				szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//-----------------------------------------------------------------
				//이송재료 가 STOCK에 존재하지 않으면 STOCK을 생성한다.
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIsExistStock 
				SELECT A.STL_NO
				      ,(SELECT STOCK_ID FROM TB_YM_STOCK WHERE STOCK_ID = A.STL_NO) AS STOCK_ID
				  FROM TB_YD_CARFTMVMTL A
				 WHERE A.DEL_YN = 'N'
				   AND A.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID)
				                            FROM TB_YD_CARSCH
				                           WHERE TRN_EQP_CD = :V_TRN_EQP_CD   
				                             AND DEL_YN = 'N')
				*/
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIsExistStock", logId, methodNm, "이송재료 가 STOCK에 존재하는지 check");
				if (rsResult.size() > 0) {
					
					String sCoilNo = "";
					String sStockMv = "";
					
					for(int ii= 0; ii < rsResult.size() ; ii++) {
						if ("".equals(commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID")))) {
							
							sCoilNo = commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO"));
							
							//코일공통의 차공정코드로 이동조건 설정, 리턴 값이 ""이면 코일공통에 존재하지 않는 코일 번호로 Error 처리 한다.
							sStockMv	= ymComm.getStockMv(logId, methodNm, sCoilNo);
							
							//TB_YM_STOCK에 존재 한지 않으면 생성한다.
							jrParam.setField("STOCK_ID" 		, sCoilNo);
							jrParam.setField("STOCK_ITEM" 		, YmConstant.ITEM_CM);
							jrParam.setField("STOCK_MOVE_TERM" 	, sStockMv);
							/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock 
							MERGE INTO TB_YM_STOCK ST USING (
							    SELECT :V_STOCK_ID          AS STOCK_ID                                 --재료번호
							         , :V_MODIFIER          AS MODIFIER         --수정자
							         , SYSDATE              AS MOD_DDTT         --수정일시
							         , 'N'                  AS DEL_YN           --삭제유무
							         , :V_STOCK_ITEM        AS STOCK_ITEM       --저장품 품목
							         , :V_STOCK_MOVE_TERM   AS STOCK_MOVE_TERM  --저장품 이동 조건
							      FROM DUAL
							) DD ON ( ST.STOCK_ID = DD.STOCK_ID)

							WHEN NOT MATCHED THEN
							    INSERT (
							           STOCK_ID             , STOCK_ITEM        , STOCK_MOVE_TERM 
							         , REGISTER             , REG_DDTT          , MODIFIER  
							         , MOD_DDTT             , DEL_YN    
							         )
							    VALUES (
							           :V_STOCK_ID          , DD.STOCK_ITEM     , DD.STOCK_MOVE_TERM 
							         , DD.MODIFIER          , DD.MOD_DDTT       , DD.MODIFIER  
							         , DD.MOD_DDTT          , DD.DEL_YN 
							         )
							WHEN MATCHED THEN 
							    UPDATE SET
							           STOCK_ITEM       = (CASE WHEN KEEPSTOCK_STL_YN='Y' THEN STOCK_ITEM ELSE DD.STOCK_ITEM END) 
							         , STOCK_MOVE_TERM  = DD.STOCK_MOVE_TERM 
							         , MODIFIER         = DD.MODIFIER 
							         , MOD_DDTT         = DD.MOD_DDTT     */     
							commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock", logId, methodNm, "TB_YM_STOCK 생성");
						}
					}
				}
				//-----------------------------------------------------------------
				
				//차량스케쥴ID로 이송재료 조회
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtostlListsangcha 
				SELECT  A.STL_NO
				       ,A.YD_STK_BED_NO
				       ,SUBSTR(A.YD_STK_LYR_NO,2,2) AS STK_LYR
				       ,A.YD_CAR_SCH_ID 
				       ,B.STL_APPEAR_GP
				  FROM  TB_YD_CARFTMVMTL A
				       ,TB_PT_COILCOMM B
				       ,TB_YM_STOCK C
				 WHERE  A.STL_NO=B.COIL_NO
				   AND  A.STL_NO =C.STOCK_ID
				   AND  A.DEL_YN = 'N'
				   AND  A.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID)
				                             FROM TB_YD_CARSCH
				                            WHERE TRN_EQP_CD = :V_TRN_EQP_CD   
				                              AND DEL_YN = 'N')
				 ORDER BY A.YD_STK_LYR_NO */
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtostlListsangcha", logId, methodNm, "차량스케쥴ID로 이송재료 조회");
				if (rsResult.size() <= 0) {
					throw new Exception("영차(F) 도착처리 대상재가 존재 안함");
				} else {
					s_STL_APPEAR_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				}
				
				//스케줄코드 생성  (개소코드로 구분)
				if ("D3Y41".equals(szARR_WLOC_CD)) {
					//이송하차(L)
					sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02LM";
				} else { //D3Y42
					//이송하차(R)
					sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT06LM";
				}
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", sSchCode);
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
		    	
				if (rsResult2 != null && rsResult2.size() > 0) {
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
				} else {
					throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}			
				
				//이송 작업지시 번호 가져오기
				sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo"); 

				//하차대상 갯수 만큼 Looping...
				for(int ii= 0; ii < rsResult.size() ; ii++) {
					
					//작업예약ID생성
					wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
					if ("".equals(first_wbook_ID)) {
						first_wbook_ID = wbook_ID; //첫번째 작업예약 ID 
					}
					
					//단정보 가져오기
					s_STACK_LAYER_GP = commUtils.nvl(rsResult.getRecord(ii).getFieldString("STK_LYR"),"01");
					
					//단정보에 따라 1단 또는 2단 작업예약ID 리스트에 추가
					if ("01".equals(s_STACK_LAYER_GP)) {
						lWrkbookIdList01.add(wbook_ID);
					} else {
						lWrkbookIdList02.add(wbook_ID);
					}
					
					/**********************************************************
					* 1-1. 작업예약(TB_YM_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
					jrParam.setField("YD_GP"			, s_STACK_YD_GP);
					jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP);
					jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_SCH_REQ_GP"	, "C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
					jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
					jrParam.setField("YD_CAR_USE_GP"	, "L"); //야드차량사용구분 L:구내운송
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
					
					/**********************************************************
					* 1-. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
					jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("STACK_COL_GP"		, sStkClo);
					jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_STK_BED_NO")));
					jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult.getRecord(ii).getFieldString("STK_LYR")));
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
					
					/**********************************************************
					* 1-3. TB_YM_STOCK의이송작업지시번호(TRANS_WORD_NO) 등록
					**********************************************************/
					jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
					jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
					
					/**********************************************************
					* 1-4. 영차도착 포인트 적치단(TB_YM_STACKLAYER)에 COIL정보 생성하기
					**********************************************************/
					jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("STACK_COL_GP"		, sStkClo);
					//jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_STK_BED_NO")));
					jrParam.setField("STACK_BED_GP"		, commUtils.format(ii+1, 2)); //Integer.toString(ii+1));
					//jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult.getRecord(ii).getFieldString("STK_LYR")));
					jrParam.setField("STACK_LAYER_GP"	, "01");
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setStackLayer", logId, methodNm, "영차도착 포인트 적치단(TB_YM_STACKLAYER)에 COIL정보 생성하기");
					
					//- 예정정보를 정확히 보내기 위해서 TB_YM_STOCK의 SHEAR_SUPPLY_SEQ 항목에 차상위치를 설정한다.
					jrParam.setField("SHEAR_SUPPLY_SEQ"		, "0"+(ii+1));		//차량적재위치
					jrParam.setField("STOCK_ID"				, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockShearSupplySeq", logId, methodNm, "TB_YM_STOCK의 SHEAR_SUPPLY_SEQ에 차상위치 셋팅");
					
				}
				
				
				
				/**********************************************************
				* 1-5. 차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)
				**********************************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt5 
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
				      ,YD_PNT_CD3 = :V_YD_PNT_CD3
				      ,YD_CARUD_ARR_DT = SYSDATE
				      ,YD_CAR_PROG_STAT = 'B' -- 하차도착
				      ,YD_CARUD_WRK_BOOK_ID= :V_YD_CARUD_WRK_BOOK_ID
				      ,FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
				WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				AND DEL_YN = 'N' */
				jrParam.setField("YD_CARUD_STOP_LOC"	, sStkClo);
				jrParam.setField("YD_PNT_CD3"			, szARR_YD_PNT_CD);
				jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, first_wbook_ID);
				jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
				jrParam.setField("FRTOMOVE_WORD_NO"		, sFRTOMOVE_WORD_NO); //이송작업지시번호
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt5", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
				
				
				
			} else if ("E".equals(szTRN_WRK_FULLVOID_GP)) {
				
				//**********************************************************************************	
				//2.운송작업영공구분이 E:공차 인 경우 처리
				szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//상차대상재 조회 (순위 감안)
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
				jrParam.setField("STACK_COL_GP"	, s_STACK_YD_GP + s_STACK_BAY_GP);
				jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtoStl_CoilNewsangcha", logId, methodNm, "상차대상재 조회 (순위 감안)");
				if (rsResult.size() <= 0) {
					throw new Exception("공차(E) 도착처리 대상재가 존재 안함");
				} else {
					s_STL_APPEAR_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				}
				
				//스케줄코드 생성  (개소코드로 구분)
				if ("D3Y41".equals(szARR_WLOC_CD)) {
					//이송상차(L)
					sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02UM";
				} else {
					//이송상차(R)
					sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT06UM";
				}
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", sSchCode);
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
		    	
				if (rsResult2 != null && rsResult2.size() > 0) {
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
				} else {
					throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}			
				

				//차량장비 MAX 중량정보 설정
				if ("TR".equals(szTRN_EQP_GP)) {
	    			try{
	    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
	    			} catch (Exception e) {
	    				lCarMaxWt = 60000;
	    			}
	    			iLoadMax = 4;	
				} else {
	    			try{
	    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA);
	    			} catch (Exception e) {
	    				lCarMaxWt = 180000;
	    			}
		    		iLoadMax = 6;	
				}
				
				List lCarStockList	= new ArrayList();
				
				//상차Lot편성 (상차대상재정보를 가지고 Lot를 편성한다.)
				for(int index=0; index < rsResult.size() ; index++) {
					
					//같이 편성할 수 있는 Lot편성 대상재이면 차량Max중량과 비교해서 Over하는지 체크
					lPerWt = Long.parseLong(commUtils.trim(rsResult.getRecord(index).getFieldString("STK_WT")));
					
					//if ("Y".equals(s_STL_APPEAR_GP)) { //제품 상차LOT대상재 편성(차량 중량체크 안함)
					//	lCarStockList.add(rsResult.getRecord(index));
					//	lTotalWt += lPerWt;
					//	iLoadCur++;
		    		//	if (iLoadMax <= iLoadCur) {
		    		//		break;
		    		//	}
					//	
					//} else { //제품 상차LOT대상재 편성(차량 중량체크 )
						if (lCarMaxWt >= lTotalWt + lPerWt) {	
							lCarStockList.add(rsResult.getRecord(index));
							lTotalWt += lPerWt;
							iLoadCur++;
			    			if (iLoadMax <= iLoadCur) {
			    				break;
			    			}
						} else {
							break;
						}
					//}
				}
				
				szMsg= "구내운송차량 : " + szTRN_EQP_CD + " (상차대상중량 : " + lTotalWt + ")";
				commUtils.printLog(logId, szMsg, "SL"); 
				
				//2010.07.14  보안관리팀에 따른 트레일러 4매이상 상차 불가 처리
		    	if (iLoadCur >= 4 && "TR".equals(szTRN_EQP_GP)) {
		    		iLoadCur = 4 ;
		    	}				
				
				
				JDTORecord FrtoProduct3 = null;
				String s_ARR_WLOC_CD 		= "";
				
				//이송 작업지시 번호 가져오기
				sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo"); 
				

				/*******************************************
				 * 상차도에 남아있는 작업예약 삭제
				 *******************************************/
//				String sAPP040 = ymComm.BCoilApplyYn("APP040","3","1");
//				if ("Y".equals(sAPP040)) {
					/*
					SELECT WB.YD_WBOOK_ID
					  FROM TB_YM_WRKBOOK    WB
					     , TB_YM_WRKBOOKMTL WM
					 WHERE WB.TRN_EQP_CD IS NOT NULL
					   AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					   AND (WB.YD_SCH_CD LIKE '3_PT02LM' OR WB.YD_SCH_CD LIKE '3_PT06LM') --이송하차
					   AND WB.DEL_YN = 'N'
					   AND WM.DEL_YN = 'N'
					   AND WM.STACK_COL_GP = :V_STACK_COL_GP
					 */
					jrParam.setField("STACK_COL_GP", sStkClo);
					JDTORecordSet jsDelList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getExistWrkList", logId, methodNm, "상차도 작업예약 조회");
					for (int ii = 0; ii < jsDelList.size(); ++ii) {
						
						jrParam.setField("YD_WBOOK_ID", jsDelList.getRecord(ii).getFieldString("YD_WBOOK_ID"));
						
						//작업예약재료 삭제
						/*
						UPDATE TB_YM_WRKBOOKMTL
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN      = 'N'
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");				
	
						//작업예약 삭제
						/*
						UPDATE TB_YM_WRKBOOK
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN      = 'N'
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK");
					}
//				}
				
				int iSHEAR_SUPPLY_SEQ = 0;
				
				//상차대상 갯수 만큼 Looping... 2단 작업예약  생성
				for(int ii = 0; ii < iLoadCur; ii++) {

					FrtoProduct3 = (JDTORecord)lCarStockList.get(ii);

					//단정보 가져오기
					s_STACK_LAYER_GP = commUtils.nvl(FrtoProduct3.getFieldString("STACK_LAYER_GP"),"01");

					if("02".equals(s_STACK_LAYER_GP)) {
					
						//작업예약ID생성
						wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
						if ("".equals(first_wbook_ID)) {
							first_wbook_ID = wbook_ID; //첫번째 작업예약 ID 
						}

						//2단 작업예약 리스트에 추가
						lWrkbookIdList02.add(wbook_ID);
						
						
						/**********************************************************
						* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("YD_GP"			, s_STACK_YD_GP);
						jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP);
						jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
						jrParam.setField("YD_SCH_REQ_GP"	, "F"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
						jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
						jrParam.setField("YD_CAR_USE_GP"	, "L"); //야드차량사용구분 L:구내운송
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
						
						/**********************************************************
						* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("STOCK_ID"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						jrParam.setField("STACK_COL_GP"		, sStkClo);
						jrParam.setField("STACK_BED_GP"		, commUtils.trim(FrtoProduct3.getFieldString("STACK_BED_GP")));
						jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(FrtoProduct3.getFieldString("STACK_LAYER_GP")));
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
						
						/**********************************************************
						* 2-3. TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
						jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
						
						//차량대상재 테이블에 insert	
						//jrParam.setField("YD_CAR_SCH_ID"	, s_YD_CAR_SCH_ID);
						//jrParam.setField("STL_NO"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insertCarftmtl", logId, methodNm, "차량대상재 테이블에 insert	");
						
						s_ARR_WLOC_CD = commUtils.trim(FrtoProduct3.getFieldString("ARR_WLOC_CD"));
						
						//- 예정정보를 정확히 보내기 위해서 TB_YM_STOCK의 SHEAR_SUPPLY_SEQ 항목에 차상위치를 설정한다.
						jrParam.setField("SHEAR_SUPPLY_SEQ"		, "0"+(++iSHEAR_SUPPLY_SEQ));		//차량적재위치
						jrParam.setField("STOCK_ID"				, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockShearSupplySeq", logId, methodNm, "TB_YM_STOCK의 SHEAR_SUPPLY_SEQ에 차상위치 셋팅");
						
					}
					
				}

				//상차대상 갯수 만큼 Looping... 1단 작업예약  생성
				for(int ii = 0; ii < iLoadCur; ii++) {

					FrtoProduct3 = (JDTORecord)lCarStockList.get(ii);

					//단정보 가져오기
					s_STACK_LAYER_GP = commUtils.nvl(FrtoProduct3.getFieldString("STACK_LAYER_GP"),"01");

					if("01".equals(s_STACK_LAYER_GP)) {
					
						//작업예약ID생성
						wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
						if ("".equals(first_wbook_ID)) {
							first_wbook_ID = wbook_ID; //첫번째 작업예약 ID 
						}

						//1단 작업예약 리스트에 추가
						lWrkbookIdList01.add(wbook_ID);
						
						
						/**********************************************************
						* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("YD_GP"			, s_STACK_YD_GP);
						jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP);
						jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
						jrParam.setField("YD_SCH_REQ_GP"	, "F"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
						jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
						jrParam.setField("YD_CAR_USE_GP"	, "L"); //야드차량사용구분 L:구내운송
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
						
						/**********************************************************
						* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("STOCK_ID"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						jrParam.setField("STACK_COL_GP"		, sStkClo);
						jrParam.setField("STACK_BED_GP"		, commUtils.trim(FrtoProduct3.getFieldString("STACK_BED_GP")));
						jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(FrtoProduct3.getFieldString("STACK_LAYER_GP")));
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
						
						/**********************************************************
						* 2-3. TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
						jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
						
						//차량대상재 테이블에 insert	
						//jrParam.setField("YD_CAR_SCH_ID"	, s_YD_CAR_SCH_ID);
						//jrParam.setField("STL_NO"			, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insertCarftmtl", logId, methodNm, "차량대상재 테이블에 insert	");
						
						s_ARR_WLOC_CD = commUtils.trim(FrtoProduct3.getFieldString("ARR_WLOC_CD"));
						
						//- 예정정보를 정확히 보내기 위해서 TB_YM_STOCK의 SHEAR_SUPPLY_SEQ 항목에 차상위치를 설정한다.
						jrParam.setField("SHEAR_SUPPLY_SEQ"		, "0"+(++iSHEAR_SUPPLY_SEQ));		//차량적재위치
						jrParam.setField("STOCK_ID"				, commUtils.trim(FrtoProduct3.getFieldString("STOCK_ID")));
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockShearSupplySeq", logId, methodNm, "TB_YM_STOCK의 SHEAR_SUPPLY_SEQ에 차상위치 셋팅");
					}
					
				}
				
				/**********************************************************
				* 2-4. 차량스케쥴 update(도착시간, 실제도착위치 업데이트처리)
				**********************************************************/
				jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, first_wbook_ID);
				jrParam.setField("ARR_WLOC_CD"			, s_ARR_WLOC_CD);
				jrParam.setField("YD_PNT_CD1"			, szARR_YD_PNT_CD);
				jrParam.setField("YD_CARLD_STOP_LOC"	, sStkClo);
				jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
				jrParam.setField("FRTOMOVE_WORD_NO"		, sFRTOMOVE_WORD_NO); //이송작업지시번호
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt4 
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
				      ,ARR_WLOC_CD =:V_ARR_WLOC_CD
				      ,YD_PNT_CD1 = :V_YD_PNT_CD1
				      ,YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC
				      ,YD_CARLD_ARR_DT = SYSDATE
				      ,YD_CAR_PROG_STAT = '2' --상차도착
				      ,FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
				WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				AND DEL_YN = 'N' */
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt4", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
				
			}
			
			/**********************************************************
			* 차량작업 예정정보 송신 (YMA7L008)
			**********************************************************/
			jrParam.setField("SEARCH_FLAG"   , "2");				//1:상차도, 2:차량스케쥴 ID	
			jrParam.setField("YD_CAR_SCH_ID" , s_YD_CAR_SCH_ID); 	//야드차량스케쥴ID
			sndRecord = commUtils.addSndData(sndRecord, ymComm.procCarPlanInfo(jrParam));
			
			
//			String sAPP045 = ymComm.BCoilApplyYn("APP045","3","1");//차량하차시 저장품제원 정보 송신
			
//			if ("Y".equals(sAPP045)) {
				if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
					/**********************************************************
					* 저장품제원(YMA7L002) 전문 생성
					**********************************************************/
					jrParam.setField("YD_INFO_SYNC_CD"		, "3"				); //야드정보동기화코드 (열)
					jrParam.setField("MSG_GP"				, "I"				); //전문구분
					jrParam.setField("STACK_COL_GP"  		, sStkClo			); //야드적치열구분
					jrParam.setField("STACK_BED_GP"  		, ""				); //야드적치Bed번호
					jrParam.setField("YD_GP"          		, "3"				); //야드구분
					jrParam.setField("STOCK_ID"       		, ""				); //재료번호
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L002", jrParam));
				}
//			}
			
			
			/**********************************************************
			* Crane스케줄 호출
			*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
			*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
			**********************************************************/
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn 
			SELECT YD_CARPNT_CD
			      ,YD_STK_COL_ACT_STAT
			      ,YD_CAR_USETYPE_GP
			      ,WLOC_CD
			      ,YD_PNT_CD
			      ,NVL(YD_FRM_YN,'N') AS YD_FRM_YN
			FROM   TB_YD_CARPOINT
			WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP */
			jrParam.setField("YD_STK_COL_GP" , sStkClo); 	
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 "); 
			if ("N".equals(rsResult.getRecord(0).getFieldString("YD_FRM_YN"))) {
				
				//ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				
				//크레인 스케줄 기동 YMYMJ303 호출
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ303"); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
				
				int pcnt = 0;
				
				commUtils.printLog(logId, "[lWrkbookIdList02.size()] 결과 건수: " + lWrkbookIdList02.size() , "LOG");
				commUtils.printLog(logId, "[lWrkbookIdList01.size()] 결과 건수: " + lWrkbookIdList01.size() , "LOG");
				
				//2단 적치된 대상 호출
				for(int ii = 0; ii < lWrkbookIdList02.size(); ii++) {
					
					jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, (String)lWrkbookIdList02.get(ii)); //야드작업예약ID
					
				}
				
				//1단 적치된 대상 호출
				for(int ii = 0; ii < lWrkbookIdList01.size(); ii++) {
					
					jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, (String)lWrkbookIdList01.get(ii)); //야드작업예약ID
					
				}
				
				jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));
				
				sndRecord = commUtils.addSndData(sndRecord, jrCrnSchMsg);
			}
			
			commUtils.printLog(logId, methodNm, "SL");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ003_ABCoil_WL()		
	
	/**
	 *      [A] 오퍼레이션명 : 1열연 SLAB야드 소재차량도착(TSYDJ003) 수신처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ003_ABSlab(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "1열연Slab_소재차량도착수신처리[YmCommCarMvSeEJB.rcvTSYDJ003_ABSlab] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	
	    String szMsg           		= "";

	    String szTRN_EQP_CD; 
	    String szARR_WLOC_CD; 
	    String szARR_YD_PNT_CD; 
	    String szTRN_WRK_FULLVOID_GP; 
	    String szTRN_EQP_STK_CAPA; 
	    String szTRN_EQP_GP;
	    String msgId;
	    String modifier;
	    
	    String sStkClo; 
	    String s_STACK_YD_GP; 
	    String s_STACK_BAY_GP;
		String s_YD_CAR_SCH_ID = "";
		String sSchCode;
		String wbook_ID = "";
		String first_wbook_ID = "";
		String sYD_SCH_PRIOR;
		String sFRTOMOVE_WORD_NO = ""; //이송작업지시 번호
		String sSCH_AUTO_RUN_MODE = "M";
		String sYD_WRK_CRN = ""; 
		String sYD_MULTI_WRK_YN = ""; //야드멀티작업여부
		String sYD_EQP_WRK_STAT = "";
    	
    	int iWbook_ID_Cnt = 0;
		
	    JDTORecordSet rsResult    	= null;
	    JDTORecordSet rsResult2    	= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
	    
	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= szTRN_EQP_CD.substring(1, 3); //PT/TR 구분
			
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ003"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
	    	//B-CAST 트레일러 이면서 공차인경우 운송설비구분을  PT 로  변경
			if ("TR".equals(szTRN_EQP_GP) && YmConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD) && "E".equals(szTRN_WRK_FULLVOID_GP)) {
	    		szTRN_EQP_GP = "PT";
	    	}					
			
	    	//개소코드가 AB열연 개소코드인지 검사한다.
	    	if (!getABLocationInfo_02(szARR_WLOC_CD)) {
				throw new Exception("개소코드 오류 [" + szARR_WLOC_CD + "]는 AB열연 개소코드가 아닙니다!");
			}			
			
	    	//도착 포인트코드가 대기장(1Z99)인 경우 도착처리 안함
	    	if ("1Z99".equals(szARR_YD_PNT_CD)) {
				//throw new Exception("도착포인트코드가 [1Z99]대기장으로 도착처리 안함.");
	    		commUtils.printLog(logId, "도착포인트코드가 [1Z99]대기장으로 도착처리 안함.", "SL");
	    		return sndRecord;
	    	}
	    	
	    	//차량포인트(TB_YM_CARPOINT)를 개소코드와 야드포인트로 조회하여 도착처리 운송장비 코드와 동일한지 체크한다.
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk 
	    	SELECT YD_CARPNT_CD
	    	      ,YD_STK_COL_ACT_STAT
	    	      ,YD_CAR_USETYPE_GP
	    	      ,YD_GP
	    	      ,YD_BAY_GP
	    	      ,YD_STK_COL_GP
	    	      ,TRN_EQP_CD
	    	      ,CAR_NO
	    	      ,CARD_NO
	    	      ,WLOC_CD
	    	      ,YD_PNT_CD
	    	      ,YD_SPAN_FROM
	    	      ,YD_SPAN_TO
	    	      ,YD_STK_COL_GP2
	    	      ,YD_FRM_YN
	    	FROM   TB_YD_CARPOINT
	    	WHERE  WLOC_CD = :V_WLOC_CD
	    	AND    YD_PNT_CD = :V_YD_PNT_CD */
			jrParam.setField("WLOC_CD"	, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD", szARR_YD_PNT_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk", logId, methodNm, "차량포인트 체크  "); 
			if (rsResult.size() <= 0) {
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 차량포인트에 없는 위치입니다.");	
			} else {
				if ("R".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) {
					//예약일 경우 입력받은 운송장비코드와 동일한지 체크
					//if (!szTRN_EQP_CD.equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD"))) {
					//	throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 로 예약되어 있는데 " + szTRN_EQP_CD + " 로 도착처리 수신되었습니다.");
					//}
				} else if ("L".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) {
					//사용중일경우 에러 처리
					//throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  이미 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 가 입동되어 있는 위치입니다.");
					szMsg="착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  이미 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 가 입동되어 있는 위치입니다.";
					commUtils.printLog(logId, szMsg, "SL");
					return sndRecord;
				} else if ("N".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) {
					//사용금지일경우 에러 처리
					throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  사용금지 상태입니다.");
				}
			}
	    	
			//차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd 
	    	UPDATE  TB_YM_STACKCOL
	    	   SET  CAR_CARD_NO = ''
	    	       ,STACK_STAT = ''
	    	       ,MODIFIER = :V_MODIFIER
	    	       ,MOD_DDTT = SYSDATE
	    	 WHERE  CAR_CARD_NO = :V_CAR_CARD_NO  --TRN_EQP_CD   */
			jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear ");
			
			//차량 포인트 예약으로 잡혀있는정보 Clear
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPlnInfoReSet 
			UPDATE TB_YD_CARPOINT
			   SET YD_STK_COL_ACT_STAT = 'C'
			      ,TRN_EQP_CD = ''
			      ,MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			   AND YD_STK_COL_ACT_STAT = 'R' */
			jrParam.setField("TRN_EQP_CD",szTRN_EQP_CD);  //운송장비코드
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPlnInfoReSet", logId, methodNm, "차량 포인트 예약으로 잡혀있는정보 Clear ");
			
	    	
			////차량저장위치 포인트에 입동유무 체크 
			////착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인 
			///* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.stackcolpointchk  
			//SELECT  STACK_COL_GP
			//  FROM  TB_YM_STACKCOL
			// WHERE  WLOC_CD = :V_WLOC_CD
			//   AND  YD_PNT_CD = :V_YD_PNT_CD
			//   AND  TRN_EQP_CD IS NULL
			//   AND  CAR_NO IS NULL
			//   AND  CARD_NO IS NULL
			//   AND  SECT_GP = 'PT' */ 
			//jrParam.setField("WLOC_CD"	, szARR_WLOC_CD); 	
			//jrParam.setField("YD_PNT_CD", szARR_YD_PNT_CD); 
			//rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.stackcolpointchk", logId, methodNm, "착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인  "); 
			//if (rsResult.size() <= 0) {
			//	throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 로 적치열 조회 결과 이미 입동되어 있는 위치입니다.");
			//}
			
			//차량저장위치 점유	  
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01 
			UPDATE  TB_YM_STACKCOL
			   SET  YD_CAR_USE_GP = :V_YD_CAR_USE_GP
			       ,TRN_EQP_CD = :V_TRN_EQP_CD
			       ,CAR_NO = :V_CAR_NO
			       ,CARD_NO = :V_CARD_NO
			 WHERE  WLOC_CD = :V_WLOC_CD
			   AND  YD_PNT_CD = :V_YD_PNT_CD
			   AND  SECT_GP = 'PT' */ 	
			jrParam.setField("YD_CAR_USE_GP", "L"); //L:구내운송차량 , G:출하차량  	--야드차량사용구분
			jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
			jrParam.setField("CAR_NO"		, ""); 
			jrParam.setField("CARD_NO"		, ""); 
			jrParam.setField("WLOC_CD"		, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD"	, szARR_YD_PNT_CD); 
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01", logId, methodNm, "차량저장위치 점유 "); 
			
			
			//차량포인트통합관리 (1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			this.YmCarPointinforeg("4","",szTRN_EQP_CD,"",szARR_WLOC_CD,szARR_YD_PNT_CD,"L", logId, methodNm);
			
			
			//운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListstlQty 
			SELECT  COUNT(B.STL_NO) AS QTY
			  FROM  TB_YD_CARSCH A
			       ,TB_YD_CARFTMVMTL B
			 WHERE  A.DEL_YN = 'N'
			   AND  A.TRN_EQP_CD = :V_TRN_EQP_CD
			   AND  A.YD_CAR_PROG_STAT = 'A' -- 차량진행상태 A:하차출발
			   AND  A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID */ 
			jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListstlQty", logId, methodNm, "운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기  ");
			String szQTY = commUtils.trim(rsResult.getRecord(0).getFieldString("QTY"));
			
			//해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다.
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_Qty 
			UPDATE TB_YM_STACKLAYER
			   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT  
			 WHERE STACK_COL_GP = (SELECT  STACK_COL_GP	    	 		
			                         FROM  TB_YM_STACKCOL
			                        WHERE  WLOC_CD = :V_WLOC_CD
			                          AND  YD_PNT_CD = :V_YD_PNT_CD
			                          AND  SECT_GP = 'PT'
			                      )
			   AND TO_NUMBER(STACK_LAYER_GP) <= :V_QTY */
			jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, "E"); //적치가능
			jrParam.setField("QTY"						, "6"); 
			jrParam.setField("WLOC_CD"					, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD"				, szARR_YD_PNT_CD); 
			if ("0".equals(szQTY)) {
				jrParam.setField("QTY"					, "6"); 
			} else {
				jrParam.setField("QTY"					, szQTY); 
			}
			//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_Qty", logId, methodNm, "해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다 "); //**주의! A열연,B열연 분리 해야함!!
			bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_Qty");
			
			//실제 도착위치 업데이트 및 영차도착시 맵정보 생성을 위해 
			//개소코드와 야드포인트 코드로 적치열구분 조회 야드구분, 동 정보를 구한다.
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStkColgp 
			SELECT STACK_COL_GP
			  FROM TB_YM_STACKCOL
			 WHERE WLOC_CD = :V_WLOC_CD
			   AND YD_PNT_CD = :V_YD_PNT_CD 
			   AND SECT_GP = 'PT'			*/
			jrParam.setField("WLOC_CD"					, szARR_WLOC_CD); 	
			jrParam.setField("YD_PNT_CD"				, szARR_YD_PNT_CD); 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStkColgp", logId, methodNm, "개소코드와 야드포인트 코드로 적치열구분 조회"); //**주의! A열연,B열연 분리 해야함!!
			if (rsResult.size() <= 0) {
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 로 적치열 조회 결과 : " + rsResult.size());
			}
			
			sStkClo			= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
			s_STACK_YD_GP	= sStkClo.substring(0, 1);
			s_STACK_BAY_GP  = sStkClo.substring(1, 2);
			
			if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
				
				sYD_EQP_WRK_STAT = "L"; //영차
				//**********************************************************************************	
				//1.운송작업영공구분이 F:영차 인 경우 처리
				szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//운송장비코드로 이송재료 조회 + 작업예약ID
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListFrtostlList
				SELECT  STL_NO
				       ,A.YD_STK_BED_NO
				       ,SUBSTR(A.YD_STK_LYR_NO,2,2) AS STK_LYR
				       ,A.YD_CAR_SCH_ID
				       ,(SELECT CS.YD_CARUD_WRK_BOOK_ID FROM TB_YD_CARSCH CS WHERE CS.YD_CAR_SCH_ID = A.YD_CAR_SCH_ID) AS YD_CARUD_WRK_BOOK_ID
				  FROM TB_YD_CARFTMVMTL A 
				 WHERE A.YD_CAR_SCH_ID = (SELECT MAX(A.YD_CAR_SCH_ID)
				                            FROM TB_YD_CARSCH A
				                           WHERE A.TRN_EQP_CD = :V_TRN_EQP_CD  
				                             AND A.DEL_YN = 'N')
				   AND A.DEL_YN = 'N'
				 ORDER BY A.YD_STK_LYR_NO */
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); 
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListFrtostlList", logId, methodNm, "운송장비코드로 이송재료 조회");
				if (rsResult.size() <= 0) {
					throw new Exception("영차(F) 도착처리 대상재가 존재 안함");
				} else {
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
					//wbook_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARUD_WRK_BOOK_ID"));
					//2023.05.23 열연1부 김기훈 주임님 요청. 이미 도착해서 작업하고잇는 슬라브는 도착 다시 못하게끔 수정 
					boolean isWorking = false;
					for(int i=0; i<rsResult.size(); i++){
						int liveWrkCnt = Integer.parseInt(rsResult.getRecord(i).getFieldString("LIVE_WRK_CNT"));
						if(liveWrkCnt>0){
							isWorking = true;
						}
					}
					if(isWorking){
						throw new Exception("작업예약이 존재하는 차량스케줄은 재도착처리 불가.");
					}
				}
				
				/**********************************************************
				* 영차도착 포인트 적치단(TB_YM_STACKLAYER) CLEAR 하기
				**********************************************************/
			    jrParam.setField("STACK_COL_GP"    	, sStkClo	);
			    jrParam.setField("STACK_BED_GP"    	, "01"		);
			    jrParam.setField("STACK_LAYER_STAT1", "%"		);
			    jrParam.setField("STACK_LAYER_STAT2", "E"		);
			    jrParam.setField("STOCK_ID"			, ""		);
			    /* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo
			    UPDATE TB_YM_STACKLAYER
			       SET 
			           MODIFIER = :V_MODIFIER
			          ,MOD_DDTT = SYSDATE
			          ,STOCK_ID = :V_STOCK_ID
			          ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
			     WHERE  STACK_COL_GP = :V_STACK_COL_GP
			       AND  STACK_BED_GP = :V_STACK_BED_GP
			       AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
			    szMsg = " 적치단 정보 Clear  << " + methodNm;
			    commUtils.printLog(logId, szMsg, "SL");
			    bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo");
				
				
				//하차대상 갯수 만큼 Looping...
				for(int ii= 0; ii < rsResult.size() ; ii++) {
				
					/**********************************************************
					* 영차도착 포인트 적치단(TB_YM_STACKLAYER)에 SLAB정보 생성하기
					**********************************************************/
					jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("STACK_COL_GP"		, sStkClo);
					jrParam.setField("STACK_BED_GP"		, "01");
					jrParam.setField("STACK_LAYER_GP"	, commUtils.format(ii+1, 2));
					
					//commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setStackLayer", logId, methodNm, "영차도착 포인트 적치단(TB_YM_STACKLAYER)에 SLAB정보 생성하기");
					bSlabComm.insStackLayer(jrParam);
				}
				
				/**********************************************************
				* STOCK에서 기존  SCAN 좌표값 초기 화 
				**********************************************************/
				for(int ii = 0; ii < rsResult.size() ; ii++) {
					
					jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO"))); 
					jrParam.setField("LOAD_LOC_CD"		, ""); 
					jrParam.setField("WGT_CENTER_XAXIS"	, ""); 
					jrParam.setField("WGT_CENTER_YAXIS"	, ""); 
					jrParam.setField("WGT_CENTER_ZAXIS"	, ""); 
					jrParam.setField("BENDING_GP"		, ""); 
					jrParam.setField("BENDING_AXIS"		, ""); 
					jrParam.setField("YD_STK_COL_DIR_GP", ""); 
					jrParam.setField("YD_STK_COL_DEG"	, ""); 
					jrParam.setField("CAU_CD"			, "_");
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockByA8YML029
					UPDATE TB_YM_STOCK
					   SET MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,LOAD_LOC_CD = :V_LOAD_LOC_CD
					      ,WGT_CENTER_XAXIS = :V_WGT_CENTER_XAXIS
					      ,WGT_CENTER_YAXIS = :V_WGT_CENTER_YAXIS
					      ,WGT_CENTER_ZAXIS = :V_WGT_CENTER_ZAXIS
					      ,BENDING_GP = :V_BENDING_GP
					      ,BENDING_AXIS = :V_BENDING_AXIS
					      ,YD_STK_COL_DIR_GP = :V_YD_STK_COL_DIR_GP
					      ,YD_STK_COL_DEG = :V_YD_STK_COL_DEG
					      ,YD_RULE_PL_RS_GP = CASE WHEN TO_NUMBER(NVL(:V_BENDING_AXIS,0)) >= (SELECT NVL(DTL_ITM1,0)
					                                                                                FROM USRYMA.TB_YM_RULE 
					                                                                               WHERE CD_GP = '2' 
					                                                                                 AND REPR_CD_GP LIKE'YM2009'
					                                                                                 AND DEL_YN = 'N') THEN 'Y'
					                               ELSE '' END 
					      ,CAU_CD = NVL(:V_CAU_CD,'0000');
					 WHERE STOCK_ID = :V_STOCK_ID  */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockByA8YML029", logId, methodNm, "STOCK에서 기존  SCAN 좌표값 초기 화  (tb_ym_stock 관련 항목 update)");
				}
				
				
				//스케줄코드 생성  - 이송하차(L)
				sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02LM";
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", sSchCode);
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				     , YD_MULTI_WRK_YN
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
		    	
				if (rsResult2 != null && rsResult2.size() > 0) {
					sYD_WRK_CRN = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN"); //야드작업크레인
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
					sYD_MULTI_WRK_YN = rsResult2.getRecord(0).getFieldString("YD_MULTI_WRK_YN"); //야드멀티작업여부
				} else {
					throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}			
				
				
					
					
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				//------------------------------------------------------------------------------------------------------------------------
				//작업예약 슬라브 단위로 생성

				String sYD_FRM_YN = "N";
				
				/**********************************************************
				* 도착동 차량형상 시스템 사용여부 확인
				*  - TB_YM_RULE 테이블의 YM2006 기준으로 사용 여부 확인
				**********************************************************/
				jrParam.setField("REPR_CD_GP"	, "YM2006");
				jrParam.setField("CD_GP"		, s_STACK_YD_GP);
				jrParam.setField("ITEM"			, s_STACK_BAY_GP);
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 
				
				if (rsResult2.size() > 0) {
					sYD_FRM_YN = commUtils.nvl(rsResult2.getRecord(0).getFieldString("DTL_ITM1"),"N");
				}
				
				commUtils.printLog(logId, "=======:::: YM2006 " + s_STACK_BAY_GP + "동 차량형상인식 사용여부: " +  sYD_FRM_YN , "SL");
				
				//if ("N".equals(rsResult2.getRecord(0).getFieldString("YD_FRM_YN"))) {
				if ("N".equals(sYD_FRM_YN)) {
					
					//이송 작업지시 번호 가져오기
					sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo"); 
					
					
					if ("Y".equals(sYD_MULTI_WRK_YN)) {
						jrParam.setField("YD_SCH_ST_GP"		, "N"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						if ("A".equals(s_STACK_BAY_GP)) {
							if ("2ACRA1".equals(sYD_WRK_CRN)) {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA1"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA2"); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA2"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA1"); //야드작업계획크레인2
							}
						} else if ("D".equals(s_STACK_BAY_GP)) {
							if ("2DCRD3".equals(sYD_WRK_CRN)) {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD3"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD2"); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD2"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD3"); //야드작업계획크레인2
							}
						} else {
							jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_CRN); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
						}
						
					} else {
						jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_CRN); //야드작업계획크레인
						jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
					}
					
					iWbook_ID_Cnt = 0;
					JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
					
					//하차대상 갯수 만큼 Looping...
					//for(int ii= 0; ii < rsResult.size() ; ii++) {
					for(int ii=(rsResult.size()-1) ; ii >= 0 ; ii--) {
						
						//작업예약ID생성
						wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
						
						iWbook_ID_Cnt++;
						
						jrCrnSchMsg.setField("YD_WBOOK_ID"+(iWbook_ID_Cnt),wbook_ID);
						jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(iWbook_ID_Cnt));
						
						/**********************************************************
						* 1-1. 작업예약(TB_YM_WRKBOOK) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("YD_GP"			, s_STACK_YD_GP);
						jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP);
						jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_REQ_GP"	, "C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
						jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
						jrParam.setField("YD_CAR_USE_GP"	, "L"); //야드차량사용구분 L:구내운송
						
						//commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
						bSlabComm.insWrkBook(jrParam);
						
						
						/**********************************************************
						* 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
						jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
						jrParam.setField("STACK_COL_GP"		, sStkClo);
						jrParam.setField("STACK_BED_GP"		, "01");
						jrParam.setField("STACK_LAYER_GP"	, commUtils.format(ii+1, 2));
						
						//commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
						bSlabComm.insWrkBookMtl(jrParam);
						
						/**********************************************************
						* TB_YM_STOCK의이송작업지시번호(TRANS_WORD_NO) 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
						//jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
						jrParam.setField("STOCK_MOVE_TERM"	, "" ); //영차출발수신시 지정됨.. 여기서는 변경하지 않고 이전 값을 그대로 설정함
						jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockTransWordNo
						UPDATE TB_YM_STOCK
						   SET MODIFIER   = :V_MODIFIER
						     , MOD_DDTT   = SYSDATE 
						     , STOCK_MOVE_TERM = NVL(:V_STOCK_MOVE_TERM,STOCK_MOVE_TERM)
						     , FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
						WHERE STOCK_ID = :V_STOCK_ID */
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
						
						///**********************************************************
						//* 1-4. 영차도착 포인트 적치단(TB_YM_STACKLAYER)에 SLAB정보 생성하기
						//**********************************************************/
						//jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
						//jrParam.setField("STACK_COL_GP"		, sStkClo);
						//jrParam.setField("STACK_BED_GP"		, "01");
						//jrParam.setField("STACK_LAYER_GP"	, commUtils.format(ii+1, 2));
						
						////commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setStackLayer", logId, methodNm, "영차도착 포인트 적치단(TB_YM_STACKLAYER)에 SLAB정보 생성하기");
						//bSlabComm.insStackLayer(jrParam);
					}
					
					/**********************************************************
					* Pallet조회 (B)에서 지정한 크레인스케줄 생성 모드 확인
					*  - Auto 이면 크레인 스케줄을 호출 한다.
					**********************************************************/
					jrParam.setField("REPR_CD_GP"	, "YM2002");
					jrParam.setField("CD_GP"		, s_STACK_YD_GP);
					jrParam.setField("ITEM"			, s_STACK_BAY_GP);
					rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 
					
					if (rsResult2.size() > 0) {
						sSCH_AUTO_RUN_MODE = rsResult2.getRecord(0).getFieldString("DTL_ITM1");
					}
					
					if ("A".equals(sSCH_AUTO_RUN_MODE)) { //Auto 모드일 경우만 스케줄 호출
						
						//크레인 스케줄 기동 YMYMJ203 호출
						jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ203"); 
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시	
						jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
						jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
						
						sndRecord = commUtils.addSndData(sndRecord, jrCrnSchMsg);
					}
				} 
				
				//------------------------------------------------------------------------------------------------------------------------
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					
					
					
				
				/**********************************************************
				* 1-5. 차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)
				**********************************************************/
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt5 
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
				      ,YD_PNT_CD3 = :V_YD_PNT_CD3
				      ,YD_CARUD_ARR_DT = SYSDATE
				      ,YD_CAR_PROG_STAT = 'B' -- 하차도착
				      ,YD_CARUD_WRK_BOOK_ID= :V_YD_CARUD_WRK_BOOK_ID
				      ,FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
				WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				AND DEL_YN = 'N' */
				jrParam.setField("YD_CARUD_STOP_LOC"	, sStkClo);
				jrParam.setField("YD_PNT_CD3"			, szARR_YD_PNT_CD);
				jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, wbook_ID);
				jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
				jrParam.setField("FRTOMOVE_WORD_NO"		, sFRTOMOVE_WORD_NO); //이송작업지시번호
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateArrDt5", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
				
				
			} else if ("E".equals(szTRN_WRK_FULLVOID_GP)) {
				
				sYD_EQP_WRK_STAT = "U"; //공차
				//**********************************************************************************	
				//2.운송작업영공구분이 E:공차 인 경우 처리
				szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				/**********************************************************
				* 2-4. 차량스케쥴 update(도착시간, 실제도착위치 업데이트처리)
				**********************************************************/
				jrParam.setField("YD_CARLD_STOP_LOC"	, sStkClo);
				jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateArrDt_1
				UPDATE TB_YD_CARSCH
				  SET  MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC
				      ,YD_CARLD_ARR_DT = SYSDATE
				      ,YD_CAR_PROG_STAT = '2' --상차도착
				WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				AND DEL_YN = 'N' */
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateArrDt_1", logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");
				
				
				//차량스케줄 ,작업예약ID 조회 
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListtrnEqpschL_1
				SELECT YD_CARLD_WRK_BOOK_ID AS WBOOK_ID
				      ,YD_CAR_SCH_ID
				  FROM TB_YD_CARSCH
				 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
				   AND YD_CAR_PROG_STAT = '2'
				   AND (DEL_YN IS NULL OR DEL_YN <> 'Y') */				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListtrnEqpschL_1", logId, methodNm, "차량스케줄 ,작업예약ID 조회");
				if (rsResult.size() > 0) {
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
					wbook_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("WBOOK_ID"));
					
					if (!"".equals(wbook_ID)) {
						//작업예약이 있는경우(자동상차인 경우)
						
						/**********************************************************
						* Crane스케줄 호출
						*  - TB_YM_RULE 테이블의 YM2006 기준으로  차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						jrParam.setField("REPR_CD_GP"	, "YM2006");
						jrParam.setField("CD_GP"		, s_STACK_YD_GP);
						jrParam.setField("ITEM"			, s_STACK_BAY_GP);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "야드 기준 조회"); 

						String sYD_FRM_YN = "N";
						if (rsResult.size() > 0) {
							sYD_FRM_YN = rsResult.getRecord(0).getFieldString("DTL_ITM1");
						}
						
						commUtils.printLog(logId, "=======:::: YM2006 " + s_STACK_BAY_GP + " 동 차량형상인식 사용여부: " +  sYD_FRM_YN , "SL");
						
						if ("N".equals(sYD_FRM_YN)) {
							
							//ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							
							//크레인 스케줄 기동 YMYMJ202 호출
							jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시	
							jrCrnSchMsg.setField("YD_WBOOK_ID"  		, wbook_ID); //작업예약ID
							jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
							
							
							sndRecord = commUtils.addSndData(sndRecord, jrCrnSchMsg);
						}			
					}
					
				}

				//이송상차 도착처리시 TB_YM_RULE 의 YM2019 해당 포인트를 초기화 한다..
				jrParam.setField("REPR_CD_GP"	, "YM2019" );
				jrParam.setField("CD_GP"		, "2" );
				jrParam.setField("ITEM"			, sStkClo);
				jrParam.setField("DTL_ITM4"		, "");
				jrParam.setField("DTL_ITM5"		, "N");

				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYmRuleNvl", logId, methodNm, "TB_YM_RULE YM2019 수정 - 초기화");

				
			}
			
			/**********************************************************
			* 차량작업 예정정보 송신 (YMA8L008)
			**********************************************************/
			jrParam.setField("SEARCH_FLAG" 			, "1"				);	//1:상차도, 2:차량스케쥴 ID	
			jrParam.setField("PT_LOAD_LOC" 			, sStkClo			); 	//상차도 위치
			sndRecord = commUtils.addSndData(sndRecord, ymComm.procCarPlanInfo_Slab(jrParam));
			
			/**********************************************************
			* 저장위치제원정보 송신 (YMA8L001)
			**********************************************************/
			//JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			jrParam.setField("YD_INFO_SYNC_CD"		, "4"				); //야드정보동기화코드
			jrParam.setField("STACK_COL_GP"    		, sStkClo			);
			jrParam.setField("STACK_BED_GP"    		, "01"				);
			jrParam.setField("YD_CAR_ARRSTRT_STAT" 	, "A"				); //A:도착, S:출발
			jrParam.setField("YD_CAR_USE_GP"    	, "L"				); //L:구내운송, G:출하차량
			jrParam.setField("YD_EQP_WRK_STAT"  	, sYD_EQP_WRK_STAT	); //U:공차, L:영차
			jrParam.setField("TRN_EQP_CD"  			, szTRN_EQP_CD		); //운송장비코드
			jrParam.setField("YD_CAR_AIM_YD_GP"		, s_STACK_YD_GP		); 
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L001_CarInfo", jrParam));		
			
			if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
				/**********************************************************
				* 저장품제원(YMA8L002) 전문 생성
				**********************************************************/
				jrParam.setField("YD_INFO_SYNC_CD"		, "4"				); //야드정보동기화코드
				jrParam.setField("MSG_GP"				, "I"				); //전문구분
				jrParam.setField("STACK_COL_GP"  		, sStkClo			); //야드적치열구분
				jrParam.setField("STACK_BED_GP"  		, "01"				); //야드적치Bed번호
				jrParam.setField("YD_GP"          		, "2"				); //야드구분
				jrParam.setField("STOCK_ID"       		, ""				); //재료번호
				sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L002", jrParam));
			}
			
			commUtils.printLog(logId, methodNm, "SL");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ003_ABSlab()			
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량출발(TSYDJ004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ004(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "소재차량출발[YmCommCarMvSeEJB.rcvTSYDJ004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
		
		//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		String msgId = commUtils.getMsgId(rcvMsg); 
        if (msgId==null || msgId.equals("")) {
        	return sndRecord;
        }
        
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "소재차량출발 수신 ", rcvMsg);
			
	    	//수신항목 변수 저장
			String szSPOS_WLOC_CD   = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")); 	//발지개소코드
			String szARR_WLOC_CD 	= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 	//착지개소코드
			
			//운송지시 발지,착지개소코드로 방향을 판단한다.(AA:AB열연에서 AB열연으로 이송,AC:AB열연에서 일관제철로 이송,CA:일관제철에서 AB열연으로 이송,CC:일관제철에서 일관제철로 이송)
			String sWorkGp = this.getABLocationInfo_01(szSPOS_WLOC_CD,szARR_WLOC_CD,logId);
			
			if (!"CC".equals(sWorkGp)) {
				
				rcvMsg.setField("WORK_GP", sWorkGp);
			
				if (	YmConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD) //D2Y44 : A열연-#1 제품/소재 Coil Yard
				||	YmConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)	//D2Y45 : A열연-#2 제품/소재 Coil Yard
				||	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
				||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y42 : B열연-#2 제품/소재 Coil Yard
				||	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szSPOS_WLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
				||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szSPOS_WLOC_CD)	//D3Y42 : B열연-#2 제품/소재 Coil Yard
				
				) {
					String  sAPP055_YN = ymComm.BCoilApplyYn("APP055","3","1"); //구내운송차량 대기장 도착 후 입동지시 여부
					
					if("Y".equals(sAPP055_YN))  {
						//1열연 COIL야드 신규 출발처리 (대기장 도착 후 입동 하는 방식) 
						sndRecord = this.rcvTSYDJ004_ABCoil_WL(rcvMsg);
					} else {
						//1열연 COIL야드 기존 출발처리 
						sndRecord = this.rcvTSYDJ004_ABCoil(rcvMsg);
					}
					
				} else {
					
					//AB열연 Slab 야드인 경우
					sndRecord = this.rcvTSYDJ004_ABSlab(rcvMsg);
				}
			} 
			//CC:일관제철에서 일관제철로 이송은 처리하지 않는다.
			
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ004()
	
	/**
	 *      [A] 오퍼레이션명 : 1열연 COIL야드 소재차량출발(TSYDJ004) 수신처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ004_ABCoil(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "1열연Coil_소재차량출발수신처리[YmCommCarMvSeEJB.rcvTSYDJ004_ABCoil] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	
	    String szMsg           		= "";
	    
	    String szTRN_EQP_CD; 
	    String szSPOS_WLOC_CD; 
	    String szSPOS_YD_PNT_CD; 
	    String szARR_WLOC_CD; 
	    String szARR_YD_PNT_CD; 
	    String szTRN_WRK_FULLVOID_GP; 
	    String szTRN_EQP_STK_CAPA; 
	    String sWorkGp;
	    String szTRN_EQP_GP;
	    String msgId;
	    String modifier;
		
	    String szYD_MSG_NM			= "";
	    String s_STL_APPEAR_GP 		= "";
	    String unloadStopwloc		= "";
	    String unloadStoppoint		= "";
		String sloadStopTsCd 		= "";
		String sunloadStoppoint 	= "";
		String s_CAR_SCH_ID			= "";
		String s_STACK_YD_GP 		= "";
		String s_STACK_BAY_GP 		= "";
		String s_ARR_WLOC_CD		= "";
		String s_TRN_WRK_FULLVOID_GP		= "";
	    
	    JDTORecordSet rsResult    	= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= null; //임시  JDTORecord 
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szSPOS_WLOC_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")); 		//발지개소코드
			szSPOS_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")); 		//발지야드포인트코드
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= szTRN_EQP_CD.substring(1, 3); //PT/TR 구분
			
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ004"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			//////////////////////////////////////////////////////////////////////////////////////
			// 2019.08.21  
			// 		구내운송에서 동일한 위치에 다른 차량번호로 출발시적이 발생
			// 		그로 인해서 L2로 잘못 된 출발정보가 전송되지 않도록
			// 		해당 위치에 실재로 존재한는 차량번호를 체크(변수에 저장)하여 L2 전송여부 판단에 사용한다.
			//////////////////////////////////////////////////////////////////////////////////////
			String szREAL_TRN_EQP_CD = "";
			
			jrParam.setField("WLOC_CD"	   , szSPOS_WLOC_CD			); //발지개소코드(상차지)
			jrParam.setField("YD_PNT_CD"   , szSPOS_YD_PNT_CD		); //발지개소코드(상차지)
			JDTORecordSet rsChk  = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk", logId, methodNm, "TB_YD_CARPOINT 에서 점유 중인 차량번호 조회");
			
			if (rsChk.size() > 0 ) {
				
				szREAL_TRN_EQP_CD = commUtils.trim(rsChk.getRecord(0).getFieldString("TRN_EQP_CD"));
			}
			
			szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " ■ ■ ■ ";
			commUtils.printLog(logId, szMsg, "SL");			
			//////////////////////////////////////////////////////////////////////////////////////
			
			//TRN_EQP_CD(운송장비코드)로 차량스케줄을 체크하여 남아있는 스케줄이 있다면 초기화 한다.
			this.initCarSch(rcvMsg);
			
			if ("DMY1P".equals(szARR_WLOC_CD)) {
				//착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제 
				szMsg="착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//차량위치 예정정보 삭제(상하차출발 위치) 정리
				jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리 ");
				
				//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				this.YmCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
			}
			
			if ("E".equals(szTRN_WRK_FULLVOID_GP)) {
				s_TRN_WRK_FULLVOID_GP = "U";
			} else {
				s_TRN_WRK_FULLVOID_GP = "L";
			}
			
			//운송지시 발지,착지개소코드로 방향을 판단한다.(AA:AB열연에서 AB열연으로 이송,AC:AB열연에서 일관제철로 이송,CA:일관제철에서 AB열연으로 이송)
			sWorkGp = commUtils.trim(rcvMsg.getFieldString("WORK_GP"));
			
			if ("AA".equals(sWorkGp)||"CA".equals(sWorkGp)) {
				//**********************************************************************************	
				//1.AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우
				
				szMsg="AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
					
					//**********************************************************************************	
					//1-1.운송작업영공구분이 F:영차 인 경우 처리
					szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					//코일 공통에서 STL_APPEAR_GP를 TS_소재이송지시(MTP)에서 착지개소코드, 착지야드포인트를 조회한다.
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtostlList_CoilAppearGp", logId, methodNm, "코일 공통에서 STL_APPEAR_GP를 TS_소재이송지시(MTP)에서 착지개소코드, 착지야드포인트를 조회");
					
					if (rsResult.size() > 0) {
						
						s_STL_APPEAR_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));
						unloadStopwloc  = commUtils.trim(rsResult.getRecord(0).getFieldString("ARR_WLOC_CD"));   //TS_소재이송지시(MTP) 에서 조회한 것으로 제품일 경우만 사용한다.
						unloadStoppoint = commUtils.trim(rsResult.getRecord(0).getFieldString("ARR_YD_PNT_CD")); //TS_소재이송지시(MTP) 에서 조회한 것으로 제품일 경우만 사용한다. (동 정보)
						
						szMsg=" 검색결과 >> STL_APPEAR_GP : " + s_STL_APPEAR_GP +", ARR_WLOC_CD : " + unloadStopwloc + ", ARR_YD_PNT_CD : " + unloadStoppoint;
						commUtils.printLog(logId, szMsg, "SL");
					}
					
					if ("AA".equals(sWorkGp)) {
						
						szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//발지 차량정보 삭제처리 
						//this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
						
						EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
						ejbConn1.trx("clearSposCarInfo", new Class[] { String.class,String.class,String.class,String.class,String.class }
										              , new Object[] { szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier });	
					}
					
					
					if ("Y".equals(s_STL_APPEAR_GP)) {
						//**********************************************************************************	
						//1-1-1.제품인 경우 처리
						szMsg="운송작업영공구분이 F:영차 이면서 제품인 경우 처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//코일 제품 하차 정지위치 검색 (해당 목적동에 점유하지 않은 포인트 검색)
						jrParam.setField("WLOC_CD"	, unloadStopwloc	); //TS_소재이송지시 (MTP)의 착지개소코드
						jrParam.setField("YD_PNT_CD", unloadStoppoint	); //TS_소재이송지시 (MTP)의 착지야드포인트코드
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppointGD", logId, methodNm, "코일 제품 하차 정지위치 검색"); //**주의! A열연,B열연 분리 해야함!!
						
						if (rsResult.size() > 0) {
							
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							sloadStopTsCd 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							
							szMsg=" 하차포인트 : " + sloadStopTsCd +", 야드하차위치 : " + sunloadStoppoint + " 에 예약";
							commUtils.printLog(logId, szMsg, "SL");
							
							//TB_YM_STACKCOL 예약정보등록 
							jrParam.setField("STACK_STAT"	, "L"); 
							jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
							jrParam.setField("STACK_COL_GP"	, sunloadStoppoint);
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
							
							//차량포인트통합관리 - 예약
							this.YmCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
							
						} else {

							szMsg=" 검색결과 >> 포인트를 찾지 못함! '0000'";
							commUtils.printLog(logId, szMsg, "SL");
							
							sloadStopTsCd = "0000"; //포인트 없음
							
							//대기장 포인트 사유 가져오기 
							szYD_MSG_NM = this.getCarMsg(s_STL_APPEAR_GP ,"" , "" , szTRN_EQP_GP , unloadStopwloc, unloadStoppoint, logId, methodNm);
						}
						
					} else {
						//**********************************************************************************	
						//1-1-2.소재인 경우 처리
						szMsg="운송작업영공구분이 F:영차 이면서 소재인 경우 처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarSchByTrnEqpCd", logId, methodNm, "차량스케줄 존재여부 체크");
						if (rsResult.size() > 0) {
							s_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
							szMsg=" 검색결과 >> YD_CAR_SCH_ID : " + s_CAR_SCH_ID ;
							commUtils.printLog(logId, szMsg, "SL");
						}
						
						//소재인 경우는 코일공통에 계획공정 및 차공정(우선)으로 목적 동 정보를 구한다.
						if (	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
						||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD) //D3Y42 : B열연-#2 제품/소재 Coil Yard
						) {
			    			//B열연 COIL야드
			    		    s_STACK_YD_GP 	= "3";	
							jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID); //야드차량스케쥴ID
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tsinfo.getListAimBay_BCoil", logId, methodNm, "B열연 COIL야드 소재 하차 이송 목적동 검색");
							if (rsResult.size() > 0) {
						    	s_STACK_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("DEST_BAY")); //목적동
							}
							szMsg=" B열연 COIL야드 소재 하차 이송 목적동 검색결과 >> DEST_BAY : " + s_STACK_BAY_GP ;
							commUtils.printLog(logId, szMsg, "SL");
					    	
				    	} else {
				    		//A열연 COIL야드
			    			s_STACK_YD_GP 	= "1";
							jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID); //야드차량스케쥴ID
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tsinfo.getListAimBay_ACoil", logId, methodNm, "A열연 COIL야드 소재 하차 이송 목적동 검색");
							if (rsResult.size() > 0) {
						    	s_STACK_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("DEST_BAY")); //목적동
							}
							szMsg=" A열연 COIL야드 소재 하차 이송 목적동 검색결과 >> DEST_BAY : " + s_STACK_BAY_GP ;
							commUtils.printLog(logId, szMsg, "SL");
			    		}			    						    			
			    		
						//-----------------------------------------------------------------------
						//코일 소재 하차 정지위치 검색 (해당 목적동에 점유하지 않은 포인트 검색)
						jrParam.setField("WLOC_CD"	, szARR_WLOC_CD	); 
						jrParam.setField("BAY_GP"	, s_STACK_BAY_GP); 
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppointCM", logId, methodNm, "코일 소재 하차 정지위치 검색"); //**주의! A열연,B열연 분리 해야함!!
						
						if (rsResult.size() > 0) {
							
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							sloadStopTsCd 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							
							szMsg=" 하차포인트 : " + sloadStopTsCd +", 야드하차위치 : " + sunloadStoppoint + " 에 예약";
							commUtils.printLog(logId, szMsg, "SL");
							
							//TB_YM_STACKCOL 예약정보등록 
							jrParam.setField("STACK_STAT"	, "L"); 
							jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
							jrParam.setField("STACK_COL_GP"	, sunloadStoppoint);
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
							
							//차량포인트통합관리 - 예약
							this.YmCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
							
						} else {

							szMsg=" 검색결과 >> 포인트를 찾지 못함! '0000'";
							commUtils.printLog(logId, szMsg, "SL");
							
							sloadStopTsCd = "0000"; //포인트 없음
							
							//대기장 포인트 사유 가져오기 
							szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD, unloadStoppoint, logId, methodNm);
						}
					}
					
					//**********************************************************************************	
					//1-1-3.차량스케쥴 하차출발(A)로 UPDATE
					szMsg="차량스케쥴 하차출발(A)로 UPDATE   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					jrParam.setField("YD_CAR_PROG_STAT"		, "A"				); //차량진행상태 (A:하차출발)
					jrParam.setField("YD_CAR_USE_GP"		, "L"				); //야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT"		, "L"				); //야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("ARR_WLOC_CD"			, szARR_WLOC_CD		); //착지개소코드
					jrParam.setField("YD_PNT_CD"			, sloadStopTsCd		); //야드포인트코드(착지)
					jrParam.setField("YD_CARUD_STOP_LOC"	, sunloadStoppoint	); //야드하차정지위치
					jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ""				); //야드하차작업예약ID
					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD		); //운송장비코드
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchUdByTrnEqpCd", logId, methodNm, "차량스케쥴 하차출발(A) 업데이트 ");
					

					
				} else if ("E".equals(szTRN_WRK_FULLVOID_GP)) {
					
					//**********************************************************************************	
					//1-2.운송작업영공구분이 E:공차 인 경우 처리
					szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					//**********************************************************************************	
					//1-2-1.상차 대상재 조회 (목적동 찾기)
					if (	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
					||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD) //D3Y42 : B열연-#2 제품/소재 Coil Yard
					) {
						//B열연 COIL 야드인 경우 대상재 조회
						jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtoStl_CoilNewB", logId, methodNm, "B열연 COIL 야드인 경우 대상재 조회");
						if (rsResult.size() > 0) {
							s_STACK_YD_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
							s_STACK_BAY_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));
							s_ARR_WLOC_CD	= commUtils.trim(rsResult.getRecord(0).getFieldString("ARR_WLOC_CD"));
						}
								
					} else {
						//A열연 COIL 야드인 경우 대상재 조회
						jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtoStl_CoilNewA", logId, methodNm, "A열연 COIL 야드인 경우 대상재 조회"); //**주의! A열연,B열연 분리 해야함!!
						if (rsResult.size() > 0) {
							s_STACK_YD_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
							s_STACK_BAY_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));
						}
					}
					
					//**********************************************************************************	
					//1-2-2.상차정지위치 검색
					jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
					jrParam.setField("YD_GP"		, s_STACK_YD_GP);
					jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
					jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint", logId, methodNm, "상차정지위치 검색 - 검색대상재 동에 있는 차량위치정보"); //**주의! A열연,B열연 분리 해야함!!
					
					if (rsResult.size() > 0) {
						
						sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
						sloadStopTsCd 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
						
						szMsg=" 상차포인트 : " + sloadStopTsCd +", 야드상차위치 : " + sunloadStoppoint + " 에 예약";
						commUtils.printLog(logId, szMsg, "SL");
						
						//TB_YM_STACKCOL 예약정보등록 
						jrParam.setField("STACK_STAT"	, "L"); 
						jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
						jrParam.setField("STACK_COL_GP"	, sunloadStoppoint);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
						
						//차량포인트통합관리 - 예약
						this.YmCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
						
					} else {

						szMsg=" 검색결과 >> 포인트를 찾지 못함! '0000'";
						commUtils.printLog(logId, szMsg, "SL");
						
						sloadStopTsCd = "0000"; //포인트 없음
						
						//대기장 포인트 사유 가져오기 
						szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD, unloadStoppoint, logId, methodNm);
					}
					
					if ("AA".equals(sWorkGp)) {
						
						szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//발지 차량정보 삭제처리 
						//this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
						
						EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
						ejbConn1.trx("clearSposCarInfo", new Class[] { String.class,String.class,String.class,String.class,String.class }
										              , new Object[] { szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier });	
						
						if(szREAL_TRN_EQP_CD.equals(szTRN_EQP_CD)) {
							
							JDTORecord jrParam1			= JDTORecordFactory.getInstance().create();
							jrParam1.setResultCode(logId);	//Log ID
							jrParam1.setResultMsg(methodNm);	//Log Method Name
							jrParam1.setField("WLOC_CD"	   , szSPOS_WLOC_CD			); //발지개소코드(상차지)
							jrParam1.setField("YD_PNT_CD"  , szSPOS_YD_PNT_CD		); //발지개소코드(상차지)
							JDTORecordSet rsResult4  = commDao.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk", logId, methodNm, "대상재조회");
							
							if (rsResult4.size() > 0 ) {
								
								sunloadStoppoint = commUtils.trim(rsResult4.getRecord(0).getFieldString("YD_STK_COL_GP"));
							
								/**********************************************************
								* 저장위치제원정보 송신 (YMA8L001) -- 차량출발
								**********************************************************/
								JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
								sndL2Msg.setResultCode(logId);	//Log ID
								sndL2Msg.setResultMsg(methodNm);	//Log Method Name
								sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
								sndL2Msg.setField("STACK_COL_GP"    	, sunloadStoppoint);
								sndL2Msg.setField("STACK_BED_GP"    	, "01");
								sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
								sndL2Msg.setField("YD_CAR_USE_GP"    	, "L"); //L:구내운송, G:출하차량
								sndL2Msg.setField("YD_EQP_WRK_STAT"  	, s_TRN_WRK_FULLVOID_GP); //U:공차, L:영차
								sndL2Msg.setField("TRN_EQP_CD"  		, szTRN_EQP_CD); //운송장비코드
								sndL2Msg.setField("YD_CAR_AIM_YD_GP"	, "3"); 
					 
								//전송 Data 생성
								sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L001_CarInfo", sndL2Msg));
							}
						} else {
					        szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " 가 동일하지 않아서  저장위치제원정보(YMA7L001) 차량출발을 전송하지 않는다! ■ ■ ■ ";
							commUtils.printLog(logId, szMsg, "SL");			
						}
					}
					
					//**********************************************************************************	
					//1-2-3.차량스케쥴 상차출발(1)로 INSERT
					szMsg="차량스케쥴 상차출발(1)로 INSERT   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					jrParam.setField("YD_CAR_SCH_ID"		, commDao.getSeqId(logId, methodNm, "CarSch")); //야드차량스케쥴ID
					jrParam.setField("YD_CAR_PROG_STAT"		, "1"				); //차량진행상태 (1:상차출발)
					jrParam.setField("YD_CAR_USE_GP"		, "L"				); //야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT"		, "U"				); //야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("SPOS_WLOC_CD"			, szARR_WLOC_CD		); //발지개소코드(상차지)
					jrParam.setField("ARR_WLOC_CD"			, s_ARR_WLOC_CD		); //착지개소코드(하차지)
					jrParam.setField("YD_PNT_CD"			, sloadStopTsCd		); //야드상차포인트코드(발지)
					jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ""				); //야드상차작업예약ID
					jrParam.setField("YD_CARLD_STOP_LOC"	, sunloadStoppoint	); //야드하차정지위치
					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD		); //운송장비코드
					jrParam.setField("CAR_KIND"				, szTRN_EQP_GP		); //TR,PT 구분
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchLd", logId, methodNm, "차량스케쥴 상차출발(1)로 INSERT ");
				}
				
				szMsg="포인트 검색결과 : "+sloadStopTsCd + " < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//**********************************************************************************	
				//3. 포인트 지시 전송
				sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD,szARR_WLOC_CD,sloadStopTsCd,szYD_MSG_NM ,s_STACK_BAY_GP ,logId));
				
				if (!"0000".equals(sloadStopTsCd)) {
					//도착할 포인트가 있으면..
					
					//**********************************************************************************	
					//3-1. 포인트점유사항 출하송신 (YDDMR026)
					//  ==> 기존 소스(TsInfRegSBean.procMatlCarArrPntSenddm) 확인 결과 송신하지 않음
					
					//**********************************************************************************	
					//3-2. L2 차량예정정보 전송 --> 전송시점 확인 필요!!
					//sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L008", jrParam));
					
				}
				
			} else if ("AC".equals(sWorkGp)) {
				//**********************************************************************************	
				//2.AB열연에서 일관제철로 이송인 경우
				
				szMsg="AB열연에서 일관제철로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//발지 차량정보 삭제처리 
				//this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
				
				EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
				ejbConn1.trx("clearSposCarInfo", new Class[] { String.class,String.class,String.class,String.class,String.class }
								              , new Object[] { szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier });	
				
				if(szREAL_TRN_EQP_CD.equals(szTRN_EQP_CD)) {
					
					JDTORecord jrParam1			= JDTORecordFactory.getInstance().create();
					jrParam1.setResultCode(logId);	//Log ID
					jrParam1.setResultMsg(methodNm);	//Log Method Name
					jrParam1.setField("WLOC_CD"	   , szSPOS_WLOC_CD			); //발지개소코드(상차지)
					jrParam1.setField("YD_PNT_CD"  , szSPOS_YD_PNT_CD		); //발지개소코드(상차지)
					JDTORecordSet rsResult4  = commDao.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk", logId, methodNm, "대상재조회");
					
					if (rsResult4.size() > 0 ) {
						
						sunloadStoppoint = commUtils.trim(rsResult4.getRecord(0).getFieldString("YD_STK_COL_GP"));
						/**********************************************************
						* 저장위치제원정보 송신 (YMA8L001) -- 차량출발
						**********************************************************/
						JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
						sndL2Msg.setResultCode(logId);	//Log ID
						sndL2Msg.setResultMsg(methodNm);	//Log Method Name
						sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
						sndL2Msg.setField("STACK_COL_GP"    	, sunloadStoppoint);
						sndL2Msg.setField("STACK_BED_GP"    	, "01");
						sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
						sndL2Msg.setField("YD_CAR_USE_GP"    	, "L"); //L:구내운송, G:출하차량
						sndL2Msg.setField("YD_EQP_WRK_STAT"  	, s_TRN_WRK_FULLVOID_GP); //U:공차, L:영차
						sndL2Msg.setField("TRN_EQP_CD"  		, szTRN_EQP_CD); //운송장비코드
						sndL2Msg.setField("YD_CAR_AIM_YD_GP"	, "3"); 
			 
						//전송 Data 생성
						sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L001_CarInfo", sndL2Msg));	
					}
				} else {
					szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " 가 동일하지 않아서  저장위치제원정보(YMA7L001) 차량출발을 전송하지 않는다! ■ ■ ■ ";
					commUtils.printLog(logId, szMsg, "SL");			
				}
			}
			
			
			/********************************************************
			 * 이송차량 영차/공차 출발시 해당 위치대기차량 입동지시 요구
			 ********************************************************/
			String sAPPLY052 = ymComm.BCoilApplyYn("APP052","3","1"); //이송차랑 영차출발시 입동지시호출
			commUtils.printLog(logId,  "이송차랑 출발시 입동지시호출:" + sAPPLY052, "[APP052]");
			
			if ("Y".equals(sAPPLY052)) {
				if ("D3Y41".equals(szSPOS_WLOC_CD) || "D3Y42".equals(szSPOS_WLOC_CD)) {

					/*
					SELECT *
					  FROM TB_YD_CARPOINT
					 WHERE DEL_YN = 'N'
					   AND YD_GP IN('2','3')
					   AND YD_PNT_CD = :V_YD_PNT_CD
					   AND WLOC_CD   = :V_WLOC_CD
					*/
					jrParam.setField("WLOC_CD"	, szSPOS_WLOC_CD  ); //발지개소코드(상차지)
					jrParam.setField("YD_PNT_CD", szSPOS_YD_PNT_CD); //발지포인트코드(상차지)
					JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPntCd", logId, methodNm, "YD_CARPNT_CD 조회");
					
					if (jsRst.size() > 0) {
						JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
						jrMsg.setResultCode(logId);	//Log ID
						jrMsg.setResultMsg(methodNm);	//Log Method Name
						jrMsg.setField("JMS_TC_CD"			, "YMYMJ662");                //차량입동지시 요구 기존:YDYDJ662
						jrMsg.setField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시
						jrMsg.setField("YD_CARPNT_CD"		, jsRst.getRecord(0).getFieldString("YD_CARPNT_CD"));
						
						sndRecord = commUtils.addSndData(sndRecord, jrMsg);
					}
				}
			}
							
			
			commUtils.printLog(logId, methodNm, "SL");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ004_ABCoil()		

	
	/**
	 *      [A] 오퍼레이션명 : 1열연 COIL야드 소재차량출발(TSYDJ004) 수신처리 - 신규로직 (대기장 도착 후 입동 하는 방식)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ004_ABCoil_WL(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "1열연Coil_소재차량출발수신처리WL[YmCommCarMvSeEJB.rcvTSYDJ004_ABCoil_WL] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	
	    String szMsg           		= "";
	    
	    String szTRN_EQP_CD; 
	    String szSPOS_WLOC_CD; 
	    String szSPOS_YD_PNT_CD; 
	    String szARR_WLOC_CD; 
	    String szARR_YD_PNT_CD; 
	    String szTRN_WRK_FULLVOID_GP; 
	    String szTRN_EQP_STK_CAPA; 
	    String sWorkGp;
	    String szTRN_EQP_GP;
	    String msgId;
	    String modifier;
		
	    String szYD_MSG_NM			= "";
	    String s_STL_APPEAR_GP 		= "";
	    String unloadStopwloc		= "";
	    String unloadStoppoint		= "";
		String sloadStopTsCd 		= "";
		String sunloadStoppoint 	= "";
		String s_CAR_SCH_ID			= "";
		String s_STACK_YD_GP 		= "";
		String s_STACK_BAY_GP 		= "";
		String s_ARR_WLOC_CD		= "";
		String s_TRN_WRK_FULLVOID_GP		= "";
	    
	    JDTORecordSet rsResult    	= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= null; //임시  JDTORecord 
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szSPOS_WLOC_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")); 		//발지개소코드
			szSPOS_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")); 		//발지야드포인트코드
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= szTRN_EQP_CD.substring(1, 3); //PT/TR 구분
			
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ004"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			//////////////////////////////////////////////////////////////////////////////////////
			// 2019.08.21  
			// 		구내운송에서 동일한 위치에 다른 차량번호로 출발시적이 발생
			// 		그로 인해서 L2로 잘못 된 출발정보가 전송되지 않도록
			// 		해당 위치에 실재로 존재한는 차량번호를 체크(변수에 저장)하여 L2 전송여부 판단에 사용한다.
			//////////////////////////////////////////////////////////////////////////////////////
			String szREAL_TRN_EQP_CD = "";
			
			jrParam.setField("WLOC_CD"	   , szSPOS_WLOC_CD			); //발지개소코드(상차지)
			jrParam.setField("YD_PNT_CD"   , szSPOS_YD_PNT_CD		); //발지개소코드(상차지)
			JDTORecordSet rsChk  = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk", logId, methodNm, "TB_YD_CARPOINT 에서 점유 중인 차량번호 조회");
			
			if (rsChk.size() > 0 ) {
				
				szREAL_TRN_EQP_CD = commUtils.trim(rsChk.getRecord(0).getFieldString("TRN_EQP_CD"));
			}
			
			szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " ■ ■ ■ ";
			commUtils.printLog(logId, szMsg, "SL");			
			//////////////////////////////////////////////////////////////////////////////////////
			
			//TRN_EQP_CD(운송장비코드)로 차량스케줄을 체크하여 남아있는 스케줄이 있다면 초기화 한다.
			this.initCarSch(rcvMsg);
			
			if ("DMY1P".equals(szARR_WLOC_CD)) {
				//착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제 
				szMsg="착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//차량위치 예정정보 삭제(상하차출발 위치) 정리
				jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리 ");
				
				//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				this.YmCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
			}
			
			if ("E".equals(szTRN_WRK_FULLVOID_GP)) {
				s_TRN_WRK_FULLVOID_GP = "U";
			} else {
				s_TRN_WRK_FULLVOID_GP = "L";
			}
			
			//운송지시 발지,착지개소코드로 방향을 판단한다.(AA:AB열연에서 AB열연으로 이송,AC:AB열연에서 일관제철로 이송,CA:일관제철에서 AB열연으로 이송)
			sWorkGp = commUtils.trim(rcvMsg.getFieldString("WORK_GP"));
			
			if ("AA".equals(sWorkGp)||"CA".equals(sWorkGp)) {
				//**********************************************************************************	
				//1.AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우
				
				szMsg="AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
					
					//**********************************************************************************	
					//1-1.운송작업영공구분이 F:영차 인 경우 처리
					szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					//코일 공통에서 STL_APPEAR_GP를 TS_소재이송지시(MTP)에서 착지개소코드, 착지야드포인트를 조회한다.
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtostlList_CoilAppearGp", logId, methodNm, "코일 공통에서 STL_APPEAR_GP를 TS_소재이송지시(MTP)에서 착지개소코드, 착지야드포인트를 조회");
					
					if (rsResult.size() > 0) {
						
						s_STL_APPEAR_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));
						unloadStopwloc  = commUtils.trim(rsResult.getRecord(0).getFieldString("ARR_WLOC_CD"));   //TS_소재이송지시(MTP) 에서 조회한 것으로 제품일 경우만 사용한다.
						unloadStoppoint = commUtils.trim(rsResult.getRecord(0).getFieldString("ARR_YD_PNT_CD")); //TS_소재이송지시(MTP) 에서 조회한 것으로 제품일 경우만 사용한다. (동 정보)
						
						szMsg=" 검색결과 >> STL_APPEAR_GP : " + s_STL_APPEAR_GP +", ARR_WLOC_CD : " + unloadStopwloc + ", ARR_YD_PNT_CD : " + unloadStoppoint;
						commUtils.printLog(logId, szMsg, "SL");
					}
					
					if ("AA".equals(sWorkGp)) {
						
						szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//발지 차량정보 삭제처리 
						//this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
						
						EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
						ejbConn1.trx("clearSposCarInfo", new Class[] { String.class,String.class,String.class,String.class,String.class }
										              , new Object[] { szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier });	
					}
					
					
					if ("Y".equals(s_STL_APPEAR_GP)) {
						//**********************************************************************************	
						//1-1-1.제품인 경우 처리
						szMsg="운송작업영공구분이 F:영차 이면서 제품인 경우 처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//코일 제품 하차 정지위치 검색 (해당 목적동에 점유하지 않은 포인트 검색)
						jrParam.setField("WLOC_CD"	, unloadStopwloc	); //TS_소재이송지시 (MTP)의 착지개소코드
						jrParam.setField("YD_PNT_CD", unloadStoppoint	); //TS_소재이송지시 (MTP)의 착지야드포인트코드
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppointGD_WL", logId, methodNm, "코일 제품 하차 정지위치 검색"); //
						
						if (rsResult.size() > 0) {
							
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							sloadStopTsCd 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							
							szMsg=" 하차포인트 : " + sloadStopTsCd +", 야드하차위치 : " + sunloadStoppoint + " 에 하차할 예정이지만 carpoint에 예약 하지 않는다! 대기장 도착 후 입동 처리한다.";
							commUtils.printLog(logId, szMsg, "SL");
							
							//TB_YM_STACKCOL 예약정보등록 
							//jrParam.setField("STACK_STAT"	, "L"); 
							//jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
							//jrParam.setField("STACK_COL_GP"	, sunloadStoppoint);
							//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
							
							//차량포인트통합관리 - 예약
							//this.YmCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
							
						} else {

							szMsg=" 검색결과 >> 포인트를 찾지 못함! '0000'";
							commUtils.printLog(logId, szMsg, "SL");
							
							sloadStopTsCd = "0000"; //포인트 없음
							
							//대기장 포인트 사유 가져오기 
							szYD_MSG_NM = this.getCarMsg(s_STL_APPEAR_GP ,"" , "" , szTRN_EQP_GP , unloadStopwloc, unloadStoppoint, logId, methodNm);
						}
						
					} else {
						//**********************************************************************************	
						//1-1-2.소재인 경우 처리
						szMsg="운송작업영공구분이 F:영차 이면서 소재인 경우 처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarSchByTrnEqpCd", logId, methodNm, "차량스케줄 존재여부 체크");
						if (rsResult.size() > 0) {
							s_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
							szMsg=" 검색결과 >> YD_CAR_SCH_ID : " + s_CAR_SCH_ID ;
							commUtils.printLog(logId, szMsg, "SL");
						}
						
						//소재인 경우는 코일공통에 계획공정 및 차공정(우선)으로 목적 동 정보를 구한다.
						if (	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
						||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD) //D3Y42 : B열연-#2 제품/소재 Coil Yard
						) {
			    			//B열연 COIL야드
			    		    s_STACK_YD_GP 	= "3";	
							jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID); //야드차량스케쥴ID
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tsinfo.getListAimBay_BCoil", logId, methodNm, "B열연 COIL야드 소재 하차 이송 목적동 검색");
							if (rsResult.size() > 0) {
						    	s_STACK_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("DEST_BAY")); //목적동
							}
							szMsg=" B열연 COIL야드 소재 하차 이송 목적동 검색결과 >> DEST_BAY : " + s_STACK_BAY_GP ;
							commUtils.printLog(logId, szMsg, "SL");
					    	
				    	} else {
				    		//A열연 COIL야드
			    			s_STACK_YD_GP 	= "1";
							jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID); //야드차량스케쥴ID
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.tsinfo.getListAimBay_ACoil", logId, methodNm, "A열연 COIL야드 소재 하차 이송 목적동 검색");
							if (rsResult.size() > 0) {
						    	s_STACK_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("DEST_BAY")); //목적동
							}
							szMsg=" A열연 COIL야드 소재 하차 이송 목적동 검색결과 >> DEST_BAY : " + s_STACK_BAY_GP ;
							commUtils.printLog(logId, szMsg, "SL");
			    		}			    						    			
			    		
						//-----------------------------------------------------------------------
						//코일 소재 하차 정지위치 검색 (해당 목적동에 점유하지 않은 포인트 검색)
						jrParam.setField("WLOC_CD"	, szARR_WLOC_CD	); 
						jrParam.setField("BAY_GP"	, s_STACK_BAY_GP); 
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppointCM_WL", logId, methodNm, "코일 소재 하차 정지위치 검색"); //**주의! A열연,B열연 분리 해야함!!
						
						if (rsResult.size() > 0) {
							
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							sloadStopTsCd 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							
							szMsg=" 하차포인트 : " + sloadStopTsCd +", 야드하차위치 : " + sunloadStoppoint + " 에 하차할 예정이지만 carpoint에 예약하지 않는다! 대기장 도착 후 입동 차리한다.";
							commUtils.printLog(logId, szMsg, "SL");
							
							//TB_YM_STACKCOL 예약정보등록 
							//jrParam.setField("STACK_STAT"	, "L"); 
							//jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
							//jrParam.setField("STACK_COL_GP"	, sunloadStoppoint);
							//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
							
							//차량포인트통합관리 - 예약
							//this.YmCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
							
						} else {

							szMsg=" 검색결과 >> 포인트를 찾지 못함! '0000'";
							commUtils.printLog(logId, szMsg, "SL");
							
							sloadStopTsCd = "0000"; //포인트 없음
							
							//대기장 포인트 사유 가져오기 
							szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD, unloadStoppoint, logId, methodNm);
						}
					}
					
					//**********************************************************************************	
					//1-1-3.차량스케쥴 하차출발(A)로 UPDATE
					szMsg="차량스케쥴 하차출발(A)로 UPDATE   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					if(!"".equals(sloadStopTsCd)) {
						if(sloadStopTsCd.length() >= 4) {
							sloadStopTsCd = sloadStopTsCd.substring(0,2) + "00";
						}
					}
					
					if(!"".equals(sunloadStoppoint)) {
						if(sunloadStoppoint.length() >= 6) {
							sunloadStoppoint = sunloadStoppoint.substring(0,4) + "00";
						}
					}
					
					jrParam.setField("YD_CAR_PROG_STAT"		, "A"				); //차량진행상태 (A:하차출발)
					jrParam.setField("YD_CAR_USE_GP"		, "L"				); //야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT"		, "L"				); //야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("ARR_WLOC_CD"			, szARR_WLOC_CD		); //착지개소코드
					jrParam.setField("YD_PNT_CD"			, sloadStopTsCd		); //야드포인트코드(착지)
					jrParam.setField("YD_CARUD_STOP_LOC"	, sunloadStoppoint	); //야드하차정지위치
					jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ""				); //야드하차작업예약ID
					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD		); //운송장비코드
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchUdByTrnEqpCd", logId, methodNm, "차량스케쥴 하차출발(A) 업데이트 ");
					

					
				} else if ("E".equals(szTRN_WRK_FULLVOID_GP)) {
					
					//**********************************************************************************	
					//1-2.운송작업영공구분이 E:공차 인 경우 처리
					szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					//**********************************************************************************	
					//1-2-1.상차 대상재 조회 (목적동 찾기)
					if (	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
					||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD) //D3Y42 : B열연-#2 제품/소재 Coil Yard
					) {
						//B열연 COIL 야드인 경우 대상재 조회
						jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtoStl_CoilNewB", logId, methodNm, "B열연 COIL 야드인 경우 대상재 조회");
						if (rsResult.size() > 0) {
							s_STACK_YD_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
							s_STACK_BAY_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));
							s_ARR_WLOC_CD	= commUtils.trim(rsResult.getRecord(0).getFieldString("ARR_WLOC_CD"));
						}
								
					} else {
						//A열연 COIL 야드인 경우 대상재 조회
						jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtoStl_CoilNewA", logId, methodNm, "A열연 COIL 야드인 경우 대상재 조회"); //**주의! A열연,B열연 분리 해야함!!
						if (rsResult.size() > 0) {
							s_STACK_YD_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
							s_STACK_BAY_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));
						}
					}
					
					//**********************************************************************************	
					//1-2-2.상차정지위치 검색
					jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
					jrParam.setField("YD_GP"		, s_STACK_YD_GP);
					jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
					jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint_WL", logId, methodNm, "상차정지위치 검색 - 검색대상재 동에 있는 차량위치정보"); //**주의! A열연,B열연 분리 해야함!!
					
					if (rsResult.size() > 0) {
						
						sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
						sloadStopTsCd 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
						
						szMsg=" 상차포인트 : " + sloadStopTsCd +", 야드상차위치 : " + sunloadStoppoint + " 에서 상차할 예정이지만 carpoint에 예약 하지 않는다! 대기장 도착 후 입동 처리한다.";
						commUtils.printLog(logId, szMsg, "SL");
						
						//TB_YM_STACKCOL 예약정보등록 
						//jrParam.setField("STACK_STAT"	, "L"); 
						//jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
						//jrParam.setField("STACK_COL_GP"	, sunloadStoppoint);
						//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
						
						//차량포인트통합관리 - 예약
						//this.YmCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
						
					} else {

						szMsg=" 검색결과 >> 포인트를 찾지 못함! '0000'";
						commUtils.printLog(logId, szMsg, "SL");
						
						sloadStopTsCd = "0000"; //포인트 없음
						
						//대기장 포인트 사유 가져오기 
						szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD, unloadStoppoint, logId, methodNm);
					}
					
					if ("AA".equals(sWorkGp)) {
						
						szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//발지 차량정보 삭제처리 
						//this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
						
						EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
						ejbConn1.trx("clearSposCarInfo", new Class[] { String.class,String.class,String.class,String.class,String.class }
										              , new Object[] { szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier });	
						
						if(szREAL_TRN_EQP_CD.equals(szTRN_EQP_CD)) {
							
							JDTORecord jrParam1			= JDTORecordFactory.getInstance().create();
							jrParam1.setResultCode(logId);	//Log ID
							jrParam1.setResultMsg(methodNm);	//Log Method Name
							jrParam1.setField("WLOC_CD"	   , szSPOS_WLOC_CD			); //발지개소코드(상차지)
							jrParam1.setField("YD_PNT_CD"  , szSPOS_YD_PNT_CD		); //발지개소코드(상차지)
							JDTORecordSet rsResult4  = commDao.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk", logId, methodNm, "대상재조회");
							
							if (rsResult4.size() > 0 ) {
								
								sunloadStoppoint = commUtils.trim(rsResult4.getRecord(0).getFieldString("YD_STK_COL_GP"));
							
								/**********************************************************
								* 저장위치제원정보 송신 (YMA8L001) -- 차량출발
								**********************************************************/
								JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
								sndL2Msg.setResultCode(logId);	//Log ID
								sndL2Msg.setResultMsg(methodNm);	//Log Method Name
								sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
								sndL2Msg.setField("STACK_COL_GP"    	, sunloadStoppoint);
								sndL2Msg.setField("STACK_BED_GP"    	, "01");
								sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
								sndL2Msg.setField("YD_CAR_USE_GP"    	, "L"); //L:구내운송, G:출하차량
								sndL2Msg.setField("YD_EQP_WRK_STAT"  	, s_TRN_WRK_FULLVOID_GP); //U:공차, L:영차
								sndL2Msg.setField("TRN_EQP_CD"  		, szTRN_EQP_CD); //운송장비코드
								sndL2Msg.setField("YD_CAR_AIM_YD_GP"	, "3"); 
					 
								//전송 Data 생성
								sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L001_CarInfo", sndL2Msg));
							}
						} else {
					        szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " 가 동일하지 않아서  저장위치제원정보(YMA7L001) 차량출발을 전송하지 않는다! ■ ■ ■ ";
							commUtils.printLog(logId, szMsg, "SL");			
						}
					}
					
					//**********************************************************************************	
					//1-2-3.차량스케쥴 상차출발(1)로 INSERT
					szMsg="차량스케쥴 상차출발(1)로 INSERT   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					if(!"".equals(sloadStopTsCd)) {
						if(sloadStopTsCd.length() >= 4) {
							sloadStopTsCd = sloadStopTsCd.substring(0,2) + "00";
						}
					}
					
					if(!"".equals(sunloadStoppoint)) {
						if(sunloadStoppoint.length() >= 6) {
							sunloadStoppoint = sunloadStoppoint.substring(0,4) + "00";
						}
					}

					jrParam.setField("YD_CAR_SCH_ID"		, commDao.getSeqId(logId, methodNm, "CarSch")); //야드차량스케쥴ID
					jrParam.setField("YD_CAR_PROG_STAT"		, "1"				); //차량진행상태 (1:상차출발)
					jrParam.setField("YD_CAR_USE_GP"		, "L"				); //야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT"		, "U"				); //야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("SPOS_WLOC_CD"			, szARR_WLOC_CD		); //발지개소코드(상차지)
					jrParam.setField("ARR_WLOC_CD"			, s_ARR_WLOC_CD		); //착지개소코드(하차지)
					jrParam.setField("YD_PNT_CD"			, sloadStopTsCd		); //야드상차포인트코드(발지)
					jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ""				); //야드상차작업예약ID
					jrParam.setField("YD_CARLD_STOP_LOC"	, sunloadStoppoint	); //야드상차정지위치
					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD		); //운송장비코드
					jrParam.setField("CAR_KIND"				, szTRN_EQP_GP		); //TR,PT 구분
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchLd", logId, methodNm, "차량스케쥴 상차출발(1)로 INSERT ");
				}
				
				szMsg="상차 포인트 검색결과 : "+sloadStopTsCd + " , 포인트지시 YDTSJ011 은 '0000'으로 전송한다! < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//**********************************************************************************	
				//3. 포인트 지시 전송
				sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD,szARR_WLOC_CD, "0000" ,szYD_MSG_NM ,s_STACK_BAY_GP ,logId));
				
				if (!"0000".equals(sloadStopTsCd)) {
					//도착할 포인트가 있으면..
					
					//**********************************************************************************	
					//3-1. 포인트점유사항 출하송신 (YDDMR026)
					//  ==> 기존 소스(TsInfRegSBean.procMatlCarArrPntSenddm) 확인 결과 송신하지 않음
					
					//**********************************************************************************	
					//3-2. L2 차량예정정보 전송 --> 전송시점 확인 필요!!
					//sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L008", jrParam));
					
				}
				
			} else if ("AC".equals(sWorkGp)) {
				//**********************************************************************************	
				//2.AB열연에서 일관제철로 이송인 경우
				
				szMsg="AB열연에서 일관제철로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//발지 차량정보 삭제처리 
				//this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
				
				EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
				ejbConn1.trx("clearSposCarInfo", new Class[] { String.class,String.class,String.class,String.class,String.class }
								              , new Object[] { szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier });	
				
				if(szREAL_TRN_EQP_CD.equals(szTRN_EQP_CD)) {
					
					JDTORecord jrParam1			= JDTORecordFactory.getInstance().create();
					jrParam1.setResultCode(logId);	//Log ID
					jrParam1.setResultMsg(methodNm);	//Log Method Name
					jrParam1.setField("WLOC_CD"	   , szSPOS_WLOC_CD			); //발지개소코드(상차지)
					jrParam1.setField("YD_PNT_CD"  , szSPOS_YD_PNT_CD		); //발지개소코드(상차지)
					JDTORecordSet rsResult4  = commDao.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk", logId, methodNm, "대상재조회");
					
					if (rsResult4.size() > 0 ) {
						
						sunloadStoppoint = commUtils.trim(rsResult4.getRecord(0).getFieldString("YD_STK_COL_GP"));
						/**********************************************************
						* 저장위치제원정보 송신 (YMA8L001) -- 차량출발
						**********************************************************/
						JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
						sndL2Msg.setResultCode(logId);	//Log ID
						sndL2Msg.setResultMsg(methodNm);	//Log Method Name
						sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
						sndL2Msg.setField("STACK_COL_GP"    	, sunloadStoppoint);
						sndL2Msg.setField("STACK_BED_GP"    	, "01");
						sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
						sndL2Msg.setField("YD_CAR_USE_GP"    	, "L"); //L:구내운송, G:출하차량
						sndL2Msg.setField("YD_EQP_WRK_STAT"  	, s_TRN_WRK_FULLVOID_GP); //U:공차, L:영차
						sndL2Msg.setField("TRN_EQP_CD"  		, szTRN_EQP_CD); //운송장비코드
						sndL2Msg.setField("YD_CAR_AIM_YD_GP"	, "3"); 
			 
						//전송 Data 생성
						sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L001_CarInfo", sndL2Msg));	
					}
				} else {
					szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " 가 동일하지 않아서  저장위치제원정보(YMA7L001) 차량출발을 전송하지 않는다! ■ ■ ■ ";
					commUtils.printLog(logId, szMsg, "SL");			
				}
			}
			
			
			/********************************************************
			 * 이송차량 영차/공차 출발시 해당 위치대기차량 입동지시 요구
			 ********************************************************/
			String sAPPLY052 = ymComm.BCoilApplyYn("APP052","3","1"); //이송차랑 영차출발시 입동지시호출
			commUtils.printLog(logId,  "이송차랑 출발시 입동지시호출:" + sAPPLY052, "[APP052]");
			
			if ("Y".equals(sAPPLY052)) {
				if ("D3Y41".equals(szSPOS_WLOC_CD) || "D3Y42".equals(szSPOS_WLOC_CD)) {

					/*
					SELECT *
					  FROM TB_YD_CARPOINT
					 WHERE DEL_YN = 'N'
					   AND YD_GP IN('2','3')
					   AND YD_PNT_CD = :V_YD_PNT_CD
					   AND WLOC_CD   = :V_WLOC_CD
					*/
					jrParam.setField("WLOC_CD"	, szSPOS_WLOC_CD  ); //발지개소코드(상차지)
					jrParam.setField("YD_PNT_CD", szSPOS_YD_PNT_CD); //발지포인트코드(상차지)
					JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPntCd", logId, methodNm, "YD_CARPNT_CD 조회");
					
					if (jsRst.size() > 0) {
						JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
						jrMsg.setResultCode(logId);	//Log ID
						jrMsg.setResultMsg(methodNm);	//Log Method Name
						jrMsg.setField("JMS_TC_CD"			, "YMYMJ662");                //차량입동지시 요구 기존:YDYDJ662
						jrMsg.setField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시
						jrMsg.setField("YD_CARPNT_CD"		, jsRst.getRecord(0).getFieldString("YD_CARPNT_CD"));
						
						sndRecord = commUtils.addSndData(sndRecord, jrMsg);
					}
				}
			}
							
			
			commUtils.printLog(logId, methodNm, "SL");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ004_ABCoil_WL()		
	
	/**
	 *      [A] 오퍼레이션명 : 1열연 SLAB야드 소재차량출발(TSYDJ004) 수신처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ004_ABSlab(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "1열연Slab_소재차량출발수신처리[YmCommCarMvSeEJB.rcvTSYDJ004_ABSlab] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	
	    String szMsg           		= "";
	    
	    String szTRN_EQP_CD; 
	    String szSPOS_WLOC_CD; 
	    String szSPOS_YD_PNT_CD; 
	    String szARR_WLOC_CD; 
	    String szARR_YD_PNT_CD; 
	    String szTRN_WRK_FULLVOID_GP; 
	    String szTRN_EQP_STK_CAPA; 
	    String sWorkGp;
	    String szTRN_EQP_GP;
	    String msgId;
	    String modifier;
		
	    String ydWbookId			= "";
	    String s_CAR_SCH_ID			= "";
	    String s_STACK_YD_GP		= ""; //aimYD_All
	    String s_STACK_BAY_GP		= ""; //aimBay_All
	    String s_STACK_BAY_GP1		= ""; 
	    String s_STACK_BAY_GP2		= ""; 
	    String s_STACK_BAY_GP3		= ""; 
	    String sSchCode				= "";
	    String sunloadStoppoint 	= "";
	    String sunloadStopTsCd		= "";
	    String szYD_MSG_NM			= "";
	    
	    String s_STOCK_ID			= "";
	    String s_C3_SCARF_TRF_YN	= "";
	    String sYD_EQP_WRK_STAT		= "";
	    
	    String inCarspec;
	    String outCarspec;
	    
	    String sMoveterm			= "";
	    String ydSchPrior			= "";
	    
	    JDTORecordSet rsResult    	= null;
	    JDTORecordSet rsResult2    	= null;
	    JDTORecordSet rsResult3    	= null;
	    
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= null; //임시  JDTORecord 
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szSPOS_WLOC_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")); 		//발지개소코드
			szSPOS_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")); 		//발지야드포인트코드
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= szTRN_EQP_CD.substring(1, 3); //PT/TR 구분
			
			msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ004"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
	    	//B-CAST 트레일러 이면서 공차인경우 운송설비구분을  PT 로  변경
			if ("TR".equals(szTRN_EQP_GP) && YmConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD) && "E".equals(szTRN_WRK_FULLVOID_GP)) {
	    		szTRN_EQP_GP = "PT";
	    	}			
			
			//////////////////////////////////////////////////////////////////////////////////////
			// 2019.08.21  
			// 		구내운송에서 동일한 위치에 다른 차량번호로 출발시적이 발생
			// 		그로 인해서 L2로 잘못 된 출발정보가 전송되지 않도록
			// 		해당 위치에 실재로 존재한는 차량번호를 체크(변수에 저장)하여 L2 전송여부 판단에 사용한다.
			//////////////////////////////////////////////////////////////////////////////////////
			String szREAL_TRN_EQP_CD = "";
			
			jrParam.setField("WLOC_CD"	   , szSPOS_WLOC_CD			); //발지개소코드(상차지)
			jrParam.setField("YD_PNT_CD"   , szSPOS_YD_PNT_CD		); //발지개소코드(상차지)
			JDTORecordSet rsChk  = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPointChk", logId, methodNm, "TB_YD_CARPOINT 에서 점유 중인 차량번호 조회");
			
			if (rsChk.size() > 0 ) {
				
				szREAL_TRN_EQP_CD = commUtils.trim(rsChk.getRecord(0).getFieldString("TRN_EQP_CD"));
			}
			
			szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " ■ ■ ■ ";
			commUtils.printLog(logId, szMsg, "SL");			
			//////////////////////////////////////////////////////////////////////////////////////
			
			//TRN_EQP_CD(운송장비코드)로 차량스케줄을 체크하여 남아있는 스케줄이 있다면 초기화 한다.
			this.initCarSch(rcvMsg);
			
			if ("DMY1P".equals(szARR_WLOC_CD)) {
				//착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제 
				szMsg="착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//차량위치 예정정보 삭제(상하차출발 위치) 정리
				jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리 ");
				
				//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				this.YmCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
			}
			
			
			if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
				sYD_EQP_WRK_STAT = "L";//영차
			} else {
				sYD_EQP_WRK_STAT = "U";//공차
			}
			
			//운송지시 발지,착지개소코드로 방향을 판단한다.
			//(AA:AB열연에서 AB열연으로 이송,AC:AB열연에서 일관제철로 이송,CA:일관제철에서 AB열연으로 이송)
			sWorkGp = commUtils.trim(rcvMsg.getFieldString("WORK_GP"));
			
			if ("AA".equals(sWorkGp)||"CA".equals(sWorkGp)) {
				/**********************************************************
				* 1. AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우
				**********************************************************/
				
				szMsg="AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				
				if ("F".equals(szTRN_WRK_FULLVOID_GP)) {
					
					
					/**********************************************************
					* 1-1. 운송작업영공구분이 F:영차 인 경우 처리
					**********************************************************/
					
					szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					//**********************************************************************************	
					// TSYDJ004 전문이 시간차를 두고 두번 발생할 경우 출발실적처리가 2번 실행되는 오류 수정
					jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
					jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
					
					JDTORecordSet loadPointList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint2", logId, methodNm, "장비코드로 포인트 재 요구 시 상차예약정지위치 검색");
					
					if (loadPointList.size() > 0) {
						
						if("R".equals(commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))) {
							//이미 예약이 잡혀 있는 경우 포인트 지시 재송신
							szMsg="["+methodNm+"] 이미 예약이 잡혀 있는 경우   ---> 포인트 지시 재송신";
							commUtils.printLog(logId, szMsg, "SL");
							
							s_STACK_BAY_GP = commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_BAY_GP"));
							sunloadStopTsCd = commUtils.nvl(loadPointList.getRecord(0).getFieldString("YD_PNT_CD"),"0000");
							
						} else if("L".equals(commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))) {
							//이미 도착한 상태인 경우 여기서 종료
							szMsg="["+methodNm+"] 이미 도착한 상태인 경우 여기서 종료 ";
							commUtils.printLog(logId, szMsg, "SL");
							
							commUtils.printLog(logId, methodNm, "S-");
							return sndRecord;
							
						} else {
							
							szMsg="["+methodNm+"] YD_STK_COL_ACT_STAT : " + commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
							commUtils.printLog(logId, szMsg, "SL");
							
							commUtils.printLog(logId, methodNm, "S-");
							return sndRecord;
						}
						
					} else {
						
						//야드구분 지정
						if (YmConstant.WLOC_CD_B_HR_SLAB_YARD.equals(szARR_WLOC_CD)) {
							s_STACK_YD_GP 	= "2";	//B열연 SLAB야드
						} else {
							s_STACK_YD_GP 	= "0";  //b-cast
						}
						
						//운송장비코드로 차량스케줄ID 조회
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtostlList_1", logId, methodNm, "운송장비코드로 차량스케줄ID 조회");
						if (rsResult.size() <= 0) {
							throw new Exception("차량스케줄ID(YD_CAR_SCH_ID) 조회 실패!!!");
						}
						s_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
						
						//이송목적동 조회
						s_STACK_BAY_GP = getSlabAimCd(s_CAR_SCH_ID , szSPOS_WLOC_CD , logId , methodNm);
						
						if ("".equals(s_STACK_BAY_GP)) {
							throw new Exception("이송목적동 조회 실패!!!");
						}
						
						//스케줄코드 편성
						sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02LM"; //SLAB이송하차 
	
						szMsg=" 결과 >> 야드구분 : " + s_STACK_YD_GP +", 차량스케줄ID : " + s_CAR_SCH_ID + ", 이송목적동 : " + s_STACK_BAY_GP + ", 스케줄코드 : " + sSchCode;
						commUtils.printLog(logId, szMsg, "SL");
	
						/**********************************************************
						* 1-1-1. 하차정지위치 검색
						**********************************************************/
						szMsg="하차정지위치 검색  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						if (YmConstant.WLOC_CD_B_HR_SLAB_YARD.equals(szARR_WLOC_CD)) {
							//B열연 SLAB야드(D3Y43)
							jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
							jrParam.setField("YD_GP"		, s_STACK_YD_GP);
							jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
							jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint", logId, methodNm, "B열연 SLAB야드(D3Y43) 하차정지위치 검색 - 이송목적동 차량위치정보");
							
						} else {
							//B-CAST(D2Y43)
							jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
							jrParam.setField("YD_GP"		, s_STACK_YD_GP);
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppointBCAST", logId, methodNm, "B-CAST(D2Y43) 하차정지위치 검색 ");
						}
	
						if (rsResult.size() > 0) {
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
						} else {
							
							if ("PT".equals(szTRN_EQP_GP)) {
								//포인트 모두 점유상태일때 하차완료된 포인트찾음
								jrParam.setField("YD_GP"		, s_STACK_YD_GP);
								jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
								jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
								rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListUnloadEndpoint_slab", logId, methodNm, "포인트 모두 점유상태일때 하차완료된 포인트찾음");
								
								if (rsResult.size() > 0) {
									
									jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD);
									rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListCarSpec", logId, methodNm, "YD_차량사양 테이블에서 운송장비코드로 CAR_NO 조회");
									
									inCarspec = commUtils.trim(rsResult2.getRecord(0).getFieldString("CAR_NO"));
									
									for(int ii = 0 ; ii < rsResult.size() ; ii++) {
										
										outCarspec = commUtils.trim(rsResult.getRecord(ii).getFieldString("CAR_NO"));
										
										if (inCarspec.equals(outCarspec)) {
											sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(ii).getFieldString("STACK_COL_GP"));
											sunloadStopTsCd		= commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_PNT_CD"));
										}
									}
								}
							}
						}
						
						if (rsResult.size() <= 0 || "".equals(sunloadStopTsCd)) {
							
							//포인트찾지 못함
							sunloadStoppoint	= "XXPTXX";
							sunloadStopTsCd		= "0000";
							
							szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD, "", logId, methodNm);
						}
						
						if ("AA".equals(sWorkGp)) {
							
							szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리  < " + methodNm;
							commUtils.printLog(logId, szMsg, "SL");
							
							//발지 차량정보 삭제처리 
							this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
						}
						
						/**********************************************************
						* 1-1-2. 차량이송재료 조회
						**********************************************************/
						szMsg="차량이송재료 조회  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//차량스케쥴ID로 이송재료 조회 (운송장비코드로 차량스케줄ID를 구하고 차량스케줄ID로 차량이송재료를 구한다.)
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListFrtostlList", logId, methodNm, "운송장비코드로 차량스케줄ID를 구하고 차량스케줄ID로 차량이송재료를 조회");
						
						if (rsResult.size() <= 0) {
							throw new Exception("차량이송재료 조회 실패!!!");
						}
						
						
						for(int ii = 0 ; ii < rsResult.size() ; ii++) {
							//검색된 이송재료 1건씩 읽어서 아래  처리
							
							s_STOCK_ID			= commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO"));
							s_C3_SCARF_TRF_YN	= commUtils.trim(rsResult.getRecord(ii).getFieldString("C3_SCARF_TRF_YN")); //CA 항만스카핑 핸드구분
						
							if ("CA".equals(sWorkGp) && "Y".equals(s_C3_SCARF_TRF_YN)) {
								//일관제철에서 AB열연으로 이송인 경우이면서 C3_SCARF_TRF_YN 이 'Y'인 경우
								sMoveterm = YmConstant.NEW_STOCK_MOVE_TERM_D3; //핸드스카핑작업대기
							} else {
								sMoveterm = YmConstant.NEW_STOCK_MOVE_TERM_CS; //이송대기
								
								//다음 재료진도와 지시주편손질방법을 조회하여 이동조건을 구한다.
								jrParam.setField("SLAB_NO", s_STOCK_ID);
								/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getNextProcCd
								SELECT (
								          case when C.STL_APPEAR_GP = 'B' then DECODE(substr(C.SLAB_NO,1,1), 'G', ( case when C.SCARFING_YN = 'Y' then 'A' ELSE (case when C.ORD_YEOJAE_GP = '1' then 'A' else 'Y' end) END),'A')
								               else (case when C.SLAB_WO_RT_CD = 'MS' then (case when C.STL_APPEAR_GP = 'Y' then DECODE(C.ORD_YEOJAE_GP,'1','K','Z')
								                                                                 else DECODE(C.ORD_YEOJAE_GP,'1','A','Y') end
								                                                           )
								                          else DECODE(C.ORD_YEOJAE_GP, '1', (case when (C.SCARFING_YN = 'Y' AND C.SCARFING_DONE_YN = 'N') then 'A'
								                                                                  when H.CT_MILL_SPEC_WRK_STAT_GP = '*' then C.CURR_PROG_CD
								                                                                  when TO_NUMBER(H.CT_MILL_SPEC_WRK_STAT_GP) < 3 then 'B'
								                                                                  ELSE 'C'
								                                                             END), '2', 'Y')
								                     end)
								          end
								       ) AS NEXT_PROC_CD --다음재료진도
								      ,C.WO_MSLAB_RPR_MTD --SCARFING PATTERN  
								FROM   VW_YD_SLABCOMM C
								      ,TB_CT_K_HRMILLSPEC H 
								      
								WHERE  C.SLAB_NO = :V_SLAB_NO
								  AND  C.SLAB_NO = H.STL_NO(+)  */
							    rsResult3 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getNextProcCd", logId, methodNm, "다음 재료진도와 지시주편손질방법을 조회");
							    if (rsResult3.size() > 0) {
							    	sMoveterm = bSlabComm.getStockMoveTerm( rsResult3.getRecord(0).getFieldString("NEXT_PROC_CD")
							    			                               ,rsResult3.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD"));
							    }	
							}
							
							jrParam.setField("STOCK_ID"	, s_STOCK_ID);
							rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStockInfoWcrGp", logId, methodNm, "TB_YM_STOCK에 존재하는지 체크");
	
							if (rsResult2.size() <= 0) {
								//TB_YM_STOCK 에 STOCK_ID가 존재하지 않을 경우..
								
								/**********************************************************
								* 1-1-2-1. TB_YM_STOCK에 STOCK_ID를 신규생성
								**********************************************************/
								jrParam.setField("STOCK_ID"			, s_STOCK_ID );
								jrParam.setField("STOCK_MOVE_TERM"	, sMoveterm  ); //저장품 이동 조건 
								jrParam.setField("MODIFIER"			, modifier 	 );
								
								commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insStockInfo", logId, methodNm, "TB_YM_STOCK에 STOCK_ID를 신규생성");
								
								/**********************************************************
								* 1-1-2-2. 저장품제원정보(YMA8L002)  L2 송신
								**********************************************************/
								jrParam.setField("STOCK_ID"			, s_STOCK_ID ); //재료번호(SLAB번호)
								jrParam.setField("MSG_GP"			, "I"		 ); //정보구분(I:신규)
								jrParam.setField("YD_INFO_SYNC_CD"	, "A"		 ); //야드정보동기화코드(A:생산실적)
					
								sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L002", jrParam));
								
							} else {
								//TB_YM_STOCK 에 STOCK_ID가 존재하는 경우..
								
								//작업예약재료가 존재하면 에러처리
								if (!"".equals(commUtils.trim(rsResult2.getRecord(0).getFieldString("YD_WBOOK_ID")))) {
									throw new Exception("STOCK_ID : " + s_STOCK_ID + " 가 작업예약에 이미 잡혀 있습니다!!! YD_WBOOK_ID : "
											+ rsResult2.getRecord(0).getFieldString("YD_WBOOK_ID")
											);
								}
								
								/**********************************************************
								* 1-1-2-3. TB_YM_STOCK의 STL_NO에 저장품 이동 조건 갱신
								**********************************************************/
								jrParam.setField("STOCK_ID"			, s_STOCK_ID );
								jrParam.setField("STOCK_MOVE_TERM"	, sMoveterm  ); //저장품 이동 조건 
								jrParam.setField("MODIFIER"			, modifier   );
								
								commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 STOCK_ID에 저장품 이동 조건 갱신");
							}
							
							
						} //end of for(int ii = 0...
						
						
						/**********************************************************
						* 1-1-4.  차량스케쥴 하차출발(A)로 UPDATE
						**********************************************************/
						szMsg="차량스케쥴 하차출발(A)로 UPDATE < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						jrParam.setField("YD_CAR_PROG_STAT"		, "A"				); //차량진행상태 (A:하차출발)
						jrParam.setField("YD_CAR_USE_GP"		, "L"				); //야드차량사용구분 (L:구내운송, G:출하차량 )
						jrParam.setField("YD_EQP_WRK_STAT"		, "L"				); //야드설비작업상태 (L:영차, U:공차)
						jrParam.setField("ARR_WLOC_CD"			, szARR_WLOC_CD		); //착지개소코드
						jrParam.setField("YD_PNT_CD"			, sunloadStopTsCd	); //야드포인트코드(착지)
						jrParam.setField("YD_CARUD_STOP_LOC"	, sunloadStoppoint	); //야드하차정지위치
						jrParam.setField("YD_CARUD_WRK_BOOK_ID"	, ydWbookId			); //야드하차작업예약ID
						jrParam.setField("WAIT_ARR_GP"			, s_STACK_BAY_GP	); //이송목적동 ** 정종균과장 2017.4.14 추가 
						jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD		); //운송장비코드
						                         
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchUdByTrnEqpCdNew", logId, methodNm, "차량스케쥴 하차출발(A) 업데이트 ");
						
						
						if (!"0000".equals(sunloadStopTsCd)) {
							
							/**********************************************************
							* 1-1-5. 하차정지위치에 예약하기
							**********************************************************/
							szMsg=" 하차포인트 : " + sunloadStopTsCd +", 야드하차위치 : " + sunloadStoppoint + " 에 예약 < " + methodNm;
							commUtils.printLog(logId, szMsg, "SL");
							
							//TB_YM_STACKCOL 예약정보등록 
							jrParam.setField("STACK_STAT"	, "L"); 
							jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
							jrParam.setField("STACK_COL_GP"	, sunloadStoppoint);
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat        
							UPDATE TB_YM_STACKCOL
							   SET STACK_STAT = :V_STACK_STAT
							      ,CAR_CARD_NO = :V_CAR_CARD_NO
							      ,MODIFIER = :V_MODIFIER
							      ,MOD_DDTT = SYSDATE
							 WHERE STACK_COL_GP = :V_STACK_COL_GP */
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
							
							//차량포인트통합관리 - 예약
							this.YmCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
						}
					}
				
				} else if ("E".equals(szTRN_WRK_FULLVOID_GP)) {
					
					/**********************************************************
					* 1-2. 운송작업영공구분이 E:공차 인 경우 처리
					**********************************************************/
					
					szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					//도착개소코들별 목적동 검색
					if (YmConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD)) {
						//B Cast Slab Yard (D2Y43)
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListAimbayBCast", logId, methodNm, "파레트 도착 목적동검색");
						if (rsResult.size() > 0) {
							s_STACK_YD_GP = "0"; //B Cast Slab Yard
							s_STACK_BAY_GP1 = commUtils.trim(rsResult.getRecord(0).getFieldString("AIM_BAY"));
							szMsg="["+methodNm+"] BCast 공차 목적동 검색결과 : " + s_STACK_BAY_GP;
							commUtils.printLog(logId, szMsg, "SL");
						}
						
					} else if (YmConstant.WLOC_CD_B_HR_SLAB_YARD.equals(szARR_WLOC_CD)) {
						//B열연-Slab Yard (D3Y43)
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListAimbayBSlab", logId, methodNm, "파레트 도착 목적동검색");
						if (rsResult.size() > 0) {
							s_STACK_YD_GP = "2"; //B열연 SLAB야드
							s_STACK_BAY_GP1 = commUtils.trim(rsResult.getRecord(0).getFieldString("AIM_BAY"));
							s_STACK_BAY_GP2 = commUtils.trim(rsResult.getRecord(1).getFieldString("AIM_BAY"));
							s_STACK_BAY_GP3 = commUtils.trim(rsResult.getRecord(2).getFieldString("AIM_BAY"));
							szMsg="["+methodNm+"] BSLAB 공차 목적동 검색결과  1순위:" + s_STACK_BAY_GP1 + ",2순위:" + s_STACK_BAY_GP2 + ",3순위:" + s_STACK_BAY_GP3;
							commUtils.printLog(logId, szMsg, "SL");
						}
					}
					
					//이송지시대상재 조회
					jrParam.setField("SPOS_WLOC_CD"	, szARR_WLOC_CD);
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListFrtoStl_ASlab
					--이송지시대상제조회
					SELECT  (
					            SELECT SCH_RULE_VAL
					              FROM TB_YM_STACKPRIORITY
					             WHERE RULE_ID = 'YM05'
					              AND  B.ARR_WLOC_CD = SCH_CD ) AS WLOC_RANK
					       ,(
					            SELECT SCH_RULE_VAL
					              FROM TB_YM_STACKPRIORITY
					             WHERE RULE_ID = 'YM04'
					               AND SUBSTR(A.STACK_COL_GP, 2, 1) = SCH_CD ) AS BAY_RANK
					       ,(
					            SELECT SCH_RULE_VAL
					              FROM TB_YM_STACKPRIORITY
					             WHERE RULE_ID = 'YM03'
					               AND C.SCARFING_YN = SCH_CD ) AS SCARFING_RANK
					       ,SUBSTR(A.STACK_COL_GP, 2, 1) AS AIM_BAY
					       ,A.STOCK_ID
					       ,A.STACK_COL_GP
					       ,A.STACK_BED_GP
					       ,A.STACK_LAYER_GP
					       ,B.SPOS_WLOC_CD -- 발지개소
					       ,B.ARR_WLOC_CD -- 착지개소
					  FROM  TB_YM_STACKLAYER A
					       ,TB_TS_MATL_FTMV_WO B
					       ,VW_YD_SLABCOMM C
					 WHERE A.STOCK_ID = B.STL_NO
					   AND A.STOCK_ID = C.SLAB_NO
					   AND A.STACK_LAYER_STAT = 'C'
					   AND ((SUBSTR(A.STACK_COL_GP,3,2)<>'PT') AND (SUBSTR(A.STACK_COL_GP,3,2)<>'TR'))
					   AND B.SPOS_WLOC_CD = :V_SPOS_WLOC_CD
					   AND B.TS_MATL_FTMV_STAT_GP = '1'
					   AND B.MATL_FTMV_WO_NML_HD_YN = 'Y'
					   AND (
					           (B.SPOS_WLOC_CD ='D3Y43' AND SUBSTR(A.STACK_COL_GP, 2, 1) IN ('A','D','E')) 
					        OR (B.SPOS_WLOC_CD ='D2Y43' AND SUBSTR(A.STACK_COL_GP, 2, 1) IN('A','B') )
					       )
					 ORDER BY WLOC_RANK, BAY_RANK, SCARFING_RANK, A.STACK_COL_GP, A.STACK_BED_GP, A.STACK_LAYER_GP DESC */
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListFrtoStl_ASlab", logId, methodNm, "대상재조회");
					
					if (rsResult.size() > 0) {
						
						szMsg="야드에 적치된 이송지시대상재가 존재하는 경우 < E:공차 처리 < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						s_STACK_BAY_GP =  commUtils.trim(rsResult.getRecord(0).getFieldString("AIM_BAY")); //대상이 있는 목적동
						
						//상차정지위치 검색
						jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
						jrParam.setField("YD_GP"		, s_STACK_YD_GP);
						jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
						jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint", logId, methodNm, "B열연 SLAB야드 상차정지위치 조회");
						
						if (rsResult.size() > 0) {
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
						} else {
							//포인트 모두 점유상태일때 상차완료된 포인트찾음
							jrParam.setField("YD_GP"		, s_STACK_YD_GP);
							jrParam.setField("BAY_GP"		, s_STACK_BAY_GP);
							jrParam.setField("SECT_GP "		, "PT");
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadEndpoint", logId, methodNm, "포인트 모두 점유상태일때 상차완료된 포인트찾음");
							
							if (rsResult.size() > 0) {
								sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
								sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							}
						}
							
						
						if (rsResult.size() <= 0 || "".equals(sunloadStopTsCd)) {
							
							//포인트찾지 못함
							sunloadStoppoint	= "XXPTXX";
							sunloadStopTsCd		= "0000";
							
							szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD, "", logId, methodNm);
						}
						
						
					} else if (rsResult.size() == 0) {
						
						szMsg="야드에 적치된 이송지시대상재가 없을 경우 < E:공차 처리 < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//이송상차 동지정 1순위 상차정지위치 검색
						s_STACK_BAY_GP = s_STACK_BAY_GP1;
						jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
						jrParam.setField("YD_GP"		, s_STACK_YD_GP);
						jrParam.setField("BAY_GP"		, s_STACK_BAY_GP1);
						jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint", logId, methodNm, "B열연 SLAB야드 상차정지위치 조회");
						
						if (rsResult.size() > 0) {
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
							sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
						} else {
							//포인트 모두 점유상태일때 상차완료된 포인트찾음
							jrParam.setField("YD_GP"		, s_STACK_YD_GP);
							jrParam.setField("BAY_GP"		, s_STACK_BAY_GP1);
							jrParam.setField("SECT_GP "		, "PT");
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadEndpoint", logId, methodNm, "포인트 모두 점유상태일때 상차완료된 포인트찾음");
							
							if (rsResult.size() > 0) {
								sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
								sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							}
						}
						
						if ("".equals(sunloadStopTsCd)) {
							
							//이송상차 동지정 2순위 상차정지위치 검색
							s_STACK_BAY_GP = s_STACK_BAY_GP2;
							jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
							jrParam.setField("YD_GP"		, s_STACK_YD_GP);
							jrParam.setField("BAY_GP"		, s_STACK_BAY_GP2);
							jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint", logId, methodNm, "B열연 SLAB야드 상차정지위치 조회");
							
							if (rsResult.size() > 0) {
								sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
								sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							} else {
								//포인트 모두 점유상태일때 상차완료된 포인트찾음
								jrParam.setField("YD_GP"		, s_STACK_YD_GP);
								jrParam.setField("BAY_GP"		, s_STACK_BAY_GP2);
								jrParam.setField("SECT_GP "		, "PT");
								rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadEndpoint", logId, methodNm, "포인트 모두 점유상태일때 상차완료된 포인트찾음");
								
								if (rsResult.size() > 0) {
									sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
									sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
								}
							}
						}
						
						if ("".equals(sunloadStopTsCd)) {
							
							//이송상차 동지정 3순위 상차정지위치 검색
							s_STACK_BAY_GP = s_STACK_BAY_GP3;
							jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
							jrParam.setField("YD_GP"		, s_STACK_YD_GP);
							jrParam.setField("BAY_GP"		, s_STACK_BAY_GP3);
							jrParam.setField("TRN_EQP_GP"	, szTRN_EQP_GP);
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadStoppoint", logId, methodNm, "B열연 SLAB야드 상차정지위치 조회");
							
							if (rsResult.size() > 0) {
								sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
								sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							} else {
								//포인트 모두 점유상태일때 상차완료된 포인트찾음
								jrParam.setField("YD_GP"		, s_STACK_YD_GP);
								jrParam.setField("BAY_GP"		, s_STACK_BAY_GP3);
								jrParam.setField("SECT_GP "		, "PT");
								rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListloadEndpoint", logId, methodNm, "포인트 모두 점유상태일때 상차완료된 포인트찾음");
								
								if (rsResult.size() > 0) {
									sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP"));
									sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
								}
							}
						}
						
						if (rsResult.size() <= 0 || "".equals(sunloadStopTsCd)) {
							
							//포인트찾지 못함
							sunloadStoppoint	= "XXPTXX";
							sunloadStopTsCd		= "0000";
							
							szYD_MSG_NM = this.getCarMsg("N" ,s_STACK_YD_GP , s_STACK_BAY_GP , szTRN_EQP_GP , szARR_WLOC_CD, "", logId, methodNm);
						}
						
					}
					
					if ("AA".equals(sWorkGp)) {
						
						szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//발지 차량정보 삭제처리 
						this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
					}
					
					szMsg=" 상차포인트 : " + sunloadStopTsCd +", 야드상차위치 : " + sunloadStoppoint + " 에 예약";
					commUtils.printLog(logId, szMsg, "SL");
					
					//TB_YM_STACKCOL 예약정보등록 
					jrParam.setField("STACK_STAT"	, "L"); 
					jrParam.setField("CAR_CARD_NO"	, szTRN_EQP_CD);
					jrParam.setField("STACK_COL_GP"	, sunloadStoppoint);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
					
					//차량포인트통합관리 - 예약
					this.YmCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
					
					//**********************************************************************************	
					//1-2-3.차량스케쥴 상차출발(1)로 INSERT
					szMsg="차량스케쥴 상차출발(1)로 INSERT   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					
					jrParam.setField("YD_CAR_SCH_ID"		, commDao.getSeqId(logId, methodNm, "CarSch")); //야드차량스케쥴ID
					jrParam.setField("YD_CAR_PROG_STAT"		, "1"				); //차량진행상태 (1:상차출발)
					jrParam.setField("YD_CAR_USE_GP"		, "L"				); //야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT"		, "U"				); //야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("SPOS_WLOC_CD"			, szARR_WLOC_CD		); //발지개소코드(상차지)
					jrParam.setField("ARR_WLOC_CD"			, ""				); //착지개소코드(하차지)
					jrParam.setField("YD_PNT_CD"			, sunloadStopTsCd	); //야드상차포인트코드(발지)
					jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ydWbookId			); //야드상차작업예약ID
					jrParam.setField("YD_CARLD_STOP_LOC"	, sunloadStoppoint	); //야드하차정지위치
					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD		); //운송장비코드
					jrParam.setField("CAR_KIND"				, szTRN_EQP_GP		); //TR,PT 구분
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchLd", logId, methodNm, "차량스케쥴 상차출발(1)로 INSERT ");
					
				}

				szMsg="포인트 검색결과 : "+sunloadStopTsCd + " < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//**********************************************************************************	
				//3. 포인트 지시 전송
				sndRecord = commUtils.addSndData(sndRecord, this.makeYDTSJ011(szTRN_EQP_CD,szARR_WLOC_CD,sunloadStopTsCd,szYD_MSG_NM ,s_STACK_BAY_GP ,logId));
				
			} else if ("AC".equals(sWorkGp)) {
				/**********************************************************
				* 2. AB열연에서 일관제철로 이송인 경우
				**********************************************************/
				
				szMsg="AB열연에서 일관제철로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//발지 차량정보 삭제처리 
				this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
				
				if(szREAL_TRN_EQP_CD.equals(szTRN_EQP_CD)) {
				
					sunloadStoppoint = "2" + szSPOS_YD_PNT_CD.substring(1,2) + "PT" + szSPOS_YD_PNT_CD.substring(2,4);
					
					/**********************************************************
					* 저장위치제원정보 송신 (YMA8L001) -- 차량출발
					**********************************************************/
					JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setResultCode(logId);	//Log ID
					sndL2Msg.setResultMsg(methodNm);	//Log Method Name
					sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
					sndL2Msg.setField("STACK_COL_GP"    	, sunloadStoppoint);
					sndL2Msg.setField("STACK_BED_GP"    	, "01");
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
					sndL2Msg.setField("YD_CAR_USE_GP"    	, "L"); //L:구내운송, G:출하차량
					sndL2Msg.setField("YD_EQP_WRK_STAT"  	, sYD_EQP_WRK_STAT); //U:공차, L:영차
					sndL2Msg.setField("TRN_EQP_CD"  		, szTRN_EQP_CD); //운송장비코드
					sndL2Msg.setField("YD_CAR_AIM_YD_GP"	, "2"); 
		 
					//전송 Data 생성
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L001_CarInfo", sndL2Msg));	
				} else {
			        szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " 가 동일하지 않아서  저장위치제원정보(YMA8L001) 차량출발을 전송하지 않는다! ■ ■ ■ ";
					commUtils.printLog(logId, szMsg, "SL");			
				}
				
			}
			
			
			commUtils.printLog(logId, methodNm, "SL");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ004_ABSlab()		
	
	/**
	 *      [A] 오퍼레이션명 : 대기장도착(TSYDJ005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ005(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "대기장도착[YmCommCarMvSeEJB.rcvTSYDJ005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
		
		//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		String msgId = commUtils.getMsgId(rcvMsg); 
        if (msgId==null || msgId.equals("")) {
        	return sndRecord;
        }
        
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "소재차량도착 수신 ", rcvMsg);
			
	    	//수신항목 변수 저장
			String szARR_WLOC_CD 	= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 	//착지개소코드
				
			
			if (	YmConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD) //D2Y44 : A열연-#1 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)	//D2Y45 : A열연-#2 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y41 : B열연-#1 제품/소재 Coil Yard
			||	YmConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)	//D3Y42 : B열연-#2 제품/소재 Coil Yard
			) {
				String  sAPP055_YN = ymComm.BCoilApplyYn("APP055","3","1"); //구내운송차량 대기장 도착 후 입동지시 여부
				
				if("Y".equals(sAPP055_YN))  {
					//1열연 Coil 야드 신규 도착처리 (대기장 도착 후 입동하는 방식)
					sndRecord = this.rcvTSYDJ005_ABCoil_WL(rcvMsg);
				} else {
				}
				
			} else {
				
				//1열연 Slab 야드인 경우
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ005()	
	
	/**
	 *      [A] 오퍼레이션명 : rcvTSYDJ005_ABCoil_WL(TSYDJ005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ005_ABCoil_WL(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "대기장도착[YmCommCarMvSeEJB.rcvTSYDJ005_ABCoil_WL] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
	    
		JDTORecord jrRtn		= JDTORecordFactory.getInstance().create();
		
	    String		szTRN_EQP_CD;
	    String		szARR_WLOC_CD;
	    String		szARR_YD_PNT_CD;
	    String		szTRN_WRK_FULLVOID_GP;
	    String		szTRN_EQP_STK_CAPA;
	    String		szMGS_GP;
	    String		szTRN_EQP_GP;
	    String		svTRN_EQP_GP;
	    String		s_YD_EQP_WRK_STAT	= "";

	    String		sStkClo				= "";
	    String		sYdPntCd			= "0000";
	    String		s_STACK_YD_GP		= "";
	    String		s_STACK_BAY_GP		= "";
	    String		s_STACK_SAPN_GP		= "";
	    String		szMsg				= "";
		
		JDTORecordSet rsResult	= null;
	    JDTORecordSet rsResult2	= null;

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYFJ005");	//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============소재차량 대기장도착 시작========", "SL");

	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드...구내운송에서 보내주는 값이 의미가 없어서 안쓰임
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szMGS_GP				= commUtils.trim(rcvMsg.getFieldString("MGS_GP")); 				//I(신규), U(수정), D(취소,삭제), R(재 전송)


	    	//개소코드가 박판열연 개소코드인지 검사한다.
	    	if (!getABLocationInfo_02(szARR_WLOC_CD))
	    	{
				throw new Exception("개소코드 오류 [" + szARR_WLOC_CD + "]는 AB열연 개소코드가 아닙니다!");
			}

			if ("F".equals(szTRN_WRK_FULLVOID_GP))	//TRN_WRK_FULLVOID_GP(운송작업영공구분) F:영차 / E:공차
			{
				//1.운송작업영공구분이 F:영차 인 경우 처리
				szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//실제 도착위치 업데이트 및 영차도착시 맵정보 생성을 위해
				//개소코드와 야드포인트 코드로 적치열구분 조회 야드구분, 동 정보를 구한다.
				jrParam.setField("WLOC_CD",				szARR_WLOC_CD);
				jrParam.setField("TRN_EQP_CD",			szTRN_EQP_CD);
				jrParam.setField("TRN_WRK_FULLVOID_GP",	szTRN_WRK_FULLVOID_GP);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStkColgp2", logId, methodNm, "TB_YM_STKCOL 개소코드와 야드포인트 코드로 적치열구분 조회");

//				if (rsResult.size() <= 0)
//				{
//					throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 운송장비코드 [" + szTRN_EQP_CD + "] 로 TB_YD_CARPOINT 조회 결과 : " + rsResult.size());
//				}
				
				commUtils.printLog(logId, "착지개소코드 [" + szARR_WLOC_CD + "], 운송장비코드 [" + szTRN_EQP_CD + "] 로 TB_YD_CARPOINT 조회 결과 : " + rsResult.size(), "SL");

				if (rsResult.size() > 0)
				{
					sStkClo			= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
					sYdPntCd		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
				}

				//이송재료 가 STOCK에 존재하지 않으면 STOCK을 생성한다.
				jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIsExistStock", logId, methodNm, "TB_YD_CARFTMVMTL 이송재료 가 TB_YM_STOCK에 존재하는지 check");

				if (rsResult.size() > 0)
				{
					String sCoilNo = "";
					String sStockMv = "";

					for(int ii= 0; ii < rsResult.size(); ii++)
					{
						if ("".equals(commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID"))))
						{
							sCoilNo = commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO"));

							//코일공통의 차공정코드로 이동조건 설정, 리턴 값이 "" 이면 코일공통에 존재하지 않는 코일 번호로 Error 처리 한다.
							sStockMv = ymComm.getStockMv(logId, methodNm, sCoilNo);

							//TB_YF_STOCK에 존재 한지 않으면 생성한다.
							jrParam.setField("STOCK_ID"			, sCoilNo);
							jrParam.setField("STOCK_ITEM"		, YmConstant.ITEM_CM);
							jrParam.setField("STOCK_MOVE_TERM"	, sStockMv);
							commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock", logId, methodNm, "TB_YM_STOCK 재료 생성");
						}
					}
				}
				
				//**********************************************************************************
				szMsg="차량스케쥴 대기장도착으로 만들기위해 UPDATE 야드포인트 하차정지위치 등의 정보를 등록< " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				jrParam.setField("YD_CAR_PROG_STAT",		"A");				//차량진행상태 (A:하차출발)
				jrParam.setField("YD_CAR_USE_GP",			"L");				//야드차량사용구분 (L:구내운송, G:출하차량 )
				jrParam.setField("YD_EQP_WRK_STAT",			"L");				//야드설비작업상태 (L:영차, U:공차)
				jrParam.setField("ARR_WLOC_CD",				szARR_WLOC_CD);		//착지개소코드
				jrParam.setField("YD_PNT_CD",				sYdPntCd);			//야드포인트코드(착지)
				jrParam.setField("YD_CARUD_STOP_LOC",		sStkClo);			//야드하차정지위치
				jrParam.setField("YD_CARUD_WRK_BOOK_ID",	"");				//야드하차작업예약ID
				jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchUdByTrnEqpCd", logId, methodNm, "차량스케쥴 하차출발(A)업데이트 및 대기장도착을 위해 야드포인트 및 하차정지위치 업데이트 ");
				
			}
			else if ("E".equals(szTRN_WRK_FULLVOID_GP))
			{
				//2.운송작업영공구분이 E:공차 인 경우 처리
				szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				//박판창고 야드인 경우 냉연대상재 조회
				jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
				jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
				jrParam.setField("TRN_WRK_FULLVOID_GP",	szTRN_WRK_FULLVOID_GP);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListStkColgp2", logId, methodNm, "TB_YM_STKCOL 개소코드와 야드포인트 코드로 적치열구분 조회");	
				
				if (rsResult.size() <= 0)
				{
					throw new Exception("상차지개소코드 [" + szARR_WLOC_CD + "], 운송장비코드 [" + szTRN_EQP_CD + "] 로 TB_YD_CARPOINT 조회 결과 : " + rsResult.size());
				}
				
				sStkClo			= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
				sYdPntCd		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
				
				szMsg="차량스케쥴 대기장도착으로 만들기위해 UPDATE 야드포인트 상차정지위치 등의 정보를 등록< " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");
				
				jrParam.setField("YD_CAR_PROG_STAT",		"1");				//차량진행상태 (1:상차출발)
				jrParam.setField("YD_CAR_USE_GP",			"L");				//야드차량사용구분 (L:구내운송, G:출하차량 )
				jrParam.setField("YD_EQP_WRK_STAT",			"U");				//야드설비작업상태 (L:영차, U:공차)
				jrParam.setField("SPOS_WLOC_CD",			szARR_WLOC_CD);		//발지개소코드
				jrParam.setField("YD_PNT_CD",				sYdPntCd);			//야드포인트코드(상차지)
				jrParam.setField("YD_CARLD_STOP_LOC",		sStkClo);			//야드상차정지위치
				jrParam.setField("YD_CARLD_WRK_BOOK_ID",	"");				//야드상차작업예약ID
				jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchUdByTrnEqpCd2", logId, methodNm, "차량스케쥴 상차출발(1) 업데이트  및 대기장도착을 위해 야드포인트 및 상차정지위치 업데이트");
				
			}
			
			//대기장도착처리 후 포인트 지시를 위해 TSYDJ002호출
			JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
			jrMsg.setResultCode(logId);		//Log ID
			jrMsg.setResultMsg(methodNm);	//Log Method Name
			jrMsg.setField("JMS_TC_CD",				"TSYDJ002");                //차량입동지시 요구 기존:YFYFJ662
			jrMsg.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());	//JMSTC생성일시
			jrMsg.setField("TRN_EQP_CD",			szTRN_EQP_CD);
			jrMsg.setField("WLOC_CD",				szARR_WLOC_CD);
			jrMsg.setField("TRN_WRK_FULLVOID_GP",	szTRN_WRK_FULLVOID_GP);

			jrRtn = commUtils.addSndData(jrRtn, jrMsg);

			commUtils.printLog(logId, "=============소재차량 대기장도착_종료========", "SL");

			commUtils.printLog(logId, methodNm, "SL");

			return jrRtn;
		}
	    catch (DAOException e)
	    {
			throw e;
		}
	    catch (Exception e)
	    {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvTSYDJ005_ABCoil_WL()		
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량출발취소(TSYDJ014) 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvTSYDJ014(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "소재차량출발취소[YmCommCarMvSeEJB.rcvTSYDJ014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
	    String szMsg           		= "";
	    
	    String szTRN_EQP_CD			 = ""; //운송장비코드
	    //String szTRN_WRK_FULLVOID_GP = ""; //운송작업영공구분[상하차구분 E:공차상태(상차작업),F:영차(하차작업)]	
        String modifier;
	
	    JDTORecordSet rsResult    	= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
	    
        String msgId = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
        
        if (msgId == null || msgId.equals("")) {
        	return sndRecord;
        }

	    try{

			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "소재차량출발취소[TSYDJ014] 수신 ", rcvMsg);
			
	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); //운송장비코드
			//szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분[상하차구분 E:공차상태(상차작업),F:영차(하차작업)]
			modifier 				= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)

			if ("".equals(modifier)) { modifier = msgId; }
			
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			
			//**********************************************************************************	
			// 기존 출발정보 유무 확인
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListSposYNchk_E
			SELECT YD_CAR_SCH_ID
			      ,SPOS_WLOC_CD
			      ,YD_CARLD_WRK_BOOK_ID
			      ,SPOS_WLOC_CD
			      ,YD_CAR_PROG_STAT
			  FROM TB_YD_CARSCH
			 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			   AND DEL_YN = 'N'
			   AND YD_CAR_PROG_STAT IN ('1','2') --1:상차출발,2:상차도착 */
			jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
			JDTORecordSet sposChklist = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListSposYNchk_E", logId, methodNm, "기존 출발정보 유무 확인");
			
			if (sposChklist.size() > 0) {
				
				String s_SPOS_WLOC_CD 		= commUtils.trim(sposChklist.getRecord(0).getFieldString("SPOS_WLOC_CD"));
				String s_CARLD_WRK_BOOK_ID 	= commUtils.trim(sposChklist.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID"));
				String s_YD_CAR_SCH_ID		= commUtils.trim(sposChklist.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				String s_YD_CAR_PROG_STAT	= commUtils.trim(sposChklist.getRecord(0).getFieldString("YD_CAR_PROG_STAT"));
				
				szMsg="["+methodNm+"] 검색 결과  >> SPOS_WLOC_CD: " + s_SPOS_WLOC_CD + ", CARLD_WRK_BOOK_ID: " + s_CARLD_WRK_BOOK_ID + ", YD_CAR_SCH_ID: " + s_YD_CAR_SCH_ID + ", YD_CAR_PROG_STAT: " + s_YD_CAR_PROG_STAT;
				commUtils.printLog(logId, szMsg, "SL");
				
				//**********************************************************************************	
				// AB열연스케쥴 존재여부 체크
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListSchchkYN 
				SELECT *
				FROM   TB_YM_CRNSCH
				WHERE  YD_WBOOK_ID = :V_YD_WBOOK_ID
				AND    DEL_YN = 'N' */
				jrParam.setField("YD_WBOOK_ID"	, s_CARLD_WRK_BOOK_ID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListSchchkYN", logId, methodNm, "AB열연스케쥴 존재여부 체크");
				if (rsResult.size() > 0) {
					szMsg = "["+methodNm+"] AB열연스케쥴 존재여부 체크 결과 YD_CRN_SCH_ID : " 
					      + commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")) 
					      + " 검색되어 차량출발취소 처리가 불가합니다.";
					commUtils.printLog(logId, szMsg, "S-");
					
					return sndRecord;					
				}
				
				//**********************************************************************************	
				// 신규야드스케쥴 존재여부 체크
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListYDSchchkYN 
				SELECT *
				FROM   TB_YD_CRNSCH
				WHERE  YD_WBOOK_ID = :V_YD_WBOOK_ID
				AND    DEL_YN = 'N' */
				jrParam.setField("YD_WBOOK_ID"	, s_CARLD_WRK_BOOK_ID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListYDSchchkYN", logId, methodNm, "신규야드스케쥴 존재여부 체크");
				if (rsResult.size() > 0) {
					szMsg = "["+methodNm+"] 신규야드스케쥴 존재여부 체크 결과 YD_CRN_SCH_ID : " 
					      + commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")) 
					      + " 검색되어 차량출발취소 처리가 불가합니다.";
					commUtils.printLog(logId, szMsg, "S-");
							
					return sndRecord;				
				}
				
				//개소코드로 AB열연인지 판단한다. 
				if (getABLocationInfo_02(s_SPOS_WLOC_CD)) {

					//**********************************************************************************	
					// 1.AB열연으로 출발한 정보 취소처리
					szMsg="["+methodNm+"] AB열연으로 출발한 정보 취소처리 ============================";
					commUtils.printLog(logId, szMsg, "SL");
					
					//**********************************************************************************	
					// 1-1.AB열연야드 작업예약삭제 (DEL_YN='Y')
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.deleteWBook", logId, methodNm, "AB열연야드 작업예약삭제");
					
					//**********************************************************************************	
					// 1-2.AB열연야드 작업예약재료삭제 (DEL_YN='Y')
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delYMStkBookDtl", logId, methodNm, "AB열연야드 작업예약삭제");
					
					if ("1".equals(s_YD_CAR_PROG_STAT)) {
						
						//**********************************************************************************	
						// 1-3-1.AB열연야드 예약위치정보삭제
						jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리");
						
					} else if ("2".equals(s_YD_CAR_PROG_STAT)) {

						//**********************************************************************************	
						// 1-3-2.AB열연야드 적치단  Table Update(close 로 변경)
						jrParam.setField("STACK_LAYER_ACTIVE_STAT", "C");
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStackLayerStatMark_empty", logId, methodNm, "AB열연야드 적치단  Table Update(close 로 변경)");
						
						//**********************************************************************************	
						// 1-3-3.AB열연야드 현재위치정보삭제
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseGpByTrnEqpCd", logId, methodNm, "차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");
					}
					
					//**********************************************************************************	
					// 1-4.차량스케줄정보삭제
					jrParam.setField("YD_CAR_SCH_ID", s_YD_CAR_SCH_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delCarschID", logId, methodNm, "차량스케줄정보삭제");
					
					
					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YmCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
					
				} else {
					
					//**********************************************************************************	
					// 2.신규야드로 출발한 정보 취소처리
					szMsg="["+methodNm+"] 신규야드로 출발한 정보 취소처리 ============================";
					commUtils.printLog(logId, szMsg, "SL");
					
					//**********************************************************************************	
					// 2-1.신규야드 작업예약삭제
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delYDStkBook", logId, methodNm, "신규야드 작업예약삭제");
					
					//**********************************************************************************	
					// 2-2.신규야드 작업예약재료삭제
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delYDStkBookDtl", logId, methodNm, "신규야드 작업예약삭제");
					
					//**********************************************************************************	
					// 2-3.신규야드 예약위치정보삭제
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delYDStkBookLoc", logId, methodNm, "신규야드 예약위치정보삭제");
					
					//**********************************************************************************	
					// 2-4.차량스케줄정보삭제
					jrParam.setField("YD_CAR_SCH_ID", s_YD_CAR_SCH_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delCarschID", logId, methodNm, "차량스케줄정보삭제");
					
					
					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YmCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
				}
				
			} else {
				
				szMsg="["+methodNm+"] 기존 출발정보 유무 확인 - 운송장비코드 : " + szTRN_EQP_CD + " 로 출발정보를 찾지 못했습니다!" ;
				commUtils.printLog(logId, szMsg, "SL");
			}
	    
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} //end of rcvTSYDJ014()
	
	/**
	 *      [A] 오퍼레이션명 : 차량입동지시요구(YMYMJ662) -- 기존 procCarBayInOrdReqNEW (YDYDJ662) 참조
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ662(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "차량입동지시요구[YmCommCarMvSeEJB.rcvYMYMJ662] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		
	    try{
	    	commUtils.printLog(logId, methodNm, "S+");
	    	commUtils.printParam(logId + " 차량입동지시요구(YMYMJ662) 수신 ", rcvMsg);
	    	
	    	String  sAPP055_YN = ymComm.BCoilApplyYn("APP055","3","1"); //구내운송차량 대기장 도착 후 입동지시 여부
	    	
	    	if("Y".equals(sAPP055_YN))  {
	    		
	    		JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	
	    		
	    		JDTORecordSet	jsCarSch = null;
	    		
	    	    String		sYD_CAR_USE_GP		= "G";	//야드차량사용구분(L구내운송 / G출하)
	    	    String		sYD_CAR_USETYPE_GP	= "";	//야드차량사용TYPE구분
	    		
	    	    String		sTRN_EQP_CD			= "";
	    	    String		sWLOC_CD			= "";
	    	    String		sYD_EQP_WRK_STAT	= "";	//야드설비작업상태 (L:영차, U:공차)
	    	    String		sTRN_WRK_FULLVOID_GP= "";
	    	    
	    		
		    	jrParam.setField("YD_CARPNT_CD",	commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD")));
	    		
		    	jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointAllCarSchSelect", logId, methodNm, "TB_YD_CARSCH 조회 - 다음에 입동할차량 조회(출하차량 + 구내운송차량)");
	    		
		    	if (jsCarSch.size() > 0) {
		    		
					sYD_CAR_USE_GP		= jsCarSch.getRecord(0).getFieldString("YD_CAR_USE_GP");		//야드차량사용구분(L구내운송 / G출하)
					sYD_CAR_USETYPE_GP	= jsCarSch.getRecord(0).getFieldString("YD_CAR_USETYPE_GP");	//야드차량사용TYPE구분
					
					sTRN_EQP_CD			= jsCarSch.getRecord(0).getFieldString("TRN_EQP_CD");
					sWLOC_CD			= jsCarSch.getRecord(0).getFieldString("WLOC_CD");
					sYD_EQP_WRK_STAT	= jsCarSch.getRecord(0).getFieldString("YD_EQP_WRK_STAT");		//야드설비작업상태 (L:영차, U:공차)
		    		
					if("L".equals(sYD_CAR_USE_GP) && ("GT".equals(sYD_CAR_USETYPE_GP) || "TO".equals(sYD_CAR_USETYPE_GP))) {
						
						//다음입동할 차량스케줄이 L(구내운송) 이고 포인트TYPE이 GT 이거나 TO 인경우...구내운송 입동포인트 전송및 입동처리
						if("L".equals(sYD_EQP_WRK_STAT)) {
							//영차(L)인경우 F(영차)로 셋팅
							sTRN_WRK_FULLVOID_GP = "F";	//운송작업영공구분 F : 영차
						} else {
							//영차(L)가 아닌경우 E(공차)로 셋팅
							sTRN_WRK_FULLVOID_GP = "E";	//운송작업영공구분 E : 공차
						}
						
						rcvMsg.setField("JMS_TC_CD",			"TSYDJ002");
						rcvMsg.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
						rcvMsg.setField("TRN_EQP_CD",			sTRN_EQP_CD);
						rcvMsg.setField("WLOC_CD", 				sWLOC_CD);
						rcvMsg.setField("TRN_WRK_FULLVOID_GP",	sTRN_WRK_FULLVOID_GP);
						
						jrRtn = commUtils.addSndData(jrRtn, this.rcvTSYDJ002(rcvMsg));
					} else {
			    		jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ662(rcvMsg));
					}
		    	} else {
		    		jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ662(rcvMsg));
		    	}
		    	
	    	} else {
	    		
	    		jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ662(rcvMsg));
	    	}
	    	
	    	commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvYMYMJ662
	
	/**
	 *      [A] 오퍼레이션명 : 차량입동지시요구(YMYMJ662) -- 기존 procCarBayInOrdReqNEW (YDYDJ662) 참조
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procYMYMJ662(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "차량입동지시요구[YmCommCarMvSeEJB.procYMYMJ662] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult 	= null;
		JDTORecordSet jsCarSch  = null;
		
	    try{
	    	commUtils.printLog(logId, methodNm, "S+");
	    	commUtils.printParam(logId + " 차량입동지시요구(YMYMJ662) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId           	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier        	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 			//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
			
			String szCAR_KIND        	= commUtils.nvl (rcvMsg.getFieldString("CAR_KIND"), "TR"); 		//야드설비ID
			String szYD_CARPNT_CD   	= commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"));  		//입동포인트	 - 필수항목 
			String szYD_CAR_SCH_ID		= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); 		//차량스케줄ID - 선택항목 
			String szCHK_YN  			= commUtils.nvl (rcvMsg.getFieldString("CHK_YN"),"Y"); 			//CHK_YN = 'N' 이면  전달 받은 YD_CARPNT_CD 포인트만 입동 가능한지 체크한다.  
			                                                                                            //CHK_YN = 'Y' 이면  전달 받은 YD_CARPNT_CD 포인트와 동일 통로에 입동가능한 포인트가 있는지 체크한다.
			String szTRANS_FRTOMOVE_GP	= commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP")); 	//1 운송 2 이송
			String szCR_FRTOMOVE_GP		= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP"));		//냉연이송구분
			
			String rcvTransOrdDate 		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"));      //파라메터로 전달 받은 운송지시일자 : 호출하는 DMYDR070, DMYDR061 에서 전달하는 운송지시일자 순번으로
			String rcvTransOrdSeqNo		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));     //파라메터로 전달 받는 운송지시순번 : 하단 YMYMJ312 호출 판단 조건으로 사용된다.
			String szCAR_NO				= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));    			//차량번호 추가
			
			if ( szYD_CARPNT_CD.equals("") ) {
				throw new Exception("차량포인트코드 오류 YD_CARPNT_CD : [" + szYD_CARPNT_CD + "] 차량포인트코드 정보가 없습니다.");
			}
			String szYD_GP = szYD_CARPNT_CD.substring(0,1); //차량포인트 첫자리가 야드구분
			
			String sAPPLY1 = ymComm.BCoilApplyYn("APP003","3","1");
			commUtils.printLog(logId,  "차량ERROR LOG 처리:" + sAPPLY1, "SL");	
			
			// PIDEV 2023.05.23  CHITO007
//			String sApplyYnPI = commDao.ApplyYnPI("", "YmCommCarMvSeEJBSBean => procYMYMJ662", "APPPI0", "3", "*");
//			
//			if("Y".equals(sApplyYnPI)) {
							 
				jrParam.setField("TRANS_ORD_DATE"		, rcvTransOrdDate); //야드차량포인트코드
				jrParam.setField("TRANS_ORD_SEQNO"		, rcvTransOrdSeqNo ); 
				jrParam.setField("CAR_NO"		, szCAR_NO ); //차량번호 추가
				/**********************************************************
				* 3. 작업예약 등록여부 : 입동 전에 상차 대상이 크레인 작업 또는 다른 작업예약 시 입동 보류
				**********************************************************/		
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookStockYN    
				SELECT DECODE(A.CS_CNT + B.WB_CNT,0, 'N','Y') AS WB_STL_YN
				 FROM 
				       (
				
				        SELECT COUNT(*)         AS CS_CNT --크레인작업재료여부
				          FROM TB_YM_CRNWRKMTL  CM
				             , TB_YM_CRNSCH     CS
				        WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				          AND CM.DEL_YN      = 'N'
				          AND CS.DEL_YN      = 'N'
				          AND CM.STOCK_ID    IN (SELECT STOCK_ID 
				                                   FROM TB_YM_STOCK 
				                                  WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE 
				                                    AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
				                                    AND DEL_YN = 'N')
				       ) A ,
				       (
				        SELECT COUNT(*)         AS WB_CNT --작업예약재료여부
				          FROM TB_YM_WRKBOOKMTL WM
				             , TB_YM_WRKBOOK    WB
				        WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
				          AND WM.DEL_YN      = 'N'
				          AND WB.DEL_YN      = 'N'
				          AND WM.STOCK_ID    IN (SELECT STOCK_ID 
				                                   FROM TB_YM_STOCK 
				                                  WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE 
				                                    AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
				                                    AND DEL_YN = 'N')
				       ) B
				*/  	  
				JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookStockYN", logId, methodNm, "작업예약 등록여부");
	
				commUtils.printLog(logId, "작업 예약 편성여부:" + jsChk2.getRecord(0).getFieldString("WB_STL_YN") , "SL");
				if (jsChk2.size() > 0) {
					if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
						
						if(sAPPLY1.equals("Y")) {						
							/***** 차량log ****/
							JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
							jrLogMsg.setResultCode(logId);	//Log ID
							jrLogMsg.setResultMsg(methodNm);	//Log Method Name
							jrLogMsg.setField("MODIFIER"		, modifier); //수정자 셋팅
							jrLogMsg.setField("YD_MSG_NM"		, "대상 코일 작업중(스케줄,작업예약 확인)"); //메세지
							jrLogMsg.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID); //차량스케쥴
							
							EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
							ejbConnLog.trx("updCarErrorLogNew", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
						}	
						throw new Exception("이미 작업예약되어 있슴  Error : "+ szCAR_NO);
					}
				}
//			}
			
			
			//------------------------------------------------------------------------------------------------------------
			// 해당 포인트 입동가능 체크 
			//  - CHK_YN = 'N' 이면  YD_CARPNT_CD 포인트만 입동 가능한지 체크한다. (반품/회송 차량 는 지정한 포인트로만 입동되어야 함)
			//  - CHK_YN = 'Y' 이면  YD_CARPNT_CD 포인트와 동일 통로에 입동가능한 포인트가 있는지 체크한다.
	    	//------------------------------------------------------------------------------------------------------------
			jrParam.setField("YD_CARPNT_CD"	, szYD_CARPNT_CD); //야드차량포인트코드
			jrParam.setField("CAR_KIND"		, szCAR_KIND ); 
			jrParam.setField("CAR_NO"		, szCAR_NO ); //차량번호 추가
			
			if (szCHK_YN.equals("N")) { 
				//파라메터로 전달 받은 YD_CARPNT_CD 포인트가 입동 가능한지 체크
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointCHK2", logId, methodNm, "TB_YD_CARPOINT 조회 - 지정 포인트");
			}else{
				//파라메터로 전달 받은 YD_CARPNT_CD 포인트가 있는 통로에 입동가능한 포인트가 있는지 체크
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointCHK", logId, methodNm, "TB_YD_CARPOINT 조회 - 해당 포인트 통로에 입동가능한 포인트");
			}
			
			if (rsResult.size() <= 0) {
				
				/*******************
				 * 입동불가
				 *******************/
				commUtils.printLog(logId, "해당 포인트 ["+szYD_CARPNT_CD+"]에  입동이 불가능 합니다.", "SL");	

				
				
				//======================================================================================
				// 입동지시 미리 주기 적용여부 확인
				String sAPPLY060 = ymComm.BCoilApplyYn("APP060","3","PREINWO_YN");
				commUtils.printLog(logId,  ">>> 입동지시 미리 주기 적용여부 (Y:적용, N:비적용) :" + sAPPLY060, "SL");
				if (sAPPLY060.equals("Y")) {
				
					//입동지시 받은 차량 갯수 조회
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointCHK3", logId, methodNm, "입동지시 받은 차량 갯수 조회");
					
					if(rsResult.size() > 0) { 
					
						String sJ00002_YN 			= rsResult.getRecord(0).getFieldString("J00002_YN");
						int    iCARPOINT_CLOSE_CNT 	= rsResult.getRecord(0).getFieldInt("CARPOINT_CLOSE_CNT");
						int    iE_CHK_CNT 			= rsResult.getRecord(0).getFieldInt("E_CHK_CNT");
						String sFlag_YN             = "Y";
						
						String newYdCarSchId 	= "";
						String newYdCarNo 		= "";
						String newYdCardNo 		= "";
						String newTransOrdDate	= "";
						String newTransOrdSeqNo	= ""; 
						String newYdCarPntCd	= ""; //입동가능한 신규 포인트
						String newYdPntCd		= "";
						String newWlocCd		= "";
						String transEquipType	= ""; //운송장비Type DMYDR070(이송상차대기장도착), DMYDR073(이송하차대기장도착) 수신으로 생성시 'P'
						
						commUtils.printLog(logId, "전체입동제한(J00002) :"+sJ00002_YN+" ,해당통로 사용불가 포인트 갯수"+": ["+iCARPOINT_CLOSE_CNT+"] ,입동지시 받은 차량갯수 ["+iE_CHK_CNT+"]******* ", "SL");	
						
						if("Y".equals(sJ00002_YN)) {
							//전체입동제한  여기서 종료
							sFlag_YN = "N";
						} else if(iCARPOINT_CLOSE_CNT >= 2) {
							//해당 통로 사용불가 포인트 수가 2 이상이면 여기서 종료
							sFlag_YN = "N";
						} else if(iE_CHK_CNT >= 2) {
							//이미 입동지시 받은 차량스케줄id가 2 이상이면 여기서 종료
							sFlag_YN = "N";
						}
						
						
						if("Y".equals(sFlag_YN)) {
							//2통로에서 다음 차량에 입동지시 미리 줄 수 있는 상황일 경우 
							
					    	//------------------------------------------------------------------------------------------------------------
					    	//	해당 포인트 입동대상 차량스케줄 조회(좌우 통로 같이 검색) 
							//   : 해당 포인트가 있는 통로에 입동 대기중인 차량들 중에서 다음 입동할 차량 (차량스케줄)을 검색 한다.
					    	//------------------------------------------------------------------------------------------------------------
							jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointAllCarSchSelect", logId, methodNm, "TB_YD_CARSCH 해당 포인트 입동대상 차량스케줄 조회(좌우 통로 같이 검색)");
							
							if (jsCarSch.size() > 0) {
								
								jsCarSch.first();
								JDTORecord jrCarSch = jsCarSch.getRecord();

								newYdCarSchId    	= commUtils.nvl(jrCarSch.getFieldString("YD_CAR_SCH_ID"),"");
								newYdCarNo			= commUtils.nvl(jrCarSch.getFieldString("CAR_NO"),"");
								newYdCardNo			= commUtils.nvl(jrCarSch.getFieldString("CARD_NO"),"");
								newTransOrdDate 	= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_DATE"),"");
								newTransOrdSeqNo	= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_SEQNO"),""); 
								newYdCarPntCd    	= commUtils.nvl(jrCarSch.getFieldString("YD_CARPNT_CD"),"");    //입동가능한 신규 포인트
								newYdPntCd   		= commUtils.nvl(jrCarSch.getFieldString("YD_PNT_CD"),"");
								newWlocCd   		= commUtils.nvl(jrCarSch.getFieldString("WLOC_CD"),"");
								transEquipType		= commUtils.nvl(jrCarSch.getFieldString("TRANS_EQUIPMENT_TYPE"),"");  //운송장비Type DMYDR070(이송상차대기장도착), DMYDR073(이송하차대기장도착) 수신으로 생성시 'P'
								
								
								//=============================================================
								// 사용불가 포인트가 아니고 차량동간이적 적용 포인트를 제외한 입동지시 미리 주기용 차량 포인트 조회
								//  - 예를 들어 3APT01 에 차량이 입동해 있고 3APT02 가 사용 불가라면 3APT01 포인트를 리턴한다.
								//=============================================================
								JDTORecordSet jsCarPoint  = null;
								jrParam.setField("YD_CARPNT_CD"	, szYD_CARPNT_CD); //야드차량포인트코드
								jsCarPoint = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointPreInWo", logId, methodNm, "TB_YD_CARPOINT에서 입동지시 미리 주기 차량 포인트 조회");
								
								if (jsCarPoint.size() > 0) {
								
									jsCarPoint.first();
									JDTORecord jrCarPoint = jsCarPoint.getRecord();
									
									newYdCarPntCd  	= commUtils.nvl(jrCarPoint.getFieldString("YD_CARPNT_CD"),"");    //입동가능한 신규 포인트
									newYdPntCd   	= commUtils.nvl(jrCarPoint.getFieldString("YD_PNT_CD"),"");
									newWlocCd   	= commUtils.nvl(jrCarPoint.getFieldString("WLOC_CD"),"");
								}
								
								commUtils.printLog(logId, "[[[[[[[[[[[ szYD_CAR_SCH_ID ["+szYD_CAR_SCH_ID+"] , newYdCarSchId ["+ newYdCarSchId +"] "  , "SL");	
								
								if(!"".equals(szYD_CAR_SCH_ID)) {
									if(!szYD_CAR_SCH_ID.equals(newYdCarSchId)) {

										//대기장에 대기차량이 많을 경우 맨 뒤에 대기장 도착한 차량은 앞의 순의 차량 때문에 입동지시 NO 응답을 받지 못하는 경우 발생
										//이부분에서 YMYMJ662 를 호출한 차량의 차량스케줄ID 가 있을 경우 해당차량에 입동불가 지시 전송
										jrParam.setField("YD_CAR_SCH_ID",szYD_CAR_SCH_ID);
										//해당 차량스케줄ID 차량에 입동불가 지시 전송
										jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
									}
								}
								
								if("32A".equals(szYD_CARPNT_CD.substring(0,3))) {
									
									//수신받은 운송지시일자 순번이 일치하지 않으면 A동 2통로 자동 이적 할 대상이 있는지 확인하여 대상이 있다면 YMYMJ312 를 호출 한다.
									//-------------------------------------------------------------------------------------------------------------
									// A동 2통로 출하대상 자동이적 YMYMJ312
									sAPPLY060 = ymComm.BCoilApplyYn("APP060","3","A3TOA4_DM");
									commUtils.printLog(logId,  ">>> A동 2통로 출하작업 고도화 적용 (Y:적용, N:비적용) :" + sAPPLY060, "SL");
									if (sAPPLY060.equals("Y")) {
										
									    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
									    JDTORecordSet jsCarSchLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarStlYardInfoChk4", logId, methodNm, "대상재가 작업예약이 있는지 체크 조회");
										
									    if (jsCarSchLoc.size() > 0)	{
											commUtils.printLog(logId, "TB_YD_CARSCH["+newYdCarSchId+"]에 대상재에 작업예약이 있습니다.★★★★★★", "SL");
										
											for(int ii=0; ii < jsCarSchLoc.size(); ii++)
											{
												if("3AYD31MM".equals(jsCarSchLoc.getRecord(ii).getFieldString("YD_SCH_CD"))) {
													//A동 2통로 상차대상 자동이적 일 경우
														
													if ("Y".equals(sAPPLY1)) {
														/***** 차량 log ****/
														JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
														jrLogMsg.setResultCode(logId);	//Log ID
														jrLogMsg.setResultMsg(methodNm);	//Log Method Name
														jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
														jrLogMsg.setField("YD_MSG_NM",		"A동 2통로 상차대상재 스케줄작업 중입니다."); //메세지
														jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴

														EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
														ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });	
													}
													
												    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
													jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
													
													return jrRtn;
												}
													
											}
									    }
										
										
										jrParam.setField("TRANS_ORD_DATE" 	, newTransOrdDate);
										jrParam.setField("TRANS_ORD_SEQNO"	, newTransOrdSeqNo);
										
										rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListA3toA4Dm", logId, methodNm, "A동 2통로 출하 자동이적 대상 리스트 조회");
										
										if (rsResult.size() > 0) {
											//A4 크레인이 갈수 없는 영역에 출하 대상이 존재하면 자동이적  YMYMJ312 호출 
										
											JDTORecord jrYMYMJ312 = JDTORecordFactory.getInstance().create();
											jrYMYMJ312.setResultCode(logId);	//Log ID
											jrYMYMJ312.setResultMsg(methodNm);	//Log Method Name
											jrYMYMJ312.setField("JMS_TC_CD" 		, "YMYMJ312");
											jrYMYMJ312.setField("TRANS_ORD_DATE" 	, newTransOrdDate);
											jrYMYMJ312.setField("TRANS_ORD_SEQNO"	, newTransOrdSeqNo);
											jrYMYMJ312.setField("MODIFIER"	        , modifier);
											
											jrRtn = commUtils.addSndData(jrRtn, jrYMYMJ312);
											
											if ("Y".equals(sAPPLY1)) {
												/***** 차량 log ****/
												JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
												jrLogMsg.setResultCode(logId);	//Log ID
												jrLogMsg.setResultMsg(methodNm);	//Log Method Name
												jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
												jrLogMsg.setField("YD_MSG_NM",		"A4 크레인 작업 불가한 영역에 대상재가 있습니다."); //메세지
												jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴
		
												EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
												ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });	
											}
											
										    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
											jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
											
											return jrRtn;
										}
									}
								}
								
							} else {
								
								sFlag_YN = "N";
							}
						}
						
						
						
						if("Y".equals(sFlag_YN)) {
							//2통로에서 다음 차량에 입동지시 미리 줄 수 있는 상황일 경우 
							
							//------------------------------------------------------------------------------------------------------------
					    	//	입동TC 전송
					    	//------------------------------------------------------------------------------------------------------------
							JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
							
							// PIDEV
//							if("Y".equals(sApplyYnPI)) {
								recInTemp.setField("MQ_TC_CD"				, "M10YDLMJ1061");
								recInTemp.setField("MQ_TC_CREATE_DDTT"		, commUtils.getDateTime14());
								recInTemp.setField("TRN_REQ_DATE"			, newTransOrdDate);
								recInTemp.setField("TRN_REQ_SEQ" 			, newTransOrdSeqNo);
								recInTemp.setField("CAR_NO"					, newYdCarNo);
								// recInTemp.setField("CARD_NO"				, newYdCardNo);
								recInTemp.setField("YD_GP"		        	, "3");
								recInTemp.setField("DIST_GOODS_GP"		    , "H");
								if ("P".equals(transEquipType)) {
									recInTemp.setField("SCH_YN"       		, "Y"); // 스케줄 여부
								} else {
									recInTemp.setField("SCH_YN"       		, "N"); // 스케줄 여부
								}
								recInTemp.setField("BAYIN_DDTT"		        , commUtils.getDateTime14());
								recInTemp.setField("WLOC_CD"				, newWlocCd);
								recInTemp.setField("YD_PNT_CD"				, newYdPntCd);
								recInTemp.setField("YD_CARPNT_CD"			, newYdCarPntCd);	
								recInTemp.setField("LOAN_PULLOUT_ABLE_YN"   , "Y");
								
//							} else {
//								if ("P".equals(transEquipType)) {
//									
//									jrParam.setField("TRANS_ORD_DATE2",  commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE")));
//									jrParam.setField("TRANS_ORD_SEQNO2", commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
//									
//									// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
//									rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockTransOrdDateAB", logId, methodNm, "운송지싱일자,순번으로 STOCK테이블에서 CR_FRTOMOVE_GP(냉연이송구분 )조회");
//									if(rsResult.size()>0) {
//										szCR_FRTOMOVE_GP = StringHelper.evl(rsResult.getRecord(0).getFieldString("CR_FRTOMOVE_GP"),"");
//									}
//									//-------------------------------------------------------------------------
//									
//									
//									recInTemp.setField("JMS_TC_CD"			, "YDDMR070");
//									recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
//									recInTemp.setField("TC_CODE"			, "YDDMR070");
//									recInTemp.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
//									recInTemp.setField("CR_FRTOMOVE_GP"     , szCR_FRTOMOVE_GP);
//								} else {
//									recInTemp.setField("JMS_TC_CD"			, "YDDMR028");
//									recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
//									recInTemp.setField("TC_CODE"			, "YDDMR028");
//									recInTemp.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
//								}
//								recInTemp.setField("TRANS_WORD_DATE"		, newTransOrdDate);
//								recInTemp.setField("TRANS_WORD_SEQNO" 		, newTransOrdSeqNo);
//								recInTemp.setField("BAYIN_DDTT"		        , commUtils.getDateTime14());
//
//								recInTemp.setField("CARD_NO"				, newYdCardNo);
//								recInTemp.setField("CAR_NO"					, newYdCarNo);
//								recInTemp.setField("WLOC_CD"				, newWlocCd);
//								recInTemp.setField("YD_PNT_CD"				, newYdPntCd);
//								recInTemp.setField("YD_CARPNT_CD"			, newYdCarPntCd);	
//								recInTemp.setField("LOAN_PULLOUT_ABLE_YN"   , "Y");								
//								
//							}
							
							jrRtn = commUtils.addSndData(jrRtn, recInTemp);
							
							
							//------------------------------------------------------------------------------------------------------------
							//입동지시 대상차량에 입동지시 송신여부 셋팅
							//------------------------------------------------------------------------------------------------------------
							
							//PIDEV					
//							if("Y".equals(sApplyYnPI)) {	
								jrParam.setField("YD_CAR_RCPT_CHK_YN"	, "E"); 
								jrParam.setField("CAR_NO"				, newYdCarNo);
								jrParam.setField("TRANS_ORD_DATE"		, newTransOrdDate);
								jrParam.setField("TRANS_ORD_SEQNO"		, newTransOrdSeqNo);
								
								/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarschYdCarRcptChkYn_PIDEV 
								UPDATE TB_YD_CARSCH 
								   SET YD_CAR_RCPT_CHK_YN = :V_YD_CAR_RCPT_CHK_YN
								 WHERE CAR_NO             = :V_CAR_NO
								   AND TRANS_ORD_DATE     = :V_TRANS_ORD_DATE
								   AND TRANS_ORD_SEQNO    = :V_TRANS_ORD_SEQNO
								*/   
								commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarschYdCarRcptChkYn_PIDEV", logId, methodNm, "차량스케줄에 입동지시 송신여부 셋팅");								
								
//							} else {
//								jrParam.setField("YD_CAR_RCPT_CHK_YN"	, "E"); 
//								jrParam.setField("CAR_NO"				, newYdCarNo);
//								jrParam.setField("CARD_NO"				, newYdCardNo);
//								jrParam.setField("TRANS_ORD_DATE"		, newTransOrdDate);
//								jrParam.setField("TRANS_ORD_SEQNO"		, newTransOrdSeqNo);
//								
//								/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarschYdCarRcptChkYn 
//								UPDATE TB_YD_CARSCH 
//								   SET YD_CAR_RCPT_CHK_YN = :V_YD_CAR_RCPT_CHK_YN
//								 WHERE CAR_NO             = :V_CAR_NO
//								   AND CARD_NO            = :V_CARD_NO
//								   AND TRANS_ORD_DATE     = :V_TRANS_ORD_DATE
//								   AND TRANS_ORD_SEQNO    = :V_TRANS_ORD_SEQNO
//								*/   
//								commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarschYdCarRcptChkYn", logId, methodNm, "차량스케줄에 입동지시 송신여부 셋팅");								
//							}
							
					 		if ("Y".equals(sAPPLY1)) {
								/***** 차량log Claer ****/
								JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
								jrLogMsg.setResultCode(logId);	//Log ID
								jrLogMsg.setResultMsg(methodNm);	//Log Method Name
								jrLogMsg.setField("MODIFIER"		, modifier); //수정자 셋팅
								jrLogMsg.setField("YD_CAR_SCH_ID"	, newYdCarSchId); //차량스케쥴
								
								EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
								ejbConnLog.trx("updCarErrorLogClear", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });

					 		}
					 		
						   //------------------------------------------------------------------------------------------------------------
					       //	Ym 차량  STOCK 저장품 현위치 등록 
					       //------------------------------------------------------------------------------------------------------------
							jrParam.setField("TRANS_WORD_DATE"		, newTransOrdDate);
							jrParam.setField("TRANS_WORD_SEQNO"		, newTransOrdSeqNo);
							jrParam.setField("MODIFIER"				, modifier);
					 		
						    EJBConnector ejbConn9 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
						    ejbConn9.trx("procUpdYmStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
							//this.procUpdYmStock(jrParam);
					 		
							return jrRtn;
							
						}
					}
				}
				//======================================================================================
				
				//파라메터로 전달 받는 YD_CAR_SCH_ID 값이 필수가 아님으로 값이 없을 수 있다 차량스케줄ID가 없다면 입동불가(N) 전문이 발송 안되고 여기서 종료 된다. 
				if(!"".equals(szYD_CAR_SCH_ID)) {
					
					jrParam.setField("YD_CAR_SCH_ID",szYD_CAR_SCH_ID);
					//해당 차량스케줄ID 차량에 입동불가 지시 전송
					jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
				}
				
			} else {
				
				/*******************
				 * 입동가능 
				 *******************/
				String  szOLD_YD_CARPNT_CD = szYD_CARPNT_CD;
				
		    	//------------------------------------------------------------------------------------------------------------
		    	//	해당 포인트 입동대상 차량스케줄 조회(좌우 통로 같이 검색) 
				//   : 해당 포인트가 있는 통로에 입동 대기중인 차량들 중에서 입동할 차량 (차량스케줄)을 검색 한다.
		    	//------------------------------------------------------------------------------------------------------------
				jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointCarSchSelect", logId, methodNm, "TB_YD_CARSCH 조회");					
				if (jsCarSch.size() <= 0) {
					commUtils.printLog(logId, "해당 포인트 ["+szYD_CARPNT_CD+"] 입동대상 차량스케줄이 없습니다.", "SL");	
					jrRtn.setField("RTN_CD"	, "-1");
					return jrRtn;
					
				}
				jsCarSch.first();
				JDTORecord jrCarSch = jsCarSch.getRecord();

				String newYdCarSchId    = commUtils.nvl(jrCarSch.getFieldString("YD_CAR_SCH_ID"),"");
				String newYdCarKind		= commUtils.nvl(jrCarSch.getFieldString("CAR_KIND"),"TR");
				String newYdCarNo		= commUtils.nvl(jrCarSch.getFieldString("CAR_NO"),"");
				String newYdCardNo		= commUtils.nvl(jrCarSch.getFieldString("CARD_NO"),"");
				String newTrnEqpCd	   	= commUtils.nvl(jrCarSch.getFieldString("TRN_EQP_CD"),"");					
				String newTransOrdDate 	= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_DATE"),"");
				String newTransOrdSeqNo	= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_SEQNO"),""); 
				String ydCarWrkGp		= commUtils.nvl(jrCarSch.getFieldString("YD_CAR_WRK_GP"),""); 	//야드차량작업구분  1:내수,2:수출,3:연안해송,9:냉연이송
				String newYdCarPntCd    = commUtils.nvl(jrCarSch.getFieldString("YD_CARPNT_CD"),"");    //입동가능한 신규 포인트
				String newYdStackColGp  = commUtils.nvl(jrCarSch.getFieldString("YD_STK_COL_GP"),""); 
				String newYdPntCd   	= commUtils.nvl(jrCarSch.getFieldString("YD_PNT_CD"),"");
				szYD_CARPNT_CD    	   	= commUtils.nvl(jrCarSch.getFieldString("OLD_YD_CARPNT_CD"),""); //원래 입동하려고 했던 OLD 포인트
				String ydCarldStopLoc   = commUtils.nvl(jrCarSch.getFieldString("OLD_YD_STK_COL_GP"),""); 
				String ydPntCd    		= commUtils.nvl(jrCarSch.getFieldString("YD_PNT_CD1"),"");
				String newWlocCd   		= commUtils.nvl(jrCarSch.getFieldString("WLOC_CD"),"");
				String transEquipType	= commUtils.nvl(jrCarSch.getFieldString("TRANS_EQUIPMENT_TYPE"),"");  //운송장비Type DMYDR070(이송상차대기장도착), DMYDR073(이송하차대기장도착) 수신으로 생성시 'P'
				String ydEqpWrkStat		= commUtils.nvl(jrCarSch.getFieldString("YD_EQP_WRK_STAT"),"");	//야드설비작업상태  U:공차, L:영차
				
				commUtils.printLog(logId, "[[[[[[[[[[[ szYD_CAR_SCH_ID ["+szYD_CAR_SCH_ID+"] , newYdCarSchId ["+ newYdCarSchId +"] "  , "SL");	

				if(!"".equals(szYD_CAR_SCH_ID)) {
					if(!szYD_CAR_SCH_ID.equals(newYdCarSchId)) {

						//대기장에 대기차량이 많을 경우 맨 뒤에 대기장 도착한 차량은 앞의 순의 차량 때문에 입동지시 NO 응답을 받지 못하는 경우 발생
						//이부분에서 YMYMJ662 를 호출한 차량의 차량스케줄ID 가 있을 경우 해당차량에 입동불가 지시 전송
						jrParam.setField("YD_CAR_SCH_ID",szYD_CAR_SCH_ID);
						//해당 차량스케줄ID 차량에 입동불가 지시 전송
						jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
					}
				}

			    //------------------------------------------------------------------------------------------------------------
		        //	도착포인트 동에 야드 적치 여부 체크...동에 없으면 종료
		        //------------------------------------------------------------------------------------------------------------
			    jrParam.setField("YD_CAR_SCH_ID"		, newYdCarSchId);
			    
 //PIDEV_S :병행가동용:PI_YD
				jrParam.setField("PI_YD",    	"3");					
			    JDTORecordSet jsCarSchLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarStlYardInfoChk_PIDEV", logId, methodNm, "TB_YD_CARSCH 조회");					
				if (jsCarSchLoc.size() <= 0) {
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+szYD_CARPNT_CD+"]동에 상차가능 대상이 존재 안합니다.★★★★★★", "SL");
					if (sAPPLY1.equals("Y")) {
						/***** 차량 log ****/
						JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
						jrLogMsg.setResultCode(logId);	//Log ID
						jrLogMsg.setResultMsg(methodNm);	//Log Method Name
						jrLogMsg.setField("MODIFIER"		, modifier); //수정자 셋팅
						jrLogMsg.setField("YD_MSG_NM"		, "[해당 포인트 ["+szYD_CARPNT_CD+"]동에 상차대상재료가 없습니다.: 대상재위치 확인바랍니다. "); //메세지
						jrLogMsg.setField("YD_CAR_SCH_ID"	, newYdCarSchId); //차량스케쥴
						
						EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
						ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });	
					}

				    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
					jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));

					return jrRtn;
				}
				
				//------------------------------------------------------------------------------------------------------------
				//	도착포인트 TB_YD_CARPOINT 스판 범위안에 야드 적치 여부 체크...범위에 없으면 종료
				//------------------------------------------------------------------------------------------------------------
				jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
			    jsCarSchLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarStlYardInfoChk2", logId, methodNm, "도착포인트 TB_YD_CARPOINT 스판 범위안에 야드 적치 여부 체크 조회");

			    if (jsCarSchLoc.size() <= 0) {
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+szYD_CARPNT_CD+"]동의 SPAN영역에 상차가능 대상이 존재 안합니다.★★★★★★", "SL");

					if ("Y".equals(sAPPLY1)) {
						/***** 차량 log ****/
						JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
						jrLogMsg.setResultCode(logId);	//Log ID
						jrLogMsg.setResultMsg(methodNm);	//Log Method Name
						jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
						jrLogMsg.setField("YD_MSG_NM",		"해당 동의 SPAN영역에 상차대상재료가 없습니다."); //메세지
						jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴

						EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
						ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });	
					}

				    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
					jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
					
					return jrRtn;
				}
				
				//------------------------------------------------------------------------------------------------------------
				//	종료안된 크레인스케줄이 있는지 체크...존재하면 종료
				//------------------------------------------------------------------------------------------------------------
			    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
			    jsCarSchLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarStlYardInfoChk3", logId, methodNm, "대상재가 종료안된 크레인스케줄이 있는지 체크 조회");

			    if (jsCarSchLoc.size() > 0) {
					commUtils.printLog(logId, "TB_YD_CARSCH["+newYdCarSchId+"]에 대상재가 크레인작업 중입니다.★★★★★★", "SL");

					if ("Y".equals(sAPPLY1)) {
						/***** 차량 log ****/
						JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
						jrLogMsg.setResultCode(logId);	//Log ID
						jrLogMsg.setResultMsg(methodNm);	//Log Method Name
						jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
						jrLogMsg.setField("YD_MSG_NM",		"출하대상재가 크레인 작업 중입니다."); //메세지
						jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴

						EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
						ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });	
					}

				    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
					jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
					
					return jrRtn;
				}
			    
			    //------------------------------------------------------------------------------------------------------------
				//	대상재가 작업예약이 있는지 체크...존재하면 삭제처리 후 진행 
			    //  3AYD31MM A동 2통로 상차대상 자동이적일 경우 로그 기록 후 종료 (입동처리 안함)
				//------------------------------------------------------------------------------------------------------------
			    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
			    jsCarSchLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarStlYardInfoChk4", logId, methodNm, "대상재가 작업예약이 있는지 체크 조회");
			    
			    if (jsCarSchLoc.size() > 0)	{
					commUtils.printLog(logId, "TB_YD_CARSCH["+newYdCarSchId+"]에 대상재에 작업예약이 있습니다.★★★★★★", "SL");
					
					for(int ii=0; ii < jsCarSchLoc.size(); ii++)
					{
						if("3AYD31MM".equals(jsCarSchLoc.getRecord(ii).getFieldString("YD_SCH_CD"))) {
							//A동 2통로 상차대상 자동이적 일 경우
								
							if ("Y".equals(sAPPLY1)) {
								/***** 차량 log ****/
								JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
								jrLogMsg.setResultCode(logId);	//Log ID
								jrLogMsg.setResultMsg(methodNm);	//Log Method Name
								jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
								jrLogMsg.setField("YD_MSG_NM",		"A동 2통로 상차대상재 스케줄작업 중입니다."); //메세지
								jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴

								EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
								ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });	
							}
							
						    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
							jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
							
							return jrRtn;
						}
							
					}

					commUtils.printLog(logId, "TB_YD_CARSCH["+newYdCarSchId+"]에 대상재에 작업예약 삭제 후 계속 진행★★★★★★", "SL");
					
					for(int ii=0; ii < jsCarSchLoc.size(); ii++)
					{
						jrParam.setField("YD_WBOOK_ID", jsCarSchLoc.getRecord(ii).getFieldString("YD_WBOOK_ID"));

						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL 작업예약재료 삭제");	//작업예약재료 삭제

						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK 작업예약 삭제");			//작업예약 삭제					}
					}
				}
			    
			    
				if(!rcvTransOrdDate.equals(newTransOrdDate) || !rcvTransOrdSeqNo.equals(newTransOrdSeqNo)) {
					//수신받은 운송지시일자 순번이 일치하지 않으면 A동 2통로 자동 이적 할 대상이 있는지 확인하여 대상이 있다면 YMYMJ312 를 호출 한다.
					//-------------------------------------------------------------------------------------------------------------
					// A동 2통로 출하대상 자동이적 YMYMJ312
					String sAPPLY060 = ymComm.BCoilApplyYn("APP060","3","A3TOA4_DM");
					commUtils.printLog(logId,  ">>> A동 2통로 출하작업 고도화 적용 (Y:적용, N:비적용) :" + sAPPLY060, "SL");
					if (sAPPLY060.equals("Y")) {
						
						jrParam.setField("TRANS_ORD_DATE" 	, newTransOrdDate);
						jrParam.setField("TRANS_ORD_SEQNO"	, newTransOrdSeqNo);
						
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListA3toA4Dm", logId, methodNm, "A동 2통로 출하 자동이적 대상 리스트 조회");
						
						if (rsResult.size() > 0) {
							//A4 크레인이 갈수 없는 영역에 출하 대상이 존재하면 자동이적  YMYMJ312 호출 
						
							JDTORecord jrYMYMJ312 = JDTORecordFactory.getInstance().create();
							jrYMYMJ312.setResultCode(logId);	//Log ID
							jrYMYMJ312.setResultMsg(methodNm);	//Log Method Name
							jrYMYMJ312.setField("JMS_TC_CD" 		, "YMYMJ312");
							jrYMYMJ312.setField("TRANS_ORD_DATE" 	, newTransOrdDate);
							jrYMYMJ312.setField("TRANS_ORD_SEQNO"	, newTransOrdSeqNo);
							jrYMYMJ312.setField("MODIFIER"	        , modifier);
							
							jrRtn = commUtils.addSndData(jrRtn, jrYMYMJ312);
							
							if ("Y".equals(sAPPLY1)) {
								/***** 차량 log ****/
								JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
								jrLogMsg.setResultCode(logId);	//Log ID
								jrLogMsg.setResultMsg(methodNm);	//Log Method Name
								jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
								jrLogMsg.setField("YD_MSG_NM",		"A4 크레인 작업 불가한 영역에 대상재가 있습니다."); //메세지
								jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴

								EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
								ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });	
							}
							
							jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
							
							jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));

							return jrRtn;
						}
					}
				}
			    
				
				//-------------------------------------------------------------------------------------------------------------
			    
				//------------------------------------------------------------------------------------------------------------
		    	//	1.차량포인트 입동 점유 , 2.차량상태 도착처리
		    	//------------------------------------------------------------------------------------------------------------
				commUtils.printLog(logId, "야드차량작업구분:"+ydCarWrkGp+",변경 포인트"+": ["+newYdCarPntCd+"] 이전포인트 ["+szYD_CARPNT_CD+"]******* 야드설비작업상태:"+ydEqpWrkStat, "SL");	
				commUtils.printLog(logId, "변경 차상위치 ["+newYdStackColGp+"] 이전차상위치["+ydCarldStopLoc+"]******* " + newYdCarNo, "SL");	
				
				//2냉연인경우 기존 차상위치와 포인트로 도착 처리를 한다...냉연차량도 동일동 동일통로인 경우 좌우 구분없이 입동지시에 들어갈수있도록 수정함
				//if (ydCarWrkGp.equals("9") && newYdCarKind.equals("TR")) {
				//	
				//	commUtils.printLog(logId, "2냉연 TR 요구한 포인트와 틀린 경우 skip:"+szOLD_YD_CARPNT_CD+","+szYD_CARPNT_CD, "SL");	
                // 
				//	//입력 차량포인트 와 틀린 경우 return
				//	if (!szOLD_YD_CARPNT_CD.equals(szYD_CARPNT_CD)) {		
				//		if (sAPPLY1.equals("Y")) {
				//			/***** 차량 log ****/
				//			JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
				//			jrLogMsg.setResultCode(logId);	//Log ID
				//			jrLogMsg.setResultMsg(methodNm);	//Log Method Name
				//			jrLogMsg.setField("MODIFIER"		, modifier); //수정자 셋팅
				//			jrLogMsg.setField("YD_MSG_NM"		, "요구  차량포인트:"+ szYD_CARPNT_CD + "기존포인트:" + szYD_CARPNT_CD+" 와 틀립니다."); //메세지
				//			jrLogMsg.setField("YD_CAR_SCH_ID"	, newYdCarSchId); //차량스케쥴
				//			
				//			EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
				//			ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
				//		}	
				//		return jrRtn;
				//	}
				//	
				//}
				//2냉연인경우 기존 차상위치와 포인트로 도착 처리를 한다...냉연차량도 동일동 동일통로인 경우 좌우 구분없이 입동지시에 들어갈수있도록 수정함 
			    //if (ydCarWrkGp.equals("9")) {
				//	commUtils.printLog(logId, "2냉연인경우 [ydCarWrkGp:"+ydCarWrkGp+"] 입동 포인트를 " + newYdStackColGp + " --> " + ydCarldStopLoc + " 로 변경 합니다.", "SL");	
			    //	newYdCarPntCd	= szYD_CARPNT_CD ;
			    //	newYdStackColGp	= ydCarldStopLoc;
			    //	newYdPntCd		= ydPntCd;
			    //}
			    
				// 반입 / 회송도 기존 화면에서 도착처리한 차상위치와 포인트로 도착 처리를 한다. 
			    int intNewTransOrdSeqNo = StringHelper.parseInt(newTransOrdSeqNo, 0);
			 	if (intNewTransOrdSeqNo > 999000) {
					commUtils.printLog(logId, "반입 회송 일경우 [TransOrdSeqNo:"+intNewTransOrdSeqNo+"] 입동 포인트를 " + newYdStackColGp + " --> " + ydCarldStopLoc + " 로 변경 합니다.", "SL");	
			 		newYdCarPntCd	= szYD_CARPNT_CD ;
			    	newYdStackColGp	= ydCarldStopLoc;
			    	newYdPntCd		= ydPntCd;
			 	}
			 	
				if (intNewTransOrdSeqNo > 999000) {
					commUtils.printLog(logId, "포인트와 틀린 경우 skip:"+szOLD_YD_CARPNT_CD+","+szYD_CARPNT_CD, "SL");

					//입력 차량포인트 와 틀린 경우 return
					if (!szOLD_YD_CARPNT_CD.equals(szYD_CARPNT_CD)) {
						commUtils.printLog(logId, "요구  차량포인트:"+ szYD_CARPNT_CD + "/기존포인트:" + szOLD_YD_CARPNT_CD + " 가 틀립니다.", "SL");
						jrRtn.setField("RTN_CD"	, "-1");

						jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
						
						jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
						
				 		return jrRtn;
					}
				}			 	
			 	
			 	//if(ydCarWrkGp.equals("9") || intNewTransOrdSeqNo > 999000) {
			 	if(intNewTransOrdSeqNo > 999000) {
			 	    // 반품 회송일 경우 포인트가  점유 되어 있는지 확인해서  점유되어 있다면 여기서 return;
			 		jrParam.setField("YD_CARPNT_CD"	, szYD_CARPNT_CD); //newYdCarPntCd	= szYD_CARPNT_CD ;
				    JDTORecordSet jsCarPntChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "TB_YD_CARPOINT 체크");
				    if (jsCarPntChk.size() > 0) {
				    	
				    	if(!"C".equals(jsCarPntChk.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"))) {

							commUtils.printLog(logId, "해당 포인트 ["+szYD_CARPNT_CD+"] 가  사용가능(C)가 아닙니다. " + jsCarPntChk.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"), "SL");	
							jrRtn.setField("RTN_CD"	, "-1");

							jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
							
							jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
							
					 		return jrRtn;
				    	}

				    } else {
						commUtils.printLog(logId, "해당 포인트 ["+szYD_CARPNT_CD+"] 가  TB_YD_CARPOINT에 없습니다.", "SL");	
						jrRtn.setField("RTN_CD"	, "-1");
						
						jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
						
						jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
						
						return jrRtn;
				    }
			 	}
			 	
			    
			   //------------------------------------------------------------------------------------------------------------
		       //	작업 대상재 CHECK
		       //------------------------------------------------------------------------------------------------------------
			    /*if (ydCarWrkGp.equals("9") && newYdCarKind.equals("TR")) {
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+newYdCarPntCd+"] 2냉연 TR은 크레인 작업예약은 도착전문을 받고서 처리 한다.", "SL");	
					
				} else {*/
					JDTORecord jrInTemp1 = JDTORecordFactory.getInstance().create();
					jrInTemp1.setResultCode(logId);	//Log ID
				    jrInTemp1.setResultMsg(methodNm);	//Log Method Name
					jrInTemp1.setField("MODIFIER"     		, modifier  ); //수정자
					jrInTemp1.setField("TRANS_WORD_DATE"	, newTransOrdDate);
					jrInTemp1.setField("TRANS_WORD_SEQNO"	, newTransOrdSeqNo);
					jrInTemp1.setField("CAR_NO"				, newYdCarNo);
					jrInTemp1.setField("CARD_NO"			, newYdCardNo);
					jrInTemp1.setField("YD_GP"				, newYdStackColGp.substring(0, 1));
					jrInTemp1.setField("YD_BAY_GP"			, newYdCarPntCd.substring(2, 3)); //동정보
					jrInTemp1.setField("YD_CARPNT_CD"		, newYdCarPntCd);
					jrInTemp1.setField("STACK_COL_GP"		, newYdStackColGp);	
					jrInTemp1.setField("CAR_KIND"			, szCAR_KIND);
					jrInTemp1.setField("TRANS_FRTOMOVE_GP"	, szTRANS_FRTOMOVE_GP);
					jrInTemp1.setField("YD_EQP_WRK_STAT"	, ydEqpWrkStat);
					jrInTemp1.setField("YD_CAR_SCH_ID"		, newYdCarSchId); 
					
					//작업 대상재 미리 CHECK
					JDTORecord jrOutCheck = this.procYmWbookInsertCheck(jrInTemp1); //** 주의)COIL야드 작업예약 생성 (코일1개당 작업예약 1개 생성) --> Slab 야드와 달라서 Slab 작업예약 생성에 사용할 수 없다.
					
					String sStockStat	= commUtils.trim(jrOutCheck.getFieldString("STAT"));
					String sYdMsg		= commUtils.trim(jrOutCheck.getFieldString("YD_MSG"));
					if (!sStockStat.equals("1")) {
						if (sAPPLY1.equals("Y")) {
							/***** 차량 log ****/
							JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
							jrLogMsg.setResultCode(logId);	//Log ID
							jrLogMsg.setResultMsg(methodNm);	//Log Method Name
							jrLogMsg.setField("MODIFIER"		, modifier); //수정자 셋팅
							jrLogMsg.setField("YD_MSG_NM"		, sYdMsg); //메세지
							jrLogMsg.setField("YD_CAR_SCH_ID"	, newYdCarSchId); //차량스케쥴
							
							EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
							ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
						}
						
						jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
						
						jrRtn = commUtils.addSndData(jrRtn, this.sndBayInRejTC(jrParam));
						
						return jrRtn;
					}
				//} --작업예약 체크(procYmWbookInsertCheck) 도착전문 받기전에 작업예약 체크 실행
			    
			    JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();
			    jrInTemp.setResultCode(logId);	//Log ID
			    jrInTemp.setResultMsg(methodNm);	//Log Method Name
				jrInTemp.setField("MODIFIER"     		, modifier  ); //수정자
				jrInTemp.setField("TRN_EQP_CD"			, newTrnEqpCd);									
				jrInTemp.setField("CAR_NO"				, newYdCarNo);
				jrInTemp.setField("CARD_NO"			    , newYdCardNo);
				jrInTemp.setField("YD_MAKECARPNT_CD"    , newYdCarPntCd);
			    				
			   //------------------------------------------------------------------------------------------------------------
		       //	차량 POINT TABLE 점유상태
		       //------------------------------------------------------------------------------------------------------------
			    EJBConnector ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
			    ejbConn.trx("procUpdYdTransOrdChange", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });	
			    
				//------------------------------------------------------------------------------------------------------------
		    	//	차량스케줄 도착상태 변경 처리
		    	//------------------------------------------------------------------------------------------------------------
	
			    jrInTemp = JDTORecordFactory.getInstance().create();	 
			    jrInTemp.setResultCode(logId);	//Log ID
			    jrInTemp.setResultMsg(methodNm);	//Log Method Name
				jrInTemp.setField("MODIFIER"     		, modifier  ); //수정자
				jrInTemp.setField("YD_CAR_SCH_ID"		, newYdCarSchId); 
				
				if (ydEqpWrkStat.equals("L")) {
					jrInTemp.setField("YD_CARUD_ARR_DT"	, commUtils.getDateTime14());
					jrInTemp.setField("YD_CAR_PROG_STAT", "B" );		//하차도착상태
					
					jrInTemp.setField("ARR_WLOC_CD",		newWlocCd);	//착지개소코드
					jrInTemp.setField("YD_PNT_CD3",			newYdPntCd);
					jrInTemp.setField("YD_CARUD_STOP_LOC",	newYdStackColGp);
					
				}else{
					jrInTemp.setField("YD_CARLD_ARR_DT"	, commUtils.getDateTime14());
					jrInTemp.setField("YD_CAR_PROG_STAT", "2" );		//상차도착상태
					
					jrInTemp.setField("SPOS_WLOC_CD"		, newWlocCd);	//발지개소코드													
					jrInTemp.setField("YD_PNT_CD1"		 	, newYdPntCd);
					jrInTemp.setField("YD_CARLD_STOP_LOC"	, newYdStackColGp); 
				}
				
				//냉연차량도 동일동 동일통로인 경우 좌우 구분없이 입동지시에 들어갈수있도록 수정함
				//if (!ydCarWrkGp.equals("9")) {
				//	jrInTemp.setField("SPOS_WLOC_CD"		, newWlocCd);	//발지개소코드													
				//	jrInTemp.setField("YD_PNT_CD1"		 	, newYdPntCd);
				//	jrInTemp.setField("YD_CARLD_STOP_LOC"	, newYdStackColGp); 
				//} 
	
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch
				UPDATE TB_YD_CARSCH
				   SET MODIFIER         = :V_MODIFIER
				     , MOD_DDTT         = SYSDATE
				     , DEL_YN           = NVL(:V_DEL_YN          , DEL_YN)
				     --하차
				     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT    , YD_CAR_PROG_STAT)
				     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
				     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC   , YD_CARUD_STOP_LOC)
				     , YD_CARUD_ARR_DT      = NVL(:V_YD_CARUD_ARR_DT     , YD_CARUD_ARR_DT)
				     , YD_PNT_CD3           = NVL(:V_YD_PNT_CD3          , YD_PNT_CD3)
				     --상차
				     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID)
				     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC   , YD_CARLD_STOP_LOC)
				     , YD_CARLD_ARR_DT      = NVL(:V_YD_CARLD_ARR_DT     , YD_CARLD_ARR_DT)
				     , YD_PNT_CD1           = NVL(:V_YD_PNT_CD1          , YD_PNT_CD1)
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  

				*/ 
				commDao.update(jrInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 상태변경");

				jrInTemp = JDTORecordFactory.getInstance().create();	 
				jrInTemp.setResultCode(logId);	//Log ID
			    jrInTemp.setResultMsg(methodNm);	//Log Method Name
				jrInTemp.setField("MODIFIER"     	, modifier  ); //수정자
				jrInTemp.setField("STACK_COL_GP"	, newYdStackColGp);
				jrInTemp.setField("CARD_NO"			, newYdCardNo);
				jrInTemp.setField("CAR_NO"			, newYdCarNo);
				jrInTemp.setField("TRN_EQP_CD"		, newTrnEqpCd);
				jrInTemp.setField("YD_GP"			, newYdStackColGp.substring(0, 1));
				jrInTemp.setField("TRANS_WORD_DATE"	, newTransOrdDate);
				jrInTemp.setField("TRANS_WORD_SEQNO", newTransOrdSeqNo);
				jrInTemp.setField("YD_EQP_WRK_STAT" , ydEqpWrkStat); // 'L': 하차작업,'U':상차적업
				
				jrInTemp.setField("MODIFIER"    	, modifier); //수정자 
	
			   //------------------------------------------------------------------------------------------------------------
		       //	Ym 차량  STOCK 저장품 현위치 등록 
			   //   출하 MAP 활성화	
		       //------------------------------------------------------------------------------------------------------------
			    EJBConnector ejbConn9 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
			    ejbConn9.trx("procUpdYmStock", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });
				
				EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
				ejbConn1.trx("procYmLayerOpen", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });

	 			String ydWbookId	= "";
	 			String ydEqpId   	= "";
	 			String ydSchCd   	= "";				

				//------------------------------------------------------------------------------------------------------------
		    	//	크레인 작업예약 스케줄 생성
		    	//------------------------------------------------------------------------------------------------------------		 

	 			if (ydCarWrkGp.equals("9") && newYdCarKind.equals("TR")) {
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+szYD_CARPNT_CD+"] 2냉연 TR은 크레인 작업예약은 도착전문을 받고서 처리 한다.", "SL");	
					
				} else {
					jrInTemp = JDTORecordFactory.getInstance().create();
					jrInTemp.setResultCode(logId);	//Log ID
				    jrInTemp.setResultMsg(methodNm);	//Log Method Name
					jrInTemp.setField("MODIFIER"     		, modifier  ); //수정자
					jrInTemp.setField("TRANS_WORD_DATE"		, newTransOrdDate);
					jrInTemp.setField("TRANS_WORD_SEQNO"	, newTransOrdSeqNo);
					jrInTemp.setField("CAR_NO"				, newYdCarNo);
					jrInTemp.setField("CARD_NO"				, newYdCardNo);
					jrInTemp.setField("YD_GP"				, newYdStackColGp.substring(0, 1));
					jrInTemp.setField("YD_BAY_GP"			, newYdCarPntCd.substring(2, 3)); //동정보
					jrInTemp.setField("YD_CARPNT_CD"		, newYdCarPntCd);
					jrInTemp.setField("STACK_COL_GP"		, newYdStackColGp);	
					jrInTemp.setField("CAR_KIND"			, szCAR_KIND);
					jrInTemp.setField("TRANS_FRTOMOVE_GP"	, szTRANS_FRTOMOVE_GP);
					jrInTemp.setField("YD_EQP_WRK_STAT"	    , ydEqpWrkStat);
					jrInTemp.setField("YD_CAR_SCH_ID"		, newYdCarSchId); 
					
					JDTORecord jrOutPara = this.procYmWbookInsert(jrInTemp); //** 주의)COIL야드 작업예약 생성 (코일1개당 작업예약 1개 생성) --> Slab 야드와 달라서 Slab 작업예약 생성에 사용할 수 없다.
					
		 			ydWbookId	= commUtils.trim(jrOutPara.getFieldString("YD_WBOOK_ID"));
		 			ydEqpId   	= commUtils.trim(jrOutPara.getFieldString("YD_EQP_ID"));
		 			ydSchCd   	= commUtils.trim(jrOutPara.getFieldString("YD_SCH_CD"));
					 
					//------------------------------------------------------------------------------------------------------------
			    	//	차량스케줄 도착상태 변경 처리
			    	//------------------------------------------------------------------------------------------------------------				   
					
		 			jrInTemp = JDTORecordFactory.getInstance().create();	 
		 			jrInTemp.setResultCode(logId);	//Log ID
				    jrInTemp.setResultMsg(methodNm);	//Log Method Name
					jrInTemp.setField("MODIFIER"     		, modifier  ); //수정자
					jrInTemp.setField("YD_CAR_SCH_ID"		, newYdCarSchId);
		 			jrInTemp.setField("YD_CARLD_WRK_BOOK_ID", ydWbookId);
		 			jrInTemp.setField("YD_CARLD_ARR_DT"		, commUtils.getDateTime14());
					
					if ("P".equals(transEquipType) && "9".equals(ydCarWrkGp)) {
						jrInTemp.setField("YD_CAR_PROG_STAT"		, "B" );		//하차도착상태
					}else{
						
						int intTransOrdSeqNo = StringHelper.parseInt(newTransOrdSeqNo, 0);
						if (intTransOrdSeqNo > 999000) {
							//반품,회송,부분하차
							jrInTemp.setField("YD_CAR_PROG_STAT"	, "B" );		//하차도착상태
							jrInTemp.setField("YD_CARUD_WRK_BOOK_ID", ydWbookId); //야드하차작업예약ID
							jrInTemp.setField("YD_CARLD_WRK_BOOK_ID", ""); //상단에 설정한 상차작업예약ID clear
							jrInTemp.setField("YD_CARUD_ARR_DT"		, commUtils.getDateTime14()); //야드하차도착일시
						} else {	
						
							jrInTemp.setField("YD_CAR_PROG_STAT"	, "2" );		//상차도착상태
						}
					}
					jrInTemp.setField("SPOS_WLOC_CD",     		newWlocCd);	//발지개소코드
					
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch
					UPDATE TB_YD_CARSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , DEL_YN           = NVL(:V_DEL_YN          , DEL_YN)
					     --하차
					     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT    , YD_CAR_PROG_STAT)
					     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
					     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC   , YD_CARUD_STOP_LOC)
					     , YD_CARUD_ARR_DT      = NVL(:V_YD_CARUD_ARR_DT     , YD_CARUD_ARR_DT)
					     , YD_PNT_CD3           = NVL(:V_YD_PNT_CD3          , YD_PNT_CD3)
					     --상차
					     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID)
					     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC   , YD_CARLD_STOP_LOC)
					     , YD_CARLD_ARR_DT      = NVL(:V_YD_CARLD_ARR_DT     , YD_CARLD_ARR_DT)
					     , YD_PNT_CD1           = NVL(:V_YD_PNT_CD1          , YD_PNT_CD1)
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  

					*/ 
					commDao.update(jrInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 상차도착");
				
				}
				//------------------------------------------------------------------------------------------------------------
		    	//	재료정보 조회 (2단,1단 순)
		    	//------------------------------------------------------------------------------------------------------------				   
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				jrParam1.setResultCode(logId);	//Log ID
	 			jrParam1.setResultMsg(methodNm);	//Log Method Name
				jrParam1.setField("TRANS_ORD_DATE2"	, newTransOrdDate);
				jrParam1.setField("TRANS_ORD_SEQNO2" 	, newTransOrdSeqNo);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockOfCarLoad 
				SELECT 
				       C.STACK_COL_GP||C.STACK_BED_GP|| C.STACK_LAYER_GP  AS UP_LOC
				     , C.STACK_LAYER_GP
				     , A.STOCK_ID
				     , B.YD_SCH_CD
				     , DECODE(LENGTH(A.TRANS_WORD_NO), 10, A.TRANS_WORD_NO
				     , SUBSTR(A.TRANS_WORD_NO, 1, 8) || '0' || SUBSTR(A.TRANS_WORD_NO, 9,1)) AS TRANS_WORD_DATE_NO
				     , B.YD_WBOOK_ID
				     , B.DEL_YN
				     , A.CR_FRTOMOVE_GP --
				  FROM USRYMA.TB_YM_STOCK  A
				     , USRYMA.TB_YM_WRKBOOK  B
				     , USRYMA.TB_YM_WRKBOOKMTL  D
				     , USRYMA.TB_YM_STACKLAYER C
				 WHERE B.YD_WBOOK_ID=D.YD_WBOOK_ID
				   AND A.STOCK_ID = D.STOCK_ID
				   AND D.STOCK_ID = C.STOCK_ID
				   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DATE2
				   AND A.TRANS_ORD_SEQNO2 =:V_TRANS_ORD_SEQNO2
				   AND B.DEL_YN = 'N'
				   AND D.DEL_YN = 'N'
				 ORDER BY C.STACK_LAYER_GP DESC, B.YD_WBOOK_ID */
				JDTORecordSet jsCarMtl = commDao.select(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockOfCarLoad", logId, methodNm, "재료정보 조회");	
				
				//if (jsCarMtl.size()>0) {
				//	szCR_FRTOMOVE_GP = jsCarMtl.getRecord(0).getFieldString("CR_FRTOMOVE_GP");
				//}
				
				if ("9".equals(ydCarWrkGp) && "TR".equals(newYdCarKind)) {
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+szYD_CARPNT_CD+"] 2냉연 TR은 크레인 스케줄을 도착전문을 받고서 처리 한다.", "SL");	

					szCHK_YN = "H";
					
				} else {
					//------------------------------------------------------------------------------------------------------------
			    	//	차량 크레인스케줄 호출(크레인 스케줄 , TO위치 결정 ) 
			    	//------------------------------------------------------------------------------------------------------------
					String szMsg= "차량스케줄ID[" + newYdCarSchId + "]의 출하차량[차량번호:" + newYdCarNo + 
							", 카드번호:" + newYdCardNo + ", 운송지시일자:" + newTransOrdDate + ", 운송지시순번:" + newTransOrdSeqNo + "]에 대해 차량도착 전문전송 시작";
					commUtils.printLog(logId, szMsg, "SL");
					
					// 차량예정정보 송신 (YMA7L008)
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
		 			jrParam.setField("MODIFIER"		, modifier  ); //수정자
					jrParam.setField("YD_CAR_SCH_ID", newYdCarSchId); //야드차량스케쥴ID
					jrParam.setField("SEARCH_FLAG"  , "2");  //1:상차도, 2:차량스케쥴 ID	
					jrRtn = commUtils.addSndData(jrRtn, ymComm.procCarPlanInfo(jrParam));

				
					/**********************************************************
					* Crane스케줄 호출
					*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
					*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
					**********************************************************/
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn 
					SELECT YD_CARPNT_CD
					      ,YD_STK_COL_ACT_STAT
					      ,YD_CAR_USETYPE_GP
					      ,WLOC_CD
					      ,YD_PNT_CD
					      ,NVL(YD_FRM_YN,'N') AS YD_FRM_YN
					FROM   TB_YD_CARPOINT
					WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP */
					jrParam.setField("YD_STK_COL_GP" , newYdStackColGp); 	
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarPntFrmYn", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 "); 
					if ("N".equals(rsResult.getRecord(0).getFieldString("YD_FRM_YN"))) {
						
						if ("2".equals(szYD_GP)) {
							
							for(int i = 0; i < jsCarMtl.size(); i++) {	
								// 크레인스케줄편성 기동
								jrParam.setField("YD_GP"			, szYD_GP); 		    		
								jrParam.setField("YD_SCH_CD"		, ydSchCd); //스케줄코드		    		
								jrParam.setField("YD_EQP_ID"		, ydEqpId); //설비ID		    		
								jrParam.setField("YD_WBOOK_ID"		, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //작업예약ID
								jrParam.setField("TRANS_WORD_DATE"	, newTransOrdDate);
								jrParam.setField("TRANS_WORD_SEQNO" , newTransOrdSeqNo);
								jrParam.setField("CARD_NO"			, newYdCardNo);
								jrParam.setField("CAR_NO"			, newYdCarNo);

								jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrParam));
							} 
							
						} else if ("3".equals(szYD_GP)) {
						
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
							
							//크레인 스케줄 기동 YMYMJ303 호출
							jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ303"); 
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
							jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
							
							int pcnt = 0;
							
							//2단 적치된 대상 
							for(int i = 0; i < jsCarMtl.size(); i++) {
								
								if ("02".equals(jsCarMtl.getRecord(i).getFieldString("STACK_LAYER_GP"))) {
									
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								}
							}
							
							//1단 적치된 대상 
							for(int i = 0; i < jsCarMtl.size(); i++) {
								
								if ("01".equals(jsCarMtl.getRecord(i).getFieldString("STACK_LAYER_GP"))) {
									
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
									
								}
							}
							
							//작업예약 순서대로 스케줄 호출 
							//for(int i = 0; i < jsCarMtl.size(); i++) {
							//		
							//	jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
							//}

							jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));
							
							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						}
					}
					
					szCHK_YN="Y";
										
					szMsg= "차량스케줄ID[" + newYdCarSchId + "]의 출하차량[차량번호:" + newYdCarNo + 
							", 카드번호:" + newYdCardNo + ", 운송지시일자:" + newTransOrdDate + ", 운송지시순번:" + newTransOrdSeqNo + "]에 대해 차량도착 전문전송 완료 , szCHK_YN:"+szCHK_YN;
					commUtils.printLog(logId, szMsg, "SL");
				}
				
				if("9".equals(ydCarWrkGp) && "TR".equals(newYdCarKind))	{
					
					commUtils.printLog(logId, "2냉연 TR은 도착전문을 받고서 야드저장위치제원(YMA7L001)송신 처리 한다.", "SL");
					
				} else {
					//----------------------------------------------------------------------
					// 야드저장위치제원(YMA7L001) 전문전송
					//----------------------------------------------------------------------
					jrParam.setField("YD_INFO_SYNC_CD"	, "3" ); 			//야드정보동기화코드(3:열)
					jrParam.setField("STACK_COL_GP"   	, newYdStackColGp); //야드적치열구분
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L001", jrParam));
				}
				
				if ("Y".equals(szCHK_YN) || "H".equals(szCHK_YN)) {
					
					//------------------------------------------------------------------------------------------------------------
			    	//	입동TC 전송
			    	//------------------------------------------------------------------------------------------------------------
					JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
					
					// PIDEV
//					if("Y".equals(sApplyYnPI)) {						
						recInTemp.setField("MQ_TC_CD"				, "M10YDLMJ1061");
						recInTemp.setField("MQ_TC_CREATE_DDTT"		, commUtils.getDateTime14());						
						recInTemp.setField("TRN_REQ_DATE"			, newTransOrdDate);
						recInTemp.setField("TRN_REQ_SEQ" 			, newTransOrdSeqNo);
						recInTemp.setField("CAR_NO"					, newYdCarNo);
						recInTemp.setField("YD_GP"					, "3");
						recInTemp.setField("DIST_GOODS_GP"			, "H");						
						if ("P".equals(transEquipType)) {
							recInTemp.setField("SCH_YN"     , "Y");
						} else {
							recInTemp.setField("SCH_YN"     , "N");
						}
						recInTemp.setField("BAYIN_DDTT"		        , commUtils.getDateTime14());
						recInTemp.setField("WLOC_CD"				, newWlocCd);
						recInTemp.setField("YD_PNT_CD"				, newYdPntCd);
						recInTemp.setField("YD_CARPNT_CD"			, newYdCarPntCd);	
						recInTemp.setField("LOAN_PULLOUT_ABLE_YN"   , "Y");						
						
//					} else {
//						if ("P".equals(transEquipType)) {
//							
//							jrParam.setField("TRANS_ORD_DATE2",  commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE")));
//							jrParam.setField("TRANS_ORD_SEQNO2", commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
//							
//							// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
//							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockTransOrdDateAB", logId, methodNm, "운송지싱일자,순번으로 STOCK테이블에서 CR_FRTOMOVE_GP(냉연이송구분 )조회");
//							if(rsResult.size()>0) {
//								szCR_FRTOMOVE_GP = StringHelper.evl(rsResult.getRecord(0).getFieldString("CR_FRTOMOVE_GP"),"");
//							}
//							//-------------------------------------------------------------------------
//							
//							
//							recInTemp.setField("JMS_TC_CD"			, "YDDMR070");
//							recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
//							recInTemp.setField("TC_CODE"			, "YDDMR070");
//							recInTemp.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
//							recInTemp.setField("CR_FRTOMOVE_GP"     , szCR_FRTOMOVE_GP);
//						} else {
//							recInTemp.setField("JMS_TC_CD"			, "YDDMR028");
//							recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
//							recInTemp.setField("TC_CODE"			, "YDDMR028");
//							recInTemp.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
//						}
//						recInTemp.setField("TRANS_WORD_DATE"		, newTransOrdDate);
//						recInTemp.setField("TRANS_WORD_SEQNO" 		, newTransOrdSeqNo);
//						recInTemp.setField("BAYIN_DDTT"		        , commUtils.getDateTime14());
//
//						recInTemp.setField("CARD_NO"				, newYdCardNo);
//						recInTemp.setField("CAR_NO"					, newYdCarNo);
//						recInTemp.setField("WLOC_CD"				, newWlocCd);
//						recInTemp.setField("YD_PNT_CD"				, newYdPntCd);
//						recInTemp.setField("YD_CARPNT_CD"			, newYdCarPntCd);	
//						recInTemp.setField("LOAN_PULLOUT_ABLE_YN"   , "Y");
//						
//					}
					
					jrRtn = commUtils.addSndData(jrRtn, recInTemp);
					
					
					//------------------------------------------------------------------------------------------------------------
					//입동지시 대상차량에 입동지시 송신여부 셋팅
					//------------------------------------------------------------------------------------------------------------
//PIDEV					
//					if("Y".equals(sApplyYnPI)) {	
						jrParam.setField("YD_CAR_RCPT_CHK_YN"	, "E"); 
						jrParam.setField("CAR_NO"				, newYdCarNo);
						jrParam.setField("TRANS_ORD_DATE"		, newTransOrdDate);
						jrParam.setField("TRANS_ORD_SEQNO"		, newTransOrdSeqNo);
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarschYdCarRcptChkYn_PIDEV 
						UPDATE TB_YD_CARSCH 
						   SET YD_CAR_RCPT_CHK_YN = :V_YD_CAR_RCPT_CHK_YN
						 WHERE CAR_NO             = :V_CAR_NO
						   AND TRANS_ORD_DATE     = :V_TRANS_ORD_DATE
						   AND TRANS_ORD_SEQNO    = :V_TRANS_ORD_SEQNO
						*/   
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarschYdCarRcptChkYn_PIDEV", logId, methodNm, "차량스케줄에 입동지시 송신여부 셋팅");
						
//					} else {
//						jrParam.setField("YD_CAR_RCPT_CHK_YN"	, "E"); 
//						jrParam.setField("CAR_NO"				, newYdCarNo);
//						jrParam.setField("CARD_NO"				, newYdCardNo);
//						jrParam.setField("TRANS_ORD_DATE"		, newTransOrdDate);
//						jrParam.setField("TRANS_ORD_SEQNO"		, newTransOrdSeqNo);
//						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarschYdCarRcptChkYn 
//						UPDATE TB_YD_CARSCH 
//						   SET YD_CAR_RCPT_CHK_YN = :V_YD_CAR_RCPT_CHK_YN
//						 WHERE CAR_NO             = :V_CAR_NO
//						   AND CARD_NO            = :V_CARD_NO
//						   AND TRANS_ORD_DATE     = :V_TRANS_ORD_DATE
//						   AND TRANS_ORD_SEQNO    = :V_TRANS_ORD_SEQNO
//						*/   
//						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarschYdCarRcptChkYn", logId, methodNm, "차량스케줄에 입동지시 송신여부 셋팅");
//					}	
					
					if("Y".equals(szCHK_YN)) {

						// 입동지시 미리 주기 적용여부 확인
						String sAPPLY060 = ymComm.BCoilApplyYn("APP060","3","PREINWO_YN");
						commUtils.printLog(logId,  ">>> 입동지시 미리 주기 적용여부 (Y:적용, N:비적용) :" + sAPPLY060, "SL");
						if (sAPPLY060.equals("Y")) {
							
							jrParam.setField("YD_CARPNT_CD" , szYD_CARPNT_CD);
							//사용금지 포인트갯수 조회
							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointCHK3", logId, methodNm, "사용금지 포인트갯수 조회");
							
							if(rsResult.size() > 0) { 
								
								String sJ00002_YN 			= rsResult.getRecord(0).getFieldString("J00002_YN");
								int    iCARPOINT_CLOSE_CNT 	= rsResult.getRecord(0).getFieldInt("CARPOINT_CLOSE_CNT");
								int    iE_CHK_CNT 			= rsResult.getRecord(0).getFieldInt("E_CHK_CNT");
								String sFlag_YN             = "Y";
							
								commUtils.printLog(logId, "[입동가능시] 전체입동제한(J00002) :"+sJ00002_YN+" ,해당통로 사용불가 포인트 갯수"+": ["+iCARPOINT_CLOSE_CNT+"] ,입동지시 받은 차량갯수 ["+iE_CHK_CNT+"]******* ", "SL");	

								if("Y".equals(sJ00002_YN)) {
									//전체입동제한  여기서 종료
									sFlag_YN = "N";
								} else if(iCARPOINT_CLOSE_CNT != 1) {
									//해당 통로 사용불가 포인트 수가 1 이 아니면 여기서 종료
									sFlag_YN = "N";
								} else if(iE_CHK_CNT >= 2) {
									//이미 입동지시 받은 차량스케줄id가 2 이상이면 여기서 종료
									sFlag_YN = "N";
								}
								
								if("Y".equals(sFlag_YN)) {
									
									JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
									jrTemp.setResultCode(logId);	//Log ID
									jrTemp.setResultMsg(methodNm);	//Log Method Name
									jrTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 
									jrTemp.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
									jrTemp.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD);		//입동포인트
									
									jrRtn = commUtils.addSndData(jrRtn, jrTemp);
								}
							}
						}
					}
					
				}
				
		 		if ("Y".equals(sAPPLY1)) {
					/***** 차량log Claer ****/
					JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
					jrLogMsg.setResultCode(logId);	//Log ID
					jrLogMsg.setResultMsg(methodNm);	//Log Method Name
					jrLogMsg.setField("MODIFIER"		, modifier); //수정자 셋팅
					jrLogMsg.setField("YD_CAR_SCH_ID"	, newYdCarSchId); //차량스케쥴
					
					EJBConnector ejbConnLog = new EJBConnector("default", "YmCommSeEJB", this);
					ejbConnLog.trx("updCarErrorLogClear", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });

		 		}
			}				

			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of rcvYMYMJ662
	
	/**
	 * 오퍼레이션명 : 차량포인트 통합관리 (기존형태 유지 yd와 동일)
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */     	
	public boolean YmCarPointinforeg(String chk 
									, String s_CAR_NO
									, String s_TRN_EQP_CD
									, String s_STACK_COL_GP
									, String szARR_WLOC_CD 
									, String szARR_YD_PNT_CD
									, String s_STAT
									, String logId
									, String mthdNm
									)throws DAOException {
		
		boolean isSuccess = false;
		int iSeq=0;
		String stkQueryId ="";
		String szMsg ="";
		//YdStockDAO ydStockDAO = new YdStockDAO();
		String methodNm =  "[YmCommCarMvSeEJB.YmCarPointinforeg] < " + mthdNm;
		
		try{

			commUtils.printLog(logId, methodNm, "S+");
			szMsg = "▣▣▣▣차량포인트 통합관리(START):"+chk+","+s_CAR_NO+","+s_TRN_EQP_CD+","+s_STACK_COL_GP+","+szARR_WLOC_CD+","+szARR_YD_PNT_CD+","+s_STAT+"▣▣▣▣▣" ;
    		szMsg =  "▣▣▣▣차량포인트 통합관리(START):통합작업 시작 ▣▣▣▣▣";
			commUtils.printLog(logId, szMsg, "ST");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//쿼리돌릴Param담을 변수
			
			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "YmCommCarMvSeEJB.YmCarPointinforeg", "APPPI0", "*", "*");
			
			if ("1".equals(chk)) {
				//설비코드로 초기화 하는 경우(구내운송)			 			
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdate
				UPDATE TB_YD_CARPOINT
				   SET TRN_EQP_CD=null
				     , YD_STK_COL_ACT_STAT=DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointin'
				 WHERE TRN_EQP_CD=:V_TRN_EQP_CD
				   AND MOD_DDTT<>sysdate
				*/
				jrParam.setField("STAT"				,s_STAT);
				jrParam.setField("TRN_EQP_CD"		,s_TRN_EQP_CD);
				
				iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdate", logId, methodNm, "설비코드로 초기화 하는 경우(구내운송)");
				
			}else if ("2".equals(chk)) {
				//저장위치로 초기화 하는 경우(구내운송)
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointstackcolgpupdateCT
				UPDATE TB_YD_CARPOINT
				   SET TRN_EQP_CD=null
				     , YD_STK_COL_ACT_STAT=DECODE(CARD_NO,NULL,(DECODE(YD_STK_COL_ACT_STAT,'N','N',:V_STAT)),YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointCT'
				 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP
				*/ 
				jrParam.setField("STAT"				,s_STAT);
				jrParam.setField("YS_STK_COL_GP"	,s_STACK_COL_GP); //쿼리 파라메터 수정 후 삭제해야 함 
				jrParam.setField("YD_STK_COL_GP"	,s_STACK_COL_GP);
				
				iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointstackcolgpupdateCT", logId, methodNm, "저장위치로 초기화 하는 경우(구내운송)");

			}else if ("3".equals(chk)) {
				//저장위치로 차량 포인트 예약 하는 경우(구내운송)
				 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdateC
				UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , TRN_EQP_CD =:V_TRN_EQP_CD
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointCP'
				 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP
				*/ 
				jrParam.setField("STAT"				,s_STAT);
				jrParam.setField("TRN_EQP_CD"		,s_TRN_EQP_CD);
				jrParam.setField("YS_STK_COL_GP"	,s_STACK_COL_GP); //쿼리 파라메터 수정 후 삭제해야 함 
				jrParam.setField("YD_STK_COL_GP"	,s_STACK_COL_GP);

				iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdateC", logId, methodNm, "저장위치로 차량 포인트 예약 하는 경우(구내운송)");
				
			} else if ("4".equals(chk)) {
				//개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)
				 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointWlocpntupdate
				UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , TRN_EQP_CD = :V_TRN_EQP_CD
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointin'
				 WHERE WLOC_CD=:V_WLOC_CD
				   AND YD_PNT_CD=:V_YD_PNT_CD
				*/
				jrParam.setField("STAT"				,s_STAT);
				jrParam.setField("TRN_EQP_CD"		,s_TRN_EQP_CD);
				jrParam.setField("WLOC_CD"			,szARR_WLOC_CD);
				jrParam.setField("YD_PNT_CD"		,szARR_YD_PNT_CD);
				
				iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointWlocpntupdate", logId, methodNm, "개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)");
				
			} else if ("A".equals(chk)) {
				
				//설비코드로 초기화 하는 경우(출하)
//				if ("Y".equals(sApplyYnPI)) {
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdatePT_PIDEV
					UPDATE TB_YD_CARPOINT
					   SET CARD_NO = NULL
					     , CAR_NO  = NULL
					     , YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'N', 'N',:V_STAT)
					     , MOD_DDTT = SYSDATE
					     , MODIFIER ='CarPointPT'
					 WHERE CAR_NO = :V_CAR_NO
					*/
					jrParam.setField("STAT", s_STAT);
					jrParam.setField("CAR_NO", s_CAR_NO);
					iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdatePT_PIDEV", logId, methodNm, "설비코드로 초기화 하는 경우(출하)");
//				} else {
//					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdatePT
//					UPDATE TB_YD_CARPOINT
//					   SET CARD_NO = NULL
//					     , CAR_NO  = NULL
//					     , YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'N', 'N',:V_STAT)
//					     , MOD_DDTT = SYSDATE
//					     , MODIFIER ='CarPointPT'
//					 WHERE CARD_NO=:V_TRN_EQP_CD   
//					     AND CAR_NO  =nvl(:V_CAR_NO,CAR_NO  )
//					*/
//					jrParam.setField("STAT", s_STAT);
//					jrParam.setField("TRN_EQP_CD", s_TRN_EQP_CD); //'A' 일경우는  s_TRN_EQP_CD 변수에 CARD_NO 가 넘어온다. 
//					jrParam.setField("CAR_NO", s_CAR_NO);
//					iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdatePT", logId, methodNm, "설비코드로 초기화 하는 경우(출하)");
//				}
				
			}else if ("B".equals(chk)) {
				//저장위치로 초기화 하는 경우(출하)
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointstackcolgpupdateC
				UPDATE TB_YD_CARPOINT
				   SET CARD_NO=null
				     , CAR_NO=NULL
				     , YD_STK_COL_ACT_STAT=DECODE(TRN_EQP_CD,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointC'
				 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP 
				*/ 
				jrParam.setField("STAT"				,s_STAT);
				jrParam.setField("YS_STK_COL_GP"	,s_STACK_COL_GP); //쿼리 파라메터 수정 후 삭제해야 함 
				jrParam.setField("YD_STK_COL_GP"	,s_STACK_COL_GP);
				
				iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointstackcolgpupdateC", logId, methodNm, "저장위치로 초기화 하는 경우(출하)");
				
			}else if ("C".equals(chk)) {
				//저장위치로 차량 포인트 예약 하는 경우(출하)
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdateC2
	    		UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , CAR_NO  =:V_CAR_NO
				     , CARD_NO =:V_TRN_EQP_CD
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointC'
				 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP
	    		*/ 
				jrParam.setField("STAT"				,s_STAT);
				jrParam.setField("CAR_NO"			,s_CAR_NO);
				jrParam.setField("TRN_EQP_CD"		,s_TRN_EQP_CD);   //'C' 일경우는  s_TRN_EQP_CD 변수에 CARD_NO 가 넘어온다. 
				jrParam.setField("YS_STK_COL_GP"	,s_STACK_COL_GP); //쿼리 파라메터 수정 후 삭제해야 함 
				jrParam.setField("YD_STK_COL_GP"	,s_STACK_COL_GP);
				
				iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdateC2", logId, methodNm, "저장위치로 차량 포인트 예약 하는 경우(출하)");
				
			} else if ("D".equals(chk)) {
				 /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointWlocpntupdatePT
				UPDATE TB_YD_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , CAR_NO  =:V_CAR_NO
				     , CARD_NO = :V_TRN_EQP_CD
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointPT'
				 WHERE WLOC_CD=:V_WLOC_CD
				   AND YD_PNT_CD=:V_YD_PNT_CD
				 */  
				jrParam.setField("STAT"				,s_STAT);
				jrParam.setField("CAR_NO"			,s_CAR_NO);
				jrParam.setField("TRN_EQP_CD"		,s_TRN_EQP_CD);    //'D' 일경우는  s_TRN_EQP_CD 변수에 CARD_NO 가 넘어온다. 
				jrParam.setField("WLOC_CD"			,szARR_WLOC_CD);
				jrParam.setField("YD_PNT_CD"		,szARR_YD_PNT_CD);
				
				iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointWlocpntupdatePT", logId, methodNm, "개소코드,포인트로 차량 포인트 예약 하는 경우(출하)");
				
			}  
	 
	    	szMsg =  "▣▣▣▣차량포인트 통합관리(END)COUNT:"+iSeq+"▣▣▣▣▣";
			isSuccess = true;
			commUtils.printLog(logId, methodNm, "S-");	
	    } catch (DAOException daoe) {
	        throw daoe;
	    } catch (Exception e) {
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	} // End of YmCarPointinforeg()		
	
	/**
	 * 오퍼레이션명 : 대기장 도착 MSG 생성
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */ 
	public String getCarMsg(String s_STL_APPEAR_GP , String s_STACK_YD_GP, String  s_STACK_BAY_GP, String  szTRN_EQP_GP,String szARR_WLOC_CD,String szYD_POINT_CD, String logId, String methodNm) {
    	
	    JDTORecordSet rsResult    	= null;
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();//Query 실행시 파라메터 전달용 JDTORecord 
		
    	try{
    		//개소코드 체크
			if (szARR_WLOC_CD.equals("")) {
				return "입력된 개소코드가 존재 안함.";
			}

			
			//코일야드 만 해당됨
			if (	szARR_WLOC_CD.equals("D2Y44")||szARR_WLOC_CD.equals("D2Y45")||
				szARR_WLOC_CD.equals("D3Y41")||szARR_WLOC_CD.equals("D3Y42")||
				szARR_WLOC_CD.equals("DJY21")||szARR_WLOC_CD.equals("DJY22")||
				szARR_WLOC_CD.equals("DJY1E")) {
				 //-------------------------------------코일야드-------------------------------------------
			
	    		if (s_STL_APPEAR_GP.equals("Y")) {
	    			//******************************제품 ***************************************
	    			//포인트코드 체크
	    			if (szYD_POINT_CD.equals("")) {
	    				return "입력된 포인트코드가 존재 안함.";
	    			}
	    			
	    			//개소코드 체크
					jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
					jrParam.setField("YD_PNT_CD"	, szYD_POINT_CD);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListUnloadEndpointGoods_chk", logId, methodNm, "getCarMsg - COIL제품 - 개소코드체크");
					if (rsResult.size() <= 0) {
		    			return s_STACK_BAY_GP+"동  지시개소코드가 야드와 틀림.";
					}
		    		
		    		//포인트 체크
					jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
					jrParam.setField("YD_PNT_CD"	, szYD_POINT_CD);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListUnloadEndpointGoods_chk2", logId, methodNm, "getCarMsg - COIL제품 - 포인트체크");
					if (rsResult.size() > 0) {
		    			return s_STACK_BAY_GP+"동 개소지의 야드포인트가 사용불가.";
					}
				 
		    		//다른차량 체크
					jrParam.setField("WLOC_CD"		, szARR_WLOC_CD);
					jrParam.setField("YD_PNT_CD"	, szYD_POINT_CD);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListUnloadEndpointGoods_chk4", logId, methodNm, "getCarMsg - COIL제품 - 다른차량체크");
					if (rsResult.size() > 0) {
		    			return s_STACK_BAY_GP+"동 해당개소에 다른 차량 점유.";
					}
	    			
	    		}else{
	    			//******************************소재 ***************************************
		    		//목적동 체크
					if (s_STACK_BAY_GP.equals("")) {
						return "목적동OR이송대상이 존재 안함.";
					}
					 
					//야드 체크
					if (s_STACK_YD_GP.equals("")) {
						return "입력된 야드코드가 존재 안함.";
					}
					
					//TR/PT 체크
					if (szTRN_EQP_GP.equals("")) {
						return "입력된 장비구분(TR/PT)가 존재 안함.";
					}
					
					//개소코드 체크
					jrParam.setField("YD_GP"	, s_STACK_YD_GP);
					jrParam.setField("BAY_GP"	, s_STACK_BAY_GP);
					jrParam.setField("SECT_GP"	, szTRN_EQP_GP);
					jrParam.setField("WLOC_CD"	, szARR_WLOC_CD);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListUnloadEndpoint_chk", logId, methodNm, "getCarMsg - COIL소재 - 개소코드체크");
					if (rsResult.size() <= 0) {
		    			return s_STACK_BAY_GP+"동  지시개소코드가 야드와 틀림.";
					}
		    		
		    		//포인트 체크
					jrParam.setField("YD_GP"	, s_STACK_YD_GP);
					jrParam.setField("BAY_GP"	, s_STACK_BAY_GP);
					jrParam.setField("SECT_GP"	, szTRN_EQP_GP);
					jrParam.setField("WLOC_CD"	, szARR_WLOC_CD);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListUnloadEndpoint_chk2", logId, methodNm, "getCarMsg - COIL소재 - 포인트체크");
					if (rsResult.size() > 0) {
		    			return s_STACK_BAY_GP+"동 개소지의 야드포인트가 사용불가.";
					}
				 
		    		//다른차량 체크
					jrParam.setField("YD_GP"	, s_STACK_YD_GP);
					jrParam.setField("BAY_GP"	, s_STACK_BAY_GP);
					jrParam.setField("SECT_GP"	, szTRN_EQP_GP);
					jrParam.setField("WLOC_CD"	, szARR_WLOC_CD);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListUnloadEndpoint_chk3", logId, methodNm, "getCarMsg - COIL소재 - 다른차량 체크");
					if (rsResult.size() > 0) {
		    			return s_STACK_BAY_GP+"동 해당개소에 다른 차량 점유.";
					}
 
	    		}

	    		//-------------------------------------코일야드-------------------------------------------
			}else{
				//-------------------------------------슬라브야드-------------------------------------------
				return "";
				//-------------------------------------슬라브야드-------------------------------------------
			}
			//-------------------------------------------------------------------------
    		 
			return "시스템 담당자 확인 요망.";
    	} catch (DAOException daoe) {
	        throw daoe;
	    } catch (Exception e) {
	        throw new EJBServiceException(e);
	    }
	}	
	/**
	 *      [A] 오퍼레이션명 : 출하차량출발실적 처리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procOutCarLevWr(JDTORecord rcvMsg)throws JDTOException  {
		String methodNm = "코일제품출하차량출발실적[YmCommCarMvSeEJB.procOutCarLevWr] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		String szLogMsg = "";
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();        
		JDTORecord jrParam = JDTORecordFactory.getInstance().create(); 
	    String szMsg	= "";		
	    String ydCarDiffYn = "N"; //위치 동일차량여부		
		
	    try{
		
				
	    	commUtils.printLog(logId, methodNm, "S+");	
			commUtils.printParam(logId + "출하차량출발실적 처리 수신 ", rcvMsg);
			
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String transOrdDate = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"));  //운송지시일자
	    	String transOrdSeqNo= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
	    	String ydCarNo  	= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));          //차량번호
	    	String ydCardNo     = commUtils.trim(rcvMsg.getFieldString("CARD_NO"));         //카드번호
	    	String sposWlocCd   = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));    //발지개소코드
	    	String sposYdPntCd  = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));  //발지포인트코드
	    	String ydCarPntCd   = commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"));   	//PALLET 출하 차량포인트
	    	String modifier  	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
	    	if ("".equals(modifier)) { modifier = msgId; }
	    	
	    	if (transOrdDate.equals("")) {
	    		szLogMsg="운송지시일자가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("TRANS_ORD_DATE Error");
	    	}
	    	
	    	if (transOrdSeqNo.equals("")) {
	    		szLogMsg="운송지시순번이 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("TRANS_ORD_SEQNO Error");
	    	}
	    	
	    	if (ydCarNo.equals("")) {
	    		szLogMsg="차량번호가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("CAR_NO Error");
	    	}
	    	
	    	if (sposWlocCd.equals("")) {
	    		szLogMsg="발지개소코드가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
	    	}
	    	
	    	if (sposYdPntCd.equals("")) {
	    		szLogMsg="발지포인트코드가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("SPOS_YD_PNT_CD Error");
	    	}
	    	
			/**********************************************************
			* 2. PALLET 출하 차량여부
			**********************************************************/
	    	if (!ydCarPntCd.equals("")) {
	    		jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YD_CARPNT_CD"	, ydCarPntCd);
				
				szMsg="["+methodNm+"] PALLET 출하 차량포인트 조회  시작";
				commUtils.printLog(logId, szMsg, "SL");	
				
				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.selectQueryId_0102*/               
		                /*SELECT YD_CARPNT_CD
					      ,YD_STK_COL_ACT_STAT
					      ,YD_CAR_USETYPE_GP
					      ,YD_GP
					      ,YD_BAY_GP
					      ,YD_STK_COL_GP
					      ,TRN_EQP_CD
					      ,CAR_NO
					      ,CARD_NO
					      ,WLOC_CD
					      ,YD_PNT_CD
					      ,YD_CARPNT_DESC
					FROM   TB_YD_CARPOINT
					WHERE  YD_CARPNT_CD = :V_YD_CARPNT_CD  */        
				
				
				JDTORecordSet loadCarPnt = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.selectQueryId_0102", logId, methodNm, "PALLET 출하 차량포인트 조회");
				if (loadCarPnt.size() <= 0 ) {
					szMsg="["+methodNm+"] PALLET 출하 차량포인트 조회 시  SELECT Error ::  DO NOT EXIST"  ;
					commUtils.printLog(logId, szMsg, "SL");	
					return jrRtn ;
				}else{
					sposWlocCd 	= commUtils.trim(loadCarPnt.getRecord(0).getFieldString("WLOC_CD")); 
					sposYdPntCd	= commUtils.trim(loadCarPnt.getRecord(0).getFieldString("YD_PNT_CD")); 
				}
	    	}	    	
	    	
	    	//발지위치정보로 출발위치 Clear
	    	//열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	//업데이트값이 없다면 그냥 종료
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
    		jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("WLOC_CD",   sposWlocCd);
			jrParam.setField("YD_PNT_CD", sposYdPntCd);
	    	
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkcolWLocCdandPntCd
			SELECT A.STACK_COL_GP   AS STACK_COL_GP
			     , A.CAR_NO         AS CAR_NO                               
			     , A.CARD_NO        AS CARD_NO                             
			     , A.WLOC_CD        AS WLOC_CD                             
			     , A.YD_PNT_CD      AS YD_PNT_CD   
			     , B.YD_CARPNT_CD   AS YD_CARPNT_CD
			     , B.YD_STK_COL_ACT_STAT AS YD_STK_COL_ACT_STAT
			  FROM TB_YM_STACKCOL A   
			     , TB_YD_CARPOINT B
			 WHERE B.YD_STK_COL_GP = A.STACK_COL_GP
			   AND A.WLOC_CD LIKE   SUBSTR(:V_WLOC_CD,1,3)||'%'
			   AND A.YD_PNT_CD = :V_YD_PNT_CD
	    	 */	    	
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 

	    	if (jsStkCol == null || jsStkCol.size() <= 0) {
				szLogMsg = methodNm + "발지개소["+sposWlocCd+"] 및 포인트 코드["+sposYdPntCd+"]가 타공정코드가 아니고 대기장입니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jrRtn ;
				
	    	} else {
	    		
		    	//열구분을 조회(도착지)
		    	String ydCarldLevLoc    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("STACK_COL_GP")); 
		    	String ydCarPntCdChk 	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CARPNT_CD")); 
		    	String ydCarNoChk 	    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("CAR_NO"));
		    	String ydStackColActStat= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
		    	
		    	if (!ydCarNoChk.equals(ydCarNo)) {
		 		   /**********************************************************
					* 다른 차량이 존재 하는 경우 
					**********************************************************/
		    		szMsg="["+methodNm+"] 해당 위치에 다른 차량이 존재 합니다. 포인트 차량:"+ydCarNoChk + "  취소대상 차량:"+ ydCarNo;
		    		commUtils.printLog(logId, szMsg, "SL");	
		    		ydCarDiffYn = "Y";
		    	} else {
		    		/**********************************************************
					* 동일차량존재 
					**********************************************************/
		    		
		    		//---------------------------------------------------------------------------------------
    				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
    				sndL2Msg.setResultCode(logId);	//Log ID
    				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
    				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "3"                         ); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP"			, "I"                         ); //전문구분
    				sndL2Msg.setField("STACK_COL_GP"    , ydCarldLevLoc);
    				sndL2Msg.setField("STACK_BED_GP"    , "");
    	 
    				//전송 Data 생성
    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L001", sndL2Msg));		
    				szMsg="[" + methodNm + "] 저장위치 : 코일야드L2 로 송신 열구분[" + ydCarldLevLoc + "] - 저장위치 제원 : 코일야드L2 로 송신 호출 "+jrRtn.size();
    				commUtils.printLog(logId, szMsg, "SL");
		    		//---------------------------------------------------------------------------------------
		    		
		    		
		    		//사용불가로 되어있으면 상태값을 바꾸지 않는다
		    		if (!"N".equals(ydStackColActStat)) {
		    			ydStackColActStat = "C";
		    		}		    		
		    		/********************************
					 * 적치열 상태 비활성화등록
					 *******************************/
		    		szLogMsg = methodNm + "출발야드의 적치열["+ydCarldLevLoc+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("STACK_COL_GP"			, ydCarldLevLoc);
			    	jrParam.setField("STACK_COL_ACTIVE_STAT", ydStackColActStat);
			    	jrParam.setField("YD_CAR_USE_GP"		, "");
			    	jrParam.setField("TRN_EQP_CD"			, "");
			    	jrParam.setField("CAR_NO"				, "");
			    	jrParam.setField("CARD_NO"				, "");
			    	jrParam.setField("MODIFIER"				, modifier);
			    	/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol
		    		UPDATE TB_YM_STACKCOL
		    		   SET MOD_DDTT      = SYSDATE             
		    			 , MODIFIER      = :V_MODIFIER   
		    			 , YD_CAR_USE_GP = :V_YD_CAR_USE_GP      
		    			 , TRN_EQP_CD    = :V_TRN_EQP_CD           
		    			 , CAR_NO        = :V_CAR_NO               
		    			 , CARD_NO       = :V_CARD_NO  
		    		     , STACK_COL_ACTIVE_STAT = :V_STACK_COL_ACTIVE_STAT
		    		 WHERE STACK_COL_GP  = :V_STACK_COL_GP
			    	 */
			    	int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol", logId, methodNm, "TB_YM_STACKCOL 등록");
					if (intRtnVal <= 0) {

						szLogMsg = methodNm + "적치열[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						
						m_ctx.setRollbackOnly();
						throw new DAOException(szLogMsg);
					}

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YmCarPointinforeg("B","","",ydCarldLevLoc,"","",ydStackColActStat,logId,methodNm);
					
					/********************************
					 * 적치베드 상태 비활성화등록
					 *******************************/
					jrParam.setField("STACK_BED_WT_MAX"		, YmConstant.YD_STK_BED_WT_MAX_DEFAULT);
		    		jrParam.setField("STACK_BED_ACTIVE_STAT", "L");
					
		    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkbedYdStkColGp */

		    		/*UPDATE USRYMA.TB_YM_STACKER
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , STACK_BED_ACTIVE_STAT  = NVL(:V_STACK_BED_ACTIVE_STAT,STACK_BED_ACTIVE_STAT)
					     , STACK_BED_WT_MAX  = NVL(:V_STACK_BED_WT_MAX,STACK_BED_WT_MAX )
					  WHERE STACK_COL_GP  = :V_STACK_COL_GP	*/	
		    		
    				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YM_STACKER 활성상태수정(C)");
    				if (intRtnVal <= 0) {
						szLogMsg = methodNm + "적치BED[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);
					}
					

					/********************************
					 * 적치단 상태 비활성화등록
					 *******************************/
    				jrParam.setField("STACK_LAYER_ACTIVE_STAT", "E");
		    		jrParam.setField("STACK_LAYER_STAT", "E");
		    		
		    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear */

		    		/*UPDATE USRYMA.TB_YM_STACKLAYER           
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , STACK_LAYER_ACTIVE_STAT  = :V_STACK_LAYER_ACTIVE_STAT 
					     , STOCK_ID  = null
					     , STACK_LAYER_STAT  = :V_STACK_LAYER_STAT
					 WHERE STACK_COL_GP = :V_STACK_COL_GP*/	
							    		
    				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YM_STACKLAYER 차량 적치단 정보 비활성화(C)");
    				if (intRtnVal <= 0) {

						szLogMsg = methodNm + " 적치단[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);

					}
		    	}
		    	
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 차량스케줄 삭제
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		    	if (ydCarNo.startsWith("ET")) {
					ydCarNo = "ET";
				}
		    	jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
		    	jrParam.setField("TRANS_ORD_DATE"		, transOrdDate);
		    	jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
				jrParam.setField("CAR_NO" 			, ydCarNo);
		    	jrParam.setField("CARD_NO"			, ydCardNo);
				jrParam.setField("MODIFIER" 		, modifier);
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschTransDTSeq2
				SELECT *
				  FROM (
				       SELECT YD_CAR_SCH_ID
				            , SPOS_WLOC_CD
				            , YD_PNT_CD1
				         FROM TB_YD_CARSCH
				        WHERE CAR_NO        LIKE :V_CAR_NO||'%'
				          AND TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				          AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				        --AND DEL_YN='N'
				        ORDER BY YD_CAR_SCH_ID DESC
				        ) A
				WHERE ROWNUM<=1
		    	*/
		    	JDTORecordSet jsCarResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschTransDTSeq2", logId, methodNm, "차량스케줄  조회"); 
		    	
		    	
		    	if (jsCarResult.size() <= 0 ) {
					szLogMsg = "차량스케쥴 조회 오류 + ("+transOrdDate+", "+ ydCarNo + ", " + ydCardNo + ", 'G')";
					commUtils.printLog(logId, szLogMsg, "SL");
					return jrRtn ;
		    		
		    	} else {
		    
		    		jsCarResult.first();
					JDTORecord recGetVal = jsCarResult.getRecord();
					String szCarSchId= commUtils.trim(recGetVal.getFieldString("YD_CAR_SCH_ID"     ));
					
					jrParam.setField("YD_CAR_SCH_ID" , szCarSchId);
					jrParam.setField("DEL_YN" , "Y");
		    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch */

					/*UPDATE TB_YD_CARSCH
					   SET  
					       MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,DEL_YN = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID */
							    		
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 스케줄삭제");

					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					// 차량 이송재료 삭제
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl 
					UPDATE TB_YD_CARFTMVMTL
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , DEL_YN = 'Y'
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID

	                */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl", logId, methodNm, "차량스케줄재료 삭제");
		    	}

				if (ydCarDiffYn.equals("N")) {
					/****************************************************************************************************
					 * 차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 ***************************************************************************************************/
					JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setResultCode(logId);	//Log ID
					jrTemp.setResultMsg(methodNm);	//Log Method Name
					jrTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 기존:YDYDJ662
					jrTemp.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrTemp.setField("YD_CARPNT_CD"		, ydCarPntCdChk);		//입동포인트
					
					jrRtn = commUtils.addSndData(jrRtn, jrTemp);	
								
		    	}//end of if
	    	}
    	
		} catch (Exception e) {
			szLogMsg="출하차량출발실적 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			throw new DAOException(e);
		}

		return jrRtn;
	}// end of procOutCarLevWr()	
	
	/**
	 *      [A] 오퍼레이션명 : 이송차량출발실적 처리 carStartOrder
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procFrtoCarLevWr(JDTORecord rcvMsg)throws JDTOException  {
		String methodNm = "코일이송차량출발실적[YmCommCarMvSeEJB.procFrtoCarLevWr] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		String szLogMsg = "";
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();        
		JDTORecord jrParam = JDTORecordFactory.getInstance().create(); 
	    String szMsg	= "";		
	    String ydCarDiffYn = "N"; //위치 동일차량여부		
		
	    try{
	    	commUtils.printLog(logId, methodNm, "S+");	
			commUtils.printParam(logId + "이송차량출발실적 처리 수신 ", rcvMsg);
			
			// PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", "YmCommCarMvSeEJB => procFrtoCarLevWr", "APPPI0", "3", "*");
			
			if("PIDEV".equals("PIDEV")) {
				jrRtn = procFrtoCarLevWr_PIDEV(rcvMsg);
				return jrRtn;
			}
			
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydCardNo  	= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));         //카드번호
	    	String StackColGp   = commUtils.trim(rcvMsg.getFieldString("STACK_COL_GP"));    //위치
	    	String modifier  	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
	    	if ("".equals(modifier)) { modifier = msgId; }
	    	
	    	
	    	if (ydCardNo.equals("")) {
	    		szLogMsg="카드번호가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("CARD_NO Error");
	    	}
	    	
	    	if (StackColGp.equals("")) {
	    		szLogMsg="출발위치가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
	    	}
	    	
    		jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name	
			jrParam.setField("STACK_COL_GP", StackColGp);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkcolStackColGp 
			SELECT A.STACK_COL_GP   AS STACK_COL_GP
			     , A.CAR_NO         AS CAR_NO                               
			     , A.CARD_NO        AS CARD_NO                             
			     , A.WLOC_CD        AS WLOC_CD                             
			     , A.YD_PNT_CD      AS YD_PNT_CD   
			     , B.YD_CARPNT_CD   AS YD_CARPNT_CD
			  FROM TB_YM_STACKCOL A   
			     , TB_YD_CARPOINT B
			 WHERE B.YD_STK_COL_GP = A.STACK_COL_GP
			   AND A.STACK_COL_GP = :V_STACK_COL_GP
	    	 */	    	
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkcolStackColGp", logId, methodNm, "적치열 조회"); 

	    	if (jsStkCol == null || jsStkCol.size() <= 0) {
				szLogMsg = methodNm + "발지위치["+StackColGp+"] 및 카드번호 ["+ydCardNo+"]가 이상 합니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jrRtn ;
				
	    	} else {
	    		
		    	//열구분을 조회(도착지)
		    	String ydCarldLevLoc    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("STACK_COL_GP")); 
		    	String ydCarPntCdChk 	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CARPNT_CD")); 
		    	String ydCardNoChk 	    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("CARD_NO"));
		    	// PIDEV
		    	String ydCarNoChk 	    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("CAR_NO"));
		    	String ydStackColActStat= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
		    	
		    	if (!ydCardNoChk.equals(ydCardNo)) {
		 		   /**********************************************************
					* 다른 차량이 존재 하는 경우 
					**********************************************************/
		    		szMsg="["+methodNm+"] 해당 위치에 다른 차량이 존재 합니다. ";
		    		commUtils.printLog(logId, szMsg, "SL");	
		    		ydCarDiffYn = "Y";
		    	} else {
		    		/**********************************************************
					* 동일차량존재 
					**********************************************************/
		    		
		    		//사용불가로 되어있으면 상태값을 바꾸지 않는다
		    		if (!"N".equals(ydStackColActStat)) {
		    			ydStackColActStat = "C";
		    		}		    		
		    		/********************************
					 * 적치열 상태 비활성화등록
					 *******************************/
		    		szLogMsg = methodNm + "출발야드의 적치열["+ydCarldLevLoc+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("STACK_COL_GP"			, ydCarldLevLoc);
			    	jrParam.setField("STACK_COL_ACTIVE_STAT", ydStackColActStat);
			    	jrParam.setField("YD_CAR_USE_GP"		, "");
			    	jrParam.setField("TRN_EQP_CD"			, "");
			    	jrParam.setField("CAR_NO"				, "");
			    	jrParam.setField("CARD_NO"				, "");
			    	jrParam.setField("MODIFIER"				, modifier);
			    	/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol
		    		UPDATE TB_YM_STACKCOL
		    		   SET MOD_DDTT      = SYSDATE             
		    			 , MODIFIER      = :V_MODIFIER   
		    			 , YD_CAR_USE_GP = :V_YD_CAR_USE_GP      
		    			 , TRN_EQP_CD    = :V_TRN_EQP_CD           
		    			 , CAR_NO        = :V_CAR_NO               
		    			 , CARD_NO       = :V_CARD_NO  
		    		     , STACK_COL_ACTIVE_STAT = :V_STACK_COL_ACTIVE_STAT
		    		 WHERE STACK_COL_GP  = :V_STACK_COL_GP
			    	 */
			    	int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol", logId, methodNm, "TB_YM_STACKCOL 등록");
					if (intRtnVal <= 0) {

						szLogMsg = methodNm + "적치열[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						
						m_ctx.setRollbackOnly();
						throw new DAOException(szLogMsg);
					}

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YmCarPointinforeg("B","","",ydCarldLevLoc,"","",ydStackColActStat,logId,methodNm);
					
					/********************************
					 * 적치베드 상태 비활성화등록
					 *******************************/
					jrParam.setField("STACK_BED_WT_MAX"		, YmConstant.YD_STK_BED_WT_MAX_DEFAULT);
		    		jrParam.setField("STACK_BED_ACTIVE_STAT", "L");
					
		    		/*UPDATE USRYMA.TB_YM_STACKER
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , STACK_BED_ACTIVE_STAT  = NVL(:V_STACK_BED_ACTIVE_STAT,STACK_BED_ACTIVE_STAT)
					     , STACK_BED_WT_MAX  = NVL(:V_STACK_BED_WT_MAX,STACK_BED_WT_MAX )
					  WHERE STACK_COL_GP  = :V_STACK_COL_GP	*/	
		    		
    				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YM_STACKER 활성상태수정(C)");
    				if (intRtnVal <= 0) {
						szLogMsg = methodNm + "적치BED[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);
					}
					

					/********************************
					 * 적치단 상태 비활성화등록
					 *******************************/
    				jrParam.setField("STACK_LAYER_ACTIVE_STAT", "E");
		    		jrParam.setField("STACK_LAYER_STAT", "E");
		    		
		    		/*UPDATE USRYMA.TB_YM_STACKLAYER           
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , STACK_LAYER_ACTIVE_STAT  = :V_STACK_LAYER_ACTIVE_STAT 
					     , STOCK_ID  = null
					     , STACK_LAYER_STAT  = :V_STACK_LAYER_STAT
					 WHERE STACK_COL_GP = :V_STACK_COL_GP*/	
							    		
    				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YM_STACKLAYER 차량 적치단 정보 비활성화(C)");
    				if (intRtnVal <= 0) {

						szLogMsg = methodNm + " 적치단[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);

					}
					
    				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
    				sndL2Msg.setResultCode(logId);	//Log ID
    				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
    				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "3"                         ); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP"			, "I"                         ); //전문구분
    				sndL2Msg.setField("STACK_COL_GP"    , ydCarldLevLoc);
    				sndL2Msg.setField("STACK_BED_GP"    , "");
    				commUtils.printParam(logId, sndL2Msg);
    	 
    					//전송 Data 생성
    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L001", sndL2Msg));		
    				szMsg="[" + methodNm + "] 저장품제원 : 코일야드L2 로 송신 열구분[" + ydCarldLevLoc + "] - 저장품제원위치 : 코일야드L2 로 송신 호출 성공"+jrRtn.size();
    				commUtils.printLog(logId, szMsg, "SL");

		    	}
		    	
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 차량스케줄 삭제
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		    	jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
		    	//PIDEV				
//				if("Y".equals(sApplyYnPI)) {
					jrParam.setField("CAR_NO"			, ydCarNoChk);
//				} else {
//					jrParam.setField("CARD_NO"			, ydCardNo);
//				}
		    	jrParam.setField("YD_CARLD_STOP_LOC", ydCarldLevLoc);
				jrParam.setField("MODIFIER" 		, modifier);
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getreadcarinfoOfwloc_PIDEV
				WITH TEMP_TABLE AS (
				SELECT :V_CARD_NO  AS CARD_NO , :V_YD_CARLD_STOP_LOC AS YD_CARLD_STOP_LOC 
				  FROM DUAL
				)
				SELECT *
				 FROM (
				       SELECT A.YD_CAR_SCH_ID
				         FROM USRYDA.TB_YD_CARSCH A
				            , TEMP_TABLE B
				        WHERE A.CARD_NO=B.CARD_NO(+)
				          AND A.YD_CARLD_STOP_LOC like substr(REPLACE(B.YD_CARLD_STOP_LOC,'TR1','TR0'),1,5)||'%'
				          AND A.DEL_YN='N'
				          AND A.CARD_NO =B.CARD_NO
				        ORDER BY A.YD_CAR_SCH_ID DESC
				       ) A
				 WHERE ROWNUM<=1 
		    	*/
		    	JDTORecordSet jsCarResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getreadcarinfoOfwloc_PIDEV", logId, methodNm, "차량스케줄  조회"); 
		    	
		    	
		    	if (jsCarResult.size() <= 0 ) {
					szLogMsg = "차량스케쥴 조회 오류 + (" + ydCarldLevLoc + ", " + ydCardNo + ", 'G')";
					commUtils.printLog(logId, szLogMsg, "SL");
		    		
		    	} else {
		    
		    		jsCarResult.first();
					JDTORecord recGetVal = jsCarResult.getRecord();
					String szCarSchId= commUtils.trim(recGetVal.getFieldString("YD_CAR_SCH_ID"     ));
					
					jrParam.setField("YD_CAR_SCH_ID" , szCarSchId);
					jrParam.setField("DEL_YN" , "Y");

					/*UPDATE TB_YD_CARSCH
					   SET  
					       MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,DEL_YN = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID */
							    		
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 스케줄삭제");

					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					// 차량 이송재료 삭제
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl 
					UPDATE TB_YD_CARFTMVMTL
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , DEL_YN = 'Y'
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID

	                */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl", logId, methodNm, "차량스케줄재료 삭제");
		    	}
		    	
		    	commUtils.printLog(logId, szLogMsg+ "현재위치 복수동 입동지시 여부 N 이면 입동지시"+ ydCarDiffYn + ":" + ydCarPntCdChk , "SL");

				if (ydCarDiffYn.equals("N")) {
					/****************************************************************************************************
					 * 차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 ***************************************************************************************************/
					JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setResultCode(logId);	//Log ID
					jrTemp.setResultMsg(methodNm);	//Log Method Name
					jrTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 기존:YDYDJ662
					jrTemp.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrTemp.setField("YD_CARPNT_CD"		, ydCarPntCdChk);		//입동포인트
					
					jrRtn = commUtils.addSndData(jrRtn, jrTemp);

								
		    	}//end of if
	    	}
    	
		} catch (Exception e) {
			szLogMsg="출하차량출발실적 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			throw new DAOException(e);
		}

		return jrRtn;
	}// end of procFrtoCarLevWr()	
		
		

	/**
	 *      [A] 오퍼레이션명 : AB열연 차량초기화 작업 (구내운송 차량 초기화)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"      
	*/
	public void initCarSchNew(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "AB열연 차량초기화 작업[YmCommCarMvSeEJB.initCarSch] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
	    JDTORecord jrParam;	//Query 실행시 파라메터 전달용 JDTORecord 
	    
	    JDTORecordSet rsResult    	= null;
		
	    String szMsg           		= "";
	    String szTRN_EQP_CD;
	    String szTRN_WRK_FULLVOID_GP;
	    String szBACKUP_YN;
	    String szWLOC_CD;
	    String msgId;
	    String modifier;
	    
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
	    	//수신항목 변수 저장
			msgId    		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ004"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			szTRN_EQP_CD    = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); //운송장비코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szBACKUP_YN		= commUtils.nvl(rcvMsg.getFieldString("BACKUP_YN"), "N"); //BACKUP 구분 (화면에서 강제초기화시 Y)
			szWLOC_CD		= commUtils.nvl(rcvMsg.getFieldString("WLOC_CD"), ""); //개소코드
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			//파라메터 JDTORecord 생성 - Log ID, Method, 수정자 Set
			jrParam = commUtils.getParam(logId, methodNm, modifier);
			
			
			//**********************************************************************************	
			//1. 차량스케줄 존재여부 체크
			jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarSchByTrnEqpCd", logId, methodNm, "차량스케줄 존재여부 체크");
			if (rsResult.size() > 0) {
				//초기화 대상 차량스케줄이 존재함..
			
				if ("E".equals(szTRN_WRK_FULLVOID_GP)||"Y".equals(szBACKUP_YN)) {

					//**********************************************************************************	
					//3. 크레인 스케줄 편성 상태인지 체크
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchByTrnEqpCd", logId, methodNm, "크레인 스케줄 편성 상태인지 체크");
					if (rsResult.size() > 0) {
						szMsg = " 이송대상제가 크레인 스케줄 편성상태 입니다!!! 크레인스케줄을 취소한 후에 초기화를 실행하세요. << " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						throw new Exception(szMsg);
					}
					
					//**********************************************************************************	
					//4. Layer(단) 저장품 상태를 'C:적치중'로 초기화 
					jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
					jrParam.setField("STACK_LAYER_STAT"	, "C"); //적치단 상태 C:적치중
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updLayerStatByTrnEqpCd", logId, methodNm, "Layer(단) 저장품 상태를 'C:적치중'로 초기화 ");
					
					//**********************************************************************************	
					//5. 작업예약, 작업예약재료 삭제 
					jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); //운송장비코드
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWrkBookByTrnEqpCd", logId, methodNm, "운송장비코드로 작업예약 유무 체크");
					if (rsResult.size() > 0) {
						szMsg = " 작업예약이 존재함으로 작업예약과 작업예약재료 를 삭제(DEL_YN='Y')처리  << " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						
						//작업예약(TB_YM_WRKBOOK) 삭제처리 (DEL_YN='Y')
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
						jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnWrkBookByTrnEqpCd", logId, methodNm, "작업예약 삭제(DEL_YN='Y')처리 ");

						//작업예약재료(TB_YM_WRKBOOKMTL) 삭제처리 (DEL_YN='Y')
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
						jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnWrkBookMtlByTrnEqpCd", logId, methodNm, "작업예약재료 삭제(DEL_YN='Y')처리 ");
					}
					
					//**********************************************************************************	
					//6. 차량스케줄, 차량이송재료 삭제 
					
					//차량이송재료(TB_YD_CARFTMVMTL) 삭제처리 (DEL_YN='Y')
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarFtMvMtlByTrnEqpCd", logId, methodNm, "차량이송재료 삭제(DEL_YN='Y')처리 ");
					
					//차량스케줄(TB_YD_CARSCH) 삭제처리 (DEL_YN='Y')
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchByTrnEqpCd", logId, methodNm, "차량스케줄 삭제(DEL_YN='Y')처리 ");
					
					//**********************************************************************************	
					//7. 차량위치(적치열) 정리 작업
					
					if ("Y".equals(szBACKUP_YN)) {
						////////////////////////////////////////////////////////////////////////////////////////
						//발지 차량정보 삭제 하기 - 적치단 정리
						jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, "C"			); //적치 단 활성 상태 C:비활성화
						jrParam.setField("WLOC_CD"	 				, szWLOC_CD		); //개소코드
						jrParam.setField("TRN_EQP_CD"				, szTRN_EQP_CD	); //운송장비코드
						jrParam.setField("MODIFIER"					, modifier		);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_02", logId, methodNm, "발지 차량정보 삭제 하기 - 적치단 정리 ");
					}
					
					//차량위치 예정정보 삭제(상하차출발 위치) 정리
					jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리 ");
					
					//차량위치정보 삭제(상하차개시/완료/도착 위치) 정리
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseGpByTrnEqpCd", logId, methodNm, "차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");

					//**********************************************************************************	
					//8. 차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YmCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
					
				}

			}
			
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of initCarSch()

	/**
	 *      [A] 오퍼레이션명 : AB열연 차량초기화 작업 (구내운송 차량 초기화)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public void initCarSch(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "AB열연 차량초기화 작업[YmCommCarMvSeEJB.initCarSch] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
	    JDTORecord jrParam;	//Query 실행시 파라메터 전달용 JDTORecord 
	    
	    JDTORecordSet rsResult    	= null;
		
	    String szMsg           		= "";
	    String szTRN_EQP_CD;
	    String szTRN_WRK_FULLVOID_GP;
	    String szBACKUP_YN;
	    String szWLOC_CD;
	    String msgId;
	    String modifier;
	    
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
	    	//수신항목 변수 저장
			msgId    		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ004"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			szTRN_EQP_CD    = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); //운송장비코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szBACKUP_YN		= commUtils.nvl(rcvMsg.getFieldString("BACKUP_YN"), "N"); //BACKUP 구분 (화면에서 강제초기화시 Y)
			szWLOC_CD		= commUtils.nvl(rcvMsg.getFieldString("WLOC_CD"), ""); //개소코드
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			//파라메터 JDTORecord 생성 - Log ID, Method, 수정자 Set
			jrParam = commUtils.getParam(logId, methodNm, modifier);
			
			
				if ("E".equals(szTRN_WRK_FULLVOID_GP)||"Y".equals(szBACKUP_YN)) {

					if ("Y".equals(szBACKUP_YN)) {
						//**********************************************************************************	
						//3. 크레인 스케줄 편성 상태인지 체크
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchByTrnEqpCd", logId, methodNm, "크레인 스케줄 편성 상태인지 체크");
						if (rsResult.size() > 0) {
							szMsg = " 이송대상제가 크레인 스케줄 편성상태 입니다!!! 크레인스케줄을 취소한 후에 초기화를 실행하세요. << " + methodNm;
							commUtils.printLog(logId, szMsg, "SL");
							throw new Exception(szMsg);
						}
					
						//**********************************************************************************	
						//4. Layer(단) 저장품 상태를 'C:적치중'로 초기화  - ejb.transaction type="RequiresNew"
						jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
						jrParam.setField("STACK_LAYER_STAT"	, "C"); //적치단 상태 C:적치중
						//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updLayerStatByTrnEqpCd", logId, methodNm, "Layer(단) 저장품 상태를 'C:적치중'로 초기화 ");
						szMsg = " Layer(단) 저장품 상태를 'C:적치중'로 초기화  << " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updLayerStatByTrnEqpCd");
					
					
						//**********************************************************************************	
						//7. 차량위치(적치열) 정리 작업
					
						////////////////////////////////////////////////////////////////////////////////////////
						//발지 차량정보 삭제 하기 - 적치단 정리 - ejb.transaction type="RequiresNew"
						jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, "C"			); //적치 단 활성 상태 C:비활성화
						jrParam.setField("WLOC_CD"	 				, szWLOC_CD		); //개소코드
						jrParam.setField("TRN_EQP_CD"				, szTRN_EQP_CD	); //운송장비코드
						jrParam.setField("MODIFIER"					, modifier		);
						//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_02", logId, methodNm, "발지 차량정보 삭제 하기 - 적치단 정리 ");
						szMsg = " 발지 차량정보 삭제 하기 - 적치단 정리  << " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");
						bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_02");
						
					}
					
					//차량위치 예정정보 삭제(상하차출발 위치) 정리 - ejb.transaction type="RequiresNew"
					jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
					//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리 ");
					szMsg = " 차량위치 예정정보 삭제(상하차출발 위치)정리  << " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd");
					
					//차량위치정보 삭제(상하차개시/완료/도착 위치) 정리 - ejb.transaction type="RequiresNew"
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseGpByTrnEqpCd", logId, methodNm, "차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");
					szMsg = " 차량위치정보 삭제(상하차개시/완료/도착 위치)정리  << " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseGpByTrnEqpCd");

					//설비코드로 초기화 하는 경우(구내운송)			 			
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdate
					UPDATE USRYSA.TB_YM_CARPOINT
					   SET TRN_EQP_CD=null
					     , YD_STK_COL_ACT_STAT=DECODE(CARD_NO,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
					     , MOD_DDTT=sysdate
					     , MODIFIER='CarPointin'
					 WHERE TRN_EQP_CD=:V_TRN_EQP_CD
					   AND MOD_DDTT<>sysdate
					*/
					//ejb.transaction type="RequiresNew"
					jrParam.setField("STAT", "C"); //적치형 활성상태
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					
					szMsg = " 차량포인트통합관리  << " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");
					bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdate");
					
					
					if ("Y".equals(szBACKUP_YN)) {
						//**********************************************************************************	
						//5. 작업예약, 작업예약재료 삭제 
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD); //운송장비코드
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWrkBookByTrnEqpCd", logId, methodNm, "운송장비코드로 작업예약 유무 체크");
						if (rsResult.size() > 0) {
							szMsg = " 작업예약이 존재함으로 작업예약과 작업예약재료 를 삭제(DEL_YN='Y')처리  << " + methodNm;
							commUtils.printLog(logId, szMsg, "SL");
							
							//작업예약(TB_YM_WRKBOOK) 삭제처리 (DEL_YN='Y')
							jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
							jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnWrkBookByTrnEqpCd", logId, methodNm, "작업예약 삭제(DEL_YN='Y')처리 ");
	
							//작업예약재료(TB_YM_WRKBOOKMTL) 삭제처리 (DEL_YN='Y')
							jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
							jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnWrkBookMtlByTrnEqpCd", logId, methodNm, "작업예약재료 삭제(DEL_YN='Y')처리 ");
						}
					}
					
					//**********************************************************************************	
					//6. 차량스케줄, 차량이송재료 삭제 
					
					//차량이송재료(TB_YD_CARFTMVMTL) 삭제처리 (DEL_YN='Y')
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarFtMvMtlByTrnEqpCd", logId, methodNm, "차량이송재료 삭제(DEL_YN='Y')처리 ");
					
					//차량스케줄(TB_YD_CARSCH) 삭제처리 (DEL_YN='Y')
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					jrParam.setField("DEL_YN"	 , "Y"); //삭제유무 Y:삭제
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchByTrnEqpCd", logId, methodNm, "차량스케줄 삭제(DEL_YN='Y')처리 ");
					
					
				}

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of initCarSch()
		
	/**
	 *      [A] 오퍼레이션명 : AB열연 예약 차량초기화 작업 (구내운송 예약 차량 초기화)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public void initBookCarSch(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "AB열연 예약 차량초기화 작업[YmCommCarMvSeEJB.initBookCarSch] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
	    JDTORecord jrParam;	//Query 실행시 파라메터 전달용 JDTORecord 
	    
	    JDTORecordSet rsResult    	= null;
		
	    String szMsg           		= "";
	    String szTRN_EQP_CD;
	    String szTRN_WRK_FULLVOID_GP;
	    String szBACKUP_YN;
	    String szWLOC_CD;
	    String msgId;
	    String modifier;
	    
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
	    	//수신항목 변수 저장
			msgId    		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ004"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			szTRN_EQP_CD    = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); //운송장비코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szBACKUP_YN		= commUtils.nvl(rcvMsg.getFieldString("BACKUP_YN"), "N"); //BACKUP 구분 (화면에서 강제초기화시 Y)
			szWLOC_CD		= commUtils.nvl(rcvMsg.getFieldString("WLOC_CD"), ""); //개소코드
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			//파라메터 JDTORecord 생성 - Log ID, Method, 수정자 Set
			jrParam = commUtils.getParam(logId, methodNm, modifier);
			
			
			//초기화 대상 차량스케줄이 존재함..
		
			if ("E".equals(szTRN_WRK_FULLVOID_GP)||"Y".equals(szBACKUP_YN)) {

				
				//**********************************************************************************	
				//7. 차량위치(적치열) 정리 작업
				
				//차량위치 예정정보 삭제(상하차출발 위치) 정리
				jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "적치열 차량위치 예정정보 삭제(상하차출발 위치)정리 ");
				
				//차량위치정보 삭제(상하차개시/완료/도착 위치) 정리
				jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseGpByTrnEqpCd", logId, methodNm, "적치열 차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");

				//**********************************************************************************	
				//8. 차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				this.YmCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
				
			}

			
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of initBookCarSch()		
	

	/**
	 *      [A] 오퍼레이션명 : 발지 차량정보 삭제처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord clearSposCarInfo(String szWLOC_CD,String szYD_PNT_CD,String szTRN_EQP_CD,String logId,String modifier)throws DAOException  {
		String methodNm = "발지 차량정보 삭제처리[YmCommCarMvSeEJB.clearSposCarInfo] ";
	    
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= JDTORecordFactory.getInstance().create(); //임시  JDTORecord 
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "발지 차량정보 삭제처리[YmCommCarMvSeEJB.clearSposCarInfo]", "APPPI0", "3", "*");
			
			////////////////////////////////////////////////////////////////////////////////////////
			//발지 차량정보 삭제 하기 - 적치단 정리
			jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, "C"			); //적치 단 활성 상태 C:비활성화
			jrParam.setField("WLOC_CD"	 				, szWLOC_CD		); //개소코드
			jrParam.setField("TRN_EQP_CD"				, szTRN_EQP_CD	); //운송장비코드
			jrParam.setField("MODIFIER"					, modifier		);
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_02", logId, methodNm, "발지 차량정보 삭제 하기 - 적치단 정리 ");
			
			//발지 차량정보 삭제 하기 - 적치열 정리
			jrParam.setField("WLOC_CD"	 	, szWLOC_CD		); //발지개소코드
			jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD	); //운송장비코드
			jrParam.setField("MODIFIER"		, modifier		);
//			if ("Y".equals(sApplyYnPI)) {
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_03_PIDEV", logId, methodNm, "발지 차량정보 삭제 하기 - 적치열 정리 ");
//			} else {
//				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_03", logId, methodNm, "발지 차량정보 삭제 하기 - 적치열 정리 ");
//			}

			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			this.YmCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
			////////////////////////////////////////////////////////////////////////////////////////
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrTemp;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of clearSposCarInfo()		
	
	
	/**
	 * 차량 및 팔레트 출하/이송/이적 도착을 처리한다.--arrivalOfShippingOrTrans 사용안함
	 *	- A/B열연 코일 출하/이송상차/이송하차
	 *	- B열연 슬라브 이송상차/이송하차/팔레트이적하차 도착처리
	 *  - 차량이적에 관련된 내용 추가    최규성 
	 *  - 일관제철에서 moveGp인자 추가됨. cgs 2009-09-29
	 * @param cardNo	카드번호
	 * @param pos		차량정지위치
	 * @param moveGp                   
	 */
	private JDTORecord procArrivalTrans(String logId, String methodNms, String ydCardNo, String ydStkColGp, String moveGp) throws Exception {
		String methodNm = "차량및팔레드 도착 [YmCommCarMvSeEJB.procArrivalTrans] < " + methodNms;


		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();        
    	try{

    		/**
    		 * B열연 슬라브 팔레트번호로 도착처리시
    		 */
    		commUtils.printLog(logId, methodNm + "도착처리 : 카드번호=>"+ydCardNo, "S+");
    		
    		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("CARD_NO" 	, ydCardNo); 						
    		
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getMultyBay 
			SELECT A.* 
			     , C.CAR_NO
			     , C.ARR_WLOC_CD
			     , C.YD_PNT_CD3 AS ARR_YD_PNT_CD
			  FROM (
			        SELECT  
			                A.STOCK_ID,
			                A.STOCK_ITEM,
			                A.SCARFING_SUPPLY_YN,
			                A.FRTOMOVE_EQUIP_GP,         --이송 설비 구분
			                A.FRTOMOVE_EQUIP_BED_GP,     --이송 설비 BED 구분
			                A.FRTOMOVE_EQUIP_LAYER_GP,   --이송 설비 단 구분
			                A.CAR_CARD_NO,
			                SUBSTR(A.TRANS_WORD_NO,1,8) AS TRANS_ORD_DT , 
			                SUBSTR(A.TRANS_WORD_NO,9) AS TRANS_ORD_SEQNO
			        FROM    TB_YM_STOCK A
			        WHERE    A.FRTOMOVE_EQUIP_GP IS NOT NULL
			        AND     (A.CAR_CARD_NO = :V_CARD_NO OR A.SHEAR_SUPPLY_DEMAND_DDTT = :V_CARD_NO ) --pallet_no 임시 저장 컬럼       
			        AND     (SUBSTR(A.FRTOMOVE_EQUIP_GP,3,2) = 'TR'
			        OR       SUBSTR(A.FRTOMOVE_EQUIP_GP,3,2) = 'PT')
			        AND     A.DEL_YN = 'N'
			        ) A
			        ,TB_YD_CARFTMVMTL B
			        ,TB_YD_CARSCH C
			   WHERE A.STOCK_ID=B.STL_NO
			     AND B.YD_CAR_SCH_ID=C.YD_CAR_SCH_ID
			     AND C.DEL_YN='N'
			     AND B.DEL_YN='N'
			ORDER BY A.FRTOMOVE_EQUIP_BED_GP, A.FRTOMOVE_EQUIP_LAYER_GP DESC
    		*/
			JDTORecordSet jsCardStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getMultyBay", logId, methodNm, "카드번호에 해당하는 재료 조회");
	    	
	    	int iCardStockCnt =  jsCardStock.size();

	        
	    	commUtils.printLog(logId, "도착처리 : 동 재료정보 정보"+String.valueOf(iCardStockCnt), "SL");
	        
	        /**
	         *	차량 상차 저장품 정보를 가져온다.
	         *	=> 카드번호를 기준으로 작업예약된 정보를 가져온다.
	         */
	        String ydGp 	= ydStkColGp.substring(0, 1);
	        String bayGp 	= ydStkColGp.substring(1, 2);
	        /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarArrival
	        --차량 상차 저장품 정보를 리턴한다.
	        SELECT WB.YD_SCH_CD                  AS YD_SCH_CD
	             , ST.STOCK_ID                               
	             , WB.YD_WBOOK_ID                                  
	             , ST.STOCK_ITEM
	             , ST.CAR_CARD_NO                           
	             , ST.CAR_NO2                    AS CAR_NO  
	             , ST.TRANS_ORD_DATE2            AS TRANS_WORD_DATE
	             , ST.TRANS_ORD_SEQNO2           AS TRANS_WORD_SEQNO
	             , ST.STOCK_MOVE_TERM  --저장품이동조건
	             , SUBSTR(LY.STACK_COL_GP, 1, 2) AS CURR_STOCK_LOC
	             , LY.STACK_LAYER_GP
	             , LY.STACK_BED_GP
	             , LY.STACK_COL_GP || LY.STACK_BED_GP  || LY.STACK_LAYER_GP  AS UP_LOC
	             , FTMV.ARR_WLOC_CD
	             , FTMV.ARR_YD_PNT_CD
	          FROM TB_YM_STOCK ST
	             , TB_YM_WRKBOOK WB
	             , TB_YM_WRKBOOKMTL WM
	             , TB_YM_STACKLAYER LY
	             , TB_TS_MATL_FTMV_WO FTMV
	         WHERE WB.YD_WBOOK_ID= WM.YD_WBOOK_ID
	           AND WM.STOCK_ID   = ST.STOCK_ID
	           AND ST.STOCK_ID   = LY.STOCK_ID
	           AND ST.STOCK_ID   = FTMV.STL_NO(+)
	           AND LY.STACK_LAYER_STAT IN('S','C','U')
	           AND WB.YD_GP       = :V_YD_GP
	           AND WB.YD_BAY_GP   = :V_BAY_GP
	           AND ST.CAR_CARD_NO = :V_CARD_NO
	           AND WB.DEL_YN = 'N'
	           AND WM.DEL_YN = 'N'
	        ORDER BY WB.YD_WBOOK_ID ASC

	       */ 
	        jrParam.setField("YD_GP" , ydGp); 		
	        jrParam.setField("BAY_GP", bayGp); 		
	        JDTORecordSet jsFrtoStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarArrival", logId, methodNm, "카드번호 상태 조회");

	        String ydCarNo = commUtils.trim(jsFrtoStock.getRecord(0).getFieldString("CAR_NO"));
	        String ydGpBay = commUtils.trim(jsFrtoStock.getRecord(0).getFieldString("CURR_STOCK_LOC"));
	        
	        /**
	         *	현물의 위치와 차량 정지위치가 맞는지 확인한다.
	         */
	        int ydCarUppLocCnt = jsFrtoStock.size();    // 차상위치
	        if (ydCarUppLocCnt > 0) {
	        	
	        	//TT CAR인경우 15개 차상위치를 활성화 
		        if (moveGp.equals("T")) {
		        	ydCarUppLocCnt = 15 ;
		        }
		        if (!ydStkColGp.substring(0, 2).equals(ydGpBay)) {
		            throw new Exception("상차 위치가 다릅니다. 차량정지위치를 확인하십시요.");
		        }
		        
	        } else if (jsCardStock.size() == 0) {
	            throw new Exception("### 산적위치를 확인 하십시요.");                
	        }
	        
	        /**
	         *	설비 MAP '적재상태', 적치열 '차량 CARD 번호' UPDATE 
	         *	=> 차량이 도착 되었으므로 차량MAP을 활성화 하고 적치열에 카드번호를 MAPPING 한다.
	         */
	        /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCardNoOfStackCol
	        --차량의 카드번호를 UPDATE
	        UPDATE TB_YM_STACKCOL 
	           SET CARD_NO      = :V_CARD_NO
	             , CAR_CARD_NO  = NULL
	             , CAR_NO       = :V_CAR_NO
	         WHERE STACK_COL_GP = :V_STACK_COL_GP
	        */		
	        jrParam.setField("STACK_COL_GP" , ydStkColGp); 	
	        jrParam.setField("CAR_NO"       , ydCarNo); 	
	        commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCardNoOfStackCol", logId, methodNm, "TB_YM_STACKCOL 카드번호 갱신");
	        
	        //차량예약 포인트 지우기
	        /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCardNoOfStackCol2 
	        --차량의 카드번호를 UPDATE
	        UPDATE TB_YM_STACKCOL 
	           SET CAR_CARD_NO = NULL
	         WHERE CAR_CARD_NO = :V_CARD_NO
	        */		
	        commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCardNoOfStackCol2", logId, methodNm, "TB_YM_STACKCOL 차량예약 포인트 삭제");
	        
	        /**
	         * 적치단 갱신
             *  COIL STACKER 와 SLAB STACKER가 상이
             *  - COIL: 적치대의 BED가 1씩 증가, 적치단은 '01'로 고정
             *  - SLAB: 적치대는 '01'로 고정, 적치단의 단 수가 1씩 증가
             */
	    
            for(int i = 0; i < ydCarUppLocCnt; i++) {
            	
                if (YmConstant.YD_GP_2.equals(ydGp)) {
                	/** 
                	 * 슬라브
                	 *  **/
                	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updActiveStatOfLayer1 
                	--적치 단 활성 상태를 UPDATE
                	UPDATE TB_YM_STACKLAYER
                	   SET STACK_LAYER_ACTIVE_STAT = :V_ACTIVE_STAT
                	 WHERE STACK_COL_GP = :V_STACK_COL_GP
                	   AND STACK_BED_GP = :V_STACK_BED_GP
                	   AND STACK_LAYER_GP = :V_STACK_LAYER_GP
                	*/		
                	jrParam.setField("ACTIVE_STAT" 	    , "E");
                	jrParam.setField("STACK_BED_GP" 	, "01");
                	if (i < 9) {
                		jrParam.setField("STACK_LAYER_GP" 	, "0"+(i+1));
                	} else {
                		jrParam.setField("STACK_LAYER_GP" 	, "" +(i+1));
                	}
        	        commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updActiveStatOfLayer1", logId, methodNm, "TB_YM_STACKLAYER 활성화");
        	        
                } else {
                	/**************** 
                	 * 코일
                	 ***************/
                	/*
                	 * 차량 도착시 STOCK_ID is null 처리
                	 */
                    if (iCardStockCnt > 0) {
                    	
                    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updActiveStatOfLayer_02 
                    	--적치 단 활성 상태를 UPDATE
                    	UPDATE TB_YM_STACKLAYER
                    	   SET STACK_LAYER_ACTIVE_STAT = :V_ACTIVE_STAT
                    	     , STOCK_ID                = ''
                    	     , STACK_LAYER_STAT        = 'E' 
                    	 WHERE STACK_COL_GP = :V_STACK_COL_GP
                    	   AND STACK_BED_GP = LPAD(TO_NUMBER( :V_STACK_BED_GP ),2,'0')
                    	*/
                    	jrParam.setField("ACTIVE_STAT" 	    , "E");

                    	if ((iCardStockCnt + i) < 9) {
                    		jrParam.setField("STACK_LAYER_GP" 	, "0"+(iCardStockCnt + (i + 1)));
                    	} else {
                    		jrParam.setField("STACK_LAYER_GP" 	, "" +(iCardStockCnt + (i + 1)));
                    	}
                    	
                    	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updActiveStatOfLayer_02", logId, methodNm, "TB_YM_STACKLAYER 활성화");
                    }else {
                    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updActiveStatOfLayer_02 
                    	--적치 단 활성 상태를 UPDATE
                    	UPDATE TB_YM_STACKLAYER
                    	   SET STACK_LAYER_ACTIVE_STAT = :V_ACTIVE_STAT
                    	     , STOCK_ID                = ''
                    	     , STACK_LAYER_STAT        = 'E' 
                    	 WHERE STACK_COL_GP = :V_STACK_COL_GP
                    	   AND STACK_BED_GP = LPAD(TO_NUMBER( :V_STACK_BED_GP ),2,'0')
                    	*/
                    	jrParam.setField("ACTIVE_STAT" 	    , "E");

                    	if ((iCardStockCnt + i) < 9) {
                    		jrParam.setField("STACK_LAYER_GP" 	, "0"+(i+1));
                    	} else {
                    		jrParam.setField("STACK_LAYER_GP" 	, "" +(i+1));
                    	}
                    	
                    	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updActiveStatOfLayer_02", logId, methodNm, "TB_YM_STACKLAYER 활성화");
                    }
                }
            }            
	        
            //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			
			this.YmCarPointinforeg("C",ydCarNo,ydCardNo,ydStkColGp,"","","L", logId, methodNm);
			
	        /**
	         * 차량 도착 정보를 처리한다.
	         * 1. 작업예약을 오퍼레이터 지정으로 UPDATE
	         * 2. SCH CALL
	         */
	    
	        
	        if (ydCarUppLocCnt == 0) {
		        /*******************************************
		         * 하차 차량 도착 정보를 처리한다.
		         *******************************************/
	        	
	        	commUtils.printLog(logId, "하차작업 예약", "SL");
	        	if (iCardStockCnt > 0) {
 
	        		JDTORecord jrCardStock = JDTORecordFactory.getInstance().create();
	        		if (YmConstant.YD_GP_2.equals(ydGp)) {
//			             slabUnloadWork(multys, pos, stocksCnt);		        	 
//	        			/** 슬라브처리    	  */
//	        			/**
//		            	 * 1.작업예약 생성
//		            	 * 2.저장품테이블의 '작업예약ID', '저장품 이동 조건'를 UPDATE
//		            	 * 3.적치단 테이블의 '적치상태'를 UPDATE
//		            	 */	        			
//	        			String ydSchCd  = "2" + bayGp +"PT02LM";
//	        			
//	        			for(int i = 1; i <= iCardStockCnt; i++) { 
//	        				jsCardStock.absolute(i);
//	        				jrCardStock = jsCardStock.getRecord();
//	        				JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();
//	        				jrParam.setResultCode(logId);	//Log ID
//	        				jrParam.setResultMsg(methodNm);	//Log Method Name	
//	        				jrInTemp.setField("STOCK_ID" 			, commUtils.trim(jrCardStock.getFieldString("STOCK_ID")));
//	        				jrInTemp.setField("YD_SCH_CD"       	, ydSchCd   );// 자동이적 
//			            	String ydWbookId = commComm.procSalbWkBookInsert(jrInTemp);
//				    			
//			            	if (ydWbookId.equals(YmConstant.RETN_CD_FAILURE)) {
//				    			throw new Exception("작업예약ID 생성 실패"); 				
//			            	 }
//			            	 
//			            	 
//		                 dto 		= (JDTORecord)multys.get(i);
//		                 nextWBookId = ymCommonDAO.createWBook( pos, 
//		                         getUnloadSchKind(getField(dto, "STOCK_ITEM"), pos),
//		                         YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
//		                         "");
//		                 ymCommonDAO.modifyTermAndWBookIdOfStock(
//		                         nextWBookId, 
//		                         getStockMoveTerm(getField(dto, "STOCK_ITEM"),getField(dto, "FRTOMOVE_EQUIP_GP").substring(0, 1)), 
//		                         getField(dto, "STOCK_ID"));
//		                 ymCommonDAO.modifyLayerStatOfLayer(
//		                         YmCommonConst.STACK_LAYER_STAT_S, 
//		                         pos, 
//		                         getField(dto, "STOCK_ID"));
//		             } 
		        	 
		         } else {
		 			
 
		        	 String ydSchCd = "";

		 				for(int i = 1; i <= iCardStockCnt; i++) { 
	        				jsCardStock.absolute(i);
	        				jrCardStock = jsCardStock.getRecord();
	        				JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();
	        				jrInTemp.setResultCode(logId);	//Log ID
	        				jrInTemp.setResultMsg(methodNm);	//Log Method Name	
	        				jrInTemp.setField("STOCK_ID" 		, commUtils.trim(jrCardStock.getFieldString("STOCK_ID")));
	        				jrInTemp.setField("STACK_COL_GP" 	, commUtils.trim(jrCardStock.getFieldString("FRTOMOVE_EQUIP_GP")));
	        				jrInTemp.setField("STACK_BED_GP" 	, commUtils.trim(jrCardStock.getFieldString("FRTOMOVE_EQUIP_BED_GP")));
	        				jrInTemp.setField("STACK_LAYER_GP" 	, commUtils.trim(jrCardStock.getFieldString("FRTOMOVE_EQUIP_LAYER_GP")));
	        				
	        				if ("3".equals(ydStkColGp.substring(5 , 6)) || "4".equals(ydStkColGp.substring(5 , 6))) {
								ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT07LM";
							} else {
								ydSchCd  = "3"+ydStkColGp.substring(1,2) + "PT03LM";
							}
	        				jrInTemp.setField("YD_SCH_CD"       , ydSchCd   );// 차량이적 
	        				String ydWbookId = ymComm.procWkBookInsert(jrInTemp);//작업예약 생성
	        				
 
	        				String item     = commUtils.trim(jrCardStock.getFieldString("STOCK_ITEM"));
	        				String yd       = commUtils.trim(jrCardStock.getFieldString("FRTOMOVE_EQUIP_GP")).substring(0, 1);
	        				String MoveTerm ="";
	        				
	        				if (YmConstant.ITEM_CM.equals(item)) {
	        					MoveTerm= YmConstant.NEW_STOCK_MOVE_TERM_CS;
	        		        }else if (YmConstant.ITEM_CG.equals(item)) {
	        		        	MoveTerm= YmConstant.NEW_STOCK_MOVE_TERM_CS;
	        		        }else if (YmConstant.ITEM_SM.equals(item)) {
	        		           if (YmConstant.YD_GP_0.equals(yd)) {
	        		        	   MoveTerm= YmConstant.NEW_STOCK_MOVE_TERM_VW;
	        		           }else{
	        		        	   MoveTerm= YmConstant.NEW_STOCK_MOVE_TERM_VM;
	        		           } 
	        		        }	        				

				 	        jrParam.setField("WBOOK_ID"        , ydWbookId); 	
					        jrParam.setField("STOCK_MOVE_TERM" , MoveTerm); 	
					        jrParam.setField("STOCK_ID"        , commUtils.trim(jrCardStock.getFieldString("STOCK_ID"))); 	
		                	/*
		                	 * 이동조건수정
		                	 */					        
					        /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTermAndWBookIdOfStock 
								UPDATE  TB_YM_STOCK
								SET WBOOK_ID = :V_WBOOK_ID
								   ,STOCK_MOVE_TERM = DECODE(STOCK_MOVE_TERM,'BD',STOCK_MOVE_TERM, :V_STOCK_MOVE_TERM)
								   ,SHEAR_SUPPLY_DEMAND_DDTT=''
								WHERE    STOCK_ID = :V_STOCK_ID
					         */				             
					        commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTermAndWBookIdOfStock ", logId, methodNm, "TB_YM_STOCK 이동조건 갱신");
			
		 				}	
		         }
		         }
		         
			    	commUtils.printLog(logId, "차량 상차 저장품을 확인한다.인자:"+ydCardNo+ydGp+bayGp, "SL");
 
			    	
			    	
		 	        jrParam.setField("CAR_CARD_NO"   , ydCardNo); 	
			        jrParam.setField("YD_GP"         , ydGp); 	
			        jrParam.setField("BAY_GP"        , bayGp); 				    	
 
			    	/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectCarArrival
			    	--차량 상차 저장품 정보를 리턴한다., 
			    	SELECT  WBOOK.YD_SCH_CD AS SCH_WORK_KIND,
			    	        STOCK.STOCK_ID,         --저장품ID
			    	        STOCK.WBOOK_ID,         --작업예약ID,
			    	        STOCK.STOCK_ITEM,
			    	        STOCK.CAR_CARD_NO,      --차량CARD번호
			    	--        (select CAR_NO 
			    	--        from TB_DM_CARCARDINFO@DL_SMDB
			    	--        where card_no =STOCK.CAR_CARD_NO) as CAR_NO,  --차량 번호
			    	        STOCK.CAR_NO2 as CAR_NO,  
			    	        SUBSTR(STOCK.TRANS_WORD_NO, 1, 8) AS TRANS_WORD_DATE,
			    	        DECODE(LENGTH(STOCK.TRANS_WORD_NO), 10, SUBSTR(STOCK.TRANS_WORD_NO, 9, 100),
			    	            SUBSTR(STOCK.TRANS_WORD_NO, 9,100)) AS TRANS_WORD_SEQNO,
			    	        DECODE(LENGTH(STOCK.TRANS_WORD_NO), 10, STOCK.TRANS_WORD_NO,
			    	            SUBSTR(STOCK.TRANS_WORD_NO, 1, 8) || '0' || SUBSTR(STOCK.TRANS_WORD_NO, 9,1)) AS TRANS_WORD_DATE_NO,   --운송지시번호
			    	        STOCK.STOCK_MOVE_TERM,  --저장품이동조건
			    	        DECODE(STOCK.STOCK_ITEM, 'CG', DECODE(STOCK.STOCK_MOVE_TERM, 'LG', '1', '2'),
			    	            DECODE(STOCK.STOCK_ITEM, 'CM', '3', 
			    	                DECODE(STOCK.STOCK_ITEM, 'SM', '4'))) AS MATERIAL_GOODS,
			    	        SUBSTR(LAYER.STACK_COL_GP, 1, 2) AS CURR_STOCK_LOC,
			    	        LAYER.STACK_LAYER_GP,
			    	        LAYER.STACK_BED_GP,
			    	        LAYER.STACK_COL_GP  ||
			    	        LAYER.STACK_BED_GP  ||
			    	        LAYER.STACK_LAYER_GP  AS UP_LOC
			    	        ,FTMV.ARR_WLOC_CD
			    	        ,FTMV.ARR_YD_PNT_CD
			    	FROM    (
			    	        SELECT  STOCK_ID,
			    	                WBOOK_ID,
			    	                CAR_CARD_NO,
			    	                STOCK_ITEM,
			    	                STOCK_MOVE_TERM,
			    	                MIN(TRANS_WORD_NO) AS TRANS_WORD_NO   
			    	                ,CAR_NO2
			    	        FROM    TB_YM_STOCK
			    	        WHERE   DEL_YN = 'N'  
			    	        AND     CAR_CARD_NO = :V_CAR_CARD_NO
			    	        GROUP BY STOCK_ID,WBOOK_ID,STOCK_ITEM,CAR_CARD_NO,STOCK_MOVE_TERM,CAR_NO2
			    	        ) STOCK,
			    	        USRYMA.TB_YM_WRKBOOK WBOOK,
			    	        TB_YM_STACKLAYER LAYER,
			    	        TB_TS_MATL_FTMV_WO FTMV
			    	WHERE   STOCK.WBOOK_ID   = WBOOK.YD_WBOOK_ID
			    	AND     STOCK.STOCK_ID   = FTMV.STL_NO(+)
			    	AND     STOCK.STOCK_ID   = LAYER.STOCK_ID
			    	AND     LAYER.STACK_LAYER_STAT IN('S','L','U')
			    	AND     WBOOK.YD_GP     = :V_YD_GP
			    	AND     WBOOK.YD_BAY_GP    = :V_BAY_GP
			    	ORDER BY STOCK.WBOOK_ID ASC
 	        			*/
	        	JDTORecordSet jsstocks2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectCarArrival", logId, methodNm, "차량 상차 저장품 정보 조회");
	        	jsstocks2.absolute(1);
	        	JDTORecord inRecord = JDTORecordFactory.getInstance().create();
				inRecord = jsstocks2.getRecord();
     
	        /**
	         * 출하/이송/이적 구분.
	         */
				commUtils.printLog(logId, "출하/이송/이적 구분", "SL");
	
			    String sSchCode ="";
			    String sBookId ="";
				commUtils.printLog(logId, "출하/이송/이적 구분", "SL");
				commUtils.printParam(logId, inRecord);
				

				commUtils.printLog(logId, "작업예약의 스케쥴 수행 방법을 YD_SCH_CD-"+commUtils.trim(inRecord.getFieldString("SCH_WORK_KIND")), "SL");
				commUtils.printLog(logId, "작업예약의 스케쥴 수행 방법을 pos-"+ydStkColGp, "SL");
				commUtils.printLog(logId, "작업예약의 스케쥴 수행 방법을 STOCK_ITEM-"+commUtils.trim(inRecord.getFieldString("STOCK_ITEM")), "SL");		
				commUtils.printLog(logId, "작업예약의 스케쥴 수행 방법을 cardNo-"+ydCardNo, "SL");					
		
			    sSchCode = commUtils.trim(inRecord.getFieldString("SCH_WORK_KIND")) ;
		

			if (!"T".equals(ydCardNo.substring(0, 1))
					&& !"P".equals(ydCardNo.substring(0, 1))
					&& !"A".equals(ydCardNo.substring(0, 1))
					&& !"B".equals(ydCardNo.substring(0, 1))
					&& !"C".equals(ydCardNo.substring(0, 1))
					&& !"K".equals(ydCardNo.substring(0, 1))
					&& !"S".equals(ydCardNo.substring(0, 1))
					) {
		        /**
		         * 제품이송 하차인 경우 처리 
		         */
			
 					if (sSchCode.equals(YmConstant.YD_SCH_CD_3APT02LM)||sSchCode.equals(YmConstant.YD_SCH_CD_3APT06LM) ||
 					   sSchCode.equals(YmConstant.YD_SCH_CD_3BPT02LM)||sSchCode.equals(YmConstant.YD_SCH_CD_3BPT06LM) ||	
 					   sSchCode.equals(YmConstant.YD_SCH_CD_3CPT02LM)||sSchCode.equals(YmConstant.YD_SCH_CD_3CPT06LM) || 
 					   sSchCode.equals(YmConstant.YD_SCH_CD_3DPT02LM)||sSchCode.equals(YmConstant.YD_SCH_CD_3DPT06LM) ||					   
 					   sSchCode.equals(YmConstant.YD_SCH_CD_3EPT02LM)||sSchCode.equals(YmConstant.YD_SCH_CD_3EPT06LM)) {
					
 
 						
 						for(int i = 0; i < jsstocks2.size(); i++) {
 						   
 							sBookId = commUtils.trim(jsstocks2.getRecord(i).getFieldString("WBOOK_ID"));  
 						      
 				 	        jrParam.setField("YD_SCH_CD"   , sSchCode); 	
 					        jrParam.setField("YD_TO_LOC_DCSN_MTD"         , YmConstant.SCH_WORK_LOC_DECISION_METHOD_S); 	
 					        jrParam.setField("YD_TO_LOC_GUIDE"         , ydStkColGp); 	
 					        jrParam.setField("YD_WBOOK_ID"        , sBookId); 	
 							
 					        /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateOperatorOfWBook1
 							--작업예약을 오퍼레이터 지정으로 UPDATE
 							UPDATE  USRYMA.TB_YM_WRKBOOK        
 							SET     ,YD_SCH_CD  = :V_YD_SCH_CD
 							        ,YD_TO_LOC_DCSN_MTD  = :V_YD_TO_LOC_DCSN_MTD
 							        ,YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
 							WHERE   YD_WBOOK_ID  = :V_YD_WBOOK_ID
 							*/
 					       commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateOperatorOfWBook1", logId, methodNm, "TB_YM_WRKBOOK 작업예약 갱신"); 					 

 						}
 					}else{

 						for(int i = 0; i < jsstocks2.size(); i++) {
							
	        				if ("3".equals(ydStkColGp.substring(5 , 6)) || "4".equals(ydStkColGp.substring(5 , 6))) {
	        					sSchCode  = "3"+ydStkColGp.substring(1,2) + "PT06UM";  //확인필요
							} else {
								sSchCode  = "3"+ydStkColGp.substring(1,2) + "PT02UM";  //확인필요
							}	
	        				sBookId = commUtils.trim(jsstocks2.getRecord(i).getFieldString("WBOOK_ID"));  
 				 	        jrParam.setField("YD_SCH_CD"   , sSchCode); 	
 					        jrParam.setField("YD_TO_LOC_DCSN_MTD"     , YmConstant.SCH_WORK_LOC_DECISION_METHOD_O); 	
 					        jrParam.setField("YD_TO_LOC_GUIDE"        , ydStkColGp); 	
 					        jrParam.setField("YD_WBOOK_ID"            , sBookId); 								
 					        /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateOperatorOfWBook1
 							--작업예약을 오퍼레이터 지정으로 UPDATE
 							UPDATE  USRYMA.TB_YM_WRKBOOK        
 							SET     ,YD_SCH_CD  = :V_YD_SCH_CD
 							        ,YD_TO_LOC_DCSN_MTD  = :V_YD_TO_LOC_DCSN_MTD
 							        ,YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
 							WHERE   YD_WBOOK_ID  = :V_YD_WBOOK_ID
 							*/
 					       commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateOperatorOfWBook1", logId, methodNm, "TB_YM_WRKBOOK 작업예약 갱신"); 					 
						}
 
 					}
 					   jrParam.setField("YD_WBOOK_ID" , YmCommUtils.getCurDate("yyyyMMddHHmm")); 			
			 	       jrParam.setField("YD_SCH_CD"   , sSchCode); 	
					   jrParam.setField("EQUIP_GP"    , ydStkColGp); 	

					   /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWBookAndLoadSchOfEquip
					   --차량의 작업시작과 스케쥴종류를 UPDATE
					   UPDATE  TB_YM_EQUIP
					   SET     WBOOK_ID = :V_WBOOK_ID
					          ,CARLOAD_SCH_WORK_KIND = :V_CARLOAD_SCH_WORK_KIND
					   WHERE    EQUIP_GP = :V_EQUIP_GP
					  */ 
					  commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWBookAndLoadSchOfEquip", logId, methodNm, "TB_YM_EQUIP 관리");

					  
 	    	} else {

					for(int i = 0; i < jsstocks2.size(); i++) {
					
    				if ("3".equals(ydStkColGp.substring(5 , 6)) || "4".equals(ydStkColGp.substring(5 , 6))) {
    					sSchCode  = "3"+ydStkColGp.substring(1,2) + "PT06UM";  //확인필요
					} else {
						sSchCode  = "3"+ydStkColGp.substring(1,2) + "PT02UM";  //확인필요
					}
    				
    				sBookId = commUtils.trim(jsstocks2.getRecord(i).getFieldString("WBOOK_ID"));  
			 	        jrParam.setField("YD_SCH_CD"   , sSchCode); 	
				        jrParam.setField("YD_TO_LOC_DCSN_MTD"     , YmConstant.SCH_WORK_LOC_DECISION_METHOD_O); 	
				        jrParam.setField("YD_TO_LOC_GUIDE"        , ydStkColGp); 	
				        jrParam.setField("YD_WBOOK_ID"            , sBookId); 								
				        /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateOperatorOfWBook1
						--작업예약을 오퍼레이터 지정으로 UPDATE
						UPDATE  USRYMA.TB_YM_WRKBOOK        
						SET     ,YD_SCH_CD  = :V_YD_SCH_CD
						        ,YD_TO_LOC_DCSN_MTD  = :V_YD_TO_LOC_DCSN_MTD
						        ,YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
						WHERE   YD_WBOOK_ID  = :V_YD_WBOOK_ID
						*/
				       commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateOperatorOfWBook1", logId, methodNm, "TB_YM_WRKBOOK 작업예약 갱신"); 					 
				}
					   jrParam.setField("YD_WBOOK_ID" , YmCommUtils.getCurDate("yyyyMMddHHmm")); 			
			 	       jrParam.setField("YD_SCH_CD"   , sSchCode); 	
					   jrParam.setField("EQUIP_GP"    , ydStkColGp); 	

					   /*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateWBookAndLoadSchOfEquip
					   --차량의 작업시작과 스케쥴종류를 UPDATE
					   UPDATE  TB_YM_EQUIP
					   SET     WBOOK_ID = :V_WBOOK_ID
					          ,CARLOAD_SCH_WORK_KIND = :V_CARLOAD_SCH_WORK_KIND
					   WHERE    EQUIP_GP = :V_EQUIP_GP
					  */ 
 			
    	}
			
			commUtils.printLog(logId, methodNm+"차량 도착 후 예약된 작업의 스케쥴을 CALL", "SL");
			for(int i = 0; i < jsstocks2.size(); i++) {	
				sBookId = commUtils.trim(jsstocks2.getRecord(i).getFieldString("WBOOK_ID"));  	
				jrParam.setField("YD_WBOOK_ID"  , sBookId ); //야드작업예약ID
				jrParam.setField("YD_SCH_ST_GP" , "A"       ); //야드스케쥴기동구분(Auto)
				jrParam.setField("YD_SCH_REQ_GP", "3"); //야드스케쥴요청구분 하차도착
				jrParam.setField("MODIFIER"     , logId  ); //수정자
				jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrParam));
			
			}
 
	        /**
	         * 조건 추가 CGS
	         *  차량이적 카드번호(9999~9995) 일 땐 출하관련 전송 처리를 하지 않음.
	         */				
 	        if (commUtils.getCarMoveYN(ydCardNo).equals("N")) {
	    			   
		        //DMYDR036 ET차량도착처리인 경우 출하에 전송을 안함
					if (!moveGp.equals("Y")) {
						if (ydGp.equals("1") || ydGp.equals("2")|| ydGp.equals("3")) { //AB열연 
		        /**
		         * 출하차량도착실적(YDDMR029)
		         *  
		         */								
							
				//	    	YD_GP			야드구분
				//	    	TRANS_ORD_DT	운송지시일자
				//	    	TRANS_ORD_SEQNO	운송지시순번
				//	    	CAR_NO			차량번호
				//	    	CARD_NO			카드번호
				//	    	ARR_WLOC_CD     착지개소코드
				//	    	ARR_YD_PNT_CD   착지야드포인트코드
 							
							/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStockOfwloc 
							SELECT NVL(WLOC_CD,(SELECT WLOC_CD 
							                     FROM  TB_YM_STACKCOL 
							                    WHERE STACK_COL_GP= SUBSTR(A.STACK_COL_GP,1,4)||'1'
							                                        ||SUBSTR(A.STACK_COL_GP,6,1) 
							                                        )) AS WLOC_CD
							        , NVL(YD_PNT_CD,(SELECT YD_PNT_CD 
							                     FROM  TB_YM_STACKCOL 
							                    WHERE STACK_COL_GP= SUBSTR(A.STACK_COL_GP,1,4)||'1'
							                                        ||SUBSTR(A.STACK_COL_GP,6,1) 
							                                        )) AS YD_PNT_CD   
							      ,(SELECT MAX(YD_CAR_SCH_ID) AS YD_CAR_SCH_ID
							         FROM USRYDA.TB_YD_CARSCH B
							        WHERE B.DEL_YN='N'
							          AND B.CARD_NO=A.CARD_NO
							        ) AS YD_CAR_SCH_ID
							        ,(SELECT MAX(YD_CAR_PROG_STAT) AS YD_CAR_PROG_STAT
							         FROM USRYDA.TB_YD_CARSCH B
							        WHERE B.DEL_YN='N'
							          AND B.CARD_NO=A.CARD_NO
							        ) AS YD_CAR_PROG_STAT 
							         ,(SELECT MAX(YD_EQP_WRK_STAT) AS YD_EQP_WRK_STAT
							         FROM USRYDA.TB_YD_CARSCH B
							        WHERE B.DEL_YN='N'
							          AND B.CARD_NO=A.CARD_NO
							        ) AS YD_EQP_WRK_STAT 
							      ,(SELECT MAX(WBOOK_ID) AS  WBOOK_ID
							          FROM USRYMA.TB_YM_STOCK C
							        WHERE C.CAR_CARD_NO=A.CARD_NO
							          AND C.WBOOK_ID IS NOT NULL
							         ) AS WBOOK_ID
							      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS CURDATE    
							  FROM   TB_YM_STACKCOL A
							 WHERE STACK_COL_GP=:V_STACK_COL_GP
							*/ 
					        JDTORecordSet jswloc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStockOfwloc", logId, methodNm, "저장위치 조회");

				 			JDTORecord jrMsg = JDTORecordFactory.getInstance().create(); 
				 			jrMsg.setField("YD_GP"				, ydGp); 
				 			jrMsg.setField("TRANS_WORD_DATE"	, commUtils.trim(inRecord.getFieldString("TRANS_WORD_DATE"))); 
				 			jrMsg.setField("TRANS_WORD_SEQNO"	, commUtils.trim(inRecord.getFieldString("TRANS_WORD_SEQNO"))); 
				 			jrMsg.setField("CAR_NO"				, commUtils.trim(inRecord.getFieldString("CAR_NO"))); 
				 			jrMsg.setField("CARD_NO"			, ydCardNo); 
				 			jrMsg.setField("ARR_WLOC_CD"		, commUtils.trim(jswloc.getRecord(0).getFieldString("WLOC_CD"))); 
				 			jrMsg.setField("ARR_YD_PNT_CD"		, commUtils.trim(jswloc.getRecord(0).getFieldString("YD_PNT_CD"))); 
 			 			
 				 			//인터페이스 전문 호출
				 			commUtils.printLog(logId, methodNm+"내부IF호출=== 일관제철 B열연 출하차량도착실적.===", "SL");
							EJBConnector ejbConn1 = new EJBConnector("default","YmCommDAO",this);    
							jrRtn = (JDTORecord)ejbConn1.trx("getMsgL3",new Class[]{String.class,JDTORecord.class},
				 			  	  	 new Object[]{"YDDMR029",jrMsg}); 
 		            
 			            
				            String szYD_EQP_WRK_STAT = commUtils.trim(jswloc.getRecord(0).getFieldString("YD_EQP_WRK_STAT"));
 				            
				            //차량스케줄 도착등록,여기까지계속20170516 ?????????????????????????
				            JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();
				            jrInTemp.setField("YD_CAR_SCH_ID", 		commUtils.trim(jswloc.getRecord(0).getFieldString("YD_CAR_SCH_ID")));
				            jrInTemp.setField("MODIFIER"     , 		"arrivalOf");
 						
 						
							if (szYD_EQP_WRK_STAT.equals("L") ) {
								jrInTemp.setField("YD_CAR_PROG_STAT", "B");									//하차도착상태		
								jrInTemp.setField("YD_CARUD_WRK_BOOK_ID", 	commUtils.trim(jswloc.getRecord(0).getFieldString("WBOOK_ID")));
								jrInTemp.setField("YD_CARUD_STOP_LOC", 	ydStkColGp);
								jrInTemp.setField("YD_CARUD_ARR_DT", 		commUtils.trim(jswloc.getRecord(0).getFieldString("CURDATE"))); 
								jrInTemp.setField("YD_PNT_CD3", 			commUtils.trim(jswloc.getRecord(0).getFieldString("YD_PNT_CD")));
							}else{
								jrInTemp.setField("YD_CAR_PROG_STAT", "2");									//상차도착상태
								jrInTemp.setField("YD_CARLD_WRK_BOOK_ID", 	commUtils.trim(jswloc.getRecord(0).getFieldString("WBOOK_ID")));
								jrInTemp.setField("YD_CARLD_STOP_LOC", 	ydStkColGp);
								jrInTemp.setField("YD_CARLD_ARR_DT", 		commUtils.trim(jswloc.getRecord(0).getFieldString("CURDATE"))); 
								jrInTemp.setField("YD_PNT_CD1", 			commUtils.trim(jswloc.getRecord(0).getFieldString("YD_PNT_CD")));
							}
 							
 						
							/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch
							UPDATE TB_YD_CARSCH
							   SET MODIFIER         = :V_MODIFIER
							     , MOD_DDTT         = SYSDATE
							     , DEL_YN           = NVL(:V_DEL_YN          , DEL_YN)
							     --하차
							     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT    , YD_CAR_PROG_STAT)
							     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
							     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC   , YD_CARUD_STOP_LOC)
							     , YD_CARUD_ARR_DT      = NVL(:V_YD_CARUD_ARR_DT     , YD_CARUD_ARR_DT)
							     , YD_PNT_CD3           = NVL(:V_YD_PNT_CD3          , YD_PNT_CD3)
							     --상차
							     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID)
							     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC   , YD_CARLD_STOP_LOC)
							     , YD_CARLD_ARR_DT      = NVL(:V_YD_CARLD_ARR_DT     , YD_CARLD_ARR_DT)
							     , YD_PNT_CD1           = NVL(:V_YD_PNT_CD1          , YD_PNT_CD1)
							 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  

							*/
							int intRtnVal= commDao.update(jrInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 수정");
 
							if ( intRtnVal <= 0 ) {
					 			commUtils.printLog(logId, methodNm+"차량스케줄 도착등록 시 오류발생[반환값 :.==="+ intRtnVal + "]", "SL");
							
							}
 			            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 						}
 				 }
 		        }
	        /**
	         * 야드 L-2 송신
	         * 차량예정정보 코일만 먼저 만듬 : 슬라브 추가
	         */	

	        	if (YmConstant.YD_GP_3.equals(ydGp)) {
	        		// L2 수신 전문 호출
	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	        		jrYdMsg.setResultCode(logId);	//Log ID
	    			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	        		jrYdMsg.setField("JMS_TC_CD"          , "A7YML016"); //차량예정정보
					jrYdMsg.setField("PT_LOAD_LOC"        , ydStkColGp.substring(0, 6)); 		 //상차도 위치
					
	        		EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
					jrRtn = (JDTORecord)sndConn.trx("rcvA7YML016", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
	        	}
	
	        }
	        return jrRtn;
    	}catch (Exception e) {
			commUtils.printLog(logId, "차량도착 처리중 Error:" +e.getMessage(), "SL");
			throw new DAOException(e);
    	}
	}	
	
	
	/**
	 * 오퍼레이션명 : 출하시 맵활성화
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public String procYmLayerOpen(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "출하시 맵 활성화[YmCommCarMvSeEJB.procYmLayerOpen] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal 				    = 0;
		
		try{
			commUtils.printLog(logId, methodNm, "S+");
		    	
	    	//------------------------------------------------------------------------------------------------------------
	    	//	파라미터 확인
	    	//------------------------------------------------------------------------------------------------------------
			String ydStackColGp		= commUtils.nvl(rcvMsg.getFieldString("STACK_COL_GP"),""); 	 
			String ydCarNo 			= commUtils.nvl(rcvMsg.getFieldString("CAR_NO"),"");		 
			String ydCardNo 		= commUtils.nvl(rcvMsg.getFieldString("CARD_NO"),"");		
			String TrnEqpCd 		= commUtils.nvl(rcvMsg.getFieldString("TRN_EQP_CD"),"");	
			String ydCarProgStat 	= commUtils.nvl(rcvMsg.getFieldString("YD_CAR_PROG_STAT"),"");	
			String transEquipType 	= commUtils.nvl(rcvMsg.getFieldString("TRANS_EQUIPMENT_TYPE"),"N");	
			String modifier     	= commUtils.nvl(rcvMsg.getFieldString("MODIFIER"),"");    
			//------------------------------------------------------------------------------------------------------------
	    	//	적치열 테이블에 활성상태 처리 
	    	//------------------------------------------------------------------------------------------------------------
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"    			, modifier       ); //수정자 
			jrParam.setField("STACK_COL_ACTIVE_STAT", "L");
			jrParam.setField("YD_CAR_USE_GP"      	, "G");
			jrParam.setField("TRN_EQP_CD"         	, TrnEqpCd);
			jrParam.setField("CAR_NO"             	, ydCarNo);
			jrParam.setField("CARD_NO"            	, ydCardNo);
			jrParam.setField("STACK_COL_GP"			, ydStackColGp);
	    	
	    	/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol
    		UPDATE TB_YM_STACKCOL
    		   SET MOD_DDTT      = SYSDATE             
    			 , MODIFIER      = :V_MODIFIER   
    			 , YD_CAR_USE_GP = :V_YD_CAR_USE_GP      
    			 , TRN_EQP_CD    = :V_TRN_EQP_CD           
    			 , CAR_NO        = :V_CAR_NO               
    			 , CARD_NO       = :V_CARD_NO  
    		     , STACK_COL_ACTIVE_STAT = :V_STACK_COL_ACTIVE_STAT
    		 WHERE STACK_COL_GP  = :V_STACK_COL_GP
	    	 */
	    	intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol", logId, methodNm, "TB_YM_STACKCOL 등록");
	    	
			/********************************
			 * 적치베드 상태 비활성화등록
			 *******************************/
	    	jrParam.setField("STACK_BED_ACTIVE_STAT", "L");
	    	jrParam.setField("STACK_BED_WT_MAX"		, YmConstant.YD_STK_BED_WT_MAX_DEFAULT);
			
    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkbedYdStkColGp */

    		/*UPDATE USRYMA.TB_YM_STACKER
			   SET MOD_DDTT     = SYSDATE             
				 , MODIFIER     = :V_MODIFIER             
				 , STACK_BED_ACTIVE_STAT  = NVL(:V_STACK_BED_ACTIVE_STAT,STACK_BED_ACTIVE_STAT)
			     , STACK_BED_WT_MAX  = NVL(:V_STACK_BED_WT_MAX,STACK_BED_WT_MAX )
			  WHERE STACK_COL_GP  = :V_STACK_COL_GP	*/	
    		
			intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YM_STACKER 활성상태수정");

			//PDA하차 출하인 경우 생략
			if ("P".equals(transEquipType) && ("A".equals(ydCarProgStat) || "B".equals(ydCarProgStat))) {
				commUtils.printLog(logId, methodNm+"PDA하차 출하 인 경우 적치단 활성화 생략", "SL");
			}else{
				/********************************
				 * 적치단 상태 활성화등록
				 *******************************/
				jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, "E");
				jrParam.setField("STACK_LAYER_STAT"			, "E");
	    		
	    		/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear */
	
	    		/*UPDATE USRYMA.TB_YM_STACKLAYER           
				   SET MOD_DDTT     = SYSDATE             
					 , MODIFIER     = :V_MODIFIER             
					 , STACK_LAYER_ACTIVE_STAT  = :V_STACK_LAYER_ACTIVE_STAT 
				     , STOCK_ID  = null
				     , STACK_LAYER_STAT  = :V_STACK_LAYER_STAT
				 WHERE STACK_COL_GP = :V_STACK_COL_GP*/	
						    		
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YM_STACKLAYER 차량 적치단 활성상태수정");
			}	
			commUtils.printLog(logId, methodNm, "S-");
			return  ""+intRtnVal ;

			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of // end of procYdLayerOpen()
	
	/**
	 * 오퍼레이션명 : Ym 작업예약생성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procYmWbookInsert(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "Ym 작업예약생성[YmCommCarMvSeEJB.procYmWbookInsert] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try{
			commUtils.printLog(logId, methodNm, "S+");
	    	//------------------------------------------------------------------------------------------------------------
	    	//	파라미터 확인
	    	//------------------------------------------------------------------------------------------------------------
			String ydStackColGp		= commUtils.trim(rcvMsg.getFieldString("STACK_COL_GP")); 	 
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO")); 	 
		 	String YdCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO")); 	
		 	String ydBayGp 			= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP")); 	
		 	String ydGp 			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); 
		 	String TransOrdDate 	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_DATE")); 
		 	String TransOrdSeqNo 	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_SEQNO")); 
		 	String ydCarPntCd 		= commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"));  
		 	String transFrtoMoveGp	= commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP")); //1 운송 2 이송
		 	String ydEqpWrkStat 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_STAT"));   // 'U':상차적업,'L': 하차작업
		 	String modifier        	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 		  //수정자(Backup Only)
		 	String ydCarSchId      	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); 		  //수정자(Backup Only)

		 	String ydSchCd			= "";           
		 	//------------------------------------------------------------------------------------------------------------
	    	//	스케줄 코드 생성 하기
	    	//------------------------------------------------------------------------------------------------------------		 
			// 출하 1통로 2통로 구분
		 	int intTransOrdSeqNo = StringHelper.parseInt(TransOrdSeqNo, 0);
		 	
		 	// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");

			// PIDEV
//			if ("Y".equals(sApplyYnPI)) {
				String[] rVal = commDao.getTrnFrtomoveGpPI("", methodNm, TransOrdDate, TransOrdSeqNo);
				String sTrnFrtomoveGp = rVal[0];
				String hIssueGp = rVal[1];
				
				commUtils.printLog(logId, "sTrnFrtomoveGp:" + sTrnFrtomoveGp + "," + "hIssueGp:" + hIssueGp, "SL");
				
			    if (intTransOrdSeqNo > 999000) {
					// 반품,회송,부분하차	
					if (ydCarPntCd.substring(1,2).equals("2")) {
						ydSchCd = "3" + ydBayGp + "PT05LM";
					} else {
						ydSchCd = "3" + ydBayGp + "PT01LM";
					}
//			    } else if ( "21".equals(sTrnFrtomoveGp) ) {
//					// 제품이송
//					if (ydEqpWrkStat.equals("U")) {
//						//상차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT26UM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT22UM";
//						}
//					} else {
//						//하차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT26LM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT22LM";
//						}
//					}
			    } else if ( "21".equals(sTrnFrtomoveGp) && "K".equals(hIssueGp) ) {
					// 제품이송
					if (ydEqpWrkStat.equals("U")) {
						//상차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT26UM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT22UM";
						}
					} else {
						//하차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT26LM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT22LM";
						}
					}
			    } else if ( "22".equals(sTrnFrtomoveGp) && "9".equals(hIssueGp) ) {
					// 제품이송
					if (ydEqpWrkStat.equals("U")) {
						//상차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT26UM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT22UM";
						}
					} else {
						//하차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT26LM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT22LM";
						}
					}
//					} else if ( "23".equals(sTrnFrtomoveGp) ) {
//					//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
//					if (ydEqpWrkStat.equals("U")) {
//						//상차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT16UM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT12UM";
//						}
//					} else {
//						//하차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT16LM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT12LM";
//						}
//					}
			    } else if ( ("21".equals(sTrnFrtomoveGp) && "C".equals(hIssueGp))
			    			||
			    			("21".equals(sTrnFrtomoveGp) && "D".equals(hIssueGp)) ) {
					// 이송출고
					if (ydEqpWrkStat.equals("U")) {
						//상차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT16UM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT12UM";
						}
					} else {
						//하차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT16LM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT12LM";
						}
					}
				} else {
					/* 사내임시야적장냉연재이송작업적용
				 	 * 진도코드 3일경우 스케줄코드 이송상차로 진행 20200723
				 	 */
				 	String sYM2017 = ymComm.BCoilApplyYn("YM2017","3","TEMP_STK");
				 	
					//운송지시순번으로 대상재의 진도코드 조회
					JDTORecord jrTransParam = JDTORecordFactory.getInstance().create();
					jrTransParam.setResultCode(logId);	//Log ID
					jrTransParam.setResultMsg(methodNm);	//Log Method Name
					jrTransParam.setField("TRANS_ORD_DT", 			TransOrdDate);
					jrTransParam.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
			    	/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilTransCurrProgInfo
						SELECT  A.STOCK_ID
						      , B.CURR_PROG_CD
						      , B.NEXT_PROC           -- 차공정
						  FROM USRYMA.TB_YM_STOCK A
						     , USRPTA.TB_PT_COILCOMM B
						 WHERE A.STOCK_ID = B.COIL_NO
						   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DT
						   AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
						   AND A.DEL_YN = 'N'
					*/   
					JDTORecordSet rsTransResult = commDao.select(jrTransParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilTransCurrProgInfo", logId, methodNm, "대상재 진도코드 조회");
					String currProgCd = "";
					if (rsTransResult != null && rsTransResult.size() > 0) {
						currProgCd   = rsTransResult.getRecord(0).getFieldString("CURR_PROG_CD"); //작업 크레인
					}
					
					if(sYM2017.equals("Y") && currProgCd.equals("3")){
						//이송상차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT06UM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT02UM";
						}
					} else {
						// 출하
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT05UM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT01UM";
						}
					}
				}

			// 기존
//			} else {
//			    if (intTransOrdSeqNo > 999000) {
//					// 반품,회송,부분하차	
//					if (ydCarPntCd.substring(1,2).equals("2")) {
//						ydSchCd = "3" + ydBayGp + "PT05LM";
//					} else {
//						ydSchCd = "3" + ydBayGp + "PT01LM";
//					}
//
//			    } else if ((intTransOrdSeqNo > 800000)) {
//					// 제품이송
//					if (ydEqpWrkStat.equals("U")) {
//						//상차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT26UM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT22UM";
//						}
//					} else {
//						//하차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT26LM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT22LM";
//						}
//					}
//				} else if ((intTransOrdSeqNo > 700000 && intTransOrdSeqNo <= 800000)) {
//					//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
//					if (ydEqpWrkStat.equals("U")) {
//						//상차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT16UM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT12UM";
//						}
//					} else {
//						//하차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT16LM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT12LM";
//						}
//					}
//				} else {
//					/* 사내임시야적장냉연재이송작업적용
//				 	 * 진도코드 3일경우 스케줄코드 이송상차로 진행 20200723
//				 	 */
//				 	String sYM2017 = ymComm.BCoilApplyYn("YM2017","3","TEMP_STK");
//				 	
//					//운송지시순번으로 대상재의 진도코드 조회
//					JDTORecord jrTransParam = JDTORecordFactory.getInstance().create();
//					jrTransParam.setResultCode(logId);	//Log ID
//					jrTransParam.setResultMsg(methodNm);	//Log Method Name
//					jrTransParam.setField("TRANS_ORD_DT", 			TransOrdDate);
//					jrTransParam.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
//			    	/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilTransCurrProgInfo
//						SELECT  A.STOCK_ID
//						      , B.CURR_PROG_CD
//						      , B.NEXT_PROC           -- 차공정
//						  FROM USRYMA.TB_YM_STOCK A
//						     , USRPTA.TB_PT_COILCOMM B
//						 WHERE A.STOCK_ID = B.COIL_NO
//						   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DT
//						   AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
//						   AND A.DEL_YN = 'N'
//					*/   
//					JDTORecordSet rsTransResult = commDao.select(jrTransParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilTransCurrProgInfo", logId, methodNm, "대상재 진도코드 조회");
//					String currProgCd = "";
//					if (rsTransResult != null && rsTransResult.size() > 0) {
//						currProgCd   = rsTransResult.getRecord(0).getFieldString("CURR_PROG_CD"); //작업 크레인
//					}
//					
//					if(sYM2017.equals("Y") && currProgCd.equals("3")){
//						//이송상차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT06UM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT02UM";
//						}
//					}else{
//						// 출하
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT05UM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT01UM";
//						}
//					}
//				}
//				
//			}	
 
			//스케줄코드로 스케줄기준Table조회
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_SCH_CD", ydSchCd);
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			String ydSchPrior = "";
			String ydWrkCrn = "";
			if (rsResult != null && rsResult.size() > 0) {
				ydWrkCrn   = rsResult.getRecord(0).getFieldString("YD_WRK_CRN"); //작업 크레인
				ydSchPrior = rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			} else {
				throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + ydSchCd + "]");
			}			
			
			//------------------------------------------------------------------------------------------------------------
	    	//	작업예약 존재 여부 CHECK
	    	//------------------------------------------------------------------------------------------------------------
			jrParam.setField("TRANS_ORD_DT", 			TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockWbookcheck 
			SELECT B.YD_WBOOK_ID
			  FROM USRYMA.TB_YM_STOCK A
			     , USRYMA.TB_YM_WRKBOOK B
			     , USRYMA.TB_YM_WRKBOOKMTL C
			 WHERE B.YD_WBOOK_ID=C.YD_WBOOK_ID
			   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DT
			   AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
			   AND A.STOCK_ID = C.STOCK_ID
			   AND A.DEL_YN = 'N'
			   AND B.DEL_YN = 'N'
			   AND C.DEL_YN = 'N'
			   AND (SUBSTR(B.YD_SCH_CD,3,2) IN ('YD') OR  SUBSTR(B.YD_SCH_CD,3,4) = 'TC12')                      
			   AND B.YD_WBOOK_ID NOT IN ( SELECT YD_WBOOK_ID FROM TB_YM_CRNSCH WHERE DEL_YN = 'N' )
			GROUP BY B.YD_WBOOK_ID     
			 */  
			
			JDTORecordSet jsWbookMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockWbookcheck", logId, methodNm, "작업예약 조회");
		 	if (jsWbookMtl.size() > 0) {
				commUtils.printLog(logId, "이적 및 대차출하 작업예약이 존재 합니다", "SL");
				
				for (int Loop_i = 1; Loop_i <= jsWbookMtl.size() ; Loop_i++) {
					
					jsWbookMtl.absolute(Loop_i);
					JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
					jrInPara.setRecord(jsWbookMtl.getRecord());
					jrInPara.setResultCode(logId);			//Log ID
					jrInPara.setResultMsg(methodNm);		//Log Method Name
					jrInPara.setField("MODIFIER" 			, modifier); //수정자
					//크레인 작업예약 삭제
					EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
					ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrInPara });
					
					
				}
			}
			/**********************************************************
			* 3. 작업예약 등록여부 .. 재송신error
			**********************************************************/		
		 	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookStockYN /   
		 	SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
		 	  FROM TB_YM_WRKBOOKMTL WM
		 	     , TB_YM_WRKBOOK    WB
		 	WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
		 	  AND WM.DEL_YN      = 'N'
		 	  AND WB.DEL_YN      = 'N'
		 	  AND WM.STOCK_ID    IN (SELECT STOCK_ID 
		 	                           FROM TB_YM_STOCK 
		 	                          WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE 
		 	                            AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
		 	                            AND DEL_YN = 'N')
			*/  	  
			jrParam.setField("TRANS_ORD_DATE", 			TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookStockYN", logId, methodNm, "작업예약 등록여부");
			
			commUtils.printLog(logId, "작업 예약 편성여부:" + jsChk2.getRecord(0).getFieldString("WB_STL_YN") , "SL");
			
			if (jsChk2 != null && jsChk2.size() > 0) {
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
					
					throw new Exception("이미 다른 크레인예약이  되어 있슴 ");
				}
			}			
				 	
		 	
			//------------------------------------------------------------------------------------------------------------
	    	//	작업예약 생성 하기
	    	//------------------------------------------------------------------------------------------------------------
		 	JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
			jrInPara.setField("TRANS_ORD_DT", 			TransOrdDate);
			jrInPara.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
			jrInPara.setField("YD_BAY_GP", 				ydBayGp);
			jrInPara.setField("YD_GP", 					ydGp);
			jrInPara.setField("YD_CARPNT_CD", 			ydCarPntCd);
			jrInPara.setField("CAR_NO",					ydCarNo);
			jrInPara.setField("MODIFIER",				modifier);	
			String ydAimydGp 	= ""; 	
			String ydAimBayGp	= ""; 
			JDTORecordSet jsStock  = JDTORecordFactory.getInstance().createRecordSet("Temp");
			if (intTransOrdSeqNo > 999000) {
				ydAimydGp	= ydGp;
				ydAimBayGp	= ydBayGp;					
				//반품,회송,부분하차 일경우
				//차량 하차 위치(STKLYR)에 제품을 적치시킨다.
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarftmvmtlToLyr 
				--반품,회송,부분하차시 차량 하차 위치(STKLYR)에 제품을 적치 

				MERGE INTO TB_YM_STACKLAYER TG USING (
				SELECT A.YD_CARUD_STOP_LOC AS STACK_COL_GP
				     , B.YD_STK_BED_NO     AS STACK_BED_GP
				     ,'01'                 AS STACK_LAYER_GP
				     , B.STL_NO            AS STOCK_ID
				  FROM TB_YD_CARSCH A
				     , TB_YD_CARFTMVMTL B
				 WHERE A.CAR_NO = :V_CAR_NO
				   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND A.DEL_YN = 'N'
				   AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID

				) DD ON (TG.STACK_COL_GP = DD.STACK_COL_GP AND TG.STACK_BED_GP = DD.STACK_BED_GP AND TG.STACK_LAYER_GP = DD.STACK_LAYER_GP)
				WHEN MATCHED THEN 
				UPDATE 
				   SET TG.STOCK_ID         = DD.STOCK_ID
				     , TG.STACK_LAYER_STAT = 'C'
				     , TG.MODIFIER         = :V_MODIFIER
				     , TG.MOD_DDTT         = SYSDATE
				 */	   
				   
				commDao.update(jrInPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarftmvmtlToLyr");

				//작업예약재료를 생성하기 위해 rsResult 에 하차 대상 재료를 조회한다.
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarftmvmtl 
				--하차대상제 조회  
				SELECT A.YD_CARUD_STOP_LOC   AS STACK_COL_GP
				     , B.YD_STK_BED_NO       AS STACK_BED_GP
				     , '01' AS YD_STK_LYR_NO AS STACK_LAYER_GP
				     , B.STL_NO              AS STOCK_ID 
				  FROM TB_YD_CARSCH A
				     , TB_YD_CARFTMVMTL B
				 WHERE A.CAR_NO = :V_CAR_NO
				   AND A.TRANS_ORD_DATE = :V_TRANS_ORD_DT
				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND A.DEL_YN = 'N'
				   AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				 ORDER BY B.YD_STK_BED_NO
				*/
				jrParam.setField("TRANS_ORD_DT"		,TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	,TransOrdSeqNo);
				jrParam.setField("CAR_NO"			,ydCarNo);
				jsStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarftmvmtl", logId, methodNm, "하차대상재 조회");
			 	if (jsStock.size() <= 0) {
					m_ctx.setRollbackOnly();
			 		throw new Exception("운송지시 저장품대상이 없습니다: [" + ydSchCd + "]");
				}				
			} else {
				ydAimydGp	= ydGp;
				ydAimBayGp	= ydBayGp;	
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockTransOrdDTWbook /
				SELECT A.STOCK_ID  
				     , SUBSTR(B.STACK_COL_GP,2,1) AS YD_BAY_GP
				     , B.STACK_COL_GP 
				     , B.STACK_BED_GP 
				     , B.STACK_LAYER_GP
				  FROM USRYMA.TB_YM_STOCK A
				     , USRYMA.TB_YM_STACKLAYER B
				     , USRYDA.TB_YD_CARPOINT C
				 WHERE A.STOCK_ID =B.STOCK_ID
				   AND A.TRANS_ORD_DATE2 =:V_TRANS_ORD_DT
				   AND A.TRANS_ORD_SEQNO2 =:V_TRANS_ORD_SEQNO
				   AND B.STACK_COL_GP LIKE :V_YD_GP ||:V_YD_BAY_GP ||'%'
				   AND C.YD_CARPNT_CD=:V_YD_CARPNT_CD
				   AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN '00' AND '99'
				   AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				   AND B.STACK_LAYER_STAT IN ('C')
				   ORDER BY B.STACK_LAYER_GP DESC 
				*/
				jrParam.setField("TRANS_ORD_DT"		,TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	,TransOrdSeqNo);
				jrParam.setField("YD_GP"			,ydGp);
				jrParam.setField("YD_BAY_GP"		,ydBayGp);
				jrParam.setField("YD_CARPNT_CD"		,ydCarPntCd);
				jsStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockTransOrdDTWbook", logId, methodNm, "스케줄 기준 조회");
			 	if (jsStock.size() <= 0) {
					m_ctx.setRollbackOnly();
			 		throw new Exception("운송지시 저장품대상이 없습니다: [" + ydSchCd + "]");
				}
			}
			
			
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			JDTORecord jrInTemp  = JDTORecordFactory.getInstance().create();
			
			String first_wbook_ID = "";
			
	    	for(int Loop_i = 0; Loop_i < jsStock.size(); Loop_i++) {
	    		
				//작업예약 등록
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				if ("".equals(first_wbook_ID)) {
					first_wbook_ID = ydWbookId; //첫번째 작업예약 ID 
				}				
				
				recInTemp.setField("YD_WBOOK_ID"       	, ydWbookId     ); //야드작업예약ID
				recInTemp.setField("MODIFIER"          	, modifier      ); //수정자
				recInTemp.setField("YD_GP"             	, ydGp ); //야드구분
				recInTemp.setField("YD_BAY_GP"         	, ydBayGp ); //야드동구분
				recInTemp.setField("YD_SCH_CD"         	, ydSchCd       ); //야드스케쥴코드
				recInTemp.setField("YD_SCH_PRIOR"      	, ydSchPrior    ); //야드스케쥴우선순위
				recInTemp.setField("YD_SCH_PROG_STAT"  	, "W"           ); //야드스케쥴진행상태(스케줄수행대기)
				recInTemp.setField("YD_SCH_ST_GP"      	, "O"           ); //야드스케쥴기동구분(Manual)
				recInTemp.setField("YD_SCH_REQ_GP"     	, "M"           ); //야드스케쥴요청구분(이적)
				recInTemp.setField("YD_CAR_USE_GP"		, "G");
				recInTemp.setField("CAR_NO"				, ydCarNo);
				recInTemp.setField("CARD_NO"			, YdCardNo);
				recInTemp.setField("YD_AIM_YD_GP"		, ydAimydGp);
				recInTemp.setField("YD_AIM_BAY_GP"		, ydAimBayGp);
				commDao.insert(recInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "TB_YM_WRKBOOK");
	    		
	    		//작업예약재료 등록
	    		jrInTemp.setField("YD_WBOOK_ID"		, ydWbookId);
	    		jrInTemp.setField("MODIFIER"		, modifier);
	    		jrInTemp.setField("STACK_COL_GP"	, ydStackColGp);
	    		jrInTemp.setField("STACK_BED_GP"	, commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("STACK_BED_GP"       )));
	    		jrInTemp.setField("STACK_LAYER_GP"	, commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("STACK_LAYER_GP"       )));
	    		jrInTemp.setField("STOCK_ID"		, commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("STOCK_ID"       )));
	    		jrInTemp.setField("YD_UP_COLL_SEQ"	, "" + Loop_i);
	    		commDao.insert(jrInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");
	    	}						

				
			//출력 값
			jrRtn.setField("YD_WBOOK_ID"	, first_wbook_ID);
			jrRtn.setField("YD_EQP_ID"		, ydWrkCrn);
			jrRtn.setField("YD_SCH_CD"		, ydSchCd);

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of 
    /**
	 * 오퍼레이션명 : 차량POINT 점유(procUpdYdTransOrdChange)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 
	 */
	public String procUpdYdTransOrdChange(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "Ym 차량POINT 점유[YmCommCarMvSeEJB.procUpdYdTransOrdChange] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try{

			commUtils.printLog(logId, methodNm, "S+");
			String trnEqpCd			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));
			String ydCarNo			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String ydCardNo			= commUtils.trim(rcvMsg.getFieldString("CARD_NO")); 
			String ydMakeCarPntCd	= commUtils.trim(rcvMsg.getFieldString("YD_MAKECARPNT_CD"));
			String ydModifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
	 
		 	//------------------------------------------------------------------------------------------------------------
	    	//	차량포인트 도착상태 변경 처리
	    	//------------------------------------------------------------------------------------------------------------

			JDTORecord 	recInTemp = JDTORecordFactory.getInstance().create();
		    recInTemp.setField("YD_STK_COL_ACT_STAT", "L");
		    recInTemp.setField("TRN_EQP_CD"			, trnEqpCd);
		    recInTemp.setField("CAR_NO"				, ydCarNo);
		    recInTemp.setField("CARD_NO"			, ydCardNo);
		    recInTemp.setField("YD_CARPNT_CD"		, ydMakeCarPntCd);
		    recInTemp.setField("MODIFIER"			, ydModifier);
			
		    /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarpointByCarpnt
		    UPDATE USRYDA.TB_YD_CARPOINT
		       SET MOD_DDTT  = SYSDATE
		         , MODIFIER            = :V_MODIFIER
		         , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
		         , TRN_EQP_CD          = :V_TRN_EQP_CD
		         , CAR_NO              = :V_CAR_NO
		         , CARD_NO             = :V_CARD_NO
		    WHERE YD_CARPNT_CD         = :V_YD_CARPNT_CD

		    */
		    commDao.insert(recInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarpointByCarpnt", logId, methodNm, "TB_YD_CARPOINT 갱신");
		    commUtils.printLog(logId, methodNm, "S-");
		    return YmConstant.RETN_CD_SUCCESS;
		    
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of procUpdYdTransOrdChange()		
    /**
	 * 오퍼레이션명 : STOCK에 현재 저장품 관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 
	 */
	public String procUpdYmStock(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "Ym 차량  STOCK 저장품 현위치 등록[YmCommCarMvSeEJB.procUpdYmStock] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try{

			commUtils.printLog(logId, methodNm, "S+");
			
			String TransOrdDate		= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_DATE"));
			String TransOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_SEQNO"));
			String modifier		    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			String StockId    = "";       
			String StackColGp  = "";       
		 	//------------------------------------------------------------------------------------------------------------
	    	//	저장품 에 현 위치 등록
	    	//------------------------------------------------------------------------------------------------------------
			JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER" 			, modifier); 	
			jrParam.setField("TRANS_ORD_DATE"		, TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO"		, TransOrdSeqNo);
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockExpect
			SELECT A.STOCK_ID
			     , B.STACK_COL_GP
			  FROM TB_YM_STOCK A
			     , TB_YM_STACKLAYER B
			 WHERE A.STOCK_ID = B.STOCK_ID
			   AND A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
			   AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
			   AND B.STACK_LAYER_STAT IN ('C','U')	
			*/	   
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockExpect", logId, methodNm, "차량스케쥴 조회");
			if (jsStock.size() > 0) {
				for(int ii = 0; ii < jsStock.size(); ii++) {
	    			
					StockId 	= commUtils.trim(jsStock.getRecord(ii).getFieldString("STOCK_ID"));  
					StackColGp 	= commUtils.trim(jsStock.getRecord(ii).getFieldString("STACK_COL_GP"));  
					
					jrMsg = JDTORecordFactory.getInstance().create();
					jrMsg.setResultCode(logId);	//Log ID
					jrMsg.setResultMsg(methodNm);	//Log Method Name
					jrMsg.setField("MODIFIER" 		, modifier); 
					jrMsg.setField("STOCK_ID"		, StockId);
					jrMsg.setField("STACK_COL_GP" 	, StackColGp);
				
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStockExpect 
					UPDATE TB_YM_STOCK A
					   SET EXPECT_STACK_LOC = :V_STACK_COL_GP
					     , MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					 WHERE STOCK_ID         = :V_STOCK_ID
					*/ 
					commDao.update(jrMsg, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStockExpect", logId, methodNm, "저장품 제원");
				}	
			}			
		    commUtils.printLog(logId, methodNm, "S-");
		    return YmConstant.RETN_CD_SUCCESS;
		    
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of procUpdYmStock()		
		
	/**
	 * 복수상차 처리 로직  
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public JDTORecord procCmbnCarldYn(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "복수상차 처리 로직  [YmCommCarMvSeEJB.procCmbnCarldYn] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult2 = null;
		
		try{		
 
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String StockId      = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"      	)); //재료번호
			String szCR_FRTOMOVE_GP		= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP"));		//냉연이송구분

			String CmbnBayChk   = ""; //복수동상차구분
			
			// PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", "YmCommCarMvSeEJB => procCmbnCarldYn", "APPPI0", "3", "*");
			
			if("PIDEV".equals("PIDEV")) {
				jrRtn = this.procCmbnCarldYn_PIDEV(rcvMsg);
				return jrRtn;
			}
			
			//차량정보 존재여부 체크 //////////////////////////////////////////////////////////////////////////////////
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"		, StockId);
 

			//차량정보 조회
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarwbookid 
			SELECT A.*
			  FROM TB_YD_CARSCH A
			     ,(
			       SELECT TRANS_ORD_DATE,TRANS_ORD_SEQNO
			         FROM USRYDA.TB_YD_STOCK
			         WHERE STL_NO= :V_STOCK_ID
			        UNION
			       SELECT NVL(TRANS_ORD_DATE2,SUBSTR(TRANS_WORD_NO,1,8))
			            , NVL(TRANS_ORD_SEQNO2,SUBSTR(TRANS_WORD_NO,9))
			        FROM USRYMA.TB_YM_STOCK
			        WHERE STOCK_ID =:V_STOCK_ID
			       ) B
			 WHERE A.TRANS_ORD_DATE=B.TRANS_ORD_DATE
			   AND A.TRANS_ORD_SEQNO=B.TRANS_ORD_SEQNO
			   AND DEL_YN   = 'N'
			 ORDER BY A.YD_CAR_SCH_ID DESC
			*/
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarwbookid", logId, methodNm, "설비상태조회");
			if (jsCarSch.size() <= 0) {
				commUtils.printLog(logId, methodNm+  "TB_YD_CARSCH[차량스케줄이 존재 안 합니다", "SL");
				return  jrRtn ;
			}
			
			jsCarSch.first();
			JDTORecord jrCarSch = jsCarSch.getRecord();
			String cmbnCarldYn  	= commUtils.nvl(jrCarSch.getFieldString("CMBN_CARLD_YN"),"N");	
			String ydCarWrkGp		= commUtils.nvl(jrCarSch.getFieldString("YD_CAR_WRK_GP"),"");
			String TelNo        	= commUtils.nvl(jrCarSch.getFieldString("TEL_NO"),"");	
			String ydCarNo	    	= commUtils.nvl(jrCarSch.getFieldString("CAR_NO"),"");    	
			String ydCardNo	    	= commUtils.nvl(jrCarSch.getFieldString("CARD_NO"),"");    			
			String ydStackColGp 	= commUtils.nvl(jrCarSch.getFieldString("YD_CARLD_STOP_LOC"),"");
			String TransOrdDate		= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_DATE"),"");
			String TransOrdSeqNo	= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_SEQNO"),"");
			String transEquipType	= commUtils.nvl(jrCarSch.getFieldString("TRANS_EQUIPMENT_TYPE"),"");
			String DriverName      	= commUtils.nvl(jrCarSch.getFieldString("DRIVER_NAME"),"");	
			
			//조합상차(시작:S , 종료: E ,  단일상차: N )
			if ("S".equals(cmbnCarldYn)) { 
				commUtils.printLog(logId, methodNm+  "★★★★★ 복수 상차 인 경우 ★★★★★", "SL");
				
				
				//자동차량출발 처리      //////////////////////////////////////////////////////////////////////////////				 
				// -----------------------------------------AB열연---------------------------------------------
				
				jrParam.setField("TRANS_ORD_DATE"	, TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	, TransOrdSeqNo);
				jrParam.setField("YD_STK_COL_GP"	, ydStackColGp);					
				jrParam.setField("DEL_YN"			, "Y");
				jrParam.setField("STOCK_MOVE_TERM" 	, "MG");
				jrParam.setField("MODIFIER"			, "복수상차");	 

								
				//저장품종료처리  
				EJBConnector ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
				ejbConn.trx("updYmStockTrnsOrdTX", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				
				
				commUtils.printLog(logId, methodNm+  "★자동차량출발", "SL");
					
				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);		//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("CARD_NO", 				ydCardNo);			
				recInTemp.setField("STACK_COL_GP", 			ydStackColGp);
				
				
				//자동차량출발 처리
				JDTORecord jrRtn1 = this.procFrtoCarLevWr(recInTemp);
				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);	
				
				
				jrParam 	= JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);		//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("TRANS_ORD_DATE"	, TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	, TransOrdSeqNo);
				jrParam.setField("YD_GP"			, ydGp );
				jrParam.setField("STOCK_ID"			, StockId);
				//복수창고 구분
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarYdCmbnCarldGP 
				WITH TEMP_TABLE AS (
				   SELECT  A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO , A.YD_GP
				     FROM(
				        SELECT B.YD_GP
				            , A.STL_NO ,A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO
				         FROM USRYDA.TB_YD_STOCK A
				            , TB_PT_COILCOMM B
				        WHERE A.STL_NO=B.COIL_NO 
				          AND A.TRANS_ORD_DATE=:V_TRANS_ORD_DATE
				          AND A.TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
				        UNION ALL
				        SELECT B.YD_GP 
				            , A.STOCK_ID,A.TRANS_ORD_DATE2,A.TRANS_ORD_SEQNO2
				         FROM USRYMA.TB_YM_STOCK A
				            , TB_PT_COILCOMM B
				        WHERE A.STOCK_ID=B.COIL_NO 
				         AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DATE
				         AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
				        ) A
				       GROUP BY  A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO, A.YD_GP
				)
				SELECT A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO , COUNT(YD_GP) AS CNT
				    ,(SELECT B.YD_GP FROM TEMP_TABLE B WHERE B.YD_GP <>:V_YD_GP) AS NEXT_YD_GP
				  FROM TEMP_TABLE A
				 GROUP BY A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO
				 HAVING COUNT(YD_GP)>1
				*/ 
				
				JDTORecordSet jsCmbn = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarYdCmbnCarldGP", logId, methodNm, "복수창고 구분");
				if (jsCmbn.size() <= 0) {
					commUtils.printLog(logId, methodNm+  "☆☆☆☆☆ 복수 창고가 아닌 경우 ☆☆☆☆☆", "SL");

					//복수동 CHECK
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschCarNoCardNoTransNoCHK 
					WITH TEMP_TABLE AS (
					SELECT :V_YD_GP AS YD_GP 
					     , :V_TRANS_ORD_DATE AS TRANS_ORD_DATE 
					     , :V_TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
					  FROM DUAL
					)
					SELECT COUNT(*) as CHK
					--SELECT * 
					 FROM (
					        SELECT SUBSTR(YD_CARPNT_CD,2,2) 
					         FROM (
					        SELECT A.STL_NO , B.YD_STK_COL_GP, SUBSTR(B.YD_STK_COL_GP,1,1) AS YD_GP  ,SUBSTR(B.YD_STK_COL_GP,3,2) AS SPAN_GP  ,SUBSTR(B.YD_STK_COL_GP,2,1) AS BAY_GP
					         FROM USRYDA.TB_YD_STOCK A
					             , USRYDA.TB_YD_STKLYR B
					             , TEMP_TABLE C
					        WHERE A.STL_NO=B.STL_NO
					           AND A.TRANS_ORD_DATE=C.TRANS_ORD_DATE
					           AND A.TRANS_ORD_SEQNO=C.TRANS_ORD_SEQNO
					           AND C.YD_GP ='J'
					         UNION ALL
					         SELECT A.STOCK_ID,B.STACK_COL_GP, SUBSTR(B.STACK_COL_GP,1,1) AS YD_GP  ,SUBSTR(B.STACK_COL_GP,3,2) AS SPAN_GP,SUBSTR(B.STACK_COL_GP,2,1) AS BAY_GP
					            FROM USRYMA.TB_YM_STOCK A
					                , USRYMA.TB_YM_STACKLAYER B
					                 , TEMP_TABLE C
					        WHERE A.STOCK_ID=B.STOCK_ID
					           AND A.TRANS_ORD_DATE2=C.TRANS_ORD_DATE
					           AND A.TRANS_ORD_SEQNO2=C.TRANS_ORD_SEQNO
					           AND C.YD_GP IN ('1','3')                
					              ) A
					            ,USRYDA.TB_YD_CARPOINT B
					         WHERE A.YD_GP=B.YD_GP
					            AND B.DEL_YN='N'
					            AND A.SPAN_GP BETWEEN B.YD_SPAN_FROM AND B.YD_SPAN_TO
					            AND SUBSTR(B.YD_CARPNT_CD,4,1)='1'
					            AND A.BAY_GP=B.YD_BAY_GP
					            AND A.STL_NO<> :V_STOCK_ID
					        GROUP BY     SUBSTR(YD_CARPNT_CD,2,2)
					    ) A 
					*/    
					JDTORecordSet jsCmbnBay = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschCarNoCardNoTransNoCHK", logId, methodNm, "복수동 구분");
					
					if (jsCmbnBay.size() > 0) {
						jsCmbnBay.first();
						CmbnBayChk     = StringHelper.evl(jsCmbnBay.getRecord(0).getFieldString("CHK"), "");
					} 
					
					//입동지시 호출/////////////////////////////////////////////////////////////////////////////////////
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("TC_CODE"			, "DMYDR061");
					recInTemp.setField("YD_GP"		        , ydGp);					
					recInTemp.setField("WORK_GP"		    , ydCarWrkGp);
					recInTemp.setField("TEL_NO"		        , TelNo);
					recInTemp.setField("TRANS_ORD_DT"		, TransOrdDate);
					recInTemp.setField("TRANS_ORD_SEQNO" 	, TransOrdSeqNo);
					recInTemp.setField("CAR_NO"				, ydCarNo);
					recInTemp.setField("CARD_NO"			, ydCardNo);
					recInTemp.setField("WAIT_ARR_DDTT"		, YmCommUtils.getCurDate("yyyyMMddHHmmss"));
					recInTemp.setField("WAIT_ARR_GP"		,"B");
					recInTemp.setField("DRIVER_NAME"		,DriverName);
					
					
					commUtils.printLog(logId, methodNm+  "복수동 존재 수량 체크"+CmbnBayChk, "SL");

					
					if (CmbnBayChk.equals("0")||CmbnBayChk.equals("1")) {
						recInTemp.setField("CMBN_CARLD_YN"	, "E");
					} else {
						recInTemp.setField("CMBN_CARLD_YN"	, "S");
					}
					
					EJBConnector ejbConn2 = new EJBConnector("default", "YmCommL3RcvSeEJB", this);
					JDTORecord jrRtn2 = (JDTORecord)ejbConn2.trx("procDMYDR061", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					jrRtn = commUtils.addSndData(jrRtn, jrRtn2);
					/////////////////////////////////////////////////////////////////////////////////////////////////
				}else{
					commUtils.printLog(logId, methodNm+  "☆☆☆☆☆ 복수 창고 인 경우 ☆☆☆☆☆", "SL");
					

					jsCmbn.first();
					JDTORecord jRCmbn 	= jsCmbn.getRecord();
					String ydGpNext		= commUtils.nvl(jRCmbn.getFieldString("NEXT_YD_GP"),""); 	

					jrParam.setField("YD_GP"			, ydGpNext );
					
	 				
					//다음 창고 도착 포인트
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarSch
					SELECT C.*
					 FROM USRYDA.TB_YD_STOCK A
					    , USRYDA.TB_YD_STKLYR B
					    , USRYDA.TB_YD_CARPOINT C
					WHERE A.STL_NO=B.STL_NO
					  AND A.TRANS_ORD_DATE=:V_TRANS_ORD_DATE
					  AND A.TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
					  AND SUBSTR(B.YD_STK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
					  AND SUBSTR(B.YD_STK_COL_GP,1,1)=C.YD_GP
					  AND 'J'=:V_YD_GP
					UNION ALL
					SELECT C.*
					 FROM USRYMA.TB_YM_STOCK A
					    , USRYMA.TB_YM_STACKLAYER B
					    , USRYDA.TB_YD_CARPOINT C
					WHERE A.STOCK_ID=B.STOCK_ID 
					 AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DATE
					 AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
					 AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
					 AND SUBSTR(B.STACK_COL_GP,1,1)=C.YD_GP
					 AND :V_YD_GP IN('1','3')
					*/
					JDTORecordSet jsNextYdGp = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarSch", logId, methodNm, "타 창고");
					
					if (jsNextYdGp.size() <= 0) {
						commUtils.printLog(logId, methodNm+  " 다음 창고 도착가능 포인트가 존재 안 합니다.", "SL");
						return  jrRtn ;
					}
					
					jsNextYdGp.first();
					JDTORecord jrNextYdGp = jsNextYdGp.getRecord();
					String WlocCd 	= commUtils.nvl(jrNextYdGp.getFieldString("WLOC_CD"),""); 	
					String ydPntCd	= commUtils.nvl(jrNextYdGp.getFieldString("YD_PNT_CD"),"");
					
					//다음 창고 입동TC 전송//////////////////////////////////////////////////////////////////////////////
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					if ("P".equals(transEquipType)) {
						
						//-------------------------------------------------------------------------
						//YdStockDao ydStockDao  = new YdStockDao();
						
						//rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						//JDTORecord recPara = JDTORecordFactory.getInstance().create();
						//recPara.setField("TRANS_ORD_DATE",  TransOrdDate);
						//recPara.setField("TRANS_ORD_SEQNO", TransOrdSeqNo);
						
												
						// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB*/
						//int nRet = ydStockDao.getYdStock(recPara, rsResult2, 731);
						//if (nRet > 0) {
						//	rsResult2.first();
						//	JDTORecord recTemp	= rsResult2.getRecord();			
						//	szCR_FRTOMOVE_GP	=  StringHelper.evl(recTemp.getFieldString("CR_FRTOMOVE_GP") , "");
						//}						
						//-------------------------------------------------------------------------
						
						jrParam.setField("TRANS_ORD_DATE2",  TransOrdDate);
						jrParam.setField("TRANS_ORD_SEQNO2", TransOrdSeqNo);
						
						// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
						rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockTransOrdDateAB", logId, methodNm, "운송지싱일자,순번으로 STOCK테이블에서 CR_FRTOMOVE_GP(냉연이송구분 )조회");
						if(rsResult2.size()>0) {
							szCR_FRTOMOVE_GP = StringHelper.evl(rsResult2.getRecord(0).getFieldString("CR_FRTOMOVE_GP"),"");
						}
						//-------------------------------------------------------------------------
						
						
						
						recInTemp.setField("JMS_TC_CD"			, "YDDMR070");
						recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
						recInTemp.setField("TC_CODE"			, "YDDMR070");
						recInTemp.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
						recInTemp.setField("CR_FRTOMOVE_GP"     , szCR_FRTOMOVE_GP);
						
					} else {
						recInTemp.setField("JMS_TC_CD"			, "YDDMR028");
						recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
						recInTemp.setField("TC_CODE"			, "YDDMR028");
						recInTemp.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
					}
					recInTemp.setField("TRANS_WORD_DATE"		, TransOrdDate);
					recInTemp.setField("TRANS_WORD_SEQNO" 		, TransOrdSeqNo);
					recInTemp.setField("CARD_NO"				, ydCardNo);
					recInTemp.setField("CAR_NO"					, ydCarNo);
					recInTemp.setField("WLOC_CD"				, WlocCd);
					recInTemp.setField("YD_PNT_CD"				, ydPntCd);
					recInTemp.setField("BAYIN_DDTT"		        , commUtils.getDateTime14());
					//복수창고인 경우 다음 창고로 대기 하기 위해서 다음과 같이 전송 한다.
					recInTemp.setField("YD_CARPNT_CD"			, "");	
					recInTemp.setField("LOAN_PULLOUT_ABLE_YN"	, "Y");
					
					jrRtn = commUtils.addSndData(jrRtn, recInTemp);	

				}
	
			}else{
				commUtils.printLog(logId, methodNm+  "★★★★★ 복수 상차가 아님 ★★★★★", "SL");
			}
			
		    
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}			

		return jrRtn;

	} // end of procCmbnCarldYn()   
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updYmStockTrnsOrdTX(JDTORecord jrParam) throws DAOException {
		String methodNm = "야드저장품 UPDATE[YmCommCarMvSeEJB.updYmStockTrnsOrdTX] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStockTrnsOrd 
			UPDATE USRYMA.TB_YM_STOCK
			   SET DEL_YN =:V_DEL_YN
			     , STOCK_MOVE_TERM =:V_STOCK_MOVE_TERM
			     , MODIFIER =:V_MODIFIER
			     , MOD_DDTT =SYSDATE
			 WHERE STOCK_ID IN (
			        SELECT A.STOCK_ID
			          FROM TB_YM_STOCK A
			             , TB_YM_STACKLAYER B
			         WHERE A.STOCK_ID=B.STOCK_ID
			           AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DATE
			           AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
			           AND B.STACK_COL_GP LIKE SUBSTR(:V_YD_STK_COL_GP,1,2)||'%'
			       )
			*/
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStockTrnsOrd", logId, methodNm, "저장품 수정");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 오퍼레이션명 : Ym 작업예약생성전 검사
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procYmWbookInsertCheck(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "Ym 작업예약생성[YmCommCarMvSeEJB.procYmWbookInsertCheck] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try{
			commUtils.printLog(logId, methodNm, "S+");
	    	//------------------------------------------------------------------------------------------------------------
	    	//	파라미터 확인
	    	//------------------------------------------------------------------------------------------------------------
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO")); 	 
		 	String ydBayGp 			= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP")); 	
		 	String ydGp 			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); 
		 	String TransOrdDate 	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_DATE")); 
		 	String TransOrdSeqNo 	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_SEQNO")); 
		 	String ydCarPntCd 		= commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"));  
		 	String ydEqpWrkStat 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_STAT"));   // 'U':상차적업,'L': 하차작업
		 	String modifier        	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 		  //수정자(Backup Only)
		 	String ydCarSchId      	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); 		  //수정자(Backup Only)

		 	String ydSchCd			= "";   
		 	
		 	jrRtn.setField("STAT"			, "1");

			/********************************
			 * 입동전 저장품 상태 체크 
			 *******************************/
			JDTORecord jrChkParam = JDTORecordFactory.getInstance().create();
			String sIPDONG_YN = "N";
			jrChkParam.setField("TRANS_ORD_DATE" , TransOrdDate); 
			jrChkParam.setField("TRANS_ORD_SEQNO", TransOrdSeqNo);
			jrChkParam.setField("YD_BAY_GP", ydBayGp);
			/*
			SELECT CASE WHEN MIN(CASE WHEN S2.STACK_LAYER_STAT = 'D' THEN '1' END ) > 0 THEN 'N' 
			       ELSE 'Y' END AS IPDONG_YN
			  FROM TB_YM_STACKLAYER SL
			     , TB_YM_STACKLAYER S2
			 WHERE 1 = 1
			   AND SL.DEL_YN      = 'N'
			   AND S2.STACK_COL_GP = SL.STACK_COL_GP 
			   AND S2.STACK_BED_GP IN (SL.STACK_BED_GP, CASE WHEN SL.STACK_LAYER_GP = '02' THEN SL.STACK_BED_GP
			                                                 ELSE LPAD(TO_NUMBER(SL.STACK_BED_GP) - 1, 2, '0') END
			                          )
			   AND S2.STACK_LAYER_GP IN (CASE WHEN SL.STACK_LAYER_GP = '02' THEN '02'
			                                  WHEN S2.STACK_BED_GP = TO_NUMBER(SL.STACK_BED_GP)  THEN '01'
			                                  ELSE '02' END , '02' )
			   AND SL.STOCK_ID IN (SELECT STOCK_ID
			                         FROM TB_YM_STOCK
			                        WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE
			                          AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
			                      )     
			   AND SUBSTR(SL.STACK_COL_GP,2,1) LIKE :V_YD_BAY_GP ||'%'                    
			 */
			JDTORecordSet rst = commDao.select(jrChkParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getChkBayInYn");
			if (rst.size() > 0) {
				sIPDONG_YN      = commUtils.trim(rst.getRecord(0).getFieldString("IPDONG_YN"));
			}
			
			if ("N".equals(sIPDONG_YN)) {
				jrRtn.setField("STAT"	, "-1");
				//jrRtn.setField("YD_MSG"	, "해당 저장품 상태 이상");
				jrRtn.setField("YD_MSG"	, "대상코일 상단에 다른 크레인작업 권하예약이 잡혀있습니다!");
				return jrRtn;
			}
			
		 	/**********************************************************
			* 1. 스케쥴 코드 등록여부 CHECK
			**********************************************************/
		 	
			// 출하 1통로 2통로 구분
		 	int intTransOrdSeqNo = StringHelper.parseInt(TransOrdSeqNo, 0);
		
		 	// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");

//			if ("Y".equals(sApplyYnPI)) {
				String[] rVal = commDao.getTrnFrtomoveGpPI("", methodNm, TransOrdDate, TransOrdSeqNo);
				String sTrnFrtomoveGp = rVal[0];
				String hIssueGp = rVal[1];
				
				commUtils.printLog(logId, "sTrnFrtomoveGp:" + sTrnFrtomoveGp + "," + "hIssueGp:" + hIssueGp, "SL");
				
			 	if (intTransOrdSeqNo > 999000) {
					// 반품,회송,부분하차	
					if (ydCarPntCd.substring(1,2).equals("2")) {
						ydSchCd = "3" + ydBayGp + "PT05LM";
					} else {
						ydSchCd = "3" + ydBayGp + "PT01LM";
					}
//				} else if ( "21".equals(sTrnFrtomoveGp) ) {
//					// 제품이송
//					if (ydEqpWrkStat.equals("U")) {
//						//상차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT26UM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT22UM";
//						}
//					} else {
//						//하차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT26LM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT22LM";
//						}
//					}
				} else if ( "21".equals(sTrnFrtomoveGp) && "K".equals(hIssueGp) ) {
					// 제품이송
					if (ydEqpWrkStat.equals("U")) {
						//상차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT26UM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT22UM";
						}
					} else {
						//하차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT26LM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT22LM";
						}
					}
				} else if ( "22".equals(sTrnFrtomoveGp) && "9".equals(hIssueGp) ) {
					// 제품이송
					if (ydEqpWrkStat.equals("U")) {
						//상차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT26UM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT22UM";
						}
					} else {
						//하차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT26LM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT22LM";
						}
					}
//					} else if ( "23".equals(sTrnFrtomoveGp) ) {
//					//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
//					if (ydEqpWrkStat.equals("U")) {
//						//상차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT16UM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT12UM";
//						}
//					} else {
//						//하차
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT16LM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT12LM";
//						}
//					}
				} else if ( ("21".equals(sTrnFrtomoveGp) && "C".equals(hIssueGp))
							||
							("21".equals(sTrnFrtomoveGp) && "D".equals(hIssueGp)) ) {
					// 이송출고
					if (ydEqpWrkStat.equals("U")) {
						//상차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT16UM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT12UM";
						}
					} else {
						//하차
						if (ydCarPntCd.substring(1,2).equals("2")) {
							ydSchCd = "3" + ydBayGp + "PT16LM";
						} else {
							ydSchCd = "3" + ydBayGp + "PT12LM";
						}
					}
				} else {
						/* 사내임시야적장냉연재이송작업적용
					 	 * 진도코드 3일경우 스케줄코드 이송상차로 진행 20200723
					 	 */
					 	String sYM2017 = ymComm.BCoilApplyYn("YM2017","3","TEMP_STK");
					 	
						//운송지시순번으로 대상재의 진도코드 조회
						JDTORecord jrTransParam = JDTORecordFactory.getInstance().create();
						jrTransParam.setResultCode(logId);	//Log ID
						jrTransParam.setResultMsg(methodNm);	//Log Method Name
						jrTransParam.setField("TRANS_ORD_DT", 			TransOrdDate);
						jrTransParam.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
				    	/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilTransCurrProgInfo
							SELECT  A.STOCK_ID
							      , B.CURR_PROG_CD
							      , B.NEXT_PROC           -- 차공정
							  FROM USRYMA.TB_YM_STOCK A
							     , USRPTA.TB_PT_COILCOMM B
							 WHERE A.STOCK_ID = B.COIL_NO
							   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DT
							   AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
							   AND A.DEL_YN = 'N'
						*/   
						JDTORecordSet rsTransResult = commDao.select(jrTransParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilTransCurrProgInfo", logId, methodNm, "대상재 진도코드 조회");
						String currProgCd = "";
						if (rsTransResult != null && rsTransResult.size() > 0) {
							currProgCd   = rsTransResult.getRecord(0).getFieldString("CURR_PROG_CD"); //작업 크레인
						}
						
						if(sYM2017.equals("Y") && currProgCd.equals("3")){
							//이송상차
							if (ydCarPntCd.substring(1,2).equals("2")) {
								ydSchCd = "3" + ydBayGp + "PT06UM";
							} else {
								ydSchCd = "3" + ydBayGp + "PT02UM";
							}
						}else{
							// 출하
							if (ydCarPntCd.substring(1,2).equals("2")) {
								ydSchCd = "3" + ydBayGp + "PT05UM";
							} else {
								ydSchCd = "3" + ydBayGp + "PT01UM";
							}
						}

					}					
				
//			} else {
//
//			 	if (intTransOrdSeqNo > 999000) {
//					// 반품,회송,부분하차	
//						if (ydCarPntCd.substring(1,2).equals("2")) {
//							ydSchCd = "3" + ydBayGp + "PT05LM";
//						} else {
//							ydSchCd = "3" + ydBayGp + "PT01LM";
//						} 
//						
//				 	} else if ((intTransOrdSeqNo > 800000)) {
//						// 제품이송
//						if (ydEqpWrkStat.equals("U")) {
//							//상차
//							if (ydCarPntCd.substring(1,2).equals("2")) {
//								ydSchCd = "3" + ydBayGp + "PT26UM";
//							} else {
//								ydSchCd = "3" + ydBayGp + "PT22UM";
//							}
//						} else {
//							//하차
//							if (ydCarPntCd.substring(1,2).equals("2")) {
//								ydSchCd = "3" + ydBayGp + "PT26LM";
//							} else {
//								ydSchCd = "3" + ydBayGp + "PT22LM";
//							}
//						}
//					} else if ((intTransOrdSeqNo > 700000 && intTransOrdSeqNo <= 800000)) {
//						//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
//						if (ydEqpWrkStat.equals("U")) {
//							//상차
//							if (ydCarPntCd.substring(1,2).equals("2")) {
//								ydSchCd = "3" + ydBayGp + "PT16UM";
//							} else {
//								ydSchCd = "3" + ydBayGp + "PT12UM";
//							}
//						} else {
//							//하차
//							if (ydCarPntCd.substring(1,2).equals("2")) {
//								ydSchCd = "3" + ydBayGp + "PT16LM";
//							} else {
//								ydSchCd = "3" + ydBayGp + "PT12LM";
//							}
//						}
//					
//					} else {
//						
//						/* 사내임시야적장냉연재이송작업적용
//					 	 * 진도코드 3일경우 스케줄코드 이송상차로 진행 20200723
//					 	 */
//					 	String sYM2017 = ymComm.BCoilApplyYn("YM2017","3","TEMP_STK");
//					 	
//						//운송지시순번으로 대상재의 진도코드 조회
//						JDTORecord jrTransParam = JDTORecordFactory.getInstance().create();
//						jrTransParam.setResultCode(logId);	//Log ID
//						jrTransParam.setResultMsg(methodNm);	//Log Method Name
//						jrTransParam.setField("TRANS_ORD_DT", 			TransOrdDate);
//						jrTransParam.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
//				    	/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilTransCurrProgInfo
//							SELECT  A.STOCK_ID
//							      , B.CURR_PROG_CD
//							      , B.NEXT_PROC           -- 차공정
//							  FROM USRYMA.TB_YM_STOCK A
//							     , USRPTA.TB_PT_COILCOMM B
//							 WHERE A.STOCK_ID = B.COIL_NO
//							   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DT
//							   AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
//							   AND A.DEL_YN = 'N'
//						*/   
//						JDTORecordSet rsTransResult = commDao.select(jrTransParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilTransCurrProgInfo", logId, methodNm, "대상재 진도코드 조회");
//						String currProgCd = "";
//						if (rsTransResult != null && rsTransResult.size() > 0) {
//							currProgCd   = rsTransResult.getRecord(0).getFieldString("CURR_PROG_CD"); //작업 크레인
//						}
//						
//						if(sYM2017.equals("Y") && currProgCd.equals("3")){
//							//이송상차
//							if (ydCarPntCd.substring(1,2).equals("2")) {
//								ydSchCd = "3" + ydBayGp + "PT06UM";
//							} else {
//								ydSchCd = "3" + ydBayGp + "PT02UM";
//							}
//						}else{
//							// 출하
//							if (ydCarPntCd.substring(1,2).equals("2")) {
//								ydSchCd = "3" + ydBayGp + "PT05UM";
//							} else {
//								ydSchCd = "3" + ydBayGp + "PT01UM";
//							}
//						}
//					}					
//			}
 
			//스케줄코드로 스케줄기준Table조회
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_SCH_CD", ydSchCd);
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			if (rsResult != null && rsResult.size() > 0) {

			} else {
				
				jrRtn.setField("STAT"			, "-1");
				jrRtn.setField("YD_MSG"			, "B열연 코일 스케쥴 코드 이상 : [" + ydSchCd + "]");
				return jrRtn;
//SJH				throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + ydSchCd + "]");
			}	
			
		 	/**********************************************************
			* 2. 작업예약 존재 여부 CHECK
			**********************************************************/

			jrParam.setField("TRANS_ORD_DT", 			TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockWbookcheck 
			SELECT B.YD_WBOOK_ID
			  FROM USRYMA.TB_YM_STOCK A
			     , USRYMA.TB_YM_WRKBOOK B
			     , USRYMA.TB_YM_WRKBOOKMTL C
			 WHERE B.YD_WBOOK_ID=C.YD_WBOOK_ID
			   AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DT
			   AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
			   AND A.STOCK_ID = C.STOCK_ID
			   AND A.DEL_YN = 'N'
			   AND B.DEL_YN = 'N'
			   AND C.DEL_YN = 'N'
			   AND (SUBSTR(B.YD_SCH_CD,3,2) IN ('YD') OR  SUBSTR(B.YD_SCH_CD,3,4) = 'TC12')                      
			   AND B.YD_WBOOK_ID NOT IN ( SELECT YD_WBOOK_ID FROM TB_YM_CRNSCH WHERE DEL_YN = 'N' )
			GROUP BY B.YD_WBOOK_ID     
			 */  
			
			JDTORecordSet jsWbookMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockWbookcheck", logId, methodNm, "작업예약 조회");
		 	if (jsWbookMtl.size() > 0) {
				commUtils.printLog(logId, "이적 및 대차출하 작업예약이 존재 합니다", "SL");
				
				for (int Loop_i = 1; Loop_i <= jsWbookMtl.size() ; Loop_i++) {
					
					jsWbookMtl.absolute(Loop_i);
					JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
					jrInPara.setRecord(jsWbookMtl.getRecord());
					jrInPara.setResultCode(logId);			//Log ID
					jrInPara.setResultMsg(methodNm);		//Log Method Name
					jrInPara.setField("MODIFIER" 			, modifier); //수정자
					//크레인 작업예약 삭제
					EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
					ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrInPara });
					
					
				}
			}

		 	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookStockYN /   
		 	SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
		 	  FROM TB_YM_WRKBOOKMTL WM
		 	     , TB_YM_WRKBOOK    WB
		 	WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
		 	  AND WM.DEL_YN      = 'N'
		 	  AND WB.DEL_YN      = 'N'
		 	  AND WM.STOCK_ID    IN (SELECT STOCK_ID 
		 	                           FROM TB_YM_STOCK 
		 	                          WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE 
		 	                            AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
		 	                            AND DEL_YN = 'N')
			*/  	  
			jrParam.setField("TRANS_ORD_DATE", 			TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
			JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmwBookStockYN", logId, methodNm, "작업예약 등록여부");
			
			commUtils.printLog(logId, "작업 예약 편성여부:" + jsChk2.getRecord(0).getFieldString("WB_STL_YN") , "SL");
			
			if (jsChk2 != null && jsChk2.size() > 0) {
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN"))) {
					
					jrRtn.setField("STAT"			, "-1");
					jrRtn.setField("YD_MSG"			, "이미 다른 크레인예약이  되어 있슴:크레인 스케쥴 확인");
					return jrRtn;					
				}
			}			
				 	
		 	
		 	/**********************************************************
			* 3. 운송지시 저장품대상 CHECK
			**********************************************************/

		 	JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
			jrInPara.setField("TRANS_ORD_DT", 			TransOrdDate);
			jrInPara.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
			jrInPara.setField("YD_BAY_GP", 				ydBayGp);
			jrInPara.setField("YD_GP", 					ydGp);
			jrInPara.setField("YD_CARPNT_CD", 			ydCarPntCd);
			jrInPara.setField("CAR_NO",					ydCarNo);
			jrInPara.setField("MODIFIER",				modifier);	
			JDTORecordSet jsStock  = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			//하차시 야드에 재료정보가 없으므로 상차일 경우에만 체크
			if (ydEqpWrkStat.equals("U")) {
				//반품
				if (intTransOrdSeqNo > 999000) {

					//작업예약재료를 생성하기 위해 rsResult 에 하차 대상 재료를 조회한다.
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarftmvmtl 
					--하차대상제 조회  
					SELECT A.YD_CARUD_STOP_LOC   AS STACK_COL_GP
					     , B.YD_STK_BED_NO       AS STACK_BED_GP
					     , '01' AS YD_STK_LYR_NO AS STACK_LAYER_GP
					     , B.STL_NO              AS STOCK_ID 
					  FROM TB_YD_CARSCH A
					     , TB_YD_CARFTMVMTL B
					 WHERE A.CAR_NO = :V_CAR_NO
					   AND A.TRANS_ORD_DATE = :V_TRANS_ORD_DT
					   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					   AND A.DEL_YN = 'N'
					   AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
					 ORDER BY B.YD_STK_BED_NO
					*/
					jrParam.setField("TRANS_ORD_DT"		,TransOrdDate);
					jrParam.setField("TRANS_ORD_SEQNO"	,TransOrdSeqNo);
					jrParam.setField("CAR_NO"			,ydCarNo);
					jsStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarftmvmtl", logId, methodNm, "하차대상재 조회");
				 	if (jsStock.size() <= 0) {
				 		
						jrRtn.setField("STAT"			, "-1");
						jrRtn.setField("YD_MSG"			, "운송지시 저장품대상이 없습니다:코일정보 확인");
						return jrRtn;
					}				
				} else {
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockTransOrdDTWbook /
					SELECT A.STOCK_ID  
					     , SUBSTR(B.STACK_COL_GP,2,1) AS YD_BAY_GP
					     , B.STACK_COL_GP 
					     , B.STACK_BED_GP 
					     , B.STACK_LAYER_GP
					  FROM USRYMA.TB_YM_STOCK A
					     , USRYMA.TB_YM_STACKLAYER B
					     , USRYDA.TB_YD_CARPOINT C
					 WHERE A.STOCK_ID =B.STOCK_ID
					   AND A.TRANS_ORD_DATE2 =:V_TRANS_ORD_DT
					   AND A.TRANS_ORD_SEQNO2 =:V_TRANS_ORD_SEQNO
					   AND B.STACK_COL_GP LIKE :V_YD_GP ||:V_YD_BAY_GP ||'%'
					   AND C.YD_CARPNT_CD=:V_YD_CARPNT_CD
					   AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN '00' AND '99'
					   AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
					   AND B.STACK_LAYER_STAT IN ('C')
					   ORDER BY B.STACK_LAYER_GP DESC 
					*/
					jrParam.setField("TRANS_ORD_DT"		,TransOrdDate);
					jrParam.setField("TRANS_ORD_SEQNO"	,TransOrdSeqNo);
					jrParam.setField("YD_GP"			,ydGp);
					jrParam.setField("YD_BAY_GP"		,ydBayGp);
					jrParam.setField("YD_CARPNT_CD"		,ydCarPntCd);
					jsStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockTransOrdDTWbook", logId, methodNm, "작업대상 조회");
				 	if (jsStock.size() <= 0) {
					
						jrRtn.setField("STAT"			, "-1");
						jrRtn.setField("YD_MSG"			, "운송지시 저장품대상이 없습니다:코일정보 확인");
						return jrRtn;
					}
				}
				if (intTransOrdSeqNo > 999000) {
					
				} else { 
				 	/**********************************************************
					* 4. 운송지시갯수와 LAYER 갯수 확인  CHECK
					**********************************************************/
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockLayerCntChk 
					SELECT CASE WHEN STOCK_CNT = LAYER_CNT THEN 'Y' --정상
					            ELSE 'N' END AS STOCK_LAYER_CHK     --비정상
					            
					  FROM
					       (
					        SELECT COUNT(*) AS STOCK_CNT
					          FROM TB_YM_STOCK
					         WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DT
					           AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
					       ) A
					     , (
					        SELECT COUNT(*) AS LAYER_CNT
					          FROM TB_YM_STACKLAYER
					         WHERE STOCK_ID IN (
					                             SELECT STOCK_ID
					                               FROM TB_YM_STOCK
					                              WHERE TRANS_ORD_DATE2  = :V_TRANS_ORD_DT
					                                AND TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
					                           )    
					           AND STACK_LAYER_STAT IN ('C','U')                
					                               
					       ) B
					  WHERE STOCK_CNT <> LAYER_CNT 
					*/
					jrParam.setField("TRANS_ORD_DT"		,TransOrdDate);
					jrParam.setField("TRANS_ORD_SEQNO"	,TransOrdSeqNo);
					JDTORecordSet jsStockCnt = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStockLayerCntChk", logId, methodNm, "작업대상갯수 조회");
				 	if (jsStockCnt.size() <= 0) {
					
						jrRtn.setField("STAT"			, "-1");
						jrRtn.setField("YD_MSG"			, "운송지시갯수와  저장위치 저장품갯수가 틀림:코일정보 확인");
						return jrRtn;
					}
				}	
			}
			
			
			
			//출력 값
	    	jrRtn.setField("STAT"			, "1");
			jrRtn.setField("YD_MSG"			, "정상");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	
	
    /**
     * 오퍼레이션명 : 차량동간이적기능(신)
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public JDTORecord procTraillerMoveSch (JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "차량동간이적기능(신) [YmCommCarMvSeEJB.procTraillerMoveSch] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult2 = null;
		
		try{		
 
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydSchCd      = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"  	)); //야드구분
			String StockId      = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"     	));
			String modifier	    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"		));		
			String ydDnWrLoc	= commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"	));	
			String ydUpWrLoc	= commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"	));	
			String ydCrnSchId	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"	));	
			String ydEqpId		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"	));	

			commUtils.printLog(logId, "ydCrnSchId:"+ydCrnSchId, "SL");
        	
        	// 상차작업 인 경우(스케줄 코드로 구분)			
        	// 1. 하차지 차량  작업예약을 생성 한다.
        	// 2. 상차매수를 체크 해서 하차지 작업예약ID로 하차지 스케줄을 호출 한다.  			
        	// 하차작업 인 경우	
        	// 1. 하차매수를 체크 해서 상차지 작업예약id로 상차지 스케줄을 호출 한다.

        	//************************************************************************************************************
        	// 상차 작업
        	if (ydSchCd.substring(6, 7).equals("U")) {
        		
    			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
    			jrParam.setResultCode(logId);	//Log ID
    			jrParam.setResultMsg(methodNm);	//Log Method Name
    			jrParam.setField("YD_SCH_CD"		, ydSchCd);
    			jrParam.setField("STOCK_ID"		    , StockId);
    			jrParam.setField("YD_DN_WR_LOC"		, ydDnWrLoc);
    			jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchId     ); 
    			jrParam.setField("STACK_COL_GP"  	, ydDnWrLoc.substring(0, 6) ); 
    			jrParam.setField("YD_EQP_ID"     	, ydEqpId); 
    			
    			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAxYML009CarSchLd 
    			WITH DATA_TBL AS (
    			SELECT CARUNLOAD_BAY, CTS_RELAY_SADDLE 
    			  FROM TB_YM_STOCK
    			 WHERE STOCK_ID = :V_STOCK_ID

    			)
    			SELECT A.YD_CAR_SCH_ID
    			     , A.YD_CAR_USE_GP 
    			     , A.CAR_NO
    			     , A.CARD_NO
    			     , A.BAY_WRK_CNT     -- 해당상차도 작업대상
    			     , A.BAY_WRK_END_CNT -- 해당 상차도 작업완료
    			     , A.TRANS_ORD_DATE
    			     , A.TRANS_ORD_SEQNO
    			     , A.STACK_COL_GP
    			     , A.WLOC_CD
    			     , A.YD_PNT_CD
    			     , (SELECT YD_CARPNT_CD FROM TB_YD_CARPOINT WHERE YD_STK_COL_GP = A.STACK_COL_GP) AS YD_CARPNT_CD
    			     , A.YD_CAR_PROG_STAT                                 -- 차량진행
    			     , CASE WHEN A.BAY_WRK_CNT <= A.BAY_WRK_END_CNT THEN 'Y' ELSE'N' END AS CAR_LD_CMPL_YN      --해당상차도 [1123]경기99사1123
    			     , CARUNLOAD_BAY AS TO_BAY_GP 
    			     , CASE WHEN A.CTS_RELAY_SADDLE IN  ('1') THEN '3'||A.CARUNLOAD_BAY||'PT07LM' 
    			            WHEN A.CTS_RELAY_SADDLE IN  ('2') THEN '3'||A.CARUNLOAD_BAY||'PT03LM' 
    			            WHEN A.CTS_RELAY_SADDLE IN  ('3') THEN '3'||A.CARUNLOAD_BAY||'PT03LM' 
    			            WHEN A.CTS_RELAY_SADDLE IN  ('4') THEN '3'||A.CARUNLOAD_BAY||'PT07LM' 
    			            END AS TO_YD_SCH_CD 
    			  FROM
    			       (
    			        SELECT TS.YD_CAR_SCH_ID
    			             , TS.YD_CAR_USE_GP
    			             , TS.CAR_NO
    			             , TS.CARD_NO
    			             , TS.TRANS_ORD_DATE
    			             , TS.TRANS_ORD_SEQNO
    			             , TS.DEST_TEL_NO
    			             , TS.YD_CAR_PROG_STAT
    			             , TS.TRANS_EQUIPMENT_TYPE
    			             , TS.CMBN_CARLD_YN
    			             , SC.STACK_COL_GP
    			             , SC.WLOC_CD
    			             , SC.YD_PNT_CD
    			             , ST.CARUNLOAD_BAY
    			             , ST.CTS_RELAY_SADDLE 
    			         -- 해당상차도 작업대상 건수   
    			            , 
    			             -- 출하
    			                (SELECT COUNT(DISTINCT(B.STOCK_ID)) 
    			                   FROM TB_YM_STOCK B
    			                      , TB_YD_CARPOINT C
    			                      , TB_YM_STACKLAYER D
    			                  WHERE B.STOCK_ID  = D.STOCK_ID
    			                    AND B.TRANS_ORD_DATE2  = TS.TRANS_ORD_DATE
    			                    AND B.TRANS_ORD_SEQNO2 = TS.TRANS_ORD_SEQNO
    			                    AND SUBSTR(D.STACK_COL_GP,2,1) = SUBSTR(C.YD_STK_COL_GP,2,1)
    			                    AND C.YD_STK_COL_GP    = SC.STACK_COL_GP
    			                    AND D.STACK_LAYER_STAT IN ('U','C')
    			                    )
    			                      
    			               AS BAY_WRK_CNT    
    			               
    			         -- 해당상차도 작업완료 건수                 
    			             ,  (SELECT COUNT(DISTINCT(A.COIL_NO))
    			                   FROM TB_PT_COILCOMM A
    			                  WHERE SUBSTR(A.YD_STR_LOC,3,2) IN ('PT') 
    			                    AND A.COIL_NO  IN (SELECT B.STOCK_ID
    			                                         FROM TB_YM_STOCK B
    			                                            , TB_YD_CARPOINT C
    			                                            , TB_YM_STACKLAYER D
    			                                        WHERE B.STOCK_ID  = D.STOCK_ID
    			                                          AND B.TRANS_ORD_DATE2  = TS.TRANS_ORD_DATE
    			                                          AND B.TRANS_ORD_SEQNO2 = TS.TRANS_ORD_SEQNO
    			                                          AND SUBSTR(D.STACK_COL_GP,2,1) = SUBSTR(C.YD_STK_COL_GP,2,1)
    			                                          AND C.YD_STK_COL_GP = SC.STACK_COL_GP
    			                                          AND D.STACK_LAYER_STAT IN ('C')
    			                                          )
    			                )   
    			               AS BAY_WRK_END_CNT   
    			          FROM TB_YM_STACKCOL SC
    			             , USRYDA.TB_YD_CARSCH TS
    			             , DATA_TBL ST
    			         WHERE SC.STACK_COL_GP = :V_STACK_COL_GP
    			           AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
    			           AND SC.YD_CAR_USE_GP = 'G' 
    			           AND SC.CAR_NO = TS.CAR_NO
    			           AND TS.DEL_YN = 'N'
    			        ) A
    			*/
    			JDTORecordSet jsCarUpSch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAxYML009CarSchLd", logId, methodNm, "상차 차량스케줄 조회 "); 
    	    	
    			if (jsCarUpSch.size() > 0) {
    				JDTORecord jrCarUpSch = jsCarUpSch.getRecord(0);

    				String ydCarSchId  	= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_SCH_ID"));
    				String CarNo 		= commUtils.trim(jrCarUpSch.getFieldString("CAR_NO")); 
    				String CardNo 		= commUtils.trim(jrCarUpSch.getFieldString("CARD_NO")); 
    				String ydCarPntCd	= commUtils.trim(jrCarUpSch.getFieldString("YD_CARPNT_CD")); 
    				String TransOrdDate	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_DATE"));
    				String TransOrdSeqNo= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_SEQNO")); 
    				String WlocCd 		= commUtils.trim(jrCarUpSch.getFieldString("WLOC_CD")); 
    				String ydPndCd 		= commUtils.trim(jrCarUpSch.getFieldString("YD_PNT_CD")); 					
    				String carLdCmplYn 	= commUtils.trim(jrCarUpSch.getFieldString("CAR_LD_CMPL_YN")); 		//차량상차완료여부
    				String ydCarProgStat= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_PROG_STAT")); 	//차량진행상태
    				String sToBayGp 	= commUtils.trim(jrCarUpSch.getFieldString("TO_BAY_GP")); 			//TO동		
    				String sToYdSchCd 	= commUtils.trim(jrCarUpSch.getFieldString("TO_YD_SCH_CD")); 	    //TO스케쥴 코드
    				String sPT_LOC   	= commUtils.trim(jrCarUpSch.getFieldString("CTS_RELAY_SADDLE")); 	//방향
    				
    				
    				String ydSchPriorNew = "";
    				String ydEqpIdNew    = "";
    				commUtils.printLog(logId, methodNm+  "★★★★★  권하차량스케쥴:" + ydCarSchId + " 차량상차완료:" + carLdCmplYn+ " ★★★★", "SL");
    				
    				//차량이송재료(TB_YM_CARFTMVMTL) 상차 등록
    				jrParam = JDTORecordFactory.getInstance().create();
    				jrParam.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
    				jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
    				jrParam.setField("STOCK_ID"		 , StockId);
    				jrParam.setField("MODIFIER" 	 , modifier     ); 
    				jrParam.setField("YD_DN_WR_LOC"  , ydDnWrLoc   ); //야드권하실적위치
    				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insAxYML009CarMtlIns
    				--크레인권하실적 차량이송재료 등록 
    				INSERT INTO USRYDA.TB_YD_CARFTMVMTL
    				     ( YD_CAR_SCH_ID
    				     , STL_NO   
    				     , REGISTER     
    				     , REG_DDTT     
    				     , MODIFIER     
    				     , MOD_DDTT 
    				     , DEL_YN       
    				     , YD_STK_BED_NO
    				     , YD_STK_LYR_NO   
    				) 
    				VALUES 
    				(
    				        :V_YD_CAR_SCH_ID
    				      , :V_STOCK_ID
    				      , :V_MODIFIER     
    				      , SYSDATE         
    				      , :V_MODIFIER     
    				      , SYSDATE         
    				      , 'N'             
    				      , NVL(SUBSTR(:V_YD_DN_WR_LOC,-2),'01')
    				      , '001' 
    				      )
    				*/    	
    				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insAxYML009CarMtlIns", logId, methodNm, " 상차 이송재료 등록 ");
    				
    				//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
    				jrParam = JDTORecordFactory.getInstance().create();
    				jrParam.setResultCode(logId);	//Log ID
    				jrParam.setResultMsg(methodNm);	//Log Method Name
    				
    				if ("Y".equals(carLdCmplYn)) {              //해당동만 완료이면 차량상태 완료처리함
    					jrParam.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					} else {
						jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					}
    				jrParam.setField("STACK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
    				jrParam.setField("WR_DT" 			, commUtils.getDateTime14() ); 
    				jrParam.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
    				jrParam.setField("MODIFIER"         , modifier     );
    				
    				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML009CarSchLd 
    				--크레인권하실적 상차 차량스케줄 수정
    				UPDATE USRYDA.TB_YD_CARSCH TS
    				   SET TS.MODIFIER             = :V_MODIFIER
    				     , TS.MOD_DDTT             = SYSDATE
    				     , TS.YD_EQP_WRK_STAT      = 'L' --영차
    				     , TS.YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT   
    				     , TS.YD_EQP_WRK_SH        = (SELECT COUNT(*) 
    				                                    FROM TB_YD_CARFTMVMTL 
    				                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
    				     , TS.YD_EQP_WRK_WT        = (SELECT SUM(COIL_WT) 
    				                                    FROM TB_YD_CARFTMVMTL A
    				                                       , USRPTA.TB_PT_COILCOMM   B
    				                                   WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
    				                                     AND A.STL_NO        = B.COIL_NO
    				                                   
    				                                   )
    				--     , TS.ARR_WLOC_CD          = NVL(TS.ARR_WLOC_CD,DD.ARR_WLOC_CD)
    				     , TS.YD_PNT_CD3           = '0000'
    				     , TS.YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
    				     , TS.YD_CARLD_STOP_LOC    = :V_STACK_COL_GP
    				     , TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE))
    				     , TS.YD_CARLD_CMPL_DT     = DECODE(:V_YD_CAR_PROG_STAT,'5',NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE),NULL)
    				   
    				WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
    				*/    
    				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML009CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
					commUtils.printLog(logId, methodNm+  "상차지 완료 여부: " + carLdCmplYn, "SL");

    				if ("Y".equals(carLdCmplYn)) {
			        // 하차지 작업 예약 생성
						
						JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
						recInTemp1.setField("YD_SCH_CD", sToYdSchCd);
				    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
						SELECT YD_SCH_CD
						     , YD_WRK_CRN
						     , YD_WRK_CRN_PRIOR
						  FROM TB_YM_SCHEDULERULE
						 WHERE DEL_YN = 'N'
						   AND YD_SCH_CD = :V_YD_SCH_CD
						*/   
						JDTORecordSet jsResult = commDao.select3(recInTemp1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
				    	
						if (jsResult != null && jsResult.size() > 0) {
							ydSchPriorNew = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
							ydEqpIdNew    = jsResult.getRecord(0).getFieldString("YD_WRK_CRN");
						} 
	
						/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getDest 
						SELECT STACK_COL_GP,STACK_BED_GP,STACK_LAYER_GP,STOCK_ID,
						  FROM TB_YM_STACKLAYER A1 
						 WHERE A1.STACK_COL_GP = SUBSTR(:V_YD_DN_WR_LOC,1,6)  -- 차량위 갯수 
						   AND STACK_LAYER_STAT = 'C'  
						*/   
						JDTORecordSet jsWbookSearch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getDest", logId, methodNm, "목적동 대상재 조회"); 
				    	
						if (jsWbookSearch.size() > 0) {
							//작업예약ID 조회
							String ydWbookIdNew = "";
							JDTORecord jrWbookSearch= JDTORecordFactory.getInstance().create();
							JDTORecord jrWbook    	= JDTORecordFactory.getInstance().create();
							JDTORecord jrWbookMtl 	= JDTORecordFactory.getInstance().create();
							
							for(int Loop_i = 1; Loop_i <= jsWbookSearch.size() ; Loop_i++) {
								jsWbookSearch.absolute(Loop_i);
								jrWbookSearch = jsWbookSearch.getRecord();
								
								String sStockId = commUtils.trim(jrWbookSearch.getFieldString("STOCK_ID"));
								
								ydWbookIdNew = commDao.getSeqId(logId, methodNm, "WrkBook");
								
								if (!"".equals(ydWbookIdNew)) {
									jrWbook    = JDTORecordFactory.getInstance().create();
									//작업예약 등록
									jrWbook.setField("YD_WBOOK_ID"       , ydWbookIdNew     ); //야드작업예약ID
									jrWbook.setField("MODIFIER"          , modifier      ); //수정자
									jrWbook.setField("YD_GP"             , "3"           ); //야드구분
									jrWbook.setField("YD_BAY_GP"         , sToBayGp      ); //야드동구분
									jrWbook.setField("YD_SCH_CD"         , sToYdSchCd    ); //야드스케쥴코드
									jrWbook.setField("YD_SCH_PRIOR"      , ydSchPriorNew ); //야드스케쥴우선순위
									jrWbook.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
									jrWbook.setField("YD_SCH_ST_GP"      , "O"           ); //야드스케쥴기동구분(Manual)
									jrWbook.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
									jrWbook.setField("YD_WRK_PLAN_CRN"   , ydEqpIdNew    ); //작업예약 크레인
									jrWbook.setField("CAR_NO"   		 , CarNo    	 ); 
									jrWbook.setField("CARD_NO"   		 , CardNo      	 ); 
									jrWbook.setField("YD_CAR_USE_GP"     , "G"           ); 
									
									int ins_cnt = commDao.insert(jrWbook, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "TB_YM_WRKBOOK");
									if (ins_cnt > 0) {
										jrWbookMtl = JDTORecordFactory.getInstance().create();
										//작업예약 등록
										jrWbookMtl.setField("YD_WBOOK_ID"      , ydWbookIdNew     ); //야드작업예약ID
										jrWbookMtl.setField("MODIFIER"         , modifier      ); //수정자
										jrWbookMtl.setField("STOCK_ID"         , sStockId);
										jrWbookMtl.setField("STACK_BED_GP"     , ydDnWrLoc.substring(6, 8));
		
										commDao.insert(jrWbookMtl, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
									}
								}
							}
						}	
		        	
		    			// 마지막 작업 마치고 차량 정보 초기화 호출
						JDTORecord jrSnd = JDTORecordFactory.getInstance().create();
						jrSnd.setResultCode(logId);	//Log ID
						jrSnd.setResultMsg(methodNm);	//Log Method Name
						jrSnd.setField("TRANS_ORD_DATE"		, TransOrdDate);
						jrSnd.setField("TRANS_ORD_SEQNO"	, TransOrdSeqNo);
						jrSnd.setField("CAR_NO"				, CarNo);
						jrSnd.setField("CARD_NO"			, CardNo);
						jrSnd.setField("SPOS_WLOC_CD"		, WlocCd);
						jrSnd.setField("SPOS_YD_PNT_CD"		, ydPndCd);
						jrSnd.setField("YD_CARPNT_CD"		, ydCarPntCd);
						jrSnd.setField("MODIFIER"			, modifier);
						jrSnd.setField("WRK_GP"				, "U");  //상차작업

						jrRtn = commUtils.addSndData(jrRtn, this.procYDOutCarLevWr(jrSnd));	
						
						
						/**********************************************************
						* Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarPntFrmYnTo 
						SELECT YD_STK_COL_GP      
						     , NVL(YD_FRM_YN,'N') AS YD_FRM_YN
						  FROM USRYDA.TB_YD_CARPOINT
						 WHERE YD_CAR_USETYPE_GP = 'MT'
						   AND YD_GP     = :V_YD_GP
						   AND YD_BAY_GP = :V_YD_BAY_GP
						   AND CASE WHEN SUBSTR(YD_STK_COL_GP,6,1) IN ('1','2') THEN '1' 
						            WHEN SUBSTR(YD_STK_COL_GP,6,1) IN ('3','4') THEN '3'
						            END 
						     = CASE WHEN :V_PT_LOC IN ('1')     THEN '3'
						            WHEN :V_PT_LOC IN ('2','3') THEN '1'
						            WHEN :V_PT_LOC IN ('4')     THEN '3' END
						   AND ROWNUM = 1   
						*/

						jrParam.setField("YD_GP" 		, "3"); 	
						jrParam.setField("YD_BAY_GP" 	, sToBayGp); 
						jrParam.setField("PT_LOC" 		, sPT_LOC); 
						
						JDTORecordSet jsPntFrm = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarPntFrmYnTo", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
						if (jsPntFrm.size() > 0) {
							String ydFrmYn = commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_FRM_YN"));
							// 형상여부
							if ("N".equals(ydFrmYn)) {
								
								// 형상이 없는 경우 도착 미리 기동처리 함	
								jrParam.setField("JMS_TC_CD"		, "A7YML018" );
								jrParam.setField("PT_LOAD_LOC"		, commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_STK_COL_GP")));
								jrParam.setField("CAR_NO"			, CardNo );
								jrParam.setField("CAR_UPDN_GP"		, "2");  //하차
								jrParam.setField("MODIFIER"			, modifier );
								
								EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
								JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("procA7YML018", new Class[] { JDTORecord.class }, new Object[] { jrParam });

								jrRtn = commUtils.addSndData(jrRtn, jrRtn1);				
							}
						}
		    		}
				}	
        	} else {
        		// 하차 작업
       			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
    			jrParam.setResultCode(logId);	//Log ID
    			jrParam.setResultMsg(methodNm);	//Log Method Name
    			jrParam.setField("YD_SCH_CD"		, ydSchCd);
    			jrParam.setField("STOCK_ID"		    , StockId);
    			jrParam.setField("YD_DN_WR_LOC"		, ydDnWrLoc);
    			jrParam.setField("YD_UP_WR_LOC"		, ydUpWrLoc);
    			jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchId     ); 
    			jrParam.setField("STACK_COL_GP"  	, ydUpWrLoc.substring(0, 6) ); 
    			jrParam.setField("YD_EQP_ID"     	, ydEqpId); 
    			
    			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAxYML009CarSchUd 
    			SELECT B.YD_CAR_SCH_ID
    			     , A.YD_STK_COL_GP
    			     , (SELECT CASE WHEN COUNT(*) = 0 THEN 'Y' ELSE 'N' END 
    			          FROM TB_YM_STACKLAYER
    			         WHERE STACK_COL_GP = A.YD_STK_COL_GP
    			           AND STOCK_ID IS NOT NULL) AS CAR_UD_CMPL_YN
    			     , B.CAR_NO   
    			     , B.CARD_NO   
    			     , A.YD_CARPNT_CD   
    			     , B.TRANS_ORD_DATE   
    			     , B.TRANS_ORD_SEQNO  
    			     , A.WLOC_CD     AS WLOC_CD   
    			     , A.YD_PNT_CD   AS YD_PNT_CD   
    			  FROM TB_YD_CARPOINT       A    
    			     , USRYDA.TB_YD_CARSCH  B
    			 WHERE A.YD_GP = '3'  
    			   AND A.CARD_NO = B.CARD_NO 
    			   AND B.DEL_YN = 'N'
    			   AND A.YD_STK_COL_GP = :V_STACK_COL_GP
    			*/
    			JDTORecordSet jsCarUpSch = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getAxYML009CarSchUd", logId, methodNm, "상차 차량스케줄 조회 "); 
    	    	
    			if (jsCarUpSch.size() > 0) {
    				JDTORecord jrCarUpSch = jsCarUpSch.getRecord(0);

    				String carUdCmplYn 	= commUtils.trim(jrCarUpSch.getFieldString("CAR_UD_CMPL_YN")); 		//차량하차완료여부
    				String ydCarSchId  	= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_SCH_ID"));
    				String CarNo 		= commUtils.trim(jrCarUpSch.getFieldString("CAR_NO")); 
    				String CardNo 		= commUtils.trim(jrCarUpSch.getFieldString("CARD_NO")); 
    				String ydCarPntCd	= commUtils.trim(jrCarUpSch.getFieldString("YD_CARPNT_CD")); 
    				String TransOrdDate	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_DATE"));
    				String TransOrdSeqNo= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_SEQNO")); 
    				String WlocCd 		= commUtils.trim(jrCarUpSch.getFieldString("WLOC_CD")); 
    				String ydPndCd 		= commUtils.trim(jrCarUpSch.getFieldString("YD_PNT_CD")); 					
    				
    				
    				String ydSchPriorNew = "";
    				String ydEqpIdNew    = "";
    				commUtils.printLog(logId, methodNm+  "★★★★★  권하차량스케쥴:" + ydCarSchId + " 차량하차완료:" + carUdCmplYn+ " ★★★★", "SL");
    				
    				//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
    				jrParam = JDTORecordFactory.getInstance().create();
    				jrParam.setResultCode(logId);	//Log ID
    				jrParam.setResultMsg(methodNm);	//Log Method Name
    				
    				if ("Y".equals(carUdCmplYn)) {              //해당동만 완료이면 차량상태 완료처리함
    					jrParam.setField("YD_CAR_PROG_STAT", "E"); //야드차량진행상태(하차완료)
					} else {
						jrParam.setField("YD_CAR_PROG_STAT", "D"); //야드차량진행상태(하차개시)
					}
    				jrParam.setField("STACK_COL_GP" 	, ydUpWrLoc.substring(0, 6) ); 
    				jrParam.setField("WR_DT" 			, commUtils.getDateTime14() ); 
    				jrParam.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
    				jrParam.setField("MODIFIER"         , modifier     );
    				
    				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML009CarSchUd
    				--크레인권하실적 하차 차량스케줄 수정
    				UPDATE USRYDA.TB_YD_CARSCH TS
    				   SET TS.MODIFIER             = :V_MODIFIER
    				     , TS.MOD_DDTT             = SYSDATE
    				     , TS.YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT   
    				     , TS.YD_EQP_WRK_SH        = (SELECT COUNT(*) 
    				                                    FROM TB_YD_CARFTMVMTL 
    				                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
    				     , TS.YD_EQP_WRK_WT        = (SELECT SUM(COIL_WT) 
    				                                    FROM TB_YD_CARFTMVMTL A
    				                                       , USRPTA.TB_PT_COILCOMM   B
    				                                   WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
    				                                     AND A.STL_NO        = B.COIL_NO
    				                                   
    				                                   )
    				     , TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
    				     , TS.YD_CARUD_STOP_LOC    = :V_STACK_COL_GP
    				     , TS.YD_CARUD_ST_DT       = NVL(TS.YD_CARUD_ST_DT,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE))
    				     , TS.YD_CARUD_CMPL_DT     = DECODE(:V_YD_CAR_PROG_STAT,'E',NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE),NULL)
    				   
    				WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
    				*/    
    				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updAxYML009CarSchUd", logId, methodNm, " 하차 차량스케줄 수정 ");
					commUtils.printLog(logId, methodNm+  "하차지 완료 여부: " + carUdCmplYn, "SL");

    				if ("Y".equals(carUdCmplYn)) {
        		
    					// 마지막 작업 마치고 차량 정보 초기화 호출
						JDTORecord jrSnd = JDTORecordFactory.getInstance().create();
						jrSnd.setResultCode(logId);	//Log ID
						jrSnd.setResultMsg(methodNm);	//Log Method Name
						jrSnd.setField("TRANS_ORD_DATE"		, TransOrdDate);
						jrSnd.setField("TRANS_ORD_SEQNO"	, TransOrdSeqNo);
						jrSnd.setField("CAR_NO"				, CarNo);
						jrSnd.setField("CARD_NO"			, CardNo);
						jrSnd.setField("SPOS_WLOC_CD"		, WlocCd);
						jrSnd.setField("SPOS_YD_PNT_CD"		, ydPndCd);
						jrSnd.setField("YD_CARPNT_CD"		, ydCarPntCd);
						jrSnd.setField("MODIFIER"			, modifier);
						jrSnd.setField("WRK_GP"				, "D");  //하차작업

						jrRtn = commUtils.addSndData(jrRtn, this.procYDOutCarLevWr(jrSnd));	
						
						
						/**********************************************************
						* 상차지 작업 예약 호출 Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarPntFrmYnToUp 
						SELECT AA.* 
						     , (SELECT NVL(YD_FRM_YN,'N')
						          FROM USRYDA.TB_YD_CARPOINT 
						         WHERE YD_STK_COL_GP = AA.YD_STK_COL_GP)  AS YD_FRM_YN 
						  FROM (
						        SELECT CTS_RELAY_SADDLE AS PT_LOC
						             , (SELECT YD_STK_COL_GP
						                  FROM USRYDA.TB_YD_CARPOINT
						                 WHERE YD_CAR_USETYPE_GP = 'MT'
						                   AND YD_GP     = SUBSTR(A.YD_SCH_CD,1,1)
						                   AND YD_BAY_GP = SUBSTR(A.YD_SCH_CD,2,1)
						                   AND CASE WHEN SUBSTR(YD_STK_COL_GP,6,1) IN ('1','2') THEN '1' 
						                            WHEN SUBSTR(YD_STK_COL_GP,6,1) IN ('3','4') THEN '3'
						                            END 
						                     = CASE WHEN C.CTS_RELAY_SADDLE IN ('1','2')     THEN '1'
						                            WHEN C.CTS_RELAY_SADDLE IN ('3','4')     THEN '3' END
						               ) AS YD_STK_COL_GP       
						          FROM TB_YM_WRKBOOK A
						             , TB_YM_WRKBOOKMTL B
						             , TB_YM_STOCK C
						         WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
						           AND B.STOCK_ID = C.STOCK_ID
						           AND A.DEL_YN = 'N'
						           AND B.DEL_YN = 'N'
						           AND A.CARD_NO = :V_CARD_NO
						           AND SUBSTR(A.YD_SCH_CD, 7,1) = 'U'  --상차
						        ORDER BY A.YD_WBOOK_ID  
						       ) AA
						*/

						jrParam.setField("CARD_NO" 	    , CardNo); 
						
						JDTORecordSet jsPntFrm = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarPntFrmYnToUp", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
						if (jsPntFrm.size() > 0) {
							String ydFrmYn = commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_FRM_YN"));
							// 형상여부
							if ("N".equals(ydFrmYn)) {
								
								// 형상이 없는 경우 도착 미리 기동처리 함	
								jrParam.setField("JMS_TC_CD"		, "A7YML018" );
								jrParam.setField("PT_LOAD_LOC"		, commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_STK_COL_GP")));
								jrParam.setField("CAR_NO"			, CardNo );
								jrParam.setField("CAR_UPDN_GP"		, "1");  //상차
								jrParam.setField("MODIFIER"			, modifier );
								
								EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
								JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("procA7YML018", new Class[] { JDTORecord.class }, new Object[] { jrParam });

								jrRtn = commUtils.addSndData(jrRtn, jrRtn1);				
							}
						}
		    		}
				}	
        	}
        	
        	return jrRtn;
        	
        } catch (Exception e) {
			throw new DAOException(e);
        }//end of try~catch
        
    }//end of procTraillerMoveSch()	
    		
	/**
	 *      [A] 오퍼레이션명 : 동간이적차량 출발 처리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procYDOutCarLevWr(JDTORecord rcvMsg)throws JDTOException  {
		String methodNm = "동간이적차량 출발 처리 [YmCommCarMvSeEJB.procYDOutCarLevWr] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		String szLogMsg = "";
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();        
		JDTORecord jrParam = JDTORecordFactory.getInstance().create(); 
	    String szMsg	= "";		
	    String ydCarDiffYn = "N"; //위치 동일차량여부		
		
	    try{
		
				
	    	commUtils.printLog(logId, methodNm, "S+");	
			
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String transOrdDate = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"));  //운송지시일자
	    	String transOrdSeqNo= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
	    	String ydCarNo  	= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));          //차량번호
	    	String ydCardNo     = commUtils.trim(rcvMsg.getFieldString("CARD_NO"));         //카드번호
	    	String sposWlocCd   = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));    //발지개소코드
	    	String sposYdPntCd  = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));  //발지포인트코드
	    	String modifier  	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
	    	String sWrkGp   	= commUtils.trim(rcvMsg.getFieldString("WRK_GP"));      //'U' 상차작업 ,'D' 하차작업
	    	if ("".equals(modifier)) { modifier = msgId; }
	    	
	    	if (transOrdDate.equals("")) {
	    		szLogMsg="운송지시일자가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("TRANS_ORD_DATE Error");
	    	}
	    	
	    	if (transOrdSeqNo.equals("")) {
	    		szLogMsg="운송지시순번이 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("TRANS_ORD_SEQNO Error");
	    	}
	    	
	    	if (ydCarNo.equals("")) {
	    		szLogMsg="차량번호가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("CAR_NO Error");
	    	}
	    	
	    	if (sposWlocCd.equals("")) {
	    		szLogMsg="발지개소코드가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
	    	}
	    	
	    	if (sposYdPntCd.equals("")) {
	    		szLogMsg="발지포인트코드가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("SPOS_YD_PNT_CD Error");
	    	}
	    	
			
	    	//발지위치정보로 출발위치 Clear
	    	//열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	//업데이트값이 없다면 그냥 종료
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
    		jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("WLOC_CD",   sposWlocCd);
			jrParam.setField("YD_PNT_CD", sposYdPntCd);
	    	
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkcolWLocCdandPntCd
			SELECT A.STACK_COL_GP   AS STACK_COL_GP
			     , A.CAR_NO         AS CAR_NO                               
			     , A.CARD_NO        AS CARD_NO                             
			     , A.WLOC_CD        AS WLOC_CD                             
			     , A.YD_PNT_CD      AS YD_PNT_CD   
			     , B.YD_CARPNT_CD   AS YD_CARPNT_CD
			     , B.YD_STK_COL_ACT_STAT AS YD_STK_COL_ACT_STAT
			  FROM TB_YM_STACKCOL A   
			     , TB_YD_CARPOINT B
			 WHERE B.YD_STK_COL_GP = A.STACK_COL_GP
			   AND A.WLOC_CD LIKE   SUBSTR(:V_WLOC_CD,1,3)||'%'
			   AND A.YD_PNT_CD = :V_YD_PNT_CD
	    	 */	    	
	    	JDTORecordSet jsStkCol = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 

	    	if (jsStkCol == null || jsStkCol.size() <= 0) {
				szLogMsg = methodNm + "발지개소["+sposWlocCd+"] 및 포인트 코드["+sposYdPntCd+"]가 타공정코드가 아니고 대기장입니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jrRtn ;
				
	    	} else {
	    		
		    	//열구분을 조회(도착지)
		    	String ydCarldLevLoc    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("STACK_COL_GP")); 
		    	String ydCarPntCdChk 	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CARPNT_CD")); 
		    	String ydCarNoChk 	    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("CAR_NO"));
		    	String ydStackColActStat= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
		    	
		    	if (!ydCarNoChk.equals(ydCarNo)) {
		 		   /**********************************************************
					* 다른 차량이 존재 하는 경우 
					**********************************************************/
		    		szMsg="["+methodNm+"] 해당 위치에 다른 차량이 존재 합니다. 포인트 차량:"+ydCarNoChk + "  취소대상 차량:"+ ydCarNo;
		    		commUtils.printLog(logId, szMsg, "SL");	
		    		ydCarDiffYn = "Y";
		    	} else {
		    		/**********************************************************
					* 동일차량존재 
					**********************************************************/
		    		
		    		//---------------------------------------------------------------------------------------
    				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
    				sndL2Msg.setResultCode(logId);	//Log ID
    				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
    				sndL2Msg.setField("YD_INFO_SYNC_CD"		, "4"                         ); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP"				, "I"                         ); //전문구분
    				sndL2Msg.setField("STACK_COL_GP"    	, ydCarldLevLoc);
    				sndL2Msg.setField("STACK_BED_GP"    	, "01");
					sndL2Msg.setField("YD_CAR_USE_GP"    	, "G"); //L:구내운송, G:출하차량
					sndL2Msg.setField("CAR_NO"  			, ydCarNo); //차량번호
					sndL2Msg.setField("CARD_NO"  			, ydCardNo); //카드번호
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT" , "S"); //A:도착, S:출발
					sndL2Msg.setField("YD_CAR_AIM_YD_GP"	, "3"); 
					
					if("U".equals(sWrkGp)) {
						//상차완료 후 출발
						sndL2Msg.setField("YD_EQP_WRK_STAT"  	, "L"); //U:공차, L:영차
					} else {
						//하차완료 후 출발
						sndL2Msg.setField("YD_EQP_WRK_STAT"  	, "U"); //U:공차, L:영차
					}
    	 
    				//전송 Data 생성
    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L001_CarInfo", sndL2Msg));		
    				szMsg="[" + methodNm + "] 저장위치 : 코일야드L2 로 송신 열구분[" + ydCarldLevLoc + "] - 저장위치 제원 : 코일야드L2 로 송신 호출 "+jrRtn.size();
    				commUtils.printLog(logId, szMsg, "SL");
		    		//---------------------------------------------------------------------------------------
		    		
		    		
		    		//사용불가로 되어있으면 상태값을 바꾸지 않는다
		    		if (!"N".equals(ydStackColActStat)) {
		    			ydStackColActStat = "C";
		    		}		    		
		    		/********************************
					 * 적치열 상태 비활성화등록
					 *******************************/
		    		szLogMsg = methodNm + "출발야드의 적치열["+ydCarldLevLoc+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("STACK_COL_GP"			, ydCarldLevLoc);
			    	jrParam.setField("STACK_COL_ACTIVE_STAT", ydStackColActStat);
			    	jrParam.setField("YD_CAR_USE_GP"		, "");
			    	jrParam.setField("TRN_EQP_CD"			, "");
			    	jrParam.setField("CAR_NO"				, "");
			    	jrParam.setField("CARD_NO"				, "");
			    	jrParam.setField("MODIFIER"				, modifier);
			    	/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol
		    		UPDATE TB_YM_STACKCOL
		    		   SET MOD_DDTT      = SYSDATE             
		    			 , MODIFIER      = :V_MODIFIER   
		    			 , YD_CAR_USE_GP = :V_YD_CAR_USE_GP      
		    			 , TRN_EQP_CD    = :V_TRN_EQP_CD           
		    			 , CAR_NO        = :V_CAR_NO               
		    			 , CARD_NO       = :V_CARD_NO  
		    		     , STACK_COL_ACTIVE_STAT = :V_STACK_COL_ACTIVE_STAT
		    		 WHERE STACK_COL_GP  = :V_STACK_COL_GP
			    	 */
			    	int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol", logId, methodNm, "TB_YM_STACKCOL 등록");
					if (intRtnVal <= 0) {

						szLogMsg = methodNm + "적치열[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);
					}

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YmCarPointinforeg("B","","",ydCarldLevLoc,"","",ydStackColActStat,logId,methodNm);
					
					/********************************
					 * 적치베드 상태 비활성화등록
					 *******************************/
					jrParam.setField("STACK_BED_WT_MAX"		, YmConstant.YD_STK_BED_WT_MAX_DEFAULT);
		    		jrParam.setField("STACK_BED_ACTIVE_STAT", "L");
					
		    		/*UPDATE USRYMA.TB_YM_STACKER
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , STACK_BED_ACTIVE_STAT  = NVL(:V_STACK_BED_ACTIVE_STAT,STACK_BED_ACTIVE_STAT)
					     , STACK_BED_WT_MAX  = NVL(:V_STACK_BED_WT_MAX,STACK_BED_WT_MAX )
					  WHERE STACK_COL_GP  = :V_STACK_COL_GP	*/	
		    		
    				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YM_STACKER 활성상태수정(C)");
    				if (intRtnVal <= 0) {
						szLogMsg = methodNm + "적치BED[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);
					}
					

					/********************************
					 * 적치단 상태 비활성화등록
					 *******************************/
    				jrParam.setField("STACK_LAYER_ACTIVE_STAT", "E");
		    		jrParam.setField("STACK_LAYER_STAT", "E");
		    		
		    		/*UPDATE USRYMA.TB_YM_STACKLAYER           
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , STACK_LAYER_ACTIVE_STAT  = :V_STACK_LAYER_ACTIVE_STAT 
					     , STOCK_ID  = null
					     , STACK_LAYER_STAT  = :V_STACK_LAYER_STAT
					 WHERE STACK_COL_GP = :V_STACK_COL_GP*/	
							    		
    				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YM_STACKLAYER 차량 적치단 정보 비활성화(C)");
    				if (intRtnVal <= 0) {

						szLogMsg = methodNm + " 적치단[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);

					}
		    	}
		    	
		    	if ("D".equals(sWrkGp)) {
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					// 하차 작업인 경우 차량스케줄 삭제
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			    	jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
			    	jrParam.setField("TRANS_ORD_DATE"		, transOrdDate);
			    	jrParam.setField("TRANS_ORD_SEQNO"	, transOrdSeqNo);
					jrParam.setField("CAR_NO" 			, ydCarNo);
			    	jrParam.setField("CARD_NO"			, ydCardNo);
					jrParam.setField("MODIFIER" 		, modifier);
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschTransDTSeq2
					SELECT *
					  FROM (
					       SELECT YD_CAR_SCH_ID
					            , SPOS_WLOC_CD
					            , YD_PNT_CD1
					         FROM TB_YD_CARSCH
					        WHERE CAR_NO        LIKE :V_CAR_NO||'%'
					          AND TRANS_ORD_DATE = :V_TRANS_ORD_DATE
					          AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					        --AND DEL_YN='N'
					        ORDER BY YD_CAR_SCH_ID DESC
					        ) A
					WHERE ROWNUM<=1
			    	*/
			    	JDTORecordSet jsCarResult = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschTransDTSeq2", logId, methodNm, "차량스케줄  조회"); 
			    	
			    	
			    	if (jsCarResult.size() <= 0 ) {
						szLogMsg = "차량스케쥴 조회 오류 + ("+transOrdDate+", "+ ydCarNo + ", " + ydCardNo + ", 'G')";
						commUtils.printLog(logId, szLogMsg, "SL");
						return jrRtn ;
			    		
			    	} else {
			    
			    		jsCarResult.first();
						JDTORecord recGetVal = jsCarResult.getRecord();
						String szCarSchId= commUtils.trim(recGetVal.getFieldString("YD_CAR_SCH_ID"     ));
						
						jrParam.setField("YD_CAR_SCH_ID" , szCarSchId);
						jrParam.setField("DEL_YN" , "Y");
	
						/*UPDATE TB_YD_CARSCH
						   SET  
						       MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,DEL_YN = :V_DEL_YN
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID */
								    		
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 스케줄삭제");
	
						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						// 차량 이송재료 삭제
						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl 
						UPDATE TB_YD_CARFTMVMTL
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , DEL_YN = 'Y'
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
		                */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl", logId, methodNm, "차량스케줄재료 삭제");
			    	}
		    	}	
				if ("N".equals(ydCarDiffYn)) {
					/****************************************************************************************************
					 * 차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 ***************************************************************************************************/
					JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setResultCode(logId);	//Log ID
					jrTemp.setResultMsg(methodNm);	//Log Method Name
					jrTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 기존:YDYDJ662
					jrTemp.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrTemp.setField("YD_CARPNT_CD"		, ydCarPntCdChk);		//입동포인트
					
					jrRtn = commUtils.addSndData(jrRtn, jrTemp);	
					
		    	}//end of if
	    	}
    	
		} catch (Exception e) {
			szLogMsg="동간이적출발실적 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			throw new DAOException(e);
		}

		return jrRtn;
	}// end of procYDOutCarLevWr()	
	
	
	/**
	 *      [A] 오퍼레이션명 : 입동불가전문전송
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord sndBayInRejTC(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "입동불가전문전송[YmCommCarMvSeEJB.sndBayInRejTC] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult 	= null;
		JDTORecordSet jsCarSch  = null;

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String szYD_CAR_SCH_ID = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); //야드설비ID
			String szCR_FRTOMOVE_GP = "";
			
			//파라메터로 전달 받는 YD_CAR_SCH_ID 값이 필수가 아님으로 값이 없을 수 있다 차량스케줄ID가 없다면 입동불가(N) 전문이 발송 안되고 여기서 종료 된다. 
			if(!"".equals(szYD_CAR_SCH_ID)) {
			
		    	//	차량스케줄 조회
				jrParam.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarsch", logId, methodNm, "TB_YD_CARSCH 조회");				
				
				if (jsCarSch.size() > 0) {

					//-------------------------------------------------------------------------------------------------------
			    	//	입동대기TC 전송
			    	//-------------------------------------------------------------------------------------------------------
					JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
					
					// PIDEV
//					jrMsg.setResultCode(logId);	//Log ID
//					jrMsg.setResultMsg(methodNm);	//Log Method Name
					
//					String sApplyYnPI = commDao.ApplyYnPI("", "YmCommCarMvSeEJBSBean => sndBayInRejTC", "APPPI0", "3", "*");
								
//					if ("Y".equals(sApplyYnPI)) {
						jrMsg.setField("MQ_TC_CD"				, "M10YDLMJ1061");
						jrMsg.setField("MQ_TC_CREATE_DDTT"		, commUtils.getDateTime14());						
						jrMsg.setField("TRN_REQ_DATE"			, commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE")));
						jrMsg.setField("TRN_REQ_SEQ" 			, commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
						// jrMsg.setField("CARD_NO"				, commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO")));
						jrMsg.setField("CAR_NO"					, commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO")));
						jrMsg.setField("YD_GP"					, "3");
						jrMsg.setField("DIST_GOODS_GP"			, "H");
						if (commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE")).equals("P")) {
							jrMsg.setField("SCH_YN"				, "Y");
						} else {
							jrMsg.setField("SCH_YN"				, "N");
						}
						jrMsg.setField("BAYIN_DDTT"		        , commUtils.getDateTime14());
						jrMsg.setField("WLOC_CD"				, "");
						jrMsg.setField("YD_PNT_CD"				, "");
						jrMsg.setField("YD_CARPNT_CD"			, "");	
						jrMsg.setField("LOAN_PULLOUT_ABLE_YN"   , "N");						
						
//					} else {
//						if (commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE")).equals("P")) {
//							
//							//-------------------------------------------------------------------------
//							jrParam.setField("TRANS_ORD_DATE2",  commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE")));
//							jrParam.setField("TRANS_ORD_SEQNO2", commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
//							
//							// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
//							rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockTransOrdDateAB", logId, methodNm, "운송지싱일자,순번으로 STOCK테이블에서 CR_FRTOMOVE_GP(냉연이송구분 )조회");
//							if(rsResult.size()>0) {
//								szCR_FRTOMOVE_GP = StringHelper.evl(rsResult.getRecord(0).getFieldString("CR_FRTOMOVE_GP"),"");
//							}
//							//-------------------------------------------------------------------------
//							
//							jrMsg.setField("JMS_TC_CD"			, "YDDMR070");
//							jrMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
//							jrMsg.setField("TC_CODE"			, "YDDMR070");
//							jrMsg.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
//							jrMsg.setField("CR_FRTOMOVE_GP"     , szCR_FRTOMOVE_GP);
//							
//						} else {
//							jrMsg.setField("JMS_TC_CD"			, "YDDMR028");
//							jrMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
//							jrMsg.setField("TC_CODE"			, "YDDMR028");
//							jrMsg.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
//						}
//						jrMsg.setField("TRANS_WORD_DATE"		, commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE")));
//						jrMsg.setField("TRANS_WORD_SEQNO" 		, commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
//						jrMsg.setField("CARD_NO"				, commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO")));
//						jrMsg.setField("CAR_NO"					, commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO")));
//						jrMsg.setField("BAYIN_DDTT"		        , commUtils.getDateTime14());
//						jrMsg.setField("WLOC_CD"				, "");
//						jrMsg.setField("YD_PNT_CD"				, "");
//						jrMsg.setField("YD_CARPNT_CD"			, "");	
//						jrMsg.setField("LOAN_PULLOUT_ABLE_YN"   , "N");						
//					}
					
					jrRtn = commUtils.addSndData(jrRtn, jrMsg);	
					
					//return jrRtn;
				}
			}			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sndBayInRejTC()	

	// PIDEV
	/**
	 *      [A] 오퍼레이션명 : 이송차량출발실적 처리 carStartOrder -> PI적용
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procFrtoCarLevWr_PIDEV(JDTORecord rcvMsg)throws JDTOException  {
		String methodNm = "코일이송차량출발실적[YmCommCarMvSeEJB.procFrtoCarLevWr_PIDEV] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		String szLogMsg = "";
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();        
		JDTORecord jrParam = JDTORecordFactory.getInstance().create(); 
	    String szMsg	= "";		
	    String ydCarDiffYn = "N"; //위치 동일차량여부		
		
	    try{
		
				
	    	commUtils.printLog(logId, methodNm, "S+");	
			commUtils.printParam(logId + "이송차량출발실적 처리 수신 ", rcvMsg);
			
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydCarNo  	= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));         //카드번호
	    	String StackColGp   = commUtils.trim(rcvMsg.getFieldString("STACK_COL_GP"));    //위치
	    	String modifier  	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
	    	if ("".equals(modifier)) { modifier = msgId; }
	    	
	    	
	    	if (ydCarNo.equals("")) {
	    		szLogMsg="차량번호가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("CAR_NO Error");
	    	}
	    	
	    	if (StackColGp.equals("")) {
	    		szLogMsg="출발위치가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
	    	}
	    	
    		jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name	
			jrParam.setField("STACK_COL_GP", StackColGp);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkcolStackColGp 
			SELECT A.STACK_COL_GP   AS STACK_COL_GP
			     , A.CAR_NO         AS CAR_NO                               
			     , A.CARD_NO        AS CARD_NO                             
			     , A.WLOC_CD        AS WLOC_CD                             
			     , A.YD_PNT_CD      AS YD_PNT_CD   
			     , B.YD_CARPNT_CD   AS YD_CARPNT_CD
			  FROM TB_YM_STACKCOL A   
			     , TB_YD_CARPOINT B
			 WHERE B.YD_STK_COL_GP = A.STACK_COL_GP
			   AND A.STACK_COL_GP = :V_STACK_COL_GP
	    	 */	    	
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkcolStackColGp", logId, methodNm, "적치열 조회"); 

	    	if (jsStkCol == null || jsStkCol.size() <= 0) {
				szLogMsg = methodNm + "발지위치["+StackColGp+"] 및 차량번호 ["+ydCarNo+"]가 이상 합니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jrRtn ;
				
	    	} else {
	    		
		    	//열구분을 조회(도착지)
		    	String ydCarldLevLoc    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("STACK_COL_GP")); 
		    	String ydCarPntCdChk 	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CARPNT_CD")); 
		    	String ydCarNoChk 	    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("CAR_NO"));
		    	String ydStackColActStat= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));
		    	
		    	if (!ydCarNoChk.equals(ydCarNo)) {
		 		   /**********************************************************
					* 다른 차량이 존재 하는 경우 
					**********************************************************/
		    		szMsg="["+methodNm+"] 해당 위치에 다른 차량이 존재 합니다. ";
		    		commUtils.printLog(logId, szMsg, "SL");	
		    		ydCarDiffYn = "Y";
		    	} else {
		    		/**********************************************************
					* 동일차량존재 
					**********************************************************/
//위치 변경		    		
    				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
    				sndL2Msg.setResultCode(logId);	//Log ID
    				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
    				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "3"                         ); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP"			, "I"                         ); //전문구분
    				sndL2Msg.setField("STACK_COL_GP"    , ydCarldLevLoc);
    				sndL2Msg.setField("STACK_BED_GP"    , "");
    				commUtils.printParam(logId, sndL2Msg);
    	 
    				//전송 Data 생성
    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L001", sndL2Msg));		
    				szMsg="[" + methodNm + "] 저장품제원 : 코일야드L2 로 송신 열구분[" + ydCarldLevLoc + "] - 저장품제원위치 : 코일야드L2 로 송신 호출 성공"+jrRtn.size();
    				commUtils.printLog(logId, szMsg, "SL");

    						    		
		    		//사용불가로 되어있으면 상태값을 바꾸지 않는다
		    		if (!"N".equals(ydStackColActStat)) {
		    			ydStackColActStat = "C";
		    		}		    		
		    		/********************************
					 * 적치열 상태 비활성화등록
					 *******************************/
		    		szLogMsg = methodNm + "출발야드의 적치열["+ydCarldLevLoc+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("STACK_COL_GP"			, ydCarldLevLoc);
			    	jrParam.setField("STACK_COL_ACTIVE_STAT", ydStackColActStat);
			    	jrParam.setField("YD_CAR_USE_GP"		, "");
			    	jrParam.setField("TRN_EQP_CD"			, "");
			    	jrParam.setField("CAR_NO"				, "");
			    	jrParam.setField("CARD_NO"				, "");
			    	jrParam.setField("MODIFIER"				, modifier);
			    	/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol
		    		UPDATE TB_YM_STACKCOL
		    		   SET MOD_DDTT      = SYSDATE             
		    			 , MODIFIER      = :V_MODIFIER   
		    			 , YD_CAR_USE_GP = :V_YD_CAR_USE_GP      
		    			 , TRN_EQP_CD    = :V_TRN_EQP_CD           
		    			 , CAR_NO        = :V_CAR_NO               
		    			 , CARD_NO       = :V_CARD_NO  
		    		     , STACK_COL_ACTIVE_STAT = :V_STACK_COL_ACTIVE_STAT
		    		 WHERE STACK_COL_GP  = :V_STACK_COL_GP
			    	 */
			    	int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkcol", logId, methodNm, "TB_YM_STACKCOL 등록");
					if (intRtnVal <= 0) {

						szLogMsg = methodNm + "적치열[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						
						m_ctx.setRollbackOnly();
						throw new DAOException(szLogMsg);
					}

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YmCarPointinforeg("B","","",ydCarldLevLoc,"","",ydStackColActStat,logId,methodNm);
					
					/********************************
					 * 적치베드 상태 비활성화등록
					 *******************************/
					jrParam.setField("STACK_BED_WT_MAX"		, YmConstant.YD_STK_BED_WT_MAX_DEFAULT);
		    		jrParam.setField("STACK_BED_ACTIVE_STAT", "L");
					
		    		/*UPDATE USRYMA.TB_YM_STACKER
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , STACK_BED_ACTIVE_STAT  = NVL(:V_STACK_BED_ACTIVE_STAT,STACK_BED_ACTIVE_STAT)
					     , STACK_BED_WT_MAX  = NVL(:V_STACK_BED_WT_MAX,STACK_BED_WT_MAX )
					  WHERE STACK_COL_GP  = :V_STACK_COL_GP	*/	
		    		
    				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YM_STACKER 활성상태수정(C)");
    				if (intRtnVal <= 0) {
						szLogMsg = methodNm + "적치BED[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);
					}
					

					/********************************
					 * 적치단 상태 비활성화등록
					 *******************************/
    				jrParam.setField("STACK_LAYER_ACTIVE_STAT", "E");
		    		jrParam.setField("STACK_LAYER_STAT", "E");
		    		
		    		/*UPDATE USRYMA.TB_YM_STACKLAYER           
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , STACK_LAYER_ACTIVE_STAT  = :V_STACK_LAYER_ACTIVE_STAT 
					     , STOCK_ID  = null
					     , STACK_LAYER_STAT  = :V_STACK_LAYER_STAT
					 WHERE STACK_COL_GP = :V_STACK_COL_GP*/	
							    		
    				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YM_STACKLAYER 차량 적치단 정보 비활성화(C)");
    				if (intRtnVal <= 0) {

						szLogMsg = methodNm + " 적치단[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);

					}
					
//    				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
//    				sndL2Msg.setResultCode(logId);	//Log ID
//    				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
//    				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "3"                         ); //야드정보동기화코드
//    				sndL2Msg.setField("MSG_GP"			, "I"                         ); //전문구분
//    				sndL2Msg.setField("STACK_COL_GP"    , ydCarldLevLoc);
//    				sndL2Msg.setField("STACK_BED_GP"    , "");
//    				commUtils.printParam(logId, sndL2Msg);
//    	 
//    					//전송 Data 생성
//    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L001", sndL2Msg));		
//    				szMsg="[" + methodNm + "] 저장품제원 : 코일야드L2 로 송신 열구분[" + ydCarldLevLoc + "] - 저장품제원위치 : 코일야드L2 로 송신 호출 성공"+jrRtn.size();
//    				commUtils.printLog(logId, szMsg, "SL");

		    	}
		    	
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 차량스케줄 삭제
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		    	jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
		    	jrParam.setField("CAR_NO"			, ydCarNo);
		    	jrParam.setField("YD_CARLD_STOP_LOC", ydCarldLevLoc);
				jrParam.setField("MODIFIER" 		, modifier);
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getreadcarinfoOfwloc_PIDEV
				WITH TEMP_TABLE AS (
				SELECT :V_CARD_NO  AS CARD_NO , :V_YD_CARLD_STOP_LOC AS YD_CARLD_STOP_LOC 
				  FROM DUAL
				)
				SELECT *
				 FROM (
				       SELECT A.YD_CAR_SCH_ID
				         FROM USRYDA.TB_YD_CARSCH A
				            , TEMP_TABLE B
				        WHERE A.CARD_NO=B.CARD_NO(+)
				          AND A.YD_CARLD_STOP_LOC like substr(REPLACE(B.YD_CARLD_STOP_LOC,'TR1','TR0'),1,5)||'%'
				          AND A.DEL_YN='N'
				          AND A.CARD_NO =B.CARD_NO
				        ORDER BY A.YD_CAR_SCH_ID DESC
				       ) A
				 WHERE ROWNUM<=1 
		    	*/
		    	JDTORecordSet jsCarResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getreadcarinfoOfwloc_PIDEV", logId, methodNm, "차량스케줄  조회"); 
		    	
		    	
		    	if (jsCarResult.size() <= 0 ) {
					szLogMsg = "차량스케쥴 조회 오류 + (" + ydCarldLevLoc + ", " + ydCarNo + ", 'G')";
					commUtils.printLog(logId, szLogMsg, "SL");
		    		
		    	} else {
		    
		    		jsCarResult.first();
					JDTORecord recGetVal = jsCarResult.getRecord();
					String szCarSchId= commUtils.trim(recGetVal.getFieldString("YD_CAR_SCH_ID"     ));
					
					jrParam.setField("YD_CAR_SCH_ID" , szCarSchId);
					jrParam.setField("DEL_YN" , "Y");

					/*UPDATE TB_YD_CARSCH
					   SET  
					       MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,DEL_YN = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID */
							    		
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 차량 스케줄삭제");

					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					// 차량 이송재료 삭제
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl 
					UPDATE TB_YD_CARFTMVMTL
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , DEL_YN = 'Y'
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID

	                */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl", logId, methodNm, "차량스케줄재료 삭제");
		    	}
		    	
		    	commUtils.printLog(logId, szLogMsg+ "현재위치 복수동 입동지시 여부 N 이면 입동지시"+ ydCarDiffYn + ":" + ydCarPntCdChk , "SL");

				if (ydCarDiffYn.equals("N")) {
					/****************************************************************************************************
					 * 차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 ***************************************************************************************************/
					JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setResultCode(logId);	//Log ID
					jrTemp.setResultMsg(methodNm);	//Log Method Name
					jrTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 기존:YDYDJ662
					jrTemp.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrTemp.setField("YD_CARPNT_CD"		, ydCarPntCdChk);		//입동포인트
					
					jrRtn = commUtils.addSndData(jrRtn, jrTemp);

								
		    	}//end of if
	    	}
    	
		} catch (Exception e) {
			szLogMsg="출하차량출발실적 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			throw new DAOException(e);
		}

		return jrRtn;
	}// end of procFrtoCarLevWrPI()		

/*************************************************/
	
	/**
	 * 복수상차 처리 로직  
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public JDTORecord procCmbnCarldYn_PIDEV(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "복수상차 처리 로직  [YmCommCarMvSeEJB.procCmbnCarldYn_PIDEV] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult2 = null;
		
		try{		
 
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydGp       = commUtils.trim(rcvMsg.getFieldString("YD_GP"      )); //야드구분
			String StockId    = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"   )); //재료번호

			String CmbnBayChk = ""; //복수동상차구분
			
			//차량정보 존재여부 체크 //////////////////////////////////////////////////////////////////////////////////
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ID"		, StockId);
 

			//차량정보 조회
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarwbookid 
			SELECT A.*
			  FROM TB_YD_CARSCH A
			     ,(
			       SELECT TRANS_ORD_DATE,TRANS_ORD_SEQNO
			         FROM USRYDA.TB_YD_STOCK
			         WHERE STL_NO= :V_STOCK_ID
			        UNION
			       SELECT NVL(TRANS_ORD_DATE2,SUBSTR(TRANS_WORD_NO,1,8))
			            , NVL(TRANS_ORD_SEQNO2,SUBSTR(TRANS_WORD_NO,9))
			        FROM USRYMA.TB_YM_STOCK
			        WHERE STOCK_ID =:V_STOCK_ID
			       ) B
			 WHERE A.TRANS_ORD_DATE=B.TRANS_ORD_DATE
			   AND A.TRANS_ORD_SEQNO=B.TRANS_ORD_SEQNO
			   AND DEL_YN   = 'N'
			 ORDER BY A.YD_CAR_SCH_ID DESC
			*/
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarwbookid", logId, methodNm, "설비상태조회");
			if (jsCarSch.size() <= 0) {
				commUtils.printLog(logId, methodNm+  "TB_YD_CARSCH[차량스케줄이 존재 안 합니다", "SL");
				return  jrRtn ;
			}
			
			jsCarSch.first();
			JDTORecord jrCarSch = jsCarSch.getRecord();
			String cmbnCarldYn  	= commUtils.nvl(jrCarSch.getFieldString("CMBN_CARLD_YN"),"N");	
			String ydCarWrkGp		= commUtils.nvl(jrCarSch.getFieldString("YD_CAR_WRK_GP"),"");
			String TelNo        	= commUtils.nvl(jrCarSch.getFieldString("TEL_NO"),"");	
			String ydCarNo	    	= commUtils.nvl(jrCarSch.getFieldString("CAR_NO"),"");    	
			String ydCardNo	    	= commUtils.nvl(jrCarSch.getFieldString("CARD_NO"),"");    			
			String ydStackColGp 	= commUtils.nvl(jrCarSch.getFieldString("YD_CARLD_STOP_LOC"),"");
			String TransOrdDate		= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_DATE"),"");
			String TransOrdSeqNo	= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_SEQNO"),"");
			String transEquipType	= commUtils.nvl(jrCarSch.getFieldString("TRANS_EQUIPMENT_TYPE"),"");
			String DriverName      	= commUtils.nvl(jrCarSch.getFieldString("DRIVER_NAME"),"");	
			
			commUtils.printLog(logId, "조합상차 여부 : " + cmbnCarldYn  , "SL");
			
			//조합상차(시작:S , 종료: E ,  단일상차: N )
			if (!"S".equals(cmbnCarldYn)) { 
				commUtils.printLog(logId, methodNm+  "★★★★★ 복수 상차가 아님 ★★★★★", "SL");
				return  jrRtn ;
			}  
				
			jrParam.setField("TRANS_ORD_DATE"	, TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO"	, TransOrdSeqNo);
			jrParam.setField("YD_STK_COL_GP"	, ydStackColGp);					
			jrParam.setField("DEL_YN"			, "Y");
			jrParam.setField("STOCK_MOVE_TERM" 	, "MG");
			jrParam.setField("MODIFIER"			, "복수상차");	 

							
			//저장품종료처리  
			EJBConnector ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
			ejbConn.trx("updYmStockTrnsOrdTX", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			commUtils.printLog(logId, methodNm+  "★자동차량출발", "SL");
				
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);		//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("CAR_NO", 				ydCarNo);
			recInTemp.setField("STACK_COL_GP", 			ydStackColGp);				
			
			//자동차량출발 처리
			JDTORecord jrRtn1 = this.procFrtoCarLevWr_PIDEV(recInTemp);
			jrRtn = commUtils.addSndData(jrRtn, jrRtn1);	
			
			jrParam 	= JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);		//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TRANS_ORD_DATE"	, TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO"	, TransOrdSeqNo);
			jrParam.setField("YD_GP"			, ydGp );
			jrParam.setField("STOCK_ID"			, StockId);
			
			//복수창고 구분
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarYdCmbnCarldGP 
			WITH TEMP_TABLE AS (
			   SELECT  A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO , A.YD_GP
			     FROM(
			        SELECT B.YD_GP
			            , A.STL_NO ,A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO
			         FROM USRYDA.TB_YD_STOCK A
			            , TB_PT_COILCOMM B
			        WHERE A.STL_NO=B.COIL_NO 
			          AND A.TRANS_ORD_DATE=:V_TRANS_ORD_DATE
			          AND A.TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
			        UNION ALL
			        SELECT B.YD_GP 
			            , A.STOCK_ID,A.TRANS_ORD_DATE2,A.TRANS_ORD_SEQNO2
			         FROM USRYMA.TB_YM_STOCK A
			            , TB_PT_COILCOMM B
			        WHERE A.STOCK_ID=B.COIL_NO 
			         AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DATE
			         AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
			        ) A
			       GROUP BY  A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO, A.YD_GP
			)
			SELECT A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO , COUNT(YD_GP) AS CNT
			    ,(SELECT B.YD_GP FROM TEMP_TABLE B WHERE B.YD_GP <>:V_YD_GP) AS NEXT_YD_GP
			  FROM TEMP_TABLE A
			 GROUP BY A.TRANS_ORD_DATE ,A.TRANS_ORD_SEQNO
			 HAVING COUNT(YD_GP)>1
			*/ 
			
			JDTORecordSet jsCmbn = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarYdCmbnCarldGP", logId, methodNm, "복수창고 구분");
			
			if (jsCmbn.size() <= 0) {
				if (!"9".equals(ydCarWrkGp)) {
					/********************************
					 * 일반 출하 복수동  
					 *****************************/
					
					commUtils.printLog(logId, methodNm+  "☆☆☆☆☆ 일반출하 복수동인 경우 ☆☆☆☆☆", "SL");
	
					//복수동 CHECK
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschCarNoCardNoTransNoCHK 
					WITH TEMP_TABLE AS (
					SELECT :V_YD_GP AS YD_GP 
					     , :V_TRANS_ORD_DATE AS TRANS_ORD_DATE 
					     , :V_TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
					  FROM DUAL
					)
					SELECT COUNT(*) as CHK
					--SELECT * 
					 FROM (
					        SELECT SUBSTR(YD_CARPNT_CD,2,2) 
					         FROM (
					        SELECT A.STL_NO , B.YD_STK_COL_GP, SUBSTR(B.YD_STK_COL_GP,1,1) AS YD_GP  ,SUBSTR(B.YD_STK_COL_GP,3,2) AS SPAN_GP  ,SUBSTR(B.YD_STK_COL_GP,2,1) AS BAY_GP
					         FROM USRYDA.TB_YD_STOCK A
					             , USRYDA.TB_YD_STKLYR B
					             , TEMP_TABLE C
					        WHERE A.STL_NO=B.STL_NO
					           AND A.TRANS_ORD_DATE=C.TRANS_ORD_DATE
					           AND A.TRANS_ORD_SEQNO=C.TRANS_ORD_SEQNO
					           AND C.YD_GP ='J'
					         UNION ALL
					         SELECT A.STOCK_ID,B.STACK_COL_GP, SUBSTR(B.STACK_COL_GP,1,1) AS YD_GP  ,SUBSTR(B.STACK_COL_GP,3,2) AS SPAN_GP,SUBSTR(B.STACK_COL_GP,2,1) AS BAY_GP
					            FROM USRYMA.TB_YM_STOCK A
					                , USRYMA.TB_YM_STACKLAYER B
					                 , TEMP_TABLE C
					        WHERE A.STOCK_ID=B.STOCK_ID
					           AND A.TRANS_ORD_DATE2=C.TRANS_ORD_DATE
					           AND A.TRANS_ORD_SEQNO2=C.TRANS_ORD_SEQNO
					           AND C.YD_GP IN ('1','3')                
					              ) A
					            ,USRYDA.TB_YD_CARPOINT B
					         WHERE A.YD_GP=B.YD_GP
					            AND B.DEL_YN='N'
					            AND A.SPAN_GP BETWEEN B.YD_SPAN_FROM AND B.YD_SPAN_TO
					            AND SUBSTR(B.YD_CARPNT_CD,4,1)='1'
					            AND A.BAY_GP=B.YD_BAY_GP
					            AND A.STL_NO<> :V_STOCK_ID
					        GROUP BY     SUBSTR(YD_CARPNT_CD,2,2)
					    ) A 
					*/    
					JDTORecordSet jsCmbnBay = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschCarNoCardNoTransNoCHK", logId, methodNm, "복수동 구분");
					
					if (jsCmbnBay.size() > 0) {
						jsCmbnBay.first();
						CmbnBayChk     = StringHelper.evl(jsCmbnBay.getRecord(0).getFieldString("CHK"), "");
					} 
					
					//복수동 상차대기장도착 (코일)/////////////////////////////////////////////////////////////////////////////////////
					recInTemp  = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setField("MQ_TC_CD"             , "M10LMYDJ1041");
					recInTemp.setField("MQ_TC_CREATE_DDTT"    , commUtils.getDateTime14());
					recInTemp.setField("YD_GP"		          , ydGp);					
					recInTemp.setField("WORK_GP"		      , ydCarWrkGp);
					recInTemp.setField("TEL_NO"		          , TelNo);
					recInTemp.setField("TRN_REQ_DATE"		  , TransOrdDate);
					recInTemp.setField("TRN_REQ_SEQ" 	      , TransOrdSeqNo);
					recInTemp.setField("CAR_NO"				  , ydCarNo);
					recInTemp.setField("WAIT_ARR_DDTT"		  , YmCommUtils.getCurDate("yyyyMMddHHmmss"));
					recInTemp.setField("WAIT_ARR_GP"	 	  , "B");
					recInTemp.setField("DRIVER_NAME"		  , DriverName);
					recInTemp.setField("YD_SND_YN"			  , "Y");
					recInTemp.setField("DIST_GOODS_GP"		  , "H");
					
					commUtils.printLog(logId, methodNm+  "복수동 존재 수량 체크"+CmbnBayChk, "SL");
					
					if (CmbnBayChk.equals("0")||CmbnBayChk.equals("1")) {
						recInTemp.setField("CMBN_CARLD_YN"	, "E");
					} else {
						recInTemp.setField("CMBN_CARLD_YN"	, "S");
					}
					
					EJBConnector ejbConn2 = new EJBConnector("default", "YmCoilL3RcvPISeEJB", this);
					JDTORecord jrRtn2 = (JDTORecord)ejbConn2.trx("procM10LMYDJ_DMYDR061", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					jrRtn = commUtils.addSndData(jrRtn, jrRtn2);
					
				} else {
					/********************************
					 * 이송  복수동  
					 *****************************/
					
					commUtils.printLog(logId, methodNm+  "☆☆☆☆☆ 이송 복수동인 경우 ☆☆☆☆☆", "SL");
	
					//복수동 CHECK
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschCarNoCardNoTransNoCHK9_PIDEV
					WITH TEMP_TABLE AS 
					(
					    SELECT
					        :V_YD_GP AS YD_GP,
					        :V_TRANS_ORD_DATE AS TRANS_ORD_DATE,
					        :V_TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
					    FROM
					        DUAL
					)
					SELECT 
					      COUNT(*) OVER() AS CHK,YD_CARPNT_CD
					FROM 
					    (
					        SELECT
					                MIN(YD_CARPNT_CD) AS YD_CARPNT_CD,STL_NO
					        FROM
					            (
					                SELECT
					                    A.STL_NO,
					                    B.YD_STK_COL_GP,
					                    SUBSTR(B.YD_STK_COL_GP,1,1) AS YD_GP,
					                    SUBSTR(B.YD_STK_COL_GP,3,2) AS SPAN_GP,
					                    SUBSTR(B.YD_STK_COL_GP,2,1) AS BAY_GP
					                FROM
					                    USRYDA.TB_YD_STOCK A,
					                    USRYDA.TB_YD_STKLYR B,
					                    TEMP_TABLE C
					                WHERE 1=1
					                AND A.STL_NO = B.STL_NO
					                AND A.TRANS_ORD_DATE = C.TRANS_ORD_DATE
					                AND A.TRANS_ORD_SEQNO = C.TRANS_ORD_SEQNO
					                AND C.YD_GP = 'J'
					                
					                UNION ALL
					                
					                SELECT
					                    A.STOCK_ID,
					                    B.STACK_COL_GP,
					                    SUBSTR(B.STACK_COL_GP,1,1) AS YD_GP,
					                    SUBSTR(B.STACK_COL_GP,3,2) AS SPAN_GP,
					                    SUBSTR(B.STACK_COL_GP,2,1) AS BAY_GP
					                FROM
					                    USRYMA.TB_YM_STOCK A,
					                    USRYMA.TB_YM_STACKLAYER B,
					                    TEMP_TABLE C
					                WHERE 1=1
					                AND A.STOCK_ID = B.STOCK_ID
					                AND A.TRANS_ORD_DATE2 = C.TRANS_ORD_DATE
					                AND A.TRANS_ORD_SEQNO2 = C.TRANS_ORD_SEQNO
					                AND C.YD_GP = '3'
					                
					                UNION ALL
					                
					                SELECT
					                    A.STL_NO,
					                    B.YD_STK_COL_GP,
					                    SUBSTR(B.YD_STK_COL_GP,1,1) AS YD_GP,
					                    SUBSTR(B.YD_STK_COL_GP,3,2) AS SPAN_GP,
					                    SUBSTR(B.YD_STK_COL_GP,2,1) AS BAY_GP
					                FROM
					                    USRYFA.TB_YF_STOCK A,
					                    USRYFA.TB_YF_STKLYR B,
					                    TEMP_TABLE C
					                WHERE 1=1
					                AND A.STL_NO = B.STL_NO
					                AND A.TRANS_ORD_DATE = C.TRANS_ORD_DATE
					                AND A.TRANS_ORD_SEQNO = C.TRANS_ORD_SEQNO
					                AND C.YD_GP = '1'
					            ) A,
					            USRYDA.TB_YD_CARPOINT B
					        WHERE 1=1
					        AND A.YD_GP = B.YD_GP
					        AND B.DEL_YN = 'N'
					        AND A.SPAN_GP BETWEEN B.YD_SPAN_FROM AND B.YD_SPAN_TO
					        --AND SUBSTR(B.YD_CARPNT_CD, 4, 1)='1'
					        AND A.BAY_GP = B.YD_BAY_GP
					        AND A.STL_NO <> :V_STOCK_ID
					        GROUP BY STL_NO
					    ) A
					WHERE 1 = 1    
					*/    
					JDTORecordSet jsCmbnBay = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarschCarNoCardNoTransNoCHK9_PIDEV", logId, methodNm, "이송복수동 구분");
					
					String sChk = "";
					if (jsCmbnBay.size() > 0) {
						CmbnBayChk = jsCmbnBay.getRecord(0).getFieldString("CHK");
						
						commUtils.printLog(logId, "이송 출하 복수동  Chk : " + CmbnBayChk , "SL");
						
						//복수동 상차대기장도착 (코일)/////////////////////////////////////////////////////////////////////////////////////
						recInTemp  = JDTORecordFactory.getInstance().create();
						recInTemp.setResultCode(logId);		//Log ID
						recInTemp.setField("MQ_TC_CD"             , "M10LMYDJ1041");
						recInTemp.setField("MQ_TC_CREATE_DDTT"    , commUtils.getDateTime14());
						recInTemp.setField("YD_GP"		          , ydGp);					
						recInTemp.setField("WORK_GP"		      , ydCarWrkGp);
						recInTemp.setField("TEL_NO"		          , TelNo);
						recInTemp.setField("TRN_REQ_DATE"		  , TransOrdDate);
						recInTemp.setField("TRN_REQ_SEQ" 	      , TransOrdSeqNo);
						recInTemp.setField("CAR_NO"				  , ydCarNo);
						for (int i = 1; i <= jsCmbnBay.size(); ++i) {
							jsCmbnBay.absolute(i);
							recInTemp.setField("STL_NO"+i        , commUtils.trim(jsCmbnBay.getRecord().getFieldString("STL_NO"    )));
							recInTemp.setField("GDS_CARLD_LOC"+i , commUtils.trim(jsCmbnBay.getRecord().getFieldString("GDS_CARLD_LOC")));
						}
						
						recInTemp.setField("WAIT_ARR_DDTT"		  , YmCommUtils.getCurDate("yyyyMMddHHmmss"));
						recInTemp.setField("WAIT_ARR_GP"	 	  , "B");
						recInTemp.setField("DRIVER_NAME"		  , DriverName);
						recInTemp.setField("YD_EQP_WRK_SH"		  , sChk);
						recInTemp.setField("CARLD_PNT_CD"		  , jsCmbnBay.getRecord(0).getFieldString("YD_CARPNT_CD") );
						recInTemp.setField("YD_SND_YN"			  , "Y");
						recInTemp.setField("DIST_GOODS_GP"		  , "H");
						
						commUtils.printLog(logId, methodNm+  "복수동 존재 수량 체크"+CmbnBayChk, "SL");
						
						if (CmbnBayChk.equals("0")||CmbnBayChk.equals("1")) {
							recInTemp.setField("CMBN_CARLD_YN"	, "E");
						} else {
							recInTemp.setField("CMBN_CARLD_YN"	, "S");
						}
						
						EJBConnector ejbConn2 = new EJBConnector("default", "YmCoilL3RcvPISeEJB", this);
						JDTORecord jrRtn2 = (JDTORecord)ejbConn2.trx("procM10LMYDJ_DMYDR070", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
						
						jrRtn = commUtils.addSndData(jrRtn, jrRtn2);
					}
				}
			} else {
				
				/********************************
				 * 복수 창고 
				 *****************************/
				commUtils.printLog(logId, methodNm+  "☆☆☆☆☆ 복수 창고 인 경우 ☆☆☆☆☆", "SL");

				jsCmbn.first();
				JDTORecord jRCmbn 	= jsCmbn.getRecord();
				String ydGpNext		= commUtils.nvl(jRCmbn.getFieldString("NEXT_YD_GP"),""); 	

				jrParam.setField("YD_GP"			, ydGpNext );
				
				//다음 창고 도착 포인트
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarSch
				SELECT C.*
				 FROM USRYDA.TB_YD_STOCK A
				    , USRYDA.TB_YD_STKLYR B
				    , USRYDA.TB_YD_CARPOINT C
				WHERE A.STL_NO=B.STL_NO
				  AND A.TRANS_ORD_DATE=:V_TRANS_ORD_DATE
				  AND A.TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
				  AND SUBSTR(B.YD_STK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				  AND SUBSTR(B.YD_STK_COL_GP,1,1)=C.YD_GP
				  AND 'J'=:V_YD_GP
				UNION ALL
				SELECT C.*
				 FROM USRYMA.TB_YM_STOCK A
				    , USRYMA.TB_YM_STACKLAYER B
				    , USRYDA.TB_YD_CARPOINT C
				WHERE A.STOCK_ID=B.STOCK_ID 
				 AND A.TRANS_ORD_DATE2=:V_TRANS_ORD_DATE
				 AND A.TRANS_ORD_SEQNO2=:V_TRANS_ORD_SEQNO
				 AND SUBSTR(B.STACK_COL_GP,3,2) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				 AND SUBSTR(B.STACK_COL_GP,1,1)=C.YD_GP
				 AND :V_YD_GP IN('1','3')
				*/
				JDTORecordSet jsNextYdGp = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarSch", logId, methodNm, "타 창고");
				
				if (jsNextYdGp.size() <= 0) {
					commUtils.printLog(logId, methodNm+  " 다음 창고 도착가능 포인트가 존재 안 합니다.", "SL");
					return  jrRtn ;
				}
				
				jsNextYdGp.first();
				JDTORecord jrNextYdGp = jsNextYdGp.getRecord();
				String WlocCd 	= commUtils.nvl(jrNextYdGp.getFieldString("WLOC_CD"),""); 	
				String ydPntCd	= commUtils.nvl(jrNextYdGp.getFieldString("YD_PNT_CD"),"");
				
				//다음 창고 출하 입동지시TC 전송//////////////////////////////////////////////////////////////////////////////
				recInTemp = JDTORecordFactory.getInstance().create();
							
				recInTemp.setField("MQ_TC_CD"				, "M10YDLMJ1061");
				recInTemp.setField("MQ_TC_CREATE_DDTT"		, commUtils.getDateTime14());						

				
				recInTemp.setField("TRN_REQ_DATE"			, TransOrdDate);
				recInTemp.setField("TRN_REQ_SEQ" 			, TransOrdSeqNo);
				recInTemp.setField("CAR_NO"					, ydCarNo);
				
				recInTemp.setField("YD_GP"					, "3");
				recInTemp.setField("DIST_GOODS_GP"			, "H");
				if ("P".equals(transEquipType)) {
					recInTemp.setField("SCH_YN"    			, "Y");
				} else {
					recInTemp.setField("SCH_YN"     		, "N");
				}

				recInTemp.setField("WLOC_CD"				, WlocCd);
				recInTemp.setField("YD_PNT_CD"				, ydPntCd);
				recInTemp.setField("BAYIN_DDTT"		        , commUtils.getDateTime14());
				//복수창고인 경우 다음 창고로 대기 하기 위해서 다음과 같이 전송 한다.
				recInTemp.setField("YD_CARPNT_CD"			, "");	
				recInTemp.setField("LOAN_PULLOUT_ABLE_YN"	, "Y");		
				
				jrRtn = commUtils.addSndData(jrRtn, recInTemp);	
			}

		    
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}			

		return jrRtn;

	} // end of procCmbnCarldYn()   
	
} 
