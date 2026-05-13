/**
 * @(#)SlabYdL2RcvSeEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 *
 * @description      Slab야드 L2수신 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 * V1.02  2015/12/14   이준영      이준영      항만랴드 설비추가
 */
package com.inisteel.cim.yd.slabyd.session;

import java.util.HashMap;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydWrkHistDao.YdWrkHistDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO;
import com.inisteel.cim.yd.jsp.slabjsp.session.SlabJspSeEJBBean;
//import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;  //LeeJY

/**
 *      [A] 클래스명 : Slab야드 L2수신 처리
 *
 * @ejb.bean name="SlabYdL2RcvSeEJB" jndi-name="SlabYdL2RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class SlabYdL2RcvSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils  slabUtils = new YdSlabUtils();
	private SlabYdComm    slabComm = new SlabYdComm();
	private SlabYdCommDAO  commDao = new SlabYdCommDAO();
	private SlabYdL2RcvDAO rcv2Dao = new SlabYdL2RcvDAO();
	private YdCrnSchDao ydcrnschDao= new YdCrnSchDao();
	private Logger logger = new Logger("yd");   //LeeJY
	
	
	private PSlabYdCommDAO  PcommDao = new PSlabYdCommDAO();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/***************************************************************************
	 * 연주정정L2(C3), 연주2정정L2(C7)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : C3수불구용도변경요구(C3YDL001, C7YDL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC3YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "수불구용도변경요구[SlabYdL2RcvSeEJB.rcvC3YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			}

			/**********************************************************
			* 2. Bed용도구분설정
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);
			jrParam.setField("V_YD_STK_COL_GP", ydEqpId); //야드적치열구분

			//등록대상 Set
			int trtCnt = 0;	//처리건수
			String ydStkBedUsgGp = "";	//야드적치Bed용도구분

			for (int ii = 1; ii < 8; ii++) {
				ydStkBedUsgGp = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_USG_GP" + ii));
				if (!"".equals(ydStkBedUsgGp)) {
					jrParam.setField("V_YD_STK_BED_NO"    , "0" + ii     ); //야드적치Bed번호
					jrParam.setField("V_YD_STK_BED_USG_GP", ydStkBedUsgGp); //야드적치Bed용도구분

					//Bed용도구분설정
					trtCnt += slabComm.setBedUsgGp(jrParam);
				}
			}

			if (trtCnt <= 0) {
				slabUtils.printLog(logId, "변경된 야드적치Bed용도구분(YD_STK_BED_USG_GP)이 없습니다.", "SL");
			}

			/**********************************************************
			* 3. 수불구변경응답(YDC3L001, YDC7L001) 전문 조회
			**********************************************************/
			logger.println(LogLevel.INFO, "-----> 수불구변경응답 전문 조회 : " + ydEqpId);
			// 항만야드 기능적용 보완 : 2015.12.22 by LeeJY  -- 전송Data 조회
			if ("ACPUP7".equals(ydEqpId) || "ADPUP8".equals(ydEqpId)) {
				msgId = "YDC7L001";
			} else if ("MADP01".equals(ydEqpId) || "MBPU01".equals(ydEqpId)) {
				msgId = "YDE9L001";
			} else {
				msgId = "YDC3L001";
			}

			jrParam.setField("V_JMS_TC_CD"    , msgId  ); //JMSTC코드
			jrParam.setField("V_YD_STK_COL_GP", ydEqpId); //야드적치열구분

			//전송Data 조회
			JDTORecord jrRtn = slabUtils.addSndData(commDao.getMsgL2(msgId, jrParam));

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : C7수불구용도변경요구(C7YDL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC7YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C7수불구용도변경요구[SlabYdL2RcvSeEJB.rcvC7YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수불구재료적치정보 처리
			return this.rcvC3YDL001(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 수불구재료적치정보(C3YDL002, C7YDL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvC3YDL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "수불구재료적치정보[SlabYdL2RcvSeEJB.rcvC3YDL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID
			String ydStkBedNo = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO")); //야드적치Bed번호
			String modifier   = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
			}

			//C3# 스카핑 야드 재료 8단으로 증설에 따른 작업
			String yd_gp = ydEqpId.substring(0,1);
			int cnt =5 ;
			
			if("M".equals(yd_gp)){
				cnt =8 ;
			}
			
			
			//20240412 추가. 같은 베드에 중복재료 정보 오는 부분 EXCEPTION 처리 (추후 ORA 에러 방지 )
			for(int ii=0; ii< cnt; ii++){
				String stlNo = slabUtils.trim(rcvMsg.getFieldString("STL_NO" + (ii + 1)));	//재료번호
				
				if("".equals(stlNo)) continue;
				
				for(int jj=ii+1; jj<cnt; jj++){
					String compStlNo = slabUtils.trim(rcvMsg.getFieldString("STL_NO" + (jj + 1)));	//재료번호
					if("".equals(compStlNo)) continue;
					
					if(stlNo.equals(compStlNo)){
						slabUtils.printLog(logId, "["+(ii + 1)+"] 단 재료정보 ["+stlNo+"]와 ["+(jj + 1)+"] 단 재료정보 ["+compStlNo+"] 일치하므로 에러", "SL"); 
						throw new Exception("["+(ii + 1)+"] 단 재료정보 ["+stlNo+"]와 ["+(jj + 1)+"] 단 재료정보 ["+compStlNo+"] 일치하므로 에러");
					}
				}
			}
			
			
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태 및 저장품 저장위치 Update
			**********************************************************/
			//20240314 픽업베드 파일러크레인으로 이적 시 FROM 위치 CLEAR -> TO위치 적치 정보 로 전문이 온다
			// 하지만 도중에 TO위치 바꾸면 FROM 위치 CLEAR 정보가 오지 않아, 재료 중복 적치 현상 발생
			//중복적치 현상 해결을 위해 이전 저장위치와 현 저장위치 다르면, CLEAR 처리
			String stlNo = "";
			
			for(int ii=0; ii< cnt; ii++){
				stlNo = slabUtils.trim(rcvMsg.getFieldString("STL_NO" + (ii + 1)));	//재료번호
				
				if("".equals(stlNo)) continue;
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);
				jrParam.setField("STL_NO", 	    stlNo);
				
				
				JDTORecordSet chkResult = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO", logId, methodNm, "재료 저장위치 검색");
			
				if(chkResult != null && chkResult.size() > 0) {
					String curYdStkColGp = chkResult.getRecord(0).getFieldString("YD_STK_COL_GP");
					String curYdStkBedNo = chkResult.getRecord(0).getFieldString("YD_STK_BED_NO");
					String curYdStkLyrNo = chkResult.getRecord(0).getFieldString("YD_STK_LYR_NO");
					
					slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보 적치열 ["+curYdStkColGp+"] 적치베드 ["+curYdStkBedNo+"] 적치단 ["+curYdStkLyrNo+"]", "SL");
					
					String curYd = curYdStkColGp.length()>=6 ? curYdStkColGp.substring(0,1) : "";
					String curEqp = curYdStkColGp.length()>=6 ? curYdStkColGp.substring(2,4) : "";
					
					if( ydEqpId.equals(curYdStkColGp) &&
						ydStkBedNo.equals(curYdStkBedNo) &&
						("00" + (ii + 1)).equals(curYdStkLyrNo)){
						slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보와 수불구 재료 적치정보 적치정보 동일하므로 clear 하지 않음", "SL"); 
					}
					//적치야드가 기존과 같으며, 적치정보가 PU,PI 경우에만 기존 정보 clear (ora- 에러 중복적치 현상 방지)  
					else if(yd_gp.equals(curYd) && (curEqp.equals("PU") || curEqp.equals("PI") || curEqp.equals("SB"))){
						slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보와 수불구 재료 적치정보 적치정보 다르므로, 기존 정보 clear", "SL"); 
						
						jrParam = slabUtils.getParam(logId, methodNm, modifier);
						jrParam.setField("V_MODIFIER"				,modifier);
						jrParam.setField("V_STL_NO"				,"");
						jrParam.setField("V_YD_STK_LYR_MTL_STAT"	,"E");
						jrParam.setField("V_YD_STK_COL_GP"		,curYdStkColGp);
						jrParam.setField("V_YD_STK_BED_NO"		,curYdStkBedNo);
						jrParam.setField("V_YD_STK_LYR_NO"		,curYdStkLyrNo);
						
						
						commDao.updSlabYd("StkLyrStlNo", jrParam);
					
					}

					else{
						slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보가 같은야드 픽업베드가 아니므로, 처리 불가", "SL");
						throw new Exception("["+stlNo+"] 기존 적치정보가 같은야드 픽업베드가 아니므로, 처리 불가");
					}
				}
				else {
					slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보 존재하지 않음", "SL");
				}
			}
			
			
			
		    stlNo = ""; //재료번호
			String[][] param = new String[cnt][6];

			//Param Set
			for (int ii = 0; ii < cnt; ii++) {
				stlNo = slabUtils.trim(rcvMsg.getFieldString("STL_NO" + (ii + 1)));	//재료번호
				param[ii][0] = modifier;		//수정자
				param[ii][1] = stlNo;			//재료번호
				//야드적치단재료상태
				if ("".equals(stlNo)) {
					param[ii][2] = "E";			//적치가능	
				} else {
					param[ii][2] = "C";			//적치중
				}
				param[ii][3] = ydEqpId;			//야드적치열구분(야드설비ID)
				param[ii][4] = ydStkBedNo;		//야드적치Bed번호
				param[ii][5] = "00" + (ii + 1);	//야드적치단번호
			}

			//적치Bed Table Update
			commDao.upsBatch("StkLyrStlNo", param, logId, methodNm);

			//저장품 저장위치만 Update
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_STK_COL_GP", ydEqpId   ); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호
			jrParam.setField("V_MODIFIER"     , modifier  ); //수정자

			//저장품 Table Update
			commDao.updSlabYd("StockLoc", jrParam);
			
			  
			/**********************************************************
			* 4. 공통Table 야드저장위치  Update
			**********************************************************/
			JDTORecordSet jsChk = rcv2Dao.getC3YDL005("BedLayer", jrParam);

			if (jsChk != null && jsChk.size() > 0) {
				for (int ii = 0; ii < jsChk.size(); ii++) {
					JDTORecord jrChk = jsChk.getRecord(ii);
					
					stlNo = slabUtils.trim(jrChk.getFieldString("STL_NO")); //적치Bed 재료번호
					String slabGp   = slabUtils.trim(jrChk.getFieldString("SLAB_GP"   )); //Slab구분(공통 수정용)
					String ydStrLoc = slabUtils.trim(jrChk.getFieldString("YD_STR_LOC")); //야드저장위치(공통 수정용)
			 

					//야드저장위치 값이 있으면
					if (!"".equals(ydStrLoc)) {
						jrParam.setField("V_STL_NO", stlNo);
						jrParam.setField("V_YD_STR_LOC", ydStrLoc); //야드저장위치
						if ("M".equals(slabGp)) {
							//주편공통
							rcv2Dao.updC3YDL005("MslabComm", jrParam);
						} else {
							//Slab공통
							rcv2Dao.updC3YDL005("SlabComm", jrParam);
						}
					}
			
				  }
				}
			//////////////////////////////////////////////////////////
			

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : C7수불구재료적치정보(C7YDL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC7YDL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C7수불구재료적치정보[SlabYdL2RcvSeEJB.rcvC7YDL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수불구재료적치정보 처리
			return this.rcvC3YDL002(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : C3OHCTake-Out요구, A6OHCTake-Out요구(C3YDL003,A6YDL003) 
	 *      2021.07.02 연주 PU CR 비상작업 지원 A6YDL003 사용
	 *      
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvC3YDL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C3OHCTake-Out요구[SlabYdL2RcvSeEJB.rcvC3YDL003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			//slabUtils.printLog(logId, "삭제된 전문입니다.", "SL");

			//수신 항목 값 
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //장비번호 (rt)
			String ydStkBedNo    = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"    	 )); //적치 bed 번호
			String stlNo	     = slabUtils.trim(rcvMsg.getFieldString("STL_NO"    	 )); //재료번호
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"     )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
			} else if ("".equals(stlNo)) {
				throw new Exception("재료번호(STL_NO) 없음");
			}
			
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_STK_COL_GP"      , ydEqpId   );  //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO"      , ydStkBedNo);  //야드적치Bed번호
			jrParam.setField("V_YD_STK_LYR_NO"      , "001"); 		//야드적치단 번호
			jrParam.setField("V_YD_STK_LYR_MTL_STAT", "C"       );  //야드적치단재료상태(적치중)
			jrParam.setField("V_MODIFIER"           , modifier  );  //수정자
			jrParam.setField("V_STL_NO"           , stlNo  );  //수정자
			//적치단 UPDATE
			commDao.updSlabYd("StkLyrStlNo", jrParam);
			//슬라브공통 Table Update
			//commDao.updSlabYd("SlabCommLyr",jrParam);
			//저장품 Table 산적LotType 등 Update
			commDao.insSlabYd("Stock", jrParam);
			
			JDTORecord jrRtn = null;
			/**********************************************************
			* 2-1. 저장품제원 전문을 전송(C연주)
			**********************************************************/
			
			jrParam.setField("V_STL_NO"          , stlNo); //재료번호
			jrParam.setField("V_YD_GP"          , "A"); //야드구분
			jrParam.setField("V_YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)
			//저장품제원(YDY1L002) 전송Data 조회
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY1L002", jrParam));
		
			/**********************************************************
			* 3. OHC Take-Out 처리
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

			//설비인출요구
			jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ410"               ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
			jrYdMsg.setField("YD_STK_BED_NO"     , ydStkBedNo               ); //야드적치Bed번호
			jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
			jrYdMsg.setField("V_MODIFIER"        , modifier                 ); //수정자
			//jrYdMsg.setField("STL_NOS"        	 , stlNo                 ); //수정자
			//전송할 전문에 추가
			jrRtn = slabUtils.addSndData(jrRtn,jrYdMsg);

			
			slabUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : Take-Out완료(C3YDL004, C7YDL004, Y3YDL012 , E9YDL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC3YDL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Take-Out완료[SlabYdL2RcvSeEJB.rcvC3YDL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID
			String ydStkBedNo    = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"    )); //야드적치Bed번호
			String ydStkBedStlSh = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")); //야드적치Bed재료매수
			String carryOutReqYn = slabUtils.trim(rcvMsg.getFieldString("CARRY_OUT_REQ_GP" )); //Carry-Out요구구분
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"       )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
			} else if ("".equals(ydStkBedStlSh)) {
				throw new Exception("적치Bed재료매수(YD_STK_BED_STL_SH) 없음");
			}
			
			/*
			 * 그라인딩 머신 트랭킹정보 삭제
			 */
			if("MBPU01".equals(ydEqpId))
			{
				YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao();	
				
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("STL_NO", 			    "");
				recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				recPara.setField("YD_STK_COL_GP", 	    "MBGM01");
				recPara.setField("STL_NO1", 	   	    slabUtils.trim(rcvMsg.getFieldString("STL_NO")));
				ydStkLyrDao.updYdStklyrWithColStock(recPara);
				
				//21.09.28 추가 MADP01 take-in 완료로 인한 MART01 저장위치 트래킹 clear 하지 않아 작업 되지 않는 문제 개선. MART01 또한 clear
				recPara.setField("YD_STK_COL_GP", 	    "MART01");
				ydStkLyrDao.updYdStklyrWithColStock(recPara);
				//
				
				recPara.setField("STL_NO", 	   	    slabUtils.trim(rcvMsg.getFieldString("STL_NO")));
				boolean IsGrinderOut = ydStkLyrDao.isGrinderTakeOut(recPara);
				
				if(IsGrinderOut) {
					String YdDnWrLoc = ydEqpId+ydStkBedNo;
					String YdDnWrLayer = ydStkBedStlSh;
					recPara.setField("MODIFIER", 	   	    "E9YDL004");
					recPara.setField("YD_SCH_CD", 	   	    "MBYDPUMM");
					//recPara.setField("STL_NO", 	   	    slabUtils.trim(rcvMsg.getFieldString("STL_NO")));
					recPara.setField("YD_DN_WR_LOC", YdDnWrLoc);//권하위치
					recPara.setField("YD_DN_WR_LAYER", YdDnWrLayer);//권하단
					ydStkLyrDao.insYdWrkHistByGrinder(recPara);
				}
				//////
				//-> 그라인딩에 있던놈만 작업이력 찍어야한다.
				
				//마킹표시 지정
				GridData returnGrid = new GridData();
				GridData gdRtn = new GridData();
				
				gdRtn.createHeader("V_STL_NOS" , "T");
				gdRtn.addParam("V_STL_NOS" , slabUtils.trim(rcvMsg.getFieldString("STL_NO")));
				gdRtn.createHeader("V_MARKING_YN" , "T");
				gdRtn.addParam("V_MARKING_YN" , "Y");
				gdRtn.createHeader("V_MODIFIER" , "T");
				gdRtn.addParam("V_MODIFIER" , modifier);
				
				//param setting
				JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
				jrParam2.addField("V_STL_NOS" , slabUtils.trim(rcvMsg.getFieldString("STL_NO")));
				jrParam2.addField("V_MARKING_YN" , "Y");
				jrParam2.addField("V_MODIFIER" , modifier);
				
				SlabJspSeEJBBean slabJspSeEJBBean = new SlabJspSeEJBBean();
				slabJspSeEJBBean.updStockMarkReg(slabUtils.jdtoRecordToGridData(returnGrid,jrParam2,gdRtn));
				
			}
			
			
			//C3# 스카핑 야드 재료 8단으로 증설에 따른 작업
			String yd_gp = ydEqpId.substring(0,1);
			int cnt =6 ;
			
			if("M".equals(yd_gp)){
				cnt =9 ;
			}
			
			//20240412 추가. 같은 베드에 중복재료 정보 오는 부분 EXCEPTION 처리 (추후 ORA 에러 방지 )
			for(int ii=0; ii< cnt; ii++){
				String stlNo = slabUtils.trim(rcvMsg.getFieldString("STL_NO" + (ii + 1)));	//재료번호
				
				if("".equals(stlNo)) continue;
				
				for(int jj=ii+1; jj<cnt; jj++){
					String compStlNo = slabUtils.trim(rcvMsg.getFieldString("STL_NO" + (jj + 1)));	//재료번호
					if("".equals(compStlNo)) continue;
					
					if(stlNo.equals(compStlNo)){
						slabUtils.printLog(logId, "["+(ii + 1)+"] 단 재료정보 ["+stlNo+"]와 ["+(jj + 1)+"] 단 재료정보 ["+compStlNo+"] 일치하므로 에러", "SL"); 
						throw new Exception("["+(ii + 1)+"] 단 재료정보 ["+stlNo+"]와 ["+(jj + 1)+"] 단 재료정보 ["+compStlNo+"] 일치하므로 에러");
					}
				}
			}
			
			
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			int lyrNo = 0;							//단번호
			int mtlSh = 0;							//Bed재료매수
			String[][] bedMtl = new String[cnt][2];	//Bed재료정보

			for (int ii = 0; ii < cnt; ii++) {
				for (int jj = 0; jj < 2; jj++) {
					bedMtl[ii][jj] = "";
				}
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO"      , ydStkBedNo); //야드적치Bed번호
			jrParam.setField("V_YD_STK_LYR_MTL_STAT", "C"       ); //야드적치단재료상태(적치중)
			jrParam.setField("V_MODIFIER"           , modifier  ); //수정자

			//기 등록된 Bed 재료정보 조회 - 이미 재료정보가 생성되어 있으면 다시 등록 안 함
			JDTORecordSet jsChk = rcv2Dao.getC3YDL004("BedMtl", jrParam);

			if (jsChk != null && jsChk.size() > 0) {
				mtlSh = jsChk.size();
				for (int ii = 0; ii < mtlSh; ii++) {
					lyrNo = Integer.parseInt(slabUtils.nvl(jsChk.getRecord(0).getFieldString("YD_STK_LYR_NO"),"0"));	//야드적치단번호
					if (lyrNo > 0 && lyrNo < cnt) {
						bedMtl[lyrNo][0] = slabUtils.trim(jsChk.getRecord(ii).getFieldString("STL_NO"       ));	//재료번호
						bedMtl[lyrNo][1] = slabUtils.trim(jsChk.getRecord(ii).getFieldString("YD_AIM_BAY_GP"));	//야드목표동구분
					}
				}
			}
			
			//항만슬라브야드 기능추가 - 2016.01.12 LeeJY : 적치단에 Take-In된 재료가 없슴()
			//항만야드는 오로지 B동으로만 수입(Carry Out)함
			else {
				String ydGp = ydEqpId.substring(0,1);
				if (ydGp.equals("M")) {
					mtlSh = Integer.parseInt(ydStkBedStlSh);
					logger.println(LogLevel.INFO, "-----> 항만슬라브야드 적치bed처리 (매수) : " + mtlSh);
					for (int ii = 0; ii <= mtlSh; ii++) {
						lyrNo = ii;	//야드적치단번호
						if (lyrNo > 0 && lyrNo < cnt) {
							//bedMtl[lyrNo][0] = slabUtils.trim(rcvMsg.getFieldString("STL_NO" + ii));	//재료번호
							bedMtl[lyrNo][0] = "None";	//재료번호
							bedMtl[lyrNo][1] = "B";	//야드목표동구분
							logger.println(LogLevel.INFO, "-----> 항만슬라브야드 적치단 재료 : " + bedMtl[lyrNo][0]);
						}
					}
					if (bedMtl[1][0].equals("")) {
						throw new Exception(">>> (항만야드) Carry-Out할 재료가 없음");
					}
				}
			}
			
			String stlNo = "";	//재료번호
			mtlSh = Integer.parseInt(ydStkBedStlSh); //야드적치Bed재료매수
			
			//적치Bed 정보 Set
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = slabUtils.trim(rcvMsg.getFieldString("STL_NO" + ii));	//재료번호
				if ("".equals(stlNo)) {
					throw new Exception("재료번호(STL_NO" + ii + ") 없음");
				}
				
				jrParam.setField("V_STL_NO"       , stlNo    ); //재료번호
				jrParam.setField("V_YD_STK_LYR_NO", "00" + ii); //야드적치단번호
				jrParam.setField("V_YD_STK_LYR_NO2", "0" + ii); //야드적치단번호2
				
				//기 등록된 적치단에 재료번호가 없거나 다르면
				if (!stlNo.equals(bedMtl[ii][0])) {
					
					//20240314 픽업베드 파일러크레인으로 이적 시 FROM 위치 CLEAR -> TO위치 적치 정보 로 전문이 온다
					// 하지만 도중에 TO위치 바꾸면 FROM 위치 CLEAR 정보가 오지 않아, 재료 중복 적치 현상 발생
					//중복적치 현상 해결을 위해 이전 저장위치와 현 저장위치 다르면, CLEAR 처리
					jrParam.setField("STL_NO"       , stlNo    ); //재료번호
					JDTORecordSet chkResult = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO", logId, methodNm, "재료 저장위치 검색");
					
					if(chkResult != null && chkResult.size() > 0) {
						String curYdStkColGp = chkResult.getRecord(0).getFieldString("YD_STK_COL_GP");
						String curYdStkBedNo = chkResult.getRecord(0).getFieldString("YD_STK_BED_NO");
						String curYdStkLyrNo = chkResult.getRecord(0).getFieldString("YD_STK_LYR_NO");
						
						slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보 적치열 ["+curYdStkColGp+"] 적치베드 ["+curYdStkBedNo+"] 적치단 ["+curYdStkLyrNo+"]", "SL");
						
						String curYd = curYdStkColGp.length()>=6 ? curYdStkColGp.substring(0,1) : "";
						String curEqp = curYdStkColGp.length()>=6 ? curYdStkColGp.substring(2,4) : "";
						
						if( ydEqpId.equals(curYdStkColGp) &&
								ydStkBedNo.equals(curYdStkBedNo) &&
								("00" + (ii + 1)).equals(curYdStkLyrNo)){
								slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보와 take-out 실적정보 동일하므로 clear 하지 않음", "SL"); 
							}
							//적치야드가 기존과 같으며, 적치정보가 PU,PI 경우에만 기존 정보 clear (ora- 에러 중복적치 현상 방지)  
							else if(yd_gp.equals(curYd) && (curEqp.equals("PU") || curEqp.equals("PI") || curEqp.equals("SB"))){
								slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보와 take-out 실적정보 다르므로, 기존 정보 clear", "SL"); 
								
								JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
								jrParam2 = slabUtils.getParam(logId, methodNm, modifier);
								jrParam2.setField("V_MODIFIER"				,modifier);
								jrParam2.setField("V_STL_NO"				,"");
								jrParam2.setField("V_YD_STK_LYR_MTL_STAT"	,"E");
								jrParam2.setField("V_YD_STK_COL_GP"		,curYdStkColGp);
								jrParam2.setField("V_YD_STK_BED_NO"		,curYdStkBedNo);
								jrParam2.setField("V_YD_STK_LYR_NO"		,curYdStkLyrNo);
								
								
								commDao.updSlabYd("StkLyrStlNo", jrParam2);
							
							}

							else{
								slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보가 같은야드 픽업베드가 아니므로, 처리 불가", "SL");
								throw new Exception("["+stlNo+"] 기존 적치정보가 같은야드 픽업베드가 아니므로, 처리 불가");
							}

					}
					else {
						slabUtils.printLog(logId, "["+stlNo+"] 기존 적치정보 존재하지 않음", "SL");
					}
				
					
					
					//적치단 Table Update
					commDao.updSlabYd("StkLyrStlNo", jrParam);
					
					//슬라브공통 Table Update
					commDao.updSlabYd("SlabCommLyr",jrParam);
					
					//주편공통 Table Update
					commDao.updSlabYd("MSlabCommLyr",jrParam);

					//저장품 Table 산적LotType 등 Upsert
					commDao.insSlabYd("Stock", jrParam);
				} else if ("".equals(bedMtl[ii][1])) {
					//기 등록된 야드목표동구분이 없으면 저장품 Table에 등록되지 않은 것으로 간주하여 저장품 정보 등록
					commDao.insSlabYd("Stock", jrParam);
				}
			}
			
			JDTORecord jrRtn = null;
			/**********************************************************
			* 2-1. 저장품제원 전문을 전송(C연주)
			**********************************************************/
			if("A".equals(yd_gp)){
				stlNo =   slabUtils.trim(rcvMsg.getFieldString("STL_NO"));
				if(!"".equals(stlNo)){
					jrParam.setField("V_STL_NO"          , stlNo); //재료번호
					jrParam.setField("V_YD_GP"          , "A"); //야드구분
					jrParam.setField("V_YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)
					//저장품제원(YDY1L002) 전송Data 조회
					jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY1L002", jrParam));
				}

			}

			/**********************************************************
			* 3. Carry-Out 처리
			**********************************************************/
			//Carry-Out요구구분이 'Y'이면
			if ("Y".equals(carryOutReqYn)) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

				//설비인출요구
				jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ410"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YD_STK_BED_NO"     , ydStkBedNo               ); //야드적치Bed번호
				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("V_MODIFIER"        , modifier                 ); //수정자
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn,jrYdMsg);
			}
 
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	 
	/**
	 *      [A] 오퍼레이션명 : 그라인딩머신 Take-Out완료(C8YDL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC8YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Take-Out완료[SlabYdL2RcvSeEJB.rcvC8YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값 
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydSlabNo      = slabUtils.trim(rcvMsg.getFieldString("SLAB_NO"        )); //SlabNo
			String ydPosition    = slabUtils.trim(rcvMsg.getFieldString("POSITION"    	 )); //Position
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"     )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			String ydEqpId 		= "";
			String ydStkBedNo 	= "";
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydSlabNo)) {
				throw new Exception("SLAB NO 없음");
			} else if ("".equals(ydPosition)) {
				throw new Exception("POSITION 없음");
			}

			if ("1".equals(ydPosition)) {
				ydEqpId		= "ABDP04";
				ydStkBedNo 	= "01";
			}else if ("2".equals(ydPosition)) {
				ydEqpId		= "ABDP04";
				ydStkBedNo 	= "02";
			}else if ("3".equals(ydPosition)) {
				ydEqpId		= "AADP04";
				ydStkBedNo 	= "01";
			}else if ("4".equals(ydPosition)) {
				ydEqpId		= "AADP04";
				ydStkBedNo 	= "02";
			}
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO"      , ydStkBedNo); //야드적치Bed번호
			jrParam.setField("V_YD_STK_LYR_MTL_STAT", "C"       ); //야드적치단재료상태(적치중)
			jrParam.setField("V_MODIFIER"           , modifier  ); //수정자
			jrParam.setField("V_STL_NO"       		, ydSlabNo  ); //재료번호
			jrParam.setField("V_YD_STK_LYR_NO"		, "001"     ); //야드적치단번호
			
			//적치단 Table Update
			commDao.updSlabYd("StkLyrStlNo", jrParam);

			//저장품 Table 산적LotType 등 Upsert
			commDao.insSlabYd("Stock", jrParam);
			
			JDTORecord jrRtn =  JDTORecordFactory.getInstance().create();
			
			/**********************************************************
			* 2.2 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("V_YD_GP"          , "A"); //야드구분
			jrParam.setField("V_YD_INFO_SYNC_CD", "5" ); //야드정보동기화코드(지정저장품)

			//저장품제원(YDY1L002) 전송Data 조회
			jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L002", jrParam));
			
			/**********************************************************
			* 3. Carry-Out 처리
			**********************************************************/
			
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

			//설비인출요구
			jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ410"               ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
			jrYdMsg.setField("YD_STK_BED_NO"     , ydStkBedNo               ); //야드적치Bed번호
			jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
			jrYdMsg.setField("V_MODIFIER"        , modifier                 ); //수정자
			
			//전송할 전문에 추가
			jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
			
			/**********************************************************
			* 4. GM 머신스카핑 출고이력 UPDATE
			**********************************************************/
			jrParam.setField("EQP_CD"			, "MC"); //설비코드
			jrParam.setField("EQP_GP"   		, "GM"  ); //설비구분
			jrParam.setField("MODIFIER" 		, modifier  ); //수정자
			jrParam.setField("STL_NO" 			, ydSlabNo  ); //수정자
			
			commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdEqpIOHistByStlNo", logId, methodNm, "머신스카핑 출고이력");
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 그라인딩머신 Take-In완료(C8YDL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC8YDL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Take-In완료[SlabYdL2RcvSeEJB.rcvC8YDL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값 
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydSlabNo      = slabUtils.trim(rcvMsg.getFieldString("SLAB_NO"        )); //SlabNo
			String ydPosition    = slabUtils.trim(rcvMsg.getFieldString("POSITION"    	 )); //Position
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"     )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			String ydEqpId 		= "";
			String ydStkBedNo 	= "";
			String slabGp       = "";	//Slab구분(S:Slab, M:주편)
			String ydStrLoc     = "";	//야드저장위치(공통 수정용)
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydSlabNo)) {
				throw new Exception("SLAB NO 없음");
			} else if ("".equals(ydPosition)) {
				throw new Exception("POSITION 없음");
			}

			if ("1".equals(ydPosition)) {
				ydEqpId		= "ABDP04";
				ydStkBedNo 	= "01";
			}else if ("2".equals(ydPosition)) {
				ydEqpId		= "ABDP04";
				ydStkBedNo 	= "02";
			}else if ("3".equals(ydPosition)) {
				ydEqpId		= "AADP04";
				ydStkBedNo 	= "01";
			}else if ("4".equals(ydPosition)) {
				ydEqpId		= "AADP04";
				ydStkBedNo 	= "02";
			}
			
			/**********************************************************
			* 2. Take-In 재료번호 Check
			**********************************************************/
			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_STK_COL_GP", ydEqpId   ); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호
			jrParam.setField("V_YD_STK_LYR_NO", "001"); //야드적치단번호

			JDTORecordSet jsChk = rcv2Dao.getC3YDL005("Bed", jrParam);

			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);
				
				slabGp   = slabUtils.trim(jrChk.getFieldString("SLAB_GP"   )); //Slab구분(공통 수정용)
				ydStrLoc = slabUtils.trim(jrChk.getFieldString("YD_STR_LOC")); //야드저장위치(공통 수정용)				
			} else {
				throw new Exception("적치단[" + ydEqpId + "-" + ydStkBedNo + "- 001 ] 정보가 없습니다.");
			}
			
			
			/**********************************************************
			* 3. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO"      , ydStkBedNo); //야드적치Bed번호
			jrParam.setField("V_YD_STK_LYR_MTL_STAT", "E"       ); //야드적치단재료상태(적치가능)
			jrParam.setField("V_MODIFIER"           , modifier  ); //수정자
			jrParam.setField("V_STL_NO"       		, ""	  	); //재료번호
			jrParam.setField("V_YD_STK_LYR_NO"		, "001"     ); //야드적치단번호
			
			//적치단 Table Update
			commDao.updSlabYd("StkLyrStlNo", jrParam);
						
			
			//20220919 스카핑 CFT 이근 책임매니저 요청
			//C8 그라인딩 L2 수정 불가에 따라, 그라인딩 TAKE-IN 시간 직접 연주조업으로 전송
			jrParam.setField("V_MSLAB_NO"       	, ydSlabNo); //재료번호
			jrParam.setField("V_SCRF_MCNO_GP"		, "1"     ); //그라인딩 호기 (1:연주, 2:C#3)
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDCSJ003", jrParam));
			
			/**********************************************************
			* 4. 공통Table 야드저장위치  Update
			**********************************************************/
			//야드저장위치 값이 있으면
			if (!"".equals(ydStrLoc)) {
				jrParam.setField("V_STL_NO", ydSlabNo); //재료번호
				jrParam.setField("V_YD_STR_LOC", ydStrLoc); //야드저장위치
				if ("M".equals(slabGp)) {
					//주편공통
					rcv2Dao.updC3YDL005("MslabComm", jrParam);
				} else {
					//Slab공통
					rcv2Dao.updC3YDL005("SlabComm", jrParam);
				}
			}
			
			/**********************************************************
			* 5. GM 머신스카핑 입고이력 INSERT
			**********************************************************/
			jrParam.setField("EQP_CD"			, "MC"); //설비코드
			jrParam.setField("EQP_GP"   		, "GM"  ); //설비구분
			jrParam.setField("MODIFIER" 		, modifier  ); //수정자
			jrParam.setField("STL_NO" 			, ydSlabNo  ); //수정자
			
			commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdEqpIOHistByStlNo", logId, methodNm, "머신스카핑 입고이력");
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			//JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			 
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : C7Take-Out완료(C7YDL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC7YDL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C7Take-Out완료[SlabYdL2RcvSeEJB.rcvC7YDL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//Take-Out완료 처리
			return this.rcvC3YDL004(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : Take-In완료(C3YDL005, Y3YDL013, E9YDL005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC3YDL005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Take-In완료[SlabYdL2RcvSeEJB.rcvC3YDL005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			

			//수신 항목 값
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID
			String ydStkBedNo    = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"    )); //야드적치Bed번호
			String ydStkBedStlSh = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")); //야드적치Bed재료매수
			String takeInStlNo   = slabUtils.trim(rcvMsg.getFieldString("STL_NO"           )); //재료번호(Take-In)
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"       )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			/*
			 * 그라인더 머신 트래킹정보 생성
			 */
			if("MBGM01".equals(ydEqpId)){
				
				YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
				
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_STK_COL_GP", 	    "MBGM01");
				jrParam.setField("YD_STK_BED_NO", 	    "01");
				jrParam.setField("YD_STK_LYR_NO", 	    "001");
				jrParam.setField("YD_STK_LYR_MTL_STAT", "C");
				jrParam.setField("STL_NO", 			    takeInStlNo);
				
				ydStklyrDao.updYdStklyrWithGM(jrParam);
				ydStklyrDao.updYdStklyr(jrParam, 0);
				
				
				jrParam.setField("MODIFIER", "E9YDL005");//수정자
				jrParam.setField("YD_SCH_CD", "MBYDPUMM");//스케쥴코드
				
				jrParam.setField("YD_DN_WR_LOC", "MBGM0101");//권하위치
				jrParam.setField("YD_DN_WR_LAYER", "001");//권하단
				//jrParam.setField("STL_NO", takeInStlNo);
				ydStklyrDao.insYdWrkHistByGrinder(jrParam);


				//take-in 시 MART clear 
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
								
				//24.03.18 추가 MADP01 take-in 완료로 인한 MART01 저장위치 트래킹 clear 하지 않아 작업 되지 않는 문제 개선. MART01 또한 clear
				recPara.setField("STL_NO", 			    "");
				recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				recPara.setField("YD_STK_COL_GP", 	    "MART01");
				recPara.setField("STL_NO1", 	   	    slabUtils.trim(rcvMsg.getFieldString("STL_NO")));
				ydStklyrDao.updYdStklyrWithColStock(recPara);
				
				
				//20220919 스카핑 CFT 이근 책임매니저 요청
				//그라인딩 L2 수정 불가에 따라, 그라인딩 TAKE-IN 시간 직접 연주조업으로 전송
				jrParam.setField("V_MSLAB_NO"       	, takeInStlNo); //재료번호
				jrParam.setField("V_SCRF_MCNO_GP"		, "2"     ); //그라인딩 호기 (1:연주, 2:C#3)
				
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDCSJ003", jrParam));
				return jrRtn;
			}
				
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
			} else if ("".equals(ydStkBedStlSh)) {
				throw new Exception("적치Bed재료매수(YD_STK_BED_STL_SH) 없음");
			} else if ("".equals(takeInStlNo)) {
				throw new Exception("Take-In재료번호(STL_NO) 없음");
			}
			
			
			String ydStkLyrNo   = "00" + (Integer.parseInt(ydStkBedStlSh) + 1); //Take-In 야드적치단번호
			String slabGp       = "";	//Slab구분(S:Slab, M:주편)
			String ydStrLoc     = "";	//야드저장위치(공통 수정용)
			String carryInReqYn = "N";	//Carry-In요구구분
			
			/**********************************************************
			* 2. Take-In 재료번호 Check
			**********************************************************/
			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_STK_COL_GP", ydEqpId   ); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호
			jrParam.setField("V_YD_STK_LYR_NO", ydStkLyrNo); //야드적치단번호

			JDTORecordSet jsChk = rcv2Dao.getC3YDL005("Bed", jrParam);

			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);
				
				String stlNo = slabUtils.trim(jrChk.getFieldString("STL_NO")); //적치Bed 재료번호
				if (!takeInStlNo.equals(stlNo)) {
					throw new Exception("Take-In 재료번호[" + takeInStlNo + "]와 적치Bed 재료번호[" + stlNo + "]가 서로 다릅니다.");
				}
				
				slabGp   = slabUtils.trim(jrChk.getFieldString("SLAB_GP"   )); //Slab구분(공통 수정용)
				ydStrLoc = slabUtils.trim(jrChk.getFieldString("YD_STR_LOC")); //야드저장위치(공통 수정용)

				//Carry-In요구 결정
				String tiSupYn    = slabUtils.trim(jrChk.getFieldString("TI_SUP_YN"    )); //TakeIn공Bed보급요구여부
				String tiPreSupYn = slabUtils.trim(jrChk.getFieldString("TI_PRE_SUP_YN")); //TakeInBed1매선보급요구여부

				//#1 Scafer에서 Hand스카핑장으로 Take-In 되는 경우는 Carry-In요구를 하지 않는다.
				if ("Y".equals(tiSupYn) && !"ABPUP5".equals(ydEqpId)) {
					//공Bed 이거나 Bed에 1매 있고 선보급요구여부가 "Y"이면
					if ("001".equals(ydStkLyrNo) || ("002".equals(ydStkLyrNo) && "Y".equals(tiPreSupYn))) {
						carryInReqYn = "Y"; //Carry-In요구구분
					}
				}
			} else {
				throw new Exception("적치단[" + ydEqpId + "-" + ydStkBedNo + "-" + ydStkLyrNo + "] 정보가 없습니다.");
			}
			
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  Update
			**********************************************************/
			jrParam.setField("V_MODIFIER"           , modifier); //수정자
			jrParam.setField("V_STL_NO"             , ""      ); //재료번호
			jrParam.setField("V_YD_STK_LYR_MTL_STAT", "E"     ); //야드적치단재료상태(적치가능)

			//적치Bed Table Update
			commDao.updSlabYd("StkLyrStlNo", jrParam);
			
			
			/*
			 * C#3 디파일러 -> R/T 트래킹정보 생성
			 */
			if("MADP01".equals(ydEqpId)){
				
				YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
				
				JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
				jrParam2.setField("YD_STK_COL_GP", 	    "MART01");
				jrParam2.setField("YD_STK_BED_NO", 	    "01");
				jrParam2.setField("YD_STK_LYR_NO", 	    "001");
				jrParam2.setField("YD_STK_LYR_MTL_STAT", "C");
				jrParam2.setField("STL_NO", 			    takeInStlNo);
				
				//ydStklyrDao.updYdStklyrWithGM(jrParam2);
				ydStklyrDao.updYdStklyr(jrParam2, 0);
				
				
				//return null;
			}
			
			/**********************************************************
			* 3. 송신 전문 조회
			* 3.1 생산통제 장입진행실적(Take-In) : (C열연:YDCTJ033, 후판:YDCTJ031)
			* 3.2 Slab야드 야드저장위치제원 : YDY1L001, YDY3L001, YDE7L001
			* 3.3 보급요구처리 : Slab보급설비기준(YDB033)에 정의 됨
			**********************************************************/
			//생산통제 장입진행실적(Take-In) (YDCTJ033, YDCTJ031)
			jrParam.setField("V_STL_NO", takeInStlNo); //재료번호
			jrRtn = slabUtils.addSndData(commDao.getMsgL3("YDCTJ033TI", jrParam));
			
			//Slab야드 저장위치제원(YDY1L001, YDY3L001, YDE7L001)
			jrParam.setField("V_YD_INFO_SYNC_CD", "4"); //야드정보동기화코드(Bed)
			if (ydEqpId.startsWith("M")) {  //항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE7L001", jrParam));
			}
			else {
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY1L001", jrParam));
			}

			//Carry-In요구구분이 'Y'이면
			logger.println(LogLevel.INFO, "-----> Carry-In요구구분 : " + carryInReqYn);
			if ("Y".equals(carryInReqYn)) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

				//설비보급요구 전문
				logger.println(LogLevel.INFO, "-----> 설비보급요구 처리 (YDYDJ420) ");
				jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ420"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YD_STK_BED_NO"     , ydStkBedNo               ); //야드적치Bed번호
				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("V_MODIFIER"        , modifier                 ); //수정자
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
			}
			logger.println(LogLevel.INFO, "-----> 설비보급요구 처리중 (YDYDJ420) ");

			/**********************************************************
			* 4. 공통Table 야드저장위치  Update
			**********************************************************/
			//야드저장위치 값이 있으면
			if (!"".equals(ydStrLoc)) {
				jrParam.setField("V_YD_STR_LOC", ydStrLoc); //야드저장위치
				if ("M".equals(slabGp)) {
					//주편공통
					rcv2Dao.updC3YDL005("MslabComm", jrParam);
				} else {
					//Slab공통
					rcv2Dao.updC3YDL005("SlabComm", jrParam);
				}
			}
			
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : OHCTake-In요구(C3YDL006,A6YDL006)
	 *      2021.07.02 연주 PU CR 비상작업 지원 A6YDL006 사용
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvC3YDL006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "OHCTake-In요구[SlabYdL2RcvSeEJB.rcvC3YDL006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			//slabUtils.printLog(logId, "삭제된 전문입니다.", "SL");
			//수신 항목 값 
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //장비번호 (rt)
			String ydStkBedNo    = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"    	 )); //적치 bed 번호
			String stlNo	     = slabUtils.trim(rcvMsg.getFieldString("STL_NO"    	 )); //재료번호
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"     )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
			} else if ("".equals(stlNo)) {
				throw new Exception("재료번호(STL_NO) 없음");
			}
			
			//String ydStkColGp = "";
			/**********************************************************
			* 2. 재료의 현위치 조회
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);//JDTORecordFactory.getInstance().create();
			jrParam.setField("V_STL_NO", 	    stlNo);

			JDTORecordSet jsChk = commDao.getStrLocInfo2("stock",jrParam);
			
			if(jsChk.size() <= 0) throw new Exception(stlNo+" 저장 위치 이상");
			/*if (jsChk.size() > 0) {
				ydStkColGp  = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_STK_COL_GP" ));
			} else {
				throw new Exception(stlNo+" 저장 위치 이상");
			}*/
			//전문의 yd_eqp_id RT 번호로 SCH_CD 생성
			String ydSchCd = ydEqpId + "UM";
			String ydWrkPlanCrn = "";
			String ydSchPrior = "";
			//스케줄 기준에서 작업크레인 조회
		
			/**********************************************************
			* 3. 스케쥴코드 조회 및 Check
			**********************************************************/
			//스케줄금지여부 등 Check
			jrParam.setField("V_YD_SCH_CD"     , ydSchCd); //야드스케쥴코드
			jrParam.setField("V_YD_SCH_WHIO_GP", "U"    ); //야드스케쥴입출고구분(출고)

			JDTORecord jrSchCd = slabComm.getSchCd(jrParam);
			
			ydSchCd    		= slabUtils.trim(jrSchCd.getFieldString("YD_SCH_CD"   )); //야드스케쥴코드
			ydWrkPlanCrn    = slabUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   )); //야드설비ID(작업크레인)
			ydSchPrior		= slabUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"   )); //야드설비ID(작업크레인)
			
			/**********************************************************
			* 4. 해당 스케줄 코드로 기 등록된 작업예약 CHECK
			**********************************************************/
			jrParam.setField("STL_NO", 	    stlNo);
			JDTORecordSet chkResult = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSTLNO", logId, methodNm, "작업예약 CHECK");
			
			//기 등록된 작업예약 존재
			if(chkResult != null && chkResult.size() > 0) {
				String chkStlNo = chkResult.getRecord(0).getFieldString("STL_NO");
				if(stlNo.equals(chkStlNo)) throw new Exception("["+stlNo+"] 해당 재료번호로 기 등록된 보급 작업예약 존재");
			}
			
			jrParam.setField("V_YD_SCH_CD"      	,ydSchCd		); //야드스케쥴코드
			jrParam.setField("V_YD_SCH_PRIOR"   	,ydSchPrior		); //스케줄 기동 순위
			//jrParam.setField("V_YD_SCH_ST_GP"   	,"A"		); //야드스케쥴기동구분
			jrParam.setField("V_YD_TO_LOC_GUIDE"	,ydEqpId+ydStkBedNo); //야드To위치Guide
			jrParam.setField("V_YD_EQP_ID"      	,ydWrkPlanCrn		); //야드설비ID(크레인)
			jrParam.setField("V_MODIFIER"       	,modifier); //수정자
			//jrParam.setField("V_WB_MTL_SH"			,String.valueOf(jsChk.size())); //작업예약재료매수
			jrParam.setField("V_YD_WRK_PLAN_CRN"	,ydWrkPlanCrn); //작업크레인
			
			//설비보급요구 처리
			EJBConnector sndConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)sndConn.trx("insMvstkWrkBook", new Class[] { JDTORecord.class, JDTORecordSet.class}, new Object[] { jrParam,jsChk  });

			/*//설비보급요구 결과가 있으면 크레인스케줄 호출 임 : 신 크레인스케줄이면 EJB Call 아니면 JSM 전송
			if (jrRst != null) {
				jrRst.setResultCode(logId);		//Log ID
				jrRst.setResultMsg(methodNm);	//Log Method Name
				jrRst.setField("EJB_MSG_ID", "YDYDJ400"); //EJBCall전문ID
				jrRst = slabComm.rcvMsgToEjbCall(jrRst);
			}*/
			
			slabUtils.printLog(logId, methodNm, "S-");
			return jrRst;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 대차이동실적(C3YDL007, C7YDL007, Y3YDL014)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC3YDL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차이동실적[SlabYdL2RcvSeEJB.rcvC3YDL007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId      = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"      )); //야드설비ID
			String ydTcarMoveGp = slabUtils.trim(rcvMsg.getFieldString("YD_TCAR_MOVE_GP")); //야드대차이동구분
			String ydBayGp1     = slabUtils.trim(rcvMsg.getFieldString("YD_BAY_GP1"     )); //야드동구분1
			String modifier     = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"     )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydTcarMoveGp)) {
				throw new Exception("대차이동구분(YD_TCAR_MOVE_GP) 없음");
			} else if ("".equals(ydBayGp1)) {
				throw new Exception("현재동(YD_BAY_GP1) 없음");
			}

			if (!"S".equals(ydTcarMoveGp) && !"E".equals(ydTcarMoveGp)) {
				slabUtils.printLog(logId, "대차이동구분[" + ydTcarMoveGp + "]이 'S' 또는 'E'가 아니므로 종료", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				return null;
			}

			String ydStkColGp = ydEqpId.substring(0, 1) + ydBayGp1 + ydEqpId.substring(2); //야드적치열구분(현재동)
			
			/**********************************************************
			* 2. 설비 야드현재동구분, 대차스케줄 야드차량진행상태 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_EQP_ID"      , ydEqpId     ); //야드설비ID
			jrParam.setField("V_YD_TCAR_MOVE_GP", ydTcarMoveGp); //야드대차이동구분
			jrParam.setField("V_YD_STK_COL_GP"  , ydStkColGp  ); //야드적치열구분(현재동)
			jrParam.setField("V_MODIFIER"       , modifier    ); //수정자
			//야드현재동구분
			if ("S".equals(ydTcarMoveGp)) {
				jrParam.setField("V_YD_CURR_BAY_GP", "");
			} else {
				jrParam.setField("V_YD_CURR_BAY_GP", ydBayGp1);
			}
			
			//설비 Table 야드현재동구분 수정
			commDao.updSlabYd("EqpCurrBay", jrParam);
			
			//대차스케줄 야드차량진행상태 수정
			rcv2Dao.updC3YDL007("TcarSch", jrParam);

			/**********************************************************
			* 3. 대차스케줄 조회
			**********************************************************/
			JDTORecordSet jsTcar = rcv2Dao.getC3YDL007("TcarSch", jrParam);

			if (jsTcar == null || jsTcar.size() == 0) {
				throw new Exception("대차스케줄[" + ydEqpId + "] 없음");
			}

			JDTORecord jrTcar = jsTcar.getRecord(0);
			String ydTcarSchId       = slabUtils.trim(jrTcar.getFieldString("YD_TCAR_SCH_ID"        )); //야드대차스케쥴ID
			String ydCarProgStat     = slabUtils.trim(jrTcar.getFieldString("YD_CAR_PROG_STAT"      )); //야드차량진행상태
			String ydCarldWrkbookId  = slabUtils.trim(jrTcar.getFieldString("YD_CARLD_WRK_BOOK_ID"  )); //야드상차작업예약ID
			String ydCarudWrkbookId  = slabUtils.trim(jrTcar.getFieldString("YD_CARUD_WRK_BOOK_ID"  )); //야드하차작업예약ID
			String ydCarldStopLoc    = slabUtils.trim(jrTcar.getFieldString("YD_CARLD_STOP_LOC"     )); //야드상차정지위치
			String ydCarudStopLoc    = slabUtils.trim(jrTcar.getFieldString("YD_CARUD_STOP_LOC"     )); //야드하차정지위치
			String ydStkBedActStatLd = slabUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_LD")); //야드적치Bed활성상태(상차정지위치)
			String ydStkBedActStatUd = slabUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_UD")); //야드적치Bed활성상태(하차정지위치)
			//업무기준(YDB034) 야드스케쥴기동구분(Y:기동,N:기동안함) : 출발 또는 도착 중 하나 이상은 반드시 'Y'로 되어 있어야 함
			String crnSchYn          = slabUtils.trim(jrTcar.getFieldString("CRN_SCH_YN"            )); //크레인스케줄기동여부
			String ydWbookId         = ""; //작업예약ID
			String ydSchReqGp        = ""; //야드스케쥴요청구분

			slabUtils.printLog(logId, "대차[" + ydEqpId + "] 스케줄 >> 대차스케쥴ID:" + ydTcarSchId + ", 차량진행:" + ydCarProgStat + ", 크레인스케줄:" + crnSchYn
					                + ", 상차작업:" + ydCarldWrkbookId + "-" + ydCarldStopLoc + ", 하차작업:" + ydCarudWrkbookId + "-" + ydCarudStopLoc, "SL");
			
			/**********************************************************
			* 4. 적치Bed, 적치단 Table 상태 수정
			**********************************************************/
			jrParam.setField("V_YD_STK_BED_NO" , "01"       ); //야드적치Bed번호
			jrParam.setField("V_YD_TCAR_SCH_ID", ydTcarSchId); //야드대차스케쥴ID
			
			if ("A".equals(ydCarProgStat)) {
				ydSchReqGp = "2"; //영대차출발 : 하차출발(A)

				//영대차출발이면 출발위치 적치Bed 비활성화 처리
				jrParam.setField("V_YD_STK_COL_GP"      , ydStkColGp); //야드적치열구분
				jrParam.setField("V_YD_STK_BED_ACT_STAT", "C"       ); //야드적치Bed활성상태(비활성화)
				commDao.updStat("StkBedAct", jrParam);
				
				//출발위치 적치단 재료 삭제
				commDao.updSlabYd("StkLyrClr", jrParam);

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarudWrkbookId; //야드하차작업예약ID

					//하차위치 적치Bed 활성화 처리
					jrParam.setField("V_YD_STK_COL_GP"      , ydCarudStopLoc); //적치Bed 야드적치열구분(하차정지위치)
					jrParam.setField("V_YD_STK_BED_ACT_STAT", "L"           ); //적치Bed 야드적치Bed활성상태(활성)
					commDao.updStat("StkBedAct", jrParam);

					//하차위치 적치단 재료번호 등록
					rcv2Dao.updC3YDL007("StkLyrStl", jrParam);

					//설비 Table 야드현재동구분 미리 수정
					jrParam.setField("V_YD_CURR_BAY_GP", ydCarudStopLoc.substring(1, 2));
					commDao.updSlabYd("EqpCurrBay", jrParam);
				}
			} else if ("B".equals(ydCarProgStat)) {
				ydSchReqGp = "3"; //영대차도착 : 하차도착(B)

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarudWrkbookId; //야드하차작업예약ID
				}

				//영대차도착이고 적치Bed 비활성화 이면 활성화
				jrParam.setField("V_YD_STK_COL_GP", ydStkColGp); //적치Bed 야드적치열구분(현위치)

				if (!"L".equals(ydStkBedActStatUd)) {
					//현위치 적치Bed 활성화 처리
					jrParam.setField("V_YD_STK_BED_ACT_STAT", "L"); //적치Bed 야드적치Bed활성상태(활성)
					commDao.updStat("StkBedAct", jrParam);
				}

				//하차위치 적치단 재료번호 등록 -> 혹시 정보가 맞지 않을 수도 있으므로 무조건 Update
				rcv2Dao.updC3YDL007("StkLyrStl", jrParam);
			} else if ("1".equals(ydCarProgStat)) {
				ydSchReqGp = "5"; //공대차출발 : 상차출발(1)

				//공대차출발이면 출발위치 적치Bed 비활성화 처리
				jrParam.setField("V_YD_STK_COL_GP"      , ydStkColGp); //야드적치열구분
				jrParam.setField("V_YD_STK_BED_ACT_STAT", "C"       ); //야드적치Bed활성상태(비활성화)
				commDao.updStat("StkBedAct", jrParam);

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarldWrkbookId; //야드상차작업예약ID

					//상차위치 적치Bed 활성화 처리
					jrParam.setField("V_YD_STK_COL_GP"      , ydCarldStopLoc); //야드적치열구분(상차정지위치)
					jrParam.setField("V_YD_STK_BED_ACT_STAT", "L"           ); //적치Bed 야드적치Bed활성상태(활성)
					commDao.updStat("StkBedAct", jrParam);
				
					//설비 Table 야드현재동구분 미리 수정
					jrParam.setField("V_YD_CURR_BAY_GP", ydCarldStopLoc.substring(1, 2));
					commDao.updSlabYd("EqpCurrBay", jrParam);
				}
			} else {
				ydSchReqGp = "6"; //공대차도착 : 상차도착(2) or 상차대기(0)

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarldWrkbookId; //야드상차작업예약ID
				}

				//공대차도착이고 적치Bed 비활성화 이면 활성화
				if (!"L".equals(ydStkBedActStatLd)) {
					//현위치 적치Bed 활성화 처리
					jrParam.setField("V_YD_STK_COL_GP"      , ydStkColGp); //야드적치열구분(현위치)
					jrParam.setField("V_YD_STK_BED_ACT_STAT", "L"       ); //야드적치Bed활성상태(활성)
					commDao.updStat("StkBedAct", jrParam);
				}
			}

			/**********************************************************
			* 5. 야드저장위치제원(YDY1L001, YDY3L001) 전문 조회
			**********************************************************/
			jrParam.setField("V_YD_INFO_SYNC_CD", "4"       ); //야드정보동기화코드(Bed)
			jrParam.setField("V_YD_STK_COL_GP"  , ydStkColGp); //야드적치열구분(현재동)

			//전송Data 조회
			JDTORecord jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L001", jrParam));

			/**********************************************************
			* 6. 크레인스케줄(YDYDJ400) 전송
			**********************************************************/
			//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
			if ("Y".equals(crnSchYn) && !"".equals(ydWbookId)) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId ); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_ST_GP" , "A"       ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("YD_SCH_REQ_GP", ydSchReqGp); //야드스케쥴요청구분
				jrYdMsg.setField("V_MODIFIER"   , modifier  ); //수정자
				
				//크레인스케줄 전문
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getCrnSchMsg(jrYdMsg));
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 대차이동실적(C7YDL007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC7YDL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C7대차이동실적[SlabYdL2RcvSeEJB.rcvC7YDL007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//대차이동실적 처리
			return this.rcvC3YDL007(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : C3설비고장복구실적(C3YDL008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC3YDL008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C3설비고장복구실적[SlabYdL2RcvSeEJB.rcvC3YDL008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//설비고장복구실적 처리
			return this.rcvY1YDL004(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : C3설비운전모드전환(C3YDL009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC3YDL009(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C3설비운전모드전환[SlabYdL2RcvSeEJB.rcvC3YDL009] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//설비운전모드전환 처리
			return this.rcvY1YDL003(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : C7설비운전모드전환(C7YDL009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC7YDL009(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C7설비운전모드전환[SlabYdL2RcvSeEJB.rcvC7YDL009] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//설비운전모드전환 처리
			return this.rcvY1YDL003(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : ROT재료도착통과정보(C3YDL010, C7YDL010) : 처리 내용 없음
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvC3YDL010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "ROT재료도착통과정보[SlabYdL2RcvSeEJB.rcvC3YDL010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			/**********************************************************
			* 1. 연주Machine코드
			*  - 1~5 : #1~5 M/C   : ADRT01, ADRT02, ADRT03, ADRT06, ADRT07
			*  - S   : #1 Scarfer : ADRT04
			*  - T   : #1 2차전단   : ADRT05
			*  - U   : #2 Scarfer : ADRT08
			*  - V   : #2 2차전단   : ADRT09
			*  - W   : #3 2차전단   : ADRT10
			**********************************************************/
			slabUtils.printLog(logId, "처리 내용 없음 : 연주Machine코드[" + slabUtils.trim(rcvMsg.getFieldString("CC_MC_CD")) + "]", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : C7ROT재료도착통과정보(C7YDL010) : 처리 내용 없음
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvC7YDL010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C7ROT재료도착통과정보[SlabYdL2RcvSeEJB.rcvC7YDL010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//ROT재료도착통과정보 처리
			return this.rcvC3YDL010(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : C3HandScarfing작업진행정보(C3YDL011)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvC3YDL011(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C3HandScarfing작업진행정보[SlabYdL2RcvSeEJB.rcvC3YDL011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			}
			
			
			/**********************************************************
			* 2.  연주야드 적치 최적화 핸드스카핑장 입출고 이력 추가
			**********************************************************/
			
			//TC핸드장 입고실적 INSERT
			//1번지 재료번호가 적치정보 있는지 확인
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			String chkStlNo = slabUtils.trim(rcvMsg.getFieldString("SLAB_NO1"));
			jrParam.setField("STL_NO",chkStlNo);
			String queryId = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO";
			JDTORecordSet chkResult = commDao.select(jrParam, queryId, logId, methodNm, "적치정보Check");
			//적치정보가 없을시 입고실적 insert
			if(chkResult.size() <=0){
				jrParam.setField("EQP_CD"			, "SB"); //설비코드
				jrParam.setField("EQP_GP"   		, "TC"  ); //설비구분
				jrParam.setField("MODIFIER" 		, modifier  ); //수정자
				
				commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdEqpIOHistByStlNo", logId, methodNm, "핸드스카핑 입고이력");
			}
			/**********************************************************
			* 3. 적치Bed 재료번호, 재료상태 Update
			**********************************************************/
			String stlNo = ""; //재료번호
			String[][] param = new String[9][6];

			
			//적치Bed Param Set
			for (int ii = 0; ii < 9; ii++) {
				stlNo = slabUtils.trim(rcvMsg.getFieldString("SLAB_NO" + (ii + 1)));	//재료번호
				
				param[ii][0] = modifier;		//수정자
				param[ii][1] = stlNo;			//재료번호
				//야드적치단재료상태
				if ("".equals(stlNo)) {
					param[ii][2] = "E";			//적치가능
				} else {
					param[ii][2] = "C";			//적치중
				}
				param[ii][3] = ydEqpId;			//야드적치열구분(야드설비ID)
				param[ii][4] = "0" + (ii + 1);	//야드적치Bed번호
				param[ii][5] = "001";			//야드적치단번호
			}

			//적치Bed Table Update
			commDao.upsBatch("StkLyrStlNo", param, logId, methodNm);
			 
			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/***************************************************************************
	 * C연주Slab야드L2(Y1), 후판Slab야드L2(Y3), 항만야드L2(E7)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 저장위치제원요구(Y1YDL001, Y3YDL001, YDE7L001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY1YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "저장위치제원요구[SlabYdL2RcvSeEJB.rcvY1YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = slabUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = slabUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = slabUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = slabUtils.trim(rcvMsg.getFieldString("YD_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"  )); //야드적치Bed번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
				throw new Exception("야드동구분(YD_BAY_GP) 없음");
			} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			}

			/**********************************************************
			* 2. 저장위치제원(YDY1L001, YDY3L001, YDE7L001) 전문 조회
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("V_YD_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호

			// 항만야드 기능적용 보완 : 2015.12.15 by LeeJY  -- 전송Data 조회
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			if (ydGp.equals("M")) {
				jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDE7L001", jrParam));
			} else {
				jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L001", jrParam));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3저장위치제원요구(Y3YDL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3저장위치제원요구[SlabYdL2RcvSeEJB.rcvY3YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

          }else{
        	  return null;
          }

			//저장위치제원요구 처리
			return this.rcvY1YDL001(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 저장품제원요구(Y1YDL002, Y3YDL002, YDE7L001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY1YDL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "저장품제원요구[SlabYdL2RcvSeEJB.rcvY1YDL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "구소스테스트진행YYS2RcvSe============="+methodNm, "YYS-");
			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = slabUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = slabUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = slabUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = slabUtils.trim(rcvMsg.getFieldString("YD_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"  )); //야드적치Bed번호
			String stlNo        = slabUtils.trim(rcvMsg.getFieldString("STL_NO"         )); //재료번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			}

			if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
				//저장위치별
				if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
					throw new Exception("야드동구분(YD_BAY_GP) 없음");
				} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
					throw new Exception("야드설비구분(YD_EQP_GP) 없음");
				}
			} else {
				//재료별
				if ("".equals(stlNo)) {
					throw new Exception("재료번호(STL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YDY1L002, YDY3L002) 전문 조회
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("V_YD_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호
			jrParam.setField("V_YD_GP"          , ydGp                                 ); //야드구분
			jrParam.setField("V_STL_NO"         , stlNo                                ); //재료번호

			// 항만야드 기능적용 보완 : 2015.12.15 by LeeJY  -- 전송Data 조회
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			if (ydGp.equals("M")) {
				jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDE7L002", jrParam));
			} else {
				jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L002", jrParam));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3저장품제원요구(Y3YDL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3저장품제원요구[SlabYdL2RcvSeEJB.rcvY3YDL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

        }else{
      	  return null;
        }
			//저장품제원요구 처리
			return this.rcvY1YDL002(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : Crane Reschedule 처리 (Y1YDL003, Y3YDL003, Y1YDL004, Y3YDL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnResch(JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인리스케줄[SlabYdL2RcvSeEJB.trtCrnResch] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			
			
			slabUtils.printLog(logId, methodNm, "S+");
			jrParam.setResultMsg(methodNm);	//Log Method Name

			JDTORecord jrRtn = null;	//크레인작업지시 전문 Return

			//작업예약 야드스케쥴우선순위 수정
			rcv2Dao.updCrnResch("WrkBook", jrParam);

			//크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			rcv2Dao.updCrnResch("CrnSch", jrParam);

			//크레인작업지시 대상 설비 조회
			JDTORecordSet jsWoEqp = rcv2Dao.getCrnResch("WoEqp", jrParam);

			int schCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			String msgId = slabUtils.trim(jrParam.getFieldString("V_MSG_ID")); //수신 전문 I/F ID
			msgId = msgId.substring(0, 2);

			if ("Y1".equals(msgId) || "Y3".equals(msgId) || "E7".equals(msgId)) {  // 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				jrYdMsg.setField("JMS_TC_CD"         , msgId + "YDL007"         ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("V_MODIFIER"        , jrParam.getFieldString("V_MODIFIER")); //수정자
	
				for (int ii = 0; ii < schCnt; ii++) {
					jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "W"                                              ); //야드작업진행상태
					
					//크레인작업지시 전문을 추가
					jrRtn = slabUtils.addSndData(jrRtn, this.rcvY1YDL007(jrYdMsg));
				}
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 설비운전모드전환(Y1YDL003, Y3YDL003, C3YDL009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY1YDL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비운전모드전환[SlabYdL2RcvSeEJB.rcvY1YDL003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = false;	//크레인작업실적응답 전문 전송여부

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId      = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"      )); //야드설비ID
			String ydEqpWrkMode = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE")); //야드설비작업Mode(1:On-Line, 0:Off-Line)
			String modifier     = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"     )); //수정자(Backup Only)
			String brGp         = ""; //고장복구구분
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 전송여부를 Check 
			if (msgId.startsWith("Y1") || msgId.startsWith("Y3") ||	msgId.startsWith("E7") ||	// 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
					(ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
				resYn = true;
			}

			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydEqpWrkMode); //야드작업진행상태(야드설비작업Mode)
			resMsg.setField("YD_L2_WR_GP"     , "M"         ); //야드L2실적구분(운전모드변경)
			resMsg.setField("YD_L3_HD_RS_CD"  , "EM99"      ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비운전모드전환 수신처리"); //야드L3MESSAGE(Error)
			

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "EM01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "EM02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpWrkMode)) {
				ydL3HdRsCd = "EM03";
				ydL3Msg    = "오류:설비작업Mode 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			logger.println(LogLevel.INFO, "-----> 수신 항목 값 Check 완료");

			if ("1".equals(ydEqpWrkMode)) {
				brGp = "R";	//복구
			} else {
				brGp = "B";	//고장
				ydEqpWrkMode = "0";
			}

			/**********************************************************
			* 2. 설비작업Mode Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_YD_EQP_ID"      , ydEqpId     ); //야드설비ID
			jrParam.setField("V_YD_EQP_WRK_MODE", ydEqpWrkMode); //야드설비작업Mode
			jrParam.setField("V_BR_GP"          , brGp        ); //고장복구구분
			jrParam.setField("V_MODIFIER"       , modifier    ); //수정자

			JDTORecordSet jsChk = commDao.getStat("Eqp", jrParam);

			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "EM11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpWrkMode.equals(slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE")))) {
				//설비 Table 설비작업Mode Check
				ydL3HdRsCd = "EM12";
				ydL3Msg = "오류:현재 설비작업Mode와 동일";
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			logger.println(LogLevel.INFO, "-----> 설비작업Mode Check 완료");

			/**********************************************************
			* 3. 설비의 야드설비작업Mode 수정
			**********************************************************/
			commDao.updStat("EqpMode", jrParam);

			//크레인정보 Flex Server로 전송
			String ydGp = ydEqpId.substring(0, 1);
			if (!ydGp.equals("M")) { slabComm.sndToFlexData(resMsg); } // 항만야드 미적용 : 2015.12.15 by LeeJY

			logger.println(LogLevel.INFO, "-----> 설비의 야드설비작업Mode 수정 완료 (야드구분 : "+ydGp+" )");

			/**********************************************************
			* 4. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
				//크레인 리스케줄
				jrParam.setField("V_MSG_ID", msgId); //수신 전문 I/F ID
				jrRtn = this.trtCrnResch(jrParam);
			}
			
			/**********************************************************
			* 5. 크레인작업실적응답 전문 전송(YDY1L005, YDE7L005)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				logger.println(LogLevel.INFO, "-----> 크레인작업실적응답 전문 전송  준비 (야드구분 : "+ydGp+" )");
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY1L005(resMsg));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YdCommEJB", this);
					logger.println(LogLevel.INFO, "-----> 크레인작업실적응답 전문 전송 준비");
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY1L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3설비운전모드전환(Y3YDL003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3설비운전모드전환[SlabYdL2RcvSeEJB.rcvY3YDL003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			//설비운전모드전환 처리
			return this.rcvY1YDL003(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 설비고장복구실적(Y1YDL004, Y3YDL004, C3YDL008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY1YDL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비고장복구실적[SlabYdL2RcvSeEJB.rcvY1YDL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = false;	//크레인작업실적응답 전문 전송여부

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId           = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //야드설비ID
			String ydEqpStat       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); //야드설비상태(B:고장, N:정상, R:복구 등)
			String ydEqpPauseCode  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); //야드설비휴지코드
			String ydEqpTrblRcvrDt = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); //야드설비고장복구일시
			String modifier        = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"         )); //수정자(Backup Only)
			String brGp            = ""; //고장복구구분
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 전송여부를 Check
			if (msgId.startsWith("Y1") || msgId.startsWith("Y3") ||	(ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
				resYn = true;
			}

			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId       ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydEqpStat     ); //야드작업진행상태(야드설비상태)
			resMsg.setField("YD_SCH_CD"       , ydEqpPauseCode); //야드스케쥴코드(야드설비휴지코드)
			resMsg.setField("YD_L2_WR_GP"     , "R"           ); //야드L2실적구분(고장복구실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비고장복구실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:야드설비상태 없음";
			} else if ("".equals(ydEqpPauseCode) && "B".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:설비휴지코드 없음";
			} else if ("".equals(ydEqpTrblRcvrDt) && ("B".equals(ydEqpStat) || "N".equals(ydEqpStat) || "R".equals(ydEqpStat))) {
				ydL3HdRsCd = "BR05";
				ydL3Msg    = "오류:고장복구일시 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			if ("B".equals(ydEqpStat)) {
				brGp = "B"; //고장
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "B000";
				}
			} else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) {
				brGp = "R"; //복구
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "R000";
				}

				if ("CR".equals(ydEqpId.substring(2, 4))) {
					ydEqpStat = "W";
				} else {
					ydEqpStat = "N";
				}
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_YD_EQP_ID"          , ydEqpId        ); //야드설비ID
			jrParam.setField("V_YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); //야드설비휴지코드
			jrParam.setField("V_YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); //야드설비휴지발생일시
			jrParam.setField("V_YD_EQP_STAT"        , ydEqpStat      ); //야드설비상태
			jrParam.setField("V_BR_GP"              , brGp           ); //고장복구구분
			jrParam.setField("V_MODIFIER"           , modifier       ); //수정자

			JDTORecordSet jsChk = commDao.getStat("Eqp", jrParam);

			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "BR11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpStat.equals(slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT")))) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "BR12";
				ydL3Msg = "오류:현재 설비상태[" + ydEqpStat + "]와 동일";
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			commDao.updStat("Eqp", jrParam);

			//크레인정보 Flex Server로 전송
			slabComm.sndToFlexData(resMsg);

			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(brGp)) {
				rcv2Dao.updY1YDL004("EqpPause", jrParam);
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
				//해당 크레인 스케줄 상태가 권상지시(1)일 경우 명령선택대기(W)로 변경
				if ("B".equals(ydEqpStat)) {
					rcv2Dao.updY1YDL004("CrnSchW", jrParam);
				}
				//크레인 리스케줄
				if(!ydEqpId.equals("DBCRB2")) {
					jrParam.setField("V_MSG_ID", msgId); //수신 전문 I/F ID
					jrRtn = this.trtCrnResch(jrParam);
				}
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YDY1L005)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY1L005(resMsg));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YdCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY1L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3설비고장복구실적(Y3YDL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3설비고장복구실적[SlabYdL2RcvSeEJB.rcvY3YDL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			//설비고장복구실적 처리
			return this.rcvY1YDL004(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인현재위치(Y1YDL005, Y3YDL005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY1YDL005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인현재위치[SlabYdL2RcvSeEJB.rcvY1YDL005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			String msgId = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			methodNm = msgId.substring(0, 2) + methodNm;

			String sModifier = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String ydEqpId    = ""; //야드설비ID
			String ydCrnXaxis = ""; //야드크레인X축
			String ydCrnYaxis = ""; //야드크레인Y축
			String ydGp       = "A"; //야드구분

			if (msgId.startsWith("Y3")) {
				ydGp = "D"; //후판슬라브야드
			}

			HashMap hmData = new HashMap(); //전송할 Data

			hmData.put("MSG_GP", "C" ); //크레인위치
			hmData.put("YD_GP" , ydGp); //야드구분

			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			
			for (int ii = 1; ii <= 20; ii++) {
				ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    + ii));
				ydCrnXaxis = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS" + ii));
				ydCrnYaxis = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS" + ii));

				if (ydEqpId.length() != 6 || "".equals(ydCrnXaxis) || "".equals(ydCrnYaxis)) {
					continue;
				}

				hmData.put("YD_EQP_ID" + ii, ydEqpId                );
				hmData.put("YD_POS_X"  + ii, new Integer(ydCrnXaxis));
				hmData.put("YD_POS_Y"  + ii, new Integer(ydCrnYaxis));
				
				
				jrParam.setField("YD_EQP_ID"     	, ydEqpId );  //YD_EQP_ID 
				jrParam.setField("CRN_WRK_PROC_STAT", "00" );  //CRN_WRK_PROC_STAT
				jrParam.setField("CURR_XAXIS"     	, ydCrnXaxis );  //CURR_XAXIS
				jrParam.setField("FROM_XAXIS"     	, "00000000" );  //FROM_XAXIS 
				jrParam.setField("TO_XAXIS"     	, "00000000" );  //TO_XAXIS
				jrParam.setField("MODIFIER"     	, "Y1YDL005" );  //MODIFIER
				
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCrnLoc", logId, methodNm, "크레인현재위치[SlabYdL2RcvSeEJB.rcvY1YDL005]");
			}

			slabComm.sndToFlex("yd_monitor" + ydGp, hmData);

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3크레인현재위치(Y3YDL005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY3YDL005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3크레인현재위치[SlabYdL2RcvSeEJB.rcvY3YDL005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			//크레인현재위치 처리
			return this.rcvY1YDL005(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인작업계획요구(Y1YDL006, Y3YDL006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY1YDL006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업계획요구[SlabYdL2RcvSeEJB.rcvY1YDL006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = slabUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ptopPlntGp   = ""; //조업공장구분
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydInfoSyncCd)) {
				throw new Exception("야드정보동기화코드(YD_INFO_SYNC_CD) 없음");
			}
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	    

			if ("Y1YDL006".equals(msgId)) {
				msgId = "YDY1L003"; //연주Slab야드L2 송신 전문ID
				ptopPlntGp = "HC";
			} else {
				 if("Y".equals(APPLY_YN34)){
					 msgId = "YDY3L003"; //후판Slab야드L2 송신 전문ID
						if ("P".equals(ydInfoSyncCd)) {
							ptopPlntGp = "PA";	//1후판
						} else if ("Q".equals(ydInfoSyncCd)) {
							ptopPlntGp = "PB";	//2후판
						} else {
							throw new Exception("야드정보동기화코드(YD_INFO_SYNC_CD) 이상 [" + ydInfoSyncCd + "]");
						}
	             }
				
			}

			/**********************************************************
			* 2. 크레인작업계획(YDY1L003, YDY3L003) 전문 조회
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("V_PTOP_PLNT_GP", ptopPlntGp); //조업공장구분

			//전송Data 조회
			JDTORecord jrRtn = slabUtils.addSndData(commDao.getMsgL2(msgId, jrParam));

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3크레인작업계획요구(Y3YDL006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY3YDL006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3크레인작업계획요구[SlabYdL2RcvSeEJB.rcvY3YDL006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			//크레인작업계획요구 처리
			return this.rcvY1YDL006(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인작업지시요구(Y1YDL007, Y3YDL007,YDYDJ440)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY1YDL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업지시요구[SlabYdL2RcvSeEJB.rcvY1YDL007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"      )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			slabUtils.printLog(logId, "크레인작업지시요구 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
			
			if("X".equals(ydWrkProgStat)){
				/*
				 * 2016.05.23 후판장척재슬라브 R/T베드 장입요구
				 */
				slabUtils.printLog(logId, "후판장척재슬라브 R/T베드 장입요구 [ " + ydEqpId + " : " + ydWrkProgStat  + " ]", "SL");
				
				/**********************************************************
				* 2. Take-In 재료번호 Check
				**********************************************************/
				//조회 및 등록용
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name

				jrParam.setField("V_YD_STK_COL_GP", "DART01"   ); //야드적치열구분
				jrParam.setField("V_YD_STK_BED_NO", "05"); 		  //야드적치Bed번호
				jrParam.setField("V_YD_STK_LYR_NO", "001"); 	  //야드적치단번호

				JDTORecordSet jsChk = rcv2Dao.getC3YDL005("Bed", jrParam);
				
				String carryInReqYn = "";
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					//Carry-In요구 결정
					String tiSupYn    = slabUtils.trim(jrChk.getFieldString("TI_SUP_YN"    )); //TakeIn공Bed 자동보급요구여부

					if ("Y".equals(tiSupYn)){
						carryInReqYn = "Y"; //Carry-In요구구분
					}
				} else {
					throw new Exception("적치단[DART01/05/001] 정보가 없습니다.");
				}
				
				/**********************************************************
				* 2. 적치Bed 재료번호, 재료상태  Update
				**********************************************************/
				jrParam.setField("V_MODIFIER"           , modifier); //수정자
				jrParam.setField("V_STL_NO"             , ""      ); //재료번호
				jrParam.setField("V_YD_STK_LYR_MTL_STAT", "E"     ); //야드적치단재료상태(적치가능)

				//적치Bed Table Update
				commDao.updSlabYd("StkLyrStlNo", jrParam);
				
				JDTORecord jrRtn = null;
				//Carry-In요구구분이 'Y'이면
				logger.println(LogLevel.INFO, "-----> Carry-In요구구분 : " + carryInReqYn);
				if ("Y".equals(carryInReqYn)) {
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

					//설비보급요구 전문
					logger.println(LogLevel.INFO, "-----> 설비보급요구 처리 (YDYDJ420) ");
					jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ420"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , "DART01"                 ); //야드설비ID
					jrYdMsg.setField("YD_STK_BED_NO"     , "05"              		); //야드적치Bed번호
					jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
					jrYdMsg.setField("V_MODIFIER"        , modifier                 ); //수정자
					
					//전송할 전문에 추가
					jrRtn = slabUtils.addSndData(jrYdMsg);
				}
				
				logger.println(LogLevel.INFO, "-----> 설비보급요구 처리중 (YDYDJ420) ");
				
				return jrRtn;
			}
			
			JDTORecord jrRtn  = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "J"          ); //야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("V_YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("V_MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = slabComm.chkEqpStat(jrParam);

			ydL3HdRsCd = slabUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = slabUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			logger.println(LogLevel.INFO, "-----> 설비상태 Check 완료");

			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			JDTORecordSet jsSch = rcv2Dao.getY1YDL007("CS", jrParam);

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = slabUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = slabUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

				jrParam.setField("V_YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					logger.println(LogLevel.INFO, "-----> 권상,권하 재지시 처리");
					jrParam.setField("V_MSG_GP", "U"); //전문구분 - 재지시
				} else {
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/
					logger.println(LogLevel.INFO, "-----> 대기 : 다음 작업지시 처리");
					jrParam.setField("V_MSG_GP", "I"); //전문구분 - 신규

					//설비의 야드설비상태 수정
					jrParam.setField("V_YD_EQP_STAT", "1"); //권상작업지시

	        		commDao.updStat("Eqp", jrParam);

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("V_YD_WRK_PROG_STAT", "1"); //권상지시

					commDao.updStat("CrnSchWrkProg", jrParam);

					//크레인스케줄 권상지시단 수정
					rcv2Dao.updY1YDL007("CS", jrParam);
					
					
					//연주슬라브야드 전광판 전문송신처리 4번 혹은 6번포인트에 지시대기가 아닌 크레인스케줄이 있다면, 전광판전문 송신처리
					String ydUpWoLoc = slabUtils.trim(jsSch.getRecord(0).getFieldString("YD_UP_WO_LOC"   ));
					String ydDnWoLoc = slabUtils.trim(jsSch.getRecord(0).getFieldString("YD_DN_WO_LOC"   ));
					
					logger.println(LogLevel.INFO, "-----> 권상지시["+ydUpWoLoc+"] 권하지시["+ydDnWoLoc+"]");
					
					//AAPT04
					if((ydUpWoLoc.length()>=6 && "PT04".equals(ydUpWoLoc.substring(2,6)) )
				     ||(ydDnWoLoc.length()>=6 && "PT04".equals(ydDnWoLoc.substring(2,6)) )
				      ){
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDT1L001", jrParam));
					}
					else if ((ydUpWoLoc.length()>=6 && "PT06".equals(ydUpWoLoc.substring(2,6)) )
						     ||(ydDnWoLoc.length()>=6 && "PT06".equals(ydDnWoLoc.substring(2,6)) )
				      ){
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDT2L001", jrParam));
					}
				}
				
				//크레인작업지시(YDY1L004, YDY3L004, YDE7L004) 전문 조회
				// 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				String ydGp = ydEqpId.substring(0, 1);
				if (ydGp.equals("M")) {
					logger.println(LogLevel.INFO, "-----> 항만야드 크레인작업지시 추출");
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE7L004", jrParam));
				} else {
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY1L004", jrParam));
					//크레인정보 Flex Server로 전송
					slabComm.sndToFlexData(resMsg);
				}	

				slabUtils.printLog(logId, "크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "L");
				
				 
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY1L005(resMsg));

					slabUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("V_YD_EQP_STAT", "W"); //대기(Wait)

	        		commDao.updStat("Eqp", jrParam);

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
	        		JDTORecordSet jsWrkBook = rcv2Dao.getY1YDL007("WB", jrParam);
					//logger.println(LogLevel.INFO, "-----> 대기상태[W], 권하완료[4] 지시요구 : 작업예약 추출");

					//작업예약이 있으면 크레인스케줄 없으면 슬라브자동준비작업요구 호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("V_MODIFIER"   , modifier); //수정자

						logger.println(LogLevel.INFO, "-----> 대기상태[W], 권하완료[4] 지시요구 : 크레인스케줄 호출");
						jrRtn = slabComm.getCrnSchMsg(jrYdMsg);
						resMsg.setField("YD_L3_HD_RS_CD", "8888" ); //야드L3처리결과코드
						
						//2023.11.10 연주 김충만 계장 요청 --//REQ202311504859
						//지정크레인 작업종료 시 해당 스케줄의 주크레인 작업도 기동될 수 있도록 수정
						//REASON: A2 주크레인 A3 지정 A2 작업 종료시, 0순위 A3 지정이 기동되어, A2는 가만히 있음
						
						String APPLY_YN01 = commDao.slabApplyYn("APPLY_YN01");
						
						if(ydEqpId.startsWith("A") && "Y".equals(APPLY_YN01)){
							String ydWrkPlanCrn = slabUtils.trim(jsWrkBook.getRecord(0).getFieldString("YD_WRK_PLAN_CRN"));
							if(!"".equals(ydWrkPlanCrn) && (ydWrkPlanCrn != ydEqpId)){
								slabUtils.printLog(logId, "작업크레인 ["+ydEqpId+"] 작업지시요구에 대해 지정크레인["
							+ydWrkPlanCrn+"] 작업 기동하므로, 작업크레인이 작업할 작업예약 하나 더 기동", "SL");
								
								jrParam = JDTORecordFactory.getInstance().create();
								jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
								
								JDTORecordSet jsWrkBook2 = commDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL007WB_assignEqp", logId, methodNm, "작업예약 조회");
								if(jsWrkBook2.size() >0){
									String ydWbookId2 = jsWrkBook2.getRecord(0).getFieldString("YD_WBOOK_ID");
									String ydSchCd2   = jsWrkBook2.getRecord(0).getFieldString("YD_SCH_CD");
									
									jrYdMsg = JDTORecordFactory.getInstance().create();
									jrYdMsg.setResultCode(logId);	//Log ID
									jrYdMsg.setResultMsg(methodNm);	//Log Method Name
									
									jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId2); //야드작업예약ID
									jrYdMsg.setField("YD_SCH_CD"    , ydSchCd2); //야드스케쥴코드
									jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
									jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
									jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
									jrYdMsg.setField("V_MODIFIER"   , modifier); //수정자

									slabUtils.printLog(logId, "작업크레인 ["+ydEqpId+"]이 작업할 작업예약 ["+ydWbookId2+"] 기동", "SL");
									jrRtn = slabUtils.addSndData(jrRtn, slabComm.getCrnSchMsg(jrYdMsg));
								}
								
								
							}
						}
					} else {
						/**********************************************************
						* 장입준비작업요구는 사용하지 않음
						***********************************************************
						if ("AACRA1".equals(ydEqpId) || "AACRA2".equals(ydEqpId) || "DACRA1".equals(ydEqpId) || "DBCRB1".equals(ydEqpId)) {
							ydL3Msg = "장입준비작업요구 호출";
	
							jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ430"); //JMSTC코드
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
							jrYdMsg.setField("YD_EQP_ID"         , ydEqpId   ); //야드설비ID
							jrYdMsg.setField("YD_SCH_ST_GP"      , "A"       ); //야드스케쥴기동구분(Auto)
							jrYdMsg.setField("V_MODIFIER"        , modifier  ); //수정자
	
							jrRtn = slabUtils.addSndData(jrYdMsg);
						} else {
							ydL3Msg = "다음 크레인작업지시 없음";
						}
						**********************************************************/
						ydL3Msg = "다음 크레인작업지시 없음";
						resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					}

					
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY1L005(resMsg));

					slabUtils.printLog(logId, "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				//chito : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YdCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY1L005(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3크레인작업지시요구(Y3YDL007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3크레인작업지시요구[SlabYdL2RcvSeEJB.rcvY3YDL007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			//크레인작업지시요구 처리
			return this.rcvY1YDL007(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인권상실적(Y1YDL008, Y3YDL008, E9YDL008)  
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY1YDL008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인권상실적[SlabYdL2RcvSeEJB.rcvY1YDL008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydUpWrLoc     = slabUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"    )); //야드권상실적위치
			String ydUpWrLayer   = slabUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LAYER"  )); //야드권상실적단
			String ydCrnXaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"      )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydDnWoLoc     = ""; //야드권하지시위치
			String ydDnWoLayer     = ""; //야드권하지시단
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			//크레인작업실적응답 전문 전송여부를 Check -> Backup시에도 응답전문 전송
			//if (!msgId.equals(modifier) || msgId.startsWith("YDYD")) {
			//	resYn = false;
			//}

			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "UP01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "UP02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "UP03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydUpWrLoc)) {
				ydL3HdRsCd = "UP04";
				ydL3Msg    = "오류:권상실적위치 없음";
			} else if ("".equals(ydUpWrLayer)) {
				ydL3HdRsCd = "UP05";
				ydL3Msg    = "오류:권상실적단 없음";
			}

			logger.println(LogLevel.INFO, "-----> 수신 항목 값 Check 완료");

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("V_MODIFIER"     , modifier  ); //수정자

			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.getStat("CrnSch", jrParam);

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = slabUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydDnWoLoc       = slabUtils.trim(jrChk.getFieldString("YD_DN_WO_LOC"    )); //야드권하지시위치
				ydDnWoLayer     = slabUtils.trim(jrChk.getFieldString("YD_DN_WO_LAYER"    )); //야드권하지시단
				String tmpStat  = slabUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = slabUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				if (!"1".equals(tmpStat) && !"W".equals(tmpStat)) {
					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "UP13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			/**********************************************************
			* 2-1. 권상단 상단에 스케쥴없이 재료가 적치되어 있는 경우( 권상처리 에러) Check
			**********************************************************/
			jrParam.setField("V_YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호
			jrParam.setField("V_YD_UP_WR_LAYER"  , ydUpWrLayer ); //야드권상실적단
			
			JDTORecordSet jsChk2 = commDao.getStat("CrnDan", jrParam);
			if (jsChk2.size() > 0) {
				//상단에 스케쥴 없는 재료  존재유무 Check
				ydL3HdRsCd = "UP14";
				//ydL3Msg = "오류:스케쥴 없이 더미재가 상단에 존재";
				ydL3Msg = "오류:("+ydUpWrLoc+")슬라브가 상단에 존재";
				
				
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY1L005(resMsg));
			}
			//********************************************************/
			
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				//throw new Exception(ydL3Msg);
				logger.println(LogLevel.INFO, "["+ methodNm +"] "+ ydL3Msg);
				return jrRtn;
			}
			
			logger.println(LogLevel.INFO, "-----> 크레인스케쥴ID Check 완료");
			
			/**********************************************************
			* 3. 전송 전문 조회
			* 3.1 생산통제 장입진행실적(C열연:YDCTJ033, 후판:YDCTJ031)
			* 3.2 C연주정정L2 Carry-Out완료(YDC3L003, YDC7L003)
			* 3.3 삭제 전문
			*   - C연주정정L2 OHCTake-Out완료(YDC3L002, YDC7L002)
			**********************************************************/
			String currDt = slabUtils.getDateTime14(); //현재시각

			jrParam.setField("V_YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			jrParam.setField("V_WR_DT"      , currDt   ); //실적일시

			//생산통제 장입진행실적(권상권하)
			jrParam.setField("V_UP_DN_GP"     , "U"                      ); //권상권하구분(권상)
			jrParam.setField("V_YD_STK_COL_GP", ydDnWoLoc.substring(0, 6)); //야드적치열구분
			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDCTJ033UD", jrParam));

			jrParam.setField("V_YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호

			
			if ("A".equals(ydUpWrLoc.substring(0, 1))) {
				//C연주정정L2 Carry-Out완료
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L003", jrParam));
				
				//C연주그라인딩L2 Carry-Out완료
				if ("DP04".equals(ydUpWrLoc.substring(2, 6))) {
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC8L001", jrParam)); 
				}
			} else if (ydUpWrLoc.startsWith("MBPU")) {  
				/** 항만야드 기능적용 보완 : 2015.12.15 by LeeJY 
				 *  -.정정라인에서 정정작업 후 Take Out을 완료하여 적치된  Pick-Up Bed의 재료를 야드이적 완료 시*/
				logger.println(LogLevel.INFO, "-----> Pick-Up Bed Carry-Out 완료 전문추출");
				//항만정정L2 Carry-Out완료
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE9L003", jrParam));
			} else if (ydUpWrLoc.startsWith("MADP")) {
				/** 항만야드 기능적용 보완 : 2019.05.16 **/
				logger.println(LogLevel.INFO, "-----> A동 Depiler Bed Carry-Out 완료 전문추출");
				//항만정정L2 Carry-Out완료
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE9L003", jrParam));
			} else if (ydUpWrLoc.startsWith("MART")) {
				/** 항만야드 기능적용 보완 : 2019.05.16 **/
				logger.println(LogLevel.INFO, "-----> A동 Entry Carry-Out 완료 전문추출");
				//항만정정L2 Carry-Out완료
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE9L003", jrParam));
			} 

			/**********************************************************
			* 4. 권상실적위치가 대차(하차)
			* 4.1 대차 하차 정보 등록
			*   - 대차이송재료 삭제
			*   - C연주정정L2 대차작업실적(YDC3L007, YDC7L007) 전송 : 후판Slab야드L2 전문 없음
			*   - 대차스케줄 삭제 : 하차완료 시
			* - 공대차출발지시 : C연주정정L2, 후판Slab야드L2 대차출발지시(YDC3L006, YDC7L006, YDY3L006) 
			**********************************************************/
			if ("TC".equals(ydUpWrLoc.substring(2, 4))) {
				//대차하차스케쥴 조회
				jsChk = rcv2Dao.getY1YDL008("TcarSchUd", jrParam);
				
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
					String ydTcarSchId  = slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID" )); //야드대차스케쥴ID
					String tcarUdCmplYn = slabUtils.trim(jrChk.getFieldString("TCAR_UD_CMPL_YN")); //대차하차완료여부

					if ("Y".equals(tcarUdCmplYn)) {
						//하차완료이면 대차스케줄 삭제 후 공대차출발지시 처리
						if (resYn) {
							resMsg.setField("YD_L3_HD_RS_CD", "UP21"                    ); //야드L3처리결과코드
							resMsg.setField("YD_L3_MSG"     , "오류:대차 하차완료처리 실패"); //야드L3MESSAGE
						}

						//하차완료(공대차출발지시) 처리
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name

						jrYdMsg.setField("YD_EQP_ID"     , ydUpWrLoc.substring(0, 1) + "X" + ydUpWrLoc.substring(2, 6)); //야드설비ID(대차)
						jrYdMsg.setField("YD_TCAR_SCH_ID", ydTcarSchId); //야드대차스케쥴ID
						jrYdMsg.setField("V_MODIFIER"    , modifier   ); //수정자

						jrRtn = slabUtils.addSndData(jrRtn, slabComm.trtTcarSchUdCmpl(jrYdMsg));
					} else {
						//하차완료가 아니면 크레인 권상재료 만큼 대차이송재료 삭제 후 대차작업실적 전송
						jrParam.setField("V_YD_TCAR_SCH_ID"   , ydTcarSchId              ); //야드대차스케쥴ID
						jrParam.setField("V_YD_CARUD_STOP_LOC", ydUpWrLoc.substring(0, 6)); //야드하차정지위치
						jrParam.setField("V_YD_CARUD_WRK_CRN" , ydEqpId                  ); //야드하차작업크레인

						//대차스케줄(하차) 수정
						rcv2Dao.updY1YDL008("TcarSchUd", jrParam);

						//대차이송재료 삭제
						rcv2Dao.updY1YDL008("TcarMtlDel", jrParam);
						
						//C연주정정L2 대차작업실적
						if ("A".equals(ydUpWrLoc.substring(0, 1))) {
							jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L007", jrParam));
						}
					}
				}
			}

			/**********************************************************
			* 5. 권상실적위치가 차량(하차)
			*    차량스케줄 야드차량진행상태가 하차도착(B) 또는 하차검수(C) 이고
			*  	   야드차량사용구분이 구내운송(L) 이면
			* 5.1 구내운송 소재차량하차개시(YDTSJ009) 전송
			* 5.2 차량이송재료 삭제
			* 5.3 차량스케줄 수정
			*   - 야드차량진행상태(D:하차개시), 야드설비작업매수 등 수정
			**********************************************************/
			if("PT".equals(ydUpWrLoc.substring(2, 4))) {
				logger.println(LogLevel.INFO, "-----> 권상실적위치가 차량(하차) 처리준비");
				//차량스케줄  야드하차작업예약ID 등록 (차량스케줄에 야드하차작업예약ID 없을 경우)
				rcv2Dao.updY1YDL008("CarSchUdWbId", jrParam);
				
				//구내운송 소재차량하차개시
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ009", jrParam));

				//차량이송재료 삭제
				rcv2Dao.updY1YDL008("CarMtlDel", jrParam);
				//하차 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
				rcv2Dao.updY1YDL008("CarSchUd", jrParam);
				
				//차량 회송처리기능. 권상시마다 update 
				jrParam.setField("V_YD_CRN_SCH_ID"  , ydCrnSchId );
				rcv2Dao.updY1YDL008("RethtHist", jrParam);
				logger.println(LogLevel.INFO, "-----> 권상실적위치가 차량(하차) 처리완료");
			}
			
			/**********************************************************
			* 6. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 6.1 구내운송 소재차량상차개시(YDTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			* 6.2 출하관리 외판슬라브출하상차개시(YDDMR009) 전송
			*   - 야드차량사용구분이 출하차량(G)
			* 6.3 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("PT".equals(ydDnWoLoc.substring(2, 4))) {
				//차량하차스케줄 정보 조회
				jsChk = rcv2Dao.getY1YDL008("CarSchLd", jrParam);
	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
				
					//상차개시 전문
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					if("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
						logger.println(LogLevel.INFO, "-----> 구내운송 소재차량상차개시 전문추출");
						//구내운송 소재차량상차개시
						jrYdMsg.setField("JMS_TC_CD"         , "YDTSJ007"); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
						jrYdMsg.setField("TRN_EQP_CD"        , slabUtils.trim(jrChk.getFieldString("TRN_EQP_CD"    ))); //운송장비코드
						jrYdMsg.setField("SPOS_WLOC_CD"      , slabUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"  ))); //발지개소코드
						jrYdMsg.setField("SPOS_YD_PNT_CD"    , slabUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))); //발지야드포인트코드
						jrYdMsg.setField("ARR_WLOC_CD"       , slabUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"   ))); //착지개소코드
						jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    ); //운송작업시작일시
					} else {
						//출하관리 외판슬라브출하상차개시
						jrYdMsg.setField("JMS_TC_CD"         , "YDDMR009"); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
						jrYdMsg.setField("TC_CODE"           , "YDDMR009"); //IF구분코드
						jrYdMsg.setField("TC_CREATE_DDTT"    , currDt    ); //TC생성일시
						jrYdMsg.setField("CARD_NO"           , slabUtils.trim(jrChk.getFieldString("CARD_NO"        ))); //카드번호
						jrYdMsg.setField("CAR_NO"            , slabUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
						jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
						jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
						jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
						jrYdMsg.setField("TRANS_ORD_DATE"    , slabUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
						jrYdMsg.setField("TRANS_ORD_SEQNO"   , slabUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
					}
					
					//전송할 전문에 추가
					jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);

					jrParam.setField("V_YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID
					jrParam.setField("V_ARR_WLOC_CD"  , slabUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"  ))); //착지개소코드

					//상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
					rcv2Dao.updY1YDL008("CarSchLd", jrParam);
				}
			}

			/**********************************************************
			* 7. 설비, 크레인스케쥴, 적치단, 적치Bed 수정
			* 7.1 설비 야드설비상태(권상중) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 등록
			*   - 권상위치 재료정보 삭제
			* 7.3 적치Bed 야드적치Bed입출고상태(완산Bed->입출고가능) 수정
			* 7.4 크레인스케쥴 권상실적 수정
			**********************************************************/
			//야드권상작업수행구분
			String ydUpWrkActGp = ydEqpWrkMode;

			if ("0".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "B"; //Backup
			}

			//설비
			jrParam.setField("V_YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			jrParam.setField("V_YD_EQP_STAT"     , "2"         ); //야드설비상태(권상중)
			//크레인스케쥴
			jrParam.setField("V_YD_UP_CMPL_DT"   , currDt      ); //야드권상완료일시
			jrParam.setField("V_YD_UP_WR_LOC"    , ydUpWrLoc   ); //야드권상실적위치
			jrParam.setField("V_YD_UP_WR_LAYER"  , ydUpWrLayer ); //야드권상실적단
			jrParam.setField("V_YD_UP_WRK_ACT_GP", ydUpWrkActGp); //야드권상작업수행구분
			jrParam.setField("V_YD_UP_WR_XAXIS"  , ydCrnXaxis  ); //야드권상실적X축
			jrParam.setField("V_YD_UP_WR_YAXIS"  , ydCrnYaxis  ); //야드권상실적Y축
			jrParam.setField("V_YD_UP_WR_ZAXIS"  , ydCrnZaxis  ); //야드권상실적Z축

			//설비(야드설비상태) 수정
			commDao.updStat("Eqp", jrParam);
			//적치단(크레인 및 권상위치) 수정
			rcv2Dao.updY1YDL008("StkLyr", jrParam);
			//적치Bed(완산Bed->입출고가능) 수정
			rcv2Dao.updY1YDL008("StkBedF", jrParam);
			//크레인스케쥴 수정
			rcv2Dao.updY1YDL008("CrnSch", jrParam);

			logger.println(LogLevel.INFO, "-----> 설비, 크레인스케쥴, 적치단, 적치Bed 수정 처리완료");

			/**********************************************************
			* 8. 재열재인출이면 Bed재료 Shift후 Carry-Out요구
			*  - 적치중인 재료만 있을경우(작업예약,권상대기,권하대기 없음)
			**********************************************************/
			if ("PS".equals(ydUpWrLoc.substring(2, 4))) {
				String carryOutYn = "N"; //Carry-Out가능여부
				String ydStkColGp = ydUpWrLoc.substring(0, 6); //야드적치열구분

				//Bed상태 조회
				jrParam.setField("V_STL_NO"       , ""        ); //재료번호(적치시)
				jrParam.setField("V_YD_STK_COL_GP", ydStkColGp); //야드적치열구분

				jsChk = commDao.getStat("RehtBed", jrParam);
				
				if (jsChk.size() > 0) {
					carryOutYn = slabUtils.trim(jsChk.getRecord(0).getFieldString("CARRY_OUT_YN")); //Carry-Out가능여부
				}

				if ("Y".equals(carryOutYn)) {
					//적치단 재료 Shift(권상 했으니 공Bed가 발생했을 것이므로)
					commDao.updSlabYd("StkLyrShift", jrParam);
					
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	
					//설비인출요구
					jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ410"); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydStkColGp); //야드설비ID
					jrYdMsg.setField("YD_STK_BED_NO"     , "01"      ); //야드적치Bed번호
					jrYdMsg.setField("YD_SCH_ST_GP"      , "A"       ); //야드스케쥴기동구분(Auto)
					jrYdMsg.setField("V_MODIFIER"        , modifier  ); //수정자
					
					//전송할 전문에 추가
					jrRtn = slabUtils.addSndData(jrYdMsg);
					
					/**********************************************************
					* 8-1. SHIFT 이후 저장품제원 전문을 전송(C연주)
					**********************************************************/
					
					jrParam.setField("V_YD_STK_COL_GP"   , ydStkColGp); //재료번호
					jrParam.setField("V_YD_INFO_SYNC_CD" , "4"); //야드정보동기화코드(베드단위)
					//저장품제원(YDY1L002) 전송Data 조회
					jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY1L002", jrParam));
				}
			}
			
			/**********************************************************
			 * 9.보온뱅크 추출실적처리
			 * 9.1 보온뱅크에 권상 시점 부터 실적 처리를 시작한다.
			 * 
			 **********************************************************/
			
			//보온뱅크 권상 작업
			if ( "BK".equals(ydUpWrLoc.substring(2, 4)) && !"BK".equals(ydDnWoLoc.substring(2, 4))) {
				
				jsChk = rcv2Dao.getY1YDL009("BankStlList", jrParam);
				
				for (int ii = 0; ii < jsChk.size(); ii++) {
					jrChk = jsChk.getRecord(ii);
					
					slabUtils.printLog(logId, "보온뱅크 실적 등록 " + slabUtils.trim(jrChk.getFieldString("STL_NO")), "SL");
 
					jrParam.setField("V_MSLAB_NO"            , slabUtils.trim(jrChk.getFieldString("STL_NO"            ))); //재료번호

					//주편(보온뱅크추출시간)
					rcv2Dao.updY1YDL009("updateMslabCommonSubEndInfo", jrParam); 					
					 
				}
			}
			
			/**********************************************************
			* 9. Flex Server 및 크레인작업실적응답 전문 전송(YDY1L005)
			**********************************************************/
			//크레인 및 권상실적위치 재료정보 Flex Server로 전송
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			slabComm.sndToFlexData(resMsg);

			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY1L005(resMsg));
			}
			
			/**********************************************************
			* 12.  연주야드 적치 최적화 핸드스카핑장 입출고 이력 추가
			**********************************************************/
			if ("A".equals(ydUpWrLoc.substring(0, 1))) {
				//C1: ABSB1, C2: AASB2, GM : ABGM, TC: ABSB0
				
				String upEqpCd = ydUpWrLoc.substring(2, 4);
				String dnEqpCd = ydDnWoLoc.substring(2, 4);
				
				String eqpGp = "";
				//핸드스카핑장 출고
				//1.핸드스카핑장1단 에서 핸드스카핑장 아닌곳으로 이적 
				//2.핸드스카핑장1단에서 핸드스카핑장 1단 아닌 곳으로 이적

				slabUtils.printLog(logId, "핸드스카핑출고 조건 확인 upEqp["+upEqpCd+"] dnEqp["+dnEqpCd+"] upWrLayer["+ydUpWrLayer+"] dnWoLayer["+ydDnWoLayer+"]", "SL");
				if( //1번조건
					(   ("SB".equals(upEqpCd) || "GM".equals(upEqpCd) )
					  && !("SB".equals(dnEqpCd) || "GM".equals(dnEqpCd) )
					  && "001".equals(ydUpWrLayer)
					) ||
					//2번조건
					(    ("SB".equals(upEqpCd) || "GM".equals(upEqpCd) )
					  && ("SB".equals(dnEqpCd) || "GM".equals(dnEqpCd) )
				  	  && "001".equals(ydUpWrLayer)
					  && !"001".equals(ydDnWoLayer)
					)
				  )
				{
					
					String delimiter = ydUpWrLoc.substring(4, 5);
					String ydBayGp   = ydUpWrLoc.substring(1, 2);
					
					if("GM".equals(upEqpCd)) {
						eqpGp = "GM";
					}
					else if ("SB".equals(upEqpCd)){
						if("A".equals(ydBayGp)){
							eqpGp = "C2"; //C2 핸드장
						}
						else if ("B".equals(ydBayGp)){
							if("0".equals(delimiter)) {
								eqpGp = "TC";
							}
							else if("2".equals(delimiter) || "0".equals(delimiter)) {
								eqpGp = "C1";
							}
						}
					}
					
				
					jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId); //크레인스케줄ID
					jrParam.setField("EQP_CD"			, "SB"); //설비코드
					jrParam.setField("EQP_GP"   		, eqpGp  ); //설비구분
					jrParam.setField("MODIFIER" 		, modifier  ); //수정자
					
					commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdEqpIOHistByCrnSch", logId, methodNm, "핸드스카핑 출고이력");
				}
			}
			//2023.02.15 추가 이정규매니저 요청 REQ202301442577
			/**********************************************************
			* 13.  연주야드 C1, C2 밴딩슬라브 발생 이력 추가 
			* ACDP0101 (C1) ABDP0301(C2) 권상시 벤딩재 판정 
			**********************************************************/
			if( "ACDP0101".equals(ydUpWrLoc) || "ABDP0301".equals(ydUpWrLoc) ){
				jrParam.setField("YD_CRN_SCH_ID",ydCrnSchId);
				JDTORecordSet chkResult = commDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtMtl_PIDEV", logId, methodNm, "스케줄재료정보Check");
				
				if(chkResult.size() >0){
					String stlNos = "";
					for(int i=0; i< chkResult.size() ; i++){
						if("".equals(stlNos)){
							stlNos += chkResult.getRecord(i).getFieldString("STL_NO");
						}
						else {
							stlNos += ","+ chkResult.getRecord(i).getFieldString("STL_NO");
						}
					}
					
					GridData gridParam = new GridData();
					gridParam.addParam("V_STL_NOS", stlNos);
					gridParam.addParam("V_BENDING_YN", "Y");
					gridParam.addParam("V_ITM_GP", "*");
					gridParam.addParam("V_MODIFIER", modifier);
					gridParam.addParam("action_code", "update");
					
					EJBConnector ejbConn = new EJBConnector("default", "SlabJspFaEJB", this);
					ejbConn.trx("updStockBendReg" , new Class[]{ GridData.class } , new Object[] { gridParam } );
					 
				}
			}
			

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YdCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY1L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3크레인권상실적(Y3YDL008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3크레인권상실적[SlabYdL2RcvSeEJB.rcvY3YDL008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			//크레인권상실적 처리
			return this.rcvY1YDL008(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인권하실적(Y1YDL009, Y3YDL009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY1YDL009(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인권하실적[SlabYdL2RcvSeEJB.rcvY1YDL009] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부
		YdDelegate ydDelegate = new YdDelegate();
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydDnWrLoc     = slabUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = slabUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LAYER"  )); //야드권하실적단
			String ydCrnXaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"      )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydUpWrLoc     = ""; //야드권상실적위치
			String ydUpWrLayer     = ""; //야드권상실적단
			
			String wrkCrnEqpId = ydEqpId;
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			//크레인작업실적응답 전문 전송여부를 Check -> Backup시에도 응답전문 전송
			//if (!msgId.equals(modifier) || msgId.startsWith("YDYD")) {
			//	resYn = false;
			//}

			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)
			if ("4".equals(ydWrkProgStat)) {
				resMsg.setField("YD_L2_WR_GP", "D"); //야드L2실적구분(권하실적)
			} else {
				resMsg.setField("YD_L2_WR_GP", "F"); //야드L2실적구분(강제권하)
			}

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydDnWrLoc)) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			logger.println(LogLevel.INFO, "-----> 수신 항목 값 Check 완료");

			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("V_MODIFIER"     , modifier  ); //수정자

			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.getStat("CrnSch", jrParam);

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "DN11";
				ydL3Msg = "오류:크레인스케쥴ID DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = slabUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydSchCd         = slabUtils.trim(jrChk.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
				ydUpWrLoc       = slabUtils.trim(jrChk.getFieldString("YD_UP_WR_LOC"    )); //야드권상실적위치
				ydUpWrLayer		= slabUtils.trim(jrChk.getFieldString("YD_UP_WR_LAYER"    )); //야드권상실적단
				String tmpStat  = slabUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = slabUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) { //2:권상완료, 3:권하지시
					ydL3HdRsCd = "DN12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "DN13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				//throw new Exception(ydL3Msg);
				logger.println(LogLevel.INFO, "["+ methodNm +"] "+ ydL3Msg);
				return jrRtn;
			}

			//조회 Parameter
			jrParam.setField("V_YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrParam.setField("V_YD_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			//설비
			jrParam.setField("V_YD_EQP_ID"  , ydEqpId); //야드설비ID(크레인)
			jrParam.setField("V_YD_EQP_STAT", "4"    ); //야드설비상태(권하완료)
			
			//실제 야드권하실적단 및 기타 정보 조회
			String wbCmplYn = ""; //작업예약완료여부
			boolean chgDnWrLayer = false; //권하위치 적치단 변경여부

			jsChk = rcv2Dao.getY1YDL009("Curr", jrParam);
			
			if (jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				wbCmplYn = slabUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				String tbDnWrLayer = slabUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));

				slabUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				if (!ydDnWrLayer.equals(tbDnWrLayer)) {
					slabUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
					chgDnWrLayer = true;
					ydDnWrLayer  = tbDnWrLayer;
					ydCrnZaxis   = "";
				}
				
				if ("".equals(ydCrnXaxis) || "".equals(ydCrnYaxis) || "".equals(ydCrnZaxis)) {
					ydCrnXaxis = slabUtils.trim(jrChk.getFieldString("YD_DN_WR_XAXIS"));
					ydCrnYaxis = slabUtils.trim(jrChk.getFieldString("YD_DN_WR_YAXIS"));
					ydCrnZaxis = slabUtils.trim(jrChk.getFieldString("YD_DN_WR_ZAXIS"));
				}
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:권하위치 DB정보 없음"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			//2023.11.10 연주 김충만 계장 요청 --//REQ202311504859
			//지정크레인 작업종료 시 해당 스케줄의 주크레인 작업도 기동될 수 있도록 수정
			jrParam.setField("V_YD_SCH_CD"     , ydSchCd  ); //수정자
			jsChk = commDao.getStat("SchCd", jrParam);
			if (jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				wrkCrnEqpId = slabUtils.trim(jrChk.getFieldString("YD_EQP_ID"));
				
				slabUtils.printLog(logId, "스케줄코드 ["+ydSchCd+"] 주크레인["+wrkCrnEqpId+"] 현재작업크레인["+ydEqpId+"]", "SL");
			}

			//야드권하작업수행구분
			String ydDnWrkActGp = ydEqpWrkMode;
			
			if ("0".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "B"; //Backup
			}
			
			logger.println(LogLevel.INFO, "-----> 크레인스케쥴ID Check 완료");

			
			/**********************************************************
			* 3. 전문 전송
			* 3.1 생산통제 장입진행실적(C열연:YDCTJ033, 후판:YDCTJ031)
			* 3.2 품질관리 M-Scarfing입측보급실적(YDQMJ001) : M/C Scarfer 보급시
			* 3.3 C연주정정L2 Carry-In재료정보(YDC3L005)
			* 3.4 C연주정정L2 Carry-In완료(YDC3L004)
			* 3.5 삭제 전문
			*   - C연주정정L2 OHCTake-Out완료(YDC3L002, YDC7L002)
			*   - C연주정정L2 OHCTake-In완료(YDC3L008, YDC7L008)
			**********************************************************/
			String currDt = slabUtils.getDateTime14(); //현재시각
			
			//크레인스케쥴
			jrParam.setField("V_YD_DN_CMPL_DT"   , currDt      ); //야드권하완료일시
			jrParam.setField("V_YD_DN_WR_LOC"    , ydDnWrLoc   ); //야드권하실적위치
			jrParam.setField("V_YD_DN_WR_LAYER"  , ydDnWrLayer ); //야드권하실적단
			jrParam.setField("V_YD_DN_WRK_ACT_GP", ydDnWrkActGp); //야드권하작업수행구분
			jrParam.setField("V_YD_DN_WR_XAXIS"  , ydCrnXaxis  ); //야드권하실적X축
			jrParam.setField("V_YD_DN_WR_YAXIS"  , ydCrnYaxis  ); //야드권하실적Y축
			jrParam.setField("V_YD_DN_WR_ZAXIS"  , ydCrnZaxis  ); //야드권하실적Z축
			jrParam.setField("V_WR_DT"           , currDt      ); //실적일시
			jrParam.setField("V_UP_DN_GP"        , "D"         ); //권상권하구분(권하)

			//생산통제 장입진행실적(권상권하)
			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDCTJ033UD", jrParam));

			//C연주야드이면
			if ("A".equals(ydDnWrLoc.substring(0, 1))) {
				//품질관리 M-Scarfing입측보급실적
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDQMJ001", jrParam));

				//C연주정정L2 Carry-In재료정보
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L005", jrParam));
				//C연주정정L2 Carry-In완료
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L004", jrParam)); 
				
				//C연주L3 그라인딩머신 보급완료
				if ("DP04".equals(ydDnWrLoc.substring(2, 6))) {
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDCSJ001", jrParam));
				}
			//else if ("M".equals(ydDnWrLoc.substring(0, 1))) { // 항만야드 기능적용 보완 : 2015.12.16 by LeeJY
			} else if (ydDnWrLoc.startsWith("MADP")) {  
				/** 항만야드 기능적용 보완 : 2015.12.16 by LeeJY 
				 *  -.정정작업에 투입될 재료를 Depiler Bed에 이적 완료 시*/
				logger.println(LogLevel.INFO, "-%---> 항만정정L2 Depiler Bed Carry-In 완료 전문전송");
				//항만정정L2 Carry-In재료정보
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE9L005", jrParam));
				//항만정정L2 Carry-In완료
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE9L004", jrParam)); 
			} else if (ydDnWrLoc.startsWith("MBPU")) {  
				/** 항만야드 기능적용 보완 : 2016.12.22 
				 *  -.B동에서 Carry-In 작업 시*/
				logger.println(LogLevel.INFO, "-%---> 항만정정L2 B동 PU Bed Carry-In 완료 전문전송");
				//항만정정L2 Carry-In재료정보
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE9L005", jrParam));
				//항만정정L2 Carry-In완료
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE9L004", jrParam)); 
			} else if("ABBK".equals(ydDnWrLoc.substring(0,4))) {
				
				//2018.06.19 C연주 보온뱅크 권하 작업의 경우 연주L2에 Carry-In 완료 전문 전송
				jrParam.setField("V_YD_STK_COL_GP" 	, ydDnWrLoc.substring(0,6));  //야드설비ID
				jrParam.setField("V_YD_STK_BED_NO" 	, ydDnWrLoc.substring(6,8)); //야드적치Bed번호
				jrParam.setField("V_YD_CRN_SCH_ID"		, ydCrnSchId); //야드크레인스케쥴ID
				jrParam.setField("V_YD_WBOOK_ID"		, ydWbookId); //야드작업예약ID
				
				logger.println(LogLevel.INFO, "-%---> C연주 보온뱅크 Carry-In 완료 전문전송");
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC7L004", jrParam));
			}
			
			/**********************************************************
			* 4. 권하실적위치가 대차(상차)
			* 4.1 대차 상차 정보 등록
			*   - 작업예약 야드작업계획대차 수정
			*   - 대차이송재료 등록
			* 4.2 대차 하차스케줄 생성
			*   - 작업예약 등록
			*   - 작업예약재료 등록
			* 4.3 대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
			* 4.4 L2 전송 전문
			*   - C연주정정L2 대차작업실적(YDC3L007, YDC7L007) 전송 : 후판Slab야드L2 전문 없음
			*   - C연주정정L2, 후판Slab야드L2 대차출발지시(YDC3L006, YDC7L006, YDY3L006) 전송 : 상차완료 시
			**********************************************************/
			if ("TC".equals(ydDnWrLoc.substring(2, 4))) {
				//야드작업계획대차
				jrParam.setField("V_YD_WRK_PLAN_TCAR", ydDnWrLoc.substring(0, 1) + "X" + ydDnWrLoc.substring(2, 6));

				//대차상차스케쥴 조회
				jsChk = rcv2Dao.getY1YDL009("TcarSchLd", jrParam);
				
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					jrParam.setField("V_YD_TCAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"))); //야드대차스케쥴ID(이력등록시에도 사용)
					String tcarLdCmplYn = slabUtils.trim(jrChk.getFieldString("TCAR_LD_CMPL_YN")); //대차상차완료여부

					//작업예약 야드작업계획대차 수정
					rcv2Dao.updY1YDL009("WbTcar", jrParam);

					//대차이송재료 등록
					rcv2Dao.updY1YDL009("TcarMtlIns", jrParam);

					if ("N".equals(tcarLdCmplYn)) {
						//상차완료가 아니면
						jrParam.setField("V_YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					} else {
						//상차완료이면
						jrParam.setField("V_YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)

						//대차스케줄 처리 (영대차출발지시)
						//야드하차작업예약ID 생성
						String ydCarudWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");

						if ("".equals(ydCarudWrkBookId)) {
							ydL3Msg = "오류:대차작업예약ID 생성 실패";
							resMsg.setField("YD_L3_HD_RS_CD", "DN21" ); //야드L3처리결과코드
							resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
							throw new Exception(ydL3Msg);
						}

						//작업예약 등록
						jrParam.setField("V_YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID
						jrParam.setField("V_YD_SCH_ST_GP"        , ydDnWrkActGp    ); //야드스케쥴기동구분

						rcv2Dao.updY1YDL009("WbTcarIns", jrParam);

						//작업예약재료 등록
						rcv2Dao.updY1YDL009("WbMtlTcarIns", jrParam);
					}

					//대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					rcv2Dao.updY1YDL009("TcarSchLd", jrParam);
					
					//C연주정정L2 대차작업실적
					if ("A".equals(ydDnWrLoc.substring(0, 1))) {
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L007", jrParam));
					}
					
					//C연주정정L2, 후판Slab야드L2 영대차출발지시
					if ("Y".equals(tcarLdCmplYn)) {
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L006", jrParam));
					}						
				}
			}
			
			/**********************************************************
			* 5. 권하실적위치가 차량(상차)
			* 5.1 차량이송재료 등록
			* 5.2 차량스케줄 야드차량진행상태, 야드설비작업상태(L:영차), 야드설비작업매수 등 수정
			*   - 직상차(차량 스케줄코드가 아님) : 야드차량진행상태(4:상차개시)
			*   - 상차완료(차량 스케줄코드가 아님) : 야드차량진행상태(5:상차완료)
			*   - 야드차량사용구분이 구내운송(L)이고 상차완료이면
			*     . 권상실적위치가 ADPUP1 이고 차량 상차매수 4매가 안되면 Skip
			*     . 권상실적위치가 ADPUP1 이고 차량 상차매수 4매 이상이거나
			*     . 마지막 크레인스케줄 이면
			* 5.3 공통 처리 : 야드차량사용구분이 구내운송(L)이고 상차완료이면
			* 5.3.1 주편, Slab 공통 Table 소재이송일시 수정
			* 5.3.2 소재이송지시 야드재료예정저장From위치코드, 이송상차일자 수정
			* 5.3.3 저장품 목표야드, 목표동, 목표행선 등을 수정
			* 5.4 야드차량사용구분이 출하차량(G)
			* 5.4.1 출하관리 외판슬라브일품출하상차실적(YDDMR013) 전송
			* 5.4.2 출하관리 외판슬라브출하상차완료(YDDMR017) 전송
			*     - 상차완료(마지막 크레인스케줄)이면
			**********************************************************/
			//차량상차완료여부(소재이송지시 수정 및 공통Table 소재이송일시 수정)
			String carLdCmplYn = "N";

			if("PT".equals(ydDnWrLoc.substring(2, 4))) {
				//차량상차스케줄 정보 조회
				jsChk = rcv2Dao.getY1YDL009("CarSchLd", jrParam);
				
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					jrParam.setField("V_YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					String ydCarUseGp = slabUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")); //야드차량사용구분
					carLdCmplYn = slabUtils.trim(jrChk.getFieldString("CAR_LD_CMPL_YN")); //차량상차완료여부
					String trnEqpCd = slabUtils.trim(jrChk.getFieldString("TRN_EQP_CD")); //장비번호
					
					//차량이송재료 등록
					rcv2Dao.updY1YDL009("CarMtlIns", jrParam);

					//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					if ("Y".equals(carLdCmplYn)) {
						jrParam.setField("V_YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					} else {
						jrParam.setField("V_YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					}

					rcv2Dao.updY1YDL009("CarSchLd", jrParam);

					if ("G".equals(ydCarUseGp)) {
						//출하차량(G)
						carLdCmplYn = "N";	//출하차량은 소재이송지시 수정 및 공통Table 소재이송일시 등록 안 함
						//출하관리 외판슬라브일품출하상차실적
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR013", jrParam));
						//출하관리 외판슬라브출하상차완료
						if ("Y".equals(carLdCmplYn)) {
							jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR017", jrParam));
						}						
					} 
					
					
					//구내운송 상차완료 TC 자동전송(트레일러 인 경우에만 ) C연주 인경우
					if ("L".equals(ydCarUseGp) && "TR".equals(trnEqpCd.substring(1 ,3)) && "A".equals(ydDnWrLoc.substring(0, 1)) && "Y".equals(carLdCmplYn)) { 
						
						JDTORecord recInTemp = JDTORecordFactory.getInstance().create();		 
						recInTemp.setField("MSG_ID",        "YDTSJ008");
		    			recInTemp.setField("YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); 
		    			ydDelegate.sendMsg(recInTemp);
						
					}
					//2024.06.10 차량 작업관리 화면에 자동상차완료처리 할수 있기 기능개선 -- 연주 김충만계장 --//REQ202405572881
					//권하위치의 차량 포인트가 자동상차완료 설정인지 확인 추가해야함 .
					String ptAutoCompleteYn = "M";
					JDTORecordSet jsChkPt = rcv2Dao.getPtAutoCompleteYn(ydDnWrLoc.substring(0, 6));
					if(jsChkPt.size() > 0) {
						JDTORecord jrChkPt = jsChkPt.getRecord(0);
						ptAutoCompleteYn = slabUtils.trim(jrChkPt.getFieldString("MATL_SUP_MTD_GP")); //자동상차완료 설정 여부
						logger.println(LogLevel.INFO, "-%---> PT 자동 상차완료 가능 포인트 여부(A자동 M수동) : " + ptAutoCompleteYn);
					}
					logger.println(LogLevel.INFO, "-%---> PT 자동 상차완료 처리 조건 : ydCarUseGp(L) = " + ydCarUseGp + ", trnEqpCd(PT) = " + trnEqpCd.substring(1 ,3) + ", ydDnWrLoc(A) = "
							+ ydDnWrLoc.substring(0, 1) + ", carLdCmplYn(Y) = " + carLdCmplYn + ", ptAutoCompleteYn(A) = " + ptAutoCompleteYn);
					if ("L".equals(ydCarUseGp) && "PT".equals(trnEqpCd.substring(1 ,3)) && "A".equals(ydDnWrLoc.substring(0, 1)) && "Y".equals(carLdCmplYn) && "A".equals(ptAutoCompleteYn)) { 
						
						JDTORecord recInTemp = JDTORecordFactory.getInstance().create();		 
						recInTemp.setField("MSG_ID",        "YDTSJ008");
		    			recInTemp.setField("YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); 
		    			ydDelegate.sendMsg(recInTemp);
						
					}
				}
			}
			
			/**********************************************************
			* 6. 권상실적위치가 차량(하차)
			*    마지막 스케줄이면(차량이송재료 없음)
			* 6.1 차량스케줄 야드차량진행상태(E:하차완료), 야드설비작업상태(U:공차) 등 수정
			* 6.2 생산통제 이송하차실적(YDCTJ034) 전송
			*   - 재료외형구분이 Slab이고 Slab지시행선이 PA,PB 이고 재열재구분이 1,2
			* 6.3 후판조업 후판재열재슬라브적치실적(1후판:YDPRJ003, 2후판:YDPPJ003) 전송
			*   - 권상실적위치가 후판Slab야드이고 발지개소코드가 후판-극후물 냉각대(DKY23,DWY23) 
			* 6.4 구내운송 소재차량하차완료 송신(YDTSJ010) 전송
			* 6.5 권하실적재료 적치단 수정 후 등록하여야 하므로 하단부로 이동
			*   - 소재이송지시 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드 수정
			* 6.6 공통 Table 현재진도코드 변경 후 작업대상은 하단부로 이동
			*   - 주편, Slab 공통 Table 소재인수일시 수정
			*   - 진행관리 Slab이송완료실적(YDPTJ001) 전송
			**********************************************************/
			//차량하차여부(공통Table 소재인수일시 수정 및 진행관리 Slab이송완료실적 송신)
			String carUdCmplYn = "N";

			if ("PT".equals(ydUpWrLoc.substring(2, 4)) && !"PT".equals(ydDnWrLoc.substring(2, 4))) {
				//야드하차작업예약ID 차량하차스케줄 정보 조회
				jsChk = rcv2Dao.getY1YDL009("CarSchUd", jrParam);
	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					jrParam.setField("V_YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					carUdCmplYn = slabUtils.trim(jrChk.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					
					//차량하차완료이면
					if ("Y".equals(carUdCmplYn)) {
						//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						rcv2Dao.updY1YDL009("CarSchUd", jrParam);
						//생산통제 이송하차실적
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDCTJ034", jrParam));
						//구내운송 소재차량하차완료
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ010", jrParam));
						if (!("M".equals(ydDnWrLoc.substring(0, 1)))) {  // 항만야드는 전송 불필요 : 2015.12.16 by LeeJY
							//후판조업 후판재열재슬라브적치실적
							jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDPRJ003", jrParam));
						}
					}
				} else {
					//야드하차작업예약ID가 없는 경우 권상실적수신에서 작업예약 재료번호로 차량하차스케줄ID를 조회하여 작업예약ID 등록
					slabUtils.printLog(logId, "권하실적 하차 차량스케줄이 없습니다.", "SL");
				}
			}
			
			

			/**********************************************************
			* 7. 설비, 적치단 , 크레인스케쥴, 저장품, 작업예약재료, 작업예약 수정
			* 7.1 설비 야드설비상태(권하완료) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 삭제
			*   - 권하위치 재료정보 수정
			*   - 권하위치외 같은 재료번호로 등록된 적치단 수정(권하분리 재료 제외)
			* 7.3 크레인스케쥴
			*   - 크레인작업재료 삭제
			*   - 크레인스케쥴 권하실적 수정 및 삭제
			* 7.4 작업예약 마지막 크레인스케쥴 이면
			*   - 작업예약재료 삭제
			*   - 작업예약 수정 및 삭제
			**********************************************************/
			//설비(야드설비상태) 수정
			commDao.updStat("Eqp", jrParam);
			//적치단(크레인 및 권하위치) 수정
			rcv2Dao.updY1YDL009("StkLyr", jrParam);
			//크레인작업재료 삭제
			rcv2Dao.updY1YDL009("CrnMtl", jrParam);
			//크레인스케쥴 권하실적 수정 및 삭제
			rcv2Dao.updY1YDL009("CrnSch", jrParam);

			//작업예약완료 이면
			if ("Y".equals(wbCmplYn)) {
				//작업예약재료 삭제
				rcv2Dao.updY1YDL009("WbMtlDel", jrParam);
				//작업예약 수정 및 삭제
				rcv2Dao.updY1YDL009("WbDel"   , jrParam);
			}
			
			/**********************************************************
			* 8. 공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			* 8.1 주편 및 Slab 공통 Table 수정
			*   - 크레인스케줄 재료를 대상
			*   - 권하실적위치로 저장위치 수정
			*   - 권상실적위치가 차량이면 현재진도코드 수정
			* 8.2 저장품 수정
			*   - 작업예약 재료를 대상
			*   - 작업예약이 삭제되었으면 작업예약ID, 스케줄코드 삭제
			*   - 현재진도코드가 저장품과 다르면 관련 항목(산적LotType 등) 수정
			*   - 저장위치가 저장품과 다르면 저장위치 수정
			* 8.3 작업이력 등록
			*   - 크레인스케줄 재료를 대상
			* 8.4 차량상차 또는 하차완료 시 차량이송재료 대상으로
			* 8.4.1 진행관리 Slab이송완료실적(YDPTJ001) 전송
			*     - 하차완료 시 차량이송재료 현재진도코드 수정 후
			* 8.4.1 소재이송지시 수정 : 권하실적재료 적치단 수정 후
			*     - 상차 : 이송상차일자, 야드재료예정저장From위치코드
			*     - 하차 : 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드
			* 8.4.2 주편 및 Slab 공통 Table 수정
			*     - 상차 : 소재이송일시
			*     - 하차 : 소재인수일시
			**********************************************************/
			//주편공통 수정
			rcv2Dao.updY1YDL009("MslabComm", jrParam);
			//Slab공통 수정
			rcv2Dao.updY1YDL009("SlabComm" , jrParam);
			
			//차량하차완료 시
			//202510.이송지시완료처리 개선
			//202512.재료진도 업데이트하지 않도록 수정
			jrParam.setField("V_YD_DN_WR_LOC_SUB", ydDnWrLoc.substring(0, 1)); //권하실적위치
			jrParam.setField("V_LD_UD_GP", "U"); //상하차구분(하차)
			String stlFrtoMoveChkYn = "Y";
			stlFrtoMoveChkYn = commDao.stlFrtoMoveChk(jrParam);

			//차량하차작업인 경우 재료공통 진도 변경
			if ("PT".equals(ydUpWrLoc.substring(2, 4)) && !"PT".equals(ydDnWrLoc.substring(2, 4))) {
				//주편공통 재료진도 변경
				jsChk = rcv2Dao.getY1YDL009("MslabCommProg", jrParam);
				
				for (int ii = 0; ii < jsChk.size(); ii++) {

					slabUtils.printLog(logId, "(주편)차량하차 권하위치와 이송지시 상차지 비교 (권하위치 YD_GP) : " + ydDnWrLoc.substring(0, 1), "SL");
					if("Y".equals(stlFrtoMoveChkYn)) {
						slabUtils.printLog(logId, "(주편)차량하차 권하위치와 이송지시 상차지 비교결과 : 정상(Y) - 재료진도 업데이트", "SL");
						jrChk = jsChk.getRecord(ii);
						
						slabUtils.printLog(logId, "주편공통 재료진도 변경 " + slabUtils.trim(jrChk.getFieldString("LOG_MSG")), "SL");

						jrParam.setField("V_CURR_PROG_REG_DDTT", slabUtils.trim(jrChk.getFieldString("CURR_PROG_REG_DDTT"))); //현재진도등록일시
						jrParam.setField("V_CURR_PROG_CD"      , slabUtils.trim(jrChk.getFieldString("CURR_PROG_CD"      ))); //현재진도코드
						jrParam.setField("V_STL_NO"            , slabUtils.trim(jrChk.getFieldString("STL_NO"            ))); //재료번호

						rcv2Dao.updY1YDL009("MslabCommProg", jrParam);

						//진행관리 Slab이송완료실적(진도변경) 전송
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDPTJ001Mslab", jrParam));
					} else {
						slabUtils.printLog(logId, "(주편)차량하차 권하위치와 이송지시 상차지 비교결과 : 비정상(N) - 재료진도 업데이트 하지 않음", "SL");
					}

				}

				//Slab공통 재료진도 변경
				jsChk = rcv2Dao.getY1YDL009("SlabCommProg", jrParam);

				for (int ii = 0; ii < jsChk.size(); ii++) {
					
					slabUtils.printLog(logId, "(슬라브)차량하차 권하위치와 이송지시 상차지 비교 (권하위치 YD_GP) : " + ydDnWrLoc.substring(0, 1), "SL");
					if("Y".equals(stlFrtoMoveChkYn)) {
						slabUtils.printLog(logId, "(슬라브)차량하차 권하위치와 이송지시 상차지 비교결과 : 정상(Y) - 재료진도 업데이트", "SL");
						jrChk = jsChk.getRecord(ii);

						slabUtils.printLog(logId, "Slab공통 재료진도 변경 " + slabUtils.trim(jrChk.getFieldString("LOG_MSG")), "SL");

						jrParam.setField("V_CURR_PROG_REG_DDTT", slabUtils.trim(jrChk.getFieldString("CURR_PROG_REG_DDTT"))); //현재진도등록일시
						jrParam.setField("V_CURR_PROG_CD"      , slabUtils.trim(jrChk.getFieldString("CURR_PROG_CD"      ))); //현재진도코드
						jrParam.setField("V_STL_NO"            , slabUtils.trim(jrChk.getFieldString("STL_NO"            ))); //재료번호

						rcv2Dao.updY1YDL009("SlabCommProg", jrParam);

						//진행관리 Slab이송완료실적(진도변경) 전송
						jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL3("YDPTJ001Slab", jrParam));
					} else {
						slabUtils.printLog(logId, "(슬라브)차량하차 권하위치와 이송지시 상차지 비교결과 : 비정상(N) - 재료진도 업데이트 하지 않음", "SL");
					}

				}
			}
			
			/**********************************************************
			 * 9.보온뱅크 보급실적처리
			 * 9.1 보온뱅크에 권하 시점 부터 실적 처리를 시작한다.
			 * 
			 **********************************************************/
			
			//보온뱅크 권하 작업
			if ( !"BK".equals(ydUpWrLoc.substring(2, 4)) && "BK".equals(ydDnWrLoc.substring(2, 4))) {
				
				//JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
				
				jsChk = rcv2Dao.getY1YDL009("BankStlList", jrParam);
				
				for (int ii = 0; ii < jsChk.size(); ii++) {
					jrChk = jsChk.getRecord(ii);
					
					slabUtils.printLog(logId, "보온뱅크 실적 등록 " + slabUtils.trim(jrChk.getFieldString("STL_NO")), "SL");
 
					jrParam.setField("V_MSLAB_NO"            , slabUtils.trim(jrChk.getFieldString("STL_NO"            ))); //재료번호

					//주편(보온뱅크적치유무 , 보온뱅크장입시간)
					rcv2Dao.updY1YDL009("updateMslabCommonSubInfo", jrParam); 
					
					//슬라브(보온뱅크적치유무 , 보온뱅크장입시간)
					rcv2Dao.updY1YDL009("updateSlabCommonSubInfo", jrParam);
			 
				}
				
				//2018.06.19 C연주 보온뱅크 권하 작업의 경우 연주L2에 Carry-In 완료 전문 전송
				/*if("ABBK".equals(ydDnWrLoc.substring(0,4))) {
					jrParam2.setField("V_YD_STK_COL_GP" 	, ydDnWrLoc.substring(0,6));  //야드설비ID
					jrParam2.setField("V_YD_STK_BED_NO" 	, ydDnWrLoc.substring(6,8)); //야드적치Bed번호
					jrParam2.setField("V_YD_CRN_SCH_ID"		, ydCrnSchId); //야드크레인스케쥴ID
					jrParam2.setField("V_YD_WBOOK_ID"		, ydWbookId); //야드작업예약ID
					
					jrParam2.setField("V_YD_EQP_CD"   			, ydDnWrLoc.substring(0,6)); //야드설비ID
					jrParam2.setField("V_YD_STK_BED_NO"    		, ydDnWrLoc.substring(6,8)   ); //야드적치Bed번호
					jrParam2.setField("V_YD_STK_BED_STL_SH"  	, ydDnWrLayer ); //야드적치Bed재료매수
					jrParam2.setField("V_CARRY_IN_END_GP"		, "Y"); //Carry-In완료구분
					jrParam2.setField("V_YD_EQP_WRK_SH"  		, String.valueOf(jsChk.size()) ); //야드설비작업매수
					
					logger.println(LogLevel.INFO, "-%---> C연주 보온뱅크 Carry-In 완료 전문전송");
					//항만정정L2 Carry-In재료정보
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC7L004", jrParam2));
				}*/
			}
			
			
			//저장품 수정
			rcv2Dao.updY1YDL009("Stock", jrParam);
			//작업이력 등록
			commDao.insSlabYd("WrkHist", jrParam);

			//차량상차 또는 하차완료 시
			if ("Y".equals(carLdCmplYn) || "Y".equals(carUdCmplYn)) {
				//소재이송지시 수정
				if ("Y".equals(carLdCmplYn)) {
					//차량상차완료 시
					jrParam.setField("V_LD_UD_GP", "L"); //상하차구분(상차)
					rcv2Dao.updY1YDL009("StlMoveLd", jrParam);
				} else {
					//차량하차완료 시
					//202510.이송지시완료처리 개선
					//202512.상단 재료진도 업데이트하는 부분으로 이동
					//jrParam.setField("V_YD_DN_WR_LOC_SUB", ydDnWrLoc.substring(0, 1)); //권하실적위치
					//jrParam.setField("V_LD_UD_GP", "U"); //상하차구분(하차)
					
					slabUtils.printLog(logId, "차량하차 권하위치와 이송지시 상차지 비교 (권하위치 YD_GP) : " + ydDnWrLoc.substring(0, 1), "SL");
					if("Y".equals(stlFrtoMoveChkYn)) {
						slabUtils.printLog(logId, "차량하차 권하위치와 이송지시 상차지 비교결과 : 정상(Y) - 이송지시 업데이트", "SL");
						rcv2Dao.updY1YDL009("StlMoveUd", jrParam);
					} else {
						slabUtils.printLog(logId, "차량하차 권하위치와 이송지시 상차지 비교결과 : 비정상(N) - 이송지시 업데이트 하지 않음", "SL");
					}

					//회송테이블 업데이트
					//jrParam.setField("V_YD_CAR_SCH_ID", slabUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					//rcv2Dao.updY1YDL009("RethtHist", jrParam);
					
				}

				//주편공통 수정
				rcv2Dao.updY1YDL009("MslabCommCar", jrParam);
				//Slab공통 수정
				rcv2Dao.updY1YDL009("SlabCommCar" , jrParam);
			}
			
			/**********************************************************
			* 9. 권하지시위치 단과 실적 단이 다르면 저장품제원 전문 전송(YDY1L002, YDY3L002, YDE7L002)
			**********************************************************/
			if (chgDnWrLayer) {
				// 항만야드 기능적용 보완 : 2015.12.16 by LeeJY 
				if ("M".equals(ydDnWrLoc.substring(0, 1))) {  
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDE7L002DnWr", jrParam));
				} 
				else {
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY1L002DnWr", jrParam));
				}
			}
			
			/**********************************************************
			* 10. Flex Server 및 크레인작업실적응답 전문 전송(YDY1L005, YDY3L005)
			**********************************************************/
			//크레인, 권상실적위치 및 권하실적위치 재료정보 Flex Server로 전송
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치
			slabComm.sndToFlexData(resMsg);

			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY1L005(resMsg));
			}
			
			
			/**********************************************************
			* 11. to위치가 C#3 핸드스카핑장인 경우 별도 처리
			**********************************************************/
			if("MBHD".equals(ydDnWrLoc.substring(0,4))) {
				
				String stl_nos = "";
				SlabYdJspSeEJBBean slabYdJspSeEJBBean = new SlabYdJspSeEJBBean();
				GridData returnGrid = new GridData();
				GridData gdRtn = new GridData();
				String nextToYnDnWrCol = "";
				String nextToYnDnWrBed = "";

				//현재 위치로 권하한 슬라브 매수 조회
				jrParam.setField("V_YD_STK_COL_GP", ydDnWrLoc.substring(0,6));
				jrParam.setField("V_YD_STK_BED_NO", ydDnWrLoc.substring(6,8));
				jsChk = rcv2Dao.getY1YDL009("SlabCnt", jrParam);
				
				if (jsChk.size() > 1) {
					
					//다음 스케쥴 생성시 필요한 슬라브 번호 정보 저장
					for (int ii = 0; ii < jsChk.size()-1; ii++) {
						jrChk = jsChk.getRecord(ii);
						
						if(ii != jsChk.size()-2) {
							stl_nos += slabUtils.trim(jrChk.getFieldString("STL_NO")) + ",";
						} else {
							stl_nos += slabUtils.trim(jrChk.getFieldString("STL_NO"));
						}
						
					}
					
					//작업예약이 생성되어 있는 슬라브가 있다면 해당 작업예약 삭제
					//1.해당 슬라브에 지정되어 있는 작업예약ID 조회
					jrParam.setField("V_STL_NO", stl_nos);
					jsChk = rcv2Dao.getY1YDL009("SlabWrkId", jrParam);
					
					if (jsChk.size() > 0) { 
						
						for (int ii = 0; ii < jsChk.size(); ii++) {
							jrChk = jsChk.getRecord(ii);
							
							//2.조회 결과로 나온 작업예약 ID 삭제
							//jrParam.setField("V_MODIFIER" , arg1);
							jrParam.setField("V_YD_WBOOK_ID" , slabUtils.trim(jrChk.getFieldString("YD_WBOOK_ID")));
							rcv2Dao.updY1YDL009("WbDel",jrParam);
							
							//3.조회 결과로 나온 작업예약 재료 삭제
							//jrParam.setField("V_MODIFIER" , arg1);
							jrParam.setField("V_YD_WBOOK_ID" , slabUtils.trim(jrChk.getFieldString("YD_WBOOK_ID")));
							rcv2Dao.updY1YDL009("WbMtlDel",jrParam);
						}
	//					
					}
					
					//다음 to위치 지정을 위한 적치 가능한 베드 검색
					jrParam.setField("V_YD_STK_COL_GP", ydDnWrLoc.substring(0,6));
					jrParam.setField("V_YD_STK_BED_NO", ydDnWrLoc.substring(6,8));
					jsChk = rcv2Dao.getY1YDL009("EmpBed", jrParam);
					
					if(jsChk.size() > 0) {
						jrChk = jsChk.getRecord(0);
						nextToYnDnWrCol = slabUtils.trim(jrChk.getFieldString("YD_STK_COL_GP"));
						nextToYnDnWrBed = slabUtils.trim(jrChk.getFieldString("YD_STK_BED_NO"));
					}
					
					//이적작업예약등록 호출
					//grid setting
					gdRtn.createHeader("V_STL_NOS" , "T");
					gdRtn.addParam("V_STL_NOS" , stl_nos);
					gdRtn.createHeader("V_YD_STK_COL_GP" , "T");
					gdRtn.addParam("V_YD_STK_COL_GP" , nextToYnDnWrCol);
					gdRtn.createHeader("V_YD_TO_LOC_GUIDE" , "T");
					gdRtn.addParam("V_YD_TO_LOC_GUIDE" , nextToYnDnWrCol+nextToYnDnWrBed);
					gdRtn.createHeader("V_YD_SCH_PRIOR","T");
					gdRtn.addParam("V_YD_SCH_PRIOR" , "1");
					
					
					//param setting
					JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
					jrParam2.addField("V_STL_NOS" , stl_nos);
					jrParam2.addField("V_YD_STK_COL_GP" , nextToYnDnWrCol);
					jrParam2.addField("V_YD_TO_LOC_GUIDE" , nextToYnDnWrCol+nextToYnDnWrBed);
					jrParam2.addField("V_YD_SCH_PRIOR" , "1");
					
					slabYdJspSeEJBBean.trtMvStkWrkBookReg(slabUtils.jdtoRecordToGridData(returnGrid,jrParam2,gdRtn));
				}
				
			}
			
			/**********************************************************
			* 12.  연주야드 적치 최적화 핸드스카핑장 입출고 이력 추가
			**********************************************************/
			if ("A".equals(ydDnWrLoc.substring(0, 1))) {
				//C1: ABSB1, C2: AASB2, GM : ABGM, TC: ABSB0
				
				String upEqpCd = ydUpWrLoc.substring(2, 4);
				String dnEqpCd = ydDnWrLoc.substring(2, 4);
				
				String eqpGp = "";
				//핸드스카핑장 입고
				//1.핸드스카핑장 아닌곳에서 핸드스카핑장 1단으로 이적
				//2.핸드스카핑장인곳의 1단 아닌곳에서 핸드스카핑장 1단으로 이적

				slabUtils.printLog(logId, "핸드스카핑입고 조건 확인 upEqp["+upEqpCd+"] dnEqp["+dnEqpCd+"] upWrLayer["+ydUpWrLayer+"] dnWrLayer["+ydDnWrLayer+"]", "SL");
				
				if( //1번조건
					(   !("SB".equals(upEqpCd) || "GM".equals(upEqpCd) )
					  && ("SB".equals(dnEqpCd) || "GM".equals(dnEqpCd) )
					  && "001".equals(ydDnWrLayer)
					) ||
					//2번조건
					(    ("SB".equals(upEqpCd) || "GM".equals(upEqpCd) )
					  && ("SB".equals(dnEqpCd) || "GM".equals(dnEqpCd) )
				  	  && !"001".equals(ydUpWrLayer)
					  && "001".equals(ydDnWrLayer)
					)
				  )
				{
					
					String delimiter = ydDnWrLoc.substring(4, 5);
					String ydBayGp   = ydDnWrLoc.substring(1, 2);
					
					
					if("GM".equals(dnEqpCd)) {
						eqpGp = "GM";
					}
					else if ("SB".equals(dnEqpCd)){
						if("A".equals(ydBayGp)){
							eqpGp = "C2"; //C2 핸드장
						}
						else if ("B".equals(ydBayGp)){
							if("0".equals(delimiter)) {
								eqpGp = "TC";
							}
							else if("2".equals(delimiter) || "0".equals(delimiter)) {
								eqpGp = "C1";
							}
						}
					}
					
				
					jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId); //크레인스케줄ID
					jrParam.setField("EQP_CD"			, "SB"); //설비코드
					jrParam.setField("EQP_GP"   		, eqpGp  ); //설비구분
					jrParam.setField("MODIFIER" 		, modifier  ); //수정자
					
					commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdEqpIOHistByCrnSch", logId, methodNm, "핸드스카핑 입고이력");
				}
			}

			
			
			//인터락 여부 확인해서,현재 작업이 인터락 구간 작업일시 스위치 ON 전문 L2로 
			/**********************************************************
			* 13.  크레인 인터락 여부 확인
			**********************************************************/
			//if("AACRA1".equals(ydEqpId)){
			//현재 크레인 작업이 인터락 구간 작업인지 확인
			jsChk = JDTORecordFactory.getInstance().createRecordSet("YD");

			slabUtils.printLog(logId, "장비 ["+ydEqpId+"] 현 작업중인 크레인 스케줄 인터락 확인", "SL");
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
			ydcrnschDao.getYdCrnsch(jrParam, jsChk,702);
			
			if(jsChk != null && jsChk.size()>0){
				String chkinterlock_sect = slabUtils.trim(jsChk.getRecord(0).getFieldString("INTERLOCK_WRK_YN"));
	
				slabUtils.printLog(logId, "장비 ["+ydEqpId+"] 현 작업 인터락 구간 작업 여부["+chkinterlock_sect+"]", "SL");
				//인터락 여부 및 인터락구간 여부 모두 Y라면 l2로 전문 전송.
				if("Y".equals(chkinterlock_sect)){
					jrParam.setField("V_YD_EQP_ID", ydEqpId);
					jrParam.setField("C2CR_INTERLOCK_YN", "Y");
					jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY1L006", jrParam));
				}
			}
				
			//}
			
			/**********************************************************
			* 14. 크레인작업지시요구 전문 호출(Y1YDL007, Y3YDL007, E7YDL007)
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    
			if ("A".equals(ydDnWrLoc.substring(0, 1))) {
				jrYdMsg.setField("JMS_TC_CD", "Y1YDL007"); //JMSTC코드
			} else if ("M".equals(ydDnWrLoc.substring(0, 1))) {
				jrYdMsg.setField("JMS_TC_CD", "E7YDL007"); //JMSTC코드 -- 항만야드 기능적용 보완 : 2015.12.16 by LeeJY 
			}
			
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
			jrYdMsg.setField("V_MODIFIER"      , modifier  ); //수정자
			
			//크레인작업지시 전문을 추가
			jrRtn = slabUtils.addSndData(jrRtn, this.rcvY1YDL007(jrYdMsg));
			
			String APPLY_YN01 = commDao.slabApplyYn("APPLY_YN01");
			
			if("Y".equals(APPLY_YN01)){
				//2023.11.10 연주 김충만 계장 요청 --//REQ202311504859
				//지정크레인 작업종료 시 해당 스케줄의 주크레인 작업도 기동될 수 있도록 수정 
				if("A".equals(ydDnWrLoc.substring(0, 1))
				   && !ydEqpId.equals(wrkCrnEqpId)){
					slabUtils.printLog(logId, "스케줄코드 ["+ydSchCd+"] 주크레인["+wrkCrnEqpId+"] 현재작업크레인["+ydEqpId+"] 다르므로, 주크레인 작업지시요구", "SL");
					
					slabUtils.printLog(logId, "주크레인["+wrkCrnEqpId+"] 현재 크레인 스케줄 조회", "SL");
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("YD_EQP_ID" 		, wrkCrnEqpId  ); 
					JDTORecordSet chkWrkCrn = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCrnStatSet", logId, methodNm, "크레인스케줄 CHECK");
					
					String wrkCrnYdCrnSchId = "";
					String wrkCrnYdSchCd ="";
					//기 등록된 작업예약 존재
					if(chkWrkCrn != null && chkWrkCrn.size() > 0) {
						wrkCrnYdCrnSchId = chkWrkCrn.getRecord(0).getFieldString("YD_CRN_SCH_ID");
						wrkCrnYdSchCd    = chkWrkCrn.getRecord(0).getFieldString("YD_SCH_CD");
					}
					
					
					
					jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					
					jrYdMsg.setField("JMS_TC_CD", "Y1YDL007"); //JMSTC코드
					jrYdMsg.setField("YD_EQP_ID"       , wrkCrnEqpId   ); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
					jrYdMsg.setField("YD_SCH_CD"       , wrkCrnYdSchCd   ); //야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , wrkCrnYdCrnSchId); //야드크레인스케쥴ID
					jrYdMsg.setField("V_MODIFIER"      , modifier  ); //수정자
					
					jrRtn = slabUtils.addSndData(jrRtn, this.rcvY1YDL007(jrYdMsg));
				}
			}
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YdCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY1L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3크레인권하실적(Y3YDL009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL009(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3크레인권하실적[SlabYdL2RcvSeEJB.rcvY3YDL009] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			//크레인권하실적 처리
			return this.rcvY1YDL009(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : Y1크레인비상조업실적(Y1YDL010) : 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY1YDL010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y1크레인비상조업실적[SlabYdL2RcvSeEJB.rcvY1YDL010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "삭제된 전문입니다.", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : Y3크레인비상조업실적(Y3YDL010) : 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY3YDL010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3크레인비상조업실적[SlabYdL2RcvSeEJB.rcvY3YDL010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "삭제된 전문입니다.", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : Y3수불구변경요구(Y3YDL011)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL011(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3수불구변경요구[SlabYdL2RcvSeEJB.rcvY3YDL011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			}

			/**********************************************************
			* 2. 적치Bed Table에 Update할 대상을 검색
			**********************************************************/
			int trtCnt = 0;	//처리건수
			int bedCnt = 6;	//Bed건수

			String ydStkBedActStat[] = new String[bedCnt]; //야드적치Bed활성상태

			//Bed건수 만큼 Set
			for (int ii = 0; ii < bedCnt; ii++) {
				ydStkBedActStat[ii] = slabUtils.trim(rcvMsg.getFieldString("YD_STK_BED_ACT_STAT" + (ii + 1)));
				if (!"".equals(ydStkBedActStat[ii])) {
					trtCnt++;
				}
			}

			if (trtCnt <= 0) {
				throw new Exception("야드적치Bed활성상태(YD_STK_BED_ACT_STAT) 이상");
			}

			/**********************************************************
			* 3. 적치Bed Table에 활성상태를 Update
			**********************************************************/
			int idx = 0;
			String[][] param = new String[trtCnt][4];

			//Param Set
			for (int ii = 0; ii < bedCnt; ii++) {
				if (!"".equals(ydStkBedActStat[ii])) {
					param[idx][0] = modifier;				//수정자
					param[idx][1] = ydStkBedActStat[ii];	//야드적치Bed활성상태
					param[idx][2] = ydEqpId;				//야드적치열구분(야드설비ID)
					param[idx][3] = "0" + (ii + 1);			//야드적치Bed번호

					idx++;
				}
			}

			//적치Bed Table Update
			rcv2Dao.updY3YDL011SB(param, logId, methodNm);

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : Y1강제권상요구(Y1YDL012)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY1YDL012(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y1강제권상요구[SlabYdL2RcvSeEJB.rcvY1YDL012] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"   )); //야드설비ID
			String ydUpWrLoc  = slabUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC")); //야드권상실적위치
			String ydCrnXaxis = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS")); //야드크레인X축
			String ydCrnYaxis = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS")); //야드크레인Y축
			String modifier   = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"  )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			//크레인작업실적응답 전문 전송여부를 Check -> Backup시에도 응답전문 전송
			//if (!msgId.equals(modifier) || msgId.startsWith("YDYD")) {
			//	resYn = false;
			//}

			//크레인작업실적응답 전문 생성용
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE

			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"     , ydEqpId  ); //야드설비ID
			resMsg.setField("YD_CRN_SCH_ID" , ydUpWrLoc); //야드크레인스케쥴ID(야드권상실적위치)
			resMsg.setField("YD_L2_WR_GP"   , "J"      ); //야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD", "FU99"   ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"     , "오류:강제권상요구 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "FU01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "FU02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if (!"AACRM2".equals(ydEqpId) && !"ABCRM1".equals(ydEqpId)) {
				ydL3HdRsCd = "FU03";
				ydL3Msg    = "오류:마그네틱 크레인만 가능[" + ydEqpId + "]";
			} else if (ydUpWrLoc.length() < 8) {
				ydL3HdRsCd = "FU04";
				ydL3Msg    = "오류:권상위치 이상";
			} else if (!ydEqpId.substring(0, 2).equals(ydUpWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "FU05";
				ydL3Msg    = "오류:설비-권상위치[" + ydEqpId.substring(0, 2) + "-" + ydUpWrLoc.substring(0, 2) + "] 부적합";
			} else if ("".equals(ydCrnXaxis)) {
				ydL3HdRsCd = "FU06";
				ydL3Msg    = "오류:크레인X축 없음";
			} else if ("".equals(ydCrnYaxis)) {
				ydL3HdRsCd = "FU07";
				ydL3Msg    = "오류:크레인Y축 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 권상위치, 좌표값, 이적 재료 등을 Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_YD_EQP_ID"    , ydEqpId                 ); //야드설비ID
			jrParam.setField("V_YD_STK_COL_GP", ydUpWrLoc.substring(0,6)); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", ydUpWrLoc.substring(6,8)); //야드적치Bed번호
			jrParam.setField("V_YD_CRN_XAXIS" , ydCrnXaxis              ); //야드크레인X축
			jrParam.setField("V_YD_CRN_YAXIS" , ydCrnYaxis              ); //야드크레인Y축
			jrParam.setField("V_MODIFIER"     , modifier                ); //수정자

			//좌표값 조회
			JDTORecordSet jsChk = rcv2Dao.getY1YDL012("BL", jrParam);

			String stlNo         = ""; //권상가능 재료번호
			String ydStkLyrNo    = ""; //야드적치단번호
			String xaxisYn       = ""; //X좌표 정합성여부
			String yaxisYn       = ""; //Y좌표 정합성여부
			String ydStkBedXaxis = ""; //야드적치BedX축
			String ydStkBedYaxis = ""; //야드적치BedY축
			String ydMtlW        = ""; //야드재료폭
			String ydMtlL        = ""; //야드재료길이
			String ydMtlWt       = ""; //야드재료중량
			String wmCnt         = ""; //작업예약재료 건수

			if (jsChk.size() > 0) {
				stlNo         = slabUtils.trim(jsChk.getRecord(0).getFieldString("STL_NO"          ));
				ydStkLyrNo    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_STK_LYR_NO"   ));
				xaxisYn       = slabUtils.trim(jsChk.getRecord(0).getFieldString("XAXIS_YN"        ));
				yaxisYn       = slabUtils.trim(jsChk.getRecord(0).getFieldString("YAXIS_YN"        ));
				ydStkBedXaxis = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_STK_BED_XAXIS"));
				ydStkBedYaxis = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_STK_BED_YAXIS"));
				ydMtlW        = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_MTL_W"        ));
				ydMtlL        = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_MTL_L"        ));
				ydMtlWt       = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_MTL_WT"       ));
				wmCnt         = slabUtils.trim(jsChk.getRecord(0).getFieldString("WM_CNT"          ));
			}

			if (!"Y".equals(xaxisYn)) {
				//권상위치의 BedX축 허용오차 값내에 크레인X축 값 존재여부를 Check
				ydL3HdRsCd = "FU11";
				ydL3Msg    = "오류:크레인X축[" + ydStkBedXaxis + ":" + ydCrnXaxis + "] 이상";
			} else if (!"Y".equals(yaxisYn)) {
				//권상위치의 BedY축 허용오차 값내에 크레인Y축 값 존재여부를 Check
				ydL3HdRsCd = "FU12";
				ydL3Msg    = "오류:크레인Y축[" + ydStkBedYaxis + ":" + ydCrnYaxis + "] 이상";
			} else if ("".equals(stlNo)) {
				//권상위치에 적치된 이적 가능 재료 존재여부를 Check
				ydL3HdRsCd = "FU13";
				ydL3Msg    = "오류:권상가능 재료 없음[" + ydUpWrLoc + "]";
			} else if ("".equals(ydMtlW) || "".equals(ydMtlL) || "".equals(ydMtlWt)) {
				//이적 재료 저장품 Table 존재여부 및  Size 정상여부를 Check
				ydL3HdRsCd = "FU14";
				ydL3Msg    = "오류:[" + stlNo + "] 저장품정보 이상";
			} else if (!"".equals(wmCnt) && Integer.parseInt(wmCnt) > 0) {
				//이적 재료 작업예약재료 Table 존재여부를 Check
				ydL3HdRsCd = "FU15";
				ydL3Msg    = "오류:[" + stlNo + "] 작업예약 기등록";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 3. 설비상태, 설비사양, 크레인스케줄 존재여부 등을 Check
			**********************************************************/
			jsChk = rcv2Dao.getY1YDL012("ES", jrParam);

			String ydEqpStat     = ""; //야드설비상태
			String ydEqpWrkMode  = ""; //야드설비작업Mode
			String ydCrnSchId    = ""; //야드크레인스케쥴ID(현재)
			String ydWrkProgStat = ""; //야드작업진행상태
			String ydWrkAbleW    = ""; //야드작업가능폭
			String ydWrkAbleL    = ""; //야드작업가능길이
			String ydWrkAbleWt   = ""; //야드작업가능중량
			String ydSchCdCur    = ""; //현재 작업중인 크레인스케줄 스케줄코드 //2023.02.24 연주 김충만 계장요청. 핸드스카핑인출작업은 삭제후 올릴수있도록.
			boolean isHandSch	 = false;
			if (jsChk.size() > 0) {
				ydEqpStat     = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"     ));
				ydEqpWrkMode  = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE" ));
				ydCrnSchId    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
				ydWrkAbleW    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_ABLE_W"   ));
				ydWrkAbleL    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_ABLE_L"   ));
				ydWrkAbleWt   = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_ABLE_WT"  ));
				ydSchCdCur    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_CD"  ));
				//현재 작업중인 스케줄이 핸드스카핑 인출일때만 강제권상요구 작업 생성
				if(ydSchCdCur.length() >=6 && "SB".equals(ydSchCdCur.substring(2,4))){
					isHandSch = true;
				}
			}

			if ("".equals(ydEqpStat)) {
				//설비 Table 정보 Check
				ydL3HdRsCd = "FU21";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 정보 없음";
			} else if ("B".equals(ydEqpStat)) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "FU22";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 고장";
			} else if (!"1".equals(ydEqpWrkMode)) {
				//설비 Table 설비작업Mode Check
				ydL3HdRsCd = "FU23";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] Off-Line";
			} else if (!"1".equals(ydEqpStat) && !"W".equals(ydEqpStat)) {
				//설비 Table 설비상태 Check : 권상작업지시, 대기 외이면 불가
				ydL3HdRsCd = "FU24";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 상태[" + ydEqpStat + "] 부적합";
			} else if ("1".equals(ydEqpStat) && "".equals(ydCrnSchId)) {
				//설비 Table 설비상태가 권상작업지시이면 크레인스케줄의 존재여부를 Check
				//크레인스케줄  Table의 야드작업진행상태가 권상지시[1], 권상완료[2], 권하지시[3]인 스케줄이 있어야 함
				ydL3HdRsCd = "FU25";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 상태[" + ydWrkProgStat + "] 스케줄 없음";
			} else if ("".equals(ydWrkAbleW) || "".equals(ydWrkAbleL) || "".equals(ydWrkAbleWt)) {
				//크레인사양 Table 정보의 이상여부를 Check
				ydL3HdRsCd = "FU26";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 사양 이상";
			} else if (Double.parseDouble(ydMtlW) > Double.parseDouble(ydWrkAbleW)) {
				//이적 대상 재료 폭의 크레인사양폭 초과여부를 Check
				ydL3HdRsCd = "FU27";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 사양폭 초과";
			} else if (Double.parseDouble(ydMtlL) > Double.parseDouble(ydWrkAbleL)) {
				//이적 대상 재료 길이의 크레인사양길이 초과여부를 Check
				ydL3HdRsCd = "FU28";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 사양길이 초과";
			} else if (Double.parseDouble(ydMtlWt) > Double.parseDouble(ydWrkAbleWt)) {
				//이적 대상 재료 중량의 크레인사양중량 초과여부를 Check
				ydL3HdRsCd = "FU29";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 사양중량 초과";
			} else if (!"".equals(ydCrnSchId) && !isHandSch){
				//2023.02.16 연주 김충만 계장 요청. 강제권상 크레인 작업지시 있을시,핸드스카핑작업아니라면 추가 작업지시 생성 못하도록 요청
				ydL3HdRsCd = "FU30";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 작업지시 존재";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 4. 스케줄기준 정보 Check
			**********************************************************/
			String ydGp          = ydUpWrLoc.substring(0,1); //야드구분
			String ydBayGp       = ydUpWrLoc.substring(1,2); //야드동구분
			//야드스케쥴코드 : 야드+동+FU(강제권상)+마그네틱크레인번호+M(이적)+M(분할없음)
			String ydSchCd       = ydGp + ydBayGp + "FU0" + ydEqpId.substring(5, 6) + "MM";
			String ydSchProhExn  = "";  //야드스케쥴금지유무
			String ydWrkCrnPrior = "1"; //야드작업크레인우선순위

			jrParam.setField("V_YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			jsChk = commDao.getStat("SchCd", jrParam);

			if (jsChk.size() > 0) {
				ydSchProhExn  = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN" ));
				//야드작업크레인우선순위 : 야드스케쥴코드에 해당하는 작업크레인의 우선순위이므로
				//실제 요청한 크레인의 우선순위와 다를 수 있음
				ydWrkCrnPrior = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"));
			}

			if ("".equals(ydSchProhExn)) {
				//스케줄기준 Table 정보 Check
				ydL3HdRsCd = "FU31";
				ydL3Msg    = "오류:스케쥴코드[" + ydSchCd + "] 정보 없음";
			} else if ("Y".equals(ydSchProhExn)) {
				//스케줄 금지여부 Check
				ydL3HdRsCd = "FU32";
				ydL3Msg    = "오류:스케쥴코드[" + ydSchCd + "] 기동금지";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 5. 현재 크레인스케줄의 야드작업진행상태 Update
			*    설비상태가 권상작업지시[1]이고  야드작업진행상태가 권상지시,
			*    권상완료, 권하지시[1,2,3] 이면 야드작업진행상태를
			*    명령선택대기[W]로 수정
			*   (작업지시가 정상 처리되면 크레인의 설비상태가 권상작업지시[1]로
			*    변경되므로 설비상태를 대기[W]로 변경하는 부분은 삭제)
			**********************************************************/
			if ("1".equals(ydEqpStat) && !"".equals(ydCrnSchId)) {
				jrParam.setField("V_YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID(현재)
				jrParam.setField("V_YD_WRK_PROG_STAT", "W"       ); //명령선택대기

				//현재 크레인스케줄 야드작업진행상태 수정
				commDao.updStat("CrnSchWrkProg", jrParam);
			}

			/**********************************************************
			* 6. 작업예약 등록 및 저장품, 적치단 정보 수정
			**********************************************************/
			//작업예약ID 생성
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");

			if ("".equals(ydWbookId)) {
				ydL3Msg = "오류:작업예약ID 생성 실패";
				resMsg.setField("YD_L3_HD_RS_CD", "FU41" ); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//작업예약 등록
			jrParam.setField("V_YD_WBOOK_ID"       , ydWbookId    ); //야드작업예약ID
			jrParam.setField("V_YD_GP"             , ydGp         ); //야드구분
			jrParam.setField("V_YD_BAY_GP"         , ydBayGp      ); //야드동구분
			jrParam.setField("V_YD_SCH_CD"         , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("V_YD_SCH_PRIOR"      , ydWrkCrnPrior); //야드스케쥴우선순위
			jrParam.setField("V_YD_SCH_PROG_STAT"  , "W"          ); //야드스케쥴진행상태(스케줄수행대기)
			jrParam.setField("V_YD_SCH_ST_GP"      , "M"          ); //야드스케쥴기동구분(Manual 작업)
			jrParam.setField("V_YD_SCH_REQ_GP"     , "X"          ); //야드스케쥴요청구분(강제권상요구)
			jrParam.setField("V_YD_AIM_YD_GP"      , ydGp         ); //야드목표야드구분
			jrParam.setField("V_YD_AIM_BAY_GP"     , ydBayGp      ); //야드목표동구분
			jrParam.setField("V_YD_TO_LOC_DCSN_MTD", "S"          ); //야드TO위치결정방법(스케줄기준적용)

			commDao.insSlabYd("WrkBook", jrParam);

			//작업예약재료 등록
			jrParam.setField("V_STL_NO"        , stlNo     ); //재료번호
			jrParam.setField("V_YD_STK_LYR_NO" , ydStkLyrNo); //야드적치단번호
			jrParam.setField("V_YD_UP_COLL_SEQ", "1"       ); //야드권상모음순서

			commDao.insSlabYd("WrkBookMtl", jrParam);

			//저장품 작업예약정보 수정
			rcv2Dao.updY1YDL012("ST", jrParam);

			//2023.02.15 수정 --중복단작업지시 방지를 위해 YDYDJ400 전문으로 대체
			/**********************************************************
			* 7. 크레인스케줄(YDYDJ400) 전송
			***********************************************************/
			//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
	/*
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId ); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_ST_GP" , "A"       ); //야드스케쥴기동구분(Auto)
			jrYdMsg.setField("V_MODIFIER"   , modifier  ); //수정자
			//크레인스케줄 전문
			JDTORecord jrRtn = slabUtils.addSndData(slabComm.getCrnSchMsg(jrYdMsg));
			
			*/
			//적치단 야드적치단재료상태 수정
			jrParam.setField("V_YD_STK_LYR_MTL_STAT", "U"); //권상대기

			commDao.updStat("StkLyrMtl", jrParam);

			/**********************************************************
			* 7. 크레인스케줄 등록
			**********************************************************/
			//크레인스케줄ID 생성
			ydCrnSchId = commDao.getSeqId(logId, methodNm, "CrnSch");

			if ("".equals(ydCrnSchId)) {
				ydL3Msg = "오류:크레인스케줄ID 생성 실패";
				resMsg.setField("YD_L3_HD_RS_CD", "FU51" ); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			jrParam.setField("V_YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

			//크레인스케줄 등록
			rcv2Dao.updY1YDL012("CS", jrParam);

			//크레인작업재료 등록
			rcv2Dao.updY1YDL012("CM", jrParam);

			/**********************************************************
			* 9. 크레인작업지시(YDY1L004) 전문 조회
			**********************************************************/
			//설비 야드설비상태 수정
			jrParam.setField("V_YD_EQP_STAT", "1"); //권상작업지시

			commDao.updStat("Eqp", jrParam);

			//크레인작업지시 전문 조회
			JDTORecord jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L004", jrParam));

			//크레인정보 Flex Server로 전송
			slabComm.sndToFlexData(resMsg);
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(slabUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YdCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { slabComm.getYDY1L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3Take-Out완료(Y3YDL012)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL012(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3Take-Out완료[SlabYdL2RcvSeEJB.rcvY3YDL012] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {

			//Take-Out완료 처리
			return this.rcvC3YDL004(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3Take-In완료(Y3YDL013)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL013(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3Take-In완료[SlabYdL2RcvSeEJB.rcvY3YDL013] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {

			//Take-In완료 처리
			return this.rcvC3YDL005(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Y3대차이동실적(Y3YDL014)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL014(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "Y3대차이동실적[SlabYdL2RcvSeEJB.rcvY3YDL014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			//대차이동실적 처리
			return this.rcvC3YDL007(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : L2픽업크레인 지시정보(Y3YDL015)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY3YDL015(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "L2픽업크레인 지시정보[SlabYdL2RcvSeEJB.rcvY3YDL015] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){

         }else{
       	  return null;
         }
			
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo  	= slabUtils.trim(rcvMsg.getFieldString("STL_NO" )); //재료번호
			String cancelYN = slabUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //취소유무
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자(Backup Only)
			String ydStkColGp = "";
			String ydStkBedNo = "";
			if ("".equals(modifier)) { modifier = msgId; }

			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (stlNo.length() > 11) {
				throw new Exception("재료번호(STL_NO) 이상 [" + stlNo + "]");
			}

			
			/**********************************************************
			* 2. 해당 슬라브가 적치되어있는 Bed 검색
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_STL_NO"    , stlNo                 ); //재료번호
			
			JDTORecordSet jsChk = commDao.getStrLocInfo2("stock",jrParam);
			
			if (jsChk.size() > 0) {
				ydStkColGp  = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_STK_COL_GP" ));
				ydStkBedNo = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_STK_BED_NO"));
			} else {
				throw new Exception("슬라브 저장 위치 이상");
			}

			
			/**********************************************************
			* 3. 2후판 Pickup Bed Table에 Flag 초기화
			**********************************************************/
			jrParam.setField("V_YD_STK_COL_GP", "DBPU05"); //적치열

			//해당 베드 Flag 초기화
			rcv2Dao.updY3YDL015("BC", jrParam);
			
			
			/**********************************************************
			* 4. 슬라브가 적치되어있는 베드에 Flag update
			**********************************************************/
			jrParam.setField("V_YD_STK_COL_GP", ydStkColGp); //적치열
			jrParam.setField("V_YD_STK_BED_NO", ydStkBedNo); //적치Bed

			//해당 베드 Flag 초기화
			if("N".equals(cancelYN)) {
				rcv2Dao.updY3YDL015("BU", jrParam);
			}

			
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

}
