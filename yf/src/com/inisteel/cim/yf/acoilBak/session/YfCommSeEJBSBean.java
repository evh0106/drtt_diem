/**
 * @(#)YfCommSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 공통 로직 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.acoilBak.session;

import java.io.File;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.sb.common.util.CmnUtil;
import com.inisteel.cim.sb.common.util.SbConstant;

import com.inisteel.cim.yf.acoilBak.YfCommUtils;
import com.inisteel.cim.yf.acoilBak.YfConstant;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld2;
import com.inisteel.cim.yf.acoilBak.dao.YfCommDAO;


/**
 *      [A] 클래스명 : 박판열연 공통 로직 처리
 *
 * @ejb.bean name="YfCommBakSeEJB" jndi-name="YfCommBakSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True 
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class YfCommSeEJBSBean extends BaseSessionBean implements YfQueryIFOld, YfQueryIFOld2
{	
	private static final long serialVersionUID = 1L;
	
	private Logger logger = new Logger("yf");
	private YfCommUtils commUtils = new YfCommUtils();
	private YfCommDAO commDao = new YfCommDAO();
	private YfComm yfComm = new YfComm();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException 
	{
		
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test(조회)
	 *      -특수강
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getIFTest(GridData gdReq) throws DAOException 
	{
		try 
		{
			JDTORecordSet jrResult = commDao.getIFTest(gdReq);
			GridData gdReturn = OperateGridData.cloneResponseGridData(gdReq);

			//EAI 전송용 Message 편성
			String ifMsg = "";

			for(int i = 0; i < jrResult.size(); i++) 
			{
				ifMsg += jrResult.getRecord(i).getFieldString("ITM_VAL");
			}
			
			gdReq.addParam("IF_MSG", ifMsg);
			//logger.println(LogLevel.DEBUG, "▒ IF_MSG [" + ifMsg + "]");

			//args[] - 1 : 리턴할 GridData, 2 : 디비 결과 List, 3 : JSP에서 받은 GridData
			//3번째 아규먼트가 있었을 경우 JSP에서 받은 파라미터를 리턴할 GridData에 그대로 세팅한다.
	        return commUtils.jdtoRecordToGridData(gdReturn, jrResult.toList(), gdReq);
		} 
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test(저장)
	 *      -특수강
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updIFTest(GridData gdReq) throws DAOException 
	{
		try 
		{
			logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE2);

			int trtCnt = gdReq.getHeader("CHECK").getRowCount(); //처리건수

			if(trtCnt <= 0) 
			{
				throw new DAOException("\n▒▒▒ 인터페이스Test Data 저장 오류 발생 ▒▒▒\n>> 저장할 Data가 존재하지 않습니다.\n▒▒▒ 인터페이스Test Data 저장 오류 끝 ▒▒▒\n");
			}

			String ifID   = commUtils.nvl(gdReq.getParam("IF_ID"), ""); //IFID
			Object[][] objs = new Object[trtCnt][3];

			logger.println(LogLevel.DEBUG, "▒▒ 인터페이스TestData 저장 시작 : " + ifID);

			//Row수 만큼 Set
			for(int i = 0; i < trtCnt; i++) 
			{
				objs[i][0] = commUtils.nvl(gdReq.getHeader("ITM_VAL").getValue(i), "");
				objs[i][1] = ifID;
				objs[i][2] = commUtils.nvl(gdReq.getHeader("ITM_SEQ").getValue(i), "");
			}

			//Test Data 저장
			commDao.updIFTest(objs);

			logger.println(LogLevel.DEBUG, "▒▒ 인터페이스TestData 저장 : " + trtCnt + " 건");
			logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE2);

			return getIFTest(gdReq);
		} 
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test(전송)
	 *      -특수강
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData sndIFTest(GridData gdReq) throws DAOException 
	{
		try 
		{
			String ifID       = commUtils.nvl(gdReq.getParam("IF_ID"       ), "").trim(); //IFID
			String ifMthGp    = commUtils.nvl(gdReq.getParam("V_IF_MTH_GP"   ), "").trim(); //IF방법구분(EAI, JMS)
			String ejbCall   = commUtils.nvl(gdReq.getParam("V_EJB_CALL"   ), "").trim(); //선택적 ejb call
			String ifSndRcvGp = commUtils.nvl(gdReq.getParam("V_IF_SNDRCV_GP"), "").trim(); //IF송수신구분(송신, 수신)
			String sysGp      = commUtils.nvl(gdReq.getParam("V_SYS_GP"    ), "").trim(); //시스템구분
			String sysGp2     = commUtils.nvl(gdReq.getParam("SYS_GP2"     ), "").trim(); //시스템구분2
	    	String jndiNm     = commUtils.nvl(gdReq.getParam("PGM_NM1"     ), "").trim(); //jndi명
	    	String methodNm   = commUtils.nvl(gdReq.getParam("PGM_NM2"     ), "").trim(); //Method명
	    	
			logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE1);
			logger.println(LogLevel.DEBUG, "▒ 인터페이스TestData 전송처리(sndIFTest) 시작 : " + ifID);

			//전송데이터를 먼저 저장 함
			updIFTest(gdReq);
			
			
			//전송데이터를 조회
			JDTORecordSet jrResult = commDao.getIFTest(gdReq);
			
			if(jrResult == null || jrResult.size() < 1) 
			{
				logger.println(LogLevel.DEBUG, "▒▒ 인터페이스TestData가 없어 전송할 수 없습니다.");
				logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE2);
				
				return getIFTest(gdReq);
			}
			
			//큐에 넣을 데이터를 생성합니다.
			JDTORecord sndRec = JDTORecordFactory.getInstance().create();
			logger.println(LogLevel.DEBUG, "sysGp : "+sysGp+" ,ifMthGp : "+ifMthGp+" ,ifSndRcvGp : "+ifSndRcvGp);
			
				
			String itmID  = ""; //항목ID
			String itmVal = ""; //항목값

			for(int i = 0; i < jrResult.size(); i++) 
			{
				itmID  = commUtils.nvl(jrResult.getRecord(i).getFieldString("ITM_ID" ), "").trim();
				itmVal = commUtils.nvl(jrResult.getRecord(i).getFieldString("ITM_VAL"), "").trim();

				sndRec.setField(itmID, itmVal);
			}
			
			sndRec.setField("JMS_TC_CD"         , ifID);
			sndRec.setField("JMS_TC_CREATE_DDTT", commUtils.getCreDateTime());
			
			if("S".equals(ifSndRcvGp))
			{
				this.sndInterface(commUtils.addSndData(sndRec));
			}
			else
			{
				//ejb호출
				if("Y".equals(ejbCall))
				{
					EJBConnector rcvConn = new EJBConnector("default", "YfRcvFaEJB", this);
					rcvConn.trx("rcvInterface", new Class[] { JDTORecord.class }, new Object[] { sndRec });
				}
				//jms, eai 호출
				else
				{
					if("E".equals(ifMthGp)) 
					{
						//EAI수신처리 일 경우
						logger.println(LogLevel.DEBUG, "EJB(EAI) CALL ~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						//this.sndToEAI(sndRec);
						EJBConnector rcvConn = new EJBConnector("default", "YfRcvFaEJB", this);
						rcvConn.trx("rcvInterface", new Class[] { JDTORecord.class }, new Object[] { sndRec });
					} 
					else 
					{
						//JMS송신처리 일 경우
						logger.println(LogLevel.DEBUG, "JMS CALL ~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						this.sndToJMS(sndRec);
						/*EJBConnector delConn = new EJBConnector("default", "SbCommSndDeEJB", this);
						delConn.trx("sndToJMS", new Class[] { JDTORecord.class }, new Object[] { sndRec });*/
					}
					
					//this.sndInterface(commUtils.addSndData(sndRec));
				}
			}
			
			
			logger.println(LogLevel.DEBUG, "▒ 인터페이스TestData 전송처리(sndIFTest) 완료 : " + ifID);
			logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE1);
			
			return getIFTest(gdReq);
		}
		catch(DAOException ex) 
		{
			throw ex;
		}
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test(Multi전송)
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return void
	 *      @throws DAOException
	*/
	public GridData sndIFTestMulti(GridData gdReq) throws DAOException 
	{
		try 
		{
			String sndList = gdReq.getParam("SND_LIST"); //전송List
			String sndMsg  = ""; //전송Data
			int sndCnt = 0; //전송건수

			logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE1);
			logger.println(LogLevel.DEBUG, "▒ 인터페이스TestData 전송처리(sndIFTestMulti) 시작");
			logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE2);
			logger.println(LogLevel.DEBUG, "\n" + sndList);
			logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE2);

			while(sndList.length() > 0)
			{
				int idx = sndList.indexOf("\r\n");
				
				if(idx > 0) 
				{
					sndMsg  = sndList.substring(0, idx);
					sndList = sndList.substring(idx + 2);
				} 
				else 
				{
					sndMsg = sndList;
					sndList = "";
				}

				//한건 전송
				if(!"".equals(sndMsg)) 
				{
					sndIFTestData(sndMsg);
					sndCnt++;
				}
			}

			gdReq.addParam("SND_CNT", String.valueOf(sndCnt));
			
			return gdReq;
		} 
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test(Multi전송)
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param String sndMsg
	 *      @return void
	 *      @throws DAOException
	*/
	public void sndIFTestData(String sndMsg) throws DAOException 
	{
		try 
		{
			if("".equals(sndMsg) || sndMsg.length() < 9) 
			{
				throw new DAOException("전문 자릿수가 오류입니다.");
			}
			
			String ifID       = sndMsg.substring(0, 8); //IFID
			String ifNm       = ""; //인터페이스명
			String ifMthGp    = ""; //IF방법구분(EAI, JMS)
			String ifSndRcvGp = ""; //IF송수신구분(송신, 수신)
			String rcvSys     = ifID.substring(2, 4); //수신시스템
			
			JDTORecordSet jrsRst = commDao.getYfIFInfo(ifID);
			JDTORecord jrRst = null;

			if(jrsRst != null && jrsRst.size() > 0) 
			{ 
				jrRst = jrsRst.getRecord(0);
		    } 
			else 
			{
				throw new DAOException("인터페이스(TB_SB_Z_IF) Table에 등록되어 있지 않은 인터페이스ID [" + ifID + "] 입니다.");
		    }

			if(jrRst != null && jrRst.size() > 0) 
			{ 
				ifNm       = commUtils.nvl(jrRst.getFieldString("IF_NM"       ), "").trim(); //인터페이스명
				ifMthGp    = commUtils.nvl(jrRst.getFieldString("IF_MTH_GP"   ), "").trim(); //IF방법구분(EAI, JMS)
				ifSndRcvGp = commUtils.nvl(jrRst.getFieldString("IF_SNDRCV_GP"), "").trim(); //IF송수신구분(송신, 수신)
		    }
			
			logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE2);
			logger.println(LogLevel.DEBUG, "▒ 인터페이스Test Data 전송처리(sndIFTestData) 시작 : " + ifID + " - " + ifNm);
			
			//sndRec.setField("JMS_TC_CD"         , ifID                    );
			//sndRec.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
			
			if("E".equals(ifMthGp) && "S".equals(ifSndRcvGp)) 
			{
				//큐에 넣을 데이터를 생성합니다.
				JDTORecord sndRec = JDTORecordFactory.getInstance().create();

				//EAI송신처리 일 경우
				sndRec.setField("JMS_TC_CD"         , ifID);
				sndRec.setField("JMS_TC_CREATE_DDTT", commUtils.getCreDateTime());
				sndRec.setField("JMS_TC_MESSAGE"    , sndMsg);

				//송신 공통 EJB를 이용하여 L2로 전송
				EJBConnector delConn = new EJBConnector("default", "SbCommSndDeEJB", this);
				
				if("P2".equals(rcvSys)) 
				{
					//후판압연전단L2 송신 시
					delConn.trx("sndToSMS", new Class[] { JDTORecord.class }, new Object[] { sndRec });
				} 
				else if("H4".equals(rcvSys) || "H5".equals(rcvSys) || "PO".equals(rcvSys)) 
				{
					//B열연(압연,Label)L2 송신 시
					delConn.trx("sndToEAIPO", new Class[] { JDTORecord.class }, new Object[] { sndRec });
				} 
				else 
				{
					//기타 L2 송신 시
					delConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { sndRec });
				}
			} 
			else 
			{
				//EAI송신 외 처리 일 경우
				JDTORecord sndRec = getJMSMsg(ifID, ifNm, sndMsg);
				
				if(sndRec != null && sndRec.size() > 0) 
				{
					if("YF".equals(rcvSys)) 
					{
						//EAI수신처리 일 경우
						logger.println(LogLevel.DEBUG, "EJB CALL ~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						this.sndToEAI(sndRec);
						/*EJBConnector rcvConn = new EJBConnector("default", "SbReceiveTcFaEJB", this);
						rcvConn.trx("rcvInterface", new Class[] { JDTORecord.class }, new Object[] { sndRec });*/
					} 
					else 
					{
						//JMS송신처리 일 경우
						logger.println(LogLevel.DEBUG, "JMS CALL ~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						this.sndToJMS(sndRec);
						/*EJBConnector delConn = new EJBConnector("default", "SbCommSndDeEJB", this);
						delConn.trx("sndToJMS", new Class[] { JDTORecord.class }, new Object[] { sndRec });*/
					}
				}
			}

			logger.println(LogLevel.DEBUG, "▒ 인터페이스Test Data 전송처리(sndIFTestData) 완료 : " + ifID + " - " + ifNm);
			logger.println(LogLevel.DEBUG, YfConstant.LOG_LINE1);
		} 
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
    
	/**
	 *      [A] 오퍼레이션명 : String Type Data로 JMS 전문 편성
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param String ifID
	 *      @param String ifNm
	 *      @param String sndMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getJMSMsg(String ifID, String ifNm, String sndMsg) throws DAOException 
	{
		try 
		{
			//송신 Data Log 처리부분
			logger.println(LogLevel.DEBUG, SbConstant.LOG_LINE2);
			logger.println(LogLevel.DEBUG, "▒▒▒▒ JMS전문편성(getJMSMsg) 시작 : " + ifNm + "(" + ifID + ")");
			logger.println(LogLevel.DEBUG, SbConstant.LOG_LINE2);
			logger.println(LogLevel.DEBUG, sndMsg);
			
			//DB에 있는 인터페이스 레이아웃 정보를 조회
			JDTORecordSet jrRst = commDao.getYfIFLayout(ifID);

			//불량 전문은 Logging만 하고 계속 진행
			if(jrRst == null || jrRst.size() <= 0) 
			{
				throw new DAOException("인터페이스레이아웃(TB_HR_Z_IFLAYOUT) Table에 등록되어 있지않은 인터페이스ID [" + ifID + "] 입니다.");
			}

			int stPos = 0; //substr할 시작위치
			JDTORecord sndRec = JDTORecordFactory.getInstance().create(); //송신 Message
			
			//레이아웃과 같이 전문 편성
			for(int i = 0; i < jrRst.size(); i++) 
			{
				//DB에 등록된 항목 값
				String itmId = CmnUtil.nvl(jrRst.getRecord(i).getFieldString("ITM_ID"), "");
				String itmNm = CmnUtil.nvl(jrRst.getRecord(i).getFieldString("ITM_NM"), "");
				int itmDataL = jrRst.getRecord(i).getFieldInt("ITM_DATA_L");

				String itmVal = CmnUtil.substr(sndMsg, stPos, itmDataL).trim();
				stPos += itmDataL;
				
				sndRec.setField(itmId, itmVal);
				logger.println(LogLevel.DEBUG, "▒▒▒▒ " + itmNm + "(" + itmId + ") : [" + itmVal + "]");
			}			

			logger.println(LogLevel.DEBUG, "▒▒▒▒ JMS전문편성(getJMSMsg) 완료 : " + ifNm + "(" + ifID + ")");
			logger.println(LogLevel.DEBUG, SbConstant.LOG_LINE2);

			return sndRec;
		} 
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 * 출하전문 취소처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord receiveCancel(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm	= "A열연 출하전문 취소처리[YfCommSeEJBSBean.receiveCancel] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
 
		String sSTOCK_MOVE_TERM		= "";
		String sSchId				= "";
		String sBookId				= "";

    	JDTORecordSet 	jrStlNo		= null;
    	JDTORecordSet	rsResult	= null;
    	JDTORecord 		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		JDTORecord 		jrRtn 		= JDTORecordFactory.getInstance().create();
    	
		try 
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			String msgId    			= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sYD_GP 				= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String sCURR_PROG_CD		= commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"));	    		
			String sTRANS_ORD_DT		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			String sTRANS_ORD_SEQNO		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String sFRTOMOVE_WORD_NO	= commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_NO")); //이송 작업지시 번호
			String scardNo				= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String sSTL_NO 				= commUtils.trim(rcvMsg.getFieldString("STL_NO"));
			String modifier 			= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			//변경자 설정 (insert,update 문에서 사용)
			if ("".equals(modifier)) 
			{ 
				modifier = msgId; 
			}
			
			jrParam.setField("MODIFIER", modifier); //수정자
			
	    	/*
			DMYDR008	코일제품반납대기		1.저장품 이동 조건 변경
			DMYDR013	외판슬라브목전		1.저장품 이동 조건 변경
			DMYDR014	코일제품목전			1.저장품 이동 조건 변경
			DMYDR016	외판슬라브운송지시대기	1.저장품 이동 조건 변경
			DMYDR029	외판슬라브출하완료 		1.저장품 이동 조건 변경
			DMYDR030	코일제품출하완료              1.저장품 이동 조건 변경
			DMYDR011	코일제품고간이송지시	1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
			DMYDR020	코일제품운송지시              1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함) ,저장품 이동 조건 변경
			DMYDR022	외판슬라브운송상차지시 	1.크레인 스케줄취소 ,2 작업예약취소 , 3. 카드번호삭제,저장품 이동 조건 변경
			DMYDR023	코일제품상차지시		1.크레인 스케줄취소 ,2 작업예약취소 , 3. 카드번호삭제,저장품 이동 조건 변경
			DMYDR026	외판슬라브보관지시		KEEPSTOCK_STL_YN= ''
			DMYDR027	코일제품보관지시		KEEPSTOCK_STL_YN= ''
	    	 */
			
			//코일제품고간이송지시(DMYDR011)가 아닌 경우에만...
	    	if(!YfConstant.DMYDR011.equals(msgId))
	    	{
		    	if("".equals(sSTL_NO))
		    	{
	 				/**********************************************************
	 				* 운송지시번호,이송 작업지시 번호로 저장품 조회
	 				**********************************************************/  
					jrParam.setField("TRANS_ORD_DATE",		sTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO",		sTRANS_ORD_SEQNO);
					jrParam.setField("FRTOMOVE_WORD_NO",	sFRTOMOVE_WORD_NO);
					jrStlNo = commDao.select(jrParam, getTransStock, logId, methodNm, "운송지시번호,이송 작업지시 번호로 저장품 조회");				
					
					if(jrStlNo.size() <= 0 )
					{
						commUtils.printLog("", "출하/이송/이적 대상을 찾지 못했습니다!!", "[INFO]");
					}
					else
					{
						sSTL_NO = commUtils.trim(jrStlNo.getRecord(0).getFieldString("STL_NO"));  
					}
		    	} 
				
 				/**********************************************************
 				* 진도코드로 저장품이동조건을 가져온다.
 				**********************************************************/  
				jrParam.setField("STL_NO",			sSTL_NO); 
				jrParam.setField("CURR_PROG_CD",	sCURR_PROG_CD);
				
				JDTORecord jrRtnProg = yfComm.getCoilCurrProgCd2(jrParam);
				sSTOCK_MOVE_TERM = commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"));
	      	}
	    	
	    	if
	    	(
	    		YfConstant.DMYDR008.equals(msgId)||  //코일제품반납대기
	    		YfConstant.DMYDR013.equals(msgId)||  //외판슬라브목전
	    		YfConstant.DMYDR014.equals(msgId)||  //코일제품목전
	    		YfConstant.DMYDR016.equals(msgId)||  //외판슬라브운송지시대기
	    		YfConstant.DMYDR029.equals(msgId)||  //외판슬라브출하완료
	    		YfConstant.DMYDR030.equals(msgId)	 //코일제품출하완료
	    	)
	    	{
 				/**********************************************************
 				* 저장품 이동 조건 변경
 				**********************************************************/  
	    		jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
	    		jrParam.setField("STL_NO",			sSTL_NO);
	    		commDao.update(jrParam, updYdStock, logId, methodNm, "TB_YF_STOCK 저장품 이동 조건 변경");
	        }
	    	else if
	    	(
	    		YfConstant.DMYDR011.equals(msgId)||  //코일제품고간이송지시
	    		YfConstant.DMYDR020.equals(msgId)||  //코일제품운송지시
	    		YfConstant.DMYDR070.equals(msgId)||  //코일이송상차대기장도착PDA
	    		YfConstant.DMYDR073.equals(msgId)||  //코일이송하차대기장도착PDA
	    		YfConstant.DMYDR060.equals(msgId)||  //코일제품운송상차지시
	    		YfConstant.DMYDR023.equals(msgId)||  //코일제품상차지시
	    		YfConstant.DMYDR022.equals(msgId)	 //외판슬라브운송상차지시
	    	) 
	        {
 				/**********************************************************
 				* 적치열 차량포인트 점유 정보 비우기
 				**********************************************************/  
				jrParam.setField("TRANS_ORD_DATE",	sTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO",	sTRANS_ORD_SEQNO);
			    commDao.update(jrParam, updYdStackcolCarPint, logId, methodNm, "TB_YF_STACKCOL 적치열 차량포인트 점유 정보 비우기");
			    
		        if
		        (
		        	YfConstant.DMYDR011.equals(msgId)||  //코일제품고간이송지시
		        	YfConstant.DMYDR020.equals(msgId)||  //코일제품운송지시
		        	YfConstant.DMYDR070.equals(msgId)||  //코일이송상차대기장도착PDA
		        	YfConstant.DMYDR073.equals(msgId)||  //코일이송하차대기장도착PDA
		        	YfConstant.DMYDR060.equals(msgId)	 //코일제품운송상차지시
		        )
		        {
	 				/**********************************************************
	 				* 운송지시번호 삭제,저장품 이동 조건 변경
	 				**********************************************************/  
		    		if(YfConstant.DMYDR011.equals(msgId)) 
		    		{
		    			jrParam.setField("STOCK_MOVE_TERM"	, "");
		    		} 
		    		else 
		    		{
		    			jrParam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM);
		    		}
		    		
					jrParam.setField("TRANS_ORD_DATE",	sTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO",	sTRANS_ORD_SEQNO);
		    		commDao.update(jrParam, updYdStock6, logId, methodNm, "TB_YF_STOCK 운송지시번호 삭제,저장품 이동 조건 변경 ");	    			
		 	    }
		        else
		        {
	 				/**********************************************************
	 				* 카드번호삭제,저장품 이동 조건 변경
	 				**********************************************************/
		 	    	jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
					jrParam.setField("TRANS_ORD_DATE",	sTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO",	sTRANS_ORD_SEQNO);
		    		commDao.update(jrParam, updYdStock7, logId, methodNm, "TB_YF_STOCK 카드번호삭제,저장품 이동 조건 변경");	
		 	    }
	 	    }
	        else if(YfConstant.DMYDR027.equals(msgId))	//코일제품보관지시
	        {
 				/**********************************************************
 				* 보관지시구분 KEEPSTOCK_STL_YN = ''
 				**********************************************************/  
	    		jrParam.setField("KEEP_STL_YN",	"");
	    		jrParam.setField("STL_NO",			sSTL_NO);
	    		commDao.update(jrParam, updYdStock2, logId, methodNm, "TB_YM_STOCK 보관지시구분 KEEP_STL_YN = '' ");
	        }
    	
			//코일제품고간이송지시(DMYDR011)가 아닌 경우에만...
	    	if(!YfConstant.DMYDR011.equals(msgId)) 
	    	{
	    		for(int ii = 0; ii < jrStlNo.size(); ii++) 
	    		{
	    			sSTL_NO = commUtils.trim(jrStlNo.getRecord(ii).getFieldString("STL_NO"));  
			
	 				/**********************************************************
	 				* STL_NO로 스케줄 ID,작업예약ID 가져오기
	 				**********************************************************/  
	    			jrParam.setField("STL_NO", sSTL_NO);
	    			rsResult  = commDao.select(jrParam, getYdWrkbookDelChk, logId, methodNm, "STL_NO로 스케줄 ID,작업예약ID 가져오기");				
	    		
		    		if(rsResult.size() > 0) 
		    		{
			    		sSchId 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));   
			    		sBookId = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));
			    		
			    		if("1".equals(sYD_GP))
			    		{
			    			//박판코일
			    			if(!"".equals(sSchId))
				    		{
			    				//박판코일 크레인스케줄취소
	    			        	JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		        				jrParam.setField("YD_CRN_SCH_ID", sSchId);
		        				jrParam.setField("YD_WBOOK_ID"  , sBookId);
		        				jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
		        				
		        				EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
		        				jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		        				jrRtn = commUtils.addSndData(jrRtn, jrRst);
				    		}
			    			
			    			if(!"".equals(sBookId))
			    			{
			    				//박판코일 작업예약취소
	    			        	JDTORecord jrRst = JDTORecordFactory.getInstance().create();
	    			        	jrParam.setField("YD_WBOOK_ID"  , sBookId);
	    			        	
	    			        	EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
		        				jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		        				jrRtn = commUtils.addSndData(jrRtn, jrRst);
			    			}
			    		}
			    		else if("0".equals(sYD_GP))
			    		{
			    			//박판슬라브
			    			if(!"".equals(sSchId))
				    		{
			    				//박판슬라브 크레인스케줄취소
			    				JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		        				jrParam.setField("YD_CRN_SCH_ID", sSchId);
		        				jrParam.setField("WRK_CNCL_YN", "Y"); //작업취소 여부
		        				
		        				EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
		        				jrRst = (JDTORecord)ejbConn.trx("cancelSlabSchInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		        				jrRtn = commUtils.addSndData(jrRtn, jrRst);
				    		}
			    			
			    			if(!"".equals(sBookId))
			    			{
			    				//박판슬라브 작업예약취소
	    			        	JDTORecord jrRst = JDTORecordFactory.getInstance().create();
	    			        	jrParam.setField("YD_WBOOK_ID"  , sBookId);
		        				
	    			        	EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
	    			        	jrRst = (JDTORecord)ejbConn.trx("cancelSlabwbookInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		        				jrRtn = commUtils.addSndData(jrRtn, jrRst);
			    			}
			    		}
		    		}
	    		} //end of for 
	    	
	        	if(!"".equals(scardNo))
	        	{
					/**********************************************************
					* TB_YF_STKLYR 하차인 경우 차량위치재료 종료처리
					**********************************************************/
	    			jrParam.setField("CARD_NO", scardNo);
	    			commDao.update(jrParam, modifyCardNoOflayerEND, logId, methodNm, "TB_YF_STACKLAYER 하차인 경우 차량위치재료 종료처리");
	        		
					/**********************************************************
					* TB_YD_CARFTMVMTL 차량이송재로 종료처리
					**********************************************************/ 
	    			jrParam.setField("CARD_NO", scardNo);
	    			commDao.update(jrParam, modifyCardNoOfDetailEND, logId, methodNm, "TB_YD_CARFTMVMTL 차량이송재로 종료처리");
	    			
					/**********************************************************
					* TB_YD_CARSCH 차량스케줄 종료 처리
					**********************************************************/ 
	    			jrParam.setField("CARD_NO", scardNo);
	    			commDao.update(jrParam, modifyCardNoOfEND, logId, methodNm, "TB_YD_CARSCH 차량스케줄 종료 처리");
			        
					/**********************************************************
					* TB_YF_STKCOL 적치열 차량예약 포인트 지우기
					**********************************************************/ 
	    			jrParam.setField("CARD_NO", scardNo);
				    commDao.update(jrParam, updateCardNoOfStackCol1, logId, methodNm, "TB_YF_STACKCOL 적치열 차량예약 포인트 지우기");
			        
			        //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			        EJBConnector ejbConn2 = new EJBConnector("default","YfCommCarMvBakSeEJB",this);
					ejbConn2.trx("YfCarPointinforeg", new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class},
				  	             new Object[]{"A", "", scardNo, "", "", "", "C", logId, methodNm});
	        	}
	    	}
			commUtils.printLog(logId, methodNm, "S-");
			
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
	}
	
	/**	
	 * [A] 오퍼레이션명 : 저장이동조건 수신(POYMJ007)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ007(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "저장이동조건 수신[YfCommSeEJBSBean.rcvPOYMJ007] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		
	    String[]	rVal 		= new String[2];
	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			
			if ("".equals(modifier)) 
			{
				modifier = msgId; 
			}
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
			
			commUtils.printLog(logId, "=============저장이동조건 수신 시작========", "SL");
			
			String sYD_GP    = commUtils.trim(rcvMsg.getFieldString("yardID"));
			String sSTL_NO	 = commUtils.trim(rcvMsg.getFieldString("stockid"));
			
			/**
			* 1. 수신 항목 값 Check
			*/
			if ("".equals(sYD_GP)) 
			{
				throw new Exception("야드 구분이 없습니다..");
			}
			
			if ("".equals(sSTL_NO)) 
			{
				throw new Exception("저장품이 없습니다..");
			}

			/**
			* 2. 수신 실적 처리 - 저장조건을 (정정작업지시대기/압연지시대기) 로 변경
			*/
			jrParam.setField("STL_NO",	sSTL_NO);
			jrParam.setField("TC_CD",	YfConstant.POYMJ007);
			
			String sSTOCK_MOVE_TERM = "";

			if (sYD_GP.equals(YfConstant.YD_GP_0))		//A열연 Slab
			{	
				rVal = commUtils.getSlabCurrProgCd(sSTL_NO, YfConstant.POYMJ007);
				sSTOCK_MOVE_TERM = rVal[1];
				
			} 
			else if (sYD_GP.equals(YfConstant.YD_GP_1))	//A열연 Coil 
			{
				rVal = commUtils.getCoilCurrProgCd(sSTL_NO, YfConstant.POYMJ007);
				sSTOCK_MOVE_TERM = rVal[1];
			}
			
    		jrParam.setField("STL_NO",			sSTL_NO);
    		jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
    		commDao.update(jrParam, updateStockTransInfo);
    		
    		commUtils.printLog(logId, "=============저장이동조건 수신 종료========", "SL");
			
			commUtils.printLog(logId, methodNm, "S-");

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
	}
	
	/**
	 *      [A] 오퍼레이션명 : INSERT,UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public int execQueryIdTx(JDTORecord rcvMsg, String queryId) throws DAOException 
	{
		String methodNm = "Transaction 분리 수행 [YfCommSeEJB.execQueryIdTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		int intRtnVal = 0;
		
		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			intRtnVal = commDao.update(rcvMsg, queryId, logId, methodNm, "Transaction 분리 수행");
			
			commUtils.printLog(logId, methodNm, "S-");
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return intRtnVal;
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Queue로 전문 1건 송신
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String queueNm
	 *      @param JDTORecord sndMsg
	 *      @return void
	 *      @throws DAOException
	*/
	public void sndQueue(String logId, String methodNm, String queueNm, JDTORecord sndMsg) throws DAOException {
		try {
			commUtils.printParam(logId + " " + queueNm + " 송신  < " + methodNm, sndMsg);

			// Property Service 인스턴스를 취득합니다.
			PropertyService propertyService = PropertyService.getInstance();
			// Queue 명칭을 Property로부터 취득합니다.
			String queueName = propertyService.getProperty("common.properties", queueNm);

			JmsQueueSender sender = new JmsQueueSender();
			// Queue에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName);

			//JMS Log에 남으므로 초기화
			String rstCd = (logId == null || "".equals(logId)) ? null : logId;
			sndMsg.setResultCode(rstCd);
			sndMsg.setResultMsg(null);

			// Queue에 데이터를 전송합니다.
			String id = sender.send(sndMsg);

			commUtils.printLog(logId, queueNm + " 1 건 송신 : " + id, "SQ");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, queueNm + " 송신[sndQueue] < " + methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Queue로 전문 여러건 송신
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String queueNm
	 *      @param JDTORecord[] sndMsg
	 *      @return String[]
	 *      @throws DAOException
	*/
	public void sndQueue(String logId, String methodNm, String queueNm, JDTORecord[] sndMsg) throws DAOException {
		try {
			commUtils.printParam(logId + " " + queueNm + " 송신  < " + methodNm, sndMsg);

			// 프로퍼티 서비스 인스턴스를 취득합니다.
			PropertyService propertyService = PropertyService.getInstance();
			// 열연 EAI Queue 명칭을 프로퍼티로부터 취득합니다.
			String queueName = propertyService.getProperty("common.properties", queueNm);

			JmsQueueSender sender = new JmsQueueSender();
			// 큐에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName);

			//JMS Log에 남으므로 초기화
			String rstCd = (logId == null || "".equals(logId)) ? null : logId;
			for (int ii = 0; ii < sndMsg.length; ii++) {
				sndMsg[ii].setResultCode(rstCd);
				sndMsg[ii].setResultMsg(null);
			}

			// 큐에 데이터를 전송합니다.
			String[] id = sender.send(sndMsg);

			commUtils.printLog(logId, queueNm + " " + sndMsg.length + " 건 송신 : " +	commUtils.toString(id), "SQ");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, queueNm + " Multi송신[sndQueue] < " + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : EAI인터페이스 송신 처리 - Main 프로그램과 상관없이 무조건 전송
	 *      
	 *      [B] 처리 개요          : 3개(JMS_TC_CD, JMS_TC_CREATE_DDTT, JMS_TC_MESSAGE)의 항목이 반드시 존재하여야 함.
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public void sndToEAI(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "EAI송신[YfCommSeEJB.sndToEAI] < " + sndData.getResultMsg();
		JmsQueueSender queueSnder = new JmsQueueSender();
		
		try {
			commUtils.printLog(logId, methodNm, "I+");

			String msgId  = ""; //IF ID
			String tcMsg  = ""; //TCMessage
			String queueNm = "";
			JDTORecord sndMsg = null;
			JDTORecordSet sndMsgSet = null;

			Object obj = sndData.getField("SEND_DATA");

			if (obj == null) {
				sndMsg = sndData;
			} else {
				if (obj instanceof JDTORecord) {
					sndMsg = (JDTORecord)obj;
				} else if (obj instanceof JDTORecordSet) {
					sndMsgSet = (JDTORecordSet)obj;
				} else {
					commUtils.printLog(logId, methodNm + " : [SEND_DATA] 잘못된 Data Type입니다 .", "IS");
					return;
				}
			}
			
			//EAI Queue로 전송
			if (sndMsg != null) {
				//1건 전송
				msgId = commUtils.trim(sndMsg.getFieldString("JMS_TC_CD"     )); //IF ID
				tcMsg = commUtils.trim(sndMsg.getFieldString("JMS_TC_MESSAGE")); //TCMessage

				//불량 전문은 Logging하고 종료
				if ("".equals(msgId) || "".equals(tcMsg)) {
					commUtils.printParam("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없음", sndMsg);
					throw new Exception("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없는 전문입니다.");
				}
				
				//Queue명 조회 kbs
				queueNm = getQueueNm(logId, methodNm, msgId);
				//queueNm = queueSnder.getQueueName("YF",sndMsg);
				
				if ("".equals(queueNm)) {
					commUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsg);
					throw new Exception("[ " + msgId + " ]의 Queue명을 찾을 수 없습니다.");
				}

				//EAI Queue로 전송
				sndQueue(logId, methodNm, queueNm, sndMsg);
			} else {
				//Multi 전송
				int sndCnt = sndMsgSet.size(); //전송Data 건수

				if (sndCnt <= 0) {
					commUtils.printLog(logId, "전송할 Data가 존재하지 않습니다 . < " + methodNm, "IS");
					return;
				}
				
				//JMS에 송신하기 위해 JDTORecord[]에 Set
				JDTORecord[] sndMsgs = new JDTORecord[sndCnt];

				for (int ii = 0; ii < sndCnt; ii++) {
					msgId = commUtils.trim(sndMsgSet.getRecord(ii).getFieldString("JMS_TC_CD"     )); //IF ID
					tcMsg = commUtils.trim(sndMsgSet.getRecord(ii).getFieldString("JMS_TC_MESSAGE")); //TCMessage

					//불량 전문은 LIng하고 종료
					if ("".equals(msgId) || "".equals(tcMsg)) {
						commUtils.printParam("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없음", sndMsgSet.getRecord(ii));
						throw new Exception("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없는 전문입니다.");
					}

					//Queue명 조회 kbs
					queueNm = getQueueNm(logId, methodNm, msgId);
					//queueNm = queueSnder.getQueueName("YF",sndMsgs);
					
					
					if ("".equals(queueNm)) {
						commUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsg);
						throw new Exception("[ " + msgId + " ]의 Queue명을 찾을 수 없습니다.");
					}
					
					sndMsgs[ii] = sndMsgSet.getRecord(ii);
				}

				//EAI Queue로 전송
				sndQueue(logId, methodNm, queueNm, sndMsgs);
			}

			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : IF ID로 Queue명을 조회
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String msgId
	 *      @return String
	 *      @throws DAOException
	*/
	public String getQueueNm(String logId, String methodNm, String msgId) throws DAOException {
		try {
			String queueNm = ""; //JMS Queue명
			JmsQueueSender queueSnder = new JmsQueueSender();
			
			if ("".equals(msgId) || msgId.length() < 5) {
				return queueNm;
			}
				
			/******************************************
			 * 외부 EAI 
			 * - BRE에서 큐이름을 찾는다
			 ******************************************/
			if ("L".equals(msgId.substring(4, 5))) {
				//야드관리 EAI Queue
				queueNm = "jms.queue." + queueSnder.getQueueName("YF",msgId);
			} 

			/******************************************
			 * 내부 JMS
			 ******************************************/
			else 
			{
				
				JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
				if (jrRst != null && jrRst.size() > 0) 
				{
					queueNm = commUtils.trim(jrRst.getRecord(0).getFieldString("QUEUE_NAME")); //Queue명
			    }
				
				if ("".equals(queueNm) || !queueNm.startsWith("jms.queue.")) 
				{
					queueNm = "jms.queue." + msgId.substring(2, 4) + "_MDB_QUEUE";
				}
			}
			
			return queueNm;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, "Queue명[YfCommSeEJB.getQueueNm] < " + methodNm, e));
		}
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : IF ID로 Queue명을 조회
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String msgId
	 *      @return String
	 *      @throws DAOException
	
	public String getQueueNm(String logId, String methodNm, String msgId) throws DAOException {
		try {
			String queueNm = ""; //JMS Queue명

			if ("".equals(msgId) || msgId.length() < 5) {
				return queueNm;
			}
				
			//TB_YM_Z_IF Table에 등록된 Queue명을 조회
			JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
			
			if (jrRst != null && jrRst.size() > 0) {
				queueNm = commUtils.trim(jrRst.getRecord(0).getFieldString("QUEUE_NAME")); //Queue명
		    }

			//TB_YS_Z_IF Table에 등록된 Queue명이 없으면
			if ("".equals(queueNm) || !queueNm.startsWith("jms.queue.")) {
				queueNm = msgId.substring(2, 4); //수신처

				if ("YM".equals(queueNm)) {
					queueNm = "jms.queue.YM_MDB_QUEUE"; 	
				} else {
					if ("L".equals(msgId.substring(4, 5))) {
						//야드관리 EAI Queue
						queueNm = "jms.queue.YMB_EAI_QUEUE"; //야드관리 EAI Queue
					} else {
						//내부 JMS Queue명
						queueNm = "jms.queue." + queueNm + "_MDB_QUEUE"; //내부 JMS Queue명
					}
				}
			}
			
			return queueNm;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, "Queue명[YfCommSeEJB.getQueueNm] < " + methodNm, e));
		}
	}
	*/
	
	
	
	/**
	 *      [A] 오퍼레이션명 : JMS 인터페이스 송신 처리 - Main 프로그램과 상관없이 무조건 전송
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public void sndToJMS(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "JMS송신[YfCommSeEJB.sndToJMS] < " + sndData.getResultMsg();
		JmsQueueSender queueSnder = new JmsQueueSender();
		try {
			commUtils.printLog(logId, methodNm, "I+");

			String msgId = ""; //IF ID

			JDTORecord sndMsg = (JDTORecord)sndData.getField("SEND_DATA");
			//SEND_DATA가 없을 경우
			if (sndMsg == null) {
				sndMsg = sndData;
			}

			//JMS 송신 전문 IF ID
			msgId = commUtils.trim(sndData.getFieldString("JMS_TC_CD"));
			
			//불량 전문은 Logging하고 종료
			if ("".equals(msgId)) {
				commUtils.printParam("JMS_TC_CD가 없음", sndMsg);
				throw new Exception("JMS_TC_CD가 없는 전문입니다.");
			}
			
			//Queue명 조회 kbs
			String queueNm = getQueueNm(logId, methodNm, msgId);
			//String queueNm = queueSnder.getQueueName("YF",msgId);
			
			if ("".equals(queueNm)) {
				commUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsg);
				throw new Exception("[ " + msgId + " ]의 Queue명을 찾을 수 없습니다.");
			}

			//JMS Queue로 전송
			sndQueue(logId, methodNm, queueNm, sndMsg);

			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : EAI, JMS Interface 공통 수신 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	*/
	public void rcvInterface(JDTORecord rcvMsg) throws DAOException {
		String logId = commUtils.getLogId();
		String methodNm = "수신[YfCommSeEJB.rcvInterface]";
		String msgId = ""; //IF ID
		String msgNm = ""; //IF 명

		try {
			//JMS송신시 추가되는 항목값이 있으면 logId를 변경
			String uniqueId = commUtils.trim(rcvMsg.getFieldString("UNIQUE_ID"));
			if (!"".equals(uniqueId)) {
				logId = uniqueId;
			}
			
			commUtils.printLog(logId, "I/F" + methodNm, "I+");

			String classNm = ""; //처리 Class명
			String mthdNm  = ""; //처리 Method명
			String errMsg  = ""; //오류내용

			//EAI, JMS, HTTP(출하관리 등) 수신 전문 IF ID
			msgId = commUtils.getMsgId(rcvMsg);
			
			if ("".equals(msgId)) {
		    	errMsg = "수신된 전문의 IF ID가 존재하지 않습니다.";
			} else {
				JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
				
				if (jrRst != null && jrRst.size() > 0) { 
					msgNm   = commUtils.trim(jrRst.getRecord(0).getFieldString("IF_NM"       )); //IF 명
			    	classNm = commUtils.trim(jrRst.getRecord(0).getFieldString("CLASS_NAME"  )); //Class명
			    	mthdNm  = commUtils.trim(jrRst.getRecord(0).getFieldString("METHODE_NAME")); //Method명

			    	if ("".equals(classNm) || "".equals(mthdNm)) { 
				    	errMsg = "[ " + msgId + " ]의 처리 프로그램이 I/F(TB_YM_Z_IF) Table에 정의되지 않았습니다.\n";
				    }
			    } else {
			    	errMsg = "[ " + msgId + " ]의 정보가  I/F(TB_YM_Z_IF) Table에 존재하지 않습니다.\n";
			    }
			}

			methodNm = msgNm + "(" + msgId + ")" + methodNm;

			if (!"".equals(errMsg)) {
				commUtils.printParam(logId + " " + methodNm, rcvMsg);
				throw new Exception(errMsg);
			}

			rcvMsg.setResultCode(logId);
			rcvMsg.setResultMsg(methodNm);
			
			//수신 전문처리 Log
			commUtils.printLog(logId, msgNm + "(" + msgId + ") >> [ " + classNm + "." + mthdNm + " ]", "IR");

			EJBConnector rcvConn = new EJBConnector("default", classNm, this);
			
			JDTORecord jrRst = (JDTORecord)rcvConn.trx(mthdNm, new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				
				sndInterface(jrRst);
			}

			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, msgNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스 송신 처리 (EAI, JMS 공통)
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	*/
	public void sndInterface(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "I/F송신[YfCommSeEJB.sndInterface] < " + sndData.getResultMsg();
		JmsQueueSender queueSnder = new JmsQueueSender();
		try {
			commUtils.printLog(logId, methodNm, "I+");

			String msgId   = ""; //IF ID
			String queueNm = ""; //Queue명

			int msgNo  = 0;  //IF ID 번호
			int sndCnt = 0;  //전송Data 건수
			boolean chkOK = false; //정상여부 Check

			JDTORecordSet sndMsgSet = (JDTORecordSet)sndData.getField("SEND_DATA");
			if (sndMsgSet == null || sndMsgSet.size() <= 0) {
				commUtils.printLog("", commUtils.makeErrorLog(logId, methodNm, "전송할 Data가 존재하지 않습니다 ."), "IS");
				return;
			}

			//인터페이스 정보
			sndCnt = sndMsgSet.size(); //전송Data 건수
			int[][] msgNos = new int[sndCnt][sndCnt+1]; //인터페이스별 건수 및 IF ID 번호
			String[][] msgInfo = new String[sndCnt][2]; //IF ID, Queue명
			
			//같은 IF ID 끼리 정리
			for (int ii = 0; ii < sndCnt; ii++) {
				//EAI, JMS, HTTP(출하관리 등) 송신 전문 IF ID
				msgId = commUtils.getMsgId(sndMsgSet.getRecord(ii));
				
				if (!"".equals(msgId) && ((msgId.length() == 8)||(msgId.length() == 7))) {
					//기 등록된 List에서 찾기
					chkOK = true;
					for (int kk = 0; kk < sndCnt; kk++) {
						if (msgId.equals(msgInfo[kk][0])) {
							msgNos[kk][0] = msgNos[kk][0] + 1;
							msgNos[kk][msgNos[kk][0]] = ii;
							chkOK = false;
							break;
						}
					}

					//못 찾으면 신규로 등록
					if (chkOK) {
						//Queue명 조회 kbs
						queueNm = getQueueNm(logId, methodNm, msgId);
						//queueNm = queueSnder.getQueueName("YF",msgId);
						if (!"".equals(queueNm)) {
							msgNos[msgNo][0] = 1;
							msgNos[msgNo][1] = ii;
							msgInfo[msgNo][0] = msgId;
							msgInfo[msgNo][1] = queueNm;
							msgNo++;
						} else {
							commUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsgSet.getRecord(ii));
						}

					}
				}
			}
				
			//송신 전문 편성
			for (int ii = 0; ii < msgNo; ii++) {
				msgId   = msgInfo[ii][0];
				queueNm = msgInfo[ii][1];
				//msgId가 없으면 Skip
				if ("".equals(msgId)) {	continue; }

				int sCnt = 0;

				for (int kk = ii; kk < msgNo; kk++) {
					if (queueNm.equals(msgInfo[kk][1])) {
						sCnt = sCnt + msgNos[kk][0];
					}
				}
				
				//전송건수 별 전송처리
				if (sCnt == 1) {
					//전송건수가 1개일 경우
					JDTORecord sndMsg = sndMsgSet.getRecord(msgNos[ii][1]);

					//msgId 삭제
					msgInfo[ii][0] = "";

					if (sndMsg != null) {
						sndQueue(logId, methodNm, queueNm, sndMsg);
					}
				} else {
					//여러개일 경우
					int sNo  = 0;
					JDTORecord[] sndMsgs = new JDTORecord[sCnt];

					for (int kk = ii; kk < msgNo; kk++) {
						if (queueNm.equals(msgInfo[kk][1])) {
							for (int m = 1; m <= msgNos[kk][0]; m++) {
								JDTORecord sndMsg = sndMsgSet.getRecord(msgNos[kk][m]);

								if (sndMsg != null) {
									sndMsgs[sNo] = sndMsg;
									sNo++;
								}
							}

							msgInfo[kk][0] = "";
						}
					}

					//여러건 전송
					sndQueue(logId, methodNm, queueNm, sndMsgs);
				}
			}

			//송신 결과 Log 처리부분
			commUtils.printLog(logId, "전송 합계 : " + sndCnt + " 건", "IS");
			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드공통관리  코드 조회(WiseGrid)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getYfCode(GridData gdReq) throws DAOException {
		String methodNm = "Yf야드코드조회[YfCommSeEJB.getYfCode]";
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = commDao.getYfCode(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return commUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
//	사용안함 2019-12-10 삭제검토
//	/**
//	 *      [A] 오퍼레이션명 : 야드공통관리  코드 조회(WiseGrid)
//	 *
//	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 *      @param GridData gdReq
//	 *      @return GridData
//	 *      @throws DAOException
//	*/
//	public GridData getYfGridCode(GridData gdReq) throws DAOException {
//		String methodNm = "Yf야드Grid코드조회[YfCommSeEJB.getYfGridCode]";
//		String logId = gdReq.getIPAddress();
//
//		try {
//			//Return Value
//			JDTORecord jrRtn = null;
//
//			//DAO Parameter - Log ID, Method, 수정자 Set
//			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
//
//			String pColID = gdReq.getParam("pColID");
//			String pItem = gdReq.getParam("pItem");
//			String pCategory = commUtils.nvl(gdReq.getParam("pCategory"),"HS0000");
//			
//			CmCode ccCmCode = new CmCode(pItem, pCategory);			
//			
//			String sCode = ccCmCode.getCodes();
//			String sName = ccCmCode.getNames();
//			String sArrayCode[] = sCode.split(";");
//			String sArrayName[] = sName.split(";");
//			
//			commUtils.printLog(logId, methodNm, "===start=====");
//			commUtils.printLog(logId, methodNm, sArrayCode.toString());
//			commUtils.printLog(logId, methodNm, sArrayName.toString());
//			
//			JDTORecordSet jrRst = commDao.select(jrParam, "com.inisteel.cim.yf.acoil.acommon.dao.YfCommDAO.getCmCode", logId, methodNm, "그리드 콤보 추가");
//			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
//			return commUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
//		} catch(DAOException e) {
//			throw e;
//		} catch(Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//	}
		
	/**
	 * 화면 도움말 - 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord setPageHelpInfo(GridData gdReq) throws DAOException {
		String methodNm = "화면 도움말 등록[YfCommSeEJB.setPageHelpInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecordSet btnRecord;
			String pPAGE_ID = commUtils.trim(gdReq.getParam("PAGE_ID"	));
			
			jrParam.setField("PAGE_ID"		,pPAGE_ID); 
			jrParam.setField("PAGE_PT"		,gdReq.getParam("PAGE_PT"	)); 
			jrParam.setField("SCR_REMARK"	,gdReq.getParam("SCR_REMARK")); 
			jrParam.setField("DEL_YN"		,gdReq.getParam("DEL_YN"	)); 
			
			commDao.update(jrParam, setPageHelpInfo, logId, methodNm, "화면도움말등록");
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			String sBTN_IMG_PATH = "";
			String sBTN_ID = "";
			String sBTN_NM = "";
			String sRVS_NO = "";
			for (int i = 0; i < rowCnt; i++) {
				sBTN_ID = commUtils.trim(commUtils.getValue(gdReq, "BTN_ID"		, i));
				
				jrParam.setField("PAGE_ID", pPAGE_ID);
				jrParam.setField("BUTTON_DISC", sBTN_ID);
				btnRecord = commDao.select(jrParam, getBtnInfo, logId, methodNm, "버튼정보 조회");
				
				if(btnRecord.size()>0){
					sBTN_IMG_PATH = btnRecord.getRecord(0).getFieldString("BUTTON_IMAGE_PATH");
					sBTN_NM = btnRecord.getRecord(0).getFieldString("BUTTON_NAME");
					sRVS_NO = btnRecord.getRecord(0).getFieldString("RVS_NO");
				}else{
					sBTN_IMG_PATH = "/images/button/ico_en_other.gif";
					sBTN_NM = "OTHER";
					sRVS_NO = "1";
				}
				
				// 파라미터 Set.
				jrParam.setField("PAGE_ID"	,pPAGE_ID); 
				jrParam.setField("BTN_ID"	,sBTN_ID);
				jrParam.setField("BTN_NM"	,sBTN_NM);								
				jrParam.setField("RVS_NO"	,sRVS_NO);
				jrParam.setField("BTN_IMG_PATH"	,sBTN_IMG_PATH);
				jrParam.setField("BTN_SEQ"	,commUtils.getValue(gdReq, "BTN_SEQ"	, i)	);
				jrParam.setField("BTN_DISC"	,commUtils.getValue(gdReq, "BTN_DISC"	, i)	);
				jrParam.setField("DEL_YN"	,"N");
				
				if(!"".equals(sBTN_ID)){
					commDao.update(jrParam, MergeHelpBtn, logId, methodNm, "박판열연 YF 화면 도움말 - 버튼등록");													
				}				
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPageHelpInfo
	
	
	/**
	 * 화면 도움말 - 버튼등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord setPageHelpBtnInfo(GridData gdReq) throws DAOException {
		String methodNm = "화면 도움말 - 버튼등록[YfCommSeEJB.setPageHelpBtnInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				jrParam.setField("PAGE_ID", gdReq.getParam("PAGE_ID"));
				commDao.update(jrParam, updPageHelpBtnRvsInit, logId, methodNm, "이전 버전 버튼 미사용 처리");
				String nextRvsNo = commDao.select(jrParam, getPageHelpBtnNextRvsNo, logId, methodNm, "다음 버전 조회").getRecord(0).getFieldString("RVS_NO");
				
			for (int ii = 0; ii < rowCnt; ii++) {
				
				// 파라미터 Set.
				jrParam.setField("PAGE_ID"	,commUtils.getValue(gdReq, "PAGE_ID"	, ii)	); 
				jrParam.setField("BTN_ID"	,commUtils.getValue(gdReq, "BTN_ID"		, ii)	);
				jrParam.setField("BTN_NM"	,commUtils.getValue(gdReq, "BTN_NM"		, ii)	);
				jrParam.setField("RVS_NO"	,nextRvsNo 										);
				jrParam.setField("BTN_IMG_PATH"	,commUtils.getValue(gdReq, "BTN_IMG_PATH"		, ii)	);
				jrParam.setField("BTN_DISC"	,commUtils.getValue(gdReq, "BTN_DISC"	, ii)	);
				jrParam.setField("REGISTER"	,commUtils.getValue(gdReq, "REGISTER"	, ii)	);
				jrParam.setField("MODIFIER"	,commUtils.getValue(gdReq, "MODIFIER"	, ii)	);
				
				commDao.update(jrParam, setPageHelpBtnInfo, logId, methodNm, "박판열연 YF 화면 도움말 - 버튼등록");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of setPageHelpBtnInfo
	
	/**
	 * 화면 도움말 - 버튼삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPageHelpInfo(GridData gdReq) throws DAOException {
		String methodNm = "화면 도움말 - 버튼삭제[YfCommSeEJB.delPageHelpInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			String pPAGE_ID = commUtils.trim(gdReq.getParam("PAGE_ID"	));
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int i = 0; i < rowCnt; i++) {
				
				// 파라미터 Set.
				jrParam.setField("PAGE_ID"	,pPAGE_ID); 
				jrParam.setField("BTN_ID"	,commUtils.trim(commUtils.getValue(gdReq, "BTN_ID"		, i)	));
				
				commDao.update(jrParam, delPageHelpBtnInfo, logId, methodNm, "박판열연 YF 화면 도움말 - 버튼삭제");				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delPageHelpInfo
	
	/**
	 * 화면 도움말 - 작업방법(버튼상세) 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord setPageHelpBtnDtlInfo(GridData gdReq) throws DAOException {
		String methodNm = "화면 도움말 - 작업방법(버튼상세) 등록[YfCommSeEJB.setPageHelpBtnDtlInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				jrParam.setField("PAGE_ID"	, gdReq.getParam("PAGE_ID"	));
				jrParam.setField("BTN_ID"	, gdReq.getParam("BTN_ID"	));
				commDao.update(jrParam, delPageHelpBtnDtlInfo, logId, methodNm, "이전 버전 data 삭제");

			for (int ii = 0; ii < rowCnt; ii++) {
				
				// 파라미터 Set.
				jrParam.setField("PAGE_ID"	,commUtils.getValue(gdReq, "PAGE_ID"	, ii)	); 
				jrParam.setField("BTN_ID"	,commUtils.getValue(gdReq, "BTN_ID"		, ii)	);
				jrParam.setField("BTN_SEQ"	,commUtils.getValue(gdReq, "BTN_SEQ"	, ii)	);
				jrParam.setField("BTN_CMNT"	,commUtils.getValue(gdReq, "BTN_CMNT"	, ii)	);
				jrParam.setField("BTN_DISC"	,commUtils.getValue(gdReq, "BTN_DISC"	, ii)	);
				jrParam.setField("REGISTER"	,commUtils.getValue(gdReq, "REGISTER"	, ii)	);
				jrParam.setField("MODIFIER"	,commUtils.getValue(gdReq, "MODIFIER"	, ii)	);
				commDao.update(jrParam, setPageHelpBtnDtlInfo, logId, methodNm, "작업방법(버튼상세)등록");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of setPageHelpBtnInfo
	
	
	/**
	 * 화면 도움말 - 신규 문서번호 채번
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord getPageHelpDocMaxDocSeq(JDTORecord inDto) throws DAOException {
		String methodNm = "화면 도움말 - 신규 문서번호 채번[YfCommSeEJB.getPageHelpDocMaxDocSeq ] < ";
		String logId = inDto.getResultCode();

		try {

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	// 처리결과 건수 담아서 리턴
			String nextSeq = commDao.select(inDto, getPageHelpDocMaxDocSeq,logId,methodNm,"도움말 문서번호 채번").getRecord(0).getFieldString("DOC_SEQ");
			jrRtn.addField("DOC_SEQ", nextSeq); // 처리 결과 수 return;
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of getPageHelpDocMaxDocSeq
	
	
	/**
	 * 화면 도움말 - 첨부문서 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord setPageHelpDoc(JDTORecord inDto) throws DAOException {
		String methodNm = "화면 도움말 - 첨부문서 등록[YfCommSeEJB.setPageHelpDoc] < ";
		String logId = inDto.getResultCode();

		try {

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	// 처리결과 건수 담아서 리턴


			int rtn = 0;	// 결과 처리건수
			
			rtn = commDao.insert(inDto, setPageHelpDoc, logId, methodNm, "도움말 첨부문서 등록");
			
			jrRtn.addField("rtn", "" + rtn); // 처리 결과 수 return;
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of setPageHelpDoc
	
	/**
	 * 화면 도움말 - 첨부문서 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPageHelpDoc(GridData gdReq) throws DAOException {
		String methodNm = "화면 도움말 - 첨부문서 삭제[YfCommSeEJB.updPageHelpDoc] < ";
		String logId = gdReq.getIPAddress();

		try {

			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			File rootDir = new File("/");
			String rootPath 	= rootDir.getAbsolutePath() + "/app/webdocs/hsteelApp/hsteelWeb";
			String pDOC_PATH	= "";		// 필수(삭제경로)
			String fullFilePath = "";
			String pPAGE_ID			= "";		// 화면ID
			String pDOC_SEQ			= "";		// 문서번호
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int i = 0; i < rowCnt; i++) {
				pDOC_PATH	= commUtils.getValue(gdReq, "DOC_PATH", i);		// 필수(삭제경로)
				pPAGE_ID		= commUtils.getValue(gdReq, "PAGE_ID", i);		// 화면ID
				pDOC_SEQ		= commUtils.getValue(gdReq, "DOC_SEQ", i);		// 문서번호
				fullFilePath 	= rootPath + "/" + pDOC_PATH; 
				
				logger.println(LogLevel.INFO, "################ 파라미터 ###############"	);
				logger.println(LogLevel.INFO, "# sFilePath        : " + fullFilePath		);
				logger.println(LogLevel.INFO, "# sPAGE_ID         : " + pPAGE_ID			);
				logger.println(LogLevel.INFO, "# sDOC_SEQ         : " + pDOC_SEQ 			);
				logger.println(LogLevel.INFO, "#########################################"	);
				
				YfCommUtils.fileDelete(fullFilePath);
				
				jrParam.setField("PAGE_ID"	, pPAGE_ID); 
				jrParam.setField("DOC_SEQ"	, pDOC_SEQ); 

				commDao.update(jrParam, updPageHelpDocDelYn, logId, methodNm, "도움말 첨부파일 삭제");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrParam;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPageHelpDoc
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 이송완료처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */
	public JDTORecord procFtmvCmtl(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "이송완료처리[YfCommSeEJB.procFtmvCmtl] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			String sSTOCK_ID = rcvMsg.getFieldString("STL_NO");
			
			/************************************************
			 ** 이송완료
			 ************************************************/
			commUtils.printLog("", "이송백업 실적처리 START", "[INFO]+");
			JDTORecord tcRecord = JDTORecordFactory.getInstance().create(); 
     
		    /*********************
		     * 실적BACKUP처리 CALL
		     *********************/
			//코일공통 업데이트
		    tcRecord.setField("COIL_NO", sSTOCK_ID);
		    
			//Coil공통 테이블 업데이트
		    EJBConnector ejbConnPT = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
			ejbConnPT.trx("UpdCoilComProg", new Class[] { JDTORecord.class }, new Object[] { tcRecord });
			
			
			//Coil공통 테이블 조회
			jrParam.setField("COIL_NO", sSTOCK_ID);
		    JDTORecord stlRecord  = commDao.select(jrParam, getCOILCOMM, logId, methodNm, "공통 테이블 조회").getRecord(0);
		    String sSTL_APPEAR_GP = commUtils.nvl(stlRecord.getFieldString("STL_APPEAR_GP"), "");
		    
		    
		    if (!"Y".equals(sSTL_APPEAR_GP)) {
				
	        	//TB_PT_STLFRTOMOVE update			
		    	jrParam.setField("STL_NO", sSTOCK_ID);
		    	
		    	//TB_PT_STLFRTOMOVE 테이블 업데이트
			    EJBConnector ejbConnPT2 = new EJBConnector("default", "YfCommBakSeEJB", this);
				ejbConnPT2.trx("updProcStlFrToMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			    //코일소재 이송완료실적(YDPTJ002)
				JDTORecord tcRecord2 = JDTORecordFactory.getInstance().create();

				tcRecord2.setField("JMS_TC_CD"         , "YDPTJ002");
				tcRecord2.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
				
			    tcRecord2.setField("STL_NO"             , StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
			    tcRecord2.setField("ORD_NO"             , StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));// 주문번호
			    tcRecord2.setField("ORD_DTL"            , StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));// 주문행번
			    tcRecord2.setField("PLNT_PROC_CD"       , StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));// 공장공정코드
			    tcRecord2.setField("STL_APPEAR_GP"      , StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));// 재료외형구분
			    tcRecord2.setField("CURR_PROG_CD"       , StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));// 현재진도코드
			    tcRecord2.setField("ORD_YEOJAE_GP"      , StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));// 주문여재구분
			    tcRecord2.setField("STL_WT"             , StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));// 재료중량 (SLAB중량)
		    	tcRecord2.setField("DS_MTL_WT"          , "");// 설계재료중량
			    tcRecord2.setField("MTL_STAT_GP"        , StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));// 재료상태구분
			    tcRecord2.setField("RECORD_END_GP"      , StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));// Record 종료구분
			    tcRecord2.setField("RECORD_END_GP1"     , "");//Record 종료구분 1
			    tcRecord2.setField("BEFO_PROG_CD"       , StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));//전진도 코드
			    tcRecord2.setField("BEF_ORD_NO"         , StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));// 전주문 번호
			    tcRecord2.setField("BEF_ORD_DTL"        , StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));// 전주문 행번
			    tcRecord2.setField("MMATL_FEE_NO"       , StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));// 모재료번호
			    tcRecord2.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));// 목전충당구분	
			
			    //내부인터페이스 송신모듈 호출 
				jrRtn = commUtils.addSndData(jrRtn, tcRecord2);	
			    
			    commUtils.printLog(logId, "YDPTJ002 코일소재 이송완료실적BACKUP처리", "[INFO]");
			}
		    
		    commUtils.printLog("", "이송백업 실적처리 END", "[INFO]-");
        	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	
	/**
	 *      [A] 오퍼레이션명 : Slab 이송지시 완료처리
	 *      <pre>
	 *				이송완료처리 
	 *				 - Slab상세내역조회 -> 수정
	 *				 - Pallet조회 -> 하차
	 *				 
	 *				 1. 주편 or 슬라브 판별하기
	 *				 2. 레코드 상태값에 따라 슬라브 OR 주편의 진도코드, 이송완료처리
	 *				 3. 주편일 경우 
	 *				     - YMCSJ001 전문발송
	 *				     - 전문 발송조건 : 슬라브 생산공장구분이 "M"이 아닌건(구입재 AND 여재 제외) 
	 *				 4. 공통에 전문 전송(YDCTJ032, YDPTJ001)
	 *      
	 *      </pre>
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */
	public JDTORecord updateFtmvCmtl_Slab(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "이송완료처리_SLAB[YmCommSeEJB.procFtmvCmtl_SLAB] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		JDTORecord jtoYdSlabComm = null;
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecordSet rsResult = null;
			String szCurrProgCd = YfConstant.CURR_PROG_CD_SLAB_A;
    	 	String sScarfingYn 		= "";
    	 	String sOrdYeojaeGp 	= "";
    	 	String sSlabCreateGp 	= "";
    	 	String currDate			= commUtils.getDateTime14();
    	 	
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			String sSTL_NO = rcvMsg.getFieldString("STL_NO");
			
			commUtils.printLog("", "이송백업 실적처리 START", "[INFO]+"); 
			
			/**********************************************************
			* 1. TB_PT_COILCOMM  진도코드 UPDATE
			**********************************************************/
			
			// 야드슬라브공통 뷰에서 주편 OR 슬라이브인지 판단한다.
			jrParam.setField("SLAB_NO"		, sSTL_NO		);  
			JDTORecordSet rsYdSlabComm = commDao.select(jrParam, getYdSlabCommonInfo, logId, methodNm, "야드 VW_YD_SLABCOMM 조회"); 
			
			if(rsYdSlabComm == null || rsYdSlabComm.size() < 1) {
				throw new Exception("야드슬라브공통[VW_YD_SLABCOMM]에서 정보를 찾을 수 없습니다.");
			}


			jtoYdSlabComm = rsYdSlabComm.getRecord(0);
			String sSalbGp = jtoYdSlabComm.getFieldString("SLAB_GP");

			// 슬라브
			if("S".equals(sSalbGp)){
				jrParam.setField("SLAB_NO"		, sSTL_NO		);
				rsResult = commDao.select(jrParam, getListcurrprogcdSlab, logId, methodNm, "공정 함수를 이용한 진도코드 가져오기 "); 
				
				if(rsResult.size() > 0) {
					szCurrProgCd = rsResult.getRecord(0).getFieldString("CURR_PROG_CD");
				}
				
				//SLAB공통 진도코드 UPDATE
				jrParam.setField("CURR_PROG_CD"		, szCurrProgCd		);
			    EJBConnector ejbConnPT = new EJBConnector("default", "YfCommBakSeEJB", this);
			    ejbConnPT.trx("updSlabCommCurrProgCdTx", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			// 주편
			else if("M".equals(sSalbGp)){
				//RECORD_PROG_STAT != '3'
				//공정 함수를 이용한 진도코드 가져오기
				jrParam.setField("MSLAB_NO"		, sSTL_NO		);  
				rsResult = commDao.select(jrParam, getListcurrprogcd, logId, methodNm, "공정 함수를 이용한 진도코드 가져오기 "); 
				
				if(rsResult.size() > 0) {
					szCurrProgCd = rsResult.getRecord(0).getFieldString("CURR_PROG_CD");
				}
				
				//주편공통 진도코드 UPDATE
				jrParam.setField("CURR_PROG_CD"		, szCurrProgCd		);
				
			    EJBConnector ejbConnPT = new EJBConnector("default", "YfCommBakSeEJB", this);
			    ejbConnPT.trx("updMSlabCommCurrProgCdTx", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			    
	    	 	sScarfingYn 	= jtoYdSlabComm.getFieldString("SCARFING_YN");
	    	 	sOrdYeojaeGp 	= jtoYdSlabComm.getFieldString("ORD_YEOJAE_GP");
	    	 	sSlabCreateGp 	= jtoYdSlabComm.getFieldString("SLAB_CREATE_GP"); 
				
	    	 	commUtils.printLog(logId, methodNm,"▶▶정정작업 sScarfingYn ◀◀"+sScarfingYn);
	    	 	commUtils.printLog(logId, methodNm,"▶▶정정작업 sOrdYeojaeGp◀◀"+sOrdYeojaeGp);
	    	 	commUtils.printLog(logId, methodNm,"▶▶정정작업 sSlabCreateGp◀◀"+sSlabCreateGp);
	    	 	
	    	 	if(!"M".equals(sSTL_NO.substring(0,1))) { //슬라브 생산공장구분이 "M"이 아니고
	    	    	if("N".equals(sScarfingYn)){ //Non Scarfing 대상재 중
	    	    		if("G".equals(sSlabCreateGp)&&"2".equals(sOrdYeojaeGp)){
	    	    			//구입재이면서 여재인것은 제외
	    	    		} else {
			    			JDTORecord tEndRecord = null;
			    			tEndRecord = JDTORecordFactory.getInstance().create(); 
			    			tEndRecord.setField("JMS_TC_CD", "YMCSJ001");
			    			tEndRecord.setField("JMS_TC_CREATE_DDTT", currDate);						
			    			tEndRecord.setField("MSLAB_NO",sSTL_NO);
	    	    			
			    			jrRtn = commUtils.addSndData(jrRtn, tEndRecord);
	    	    		}
	    	    	}
	    	 	}
			}
			
			/**********************************************************
			* 2. 이송지시 실적처리
			**********************************************************/
	    	jrParam.setField("STL_NO", sSTL_NO);
	    	//TB_PT_STLFRTOMOVE 테이블 업데이트
		    EJBConnector ejbConnPT2 = new EJBConnector("default", "YfCommBakSeEJB", this);
			ejbConnPT2.trx("updProcStlFrToMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			// 진도 변경때문에 재조회 처리함
			rsYdSlabComm = commDao.select(jrParam, getYdSlabCommonInfo, logId, methodNm, "야드 VW_YD_SLABCOMM 조회");
			jtoYdSlabComm = rsYdSlabComm.getRecord(0);
			
			/**********************************************************
			* 3. YDPTJ001(슬라브소재이송완료실적) 전문발송처리
			**********************************************************/
			JDTORecord FrtoendRecord = null;
			FrtoendRecord = JDTORecordFactory.getInstance().create();
			FrtoendRecord.setField("JMS_TC_CD" , "YDPTJ001");
			FrtoendRecord.setField("JMS_TC_CREATE_DDTT" , currDate);
			FrtoendRecord.setField("STL_NO" , sSTL_NO);// 재료번호
			FrtoendRecord.setField("ORD_NO" , jtoYdSlabComm.getFieldString("ORD_NO")); // 주문번호
			FrtoendRecord.setField("ORD_DTL" , jtoYdSlabComm.getFieldString("ORD_DTL")); // 주문행번
			FrtoendRecord.setField("PLNT_PROC_CD" , jtoYdSlabComm.getFieldString("PLNT_PROC_CD"));// 공장공정코드
			FrtoendRecord.setField("STL_APPEAR_GP" , jtoYdSlabComm.getFieldString("STL_APPEAR_GP"));// 재료외형구분
			FrtoendRecord.setField("CURR_PROG_CD" , jtoYdSlabComm.getFieldString("CURR_PROG_CD"));// 현재진도코드
			FrtoendRecord.setField("ORD_YEOJAE_GP" , jtoYdSlabComm.getFieldString("ORD_YEOJAE_GP")); // 주문여재구분
			FrtoendRecord.setField("DS_MTL_WT" , ""); // 설계재료중량
			FrtoendRecord.setField("MTL_STAT_GP" , jtoYdSlabComm.getFieldString("RECORD_PROG_STAT")); // 재료상태구분
			FrtoendRecord.setField("RECORD_END_GP" , jtoYdSlabComm.getFieldString("RECORD_END_GP"));// Record 종료구분
			FrtoendRecord.setField("RECORD_END_GP1" , "");
			FrtoendRecord.setField("MMATL_FEE_NO" , "");// 모재료번호
			
			// 슬라브
			if("S".equals(sSalbGp)){
				FrtoendRecord.setField("STL_WT" , jtoYdSlabComm.getFieldString("SLAB_WT"));// 재료중량 (SLAB중량)
				FrtoendRecord.setField("BEFO_PROG_CD" , jtoYdSlabComm.getFieldString("BEFO_PROG_CD"));// 전진도 코드
				
				FrtoendRecord.setField("BEF_ORD_NO" , jtoYdSlabComm.getFieldString("BEF_ORD_NO"));// 전주문 번호
				FrtoendRecord.setField("BEF_ORD_DTL" , jtoYdSlabComm.getFieldString("BEF_ORD_DTL"));// 전주문 행번
				FrtoendRecord.setField("ORDERTRANS_MATCH_GP" , jtoYdSlabComm.getFieldString("MATCH_ORDERTRANS_GP"));// 목전충당구분
			}
			// 주편
			else{
				FrtoendRecord.setField("STL_WT" , jtoYdSlabComm.getFieldString("MSLAB_WT"));// 재료중량 (SLAB중량)
				FrtoendRecord.setField("BEFO_PROG_CD" , jtoYdSlabComm.getFieldString("BEFO_PROG_CD"));// 전진도 코드
				FrtoendRecord.setField("BEF_ORD_NO" , "");// 전주문 번호
				FrtoendRecord.setField("BEF_ORD_DTL" , "");// 전주문 행번
				FrtoendRecord.setField("ORDERTRANS_MATCH_GP" , "");// 목전충당구분
			}
			jrRtn = commUtils.addSndData(jrRtn, FrtoendRecord);
			
			/**********************************************************
			* 4. YDCTJ032(열연장입진행실적) 전문발송처리
			**********************************************************/
			// 이송완료 후 YDCTJ032전문 송신
			JDTORecord FrtoendRecord2 = null;
			FrtoendRecord2 = JDTORecordFactory.getInstance().create();
			FrtoendRecord2.setField("JMS_TC_CD" , "YDCTJ032");
			FrtoendRecord2.setField("JMS_TC_CREATE_DDTT" , currDate);
			FrtoendRecord2.setField("PTOP_PLNT_GP" , "HB");
			FrtoendRecord2.setField("STL_APPEAR_GP" , "C");
			FrtoendRecord2.setField("CHG_SUP_PROG_STAT" , "09");
			FrtoendRecord2.setField("WR_OCCR_DT" , currDate);
			FrtoendRecord2.setField("YD_EQP_WR_CNT" , "1");
			FrtoendRecord2.setField("STL_NO1" , sSTL_NO);
			jrRtn = commUtils.addSndData(jrRtn, FrtoendRecord2);
			
			
		    commUtils.printLog("", "이송백업 실적처리 END", "[INFO]-");
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 이송완료처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */
	public boolean updProcStlFrToMove(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "이송완료처리[YfCommSeEJB.updProcStlFrToMove] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commDao.update(rcvMsg, updateunLoadTimeToPT, logId, methodNm, "이송완료");
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch(Exception e) {
			
		}
		return true;

	}
	
	/**
	 *      [A] 오퍼레이션명 : SLAB 공통 Table 진도코드를 UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *		@ejb.transaction type="RequiresNew"
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean updSlabCommCurrProgCdTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB공통 진도코드 update[YfCommSeEJB.updSlabCommCurrProgCdTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+"); 
			//수신 항목 값
			// CURR_PROG_CD : 진도코드
			// STL_NO : MLAB번호 or 주편번호
			commDao.update(rcvMsg, updateMatlFtmvWlrstSlabNEW, logId, methodNm, "SLAB공통 진도코드 수정");				
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} // end of SLAB 공통 Table 저장위치를 UPDATE (Transaction 분리)
	
	
	/**
	 *      [A] 오퍼레이션명 : SLAB 공통 Table 저장위치 UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *		@ejb.transaction type="RequiresNew"
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */	
	public JDTORecord updateSlabCommonLocInfo(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "SLAB 공통 Table 저장위치 UPDATE[YfCommSeEJB.updateSlabCommonLocInfo] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			JDTORecord jrRtn = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value

			jrRtn.setField("SLAB_NO", rcvMsg.getFieldString("SLAB_NO"));
			JDTORecordSet rsSlabQuery = commDao.select(jrRtn, getYdSlabCommonInfo, logId, methodNm, "SLAB 조회");
			String slabGp = "";
			
			if(rsSlabQuery.size()>0){
				slabGp = StringHelper.evl(rsSlabQuery.getRecord(0).getFieldString("SLAB_GP"),"");

				jrRtn.setField("YD_GP", rcvMsg.getFieldString("YD_GP"));
				jrRtn.setField("BAY_GP", rcvMsg.getFieldString("BAY_GP"));
				jrRtn.setField("YD_EQP_GP", rcvMsg.getFieldString("YD_EQP_GP"));
				jrRtn.setField("YD_STK_COL_NO", rcvMsg.getFieldString("YD_STK_COL_NO"));
				jrRtn.setField("YD_STK_BED_NO", rcvMsg.getFieldString("YD_STK_BED_NO"));
				jrRtn.setField("YD_STK_LYR_NO", rcvMsg.getFieldString("YD_STK_LYR_NO"));
				jrRtn.setField("SLAB_NO", rcvMsg.getFieldString("SLAB_NO"));
				
				if("S".equals(slabGp)){
					commDao.update(jrRtn, updateSlabCommonLocInfo, logId, methodNm, "SLABCOMM UPDATE 저장위치");
				}else if("M".equals(slabGp)){
					commDao.update(jrRtn, updateMslabCommonLocInfo, logId, methodNm, "MSLABCOMM UPDATE 저장위치");
				}
			}	
			
			if("BK".equals(rcvMsg.getFieldString("YD_EQP_GP"))){
				jrRtn.setField("SLAB_NO", rcvMsg.getFieldString("SLAB_NO"));
				
				commDao.update(rcvMsg, updateMslabCommonSubInfo, logId, methodNm, "SLABCOMMSUB UPDATE 보온뱅크적치유무");
				
				//슬라브(보온뱅크적치유무 , 보온뱅크장입시간)
				commDao.update(rcvMsg, updateSlabCommonSubInfo, logId, methodNm, "MSLABCOMMSUB UPDATE 보온뱅크적치유무 , 보온뱅크장입시간");
			}
			
			
		    commUtils.printLog("", "SLAB 공통 Table 저장위치 UPDATE END", "[INFO]-");
        	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : MSLAB공통 Table 진도코드를 UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean updMSlabCommCurrProgCdTx(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통 진도코드 update[YfCommSeEJB.updMSlabCommCurrProgCdTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// CURR_PROG_CD : 진도코드
			// STL_NO : MLAB번호 or 주편번호
			
			commDao.update(rcvMsg, updateMatlFtmvWlrstMSlabNEW, logId, methodNm, "주편공통 진도코드 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 차량스케줄관련 삭제 (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean delCarSchInfo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량스케줄 관련 정보 DELYN -> Y update[YfCommSeEJB.delCarSchInfo] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));

			String pTRN_EQP_CD = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));
			
			/*
			 * 1.차량 이송대상재 리셋	
			 */
			jrParam.setField("TRN_EQP_CD",	pTRN_EQP_CD);	//운송장비코드
			jrParam.setField("DEL_YN",		"Y");			//삭제유무 Y:삭제
			commDao.update(jrParam, updDelYnCarFtMvMtlByTrnEqpCd, logId, methodNm, "TB_YD_CARFTMVMTL 차량이송재료 삭제(DEL_YN='Y')처리 ");
				
			/*
			 * 2.차량 스케쥴 리셋	
			 */
			jrParam.setField("TRN_EQP_CD",	pTRN_EQP_CD);	//운송장비코드
			jrParam.setField("DEL_YN",		"Y");			//삭제유무 Y:삭제
			commDao.update(jrParam, updDelYnCarSchByTrnEqpCd, logId, methodNm, "TB_YD_CARSCH 차량스케줄 삭제(DEL_YN='Y')처리 ");	

			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}
}	
	