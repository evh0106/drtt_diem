package com.inisteel.cim.yd.testYD.session;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSpecDao.YdCarSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDeleComm;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.CmnUtil;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.testYD.dao.JspSimTestDAO;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;



import  com.inisteel.cim.common.eai.EAIHttpSender;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="JspTestSeEJB" jndi-name="JspTestSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JspTestSeEJBBean extends BaseSessionBean {

	private YdUtils ydUtils = new YdUtils();	
	YDComUtil ydComUtil = new YDComUtil();
	YDDataUtil  ydDataUtil = new YDDataUtil();
	
	private String szSessionName = getClass().getName();
	private YmCommDAO commDao = new YmCommDAO();

	/**
	 * 메뉴관련 데이터베이스를 조작하는 DAO
	 */
	 JspSimTestDAO dao = new JspSimTestDAO();
	 YDDataUtil  yddatautil = new YDDataUtil();
	 YdDelegate ydDelegate = new YdDelegate();
	 YdDaoUtils ydDaoUtils = new YdDaoUtils();
	 
	 
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	
	
	
	
	/**
	 *
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void sendJVMTest(GridData  inDto) throws DAOException {
		System.out.println("sendJVMTest() In");

		String szFaJvmGp=null;
		String szInOutGp=null;
		JDTORecord recPara = null;
		String szOperationName = "sendJVMTest처리";
	
	
		
	
		JDTORecord[] inRecord =null;
		try {
				
			//현재 문제점 변환 불가함.
		 			
		  //inRecord = CmUtil.genJDTORecordSet(inDto);
		  inRecord = ydComUtil.genJDTORecordSetTemp(inDto);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//EJB/FACADE 구분자를 가져옴 
		JDTORecord detRecord = CmUtil.genJDTORecord(inDto);
		
		
		try {
			String headerInfo = inDto.getParam("HEADER_INFO");
			String[] headers = headerInfo.split(";");
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inRecord.length;x++){
				
				
				recPara = JDTORecordFactory.getInstance().create();			
								
				szFaJvmGp = detRecord.getFieldString("JMSFACADE");
				szInOutGp = detRecord.getFieldString("TC_ID");
				
						
				
				//LOCAL 전송
				if(szFaJvmGp.trim().equals("LOCAL")){
					
					// Denug Msg
															
					ydUtils.displayRecord(szOperationName, inRecord[x]);					
					
					//전송 데이터 편집 [TC 코드 삽입]
					ydComUtil.editRec(inRecord[x], szInOutGp, recPara);					
										
					
					ydUtils.displayRecord(szOperationName, recPara);					
					
					// LOCAL 전송
					ydDelegate.lclSndMsg(recPara);
					
				}
				// REMOTE 전송 
				else if(szFaJvmGp.trim().equals("REMOTE"))
				{

					// DEBUG MESSAGE
					ydUtils.displayRecord(szOperationName, inRecord[x]);					
					
					//전송 데이터 편집 [TC 코드 삽입]
					ydComUtil.editRec(inRecord[x], szInOutGp, recPara);					
					
					ydUtils.displayRecord(szOperationName, recPara);					
					
					// REMOTE 전송 
					ydDelegate.rmtSndMsg(recPara);
				}
				//TEST  전송 
				else if(szFaJvmGp.trim().equals("TEST"))
				{

					// DEBUG MESSAGE
					ydUtils.displayRecord(szOperationName, inRecord[x]);					
					
					//전송 데이터 편집 [TC 코드 삽입]
					ydComUtil.editRec(inRecord[x], szInOutGp, recPara);					
					
					ydUtils.displayRecord(szOperationName, recPara);					
					
					// REMOTE 전송 
					ydDelegate.tstSndMsg(recPara);
					
				}//EAI L2==>L3
				else if(szFaJvmGp.trim().equals("EAI_L2")) {
					StringBuffer strStream = new StringBuffer();
					String temp = null;
					
					System.out.println("LOOP : 0 :::: " + headers[0]);
					
					for(int Loop_i = 1; Loop_i < headers.length; Loop_i++) {
						System.out.println("LOOP : " + Loop_i + " :::: " + headers[Loop_i]);
						temp = ydDaoUtils.paraRecChkNull_2(inRecord[x], headers[Loop_i]).replaceAll("@", " ");
						strStream.append(temp);
					}

					String szSendData  = headers[0];
					szSendData += YdUtils.getCurDate("yyyy-MM-dd");
					szSendData += YdUtils.getCurDate("HH-mm-ss");
					szSendData += "I";
					szSendData += YdUtils.fillSpZr(Integer.toString(strStream.toString().length()), 4, 0);
					szSendData += YdUtils.fillSpZr("", 29, 1);
					szSendData += strStream.toString();
						
					YdDeleComm ydDeleComm = new YdDeleComm();
					ydDeleComm.socketSender(szSendData);
				} else if(szFaJvmGp.trim().equals("EAI"))
				{

					// DEBUG MESSAGE
					ydUtils.displayRecord(szOperationName, inRecord[x]);					
					
					//전송 데이터 편집 [TC 코드 삽입]
					ydComUtil.editRecEai(inRecord[x], szInOutGp, recPara);					
					
					ydUtils.displayRecord(szOperationName, recPara);					
					
					EAIHttpSender sender = new EAIHttpSender();
					
					
					// EAI HttpClient 일관제철 송신 설정
					sender.initService(EAIHttpSender.issnd);
					
				
					
					// EAI 에 데이터 전송		
					sender.send(recPara);
					
				}
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			System.out.println("Error : " + e.getMessage());
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			System.out.println("sendJVMTest() Out");
		}
	}	// end of sendJVMTest
	
	

	/**
	 * 작업예약테이블을 보기위함 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getWRKBOOK(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		try {

			outRecordSet = dao.getWRKBOOK(inDto);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;

	}
	
	/**
	 * 작업예약 재료 을 보기위함 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getWRKBOOKMTL(JDTORecord inDto) throws DAOException {

		JDTORecordSet outRecordSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	
		try {
			YdWrkbookMtlDao ydWrkbookmtlDao = new YdWrkbookMtlDao();
			ydWrkbookmtlDao.getYdWrkbookmtl(inDto, outRecordSet, 1);			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;

	}
	
	
	/**
	 * 크레인 스케줄 정보를 보기위함 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCRNSCH(JDTORecord inDto) throws DAOException {

		JDTORecordSet outRecordSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	
		try {
			outRecordSet = dao.getCRNSCH(inDto);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			
		}
		return outRecordSet;

	}
	
	
	/**
	 * 작업예약 재료 을 보기위함 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCRNWRKMTL(JDTORecord inDto) throws DAOException {

		JDTORecordSet outRecordSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	
		try {
			YdCrnWrkMtlDao ydCrnwrkmtlDao = new YdCrnWrkMtlDao();
			ydCrnwrkmtlDao.getYdCrnwrkmtl(inDto, outRecordSet, 1);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;

	}
	


	/**
	 * 야드 저장품 정보 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getSTOCK(JDTORecord inDto) throws DAOException {

		JDTORecordSet outRecordSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	
		try {
			outRecordSet = dao.getSTOCK(inDto);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			
		}
		return outRecordSet;

	}
	
	/**
	 * 적치단 정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getStkLyr(JDTORecord inDto) throws DAOException {

		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
	
		try {
			
		recPara.setField("YD_STK_COL_GP", ydDataUtil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
		recPara.setField("YD_STK_BED_NO", ydDataUtil.setDataDefault(inDto.getField("YD_STK_BED_NO"), ""));
		recPara.setField("YD_STK_LYR_NO", ydDataUtil.setDataDefault(inDto.getField("YD_STK_LYR_NO"), ""));
		recPara.setField("STL_NO", ydDataUtil.setDataDefault(inDto.getField("STL_NO"), ""));
		
		ydStkLyrDao.getYdStklyr(recPara, outRecSet, 16);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecSet;

	}
	
	/**
	 * 권상 실적 전문을 내부큐로 전송 (수정버튼) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void sendRstLdDn(JDTORecord[] inDto) throws DAOException {
	
		String szMsg="";	
		String szMethodName="sendRstLdDn";
		String szOperationName = "권상 실적 전문을 내부큐로 전송 (수정버튼)";
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		String szYdGp ="";
		JDTORecord jrRtn = null;
		try {
			//수불구 변경등록 (수정) 
			for(int x=0;x<inDto.length;x++){
				
				recPara = JDTORecordFactory.getInstance().create();
		        
				szYdGp =  inDto[x].getFieldString("YD_SCH_CD").substring(0, 1);
				
				if ("A".equals(szYdGp) || "M".equals(szYdGp) || "D".equals(szYdGp) ){
					//C연주 슬라브 야드 
					//recPara.setField("JMS_TC_CD","YDYDJ600");
					// 권상실적 처리
					
					//권상실적처리
					if ("D".equals(szYdGp)) recPara.setField("MSG_ID"       , "Y3YDL008"); //크레인권상실적
					else if ("M".equals(szYdGp)) recPara.setField("MSG_ID"       , "E9YDL008"); //크레인권상실적
					else recPara.setField("MSG_ID"       , "Y1YDL008"); //크레인권상실적
					
					recPara.setField("YD_EQP_ID"       ,inDto[x].getFieldString("YD_EQP_ID")); //크레인권상실적
					recPara.setField("YD_EQP_WRK_MODE" , "1"       ); //야드설비작업Mode(Backup)
					recPara.setField("YD_WRK_PROG_STAT", "2"       ); //야드작업진행상태(권상완료)
					recPara.setField("YD_SCH_CD"       , inDto[x].getFieldString("YD_SCH_CD")); //야드스케쥴코드
					recPara.setField("YD_CRN_SCH_ID"   , inDto[x].getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
					recPara.setField("YD_UP_WR_LOC"    , inDto[x].getFieldString("YD_UP_WO_LOC")); //야드권상실적위치
					recPara.setField("YD_UP_WR_LAYER"    , inDto[x].getFieldString("YD_UP_WO_LAYER")); //야드권상실적단
					recPara.setField("YD_CRN_XAXIS"    , "0"); //야드크레인X축
					recPara.setField("YD_CRN_YAXIS"    , "0"); //야드크레인Y축
					recPara.setField("YD_CRN_ZAXIS"    , "0"); //야드크레인Z축
					
					EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
					jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL008", new Class[] { JDTORecord.class }, new Object[] { recPara });
					
					return ;
				} else if("D".equals(szYdGp)){
					//A후판 슬라브야드
					recPara.setField("JMS_TC_CD","YDYDJ602");
					
				} else if("K".equals(szYdGp)){
					//후판 제품 야드
					recPara.setField("JMS_TC_CD","YDYDJ604");
				} else if("J".equals(szYdGp)){
					//후판 제품 야드
					recPara.setField("JMS_TC_CD","YDYDJ606");
				}  else if("H".equals(szYdGp)){
					//후판 제품 야드
					recPara.setField("JMS_TC_CD","YDYDJ606");
				}
				
				//크레인 스케줄 ID
				recPara.setField("YD_CRN_SCH_ID", inDto[x].getField("YD_CRN_SCH_ID"));
				//설비 ID
				recPara.setField("YD_EQP_ID", inDto[x].getField("YD_EQP_ID"));
				
				//스케줄 코드 
				recPara.setField("YD_SCH_CD",  inDto[x].getField("YD_SCH_CD"));
				
				//요청 설비작업모드는 '9'
				recPara.setField("YD_EQP_WRK_MODE", "9");
				
		
				//작업진행상태 
				recPara.setField("YD_WRK_PROG_STAT", "2");
				
				//야드 권상 실적 위치 = 야드 권상 지시위치 
				recPara.setField("YD_UP_WR_LOC",  inDto[x].getField("YD_UP_WO_LOC"));
				
				
				//야드 권상 실적 단 = 야드 권상 지시단  
				recPara.setField("YD_UP_WR_LAYER",  inDto[x].getField("YD_UP_WO_LAYER"));
				
				
				// 야드 권상실적 BACKUP 전송  	
				ydUtils.displayRecord(szOperationName, recPara);
				ydDelegate.sendMsg(recPara);
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of sendRstLdDn
	
	
	/**
	 * 권하 실적 전문을 내부큐로 전송 (수정버튼) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void sendRstDn(JDTORecord[] inDto) throws DAOException {
	
		String szMsg="";	
		String szMethodName="sendRstDn";
		String szOperationName = "권상 실적 전문을 내부큐로 전송 (수정버튼)";
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		String szYdGp = "";
		JDTORecord jrRtn = null;
		try {
			//수불구 변경등록 (수정) 
			for(int x=0;x<inDto.length;x++){
    			// 권하실적 처리
				recPara = JDTORecordFactory.getInstance().create();
				
				szYdGp =  inDto[x].getFieldString("YD_SCH_CD").substring(0, 1);
				
				
				if ("A".equals(szYdGp) || "M".equals(szYdGp) || "D".equals(szYdGp) ){
					//C연주 슬라브 야드 
					//recPara.setField("JMS_TC_CD","YDYDJ601");
					
					//권하실적처리
					if ("D".equals(szYdGp)) recPara.setField("MSG_ID"       , "Y3YDL009"); //크레인권상실적
					else if ("M".equals(szYdGp)) recPara.setField("MSG_ID"       , "E9YDL009"); //크레인권상실적
					else recPara.setField("MSG_ID"       , "Y1YDL009"); //크레인권상실적
					
					recPara.setField("YD_EQP_ID"       ,inDto[x].getFieldString("YD_EQP_ID")); //크레인권상실적
					recPara.setField("YD_EQP_WRK_MODE" , "1"       ); //야드설비작업Mode(Backup)
					recPara.setField("YD_WRK_PROG_STAT", "2"       ); //야드작업진행상태(권상완료)
					recPara.setField("YD_SCH_CD"       , inDto[x].getFieldString("YD_SCH_CD")); //야드스케쥴코드
					recPara.setField("YD_CRN_SCH_ID"   , inDto[x].getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
					recPara.setField("YD_DN_WR_LOC"    , inDto[x].getFieldString("YD_DN_WO_LOC")); //야드권상실적위치
					recPara.setField("YD_DN_WR_LAYER"    , inDto[x].getFieldString("YD_DN_WO_LAYER")); //야드권상실적단
					recPara.setField("YD_CRN_XAXIS"    , "0"); //야드크레인X축
					recPara.setField("YD_CRN_YAXIS"    , "0"); //야드크레인Y축
					recPara.setField("YD_CRN_ZAXIS"    , "0"); //야드크레인Z축
					
					EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
					jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL009", new Class[] { JDTORecord.class }, new Object[] { recPara });
					
					return;
					
				} else if("D".equals(szYdGp)){
					//A후판 슬라브야드
					recPara.setField("JMS_TC_CD","YDYDJ603");
					
				} else if("K".equals(szYdGp)){
					//후판 제품 야드
					recPara.setField("JMS_TC_CD","YDYDJ605");
				} else if("J".equals(szYdGp)){
					//후판 제품 야드
					recPara.setField("JMS_TC_CD","YDYDJ607");
				} else if("H".equals(szYdGp)){
					//후판 제품 야드
					recPara.setField("JMS_TC_CD","YDYDJ607");
				}
				
				
				//크레인 스케줄 ID
				recPara.setField("YD_CRN_SCH_ID", inDto[x].getField("YD_CRN_SCH_ID"));
				//설비 ID
				recPara.setField("YD_EQP_ID", inDto[x].getField("YD_EQP_ID"));
				
				//스케줄 코드 
				recPara.setField("YD_SCH_CD",  inDto[x].getField("YD_SCH_CD"));
				
				//요청 설비작업모드는 '9'
				recPara.setField("YD_EQP_WRK_MODE", "9");
				
			
				//작업진행상태
				recPara.setField("YD_WRK_PROG_STAT", "4");
				
				//야드 권상 실적 위치 = 야드 권하지시위치 
				recPara.setField("YD_DN_WR_LOC",  inDto[x].getField("YD_DN_WO_LOC"));
				
				//야드 권상 실적 단 = 야드 권하 지시단
				recPara.setField("YD_DN_WR_LAYER",  inDto[x].getField("YD_DN_WO_LAYER"));
				
				// 야드 권하 실적 BACKUP 전송  	
				ydUtils.displayRecord(szOperationName, recPara);
				ydDelegate.sendMsg(recPara);

			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of sendRstDn
	
	
	
	
	
	/**
	 * TO 위치 결정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void setToPosition(JDTORecord[] inDto) throws DAOException {
	
		String szMsg="";	
		String szMethodName="setToPosition";
		String szOperationName = "TO 위치 결정";
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		try {
		
			for(int x=0;x<inDto.length;x++){

				// YDYDJ501			
				recPara.setField("JMS_TC_CD" , "YDYDJ501");
				
				//크레인 스케줄 ID
				recPara.setField("YD_WBOOK_ID", inDto[x].getField("YD_WBOOK_ID"));
				//설비 ID
				recPara.setField("YD_EQP_ID", inDto[x].getField("YD_EQP_ID"));
			
				//전송 	
				ydUtils.displayRecord(szOperationName, recPara);
				ydDelegate.sendMsg(recPara);

			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of setToPosition
	
	/**
	 * 플렉스 조회 테스트 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getYdFlexTest(HashMap param) {
		try {
			GridData grs = new GridData();
		
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord msgPara = JDTORecordFactory.getInstance().create();
			
			JDTORecordSet outRecordSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
		
			
			
			grs = CmnUtil.hashMapToGridData(param);
			recPara = CmUtil.genJDTORecord(grs); 
				
			 msgPara.setField("YD_GP", ydDataUtil.setDataDefault(recPara.getField("YD_GP"), ""));
			 
			 outRecordSet = dao.getYdFlexTest(msgPara);
			
			return CmnUtil.listJdtoRecordTohashMap(outRecordSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
	}
	
	/**
	 * 야드 대차 스케쥴 조회(테스트용 - 임춘수 20090209)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getTcarSch(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		
		
		String szMethodName = "getTcarSch";
	
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_TCAR",     yddatautil.setDataDefault(inDto.getField("YD_TCAR"), ""));
			
			YdTcarSchDao ydTcarSchDao  = new YdTcarSchDao ();
			

			
			/* com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschYdGpYdTCar */
			
			ydTcarSchDao.getYdTcarsch(recPara, outRecSet, 3);
			
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		return outRecSet;
	}	// end of getTcarSch
	
	
	/**
	 * 야드 대차 이송재료 조회(테스트용 - 임춘수 20090209)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getTcarSchFtmvMtl(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		//YdCarFtmvMtlDao  ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		
		String szMsg        = "";		
		String szMethodName = "getTcarSchFtmvMtl";
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_TCAR_SCH_ID",     yddatautil.setDataDefault(inDto.getField("YD_TCAR_SCH_ID"), ""));
			
			/* 
			 * 	com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdTcarftmvmtlByYdTCarSchId
			 */
			
			YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();
			
			ydTcarFtmvMtlDao.getYdTcarftmvmtl(recPara, outRecSet, 2);
			
						
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		return outRecSet;
	}	// end of getTcarSchFtmvMtl
	
	
	
	
	/**
	 * 플렉스 화면 - ROllTable 적치 조회 FLEX
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getYdRTPlateStlNo(HashMap param) {
		String szOperationName = "플렉스 화면 - ROllTable 적치 조회 FLEX";
		
		try {
			GridData grs = new GridData();
		
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord inPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecordSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			YdDBAssist ydDBAssist = new YdDBAssist();	
			String szStlNo = null;							
			YdStockDao ydStockDao = new YdStockDao();
			grs = CmnUtil.hashMapToGridData(param);
			recPara = CmUtil.genJDTORecord(grs);
			
			
			szStlNo = recPara.getFieldString("STL_NO");	
			
			ydUtils.displayRecord(szOperationName, recPara);
			
			inPara.setField("STL_NO", szStlNo);
			
			ydStockDao.getYdStock(inPara, outRecordSet, 0);
			
			return CmnUtil.listJdtoRecordTohashMap(outRecordSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getYdRTPlateStlNo()
	
	/**
	 * 야드 대차 베드 단 상태 조회(테스트용 - 임춘수 20090209)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getTcarBedStkLyr(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	
		
		try {
			recPara.setField("YD_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_TCAR",     yddatautil.setDataDefault(inDto.getField("YD_TCAR"), ""));
		
			
			/* com.inisteel.cim.yd.dao.ydStkLyrDao.getStkLyrbyYdGpYdTCar */
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao() ; 
			ydStkLyrDao.getYdStklyr(recPara, outRecSet, 17);
			
			
			
						
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		return outRecSet;
	}	// end of getTcarBedStkLyr
	
	/**
	 * 야드별 대차 조회(테스트용 - 임춘수 20090209)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getTcarSearch(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			try {
			recPara.setField("YD_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			
			/* com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getTcarSearchByYdGp */
			
			YdEqpDao ydEqpDao = new YdEqpDao();
			ydEqpDao.getYdEqp(recPara, outRecSet, 4);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		return outRecSet;
	}	// end of getTcarSearch
	
	/**
	 * 야드 차량 조회(테스트용 - 임춘수 20090218)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCarSearch(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		try {
			recPara.setField("TRN_EQP_CLASS",     yddatautil.setDataDefault(inDto.getField("TRN_EQP_CLASS"), ""));
			
			YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
			ydCarSpecDao.getYdCarspec(recPara, outRecSet, 3);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		return outRecSet;
	}	// end of getCarSearch
	
	/**
	 * 차량상하차 베드 단 상태 조회(테스트용 - 임춘수 20090218)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCarBedStkLyr(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		try {
			recPara.setField("TRN_EQP_CLASS",     yddatautil.setDataDefault(inDto.getField("TRN_EQP_CLASS"), ""));
			recPara.setField("YD_CAR",     yddatautil.setDataDefault(inDto.getField("YD_CAR"), ""));
			
			//KUD
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();			
			ydStkLyrDao.getYdStklyr(recPara, outRecSet, 18);
			
		
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		return outRecSet;
	}	// end of getCarBedStkLyr
	
	/**
	 * 야드 차량 스케쥴 조회(테스트용 - 임춘수 20090218)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCarSch(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		//YdCarFtmvMtlDao  ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		
		String szMsg        = "";		
		String szMethodName = "getCarSch";
	
		
		try {
			recPara.setField("TRN_EQP_CLASS",     yddatautil.setDataDefault(inDto.getField("TRN_EQP_CLASS"), ""));
			recPara.setField("YD_CAR",     yddatautil.setDataDefault(inDto.getField("YD_CAR"), ""));
			
		
			YdCarSchDao ydCarSchDao = new YdCarSchDao();			
			ydCarSchDao.getYdCarsch(recPara, outRecSet, 9);
			
		
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		return outRecSet;
	}	// end of getCarSch
	
	/**
	 * 야드 차량스케쥴 등록, 수정, 삭제(테스트용 - 임춘수 20090401)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void uptCarSch(JDTORecord inParam, Vector inVParam) throws DAOException {
		//JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		//YdCarFtmvMtlDao  ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		JDTORecord recPara = null;
		String szMsg        = "야드 차량스케쥴 등록, 수정, 삭제";		
		String szMethodName = "uptCarSch";
		String szOperationName = "";
		JDTORecord[] inDto = (JDTORecord[])inVParam.get(0);
		
		try {
//			recPara.setField("TRN_EQP_CLASS",     yddatautil.setDataDefault(inDto.getField("TRN_EQP_CLASS"), ""));
//			recPara.setField("YD_CAR",     yddatautil.setDataDefault(inDto.getField("YD_CAR"), ""));
		System.out.println("Mode : " + inParam.getFieldString("MODE"));
		System.out.println("uptCarSch 호출전 ");
//			YdCarSchDao ydCarSchDao = new YdCarSchDao();			
//			ydCarSchDao.getYdCarsch(recPara, outRecSet, 9);
		String szMode = inParam.getFieldString("MODE");
		if(szMode.equals("CSA")) {						//차량스케쥴 등록
			YdCarSchDao ydCarSchDao = new YdCarSchDao();
			for(int i = 0; i < inDto.length; i++ ) {
				recPara = inDto[i];
				ydUtils.displayRecord(szOperationName, recPara);
				ydCarSchDao.insYdCarsch(recPara);
				System.out.println("차량스케쥴 등록 성공 ");
			}
		}else if(szMode.equals("CSMA")) {									//차량이송재료 등록
			YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
			for(int i = 0; i < inDto.length; i++ ) {
				recPara = inDto[i];
				ydUtils.displayRecord(szOperationName, recPara);
				ydCarFtmvMtlDao.insYdCarftmvmtl(recPara);
				System.out.println("차량이송재료 등록 성공 ");
			}
		}
		System.out.println("uptCarSch 호출후 ");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		//return outRecSet;
	}	// end of getCarSch
	
	
	/**
	 * 야드 차랑 이송재료 조회(테스트용 - 임춘수 20090218)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCarSchFtmvMtl(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_CAR_SCH_ID",     yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), ""));
		
			YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
			
			//KUD
			
			ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 4);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			//if( ydb != null ) ydb.close();
		}
		return outRecSet;
	}	// end of getCarSchFtmvMtl
	
	
	/**
	 * 슬라브야드 스케줄 기동
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void trxRunSchedule(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		String tempLog = null;
		String szYD_SCH_PRIOR = "";
		
		szMsg        = "";
		szMethodName = "trxRunSchedule";
		String szOperationName = "슬라브야드 스케줄 기동";
		String szYD_GP = "";
		String szYD_SCH_CD		= null;
		String szTC_CD = "";
		String szCRN_SCH_INS_TYPE	= "";
		String szYD_WBOOK_ID = "";
		
		JDTORecord recPara = null;
		
		try {
			
			szMsg = "JSP-SESSION [슬라브야드 스케줄 기동 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				tempLog = inDto[x].toString();
				szYD_GP = inDto[x].getFieldString("YD_GP");
				szYD_SCH_CD  = inDto[x].getFieldString("YD_SCH_CD");
				if( szYD_GP.equals("A")) {
					if( !szYD_SCH_CD.equals("") && szYD_SCH_CD.length() == 8 ) {
						if( szYD_SCH_CD.substring(2, 4).equals("PT") 
							&& szYD_SCH_CD.substring(6).equals("LM") ) {
							szCRN_SCH_INS_TYPE = "U";
						}
					}
					szTC_CD = "YDYDJ500";
				}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD) || szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)) {
					szTC_CD = "YDYDJ509";
				}else if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)) {
					szTC_CD = "YDYDJ503";
				}else if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD)) {
					szTC_CD = "YDYDJ506";
				}else if( szYD_GP.equals(YdConstant.YD_GP_INTGR_YARD)) {
					szTC_CD = "YDYDJ512";
				}
				
				szMsg = "JSP-SESSION [슬라브야드 스케줄 기동 ] 차량하차작업인 지 판단 변수의 값["+szCRN_SCH_INS_TYPE+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				if( !szCRN_SCH_INS_TYPE.equals("")) {
					szMsg = "JSP-SESSION [슬라브야드 스케줄 기동 ] 차량하차작업인 경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				}
				
				szYD_SCH_PRIOR 	= ydDaoUtils.paraRecChkNull(inDto[x], "YD_SCH_PRIOR");
				szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(inDto[x], "YD_WBOOK_ID");
				
				recPara		= JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD", 			szTC_CD);
				recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
				recPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
				recPara.setField("CRN_SCH_INS_TYPE", 	szCRN_SCH_INS_TYPE);
				recPara.setField("YD_CRN_SCH_ID", 		ydDaoUtils.paraRecChkNull(inDto[x], "YD_CRN_SCH_ID"));	
				recPara.setField("YD_EQP_ID", 			ydDaoUtils.paraRecChkNull(inDto[x], "YD_EQP_ID"));	
				recPara.setField("YD_TO_LOC_GUIDE", 	ydDaoUtils.paraRecChkNull(inDto[x], "YD_TO_LOC_GUIDE"));	
				recPara.setField("YD_SCH_PROG_STAT", 	ydDaoUtils.paraRecChkNull(inDto[x], "YD_SCH_PROG_STAT"));	
				recPara.setField("YD_BAY_GP", 			ydDaoUtils.paraRecChkNull(inDto[x], "YD_BAY_GP"));	
				recPara.setField("YD_SCH_PROH_EXN", 	ydDaoUtils.paraRecChkNull(inDto[x], "YD_SCH_PROH_EXN"));	
				recPara.setField("YD_SCH_CD", 			ydDaoUtils.paraRecChkNull(inDto[x], "YD_SCH_CD"));	
				recPara.setField("YD_GP", 				ydDaoUtils.paraRecChkNull(inDto[x], "YD_GP"));	
				recPara.setField("MODIFIER", 			ydDaoUtils.paraRecChkNull(inDto[x], "MODIFIER"));	
				recPara.setField("YD_USER_ID", 			ydDaoUtils.paraRecChkNull(inDto[x], "YD_USER_ID"));	
				
				ydDelegate.sendMsg(recPara); 
			}		
			
			szMsg = "JSP-SESSION [슬라브야드 스케줄 기동 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of trxRunSchedule
	
	
	/**
	 * 슬라브야드 스케줄 우선순위 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void trxRunSchPrior(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
		szMsg        				= "";
		szMethodName 				= "trxRunSchPrior";
		String szOperationName 		= "슬라브야드 스케줄 기동";
		String szYD_WBOOK_ID 		= "";
		String szYD_SCH_PRIOR 		= "";
		
		JDTORecord	recPara 		= null;
		//DAO 
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao(); 
		try {
			
			szMsg = "JSP-SESSION [슬라브야드 스케줄 기동 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				//------------------------------------------------------------------------------
				// 스케줄 기동전 스케줄 우선순위 값이 존재하는경우 우선순위 값을 넣어서 UPDATE 한다
				//------------------------------------------------------------------------------
				
				szYD_SCH_PRIOR 	= ydDaoUtils.paraRecChkNull(inDto[x], "YD_SCH_PRIOR");
				szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(inDto[x], "YD_WBOOK_ID");
				
				if(!szYD_SCH_PRIOR.equals("")){
					// 작업 예약 정보 우선순위 변경
					recPara		= JDTORecordFactory.getInstance().create();
					
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);	
					recPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(inDto[x], "YD_USER_ID"));
					
					intRtnVal =  ydWrkbookDao.updYdWrkbook(recPara, 0);
					
					if(intRtnVal< 0){
						szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 UPDATE 실패!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException("작업예약 UPDATE 실패");
						
					}else if(intRtnVal == 0 ){
						
						szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 UPDATE 할 항목이 없습니다!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					}else{
						szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 우선순위 UPDATE 성공!!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						
					}
				} 
			}		
			
			szMsg = "JSP-SESSION [슬라브야드 스케줄 기동 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of trxRunSchPrior

	
	/**
	 *
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void receiveEAITest(JDTORecord msgRecord) throws JDTOException,RemoteException {
		System.out.println("receiveEAITest() In"); 
		String szOperationName = "receiveEAITest처리";	 
		JDTORecord setRecord = JDTORecordFactory.getInstance().create();
		
		try { 
			 
			setRecord.setField("MSG_ID"          , ydDaoUtils.paraRecChkNull(msgRecord, "MSG_ID"));            // TC-CODE
			setRecord.setField("EAI_DDTT"        , msgRecord.getFieldString("DATE").replaceAll("-", "") + msgRecord.getFieldString("TIME").replaceAll("-", "").replaceAll(":", ""));    //EAI생성시간
			setRecord.setField("MSG_GP"          , ydDaoUtils.paraRecChkNull(msgRecord, "MSG_GP"));            //전문구분
			setRecord.setField("MSG_LEN"         , ydDaoUtils.paraRecChkNull(msgRecord, "MSG_LEN"));            //전문길이
			
			commDao.update(setRecord, "com.inisteel.cim.yd.testYD.session.JspTestFaEJBBean.insertEAI", "EAI", szOperationName, "EAI전문 등록");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			System.out.println("Error : " + e.getMessage());
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			System.out.println("receiveEAITest() Out");
		}
	}	// end of receiveEAITest
	
}
