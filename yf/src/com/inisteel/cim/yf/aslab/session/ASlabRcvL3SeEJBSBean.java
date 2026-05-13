/**
 * @(#)ASlabRcvL3SeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 Slab 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.aslab.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.common.session.YfComm;
import com.inisteel.cim.yf.common.YfQueryIF;
import com.inisteel.cim.yf.common.YfQueryIF2;

/**
 *      [A] 클래스명 : 박판열연 Slab 야드 L3수신 처리
 *
 * @ejb.bean name="ASlabRcvL3SeEJB" jndi-name="ASlabRcvL3SeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class ASlabRcvL3SeEJBSBean extends BaseSessionBean implements YfQueryIF, YfQueryIF2
{	
	private static final long serialVersionUID = 1L;
	private String classNm = getClass().getName();
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
	 * [A] 오퍼레이션명 : 슬라브 Scarfing 출측 Line Off 요구 정보 수신 (POYMJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ003(JDTORecord rcvMsg) throws DAOException 
	{	
		String		methodNm	= "슬라브 Scarfing 출측 Line Off 요구 정보 수신[ASlabRcvL3SeEJB.rcvPOYMJ003] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		
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
			
			commUtils.printLog(logId, "=============슬라브 Scarfing 출측 Line Off 요구 정보 수신 시작========", "SL");
			
			String sPlantGp      = commUtils.trim(rcvMsg.getFieldString("PlantGp"));
			String sProcGp       = commUtils.trim(rcvMsg.getFieldString("ProcGp"));
			String sWordUnitName = commUtils.trim(rcvMsg.getFieldString("WordUnitName"));  //작업지시단위명

			/**
			 * 1. 수신 항목 값 Check
			 */
//			if ("".equals(sSLAB_NO)) 
//			{
//				commUtils.printLog(logId, "수신한 SLAB_NO 없음", "[ERROR]");
//				return null;
//			} 

			/**********************************************************
			* 2.  조업 정정Table에서 저장품정보를 읽어서 해당 저장품의 저장품이동경로 항목을 UPDATE한다.
			**********************************************************/
			String sSTOCK_MOVE_TERM	= YfConstant.NEW_STOCK_MOVE_TERM_CC; // COIL 정정작업대기
			String sSTL_NO			= "";
			
			if(sPlantGp.equals(YfConstant.YD_GP_1))
			{
				sPlantGp = YfConstant.YD_GP_A;
			}
			else
			{
				sPlantGp = YfConstant.YD_GP_B;
			}
			
			jrParam.setField("HR_PLNT_GP",		sPlantGp);
			jrParam.setField("PROC_GP",			sProcGp);
			jrParam.setField("WORD_UNIT_NAME",	sWordUnitName);
	    	JDTORecordSet rst = commDao.select(jrParam, getPoPmStockInfo, logId, methodNm, "조업정보 검색");
	    	
	    	if (rst.size() == 0) 
	    	{
	    		commUtils.printLog(logId, "= 슬라브 Scarfing 출측 Line Off 요구 정보 수신 => 조업TABLE 저장품정보 존재안함.", "[ERROR]");
	    		return null;
	    	}
	    	
	    	for (int idx = 0; idx < rst.size() ; idx++) 
	    	{
	    		sSTL_NO = rst.getRecord(idx).getFieldString("STL_NO");
	    		
	    		jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
	    		jrParam.setField("STL_NO",			sSTL_NO);
	    		
		    	commDao.update(jrParam, updateMoveTermOfStock, logId, methodNm, "STOCK TABLE UPDATE");
	    	}
	    	
	    	commUtils.printLog(logId, "=============슬라브 Scarfing 출측 Line Off 요구 정보 수신 종료========", "SL");

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
	 * [A] 오퍼레이션명 : 슬라브 전단실적 (POYMJ005)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ005(JDTORecord rcvMsg) throws DAOException 
	{	
		String		methodNm	= "슬라브 전단실적[ASlabRcvL3SeEJB.rcvPOYMJ005] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		
	    String		szMsg		= "";
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
			
			commUtils.printLog(logId, "=============슬라브 전단실적 시작========", "SL");
			
			String sSLAB_NO		= commUtils.trim(rcvMsg.getFieldString("SlabNo"));		//슬라브 번호
			String sPROCESS_ID	= commUtils.trim(rcvMsg.getFieldString("ProcessID"));	//처리구분
			String sYD_GP 		= commUtils.trim(rcvMsg.getFieldString("yardID"));		//야드구분

			/**
			 * 1. 수신 항목 값 Check
			 */
			if("".equals(sSLAB_NO)) 
			{
				throw new Exception("슬라브번호 정보가 없습니다.");
			}
			
			if(sSLAB_NO.length() > 11 ) 
			{
				throw new Exception("슬라브번호의 길이가 11보다 작아야 합니다.");
			}

			if("7".equals(sPROCESS_ID))		//7:모 슬라브 종료처리 
			{
				szMsg="["+methodNm+"] PROCESS_ID가 7(:모 슬라브 종료)일 경우.. ";
				commUtils.printLog(logId, szMsg, "SL");
				
				jrParam = JDTORecordFactory.getInstance().create();
				
				/**
				* 2. TB_YF_STKLYR 의 적치상태를 적치가능으로 변경
				*/
				jrParam.setField("STL_NO",		sSLAB_NO);
				jrParam.setField("MODIFIER",	modifier);
				commDao.update(jrParam, updClrLyrByStockId, logId, methodNm, "TB_YF_STKLYR에 SLAB_NO가 위치하는 단을 CLEAR");
				
				/**
				* 3. TB_YF_STOCK의 DEL_YN = 'Y' 설정
				*/
				jrParam.setField("STL_NO",		sSLAB_NO);
				jrParam.setField("MODIFIER",	modifier);
				jrParam.setField("DEL_YN",		"Y");		//모슬라브 삭제
				commDao.update(jrParam, updStockDelYn, logId, methodNm, "저장품id로 TB_YF_STOCK의 DEL_YN = 'Y' 설정");
			}
			else
			{	
				szMsg="["+methodNm+"] PROCESS_ID가 7(:모 슬라브 종료)이 아닌 경우 연주전단 실적 처리 모듈 호출한다. ";
				commUtils.printLog(logId, szMsg, "SL");
				
				/**
				* 4. 슬라브 연주전단 실적 (CSYDJ001) 전송
				*/
				jrParam.setField("JMS_TC_CD",			"CSYDJ001");
				jrParam.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
				jrParam.setField("STL_NO",				sSLAB_NO);

				//CSYDJ001 슬라브 연주전단 실적 전송 ->rcvCSYDJ001()호출
				jrRtn = commUtils.addSndData(jrRtn, jrParam);
			}
	    	
	    	commUtils.printLog(logId, "=============슬라브 전단실적 종료========", "SL");

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
	 * [A] 오퍼레이션명 : 슬라브 결번실적 (POYMJ006)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ006(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "슬라브 결번실적 [ASlabL3RcvSeEJB.rcvPOYMJ006] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		
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
			
			commUtils.printLog(logId, "=============슬라브 결번실적 시작========", "SL");
			
			String sYD_GP   = commUtils.trim(rcvMsg.getFieldString("yardID"));
			String sSLAB_NO = commUtils.trim(rcvMsg.getFieldString("SlabNo"));

			/**
			* 1. 수신 항목 값 Check
			*/
			if ("".equals(sSLAB_NO)) 
			{
				throw new Exception("수신한 SLAB_NO 없음");
			}
			
			if (sSLAB_NO.length() > 11) 
			{
				throw new Exception("슬라브번호의 길이가 11보다 작아야 합니다.");
			}
			
			/**
			* 2.  저장품 테이블의 '저장품 이동 조건'을 UPDATE
			*/
			jrParam.setField("SLAB_NO", sSLAB_NO);
			JDTORecord rst = commDao.select(jrParam, selectSlabMatirialInfo2, logId, methodNm, "SLAB공통 조회").getRecord(0);
			
			String sCURR_PROG_CD     = rst.getFieldString("CURR_PROG_CD");
			String sWO_MSLAB_RPR_MTD = rst.getFieldString("WO_MSLAB_RPR_MTD");
			String sSTOCK_MOVE_TERM  = "";
			
			/* 일관제철 진도코드 */
	    	if(YfConstant.DMYDR016.equals(msgId))	//외판슬라브운송지시대기
	    	{				
	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_NS;
	    	}
	    	else if(YfConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)) 
	    	{    		
	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_11; 
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_12;	
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)) 
	    	{
	    		if ("Q".equals(sSTOCK_MOVE_TERM)) 
	    		{
	    			sWO_MSLAB_RPR_MTD = YfConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
	    		}
	    		else 
	    		{
	    			sWO_MSLAB_RPR_MTD = YfConstant.NEW_STOCK_MOVE_TERM_DS;
	    		}
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ES;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD))
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_FS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD))
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_BS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_YS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_KS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_LS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_MS;    		
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD))
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_NS;    	
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;    	
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD))
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;
	    	}		
			
	    	jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
	    	jrParam.setField("STL_NO",			sSLAB_NO);
	    	jrParam.setField("MODIFIER",		modifier);
	    	
	    	commDao.update(jrParam, updateMoveTermOfStock, logId, methodNm, "TB_YF_STOCK 의 STOCK_MOVE_TERM 을 UPDATE");
	    	
	    	commUtils.printLog(logId, "=============슬라브 결번실적 종료========", "SL");
	    	
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
	 * [A] 오퍼레이션명 : 슬라브 장입예정번호취소(PCYM001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPCYM001(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "슬라브 장입예정번호취소[ASlabRcvL3SeEJB.rcvPCYM001] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		
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
			
			commUtils.printLog(logId, "=============슬라브 장입예정번호취소 시작========", "SL");
			
			String sYD_GP   = commUtils.trim(rcvMsg.getFieldString("yardID"));  //야드구분
			String sSLAB_NO = commUtils.trim(rcvMsg.getFieldString("slabNo"));  //SLAB_NO
			
			/**
			 * 1. 수신 항목 값 Check
			 */
			if ("".equals(sSLAB_NO))
			{
				throw new Exception("수신한 SLAB_NO 없음");
			}
			
			/**
			 * 2. 관제 ReSchedul 취소에 따른 '장입LOT번호'를 UPDATE
			 */
			jrParam.setField("SLAB_NO", sSLAB_NO);
			JDTORecord rst = commDao.select(jrParam, selectSlabMatirialInfo2, logId, methodNm, "SLAB공통 조회").getRecord(0);
			
			String sCURR_PROG_CD     = rst.getFieldString("CURR_PROG_CD");
			String sWO_MSLAB_RPR_MTD = rst.getFieldString("WO_MSLAB_RPR_MTD");
			String sSTOCK_MOVE_TERM  = "";
			
	    	/* 일관제철 진도코드 */
	    	if (YfConstant.DMYDR016.equals(msgId)) 
	    	{				
	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_NS;	//외판슬라브운송지시대기
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)) 
	    	{    		
	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_11; 
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_12;	
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)) 
	    	{
	    		if ("Q".equals(sSTOCK_MOVE_TERM)) 
	    		{
	    			sWO_MSLAB_RPR_MTD = YfConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
	    		} 
	    		else 
	    		{
	    			sWO_MSLAB_RPR_MTD = YfConstant.NEW_STOCK_MOVE_TERM_DS;
	    		}
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ES;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_FS;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_BS;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_YS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_KS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD))
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_LS;
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD))
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_MS;    		
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD))
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_NS;    	
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD))
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;    	
	    	}
	    	else if (YfConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD))
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;
	    	}		
	    	
	    	jrParam.setField("CHARGE_LOT_NO",		"");
	    	jrParam.setField("STOCK_MOVE_TERM",	sWO_MSLAB_RPR_MTD);
	    	jrParam.setField("STL_NO",			sSLAB_NO);
			commDao.update(jrParam, updateZoneInOfStock, logId, methodNm, "TB_YF_STOCK 슬라브 장입예정번호취소");
    		
    		commUtils.printLog(logId, "=============슬라브 장입예정번호취소 종료========", "SL");
			
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
	 * [A] 오퍼레이션명 : 슬라브 장입예정번호등록(PCYM002)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPCYM002(JDTORecord rcvMsg) throws DAOException 
	{
		String			methodNm	= "슬라브 장입예정번호등록[ASlabRcvL3SeEJB.rcvPCYM002] < " + rcvMsg.getResultMsg();
		String			logId		= rcvMsg.getResultCode();
	    JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		
	    JDTORecord		jrRtn		= JDTORecordFactory.getInstance().create();
	    
	    JDTORecordSet	rsResult	= null;
	    int				cnt			= 0;

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
			
			commUtils.printLog(logId, "=============슬라브 장입예정번호등록 시작========", "SL");
			
			String sYD_GP   = commUtils.trim(rcvMsg.getFieldString("yardID"));  //야드구분
			String sSLAB_NO = commUtils.trim(rcvMsg.getFieldString("slabNo"));  //SLAB_NO
			
			/**********************************************************
			* 저장품의 장입순번을 CLEAR 한다.
			**********************************************************/
			rsResult = commDao.select(jrParam, readZoneInNoOfStock, logId, methodNm, "TB_YF_STOCK 조회");
			
			cnt = rsResult != null ? rsResult.size() : 0;
			
			for (int idx = 0; idx < cnt; idx++) 
			{
				jrParam.setField("STL_NO", rsResult.getRecord(idx).getFieldString("STL_NO"));
				commDao.update(jrParam, updateZoneInNoOfStock, logId, methodNm, "저장품의 장입순번을 CLEAR(TB_YF_STOCK)");
	        }
			
			/**********************************************************
			* 장입예정번호를 READ 한다.
			**********************************************************/
			JDTORecordSet rsResult4 = commDao.select(jrParam, selectZoneInStocks, logId, methodNm, "장입대기인 저장품을 조회");
			
			int cnt4 = rsResult != null ? rsResult.size() : 0;
			if (cnt4 == 0) 
			{
				//장입예정번호가 존재하지 않습니다
				return jrRtn;
	        }
			
			/**
             * 저장품 테이블에 READ한 Slab No를 Update 한다.
             * -저장품 상태	: "F".
             * -장입 LOT 번호	: READ한 예정번호. 
             */
			String sSTL_NO 				= "";
			String sLOT_PRIOR			= "";
			
			boolean notStock			= true;
			
			String sCURR_PROG_CD		= "";
			String sWO_MSLAB_RPR_MTD	= "";
			String sSTOCK_MOVE_TERM 	= "";
			
            for (int i = 0; i < cnt4; i++) 
            {    
            	sSTL_NO		= rsResult4.getRecord(i).getFieldString("STL_NO");
            	sLOT_PRIOR	= rsResult4.getRecord(i).getFieldString("LOT_PRIOR");
            	
            	notStock = true;
            	
                if(notStock) 
                {
                	jrParam.setField("SLAB_NO", sSTL_NO);
        			JDTORecord rst = commDao.select(jrParam, selectSlabMatirialInfo, logId, methodNm, "SLAB공통 조회").getRecord(0);
        			
        			sCURR_PROG_CD		= rst.getFieldString("CURR_PROG_CD");
        			sWO_MSLAB_RPR_MTD	= rst.getFieldString("WO_MSLAB_RPR_MTD");
        			sSTOCK_MOVE_TERM 	= "";
        			
        	    	/* 일관제철 진도코드 */
        	    	if (YfConstant.DMYDR016.equals(msgId)) 
        	    	{
        	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_NS;		//외판슬라브운송지시대기
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)) 
        	    	{    		
        	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_11; 
        	    	}
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_12;	
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)) 
        	    	{
        	    		if ("Q".equals(sSTOCK_MOVE_TERM)) 
        	    		{
        	    			sWO_MSLAB_RPR_MTD = YfConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
        	    		}
        	    		else 
        	    		{
        	    			sWO_MSLAB_RPR_MTD = YfConstant.NEW_STOCK_MOVE_TERM_DS;
        	    		}
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ES;
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_FS;
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_BS;
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_YS;
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_GS;		// 종합판정대기
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_HS;		// 입고대기
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_JS;		// 반납대기
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_KS;
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_LS;
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_MS;    		
        	    	}
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_NS;    	
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;    	
        	    	} 
        	    	else if (YfConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD)) 
        	    	{
        	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;
        	    	}		
        	    	
        	    	jrParam.setField("CHARGE_LOT_NO",	sLOT_PRIOR);
        	    	jrParam.setField("STOCK_MOVE_TERM",	sWO_MSLAB_RPR_MTD);
        	    	jrParam.setField("STL_NO",			sSTL_NO);
        			commDao.update(jrParam, updateZoneInOfStock, logId, methodNm, "슬라브 장입예정번호등록");
                }
            }
			
    		commUtils.printLog(logId, "=============슬라브 장입예정번호등록 종료========", "SL");
			
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
	 * [A] 오퍼레이션명 : 슬라브 미처리,반송 Slab 결번(PCYM003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPCYM003(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "슬라브 미처리,반송 Slab 결번[ASlabRcvL3SeEJB.rcvPCYM003] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		
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
			
			commUtils.printLog(logId, "=============슬라브 미처리,반송 Slab 결번 시작========", "SL");
			
			String sSLAB_NO = commUtils.trim(rcvMsg.getFieldString("slabNo"));  //SLAB_NO
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sSLAB_NO)) 
			{
				throw new Exception("수신한 SLAB_NO 없음");
			} 
			else if (sSLAB_NO.length() > 11) 
			{
				throw new Exception("SLAB_NO의 길이가 11보다 큼");
			}
			
			/**********************************************************
			* 2. 슬라브 공통 테이블의 진도코드를 참조해서 저장품이동조건을 가져온다. 
			**********************************************************/
			jrParam.setField("SLAB_NO", sSLAB_NO);
			JDTORecord rst = commDao.select(jrParam, selectSlabMatirialInfo2, logId, methodNm, "SLAB공통 조회").getRecord(0);
			
			String sCURR_PROG_CD     = rst.getFieldString("CURR_PROG_CD");
			String sWO_MSLAB_RPR_MTD = rst.getFieldString("WO_MSLAB_RPR_MTD");
			String sSTOCK_MOVE_TERM  = "";
			
			/* 일관제철 진도코드 */
	    	if (YfConstant.DMYDR016.equals(msgId)) 
	    	{
	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_NS;		//외판슬라브운송지시대기
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)) 
	    	{    		
	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_11; 
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM = YfConstant.NEW_STOCK_MOVE_TERM_12;	
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)) 
	    	{
	    		if ("Q".equals(sSTOCK_MOVE_TERM)) 
	    		{
	    			sWO_MSLAB_RPR_MTD = YfConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
	    		} 
	    		else 
	    		{
	    			sWO_MSLAB_RPR_MTD = YfConstant.NEW_STOCK_MOVE_TERM_DS;
	    		}
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ES;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_FS;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_BS;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_YS;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_KS;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_LS;
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_MS;    		
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_NS;    	
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;    	
	    	} 
	    	else if (YfConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD)) 
	    	{
	    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;
	    	}
	    	
	    	jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
	    	jrParam.setField("STL_NO",			sSLAB_NO);
	    	commDao.update(jrParam, updateMoveTermOfStock, logId, methodNm, "TB_YF_STOCK 수정");
    		
    		commUtils.printLog(logId, "=============슬라브 미처리,반송 Slab 결번 종료========", "SL");
			
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
	
}	
	