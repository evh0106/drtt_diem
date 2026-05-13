/**
 * @(#)PSlabCommDAO
 *
 * @version          V1.00
 * @author           염용선
 * @date             2020/05/06
 * 
 * @description      Slab야드 공통 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2020/05/06   염용선      염용선      최초 등록
 */
package com.inisteel.cim.yd.pSlabYd.dao;

import java.sql.Types;
import java.util.Iterator;
import java.util.List;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdConstant;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.common.rule.GetBreRule5;
import com.inisteel.cim.common.dao.CommonDAO;

import jspeed.base.util.StringHelper;

/**
 * [A] 클래스명 : Slab야드 공통 DAO
 *
 */

public class PSlabYdCommDAO extends DBAssistantDAO {

	
	private PSlabYdUtils slabUtils = new PSlabYdUtils();
	private CommonDAO slabCommonDAO = new CommonDAO();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
    private static PSlabYdCommDAO instance;
	
	public static PSlabYdCommDAO getInstance()
	{
		if(instance == null)
		{
			synchronized(com.inisteel.cim.common.dao.CommonDAO.class)
            {
                if(instance == null)
                    instance = new PSlabYdCommDAO();
            }
		}
		return instance;
	}
	
	
    /**
     * YJK
     * @return 
     * @throws 
     */
	public List getCommonList(String queryCode,Object[] objs) throws DAOException{
		return slabCommonDAO.findList(queryCode, objs);	
    }
	/***************************************************************************
	 * L2 송신 전문 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : L2전문조회
	 *      
	 *      @param String/ msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgL2(String msgId, JDTORecord jrParam) throws DAOException {
		String methodNm = "L2전문조회[PSlabCommDAO.getMsgL2] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("YDY3L001".equals(msgId)) {

				trtNm = "저장위치제원";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDY3L001";
				/*
				--저장위치제원 전문조회 
				SELECT JMS_TC_CD                                  --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,JMS_TC_CD                                  --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
				     ||'I'                                        --전문구분
				     ||'0092'                                     --전문길이
				     ||RPAD(' ',29,' ')                           --임시 
				     ||RPAD(NVL(YD_INFO_SYNC_CD     ,' '), 1,' ') --야드정보동기화코드  
				     ||RPAD(NVL(YD_GP               ,' '), 1,' ') --야드구분
				     ||RPAD(NVL(YD_BAY_GP           ,' '), 1,' ') --야드동구분
				     ||RPAD(NVL(YD_EQP_GP           ,' '), 2,' ') --야드설비구분
				     ||RPAD(NVL(YD_STK_COL_NO       ,' '), 2,' ') --야드적치열번호
				     ||RPAD(NVL(YD_STK_BED_NO       ,' '), 2,' ') --야드적치Bed번호
				     ||RPAD(NVL(YD_STK_BED_L_GP     ,' '), 1,' ') --야드적치Bed길이구분
				     ||RPAD(NVL(YD_STK_BED_W_GP     ,' '), 1,' ') --야드적치Bed폭구분
				     ||RPAD(NVL(YD_STK_BED_DIR_GP   ,' '), 1,' ') --야드적치Bed방향구분
				     ||RPAD(NVL(YD_STK_BED_ACT_STAT ,' '), 1,' ') --야드적치Bed활성상태
				     ||RPAD(NVL(YD_STK_BED_WHIO_STAT,' '), 1,' ') --야드적치Bed입출고상태
				     ||RPAD(NVL(YD_STK_BED_XAXIS    ,' '), 7,' ') --야드적치BedX축
				     ||RPAD(NVL(YD_STK_BED_YAXIS    ,' '), 5,' ') --야드적치BedY축
				     ||RPAD(NVL(YD_STK_BED_ZAXIS    ,' '), 5,' ') --야드적치BedZ축
				     ||RPAD(NVL(YD_STK_BED_LYR_MAX  ,' '), 3,' ') --야드적치Bed단Max
				     ||RPAD(NVL(YD_STK_BED_WT_MAX   ,' '), 7,' ') --야드적치Bed중량Max
				     ||RPAD(NVL(YD_STK_BED_H_MAX    ,' '), 5,' ') --야드적치Bed높이Max
				     ||RPAD(NVL(YD_STK_BED_L_MAX    ,' '), 5,' ') --야드적치Bed길이Max
				     ||RPAD(NVL(YD_STK_BED_W_MAX    ,' '), 5,' ') --야드적치Bed폭Max
				     ||RPAD(NVL(YD_STK_BED_W_MIN    ,' '), 5,' ') --야드적치Bed폭Min
				     ||RPAD(NVL(YD_CAR_ARRSTRT_STAT ,' '), 1,' ') --야드차량착발상태
				     ||RPAD(NVL(YD_CAR_USE_GP       ,' '), 1,' ') --야드차량사용구분
				     ||RPAD(NVL(YD_EQP_WRK_STAT     ,' '), 1,' ') --야드설비작업상태
				     ||RPAD(NVL(CAR_NO              ,' '),15,' ') --차량번호
				     ||RPAD(NVL(TRN_EQP_CD          ,' '), 8,' ') --운송장비코드
				     ||RPAD(NVL(CARD_NO             ,' '), 4,' ') --카드번호
				     ||RPAD(NVL(YD_CAR_AIM_YD_GP    ,' '), 1,' ') --야드차량목표야드구분
				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (SELECT 'YDY3L001' AS JMS_TC_CD
				              ,:V_YD_INFO_SYNC_CD AS YD_INFO_SYNC_CD
				              ,SC.YD_GP
				              ,SC.YD_BAY_GP
				              ,SC.YD_EQP_GP
				              ,SC.YD_STK_COL_NO
				              ,SB.YD_STK_BED_NO
				              ,SB.YD_STK_BED_L_GP
				              ,SB.YD_STK_BED_W_GP
				              ,SB.YD_STK_BED_DIR_GP
				              ,SB.YD_STK_BED_ACT_STAT
				              ,SB.YD_STK_BED_WHIO_STAT
				              ,TO_CHAR(SB.YD_STK_BED_XAXIS  ,'FM0000000') AS YD_STK_BED_XAXIS
				              ,TO_CHAR(SB.YD_STK_BED_YAXIS  ,'FM00000'  ) AS YD_STK_BED_YAXIS
				              ,TO_CHAR(SB.YD_STK_BED_ZAXIS  ,'FM00000'  ) AS YD_STK_BED_ZAXIS
				              ,TO_CHAR(SB.YD_STK_BED_LYR_MAX,'FM000'    ) AS YD_STK_BED_LYR_MAX
				              ,TO_CHAR(SB.YD_STK_BED_WT_MAX ,'FM0000000') AS YD_STK_BED_WT_MAX
				              ,TO_CHAR(SB.YD_STK_BED_H_MAX  ,'FM00000'  ) AS YD_STK_BED_H_MAX
				              ,TO_CHAR(SB.YD_STK_BED_L_MAX  ,'FM00000'  ) AS YD_STK_BED_L_MAX
				              ,TO_CHAR(SB.YD_STK_BED_W_MAX  ,'FM0000V0' ) AS YD_STK_BED_W_MAX
				              ,TO_CHAR(NVL(SB.YD_STK_BED_W_MIN,0)  ,'FM0000V0' ) AS YD_STK_BED_W_MIN
				              ,DECODE(TS.YD_CAR_PROG_STAT,'1','S','A','S','2','A','B','A') AS YD_CAR_ARRSTRT_STAT
				              ,SC.YD_CAR_USE_GP
				              ,TS.YD_EQP_WRK_STAT
				              ,SC.CAR_NO
				              ,SC.TRN_EQP_CD
				              ,SC.CARD_NO
				              ,TS.YD_CAR_AIM_YD_GP
				          FROM TB_YD_STKBED SB
				              ,TB_YD_STKCOL SC
				              ,(SELECT YD_STK_COL_GP
				                      ,SUBSTR(YD_CARUD_STOP_LOC,1,1) AS YD_CAR_AIM_YD_GP
				                      ,YD_CAR_PROG_STAT
				                      ,YD_EQP_WRK_STAT
				                  FROM (SELECT SC.YD_STK_COL_GP
				                              ,TS.YD_CARUD_STOP_LOC
				                              ,TS.YD_CAR_PROG_STAT
				                              ,TS.YD_EQP_WRK_STAT
				                              ,ROW_NUMBER() OVER (ORDER BY TS.YD_CAR_SCH_ID DESC) AS RN
				                          FROM TB_YD_STKCOL SC
				                              ,TB_YD_CARSCH TS
				                         WHERE SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
				                           AND SC.YD_STK_COL_GP LIKE '__PT%'
				                           AND SC.DEL_YN        = 'N'
				                           AND TS.DEL_YN        = 'N'
				                           AND ((TS.TRN_EQP_CD    = SC.TRN_EQP_CD
				                             AND SC.YD_CAR_USE_GP = 'L')  --구내운송
				                             OR (TS.CAR_NO        = SC.CAR_NO
				                             AND TS.CARD_NO       = SC.CARD_NO
				                             AND SC.YD_CAR_USE_GP = 'G')) --출하차량
				                         UNION ALL
				                        SELECT SC.YD_STK_COL_GP
				                              ,TS.YD_CARUD_STOP_LOC
				                              ,TS.YD_CAR_PROG_STAT
				                              ,TS.YD_EQP_WRK_STAT
				                              ,ROW_NUMBER() OVER (ORDER BY TS.YD_TCAR_SCH_ID DESC) AS RN
				                          FROM TB_YD_STKCOL  SC
				                              ,TB_YD_TCARSCH TS
				                         WHERE SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
				                           AND SC.YD_STK_COL_GP LIKE '__TC%'
				                           AND SC.DEL_YN        = 'N'
				                           AND TS.YD_EQP_ID     = SUBSTR(SC.YD_STK_COL_GP,1,1)||'X'||SUBSTR(SC.YD_STK_COL_GP,3)
				                           AND TS.DEL_YN        = 'N')
				                 WHERE RN = 1) TS
				         WHERE SC.YD_STK_COL_GP = SB.YD_STK_COL_GP
				           AND SC.YD_STK_COL_GP = TS.YD_STK_COL_GP(+)
				           AND SB.YD_STK_COL_GP LIKE :V_YD_STK_COL_GP||'%'
				           AND SB.YD_STK_BED_NO LIKE :V_YD_STK_BED_NO||'%'
				           AND SC.YD_GP = 'D'
				           AND SC.DEL_YN = 'N'
				           AND SB.DEL_YN = 'N')
				 WHERE JMS_TC_CD IS NOT NULL
				 ORDER BY YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, YD_STK_BED_NO

				 */
			
			} else //-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^
			if ("YDY3L001_CarInfo".equals(msgId)) {

				trtNm = "저장위치제원(추가)";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDY3L001_CarInfo";
				/*
				 * --저장위치제원 전문조회 (BACKUP처리) 
				SELECT JMS_TC_CD                                  --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,JMS_TC_CD                                  --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
				     ||'I'                                        --전문구분
				     ||'0092'                                     --전문길이
				     ||RPAD(' ',29,' ')                           --임시
				     ||RPAD(NVL(YD_INFO_SYNC_CD     ,' '), 1,' ') --야드정보동기화코드
				     ||RPAD(NVL(YD_GP               ,' '), 1,' ') --야드구분
				     ||RPAD(NVL(YD_BAY_GP           ,' '), 1,' ') --야드동구분
				     ||RPAD(NVL(YD_EQP_GP           ,' '), 2,' ') --야드설비구분
				     ||RPAD(NVL(YD_STK_COL_NO       ,' '), 2,' ') --야드적치열번호
				     ||RPAD(NVL(YD_STK_BED_NO       ,' '), 2,' ') --야드적치Bed번호
				     ||RPAD(NVL(YD_STK_BED_L_GP     ,' '), 1,' ') --야드적치Bed길이구분
				     ||RPAD(NVL(YD_STK_BED_W_GP     ,' '), 1,' ') --야드적치Bed폭구분
				     ||RPAD(NVL(YD_STK_BED_DIR_GP   ,' '), 1,' ') --야드적치Bed방향구분
				     ||RPAD(NVL(YD_STK_BED_ACT_STAT ,' '), 1,' ') --야드적치Bed활성상태
				     ||RPAD(NVL(YD_STK_BED_WHIO_STAT,' '), 1,' ') --야드적치Bed입출고상태
				     ||RPAD(NVL(YD_STK_BED_XAXIS    ,' '), 7,' ') --야드적치BedX축
				     ||RPAD(NVL(YD_STK_BED_YAXIS    ,' '), 5,' ') --야드적치BedY축
				     ||RPAD(NVL(YD_STK_BED_ZAXIS    ,' '), 5,' ') --야드적치BedZ축
				     ||RPAD(NVL(YD_STK_BED_LYR_MAX  ,' '), 3,' ') --야드적치Bed단Max
				     ||RPAD(NVL(YD_STK_BED_WT_MAX   ,' '), 7,' ') --야드적치Bed중량Max
				     ||RPAD(NVL(YD_STK_BED_H_MAX    ,' '), 5,' ') --야드적치Bed높이Max
				     ||RPAD(NVL(YD_STK_BED_L_MAX    ,' '), 5,' ') --야드적치Bed길이Max
				     ||RPAD(NVL(YD_STK_BED_W_MAX    ,' '), 5,' ') --야드적치Bed폭Max
				     ||RPAD(NVL(YD_STK_BED_W_MIN    ,' '), 5,' ') --야드적치Bed폭Min
				     ||RPAD(NVL(YD_CAR_ARRSTRT_STAT ,' '), 1,' ') --야드차량착발상태
				     ||RPAD(NVL(YD_CAR_USE_GP       ,' '), 1,' ') --야드차량사용구분
				     ||RPAD(NVL(YD_EQP_WRK_STAT     ,' '), 1,' ') --야드설비작업상태
				     ||RPAD(NVL(CAR_NO              ,' '),15,' ') --차량번호
				     ||RPAD(NVL(TRN_EQP_CD          ,' '), 8,' ') --운송장비코드
				     ||RPAD(NVL(CARD_NO             ,' '), 4,' ') --카드번호
				     ||RPAD(NVL(YD_CAR_AIM_YD_GP    ,' '), 1,' ') --야드차량목표야드구분
				       AS JMS_TC_MESSAGE --JMSTCMessage
				  FROM (
				        SELECT 'YDY3L001'                                 AS JMS_TC_CD
				              ,:V_YD_INFO_SYNC_CD                         AS YD_INFO_SYNC_CD
				              ,SC.YD_GP
				              ,SC.YD_BAY_GP
				              ,SC.YD_EQP_GP
				              ,SC.YD_STK_COL_NO
				              ,SB.YD_STK_BED_NO
				              ,SB.YD_STK_BED_L_GP
				              ,SB.YD_STK_BED_W_GP
				              ,SB.YD_STK_BED_DIR_GP
				              ,SB.YD_STK_BED_ACT_STAT
				              ,SB.YD_STK_BED_WHIO_STAT
				              ,TO_CHAR(SB.YD_STK_BED_XAXIS  ,'FM0000000') AS YD_STK_BED_XAXIS
				              ,TO_CHAR(SB.YD_STK_BED_YAXIS  ,'FM00000'  ) AS YD_STK_BED_YAXIS
				              ,TO_CHAR(SB.YD_STK_BED_ZAXIS  ,'FM00000'  ) AS YD_STK_BED_ZAXIS
				              ,TO_CHAR(SB.YD_STK_BED_LYR_MAX,'FM000'    ) AS YD_STK_BED_LYR_MAX
				              ,TO_CHAR(SB.YD_STK_BED_WT_MAX ,'FM0000000') AS YD_STK_BED_WT_MAX
				              ,TO_CHAR(SB.YD_STK_BED_H_MAX  ,'FM00000'  ) AS YD_STK_BED_H_MAX
				              ,TO_CHAR(SB.YD_STK_BED_L_MAX  ,'FM00000'  ) AS YD_STK_BED_L_MAX
				              ,TO_CHAR(SB.YD_STK_BED_W_MAX  ,'FM0000V0' ) AS YD_STK_BED_W_MAX
				              ,TO_CHAR(SB.YD_STK_BED_W_MIN  ,'FM0000V0' ) AS YD_STK_BED_W_MIN
				              
				              ,NVL(:V_YD_CAR_ARRSTRT_STAT,'')                    AS YD_CAR_ARRSTRT_STAT
				              --,DECODE(TS.YD_CAR_PROG_STAT,'1','S','A','S','2','A','B','A') AS YD_CAR_ARRSTRT_STAT
				              ,NVL(:V_YD_CAR_USE_GP,'')                          AS YD_CAR_USE_GP
				              ,NVL(:V_YD_EQP_WRK_STAT,'')                        AS YD_EQP_WRK_STAT
				              ,NVL(:V_CAR_NO,'')                                 AS CAR_NO
				              ,NVL(:V_TRN_EQP_CD,'')                             AS TRN_EQP_CD
				              ,NVL(:V_CARD_NO,'')                                AS CARD_NO
				              ,'D'                                               AS YD_CAR_AIM_YD_GP 
				          FROM TB_YD_STKBED SB
				              ,TB_YD_STKCOL SC
				              ,(SELECT YD_STK_COL_GP
				                      ,SUBSTR(YD_CARUD_STOP_LOC,1,1) AS YD_CAR_AIM_YD_GP
				                      ,YD_CAR_PROG_STAT
				                      ,YD_EQP_WRK_STAT
				                  FROM (SELECT SC.YD_STK_COL_GP
				                              ,TS.YD_CARUD_STOP_LOC
				                              ,TS.YD_CAR_PROG_STAT
				                              ,TS.YD_EQP_WRK_STAT
				                              ,ROW_NUMBER() OVER (ORDER BY TS.YD_CAR_SCH_ID DESC) AS RN
				                          FROM TB_YD_STKCOL SC
				                              ,TB_YD_CARSCH TS
				                         WHERE SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
				                           AND SC.YD_STK_COL_GP LIKE '__PT%'
				                           AND SC.DEL_YN        = 'N'
				                           AND TS.DEL_YN        = 'N'
				                           AND ((TS.TRN_EQP_CD    = SC.TRN_EQP_CD
				                             AND SC.YD_CAR_USE_GP = 'L')  --구내운송
				                             OR (TS.CAR_NO        = SC.CAR_NO
				                             AND TS.CARD_NO       = SC.CARD_NO
				                             AND SC.YD_CAR_USE_GP = 'G')) --출하차량
				                         UNION ALL
				                        SELECT SC.YD_STK_COL_GP
				                              ,TS.YD_CARUD_STOP_LOC
				                              ,TS.YD_CAR_PROG_STAT
				                              ,TS.YD_EQP_WRK_STAT
				                              ,ROW_NUMBER() OVER (ORDER BY TS.YD_TCAR_SCH_ID DESC) AS RN
				                          FROM TB_YD_STKCOL  SC
				                              ,TB_YD_TCARSCH TS
				                         WHERE SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
				                           AND SC.YD_STK_COL_GP LIKE '__TC%'
				                           AND SC.DEL_YN        = 'N'
				                           AND TS.YD_EQP_ID     = SUBSTR(SC.YD_STK_COL_GP,1,1)||'X'||SUBSTR(SC.YD_STK_COL_GP,3)
				                           AND TS.DEL_YN        = 'N')
				                 WHERE RN = 1) TS
				         WHERE SC.YD_STK_COL_GP = SB.YD_STK_COL_GP
				           AND SC.YD_STK_COL_GP = TS.YD_STK_COL_GP(+)
				           AND SB.YD_STK_COL_GP LIKE :V_YD_STK_COL_GP||'%'
				           AND SB.YD_STK_BED_NO LIKE :V_YD_STK_BED_NO||'%'
				           AND SC.YD_GP = 'D'
				           AND SC.DEL_YN = 'N'
				           AND SB.DEL_YN = 'N')
				 WHERE JMS_TC_CD IS NOT NULL
				 ORDER BY YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, YD_STK_BED_NO

				 */				
			
			} else 
			if ("YDY3L002".equals(msgId)) { 
				trtNm = "저장품제원";

				//야드정보동기화코드
				// 1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
				String ydInfoSyncCd = slabUtils.trim(jrParam.getFieldString("YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) 
				{
					//저장위치별
					/*
					 * --저장품제원(저장위치별) 전문조회
					SELECT JMS_TC_CD                                     --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,JMS_TC_CD                                     --전문ID
					     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')       --생성일시
					     ||'I'                                           --전문구분
					     ||'0261'    --전문길이
					     ||RPAD(' ',29,' ')                              --임시
					     ||RPAD(NVL(YD_INFO_SYNC_CD        ,' '), 1,' ') --야드정보동기화코드
					     ||TO_CHAR(COUNT(*) OVER (),'FM000')             --야드재료정보송신매수
					     ||TO_CHAR(ROWNUM          ,'FM000')             --야드재료정보송신순번
					     ||RPAD(NVL(STL_APPEAR_GP          ,' '), 1,' ') --재료외형구분
					     ||RPAD(NVL(STL_NO                 ,' '),11,' ') --재료번호
					     ||RPAD(NVL(YD_STR_LOC             ,' '), 8,' ') --야드저장위치
					     ||RPAD(NVL(YD_STK_LYR_NO          ,' '), 3,' ') --야드적치단번호
					     ||RPAD(NVL(YD_STL_WT              ,' '), 5,' ') --야드재료중량
					     ||RPAD(NVL(YD_STL_T               ,' '), 6,' ') --야드재료두께
					     ||RPAD(NVL(YD_STL_W               ,' '), 5,' ') --야드재료폭
					     ||RPAD(NVL(YD_STL_L               ,' '), 7,' ') --야드재료길이
					     ||RPAD(NVL(MAT_ODIA               ,' '), 5,' ') --재료외경
					     ||RPAD(NVL(MAT_IDIA               ,' '), 5,' ') --재료내경
					     ||RPAD(NVL(STLKIND_CD             ,' '), 3,' ') --강종
					     ||RPAD(NVL(SPEC_ABBSYM            ,' '),15,' ') --규격약호
					     ||RPAD(NVL(YD_IPGO_DD             ,' '),14,' ') --야드입고일자
					     ||RPAD(NVL(PLNT_PROC_CD           ,' '), 3,' ') --공장공정코드
					     ||RPAD(NVL(CURR_PROG_CD           ,' '), 1,' ') --현재진도코드
					     ||RPAD(NVL(ORD_YEOJAE_GP          ,' '), 1,' ') --주문여재구분
					     ||RPAD(NVL(ORD_NO                 ,' '),10,' ') --주문번호
					     ||RPAD(NVL(ORD_DTL                ,' '), 3,' ') --주문행번
					     ||RPAD(NVL(BUY_SLAB_NO            ,' '),30,' ') --구입SLAB번호
					     ||RPAD(NVL(SLAB_WO_RT_CD          ,' '), 2,' ') --SLAB지시행선코드
					     ||RPAD(NVL(ORD_HCR_GP             ,' '), 1,' ') --설계HCR구분
					     ||RPAD(NVL(HCR_GP                 ,' '), 1,' ') --HCR구분
					     ||RPAD(NVL(CC_MC_CD               ,' '), 1,' ') --연주Machine코드
					     ||RPAD(NVL(SCARFING_YN            ,' '), 1,' ') --SCARFING여부
					     ||RPAD(NVL(SCARFING_DONE_YN       ,' '), 1,' ') --SCARFING완료유무
					     ||RPAD(NVL(RPR_MTD                ,' '), 1,' ') --주편손질방법
					     ||RPAD(NVL(SCARFING_DEPTH         ,' '), 2,' ') --SCARFING깊이
					     ||RPAD(NVL(REHEAT_SLAB_GP         ,' '), 1,' ') --재열재구분
					     ||RPAD(NVL(PTOP_PLNT_GP           ,' '), 2,' ') --조업공장구분
					     ||RPAD(NVL(REFUR_CHG_LOT_NO       ,' '),10,' ') --가열로장입Lot번호
					     ||RPAD(NVL(CT_LOT_SCH_SERNO       ,' '),22,' ') --생산통제Lot스케줄일련번호
					     ||RPAD(NVL(FRTOMOVE_ORD_DATE      ,' '), 8,' ') --이송지시일자
					     ||RPAD(NVL(FRTOMOVE_PLANT_GP      ,' '), 2,' ') --이송공장구분
					     ||RPAD(NVL(URGENT_FRTOMOVE_WORD_GP,' '), 1,' ') --긴급이송작업지시구분
					     ||RPAD(NVL(HYSCO_TRANS_CLS        ,' '), 1,' ') --HYSCO운송구분
					     ||RPAD(NVL(APPEAR_GRADE           ,' '), 1,' ') --외관종합판정등급
					     ||RPAD(NVL(COOL_METHOD            ,' '), 1,' ') --권취코일냉각방법
					     ||RPAD(NVL(COOL_DONE_GP           ,' '), 1,' ') --냉각완료구분
					     ||RPAD(NVL(CONV_BRANCH_CD         ,' '), 2,' ') --야드Conveyor분기코드
					     ||RPAD(NVL(CUST_CD                ,' '), 6,' ') --고객코드
					     ||RPAD(NVL(DEST_CD                ,' '), 5,' ') --목적지코드
					     ||RPAD(NVL(DLVRDD_RULE_DD         ,' '), 8,' ') --납기기준일
					     ||RPAD(NVL(ITEMNAME_CD            ,' '), 3,' ') --품명코드
					     ||RPAD(NVL(OVERALL_STATAMP_GRADE  ,' '), 1,' ') --종합판정등급
					     ||RPAD(NVL(ORD_GP                 ,' '), 1,' ') --수주구분
					     ||RPAD(NVL(YD_STK_LOT_TP          ,' '), 2,' ') --야드산적LotType
					     ||RPAD(NVL(YD_STK_LOT_CD          ,' '),18,' ') --야드산적Lot코드
					     ||DECODE(JMS_TC_CD,'YDY3L002',RPAD(NVL(PL_MPL_NO,' '),10,' ')) --후판날판번호
					     ||RPAD(NVL(RCPT_SLAB_STAT          ,' '), 2,' ') --야드산적LotType 
					       AS JMS_TC_MESSAGE --JMSTCMessage
					  FROM (
					             SELECT 'YDY3L002' AS JMS_TC_CD
					              ,:V_YD_INFO_SYNC_CD                      AS YD_INFO_SYNC_CD
					              ,ST.STL_APPEAR_GP
					              ,SL.STL_NO
					              ,SL.YD_STK_COL_GP || SL.YD_STK_BED_NO    AS YD_STR_LOC
					              ,SL.YD_STK_LYR_NO
					              ,TO_CHAR(ST.SLAB_WT,'FM00000'  )       AS YD_STL_WT
					              ,TO_CHAR(ST.SLAB_T ,'FM000V000')       AS YD_STL_T
					              ,TO_CHAR(ST.SLAB_W ,'FM0000V0' )       AS YD_STL_W
					              ,TO_CHAR(ST.SLAB_LEN ,'FM0000000')       AS YD_STL_L
					              ,'00000'                                     AS MAT_ODIA
					              ,'00000'                                 AS MAT_IDIA
					              ,SS.STLKIND_CD
					              ,ST.SPEC_ABBSYM
					              ,TO_CHAR(ST.REG_DDTT,'YYYYMMDDHH24MISS') AS YD_IPGO_DD
					              ,ST.PLNT_PROC_CD
					              ,ST.CURR_PROG_CD                          AS CURR_PROG_CD
					              ,ST.ORD_YEOJAE_GP
					              ,ST.ORD_NO
					              ,ST.ORD_DTL
					              ,SS.BUY_SLAB_NO
					              ,ST.SLAB_WO_RT_CD
					              ,ST.ORD_HCR_GP
					              ,ST.HCR_GP
					              ,TO_CHAR(SS.CC_CCM_NO)                   AS CC_MC_CD
					              ,ST.SCARFING_YN
					              ,ST.SCARFING_DONE_YN
					              ,ST.WO_MSLAB_RPR_MTD                     AS RPR_MTD
					              ,ST.SCARFING_DEPTH
					              ,ST.REHEAT_SLAB_GP
					              ,ST.PTOP_PLNT_GP
					              ,SS.REFUR_CHG_LOT_NO
					              ,DECODE(SUBSTR(ST.SLAB_WO_RT_CD,1,1),'P',SS.YD_CHG_NO,TO_CHAR(SS.REFUR_CHG_PLN_SERNO)) AS CT_LOT_SCH_SERNO
					              ,ST.FRTOMOVE_ORD_DATE
					              ,SS.FRTOMOVE_PLANT_GP
					              ,SS.URGENT_FRTOMOVE_WORD_GP
					              ,' '                                     AS HYSCO_TRANS_CLS
					              ,' '                                     AS APPEAR_GRADE
					              ,' '                                     AS COOL_METHOD
					              ,' '                                     AS COOL_DONE_GP
					              ,' '                                     AS CONV_BRANCH_CD
					              ,SS.CUST_CD
					              ,SS.DEST_CD
					              ,SS.YD_DLVRDD_RULE_DD                    AS DLVRDD_RULE_DD
					              ,ST.ITEMNAME_CD
					              ,ST.OVERALL_STAMP_GRADE                  AS OVERALL_STATAMP_GRADE
					              ,SS.ORD_GP
					              ,SS.YD_STK_LOT_TP
					              ,SS.YD_STK_LOT_CD
					              ,SS.PL_MPL_NO
					              ,NVL((SELECT STLKIND_CD
										FROM USRPTA.TB_PT_HEATCOMM
										WHERE HEAT_NO = ST.HEAT_NO
										  AND ROWNUM = 1),'') AS STLKIND_CD2 
							      ,'' AS RCPT_SLAB_STAT // 녹슨재
					          FROM  VW_YD_SLABCOMM  ST
					              ,TB_YD_STKLYR SL
					              ,TB_YD_STOCK SS
					         WHERE SL.STL_NO = ST.SLAB_NO
					           AND SL.STL_NO = SS.STL_NO
					           AND SL.YD_STK_COL_GP LIKE :V_YD_STK_COL_GP||'%'
					           AND SL.YD_STK_BED_NO LIKE :V_YD_STK_BED_NO||'%'
					           AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
					           AND SL.DEL_YN = 'N'           
					         ORDER BY SL.YD_STK_COL_GP, SL.YD_STK_BED_NO, SL.YD_STK_LYR_NO)
					 */
					jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDY3L002Loc";
					
				} else {
					//재료별
					jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDY3L002Stl";
					/*
					 * /*--저장품제원(재료별) 전문조회 
						WITH TEMP_PARAM AS (
						SELECT :V_YD_GP AS V_YD_GP FROM DUAL
						
						)
						SELECT JMS_TC_CD                                     --JMSTC코드
						      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
						      ,JMS_TC_CD                                     --전문ID
						     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')       --생성일시
						     ||'I'                                           --전문구분
						     ||'0259'    --전문길이
						     ||RPAD(' ',29,' ')                              --임시
						     ||RPAD(NVL(YD_INFO_SYNC_CD        ,' '), 1,' ') --야드정보동기화코드
						     ||RPAD(NVL(YD_STL_INFO_SND_SH     ,' '), 3,' ') --야드재료정보송신매수
						     ||RPAD(NVL(YD_STL_INFO_SND_CNT    ,' '), 3,' ') --야드재료정보송신순번
						     ||RPAD(NVL(STL_APPEAR_GP          ,' '), 1,' ') --재료외형구분
						     ||RPAD(NVL(STL_NO                 ,' '),11,' ') --재료번호
						     ||RPAD(NVL(YD_STR_LOC             ,' '), 8,' ') --야드저장위치
						     ||RPAD(NVL(YD_STK_LYR_NO          ,' '), 3,' ') --야드적치단번호
						     ||RPAD(NVL(YD_STL_WT              ,' '), 5,' ') --야드재료중량
						     ||RPAD(NVL(YD_STL_T               ,' '), 6,' ') --야드재료두께
						     ||RPAD(NVL(YD_STL_W               ,' '), 5,' ') --야드재료폭
						     ||RPAD(NVL(YD_STL_L               ,' '), 7,' ') --야드재료길이
						     ||RPAD(NVL(MAT_ODIA               ,' '), 5,' ') --재료외경
						     ||RPAD(NVL(MAT_IDIA               ,' '), 5,' ') --재료내경
						     ||RPAD(NVL(STLKIND_CD             ,' '), 3,' ') --강종
						     ||RPAD(NVL(SPEC_ABBSYM            ,' '),15,' ') --규격약호
						     ||RPAD(NVL(YD_IPGO_DD             ,' '),14,' ') --야드입고일자
						     ||RPAD(NVL(PLNT_PROC_CD           ,' '), 3,' ') --공장공정코드
						     ||RPAD(NVL(CURR_PROG_CD           ,' '), 1,' ') --현재진도코드
						     ||RPAD(NVL(ORD_YEOJAE_GP          ,' '), 1,' ') --주문여재구분
						     ||RPAD(NVL(ORD_NO                 ,' '),10,' ') --주문번호
						     ||RPAD(NVL(ORD_DTL                ,' '), 3,' ') --주문행번
						     ||RPAD(NVL(BUY_SLAB_NO            ,' '),30,' ') --구입SLAB번호
						     ||RPAD(NVL(SLAB_WO_RT_CD          ,' '), 2,' ') --SLAB지시행선코드
						     ||RPAD(NVL(ORD_HCR_GP             ,' '), 1,' ') --설계HCR구분
						     ||RPAD(NVL(HCR_GP                 ,' '), 1,' ') --HCR구분
						     ||RPAD(NVL(CC_MC_CD               ,' '), 1,' ') --연주Machine코드
						     ||RPAD(NVL(SCARFING_YN            ,' '), 1,' ') --SCARFING여부
						     ||RPAD(NVL(SCARFING_DONE_YN       ,' '), 1,' ') --SCARFING완료유무
						     ||RPAD(NVL(RPR_MTD                ,' '), 1,' ') --주편손질방법
						     ||RPAD(NVL(SCARFING_DEPTH         ,' '), 2,' ') --SCARFING깊이
						     ||RPAD(NVL(REHEAT_SLAB_GP         ,' '), 1,' ') --재열재구분
						     ||RPAD(NVL(PTOP_PLNT_GP           ,' '), 2,' ') --조업공장구분
						     ||RPAD(NVL(REFUR_CHG_LOT_NO       ,' '),10,' ') --가열로장입Lot번호
						     ||RPAD(NVL(CT_LOT_SCH_SERNO       ,' '),22,' ') --생산통제Lot스케줄일련번호
						     ||RPAD(NVL(FRTOMOVE_ORD_DATE      ,' '), 8,' ') --이송지시일자
						     ||RPAD(NVL(FRTOMOVE_PLANT_GP      ,' '), 2,' ') --이송공장구분
						     ||RPAD(NVL(URGENT_FRTOMOVE_WORD_GP,' '), 1,' ') --긴급이송작업지시구분
						     ||RPAD(NVL(HYSCO_TRANS_CLS        ,' '), 1,' ') --HYSCO운송구분
						     ||RPAD(NVL(APPEAR_GRADE           ,' '), 1,' ') --외관종합판정등급
						     ||RPAD(NVL(COOL_METHOD            ,' '), 1,' ') --권취코일냉각방법
						     ||RPAD(NVL(COOL_DONE_GP           ,' '), 1,' ') --냉각완료구분
						     ||RPAD(NVL(CONV_BRANCH_CD         ,' '), 2,' ') --야드Conveyor분기코드
						     ||RPAD(NVL(CUST_CD                ,' '), 6,' ') --고객코드
						     ||RPAD(NVL(DEST_CD                ,' '), 5,' ') --목적지코드
						     ||RPAD(NVL(DLVRDD_RULE_DD         ,' '), 8,' ') --납기기준일
						     ||RPAD(NVL(ITEMNAME_CD            ,' '), 3,' ') --품명코드
						     ||RPAD(NVL(OVERALL_STATAMP_GRADE  ,' '), 1,' ') --종합판정등급
						     ||RPAD(NVL(ORD_GP                 ,' '), 1,' ') --수주구분
						     ||RPAD(NVL(YD_STK_LOT_TP          ,' '), 2,' ') --야드산적LotType
						     ||RPAD(NVL(YD_STK_LOT_CD          ,' '),18,' ') --야드산적Lot코드
						     ||DECODE(JMS_TC_CD,'YDY3L002',RPAD(NVL(PL_MPL_NO,' '),10,' ')) --후판날판번호
						     ||RPAD(NVL(RCPT_SLAB_STAT          ,' '), 2,' ') --녹슨재 판단 
						       AS JMS_TC_MESSAGE --JMSTCMessage
						  FROM (SELECT 'YDY3L002' AS JMS_TC_CD
						              ,:V_YD_INFO_SYNC_CD                      AS YD_INFO_SYNC_CD
						              ,'001'                                   AS YD_STL_INFO_SND_SH
						              ,'001'                                   AS YD_STL_INFO_SND_CNT
						              ,ST.STL_APPEAR_GP
						              ,SS.STL_NO
						              ,SL.YD_STK_COL_GP || SL.YD_STK_BED_NO    AS YD_STR_LOC
						              ,SL.YD_STK_LYR_NO
						              ,TO_CHAR(ST.SLAB_WT,'FM00000'  )       AS YD_STL_WT
						              ,TO_CHAR(ST.SLAB_T ,'FM000V000')       AS YD_STL_T
						              ,TO_CHAR(ST.SLAB_W ,'FM0000V0' )       AS YD_STL_W
						              ,TO_CHAR(ST.SLAB_LEN ,'FM0000000')       AS YD_STL_L
						              ,'00000'                                 AS MAT_ODIA
						              ,'00000'                                 AS MAT_IDIA
						              ,SS.STLKIND_CD
						              ,ST.SPEC_ABBSYM
						              ,TO_CHAR(ST.REG_DDTT,'YYYYMMDDHH24MISS') AS YD_IPGO_DD
						              ,ST.PLNT_PROC_CD
						              ,ST.CURR_PROG_CD                          AS CURR_PROG_CD
						              ,ST.ORD_YEOJAE_GP
						              ,ST.ORD_NO
						              ,ST.ORD_DTL
						              ,SS.BUY_SLAB_NO
						              ,ST.SLAB_WO_RT_CD
						              ,ST.ORD_HCR_GP
						              ,ST.HCR_GP
						              ,TO_CHAR(SS.CC_CCM_NO)                   AS CC_MC_CD
						              ,ST.SCARFING_YN
						              ,ST.SCARFING_DONE_YN
						              ,ST.WO_MSLAB_RPR_MTD                     AS RPR_MTD
						              ,ST.SCARFING_DEPTH
						              ,ST.REHEAT_SLAB_GP
						              ,ST.PTOP_PLNT_GP
						              ,SS.REFUR_CHG_LOT_NO
						              ,DECODE(SUBSTR(SS.SLAB_WO_RT_CD,1,1),'P',SS.YD_CHG_NO,TO_CHAR(SS.REFUR_CHG_PLN_SERNO)) AS CT_LOT_SCH_SERNO
						              ,SS.FRTOMOVE_ORD_DATE
						              ,SS.FRTOMOVE_PLANT_GP
						              --,SS.URGENT_FRTOMOVE_WORD_GP
						              ,(CASE WHEN NVL(SL.YD_STK_COL_GP,TP.V_YD_GP) LIKE 'D%' AND  --//후판슬라브야드
						                       (SELECT max(PL_PLN_WO_GP)             --//압연지시 예정대상재
						                        FROM USRCTA.TB_CT_M_PLMPLSPEC X
						                        WHERE X.STL_NO LIKE SS.STL_NO||'%' 
						                        AND X.CT_MILL_SCH_WRK_STAT_GP IN ('3','4')
						                        )='Y'
						                THEN 'Y' ELSE SS.URGENT_FRTOMOVE_WORD_GP END ) AS URGENT_FRTOMOVE_WORD_GP
						              ,' '                                     AS HYSCO_TRANS_CLS
						              ,' '                                     AS APPEAR_GRADE
						              ,' '                                     AS COOL_METHOD
						              ,' '                                     AS COOL_DONE_GP
						              ,' '                                     AS CONV_BRANCH_CD
						              ,SS.CUST_CD
						              ,SS.DEST_CD
						              ,SS.YD_DLVRDD_RULE_DD                    AS DLVRDD_RULE_DD
						              ,ST.ITEMNAME_CD
						              ,ST.OVERALL_STAMP_GRADE                  AS OVERALL_STATAMP_GRADE
						              ,SS.ORD_GP
						              ,SS.YD_STK_LOT_TP
						              ,SS.YD_STK_LOT_CD
						              ,SS.PL_MPL_NO
						              ,(SELECT STLKIND_CD
										FROM USRPTA.TB_PT_HEATCOMM
										WHERE HEAT_NO IN ( SELECT HEAT_NO FROM USRPTA.TB_PT_SLABCOMM
										                   SLAB_NO =  :V_STL_NO)
										  AND ROWNUM = 1) AS STLKIND_CD2 
									  ,'' AS RCPT_SLAB_STAT // 녹슨재
						          FROM VW_YD_SLABCOMM  ST
						              ,(SELECT STL_NO
						                      ,YD_STK_COL_GP
						                      ,YD_STK_BED_NO
						                      ,YD_STK_LYR_NO
						                  FROM TB_YD_STKLYR SL
						                 WHERE STL_NO = :V_STL_NO
						                   AND YD_STK_LYR_MTL_STAT IN ('C','U')
						                   AND ROWNUM = 1) SL
						               , TB_YD_STOCK SS  
						               , TEMP_PARAM TP
						         WHERE SS.STL_NO = SL.STL_NO(+)
						           AND SS.STL_NO = :V_STL_NO
						           AND SS.STL_NO =ST.SLAB_NO
						           )
					 */
					/*
					 * SELECT PL_MPL_NO -- 날판번호 : 압연지시 확정시 연주 테이블에서 STOCK 테이블에 데아터 업데이트 실행
					              FROM USRCTA.TB_CT_N_PLMPLWO
					             WHERE STL_NO = :V_STL_NO
					               AND CT_MILL_SPEC_WRK_STAT_GP >= '3'
					 */
				}
			
			} else //-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^
			if ("YDY3L002DnWr".equals(msgId)) {
				trtNm = "저장품제원(권하실적)";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDY3L002DnWr";
				/*
				 * 
				 --저장품제원(권하실적) 전문조회 -
					SELECT JMS_TC_CD                                     --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,JMS_TC_CD                                     --전문ID
					     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')       --생성일시
					     ||'I'                                           --전문구분
					     ||'0259'   --전문길이
					     ||RPAD(' ',29,' ')                              --임시
					     ||RPAD(NVL(YD_INFO_SYNC_CD        ,' '), 1,' ') --야드정보동기화코드
					     ||TO_CHAR(COUNT(*) OVER (),'FM000')             --야드재료정보송신매수
					     ||TO_CHAR(ROWNUM          ,'FM000')             --야드재료정보송신순번
					     ||RPAD(NVL(STL_APPEAR_GP          ,' '), 1,' ') --재료외형구분
					     ||RPAD(NVL(STL_NO                 ,' '),11,' ') --재료번호
					     ||RPAD(NVL(YD_STR_LOC             ,' '), 8,' ') --야드저장위치
					     ||RPAD(NVL(YD_STK_LYR_NO          ,' '), 3,' ') --야드적치단번호
					     ||RPAD(NVL(YD_STL_WT              ,' '), 5,' ') --야드재료중량
					     ||RPAD(NVL(YD_STL_T               ,' '), 6,' ') --야드재료두께
					     ||RPAD(NVL(YD_STL_W               ,' '), 5,' ') --야드재료폭
					     ||RPAD(NVL(YD_STL_L               ,' '), 7,' ') --야드재료길이
					     ||RPAD(NVL(MAT_ODIA               ,' '), 5,' ') --재료외경
					     ||RPAD(NVL(MAT_IDIA               ,' '), 5,' ') --재료내경
					     ||RPAD(NVL(STLKIND_CD             ,' '), 3,' ') --강종
					     ||RPAD(NVL(SPEC_ABBSYM            ,' '),15,' ') --규격약호
					     ||RPAD(NVL(YD_IPGO_DD             ,' '),14,' ') --야드입고일자
					     ||RPAD(NVL(PLNT_PROC_CD           ,' '), 3,' ') --공장공정코드
					     ||RPAD(NVL(CURR_PROG_CD           ,' '), 1,' ') --현재진도코드
					     ||RPAD(NVL(ORD_YEOJAE_GP          ,' '), 1,' ') --주문여재구분
					     ||RPAD(NVL(ORD_NO                 ,' '),10,' ') --주문번호
					     ||RPAD(NVL(ORD_DTL                ,' '), 3,' ') --주문행번
					     ||RPAD(NVL(BUY_SLAB_NO            ,' '),30,' ') --구입SLAB번호
					     ||RPAD(NVL(SLAB_WO_RT_CD          ,' '), 2,' ') --SLAB지시행선코드
					     ||RPAD(NVL(ORD_HCR_GP             ,' '), 1,' ') --설계HCR구분
					     ||RPAD(NVL(HCR_GP                 ,' '), 1,' ') --HCR구분
					     ||RPAD(NVL(CC_MC_CD               ,' '), 1,' ') --연주Machine코드
					     ||RPAD(NVL(SCARFING_YN            ,' '), 1,' ') --SCARFING여부
					     ||RPAD(NVL(SCARFING_DONE_YN       ,' '), 1,' ') --SCARFING완료유무
					     ||RPAD(NVL(RPR_MTD                ,' '), 1,' ') --주편손질방법
					     ||RPAD(NVL(SCARFING_DEPTH         ,' '), 2,' ') --SCARFING깊이
					     ||RPAD(NVL(REHEAT_SLAB_GP         ,' '), 1,' ') --재열재구분
					     ||RPAD(NVL(PTOP_PLNT_GP           ,' '), 2,' ') --조업공장구분
					     ||RPAD(NVL(REFUR_CHG_LOT_NO       ,' '),10,' ') --가열로장입Lot번호
					     ||RPAD(NVL(CT_LOT_SCH_SERNO       ,' '),22,' ') --생산통제Lot스케줄일련번호
					     ||RPAD(NVL(FRTOMOVE_ORD_DATE      ,' '), 8,' ') --이송지시일자
					     ||RPAD(NVL(FRTOMOVE_PLANT_GP      ,' '), 2,' ') --이송공장구분
					     ||RPAD(NVL(URGENT_FRTOMOVE_WORD_GP,' '), 1,' ') --긴급이송작업지시구분
					     ||RPAD(NVL(HYSCO_TRANS_CLS        ,' '), 1,' ') --HYSCO운송구분
					     ||RPAD(NVL(APPEAR_GRADE           ,' '), 1,' ') --외관종합판정등급
					     ||RPAD(NVL(COOL_METHOD            ,' '), 1,' ') --권취코일냉각방법
					     ||RPAD(NVL(COOL_DONE_GP           ,' '), 1,' ') --냉각완료구분
					     ||RPAD(NVL(CONV_BRANCH_CD         ,' '), 2,' ') --야드Conveyor분기코드
					     ||RPAD(NVL(CUST_CD                ,' '), 6,' ') --고객코드
					     ||RPAD(NVL(DEST_CD                ,' '), 5,' ') --목적지코드
					     ||RPAD(NVL(DLVRDD_RULE_DD         ,' '), 8,' ') --납기기준일
					     ||RPAD(NVL(ITEMNAME_CD            ,' '), 3,' ') --품명코드
					     ||RPAD(NVL(OVERALL_STATAMP_GRADE  ,' '), 1,' ') --종합판정등급
					     ||RPAD(NVL(ORD_GP                 ,' '), 1,' ') --수주구분
					     ||RPAD(NVL(YD_STK_LOT_TP          ,' '), 2,' ') --야드산적LotType
					     ||RPAD(NVL(YD_STK_LOT_CD          ,' '),18,' ') --야드산적Lot코드
					     ||DECODE(JMS_TC_CD,'YDY3L002',RPAD(NVL(PL_MPL_NO,' '),10,' ')) --후판날판번호
					       AS JMS_TC_MESSAGE --JMSTCMessage
					  FROM (SELECT 'YDY3L002'                             AS JMS_TC_CD
					              ,'6'                                    AS YD_INFO_SYNC_CD --권하실적
					              ,ST.STL_APPEAR_GP
					              ,SL.STL_NO
					              ,SL.YD_STK_COL_GP || SL.YD_STK_BED_NO    AS YD_STR_LOC
					              ,SL.YD_STK_LYR_NO
					              ,TO_CHAR(ST.CAL_SLAB_WT,'FM00000'  )       AS YD_STL_WT
					              ,TO_CHAR(ST.REAL_MEASURE_SLAB_T ,'FM000V000')       AS YD_STL_T
					              ,TO_CHAR(ST.REAL_MEASURE_SLAB_W ,'FM0000V0' )       AS YD_STL_W
					              ,TO_CHAR(ST.REAL_MEASURE_SLAB_LEN ,'FM0000000')       AS YD_STL_L
					              ,'00000'                                 AS MAT_ODIA
					              ,'00000'                                 AS MAT_IDIA
					              ,ST.STLKIND_CD
					              ,ST.SPEC_ABBSYM
					              ,TO_CHAR(ST.REG_DDTT,'YYYYMMDDHH24MISS') AS YD_IPGO_DD
					              ,ST.PLNT_PROC_CD
					              ,ST.STL_PROG_CD                          AS CURR_PROG_CD
					              ,ST.ORD_YEOJAE_GP
					              ,ST.ORD_NO
					              ,ST.ORD_DTL
					              ,ST.BUY_SLAB_NO
					              ,ST.SLAB_WO_RT_CD
					              ,ST.ORD_HCR_GP
					              ,ST.HCR_GP
					              ,TO_CHAR(ST.CC_CCM_NO)                   AS CC_MC_CD
					              ,ST.SCARFING_YN
					              ,ST.SCARFING_DONE_YN
					              ,ST.WO_MSLAB_RPR_MTD                     AS RPR_MTD
					              ,ST.SCARFING_DEPTH
					              ,ST.REHEAT_SLAB_GP
					              ,ST.PTOP_PLNT_GP
					              ,ST.REFUR_CHG_LOT_NO
					              ,DECODE(SUBSTR(ST.SLAB_WO_RT_CD,1,1),'P',ST.YD_CHG_NO,TO_CHAR(ST.REFUR_CHG_PLN_SERNO)) AS CT_LOT_SCH_SERNO
					              ,ST.FRTOMOVE_ORD_DATE
					              ,ST.FRTOMOVE_PLANT_GP
					              ,ST.URGENT_FRTOMOVE_WORD_GP
					              ,' '                                     AS HYSCO_TRANS_CLS
					              ,' '                                     AS APPEAR_GRADE
					              ,' '                                     AS COOL_METHOD
					              ,' '                                     AS COOL_DONE_GP
					              ,' '                                     AS CONV_BRANCH_CD
					              ,ST.CUST_CD
					              ,ST.DEST_CD
					              ,ST.YD_DLVRDD_RULE_DD                    AS DLVRDD_RULE_DD
					              ,ST.ITEMNAME_CD
					              ,ST.OVERALL_STAMP_GRADE                  AS OVERALL_STATAMP_GRADE
					              ,ST.ORD_GP
					              ,ST.YD_STK_LOT_TP
					              ,ST.YD_STK_LOT_CD
					              ,ST.PL_MPL_NO
					          FROM TB_YD_CRNWRKMTL CM
					              --,TB_YD_STOCK     ST
					              ,(
					                    SELECT ST.*
					                         , SC.SLAB_LEN               -- 길이
					                         , SC.REAL_MEASURE_SLAB_LEN  -- 실측길이
					                         , SC.SLAB_T                 -- 두께
					                         , SC.REAL_MEASURE_SLAB_T    -- 실측두께
					                         , SC.SLAB_W                 -- 폭
					                         , SC.REAL_MEASURE_SLAB_W    -- 실측폭
					                         , SC.SLAB_WT                -- 중량
					                         , SC.CAL_SLAB_WT            -- 계산중량
					                      FROM USRYDA.TB_YD_STOCK    ST -- YD_저장품
					                         , USRPTA.TB_PT_SLABCOMM SC -- SLAB공통
					                     WHERE ST.STL_NO = SC.SLAB_NO
					                ) ST
					              ,TB_YD_STKLYR    SL
					         WHERE CM.STL_NO        = ST.STL_NO
					           AND CM.STL_NO        = SL.STL_NO
					           AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					           AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
					         ORDER BY CM.YD_STK_LYR_NO)
				 */
			
			} else //-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^
			if ("YDY3L003".equals(msgId)) {
				trtNm = "크레인작업계획";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDY3L003";

				/*
				 * --크레인작업계획(후판) 전문조회 - com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDY3L003
					SELECT JMS_TC_CD                               --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,JMS_TC_CD                               --전문ID
					     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS') --생성일시
					     ||'I'                                     --전문구분
					     ||'0062'                                  --전문길이
					     ||RPAD(' ',29,' ')                        --임시
					     ||RPAD(NVL(YD_INFO_SYNC_CD ,' '), 1,' ')  --야드정보동기화코드
					     ||TO_CHAR(COUNT(*) OVER () ,'FM000'    )  --야드재료정보송신매수
					     ||TO_CHAR(ROWNUM           ,'FM000'    )  --야드재료정보송신순번
					     ||RPAD(NVL(STL_NO          ,' '),11,' ')  --재료번호
					     ||RPAD(NVL(PTOP_PLNT_GP    ,' '), 2,' ')  --조업공장구분
					     ||RPAD(NVL(REFUR_CHG_LOT_NO,' '),10,' ')  --가열로장입Lot번호
					     ||RPAD(NVL(CT_LOT_SCH_SERNO,' '),22,' ')  --생산통제Lot스케줄일련번호
					     ||RPAD(NVL(PL_MPL_NO       ,' '),10,' ')  --후판날판번호
					       AS JMS_TC_MESSAGE --JMSTCMessage
					  FROM (SELECT 'YDY3L003'                                AS JMS_TC_CD
					              ,DECODE(PO.PTOP_PLNT_GP,'PA','P','PB','Q') AS YD_INFO_SYNC_CD
					              ,PO.STL_NO
					              ,PO.PTOP_PLNT_GP
					              ,PO.REFUR_CHG_LOT_NO
					              ,PO.YD_CHG_NO                              AS CT_LOT_SCH_SERNO
					              ,PO.PL_MPL_NO
					          FROM TB_CT_N_PLMPLWO   PO
					              ,TB_CT_M_PLMPLSPEC PS
					              ,(SELECT MO.PTOP_PLNT_GP
					                      ,MO.CHG_WO_FR_PNT
					                      ,MO.CHG_WO_TO_PNT
					                  FROM TB_CT_J_MILLWOIDX MO
					                 WHERE MO.CT_RCV_SEQ = (SELECT --/*+ INDEX_DESC(MI OK_CT_J_MILLWOIDX) --/
					                                               MI.CT_RCV_SEQ
					                                          FROM TB_CT_J_MILLWOIDX MI
					                                         WHERE MI.PTOP_PLNT_GP = MO.PTOP_PLNT_GP
					                                           AND ROWNUM = 1)
					                   AND MO.PTOP_PLNT_GP = :V_PTOP_PLNT_GP) MO
					         WHERE PO.PTOP_PLNT_GP = MO.PTOP_PLNT_GP
					           AND PO.STL_NO       = PS.STL_NO
					           AND PO.REFUR_CHG_PLN_SERNO BETWEEN MO.CHG_WO_FR_PNT AND MO.CHG_WO_TO_PNT
					           AND PO.CT_MILL_SPEC_WRK_STAT_GP >= '3'
					           AND PS.CT_MILL_SCH_WRK_STAT_GP IN ('3','4','5','A','B','C','D')
					         ORDER BY PO.REFUR_CHG_PLN_SERNO)

				 */
				
			
			} else 
			if ("YDY3L004".equals(msgId)) {  
				trtNm = "크레인작업지시";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDY3L004";
				/*
				 /*크레인작업지시 전문조회
					SELECT CS.JMS_TC_CD                                    --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,CS.JMS_TC_CD                                    --전문ID
					     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')         --생성일시
					     ||NVL(:V_MSG_GP,'I')                              --전문구분
					     ||'0704'                                          --전문길이
					     ||RPAD(' ',29,' ')                                --임시
					     ||RPAD(NVL(CS.YD_EQP_ID             ,' '), 6,' ') --야드설비ID
					     ||RPAD(NVL(CS.YD_WRK_PROG_STAT      ,' '), 1,' ') --야드작업진행상태
					     ||RPAD(NVL(CS.YD_SCH_CD             ,' '), 8,' ') --야드스케쥴코드
					     ||RPAD(NVL(CS.YD_SCH_NAME           ,' '),30,' ') --야드스케쥴명
					     ||RPAD(NVL(CS.YD_CRN_SCH_ID         ,' '),18,' ') --야드크레인스케쥴ID
					     ||LPAD(NVL(CS.YD_CRN_WRK_SH         ,'0'), 2,'0') --야드크레인작업매수
					     ||RPAD(NVL(CS.YD_CRN_WRK_WT         ,' '), 7,' ') --야드크레인작업중량
					     ||RPAD(NVL(CS.YD_CRN_WRK_T          ,' '), 7,' ') --야드크레인작업총두께
					     ||RPAD(NVL(CS.YD_CRN_WRK_MAX_W      ,' '), 5,' ') --야드크레인작업최대폭
					     ||RPAD(NVL(CS.YD_CRN_WRK_MAX_L      ,' '), 7,' ') --야드크레인작업최대길이
					     ||LPAD(NVL(NS.YD_CRN_SCH_RMD_CNT    ,'0'), 2,'0') --야드크레인스케줄잔여회수
					     ||RPAD(NVL(CS.YD_UP_WO_LOC          ,' '), 8,' ') --야드권상지시위치
					     ||RPAD(NVL(CS.YD_UP_WO_LAYER_NEW        ,' '), 3,' ') --야드권상지시단
					     ||RPAD(NVL(CS.YD_UP_WO_LOC_XAXIS    ,' '), 7,' ') --야드권상지시X축
					     ||RPAD(NVL(CS.YD_UP_WO_XAXIS_GAP_MAX,' '), 5,' ') --야드권상지시X축오차최대
					     ||RPAD(NVL(CS.YD_UP_WO_XAXIS_GAP_MIN,' '), 5,' ') --야드권상지시X축오차최소
					     ||RPAD(NVL(CS.YD_UP_WO_LOC_YAXIS    ,' '), 5,' ') --야드권상지시Y축
					     ||RPAD(NVL(CS.YD_UP_WO_YAXIS_GAP_MAX,' '), 5,' ') --야드권상지시Y축오차최대
					     ||RPAD(NVL(CS.YD_UP_WO_YAXIS_GAP_MIN,' '), 5,' ') --야드권상지시Y축오차최소
					     ||RPAD(NVL(CS.YD_UP_WO_LOC_ZAXIS    ,' '), 5,' ') --야드권상지시Z축
					     ||RPAD(NVL(CS.YD_UP_WO_ZAXIS_GAP_MAX,' '), 5,' ') --야드권상지시Z축오차최대
					     ||RPAD(NVL(CS.YD_UP_WO_ZAXIS_GAP_MIN,' '), 5,' ') --야드권상지시Z축오차최소
					     ||RPAD(NVL(CS.YD_DN_WO_LOC          ,' '), 8,' ') --야드권하지시위치
					     ||RPAD(NVL(CS.YD_DN_WO_LAYER        ,' '), 3,' ') --야드권하지시단
					     ||RPAD(NVL(CS.YD_DN_WO_LOC_XAXIS    ,' '), 7,' ') --야드권하지시X축
					     ||RPAD(NVL(CS.YD_DN_WO_XAXIS_GAP_MAX,' '), 5,' ') --야드권하지시X축오차최대
					     ||RPAD(NVL(CS.YD_DN_WO_XAXIS_GAP_MIN,' '), 5,' ') --야드권하지시X축오차최소
					     ||RPAD(NVL(CS.YD_DN_WO_LOC_YAXIS    ,' '), 5,' ') --야드권하지시Y축
					     ||RPAD(NVL(CS.YD_DN_WO_YAXIS_GAP_MAX,' '), 5,' ') --야드권하지시Y축오차최대
					     ||RPAD(NVL(CS.YD_DN_WO_YAXIS_GAP_MIN,' '), 5,' ') --야드권하지시Y축오차최소
					     ||RPAD(NVL(CS.YD_DN_WO_LOC_ZAXIS    ,' '), 5,' ') --야드권하지시Z축
					     ||RPAD(NVL(CS.YD_DN_WO_ZAXIS_GAP_MAX,' '), 5,' ') --야드권하지시Z축오차최대
					     ||RPAD(NVL(CS.YD_DN_WO_ZAXIS_GAP_MIN,' '), 5,' ') --야드권하지시Z축오차최소
					     ||RPAD(NVL(CS.YD_EQP_ID2            ,' '), 6,' ') --야드설비ID2
					     ||RPAD(NVL(CS.YD_TC_AIM_BAY_GP      ,' '), 1,' ') --야드대차목적동
					     ||RPAD(NVL(CS.YD_CAR_USE_GP         ,' '), 1,' ') --야드차량사용구분
					     ||RPAD(NVL(CS.CAR_NO                ,' '),15,' ') --차량번호
					     ||RPAD(NVL(CS.TRN_EQP_CD            ,' '), 8,' ') --운송장비코드
					     ||RPAD(NVL(CS.YD_EQP_WRK_SH         ,' '), 2,' ') --야드설비작업매수
					     ||RPAD(NVL(CS.YD_EQP_RMN_SH         ,' '), 2,' ') --야드설비잔량매수
					     ||RPAD(NVL(CM.STL_NO1               ,' '),11,' ') --재료번호1
					     ||RPAD(NVL(CM.YD_STL_WT1            ,' '), 5,' ') --야드재료중량1
					     ||RPAD(NVL(CM.YD_STL_T1             ,' '), 6,' ') --야드재료두께1
					     ||RPAD(NVL(CM.YD_STL_W1             ,' '), 5,' ') --야드재료폭1
					     ||RPAD(NVL(CM.YD_STL_L1             ,' '), 7,' ') --야드재료길이1
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO1)       ,' '), 1,' ') --야드재료고강도구분1/압연지시1
					     ||RPAD(NVL(CM.STL_NO2               ,' '),11,' ') --재료번호2
					     ||RPAD(NVL(CM.YD_STL_WT2            ,' '), 5,' ') --야드재료중량2
					     ||RPAD(NVL(CM.YD_STL_T2             ,' '), 6,' ') --야드재료두께2
					     ||RPAD(NVL(CM.YD_STL_W2             ,' '), 5,' ') --야드재료폭2
					     ||RPAD(NVL(CM.YD_STL_L2             ,' '), 7,' ') --야드재료길이2
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO2)       ,' '), 1,' ') --야드재료고강도구분2/압연지시2
					     ||RPAD(NVL(CM.STL_NO3               ,' '),11,' ') --재료번호3
					     ||RPAD(NVL(CM.YD_STL_WT3            ,' '), 5,' ') --야드재료중량3
					     ||RPAD(NVL(CM.YD_STL_T3             ,' '), 6,' ') --야드재료두께3
					     ||RPAD(NVL(CM.YD_STL_W3             ,' '), 5,' ') --야드재료폭3
					     ||RPAD(NVL(CM.YD_STL_L3             ,' '), 7,' ') --야드재료길이3
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO3)       ,' '), 1,' ') --야드재료고강도구분3/압연지시3
					     ||RPAD(NVL(CM.STL_NO4               ,' '),11,' ') --재료번호4
					     ||RPAD(NVL(CM.YD_STL_WT4            ,' '), 5,' ') --야드재료중량4
					     ||RPAD(NVL(CM.YD_STL_T4             ,' '), 6,' ') --야드재료두께4
					     ||RPAD(NVL(CM.YD_STL_W4             ,' '), 5,' ') --야드재료폭4
					     ||RPAD(NVL(CM.YD_STL_L4             ,' '), 7,' ') --야드재료길이4
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO4)       ,' '), 1,' ') --야드재료고강도구분4/압연지시4
					     ||RPAD(NVL(CM.STL_NO5               ,' '),11,' ') --재료번호5
					     ||RPAD(NVL(CM.YD_STL_WT5            ,' '), 5,' ') --야드재료중량5
					     ||RPAD(NVL(CM.YD_STL_T5             ,' '), 6,' ') --야드재료두께5
					     ||RPAD(NVL(CM.YD_STL_W5             ,' '), 5,' ') --야드재료폭5
					     ||RPAD(NVL(CM.YD_STL_L5             ,' '), 7,' ') --야드재료길이5
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO5)       ,' '), 1,' ') --야드재료고강도구분5/압연지시5
					     ||RPAD(NVL(CM.STL_NO6               ,' '),11,' ') --재료번호6
					     ||RPAD(NVL(CM.YD_STL_WT6            ,' '), 5,' ') --야드재료중량6
					     ||RPAD(NVL(CM.YD_STL_T6             ,' '), 6,' ') --야드재료두께6
					     ||RPAD(NVL(CM.YD_STL_W6             ,' '), 5,' ') --야드재료폭6
					     ||RPAD(NVL(CM.YD_STL_L6             ,' '), 7,' ') --야드재료길이6
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO6)       ,' '), 1,' ') --야드재료고강도구분6/압연지시6
					     ||RPAD(NVL(CM.STL_NO7               ,' '),11,' ') --재료번호7
					     ||RPAD(NVL(CM.YD_STL_WT7            ,' '), 5,' ') --야드재료중량7
					     ||RPAD(NVL(CM.YD_STL_T7             ,' '), 6,' ') --야드재료두께7
					     ||RPAD(NVL(CM.YD_STL_W7             ,' '), 5,' ') --야드재료폭7
					     ||RPAD(NVL(CM.YD_STL_L7             ,' '), 7,' ') --야드재료길이7
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO7)       ,' '), 1,' ') --야드재료고강도구분7/압연지시7
					     ||RPAD(NVL(CM.STL_NO8               ,' '),11,' ') --재료번호8
					     ||RPAD(NVL(CM.YD_STL_WT8            ,' '), 5,' ') --야드재료중량8
					     ||RPAD(NVL(CM.YD_STL_T8             ,' '), 6,' ') --야드재료두께8
					     ||RPAD(NVL(CM.YD_STL_W8             ,' '), 5,' ') --야드재료폭8
					     ||RPAD(NVL(CM.YD_STL_L8             ,' '), 7,' ') --야드재료길이8
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO8)       ,' '), 1,' ') --야드재료고강도구분8/압연지시8
					     ||RPAD(NVL(CM.STL_NO9               ,' '),11,' ') --재료번호9
					     ||RPAD(NVL(CM.YD_STL_WT9            ,' '), 5,' ') --야드재료중량9
					     ||RPAD(NVL(CM.YD_STL_T9             ,' '), 6,' ') --야드재료두께9
					     ||RPAD(NVL(CM.YD_STL_W9             ,' '), 5,' ') --야드재료폭9
					     ||RPAD(NVL(CM.YD_STL_L9             ,' '), 7,' ') --야드재료길이9
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO9)       ,' '), 1,' ') --야드재료고강도구분9/압연지시9
					     ||RPAD(NVL(CM.STL_NO10              ,' '),11,' ') --재료번호10
					     ||RPAD(NVL(CM.YD_STL_WT10           ,' '), 5,' ') --야드재료중량10
					     ||RPAD(NVL(CM.YD_STL_T10            ,' '), 6,' ') --야드재료두께10
					     ||RPAD(NVL(CM.YD_STL_W10            ,' '), 5,' ') --야드재료폭10
					     ||RPAD(NVL(CM.YD_STL_L10            ,' '), 7,' ') --야드재료길이10
					     ||RPAD(NVL(TO_CHAR(CM.CI_MO10)      ,' '), 1,' ') --야드재료고강도구분10/압연지시10
					     --||RPAD(NVL(NS.YD_SCH_CD_NEXT        ,' '), 8,' ') --야드스케쥴코드_Next [C연주 주여구분항목 셋팅]
					     ||RPAD(NVL(DECODE(SUBSTR(CS.YD_EQP_ID,1,1),'A',CM.OP1||CM.OP2||CM.OP3||CM.OP4||CM.OP5,'M',CM.OP1||CM.OP2||CM.OP3||CM.OP4||CM.OP5,'D',NS.YD_SCH_CD_NEXT),' '), 8,' ') --야드스케쥴코드_Next [C연주 주여구분항목 셋팅]
					     ||RPAD(NVL(NS.YD_SCH_NAME_NEXT      ,' '),30,' ') --야드스케쥴명_NEXT
					     ||RPAD(NVL(NS.YD_UP_WO_LOC_NEXT     ,' '), 8,' ') --야드권상지시위치_Next
					     ||RPAD(NVL(NS.YD_UP_WO_LAYER_NEXT   ,' '), 3,' ') --야드권상지시단_Next
					     ||RPAD(NVL(NS.YD_DN_WO_LOC_NEXT     ,' '), 8,' ') --야드권하지시위치_Next
					     ||RPAD(NVL(NS.YD_DN_WO_LAYER_NEXT   ,' '), 3,' ') --야드권하지시단_Next
					     --||RPAD(NVL(NS.STL_NO_NEXT           ,' '),11,' ') --재료번호_Next       [C연주 행선항목 셋팅]
					     ||RPAD(NVL(DECODE(SUBSTR(CS.YD_EQP_ID,1,1),'A',CM.YP1||CM.YP2||CM.YP3||CM.YP4||CM.YP5,'M',CM.YP1||CM.YP2||CM.YP3||CM.YP4||CM.YP5,'D',NS.STL_NO_NEXT),' '), 11,' ') --재료번호_Next       [C연주 행선항목 셋팅]
					     ||RPAD(NVL(NS.YD_CRN_WRK_SH_NEXT    ,' '), 2,' ') --야드크레인작업매수_Next
					     ||RPAD(NVL(NS.YD_CRN_WRK_WT_NEXT    ,' '), 7,' ') --야드크레인작업중량_Next
					     ||RPAD(NVL(DECODE(NS.YD_CRN_WRK_SH_NEXT,'','',TM.YD_MTL_T)              ,' '), 7,' ') --야드크레인작업총두께_Next
					     ||RPAD(NVL(DECODE(NS.YD_CRN_WRK_SH_NEXT,'','',TM.YD_MTL_W)              ,' '), 5,' ') --야드크레인작업최대폭_Next
					     ||RPAD(NVL(DECODE(NS.YD_CRN_WRK_SH_NEXT,'','',TM.YD_MTL_L)              ,' '), 7,' ') --야드크레인작업최대길이_Next
					     ||RPAD(NVL(:V_YD_WRK_MAX_SLAB_H     ,' '), 5,' ') --야드작업최대SLAB높이
					     ||RPAD(NVL(:V_YD_WRK_MAX_SLAB_H_NEXT,' '), 5,' ') --야드작업최대SLAB높이_Next
					     ||RPAD(NVL(CS.END_INST_TP                      ,' '), 1,' ') --마지막지시구분(연결작업이 있으면 N 없으면 Y)
					     AS JMS_TC_MESSAGE --JMSTCMessage
					  FROM ( SELECT      CUR_IF.JMS_TC_CD
					              ,CUR_IF.YD_EQP_ID              
					              ,CUR_IF.YD_WRK_PROG_STAT
					              ,CUR_IF.YD_SCH_CD
					              ,CUR_IF.YD_SCH_NAME
					              ,CUR_IF.YD_CRN_SCH_ID
					              ,CUR_IF.YD_CRN_WRK_SH
					              ,CUR_IF.YD_CRN_WRK_WT
					              ,CUR_IF.YD_CRN_WRK_T
					              ,CUR_IF.YD_CRN_WRK_MAX_W
					              ,CUR_IF.YD_CRN_WRK_MAX_L
					              ,CUR_IF.YD_UP_WO_LOC
					              ,CUR_IF.YD_UP_WO_LAYER_BEFORE--권상전 최상레이어(권상후 최상단 레이어로 변경 아래 컬럼으로 사용)
					              ,CUR_IF.YD_UP_WO_LAYER_NEW
					              ,CASE WHEN  CUR_IF.DTL_ITEM6 = 'Y' AND  CUR_IF.DTL_ITEM5 = 'Y'   THEN  TO_CHAR(NVL(CUR_IF.UP_XAXIS_LOC ,0),'FM0000000') 
					                    ELSE TO_CHAR(DECODE(SUBSTR(CUR_IF.YD_UP_WO_LOC,3,2) ,'PT' ,CUR_IF.UP_XAXIS_LOC, CUR_IF.YD_UP_WO_LOC_XAXIS) , 'FM0000000') END    YD_UP_WO_LOC_XAXIS 
					             
					              ,CUR_IF.YD_UP_WO_XAXIS_GAP_MAX
					              ,CUR_IF.YD_UP_WO_XAXIS_GAP_MIN
					              --,CUR_IF.YD_UP_WO_LOC_YAXIS
					              ,CASE WHEN  CUR_IF.DTL_ITEM6 = 'Y' AND  CUR_IF.DTL_ITEM5 = 'Y'  THEN TO_CHAR(NVL(CUR_IF.UP_YAXIS_LOC ,0),'FM00000') 
					                    ELSE TO_CHAR( DECODE(SUBSTR(CUR_IF.YD_UP_WO_LOC,3,2) ,'PT' ,CUR_IF.UP_YAXIS_LOC, CUR_IF.YD_UP_WO_LOC_YAXIS) , 'FM00000') END    YD_UP_WO_LOC_YAXIS 
					                    
					              ,CUR_IF.YD_UP_WO_YAXIS_GAP_MAX
					              ,CUR_IF.YD_UP_WO_YAXIS_GAP_MIN
					              --,TO_CHAR(CUR_IF.UP_BASE_SLAB_T+CUR_IF.YD_UP_WO_LOC_ZAXIS+CUR_IF.UP_MTL_SLAB_T,'FM00000')  AS  YD_UP_WO_LOC_ZAXIS
					              ,CASE WHEN  (CUR_IF.DTL_ITEM6 = 'Y'  AND  CUR_IF.DTL_ITEM5 = 'Y' ) OR  SUBSTR(CUR_IF.YD_UP_WO_LOC,3,2) = 'PT'  THEN TO_CHAR(NVL(CUR_IF.UP_BASE_SLAB_T+(CUR_IF.UP_MTL_ALL_SLAB_T-CUR_IF.CRN_WRK_ALL_T) ,0),'FM00000') 
					                    ELSE TO_CHAR(CUR_IF.UP_BASE_SLAB_T+(CUR_IF.UP_MTL_ALL_SLAB_T-CUR_IF.CRN_WRK_ALL_T)+CUR_IF.CRN_WRK_SLAB_T03,'FM00000')   END    YD_UP_WO_LOC_ZAXIS 
					              
					              ,CUR_IF.YD_UP_WO_ZAXIS_GAP_MAX
					              ,CUR_IF.YD_UP_WO_ZAXIS_GAP_MIN
					              ,CUR_IF.YD_DN_WO_LOC 
					              ,CUR_IF.YD_DN_WO_LAYER_BEFORE--권하전 최상레이어(권하후 최상 레이어로 변경 아래 컬럼으로 사용)
					              ,CUR_IF.YD_DN_WO_LAYER
					              --,CUR_IF.YD_DN_WO_LOC_XAXIS
					              ,CASE WHEN  CUR_IF.DTL_ITEM6 = 'Y' AND  CUR_IF.DTL_ITEM5 = 'Y'    THEN  TO_CHAR(NVL(CUR_IF.DTL_ITEM1 ,0),'FM0000000') 
					                    ELSE TO_CHAR(DECODE(SUBSTR(CUR_IF.YD_DN_WO_LOC,3,2) ,'PT' ,CUR_IF.UP_XAXIS_LOC, CUR_IF.YD_DN_WO_LOC_XAXIS), 'FM0000000' ) END    YD_DN_WO_LOC_XAXIS 
					              ,CUR_IF.YD_DN_WO_XAXIS_GAP_MAX
					              ,CUR_IF.YD_DN_WO_XAXIS_GAP_MIN
					              --,CUR_IF.YD_DN_WO_LOC_YAXIS
					              ,CASE WHEN  CUR_IF.DTL_ITEM6 = 'Y' AND  CUR_IF.DTL_ITEM5 = 'Y'    THEN  TO_CHAR(NVL(CUR_IF.DTL_ITEM2 ,0),'FM00000') 
					                    ELSE TO_CHAR(DECODE(SUBSTR(CUR_IF.YD_DN_WO_LOC,3,2) ,'PT' ,CUR_IF.UP_YAXIS_LOC, CUR_IF.YD_DN_WO_LOC_YAXIS), 'FM00000' ) END    YD_DN_WO_LOC_YAXIS 
					              ,CUR_IF.YD_DN_WO_YAXIS_GAP_MAX
					              ,CUR_IF.YD_DN_WO_YAXIS_GAP_MIN
					              ,CASE WHEN (CUR_IF.DTL_ITEM6 = 'Y'  AND  CUR_IF.DTL_ITEM5 = 'Y' ) OR SUBSTR(CUR_IF.YD_DN_WO_LOC,3,2) = 'PT'  THEN 
					                      TO_CHAR(NVL(CUR_IF.DN_BASE_SLAB_T+(CUR_IF.DN_MTL_ALL_SLAB_T) ,0),'FM00000')   
					                    ELSE 
					                      TO_CHAR(CUR_IF.DN_BASE_SLAB_T+(CUR_IF.DN_MTL_ALL_SLAB_T)+CUR_IF.CRN_WRK_SLAB_T03+CUR_IF.DN_MTL_SLAB_BND_T ,'FM00000')   END    YD_DN_WO_LOC_ZAXIS 
					              ,CUR_IF.YD_DN_WO_ZAXIS_GAP_MAX
					              ,CUR_IF.YD_DN_WO_ZAXIS_GAP_MIN
					              ,CUR_IF.YD_EQP_ID2
					              ,CUR_IF.YD_TC_AIM_BAY_GP
					              ,CUR_IF.YD_CAR_USE_GP
					              ,CUR_IF.CAR_NO
					              ,CUR_IF.TRN_EQP_CD
					              ,CUR_IF.YD_EQP_WRK_SH
					              ,CUR_IF.YD_EQP_RMN_SH
					              ,CUR_IF.YD_DN_MAX_LAYER  
					              ,CUR_IF.CRN_WRK_ALL_T
					              ,CUR_IF.CRN_WRK_SLAB_T03 --권상/권하 하단 마지막 재료 두께의 3/1 지점              
					              ,CUR_IF.DN_MTL_SLAB_T --권하 하단 마지막 재료 두께의 3/1 지점              
					              ,CUR_IF.UP_BASE_SLAB_T --권상베드기준 위치               
					              ,CUR_IF.DN_BASE_SLAB_T --권하베드기준 위치             
					              ,CUR_IF.DN_MTL_SLAB_BND_T  -- 권하위치 최상단 벤딩수치
					              ,CUR_IF.YD_TO_LOC_DCSN_MTD --야드To위치결정방법(S 면 마지막 스케줄)
					              ,CASE WHEN  CUR_IF.YD_TO_LOC_DCSN_MTD = 'S' THEN 'Y'
					                    ELSE 'N' END    END_INST_TP 
					      FROM (
					
					        SELECT 'YDY3L004' AS JMS_TC_CD
					              ,CS.YD_EQP_ID
					              , CASE WHEN CS.YD_L2_REQUEST_STAT = 'D' THEN '1' 
					                    WHEN CS.YD_L2_REQUEST_STAT = '5' AND CS.YD_WRK_PROG_STAT = '2'  THEN '5' --무인 권하위치변경시 권상전이면 1 권상 후면 5 보내줘야함 
					                    WHEN CS.YD_L2_REQUEST_STAT = '5' AND CS.YD_WRK_PROG_STAT != '2' THEN '1'
					                    ELSE DECODE(CS.YD_WRK_PROG_STAT,'S','1','W','1',CS.YD_WRK_PROG_STAT)  END 
					                                                               AS YD_WRK_PROG_STAT          --야드작업진행상태              
					              ,CS.YD_SCH_CD
					              ,SUBSTRB(SR.CD_CONTENTS,1,30)                    AS YD_SCH_NAME
					              ,CS.YD_CRN_SCH_ID
					              ,TO_CHAR(CS.YD_EQP_WRK_SH         ,'FM00'      ) AS YD_CRN_WRK_SH
					              ,TO_CHAR(CS.YD_EQP_WRK_WT         ,'FM0000000' ) AS YD_CRN_WRK_WT
					              ,TO_CHAR(CS.YD_EQP_WRK_T          ,'FM0000V000') AS YD_CRN_WRK_T
					              ,TO_CHAR(CS.YD_EQP_WRK_MAX_W      ,'FM0000V0'  ) AS YD_CRN_WRK_MAX_W
					              ,TO_CHAR(CS.YD_EQP_WRK_MAX_L      ,'FM0000000' ) AS YD_CRN_WRK_MAX_L
					              ,CS.YD_UP_WO_LOC
					              ,CS.YD_UP_WO_LAYER AS YD_UP_WO_LAYER_BEFORE--권상전 최상레이어(권상후 최상단 레이어로 변경 아래 컬럼으로 사용)
					              ,(SELECT MAX(YD_STK_LYR_NO) 
					                      FROM TB_YD_STKLYR AA 
					                    WHERE AA.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6) 
					                        AND AA.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2) 
					                        AND AA.YD_STK_LYR_MTL_STAT IN ('C','U') 
					               ) AS YD_UP_WO_LAYER_NEW
					              ,TO_CHAR(CS.YD_UP_WO_LOC_XAXIS    ,'FM0000000' ) AS YD_UP_WO_LOC_XAXIS
					              ,TO_CHAR(DECODE(SUBSTR(CS.YD_UP_WO_LOC,3,2) , 'PT', 5000 ,CS.YD_UP_WO_XAXIS_GAP_MAX ),'FM00000'   ) AS YD_UP_WO_XAXIS_GAP_MAX
					              ,TO_CHAR(DECODE(SUBSTR(CS.YD_UP_WO_LOC,3,2) , 'PT', 5000 ,CS.YD_UP_WO_XAXIS_GAP_MIN ),'FM00000'   ) AS YD_UP_WO_XAXIS_GAP_MIN
					              ,TO_CHAR(CS.YD_UP_WO_LOC_YAXIS    ,'FM00000'   ) AS YD_UP_WO_LOC_YAXIS
					              ,TO_CHAR(DECODE(SUBSTR(CS.YD_UP_WO_LOC,3,2) , 'PT', 5000 ,CS.YD_UP_WO_YAXIS_GAP_MAX ),'FM00000'   ) AS YD_UP_WO_YAXIS_GAP_MAX
					              ,TO_CHAR(DECODE(SUBSTR(CS.YD_UP_WO_LOC,3,2) , 'PT', 5000 ,CS.YD_UP_WO_YAXIS_GAP_MIN ),'FM00000'   ) AS YD_UP_WO_YAXIS_GAP_MIN
					              ,TO_CHAR(CS.YD_UP_WO_LOC_ZAXIS    ,'FM00000'   ) AS YD_UP_WO_LOC_ZAXIS
					              ,TO_CHAR(CS.YD_UP_WO_ZAXIS_GAP_MAX,'FM00000'   ) AS YD_UP_WO_ZAXIS_GAP_MAX
					              ,TO_CHAR(CS.YD_UP_WO_ZAXIS_GAP_MIN,'FM00000'   ) AS YD_UP_WO_ZAXIS_GAP_MIN
					              ,CS.YD_DN_WO_LOC 
					              ,CS.YD_DN_WO_LAYER AS YD_DN_WO_LAYER_BEFORE--권하전 최상레이어(권하후 최상 레이어로 변경 아래 컬럼으로 사용)
					              ,(SELECT MAX(YD_STK_LYR_NO) FROM TB_YD_STKLYR AA WHERE AA.YD_STK_COL_GP = SUBSTR(CS.YD_DN_WO_LOC,1,6) AND AA.YD_STK_BED_NO = SUBSTR(CS.YD_DN_WO_LOC,7,2) AND AA.YD_STK_LYR_MTL_STAT IN ('C','D') ) AS YD_DN_WO_LAYER
					              --,TO_CHAR(DECODE(FYN_UM.DTL_ITEM6 ,'Y', FYN_UM.DTL_ITEM1 ,CS.YD_DN_WO_LOC_XAXIS  )  ,'FM0000000' ) AS YD_DN_WO_LOC_XAXIS
					              
					              ,TO_CHAR(NVL(CS.YD_DN_WO_LOC_XAXIS ,0) , 'FM0000000')   YD_DN_WO_LOC_XAXIS 
					                    
					              ,TO_CHAR(DECODE(SUBSTR(CS.YD_DN_WO_LOC,3,2) , 'PT', 5000 ,CS.YD_DN_WO_XAXIS_GAP_MAX ),'FM00000'   ) AS YD_DN_WO_XAXIS_GAP_MAX
					              ,TO_CHAR(DECODE(SUBSTR(CS.YD_DN_WO_LOC,3,2) , 'PT', 5000 ,CS.YD_DN_WO_XAXIS_GAP_MIN ),'FM00000'   ) AS YD_DN_WO_XAXIS_GAP_MIN
					              --,TO_CHAR(DECODE(FYN_UM.DTL_ITEM6 ,'Y', FYN_UM.DTL_ITEM2 , CS.YD_DN_WO_LOC_YAXIS )   ,'FM00000'   ) AS YD_DN_WO_LOC_YAXIS
					              
					              ,TO_CHAR(NVL(CS.YD_DN_WO_LOC_YAXIS ,0) , 'FM00000')   YD_DN_WO_LOC_YAXIS 
					              ,SUBSTR(CS.YD_DN_WO_LOC,3,2)      
					              ,TO_CHAR(DECODE(SUBSTR(CS.YD_DN_WO_LOC,3,2) , 'PT', 5000 ,CS.YD_DN_WO_YAXIS_GAP_MAX ),'FM00000'   ) AS YD_DN_WO_YAXIS_GAP_MAX
					              ,TO_CHAR(DECODE(SUBSTR(CS.YD_DN_WO_LOC,3,2) , 'PT', 5000 ,CS.YD_DN_WO_YAXIS_GAP_MIN ),'FM00000'   ) AS YD_DN_WO_YAXIS_GAP_MIN
					              ,TO_CHAR(CS.YD_DN_WO_LOC_ZAXIS ,'FM00000'   ) AS YD_DN_WO_LOC_ZAXIS
					              ,TO_CHAR(CS.YD_DN_WO_ZAXIS_GAP_MAX,'FM00000'   ) AS YD_DN_WO_ZAXIS_GAP_MAX
					              ,TO_CHAR(CS.YD_DN_WO_ZAXIS_GAP_MIN,'FM00000'   ) AS YD_DN_WO_ZAXIS_GAP_MIN
					              ,SR.YD_ALT_CRN                                   AS YD_EQP_ID2
					              , CASE WHEN SUBSTR(CS.YD_SCH_CD,3,2) = 'TC' THEN WB.YD_AIM_BAY_GP 
					                    ELSE '' END    YD_TC_AIM_BAY_GP 
					              ,WB.YD_AIM_BAY_GP                                AS YD_TC_AIM_BAY_GP_BEFORE
					              ,WB.YD_CAR_USE_GP
					              ,WB.CAR_NO
					              ,WB.TRN_EQP_CD
					              ,' '                                             AS YD_EQP_WRK_SH
					              ,' '                                             AS YD_EQP_RMN_SH
					              ,(SELECT MAX(YD_STK_LYR_NO) FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = SUBSTR(CS.YD_DN_WO_LOC,0,6) AND YD_STK_BED_NO = SUBSTR(CS.YD_DN_WO_LOC,7,2) AND YD_STK_LYR_MTL_STAT IN('C','U')) AS YD_DN_MAX_LAYER
					               --권상 하단 마지막 재료 두께의 3/1 지점
					              , (SELECT NVL(SC.REAL_MEASURE_SLAB_T,SC.SLAB_T)*0.3 
					                      FROM TB_YD_STKLYR SL
					                         , VW_YD_SLABCOMM   SC 
					                         , TB_YD_STOCK      ST
					                     WHERE SL.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					                       AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					                       AND SL.STL_NO = SC.SLAB_NO 
					                       AND SL.YD_STK_LYR_NO = CS.YD_UP_WO_LAYER 
					                       AND SL.STL_NO    = ST.STL_NO
					                   )   AS CRN_WRK_SLAB_T03
					               --권상 재료 총두께 합(크레인 권상 재료 합)-권상/권하 시 동시 사용
					              , (SELECT SUM(NVL(SC.REAL_MEASURE_SLAB_T,SC.SLAB_T)) 
					                      FROM TB_YD_STKLYR SL
					                         , VW_YD_SLABCOMM   SC 
					                         , TB_YD_STOCK      ST
					                     WHERE SL.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					                       AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					                       AND SL.STL_NO = SC.SLAB_NO 
					                       AND SL.YD_STK_LYR_NO >= CS.YD_UP_WO_LAYER 
					                       AND SL.STL_NO    = ST.STL_NO
					                   )   AS CRN_WRK_ALL_T
					                   
					                --권상 베드 적치 재료 총두께
					              , (SELECT NVL(SUM(SC.REAL_MEASURE_SLAB_T),0) 
					                      FROM TB_YD_STKLYR SL
					                         , VW_YD_SLABCOMM   SC 
					                         , TB_YD_STOCK      ST
					                     WHERE SL.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					                       AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					                       AND SL.STL_NO = SC.SLAB_NO 
					                       AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
					                       AND SL.STL_NO IS NOT NULL
					                       AND SL.STL_NO    = ST.STL_NO
					                   )   AS UP_MTL_ALL_SLAB_T
					               --권상 형상 스캔 Z축 지점
					--               , CASE WHEN  NVL(FYN_UM.DTL_ITEM6 ,'N') = 'Y' AND SUBSTR(CS.YD_UP_WO_LOC,1,6) = 'DBPT01'  THEN 
					--                   (SELECT TO_CHAR(NVL(ST.WGT_CENTER_ZAXIS,0)    ,'FM00000' )  
					--                      FROM TB_YD_STKLYR SL
					--                         , VW_YD_SLABCOMM   SC 
					--                         , TB_YD_STOCK      ST
					--                     WHERE SL.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					--                       AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					--                       AND SL.STL_NO = SC.SLAB_NO 
					--                       AND SL.YD_STK_LYR_NO = CS.YD_UP_WO_LAYER
					--                       AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
					--                       AND SL.STL_NO    = ST.STL_NO
					--                   ) 
					--                    ELSE NVL(FYN_UM.DTL_ITEM3 ,0)  END    UP_MTL_SLAB_ZAXIS_T 
					                    
					              , (SELECT TO_CHAR(NVL(ST.WGT_CENTER_ZAXIS,0)    ,'FM00000' )  
					                      FROM TB_YD_STKLYR SL
					                         , VW_YD_SLABCOMM   SC 
					                         , TB_YD_STOCK      ST
					                     WHERE SL.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					                       AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					                       AND SL.STL_NO = SC.SLAB_NO 
					                       AND SL.YD_STK_LYR_NO = CS.YD_UP_WO_LAYER
					                       AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
					                       AND SL.STL_NO    = ST.STL_NO                  
					                )   AS UP_MTL_SLAB_ZAXIS_T
					                
					                 --권상 형상 스캔 X축 지점
					              , (SELECT TO_CHAR(NVL(ST.WGT_CENTER_XAXIS,0)     ,'FM0000000' )   
					                      FROM TB_YD_STKLYR SL
					                         , VW_YD_SLABCOMM   SC 
					                         , TB_YD_STOCK      ST
					                     WHERE SL.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					                       AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					                       AND SL.STL_NO = SC.SLAB_NO 
					                       AND SL.YD_STK_LYR_NO = '001'  
					                       AND SL.STL_NO    = ST.STL_NO
					                   )   AS UP_MTL_SLAB_XAXIS_T
					                
					               --권상 형상 스캔 Y축 지점
					              , (SELECT TO_CHAR(NVL(ST.WGT_CENTER_YAXIS,0)     ,'FM00000' )   
					                      FROM TB_YD_STKLYR SL
					                         , VW_YD_SLABCOMM   SC 
					                         , TB_YD_STOCK      ST
					                     WHERE SL.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					                       AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					                       AND SL.STL_NO = SC.SLAB_NO 
					                       AND SL.YD_STK_LYR_NO = '001' 
					                       AND SL.STL_NO    = ST.STL_NO
					                   )   AS UP_MTL_SLAB_YAXIS_T
					              --권하 하단 마지막 재료 두께의 3/1 지점
					              , (SELECT NVL(SC.REAL_MEASURE_SLAB_T,SC.SLAB_T)*0.3 
					                      FROM TB_YD_STKLYR SL
					                         , VW_YD_SLABCOMM   SC 
					                         , TB_YD_STOCK      ST
					                     WHERE SL.YD_STK_COL_GP = SUBSTR(CS.YD_DN_WO_LOC,1,6)
					                       AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_DN_WO_LOC,7,2)
					                       AND SL.STL_NO = SC.SLAB_NO 
					                       AND SL.YD_STK_LYR_NO = CS.YD_DN_WO_LAYER
					                       AND SL.STL_NO    = ST.STL_NO
					                   )   AS DN_MTL_SLAB_T
					               --권하 베드 재료 총두께
					              , (SELECT NVL(SUM(SC.REAL_MEASURE_SLAB_T) ,0)
					                      FROM TB_YD_STKLYR SL
					                         , VW_YD_SLABCOMM   SC 
					                         , TB_YD_STOCK      ST
					                     WHERE SL.YD_STK_COL_GP = SUBSTR(CS.YD_DN_WO_LOC,1,6)
					                       AND SL.YD_STK_BED_NO = SUBSTR(CS.YD_DN_WO_LOC,7,2)
					                       AND SL.STL_NO = SC.SLAB_NO
					                       AND SL.YD_STK_LYR_MTL_STAT IN ('C')
					                       AND SL.STL_NO IS NOT NULL
					                       AND SL.STL_NO    = ST.STL_NO
					                   )   AS DN_MTL_ALL_SLAB_T
					              --권상베드기준 위치
					              ,(SELECT NVL(YD_STK_BED_ZAXIS,0)
					                  FROM TB_YD_STKBED SK
					                 WHERE SK.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					                   AND SK.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					                   AND ROWNUM = 1
					               )                         AS UP_BASE_SLAB_T
					               
					               --권상베드기준 X축 위치
					              ,(SELECT NVL(YD_STK_BED_XAXIS,0)
					                  FROM TB_YD_STKBED SK
					                 WHERE SK.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					                   AND SK.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					                   AND ROWNUM = 1
					               )                         AS UP_XAXIS_LOC
					               --Y축 위치
					               ,(SELECT NVL(YD_STK_BED_YAXIS,0)
					                  FROM TB_YD_STKBED SK
					                 WHERE SK.YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,1,6)
					                   AND SK.YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2)
					                   AND ROWNUM = 1
					               )                         AS UP_YAXIS_LOC
					               --권하베드기준 위치
					               
					              ,(SELECT NVL(YD_STK_BED_ZAXIS,0)
					                              FROM TB_YD_STKBED SKK
					                             WHERE SKK.YD_STK_COL_GP = SUBSTR(CS.YD_DN_WO_LOC,1,6)
					                               AND SKK.YD_STK_BED_NO = SUBSTR(CS.YD_DN_WO_LOC,7,2)
					                               AND ROWNUM = 1
					                           )  
					                  AS DN_BASE_SLAB_T             
					                
					              -- 권하위치 최상단 벤딩수치
					              ,(SELECT NVL(CASE WHEN ST.BENDING_AXIS > 100 THEN 100
					                            ELSE ST.BENDING_AXIS END,0)
					                  FROM TB_YD_STKLYR SL
					                     , VW_YD_SLABCOMM   SC 
					                     , TB_YD_STOCK      ST
					                 WHERE SL.YD_STK_COL_GP = DECODE(CS.YD_L2_REQUEST_STAT,'5', SUBSTR(CS.YD_DN_WO_LOC_TO,1,6), SUBSTR(CS.YD_DN_WO_LOC,1,6))
					                   AND SL.YD_STK_BED_NO = DECODE(CS.YD_L2_REQUEST_STAT,'5', SUBSTR(CS.YD_DN_WO_LOC_TO,7,2), SUBSTR(CS.YD_DN_WO_LOC,7,2))
					                   AND SL.STL_NO = SC.SLAB_NO 
					                    -- 'D'추가/해당단 하단 정보
					                   AND SL.YD_STK_LYR_MTL_STAT IN ('C','D')
					                   AND SL.YD_STK_LYR_NO = TO_CHAR(DECODE(CS.YD_L2_REQUEST_STAT,'5', CS.STK_LYR_NO_TEMP, DECODE(CS.YD_DN_WO_LAYER,'001',CS.YD_DN_WO_LAYER , CS.YD_DN_WO_LAYER-1)),'FM000')
					                   AND SL.STL_NO    = ST.STL_NO
					               )                         AS DN_MTL_SLAB_BND_T
					               
					              ,NVL(FYN_UM.DTL_ITEM3 ,0) AS DTL_ITEM3 -- 형상차량 Z축
					              ,NVL(FYN_UM.DTL_ITEM2 ,0) AS DTL_ITEM2 -- 형상차량 Y축
					              ,NVL(FYN_UM.DTL_ITEM1 ,0) AS DTL_ITEM1 -- 형상차량 X축
					              ,NVL(FYN_UM.DTL_ITEM5 ,'N') AS DTL_ITEM5 -- 형상차량 스캔 유무
					              ,NVL(FYN_UM.DTL_ITEM6 ,'N') AS DTL_ITEM6 -- 형상기기 설치 유무 USRYDA.TB_YD_STOCK
					              --,(SELECT WGT_CENTER_ZAXIS FROM USRYDA.TB_YD_STOCK WHERE YD_SCH_CD = 'DBPT01UM' ) AS WGT_CENTER_ZAXIS --202102241042041705
					              ,CS.YD_TO_LOC_DCSN_MTD               
					                
					          FROM TB_YD_CRNSCH  CS
					              ,TB_YD_WRKBOOK WB
					              ,TB_YD_SCHRULE SR
					              ,( SELECT  NVL(A.CD_GP,'DDDDDD') AS CD_GP
					                        ,NVL(B.YD_FRM_YN ,'N') AS DTL_ITEM6
					                        ,NVL(A.DTL_ITEM1 ,0) AS DTL_ITEM1
					                        ,NVL(A.DTL_ITEM2 ,0) AS DTL_ITEM2
					                        ,NVL(A.DTL_ITEM3 ,0) AS DTL_ITEM3
					                        ,NVL(A.DTL_ITEM5 ,0) AS DTL_ITEM5
					                        FROM TB_YD_RULE A
					                             , TB_YD_CARPOINT B
					                             WHERE 1=1
					                                AND  A.CD_GP = B.YD_STK_COL_GP
					                                AND  A.REPR_CD_GP = 'DYD006'
					                                AND  A.CD_GP IN ( 'DBPT01', 'DBPT02' , 'DAPT01' , 'DAPT02'  )-- 차량 출고 상차 형상 포인트(현재 DBPT01에만 설치되어 있음 == 형상설치 가능성때문에 4포인트 전부 표시)
					                                AND  A.ITEM = 'D'
					                          ) FYN_UM
					         WHERE CS.YD_WBOOK_ID   = WB.YD_WBOOK_ID
					           AND CS.YD_SCH_CD     = SR.YD_SCH_CD
					           AND SUBSTR(CS.YD_SCH_CD ,1,6)    = FYN_UM.CD_GP(+)
					           AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					           AND CS.DEL_YN        = 'N'
					           AND WB.DEL_YN        = 'N'
					           ) CUR_IF
					         WHERE 1=1  ) CS
					      ,(SELECT YD_CRN_SCH_ID
					              ,MIN(DECODE(LYR_NO, 1,STL_NO   )) AS STL_NO1
					              ,MIN(DECODE(LYR_NO, 1,YD_STL_WT)) AS YD_STL_WT1
					              ,MIN(DECODE(LYR_NO, 1,YD_STL_T )) AS YD_STL_T1
					              ,MIN(DECODE(LYR_NO, 1,YD_STL_W )) AS YD_STL_W1
					              ,MIN(DECODE(LYR_NO, 1,YD_STL_L )) AS YD_STL_L1
					              ,MIN(DECODE(LYR_NO, 1,HSM_GP ))   AS HSM_GP1
					              ,MIN(DECODE(LYR_NO, 1,YD_AIM_RT_GP )) AS YP1
					              ,MIN(DECODE(LYR_NO, 1,ORD_YEOJAE_GP)) AS OP1
					              ,MIN(DECODE(LYR_NO, 1,CI_MO)) AS CI_MO1
					              ,MIN(DECODE(LYR_NO, 2,STL_NO   )) AS STL_NO2
					              ,MIN(DECODE(LYR_NO, 2,YD_STL_WT)) AS YD_STL_WT2
					              ,MIN(DECODE(LYR_NO, 2,YD_STL_T )) AS YD_STL_T2
					              ,MIN(DECODE(LYR_NO, 2,YD_STL_W )) AS YD_STL_W2
					              ,MIN(DECODE(LYR_NO, 2,YD_STL_L )) AS YD_STL_L2
					              ,MIN(DECODE(LYR_NO, 2,HSM_GP ))   AS HSM_GP2
					              ,MIN(DECODE(LYR_NO, 2,YD_AIM_RT_GP )) AS YP2
					              ,MIN(DECODE(LYR_NO, 2,ORD_YEOJAE_GP)) AS OP2
					              ,MIN(DECODE(LYR_NO, 2,CI_MO)) AS CI_MO2
					              ,MIN(DECODE(LYR_NO, 3,STL_NO   )) AS STL_NO3
					              ,MIN(DECODE(LYR_NO, 3,YD_STL_WT)) AS YD_STL_WT3
					              ,MIN(DECODE(LYR_NO, 3,YD_STL_T )) AS YD_STL_T3
					              ,MIN(DECODE(LYR_NO, 3,YD_STL_W )) AS YD_STL_W3
					              ,MIN(DECODE(LYR_NO, 3,YD_STL_L )) AS YD_STL_L3
					              ,MIN(DECODE(LYR_NO, 3,HSM_GP ))   AS HSM_GP3
					              ,MIN(DECODE(LYR_NO, 3,YD_AIM_RT_GP )) AS YP3
					              ,MIN(DECODE(LYR_NO, 3,ORD_YEOJAE_GP)) AS OP3
					              ,MIN(DECODE(LYR_NO, 3,CI_MO)) AS CI_MO3
					              ,MIN(DECODE(LYR_NO, 4,STL_NO   )) AS STL_NO4
					              ,MIN(DECODE(LYR_NO, 4,YD_STL_WT)) AS YD_STL_WT4
					              ,MIN(DECODE(LYR_NO, 4,YD_STL_T )) AS YD_STL_T4
					              ,MIN(DECODE(LYR_NO, 4,YD_STL_W )) AS YD_STL_W4
					              ,MIN(DECODE(LYR_NO, 4,YD_STL_L )) AS YD_STL_L4
					              ,MIN(DECODE(LYR_NO, 4,HSM_GP ))   AS HSM_GP4
					              ,MIN(DECODE(LYR_NO, 4,YD_AIM_RT_GP )) AS YP4
					              ,MIN(DECODE(LYR_NO, 4,ORD_YEOJAE_GP)) AS OP4
					              ,MIN(DECODE(LYR_NO, 4,CI_MO)) AS CI_MO4
					              ,MIN(DECODE(LYR_NO, 5,STL_NO   )) AS STL_NO5
					              ,MIN(DECODE(LYR_NO, 5,YD_STL_WT)) AS YD_STL_WT5
					              ,MIN(DECODE(LYR_NO, 5,YD_STL_T )) AS YD_STL_T5
					              ,MIN(DECODE(LYR_NO, 5,YD_STL_W )) AS YD_STL_W5
					              ,MIN(DECODE(LYR_NO, 5,YD_STL_L )) AS YD_STL_L5
					              ,MIN(DECODE(LYR_NO, 5,HSM_GP ))   AS HSM_GP5
					              ,MIN(DECODE(LYR_NO, 5,YD_AIM_RT_GP )) AS YP5
					              ,MIN(DECODE(LYR_NO, 5,ORD_YEOJAE_GP)) AS OP5
					              ,MIN(DECODE(LYR_NO, 5,CI_MO)) AS CI_MO5
					              ,MIN(DECODE(LYR_NO, 6,STL_NO   )) AS STL_NO6
					              ,MIN(DECODE(LYR_NO, 6,YD_STL_WT)) AS YD_STL_WT6
					              ,MIN(DECODE(LYR_NO, 6,YD_STL_T )) AS YD_STL_T6
					              ,MIN(DECODE(LYR_NO, 6,YD_STL_W )) AS YD_STL_W6
					              ,MIN(DECODE(LYR_NO, 6,YD_STL_L )) AS YD_STL_L6
					              ,MIN(DECODE(LYR_NO, 6,HSM_GP ))   AS HSM_GP6
					              ,MIN(DECODE(LYR_NO, 6,YD_AIM_RT_GP )) AS YP6
					              ,MIN(DECODE(LYR_NO, 6,ORD_YEOJAE_GP)) AS OP6
					              ,MIN(DECODE(LYR_NO, 6,CI_MO)) AS CI_MO6
					              ,MIN(DECODE(LYR_NO, 7,STL_NO   )) AS STL_NO7
					              ,MIN(DECODE(LYR_NO, 7,YD_STL_WT)) AS YD_STL_WT7
					              ,MIN(DECODE(LYR_NO, 7,YD_STL_T )) AS YD_STL_T7
					              ,MIN(DECODE(LYR_NO, 7,YD_STL_W )) AS YD_STL_W7
					              ,MIN(DECODE(LYR_NO, 7,YD_STL_L )) AS YD_STL_L7
					              ,MIN(DECODE(LYR_NO, 7,HSM_GP ))   AS HSM_GP7
					              ,MIN(DECODE(LYR_NO, 7,YD_AIM_RT_GP )) AS YP7
					              ,MIN(DECODE(LYR_NO, 7,ORD_YEOJAE_GP)) AS OP7
					              ,MIN(DECODE(LYR_NO, 7,CI_MO)) AS CI_MO7
					              ,MIN(DECODE(LYR_NO, 8,STL_NO   )) AS STL_NO8
					              ,MIN(DECODE(LYR_NO, 8,YD_STL_WT)) AS YD_STL_WT8
					              ,MIN(DECODE(LYR_NO, 8,YD_STL_T )) AS YD_STL_T8
					              ,MIN(DECODE(LYR_NO, 8,YD_STL_W )) AS YD_STL_W8
					              ,MIN(DECODE(LYR_NO, 8,YD_STL_L )) AS YD_STL_L8
					              ,MIN(DECODE(LYR_NO, 8,HSM_GP ))   AS HSM_GP8
					              ,MIN(DECODE(LYR_NO, 8,YD_AIM_RT_GP )) AS YP8
					              ,MIN(DECODE(LYR_NO, 8,ORD_YEOJAE_GP)) AS OP8
					              ,MIN(DECODE(LYR_NO, 8,CI_MO)) AS CI_MO8
					              ,MIN(DECODE(LYR_NO, 9,STL_NO   )) AS STL_NO9
					              ,MIN(DECODE(LYR_NO, 9,YD_STL_WT)) AS YD_STL_WT9
					              ,MIN(DECODE(LYR_NO, 9,YD_STL_T )) AS YD_STL_T9
					              ,MIN(DECODE(LYR_NO, 9,YD_STL_W )) AS YD_STL_W9
					              ,MIN(DECODE(LYR_NO, 9,YD_STL_L )) AS YD_STL_L9
					              ,MIN(DECODE(LYR_NO, 9,HSM_GP ))   AS HSM_GP9
					              ,MIN(DECODE(LYR_NO, 9,YD_AIM_RT_GP )) AS YP9
					              ,MIN(DECODE(LYR_NO, 9,ORD_YEOJAE_GP)) AS OP9
					              ,MIN(DECODE(LYR_NO, 9,CI_MO)) AS CI_MO9
					              ,MIN(DECODE(LYR_NO,10,STL_NO   )) AS STL_NO10
					              ,MIN(DECODE(LYR_NO,10,YD_STL_WT)) AS YD_STL_WT10
					              ,MIN(DECODE(LYR_NO,10,YD_STL_T )) AS YD_STL_T10
					              ,MIN(DECODE(LYR_NO,10,YD_STL_W )) AS YD_STL_W10
					              ,MIN(DECODE(LYR_NO,10,YD_STL_L )) AS YD_STL_L10
					              ,MIN(DECODE(LYR_NO,10,HSM_GP ))   AS HSM_GP10
					              ,MIN(DECODE(LYR_NO,10,YD_AIM_RT_GP )) AS YP10
					              ,MIN(DECODE(LYR_NO,10,ORD_YEOJAE_GP)) AS OP10
					              ,MIN(DECODE(LYR_NO,10,CI_MO)) AS CI_MO10
					          FROM (SELECT CM.YD_CRN_SCH_ID
					                      ,CM.STL_NO
					                      ,TO_CHAR(ST.YD_MTL_WT,'FM00000'  ) AS YD_STL_WT
					                      ,TO_CHAR(ST.YD_MTL_T ,'FM000V000') AS YD_STL_T
					                      ,TO_CHAR(ST.YD_MTL_W ,'FM0000V0' ) AS YD_STL_W
					                      ,TO_CHAR(ST.YD_MTL_L ,'FM0000000') AS YD_STL_L
					                      ,ROW_NUMBER() OVER (ORDER BY CM.YD_STK_LYR_NO DESC) AS LYR_NO
					                      ,(CASE WHEN SH.HSM_GP='X' THEN 'X' --TONG크레인 사용금지 규격
					                            WHEN (
					                                  MS.CCSLAB_CL_MTD_GP IN('G','H','I','J')
					                                  OR 
					                                  (SH.SLAB_WO_RT_CD LIKE 'P%' AND SH.MSLAB_ASGN_GP IN ('D','H','G'))
					                                 ) 
					                                 AND (DECODE(MS.CCSLAB_CL_MTD_GP,'J',1440, 'I',2880,'H',4320,'G',5760,2880) - ROUND((SYSDATE - SH.MSLAB_FS_CMPL_DT) * 1440)) > 0 --냉각잔여분(96Hr/5760)
					                       THEN NVL(DECODE(SH.HSM_GP,'H','A','G','C','B'),'N')
					                       ELSE NVL(DECODE(SH.HSM_GP,'H','Y','G','C','N'),'N') 
					                       END) AS HSM_GP -- 고강도재/선행재여부(A:선행재+고강도재,B:선행재,Y:고강도재,N:NOT)
					                      ,ST.YD_AIM_RT_GP
					                      ,SC.ORD_YEOJAE_GP
					                      ,DECODE(SC.SLAB_WO_RT_CD,'PA',1,'PB',2) AS CI_MO  --압연지시   압연지시 PA 면 1 PB 면 2 로 주기로 함  2021-2-9 L2 요구
					                  FROM TB_YD_CRNWRKMTL CM
					                      ,TB_YD_STOCK     ST
					                      ,VW_YD_SLABHSM   SH 
					                      ,VW_YD_SLABCOMM  SC
					                      ,USRPTA.TB_PT_MSLABCOMMSUB MS
					                 WHERE CM.STL_NO        = ST.STL_NO 
					                   AND CM.STL_NO        = SH.STL_NO  
					                   AND CM.STL_NO        = SC.SLAB_NO
					                   AND CM.STL_NO        = MS.MSLAB_NO(+)
					                   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					                   AND CM.DEL_YN        = 'N'
					                   AND ST.DEL_YN        = 'N')
					         GROUP BY YD_CRN_SCH_ID) CM
					      ,(SELECT NS.YD_CRN_SCH_ID
					              ,NS.YD_SCH_CD                                AS YD_SCH_CD_NEXT
					              ,(SELECT SUBSTRB(SR.CD_CONTENTS,1,30)
					                  FROM TB_YD_SCHRULE SR
					                 WHERE SR.YD_SCH_CD = NS.YD_SCH_CD)        AS YD_SCH_NAME_NEXT
					              ,NS.YD_UP_WO_LOC                             AS YD_UP_WO_LOC_NEXT
					              ,NS.YD_UP_TOTMAX_LAYER_NEXT                  AS YD_UP_WO_LAYER_NEXT
					              ,NS.YD_DN_WO_LOC                             AS YD_DN_WO_LOC_NEXT
					              ,NS.YD_DN_TOTMAX_LAYER_NEXT                  AS YD_DN_WO_LAYER_NEXT
					              ,NS.STL_NO                                   AS STL_NO_NEXT
					              ,TO_CHAR(NS.YD_EQP_WRK_SH     ,'FM00'      ) AS YD_CRN_WRK_SH_NEXT
					              ,TO_CHAR(NS.YD_EQP_WRK_WT     ,'FM0000000' ) AS YD_CRN_WRK_WT_NEXT
					              ,TO_CHAR(NS.YD_EQP_WRK_T      ,'FM0000V000') AS YD_CRN_WRK_T_NEXT
					              ,TO_CHAR(NS.YD_EQP_WRK_MAX_W  ,'FM0000V0'  ) AS YD_CRN_WRK_MAX_W_NEXT
					              ,TO_CHAR(NS.YD_EQP_WRK_MAX_L  ,'FM0000000' ) AS YD_CRN_WRK_MAX_L_NEXT
					              ,TO_CHAR(NS.YD_CRN_SCH_RMD_CNT,'FM00'      ) AS YD_CRN_SCH_RMD_CNT
					              ,YD_EQP_ID
					          FROM (SELECT DD.YD_CRN_SCH_ID
					                      ,CS.YD_SCH_CD
					                      ,CS.YD_UP_WO_LOC
					                      ,CS.YD_UP_WO_LAYER
					                      ,CS.YD_DN_WO_LOC
					                      ,CS.YD_DN_WO_LAYER
					                      ,(SELECT MAX(YD_STK_LYR_NO) FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = SUBSTR(CS.YD_DN_WO_LOC,0,6) AND YD_STK_BED_NO = SUBSTR(CS.YD_DN_WO_LOC,7,2) AND YD_STK_LYR_MTL_STAT IN('C','D') ) AS YD_DN_TOTMAX_LAYER_NEXT
					                      ,(SELECT MAX(YD_STK_LYR_NO) FROM TB_YD_STKLYR WHERE YD_STK_COL_GP = SUBSTR(CS.YD_UP_WO_LOC,0,6) AND YD_STK_BED_NO = SUBSTR(CS.YD_UP_WO_LOC,7,2) AND YD_STK_LYR_MTL_STAT IN('C','U') ) AS YD_UP_TOTMAX_LAYER_NEXT
					                      ,CM.STL_NO
					                      ,CS.YD_EQP_WRK_SH
					                      ,CS.YD_EQP_WRK_WT
					                      ,CS.YD_EQP_WRK_T
					                      ,CS.YD_EQP_WRK_MAX_W
					                      ,CS.YD_EQP_WRK_MAX_L
					                      ,COUNT(DISTINCT CS.YD_CRN_SCH_ID) OVER () AS YD_CRN_SCH_RMD_CNT
					                      ,CS.YD_EQP_ID
					                  FROM TB_YD_CRNSCH    CS
					                      ,TB_YD_CRNWRKMTL CM
					                      ,(SELECT YD_CRN_SCH_ID
					                              ,YD_EQP_ID
					                          FROM TB_YD_CRNSCH
					                         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) DD
					                 WHERE CS.YD_EQP_ID       = DD.YD_EQP_ID
					                   AND CS.YD_CRN_SCH_ID    != DD.YD_CRN_SCH_ID
					                   AND CS.YD_CRN_SCH_ID     = CM.YD_CRN_SCH_ID
					                   AND CS.YD_WRK_PROG_STAT IN ('S','W','1','2','3') -- S 추가
					                   AND CS.DEL_YN            = 'N'
					                   AND CM.DEL_YN            = 'N'
					                 ORDER BY CS.YD_SCH_PRIOR , CS.YD_CRN_SCH_ID, CM.YD_STK_LYR_NO DESC
					                 ) NS
					         WHERE ROWNUM = 1
					         ) NS
					        ,(
					           SELECT 
					                B.YD_STK_COL_GP,
					                B.YD_STK_BED_NO,
					                B.YD_STK_LYR_NO,
					                TO_CHAR(A.YD_MTL_T ,'FM000V000') AS YD_MTL_T,
					                TO_CHAR(A.YD_MTL_W ,'FM0000V0' ) AS YD_MTL_W,
					                TO_CHAR(A.YD_MTL_L ,'FM0000000') AS YD_MTL_L
					           FROM TB_YD_STOCK A,
					                TB_YD_STKLYR B
					           WHERE A.STL_NO = B.STL_NO
					         )TM
					 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					   AND CS.YD_EQP_ID = NS.YD_EQP_ID(+)
					   AND CS.JMS_TC_CD IS NOT NULL
					   AND SUBSTR(CS.YD_DN_WO_LOC,0,6) = TM.YD_STK_COL_GP(+)
					   AND SUBSTR(CS.YD_DN_WO_LOC,7,2) = TM.YD_STK_BED_NO(+) 
					   AND CS.YD_DN_MAX_LAYER          = TM.YD_STK_LYR_NO(+)
				 */
			
			} else //-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^
			if ("YDY3L006".equals(msgId)) {
				trtNm = "대차출발지시";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getMsgYDY3L006";
				//jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC3L006";
				/*
				 * 대차출발지시 전문조회
					SELECT JMS_TC_CD --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,JMS_TC_CD                                  --전문ID
					     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')    --생성일시
					     ||'I'                                        --전문구분
					     ||DECODE(JMS_TC_CD,'YDY3L006','0029','0020') --전문길이
					     ||RPAD(' ',29,' ')                           --임시
					     ||RPAD(NVL(YD_EQP_ID          ,' '), 6,' ')  --야드설비ID
					     ||RPAD(NVL(YD_EQP_WRK_STAT    ,' '), 1,' ')  --야드설비작업상태
					     ||RPAD(NVL(YD_TCAR_AIM_AREA_GP,' '), 1,' ')  --야드목표동구분
					     ||RPAD(NVL(YD_CARLD_STOP_LOC  ,' '), 6,' ')  --야드상차정지위치
					     ||RPAD(NVL(YD_CARUD_STOP_LOC  ,' '), 6,' ')  --야드하차정지위치
					     ||DECODE(JMS_TC_CD,'YDY3L006',YD_EQP_WRK_SH) --야드설비작업매수
					     ||DECODE(JMS_TC_CD,'YDY3L006',YD_EQP_WRK_WT) --야드설비작업중량
					       AS JMS_TC_MESSAGE --JMSTCMessage
					  FROM (SELECT
					              'YDY3L006' AS JMS_TC_CD
					              ,YD_EQP_ID
					              ,YD_EQP_WRK_STAT
					              ,SUBSTR(DECODE(YD_EQP_WRK_STAT,'L',YD_CARUD_STOP_LOC,YD_CARLD_STOP_LOC),2,1) AS YD_TCAR_AIM_AREA_GP
					              ,YD_CARLD_STOP_LOC
					              ,YD_CARUD_STOP_LOC
					              ,TO_CHAR(NVL(YD_EQP_WRK_SH,0),'FM00'     ) AS YD_EQP_WRK_SH
					              ,TO_CHAR(NVL(YD_EQP_WRK_WT,0),'FM0000000') AS YD_EQP_WRK_WT
					          FROM (SELECT TS.YD_EQP_ID
					                      ,MIN(TS.YD_CARLD_STOP_LOC) AS YD_CARLD_STOP_LOC
					                      ,MIN(TS.YD_CARUD_STOP_LOC) AS YD_CARUD_STOP_LOC
					                      ,MIN(TS.YD_EQP_WRK_STAT  ) AS YD_EQP_WRK_STAT
					                      ,COUNT(ST.STL_NO)          AS YD_EQP_WRK_SH
					                      ,SUM(NVL(ST.CAL_SLAB_WT, ST.SLAB_WT) )        AS YD_EQP_WRK_WT
					                  FROM TB_YD_TCARSCH     TS
					                      ,TB_YD_TCARFTMVMTL TM
					                      --,TB_YD_STOCK       ST					                     
						                    ,( SELECT ST.*
					                                 , SC.SLAB_LEN               -- 길이
					                                 , SC.REAL_MEASURE_SLAB_LEN  -- 실측길이
					                                 , SC.SLAB_T                 -- 두께
					                                 , SC.REAL_MEASURE_SLAB_T    -- 실측두께
					                                 , SC.SLAB_W                 -- 폭
					                                 , SC.REAL_MEASURE_SLAB_W    -- 실측폭
					                                 , SC.SLAB_WT                -- 중량
					                                 , SC.CAL_SLAB_WT            -- 계산중량
					                              FROM USRYDA.TB_YD_STOCK    ST -- YD_저장품
					                                 , USRPTA.TB_PT_SLABCOMM SC -- SLAB공통
					                             WHERE ST.STL_NO = SC.SLAB_NO
					                       ) ST
					                 WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID(+)
					                   AND TM.DEL_YN(+)      = 'N'
					                   AND TM.STL_NO         = ST.STL_NO(+)
					                   AND TS.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					                 GROUP BY TS.YD_EQP_ID))
					 WHERE JMS_TC_CD IS NOT NULL
				 */
				
			} else //-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^
// YYS			if ("YDY3L007".equals(msgId)) {
//				trtNm = "대차작업실적";
//				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getMsgYDC3L007";
//				/*
//				 *대차작업실적 전문조회 
//					SELECT JMS_TC_CD --JMSTC코드
//					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
//					      ,JMS_TC_CD                                --전문ID
//					     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')  --생성일시
//					     ||'I'                                      --전문구분
//					     ||'0142'                                   --전문길이
//					     ||RPAD(' ',29,' ')                         --임시
//					     ||RPAD(NVL(YD_EQP_ID         ,' '), 6,' ') --야드설비구분
//					     ||RPAD(NVL(YD_TCAR_BAY       ,' '), 1,' ') --야드대차현재동구분
//					     ||RPAD(NVL(YD_TCAR_STOP_LOC  ,' '), 6,' ') --야드대차현재정지위치
//					     ||RPAD(NVL(YD_EQP_WRK_STAT   ,' '), 1,' ') --적재상태
//					     ||LPAD(NVL(YD_STK_CUR_SH     ,'0'), 2,'0') --적치적재현재수량(누계)
//					     ||LPAD(NVL(YD_STK_CUR_WT     ,'0'), 7,'0') --적치적재현재중량
//					     ||LPAD(NVL(YD_CRN_HANDLING_SH,'0'), 2,'0') --크래인Handling매수(현재)
//					     ||LPAD(NVL(YD_CRN_HANDLING_WT,'0'), 7,'0') --크레인Handling중량
//					     ||RPAD(NVL(STL_NO1           ,' '),11,' ') --재료번호1
//					     ||RPAD(NVL(STL_NO2           ,' '),11,' ') --재료번호2
//					     ||RPAD(NVL(STL_NO3           ,' '),11,' ') --재료번호3
//					     ||RPAD(NVL(STL_NO4           ,' '),11,' ') --재료번호4
//					     ||RPAD(NVL(STL_NO5           ,' '),11,' ') --재료번호5
//					     ||RPAD(NVL(STL_NO6           ,' '),11,' ') --재료번호6
//					     ||RPAD(NVL(STL_NO7           ,' '),11,' ') --재료번호7
//					     ||RPAD(NVL(STL_NO8           ,' '),11,' ') --재료번호8
//					     ||RPAD(NVL(STL_NO9           ,' '),11,' ') --재료번호9
//					     ||RPAD(NVL(STL_NO10          ,' '),11,' ') --재료번호10
//					       AS JMS_TC_MESSAGE --JMSTCMessage
//					  FROM (SELECT CASE WHEN YD_EQP_ID IN ('AXTC01','AXTC02','AXTC03') THEN 'YDC3L007'
//					  	                WHEN YD_EQP_ID IN ('AXTC04','AXTC05','AXTC06') THEN 'YDC7L007'
//					  	           END AS JMS_TC_CD
//					              ,TS.* ,TM.* ,CM.*
//					              ,SUBSTR(DECODE(TS.YD_EQP_WRK_STAT,'L',TS.YD_CARLD_STOP_LOC,TS.YD_CARUD_STOP_LOC),2,1) AS YD_TCAR_BAY
//					              ,DECODE(TS.YD_EQP_WRK_STAT,'L',TS.YD_CARLD_STOP_LOC,TS.YD_CARUD_STOP_LOC)             AS YD_TCAR_STOP_LOC
//					          FROM (SELECT TS.YD_TCAR_SCH_ID
//					                      ,TS.YD_EQP_ID
//					                      ,TS.YD_CARLD_STOP_LOC
//					                      ,TS.YD_CARUD_STOP_LOC
//					                      ,TS.YD_EQP_WRK_STAT
//					                  FROM TB_YD_TCARSCH TS
//					                 WHERE TS.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID) TS
//					              ,(SELECT TM.YD_TCAR_SCH_ID
//					                      ,TO_CHAR(COUNT(*)                ,'FM00'     ) AS YD_STK_CUR_SH
//					                      ,TO_CHAR(NVL(SUM(NVL(ST.CAL_SLAB_WT,ST.SLAB_WT)),0),'FM0000000') AS YD_STK_CUR_WT
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'001',TM.STL_NO)) AS STL_NO1
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'002',TM.STL_NO)) AS STL_NO2
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'003',TM.STL_NO)) AS STL_NO3
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'004',TM.STL_NO)) AS STL_NO4
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'005',TM.STL_NO)) AS STL_NO5
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'006',TM.STL_NO)) AS STL_NO6
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'007',TM.STL_NO)) AS STL_NO7
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'008',TM.STL_NO)) AS STL_NO8
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'009',TM.STL_NO)) AS STL_NO9
//					                      ,MIN(DECODE(TM.YD_STK_LYR_NO,'010',TM.STL_NO)) AS STL_NO10
//					                  FROM TB_YD_TCARFTMVMTL TM
//					                      --,TB_YD_STOCK       ST
//					                      ,(
//					                            SELECT ST.*
//					                                 , SC.SLAB_LEN               -- 길이
//					                                 , SC.REAL_MEASURE_SLAB_LEN  -- 실측길이
//					                                 , SC.SLAB_T                 -- 두께
//					                                 , SC.REAL_MEASURE_SLAB_T    -- 실측두께
//					                                 , SC.SLAB_W                 -- 폭
//					                                 , SC.REAL_MEASURE_SLAB_W    -- 실측폭
//					                                 , SC.SLAB_WT                -- 중량
//					                                 , SC.CAL_SLAB_WT            -- 계산중량
//					                              FROM USRYDA.TB_YD_STOCK    ST -- YD_저장품
//					                                 , USRPTA.TB_PT_SLABCOMM SC -- SLAB공통
//					                             WHERE ST.STL_NO = SC.SLAB_NO
//					                      ) ST
//					
//					                 WHERE TM.STL_NO         = ST.STL_NO
//					                   AND TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
//					                   AND TM.DEL_YN         = 'N'
//					                 GROUP BY TM.YD_TCAR_SCH_ID) TM
//					              ,(SELECT TO_CHAR(COUNT(*)                ,'FM00'     ) AS YD_CRN_HANDLING_SH
//					                      ,TO_CHAR(NVL(SUM(ST.CAL_SLAB_WT),0),'FM0000000') AS YD_CRN_HANDLING_WT
//					                  FROM TB_YD_CRNWRKMTL CM
//					                      --,TB_YD_STOCK     ST
//					                      ,(
//					                            SELECT ST.*
//					                                 , SC.SLAB_LEN               -- 길이
//					                                 , SC.REAL_MEASURE_SLAB_LEN  -- 실측길이
//					                                 , SC.SLAB_T                 -- 두께
//					                                 , SC.REAL_MEASURE_SLAB_T    -- 실측두께
//					                                 , SC.SLAB_W                 -- 폭
//					                                 , SC.REAL_MEASURE_SLAB_W    -- 실측폭
//					                                 , SC.SLAB_WT                -- 중량
//					                                 , SC.CAL_SLAB_WT            -- 계산중량
//					                              FROM USRYDA.TB_YD_STOCK    ST -- YD_저장품
//					                                 , USRPTA.TB_PT_SLABCOMM SC -- SLAB공통
//					                             WHERE ST.STL_NO = SC.SLAB_NO
//					                      ) ST
//					                 WHERE CM.STL_NO        = ST.STL_NO
//					                   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
//					                   AND CM.DEL_YN        = 'N') CM
//					         WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID(+))
//					 WHERE JMS_TC_CD IS NOT NULL
//				 */
//			} else //-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^
			if ("YDY3L008".equals(msgId)) {
				trtNm = "차량작업 예정정보 ";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.YDY3L008";
				
				/*
				 * 
					SELECT JMS_TC_CD  --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT  --JMSTC생성일시
					      ,JMS_TC_CD                                                  --전문ID
					     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')                    --생성일시
					     ||'I'                                                        --전문구분
					     ||'0821'                                                     --전문길이
					     ||RPAD(' ',29,' ')                                           --임시
					     ||RPAD(NVL(A.PT_LOAD_LOC                       ,' '), 6,' ') --상차도위치
					     ||RPAD(NVL(A.CAR_NO                            ,' '),15,' ') --차량번호
					     ||RPAD(NVL(A.CARD_NO                           ,' '), 4,' ') --카드번호
					     ||RPAD(NVL(A.PT_CLS                            ,' '), 2,' ') --차량구분
					     ||RPAD(NVL(A.WORK_CLS                          ,' '), 1,' ') --작업구분
					     ||LPAD(NVL(A.WORK_COIL_MAX_CNT                 ,'0'), 2,'0') --야드적치Bed번호
					     
					     ||RPAD(NVL(A.STOCK_ID_0                        ,' '),11,' ') --재료번호_0
					     ||RPAD(NVL(A.LOAD_LOC_CD_0                     ,' '), 2,' ') --차량적재위치_0
					     ||LPAD(NVL(C_0.SLAB_WT                         ,'0'), 5,'0') --재료중량_0
					     ||LPAD(NVL(TO_CHAR(C_0.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_0
					     ||LPAD(NVL(TO_CHAR(C_0.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_0
					     ||LPAD(NVL(C_0.SLAB_LEN                        ,'0'), 7,'0') --재료길이_0
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_0                      ,' '), 1,' ') --작업상태_0
					     ||RPAD(DECODE(A.STOCK_ID_0,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_0
					     
					     ||RPAD(NVL(A.STOCK_ID_1                        ,' '),11,' ') --재료번호_1
					     ||RPAD(NVL(A.LOAD_LOC_CD_1                     ,' '), 2,' ') --차량적재위치_1
					     ||LPAD(NVL(C_1.SLAB_WT                         ,'0'), 5,'0') --재료중량_1
					     ||LPAD(NVL(TO_CHAR(C_1.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_1
					     ||LPAD(NVL(TO_CHAR(C_1.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_1
					     ||LPAD(NVL(C_1.SLAB_LEN                        ,'0'), 7,'0') --재료길이_1
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_1                      ,' '), 1,' ') --작업상태_1
					     ||RPAD(DECODE(A.STOCK_ID_1,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_1
					     
					     ||RPAD(NVL(A.STOCK_ID_2                        ,' '),11,' ') --재료번호_2
					     ||RPAD(NVL(A.LOAD_LOC_CD_2                     ,' '), 2,' ') --차량적재위치_2
					     ||LPAD(NVL(C_2.SLAB_WT                         ,'0'), 5,'0') --재료중량_2
					     ||LPAD(NVL(TO_CHAR(C_2.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_2
					     ||LPAD(NVL(TO_CHAR(C_2.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_2
					     ||LPAD(NVL(C_2.SLAB_LEN                        ,'0'), 7,'0') --재료길이_2
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_2                      ,' '), 1,' ') --작업상태_2
					     ||RPAD(DECODE(A.STOCK_ID_2,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_2
					     
					     ||RPAD(NVL(A.STOCK_ID_3                        ,' '),11,' ') --재료번호_3
					     ||RPAD(NVL(A.LOAD_LOC_CD_3                     ,' '), 2,' ') --차량적재위치_3
					     ||LPAD(NVL(C_3.SLAB_WT                         ,'0'), 5,'0') --재료중량_3
					     ||LPAD(NVL(TO_CHAR(C_3.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_3
					     ||LPAD(NVL(TO_CHAR(C_3.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_3
					     ||LPAD(NVL(C_3.SLAB_LEN                        ,'0'), 7,'0') --재료길이_3
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_3                      ,' '), 1,' ') --작업상태_3
					     ||RPAD(DECODE(A.STOCK_ID_3,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_3
					     
					     ||RPAD(NVL(A.STOCK_ID_4                        ,' '),11,' ') --재료번호_4
					     ||RPAD(NVL(A.LOAD_LOC_CD_4                     ,' '), 2,' ') --차량적재위치_4
					     ||LPAD(NVL(C_4.SLAB_WT                         ,'0'), 5,'0') --재료중량_4
					     ||LPAD(NVL(TO_CHAR(C_4.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_4
					     ||LPAD(NVL(TO_CHAR(C_4.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_4
					     ||LPAD(NVL(C_4.SLAB_LEN                        ,'0'), 7,'0') --재료길이_4
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_4                      ,' '), 1,' ') --작업상태_4
					     ||RPAD(DECODE(A.STOCK_ID_4,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_4
					     
					     ||RPAD(NVL(A.STOCK_ID_5                        ,' '),11,' ') --재료번호_5
					     ||RPAD(NVL(A.LOAD_LOC_CD_5                     ,' '), 2,' ') --차량적재위치_5
					     ||LPAD(NVL(C_5.SLAB_WT                         ,'0'), 5,'0') --재료중량_5
					     ||LPAD(NVL(TO_CHAR(C_5.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_5
					     ||LPAD(NVL(TO_CHAR(C_5.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_5
					     ||LPAD(NVL(C_5.SLAB_LEN                        ,'0'), 7,'0') --재료길이_5
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_5                      ,' '), 1,' ') --작업상태_5
					     ||RPAD(DECODE(A.STOCK_ID_5,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_5
					     
					     ||RPAD(NVL(A.STOCK_ID_6                        ,' '),11,' ') --재료번호_6
					     ||RPAD(NVL(A.LOAD_LOC_CD_6                     ,' '), 2,' ') --차량적재위치_6
					     ||LPAD(NVL(C_6.SLAB_WT                         ,'0'), 5,'0') --재료중량_6
					     ||LPAD(NVL(TO_CHAR(C_6.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_6
					     ||LPAD(NVL(TO_CHAR(C_6.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_6
					     ||LPAD(NVL(C_6.SLAB_LEN                        ,'0'), 7,'0') --재료길이_6
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_6                      ,' '), 1,' ') --작업상태_6
					     ||RPAD(DECODE(A.STOCK_ID_6,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_6
					     
					     ||RPAD(NVL(A.STOCK_ID_7                        ,' '),11,' ') --재료번호_7
					     ||RPAD(NVL(A.LOAD_LOC_CD_7                     ,' '), 2,' ') --차량적재위치_7
					     ||LPAD(NVL(C_7.SLAB_WT                         ,'0'), 5,'0') --재료중량_7
					     ||LPAD(NVL(TO_CHAR(C_7.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_7
					     ||LPAD(NVL(TO_CHAR(C_7.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_7
					     ||LPAD(NVL(C_7.SLAB_LEN                        ,'0'), 7,'0') --재료길이_7
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_7                      ,' '), 1,' ') --작업상태_7
					     ||RPAD(DECODE(A.STOCK_ID_7,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_7
					     
					     ||RPAD(NVL(A.STOCK_ID_8                        ,' '),11,' ') --재료번호_8
					     ||RPAD(NVL(A.LOAD_LOC_CD_8                     ,' '), 2,' ') --차량적재위치_8
					     ||LPAD(NVL(C_8.SLAB_WT                         ,'0'), 5,'0') --재료중량_8
					     ||LPAD(NVL(TO_CHAR(C_8.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_8
					     ||LPAD(NVL(TO_CHAR(C_8.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_8
					     ||LPAD(NVL(C_8.SLAB_LEN                        ,'0'), 7,'0') --재료길이_8
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_8                      ,' '), 1,' ') --작업상태_8
					     ||RPAD(DECODE(A.STOCK_ID_8,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_8
					     
					     ||RPAD(NVL(A.STOCK_ID_9                        ,' '),11,' ') --재료번호_9
					     ||RPAD(NVL(A.LOAD_LOC_CD_9                     ,' '), 2,' ') --차량적재위치_9
					     ||LPAD(NVL(C_9.SLAB_WT                         ,'0'), 5,'0') --재료중량_9
					     ||LPAD(NVL(TO_CHAR(C_9.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_9
					     ||LPAD(NVL(TO_CHAR(C_9.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_9
					     ||LPAD(NVL(C_9.SLAB_LEN                        ,'0'), 7,'0') --재료길이_9
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_9                      ,' '), 1,' ') --작업상태_9
					     ||RPAD(DECODE(A.STOCK_ID_9,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_9
					     
					     ||RPAD(NVL(A.STOCK_ID_10                        ,' '),11,' ') --재료번호_10
					     ||RPAD(NVL(A.LOAD_LOC_CD_10                     ,' '), 2,' ') --차량적재위치_10
					     ||LPAD(NVL(C_10.SLAB_WT                         ,'0'), 5,'0') --재료중량_10
					     ||LPAD(NVL(TO_CHAR(C_10.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_10
					     ||LPAD(NVL(TO_CHAR(C_10.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_10
					     ||LPAD(NVL(C_10.SLAB_LEN                        ,'0'), 7,'0') --재료길이_10
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_10                      ,' '), 1,' ') --작업상태_10
					     ||RPAD(DECODE(A.STOCK_ID_10,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_10
					     
					     ||RPAD(NVL(A.STOCK_ID_11                        ,' '),11,' ') --재료번호_11
					     ||RPAD(NVL(A.LOAD_LOC_CD_11                     ,' '), 2,' ') --차량적재위치_11
					     ||LPAD(NVL(C_11.SLAB_WT                         ,'0'), 5,'0') --재료중량_11
					     ||LPAD(NVL(TO_CHAR(C_11.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_11
					     ||LPAD(NVL(TO_CHAR(C_11.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_11
					     ||LPAD(NVL(C_11.SLAB_LEN                        ,'0'), 7,'0') --재료길이_11
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_11                      ,' '), 1,' ') --작업상태_11
					     ||RPAD(DECODE(A.STOCK_ID_11,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_11
					     
					     ||RPAD(NVL(A.STOCK_ID_12                        ,' '),11,' ') --재료번호_12
					     ||RPAD(NVL(A.LOAD_LOC_CD_12                     ,' '), 2,' ') --차량적재위치_12
					     ||LPAD(NVL(C_12.SLAB_WT                         ,'0'), 5,'0') --재료중량_12
					     ||LPAD(NVL(TO_CHAR(C_12.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_12
					     ||LPAD(NVL(TO_CHAR(C_12.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_12
					     ||LPAD(NVL(C_12.SLAB_LEN                        ,'0'), 7,'0') --재료길이_12
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_12                      ,' '), 1,' ') --작업상태_12
					     ||RPAD(DECODE(A.STOCK_ID_12,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_12
					     
					     ||RPAD(NVL(A.STOCK_ID_13                        ,' '),11,' ') --재료번호_13
					     ||RPAD(NVL(A.LOAD_LOC_CD_13                     ,' '), 2,' ') --차량적재위치_13
					     ||LPAD(NVL(C_13.SLAB_WT                         ,'0'), 5,'0') --재료중량_13
					     ||LPAD(NVL(TO_CHAR(C_13.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_13
					     ||LPAD(NVL(TO_CHAR(C_13.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_13
					     ||LPAD(NVL(C_13.SLAB_LEN                        ,'0'), 7,'0') --재료길이_13
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_13                      ,' '), 1,' ') --작업상태_13
					     ||RPAD(DECODE(A.STOCK_ID_13,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_13
					     
					     ||RPAD(NVL(A.STOCK_ID_14                        ,' '),11,' ') --재료번호_14
					     ||RPAD(NVL(A.LOAD_LOC_CD_14                     ,' '), 2,' ') --차량적재위치_14
					     ||LPAD(NVL(C_14.SLAB_WT                         ,'0'), 5,'0') --재료중량_14
					     ||LPAD(NVL(TO_CHAR(C_14.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_14
					     ||LPAD(NVL(TO_CHAR(C_14.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_14
					     ||LPAD(NVL(C_14.SLAB_LEN                        ,'0'), 7,'0') --재료길이_14
					     ||'00000' --재료외경
					     ||'00000' --재료내경
					     ||RPAD(NVL(A.WORK_STATE_14                      ,' '), 1,' ') --작업상태_14
					     ||RPAD(DECODE(A.STOCK_ID_14,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_14
					     
					       AS JMS_TC_MESSAGE --JMSTCMessage
					  FROM (
					            SELECT 'YDY3L008'           AS JMS_TC_CD
					              -- YD_PREP_SCH_ID 
					              , '' PT_LOAD_LOC
					              , '' CAR_NO
					              , '' CARD_NO
					              , '' PT_CLS
					              , '' WORK_CLS
					              , '' WORK_COIL_MAX_CNT
					
					             , MIN(DECODE(SC.SEQNO,1,STL_NO)   )  AS STOCK_ID_0
					             , MIN(DECODE(SC.SEQNO,2,STL_NO)   )  AS STOCK_ID_1
					             , MIN(DECODE(SC.SEQNO,3,STL_NO)   )  AS STOCK_ID_2
					             , MIN(DECODE(SC.SEQNO,4,STL_NO)   )  AS STOCK_ID_3
					             , MIN(DECODE(SC.SEQNO,5,STL_NO)   )  AS STOCK_ID_4
					             , MIN(DECODE(SC.SEQNO,6,STL_NO)   )  AS STOCK_ID_5
					             , MIN(DECODE(SC.SEQNO,7,STL_NO)   )  AS STOCK_ID_6
					             , MIN(DECODE(SC.SEQNO,8,STL_NO)   )  AS STOCK_ID_7
					             , MIN(DECODE(SC.SEQNO,9,STL_NO)   )  AS STOCK_ID_8
					             , MIN(DECODE(SC.SEQNO,10,STL_NO)  )  AS STOCK_ID_9
					             , MIN(DECODE(SC.SEQNO,11,STL_NO)  )  AS STOCK_ID_10
					             , MIN(DECODE(SC.SEQNO,12,STL_NO)  )  AS STOCK_ID_11
					             , MIN(DECODE(SC.SEQNO,13,STL_NO)  )  AS STOCK_ID_12
					             , MIN(DECODE(SC.SEQNO,14,STL_NO)  )  AS STOCK_ID_13
					             , MIN(DECODE(SC.SEQNO,15,STL_NO)  )  AS STOCK_ID_14
					             
					             , MIN(DECODE(SC.SEQNO,1,LOAD_LOC_CD)   )  AS LOAD_LOC_CD_0
					             , MIN(DECODE(SC.SEQNO,2,LOAD_LOC_CD)   )  AS LOAD_LOC_CD_1
					             , MIN(DECODE(SC.SEQNO,3,LOAD_LOC_CD)   )  AS LOAD_LOC_CD_2
					             , MIN(DECODE(SC.SEQNO,4,LOAD_LOC_CD)   )  AS LOAD_LOC_CD_3
					             , MIN(DECODE(SC.SEQNO,5,LOAD_LOC_CD)   )  AS LOAD_LOC_CD_4
					             , MIN(DECODE(SC.SEQNO,6,LOAD_LOC_CD)   )  AS LOAD_LOC_CD_5
					             , MIN(DECODE(SC.SEQNO,7,LOAD_LOC_CD)   )  AS LOAD_LOC_CD_6
					             , MIN(DECODE(SC.SEQNO,8,LOAD_LOC_CD)   )  AS LOAD_LOC_CD_7
					             , MIN(DECODE(SC.SEQNO,9,LOAD_LOC_CD)   )  AS LOAD_LOC_CD_8
					             , MIN(DECODE(SC.SEQNO,10,LOAD_LOC_CD)  )  AS LOAD_LOC_CD_9
					             , MIN(DECODE(SC.SEQNO,11,LOAD_LOC_CD)  )  AS LOAD_LOC_CD_10
					             , MIN(DECODE(SC.SEQNO,12,LOAD_LOC_CD)  )  AS LOAD_LOC_CD_11
					             , MIN(DECODE(SC.SEQNO,13,LOAD_LOC_CD)  )  AS LOAD_LOC_CD_12
					             , MIN(DECODE(SC.SEQNO,14,LOAD_LOC_CD)  )  AS LOAD_LOC_CD_13
					             , MIN(DECODE(SC.SEQNO,15,LOAD_LOC_CD)  )  AS LOAD_LOC_CD_14
					
					             , MIN(DECODE(SC.SEQNO,0,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_0
					             , MIN(DECODE(SC.SEQNO,1,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_1
					             , MIN(DECODE(SC.SEQNO,2,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_2
					             , MIN(DECODE(SC.SEQNO,3,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_3
					             , MIN(DECODE(SC.SEQNO,4,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_4
					             , MIN(DECODE(SC.SEQNO,5,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_5
					             , MIN(DECODE(SC.SEQNO,6,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_6
					             , MIN(DECODE(SC.SEQNO,7,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_7
					             , MIN(DECODE(SC.SEQNO,8,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_8
					             , MIN(DECODE(SC.SEQNO,9,YD_STK_LYR_MTL_STAT)  )  AS WORK_STATE_9
					             , MIN(DECODE(SC.SEQNO,10,YD_STK_LYR_MTL_STAT) )  AS WORK_STATE_10
					             , MIN(DECODE(SC.SEQNO,11,YD_STK_LYR_MTL_STAT) )  AS WORK_STATE_11
					             , MIN(DECODE(SC.SEQNO,12,YD_STK_LYR_MTL_STAT) )  AS WORK_STATE_12
					             , MIN(DECODE(SC.SEQNO,13,YD_STK_LYR_MTL_STAT) )  AS WORK_STATE_13
					             , MIN(DECODE(SC.SEQNO,14,YD_STK_LYR_MTL_STAT) )  AS WORK_STATE_14
					         FROM (   
					
					            SELECT  F.* --STL_NO
					                  --,LISTAGG(F.LOAD_LOC_CD, ',') WITHIN GROUP(ORDER BY F.LOAD_LOC_CD) AS AAA
					                  --, LISTAGG(LOAD_LOC_CD, ',') WITHIN GROUP(ORDER BY LOAD_LOC_CD) AS AAA 
					              FROM TB_YD_PREPSCH A
					                 ,(
					                 SELECT 
					                   ROW_NUMBER() OVER (ORDER BY  A.YD_PREP_SCH_ID DESC ) AS SEQNO
					                  ,A.STL_NO
					                  ,A.YD_STK_COL_GP||A.YD_STK_BED_NO||SUBSTR(LPAD(A.YD_STK_LYR_NO,3,'0'),2,2)   LOAD_LOC_CD
					                  ,B.YD_STK_LYR_MTL_STAT
					                  ,A.YD_PREP_SCH_ID
					                 FROM TB_YD_PREPMTL A
					                 , TB_YD_STKLYR  B
					                 WHERE  A.STL_NO = B.STL_NO   
					                 ) F
					
					            WHERE A.YD_PREP_SCH_ID = F.YD_PREP_SCH_ID
					              AND A.YD_SCH_CD = :V_YD_SCH_CD
					              AND A.DEL_YN = 'N'
					            ) SC     
					        GROUP BY YD_PREP_SCH_ID
					  
					       ) A
					      ,VW_YD_SLABCOMM C_0
					      ,VW_YD_SLABCOMM C_1
					      ,VW_YD_SLABCOMM C_2
					      ,VW_YD_SLABCOMM C_3
					      ,VW_YD_SLABCOMM C_4
					      ,VW_YD_SLABCOMM C_5
					      ,VW_YD_SLABCOMM C_6
					      ,VW_YD_SLABCOMM C_7
					      ,VW_YD_SLABCOMM C_8
					      ,VW_YD_SLABCOMM C_9
					      ,VW_YD_SLABCOMM C_10
					      ,VW_YD_SLABCOMM C_11
					      ,VW_YD_SLABCOMM C_12
					      ,VW_YD_SLABCOMM C_13
					      ,VW_YD_SLABCOMM C_14
					 WHERE A.STOCK_ID_0 = C_0.SLAB_NO(+)
					  AND  A.STOCK_ID_1 = C_1.SLAB_NO(+)
					  AND  A.STOCK_ID_2 = C_2.SLAB_NO(+)
					  AND  A.STOCK_ID_3 = C_3.SLAB_NO(+)
					  AND  A.STOCK_ID_4 = C_4.SLAB_NO(+)
					  AND  A.STOCK_ID_5 = C_5.SLAB_NO(+)
					  AND  A.STOCK_ID_6 = C_6.SLAB_NO(+)
					  AND  A.STOCK_ID_7 = C_7.SLAB_NO(+)
					  AND  A.STOCK_ID_8 = C_8.SLAB_NO(+)
					  AND  A.STOCK_ID_9 = C_9.SLAB_NO(+)
					  AND  A.STOCK_ID_10 = C_10.SLAB_NO(+)
					  AND  A.STOCK_ID_11 = C_11.SLAB_NO(+)
					  AND  A.STOCK_ID_12 = C_12.SLAB_NO(+)
					  AND  A.STOCK_ID_13 = C_13.SLAB_NO(+)
					  AND  A.STOCK_ID_14 = C_14.SLAB_NO(+)
				 */
		
			} else //-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^
			if ("YDY3L008BackUp".equals(msgId)) {
		    	trtNm = "후판Slab 차량예정정보 Backup";
		    	jspeed_query_id =  "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.YDY3L008BackUp";
		    	/*
				SELECT JMS_TC_CD  --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT  --JMSTC생성일시
				      ,JMS_TC_CD                                                  --전문ID
				     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')                    --생성일시
				     ||'I'                                                        --전문구분
				     ||'0821'                                                     --전문길이
				     ||RPAD(' ',29,' ')                                           --임시
				     ||RPAD(NVL(A.PT_LOAD_LOC                       ,' '), 6,' ') --상차도위치
				     ||RPAD(NVL(A.CAR_NO                            ,' '),15,' ') --차량번호
				     ||RPAD(NVL(A.PT_CLS                            ,' '), 2,' ') --차량구분
				     ||RPAD(NVL(A.WORK_CLS                          ,' '), 1,' ') --자업구분
				     ||LPAD(NVL(A.WORK_COIL_MAX_CNT                 ,'0'), 2,'0') --야드적치Bed번호
				     
				     ||RPAD(NVL(A.STL_NO_0                        ,' '),11,' ') --재료번호_0
				     ||RPAD(NVL(A.LOAD_LOC_CD_0                     ,' '), 2,' ') --차량적재위치_0
				     ||LPAD(NVL(C_0.SLAB_WT                         ,'0'), 5,'0') --재료중량_0
				     ||LPAD(NVL(TO_CHAR(C_0.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_0
				     ||LPAD(NVL(TO_CHAR(C_0.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_0
				     ||LPAD(NVL(C_0.SLAB_LEN                        ,'0'), 7,'0') --재료길이_0
				     
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_0
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_0
				     
				     ||RPAD(NVL(A.WORK_STATE_0                      ,' '), 1,' ') --작업상태_0
				     ||RPAD(DECODE(A.STL_NO_0,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_0
				     
				     ||RPAD(NVL(A.STL_NO_1                          ,' '),11,' ') --재료번호_1
				     ||RPAD(NVL(A.LOAD_LOC_CD_1                     ,' '), 2,' ') --차량적재위치_1
				     ||LPAD(NVL(C_1.SLAB_WT                         ,'0'), 5,'0') --재료중량_1
				     ||LPAD(NVL(TO_CHAR(C_1.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_1
				     ||LPAD(NVL(TO_CHAR(C_1.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_1
				     ||LPAD(NVL(C_1.SLAB_LEN                        ,'0'), 7,'0') --재료길이_1
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_1
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_1
				     ||RPAD(NVL(A.WORK_STATE_1                      ,' '), 1,' ') --작업상태_1
				     ||RPAD(DECODE(A.STL_NO_1,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_1
				     
				     ||RPAD(NVL(A.STL_NO_2                          ,' '),11,' ') --재료번호_2
				     ||RPAD(NVL(A.LOAD_LOC_CD_2                     ,' '), 2,' ') --차량적재위치_2
				     ||LPAD(NVL(C_2.SLAB_WT                         ,'0'), 5,'0') --재료중량_2
				     ||LPAD(NVL(TO_CHAR(C_2.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_2
				     ||LPAD(NVL(TO_CHAR(C_2.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_2
				     ||LPAD(NVL(C_2.SLAB_LEN                        ,'0'), 7,'0') --재료길이_2
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_2
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_2
				     ||RPAD(NVL(A.WORK_STATE_2                      ,' '), 1,' ') --작업상태_2
				     ||RPAD(DECODE(A.STL_NO_2,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_2
				     
				     ||RPAD(NVL(A.STL_NO_3                          ,' '),11,' ') --재료번호_3
				     ||RPAD(NVL(A.LOAD_LOC_CD_3                     ,' '), 2,' ') --차량적재위치_3
				     ||LPAD(NVL(C_3.SLAB_WT                         ,'0'), 5,'0') --재료중량_3
				     ||LPAD(NVL(TO_CHAR(C_3.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_3
				     ||LPAD(NVL(TO_CHAR(C_3.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_3
				     ||LPAD(NVL(C_3.SLAB_LEN                        ,'0'), 7,'0') --재료길이_3
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_3
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_3
				     ||RPAD(NVL(A.WORK_STATE_3                      ,' '), 1,' ') --작업상태_3
				     ||RPAD(DECODE(A.STL_NO_3,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_3
				     
				     ||RPAD(NVL(A.STL_NO_4                          ,' '),11,' ') --재료번호_4
				     ||RPAD(NVL(A.LOAD_LOC_CD_4                     ,' '), 2,' ') --차량적재위치_4
				     ||LPAD(NVL(C_4.SLAB_WT                         ,'0'), 5,'0') --재료중량_4
				     ||LPAD(NVL(TO_CHAR(C_4.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_4
				     ||LPAD(NVL(TO_CHAR(C_4.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_4
				     ||LPAD(NVL(C_4.SLAB_LEN                        ,'0'), 7,'0') --재료길이_4
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_4
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_4
				     ||RPAD(NVL(A.WORK_STATE_4                      ,' '), 1,' ') --작업상태_4
				     ||RPAD(DECODE(A.STL_NO_4,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_4
				     
				     ||RPAD(NVL(A.STL_NO_5                          ,' '),11,' ') --재료번호_5
				     ||RPAD(NVL(A.LOAD_LOC_CD_5                     ,' '), 2,' ') --차량적재위치_5
				     ||LPAD(NVL(C_5.SLAB_WT                         ,'0'), 5,'0') --재료중량_5
				     ||LPAD(NVL(TO_CHAR(C_5.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_5
				     ||LPAD(NVL(TO_CHAR(C_5.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_5
				     ||LPAD(NVL(C_5.SLAB_LEN                        ,'0'), 7,'0') --재료길이_5
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_5
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_5
				     ||RPAD(NVL(A.WORK_STATE_5                      ,' '), 1,' ') --작업상태_5
				     ||RPAD(DECODE(A.STL_NO_5,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_5
				     
				     ||RPAD(NVL(A.STL_NO_6                          ,' '),11,' ') --재료번호_6
				     ||RPAD(NVL(A.LOAD_LOC_CD_6                     ,' '), 2,' ') --차량적재위치_6
				     ||LPAD(NVL(C_6.SLAB_WT                         ,'0'), 5,'0') --재료중량_6
				     ||LPAD(NVL(TO_CHAR(C_6.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_6
				     ||LPAD(NVL(TO_CHAR(C_6.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_6
				     ||LPAD(NVL(C_6.SLAB_LEN                        ,'0'), 7,'0') --재료길이_6
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_6
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_6
				     ||RPAD(NVL(A.WORK_STATE_6                      ,' '), 1,' ') --작업상태_6
				     ||RPAD(DECODE(A.STL_NO_6,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_6
				     
				     ||RPAD(NVL(A.STL_NO_7                          ,' '),11,' ') --재료번호_7
				     ||RPAD(NVL(A.LOAD_LOC_CD_7                     ,' '), 2,' ') --차량적재위치_7
				     ||LPAD(NVL(C_7.SLAB_WT                         ,'0'), 5,'0') --재료중량_7
				     ||LPAD(NVL(TO_CHAR(C_7.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_7
				     ||LPAD(NVL(TO_CHAR(C_7.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_7
				     ||LPAD(NVL(C_7.SLAB_LEN                        ,'0'), 7,'0') --재료길이_7
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_7
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_7
				     ||RPAD(NVL(A.WORK_STATE_7                      ,' '), 1,' ') --작업상태_7
				     ||RPAD(DECODE(A.STL_NO_7,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_7
				     
				     ||RPAD(NVL(A.STL_NO_8                          ,' '),11,' ') --재료번호_8
				     ||RPAD(NVL(A.LOAD_LOC_CD_8                     ,' '), 2,' ') --차량적재위치_8
				     ||LPAD(NVL(C_8.SLAB_WT                         ,'0'), 5,'0') --재료중량_8
				     ||LPAD(NVL(TO_CHAR(C_8.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_8
				     ||LPAD(NVL(TO_CHAR(C_8.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_8
				     ||LPAD(NVL(C_8.SLAB_LEN                        ,'0'), 7,'0') --재료길이_8
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_8
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_8
				     ||RPAD(NVL(A.WORK_STATE_8                      ,' '), 1,' ') --작업상태_8
				     ||RPAD(DECODE(A.STL_NO_8,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_8
				     
				     ||RPAD(NVL(A.STL_NO_9                          ,' '),11,' ') --재료번호_9
				     ||RPAD(NVL(A.LOAD_LOC_CD_9                     ,' '), 2,' ') --차량적재위치_9
				     ||LPAD(NVL(C_9.SLAB_WT                         ,'0'), 5,'0') --재료중량_9
				     ||LPAD(NVL(TO_CHAR(C_9.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_9
				     ||LPAD(NVL(TO_CHAR(C_9.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_9
				     ||LPAD(NVL(C_9.SLAB_LEN                        ,'0'), 7,'0') --재료길이_9
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료외경_9
				     ||LPAD(NVL(''                                  ,'0'), 5,'0') --재료내경_9
				     ||RPAD(NVL(A.WORK_STATE_9                      ,' '), 1,' ') --작업상태_9
				     ||RPAD(DECODE(A.STL_NO_9,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_9
				     
				     ||RPAD(NVL(A.STL_NO_10                          ,' '),11,' ') --재료번호_10
				     ||RPAD(NVL(A.LOAD_LOC_CD_10                     ,' '), 2,' ') --차량적재위치_10
				     ||LPAD(NVL(C_10.SLAB_WT                         ,'0'), 5,'0') --재료중량_10
				     ||LPAD(NVL(TO_CHAR(C_10.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_10
				     ||LPAD(NVL(TO_CHAR(C_10.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_10
				     ||LPAD(NVL(C_10.SLAB_LEN                        ,'0'), 7,'0') --재료길이_10
				     ||LPAD(NVL(''                                   ,'0'), 5,'0') --재료외경_10
				     ||LPAD(NVL(''                                   ,'0'), 5,'0') --재료내경_10
				     ||RPAD(NVL(A.WORK_STATE_10                      ,' '), 1,' ') --작업상태_10
				     ||RPAD(DECODE(A.STL_NO_10,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_10
				     
				     ||RPAD(NVL(A.STL_NO_11                          ,' '),11,' ') --재료번호_11
				     ||RPAD(NVL(A.LOAD_LOC_CD_11                     ,' '), 2,' ') --차량적재위치_11
				     ||LPAD(NVL(C_11.SLAB_WT                         ,'0'), 5,'0') --재료중량_11
				     ||LPAD(NVL(TO_CHAR(C_11.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_11
				     ||LPAD(NVL(TO_CHAR(C_11.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_11
				     ||LPAD(NVL(C_11.SLAB_LEN                        ,'0'), 7,'0') --재료길이_11
				     ||LPAD(NVL(''                                   ,'0'), 5,'0') --재료외경_11
				     ||LPAD(NVL(''                                   ,'0'), 5,'0') --재료내경_11
				     ||RPAD(NVL(A.WORK_STATE_11                      ,' '), 1,' ') --작업상태_11
				     ||RPAD(DECODE(A.STL_NO_11,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_11
				     
				     ||RPAD(NVL(A.STL_NO_12                          ,' '),11,' ') --재료번호_12
				     ||RPAD(NVL(A.LOAD_LOC_CD_12                     ,' '), 2,' ') --차량적재위치_12
				     ||LPAD(NVL(C_12.SLAB_WT                         ,'0'), 5,'0') --재료중량_12
				     ||LPAD(NVL(TO_CHAR(C_12.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_12
				     ||LPAD(NVL(TO_CHAR(C_12.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_12
				     ||LPAD(NVL(C_12.SLAB_LEN                        ,'0'), 7,'0') --재료길이_12
				     ||LPAD(NVL(''                                   ,'0'), 5,'0') --재료외경_12
				     ||LPAD(NVL(''                                   , ''), 5,'0') --재료내경_12
				     ||RPAD(NVL(A.WORK_STATE_12                      ,' '), 1,' ') --작업상태_12
				     ||RPAD(DECODE(A.STL_NO_12,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_12
				     
				     ||RPAD(NVL(A.STL_NO_13                          ,' '),11,' ') --재료번호_13
				     ||RPAD(NVL(A.LOAD_LOC_CD_13                     ,' '), 2,' ') --차량적재위치_13
				     ||LPAD(NVL(C_13.SLAB_WT                         ,'0'), 5,'0') --재료중량_13
				     ||LPAD(NVL(TO_CHAR(C_13.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_13
				     ||LPAD(NVL(TO_CHAR(C_13.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_13
				     ||LPAD(NVL(C_13.SLAB_LEN                        ,'0'), 7,'0') --재료길이_13
				     ||LPAD(NVL(''                                   ,'0'), 5,'0') --재료외경_13
				     ||LPAD(NVL(''                                   ,'0'), 5,'0') --재료내경_13
				     ||RPAD(NVL(A.WORK_STATE_13                      ,' '), 1,' ') --작업상태_13
				     ||RPAD(DECODE(A.STL_NO_13,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_13
				     
				     ||RPAD(NVL(A.STL_NO_14                          ,' '),11,' ') --재료번호_14
				     ||RPAD(NVL(A.LOAD_LOC_CD_14                     ,' '), 2,' ') --차량적재위치_14
				     ||LPAD(NVL(C_14.SLAB_WT                         ,'0'), 5,'0') --재료중량_14
				     ||LPAD(NVL(TO_CHAR(C_14.SLAB_T,'FM000V000')     ,'0'), 6,'0') --재료두께_14
				     ||LPAD(NVL(TO_CHAR(C_14.SLAB_W,'FM0000V0')      ,'0'), 5,'0') --재료폭_14
				     ||LPAD(NVL(C_14.SLAB_LEN                        ,'0'), 7,'0') --재료길이_14
				     ||LPAD(NVL(''                                   ,'0'), 5,'0') --재료외경_14
				     ||LPAD(NVL(''                                   ,'0'), 5,'0') --재료내경_14
				     ||RPAD(NVL(A.WORK_STATE_14                      ,' '), 1,' ') --작업상태_14
				     ||RPAD(DECODE(A.STL_NO_14,NULL,' ',A.PT_LOAD_LOC)  , 6,' ') --동정보_14
				     
				       AS JMS_TC_MESSAGE --JMSTCMessage
				      
				  FROM (
				  
				        SELECT 'YDY3L008'           AS JMS_TC_CD
				              ,NVL(:V_PT_LOAD_LOC, '      ')       AS PT_LOAD_LOC
				              ,:V_CAR_NO            AS CAR_NO
				              ,:V_PT_CLS            AS PT_CLS
				              ,:V_WORK_CLS          AS WORK_CLS
				              ,:V_WORK_COIL_MAX_CNT AS WORK_COIL_MAX_CNT
				              
				              ,:V_STL_NO_0          AS STL_NO_0
				              ,:V_LOAD_LOC_CD_0     AS LOAD_LOC_CD_0
				              ,:V_WORK_STATE_0      AS WORK_STATE_0
				              
				              ,:V_STL_NO_1          AS STL_NO_1
				              ,:V_LOAD_LOC_CD_1     AS LOAD_LOC_CD_1
				              ,:V_WORK_STATE_1      AS WORK_STATE_1
				              
				              ,:V_STL_NO_2          AS STL_NO_2
				              ,:V_LOAD_LOC_CD_2     AS LOAD_LOC_CD_2
				              ,:V_WORK_STATE_2      AS WORK_STATE_2
				              
				              ,:V_STL_NO_3          AS STL_NO_3
				              ,:V_LOAD_LOC_CD_3     AS LOAD_LOC_CD_3
				              ,:V_WORK_STATE_3      AS WORK_STATE_3
				              
				              ,:V_STL_NO_4          AS STL_NO_4
				              ,:V_LOAD_LOC_CD_4     AS LOAD_LOC_CD_4
				              ,:V_WORK_STATE_4      AS WORK_STATE_4
				              
				              ,:V_STL_NO_5          AS STL_NO_5
				              ,:V_LOAD_LOC_CD_5     AS LOAD_LOC_CD_5
				              ,:V_WORK_STATE_5      AS WORK_STATE_5
				              
				              ,:V_STL_NO_6          AS STL_NO_6
				              ,:V_LOAD_LOC_CD_6     AS LOAD_LOC_CD_6
				              ,:V_WORK_STATE_6      AS WORK_STATE_6
				              
				              ,:V_STL_NO_7          AS STL_NO_7
				              ,:V_LOAD_LOC_CD_7     AS LOAD_LOC_CD_7
				              ,:V_WORK_STATE_7      AS WORK_STATE_7
				              
				              ,:V_STL_NO_8          AS STL_NO_8
				              ,:V_LOAD_LOC_CD_8     AS LOAD_LOC_CD_8
				              ,:V_WORK_STATE_8      AS WORK_STATE_8
				              
				              ,:V_STL_NO_9          AS STL_NO_9
				              ,:V_LOAD_LOC_CD_9     AS LOAD_LOC_CD_9
				              ,:V_WORK_STATE_9      AS WORK_STATE_9
				              
				              ,:V_STL_NO_10         AS STL_NO_10
				              ,:V_LOAD_LOC_CD_10    AS LOAD_LOC_CD_10
				              ,:V_WORK_STATE_10     AS WORK_STATE_10
				              
				              ,:V_STL_NO_11         AS STL_NO_11
				              ,:V_LOAD_LOC_CD_11    AS LOAD_LOC_CD_11
				              ,:V_WORK_STATE_11     AS WORK_STATE_11
				              
				              ,:V_STL_NO_12         AS STL_NO_12
				              ,:V_LOAD_LOC_CD_12    AS LOAD_LOC_CD_12
				              ,:V_WORK_STATE_12     AS WORK_STATE_12
				              
				              ,:V_STL_NO_13         AS STL_NO_13
				              ,:V_LOAD_LOC_CD_13    AS LOAD_LOC_CD_13
				              ,:V_WORK_STATE_13     AS WORK_STATE_13
				              
				              ,:V_STL_NO_14         AS STL_NO_14
				              ,:V_LOAD_LOC_CD_14    AS LOAD_LOC_CD_14
				              ,:V_WORK_STATE_14     AS WORK_STATE_14
				              
				        FROM   DUAL
				  
				       ) A
				     , TB_PT_SLABCOMM C_0
				     , TB_PT_SLABCOMM C_1
				     , TB_PT_SLABCOMM C_2
				     , TB_PT_SLABCOMM C_3
				     , TB_PT_SLABCOMM C_4
				     , TB_PT_SLABCOMM C_5
				     , TB_PT_SLABCOMM C_6
				     , TB_PT_SLABCOMM C_7
				     , TB_PT_SLABCOMM C_8
				     , TB_PT_SLABCOMM C_9
				     , TB_PT_SLABCOMM C_10
				     , TB_PT_SLABCOMM C_11
				     , TB_PT_SLABCOMM C_12
				     , TB_PT_SLABCOMM C_13
				     , TB_PT_SLABCOMM C_14
				 WHERE A.STL_NO_0  = C_0.SLAB_NO(+)
				   AND A.STL_NO_1  = C_1.SLAB_NO(+)
				   AND A.STL_NO_2  = C_2.SLAB_NO(+)
				   AND A.STL_NO_3  = C_3.SLAB_NO(+)
				   AND A.STL_NO_4  = C_4.SLAB_NO(+)
				   AND A.STL_NO_5  = C_5.SLAB_NO(+)
				   AND A.STL_NO_6  = C_6.SLAB_NO(+)
				   AND A.STL_NO_7  = C_7.SLAB_NO(+)
				   AND A.STL_NO_8  = C_8.SLAB_NO(+)
				   AND A.STL_NO_9  = C_9.SLAB_NO(+)
				   AND A.STL_NO_10 = C_10.SLAB_NO(+)
				   AND A.STL_NO_11 = C_11.SLAB_NO(+)
				   AND A.STL_NO_12 = C_12.SLAB_NO(+)
				   AND A.STL_NO_13 = C_13.SLAB_NO(+)
				   AND A.STL_NO_14 = C_14.SLAB_NO(+)
		    	 */
			}
			trtNm = trtNm + "(" + msgId + ") : 조회";
			
            JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select(jrParam, jspeed_query_id, logId, methodNm, trtNm);
					
				slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			}
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * L3 송신 전문 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : L3전문조회
	 *      
	 *      @param String msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgL3(String msgId, JDTORecord jrParam) throws DAOException {
		String methodNm = "L3전문조회[SlabYdCommDAO.getMsgL3] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("YDCTJ031UD".equals(msgId)) {
				trtNm = "장입진행실적(권상권하)";
				/*
				 * 장입진행실적(권상권하) 전문조회 
					SELECT DD.JMS_TC_CD                                              --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,MIN(DD.PTOP_PLNT_GP )               AS PTOP_PLNT_GP       --조업공장구분
					      ,MAX(ST.STL_APPEAR_GP)               AS STL_APPEAR_GP      --재료외형구분(Slab)
					      ,MIN(DD.PROG_STAT    )               AS CHG_SUP_PROG_STAT  --장입보급진행상태
					      ,MIN(DD.WR_OCCR_DT   )               AS WR_OCCR_DT         --실적발생일시
					      ,TO_CHAR(COUNT(*))                   AS YD_EQP_WR_CNT      --야드설비작업매수
					      ,MIN(DECODE(ROWNUM,1,ST.STL_NO))     AS STL_NO1            --재료번호1
					      ,MIN(DECODE(ROWNUM,2,ST.STL_NO))     AS STL_NO2            --재료번호2
					      ,MIN(DECODE(ROWNUM,3,ST.STL_NO))     AS STL_NO3            --재료번호3
					      ,MIN(DECODE(ROWNUM,4,ST.STL_NO))     AS STL_NO4            --재료번호4
					      ,MIN(DECODE(ROWNUM,5,ST.STL_NO))     AS STL_NO5            --재료번호5
					      ,MIN(DECODE(ROWNUM,6,ST.STL_NO))     AS STL_NO6            --재료번호6
					      ,MIN(DECODE(ROWNUM,7,ST.STL_NO))     AS STL_NO7            --재료번호7
					  FROM TB_YD_CRNSCH    CS
					      ,TB_YD_CRNWRKMTL CM
					      ,TB_YD_STOCK     ST
					      ,(SELECT BR.ITEM_VALUE1 AS JMS_TC_CD
					              ,BR.DTL_ITEM3   AS PTOP_PLNT_GP
					              ,BR.DTL_ITEM4   AS PROG_STAT
					              ,BR.DTL_ITEM2   AS YD_AIM_RT_GP
					              ,DD.YD_CRN_SCH_ID
					              ,DD.WR_OCCR_DT
					          FROM --VW_YD_YDB032 BR --Slab전문송신기준
					               TB_YD_RULE BR
					              ,(SELECT DECODE(:V_UP_DN_GP,'U','YDL008','YDL009')         AS MSG_ID --야드권상권하구분
					                      ,:V_YD_STK_COL_GP                                  AS YD_STK_COL_GP
					                      ,:V_YD_CRN_SCH_ID                                  AS YD_CRN_SCH_ID
					                      ,NVL(:V_WR_DT,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')) AS WR_OCCR_DT
					                  FROM DUAL) DD
					         WHERE BR.ITEM_VALUE1 = 'YDCTJ031'
					           AND BR.ITEM_VALUE2 = 'Y3'||DD.MSG_ID
					           AND BR.REPR_CD_GP         = 'DYD200'
					           AND BR.CD_GP              = 'D'
					           AND DD.YD_STK_COL_GP LIKE BR.DTL_ITEM1||'%') DD           
					 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					   AND CM.STL_NO        = ST.STL_NO
					   AND ST.YD_AIM_RT_GP  = DD.YD_AIM_RT_GP
					   AND CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID
					   AND CS.DEL_YN        = 'N'
					   AND CM.DEL_YN        = 'N'
					   AND ST.DEL_YN        = 'N'
					 GROUP BY DD.JMS_TC_CD
				 */
				//jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDCTJ033UD";
				jspeed_query_id =  "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDCTJ033UD";
			} else if ("YDCTJ031TI".equals(msgId)) {
				trtNm = "장입진행실적(Take-In)";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDCTJ033TI";
				/*
				 * --장입진행실적(Take-In) 전문조회 - com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDCTJ033TI
				    SELECT BR.ITEM_VALUE1   AS JMS_TC_CD                           --JMSTC코드
					      ,DD.WR_OCCR_DT    AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,BR.DTL_ITEM3     AS PTOP_PLNT_GP       --조업공장구분
					      ,'C'              AS STL_APPEAR_GP      --재료외형구분(Slab)
					      ,BR.DTL_ITEM4     AS CHG_SUP_PROG_STAT  --장입보급진행상태
					      ,DD.WR_OCCR_DT    AS WR_OCCR_DT         --실적발생일시
					      ,'1'              AS YD_EQP_WR_CNT      --야드설비작업매수
					      ,DD.STL_NO        AS STL_NO1            --재료번호1
					  FROM TB_YD_RULE BR --Slab전문송신기준
					      ,(SELECT :V_YD_STK_COL_GP AS YD_STK_COL_GP
					              ,:V_STL_NO        AS STL_NO
					              ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS WR_OCCR_DT
					          FROM DUAL) DD
					 WHERE BR.ITEM_VALUE1     = 'YDCTJ031'
					   AND BR.ITEM_VALUE2     = 'Y3YDL013'
					   AND REPR_CD_GP         = 'DYD200'
					   AND CD_GP              = 'D'
					   AND BR.DTL_ITEM1       =  DD.YD_STK_COL_GP
				
				 */
			} else if ("YDCTJ034".equals(msgId)) {
				trtNm = "이송하차실적";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDCTJ034";
                /*
                 * --이송하차실적 전문조회 
				SELECT 'YDCTJ034'                          AS JMS_TC_CD          --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,SC.SLAB_NO                                                --Slab번호
				  FROM TB_YD_CARFTMVMTL TM
				      ,TB_PT_SLABCOMM   SC
				 WHERE TM.STL_NO        = SC.SLAB_NO
				   AND TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND SC.SLAB_WO_RT_CD  IN ('PA','PB')
				   AND SC.REHEAT_SLAB_GP IN ('1','2')
                 */
			} else if ("YDPRJ003".equals(msgId)) {
				trtNm = "후판재열재슬라브적치실적";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDPRJ003";
                /*
                 * --후판재열재슬라브적치실적 전문조회 - 
				SELECT 'YDP'||DECODE(TS.SPOS_WLOC_CD,'DKY23','R','P')||'J003' AS JMS_TC_CD          --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')                    AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				      ,TM.STL_NO                                                                    --재료번호
				      ,NVL(:V_WR_DT,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'))      AS EXT_DT             --불출일시
				  FROM TB_YD_CARSCH     TS
				      ,TB_YD_CARFTMVMTL TM
				 WHERE TS.YD_CAR_SCH_ID = TM.YD_CAR_SCH_ID
				   AND TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND TS.SPOS_WLOC_CD IN ('DKY23','DWY23') --후판-극후물냉각대
				   AND TS.ARR_WLOC_CD  IN ('DKY21','DWY22') --후판-옥내 Yard
                 */
			} else if ("YDPTJ001Mslab".equals(msgId)) {
				trtNm = "Slab이송완료실적(주편)";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDPTJ001Mslab";
				/*
				 * --Slab이송완료실적(주편) 전문조회 - 
				SELECT 'YDPTJ001'                          AS JMS_TC_CD           --JMSTC코드
				      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT  --JMSTC생성일시
				      ,MSLAB_NO AS STL_NO                                         --재료번호
				      ,ORD_NO                                                     --주문번호
				      ,ORD_DTL                                                    --주문행번
				      ,PLNT_PROC_CD                                               --공장공정코드
				      ,STL_APPEAR_GP                                              --재료외형구분
				      ,CURR_PROG_CD                                               --현재진도코드
				      ,ORD_YEOJAE_GP                                              --주문여재구분
				      ,MSLAB_WT                            AS STL_WT              --재료중량
				      ,''                                  AS DS_MTL_WT           --설계재료중량
				      ,RECORD_PROG_STAT                    AS MTL_STAT_GP         --재료상태구분
				      ,RECORD_END_GP                                              --Record종료구분
				      ,''                                  AS RECORD_END_GP1      --Record종료구분1
				      ,BEFO_PROG_CD                                               --전진도코드
				      ,''                                  AS BEF_ORD_NO          --전주문번호
				      ,''                                  AS BEF_ORD_DTL         --전주문행번
				      ,''                                  AS MMATL_FEE_NO        --모재료번호
				      ,''                                  AS ORDERTRANS_MATCH_GP --목전충당구분
				  FROM TB_PT_MSLABCOMM
				 WHERE MSLAB_NO = :V_STL_NO
				   AND RECORD_PROG_STAT = '2' --진행
				 */
			} else if ("YDPTJ001Slab".equals(msgId)) {
				trtNm = "Slab이송완료실적(Slab)";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDPTJ001Slab";
                /*
                 * --Slab이송완료실적(Slab) 전문조회 - 
					SELECT 'YDPTJ001'                          AS JMS_TC_CD           --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT  --JMSTC생성일시
					      ,SLAB_NO AS STL_NO                                          --재료번호
					      ,ORD_NO                                                     --주문번호
					      ,ORD_DTL                                                    --주문행번
					      ,PLNT_PROC_CD                                               --공장공정코드
					      ,STL_APPEAR_GP                                              --재료외형구분
					      ,CURR_PROG_CD                                               --현재진도코드
					      ,ORD_YEOJAE_GP                                              --주문여재구분
					      ,SLAB_WT                             AS STL_WT              --재료중량
					      ,''                                  AS DS_MTL_WT           --설계재료중량
					      ,RECORD_PROG_STAT                    AS MTL_STAT_GP         --재료상태구분
					      ,RECORD_END_GP                                              --Record종료구분
					      ,''                                  AS RECORD_END_GP1      --Record종료구분1
					      ,BEFO_PROG_CD                                               --전진도코드
					      ,BEF_ORD_NO                                                 --전주문번호
					      ,BEF_ORD_DTL                                                --전주문행번
					      ,PARENT_SLAB_NO                      AS MMATL_FEE_NO        --모재료번호
					      ,MATCH_ORDERTRANS_GP                 AS ORDERTRANS_MATCH_GP --목전충당구분
					  FROM TB_PT_SLABCOMM
					 WHERE SLAB_NO = :V_STL_NO
					   AND RECORD_PROG_STAT = '2' --진행
                 */
				
			} else if ("YDTSJ007".equals(msgId)) {
				trtNm = "소재차량상차개시";
				/*
				 * (원본) com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDTSJ007
				SELECT 'YDTSJ007'                          AS JMS_TC_CD          --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
				     , CS.TRN_EQP_CD
				     , CS.SPOS_WLOC_CD
				     , SC.YD_PNT_CD    AS SPOS_YD_PNT_CD
				     , CS.ARR_WLOC_CD
				     , CS.YD_CARLD_ST_DT
				  FROM TB_YD_CARSCH   CS
				     , TB_YD_STKCOL   SC
				 WHERE CS.YD_CAR_SCH_ID     = :V_YD_CAR_SCH_ID
				   AND CS.YD_CARLD_STOP_LOC = SC.YD_STK_COL_GP
				   AND SC.YD_CAR_USE_GP = 'L'           --L:구내운송, G:출하차량
				   AND SC.DEL_YN        = 'N' 
				 */
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.TcYDTSJ007";
				//jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDTSJ007";

			} 
			else if("YDTSJ008".equals(msgId)) {
				trtNm = "소재차량상차완료";
				/*
				SELECT 'YDTSJ008'                          AS JMS_TC_CD            --JMSTC코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT   --JMSTC생성일시
				     , 'YDTSJ008'                          AS TC_CODE              --IF구분코드
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TC_CREATE_DDTT       --TC생성일시
				     , TRN_EQP_CD           --운송장비코드
				     , SPOS_WLOC_CD         --발지개소코드
				     , SPOS_YD_PNT_CD       --발지야드포인트코드
				     , ARR_WLOC_CD          --착지개소코드
				     , TRN_WRK_MTL_GP       --운송작업재료구분
				     , MTL_UGNT_GP          --재료긴급구분
				     , HCR_GP               --HCR구분
				     , CARLD_CMPL_DT        --상차완료일시
				     , CARLD_SH             --상차매수
				     , STL_NO1              --특수강재료번호1
				     , STL_WT1              --재료중량1
				     , STL_LOC1             --재료위치1
				     , STL_NO2              --특수강재료번호2
				     , STL_WT2              --재료중량2
				     , STL_LOC2             --재료위치2
				     , STL_NO3              --특수강재료번호3
				     , STL_WT3              --재료중량3
				     , STL_LOC3             --재료위치3
				     , STL_NO4              --특수강재료번호4
				     , STL_WT4              --재료중량4
				     , STL_LOC4             --재료위치4
				     , STL_NO5              --특수강재료번호5
				     , STL_WT5              --재료중량5
				     , STL_LOC5             --재료위치5
				     , STL_NO6              --특수강재료번호6
				     , STL_WT6              --재료중량6
				     , STL_LOC6             --재료위치6
				     , STL_NO7              --특수강재료번호7
				     , STL_WT7              --재료중량7
				     , STL_LOC7             --재료위치7
				     , STL_NO8              --특수강재료번호8
				     , STL_WT8              --재료중량8
				     , STL_LOC8             --재료위치8
				     , STL_NO9              --특수강재료번호9
				     , STL_WT9              --재료중량9
				     , STL_LOC9             --재료위치9
				     , STL_NO10             --특수강재료번호10
				     , STL_WT10             --재료중량10
				     , STL_LOC10            --재료위치10
				     , STL_NO11             --특수강재료번호11
				     , STL_WT11             --재료중량11
				     , STL_LOC11            --재료위치11
				     , STL_NO12             --특수강재료번호12
				     , STL_WT12             --재료중량12
				     , STL_LOC12            --재료위치12
				  FROM(
				       SELECT
				              DD.YD_CAR_SCH_ID
				            , MAX(DD.TRN_EQP_CD)     AS TRN_EQP_CD
				            , MAX(DD.SPOS_WLOC_CD)   AS SPOS_WLOC_CD
				            , MAX(DD.SPOS_YD_PNT_CD) AS SPOS_YD_PNT_CD
				            , MAX(DD.ARR_WLOC_CD)    AS ARR_WLOC_CD
				            , MAX(DD.TRN_WRK_MTL_GP) AS TRN_WRK_MTL_GP
				            , MAX(DD.MTL_UGNT_GP)    AS MTL_UGNT_GP
				            , MAX(DD.HCR_GP)         AS HCR_GP
				            , MAX(DD.CARLD_CMPL_DT)  AS CARLD_CMPL_DT
				            , COUNT(*)               AS CARLD_SH
				            , MAX(DECODE(NO,1,DD.STL_NO         ,''))    AS STL_NO1
				            , MAX(DECODE(NO,1,DD.YD_MTL_WT      ,''))    AS STL_WT1
				            , MAX(DECODE(NO,1,DD.YD_STK_LYR_NO  ,''))    AS STL_LOC1
				            , MAX(DECODE(NO,2,DD.STL_NO         ,''))    AS STL_NO2
				            , MAX(DECODE(NO,2,DD.YD_MTL_WT      ,''))    AS STL_WT2
				            , MAX(DECODE(NO,2,DD.YD_STK_LYR_NO  ,''))    AS STL_LOC2
				            , MAX(DECODE(NO,3,DD.STL_NO         ,''))    AS STL_NO3
				            , MAX(DECODE(NO,3,DD.YD_MTL_WT      ,''))    AS STL_WT3
				            , MAX(DECODE(NO,3,DD.YD_STK_LYR_NO  ,''))    AS STL_LOC3
				            , MAX(DECODE(NO,4,DD.STL_NO         ,''))    AS STL_NO4
				            , MAX(DECODE(NO,4,DD.YD_MTL_WT      ,''))    AS STL_WT4
				            , MAX(DECODE(NO,4,DD.YD_STK_LYR_NO  ,''))    AS STL_LOC4
				            , MAX(DECODE(NO,5,DD.STL_NO         ,''))    AS STL_NO5
				            , MAX(DECODE(NO,5,DD.YD_MTL_WT      ,''))    AS STL_WT5
				            , MAX(DECODE(NO,5,DD.YD_STK_LYR_NO  ,''))    AS STL_LOC5
				            , MAX(DECODE(NO,6,DD.STL_NO         ,''))    AS STL_NO6
				            , MAX(DECODE(NO,6,DD.YD_MTL_WT      ,''))    AS STL_WT6
				            , MAX(DECODE(NO,6,DD.YD_STK_LYR_NO  ,''))    AS STL_LOC6
				            , MAX(DECODE(NO,7,DD.STL_NO         ,''))    AS STL_NO7
				            , MAX(DECODE(NO,7,DD.YD_MTL_WT      ,''))    AS STL_WT7
				            , MAX(DECODE(NO,7,DD.YD_STK_LYR_NO  ,''))    AS STL_LOC7
				            , MAX(DECODE(NO,8,DD.STL_NO         ,''))    AS STL_NO8
				            , MAX(DECODE(NO,8,DD.YD_MTL_WT      ,''))    AS STL_WT8
				            , MAX(DECODE(NO,8,DD.YD_STK_LYR_NO  ,''))    AS STL_LOC8
				            , MAX(DECODE(NO,9,DD.STL_NO         ,''))    AS STL_NO9
				            , MAX(DECODE(NO,9,DD.YD_MTL_WT      ,''))    AS STL_WT9
				            , MAX(DECODE(NO,9,DD.YD_STK_LYR_NO  ,''))    AS STL_LOC9
				            , MAX(DECODE(NO,10,DD.STL_NO        ,''))    AS STL_NO10
				            , MAX(DECODE(NO,10,DD.YD_MTL_WT     ,''))    AS STL_WT10
				            , MAX(DECODE(NO,10,DD.YD_STK_LYR_NO ,''))    AS STL_LOC10
				            , MAX(DECODE(NO,11,DD.STL_NO        ,''))    AS STL_NO11
				            , MAX(DECODE(NO,11,DD.YD_MTL_WT     ,''))    AS STL_WT11
				            , MAX(DECODE(NO,11,DD.YD_STK_LYR_NO ,''))    AS STL_LOC11
				            , MAX(DECODE(NO,12,DD.STL_NO        ,''))    AS STL_NO12
				            , MAX(DECODE(NO,12,DD.YD_MTL_WT     ,''))    AS STL_WT12
				            , MAX(DECODE(NO,12,DD.YD_STK_LYR_NO ,''))    AS STL_LOC12
				         FROM(
				              SELECT A.YD_CAR_SCH_ID 
				                   , A.TRN_EQP_CD 
				                   , A.SPOS_WLOC_CD 
				                   , C.YD_PNT_CD                   AS SPOS_YD_PNT_CD
				                   , A.ARR_WLOC_CD 
				                   , 'S' AS TRN_WRK_MTL_GP
				                   , NVL(D.URGENT_FRTOMOVE_WORD_GP, 'N') AS MTL_UGNT_GP --'Y(긴급재),N(일반재)
				                   , NVL(B.HCR_GP,E.HCR_GP)              AS  HCR_GP
				                   , TO_CHAR(A.YD_CARLD_CMPL_DT, 'YYYYMMDDHH24MISS') AS CARLD_CMPL_DT
				                   , B.STL_NO
				                   , ROWNUM NO
				                   , E.SLAB_WT AS YD_MTL_WT 
				                   , B.YD_STK_BED_NO -- BED정보
				                   , B.YD_STK_LYR_NO -- 단정보
				               FROM TB_YD_CARSCH     A
				                  , TB_YD_CARFTMVMTL B
				                  , TB_YD_STKCOL     C
				                  --, TB_YD_STOCK      D
				                  ,(
				                      SELECT ST.*
				                           , DECODE(SC.REAL_MEASURE_SLAB_LEN,0,SC.SLAB_LEN, SC.REAL_MEASURE_SLAB_LEN) SLAB_LEN   -- (실측)길이
				                           , DECODE(SC.REAL_MEASURE_SLAB_T,  0,SC.SLAB_T  , SC.REAL_MEASURE_SLAB_T  ) SLAB_T     -- (실측)두께
				                           , DECODE(SC.REAL_MEASURE_SLAB_W,  0,SC.SLAB_W  , SC.REAL_MEASURE_SLAB_W  ) SLAB_W  -- (실측)폭
				                           , DECODE(SC.CAL_SLAB_WT,          0,SC.SLAB_WT , SC.CAL_SLAB_WT          ) SLAB_WT -- (계산)중량
				                        FROM USRYDA.TB_YD_STOCK    ST -- YD_저장품
				                           , USRPTA.TB_PT_SLABCOMM SC -- SLAB공통
				                       WHERE ST.STL_NO = SC.SLAB_NO
				                   ) D
				                  , TB_PT_SLABCOMM E
				                   
				              WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				                AND A.YD_CARLD_STOP_LOC = C.YD_STK_COL_GP 
				                AND B.STL_NO = D.STL_NO
				                AND B.STL_NO = E.SLAB_NO (+)
				
				             ) DD
				        GROUP BY YD_CAR_SCH_ID      
				      )    
				 WHERE 1 = 1
				 
				 */
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.TcYDTSJ008";
				//jspeed_query_id = "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.TcYDTSJ008";
				
			//+++++++++++++++++ 추가( end ) +++++++++++++++++++++++++++++++++++++++++++++	
			} else if ("YDTSJ009".equals(msgId)) {
				trtNm = "소재차량하차개시";
				//jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDTSJ009";
				/*
				 * 소재차량하차개시 전문조회 
					SELECT 'YDTSJ009'                          AS JMS_TC_CD          --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,TS.TRN_EQP_CD                                             --운송장비코드
					      ,TS.ARR_WLOC_CD                                            --착지개소코드
					      ,SC.YD_PNT_CD                        AS ARR_YD_PNT_CD      --착지야드포인트코드
					      ,NVL(:V_WR_DT,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')) AS TRN_WRK_ST_DT --운송작업시작일시
					  FROM TB_YD_STKCOL SC
					      ,TB_YD_CARSCH TS
					 WHERE SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
					   AND SC.TRN_EQP_CD    = TS.TRN_EQP_CD
					   AND SC.YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND SC.YD_CAR_USE_GP = 'L'           --구내운송
					   AND SC.DEL_YN        = 'N'
					   AND TS.YD_CAR_PROG_STAT IN ('B','C') --하차도착,검수
					   AND TS.DEL_YN        = 'N'
				 */
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDTSJ009";
				
			} else if ("YDTSJ010".equals(msgId)) {
				trtNm = "소재차량하차완료";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDTSJ010";
                /*
                 * --소재차량하차완료 전문조회 
					SELECT 'YDTSJ010'                          AS JMS_TC_CD          --JMSTC코드
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT --JMSTC생성일시
					      ,TS.TRN_EQP_CD                                             --운송장비코드
					      ,TS.ARR_WLOC_CD                                            --착지개소코드
					      ,SC.YD_PNT_CD                        AS ARR_YD_PNT_CD      --착지야드포인트코드
					      ,TO_CHAR(TS.YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS') AS CARUD_CMPL_DT --하차완료일시
					  FROM TB_YD_STKCOL SC
					      ,TB_YD_CARSCH TS
					 WHERE SC.YD_STK_COL_GP = TS.YD_CARUD_STOP_LOC
					   AND TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
                 */
			} else if ("YDSSJ001".equals(msgId)) {
				trtNm = "슬라브 이송실적(통계)";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDSSJ001";
                /*
                 *  
                 *  --com.inisteel.cim.yd.pslabyd.dao.PSlabCommDAO.getMsgYDSSJ001 
					SELECT 'YDSSJ001' AS JMS_TC_CD
					      ,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS JMS_TC_CREATE_DDTT
					      ,STL_NO
					      ,:V_FROM_LOC AS FROM_LOC
					      ,:V_TO_LOC   AS TO_LOC
					      ,:V_MODIFIER AS MODIFIER
					      ,:V_PGM_ID   AS PGM_ID
					      ,:V_ERP_HDS_DD AS ERP_HDS_DD
					 FROM TB_YD_CRNWRKMTL CM 
					WHERE 1=1
					 AND YD_CRN_sCH_ID = :V_CRN_SCH_ID
                 */
			}else {
				throw new Exception("정의되지 않은 전문ID[" + msgId + "] 입니다.");
			}

				
            JDTORecordSet jsRst = null;
			
			if(!"".equals(jspeed_query_id)) {
				trtNm = trtNm + "(" + msgId + ") : ";
				
				jsRst = this.select(jrParam, jspeed_query_id, logId, methodNm, trtNm);
			}
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}



	
	/***************************************************************************
	 * Flex Push Server 전송Data 조회
	 **************************************************************************/

	
	/***************************************************************************
	 * 기타 조회
	 **************************************************************************/

	
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
	public int update(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[PSlabCommDAO.update] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			slabUtils.printLog(logId, trtNm + "[PSlabCommDAO.update] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
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
	public JDTORecordSet select(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[PSlabCommDAO.select] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			
			//PIDEV:확인
//			queryId = ydPICommDAO.getYdRulePI("", mthdNm, "YD0001", queryId, "APPPI0", "*", "*" );			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			slabUtils.printLog(logId, trtNm+" 조회[PSlabCommDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
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
	public int insert(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[PSlabCommDAO.insert] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			slabUtils.printLog(logId, trtNm + "[PSlabCommDAO.insert] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;	
	}
	
	/**
	 *      [A] 오퍼레이션명 : conversionFieldname 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         int intGp             // 구분(0:"V_" 추가, 1:"V_" 제거
	 * @return JDTORecord			 // 필드명을 변환한 결과레코드
	 * @throws JDTOException 
	 */
	public JDTORecord conversionFieldname(JDTORecord recPara, int intGp) throws JDTOException {
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		String szFieldName = null;
		Iterator itrFieldName = null;
		
		//필드명을 가져온다.
		itrFieldName = recPara.iterateName();
		
		//필드명 갯수만큼 루프를 돈다.
		while(itrFieldName.hasNext()) {
			
			szFieldName = (String)itrFieldName.next();
			//"V_" 추가
			if (intGp == 0) {
				recRtnVal.setField("V_" + szFieldName, recPara.getField(szFieldName));
			//"V_" 제거
			} else {
				recRtnVal.setField(szFieldName.substring(2), recPara.getField(szFieldName));
			}
		}
		
		return recRtnVal ;
	}

	/**
	 *      [A] 오퍼레이션명 : 저장위치별현황 조회 (JSP에서 바로 조회)
	 *      염용선 2020-08-18
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getStrLocStat(JDTORecord jrParam) throws DAOException {
		String methodNm = "저장위치별현황조회[PSlabYdJspDAO.getStrLocStat]";
		String logId = slabUtils.getLogId();
		try {
			String jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getStrLocStat";
			
			JDTORecordSet jrRst = this.select(jrParam, jspeed_query_id, logId, methodNm, "저장위치별현황조회");
			return jrRst;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(slabUtils.getLogId(), methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : Slab야드 크레인스케줄 등록
	 *      
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String trtGp
	 *      @param String[][] param
	 *      @return int
	 *      @throws DAOException
	*/
	public int insYDYDJ401(String trtGp, String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "크레인스케줄[PSlabYdSchDAO.insYDYDJ401] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			if ("StkLyrD".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 권하대기 수정";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.updYDYDJ401StkLyrD";
			} else if ("CrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.insYDYDJ401CrnSch";
			} else if ("CrnMtl".equals(trtGp)) {
				trtNm = "크레인작업재료(TB_YD_CRNWRKMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdSchSeEJB.insYDYDJ401CrnMtl";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/**
	 * 코드맵핑재료수정
	 * 박영수  2020-09-10
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public String uptStockCodeMapping(JDTORecord recPara, String userId, String methodNm2) throws JDTOException {
		String methodNm 		= "코드맵핑재료수정[PSlabCommDAO.uptStockCodeMapping]" + methodNm2;
		String logId 			= slabUtils.getLogId();
		String szRtnMsg			= PSlabYdConstant.RETN_CD_SUCCESS;
		String szMethodName		= "uptStockCodeMapping";
		String sModifier           = userId; 		
	
		String szMsg			= "";
		String szPtTbComm		= slabUtils.paraRecChkNull(recPara, "PT_TB_COMM");					//주편/슬라브구분
		String szStlNo			= slabUtils.paraRecChkNull(recPara, "STL_NO");						//재료번호
		String szSlabWoRtCd 	= slabUtils.paraRecChkNull(recPara, "SLAB_WO_RT_CD");				//슬라브지시행선코드
		String szOrdYeojaeGp 	= slabUtils.paraRecChkNull(recPara, "ORD_YEOJAE_GP");				//주여구분
		String szScarfingYn 	= slabUtils.paraRecChkNull(recPara, "SCARFING_YN");					//스카핑여부
		String szScarfingDoneYn = slabUtils.paraRecChkNull(recPara, "SCARFING_DONE_YN");			//스카핑완료여부
		String szMillWoExn 	    = slabUtils.paraRecChkNull(recPara, "MILL_WO_EXN");					//압연지시
		String szYdGp			= slabUtils.paraRecChkNull(recPara, "YD_GP");						//야드구분
		String szStlAppearGp	= slabUtils.paraRecChkNull(recPara, "STL_APPEAR_GP");				//재료외형구분
		String szHcrGp			= slabUtils.paraRecChkNull(recPara, "HCR_GP");						//HCR구분
		
		int    intRtnVal		= -100;
		JDTORecord jrParam  	= slabUtils.getParam(logId, methodNm, sModifier);  	//DAO Parameter - Log ID, Method, 수정자 Set
		
		szMsg="["+methodNm+"] 메소드 시작 - 파라미터 확인";
		slabUtils.printLog(logId, szMsg +" STL_NO:"		+ szStlNo, 		"SL");   
		slabUtils.printLog(logId, "PT_TB_COMM:"	+ szPtTbComm + " SLAB_WO_RT_CD:"+ szSlabWoRtCd + " ORD_YEOJAE_GP:"+ szOrdYeojaeGp
				                + " SCARFING_YN:"	+ szScarfingYn + " SCARFING_DONE_YN:"+ szScarfingDoneYn + " MILL_WO_EXN:"	+ szMillWoExn
				                + " YD_GP:"		+ szYdGp + " STL_APPEAR_GP:"+ szStlAppearGp + " HCR_GP:"		+ szHcrGp	, 	"SL");  
		String szCurrProgCd	= "";																	//재료진도코드
		
		if( "".equals(szScarfingDoneYn) ) {
			szMsg= szStlNo + "]의 szSCARFING_DONE_YN값이 없으므로 N으로 설정";
			slabUtils.printLog(logId, szMsg, "SL");  
			szScarfingDoneYn = "N";
		}
		if( "".equals(szMillWoExn) ) {
			szMsg=" 재료[" + szStlNo + "]의 압연지시(MILL_WO_EXN)값이 없으므로 N으로 설정";
			slabUtils.printLog(logId, szMsg, "SL");  
			szMillWoExn = "N";
		}
		szMsg=" (PSlabYdComm) 재료[" + szStlNo + "]에 대한 재료진도판단 시작 - 주편/슬라브구분["+szPtTbComm+"], 슬라브지시행선코드["+szSlabWoRtCd
					 +"], 주여구분["+szOrdYeojaeGp
					 +"], 스카핑여부["+szScarfingYn
					 +"], 스카핑완료여부["+szScarfingDoneYn
					 +"], 압연지시구분["+szMillWoExn+"]" +"\n "
					 +"야드구분[" + szYdGp +"], 재료외형구분[" + szStlAppearGp + "], HCR구분["+ szHcrGp + "]";
		slabUtils.printLog(logId, szMsg, "SL");  
		
	    //List FrtoProductList = null;
    	//공정 함수를 이용한 진도코드 가져오기
    	if("B".equals(szPtTbComm)){
    		//주편 공통
    		String queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListcurrprogcd";
    		JDTORecordSet recordSet = this.select(recPara, queryId, logId, methodNm, "주편 공통");	
        	if(recordSet.size() > 0 ) {
        		//szCurrProgCd = slabUtils.nvl(recordSet.getFieldString("CURR_PROG_CD"), ""); 
        		szCurrProgCd = slabUtils.trim(recordSet.getRecord(0).getFieldString("CURR_PROG_CD"));
        	}

    	}else if ("S".equals(szPtTbComm)) {
    		//슬라브 공통
    		/*--회송차량의 재료의 경우 진도코드 UPDATE 하지 않는다.
    		 WITH TEMP_TABLE AS (
				   SELECT :V_SLAB_NO AS V_SLAB_NO FROM DUAL
				)
				SELECT CASE WHEN (SELECT COUNT(*)
				  FROM TB_YD_RETHTHIST
				      ,TEMP_TABLE A
				 WHERE 1=1
				   AND YD_CAR_SCH_ID IN (
				                    SELECT CS.YD_CAR_SCH_ID
				                      FROM TB_YD_CARSCH CS
				                          ,TB_YD_CARFTMVMTL CM
				                          ,TEMP_TABLE A
				                     WHERE 1=1
				                       AND CM.STL_NO = A.V_SLAB_NO
				                       AND CS.YD_CAR_SCH_ID = CM.YD_CAR_SCH_ID
				                       AND CS.DEL_YN = 'N'
				       )
				   AND STL_NO = A.V_SLAB_NO) >0 THEN BEFO_PROG_CD
				   
				   ELSE USRPMA.IHSF_PM_주편SLAB진도찾기(STL_APPEAR_GP,SLAB_NO) END AS CURR_PROG_CD
				
				FROM USRPTA.TB_PT_SLABCOMM
				    ,TEMP_TABLE A
				
				WHERE SLAB_NO= A.V_SLAB_NO
    		 * 
    		 */
    		recPara.setField("SLAB_NO", 				szStlNo);
    		JDTORecordSet recordSet = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getListcurrprogcdSlab", logId, methodNm, "슬라브 공통");	
    		if(recordSet.size() > 0 ) {
    			//szCurrProgCd = slabUtils.nvl(recordSet.getFieldString("CURR_PROG_CD"), ""); 
    			szCurrProgCd = slabUtils.trim(recordSet.getRecord(0).getFieldString("CURR_PROG_CD"));
    		}
    	}       	
    	//JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
    	//szCurrProgCd =StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
		slabUtils.printLog(logId, "()IHSF_PM_주편SLAB진도찾기==>>:"+szCurrProgCd, "SL");  
		//---------------------------------------------------------------------------------------------------------
		szMsg=" 재료[" + szStlNo + "]에 대한 코드맵핑 시작 - 재료진도코드["+szCurrProgCd+"]";
		slabUtils.printLog(logId, szMsg, "SL");  
		
		szMsg=" 목표야드["+szYdGp+"]를 코드맵핑시 야드구분으로 사용";
		slabUtils.printLog(logId, szMsg, "SL");  
		
		JDTORecord recStock = JDTORecordFactory.getInstance().create();
		recStock.setField("YD_GP", 				szYdGp);
		recStock.setField("SLAB_WO_RT_CD", 		szSlabWoRtCd);
		recStock.setField("STL_APPEAR_GP", 		szStlAppearGp);
		recStock.setField("HCR_GP", 			szHcrGp);
		recStock.setField("ORD_YEOJAE_GP", 		szOrdYeojaeGp);
		recStock.setField("SCARFING_YN", 		szScarfingYn);
		recStock.setField("SCARFING_DONE_YN", 	szScarfingDoneYn);
		recStock.setField("CURR_PROG_CD", 		szCurrProgCd);
		recStock.setField("ARR_WLOC_CD", 		"");
		recStock.setField("MILL_WO_EXN", 		szMillWoExn);
		
		intRtnVal = this.CallMapping(recStock, jrParam, szPtTbComm, logId, methodNm2, sModifier);
		
		JDTORecord recTemp = JDTORecordFactory.getInstance().create();
		recTemp.addRecord(jrParam);
		
		recTemp.setField("STL_NO", 		szStlNo);
		recTemp.setField("MODIFIER", 	szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		//intRtnVal = ydstockdao.updYdStock(recTemp, 0);
		/*
		 *   UPDATE TB_YD_STOCK 
			   SET SNDBK_GP      = :V_SNDBK_GP
			     , MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , SNDBK_GP_ETC  = :V_SNDBK_GP_ETC
			 WHERE STL_NO        = :V_STL_NO
		 */
		intRtnVal = this.update(recTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdStock", logId, methodNm, "슬라브 수정");	
		if( intRtnVal <= 0 ) {
			szMsg=" 재료["+szStlNo+"]의 속성을 수정 시 오류발생 - 반환값 : " + intRtnVal;
			slabUtils.printLog(logId, szMsg, "SL");  
			return PSlabYdConstant.RETN_CD_NOTEXIST;
		}else{
			szMsg=" 재료["+szStlNo+"]의 속성을 수정 성공";
			slabUtils.printLog(logId, szMsg, "SL");  
		}
		
		szMsg="["+methodNm+"] 메소드 끝";
		slabUtils.printLog(logId, szMsg, "SL");  
		
		return szRtnMsg;
	}
	
	/**
	 * 공통테이블에서 재료정보를 조회하는 메소드
	 * 박영수  2020-09-10
	 * @param szSTL_NO
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public String getPtCommStock(String szStlNo, JDTORecordSet rsResult , String usrId) throws JDTOException {
		String 			methodNm 			= "공통테이블에서 재료정보를 조회하는 메소드[PSlabCommDAO.getPtCommStock]";
		String 			logId 				= slabUtils.getLogId();
		String          sModifier           = usrId;
		String 			szRtnMsg 			= PSlabYdConstant.RETN_CD_SUCCESS;
		String 			szLogMsg 			= "";							//로그메세지
		String          szPtTbComm          = "";
		int 			intRtnVal 			= 0;
		JDTORecord 		recInParam 			= slabUtils.getParam(logId, methodNm, sModifier); 
		JDTORecord 		recTemp 			= slabUtils.getParam(logId, methodNm, sModifier); 
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			slabUtils.printLog(logId, "szStlNo(input):"+ szStlNo, "SL");
			
			recInParam.setField("MSLAB_NO", szStlNo);
			//주편공통테이블에서 재료를 먼저 조회한다.
			/*
			 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMSLABCOMM
			 * 						
				SELECT 
					 MSLAB_NO                                              AS MSLAB_NO
					,HEAT_NO                                               AS HEAT_NO
					,PLN_MSLAB_NO                                          AS PLN_MSLAB_NO
					,FNL_REG_PGM                                           AS FNL_REG_PGM
					,PLNT_PROC_CD                                          AS PLNT_PROC_CD
					,CC_PLNT_GP                                            AS CC_PLNT_GP
					,STL_APPEAR_GP                                         AS STL_APPEAR_GP
					,RECORD_PROG_STAT                                      AS RECORD_PROG_STAT
					,RECORD_END_GP                                         AS RECORD_END_GP
					,TO_CHAR(RECORD_END_DDTT, 'YYYYMMDDHH24MISS')          AS RECORD_END_DDTT
					,CURR_PROG_CD_REG_PGM                                  AS CURR_PROG_CD_REG_PGM
					,TO_CHAR(CURR_PROG_REG_DDTT, 'YYYYMMDDHH24MISS')       AS CURR_PROG_REG_DDTT
					,CURR_PROG_CD                                          AS CURR_PROG_CD
					,BEFO_PROG_CD_REG_PGM                                  AS BEFO_PROG_CD_REG_PGM
					,TO_CHAR(BEFO_PROG_REG_DDTT, 'YYYYMMDDHH24MISS')       AS BEFO_PROG_REG_DDTT
					,BEFO_PROG_CD                                          AS BEFO_PROG_CD
					,BEFOBEFO_PROG_CD_REG_PGM                              AS BEFOBEFO_PROG_CD_REG_PGM
					,TO_CHAR(BEFOBEFO_PROG_REG_DDTT, 'YYYYMMDDHH24MISS')   AS BEFOBEFO_PROG_REG_DDTT
					,BEFOBEFO_PROG_CD                                      AS BEFOBEFO_PROG_CD
					,ORD_YEOJAE_GP                                         AS ORD_YEOJAE_GP
					,ORD_NO                                                AS ORD_NO
					,ORD_DTL                                               AS ORD_DTL
					,PL_MSLAB_STR_GP                                       AS PL_MSLAB_STR_GP
					,REAL_MEASURE_SLAB_T                                   AS MSLAB_T
					,REAL_MEASURE_SLAB_W                                   AS MSLAB_W
					,REAL_MEASURE_SLAB_LEN                                 AS MSLAB_L
					,CAL_SLAB_WT                                           AS MSLAB_WT
					,STLQLTY_SYM                                           AS STLQLTY_SYM
					,SPEC_ABBSYM                                           AS SPEC_ABBSYM
					,USAGE_CD                                              AS USAGE_CD
					,ITEMNAME_CD                                           AS ITEMNAME_CD
					,CC_CCM_NO                                             AS CC_CCM_NO
					,SLAB_WO_RT_CD                                         AS SLAB_WO_RT_CD
					,WR_HCR_GP                                             AS WR_HCR_GP
					,WR_HCR_GP                                             AS HCR_GP
					,ORD_HCR_GP                                            AS ORD_HCR_GP
					,SPEC_HEATOUT_AIM                                      AS SPEC_HEATOUT_AIM
					,TO_CHAR(MSLAB_FS_CMPL_DT, 'YYYYMMDDHH24MISS')         AS MSLAB_FS_CMPL_DT
					,INGR_STAMP_DATE                                       AS INGR_STAMP_DATE
					,INGR_STAMP_GRADE                                      AS INGR_STAMP_GRADE
					,YEOJAE_CAUSE_CD                                       AS YEOJAE_CAUSE_CD
					,YEOJAE_OCCUR_DATE                                     AS YEOJAE_OCCUR_DATE
					,YEOJAE_OCCUR_TIME                                     AS YEOJAE_OCCUR_TIME
					,TO_CHAR(TRS_INDI_DT, 'YYYYMMDDHH24MISS')              AS TRS_INDI_DT
					,YD_GP                                                 AS YD_GP
					,YD_BAY_GP                                             AS YD_BAY_GP
					,YD_EQP_GP                                             AS YD_EQP_GP
					,YD_STK_COL_NO                                         AS YD_STK_COL_NO
					,YD_STK_BED_NO                                         AS YD_STK_BED_NO
					,YD_STK_LYR_NO                                         AS YD_STK_LYR_NO
					,YD_STR_LOC                                            AS YD_STR_LOC
					,YD_STR_LOC_HIS1                                       AS YD_STR_LOC_HIS1
					,YD_STR_LOC_HIS2                                       AS YD_STR_LOC_HIS2
					,TO_CHAR(MATL_FTMV_DT, 'YYYYMMDDHH24MISS')             AS MATL_FTMV_DT
					,TO_CHAR(MATL_TKOV_DT, 'YYYYMMDDHH24MISS')             AS MATL_TKOV_DT
					,SCARFING_YN                                           AS SCARFING_YN
					,TO_CHAR(SCARF_WRK_DT, 'YYYYMMDDHH24MISS')             AS SCARF_WRK_DT
					,WO_ITM                                                AS WO_ITM
					,PRD_ITM_CD                                            AS PRD_ITM_CD
					,REGISTER                                              AS REGISTER
					,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')                 AS REG_DDTT
					,MODIFIER                                              AS MODIFIER
					,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')                 AS MOD_DDTT
					,IG_STMP_GP                                            AS IG_STMP_GP
					,MSLAB_ASGN_GP                                         AS MSLAB_ASGN_GP
					,WO_MSLAB_RPR_MTD                                      AS WO_MSLAB_RPR_MTD
					,SCARFING_DEPTH                                        AS SCARFING_DEPTH
					,SCARFING_DONE_YN                                      AS SCARFING_DONE_YN
					,CS_WRK_HDS_DD                                         AS CS_WRK_HDS_DD
					,ND2_TRIM_TT_MSLAB_SF_TMP                              AS ND2_TRIM_TT_MSLAB_SF_TMP
					,STACK_LOT_NO                                          AS STACK_LOT_NO
					,PTOP_PLNT_GP                                          AS PTOP_PLNT_GP
					,MSLAB_RPR_MTD1                                        AS MSLAB_RPR_MTD1
					,MSLAB_RPR_MTD2                                        AS MSLAB_RPR_MTD2
					,MSLAB_RPR_MTD3                                        AS MSLAB_RPR_MTD3
					,MSLAB_RPR_MTD4                                        AS MSLAB_RPR_MTD4
					,PRKD_CD                                               AS PRKD_CD
					,INSPECT_DATE                                          AS INSPECT_DATE
					,REAL_MEASURE_SLAB_T                                   AS REAL_MEASURE_SLAB_T
					,REAL_MEASURE_SLAB_W                                   AS REAL_MEASURE_SLAB_W
					,REAL_MEASURE_SLAB_LEN                                 AS REAL_MEASURE_SLAB_LEN
					,CAL_SLAB_WT                                           AS CAL_SLAB_WT
					,WDH_STAMP_GRADE                                       AS WDH_STAMP_GRADE
					,WDH_STAMP_CAUSE_CD                                    AS WDH_STAMP_CAUSE_CD
					,SURFACE_STAMP_GRADE                                   AS SURFACE_STAMP_GRADE
					,SURFACE_STAMP_CAUSE_CD                                AS SURFACE_STAMP_CAUSE_CD
					,FORM_STAMP_GRADE                                      AS FORM_STAMP_GRADE
					,FORM_STAMP_CAUSE_CD                                   AS FORM_STAMP_CAUSE_CD
					,INSPECT_GRADE                                         AS INSPECT_GRADE
					,CAMBER_YN                                             AS CAMBER_YN
					,LONG_BOW_YN                                           AS LONG_BOW_YN
					,LONG_BOW_VAL                                          AS LONG_BOW_VAL
					,SLAB_SHEAR_DF_CD1                                     AS SLAB_SHEAR_DF_CD1
					,SLAB_SHEAR_DF_CD2                                     AS SLAB_SHEAR_DF_CD2
					,SLAB_SHEAR_DF_CD3                                     AS SLAB_SHEAR_DF_CD3
					,SLAB_SHEAR_DF_CD4                                     AS SLAB_SHEAR_DF_CD4
					,SLAB_SHEAR_DF_CD5                                     AS SLAB_SHEAR_DF_CD5
					,SLAB_SHEAR_DF_GRD1                                    AS SLAB_SHEAR_DF_GRD1
					,SLAB_SHEAR_DF_GRD2                                    AS SLAB_SHEAR_DF_GRD2
					,SLAB_SHEAR_DF_GRD3                                    AS SLAB_SHEAR_DF_GRD3
					,SLAB_SHEAR_DF_GRD4                                    AS SLAB_SHEAR_DF_GRD4
					,SLAB_SHEAR_DF_GRD5                                    AS SLAB_SHEAR_DF_GRD5
					,REAGENT_PICK_TARGET_YN                                AS REAGENT_PICK_TARGET_YN
					,REAGENTPICK_TARGET_ASSIGN_DATE                        AS REAGENTPICK_TARGET_ASSIGN_DATE
					,REAGENTPICK_TARGET_ASSIGN_HR                          AS REAGENTPICK_TARGET_ASSIGN_HR
					,REAGENTPICK_DONE_YN                                   AS REAGENTPICK_DONE_YN
					,REAGENTPICK_DONE_DATE                                 AS REAGENTPICK_DONE_DATE
					,REAGENTPICK_DONE_HR                                   AS REAGENTPICK_DONE_HR
					,DESIGN_SPEC_ABBSYM                                    AS DESIGN_SPEC_ABBSYM
					,SURFACE_INSPECT_DATE                                  AS SURFACE_INSPECT_DATE
					,REAGENT_PICK_LEN                                      AS REAGENT_PICK_LEN
					,SCARFING_COUNT                                        AS SCARFING_COUNT
					,SCARFING_INSPECT_YN                                   AS SCARFING_INSPECT_YN
					,DEFECT_REMOVE_YN                                      AS DEFECT_REMOVE_YN
					,SCARFING_INSPECT_SLAB_W_MIN                           AS SCARFING_INSPECT_SLAB_W_MIN
					,SCARFING_INSPECT_SLAB_W_MAX                           AS SCARFING_INSPECT_SLAB_W_MAX
					,SCARFING_INSPECT_SLAB_W_AVG                           AS SCARFING_INSPECT_SLAB_W_AVG
					,SURFACE_INSPECT_TIME                                  AS SURFACE_INSPECT_TIME
					,SURFACE_INSPECT_INI_DATE                              AS SURFACE_INSPECT_INI_DATE
					,WDH_FORM_INSPECT_RSLT_MSG                             AS WDH_FORM_INSPECT_RSLT_MSG
					,INSPECTOR_MSG                                         AS INSPECTOR_MSG
					,MS_SLAB_T                                             AS MS_SLAB_T
					,MS_SLAB_W                                             AS MS_SLAB_W
					,MS_SLAB_LEN                                           AS MS_SLAB_LEN
					,MS_SLAB_WT                                            AS MS_SLAB_WT
					,MS_SLAB_ALLOC_WT                                      AS MS_SLAB_ALLOC_WT
					,MS_SPEC_ABBSYM                                        AS MS_SPEC_ABBSYM
					,DEMANDER_CD                                           AS DEMANDER_CD
					,MSLAB_FS_HDS_DD                                       AS MSLAB_FS_HDS_DD
					,FS_WRK_DD                                             AS FS_WRK_DD
					,MSLAB_FS_WD                                           AS MSLAB_FS_WD
					,MSLAB_OTSTKND_GP                                      AS MSLAB_OTSTKND_GP
					,HT_IN_FNL_MSLAB_YN                                    AS HT_IN_FNL_MSLAB_YN
					,OVER_YEOJAE_GP                                        AS OVER_YEOJAE_GP
					,MSLAB_FRM_GP                                          AS MSLAB_FRM_GP
					,SCARFING_SIGN                                         AS SCARFING_SIGN
					,MSLAB_DSCD_WO_L                                       AS MSLAB_DSCD_WO_L
					,REAGENT_PICK_YN                                       AS REAGENT_PICK_YN
					,TAPERED_GP                                            AS TAPERED_GP
					,TAPERED_WD_W                                          AS TAPERED_WD_W
					,TAPERED_NARR_W                                        AS TAPERED_NARR_W
					,SCARF_OPRNER_SET_GP                                   AS SCARF_OPRNER_SET_GP
					,SCARF_OPRNER_SET_RSN_GP                               AS SCARF_OPRNER_SET_RSN_GP
					,TO_CHAR(SCARF_OPRNER_SET_DT, 'YYYYMMDDHH24MISS')      ASSCARF_OPRNER_SET_DT
					,SCARF_OPRNER_SET_EMPNO                                ASSCARF_OPRNER_SET_EMPNO
					,SCARF_LOC                                             AS SCARF_LOC
					,MSLAB_IN_SLAB_WO_SH                                   AS MSLAB_IN_SLAB_WO_SH
					,MSLAB_IN_SLAB_PRD_SH                                  AS MSLAB_IN_SLAB_PRD_SH
					,MATL_TRTWT                                            ASMATL_TRTWT
					,MSLAB_ND2_MNUL_W_TRIM_NO                              AS MSLAB_ND2_MNUL_W_TRIM_NO
					,MSLAB_ND2_MNUL_L_TRIM_NO                              AS MSLAB_ND2_MNUL_L_TRIM_NO
					,MSLAB_ND2_CCM_W_TRIM_NO                               AS MSLAB_ND2_CCM_W_TRIM_NO
					,CC_SCARF_OCCR_HDS_DD                                  AS CC_SCARF_OCCR_HDS_DD
					,SCARF_WRK_DD                                          AS SCARF_WRK_DD
					,SCARF_WD                                              AS SCARF_WD
					,SCARF_INSR_EMPNO                                      AS SCARF_INSR_EMPNO
					,TOP_SCARF_AREA_RT                                     AS TOP_SCARF_AREA_RT
					,BOT_SCARF_AREA_RT                                     AS BOT_SCARF_AREA_RT
					,AVG_SCARF_AREA_RT                                     AS AVG_SCARF_AREA_RT
					,SLAB_DF_REM                                           AS SLAB_DF_REM
				FROM USRPTA.TB_PT_MSLABCOMM
				WHERE MSLAB_NO = :V_MSLAB_NO

			 */
			JDTORecordSet jrRst = this.select(recInParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getMSLABCOMM", logId, methodNm, "주편 공통");
			
			szLogMsg = "주편공통테이블에서 재료["+szStlNo+"]를 먼저 조회한다" ;
			intRtnVal = jrRst.size();  
            slabUtils.printLog(logId, szLogMsg, "SL"); 
			if( intRtnVal <= 0 ) {
				szLogMsg = "주편공통테이블에서 재료["+szStlNo+"]가 존재하지 않으므로 슬라브공통테이블을 조회한다";
	            slabUtils.printLog(logId, szLogMsg, "SL"); 
				recInParam.setField("SLAB_NO", szStlNo);
				/* 슬라브공통테이블 조회
				 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMM
				 * 
				SELECT 
					SLAB_NO                                        AS SLAB_NO    
					,MSLAB_NO                                      AS MSLAB_NO
					,PLAN_SLAB_NO                                  AS PLAN_SLAB_NO
					,FNL_REG_PGM                                   AS FNL_REG_PGM
					,PLNT_PROC_CD                                  AS PLNT_PROC_CD
					,SCARFING_YN                                   AS SCARFING_YN
					,NVL(SCARFING_YN, 'N')                         AS BEFORE_SCARFING_YN
					,SCARFING_DONE_YN                              AS SCARFING_DONE_YN
					,NVL(SCARFING_DONE_YN, 'N')                    AS BEFORE_SCARFING_DONE_YN
					,CC_PLNT_GP                                    AS CC_PLNT_GP
					,RECORD_PROG_STAT                              AS RECORD_PROG_STAT
					,TO_CHAR(SLAB_CREATE_DDTT, 'YYYYMMDDHH24MISS') AS SLAB_CREATE_DDTT
					,SLAB_CREATE_GP                                AS SLAB_CREATE_GP
					,RECORD_END_GP                                 AS RECORD_END_GP
					,TO_CHAR(RECORD_END_DDTT, 'YYYYMMDDHH24MISS')  AS RECORD_END_DDTT
					,CURR_PROG_CD_REG_PGM                            AS CURR_PROG_CD_REG_PGM
					,TO_CHAR(CURR_PROG_REG_DDTT, 'YYYYMMDDHH24MISS') AS CURR_PROG_REG_DDTT
					,CURR_PROG_CD                                  AS CURR_PROG_CD
					,BEFO_PROG_CD_REG_PGM                            AS BEFO_PROG_CD_REG_PGM
					,TO_CHAR(BEFO_PROG_REG_DDTT, 'YYYYMMDDHH24MISS') AS BEFO_PROG_REG_DDTT
					,BEFO_PROG_CD                                  AS BEFO_PROG_CD
					,BEFOBEFO_PROG_CD_REG_PGM                      AS BEFOBEFO_PROG_CD_REG_PGM
					,TO_CHAR(BEFOBEFO_PROG_REG_DDTT, 'YYYYMMDDHH24MISS') AS BEFOBEFO_PROG_REG_DDTT
					,BEFOBEFO_PROG_CD                              AS BEFOBEFO_PROG_CD
					,ORD_YEOJAE_GP                                 AS ORD_YEOJAE_GP
					,ORD_NO                                        AS ORD_NO
					,ORD_DTL                                       AS ORD_DTL
					,SPEC_HEATOUT_AIM                              AS SPEC_HEATOUT_AIM
					,SPEC_ABBSYM                                   AS SPEC_ABBSYM
					,USAGE_CD                                      AS USAGE_CD
					,REAL_MEASURE_SLAB_T                           AS SLAB_T
					,REAL_MEASURE_SLAB_W                           AS SLAB_W
					,REAL_MEASURE_SLAB_LEN                         AS SLAB_LEN
					,CAL_SLAB_WT                                   AS SLAB_WT
					,SLAB_IORATE_WT                                AS SLAB_IORATE_WT
					,SLAB_TRIM_GP                                  AS SLAB_TRIM_GP
					,SLAB_TRIM_QNTY                                AS SLAB_TRIM_QNTY
					,SLAB_TRIM_DATE                                AS SLAB_TRIM_DATE
					,CC_FST_FNL_SLAB_GP                            AS CC_FST_FNL_SLAB_GP
					--   ,TAPER_SLAB_GP                            AS TAPER_SLAB_GP
					,ITEMNAME_CD                                   AS ITEMNAME_CD
					,YEOJAE_CAUSE_CD                               AS YEOJAE_CAUSE_CD
					,YEOJAE_OCCUR_DATE                             AS YEOJAE_OCCUR_DATE
					,YEOJAE_OCCUR_TIME                             AS YEOJAE_OCCUR_TIME
					,MATCH_ORDERTRANS_GP                           AS MATCH_ORDERTRANS_GP
					,FNL_MATCH_ORDERTRANS_OCCURDATE                AS FNL_MATCH_ORDERTRANS_OCCURDATE
					,FNL_MATCH_ORDERTRANS_OCCURTIME                AS FNL_MATCH_ORDERTRANS_OCCURTIME
					,MATCH_DATE                                    AS MATCH_DATE
					,YD_GP                                         AS YD_GP
					,YD_BAY_GP                                     AS YD_BAY_GP
					,YD_EQP_GP                                     AS YD_EQP_GP
					,YD_STK_COL_NO                                 AS YD_STK_COL_NO
					,YD_STK_BED_NO                                 AS YD_STK_BED_NO
					,YD_STK_LYR_NO                                 AS YD_STK_LYR_NO
					,YD_STR_LOC                                    AS YD_STR_LOC
					,YD_STR_LOC_HIS1                               AS YD_STR_LOC_HIS1
					,YD_STR_LOC_HIS2                               AS YD_STR_LOC_HIS2
					,RECEIPT_DATE                                  AS RECEIPT_DATE
					,RECEIPT_TIME                                  AS RECEIPT_TIME
					,MISSNO_YN                                     AS MISSNO_YN
					,MISSNO_CAUSE_CD                               AS MISSNO_CAUSE_CD
					,SCRAP_CAUSE_CD                                AS SCRAP_CAUSE_CD
					,SCRAP_OCCUR_DATE                              AS SCRAP_OCCUR_DATE
					,SCRAP_OCCUR_TIME                              AS SCRAP_OCCUR_TIME
					,MISSNO_OCCUR_DATE                             AS MISSNO_OCCUR_DATE
					,MISSNO_OCCUR_TIME                             AS MISSNO_OCCUR_TIME
					,A1_TAB_PASS_DATE                              AS A1_TAB_PASS_DATE
					,A1_TAB_PASS_TIME                              AS A1_TAB_PASS_TIME
					,A1_SLAB_LEN                                   AS A1_SLAB_LEN
					,A1_SLAB_WT                                    AS A1_SLAB_WT
					,A1_MODE                                       AS A1_MODE
					,HEAT_NO                                       AS HEAT_NO
					,COIL_NO                                       AS COIL_NO
					,OVER_YEOJAE_GP                                AS OVER_YEOJAE_GP
					,REHEAT_SLAB_GP                                AS REHEAT_SLAB_GP
					,REHEAT_SLAB_OCCUR_DATE                        AS REHEAT_SLAB_OCCUR_DATE
					,REHEAT_SLAB_OCCUR_TIME                        AS REHEAT_SLAB_OCCUR_TIME
					,WO_MSLAB_RPR_MTD                              AS WO_MSLAB_RPR_MTD
					,SCARFING_DEPTH                                AS SCARFING_DEPTH
					,PROD_DUE_DATE                                 AS PROD_DUE_DATE
					,INSPECT_DATE                                  AS INSPECT_DATE
					,REAL_MEASURE_SLAB_T                           AS REAL_MEASURE_SLAB_T
					,REAL_MEASURE_SLAB_W                           AS REAL_MEASURE_SLAB_W
					,REAL_MEASURE_SLAB_LEN                         AS REAL_MEASURE_SLAB_LEN
					,CAL_SLAB_WT                                   AS CAL_SLAB_WT
					,WDH_STAMP_GRADE                               AS WDH_STAMP_GRADE
					,WDH_STAMP_CAUSE_CD                            AS WDH_STAMP_CAUSE_CD
					,SURFACE_STAMP_GRADE                           AS SURFACE_STAMP_GRADE
					,SURFACE_STAMP_CAUSE_CD                        AS SURFACE_STAMP_CAUSE_CD
					,FORM_STAMP_GRADE                              AS FORM_STAMP_GRADE
					,FORM_STAMP_CAUSE_CD                           AS FORM_STAMP_CAUSE_CD
					,INSPECT_GRADE                                 AS INSPECT_GRADE
					,INGR_STAMP_DATE                               AS INGR_STAMP_DATE
					,INGR_STAMP_GRADE                              AS INGR_STAMP_GRADE
					,REGISTER                                      AS REGISTER
					,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')         AS REG_DDTT
					,MODIFIER                                      AS MODIFIER
					,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')         AS MOD_DDTT
					,PARENT_SLAB_NO                                AS PARENT_SLAB_NO
					,CAMBER_YN                                     AS CAMBER_YN
					,LONG_BOW_YN                                   AS LONG_BOW_YN
					,LONG_BOW_VAL                                  AS LONG_BOW_VAL
					,REAGENT_PICK_TARGET_YN                        AS REAGENT_PICK_TARGET_YN
					,REAGENTPICK_TARGET_ASSIGN_DATE                AS REAGENTPICK_TARGET_ASSIGN_DATE
					,REAGENTPICK_TARGET_ASSIGN_HR                  AS REAGENTPICK_TARGET_ASSIGN_HR
					,REAGENTPICK_DONE_YN                           AS REAGENTPICK_DONE_YN
					,REAGENTPICK_DONE_DATE                         AS REAGENTPICK_DONE_DATE
					,REAGENTPICK_DONE_HR                           AS REAGENTPICK_DONE_HR
					,DESIGN_SPEC_ABBSYM                            AS DESIGN_SPEC_ABBSYM
					,SURFACE_INSPECT_DATE                          AS SURFACE_INSPECT_DATE
					,REAGENT_PICK_LEN                              AS REAGENT_PICK_LEN
					,SCARFING_COUNT                                AS SCARFING_COUNT
					,MSLAB_RPR_MTD1                                AS MSLAB_RPR_MTD1
					,MSLAB_RPR_MTD2                                AS MSLAB_RPR_MTD2
					,MSLAB_RPR_MTD3                                AS MSLAB_RPR_MTD3
					,MSLAB_RPR_MTD4                                AS MSLAB_RPR_MTD4
					,SCARFING_INSPECT_YN                           AS SCARFING_INSPECT_YN
					,DEFECT_REMOVE_YN                              AS DEFECT_REMOVE_YN
					,MILL_ORD_DATE                                 AS MILL_ORD_DATE
					,HGUARD_TREAT_GP                               AS HGUARD_TREAT_GP
					,SCARFING_INSPECT_SLAB_W_MIN                   AS SCARFING_INSPECT_SLAB_W_MIN
					,SCARFING_INSPECT_SLAB_W_MAX                   AS SCARFING_INSPECT_SLAB_W_MAX
					,SCARFING_INSPECT_SLAB_W_AVG                   AS SCARFING_INSPECT_SLAB_W_AVG
					,SURFACE_INSPECT_TIME                          AS SURFACE_INSPECT_TIME
					,SURFACE_INSPECT_INI_DATE                      AS SURFACE_INSPECT_INI_DATE
					,RECEIPT_INI_DATE                              AS RECEIPT_INI_DATE
					,HCR_GP                                        AS HCR_GP
					,CCM_NO                                        AS CCM_NO
					,ORD_HCR_GP                                    AS ORD_HCR_GP
					,SLAB_CHARACTER                                AS SLAB_CHARACTER
					,WDH_FORM_INSPECT_RSLT_MSG                     AS WDH_FORM_INSPECT_RSLT_MSG
					,INSPECTOR_MSG                                 AS INSPECTOR_MSG
					,SLAB_TRIM_YN                                  AS SLAB_TRIM_YN
					,TO_CHAR(SLAB_TRIM_DDTT, 'YYYYMMDDHH24MISS')   AS SLAB_TRIM_DDTT
					,INGRHOLD_YN                                   AS INGRHOLD_YN
					,INGRHOLD_RELEASEDATE                          AS INGRHOLD_RELEASEDATE
					,INSPECT_CONFIRM_GP                            AS INSPECT_CONFIRM_GP
					,TO_CHAR(MILL_WORD_DATE, 'YYYYMMDDHH24MISS')   AS MILL_WORD_DATE
					,HANDSCARFING_YN                               AS HANDSCARFING_YN
					,WO_ITM                                        AS WO_ITM
					,PRD_ITM_CD                                    AS PRD_ITM_CD
					,TO_CHAR(TRS_INDI_DT, 'YYYYMMDDHH24MISS')      AS TRS_INDI_DT
					,TO_CHAR(MATL_TKOV_DT, 'YYYYMMDDHH24MISS')     AS MATL_TKOV_DT
					,TO_CHAR(SCARF_WRK_DT, 'YYYYMMDDHH24MISS')     AS SCARF_WRK_DT
					,TO_CHAR(SLAB_SHEAR_DT, 'YYYYMMDDHH24MISS')    AS SLAB_SHEAR_DT
					,TO_CHAR(MSLAB_FS_CMPL_DT, 'YYYYMMDDHH24MISS') AS MSLAB_FS_CMPL_DT
					,STL_APPEAR_GP                                 AS STL_APPEAR_GP
					,PL_ORD_CNT                                    AS PL_ORD_CNT
					,BEFO_ORD_YEOJAE_GP                            AS BEFO_ORD_YEOJAE_GP
					,OVERALL_STAMP_GRADE                           AS OVERALL_STAMP_GRADE
					,OVERALL_STAMP_DATE                            AS OVERALL_STAMP_DATE
					,BEF_ORD_NO                                    AS BEF_ORD_NO
					,BEF_ORD_DTL                                   AS BEF_ORD_DTL
					,PTRMN_EA                                      AS PTRMN_EA
					,RE_HT_MAT_OCR_CAU_CD                          AS RE_HT_MAT_OCR_CAU_CD
					,FRTOMOVE_ORD_DATE                             AS FRTOMOVE_ORD_DATE
					,SLAB_WO_RT_CD                                 AS SLAB_WO_RT_CD
					,DEMANDER_CD                                   AS DEMANDER_CD
					,MILL_WO_EXN                                   AS MILL_WO_EXN
					,TO_CHAR(MILL_WRK_DT, 'YYYYMMDDHH24MISS')      AS MILL_WRK_DT
					,STLQLTY_SYM                                   AS STLQLTY_SYM
					,OVROLL_YN                                     AS OVROLL_YN
					,TO_CHAR(OVROLL_CHECK_DT, 'YYYYMMDDHH24MISS')  AS OVROLL_CHECK_DT
					,OVROLL_PASS_YN                                AS OVROLL_PASS_YN
					,TO_CHAR(OVROLL_RELEASE_DATE, 'YYYYMMDDHH24MISS') AS OVROLL_RELEASE_DATE
					,TO_CHAR(MILL_PLAN_DDTT, 'YYYYMMDDHH24MISS')   AS MILL_PLAN_DDTT
					,TO_CHAR(SHEAR_PLNDD, 'YYYYMMDDHH24MISS')      AS SHEAR_PLNDD
					,TO_CHAR(DIST_PLAN_DDTT, 'YYYYMMDDHH24MISS')   AS DIST_PLAN_DDTT
					,IG_STMP_GP                                    AS IG_STMP_GP
					,PRPL_MILL_MTD_WO_CD                           AS PRPL_MILL_MTD_WO_CD
					,STACK_LOT_NO                                  AS STACK_LOT_NO
					,SZ_GP                                         AS SZ_GP
					,SZ_MSLAB_GP                                   AS SZ_MSLAB_GP
					,SZ_CSLAB_SH                                   AS SZ_CSLAB_SH
					,PTOP_PLNT_GP                                  AS PTOP_PLNT_GP
					,SL_PRSNT_SHEAR_CMPL_GP                        AS SL_PRSNT_SHEAR_CMPL_GP
					,TO_CHAR(SLAB_SZ_MOD_DT, 'YYYYMMDDHH24MISS')   AS SLAB_SZ_MOD_DT
					,SLAB_SZ_MOD_RSN_CD                            AS SLAB_SZ_MOD_RSN_CD
					,SLAB_SZ_MODR_EMPNO                            AS SLAB_SZ_MODR_EMPNO
					,SLAB_REBIRTH_RSN_CD                           AS SLAB_REBIRTH_RSN_CD
					,SLAB_REBIRTH_GP                               AS SLAB_REBIRTH_GP
					,SLAB_SCRP_T                                   AS SLAB_SCRP_T
					,SLAB_SCRP_W                                   AS SLAB_SCRP_W
					,SLAB_SCRP_L                                   AS SLAB_SCRP_L
					,SLAB_SCRP_WT                                  AS SLAB_SCRP_WT
					,TAPERED_WD_W                                  AS TAPERED_WD_W
					,TAPERED_NARR_W                                AS TAPERED_NARR_W
					,MATL_TRTWT                                    AS MATL_TRTWT
					,MS_SLAB_T                                     AS MS_SLAB_T
					,MS_SLAB_W                                     AS MS_SLAB_W
					,MS_SLAB_LEN                                   AS MS_SLAB_LEN
					,MS_SLAB_WT                                    AS MS_SLAB_WT
					,MS_SLAB_ALLOC_WT                              AS MS_SLAB_ALLOC_WT
					,MS_SPEC_ABBSYM                                AS MS_SPEC_ABBSYM
					,SHEAR_INI_DATE                                AS SHEAR_INI_DATE
					,SHEAR_WRK_DD                                  AS SHEAR_WRK_DD
					,SLAB_SHEAR_WD                                 AS SLAB_SHEAR_WD
					,PRKD_CD                                       AS PRKD_CD
					,MSLAB_ASGN_GP                                 AS MSLAB_ASGN_GP
					,MATCH_MTD_GP                                  AS MATCH_MTD_GP
					,TO_CHAR(MATL_FTMV_DT, 'YYYYMMDDHH24MISS')     AS MATL_FTMV_DT
					,SLAB_SHEAR_DF_CD1                             AS SLAB_SHEAR_DF_CD1
					,SLAB_SHEAR_DF_CD2                             AS SLAB_SHEAR_DF_CD2
					,SLAB_SHEAR_DF_CD3                             AS SLAB_SHEAR_DF_CD3
					,SLAB_SHEAR_DF_CD4                             AS SLAB_SHEAR_DF_CD4
					,SLAB_SHEAR_DF_CD5                             AS SLAB_SHEAR_DF_CD5
					,SLAB_SHEAR_DF_GRD1                            AS SLAB_SHEAR_DF_GRD1
					,SLAB_SHEAR_DF_GRD2                            AS SLAB_SHEAR_DF_GRD2
					,SLAB_SHEAR_DF_GRD3                            AS SLAB_SHEAR_DF_GRD3
					,SLAB_SHEAR_DF_GRD4                            AS SLAB_SHEAR_DF_GRD4
					,SLAB_SHEAR_DF_GRD5                            AS SLAB_SHEAR_DF_GRD5
					,TO_CHAR(DIST_END_DDTT, 'YYYYMMDDHH24MISS')    AS DIST_END_DDTT
					,DIST_DATE                                     AS DIST_DATE
				    ,SLAB_WO_RT_CD                                 AS SLAB_WO_RT_CD
				FROM TB_PT_SLABCOMM
				WHERE SLAB_NO 								= :V_SLAB_NO
				
					//intRtnVal = ydStockDao.getYdStock(recInParam, rsOut, 2);
				 */
				jrRst = this.select(recInParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdCommDAO.getSLABCOMM", logId, methodNm, "슬라브 공통T");
				if( jrRst.size() > 0 ) {
					intRtnVal = jrRst.size();
					szLogMsg = "슬라브공통테이블에 재료["+szStlNo+"]가 존재합니다.";
		            slabUtils.printLog(logId, szLogMsg, "SL"); 
		            
		            jrRst.first();
					recTemp = jrRst.getRecord();
		            
					recTemp.setField("PT_TB_COMM", "S");
					szPtTbComm = "S";
				}
			}else if( intRtnVal > 0 ) {
				szLogMsg = "주편공통테이블에 재료["+szStlNo+"]가 존재합니다.";
	            slabUtils.printLog(logId, szLogMsg, "SL"); 
				
	            jrRst.first();
				recTemp = jrRst.getRecord();
	            
	            recTemp.setField("PT_TB_COMM", "B");
				szPtTbComm = "B";
				
				//주편공통테이블에서 재료를 조회해서 Record진행상태가 3인 경우에는 (슬라브공통)테이블을 조회하여 대상재를 찾는다.
				if( "3".equals(slabUtils.paraRecChkNull(recTemp, "RECORD_PROG_STAT")) ) {
					szLogMsg = "주편공통테이블에 재료["+szStlNo+"]가 존재하지만 레코드가 종료된 상태이므로 슬라브공통테이블을 조회한다.";
		            slabUtils.printLog(logId, szLogMsg, "SL"); 
					//슬라브공통테이블 조회
					recInParam.setField("SLAB_NO", szStlNo);
					
					jrRst = this.select(recInParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdCommDAO.getSLABCOMM", logId, methodNm, "슬라브 공통T"); 
					
					if( jrRst.size() > 0 ) {
						szLogMsg = "슬라브공통테이블에 재료["+szStlNo+"]가 존재합니다.";
			            slabUtils.printLog(logId, szLogMsg, "SL"); 
						
			            jrRst.first();
						recTemp = jrRst.getRecord();
						
			            recTemp.setField("PT_TB_COMM", "S");
						szPtTbComm = "S";
					}
				}
			}
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	            	szLogMsg = "주편공통테이블이나 슬라브공통테이블에 재료[" + szStlNo + "]가 존재하지 않습니다. 에러코드 : " + intRtnVal;
		            slabUtils.printLog(logId, szLogMsg, "SL"); 
	            } else if (intRtnVal == -2) {
	            	szLogMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		            slabUtils.printLog(logId, szLogMsg, "SL"); 
	            }
	            szRtnMsg = PSlabYdConstant.RETN_CD_FAILURE;
	        }else{
	        	
	        	rsResult.addRecord(recTemp);
	        	szRtnMsg = szPtTbComm;
	        }
		}catch(JDTOException ex) {
			szLogMsg = " 에러 발생 : " + ex.getMessage();
            slabUtils.printLog(logId, szLogMsg, "SL"); 
            throw ex;
		}
		return szRtnMsg;
	}
	
	/**
	 * 동분산구분 , 목표야드  추출  , 목표동 추출     : GetBreRule5.getYDB001~3
	 * 박영수  2020-09-10
	 * @param szSTL_NO
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 * 
	 */
	public int CallMapping(JDTORecord inRec, JDTORecord outRec, String strPT_TB_COMM , String logId, String methodNm2 , String userId) throws JDTOException {
		int nCntCheck             = 0;
		int nRet                  = 0;
		boolean bRet              = false;
	    String methodNm           = "동분산구분 , 목표야드  추출  , 목표동 추출(CallMapping)" + methodNm2; 
	    String sModifier 		  = userId;
	    String szMsg              = "";
	    JDTORecord recPara        = slabUtils.getParam(logId, methodNm, sModifier);
	    JDTORecord tmpRec         = slabUtils.getParam(logId, methodNm, sModifier);
		//===================================================================
		// 수신 파라미터에서 항목 추출
		//===================================================================
	    String szSlabWoRtCd       = slabUtils.paraRecChkNull(inRec, "SLAB_WO_RT_CD");
	    String szStlAppearGp      = slabUtils.paraRecChkNull(inRec, "STL_APPEAR_GP");
	    String szOrdYeojaeGp      = slabUtils.paraRecChkNull(inRec, "ORD_YEOJAE_GP");
	    String szScarfingYn       = slabUtils.paraRecChkNull(inRec, "SCARFING_YN");
	    String szScarfingDoneYn   = slabUtils.paraRecChkNull(inRec, "SCARFING_DONE_YN");
	    String szMillWoExn        = slabUtils.paraRecChkNull(inRec, "MILL_WO_EXN");
	    String szCurrProgCd       = slabUtils.paraRecChkNull(inRec, "CURR_PROG_CD");
	    String szRcvTcCode        = slabUtils.getTcCode(inRec);
		slabUtils.printLog(logId, " szRcvTcCode:" + szRcvTcCode, "SL");
		
		String szBayDistribution  = ""; 
		String szAimYd            = "";
		String szAimBay           = "";
		String szAimRt            = "";
		String szStlProgCd        = "";
		
		//===================================================================
		// 동분산 구분
		// String szBayDistribution = getBayDistribution(inRec);
		//===================================================================
		bRet   = GetBreRule5.getYDB001(inRec, tmpRec);			//코드매핑 동분산 구분
		
    	if(bRet){
        	szBayDistribution = slabUtils.paraRecChkNull(tmpRec, "YD_BAY_GP");

    		szMsg = "*** 동분산구분 추출 성공 *** : " + szBayDistribution;
    		slabUtils.printLog(logId, " " + szMsg, "SL");
    	} else {
    		szMsg = "*** 동분산구분 추출 실패 *** : " + szBayDistribution;
    		slabUtils.printLog(logId, " " + szMsg, "SL");
    	}
    	
		//===================================================================
    	// 목표야드
    	// String szAimYd  = getAimYd(inRec);
		//===================================================================
		bRet = GetBreRule5.getYDB002(inRec, tmpRec);
		
    	if(bRet){
    		szAimYd = slabUtils.paraRecChkNull(tmpRec, "YD_AIM_YD_GP");

    		szMsg = "*** 목표야드 추출 성공 *** : " + szAimYd;
    		slabUtils.printLog(logId, " " + szMsg, "SL");
    	} else {
    		szMsg = "*** 목표야드 추출 실패 *** : " + szAimYd;
    		slabUtils.printLog(logId, " " + szMsg, "SL");
    	}
        
		//===================================================================
    	// 목표동
		// String szAimBay = getAimBay(inRec, szBayDistribution, szAimYd);
		//===================================================================
    	
    	recPara.setField("YD_AIM_YD_GP" , szAimYd);
    	recPara.setField("SLAB_WO_RT_CD", szSlabWoRtCd);
    	recPara.setField("STL_APPEAR_GP", szStlAppearGp);
    	recPara.setField("YD_BAY_GP"    , szBayDistribution);
		
    	bRet = GetBreRule5.getYDB003(recPara, tmpRec);
    	
    	if(bRet){
    		szAimBay = slabUtils.paraRecChkNull(tmpRec, "YD_AIM_BAY_GP");

    		szMsg = "*** 목표동 추출 성공 *** : " + szAimBay;
    		slabUtils.printLog(logId, " " + szMsg, "SL");
    	} else {
    		szMsg = "*** 목표동 추출 실패 *** : " + szAimBay;
    		slabUtils.printLog(logId, " " + szMsg, "SL");
    	}    	
    	
		//===================================================================
    	// 목표행선구분
		// String szAimRt  = getAimRtBase(inRec, szBayDistribution, strPT_TB_COMM);
		//===================================================================
		tmpRec = JDTORecordFactory.getInstance().create();
    	recPara = JDTORecordFactory.getInstance().create();
    	recPara.setField("SLAB_WO_RT_CD"   , szSlabWoRtCd);
    	recPara.setField("STL_APPEAR_GP"   , szStlAppearGp);
    	recPara.setField("YD_BAY_GP"       , szBayDistribution);

    	slabUtils.printLog(logId, "szRcvTcCode: " + szRcvTcCode + " strPT_TB_COMM:" + strPT_TB_COMM, "SL");
    	
    	if("Y3YDL009".equals(szRcvTcCode)){
//			if(strPT_TB_COMM.equals("3")){			[이송대상재가 하차완료시 해당조건에 따른 진도코드를 반환하는 메소드 - 주편, 슬라브에 따른 대기코드 반환 ]
//				szStlProgCd = this.getCurrProgCdAA("S", szSlabWoRtCd, szOrdYeojaeGp, szScarfingYn, szScarfingDoneYn, szMillWoExn, logId);								
//			} else {
//				szStlProgCd = this.getCurrProgCdAA("B", szSlabWoRtCd, szOrdYeojaeGp, szScarfingYn, szScarfingDoneYn, szMillWoExn, logId);								
//			}
			
			//---------------------------------------------------------------------------------------------------------
			//---------------------------------------------------------------------------------------------------------
        	//공정 함수를 이용한 진도코드 가져오기
		    JDTORecordSet jsCrnSch;
        	if("3".equals(strPT_TB_COMM)){
        		/* 슬라브 공통
        		 * (원본) ym.facilitywork.putwrecord.session.getListcurrprogcdSlab
        		 *      
					--회송차량의 재료인 경우 진도코드 UPDATE 하지 않는다.
					WITH TEMP_TABLE AS (
					   SELECT :v_SLAB_NO AS V_SLAB_NO FROM DUAL
					)
					SELECT CASE WHEN (SELECT COUNT(*)
					  FROM TB_YD_RETHTHIST
					      ,TEMP_TABLE A
					 WHERE 1=1
					   AND YD_CAR_SCH_ID IN (
					                    SELECT CS.YD_CAR_SCH_ID
					                      FROM TB_YD_CARSCH CS
					                          ,TB_YD_CARFTMVMTL CM
					                          ,TEMP_TABLE A
					                     WHERE 1=1
					                       AND CM.STL_NO = A.V_SLAB_NO
					                       AND CS.YD_CAR_SCH_ID = CM.YD_CAR_SCH_ID
					                       AND CS.DEL_YN = 'N'
					       )
					   AND STL_NO = A.V_SLAB_NO) >0 THEN BEFO_PROG_CD
					   ELSE USRPMA.IHSF_PM_주편SLAB진도찾기(STL_APPEAR_GP,SLAB_NO) END AS CURR_PROG_CD
					FROM USRPTA.TB_PT_SLABCOMM
					    ,TEMP_TABLE A
					WHERE SLAB_NO= A.V_SLAB_NO
        		 */
    		    
    	        jsCrnSch = this.select(outRec, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.getListcurrprogcdSlab", logId, methodNm, "슬라브 공통 추출");
    		    
        	}else  {
        		/* 
        		 * (원본) ym.facilitywork.putwrecord.session.getListcurrprogcd
					select  USRPMA.IHSF_PM_주편SLAB진도찾기(STL_APPEAR_GP,SLAB_NO)  AS CURR_PROG_CD
					FROM VW_YD_SLABCOMM
					WHERE SLAB_NO= :V_SLAB_NO
        		*/
    		    
        		jsCrnSch = this.select(outRec, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getListcurrprogcd", logId, methodNm, "슬라브 공통 추출");
        	}       	

        	if(jsCrnSch.size() <= 0 ) {
        		szStlProgCd = "";				//getCurrProgCdAA 
        	} else {
        		//(원본)szStlProgCd = StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
        		szStlProgCd = slabUtils.nvl(jsCrnSch.getFieldString("CURR_PROG_CD"), "");
        	}
        	if("".equals(szStlProgCd)) {
        		slabUtils.printLog(logId, "\nIHSF_PM_주편SLAB진도찾기 (오류)\n", "SL");
        		
        	} else {
        		slabUtils.printLog(logId, "IHSF_PM_주편SLAB진도찾기==>>:"+szStlProgCd, "SL");
        	}
			
		} else {
			szStlProgCd = szCurrProgCd;		
		}
    	
    	recPara.setField("STL_PROG_CD", szStlProgCd);
    	
    	bRet = GetBreRule5.getYDB004(recPara, tmpRec);
    	if(bRet){
    		szAimRt = slabUtils.paraRecChkNull(tmpRec, "YD_AIM_RT_GP");

    		szMsg = "*** 목표행선구분 추출 성공 *** : " + szAimRt;
    		slabUtils.printLog(logId, " " + szMsg, "SL");
    		
    	} else 
    	{
    		szMsg = "*** 목표행선구분 추출 실패 *** : " + szAimRt;
    		slabUtils.printLog(logId, " " + szMsg, "SL");
    	}    	
		
		if(!"".equals(szAimYd)){
			outRec.setField("YD_AIM_YD_GP", szAimYd);
			nCntCheck++;
			nRet = 1;
		}
		
		if(!"".equals(szAimRt)){
			outRec.setField("YD_AIM_RT_GP", szAimRt);
			nCntCheck++;
			nRet = 1;
		}
		
		if(!"".equals(szAimBay)){
			outRec.setField("YD_AIM_BAY_GP", szAimBay);
			nCntCheck++;
			nRet = 1;
		}	
		return nRet;
	}

	/**
	 *      [A] 오퍼레이션명 : 주편, 슬라브에 따른 대기코드 반환  
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param String stlAppearGp
	 *      @param String szSlabWoRtCd
	 *      @return String
	*/
//	public  String getCurrProgCd(String szPtTbComm, 
//								 String szSlabWoRtCd,
//								 String szOrdYeojaeGp,
//								 String szScarfingYn,
//								 String szScarfingDoneYn,
//								 String szMillWoExn,
//								 String sYdGp,
//								 String szStlAppearGp
//								,String logId) {
//	/*
//	* 업무기준 : 1. 주편이면 진도코드를 Slab정정작업대기[A]로 반환
//	* 			 2. 슬라브이면
//	* 				2-1. 슬라브지시행선이 판매Slab이면
//	* 					2-1-1. 주문재이면 진도코드를 출하지시대기[K]로 반환
//	* 					2-1-2. 여재이면 진도코드를 제품충당대기[Z]로 반환
//	* 				2-2. 슬라브지시행선이 판매Slab가 아니면
//	* 					2-2-1. 주문재이면
//	* 						2-2-1-1. 압연지시여부가 Y이면 진도코드를 작업대기[C]로 반환
//	* 						2-2-1-2. 압연지시여부가 Y가 아니면
//	* 							2-2-1-2-1. 스카핑여부가 Y이고 스카핑완료여부가 N이면 진도코드를 Slab정정작업대기[A]로 반환
//	* 							2-2-1-2-2. 그외는 진도코드를 지시대기[B]로 반환
//	* 					2-2-2. 여재이면
//	* 						2-2-2-1. 스카핑여부가 Y이고 스카핑완료여부가 N이면 진도코드를 Slab정정작업대기[A]로 반환
//	* 						2-2-2-2. 그외는 진도코드를 재공충당대기[Y]로 반환
//	*/
//		String szOperationName 		= "재료진도결정";
//		String szMsg				= null;
//		String szCurrProgCd = "";
//		
//		szMsg = "["+szOperationName+"] 메소드 시작 - 주편/슬라브구분["+szPtTbComm
//					+"], 슬라브지시행선코드["	+szSlabWoRtCd
//					+"], 주여구분["			+szOrdYeojaeGp
//				    +"], 스카핑여부["		+szScarfingYn
//				    +"], 스카핑완료여부["		+szScarfingDoneYn
//				    +"], 압연지시구분["		+szMillWoExn+"]";
//		slabUtils.printLog(logId, szMsg, "SL");
//		
//		if( ("B").equals(szPtTbComm) ) {										//+ 주편이면 
//			szCurrProgCd = "A";													//Slab정정작업대기
//		}else{																	// 슬라브이면 
//			if( ("MS").equals(szSlabWoRtCd) ) {
//				if("Y".equals(szStlAppearGp))  									//+ 제품이면
//				{
//					if( "1".equals(szOrdYeojaeGp) ) {							// 주문재이면 
//						szCurrProgCd = "K";										//출하지시대기
//					}else{														/* 여재이면 */
//						szCurrProgCd = "Z";										//제품충당대기
//					}   				
//				}else { 
//					if( "1".equals(szOrdYeojaeGp) ) {							//+ 주문재이면 
//						if("Y".equals(szScarfingYn) && "N".equals(szScarfingDoneYn)){
//							szCurrProgCd = "A";	
//						}else{
//							szCurrProgCd = "A";									//출하지시대기
//						}
//					}else{														//+ 여재이면 
//						if("Y".equals(szScarfingYn) && "N".equals(szScarfingDoneYn)){
//							szCurrProgCd = "A";	
//						}else{
//							szCurrProgCd = "Y";									//제품충당대기
//						}		        																	
//					}     				
//				}    			
//			}
//			if( ("PA").equals(szSlabWoRtCd) ) {
//				if(("A").equals(sYdGp) || ("D").equals(sYdGp))  				//+C열연,A후판 야드
//				{
//					if( szOrdYeojaeGp.equals("1") ) {							//+주문재이면 
//						if(("Y").equals(szScarfingYn) && ("N").equals(szScarfingDoneYn)){
//							szCurrProgCd = "A";	
//						}else{
//							szCurrProgCd = "B";									//출하지시대기
//						}
//					}else{														//+여재이면 
//						if(("Y").equals(szScarfingYn) && ("N").equals(szScarfingDoneYn)){
//							szCurrProgCd = "A";	
//						}else{
//							szCurrProgCd = "Y";									//제품충당대기
//						}		        																	
//					}   				
//				} else {
//					if( ("1").equals(szOrdYeojaeGp) ) {							//+주문재이면 
//						if(("Y").equals(szScarfingYn) && ("N").equals(szScarfingDoneYn)){
//							szCurrProgCd = "A";	
//						}else{
//							szCurrProgCd = "A";									//출하지시대기
//						}
//					}else{														//+여재이면 
//						if(("Y").equals(szScarfingYn) && ("N").equals(szScarfingDoneYn)){
//							szCurrProgCd = "A";	
//						}else{
//							szCurrProgCd = "Y";									//제품충당대기
//						}		        																	
//					}     				
//				}    			
//			} 
//			if(("").equals(szSlabWoRtCd )) {
//				slabUtils.printLog(logId, "szSlabWoRtCd:"+ szSlabWoRtCd, "SL");
//			}
//		
//	  
//		}
//		szMsg = "["+szOperationName+"] 메소드 끝";
//		slabUtils.printLog(logId, szMsg, "SL");
//		return szCurrProgCd;
//	}
	

	/**
	 * 이송대상재가 하차완료시 해당조건에 따른 진도코드를 반환하는 메소드
	 * @param szPT_TB_COMM
	 * @param szSLAB_WO_RT_CD
	 * @param szORD_YEOJAE_GP
	 * @param szSCARFING_YN
	 * @param szSCARFING_DONE_YN
	 * @param szMILL_WO_EXN
	 * @return
	 */
//	public  String getCurrProgCdAA(String szPT_TB_COMM, 
//										String szSLAB_WO_RT_CD,
//										String szORD_YEOJAE_GP,
//										String szSCARFING_YN,
//										String szSCARFING_DONE_YN,
//										String szMILL_WO_EXN
//										,String logId) {
//		
//		return getCurrProgCd(	 szPT_TB_COMM, 
//								 szSLAB_WO_RT_CD,
//								 szORD_YEOJAE_GP,
//								 szSCARFING_YN,
//								 szSCARFING_DONE_YN,
//								 szMILL_WO_EXN,
//								 "","", logId);
//	}

	
	/**
	 * 재료번호로 공통(주편 or 슬라브)테이블의 필수 공통 항목을 편집 및 코드매핑처리를 하여 저장품에 업데이트 한후 Level2에도 전송
     * 야드관리 > 후판슬라브야드 [신] > 설비관리 > 저장위치수정     	
     * 박영수  2020.09.18  
     * 변경전: 0:건수없음    -1:조회쿼리 파라미터에러 or 업데이트쿼리 실패     -2: 예외발생     -3: L2전송에러
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param gdReq
     * @return
     * @throws DAOException
     */
	
	public JDTORecord  getMappingCommonField(String strTcCode  , String strStlNo	, String strStlGp , boolean isSend , String logId , String usrId) throws DAOException {
		
		// 레코드 선언
		JDTORecord recPara        = null;
		JDTORecord recGetVal      = null; 
		JDTORecord outRecTemp     = null; 
		JDTORecord recEditRec     = null; 
		JDTORecord recResult      = null;
		JDTORecord jrRtn          = null;
		
		// 변수 선언
		String methodNm			  = "주편/슬라브/저장품 필수 공통항목 저장품에 업데이트 처리(getMappingCommonField)";	
		String szMsg              = "";
		String szRECORD_PROG_STAT = "";
		String szTcCode           = strTcCode;
		String szStlNo            = strStlNo;
		String szYdGp             = "";
		//String logId			  = "" ;	

		int nRet                  = 0;
		
		try {
			
			String sModifier = usrId;
		    //================================================================================================= 
            // 수정내용 : 재료형태를 파라미터로 입력받아서 처리
            //=================================================================================================
            //rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			jrRtn    = slabUtils.getParam(logId, methodNm, sModifier);
			recPara  = JDTORecordFactory.getInstance().create();			
			
            if ("MSLAB".equals(strStlGp)) {
            	recPara.setField("MSLAB_NO", szStlNo);
            	/* 주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 주편공통)   - V_MSLAB_NO 
            	 * (원본)"com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockMslabCommBySTLNo"
            	 * SELECT  B.MSLAB_NO AS STL_NO
					       ,B.YD_GP AS YD_GP
					       ,B.RECORD_PROG_STAT AS RECORD_PROG_STAT
					       ,B.STL_APPEAR_GP AS STL_APPEAR_GP
					       ,B.CURR_PROG_CD AS STL_PROG_CD
					       ,B.ORD_YEOJAE_GP AS ORD_YEOJAE_GP
					       ,B.ORD_NO AS ORD_NO
					       ,B.SLAB_WO_RT_CD AS SLAB_WO_RT_CD
					       ,B.PTOP_PLNT_GP AS PTOP_PLNT_GP
					       ,B.SCARFING_YN AS SCARFING_YN
					       ,B.SCARFING_DONE_YN AS SCARFING_DONE_YN
					       ,B.REAL_MEASURE_SLAB_T AS YD_MTL_T
					       ,B.REAL_MEASURE_SLAB_W AS YD_MTL_W
					       ,B.REAL_MEASURE_SLAB_LEN AS YD_MTL_L
					       ,B.CAL_SLAB_WT AS YD_MTL_WT
					       ,B.PLNT_PROC_CD AS PLNT_PROC_CD 
					       ,B.ORD_DTL AS ORD_DTL
					       ,B.ITEMNAME_CD AS ITEMNAME_CD
					       ,B.SPEC_ABBSYM AS SPEC_ABBSYM
					       ,B.CC_CCM_NO AS CC_MC_CD
					       ,B.CC_CCM_NO AS CC_CCM_NO
					       ,B.WR_HCR_GP AS HCR_GP
					       ,B.ORD_HCR_GP AS ORD_HCR_GP
					       ,B.DEMANDER_CD AS DEMANDER_CD
					       ,B.WO_MSLAB_RPR_MTD AS WO_MSLAB_RPR_MTD
					       ,B.YD_STK_BED_NO AS YD_STK_BED_NO
					       ,B.STACK_LOT_NO AS STACK_LOT_NO
					  FROM  TB_YD_STOCK A
					       ,TB_PT_MSLABCOMM B
					 WHERE  A.STL_NO = B.MSLAB_NO
					   AND (A.DEL_YN IS NULL OR A.DEL_YN <> 'Y')
					   AND  MSLAB_NO = :V_MSLAB_NO
					   
            		//nRet = ydStockDao.getYdStock(recPara, rsResult, 184);
            	 */
            	
            	JDTORecordSet rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCommonFieldYdStockMslabCommBySTLNo", logId, methodNm, "주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 주편공통)");
            	
    			if(rsResult.size() <= 0){
    				szMsg = "조회내용이 없습니다. (주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회)";
    				jrRtn.setField("RTN_CD"	, "0");
    				jrRtn.setField("RTN_MSG", szMsg);
    				return jrRtn;    				 
    			}
    			rsResult.first();
    			recGetVal = rsResult.getRecord();		
    			
            }else if("SLAB".equals(strStlGp)) {
            	
            	recPara.setField("SLAB_NO", szStlNo);
            	/* 주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 슬라브공통) - V_SLAB_NO
            	 * (원본) "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockSlabCommBySTLNo"
            	 * 
					SELECT                                                                                  
					    B.SLAB_NO AS STL_NO                                                                 
					    , B.YD_GP AS YD_GP                                                                  
					    , B.RECORD_PROG_STAT AS RECORD_PROG_STAT                                            
					    , B.STL_APPEAR_GP AS STL_APPEAR_GP                                                  
					    , B.CURR_PROG_CD AS STL_PROG_CD                                                     
					    , B.ORD_YEOJAE_GP AS ORD_YEOJAE_GP                                                  
					    , B.ORD_NO AS ORD_NO                                                                
					    , B.SLAB_WO_RT_CD AS SLAB_WO_RT_CD                                                  
					    , B.PTOP_PLNT_GP AS PTOP_PLNT_GP                                                    
					    , B.SCARFING_YN AS SCARFING_YN                                                      
					    , B.SCARFING_DONE_YN AS SCARFING_DONE_YN                                            
					    , B.REAL_MEASURE_SLAB_T AS YD_MTL_T                                                 
					    , B.REAL_MEASURE_SLAB_W AS YD_MTL_W                                                 
					    , B.REAL_MEASURE_SLAB_LEN AS YD_MTL_L                                               
					    , B.CAL_SLAB_WT AS YD_MTL_WT                                                        
					    , B.PLNT_PROC_CD AS PLNT_PROC_CD                                                    
					    , B.ORD_DTL AS ORD_DTL                                                              
					    , B.ITEMNAME_CD AS ITEMNAME_CD                                                      
					    , B.SPEC_ABBSYM AS SPEC_ABBSYM                                                      
					    , B.CCM_NO AS CC_MC_CD                                                              
					    , B.CCM_NO AS CC_CCM_NO                                                             
					    , B.HCR_GP AS HCR_GP                                                                
					    , B.ORD_HCR_GP AS ORD_HCR_GP                                                        
					    , B.DEMANDER_CD AS DEMANDER_CD                                                      
					    , B.WO_MSLAB_RPR_MTD AS WO_MSLAB_RPR_MTD                                            
					    , B.YD_STK_BED_NO AS YD_STK_BED_NO                                                  
					    , B.STACK_LOT_NO AS STACK_LOT_NO                                                    
					FROM TB_YD_STOCK A                                                                      
					    , TB_PT_SLABCOMM B                                                                  
					WHERE A.STL_NO = B.SLAB_NO                                                              
					    AND (A.DEL_YN IS NULL OR A.DEL_YN <> 'Y')                                           
					    AND SLAB_NO = :V_SLAB_NO
					    
					//nRet = ydStockDao.getYdStock(recPara, rsResult, 185);
            	 */
            	
            	JDTORecordSet rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCommonFieldYdStockSlabCommBySTLNo", logId, methodNm, "주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 슬라브공통) - V_SLAB_NO");

            	if(rsResult.size() <= 0){
    				szMsg = "슬라브공통 조회내용이 없습니다. (주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회)";
    				jrRtn.setField("RTN_CD"	, "0");
    				jrRtn.setField("RTN_MSG", szMsg);
    				return jrRtn;					 
				}
            	rsResult.first();
				recGetVal = rsResult.getRecord();		
				
            } else if("".equals(strStlGp)) {
            
				//=================================================================================================
				// 주편공통 조회 (intGp : 184)
				//    쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockMslabCommBySTLNo 
				// 파라미터 : V_MSLAB_NO
				//=================================================================================================
				//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara  = JDTORecordFactory.getInstance().create();								
				recPara.setField("MSLAB_NO", szStlNo);
            	/* 주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 주편공통)   - V_MSLAB_NO 
            	 * (원본)"com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockMslabCommBySTLNo"
            	 * SELECT  B.MSLAB_NO AS STL_NO
					       ,B.YD_GP AS YD_GP
					       ,B.RECORD_PROG_STAT AS RECORD_PROG_STAT
					       ,B.STL_APPEAR_GP AS STL_APPEAR_GP
					       ,B.CURR_PROG_CD AS STL_PROG_CD
					       ,B.ORD_YEOJAE_GP AS ORD_YEOJAE_GP
					       ,B.ORD_NO AS ORD_NO
					       ,B.SLAB_WO_RT_CD AS SLAB_WO_RT_CD
					       ,B.PTOP_PLNT_GP AS PTOP_PLNT_GP
					       ,B.SCARFING_YN AS SCARFING_YN
					       ,B.SCARFING_DONE_YN AS SCARFING_DONE_YN
					       ,B.REAL_MEASURE_SLAB_T AS YD_MTL_T
					       ,B.REAL_MEASURE_SLAB_W AS YD_MTL_W
					       ,B.REAL_MEASURE_SLAB_LEN AS YD_MTL_L
					       ,B.CAL_SLAB_WT AS YD_MTL_WT
					       ,B.PLNT_PROC_CD AS PLNT_PROC_CD 
					       ,B.ORD_DTL AS ORD_DTL
					       ,B.ITEMNAME_CD AS ITEMNAME_CD
					       ,B.SPEC_ABBSYM AS SPEC_ABBSYM
					       ,B.CC_CCM_NO AS CC_MC_CD
					       ,B.CC_CCM_NO AS CC_CCM_NO
					       ,B.WR_HCR_GP AS HCR_GP
					       ,B.ORD_HCR_GP AS ORD_HCR_GP
					       ,B.DEMANDER_CD AS DEMANDER_CD
					       ,B.WO_MSLAB_RPR_MTD AS WO_MSLAB_RPR_MTD
					       ,B.YD_STK_BED_NO AS YD_STK_BED_NO
					       ,B.STACK_LOT_NO AS STACK_LOT_NO
					  FROM  TB_YD_STOCK A
					       ,TB_PT_MSLABCOMM B
					 WHERE  A.STL_NO = B.MSLAB_NO
					   AND (A.DEL_YN IS NULL OR A.DEL_YN <> 'Y')
					   AND  MSLAB_NO = :V_MSLAB_NO
   
            		//nRet = ydStockDao.getYdStock(recPara, rsResult, 184);
            	 */
            	
            	JDTORecordSet rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCommonFieldYdStockMslabCommBySTLNo", logId, methodNm, "주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 주편공통)");
				if(rsResult.size() < 0){
    				szMsg = "주편공통 조회내용이 없습니다. (주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회)";
    				jrRtn.setField("RTN_CD"	, "0");
    				jrRtn.setField("RTN_MSG", szMsg);
    				return jrRtn;					 
				} else if(rsResult.size() == 0){
					//=================================================================================================
					// 슬라브공통 조회 (intGp : 185)
					//    쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockSlabCommBySTLNo
					// 파라미터 : V_SLAB_NO
					//=================================================================================================
					//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara  = JDTORecordFactory.getInstance().create();								
					recPara.setField("SLAB_NO", szStlNo);
					//nRet = ydStockDao.getYdStock(recPara, rsResult, 185);
	            	/* 주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 슬라브공통)  
	            	 * (원본) "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockSlabCommBySTLNo"
	            	 * 
	            	 * SELECT B.SLAB_NO AS STL_NO                                                                 
						    , B.YD_GP AS YD_GP                                                                  
						    , B.RECORD_PROG_STAT AS RECORD_PROG_STAT                                            
						    , B.STL_APPEAR_GP AS STL_APPEAR_GP                                                  
						    , B.CURR_PROG_CD AS STL_PROG_CD                                                     
						    , B.ORD_YEOJAE_GP AS ORD_YEOJAE_GP                                                  
						    , B.ORD_NO AS ORD_NO                                                                
						    , B.SLAB_WO_RT_CD AS SLAB_WO_RT_CD                                                  
						    , B.PTOP_PLNT_GP AS PTOP_PLNT_GP                                                    
						    , B.SCARFING_YN AS SCARFING_YN                                                      
						    , B.SCARFING_DONE_YN AS SCARFING_DONE_YN                                            
						    , B.REAL_MEASURE_SLAB_T AS YD_MTL_T                                                 
						    , B.REAL_MEASURE_SLAB_W AS YD_MTL_W                                                 
						    , B.REAL_MEASURE_SLAB_LEN AS YD_MTL_L                                               
						    , B.CAL_SLAB_WT AS YD_MTL_WT                                                        
						    , B.PLNT_PROC_CD AS PLNT_PROC_CD                                                    
						    , B.ORD_DTL AS ORD_DTL                                                              
						    , B.ITEMNAME_CD AS ITEMNAME_CD                                                      
						    , B.SPEC_ABBSYM AS SPEC_ABBSYM                                                      
						    , B.CCM_NO AS CC_MC_CD                                                              
						    , B.CCM_NO AS CC_CCM_NO                                                             
						    , B.HCR_GP AS HCR_GP                                                                
						    , B.ORD_HCR_GP AS ORD_HCR_GP                                                        
						    , B.DEMANDER_CD AS DEMANDER_CD                                                      
						    , B.WO_MSLAB_RPR_MTD AS WO_MSLAB_RPR_MTD                                            
						    , B.YD_STK_BED_NO AS YD_STK_BED_NO                                                  
						    , B.STACK_LOT_NO AS STACK_LOT_NO                                                    
						FROM TB_YD_STOCK A                                                                      
						    , TB_PT_SLABCOMM B                                                                  
						WHERE A.STL_NO = B.SLAB_NO                                                              
						    AND (A.DEL_YN IS NULL OR A.DEL_YN <> 'Y')                                           
						    AND SLAB_NO = :V_SLAB_NO

						//nRet = ydStockDao.getYdStock(recPara, rsResult, 185);
	            	 */
	            	
	            	rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCommonFieldYdStockSlabCommBySTLNo", logId, methodNm, "주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 슬라브공통) - V_SLAB_NO");
					if(rsResult.size() < 0){
	    				szMsg = "주편공통 조회내용이 없습니다. ()";
	    				jrRtn.setField("RTN_CD"	, "0");
	    				jrRtn.setField("RTN_MSG", szMsg);
	    				return jrRtn;						
					} else if(rsResult.size() == 0){
	    				szMsg = "주편공통 조회내용이 없습니다. (주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회)";
	    				jrRtn.setField("RTN_CD"	, "0");
	    				jrRtn.setField("RTN_MSG", szMsg);
	    				return jrRtn;						 
					} else {
						rsResult.first();
						recGetVal = rsResult.getRecord();
						
						strStlGp = "SLAB";	
					}			
				} else {
					rsResult.first();
					recGetVal = rsResult.getRecord();
					
					strStlGp = "MSLAB";	
					
					szRECORD_PROG_STAT = slabUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
					
					szMsg = "재료번호(" + szStlNo + ") 주편공통 레코드 상태(" + szRECORD_PROG_STAT + ")";
					slabUtils.printLog(logId, szMsg, "S-");
					
					if("3".equals(szRECORD_PROG_STAT.trim())){
						//=================================================================================================
						// 슬라브공통 조회 (intGp : 185)
						//    쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockSlabCommBySTLNo
						// 파라미터 : V_SLAB_NO
						//=================================================================================================
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recPara  = JDTORecordFactory.getInstance().create();								
						recPara.setField("SLAB_NO", szStlNo);
		            	/* 주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 슬라브공통) - V_SLAB_NO
		            	 * (원본) "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockSlabCommBySTLNo"
		            	 * 
							//nRet = ydStockDao.getYdStock(recPara, rsResult, 185);    //(윗부분 쿼리 참조)
		            	 */
		            	
		            	rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getCommonFieldYdStockSlabCommBySTLNo", logId, methodNm, "주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 슬라브공통) - V_SLAB_NO");

						if(rsResult.size() < 0){
		    				szMsg = "슬라브공통 조회내용이 없습니다. ()";
		    				jrRtn.setField("RTN_CD"	, "0");
		    				jrRtn.setField("RTN_MSG", szMsg);
		    				return jrRtn; 		
						} else if(rsResult.size() == 0){
		    				szMsg = "슬라브공통 조회내용이 없습니다. (주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회)";
		    				jrRtn.setField("RTN_CD"	, "0");
		    				jrRtn.setField("RTN_MSG", szMsg);
		    				return jrRtn;							 
						} else {
							
				            rsResult.first();
							recGetVal = rsResult.getRecord();	
							
							strStlGp = "SLAB";	
						}
					}
				}			
            }
			//===================================================================================
			// (주편 or 슬라브공통)과 저장품 필수 공통항목 편집 
			//===================================================================================
			recEditRec = slabUtils.getParam(logId, methodNm, sModifier);
				
			recEditRec.setField("STL_NO"          , slabUtils.paraRecChkNull(recGetVal, "STL_NO"));                           
			recEditRec.setField("STL_APPEAR_GP"   ,	slabUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
			recEditRec.setField("STL_PROG_CD"     ,	slabUtils.paraRecChkNull(recGetVal, "STL_PROG_CD"));
			recEditRec.setField("ORD_YEOJAE_GP"   ,	slabUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"));
			recEditRec.setField("ORD_NO"          ,	slabUtils.paraRecChkNull(recGetVal, "ORD_NO"));
			recEditRec.setField("SLAB_WO_RT_CD"   ,	slabUtils.paraRecChkNull(recGetVal, "SLAB_WO_RT_CD"));
			recEditRec.setField("PTOP_PLNT_GP"    ,	slabUtils.paraRecChkNull(recGetVal, "PTOP_PLNT_GP"));
			recEditRec.setField("SCARFING_YN"     ,	slabUtils.paraRecChkNull(recGetVal, "SCARFING_YN"));
			recEditRec.setField("SCARFING_DONE_YN",	slabUtils.paraRecChkNull(recGetVal, "SCARFING_DONE_YN"));
			recEditRec.setField("YD_MTL_T"        ,	slabUtils.paraRecChkNull(recGetVal, "YD_MTL_T"));
			recEditRec.setField("YD_MTL_W"        ,	slabUtils.paraRecChkNull(recGetVal, "YD_MTL_W"));
			recEditRec.setField("YD_MTL_L"        ,	slabUtils.paraRecChkNull(recGetVal, "YD_MTL_L"));
			recEditRec.setField("YD_MTL_WT"       ,	slabUtils.paraRecChkNull(recGetVal, "YD_MTL_WT"));
			recEditRec.setField("PLNT_PROC_CD"    ,	slabUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD"));
			recEditRec.setField("ORD_DTL"         ,	slabUtils.paraRecChkNull(recGetVal, "ORD_DTL"));
			recEditRec.setField("ITEMNAME_CD"     ,	slabUtils.paraRecChkNull(recGetVal, "ITEMNAME_CD"));
			recEditRec.setField("SPEC_ABBSYM"     ,	slabUtils.paraRecChkNull(recGetVal, "SPEC_ABBSYM"));
			recEditRec.setField("CC_CCM_NO"       ,	slabUtils.paraRecChkNull(recGetVal, "CC_CCM_NO"));
			recEditRec.setField("HCR_GP"          ,	slabUtils.paraRecChkNull(recGetVal, "HCR_GP"));
			recEditRec.setField("ORD_HCR_GP"      ,	slabUtils.paraRecChkNull(recGetVal, "ORD_HCR_GP"));
			recEditRec.setField("DEMANDER_CD"     ,	slabUtils.paraRecChkNull(recGetVal, "DEMANDER_CD"));
			recEditRec.setField("WO_MSLAB_RPR_MTD",	slabUtils.paraRecChkNull(recGetVal, "WO_MSLAB_RPR_MTD"));
			recEditRec.setField("YD_STK_LOT_CD"   ,	slabUtils.paraRecChkNull(recGetVal, "STACK_LOT_NO"));
	                    
			//===================================================================================
			// 코드매핑 처리
			//===================================================================================
			outRecTemp = JDTORecordFactory.getInstance().create();
			nRet = this.MakeCodeMapping(szTcCode, szStlNo, recGetVal, outRecTemp, strStlGp);
			if(nRet <= 0){
				szMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다.";
				slabUtils.printLog(logId, szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);
				return jrRtn;
				
			}else {
				String szYD_AIM_RT_GP = slabUtils.paraRecChkNull(outRecTemp, "YD_AIM_RT_GP");
				if(!"".equals(szYD_AIM_RT_GP)){
					recEditRec.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
				}

				String szYD_AIM_YD_GP = slabUtils.paraRecChkNull(outRecTemp, "YD_AIM_YD_GP");
				if(!"".equals(szYD_AIM_YD_GP)){
					recEditRec.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
				}

				String szYD_AIM_BAY_GP = slabUtils.paraRecChkNull(outRecTemp, "YD_AIM_BAY_GP");
				if(!"".equals(szYD_AIM_BAY_GP)){
					recEditRec.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
				}
			}
			
			nRet = this.setYdStkLocTpCd(recEditRec, logId , usrId);
			if( nRet < 0 ){
				szMsg= "[산적LotType 산적LotCD SET] Error :: [" + nRet + "]";
				slabUtils.printLog(logId, szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);
				return jrRtn;
			}	
			
			//==================================================================================================
			// (주편 or 슬라브) 공통에서 읽어온 항목들을 저장품 테이블에 업데이트
			//==================================================================================================
			recEditRec.setField("MODIFIER", szTcCode);
			/*
			 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStock
			 *   UPDATE TB_YD_STOCK 
				   SET SNDBK_GP      = :V_SNDBK_GP
				     , MODIFIER      = :V_MODIFIER
				     , MOD_DDTT      = SYSDATE
				     , SNDBK_GP_ETC  =:V_SNDBK_GP_ETC
				 WHERE STL_NO        = :V_STL_NO
				//nRet = ydStockDao.updYdStock(recEditRec, 0);
			 */
			nRet = this.update(recEditRec, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdStock", logId, methodNm, "슬라브 수정");	

			if(nRet <= 0){
				szMsg= "공통에서 읽어온 항목들을 저장품 테이블에 업데이트 실패 [" + nRet + "] TCCODE(" + szTcCode + ")";
				slabUtils.printLog(logId, szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg);
				return jrRtn;
			}else {
				szMsg= "공통에서 읽어온 항목들을 저장품 테이블에 업데이트 성공 [" + nRet + "] TCCODE(" + szTcCode + ")";
				slabUtils.printLog(logId, szMsg, "SL");
			}
			
			if(isSend){
				//===================================================================================================
				// 야드L2로 저장품 제원 전송
				//===================================================================================================
				szYdGp = slabUtils.paraRecChkNull(recGetVal, "YD_GP");
				
				recResult = JDTORecordFactory.getInstance().create();
				if(PSlabYdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){	// A후판슬라브야드
					recResult.setField("MSG_ID", "YDY3L002");
					szMsg = "후판슬라브 L2로 저장품제원(YDY3L002) 송신";
				}else {
					szMsg = "(-3)야드구분값이 없거나 지원하지 않는 야드 구분입니다. YD_GP(" + szYdGp + ")";
					slabUtils.printLog(logId, szMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg);
					return jrRtn;
				}
				recResult.setField("YD_INFO_SYNC_CD", "5");        // 5:지정저장품
				recResult.setField("STL_NO"         , szStlNo);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				recResult.setField("DEL_YN_CHECK"   , "N");
				
				jrRtn = slabUtils.addSndData(jrRtn, this.getMsgL2("YDY3L002", recResult));	
			}	
			
			
			
			jrRtn.setField("RTN_CD"	, "0");
			jrRtn.setField("RTN_MSG", szMsg);
			return jrRtn;									//return intRtnVal;		
			
		}catch(JDTOException e) {
			szMsg = "[" + methodNm + "] 공통업무 처리시 예외메세지: " + e.getMessage();
			slabUtils.printLog(logId, szMsg, "SL");
			//throw new DAOException(szClassName + " : " + szMethodName + e.getMessage(), e);
			return jrRtn;
		}
	}
	
	//\

	/**
	 * 산적LOT TYPE, CODE
	 * 박영수 
	 * return  0: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param outRec
	 * @throws JDTOException
	 */
	public int setYdStkLocTpCd(JDTORecord recOut, String logId, String methodNm2) throws JDTOException {
		//=============================================================================
		// 산적 LOT타입, 산적LOT코드 항목을 구함
		// 
		// STL_NO                     재료번호
		// SLAB_WO_RT_CD              슬라브지시행선코드
		// SCARFING_YN           	    스카핑여부
		// SCARFING_DONE_YN           스카핑완료여부
		// YD_AIM_RT_GP               야드목표행선구분
		// STACK_LOT_NO               산적LOT번호
		//=============================================================================
		//YdStockDao ydStockDao        		= new YdStockDao();
		//진행관리 - 이송지시
		//PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
		
		JDTORecordSet rsResult       = null;
		String methodNm				 = "산적 LOT타입, 산적LOT코드 항목을 구함" +methodNm2;
		
		JDTORecord recPara           = null;		
		JDTORecord recGetVal         = null;
		String szStlNo               = "";
		String szSlabWoRtCd          = "";
		String szScarfingYn    	     = ""; 
		String szScarfingDoneYn      = "";
		String szYdAimRtGp	         = "";
		String szStackLotNo          = "";
		String szYdStkLotTp	         = ""; 
		String szYdStkLotCd          = "";
		String szYdChgNo	 		 = "";
		String szArrWlocCd 		     = "";
		String szOrdYeojaeGp 		 = ""; 
		String szProdDueDate 		 = ""; 
		//String szMethodName		 = "setYdStkLocTpCd";
		//String szOperationName     = "산적LOT타입코드";
		String szMsg                 = "";
		int nRet                     = 0;
		
		//=================================================
		// 파라미터 레코드로부터 항목추출
		//=================================================
		szStlNo          = slabUtils.paraRecChkNull(recOut, "STL_NO");              // 재료번호
		szYdAimRtGp	     = slabUtils.paraRecChkNull(recOut, "YD_AIM_RT_GP");        // 야드목표행선구분
		szStackLotNo     = slabUtils.paraRecChkNull(recOut, "YD_STK_LOT_CD");       // 산적LOT번호 (STACK_LOT_NO)
	    
		szSlabWoRtCd     = StringHelper.evl(recOut.getFieldString("SLAB_WO_RT_CD"), "").trim();		// 슬라브지시행선  
		szScarfingYn 	 = StringHelper.evl(recOut.getFieldString("SCARFING_YN"), "N").trim();		// 스카핑여부
		szScarfingDoneYn = StringHelper.evl(recOut.getFieldString("SCARFING_DONE_YN"), "N").trim();	// 스카핑완료여부  
		
		/*
		 *  스카핑대상이 아닌것은 완료로 본다.
		 */
		if("N".equals(szScarfingDoneYn)){
			if("N".equals(szScarfingYn)){
				szScarfingDoneYn	= "Y";
			}
		}
		
		try{
			slabUtils.printLog(logId, szMsg, "S+");
			
			
			//=======================================================================================================================
			// 재료번호로 가열로장입LOT번호와 가열로장입LOT순번 조회
			//=======================================================================================================================
			//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();						
			recPara.setField("STL_NO", szStlNo);
			
			if(szYdAimRtGp.startsWith("C"))
			{
				if("C1".equals(szYdAimRtGp) || "C2".equals(szYdAimRtGp)){
					// 슬라브 열연장입Lot번호    [야드목표행선구분(YD_AIM_RT_GP) + 야드장입순번 :가열로장입Lot번호(REFUR_CHG_LOT_NO)]
					
					/* 재료번호로 가열로장입LOT번호와 가열로장립LOT순번 조회(슬라브)
					 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getHrMillWoBySTLNo
					    nRet = ydStockDao.getYdStock(recPara, rsResult, 203);
					    
					 */
					rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getHrMillWoBySTLNo", logId, methodNm, "조회");
					nRet = 0;
				}else if("C3".equals(szYdAimRtGp)){
				    // 슬라브 후판장입일련번호    [야드목표행선구분(YD_AIM_RT_GP) + 야드장입순번 :가열로장입장입일련번호(REFUR_CHG_PLN_SERNO)]
					
					/* 재료번호로 가열로장입LOT번호와 가열로장립LOT순번 조회(후판)
					 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLREFURWOBySTLNo
					    nRet = ydStockDao.getYdStock(recPara, rsResult, 205);

					 */
					rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getPLREFURWOBySTLNo", logId, methodNm, "조회");
					nRet = 0;
				}
				
				if(nRet < 0) {
					szMsg = "재료번호로 가열로장입LOT번호와 가열로장입LOT순번 조회 오류 (" + nRet + ") STL_NO(" + szStlNo + ")";
					slabUtils.printLog(logId, szMsg, "SL");
				} else if(nRet == 0){
					szMsg = "재료번호로 가열로장입LOT번호와 가열로장입LOT순번 조회건수 없음 (" + nRet + ") STL_NO(" + szStlNo + ")";
					slabUtils.printLog(logId, szMsg, "SL");
				} else {
					szMsg = "재료번호로 가열로장입LOT번호와 가열로장입LOT순번 조회성공 (" + nRet + ") STL_NO(" + szStlNo + ")";
					slabUtils.printLog(logId, szMsg, "SL");
	
					rsResult.first();
					recGetVal = rsResult.getRecord();
	
					szYdChgNo	  = slabUtils.paraRecChkNull(recGetVal, "YD_CHG_NO");    
				}
			}else if(szYdAimRtGp.startsWith("E"))
			{
				/* 
				 * (원본) com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.getPtStlFrtoMove
				 * 
				   nRet = ptStlFrtoMoveDao.getPtStlFrtoMove(recPara, rsResult, 0); 
				 */
				JDTORecordSet recordSet3 = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getPtStlFrtoMove", logId, methodNm, "조회1");
				nRet = recordSet3.size();
				if(nRet < 0) {
					szMsg = "재료번호로 이송지시 불출개소코드 조회 오류 (" + nRet + ") STL_NO(" + szStlNo + ")";
					slabUtils.printLog(logId, szMsg, "SL");
				} else if(nRet == 0){
					szMsg = "재료번호로이송지시 불출개소코드 조회건수 없음 (" + nRet + ") STL_NO(" + szStlNo + ")";
					slabUtils.printLog(logId, szMsg, "SL");
				} else {
					szMsg = "재료번호로 이송지시 불출개소코드 조회성공 (" + nRet + ") STL_NO(" + szStlNo + ")";
					slabUtils.printLog(logId, szMsg, "SL");
	
					recordSet3.first();
					recGetVal = recordSet3.getRecord();
	
					szArrWlocCd = slabUtils.paraRecChkNull(recGetVal, "ARR_WLOC_CD");    // 불출개소 
				}
			}else if(szYdAimRtGp.equals("A4")|| // 정정대기(PA-CCR)
					 szYdAimRtGp.equals("A9"))  // 정정대기(PA-HCR)
			{
				recPara.setField("MSLAB_NO", szStlNo);	
				/*
				 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockOSCOMM
				 * 
					nRet = ydStockDao.getYdStock(recPara, rsResult, 35);
				 */
				
				
				if(nRet < 0) {
					szMsg = "재료번호로 주여구분/생산기한일 조회 오류 (" + nRet + ") STL_NO(" + szStlNo + ")";
					slabUtils.printLog(logId, szMsg, "SL");
				} else if(nRet == 0){
					szMsg = "재료번호로 주여구분/생산기한일 조회 건수 없음 (" + nRet + ") STL_NO(" + szStlNo + ")";
					slabUtils.printLog(logId, szMsg, "SL");
				} else {
					szMsg = "재료번호로 주여구분/생산기한일 조회 성공 (" + nRet + ") STL_NO(" + szStlNo + ")";
					slabUtils.printLog(logId, szMsg, "SL");
	
					rsResult.first();
					recGetVal = rsResult.getRecord();
					szOrdYeojaeGp = StringHelper.evl(recGetVal.getFieldString("ORD_YEOJAE_GP"), "2").trim(); 		// 주여구분 
					szProdDueDate = StringHelper.evl(recGetVal.getFieldString("PROD_DUE_DATE"), "00000000").trim(); 	// 생산기한일 
				}
			}
			
			szYdAimRtGp = szYdAimRtGp.trim();
			szMsg = "TPCODE 조회조건값   : " + szYdAimRtGp;
			slabUtils.printLog(logId, szMsg, "SL");
			
			//=====================================================================
			// 산적 LOT TYPE과 CODE설정
			//=====================================================================
			if(szYdAimRtGp.equals("A1") || szYdAimRtGp.equals("A2") || szYdAimRtGp.equals("A3") || szYdAimRtGp.equals("A5") || 
			   szYdAimRtGp.equals("A6") || szYdAimRtGp.equals("A7") || szYdAimRtGp.equals("A8") || szYdAimRtGp.equals("AA")){
				
				// 슬라브 정정대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 저장품 : 야드목표행선구분(YD_AIM_RT_GP)] 
				szYdStkLotTp = PSlabYdConstant.LOT_TYPE_SLAB_SHEAR;
				szYdStkLotCd = szScarfingDoneYn + szSlabWoRtCd + szYdAimRtGp; 
			
			}else if(szYdAimRtGp.equals("A4")|| 
					 szYdAimRtGp.equals("A9")){
						
				// 슬라브 정정대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 저장품 : 야드목표행선구분(YD_AIM_RT_GP)+주여구분(ORD_YEOJAE_GP)+생산기한일(PROD_DUE_DATE)] 
				szYdStkLotTp = PSlabYdConstant.LOT_TYPE_SLAB_SHEAR;
				if("1".equals(szOrdYeojaeGp)){
					szYdStkLotCd = szScarfingDoneYn + szSlabWoRtCd + szYdAimRtGp + szOrdYeojaeGp + szProdDueDate;
				}else{
					szYdStkLotCd = szScarfingDoneYn + szSlabWoRtCd + szYdAimRtGp + szOrdYeojaeGp + "00000000";
				}
						
			}else if(szYdAimRtGp.equals("B1") || szYdAimRtGp.equals("B2") || szYdAimRtGp.equals("B3") || szYdAimRtGp.equals("B4") || szYdAimRtGp.equals("B5") || 
					 szYdAimRtGp.equals("B6") || szYdAimRtGp.equals("B7")){
				
				// 슬라브 지시대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 산적LOT번호(STACK_LOT_NO)]     
				szYdStkLotTp = PSlabYdConstant.LOT_TYPE_SLAB_WO;
				if(szStackLotNo.length()> 13) szStackLotNo = szStackLotNo.substring(3);
				szYdStkLotCd = szScarfingDoneYn + szSlabWoRtCd + szStackLotNo;
				
			}else if(szYdAimRtGp.equals("C1") || szYdAimRtGp.equals("C2")){
				
				// 슬라브 열연장입Lot번호    [야드목표행선구분(YD_AIM_RT_GP) + 가열로장입Lot번호(REFUR_CHG_LOT_NO)]
				szYdStkLotTp = PSlabYdConstant.LOT_TYPE_LOT_NO;
			    szYdStkLotCd = szYdAimRtGp + szYdChgNo;
				    
			}else if(szYdAimRtGp.equals("C3")){
				
			     // 슬라브 후판장입일련번호    [야드목표행선구분(YD_AIM_RT_GP) + 가열로장입장입일련번호(REFUR_CHG_PLN_SERNO)]
				szYdStkLotTp = PSlabYdConstant.LOT_TYPE_SLAB_PLN_SER;
				szYdStkLotCd = szYdAimRtGp + szYdChgNo;
			     	 
			}else if(szYdAimRtGp.equals("Y1") || szYdAimRtGp.equals("Y2") || szYdAimRtGp.equals("Y3") || szYdAimRtGp.equals("Y4") || szYdAimRtGp.equals("Y5") || 
					 szYdAimRtGp.equals("Y6") || szYdAimRtGp.equals("Y7") || szYdAimRtGp.equals("Y8") || szYdAimRtGp.equals("YA")){
				
				// 슬라브 충당대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 산적LOT번호(STACK_LOT_NO)]  
				szYdStkLotTp = PSlabYdConstant.LOT_TYPE_SLAB_SHUNG;
				if(szStackLotNo.length()> 9) szStackLotNo = szStackLotNo.substring(3);
				szYdStkLotCd = szScarfingDoneYn + szSlabWoRtCd + szStackLotNo; 
				     
			}else if(szYdAimRtGp.equals("E1") || szYdAimRtGp.equals("E2") || szYdAimRtGp.equals("E3") || szYdAimRtGp.equals("E4") || szYdAimRtGp.equals("E5") || 
					 szYdAimRtGp.equals("E6") || szYdAimRtGp.equals("E7") || szYdAimRtGp.equals("E8") || szYdAimRtGp.equals("E9") || szYdAimRtGp.equals("EA")){
				
				// 슬라브 이송대기    [스카핑완료여부(SCARFING_DONE_YN) + 슬라브지시행선(SLAB_WO_RT_CD) + 저장품 : 야드목표행선구분(YD_AIM_RT_GP)] + 불출개소코드. 
				szYdStkLotTp = PSlabYdConstant.LOT_TYPE_SLAB_TRAN;
				szYdStkLotCd = szScarfingDoneYn + szSlabWoRtCd + szYdAimRtGp + szArrWlocCd; 
				     
			}else if(szYdAimRtGp.equals("GA") || szYdAimRtGp.equals("HA") || szYdAimRtGp.equals("KA") || szYdAimRtGp.equals("LA") || szYdAimRtGp.equals("MA") || 
					szYdAimRtGp.equals("NA") || szYdAimRtGp.equals("OA") || szYdAimRtGp.equals("ZA")){

				// 슬라브 외판대기    [스카핑완료여부(SCARFING_DONE_YN) +  저장품 : 야드목표행선구분(YD_AIM_RT_GP)]
				szYdStkLotTp = PSlabYdConstant.LOT_TYPE_MS;
				szYdStkLotCd = szScarfingDoneYn + szSlabWoRtCd + szYdAimRtGp; 
			
			}else{
				// 해당 업무가 없을시에
				szMsg ="정의된 LOT타입과 코드가  없음 ";
				slabUtils.printLog(logId, szMsg, "SL");
				szYdStkLotTp = PSlabYdConstant.LOT_TYPE_WO;				     
				szYdStkLotCd = szScarfingDoneYn + szSlabWoRtCd + szYdAimRtGp; 				 
			}
			
			if(szYdStkLotTp.equals("")){
				szMsg ="LOT CODE :: LOT TYPE이 공란이므로 공란으로 설정됨";
				slabUtils.printLog(logId, szMsg, "SL");
				szYdStkLotCd = "";
			}
			
			//===========================================================================	
			// 산적LOT타입, 산적LOT코드 값을 레코드에 설정
			//===========================================================================	
			recOut.setField("YD_STK_LOT_TP", szYdStkLotTp); // 야드산적Lot타입
			recOut.setField("YD_STK_LOT_CD", szYdStkLotCd); // 야드산적Lot코드

			szMsg = "================================== setYdStkLocTpCd() OUT ==================================";
			slabUtils.printLog(logId, szMsg, "SL");
	
		} catch(Exception e){
			szMsg = "산적LOT타입과 코드 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			slabUtils.printLog(logId, szMsg, "S-");
			throw new JDTOException(szMsg);
		}
		
		return 1;
	}
	
	public int MakeCodeMapping(String szTcCode, String szSTL_NO, JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		return this.MakeCodeMapping(szTcCode, szSTL_NO, inRec, outRec, ""); 
	}
	
	/**
	 * 데이터 매핑 함수
	 * 박영수
	 * @param inRec, outRec
	 * @return 처리건수
	 */	
	public int MakeCodeMapping(	String szTcCode, 
								String szSTL_NO, 
								JDTORecord inRec, 
								JDTORecord outRec, 
								String strStlGp) throws JDTOException {
		
		JDTORecord recPara     = null;
		JDTORecordSet rsResult = null;
		JDTORecord recGetVal   = null; 
		String szMethodName    = "MakeCodeMapping";
		String szMsg           = "";
		//String szOperationName = "데이터 매핑 함수";
		String szRECORD_PROG_STAT = "";
		String szCC_MC_CD      = "";
		String szCC_CCM_NO     = "";
		String szPLNT_PROC_CD  = "";
		String szYD_GP         = "";
		String strPT_TB_COMM   = "";
		
		String logId			= "";
		String methodNm2		= "";	
		String userId			= "";
		String methodNm         = "데이터 매핑 함수(MakeCodeMapping)";
		int nRet               = 0;

		// Debug MSG
		slabUtils.printLog("MakeCodeMapping", "\n======= YdCodeMapping::MakeCodeMapping() IN ====================\n", "SL");
		slabUtils.printLog("MakeCodeMapping", "MakeCodeMapping() : TCCode(" + szTcCode + ") STL_NO(" + szSTL_NO + ")", "SL");
		slabUtils.printLog("MakeCodeMapping", "\n================================================================\n", "SL");

		if("MSLAB".equals(strStlGp)) {
			//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();								
			recPara.setField("STL_NO", szSTL_NO);
			/* TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회
			 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMSLABCOMMYdStockYD_STKLYRToCodeMapping 
				//nRet = ydStockDao.getYdStock(recPara, rsResult, 134);
			 */
			rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getMSLABCOMMYdStockYD_STKLYRToCodeMapping", logId, methodNm, "TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회");
			
			if(rsResult.size() <= 0){
				return rsResult.size();
			}
		}else if ("SLAB".equals(strStlGp)) {
			//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();								
			recPara.setField("STL_NO", szSTL_NO);
			
			/* TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회
			 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabcomYdStockYD_STKLYRToCodeMapping 
				//nRet = ydStockDao.getYdStock(recPara, rsResult, 131);
			 */
			rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getSlabcomYdStockYD_STKLYRToCodeMapping", logId, methodNm, "TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회");
			if(rsResult.size() <= 0){
				return rsResult.size();
			}
		} else if ("".equals(strStlGp)) {

			//주편공통 조회
			//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();								
			recPara.setField("STL_NO", szSTL_NO);
			/* TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회
			 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMSLABCOMMYdStockYD_STKLYRToCodeMapping 
				//nRet = ydStockDao.getYdStock(recPara, rsResult, 134);
			 */
			rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getMSLABCOMMYdStockYD_STKLYRToCodeMapping", logId, methodNm, "TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회");
			if(rsResult.size() < 0){
				return rsResult.size();
			} else if(rsResult.size() == 0){
				// 슬라브 조회
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara  = JDTORecordFactory.getInstance().create();								
				recPara.setField("STL_NO", szSTL_NO);
				/* TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회
				 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabcomYdStockYD_STKLYRToCodeMapping 
					//nRet = ydStockDao.getYdStock(recPara, rsResult, 131);
				 */
				rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getSlabcomYdStockYD_STKLYRToCodeMapping", logId, methodNm, "TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회");
				if(nRet < 0){
					return nRet;
				} else if(nRet == 0){
					return nRet;
				} else {
	
					rsResult.first();
					recGetVal = rsResult.getRecord();
		
					szRECORD_PROG_STAT 	= slabUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
					strPT_TB_COMM 		= szRECORD_PROG_STAT;
				}			
			} else {

				rsResult.first();
				recGetVal = rsResult.getRecord();
				
				szRECORD_PROG_STAT 	= slabUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
				strPT_TB_COMM 		= szRECORD_PROG_STAT.trim();
				
				if(szRECORD_PROG_STAT.trim().equals("3")){
				
					recGetVal = null;
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara  = JDTORecordFactory.getInstance().create();								
					recPara.setField("STL_NO", szSTL_NO);
					/* TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회
					 * (원본) com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabcomYdStockYD_STKLYRToCodeMapping 
						//nRet = ydStockDao.getYdStock(recPara, rsResult, 131);
					 */
					rsResult = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getSlabcomYdStockYD_STKLYRToCodeMapping", logId, methodNm, "TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회");
					
					if(rsResult.size() < 0){
						return rsResult.size();
					} else if(rsResult.size() == 0){
						return rsResult.size();
					} else {
						szMsg = "TB_PT_SLABCOMM(슬라브공통) + TB_YD_STOCK(저장품) + TB_YD_STKLYR(적치단) 테이블 조회 성공 STL_NO(" + szSTL_NO + ")";
			            //ydUtils.putLog(methodNm, szMethodName, szMsg, YdConstant.DEBUG);	
						slabUtils.printLog(logId, szMsg, "SL");
			            
					}
				}
			}			
		}
		
		// 조회된 주편 or 슬라브의 레코드를 읽어온다. 
		recGetVal = null;
		rsResult.first();
		recGetVal = rsResult.getRecord();
		
		// 재료외형뿐만 아니라 스키핑여부와 스카핑완료도 공통에 읽은 것을 저장품에 업데이트 처리 위해 편집 
		outRec.setField("STL_APPEAR_GP"   , slabUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
		outRec.setField("SCARFING_YN"     , slabUtils.paraRecChkNull(recGetVal, "SCARFING_YN"));
		outRec.setField("SCARFING_DONE_YN", slabUtils.paraRecChkNull(recGetVal, "SCARFING_DONE_YN"));
		
		recGetVal.setField("JMS_TC_CD", szTcCode);
		
		nRet = this.CallMapping(recGetVal, outRec, strPT_TB_COMM, logId, methodNm2, userId); 
		
		return nRet;
		
	}
	


	//박영수 2020.09.15
    public static String nvl(Object o, String defaultValue) {
        return (o == null) ? defaultValue.trim() : o.toString().trim();
    }

    

	/**
	 * 이송완료 실적처리
	 * 박영수 2020.09.15
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String STL_NO
	 * @return 성공 또는 보낼필요가 없을경우: YdConstant.RETN_CD_SUCCESS , 실패 : YdConstant.RETN_CD_FAILURE
	 * @throws DAOException
	 */
	public JDTORecord sendYDPRJ003(String pzStlNo, String logId, String usrId) throws DAOException {
		/*
		 * 1.입고시에 해당재료정보로 차량스케줄 정보를 조회하여 
		 *   발지개소가 후판공장일경우 해당 TC 를 보내기 위함 
		 * 
		 *  후판공장 발지개소 : YdConstant.WLOC_CD_A_PLATE_PLANT
		 */
		JDTORecord recPara = null;

		String szOperationName 	= "이송완료 실적처리";
		String szMsg 			= null;
		String methodNm         = "이송완료 실적처리(sendYDPRJ003)"; 
		
		int intRtnVal = 0;
		JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, usrId);
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 시작";
			slabUtils.printLog(logId, "" + "", "SL");
			
			recPara = JDTORecordFactory.getInstance().create(); // 초기화		
			recPara.setField("STL_NO", pzStlNo);
			
			// 1. 해당 재료로 차량 스케줄 정보를 조회한다.
			szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 스케줄/재료 정보 조회";
			slabUtils.printLog(logId, "" + szMsg, "SL");
			
			/* 재료번호로  차량스케줄 조회 
			 * (원본) com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNo
			 *  SELECT A.*
				  FROM USRYDA.TB_YD_CARSCH A
				     , USRYDA.TB_YD_CARFTMVMTL B
				 WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				   AND A.DEL_YN ='N'
				   AND B.DEL_YN ='N'
				   AND B.STL_NO =:V_STL_NO
			 *  
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsCarSch, 35);
			 */
			
			JDTORecordSet rsCarSch = this.select(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCarschByStlNo", logId, methodNm, "차량 스케줄/재료 정보 조회");
			intRtnVal = rsCarSch.size(); 
			
			
			if(intRtnVal < 0 ){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 스케줄/재료 정보 조회 ERROR";
				slabUtils.printLog(logId, "" + szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "전문처리대상 없음(차량 스케줄/재료 정보 조회 ERROR)");

				return jrRtn; 
				
			}else if(intRtnVal == 0){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 차량 스케줄/재료 정보 조회된 데이터가 없습니다.";
				slabUtils.printLog(logId, "" + szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "차량 스케줄/재료 정보 조회된 데이터가 없습니다.");

				return  jrRtn; 
			}
			recPara = JDTORecordFactory.getInstance().create(); // 초기화
			rsCarSch.first();			
			recPara = rsCarSch.getRecord();
			String sJmsTcCd = ""; 
			
			// 2. 차량 스케줄 정보에서 발지개소코드가 후판공장인지 체크한다.			
			szMsg = "[Jsp Session  -  " + szOperationName +"] 발지개소코드 체크";
			slabUtils.printLog(logId, "" + szMsg, "SL");
			
			if(slabUtils.paraRecChkNull(recPara, "SPOS_WLOC_CD").equals(PSlabYdConstant.WLOC_CD_A_PLATE_PLANT)){
				szMsg = "[Jsp Session  -  " + szOperationName +"] 발지개소코드가 1후판공장이므로 전문 전송";
				slabUtils.printLog(logId, "" + szMsg, "SL");
				sJmsTcCd = PSlabYdConstant.YDPRJ003;
			} else if(slabUtils.paraRecChkNull(recPara, "SPOS_WLOC_CD").equals(PSlabYdConstant.WLOC_CD_B_PLATE_PLANT)){
					szMsg = "[Jsp Session  -  " + szOperationName +"] 발지개소코드가 2후판공장이므로 전문 전송";
					slabUtils.printLog(logId, "" + szMsg, "SL");
					sJmsTcCd = "YDPPJ003";
			}else{
				szMsg = "[Jsp Session  -  " + szOperationName +"] 발지개소코드가 후판공장이 아니므로 전문을 전송할 필요가 없습니다.";
				slabUtils.printLog(logId, "" + szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "발지개소코드가 후판공장(DKY23)이 아니므로 전문을 전송할 필요가 없습니다.");
				//return  PSlabYdConstant.RETN_CD_SUCCESS;
				return jrRtn;
			}
						
			// 3. 해당 전문을 전송한다.			
			szMsg = "[Jsp Session  -  " + szOperationName +"] YDP(P)RJ003 전문전송";
			slabUtils.printLog(logId, "" + szMsg, "SL");
			
			recPara = JDTORecordFactory.getInstance().create(); // 초기화
			recPara.setField("JMS_TC_CD", sJmsTcCd);
			recPara.setField("STL_NO"	, pzStlNo);
			recPara.setField("EXT_DT"	, "");
			
			//YdDelegate ydDelegate = new YdDelegate();
			//ydDelegate.sendMsg(recPara);
			jrRtn = slabUtils.addSndData(jrRtn, recPara);
			
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "정상 처리 되었습니다.");
			slabUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	 
	
	
	public JDTORecord ProcedureAbmtlPmCall(JDTORecord inDto) {
		 DBAssistantDAO assistantDAO = new DBAssistantDAO();
		 
		 /*
           USRPMA.SP_PMC3_606_여재처리메인 (IN_JOB_GBN             IN  VARCHAR2         
                                         ,IN_STL_NO            IN  VARCHAR2         
                                         ,IN_YEOJAE_CAUSE_CD   IN  VARCHAR2    
                                         ,IN_STR_GP 			IN  VARCHAR2      
                                         ,W_ERR_CODE           OUT VARCHAR2 ) 

           IN_JOB_GBN : Y ( YARD JOB으로 로그 관리 )
           IN_STL_NO   : 재료 단위 한 매씩 호출 함. 
           IN_YEOJAE_CAUSE_CD : 여재 원인코드 ( 이상재 코드 일단 넣어주삼 => 변경이 필요할지도.....?? )
           IN_STR_GP : 비축재구분 Default 'N'
           W_ERR_CODE : 이건 ERROR 가 발생했을때 야드에서 OUTPUT 으로 활용하기 위함 ( NULL 이 아닌 값 )
		*/
		 
		/*
		 * { call USRPMA.SP_PMC3_606_여재처리메인(?,?,?,?,?) }
		 */
		try {

			String jspeed_query_id = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.ProcedureAbmtlPmCall";
			String add_query = "";
			Object[] inParam = {"Y" 
					            ,StringHelper.evl(inDto.getFieldString("SLAB_NO"),"")
								,StringHelper.evl(inDto.getFieldString("AB_OCCR_RSN_CD"),"")
								,"N"};
			
			int[] inParamIndex = {1,2,3,4};
	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {5};
	 		
			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
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
			/*
			SELECT IF_NM
			      ,P_PGM_NM1 AS CLASS_NAME
			      ,P_PGM_NM2 AS METHODE_NAME
			      ,PGM_NM3 AS QUEUE_NAME
			     , BEF_PGM_NM1
			     , BEF_PGM_NM2			      
			  FROM USRYDA.TB_YD_Z_IF
			 WHERE IF_ID = :V_IF_ID	
			 
 
			 */
			return getRecordSet("com.inisteel.cim.yd.pslabyd.dao.PSlabYdCommDAO.getMsgInfo", new Object[] { msgID });
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Procesure 호출 메소드
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
			
			String add_query = "";

	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {inParamIndex.length+1};
	 		
			return trtProcedure(queryId, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 	
	}		
	
    /**
     *      [A] 오퍼레이션명 : Procesure 호출 메소드
     * 
     * @param  Object[]         inParam         procedure input parameter array
     *         int[]            inParamIndex    procedure input parameter seq array 
     *         String           queryId         QueryId 
     * @return JDTORecord       procedure Result
     * @throws DAOException
     * @throws JDTOException 
     */     
    public JDTORecord callProcedure( Object[] inParam, int[] inParamIndex , String queryId, String[] outParamKey, int[] outParamType, int[] outParamIndex) throws DAOException, JDTOException {
        try {
            
            String add_query = "";
            
            return trtProcedure(queryId, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

        } catch (Exception e) {

            throw new DAOException(getClass().getName() + e.getMessage(),e);
        }   
    }       
    
	/**
	 *      [A] 오퍼레이션명 : INSERT Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */		
	public int insertTx(JDTORecord rcvMsg,String queryId) throws DAOException {

		return this.updateTx(rcvMsg, queryId);
	}	
	
	/**
	 *      [A] 오퍼레이션명 : INSERT Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public int insertTx(JDTORecord rcvMsg,String queryId, String logId, String mthdNm, String trtNm) throws DAOException {
		
		String methodNm = trtNm + "[insertTx] < " + mthdNm;
		int intRtnVal = 0;
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdCommEJB", this);
			intRtnVal = ((Integer)ejbConn.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId })).intValue();
			
			slabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
	public int updateTx(JDTORecord rcvMsg,String queryId) throws DAOException {
		
		String methodNm = "Transaction 분리메소드 호출 < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		int intRtnVal   = 0;
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdCommEJB", this);
			intRtnVal = ((Integer)ejbConn.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId })).intValue();
			
			slabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
	public int updateTx(JDTORecord rcvMsg,String queryId, String logId, String mthdNm, String trtNm) throws DAOException {
		
		String methodNm = trtNm + "[PSlabYdCommDAO.updateTx] < " + mthdNm;
		int intRtnVal = 0;
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdCommEJB", this);
			intRtnVal = ((Integer)ejbConn.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId })).intValue();
			
			slabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
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
	public String getSeqId(String logId, String mthdNm, String trtGp) throws DAOException {
		String methodNm = "SeqID조회[PSlabYdCommDAO.getSeqId] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			String seqId = ""; //반환할 Sequence ID

			if ("CrnSch".equals(trtGp)) {
				trtNm = "야드크레인스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getSeqIdCrnSch";
			} else if ("WrkBook".equals(trtGp)) {
				trtNm = "야드작업예약ID";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getSeqIdWrkBook";
			} else if ("PrepSch".equals(trtGp)) {
				trtNm = "야드준비스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdCommDAO.getSeqIdPrepSch";
			} else if ("TcarSch".equals(trtGp)) {
				trtNm = "야드대차스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getSeqIdTcarSch";
			} else if ("CarSch".equals(trtGp)) {
				trtNm = "야드차량스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getSeqIdCarSch";
			} else if ("RetHt".equals(trtGp)) {
				trtNm = "회송이력ID";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.getRetHtHistID";
			}else if ("PriYdPrepSch".equals(trtGp)) {
				trtNm = "동일상차야드준비스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getPriYdPrepSchId";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, null);

			if (jsRst.size() > 0) {
				seqId = slabUtils.trim(jsRst.getRecord(0).getFieldString("SEQ_ID")); //Sequence ID
			}
			
			return seqId;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
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
	public String PSlabApplyYn(String FieldName) throws DAOException {
		String methodNm = "구시스템 삭제 여부[PSlabApplyYn]" ;
		String logId = "";
		String APPLY_YN  = "N";

		try {
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
				JDTORecordSet jsApplyYNChk = this.select(jrParam, "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYnDualSql", logId, methodNm, "열정보 Read"); 


				APPLY_YN     = slabUtils.trim(jsApplyYNChk.getRecord(0).getFieldString(FieldName));
            
			

			return APPLY_YN;
		} catch (DAOException e) {
			
			return APPLY_YN;
		} catch (Exception e) {
			return APPLY_YN;
		}
	}

	
	
}
