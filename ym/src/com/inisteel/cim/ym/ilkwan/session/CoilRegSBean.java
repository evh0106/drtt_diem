package com.inisteel.cim.ym.ilkwan.session;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.ilkwan.dao.YdStockDao;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CoilRegEJB" jndi-name="JNDICoilReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilRegSBean extends BaseSessionBean { 

	private Logger logger 	= null;
	private String szSessionName = "JNDICoilInfoReg";
	String[] rVal = new String[2];
	String sStocMv   = "";
	
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 				= new Logger(config);
	}
	
    
	/**
	 * 코일충당실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilMatchWr(JDTORecord inRecord)throws JDTOException  {

		boolean isSucess = false;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}

			
			YdStockDAO dao = new YdStockDAO();
			
			String sStockId  = YmCommonUtil.paraRecChkNull(inRecord,"COIL_NO");
			
            int iSeq = 0;
            {
            	String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sStockId,"");
				String sStocMv   	= sStockInfo[1];
	            
	            iSeq = dao.updateStockTransInfo(sStockId,
	            								sStocMv); 								
												
				logger.println(LogLevel.DEBUG, this, "COIL 충당실적 결과 		="+iSeq);
			}    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
	} // end of procCoilMatchWr()
	
	
	
	/**
	 * 코일소재이송지시(PTYDJ002)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilMatlFtmvOrd(JDTORecord inRecord)throws JDTOException  {

		boolean isSucess = false;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			String sFrtomoveWordDate  = YmCommonUtil.paraRecChkNull(inRecord,"FRTOMOVE_WORD_DATE");
			
			YdStockDAO dao = new YdStockDAO();
			
			//공정정보 검색
			List pmList 		= null;
            {
            	pmList = dao.getPmStockInfo_04(sFrtomoveWordDate);

            	if(pmList.size() == 0){
	            	logger.println(LogLevel.DEBUG, this, "공정TABLE 에서 저장품정보를 가져오지 못했습니다..");
	            	throw new EJBServiceException("=인터페이스 작업요구=>공정TABLE 저장품정보 존재안함.");
	            }
	        }
            //STOCK TABLE UPDATE
            int iSeq = 0;
            JDTORecord stockV	= null;
            String sStockId		= "";
            {
            	for(int inx = 0; inx < pmList.size() ; inx++){
				 	stockV 		= (JDTORecord)pmList.get(inx);
				 	sStockId   	= StringHelper.evl(stockV.getFieldString("STL_NO"),"");
				 	
				 	logger.println(LogLevel.DEBUG, this, "COIL 이송지시 ===========");
				 	logger.println(LogLevel.DEBUG, this, "COIL 이송지시 저장품정보 	="+sStockId);
				 	logger.println(LogLevel.DEBUG, this, "COIL 이송지시 상태 		="+StringHelper.evl(stockV.getFieldString("FRTOMOVE_STAT_CD"),""));
				 	logger.println(LogLevel.DEBUG, this, "COIL 이송지시 현진도 		="+StringHelper.evl(stockV.getFieldString("CURR_PROG_CD"),""));
				 	logger.println(LogLevel.DEBUG, this, "COIL 이송지시 행선 		="+StringHelper.evl(stockV.getFieldString("PLNT_PROC_CD"),""));
				 	logger.println(LogLevel.DEBUG, this, "COIL 이송지시 착지개소	="+StringHelper.evl(stockV.getFieldString("ARR_WLOC_CD"),""));
					
					String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sStockId,"");
					String sStocMv   	= sStockInfo[1];
		            
		            iSeq = dao.updateStockTransInfo_05(sStockId,
		            								   sFrtomoveWordDate,
		            								   "",
		            								   sStocMv); 								
													
					logger.println(LogLevel.DEBUG, this, "COIL 이송지시 결과 		="+iSeq);
				}								
	        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
	} // end of procCoilMatlFtmvOrd()
	
	/**
	 * 코일소재임가공이송지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilMatlRentprocFtmvOrd(JDTORecord inRecord)throws JDTOException  {

		boolean isSucess = false;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			String sFrtomoveWordDate  = YmCommonUtil.paraRecChkNull(inRecord,"FRTOMOVE_WORD_DATE");
			
			YdStockDAO dao = new YdStockDAO();
			
			//공정정보 검색
			List pmList 		= null;
            {
            	pmList = dao.getPmStockInfo_05(sFrtomoveWordDate);

            	if(pmList.size() == 0){
	            	logger.println(LogLevel.DEBUG, this, "공정TABLE 에서 저장품정보를 가져오지 못했습니다..");
	            	throw new EJBServiceException("=인터페이스 작업요구=>공정TABLE 저장품정보 존재안함.");
	            }
	        }
            //STOCK TABLE UPDATE
            int iSeq = 0;
            JDTORecord stockV	= null;
            String sStockId		= "";
            {
            	for(int inx = 0; inx < pmList.size() ; inx++){
				 	stockV 		= (JDTORecord)pmList.get(inx);
				 	sStockId   	= StringHelper.evl(stockV.getFieldString("STL_NO"),"");
				 	
				 	logger.println(LogLevel.DEBUG, this, "임가공 이송지시 ===========");
				 	logger.println(LogLevel.DEBUG, this, "임가공 이송지시 저장품정보 	="+sStockId);
				 	logger.println(LogLevel.DEBUG, this, "임가공 이송지시 상태 		="+StringHelper.evl(stockV.getFieldString("FRTOMOVE_STAT_CD"),""));
				 	logger.println(LogLevel.DEBUG, this, "임가공 이송지시 임가공사	="+StringHelper.evl(stockV.getFieldString("RENTPROC_COMCD"),""));
				 	logger.println(LogLevel.DEBUG, this, "임가공 이송지시 행선 		="+StringHelper.evl(stockV.getFieldString("PLNT_PROC_CD"),""));
				 	
					String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sStockId,"");
					String sStocMv   	= sStockInfo[1];
		            
		            iSeq = dao.updateStockTransInfo_05(sStockId,
		            								   sFrtomoveWordDate,
		            								   "",
		            								   sStocMv); 								
													
					logger.println(LogLevel.DEBUG, this, "임가공 이송지시 결과 		="+iSeq);
				}								
	        }    
	        
			isSucess = true;
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return isSucess;
	} // end of procCoilMatlRentprocFtmvOrd()
	
	/**
	 * 코일제품보류확정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsHoldCommt(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procCoilGdsHoldCommt";
		String szMsg = "";

		int intRtnVal = 0;

		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;

		}
		try{
			rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", szRcvTcCode);
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 데이트가가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
		}catch(Exception e){
			szMsg = "Exception Error : "+ e.getMessage() ;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		}
		
		return true;
	} // end of procCoilGdsHoldCommt()
	
	/**
	 * 코일제품출하지시대기 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsDistOrdWait(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procCoilGdsDistOrdWait";
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


			rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", szRcvTcCode);
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
		}catch(Exception e){

			szMsg="Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		return true;

	} // end of procCoilGdsDistOrdWait()
	
	/**
	 * 코일제품반납대기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsRetnWait(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procCoilGdsRetnWait";
		String szMsg = "";

		int intRtnVal = 0;

		JDTORecord recStockColumn = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;

		}
		try{

			rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", szRcvTcCode);
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
		}catch(Exception e){
			szMsg = "Exception Error : "+ e.getMessage() ;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		}
		
		return true;
	} // end of procCoilGdsRetnWait()
	
	
	
	
	/**
	 * 코일제품고간이송지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsWhFtmvOrd(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procCoilGdsWhFtmvOrd";
		String szMsg = "";

		String szSTL_NO = null;

		int intRtnVal = 0;
		int i = 0;

		JDTORecord recStockColumn 	= JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();

		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;

		}
		try{
			
//			수신된 전문의 STL_NO의 수 만큼 Loop
			for(i = 1 ; i<=20; i++){
				recStockColumn 	= JDTORecordFactory.getInstance().create();
				String chk =YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i);
				
				if(chk.equals("")){
					return true;
				}
					
				
				rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"+i),szRcvTcCode);
				sStocMv = rVal[1];
				recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i));
				recStockColumn.setField("TRANS_ORD_DT", YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
				recStockColumn.setField("TRANS_ORD_SEQNO", YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
				recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
				recStockColumn.setField("MODIFIER", szRcvTcCode);
				intRtnVal = ydStockDao.updYdStock5(recStockColumn, 0);
			}
				
		}catch(Exception e){
			szMsg = "Exception Error : "+ e.getMessage() ;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		}
		
		return true;
	} // end of procCoilGdsWhFtmvOrd
	
	
	/**
	 * 코일제품운송지시(DMYDR020) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsTrnOrd(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		//재료상태변경이력 DAO
		JDTORecord recStockColumn 	= JDTORecordFactory.getInstance().create();
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		JDTORecord recInTemp		= JDTORecordFactory.getInstance().create();
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult1 	= null;
		String szMethodName = "procCoilGdsTrnOrd";
		String szMsg = "";
		String  szYD_CAR_SCH_ID = "";
		String szWLOC_CD			= "";
		String szYD_STK_COL_GP		= "";
		String szYD_GP			 	= "";
		String szCARLD_PNT_CD 		= "";
		String szHANDLING_CNT 		= "";
		String szYD_PNT_CD 			= "";
		int i = 0;
		
		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);
		YdDelegate      ydDelegate      = new YdDelegate();
		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{
			   for(i = 1 ; i<=20; i++){
				   
					String chk =YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i);
					
					if(chk.equals("")){
						break;
					}
					
					rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"+i),szRcvTcCode);
					sStocMv = rVal[1];
					recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i));
					recStockColumn.setField("SHEAR_SUPPLY_SEQ", YmCommonUtil.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i)); //야드 차상위치코드
					recStockColumn.setField("TRANS_ORD_DT", YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
					recStockColumn.setField("TRANS_ORD_SEQNO", YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
					recStockColumn.setField("CAR_KIND", YmCommonUtil.paraRecChkNull(inRecord,"CAR_KIND")); //차량구분
					recStockColumn.setField("CAR_CARD_NO", YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));
					recStockColumn.setField("CAR_NO2", YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO"));
					
					recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
					recStockColumn.setField("MODIFIER", szRcvTcCode);
					intRtnVal = ydStockDao.updYdStock5(recStockColumn, 0);
				}
			   
			   
			   String sWORK_GP = YmCommonUtil.paraRecChkNull(inRecord,"WORK_GP");
			   String sCAR_KIND =YmCommonUtil.paraRecChkNull(inRecord,"CAR_KIND"); //장비구분: Trailer-T , TT Trailer -TT
			   szMsg= "["+szMethodName+"] 작업구분:::::"+sWORK_GP;
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
			   
				//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
				if(sWORK_GP.equals("9")){
				//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("TRANS_ORD_DT"		, YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
				recPara.setField("TRANS_ORD_SEQNO"	, YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
				recPara.setField("CARD_NO"			, YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO") );

				//중복 check
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
				if(intRtnVal > 0){
					szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szYD_CAR_SCH_ID    = StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
				 
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("DEL_YN", "Y");
					recInTemp.setField("MODIFIER", 	"DMYDR020");
					
					intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
					if( intRtnVal <= 0 ){
						szMsg="[" + szMethodName + "] 차량스케줄 삭제 시 오류발생[반환값 : " + intRtnVal + "]";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);					 
		    		}
				}
				////////////////////////////////////////////////////////////////////////////////////////
				
				
				//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
				
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CARPNT_CD"		, YmCommonUtil.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
				
				//포인트코드 -> 개소코드와 저장위치 가져오기
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
				if(intRtnVal > 0){
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다..]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szWLOC_CD    		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
					szYD_STK_COL_GP    	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");		
					szYD_PNT_CD	    	= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");
					
					/*
					 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
					 */
					szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         szRcvTcCode);
					recInTemp.setField("YD_EQP_WRK_STAT",  "U");									//야드설비작업상태
					recInTemp.setField("YD_EQP_ID",        YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
					recInTemp.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
					recInTemp.setField("SPOS_WLOC_CD",     szWLOC_CD);								//발지개소코드
					recInTemp.setField("CAR_NO",           YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO"));								//차량번호
					recInTemp.setField("CARD_NO",          YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));								//카드번호
					recInTemp.setField("YD_CARLD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
					recInTemp.setField("TRANS_ORD_DATE",   YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));						//운송지시순번
			    	recInTemp.setField("YD_CARLD_STOP_LOC",szYD_STK_COL_GP);						//차량상차정지위치
			    	recInTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);		//입동지시순번 - 기본값으로 설정(9)
					recInTemp.setField("YD_CAR_PROG_STAT", "1");									//상차출발상태
					recInTemp.setField("YD_CAR_WRK_GP", 	sWORK_GP);
					recInTemp.setField("YD_PNT_CD1", 	szYD_PNT_CD);
					
					if(sCAR_KIND.equals("TT")){
						recInTemp.setField("CAR_KIND",          "TT");									//차량종류
					}else{
						recInTemp.setField("CAR_KIND",          "TR");									//차량종류
					}
		    		
		    		//차량스케줄 등록
			    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
		    		if( intRtnVal <= 0 ){
						szMsg="[" + szMethodName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);	 
		    		}
		    		
		    		if(sCAR_KIND.equals("TT")){
		    			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn3 = new EJBConnector("default","JNDITsInfoReg",this);
						ejbConn3.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"C",YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO"),YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"),szYD_STK_COL_GP,"","","R"});
		    		}
				}else {
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 안합니다..]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				}
	    		//////////////////////////////////////////////////////////////////////////////////////////
				
				
				//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
				if(sCAR_KIND.equals("T") || sCAR_KIND.equals("TR")){
					/*
					 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 */
					szMsg="[" + szMethodName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - AB차량입동지시요구 모듈을 호출 시작";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					
					recInTemp = JDTORecordFactory.getInstance().create();			 
					recInTemp.setField("YD_CARPNT_CD"			,YmCommonUtil.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
					recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
					
					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
					ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					

					szMsg="[" + szMethodName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - AB차량입동지시요구 모듈을 호출 성공";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				}
				//////////////////////////////////////////////////////////////////////////////////////////
				
				
				//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
				//육송출하고도화
				ymCommonDAO dao = ymCommonDAO.getInstance();
				 List chkList = null;
				String QueryId 	= "com.inisteel.cim.yd.dao.chklist";
				chkList = dao.getCommonList(QueryId, new Object[]{});

			    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
		    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");	    	
		    	YmCommonUtil.putLog(szSessionName, szMethodName, "◑◑◑◑◑ TC_CODE:DMYDR020 , CHK:"+CHK, YdConstant.INFO);
		    	
		    	if(CHK.equals("Y")){    		 
		    		if(sCAR_KIND.equals("T") || sCAR_KIND.equals("TR")){
			    		rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
						recPara 	= JDTORecordFactory.getInstance().create();
						recPara.setField("TRANS_ORD_DATE"		,YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
						recPara.setField("TRANS_ORD_SEQNO"		, YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
			 
						//출하제품핸들링횟수
						/*com.inisteel.cim.yd.ydStock.RouteModReg.procCoilGdsTrnOrdNEW.getHandlingCnt*/
						intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 418);	
						if( intRtnVal > 0 ){
							szMsg="[" + szMethodName + "] 출하제품핸들링횟수[반환값 : " + intRtnVal + "]";
							YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	 
			    		 
							for( i = 1; i <= rsResult1.size(); i++ ) {
								recInTemp = JDTORecordFactory.getInstance().create();
								rsResult1.absolute(i);					
								recInTemp 		= rsResult1.getRecord();
								
								szYD_GP			 	= StringHelper.evl(recInTemp.getFieldString("YD_GP"), "");
								szCARLD_PNT_CD 		= StringHelper.evl(recInTemp.getFieldString("CARLD_PNT_CD"), "");
								szHANDLING_CNT 		= StringHelper.evl(recInTemp.getFieldString("HANDLING_CNT"), "");
								
								
								szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 시작";
								YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("MSG_ID",        			"YDDMR050");
								recInTemp.setField("YD_GP"           	 		,szYD_GP );
								recInTemp.setField("TRANS_ORD_DT"           	,YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
								recInTemp.setField("TRANS_ORD_SEQNO"         	,YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
								recInTemp.setField("CMBN_CARLD_YN"         		,"" );
								recInTemp.setField("CARLD_PNT_CD"         		,szCARLD_PNT_CD );
								recInTemp.setField("CAR_NO"           			,YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO") );
								recInTemp.setField("HANDLING_CNT"          		,szHANDLING_CNT ); 
								recInTemp.setField("YD_STK_BED_WHIO_STAT"       ,"" );
			
								ydDelegate.sendMsg(recInTemp);
								
								szMsg="상차완료 송신 YDDMR050 (야드핸들링정보) 송신 완료";
								YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}
		    		}
		    	}
				/////////////////////////////////////////////////////////////////////////////////////////////////
	    		
				}
			
		}catch(Exception e){

			szMsg="[코일제품운송지시] Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch

		szMsg="코일제품운송지시수신 처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);

		return true;
	} // end of procCoilGdsTrnOrd()
	
	
	
	
	/**
	 * 코일제품목전
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsOrdtrn(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procCoilGdsOrdtrn";
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

			rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", szRcvTcCode);
			intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
		}catch(Exception e){
			szMsg="Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		return true;

	} // end of procCoilGdsOrdtrn()

	
	
	/**
	 * 코일제품보관지시
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */	
	public boolean procCoilGdsKeepOrd(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procCoilGdsKeepOrd";
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
			 
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			intRtnVal = ydStockDao.updYdStock2(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
		}catch(Exception e){
			szMsg="Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		return true;

	} // end of procCoilGdsKeepOrd()
	
	
	/**
	 * 코일제품상차지시등록(DMYDR023)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsCarLdOrd(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		String szMethodName = "procCoilGdsCarLdOrd";
		String szMsg = "";
		String sQueryId= "";
		String szYD_CAR_SCH_ID="";
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		YdStockDao ydStockDao = new YdStockDao();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		int intRtnVal = 0;
		boolean isTrue 			= false;
		JDTORecord recStockColumn				= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara						= JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp					= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult1 				= null;
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{
			
			String sTRANS_ORD_DT 		= StringHelper.evl(inRecord.getFieldString("TRANS_ORD_DT"),""); //운송실적일자
			String sTRANS_ORD_SEQNO 	= StringHelper.evl(inRecord.getFieldString("TRANS_ORD_SEQNO"),""); //운송실적순번
			String szCARD_NO		 	= StringHelper.evl(inRecord.getFieldString("CARD_NO"),""); //카드번호
			String szCAR_NO				= StringHelper.evl(inRecord.getFieldString("CAR_NO"),""); //차량번호
			String szCAR_KIND			= StringHelper.evl(inRecord.getFieldString("CAR_KIND"),""); //차량구분
			
			//ET,해송차량 인경우 구내운송과의 충돌을 피하기 위하여 포인트 점유하기 ////////////////////////////////////////
			String sARR_WLOC_CD= StringHelper.evl(inRecord.getFieldString("ARR_WLOC_CD"),"");
			String sARR_YD_PNT_CD= StringHelper.evl(inRecord.getFieldString("ARR_YD_PNT_CD"),"");
			
			recStockColumn.setField("CARD_NO", YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));		
			recStockColumn.setField("MODIFIER", szRcvTcCode);
						
			//개소코드가 존재 하는 경우
			if(!sARR_WLOC_CD.equals("")){
				sQueryId = "ym.ilkwan.session.CoilRegSBean.stackcolcarpoint";				
				JDTORecord 	jtR = dao.getCommonInfo(sQueryId,new Object[]{sARR_WLOC_CD,sARR_YD_PNT_CD});
				
				if(jtR != null){
					szMsg="[코일제품상차지시등록] 차량정지 위치에 이미 차량이 존재 합니다. 확인요망:";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					return false;
				}
				
				//이전 도착한 포인트 차량 비우기-----------------------------------------
				sQueryId = "ym.ilkwan.session.CoilRegSBean.stackcolcarpointupdate";	
				int chk = dao.updateData(sQueryId,new Object[]{YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO")});	
				//------------------------------------------------------------------

				recStockColumn.setField("ARR_WLOC_CD", 	sARR_WLOC_CD);
				recStockColumn.setField("ARR_YD_PNT_CD",sARR_YD_PNT_CD);
				intRtnVal = ydStockDao.updYdStock9(recStockColumn, 0);			
				if(intRtnVal <= 0){
					szMsg = "차량 포인트 예약 처리 중 에러 발생";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					return false;
				}
			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////
			
	
			
			//운송실적번호로 제품번호 가져오기
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTransStockInfo";				
			JDTORecord 	jtR = dao.getCommonInfo(sQueryId,new Object[]{sTRANS_ORD_DT+sTRANS_ORD_SEQNO});
			
			if(jtR == null){
				szMsg="[코일제품상차지시등록] 운송실적번호에 맞는 제품번호가 존재 안함:";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
			
			
			String szWLOC_CD		=jtR.getFieldString("WLOC_CD");
			String szYD_STK_COL_GP	=jtR.getFieldString("YD_STK_COL_GP");
			

			
        	//2. 작업예약취소--------------------------------------------------------------------------
    		JDTORecord jtR1 = dao.getWbookSearch(jtR.getFieldString("STL_NO"));
    		if (jtR1 == null || jtR1.size() == 0) {
    			logger.println(LogLevel.DEBUG, this, "SCH =>작업예약 정보가 없습니다.");
    		} else {
    			logger.println(LogLevel.DEBUG, this, "SCH =>복수동 상차 시 작업예약 정보가 존재 합니다.");
    			return false;
    		}
			//---------------------------------------------------------------------------------------
    		
    		
    		//3. 카드번호로 운송지시가 존재 하는 경우 -------------------------------------------------------
    		String card_No = StringHelper.evl(recStockColumn.getFieldString("CARD_NO"),"");
    		
    		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTransStockCardInfo";				
			JDTORecord 	jtR2 = dao.getCommonInfo(sQueryId,new Object[]{card_No});
			if (jtR2 == null) {
				logger.println(LogLevel.DEBUG, this, "카드번호로 운송 지시가 존재 안 함");
			}else {
				sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock10";
				int  count = dao.updateData(sQueryId,new Object[]{card_No });
				
				logger.println(LogLevel.DEBUG, this, "이전카드번호 운송지시 대상 카드번호 취소처리:"+card_No+" "+count);
			}
			//---------------------------------------------------------------------------------------
			
			
			rVal = YmCommonUtil.getCoilCurrProgCd(jtR.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("TRANS_ORD_DT", sTRANS_ORD_DT); //운송실적일자
			recStockColumn.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO); //운송실적순번
			recStockColumn.setField("STOCK_MOVE_TERM", StringHelper.evl(sStocMv,"LG"));
			
			intRtnVal = ydStockDao.updYdStock4(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
			
			
			
			String sWORK_GP =StringHelper.evl(inRecord.getFieldString("WORK_GP"), "");
			String sCAR_KIND =StringHelper.evl(inRecord.getFieldString("CAR_KIND"), "");
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
				//########################################## 9:HYSCO스케줄 ####################################################

				
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CARPNT_CD"		,StringHelper.evl(inRecord.getFieldString("CARLD_PNT_CD"), ""));
				
				//포인트코드 -> 개소코드와 저장위치 가져오기
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
				if(intRtnVal > 0){
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szYD_STK_COL_GP    = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP2"), "");
					
					
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다 작업예약 생성 후 스케줄 기동..]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					//작업예약 생성
					EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
					isTrue 	=  ((Boolean)ejbCon.trx("hsCyGoodsChulHaSangChaGisiRegistInfo", 
									new Class[]{ JDTORecord.class }, new Object[]{ inRecord })).booleanValue();
					
					//TT , TRAIER
					if(sCAR_KIND.equals("TT")||sCAR_KIND.equals("T")){
						sCAR_KIND="T";
					}else{
						if(sCAR_KIND.equals("TR")){
							sCAR_KIND="R";
						}else{
							sCAR_KIND="N";
						}
					}
					
					
					
					//도착위치가 없을 시 도착처리 skip
					if(!szYD_STK_COL_GP.equals("")){
						//차량도착처리 
						EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
						isTrue 	= ((Boolean)ejbConn.trx("carArrival",new Class[]{String.class, String.class, String.class},new Object[]{sCAR_KIND, card_No, szYD_STK_COL_GP})).booleanValue();
					}
					
					//다음 작업예약 호출을 막기 위한 처리 
					return false;
					
				
				}
				
				//################################################################################################################
			}else{
				//##########################################1:내수/2:수출/3:연안해송#############################################
				
				//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("TRANS_ORD_DT"		, sTRANS_ORD_DT);
				recPara.setField("TRANS_ORD_SEQNO"	, sTRANS_ORD_SEQNO);
				recPara.setField("CARD_NO"			, szCARD_NO );
	
				//중복 check
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
				if(intRtnVal > 0){
					szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szYD_CAR_SCH_ID    = StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
				 
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("DEL_YN", "Y");
					recInTemp.setField("MODIFIER", 	"DMYDR023");
					
					intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
					if( intRtnVal <= 0 ){
						szMsg="[" + szMethodName + "] 차량스케줄 삭제 시 오류발생[반환값 : " + intRtnVal + "]";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);					 
		    		}
				}
				////////////////////////////////////////////////////////////////////////////////////////
				
				
				//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  "U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("SPOS_WLOC_CD",     szWLOC_CD);								//발지개소코드
				recInTemp.setField("CAR_NO",           szCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          szCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   sTRANS_ORD_DT);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  sTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);		//입동지시순번 - 기본값으로 설정(9)
				recInTemp.setField("YD_CAR_PROG_STAT", "1");									//상차출발상태
	    		
				if(sCAR_KIND.equals("TT")){
					recInTemp.setField("CAR_KIND",          "TT");									//차량종류
				}else{
					recInTemp.setField("CAR_KIND",          "TR");									//차량종류
				}
				
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);			 
	    		}
	    		//////////////////////////////////////////////////////////////////////////////////////////
	    	//###########################################################################################
			}
			
			
		}catch(Exception e){

			szMsg="[코일제품상차지시등록] Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		szMsg="[코일제품상차지시등록]수신처리 ("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
		return true;
		
	} // end of procCoilGdsCarLdOrd()
	
	/**
	 *코일공통 항목을 야드 저장품 항목으로 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int edtCoilToStock(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			recEditRec.setField("REGISTER", 				YmCommonUtil.paraRecChkNull(inRecord,"REGISTER")); 			
			recEditRec.setField("STL_NO", 					YmCommonUtil.paraRecChkNull(inRecord,"COIL_NO")); 					
			recEditRec.setField("STL_PROG_CD", 				YmCommonUtil.paraRecChkNull(inRecord,"CURR_PROG_CD"));				
			recEditRec.setField("STL_APPEAR_GP", 			YmCommonUtil.paraRecChkNull(inRecord,"STL_APPEAR_GP"));		
			recEditRec.setField("ORD_YEOJAE_GP", 			YmCommonUtil.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO", 					YmCommonUtil.paraRecChkNull(inRecord,"ORD_NO")); 			
			recEditRec.setField("ORD_DTL", 					YmCommonUtil.paraRecChkNull(inRecord,"ORD_DTL")); 				
			recEditRec.setField("CUST_CD", 				    YmCommonUtil.paraRecChkNull(inRecord,"CUST_CD"));				
			
		} catch(Exception e){

			return -1;
		}
		return 1;

	} //end of edtCoilToStock()


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
	 * 코일제품반품(DMYDR033)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsRetngds(JDTORecord inRecord)throws JDTOException  {

		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName 			= "procCoilGdsRetngds";
		String szMsg 					= "";
		String szOperationName  		= "코일제품반품";
		String szSTL_NO					= "";
		String szDIST_GOODS_GP  		= "";
		String szOLD_TRANS_WORD_DATE  	= "";
		String szOLD_TRANS_WORD_SEQNO  	= "";
		String szNEW_TRANS_WORD_DATE  	= "";
		String szNEW_TRANS_WORD_SEQNO  	= "";
		String szYD_CAR_SCH_ID		  	= "";		
		String 	szRtnMsg				= null;
		
		JDTORecord recPara               = null; 	 
		JDTORecordSet rsResult           = null; 

		
		int intRtnVal 					 = 0;
		int nRet                         = 0;
 
		JDTORecord		recTemp			 = null;
		JDTORecord recStockColumn 		 = JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao 			 = new YdStockDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao  = new YdCarFtmvMtlDao();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;

		}
		try{
			
			//수신한 재료번호
			szSTL_NO  				= YmCommonUtil.paraRecChkNull(inRecord,"STL_NO");
			
			szDIST_GOODS_GP  		= YmCommonUtil.paraRecChkNull(inRecord,"DIST_GOODS_GP");
			szOLD_TRANS_WORD_DATE  	= YmCommonUtil.paraRecChkNull(inRecord,"OLD_TRANS_WORD_DATE");
			szOLD_TRANS_WORD_SEQNO  = YmCommonUtil.paraRecChkNull(inRecord,"OLD_TRANS_WORD_SEQNO");
			szNEW_TRANS_WORD_DATE  	= YmCommonUtil.paraRecChkNull(inRecord,"NEW_TRANS_WORD_DATE");
			szNEW_TRANS_WORD_SEQNO  = YmCommonUtil.paraRecChkNull(inRecord,"NEW_TRANS_WORD_SEQNO");

			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP		재료외형구분
			STL_NO				재료번호
			CURR_PROG_CD		현재진도코드
			ORD_YEOJAE_GP		주문여재구분
			ORD_NO				주문번호
			ORD_DTL				주문행번
			ORD_GP				수주구분
			CUST_CD				고객코드
			DEST_CD				목적지코드
			DEST_TEL_NO			목적지전화번호
			DIST_SHIPASSIGN_GP	출하배선지시구분
			DIST_GOODS_GP		출하제품구분	H:코일, T:HRPLATE
			OLD_TRANS_WORD_DATE	구운송지시일자
			OLD_TRANS_WORD_SEQNO구운송지시순번
			NEW_TRANS_WORD_DATE	신운송지시일자
			NEW_TRANS_WORD_SEQNO신운송지시순번

			*/
			 
			recStockColumn.setField("STL_NO", 				YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STL_PROG_CD",			YmCommonUtil.paraRecChkNull(inRecord,"CURR_PROG_CD"));			 
			recStockColumn.setField("DEL_YN", 				"N");
			recStockColumn.setField("MODIFIER", 			"DMYDR033");
 
			//****************************************************************************************************

						
			//저장품갱신******************************************************************************************** 
			String trnEqpQueryId 		= "ym.ilkwan.session.CoilRegSBean.updateStock";
			intRtnVal = dao.updateData(trnEqpQueryId, new Object[]{ szSTL_NO});
 
			if(intRtnVal <= 0){
				szMsg= "YM_STOCK[코일제품반품] UPDATE Error :: [" + intRtnVal + "]" ;
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
			YmCommonUtil.putLog(szSessionName, szMethodName,"[2] YM_STOCK[코일제품반품] UPDATE Success",3);
			//****************************************************************************************************

			
					
			
			//=====================================================================================================
			// 2013.04.11
			// 정종균
			// 1.저장품 운송지시 변경
			// 2.차량스케줄 운송지시 변경
			// 3.검수운송지시 변경 및 재검수 상태로 변경
			//=====================================================================================================
  
			// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
			/*ym.ilkwan.session.CoilRegSBean.getStockTransWord*/
			String QueryId 	= "ym.ilkwan.session.CoilRegSBean.getStockTransWord";
		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{szOLD_TRANS_WORD_DATE,szOLD_TRANS_WORD_SEQNO});
		    if(sposYNChklist.size() > 0){
				szMsg = "[코일제품반품(DMYDR033)] 이전 운송지시번호로 변경 대상이 존재 함 ";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				
				// 레코드생성
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("OLD_TRANS_WORD_DATE" , szOLD_TRANS_WORD_DATE);
				recPara.setField("OLD_TRANS_WORD_SEQNO", szOLD_TRANS_WORD_SEQNO);
				recPara.setField("NEW_TRANS_WORD_DATE" , szNEW_TRANS_WORD_DATE);
				recPara.setField("NEW_TRANS_WORD_SEQNO", szNEW_TRANS_WORD_SEQNO);
				recPara.setField("CHK_GP", "YM");
				
				// 차량스케줄 삭제 및 차량 포인트 클리어
				szMsg = "[코일제품반품(DMYDR033)] 운송지시 변경 작업 시작 - ";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
 
				
				szRtnMsg = YdCommonUtils.transOrdChange(recPara);
				
				szMsg = "[코일제품반품(DMYDR033)] 운송지시 변경 작업 완료 - " + szRtnMsg;
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
			}
			//---------------------------------------------------------------------------- 
			
			 
			
			//=====================================================================================================
			// 2013.04.11
			// 정종균
			// 1.차량스케줄 재료 삭제
			// 2.검수재료 삭제
			//=====================================================================================================
			
			//차량스케줄ID 조회--------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DATE", 			szNEW_TRANS_WORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO", 		szNEW_TRANS_WORD_SEQNO);

			szRtnMsg		= DaoManager.getYdCarsch(recPara, rsResult, 34);
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg = "["+szOperationName+"] 운송지시일자 :["+szNEW_TRANS_WORD_DATE+"] , 운송지시순번["+szNEW_TRANS_WORD_SEQNO+"]로 차량스케줄 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false ;
			}
			
			rsResult.first();
			recTemp		= rsResult.getRecord();
			
			szYD_CAR_SCH_ID	 = YmCommonUtil.paraRecChkNull(recTemp, "YD_CAR_SCH_ID");
			//--------------------------------------------------------------------------------
			
			
			//1.차량스케줄 재료 삭제--------------------------------------------------------------
			recPara =  JDTORecordFactory.getInstance().create();			
			recPara.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID);
			recPara.setField("STL_NO", 			szSTL_NO);
 			
			/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.deleteYdCarftmvmtl*/
			nRet = ydCarFtmvMtlDao.updYdCarftmvmtl(recPara, 10);
			//--------------------------------------------------------------------------------
			
			
			//2.검수재료 삭제--------------------------------------------------------------------
			recPara =  JDTORecordFactory.getInstance().create();	
			recPara.setField("TRANS_ORD_DATE", 			szNEW_TRANS_WORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO", 		szNEW_TRANS_WORD_SEQNO);
			recPara.setField("STL_NO", 					szSTL_NO);
 			
			/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.deleteYdExaminationmtl*/
			nRet = ydCarFtmvMtlDao.updYdCarftmvmtl(recPara, 11);
			//--------------------------------------------------------------------------------

			//3.저장품재료 삭제--------------------------------------------------------------------
			trnEqpQueryId 		= "ym.ilkwan.session.CoilRegSBean.updateStockEX";
			intRtnVal = dao.updateData(trnEqpQueryId, new Object[]{ szSTL_NO});
			//--------------------------------------------------------------------------------
			
		}catch(Exception e){
			szMsg="Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		szMsg="코일제품반품수신 처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
		
		return true;

	} // end of procCoilGdsRetngds()
	
	/**
	 * 코일제품출하완료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsDistCmpl(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdDelegate		ydDelegate 		= new YdDelegate();
		CraneSchDAO		craneSchDAO 		= new CraneSchDAO();
	 
		ymCommonDAO dao = ymCommonDAO.getInstance();
		JDTORecord recStockColumn				= JDTORecordFactory.getInstance().create();
		String szMethodName = "procCoilGdsDistCmpl";
		String szMsg = "";
		String sQueryId = "";
		String sSchId = "";
		String sCarCardNo = "";
		String sPointNo = "";
		String sYD_CAR_SCH_ID = "";
		String sYD_CARPNT_CD = "";
		String queryID = "";
		String szCARD_NO_CHK = "";
		String szCHK_YN = "N";
	    
		int intRtnVal = 0;
		List loadList = null;
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);
		JDTORecord recInTemp 				= null;
		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;

		}
		try{
			
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getcarStartOrderInfo";				
			List 	list = dao.getCommonList(sQueryId,new Object[]{inRecord.getFieldString("STL_NO")});			
			if(list.size()> 0){
				szMsg="[코일제품출하완료]["+recStockColumn.getFieldString("STL_NO")+"]에 대한 포인트 정보가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			}
			
			String sSTL_NO= inRecord.getFieldString("STL_NO");
			String sYD_GP= inRecord.getFieldString("YD_GP");
			String sSTL_APPEAR_GP=YmCommonUtil.paraRecChkNull(inRecord,"STL_APPEAR_GP");
			
			//출하BACKUP 시 필요 한 항목
			String sBACKUP_YN= YmCommonUtil.paraRecChkNull(inRecord,"BACKUP_YN");
			String sMODIFIER= YmCommonUtil.paraRecChkNull(inRecord,"MODIFIER");
			String sMOD_DDTT= YmCommonUtil.paraRecChkNull(inRecord,"MOD_DDTT");
			
			//스케줄 번호 가져오기-----------------------------------------------------------------------
    		JDTORecord jtR2 = dao.getSchSearch(sSTL_NO);
    		if (jtR2 == null || jtR2.size() == 0) {
    			logger.println(LogLevel.DEBUG, this, "SCH =>스케줄 정보가 없습니다.");
    		} else {	    		
	    		sSchId =YmCommonUtil.paraRecChkNull(jtR2,"SCH_ID");
	    		
	    		//스케줄 취소처리
	        	EJBConnector ejbCon = new EJBConnector("default", "JNDICraneSchReg", this);
	        	Boolean isFalse = new Boolean(false);        	
	        	
        		if(sYD_GP != null) {
        			if( (sYD_GP.compareTo("1") == 0) || (sYD_GP.compareTo("3") == 0) )
        		      
        				isFalse =  (Boolean)ejbCon.trx("cancelCoilSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });
        		      
        			if( (sYD_GP.compareTo("2") == 0) || (sYD_GP.compareTo("4") == 0 || sYD_GP.compareTo("0") == 0) )
        		      
        				isFalse =  (Boolean)ejbCon.trx("cancelSlabSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });
        		}
        		
	        	if(isFalse.booleanValue() == true){
	        		logger.println(LogLevel.DEBUG, this,  "스케줄취소 처리가 완료되었습니다.");
	        	}else{	
	        		logger.println(LogLevel.DEBUG, this, "스케줄취소 처리도중에 에러가 발생하였습니다.");
	        		return false;
	        	}
    		}
        	//--------------------------------------------------------------------------------------
        	
        	
        	//2. 작업예약취소--------------------------------------------------------------------------
    		JDTORecord jtR1 = dao.getWbookSearch(sSTL_NO);
    		if (jtR1 == null || jtR1.size() == 0) {
    			logger.println(LogLevel.DEBUG, this, "SCH =>작업예약 정보가 없습니다.");
    		} else {	        	
        	
	        	//작업예약취소처리
	        	EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
	        	Boolean resultRes =  (Boolean) ejbConn1.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {sSTL_NO});	
	        	if(resultRes.booleanValue() == true){
	        		logger.println(LogLevel.DEBUG, this,  "작업예약취소  처리가 완료되었습니다.");
	        	}else{	
	        		logger.println(LogLevel.DEBUG, this, "작업예약취소 처리도중에 에러가 발생하였습니다.");
	        		return false;
	        	}
    		}
        	//--------------------------------------------------------------------------------------
    		
    		//출하완료 잔여 건수
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getcarStartOrderInfo2";				
			List 	chklist = dao.getCommonList(sQueryId,new Object[]{inRecord.getFieldString("STL_NO"),inRecord.getFieldString("STL_NO")});	
			
			szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]출하완료 잔여 건수:"+chklist.size();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);
			
			rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			
			if(sBACKUP_YN.equals("Y")){
				recStockColumn.setField("MODIFIER", sMODIFIER);
			}else{
				recStockColumn.setField("MODIFIER", "DMYDR030");
			}
			
			intRtnVal = ydStockDao.updYdStock3(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
			
			
			
			/*
		 	 *****************************************************************************
		 	 *차량  자동출발 모듈 CALL
		 	 */
			
			//if(list.size()> 0){
			if(sSTL_APPEAR_GP.equals("*")){	
			

				
				
				
				if(chklist.size()== 0){
					if(list.size()> 0){
						JDTORecord jtR = (JDTORecord)list.get(0);
						sCarCardNo 	= YmCommonUtil.paraRecChkNull(jtR,"CARD_NO");
			 			sPointNo 	= YmCommonUtil.paraRecChkNull(jtR,"STACK_COL_GP");
			 			sYD_CAR_SCH_ID 	= YmCommonUtil.paraRecChkNull(jtR,"YD_CAR_SCH_ID");
			 			sYD_CARPNT_CD 	= YmCommonUtil.paraRecChkNull(jtR,"YD_CARPNT_CD");
			 			
			 			
			 			//장비번호 가져오기 					
						queryID	= "ym.tsinfo.getLoadendLayer";
						loadList = dao.getCommonList(queryID, new Object[]{sPointNo});
						
						if(loadList.size()>0){
							JDTORecord FrtoProduct = (JDTORecord)loadList.get(0);	    	
							szCARD_NO_CHK = StringHelper.evl(FrtoProduct.getFieldString("CARD_NO"),"");
					    	
							//다른 차량이 존재 하는 경우 
					    	if(!szCARD_NO_CHK.equals(sCarCardNo)){
					    		szCHK_YN ="Y";
					    	}
						}
					}
					
					
	 		
	 			if(!sCarCardNo.equals("")){

					EJBConnector ejbConn1 = new EJBConnector("default","JNDICTSStatusReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("carStartOrder",
												new  Class[]{String.class,
															 String.class,
															 String.class},
												new Object[]{" ",						//한자리공백
															sCarCardNo,					//카드번호
															sPointNo});					//차량정지위치

					
					
					
					szMsg="[" + szMethodName + "] 차량정지위치["+szCHK_YN+"-" + sPointNo + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "]  ";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					
					//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
					if(!sYD_CARPNT_CD.equals("")&& szCHK_YN.equals("N")){
						/*
						 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
						 */
						szMsg="[" + szMethodName + "] 차량정지위치[" + sPointNo + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - AB차량입동지시요구 모듈을 호출 시작";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
						
						
						recInTemp = JDTORecordFactory.getInstance().create(); 
						recInTemp.setField("JMS_TC_CD",  "YDYDJ662");
						recInTemp.setField("YD_CARPNT_CD",    sYD_CARPNT_CD);	//입동포인트
						recInTemp.setField("YD_CAR_SCH_ID",    sYD_CAR_SCH_ID);	//차량스케줄ID
				 
						ydDelegate.sendMsg(recInTemp);
						
//						EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
//						ejbConn.trx("procCarBayInOrdReqNEW", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
						

						szMsg="[" + szMethodName + "] 차량정지위치[" + sPointNo + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - AB차량입동지시요구 모듈을 호출 성공";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					 
					}
					//////////////////////////////////////////////////////////////////////////////////////////
	 			}
				}
			}
		 	/*
		 	 *****************************************************************************
		 	 */
 			
			szMsg="[코일제품출하완료]["+recStockColumn.getFieldString("STL_NO")+"] BACKUP 유무=>:+"+sBACKUP_YN;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			if(sBACKUP_YN.equals("Y")){
			//if(szCHK_YN.equals("N")){
	 			//layer정보가 존재 하는 경우 (백업으로 취소 처리 시)
	 			sQueryId = "com.inisteel.cim.ym.dao.getStacklayerList";				
				List 	list2 = dao.getCommonList(sQueryId,new Object[]{inRecord.getFieldString("STL_NO")});			
				if(list2.size()> 0){
					
					sQueryId = "com.inisteel.cim.ym.dao.updateStacklayer";		
					int chk=	dao.updateData(sQueryId,new Object[]{inRecord.getFieldString("STL_NO")});			
					
					JDTORecord jtR3 = (JDTORecord)list2.get(0);
					sPointNo 	= YmCommonUtil.paraRecChkNull(jtR3,"STACK_COL_GP");
					
					szMsg="[코일제품출하완료]["+recStockColumn.getFieldString("STL_NO")+"]에 저장위치맵을 비웁니다.+"+sPointNo;
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
				}
					
		  
					/*
					 * Coil 공통 Table 저장위치 Update 
					 */	 
					if(!"".equals(sPointNo)){
						sPointNo=sPointNo.substring(0 , 2);
					}else{
						sPointNo=sYD_GP+"X";
					}
					
					//코일 상차백업 위치로 위치 변경 작업 
					int chk2 =craneSchDAO.updateCoilCommonLocInfo(inRecord.getFieldString("STL_NO"),sPointNo + "PT010101");  
				
			}
 			
 			
		 	
		 	
		}catch(Exception e){
			szMsg = "[코일제품출하완료 수신]Exception Error : "+ e.getMessage() ;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		}
		
		szMsg="코일제품출하완료 수신 처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
		
		return true;

	} // end of procCoilGdsDistCmpl
	
	
	/**
	 * 임가공이송상차지시(DMYDR025)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilMatlRentGdsCarLdOrd(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procCoilMatlRentGdsCarLdOrd";
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
				recStockColumn.setField("MODIFIER", 				"DMYDR025");
				
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

			szMsg="[임가공이송상차지시등록] Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		szMsg="[임가공이송상차지시등록]수신처리 ("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
		return true;
		
	} // end of procCoilMatlRentGdsCarLdOrd()
	
	
	
	/**
	 * 코일이송상차대기장도착PDA(DMYDR070) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsTrnOrdLdPDAAB(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		//재료상태변경이력 DAO
		JDTORecord recStockColumn 	= JDTORecordFactory.getInstance().create();
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		JDTORecord recInTemp		= JDTORecordFactory.getInstance().create(); 
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsResult1 	= null;
		String szMethodName = "procCoilGdsTrnOrdLdPDAAB";
		String szMsg = "";
		String  szYD_CAR_SCH_ID = "";
		String szWLOC_CD			= "";
		String szYD_STK_COL_GP		= "";
 
		String szYD_PNT_CD 			= "";
		String szGDS_CARLD_LOC	= "";
		String  szYD_STK_BED_NO	= "";
		String szMOV_YN				= "N";
		int i = 0;
		
		int intRtnVal = 0;

		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);
 
		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{
			   for(i = 1 ; i<=20; i++){
				   
					String chk =YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i);
					
					if(chk.equals("")){
						break;
					}
					
					rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"+i),szRcvTcCode);
					sStocMv = rVal[1];
					recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i));
					recStockColumn.setField("SHEAR_SUPPLY_SEQ", YmCommonUtil.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i)); //야드 차상위치코드
					recStockColumn.setField("TRANS_ORD_DT", YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
					recStockColumn.setField("TRANS_ORD_SEQNO", YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
					recStockColumn.setField("CAR_KIND", YmCommonUtil.paraRecChkNull(inRecord,"CAR_KIND")); //차량구분
					recStockColumn.setField("CAR_CARD_NO", YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));
					recStockColumn.setField("CAR_NO2", YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO"));
					recStockColumn.setField("CR_FRTOMOVE_GP", YmCommonUtil.paraRecChkNull(inRecord,"CR_FRTOMOVE_GP"));
					recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
					recStockColumn.setField("MODIFIER", szRcvTcCode);
					intRtnVal = ydStockDao.updYdStock11(recStockColumn, 0);
				}
			   
			   
			   String sWORK_GP = YmCommonUtil.paraRecChkNull(inRecord,"WORK_GP");
			   String sCAR_KIND =YmCommonUtil.paraRecChkNull(inRecord,"CAR_KIND"); //장비구분: Trailer-T , TT Trailer -TT
			   szMOV_YN = YmCommonUtil.paraRecChkNull(inRecord,"UGNT_BAYIN_YN");
			   szMsg= "["+szMethodName+"] 작업구분:::::"+sWORK_GP+" UGNT_BAYIN_YN:"+ szMOV_YN;
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
			   
				//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
				if(sWORK_GP.equals("9")){
				//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("TRANS_ORD_DT"		, YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
				recPara.setField("TRANS_ORD_SEQNO"	, YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
				recPara.setField("CARD_NO"			, YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO") );

				//중복 check
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
				if(intRtnVal > 0){
					szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szYD_CAR_SCH_ID    = StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
				 
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("DEL_YN", "Y");
					recInTemp.setField("MODIFIER", 	szRcvTcCode);
					
					intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
					if( intRtnVal <= 0 ){
						szMsg="[" + szMethodName + "] 차량스케줄 삭제 시 오류발생[반환값 : " + intRtnVal + "]";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);					 
		    		}
				}
				////////////////////////////////////////////////////////////////////////////////////////
				
				
				//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
				
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CARPNT_CD"		, YmCommonUtil.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
				
				//포인트코드 -> 개소코드와 저장위치 가져오기
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
				if(intRtnVal > 0){
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다..]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szWLOC_CD    		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
					szYD_STK_COL_GP    	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");		
					szYD_PNT_CD	    	= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");
					
					if(!"Y".equals(szMOV_YN)){
						szMOV_YN		    	= StringHelper.evl(recInTemp.getFieldString("MOV_YN"), "");	
					}
					
					/*
					 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
					 */
					szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         szRcvTcCode);
					recInTemp.setField("YD_EQP_WRK_STAT",  "U");									//야드설비작업상태
					recInTemp.setField("YD_EQP_ID",        YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
					recInTemp.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
					recInTemp.setField("SPOS_WLOC_CD",     szWLOC_CD);								//발지개소코드
					recInTemp.setField("CAR_NO",           YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO"));								//차량번호
					recInTemp.setField("CARD_NO",          YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));								//카드번호
					recInTemp.setField("YD_CARLD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
					recInTemp.setField("TRANS_ORD_DATE",   YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));						//운송지시일자
					recInTemp.setField("TRANS_ORD_SEQNO",  YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));						//운송지시순번
			    	recInTemp.setField("YD_CARLD_STOP_LOC",szYD_STK_COL_GP);						//차량상차정지위치
			    	//recInTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);		//입동지시순번 - 기본값으로 설정(9)
			    	
			    	if("Y".equals(szMOV_YN)){
			    		recInTemp.setField("YD_BAYIN_WO_SEQ",  	"1");						//입동지시순번 - 1순위변경
			    	}else{
			    		recInTemp.setField("YD_BAYIN_WO_SEQ",  	YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);						//입동지시순번 - 기본값으로 설정(9)
			    	}
			    	
					recInTemp.setField("YD_CAR_PROG_STAT", "1");									//상차출발상태
					recInTemp.setField("YD_CAR_WRK_GP", 	sWORK_GP);
					recInTemp.setField("YD_PNT_CD1", 	szYD_PNT_CD);
					recInTemp.setField("TRANS_EQUIPMENT_TYPE", 	"P");			//운송장비타입 P : PDA
										
					if(sCAR_KIND.equals("TT")){
						recInTemp.setField("CAR_KIND",          "TT");									//차량종류
					}else{
						recInTemp.setField("CAR_KIND",          "TR");									//차량종류
					}
					
					recInTemp.setField("TEL_NO",   		YmCommonUtil.paraRecChkNull(inRecord,"TEL_NO"));						//연락처
					recInTemp.setField("DRIVER_NAME",  	YmCommonUtil.paraRecChkNull(inRecord,"DRIVER_NAME"));						//운전기사명
		    		
		    		//차량스케줄 등록
			    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
		    		if( intRtnVal <= 0 ){
						szMsg="[" + szMethodName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);	 
		    		}
		    		
		    		int intYD_EQP_WRK_SH = YmCommonUtil.paraRecChkNullInt(inRecord, "YD_EQP_WRK_SH");
		    		
		    		szMsg="[" + szMethodName + "] 차량스케줄 상차 수량: [ " + YmCommonUtil.paraRecChkNull(inRecord, "YD_EQP_WRK_SH") + " ]";
		    		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
		    		
		    		//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
					for(i = 1 ; i<=intYD_EQP_WRK_SH; i++){
						szGDS_CARLD_LOC = YmCommonUtil.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i);
						
						if(szGDS_CARLD_LOC.substring(0 , 1).equals("A")){
							szYD_STK_BED_NO = "0"+szGDS_CARLD_LOC.substring(1 , 2);
						}else if(szGDS_CARLD_LOC.substring(0 , 1).equals("B")){
							if(szGDS_CARLD_LOC.substring(1 , 2).equals("1")){
								szYD_STK_BED_NO="06";
							}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("2")){
								szYD_STK_BED_NO="07";
							}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("3")){
								szYD_STK_BED_NO="08";
							}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("4")){
								szYD_STK_BED_NO="09";
							}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("5")){
								szYD_STK_BED_NO="10";
							}
						}else if(szGDS_CARLD_LOC.substring(0 , 1).equals("C")){
							if(szGDS_CARLD_LOC.substring(1 , 2).equals("1")){
								szYD_STK_BED_NO="11";
							}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("2")){
								szYD_STK_BED_NO="12";
							}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("3")){
								szYD_STK_BED_NO="13";
							}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("4")){
								szYD_STK_BED_NO="14";
							}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("5")){
								szYD_STK_BED_NO="15";
							}
						} 
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
						recInTemp.setField("REGISTER",         		szRcvTcCode);
						recInTemp.setField("STL_NO", 				YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i));	 
						//recInTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO); //야드 차상위치코드
						recInTemp.setField("YD_STK_LYR_NO", 		"001");
						recInTemp.setField("DEL_YN"					, "N");
				 
						intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(recInTemp);
			    		if(intRtnVal != 1) {
			    			szMsg="[" + szMethodName + "] 차량스케줄제료 생성 시 오류발생[반환값 : " + intRtnVal + "]";
			    			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			    		}


					} //end of for *******************************************************************************************
		    		
		    		
		    		
		    		
		    		if(sCAR_KIND.equals("TT")){
		    			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn3 = new EJBConnector("default","JNDITsInfoReg",this);
						ejbConn3.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"C",YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO"),YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"),szYD_STK_COL_GP,"","","R"});
		    		}
				}else {
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 안합니다..]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				}
	    		//////////////////////////////////////////////////////////////////////////////////////////
				
				
				//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
				if(sCAR_KIND.equals("T") || sCAR_KIND.equals("TR")){
					/*
					 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 */
					szMsg="[" + szMethodName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] -PDA AB차량입동지시요구 모듈을 호출 시작";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					
					recInTemp = JDTORecordFactory.getInstance().create();			 
					recInTemp.setField("YD_CARPNT_CD"			,YmCommonUtil.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
					recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
					
					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
					ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					

					szMsg="[" + szMethodName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] -PDA AB차량입동지시요구 모듈을 호출 성공";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				}
				//////////////////////////////////////////////////////////////////////////////////////////
				
				
				}
			
		}catch(Exception e){

			szMsg="[코일이송상차대기장도착PDA] Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch

		szMsg="코일이송상차대기장도착PDA 처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);

		return true;
	} // end of procCoilGdsTrnOrdLdPDAAB()
	
	
	
	/**
	 * AB코일이송상차도착PDA(DMYDR071)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procStandByYdArriveLdPDAAB(JDTORecord inRecord)throws JDTOException  {

		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procStandByYdArriveLdPDAAB";
		String szMsg = "";
		String sQueryId= "";
		String szYD_STK_COL_GP="";
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		YdStockDao ydStockDao = new YdStockDao();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		int intRtnVal = 0;
		boolean isTrue 			= false;
		JDTORecord recStockColumn				= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara						= JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp					= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult1 				= null;
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{
			
			String sTRANS_ORD_DT 		= StringHelper.evl(inRecord.getFieldString("TRANS_ORD_DT"),""); //운송실적일자
			String sTRANS_ORD_SEQNO 	= StringHelper.evl(inRecord.getFieldString("TRANS_ORD_SEQNO"),""); //운송실적순번
		
			
  
			recStockColumn.setField("CARD_NO", YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));		
			recStockColumn.setField("MODIFIER", szRcvTcCode);
						
  
			//운송실적번호로 제품번호 가져오기
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTransStockInfo";				
			JDTORecord 	jtR = dao.getCommonInfo(sQueryId,new Object[]{sTRANS_ORD_DT+sTRANS_ORD_SEQNO});
			
			if(jtR == null){
				szMsg="[코일제품상차지시등록] 운송실적번호에 맞는 제품번호가 존재 안함:";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
			
 
        	//2. 작업예약취소--------------------------------------------------------------------------
    		JDTORecord jtR1 = dao.getWbookSearch(jtR.getFieldString("STL_NO"));
    		if (jtR1 == null || jtR1.size() == 0) {
    			logger.println(LogLevel.DEBUG, this, "SCH =>작업예약 정보가 없습니다.");
    		} else {
    			logger.println(LogLevel.DEBUG, this, "SCH =>복수동 상차 시 작업예약 정보가 존재 합니다.");
    			return false;
    		}
			//---------------------------------------------------------------------------------------
    		
    		
    		//3. 카드번호로 운송지시가 존재 하는 경우 -------------------------------------------------------
    		String card_No = StringHelper.evl(recStockColumn.getFieldString("CARD_NO"),"");
    		
    		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTransStockCardInfo";				
			JDTORecord 	jtR2 = dao.getCommonInfo(sQueryId,new Object[]{card_No});
			if (jtR2 == null) {
				logger.println(LogLevel.DEBUG, this, "카드번호로 운송 지시가 존재 안 함");
			}else {
				sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock10";
				int  count = dao.updateData(sQueryId,new Object[]{card_No });
				
				logger.println(LogLevel.DEBUG, this, "이전카드번호 운송지시 대상 카드번호 취소처리:"+card_No+" "+count);
			}
			//---------------------------------------------------------------------------------------
			
			
			rVal = YmCommonUtil.getCoilCurrProgCd(jtR.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("TRANS_ORD_DT", sTRANS_ORD_DT); //운송실적일자
			recStockColumn.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO); //운송실적순번
			recStockColumn.setField("STOCK_MOVE_TERM", StringHelper.evl(sStocMv,"LG"));
			
			intRtnVal = ydStockDao.updYdStock4(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
			
			
			
			String sWORK_GP =StringHelper.evl(inRecord.getFieldString("WORK_GP"), "");
			String sCAR_KIND =StringHelper.evl(inRecord.getFieldString("CAR_KIND"), "");
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
				//########################################## 9:HYSCO스케줄 ####################################################

				
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CARPNT_CD"		,StringHelper.evl(inRecord.getFieldString("CARLD_PNT_CD"), ""));
				
				//포인트코드 -> 개소코드와 저장위치 가져오기
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
				if(intRtnVal > 0){
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szYD_STK_COL_GP    = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP2"), "");
					
					
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다 작업예약 생성 후 스케줄 기동..]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					//작업예약 생성
					inRecord.setField("CARUD_GP",     "L");
					inRecord.setField("YD_STK_COL_GP",     szYD_STK_COL_GP);
					EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
					isTrue 	=  ((Boolean)ejbCon.trx("hsCyGoodsChulHaSangChaGisiRegistInfo", 
									new Class[]{ JDTORecord.class }, new Object[]{ inRecord })).booleanValue();
					
					//TT , TRAIER
					if(sCAR_KIND.equals("TT")||sCAR_KIND.equals("T")){
						sCAR_KIND="T";
					}else{
						if(sCAR_KIND.equals("TR")){
							sCAR_KIND="R";
						}else{
							sCAR_KIND="N";
						}
					}
					
					
					
					//도착위치가 없을 시 도착처리 skip
					if(!szYD_STK_COL_GP.equals("")){
						//차량도착처리 
						EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
						isTrue 	= ((Boolean)ejbConn.trx("carArrival",new Class[]{String.class, String.class, String.class},new Object[]{sCAR_KIND, card_No, szYD_STK_COL_GP})).booleanValue();
					}
					
					//다음 작업예약 호출을 막기 위한 처리 
					return false;
					
				
				}
				
				//################################################################################################################
			} 
			
			
		}catch(Exception e){

			szMsg="[AB코일이송상차도착PDA] Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		szMsg="[AB코일이송상차도착PDA]수신처리 ("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
		return true;
		
	} // end of procStandByYdArriveLdPDAAB()
	
	
	
	/**
	 * AB코일이송상차완료PDA(DMYDR072,DMYDR075)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procCoilGdsDistCmplLdPDAAB(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdDelegate		ydDelegate 		= new YdDelegate();
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		JDTORecord recStockColumn				= JDTORecordFactory.getInstance().create();
		String szMethodName = "procCoilGdsDistCmplLdPDAAB";
		String szMsg = "";
		String sQueryId = "";
		String sSchId = "";
		String sCarCardNo = "";
		String sPointNo = "";
		String sYD_CAR_SCH_ID = "";
		String sYD_CARPNT_CD = "";
		String queryID = "";
		String szCARD_NO_CHK = "";
		String szCHK_YN = "N";
	    
		int intRtnVal = 0;
		List loadList = null;
		//전문받아서 szRcvTcCode에 저장
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);
		JDTORecord recInTemp 				= null;
		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;

		}
		try{
			
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getcarStartOrderInfo";				
			List 	list = dao.getCommonList(sQueryId,new Object[]{inRecord.getFieldString("STL_NO")});			
			if(list.size()> 0){
				szMsg="[AB코일이송상차완료PDA ]["+inRecord.getFieldString("STL_NO")+"]에 대한 포인트 정보가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			}
			
			String sSTL_NO= inRecord.getFieldString("STL_NO");
			String sYD_GP= inRecord.getFieldString("YD_GP");
			String sSTL_APPEAR_GP=YmCommonUtil.paraRecChkNull(inRecord,"STL_APPEAR_GP");
			
			//스케줄 번호 가져오기-----------------------------------------------------------------------
    		JDTORecord jtR2 = dao.getSchSearch(sSTL_NO);
    		if (jtR2 == null || jtR2.size() == 0) {
    			logger.println(LogLevel.DEBUG, this, "SCH =>스케줄 정보가 없습니다.");
    		} else {	    		
	    		sSchId =YmCommonUtil.paraRecChkNull(jtR2,"SCH_ID");
	    		
	    		//스케줄 취소처리
	        	EJBConnector ejbCon = new EJBConnector("default", "JNDICraneSchReg", this);
	        	Boolean isFalse = new Boolean(false);        	
	        	
        		if(sYD_GP != null) {
        			if( (sYD_GP.compareTo("1") == 0) || (sYD_GP.compareTo("3") == 0) )
        		      
        				isFalse =  (Boolean)ejbCon.trx("cancelCoilSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });
        		      
        			if( (sYD_GP.compareTo("2") == 0) || (sYD_GP.compareTo("4") == 0 || sYD_GP.compareTo("0") == 0) )
        		      
        				isFalse =  (Boolean)ejbCon.trx("cancelSlabSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });
        		}
        		
	        	if(isFalse.booleanValue() == true){
	        		logger.println(LogLevel.DEBUG, this,  "스케줄취소 처리가 완료되었습니다.");
	        	}else{	
	        		logger.println(LogLevel.DEBUG, this, "스케줄취소 처리도중에 에러가 발생하였습니다.");
	        		return false;
	        	}
    		}
        	//--------------------------------------------------------------------------------------
        	
        	
        	//2. 작업예약취소--------------------------------------------------------------------------
    		JDTORecord jtR1 = dao.getWbookSearch(sSTL_NO);
    		if (jtR1 == null || jtR1.size() == 0) {
    			logger.println(LogLevel.DEBUG, this, "SCH =>작업예약 정보가 없습니다.");
    		} else {	        	
        	
	        	//작업예약취소처리
	        	EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
	        	Boolean resultRes =  (Boolean) ejbConn1.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {sSTL_NO});	
	        	if(resultRes.booleanValue() == true){
	        		logger.println(LogLevel.DEBUG, this,  "작업예약취소  처리가 완료되었습니다.");
	        	}else{	
	        		logger.println(LogLevel.DEBUG, this, "작업예약취소 처리도중에 에러가 발생하였습니다.");
	        		return false;
	        	}
    		}
        	//--------------------------------------------------------------------------------------
    		
    		//출하완료 잔여 건수
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getcarStartOrderInfo2";				
			List 	chklist = dao.getCommonList(sQueryId,new Object[]{inRecord.getFieldString("STL_NO"),inRecord.getFieldString("STL_NO")});	
			
			szMsg = "수신한 재료번호 ["+inRecord.getFieldString("STL_NO")+"]출하완료 잔여 건수:"+chklist.size();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);
			
			rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"),szRcvTcCode);
			sStocMv = rVal[1];
			recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"));
			recStockColumn.setField("STOCK_MOVE_TERM", sStocMv);
			recStockColumn.setField("MODIFIER", szRcvTcCode);
			intRtnVal = ydStockDao.updYdStock3(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
			
			
			
			/*
		 	 *****************************************************************************
		 	 *차량  자동출발 모듈 CALL
		 	 */
			
			//if(list.size()> 0){
			if(sSTL_APPEAR_GP.equals("*")){	
			

				
				
				
				if(chklist.size()== 0){
					if(list.size()> 0){
						JDTORecord jtR = (JDTORecord)list.get(0);
						sCarCardNo 	= YmCommonUtil.paraRecChkNull(jtR,"CARD_NO");
			 			sPointNo 	= YmCommonUtil.paraRecChkNull(jtR,"STACK_COL_GP");
			 			sYD_CAR_SCH_ID 	= YmCommonUtil.paraRecChkNull(jtR,"YD_CAR_SCH_ID");
			 			sYD_CARPNT_CD 	= YmCommonUtil.paraRecChkNull(jtR,"YD_CARPNT_CD");
			 			
			 			
			 			//장비번호 가져오기 					
						queryID	= "ym.tsinfo.getLoadendLayer";
						loadList = dao.getCommonList(queryID, new Object[]{sPointNo});
						
						if(loadList.size()>0){
							JDTORecord FrtoProduct = (JDTORecord)loadList.get(0);	    	
							szCARD_NO_CHK = StringHelper.evl(FrtoProduct.getFieldString("CARD_NO"),"");
					    	
							//다른 차량이 존재 하는 경우 
					    	if(!szCARD_NO_CHK.equals(sCarCardNo)){
					    		szCHK_YN ="Y";
					    	}
						}
					}
					
					
	 		
	 			if(!sCarCardNo.equals("")){

					EJBConnector ejbConn1 = new EJBConnector("default","JNDICTSStatusReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("carStartOrder",
												new  Class[]{String.class,
															 String.class,
															 String.class},
												new Object[]{" ",						//한자리공백
															sCarCardNo,					//카드번호
															sPointNo});					//차량정지위치

					
					
					
					szMsg="[" + szMethodName + "] 차량정지위치["+szCHK_YN+"-" + sPointNo + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "]  ";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					
					//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
					if(!sYD_CARPNT_CD.equals("")&& szCHK_YN.equals("N")){
						/*
						 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
						 */
						szMsg="[" + szMethodName + "] 차량정지위치[" + sPointNo + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - AB차량입동지시요구 모듈을 호출 시작";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
						
						
						recInTemp = JDTORecordFactory.getInstance().create(); 
						recInTemp.setField("JMS_TC_CD",  "YDYDJ662");
						recInTemp.setField("YD_CARPNT_CD",    sYD_CARPNT_CD);	//입동포인트
						recInTemp.setField("YD_CAR_SCH_ID",    sYD_CAR_SCH_ID);	//차량스케줄ID
				 
						ydDelegate.sendMsg(recInTemp);
						
//						EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
//						ejbConn.trx("procCarBayInOrdReqNEW", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
						

						szMsg="[" + szMethodName + "] 차량정지위치[" + sPointNo + "], 차량스케줄ID[" + sYD_CAR_SCH_ID + "] - AB차량입동지시요구 모듈을 호출 성공";
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					 
					}
					//////////////////////////////////////////////////////////////////////////////////////////
	 			}
				}
			}
		 	/*
		 	 *****************************************************************************
		 	 */
			if("DMYDR075".equals(szRcvTcCode)){
				szCHK_YN ="Y";  //하차작업완료 인경우 생략 ,
			}
 			
 			
			if(szCHK_YN.equals("N")){
	 			//layer정보가 존재 하는 경우 (백업으로 취소 처리 시)
	 			sQueryId = "com.inisteel.cim.ym.dao.getStacklayerList";				
				List 	list2 = dao.getCommonList(sQueryId,new Object[]{inRecord.getFieldString("STL_NO")});			
				if(list2.size()> 0){
					
					sQueryId = "com.inisteel.cim.ym.dao.updateStacklayer";		
					int chk=	dao.updateData(sQueryId,new Object[]{inRecord.getFieldString("STL_NO")});			
					
					
					szMsg="[코일제품출하완료]["+inRecord.getFieldString("STL_NO")+"]에 저장위치맵을 비웁니다.";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				}
			}
 			
 			
		 	
		 	
		}catch(Exception e){
			szMsg = "[AB 코일이송상차완료PDA수신]Exception Error : "+ e.getMessage() ;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		}
		
		szMsg="AB 코일이송상차완료PDA수신 처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg,4);
		
		return true;

	} // end of procCoilGdsDistCmplLdPDAAB
	
	
	
	/**
	 * AB코일이송하차대기장도착PDA(DMYDR073)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsTrnOrdUdPDAAB(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdStockDAO dao	= new YdStockDAO();
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
 
		
		JDTORecord recInTemp		= JDTORecordFactory.getInstance().create();
		JDTORecord recStockColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara			= JDTORecordFactory.getInstance().create();
 
		JDTORecordSet rsResult1 	= null;
		String szMethodName 		= "procCoilGdsTrnOrdUdPDAAB";
		String szMsg 				= "";
		String szOperationName      = "AB코일이송하차대기장도착PDA";
		String szSTL_NO 			= "";
		String szYD_CAR_SCH_ID 		= "";
		String szWLOC_CD			= "";
		String szYD_STK_COL_GP		= "";	
		
 
		String szYD_PNT_CD 			= "";
		String szGDS_CARLD_LOC		= "";
		String szYD_STK_BED_NO		= "";
		String szSTOCK_MOVE_TERM	= "";
		String szMOV_YN				= "N";
		
		int intRtnVal 				= 0;
		int i =0;
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		try{
			
			String sCAR_KIND =YmCommonUtil.paraRecChkNull(inRecord,"CAR_KIND"); //장비구분: Trailer-T , TT Trailer -TT
			 
			for(i = 1 ; i<=20; i++){
				   
				szSTL_NO =YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i);
				
				if(szSTL_NO.equals("")){
					break;
				}
				
				//rVal = YmCommonUtil.getCoilCurrProgCd(inRecord.getFieldString("STL_NO"+i),szRcvTcCode);
				//sStocMv = rVal[1];
				szSTOCK_MOVE_TERM ="CS";
				recStockColumn.setField("STOCK_ID", YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i));
				recStockColumn.setField("SHEAR_SUPPLY_SEQ", YmCommonUtil.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i)); //야드 차상위치코드
				recStockColumn.setField("TRANS_ORD_DT", YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
				recStockColumn.setField("TRANS_ORD_SEQNO", YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
				recStockColumn.setField("CAR_KIND", YmCommonUtil.paraRecChkNull(inRecord,"CAR_KIND")); //차량구분
				recStockColumn.setField("CAR_CARD_NO", YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));
				recStockColumn.setField("CAR_NO2", YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO"));
				recStockColumn.setField("CR_FRTOMOVE_GP", YmCommonUtil.paraRecChkNull(inRecord,"CR_FRTOMOVE_GP"));
				recStockColumn.setField("STOCK_MOVE_TERM", szSTOCK_MOVE_TERM);
				recStockColumn.setField("MODIFIER", szRcvTcCode); 
				
				boolean isExist = dao.isExistPrimaryKey(szSTL_NO);
		    	
		    	if(!isExist){
					logger.println(LogLevel.DEBUG,this,"NOT EXIST STOCK TABLE COIL DATA");
					
					intRtnVal = dao.insertStockTransInfo(szSTL_NO , // 저장품ID
														YmCommonConst.ITEM_CG , // 저장품품목(코일제품)
														szSTOCK_MOVE_TERM // 저장품이동조건
														);  			      
				}
				
		    	intRtnVal = ydStockDao.updYdStock11(recStockColumn, 0);
				
				
			}
			
			String sWORK_GP =YmCommonUtil.paraRecChkNull(inRecord,"WORK_GP");
			szMOV_YN = YmCommonUtil.paraRecChkNull(inRecord,"UGNT_BAYIN_YN");
			
			szMsg= "["+szMethodName+"] 작업구분:::::"+sWORK_GP+" UGNT_BAYIN_YN:"+ szMOV_YN;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
			//차량정보 존재여부 체크 /////////////////////////////////////////////////////////////////
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("TRANS_ORD_DT"		, YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
			recPara.setField("TRANS_ORD_SEQNO"	, YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
			recPara.setField("CARD_NO"			, YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO") );

			//중복 check
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
			if(intRtnVal > 0){
				szMsg= "["+szMethodName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				szYD_CAR_SCH_ID    = StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
			 
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recInTemp.setField("DEL_YN", "Y");
				recInTemp.setField("MODIFIER", 	szRcvTcCode);
				
				intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
				if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 삭제 시 오류발생[반환값 : " + intRtnVal + "]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);					 
	    		}
			}
			////////////////////////////////////////////////////////////////////////////////////////
			
			
			//차량스케줄 생성 ////////////////////////////////////////////////////////////////////////
			
			rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CARPNT_CD"		, YmCommonUtil.paraRecChkNull(inRecord,"CARUD_PNT_CD"));
			
			//포인트코드 -> 개소코드와 저장위치 가져오기
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
			if(intRtnVal > 0){
				szMsg= "["+szMethodName+"] TB_YD_CARPOINT[AB차량포인트가 존재 합니다..]";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				rsResult1.first();
				recInTemp = rsResult1.getRecord();
				szWLOC_CD    		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
				szYD_STK_COL_GP    	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");		
				szYD_PNT_CD	    	= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");	
				
				if(!"Y".equals(szMOV_YN)){
					szMOV_YN		    	= StringHelper.evl(recInTemp.getFieldString("MOV_YN"), "");	
				}
				
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    	szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         	szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  	"L");													//야드설비작업상태(영차)
				recInTemp.setField("YD_EQP_ID",        	YdConstant.YD_DM_CAR_EQP_ID);							//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    	YdConstant.YD_CAR_USE_GP_DM);							//차량사용구분
				recInTemp.setField("SPOS_WLOC_CD",     	szWLOC_CD);
				recInTemp.setField("ARR_WLOC_CD",     	szWLOC_CD);												//발지개소코드
				recInTemp.setField("CAR_NO",           	YmCommonUtil.paraRecChkNull(inRecord,"CAR_NO"));			//차량번호
				recInTemp.setField("CARD_NO",          	YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));			//카드번호
				recInTemp.setField("YD_CARUD_LEV_DT",  	YdUtils.getCurDate("yyyyMMddHHmmss"));					//하차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   	YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"));		//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  	YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));	//운송지시순번
		    	recInTemp.setField("YD_CARUD_STOP_LOC",	szYD_STK_COL_GP);										//차량하차정지위치
		    	
		    	if("Y".equals(szMOV_YN)){
		    		recInTemp.setField("YD_BAYIN_WO_SEQ",  	"1");						//입동지시순번 - 1순위변경
		    	}else{
		    		recInTemp.setField("YD_BAYIN_WO_SEQ",  	YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);						//입동지시순번 - 기본값으로 설정(9)
		    	}
		    	
				recInTemp.setField("YD_CAR_PROG_STAT", 	"A");													//하차출발상태
				recInTemp.setField("YD_CAR_WRK_GP", 	sWORK_GP);
				recInTemp.setField("YD_PNT_CD3", 		szYD_PNT_CD);
				recInTemp.setField("TRANS_EQUIPMENT_TYPE", 		"P");
				
				if(sCAR_KIND.equals("TT")){
					recInTemp.setField("CAR_KIND",          "TT");									//차량종류
				}else{
					recInTemp.setField("CAR_KIND",          "TR");									//차량종류
				}
				
				recInTemp.setField("TEL_NO",   		YmCommonUtil.paraRecChkNull(inRecord,"TEL_NO"));						//연락처
				recInTemp.setField("DRIVER_NAME",  	YmCommonUtil.paraRecChkNull(inRecord,"DRIVER_NAME"));						//운전기사명
	    		
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szMethodName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	 
					throw new JDTOException(szMsg);
	    		}
	    		  
	    		int intYD_EQP_WRK_SH = YmCommonUtil.paraRecChkNullInt(inRecord, "YD_EQP_WRK_SH");
	    		
	    		szMsg="[" + szMethodName + "] 차량스케줄 상차 수량: [ " + YmCommonUtil.paraRecChkNull(inRecord, "YD_EQP_WRK_SH") + " ]";
	    		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
	    		//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
				for(i = 1 ; i<=intYD_EQP_WRK_SH; i++){
					szGDS_CARLD_LOC = YmCommonUtil.paraRecChkNull(inRecord,"GDS_CARLD_LOC"+i);
					
					if(szGDS_CARLD_LOC.substring(0 , 1).equals("A")){
						szYD_STK_BED_NO = "0"+szGDS_CARLD_LOC.substring(1 , 2);
					}else if(szGDS_CARLD_LOC.substring(0 , 1).equals("B")){
						if(szGDS_CARLD_LOC.substring(1 , 2).equals("1")){
							szYD_STK_BED_NO="06";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("2")){
							szYD_STK_BED_NO="07";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("3")){
							szYD_STK_BED_NO="08";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("4")){
							szYD_STK_BED_NO="09";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("5")){
							szYD_STK_BED_NO="10";
						}
					}else if(szGDS_CARLD_LOC.substring(0 , 1).equals("C")){
						if(szGDS_CARLD_LOC.substring(1 , 2).equals("1")){
							szYD_STK_BED_NO="11";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("2")){
							szYD_STK_BED_NO="12";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("3")){
							szYD_STK_BED_NO="13";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("4")){
							szYD_STK_BED_NO="14";
						}else if(szGDS_CARLD_LOC.substring(1 , 2).equals("5")){
							szYD_STK_BED_NO="15";
						}
					} 
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
					recInTemp.setField("REGISTER",         		szRcvTcCode);
					recInTemp.setField("STL_NO", 				YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"+i));	 
					recInTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO); //야드 차상위치코드
					recInTemp.setField("YD_STK_LYR_NO", 		"001");
					recInTemp.setField("DEL_YN"					, "N");
			 
					intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(recInTemp);
		    		if(intRtnVal != 1) {
		    			szMsg="[" + szMethodName + "] 차량스케줄제료 생성 시 오류발생[반환값 : " + intRtnVal + "]";
		    			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		    			throw new JDTOException(szMsg);
		    		}


				} //end of for *******************************************************************************************
				
	    		
	    		if(sCAR_KIND.equals("TT")){
		    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			        EJBConnector ejbConn3 = new EJBConnector("default","JNDITsInfoReg",this);
					ejbConn3.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
				  	             new Object[]{"C","","",szYD_STK_COL_GP,"","","R"});
	    		}
			}else {
				szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 안합니다..1]";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				throw new JDTOException(szMsg);
			}
    		//////////////////////////////////////////////////////////////////////////////////////////
			
			
			//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
			if(sCAR_KIND.equals("T") || sCAR_KIND.equals("TR")){
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] -AB 차량입동지시요구 모듈을 호출 시작1";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				recInTemp = JDTORecordFactory.getInstance().create();			 
				recInTemp.setField("YD_CARPNT_CD"			,YmCommonUtil.paraRecChkNull(inRecord,"CARUD_PNT_CD"));
				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
				
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				

				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] -AB 차량입동지시요구 모듈을 호출 성공1";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			}
			//////////////////////////////////////////////////////////////////////////////////////////

			
			}
			
		}catch(Exception e){

			szMsg="[AB코일이송하차대기장도착PDA] Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);

		} // end of try-catch

		szMsg="AB코일이송하차대기장도착PDA 처리("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


	} // end of procCoilGdsTrnOrdUdPDAAB()
	
	
	
	/**
	 * AB코일이송하차도착PDA(DMYDR074)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public boolean procStandByYdArriveUdPDAAB(JDTORecord inRecord)throws JDTOException  {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String szMethodName = "procStandByYdArriveUdPDAAB";
		String szMsg = "";
		String sQueryId= "";
		String szYD_STK_COL_GP="";
		String szSTOCK_MOVE_TERM="";
		
		YdCarSchDao ydCarSchDao	= new YdCarSchDao();
		YdStockDao ydStockDao = new YdStockDao();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		int intRtnVal = 0;
		boolean isTrue 			= false;
		JDTORecord recStockColumn				= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara						= JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp					= JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult1 				= null;
		
		//전문받아서 szRcvTcCode에 대입
		String szRcvTcCode=YmCommonUtil.getTcCode(inRecord);

		//수신한 전문이 null이라면 error
		if(szRcvTcCode==null){

			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return false;
		}
		try{
			
			String sTRANS_ORD_DT 		= StringHelper.evl(inRecord.getFieldString("TRANS_ORD_DT"),""); //운송실적일자
			String sTRANS_ORD_SEQNO 	= StringHelper.evl(inRecord.getFieldString("TRANS_ORD_SEQNO"),""); //운송실적순번
		
			
  
			recStockColumn.setField("CARD_NO", YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"));		
			recStockColumn.setField("MODIFIER", szRcvTcCode);
						
  
			//운송실적번호로 제품번호 가져오기
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTransStockInfo";				
			JDTORecord 	jtR = dao.getCommonInfo(sQueryId,new Object[]{sTRANS_ORD_DT+sTRANS_ORD_SEQNO});
			
			if(jtR == null){
				szMsg="[코일제품상차지시등록] 운송실적번호에 맞는 제품번호가 존재 안함:";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
		
			String szYD_CAR_SCH_ID = StringHelper.evl(jtR.getFieldString("YD_CAR_SCH_ID"),"");
			
    		
    		//3. 카드번호로 운송지시가 존재 하는 경우 -------------------------------------------------------
    		String card_No = StringHelper.evl(recStockColumn.getFieldString("CARD_NO"),"");
    		
    		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTransStockCardInfo";				
			JDTORecord 	jtR2 = dao.getCommonInfo(sQueryId,new Object[]{card_No});
			if (jtR2 == null) {
				logger.println(LogLevel.DEBUG, this, "카드번호로 운송 지시가 존재 안 함");
			}else {
				sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock10";
				int  count = dao.updateData(sQueryId,new Object[]{card_No });
				
				logger.println(LogLevel.DEBUG, this, "이전카드번호 운송지시 대상 카드번호 취소처리:"+card_No+" "+count);
			}
			//---------------------------------------------------------------------------------------
			
			
			rVal = YmCommonUtil.getCoilCurrProgCd(jtR.getFieldString("STL_NO"),szRcvTcCode);
			szSTOCK_MOVE_TERM = "CS";
			recStockColumn.setField("TRANS_ORD_DT", sTRANS_ORD_DT); //운송실적일자
			recStockColumn.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO); //운송실적순번
			recStockColumn.setField("STOCK_MOVE_TERM", szSTOCK_MOVE_TERM);
			
			intRtnVal = ydStockDao.updYdStock4(recStockColumn, 0);			

			if(intRtnVal >0){
				szMsg = "수신한 재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재함";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);

			}else if(intRtnVal == 0){
				szMsg = "수신한  재료번호 ["+recStockColumn.getFieldString("STOCK_ID")+"]에 대한 저장품 DATA가 존재하지 않음";
				YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return false;
			}
			
			
			
			String sWORK_GP =StringHelper.evl(inRecord.getFieldString("WORK_GP"), "");
			String sCAR_KIND =StringHelper.evl(inRecord.getFieldString("CAR_KIND"), "");
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
				//########################################## 9:HYSCO스케줄 ####################################################

				
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CARPNT_CD"		,StringHelper.evl(inRecord.getFieldString("CARUD_PNT_CD"), ""));
				
				//포인트코드 -> 개소코드와 저장위치 가져오기
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
				if(intRtnVal > 0){
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szYD_STK_COL_GP    = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP2"), "");
					
					
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다 작업예약 생성 후 스케줄 기동..]";
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					
					
					sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYmStacklayer";
					int  count = dao.updateData(sQueryId,new Object[]{szYD_STK_COL_GP , szYD_CAR_SCH_ID });
					 
					szMsg= "["+szMethodName+"] TB_YD_STKLYR [차상위치에 저장위치 등록 완료 count:]"+count;
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					
					//차량스케줄에 차량진행상태 수정
			    	szMsg="[" + szMethodName + "] 차량스케줄["+szYD_CAR_SCH_ID+"]에 , 차량진행상태[B] 등록 시작";
			    	YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_CAR_SCH_ID", 	   szYD_CAR_SCH_ID);
			    	recInTemp.setField("YD_CAR_PROG_STAT",     "B");
			    	//하차도착
			    	recInTemp.setField("YD_CARUD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
			    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
			    	if(intRtnVal <= 0){
						szMsg= "["+szMethodName+"]차량스케줄에 차량진행상태 수정 UPDATE Error :: [" + intRtnVal + "]" ;
						YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return  false;
					}
			    	
					
					//작업예약 생성
			    	inRecord.setField("CARUD_GP",     "U");
			    	inRecord.setField("YD_STK_COL_GP",     szYD_STK_COL_GP);
					EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
					isTrue 	=  ((Boolean)ejbCon.trx("hsCyGoodsChulHaSangChaGisiRegistInfo", 
									new Class[]{ JDTORecord.class }, new Object[]{ inRecord })).booleanValue();
					
					//TT , TRAIER
					if(sCAR_KIND.equals("TT")||sCAR_KIND.equals("T")){
						sCAR_KIND="T";
					}else{
						if(sCAR_KIND.equals("TR")){
							sCAR_KIND="R";
						}else{
							sCAR_KIND="N";
						}
					}
					
					
					
					//도착위치가 없을 시 도착처리 skip
					if(!szYD_STK_COL_GP.equals("")){
						//차량도착처리 
						EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
						isTrue 	= ((Boolean)ejbConn.trx("carArrival",new Class[]{String.class, String.class, String.class},new Object[]{sCAR_KIND, card_No, szYD_STK_COL_GP})).booleanValue();
					}
					
					//다음 작업예약 호출을 막기 위한 처리 
					return false;
					
				
				}
				
				//################################################################################################################
			} 
			
			
		}catch(Exception e){

			szMsg="[AB코일이송상차도착PDA] Exception Error:" +e.getMessage();
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);

		} // end of try-catch
		
		szMsg="[AB코일이송상차도착PDA]수신처리 ("+szMethodName+") 완료";
		YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
		return true;
		
	} // end of procStandByYdArriveUdPDAAB()
	
}

