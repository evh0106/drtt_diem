/**
 * @(#)ACoilRcvFaEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/22
 * 
 * @description      박판열연 COIL 야드 L2 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/22   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.common.session;

import com.inisteel.cim.common.exception.DAOException;
//import com.inisteel.cim.yf.common.ThreadExceptionHandler;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.YfParseAndValidateTC;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

/**
 *      [A] 클래스명 :  박판열연 IF 수신
 *
 * @ejb.bean name="YfRcvFaEJB" jndi-name="YfRcvFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/

public class YfRcvFaEJBSBean extends BaseSessionBean 
{
	private static final long serialVersionUID = 1L;
	private Logger logger = new Logger("yf");
	
	private YfCommUtils yfCommUtils = new YfCommUtils();
	private YfParseAndValidateTC yfParseAndValidateTC = new YfParseAndValidateTC();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException 
	{
		
	}
	
    
	/**
	 * @(#)클래스 이름
	 *                   rcvInterface
	 *
	 * @description      클래스 설명
	 *                   박판열업으로 오는 모든 전문 받아 TC ID에 해당하는 메소드를 호출하는 메인 메소드
	 *
	 * @ejb.interface-method EJB FA 메소드 생성하는 태그입니다.
	 * @param  JDTORecord    indo
	 * @return void
	 * @throws DAOException
	 */	
	public String rcvInterface(JDTORecord into) throws DAOException 
	{
		String MSG_ID = "";
		String logId = yfCommUtils.getLogId();
		String methodNm = "수신[YfRcvFaEJB.rcvInterface]";
		try
		{
			String uniqueId = yfCommUtils.trim(into.getFieldString("UNIQUE_ID"));
			if (!"".equals(uniqueId)) 
			{
				logId = uniqueId;
			}
			
			logId = "[1]" + logId; //야드구분 1
			
			yfCommUtils.printLog(logId, "I/F" + methodNm, "I+");
			
			//1. TC ID 얻어오기 
			MSG_ID = yfCommUtils.nvl(into.getField("Telegram_Id"), "EMPTY");
			
			if("EMPTY".equals(MSG_ID))
			{
				MSG_ID = yfCommUtils.nvl(into.getField("TC_CODE"), "EMPTY");
			}
			if("EMPTY".equals(MSG_ID))
			{
				MSG_ID =  yfCommUtils.nvl(into.getField("MSG_ID"), "EMPTY");
			}
			if("EMPTY".equals(MSG_ID))
            {
                MSG_ID =  yfCommUtils.nvl(into.getField("JMS_TC_CD"), "EMPTY");
            }
			if("EMPTY".equals(MSG_ID))
            {
                MSG_ID =  yfCommUtils.nvl(into.getField("TcCode"), "EMPTY");
            }
			if("EMPTY".equals(MSG_ID))
            {
                MSG_ID =  yfCommUtils.nvl(into.getField("tcCode"), "EMPTY");
            }
            
			// PIDEV
			if("EMPTY".equals(MSG_ID))
            {
                MSG_ID =  yfCommUtils.nvl(into.getField("MQ_TC_CD"), "EMPTY");
            }


			//2. JMS(L3간 송수신)가 아닐 경우에만 전문유효성검사 : 
			//   JMS전문에는 큐 네임 같은 불필요한 항목이 중구난방으로 붙어오므로, 유효성 검사하기가 어려움
			// PIDEV			
//			if("".equals(yfCommUtils.trim(into.getFieldString("JMS_TC_CD"))) &&"".equals(yfCommUtils.trim(into.getFieldString("TcCode"))))
			if("".equals(yfCommUtils.trim(into.getFieldString("JMS_TC_CD"))) 
					&&"".equals(yfCommUtils.trim(into.getFieldString("TcCode")))
					&&"".equals(yfCommUtils.trim(into.getFieldString("MQ_TC_CD")))
				)
				
			{
			    JDTORecord validateJR = yfParseAndValidateTC.validateTC("ALL", into);

			    if(!"SUCCESS".equals(validateJR.getFieldString("STATE_MSG")))
	            {
			    	throw new DAOException("▒▒ [ " + MSG_ID + " ] 전문 layout 유효성검증 실패 : "+validateJR.getFieldString("STATE_MSG") + " ▒▒\n" + into.toString());
	            }
			}

			//3. 전문 파싱
			JDTORecord inDto = yfParseAndValidateTC.parseOutRcvTC(into);
			
			inDto.setResultCode(logId);
			inDto.setResultMsg(methodNm);

			// PIDEV			
//			//4. 메소드 호출
//			EJBConnector ejbConn = new EJBConnector("default", "YfRcvFaEJB", this);  
//			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcv" + MSG_ID,   new Class[] { JDTORecord.class }, new Object[] { inDto });
//			
//			
//			//5. 전문 전송
//			if (jrRst != null) 
//			{
//				jrRst.setResultCode(logId);
//				jrRst.setResultMsg(methodNm);
//				
//				ejbConn = new EJBConnector("default", "YfCommSeEJB", this);  
//				ejbConn.trx("sndInterface",   new Class[] { JDTORecord.class }, new Object[] { jrRst });
//			}
			
			if("".equals(yfCommUtils.trim(into.getFieldString("MQ_TC_CD")))) {
				//4. 메소드 호출
				EJBConnector ejbConn = new EJBConnector("default", "YfRcvFaEJB", this);  
				JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcv" + MSG_ID,   new Class[] { JDTORecord.class }, new Object[] { inDto });
			
				
				//5. 전문 전송
				if (jrRst != null) 
				{
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
					
					ejbConn = new EJBConnector("default", "YfCommSeEJB", this);  
					ejbConn.trx("sndInterface",   new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			} else {
				//PI 로직
				EJBConnector ejbConn = new EJBConnector("default", "YfCoilL3RcvPISeEJB", this);
				JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcv" + MSG_ID,   new Class[] { JDTORecord.class }, new Object[] { inDto });

				//5. 전문 전송
				if (jrRst != null) 
				{
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
					
					ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
					ejbConn.trx("sndInterface",   new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			}
		}
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}

		return "Y";
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일정보 수신(POYMJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPOYMJ001(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "코일정보 수신[YfRcvFaEJB.rcvPOYMJ001] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPOYMJ001", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		}
		catch (DAOException e) 
		{
			logger.println("==================rcvPOYMJ001 catch 1");
			throw e;
		} 
		catch (Exception e) 
		{
			logger.println("==================rcvPOYMJ001 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일 결번 실적 (POYMJ002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPOYMJ002(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "코일 결번 실적[YfRcvFaEJB.rcvPOYMJ002] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPOYMJ002", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPOYMJ002 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPOYMJ002 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 슬라브 Scarfing 출측 Line Off 요구 정보 수신 (POYMJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPOYMJ003(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "슬라브 Scarfing 출측 Line Off 요구 정보 수신[YfRcvFaEJB.rcvPOYMJ003] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ASlabRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPOYMJ003", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPOYMJ003 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPOYMJ003 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일 SPM/HFL 작업 요구 정보를 수신(POYMJ004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPOYMJ004(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "코일 SPM/HFL 작업 요구 정보를 수신[YfRcvFaEJB.rcvPOYMJ004] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPOYMJ004", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPOYMJ004 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPOYMJ004 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 슬라브 전단실적 (POYMJ005)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPOYMJ005(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "슬라브 전단실적[YfRcvFaEJB.rcvPOYMJ005] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ASlabRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPOYMJ005", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPOYMJ005 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPOYMJ005 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : SLAB 결번실적 (POYMJ006)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPOYMJ006(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "SLAB 결번실적[YfRcvFaEJB.rcvPOYMJ006] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ASlabRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPOYMJ006", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPOYMJ006 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPOYMJ006 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 저장이동조건 (POYMJ007)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPOYMJ007(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "저장이동조건[YfRcvFaEJB.rcvPOYMJ007] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPOYMJ007", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPOYMJ007 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPOYMJ007 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일공냉재 실적 (POYMJ008)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPOYMJ008(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "코일공냉재 실적[YfRcvFaEJB.rcvPOYMJ008] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPOYMJ008", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPOYMJ008 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPOYMJ008 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 슬라브 장입예정번호취소(PCYM001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPCYM001(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "슬라브 장입예정번호취소[YfRcvFaEJB.rcvPCYM001] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ASlabRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPCYM001", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPCYM001 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPCYM001 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 슬라브 장입예정번호등록 (PCYM002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPCYM002(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "슬라브 장입예정번호등록[YfRcvFaEJB.rcvPCYM002] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ASlabRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPCYM002", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPCYM002 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPCYM002 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 슬라브 미처리,반송 Slab 결번(PCYM003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPCYM003(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "슬라브 미처리,반송 Slab 결번[YfRcvFaEJB.rcvPCYM003] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ASlabRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPCYM003", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPCYM003 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPCYM003 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 소재차량도착Point 요구(TSYDJ002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ002(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "소재차량도착Point 요구[YfRcvFaEJB.rcvTSYDJ002] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYDJ002", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYDJ002 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYDJ002 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 소재차량 도착실적(TSYDJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ003(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "소재차량 도착실적[YfRcvFaEJB.rcvTSYDJ003] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYDJ003", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYDJ003 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYDJ003 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 소재차량 출발실적(TSYDJ004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ004(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "소재차량 출발실적[YfRcvFaEJB.rcvTSYDJ004] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYDJ004", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYDJ004 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYDJ004 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 소재차량 대기장도착(TSYDJ005)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ005(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "소재차량 대기장도착[YfRcvFaEJB.rcvTSYDJ005] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYDJ005", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYDJ005 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYDJ005 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 차량출발취소(TSYDJ014)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ014(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "차량출발취소[YfRcvFaEJB.rcvTSYDJ014] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYDJ014", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYDJ014 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYDJ014 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일충당실적(PTYDJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPTYDJ001(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "코일충당실적[YfRcvFaEJB.rcvPTYDJ001] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPTYDJ001", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPTYDJ001 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPTYDJ001 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일소재이송지시(PTYDJ002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPTYDJ002(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "코일소재이송지시[YfRcvFaEJB.rcvPTYDJ002] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPTYDJ002", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPTYDJ002 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPTYDJ002 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일소재임가공이송지시(PTYDJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvPTYDJ003(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "코일소재임가공이송지시[YfRcvFaEJB.rcvPTYDJ003] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvPTYDJ003", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvPTYDJ003 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvPTYDJ003 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일제품출하지시대기(DMYDR005)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR005(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일제품출하지시대기 [YfRcvFaEJB.rcvDMYDR005] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR005", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR005 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR005 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일제품반납대기(DMYDR008)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR008(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일제품반납대기 [YfRcvFaEJB.rcvDMYDR008] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				//취소('Y')
				EJBConnector ejbCon = new EJBConnector("default", "YfCommSeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("receiveCancel", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			else
			{
				//지시('N')
				EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR008", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR008 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR008 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일제품목전(DMYDR014)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR014(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일제품목전 [YfRcvFaEJB.rcvDMYDR014] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				//취소('Y')
				EJBConnector ejbCon = new EJBConnector("default", "YfCommSeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("receiveCancel", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			else
			{
				//지시('N')
				EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR014", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR014 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR014 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일제품보관지시(DMYDR027)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR027(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일제품보관지시 [YfRcvFaEJB.rcvDMYDR027] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				//취소('Y')
				EJBConnector ejbCon = new EJBConnector("default", "YfCommSeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("receiveCancel", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			else
			{
				//지시('N')
				EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR027", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR027 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR027 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일제품출하완료(DMYDR030)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR030(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일제품출하완료 [YfRcvFaEJB.rcvDMYDR030] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR030", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR030 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR030 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일제품반품(DMYDR033)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR033(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일제품반품 [YfRcvFaEJB.rcvDMYDR033] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR033", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR033 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR033 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일제품운송상차지시 (DMYDR060)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR060(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일제품운송상차지시[YfRcvFaEJB.rcvDMYDR060] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				//취소('Y')
				EJBConnector ejbCon = new EJBConnector("default", "YfCommSeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("receiveCancel", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			else
			{
				//지시('N')
				EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR060", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR060 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR060 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 대기장도착실적 (DMYDR061)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR061(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "대기장도착실적[YfRcvFaEJB.rcvDMYDR061] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR061", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR060 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR060 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일이송상차대기장도착PDA(DMYDR070)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR070(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일이송상차대기장도착PDA[YfRcvFaEJB.rcvDMYDR070] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				//취소('Y')
				EJBConnector ejbCon = new EJBConnector("default", "YfCommSeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("receiveCancel", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			else
			{
				//지시('N')
				EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR070", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR070 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR070 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일이송상차도착PDA(DMYDR071)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR071(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일이송상차도착PDA[YfRcvFaEJB.rcvDMYDR071] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR071", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR071 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR071 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일이송상차완료PDA(DMYDR072)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR072(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일이송상차완료PDA[YfRcvFaEJB.rcvDMYDR072] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR072", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR072 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR072 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일이송하차대기장도착PDA(DMYDR073)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR073(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일이송하차대기장도착PDA[YfRcvFaEJB.rcvDMYDR073] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String cancelChk = yfCommUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));	//Y: 취소 , N: 지시
			
			if("Y".equals(cancelChk))
			{
				//취소('Y')
				EJBConnector ejbCon = new EJBConnector("default", "YfCommSeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("receiveCancel", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			else
			{
				//지시('N')
				EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR073", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			}
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR073 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR073 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일이송하차도착PDA(DMYDR074)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR074(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일이송하차도착PDA[YfRcvFaEJB.rcvDMYDR074] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR074", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR074 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR074 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일이송하차완료PDA(DMYDR075)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR075(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일이송하차완료PDA[YfRcvFaEJB.rcvDMYDR075] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvDMYDR075", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvDMYDR075 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvDMYDR075 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 구입슬라브등록실적(QMYDJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvQMYDJ001(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "구입슬라브등록실적[YfRcvFaEJB.rcvQMYDJ001] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ASlabRcvL3SeEJB", this);
			((Boolean)ejbCon.trx("rcvQMYDJ001", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg })).booleanValue();
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvQMYDJ001 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvQMYDJ001 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 슬라브보류실적(QMYDJ002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvQMYDJ002(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "슬라브보류실적[YfRcvFaEJB.rcvQMYDJ002] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ASlabRcvL3SeEJB", this);
			((Boolean)ejbCon.trx("rcvQMYDJ002", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg })).booleanValue();
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvQMYDJ002 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvQMYDJ002 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : Scarfing 대상재변경(QMYDJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvQMYDJ003(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "Scarfing 대상재변경[YfRcvFaEJB.rcvQMYDJ003] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ASlabRcvL3SeEJB", this);
			((Boolean)ejbCon.trx("rcvQMYDJ003", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg })).booleanValue();
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvQMYDJ003 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvQMYDJ003 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : SLAB No Action Method(YDYDJ630)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvYDYDJ630(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "Scarfing 대상재변경[YfRcvFaEJB.rcvQMYDJ003] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			/*
			 YDYDJ630 
			 YD에서 수행되고 YM에서는 SKIP 하는 TC
			*/
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvQMYDJ003 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvQMYDJ003 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL001(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "저장위치제원요구[YfRcvFaEJB.rcvF1YFL001] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL001", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL002(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "저장품제원요구[YfRcvFaEJB.rcvF1YFL002] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL002", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		}
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL003(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "C/R 운전모드 변경[YfRcvFaEJB.rcvF1YFL003] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL003", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL004(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "C/R 고장/복구실적[YfRcvFaEJB.rcvF1YFL004] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL004", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL006)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL006(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "CTS작업지시요구[YfRcvFaEJB.rcvF1YFL006] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL006", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL007)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL007(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "C/R 작업지시요구[YfRcvFaEJB.rcvF1YFL007] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL007", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL008)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL008(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "C/R 권상실적[YfRcvFaEJB.rcvF1YFL008] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL008", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL009)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL009(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "C/R 권하실적[YfRcvFaEJB.rcvF1YFL009] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL009", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL010)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL010(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "크레인 비상조업실적[YfRcvFaEJB.rcvF1YFL010] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL010", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL011)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL011(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "대차이동실적[YfRcvFaEJB.rcvF1YFL011] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL011", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL013)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL013(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "작업현황요구[YfRcvFaEJB.rcvF1YFL013] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL013", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL014)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL014(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "크레인작업 가능유무응답[YfRcvFaEJB.rcvF1YFL014] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL014", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL015)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL015(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "크레인작업 가능유무응답[YfRcvFaEJB.rcvF1YFL015] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL015", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL016)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL016(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "차량작업예정정보요구[YfRcvFaEJB.rcvF1YFL016] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL016", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL017)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL017(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "상차도 작업불가[YfRcvFaEJB.rcvF1YFL017] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL017", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL018)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL018(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "차량동간이적(도착)[YfRcvFaEJB.rcvF1YFL018] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL018", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL019)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL019(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "자동이적요구[YfRcvFaEJB.rcvF1YFL019] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL019", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL020)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL020(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "분기컨베이어트레킹(MILL TAKE OFF)[YfRcvFaEJB.rcvF1YFL020] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL020", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL021)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL021(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "CTS트레킹[YfRcvFaEJB.rcvF1YFL021] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL021", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL022)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL022(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "SPM트레킹[YfRcvFaEJB.rcvF1YFL022] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL022", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL023)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL023(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "EQL트레킹[YfRcvFaEJB.rcvF1YFL023] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL023", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL024)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL024(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "HFL트레킹[YfRcvFaEJB.rcvF1YFL024] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL024", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL027)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL027(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "분동코일 요구[YfRcvFaEJB.rcvF1YFL027] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL027", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL028)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL028(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "스크랩차량 차단기 /차량형상완료 정보[YfRcvFaEJB.rcvF1YFL028] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL028", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL030)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL030(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "크레인 주행금지구간정보[YfRcvFaEJB.rcvF1YFL030] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL030", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL031)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL031(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "크레인트레킹[YfRcvFaEJB.rcvF1YFL031] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL031", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL041)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL041(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "컨베이어Line-Off요구[YfRcvFaEJB.rcvF1YFL041] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL041", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL042)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL042(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "cts대차위치정보[YfRcvFaEJB.rcvF1YFL042] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL042", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvF1YFL043)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvF1YFL043(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "cts작업실적[YfRcvFaEJB.rcvF1YFL043] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL043", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**	
	 * [A] 오퍼레이션명 : OFF-LINE 크레인 변경 처리 (rcvYFYFJ305)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvYFYFJ305(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "OFF-LINE 크레인 변경 처리[YfRcvFaEJB.rcvYFYFJ305] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("offLineChgnCrn", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	/**	
	 * [A] 오퍼레이션명 : 명령선택기동(rcvYFYFJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvYFYFJ001(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "명령선택기동[YfRcvFaEJB.rcvYFYFJ001] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilSchSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvYFYFJ001", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**	
	 * [A] 오퍼레이션명 : 코일크레인스케줄 (rcvYFYFJ302)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvYFYFJ302(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일크레인스케줄[YfRcvFaEJB.rcvYFYFJ302] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilSchSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvYFYFJ302", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 코일크레인스케줄 멀티기동 (rcvYFYFJ303)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvYFYFJ303(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "코일크레인스케줄 멀티기동[YfRcvFaEJB.rcvYFYFJ303] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilSchSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvYFYFJ303", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : LINE-OFF 긴급작업(rcvYFYFJ304)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvYFYFJ304(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "LINE-OFF 긴급작업[YfRcvFaEJB.rcvYFYFJ304] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilSchSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvYFYFJ304", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 차량입동지시요구(YFYFJ662)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvYFYFJ662(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "차량입동지시요구[YfRcvFaEJB.YFYFJ662] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvYFYFJ662", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : (rcvYFYFJ998)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvYFYFJ998(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "쿼리IF생성[YfRcvFaEJB.rcvYFYFJ998] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvYFYFJ998", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**	
	 * [A] 오퍼레이션명 : (rcvYFYFJ999)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvYFYFJ999(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "쿼리IF생성[YfRcvFaEJB.rcvYFYFJ999] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvYFYFJ999", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/*************** ↓이하 냉연 인터페이스↓ ***************/
	
	/**	
	 * [A] 오퍼레이션명 : 냉연코일정보수신(CRYFJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvCRYFJ001(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "냉연코일정보수신[YfRcvFaEJB.CRYFJ001] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvCRYFJ001", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 저장품위치요구(CRYFJ002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvCRYFJ002(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "저장품위치요구[YfRcvFaEJB.CRYFJ002] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvCRYFJ002", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 냉연소재이송지시(CRYFJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvCRYFJ003(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "냉연소재이송지시[YfRcvFaEJB.CRYFJ003] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvCRYFJ003", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 냉연제품이송정보(순천)(CRYFJ004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvCRYFJ004(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "냉연제품이송정보(순천)[YfRcvFaEJB.CRYFJ004] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvCRYFJ004", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 소재차량도착Point 요구(냉연)(TSYDJ002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYFJ002(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "소재차량도착Point 요구(냉연)[YfRcvFaEJB.rcvTSYFJ002] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYFJ002", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYFJ002 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYFJ002 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 소재차량 도착실적(냉연)(TSYFJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYFJ003(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "소재차량 도착실적(냉연)[YfRcvFaEJB.rcvTSYFJ003] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYFJ003", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYFJ003 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYFJ003 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 소재차량 출발실적(냉연)(TSYFJ004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYFJ004(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "소재차량 출발실적(냉연)[YfRcvFaEJB.rcvTSYFJ004] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYFJ004", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYFJ004 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYFJ004 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 소재차량 대기장도착(냉연)(TSYFJ005)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYFJ005(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "소재차량 대기장도착(냉연)[YfRcvFaEJB.rcvTSYFJ005] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYFJ005", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYFJ005 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYFJ005 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 차량출발취소(냉연)(TSYFJ014)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYFJ014(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "차량출발취소(냉연)[YfRcvFaEJB.rcvTSYFJ014] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvTSYFJ014", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			logger.println("==================rcvTSYFJ014 catch 1");
			throw e;
		}
		catch (Exception e) 
		{
			logger.println("==================rcvTSYFJ014 catch 2");
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 사외창고배차정보(냉연)(DMYFJ004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYFJ004(JDTORecord rcvMsg) throws DAOException 
	{
		String 		methodNm	= "사외창고배차정보(냉연)[YfRcvFaEJB.DMYFJ004] < " + rcvMsg.getResultMsg();
		String 		logId		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try 
		{
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRtn = (JDTORecord)ejbCon.trx("rcvDMYFJ004", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
			
			yfCommUtils.printLog(logId, methodNm, "S-");
			 
			return jrRtn;
		} 
		catch (DAOException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 열연야드_THM_공장내외온습도정보[박판](X1YDL001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvX1YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "열연야드_THM_공장내외온습도정보[박판][YfRcvFaEJB.rcvX1YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "CondPredRcvL2SeEJB", this);
			jrRtn = (JDTORecord) ejbCon.trx("rcvX1YDL001", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });

			yfCommUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 열연야드_THM_공장내외온습도정보[1열연](X2YDL001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvX2YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "열연야드_THM_공장내외온습도정보[1열연][YfRcvFaEJB.rcvX2YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "CondPredRcvL2SeEJB", this);
			jrRtn = (JDTORecord) ejbCon.trx("rcvX1YDL001", new Class[] { JDTORecord.class }, new Object[] { rcvMsg }); // rcvX1YDL001 에서 공통으로 처리한다.

			yfCommUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 열연야드_THM_공장내외온습도정보[2열연](X3YDL001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvX3YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "열연야드_THM_공장내외온습도정보[2열연][YfRcvFaEJB.rcvX3YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			yfCommUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbCon = new EJBConnector("default", "CondPredRcvL2SeEJB", this);
			jrRtn = (JDTORecord) ejbCon.trx("rcvX1YDL001", new Class[] { JDTORecord.class }, new Object[] { rcvMsg }); // rcvX1YDL001 에서 공통으로 처리한다.

			yfCommUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(yfCommUtils.makeErrorLog(logId, methodNm, e));
		}
	}
} 
