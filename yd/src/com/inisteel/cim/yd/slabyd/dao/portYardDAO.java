package com.inisteel.cim.yd.slabyd.dao;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.ExceptionMessageUtil;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

/**
 * 		[A]클래스명 : 항만야드 DAO class
 * 
 *
 */
public class portYardDAO {

	private Logger logger = new Logger("yd");
	
	private DBAssistantDAO assistantDAO = new DBAssistantDAO();
	
	/**
	 * 		[A] 오퍼레이션명 : 적치열구분,개소코드로 미예약(여유분)된 적치열 정보추출
	 * 
	 * @param sPLAN_SLAB_NO
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord getYdMinStkcolLike(String vYD_STK_COL_GP, String vWLOC_CD) throws DAOException {
		
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		
		try {
			
			/*
			-----------------------------------------
			-- 실제 QueryService에 등록되어있는 Query --
			-----------------------------------------
			-- jspeed 등록
			
			SELECT MIN(YD_STK_COL_GP) AS YD_STK_COL_GP
			      ,MIN(YD_PNT_CD) AS YD_PNT_CD
			  FROM TB_YD_STKCOL
			 WHERE YD_STK_COL_GP LIKE :V_YD_STK_COL_GP || '%'
			   AND WLOC_CD = :WLOC_CD
			   AND DEL_YN='N'
			   AND TRN_EQP_CD IS NULL
			 ORDER BY YD_STK_COL_GP
			
			-----------------------------------------
			*/
			
			String sQueryID = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdMinStkcolLike";
			Object objParam[] = {
					vYD_STK_COL_GP	// 적치열구분
				   ,vWLOC_CD		// 개소코드
			};
			
			JDTORecordSet jrSetRtn = assistantDAO.getRecordSet(sQueryID, objParam);
			
			if(jrSetRtn.next()){
				jrReturn = jrSetRtn.getRecord();
			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e));
			throw new DAOException(e) ; 
		}
		return jrReturn;
	}
	
	/**
	 * 			[A] 오퍼레이션 명 : 적치열 정보갱신 (POINT 예약처리) 			
	 * 
	 * @param sPLN_BLM_NO
	 * @return
	 * @throws DAOException
	 */
	public int updPortYdStkcol(JDTORecord jrParam) throws DAOException {
		
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		
		try {
			/*
			-----------------------------------------
			-- 실제 QueryService에 등록되어있는 Query --
			-----------------------------------------
			-- sspped 등록
			
			UPDATE TB_YD_STKCOL
			   SET TRN_EQP_CD = :TRN_EQP_CD
			      ,YD_CAR_USE_GP = :YD_CAR_USE_GP
			      ,MODIFIER = :MODIFIER
			      ,MOD_DDTT = SYSDATE
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			
			-----------------------------------------
			*/			
			String sQueryID = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updPortYdStkcol";
			Object objParam[] = {
				    jrParam.getField(StringHelper.trim("TRN_EQP_CD"))        
				   ,jrParam.getField(StringHelper.trim("YD_CAR_USE_GP"))        
				   ,jrParam.getField(StringHelper.trim("MODIFIER"))        
				   ,jrParam.getField(StringHelper.trim("YD_STK_COL_GP"))        
			};
			
			return assistantDAO.trtProcess(sQueryID, objParam);
			

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e));
			throw new DAOException(e) ; 
		}

	}

}
