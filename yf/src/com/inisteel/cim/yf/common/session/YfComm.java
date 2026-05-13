package com.inisteel.cim.yf.common.session;

import java.util.List;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.YfQueryIF;
import com.inisteel.cim.yf.common.YfQueryIF2;
import com.inisteel.cim.yf.common.dao.YfCommDAO;

/**
 *      [A] 클래스명 : 박판열연 야드 공통 처리
 *
*/
public class YfComm implements YfQueryIF, YfQueryIF2
{
	private YfCommUtils commUtils	= new YfCommUtils();
	private YfCommDAO 	commDao		= new YfCommDAO();
	private Logger 		logger		= new Logger("yf");
			Boolean 	isSuccess	= new Boolean(false);
	
	/***************************************************************************
	 * 공통 Check
	 **************************************************************************/
	/**
	 *      [A] 오퍼레이션명 :  신규시스템 적용 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String ACoilApplyYn(String szREPR_CD_GP,String szCD_GP,String szITEM) throws DAOException 
	{
		String methodNm = "신규시스템 적용여부[YfComm.ACoilApplyYn]" ;
		String logId = "";
		String szAPPLY_YN = "N";

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", methodNm, "");
			jrParam.setField("REPR_CD_GP", szREPR_CD_GP  ); //작업구분
			jrParam.setField("CD_GP"     , szCD_GP       ); //구분
			jrParam.setField("ITEM"      , szITEM        ); //ITEM
			JDTORecordSet jsChk = commDao.select(jrParam, getACoilApplyYn, logId, methodNm, "열정보 Read"); 

			if (jsChk.size() > 0) {
				szAPPLY_YN    = commUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
			}
            
			commUtils.printLog(logId, methodNm, "S-");

			return szAPPLY_YN;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return szAPPLY_YN;
		} catch (Exception e) {
			return szAPPLY_YN;
		}
	}	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 저장품 이동 조건
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getStockMv(String logId , String methodNm, String StockId) throws DAOException 
	{
		String sProgCd 		= "";
		String sNextProc 	= "";
		String sPlanProc 	= "";
		String sCoilProc    = "";
		String sStockMv     = "";   //return sStockMv

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");
			jrParam.setField("COIL_NO", StockId);
			JDTORecordSet jsChk = commDao.select(jrParam, getCoilComByCurrProgCd, logId, methodNm, "열정보 Read"); 

			if (jsChk.size() > 0) 
			{
				sProgCd 	= commUtils.trim(jsChk.getRecord(0).getFieldString("CURR_PROG_CD"));
				sNextProc 	= commUtils.trim(jsChk.getRecord(0).getFieldString("NEXT_PROC"));
				sPlanProc 	= commUtils.trim(jsChk.getRecord(0).getFieldString("PLAN_PROC1"));
			}
			
			if("".equals(sNextProc))
			{
				sCoilProc = sPlanProc;
			}
			else
			{
				sCoilProc = sNextProc;
			}
			
			if(YfConstant.CURR_PROG_CD_COIL_1.equals(sProgCd))
			{
				sStockMv   = YfConstant.NEW_STOCK_MOVE_TERM_1C;
	    	}
			else
			{
	    		if("1H".equals(sCoilProc))
	    		{
	    			//HFL
					sStockMv = "1H";				
				}
	    		else if("8H".equals(sCoilProc))
	    		{
	    			//HFL 결속장 
					sStockMv = YfConstant.NEW_STOCK_MOVE_TERM_E1;			
				}
	    		else if("1Q".equals(sCoilProc))
	    		{
	    			//EQL
					sStockMv = "1Q";				
				}
	    		else if("1K".equals(sCoilProc))
	    		{
	    			//SPM
					sStockMv = "1K";				
				}
	    		else
	    		{ 
	    			//일반
					sStockMv =  "GN";
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return sStockMv;
		}
		catch (DAOException e) 
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), commUtils, e);
			return sStockMv;
		}
		catch (Exception e) 
		{
			return sStockMv;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 기동 조회
	 *
	 *      @param String JDTORecord rcvMsg
	 *      @return String JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCrnSchMsg(JDTORecord jrParam) {
 		/***************************************************************************
		 * 스케줄 기동시 사용: procCrnWrkBookMgtStart
		 **************************************************************************/
		
		String methodNm = "크레인스케줄전문조회[YfComm.getCrnSchMsg] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			String currDate   = commUtils.getDateTime14();									//현재시각
			String ydGp       = commUtils.trim(jrParam.getFieldString("YD_GP"        ));	//야드구분
			String ydWbookId  = commUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"  ));	//야드작업예약ID
			String ydSchCd    = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
			String ydEqpId    = commUtils.trim(jrParam.getFieldString("YD_EQP_ID"    ));	//야드설비ID
			String ydSchStGp  = commUtils.trim(jrParam.getFieldString("YD_SCH_ST_GP" ));	//야드스케쥴기동구분
			String ydSchReqGp = commUtils.trim(jrParam.getFieldString("YD_SCH_REQ_GP"));	//야드스케쥴요청구분
			String modifier   = commUtils.trim(jrParam.getFieldString("MODIFIER"     ));	//수정자
			String ejbCallYn  = commUtils.trim(jrParam.getFieldString("EJB_CALL_YN"  ));	//EJBCall여부(신 크레인스케줄)

			if ("".equals(ydWbookId) && "".equals(ydSchCd) && "".equals(ydEqpId)) 
			{
				if ("Y".equals(ejbCallYn))
				{
					throw new Exception("크레인스케줄 기동을 위한 정보가 없습니다.");
				} 
				else 
				{
					commUtils.printLog(logId, "크레인스케줄 기동을 위한 정보가 없습니다.", "SL");
					return null;
				}
			}

			//크레인스케줄기동구분 조회
			if (!"".equals(ydWbookId) && ("".equals(ydSchCd) || "".equals(ydEqpId))) 
			{
				jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
				JDTORecordSet jsChk = commDao.select(jrParam, getCrnSchStartGp, logId, methodNm, "크레인스케줄기동구분 조회");

				if (jsChk.size() > 0) 
				{
					ydGp       = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_GP"        ));	//야드구분
					ydSchCd    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
					ydEqpId    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"    ));	//야드설비ID
				} 
				else 
				{
					if ("Y".equals(ejbCallYn)) 
					{
						throw new Exception("작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.");
					} 
					else 
					{
						commUtils.printLog(logId, "작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.", "SL");
						return null;
					}
				}
			} 
			else 
			{
				if ("".equals(ydGp)) 
				{
					if (!"".equals(ydSchCd)) 
					{
						ydGp = ydSchCd.substring(0, 1);
					} 
					else if (!"".equals(ydEqpId)) 
					{
						ydGp = ydEqpId.substring(0, 1);
					}
				}

				jrParam.setField("YD_GP", ydGp); //야드구분
			}


			commUtils.printLog(logId, "[작업예약ID:" + ydWbookId + ", 스케쥴코드:" + ydSchCd + ", 설비ID:" + ydEqpId + "]", "SL");

			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
			
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name			
			// 크레인스케줄 기동
			if("0".equals(ydGp)) 
			{
				jrYdMsg.setField("JMS_TC_CD", "YFYFJ202"); //slab
			} 
			else 
			{
				jrYdMsg.setField("JMS_TC_CD", "YFYFJ302"); //coil
			}
			
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate  ); //JMSTC생성일시
			jrYdMsg.setField("YD_WBOOK_ID"       , ydWbookId ); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"         , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"      , ydSchStGp ); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP"     , ydSchReqGp); //야드스케쥴요청구분

			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 진도코드 get
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCoilCurrProgCd(JDTORecord rcvMsg) throws DAOException 
	{	
		String		methodNm	= "진도코드Check[YfComm.getCoilCurrProgCd] < " + rcvMsg.getResultMsg();
		String		logId 		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create(); //결과

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");
			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");
			String pidevProc = "Y";
			if("Y".equals(pidevProc)) {
				jrRtn = this.getCoilCurrProgCd_PIDEV(rcvMsg);
				return jrRtn;
			}
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 	= commUtils.trim(rcvMsg.getFieldString("TC_CD"));		//TC_CD
			String StlNo 	= commUtils.trim(rcvMsg.getFieldString("STL_NO"));		//재료
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			
			if("".equals(modifier)) 
			{ 
				modifier = msgId; 
			}

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("COIL_NO"	, StlNo);		//충당재료
			jrParam.setField("MODIFIER" , modifier);	//수정자

			JDTORecordSet jsStl = commDao.select(jrParam, getCoilComByCurrProgCd, logId, methodNm, "CoilComm 조회");
			
			String ydStocMv = "";
			
			if (jsStl != null && jsStl.size() > 0) 
			{
				String CurrProgCd 	= commUtils.trim(jsStl.getRecord(0).getFieldString("CURR_PROG_CD"));//진도코드
				String ReturnGp 	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분
			   	
		    	if(YfConstant.DMYDR008.equals(TcCode))		//코일제품반납대기
		    	{			
		    		if(YfConstant.RETURN_GP_1.equals(ReturnGp))
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}
		    		else
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    	}
		    	else if
		    	(
		    		YfConstant.DMYDR005.equals(TcCode)||	//코일제품출하지시대기 
		    		YfConstant.DMYDR004.equals(TcCode)|| 	//외판슬라브출하지시대기
		    		YfConstant.DMYDR033.equals(TcCode)		//코일제품반품
		    	)
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_KG;
		    	}
		    	else if
		    	(
		    		YfConstant.DMYDR027.equals(TcCode)||	//코일제품보관지시 
		    		YfConstant.DMYDR030.equals(TcCode)		//코일제품출하완료
		    	)
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_MG;
		    	}
		    	else if(YfConstant.DMYDR016.equals(TcCode))	//외판슬라브운송지시대기
		    	{			
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_NG;
		    	}
		    	else if
		    	(
		    		YfConstant.DMYDR060.equals(TcCode)||	//코일제품운송지시
		    		YfConstant.DMYDR022.equals(TcCode)		//외판슬라브운송상차지시
		    	)
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_LG;
		    	}
		    	else if
		    	(
		    		YfConstant.CURR_PROG_CD_COIL_A.equals(CurrProgCd)||
		    		YfConstant.CURR_PROG_CD_COIL_R.equals(CurrProgCd)
		    	)
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_AC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_B.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_BC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_C.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_CC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_D.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_DC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_E.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_CS;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_F.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_FC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_K.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_KG;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_G.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_GC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_H.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_HG;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_J.equals(CurrProgCd))
		    	{
		    		if(YfConstant.RETURN_GP_1.equals(ReturnGp))
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}
		    		else
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_L.equals(CurrProgCd))	//코일제품상차지시
		    	{ 
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_LG;
		    		
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_N.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_NG;	
		    	}
		    	else if
		    	(
		    		YfConstant.CURR_PROG_CD_COIL_M.equals(CurrProgCd)||
		    		YfConstant.CURR_PROG_CD_COIL_P.equals(CurrProgCd)
		    	)
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_MG;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_X.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_XG;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_Y.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_YG;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_Z.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_ZG;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);							//Log ID
		    	jrRtn.setResultMsg(methodNm);						//Log Method Name
		    	//jrRtn.setField("STL_NO",			StlNo); 		//충당재료
		    	//jrRtn.setField("MODIFIER",			modifier); 		//수정자
		    	jrRtn.setField("CURR_PROG_CD",		CurrProgCd);	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM",	ydStocMv  );	//저장품 이동 조건
			} 

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e) 
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		}
		catch (Exception e) 
		{
			return jrRtn;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 진도코드 get
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCoilCurrProgCd2(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "진도코드Check2[YfComm.getCoilCurrProgCd2] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			
			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg);								//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String StlNo 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"));			//재료
			String CurrProgCd	= commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"));	//진도코드
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));		//수정자(Backup Only)
			
			if("".equals(modifier)) 
			{ 
				modifier = msgId; 
			}

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("COIL_NO"	, StlNo); //충당재료
			jrParam.setField("MODIFIER" , modifier); //수정자
			JDTORecordSet jsStl = commDao.select(jrParam, getCoilComByCurrProgCd, logId, methodNm, "CoilComm 조회");

			String ydStocMv = "";

			if (jsStl != null && jsStl.size() > 0) 
			{
				String ReturnGp	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분

		    	if(YfConstant.CURR_PROG_CD_COIL_B.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_BC;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_A.equals(CurrProgCd)|| YfConstant.CURR_PROG_CD_COIL_R.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_AC;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_C.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_CC;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_D.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_DC;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_E.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_CS;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_F.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_FC;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_K.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_KG;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_G.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_GC;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_H.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_HG;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_J.equals(CurrProgCd))
		    	{
		    		if(YfConstant.RETURN_GP_1.equals(ReturnGp))
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}
		    		else
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_L.equals(CurrProgCd))	//코일제품상차지시
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_LG;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_M.equals(CurrProgCd)|| YfConstant.CURR_PROG_CD_COIL_P.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_MG;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_N.equals(CurrProgCd))
		    	{
	    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_NG;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_X.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_XG;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_Y.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_YG;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_Z.equals(CurrProgCd))
		    	{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_ZG;
		    	}

		    	jrRtn.setResultCode(logId);						//Log ID
		    	jrRtn.setResultMsg(methodNm);					//Log Method Name
//		    	jrRtn.setField("STL_NO"			, StlNo);		//충당재료
//		    	jrRtn.setField("MODIFIER" 		, modifier); 	//수정자
		    	jrRtn.setField("CURR_PROG_CD"  	, CurrProgCd); 	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM", ydStocMv  );	//저장품 이동 조건
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e) 
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		}
		catch (Exception e) 
		{
			return jrRtn;
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : INSERT,UPDATE Transaction 분리메소드 호출 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
     */			
	public boolean execQueryId(JDTORecord rcvMsg,String queryId) throws DAOException 
	{
		String methodNm = "INSERT,UPDATE Transaction 분리메소드 호출[YfComm.execQueryId] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn1 = new EJBConnector("default", "YfCommSeEJB", this);
			ejbConn1.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId });
			
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
		return true;
	}
	
	/**
	 * [A] 오퍼레이션명 : 스케줄코드 Check
	 *
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord chkSchCd(JDTORecord rcvMsg) 
	{
		String methodNm = "스케줄코드Check[YfComm.chkSchCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "SC99"); //야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:스케줄코드Check 예상치 못한 오류"); //야드L3MESSAGE(40Byte)
			
			//수신 항목 값
			String ydSchCd = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //야드스케쥴코드
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydSchCd)) 
			{
				ydL3HdRsCd = "SC01";
				ydL3Msg = "오류:스케줄코드 없음";
			}
			else if (ydSchCd.length() < 8) 
			{
				ydL3HdRsCd = "SC02";
				ydL3Msg = "오류:스케줄코드[" + ydSchCd + "] 이상";
			}

			if (!"".equals(ydL3Msg)) 
			{
				jrRtn.setField("YD_L3_HD_RS_CD",	ydL3HdRsCd);	//야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG",			ydL3Msg);		//야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 크레인스케줄 상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");

			jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.select(jrParam, getStatSchCd, logId, methodNm, "야드스케쥴금지유무 조회"); 

			String ydSchProhExn = "";  //야드스케쥴금지유무

			if (jsChk.size() > 0) 
			{
				ydSchProhExn  = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
			}

			if ("".equals(ydSchProhExn))
			{
				//스케줄기준 Table 정보 Check
				ydL3HdRsCd = "SC03";
				ydL3Msg = "오류:스케쥴코드[" + ydSchCd + "] 정보 없음";
			}
			else if ("Y".equals(ydSchProhExn))
			{
				//스케줄 금지여부 Check
				ydL3HdRsCd = "SC04";
				ydL3Msg = "오류:스케쥴코드[" + ydSchCd + "] 기동금지";
			}
			
			if (!"".equals(ydL3Msg)) 
			{
				jrRtn.setField("YD_L3_HD_RS_CD",	ydL3HdRsCd);	//야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG",			ydL3Msg);		//야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD",	"0000");	//야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG",			"");		//야드L3MESSAGE

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e) 
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		}
		catch (Exception e)
		{
			return jrRtn;
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 설비상태 Check
	 *
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord chkEqpStat(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "설비상태Check[YfComm.chkEqpStat] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD",	"EQ99");	//야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG",			"오류:설비상태Check 예상치 못한 오류");	//야드L3MESSAGE(40Byte)

			//수신 항목 값
			String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));	//야드설비ID
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId))
			{
				ydL3HdRsCd = "EQ01";
				ydL3Msg = "오류:설비ID 없음";
			}
			else if (ydEqpId.length() < 6)
			{
				ydL3HdRsCd = "EQ02";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 이상";
			}

			if (!"".equals(ydL3Msg))
			{
				jrRtn.setField("YD_L3_HD_RS_CD",	ydL3HdRsCd);	//야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG",			ydL3Msg);		//야드L3MESSAGE
				
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");

			jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID	   
			JDTORecordSet jsChk = commDao.select(jrParam, getStatEqp, logId, methodNm, "설비상태 Check"); 

			String ydEqpStat     = ""; //야드설비상태
			String ydEqpWrkMode  = ""; //야드설비작업Mode

			if(jsChk.size() > 0)
			{
				ydEqpStat    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_PROG_STAT"));
				ydEqpWrkMode = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			}

			if ("".equals(ydEqpStat)) 
			{
				//설비 Table 정보 Check
				ydL3HdRsCd = "EQ03";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] 정보 없음";
			}
			else if ("B".equals(ydEqpStat))
			{
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "EQ04";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] 고장";
			}
			else if (!"1".equals(ydEqpWrkMode))
			{
				//설비 Table 설비작업Mode Check
				ydL3HdRsCd = "EQ05";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] Off-Line";
			}
			
			if(!"".equals(ydL3Msg))
			{
				jrRtn.setField("YD_L3_HD_RS_CD",	ydL3HdRsCd);	//야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG",			ydL3Msg);		//야드L3MESSAGE
				
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, new Exception(ydL3Msg)));
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD",	"0000");	//야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG",			"");		//야드L3MESSAGE

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
	 *      [A] 오퍼레이션명 : 크레인작업실적응답(YFF1L005) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYFF1L005(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "크레인작업실적응답 조회[YfComm.getYFF1L005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try 
		{
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) 
			{
				return null;
			}

			if (ydEqpId.startsWith("1")) 
			{
				msgId = "YFF1L005";
			}
			else 
			{
				return null;
			}

			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			if ("".equals(ydL3Msg)) 
			{
				if ("U".equals(ydL2WrGp)) 
				{
					ydL3Msg = "권상실적";
				}
				else if ("D".equals(ydL2WrGp)) 
				{
					ydL3Msg = "권하실적";
				}
				else if ("E".equals(ydL2WrGp)) 
				{
					ydL3Msg = "비상조업실적";
				}
				else if ("R".equals(ydL2WrGp)) 
				{
					ydL3Msg = "고장복구실적";
				}
				else if ("M".equals(ydL2WrGp)) 
				{
					ydL3Msg = "운전모드전환";
				}
				else if ("J".equals(ydL2WrGp))
				{
					ydL3Msg = "지시요구";
				}
				else if ("F".equals(ydL2WrGp)) 
				{
					ydL3Msg = "강제권하";
				}
				else if ("G".equals(ydL2WrGp)) 
				{
					ydL3Msg = "강제권상요구";
				}
				else 
				{
					ydL3Msg = ydL2WrGp;
				}

				if ("0000".equals(ydL3HdRsCd)) 
				{
					ydL3Msg = ydL3Msg + " 정상 처리";
				} 
				else if ("9999".equals(ydL3HdRsCd)) 
				{
					ydL3Msg = ydL3Msg + " 정보 없음";
				} 
				else 
				{
					ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
				}
			}

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                 ); //전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()             ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                   ); //전문구분
			sbMsg = sbMsg.append("0078"                                ); //전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 29, " ")); //임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydEqpId   ,  6, " ")); //야드설비ID
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")),  1, " ")); //야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )),  8, " ")); //야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )), 18, " ")); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(ydL2WrGp  ,  1, " ")); //야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")); //야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")); //야드L3Message

			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

			
			//L2송신인데 왜 jms?? kbs
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndL2Msg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndL2Msg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndL2Msg);
		}
		catch (Exception e) 
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	
    
	/**
	 *      [A] 오퍼레이션명 : ZONE 정보 (YFF1L021) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYFF1L021(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "ZONE 정보 [ACoilRcvL2SeEJB.getYFF1L021] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			StringBuffer sbMsg = new StringBuffer();
			String msgId = "YFF1L021";
			
			sbMsg.append(YfCommUtils.FillToString(msgId													,8));
			sbMsg.append(YfCommUtils.FillToString(commUtils.getDateTime18()								,18));
			sbMsg.append(YfCommUtils.FillToString("I"													,1));
//			sbMsg.append(YfCommUtils.FillToString("0040"												,4));	
			sbMsg.append(YfCommUtils.FillToString("0026"												,4));	
			sbMsg.append(YfCommUtils.FillToString(""													,29));
			sbMsg.append(YfCommUtils.FillToString(commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_GP")),6));
			sbMsg.append(YfCommUtils.FillToString(commUtils.trim(rcvMsg.getFieldString("YD_ZONE_GP"))	,1));	
			sbMsg.append(YfCommUtils.FillToString(commUtils.trim(rcvMsg.getFieldString("YD_ZONE_COLOR")),10));
//			sbMsg.append(YfCommUtils.FillToString(commUtils.getDateTime14()								,14));
			sbMsg.append(YfCommUtils.FillToString(""													,9));	

			JDTORecord jrSnd = JDTORecordFactory.getInstance().create();

			jrSnd.setResultCode(logId);	//Log ID
			jrSnd.setResultMsg(methodNm);	//Log Method Name
			jrSnd.setField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			jrSnd.setField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			jrSnd.setField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(jrSnd);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 :  고도화 적용 SCH CD
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean chkRule(String ydSchCd) throws DAOException 
	{
		String methodNm = "RULE CHECK [YfComm.chkRule]" ;
		String logId = "";

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 설비정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", methodNm, "");
			jrParam.setField("YD_SCH_CD", ydSchCd);
			JDTORecordSet jsChk = commDao.select(jrParam, ChkRule, logId, methodNm, "대상 SCH 여부"); 

			if (jsChk.size() > 0) 
			{
				commUtils.printLog(logId, methodNm, "S-");
				return true;
			} 
			else 
			{
				commUtils.printLog(logId, methodNm, "S-");
				return false;
			}
		}
		catch (DAOException e) 
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return false;
		}
		catch (Exception e) 
		{
			return false;
		}
	}	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 일반작업예약 생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String procWkBookInsert(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "작업예약생성[YmComm.procWkBookInsert] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String ydSchPrior = "";
		
		try 
		{
			
			//수신 항목 값
			String ydStackColGp	= commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_GP"));	//적재위치
			String ydStackBedGp	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"));	//적재위치
			String ydStackLayer	= commUtils.trim(rcvMsg.getFieldString("YD_STK_LYR_NO"));	//적재위치
			String ydSchCd		= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));		//야드스케쥴코드
			String toLocGuide	= commUtils.trim(rcvMsg.getFieldString("YD_TO_LOC_GUIDE"));	//TO위치 가이드
			String ydAimBayGp	= commUtils.trim(rcvMsg.getFieldString("YD_AIM_BAY_GP"));	//TO위치 가이드
			
			String modifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			String sYD_EQP_ID	= StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")), "");  //설비
			
			String sStl_no		= commUtils.trim(rcvMsg.getFieldString("STL_NO"));		//저장품
			
	    	// PIDEV
//	    	String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");			
			
			JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
			recInTemp1.setField("YD_SCH_CD", ydSchCd);
	    	
			JDTORecordSet jsResult = commDao.select(recInTemp1, getYdSchrule, logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (jsResult != null && jsResult.size() > 0) 
			{
				ydSchPrior = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			} 
			else 
			{
				return YfConstant.RETN_CD_FAILURE;
			}
			
			if ("".equals(sYD_EQP_ID)) 
			{
				sYD_EQP_ID = jsResult.getRecord(0).getFieldString("YD_WRK_CRN");
			}
			
			//작업예약ID 조회
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			if ("".equals(ydWbookId)) 
			{
				return YfConstant.RETN_CD_FAILURE;
			}
			
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID",		ydWbookId);		//야드작업예약ID
			recInTemp.setField("MODIFIER",			modifier);		//수정자
			recInTemp.setField("YD_GP",				ydStackColGp.substring(0,1));	//야드구분
			recInTemp.setField("YD_BAY_GP",			ydStackColGp.substring(1,2));	//야드동구분
			recInTemp.setField("YD_SCH_CD",			ydSchCd);		//야드스케쥴코드
			recInTemp.setField("YD_SCH_PRIOR",		ydSchPrior);	//야드스케쥴우선순위
			recInTemp.setField("YD_SCH_PROG_STAT",	"W");			//야드스케쥴진행상태(스케줄수행대기)
			recInTemp.setField("YD_SCH_ST_GP",		"O");			//야드스케쥴기동구분(Manual)
			recInTemp.setField("YD_SCH_REQ_GP",		"M");			//야드스케쥴요청구분(이적)
			recInTemp.setField("YD_TO_LOC_GUIDE",	toLocGuide);	//TO위치가이드
			recInTemp.setField("YD_WRK_PLAN_CRN",	sYD_EQP_ID);	//작업예약 크레인
			recInTemp.setField("YD_AIM_BAY_GP",	ydAimBayGp);	//TO위치가이드
			
	    	// PIDEV				
			int ins_cnt = 0;
//			if("Y".equals(sApplyYnPI)) {
				ins_cnt = commDao.insert(recInTemp, insWrkBook2_PIDEV, logId, methodNm, "TB_YF_WRKBOOK");				
//			} else {
//				ins_cnt = commDao.insert(recInTemp, insWrkBook2, logId, methodNm, "TB_YF_WRKBOOK");				
//			}
			
			if (ins_cnt <= 0) 
			{
				//throw new JDTOException("작업예약 등록실패");
				return YfConstant.RETN_CD_FAILURE;

			}
			        
			recInTemp = JDTORecordFactory.getInstance().create();
			//작업예약 등록
			recInTemp.setField("YD_WBOOK_ID",	ydWbookId);		//야드작업예약ID
			recInTemp.setField("MODIFIER",		modifier);		//수정자
			recInTemp.setField("YD_STK_COL_GP",	ydStackColGp);
			recInTemp.setField("YD_STK_BED_NO",	ydStackBedGp); 
			recInTemp.setField("YD_STK_LYR_NO",	ydStackLayer);
			recInTemp.setField("STL_NO",		sStl_no);
			
			ins_cnt = commDao.insert(recInTemp, insWrkBookMtlByStkLyr, logId, methodNm, "TB_YF_WRKBOOKMTL");
			
			if (ins_cnt <= 0) 
			{
				//throw new JDTOException("작업예약 재료 등록실패");
				return YfConstant.RETN_CD_FAILURE;
			}
			
			return ydWbookId;
			
		} 
		catch (Exception e) 
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return YfConstant.RETN_CD_FAILURE;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(procCarPlanInfo)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCarPlanInfo(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "차량작업예정정보요구[YfComm.procCarPlanInfo] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();	//전문 Return
		
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
			
			commUtils.printLog(logId, "=============차량작업예정정보요구 시작========", "SL");
			
			String SearchFlag	= commUtils.trim(rcvMsg.getFieldString("SEARCH_FLAG"));   	//1:상차도, 2:차량스케쥴 ID
			String ydLoadLoc   	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));    	//상차도 위치
			String ydCarSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));  	//차량스케쥴 ID

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (SearchFlag.length() < 0) 
			{
				commUtils.printLog(logId, methodNm + " 검색조건 없음 [" + SearchFlag + "]" , "SL");
			}
			else if ("1".equals(SearchFlag)) 
			{
				if (ydLoadLoc.length() < 6) 
				{
					commUtils.printLog(logId, methodNm + " 상차도 위치 Error [" + ydLoadLoc + "]" , "SL");
				}
			} 
			else if ("2".equals(SearchFlag)) 
			{
				if ("".equals(ydCarSchId)) 
				{
					commUtils.printLog(logId, methodNm + " 차량스케쥴 ID Error [" + ydCarSchId + "]" , "SL");
				}
			}
			
			JDTORecordSet jrCarInfo = JDTORecordFactory.getInstance().createRecordSet("Temp");
			/**********************************************************
			* 2. 차량예정정보 조회
			**********************************************************/			
			if("1".equals(SearchFlag)) 
			{
				//상차위치로 차량예정정보 조회
				jrParam.setField("YD_CARUD_STOP_LOC",	ydLoadLoc);
				jrCarInfo = commDao.select(jrParam, getYdCarschCarGetInWorkByCarNo, logId, methodNm, "상차위치로 차량예정정보 조회");
			}
			else 
			{
				//차량스케줄ID로 차량예정정보 조회
				jrParam.setField("YD_CAR_SCH_ID",		ydCarSchId);
				jrCarInfo = commDao.select(jrParam, getYdCarschCarGetInWorkInfo, logId, methodNm, "차량스케줄ID로 차량예정정보 조회"); 
			}

			/**********************************************************
			* 2. 차량예정정보 송신
			**********************************************************/
			if(jrCarInfo.size() > 0)
			{	
				jrCarInfo.first();
				
				JDTORecord	jsCarInfo	= JDTORecordFactory.getInstance().create();
				jsCarInfo.setRecord(jrCarInfo.getRecord());
				
				//차량작업 예정정보 전문 data setup
			    jrParam.setField("PT_LOAD_LOC",			commUtils.trim(jsCarInfo.getFieldString("YD_PT_LOAD_LOC")));		// 상차도 위치				
			    jrParam.setField("CAR_NO",				commUtils.trim(jsCarInfo.getFieldString("YD_CAR_NO")));				// 차량번호	
			    jrParam.setField("CARD_NO",				commUtils.trim(jsCarInfo.getFieldString("YD_CARD_NO")));			// 차량번호	
			    jrParam.setField("PT_CLS",				commUtils.trim(jsCarInfo.getFieldString("YD_PT_CLS")));				// 차량구분				
			    jrParam.setField("WORK_CLS",			commUtils.trim(jsCarInfo.getFieldString("YD_WORK_CLS")));			// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT",	commUtils.trim(jsCarInfo.getFieldString("YD_WORK_COIL_MAX_CNT")));	// 작업총 수량
			    jrParam.setField("COIL_GP",				commUtils.trim(jsCarInfo.getFieldString("COIL_GP")));				// COIL구분(HR열연/CR냉연)
			    
			    for (int ii = 0; ii < jrCarInfo.size(); ii++)
			    {		    	
			    	jrParam.setField("STL_NO_"+ii,		commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_STL_NO"))); 
			    	jrParam.setField("LOAD_LOC_CD_"+ii,	commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_LOAD_LOC_CD")));
			    	jrParam.setField("WORK_STATE_"+ii,	commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_WORK_STATE")));
				}
	
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L008BackUp", jrParam));
			}
			else
			{
				//빈 전문 생성
			    jrParam.setField("PT_LOAD_LOC",			ydLoadLoc);	// 상차도 위치				
			    jrParam.setField("CAR_NO",				"");		// 차량번호	
			    jrParam.setField("CARD_NO",				"");		// 차량번호	
			    jrParam.setField("PT_CLS",				"");		// 차량구분				
			    jrParam.setField("WORK_CLS",			"");		// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT",	"0");		// 작업총 수량
			    jrParam.setField("COIL_GP",				"");		// COIL구분(HR열연/CR냉연)
			    
		    	jrParam.setField("STL_NO_0",			""); 
		    	jrParam.setField("LOAD_LOC_CD_0",		"");
		    	jrParam.setField("WORK_STATE_0",		"");
				
		    	jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L008BackUp", jrParam));
			}
			
			commUtils.printLog(logId, "=============차량작업예정정보요구 종료========", "SL");
			
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
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(procCarPlanInfo)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCarPlanInfo_Slab(JDTORecord rcvMsg) throws DAOException 
	{
		String		methodNm	= "차량작업예정정보요구[YfComm.procCarPlanInfo_Slab] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();	//전문 Return
		
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
			
			commUtils.printLog(logId, "=============차량작업예정정보요구(Slab) 시작========", "SL");
			
			String SearchFlag	= commUtils.trim(rcvMsg.getFieldString("SEARCH_FLAG"));   	//1:상차도, 2:차량스케쥴 ID
			String ydLoadLoc   	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));    	//상차도 위치
			String ydCarSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));  	//차량스케쥴 ID
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (SearchFlag.length() < 0) 
			{
				commUtils.printLog(logId, methodNm + " 검색조건 없음 [" + SearchFlag + "]" , "SL");
			}
			else if ("1".equals(SearchFlag)) 
			{
				if (ydLoadLoc.length() < 6)
				{
					commUtils.printLog(logId, methodNm + " 상차도 위치 Error [" + ydLoadLoc + "]" , "SL");
				}
			}
			else if ("2".equals(SearchFlag))
			{
				if ("".equals(ydCarSchId))
				{
					commUtils.printLog(logId, methodNm + " 차량스케쥴 ID Error [" + ydCarSchId + "]" , "SL");
				}
			}
			
			JDTORecordSet jrCarInfo = JDTORecordFactory.getInstance().createRecordSet("Temp");			
			/**********************************************************
			* 2. 차량예정정보 조회
			**********************************************************/			
			if("1".equals(SearchFlag)) 
			{
				//상차위치로 차량예정정보 조회
				jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);
				jrCarInfo = commDao.select(jrParam, getYdCarschCarGetInWorkByCarNoSlab, logId, methodNm, "상차위치로 차량예정정보 조회");
			}
			else if("2".equals(SearchFlag)) 
			{
				//차량스케줄ID로 차량예정정보 조회
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
				jrCarInfo = commDao.select(jrParam, getYdCarschCarGetInWorkInfoSlab, logId, methodNm, "차량스케줄ID로 차량예정정보 조회"); 
			}
			else if("3".equals(SearchFlag)) 
			{
				jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);
				jrParam.setField("YD_WBOOK_ID"		, commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")));
				jrCarInfo = commDao.select(jrParam, getYdCarschCarGetInWorkByWbookId, logId, methodNm, "상차 차량예정정보 조회");
			}
			else if("4".equals(SearchFlag)) 
			{
				jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);
				jrCarInfo = commDao.select(jrParam, getYdCarschCarGetInWork, logId, methodNm, "상차 차량예정정보 조회");
			}
			else if("5".equals(SearchFlag)) 
			{
				jrParam.setField("LOC", ydLoadLoc);
				jrCarInfo = commDao.select(jrParam, getYdCarschCarGetInWorkByTcLoc, logId, methodNm, "대차 차량예정정보 조회");
			}

			/**********************************************************
			* 2. 차량예정정보 송신
			**********************************************************/
			if(jrCarInfo.size() > 0) 
			{	
				jrCarInfo.first();
				
				JDTORecord jsCarInfo = JDTORecordFactory.getInstance().create();
				jsCarInfo.setRecord(jrCarInfo.getRecord());
				
				//차량작업 예정정보 전문 data setup
			    jrParam.setField("PT_LOAD_LOC",			commUtils.trim(jsCarInfo.getFieldString("YD_PT_LOAD_LOC")));   		// 상차도 위치				
			    jrParam.setField("CAR_NO",				commUtils.trim(jsCarInfo.getFieldString("YD_CAR_NO")));  			// 차량번호	
			    jrParam.setField("CARD_NO",				commUtils.trim(jsCarInfo.getFieldString("YD_CARD_NO"))); 			// 차량번호	
			    jrParam.setField("PT_CLS",				commUtils.trim(jsCarInfo.getFieldString("YD_PT_CLS")));   			// 차량구분				
			    jrParam.setField("WORK_CLS",			commUtils.trim(jsCarInfo.getFieldString("YD_WORK_CLS")));			// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT",	commUtils.trim(jsCarInfo.getFieldString("YD_WORK_COIL_MAX_CNT")));	// 작업총 수량 				
	
			    for (int ii = 0; ii < jrCarInfo.size(); ii++) 
			    {
			    	jrParam.setField("STL_NO_"+ii,		commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_STL_NO"))); 
			    	jrParam.setField("LOAD_LOC_CD_"+ii,	commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_LOAD_LOC_CD")));
			    	jrParam.setField("WORK_STATE_"+ii,	commUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_WORK_STATE")));	
				}
	
				//차량예정정보 백업 송신
			    jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF0L008BackUp", jrParam));
			}
			else 
			{
				//빈 전문 생성
			    jrParam.setField("PT_LOAD_LOC",			ydLoadLoc);	// 상차도 위치				
			    jrParam.setField("CAR_NO",				"");  		// 차량번호	
			    jrParam.setField("CARD_NO",				""); 		// 차량번호	
			    jrParam.setField("PT_CLS",				"");   		// 차량구분				
			    jrParam.setField("WORK_CLS",			"");		// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT",	"0");		// 작업총 수량
		    	jrParam.setField("STL_NO_0",			""); 
		    	jrParam.setField("LOAD_LOC_CD_0",		"");
		    	jrParam.setField("WORK_STATE_0",		"");
				
		    	jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF0L008BackUp", jrParam));
			}
			
			commUtils.printLog(logId, "=============차량작업예정정보요구(Slab) 종료========", "SL");
			
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
	 * [A] 오퍼레이션명 : 진도코드 get(slab)
	 *
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord getSlabCurrProgCd(JDTORecord rcvMsg) throws DAOException
	{	
		String methodNm = "진도코드Check[YfComm.getSlabCurrProgCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 	 = commUtils.trim(rcvMsg.getFieldString("TC_CD"));	//TC_CD
			String Stl_no = commUtils.trim(rcvMsg.getFieldString("STL_NO"));//재료
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("SLAB_NO"	, Stl_no); 
			jrParam.setField("MODIFIER" , modifier); //수정자


			JDTORecordSet rsResult = commDao.select(jrParam, selectSlabMatirialInfo2, logId, methodNm, "SlabComm 조회");
			
			String sSTOCK_MOVE_TERM = "";
			
			if (rsResult != null && rsResult.size() > 0) 
			{
				String sCURR_PROG_CD     = commUtils.trim(rsResult.getRecord(0).getFieldString("CURR_PROG_CD"));
				String sWO_MSLAB_RPR_MTD = commUtils.trim(rsResult.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD"));
			   	
		    	/* 일관제철 진도코드 */
		    	if(YfConstant.DMYDR016.equals(TcCode))
		    	{
		    		//외판슬라브운송지시대기
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_NS;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD))
		    	{    		
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_11; 
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_12;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD))
		    	{
		    		if("Q".equals(sWO_MSLAB_RPR_MTD))
		    		{
		        		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
		    		}
		    		else
		    		{
		    			sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_DS;
		    		}
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ES;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_FS;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_BS;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_YS;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_KS;
				}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD))
		    	{
			    	sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_LS;
				}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_MS;    		
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_NS;    	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;    	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD))
		    	{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);	//Log ID
		    	jrRtn.setResultMsg(methodNm);	//Log Method Name
		    	jrRtn.setField("CURR_PROG_CD"  	, sCURR_PROG_CD); 	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM  );	//저장품 이동 조건
			} 

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		}
		catch (Exception e) 
		{
			return jrRtn;
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 저장품 이동 조건 (현재진도코드와 Scarfing Pattern 으로 이동조건 판단) 
	 *
	 * @param  sCURR_PROG_CD : 현재진도코드
	 * @param  sWO_MSLAB_RPR_MTD : Scarfing Pattern
	 * @return String
	 * @throws DAOException
	 */
	public String getStockMoveTerm(String sCURR_PROG_CD, String sWO_MSLAB_RPR_MTD) throws DAOException 
	{	
		String sSTOCK_MOVE_TERM = ""; //결과 

		try 
		{
			if(YfConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD))
			{    		
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_11; 
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_12;	
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD))
			{
				if("Q".equals(sWO_MSLAB_RPR_MTD))
				{
		    		sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_D3;	
				}
				else
				{
					sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_DS;
				}
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ES;
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_FS;
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_BS;
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_YS;
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_GS; 
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_HS; 
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_JS; 
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_KS;
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_LS;
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_MS;    		
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_NS;    	
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;    	
			}
			else if(YfConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD))
			{
				sSTOCK_MOVE_TERM   = YfConstant.NEW_STOCK_MOVE_TERM_ZS;
			}															
			
			return sSTOCK_MOVE_TERM;
		}
		catch (DAOException e) 
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sSTOCK_MOVE_TERM;
		}
		catch (Exception e)
		{
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sSTOCK_MOVE_TERM;
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 :  자동화 크레인 CHECK 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean chkAutoCrn(String szYD_EQP_ID) throws DAOException {
		String methodNm = "자동화 크레인 CHECK [YfComm.chkAutoCrn]" ;
		String logId = "";
		String szYD_EQP_ID_GET = "";

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 설비정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", methodNm, "");
			jrParam.setField("YD_EQP_ID", szYD_EQP_ID); //공장구분 2,3

			JDTORecordSet jsChk = commDao.select(jrParam, ChkCrnMode2, logId, methodNm, "설비정보 조회"); 

			if (jsChk.size() > 0) {
				szYD_EQP_ID_GET    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE2"));
			}
			//if ("A".equals(szYD_EQP_ID_GET) ||"R".equals(szYD_EQP_ID_GET)){
			if ("A".equals(szYD_EQP_ID_GET)){ //리모컨은 유인
				commUtils.printLog(logId, methodNm, "S-");
				return true;
			} else {
				commUtils.printLog(logId, methodNm, "S-");
				return false;
			}
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 작업예약 생성-차량
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public String procCarWkBookInsert(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "작업예약 생성-차량[YfComm.procCarWkBookInsert] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String ydSchPrior = "";
		
		try
		{
			//수신 항목 값
			String ydStackColGp = commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_GP"));   //적재위치
			String ydCarSchId   = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));  //차량스케줄
			String ydSchCd 		= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));      //야드스케쥴코드
			String ydCarNo 		= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));         //차량번호
			String YdCardNo 	= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));        //차량번호
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));       //야드L3MESSAGE

			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_SCH_CD",		ydSchCd);
			jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);
	    	JDTORecordSet jsResult = commDao.select(jrParam, getYdSchrule, logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (jsResult != null && jsResult.size() > 0)
			{
				ydSchPrior = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			}
			else
			{
				return YfConstant.RETN_CD_FAILURE;
			}

			jrParam.setField("YD_GP",			ydStackColGp.substring(0, 1));
			jrParam.setField("YD_BAY_GP",		ydStackColGp.substring(1, 2));
			jrParam.setField("YD_STK_COL_GP",	ydStackColGp);
			jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);
			JDTORecordSet jsStock = commDao.select(jrParam, getYdStockTransOrdDT70Wbook, logId, methodNm, "스케줄 기준 조회");			
			
			if ( jsStock.size() < 1)
			{
				return YfConstant.RETN_CD_FAILURE;
			}
			
			String first_wbook_ID = "";
			
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();
	    	for(int Loop_i = 0; Loop_i < jsStock.size(); Loop_i++)
	    	{	
				//작업예약 등록
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				
				if("".equals(first_wbook_ID)) 
				{
					first_wbook_ID = ydWbookId; //첫번째 작업예약 ID 
				}				
				
				recInTemp.setField("YD_WBOOK_ID",		ydWbookId); //야드작업예약ID
				recInTemp.setField("MODIFIER",			modifier); //수정자
				recInTemp.setField("YD_GP",				ydStackColGp.substring(0,1)); //야드구분
				recInTemp.setField("YD_BAY_GP",			ydStackColGp.substring(1,2)); //야드동구분
				recInTemp.setField("YD_SCH_CD",			ydSchCd); //야드스케쥴코드
				recInTemp.setField("YD_SCH_PRIOR",		ydSchPrior); //야드스케쥴우선순위
				recInTemp.setField("YD_SCH_PROG_STAT",	"W"); //야드스케쥴진행상태(스케줄수행대기)
				recInTemp.setField("YD_SCH_ST_GP",		"O"); //야드스케쥴기동구분(Manual)
				recInTemp.setField("YD_SCH_REQ_GP",		"M"); //야드스케쥴요청구분(이적)
				recInTemp.setField("YD_CAR_USE_GP",		"G");
				recInTemp.setField("CAR_NO",			ydCarNo);
				
		    	//PIDEV				
//				if("N".equals(sApplyYnPI)) {				
//					recInTemp.setField("CARD_NO",			YdCardNo);
//				}
				
				recInTemp.setField("YD_AIM_YD_GP",		ydStackColGp.substring(0,1)); //야드구분;
				recInTemp.setField("YD_AIM_BAY_GP",		ydStackColGp.substring(1,2)); //야드동구분;
				
		    	//PIDEV				
//				if("Y".equals(sApplyYnPI)) {
					commDao.insert(recInTemp, insWrkBook2_PIDEV, logId, methodNm, "TB_YF_WRKBOOK");
//				} else {
//					commDao.insert(recInTemp, insWrkBook2, logId, methodNm, "TB_YF_WRKBOOK");
//				}
				
	    		//작업예약재료 등록
	    		jrInTemp.setField("YD_WBOOK_ID",		ydWbookId);
	    		jrInTemp.setField("MODIFIER",			modifier);
	    		jrInTemp.setField("YD_STK_COL_GP",		ydStackColGp);
	    		jrInTemp.setField("YD_STK_BED_NO",		commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("YD_STK_BED_NO")));
	    		jrInTemp.setField("YD_STK_LYR_NO",		commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("YD_STK_LYR_NO")));
	    		jrInTemp.setField("STL_NO",				commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("STL_NO")));
	    		jrInTemp.setField("YD_UP_COLL_SEQ",		"" + Loop_i);
	    		commDao.insert(jrInTemp, insWrkBookMtl, logId, methodNm, "TB_YF_WRKBOOKMTL");
	    	}		
	    	
			return first_wbook_ID;
		}
		catch (Exception e)
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return YfConstant.RETN_CD_FAILURE;
		}
	}

/***********************************
    PIDEV 개발
***********************************/	
	
	/**
	 *      [A] 오퍼레이션명 : 진도코드 get PI
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCoilCurrProgCd_PIDEV(JDTORecord rcvMsg) throws DAOException 
	{	
		String		methodNm	= "진도코드Check[YfComm.getCoilCurrProgCd_PIDEV] < " + rcvMsg.getResultMsg();
		String		logId 		= rcvMsg.getResultCode();
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create(); //결과

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String TcCode 	= commUtils.trim(rcvMsg.getFieldString("TC_CD"));		//TC_CD
			String StlNo 	= commUtils.trim(rcvMsg.getFieldString("STL_NO"));		//재료
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			String infoGp   = commUtils.trim(rcvMsg.getFieldString("INFO_GP"));	// 정보구분
			
			if("".equals(modifier)) 
			{ 
				modifier = msgId; 
			}

			/**********************************************************
			* 2. COILCOMM READ
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("COIL_NO"	, StlNo);		//충당재료
			jrParam.setField("MODIFIER" , modifier);	//수정자

			JDTORecordSet jsStl = commDao.select(jrParam, getCoilComByCurrProgCd, logId, methodNm, "CoilComm 조회");
			
			String ydStocMv = "";
			commUtils.printLog(logId, "TcCode:" + TcCode + "infoGp:" + infoGp , "SL");
			if (jsStl != null && jsStl.size() > 0) 
			{
				String CurrProgCd 	= commUtils.trim(jsStl.getRecord(0).getFieldString("CURR_PROG_CD"));//진도코드
				String ReturnGp 	= commUtils.trim(jsStl.getRecord(0).getFieldString("RETURN_GP")); 	//반납구분
			   	
		    	if("M10LMYDJ1021".equals(TcCode))		//코일제품반납대기
		    	{			
		    		if(YfConstant.RETURN_GP_1.equals(ReturnGp))
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}
		    		else
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}
		    	}
		    	else if
		    	(
		    		"M10LMYDJ1011".equals(TcCode) && ("4".equals(infoGp) ||	//코일제품출하지시대기 
		    		"3".equals(infoGp))		//코일제품반품
		    	)
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_KG;
		    	}
		    	else if
		    	(
		    		("M10LMYDJ1011".equals(TcCode) && "2".equals(infoGp))||	//코일제품보관지시 
		    		"M10LMYDJ1071".equals(TcCode)		//코일제품출하완료
		    	)
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_MG;
		    	}
		    	else if
		    	(
		    		"M10LMYDJ1031".equals(TcCode)	//코일제품운송지시
		    	)
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_LG;
		    	}
		    	else if
		    	(
		    		YfConstant.CURR_PROG_CD_COIL_A.equals(CurrProgCd)||
		    		YfConstant.CURR_PROG_CD_COIL_R.equals(CurrProgCd)
		    	)
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_AC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_B.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_BC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_C.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_CC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_D.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_DC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_E.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_CS;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_F.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_FC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_K.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_KG;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_G.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_GC;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_H.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_HG;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_J.equals(CurrProgCd))
		    	{
		    		if(YfConstant.RETURN_GP_1.equals(ReturnGp))
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JR;
		    		}
		    		else
		    		{
		    			ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_JG;
		    		}	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_L.equals(CurrProgCd))	//코일제품상차지시
		    	{ 
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_LG;
		    		
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_N.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_NG;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_M.equals(CurrProgCd) ||
		    			YfConstant.CURR_PROG_CD_COIL_P.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_MG;
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_X.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_XG;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_Y.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_YG;	
		    	}
		    	else if(YfConstant.CURR_PROG_CD_COIL_Z.equals(CurrProgCd))
		    	{
		    		ydStocMv = YfConstant.NEW_STOCK_MOVE_TERM_ZG;
		    	}															
		    	
		    	jrRtn.setResultCode(logId);							//Log ID
		    	jrRtn.setResultMsg(methodNm);						//Log Method Name
		    	//jrRtn.setField("STL_NO",			StlNo); 		//충당재료
		    	//jrRtn.setField("MODIFIER",			modifier); 		//수정자
		    	jrRtn.setField("CURR_PROG_CD",		CurrProgCd);	//진도코드
		    	jrRtn.setField("STOCK_MOVE_TERM",	ydStocMv  );	//저장품 이동 조건
			} 

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e) 
		{
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		}
		catch (Exception e) 
		{
			return jrRtn;
		}
	}	
	
	
	/**
	 * 야드목표행선지구분를 지정한다. PI
     *
     * @param  String	sItemGp :	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,JDTORecord inRecord
     *
     * @return String
     * @throws  
     */		
	public String[] getYdAimRtGp_PIDEV(String sItemGp, JDTORecord inRecord)
	{
		// 메세지
		String logId = inRecord.getResultCode();
		String szMsg = null;
		String currProgCd = null;
		String ydAimRtGp = null;
		String sYD_AIM_RT_GP2 = "";
		String sSKINPASS_YN = "";
		String sHCR_GP = "";
		// 메소드명
		String szMethodName = "getYdAimRtGp_PIDEV";
		String sNextProc = ""; // 다음공정
		String sPlanProc1 = ""; // 열연계획작업코드1

		String[] rVal = new String[2];

		JDTORecord recEditInRecord = JDTORecordFactory.getInstance().create();

		YfCommDAO commDao = new YfCommDAO();

		try 
		{
			// 전문받아서 szRcvTcCode에 대입
			String szRcvTcCode = commUtils.getTcCode(inRecord);
			String sSTL_NO     = commUtils.trim(inRecord.getFieldString("STL_NO"));

			if ("C".equals(sItemGp)) 
			{
				if (!"".equals(sSTL_NO)) 
				{
					recEditInRecord.setField("STL_NO", sSTL_NO);

					JDTORecordSet loadYdStock = commDao.select(recEditInRecord, getCOILCOMM1,logId, szMethodName, "코일공통검색");

					if (loadYdStock.size() <= 0) 
					{
						szMsg = "코일공통 SELECT Error :: [" + sSTL_NO + "]" + "DO NOT EXIST";
						commUtils.printLog(logId, szMsg, "SL");
						return rVal;
					} 
					else 
					{
						szMsg = inRecord.getFieldString("STL_NO") + " :: 코일공통 SELECT Success :: [" + loadYdStock.size() + "]";
						commUtils.printLog(logId, szMsg, "SL");

						sYD_AIM_RT_GP2 = commUtils.trim(loadYdStock.getRecord(0).getFieldString("YD_AIM_RT_GP2"));
						sHCR_GP        = commUtils.trim(loadYdStock.getRecord(0).getFieldString("HCR_GP"));
						sSKINPASS_YN   = commUtils.trim(loadYdStock.getRecord(0).getFieldString("SKINPASS_YN"));

						// 진도코드 존제여부 체크
						if ("".equals(commUtils.trim(loadYdStock.getRecord(0).getFieldString("CURR_PROG_CD")))) 
						{
							szMsg = "진도코드가  존재  안 함";
							commUtils.printLog(logId, szMsg, "SL");
							return rVal;
						}
						
						sNextProc  = commUtils.trim(loadYdStock.getRecord(0).getFieldString("NEXT_PROC"));
						sPlanProc1 = commUtils.trim(loadYdStock.getRecord(0).getFieldString("PLAN_PROC1"));
					}

					// 진도코드
					currProgCd = commUtils.trim(loadYdStock.getRecord(0).getFieldString("CURR_PROG_CD"));
				} 
				else 
				{
					// 진도코드
					currProgCd = commUtils.trim(inRecord.getFieldString("CURR_PROG_CD"));
				}

				szMsg = "진도코드::" + currProgCd;
				commUtils.printLog(logId, szMsg, "SL");

				// ***********************************************************//
				if ("M10LMYDJ1011".equals(szRcvTcCode)) 
				{
					ydAimRtGp = "K2"; // 코일출하지시대기
					currProgCd = "K";
				} 
				else if ("M10LMYDJ1031".equals(szRcvTcCode)) 
				{
					ydAimRtGp = "L5"; // 코일운송상차지시
					currProgCd = "L";
				} 
				else if ("M10LMYDJ1071".equals(szRcvTcCode)) 
				{
					ydAimRtGp = "M2"; // 코일출하완료
					currProgCd = "M";
					// ***********************************************************//
				} 
				else if ("G".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 종합판정대기
				} 
				else if ("I".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 반송대기
				} 
				else if ("H".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 입고대기
				} 
				else if ("Y".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "C"; // 재공충당대기(C열연정정)
				} 
				else if ("B".equals(currProgCd)) 
				{ 
					if ("H".equals(sNextProc.substring(1, 2))) 
					{
						ydAimRtGp = currProgCd + "3"; // 지시대기
					} 
					else 
					{
						ydAimRtGp = currProgCd + "4"; // 지시대기
					}
				} 
				else if ("J".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 반납대기
				} 
				else if ("Z".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 제품충당대기
				} 
				else if ("X".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "2"; // 경매대상선정
				} 
				else if ("E".equals(currProgCd) || "D".equals(currProgCd)) 
				{
					// 재공이송작업대기
					String sWorkProc = "";

					if (!"".equals(sNextProc)) 
					{
						sWorkProc = sNextProc;
					} 
					else 
					{
						sWorkProc = sPlanProc1;
					}
					
					// 계획공정정보를 가지고 야드행선을 셋팅
					if (sWorkProc.startsWith("1")) 
					{
						ydAimRtGp = "EA";
					} 
					else if (sWorkProc.startsWith("5") || sWorkProc.startsWith("6")) 
					{
						ydAimRtGp = "EB";
					} 
					else if (sWorkProc.startsWith("9S")) 
					{
						ydAimRtGp = "ED";
					} 
					else 
					{
						ydAimRtGp = "EC";
					}
				} 
				else if ("C".equals(currProgCd)) 
				{
					// 정정작업지시대기
					String sWorkProc = "";

					if (!"".equals(sNextProc)) 
					{
						sWorkProc = sNextProc;
					} 
					else 
					{
						sWorkProc = sPlanProc1;
					}

					szMsg = "다음공정(계획공정)::" + sWorkProc;
					commUtils.printLog(logId, szMsg, "SL");
					
					// 계획공정정보를 가지고 야드행선을 셋팅 _ 추후 다시 셋팅 (C열연만 셋팅 )
					if ("DH".equals(sWorkProc) || "FH".equals(sWorkProc)
							|| "GA".equals(sWorkProc) || "GH".equals(sWorkProc)
							|| "CA".equals(sWorkProc) || "CH".equals(sWorkProc)
							|| "AA".equals(sWorkProc) || "BH".equals(sWorkProc)
							|| "GT".equals(sWorkProc))
					{
						ydAimRtGp = "CE";
					} 
					else if ("HH".equals(sWorkProc) || "HK".equals(sWorkProc) || "HR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else if ("EH".equals(sWorkProc) || "EK".equals(sWorkProc) || "ER".equals(sWorkProc)) 
					{
						ydAimRtGp = "CG";
					} 
					else if ("CK".equals(sWorkProc) || "CR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else if ("BK".equals(sWorkProc) || "BR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else if ("AK".equals(sWorkProc) || "AR".equals(sWorkProc)) 
					{
						ydAimRtGp = "CF";
					} 
					else 
					{
						ydAimRtGp = "XX";
					}
					
					if ("F4".equals(sYD_AIM_RT_GP2) || "F5".equals(sYD_AIM_RT_GP2)) 
					{ 
						// 재작업인 경우
						ydAimRtGp = sYD_AIM_RT_GP2; // 재작업인(C열연정정)
					}
				} 
				else if ("F".equals(currProgCd)) 
				{
					ydAimRtGp = currProgCd + "3"; // 판정보류
				}

				// 2pass재 작업 대상
				if ("Z".equals(sSKINPASS_YN) && ("C".equals(currProgCd) || "D".equals(currProgCd))) 
				{
					ydAimRtGp = "EA";
				}
			} 
		} 
		catch (Exception e) 
		{
			szMsg = "야드목표행선지구분 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printErrorLog(logId, szMethodName, szMsg, this, e);
		}

		szMsg = "진도코드: " + currProgCd+" 야드목표행선지구분: " + ydAimRtGp;
		commUtils.printLog(logId, szMsg, "SL");
 
		rVal[0] = ydAimRtGp;
		rVal[1] = currProgCd;
		return rVal;
	}
}
