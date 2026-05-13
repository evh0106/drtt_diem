/**
 *                   SbParseAndValidateTC
 * @version          Ver 1.0
 * @author           장병곤
 * @date             2013/06/01
 * @description      외부 수신 전문 유효성 클래스
 *
 * ------------------------------------------------------------------------------
 * Ver.    수정일자          요청자   수정자         내용
 * =====  ===========  ======  ======  ==================================================
 *
 *
 *
*/
package com.inisteel.cim.yf.acoilBak;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.sb.common.util.CmnUtil;
import com.inisteel.cim.yf.common.dao.YfCommDAO;

public class YfParseAndValidateTC 
{
	private static Logger logger = new Logger("yf");
	private YfCommDAO dao = new YfCommDAO();	
	
	public JDTORecord validateTC(String type, JDTORecord inRecord) throws DAOException 
	{
		// 수신 TC 항목 개수 및 길이 확인하기
		JDTORecord 		parsedJR 	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	tcLayOutJRS = JDTORecordFactory.getInstance().createRecordSet("tcLayOutJRS");

		// STATE_MSG msg의 초기값
		// parsedJR.setField("STATE_MSG", "SUCCESS");
		String msg_id = null;
		
		try 
		{
	        // 수신TC의 MSG_ID 확인
			/*
			if("SMS".equals(type)) 
			{
				msg_id = CmnUtil.nvl(inRecord.getField("Telegram_Id"), "EMPTY");
			} 
			else if("DM".equals(type)) 
			{ 
				//제품출하
				msg_id = CmnUtil.nvl(inRecord.getField("TC_CODE"), "EMPTY");
			} 
			else 
			{
				msg_id = CmnUtil.nvl(inRecord.getField("MSG_ID"), "EMPTY");
			}
			*/
			msg_id = CmnUtil.nvl(inRecord.getField("Telegram_Id"), "EMPTY");
			if("EMPTY".equals(msg_id))
			{
				msg_id = CmnUtil.nvl(inRecord.getField("TC_CODE"), "EMPTY");
			}
			if("EMPTY".equals(msg_id))
			{
				msg_id =  CmnUtil.nvl(inRecord.getField("MSG_ID"), "EMPTY");
			}
			if("EMPTY".equals(msg_id))
            {
                msg_id =  CmnUtil.nvl(inRecord.getField("JMS_TC_CD"), "EMPTY");
            }
			if("EMPTY".equals(msg_id))
            {
                msg_id =  CmnUtil.nvl(inRecord.getField("TcCode"), "EMPTY");
            }
			if("EMPTY".equals(msg_id))
            {
                msg_id =  CmnUtil.nvl(inRecord.getField("tcCode"), "EMPTY");
            }

			logger.println(LogLevel.DEBUG, "▩▩▩▩▩▩▩▩[TC 코드 : " + msg_id + " ]  VALIDATE  START  ▩▩▩▩▩▩▩▩");
			
			// 1. msg_id 가 NULL인지 검사
			if("EMPTY".equals(msg_id))
			{
				parsedJR.setField("STATE_MSG", "EMPTY");
				return parsedJR;
			}
			
			// 2. TB_SB_Z_IFLAYOUT에 등록된 TC인지 검사
			if(!checkExistTcId(msg_id))
			{
				parsedJR.setField("STATE_MSG", "NOT_EXIST");
				return parsedJR;
			}
				
			logger.println(LogLevel.DEBUG, "[전송된 TC의 MSG_ID] : "+msg_id);
			logger.println(LogLevel.DEBUG, "[전송된 TC의 항목수] : "+inRecord.size());
			
			// 3. 수신 TC에서 jsp 관련 필드를 제거
			int tcLength 		  = 0;
			int inTcLength 		  = 0;
			int tcCount 		  = 0;
			int inTcCount 		  = 0;
			int checkCount		  = 0;
			int tempCount		  = 0;
			String flag 	      = "";
			String tempString 	  = "";
			String[] checkParam = { "userid", "forward_url", "class_jndi_nm", "class_method_nm", "fail_url", "GROUP_ID", "MODIFIER", "REGISTER", "ROLE_ID", "TYPE", "TC_VALUES", "TCCODE"};

			Iterator it = inRecord.iterateName();
			
			while(it.hasNext())
			{
				tempString = (String) it.next();
				tempCount = 0 ;
				
				for(int i=0 ; i<checkParam.length ; i++)
				{
					if(!tempString.equals(checkParam[i]))
					{
						tempCount = tempCount + 1 ;
					}
					
					if(tempCount == 12)
					{
						parsedJR.setField(tempString, inRecord.getFieldString(tempString));
					}
				}
			}

			logger.println(LogLevel.DEBUG, "[JSP 제거 한 후 항목 수] : " + parsedJR.size());
			tcLayOutJRS = getTcLayoutInfo(msg_id);
			
			// 4. 필수항목 누락 여부 검사
			String strEss = chkEssYn(tcLayOutJRS,parsedJR);
			
			if(!"".equals(strEss))
			{
				parsedJR.setField("STATE_MSG", strEss);
				return parsedJR;
			}
			
			// 5. 기준 TC 항목수 와 수신 TC 항목수 일치하는지 검사
			tcCount = tcLayOutJRS.size(); // 기준 TC 항목수(TB_SB_Z_IFLAYOUT)
			inTcCount = parsedJR.size(); // 수신 TC 항목수(전문)
			
			checkCount = tcCount - inTcCount ;

			if(checkCount > 0)
			{
			    logger.println(LogLevel.DEBUG, "[ checkCount > 0 ] : " + checkCount + " 건");
				flag = "SMALL";

				// 누락된 항목 정보를 String으로 취합
				makeErrorItemList(parsedJR, tcLayOutJRS, flag, checkCount);
				parsedJR.setField("STATE_MSG", "SMALL");
				return parsedJR;
			} 
			else if (checkCount < 0)
			{
				flag = "BIG";
				logger.println(LogLevel.DEBUG, "[ checkCount < 0 ] : " + checkCount + " 건");

				// 추가된 항목 정보를 String으로 취합
				makeErrorItemList(parsedJR, tcLayOutJRS, flag, checkCount);
				parsedJR.setField("STATE_MSG", "BIG");
				return parsedJR;
			} 
			else if (checkCount == 0)
			{
				logger.println(LogLevel.DEBUG, "[수신 TC 항목수 정상] : "+tcCount+"건");
			}
			
			// 7. 각 항목별 항목값 Min/Max Check
			boolean isValid = checkValuesOneByOne(parsedJR, tcLayOutJRS);
			
			if(!isValid) 
			{
				parsedJR.setField("STATE_MSG", "VASLUES");
				return parsedJR;
			}
			
			logger.println(LogLevel.DEBUG, "[수신 TC Length 정상] : " + inTcLength + "byte");
			parsedJR.setField("STATE_MSG", "SUCCESS");
		} 
		catch (Exception e) 
		{
	 		logger.println(LogLevel.ERROR, "validateTC   Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}//try
		
		logger.println(LogLevel.DEBUG, "▩▩▩▩▩▩▩▩[TC 코드 : " + msg_id + " ] VALIDATE  FINISH ▩▩▩▩▩▩▩▩");
		return parsedJR;
	}
	
	public String chkEssYn(JDTORecordSet layout, JDTORecord tc)
	{
		String str = "";
		
		try
		{
			JDTORecord rec = JDTORecordFactory.getInstance().create();
			
			int size  = layout.size();
			
			for(int i =0; i<size; i++)
			{
				rec = layout.getRecord(i);
				
				if("Y".equals(rec.getFieldString("ESS_YN")))
				{
					logger.println(LogLevel.DEBUG, "[필수항목 발견] " + rec.getFieldString("ITM_ID"));
					
					if("".equals(CmnUtil.trim(tc.getFieldString(rec.getFieldString("ITM_ID")))))
					{
						logger.println(LogLevel.DEBUG, "[필수항목 누락] : " + rec.getFieldString("ITM_ID") + ":" + CmnUtil.trim(tc.getFieldString(rec.getFieldString("ITM_ID"))));
						str = "필수항목 ["+rec.getFieldString("ITM_ID") + "]이 누락되었습니다 . -> ["+ CmnUtil.trim(tc.getFieldString(rec.getFieldString("ITM_ID")))+"]";
						
						return str;
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.println(LogLevel.ERROR, "chkEssYn   Exception Error : "+e);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		return str;
	}
	
	
	/**
	 * parseOutRcvTC
	 */
	public JDTORecord parseOutRcvTC(JDTORecord parsedJR) throws DAOException 
	{
		String itmId		= "" ;
		String itmSeq		= "" ;
		String itmDataTp	= "" ;
		String itmDataL		= "" ;
		String itmDataDot	= "" ;
		String itmNm		= "" ;
		String tempData 	= "" ;
		String intData		= "" ;
		String layOutInfo	= "" ;

		String  MSG_ID = "EMPTY";
		JDTORecordSet 	tcLayOutJRS = JDTORecordFactory.getInstance().createRecordSet("tcLayOutJRS");
		JDTORecord 	validateJR = JDTORecordFactory.getInstance().create();

		try
		{
			// 수신TC의 MSG_ID 확인
			if("EMPTY".equals(MSG_ID))
			{
				MSG_ID = CmnUtil.nvl(parsedJR.getField("TC_CODE"), "EMPTY");
			}
			if("EMPTY".equals(MSG_ID))
			{
				MSG_ID =  CmnUtil.nvl(parsedJR.getField("MSG_ID"), "EMPTY");
			}
			if("EMPTY".equals(MSG_ID))
            {
                MSG_ID =  CmnUtil.nvl(parsedJR.getField("JMS_TC_CD"), "EMPTY");
            }
			if("EMPTY".equals(MSG_ID))
            {
                MSG_ID =  CmnUtil.nvl(parsedJR.getField("TcCode"), "EMPTY");
            }
			if("EMPTY".equals(MSG_ID))
            {
                MSG_ID =  CmnUtil.nvl(parsedJR.getField("tcCode"), "EMPTY");
            }
			
			logger.println(LogLevel.DEBUG, "▩▩▩▩▩▩▩▩[TC 코드 : " + MSG_ID + " ] PARSING 시작 ▩▩▩▩▩▩▩▩");
			// 수신TC 기준 항목  검색
			tcLayOutJRS = getTcLayoutInfo(MSG_ID);

			JDTORecord tempTcJR = JDTORecordFactory.getInstance().create();

			for(int i = 0; i < tcLayOutJRS.size(); i++) 
			{
				tempTcJR = tcLayOutJRS.getRecord(i);

				itmId		= tempTcJR.getFieldString("ITM_ID");
				itmSeq		= tempTcJR.getFieldString("ITM_SEQ");
				itmDataTp	= tempTcJR.getFieldString("ITM_DATA_TP");
				itmDataL	= tempTcJR.getFieldString("ITM_DATA_L");
				itmDataDot	= tempTcJR.getFieldString("ITM_DATA_DOT");
				itmNm		= tempTcJR.getFieldString("ITM_NM");

				tempData 	= CmnUtil.trim(parsedJR.getFieldString(itmId));

 				if ("C".equals(itmDataTp)||"D".equals(itmDataTp)) 
 				{
					//문자일경우
					validateJR.setField(itmId, tempData);
				} 
 				else if("N".equals(itmDataTp))
 				{
					//숫자일경우

					//소수점 표현을 위한 layOut
					layOutInfo = itmDataL + "," + itmDataDot ;

					if( "".equals(tempData) ||  "0".equals(trimZero(tempData)) ) 
					{
						logger.println(LogLevel.DEBUG, itmNm+ ":  tempData : " +tempData);
						intData = "0" ;
					}
					else
					{
						intData = trimZero(setEaiRcvMessage(tempData, itmDataDot));
						logger.println(LogLevel.DEBUG, itmNm+ ":  intData : " +intData);
					}
					
					validateJR.setField(itmId, intData);
				} 
 				else 
 				{
					logger.println(LogLevel.ERROR, "[숫자도 문자도 아님............................]");
					validateJR.setField("STATE_MSG", "NCNN");
					return validateJR;
				}//if
 				
 				logger.println(LogLevel.DEBUG, "⊙ 순번:"+ itmSeq + " ["+itmId+ " 포맷 정보 ]  NAME:" + itmNm + ", TYPE:" + itmDataTp + ", LENGTH:" + itmDataL + ", [전:" + parsedJR.getFieldString(itmId) + "], ==> [후:"+validateJR.getFieldString(itmId)+ "]");
			} //for

			validateJR.setField("STATE_MSG", "SUCCESS");
			logger.println(LogLevel.DEBUG, "▩▩▩▩▩▩▩▩[TC 코드 : " + MSG_ID + " ] PARSING 종료 ▩▩▩▩▩▩▩▩");
		} 
		catch (Exception e) 
		{
	 		logger.println(LogLevel.ERROR, "parseOutRcvTC   Exception Error : "+e);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}//try
		
		return validateJR;
	}

	/**
	 * 수신 TC의 기준항목 검색
	 * 전문 항목중 숫자로 올라 오면서 소수점이 붙어 오는 전문 편집처리
	 * SMS 에서는 전문 항옥에 소수점이 붙어 옴  2009.12.01 작성
	 * #{@link #checkExistTcId(String)}  12.2345678   [6.3] ==>  12.234 로 편집
	 *
	 */
	public String setEaiRcvMessage(String tempData, String itmDataDot) throws DAOException 
	{
		String   rtempData = null;
		
		try 
		{
			// layout 정보에  소수점이 없을때
			if( "".equals(CmnUtil.nvl(itmDataDot, "")) ) 
			{   
				rtempData = tempData;
			} 
			// layout에 정보에  소수점이 있다면
			else 
			{
				//소수점만큼 10을 나눔
				tempData = trimZero(tempData);
				//System.out.println("1111111111111111 :"+tempData);
				String belowDot = CmnUtil.sqrtTen(Integer.parseInt(itmDataDot));
				BigDecimal decmalVal = new BigDecimal(tempData);
				rtempData = String.valueOf(decmalVal.divide(new BigDecimal(belowDot), Integer.parseInt(itmDataDot), BigDecimal.ROUND_UP));
				//System.out.println("2222222222222222 :"+rtempData);
			}
		} 
		catch (Exception e) 
		{
	 		logger.println(LogLevel.ERROR, "setEaiRcvMessage   Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}
		
		return rtempData;
	}
	
	/**
	 * 수신 TC의 기준항목 검색
	 * 1. TB_HR_Z_IFLAYOUT 에 전문 Key(MSG_ID, Telegram_Id) 를 이용 하여 등록 되어 있는 전문 항목
	 *    정보를 JDTORecordSet 에 담아 Return 한다.
	 */
	public JDTORecordSet getTcLayoutInfo(String MSG_ID) throws DAOException 
	{
		ArrayList getTcLayoutAL = new ArrayList();
		JDTORecordSet tcLayoutJRS = null;
		
		try 
		{
			getTcLayoutAL.add(MSG_ID);

			// TC_ID를 바인딩변수로 하여 TC의 항목을 검색한다.
			tcLayoutJRS = dao.getTcRS(getTcLayoutAL);
		} 
		catch (Exception e) 
		{
	 		logger.println(LogLevel.ERROR, "getTcLayoutInfo   Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}
		
		return tcLayoutJRS;
	}

	/**
	 * 수신 TC의 TC_ID 존재 여부 확인
	 * 1. TB_SB_Z_IFLAYOUT 에 전문 Key(MSG_ID, Telegram_Id) 를 이용 하여 등록 되어 있는 전문인지를
	 *    Check 하여 등록 되어 있으면 true, 없으면 false 를 Return 한더.
	 */
	public boolean checkExistTcId(String MSG_ID)  throws DAOException 
	{
		boolean exist = false;
		
		ArrayList getTcLayoutAL = new ArrayList();
		JDTORecordSet tcLayoutJRS = null;
	
		try 
		{
			getTcLayoutAL.add(MSG_ID);

			// TC_ID를 바인딩변수로 하여 TC의 항목을 검색한다.
			tcLayoutJRS = dao.getTcRS(getTcLayoutAL);

			if(tcLayoutJRS.size() != 0) 
			{
				exist = true;
			}
		} 
		catch (Exception e) 
		{
	 		logger.println(LogLevel.ERROR, "checkExistTcId   Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}
		
		return exist;
	}

	/**
	 * 수신 TC를 처리할 퍼사드JNDI, 메소드 이름 검색
	 * 1. TB_HR_Z_IF TABLE에서 EJB class 명과 수행할 매소그 명을 가져온다.
	 * 2. Return : JDTORecord
	 */
	public JDTORecord getEjbInfo(String MSG_ID) throws DAOException 
	{	
		ArrayList ejbInfoAL = new ArrayList();
		JDTORecordSet ejbInfoJRS = null;
		JDTORecord ejbInfoJR = null;

		int ejbInfoSize = 0;

		try
		{
			ejbInfoAL.add(MSG_ID);
			ejbInfoJRS = dao.getTcRS(ejbInfoAL);
			ejbInfoSize = ejbInfoJRS.size();
		
			if(ejbInfoSize == 1 )
			{
				ejbInfoJR = ejbInfoJRS.getRecord(0);
			}
			
	 		logger.println(LogLevel.INFO, this, "[ejbInfoSize]"+ejbInfoSize);
			String ejbInfoCount = new DecimalFormat("0").format(ejbInfoSize);
	 		logger.println(LogLevel.INFO, this, "[ejbInfoCount]"+ejbInfoCount);
			ejbInfoJR.setField("ejbInfoCount", ejbInfoCount);
		} 
		catch (Exception e) 
		{
	 		logger.println(LogLevel.ERROR, "getEjbInfo   Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}
		
		return ejbInfoJR;
	}

	/**
	 * 누락/추가된 항목 리스트 검색 및 String으로 취합
	 * 1. 누락/추가된 항목 정보를 편집 처리 한다.
	 * 2. Prameter :  수신한 전문, 기준전문, Check Type, Check수)
	 *     (JDTORecord inTempJR, JDTORecordSet tcLayOutJRS, String flag, int checkCount )
	 * 3. Return : void
	 */
	public void makeErrorItemList(JDTORecord inTempJR, JDTORecordSet tcLayOutJRS, String flag, int checkCount) throws DAOException 
	{
		int tcLayOutSize = tcLayOutJRS.size();
		int tempCount = 1;
		String itmId		 = "";
		String itmNm		 = "";

		String errorItemList = "";
		String tempItemId 	 = "";
		String tempItem 	 = "";
		StringBuffer sb 	 = new StringBuffer();
		JDTORecord tempTcJR  = JDTORecordFactory.getInstance().create();

		try
		{
			logger.println(LogLevel.ERROR, "[ flag : >>>> ] " + flag);
			
			if ("SMALL".equals(flag)) 
			{
				logger.println(LogLevel.ERROR, "[수신 TC에 누락된 항목이 "+checkCount+"건 존재합니다.]");
				logger.println(LogLevel.ERROR, "[누락된 항목 목록]");
				
				for(int i=0 ; i<tcLayOutSize ; i++)
				{
					tempTcJR = tcLayOutJRS.getRecord(i);
					itmId		= tempTcJR.getFieldString("ITM_ID");
					itmNm		= tempTcJR.getFieldString("ITM_NM");
					tempItemId = inTempJR.getFieldString(itmId);

					logger.println(LogLevel.DEBUG, "[ "+tempCount +". 전문 항목명 : " + itmNm + "(" + itmId + ")");

					if(tempItemId == null)
					{
						logger.println(LogLevel.DEBUG, "[ "+tempCount +". 항목명 : " + itmNm + "(" + itmId + ")");
						sb.append(itmId+"&");
						tempCount= tempCount + 1 ;
					}
				}
				
				errorItemList = sb.toString();
				logger.println(LogLevel.ERROR, "[ TEST 수신 TC에 누락된 항목 스트링 ] " + errorItemList);
				logger.println(LogLevel.ERROR, "[ 수신된 RECORD ] " + inTempJR);
				logger.println(LogLevel.ERROR, "[ 수신된 RECORD ] " + inTempJR.toString());
			}
			else if("BIG".equals(flag))
			{
				logger.println(LogLevel.ERROR, "[수신 TC에 추가된 항목이 "+checkCount*(-1)+"건 존재합니다.]");
				logger.println(LogLevel.ERROR, "[추가된 항목]");
				
				Iterator it = inTempJR.iterateName();
				
				while (it.hasNext())
				{
					tempItemId = (String) it.next();
					
					for(int i=0 ; i<tcLayOutSize ; i++)
					{
						tempTcJR = tcLayOutJRS.getRecord(i);

						itmId = tempTcJR.getFieldString("ITM_ID");
						itmNm = tempTcJR.getFieldString("ITM_NM");
						tempItem = inTempJR.getFieldString(itmId);
						
						if(tempItem == null)
						{
							logger.println(LogLevel.DEBUG, "[ "+ tempCount +". 항목명 : " + itmId);
							sb.append(itmId+"&");
							tempCount= tempCount + 1 ;
						}//if
					}//for
				}//while
				
				errorItemList = sb.toString();
				logger.println(LogLevel.ERROR, "[ TEST 수신 TC에 추가된 항목 스트링 ] " + errorItemList);
				logger.println(LogLevel.ERROR, "[ 수신된 RECORD ] " + inTempJR);
				logger.println(LogLevel.ERROR, "[ 수신된 RECORD ] " + inTempJR.toString());
				throw new DAOException(getClass().getName() +  " 수신받은 항목과 인터페이스레이아웃 에 등록된 항목 이 상이 합니다. " );
			}//else
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "makeErrorItemList Exception Error");
			String Messaage = "  [ "+ tempCount+1 +"번째  항목명 : " + itmId + "/" + itmNm + " 항목명이 수신 전문에 없습니다.";
			throw new DAOException(getClass().getName() + e.getMessage() + Messaage );
		}
	}

	/**
	 * 수신TC와 기준TC 항목의 길이를 하나씩 비교
	 * 1. 수신한 전문의 항목 길이와 기준Table
	 *    (TB_HR_Z_IFLAYOUT) 에 등록한 전문 기준길이를 비교하여
	 *    결과 정보를 호출한 매소드로 Retuen
	 * 2. Prameter :  수신한 전문, 기준전문 (JDTORecord inTempJR, JDTORecordSet tcLayOutJRS )
	 * 3. Return  boolean
	 */
	public boolean checkLengthOneByOne(JDTORecord inTempJR, JDTORecordSet tcLayOutJRS) throws DAOException 
	{
		int layOutSize = tcLayOutJRS.size();
		int tempLength = 0;
		int tempInt = 0;
		String itmId		= "";
		int itmDataL		= 0;
		String itmNm		= "";

		String errorItemList = "";
		String tempData 	= "" ;
		StringBuffer sb 	 = new StringBuffer() ;
		JDTORecord tempTcR = null;
		boolean isValid = false ;
		try 
		{
			for (int i = 0; i < layOutSize; i++) 
			{
				tempTcR = tcLayOutJRS.getRecord(i);
				itmId		= tempTcR.getFieldString("ITM_ID");
				itmDataL	= Integer.parseInt(tempTcR.getFieldString("ITM_DATA_L"));
				itmNm		= tempTcR.getFieldString("ITM_NM");
				tempData = inTempJR.getFieldString(itmId);
				// logger.println(LogLevel.DEBUG, "checkLengthOneByOne : [" + itmId + "/" + itmNm + "(" + itmDataL + ")" + "=>" + tempData + "]");
				tempLength = tempData.length();
 				
				if( !"".equals(CmnUtil.trim(tempData)))
 				{
					if(tempLength != itmDataL)
					{
 						errorItemList = "[항목명 : " + itmId + " ( " + itmNm + " )";
						errorItemList = errorItemList +  "(기준TC길이 :" + itmDataL + "byte)";
						errorItemList = errorItemList +  "(수신 TC 길이 : " + tempLength + "byte) (값 : "+tempData +")";
						sb.append(errorItemList + "  #  ");
						tempInt = tempInt + 1;
					}
				} 
				else 
				{
					errorItemList = "[항목명 : " + itmId + " ( " + itmNm + " )";
					errorItemList = errorItemList +  "(기준TC길이 :" + itmDataL + "byte)";
					errorItemList = errorItemList +  "(수신 TC 길이 : " + tempLength + "byte) (값 : "+tempData +")";
					sb.append(errorItemList + "  #  ");
					tempInt = tempInt + 1;
				}
			}//for
			
			if(tempInt == 0)
			{
				logger.println(LogLevel.DEBUG, "[모든 항목의 길이가 같음]");
				isValid = true ;
			} 
			else
			{
				logger.println(LogLevel.ERROR, "[수신된 TC에서 다음과 같이 길이가 다른 항목 발견 됨] : "+ sb.toString());
			}
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "checkLengthOneByOne Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}
		
		return isValid ;
	}

	/**
	 * 항목의 MIN값 MAX값 Check
	 * 1. 수신한 전문의 항목 의 값과  기준Table
	 *    (TB_HR_Z_IFLAYOUT) 에 등록한 전문 MIN/MAX를 비교하여
	 *    결과 정보를 호출한 매소드로 Retuen
	 * 2. Prameter :  수신한 전문, 기준전문 (JDTORecord inTempJR, JDTORecordSet tcLayOutJRS )
	 * 3. Return  boolean
	 */
	public boolean checkValuesOneByOne(JDTORecord inTempJR, JDTORecordSet tcLayOutJRS) throws DAOException 
	{
		logger.println(LogLevel.DEBUG, "@@@@@@@@@@@@@@@@@@@  checkValuesOneByOne : START  " );
		
		int layOutSize = tcLayOutJRS.size();
		int tempvalue = 0;
		int tempInt = 0;
		String itmId		= "";
		String itmNm		= "";
		int    minval		= 0;
		int    maxval		= 0;

		float  fminval		= 0;
		float  fmaxval		= 0;
		float  ftempvalue   = 0;

		String    cminval		= null;
		String    cmaxval		= null;

		String errorItemList = "";
		String tempData 	 = "" ;
		StringBuffer sb 	 = new StringBuffer() ;
		JDTORecord tempTcR   = null;
		boolean isValid      = false ;
		
		try 
		{
			for (int i = 0; i < layOutSize; i++) 
			{
				tempTcR = tcLayOutJRS.getRecord(i);
				itmId		= tempTcR.getFieldString("ITM_ID");
				itmNm		= tempTcR.getFieldString("ITM_NM");

				if("N".equals(tempTcR.getFieldString("ITM_DATA_TP"))) 
				{
					// 숫자 이면서 소수점이  없을때
					if("0".equals(tempTcR.getFieldString("ITM_DATA_DOT"))) 
					{
						minval		= Integer.parseInt(tempTcR.getFieldString("MIN_VAL"));
						maxval		= Integer.parseInt(tempTcR.getFieldString("MAX_VAL"));
						// MIN/MAX 값이 입력 되어 있는 항목만 Check 한다.
						if(minval != 0 && maxval !=0) 
						{
							 tempData  = inTempJR.getFieldString(itmId);
						 	 tempvalue = Integer.parseInt(tempData);
						 	 
						 	 if(!(tempvalue >=  minval  && tempvalue <= maxval) ) 
						 	 {
						 		 errorItemList =  " [ " + itmId + " (" + itmNm + ") " + " 전문값 :(" + tempvalue + ") 기준값 :{" + "Min:(" + minval + ")" + " Max:(" + maxval + ") ] ";
						 		 tempInt = tempInt + 1;
						 		 sb.append(errorItemList + " # ");
						 	 }
						}
					} 
					else 
					{
                        //	  숫자 이면서 소수점 이  있을때
						fminval		= Float.parseFloat(tempTcR.getFieldString("MIN_VAL"));
						fmaxval		= Float.parseFloat(tempTcR.getFieldString("MAX_VAL"));
						// MIN/MAX 값이 입력 되어 있는 항목만 Check 한다.
						
						if(fminval != 0 && fmaxval !=0) 
						{
							tempData  = inTempJR.getFieldString(itmId);
							ftempvalue = Integer.parseInt(tempData);
							
							if(!(ftempvalue >=  fminval  && ftempvalue <= fminval) ) 
							{
								errorItemList =  " [ " + itmId + " (" + itmNm + ") " + " 전문값 :(" + ftempvalue + ") 기준값 :{" + "Min:(" + fminval + ")" + " Max:(" + fmaxval + ") ] ";
								tempInt = tempInt + 1;
								sb.append(errorItemList + " # ");
							}
						}
					}
				// 문자 일때
			    } 
				else 
				{
					cminval		= tempTcR.getFieldString("ITM_VAL1");
					cmaxval		= tempTcR.getFieldString("ITM_VAL2");
					// MIN/MAX 값이 입력 되어 있는 항목만 Check 한다.
					if(!(cminval == null || "".equals(cminval)) && !(cmaxval == null  || "".equals(cminval)) ) 
					{
						tempData  = inTempJR.getFieldString(itmId);
						//작을 는 음수 반환, 같으면 0 반환, 크면 양수 반환
						if((tempData.compareTo(cminval) <= 0 )  || (tempData.compareTo(cmaxval) >= 0) ) 
						{
							errorItemList =  " [ " + itmId + " (" + itmNm + ") " + " 전문값 :(" + tempData + ") 기준값 :{" + "Min:(" + cminval + ")" + " Max:(" + cmaxval + ") ] ";
							tempInt = tempInt + 1;
							sb.append(errorItemList + " # ");
						}
					}
				}
			}//for
			
			if(tempInt == 0)
			{
				logger.println(LogLevel.DEBUG, "[ checkValuesOneByOne 항목의 MIN값 MAX값 Check  Check 정상 ]");
				isValid = true ;
			} 
			else
			{
				logger.println(LogLevel.ERROR, "[ checkValuesOneByOne 항목의 MIN값 MAX값 Check  Check Error ]" + sb.toString());
				throw new DAOException(getClass().getName() + "[ checkValuesOneByOne 항목의 MIN값 MAX값 Check  Check Error ]" + sb.toString());
			}
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "checkValuesOneByOne Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}
		
		return isValid ;
	}

	/**
	 * 숫자 자리 모자를때 0추가 해주는 함수
	 * 2009-02-05
	 */
	public String getTempZero(int zeroLength) throws DAOException 
	{
		String tempZero = "";
		
		for (int j = 0; j < zeroLength; j++) 
		{
			tempZero = tempZero + "0";
		}
		
		return tempZero;
	}

	/**
	 * 문자 앞에 0 제거하기
	 * 2009-02-05
	 */
	public String trimZero(String str) throws DAOException 
	{
		try 
		{
			if("".equals(str.replaceAll("0", "")))
			{
				return "0";
			}
			
			for (int i = 0; i < str.length(); i++) 
			{
				if (!(str.substring(i, i + 1).indexOf("0") > -1)) 
				{
					return str.substring(i);
				}
			}
		} 
		catch (IndexOutOfBoundsException e) 
		{
			str = "";
		}
		
		return str;
	}

	//L2송신을 위해 수신한  JDTORecord 정보를 String으로 만든다
	public String makeSendString(JDTORecord inRecord, String TC_CODE) throws DAOException 
	{
		StringBuffer sendMsg 	 = new StringBuffer() ;
		String tempData = "";
		JDTORecordSet sendTcLayoutRS = null;
		sendTcLayoutRS = getTcLayoutInfo(TC_CODE);

		try 
		{
			inRecord.setField("MSG_ID", TC_CODE);
			
			for (int i = 0; i < sendTcLayoutRS.size(); i++) 
			{
				JDTORecord tempTcR = null ;

				tempTcR = sendTcLayoutRS.getRecord(i);

				String itmId		= tempTcR.getFieldString("ITM_ID");
				String itmDataTp	= tempTcR.getFieldString("ITM_DATA_TP");
				String itmDataL		= tempTcR.getFieldString("ITM_DATA_L");

				tempData = inRecord.getFieldString(itmId);

				sendMsg = sendMsg.append(getStringData(tempData, itmDataTp, itmDataL));
			} //for
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "makeSendString Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}//try
		
		return sendMsg.toString();
	}

	//인터페이스 레이아웃 정보에 맞게 data 변환
	public String getStringData(String tempData, String itmDataTp, String itmDataL)  throws DAOException 
	{
		String tempStringData = "";
		
		try 
		{
			if ("C".equals(itmDataTp)) 
			{
				tempStringData = getSpase(tempData, itmDataL);
			} 
			else if("N".equals(itmDataTp))
			{
				tempStringData = getZero(tempData, itmDataL);
			} 
			else 
			{
				logger.println(LogLevel.DEBUG, "[변환을 위한 조건에 만족하지 않음. C도 N도 아님]");
				//무조건 스패이스 채운다
				tempStringData = getSpase(tempData, itmDataL);
			}
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "getStringData Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}//try
		
		return tempStringData;
	}

	// char 타입에 space 채우기
	public String getSpase(String tempData, String itmDataL)   throws DAOException 
	{
		StringBuffer sb 	 = new StringBuffer() ;
		
		try 
		{
			int tempDataLength = tempData.length();
			int aa = Integer.parseInt(itmDataL) - tempDataLength;
			sb.append(tempData);
			//logger.println(LogLevel.DEBUG, "[C 변환전 tempData] : " + tempData + ", [Size] : " + tempDataLength);

			for (int i = 0; i < aa; i++) 
			{
				sb.append(" ");
			}

			//logger.println(LogLevel.DEBUG, "[C 변환후 tempData] : " + sb.toString() + ", [Size] : " + sb.length());
			//logger.println(LogLevel.DEBUG, "---------------------------------------------------------");
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "getSpase Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}//try
		
		return sb.toString();
	}

	// number 타입에 0 채우기
	public String getZero(String tempData, String itmDataL) throws DAOException 
	{
		String tempNumber = tempData;
		//tempNumber = trimDot(tempData);
		try 
		{
			int tempDataLength = tempNumber.length();
			int aa = Integer.parseInt(itmDataL) - tempDataLength;
			//logger.println(LogLevel.DEBUG, "[N 변환전 tempData] : " + tempData + ", [Size] : "+tempDataLength);

			for (int i = 0; i < aa; i++) 
			{
				tempNumber = "0" + tempNumber;
			}
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "getZero Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}//try
		//logger.println(LogLevel.DEBUG, "[N 변환후 tempData] : " + tempNumber + ", [Size] : "+tempNumber.length());
		//logger.println(LogLevel.DEBUG, "---------------------------------------------------------");
		return tempNumber;
	}

	//소수점 제거 하기
	public String trimDot(String tempData) throws DAOException 
	{
		StringBuffer sb = new StringBuffer() ;
		String tempBuffer = "";
		
		try 
		{
			for(int i=0 ; i<tempData.length() ; i++)
			{
				tempBuffer = tempData.substring(i, i+1);
			
				if(!".".equals(tempBuffer))
				{
					sb.append(tempBuffer);
				}
			}
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "trimDot Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}//try

		return sb.toString();
	}

	/*
	 * 화면에서 받은 정보를 DB에 등록되어있는 수신전문  Layout 형태로
	 * 1. DB에 등록된 LayOut 정보를 수집한다.
	 * 2. 수집한 정보롸 화면에서 받은 JDTORecord 를 이용 하여 전문Layout Format 형태로
	 *    JDTORecord를 만드는 makeRcvDataInfo를 호출한다.
	 * 3. 호출한 매소드로 전문  전문Layout Format 형태의 JDTORecord 를 방환한다.
	 * 전문 발생 설비 'L2', 'SMS', 화면 JDTORecord
	 */
	public JDTORecord makeRcvJdtoRecord(String Type, JDTORecord inRecord) throws DAOException 
	{
 		String tempData = "";
		JDTORecordSet sendTcLayoutRS = null;
		JDTORecord tempJR   = JDTORecordFactory.getInstance().create();
		JDTORecord tempIJR  = JDTORecordFactory.getInstance().create();
		JDTORecord tempOJR  = JDTORecordFactory.getInstance().create();
		String  MSG_ID = null;
	
		try 
		{
			// 수신TC의 MSG_ID 확인
			if("SMS".equals(Type)) 
			{
				  MSG_ID = CmnUtil.nvl(inRecord.getField("Telegram_Id"), "EMPTY");
			} 
			else 
			{
				MSG_ID = CmnUtil.nvl(inRecord.getField("MSG_ID"), "EMPTY");
			}

			sendTcLayoutRS = getTcLayoutInfo(MSG_ID);
			
			for (int i = 0; i < sendTcLayoutRS.size(); i++) 
			{
				JDTORecord tempTcR = null ;
				tempTcR = sendTcLayoutRS.getRecord(i);
				String itmId		= tempTcR.getFieldString("ITM_ID");
				String itmDataTp	= tempTcR.getFieldString("ITM_DATA_TP");
				String itmDataL		= tempTcR.getFieldString("ITM_DATA_L");
				
				if("C".equals(itmDataTp))
				{
					tempData = " ";
				}
				else 
				{
					tempData = "0";
				}
//				logger.println(LogLevel.DEBUG, "변환 항목 : " + itmId + " : " + tempTcR.getFieldString("ITM_NM"));
				tempJR.setField(itmId,  getStringData(tempData, itmDataTp, itmDataL));
			}

//			logger.println(LogLevel.DEBUG, "===================================================");
			tempIJR =  makeRcvDataInfo(inRecord, tempJR);
			
			for (int i = 0; i < tempIJR.size(); i++) 
			{
				JDTORecord tempTcR = null ;
				tempTcR = sendTcLayoutRS.getRecord(i);
				String itmId		= tempTcR.getFieldString("ITM_ID");
				String itmDataTp	= tempTcR.getFieldString("ITM_DATA_TP");
				String itmDataL		= tempTcR.getFieldString("ITM_DATA_L");

//				logger.println(LogLevel.DEBUG, "변환 항목 : " + itmId);
				tempOJR.setField(itmId,  getStringData(tempIJR.getFieldString(itmId), itmDataTp, itmDataL));
			}
			
			logger.println(LogLevel.DEBUG, "변환 항목 tempOJR 항목수 : " + tempOJR.size());
			return tempOJR;
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "makeRcvJdtoRecord Exception Error");
			throw new DAOException(getClass().getName() + e.getMessage());
		}//try
	}
	
	/*
	 *   화면에서 받은 정보를 DB에 등록되어있는 수신전문  Layout 형태로
	 *   JDTORecord 를 만드는 매소드
	 */
	public JDTORecord makeRcvDataInfo(JDTORecord inDto, JDTORecord rcvDto) throws DAOException 
	{
		JDTORecord tmpDto  = JDTORecordFactory.getInstance().create();
		String  flag = null;
		
		try 
		{
			Object[] inkey     = ((JDTORecordImplMap)inDto).getFieldNames();
			Object[] rcvkey    = ((JDTORecordImplMap)rcvDto).getFieldNames();
			/*
			PrEaiJmsSend.rcvDataInfo(inDto);
			PrEaiJmsSend.rcvDataInfo(rcvDto);
			for(int jj=0;jj<rcvkey.length;jj++)
		  	{

			}
		  
			for(int jj=0;jj<inkey.length;jj++)
			{
				logger.println(LogLevel.DEBUG, "[inkey] : " + (String)inkey[jj]);
			} 
			*/
		  
			for(int jj=0;jj<rcvkey.length;jj++)
			{
				flag = "N";
				String rcv_key = (String)rcvkey[jj];
			  
				for(int ii=0;ii<inkey.length;ii++)
				{
					String in_key = (String)inkey[ii];
				  
					if( rcv_key.equals(in_key) ) 
					{
						String value = inDto.getFieldString(in_key);
						tmpDto.setField(rcv_key,  value);
						flag = "T";
					}
				}
 			  
				if(!"T".equals(flag))
				{
					tmpDto.setField(rcv_key,  rcvDto.getFieldString(rcv_key) );
				}
			}
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.DEBUG, "makeRcvDataInfo Exception Error");
             throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return tmpDto;
	}

	
	
}
