package com.inisteel.cim.ym.ilkwan.session;

import java.util.List;
import java.util.ArrayList;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.ilkwan.dao.YdCarSpecDao;
import com.inisteel.cim.ym.ilkwan.dao.YdMtlStatModHistDao;
import com.inisteel.cim.ym.ilkwan.dao.YdStkColDao;
import com.inisteel.cim.ym.ilkwan.dao.YdStockDao;
import com.inisteel.cim.ym.ilkwan.dao.ilkwanDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmDelegate;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

import jspeed.base.record.*;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.*;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="SlabRegEJB" jndi-name="JNDISlabReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SlabRegSBean extends BaseSessionBean { 


	private ilkwanDAO dao 			= null;	
	private Logger logger 			= null;
	private ymCommonDAO ymDao 		= null;
	private CraneSchDAO ydDao 		= null;
	private String szSessionName 	= "JNDISlabInfoReg";
	// [DEBUG] message flag
	private boolean bDebugFlag		=true;
	private YmDelegate ymDelegate 	=new YmDelegate();
	String[] rVal 					= new String[2];
	String sStocMv   				= "";
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 				= new Logger(config);
		dao					= new ilkwanDAO();
		ymDao		 		= new ymCommonDAO();
		ydDao 				= new CraneSchDAO();
	}
	
	/**
	 * 슬라브이송지시
	 * 
	 * 	1	전문코드			JMS_TC_CD							
	 *	2	상태코드			YD_EQP_WRK_SH				
	 *	3	이송지시일자		FRTOMOVE_WORD_DATE	YYYYMMDD
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procSlavFtmvOrd(JDTORecord inRecord)throws JDTOException  {
		
		boolean isSucess = false;
		try{
			
//			String sFrtomoveWordDate  = YmCommonUtil.paraRecChkNull(inRecord,"MATL_FTMV_DDLN_DD");
//			
//			YdStockDAO dao = new YdStockDAO();
//			
//			//공정정보 검색
//			List pmList 		= null;
//            {
//            	pmList = dao.getPmStockInfo_03(sFrtomoveWordDate);
//
//            	if(pmList.size() == 0){
//	            	logger.println(LogLevel.DEBUG, this, "공정TABLE 에서 저장품정보를 가져오지 못했습니다..");
////	            	throw new EJBServiceException("=인터페이스 작업요구=>공정TABLE 저장품정보 존재안함.");
//	            	return isSucess;
//	            }
//	        }
//            //STOCK TABLE UPDATE
//            int iSeq = 0;
//            JDTORecord stockV	= null;
//            String sStockId		= "";
//            {
//            	for(int inx = 0; inx < pmList.size() ; inx++){
//				 	stockV 		= (JDTORecord)pmList.get(inx);
//				 	sStockId   	= StringHelper.evl(stockV.getFieldString("STL_NO"),"");
//				 	
//				 	logger.println(LogLevel.DEBUG, this, "SLAB 이송지시 ===========");
//				 	logger.println(LogLevel.DEBUG, this, "SLAB 이송지시 저장품정보 	="+sStockId);
//				 	logger.println(LogLevel.DEBUG, this, "SLAB 이송지시 상태 		="+StringHelper.evl(stockV.getFieldString("FRTOMOVE_STAT_CD"),""));
//				 	logger.println(LogLevel.DEBUG, this, "SLAB 이송지시 현진도 		="+StringHelper.evl(stockV.getFieldString("CURR_PROG_CD"),""));
//				 	logger.println(LogLevel.DEBUG, this, "SLAB 이송지시 행선 		="+StringHelper.evl(stockV.getFieldString("SLAB_WO_RT_CD"),""));
//				 	logger.println(LogLevel.DEBUG, this, "SLAB 이송지시 착지개소	="+StringHelper.evl(stockV.getFieldString("ARR_WLOC_CD"),""));
//					
//					String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sStockId,"");
//					String sStocMv   	= sStockInfo[1];
//		            
//		            iSeq = dao.updateStockTransInfo_05(sStockId,
//		            								   sFrtomoveWordDate,
//		            								   "",
//		            								   sStocMv); 								
//													
//					logger.println(LogLevel.DEBUG, this, "SLAB 이송지시 결과 		="+iSeq);
//				}								
//	        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
	
	} // end of procSlavFtmvOrd()
	
	
	
	
	/**
	 * 슬라브충당실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procSlabMatchWr(JDTORecord inRecord)throws JDTOException  {

		boolean isSucess = false;
		try{
			/*
			YdStockDAO dao = new YdStockDAO();
			
			String sStockId  = YmCommonUtil.paraRecChkNull(inRecord,"SLAB_NO");
			
            int iSeq = 0;
            {
            	String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sStockId,"");
				String sStocMv   	= sStockInfo[1];
	            
	            iSeq = dao.updateStockTransInfo(sStockId,
	            								sStocMv); 								
												
				logger.println(LogLevel.DEBUG, this, "SLAB 충당실적 결과 		="+iSeq);
			}    
	        */
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;

	} // end of procSlabMatchWr()
	
	
	/**
	 * 외판슬라브출하지시대기 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procOutplSlabDistOrdWait(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procOutplSlabDistOrdWait";
		String szMsg = "";

		int intRtnVal = 0;

		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{

			rVal = YmCommonUtil.getSlabCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", szRcvTcCode);
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}

		}catch(Exception e){

			szMsg="[외판슬라브출하지시대기]Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		
		} // end of try-catch

		szMsg="외판슬라브출하지시대기  등록("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
		return true;
	} // end of procOutplSlabDistOrdWait()

	/**
	 * 외판슬라브목전
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procOutplSlabOrdtrn(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procOutplSlabOrdtrn";
		String szMsg = "";

		int intRtnVal = 0;
		JDTORecord recStockColumn 			= JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{
			rVal = YmCommonUtil.getSlabCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", szRcvTcCode);
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
		}catch(Exception e){
			szMsg="Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch

		return true;
	} // end of procOutplSlabOrdtrn()
	
	/**
	 * 외판슬라브보관지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */	
	public boolean procOutplSlabKeepOrd(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procOutplSlabKeepOrd";
		String szMsg = "";

		int intRtnVal = 0;
		
		JDTORecord recStockColumn 			= JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		YdMtlStatModHistDao ydMtlstatmodhistDao = new YdMtlStatModHistDao();
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{
			
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			intRtnVal = ydStockDao.updYdStock2(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
		}catch(Exception e){
			szMsg="Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch

		return true;
	} // end of procOutplSlabKeepOrd()
	
	/**
	 * 외판행선변경확정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procOutplRtChng(JDTORecord inRecord)throws JDTOException  {

		return true;
	} // end of procOutplRtChng()
	
	
	public int disyRec(JDTORecord inRecord)	{
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		int nRecCnt=-1;
		
		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
		nRecCnt =objTemp.length;
		String szRecKey="";
		String szValue="";
		String szMsg="";
		
		for(int i=0; i<nRecCnt; i++){
			szRecKey =objTemp[i].toString();
			szValue =inRecord.getFieldString(szRecKey);

			if(szValue==null)
				szValue="(null)";

			szMsg= "["+(i+1)+"]"
			     + "\t"+szRecKey
			     + "\t["+szValue+"]";
			System.out.println(szMsg);
				     
		} // end of for()
			
		return nRecCnt;
		
	} // end addFiller()
		
	
	/**
	 * 외판슬라브반품
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procOutplSlabRetngds(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		//저장품DAO
		YdStockDao ydStockDao 		= new YdStockDao();
		//재료상태변경이력DAO
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();
		
		String szMethodName = "procOutplSlabRetngds";
		String szMsg = "";
		
		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;

		}
		try{
			
			rVal = YmCommonUtil.getSlabCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", szRcvTcCode);
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
		}catch(Exception e){
			szMsg="Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		return true;

	} // end of procOutplSlabRetngds()
	
	/**
	 * 외판슬라브출하완료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procOutplSlabDistCmpl(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();

		ymCommonDAO dao = ymCommonDAO.getInstance();
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();

		String szMethodName = "procOutplSlabDistCmpl";
		String szMsg = "";
		String sQueryId ="";
		
		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;

		}
		try{
			
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getcarStartOrderInfo";				
			JDTORecord 	jtR = dao.getCommonInfo(sQueryId,new Object[]{inRecord.getFieldString("STL_NO")});			
			if(jtR == null){
				szMsg="[외판슬라브출하완료]["+recStockColumn.getFieldString("STL_NO")+"]에 대한 포인트 정보가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			}
			
			rVal = YmCommonUtil.getSlabCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", "DMYDR029");
			
			intRtnVal = ydStockDao.updYdStock3(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
			
			
			/*
		 	 *****************************************************************************
		 	 *차량  자동출발 모듈 CALL
		 	 */
			String sCarCardNo 	= StringHelper.evl(jtR.getFieldString("CARD_NO"),"");
 			String sPointNo 	= StringHelper.evl(jtR.getFieldString("STACK_COL_GP"),"");
 		
 			if(!sCarCardNo.equals("")){
// 			if(sCarCardNo.substring(0, 1).equals("T")||
// 			   sCarCardNo.substring(0, 1).equals("P")||
// 			   sCarCardNo.substring(0, 1).equals("E")
// 			){
// 				//ET 해송인 경우 출하로 부터 출발처리를 받는다.
// 			}else { 				
			EJBConnector ejbConn1 = new EJBConnector("default","JNDICTSStatusReg",this);
			Boolean isTemp = (Boolean)ejbConn1.trx("carStartOrder",
										new  Class[]{String.class,
													 String.class,
													 String.class},
										new Object[]{" ",						//한자리공백
													sCarCardNo,					//카드번호
													sPointNo});					//차량정지위치
// 			}
 			}
		 	/*
		 	 *****************************************************************************
		 	 */
					
		}catch(Exception e){
			szMsg = "[외판슬라브출하완료수신]Exception Error : "+ e.getMessage() ;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		}
		
		szMsg="외판슬라브출하완료수신 처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);

		return true;
	} // end of procOutplSlabDistCmpl
	
	
	
	/**
	 * 외판슬라브운송지시대기 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procOutplSlabTrnOrdWait(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		//재료상태변경이력DAO
		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();

		String szMethodName = "procOutplSlabTrnOrdWait";
		String szMsg = "";
		
		int intRtnVal = 0;
		int i = 0;
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{
			
			rVal = YmCommonUtil.getSlabCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", "DMYDR016");
			
			intRtnVal = ydStockDao.updYdStock3(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}

		}catch(Exception e){

			szMsg="[외판슬라브운송지시대기]Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		
		} // end of try-catch

		szMsg="외판슬라브운송지시대기  등록("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
		return true;

	} // end of procOutplSlabTrnOrdWait()
	
	/**
	 *      [A] 오퍼레이션명 : 외판슬라브 출하차량 도착 실적처리
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public boolean procOutplSlabDistCarArrWr(JDTORecord msgRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		YdStkColDao ydStkColDao        = new YdStkColDao();
		YdCarSpecDao ydCarSpecDao      = new YdCarSpecDao(); 
		JDTORecordSet rsStkCol         = null;
		JDTORecordSet rsCarSpec        = null;
		JDTORecord    recInTemp        = null;
		JDTORecord    recOutTemp       = null;
		JDTORecord    recCarSpec       = null;
		JDTORecord    recSndQue        = null;
		
	    String szMsg                   = "";
	    String szMethodName      	   = "procOutplSlabDistCarArrWr";	    
	    
	    // 운송지시일자
	    String szTRANS_ORD_DT          = "";

	    // 운송지시순번
	    String szTRANS_ORD_SEQNO       = "";

	    // 차량번호
	    String szCAR_NO				   = "";
	    
	    // 카드번호
	    String szCARD_NO               = "";
    	
	    // 발지개소코드
	    String szSPOS_WLOC_CD          = "";

	    // 발지야드포인트코드
	    String szSPOS_YD_PNT_CD        = "";    
    	
	    // 야드설비구분
	    String szYD_EQP_GP             = "";
    	
	    // 야드작업허용길이
	    String szYD_WRK_ALW_L		   = "";
    	
	    // 야드작업허용폭
	    String szYD_WRK_ALW_W          = "";
    	
	    // 야드작업허용Skid간격
	    String szYD_WRK_ALW_SKID_PITCH = "";
    	
	    // 야드작업허용매수
	    String szYD_WRK_ALW_SH         = "";
    	
	    // 야드작업허용중량
	    String szYD_WRK_ALW_WT         = "";
	    
	    // 적치열구분
	    String szYD_CARLD_LEV_LOC      = "";
	    
	    // TC CODE
	    String szRcvTcCode             = null;

	    // 쿼리
	    String szQuery                 = "";

	    // 리턴값 
	    int intRtnVal 		       	   = 0;
	    int intLevLocGp                = 0;
	    int nResult                    = 0;

	    szRcvTcCode = YmCommonUtil.getTcCode(msgRecord);
	    if(szRcvTcCode == null){
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false ;
		}
		
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		}
		
	    try{
	    	szTRANS_ORD_DT = YmCommonUtil.paraRecChkNull(msgRecord, "TRANS_ORD_DT");
			if(szTRANS_ORD_DT.equals("")) {
				szMsg = "[전문 이상] 운송지시일자가 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    	
	    	
			szTRANS_ORD_SEQNO = YmCommonUtil.paraRecChkNull(msgRecord, "TRANS_ORD_SEQNO");
			if(szTRANS_ORD_SEQNO.equals("")) {
				szMsg = "[전문 이상] 운송지시순번이 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    	

			szCAR_NO = YmCommonUtil.paraRecChkNull(msgRecord, "CAR_NO");
			if(szCAR_NO.equals("")) {
				szMsg = "[전문 이상] 차량번호가 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    	
			
			szCARD_NO = YmCommonUtil.paraRecChkNull(msgRecord, "CARD_NO");
			if(szCARD_NO.equals("")) {
				szMsg = "[전문 이상] 카드번호가 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    	
	
			szSPOS_WLOC_CD = YmCommonUtil.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")) {
				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    	
			
			szSPOS_YD_PNT_CD = YmCommonUtil.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")) {
				szMsg = "[전문 이상] 발지개소포인트 코드가 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    				

			szYD_EQP_GP = YmCommonUtil.paraRecChkNull(msgRecord, "YD_EQP_GP");
			if(szYD_EQP_GP.equals("")) {
				szMsg = "[전문 이상] 야드설비구분이 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    	
			
			szYD_WRK_ALW_L = YmCommonUtil.paraRecChkNull(msgRecord, "YD_WRK_ALW_L");
			if(szYD_WRK_ALW_L.equals("")) {
				szMsg = "[전문 이상] 야드작업허용길이가 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    	
	    	
			szYD_WRK_ALW_W = YmCommonUtil.paraRecChkNull(msgRecord, "YD_WRK_ALW_W");
			if(szYD_WRK_ALW_W.equals("")) {
				szMsg = "[전문 이상] 야드작업허용폭이 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    	

			szYD_WRK_ALW_SKID_PITCH = YmCommonUtil.paraRecChkNull(msgRecord, "YD_WRK_ALW_SKID_PITCH");
			if(szYD_WRK_ALW_SKID_PITCH.equals("")) {
				szMsg = "[전문 이상] 야드작업허용Skid간격이 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	    	
			
			szYD_WRK_ALW_SH = YmCommonUtil.paraRecChkNull(msgRecord, "YD_WRK_ALW_SH");
			if(szYD_WRK_ALW_SH.equals("")) {
				szMsg = "[전문 이상] 야드작업허용매수가 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	 			
			
			szYD_WRK_ALW_WT = YmCommonUtil.paraRecChkNull(msgRecord, "YD_WRK_ALW_WT");
			if(szYD_WRK_ALW_WT.equals("")) {
				szMsg = "[전문 이상] 야드작업허용중량이 없습니다.";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}	 		
			
	    	// 발지위치정보로 출발위치 Clear
	    	// 열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	// 업데이트값이 없다면 그냥 종료
	    	// 발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
	    	szQuery  = "SELECT *               ";
	    	szQuery += "FROM TB_YD_STKCOL      ";
	    	rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");

	    	//윤혁상처리 intLevLocGp = ymDBAssist.getData(szQuery, rsStkCol, null);
	    	if(intLevLocGp > 0) {
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord(0));
	    	
		    	// 적치열구분을 조회 (도착지)
		    	szYD_CARLD_LEV_LOC = YmCommonUtil.fillSpZr(YmCommonUtil.paraRecChkNull(recOutTemp, "STACK_COL_GP"), 6, 1);
		    	if(szYD_CARLD_LEV_LOC == "")
		    		return false;
		    	
		    	// 적치열 테이블에 활성상태 처리 
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("COL_GP"      , szYD_CARLD_LEV_LOC);
		    	recInTemp.setField("STACK_COL_ACTIVE_STAT", "E");
		    	//recInTemp.setField("YD_CAR_USE_GP"      , " ");
		    	//recInTemp.setField("TRN_EQP_CD"         , " ");
		    	//recInTemp.setField("CAR_CARD_NO"             , " ");
		    	
		    	intRtnVal = ydStkColDao.updYdStkcol(recInTemp, 0);
				if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="Data Not Found";
	    				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 2);
	    			}else if(intRtnVal == -1) {
	    				szMsg="Duplicate Data,";
	    				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="Parameter Error";
	    				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
	    			}else if(intRtnVal == -3){
	    				szMsg="Execution Failed";
	    				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
	    			}
	    			throw new DAOException("<updYdStkcol> " + szMsg);
	    		}

				// 적치베드 테이블에 활성상태 처리
				szQuery  = "UPDATE TB_YM_STACKER            ";
				szQuery += "SET STACK_BED_ACTIVE_STAT = 'L'  ";
				szQuery += ", STACK_BED_WT_MAX = '" + szYD_WRK_ALW_WT + "'";
				szQuery += "WHERE STACK_COL_GP = '" + szYD_CARLD_LEV_LOC + "'";
				
				//윤혁상처리 intRtnVal = ymDBAssist.setData(szQuery);
				if(intRtnVal <= 0) {
					szMsg="적치베드 활성화상태 등록 중 Error";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				}
				
				//적치단 활성화
				szQuery  = "UPDATE TB_YM_STACKLAYER            ";
				szQuery += "SET STACK_LAYER_ACTIVE_STAT = 'E'  ";
				//szQuery += "    ,YD_STK_LYR_MTL_STAT = 'E' ";
				szQuery += "    ,STOCK_ID = ' '";
				szQuery += "WHERE STACK_COL_GP = '" + szYD_CARLD_LEV_LOC + "'";
				
				//윤혁상처리 intRtnVal = ymDBAssist.setData(szQuery);
				if(intRtnVal <= 0) {
					szMsg = "적치단  활성화상태 등록 중 Error";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				}
				
				// 차량 정보                                                                                                        
				// *********** 조회 후건수가 없다면 INSERT 처리하는 로직 필요 ***********
				rsCarSpec = JDTORecordFactory.getInstance().createRecordSet("");
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("STACK_COL_ARRIVE_CAR_NO" , szCAR_NO);
		    	intRtnVal = ydCarSpecDao.getYdCarspec(recInTemp, rsCarSpec, 4);
				if(intRtnVal <= 0) {
					szMsg = "차량제원 검색 중 Error";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					return false;
				}
				
				rsCarSpec.first();
				recCarSpec = rsCarSpec.getRecord();
				
				// 전문 생성 및 전송
				recSndQue = JDTORecordFactory.getInstance().create();
		    	recSndQue.setField("MSG_ID"           	  , "YDYDJ235");
		    	recSndQue.setField("YD_EQP_ID"        	  , recCarSpec.getFieldString("YD_EQP_ID"));
		    	recSndQue.setField("YD_CARLD_STOP_LOC"	  , szYD_CARLD_LEV_LOC);
				recSndQue.setField("YD_CAR_USE_GP"    	  , recCarSpec.getFieldString("YD_CAR_USE_GP"));
				recSndQue.setField("CAR_NO"           	  , szCAR_NO);
				recSndQue.setField("CARD_NO"          	  , szCARD_NO);
				recSndQue.setField("YD_MTL_ITEM"      	  , "SG");
				recSndQue.setField("TRANS_ORD_DATE"   	  , szTRANS_ORD_DT);
				recSndQue.setField("TRANS_ORD_SEQNO"  	  , szTRANS_ORD_SEQNO);
				recSndQue.setField("YD_GP"            	  , szYD_CARLD_LEV_LOC.substring(0, 1));
				recSndQue.setField("YD_BAY_GP"        	  , szYD_CARLD_LEV_LOC.substring(1, 2)); 

				recSndQue.setField("SPOS_WLOC_CD"         , szSPOS_WLOC_CD);
				recSndQue.setField("SPOS_YD_PNT_CD"       , szSPOS_YD_PNT_CD);
				recSndQue.setField("YD_EQP_GP"            , szYD_EQP_GP);
				recSndQue.setField("YD_WRK_ALW_L"         , szYD_WRK_ALW_L);
				recSndQue.setField("YD_WRK_ALW_W"         , szYD_WRK_ALW_W);
				recSndQue.setField("YD_WRK_ALW_SKID_PITCH", szYD_WRK_ALW_SKID_PITCH);
				recSndQue.setField("YD_WRK_ALW_SH"        , szYD_WRK_ALW_SH);
				recSndQue.setField("YD_WRK_ALW_WT"        , szYD_WRK_ALW_WT);
				
				disyRec(recSndQue);
		    	ymDelegate.sendMsg(recSndQue);	    	

	    	}//end of if
		}catch(Exception e){
			szMsg="외판슬라브출하차량 도착 실적 처리 Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		}

		szMsg="외판슬라브출하차량 도착 실적 처리(" + szMethodName + ") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
		return true;
	}// end of procOutplSlabDistCarArrWr()
 
	/* 
	 * I/F ID : CTYDJ032
	 * B열연 압연지시 ~ 연주전달실적 수신 처리 FLOW
	 * 
	 * 1. B열연 압연지시 수신
	 * 		SLAB번호/예정SLAB번호 로 재료정보수신 [재료번호, 장입순번]
	 * 		재료번호가 있으면 해당재료에 재료번호,장입순번, 이동조건 UPDATE - 기타항목 추가 	
	 * 		재료번호가 없으면 해당재료에 재료번호,장입순번, 이동조건 INSERT - 기타항목 추가
	 * 
	 * 2. C연주 전단실적 수신
	 * 		실SLAB번호의 예정SLAB번호가 있으면 실SLAB번호로 KEY UPDATE
	 * 		실SLAB번호가 있으면 재료번호, 장입순번, 이동조건 UPDATE
	 * 		실SLAB번호가 없으면 재료번호, 장입순번, 이동조건 INSERT
	 * 
	 * 3.  A연주 전단실적 수신
	 * 		SlabInfoRegSBean/sySlabInfoInsert(POYM005) - 검토 
	 * 		일관제철 YD 저장품정보 등록처리도 함.
	 */
	/**
	 * B열연압연지시확정[기존 : PCYM002/WorkOrderInfoRegSBean]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procBHrMillOrdCmmt(JDTORecord inRecord)throws JDTOException  {
		logger.println(LogLevel.DEBUG, this, "B열연 압연작업지시 확정 전문 수신 ");
		
		Boolean isSuccess = new Boolean(false);
		
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            /**
             * valid check
             */
        	String sModGp	   = YmCommonUtil.paraRecChkNull(inRecord, "MOD_GP");
        	String sPtopPlntGp = YmCommonUtil.paraRecChkNull(inRecord, "PTOP_PLNT_GP");
        	String sChgWoFrPnt = YmCommonUtil.paraRecChkNull(inRecord, "CHG_WO_FR_PNT");
        	String sChgWoToPnt = YmCommonUtil.paraRecChkNull(inRecord, "CHG_WO_TO_PNT");
        	String endGp       = YmCommonUtil.paraRecChkNull(inRecord, "END_GP"); //종료구분
        	
        	/*
        	 * I : 생산통제 압연지시
        	 * O : 생산통제 압연지시 취소
        	 */
        	if("D".equals(sModGp)){
        		
        		String sSlabNo 		= YmCommonUtil.paraRecChkNull(inRecord, "SLAB_NO");
        		String sPlnSlabNo 	= YmCommonUtil.paraRecChkNull(inRecord, "PLAN_SLAB_NO");
        		
        		//저장품 TABLE CHARGE_LOT_NO 항목 CLEAR
				int iReq1 = ydDao.updateStockLotNoWithStockId(sSlabNo, "");			
				
				//저장품 TABLE CHARGE_LOT_NO 항목 CLEAR
				int iReq2 = ydDao.updateStockLotNoWithStockId(sPlnSlabNo, "");			
				
				if ("*".equals(endGp)) {
					 //야드 L2 전송 장입순번 Clear CALL
				     boolean isTrue = callL2LotEndInfo_Slab(sSlabNo);	
				     
	        		logger.println(LogLevel.DEBUG, this, "B열연 압연작업지시 취소전문 => 예정슬라브번호 =>"+sPlnSlabNo+"=>"+iReq1+"/슬라브번호 =>"+sSlabNo+"=>"+iReq2);
				}
				
        		return true;
        	}
        	
			
            if(!"HB".equals(sPtopPlntGp)) {
            	logger.println(LogLevel.DEBUG, this, "B열연 압연작업지시 확정 전문 수신에러 => 조업공장구분 항목 에러 =>"+sPtopPlntGp);
                return false;
            }else if("".equals(sChgWoFrPnt)) {
            	logger.println(LogLevel.DEBUG, this, "B열연 압연작업지시 확정 전문 수신에러 => 장입지시 FROM POINT 에러  ");
                return false;
            }else if("".equals(sChgWoToPnt)) {
            	logger.println(LogLevel.DEBUG, this, "B열연 압연작업지시 확정 전문 수신에러 => 장입지시 TO POINT 에러  ");
                return false;
            }
            
            
             
				logger.println(LogLevel.DEBUG,this,"Start-syZoneInReservationInsert()");
					
				EJBConnector ejbConn = new EJBConnector("default", "JNDISlabReg", this);
				isSuccess = (Boolean)ejbConn.trx("procBHrMillOrdCmmtSub_01", 
													new Class[]{JDTORecord.class}, 
													new Object[]{inRecord});
					
				isSuccess = (Boolean)ejbConn.trx("procBHrMillOrdCmmtSub_02", 
													new Class[]{JDTORecord.class}, 
													new Object[]{inRecord});
            
            
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSuccess.booleanValue();
	} //end of procBHrMillOrdCmmt()
	
	/**
	 * B열연압연지시확정[기존 : PCYM002/WorkOrderInfoRegSBean]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public boolean procBHrMillOrdCmmtSub_01(JDTORecord inRecord)throws JDTOException  {
		logger.println(LogLevel.DEBUG, this, "B열연 압연작업지시 확정 전문 수신 ");
		
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            /**
             * valid check
             */
        	String sModGp	   = YmCommonUtil.paraRecChkNull(inRecord, "MOD_GP");
        	String sPtopPlntGp = YmCommonUtil.paraRecChkNull(inRecord, "PTOP_PLNT_GP");
        	String sChgWoFrPnt = YmCommonUtil.paraRecChkNull(inRecord, "CHG_WO_FR_PNT");
        	String sChgWoToPnt = YmCommonUtil.paraRecChkNull(inRecord, "CHG_WO_TO_PNT");
        	
        	{
        		/**
                 * 1.1 L2 삭제 장입정보 송신
                 */
        		List del_list 	= ymDao.readZoneInStocks_Del("");
        		
        		if(del_list.size()>0)
        		{
					JDTORecord dtoDel = null;
					for(int ix = 0; ix < 1; ix++) 
					{ 
						dtoDel = (JDTORecord)del_list.get(ix);
						sendSlabInfoD(dtoDel,
									 "C");                     
					}
        		}
				/**
	             * 1.2 저장품의 장입순번을 CLEAR 한다.
	             */
				ymDao.modifyZoneInNo_01();
        	}
        	
            {
            	/**
                 * 2. 작업지시 정보 SELECT 
                 */
            	List wList = dao.getBSlabWorkList(	sPtopPlntGp,
							            			sChgWoFrPnt,
							            			sChgWoToPnt);
            	
                if(wList.size() == 0 ) {
                    logger.println(LogLevel.DEBUG, this, "B열연 압연작업지시 확정 전문 수신에러 => 장입순번이 존재하지 않습니다  ");
                    return false;
                }        
            	
                /**
                 * #3CTC,#4CTC,W/B에 올려진 SLAB는 장입순번을 변경하지 않는다.
                 */
                List wListTmp = ymDao.readLoadWBCTC();
                
            	JDTORecord stockV = null;
            	/*
				 * 재료번호(Slab번호 Or 예정Slab번호)                   
				 * 야드장입순위
				 */
            	String sStockId		= "";
            	String sChargeLotNo	= "";
            	
            	for (int inx = 0; inx < wList.size(); inx++) {

					stockV = (JDTORecord) wList.get(inx);

					if (stockV != null) {
						
						sStockId 		= StringHelper.evl(stockV.getFieldString("STL_NO"),"");
						sChargeLotNo 	= StringHelper.evl(stockV.getFieldString("YD_CHG_NO"), "");
						
						if(notWBLoading(wListTmp, sStockId)) {
							
							//저장품 테이블에 해당 저장품이 있는지 체크
							JDTORecord stockJr = ydDao.getStockInfoWcrGp(sStockId);
							if(stockJr == null){
								logger.println(LogLevel.DEBUG,this, "저장품 테이블에 저장품정보 존재않함 INSERT="+sStockId);
								createSlabInfo(	sChargeLotNo,
												YmCommonConst.NEW_STOCK_MOVE_TERM_FS,
					                            sStockId);
							}else{
								logger.println(LogLevel.DEBUG,this, "저장품 테이블에 저장품정보 존재함    UPDATE="+sStockId);
								ymDao.modifyZoneInOfStock(	sChargeLotNo,
															YmCommonConst.NEW_STOCK_MOVE_TERM_FS,
								                            sStockId);
							}
						}
					}
				}
            }
         
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
	} //end of procBHrMillOrdCmmtSub_01()
	
	/**
	 * B열연압연지시확정[기존 : PCYM002/WorkOrderInfoRegSBean]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public boolean procBHrMillOrdCmmtSub_02(JDTORecord inRecord)throws JDTOException  {
		logger.println(LogLevel.DEBUG, this, "B열연 압연작업지시 확정 전문 수신 ");
		
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            /**
             * valid check
             */
        	String sModGp	   = YmCommonUtil.paraRecChkNull(inRecord, "MOD_GP");
        	String sPtopPlntGp = YmCommonUtil.paraRecChkNull(inRecord, "PTOP_PLNT_GP");
        	String sChgWoFrPnt = YmCommonUtil.paraRecChkNull(inRecord, "CHG_WO_FR_PNT");
        	String sChgWoToPnt = YmCommonUtil.paraRecChkNull(inRecord, "CHG_WO_TO_PNT");
        	
        	{
            	/**
                 * 2. 작업지시 정보 SELECT 
                 */
            	List wList = dao.getBSlabWorkList(	sPtopPlntGp,
							            			sChgWoFrPnt,
							            			sChgWoToPnt);
            	
                if(wList.size() == 0 ) {
                    logger.println(LogLevel.DEBUG, this, "B열연 압연작업지시 확정 전문 수신에러 => 장입순번이 존재하지 않습니다  ");
                    return false;
                }        
            	
                /**
                 * #3CTC,#4CTC,W/B에 올려진 SLAB는 장입순번을 변경하지 않는다.
                 */
                List wListTmp = ymDao.readLoadWBCTC();
                
            	JDTORecord stockV = null;
            	/*
				 * 재료번호(Slab번호 Or 예정Slab번호)                   
				 * 야드장입순위
				 */
            	String sStockId		= "";
            	String sChargeLotNo	= "";
            	
            	for (int inx = 0; inx < wList.size(); inx++) {

					stockV = (JDTORecord) wList.get(inx);

					if (stockV != null) {
						
						sStockId 		= StringHelper.evl(stockV.getFieldString("STL_NO"),"");
						sChargeLotNo 	= StringHelper.evl(stockV.getFieldString("YD_CHG_NO"), "");
						
						if(notWBLoading(wListTmp, sStockId)) {
				            sendSlabInfo(stockV,
		                    			 YmCommonConst.FORM_I);
						}
					}
				}
            }
         
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
	} //end of procBHrMillOrdCmmtSub_02()
	
	private boolean callL2LotEndInfo_Slab(String sStockId){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			ymCommonDAO ymDao 	= ymCommonDAO.getInstance();
			JDTORecord slabInfo = ymDao.readZoneInStocks_Lot(sStockId);
			String sSendMsg 	= YmCommonUtil.getSlabMsgInfo(slabInfo,YmCommonConst.FORM_R);
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("sendCM1BP02", new Class[]{String.class},
													     	new Object[]{sSendMsg});
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
    /**
     * 저장품 테이블에 INSERT 한다.
     * @param 	ydStockDAO		: DAO
     * @param 	slabNo			: 슬라브번호
     * @param 	stockStat		: 저장품상태
     * @return 	1(성공),0(실패)
     */
    private int createSlabInfo(	String sChargeLotNo, 
    							String sStocMv,
    							String slabNo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
        List insertData = new ArrayList();
        insertData.add(slabNo);					//저장품 ID
        insertData.add("");						//작업예약 ID
        insertData.add(YmCommonConst.ITEM_SM); 	//저장품 품목
        insertData.add("");						//저장품 상태        
        insertData.add("");						//저장품 냉각 상태
        insertData.add("");						//저장품 냉각 시작 일시
        insertData.add("");						//저장품 냉각 시작 온도
        insertData.add("");						//산적 LOT 번호
        insertData.add(sStocMv);				//저장품 이동 조건
        insertData.add("");						//이송 설비 구분
        insertData.add("");						//이송 설비 BED 구분
        insertData.add("");						//이송 설비 단 구분
        insertData.add(sChargeLotNo);			//장입 LOT 번호
        insertData.add("");						//이송 지시 번호
        insertData.add("");						//운송 작업지시 번호
        insertData.add(""); 					//SCARFING 보급 유무
        insertData.add("");						//차량 CARD 번호
        insertData.add("");						//정정 보급 순서
        insertData.add("");						//CTS 중계 구분
        insertData.add("");						//CTS 중계 동
        insertData.add("");						//CTS 중계 SADDLE
        insertData.add("");						//하차 YARD
        insertData.add("");						//하차 동
        insertData.add("CTYDJ032");				//등록자
        insertData.add("");						//수정자
        insertData.add("");						//수정 일시
        return new YdStockDAO().createData(insertData);		        
    }    
    /**
     * 
     */
    private boolean notWBLoading(List list, String stockId) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        boolean notStock = true;
        for(int i = 0; i < list.size(); i++) {
            if(stockId.equals(getField((JDTORecord)list.get(i), "STOCK_ID"))) {
                notStock = false;
                break;
            }
        }
        return notStock;
    }    
    
    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getField(JDTORecord data, String name) {
        return StringHelper.evl(data.getFieldString(name), "").trim();
    }
    
    /**
     * 장입확정시 L-2로 슬라브정보를 송신한다.
     * @param dto	슬라브정보
     */
    private void sendSlabInfo(JDTORecord slabInfo,String sFormGp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	sendQueue(YmCommonConst.TC_CM1BP02, YmCommonUtil.getSlabMsgInfo(slabInfo,sFormGp));
    }
    
    /**
     * 삭제 장입순번.
     * @param dto	슬라브정보
     */
    private void sendSlabInfoD(JDTORecord slabInfo,String sFormGp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	sendQueue(YmCommonConst.TC_CM1BP02, YmCommonUtil.getSlabMsgInfoD(slabInfo,sFormGp));
    }

    /**
     * 송신 EJB를 이용하여 송신데이터를 송신한다.
     * @param methodName	TC명
     * @param sendMsg		송신데이터
     * @throws Exception
     */
    private void sendQueue(String methodName, String sendMsg) {
        EJBConnector ejbConn = null;
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return;
    		}
    		
            ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
            ejbConn.trx("send"+ methodName, new Class[]{ String.class }, new Object[]{ sendMsg });
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
    }
    
    

	/**
	 * 연주전단실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCcFsWr(JDTORecord inRecord)throws JDTOException  {
		logger.println(LogLevel.DEBUG, this, "C연주 전단실적 전문 수신 ");
		
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            /**
             * valid check
             */
        	String sStlNo		= YmCommonUtil.paraRecChkNull(inRecord, "STL_NO");
        	String sStlAppearGp = YmCommonUtil.paraRecChkNull(inRecord, "STL_APPEAR_GP");
        	String sOrdYeojaeGp = YmCommonUtil.paraRecChkNull(inRecord, "ORD_YEOJAE_GP");
        	String sStlProgCd 	= YmCommonUtil.paraRecChkNull(inRecord, "STL_PROG_CD");
        	String sSlabWoRtCd 	= YmCommonUtil.paraRecChkNull(inRecord, "SLAB_WO_RT_CD");
        	String sHcrGp 		= YmCommonUtil.paraRecChkNull(inRecord, "HCR_GP");
        	String sScarfingYn 	= YmCommonUtil.paraRecChkNull(inRecord, "SCARFING_YN");
        	String sPtopPlntGp 	= YmCommonUtil.paraRecChkNull(inRecord, "PTOP_PLNT_GP");
			
        	if("".equals(sStlNo)) {
            	logger.println(LogLevel.DEBUG, this, "C연주 전단실적전문 수신에러 => 재료번호 =>"+sStlNo);
            	return false;
            }
        	/*
        	 else if("".equals(sStlAppearGp)) {
            	logger.println(LogLevel.DEBUG, this, "C연주 전단실적전문 수신에러 => 재료외형 =>"+sStlAppearGp);
            }else if("".equals(sOrdYeojaeGp)) {
            	logger.println(LogLevel.DEBUG, this, "C연주 전단실적전문 수신에러 => 주문여재구분 =>"+sOrdYeojaeGp);
            }else if("".equals(sStlProgCd)) {
            	logger.println(LogLevel.DEBUG, this, "C연주 전단실적전문 수신에러 => 재료진도 =>"+sStlProgCd);
            }else if("".equals(sSlabWoRtCd)) {
            	logger.println(LogLevel.DEBUG, this, "C연주 전단실적전문 수신에러 => Slab지시행선 =>"+sSlabWoRtCd);
            }else if("".equals(sHcrGp)) {
            	logger.println(LogLevel.DEBUG, this, "C연주 전단실적전문 수신에러 => HCR구분 =>"+sHcrGp);
            }else if("".equals(sScarfingYn)) {
            	logger.println(LogLevel.DEBUG, this, "C연주 전단실적전문 수신에러 => 스카핑유무 =>"+sScarfingYn);
            }else if("".equals(sPtopPlntGp)) {
            	logger.println(LogLevel.DEBUG, this, "C연주 전단실적전문 수신에러 => 조업공장구분 =>"+sPtopPlntGp);
            }
            */
			JDTORecord stockV = ymDao.readSlabMatirialInfo(sStlNo);
        	String sPlanSlabNo = "";
        	if(stockV != null){
        		sPlanSlabNo = StringHelper.evl(stockV.getFieldString("PLAN_SLAB_NO"),"");
        	}
        	
        	/*
        	 * 1. 저장품 테이블에 예정Slab번호로 저장품이 있는지 체크
        	 */
			JDTORecord stockJr1 = ydDao.getStockInfoWcrGp(sPlanSlabNo);
			if(stockJr1 != null){
				/*
				 * 2.1 예정Slab번호 존재 : 실재료번호로 UPDATE
				 */
				dao.updateStockTransInfo(	sPlanSlabNo,
											sStlNo,
											YmCommonUtil.getSlabCurrProgCd(sStlNo,"")[1]);
				//L2전문송신 
				sendSlabInfo(ymDao.readSlabInfo(sStlNo),
							 YmCommonConst.FORM_I);
			}else{
				/*
				 * 2.2 예정Slab번호 미존재 : 
				 */
				/*
	        	 * 3. 저장품 테이블에 실주편번호로 저장품이 있는지 체크
	        	 */
				JDTORecord stockJr2 = ydDao.getStockInfoWcrGp(sStlNo);
				if(stockJr2 != null){
					/*
					 * 2.1 실주편번호 존재 : 실재료번호로 UPDATE
					 */
					ydDao.updateStockTransInfo(sStlNo,
											   YmCommonUtil.getSlabCurrProgCd(sStlNo,"")[1]);
				}else{
					/*
					 * 2.1 실주편번호 미존재 : 실재료번호로 INSERT
					 */
					createSlabInfo(	"",//장입순번 
									YmCommonUtil.getSlabCurrProgCd(sStlNo,"")[1],
									sStlNo);
					//L2전문송신 
					sendSlabInfo(ymDao.readSlabInfo(sStlNo),
								 YmCommonConst.FORM_I);
				}
			}
        	
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
	} //end of procCcFsWr()
	
	
	
	/**
	 * 외판슬라브운송상차지시등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procSlabGdsCarLdOrd(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procSlabGdsCarLdOrd";
		String szMsg = "";
		String szSTL_NO ="";
		YdStockDao ydStockDao = new YdStockDao();

		int intRtnVal = 0;
		int	i =0;
		JDTORecord recStockColumn				= JDTORecordFactory.getInstance().create();

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{
				//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
				recStockColumn 	= JDTORecordFactory.getInstance().create();
				recStockColumn.setField("STL_APPEAR_GP", 		YmCommonUtil.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
				recStockColumn.setField("TRANS_ORD_DT", 		YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
				recStockColumn.setField("TRANS_ORD_SEQNO", 		YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
				recStockColumn.setField("CAR_NO", 				YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO"));
				recStockColumn.setField("CARD_NO", 				YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));
				recStockColumn.setField("MODIFIER", 				"DMYDR022");
				
			for(i = 1 ; i<=20; i++){
				recStockColumn.setField("STOCK_ID", 				YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i));
				
				
				//STL_NO가 없다면 loop종료
				szSTL_NO = recStockColumn.getFieldString("STOCK_ID");
				if(szSTL_NO.equals("")){
					break;
				}else {
					recStockColumn.setField("STOCK_ID", 				YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i));
				}
	
				//재료상태변경이력등록***********************************************************************************
				rVal = YmCommonUtil.getCoilCurrProgCd(recStockColumn.getFieldString("STOCK_ID"),szRcvTcCode);
				sStocMv = rVal[1];
				recStockColumn.setField("STOCK_MOVE_TERM", StringHelper.evl(sStocMv,"LG"));
				//****************************************************************************************************
	
									
				//저장품갱신******************************************************************************************** 
				intRtnVal = ydStockDao.updYdStock8(recStockColumn, 0);
				if(intRtnVal >0){
					szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

				}else if(intRtnVal == 0){
					szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					return false;
				}
				//****************************************************************************************************
	
	
			} //end of for *******************************************************************************************

		}catch(Exception e){

			szMsg="[외판슬라브상차지시등록] Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		szMsg="[외판슬라브상차지시등록]수신처리 ("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
		return true;
		
	} // end of procSlabGdsCarLdOrd()
}

