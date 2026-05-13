//YDComScript.java
package com.inisteel.cim.yd.jsp.common;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.crn.CrnSchUtil;

import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;


/**
 * 공통으로 사용할 LOGIC
 *
 * @version 1.0
 * @author 이현성
 * @date 2008-10-21
 *   
 * @description  
 */

public class YDDataUtil {
	
	
	private static String szClassName = YDDataUtil.class.getName();
	private static YdUtils ydUtils = new YdUtils();
	private String szSessionName = getClass().getName();
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	 
	
	public YDDataUtil(){
	}	
	
    /**
     * Object Data Default 값을넣어주는 Function 
     * PO
     * @param  Object , String  
     * @return String 
     * @throws Exception
     */	
	public String setDataDefault (Object sObj, String sDef) throws Exception {
		
			
		if ( sObj ==null || "".equals(sObj.toString()))  
		{			
			return sDef;			
		}
		return sObj.toString();
	} // end of setDataDefault
	
	
	
	/**
	 * 재료 적치시키기전 적치단 정보 Clear 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto (STL_NO, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO ,MODIFIER )
	 * @return
	 * @throws DAOException
	 */
	public String updStkLyrClear(JDTORecord  inDto) throws DAOException {
		
		
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updStkLyrClear";	
		String szRtnValue = YdConstant.RETN_CD_SUCCESS;
		String szOperationName = "재료 적치시키기전 적치단 정보 Clear";
		
		String szStlNo = null;
		String szStkColGp = null;
		String szStkBedNo = null;
		String szStkLyrNo = null;
		String szYdUserId = null;
		
		String szStkPosComp = null;
		String szStkColGpComp = null;
		String szStkBedNoComp = null;
						
		JDTORecord recPara = null;
		JDTORecord logRecord = null;
		JDTORecord recGetRecord = null;
		JDTORecordSet rsCrnSch = null;
		JDTORecordSet rsStkLyr = null;
		
		
		//DAO
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
	
		 
		try {
			
			
			
			/* 모듈을 분리하여 제작한다.
			 * 1. 재료정보로 크레인 스케줄 + 스케줄재료 정보중 권상/권하 위치 정보를 조회한다.
			 * 2. 적치단의 해당재료의 상태가 '권상대기'인 위치정보를 조회한다.
			 * 3. '권상대기'인 적치단과 스케줄의 권상 지시 위치 정보가 같은 경우를 제외한 적치단 정보는 Clear 한다.
			 * 4. 적치단의 해당재료의 상태가 '권하대기'인 위치정보를 조회한다.
			 * 5. '권하대기'인 적치단 과 스케줄의 권하지시 위치 정보가 같은 경우를 제외한 적치단 정보는 Clear 한다.
			 */
			
			
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szStlNo = ydDaoUtils.paraRecChkNull(inDto, "STL_NO"); 
			szStkColGp = ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP");
			szStkBedNo = ydDaoUtils.paraRecChkNull(inDto, "YD_STK_BED_NO");
			szStkLyrNo = ydDaoUtils.paraRecChkNull(inDto, "YD_STK_LYR_NO");
			szYdUserId = ydDaoUtils.paraRecChkNull(inDto, "MODIFIER");
			
			
			ydUtils.displayRecord(szOperationName, inDto);
			
			
			if(szStlNo.equals("")){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 재료정보가 올바르지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				return szMsg;				
			}
			
			if(szStkColGp.equals("")){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열 정보가 올바르지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				return szMsg;				
			}
			
			if(szStkBedNo.equals("")){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드 정보가 올바르지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				return szMsg;				
			}
			
			if(szStkLyrNo.equals("")){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치단 정보가 올바르지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				return szMsg;				
			}
			
			
			
			
			
			//  해당 재료가 포함된 스케줄 정보를 조회합니다.
			
			recPara = JDTORecordFactory.getInstance().create(); //조회 Parameter 초기화
			recPara.setField("STL_NO", szStlNo);			
			rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("rsCrnSch");  // 크레인 스케줄 정보
			
			
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsCrnSch, 52);
			
			
			if (intRtnVal < 0) {
				szMsg = "[Jsp Session  -  " + szOperationName +"]  크레인 스케줄 조회시 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CD_FAILURE;
			}
			
			
			
			/* 
			 *  스케줄 정보 존재 하지 않을 경우		
			 *  해당재료가 적치된 모든 적치단 정보를 Clear 합니다.
			 */
			
			else if(intRtnVal == 0 ){
				
				szMsg = "[Jsp Session  -  " + szOperationName +"]  조회된 스케줄 정보가 없으므로 해당 재료가 포함된 모든 적치단 정보를 Clear합니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara = JDTORecordFactory.getInstance().create(); //조회 Parameter 초기화
				recPara.setField("STL_NO", szStlNo);
				recPara.setField("YD_STK_LYR_MTL_STAT", ""); //적치단 재료상태와 관계없이 해당 재료번호가 된 적치정보 조회
				
				rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("rsStkLyr");  // 적치단 정보
				
				
				intRtnVal =  ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 3);
				
				
				if(intRtnVal < 0 ){
					szMsg = "[Jsp Session  -  " + szOperationName +"]  해당 재료 번호 [" + szStlNo+ "]적치단 조회시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					return YdConstant.RETN_CD_FAILURE;
				}
				
				else if (intRtnVal == 0) {
					szMsg = "[Jsp Session  -  " + szOperationName +"]  해당 재료 번호 [" + szStlNo+ "] 로 조회된 적치단이 없어 Clear 할 정보가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
					return YdConstant.RETN_CD_SUCCESS;
				} else {
					szMsg = "[Jsp Session  -  " + szOperationName +"]  해당 재료 번호 [" + szStlNo+ "] 로 조회된 적치단 정보가 ["  + intRtnVal + "건 존재합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
					rsStkLyr.first();  // 조회된 정보의 가장 앞으로...			
					recGetRecord = JDTORecordFactory.getInstance().create(); //조회 Parameter 초기화
					
					
					do {
						
						recGetRecord = rsStkLyr.getRecord();
						
						
						//자신의 위치랑 같은 정보는 삭제 하지않는다.
						
						if ( (szStkColGp.equals(ydDaoUtils.paraRecChkNull(recGetRecord, "YD_STK_COL_GP"))) &&   
							 (szStkBedNo.equals(ydDaoUtils.paraRecChkNull(recGetRecord, "YD_STK_BED_NO"))) &&
							 (szStkLyrNo.equals(ydDaoUtils.paraRecChkNull(recGetRecord, "YD_STK_LYR_NO"))) )
						{
							szMsg = "[Jsp Session  -  " + szOperationName +"]  자신의 정보는 삭제하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						else{
							
							recGetRecord.setField("STL_NO", "");
							recGetRecord.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK_ABLE);
							recGetRecord.setField("MODIFIER", szYdUserId);
							
							szMsg = "[Jsp Session  -  " + szOperationName +"] 적치단 정보 CLEAR UPDATE";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							
							ydUtils.displayRecord(szOperationName, recGetRecord);
							
							
							intRtnVal = ydStkLyrDao.updYdStklyr(recGetRecord , 0);
							
							if(intRtnVal <  0 ){
								szMsg = "[Jsp Session  -  " + szOperationName +"] 적치단 Clear시 ERROR";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);							
								return YdConstant.RETN_CD_FAILURE;
								
							} else if(intRtnVal == 0){
								szMsg = "[Jsp Session  -  " + szOperationName +"] 적치단 Clear 할 정보가 없습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
								
							} else {
								szMsg = "[Jsp Session  -  " + szOperationName +"] 적치단 Clear UPDATE 성공하였습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								
								logRecord = JDTORecordFactory.getInstance().create();
								logRecord.setField("YD_GP", recGetRecord.getFieldString("YD_STK_COL_GP").substring(0,1));
								logRecord.setField("YD_UP_WR_LOC", recGetRecord.getFieldString("YD_STK_COL_GP") + recGetRecord.getFieldString("YD_STK_BED_NO"));
								
								ydUtils.displayRecord(szOperationName, logRecord);
								ydUtils.putYdFlexCrnWrk("", logRecord);
								
								
							}
							
							
						}
						
						
						
					}while(rsStkLyr.next());	
					
					
					// Clear 작업이 완료
					szMsg = "[Jsp Session  -  " + szOperationName +"] 선택된 모든 적치단정보를  Clear 성공하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					return YdConstant.RETN_CD_SUCCESS;
					
				}			
			}
			
			
			
			
			
			else{
				
				/*
				 * 
				 *  스케줄 정보 존재하는 경우 임.
				 *  
				 *  적치단 정보가 '권상대기' 인 위치 정보를 조회합니다. 
				 *  스케줄 정보의 권상지시 위치 정보 와 적치단 정보에 정보가 '권상대기' 가 다른  모든 적치단 정보는 Clear 해줍니다.
				 *  적치단 정보가 '권하대기' 인 위치정보를 조회합니다.
				 *  스케줄 정보의 권하지시 위치 정보와 적치단 정보의 정보가 권하대기가 다른 모든 적치단 정보는 Clear 합니다.	
				 *  적치단 정보가 적치중인 위치정보를 조회하여 Clear 합니다.
				 *  
				 */
				
				
			    recGetRecord = JDTORecordFactory.getInstance().create(); // 초기화
				
				rsCrnSch.first();				
				recGetRecord = rsCrnSch.getRecord();

  
				szStkPosComp = ydDaoUtils.paraRecChkNull(recGetRecord, "YD_UP_WO_LOC"); // 권상지시 위치 정보
				
				
				
				//권상위치정보가 올바르게 들어있지 않을경우 
				if ( szStkPosComp.trim().equals("") ||  szStkPosComp.length() != 8 ){
					
					szMsg = "[Jsp Session  -  " + szOperationName +"] 권상위치 정보가 올바르지 않습니다. [" + szStkPosComp + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					// 이경우도 클리어를 해야한다면 
					szStkColGpComp = "";
					szStkBedNoComp = "";
					
				}else{
					szStkColGpComp = szStkPosComp.substring(0,6);
					szStkBedNoComp = szStkPosComp.substring(6,8);
					
					
				}
				
				
				// '권상대기' 인 적치단 정보를 조회한다.  -> Clear 
				
				recPara = JDTORecordFactory.getInstance().create(); // 초기화
				
				
				recPara.setField("STL_NO", szStlNo);
				recPara.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT); 
				
				rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("rsStkLyr");  // 적치단 정보
				
				
				intRtnVal =  ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 3);
				
				
				if(intRtnVal < 0 ){
					szMsg = "[Jsp Session  -  " + szOperationName +"]  해당 재료 번호 [" + szStlNo+ "] 권상대기 적치단 조회시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					return YdConstant.RETN_CD_FAILURE;
				}
				
				else if (intRtnVal == 0) {
					szMsg = "[Jsp Session  -  " + szOperationName +"]  해당 재료 번호 [" + szStlNo+ "] 로 권상대기 적치단이 없어 Clear 할 정보가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} else {
					
					
					//선택된 정보의 적치단 정보를 Clear 한다.
					
					rsStkLyr.first();
					
					do{
						
						recPara = JDTORecordFactory.getInstance().create(); // 초기화
						recPara = rsStkLyr.getRecord();
						
						
						// 적치열 구분과 베드 번호가 다른경우는 삭제 한다.
						
						if ( (!szStkColGpComp.equals(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"))) ||  
								(!szStkBedNoComp.equals(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"))) ){
							
							
							
							
							recPara.setField("STL_NO", "");
							recPara.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK_ABLE);
							recPara.setField("MODIFIER", szYdUserId);
							
							
							szMsg = "[Jsp Session  -  " + szOperationName +"] 권상지시 적치단 정보 CLEAR UPDATE";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							
							ydUtils.displayRecord(szOperationName, recPara);
							
							
							intRtnVal = ydStkLyrDao.updYdStklyr(recPara , 0);
							
							if(intRtnVal <  0 ){
								szMsg = "[Jsp Session  -  " + szOperationName +"] 권상지시 적치단 Clear시 ERROR";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);							
								return YdConstant.RETN_CD_FAILURE;
								
							} else if(intRtnVal == 0){
								szMsg = "[Jsp Session  -  " + szOperationName +"] 권상지시 적치단 Clear 할 정보가 없습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
								
							} else {
								szMsg = "[Jsp Session  -  " + szOperationName +"] 권상지시 적치단 Clear UPDATE 성공하였습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
							}					
						}	
						
					}while(rsStkLyr.next());
				}
				
				
					
				
				// 권하대기 적치단 삭제 
				
				szStkPosComp = ydDaoUtils.paraRecChkNull(recGetRecord, "YD_DN_WO_LOC"); // 권상지시 위치 정보
				
				
				
				//권상위치정보가 올바르게 들어있지 않을경우 
				if ( szStkPosComp.trim().equals("") ||  szStkPosComp.length() != 8 ){
					
					szMsg = "[Jsp Session  -  " + szOperationName +"] 권상위치 정보가 올바르지 않습니다. [" + szStkPosComp + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					// 이경우도 클리어를 해야한다면 
					szStkColGpComp = "";
					szStkBedNoComp = "";
					
				}else{
					szStkColGpComp = szStkPosComp.substring(0,6);
					szStkBedNoComp = szStkPosComp.substring(6,8);
					
					
				}
				
				
				// '권하대기' 인 적치단 정보를 조회한다.  -> Clear 
				
				recPara = JDTORecordFactory.getInstance().create(); // 초기화
				
				
				recPara.setField("STL_NO", szStlNo);
				recPara.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT); 
				
				rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("rsStkLyr");  // 적치단 정보
				
				
				intRtnVal =  ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 3);
				
				
				if(intRtnVal < 0 ){
					szMsg = "[Jsp Session  -  " + szOperationName +"]  해당 재료 번호 [" + szStlNo+ "] 권하대기 적치단 조회시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					return YdConstant.RETN_CD_FAILURE;
				}
				
				else if (intRtnVal == 0) {
					szMsg = "[Jsp Session  -  " + szOperationName +"]  해당 재료 번호 [" + szStlNo+ "] 로 권하대기 적치단이 없어 Clear 할 정보가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} else {
					
					
					//선택된 정보의 적치단 정보를 Clear 한다.
					
					rsStkLyr.first();
					
					do{
						
						recPara = JDTORecordFactory.getInstance().create(); // 초기화
						recPara = rsStkLyr.getRecord();
						
						
						// 적치열 구분과 베드 번호가 다른경우는 삭제 한다.
						
						if ( (!szStkColGpComp.equals(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"))) ||   
								(!szStkBedNoComp.equals(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"))) ){
							recPara.setField("STL_NO", "");
							recPara.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK_ABLE);
							recPara.setField("MODIFIER", szYdUserId);
							
							
							szMsg = "[Jsp Session  -  " + szOperationName +"] 권하대기 적치단 정보 CLEAR UPDATE";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							
							ydUtils.displayRecord(szOperationName, recPara);
							
							
							intRtnVal = ydStkLyrDao.updYdStklyr(recPara , 0);
							
							if(intRtnVal <  0 ){
								szMsg = "[Jsp Session  -  " + szOperationName +"] 권하대기 적치단 Clear시 ERROR";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);							
								return YdConstant.RETN_CD_FAILURE;
								
							} else if(intRtnVal == 0){
								szMsg = "[Jsp Session  -  " + szOperationName +"] 권하대기 적치단 Clear 할 정보가 없습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
								
							} else {
								szMsg = "[Jsp Session  -  " + szOperationName +"] 권하대기 적치단 Clear UPDATE 성공하였습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
							}					
						}	
						
					}while(rsStkLyr.next());
				}
				
				
							
				
				// 적치중 적치단 정보 Clear
				
				// '적치중' 인 적치단 정보를 조회한다.  -> Clear 
				
				recPara = JDTORecordFactory.getInstance().create(); // 초기화
				
				
				recPara.setField("STL_NO", szStlNo);
				recPara.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK); 
				
				rsStkLyr = JDTORecordFactory.getInstance().createRecordSet("rsStkLyr");  // 적치단 정보
				
				
				intRtnVal =  ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 3);
				
				
				if(intRtnVal < 0 ){
					szMsg = "[Jsp Session  -  " + szOperationName +"]  해당 재료 번호 [" + szStlNo+ "] 적치중 적치단 조회시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					return YdConstant.RETN_CD_FAILURE;
				}
				
				else if (intRtnVal == 0) {
					szMsg = "[Jsp Session  -  " + szOperationName +"]  해당 재료 번호 [" + szStlNo+ "] 로 적치중 적치단이 없어 Clear 할 정보가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				} else {
					
					
					//선택된 정보의 적치단 정보를 Clear 한다.
					
					rsStkLyr.first();
					
					do{
						
						recPara = JDTORecordFactory.getInstance().create(); // 초기화
						recPara = rsStkLyr.getRecord();
						
						
						// 적치열 구분과 베드 번호가 다른경우는 삭제 한다.
						
						//자신의 위치랑 같은 정보는 삭제 하지않는다.
						
						if ( (szStkColGp.equals(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"))) &&   
							 (szStkBedNo.equals(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"))) &&
							 (szStkLyrNo.equals(ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO")))
								
						){
							szMsg = "[Jsp Session  -  " + szOperationName +"] 자신의 위치는 삭제하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
						}
						else
						{
							recPara.setField("STL_NO", "");
							recPara.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK_ABLE);
							recPara.setField("MODIFIER", szYdUserId);
							
							
							szMsg = "[Jsp Session  -  " + szOperationName +"] 적치중 적치단 정보 CLEAR UPDATE";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							
							ydUtils.displayRecord(szOperationName, recPara);
							
							
							intRtnVal = ydStkLyrDao.updYdStklyr(recPara , 0);
							
							if(intRtnVal <  0 ){
								szMsg = "[Jsp Session  -  " + szOperationName +"] 적치중 적치단 Clear시 ERROR";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);							
								return YdConstant.RETN_CD_FAILURE;
								
							} else if(intRtnVal == 0){
								szMsg = "[Jsp Session  -  " + szOperationName +"] 적치중 적치단 Clear 할 정보가 없습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
								
							} else {
								szMsg = "[Jsp Session  -  " + szOperationName +"] 적치중 적치단 Clear UPDATE 성공하였습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
							}			
						}	
						
					}while(rsStkLyr.next());
				}
				
			}
			
		
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szRtnValue = YdConstant.RETN_CD_SUCCESS;
			return szRtnValue;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
	}	// end of updStkLyrClear	
	
	
	
	
	/**
	 * 이송완료 실적처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String STL_NO
	 * @return 성공 또는 보낼필요가 없을경우: YdConstant.RETN_CD_SUCCESS , 실패 : YdConstant.RETN_CD_FAILURE
	 * @throws DAOException
	 */
	public String sendYDPRJ003(String pzStlNo) throws DAOException {
		
		/*
		 * 1.입고시에 해당재료정보로 차량스케줄 정보를 조회하여 
		 *   발지개소가 후판공장일경우 해당 TC 를 보내기 위함 
		 * 
		 *  후판공장 발지개소 : YdConstant.WLOC_CD_A_PLATE_PLANT
		 * 
		 */
		JDTORecord recPara = null;
		
		JDTORecordSet rsCarSch = null;

		String szMethodName		= "sendYDPRJ003";	
		String szRtnValue 		= YdConstant.RETN_CD_SUCCESS;
		String szOperationName 	= "이송완료 실적처리";
		String szMsg 			= null;
		
		int intRtnVal = 0;
		
		//DAO
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		 
		try {
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara = JDTORecordFactory.getInstance().create(); // 초기화		
			recPara.setField("STL_NO", pzStlNo);
			
			rsCarSch = JDTORecordFactory.getInstance().createRecordSet("rsCarSch");  // 적치단 정보
			
			// 1. 해당 재료로 차량 스케줄 정보를 조회한다.
			szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 스케줄/재료 정보 조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsCarSch, 35);
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 스케줄/재료 정보 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
				
			}else if(intRtnVal == 0){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 스케줄/재료 정보 조회된 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return  YdConstant.RETN_CD_SUCCESS;
			}
			
			recPara = JDTORecordFactory.getInstance().create(); // 초기화
			rsCarSch.first();			
			recPara = rsCarSch.getRecord();
			
			// 2. 차량 스케줄 정보에서 발지개소코드가 후판공장인지 체크한다.			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 발지개소코드 체크";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(ydDaoUtils.paraRecChkNull(recPara, "SPOS_WLOC_CD").equals(YdConstant.WLOC_CD_A_PLATE_PLANT)){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 발지개소코드가 후판공장이므로 전문 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "[Jsp Session  -  " + szOperationName +"] 발지개소코드가 후판공장이 아니므로 전문을 전송할 필요가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return  YdConstant.RETN_CD_SUCCESS;
			}
						
			// 3. 해당 전문을 전송한다.			
			szMsg = "[Jsp Session  -  " + szOperationName +"] YDPRJ003 전문전송";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recPara = JDTORecordFactory.getInstance().create(); // 초기화
			recPara.setField("JMS_TC_CD", "YDPRJ003");
			recPara.setField("STL_NO", pzStlNo);
			
			YdDelegate ydDelegate = new YdDelegate();
			ydDelegate.sendMsg(recPara);
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
		
		return szRtnValue;
	}	// end of sendYDPRJ003	
	
	
	
	
	/**
	 * 오퍼레이션명 : 차량 또는 대차 작업예약 ID 삭제 Module (작업취소시 모듈임 Simple하게 수정할것!!)
	 *
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (JDTORecord : YD_WBOOK_ID , MODIFIER)
	 * @return
	 * @throws JDTOException
	 */
	public String delWBookBefoCarOrTCar(JDTORecord msgRecord)throws JDTOException  {
		
		/*
		 *  작성자 : 이현성
		 *  요약 : 작업예약 삭제 전에 해당 작업에 물려 있는 차량스케줄 또는 대차 스케줄 작업예약을 
		 *  	   Clear 하기 위함  
		 *  
		 *  1. 취소할 작업예약 ID를 받는다.
		 *  2. 작업 예약 ID로 스케줄 코드를 조회한다.
		 *  3. 해당 작업예약 스케줄이 차량 대차 작업 일경우 상하차 작업을 확인하여 해당 스케줄의
		 *     상하차 작업 예약 ID를 Clear 한다. 
		 * 
		 * 
		 */
		
		
		//대차 , 차량스케줄 DAO		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		YdWrkbookDao ydWrkbookDao= new YdWrkbookDao();
		YdStkColDao YdStkColDao = new YdStkColDao();

		JDTORecord inRec = JDTORecordFactory.getInstance().create();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		
		//작업예약   레코드셋 생성
		JDTORecordSet rsYdWBook =null;
		
		
		//파라미터 스크링 변수
		String szOperationName = "차량/대차 작업예약 ID 삭제 Module";
		String szMethodName="delWBookBefoCarOrTCar";

		
		String szSchCd = null;
		String szCarGp = null;
		String szULGp = null;
		String szYdWBookId = null;
		String szYD_EQP_GP ="";
		String szTrneqpCd ="";
		//리턴값
		int intRtnVal = 0;
		
		//체크 값
		
		String szMsg="";

		
		 
		try {
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
	
			/*
			 *  입력받은 인자 Check
			 */
			
			ydUtils.displayRecord(szOperationName, msgRecord);		
			szYdWBookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
			szYD_EQP_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP");
			
			if(szYdWBookId.trim().equals("")){
				
				szMsg = "[Jsp Session  -  " + szOperationName +"] 작업예약 ID가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CD_EXIST;
				
			}
			
			
			/*
			 * 작업예약 ID정보로 작업예약 정보를 조회하여 스케줄 코드를 얻는다.
			 */
			
			rsYdWBook = JDTORecordFactory.getInstance().createRecordSet("rsYdWBook");
			
			intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsYdWBook, 0);
			
			
			if(intRtnVal < 0 ){
	
				szMsg = "[Jsp Session  -  " + szOperationName +"] 작업예약 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CD_FAILURE;
				
			}
			
			else if(intRtnVal == 0){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 작업예약 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				return YdConstant.RETN_CD_EXIST;
				
			}
			
			rsYdWBook.first();
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara = rsYdWBook.getRecord();
			
			
			
			szSchCd = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD"); 
			szTrneqpCd = ydDaoUtils.paraRecChkNull(recPara, "TRN_EQP_CD"); 
			
			
			if ( "".equals(szSchCd) || (szSchCd.length() != 8)){
				
				szMsg = "[Jsp Session  -  " + szOperationName +"] 스케줄 코드가 올바르지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				
				return YdConstant.RETN_CD_FAILURE;
				
				
			}else{
				
				
				szULGp = szSchCd.substring(6,7);
				
				//설비 구분을 입력 안 받은 경우 작업예약 ID로 스케줄 코드를 찾아 처리 함.
				if(szYD_EQP_GP.equals(null) ||szYD_EQP_GP.equals("") ){
					szCarGp = szSchCd.substring(2,4);
				}else{
					szCarGp = szYD_EQP_GP ;
				}
				
				inRec    = JDTORecordFactory.getInstance().create();
				
				//스케줄 코드가 차량/대차 인경우 구분
				
				if(szCarGp.equals(YdConstant.YD_EQP_GP_TCAR)){
	 
					
					if(szULGp.equals(YdConstant.YD_CRN_SCH_CD_UD)){
						
						//상차 인경우  작업예약 정보 삭제
						
						szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 상차  예약정보 삭제 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						
						inRec.setField("MODIFIER", ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"));
						inRec.setField("YD_CARLD_WRK_BOOK_ID", szYdWBookId);
						              
						intRtnVal = ydTcarSchDao.updYdTCarschDir(inRec, 0);
						
						if(intRtnVal <0 ){
							
							szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 상차작업 삭제시 ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
							
						}
						
						else if(intRtnVal ==0 ){
							szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 상차할 작업이 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
							
							
							
						}else{
													
							szMsg = "[Jsp Session  -  " + szOperationName +"] 대차스케줄의 상차 작업예약 ID 삭제하였습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							
						}
						
						
						
					}else if(szULGp.equals(YdConstant.YD_CRN_SCH_CD_LD)){
						//하차인경우 작업예약 정보 삭제
	
						szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 하차  예약정보 삭제 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						inRec.setField("MODIFIER", ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"));
						inRec.setField("YD_CARUD_WRK_BOOK_ID", szYdWBookId);
						intRtnVal = ydTcarSchDao.updYdTCarschDir(inRec, 1);
						
						
						if(intRtnVal <0 ){
							
							szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업 하차작업 삭제시 ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
							
						}
						
						else if(intRtnVal ==0 ){
							szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 작업하차할 작업이 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
							
						}else{
													
							szMsg = "[Jsp Session  -  " + szOperationName +"] 대차스케줄의 하차 작업예약 ID 삭제하였습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
					}
					
				}else if(szCarGp.equals(YdConstant.YD_EQP_GP_PALLET)|| szCarGp.equals(YdConstant.YD_EQP_GP_TRAILER)){
					// 차량인 경우 		
					if(szULGp.equals(YdConstant.YD_CRN_SCH_CD_UD)){
						
						szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 작업 상차  예약정보 삭제 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						
						inRec.setField("MODIFIER", ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"));
						inRec.setField("YD_CARLD_WRK_BOOK_ID", szYdWBookId);
						intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(inRec, 4);
						
						
						if(intRtnVal <0 ){
							szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 작업 상차작업 삭제시 ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
							
						}
						
						else if(intRtnVal ==0 ){
							szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 작업 상차할 작업이 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
							
						}else{
							szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 상차 작업예약 ID 삭제하였습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						//c연주 목표동 초기화 작업
						inRec    = JDTORecordFactory.getInstance().create();
						inRec.setField("MODIFIER", ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"));
						inRec.setField("TRN_EQP_CD", szTrneqpCd);
						intRtnVal = YdStkColDao.updYdStkcolTrneqpCd(inRec,0);
						
						
						
					}else if(szULGp.equals(YdConstant.YD_CRN_SCH_CD_LD)){
						//하차인경우 작업예약 정보 삭제
						
						szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 작업 하차  예약정보 삭제 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						
						inRec.setField("MODIFIER", ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"));
						inRec.setField("YD_CARUD_WRK_BOOK_ID", szYdWBookId);
						intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(inRec, 5);
						
						
						
						if(intRtnVal <0 ){
							
							szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 작업 하차작업 삭제시 ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
							
						}
						
						else if(intRtnVal == 0 ){
							szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 작업 하차할 작업이 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
							
							
							
						}else{
													
							szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 하차 작업예약 ID 삭제하였습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
						}
						
					}
				} else{
					szMsg = "[Jsp Session  -  " + szOperationName +"] 대차 / 차량 작업이므로 삭제할 필요가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
		
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
		
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 외경군그룹(열정보) 변경
	 *
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (JDTORecord : YD_WBOOK_ID , MODIFIER)
	 * @return
	 * @throws JDTOException
	 */
	public String updYdCoilOutdiaGrpGpCol(JDTORecord msgRecord)throws JDTOException  {
		
		/*
		 * # 업무기준 
		 * 
		 * 체크사항
		 *  - 적치열 정보가 6자리
		 *  - 변경정보에 ""이 아닌값
		 *  - 수정자 (없을경우 'updYdCoil')
		 * 
		 * 1. 기존 열 정보에 있는 외경군그룹 정보와 변경할 정보를 비교한다.
		 * 	1.1 기존정보와 변경정보가 다를경우에 처리한다.
		 *  
		 * 2. 변경정보로 적치열 테이블을 UPDATE 한다.
		 * 
		 * 3. 변경정보로 해당 적치열에 포함된 적치베드 테이블을 UPDATE 한다. 
		 */
		

		JDTORecord inRec = JDTORecordFactory.getInstance().create();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		String szStkColGp = null;
		String szCoilOutdiaGp = null;
		String szCoilOutdiaGpBefo = null;
		
		String szOperationName = "외경군그룹(열정보) 변경";
		String szMethodName="updYdCoilOutdiaGrpGpCol";
		String szMsg="";
		
		JDTORecordSet rsStkCol =null;
		
		//리턴값
		int intRtnVal = 0;
		
		String szYdUsrId = null;
		
		//DAO
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao YdStkLyrDao = new YdStkLyrDao();
		

		
		 
		try {
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szStkColGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			
			
			if(szStkColGp.length() != 6 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 적치열정보가 올바르지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
				
			}
			
			szCoilOutdiaGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_COIL_OUTDIA_GRP_GP");
			
			if(szCoilOutdiaGp.equals("")){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  외경군그룹 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_SUCCESS;
				
			}
			
			
			szYdUsrId = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			
			if(szYdUsrId.equals("")){
				szYdUsrId = "updYdCoil";
				
			}
			
			
			//-------------------------------------------------------------
			// 기존 해당 적치열에대한 외경군그룹 정보 조회
			//-------------------------------------------------------------
			
			recPara = JDTORecordFactory.getInstance().create();
			inRec   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szStkColGp);
			
			
			rsStkCol = JDTORecordFactory.getInstance().createRecordSet("rsStkCol");
			
			
			intRtnVal = ydStkColDao.getYdStkcol(recPara, rsStkCol, 0);
			
			
			if( intRtnVal < 0) {
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열정보 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
				
			}else if(intRtnVal == 0){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열정보 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szMsg;
			}
			
			
			rsStkCol.first();
			inRec = rsStkCol.getRecord();
			
			szCoilOutdiaGpBefo = ydDaoUtils.paraRecChkNull(inRec, "YD_COIL_OUTDIA_GRP_GP");
			//-------------------------------------------------------------
			//  외경군그룹 정보 비교
			//-------------------------------------------------------------
			
			if(szCoilOutdiaGpBefo.equals(szCoilOutdiaGp)){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  외경군 그룹의 변경정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_SUCCESS;
				
			}
			
			//-------------------------------------------------------------
			// 적치열 정보의 외경군그룹 UPDATE
			//-------------------------------------------------------------
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szStkColGp);
			recPara.setField("YD_COIL_OUTDIA_GRP_GP", szCoilOutdiaGp);
			recPara.setField("MODIFIER", szYdUsrId);
			
			ydUtils.displayRecord(szOperationName, recPara);
			intRtnVal =  ydStkColDao.updYdStkcol(recPara, 0);

			if(intRtnVal <0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열 정보의 외경군그룹 UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			} else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열 외경군 그룹의 변경 할 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열 외경군 그룹의 변경 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			//-------------------------------------------------------------
			// 적치 베드 정보의 외경군그룹 UPDATE
			//-------------------------------------------------------------

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szStkColGp);
			recPara.setField("YD_COIL_OUTDIA_GRP_GP", szCoilOutdiaGp);
			recPara.setField("MODIFIER", szYdUsrId);
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdCoilOutdiaGrpGp*/
			intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recPara, 5);
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드 정보의 외경군그룹 UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			}else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드 외경군 그룹의 변경 할 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열 외경군 그룹의 변경 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			//-------------------------------------------------------------
			// 적치 베드 정보의 외경군그룹 UPDATE  --> TB_YD_STKLYR
			//-------------------------------------------------------------
			
			
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szStkColGp);
			recPara.setField("YD_COIL_OUTDIA_GRP_GP", szCoilOutdiaGp);
			recPara.setField("MODIFIER", szYdUsrId);
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrACT_STAT*/
			intRtnVal = YdStkLyrDao.updYdStklyrACT_STAT(recPara);
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  TB_YD_STKLYR 적치베드 정보의 외경군그룹 UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			}else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  TB_YD_STKLYR 적치베드 외경군 그룹의 변경 할 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  TB_YD_STKLYR 적치열 외경군 그룹의 변경 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
		
		
		return YdConstant.RETN_CD_SUCCESS;
	}


	
	
	
	
	/**
	 * 오퍼레이션명 : 폭구분(열정보) 변경
	 *
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (JDTORecord : YD_WBOOK_ID , MODIFIER)
	 * @return
	 * @throws JDTOException
	 */
	public String updYdStkbedYdStkBedWGp(JDTORecord msgRecord)throws JDTOException  {
		
		/*
		 * # 업무기준 
		 * 
		 * 체크사항
		 *  - 적치열 정보가 6자리
		 *  - 변경정보에 ""이 아닌값
		 *  - 수정자 (없을경우 'updYdCoil')
		 * 
		 * 1. 기존 열 정보에 있는 외경군그룹 정보와 변경할 정보를 비교한다.
		 * 	1.1 기존정보와 변경정보가 다를경우에 처리한다.
		 *  
		 * 2. 변경정보로 적치열 테이블을 UPDATE 한다.
		 * 
		 * 3. 변경정보로 해당 적치열에 포함된 적치베드 테이블을 UPDATE 한다. 
		 */
		

		JDTORecord inRec = JDTORecordFactory.getInstance().create();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
	
		String szOperationName = "폭구분(열정보) 변경";
		String szMethodName="updYdStkbedYdStkBedWGp";
		String szMsg="";
		
		JDTORecordSet rsStkCol =null;
		
		//리턴값
		int intRtnVal = 0;
		
		String szYdUsrId = null;
		String szStkColGp = null;
		String szYdStkbedYdStkBedWGp = null;
		String szYdStkbedYdStkBedWGpBefo = null;
		
		
		//DAO
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		

		
		 
		try {
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szStkColGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			
			
			if(szStkColGp.length() != 6 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 적치열정보가 올바르지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
				
			}
			
			szYdStkbedYdStkBedWGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_W_GP");
			
			if(szYdStkbedYdStkBedWGp.equals("")){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  폭그룹 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_SUCCESS;
				
			}
			
			
			szYdUsrId = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			
			if(szYdUsrId.equals("")){
				szYdUsrId = "updYdCoil";
				
			}
			
			
			//-------------------------------------------------------------
			// 기존 해당 적치열에대한 외경군그룹 정보 조회
			//-------------------------------------------------------------
			
			recPara = JDTORecordFactory.getInstance().create();
			inRec   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szStkColGp);
			
			
			rsStkCol = JDTORecordFactory.getInstance().createRecordSet("rsStkCol");
			
			
			intRtnVal = ydStkColDao.getYdStkcol(recPara, rsStkCol, 0);
			
			
			if( intRtnVal < 0) {
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열정보 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
				
			}else if(intRtnVal == 0){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열정보 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szMsg;
			}
			
			
			rsStkCol.first();
			inRec = rsStkCol.getRecord();
			
			szYdStkbedYdStkBedWGpBefo = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_W_GP");
			
			
			
			
			
			//-------------------------------------------------------------
			//  외경군그룹 정보 비교
			//-------------------------------------------------------------
			
			
			if(szYdStkbedYdStkBedWGpBefo.equals(szYdStkbedYdStkBedWGp)){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 폭그룹 의 변경정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_SUCCESS;
				
			}

			
			
			//-------------------------------------------------------------
			// 적치열 정보의 폭 그룹 UPDATE
			//-------------------------------------------------------------
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szStkColGp);
			recPara.setField("YD_STK_COL_W_GP", szYdStkbedYdStkBedWGp);
			recPara.setField("MODIFIER", szYdUsrId);
			
			ydUtils.displayRecord(szOperationName, recPara);
			
			intRtnVal =  ydStkColDao.updYdStkcol(recPara, 0);

			
			if(intRtnVal <0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열 정보의 외경군그룹 UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			} else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열 외경군 그룹의 변경 할 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열 폭 그룹의 변경 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			//-------------------------------------------------------------
			// 적치 베드 정보의 폭 그룹 UPDATE
			//-------------------------------------------------------------

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szStkColGp);
			recPara.setField("YD_STK_BED_W_GP", szYdStkbedYdStkBedWGp);
			recPara.setField("MODIFIER", szYdUsrId);
			
			intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recPara, 6);
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드 정보의 외경군그룹 UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			}else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드 외경군 그룹의 변경 할 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  적치열 폭 그룹의 변경 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
		
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 저장집합코드변경
	 *
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (JDTORecord : YD_WBOOK_ID , MODIFIER)
	 * @return
	 * @throws JDTOException
	 */
	public String updYdStkbedStrGtrCd(JDTORecord msgRecord)throws JDTOException  {
		
		/*
		 * # 업무기준 
		 * 
		 * 체크사항
		 *  - 저장집합코드 정보가 6자리
		 *  - 변경정보에 ""이 아닌값
		 *  - 수정자 (없을경우 'updYdCoil')
		 * 
		 * 1. 기존 첫번째 베드에 있는 저장집합코드와  정보와 변경할 정보를 비교한다.
		 * 	1.1 기존정보와 변경정보가 다를경우에 처리한다.
		 *  
		 * 2. 변경정보로 해당 적치열에 포함된 적치베드 테이블을 UPDATE 한다. 
		 */
		

		JDTORecord inRec = JDTORecordFactory.getInstance().create();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
	
		String szOperationName = "저장집합코드변경";
		String szMethodName="updYdStkbedStrGtrCd";
		String szMsg="";
		
		JDTORecordSet rsStkBed=null;
		
		//리턴값
		int intRtnVal = 0;
		
		String szYdUsrId = null;
		String szStkColGp = null;
		String szStrGtrCd = null;
		String szStrGtrCdBefo = null;
		
		
		//DAO
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		
		 
		try {
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szStkColGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			
			
			if(szStkColGp.length() != 6 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 적치열정보가 올바르지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
				
			}
			
			szStrGtrCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STR_GTR_CD");
			
			if(szStrGtrCd.equals("")){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  저장집합코드 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_SUCCESS;
				
			}
			
			
			szYdUsrId = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			
			if(szYdUsrId.equals("")){
				szYdUsrId = "updYdCoil";
				
			}
			
			
			//-------------------------------------------------------------
			// 기존 해당 적치열에대한 외경군그룹 정보 조회
			//-------------------------------------------------------------
			
			recPara = JDTORecordFactory.getInstance().create();
			inRec   = JDTORecordFactory.getInstance().create();
			
			
			recPara.setField("YD_STK_COL_GP", szStkColGp);
			recPara.setField("YD_STK_BED_NO", "01");
			
			
			rsStkBed = JDTORecordFactory.getInstance().createRecordSet("rsStkBed");
			
			
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, rsStkBed, 0);
			
			
			if( intRtnVal < 0) {
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드정보 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
				
			}else if(intRtnVal == 0){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szMsg;
			}
			
			
			rsStkBed.first();
			inRec = rsStkBed.getRecord();
			
			szStrGtrCdBefo = ydDaoUtils.paraRecChkNull(inRec, "YD_STR_GTR_CD");
			
			
			
			
			
			//-------------------------------------------------------------
			//  적치 베드 정보의 저장집합코드 비교
			//-------------------------------------------------------------
			
			
			if(szStrGtrCdBefo.equals(szStrGtrCd)){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 저장집합코드 변경정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_SUCCESS;
				
			}

			
			//-------------------------------------------------------------
			// 적치 베드 정보의 저장집합코드 변경
			//-------------------------------------------------------------

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szStkColGp);
			recPara.setField("YD_STR_GTR_CD", szStrGtrCd);
			recPara.setField("MODIFIER", szYdUsrId);
			
			intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recPara, 7);
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드 정보의 저장집합코드  UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			}else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드 저장집합코드 의 변경 할 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  적치베드 저장집합코 변경 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
		
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	
	/**
	 * 오퍼레이션명 : 야드적치단활성상태 변경
	 *
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (JDTORecord : YD_WBOOK_ID , MODIFIER)
	 * @return
	 * @throws JDTOException
	 */
	public String updYdCoilLyeActStatCol(JDTORecord msgRecord)throws JDTOException  {

	//	TB_YD_STKCOL : YD_STK_COL_ACT_STAT	11		Y	VARCHAR2 (1)		야드적치열활성상태
	//  TB_YD_STKBED : YD_STK_BED_ACT_STAT	13		Y	VARCHAR2 (1)		야드적치Bed활성상태
	//  TB_YD_STKLYR : YD_STK_LYR_ACT_STAT	10		Y	VARCHAR2 (1)		야드적치단활성상태

		
		JDTORecord inRec = JDTORecordFactory.getInstance().create();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		String sYD_STK_COL_ACT_STAT = null;
		String sYD_STK_COL_ACT_STAT_DB = null;
		
		String szOperationName = "야드적치단활성상태 변경";
		String szMethodName="updYdCoilLyeActStatCol";
		String szMsg="";
		
		JDTORecordSet rsStkCol =null;
		
		//리턴값
		int intRtnVal = 0;
		
		String szYdUsrId = null;
		
		//DAO
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		
		 
		try {
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			sYD_STK_COL_ACT_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_ACT_STAT");
			
			
			if(sYD_STK_COL_ACT_STAT.length() != 1 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 야드적치단활성상태  올바르지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			}
			
			szYdUsrId = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			if(szYdUsrId.equals("")){
				szYdUsrId = "updYdCoil";
			}
			
			//-------------------------------------------------------------
			// 기존 해당 적치열에대한야드적치단활성상태 정보 조회
			//-------------------------------------------------------------
			
			recPara = JDTORecordFactory.getInstance().create();
			inRec   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_ACT_STAT", sYD_STK_COL_ACT_STAT);
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP"));
			
			
			rsStkCol = JDTORecordFactory.getInstance().createRecordSet("rsStkCol");
			
			intRtnVal = ydStkColDao.getYdStkcol(recPara, rsStkCol, 0);

			if( intRtnVal < 0) {
				szMsg = "[Jsp Session  -  " + szOperationName +"]  야드적치단활성상태 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
				
			}else if(intRtnVal == 0){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  야드적치단활성상태 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szMsg;
			}
			
			
			rsStkCol.first();
			inRec = rsStkCol.getRecord();
			
			sYD_STK_COL_ACT_STAT_DB = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_ACT_STAT");
			//-------------------------------------------------------------
			// 야드적치단활성상태 정보 비교
			//-------------------------------------------------------------
			
			if(sYD_STK_COL_ACT_STAT_DB.equals(sYD_STK_COL_ACT_STAT)){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  야드적치단활성상태의 변경정보되지 않았습니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_SUCCESS;
				
			}
			
//			//-------------------------------------------------------------
//			//  TB_YD_STKCOL : 야드적치단활성상태 UPDATE
//			//-------------------------------------------------------------
//			
//			recPara = JDTORecordFactory.getInstance().create();
//			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP"));
//			recPara.setField("YD_STK_COL_ACT_STAT", sYD_STK_COL_ACT_STAT);
//			recPara.setField("MODIFIER", szYdUsrId);
//			
//			ydUtils.displayRecord(szOperationName, recPara);
//			intRtnVal =  ydStkColDao.updYdStkcol(recPara, 0);
//
//			if(intRtnVal <0 ){
//				szMsg = "[Jsp Session  -  " + szOperationName +"]  열 야드적치단활성상태의 UPDATE ERROR";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				throw new DAOException(szMsg); 
//			} else if(intRtnVal == 0 ){
//				szMsg = "[Jsp Session  -  " + szOperationName +"]  열 야드적치단활성상태의 정보가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//			}
//			
//			
//			szMsg = "[Jsp Session  -  " + szOperationName +"]  열 야드적치단활성상태의의 변경 성공.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			
			
			
			//-------------------------------------------------------------
			//  TB_YD_STKBED 적치 베드 정보의  UPDATE
			//-------------------------------------------------------------

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP"));
			recPara.setField("YD_STK_BED_ACT_STAT", sYD_STK_COL_ACT_STAT);
			recPara.setField("MODIFIER", szYdUsrId);
		
			intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recPara, 300);
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  베드 야드적치단활성상태의 UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			}else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  베드 야드적치단활성상태 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  베드  야드적치단활성상태의 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//-------------------------------------------------------------
			//  TB_YD_STKLYR 적치 베드 정보의  UPDATE
			//  야드적치단활성상태 서로 틀
			//-------------------------------------------------------------
			if(sYD_STK_COL_ACT_STAT.equals("L")){
			   sYD_STK_COL_ACT_STAT = "E";
			}
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP"));
			recPara.setField("YD_STK_LYR_ACT_STAT", sYD_STK_COL_ACT_STAT);
			recPara.setField("MODIFIER", szYdUsrId);
			
			intRtnVal = ydStkLyrDao.updYdStklyrActStat(recPara);
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  단 야드적치단활성상태의 UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			}else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  단 야드적치단활성상태 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  단 야드적치단활성상태의 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
		
		
		return YdConstant.RETN_CD_SUCCESS;
	}

	/**
	 * 오퍼레이션명 : 야드적치X값 변경 변경
	 *
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (JDTORecord : YD_WBOOK_ID , MODIFIER)
	 * @return
	 * @throws JDTOException
	 */
	public String updYdCoilRuleXCol(JDTORecord msgRecord)throws JDTOException  {

	//	TB_YD_STKCOL:YD_STK_COL_RULE_XAXIS
	//	TB_YD_STKBED:YD_STK_BED_XAXIS 
	//	TB_YD_STKLYR:YD_STK_LYR_XAXIS 

		
		JDTORecord inRec = JDTORecordFactory.getInstance().create();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		String sYD_STK_LYR_XAXIS = null;
		String sYD_STK_LYR_XAXIS_DB = null;
		
		String szOperationName = "야드적치X값 상태 변경";
		String szMethodName="updYdCoilRuleXaxisCol";
		String szMsg="";
		
		JDTORecordSet rsStkCol =null;
		
		//리턴값
		int intRtnVal = 0;
		
		String szYdUsrId = null;
		
		//DAO
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		
		 
		try {
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			sYD_STK_LYR_XAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_RULE_XAXIS");
			
			
			if(sYD_STK_LYR_XAXIS.length() == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 야드적치X값 상태.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			}
			
			szYdUsrId = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			if(szYdUsrId.equals("")){
				szYdUsrId = "updYdCoil";
			}
			
			//-------------------------------------------------------------
			// 기존 해당 적치열에 대한야드적치X값 상태 정보 조회
			//-------------------------------------------------------------
			
			recPara = JDTORecordFactory.getInstance().create();
			inRec   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP"));
			
			
			rsStkCol = JDTORecordFactory.getInstance().createRecordSet("rsStkCol");
			
			intRtnVal = ydStkColDao.getYdStkcol(recPara, rsStkCol, 0);

			if( intRtnVal < 0) {
				szMsg = "[Jsp Session  -  " + szOperationName +"]  야드적치X값 상태 조회 ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
				
			}else if(intRtnVal == 0){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  야드적치X값 상태 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return szMsg;
			}
			
			
			rsStkCol.first();
			inRec = rsStkCol.getRecord();
			
			sYD_STK_LYR_XAXIS_DB = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_RULE_XAXIS");
			//-------------------------------------------------------------
			// 야드적치단활성상태 정보 비교
			//-------------------------------------------------------------
			
			if(sYD_STK_LYR_XAXIS_DB.equals(sYD_STK_LYR_XAXIS)){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  야드적치X값 상태 변경정보되지 않았습니다..";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_SUCCESS;
				
			}
			
//			//-------------------------------------------------------------
//			//  TB_YD_STKCOL : 야드적치단활성상태 UPDATE
//			//-------------------------------------------------------------
//			
//			recPara = JDTORecordFactory.getInstance().create();
//			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP"));
//			recPara.setField("YD_STK_COL_RULE_XAXIS", sYD_STK_LYR_XAXIS);
//			recPara.setField("MODIFIER", szYdUsrId);
//			
//			ydUtils.displayRecord(szOperationName, recPara);
//			intRtnVal =  ydStkColDao.updYdStkcol(recPara, 0);
//
//			if(intRtnVal <0 ){
//				szMsg = "[Jsp Session  -  " + szOperationName +"]  열 야드적치X값 상태상태의 UPDATE ERROR";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				throw new DAOException(szMsg); 
//			} else if(intRtnVal == 0 ){
//				szMsg = "[Jsp Session  -  " + szOperationName +"]  열 야드적치X값 상태의 정보가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//			}
//			
//			
//			szMsg = "[Jsp Session  -  " + szOperationName +"]  열야드적치X값 상태의 변경 성공.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			
			
			
			//-------------------------------------------------------------
			//  TB_YD_STKBED 적치 베드 정보의  UPDATE
			//-------------------------------------------------------------

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP"));
			recPara.setField("YD_STK_BED_XAXIS", sYD_STK_LYR_XAXIS);
			recPara.setField("MODIFIER", szYdUsrId);
		
			intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recPara, 301);
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  베드 야드적치X값 상태의 UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			}else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  베드 야드적치X값 상태정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  베드  야드적치X값 상태의 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//-------------------------------------------------------------
			//  TB_YD_STKLYR 적치 베드 정보의  UPDATE
			//  야드적치단활성상태 서로 틀
			//-------------------------------------------------------------
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP"));
			recPara.setField("YD_STK_LYR_XAXIS", sYD_STK_LYR_XAXIS);
			recPara.setField("MODIFIER", szYdUsrId);
			
			intRtnVal = ydStkLyrDao.updYdStklyrX(recPara);
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  단 야드적치X값 상태의 UPDATE ERROR";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg); 
			}else if(intRtnVal == 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"]  단 야드적치X값 상태 정보가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			szMsg = "[Jsp Session  -  " + szOperationName +"]  단 야드적치X값 상태의 성공.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
		
		
		return YdConstant.RETN_CD_SUCCESS;
	}

	//------------------------------------------------------------------------------------------------
	// YdDataUtil END
	//------------------------------------------------------------------------------------------------
		
	
} 		//end class
		
	
