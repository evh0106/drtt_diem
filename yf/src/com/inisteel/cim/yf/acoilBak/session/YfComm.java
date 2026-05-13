package com.inisteel.cim.yf.acoilBak.session;

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
import com.inisteel.cim.yf.acoilBak.YfCommUtils;
import com.inisteel.cim.yf.acoilBak.YfConstant;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld2;
import com.inisteel.cim.yf.acoilBak.dao.YfCommDAO;

/**
 *      [A] 클래스명 : 박판열연 야드 공통 처리
 *
*/
public class YfComm implements YfQueryIFOld, YfQueryIFOld2
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

			EJBConnector ejbConn1 = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * [A] 오퍼레이션명 : 대차스케줄 공대차출발지시 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord trtTcarSchLevWo(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "대차스케줄 공대차출발지시 처리[YfComm.trtTcarSchLevWo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값 
			String sOPRN     = commUtils.nvl(commUtils.trim(rcvMsg.getFieldString("OPRN" )), "N");	//영대차여부(대차상태설정::acoilTcarWrkStatInqpp.jsp)
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" ));					//대차
			String ydBayGpTo = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP" ));                 //야드동구분(상차동)
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));					//수정자

			if ("".equals(ydEqpId))
			{
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차스케줄 정보 조회
			**********************************************************/
			String ydCurrBayGp      = ""; //야드현재동구분
			String ydHomeBayGp      = ""; //야드Home동구분
			String autoTcarSchYn    = ""; //자동대차스케줄여부
			String ydTcarSchId      = ""; //야드대차스케쥴ID
			String ydWbookIdCurr    = ""; //야드작업예약ID(현재 대차스케줄 상차작업예약ID)
			String ydBayGpCurr      = ""; //야드동구분(현재 대차스케줄 상차동)
			String ydAimBayGpCurr   = ""; //야드목표동구분(현재 대차스케줄 하차동)
			String ydWbookIdNext    = ""; //야드작업예약ID(다음 상차작업예약ID)
			String ydBayGpNext      = ""; //야드동구분(다음 작업예약 상차동)
			String ydAimBayGpNext   = ""; //야드목표동구분(다음 작업예약 하차동)
			JDTORecordSet jsChk = commDao.select(jrParam, getTcarSchLevWo, logId, methodNm, "대차스케쥴정보(공대차출발지시) 조회");

			if (jsChk.size() < 1) 
			{
				throw new Exception("대차 정보가 없습니다.");
		    }
			
			
			JDTORecord jrChk = jsChk.getRecord(0);
			ydCurrBayGp    = commUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"));
			ydHomeBayGp    = commUtils.trim(jrChk.getFieldString("YD_HOME_BAY_GP"));
			autoTcarSchYn  = commUtils.trim(jrChk.getFieldString("AUTO_TCAR_SCH_YN"));
			ydTcarSchId    = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"));
			ydWbookIdCurr  = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_CURR"));
			ydBayGpCurr    = commUtils.trim(jrChk.getFieldString("YD_BAY_GP_CURR"));
			ydAimBayGpCurr = commUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP_CURR"));
			ydWbookIdNext  = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_NEXT"));
			ydBayGpNext    = commUtils.trim(jrChk.getFieldString("YD_BAY_GP_NEXT"));
			ydAimBayGpNext = commUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP_NEXT"));

			
			/**********************************************************
			* 2. 현재동 세팅
			* -설비에 현재동이 없으면 작업예약에 상차동으로, 그것도 없으면 Home동을 현재동으로..
			**********************************************************/
			if ("".equals(ydCurrBayGp))
			{
				ydCurrBayGp = ydHomeBayGp;
			}
			
			if ("B".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_PROG_STAT"))))
			{
				throw new Exception("대차[" + ydEqpId + "]는 고장 상태입니다.");
			}
			else if (!"1".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) 
			{
				throw new Exception("대차[" + ydEqpId + "]는 Off-Line 상태입니다.");
			}
			/**********************************************************
			* 3. 현 대차 스케줄에 남아있는 이송재료가 있다면
			* 관련로직
			*  ㅁ) 대차이송재료 여부 SQL ID :: getTcarSchLevWo
			*   com.inisteel.cim.yf.acoil.session.ACoilRcvL2SeEJBSBean.rcvF1YFL011(JDTORecord)
				com.inisteel.cim.yf.common.session.YfComm.trtTcarSchLevWo(JDTORecord)
				com.inisteel.cim.yf.acoil.session.ACoilJspSeEJBSBean.updblMvStkWrkBook(GridData)
			**********************************************************/
			else if ("Y".equals(commUtils.trim(jrChk.getFieldString("TC_MTL_YN"))))
			{
				
				/**********************************************************
				* 3.1 영차가 아니면 에러.
				* 대차상태 변경 팝업 :: acoilTcarWrkStatInqpp.jsp
				**********************************************************/
				if(!"Y".equals(sOPRN))
				{
					throw new Exception("대차스케줄[" + ydEqpId + " : " + ydTcarSchId + "]의 이송재료가 존재하여 공대차출발지시를 할 수 없습니다.");
				}
				/**********************************************************
				* 3.2 화면에서 영대차 체크한 경우라면, 영대차 출발지시
				**********************************************************/
				else
				{
					/**********************************************************
					* 3.3 현 대차에 걸려있는 작업 예약 삭제
					* - 영차 도착시 작업예약은 다시 생성되므로, 불안전한 작업예약은 여기서 삭제한다.
					**********************************************************/
					JDTORecordSet rst = commDao.select(jrParam, getTcarWbook, logId, methodNm, "작업 예약 조회");
		
					String sYD_WBOOK_ID  = "";
					
					if (rst.size() > 0) 
					{
						for (int i = 0; i < rst.size(); ++i)
						{
							sYD_WBOOK_ID = rst.getRecord(i).getFieldString("YD_WBOOK_ID");
							
							jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
							commDao.update(jrParam, updDelYnWrkBook, logId, methodNm, "TB_YF_WRKBOOK 작업 예약 삭제");
							commDao.update(jrParam, updDelYnWrkBookMtl, logId, methodNm, "TB_YF_WRKBOOKMTL 작업 예약 재료 삭제");
						}
					}
					
					
					/**********************************************************
					* 3.4 대차스케줄 update(하차출발)
					* -상차위치:현재동,  하차위치:재료가 있는동(=상차동)
					**********************************************************/
					jrParam.setField("YD_TCAR_SCH_ID",		jrChk.getFieldString("YD_TCAR_SCH_ID"));
					jrParam.setField("HOME_BAY",			"N");	
					jrParam.setField("YD_CARUD_STOP_LOC",	ydEqpId.substring(0, 1) + ydBayGpTo   + ydEqpId.substring(2, 6));//하차위치(= 도착위치)
					jrParam.setField("YD_CARLD_STOP_LOC",	ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2, 6));//상차위치(= 출발위치)
					commDao.update(jrParam, updTcarSchLoc, logId, methodNm, "TB_YF_TCARSCH 대차스케쥴 수정");
					
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L006", jrParam));
					
					return jrRtn;	
				}
			}
			
			

			/**********************************************************
			* 4. 상차작업예약ID 및 상차도착위치, 하차도착위치 결정
			* 4.1 대차스케줄의 야드상차작업예약ID가 있으면 그대로
			* 4.2 대차스케줄의 야드상차작업예약ID가 없으면
			*   - 야드작업계획대차의 작업예약 정보로 작업예약 조회
			* 4.3 야드작업계획대차의 작업예약이 없고 자동대차스케줄 기준이 'Y'이면
			*   - 자동 스케줄 기준에 해당하는 작업예약 생성
			**********************************************************/
			String ydCarldWrkBookId = ""; //야드상차작업예약ID
			String ydCarldLevLoc    = ""; //야드상차출발위치
			String ydCarldStopLoc   = ""; //야드상차정지위치
			String ydCarudStopLoc   = ""; //야드하차정지위치
			String ydCarProgStat    = "0"; //야드차량진행상태(상차대기)
			String ydBayGp          = ""; //야드동구분(상차동)
			String ydAimBayGp       = ""; //야드동구분(하차동)
			String ydHomeBayGpYn    = "N";  //home동 여부
			
			
			
			
			/**********************************************************
			* 4. 상차, 하차동 결정
			**********************************************************/
			if (!"".equals(ydWbookIdCurr))
			{
				//대차스케줄 및 상차작업예약이 있는 경우
				//상차동으로 출발지시
				ydCarldWrkBookId = ydWbookIdCurr;	//야드상차작업예약ID
				ydBayGp          = ydBayGpCurr;		//야드상차동
				ydAimBayGp       = ydAimBayGpCurr;	//야드하차동
			}
			else if (!"".equals(ydWbookIdNext))
			{
				//대차의 다음 상차작업예약이 있는 경우
				ydCarldWrkBookId = ydWbookIdNext;	//야드상차작업예약ID
				ydBayGp          = ydBayGpNext;		//야드상차동
				ydAimBayGp       = ydAimBayGpNext;	//야드하차동
			}
			else if ("Y".equals(autoTcarSchYn))
			{

			}

			
			/**********************************************************
			* 5. 상차출발위치를 현재동으로...
			* **********************************************************/
			ydCarldLevLoc = ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2, 6);
			
			
			/**********************************************************
			* 6. 대차 상차작업예약이 없는 경우
			*    -상차도착위치 결정
			**********************************************************/
			if ("".equals(ydCarldWrkBookId))
			{
				/**********************************************************
				* 6.1 상차동도 없으면 상차도착위치를 home으로 보낸다.(일이 없으므로)
				**********************************************************/
				if ("".equals(ydBayGpTo)) 
				{
					ydCarldStopLoc = ydEqpId.substring(0, 1) + ydHomeBayGp + ydEqpId.substring(2, 6);
					ydHomeBayGpYn = "Y";
				}
				
				/**********************************************************
				* 6.2 상차작업예약이 없지만 상차동이 있으면, 상차도착위치를 상차동(화면에서 입력받은)으로...
				**********************************************************/
				else
				{
					ydCarldStopLoc = ydEqpId.substring(0, 1) + ydBayGpTo + ydEqpId.substring(2, 6);
				}
			}
			
			/**********************************************************
			* 7. 대차 상차작업예약이 있는 경우
			*    -상차도착위치를 상차동으로, 하차도착위치를 목적동으로...
			**********************************************************/
			else
			{
				ydCarldStopLoc = ydEqpId.substring(0, 1) + ydBayGp     + ydEqpId.substring(2, 6);//상차도착위치
				ydCarudStopLoc = ydEqpId.substring(0, 1) + ydAimBayGp  + ydEqpId.substring(2, 6);//하차도착위치
			}

			
			
			/**********************************************************
			* 8. 상차출발위치와 상차도착위치가 같으면
			**********************************************************/
			if (ydCarldLevLoc.equals(ydCarldStopLoc))
			{
				ydCarProgStat = "2"; //야드차량진행상태(상차도착)
			}
			
			
			/**********************************************************
			* 9. 대차스케줄ID 생성
			**********************************************************/
			if ("".equals(ydTcarSchId))
			{
				ydTcarSchId = commDao.getSeqId(logId, methodNm, "TcarSch");

				if ("".equals(ydTcarSchId))
				{
					throw new Exception( "대차스케줄ID 생성 중 오류가 발생하였습니다.");
				}
			}
			
			
			/**********************************************************
			* 10. 대차스케줄 생성
			* 	 - 대차스케줄 없으면 야드대차스케쥴ID 생성하여 대차스케줄 생성
			*    - 대차스케줄 있으면 대차스케줄 수정(대차스케줄이 있었다는건 진행중인 작업이 있었다는 뜻인데,,수정을 해도 되는지? KBS)
			**********************************************************/
			jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId     );	//야드대차스케쥴ID
			jrParam.setField("YD_CAR_PROG_STAT"    , ydCarProgStat   );	//야드차량진행상태
			jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydCarldWrkBookId);	//야드상차작업예약ID
			jrParam.setField("YD_CARLD_LEV_LOC"    , ydCarldLevLoc   );	//야드상차출발위치
			jrParam.setField("YD_CARLD_STOP_LOC"   , ydCarldStopLoc  );	//야드상차정지위치
			jrParam.setField("YD_CARUD_STOP_LOC"   , ydCarudStopLoc  );	//야드하차정지위치
			commDao.update(jrParam, updTcarSchInsSch, logId, methodNm, "대차스케줄 수정 또는 생성");
			
			
			/**********************************************************
			* 11. 상차출발위치와 상차도착위치가 다르면 대차출발지시 전송
			**********************************************************/
			if ("0".equals(ydCarProgStat)) 
			{	
				//home으로 가야한다면 home으로 보냄
				if("Y".equals(ydHomeBayGpYn)) 
				{
					jrParam.setField("HOME_BAY"      , "Y"     );	
					jrParam.setField("YD_CARLD_STOP_LOC" , ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2, 6) );	
					jrParam.setField("YD_CARUD_STOP_LOC" , ydEqpId.substring(0, 1) + ydHomeBayGp + ydEqpId.substring(2, 6) );	
					
				}
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L006", jrParam));
			}
			/**********************************************************
			* 12. 상차출발위치와 상차도착위치가 같고 신규 작업예약ID이면 크레인스케줄 호출
			**********************************************************/
			else if (!"".equals(ydCarldWrkBookId) && "".equals(ydWbookIdCurr))
			{
				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrYdMsg.setField("YD_WBOOK_ID"  , ydCarldWrkBookId); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_ST_GP" , "A"             ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("YD_SCH_REQ_GP", "6"             ); //야드스케쥴요청구분(공대차도착)

				jrRtn = commUtils.addSndData(jrRtn, this.getCrnSchMsg(jrYdMsg));
commUtils.printLog(logId, methodNm, "[공대차 :: 동일 동 :: 크레인스케쥴 호출 :: 작업예약ID]" + ydCarldWrkBookId);

				// 추가적인 크레인스케쥴 기동이라 오류시 무시한다.
				// - 화면 이적처리 등록 팝업
				try{
					List aListAddMvWbook = (java.util.ArrayList)rcvMsg.getField("ADD_MV_WBOOK");
					if(aListAddMvWbook != null && aListAddMvWbook.size() > 0){
						
						int nCnt = aListAddMvWbook.size();
						String sAddMvWbookId = "";
						
						// 최대적치매수
						int nSTK_MAX_QNTY = jrChk.getFieldInt("STK_MAX_QNTY");
						
						// 크레인스케츌 호출 Cnt 
						int nCallCrnSch = 1;
						
						for(int i=0; i < nCnt; i++){
							sAddMvWbookId = (String)aListAddMvWbook.get(i);
							if( ydCarldWrkBookId.equals(sAddMvWbookId))
							{
								continue;
							}
							else
							{
								
								// 중량체크는 하지말자!
								if(nCallCrnSch < nSTK_MAX_QNTY){
commUtils.printLog(logId, methodNm, "[공대차 :: 동일 동 :: 추가 크레인스케쥴 호출 :: 작업예약ID]" + sAddMvWbookId);
									jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

									jrYdMsg.setResultCode(logId);	//Log ID
									jrYdMsg.setResultMsg(methodNm);	//Log Method Name
									jrYdMsg.setField("YD_WBOOK_ID"  , sAddMvWbookId); //야드작업예약ID
									jrYdMsg.setField("YD_SCH_ST_GP" , "A"             ); //야드스케쥴기동구분(Auto)
									jrYdMsg.setField("YD_SCH_REQ_GP", "6"             ); //야드스케쥴요청구분(공대차도착)

									jrRtn = commUtils.addSndData(jrRtn, this.getCrnSchMsg(jrYdMsg));
									
									nCallCrnSch++;
								}
							}
						}
					}					
				}catch(Exception e){}

				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch(DAOException e) 
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	
	
	/**
	 * [A] 오퍼레이션명 : CTS스케줄 출발지시 재처리
	 * - 대차 고장이나 돌발 상황시, 현재편성되어 있는 모든 cts지시를 삭제한 후, cts 지시를 재편성한다.
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord reCTSSchLevWo(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "CTS스케줄 출발지시 재처리[YfComm.reCTSSchLevWo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			/**********************************************************
			* 1. 스케줄, 적치단 삭제
			**********************************************************/
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));					//수정자
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			commDao.update(jrParam, delCTSSch, logId, methodNm, "CTS스케줄 삭제");//대차위에 이미 적재된 재료는 삭제하지 않는다
			
			commDao.update(jrParam, delCTSLyr, logId, methodNm, "CTS적치단 초기화");
			
			
			
			/**********************************************************
			* 2. CTS스케줄 대상재 조회
			**********************************************************/
			JDTORecordSet schSet = commDao.select(jrParam, selectCTSLyr, logId, methodNm, "CTS스케줄 조회");
			if(schSet.size()<1)
			{
				throw new Exception("CTS 스케줄 대상이 없습니다");
			}
			
			
			
			/**********************************************************
			* 3. CTS스케줄 생성 호출
			*    - YD_WRK_PROG_STAT : 지시대기(W)로 생성
			**********************************************************/
			String aimBay = "";
			JDTORecord jrRst 		= JDTORecordFactory.getInstance().create();
			for(int i = 0; i<schSet.size(); i++)
			{
				aimBay = schSet.getRecord(i).getFieldString("YD_AIM_BAY_GP");

				//현 적치 위치가 목적동이면 크레인 스케줄 호출
				if(aimBay.equals((schSet.getRecord(i).getFieldString("YD_CARLD_WO_LOC")).substring(1, 2)))
				{
					jrParam.setField("STL_NO", 				schSet.getRecord(i).getFieldString("STL_NO"));
					jrParam.setField("YD_EQP_ID", 			schSet.getRecord(i).getFieldString("YD_WBOOK_ID"));
					jrParam.setField("YD_DN_WR_LOC", 		schSet.getRecord(i).getFieldString("YD_CARLD_WO_LOC"));
					jrParam.setField("YD_TO_LOC_GUIDE", 	schSet.getRecord(i).getFieldString("YD_TO_LOC_GUIDE"));
					
					
					jrRst = commUtils.addSndData(jrRst,this.delCraneSchCTS(jrParam));
					jrRst = commUtils.addSndData(jrRst,this.ctsCrnCallBackup(jrParam));
				}
				//현 적치 위치가 목적동이 아니라면 cts스케줄 호출
				else
				{
					jrParam.setField("STL_NO", 				schSet.getRecord(i).getFieldString("STL_NO"));
					jrParam.setField("YD_WBOOK_ID", 		schSet.getRecord(i).getFieldString("YD_WBOOK_ID"));
					jrParam.setField("YD_CARLD_STOP_LOC", 	schSet.getRecord(i).getFieldString("YD_CARLD_WO_LOC"));
					jrRst = commUtils.addSndData(jrRst,this.trtCTSSchLevWo(jrParam));
				}
			}
			
			
			
			jrRst.setField("RTN_MSG", "success");
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRst;
		}
		catch(DAOException e) 
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 
	 * <pre>
	 * [A] 오퍼레이션명 : 스케줄취소(cts 전용)
	 *  
	 *  - 재료 단위로 스케줄을 삭제한다.
	 *  - ACoilJspSeEJB.updCraneWrkCancel 참고
	 * </pre>
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */	
	public JDTORecord delCraneSchCTS(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업요구현황조회 스케줄취소[YfComm.delCraneSchCTS] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			EJBConnector ejbConn = null;
				
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자
			String stlNo  = commUtils.trim(rcvMsg.getFieldString("STL_NO")); 
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//Return Value
			JDTORecord jrRst = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn = null;
			
			boolean autoFlag = false;
			boolean mainFlag = false; //취소트랜젝션 플래그
			
			String szydEqpStat = "";
			String szEqpAutoCrnMode = "";
			String szEqpAutoCrnYN = "";
			String sYD_WRK_PROG_STAT = "";
			String sYD_EQP_ID        = "";
			String sYD_CRN_SCH_ID    = "";
			String sYD_SCH_CD        = "";
			String sYD_WBOOK_ID      = "";
			String sYD_DN_WO_LOC_ORG = "";
			
			
			/*********************************************
			 *  1. 스케줄 삭제
			 *********************************************/
			jrParam.setField("STL_NO"   , stlNo);
			JDTORecordSet rstCrnSch = commDao.select(jrParam, getSchInfo2, logId, methodNm, "스케줄 존재 여부 체크");
			
			if (rstCrnSch != null && rstCrnSch.size() > 0) 
			{
				sYD_WRK_PROG_STAT	= rstCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				sYD_EQP_ID 			= rstCrnSch.getRecord(0).getFieldString("YD_EQP_ID");
				sYD_CRN_SCH_ID		= rstCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");
				sYD_WBOOK_ID		= rstCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");
				sYD_DN_WO_LOC_ORG	= rstCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_ORG"); 
				commUtils.printLog(logId, "["+sYD_CRN_SCH_ID+"] = "+ sYD_WRK_PROG_STAT , "[INFO]");
				
				/*********************************************
				 *  1.1 스케줄 삭제 조건 체크
				 *********************************************/
				if (!"W".equals(sYD_WRK_PROG_STAT)   //대기
				  &&!"S".equals(sYD_WRK_PROG_STAT)   //대기
				  &&!"1".equals(sYD_WRK_PROG_STAT)) {//선택(권상지시)
					throw new Exception("권상 전에만 스케줄 취소가 가능합니다 : " + sYD_WRK_PROG_STAT);
				}
				
				/*********************************************
				 *  1.2 설비 상태 get
				 *********************************************/
				JDTORecord jrEqpInfo   = JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_EQP_ID" , sYD_EQP_ID);
				JDTORecordSet rsResult = commDao.select(jrParam, getYfEqp, logId, methodNm, "무인크레인 관련 위치변경 조건 체크");
				
				if (rsResult.size() > 0) {
					rsResult.first();
					jrEqpInfo = rsResult.getRecord();
					
					szydEqpStat      = jrEqpInfo.getFieldString("YD_WRK_PROG_STAT");          // 설비 상태
					szEqpAutoCrnMode = jrEqpInfo.getFieldString("YD_EQP_AUTO_CRN_MODE");// AutoCrn 상태
					szEqpAutoCrnYN   = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE2"); 	// AutoCrn 여부
					
					if ("A".equals(szEqpAutoCrnYN)) {// A:무인
						autoFlag = true; 
						mainFlag = true;
					}
				}
				
				// 위치검색실패인 경우 유인으로 처리
				if ("XX010101".equals(sYD_DN_WO_LOC_ORG)) {
					autoFlag = false;
				} 
				
				//W:명령선택대기 - L2에 작업지시가 내려가지 않은 상태
				if ("W".equals(sYD_WRK_PROG_STAT)) {
					autoFlag = false;
				}
				
				
				/*********************************
				 * 1.3 무인 크레인 작업일 경우 
				 *********************************/
				if (autoFlag) 
				{ 
					throw new Exception("무인크레인 [" + sYD_EQP_ID + "]의 경우 크레인 스케줄을 먼저 삭제해 주시기 바랍니다");
					
				} 
				else 
				{
					
					JDTORecord inRecord = commUtils.getParam(logId, methodNm, modifier);
					inRecord.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					inRecord.setField("YD_SCH_CD"		,sYD_SCH_CD);
					inRecord.setField("YD_EQP_ID"		,sYD_EQP_ID);
					inRecord.setField("YD_WBOOK_ID"		,sYD_WBOOK_ID);
					inRecord.setField("IS_SCH_MTL"		,"Y");
					
					
					ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
					jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					jrRtn = commUtils.addSndData(jrRtn, jrRst);
					
					
					
				} // end if (autoFlag)
			} //스케줄 삭제
			
			
			/*********************************
			 * 2. 작업 예약 삭제
			 *********************************/
			jrParam.setField("STL_NO"   , stlNo);
			JDTORecordSet rstWkbook = commDao.select(jrParam, getWkbookInfo2, logId, methodNm, "작업 예약 존재 여부 체크");
			if (rstWkbook != null && rstWkbook.size() > 0) 
			{
				
				sYD_WBOOK_ID		= rstWkbook.getRecord(0).getFieldString("YD_WBOOK_ID");
				commUtils.printLog(logId, "["+sYD_WBOOK_ID+"] = "+ sYD_WBOOK_ID , "[INFO]");
				
				JDTORecord inRecord = commUtils.getParam(logId, methodNm, modifier);
				inRecord.setField("YD_WBOOK_ID"		,sYD_WBOOK_ID);
				
				
				ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { inRecord });
				jrRtn = commUtils.addSndData(jrRtn, jrRst);
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	// end of updCraneSchCancel	
	
	
	
	
	/**
	 * [A] 오퍼레이션명 : cts 크레인 호출 백업
	 * - 대차 고장이나 돌발 상황시, 현재편성되어 있는 모든 cts지시를 삭제한 후, cts 지시를 재편성한다.
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord ctsCrnCallBackup(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "cts 크레인 호출 백업[YfComm.ctsCrnCallBackup] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");
			//수신 항목 값
			String msgId		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			String stlNo 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"     )); //재료번호
			String ydToLocGuide	= commUtils.trim(rcvMsg.getFieldString("YD_TO_LOC_GUIDE"     ));
			String ydDnWrLoc 	= commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"     )); //야드권하실적위치
			
			/**********************************************************
			* 2.5.1 스케줄 코드 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("STL_NO", stlNo);
			JDTORecordSet rsResult = commDao.select(jrParam, getYfZoneGp, logId, methodNm, "스케줄코드 좌우기준 조회");
			String nLtRtRule   = rsResult.getRecord(0).getFieldString("YD_RL_GP");
			String heatYn      = rsResult.getRecord(0).getFieldString("HEATING_COIL_YN");
			String ydSchCd = "";
			if("Y".equals(heatYn))
			{
				ydSchCd = ydDnWrLoc.substring(0, 2)+"TC21LM"; //난방코일
			}
			else
			{
				ydSchCd = ydDnWrLoc.substring(0, 2)+"TC"+("R".equals(nLtRtRule)?"1":"0")+"1LM";
			}
			
			
			/**********************************************************
			* 2.5.2 크레인작업예약 생성
			**********************************************************/
			JDTORecord wkParam = JDTORecordFactory.getInstance().create();
			wkParam.setResultCode(logId);	//Log ID
			wkParam.setResultMsg(methodNm);	//Log Method Name
			wkParam.setField("YD_STK_COL_GP"  	, ydDnWrLoc); 
			wkParam.setField("YD_STK_BED_NO"  	, "01"); 
			wkParam.setField("YD_STK_LYR_NO" 	, "01"); 
			wkParam.setField("YD_SCH_CD"		, ydSchCd); 
			wkParam.setField("YD_EQP_ID"		, "L");
			wkParam.setField("STL_NO"			, stlNo);
			wkParam.setField("YD_TO_LOC_GUIDE"	, ydToLocGuide);
			wkParam.setField("MODIFIER"     	, modifier);
			String ydWbookId = this.procWkBookInsert(wkParam);
			
			
			/**********************************************************
			* 2.5.3 크레인스케줄 전문 호출
			**********************************************************/
			JDTORecord schParam = JDTORecordFactory.getInstance().create();
			schParam.setResultCode(logId);	//Log ID
			schParam.setResultMsg(methodNm);	//Log Method Name
			schParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			schParam.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
			schParam.setField("YD_SCH_ST_GP" , "O"); //야드스케쥴기동구분
			schParam.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
			schParam.setField("MODIFIER"     , modifier ); //수정자

			jrRtn = commUtils.addSndData(this.getCrnSchMsg(schParam));
		
	
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : CTS스케줄 출발지시 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord trtCTSSchLevWo(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "CTS스케줄 출발지시 처리[YfComm.trtCTSSchLevWo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			/**********************************************************
			* 1. 파라미터 초기화
			**********************************************************/
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));					//수정자

			String ydCrnSchId   		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"    		));
			String ydCarLdStopLoc		= commUtils.trim(rcvMsg.getFieldString("YD_CARLD_STOP_LOC"				));
			String stlNo    			= commUtils.trim(rcvMsg.getFieldString("STL_NO"   ));	
			String ydWbookId	   		= commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"    		));
			
			
			
			/**********************************************************
			* 2. CTS스케줄 정합성 검증
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			jrParam.setField("STL_NO"			, stlNo			);	//재료번호
			
			JDTORecordSet schSet = commDao.select(jrParam, selectCTSSch, logId, methodNm, "CTS스케줄 조회");
			if(schSet.size()>0)
			{
				throw new Exception("CTS 스케줄에 이미 등록된 재료입니다.");
			}
			
			
			
			/**********************************************************
			* 3. CTS스케줄 생성
			*    - YD_WRK_PROG_STAT : 지시대기(W)로 생성
			**********************************************************/
			String ydSchId = commDao.getSeqId(logId, methodNm, "CtsSch");
			
			jrParam.setField("YD_CTS_SCH_ID"    , ydSchId     	);	//야드대차스케쥴ID
			jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);		//크레인스케줄ID
			jrParam.setField("YD_WBOOK_ID"		, ydWbookId);		//야드상차정지위치
			jrParam.setField("YD_CARLD_WO_LOC"	, ydCarLdStopLoc);	//야드상차정지위치
			commDao.insert(jrParam, insCTSSch, logId, methodNm, "CTS 스케줄 생성");
			
			
			
			/**********************************************************
			* 4. 생성된 CTS스케줄 정보 조회 
			**********************************************************/
			JDTORecordSet paramSet = commDao.select(jrParam, getCTSSchData, logId, methodNm, "CTS스케줄 조회");
			if(paramSet.size()<1)
			{
				throw new Exception("CTS 스케줄에 등록에 실패했습니다.");
			}
			
			
			
			/**********************************************************
			* 5. CTS to위치 조회 및 CTS이동지시 전송
			**********************************************************/
			JDTORecord jrRtn = null;
			JDTORecord param = paramSet.getRecord(0);
			jrRtn = getCTSToLoc(param);

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch(DAOException e) 
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	
	
	/**
	 * [A] 오퍼레이션명 : CTS to위치 조회 및 CTS이동지시 전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg 설비id
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord getCTSToLoc(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "CTS to위치 조회 및 CTS이동지시 전송[YfComm.getCTSToLoc] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			/**********************************************************
			* 1. 파라미터 초기화
			**********************************************************/
			String modifier  	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));					//수정자
			String ydEqpId	   	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));
			String ydSchId	   	= "";
			String stlNo	   	= "";
			String ydCarudWoLoc = "";
			String ydWrkProgStat= "";
			JDTORecord jrRtn 	= null;
			
			if(ydEqpId == null || "".equals(ydEqpId))
			{
				throw new Exception("설비 상태를 확인하세요.");
			}
			
			/**********************************************************
			* 2. 대차설비 상태 체크
			*    - 대차가 작업 중이라면, 작업지시를 내리지 않는다.
			*    **작업지시 상태: W(작업이 만들어진 상태), S(작업지시를 내린상태)
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId     	);	//CTS대차설비ID
			JDTORecordSet eqpSet = commDao.select(jrParam, getStatEqp, logId, methodNm, "CTS 설비상태 조회");
			if(!"W".equals(eqpSet.getRecord(0).getFieldString("YD_EQP_PROG_STAT")))
			{
				commUtils.printLog(logId, "대차가 작업중이므로 작업지시 skip : "+ydEqpId+":"
						+eqpSet.getRecord(0).getFieldString("YD_EQP_PROG_STAT")  , "SL");
				return jrRtn;
			}
			
			/**********************************************************
			* 3. 스케줄 상태 체크
			*    - 진행중인 CTS스케줄이 있다면 작업지시를 내리지 않는다. 
			**********************************************************/
			JDTORecordSet progSet = commDao.select(jrParam, getCTSProgging, logId, methodNm, "CTS작업중인 스케줄 조회");
			if("Y".equals(progSet.getRecord(0).getFieldString("IS_PROGGING")))
			{
				commUtils.printLog(logId, "CTS작업중인 스케줄이 있으므로 skip : "+ydEqpId+":"
						+progSet.getRecord(0).getFieldString("IS_PROGGING")  , "SL");
				return jrRtn;
			}
			
			
			/**********************************************************
			* 4. CTS to위치 조회
			*   - YD_WRK_PROG_STAT : 지시대기(W)를 조회.
			*   - 입력받은 대차에 걸려있는 작업예약을 모두 조회하여(W),
			*     TO위치 찾기에 성공한 작업예약중 우선순위가 가장 높은 작업예약을 전송한다.
			*   - 우선순위가 가장높은 작업예약의 TO위치만 찾으면 안된다. 
			*     실패시, 후순위 작업예약의 TO위치를 차례로 찾아 지시를 전송해야한다.  
			**********************************************************/
			JDTORecordSet schSet = commDao.select(jrParam, getCTSToLoc, logId, methodNm, "CTS to위치 조회");
			for(int i=0; i<schSet.size(); i++)
			{
				JDTORecord schRow = schSet.getRecord(i);
				
				ydSchId			= commUtils.trim(schRow.getFieldString("YD_CTS_SCH_ID"));
				ydCarudWoLoc	= commUtils.trim(schRow.getFieldString("YD_CARUD_WO_LOC"));
				ydWrkProgStat	= commUtils.trim(schRow.getFieldString("YD_WRK_PROG_STAT"));
				stlNo			= commUtils.trim(schRow.getFieldString("STL_NO"));
				if(!"".equals(ydCarudWoLoc))
				{
					commUtils.printLog(logId, "[검색된 TO위치:" + ydCarudWoLoc , "SL");
					break;
				}
			}
			
			
			/**********************************************************
			* 5. to위치가 없으면 홈으로,,,
			**********************************************************/
			if("".equals(ydCarudWoLoc))
			{
				
				commUtils.printLog(logId, "[설비id:" + ydEqpId + ", TO위치 검색 결과가 없습니다.", "SL");
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
				jrYdMsg.setResultCode(logId);	
				jrYdMsg.setResultMsg(methodNm);	
				jrYdMsg.setField("YD_EQP_ID" , ydEqpId             ); // CTS스케줄ID
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L017home", jrYdMsg));	// CTS 대차 출발 지시
				commUtils.printLog(logId, methodNm, "S-");
				return jrRtn;
			}
			
			
			
			/**********************************************************
			* 6. CTS to위치 등록
			*    - YD_WRK_PROG_STAT : 지시대기(W)일때만, to위치, 지시전송(S)으로 갱신
			**********************************************************/
			//TO위치 및 상태 변경
			jrParam.setField("YD_CTS_SCH_ID"    , ydSchId     	);	//CTS대차설비ID
			jrParam.setField("YD_CARUD_WO_LOC"	, ydCarudWoLoc);	//야드하차지시위치
			commDao.update(jrParam, updCTSToLoc, logId, methodNm, "CTS스케줄 TO위치 갱신");
			
			//적치단 UP위치 수정
			jrParam.setField("STL_NO",       	stlNo);
			commDao.update(jrParam, updStackLayerByStockId3, logId, methodNm, "CTS스케줄 - 적치단 UP위치 수정(새들)");
			
			//적치단 예약위치 수정
			jrParam.setField("YD_STK_COL_GP", 	ydCarudWoLoc);
			jrParam.setField("YD_STK_BED_NO", 	"01");
			jrParam.setField("YD_STK_LYR_NO", 	"01");
			jrParam.setField("STL_NO",       	stlNo);
			jrParam.setField("YD_STK_LYR_STAT", "D");
			commDao.update(jrParam, updYdStkLyrYdStkColBedGp, logId, methodNm, "CTS스케줄 - 적치단 TO위치 수정(새들)");
		
			
			
			/**********************************************************
			* 7. CTS 대차 출발 지시
			*    -W 일때만 지시전송
			**********************************************************/
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
			jrYdMsg.setResultCode(logId);	
			jrYdMsg.setResultMsg(methodNm);	
			jrYdMsg.setField("YD_EQP_ID" , ydEqpId             ); // CTS스케줄ID
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L017", jrYdMsg));	// CTS 대차 출발 지시
		

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch(DAOException e) 
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 압연실적을 처리
	 *
	 * param String	: 저장품ID
	 * param String	: 야드구분
	 * param String	: 처리구분
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
    public JDTORecord setInnerIFCoilInfo_01(JDTORecord rcvMsg)
    {	
		String		methodNm	= "압연실적 처리[YfComm.setInnerIFCoilInfo_01] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
    	JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
    	
    	try
    	{
    		commUtils.printLog(logId, methodNm, "S+");
    		
    		String sStockMoveTerm = "";
    		String processId   	= commUtils.trim(rcvMsg.getFieldString("PROCESS_ID"));		//01(평량), DC(권취), 91(조업시스템ERROR)
    		String coilNo    	= commUtils.trim(rcvMsg.getFieldString("COIL_NO"));			//코일번호 
			String yardId   	= commUtils.trim(rcvMsg.getFieldString("YARD_ID"));			//야드구분
			String processCd	= commUtils.trim(rcvMsg.getFieldString("PROCESS_CODE"));	//processCd
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));		//수정자(Backup Only)
			String tc_date		= commUtils.trim(rcvMsg.getFieldString("TC_DATE"));			//발생일자
			String tc_time		= commUtils.trim(rcvMsg.getFieldString("TC_TIME"));			//발생시각
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);		//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STL_NO",		coilNo);
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("MODIFIER",	modifier);
			
			commUtils.printLog(logId, "=============압연실적 처리 시작========", "SL");

			/**
		     * 1. 공통 Coil정보를 가져온다.
		     */
		    JDTORecordSet jsCoilCom = commDao.select(jrParam, getCoilCommonInfo, logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0)
		    {
		    	logger.println(LogLevel.DEBUG, this, "=압연실적=>NO HAVE COMMON COIL DATA");
		    	
		    	if("91".equals(processId))
		    	{	
		    		throw new EJBServiceException("=압연실적=>조업 91 NO HAVE COMMON COIL DATA");
		    	}
			} 
		    else 
			{
			    String sProgCd 		= jsCoilCom.getRecord(0).getFieldString("CURR_PROG_CD");
			    String sCoilProc    = "";
		    	String sNextProc 	= jsCoilCom.getRecord(0).getFieldString("NEXT_PROC");
				String sPlanProc 	= jsCoilCom.getRecord(0).getFieldString("PLAN_PROC1");
				
				if("".equals(sNextProc))
				{
					sCoilProc = sPlanProc;
				} 
				else 
				{
					sCoilProc = sNextProc;
				}
				
				if (YfConstant.CURR_PROG_CD_COIL_1.equals(sProgCd)) 
				{
					sStockMoveTerm	= YfConstant.NEW_STOCK_MOVE_TERM_1C;		//생산예정
		    	}
				else if(YfConstant.CURR_PROG_CD_COIL_3.equals(sProgCd))
				{
					commUtils.printLog(logId, methodNm+ "▶▶▶[" + coilNo + "]===압연실적 처리 종료(생산종료된 정보입니다)========", "SL");
					return jrRtn;
				}
				else
		    	{	
		    		if(YfConstant.SHEAR_SUPPLY_GP_1K.equals(sCoilProc))
		    		{
		    			//A열연 SPM
						sStockMoveTerm	= YfConstant.NEW_STOCK_MOVE_TERM_A2;	//SPM 추출
					}
		    		else if(YfConstant.SHEAR_SUPPLY_GP_1H.equals(sCoilProc))
		    		{
		    			//A열연 HFL
						sStockMoveTerm	= YfConstant.NEW_STOCK_MOVE_TERM_A1;	//HFL 추출
					}
		    		else if(YfConstant.SHEAR_SUPPLY_GP_1Q.equals(sCoilProc))
		    		{
		    			//A열연 EQL
		    			//원본소스에도 EQL추출관련 부분이 없음...
		    		}
				}
			}
		    
			if ("".equals(sStockMoveTerm)) 
			{ 
				sStockMoveTerm = YfConstant.NEW_STOCK_MOVE_TERM_A2;
			}
			
			logger.println(LogLevel.DEBUG,this,"저장품 이동조건 =" + sStockMoveTerm + "=");
			
			jrParam.setField("STOCK_ITEM",		YfConstant.ITEM_CM);
			jrParam.setField("STOCK_MOVE_TERM",	sStockMoveTerm);
			jrParam.setField("LINE_OFF_YN",		"N");
			jrParam.setField("TC_DATE",			tc_date);
			jrParam.setField("TC_TIME",			tc_time);
			
			/**
		     * 2. 저장품Table에 정보를 등록,수정한다.
		     *    최초 발생시 등록, 재 실적발생시 수정
		     */
			commDao.update(jrParam, insStockDC, logId, methodNm, "TB_YF_STOCK 수정 또는 생성(DC: 권치)");
			
			/**
			 * 3. 압연실적전문을 송신한다.
		     */
		    if(jsCoilCom.size() > 0)
		    {
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L009", jrParam));	//압연실적 정보
		    }
		    
		    commUtils.printLog(logId, methodNm, "S-");
		}
    	catch(DAOException daoe)
    	{
	        throw daoe;
	    }
    	catch(Exception e)
    	{
	        throw new EJBServiceException(e);
	    }
    	
	    return jrRtn;
	} 
	
     /**
	 * 오퍼레이션명 : 
	 *  
     * 정정실적(SPM, HFL)을 처리
     *
     * param String	: 저장품ID
     * param String	: SPM,HFL 구분
     *    
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
    public boolean setInnerIFCoilInfo_02(JDTORecord rcvMsg)
    {
		String		methodNm	= "정정실적 처리[YfComm.setInnerIFCoilInfo_02] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
    	JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
    	
    	boolean		blRtn		= false;
		
    	String		sStockItem	= "";
    	
		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo    = commUtils.trim(rcvMsg.getFieldString("COIL_NO"));	//코일번호 
			String workChk   = commUtils.trim(rcvMsg.getFieldString("WORK_CHK"));	//작업구분 HFL,SPM
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("STL_NO",		coilNo);
			
			commUtils.printLog(logId, "=============정정실적 처리 시작========", "SL");
			
			/**
		     * 1. 공통 Coil정보를 가져온다.
		     */
			JDTORecordSet jsCoilCom = commDao.select(jrParam, getCoilCommonInfo, logId, methodNm, "공통 Coil정보 조회");
			
			if(jsCoilCom == null || jsCoilCom.size() == 0)
			{ 
		    	return 	blRtn;
			}
			
			String sTotalPassProc = "";
			String sFinalPassProc = "";
			
			/**
    		 * 2. 통과공정의 정보를 비교한다.
    		 */
    		//COILCOMM TBL의 통과공정 1~5 중 가장 마지막에 입력된 통과공정을 비교하여야 한다.
			if("A".equals(commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("PLNT_GP"), "-")) && "QE".equals(commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("YD_EQP_GP"), "-")))
			{
				sFinalPassProc = "1Q";
			}
			else
			{
				for(int i=0; i<5; i++)
	    		{	 
	    			sTotalPassProc = commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("PASS_PROC" + String.valueOf(i+1)), "-");
	    			
	    			if ("-".equals(sTotalPassProc) || "".equals(sTotalPassProc))
	    			{
	    				break;
	    			}
	    			else
	    			{
	    				sFinalPassProc = sTotalPassProc;
	    			}
	    		}
			}
    		
    		if("EQLJ".equals(workChk))
    		{
    			//?
    		}
    		else
    		{
    			if (YfConstant.SHEAR_SUPPLY_GP_1K.equals(sFinalPassProc))
        		{
        			workChk = "SPM";
    			} 
        		else if (YfConstant.SHEAR_SUPPLY_GP_1Q.equals(sFinalPassProc)) 
        		{
        			workChk = "EQL";
    			}
        		else if (YfConstant.SHEAR_SUPPLY_GP_1H.equals(sFinalPassProc))
        		{
        			workChk = "HFL";
        		}
        		else
        		{
        			// Error 발생.
    				logger.println(LogLevel.DEBUG,this,"=SPM 통과공정 이상=> 잘못된 통과공정.");
    				throw new EJBServiceException("=SPM 통과공정 이상=> 잘못된 통과공정.");
        		}
    		}
    		
    		/**
		     * 3. 코일공통 진도코드 Table 참조.
		     */
    		String[] sStockInfo = commUtils.getCoilCurrProgCd(coilNo, "");
		    String sProgCd   	= sStockInfo[0];
			String sStocMv   	= sStockInfo[1];
			
	    	if(YfConstant.NEW_STOCK_MOVE_TERM_HG.equals(sStocMv))
	    	{
	    		sStockItem	   = YfConstant.ITEM_CG;
	    	}
	    	else
	    	{
	    		sStockItem	   = YfConstant.ITEM_CM;
	    	}
			
	    	/**
		     * 4. 저장품Table에 정보를 등록,수정한다.
		     */
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STOCK_ITEM",		sStockItem);
			jrParam.setField("STOCK_MOVE_TERM",	sStocMv);
			
			commDao.update(jrParam, updStockTransInfo_06, logId, methodNm, "TB_YF_STOCK 수정");
			
			/*********************** 
    		 *  LAYOUT 생성 안해도 됨
    		 *  트레킹테이블에서 관리됨
    		 ************************************/
			
			commUtils.printLog(logId, "=============정정실적 처리 종료========", "SL");
			
			commUtils.printLog(logId, methodNm, "S-");
			
			blRtn = true;	
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }
	     
	    return blRtn;
	} 
	
    /**
	 * 오퍼레이션명 : 
	 * 
	 * 보류재 실적 처리
	 * 
	 * param String	: 저장품ID
	 * param String	: 야드구분
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
    public JDTORecord setInnerIFCoilInfo_03(JDTORecord rcvMsg)
    {	
    	String		methodNm	= "보류재 실적 처리[YfComm.setInnerIFCoilInfo_03] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
    	JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); 	//코일번호 
			String yardId   = commUtils.trim(rcvMsg.getFieldString("YARD_ID")); 	//야드구분
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("STL_NO",		coilNo);
			jrParam.setField("MODIFIER",	modifier);
		    
			logger.println(LogLevel.DEBUG,this,"=============보류재실적 처리 시작========");
			
			/**
		     * 1. 공통 Coil정보를 가져온다.
		     */
			JDTORecordSet jsCoilCom = commDao.select(jrParam, getCoilCommonInfo, logId, methodNm, "공통 Coil정보 조회");
			
			if(jsCoilCom == null || jsCoilCom.size() == 0)
			{ 
				return jrRtn;
			}
			
			String sStockMoveTerm = "";
	     	
	     	if(YfConstant.YD_GP_1.equals(yardId))
	     	{
	     		sStockMoveTerm	= YfConstant.NEW_STOCK_MOVE_TERM_H1; // Coil 제품 입고완료
			}
	     	else
	     	{
				sStockMoveTerm	= YfConstant.NEW_STOCK_MOVE_TERM_HG; // Coil 제품 입고대기	
			}
			
	     	jrParam.setField("STOCK_ITEM",		YfConstant.ITEM_CG);
		    jrParam.setField("STOCK_MOVE_TERM",	sStockMoveTerm);
	     	
			/**
		     * 2. 저장품Table에 정보를 등록,수정한다.
		     */
		    commDao.update(jrParam, updateStockTransInfo_06, logId, methodNm, "TB_YF_STOCK 수정");
		    
		    /**
		     * 3. A열연은 보류장이 없다.
		     *    따라서, 보류재 실적처리시 출하로 제품입고 TC를 송신한다.
		     */
			if(YfConstant.YD_GP_1.equals(yardId))
			{	
		    	/*********************************************
				 * YDDMR001(일관제철 코일입고작업실적) 송신
				 *********************************************/ 
				jrParam.setField("STL_NO", coilNo);
				JDTORecordSet dmRc = commDao.select(jrParam, getMsgYMDM001Info,logId, methodNm, "YDDMR001전문생성조회");

				if (dmRc.size() > 0) 
				{
					String sPut_Position = StringHelper.evl(dmRc.getRecord(0).getFieldString("PUT_POSITION"), "");
					String sCURR_PROG_CD = StringHelper.evl(dmRc.getRecord(0).getFieldString("CURR_PROG_CD"), "");
				
					String sYardGp = sPut_Position.substring(0, 1);
					commUtils.printLog(logId, "내부IF호출 : YDDMR001(일관제철 코일입고작업실적)","[INFO]");
	                //코일입고작업실적
					JDTORecord tcRecordDM = JDTORecordFactory.getInstance().create();
					tcRecordDM.setField("JMS_TC_CD",			"YDDMR001");
					tcRecordDM.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
					tcRecordDM.setField("TC_CODE",				"YDDMR001");
					tcRecordDM.setField("TC_CREATE_DDTT",		commUtils.getDateTime14());
					tcRecordDM.setField("RECEIPT_DATE",			commUtils.getDate8());
					tcRecordDM.setField("RECEIPT_TIME",			commUtils.getTime6());
					tcRecordDM.setField("GOODS_NO",				coilNo);
					tcRecordDM.setField("YD_GP",				sYardGp);
					tcRecordDM.setField("STORE_LOC",			sPut_Position);
					tcRecordDM.setField("CURR_PROG_CD",			sCURR_PROG_CD);
					
					//인터페이스 전문 호출
			        jrRtn = commUtils.addSndData(jrRtn, tcRecordDM);		
				}
			}
			
			logger.println(LogLevel.DEBUG,this,"=============보류재실적 처리 종료========");
			
			commUtils.printLog(logId, methodNm, "S-");
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }
		
		return jrRtn;
	} 
	 
    /**
	 * 오퍼레이션명 : 
	 *  
	 * 반납 실적 처리
     *
     * param String	: 저장품ID
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
    public boolean setInnerIFCoilInfo_04(JDTORecord rcvMsg)
    {	
    	String		methodNm	= "반납 실적 처리[YfComm.setInnerIFCoilInfo_04] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
    	JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
    	
    	boolean		blRtn		= false;
		
		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); 	//코일번호 
			String yardId   = commUtils.trim(rcvMsg.getFieldString("YARD_ID")); 	//야드구분
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("STL_NO",		coilNo);
			jrParam.setField("MODIFIER",	modifier);
			
			commUtils.printLog(logId, "============반납실적 처리 시작========", "SL");
			
			/**
		     * 1. 공통 Coil정보를 가져온다.
		     */
			JDTORecordSet jsCoilCom = commDao.select(jrParam, getCoilCommonInfo, logId, methodNm, "공통 Coil정보 조회");
			
			if(jsCoilCom == null || jsCoilCom.size() == 0)
			{ 
		    	return 	blRtn;
			}
			
			/**
		     * 2. 저장품Table에 정보를 등록,수정한다.
		     */
		    jrParam.setField("STOCK_MOVE_TERM", YfConstant.NEW_STOCK_MOVE_TERM_JR);
		    
		    commDao.update(jrParam, updateStockTransInfo, logId, methodNm, "TB_YF_STOCK 수정");
			
			logger.println(LogLevel.DEBUG,this,"=============반납실적 처리 종료========");
			
			commUtils.printLog(logId, methodNm, "S-");
			
			blRtn = true;
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }    
	     
	    return blRtn;
	} 
	
    /**
	 * 오퍼레이션명 : 
	 * 
	 * 모 Coil 종료
     *
     * param String	: 저장품ID
     * param String	: 야드구분
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
    public JDTORecord setInnerIFCoilInfo_05(JDTORecord rcvMsg)
    {
    	String		methodNm	= "모코일종료 처리[YfComm.setInnerIFCoilInfo_05] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
    	JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
    	
    	boolean		blRtn		= false;
		String		workChk		= "";
		
		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO"));		//코일번호 
			String yardId   = commUtils.trim(rcvMsg.getFieldString("YARD_ID"));		//야드구분
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("STL_NO",		coilNo);
			jrParam.setField("MODIFIER",	modifier);
			
			logger.println(LogLevel.DEBUG,this,"=============모코일종료 시작========");
			
			/**
		     * 1. 공통 Coil정보를 가져온다.
		     */
			JDTORecordSet jsCoilCom = commDao.select(jrParam, getCoilCommonInfo, logId, methodNm, "공통 Coil정보 조회");
			
			if(jsCoilCom == null || jsCoilCom.size() == 0)
			{ 
		    	return 	jrRtn;
			}
			
			String sTotalPassProc = "";
			String sFinalPassProc = "";
			
			/**
    		 * 2. 통과공정의 정보를 비교한다.
    		 */
			//COILCOMM TBL의 통과공정 1~5 중 가장 마지막에 입력된 통과공정을 비교하여야 한다.
			if("A".equals(commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("PLNT_GP"), "-")) && "QE".equals(commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("YD_EQP_GP"), "-")))
			{
				sFinalPassProc = "1Q";
			}
			else
			{
				for(int i=0; i<5; i++)
	    		{	 
	    			sTotalPassProc = commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("PASS_PROC" + String.valueOf(i+1)), "-");
	    			
	    			if ("-".equals(sTotalPassProc) || "".equals(sTotalPassProc))
	    			{
	    				break;
	    			}
	    			else
	    			{
	    				sFinalPassProc = sTotalPassProc;
	    			}
	    		}
			}

    		if (YfConstant.SHEAR_SUPPLY_GP_1K.equals(sFinalPassProc))
    		{
    			workChk = "SPM";
			} 
    		else if (YfConstant.SHEAR_SUPPLY_GP_1Q.equals(sFinalPassProc)) 
    		{
    			workChk = "EQL";
			}
			
      		/**
			 * 3. Coil 입고실적 (출하로 제품입고실적 송신 YMDM001) 공통 진도 Code가 입고대기 H 이면 출하로
			 * 입고실적송신
			 */
      		JDTORecordSet StockList = commDao.select(jrParam, HRPlatecommlist, logId, methodNm, "공통 HRPlate정보 조회");		//20.02.13 정종균GJ요청에 의해 YF테이블에서 YM테이블로 변경
      		
      		//압연실적처리
			for (int i = 0; i < StockList.size(); i++)
			{
				rcvMsg.setField("COIL_NO" , StockList.getRecord(i).getFieldString("COIL_NO"));
				rcvMsg.setField("YARD_ID" , StockList.getRecord(i).getFieldString("YD_GP"));
				
				jrRtn = commUtils.addSndData(jrRtn, setInnerIFCoilInfo_HP(rcvMsg));
		   	}
			
			//진도코드가 H(입고대기) 상태만 출하로 전송 함
			JDTORecordSet jsStockList2 = commDao.select(jrParam, HRPlatecommlist2, logId, methodNm, "공통 HRPlate정보 조회2");
			
			JDTORecord	jrStockList2	= JDTORecordFactory.getInstance().create();
			JDTORecord	tcRecord1		= JDTORecordFactory.getInstance().create();
			
			for(int Loop_i = 1; Loop_i <= jsStockList2.size(); Loop_i++)
			{
				jsStockList2.absolute(Loop_i);
				
				jrStockList2.setRecord( jsStockList2.getRecord() );
						
				tcRecord1 = JDTORecordFactory.getInstance().create();
				tcRecord1.setField("TC_CODE"			, "YDDMR003");
				tcRecord1.setField("TC_CREATE_DDTT"		, commUtils.getDateTime14());
				tcRecord1.setField("JMS_TC_CD"			, "YDDMR003");				
				tcRecord1.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
				tcRecord1.setField("GOODS_NO"			, commUtils.trim(jrStockList2.getFieldString("COIL_NO")));
				tcRecord1.setField("RECEIPT_DATE"		, commUtils.trim(jrStockList2.getFieldString("RECEIPT_DATE")));
				tcRecord1.setField("RECEIPT_TIME"		, commUtils.trim(jrStockList2.getFieldString("RECEIPT_TIME")));
				tcRecord1.setField("YD_GP"				, commUtils.trim(jrStockList2.getFieldString("YD_GP")));
				tcRecord1.setField("STORE_LOC"			, commUtils.trim(jrStockList2.getFieldString("STORE_LOC")));
				tcRecord1.setField("PROD_ITEM_CODE"		, "");
				
				//내부인터페이스 송신모듈 호출 
				jrRtn = commUtils.addSndData(jrRtn, tcRecord1);	
	   		} //for end
      		
			/**
		     * 4. TB_YF_STOCK -> STL_NO기준으로 DEL_YN = 'Y'
		     */
			commDao.update(jrParam, updateStockDelYnInfo, logId, methodNm, "TB_YF_STOCK 수정 -> STL_NO 삭제");
			
			/**
		     * 5. 저장품제원 : 코일야드L2 로 송신(YFF1L002)
		     */
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);						//Log ID
			sndL2Msg.setResultMsg(methodNm);					//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",	"5");		//야드정보동기화코드
			sndL2Msg.setField("MSG_GP",				"D");		//전문구분
			sndL2Msg.setField("STL_NO",				coilNo);	//재료번호

			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));
			
			/**
		     * 6. TB_YF_STKLYR -> STL_NO기준으로  야드 MAP clear 정보 셋팅
		     */
			commDao.update(jrParam, updStackLayer, logId, methodNm, "STL_NO기준으로  TB_YF_STKLYR 야드 MAP clear 정보 셋팅");
			
			/**
		   	 * 7. 모 Coil 종료 처리 종료 발생시에 야드에 존재하는 
		   	 *    코일정보가 있으면 삭제한다.
		   	 */
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getYdWrkbookDelChk2, logId, methodNm, "크레인스케줄재료 조회");
			
			String ydCrnSchId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //크레인 작업지시
			String ydWbookId  = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));  //작업예약ID
			String ydWrkProgStat = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));	//크레인 작업지시
			
			commUtils.printLog(logId, "작업취소 [ YD_CRN_SCH_ID : " + ydCrnSchId + " / YD_WBOOK_ID : " + ydWbookId + " / YD_WRK_PROG_STAT : " + ydWrkProgStat + " ]", "SL");
			
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
			
			JDTORecord 	jrRst = JDTORecordFactory.getInstance().create();
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			if(!"".equals(ydCrnSchId)) 
			{
				/**********************************************************
				* 크레인스케줄 취소
				**********************************************************/
				jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				jrRtn = commUtils.addSndData(jrRtn, jrRst);
			}
				
			if(!"".equals(ydWbookId)) 
			{
				/**********************************************************
				* 작업예약 취소
				**********************************************************/
				jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				jrRtn = commUtils.addSndData(jrRtn, jrRst);
			}
			
			logger.println(LogLevel.DEBUG,this,"=============모코일종료 종료========");
			
			commUtils.printLog(logId, methodNm, "S-");
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }    
	     
	    return jrRtn;
	}
    
    /**
	 * 오퍼레이션명 : 
	 *  
     * 자 Coil 실적 처리
     *
     * param String	: 저장품ID
     * param String	: 야드구분
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public JDTORecord setInnerIFCoilInfo_06(JDTORecord rcvMsg)
    {
    	String		methodNm	= "자 Coil 실적 처리[YfComm.setInnerIFCoilInfo_06] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
    	JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); 	//코일번호 
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("STL_NO",		coilNo);
			jrParam.setField("MODIFIER",	modifier);
			
			logger.println(LogLevel.DEBUG,this,"=============자 Coil 실적 처리 시작========");
			
			/**
		     * 1. 공통 Coil정보를 가져온다.
		     */
			JDTORecordSet jsCoilCom = commDao.select(jrParam, getCoilCommonInfo, logId, methodNm, "공통 Coil정보 조회");
	    
		    if(jsCoilCom == null || jsCoilCom.size() == 0)
		    { 
		    	return 	jrRtn;
			}
		    
		    /**
		     * 2. 저장품Table에 정보를 등록,수정한다.
		     *    최초 발생시 등록, 재 실적발생시 수정
		     */
		    JDTORecordSet jsExist = commDao.select(jrParam, getByPrimaryKey, logId, methodNm, "저장품Table 조회");
		    
		    if(jsExist == null || jsExist.size() == 0)
		    {
				JDTORecord jrRtnProg = this.getCoilCurrProgCd(jrParam);
				
	    		String sStockItem = "";
	    		
	    		if(YfConstant.NEW_STOCK_MOVE_TERM_HG.equals(commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM"))))
	    		{
		    		sStockItem	   = YfConstant.ITEM_CG;
		    	}
	    		else
	    		{
		    		sStockItem	   = YfConstant.ITEM_CM;
		    	}
	    		
		    	if("".equals(jrRtnProg.getFieldString("STOCK_MOVE_TERM")))
		    	{
		    		return 	jrRtn;
		    	}
				
				jrParam.setField("STL_NO",			coilNo);
				jrParam.setField("STOCK_ITEM",		sStockItem);
				jrParam.setField("STOCK_MOVE_TERM",	commUtils.trim(jrRtnProg.getFieldString("STOCK_MOVE_TERM")));
				jrParam.setField("MODIFIER",		modifier);
				
				commDao.insert(jrParam, insStockTransInfo, logId, methodNm, "TB_YF_STOCK 생성");
				
				//저장품제원 : 코일야드L2 로 송신(YFF1L002)
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				sndL2Msg.setResultCode(logId);						//Log ID
				sndL2Msg.setResultMsg(methodNm);					//Log Method Name
				sndL2Msg.setField("YD_INFO_SYNC_CD",	"R");		//야드정보동기화코드
				sndL2Msg.setField("MSG_GP",				"I");		//전문구분
				sndL2Msg.setField("STL_NO",				coilNo);	//재료번호

				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));
			}
	    	else
	    	{
				//throw new EJBServiceException("=자 Coil 실적=>EXIST STOCK TABLE COIL DATA");
	    		return 	jrRtn;
			}
		    
		    logger.println(LogLevel.DEBUG,this,"=============자 Coil 실적 처리 종료========");
		    
		    commUtils.printLog(logId, methodNm, "S-");
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }    
	     
	    return jrRtn;
	} 
        
    /**
	 * 오퍼레이션명 : 
	 *  
     * SPM 재작업
     *
     * param String	: 저장품ID
     * param String	: 야드구분
     *
     *	수신이 되면 대상재가 출측에 있다면 
	 *	입측 D5로 보내고 조업으로 보급완료실적을 송신
	 *  대상재가 입측에 있다면 그대로 두고 보급완료실적을 송신
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
    public JDTORecord setInnerIFCoilInfo_07(JDTORecord rcvMsg)
    {	
    	String		methodNm	= "SPM 재작업[YfComm.setInnerIFCoilInfo_07] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
    	JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
    	
    	boolean		blRtn		= false;
    	
		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); 	//코일번호 
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			String yardId	= commUtils.trim(rcvMsg.getFieldString("YARD_ID"));		//야드구분
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("STL_NO",		coilNo);
			jrParam.setField("MODIFIER",	modifier);
			jrParam.setField("YARD_ID",		yardId);
			
			logger.println(LogLevel.DEBUG,this,"=============SPM 재작업 처리 시작========");
			
			/**
		     * 1. 공통 Coil정보를 가져온다.
		     */
			JDTORecordSet jsCoilCom = commDao.select(jrParam, getCoilCommonInfo, logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0)
		    { 
		    	return 	jrRtn;
		    	//throw new EJBServiceException("공통 Coil정보 NULL");
			}
			
		    String sTotalPassProc = "";
			String sFinalPassProc = "";
			
			/**
    		 * 2. 통과공정의 정보를 비교한다.
    		 */
			//COILCOMM TBL의 통과공정 1~5 중 가장 마지막에 입력된 통과공정을 비교하여야 한다.
			if("A".equals(commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("PLNT_GP"), "-")) && "QE".equals(commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("YD_EQP_GP"), "-")))
			{
				sFinalPassProc = "1Q";
			}
			else
			{
				for(int i=0; i<5; i++)
	    		{	 
	    			sTotalPassProc = commUtils.nvl(jsCoilCom.getRecord(0).getFieldString("PASS_PROC" + String.valueOf(i+1)), "-");
	    			
	    			if ("-".equals(sTotalPassProc) || "".equals(sTotalPassProc))
	    			{
	    				break;
	    			}
	    			else
	    			{
	    				sFinalPassProc = sTotalPassProc;
	    			}
	    		}
			}
		    
		    /**
		     * 3. SPM 입,출측 셋팅
		     */
			String sOStackColGp1  = "";
			String sOStackColGp2  = "";
    		
    		if(YfConstant.YD_GP_1.equals(yardId))
    		{
    			/**
				 * SPM 적치열 정보 셋팅
				 * 1EKD01	SPM출측컨베이어
				 * 1FKD01	SPM출측컨베이어 
				 */
			   	sOStackColGp1  = YfConstant.SPM_COL_1EKD + YfConstant.STACK_BED_GP_01;
			   	sOStackColGp2  = YfConstant.SPM_COL_1FKD + YfConstant.STACK_BED_GP_01;
			   	
			    if( "1Q".equals(sFinalPassProc) )
			    {
			    	rcvMsg.setField("WORK_CHK",	"SPM");
			    	blRtn = setInnerIFCoilInfo_02(rcvMsg);
			    	return jrRtn;
			    }
			    
			   	/**
				 * 4. 출측에 저장품이 존재하는지 체크
				 */
				jrParam.setField("YD_STK_COL_GP", sOStackColGp1);
				JDTORecordSet jsOutLayer = commDao.select(jrParam, getStackLayerInfoWithStockId, logId, methodNm, "TB_YF_EQPTRACKING 조회");
				
				if(jsOutLayer.size() == 0)
				{	
					jrParam.setField("YD_STK_COL_GP", sOStackColGp2);
					jsOutLayer = commDao.select(jrParam, getStackLayerInfoWithStockId, logId, methodNm, "TB_YF_EQPTRACKING 조회");
					
					//출측에 존재를 안하는 경우
					if(jsOutLayer.size() == 0)
					{						
						//정정실적 처리
			    		rcvMsg.setField("WORK_CHK",	"SPM");
			    		blRtn = setInnerIFCoilInfo_02(rcvMsg); 
			    		return 	jrRtn;
					}
				}
				
				/**
				 * 5. 출측에 저장품이 있으면
				 *    출측정보 삭제 후 보급완료 실적 송신
				 */ 
				if (jsOutLayer.size() > 0) 
				{
					commDao.update(jrParam, updStackLayer, logId, methodNm, "TB_YF_STKLYR 삭제");
							  				 				  			 			  			 
					/**
					 * 4. 보급완료 실적을 송신
					 */
					//-----------------------
					//코일보급 및 보급취소(YMPOJ161)
					JDTORecord tcRecord2 = JDTORecordFactory.getInstance().create();
	
					tcRecord2.setField("JMS_TC_CD",				"YMPOJ161");
					tcRecord2.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
					
					tcRecord2.setField("tcCode",				"YMPOJ161");				//TC Code
					tcRecord2.setField("tcDate",				commUtils.getDate10());		//발생일자
					tcRecord2.setField("tcTime",				commUtils.getTime8());		//발생시각
					tcRecord2.setField("plantGbn",				"A");						//공장구분
					tcRecord2.setField("procGbn",				"S");						//공정구분
					tcRecord2.setField("coilNo",				coilNo);					//COIL_NO
					tcRecord2.setField("processId",				"5");						//처리구분
					tcRecord2.setField("downDate",				commUtils.getDate8());		//권하일자 
					tcRecord2.setField("downTime",				commUtils.getTime6());		//권하시각
					tcRecord2.setField("positionNo",			YfConstant.PO_POSITION_D5);	//위치
				
				    //내부인터페이스 송신모듈 호출 
					jrRtn = commUtils.addSndData(jrRtn, tcRecord2);
			
				    commUtils.printLog(logId, "내부IF호출=YMPOJ161 코일보급 및 보급취소BACKUP처리 " + methodNm, "[INFO]");
					
				    //품질L3열연정정입측보급실적
				    JDTORecord tcParam = JDTORecordFactory.getInstance().create();
					tcParam.setField("JMS_TC_CD",				"YDQMJ002");
					tcParam.setField("JMS_TC_CREATE_DDTT",		commUtils.getDateTime14());
					tcParam.setField("STL_NO",					coilNo.trim());
					jrRtn = commUtils.addSndData(jrRtn, tcParam);
					
					commUtils.printLog(logId, "내부IF호출=YDQMJ002 품질 L3 열연정정입측보급실적 전송 송신 " + methodNm, "[INFO]");
				}
    		}
    		
    		blRtn = true;
    		
    		logger.println(LogLevel.DEBUG,this,"=============SPM 재작업 처리 종료========");
    		
    		commUtils.printLog(logId, methodNm, "S-");
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }    
	     
	    return jrRtn;
	}
    
    /**
	 * 오퍼레이션명 : 
	 *  
     * EQL 재작업
     *
     * param String	: 저장품ID
     * param String	: 야드구분
     *
     * 수신이 되면 대상재가 출측에 있다면 
	 * 입측 D5로 보내고 조업으로 보급완료실적을 송신
	 * 대상재가 입측에 있다면 그대로 두고 보급완료실적을 송신
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
    public JDTORecord setInnerIFCoilInfoEQL_07(JDTORecord rcvMsg)
    {
    	String		methodNm	= "EQL 재작업[YfComm.setInnerIFCoilInfoEQL_07] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
    	JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
    	
    	boolean		blRtn		= false;
		
    	try
    	{
    		commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo   = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); 	//코일번호 
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			String yardId	= commUtils.trim(rcvMsg.getFieldString("YARD_ID"));		//야드구분
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("STL_NO",		coilNo);
			jrParam.setField("MODIFIER",	modifier);
			jrParam.setField("YARD_ID",		yardId);
		        
		    logger.println(LogLevel.DEBUG,this,"=============EQL 재작업 처리 시작========");
		    
		    /**
		     * 1. 공통 Coil정보를 가져온다.
		     */
			JDTORecordSet jsCoilCom = commDao.select(jrParam, getCoilCommonInfo, logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0)
		    { 
		    	return 	jrRtn;
		    	//throw new EJBServiceException("공통 Coil정보 NULL");
			}
		    
		    /**
		     * 2. EQL 입,출측 셋팅
		     */
		    String sOStackColGp1  = "";
		    String sOStackColGp2  = "";
				
		    if(YfConstant.YD_GP_1.equals(yardId))
		    {
		    	/**
		    	 * EQL 적치열 정보 셋팅
		    	 * 1FQD01	EQL출측컨베이어
		    	 * 1GQD01	EQL출측컨베이어 
		    	 */
		    	sOStackColGp1  = YfConstant.EQL_COL_1FQD + YfConstant.STACK_BED_GP_01;
		    	sOStackColGp2  = YfConstant.EQL_COL_1GQD + YfConstant.STACK_BED_GP_01;
						    	
		    	/**
		    	 * 3. 출측에 저장품이 존재하는지 체크
		    	 */
		    	jrParam.setField("YD_STK_COL_GP", sOStackColGp1);
				JDTORecordSet jsOutLayer = commDao.select(jrParam, getStackLayerInfoWithStockId, logId, methodNm, "TB_YF_EQPTRACKING 조회");
				
				if(jsOutLayer.size() == 0)
				{	
					jrParam.setField("YD_STK_COL_GP", sOStackColGp2);
					jsOutLayer = commDao.select(jrParam, getStackLayerInfoWithStockId, logId, methodNm, "TB_YF_EQPTRACKING 조회");
					
					//출측에 존재를 안하는 경우
					if(jsOutLayer.size() == 0)
					{						
						//정정실적 처리
			    		rcvMsg.setField("WORK_CHK",	"EQLJ");
			    		blRtn = setInnerIFCoilInfo_02(rcvMsg);
			    		return 	jrRtn;
					}
				}
				
				/**
				 * 4. 출측에 저장품이 있으면
				 *    출측정보 삭제
				 */
				if (jsOutLayer.size() > 0) 
				{
					commDao.update(jrParam, updStackLayer, logId, methodNm, "TB_YF_STKLYR 삭제");
					
					/**
					 * 4. 보급완료 실적을 송신
					 */
					//윤재광차장 요청으로 사용안함
					//코일보급 및 보급취소(YMPOJ161)
//					JDTORecord tcRecord2 = JDTORecordFactory.getInstance().create();
//	
//					tcRecord2.setField("JMS_TC_CD",				"YMPOJ161");
//					tcRecord2.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
//					
//					tcRecord2.setField("tcCode",				"YMPOJ161");				//TC Code
//					tcRecord2.setField("tcDate",				commUtils.getDate10());		//발생일자
//					tcRecord2.setField("tcTime",				commUtils.getTime8());		//발생시각
//					tcRecord2.setField("plantGbn",				"A");						//공장구분
//					tcRecord2.setField("procGbn",				"N");						//공정구분
//					tcRecord2.setField("coilNo",				coilNo);					//COIL_NO
//					tcRecord2.setField("processId",				"5");						//처리구분
//					tcRecord2.setField("downDate",				commUtils.getDate8());		//권하일자 
//					tcRecord2.setField("downTime",				commUtils.getTime6());		//권하시각
//					tcRecord2.setField("positionNo",			YfConstant.PO_POSITION_D5);	//위치
//				
//				    //내부인터페이스 송신모듈 호출 
//					jrRtn = commUtils.addSndData(jrRtn, tcRecord2);
			
//				    commUtils.printLog(logId, "내부IF호출=YMPOJ161 코일보급 및 보급취소BACKUP처리 " + methodNm, "[INFO]");
					
				    //품질L3열연정정입측보급실적
				    JDTORecord tcParam = JDTORecordFactory.getInstance().create();
					tcParam.setField("JMS_TC_CD",				"YDQMJ002");
					tcParam.setField("JMS_TC_CREATE_DDTT",		commUtils.getDateTime14());
					tcParam.setField("STL_NO",					coilNo.trim());
					jrRtn = commUtils.addSndData(jrRtn, tcParam);
					
					commUtils.printLog(logId, "내부IF호출=YDQMJ002 품질 L3 열연정정입측보급실적 전송 송신 " + methodNm, "[INFO]");
				}
		    }	
			
		    blRtn = true;
		    
			logger.println(LogLevel.DEBUG,this,"=============EQL 재작업 처리 종료========");
			
			commUtils.printLog(logId, methodNm, "S-"); 
		}
    	catch(DAOException daoe)
    	{
	        throw daoe;
	    }
    	catch(Exception e)
    	{
	        throw new EJBServiceException(e);
	    }    
	     
	    return jrRtn;
	}
    
    /**
	 * 오퍼레이션명 : 요구차 공정 변경...박판COIL로직은 없어서 사용안함...테스트 후 이상 없으면 삭제 예정
     * param String	: 저장품ID
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
    public boolean setInnerIFCoilInfo_11(JDTORecord rcvMsg)
    {
    	String		methodNm	= "요구차 공정 변경[YfComm.setInnerIFCoilInfo_11] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
    	JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
    	
    	boolean		blRtn		= false;
		
		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo   	= commUtils.trim(rcvMsg.getFieldString("COIL_NO")); 	//코일번호
			String processCode	= commUtils.trim(rcvMsg.getFieldString("PROCESS_CODE"));
			String modifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("STL_NO",		coilNo);
			jrParam.setField("MODIFIER",	modifier);
			
			logger.println(LogLevel.DEBUG,this,"=============요구공정 변경 처리 시작========");
			
			/**
		     * 1. 저장품의 MAP정보를 가져온다.
		     */
			//JDTORecordSet jsMap = commDao.select(jrParam, getEqpTracking, logId, methodNm, "MAP/TRACKING 정보 조회");
			JDTORecordSet jsMap = commDao.select(jrParam, getStackLayerInfoWithStockId_03, logId, methodNm, "TB_YF_STKLYR 에서 STL_NO 조회");
		    
			if(jsMap.size() == 0 || jsMap == null)
			{ 
				return 	blRtn;
		    }
			else
			{
				String sStockMoveTerm 	= "";  		
	    		/**
			     * 2. 저장품Table에 정보를 등록,수정한다.
			     *    최초 발생시 등록, 재 실적발생시 수정
			     */
			    if(YfConstant.SHEAR_SUPPLY_GP_5K.equals(processCode))
			    {
			    	//B열연 SPM
			    	sStockMoveTerm = YfConstant.NEW_STOCK_MOVE_TERM_A2;		//SPM 추출
				}
			    else if(YfConstant.SHEAR_SUPPLY_GP_5H.equals(processCode))
			    {
			    	//B열연 HFL
					sStockMoveTerm = YfConstant.NEW_STOCK_MOVE_TERM_A1;		//HFL 추출
				}
			    else if(YfConstant.SHEAR_SUPPLY_GP_5T.equals(processCode))
			    {
			    	//B열연 수냉재
					sStockMoveTerm = YfConstant.NEW_STOCK_MOVE_TERM_A3;		//수냉재 추출
				}
			    else if(YfConstant.SHEAR_SUPPLY_GP_5A.equals(processCode))
			    {
			    	//B열연 공냉재
					sStockMoveTerm = YfConstant.NEW_STOCK_MOVE_TERM_A4;		//공냉재 추출
				} 
			    else if(YfConstant.SHEAR_SUPPLY_GP_6K.equals(processCode))
			    {
			    	// B열연 SPM2  
					sStockMoveTerm = YfConstant.NEW_STOCK_MOVE_TERM_A6;		// SPM2 추출
				}
			    
			    jrParam.setField("STOCK_MOVE_TERM", sStockMoveTerm);
		     	 
				commDao.update(jrParam, updateStockTransInfo, logId, methodNm, "TB_YF_STOCK 수정");
			}
			
			blRtn = true;
			
			logger.println(LogLevel.DEBUG,this,"=============요구공정 변경 처리 종료========");
			
			commUtils.printLog(logId, methodNm, "S-");
		}
		catch(DAOException daoe)
		{
	        throw daoe;
	    }
		catch(Exception e)
		{
	        throw new EJBServiceException(e);
	    }    
	     
	    return blRtn;
	} 
    
    //박판에는 setInnerIFCoilInfo_HP 없음 테스트 후 이상 없으면 삭제 예정
    /**
	 * 오퍼레이션명 : 
	 *  
     * 압연실적을 처리
     *
     * param String	: 저장품ID
     * param String	: 야드구분
     * param String	: 처리구분
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public JDTORecord setInnerIFCoilInfo_HP(JDTORecord rcvMsg)
	{	
		String		methodNm	= "압연실적 처리[YfComm.setInnerIFCoilInfo_HP] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode(); 
		JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		
		boolean		blRtn		= false;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			String coilNo    = commUtils.trim(rcvMsg.getFieldString("COIL_NO")); //코일번호 
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String processId = commUtils.trim(rcvMsg.getFieldString("PROCESS_ID"));
			String yardId    = commUtils.trim(rcvMsg.getFieldString("YARD_ID"));
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("COIL_NO",		coilNo);
			jrParam.setField("STL_NO",		coilNo);
			jrParam.setField("MODIFIER",	modifier);
			
			String sStockMoveTerm = "";

			/**
			 * 1. 공통 Coil정보를 가져온다.
			 */
			JDTORecordSet jsCoilCom = commDao.select(jrParam, getCoilCommonInfo, logId, methodNm, "공통 Coil정보 조회");
		    
		    if(jsCoilCom == null || jsCoilCom.size() == 0)
		    { 
		    	if("91".equals(processId))
		    	{	
					throw new EJBServiceException("=Plate실적=>조업 91 NO HAVE COMMON COIL DATA");
				}
			}
			
			if("".equals(sStockMoveTerm))
			{ 
				sStockMoveTerm =  YfConstant.NEW_STOCK_MOVE_TERM_A2;
			}
			
			/**
			 * 2. 저장품Table에 정보를 등록,수정한다.
			 *	    최초 발생시 등록, 재 실적발생시 수정
			 */       
		    jrParam.setField("STOCK_ITEM END",	YfConstant.ITEM_HP);
		    jrParam.setField("STOCK_MOVE_TERM",	sStockMoveTerm);
		    //commDao.update(jrParam, insStock, logId, methodNm, "저장품Table 수정");
		    commDao.updateTx(jrParam, insStock, logId, methodNm, "저장품Table 수정");
		    
		    /**
			 * 3. 야드 맵 확인 및 수정
			 */ 
		    String putPosition = "";
		    jrParam.setField("YD_GP",	yardId);
		    
		    JDTORecordSet jsEmptyloc = commDao.select(jrParam, getEmptyLoc, logId, methodNm, "저장품Table 조회");
		    
		    if (jsEmptyloc.size() > 0 ) 
		    {
		    	putPosition = jsEmptyloc.getRecord(0).getFieldString("LOCATION");
		    }
		    else 
		    {
		    	putPosition = yardId + "A01010101";
		    }
		    
		    jrParam.setField("FROM_ADDR",	"");
		    jrParam.setField("YD_STR_LOC",	putPosition);
		    
		    if(YfConstant.YD_GP_1.equals(yardId))
		    {
			    /*********************************
			     * 산적위치 수정 로직 호출
			     *********************************/
			    EJBConnector ejbConn1 = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			    jrRtn = (JDTORecord)ejbConn1.trx("changeCoilLocationInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		    }

		    commUtils.printLog(logId, methodNm, "S-");
		
		}
		catch(DAOException daoe)
		{
			throw daoe;
		}
		catch(Exception e)
		{
			throw new EJBServiceException(e);
		}
		
		return jrRtn;
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
	 *      [A] 오퍼레이션명 : 대차스케줄 하차완료 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchUdCmpl(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "대차스케줄 하차완료 처리[YfComm.trtTcarSchUdCmpl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			//수신 항목 값
			String ydEqpId     		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); 		//야드설비ID(대차)
			String ydTcarSchId 		= commUtils.trim(rcvMsg.getFieldString("YD_TCAR_SCH_ID")); 		//야드대차스케쥴ID
			String CraneId	   		= commUtils.trim(rcvMsg.getFieldString("CRANE_ID"));       		//크레인 id
			String ydCarUdStopLoc	= commUtils.trim(rcvMsg.getFieldString("YD_CARUD_STOP_LOC"));	//하차완료위치
			
			if ("".equals(ydEqpId)) 
			{
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차 하차스케쥴 정보 조회
			**********************************************************/
			if ("".equals(ydTcarSchId)) 
			{
				//대차하차스케쥴 조회
				JDTORecordSet jsChk = commDao.select(jrParam, getTcarSchUdCmpl, logId, methodNm, "대차하차스케쥴 조회");
				
				if (jsChk != null && jsChk.size() > 0) 
				{
					JDTORecord jrChk = jsChk.getRecord(0);
					ydTcarSchId = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
				}
				else 
				{
					return jrRtn;
			    }
			}
			
			
			/**********************************************************
			* 2. 대차스케줄 삭제
			**********************************************************/  
			jrParam.setField("YD_TCAR_SCH_ID"	, ydTcarSchId);	//야드대차스케쥴ID
			jrParam.setField("CRANE_ID"			, CraneId);			//크레인 id
			jrParam.setField("YD_CARUD_STOP_LOC", ydCarUdStopLoc);	//하차완료위치			
			commDao.update(jrParam, updTcarSchDelSch, logId, methodNm, "대차스케줄 삭제");
			
			/**********************************************************
			* 3. 대차이송재료 삭제
			**********************************************************/
			commDao.update(jrParam, updTcarSchDelMtl, logId, methodNm, "대차이송재료 삭제");
			
			/**********************************************************
			* 4. 공대차출발지시 처리
			**********************************************************/
			jrRtn = commUtils.addSndData(jrRtn, this.trtTcarSchLevWo(rcvMsg));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} 
		catch(DAOException e) 
		{
			throw e;
		}
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
			
			int ins_cnt = commDao.insert(recInTemp, insWrkBook2, logId, methodNm, "TB_YF_WRKBOOK");
			
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
				recInTemp.setField("CARD_NO",			YdCardNo);
				recInTemp.setField("YD_AIM_YD_GP",		ydStackColGp.substring(0,1)); //야드구분;
				recInTemp.setField("YD_AIM_BAY_GP",		ydStackColGp.substring(1,2)); //야드동구분;
				commDao.insert(recInTemp, insWrkBook2, logId, methodNm, "TB_YF_WRKBOOK");
	    		
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
	
}
