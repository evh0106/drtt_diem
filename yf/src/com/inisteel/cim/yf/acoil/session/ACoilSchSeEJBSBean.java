/**
 * @(#)ACoilRcvL3SeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 COIL 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.acoil.session;

import java.util.List;

import com.inisteel.cim.common.eai.EAIHttpSender;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.po.YMPO161;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.YfQueryIF;
import com.inisteel.cim.yf.common.YfQueryIF2;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.common.session.YfComm;

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
 *      [A] 클래스명 : 박판열연 COIL 스케줄 처리
 *
 * @ejb.bean name="ACoilSchSeEJB" jndi-name="ACoilSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class ACoilSchSeEJBSBean extends BaseSessionBean implements YfQueryIF, YfQueryIF2 {
	
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
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	
	
	
	
    /**
	 * 오퍼레이션명 : 비동기식 테스트
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @Asynchronous
	 */
	public void asyncTest(JDTORecord rcvMsg) throws DAOException { 
		System.out.println("---------------------------->asyncTest start");
		try
		{
			for(int i =0; i<100; i++)
			{
				logger.println("asyncTest------>"+i);
				Thread.sleep(1000);
			}
			
			String error = null;
			logger.println(""+error.equals("1"));
		}
		catch(Exception e)
		{
			logger.println("==================asyncTest catch");
			throw new DAOException(commUtils.makeErrorLog("에러테스트 어디까지 가나", "asyncTest", e));
		}
		
	} 
	
	
	
	

    
    
	/**
     * 압연실적 송신전문MESSAGE를 구성한다.
     *
     * @param JDTORecord : 공통코일정보
     * @param String     : 저장품ID
     * @param String     : 작업구분("" - 정상, 0 - take in, 1 - take out)
     * 
     * @return
     * @throws  
     */	
	private boolean callCoilMsgInfo(JDTORecord stockV,
							        String sGoodsNo,
							        String sGbn){
		
		Boolean isSuccess = new Boolean(false);
		CraneSchDAO dao	= new CraneSchDAO();
		String sMessage   = "";
		String sDEMANDER_NM = "";
		String sHRMILL_CMPL_DT = "";
		String sNEXT_PROC = "";
		try{
			
			String sPlantGp		= StringHelper.evl(stockV.getFieldString("공장구분"),"");		
	    	String sOrdNo		= StringHelper.evl(stockV.getFieldString("제작번호"),"");		
	    	String sOrdDtl		= StringHelper.evl(stockV.getFieldString("제작행번"),"");		
	    	String sCoilT		= StringHelper.evl(stockV.getFieldString("코일두께"),"");		
	    	String sCoilW		= StringHelper.evl(stockV.getFieldString("코일폭"),"");		
	    	String sCoilLen		= StringHelper.evl(stockV.getFieldString("코일길이"),"");		
	    	String sCoilOutdia	= StringHelper.evl(stockV.getFieldString("코일외경"),"");		
	    	String sCoilWt		= StringHelper.evl(stockV.getFieldString("코일중량"),"");		
    		String sBranchCd 	= StringHelper.evl(stockV.getFieldString("분기위치코드"),"");
    		String sExBranchCd	= StringHelper.evl(stockV.getFieldString("확장분기위치코드"),"");
			String sCoolMethod 	= StringHelper.evl(stockV.getFieldString("냉각방법"),"");
						
			logger.println(LogLevel.DEBUG,this,"sPlantGp ="	+sPlantGp);	   	
			logger.println(LogLevel.DEBUG,this,"sOrdNo ="	+sOrdNo);	   	
			logger.println(LogLevel.DEBUG,this,"sOrdDtl ="	+sOrdDtl);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilT ="	+sCoilT);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilW ="	+sCoilW);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilLen ="	+sCoilLen);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilOutdia="+sCoilOutdia);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilWt ="	+sCoilWt);	   	
			logger.println(LogLevel.DEBUG,this,"sBranchCd ="	+sBranchCd);	   	
			logger.println(LogLevel.DEBUG,this,"sExBranchCd ="	+sExBranchCd);	   	
			logger.println(LogLevel.DEBUG,this,"sCoolMethod ="	+sCoolMethod);	   	
						
			if(YfConstant.PLANT_GP_A.equals(sPlantGp)||
			   YfConstant.PLANT_GP_H.equals(sPlantGp)){//A열연 압연실적 
			
				sMessage = setACoilMsgInfo(sGoodsNo,
										   sOrdNo,
										   sOrdDtl,
										   sCoilT,
										   sCoilW,
										   sCoilLen,
										   sCoilOutdia,
										   sCoilWt,
										   sGbn);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("THHC171send",new Class[]{String.class},new Object[]{ sMessage });	
				
			}
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	
	
	
	/**
	 *  A열연 실적송신 Interface
	 *  Source	야드L3	Target	야드L2	I/F방법	JMS	I/F주기	REQ	I/F유형	
	 *  T/C	THHC171	
	 *  1	전문코드		CHAR	07	전문코드    	
	 *  2	KEY				CHAR	05	KEY         	
	 *  3	군정보			CHAR	01	군정보      	
	 *  4	구분			CHAR	02	구분        	SPACE:정상실적, 1:TAKE OUT, 2:TAKE IN
	 *  5	코일번호		CHAR	10	코일번호    	
	 *  6	제작번호/행번	CHAR	13	제작번호행번	
	 *  7	두께			CHAR	05	두께 ㎜	Coil 공통 두께 * 1000 	
	 *  8	폭				CHAR	05	폭   ㎜	Coil 공통 폭에 정수만 가져와서 5자리로 만듦
	 *  9	길이			CHAR	05	길이 Cm	Coil 공통 길이 * 10	
	 *  10	외경			CHAR	05	외경 Coil 공통 그대로       	
	 *  11	중량			CHAR	05	중량 Coil 공통 그대로       	
	 *  12	SPARE			CHAR	137	SPARE     
	 *  
     * @param schInfo : 압연실적 실적송신 INFO
     * @return
     * @throws 
     */	 
	private String setACoilMsgInfo(String sGoodsNo,
								   String sOrdNo,
								   String sOrdDtl,
								   String sCoilT,
								   String sCoilW,
								   String sCoilLen,
								   String sCoilOutdia,
								   String sCoilWt,
								   String sGbn){
		
		StringBuffer sMsg = new StringBuffer();

		String TC			= ""; 
		String sKey			= ""; 
		String sGROUP		= ""; 
		String GBN			= ""; 
		String COILNO		= ""; 
		String ORDNODTL		= ""; 
		String COILT		= ""; 
		String COILW		= ""; 
		String COILOUTDIA	= ""; 
		String COILWT		= ""; 
		String COILLEN		= ""; 
		String SPARE		= ""; 
				
		int iTC				=  7; 
		int iKey			=  5; 
		int iGROUP			=  1; 
		int iGBN			=  2; 
		int iCOILNO			= 10; 
		int iORDNODTL		= 13; 
		int iCOILT			=  5; 
		int iCOILW			=  5; 
		int iCOILOUTDIA		=  5; 
		int iCOILWT			=  5; 
		int iCOILLEN		=  5; 
		int iSPARE			=137; 
						   
		try{
			//VALUE SETTING
			TC			= "THHC171";
			sKey		= "LHCVO"; 
			sGROUP		= "2"; 
			GBN			= sGbn; // space:압연실적,0:take in,1:take out 
			COILNO		= sGoodsNo; 
			ORDNODTL	= sOrdNo+sOrdDtl; 
			COILT 		= Double.valueOf((Double.parseDouble(sCoilT) * 1000) + "").longValue()+"";
			COILW 		= Double.valueOf(sCoilW).longValue()+ "";
			COILLEN		= (Long.parseLong(sCoilLen)* 10) + "";
			COILOUTDIA	= sCoilOutdia; 
			COILWT		= sCoilWt; 
			SPARE		= ""; 
			
			sMsg.append(commUtils.FillToString(TC		    ,iTC));
			sMsg.append(commUtils.FillToString(sKey			,iKey));
			sMsg.append(commUtils.FillToString(sGROUP	    ,iGROUP));
			sMsg.append(commUtils.FillToString(GBN		    ,iGBN));
			sMsg.append(commUtils.FillToString(COILNO	    ,iCOILNO));
			sMsg.append(commUtils.FillToString(ORDNODTL		,iORDNODTL));
			sMsg.append(commUtils.FillToNumber(COILT		    ,iCOILT));
			sMsg.append(commUtils.FillToNumber(COILW		    ,iCOILW));
			sMsg.append(commUtils.FillToNumber(COILLEN	    ,iCOILLEN));
			sMsg.append(commUtils.FillToNumber(COILOUTDIA    ,iCOILOUTDIA));
			sMsg.append(commUtils.FillToNumber(COILWT	    ,iCOILWT));
			sMsg.append(commUtils.FillToString(SPARE	    	,iSPARE));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	
	
	
	
    /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * A열연 압연실적처리 저장품에 대해 
        * 작업예약을 호출한다.
        *
        * param String : 저장품ID
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */         
	public boolean callCoilWbookInfo(String stockId,
								     String sStockMoveTerm){
		
		boolean isSucess = false;
		
		try{
			
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
			// 적치단(TB_YM_STACKLAYER) Table Read 
    		// YD_STK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), YD_STK_LYR_STAT(적치단 상태 L:적치중)
            // Select YD_STK_COL_GP, substr(YD_STK_COL_GP,1,1) YD_STK_COL_GP1, substr(YD_STK_COL_GP,2,1) YD_STK_COL_GP2 
    		// From TB_YM_STACKLAYER 
    		// Where STOCK_ID = ? And YD_STK_LYR_STAT = "L" (적치단 상태 L:적치중)    	
			String stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
			JDTORecord StackColGp = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ stockId });

			logger.println(LogLevel.DEBUG,this, "1======================");
			logger.println(LogLevel.DEBUG,this, "StackColGp="+ StackColGp);
			logger.println(LogLevel.DEBUG,this, "1======================");
			
			if(StackColGp == null){ 	
				throw new EJBServiceException("=재료정보=>적치단 정보 존재안함.");
			}
				
			String stackCol  = StringHelper.evl(StackColGp.getFieldString("YD_STK_COL_GP"), "");
			String stackCol1 = StringHelper.evl(StackColGp.getFieldString("YD_STK_COL_GP1"), "");
			String stackCol2 = StringHelper.evl(StackColGp.getFieldString("YD_STK_COL_GP2"), "");
			
			logger.println(LogLevel.DEBUG,this, "2======================================================");
			logger.println(LogLevel.DEBUG,this, "stackCol="+ stackCol + "/" + stackCol1 + "/" + stackCol2);
			logger.println(LogLevel.DEBUG,this, "2======================================================");
			
			if (stackCol != null && !stackCol.equals("")){
				
                // 적치단  Table Update(작업요구상태='S'로 변경)
				// UPDATE TB_YM_STACKLAYER SET YD_STK_LYR_STAT = 'S' WHERE STOCK_ID = ?
				String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
				int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ YfConstant.STACK_LAYER_STAT_S, 
																								 stockId.trim() });
				
				logger.println(LogLevel.DEBUG,this, "3=======================");
				logger.println(LogLevel.DEBUG,this, "stockId="+ stockId.trim()+ "stkColGp="+stkColGp);
				logger.println(LogLevel.DEBUG,this, "3=======================");

				
				// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
				String wBookQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
				JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);
				String wBookid      = wBookSel.getFieldString("WBOOK_ID");
				
				logger.println(LogLevel.DEBUG,this, "5=================");
				logger.println(LogLevel.DEBUG,this, "wBookid="+ wBookid );
				logger.println(LogLevel.DEBUG,this, "5=================");

				String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				
				// 1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In
				int wbookstockId = 0;
				wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
																						 stackCol1, 
																						 stackCol2, 
																						 YfConstant.NEW_SCH_WORK_KIND_CDLO, 
																						 commUtils.getWorkDuty(),
																						 commUtils.getWorkParty()});	
				
				logger.println(LogLevel.DEBUG,this, "6============================================");
				logger.println(LogLevel.DEBUG,this, "stackCol1="+ stackCol1+ " stackCol2="+stackCol2);
				logger.println(LogLevel.DEBUG,this, "6============================================");
 
				// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
				// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
				String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				
				int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
																			       sStockMoveTerm, 
																			       stockId.trim() });	
				
				logger.println(LogLevel.DEBUG,this, "7======================================");
				logger.println(LogLevel.DEBUG,this, "stockId="+ stockId.trim()+ "stkId="+stkId);
				logger.println(LogLevel.DEBUG,this, "7======================================");
				
			}			
			isSucess = true;	 
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSucess; 
	}
	
	
	
	
	
	
	
    /**
	 * 오퍼레이션명 : 
	 *  
	 * 최규성 
        * 코일공통 테이블에서 요구 받은 코일에 대한 정보를 검사하여 SPM2 관련인지 판단한다.
        *
        * param String	: 저장품ID
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public String checkCoilCommonInfo(String sCoilNo){
    	String sVal = "";
    	String sEqpGp = "";
    	String sPlntGp	="";
    	logger.println(LogLevel.DEBUG,this,"=============코일공통에서 정보 수집 판별 시작========");
    	try{
    		JDTORecord stockV 	= null;
    		YdStockDAO dao	= new YdStockDAO();
    		String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilCommonInfo_SPM2";
    		String[] sTotalPassProc = new String[5];
    		String sFinalPassProc = "";
    		/**
    		1. 공통 코일 정보를 가져온다.
    		*/
    		stockV = dao.getData(sQueryId, new Object[]{sCoilNo});
//    		stockV = dao.getCoilCommonInfo(sCoilNo); // 쿼리가 변경됨. 최규성 운영계 반영시 주의. 
    		if (stockV == null)
    		{
    			logger.println(LogLevel.DEBUG,this,"코일공통테이블에 코일 정보가 존재하지 않음");
    			return sVal;
    		}
    		
    		sPlntGp = StringHelper.evl(stockV.getFieldString("공장구분"),"-");
			sEqpGp = StringHelper.evl(stockV.getFieldString("YD_EQP_GP"),"-");
			if(sPlntGp.equals("A") && sEqpGp.equals("QE")){
				sFinalPassProc ="1Q";
			}else{

	    		/**
	    		2. 통과공정의 정보를 비교한다.
	    		*/
	    		// COILCOMM TBL의 통과공정 1~5 중 가장 마지막에 입력된 통과공정을 비교하여야 한다. 최규성. 
	    		for(int i=0; i < 5; i++)
	    		{	 
	    			sTotalPassProc[i] = StringHelper.evl(stockV.getFieldString("통과공정"+String.valueOf(i+1)),"-");		// SPM2관련 추가 최규성
	    			if( (sTotalPassProc[i].equals("-") || sTotalPassProc[i].equals("")	)/* && i != 0*/)
	    			{
	    				logger.println(LogLevel.DEBUG,this,"A통과공정"+String.valueOf(i+1)+"은 "+sFinalPassProc );
	    				break;
	    				//sFinalPassProc = "";
	    				
	    			}else{
	    				sFinalPassProc = sTotalPassProc[i];
	    				logger.println(LogLevel.DEBUG,this,"B통과공정"+String.valueOf(i+1)+"은 "+sFinalPassProc );
	    				
	    			}
	    		}
			}

    		/**
    		3. 통과공정 정보가 6K[SPM2] 일 경우 결과값 6K
    			아닐 경우 결과값 "" 반환.
    		*/
    		sVal = sFinalPassProc;

    	}catch(DAOException daoe){
    		throw daoe;
    	}catch(Exception e){
    		throw new EJBServiceException(e);
    	}    
    	logger.println(LogLevel.DEBUG,this,"=============코일공통에서 정보 수집 판별 종료========");
    	logger.println(LogLevel.DEBUG,this,"결과값 : "+sVal);

    	return sVal;
    }
    
	
	/**	
	 *      [A] 오퍼레이션명 : 코일크레인스케줄(YFYFJ302)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYFYFJ302(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일크레인스케줄MAIN[ACoilSchSeEJB.rcvYFYFJ302] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn = this.procYFYFJ302(rcvMsg);
			
			commUtils.printLog(logId, methodNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	
	/**
	 * [A] 오퍼레이션명 :  명령선택기동 (YFYFJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvYFYFJ001(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "명령선택기동[ACoilRcvL3SeEJBSBean.rcvYFYFJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "명령선택기동 수신 ", rcvMsg);

			
			//크레인설비ID
	    	String szEqpId 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));
	    	String sWPROG_STAT = "";
	    	if (!"".equals(szEqpId)) 
	    	{
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_EQP_ID" , szEqpId);
				JDTORecordSet rsResult = commDao.select(jrParam, getYmEqp);
				JDTORecord jrEqpInfo = null;

				if (rsResult.size() > 0) 
				{
					rsResult.first();
					jrEqpInfo = rsResult.getRecord();

					sWPROG_STAT = commUtils.trim(jrEqpInfo.getFieldString("YD_EQP_PROG_STAT"));          // 설비 상태
					commUtils.printLog(logId, "명령선택 호출 1차판단: 설비ID[" + szEqpId + "], 설비상태[" + sWPROG_STAT + "]", "SL");

					
					
					//설비상태가 W가 아니면, 진행중인 스케줄이 있는지 한차례더 검증한다.
					if(!"W".equals(sWPROG_STAT)) 
					{
						rsResult = commDao.select(jrParam, getYmEqpSchCnt);
						
						//진행중인 스케줄이 없으면 W로 갱신한다.
						if (rsResult.size() < 1) 
						{
							jrParam.setField("YD_EQP_PROG_STAT", "W"); //권상작업지시
			        		commDao.update(jrParam, updStatEqp2, logId, methodNm, "설비상태 수정");
			        		
			        		sWPROG_STAT = "W";
						}
					}
				}
				
				commUtils.printLog(logId, "명령선택 호출 최종판단: 설비ID[" + szEqpId + "], 설비상태[" + sWPROG_STAT + "]", "SL");			
				
				if ("W".equals(sWPROG_STAT)) 
				{
			    	JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setResultCode(logId);	//Log ID
			    	recInTemp.setResultMsg(methodNm);	//Log Method Name
			    	recInTemp.setField("JMS_TC_CD",			"F1YFL007") ;	//크레인작업지시요구
			    	recInTemp.setField("YD_EQP_ID",			commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")));		//야드설비ID
			    	recInTemp.setField("YD_WRK_PROG_STAT",	commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")));	//야드작업진행상태(권상작업지시)
			    	recInTemp.setField("YD_SCH_CD",			commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")));		//야드스케쥴코드
			    	recInTemp.setField("YD_CRN_SCH_ID",		commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")));	//야드크레인스케쥴ID

		    		EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
		    		jrRtn = (JDTORecord)ejbConn.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
		    	
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
	 *      [A] 오퍼레이션명 : 코일크레인스케줄(YFYFJ302)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procYFYFJ302(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일크레인스케줄MAIN[ACoilSchSeEJB.procYFYFJ302] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
		int intRtnVal = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg);								//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			String ydSchCd    = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    )); //야드스케쥴코드
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID

			if ("".equals(modifier)) { modifier = msgId; }

			commUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "], 설비ID[" + ydEqpId + "], 작업예약ID[" + ydWbookId + "], 수정자[" + modifier + "]", "SL");

			JDTORecord jrRtn = null;	 //전문 Return
			String trtMsg  = ""; 		 //처리메세지
			String ydL3Msg = ""; 		 //야드L3MESSAGE
			String stackLayerChkYn = ""; //적재위치 CHECK 여부
			
			
			
			/*********************************************************
			 * 1. 스케줄 재료없이 헤더만 있는 스케줄 정리
			 * -발생사유) 동시 트랜잭션 발생
			 *********************************************************/
			JDTORecord jrParam1	= JDTORecordFactory.getInstance().create();
			jrParam1.setField("MODIFIER"   , modifier ); //수정자
			yfComm.execQueryId(jrParam1, delCrnSch);
			
			
			
			/**********************************************************
			* 2. 워크북id, 스케줄코드, 설비ID 정합성 체크
			* 
			* - 파라미터 종류
			*   1) 워크북id, 스케줄코드, 설비ID (차량도착인 경우)
 			*   2) 스케줄코드, 설비ID
 			*   3) 설비ID
			**********************************************************/
			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"  , ydSchCd  ); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
			jrParam.setField("MODIFIER"   , modifier ); //수정자
			
			
			//스케줄코드 Check 
			if ("".equals(ydWbookId) && !"".equals(ydSchCd)) 
			{
				JDTORecord jrChk = yfComm.chkSchCd(jrParam);
				
				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) 
				{
					ydSchCd = "";
				}
			}
			//설비ID Check
			if ("".equals(ydWbookId) && "".equals(ydSchCd) && !"".equals(ydEqpId)) 
			{
				JDTORecord jrChk = yfComm.chkEqpStat(jrParam);

				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) 
				{
					ydEqpId = "";
				}
			}
			
			
			/**********************************************************
 			 * 3. 작업예약id get
 			 * 
 			 *  - 작업예약id와 STACK_LAYER_CHK_YN(적치단정합성체크유무) 을 get한다. 
 			 *  - 1열연에서는 YD_SCH_DIV_GP을 대체 사용했다.
 			 *
			**********************************************************/			
			//작업예약ID 조회
			if (!"".equals(ydWbookId)) 
			{
				jsWbook = commDao.select(jrParam, getCrnSchWbook, logId, methodNm, "작업예약 조회");
			} 
			else if (!"".equals(ydSchCd)) 
			{
				jsWbook = commDao.select(jrParam, getCrnSchWbookSchcd, logId, methodNm, "작업예약 조회");
			} 
			else if (!"".equals(ydEqpId)) 
			{
				jsWbook = commDao.select(jrParam, getCrnSchWbookEqp, logId, methodNm, "작업예약 조회");
			} 
			else 
			{
				throw new Exception("오류:작업예약ID조회 항목 없음");
			}

			if (jsWbook == null || jsWbook.size() < 1) 
			{
				commUtils.printLog(logId, trtMsg + " >> 작업예약정보 없음", "SL");
				return jrRtn;
			} 
			
			
			ydWbookId 		= commUtils.trim(jsWbook.getRecord(0).getFieldString("YD_WBOOK_ID"));
			stackLayerChkYn = commUtils.trim(jsWbook.getRecord(0).getFieldString("STACK_LAYER_CHK_YN"));

			commUtils.printLog(logId, trtMsg + " >> 결정된 작업예약ID [" + ydWbookId + "]", "SL");

			
			
			/**********************************************************
			* 4. 작업예약 재료에 현재 적치단 저장위치 Update (별도 Transaction 으로 처리)
			**********************************************************/
			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			EJBConnector tranConn = new EJBConnector("default", "ACoilSchSeEJB", this);
			tranConn.trx("updCrnSchWB", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			
			
			
			/**********************************************************
			* 5. 스케줄수행판단 모듈
			*  - 스케줄수행에 필요한 데이터를 get
			*  - 스케줄수행에 필요한 정합성 check
			**********************************************************/
			//조회된 작업예약ID로 상태정보 Check
			String ydToLocDcsnMtd = ""; 	//야드To위치결정방법
			String ydToLocGuide   = ""; 	//야드To위치Guide
			String toLocChkGp     = ""; 	//To위치 점검을 위한 구분(G:To위치Guide, C:차량상차, T:대차상차)
			String trnEqpCd       = ""; 	//운송장비코드
			String ydEqpStat      = "";		//야드설비상태
			String ydSchPrior     = "";
			String WbRegister     = "";     //작업예약 등록자
			
			int ttMtlSh;			//전체 재료매수
			int wmMtlSh;			//작업예약 재료매수
			int stMtlSh;			//저장품 재료매수
			int slMtlSh;			//적치단 재료매수
			int statCSh;			//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
			int abLocSh;			//저장위치이상 재료매수   
			JDTORecordSet jsChk = commDao.select(jrParam, getCrnSchStat, logId, methodNm, "작업예약 조회");
			
			if (jsChk.size() <= 0) 
			{
				throw new Exception("오류:" + trtMsg + " >> 상태정보 없음");
			} 
		
			JDTORecord jrChk = jsChk.getRecord(0);

			ydSchCd                = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"          ));	//야드스케쥴코드
			ydToLocDcsnMtd         = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD" ));	//야드To위치결정방법
			ydToLocGuide           = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide
			toLocChkGp             = commUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분
			ydSchPrior             = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PRIOR"   ));	//야드스케쥴우선순위
			WbRegister             = commUtils.trim(jrChk.getFieldString("WB_REGISTER"    ));		//작업예약 등록자
			String ydWrkPlanCrn    = commUtils.trim(jrChk.getFieldString("YD_WRK_PLAN_CRN"    ));	//야드작업계획크레인
			String ydEqpStatPln    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//야드설비상태(작업계획크레인)
			String ydEqpWrkModePln = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//야드설비작업Mode(작업계획크레인)
			String ydWrkCrn        = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//야드작업크레인
			String ydEqpStatWrk    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//야드설비상태(작업크레인)
			String cmDupYn         = commUtils.trim(jrChk.getFieldString("CM_DUP_YN"          ));	//크레인스케줄 재료중복여부
			String clDupGp         = commUtils.trim(jrChk.getFieldString("CL_DUP_GP"          ));	//크레인스케줄 저장위치중복여부
			String upDnGp          = commUtils.trim(jrChk.getFieldString("UP_DN_GP"           ));	//크레인스케줄 저장위치중복여부
			String sYD_ALT_CRN     = commUtils.trim(jrChk.getFieldString("YD_ALT_CRN"         ));	//대체크레인
			String sYD_EQP_STAT_ALT= commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_ALT"    ));	//대체크레인 설비상태
			String sZONE_YN		   = commUtils.trim(jrChk.getFieldString("ZONE_YN"    ));	//복포존 유무
			
			String sYD_EQP_WRK_MODE_PLN = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));//작업계획크레인 야드설비작업Mode
			String sYD_EQP_WRK_MODE_WRK = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_WRK"));//작업크레인 야드설비작업Mode
			String sYD_EQP_WRK_MODE_ALT = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_ALT"));//대체크레인 야드설비작업Mode
			
			ttMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
			wmMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
			stMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
			slMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
			statCSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
			abLocSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수
			String sYM_CRANE_TRAVL_PROH = commUtils.trim(jrChk.getFieldString("YM_CRANE_TRAVL_PROH"    ));	//크레인 주행금지구간
			
			
			if (wmMtlSh == 0) {
				throw new Exception("오류:" + trtMsg + " >> 작업예약재료 정보 없음");
			} else if (wmMtlSh != ttMtlSh) {
				throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]");
			} else if (wmMtlSh != stMtlSh) {
				throw new Exception("오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]");
			} else if ("Y".equals(cmDupYn)) {
				throw new Exception("오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복");
			} else if ("1".equals(clDupGp)) {
				throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복");
			} else if ("2".equals(clDupGp)) {
				throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복");
			} else if ("Y".equals(upDnGp)) {
				throw new Exception("오류:" + trtMsg + " >> 상단에 권하 작업이 있습니다. 불가합니다.");
			} else if ("Y".equals(sYM_CRANE_TRAVL_PROH)) {
				throw new Exception("오류:" + trtMsg + " >> 권상위치가 크레인 금지구간으로 설정되었습니다. 불가합니다.");
			} else if ("X".equals(sZONE_YN)) {
				throw new Exception("오류:" + trtMsg + " >> 권상위치가 복포존 입니다. 불가합니다.");
			}
			
			if ("Y".equals(stackLayerChkYn)) { 
				if (wmMtlSh != slMtlSh) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 정보 이상 [" + (wmMtlSh - slMtlSh) + "매]");
				} else if (wmMtlSh != statCSh) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치중[C]이 아님 [" + (wmMtlSh - statCSh) + "매]");
				} else if (abLocSh > 0) {
					throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치 이상 [" + abLocSh + "매]");
				} 
			} 
			
			
			/**********************************************************
			* 6. To위치 사전 점검
			*   -차량 상차작업일 경우, 차량 정보 확인
			**********************************************************/
			//To위치 사전 점검
			if ("C".equals(toLocChkGp)) 
			{
				//차량상차작업
				String ydCarUseGp = commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP"));	//야드차량사용구분
			           trnEqpCd   = commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"   ));	//운송장비코드
				String carNo      = commUtils.trim(jrChk.getFieldString("CAR_NO"       ));	//차량번호

				if ("".equals(ydCarUseGp)) 
				{
					throw new Exception("오류:" + trtMsg + " >> 차량상차작업 야드차량사용구분 없음");
					
				} 
				else if ("L".equals(ydCarUseGp) && "".equals(trnEqpCd)) 
				{
					// 구내운송
					throw new Exception("오류:" + trtMsg + " >> 구내운송 상차작업 운송장비코드 없음");
				}
				else if ("G".equals(ydCarUseGp)) 
				{
					// 출하
					if ("".equals(carNo)) 
					{
						throw new Exception("오류:" + trtMsg + " >> 출하차량 상차작업 차량번호 또는 카드번호 없음");
					}
				}
			} 
			
			/**********************************************************
			* 7. 야드To위치결정방법, 야드To위치Guide CLEAR
			*   -야드To위치Guide 값이 4자리 이상이고 To 야드동이 같을 경우('G')가 아니면 CLEAR
			***********************************************************/
			if (!"G".equals(toLocChkGp)) 
			{
				ydToLocDcsnMtd = ""; //야드To위치결정방법
				ydToLocGuide   = ""; //야드To위치Guide
			}
			
	
			
			/**********************************************************
			* 8. 크레인 결정
			* EQUIP_STAT, STACK_STAT, WPROG_STAT, WORK_MODE 코드 정리 필요 kbs
			* WORK_MODE 1:선작업 실행, 2:대기 (구 O:선작업실행, C:대기)
			* WPROG_STAT B:고장
			**********************************************************/
			if (!"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && "1".equals(ydEqpWrkModePln)) 
			{
				ydEqpId   = ydWrkPlanCrn;	
				ydEqpStat = ydEqpStatPln;	
				commUtils.printLog(logId, trtMsg + " >> 작업예약 지정크레인[" + ydWrkPlanCrn + "]으로 설정", "SL");
			} 
			else if (!"B".equals(ydEqpStatWrk) || "2".equals(ydEqpWrkModePln)) 
			{
				ydEqpId   = ydWrkCrn;		
				ydEqpStat = ydEqpStatWrk;	
				commUtils.printLog(logId, trtMsg + " >> 작업크레인[" + ydWrkCrn + "]으로 설정", "SL");
			}
			else if (!"".equals(sYD_ALT_CRN) && !"B".equals(sYD_EQP_STAT_ALT)) 
			{
				ydEqpId   = sYD_ALT_CRN;
				ydEqpStat = sYD_EQP_STAT_ALT;
				commUtils.printLog(logId, trtMsg + " >> 대체크레인[" + sYD_ALT_CRN + "]으로 설정", "SL");
			}
			
			
			
			
			/**********************************************************
			* 9. 주작업, 보조 작업 그룹화
  			**********************************************************/
			JDTORecord paramSet = JDTORecordFactory.getInstance().create();
			
			paramSet.setResultCode(logId);	//Log ID
			paramSet.setResultMsg(methodNm);	//Log Method Name
			paramSet.setField("MODIFIER"              	, modifier ); //수정자
			paramSet.setField("YD_WBOOK_ID"				, ydWbookId); 			//야드작업예약ID
			paramSet.setField("YD_SCH_CD"  				, ydSchCd  ); 			//야드스케쥴코드
			paramSet.setField("YD_EQP_ID"  				, ydEqpId  ); 			//야드설비ID
			paramSet.setField("YD_SCH_PRIOR"  			, ydSchPrior  ); 		//야드스케쥴우선순위
			paramSet.setField("YD_TO_LOC_DCSN_MTD"  	, ydToLocDcsnMtd  ); 	//야드To위치결정방법
			paramSet.setField("YD_TO_LOC_GUIDE"  		, ydToLocGuide  ); 		//야드To위치Guide
			paramSet.setField("YD_WBOOK_MTL_CNT"   		, ""+wmMtlSh ); 		//작업예약 매수
			paramSet.setField("STACK_LAYER_CHK_YN"   	, stackLayerChkYn ); 	//적재위치 check여부
			paramSet.setField("WB_REGISTER"   			, WbRegister ); 		//작업예약 등록자
			
			commUtils.printParam(logId, paramSet);
			commUtils.printLog(logId, "그룹핑 파라미터 셋팅 시작", "SL");
			JDTORecordSet schSet = this.CrnSchGrp(logId, methodNm, paramSet);
			
			

			/**********************************************************
			* 10. 크레인스케줄과 크레인작업재료 등록
			*   - 적치단의 재료상태를 권상대기로 변경처리
			*   - 크레인스케줄과 크레인작업재료 등록
  			**********************************************************/
			commUtils.printLog(logId, "크레인스케줄과 크레인작업재료 등록 시작 ", "SL");
			this.CrnSchIns(logId, methodNm, paramSet, schSet);
			

			
			
			/**********************************************************
			* 11. TO 저장위치 결정
  			**********************************************************/
			commUtils.printLog(logId, "TO 저장위치 등록 시작 ", "SL");			
			
			JDTORecord jrLocSrcRngRtn = this.LocSrcRngDataSet(logId, methodNm, paramSet);
			
			commUtils.printLog(logId, "RTN:"+ commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")) + ", ydEqpStat:" + ydEqpStat, "SL");
			
			
			
			/**********************************************************
			* 12. 대차스케줄 공대차출발지시 처리 (별도 Transaction 으로 처리)
			if ("9".equals(commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")))) 
			{
				jrLocSrcRngRtn.setResultCode(logId);	//Log ID
				jrLocSrcRngRtn.setResultMsg(methodNm);	//Log Method Name
				try {
					EJBConnector tranConn1 = new EJBConnector("default", "ACoilSchSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)tranConn1.trx("updTcarSchLevWo"
							, new Class[] { JDTORecord.class }
							, new Object[] { jrLocSrcRngRtn });
					jrRtn = commUtils.addSndData(jrRtn , jrRtn1);
				} catch (Exception se) {}
				
			} else if (commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")).equals("-1")) {
				m_ctx.setRollbackOnly();
				throw new DAOException("TO 저장위치 등록  오류");
				
			} 
			**********************************************************/
			
			/**********************************************************
			* 14. 명령 선택 호출 
  			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("JMS_TC_CD"         , "YFYFJ001"               ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
			
			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			
			
			
			/**********************************************************
			* 15. DC Line-Off 
			*     - line-off일때 우선적으로 작업지시를 내리는 것으로 보임.
			*     - 명령선택에서 이미 작업지시 내렸는데, 이후 작업지시 다시 내리는것이 무슨 의미인지..
			*     - 순서적으로 맞는지 의문. 명령선택에서 해야할일 아닌지... kbs
  			
			if ("1CDC01LM".equals(ydSchCd)||"1BDC01LM".equals(ydSchCd)||"1ADC01LM".equals(ydSchCd)) {
				//해당크레인 크레인스케줄에 같은 스케줄코드로 상태가 S,1 인 경우 긴급작업으로
				//지금 만들어진 스케줄을 기동시킨다. (이전스케줄 삭제 전문 발송)
				jrParam.setField("YD_EQP_ID"	, ydEqpId); 	
				jrParam.setField("YD_SCH_CD"	, ydSchCd); 
				JDTORecordSet rsResult = commDao.select(jrParam, getWrkCrnSchId, logId, methodNm, "해당크레인 크레인스케줄에 같은 스케줄코드로 상태가 S,1 인 CRANE 스케줄ID 조회"); 
				
				if (rsResult.size() > 1) {

					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

					jrYdMsg.setField("JMS_TC_CD"         , "YFYFJ304"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
					jrYdMsg.setField("YD_SCH_CD"  	   	 , ydSchCd                  ); //스케줄ID
					jrYdMsg.setField("YD_WBOOK_ID"  	 , ydWbookId                ); //작업예약ID
					
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				}
			}
			**********************************************************/
			


			commUtils.printParam("AA", jrRtn);
			commUtils.printLog(logId, methodNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //procYFYFJ302 end
	
	
	
	
	
	/**	
	 *      [A] 오퍼레이션명 : 코일크레인스케줄 멀티기동(YFYFJ303)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYFYFJ303(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일크레인스케줄MAIN멀티기동 [ACoilSchSeEJB.rcvYFYFJ303] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			int schCnt  = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("SCH_CNT"),"0")); 
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilSchSeEJB", this); //추가
			EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
			JDTORecord jrRtn302 = null; // 추가

			/***************************************************************************
			 * 트랜잭션 분리 
			 ***************************************************************************/
			String sAPP000_YN = yfComm.ACoilApplyYn("APP000","1","YFYFJ303");   //트랜잭션 분리 여부
			
			commUtils.printLog(logId,  "==========[[[ APP000 YMYMJ302 트랜잭션 분리 여부:" + sAPP000_YN + " ]]]============", "SL");
			
			for (int i = 1 ; i<=schCnt; i++) 
			{				
				String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"+i  )); //야드작업예약ID

				if (ydWbookId.equals("") ||ydWbookId.length() == 0 ) {
	           	   continue;
				}
				jrParam.setField("JMS_TC_CD"			, "YFYFJ302"); 
				jrParam.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrParam.setField("YD_WBOOK_ID"			, ydWbookId); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"  			, ""); //야드설비ID	
				
				if ("Y".equals(sAPP000_YN)) {
					jrRtn302 = (JDTORecord)ejbConn.trx("rcvYFYFJ302", new Class[] { JDTORecord.class }, new Object[] { jrParam }); //추가
					
					//전송할 Data가 있으면 전송 처리
					if (jrRtn302 != null) {
						jrRtn302.setResultCode(logId);
						jrRtn302.setResultMsg(methodNm);
						
						sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn302 });
						
						jrRtn302 = null;
					}	
				}
				else
				{
					jrRtn = commUtils.addSndData(jrRtn, this.procYFYFJ302(jrParam));
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
	 *      [A] 오퍼레이션명 : LINE-OFF 긴급작업(YFYFJ304)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYFYFJ304(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "자동 LINE-OFF긴급작업 [ACoilSchSeEJB.rcvYFYFJ304] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn 			= JDTORecordFactory.getInstance().create();;	//전문 Return
		JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecordSet rsResult    	= null;
		String ydCrnSchIdWrk = "";
		String ydBefCrnSchIdWrk = "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

	    	//수신항목 변수 저장
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //설비번호(크레인번호)
			String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //스케줄코드
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); //야드작업예약ID

			String msgId	 = commUtils.nvl(commUtils.getMsgId(rcvMsg),"YFYFJ304"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			/**********************************************************
			* 1. 가장 최근의 line off 스케줄 get.
			*    - 해당 크레인에 line-off 이외에 스케줄이 있다면, 여기서 끝냄.
			**********************************************************/
			jrParam.setField("MODIFIER", modifier); 
			jrParam.setResultCode(logId);	
			jrParam.setResultMsg(methodNm);	
			jrParam.setField("YD_EQP_ID"		, ydEqpId); 	
			jrParam.setField("YD_SCH_CD"		, ydSchCd); 
			jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
			jrParam.setField("YD_SCH_PRIOR"		, "0");           //신규
			rsResult = commDao.select(jrParam, getLastCrnSchIdAuto, logId, methodNm, "가장 최근에 만들어진 크레인 스케줄ID를 가져온다"); 
			
			if (rsResult.size() > 0) {
				
			    String ydNewCrnSchId 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
	
			    /**********************************************************
				* 2. 가장 최근의 line off 스케줄 이외에 다른 작업 중인 스케줄을 대기로 변경
				**********************************************************/
				commUtils.printLog(logId, "Line-Off 긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + " >> " + ydNewCrnSchId +" >> " + ydSchCd + " ]", "SL");
				jrParam.setField("YD_CRN_SCH_ID", ydNewCrnSchId );
			    jrParam.setField("YD_SCH_CD"	, ydSchCd);
			    jrParam.setField("YD_EQP_ID"	, ydEqpId );
				JDTORecordSet jsCrn = commDao.select(jrParam, getCrnWrkMgtPriorWrkLineOffAuto, logId, methodNm, "기존 크레인작업 지시 검색조회");
				if (jsCrn.size() > 0) {
					
			    	JDTORecord jrCrn = jsCrn.getRecord(0);
			    	ydBefCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"));
					
			    	jrParam.setField("YD_WRK_PROG_STAT" , "W");
				    jrParam.setField("OLD_YD_CRN_SCH_ID" , ydBefCrnSchIdWrk);
					commDao.update(jrParam, updCrnWrkMgtPriorWrkNext1Auto, logId, methodNm,  "기존 크레인작업 지시 원복");	
				}
				

				/**********************************************************
				* 3. 가장 최근의 line off 스케줄을 명령선택지시대기로 변경
				**********************************************************/
				jrParam.setField("YD_WRK_PROG_STAT" , "S"); //명령선택지시대기
				jrParam.setField("YD_CRN_SCH_ID" 	, ydNewCrnSchId);
				commDao.update(jrParam, updCrnWrkMgt1Auto, logId, methodNm,  "신규 크레인작업 지시 대기 ");
						
				
				/**********************************************************
				* 4. 신 크레인작업지시 요구 처리
				**********************************************************/
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrYdMsg.setField("JMS_TC_CD"       , "YFF1L004");	//크레인작업지시
				jrYdMsg.setField("MSG_GP"          , "I");					//야드설비ID
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydNewCrnSchId);		//야드크레인스케쥴ID

				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L004", jrYdMsg));	
		    	
			}
			
			commUtils.printLog(logId, methodNm , "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 작업예약재료 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updCrnSchWB(JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인스케줄 작업예약재료 수정[ACoilSchSeEJB.updCrnSchWB] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commDao.update(jrParam, updWmStrLoc, logId, methodNm, "작업예약재료 저장위치 수정");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 박판열연 코일야드 크레인 스케줄
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public JDTORecordSet CrnSchGrp( String logId, String methodNms, JDTORecord jrParamSet) throws JDTOException  {
    	String 	methodNm = "그룹핑 파라미터 셋팅 [ACoilSchSeEJB.CrnSchGrp] < " + methodNms;
    	
    	JDTORecordSet schSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet mtlSet   = JDTORecordFactory.getInstance().createRecordSet("Temp");
		//레코드셋 정렬 시
		String 	szLogMsg="";
		int 	intRtnVal = 0;
		
		try 
		{
			
			commUtils.printLog(logId, methodNm, "S+");
			
			String szWbookId 			= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"));
			String szSchCd   			= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"));  
			String stackLayerChkYn   	= commUtils.trim(jrParamSet.getFieldString("STACK_LAYER_CHK_YN"));  
			
			//상단 layer 재료까지 get
			if ("Y".equals(stackLayerChkYn)) 
			{
				mtlSet = commDao.select(jrParamSet, getbcoilWrkBookMtl, logId, methodNm, "작업예약 재료정보 조회");
			} 
			//주재료만 get
			else 
			{	  
				mtlSet = commDao.select(jrParamSet, getbcoilWrkBookMtlNoLayer, logId, methodNm, "적재위치 CHECK 안함 작업예약 재료정보 조회");
			}
			
			
			if (mtlSet.size() <= 0) 
			{    			
				throw new DAOException(methodNm+ "크레인작업재료조회 >> 조회 Data 없음");			
			}
			
			
			JDTORecord mtlRow = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			for (int i = 0; i < mtlSet.size(); i++) 
			{
				mtlRow =  mtlSet.getRecord(i);
				
				
				//적치중이라면
				if ("C".equals(mtlRow.getFieldString("YD_STK_LYR_STAT")) ) 
				{
					schSet.addRecord(mtlRow);
				}
				//UP 스케쥴 수행(U)이고, 주재료와 wbookId가 다르고, 작업대기라면
				//수행중이던 스케줄 우선순위를 높여주고, 더미 스케줄을 만들지는 않는다.
				else if ("U".equals(mtlRow.getFieldString("YD_STK_LYR_STAT")) 
						&& !szWbookId.equals(mtlRow.getFieldString("CRNSCH_WBOOK_ID")) 
						&& "W".equals(mtlRow.getFieldString("YD_WRK_PROG_STAT")))  
				{
					
					commUtils.printLog(logId, "작업 상태:" + mtlRow.getFieldString("YD_WRK_PROG_STAT") , "SL");  
			    	
					
					//1. 해당 작업스케줄 update
	    			recPara = JDTORecordFactory.getInstance().create();
	    		    recPara.setField("YD_CRN_SCH_ID", mtlRow.getFieldString("YD_CRN_SCH_ID"));
	    		    recPara.setField("YD_SCH_PRIOR"	, "1"); 
		    		if (!commDao.updateVerify(recPara, updbcoilCrnSchPrior, logId, methodNm, "크레인스케쥴 갱신")) 
		    		{  
		    			throw new DAOException("크레인스케쥴 갱신 오류");
		    		}	
		    		
		    		
		    		//2. 해당 작업예약 update				    		    			
		    		recPara.setField("YD_SCH_CD"	, mtlRow.getField("CRNSCH_YD_SCH_CD"));
	    		    recPara.setField("YD_WBOOK_ID"	, mtlRow.getField("CRNSCH_WBOOK_ID"));
		    		if (!commDao.updateVerify(recPara, updbcoilWrkBookPrior, logId, methodNm, "작업예약 갱신")) 
		    		{
		    			throw new DAOException("작업예약 갱신 오류");
		    		}	
		    		
					szLogMsg = methodNm+ "작업 우선순위를 조정하였습니다.";
					commUtils.printLog(logId, szLogMsg, "SL");  
				} 
			}				

			commUtils.printLog(logId, methodNm, "S-");	

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return schSet;

	}
	
	
	
	
	
	/**
     * 오퍼레이션명 : 레코드 치환(H/J)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.  
     * @param  ● recPara1, recPara2, recResult
     * @return ● intRtnVal '1': 성공   '-1': 실패
     * @throws ● JDTOException
     */
    public JDTORecordSet YmSortCoil (int intLoop_i, int intLoop_j, JDTORecordSet rsCrnSchResult) {

    	JDTORecordSet rsTemp = null; 
    	JDTORecord recTemp = null;
    	int intRtnVal = 0;
		
		try{
			rsTemp = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			for (int Loop_i = 1;  Loop_i <= rsCrnSchResult.size(); Loop_i++) {
				if (Loop_i == intLoop_i) {
					rsCrnSchResult.absolute(intLoop_j);
					recTemp = rsCrnSchResult.getRecord();
				}else if (Loop_i == intLoop_j) {
					rsCrnSchResult.absolute(intLoop_i);
					recTemp = rsCrnSchResult.getRecord();
				}else{
					rsCrnSchResult.absolute(Loop_i);
					recTemp = rsCrnSchResult.getRecord();
				}
				rsTemp.addRecord(recTemp);
			}
			
		}catch(Exception e) {
			String szMsg = "Error : " + e.getLocalizedMessage();
        }//end of try~catch
		
		return rsTemp;
    }//end of YmSortCoil()
    
    
    
    
    
    
    
    
	/**
     * 오퍼레이션명 : 스케줄링 크레인 스케줄 등록
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.  
     * @param  ● vResult, msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public void CrnSchIns(String logId, String methodNms , JDTORecord jrParamSet , JDTORecordSet schSet) throws JDTOException {
   		String methodNm = "스케줄링 크레인 스케줄 등록[ACoilSchSeEJB.CrnSchIns] < " + methodNms;
		JDTORecord recInCrn    = null;
		int intRtnVal = 0;
		String szName = "SYSTEM";
		String szLogMsg = "";
		
		try{
			
			commUtils.printLog(logId, methodNm, "S+");
			
			String szEqpId			= commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"  ));
			String szSchCd  		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"  ));
			String szydWbookId      = commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"  ));
			String szYD_SCH_PRIOR  	= commUtils.trim(jrParamSet.getFieldString("YD_SCH_PRIOR"  ));
			String szYD_WBOOK_DT  	= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_DT"  ));
			String szYD_TO_LOC_GUIDE= commUtils.trim(jrParamSet.getFieldString("YD_TO_LOC_GUIDE"  ));
			String stackLayerChkYn	= commUtils.trim(jrParamSet.getFieldString("STACK_LAYER_CHK_YN")); 
			String modifier			= commUtils.trim(jrParamSet.getFieldString("MODIFIER")); //수정자(Backup Only)
			String WbRegister       = commUtils.trim(jrParamSet.getFieldString("WB_REGISTER")); //작업예약 등록자
			
			commUtils.printParam(logId, schSet);
			
 			//크레인 스케줄에 Insert한다.	
			for (int i = 0; i < schSet.size(); i++) 
			{
				recInCrn = JDTORecordFactory.getInstance().create();
				recInCrn  = schSet.getRecord(i);
				
				
				/* 
				 * 1. procYFYFJ302에서 이미 해당 wbookId로 생성된 스케줄은 제외시킴. kbs
				 * 2. 선행 스케줄이 끝나기 전에 후행 스케줄이 동일한 wbook을 소재로 스케줄링을 하면, 이런 오류가 발생할 수 있을듯..	
				 * 3. new트랜잭션으로 updCrnSchWB	실행하는 시점에 wbook상태를 바꿔서 관리하는건 어떤지...
				//작업예약ID 조회 
				recInCrn.setField("STL_NO"			, commUtils.trim(recInCrn.getFieldString("STL_NO"  )));
				
				if (!"".equals(szydWbookId)) 
				{
					JDTORecordSet jsWbook = commDao.select(recInCrn, getCrnSchWbookChk, logId, methodNm, "작업예약 조회(스케쥴생선 전)");
				}
				
				if (jsWbook == null || jsWbook.size() <= 0) 
				{					 
					szLogMsg = "["+ methodNm +"]크레인 스케줄 등록중  이미 크레인 스케쥴 존재 함.Error!! ydWbookId: " + szydWbookId;
					commUtils.printLog(logId, szLogMsg, "SL");
					return YfConstant.RETN_INT_FAILURE;		 
				}
				*/
				
				/**********************************************************
				*  1. 크레인 스케줄 등록
				**********************************************************/			
				//크레인스케줄ID를 할당받는다
				String ydCrnSchId = commDao.getSeqId(logId, methodNm, "CrnSch");

				recInCrn.setField("YD_WBOOK_ID",		szydWbookId);
				recInCrn.setField("MODIFIER",			modifier);
				recInCrn.setField("YD_CRN_SCH_ID",		ydCrnSchId);
				recInCrn.setField("YD_WBOOK_ID",      	szydWbookId);
				recInCrn.setField("YD_EQP_ID",        	szEqpId);
				recInCrn.setField("YD_GP",            	recInCrn.getFieldString("YD_STK_COL_GP").substring(0,1));
				recInCrn.setField("YD_BAY_GP",        	recInCrn.getFieldString("YD_STK_COL_GP").substring(1,2));
				recInCrn.setField("YD_SCH_CD",        	szSchCd);	
				recInCrn.setField("REGISTER",         	recInCrn.getFieldString("HANDLING_CNT"));	
				recInCrn.setField("YD_SCH_PRIOR",     	szYD_SCH_PRIOR);
				recInCrn.setField("YD_WBOOK_DT",      	szYD_WBOOK_DT);
				recInCrn.setField("YD_SCH_ST_GP",     	"A");
				recInCrn.setField("YD_UP_WO_LOC",     	recInCrn.getFieldString("YD_STK_COL_GP") + recInCrn.getFieldString("YD_STK_BED_NO"));
				recInCrn.setField("YD_UP_WO_LYR",   	recInCrn.getFieldString("YD_STK_LYR_NO"));
				recInCrn.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
				recInCrn.setField("YD_WRK_PROG_STAT", 	"W");
				recInCrn.setField("YD_SCH_REQ_GP"   , 	WbRegister);
				
				if ("".equals(recInCrn.getFieldString("YD_UP_WO_LOC"))) 
				{
					throw new DAOException("["+ methodNm +"] 권상지시위치가 없습니다.");			
				}

				
				if (!commDao.insertVerify(recInCrn, insYmCrnsch, logId, methodNm, "TB_YF_CRNSCH 생성")) 
				{
					throw new DAOException("["+ methodNm +"]크레인 스케줄 등록중  Error!! ErrorCode: " + intRtnVal);	
				}
  
				
				/**********************************************************
				*  2. 크레인 스케줄 작업재료 등록
				**********************************************************/			
				/*
				 * 기존의 MAIN_WRK_YN 은 주작업이 Y 보조작업이 N으로 들어옴 
				 * 크레인작업재료에는 보조작업여부에 값은 보조작업인경우 Y 주작업인경우 N로 셋팅!
				 */
				JDTORecord recInCrnMtl = JDTORecordFactory.getInstance().create();
				if ("W".equals(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD"))) 
				{
					recInCrnMtl.setField("YD_AID_WRK_YN", "Y"); //보조작업
				}
				else
				{
					recInCrnMtl.setField("YD_AID_WRK_YN", "N");
				}
				recInCrnMtl.setField("YD_CRN_SCH_ID"		, ydCrnSchId);
				recInCrnMtl.setField("REGISTER"				, modifier);
				recInCrnMtl.setField("MOD_DDTT"				, "");
				recInCrnMtl.setField("STL_NO"				, commUtils.trim(recInCrn.getFieldString("STL_NO"  )));
				recInCrnMtl.setField("YD_STK_LYR_NO"		, commUtils.trim(recInCrn.getFieldString("YD_STK_LYR_NO"  )));
				recInCrnMtl.setField("YD_STK_LOT_TP"		, commUtils.trim(recInCrn.getFieldString("YD_STK_LOT_TP"  )));
				recInCrnMtl.setField("YD_STK_LOT_CD"		, commUtils.trim(recInCrn.getFieldString("YD_STK_LOT_CD"  )));
				recInCrnMtl.setField("HCR_GP"				, commUtils.trim(recInCrn.getFieldString("HCR_GP"  )));
				recInCrnMtl.setField("STL_PROG_CD"			, commUtils.trim(recInCrn.getFieldString("STL_PROG_CD"  )));
				recInCrnMtl.setField("YD_MTL_ITEM"			, commUtils.trim(recInCrn.getFieldString("YD_MTL_ITEM"  )));
				recInCrnMtl.setField("YD_ROUTE_GP"			, commUtils.trim(recInCrn.getFieldString("YD_ROUTE_GP"  )));
				recInCrnMtl.setField("YD_TO_LOC_DCSN_MTD"	, commUtils.trim(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD")));
				//크레인작업재료 생성

				
				if (!commDao.insertVerify(recInCrnMtl, insYmCrnwrkmtl, logId, methodNm, "TB_YF_CRNWRKMTL 생성")) 
				{
					throw new DAOException("["+ methodNm +"] 크레인 스케줄 작업재료 등록중 실패: " + intRtnVal);	
				}
				
				
				
				/**********************************************************
				*  3. 적치단의 재료상태를 권상대기로 변경
				**********************************************************/		
				recInCrn.setField("YD_STK_LYR_STAT", "U");
				if (!commDao.updateVerify(recInCrn, updStackLayerMtlStat, logId, methodNm, "TB_YF_STKLYR 갱신")) 
				{
					throw new DAOException("[" + methodNm + "] 재료[" + recInCrn.getFieldString("STL_NO") + "]적재단 변경시 오류");	
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of CrnSchIns()
    
    
    
	/**
     * 오퍼레이션명 : 박판열연 COIL YARD - TO 위치 결정
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord LocSrcRngDataSet (String logId, String methodNms, JDTORecord inRecord)throws JDTOException{
    	String methodNm = "TO 위치 결정[ACoilSchSeEJB.LocSrcRngDataSet] < " + methodNms;
    	
    	JDTORecord jrLocSrcRngRtn = JDTORecordFactory.getInstance().create();
    	String szMsg        		= "";     	  
    	
    	int intRtnVal 				= 0 ;
    	String TcarTcSndyn   		= "N";    // 대차이동지시 

    	try
    	{
        	commUtils.printLog(logId, methodNm, "S+");
        	String szWbookId	= commUtils.trim(inRecord.getFieldString("YD_WBOOK_ID" ));	
        	String szEqpId 		= commUtils.trim(inRecord.getFieldString("YD_EQP_ID"   ));	
        		

        	
        	/**********************************************************
			* 1. 작업예약 조회
  			**********************************************************/
        	JDTORecordSet wbSet = commDao.select(inRecord, getYmWrkbook, logId, methodNm, "작업예약 조회"); 
	    	
	    	if (wbSet == null || wbSet.size() <= 0) 
	    	{
				commUtils.printLog(logId, methodNm + "[작업예약종료]", "SL");
    			jrLocSrcRngRtn.setField("RTN", "-1");
				return jrLocSrcRngRtn;
			}			
	    	
			JDTORecord wbRow = wbSet.getRecord(0);
			String szSchCd 				= commUtils.trim(wbRow.getFieldString("YD_SCH_CD"));
			String stlNo 				= commUtils.trim(wbRow.getFieldString("STL_NO"));
			
			
			
			/**********************************************************
			* 2. 크레인스케줄 조회
  			**********************************************************/
			JDTORecordSet schSet = JDTORecordFactory.getInstance().createRecordSet("");
			if (stlNo.substring(0,1).equals("S")) 
			{
				// 스크랩이라면
				schSet = commDao.select(inRecord, getYdCrnSchByWBookIdScrap, logId, methodNm, "크레인스케줄 조회");
			} 
			else 
			{
				schSet = commDao.select(inRecord, getYdCrnSchByWBookId, logId, methodNm, "크레인스케줄 조회"); 
			}
			
			
			
			/**********************************************************
			* 3. 크레인스케줄의 권하지시위치 결정
  			**********************************************************/
			String ydCrnSchId 			= "";
	    	String szToLocDcsnMtd 		= "";
	    	String szToLocGuide 		= "";
	    	JDTORecordSet jsRtn = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord schRow = JDTORecordFactory.getInstance().create();
		    for (int i = 0; i < schSet.size(); i++) 
		    {

		    	schRow  = schSet.getRecord(i);
        		
        		//크레인스케줄Data저장
        		ydCrnSchId     	= schRow.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        	= schRow.getFieldString("YD_SCH_CD");
        		szToLocDcsnMtd 	= schRow.getFieldString("YD_TO_LOC_DCSN_MTD");
        		stlNo 	   		= schRow.getFieldString("STL_NO");
        		szToLocGuide   	= schRow.getFieldString("YD_TO_LOC_GUIDE");
        		
        		szMsg = "작업예약 " + szWbookId + " [" + i+"]번째 크레인 스케줄[" + ydCrnSchId + "]에 대한 권하지시위치 결정 "; //szWbookId
        		commUtils.printLog(logId, szMsg, "SL");
        		
        		
        		
        		/*********************************************************
        		 * 3-1. 보조작업
        		 ********************************************************/
        		if ("W".equals(szToLocDcsnMtd)) 
       			{            		
        			commUtils.printLog(logId, "["+ i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 보조작업 스케줄의  To위치 결정 시작", "SL");
        			jsRtn = this.procToLocDummy(logId, methodNm, wbRow, schRow);
            	}
        		/*********************************************************
        		 * 3-2. 차량 상차: 차량이 정지한 적치열 조회 ==> TO위치가 됨  ==> TO 위치 가이드에 등록 처리
       			 * 확인 필! (차량방향, 복수동..) kbs
       			 ********************************************************/
            	else if ( "PT".equals(szSchCd.substring(2,4)) && ("U".equals(szSchCd.substring(6,7)))) 
            	{
            		commUtils.printLog(logId 
    						,"[" + i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 차량상차작업예약["+szWbookId+"]의 차량정보[차량사용구분:"+wbRow.getFieldString("YD_CAR_USE_GP")+", 운송장비코드:"+wbRow.getFieldString("TRN_EQP_CD")+", 차량번호:"+wbRow.getFieldString("CAR_NO")+"]에 대한 적치베드 조회 시작"
    						, "SL");
    				this.procCarToLoc(logId, methodNm, wbRow, schRow);
      			}
        		/*********************************************************
        		 * 3-3. 설비보급
        		 * ********************************************************/
       			else if (  "S".equals(szToLocDcsnMtd)					//설비위 주작업
              			 &&("1GHS01UM".equals(szSchCd)					//HFL결속장 보급  
   	           			   )) 
       			{
       				commUtils.printLog(logId, methodNm +"["+ i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 설비위 스케줄의  To위치 결정 시작", "SL");
   		       		this.procToLocConveyor(logId, methodNm, wbRow, schRow);
            	}
       			
        		/*********************************************************
        		 * 3-3. 사용자 지정
       			 ********************************************************/
       			else if (szToLocGuide.length() >= 4) 
       			{
        			commUtils.printLog(logId, "["+ i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 스케줄의  To위치 결정 시작", "SL");
        			jsRtn = this.procToLocUser(logId, methodNm, wbRow, schRow);
    			}
        		/*********************************************************
        		 * 3-4. 일반작업
       			 ********************************************************/
            	else 
            	{
    				commUtils.printLog(logId, "[" + i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-야드로 TO위치결정 시작", "SL");
    				jsRtn = this.procToLocPrimaryWork(logId, methodNm, wbRow, schRow);
    			}
        	}
        	
		    
		    /**********************************************************
			* 4. To위치 결정 실패시 default값으로 xx010101을 설정
			***********************************************************/
		    schSet 	= JDTORecordFactory.getInstance().createRecordSet("");
		    JDTORecord jrInPara 	= JDTORecordFactory.getInstance().create();
    		jrInPara.setField("YD_WBOOK_ID", szWbookId);
    		jrInPara.setField("YD_EQP_ID",   szEqpId);
    		schSet = commDao.select(jrInPara, getYdCrnschByEqpIdandWBookId, logId, methodNm, "크레인스케줄 조회");   		
    		
    		for (int i = 0; i < schSet.size(); i++) 
    		{
    			schSet.absolute(i);
				jrInPara = schSet.getRecord(i);
				
				if ("".equals(commUtils.trim(jrInPara.getFieldString("YD_DN_WO_LOC")))) 
				{
					jrInPara.setField("YD_DN_WO_LOC", "XX010101");				   
					intRtnVal = commDao.update(jrInPara, updCrnWrkMgtDnLoc, logId, methodNm, "크레인스케줄 갱신");

					if (intRtnVal <= 0) 
					{
						szMsg = methodNm + " 크레인스케줄 To위치 Default값 등록 실패!!";
	    				commUtils.printLog(logId, szMsg, "SL");
	        			jrLocSrcRngRtn.setField("RTN", "-1");
	        			return jrLocSrcRngRtn;
					}
				}
			}
			
        	commUtils.printLog(logId, methodNm, "S-");
        	
        	
        	
        	/**********************************************************
			* 5. 대차 이동지시 송신(확장대차)
  			**********************************************************/
        	//공대차 호출
			if((jsRtn != null && jsRtn.size()>0)
				&&"Y".equals(jsRtn.getRecord(0).getFieldString("TC_START_GP")))
			{
				jrLocSrcRngRtn.setField("RTN", "9");
    			jrLocSrcRngRtn.setField("YD_EQP_ID", "1XTC03");           //확장대차
    			jrLocSrcRngRtn.setField("YD_BAY_GP", szSchCd.substring(1,2));  //상차동
			}
			//영대차 출발(대차 중량초과 시)
			else if((jsRtn != null && jsRtn.size()>0)
					&&"S".equals(jsRtn.getRecord(0).getFieldString("TC_START_GP")))
			{
				jrLocSrcRngRtn.setField("RTN", "9");
				jrLocSrcRngRtn.setField("OPRN", "Y");
    			jrLocSrcRngRtn.setField("YD_EQP_ID", "1XTC03");           //확장대차
    			jrLocSrcRngRtn.setField("YD_BAY_GP", szSchCd.substring(1,2));  //상차동
			}
        	else 
        	{
    			jrLocSrcRngRtn.setField("RTN", "1");
    		}
			return jrLocSrcRngRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of LocSrcRngDataSet()  
    	
    
    
    
   
    
	/**
	 * 차량작업 TO위치결정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param wbRow
	 * @param schRow
	 * @return
	 * @throws JDTOException
	 */
    public String procCarToLoc(String logId, String methodNms, JDTORecord wbRow, JDTORecord schRow) throws JDTOException {
    	String methodNm = "TO 위치 결정: 차량작업[ACoilSchSeEJB.procCarToLoc] < " + methodNms;
    	try 
    	{
    		commUtils.printLog(logId, methodNm, "S+");
    		/**********************************************************************
			* 차량출고 : 차량이 정지한 적치열 조회 ==> TO위치가 됨  ==> TO 위치 가이드에 등록 처리
			***********************************************************************/            		
			String szRtnMsg = "";
			
			String ydCarUseGp   = commUtils.trim(wbRow.getFieldString("YD_CAR_USE_GP"));	//차량사용구분
			String TrnEqpCd		= commUtils.trim(wbRow.getFieldString("TRN_EQP_CD"));		//운송장비코드
			String ydCarNo		= commUtils.trim(wbRow.getFieldString("CAR_NO"));			//차량번호
			String ydCardNo		= commUtils.trim(wbRow.getFieldString("CARD_NO"));			//차량번호
			String ydCrnSchId   = schRow.getFieldString("YD_CRN_SCH_ID");
			String szSchCd      = schRow.getFieldString("YD_SCH_CD");
			String stlNo 	   	= schRow.getFieldString("STL_NO");
    		
    		
			
			JDTORecordSet jsCar	= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			if ( "L".equals(ydCarUseGp) ) //구내운송 
			{				
				commUtils.printLog(logId,  " TOSQL:["+ydCrnSchId+ "] 권상재료["+stlNo +" 의 적치가능한 베드 조회 시작", "SL");
				
				jrParam.setField("YD_CAR_USE_GP", 	ydCarUseGp);
				jrParam.setField("TRN_EQP_CD", 		TrnEqpCd);  
				jsCar = commDao.select(jrParam, getYdStkBedByCarUseGpandTrnEqpCd, logId, methodNm, "구내운송차량 차량BED 조회"); 
        		
        		if (jsCar.size() <= 0) 
        		{
    				commUtils.printLog(logId, methodNm + " : 구내운송차량 READ 실패!", "SL");
        		} 
        		else 
        		{
        			szRtnMsg = YfConstant.RETN_CD_SUCCESS;
        		}	
			} 
			else if ( "G".equals(ydCarUseGp) ) //출하. 
			{
				
				commUtils.printLog(logId,  " TOSQL:["+ydCrnSchId+ "] 권상재료["+stlNo +" 의 적치가능한 베드 조회 시작", "SL");
				
				jrParam.setField("YD_CAR_USE_GP"	, ydCarUseGp);
				jrParam.setField("CAR_NO"			, ydCarNo);
				jrParam.setField("STL_NO"			, stlNo);
				jrParam.setField("CARD_NO"			, ydCardNo);
				jrParam.setField("YD_SCH_CD"	    , szSchCd);
				jsCar = commDao.select(jrParam, getYdStkBedByCarUseGpandCarNoFrto, logId, methodNm, "냉연이송상차 차량BED 조회"); 
        		
        		if (jsCar.size() <= 0) 
        		{
    				commUtils.printLog(logId, methodNm + " : 냉연이송상차 READ 실패!", "SL");
        		} 
        		else 
        		{
        			szRtnMsg = YfConstant.RETN_CD_SUCCESS;
        		}    	
			}
			
			
			if ( YfConstant.RETN_CD_SUCCESS.equals(szRtnMsg) ) 
			{
				jsCar.first();
				JDTORecord jrCar = jsCar.getRecord();
				
    			String StackColGp 	= commUtils.trim(jrCar.getFieldString("YD_STK_COL_GP"));//차량정지위치 적치열
    			String StackBedGp	= commUtils.trim(jrCar.getFieldString("YD_STK_BED_NO"));//차량정지위치 적치베드
    			String StackLayerGp = commUtils.trim(jrCar.getFieldString("YD_STK_LYR_NO"));//차량정지위치 적치단
				
    			wbRow.setField("YD_TO_LOC_GUIDE", StackColGp + StackBedGp + StackLayerGp);

				this.procToLocUser(logId, methodNm, wbRow, schRow);
			}
			commUtils.printLog(logId, methodNm, "S-");
	    } catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch	
		
		return YfConstant.RETN_CD_SUCCESS;
    }
    
    
    
    
	/**
	 * TO위치 UPDATE
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */	
	public String procUpdateLoc(String logId, String methodNms, JDTORecord jrSetLoc, JDTORecord jrCrnWrk) throws JDTOException {
		String methodNm = "TO위치 UPDATE[ACoilSchSeEJB.procUpdateLoc] < " + methodNms;
		String LocalmethodNm = "TO위치 UPDATE[ACoilSchSeEJB.procUpdateLoc]" ;
		String szLogMsg					= null;
		String szYD_DN_STK_BED_NO= null;
		JDTORecord		recInBed		= null;
		
		int intRtnVal					= 0;
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");
		try {

			int intYD_EQP_WRK_SH    	= commUtils.paraRecChkNullInt(jrCrnWrk,"SH_CNT");				//크레인작업재료 총매수
			int intYD_EQP_WRK_WT    	= commUtils.paraRecChkNullInt(jrCrnWrk,"SUM_MTL_WT");			//크레인작업재료 총중량
			double dblYD_EQP_WRK_T     	= commUtils.paraRecChkNullDouble(jrCrnWrk,"SUM_MTL_T");			//크레인작업재료 총높이
			String szYD_EQP_WRK_MAX_W 	= commUtils.trim(jrCrnWrk.getFieldString("MAX_MTL_W"  ));		//크레인작업재료 중 최대 폭
			String szYD_EQP_WRK_MAX_L 	= commUtils.trim(jrCrnWrk.getFieldString("MAX_MTL_L"  ));		//크레인작업재료 중 최대 길이
			String szMODIFIER 			= commUtils.trim(jrCrnWrk.getFieldString("MODIFIER"  ));		//MODIFIER
			String szSTOCK_ID 	   		= commUtils.trim(jrCrnWrk.getFieldString("STL_NO"  ));
			
			String szYD_CRN_SCH_ID  	= commUtils.trim(jrSetLoc.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
			String szYD_SCH_CD  		= commUtils.trim(jrSetLoc.getFieldString("YD_SCH_CD"  ));	
			String szYD_UP_WO_LOC		= commUtils.trim(jrSetLoc.getFieldString("YD_UP_WO_LOC"  ));			
			String szYD_UP_WO_LAYER		= commUtils.trim(jrSetLoc.getFieldString("YD_UP_WO_LYR"  ));			
			String szYD_DN_WO_LOC		= commUtils.trim(jrSetLoc.getFieldString("YD_DN_WO_LOC"  ));			
			String szYD_DN_WO_LAYER		= commUtils.trim(jrSetLoc.getFieldString("YD_DN_WO_LYR"  ));			
			String szYD_RCPT_PLN_STR_LOC= commUtils.trim(jrSetLoc.getFieldString("YD_RCPT_PLN_STR_LOC"  )); // 입고예정위치			
			String szYD_WBOOK_ID  		= commUtils.trim(jrSetLoc.getFieldString("YD_WBOOK_ID"  ));		//작업예약
			String szYD_ROUTE_GP  		= commUtils.trim(jrSetLoc.getFieldString("YD_ROUTE_GP"  ));
			if (szYD_DN_WO_LOC.equals("")) {
				return YfConstant.RETN_CD_FAILURE;
			}
			
			commUtils.printParam(logId, jrSetLoc);
			//----------------------------------------------------------------------------------------------------------------------
			// 권하지시위치 수정
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecordSet jsLayerUpXy = JDTORecordFactory.getInstance().createRecordSet("");
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("YD_STK_COL_GP", 			szYD_UP_WO_LOC.substring(0, 6)); //권상지시위치
			recInBed.setField("YD_STK_BED_NO", 			szYD_UP_WO_LOC.substring(6));	 //권상지시위치
			recInBed.setField("YD_STK_LYR_NO", 		szYD_UP_WO_LAYER);
			jsLayerUpXy = commDao.select(recInBed, getYdStkLayerBybed, logId, methodNm, "권상 BED 좌표 조회");
			if (jsLayerUpXy.size() <= 0) {
				szLogMsg =  "확인:"+szSTOCK_ID+"권상 Layer 좌표 조회 검색 실패.";
				commUtils.printLog(logId, szLogMsg, "SL");
				
			}
			jsLayerUpXy.first();
			JDTORecord jrUpLayerXy = jsLayerUpXy.getRecord();
			
			String sAPP000_YN = yfComm.ACoilApplyYn("APP000","1","2");   //트랜잭션 분리 (저장위치)
	
			JDTORecordSet jsDnLayerXy = JDTORecordFactory.getInstance().createRecordSet("");
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("YD_STK_COL_GP", 			szYD_DN_WO_LOC.substring(0, 6));//권하지시위치
			recInBed.setField("YD_STK_BED_NO", 			szYD_DN_WO_LOC.substring(6));	//권하지시위치
			recInBed.setField("YD_STK_LYR_NO", 		szYD_DN_WO_LAYER);	
			
			if("HS".equals(szYD_DN_WO_LOC.substring(2,4))){
				jsDnLayerXy = commDao.select(recInBed, getYdStkLayerBybedHFL, logId, methodNm, "권하HFL BED 좌표 조회");
				
				if (jsDnLayerXy.size() <= 0) {
					szLogMsg = LocalmethodNm +" 권하 HFL Layer 좌표 검색 실패 ";
					commUtils.printLog(logId, szLogMsg, "SL");
					return YfConstant.RETN_CD_FAILURE;
					
				}else{
					szLogMsg = LocalmethodNm +" 권하 HFL결속장 적치 TO위치 베드 검색 완료";
					commUtils.printLog(logId, szLogMsg, "SL");
					
					//HFL결속장 적치 TO위치 베드
				    szYD_DN_STK_BED_NO= jsDnLayerXy.getRecord(0).getFieldString("YD_STK_BED_NO");
					szYD_DN_WO_LOC = szYD_DN_WO_LOC.substring(0, 6) + szYD_DN_STK_BED_NO; 
				 
					
					if ("Y".equals(sAPP000_YN)) {
						if ("D".equals(jsDnLayerXy.getRecord(0).getFieldString("YD_STK_LYR_STAT"))) {
							szLogMsg = LocalmethodNm +" 권하 Layer 좌표 검색 실패(중복) ";
							commUtils.printLog(logId, szLogMsg, "SL");
							return YfConstant.RETN_CD_FAILURE;
						}
					}
				}
					
			}else{
				jsDnLayerXy = commDao.select(recInBed, getYdStkLayerBybed, logId, methodNm, "권하 BED 좌표 조회");
				
				if (jsDnLayerXy.size() <= 0) {
					szLogMsg = LocalmethodNm +" 권하 Layer 좌표 검색 실패 ";
					commUtils.printLog(logId, szLogMsg, "SL"); 
					return YfConstant.RETN_CD_FAILURE;					
				}else{
					szLogMsg = LocalmethodNm +" 권하 적치 TO위치 베드 검색 완료";
					commUtils.printLog(logId, szLogMsg, "SL");
					 
					szYD_DN_STK_BED_NO =szYD_DN_WO_LOC.substring(6);
					
					if ("Y".equals(sAPP000_YN)) {
						if ("D".equals(jsDnLayerXy.getRecord(0).getFieldString("YD_STK_LYR_STAT"))) {
							szLogMsg = LocalmethodNm +" 권하 Layer 좌표 검색 실패(중복) ";
							commUtils.printLog(logId, szLogMsg, "SL");
							return YfConstant.RETN_CD_FAILURE;
						}
					}
				}
			}
  
			
			jsDnLayerXy.first();
			JDTORecord jrDnLayerXy = jsDnLayerXy.getRecord();
			
			JDTORecord jrUpCrnSch = JDTORecordFactory.getInstance().create();
			jrUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);										//크레인스케줄ID
			
			//권상정보   					
			jrUpCrnSch.setField("YD_UP_WO_LOC", 			szYD_UP_WO_LOC);										//권상지시위치
			jrUpCrnSch.setField("YD_UP_WO_LYR", 			szYD_UP_WO_LAYER);										//권상지시단
			jrUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_WO_LOC.substring(0, 6));						//권상지시위치 - 적치열
			jrUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_WO_LOC.substring(6));							//권상지시위치 - 적치베드
			jrUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_X_AXIS"  ))) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_Y_AXIS"  ))) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_LYR_Z_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MAX",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MIN",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MAX",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MIN",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS1",  	"" ) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS2",  	"" ) ;
			jrUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("UP_ROTATION_ANGLE",  		commUtils.trim(jrUpLayerXy.getFieldString("ROTATION_ANGLE"  )) ) ;
			//권하정보   					
			jrUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);										//권하지시위치
			jrUpCrnSch.setField("YD_DN_WO_LYR", 			szYD_DN_WO_LAYER);										//권하지시단
			jrUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_WO_LOC.substring(0, 6));						//권하지시위치 - 적치열
			jrUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_STK_BED_NO);							//권하지시위치 - 적치베드
			
			
			jrUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_LYR_X_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_LYR_Y_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_LYR_Z_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MAX",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MIN",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MAX",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MIN",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS1",  	"" ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS2",  	"" ) ;
			jrUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("DOWN_ROTATION_ANGLE",  	commUtils.trim(jrDnLayerXy.getFieldString("ROTATION_ANGLE"  )) ) ;
	
	
			//기타   					
			jrUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
			jrUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
			jrUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);									//크레인작업재료 중 최대 폭
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);									//크레인작업재료 중 최대 길이
			jrUpCrnSch.setField("MODIFIER", 				szMODIFIER);
			
			intRtnVal = commDao.update(jrUpCrnSch, updYdCrnWrkSidedelyn, logId, methodNm, "크레인스케쥴 갱신");
			if (intRtnVal <= 0) {
				szLogMsg =  "확인:"+szSTOCK_ID+"권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단[" +szYD_DN_WO_LAYER +" ]을 크레인스케줄에 수정 중 ERROR 발생";

				commUtils.printLog(logId, szLogMsg, "SL");
				return YfConstant.RETN_CD_FAILURE;
			}
			
			//크레인스케줄 재료에 행선 set
			jrUpCrnSch.setField("YD_ROUTE_GP", 			szYD_ROUTE_GP);
			jrUpCrnSch.setField("STL_NO", 				szSTOCK_ID);
			commDao.update(jrUpCrnSch, updYdCrnWrkMtl, logId, methodNm, "크레인스케쥴재료 갱신");

			
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("YD_STK_COL_GP", 	szYD_DN_WO_LOC.substring(0, 6));
			jrParam.setField("YD_STK_BED_NO", 	szYD_DN_STK_BED_NO);
			jrParam.setField("YD_STK_LYR_NO", 	commUtils.stringPlusInt(szYD_DN_WO_LAYER,0));
			jrParam.setField("STL_NO",       	szSTOCK_ID);
			jrParam.setField("YD_STK_LYR_STAT", ("00".equals(szYD_DN_STK_BED_NO))?"":"D"); //설비 보급위치는 예약(D)하지 않는다.
			if ("Y".equals(sAPP000_YN)) {
				commUtils.printLog(logId, "TB_YF_STKLYR 권하위치 수정", "[INFO]");
				if (yfComm.execQueryId(jrParam, updYdStkLyrOnlyEmpty)) {
					intRtnVal = 1;
				} else {
					intRtnVal = 0;
				}
			} else {
				intRtnVal = commDao.update(jrParam, updYdStkLyrOnlyEmpty, logId, methodNm, "TB_YF_STKLYR 갱신");
			}
		
			//저장품에 등록할 위치
			if (intRtnVal <= 0) {
				szLogMsg =LocalmethodNm +"확인:"+szSTOCK_ID+"권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]으로 권하 예약 실패" ;
				throw new Exception(szLogMsg);
			}
		
			szLogMsg =LocalmethodNm +" 크레인스케쥴 ID["+szYD_CRN_SCH_ID+"] TO위치결정>>>>>>>> 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]" ;
			commUtils.printLog(logId, szLogMsg, "SL");

			commUtils.printLog(logId, methodNm, "S-");

		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}		
		return YfConstant.RETN_CD_SUCCESS;
	}   
    
    
    
    
	/**
	 *      [A] 오퍼레이션명 : 스케줄 To위치 로그 Log
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insSchLog(JDTORecord jrParam) throws DAOException {
		String methodNm = "스케줄 To위치 로그 Log[ACoilSchSeEJB.insSchLog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			String strContents = "";
			JDTORecord row = JDTORecordFactory.getInstance().create();
			//jrParam.setField("SCH_CONTENTS"	, "대상코일위치검색실패:"+ szSTOCK_ID+" LOG :"+"\r\n" );
			
			String sAPP310_YN = yfComm.ACoilApplyYn("APP310","1","*");  
			
			commUtils.printLog(logId,  "실좌표적용여부:" + sAPP310_YN, "SL");	
			
			if("Y".equals(sAPP310_YN) ) 
			{
				/*
				jrParam.getFieldString("STL_NO"  );		//권상 STOCK
				jrParam.getFieldString("YD_SCH_CD");		//스케줄 코드
				jrParam.getFieldString("YD_EQP_ID");		//설비ID
				jrParam.getFieldString("YD_CRN_SCH_ID");	//크레인 스케쥴 ID
				jrParam.getFieldString("YD_ZONE_GP"); 	//야드존구분
				jrParam.getFieldString("BIZ_GP"); 	*/
				
				
				strContents = ""
							+ jrParam.getFieldString("STL_NO")+","
							+ "TO위치검색 실패:"+jrParam.getFieldString("SCH_CONTENTS")+","
							+ jrParam.getFieldString("GRIDE")+","
							+ jrParam.getFieldString("PRIOR0")+","
							+ jrParam.getFieldString("PRIOR1")+","
							+ jrParam.getFieldString("PRIOR2")+","
							+ jrParam.getFieldString("PRIOR3")+","
							+ jrParam.getFieldString("PRIOR4")+","
							+ jrParam.getFieldString("YD_EQP_ID")+","
							+ "," 
							+ "," 
							+ "," 
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ ","
							+ "," 
							+ "," 
							+ "," 
							
							+ jrParam.getFieldString("BIZ_GP")+","
							+ jrParam.getFieldString("YD_ZONE_GP")+","
							+ jrParam.getFieldString("YD_EQP_PROG_STAT")+","
							;
				
				row.setField("STL_NO", jrParam.getFieldString("STL_NO"));
				row.setField("YD_CRN_SCH_ID", jrParam.getFieldString("YD_CRN_SCH_ID"));
				row.setField("YD_GP", "1");
				row.setField("YD_SCH_CD", jrParam.getFieldString("YD_SCH_CD"));
				row.setField("SCH_CONTENTS", strContents);
				row.setField("SORT_SEQ", "0");
				
				commUtils.printLog(logId,  "실좌표적용여부:" + sAPP310_YN, "SL");	
				
				logId = row.getResultCode();
				methodNm = "스케줄 To위치 로그 Log[ACoilSchSeEJB.insSchLog] < "+row.getResultMsg();
				commDao.update(row, insSchLog, logId, methodNm, "스케줄 To위치 로그");
			
			}
			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 스케줄 To위치 로그 Log
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insSchLogs(String logId, String methodNms, JDTORecordSet set, String prefix, String schId, String schCd) throws DAOException {
		String methodNm = "스케줄 To위치 로그 Log[ACoilSchSeEJB.insSchLogs] < " + methodNms;
		String strContents = "";
		JDTORecord row = JDTORecordFactory.getInstance().create();
		commUtils.printLog(logId, methodNm, "S+");
		try {
			String sAPP310_YN = yfComm.ACoilApplyYn("APP310","1","*");
			
			if("Y".equals(sAPP310_YN) ) 
			{
				int size = set.size();
				for(int i = 0; i < size; i++)
				{
					strContents = prefix
								+ set.getRecord(i).getFieldString("STL_NO")+","
								+ set.getRecord(i).getFieldString("LOC_ABLE_CHK")+","
								+ set.getRecord(i).getFieldString("GRIDE")+","
								+ set.getRecord(i).getFieldString("PRIOR0")+","
								+ set.getRecord(i).getFieldString("PRIOR1")+","
								+ set.getRecord(i).getFieldString("PRIOR2")+","
								+ set.getRecord(i).getFieldString("PRIOR3")+","
								+ set.getRecord(i).getFieldString("PRIOR4")+","
								+ set.getRecord(i).getFieldString("YD_EQP_ID")+","
								+ set.getRecord(i).getFieldString("TAG_YD_STK_COL_GP")+","
								+ set.getRecord(i).getFieldString("TAG_YD_STK_BED_NO")+","
								+ set.getRecord(i).getFieldString("TAG_YD_STK_LYR_NO")+","
								+ set.getRecord(i).getFieldString("TAG_COL_USE_CD")+","
								+ set.getRecord(i).getFieldString("C_OUTDIA")+","
								+ set.getRecord(i).getFieldString("C_WIDTH")+","
								+ set.getRecord(i).getFieldString("C_WEIGTH")+","
								+ set.getRecord(i).getFieldString("C_THICK")+","
								+ set.getRecord(i).getFieldString("L_COIL_NO")+","
								+ set.getRecord(i).getFieldString("L_OUTDIA")+","
								+ set.getRecord(i).getFieldString("L_WIDTH")+","
								+ set.getRecord(i).getFieldString("L_WEIGTH")+","
								+ set.getRecord(i).getFieldString("L_THICK")+","
								+ set.getRecord(i).getFieldString("R_COIL_NO")+","
								+ set.getRecord(i).getFieldString("R_OUTDIA")+","
								+ set.getRecord(i).getFieldString("R_WIDTH")+","
								+ set.getRecord(i).getFieldString("R_WEIGTH")+","
								+ set.getRecord(i).getFieldString("R_THICK")+","
								
								+ set.getRecord(i).getFieldString("L_WB")+","
								+ set.getRecord(i).getFieldString("R_WB")+","
								+ set.getRecord(i).getFieldString("L_ISHOT")+","
								+ set.getRecord(i).getFieldString("R_ISHOT")+","
								+ set.getRecord(i).getFieldString("C_JJANG_GP")+","
								+ set.getRecord(i).getFieldString("R_JJANG_GP")+","
								+ set.getRecord(i).getFieldString("L_JJANG_GP")+","
								+ set.getRecord(i).getFieldString("BEFORE_COL_WID")+","
								+ set.getRecord(i).getFieldString("NEXT_COL_WID")+","
								+ set.getRecord(i).getFieldString("BEFORE_COL_X_AXIS")+","
								+ set.getRecord(i).getFieldString("NEXT_COL_X_AXIS")+","
								+ set.getRecord(i).getFieldString("L2DAN_OUTDIA")+","
								+ set.getRecord(i).getFieldString("R2DAN_OUTDIA")+","
								+ set.getRecord(i).getFieldString("C_YAXIS_AUTO")+","
								+ set.getRecord(i).getFieldString("L_YAXIS_AUTO")+","
								+ set.getRecord(i).getFieldString("R_YAXIS_AUTO")+","
								+ set.getRecord(i).getFieldString("INCLINE_CHK")+","
								
								+ set.getRecord(i).getFieldString("BIZ_GP")+","
								+ set.getRecord(i).getFieldString("YD_ZONE_GP")+","
								+ set.getRecord(i).getFieldString("YD_EQP_PROG_STAT")+","
								+ set.getRecord(i).getFieldString("USAGE_PRIOR")+","
								;
					
					row.setField("STL_NO", set.getRecord(i).getFieldString("STL_NO"));
					row.setField("YD_CRN_SCH_ID", schId);
					row.setField("YD_GP", "1");
					row.setField("YD_SCH_CD", schCd);
					row.setField("SCH_CONTENTS", strContents);
					row.setField("SORT_SEQ", Integer.toString(i));
					
					logId = row.getResultCode();
					methodNm = "스케줄 To위치 로그 Log[ACoilSchSeEJB.insSchLogs] < "+row.getResultMsg();
					commDao.update(row, insSchLog, logId, methodNm, "스케줄 To위치 로그");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	

	/**
	 * 보조작업TO위치결정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecordSet procToLocDummy(String logId, String methodNms, JDTORecord wbRow, JDTORecord schRow) throws JDTOException {
    	String methodNm = "TO위치결정:보조작업[ACoilSchSeEJB.procToLocDummy] < " + methodNms;
    	commUtils.printLog(logId, methodNm, "S+");

		try {
			
			/** 
			 * 1. LOG 기본 파라미터 SET 
			 * 
			 * */
			String szLogMsg					= null;
			JDTORecord jrTemp				= null;
			JDTORecordSet	jsRtn = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			String szYD_SCH_CD 	   		= commUtils.trim(wbRow.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
			String szYD_WBOOK_ID		= commUtils.trim(wbRow.getFieldString("YD_WBOOK_ID"));			//작업예약
			String szSTOCK_ID	   		= commUtils.trim(schRow.getFieldString("STL_NO"));			//크레인작업재료
			String szYD_CRN_SCH_ID 		= commUtils.trim(schRow.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
			String szYD_EQP_ID     		= commUtils.trim(schRow.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String szYD_UP_WO_LOC 		= commUtils.trim(schRow.getFieldString("YD_UP_WO_LOC"));		
			String szYD_UP_WO_LAYER 	= commUtils.trim(schRow.getFieldString("YD_UP_WO_LYR"));	
			String sAPP005_YN = yfComm.ACoilApplyYn("APP005","1","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");		
			
			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
			jrLog.setField("STL_NO"			, szSTOCK_ID);
			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
			jrLog.setField("YD_GP"			, "1");
			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
			
			
			
			/** 
			 * 2. 권상위치 이상 CHECK 
			 * 
			 * */
			if ("".equals(szYD_UP_WO_LOC)) 
			{
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new Exception(szLogMsg);
			}
			
						
			
			/** 
			 * 3. DUMMY 적치가능 베드 조회 
			 * 
			 * */
			commUtils.printLog(logId, " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작", "SL");
			
			jrTemp = JDTORecordFactory.getInstance().create();
	    	jrTemp.setField("STL_NO"      	, szSTOCK_ID);											//권상 STOCK
			jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
			jrTemp.setField("YD_UP_WO_LOC"  , szYD_UP_WO_LOC);	
			jrTemp.setField("BIZ_GP"  		, "2"); //1:주작업 2:더미 3:사용자 4:차공정 5:진도 6:zone	
			JDTORecordSet bedSet = commDao.select(jrTemp, getYfToLocMainSql, logId, methodNm, "동일한 적치가능한 베드 조회");
			
			
			
			/** 
		     * 4. 크레인현황조회 화면이라면 적재위치 list 출력하고 리턴.
		     * 
		     * */
			JDTORecord bedRow	= JDTORecordFactory.getInstance().create();
			JDTORecord udpRow = JDTORecordFactory.getInstance().create();
			if( methodNms.indexOf("procReSch") > 0)
			{	
				for (int i = 0; i < bedSet.size(); i++) 
				{
	
					bedRow  = bedSet.getRecord(i);
					if("PASS".equals(bedRow.getFieldString("LOC_ABLE_CHK")))
					{
						
						JDTORecord 	jrRtn = JDTORecordFactory.getInstance().create();
						jrRtn.setField("SEARCH"		, "D");
						jrRtn.setField("GRADE"		, bedRow.getFieldString("GRIDE"));
						jrRtn.setField("SEARCH_LOC"	, bedRow.getFieldString("TAG_YD_STK_DAN_GP")); 
						jsRtn.addRecord(jrRtn);
					}
				}
				return jsRtn;
			}
			
			
		    /** 
		     * 5. To위치 선별
		     * 
		     * */
			String finalToLoc ="";
			for (int i = 0; i < bedSet.size(); i++) 
			{

				bedRow  = bedSet.getRecord(i);
				if("PASS".equals(bedRow.getFieldString("LOC_ABLE_CHK")))
				{
					finalToLoc = bedRow.getFieldString("TAG_YD_STK_DAN_GP");
					break;
				}
			}	
			
			
			/** 
		     * 6. To위치 UPDATE 
		     * 
		     * */
			if (!"".equals(finalToLoc)) 
			{
				udpRow.setField("YD_CRN_SCH_ID",	szYD_CRN_SCH_ID); 
				udpRow.setField("YD_EQP_ID",		szYD_EQP_ID);	 
				udpRow.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
				udpRow.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
				udpRow.setField("YD_UP_WO_LYR",		szYD_UP_WO_LAYER);	 
				udpRow.setField("YD_DN_WO_LOC", 	finalToLoc.substring(0,8));
				udpRow.setField("YD_DN_WO_LYR",		finalToLoc.substring(8,10));
				udpRow.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID); 
					
				this.procUpdateLoc(logId,methodNm, udpRow, schRow  );
			}
			
			
			/** 
			 * 7. 처리 결과 LOG 
			 *    
			 * */			
			if ("Y".equals(sAPP005_YN))
			{
				if(bedSet != null && bedSet.size() > 0) 
				{
					EJBConnector SchLog = new EJBConnector("default", "ACoilSchSeEJB", this);
	    			SchLog.trx( "insSchLogs" , new Class[] {String.class, String.class, JDTORecordSet.class, String.class , String.class, String.class}
	    			, new Object[] { logId, methodNm, bedSet,"",szYD_CRN_SCH_ID, szYD_SCH_CD });
				}
				else
				{
					EJBConnector SchLog = new EJBConnector("default", "ACoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrTemp });
				}
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");
			return jsRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch			
	}
	
	
	/**
	 * TO위치 RULE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procSchSule(String logId, String methodNms) throws JDTOException {
    	String methodNm = "TO위치 RULE [ACoilSchSeEJB.procSchSule] < " + methodNms;
		JDTORecord jrResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrOutResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrParam   = commUtils.getParam(logId, methodNm, "");
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			jrParam.setField("REPR_CD_GP", "SCH_TO"  ); //작업구분
			jrParam.setField("CD_GP"     , "1"      ); 	//공장구분
			JDTORecordSet jsSchToLocRule = commDao.select(jrParam, getSchToLocRule, logId, methodNm, "스케줄 기준 Read"); 
		    // 평점 CEHCK	
			for (int i = 1; i <= jsSchToLocRule.size(); i++) 
			{
	 
				jsSchToLocRule.absolute(i);
				jrResult  = jsSchToLocRule.getRecord();
				if ("ODIA_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) 
				{     // 1단외경편차
					jrOutResult.setField("ODIA_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITEM1"), "180")); 
	        	}
				if ("WID_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) 
				{      // 1단폭편차
					jrOutResult.setField("WID_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITEM1"), "200")); 
	        	}
				if ("ODIA_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) 
				{     // 2단외경간격
					jrOutResult.setField("ODIA_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITEM1"), "550")); 
	        	}
				if ("BED_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) 
				{     // 2단BED길이
					jrOutResult.setField("BED_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITEM1"), "1300")); 
	        	}
				if ("WID_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) 
				{      // 2단폭편차
					jrOutResult.setField("WID_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITEM1"), "0")); 
	        	}
				if ("WGT_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) 
				{      // 2단중량편차
					jrOutResult.setField("WGT_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITEM1"), "0")); 
	        	}
				if ("WID_LIFT1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) 
				{      // LIFT 간격
					jrOutResult.setField("WID_LIFT1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITEM1"), "650")); 
	        	}
				if ("WID_SKID1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) 
				{      // SKID 폭
					jrOutResult.setField("WID_SKID1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITEM1"), "1000")); 
	        	}
				if ("SKID_SKID".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) 
				{      // SKID 간격
					jrOutResult.setField("SKID_SKID", 	commUtils.nvl(jrResult.getFieldString("DTL_ITEM1"), "1150")); 
	        	}
			}
			
			String sAPP044_A_YN = yfComm.ACoilApplyYn("APP044","1","A");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_B_YN = yfComm.ACoilApplyYn("APP044","1","B");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_C_YN = yfComm.ACoilApplyYn("APP044","1","C");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_D_YN = yfComm.ACoilApplyYn("APP044","1","D");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_E_YN = yfComm.ACoilApplyYn("APP044","1","E");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			jrOutResult.setField("WID_CHK_A", sAPP044_A_YN); 
			jrOutResult.setField("WID_CHK_B", sAPP044_B_YN); 
			jrOutResult.setField("WID_CHK_C", sAPP044_C_YN); 
			jrOutResult.setField("WID_CHK_D", sAPP044_D_YN); 
			jrOutResult.setField("WID_CHK_E", sAPP044_E_YN); 
        	

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jrOutResult;
	}   
    
    
    
    
    
	
	
	
	
	
	
	/**
	 * 사용자지정작업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecordSet procToLocUser(String logId, String methodNms, JDTORecord wbRow, JDTORecord schRow) throws JDTOException {
    	String methodNm = "TO위치결정:사용자지정작업[ACoilSchSeEJB.procToLocUser] < " + methodNms;
    	commUtils.printLog(logId, methodNm, "S+");	

		try {
			
			/** 
			 * 1. LOG 기본 파라미터 SET 
			 * 
			 * */
			String szLogMsg					= null;
			JDTORecord	jrTemp				= null;
			JDTORecordSet jsRtn 			= JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecordSet bedSet 			= JDTORecordFactory.getInstance().createRecordSet("Temp");

			String szYD_SCH_CD 	   		= commUtils.trim(wbRow.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
			String szYD_WBOOK_ID		= commUtils.trim(wbRow.getFieldString("YD_WBOOK_ID"));			//작업예약
			String ydToLocGuide			= commUtils.trim(wbRow.getFieldString("YD_TO_LOC_GUIDE"));		//야드To위치Guide
			String szSTOCK_ID	   		= commUtils.trim(schRow.getFieldString("STL_NO"));				//크레인작업재료
			String szYD_CRN_SCH_ID 		= commUtils.trim(schRow.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
			String szYD_EQP_ID     		= commUtils.trim(schRow.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String szYD_UP_WO_LOC 		= commUtils.trim(schRow.getFieldString("YD_UP_WO_LOC"));		
			String szYD_UP_WO_LAYER 	= commUtils.trim(schRow.getFieldString("YD_UP_WO_LYR"));
			String sAPP005_YN = yfComm.ACoilApplyYn("APP005","1","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");		
			
			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
			jrLog.setField("STL_NO"			, szSTOCK_ID);
			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
			jrLog.setField("YD_GP"			, "1");
			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
			
			
			
			/** 
			 * 2. 권상위치 이상 CHECK 
			 * 
			 * */
			if ("".equals(szYD_UP_WO_LOC)) 
			{
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new Exception(szLogMsg);
			}
			
						
			
			/** 
			 * 3. 사용자지정 적치가능 베드 조회 
			 * 
			 * */
			commUtils.printLog(logId, " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작", "SL");
			String finalToLoc = "";
			if (ydToLocGuide.length() == 10) 
			{
				commUtils.printLog(logId, " 적재위치 가이드 열+ 베드+ 단  지정된 경우 ["+ydToLocGuide+"]의 베드 지정", "SL");
				finalToLoc = ydToLocGuide;
			} 
			else 
			{
				jrTemp = JDTORecordFactory.getInstance().create();
		    	jrTemp.setField("STL_NO"      		, szSTOCK_ID);	//권상 STOCK
		    	jrTemp.setField("YD_TO_LOC_GUIDE"	, ydToLocGuide);									//가이드
				jrTemp.setField("YD_SCH_CD"			, szYD_SCH_CD);		//스케줄 코드
				jrTemp.setField("YD_EQP_ID"			, szYD_EQP_ID);		//설비ID
				jrTemp.setField("YD_CRN_SCH_ID"		, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
				jrTemp.setField("YD_UP_WO_LOC"  	, szYD_UP_WO_LOC);	
				jrTemp.setField("BIZ_GP"  			, "3"); //1:주작업 2:더미 3:사용자 4:차공정 5:진도 6:zone
				bedSet = commDao.select(jrTemp, getYfToLocMainSql, logId, methodNm, "동일한 적치가능한 베드 조회");
				
				
				JDTORecord bedRow = JDTORecordFactory.getInstance().create();
				for (int i = 0; i < bedSet.size(); i++) 
				{

					bedRow  = bedSet.getRecord(i);
					if("PASS".equals(bedRow.getFieldString("LOC_ABLE_CHK")))
					{
						finalToLoc = bedRow.getFieldString("TAG_YD_STK_DAN_GP");
						break;
					}
				}	
			}
			
			
	
			
			/** 
		     * 4. 크레인현황조회 화면이라면 적재위치 list 출력하고 리턴.
		     * 
		     * */
			JDTORecord bedRow	= JDTORecordFactory.getInstance().create();
			JDTORecord udpRow = JDTORecordFactory.getInstance().create();
			if( methodNms.indexOf("procReSch") > 0)
			{	
				for (int i = 0; i < bedSet.size(); i++) 
				{
	
					bedRow  = bedSet.getRecord(i);
					if("PASS".equals(bedRow.getFieldString("LOC_ABLE_CHK")))
					{
						
						JDTORecord 	jrRtn = JDTORecordFactory.getInstance().create();
						jrRtn.setField("SEARCH"		, "U");
						jrRtn.setField("GRADE"		, bedRow.getFieldString("GRIDE"));
						jrRtn.setField("SEARCH_LOC"	, bedRow.getFieldString("TAG_YD_STK_DAN_GP")); 
						jsRtn.addRecord(jrRtn);
					}
				}
				return jsRtn;
			}
			
			
			
		    /** 
		     * 5. To위치 UPDATE 
		     * 
		     * */
			if (!"".equals(finalToLoc)) 
			{
				udpRow.setField("YD_CRN_SCH_ID",	szYD_CRN_SCH_ID); 
				udpRow.setField("YD_EQP_ID",		szYD_EQP_ID);	 
				udpRow.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
				udpRow.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
				udpRow.setField("YD_UP_WO_LYR",		szYD_UP_WO_LAYER);	 
				udpRow.setField("YD_DN_WO_LOC", 	finalToLoc.substring(0,8));
				udpRow.setField("YD_DN_WO_LYR",		finalToLoc.substring(8,10));
				udpRow.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID); 
				this.procUpdateLoc(logId,methodNm, udpRow, schRow  );
			}
			
			
			/** 
			 * 6. 처리 결과 LOG 
			 *    
			 * */			
			if ("Y".equals(sAPP005_YN))
			{
				if(bedSet != null && bedSet.size() > 0) 
				{
					EJBConnector SchLog = new EJBConnector("default", "ACoilSchSeEJB", this);
	    			SchLog.trx( "insSchLogs" , new Class[] {String.class, String.class, JDTORecordSet.class, String.class , String.class, String.class}
	    			, new Object[] {logId, methodNm, bedSet,"",szYD_CRN_SCH_ID, szYD_SCH_CD });
				}
				else if (ydToLocGuide.length() < 10)
				{
					EJBConnector SchLog = new EJBConnector("default", "ACoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrTemp });
				}
			}
		    
			
			commUtils.printLog(logId, methodNm, "S-");
			return jsRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch	
	} 
    
    
    
	
	
	/**
	 * 설비 보급 작업TO위치결정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocConveyor(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "TO위치결정:설비보급작업[ACoilSchSeEJB.procToLocConveyor] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));		//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LYR"));
		String szBAY_GP  			= commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD").substring(1, 2)); // 동		
		String sYD_TO_LOC_GUIDE 	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));   //사용자지정위치
		
		String sRtnBed           	= "";
		String sRtnBedDan           = "";
		
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			if ( szYD_UP_WO_LOC.equals("") ) 
			{
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YfConstant.RETN_CD_FAILURE;
			}

			if ("1GHS01UM".equals(szYD_SCH_CD)) //HFL 결속장
			{
				sRtnBed = "1GHS0100";
			}
			 
			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
			jrSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
			jrSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
			jrSetLoc.setField("YD_UP_WO_LYR",	szYD_UP_WO_LAYER);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBed);
			jrSetLoc.setField("YD_DN_WO_LYR",   "01");
			jrSetLoc.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch  );
			
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YfConstant.RETN_CD_SUCCESS;
	}  
	
    /**
	 * 고정된 권하위치
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocFix(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "TO위치결정:고정된 권하위치 결정[ACoilSchSeEJB.procToLocFix] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));		//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STL_NO"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LYR"));
		String szBAY_GP  			= commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD").substring(1, 2)); // 동		
		String sYD_TO_LOC_GUIDE 	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));   //사용자지정위치
		
		String sRtnBed           	= "";
		String sRtnBedDan           = "";
		
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			if ( szYD_UP_WO_LOC.equals("") ) 
			{
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YfConstant.RETN_CD_FAILURE;
			}

			if("1FQE10UM".equals(szYD_SCH_CD)) //EQL재작업(F동)
   					
			{
				sRtnBed = "1FQE0101";
			}
			else if("1CDC02UM".equals(szYD_SCH_CD)) //DC TAKE IN
			{
				sRtnBed = "1CDC0101";
			}
			
			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
			jrSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
			jrSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
			jrSetLoc.setField("YD_UP_WO_LYR",	szYD_UP_WO_LAYER);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBed);
			jrSetLoc.setField("YD_DN_WO_LYR",   "01");
			jrSetLoc.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch  );
			
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YfConstant.RETN_CD_SUCCESS;
	} 
    
    
    
	/**
	 * 주작업TO위치결정  -> 야드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecordSet procToLocPrimaryWork(String logId, String methodNms, JDTORecord wbRow, JDTORecord schRow) throws JDTOException {
    	String methodNm = "주작업TO위치결정[ACoilSchSeEJB.procToLocPrimaryWork] < " + methodNms;
    	commUtils.printLog(logId, methodNm, "S+");	

		try {
			
			/** 
			 * 1. LOG 기본 파라미터 SET 
			 * 
			 * */
			String szLogMsg					= null;
			JDTORecord	jrTemp				= null;
			JDTORecordSet	jsRtn = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			String szYD_SCH_CD 	   		= commUtils.trim(wbRow.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
			String szYD_WBOOK_ID		= commUtils.trim(wbRow.getFieldString("YD_WBOOK_ID"));			//작업예약
			String szSTOCK_ID	   		= commUtils.trim(schRow.getFieldString("STL_NO"));			//크레인작업재료
			String szYD_CRN_SCH_ID 		= commUtils.trim(schRow.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
			String szYD_EQP_ID     		= commUtils.trim(schRow.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String szYD_UP_WO_LOC 		= commUtils.trim(schRow.getFieldString("YD_UP_WO_LOC"));		
			String szYD_UP_WO_LAYER 	= commUtils.trim(schRow.getFieldString("YD_UP_WO_LYR"));	
			String szYD_AIM_BAY_GP 		= "";
			String szYD_WRK_PLAN_TCAR	= "";
			String szYD_ROUTE_GP		= "";
			String sAPP005_YN = yfComm.ACoilApplyYn("APP005","1","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");		
			
			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
			jrLog.setField("STL_NO"			, szSTOCK_ID);
			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
			jrLog.setField("YD_GP"			, "1");
			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
			
			
			
			/** 
			 * 2. 권상위치 이상 CHECK 
			 * 
			 * */
			if ("".equals(szYD_UP_WO_LOC)) 
			{
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new Exception(szLogMsg);
			}
			
			
			
			/** 
			 * 3. 주작업 적치가능 베드 조회 
			 * 
			 * */
			commUtils.printLog(logId, " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작", "SL");
			
			jrTemp = JDTORecordFactory.getInstance().create();
	    	jrTemp.setField("STL_NO"      	, szSTOCK_ID);		//권상 STOCK
			jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
			jrTemp.setField("BIZ_GP"  		, "1"); //1:주작업 2:더미 3:사용자 4:차공정 5:진도 6:zone
			JDTORecordSet bedSet = commDao.select(jrTemp, getYfToLocMainSql, logId, methodNm, "동일한 적치가능한 베드 조회");
			
	
			
			/** 
		     * 4. 크레인현황조회 화면이라면 적재위치 list 출력하고 리턴.
		     * 
		     * */
			JDTORecord bedRow	= JDTORecordFactory.getInstance().create();
			JDTORecord udpRow = JDTORecordFactory.getInstance().create();
			if( methodNms.indexOf("procReSch") > 0)
			{	
				for (int i = 0; i < bedSet.size(); i++) 
				{
	
					bedRow  = bedSet.getRecord(i);
					if("PASS".equals(bedRow.getFieldString("LOC_ABLE_CHK")))
					{
						
						JDTORecord 	jrRtn = JDTORecordFactory.getInstance().create();
						jrRtn.setField("SEARCH"		, "U");
						jrRtn.setField("GRADE"		, bedRow.getFieldString("GRIDE"));
						jrRtn.setField("SEARCH_LOC"	, bedRow.getFieldString("TAG_YD_STK_DAN_GP")); 
						jsRtn.addRecord(jrRtn);
					}
				}
				return jsRtn;
			}
			
			
			
		    /** 
		     * 5. To위치 선별 
		     * 
		     * */
			String finalToLoc ="";
			for (int i = 0; i < bedSet.size(); i++) 
			{

				bedRow  = bedSet.getRecord(i);
				if("PASS".equals(bedRow.getFieldString("LOC_ABLE_CHK")))
				{
					finalToLoc 			= bedRow.getFieldString("TAG_YD_STK_DAN_GP");
					szYD_AIM_BAY_GP 	= bedRow.getFieldString("YD_AIM_BAY_GP");
					szYD_WRK_PLAN_TCAR	= bedRow.getFieldString("YD_WRK_PLAN_TCAR");
					szYD_ROUTE_GP		= "".equals(bedRow.getFieldString("YD_ROUTE_GP"))?"GN":bedRow.getFieldString("YD_ROUTE_GP");
					jsRtn.addRecord(bedRow);
					
					break;
				}
			}	
			
			
			/** 
		     * 6. To위치 UPDATE 
		     * 
		     * */
			if (!"".equals(finalToLoc)) 
			{
				udpRow.setField("YD_CRN_SCH_ID",	szYD_CRN_SCH_ID); 
				udpRow.setField("YD_EQP_ID",		szYD_EQP_ID);	 
				udpRow.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
				udpRow.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
				udpRow.setField("YD_UP_WO_LYR",		szYD_UP_WO_LAYER);	 
				udpRow.setField("YD_DN_WO_LOC", 	finalToLoc.substring(0,8));
				udpRow.setField("YD_DN_WO_LYR",		finalToLoc.substring(8,10));
				udpRow.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
				udpRow.setField("YD_ROUTE_GP", 		szYD_ROUTE_GP);
				this.procUpdateLoc(logId,methodNm, udpRow, schRow  );
				
			}
			
			
			
			/** 
			 * 7. 처리 결과 LOG 
			 *    
			 * */			
			if ("Y".equals(sAPP005_YN))
			{
				if(bedSet != null && bedSet.size() > 0) 
				{
					EJBConnector SchLog = new EJBConnector("default", "ACoilSchSeEJB", this);
	    			SchLog.trx( "insSchLogs" , new Class[] {String.class , String.class, JDTORecordSet.class, String.class , String.class, String.class}
	    			, new Object[] {logId, methodNm, bedSet,"",szYD_CRN_SCH_ID, szYD_SCH_CD });
				}
				else
				{
					EJBConnector SchLog = new EJBConnector("default", "ACoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrTemp });
				}
			}
			
			
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jsRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		
	}  
    
    
	
	
	
 
	
	
	
	/**
     * 오퍼레이션명 : 코일크레인리스케줄(procReSch)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecordSet procReSch(String logId, String methodNms, JDTORecord inRecord) throws JDTOException{
    	String methodNm = "TO 위치 결정[ACoilSchSeEJB.procReSch] < " + methodNms;
    	
    	JDTORecord jrLocSrcRngRtn = JDTORecordFactory.getInstance().create();
    	String szMsg        		= "";     	  
    	
    	int intRtnVal 				= 0 ;
    	String TcarTcSndyn   		= "N";    // 대차이동지시 
    	JDTORecordSet jsRtn 		= JDTORecordFactory.getInstance().createRecordSet("");
    	
    	try{
        	commUtils.printLog(logId, methodNm, "S+");
        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
        	String szWbookId	= commUtils.trim(inRecord.getFieldString("YD_WBOOK_ID" ));	
        	String ydCrnSchId	= commUtils.trim(inRecord.getFieldString("YD_CRN_SCH_ID" ));	
        	String szEqpId 		= commUtils.trim(inRecord.getFieldString("YD_EQP_ID"   ));	
        	String szSearchYd	= commUtils.trim(inRecord.getFieldString("SEARCH_YD"   ));	
       		
			//-------------------------------------------------------------------------------------------------------------
			//작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			//-------------------------------------------------------------------------------------------------------------
        	JDTORecordSet jsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");
			jsTemp = commDao.select(inRecord, getYmWrkbook, logId, methodNm, "작업예약 조회"); 
	    	
	    	if (jsTemp == null || jsTemp.size() <= 0) {
				commUtils.printLog(logId, methodNm + "[작업예약종료]", "SL");
				
    			jrLocSrcRngRtn.setField("RTN", "-1");

				return jsRtn;
				
			}			
			
			jsTemp.absolute(1);
			JDTORecord jrWbook = JDTORecordFactory.getInstance().create();
			jrWbook.setRecord(jsTemp.getRecord());
			
			String szSchCd 	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));
			String StockId 	= commUtils.trim(jrWbook.getFieldString("STL_NO"));
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			JDTORecordSet jsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
			jrInPara.setField("YD_WBOOK_ID"		, szWbookId);
			jrInPara.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
			jrInPara.setField("YD_EQP_ID"		, szEqpId);
			jsCrnsch = commDao.select(inRecord, getYdCrnSchByWBookIdALL, logId, methodNm, "크레인스케줄 조회"); 
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
	    	String szToLocDcsnMtd 	= "";
	    	String szToLocGuide 	= "";
			String sWrkFlag 		= "";
			JDTORecord jrCrnSch		= JDTORecordFactory.getInstance().create();

			// A동 대차 하차 작업 인 경우 
			String sDIR_YN = "N";
			
		    for(int i = 0; i < jsCrnsch.size(); i++) 
		    {

        		jrCrnSch  = jsCrnsch.getRecord(i);
        		
        		//크레인스케줄Data저장
        		ydCrnSchId     = jrCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = jrCrnSch.getFieldString("YD_SCH_CD");
        		szToLocDcsnMtd = jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");
        		StockId 	   = jrCrnSch.getFieldString("STL_NO");
        		szToLocGuide   = jrCrnSch.getFieldString("YD_TO_LOC_GUIDE");
        		
        		szMsg = "작업예약 " + szWbookId + " ["+ i +"]번째 크레인 스케줄[" + ydCrnSchId + "]에 대한 권하지시위치 결정 "; //szWbookId
        		commUtils.printLog(logId, szMsg, "SL");
        		
       			if ("W".equals(szToLocDcsnMtd)) 
       			{
            		/**********************************************************
    				* 보조작업인 경우 TO위치 결정 (일반적치대로...)
    				**********************************************************/            		
            		szMsg = "["+ i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 보조작업 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			if("YD".equals(szSchCd.substring(2,4))) 
        			{
        				sWrkFlag = "D";
        				jsRtn = this.procToLocPrimaryWorkMulti(logId, methodNm, sWrkFlag, szSearchYd, jrWbook, jrCrnSch);
        			} 
        			else 
        			{	
        				jsRtn = this.procToLocDummy(logId, methodNm, jrWbook, jrCrnSch);
        			}	

            	} 
            	else if( "PT".equals(szSchCd.substring(2,4)) && ("U".equals(szSchCd.substring(6,7)))) 
            	{
            		//jsRtn = this.procCarToLoc(logId, methodNm, jrWbook, jrCrnSch);
            	} 
            	else if (szToLocGuide.length() >= 4) 
            	{
            		/**********************************************************
    				* 사용자 지정 :
    				**********************************************************/            		
            		szMsg = "["+ i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			// 대차 직보급
        			if(sDIR_YN.equals("Y")) 
        			{
    					jrWbook.setField("YD_TO_LOC_GUIDE", szToLocGuide);
    				}
        			
        			jsRtn = this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);
        			
    			} 
            	else 
    			{
    				/*****************************************************************************
    				* 일반작업 TO위치 
    				* TO위치가 없을 경우  동내이적인 경우 XXXX
    				******************************************************************************/      

    				szMsg =  "[" + i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-야드로 TO위치결정 시작";
    				commUtils.printLog(logId, szMsg, "SL");

    				jsRtn = this.procToLocPrimaryWork(logId, methodNm, jrWbook, jrCrnSch);
    				
    				
    				if(jsRtn.size() == 0) 
    				{
    					sWrkFlag = "S";
    					jsRtn = this.procToLocPrimaryWorkMulti(logId, methodNm, sWrkFlag, szSearchYd, jrWbook, jrCrnSch);
    				}
    				        				
    				szMsg =  "[" + i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 주작업 스케줄의 To위치 결정 완료 ";
        			commUtils.printLog(logId, szMsg, "SL");
    			}
        	}
        	
		//-------------------------------------------------------------------------------------------------------------
    		
        	commUtils.printLog(logId, methodNm, "S-");
        	
			return jsRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of procReSch()
    
    
    
    
    
    
    
	/**
	 * 주작업TO위치결정  -> 야드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecordSet procToLocPrimaryWorkMulti(String logId, String methodNms, String sWrkFlag, String szSearchYd, JDTORecord wbRow, JDTORecord schRow) throws JDTOException {
    	String methodNm = "주작업TO위치결정[BCoilReSchSeEJB.procToLocPrimaryWorkMulti] < " + methodNms;
    	String szLogMsg					= null;
		
		try 
		{
			commUtils.printLog(logId, methodNm, "S+");
			
			
			/** 
		     * 1. 주요 파라미터 set
		     * 
		     * */
			JDTORecord		jrTemp			= null;
			JDTORecordSet	jsRtn = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			String szYD_SCH_CD 	   		= commUtils.trim(wbRow.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
			String szSTOCK_ID	   		= commUtils.trim(schRow.getFieldString("STOCK_ID"));			//크레인작업재료
			String szYD_CRN_SCH_ID 		= commUtils.trim(schRow.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
			String szYD_EQP_ID     		= commUtils.trim(schRow.getFieldString("YD_EQP_ID"));			//크레인설비ID
			String szYD_UP_WO_LOC 		= commUtils.trim(schRow.getFieldString("YD_UP_WO_LOC"));		
			
			if( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jsRtn;
			}
						
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"		, szSTOCK_ID);		//권상 STOCK
			
			if("D".equals(sWrkFlag)) {
				// 일반 Dummy 검색
				jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			} else {
				// 주작업 실패 검색
				jrTemp.setField("YD_SCH_CD"		, szSearchYd );		//스케줄 코드
			}
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
			jrTemp.setField("YD_UP_WO_LOC"  , szYD_UP_WO_LOC);
			jrTemp.setField("SEARCH_ALL"    , "Y");
			
			szLogMsg =  " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			
	    	
			/** 
		     * 2. 적치가능 베드 조회
		     * 
		     * */
			JDTORecordSet bedSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	String szStackColGp 	= "";
			String szStackBedGp 	= "";
			String szStackLayerGp 	= "";	
			String szToPosGrade 	= "999";

			String sRtnBedDan 		= "";	
			String sSearchGp 		= "Z";	
			String szGRIDE 			= "9";
			
			// 검색순서 : 1.차공정(N) 2.진도코드(P) 3.일반재(G) 99. 종료(END)
			for (int i = 0; i < 3; i++) 
			{
				
				if(i == 0) {
					jrTemp.setField("BIZ_GP"  			, "4"); //1:주작업 2:더미 3:사용자 4:차공정 5:진도 6:zone
					bedSet = commDao.select(jrTemp, getYfToLocMainSql, logId, methodNm, "동일한 적치가능한 베드 조회");
					sSearchGp = "A";
				}
				if(i == 1) {
					jrTemp.setField("BIZ_GP"  			, "5"); //1:주작업 2:더미 3:사용자 4:차공정 5:진도 6:zone
					bedSet = commDao.select(jrTemp, getYfToLocMainSql, logId, methodNm, "동일한 적치가능한 베드 조회");
					sSearchGp = "B";
				}
				if(i == 2) {
					jrTemp.setField("BIZ_GP"  			, "1"); //1:주작업 2:더미 3:사용자 4:차공정 5:진도 6:zone
					bedSet = commDao.select(jrTemp, getYfToLocMainSql, logId, methodNm, "동일한 적치가능한 베드 조회");
					sSearchGp = "C";
				}
				
				if(bedSet.size()>0) break;
			}
				
			
			/** 
		     * 3. To위치 UPDATE 
		     * 
		     * */
			JDTORecord bedRow	= JDTORecordFactory.getInstance().create();
			JDTORecord udpRow = JDTORecordFactory.getInstance().create();
			for (int i = 0; i < bedSet.size(); i++) 
			{

				bedRow  = bedSet.getRecord(i);
				if("PASS".equals(bedRow.getFieldString("LOC_ABLE_CHK")))
				{
					
					JDTORecord 	jrRtn = JDTORecordFactory.getInstance().create();
					jrRtn.setField("SEARCH"		, sSearchGp);
					jrRtn.setField("GRADE"		, bedRow.getFieldString("GRIDE"));
					jrRtn.setField("SEARCH_LOC"	, bedRow.getFieldString("TAG_YD_STK_DAN_GP")); 
					jsRtn.addRecord(jrRtn);
				}
			}	
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jsRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch		
	}
    
    
    
    
    
    
	/**
	 * 신기본 BASE CHECK
	 * 화면권하위치변경 변경 에서 사용
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public String procStockIdBaseCheckNew(String logId, String methodNms,JDTORecord jrLayer) throws JDTOException {
    	String methodNm = "화면권하위치변경 CHECK[ACoilSchSeEJB.procStockIdBaseCheckNew] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		
		
		try {
			/** 
			 * 1. 기본 파라미터 set
			 *    
			 * */	
			String StockId	   	= commUtils.trim(jrLayer.getFieldString("STL_NO"));			//크레인작업재료
			String ydSchCd      = commUtils.trim(jrLayer.getFieldString("YD_SCH_CD"));		    //스케쥴 코드
			String ydCrnSchId   = commUtils.trim(jrLayer.getFieldString("YD_CRN_SCH_ID"));		//스케쥴 ID
			String StackColGp 	= commUtils.trim(jrLayer.getFieldString("YD_STK_COL_GP"));		//열
			String StackBedGp   = commUtils.trim(jrLayer.getFieldString("YD_STK_BED_NO"));		//BED
			String StackLayerGp = commUtils.trim(jrLayer.getFieldString("YD_STK_LYR_NO"));
			String szYD_EQP_ID 	= commUtils.trim(jrLayer.getFieldString("YD_EQP_ID"));
			String bizGp	 	= commUtils.trim(jrLayer.getFieldString("BIZ_GP"));
			
			
			String sRtnBedDan 	= "";  //TO위치	
			
			if ("".equals(StackColGp)) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YfConstant.RETN_CD_FAILURE;
			}
			
			String sAPP005_YN = yfComm.ACoilApplyYn("APP005","1","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");
			
			JDTORecord jrToLocBaseCheckRtn = JDTORecordFactory.getInstance().create();
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STL_NO"			, StockId);			
			jrTemp.setField("YD_SCH_CD"			, ydSchCd);			
			jrTemp.setField("YD_TO_LOC_GUIDE"	, StackColGp+StackBedGp);	
			jrTemp.setField("YD_STK_LYR_NO"		, StackLayerGp);	
			jrTemp.setField("YD_EQP_ID"			, szYD_EQP_ID);		//설비ID
			jrTemp.setField("BIZ_GP"  			, bizGp); //1:주작업 2:더미 3:사용자 4:차공정 5:진도 6:zone
			szLogMsg =  "재료["+StockId +" +스케쥴 코드 : "+ ydSchCd + "열"+ StackColGp + "베드"+StackBedGp+ "단" + StackLayerGp + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
			
			
			/** 
		     * 2. To위치 조회
		     * 
		     * */
			JDTORecordSet bedSet = commDao.select(jrTemp, getYfToLocMainSql, logId, methodNm, "동일한 적치가능한 베드 조회");
			
			
			
			if (bedSet.size() <= 0) 
			{
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YfConstant.RETN_CD_FAILURE;
			}
	
			
			JDTORecord bedRow	= JDTORecordFactory.getInstance().create();
			JDTORecord udpRow = JDTORecordFactory.getInstance().create();
			for (int i = 0; i < bedSet.size(); i++) 
			{

				bedRow  = bedSet.getRecord(i);
				if("PASS".equals(bedRow.getFieldString("LOC_ABLE_CHK")))
				{
					sRtnBedDan 	= commUtils.trim(bedRow.getFieldString("TAG_YD_STK_DAN_GP"));
					break;
				}
			}
			

			/** 
			 * 3. 처리 결과 LOG 
			 *    
			 * */			
			if ("Y".equals(sAPP005_YN) && bedSet.size() > 0) 
			{
				EJBConnector SchLog = new EJBConnector("default", "ACoilSchSeEJB", this);
    			SchLog.trx( "insSchLogs" , new Class[] {String.class , String.class, JDTORecordSet.class, String.class , String.class, String.class}
    			, new Object[] {logId, methodNm, bedSet,"",ydCrnSchId, ydSchCd });
			}
			else if (sRtnBedDan.length() < 10) 
			{
				EJBConnector SchLog = new EJBConnector("default", "ACoilSchSeEJB", this);
    			SchLog.trx( "insSchLogs" , new Class[] {String.class , String.class,  JDTORecordSet.class, String.class , String.class, String.class}
    			, new Object[] {logId, methodNm, bedSet,"대상코일선택실패: ",ydCrnSchId, ydSchCd });
    			
    			return YfConstant.RETN_CD_FAILURE;	
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YfConstant.RETN_CD_SUCCESS;
	} 
}	
	
