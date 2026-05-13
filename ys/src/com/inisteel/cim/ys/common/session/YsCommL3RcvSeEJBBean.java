/**
 * @(#)YsCommL3RcvSeEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      야드공통 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.common.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.bl.session.BlYsComm;
import com.inisteel.cim.ys.bt.session.BtYsComm;
import com.inisteel.cim.ys.gds.session.GdsYsComm;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

/**
 *      [A] 클래스명 : 야드공통 L3수신 처리
 *
 * @ejb.bean name="YsCommL3RcvSeEJB" jndi-name="YsCommL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/

public class YsCommL3RcvSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private BlYsComm blYsComm = new BlYsComm();
	private BtYsComm btYsComm = new BtYsComm();
	private GdsYsComm gdsYsComm = new GdsYsComm();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	/**	
	 * [A] 오퍼레이션명 : 소재포인트 요구 (rcvYSYSJ901)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ901(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "내부소재포인트요구[YsCommL3RcvSeEJB.rcvYSYSJ901] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "내부소재차량도착Point요구 수신 ", rcvMsg);

//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)

// 2025.09.12 YsCommCarMvSeEJB -> YsCommCarMvFaEJB 사용으로 변경			
//    		EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
    		EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvFaEJB", this);
    		jrRtn = (JDTORecord)ejbConn.trx("rcvTSYSJ002", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
    		
    		
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**	
	 * [A] 오퍼레이션명 : 차량입동지시 요구 (rcvYSYSJ801)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ801(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "내부차량입동지시 요구[YsCommL3RcvSeEJB.rcvYSYSJ801] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;
		JDTORecord sndRecord = null;

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "내부차량입동지시 요구 수신 ", rcvMsg);

//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
    		EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
    		jrRtn = (JDTORecord)ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
    		
    		sndRecord = commUtils.addSndData(sndRecord,jrRtn);	
			
    		commUtils.printParam(logId + "내부차량입동지시 요구 수신 ", sndRecord);
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	
	/**	
	 * [A] 오퍼레이션명 : 메일발송 메세지 송신 (rcvYSYSJ802)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return void
	 * @throws DAOException
	*/
	public void rcvYSYSJ802(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "메일발송 메세지 송신[YsCommL3RcvSeEJB.rcvYSYSJ802] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			String sYdGp 	= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String sSStlNo 	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO"));
			
			if("B".equals(sYdGp)){
				this.getSstlNoAbOccurSend_01(sSStlNo,logId);
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	
	/**
	 *      [A] 오퍼레이션명 : 블룸 저장위치 변경이력 메일링 송신
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	*/
	public void getSstlNoAbOccurSend_01(String sSstlNo,String logId) throws DAOException {
		try {
				Object[] objs = null;
				objs = new Object[]{sSstlNo};
				
				commUtils.printLog(logId, "▒  블룸 저장위치 변경이력 메일발송 시작 코일 : " +  sSstlNo,"");
				
				//메일내용등록
				int iRow = commDao.insSstlNoAbOccurMailContents_01(objs);								//메일본문내용생성
				commUtils.printLog(logId, "▒ 블룸 저장위치 변경이력 메일 본문등록(TB_HR_COMMTEMPINFO)완료 : " +  iRow +" 건","");
				
				//메일등록 MaxSeqNo 조회
				JDTORecordSet jrResult2 =  commDao.getHrMailContents(new Object[]{});		//메일정보 조회		
				
				String sSeqNo   	= jrResult2.getRecord(0).getFieldString("SEQ_NO");
				String sTitle 		= jrResult2.getRecord(0).getFieldString("TITLE");
				String sSendAddress = jrResult2.getRecord(0).getFieldString("SEND_ADDRESS");
				String sSendName    = jrResult2.getRecord(0).getFieldString("SEND_NAME");
				String sSendGroup  	= jrResult2.getRecord(0).getFieldString("SEND_GROUP");
				
				commUtils.printLog(logId, "▒  블룸 저장위치 변경이력 메일 시퀀스        : " +  sSeqNo,"");
				commUtils.printLog(logId, "▒  블룸 저장위치 변경이력 메일 제목            : " +  sTitle,"");
				commUtils.printLog(logId, "▒  블룸 저장위치 변경이력 메일 보내는 주소 : " +  sSendAddress,"");
				commUtils.printLog(logId, "▒  블룸 저장위치 변경이력 메일 보내는 사람 : " +  sSendName,"");
				commUtils.printLog(logId, "▒  블룸 저장위치 변경이력 메일 보내는 그룹 : " +  sSendGroup,"");
				
				JDTORecord paramR = JDTORecordFactory.getInstance().create();
				
				paramR.setField("SEQ_NO", sSeqNo);					//메일내용시퀀스
				paramR.setField("TITLE", sTitle);					//제목
				paramR.setField("SENDER_EADDRESS", sSendAddress);	//발송자메일
				paramR.setField("SENDER_NAME", sSendName);			//발송자
				paramR.setField("RECV_GR", sSendGroup);				//수신그룹
				
				//메일발송
				EJBConnector ejbConn = new EJBConnector("default", "HrSendEmailSeEJB", this);
				ejbConn.trx("sendMailComm", new Class[] { JDTORecord.class }, new Object[] { paramR });
				
				commUtils.printLog(logId, "▒  블룸 저장위치 변경이력  메일발송 종료 ","");
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
			
	
	/**	
	 * [A] 오퍼레이션명 :  내부 크레인 작업지시 요구 (rcvYSYSJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvYSYSJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인 작업지시 요구[YsCommL3RcvSeEJB.rcvYSYSJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "크레인 작업지시 요구 수신 ", rcvMsg);

//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
	    	//크레인 작업지시 요구호출 TC :/** 블룸L2(N1),빌렛 L2(N2),선재L2(E:N3),봉강L2(B:N4),선재자동L2(D:N5),봉강자동L2(A:N6) */
			//크레인설비ID
	    	String szEqpId 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       ));
	    	
	    	if(!szEqpId.equals("")) {
	    		
				String szJMS_TC_CD = "";
				String szEjb = "";
				
		    	JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setResultCode(logId);	//Log ID
		    	recInTemp.setResultMsg(methodNm);	//Log Method Name	
		    	
		    	if(szEqpId.startsWith("B") ){	    		
		    		szJMS_TC_CD = "N1YSL004";
		    		szEjb = "BlYsL2RcvSeEJB";
		    	}else if(szEqpId.startsWith("C")){
		    		szJMS_TC_CD = "N2YSL004";
		    		szEjb = "BtYsL2RcvSeEJB";
		    	}else if(szEqpId.startsWith("KACRA2")){
		    		szJMS_TC_CD = "N4YSL004";
		    		szEjb = "GdsYsL2RcvSeEJB";
		    	}else if(szEqpId.startsWith("KATC")){
		    		szJMS_TC_CD = "N4YSL004";
		    		szEjb = "GdsYsL2RcvSeEJB";
				}else if(szEqpId.startsWith("KACRA1")){
		    		szJMS_TC_CD = "N6YSL004";
		    		szEjb = "GdsYsL2RcvSeEJB";
		    	}else if(szEqpId.startsWith("KB")){
		    		szJMS_TC_CD = "N4YSL004";
		    		szEjb = "GdsYsL2RcvSeEJB";
		    	}else if(szEqpId.startsWith("KDCRD")){
		    		szJMS_TC_CD = "N5YSL004";
		    		szEjb = "GdsYsL2RcvSeEJB";
		    	}else if(szEqpId.startsWith("KE")){
		    		szJMS_TC_CD = "N3YSL004";
		    		szEjb = "GdsYsL2RcvSeEJB";
		    	}else if(szEqpId.startsWith("GE")){
// 2025.08.25 특수강 정정 야드 : N7 추가
					szJMS_TC_CD = "N7YSL204";
		    		szEjb 		= "CbtYsL2RcvSeEJB";
		    	}else if(szEqpId.startsWith("GD")){
// 2026.01.26 소형야드 : N7 추가
					szJMS_TC_CD = "N7YSL304";
		    		szEjb 		= "SbrYsL2RcvSeEJB";
		    	}else if(szEqpId.startsWith("GF")){
// 2025.08.25 대형 봉강 옥외 야드 : N8 추가
					szJMS_TC_CD = "N8YSL004";
		    		szEjb 		= "EbtYsL2RcvSeEJB";
		    		
		    	}
		    	
		    	if(!szJMS_TC_CD.equals("")) {
			    	recInTemp.setField("JMS_TC_CD"       , szJMS_TC_CD) ;	//크레인작업지시요구
			    	recInTemp.setField("YD_EQP_ID"       , commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"         )));	//야드설비ID
			    	recInTemp.setField("YD_WRK_PROG_STAT", commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"  )));	//야드작업진행상태(권상작업지시)
			    	recInTemp.setField("YD_SCH_CD"       , commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       	)));	//야드스케쥴코드
			    	recInTemp.setField("YD_CRN_SCH_ID"   , commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"    	)));	//야드크레인스케쥴ID
			    	
		    		EJBConnector ejbConn = new EJBConnector("default", szEjb, this);
		    		jrRtn = (JDTORecord)ejbConn.trx("rcv"+szJMS_TC_CD, new Class[] { JDTORecord.class }, new Object[] { recInTemp });
		    	}	
	    		
	    	}
	    	
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
		
	/**
	 *      [A] 오퍼레이션명 : 블룸 CARRY OUT 요구(YSYSJ113)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ113(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "블룸 입고/장입 CARRY OUT요구 [YsCommL3RcvSeEJB.rcvYSYSJ113] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsResult  = null;
		JDTORecordSet rsResult1 = null;
		JDTORecord    outResult = null;
		JDTORecord    outResult1 = null;
		JDTORecord    recInTemp = null;
		JDTORecord    jrYdMsg   = null;
		JDTORecord    jrRtn     = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID
			String ysStkBedNo = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")); //야드적치Bed번호
			String ysStkLyrNo = commUtils.trim(rcvMsg.getFieldString("YS_STK_LYR_NO")); //야드적치단번호
			String ydSchStGp  = commUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" )); //야드스케쥴기동구분
			String modifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
			} else if ("".equals(ysStkBedNo)) {
				throw new Exception("적치Bed번호(YS_STK_BED_NO) 없음");
			}
			
			String ydAimYdGp   = ""; //야드목표야드구분
			String ydAimBayGp  = ""; //야드목표동구분
			String ydSchCd  =  "";
			String ydSchLocGuide = "";
			String ydCommAsgnGp = "";
			
			/**********************************************************
			*  - 작업예약등록, 크레인스케줄 전문 전송
			**********************************************************/

			recInTemp = JDTORecordFactory.getInstance().create();
			
			//야드스케쥴코드생성 및 //야드스케쥴금지유무 조회
			if(ydEqpId.equals("BAWB01")){
				
				//ydSchCd  =  ydEqpId + "LM";   //입고 CARRY OUT : BAWB01LM
				recInTemp.setField("YS_STK_COL_GP"     , ydEqpId       ); //야드구분
				recInTemp.setField("YS_STK_BED_NO"     , ysStkBedNo    ); //YS_STK_BED_NO구분
				
				rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getSchCdWB01L", logId, methodNm, "입고스케줄코드지정");
		    	if (rsResult == null || rsResult.size() <= 0) {
		    		throw new Exception("입고 스케쥴 ID 지정 이상 : [com.inisteel.cim.ys.bl.dao.BlYsDAO.getSchCdWB01L]");
				}
				rsResult.first();
				outResult = rsResult.getRecord();
				ydSchCd = outResult.getFieldString("SCH_CD"); //BAWB01LL or BAWB01LR
				
			} else {
				ydSchCd  =  ydEqpId + "LM";   //장입이상재 CARRY OUT : BBLB01LM
			}
			
			//스케줄코드로 스케줄기준Table조회
			recInTemp.setField("YD_SCH_CD", ydSchCd);
	    	
	    	
	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		throw new Exception("스케쥴 ID 이상 : [" + ydSchCd + "]");
			}
			
			//레코드 추출  
			rsResult.first();
			outResult = rsResult.getRecord();
			String ydSchPrior = outResult.getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
	
// 초말 주편 추가
// 초말 주편 삭제 12.01			
/*
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YS_STK_COL_GP"     , ydEqpId       ); //야드구분
			recInTemp.setField("YS_STK_BED_NO"     , ysStkBedNo    ); //YS_STK_BED_NO구분
			recInTemp.setField("YS_STK_LYR_NO"     , ysStkLyrNo        ); //단구분
			rsResult1 = commDao.select(recInTemp, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getCommAsgnGp", logId, methodNm, "스케줄 기준 조회"); 
	    	
	    	if (rsResult1 == null || rsResult1.size() <= 0) {
	    		ydCommAsgnGp = "N";
			} else {
			
				//레코드 추출  
				rsResult1.first();
				outResult1 = rsResult1.getRecord();
				ydCommAsgnGp  = outResult1.getFieldString("BLOOM_ASGN_GP_YN"); //초말 주편가이드
				ydSchLocGuide = outResult1.getFieldString("YD_TO_LOC_GUIDE"); //초말 주편가이드
			}
*/	    	
			//작업예약ID 조회
			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			
			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID 생성 실패");
			}
			
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
			recInTemp.setField("YD_GP"             , ydEqpId.substring(0,1)          ); //야드구분
			recInTemp.setField("YD_BAY_GP"         , ydEqpId.substring(1,2)       ); //야드동구분
			recInTemp.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
			recInTemp.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
			recInTemp.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
			recInTemp.setField("YD_SCH_ST_GP"      , "M"           ); //야드스케쥴기동구분(Manual)
			recInTemp.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
			recInTemp.setField("YD_AIM_YD_GP"      , ydAimYdGp     ); //야드목표야드구분
			recInTemp.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
/*			if(ydCommAsgnGp.equals("Y")) {
				recInTemp.setField("YD_TO_LOC_GUIDE"     , ydSchLocGuide    ); //TO위치 가이드
				recInTemp.setField("YD_TO_LOC_DCSN_MTD"  , "F"    ); 
			}
*/
			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
			if (ins_cnt <= 0) {
				throw new JDTOException("작업예약 등록실패");
			}
			
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtlByStkLyr 
			--적재위치대상재 작업예약재료 등록 
			MERGE INTO TB_YS_WRKBOOKMTL WM USING (
			SELECT :V_YD_WBOOK_ID   AS YD_WBOOK_ID --야드작업예약ID
			      ,SL.SSTL_NO                       --재료번호
			      ,:V_MODIFIER      AS MODIFIER    --수정자
			      ,SYSDATE          AS MOD_DDTT    --수정일시
			      ,'N'              AS DEL_YN      --삭제유무
			      ,SL.YS_STK_COL_GP                --야드적치열구분
			      ,SL.YS_STK_BED_NO                --야드적치Bed번호
			      ,SL.YS_STK_LYR_NO                --야드적치단번호
			      ,SL.YS_STK_SEQ_NO                --야드적치SEQ번호
			  FROM TB_YS_STKLYR SL
			 WHERE SL.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND SL.YS_STK_BED_NO = :V_YS_STK_BED_NO
			   AND SL.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
			   AND SL.SSTL_NO IS NOT NULL
			) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.SSTL_NO = DD.SSTL_NO)
			WHEN NOT MATCHED THEN
			INSERT (WM.YD_WBOOK_ID  , WM.SSTL_NO       , WM.REGISTER      , WM.REG_DDTT     ,
			        WM.MODIFIER     , WM.MOD_DDTT     , WM.DEL_YN        , WM.YS_STK_COL_GP,
			        WM.YS_STK_BED_NO, WM.YS_STK_LYR_NO, WM.YS_STK_SEQ_NO)
			VALUES (DD.YD_WBOOK_ID  , DD.SSTL_NO       , DD.MODIFIER      , DD.MOD_DDTT     ,
			        DD.MODIFIER     , DD.MOD_DDTT     , DD.DEL_YN        , DD.YS_STK_COL_GP,
			        DD.YS_STK_BED_NO, DD.YS_STK_LYR_NO, DD.YS_STK_SEQ_NO)
            */
			        
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
			recInTemp.setField("YS_STK_COL_GP"     , ydEqpId       ); //야드구분
			recInTemp.setField("YS_STK_BED_NO"     , ysStkBedNo    ); //YS_STK_BED_NO구분
			recInTemp.setField("YS_STK_LYR_NO"     , ysStkLyrNo        ); //단구분
			ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtlByStkLyr", logId, methodNm, "TB_YS_WRKBOOKMTL");
			if (ins_cnt <= 0) {
				throw new JDTOException("작업예약 재료 등록실패");
			}
			
			/**********************************************************
			* 2.2 크레인스케줄(YSYSJ102) 전문 호출
			**********************************************************/
			jrYdMsg = JDTORecordFactory.getInstance().create();
			jrRtn = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
			jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
			jrYdMsg.setField("MODIFIER"   , modifier ); //수정자

			jrRtn = commUtils.addSndData(blYsComm.getCrnSchMsg(jrYdMsg));
	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 블름보급Carry-In작업요구(YSYSJ114)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ114(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "블름보급Carry-In작업요구 [YsCommL3RcvSeEJB.rcvYSYSJ114] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsResult  = null;
		JDTORecord    outResult = null;
		JDTORecord    recInTemp  = null;
		JDTORecord    jrYdMsg  = null;
		JDTORecord    jrRtn  = null;
		String[] szSSTL_NO         = new String[3];
		String[] szYD_UP_COLL_SEQ  = new String[3];
		JDTORecord recRtn      = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			/*
			 * 2016.09.25 윤재광
			 * 블룸소재 선 장입작업요구시 해당 EJB 호출
			 */
			String sL3PreWork = commUtils.trim(rcvMsg.getFieldString("L3_PRE_WORK" ));
			if("Y".equals(sL3PreWork)){
				
				JDTORecord jrParam      = JDTORecordFactory.getInstance().create();
				
				jrParam.setField("JMS_TC_CD"			, "M3YSL101");
				jrParam.setField("YD_EQP_ID"			, "BBTZ01");
				jrParam.setField("YS_STK_BED_NO"		, "01");
				jrParam.setField("L3_PRE_WORK"  		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
				JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvM3YSL101", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				return jrRst;
			}
			
			//수신 항목 값
			String msgId       = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydschcd     = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    ));  //야드설비ID
			String ysstkcolgp  = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_GP")); 
			String ysstkbedno  = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO" )); 
			String modifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
			int ydcarryinsh    = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_CARRY_IN_SH"),"0"));	// 재료매수
			
			commUtils.printParam(logId, rcvMsg);
			
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydschcd)) {
				throw new Exception("야드설비id 가 없습니다.");
			} else if (ydcarryinsh == 0) {
				throw new Exception("적치매수이상.");
			} else if ("".equals(ysstkcolgp)) {
				throw new Exception("특수강야드적치COL번호 값이 없습니다.");
			} else if ("".equals(ysstkbedno)) {
				throw new Exception("특수강야드적치Bed번호 값이 없습니다.");
			}

			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= ydcarryinsh; Loop_i++){
				
				//재료번호
				szSSTL_NO[Loop_i-1] = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+ Loop_i));
				if(szSSTL_NO[Loop_i-1].equals("")){
					throw new Exception("[전문 이상] "+ Loop_i + "번째 재료 번호가 없습니다.");
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i-1] = commUtils.trim(rcvMsg.getFieldString("YD_UP_COLL_SEQ"+ Loop_i)); 
				if(szYD_UP_COLL_SEQ[Loop_i-1].equals("")){
					throw new Exception("[전문 이상] "+ Loop_i + " 대한 권상모음순서가 없습니다.");
				}
			}


			
			/**********************************************************
			*  - 작업예약등록, 크레인스케줄 전문 전송
			**********************************************************/
			//스케줄코드로 스케줄기준Table조회
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_SCH_CD", ydschcd);
	    	
	    	
	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule 
			SELECT A.YD_GP
			      ,A.YD_BAY_GP
			      ,YD_SCH_CD
			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
			        END AS YD_WRK_CRN
			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
			        END AS YD_WRK_CRN_PRIOR
			      ,YD_SCH_CD_NM
			      ,YD_SCH_CONTENTS
			      ,YD_SCH_PROH_EXN 
			    FROM TB_YS_SCHRULE A
			        ,(
			            SELECT YD_GP
			                  ,YD_BAY_GP
			                  ,YD_SCH_GP
			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
			            FROM   (
			                        SELECT YD_EQP_ID
			                              ,YD_GP
			                              ,YD_BAY_GP
			                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
			                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
			                              ,YD_EQP_GP AS YD_SCH_GP
			                        FROM   TB_YS_EQP
			                        WHERE  YD_EQP_GP IN ('CR','SC')
			                   )
			            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
			         ) B
			    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
			    AND   A.YD_DATA_GP = 'M'
			    AND   A.YD_SCH_GP = B.YD_SCH_GP
			    AND   A.YD_GP = B.YD_GP
			    AND   A.YD_BAY_GP = B.YD_BAY_GP
			    AND   A.YD_CRN_STAT1 = B.STAT1
			    AND   A.YD_CRN_STAT2 = B.STAT2
			    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
	    	*/	    
			commUtils.printLog(logId, "B", "SL");
		
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		throw new Exception("스케쥴 ID 이상 : [" + ydschcd + "]");
			}
			
			//레코드 추출  
			rsResult.first();
			outResult = rsResult.getRecord();
			String ydSchPrior = outResult.getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			
			//작업예약ID 조회
			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID 생성 실패");
			}
			
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
			recInTemp.setField("YD_GP"             , ysstkcolgp.substring(0,1)          ); //야드구분
			recInTemp.setField("YD_BAY_GP"         , ysstkcolgp.substring(1,2)       ); //야드동구분
			recInTemp.setField("YD_SCH_CD"         , ydschcd       ); //야드스케쥴코드
			recInTemp.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
			recInTemp.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
			recInTemp.setField("YD_SCH_ST_GP"      , "A"           ); //야드스케쥴기동구분(Manual)
			recInTemp.setField("YD_SCH_REQ_GP"     , "U"           ); //야드스케쥴요청구분(보급)
			recInTemp.setField("YD_TO_LOC_DCSN_MTD", "F");								//야드To위치결정방법
			recInTemp.setField("YD_TO_LOC_GUIDE", 	ysstkcolgp + ysstkbedno); //야드To위치Guide
	
			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
			if (ins_cnt <= 0) {
				throw new JDTOException("작업예약 재료 등록실패");
			}
			for (int Loop_i = 1; Loop_i <= ydcarryinsh; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				outResult = JDTORecordFactory.getInstance().create();
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.

				recInTemp.setField("SSTL_NO", szSSTL_NO[Loop_i-1]);
				
				 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrStlNoCU 
				SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
				      ,YS_STK_BED_NO            AS YS_STK_BED_NO
				      ,YS_STK_LYR_NO            AS YS_STK_LYR_NO
				      ,YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
				      ,REGISTER                 AS REGISTER
				      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
				      ,MODIFIER                 AS MODIFIER
				      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
				      ,DEL_YN                   AS DEL_YN
				      ,SSTL_NO                   AS SSTL_NO
				      ,YD_STK_LYR_ACT_STAT      AS YD_STK_LYR_ACT_STAT
				      ,YD_STK_LYR_MTL_STAT      AS YD_STK_LYR_MTL_STAT
				      ,YD_STK_LYR_XAXIS         AS YD_STK_LYR_XAXIS
				      ,YD_STK_LYR_YAXIS         AS YD_STK_LYR_YAXIS
				      ,YD_STK_LYR_ZAXIS         AS YD_STK_LYR_ZAXIS
				  FROM TB_YS_STKLYR
				 WHERE SSTL_NO = :V_SSTL_NO
				   AND NVL(YD_STK_LYR_MTL_STAT, '*') IN ('C','U') 
				   AND DEL_YN='N'
				   AND ROWNUM = 1  
				   */
				
				rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrStlNoCU", logId, methodNm, "적재위치조회 조회");
				if (rsResult.size() <= 0) {
					throw new JDTOException("적치단에 재료["+szSSTL_NO[Loop_i]+"]가 존재하지 않습니다.");
				}
				rsResult.first();
				outResult = rsResult.getRecord();
				
				recInTemp = JDTORecordFactory.getInstance().create();
				//작업예약 등록
				recInTemp.setField("SSTL_NO"           , szSSTL_NO[Loop_i-1]     ); //재료번호
				recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
				recInTemp.setField("MODIFIER"          , modifier      ); //수정자
				recInTemp.setField("YS_STK_COL_GP"     , ysstkcolgp       ); //야드구분
				recInTemp.setField("YS_STK_BED_NO"     , ysstkbedno    ); //YS_STK_BED_NO구분
				recInTemp.setField("YS_STK_LYR_NO"     , commUtils.trim(outResult.getFieldString("YS_STK_LYR_NO" ))); 
				recInTemp.setField("YS_STK_SEQ_NO"     , commUtils.trim(outResult.getFieldString("YS_STK_SEQ_NO" ))); 
				recInTemp.setField("YD_UP_COLL_SEQ"    , szYD_UP_COLL_SEQ[Loop_i-1]);
				
				ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "작업예약 재료");
				if (ins_cnt <= 0) {
					throw new JDTOException("작업예약 재료 등록실패");
				}
			}	
			
			/**********************************************************
			* 2.2 크레인스케줄(YSYSJ102) 전문 호출
			**********************************************************/
			// 장입은 스케줄이 1개만 기동됨
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchCnt 
			SELECT COUNT(*)  AS SCH_CNT
			  FROM TB_YS_CRNSCH 
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD 
			*/   
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			outResult = JDTORecordFactory.getInstance().create();
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_SCH_CD"           , ydschcd     ); 
			rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchCnt", logId, methodNm, "CRN 스케줄 COUNT"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		throw new Exception("CRN 스케줄 COUNT 이상 : [" + ydschcd + "]");
			}
			
			//레코드 추출  
			rsResult.first();
			outResult = rsResult.getRecord();
			int ydSchCnt= Integer.parseInt(outResult.getFieldString("SCH_CNT")); //야드스케쥴우선순위			
			
			if (ydSchCnt == 0) {			
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrRtn = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
				//jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId	); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"    , ydschcd 	); //야드스케쥴코드
				jrYdMsg.setField("YD_SCH_ST_GP" , "U"		); //야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP", "L"     	); //야드스케쥴요청구분(인출)
				jrYdMsg.setField("MODIFIER"   , modifier 	); //수정자
	
				jrRtn = commUtils.addSndData(blYsComm.getCrnSchMsg(jrYdMsg));
			}	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	/**
	 *      [A] 오퍼레이션명 : 빌렛 CARRY OUT 요구(YSYSJ213)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ213(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "빌렛입고 CARRY OUT요구 [YsCommL3RcvSeEJB.rcvYSYSJ213] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsResult  = null;
		JDTORecord    outResult = null;
		JDTORecord    recInTemp  = null;
		JDTORecord    jrYdMsg  = null;
		JDTORecord    jrRtn  = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID : CACV01,CBCV01
			String ysStkBedNo = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")); //야드적치Bed번호
			String ysStkLyrNo = commUtils.trim(rcvMsg.getFieldString("YS_STK_LYR_NO")); //야드적치단번호
			String ydSchStGp  = commUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" )); //야드스케쥴기동구분
			String modifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
			} else if ("".equals(ysStkBedNo)) {
				throw new Exception("적치Bed번호(YS_STK_BED_NO) 없음");
			}
			
			String ydAimYdGp   = ""; //야드목표야드구분
			String ydAimBayGp  = ""; //야드목표동구분
			String ydSchCd  =  "";
			
			/**********************************************************
			*  - 작업예약등록, 크레인스케줄 전문 전송
			**********************************************************/

			//야드스케쥴코드생성 및 //야드스케쥴금지유무 조회
			ydSchCd  =  ydEqpId + "LM";   
			
			recInTemp = JDTORecordFactory.getInstance().create();
			//스케줄코드로 스케줄기준Table조회
			recInTemp.setField("YD_SCH_CD", ydSchCd);
	    	
	    	
	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule 
			SELECT A.YD_GP
			      ,A.YD_BAY_GP
			      ,YD_SCH_CD
			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
			        END AS WRK_CRN
			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
			        END AS YD_WRK_CRN_PRIOR
			      ,YD_SCH_CD_NM
			      ,YD_SCH_CONTENTS
			    FROM TB_YS_SCHRULE A
			        ,(
			            SELECT YD_GP
			                  ,YD_BAY_GP
			                  ,YD_SCH_GP
			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
			            FROM   (
			                        SELECT YD_EQP_ID
			                              ,YD_GP
			                              ,YD_BAY_GP
			                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
			                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
			                              ,YD_EQP_GP AS YD_SCH_GP
			                        FROM   TB_YS_EQP
			                        WHERE  YD_EQP_GP IN ('CR','SC')
			                   )
			            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
			         ) B
			    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
			    AND   A.YD_DATA_GP = 'M'
			    AND   A.YD_SCH_GP = B.YD_SCH_GP
			    AND   A.YD_GP = B.YD_GP
			    AND   A.YD_BAY_GP = B.YD_BAY_GP
			    AND   A.YD_CRN_STAT1 = B.STAT1
			    AND   A.YD_CRN_STAT2 = B.STAT2
			    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
	    	*/	    	 
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		throw new Exception("스케쥴 ID 이상 : [" + ydSchCd + "]");
			}
			
			//레코드 추출  
			rsResult.first();
			outResult = rsResult.getRecord();
			String ydSchPrior = outResult.getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			
			//작업예약ID 조회
			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID 생성 실패");
			}
			
			/*
			 * 장입이상재 처리를 위해 로직 추가(CALB02,CBLB02)
			 */
			String ysStkColGp 		= "";
			String ydToLocDcsnMtd 	= "";
			String ydToLocGuide		= "";
			
			if("CALB02".equals(ydEqpId)){		ysStkColGp = "CALB01"; ydToLocDcsnMtd = "F"; ydToLocGuide ="CA0601";
			}else if("CBLB02".equals(ydEqpId)){	ysStkColGp = "CBLB01"; ydToLocDcsnMtd = "F"; ydToLocGuide ="CB0601";
			}else{								ysStkColGp = ydEqpId;}
			
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
			recInTemp.setField("YD_GP"             , ydEqpId.substring(0,1)          ); //야드구분
			recInTemp.setField("YD_BAY_GP"         , ydEqpId.substring(1,2)       ); //야드동구분
			recInTemp.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
			recInTemp.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
			recInTemp.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
			recInTemp.setField("YD_SCH_ST_GP"      , "A"           ); //야드스케쥴기동구분(Manual)
			recInTemp.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
			recInTemp.setField("YD_AIM_YD_GP"      , ydAimYdGp     ); //야드목표야드구분
			recInTemp.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
			recInTemp.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법
			recInTemp.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide  ); //야드To위치Guide

			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
			if (ins_cnt <= 0) {
				throw new JDTOException("작업예약 재료 등록실패");
			}
						
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtlByStkLyr 
			--적재위치대상재 작업예약재료 등록 
			MERGE INTO TB_YS_WRKBOOKMTL WM USING (
			SELECT :V_YD_WBOOK_ID   AS YD_WBOOK_ID --야드작업예약ID
			      ,SL.SSTL_NO                       --재료번호
			      ,:V_MODIFIER      AS MODIFIER    --수정자
			      ,SYSDATE          AS MOD_DDTT    --수정일시
			      ,'N'              AS DEL_YN      --삭제유무
			      ,SL.YS_STK_COL_GP                --야드적치열구분
			      ,SL.YS_STK_BED_NO                --야드적치Bed번호
			      ,SL.YS_STK_LYR_NO                --야드적치단번호
			      ,SL.YS_STK_SEQ_NO                --야드적치SEQ번호
			  FROM TB_YS_STKLYR SL
			 WHERE SL.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND SL.YS_STK_BED_NO = :V_YS_STK_BED_NO
			   AND SL.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
			   AND SL.SSTL_NO IS NOT NULL
			) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.SSTL_NO = DD.SSTL_NO)
			WHEN NOT MATCHED THEN
			INSERT (WM.YD_WBOOK_ID  , WM.SSTL_NO       , WM.REGISTER      , WM.REG_DDTT     ,
			        WM.MODIFIER     , WM.MOD_DDTT     , WM.DEL_YN        , WM.YS_STK_COL_GP,
			        WM.YS_STK_BED_NO, WM.YS_STK_LYR_NO, WM.YS_STK_SEQ_NO)
			VALUES (DD.YD_WBOOK_ID  , DD.SSTL_NO       , DD.MODIFIER      , DD.MOD_DDTT     ,
			        DD.MODIFIER     , DD.MOD_DDTT     , DD.DEL_YN        , DD.YS_STK_COL_GP,
			        DD.YS_STK_BED_NO, DD.YS_STK_LYR_NO, DD.YS_STK_SEQ_NO)
            */
			
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
			recInTemp.setField("YS_STK_COL_GP"     , ysStkColGp    ); //야드구분
			recInTemp.setField("YS_STK_BED_NO"     , ysStkBedNo    ); //YS_STK_BED_NO구분
			recInTemp.setField("YS_STK_LYR_NO"     , ysStkLyrNo    ); //단구분
			ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtlByStkLyr", logId, methodNm, "TB_YS_WRKBOOKMTL");
			if (ins_cnt <= 0) {
				throw new JDTOException("작업예약 재료 등록실패");
			}
			
			/**********************************************************
			* 2.2 크레인스케줄(YSYSJ102) 전문 호출
			**********************************************************/
			jrYdMsg = JDTORecordFactory.getInstance().create();
			jrRtn = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
			jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
			jrYdMsg.setField("MODIFIER"   , modifier ); //수정자

			jrRtn = commUtils.addSndData(btYsComm.getCrnSchMsg(jrYdMsg));
	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 빌렛보급Carry-In작업요구(YSYSJ214)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ214(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "빌렛보급Carry-In작업요구 [YsCommL3RcvSeEJB.rcvYSYSJ214] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsResult  = null;
		JDTORecord    outResult = null;
		JDTORecord    recInTemp  = null;
		JDTORecord    jrYdMsg  = null;
		JDTORecord    jrRtn  = null;
		String[] szSSTL_NO         = new String[7];
		String[] szYD_UP_COLL_SEQ  = new String[7];
		try {
			commUtils.printLog(logId, methodNm, "S+");
			 
			//수신 항목 값
			String msgId       = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydschcd     = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    ));  //야드설비ID
			String ysstkcolgp  = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_GP")); 
			String ysstkbedno  = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO" )); 
			String modifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
			int ydcarryinsh    = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_CARRY_IN_SH"),"0"));	// 재료매수
			
			/*
			 * 2016.10.27 정종균
			 * 빌렛소재 선 장입작업요구시 해당 EJB 호출
			 */
			String sL3PreWork = commUtils.trim(rcvMsg.getFieldString("L3_PRE_WORK" ));
			if("Y".equals(sL3PreWork)){
				
				JDTORecord jrParam      = JDTORecordFactory.getInstance().create();
				
				jrParam.setField("JMS_TC_CD"			, "M5YSL102");
				jrParam.setField("YD_EQP_ID"			, ysstkcolgp.substring(0 , 2)+"TZ01");
				jrParam.setField("YS_STK_BED_NO"		, "01");
				jrParam.setField("L3_PRE_WORK"  		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvM5YSL102", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				return jrRst;
			}
			
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydschcd)) {
				throw new Exception("야드설비id 가 없습니다.");
			} else if (ydcarryinsh == 0) {
				throw new Exception("적치매수이상.");
			} else if ("".equals(ysstkcolgp)) {
				throw new Exception("특수강야드적치COL번호 값이 없습니다.");
			} else if ("".equals(ysstkbedno)) {
				throw new Exception("특수강야드적치Bed번호 값이 없습니다.");
			}
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= ydcarryinsh; Loop_i++){
				
				//재료번호
				szSSTL_NO[Loop_i-1] = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+ Loop_i));
				if(szSSTL_NO[Loop_i-1].equals("")){
					throw new Exception("[전문 이상] "+ Loop_i + "번째 재료 번호가 없습니다.");
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i-1] = commUtils.trim(rcvMsg.getFieldString("YD_UP_COLL_SEQ"+ Loop_i)); 
				if(szYD_UP_COLL_SEQ[Loop_i-1].equals("")){
					throw new Exception("[전문 이상] "+ Loop_i + " 대한 권상모음순서가 없습니다.");
				}
			}
			commUtils.printLog(logId, "A", "SL");

			
			/**********************************************************
			*  - 작업예약등록, 크레인스케줄 전문 전송
			**********************************************************/
			//스케줄코드로 스케줄기준Table조회
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_SCH_CD", ydschcd);
	    	
	    	
	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule 
			SELECT A.YD_GP
			      ,A.YD_BAY_GP
			      ,YD_SCH_CD
			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
			        END AS YD_WRK_CRN
			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
			        END AS YD_WRK_CRN_PRIOR
			      ,YD_SCH_CD_NM
			      ,YD_SCH_CONTENTS
			      ,YD_SCH_PROH_EXN 
			    FROM TB_YS_SCHRULE A
			        ,(
			            SELECT YD_GP
			                  ,YD_BAY_GP
			                  ,YD_SCH_GP
			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
			            FROM   (
			                        SELECT YD_EQP_ID
			                              ,YD_GP
			                              ,YD_BAY_GP
			                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
			                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
			                              ,YD_EQP_GP AS YD_SCH_GP
			                        FROM   TB_YS_EQP
			                        WHERE  YD_EQP_GP IN ('CR','SC')
			                   )
			            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
			         ) B
			    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
			    AND   A.YD_DATA_GP = 'M'
			    AND   A.YD_SCH_GP = B.YD_SCH_GP
			    AND   A.YD_GP = B.YD_GP
			    AND   A.YD_BAY_GP = B.YD_BAY_GP
			    AND   A.YD_CRN_STAT1 = B.STAT1
			    AND   A.YD_CRN_STAT2 = B.STAT2
			    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
	    	*/	    
			commUtils.printLog(logId, "B", "SL");
		
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		throw new Exception("스케쥴 ID 이상 : [" + ydschcd + "]");
			}
			
			//레코드 추출  
			rsResult.first();
			outResult = rsResult.getRecord();
			String ydSchPrior = outResult.getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			
			//작업예약ID 조회
			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID 생성 실패");
			}
			
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
			recInTemp.setField("YD_GP"             , ysstkcolgp.substring(0,1)          ); //야드구분
			recInTemp.setField("YD_BAY_GP"         , ysstkcolgp.substring(1,2)       ); //야드동구분
			recInTemp.setField("YD_SCH_CD"         , ydschcd       ); //야드스케쥴코드
			recInTemp.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
			recInTemp.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
			recInTemp.setField("YD_SCH_ST_GP"      , "A"           ); //야드스케쥴기동구분(Manual)
			recInTemp.setField("YD_SCH_REQ_GP"     , "U"           ); //야드스케쥴요청구분(보급)
			recInTemp.setField("YD_TO_LOC_DCSN_MTD", "F");								//야드To위치결정방법
			recInTemp.setField("YD_TO_LOC_GUIDE", 	ysstkcolgp + ysstkbedno); //야드To위치Guide
	
			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
			if (ins_cnt <= 0) {
				throw new JDTOException("작업예약 재료 등록실패");
			}
			for (int Loop_i = 1; Loop_i <= ydcarryinsh; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				outResult = JDTORecordFactory.getInstance().create();
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.

				recInTemp.setField("SSTL_NO", szSSTL_NO[Loop_i-1]);
				
				 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrStlNoCU 
				SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
				      ,YS_STK_BED_NO            AS YS_STK_BED_NO
				      ,YS_STK_LYR_NO            AS YS_STK_LYR_NO
				      ,YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
				      ,REGISTER                 AS REGISTER
				      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
				      ,MODIFIER                 AS MODIFIER
				      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
				      ,DEL_YN                   AS DEL_YN
				      ,SSTL_NO                   AS SSTL_NO
				      ,YD_STK_LYR_ACT_STAT      AS YD_STK_LYR_ACT_STAT
				      ,YD_STK_LYR_MTL_STAT      AS YD_STK_LYR_MTL_STAT
				      ,YD_STK_LYR_XAXIS         AS YD_STK_LYR_XAXIS
				      ,YD_STK_LYR_YAXIS         AS YD_STK_LYR_YAXIS
				      ,YD_STK_LYR_ZAXIS         AS YD_STK_LYR_ZAXIS
				  FROM TB_YS_STKLYR
				 WHERE SSTL_NO = :V_SSTL_NO
				   AND NVL(YD_STK_LYR_MTL_STAT, '*') IN ('C','U') 
				   AND DEL_YN='N'
				   AND ROWNUM = 1  
				   */
				
				rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrStlNoCU", logId, methodNm, "적재위치조회 조회");
				if (rsResult.size() <= 0) {
					throw new JDTOException("적치단에 재료["+szSSTL_NO[Loop_i]+"]가 존재하지 않습니다.");
				}
				rsResult.first();
				outResult = rsResult.getRecord();
				
				recInTemp = JDTORecordFactory.getInstance().create();
				//작업예약 등록
				recInTemp.setField("SSTL_NO"           , szSSTL_NO[Loop_i-1]     ); //재료번호
				recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
				recInTemp.setField("MODIFIER"          , modifier      ); //수정자
				recInTemp.setField("YS_STK_COL_GP"     , ysstkcolgp       ); //야드구분
				recInTemp.setField("YS_STK_BED_NO"     , ysstkbedno    ); //YS_STK_BED_NO구분
				recInTemp.setField("YS_STK_LYR_NO"     , commUtils.trim(outResult.getFieldString("YS_STK_LYR_NO" ))); 
				recInTemp.setField("YS_STK_SEQ_NO"     , commUtils.trim(outResult.getFieldString("YS_STK_SEQ_NO" ))); 
				recInTemp.setField("YD_UP_COLL_SEQ"    , szYD_UP_COLL_SEQ[Loop_i-1]);
				
				ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "작업예약 재료");
				if (ins_cnt <= 0) {
					throw new JDTOException("작업예약 재료 등록실패");
				}
			}	
			
			commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCrnChgWithLoc", logId, methodNm, "저장위치별 크레인할당");
			
			/**********************************************************
			* 2.2 크레인스케줄(YSYSJ202) 전문 호출
			**********************************************************/
			// 장입은 스케줄이 1개만 기동됨
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchCnt 
			SELECT COUNT(*)  AS SCH_CNT
			  FROM TB_YS_CRNSCH 
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD 
			*/   
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			outResult = JDTORecordFactory.getInstance().create();
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_SCH_CD"           , ydschcd     ); 
			rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchCnt", logId, methodNm, "CRN 스케줄 COUNT"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		throw new Exception("CRN 스케줄 COUNT 이상 : [" + ydschcd + "]");
			}
			 
			//레코드 추출  
			rsResult.first();
			outResult = rsResult.getRecord();
			int ydSchCnt= Integer.parseInt(outResult.getFieldString("SCH_CNT")); //야드스케쥴우선순위			
			
			if (ydSchCnt == 0) {
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrRtn = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
				//jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId	); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"    , ydschcd 	); //야드스케쥴코드
				jrYdMsg.setField("YD_SCH_ST_GP" , "U"		); //야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP", "L"     	); //야드스케쥴요청구분(인출)
				jrYdMsg.setField("MODIFIER"   , modifier 	); //수정자
	
				jrRtn = commUtils.addSndData(btYsComm.getCrnSchMsg(jrYdMsg));
			}	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	/**
	 *      [A] 오퍼레이션명 : 봉강입고 CARRY OUT 요구(YSYSJ313)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ313(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강입고 CARRY OUT요구 [YsCommL3RcvSeEJB.rcvYSYSJ313] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsResult  = null;
		JDTORecord    outResult = null;
		JDTORecord    recInTemp = null;
		JDTORecord    jrYdMsg  	= null;
		JDTORecord    jrRtn  	= null;
		JDTORecord    recBedResult  = null;
		
	    String sYS_STK_BED_NO 	= "";
	    String sSSTL_NO 		= "";
	    String sCUST_CD 		= "";
	    String sDETAIL_ARR_CD 	= "";
	    String sYD_MTL_L_GP  	= "";
	    String sYD_AIM_BAY_GP 	= "";
	    String sHEAT_NO 		= "";
	    String sWBOOK_CNT 		= "";
	    String sCURR_PROG_CD 	= "";
	    String sPROG_CD_CHK_YN 	= "";
	    
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID : KAPC01, KAPC02, KBPC01
			String ysStkBedNo = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")); //야드적치Bed번호
			String ydSchStGp  = commUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" )); //야드스케쥴기동구분
			String modifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
			} else if ("".equals(ysStkBedNo)) {
				throw new Exception("적치Bed번호(YS_STK_BED_NO) 없음");
			}
			
			String ydSchCd  =  "";
			String sWOOK_YD_AIM_BAY_GP  =  "";

			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recBedResult = JDTORecordFactory.getInstance().create();
			
			recInTemp.setField("YS_STK_COL_GP", ydEqpId);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkLyrBedDesc 
			SELECT A.*
			     , (SELECT ITEM FROM TB_YS_RULE WHERE REPR_CD_GP = 'K00013') AS PROG_CD_CHK_YN
			  FROM 
			(
			SELECT A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO
			     , A.SSTL_NO
			     , B.ORD_YEOJAE_GP 
			     , B.CUST_CD
			     , B.DETAIL_ARR_CD
			     , B.ORD_NO
			     , B.ORD_DTL
			     , B.YD_MTL_W
			     , B.YD_MTL_T
			     , B.YD_MTL_L 
			     , B.YD_MTL_W_GP
			     , B.YD_MTL_T_GP
			     , B.YD_MTL_L_GP 
			     , B.HEAT_NO
			     , B.YD_RCPT_PLN_STR_LOC
			     , SUBSTR(B.YD_RCPT_PLN_STR_LOC,2,1) YD_AIM_BAY_GP
			     , (SELECT SUBSTR(YD_CURR_STR_LOC,2,1) 
			          FROM TB_YS_EQP 
			         WHERE YD_EQP_ID = SUBSTR(A.YS_STK_COL_GP,1,1)||'XTC01'
			       ) TC_LOC_DONG   
			     , (SELECT COUNT(*) 
			          FROM TB_YS_WRKBOOKMTL 
			         WHERE DEL_YN = 'N' 
			           AND SSTL_NO = A.SSTL_NO  
			       ) WBOOK_CNT    
			     , (SELECT CURR_PROG_CD FROM TB_PB_BUNDLECOMM WHERE BNDL_NO = A.SSTL_NO) AS CURR_PROG_CD
			  FROM TB_YS_STKLYR A
			     , TB_YS_STOCK B
			 WHERE A.SSTL_NO = B.SSTL_NO(+)
			   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND A.YS_STK_BED_NO IN ('05','06')  -- 이전대상
			   AND A.SSTL_NO NOT IN (SELECT SSTL_NO FROM TB_YS_WRKBOOKMTL WHERE DEL_YN = 'N')
			UNION ALL
			SELECT A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO
			     , A.SSTL_NO
			     , B.ORD_YEOJAE_GP 
			     , B.CUST_CD
			     , B.DETAIL_ARR_CD
			     , B.ORD_NO
			     , B.ORD_DTL
			     , B.YD_MTL_W
			     , B.YD_MTL_T
			     , B.YD_MTL_L 
			     , B.YD_MTL_W_GP
			     , B.YD_MTL_T_GP
			     , B.YD_MTL_L_GP 
			     , B.HEAT_NO
			     ,YD_RCPT_PLN_STR_LOC
			     , SUBSTR(B.YD_RCPT_PLN_STR_LOC,2,1) YD_AIM_BAY_GP
			     , (SELECT SUBSTR(YD_CURR_STR_LOC,2,1) 
			          FROM TB_YS_EQP 
			         WHERE YD_EQP_ID = SUBSTR(A.YS_STK_COL_GP,1,1)||'XTC01'
			       ) TC_LOC_DONG   
			     , (SELECT COUNT(*) 
			          FROM TB_YS_WRKBOOKMTL 
			         WHERE DEL_YN = 'N' 
			           AND SSTL_NO = A.SSTL_NO  
			       ) WBOOK_CNT    
			     , (SELECT CURR_PROG_CD FROM TB_PB_BUNDLECOMM WHERE BNDL_NO = A.SSTL_NO) AS CURR_PROG_CD
			  FROM TB_YS_STKLYR A
			     , TB_YS_STOCK B
			 WHERE A.SSTL_NO = B.SSTL_NO(+)
			   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND A.YS_STK_BED_NO >= '07' AND A.SSTL_NO IS NOT NULL
			   AND A.SSTL_NO NOT IN (SELECT SSTL_NO FROM TB_YS_WRKBOOKMTL WHERE DEL_YN = 'N')
			 ) A 
			 ORDER BY YS_STK_BED_NO DESC

			 */
	    	
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkLyrBedDesc", logId, methodNm, "설비정보 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		throw new Exception("설비정보 조회 이상 : [" + ydSchCd + "]");
			}			
	    	
			String[][] chkBed = new String[5][8];	//Bed재료정보
			for (int ii = 0; ii < 5; ii++) {
				for (int jj = 0; jj < 8; jj++) {
					chkBed[ii][jj] = "";
				}
			}
 
		    //이적대상  BED 조회
			int j = 0;
			int iTC_CNT = 0;
			for(int i = 1; i <= rsResult.size(); i++) {

				rsResult.absolute(i);
				recBedResult  = rsResult.getRecord();
				
			    sYS_STK_BED_NO	= commUtils.trim(recBedResult.getFieldString("YS_STK_BED_NO"  ));
			    sSSTL_NO 		= commUtils.trim(recBedResult.getFieldString("SSTL_NO"  ));
			    sCUST_CD 		= commUtils.trim(recBedResult.getFieldString("CUST_CD"  ));       // 고객사
			    sDETAIL_ARR_CD 	= commUtils.trim(recBedResult.getFieldString("DETAIL_ARR_CD"  )); // 상세착지
			    sYD_MTL_L_GP  	= commUtils.trim(recBedResult.getFieldString("YD_MTL_L_GP"  ));   // 길이 구분 
			    sYD_AIM_BAY_GP  = commUtils.trim(recBedResult.getFieldString("YD_AIM_BAY_GP"  ));
			    sHEAT_NO  		= commUtils.trim(recBedResult.getFieldString("HEAT_NO"  ));
			    sWBOOK_CNT		= commUtils.trim(recBedResult.getFieldString("WBOOK_CNT"  ));
			    sCURR_PROG_CD	= commUtils.trim(recBedResult.getFieldString("CURR_PROG_CD"  ));
			    sYD_AIM_BAY_GP  = commUtils.trim(recBedResult.getFieldString("YD_AIM_BAY_GP"  ));
			    sPROG_CD_CHK_YN  = commUtils.trim(recBedResult.getFieldString("PROG_CD_CHK_YN"  ));
			    
			    if(i > 5 ) {     
			    	continue;
			    } 
				
			    if(!sWBOOK_CNT.equals("0")) {     // 작업 예약 편성 여부   
			    	continue;
			    } 

			    if(i == 1){
			    	if(ydEqpId.substring(1,2).endsWith("A")) {  //A동일 경우
			    		if(sYD_AIM_BAY_GP.endsWith("B")) {
				    	  	iTC_CNT = 3;  // 대상 매수 :대차 임시 적치대 :3
			    		} else {
				    	  	iTC_CNT = 2;
			    		}
			    		
			    	} else {
			    		if(sYD_AIM_BAY_GP.endsWith("A")) {
				    	  	iTC_CNT = 2;  // 대차로 이동시 무조건 2매 편성
			    		} else {
				    	  	iTC_CNT = 3;
			    		}  	
			    	}
//입고시진도추가			  
			    	if(sPROG_CD_CHK_YN.equals("Y")){
				    	if(!sCURR_PROG_CD.equals("H")) {
				    		iTC_CNT = 3;
				    	}
			    	}	
			    	chkBed[0][0] = sYS_STK_BED_NO;		//BED 번호
			    	chkBed[0][1] = sSSTL_NO;			//재료번호
			    	chkBed[0][2] = sCUST_CD;			//고객사
			    	chkBed[0][3] = sDETAIL_ARR_CD;		//목적지
			    	chkBed[0][4] = sYD_AIM_BAY_GP;		//목표동
			    	chkBed[0][5] = sYD_MTL_L_GP;		//길이구분
//입고시진도추가			    	
			    	chkBed[0][6] = sCURR_PROG_CD;		//진도구분
			    	chkBed[0][7] = sHEAT_NO;		    //HEAT_NO
			    	sWOOK_YD_AIM_BAY_GP = sYD_AIM_BAY_GP;

			    	commUtils.printLog(logId, "대상 그룹  : 고객사 ["+sCUST_CD + "] 상세착지["+sDETAIL_ARR_CD+"] 목적동["+sYD_AIM_BAY_GP+"] 길이구분["+sYD_MTL_L_GP+"] 진도["+sCURR_PROG_CD+"]"+"] HEAT_NO["+sHEAT_NO+"]", "SL");
			    	j++;
			    	
			    	continue;
			    } else {
// RULL적용 
			    	// 진도 CHECK 함
			    	if(sPROG_CD_CHK_YN.equals("Y")){
			    	
				    	// 고객사 + 상세착지 + 동
				    	if(chkBed[0][6].equals("H")) {  // 입고 대기
				    		
					    	if ((chkBed[0][2].equals(sCUST_CD)) && 
						    	(chkBed[0][3].equals(sDETAIL_ARR_CD)) && 
						    	(chkBed[0][4].equals(sYD_AIM_BAY_GP)) && 
						    	(chkBed[0][5].equals(sYD_MTL_L_GP))   && 
						    	(chkBed[0][6].equals(sCURR_PROG_CD))  &&
						    	(chkBed[0][7].equals(sHEAT_NO))  
					    		
					    		){
						    
					    		commUtils.printLog(logId, "동일 그룹  : "+sSSTL_NO, "SL");
					    		chkBed[j][0] = sYS_STK_BED_NO;		//BED 번호
						    	chkBed[j][1] = sSSTL_NO;			//재료번호
						    	chkBed[j][2] = sCUST_CD;			//고객사
						    	chkBed[j][3] = sDETAIL_ARR_CD;		//목적지
						    	chkBed[j][4] = sYD_AIM_BAY_GP;		//목표동
						    	chkBed[j][5] = sYD_MTL_L_GP;		//길이구분
						    	chkBed[j][6] = sCURR_PROG_CD;		//진도구분
						    	chkBed[j][7] = sHEAT_NO;			//HEAT_NO
						    	
						    	j++;
						    	
						    	if (j == iTC_CNT ){           //배열은 0부터 시작함
						    		break;
						    	}
					    	} else {
					    		break;
					    	}
				    	} else {
					    	if (!sCURR_PROG_CD.equals("H") && !sCURR_PROG_CD.equals("") ){
							    
					    		commUtils.printLog(logId, "동일 그룹  : "+sSSTL_NO, "SL");
					    		chkBed[j][0] = sYS_STK_BED_NO;		//BED 번호
						    	chkBed[j][1] = sSSTL_NO;			//재료번호
						    	chkBed[j][2] = sCUST_CD;			//고객사
						    	chkBed[j][3] = sDETAIL_ARR_CD;		//목적지
						    	chkBed[j][4] = sYD_AIM_BAY_GP;		//목표동
						    	chkBed[j][5] = sYD_MTL_L_GP;		//길이구분
						    	chkBed[j][6] = sCURR_PROG_CD;		//진도구분
						    	chkBed[j][7] = sHEAT_NO;			//HEAT_NO
						    	
						    	j++;
						    	
						    	if (j == iTC_CNT ){           //배열은 0부터 시작함
						    		break;
						    	}
					    	} else {
					    		break;
					    	}
				    		
				    	}
			    	} else {
				    	// 진도 CHECK 안함
			    		
			    		// 고객사 + 상세착지 + 동
				    	if ((chkBed[0][2].equals(sCUST_CD)) && 
					    	(chkBed[0][3].equals(sDETAIL_ARR_CD)) && 
					    	(chkBed[0][4].equals(sYD_AIM_BAY_GP)) && 
				    		(chkBed[0][5].equals(sYD_MTL_L_GP))  &&
					    	(chkBed[0][7].equals(sHEAT_NO))  
				    		
				    	){
					    
				    		commUtils.printLog(logId, "동일 그룹  : "+sSSTL_NO, "SL");
				    		chkBed[j][0] = sYS_STK_BED_NO;		//BED 번호
					    	chkBed[j][1] = sSSTL_NO;			//재료번호
					    	chkBed[j][2] = sCUST_CD;			//고객사
					    	chkBed[j][3] = sDETAIL_ARR_CD;		//목적지
					    	chkBed[j][4] = sYD_AIM_BAY_GP;		//목표동
					    	chkBed[j][5] = sYD_MTL_L_GP;		//길이구분
					    	chkBed[j][7] = sHEAT_NO;			//HEAT_NO
					    	j++;
					    	
					    	if (j == iTC_CNT ){           //배열은 0부터 시작함
					    		break;
					    	}
				    	} else {
				    		break;
				    	}			    		
			    	}
			    }
			}
// RULL적용 			
			commUtils.printParam(logId, chkBed);

			if (chkBed.length <= 0){
				throw new Exception("작업 예약 대상재 없음 ");	
			}
			
			int iBED = 0;
			if(!chkBed[j-1][0].equals("")) {
				iBED = Integer.parseInt(chkBed[j-1][0]);
			} 
			/**********************************************************
			*  그룹핑 한 BED 가 '07' BED 보다 큰 경우 만 작업예약 생성 
			**********************************************************/
			commUtils.printLog(logId, "선택"+iBED, "S-");

    		if(iBED >= 7) {
    	  		
    			/**********************************************************
    			*  - 작업예약등록, 크레인스케줄 전문 전송
    			**********************************************************/

    			//야드스케쥴코드생성 및 //야드스케쥴금지유무 조회
    			ydSchCd  =  ydEqpId + "LM";   //입고 CARRY OUT : 
    			
    			recInTemp = JDTORecordFactory.getInstance().create();
    			//스케줄코드로 스케줄기준Table조회
    			recInTemp.setField("YD_SCH_CD", ydSchCd);
    	    	
    	    	
    	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
    			/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule 
    			SELECT A.YD_GP
    			      ,A.YD_BAY_GP
    			      ,YD_SCH_CD
    			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
    			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
    			        END AS WRK_CRN
    			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
    			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
    			        END AS YD_WRK_CRN_PRIOR
    			      ,YD_SCH_CD_NM
    			      ,YD_SCH_CONTENTS
    			    FROM TB_YS_SCHRULE A
    			        ,(
    			            SELECT YD_GP
    			                  ,YD_BAY_GP
    			                  ,YD_SCH_GP
    			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
    			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
    			            FROM   (
    			                        SELECT YD_EQP_ID
    			                              ,YD_GP
    			                              ,YD_BAY_GP
    			                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
    			                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
    			                              ,YD_EQP_GP AS YD_SCH_GP
    			                        FROM   TB_YS_EQP
    			                        WHERE  YD_EQP_GP IN ('CR','SC')
    			                   )
    			            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
    			         ) B
    			    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
    			    AND   A.YD_DATA_GP = 'M'
    			    AND   A.YD_SCH_GP = B.YD_SCH_GP
    			    AND   A.YD_GP = B.YD_GP
    			    AND   A.YD_BAY_GP = B.YD_BAY_GP
    			    AND   A.YD_CRN_STAT1 = B.STAT1
    			    AND   A.YD_CRN_STAT2 = B.STAT2
    			    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
    	    	*/	    	 
    	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
    	    	
    	    	if (rsResult == null || rsResult.size() <= 0) {
    	    		throw new Exception("스케쥴 ID 이상 : [" + ydSchCd + "]");
    			}
    			
    			//레코드 추출  
    			rsResult.first();
    			outResult = rsResult.getRecord();
    			String ydSchPrior = outResult.getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
    			
    			//작업예약ID 조회
    			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
    			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
    			
    			
    			if ("".equals(ydWbookId)) {
    				throw new Exception("작업예약ID 생성 실패");
    			}
    			
    			recInTemp = JDTORecordFactory.getInstance().create();
    			//작업예약 등록
    			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
    			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
    			recInTemp.setField("YD_GP"             , ydEqpId.substring(0,1)          ); //야드구분
    			recInTemp.setField("YD_BAY_GP"         , ydEqpId.substring(1,2)       ); //야드동구분
    			recInTemp.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
    			recInTemp.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
    			recInTemp.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
    			recInTemp.setField("YD_SCH_ST_GP"      , "A"           ); //야드스케쥴기동구분(Manual)
    			recInTemp.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
    			recInTemp.setField("YD_AIM_YD_GP"      , ydEqpId.substring(0,1)    ); //야드목표야드구분
    			recInTemp.setField("YD_AIM_BAY_GP"     , sWOOK_YD_AIM_BAY_GP    ); //야드목표동구분
//RULL적용    			
    			if(sPROG_CD_CHK_YN.equals("Y")) {
	    			if(!chkBed[0][6].equals("H")) {
	        			recInTemp.setField("YD_TO_LOC_GUIDE"   , ydEqpId.substring(0,2)+ "TY"    ); //야드목표동구분
			    	}
    			}
    			
    			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
    			if (ins_cnt <= 0) {
    				throw new JDTOException("작업예약 재료 등록실패");
    			}
    			
    			//작업예약재료 등록
    			
    			for (int jj = 0; jj < chkBed.length; jj++) {
    				
    				if (!chkBed[jj][1].equals("")){
    					recInTemp = JDTORecordFactory.getInstance().create();
    					
    					recInTemp.setField("YD_WBOOK_ID"   , ydWbookId     ); 	//야드작업예약ID
    					recInTemp.setField("SSTL_NO"       , chkBed[jj][1]	);	//재료번호
    					recInTemp.setField("YS_STK_COL_GP" , ydEqpId		);	//야드적치열구분
    					recInTemp.setField("YS_STK_BED_NO" , chkBed[jj][0]	);	//야드적치Bed번호
    					recInTemp.setField("YS_STK_LYR_NO" , "01"			);	//야드적치단번호
    					recInTemp.setField("YS_STK_SEQ_NO" , ""+(jj+1)		);	//야드적치SEQ번호
    					recInTemp.setField("MODIFIER"     	, modifier      );												//등록자
    					ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
    					if (ins_cnt <= 0) {
    						throw new JDTOException("작업예약 재료 등록실패");
    					}
    				}	
    			}
    			
    			
    			//반납 재 입고 시 반납구분 삭제 작업(SPST_FRTOMOVE_GP)
    			ins_cnt = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.upSpstFrtomoveStock", logId, methodNm, "TB_YS_STOCK");
    			 
    			
    			
    			
    			/**********************************************************
    			* 2.2 크레인스케줄(YSYSJ302) 전문 호출
    			**********************************************************/
    			jrYdMsg = JDTORecordFactory.getInstance().create();
    			jrRtn = JDTORecordFactory.getInstance().create();
    			jrYdMsg.setResultCode(logId);	//Log ID
    			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

    			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
    			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
    			jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
    			jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
    			jrYdMsg.setField("MODIFIER"   , modifier ); //수정자

    			jrRtn = commUtils.addSndData(gdsYsComm.getCrnSchMsg(jrYdMsg));
    		} else {
    			commUtils.printLog(logId, "작업예약 편성 안함", "SB");
    		}
			
  
	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 봉강 CARRY OUT 요구(YSYSJ313)
	 *                    : 진도코드별 분리전
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ313_BU(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강입고 CARRY OUT요구 [YsCommL3RcvSeEJB.rcvYSYSJ313] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsResult  = null;
		JDTORecord    outResult = null;
		JDTORecord    recInTemp  = null;
		JDTORecord    jrYdMsg  = null;
		JDTORecord    jrRtn  = null;
		JDTORecord    recBedResult  = null;
		
	    String sYS_STK_BED_NO = "";
	    String sSSTL_NO = "";
	    String sCUST_CD = "";
	    String sDETAIL_ARR_CD = "";
	    String sYD_MTL_L_GP  = "";
	    String sYD_AIM_BAY_GP = "";
	    String sWBOOK_CNT = "";
	    
	    
		
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID : KAPC01, KAPC02, KBPC01
			String ysStkBedNo = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")); //야드적치Bed번호
//			String ysStkLyrNo = commUtils.trim(rcvMsg.getFieldString("YS_STK_LYR_NO")); //야드적치단번호
			String ydSchStGp  = commUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" )); //야드스케쥴기동구분
			String modifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
			} else if ("".equals(ysStkBedNo)) {
				throw new Exception("적치Bed번호(YS_STK_BED_NO) 없음");
			}
			
			String ydSchCd  =  "";
			String sWOOK_YD_AIM_BAY_GP  =  "";

			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recBedResult = JDTORecordFactory.getInstance().create();
			
			recInTemp.setField("YS_STK_COL_GP", ydEqpId);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkLyrBedDesc
			SELECT A.YS_STK_COL_GP
			     , A.YS_STK_BED_NO
			     , A.SSTL_NO
			     , B.ORD_YEOJAE_GP 
			     , B.CUST_CD
			     , B.DETAIL_ARR_CD
			     , B.ORD_NO
			     , B.ORD_DTL
			     , B.YD_MTL_W
			     , B.YD_MTL_T
			     , B.YD_MTL_L 
			     , B.YD_MTL_W_GP
			     , B.YD_MTL_T_GP
			     , B.YD_MTL_L_GP 
			     , B.HEAT_NO
			     ,YD_RCPT_PLN_STR_LOC
			     , SUBSTR(B.YD_RCPT_PLN_STR_LOC,2,1) YD_AIM_BAY_GP
			     , (SELECT SUBSTR(YD_CURR_STR_LOC,2,1) 
			          FROM TB_YS_EQP 
			         WHERE YD_EQP_ID = SUBSTR(A.YS_STK_COL_GP,1,1)||'XTC01'
			       ) TC_LOC_DONG   
			     , (SELECT COUNT(*) 
			          FROM TB_YS_WRKBOOKMTL 
			         WHERE DEL_YN = 'N' 
			           AND SSTL_NO = A.SSTL_NO  
			       ) WBOOK_CNT    
			  FROM TB_YS_STKLYR A
			     , TB_YS_STOCK B
			 WHERE A.SSTL_NO = B.SSTL_NO(+)
			   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND ((A.YS_STK_BED_NO IN ('06','07','08')) OR (A.YS_STK_BED_NO > '08' AND A.SSTL_NO IS NOT NULL))
			   AND A.SSTL_NO NOT IN (SELECT SSTL_NO FROM TB_YS_WRKBOOKMTL WHERE DEL_YN = 'N')
			 ORDER BY A.YS_STK_BED_NO DESC

			 */
	    	
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkLyrBedDesc", logId, methodNm, "설비정보 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		throw new Exception("설비정보 조회 이상 : [" + ydSchCd + "]");
			}			
	    	
			String[][] chkBed = new String[5][6];	//Bed재료정보
			for (int ii = 0; ii < 5; ii++) {
				for (int jj = 0; jj < 6; jj++) {
					chkBed[ii][jj] = "";
				}
			}
 
		    //이적대상  BED 조회
			int j = 0;
			int iTC_CNT = 0;
			for(int i = 1; i <= rsResult.size(); i++) {

				rsResult.absolute(i);
				recBedResult  = rsResult.getRecord();
				
			    sYS_STK_BED_NO	= commUtils.trim(recBedResult.getFieldString("YS_STK_BED_NO"  ));
			    sSSTL_NO 		= commUtils.trim(recBedResult.getFieldString("SSTL_NO"  ));
			    sCUST_CD 		= commUtils.trim(recBedResult.getFieldString("CUST_CD"  ));       // 고객사
			    sDETAIL_ARR_CD 	= commUtils.trim(recBedResult.getFieldString("DETAIL_ARR_CD"  )); // 상세착지
			    sYD_MTL_L_GP  	= commUtils.trim(recBedResult.getFieldString("YD_MTL_L_GP"  ));   // 길이 구분 
			    sYD_AIM_BAY_GP  = commUtils.trim(recBedResult.getFieldString("YD_AIM_BAY_GP"  ));
			    sWBOOK_CNT		= commUtils.trim(recBedResult.getFieldString("WBOOK_CNT"  ));

			    sYD_AIM_BAY_GP  = commUtils.trim(recBedResult.getFieldString("YD_AIM_BAY_GP"  ));
			    
			    if(i > 5 ) {     
			    	continue;
			    } 
				
			    if(!sWBOOK_CNT.equals("0")) {     // 작업 예약 편성 여부   
			    	continue;
			    } 

			    if(i == 1){
			    	if(ydEqpId.substring(1,2).endsWith("A")) {  //A동일 경우
			    		if(sYD_AIM_BAY_GP.endsWith("B")) {
				    	  	iTC_CNT = 3;  // 대상 매수 :대차 임시 적치대 :3
			    		} else {
				    	  	iTC_CNT = 2;
			    		}
			    		
			    	} else {
			    		if(sYD_AIM_BAY_GP.endsWith("A")) {
				    	  	iTC_CNT = 2;  // 대차로 이동시 무조건 2매 편성
			    		} else {
				    	  	iTC_CNT = 3;
			    		}  	
			    	}

			    	chkBed[0][0] = sYS_STK_BED_NO;		//BED 번호
			    	chkBed[0][1] = sSSTL_NO;			//재료번호
			    	chkBed[0][2] = sCUST_CD;			//고객사
			    	chkBed[0][3] = sDETAIL_ARR_CD;		//목적지
			    	chkBed[0][4] = sYD_AIM_BAY_GP;		//목표동
			    	chkBed[0][5] = sYD_MTL_L_GP;		//길이구분

			    	sWOOK_YD_AIM_BAY_GP = sYD_AIM_BAY_GP;
			    	commUtils.printLog(logId, "대상 그룹  : "+chkBed[0][1] + "iTC_CNT:" + iTC_CNT, "S-");
			    	j++;
			    	
			    	continue;
			    } else {
			    	// 고객사 + 상세착지 + 동
			    	if ((chkBed[0][2].equals(sCUST_CD)) && 
				    	(chkBed[0][3].equals(sDETAIL_ARR_CD)) && 
				    	(chkBed[0][4].equals(sYD_AIM_BAY_GP)) && 
			    		(chkBed[0][5].equals(sYD_MTL_L_GP))){
				    
			    		commUtils.printLog(logId, "동일 그룹  : "+sSSTL_NO, "S-");
			    		chkBed[j][0] = sYS_STK_BED_NO;		//BED 번호
				    	chkBed[j][1] = sSSTL_NO;			//재료번호
				    	chkBed[j][2] = sCUST_CD;			//고객사
				    	chkBed[j][3] = sDETAIL_ARR_CD;		//목적지
				    	chkBed[j][4] = sYD_AIM_BAY_GP;		//목표동
				    	chkBed[j][5] = sYD_MTL_L_GP;		//길이구분
				    	
				    	j++;
				    	
				    	if (j == iTC_CNT ){           //배열은 0부터 시작함
				    		break;
				    	}
			    	} else {
			    		break;
			    	}
			    }
			}
			commUtils.printParam(logId, chkBed);

			if (chkBed.length <= 0){
				throw new Exception("작업 예약 대상재 없음 ");	
			}
			
			int iBED = 0;
			if(!chkBed[j-1][0].equals("")) {
				iBED = Integer.parseInt(chkBed[j-1][0]);
			} 
			/**********************************************************
			*  그룹핑 한 BED 가 '07' BED 보다 큰 경우 만 작업예약 생성 
			**********************************************************/
			commUtils.printLog(logId, "선택"+iBED, "S-");

    		if(iBED >= 7) {
    	  		
    			/**********************************************************
    			*  - 작업예약등록, 크레인스케줄 전문 전송
    			**********************************************************/

    			//야드스케쥴코드생성 및 //야드스케쥴금지유무 조회
    			ydSchCd  =  ydEqpId + "LM";   //입고 CARRY OUT : 
    			
    			recInTemp = JDTORecordFactory.getInstance().create();
    			//스케줄코드로 스케줄기준Table조회
    			recInTemp.setField("YD_SCH_CD", ydSchCd);
    	    	
    	    	
    	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
    			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
    			/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule 
    			SELECT A.YD_GP
    			      ,A.YD_BAY_GP
    			      ,YD_SCH_CD
    			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
    			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
    			        END AS WRK_CRN
    			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
    			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
    			        END AS YD_WRK_CRN_PRIOR
    			      ,YD_SCH_CD_NM
    			      ,YD_SCH_CONTENTS
    			    FROM TB_YS_SCHRULE A
    			        ,(
    			            SELECT YD_GP
    			                  ,YD_BAY_GP
    			                  ,YD_SCH_GP
    			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
    			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
    			            FROM   (
    			                        SELECT YD_EQP_ID
    			                              ,YD_GP
    			                              ,YD_BAY_GP
    			                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
    			                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
    			                              ,YD_EQP_GP AS YD_SCH_GP
    			                        FROM   TB_YS_EQP
    			                        WHERE  YD_EQP_GP IN ('CR','SC')
    			                   )
    			            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
    			         ) B
    			    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
    			    AND   A.YD_DATA_GP = 'M'
    			    AND   A.YD_SCH_GP = B.YD_SCH_GP
    			    AND   A.YD_GP = B.YD_GP
    			    AND   A.YD_BAY_GP = B.YD_BAY_GP
    			    AND   A.YD_CRN_STAT1 = B.STAT1
    			    AND   A.YD_CRN_STAT2 = B.STAT2
    			    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
    	    	*/	    	 
    	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
    	    	
    	    	if (rsResult == null || rsResult.size() <= 0) {
    	    		throw new Exception("스케쥴 ID 이상 : [" + ydSchCd + "]");
    			}
    			
    			//레코드 추출  
    			rsResult.first();
    			outResult = rsResult.getRecord();
    			String ydSchPrior = outResult.getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
    			
    			//작업예약ID 조회
    			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
    			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
    			
    			
    			if ("".equals(ydWbookId)) {
    				throw new Exception("작업예약ID 생성 실패");
    			}
    			
    			recInTemp = JDTORecordFactory.getInstance().create();
    			//작업예약 등록
    			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
    			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
    			recInTemp.setField("YD_GP"             , ydEqpId.substring(0,1)          ); //야드구분
    			recInTemp.setField("YD_BAY_GP"         , ydEqpId.substring(1,2)       ); //야드동구분
    			recInTemp.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
    			recInTemp.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
    			recInTemp.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
    			recInTemp.setField("YD_SCH_ST_GP"      , "A"           ); //야드스케쥴기동구분(Manual)
    			recInTemp.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
    			recInTemp.setField("YD_AIM_YD_GP"      , ydEqpId.substring(0,1)    ); //야드목표야드구분
    			recInTemp.setField("YD_AIM_BAY_GP"     , sWOOK_YD_AIM_BAY_GP    ); //야드목표동구분
    			
//    			jrParam.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법

    			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
    			if (ins_cnt <= 0) {
    				throw new JDTOException("작업예약 재료 등록실패");
    			}
    			
    			//작업예약재료 등록
    			
    			for (int jj = 0; jj < chkBed.length; jj++) {
    				
    				if (!chkBed[jj][1].equals("")){
    					recInTemp = JDTORecordFactory.getInstance().create();
    					
    					recInTemp.setField("YD_WBOOK_ID"   , ydWbookId     ); 	//야드작업예약ID
    					recInTemp.setField("SSTL_NO"       , chkBed[jj][1]	);	//재료번호
    					recInTemp.setField("YS_STK_COL_GP" , ydEqpId		);	//야드적치열구분
    					recInTemp.setField("YS_STK_BED_NO" , chkBed[jj][0]	);	//야드적치Bed번호
    					recInTemp.setField("YS_STK_LYR_NO" , "01"			);	//야드적치단번호
    					recInTemp.setField("YS_STK_SEQ_NO" , ""+(jj+1)		);	//야드적치SEQ번호
    					recInTemp.setField("MODIFIER"     	, modifier      );												//등록자
    					ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
    					if (ins_cnt <= 0) {
    						throw new JDTOException("작업예약 재료 등록실패");
    					}
    				}	
    			}
    			
    			/**********************************************************
    			* 2.2 크레인스케줄(YSYSJ302) 전문 호출
    			**********************************************************/
    			jrYdMsg = JDTORecordFactory.getInstance().create();
    			jrRtn = JDTORecordFactory.getInstance().create();
    			jrYdMsg.setResultCode(logId);	//Log ID
    			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

    			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
    			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
    			jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
    			jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
    			jrYdMsg.setField("MODIFIER"   , modifier ); //수정자

    			jrRtn = commUtils.addSndData(gdsYsComm.getCrnSchMsg(jrYdMsg));
    		} else {
    			commUtils.printLog(logId, "작업예약 편성 안함", "SB");
    		}
			
  
	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 선재CARRY OUT 요구(YSYSJ413)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ413(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재입고 CARRY OUT요구 [YsCommL3RcvSeEJB.rcvYSYSJ413] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsResult  	= null;
		JDTORecord    outResult 	= null;
		JDTORecord    recInTemp  	= null;
		JDTORecord    jrYdMsg  		= null;
		JDTORecord    jrRtn  		= null;
		JDTORecord    recStock  	= null;
		JDTORecordSet outRsResult  	= null;
		
		String sSstlNo = "";
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID : CACV01,CBCV01
			String ysStkBedNo = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")); //야드적치Bed번호
			String ysStkLyrNo = commUtils.trim(rcvMsg.getFieldString("YS_STK_LYR_NO")); //야드적치단번호
			String ydSchStGp  = commUtils.trim(rcvMsg.getFieldString("YD_SCH_ST_GP" )); //야드스케쥴기동구분
			String modifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 : [" + ydEqpId + "]");
			} else if ("".equals(ysStkBedNo)) {
				throw new Exception("적치Bed번호(YS_STK_BED_NO) 없음");
			}
			
			String ydAimYdGp   = ""; //야드목표야드구분
			String ydAimBayGp  = ""; //야드목표동구분
			String ydSchCd  =  "";
			String YdGp   = ydEqpId.substring(1,2); 
			String szYS_STK_COL_GP = "";
			String szYS_STK_BED_NO = "";
			String szYS_STK_LYR_NO = "";

			if (YdGp.equals("D")) {
				 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStockStkLyrKech 
				 SELECT A.SSTL_NO
				      , B.CUST_CD
				      , B.HEAT_NO
				      , B.DETAIL_ARR_CD
				      , B.YD_MTL_W
				   FROM TB_YS_STKLYR A
				      , TB_YS_STOCK B
				  WHERE A.SSTL_NO = B.SSTL_NO
				    AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
				    AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
				    AND A.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				    AND A.YS_STK_SEQ_NO = '1'
				*/    	
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
				outRsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recStock = JDTORecordFactory.getInstance().create();
				recInTemp = JDTORecordFactory.getInstance().create();
				
				recInTemp.setField("YS_STK_COL_GP", ydEqpId);
				recInTemp.setField("YS_STK_BED_NO", ysStkBedNo);
				recInTemp.setField("YS_STK_LYR_NO", ysStkLyrNo);
				
				rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStockStkLyrKech", logId, methodNm, "저장품 조회");
				if (rsResult.size() <= 0) {
					throw new Exception("적치Bed번호 재료 정보  없음");
				}
				
				rsResult.first();
				recStock 	= rsResult.getRecord();
				sSstlNo 	= commUtils.trim(recStock.getFieldString("SSTL_NO"  ));
				recInTemp.setField("CUST_CD"		, commUtils.trim(recStock.getFieldString("CUST_CD"  )));
				recInTemp.setField("HEAT_NO"		, commUtils.trim(recStock.getFieldString("HEAT_NO"  )));
				recInTemp.setField("DETAIL_ARR_CD"	, commUtils.trim(recStock.getFieldString("DETAIL_ARR_CD"  )));
				recInTemp.setField("YD_MTL_W"		, commUtils.trim(recStock.getFieldString("YD_MTL_W"  )));
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCarryOutWrkMoveWrD 
				WITH PARA_TBL2 AS (  
				-- 저장 계획  확인하여 공베드 수가 많은 열 선택 함  
				SELECT BED_YS_STK_COL_GP  
				     , BED_GOND_BED_CNT  
				     , NVL(( SELECT COUNT(*)  
				               FROM TB_YS_WRKBOOK A 
				              WHERE A.DEL_YN = 'N' 
				                AND A.YD_SCH_CD LIKE 'KDHS__LM' 
				                AND SUBSTR(A.YD_TO_LOC_GUIDE,1,6) = BED_YS_STK_COL_GP 
				              GROUP BY  SUBSTR(A.YD_TO_LOC_GUIDE,1,6) 
				           ),0) AS BED_WK_CNT  
				  FROM  
				       (  
				        SELECT BED.YS_STK_COL_GP  AS BED_YS_STK_COL_GP  
				             , BED.GOND_BED_CNT   AS BED_GOND_BED_CNT  
				          FROM (SELECT YS_STK_COL_GP  
				                     , COUNT(*) GOND_BED_CNT  
				                  FROM TB_YS_STKLYR    
				                 WHERE YS_STK_COL_GP LIKE 'KD0%'  
				                   AND YD_STK_LYR_ACT_STAT = 'E'  
				                   AND YD_STK_LYR_MTL_STAT = 'E'  
				                   AND YS_STK_COL_GP IN ( SELECT TRIM(ITEM) FROM TB_YS_RULE  --폭 CHECK 
				                                           WHERE REPR_CD_GP = 'K00009' 
				                                             AND CD_GP IN ( 
				                                                    SELECT REPR_CD_CONTENTS  
				                                                      FROM USRYSA.TB_YS_RULE  
				                                                     WHERE REPR_CD_GP = 'K00008' 
				                                                       AND CD_GP <  TO_NUMBER(:V_YD_MTL_W) 
				                                                       AND ITEM  >= TO_NUMBER(:V_YD_MTL_W)) 
				                                        )                
				                 GROUP BY YS_STK_COL_GP ) BED  
				             ORDER BY  BED.GOND_BED_CNT DESC, BED.YS_STK_COL_GP   
				        )  
				 WHERE BED_YS_STK_COL_GP IN ( SELECT ITEM  
				                               FROM USRYSA.TB_YS_RULE  
				                              WHERE REPR_CD_GP = 'K00010' 
				                                AND CD_GP IN ( SELECT 'CC'||YD_EQP_NO  
				                                                 FROM TB_YS_EQP 
				                                                WHERE YD_EQP_ID LIKE 'KDCC%' 
				                                                  AND YD_EQP_NO BETWEEN '11' AND '16' 
				                                                  AND YD_EQP_STAT = 'N' 
				                                              ) 
				                            )  -- CC 고장여부  
				)   
				SELECT * FROM
				(
				SELECT A.SEQ_NUM   
				     , A.YS_STK_COL_GP  
				     , A.MAX_YS_STK_BED_NO AS YS_STK_BED_NO  
				     , A.MAX_YS_STK_LYR_NO AS YS_STK_LYR_NO   
				     , A.MAX_YS_STK_SEQ_NO AS YS_STK_SEQ_NO   
				     , A.MAX_SSTL_NO  
				     , A.MTL_STAT_UP_CNT   
				     , A.BED_GOND_BED_CNT 
				     , A.BED_WK_CNT
				  FROM  
				       (    
				        SELECT '2' SEQ_NUM  
				             ,  A.YS_STK_COL_GP  
				             ,  A.YS_STK_BED_NO  AS MAX_YS_STK_BED_NO  
				             ,  A.YS_STK_LYR_NO  AS MAX_YS_STK_LYR_NO  
				             , '1' AS MAX_YS_STK_SEQ_NO  
				             , ''  AS MAX_SSTL_NO  
				             , 0 AS MTL_STAT_UP_CNT   -- 권상예약 수  
				             ,  B.BED_GOND_BED_CNT 
				             ,  B.BED_WK_CNT
				          FROM TB_YS_STKLYR A  
				             , (SELECT COUNT(CC.SSTL_NO)  AS SUM_CNT  
				                     , CC.YS_STK_COL_GP  
				                     , CC.YS_STK_BED_NO  
				                     , CC.YS_STK_LYR_NO  
				                     , DD.BED_GOND_BED_CNT
				                     , DD.BED_WK_CNT
				                  FROM TB_YS_STKLYR CC  
				                     , PARA_TBL2  DD  
				                 WHERE DEL_YN = 'N'  
				                  AND  CC.YS_STK_COL_GP = DD.BED_YS_STK_COL_GP  
				                 GROUP BY CC.YS_STK_COL_GP,CC.YS_STK_BED_NO, CC.YS_STK_LYR_NO , DD.BED_GOND_BED_CNT , DD.BED_WK_CNT
				               ) B  
				         WHERE A.YS_STK_COL_GP = B.YS_STK_COL_GP  
				           AND A.YS_STK_BED_NO = B.YS_STK_BED_NO   
				           AND A.YS_STK_LYR_NO = B.YS_STK_LYR_NO   
				           AND B.SUM_CNT = 0   
				         GROUP BY A.YS_STK_COL_GP,A.YS_STK_BED_NO, A.YS_STK_LYR_NO , B.BED_GOND_BED_CNT , B.BED_WK_CNT   
				        ) A  
				WHERE SUBSTR(YS_STK_COL_GP,3,1) IN ('0','1')  -- 일반야드  
				  AND YS_STK_COL_GP||MAX_YS_STK_BED_NO||MAX_YS_STK_LYR_NO NOT IN  
				                  (SELECT nvl(YD_TO_LOC_GUIDE,'*') FROM TB_YS_WRKBOOK WHERE DEL_YN = 'N') 
				ORDER BY SEQ_NUM DESC, BED_WK_CNT , BED_GOND_BED_CNT, YS_STK_COL_GP, YS_STK_LYR_NO, YS_STK_BED_NO
				) WHERE ROWNUM < 10

				*/
				
				
				outRsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCarryOutWrkMoveWrD", logId, methodNm, "동일 적치가능한 베드 조회");
				if (outRsResult.size() <= 0) {
					throw new Exception(" CARRY OUT시 이적 BED 검색 실패");
				}
			    // 적치 가능 여부 CEHCK	
				JDTORecord outRecResult = JDTORecordFactory.getInstance().create();
				
				szYS_STK_COL_GP = "";
				szYS_STK_BED_NO = "";
				szYS_STK_LYR_NO = "";
				
				
				rsResult.first();
				recStock = rsResult.getRecord();
				
				outRsResult.first();
				outRecResult  = outRsResult.getRecord();
				szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		
				szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		
				szYS_STK_LYR_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));		
			}	
			
			
			
			/**********************************************************
			*  - 작업예약등록, 크레인스케줄 전문 전송
			**********************************************************/
			if (YdGp.equals("D")) {
				if((szYS_STK_COL_GP.equals("KD0101"))||(szYS_STK_COL_GP.equals("KD0102"))){
					ydSchCd = "KDHS01LM";
				} else if((szYS_STK_COL_GP.equals("KD0103"))||(szYS_STK_COL_GP.equals("KD0104"))){
					ydSchCd = "KDHS02LM";
				} else if((szYS_STK_COL_GP.equals("KD0105"))||(szYS_STK_COL_GP.equals("KD0106"))){
					ydSchCd = "KDHS03LM";
				} else if((szYS_STK_COL_GP.equals("KD0107"))||(szYS_STK_COL_GP.equals("KD0108"))){
					ydSchCd = "KDHS04LM";
				} else if((szYS_STK_COL_GP.equals("KD0109"))){
					ydSchCd = "KDHS05LM";
				} else if((szYS_STK_COL_GP.equals("KD0110"))||(szYS_STK_COL_GP.equals("KD0111"))){
					ydSchCd = "KDHS06LM";
				}
			} else {
				ydSchCd  =  ydEqpId + "LM";   //입고 CARRY OUT :
			}
			
			
			recInTemp = JDTORecordFactory.getInstance().create();
			//스케줄코드로 스케줄기준Table조회
			recInTemp.setField("YD_SCH_CD", ydSchCd);
	    	
	    	
	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule 
			SELECT A.YD_GP
			      ,A.YD_BAY_GP
			      ,YD_SCH_CD
			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
			        END AS WRK_CRN
			      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
			            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
			        END AS YD_WRK_CRN_PRIOR
			      ,YD_SCH_CD_NM
			      ,YD_SCH_CONTENTS
			    FROM TB_YS_SCHRULE A
			        ,(
			            SELECT YD_GP
			                  ,YD_BAY_GP
			                  ,YD_SCH_GP
			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
			                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
			            FROM   (
			                        SELECT YD_EQP_ID
			                              ,YD_GP
			                              ,YD_BAY_GP
			                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
			                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
			                              ,YD_EQP_GP AS YD_SCH_GP
			                        FROM   TB_YS_EQP
			                        WHERE  YD_EQP_GP IN ('CR','SC')
			                   )
			            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
			         ) B
			    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
			    AND   A.YD_DATA_GP = 'M'
			    AND   A.YD_SCH_GP = B.YD_SCH_GP
			    AND   A.YD_GP = B.YD_GP
			    AND   A.YD_BAY_GP = B.YD_BAY_GP
			    AND   A.YD_CRN_STAT1 = B.STAT1
			    AND   A.YD_CRN_STAT2 = B.STAT2
			    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
	    	*/	    	 
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		throw new Exception("스케쥴 ID 이상 : [" + ydSchCd + "]");
			}
			
			//레코드 추출  
			rsResult.first();
			outResult = rsResult.getRecord();
			String ydSchPrior = outResult.getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			
			//작업예약ID 조회
			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			
			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID 생성 실패");
			}
			
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
			recInTemp.setField("YD_GP"             , ydEqpId.substring(0,1)          ); //야드구분
			recInTemp.setField("YD_BAY_GP"         , ydEqpId.substring(1,2)       ); //야드동구분
			recInTemp.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
			recInTemp.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
			recInTemp.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
			recInTemp.setField("YD_SCH_ST_GP"      , "A"           ); //야드스케쥴기동구분(Manual)
			recInTemp.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
			recInTemp.setField("YD_AIM_YD_GP"      , ydAimYdGp     ); //야드목표야드구분
			recInTemp.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
			recInTemp.setField("YD_TO_LOC_GUIDE"   , szYS_STK_COL_GP+szYS_STK_BED_NO+szYS_STK_LYR_NO    ); //TO 위치 가이드
			
			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
			if (ins_cnt <= 0) {
				throw new JDTOException("작업예약 재료 등록실패");
			}
			
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtlByStkLyr 
			--적재위치대상재 작업예약재료 등록 
			MERGE INTO TB_YS_WRKBOOKMTL WM USING (
			SELECT :V_YD_WBOOK_ID   AS YD_WBOOK_ID --야드작업예약ID
			      ,SL.SSTL_NO                       --재료번호
			      ,:V_MODIFIER      AS MODIFIER    --수정자
			      ,SYSDATE          AS MOD_DDTT    --수정일시
			      ,'N'              AS DEL_YN      --삭제유무
			      ,SL.YS_STK_COL_GP                --야드적치열구분
			      ,SL.YS_STK_BED_NO                --야드적치Bed번호
			      ,SL.YS_STK_LYR_NO                --야드적치단번호
			      ,SL.YS_STK_SEQ_NO                --야드적치SEQ번호
			  FROM TB_YS_STKLYR SL
			 WHERE SL.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND SL.YS_STK_BED_NO = :V_YS_STK_BED_NO
			   AND SL.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
			   AND SL.SSTL_NO IS NOT NULL
			) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.SSTL_NO = DD.SSTL_NO)
			WHEN NOT MATCHED THEN
			INSERT (WM.YD_WBOOK_ID  , WM.SSTL_NO       , WM.
			REGISTER      , WM.REG_DDTT     ,
			        WM.MODIFIER     , WM.MOD_DDTT     , WM.DEL_YN        , WM.YS_STK_COL_GP,
			        WM.YS_STK_BED_NO, WM.YS_STK_LYR_NO, WM.YS_STK_SEQ_NO)
			VALUES (DD.YD_WBOOK_ID  , DD.SSTL_NO       , DD.MODIFIER      , DD.MOD_DDTT     ,
			        DD.MODIFIER     , DD.MOD_DDTT     , DD.DEL_YN        , DD.YS_STK_COL_GP,
			        DD.YS_STK_BED_NO, DD.YS_STK_LYR_NO, DD.YS_STK_SEQ_NO)
            */
			        
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
			recInTemp.setField("MODIFIER"          , modifier      ); //수정자
			recInTemp.setField("YS_STK_COL_GP"     , ydEqpId       ); //야드구분
			recInTemp.setField("YS_STK_BED_NO"     , ysStkBedNo    ); //YS_STK_BED_NO구분
			recInTemp.setField("YS_STK_LYR_NO"     , ysStkLyrNo        ); //단구분
			
			ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtlByStkLyr", logId, methodNm, "TB_YS_WRKBOOKMTL");
			if (ins_cnt <= 0) {
				throw new JDTOException("작업예약 재료 등록실패");
			}
			
			
			//반납 재 입고 시 반납구분 삭제 작업(SPST_FRTOMOVE_GP)
			ins_cnt = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.upSpstFrtomoveStock", logId, methodNm, "TB_YS_STOCK");
			 
			
			if (ydSchCd.substring(0,2).equals("KE")) {
			
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWBCnt 
				SELECT COUNT(*)  AS SCH_CNT
				FROM TB_YS_WRKBOOK 
				WHERE DEL_YN = 'N'
				AND YD_SCH_CD = :V_YD_SCH_CD 
				*/   
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
				outResult = JDTORecordFactory.getInstance().create();
				recInTemp = JDTORecordFactory.getInstance().create();
				//작업예약이 3개 이상 있어야 함
				recInTemp.setField("YD_SCH_CD"           , ydSchCd     ); 
				rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWBCnt", logId, methodNm, "CRN 스케줄 COUNT"); 
		    	
		    	if (rsResult == null || rsResult.size() <= 0) {
		    		throw new Exception("CRN 스케줄 COUNT 이상 : [" + ydSchCd + "]");
				}
				
				//레코드 추출  
				rsResult.first();
				outResult = rsResult.getRecord();
				int ydSchCnt= Integer.parseInt(outResult.getFieldString("SCH_CNT")); //야드스케쥴우선순위			
				
				if (ydSchCnt > 2) {
					/**********************************************************
					* 2.2 크레인스케줄(YSYSJ402) 전문 호출
					**********************************************************/
					jrYdMsg = JDTORecordFactory.getInstance().create();
					jrRtn = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
		
					//jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
					jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
					jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
					jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
					jrYdMsg.setField("MODIFIER"   , modifier ); //수정자
		
					jrRtn = commUtils.addSndData(gdsYsComm.getCrnSchMsg(jrYdMsg));
					
				}
			} else {
				
				/**********************************************************
				* 2.2 크레인스케줄(YSYSJ402) 전문 호출
				**********************************************************/
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrRtn = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
				jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
				jrYdMsg.setField("YD_SCH_ST_GP" , ydSchStGp); //야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
				jrYdMsg.setField("MODIFIER"   , modifier ); //수정자
	
				jrRtn = commUtils.addSndData(gdsYsComm.getCrnSchMsg(jrYdMsg));
				
				/**********************************************************
				* 2. 코일핸들링L2입고실적(YSM9L001) 전문 생성
				**********************************************************/
				JDTORecord jrYdMsgT = JDTORecordFactory.getInstance().create();
				jrYdMsgT.setField("SSTL_NO"  	, sSstlNo ); //재료번호
				jrYdMsgT.setField("YD_EQP_ID"  	, ydEqpId ); //설비번호
				jrYdMsgT.setField("YARD_LOC"  	, szYS_STK_COL_GP+szYS_STK_BED_NO+szYS_STK_LYR_NO+"1"); //저장위치

				//전송 Data 생성
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSM9L001", jrYdMsgT));
			}	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
		
	
	

	/**
	 *      [A] 오퍼레이션명 : 빌렛정정작업지시(SBYSJ007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ007(JDTORecord rcvMsg) throws DAOException {

        String szMsg	= "";
		String methodNm = "빌렛정정작업지시 [YsCommL3RcvSeEJB.rcvSBYSJ007] < " + rcvMsg.getResultMsg();
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);

		JDTORecord	recInTemp 	= JDTORecordFactory.getInstance().create();
		JDTORecord  jrRtn  		= null;
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			//수신 항목 값
			String msgId  		= commUtils.getMsgId(rcvMsg); 						// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier    	= msgId; 											// 수정자
			String sBltNo    	= commUtils.trim(rcvMsg.getFieldString("BLT_NO" )); // 빌렛번호

            szMsg = "\n\t msgId   	: " 	+ msgId 
               	  + "\n\t modifier  : " 	+ modifier 
               	  + "\n\t sBltNo    : " 	+ sBltNo;
			commUtils.printLog(logId, szMsg, "");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sBltNo)) { 
				throw new Exception("빌렛번호(BLT_NO) 없음");
			}

			recInTemp.setField("INST_TP"				, commUtils.trim(rcvMsg.getFieldString("INST_TP"    				))); // 지시구분
			recInTemp.setField("PTOP_PLNT_GP"			, commUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP"    			))); // 조업공장구분
			recInTemp.setField("BLT_NO"					, commUtils.trim(rcvMsg.getFieldString("BLT_NO"    					))); // 빌렛번호
			recInTemp.setField("PLN_BLT_NO"				, commUtils.trim(rcvMsg.getFieldString("PLN_BLT_NO"    				))); // 예정빌렛번호
			recInTemp.setField("BLM_NO"					, commUtils.trim(rcvMsg.getFieldString("BLM_NO"    					))); // 블룸번호
			recInTemp.setField("BLT_MATL_GP"			, commUtils.trim(rcvMsg.getFieldString("BLT_MATL_GP"    			))); // 빌렛소재구분
			recInTemp.setField("NXT_PROC_MATL_GDS_GP"	, commUtils.trim(rcvMsg.getFieldString("NXT_PROC_MATL_GDS_GP"    	))); // 차공장소재제품구분
			recInTemp.setField("BLT_T"					, commUtils.trim(rcvMsg.getFieldString("BLT_T"    					))); // 빌렛 두께
			recInTemp.setField("BLT_W"					, commUtils.trim(rcvMsg.getFieldString("BLT_W"    					))); // 빌렛 폭
			recInTemp.setField("BLT_L"					, commUtils.trim(rcvMsg.getFieldString("BLT_L"    					))); // 빌렛 길이
			recInTemp.setField("BLT_WT"					, commUtils.trim(rcvMsg.getFieldString("BLT_WT"    					))); // 빌렛 중량
			recInTemp.setField("TATOO_ID"				, commUtils.trim(rcvMsg.getFieldString("TATOO_ID"    				))); // 타각 ID
			recInTemp.setField("STL_APPEAR_GP"			, commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"    			))); // 재료 외형구분
			recInTemp.setField("SPEC_ABBSYM"			, commUtils.trim(rcvMsg.getFieldString("SPEC_ABBSYM"    			))); // 규격약호
			recInTemp.setField("ST_KND"					, commUtils.trim(rcvMsg.getFieldString("ST_KND"    					))); // 강종류
			recInTemp.setField("REAGENT_NO"				, commUtils.trim(rcvMsg.getFieldString("REAGENT_NO"    				))); // 시편번호
			recInTemp.setField("REAGENT_PICK_TARGET_YN"	, commUtils.trim(rcvMsg.getFieldString("REAGENT_PICK_TARGET_YN"    	))); // 시편채취대상 유무
			recInTemp.setField("ORD_NO"					, commUtils.trim(rcvMsg.getFieldString("ORD_NO"    					))); // 주문번호
			recInTemp.setField("ORD_DTL"				, commUtils.trim(rcvMsg.getFieldString("ORD_DTL"    				))); // 주문행번
			recInTemp.setField("DEMANDER_CD"			, commUtils.trim(rcvMsg.getFieldString("DEMANDER_CD"    			))); // 수요가코드
			recInTemp.setField("DEMANDER_NAME"			, commUtils.trim(rcvMsg.getFieldString("DEMANDER_NAME"    			))); // 수요가명
			recInTemp.setField("CUST_NAME"				, commUtils.trim(rcvMsg.getFieldString("CUST_NAME"    				))); // 고객명
			recInTemp.setField("CUST_CD"				, commUtils.trim(rcvMsg.getFieldString("CUST_CD"    				))); // 고객코드
			recInTemp.setField("ORD_GP"					, commUtils.trim(rcvMsg.getFieldString("ORD_GP"    					))); // 수주구분
			recInTemp.setField("ORD_YEOJAE_GP"			, commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"    			))); // 주문여재구분
			recInTemp.setField("ITEMNAME_CD"			, commUtils.trim(rcvMsg.getFieldString("ITEMNAME_CD"    			))); // 품명코드
			recInTemp.setField("USAGE_CD"				, commUtils.trim(rcvMsg.getFieldString("USAGE_CD"    				))); // 용도코드
			recInTemp.setField("SHEAR_EQP_PUT_LN"		, commUtils.trim(rcvMsg.getFieldString("SHEAR_EQP_PUT_LN"    		))); // 정정설비 투입라인
			recInTemp.setField("SUPWV_EXPLO_EFF_YN"		, commUtils.trim(rcvMsg.getFieldString("SUPWV_EXPLO_EFF_YN"    		))); // 초음파 탐상 적용여부
			recInTemp.setField("MPI_EXPLO_EFF_YN"		, commUtils.trim(rcvMsg.getFieldString("MPI_EXPLO_EFF_YN"    		))); // MPI 탐상 적용여부
			recInTemp.setField("BLT_UNIT_FTMV"			, commUtils.trim(rcvMsg.getFieldString("BLT_UNIT_FTMV"    			))); // 빌렛단위 이송
			recInTemp.setField("BLT_SUPWV_SPEC_WO_CD"	, commUtils.trim(rcvMsg.getFieldString("BLT_SUPWV_SPEC_WO_CD"    	))); // 빌렛 초음파 규격 지시코드
			recInTemp.setField("BLT_MPI_SPEC_WO_CD"		, commUtils.trim(rcvMsg.getFieldString("BLT_MPI_SPEC_WO_CD"    		))); // 빌렛MPI규격지시코드
			recInTemp.setField("BLT_SUPWV_WO_CD"		, commUtils.trim(rcvMsg.getFieldString("BLT_SUPWV_WO_CD"    		))); // 빌렛초음파지시코드(내부결함)
			recInTemp.setField("BLT_MPI_WO_CD"			, commUtils.trim(rcvMsg.getFieldString("BLT_MPI_WO_CD"    			))); // 빌렛 MPI 지시코드(표면결함)
			recInTemp.setField("BT_GRD_WO_CD"			, commUtils.trim(rcvMsg.getFieldString("BT_GRD_WO_CD"    			))); // 빌렛 Grinding 지시코드
			recInTemp.setField("BLT_GD_WRK_D"			, commUtils.trim(rcvMsg.getFieldString("BLT_GD_WRK_D"    			))); // 빌렛 Grinding 작업깊이
			recInTemp.setField("BLT_HTTRT_YN"			, commUtils.trim(rcvMsg.getFieldString("BLT_HTTRT_YN"    			))); // 빌렛 열처리 여부
			recInTemp.setField("MILL_LOT_SERNO"			, commUtils.trim(rcvMsg.getFieldString("MILL_LOT_SERNO"    			))); // 압연 Lot 일련번호
			recInTemp.setField("ORD_SPECIAL_CD"			, commUtils.trim(rcvMsg.getFieldString("ORD_SPECIAL_CD"    			))); // 주문특별구분코드
			recInTemp.setField("BLT_SUPWV_EXPLO_MTD"	, commUtils.trim(rcvMsg.getFieldString("BLT_SUPWV_EXPLO_MTD"    	))); // 빌렛초음파탐상방법
			recInTemp.setField("MPI_ND2_BASE_WO"		, commUtils.trim(rcvMsg.getFieldString("MPI_ND2_BASE_WO"    		))); // 2차 MPI 기본지시
			recInTemp.setField("SPOT_GD_CNT"			, commUtils.trim(rcvMsg.getFieldString("SPOT_GD_CNT"    			))); // 스팟 그라인딩 회수
			recInTemp.setField("MPI_ND2_INSP_CNT"		, commUtils.trim(rcvMsg.getFieldString("MPI_ND2_INSP_CNT"    		))); // 2차 MPI 검사 개수
			recInTemp.setField("NON_GD_MIN_SZ"			, commUtils.trim(rcvMsg.getFieldString("NON_GD_MIN_SZ"    			))); // 미그라인딩 최소 사이즈
			recInTemp.setField("NON_GD_MAX_SZ"			, commUtils.trim(rcvMsg.getFieldString("NON_GD_MAX_SZ"    			))); // 미그라인딩 최대 사이즈
			recInTemp.setField("NON_GD_CNT"				, commUtils.trim(rcvMsg.getFieldString("NON_GD_CNT"    				))); // 미그라인딩 결함 개수
			recInTemp.setField("RENTPROC_CD"			, commUtils.trim(rcvMsg.getFieldString("RENTPROC_CD"    			))); // 임가공코드
			recInTemp.setField("REGISTER"				, modifier															  ); // 등록자
			recInTemp.setField("MODIFIER"				, modifier															  ); // 수정자

	        szMsg = recInTemp.toString();
			commUtils.printLog(logId, szMsg, "");
	
			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkWo", logId, methodNm, "YS_빌렛정정작업지시");
			if (ins_cnt <= 0) {
				throw new Exception("YS_빌렛정정작업지시 등록 실패");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	


	/**
	 *      [A] 오퍼레이션명 : 봉강작업지시(SBYSJ008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ008(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "정정작업지시 [YsCommL3RcvSeEJB.rcvSBYSJ008]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);

		JDTORecord	recInTemp 	= JDTORecordFactory.getInstance().create();
		JDTORecord  jrRtn  		= null;
		
// 2026.01.28 정정작업지시 처리 추가		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			//수신 항목 값
			String msgId  		= commUtils.getMsgId(rcvMsg); 							// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier    	= msgId; 												// 수정자
			String sMatlNo    	= commUtils.trim(rcvMsg.getFieldString("MATL_NO" )); 	// 소재번호

            szMsg = "\n\t msgId   	: " 	+ msgId 
               	  + "\n\t modifier  : " 	+ modifier 
               	  + "\n\t sMatlNo   : " 	+ sMatlNo;
			commUtils.printLog(logId, szMsg, "");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sMatlNo)) { 
				throw new Exception("소재번호(MATL_NO) 없음");
			}

			recInTemp.setField("INST_TP"				, commUtils.trim(rcvMsg.getFieldString("INST_TP"    				))); // 지시구분
			recInTemp.setField("PTOP_PLNT_GP"			, commUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP"    			))); // 조업공장구분
			recInTemp.setField("LN_GP"					, commUtils.trim(rcvMsg.getFieldString("LN_GP"    					))); // 정정라인구분
			recInTemp.setField("MATL_NO"				, commUtils.trim(rcvMsg.getFieldString("MATL_NO"    				))); // 소재번호
			recInTemp.setField("HEAT_NO"				, commUtils.trim(rcvMsg.getFieldString("HEAT_NO"    				))); // HEAT번호
			recInTemp.setField("BLM_NO1"				, commUtils.trim(rcvMsg.getFieldString("BLM_NO1"    				))); // Bloom번호1
			recInTemp.setField("BLM_NO2"				, commUtils.trim(rcvMsg.getFieldString("BLM_NO2"    				))); // Bloom번호2
			recInTemp.setField("BLM_NO3"				, commUtils.trim(rcvMsg.getFieldString("BLM_NO3"    				))); // Bloom번호3
			recInTemp.setField("BLT_NO1"				, commUtils.trim(rcvMsg.getFieldString("BLT_NO1"    				))); // BILLET번호1
			recInTemp.setField("BLT_NO2"				, commUtils.trim(rcvMsg.getFieldString("BLT_NO2"    				))); // BILLET번호2
			recInTemp.setField("BLT_NO3"				, commUtils.trim(rcvMsg.getFieldString("BLT_NO3"    				))); // BILLET번호3
			recInTemp.setField("STL_APPEAR_GP"			, commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"    			))); // 재료 외형구분
			recInTemp.setField("WORD_UNIT_NAME"			, commUtils.trim(rcvMsg.getFieldString("WORD_UNIT_NAME"    			))); // 작업지시단위명
			recInTemp.setField("WRK_UNIT_SEQ"			, commUtils.trim(rcvMsg.getFieldString("WRK_UNIT_SEQ"    			))); // 작업지시단위순번
			recInTemp.setField("BNDL_SHEAR_CHG_LOC"		, commUtils.trim(rcvMsg.getFieldString("BNDL_SHEAR_CHG_LOC"    		))); // 번들정정장입위치
			recInTemp.setField("WRK_UNIT_IN_BNDL_CNT"	, commUtils.trim(rcvMsg.getFieldString("WRK_UNIT_IN_BNDL_CNT"    	))); // 작업지시단위내 Bundle 수
			recInTemp.setField("MILL_WRK_DT"			, commUtils.trim(rcvMsg.getFieldString("MILL_WRK_DT"    			))); // 압연작업일시
			recInTemp.setField("BNDL_SZ"				, commUtils.trim(rcvMsg.getFieldString("BNDL_SZ"    				))); // Bundle사이즈 (Bar Diameter)
			recInTemp.setField("BNDL_L"					, commUtils.trim(rcvMsg.getFieldString("BNDL_L"    					))); // Bundle길이
			recInTemp.setField("BNDL_WT"				, commUtils.trim(rcvMsg.getFieldString("BNDL_WT"    				))); // Bundle중량
			recInTemp.setField("BNDL_IN_BAR_CNT"		, commUtils.trim(rcvMsg.getFieldString("BNDL_IN_BAR_CNT"    		))); // Bundle Bar 개수
			recInTemp.setField("SPEC_ABBSYM"			, commUtils.trim(rcvMsg.getFieldString("SPEC_ABBSYM"    			))); // 규격약호(강종)
			recInTemp.setField("REAGENT_NO"				, commUtils.trim(rcvMsg.getFieldString("REAGENT_NO"    				))); // 시편번호
			recInTemp.setField("REAGENT_PICK_TARGET_YN"	, commUtils.trim(rcvMsg.getFieldString("REAGENT_PICK_TARGET_YN"    	))); // 시편채취대상 유무
			recInTemp.setField("RGNT_PK_WO_SMPL_CD"		, commUtils.trim(rcvMsg.getFieldString("RGNT_PK_WO_SMPL_CD"    		))); // 시편채취지시Sampling코드
			recInTemp.setField("ORD_NO"					, commUtils.trim(rcvMsg.getFieldString("ORD_NO"    					))); // 주문번호
			recInTemp.setField("ORD_DTL"				, commUtils.trim(rcvMsg.getFieldString("ORD_DTL"    				))); // 주문행번
			recInTemp.setField("DEMANDER_CD"			, commUtils.trim(rcvMsg.getFieldString("DEMANDER_CD"    			))); // 수요가코드
			recInTemp.setField("DEMANDER_NAME"			, commUtils.trim(rcvMsg.getFieldString("DEMANDER_NAME"    			))); // 수요가명
			recInTemp.setField("CUST_NAME"				, commUtils.trim(rcvMsg.getFieldString("CUST_NAME"    				))); // 고객명
			recInTemp.setField("CUST_CD"				, commUtils.trim(rcvMsg.getFieldString("CUST_CD"    				))); // 고객코드
			recInTemp.setField("ORD_GP"					, commUtils.trim(rcvMsg.getFieldString("ORD_GP"    					))); // 수주구분
			recInTemp.setField("ORD_YEOJAE_GP"			, commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"    			))); // 주문여재구분
			recInTemp.setField("ITEMNAME_CD"			, commUtils.trim(rcvMsg.getFieldString("ITEMNAME_CD"    			))); // 품명코드
			recInTemp.setField("USAGE_CD"				, commUtils.trim(rcvMsg.getFieldString("USAGE_CD"    				))); // 용도코드
			recInTemp.setField("SHEAR_EQP_PUT_LN"		, commUtils.trim(rcvMsg.getFieldString("SHEAR_EQP_PUT_LN"    		))); // 정정설비 투입라인
			recInTemp.setField("UST_WO_CD1"				, commUtils.trim(rcvMsg.getFieldString("UST_WO_CD1"    				))); // Bundle UST지시코드(내부)1
			recInTemp.setField("UST_WO_CD2"				, commUtils.trim(rcvMsg.getFieldString("UST_WO_CD2"    				))); // Bundle UST지시코드(내부)2
			recInTemp.setField("UST_WO_CD3"				, commUtils.trim(rcvMsg.getFieldString("UST_WO_CD3"    				))); // Bundle UST지시코드(내부)3
			recInTemp.setField("HTTRT_WRK_YN"			, commUtils.trim(rcvMsg.getFieldString("HTTRT_WRK_YN"    			))); // 열처리 실시 결과
			recInTemp.setField("BNDL_SHEAR_WRK_PLN_DT"	, commUtils.trim(rcvMsg.getFieldString("BNDL_SHEAR_WRK_PLN_DT"    	))); // 번들정정작업예정일시
			recInTemp.setField("STLQLTY_SYM"			, commUtils.trim(rcvMsg.getFieldString("STLQLTY_SYM"    			))); // 사내보증기호
			recInTemp.setField("QT_AB_TRT_MSG_CD1"		, commUtils.trim(rcvMsg.getFieldString("QT_AB_TRT_MSG_CD1"    		))); // 품질이상조치 Message1
			recInTemp.setField("QT_AB_TRT_MSG_CD2"		, commUtils.trim(rcvMsg.getFieldString("QT_AB_TRT_MSG_CD2"    		))); // 품질이상조치 Message2
			recInTemp.setField("QT_AB_TRT_MSG_CD3"		, commUtils.trim(rcvMsg.getFieldString("QT_AB_TRT_MSG_CD3"    		))); // 품질이상조치 Message3
			recInTemp.setField("QT_AB_TRT_MSG_CNTS1"	, commUtils.trim(rcvMsg.getFieldString("QT_AB_TRT_MSG_CNTS1"    	))); // 품질이상조치 Message 내용1
			recInTemp.setField("QT_AB_TRT_MSG_CNTS2"	, commUtils.trim(rcvMsg.getFieldString("QT_AB_TRT_MSG_CNTS2"    	))); // 품질이상조치 Message 내용2
			recInTemp.setField("QT_AB_TRT_MSG_CNTS3"	, commUtils.trim(rcvMsg.getFieldString("QT_AB_TRT_MSG_CNTS3"    	))); // 품질이상조치 Message 내용3
			recInTemp.setField("SCRP_COLOR"				, commUtils.trim(rcvMsg.getFieldString("SCRP_COLOR"    				))); // Scarp Color
			recInTemp.setField("SHEAR_MSG"				, commUtils.trim(rcvMsg.getFieldString("SHEAR_MSG"    				))); // 정정 Message
			recInTemp.setField("PUT_PRIOR"				, commUtils.trim(rcvMsg.getFieldString("PUT_PRIOR"    				))); // 투입순서
			recInTemp.setField("DIV_DEL_WO"				, commUtils.trim(rcvMsg.getFieldString("DIV_DEL_WO"    				))); // 분할 삭제 지시 Flag
			recInTemp.setField("SCRP_FLAG"				, commUtils.trim(rcvMsg.getFieldString("SCRP_FLAG"    				))); // Scarp Flag
			recInTemp.setField("TOLL_MANUFACTRUING_CODE", commUtils.trim(rcvMsg.getFieldString("TOLL_MANUFACTRUING_CODE"    ))); // 임가공유무코드
			recInTemp.setField("REGISTER"				, modifier															  ); // 등록자
			recInTemp.setField("MODIFIER"				, modifier															  ); // 수정자

	        szMsg = recInTemp.toString();
			commUtils.printLog(logId, szMsg, "");
	
			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStbrWrkWo", logId, methodNm, "YS_봉강작업지시");
			if (ins_cnt <= 0) {
				throw new Exception("YS_봉강작업지시 등록 실패");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
		
	
	


	/**
	 *      [A] 오퍼레이션명 : 열처리작업지시(SBYSJ009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ009(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "열처리작업지시 [YsCommL3RcvSeEJB.rcvSBYSJ009]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);

		JDTORecord	recInTemp 	= JDTORecordFactory.getInstance().create();
		JDTORecord  jrRtn  		= null;
		
// 2026.01.28 열처리작업지시 처리 추가		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			//수신 항목 값
			String msgId  		= commUtils.getMsgId(rcvMsg); 							// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier    	= msgId; 												// 수정자
			String sMatlNo    	= commUtils.trim(rcvMsg.getFieldString("MATL_NO" )); 	// 소재번호

            szMsg = "\n\t msgId   	: " 	+ msgId 
               	  + "\n\t modifier  : " 	+ modifier 
               	  + "\n\t sMatlNo   : " 	+ sMatlNo;
			commUtils.printLog(logId, szMsg, "");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sMatlNo)) { 
				throw new Exception("소재번호(MATL_NO) 없음");
			}

			recInTemp.setField("INST_TP"				, commUtils.trim(rcvMsg.getFieldString("INST_TP"    				))); // 지시구분
			recInTemp.setField("PTOP_PLNT_GP"			, commUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP"    			))); // 조업공장구분
			recInTemp.setField("LN_GP"					, commUtils.trim(rcvMsg.getFieldString("LN_GP"    					))); // 열처리 라인구분
			recInTemp.setField("MATL_NO"				, commUtils.trim(rcvMsg.getFieldString("MATL_NO"    				))); // 소재번호
			recInTemp.setField("HEAT_NO"				, commUtils.trim(rcvMsg.getFieldString("HEAT_NO"    				))); // HEAT번호
			recInTemp.setField("BLM_NO1"				, commUtils.trim(rcvMsg.getFieldString("BLM_NO1"    				))); // Bloom번호1
			recInTemp.setField("BLM_NO2"				, commUtils.trim(rcvMsg.getFieldString("BLM_NO2"    				))); // Bloom번호2
			recInTemp.setField("BLM_NO3"				, commUtils.trim(rcvMsg.getFieldString("BLM_NO3"    				))); // Bloom번호3
			recInTemp.setField("BLT_NO1"				, commUtils.trim(rcvMsg.getFieldString("BLT_NO1"    				))); // BILLET번호1
			recInTemp.setField("BLT_NO2"				, commUtils.trim(rcvMsg.getFieldString("BLT_NO2"    				))); // BILLET번호2
			recInTemp.setField("BLT_NO3"				, commUtils.trim(rcvMsg.getFieldString("BLT_NO3"    				))); // BILLET번호3
			recInTemp.setField("STL_APPEAR_GP"			, commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"    			))); // 재료 외형구분
			recInTemp.setField("WORD_UNIT_NAME"			, commUtils.trim(rcvMsg.getFieldString("WORD_UNIT_NAME"    			))); // 작업지시단위명
			recInTemp.setField("WRK_UNIT_SEQ"			, commUtils.trim(rcvMsg.getFieldString("WRK_UNIT_SEQ"    			))); // 작업지시단위순번
			recInTemp.setField("WRK_UNIT_IN_BNDL_CNT"	, commUtils.trim(rcvMsg.getFieldString("WRK_UNIT_IN_BNDL_CNT"    	))); // 작업지시단위내 Bundle 수
			recInTemp.setField("MILL_WRK_DT"			, commUtils.trim(rcvMsg.getFieldString("MILL_WRK_DT"    			))); // 압연작업일시
			recInTemp.setField("NXT_PROC_MATL_GDS_GP"	, commUtils.trim(rcvMsg.getFieldString("NXT_PROC_MATL_GDS_GP"    	))); // 차공장 소재제품구분
			recInTemp.setField("MATL_SZ1"				, commUtils.trim(rcvMsg.getFieldString("MATL_SZ1"    				))); // 소재 사이즈1
			recInTemp.setField("MATL_SZ2"				, commUtils.trim(rcvMsg.getFieldString("MATL_SZ2"    				))); // 소재 사이즈2
			recInTemp.setField("MATL_L"					, commUtils.trim(rcvMsg.getFieldString("MATL_L"    					))); // 소재 길이
			recInTemp.setField("MATL_WT"				, commUtils.trim(rcvMsg.getFieldString("MATL_WT"    				))); // 소재 중량
			recInTemp.setField("MATL_CNT"				, commUtils.trim(rcvMsg.getFieldString("MATL_CNT"    				))); // 소재 개수
			recInTemp.setField("SPEC_ABBSYM"			, commUtils.trim(rcvMsg.getFieldString("SPEC_ABBSYM"    			))); // 규격약호
			recInTemp.setField("SPEC_HEATOUT_AIM"		, commUtils.trim(rcvMsg.getFieldString("SPEC_HEATOUT_AIM"    		))); // 출강목표기호
			recInTemp.setField("REAGENT_NO"				, commUtils.trim(rcvMsg.getFieldString("REAGENT_NO"    				))); // 시편번호
			recInTemp.setField("REAGENT_PICK_TARGET_YN"	, commUtils.trim(rcvMsg.getFieldString("REAGENT_PICK_TARGET_YN"    	))); // 시편채취대상 유무
			recInTemp.setField("RGNT_PK_WO_SMPL_CD"		, commUtils.trim(rcvMsg.getFieldString("RGNT_PK_WO_SMPL_CD"    		))); // 시편채취지시Sampling코드
			recInTemp.setField("ORD_NO"					, commUtils.trim(rcvMsg.getFieldString("ORD_NO"    					))); // 주문번호
			recInTemp.setField("ORD_DTL"				, commUtils.trim(rcvMsg.getFieldString("ORD_DTL"    				))); // 주문행번
			recInTemp.setField("DEMANDER_CD"			, commUtils.trim(rcvMsg.getFieldString("DEMANDER_CD"    			))); // 수요가코드
			recInTemp.setField("DEMANDER_NAME"			, commUtils.trim(rcvMsg.getFieldString("DEMANDER_NAME"    			))); // 수요가명
			recInTemp.setField("CUST_NAME"				, commUtils.trim(rcvMsg.getFieldString("CUST_NAME"    				))); // 고객명
			recInTemp.setField("CUST_CD"				, commUtils.trim(rcvMsg.getFieldString("CUST_CD"    				))); // 고객코드
			recInTemp.setField("ORD_GP"					, commUtils.trim(rcvMsg.getFieldString("ORD_GP"    					))); // 수주구분
			recInTemp.setField("ORD_YEOJAE_GP"			, commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"    			))); // 주문여재구분
			recInTemp.setField("ITEMNAME_CD"			, commUtils.trim(rcvMsg.getFieldString("ITEMNAME_CD"    			))); // 품명코드
			recInTemp.setField("USAGE_CD"				, commUtils.trim(rcvMsg.getFieldString("USAGE_CD"    				))); // 용도코드
			recInTemp.setField("SHEAR_EQP_PUT_LN"		, commUtils.trim(rcvMsg.getFieldString("SHEAR_EQP_PUT_LN"    		))); // 정정설비 투입라인
			recInTemp.setField("WRK_PLN_DT"				, commUtils.trim(rcvMsg.getFieldString("WRK_PLN_DT"    				))); // 작업예정일시(YYYYMMDDHHMMSS)
			recInTemp.setField("STLQLTY_SYM"			, commUtils.trim(rcvMsg.getFieldString("STLQLTY_SYM"    			))); // 사내보증기호
			recInTemp.setField("STLKIND_CD"				, commUtils.trim(rcvMsg.getFieldString("STLKIND_CD"    				))); // 강종Code
			recInTemp.setField("HTTRT_MTD_1"			, commUtils.trim(rcvMsg.getFieldString("HTTRT_MTD_1"    			))); // 열처리방법
			recInTemp.setField("HTTRT_TMPUP_HR"			, commUtils.trim(rcvMsg.getFieldString("HTTRT_TMPUP_HR"    			))); // 열처리승온시간
			recInTemp.setField("HTTRT_CL_SPD"			, commUtils.trim(rcvMsg.getFieldString("HTTRT_CL_SPD"    			))); // 열처리냉각속도
			recInTemp.setField("HTTRT_ST1_AIM_TMP"		, commUtils.trim(rcvMsg.getFieldString("HTTRT_ST1_AIM_TMP"    		))); // 열처리1차목표온도
			recInTemp.setField("HTTRT_ST1_INFUR_HR"		, commUtils.trim(rcvMsg.getFieldString("HTTRT_ST1_INFUR_HR"    		))); // 열처리1차재로시간
			recInTemp.setField("PUT_PRIOR"				, commUtils.trim(rcvMsg.getFieldString("PUT_PRIOR"    				))); // 투입순서
			recInTemp.setField("REGISTER"				, modifier															  ); // 등록자
			recInTemp.setField("MODIFIER"				, modifier															  ); // 수정자

	        szMsg = recInTemp.toString();
			commUtils.printLog(logId, szMsg, "");
	
			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insHttrtWrkWo", logId, methodNm, "YS_열처리작업지시");
			if (ins_cnt <= 0) {
				throw new Exception("YS_열처리작업지시 등록 실패");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	

	

	/**
	 *      [A] 오퍼레이션명 : 보류재등록해제정보(SBYSJ010)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ010(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "보류재등록해제정보 [YsCommL3RcvSeEJB.rcvSBYSJ010]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);

		JDTORecord	recInTemp 	= JDTORecordFactory.getInstance().create();
		JDTORecord  jrRtn  		= null;
		
// 2026.04.22 보류재등록해제정보 처리 추가		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			//수신 항목 값
			String msgId  		= commUtils.getMsgId(rcvMsg); 							// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier    	= msgId; 												// 수정자
			String sBndlNo    	= commUtils.trim(rcvMsg.getFieldString("BNDL_NO" )); 	// 번들번호

            szMsg = "\n\t msgId   	: " 	+ msgId 
               	  + "\n\t modifier  : " 	+ modifier 
               	  + "\n\t sBndlNo   : " 	+ sBndlNo;
			commUtils.printLog(logId, szMsg, "");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sBndlNo)) { 
				throw new Exception("번들번호(BNDL_NO) 없음");
			}

			recInTemp.setField("BNDL_NO"						, commUtils.trim(rcvMsg.getFieldString("BNDL_NO"    					))); 	// 번들번호
			recInTemp.setField("HOLD_GP"						, commUtils.trim(rcvMsg.getFieldString("HOLD_GP"    					))); 	// 공정보류구분
			recInTemp.setField("PTOP_PLNT_GP"					, commUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP"    				))); 	// 조업공장구분
			recInTemp.setField("BLT_NO"							, commUtils.trim(rcvMsg.getFieldString("BLT_NO"    						))); 	// BILLET번호
			recInTemp.setField("REAL_MEASURE_BUNDLE_T"			, commUtils.trim(rcvMsg.getFieldString("REAL_MEASURE_BUNDLE_T"    		))); 	// 실측BUNDLE두께
			recInTemp.setField("REAL_MEASURE_BUNDLE_W"			, commUtils.trim(rcvMsg.getFieldString("REAL_MEASURE_BUNDLE_W"    		))); 	// 실측BUNDLE폭
			recInTemp.setField("REAL_MEASURE_BUNDLE_LEN"		, commUtils.trim(rcvMsg.getFieldString("REAL_MEASURE_BUNDLE_LEN"    	))); 	// 실측BUNDLE길이
			recInTemp.setField("REAL_MEASURE_BUNDLE_WT"			, commUtils.trim(rcvMsg.getFieldString("REAL_MEASURE_BUNDLE_WT"    		))); 	// 실측BUNDLE중량
			recInTemp.setField("REAL_MEASURE_BUNDLE_CNT"		, commUtils.trim(rcvMsg.getFieldString("REAL_MEASURE_BUNDLE_PIECE_CNT"	))); 	// 실측BUNDLE내개수수량(PARM 30자리 이상 오류)
			recInTemp.setField("SPST_DF_CD"						, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD"    					))); 	// 특수강결함코드
			recInTemp.setField("HEAT_NO"						, commUtils.trim(rcvMsg.getFieldString("HEAT_NO"    					))); 	// HEAT_NO
			recInTemp.setField("BLT_NO1"						, commUtils.trim(rcvMsg.getFieldString("BLT_NO1"    					))); 	// BILLET번호1
			recInTemp.setField("SPST_DF_CD1"					, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD1"    				))); 	// 특수강결함코드1
			recInTemp.setField("BLT_NO2"						, commUtils.trim(rcvMsg.getFieldString("BLT_NO2"    					))); 	// BILLET번호2
			recInTemp.setField("SPST_DF_CD2"					, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD2"    				))); 	// 특수강결함코드2
			recInTemp.setField("BLT_NO3"						, commUtils.trim(rcvMsg.getFieldString("BLT_NO3"    					))); 	// BILLET번호3
			recInTemp.setField("SPST_DF_CD3"					, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD3"    				))); 	// 특수강결함코드3
			recInTemp.setField("BLT_NO4"						, commUtils.trim(rcvMsg.getFieldString("BLT_NO4"    					))); 	// BILLET번호4
			recInTemp.setField("SPST_DF_CD4"					, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD4"    				))); 	// 특수강결함코드4
			recInTemp.setField("BLT_NO5"						, commUtils.trim(rcvMsg.getFieldString("BLT_NO5"    					))); 	// BILLET번호5
			recInTemp.setField("SPST_DF_CD5"					, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD5"    				))); 	// 특수강결함코드5
			recInTemp.setField("BLT_NO6"						, commUtils.trim(rcvMsg.getFieldString("BLT_NO6"    					))); 	// BILLET번호6
			recInTemp.setField("SPST_DF_CD6"					, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD6"    				))); 	// 특수강결함코드6
			recInTemp.setField("BLT_NO7"						, commUtils.trim(rcvMsg.getFieldString("BLT_NO7"    					))); 	// BILLET번호7
			recInTemp.setField("SPST_DF_CD7"					, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD7"    				))); 	// 특수강결함코드7
			recInTemp.setField("BLT_NO8"						, commUtils.trim(rcvMsg.getFieldString("BLT_NO8"    					))); 	// BILLET번호8
			recInTemp.setField("SPST_DF_CD8"					, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD8"    				))); 	// 특수강결함코드8
			recInTemp.setField("BLT_NO9"						, commUtils.trim(rcvMsg.getFieldString("BLT_NO9"    					))); 	// BILLET번호9
			recInTemp.setField("SPST_DF_CD9"					, commUtils.trim(rcvMsg.getFieldString("SPST_DF_CD9"    				))); 	// 특수강결함코드9
			recInTemp.setField("HOLD_STAMP_DATE"				, commUtils.trim(rcvMsg.getFieldString("HOLD_STAMP_DATE"    			))); 	// 보류판정일시
			recInTemp.setField("RHOLD_REL_DT"					, commUtils.trim(rcvMsg.getFieldString("RHOLD_REL_DT"    				))); 	// 보류해제일시
			recInTemp.setField("REGISTER"						, modifier															  	); 		// 등록자
			recInTemp.setField("MODIFIER"						, modifier															  	); 		// 수정자

			szMsg = recInTemp.toString();
			commUtils.printLog(logId, szMsg, "");
	
			int ins_cnt = commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insBndlHoldRegRel", logId, methodNm, "YS_보류재등록해제정보");
			if (ins_cnt <= 0) {
				throw new Exception("YS_보류재등록해제정보 등록 실패");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	

	
}