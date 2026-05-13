package com.inisteel.cim.yf.common.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.sb.common.util.CmnUtil;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.YfQueryIF;
import com.inisteel.cim.yf.common.YfQueryIF2;

/**
 *      [A] 클래스명 : 공통DAO 
 *
*/
public class YfCommDAO extends DBAssistantDAO implements YfQueryIF, YfQueryIF2 
{
	private String szSessionName	= getClass().getName();
	private Logger logger			= new Logger("yf");
	private YfCommUtils	commUtils	= new YfCommUtils();
	private CommonDAO commonDao 	= new CommonDAO();
	
	/***************************************************************************
	 * 인터페이스 (EAI, JMS) 수신 공통
	 **************************************************************************/
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 파라미터를 object 배열로 받아 한 레코드만 조회하는 메소드
	 * 
	 *      @param String queryCode
	 *      @param Object [] obj
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getOneRecord(String queryCode, Object [] obj) throws DAOException {
		
		return commonDao.findByPrimaryKey(queryCode, obj);	    
	}
	
	/**
	 *      [A] 오퍼레이션명 : 수신된 전문의 정보를 조회
	 *      
	 *      @param String msgID : 수신된 전문의 MSG_ID
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgInfo(String msgID) throws DAOException {
		try {
			return super.getRecordSet(getMsgInfo, new Object[] { msgID });
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : Lyr 정보 조회
	 *      
	 *      @param String pYdStkColGp : Lyr 정보 조회
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getStkLyrInfo(String pYdStkColGp, String pYdStkBedNo, String pYdStkLyrNo) throws DAOException {
		try {
			return super.getRecordSet(getStkLyr, new Object[] { pYdStkColGp,pYdStkBedNo,pYdStkLyrNo });
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Sequence ID 조회
	 *      
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String trtGp
	 *      @return String
	 *      @throws DAOException
	*/
	public String getSeqId(String logId, String mthdNm, String trtGp) throws DAOException 
	{
		String methodNm = "SeqID조회[YsCommDAO.getSeqId] < " + mthdNm;
		String trtNm = "";

		try 
		{
			String jspeed_query_id = "";
			String seqId = ""; //반환할 Sequence ID
 
			if ("CrnSch".equals(trtGp)) 
			{
				trtNm = "야드크레인스케쥴ID";
				jspeed_query_id = getSeqIdCrnSch;
			}
			else if ("WrkBook".equals(trtGp)) 
			{
				trtNm = "야드작업예약ID";
				jspeed_query_id = getSeqIdWrkBook;
//			}
//			else if ("PrepSch".equals(trtGp)) 
//			{
//				trtNm = "야드준비스케쥴ID";
//				jspeed_query_id = "com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdPrepSch";
			}
			else if ("TcarSch".equals(trtGp)) 
			{
				trtNm = "야드대차스케쥴ID";
				jspeed_query_id = getSeqIdTcarSch;
			}
			else if ("CarSch".equals(trtGp)) 
			{
				trtNm = "야드차량스케쥴ID";
				jspeed_query_id = getSeqIdCarSch;
			}
			else if ("FtMvWo".equals(trtGp))
			{
				trtNm = "이송작업지시번호";
				jspeed_query_id = getFrToMoveWordNo;
			}
			else if ("CtsSch".equals(trtGp))
			{
				trtNm = "CTS스케줄번호";
				jspeed_query_id = getSeqIdCTSSch;
			}
			else if ("RetHt".equals(trtGp)) {
				trtNm = "회송이력ID";
				jspeed_query_id = getRetHtHistID;
			}
			else if ("Zone".equals(trtGp)) {
				trtNm = "존 버전 ID";
				jspeed_query_id = getZoneVerID;
			}
			else 
			{
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			JDTORecordSet jsRst = super.getRecordSet(jspeed_query_id, null);

			if (jsRst.size() > 0) 
			{
				seqId = commUtils.trim(jsRst.getRecord(0).getFieldString("SEQ_ID")); //Sequence ID
			}
			
			return seqId;
		} 
		catch (Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스정보조회
	 * 
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYfIFInfo(String IfId) throws DAOException
	{
		String jspeed_query_id = getYfIFInfo;
		Object[] objs = null;

		try 
		{
			objs = new Object[]{ IfId };

			return super.getRecordSet(jspeed_query_id, objs);
		} 
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스레이아웃조회
	 * 
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYfIFLayout(String IfId) throws DAOException 
	{
		String jspeed_query_id = getYfIFLayout;
		Object[] objs = null;

	
		try 
		{
			objs = new Object[]{ IfId };

			return super.getRecordSet(jspeed_query_id, objs);
		} 
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스TestData(조회)
	 *      -특수강
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getIFTest(GridData gdReq) throws DAOException 
	{
		String jspeed_query_id = null;
		Object[] objs = null;

		try 
		{
			if("SI".equals(gdReq.getParam("V_TRT_GP"))) 
			{
				//기본정보조회
				jspeed_query_id = getYfIFInfo;

				objs = new Object[]
				{
				    CmnUtil.nvl(gdReq.getParam("V_IF_MTH_GP"   ), ""),	//IF방법구분
				    CmnUtil.nvl(gdReq.getParam("V_IF_SNDRCV_GP"), ""),	//IF송수신구분
					CmnUtil.nvl(gdReq.getParam("V_IF_ID"       ), "") //IFID
				};
			} 
			else 
			{
				//TestData조회
				jspeed_query_id = getYfIFLayout;

				objs = new Object[]
				{
					CmnUtil.nvl(gdReq.getParam("IF_ID"), "") //IFID
				};
			}

			return super.getRecordSet(jspeed_query_id, objs);
		}
		catch(Exception e) 
		{
			CmnUtil.printSqlLog(jspeed_query_id, objs);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test(저장)
	 * 
	 *      @param Object[][] objs
	 *      @return int[]
	 *      @throws DAOException
	*/
	public int[] updIFTest(Object[][] objs) throws DAOException
	{
		String jspeed_query_id = updIFTestData;

		try 
		{
			CmnUtil.printSqlLog(jspeed_query_id, objs);

        	return super.trtProcess(jspeed_query_id, objs);
		}
		catch(Exception e)
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 *         String     	 logId   	 
	 *         String     	 mthdNm   	 
	 *         String     	 trtNm   	 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet select(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException
	{
		
		String methodNm = trtNm + "[YfCommDAO.select] < " + mthdNm;
		JDTORecord recPara = null;	
		
		try 
		{
			// PIDEV PI_YD
			//String piYd = commUtils.nvl(commUtils.trim(inRec.getFieldString("PI_YD")), "*");
			
			// PIDEV
			//queryId = this.getYfRulePI("", methodNm, "YF0001", queryId, "APPPI0", piYd, "*" );
				
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			JDTORecordSet rsTemp = super.getRecordSet(recPara);	//query execute
			
			commUtils.printLog(logId, trtNm + "[YfCommDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
//	/**
//	 *      [A] 오퍼레이션명 : SELECT 메소드
//	 *      
//	 * @param  JDTORecord    inRec      parameter record
//	 *         String        queryId    QueryId 
//	 *         String     	 logId   	 
//	 *         String     	 mthdNm   	 
//	 *         String     	 trtNm   	 
//	 * @return JDTORecordSet
//	 * @throws DAOException
//	 * @throws JDTOException 
//	 */	
//	public JDTORecordSet select1(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException
//	{
//		
//		String methodNm = trtNm + "[YfCommDAO.select1] < " + mthdNm;
//		JDTORecord recPara = null;	
//		
//		try 
//		{
//			// PIDEV PI_YD
//			String piYd = commUtils.nvl(commUtils.trim(inRec.getFieldString("PI_YD")), "1");
//			
//			// PIDEV
//			queryId = this.getYfRulePI("", methodNm, "YF0001", queryId, "APPPI0", piYd, "*" );
//				
//			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
//			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
//			JDTORecordSet rsTemp = super.getRecordSet(recPara);	//query execute
//			
//			commUtils.printLog(logId, trtNm + "[YfCommDAO.select1] 결과 건수: " + rsTemp.size() , "DB");
//			
//			return rsTemp;
//		}
//		catch (Exception e)
//		{
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}
//	}
	
	/**
	 *      [A] 오퍼레이션명 : conversionFieldname 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         int intGp             // 구분(0:"V_" 추가, 1:"V_" 제거
	 * @return JDTORecord			 // 필드명을 변환한 결과레코드
	 * @throws JDTOException 
	 */
	public JDTORecord conversionFieldname(JDTORecord recPara, int intGp) throws JDTOException 
	{
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		String szFieldName = null;
		Iterator itrFieldName = null;
		
		itrFieldName = recPara.iterateName();	//필드명을 가져온다.
		
		//필드명 갯수만큼 루프를 돈다.
		while(itrFieldName.hasNext()) 
		{
			szFieldName = (String)itrFieldName.next();
			
			if (intGp == 0) 
			{
				recRtnVal.setField("V_" + szFieldName, recPara.getField(szFieldName));			//"V_" 추가
			} 
			else
			{
				recRtnVal.setField(szFieldName.substring(2), recPara.getField(szFieldName));	//"V_" 제거
			}
		}
		
		return recRtnVal ;
	}
	
	public JDTORecordSet getTcRS(ArrayList param) throws DAOException 
	{
		//TODO Auto-generated method stub
		JDTORecordSet tempTcRS = null;

		try
		{
			String jspeed_query_id = getTcRS;
			tempTcRS = super.getRecordSet(jspeed_query_id, param.toArray());
		}
		catch (Exception e) 
		{
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
		
		return tempTcRS;
	}
	
	/**
	 *      [A] 오퍼레이션명 : Jsp 화면용 SELECT 메소드 
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         String        queryId    QueryId 
	 *         String        logId   	 
	 *         String        mthdNm   	 
	 * @return int           result size
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int jspSelect(JDTORecord inRec, JDTORecordSet outRecSet, String queryId, String logId, String mthdNm) throws DAOException, JDTOException 
	{	
		String methodNm = "조회[YfCommDAO.jspSelect] < " + mthdNm;
		
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try 
		{
			// PIDEV PI_YD
			//String piYd = commUtils.nvl(commUtils.trim(inRec.getFieldString("PI_YD")), "*");
			
			// PIDEV
			//queryId = this.getYfRulePI("", methodNm, "YF0001", queryId, "APPPI0", piYd, "*" );
			
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			rsTemp = super.getRecordSet(recPara);					//query execute
			
			commUtils.printLog(logId, "조회[YfCommDAO.jspSelect] 결과 건수: " + rsTemp.size() , "DB");
			
			if (rsTemp.size() > 0) 
			{
				outRecSet.addAll(rsTemp);
			} 
			else 
			{
				return 0;
			}
		} 
		catch (Exception e) 
		{	
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return outRecSet.size();
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet select(JDTORecord inRec, String queryId) throws DAOException, JDTOException
	{	
		JDTORecord recPara = null;	
		JDTORecordSet rsTemp = null;
		
		try 
		{
			// PIDEV PI_YD
			//String piYd = commUtils.nvl(commUtils.trim(inRec.getFieldString("PI_YD")), "*");
			
			// PIDEV
			//queryId = this.getYfRulePI("", "", "YF0001", queryId, "APPPI0", piYd, "*" );
			
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			rsTemp = super.getRecordSet(recPara);					//query execute
		 
		}
		catch (Exception e)
		{	
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return rsTemp;
	}
	
	/**
	 *      [A] 오퍼레이션명 : SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 *         String     	 logId   	 
	 *         String     	 mthdNm   	 
	 *         String     	 trtNm   	 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet selectL2(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException
	{	
		JDTORecord recPara = null;
		JDTORecordSet rsTemp =  null;
		
		try 
		{		
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			rsTemp = super.getRecordSet(recPara);					//query execute
			
			commUtils.printLog(logId, trtNm + "[YfCommDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
		} 
		catch (Exception e) 
		{
			return rsTemp;
		}
		
	}
	
	/**
	 *      [A] 오퍼레이션명 : UPDATE 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update(JDTORecord inRec, String queryId) throws DAOException, JDTOException 
	{	
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try 
		{
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			intRtnVal = super.trtProcess(recPara);				//query execute
		} 
		catch (Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 *      [A] 오퍼레이션명 : UPDATE 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException 
	{	
		String methodNm = trtNm + "[YfCommDAO.update] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try 
		{
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			intRtnVal = super.trtProcess(recPara);				//query execute
			
			commUtils.printLog(logId, trtNm + "[YfCommDAO.update] 결과 건수: " + intRtnVal , "DB");
		} 
		catch (Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	} 
	
	
	
	/**
	 *      [A] 오퍼레이션명 : UPDATE 메소드
	 *                      - 조회 건수 없으면, 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public boolean updateVerify(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException 
	{	
		String methodNm = trtNm + "[YfCommDAO.updateVerify] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try 
		{
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			intRtnVal = super.trtProcess(recPara);				//query execute
			
			
			if (intRtnVal <= 0) 
    		{
				String szLogMsg = methodNm+" "+trtNm+" 실패!";
				commUtils.printLog(logId, szLogMsg, "SL");  
    			return false;
    		}
			else
			{
				commUtils.printLog(logId, trtNm + "[YfCommDAO.update] 결과 건수: " + intRtnVal , "DB");
			}
		} 
		catch (Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} 
	
	
	
	/**
	 *      [A] 오퍼레이션명 : INSERT 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insert(JDTORecord inRec, String queryId) throws DAOException, JDTOException 
	{	
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try 
		{
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			intRtnVal = super.trtProcess(recPara);				//query execute	
		} 
		catch (Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	} 	

	/**
	 *      [A] 오퍼레이션명 : INSERT 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insert(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException 
	{	
		String methodNm = trtNm + "[YfCommDAO.insert] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try 
		{
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			intRtnVal = super.trtProcess(recPara);				//query execute
			
			commUtils.printLog(logId, trtNm + "[YfCommDAO.insert] 결과 건수: " + intRtnVal , "DB");
		} 
		catch (Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;	
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : INSERT 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public boolean insertVerify(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException 
	{	
		String methodNm = trtNm + "[YfCommDAO.insertVerify] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try 
		{
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			intRtnVal = super.trtProcess(recPara);				//query execute
			
			
			if (intRtnVal <= 0) 
    		{
				String szLogMsg = methodNm+" "+trtNm+" 실패!";
				commUtils.printLog(logId, szLogMsg, "SL");  
    			return false;
    		}
			else
			{
				commUtils.printLog(logId, trtNm + "[YfCommDAO.insertVerify] 결과 건수: " + intRtnVal , "DB");
			}
			
		} 
		catch (Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;	
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : DELETE 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int delete(JDTORecord inRec, String queryId) throws DAOException, JDTOException 
	{	
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try 
		{
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			intRtnVal = super.trtProcess(recPara);				//query execute	
		} 
		catch (Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	} 	
	
	/**
	 *      [A] 오퍼레이션명 : DELETE 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int delete(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YfCommDAO.delete] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try 
		{
			recPara = conversionFieldname(inRec, 0);		//필드명 변환 (필드명 -> V_필드명)
			recPara.setField("JSPEED_QUERY_ID", queryId);	//query id setting
			intRtnVal = super.trtProcess(recPara);				//query execute
			
			commUtils.printLog(logId, trtNm + "[YfCommDAO.delete] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;	
	} 
	
	/**
	 *      [A] 오퍼레이션명 : L3전문생성
	 *      
	 *      @param String msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgL3(String msgId, JDTORecord jrParam) throws DAOException 
	{
		String methodNm	= "L3전문생성[YfCommDAO.getMsgL3] < " + jrParam.getResultMsg();
		String logId	= jrParam.getResultCode();
		String trtNm	= "";

		try 
		{
			String jspeed_query_id = "";
			jrParam.setField("JMS_TC_CD",	msgId);
			
			/* 출하관리  */	
			if("YDDMR001".equals(msgId)) 
			{       				
				trtNm = "입고실적";
				jspeed_query_id = TcYDDMR001;
			}
			else if("YDDMR004".equals(msgId)) 
			{
				trtNm = "코일제품이적작업실적";
				jspeed_query_id = TcYDDMR004;				
			}
			else if("YDDMR011".equals(msgId)) 
			{	
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = TcYDDMR011;	
			}			 
			else if("YDDMR015".equals(msgId))
			{    				
				trtNm = "출하차량상차완료";
				jspeed_query_id = TcYDDMR015;
			}
			else if("YDDMR019".equals(msgId)) 
			{
				trtNm = "코일제품고간이송상하차개시";
				jspeed_query_id = TcYDDMR019;
			}
			else if("YDDMR020".equals(msgId))
			{
				trtNm = "임가공이송상하차개시";
				jspeed_query_id = TcYDDMR020;
			}
			else if("YDDMR021".equals(msgId))
			{
				trtNm = "코일제품고간이송상하차완료";
				jspeed_query_id = TcYDDMR021;
			}
			else if("YDDMR022".equals(msgId))
			{
				trtNm = "임가공이송상차완료";
				jspeed_query_id = TcYDDMR022;
			}			
			else if("YDDMR050".equals(msgId))
			{
				trtNm = "상차완료(야드핸드링)";
				jspeed_query_id = TcYDDMR050;
			}
			else if("YDDMR071".equals(msgId))
			{
				trtNm = "코일이송 상차개시";
				jspeed_query_id = TcYDDMR071;
			}
			else if("YDDMR072".equals(msgId))
			{		
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = TcYDDMR072;
			}
			else if("YDDMR073".equals(msgId))
			{  
				trtNm = "코일이송 상차완료";
				jspeed_query_id = TcYDDMR073;
			}
			else if("YDDMR075".equals(msgId))
			{
				trtNm = "코일이송하차개시전송 PDA";
				jspeed_query_id = TcYDDMR075;
			}
			else if("YDDMR076".equals(msgId))
			{
				trtNm = "코일이송하차완료전송 PDA";
				jspeed_query_id = TcYDDMR076;
			}			  			
			else if("YDTSJ007".equals(msgId)) 
			{   
				jspeed_query_id = TcYDTSJ007;
				trtNm = "소재차량상차개시";
			} 
			else if("YDTSJ008".equals(msgId))
			{
				trtNm = "소재차량상차완료";
				jspeed_query_id = TcYDTSJ008;
			}
			else if("YDTSJ009".equals(msgId)) 
			{
				trtNm = "소재차량하차개시";
				jspeed_query_id = TcYDTSJ009;
			}
			else if("YDTSJ010".equals(msgId))
			{
				trtNm = "소재차량하차완료";
				jspeed_query_id = TcYDTSJ010;
			}
			else if("YDPTJ002".equals(msgId)) 
			{
				trtNm = "코일소재이송완료실적";
				jspeed_query_id = TcYDPTJ002;
			}
			else if("YDPTJ003".equals(msgId))
			{
				trtNm = "임가공코일소재이송완료실적";
				jspeed_query_id = TcYDPTJ003;
			}
			else if("YDPTJ006".equals(msgId))
			{
				trtNm = "냉연코일이송진행 상태실적";
				jspeed_query_id = TcYDPTJ006;
			}
			else if("YDPTJ007".equals(msgId))
			{
				trtNm = "이송지시 취소";
				jspeed_query_id = TcYDPTJ007;
			}
			else if("YMPOJ161".equals(msgId)) 
			{
				trtNm = "조업 송신:코일보급 및 보급 취소 처리";
				jspeed_query_id = TcYMPOJ161;
			}
			else if("YMPOJ161B".equals(msgId))
			{
				trtNm = "";
				jspeed_query_id = TcYMPOJ161BackUp;
			} 
			else if("YDQMJ002".equals(msgId))
			{
				trtNm = "품질 송신:열연정정입측보급실적";
				jspeed_query_id = TcYDQMJ002;
			}
			else if ("YDQMJ002B".equals(msgId))
			{
				trtNm = "";
				jspeed_query_id = TcYDQMJ002BackUp;
			}
			/* ↓냉연 관련↓ */
			else if ("YFCRJ001".equals(msgId))
			{
				trtNm = "저장품제원정보요구";	//냉연저장품제원정보요구
				jspeed_query_id = TcYFCRJ001;
			}
			else if ("YFCRJ002".equals(msgId))
			{
				trtNm = "저장품위치정보";		//냉연저장품위치정보
				jspeed_query_id = TcYFCRJ002;
			}
			else if ("YFCRJ003".equals(msgId))
			{
				trtNm = "코일하차완료";		//코일하차완료(구내운송)
				jspeed_query_id = TcYFCRJ003;
			}
			/* ↑냉연 관련↑ */
			/* ↓냉연 구내운송 관련↓ */
			else if("YFTSJ008".equals(msgId))
			{
				trtNm = "소재차량상차완료";
				jspeed_query_id = TcYFTSJ008;
			}
			else if("YFTSJ009".equals(msgId)) 
			{
				trtNm = "소재차량하차개시";
				jspeed_query_id = TcYFTSJ009;
			}
			else if("YFTSJ010".equals(msgId))
			{
				trtNm = "소재차량하차완료";
				jspeed_query_id = TcYFTSJ010;
			}
			/* ↑냉연 구내운송 관련↑ */
			/* ↓냉연 출하 관련↓ */
			else if("CRDMJ018".equals(msgId))
			{
				trtNm = "냉연공장제품이송실적";
				jspeed_query_id = TcCRDMJ018;
			}
//			else if("CRDMJ007".equals(msgId))
//			{
//				trtNm = "냉연상차개시실적";
//				jspeed_query_id = TcCRDMJ007;
//			}
			else if("CRDMJ008".equals(msgId))
			{
				trtNm = "냉연상차완료실적";
				jspeed_query_id = TcCRDMJ008;
			}
			else if("CRDMJ009".equals(msgId))
			{
				trtNm = "냉연하차개시실적";
				jspeed_query_id = TcCRDMJ009;
			}
			else if("CRDMJ010".equals(msgId))
			{
				trtNm = "냉연하차완료실적";
				jspeed_query_id = TcCRDMJ010;
			}
			/* ↑냉연 출하 관련↑ */
			
			else if("M10YDLMJ1115".equals(msgId))
			{
				trtNm = "임가공이송상하차개시";
				jspeed_query_id = TcM10YDLMJ1115_PIDEV;
			}			
			else if("M10YDLMJ1011".equals(msgId)) 
			{       				
				trtNm = "입고실적";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1011_PIDEV";
			}
			else if("M10YDLMJ1031".equals(msgId)) 
			{
				trtNm = "코일제품이적작업실적";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1031_PIDEV";				
			}
			else if("M10YDLMJ1081A".equals(msgId)) 
			{	
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1081A_PIDEV";	
			}				
			else if("M10YDLMJ1091A".equals(msgId))
			{    				
				trtNm = "출하차량상차완료";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1091A_PIDEV";
			}
			else if("M10YDLMJ1111A".equals(msgId)) 
			{
				trtNm = "코일제품고간이송상하차개시";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1111A_PIDEV";
			}			
			else if("M10YDLMJ1125".equals(msgId))
			{
				trtNm = "임가공이송상하차완료";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1125_PIDEV";
			}	
			else if("M10YDLMJ1051".equals(msgId))
			{
				trtNm = "상차완료(야드핸드링)";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1051_PIDEV";
			}			
			else if("M10YDLMJ1071A".equals(msgId))
			{
				trtNm = "코일이송 상차개시";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1071A_PIDEV";
			}	
			else if("M10YDLMJ1071B".equals(msgId))
			{
				trtNm = "코일이송 상차개시";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1071B_PIDEV";
			}	
			else if("M10YDLMJ1081B".equals(msgId))
			{		
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1081B_PIDEV";
			}
			else if("M10YDLMJ1081C".equals(msgId))
			{		
				trtNm = "코일일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1081C_PIDEV";
			}
			else if("M10YDLMJ1091B".equals(msgId))
			{  
				trtNm = "코일이송 상차완료";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1091B_PIDEV";
			}		
			else if("M10YDLMJ1091C".equals(msgId))
			{  
				trtNm = "코일이송 상차완료";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1091C_PIDEV";
			}	
			else if("M10YDLMJ1111B".equals(msgId))
			{
				trtNm = "코일이송하차개시전송 PDA";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1111B_PIDEV";
			}
			else if("M10YDLMJ1121A".equals(msgId))
			{
				trtNm = "열연코일하차완료수신";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1121A_PIDEV";
			}
			else if("M10YDLMJ1121B".equals(msgId))
			{
				trtNm = "열연코일하차완료수신";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1121B_PIDEV";
			}	
			else if("M10YDLMJ1095".equals(msgId))
			{
				trtNm = "임가공이송상하차완료";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1095_PIDEV";
			}	
			else if("M10YDLMJ1141".equals(msgId))
			{
				trtNm = "입동대기차량포인트변경";
				jspeed_query_id = "com.inisteel.cim.yf.acommon.dao.YfCommDAO.TcM10YDLMJ1141_PIDEV";
			}
			
			JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) 
			{
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select(jrParam, jspeed_query_id);
					
				commUtils.printLog(logId, trtNm + jsRst.size(), "DB");
				
				//---[JMS IF 로그 조회 시 순서바뀜 현상 수정 추가 시작]-------------------------------------------------------
				JDTORecordSet addData = JDTORecordFactory.getInstance().createRecordSet("");
				String sITM_ID;
				String sITM_VALUE;
				
				if(jsRst.size()>0) 
				{
					// PIDEV
					if("M10".equals(msgId.substring(0, 3))) {
						jrParam.setField("PI_YD", "1");
					}
					JDTORecord jrAdd = JDTORecordFactory.getInstance().create();
					jrParam.setField("IF_ID",msgId);
					JDTORecordSet jsLayOut = this.select(jrParam, getIfTestLayout);
					
					for(int ii = 0; ii < jsRst.size(); ii++) 
					{	
						for(int jj = 0; jj < jsLayOut.size(); jj++ )
						{	
							sITM_ID = jsLayOut.getRecord(jj).getFieldString("ITM_ID");
							sITM_VALUE = jsRst.getRecord(ii).getFieldString(sITM_ID);
							
							jrAdd.setField(sITM_ID , sITM_VALUE);
						}
						addData.addRecord(jrAdd);
					}
					
					jsRst = JDTORecordFactory.getInstance().createRecordSet("");
					jsRst.addAll(addData);
				}
				//---[JMS IF 로그 조회 시 순서바뀜 현상 수정 추가 종료]-------------------------------------------------------
			}
			
			return jsRst;			
		} 
		catch (Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : L2전문조회
	 *      
	 * @param String msgId
	 * @param JDTORecord jrParam
	 * @return JDTORecordSet
	 * @throws DAOException
	*/
	public JDTORecordSet getMsgL2(String msgId, JDTORecord jrParam) throws DAOException 
	{
		String methodNm = "L2전문생성[YfCommDAO.getMsgL2] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try 
		{
			String jspeed_query_id = "";

			/* 박판열연 COIL야드 L2 송신 *************************************************************************************/			    	
			if("YFF1L001".equals(msgId))
			{
				trtNm = "박판열연 COIL 저장위치 제원";
		    	jspeed_query_id = getYFF1L001;
		    	// PIDEV
		    	jrParam.setField("PI_YD", "1");
			}
			else if("YFF1L001_CarInfo".equals(msgId))
			{
		    	trtNm = "박판열연 코일 저장위치제원(차량정보Backup)";
		    	jspeed_query_id = getYFF1L001_CarInfo;
			}
			else if("YFF1L002_SCRAP".equals(msgId))
			{
				trtNm = "박판열연 코일 저장품제원";
				
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd))
				{
					//위치별 >> 1:동,2:SPAN,3:열,4:BED
					jspeed_query_id = getYFF1L002ByLoc_SCRAP;
				}
				else
				{
					jspeed_query_id = getYFF1L002_SCRAP;
				}
			}
			else if("YFF1L002".equals(msgId))
			{
		    	trtNm = "박판열연 코일 저장품제원";
		    	
				//야드정보동기화코드 
				String ydInfoSyncCd = commUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) 
				{
					//위치별 >> 1:동,2:SPAN,3:열,4:BED
			    	jspeed_query_id = getYFF1L002ByLoc;
				}
				else
				{
					//재료별 >> 5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입,R:코일분할
			    	jspeed_query_id = getYFF1L002;
				}
			}
			else if("YFF1L002DnWr".equals(msgId))
			{
				trtNm = "박판열연 코일 저장품 제원";
		    	jspeed_query_id = getYFF1L002DnWr;
			}
			else if("YFF1L004".equals(msgId))
			{	
				//DEFAULT
				jspeed_query_id = TcYFF1L004;	
		    	// PIDEV
		    	jrParam.setField("PI_YD", "1");
				
				//SCRAP
		    	trtNm = "박판열연 COIL 작업지시";

		    	String sAPP022  = "N";
		    	JDTORecord jrParam1 = commUtils.getParam("", methodNm, "");
				jrParam1.setField("REPR_CD_GP", "APP022"  ); 
				jrParam1.setField("CD_GP"     , "1"       ); 
				jrParam1.setField("ITEM"      , "1"       ); 

				JDTORecordSet jsChk = this.select(jrParam1, getACoilApplyYn, logId, methodNm, "열정보 Read"); 

				if (jsChk.size() > 0)
				{
					sAPP022    = commUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
				}
	            
		    	if("Y".equals(sAPP022))
		    	{
					JDTORecordSet schInfo = this.select(jrParam, getSchScrapInfoWithSchId);
	
					if (schInfo.size() > 0)
					{
						String sSTOCK_ID      = StringHelper.evl(schInfo.getRecord(0).getFieldString("STL_NO"), "");
						String sSTEP_NO       = StringHelper.evl(schInfo.getRecord(0).getFieldString("STEP_NO"), "");
						
						if("S".equals(sSTOCK_ID.substring(0,1)))
						{
							jrParam.setField("STEP_NO"	, sSTEP_NO);
							jspeed_query_id = TcYFF1L004Scrap;		
						}
					}
		    	}
			}
			else if("YFF1L004WC".equals(msgId))
			{
				trtNm = "박판열연 분동COIL 작업지시";
				jspeed_query_id = TcYFF1L004WeightCoil;	
			}
			else if("YFF1L006".equals(msgId))
			{	
		    	trtNm = "박판열연 COIL 대차출발지시";
		    	jspeed_query_id = TcYFF1L006;    	
			}
			else if("YFF1L007".equals(msgId))
			{	
		    	trtNm = "작업 현황 응답";
		    	jspeed_query_id = TcYFF1L007;
			}
			else if("YFF1L008".equals(msgId))
			{
		    	trtNm = "박판열연 COIL 차량예정정보";
		    	jspeed_query_id = YFF1L008;
			}
			else if("YFF1L008BackUp".equals(msgId))
			{
				trtNm = "박판열연 COIL 차량예정정보 BackUp";
				jspeed_query_id = YFF1L008BackUp;				
			}
			else if("YFF1L009".equals(msgId))
			{
		    	trtNm = "박판열연 COIL 압연실적";
		    	jspeed_query_id = getYFF1L009;
			}
			else if("YFF1L017".equals(msgId))
			{
		    	trtNm = "CTS작업출발지시";
		    	jspeed_query_id = getYFF1L017;
			}
			else if("YFF1L017home".equals(msgId))
			{
		    	trtNm = "CTS작업출발지시(home)";
		    	jspeed_query_id = getYFF1L017home;
			}
			else if("YFF1L021".equals(msgId))
			{
		    	trtNm = "존정보 갱신";
		    	jspeed_query_id = getYFF1L021;
			}
			else if("YFF1L021BackUp".equals(msgId))
			{
		    	trtNm = "존버전정보 갱신";
		    	jspeed_query_id = getYFF1L021BackUp;
			}
			
			JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id))
			{
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select(jrParam, jspeed_query_id);
					
				commUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			}
			
			commUtils.printLog(logId, "end", "DB");

			return jsRst;
		}
		catch (Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int           intGp      구분
	 *         							(
	 *         								0:YD_STK_COL_GP,
	 *                                      1:YD_GP,YD_BAY_GP,YD_EQP_GP, ,YD_STK_COL_ACT_STAT
	 *                                      2:YD_GP,YD_BAY_GP,YD_EQP_GP,YD_STK_COL_NO ,YD_STK_COL_ACT_STAT
	 *                                      3:YD_STK_COL_NO1 ,YD_STK_COL_NO2,YD_STK_COL_NO3,PAGE_CNT1,ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                                      4:V_WLOC_CD ,  V_YD_PNT_CD
	 *                                      7:V_YD_GP, V_YD_BAY_GP, V_YD_EQP_GP, V_YD_STK_COL_NO, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
	 *                                      8:YD_STK_COL_GP
	 *                                      9:YD_GP, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO
	 *                                      10:YD_GP, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO
	 *                                      11:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_BED_NO_R
	 *                                      12:YD_GP
	 *                                      16:YD_STK_COL_GP
	 *                                      18:YD_GP                                      
	 *                                      21:YD_GP,YD_BAY_GP,YD_EQP_GP
	 *                                  )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */
	public int getYdStkcol(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException 
	{
		JDTORecordSet rsTemp = null;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;
		
		try 
		{
			recPara = this.conversionFieldname(inRec, 0);	//필드명 변환 (필드명 -> V_필드명)
			
			//parameter check
			blnChk_Field = commUtils.chkPara_getYdStkcol(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
			{
				return -2;
			}
			
			//query id setting
			//기존에는 intGp 값에 따라 다양하게 있었으나 intGp 오는값이 4밖에 없어서 다 삭제함
			if (intGp == 4)
			{
				recPara.setField("JSPEED_QUERY_ID", getYdStkcolWLocCdandPntCd);
			}
			
			//query execute
			rsTemp = super.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
			{
				outRecSet.addAll(rsTemp);
			}
			else 
			{
				return 0;
			}
			
			return 1;
		}
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분 
	 *         					( 
	 *         						0: 
	 *                              1: 
	 *                              2:
	 *							)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */
	public int uptYmEtcDao(JDTORecord inRec, int intGp) throws DAOException, JDTOException 
	{
		int intRtnVal               = 0;
		
		try 
		{	
			/*
			기존에는 트렌젝션 분리 적용으로 나누어져 있었으나
			intGp 으로 들어오는 값이 1~3밖에 없어서 그외는 삭제
			*/
	    	{
	    		//기존 방식 적용 
	    		intRtnVal = this.uptYmEtcDaoTX(inRec, intGp);
	    		
	    		if(intRtnVal ==0)
	    		{
	    			return intRtnVal = -1;
	    		}
	    	}
			
			intRtnVal = 1;
			
		} 
		catch (Exception e) 
		{
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szSessionName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of uptYmEtcDao
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분 ( 0: 
	 *                                1: 
	 *                                2:
	 *                              )
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int uptYmEtcDaoTX (JDTORecord inRec, int intGp) throws DAOException, JDTOException 
	{
		int intRtnVal = 0;
		Object oParam[]             = null;
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		
		try 
		{	
			//변환용 레코드
			JDTORecord recPara = null;
			recPara = inRec;
			
			//query id setting
			if (intGp == 1)
			{
				oParam = new Object[]
				             {						
								commUtils.paraRecChkNull(recPara, "YD_STK_COL_ACTIVE_STAT"),
								commUtils.paraRecChkNull(recPara, "YD_CAR_USE_GP"),
								commUtils.paraRecChkNull(recPara, "TRN_EQP_CD"),
								commUtils.paraRecChkNull(recPara, "CAR_NO"),
								commUtils.paraRecChkNull(recPara, "MODIFIER"),
								commUtils.paraRecChkNull(recPara, "YD_STK_COL_GP")
				             };
				
				intRtnVal = assistantDAO.trtProcess(updTB_YF_STACKCOL,oParam);
			}
			else if (intGp == 2)
			{
				
				oParam = new Object[] 
				             {						
								commUtils.paraRecChkNull(recPara, "YD_STK_BED_ACTIVE_STAT"),
								commUtils.paraRecChkNull(recPara, "YD_STK_BED_WT_MAX"),
								commUtils.paraRecChkNull(recPara, "MODIFIER"),
								commUtils.paraRecChkNull(recPara, "YD_STK_COL_GP")					
				             };
				
				intRtnVal = assistantDAO.trtProcess(updTB_YF_STACKER,oParam);	
			}
			else if (intGp == 3)
			{
				oParam = new Object[] 
				             {						
								commUtils.paraRecChkNull(recPara, "YD_STK_LYR_ACTIVE_STAT"),
								commUtils.paraRecChkNull(recPara, "YD_STK_LYR_STAT"),
								commUtils.paraRecChkNull(recPara, "STL_NO"),
								commUtils.paraRecChkNull(recPara, "MODIFIER"),
								commUtils.paraRecChkNull(recPara, "YD_STK_COL_GP")					
				             };
				
				intRtnVal = assistantDAO.trtProcess(updTB_YF_STACKLAYER,oParam);				
			}
			/*
			intGp 으로 들어오는 값이 1~3밖에 없어서 그외는 삭제
			*/
		}
		catch (Exception e) 
		{
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szSessionName + e.getMessage(), e);
		}
		
		return intRtnVal;
	} // end of uptYmEtcDaoTX
	
	/**
	 * 적치열 테이블에 카드번호의 적치열이 존재하는지 리턴한다.
	 * @param pos		차량정지위치
	 * @return
	 */
	public JDTORecord readStackCol(String wloccd, String ydpntcd) 
	{
        return commonDao.findByPrimaryKey(selectStackCol, new Object[]{ wloccd ,ydpntcd });	
	}
	
	/**
	 * 적치열 테이블에 재료의위치를 리턴한다.
	 * @param stockId	재료번호
	 * @return
	 */
	public JDTORecord readStockLoc(String stl_no) 
	{
        return commonDao.findByPrimaryKey(selectStockLoc, new Object[]{ stl_no });	
	}
	
	public List getCommonList(String queryCode, Object[] objs) throws DAOException
	{
		return commonDao.findList(queryCode, objs);	
    }
	
	public List getCommonList(String queryCode, JDTORecord inRec) throws DAOException
	{
		return commonDao.findList(queryCode, inRec);	
    }

	public List getCommonList(String queryCode, String dyQueryCode, Object[] objs) throws DAOException
	{
		return commonDao.findList(queryCode, dyQueryCode, objs);	
    }
	
	/**
     * 차량 개소코드,포인트 코드 차량id 리턴한다.
     * @param col		적치열
     * @return
     */
    public List readcarinfoOfwloc(String cardno,String pos) 
    {
        String queryCode = readcarinfoOfwloc;    	
        return commonDao.findList(queryCode, new Object[]{ cardno ,pos });
    }
    
    /**
     * 카드번호에 대한 저장품 정보를 리턴한다.
     * @param cardNo	카드번호
     * @return
     */
    public List readStockInfoOfCardNo(String cardNo) 
    {
        String queryCode = selectStockInfoOfCardNo;
        return commonDao.findList(queryCode, new Object[]{ cardNo });        
    }
    
    /**
     * 적치대의 적재능력을 초기화 한다.
     * @param col	적치열
     * @param bed	번지
     * @return
     */
    public void modifyPossibleOfStacker(String col, String bed)
    {
        String  queryCode = updatePossibleOfStacker;
        commonDao.updateData(queryCode, new Object[]{ col, bed });
    }
    
    /**
     * 적치열 테이블의 '차량 CARD 번호' UPDATE
     * @param cardNo	차량 CARD 번호
     * @param whrCol	적치열
     */
    public void modifyCardNoOfStackCol(String cardNo, String whrCol) 
    {
        String qId = updateCardNoOfStackCol;
        commonDao.updateData(qId, new Object[]{ cardNo, whrCol });                
    }
    
    /**
     * 적치단 테이블의 '저장품ID', '적치단 상태' 항목을 UPDATE 한다.
     * @param coilNo	저장품ID
     * @param actStat	단상태['O','C']
     * @param stat		적치상태['S','P','L',...]
     * @param whrCol	적치열
     * @return
     */
    public void modifyStockStatOfLayer(String coilNo, String actStat, String stat, String whrCol)
    {
        String  queryCode = updateStockStatOfLayer2;
        commonDao.updateData(queryCode, new Object[]{ coilNo, actStat, stat, whrCol });
    }
    
    /**
     * 저장품테이블의 '이송 설비 구분', '이송 설비 BED 구분', '이송 설비 단 구분'을 UPDATE
     * @param col			이송 설비 구분
     * @param bed			이송 설비 BED 구분
     * @param layer			이송 설비 단 구분
     * @param term			저장품이동조건
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyTermAndMoveEquipOfStock(String col, String bed, String layer, String term, String whrStockId)
    {
        String queryCode = updateMoveEquipOfStock2;
        //commonDao.updateData(queryCode, new Object[]{ col, bed, layer, term, whrStockId });	//TB_YF_STOCK 테이블의 항목 삭제됨
        commonDao.updateData(queryCode, new Object[]{ term, whrStockId });
    }
    
    /**
     * 차량번호를 리턴한다.
     * @param cardNo	차량카드번호
     * @return
     */
    public JDTORecord readCarNo(String yd, String cardNo) 
    {
        String queryCode = selectCarNo;        
        return commonDao.findByPrimaryKey(queryCode, new Object[]{ yd, cardNo });
    }
    
    /**
     * TC LAYOUT 항목의 길이 정보를 Map으로 리턴한다.
     * @param tc
     * @return
     */
    public Map readColumnLenOfTc(String tc) 
    {
        String queryCode = lengthOfTcColumn;
        return readColumnLenOfTc(tc, queryCode);
    }

    /**
     * TC LAYOUT 항목의 길이 정보를 Map으로 리턴한다.
     * @param qId	쿼리ID
     * @param tc	전문ID
     * @return
     */
    public Map readColumnLenOfTc(String tc, String qId) 
    {
        List list	= commonDao.findList(qId, new Object[]{ tc });
        int listCnt	= list != null ? list.size() : 0;
        Map data	= new HashMap();
        
        for(int i = 0; i < listCnt; i++) 
        {
            data.put(((JDTORecord)list.get(i)).getFieldString("ITEM_NAME"), ((JDTORecord)list.get(i)).getFieldString("ITEM_LEN"));
        }
        
        return data;
    }
    
    /**
     * 차량 하차시 관련정보를 저장품에서 CLEAR 한다.
     * @param gp			이송 설비 구분	
     * @param bed			이송 설비 BED 구분
     * @param layer			이송 설비 단 구분
     * @param cardNo		차량 CARD 번호
     * @param whrStockId	저장품 ID
     * PALLET_NO의 값을 SHEAR_SUPPLY_DEMAND_DDTT에 임시로 저장하기 때문에 삭제(MCH)
     */
    public void modifyUnloadInfoOfStock
    (
    	String gp,				//이송 설비 구분 
    	String bed,				//이송 설비 BED 구분 
    	String layer,			//이송 설비 단 구분 
    	String ordDate, 
    	String ordNo, 
    	String cardNo,			//차량 CARD 번호 
    	String whrStockId		//저장품 ID
    ) 
    {	
		String qId = updateUnloadInfoOfStock;
		//commonDao.updateData(qId, new Object[]{ gp, bed, layer, ordDate, ordNo, cardNo, whrStockId });	//TB_YF_STOCK 에서 항목 삭제함
		commonDao.updateData(qId, new Object[]{ ordDate, ordNo, cardNo, whrStockId });
    }
    
    /**
     * @return 
     * @throws 
     */
	public JDTORecord getCommonInfo(String queryCode,Object[] objs) throws DAOException
	{
		return commonDao.findByPrimaryKey(queryCode, objs);	
    }
    
	/**
	 * 차량재료 저장위치  초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 */
	public int delCarSchMtlLayer(JDTORecord inData) throws DAOException 
	{
		String	queryId	= delCarSchMtlLayer_PIDEV;
		int 	ret	 	= 0;
		
		try 
		{	
			inData.setField("JSPEED_QUERY_ID", queryId);	//  파라미터 설정
			
			ret = super.trtProcess(inData);	// 검색문을 실행합니다.
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		finally
		{
			
		}
		
		return ret;
	}
	
	/**
	 * 차량상태 초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 */
	public int delCarWrMgtCarSchMtl(JDTORecord inData) throws DAOException 
	{
		String	queryId	= delCarWrMgtCarSchMtl;
		int 	ret	 	= 0;
		
		try 
		{
			inData.setField("JSPEED_QUERY_ID", queryId);	//  파라미터 설정
			
			ret = super.trtProcess(inData);	// 검색문을 실행합니다.
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		finally
		{
			
		}
		
		return ret;
	}
	
	/**
	 * 차량상태 초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 */
	public int delCarWrMgtCarSch(JDTORecord inData) throws DAOException 
	{
		String	queryId	= delCarWrMgtCarSch;
		int		ret		= 0;
		
		try 
		{
			inData.setField("JSPEED_QUERY_ID", queryId);	//  파라미터 설정
			
			ret = super.trtProcess(inData);	// 검색문을 실행합니다.
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		finally
		{
			
		}
		
		return ret;
	}
	
	/**
	 * 적치열 테이블에 카드번호가 존재하는지 리턴한다.
	 * @param pos		차량정지위치
	 * @return
	 */
	public JDTORecord readCardNo(String pos) 
	{
        String qcd = selectCardNo;
        return commonDao.findByPrimaryKey(qcd, new Object[]{ pos });
	}
	
	/**
	 * 적치열 테이블에 카드번호가 존재하는지 리턴한다.
	 * pos 인자는 사용하지 않음.
	 * @param query     조회쿼리코드
	 * @param pos		차량정지위치
	 * @param cardNo    차량카드번호
	 * @return
	 */
	public JDTORecord readCardNo(String query, String pos,String cardNo) 
	{
		return commonDao.findByPrimaryKey(query, new Object[]{  cardNo });
	}
	
//	/**
//	 * 적치열 테이블에 카드번호가 존재하는지 리턴한다.
//	 * @param pos		차량정지위치
//	 * @return
//	 */
//	public JDTORecord readCardNoT(String pos) 
//	{
//        String qcd = "ym.common.dao.selectCardNoT";
//        return commonDao.findByPrimaryKey(qcd, new Object[]{ pos});
//	}
	
	/**
	 * 적치열 테이블에 카드번호가 존재하는지 리턴한다.
	 * @param yd		야드구분
	 * @param cardNo	차량카드번호
	 * @return
	 */
	public JDTORecord readCardNo(String yd, String cardNo) 
	{
		/*
		SELECT  CAR_CARD_NO
		FROM    TB_YM_STACKCOL
		WHERE   YD_GP       = ? 
		AND     CAR_CARD_NO = ?
		AND     SECT_GP IN ('TR', 'PT')
		*/
        String qcd = "ym.common.dao.selectCardNo1";
        return commonDao.findByPrimaryKey(qcd, new Object[]{ yd, cardNo });
	}
	
	/**
     * 차량의 멀티동 정보를 리턴한다.
     * @param cardNo	차량CARD번호
     * @return
     */
    public List readMultyBay(String cardNo) 
    {	
        return commonDao.findList(selectMultyBay, new Object[]{ cardNo });
    }
    
    /**
     * 차량의 멀티동 정보를 리턴한다.
     * @param cardNo	차량CARD번호
     * @return
     */
    public List readMultyBay(String cardNo,int flag_IN) 
    {
        return commonDao.findList(selectMultyBay2, new Object[]{ cardNo });
    }
    
    /**
     * @return 
     * @throws 
     */
   	public int updateData(String queryCode, Object[] objs) throws DAOException
   	{	   	 	
   		return commonDao.updateData(queryCode,objs);
   	}

   	/**
   	 * 
   	 * @param queryCode
   	 * @param dSql
   	 * @param objs
   	 * @return
   	 * @throws DAOException
   	 */
   	public int updateData(String queryCode, String dSql, Object[] objs) throws DAOException
   	{	   	 	
   		return commonDao.updateData(queryCode, dSql, objs);
   	}
    
    /**
     * 적치단 테이블의 '적치 단 활성 상태'를 UPDATE
     * @param stat			적치 단 활성 상태
     * @param whrStackCol	적치 열 구분
     * @return
     */
    public void modifyActiveStatOfLayer(String stat, String whrStackCol) 
    {
        commonDao.updateData(updateActiveStatOfLayer, new Object[]{ stat, whrStackCol });        
    }
    
    /**
     * 적치열 테이블의 '차량 CARD 번호' UPDATE
     * @param cardNo	차량 CARD 번호
     * @param whrCol	적치열
     */
    public void modifyCardNoOfStackCol2(String cardNo ) 
    {
        String qId = updateCardNoOfStackCol2;
        commonDao.updateData(qId, new Object[]{ cardNo });                
    }
    
    /**
     * 적치단 테이블의 '적치 단 활성 상태'를 UPDATE
     * @param stat			적치 단 활성 상태
     * @param whrStackCol	적치 열 구분
     * @param whrBed		적치 번지 구분
     * @param whrLayer		적치 단 구분
     * 적치 단 활성 상태 [O],비활성화 [C], 사용금지[X]
     * @return
     */
    public void modifyActiveStatOfLayer(String stat, String whrStackCol, String whrBed, String whrLayer) 
    {
        String queryCode = updateActiveStatOfLayer1;
        commonDao.updateData(queryCode, new Object[]{ stat, whrStackCol, whrBed, whrLayer });        
    }
    
    public void modifyActiveStatOfLayer_02(String stat, String whrCol, String whrBed) 
    {
        String queryCode = updateActiveStatOfLayer_02;
        commonDao.updateData(queryCode, new Object[]{ stat, whrCol, whrBed });        
    }
    
    /**
     * 적치단 테이블의 '적치상태'를 UPDATE
     * @param layerStat	적치상태
     * @param whrCol		적치열
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyLayerStatOfLayer(String layerStat, String whrCol, String whrStockId) 
    {
        String qcd = updateLayerState1;
        commonDao.updateData(qcd, new Object[]{ layerStat, whrCol, whrStockId });
    }
    
    /**
     * 1. 작업예약 테이블 INSERT
     * 2. 저장품 테이블에 '작업예약ID' UPDATE, 적치단 테이블에 '적치상태'를 UPDATE
     * @param colGp		적치열
     * @param sch		스케쥴작업종류
     * @param operGp	오퍼레이터 지정 구분
     * @param loc		PUT위치
     * @throws Exception 
     */
    public String createWBook(String colGp, String ydSchCd, String operGp, String loc, JDTORecord dto, String logId, String methodNm) throws Exception
    {
    	String ydWbookId = "";
    	
    	try
    	{
    		JDTORecord recInTemp = JDTORecordFactory.getInstance().create();	//QUERY용 변수담을곳
    		
	    	// PIDEV
//	    	String sApplyYnPI = this.ApplyYnPI("", methodNm, "APPPI0", "*", "*");    		
    		
    		ydWbookId = this.getSeqId(logId, methodNm, "WrkBook");			//YD_WBOOK_ID(야드작업예약ID) 생성
			if("".equals(ydWbookId))
			{
				throw new Exception("createWBook 작업예약ID 생성 실패");
			}
			
			recInTemp.setField("YD_SCH_CD", ydSchCd);	//변수에 YD_SCH_CD 담기
			
			String ydSchPrior = "";
			JDTORecordSet jsResult = this.select(recInTemp, getYdSchrule, logId, methodNm, "스케줄 기준 조회");
			
			if (jsResult != null && jsResult.size() > 0) 
			{
				ydSchPrior = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			} 
			else 
			{
				throw new Exception("A열연 코일 스케쥴 코드 이상 : [" + ydSchCd + "]");
			}
			
			//작업예약 등록
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_WBOOK_ID",		ydWbookId);						//야드작업예약ID
			recInTemp.setField("MODIFIER",			"SYSTEM");						//수정자
			recInTemp.setField("YD_GP",				colGp.substring(0, 1));			//야드구분
			recInTemp.setField("YD_BAY_GP",			colGp.substring(1, 2)); 		//야드동구분
			recInTemp.setField("YD_SCH_CD",			ydSchCd);						//야드스케쥴코드
			recInTemp.setField("YD_SCH_PRIOR",		ydSchPrior);					//야드스케쥴우선순위
			recInTemp.setField("YD_SCH_PROG_STAT",	YfConstant.YD_SCH_PROG_STAT_W);	//야드스케쥴진행상태(스케줄수행대기)
			recInTemp.setField("YD_SCH_ST_GP",		YfConstant.YD_SCH_ST_GP_A);		//야드스케쥴기동구분	//차후 수정해야함
			recInTemp.setField("YD_SCH_REQ_GP",		YfConstant.YD_SCH_REQ_GP_6);	//야드스케쥴요청구분	//차후 수정해야함
			
	    	// PIDEV				
			int ins_cnt = 0;
//			if("Y".equals(sApplyYnPI)) {
				ins_cnt = this.insert(recInTemp, insWrkBook_PIDEV, logId, methodNm, "TB_YF_WRKBOOK");
//			} else {
//				ins_cnt = this.insert(recInTemp, insWrkBook, logId, methodNm, "TB_YF_WRKBOOK");	
//			}
			
			if (ins_cnt <= 0) 
			{
				throw new JDTOException("작업예약 등록실패");
			}
			
			JDTORecord jRcd = this.readStockLoc(commUtils.getField(dto, "STL_NO"));

			//작업예약재료 등록
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_WBOOK_ID",	ydWbookId);		//야드작업예약ID
			recInTemp.setField("MODIFIER",		"SYSTEM");		//수정자
			recInTemp.setField("YD_STK_COL_GP",	commUtils.getField(jRcd, "YD_STK_COL_GP"));	//적치 열 구분
			recInTemp.setField("YD_STK_BED_NO",	commUtils.getField(jRcd, "YD_STK_BED_NO"));	//적치 BED 구분
			recInTemp.setField("YD_STK_LYR_NO",	commUtils.getField(jRcd, "YD_STK_LYR_NO"));	//적치 단 구분
			
			ins_cnt = this.insert(recInTemp, insWrkBookMtlByStkLyr, logId, methodNm, "TB_YF_WRKBOOKMTL");
			
			if (ins_cnt <= 0) 
			{
				throw new JDTOException("작업예약 재료 등록실패");
			}
	        
	        return ydWbookId;
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
     * 저장품테이블의 '작업예약ID', '저장품 이동 조건'를 UPDATE
     * A열연 SLAB - > B열연 SLAB야드로 이송도착시 PALLET_NO로 도착처리할 경우
     * 도착처리와 동시에 임시 PALLET_NO를 저장값 삭제
     * @param moveTerm		저장품 이동 조건
     * @param moveOrdNo		이송 지시 번호
     * @param whrStockId	저장품 ID
     * @return
     */
    public void modifyTermAndWBookIdOfStock(String wbookId, String term, String whrStockId)
    {
        commonDao.updateData(updateTermAndWBookIdOfStock, new Object[]{ term, whrStockId });        
    }
    
    /**
     * 작업예약 테이블의 '스케쥴지정방법', 'TO위치'를 UPDATE
     * @param schDec	스케쥴지정방법
     * @param loc			TO위치
     * @param whrWBookId	작업예약ID
     */
    public void modifyOperatorOfWBook(String kind, String schDec, String loc, String whrWBookId) 
    {	
        commonDao.updateData(updateOperatorOfWBook1, new Object[]{ kind, schDec, loc, whrWBookId });
    }
    
    /**
     * 작업예약존재여부 체크
     * @param STL_NO	재료번호
     * @return
     */
    public JDTORecord getWbookSearch(String stl_no) 
    {
		return commonDao.findByPrimaryKey(getWbookSearch, new Object[]{ stl_no });        
    }
    
    /**
     * 저장품 TABLE 에서 저장품 정보를 가져온다.
     *
     * @param String	: 운송작업지시일자
     * @param String	: 운송작업지시순번
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getStockList_02(String sFrtomoveWordDate, String sFrtomoveWordSeqno) throws DAOException
	{	
		Object[] params = {sFrtomoveWordDate, sFrtomoveWordSeqno};	
		return commonDao.findList(getStockList_02, params);
	}
	
	/**
     * 차량 CARD_NO 의 출하정보를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getDmCarInfo(String v_stl_no) throws DAOException
    {	
		Object[] params = {v_stl_no};	
		return commonDao.findByPrimaryKey(getDmCarInfo, params);
	}
    
    /**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Update한다. 
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */
	public JDTORecord requestFind(String queryCode) throws DAOException
	{
		return commonDao.find(queryCode);          
    }
	
	public JDTORecord requestgetData(String queryCode,Object[] objs) throws DAOException
    {
		return commonDao.findByPrimaryKey(queryCode, objs);          
    }
	
	public int requestinsertData(String queryCode, Object[] objs) throws DAOException
	{	
		return commonDao.insertData(queryCode, objs);
	}
	
   	public int requestupdateData(String queryCode, Object[] objs) throws DAOException
   	{	   	 	
   		return commonDao.updateData(queryCode, objs);
   	}
   	
   	public int requestdeleteData(String queryCode, Object[] objs) throws DAOException
   	{	   	 	
   		return commonDao.deleteData(queryCode,objs);
   	}
   	
   	public List getListData(String query, List whereData) throws DAOException
    {	
		return commonDao.findList(query, whereData.toArray());
	}
	
   	public List requestgetListData(String queryCode, Object[] objs) throws DAOException
   	{	   	 	
   		return commonDao.findList(queryCode,objs);
   	}
    
	/**
     * 저장품ID 항목을 가지고
     * 저장품정보를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStockInfo(String stl_no) throws DAOException
    {
		Object[] params = { stl_no };	
		return commonDao.findByPrimaryKey(getStockInfo, params);
	}
    
    /**
     * 작업예약 TABLE에 있는 Regacy 작업예약을 삭제한다.
	 * 
     * @return 
     * @throws DAOException
     */	
    public int deleteAllWbookId(String methodNm) throws DAOException
    {
		Object[] params = { methodNm };
		
		commonDao.updateData(deleteAllWbookId,params);			//TB_YF_WRKBOOK 수정
		return commonDao.updateData(deleteAllWbookId2,params);	//TB_YF_WRKBOOKMTL 수정
    }
	
	/**
     * 작업예약ID와 저장품ID 정보를 가지고
     * 스케쥴 정보를 가져온다.	
	 * 
     * @param String	: 작업예약ID
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSchInfoWithWbookId(String sWbookId, String sStockId) throws DAOException
    {	
		Object[] params = {sWbookId,sStockId};	
		return commonDao.findByPrimaryKey(getSchInfoWithWbookId, params);
	}
    
    /**
     * 저장품 TABLE 에서 이동관련 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 작업예약ID
     * @param String	: 차량카드번호
     * @param String	: 운송지시일자
     * @param String	: 운송지시일련번호
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockTransInfo_02
	(
		String sStl_no,
		String sYdWbookId,	
		String sCarCardNo,
		String sFrtomoveWordNo,
		String sTransWordNo,
		String sStockMoveTerm) throws DAOException
	{	
		Object[] params = {sCarCardNo, sFrtomoveWordNo, sTransWordNo, sTransWordNo, sStockMoveTerm, sStl_no};	
		return this.updateData(updateStockTransInfo_02, params);
	}
	
	/**
     * 저장품ID에 해당하는 적치단 Table의 
     * Stack_layer_stat 값을 Update한다.
     *
     * @param String	: 적치단활성상태
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStackLayerStatWithStockId(String sStackLayerStat, String sStl_no) throws DAOException
	{	
		Object[] params = {sStackLayerStat, sStl_no};	
		return this.updateData(updateStackLayerStatWithStockId, params);
	}
	
	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param JDTORecord 
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock2(JDTORecord inRec, int intGp) throws DAOException, JDTOException 
	{
		String	v_del_Yn = "";

		int		count	 = 0;
		String	v_stl_no = StringHelper.evl(inRec.getFieldString("STL_NO"), "");

		if (intGp == 0) 
		{
			v_del_Yn = "Y";
		}

		try 
		{
			count = commonDao.updateData(updYdStock2_2, new Object[] { v_del_Yn, v_stl_no });
		} 
		catch (Exception e) 
		{
			throw new DAOException(e.getMessage(), e);
		}
		
		return count;
	}
	
	/**
     * 스케줄 번호 가져오기
     * @param STL_NO	재료번호
     * @return
     */
    public JDTORecord getSchSearch(String stl_no) 
    {
		return commonDao.findByPrimaryKey(getSchSearch, new Object[]{ stl_no });        
    }
    
    /**
     * Coil 공통 Table 저장위치를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 권하위치
     *
     * @return int
     * @throws DAOException
     */			
	public int updateCoilCommonLocInfo(String sStockId, String sPutLoc) throws DAOException
	{
		Object[] params = {sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sStockId,sStockId};	
		return commonDao.updateData(updateCoilCommonLocInfo ,params);
	}
	
	/**
     * 저장품ID 항목을 가지고
     * YMDM001 - Coil을 제품 야드로 입고시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getYMDM001Info(String sStl_no) throws DAOException
    {	
		Object[] params = {sStl_no, sStl_no};	
		return commonDao.findByPrimaryKey(getYMDM001Info, params);
	}
    
	/**
	 *      [A] 오퍼레이션명 : 공통야드 코드 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYfCode(GridData gdReq) throws DAOException
	{
		String methodNm = "코드조회[YfCommDAO.getYfCode] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";
	
		try
		{
			String jspeed_query_id = "";
			Object[] param = null;
			
			String itmGp = commUtils.trim(gdReq.getParam("V_ITM_GP")); //코드항목구분
	
			commUtils.printLog(logId, "조회[YfCommDAO.jspSelect] 결과 건수: " + itmGp , "DB");
			
			if ("YD_BAY_GP".equals(itmGp)) 
			{
				trtNm = "동구분";
				jspeed_query_id = getCodeYdBayGp;  
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP"))	//야드구분
				};
			}
			else if ("YD_EQP_GP".equals(itmGp))
			{
				//00~99
				trtNm = "설비구분";
				jspeed_query_id = getCodeYdEqpGp;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"))	//야드적치열구분
				};
			}
			else if ("YD_LOC_GP".equals(itmGp))
			{
				//00~99, 설비
				trtNm = "위치(설비포함)구분";
				jspeed_query_id = getCodeYdLocGp;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"))	//야드적치열구분
					, commUtils.trim(gdReq.getParam("V_YD_SECT_TY"))	//설비포함여부
					, commUtils.trim(gdReq.getParam("V_YD_SECT_TY"))	//설비포함여부
				}; 				
			} 
			else if ("YD_STK_COL_NO".equals(itmGp))
			{
				trtNm = "적치열번호";
				jspeed_query_id = getCodeYdStkColNo;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"))	//야드적치열구분
				};
			}
			else if ("YD_STK_COL_BY_BAY_SECT".equals(itmGp))
			{
				trtNm = "야드구분 동구분 SPAN의 적치열조회";
				jspeed_query_id = getCodeYdStkColNo;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP"))
					,commUtils.trim(gdReq.getParam("V_BAY_GP"))
					,commUtils.trim(gdReq.getParam("V_SECT_GP"))
				};
			}		
			else if ("YD_STK_BED_NO".equals(itmGp))
			{
				trtNm = "적치Bed번호";
				jspeed_query_id = getCodeYdStkBedNo;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"))	//야드적치열구분
				};
			}
			else if ("YD_STK_LYR".equals(itmGp))
			{
				trtNm = "적치단번호";
				jspeed_query_id = getCodeYdStkLyrNo;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")),	//야드적치열구분
					commUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"))	//BED구분
				};
			}
			else if ("YD_EQP_ID_CR".equals(itmGp))
			{
				trtNm = "크레인설비ID";
				jspeed_query_id = getCodeYdEqp;
				param = new Object[] 
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드구분
					commUtils.trim(gdReq.getParam("V_YD_BAY_GP"))	//동구분
				};
			}
			else if ("YD_EQP_ID_TC".equals(itmGp))
			{
				trtNm = "대차설비ID";
				jspeed_query_id = getCodeYdEqpTc;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP"))	//야드구분
				};
			}
			else if ("YD_EQP_ID_TC_BY_BAY".equals(itmGp))
			{
				trtNm = "대차설비ID";
				jspeed_query_id = getCodeYdEqpTcByBay;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드구분
					commUtils.trim(gdReq.getParam("V_YD_BAY_GP")),	//동구분
					commUtils.trim(gdReq.getParam("V_TO_BAY_GP"))	//동구분
				};
			}
			else if ("YD_SCH_CD".equals(itmGp))
			{
				trtNm = "스케줄코드";
				jspeed_query_id = getCodeYdSchCd;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드구분
					commUtils.trim(gdReq.getParam("V_YD_BAY_GP"))	//동구분
				};
			}
			else if ("YD_SCH_CD_OPRN".equals(itmGp))
			{
				trtNm = "스케줄코드";
				jspeed_query_id = getCodeYdSchCdOprn;
				param = new Object[] 
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드구분
					commUtils.trim(gdReq.getParam("V_YD_BAY_GP"))	//동구분
				};		
			}
			else if ("YD_STK_ABLE_SPAN".equals(itmGp))
			{ 
				trtNm = "적치가능Span";
				jspeed_query_id = getAbleYdLocGp;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
				};				
			}
			else if ("YD_STK_ABLE_COL".equals(itmGp))
			{ 
				trtNm = "적치가능col";
				jspeed_query_id = getAbleStkColNo;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"))	//야드적치열구분
				};				
			}
			else if ("YD_STK_ABLE_BED".equals(itmGp))
			{ 
				trtNm = "적치가능Bed";
				jspeed_query_id = getUsableBedList;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")),	//야드적치열구분
					commUtils.trim(gdReq.getParam("V_YD_STK_LYR_NO"))	//야드적치단구분
				};
			}
			else if ("YD_STK_ABLE_LYR".equals(itmGp))
			{
				trtNm = "적치가능Lyr";
				jspeed_query_id = getUsableLyrList;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")),	//야드적치열구분
					commUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")),	//야드적치열구분
					"N"	//비활성화베드 포함여부
				};
			}
			// 2020.03.31 비활성 베드도 포함한다.
			else if ("YD_STK_ABLE_LYR_NON_ACTIVE".equals(itmGp))
			{
				trtNm = "적치가능Lyr";
				jspeed_query_id = getUsableLyrList;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")),	//야드적치열구분
					commUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")),	//야드적치열구분
					"Y"	//비활성화베드 포함여부
				};
			}			
			else if ("YD_RT_SLAB".equals(itmGp))
			{
				trtNm = "야드행선";
				jspeed_query_id = getYdRtSlab;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드구분
					commUtils.trim(gdReq.getParam("V_YD_SCH_CD"))	//스케줄코드
				};
			}
			else if ("YD_RT".equals(itmGp))
			{
				trtNm = "야드행선";
				jspeed_query_id = getYdRt;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드구분
					commUtils.trim(gdReq.getParam("V_YD_SCH_CD"))	//스케줄코드
				};
			}
			else if ("STKCOL".equals(itmGp))
			{
				trtNm = "TB_YF_STKCOL 조회";
				jspeed_query_id = getStkCol;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드 구분
					commUtils.trim(gdReq.getParam("V_BAY_GP")),		//동 구분
					commUtils.trim(gdReq.getParam("V_SECT_GP")),		//구역 구분
					commUtils.trim(gdReq.getParam("V_COL_GP"))		//구역 구분
				};
			}
			else if ("YD_SLB_CR_ID".equals(itmGp))
			{
				trtNm = "박판열연 SLAB 크래인조회";
				jspeed_query_id = getCraneCd01;
				param = new Object[]{};
			}
			else if ("YD_SLB_WRK_KIND".equals(itmGp))
			{
				trtNm = "박판열연 SLAB 작업종류조회";
				jspeed_query_id = getSlabWorkKindCode;
				param = new Object[]{};
			}
//			2019. 12. 10 사용안함
//			else if ("YD_SECT_GP".equals(itmGp))
//			{
//				trtNm = "박판열연 SLAB SPAN구분조회";
//				jspeed_query_id = getSectGp01;
//				param = new Object[]{
//					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드 구분
//					commUtils.trim(gdReq.getParam("V_YD_BAY_GP")),	//동 구분
//				};
//			}
			else if ("YF_RULE".equals(itmGp))
			{
				trtNm = "YF_RULE조회";
				jspeed_query_id = getYfRule;
				param = new Object[]{
					commUtils.trim(gdReq.getParam("V_REPR_CD_GP")),	//대표코드구분
					commUtils.trim(gdReq.getParam("V_CD_GP")),	//코드구분
					commUtils.trim(gdReq.getParam("V_ITEM")),	//항목
				};
			}
			else if ("CURR_PROG_CD".equals(itmGp)){
				trtNm = "VW_CM_CODES 재료진도코드";
				jspeed_query_id = getCurrProgCd;
				param = new Object[]{
					commUtils.trim(gdReq.getParam("V_YD_GP")),	//야드 구분
					commUtils.trim(gdReq.getParam("V_YD_GP")),	//야드 구분
				};
			}
			else if ("NEXT_PROC".equals(itmGp)){
				trtNm = "VW_CM_CODES 다음공정";
				jspeed_query_id = getNextProc;
				param = new Object[]{
						commUtils.trim(gdReq.getParam("V_YD_GP")),	//야드 구분
						commUtils.trim(gdReq.getParam("V_YD_GP")),	//야드 구분
				};
			}
			else if ("YD_STK_COL".equals(itmGp)){
				trtNm = "적치열 조회";
				jspeed_query_id = getColGp;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드 구분
					commUtils.trim(gdReq.getParam("V_BAY_GP")),		//동 구분
					commUtils.trim(gdReq.getParam("V_SECT_GP"))		//구역 구분
				};
			}
			else if ("PT_LOAD_LOC".equals(itmGp)){
				trtNm = "상차도위치";
				jspeed_query_id = getYdCoilCarPointBackUp;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드 구분
					commUtils.trim(gdReq.getParam("V_YD_BAY_GP")),		//동 구분
				};
			}
			else if ("WLOC_CD".equals(itmGp)){
				trtNm = "동별 차량포인트";
				jspeed_query_id = getYdCarPointByBayGp;
				param = new Object[]
				{
					commUtils.trim(gdReq.getParam("V_YD_GP")),		//야드 구분
					commUtils.trim(gdReq.getParam("V_YD_BAY_GP")),		//동 구분
				};
			}
			else
			{
				//공통코드조회
				trtNm = "[" + itmGp + "]코드";
				jspeed_query_id = getCodeCmCodes;
				param = new Object[] 
				{
					itmGp,											//코드영문ID
					commUtils.trim(gdReq.getParam("V_CD_CAT_ID"))	//코드카테고리ID
				};
			}
			
			trtNm += " : ";
			commUtils.printLog(logId, "조회[YfCommDAO.jspSelect] 결과 건수11: " + itmGp , "DB");
	
			return super.getRecordSet(jspeed_query_id, param);
		}
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	public int getYdStock(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException 
	{
		JDTORecordSet	rsTemp = null;
		JDTORecord		recPara = null;
		
		try 
		{
			recPara = this.conversionFieldname(inRec, 0);	//필드명 변환 (필드명 -> V_필드명)
			
			recPara.setField("JSPEED_QUERY_ID", getYdStockTransOrdDateA);
			
			rsTemp = super.getRecordSet(recPara);		//query execute
			
			if (rsTemp.size() > 0)
			{
				outRecSet.addAll(rsTemp);
			}
			else 
			{
				return 0;
			}
			
			return rsTemp.size();
		}
		catch(Exception e) 
		{
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 * Procedure를 실행한다.
	 * 
	 * @param queryCode
	 *            query ID
	 * @param dSQL
	 *            추가쿼리내용
	 * @param obj
	 *            IN/OUT 파리미터
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord execute(String queryCode, String dSQL, Object [][] obj) throws DAOException
	{
		return commonDao.execute(queryCode, dSQL, obj);	    
	}
	
	/**
	 *      [A] 오퍼레이션명 : UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public int updateTx(JDTORecord rcvMsg,String queryId) throws DAOException 
	{	
		String methodNm = "Transaction 분리메소드 호출 < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		int intRtnVal   = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			intRtnVal = ((Integer)ejbConn.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId })).intValue();
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	}	
	
	/**
	 *      [A] 오퍼레이션명 : UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */		
	public int updateTx(JDTORecord rcvMsg,String queryId, String logId, String mthdNm, String trtNm) throws DAOException
	{	
		String methodNm = trtNm + "[YfCommDAO.updateTx] < " + mthdNm;
		int intRtnVal = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			intRtnVal = ((Integer)ejbConn.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId })).intValue();
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	}

	/**
	 * JDTORecord 배열형태의 파라메터를 입력받아
	 *  - 다수건의 DATA를 Inert, update구문을 실행한다.
	 * @param recArrPara
	 * @param intMaxCnt
	 * @param intGp
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int[] addBatch(JDTORecord[] recArrPara, String queryId) throws DAOException, JDTOException {
		int[] intRtnVal = null;
		try {
			
			if(recArrPara.length > 0){
				//필드명 변환 (필드명 -> V_필드명)
				recArrPara[0].setField("JSPEED_QUERY_ID", queryId);
				intRtnVal = this.addBatch(recArrPara);
			}
			else{
				intRtnVal = new int[]{0};
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException( e.getMessage(), e);
		}
		return intRtnVal;	
	}
	
	/**
	 * JDTORecord 배열형태의 파라메터를 입력받아
	 *  - 다수건의 DATA를 Inert, update구문을 실행한다.
	 * @param recArrPara
	 * @param intMaxCnt
	 * @param intGp
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int[] addBatch(JDTORecord[] recArrPara) throws DAOException, JDTOException {
		int[] intRtnVal = null;
		
		try { 
			intRtnVal = super.trtProcess(recArrPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);
		}
		return intRtnVal;	
	}

	/**
	 * 야드목표행선지구분를 지정한다.
     * @param  String	:	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,제품코드
     * @return String
     * @throws  
     */		
	public String[] getYfAimRtGp_PI(String sRcvTcCode ,String sItemGp ,String sStlNo ,String sCurrProgCd) throws DAOException {
		//메세지
		String ydAimRtGp 		=null;
		
		String[] rVal 			= new String[1];
		try {
			if("P".equals(sItemGp)){
			//수신한 재료번호로 plate공통 읽기***************************************************************************************************
				if("H".equals(sCurrProgCd)){
					ydAimRtGp =sCurrProgCd + "3";	//입고대기
				}else if("J".equals(sCurrProgCd)){
					ydAimRtGp =sCurrProgCd+"3";	//반납대기
				}else if("K".equals(sCurrProgCd)){		
					ydAimRtGp =sCurrProgCd+"3";	//출하지시대기
				}else if("N".equals(sCurrProgCd)){
					ydAimRtGp =sCurrProgCd+"3";	//운송지시대기
				}else if("L".equals(sCurrProgCd)){
//					if(szRcvTcCode.equals("DMYDR021")){ //후판제품운송상차지시 - 사용안함 송정현 220321
//						ydAimRtGp =currProgCd+"6";	//상차대기 
//					}else {
//						ydAimRtGp =currProgCd+"3";	//운송대기
//					}
					ydAimRtGp = sCurrProgCd+"3";	//운송대기
				}else if("M".equals(sCurrProgCd)){
					ydAimRtGp =sCurrProgCd +"3";	//출하완료
				}	
			//***************************************************************************************************************************
			}else if(sItemGp.equals("C")){

			//***************************************************************************************************************************
			} else if(sItemGp.equals("S")){
			//수신한 재료번호로 슬라브공통을 읽기 ***************************************************************************************************
				if("H".equals(sCurrProgCd)){
					ydAimRtGp = sCurrProgCd+"1";	//입고대기
				}else if("J".equals(sCurrProgCd)){
					ydAimRtGp = sCurrProgCd+"1";	//반납대기
				}else if("K".equals(sCurrProgCd)){		
					ydAimRtGp = sCurrProgCd+"1";	//출하지시대기
				}else if("L".equals(sCurrProgCd)){
//					if(szRcvTcCode.equals("DMYDR022")){ //외판슬라브운송상차지시 - 사용안함 송정현 220321
//						ydAimRtGp =currProgCd+"4";	//상차대기 
//					}else {
//						ydAimRtGp =currProgCd+"1";	//운송대기
//					}
					ydAimRtGp = sCurrProgCd+"1";	//운송대기
				}else if("N".equals(sCurrProgCd)){
					ydAimRtGp = sCurrProgCd+"1";	//운송지시대기
				}else if("M".equals(sCurrProgCd)){
					ydAimRtGp = sCurrProgCd+"1";	//출하완료
				}			
			}
			//***************************************************************************************************************************
		} catch(Exception e) {
		}
		
		rVal[0] =ydAimRtGp ;
		return rVal;
	}	
	
	/**
     * 
	 * 야드목표행선지구분를 지정한다.
     *
     * @param  String	sItemGp :	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,JDTORecord inRecord
     *
     * @return String
     * @throws  
     */		
	public String[] getYfAimRtGp_PI(String sItemGp, JDTORecord jrRecord) throws DAOException {
		String mthdNm = "야드목표행선구분 지정[coilDao.getYdAimRtGp] < " + jrRecord.getResultMsg();
		String logId = jrRecord.getResultCode();
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");

			JDTORecordSet jsRst = JDTORecordFactory.getInstance().createRecordSet("");
			String[] rVal = new String[2];

			String sMsg       	= "";
			String ydAimRtGp  	= "";
			String ydAimRtGp2	= "";
			String sSkinPassYn  = "";
			String sCurrProgCd	= "";
			String sWorkProc   	= "";
			String sNextProc  	= ""; // 다음공정
			String sPlanProc1	= ""; // 열연계획작업코드1
			String sRcvTcCode 	= commUtils.getTcCode(jrRecord);
			String sStlNo    	= commUtils.trim(jrRecord.getFieldString("STL_NO"));

			
			 if ("C".equals(sItemGp)) {
				// 수신한 재료번호로 코일공통 읽기***************************************************************************************************
				if (!"".equals(sStlNo)) {
					
					JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, "");
					jrParam.setField("COIL_NO" , sStlNo);
					jsRst = this.select(jrParam,"com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm",logId, mthdNm, "코일공통 조회");

					if (jsRst.size() <= 0) {
						sMsg = "코일공통 SELECT Error :: [" + sStlNo + "]"+ "DO NOT EXIST";
						commUtils.printLog(logId, sMsg, "SL");
						return rVal;
					} else {
						ydAimRtGp2 	= commUtils.trim(jsRst.getRecord(0).getFieldString("YD_AIM_RT_GP2"));
						sSkinPassYn = commUtils.trim(jsRst.getRecord(0).getFieldString("SKINPASS_YN"  ));
						sCurrProgCd = commUtils.trim(jsRst.getRecord(0).getFieldString("CURR_PROG_CD" ));
						
						commUtils.printLog(logId, ydAimRtGp2 + " " + sSkinPassYn + " " + sCurrProgCd, "SL");
						
						// 진도코드 존제여부 체크
						if ("".equals(sCurrProgCd)) {
							sMsg = "진도코드가  존재  안 함";
							commUtils.printLog(logId, sMsg, "SL");
							return rVal;
						}
						
						sNextProc  = commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC"));
						sPlanProc1 = commUtils.trim(jsRst.getRecord(0).getFieldString("PLAN_PROC1"));
					}
										
				} else {
					// 진도코드
					sCurrProgCd = commUtils.trim(jrRecord.getFieldString("CURR_PROG_CD"));
				}


				if ("DMYDR005".equals(sRcvTcCode)) {
					ydAimRtGp = "K2"; // 출하지시대기
					sCurrProgCd = "K";
				} else if ("DMYDR020".equals(sRcvTcCode)) {
					ydAimRtGp = "L2"; // 운송지시
					sCurrProgCd = "L";
				} else if ("DMYDR023".equals(sRcvTcCode)
						|| "DMYDR060".equals(sRcvTcCode)) {
					ydAimRtGp = "L5"; // 상차지시
					sCurrProgCd = "L";
				} else if ("DMYDR030".equals(sRcvTcCode)) {
					ydAimRtGp = "M2"; // 출하완료
					sCurrProgCd = "M";
				} else if ("G".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 종합판정대기
				} else if ("I".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 반송대기
				} else if ("H".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 입고대기
				} else if ("Y".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "C"; // 재공충당대기(C열연정정)
				} else if ("B".equals(sCurrProgCd)) { // 지시대기

					sNextProc  = commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC"));
					
					// C 동 수입인 경우 에만 spm재와 hfl재를 분리해서 적치 한다.
					if ("H".equals(sNextProc.substring(1, 2))) {
						ydAimRtGp = sCurrProgCd + "3"; // 지시대기
					} else {
						ydAimRtGp = sCurrProgCd + "4"; // 지시대기
					}
 
				} else if ("J".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 반납대기
				} else if ("Z".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 제품충당대기
				} else if ("X".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "2"; // 경매대상선정
				} else if ("E".equals(sCurrProgCd) || "D".equals(sCurrProgCd)) {
					// 재공이송작업대기
					sNextProc  = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC" )), "");
					sPlanProc1 = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("PLAN_PROC1")), "");

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
						sWorkProc = sPlanProc1;
					}
					// 계획공정정보를 가지고 야드행선을 셋팅
					if (sWorkProc.startsWith("1")) {
						ydAimRtGp = "EA";
					} else if (sWorkProc.startsWith("5")
							|| sWorkProc.startsWith("6")) {
						ydAimRtGp = "EB";
					} else if (sWorkProc.startsWith("9S")) {
						ydAimRtGp = "ED";
					} else {
						ydAimRtGp = "EC";
					}
				} else if ("C".equals(sCurrProgCd)) {
					
					// 정정작업지시대기
					sNextProc  = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("NEXT_PROC" )), "");
					sPlanProc1 = commUtils.nvl(commUtils.trim(jsRst.getRecord(0).getFieldString("PLAN_PROC1")), "");

					if (!"".equals(sNextProc)) {
						sWorkProc = sNextProc;
					} else {
						sWorkProc = sPlanProc1;
					}

					/*
					계획공정코드
						DH C열연 D Line No3HFL C열연 D Line No3HFL(정정LINE구분 : No3HFL) 11 
						DA C열연 D Line 공냉 C열연 D Line 공냉(Hysco向) 12 
						EH C열연 E Line Hot Final C열연 E Line Hot Final(정정LINE구분:SPM2) 13 
						EK C열연 E Line Skin Pass C열연 E Line Skin Pass(정정LINE구분:SPM2) 14 
						ER C열연 E Line Recoiling C열연 E Line Recoiling(정정LINE구분:SPM2) 15 
						EA C열연 E Line 공냉 C열연 E Line 공냉(Hysco向) 16 
						FH C열연 F Line No2HFL C열연 F Line No2HFL(정정LINE구분:No2HFL) 17 
						FA C열연 F Line 공냉 C열연 F Line 공냉(Hysco向) 18 
						GA C열연 G Line 공냉 C열연 G Line 공냉(정정LINE구분:No1HFL) 19 
						GH C열연 G Line No1HFL C열연 G Line No1HFL(정정LINE구분:No1HFL) 20 
						GT C열연 G Line 수냉 C열연 G Line 수냉(정정LINE구분:No1HFL) 21 
						HH C열연 H Line Hot Final C열연 H Line Hot Final(정정LINE구분:SPM1) 22 
						HK C열연 H Line Skin Pass C열연 H Line Skin Pass(정정LINE구분:SPM1) 23 
						HR C열연 H Line Recoiling C열연 H Line Recoiling(정정LINE구분:SPM1) 24 
						HA C열연 H Line 공냉 C열연 H Line 공냉(Hysco向) 25
					야드행선구분 
						CE 작업대기(C열연 HFL)
						CF 작업대기(C열연 SPM1)
						CG 작업대기(C열연 SPM2)
						CH 작업대기(C열연#1결속대)
						CI 작업대기(C열연#2결속대) 
					*/

					// 계획공정정보를 가지고 야드행선을 셋팅 _ 추후 다시 셋팅 (C열연만 셋팅 )
					if("DH".equals(sWorkProc)||
					   "FH".equals(sWorkProc)||
					   "GA".equals(sWorkProc)||
					   "GH".equals(sWorkProc)||
					   "CA".equals(sWorkProc)||
					   "CH".equals(sWorkProc)||
					   "AA".equals(sWorkProc)||
					   "BH".equals(sWorkProc)||
					   "GT".equals(sWorkProc)){
						ydAimRtGp	= "CE";
					}else if("HH".equals(sWorkProc)||
							 "HK".equals(sWorkProc)||
							 "HR".equals(sWorkProc)){
						ydAimRtGp	= "CF";
					}else if("EH".equals(sWorkProc)||
							 "EK".equals(sWorkProc)||
							 "ER".equals(sWorkProc)){
						ydAimRtGp	= "CG";
					}else if("CK".equals(sWorkProc)||
							 "CR".equals(sWorkProc)){
						ydAimRtGp	= "CF";
					}else if("BK".equals(sWorkProc)||
							 "BR".equals(sWorkProc)){
						ydAimRtGp	= "CF";	
					}else if("AK".equals(sWorkProc)||
							 "AR".equals(sWorkProc)){
						ydAimRtGp	= "CF";	
					}else {
						ydAimRtGp	= "XX";
					}	
					if("F4".equals(ydAimRtGp2) || "F5".equals(ydAimRtGp2)) {   		//재작업인 경우 
						ydAimRtGp = ydAimRtGp2;										   //재작업인(C열연정정)
					}

				} else if ("F".equals(sCurrProgCd)) {
					ydAimRtGp = sCurrProgCd + "3"; // 판정보류 
				}

				//2pass재 작업 대상
				if ("Z".equals(sSkinPassYn) && ("C".equals(sCurrProgCd)||"D".equals(sCurrProgCd))) {
					ydAimRtGp	= "EA";
				}
			
			}
			 
			sMsg = "진도코드: " + sCurrProgCd+" 야드목표행선지구분: " + ydAimRtGp;
			commUtils.printLog(logId, sMsg, "S-");
	 
			rVal[0] = commUtils.trim(ydAimRtGp);
			rVal[1] = commUtils.trim(sCurrProgCd);

			commUtils.printLog(logId, mthdNm, "S-");

			return rVal;
			 
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

	}		

	/**
	 *      [A] 오퍼레이션명 :  Tb_YF_RULE_PI 조회
	 *      -- AS_IS SQL Name에 해당하는 TO_BE SQL Name 값을 반환한다.
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String getYfRulePI(String logId, String mthdNms,String sReprCdGp, String sDtlItemAs, String sReprCdGp1, String sCdGp1,String sItem1) throws DAOException {
		String mthdNm = "getYdRulePI 조회[YfCommDAO.getYfRulePI] < " + mthdNms;
		
		String toBeSqlNm = "";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			/**********************************************************
			* 1. TB_YF_RULE_PI 조회
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", mthdNm, "");
			jrParam.setField("REPR_CD_GP_PI"	, sReprCdGp  ); //작업구분(TB_YF_RULE_PI)
			jrParam.setField("DTL_ITEM_AS_PI"	, sDtlItemAs ); //구분(TB_YF_RULE_PI)
			jrParam.setField("REPR_CD_GP"     	, sReprCdGp1 ); //작업구분(TB_YF_RULE)
			jrParam.setField("CD_GP"     		, sCdGp1     ); //코드구분(TB_YF_RULE)
			jrParam.setField("ITEM"     		, sItem1     ); //아이템(TB_YF_RULE)

			/*
				SELECT 
				      DTL_ITEM_TO
				FROM TB_YD_RULE_PI
				WHERE REPR_CD_GP = :V_REPR_CD_GP
				   AND DTL_ITEM_AS   LIKE :V_DTL_ITEM_AS || '%'
				   AND DEL_YN     = 'N'
			*/ 
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", getYfRulePIDEV);
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);			
			
			if (jsChk.size() > 0) {
				toBeSqlNm    = commUtils.trim(jsChk.getRecord(0).getFieldString("DTL_ITEM_TO"));
			} else {
				toBeSqlNm    = sDtlItemAs;
			}
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return toBeSqlNm;

		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return toBeSqlNm;
		} catch (Exception e) {
			return toBeSqlNm;
		}
	}		
	
	/**
	 *      [A] 오퍼레이션명 :  신규 시스템 적용 여부
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String ApplyYnPI(String logId, String mthdNms,String sReprCdGp, String sCdGp,String sItem) throws DAOException {
		String mthdNm = "신규 시스템 적용여부[YfCommDAO.ApplyYnPI] < " + mthdNms;
		String szAPPLY_YN = "N";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp  ); //작업구분
			jrParam.setField("CD_GP"     , sCdGp      ); //구분
			jrParam.setField("ITEM"      , sItem      ); //ITEM

			/* 
				--com.inisteel.cim.yf.common.dao.YfCommDAO.getACoilApplyYn_PIDEV
				SELECT 'Y' AS APPLY_YN
				  FROM DUAL
				-- WHERE REPR_CD_GP = :V_REPR_CD_GP  -- APP001
				--   AND CD_GP = :V_CD_GP            -- CD_GP
				--   AND ITEM  = :V_ITEM
				--   AND DEL_YN = 'N'
			*/
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yf.common.dao.YfCommDAO.getACoilApplyYn_PIDEV");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);

			if (jsChk.size() > 0) {
				szAPPLY_YN = commUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
			}

			commUtils.printLog(logId, mthdNm, "S-");

			return szAPPLY_YN;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szAPPLY_YN;
		} catch (Exception e) {
			return szAPPLY_YN;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 :  신규 시스템 적용 여부
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String ApplyYn(String logId, String mthdNms, String sReprCdGp, String sCdGp, String sItem) throws DAOException {
		String mthdNm = "신규 시스템 적용여부[YfCommDAO.ApplyYn] < " + mthdNms;
		String szAPPLY_YN = "N";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			/**********************************************************
			 * 2. 열정보 read
			 **********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("REPR_CD_GP", sReprCdGp); //작업구분
			jrParam.setField("CD_GP", sCdGp); //구분
			jrParam.setField("ITEM", sItem); //ITEM

			/* 
				--com.inisteel.cim.yf.common.dao.YfCommDAO.getACoilApplyYn
				SELECT NVL(MAX(DTL_ITEM1),'N') AS APPLY_YN
				  FROM USRYFA.TB_YF_RULE
				 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- APP001
				   AND CD_GP = :V_CD_GP            -- CD_GP
				   AND ITEM  = :V_ITEM
				   AND DEL_YN = 'N'
			*/  
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yf.common.dao.YfCommDAO.getACoilApplyYn");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);

			if (jsChk.size() > 0) {
				szAPPLY_YN    = commUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
			}
            
			commUtils.printLog(logId, mthdNm, "S-");

			return szAPPLY_YN;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szAPPLY_YN;
		} catch (Exception e) {
			return szAPPLY_YN;
		}
	}	

	public int getCoilGp(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException {
		JDTORecordSet	rsTemp = null;
		JDTORecord		recPara = null;
		
		try {
			recPara = this.conversionFieldname(inRec, 0);	//필드명 변환 (필드명 -> V_필드명)
			
			recPara.setField("JSPEED_QUERY_ID", getCoilGp_PIDEV);
			
			rsTemp = super.getRecordSet(recPara);		//query execute
			
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
			} else {
				return 0;
			}
			
			return rsTemp.size();
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 :  운송이송구분 반환_PIDEV
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String[] getTrnFrtomoveGpPI(String logId, String mthdNms,String sTrnOrdDate, String sTrnOrdSeqno) throws DAOException {
		String mthdNm = "getTrnFrtomoveGpPI 조회[YdCommDAO.getTrnFrtomoveGpPI] < " + mthdNms;
		
		String transFrtomoveGp = "";
		String hIssueGp = "";
		String[] rVal = new String[2];
	
		try {
			commUtils.printLog(logId, mthdNm, "S+");
	
			/**********************************************************
			* 1. VW_LM_P_TRANSWORDCOMM (운송지시공통) 조회
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam("", mthdNm, "");
			jrParam.setField("TRN_ORD_DATE"		  , sTrnOrdDate  );	//  운송지시일자
			jrParam.setField("TRN_ORD_SEQNO"      , sTrnOrdSeqno ); // 운송지시순번
	
			/*
				SELECT 
				       TRANS_FRTOMOVE_GP
				     , H_ISSUE_GP
				  FROM 
				       VW_LM_P_TRANSWORDCOMM
				 WHERE TRANS_WORD_DATE  = :V_TRN_ORD_DATE
				   AND TRANS_WORD_SEQNO = :V_TRN_ORD_SEQNO
			*/ 
			//필드명 변환 (필드명 -> V_필드명)
			jrParam = conversionFieldname(jrParam, 0);			
			//query id setting
			jrParam.setField("JSPEED_QUERY_ID", "com.inisteel.cim.ydPI.dao.getTrnFrtomoveGpPi_PIDEV");
			//query execute
			JDTORecordSet jsChk = getRecordSet(jrParam);			
			
			if (jsChk.size() > 0) {
				transFrtomoveGp = commUtils.trim(jsChk.getRecord(0).getFieldString("TRANS_FRTOMOVE_GP"));
				hIssueGp = commUtils.trim(jsChk.getRecord(0).getFieldString("H_ISSUE_GP"));
			}
			
			rVal[0] = commUtils.trim(transFrtomoveGp);
			rVal[1] = commUtils.trim(hIssueGp);
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return rVal;
	
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return rVal;
		} catch (Exception e) {
			return rVal;
		}		
	}
	
	/**
	 *      [A] 오퍼레이션명 : 프로시져 호출 메소드
	 * 
	 * @param  Object[] 		inParam 		procedure input parameter array
	 *         int[]   	 		inParamIndex   	procedure input parameter seq array 
	 *         String    		queryId   		QueryId 
	 * @return JDTORecord		procedure Result
	 * @throws DAOException
	 * @throws JDTOException 
	 */
	public JDTORecord callProcedure( Object[] inParam, int[] inParamIndex , String queryId) throws DAOException, JDTOException {
		try {
			DBAssistantDAO assistantDAO = new DBAssistantDAO();
			
			String add_query = "";

	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {inParamIndex.length+1};

			return assistantDAO.trtProcedure(queryId, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);
			
	 		
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}
}