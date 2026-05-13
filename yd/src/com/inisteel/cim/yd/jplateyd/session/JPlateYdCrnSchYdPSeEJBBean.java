/*
 * @(#) 1후판정정야드 크레인스케쥴 Session EJB
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/05
 *
 * @description		크레인스케쥴 Session EJB
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/05   김현우      김현우       최초작성  
 */

package com.inisteel.cim.yd.jplateyd.session;

import java.util.Vector;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;

import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSpecDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;

import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCrnSchUtil;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdGdsUtil;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdToLocUtil;

import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdConstant;
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 크레인스케쥴 Session EJB
 *
 * @ejb.bean name="JPlateYdCrnSchYdPSeEJB" jndi-name="JPlateYdCrnSchYdPSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JPlateYdCrnSchYdPSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;

	// Session Name
	private final static String SZ_SESSION_NAME = JPlateYdCrnSchYdPSeEJBBean.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();
	private JPlateYdDelegate 	ydDelegate 	= new JPlateYdDelegate();
	
	private YdPICommDAO ydPICommDAO   = new YdPICommDAO();
	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();

	// [DEBUG] message flag
	private boolean bDebugFlag = true;

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	 /**
	 * 오퍼레이션명 : 야드작업이력생성Main
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return void
	 * @throws DAOException
	 */
	public void procWorkHistoryCreate(JDTORecord msgRecord)throws DAOException  {

		JPlateYdCrnWrkMtlDAO	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();
		JPlateYdWrkHistDAO 		ydWrkHistDao 	= new JPlateYdWrkHistDAO();

		int 	intRtnVal 			= 0;
		String 	sMsg        		= "";
		String 	sRtnMsg        		= "";
		String 	sMethodName 		= "procWorkHistoryCreate";
		String 	sOperationName 		= "야드작업이력생성Main";

		String 	sCrnSchId 			= "";     	// 크레인스케줄ID
		String 	sStlNo 				= "";
		String	sModifier			= "";

		JDTORecordSet rsResult 		= null;
		JDTORecordSet rsCrnStock 	= null;
		JDTORecord recCrnStock 		= null;
		JDTORecord recMtl 			= null;

		try {
			sMsg = "["+sOperationName+"] --------------------- 시작";
			ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);

			sCrnSchId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			sModifier = ydDaoUtils.paraRecModifier(msgRecord);

			//레코드 생성
			JDTORecord recPara = JDTORecordFactory.getInstance().create();

			//크레인스케줄ID
			recPara.setField("YD_CRN_SCH_ID", sCrnSchId);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

			// 크레인스케줄의 작업재료들을 읽어온다. [작업완료(DEL_YN = 'Y') 정보도 포함 ]
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getByYdCrnSchIdWithDel 

			SELECT YD_CRN_SCH_ID    AS YD_CRN_SCH_ID
			     , A.STL_NO         AS STL_NO
			     , A.REGISTER       AS REGISTER
			     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			     , A.MODIFIER       AS MODIFIER
			     , A.DEL_YN         AS DEL_YN
			     , A.YD_AID_WRK_YN  AS YD_AID_WRK_YN
			     , A.YD_STK_LYR_NO  AS YD_STK_LYR_NO
			     , A.YD_STK_LOT_TP  AS YD_STK_LOT_TP
			     , A.YD_STK_LOT_CD  AS YD_STK_LOT_CD
			     , A.HCR_GP         AS HCR_GP
			     , A.STL_PROG_CD    AS STL_PROG_CD
			     , A.YD_MTL_ITEM    AS YD_MTL_ITEM
			     , A.YD_ROUTE_GP    AS YD_ROUTE_GP
			     , B.YD_MTL_T
			     , B.YD_MTL_W
			     , B.YD_MTL_L
			     , B.YD_MTL_WT
			  FROM TB_YD_CRNWRKMTL A
			     , TB_YD_SHRSTOCK B
			 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND A.STL_NO = B.STL_NO
			 ORDER BY YD_STK_LYR_NO DESC

			*/ 
			intRtnVal = ydCrnWrkMtlDao.getByYdCrnSchIdWithDel(recPara, rsResult);

			if (intRtnVal <= 0) {
	  			if (intRtnVal == 0) {
	  				sMsg = "크레인스케줄ID(" + sCrnSchId + ")에 대한 재료데이터가 없습니다.";
	  				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
	  				throw new DAOException(sMsg);
	  			} else if (intRtnVal == -2) {
	  				sMsg = "크레인스케줄ID(" + sCrnSchId + ")로 조회중 parameter error 발생!";
	  				ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
	  				throw new DAOException(sMsg);
	  			}
			}

			// 작업재료 수만큼 루프
			for(int ii=1; ii<=rsResult.size(); ii++) {

				rsResult.absolute(ii);
				recMtl = rsResult.getRecord();

				sStlNo = ydDaoUtils.paraRecChkNull(recMtl,"STL_NO");

				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", 	sCrnSchId);
				recPara.setField("STL_NO", 			sStlNo);

				rsCrnStock = JDTORecordFactory.getInstance().createRecordSet("Temp");

				// 재료유형에 따라 공통테이블, 저장품테이블, 크레인작업내역들을 읽어온다.
				/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getYdWrkHistByPlate 

				SELECT Z.*
				  FROM
				(
				    SELECT
				           CR.YD_GP                                     AS YD_GP
				          ,ST.STL_NO                                    AS STL_NO
				          ,SUBSTR(CR.YD_SCH_CD, 7, 1)                   AS YD_GNT_GP
				          ,NULL                                         AS STL_APPEAR_GP
				          ,VW.ITEMNAME_CD                               AS ITEMNAME_CD
				          ,VW.ORD_YEOJAE_GP                             AS ORD_YEOJAE_GP
				          ,VW.ORD_NO                                    AS ORD_NO
				          ,VW.ORD_DTL                                   AS ORD_DTL
				          ,VW.SPEC_ABBSYM                               AS SPEC_ABBSYM
				          ,VW.DEMANDER_CD                               AS DEMANDER_CD
				          ,ST.STL_PROG_CD                               AS STL_PROG_CD
				          ,ST.YD_MTL_W_GP                               AS YD_MTL_W_GP
				          ,ST.YD_MTL_T_GP                               AS YD_MTL_T_GP
				          ,ST.YD_MTL_L_GP                               AS YD_MTL_L_GP
				          ,ST.YD_MTL_T                                  AS YD_MTL_T
				          ,ST.YD_MTL_W                                  AS YD_MTL_W
				          ,ST.YD_MTL_L                                  AS YD_MTL_L
				          ,ST.YD_MTL_WT                                 AS YD_MTL_WT
				          ,ST.REHEAT_SLAB_GP                            AS REHEAT_SLAB_GP
				          ,ST.COOL_DONE_GP                              AS COOL_DONE_GP
				          ,NULL                                         AS YD_PILING_CD
				          ,NULL                                         AS YD_BOOK_OUT_LOC
				          ,ST.FRTOMOVE_ORD_DATE                         AS FRTOMOVE_ORD_DATE
				          ,ST.URGENT_FRTOMOVE_WORD_GP                   AS URGENT_FRTOMOVE_WORD_GP
				          ,CR.YD_WBOOK_ID                               AS YD_WBOOK_ID
				          ,CR.YD_CRN_SCH_ID                             AS YD_CRN_SCH_ID
				          ,CR.YD_SCH_CD                                 AS YD_SCH_CD
				          ,CR.YD_SCH_ST_GP                              AS YD_SCH_ST_GP
				          ,CR.YD_SCH_REQ_GP                             AS YD_SCH_REQ_GP
				          ,CR.YD_SCH_PRIOR                              AS YD_SCH_PRIOR
				          ,TO_CHAR(CR.YD_WBOOK_DT,'YYYYMMDDHH24MISS')   AS YD_WBOOK_DT
				          ,WK.YD_AID_WRK_YN                             AS YD_AID_WRK_YN
				          ,CR.YD_TO_LOC_DCSN_MTD                        AS YD_TO_LOC_DCSN_MTD
				          ,CR.YD_TO_LOC_GUIDE                           AS YD_TO_LOC_GUIDE
				          ,TO_CHAR(CR.YD_SCH_DT,'YYYYMMDDHH24MISS')     AS YD_SCH_DT
				          ,TO_CHAR(CR.YD_WORD_DT,'YYYYMMDDHH24MISS')    AS YD_WORD_DT
				          ,CR.YD_UP_WO_LOC                              AS YD_UP_WO_LOC
				          ,CR.YD_UP_WO_LAYER                            AS YD_UP_WO_LAYER
				          ,CR.YD_UP_WR_LOC                              AS YD_UP_WR_LOC
				          ,CR.YD_UP_WR_LAYER                            AS YD_UP_WR_LAYER
				          ,CR.YD_UP_WRK_ACT_GP                          AS YD_UP_WRK_ACT_GP
				          ,TO_CHAR(CR.YD_UP_CMPL_DT,'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
				          
				          ,CR.YD_DN_WO_LOC                              AS YD_DN_WO_LOC
				          ,CR.YD_DN_WO_LAYER                            AS YD_DN_WO_LAYER

				          ,NVL((LY.YD_STK_COL_GP||LY.YD_STK_BED_NO), CR.YD_DN_WR_LOC)   AS YD_DN_WR_LOC
				          ,NVL(LY.YD_STK_LYR_NO, CR.YD_DN_WR_LAYER)                     AS YD_DN_WR_LAYER
				          ,NVL(SUBSTR(LY.YD_STK_COL_GP,1,1), CR.YD_GP)                  AS YD_DN_WR_YD_GP

				          ,CR.YD_DN_WRK_ACT_GP                          AS YD_DN_WRK_ACT_GP
				          ,TO_CHAR(CR.YD_DN_CMPL_DT,'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
				          ,CR.YD_WRK_HDS_DD                             AS YD_WRK_HDS_DD
				          ,CR.YD_WRK_DUTY                               AS YD_WRK_DUTY
				          ,CR.YD_WRK_PARTY                              AS YD_WRK_PARTY
				          ,CR.YD_EQP_ID                                 AS YD_EQP_ID
				          ,WK.YD_STK_LYR_NO                             AS YD_STK_LYR_NO
				      FROM TB_YD_CRNSCH    CR
				          ,TB_YD_CRNWRKMTL WK
				          ,TB_YD_SHRSTOCK  ST
				          ,VW_YD_SHRSTOCK  VW
				          ,TB_YD_STKLYR    LY
				     WHERE CR.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				       AND CR.YD_CRN_SCH_ID = WK.YD_CRN_SCH_ID
				       AND WK.STL_NO        = :V_STL_NO
				       AND WK.STL_NO        = ST.STL_NO
				       AND WK.STL_NO        = VW.STL_NO(+)
				       AND WK.STL_NO        = LY.STL_NO(+)
				       AND LY.YD_STK_LYR_MTL_STAT(+) = 'C'
				) Z
				WHERE Z.YD_GP = Z.YD_DN_WR_YD_GP
				*/
				intRtnVal = ydWrkHistDao.getYdWrkHistByPlate(recPara, rsCrnStock);

				if (intRtnVal <= 0) {
					if (intRtnVal == 0) {
						sRtnMsg = "재료번호(" + sStlNo + ")에 대한 데이터가 없습니다.";
						sMsg    = "[" + sOperationName + "] " + sRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						throw new DAOException(sRtnMsg);
					} else if (intRtnVal == -2) {
						sRtnMsg = "재료번호(" + sStlNo + ")로 조회중 parameter error 발생!";
						sMsg    = "[" + sOperationName + "] " + sRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
						throw new DAOException(sRtnMsg);
					} //end if
				}//end if

				rsCrnStock.absolute(1);
				recCrnStock = rsCrnStock.getRecord();

				recCrnStock.setField("REGISTER",	sModifier);
				recCrnStock.setField("MODIFIER",	sModifier);
				recCrnStock.setField("DEL_YN", 		"N");

				// 이력테이블에 INSERT
				/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.insYdWrkHist

				INSERT INTO TB_YD_WRKHIST
				(
				       YD_WRK_HIST_ID               -- 야드작업이력ID
				      ,REGISTER                     -- 등록자
				      ,REG_DDTT                     -- 등록일시
				      ,MODIFIER                     -- 수정자
				      ,MOD_DDTT                     -- 수정일시
				      ,DEL_YN                       -- 삭제유무
				      ,YD_GP                        -- 야드구분
				      ,STL_NO                       -- 재료번호
				      ,YD_GNT_GP                    -- 재료수불구분
				      ,STL_APPEAR_GP                -- 재료외형구분
				      ,ITEMNAME_CD                  -- 품명코드
				      ,ORD_YEOJAE_GP                -- 주문여재구분
				      ,ORD_NO                       -- 주문번호
				      ,ORD_DTL                      -- 주문행번
				      ,STLKIND_CD                   -- 강종코드
				      ,SPEC_ABBSYM                  -- 규격약호
				      ,ORD_GP                       -- 수주구분
				      ,STL_PROG_CD                  -- 재료진도코드
				      ,GOODS_GRADE                  -- 제품등급
				      ,YD_MTL_W_GP                  -- 야드재료폭구분
				      ,YD_MTL_T_GP                  -- 야드재료두께구분
				      ,YD_MTL_L_GP                  -- 야드재료길이구분
				      ,YD_MTL_T                     -- 야드재료두께
				      ,YD_MTL_W                     -- 야드재료폭
				      ,YD_MTL_L                     -- 야드재료길이
				      ,YD_MTL_WT                    -- 야드재료중량
				      ,YD_COIL_OUTDIA_GRP_GP        -- 야드코일외경군구분
				      ,REHEAT_SLAB_GP               -- 재열재구분
				      ,COOL_DONE_GP                 -- 냉각완료구분
				      ,YD_PILING_CD                 -- 야드Piling코드
				      ,YD_BOOK_OUT_LOC              -- 야드BookOut위치
				      ,PL_RCPT_LN_GP                -- 후판입고Line구분
				      ,FRTOMOVE_ORD_DATE            -- 이송지시일자
				      ,URGENT_FRTOMOVE_WORD_GP      -- 긴급이송작업지시구분
				      ,SPOS_WLOC_CD                 -- 발지개소코드
				      ,ARR_WLOC_CD                  -- 착지개소코드
				      ,YD_CAR_USE_GP                -- 야드차량사용구분
				      ,CAR_NO                       -- 차량번호
				      ,TRN_EQP_CD                   -- 운송장비코드
				      ,CAR_KIND                     -- 차량종류
				      ,TRANS_EQUIPMENT_TYPE         -- 운송장비Type
				      ,CARD_NO                      -- 카드번호
				      ,YD_CAR_SCH_ID                -- 야드차량스케쥴ID
				      ,YD_TCAR_SCH_ID               -- 야드대차스케쥴ID
				      ,YD_WBOOK_ID                  -- 야드작업예약ID
				      ,YD_CRN_SCH_ID                -- 야드크레인스케쥴ID
				      ,YD_SCH_CD                    -- 야드스케쥴코드
				      ,YD_SCH_ST_GP                 -- 야드스케쥴기동구분
				      ,YD_SCH_REQ_GP                -- 야드스케쥴요청구분
				      ,YD_SCH_PRIOR                 -- 야드스케쥴우선순위
				      ,YD_WBOOK_DT                  -- 야드작업예약일시
				      ,YD_AID_WRK_YN                -- 야드보조작업여부
				      ,YD_TO_LOC_DCSN_MTD           -- 야드To위치결정방법
				      ,YD_TO_LOC_GUIDE              -- 야드To위치Guide
				      ,YD_SCH_DT                    -- 야드스케쥴일시
				      ,YD_WORD_DT                   -- 야드작업지시일시
				      ,YD_UP_WO_LOC                 -- 야드권상지시위치
				      ,YD_UP_WO_LAYER               -- 야드권상지시단
				      ,YD_UP_WR_LOC                 -- 야드권상실적위치
				      ,YD_UP_WR_LAYER               -- 야드권상실적단
				      ,YD_UP_WRK_ACT_GP             -- 야드권상작업수행구분
				      ,YD_UP_CMPL_DT                -- 야드권상완료일시
				      ,YD_DN_WO_LOC                 -- 야드권하지시위치
				      ,YD_DN_WO_LAYER               -- 야드권하지시단
				      ,YD_DN_WR_LOC                 -- 야드권하실적위치
				      ,YD_DN_WR_LAYER               -- 야드권하실적단
				      ,YD_DN_WRK_ACT_GP             -- 야드권하작업수행구분
				      ,YD_DN_CMPL_DT                -- 야드권하완료일시
				      ,YD_WRK_HDS_DD                -- 야드작업계상일자
				      ,YD_WRK_DUTY                  -- 야드작업근
				      ,YD_WRK_PARTY                 -- 야드작업조
				      ,YD_CARLD_LEV_LOC             -- 야드상차출발위치
				      ,YD_CARLD_LEV_DT              -- 야드상차출발일시
				      ,YD_CARLD_PNT_WO_DT           -- 야드상차Point지시일시
				      ,YD_PNT_CD1                   -- 야드포인트코드1
				      ,YD_PNT_CD2                   -- 야드포인트코드2
				      ,YD_CARLD_WRK_BOOK_ID         -- 야드상차작업예약ID
				      ,YD_CARLD_SCH_REQ_GP          -- 야드상차스케쥴요청구분
				      ,YD_CARLD_STOP_LOC            -- 야드상차정지위치
				      ,YD_CARLD_ARR_DT              -- 야드상차도착일시
				      ,YD_CARLD_ST_DT               -- 야드상차개시일시
				      ,YD_CARLD_CMPL_DT             -- 야드상차완료일시
				      ,YD_CARLD_WRK_ACT_GP          -- 야드상차작업수행구분
				      ,YD_CARLD_CHK_DT              -- 야드상차검수일시
				      ,YD_CARUD_LEV_DT              -- 야드하차출발일시
				      ,YD_CARUD_PNT_WO_DT           -- 야드하차Point지시일시
				      ,YD_PNT_CD3                   -- 야드포인트코드3
				      ,YD_PNT_CD4                   -- 야드포인트코드4
				      ,YD_CARUD_WRK_BOOK_ID         -- 야드하차작업예약ID
				      ,YD_CARUD_STOP_LOC            -- 야드하차정지위치
				      ,YD_CARUD_SCH_REQ_GP          -- 야드하차스케쥴요청구분
				      ,YD_CARUD_ARR_DT              -- 야드하차도착일시
				      ,YD_CARUD_CHK_DT              -- 야드하차검수일시
				      ,YD_CARUD_ST_DT               -- 야드하차개시일시
				      ,YD_CARUD_CMPL_DT             -- 야드하차완료일시
				      ,YD_CARUD_WRK_ACT_GP          -- 야드하차작업수행구분
				      ,YD_TRN_WRK_DELY_CD           -- 야드운송작업지연코드
				      ,YD_EQP_ID                    -- 야드설비ID
				      ,BOOK_OUT_RESN                -- Book-Out원인
				      ,PL_BOOK_INOUT_GP             -- 후판북인아웃구분
				)
				VALUES
				(
				       :V_YD_WRK_HIST_ID                                    -- 야드작업이력ID
				      ,:V_REGISTER                                          -- 등록자
				      ,SYSDATE                                              -- 등록일시
				      ,:V_MODIFIER                                          -- 수정자
				      ,SYSDATE                                              -- 수정일시
				      ,:V_DEL_YN                                            -- 삭제유무
				      ,:V_YD_GP                                             -- 야드구분
				      ,:V_STL_NO                                            -- 재료번호
				      ,:V_YD_GNT_GP                                         -- 재료수불구분
				      ,:V_STL_APPEAR_GP                                     -- 재료외형구분
				      ,:V_ITEMNAME_CD                                       -- 품명코드
				      ,:V_ORD_YEOJAE_GP                                     -- 주문여재구분
				      ,:V_ORD_NO                                            -- 주문번호
				      ,:V_ORD_DTL                                           -- 주문행번
				      ,:V_STLKIND_CD                                        -- 강종코드
				      ,:V_SPEC_ABBSYM                                       -- 규격약호
				      ,:V_ORD_GP                                            -- 수주구분
				      ,:V_STL_PROG_CD                                       -- 재료진도코드
				      ,:V_GOODS_GRADE                                       -- 제품등급
				      ,:V_YD_MTL_W_GP                                       -- 야드재료폭구분
				      ,:V_YD_MTL_T_GP                                       -- 야드재료두께구분
				      ,:V_YD_MTL_L_GP                                       -- 야드재료길이구분
				      ,:V_YD_MTL_T                                          -- 야드재료두께
				      ,:V_YD_MTL_W                                          -- 야드재료폭
				      ,:V_YD_MTL_L                                          -- 야드재료길이
				      ,:V_YD_MTL_WT                                         -- 야드재료중량
				      ,:V_YD_COIL_OUTDIA_GRP_GP                             -- 야드코일외경군구분
				      ,:V_REHEAT_SLAB_GP                                    -- 재열재구분
				      ,:V_COOL_DONE_GP                                      -- 냉각완료구분
				      ,:V_YD_PILING_CD                                      -- 야드Piling코드
				      ,:V_YD_BOOK_OUT_LOC                                   -- 야드BookOut위치
				      ,:V_PL_RCPT_LN_GP                                     -- 후판입고Line구분
				      ,:V_FRTOMOVE_ORD_DATE                                 -- 이송지시일자
				      ,:V_URGENT_FRTOMOVE_WORD_GP                           -- 긴급이송작업지시구분
				      ,:V_SPOS_WLOC_CD                                      -- 발지개소코드
				      ,:V_ARR_WLOC_CD                                       -- 착지개소코드
				      ,:V_YD_CAR_USE_GP                                     -- 야드차량사용구분
				      ,:V_CAR_NO                                            -- 차량번호
				      ,:V_TRN_EQP_CD                                        -- 운송장비코드
				      ,:V_CAR_KIND                                          -- 차량종류
				      ,:V_TRANS_EQUIPMENT_TYPE                              -- 운송장비Type
				      ,:V_CARD_NO                                           -- 카드번호
				      ,:V_YD_CAR_SCH_ID                                     -- 야드차량스케쥴ID
				      ,:V_YD_TCAR_SCH_ID                                    -- 야드대차스케쥴ID
				      ,:V_YD_WBOOK_ID                                       -- 야드작업예약ID
				      ,:V_YD_CRN_SCH_ID                                     -- 야드크레인스케쥴ID
				      ,:V_YD_SCH_CD                                         -- 야드스케쥴코드
				      ,:V_YD_SCH_ST_GP                                      -- 야드스케쥴기동구분
				      ,:V_YD_SCH_REQ_GP                                     -- 야드스케쥴요청구분
				      ,:V_YD_SCH_PRIOR                                      -- 야드스케쥴우선순위
				      ,TO_DATE(:V_YD_WBOOK_DT,'YYYYMMDDHH24MISS')           -- 야드작업예약일시
				      ,:V_YD_AID_WRK_YN                                     -- 야드보조작업여부
				      ,:V_YD_TO_LOC_DCSN_MTD                                -- 야드To위치결정방법
				      ,:V_YD_TO_LOC_GUIDE                                   -- 야드To위치Guide
				      ,TO_DATE(:V_YD_SCH_DT,'YYYYMMDDHH24MISS')             -- 야드스케쥴일시
				      ,TO_DATE(:V_YD_WORD_DT,'YYYYMMDDHH24MISS')            -- 야드작업지시일시
				      ,:V_YD_UP_WO_LOC                                      -- 야드권상지시위치
				      ,:V_YD_UP_WO_LAYER                                    -- 야드권상지시단
				      ,:V_YD_UP_WR_LOC                                      -- 야드권상실적위치
				      ,:V_YD_UP_WR_LAYER                                    -- 야드권상실적단
				      ,:V_YD_UP_WRK_ACT_GP                                  -- 야드권상작업수행구분
				      ,TO_DATE(:V_YD_UP_CMPL_DT,'YYYYMMDDHH24MISS')         -- 야드권상완료일시
				      ,:V_YD_DN_WO_LOC                                      -- 야드권하지시위치
				      ,:V_YD_DN_WO_LAYER                                    -- 야드권하지시단
				      ,:V_YD_DN_WR_LOC                                      -- 야드권하실적위치
				      ,:V_YD_DN_WR_LAYER                                    -- 야드권하실적단
				      ,:V_YD_DN_WRK_ACT_GP                                  -- 야드권하작업수행구분
				      ,TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS')         -- 야드권하완료일시
				      ,:V_YD_WRK_HDS_DD                                     -- 야드작업계상일자
				      ,:V_YD_WRK_DUTY                                       -- 야드작업근
				      ,:V_YD_WRK_PARTY                                      -- 야드작업조
				      ,:V_YD_CARLD_LEV_LOC                                  -- 야드상차출발위치
				      ,TO_DATE(:V_YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')       -- 야드상차출발일시
				      ,TO_DATE(:V_YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')    -- 야드상차Point지시일시
				      ,:V_YD_PNT_CD1                                        -- 야드포인트코드1
				      ,:V_YD_PNT_CD2                                        -- 야드포인트코드2
				      ,:V_YD_CARLD_WRK_BOOK_ID                              -- 야드상차작업예약ID
				      ,:V_YD_CARLD_SCH_REQ_GP                               -- 야드상차스케쥴요청구분
				      ,:V_YD_CARLD_STOP_LOC                                 -- 야드상차정지위치
				      ,TO_DATE(:V_YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')       -- 야드상차도착일시
				      ,TO_DATE(:V_YD_CARLD_ST_DT,'YYYYMMDDHH24MISS')        -- 야드상차개시일시
				      ,TO_DATE(:V_YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS')      -- 야드상차완료일시
				      ,:V_YD_CARLD_WRK_ACT_GP                               -- 야드상차작업수행구분
				      ,TO_DATE(:V_YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS')       -- 야드상차검수일시
				      ,TO_DATE(:V_YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')       -- 야드하차출발일시
				      ,TO_DATE(:V_YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS')    -- 야드하차Point지시일시
				      ,:V_YD_PNT_CD3                                        -- 야드포인트코드3
				      ,:V_YD_PNT_CD4                                        -- 야드포인트코드4
				      ,:V_YD_CARUD_WRK_BOOK_ID                              -- 야드하차작업예약ID
				      ,:V_YD_CARUD_STOP_LOC                                 -- 야드하차정지위치
				      ,:V_YD_CARUD_SCH_REQ_GP                               -- 야드하차스케쥴요청구분
				      ,TO_DATE(:V_YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')       -- 야드하차도착일시
				      ,TO_DATE(:V_YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS')       -- 야드하차검수일시
				      ,TO_DATE(:V_YD_CARUD_ST_DT,'YYYYMMDDHH24MISS')        -- 야드하차개시일시
				      ,TO_DATE(:V_YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS')      -- 야드하차완료일시
				      ,:V_YD_CARUD_WRK_ACT_GP                               -- 야드하차작업수행구분
				      ,:V_YD_TRN_WRK_DELY_CD                                -- 야드운송작업지연코드
				      ,:V_YD_EQP_ID                                         -- 야드설비ID
				      ,:V_BOOK_OUT_RESN                                     -- Book-Out원인
				      ,:V_PL_BOOK_INOUT_GP                                  -- 후판북인아웃구분
				)
				*/
				intRtnVal = ydWrkHistDao.insYdWrkHist(recCrnStock);

				if (intRtnVal <= 0) {
					sMsg = "재료번호(" + sStlNo + ")에 대한 INSERT가 실패하였습니다.";
				//	ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
					throw new DAOException(sMsg);
				}

			} // end of for

		}  catch(Exception e) {
			sMsg = "Exception 발생 >>>> " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
		//	throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	// end try catch문

		sMsg = "["+sOperationName+"] --------------------- 종료";
		ydUtils.putLog(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG);
	}

    /**
	 * 오퍼레이션명 : 1후판정정야드 크레인스케줄Main
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String procCrnSchMainYdP(JDTORecord msgRecord)throws DAOException  {

		/**
		 * 업무기준 : 1. 크레인스케줄수행조건판단
		 * 			  2. 그룹핑 파라미터 셋팅
		 * 			  3. Handling Lot 편성
		 * 			  4. 크레인사양 비교
		 * 			  5. 크레인스케줄과 크레인작업재료등록
		 *
		 * 비고 	: 1. 대차작업일때 대차 상태체크하여 SKIP
		 * 			  2. 해당 스케쥴코드로 작업예약 건수를 조회하여 5건 이상시 SKIP
		 *
		 */
		JDTORecordSet 	rsResult		= null;
		JDTORecordSet 	rsWrkbookmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet 	rsCrnsch    	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord 		recTemp 		= null;
		JDTORecord 		recL2Para		= null;
		JDTORecord 		recCrnSch		= null;
		JDTORecord 		recEqp			= null;

		//DAO 선언
    	JPlateYdCrnSchDAO	ydCrnSchDao = new JPlateYdCrnSchDAO();
    	JPlateYdEqpDAO		ydEqpDao	= new JPlateYdEqpDAO();
    	JPlateYdStkLyrDAO	ydStkLyrDao	= new JPlateYdStkLyrDAO();

		//Vector 선언
		Vector 	vecResult      			= new Vector();
		Vector 	vecReResult     		= new Vector();

		String 	szMsg        			= "";
		String 	szRtnMsg 				= JPlateYdConst.RETN_CD_SUCCESS;	//리턴메세지정의
		String 	szMethodName 			= "procCrnSchMainYdP";
		String 	szOperationName 		= "1후판정정야드 크레인 스케줄Main";

		String 	szEqpId      			= "";			// 설비Id
		String 	szSchCd      			= "";			// 스케줄코드
		String	szTcarId				= "";			// 대차설비ID
		String	szYdCurrBayGp			= "";			// 대차현재동구분
		String	szYdBayGp				= "";
		String 	szYdWbookId 			= "";
		String	szYdCrnSchId			= "";			// 크레인스케쥴 ID
		String	szYdWrkProgStat			= "";
		String 	szYdWrkableCrn			= null;
		String 	szYdSchPrior			= null;
		String	szModifier				= "";
		String	szChkFromLoc			= "";
		String	szYdGp					= "";
		String  szYdToLocGuide           = "";

		int 	intRtnVal       		= 0;
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		String szRcvTcCode 				= ydUtils.getTcCode(msgRecord);

		if ("".equals(szRcvTcCode)) {
			szRtnMsg = "TC Code Error";
			szMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		if (bDebugFlag) {
			szMsg = "[" + szOperationName + "] 전문수신 : TCCODE=" +szRcvTcCode;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		}

		try {
			szMsg = "[" + szOperationName + "] 1후판정정야드 크레인스케줄Main .... \nSTART >>>>" + msgRecord.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------------------
			//	파라미터 확인
			//------------------------------------------------------------------------------------------------------------
			szEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, 	"YD_EQP_ID"											);		// 크레인설비ID
			szSchCd 	= ydDaoUtils.paraRecChkNull(msgRecord, 	"YD_SCH_CD"											);		// 크레인스케줄코드
			szYdWbookId = ydDaoUtils.paraRecChkNull(msgRecord, 	"YD_WBOOK_ID"										);		// 작업예약ID
			szChkFromLoc= ydDaoUtils.paraRecChkNull(msgRecord, 	"CHK_FROM_LOC", "Y"									);		// 권상위치에 작업예약 존재여부 체크 할지 말지 FLAG
			szYdGp		= ydDaoUtils.paraRecChkNull(msgRecord, 	"YD_GP", 		JPlateYdConst.YD_GP_P_PLATE_YARD	);		// 야드구분
			szModifier	= ydDaoUtils.paraRecModifier(msgRecord);															// 수정자
			szYdToLocGuide = ydDaoUtils.paraRecChkNull(msgRecord, 	"YD_TO_LOC_GUIDE"											);		// to위치 guide

			szMsg = "[" + szOperationName + "] 파라미터 확인 : \nYD_EQP_ID [" + szEqpId + "] \nYD_SCH_CD [" + szSchCd + "] \n작업예약ID [" + szYdWbookId + "]";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------------------
			// 스케쥴코드가 대차 작업일경우 대차의 현재동을 체크하여 SKIP (_BTC01LM , _BTC01UM)
			//------------------------------------------------------------------------------------------------------------
			if ("TC".equals(ydUtils.substr(szSchCd, 2, 2))) {

				szYdBayGp = ydUtils.substr(szSchCd, 1, 1);
				szTcarId  = JPlateYdConst.YD_GP_P_PLATE_YARD + "X" + ydUtils.substr(szSchCd, 2, 4);
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recTemp   = JDTORecordFactory.getInstance().create();
				recTemp.setField("YD_EQP_ID", szTcarId);
				
				//com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdEqp
				intRtnVal = ydEqpDao.getYdEqp(recTemp, rsResult);
				
				if (intRtnVal <= 0) {
    				szRtnMsg = "해당 대차설비 조회시 오류발생 .. 설비ID :: " + szTcarId;
    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    				return szRtnMsg;
				}
				rsResult.first();
				recEqp = rsResult.getRecord();
				szYdCurrBayGp = ydDaoUtils.paraRecChkNull(recEqp, "YD_CURR_BAY_GP");
				
				if (!szYdCurrBayGp.equals(szYdBayGp)) {
    			
					szRtnMsg = "대차의 현재동이 " + szYdCurrBayGp + "로 " + szYdBayGp + "동 크레인작업지시 생성 SKIP";
    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    				return JPlateYdConst.RETN_CD_SUCCESS;
				}

				szMsg = "[" + szOperationName + "] 대차의 현재동이 " + szYdCurrBayGp + "로 " + szYdBayGp + "동 크레인작업지시 생성 START";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}

			//------------------------------------------------------------------------------------------------------------
			// BOOK-IN, GAS장보급, 보수장보급일경우 TO 위치에 예약재료가 존재하면 SKIP
			// 1단으로 관리되는 저장위치에 대하여 체크 ... 사실상 무의미 한것 같음
			//------------------------------------------------------------------------------------------------------------
			
			
        	if ("U".equals(szSchCd.substring(6,7)) && !"PFRTAPUM".equals(szSchCd)) {  // 파일링모드의 북인처리시는 예외처리함.
        		//  가스장일때만 체크 하도록 변경 [RT 5단, 3베드로 변경됨에 따라]
           		//if ("CN".equals(szSchCd.substring(2,4))) {

	           		// TO 위치에 예약재료가 존재여부 판단	BOOK-IN, GAS장보급, 보수장보급일경우 TO 위치에 예약재료가 존재하면 SKIP
        		//	szRtnMsg = this.chkOtherSch(szSchCd, szYdWbookId);
        			
        		//	if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        		//		szRtnMsg = "해당 스케줄에 TO위치 예약되어 SKIP 처리 >>>> 스케쥴::" + szSchCd + ", 작업예약::" + szYdWbookId;
        		//		szMsg = "[" + szOperationName + "] " + szRtnMsg;
        		//		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
        		//		return szRtnMsg;
        		//	}
        		//}

           		// RT일때  적치 가능 베드/단이 미존재시 SKIP --> TO위치 XX010101 로 안나오도록 하기위함
            	if ("RT".equals(ydUtils.substr(szSchCd,2,2))) { 

            		// 작업예약 ID로 적치가능 RT조회
            		rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
            		recTemp	 = JDTORecordFactory.getInstance().create();
        			recTemp.setField("YD_WBOOK_ID", 		szYdWbookId);
        			recTemp.setField("YD_GP", 				szYdGp);
        			recTemp.setField("YD_STK_LYR_MTL_STAT", "E");
        			
            		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyLocByWBookId  //REQ202311508635 김도훈 6매로 확장요청 
            		-- 작업예약 ID의 TO위치 가이드에 해당 하는 적치가능베드 조회

            		SELECT
            		       B.YD_STK_COL_GP
            		     , B.YD_STK_BED_NO
            		     , B.YD_STK_LYR_NO
            		     , B.STL_NO
            		     , B.YD_STK_LYR_ACT_STAT
            		     , B.YD_STK_LYR_MTL_STAT
            		     , B.YD_STK_LYR_XAXIS
            		     , B.YD_STK_LYR_YAXIS
            		     , B.YD_STK_LYR_ZAXIS
            		     , B.YD_OCPY_BED_GP
            		     , B.YD_OCPY_STK_BED_NO
            		     , B.YD_OCPY_STK_LYR_NO
            		     , C.YD_TO_LOC_GUIDE
            		  FROM TB_YD_STKBED  A
            		     , TB_YD_STKLYR  B
            		     , (SELECT NVL(YD_TO_LOC_GUIDE, :V_YD_GP) AS YD_TO_LOC_GUIDE
            		          FROM TB_YD_WRKBOOK
            		         WHERE YD_WBOOK_ID   = :V_YD_WBOOK_ID
            		           AND DEL_YN        = 'N'
            		       ) C
            		 WHERE A.YD_STK_COL_GP    LIKE SUBSTR(C.YD_TO_LOC_GUIDE,1,6) || '%'
            		   AND A.YD_STK_BED_NO    LIKE SUBSTR(C.YD_TO_LOC_GUIDE,7,2) || '%'
            		   AND A.YD_STK_COL_GP       = B.YD_STK_COL_GP
            		   AND A.YD_STK_BED_NO       = B.YD_STK_BED_NO
            		   AND A.DEL_YN              = 'N'
            		   AND B.DEL_YN              = 'N'
            		   AND A.YD_STK_BED_ACT_STAT = 'L'
            		   AND B.YD_STK_LYR_ACT_STAT = 'E'
            		   AND B.YD_STK_LYR_MTL_STAT IN ('E', :V_YD_STK_LYR_MTL_STAT)
            		 ORDER BY B.YD_STK_LYR_NO, B.YD_STK_BED_NO
            		 */

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recTemp에 logId 추가 
        			recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
        			
            		intRtnVal = ydStkLyrDao.getEmptyLocByWBookId(recTemp, rsResult);
        			if (intRtnVal < 1) {
        				szRtnMsg = "RT  TO위치 적치불가로 SKIP 처리 >>>> 스케쥴::" + szSchCd + ", 작업예약::" + szYdWbookId;
        				szMsg = "[" + szOperationName + "] " + szRtnMsg;
        				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        				return szRtnMsg;
        			}
            	}

			}
        	//------------------------------------------------------------------------------------------------------------
			// 25.07.30 후판1팀 김대현 매니저 요청 --RITM1212853
			// A동 크레인 작업은 TO위치 GUIDE 적치 불가능할시, 작업예약 상태로 남김
        	// 25.08.22 결정 - To 위치 GUIDE 적치 불가시 xx로 남김.
			//------------------------------------------------------------------------------------------------------------
//        	JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
//			String sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A034"); //1후판정정야드  작업지시요구 (Y2YDL007) 시 YDY2L004V2 전송여부 
//			
//			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(Y2YDL007)---[[[ 1후판정정야드신규적용 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG, logId);
//			
//			
//        	if("Y".equals(sNEW_MODULE_EFF_YN) && "PA".equals(ydUtils.substr(szSchCd,0,2))){
//        		recTemp = JDTORecordFactory.getInstance().create();
//        		recTemp.setField("YD_TO_LOC_GUIDE", 	szYdToLocGuide);
//            	recTemp.setField("YD_SCH_CD", 			szSchCd);
//            	recTemp.setField("MODIFIER",			szModifier);
//        		
//        		szRtnMsg = JPlateYdToLocUtil.procAsgnedBedStackable(recTemp, null, szMethodName);
//        		
//        		if(!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)){
//        			szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
//    				szMsg = "[" + szOperationName + "] TO위치 Guide 적치불가로 SKIP 처리 >>>> 스케쥴::" + szSchCd + ", 작업예약::" + szYdWbookId;
//    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
//    				return szRtnMsg;
//        		}
//        	}
        	
        	

			//------------------------------------------------------------------------------------------------------------
			// 해당 작업예약ID이외의 FROM 위치에 작업 예약 존재여부 체크 (재료적치상태 == 'U','D')
        	// --> 대차 작업 때문에 상단에 작업예약 존재 체크를 파라미터 값에 따라 체크 하도록 변경
			//------------------------------------------------------------------------------------------------------------
        	if ("Y".equals(szChkFromLoc)) {
				szMsg = "[" + szOperationName + "] 해당 작업예약ID이외의 FROM 위치에 작업 예약 존재여부 체크 시작 >>>> ";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				//com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getMtlStatByFromLoc
				szRtnMsg = this.chkFromLocStat(szSchCd, szYdWbookId);
				
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					szMsg = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					return szRtnMsg;
				}
        	} else {
				szMsg = "[" + szOperationName + "] 해당 작업예약ID이외의 FROM 위치에 작업 예약 존재여부 체크 SKIP";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        	}

			//------------------------------------------------------------------------------------------------------------
			//	1후판정정야드크레인스케줄수행조건판단 모듈 호출
			//------------------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] 크레인스케줄수행조건판단 및 스케줄생성 가능한 작업예약의 재료들이 존재하는 BED정보를 조회 모듈 호출 시작 >>>> ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intRtnVal = this.chkCrnSchEffectCondition(msgRecord, rsWrkbookmtl);
			if (intRtnVal == -1) {
				szRtnMsg = "크레인스케줄수행조건판단 시 오류발생 - 반환값 : " + Integer.toString(intRtnVal);
				szMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			rsWrkbookmtl.absolute(1);
			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setRecord(rsWrkbookmtl.getRecord());
			szYdWbookId = ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
			if ("W".equals(szSchCd)) {
				szSchCd = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
			}

			szMsg = "[" + szOperationName + "] 크레인스케줄수행조건판단 및 스케줄생성[" + szSchCd + "] 가능한 작업예약[" + szYdWbookId + "]의 재료들이 존재하는 BED정보를 조회 모듈 호출 완료 - 대상재건수 : " + rsWrkbookmtl.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//-----------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 수행조건 판단 모듈 호출 - 스케줄금지유무와 주/대체크레인 교체 유무 판단
			//-----------------------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] 스케줄수행판단 모듈 호출 시작 ,, 스케쥴::" + szSchCd + ", 작업예약ID::" + szYdWbookId;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_SCH_CD", 	szSchCd);
			recTemp.setField("YD_WBOOK_ID", szYdWbookId);

			// CrnSchUtil을 정정야드 에 맞게 JPlateCrnSchUtil로 변경
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recSchPara에 logId 추가
//			  procCheckIfCrnSchRunnableForPlateGds Method 에서 다른 처리 없이 procCheckIfCrnSchRunnable Method Call 하기에 
//			  직접 procCheckIfCrnSchRunnable Method Call
			recTemp.setField("LOG_ID", 			logId );      // logId
			
//			szRtnMsg = JPlateYdCrnSchUtil.procCheckIfCrnSchRunnableForPlateGds(recTemp);
			szRtnMsg = JPlateYdCrnSchUtil.procCheckIfCrnSchRunnable(recTemp);
//-------------------------------------------------------------------------------------------------------------------------
			
			szYdWrkableCrn	= ydDaoUtils.paraRecChkNull(recTemp, "YD_WRKABLE_CRN");
			szYdSchPrior	= ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_PRIOR");
			
			if("PFRTAPLM".equals(szSchCd) || "PFRTAPUM".equals(szSchCd)){  //56020존 파일링 코드의 경우, 야드룰 지정 설비 활용함.
				szYdWrkableCrn=szEqpId;
				szYdSchPrior="1";
			}
			szMsg = "[" + szOperationName + "] 스케줄수행판단 모듈 호출 완료 - 메세지 : " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szRtnMsg = "작업예약[" + szYdWbookId + "]에 대한 스케줄을 수행할 수 없습니다.";
				szMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			//------------------------------------------------------------------------------------------------------------
			//	그룹핑 파라미터 셋팅
			//------------------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] 그룹핑 파라미터 셋팅  rsWrkbookmtl.SIZE() : " + rsWrkbookmtl.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
								
			intRtnVal = this.crnSchSort(rsWrkbookmtl, rsResult);
			if (intRtnVal == -1) {
				szRtnMsg = "그룹핑 파라미터 셋팅 : CrnSchSort";
				szMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 편성 : 주작업 보조작업구분
			//------------------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] Handling Lot 편성 .. 대상건수 : " + rsResult.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			intRtnVal = this.crnSchDataHandling(rsResult, vecResult);
			if (intRtnVal == -1) {
				szRtnMsg = "Handling Lot 편성 : CrnSchDataHandling" + vecResult.size();
				szMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			szMsg = "[" + szOperationName + "] Handling Lot 편성 .. 결과건수 : " + vecResult.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------------------
			//	동일 Handling Lot 편성 : 주작업 보조작업구분-> 스케쥴코드별 동일 Handling Lot 편성
			//------------------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] 동일Handling Lot 편성 대상 건수: " + vecResult.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 this.crnSchSameDataHandling() argument logId 추가
//-------------------------------------------------------------------------------------------------------------------------
//			intRtnVal = this.crnSchSameDataHandling(szSchCd, szYdWbookId, vecResult);
			intRtnVal = this.crnSchSameDataHandling(szSchCd, szYdWbookId, vecResult, logId);
//-------------------------------------------------------------------------------------------------------------------------
			
			if (intRtnVal == -1) {
				szRtnMsg = "vecResultHandling Lot 편성 : CrnSchSameDataHandling";
				szMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}
			szMsg = "[" + szOperationName + "] 동일Handling Lot 편성 .. 결과건수 : " + vecResult.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------------------
			//	크레인사양 Handling Lot 편성 : 스케쥴코드별 동일 Handling Lot 편성 -> 크레인사양별 편성
			//------------------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] 스펙 Handling Lot 편성 대상 건수: " + vecResult.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 this.crnSchCrnSpecHandling() argument logId 추가
//-------------------------------------------------------------------------------------------------------------------------
//			intRtnVal = this.crnSchCrnSpecHandling(szSchCd, szYdWrkableCrn, vecResult, vecReResult);
			intRtnVal = this.crnSchCrnSpecHandling(szSchCd, szYdWrkableCrn, vecResult, vecReResult, logId);
//-------------------------------------------------------------------------------------------------------------------------
			if (intRtnVal == -1) {
				szRtnMsg = "vecResultHandling Lot 편성 : CrnSchCrnSpecHandling";
				szMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			szMsg = "[" + szOperationName + "] 스펙 Handling Lot 편성완료 건수: " + vecReResult.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//-----------------------------------------------------------------------------------------------------------------
			//	파라미터로 전달된 크레인설비ID를 스케줄기준 조회 후 작업가능한 크레인설비ID로 교체
			//-----------------------------------------------------------------------------------------------------------------
			msgRecord.setField("YD_EQP_ID", 			szYdWrkableCrn);
			msgRecord.setField("YD_SCH_PRIOR", 			szYdSchPrior);

			//------------------------------------------------------------------------------------------------------------
			//	크레인스케줄과 크레인작업재료 등록
			//------------------------------------------------------------------------------------------------------------
			szMsg = "[" + szOperationName + "] 크레인스케줄 및 작업재료 등록 .. \nSTART >>>> " + msgRecord.toString();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			msgRecord.setField("YD_SCH_CD", szSchCd);
			
			msgRecord.setField("LOG_ID", logId);

			szRtnMsg = this.insCrnSch(vecReResult, msgRecord, szYdWbookId);
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szRtnMsg = "크레인스케줄 및 작업재료 등록!! 오류::" + szRtnMsg;
				szMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			szMsg = "[" + szOperationName + "] 크레인스케줄 및 작업재료 등록 .. END :: " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------------------
			//	크레인작업지시 L2전송 (1후판정정야드는 명령선택을 L2에서 함으로 작업지시 생성후 바로 전송처리 한다)
			//------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회 - 작업예약ID로 조회
			rsCrnsch  = JDTORecordFactory.getInstance().createRecordSet("");
			recTemp   = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_WBOOK_ID", szYdWbookId);
			intRtnVal = ydCrnSchDao.getByWrkId(recTemp, rsCrnsch);		// intGp = 21

			if (intRtnVal <= 0) {
				szRtnMsg = "크레인스케줄 조회 !! 오류::" + Integer.toString(intRtnVal);
				szMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return szRtnMsg;
			}

			String sNEW_MODULE_EFF_YN = "N";

			JPlateYdCommDAO effYnDao = new JPlateYdCommDAO();
			sNEW_MODULE_EFF_YN = effYnDao.getNewModuleEffYn("A010"); //1후판정정야드  작업지시요구 (Y2YDL007) 시 YDY2L004V2 전송여부 
			
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, "JPlateYdCommDAO.getNewModuleEffYn(Y2YDL007)---[[[ 1후판정정야드신규적용 : "+sNEW_MODULE_EFF_YN+" ]]]---", JPlateYdConst.DEBUG, logId);
			
			
			
			for(int ii=0; ii<rsCrnsch.size(); ii++) {
	    		rsCrnsch.absolute(ii+1);
	    		recCrnSch = rsCrnsch.getRecord();
	    		szYdCrnSchId 	= ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID");
	    		szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");

				szMsg = "[" + szOperationName + "] 크레인작업지시 L2전송 .. 대상 >>>> " + Integer.toString(ii) + " >> " + szYdCrnSchId;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    		if ("W".equals(szYdWrkProgStat)) {
					szMsg = "[" + szOperationName + "] 크레인작업지시 L2전송 .. START >>>> ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
 
					recL2Para = JDTORecordFactory.getInstance().create();
					if(sNEW_MODULE_EFF_YN.equals("Y")) {
						//신규 메소드 호출
						recL2Para.setField("MSG_ID", 		"YDY2L004V2");
					} else {
						//기존 메소드 호출
						recL2Para.setField("MSG_ID", 		"YDY2L004");
					}
					recL2Para.setField("YD_CRN_SCH_ID",    	szYdCrnSchId);
					recL2Para.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat);
					recL2Para.setField("YD_SCH_CD",        	szSchCd);
					recL2Para.setField("YD_GP",            	JPlateYdConst.YD_GP_P_PLATE_YARD);
					recL2Para.setField("MODIFIER", 			szModifier);
					recL2Para.setField("MSG_GP", 			"I");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recL2Para에 logId 추가 
					recL2Para.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					szRtnMsg = ydDelegate.sendMsg(recL2Para);

					szMsg = "[" + szOperationName + "] 크레인작업지시 L2전송 .. END >>>> " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    		} else {
					szMsg = "[" + szOperationName + "] 크레인작업지시 L2전송 .. SKIP >>>> " + szYdWrkProgStat;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    		}
			}

		} catch(Exception e) {
		//	throw new DAOException(getClass().getName() + e.getMessage(),e);
			szMsg = "[" + szOperationName + "] Exception 발생 >>>> " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			return e.getMessage();
		}	// end try catch문

		szMsg = "[" + szOperationName + "] 메소드("+szMethodName+") 끝";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
			
	} // end of procCrnSchMainYdP

    /**
     * 오퍼레이션명 :  TO 위치에 예약재료가 존재여부 판단
     * 				BOOK-IN, GAS장보급, 보수장보급일경우 TO 위치에 예약재료가 존재하면 SKIP
     * @param  ● 스케쥴코드, 작업예약ID
     * @return ● szRtnMsg
     * @throws ● DAOException
     */
    public String chkOtherSch(String pSchCd, String pYdWbookId) {

		String 	szMsg        		= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;	//리턴메세지정의
		String 	szMethodName 		= "chkOtherSch";
		String 	szOperationName		= "TO 위치에 예약재료가 존재여부 판단";
    	int		intRtnVal 			= 0;

    	JPlateYdStkLyrDAO ydStkLyrDao = new JPlateYdStkLyrDAO();

    	JDTORecordSet 	rsResult 	= null;
    	JDTORecord 		recPara  	= null;

		try {

			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			recPara	 = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID", pYdWbookId);
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getMtlStatByOtherSch 

			SELECT
			       YD_STK_COL_GP
			     , YD_STK_BED_NO
			     , YD_STK_LYR_NO
			     , STL_NO
			     , YD_STK_LYR_ACT_STAT
			     , YD_STK_LYR_MTL_STAT
			     , YD_STK_LYR_XAXIS
			     , YD_STK_LYR_YAXIS
			     , YD_STK_LYR_ZAXIS
			     , YD_OCPY_BED_GP
			     , YD_OCPY_STK_BED_NO
			     , YD_OCPY_STK_LYR_NO
			  FROM TB_YD_STKLYR
			 WHERE YD_STK_COL_GP LIKE (
			                           SELECT
			                                  CASE WHEN SUBSTR(YD_SCH_CD, 3, 2) = 'RT' THEN SUBSTR(NVL(YD_TO_LOC_GUIDE, YD_SCH_CD), 1, 6)
			                                       WHEN SUBSTR(YD_SCH_CD, 3, 2) = 'CN' THEN SUBSTR(NVL(YD_TO_LOC_GUIDE, YD_SCH_CD), 1, 6)
			--                                     WHEN SUBSTR(YD_SCH_CD, 3, 2) = 'BS' THEN SUBSTR(NVL(YD_TO_LOC_GUIDE, YD_SCH_CD), 1, 6)
			                                       ELSE 'XXXXXX'
			                                  END  YD_TO_LOC_GUIDE
			                             FROM TB_YD_WRKBOOK
			                            WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			                              AND DEL_YN = 'N'
			                              AND ROWNUM = 1
			                          )
			   AND YD_STK_LYR_MTL_STAT IN ('U', 'D')
			 ORDER BY YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO DESC
			 */
			intRtnVal = ydStkLyrDao.getMtlStatByOtherSch(recPara, rsResult);

			if (intRtnVal > 0) {
				szMsg = "["+szOperationName+"] >>>> TO위치에 예약정보가 존재합니다. ,, " + intRtnVal + "건";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_CD_FAILURE;
			}

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}//end of try~catch

    	return szRtnMsg;
    }//end of chkOtherSch

    /**
     * 오퍼레이션명 : FROM 위치에 예약재료가 존재여부 판단
     * @param  ● 스케쥴코드, 작업예약ID
     * @return ● szRtnMsg
     * @throws ● DAOException
     */
    public String chkFromLocStat(String pSchCd, String pYdWbookId) {

		String 	szMsg        		= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;	//리턴메세지정의
		String 	szMethodName 		= "chkFromLocStat";
		String 	szOperationName		= "FROM 위치에 예약재료가 존재여부 판단";
    	int		intRtnVal 			= 0;

    	JPlateYdStkLyrDAO ydStkLyrDao = new JPlateYdStkLyrDAO();

    	JDTORecordSet 	rsResult 	= null;
    	JDTORecord 		recPara  	= null;

		try {

			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			recPara	 = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID", pYdWbookId);

			intRtnVal = ydStkLyrDao.getMtlStatByFromLoc(recPara, rsResult);

			if (intRtnVal > 0) {
				szRtnMsg = "FROM위치에 예약정보가 존재합니다.";
				szMsg    = "["+szOperationName+"] >>>> " + szRtnMsg + " >>>> " + intRtnVal + "건";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return szRtnMsg;
			}

			szMsg = "["+szOperationName+"] FROM위치에 예약정보가 존재하지 않습니다. >>>> " + intRtnVal;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}//end of try~catch

    	return szRtnMsg;
    }//end of chkFromLocStat

	/**
	 * 오퍼레이션명 : 1후판정정야드 크레인스케줄수행조건판단
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szEqpId, szSchCd, rsResultRt
	 * @return intRtnVal 1: 성공, -1: 실패
	 * @throws DAOException
	 */
	public int chkCrnSchEffectCondition(JDTORecord msgRecord, JDTORecordSet rsResultRt) throws DAOException  {

		JPlateYdWrkbookDAO    ydWrkbookDao 		= new JPlateYdWrkbookDAO();
		JPlateYdWrkbookMtlDAO ydWrkbookMtlDao 	= new JPlateYdWrkbookMtlDAO();
		JPlateYdStkLyrDAO     ydStkLyrDao		= new JPlateYdStkLyrDAO();

		JDTORecordSet rsWrkbookmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsResult 		= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsSchCd 		= JDTORecordFactory.getInstance().createRecordSet("Temp");

		JDTORecord recInTemp        = null;
		JDTORecord recSchCd   		= null;
		JDTORecord recInPara 		= null;

		String 	szMsg        		= "";
		String 	szRtnMsg        	= "";									// DAOException 발생 Message Set
		String 	szMethodName 		= "chkCrnSchEffectCondition";
		String 	szOperationName 	= "1후판정정크레인스케줄수행조건판단";
		String 	szYdWbookId      	= "";
		String	szYdGp				= "";

		String 	szEqpId 			= null;
		String 	szSchCd 			= null;
		String	szModifier			= null;

		int 	intRtnVal       	= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.15 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			/*
			 * 업무기준 :
			 * 		1. 크레인스케줄코드, 작업예약ID없이 크레인설비ID만 넘어오는 경우
			 * 			-> 해당크레인설비ID로 만들어진 크레인스케줄금지가 되지 않고 크레인우선순위가 가장빠른 작업예약들 중에서
			 * 			가장빠른 작업예약을 하나 조회해서 작업 진행.
			 * 		2. 작업예약ID없이 크레인스케줄코드, 크레인설비ID만 넘어오는 경우
			 * 			-> 크레인스케줄코드로 크레인스케줄이 생성되지 않은 작업예약들 중에서 가장빠른 작업예약을 하나 조회해서 작업 진행.
			 * 		3. 크레인스케줄, 크레인설비ID, 작업예약ID가 모두 넘어오는 경우
			 * 			-> 해당작업예약ID로 직접 조회를 해서 작업진행 - 차량도착인 경우
			 */

			//-----------------------------------------------------------------------------------------------
			//	파라미터 확인
			//-----------------------------------------------------------------------------------------------
			szEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"		);
			szSchCd 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"		);
			szYdWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID"	);
			szModifier	= ydDaoUtils.paraRecModifier(msgRecord);
			szYdGp		= ydUtils.substr(szEqpId, 0, 1);

			szMsg = "[" + szOperationName + "] ----------------- 메소드시작 : 파라미터확인 : \n크레인설비ID[" + szEqpId + "] \n크레인스케줄코드[" + szSchCd + "] \n작업예약ID[" + szYdWbookId + "]";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//-----------------------------------------------------------------------------------------------

			if ("".equals(szSchCd)) {
				//-----------------------------------------------------------------------------------------------
				// 크레인설비ID로 작업예약 조회 시작
				//-----------------------------------------------------------------------------------------------
				// 해당크레인설비ID로 만들어진 크레인스케줄금지가 되지 않고 크레인우선순위가 가장빠른 작업예약들 중에서
				// 가장빠른 작업예약을 하나 조회
				rsSchCd   = JDTORecordFactory.getInstance().createRecordSet("");
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setField("YD_EQP_ID", szEqpId);
				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recInPara에 logId 추가 
				recInPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
				//com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getWorkTbRefNone
				intRtnVal = ydWrkbookDao.getWorkTbRefNone(recInPara, rsSchCd);		// intGp == 4

				if (intRtnVal<=0) {
	    			if (intRtnVal == 0) {
	    				szRtnMsg = "크레인설비ID[" + szEqpId + "]만 넘어온 경우에 작업예약 조회 시 data not found";
	    			} else if (intRtnVal == -2) {
	    				szRtnMsg = "크레인설비ID[" + szEqpId + "]만 넘어온 경우에 작업예약 조회 시 parameter error";
	    			} else {
	    				szRtnMsg = "크레인설비ID[" + szEqpId + "]만 넘어온 경우에 작업예약 조회 시 오류발생 - 반환값 : " + intRtnVal;
    				szMsg = "[" + szOperationName + "] " + szRtnMsg;
	    			}
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					throw new DAOException(szRtnMsg);
				}

				rsSchCd.absolute(1);
				recSchCd = rsSchCd.getRecord();
				szYdWbookId      = ydDaoUtils.paraRecChkNull(recSchCd, "YD_WBOOK_ID");
				//-----------------------------------------------------------------------------------------------

				//-----------------------------------------------------------------------------------------------
				// 지정된 작업예약ID로 작업예약재료들이 존재하는 BED정보와 대상재가 존재하는 해당BED의 최하단정보를 가져온다.
				//-----------------------------------------------------------------------------------------------
				rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_WBOOK_ID", szYdWbookId);
//				com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getWorkBookNone				
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recInTemp에 logId 추가 
				recInTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				intRtnVal = ydWrkbookMtlDao.getWorkBookNone(recInTemp, rsWrkbookmtl);		// intGp == 5
				
				if (intRtnVal<=0) {
	    			if (intRtnVal == 0) {
	    				szRtnMsg = "크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약ID["+szYdWbookId+"]로 작업예약재료가 존재하는 BED정보를 조회 시 data not found";
	    			} else if (intRtnVal == -2) {
	    				szRtnMsg = "크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약ID["+szYdWbookId+"]로 작업예약재료가 존재하는 BED정보를 조회 시 parameter error";
	    			} else {
	    				szRtnMsg = "크레인설비ID["+szEqpId+"]만 넘어온 경우에 작업예약ID["+szYdWbookId+"]로 작업예약재료가 존재하는 BED정보를 조회 시 오류발생 - 반환값 : " + intRtnVal;
	    			}
    				szMsg = "[" + szOperationName + "] " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					throw new DAOException(szRtnMsg);
				}

			} else {

				//-----------------------------------------------------------------------------------------------
				//	크레인설비ID, 크레인스케줄코드, 작업예약ID가 파라미터로 넘겨진 경우
				//-----------------------------------------------------------------------------------------------
				rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp = JDTORecordFactory.getInstance().create();
				if ("".equals(szYdWbookId)) {
					//-----------------------------------------------------------------------------------------------
					// 작업예약ID없는 경우 스케줄코드로 조회
					//-----------------------------------------------------------------------------------------------
					szMsg = "[" + szOperationName + "] 작업예약ID없이 스케줄코드["+szSchCd+"]로 작업예약의 작업예약재료가 존재하는 BED정보를 조회";
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					recInTemp.setField("YD_SCH_CD", szSchCd);
					//com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getWorkBookSchCd

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recInTemp에 logId 추가 
					recInTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					
					intRtnVal = ydWrkbookMtlDao.getWorkBookSchCd(recInTemp, rsWrkbookmtl);		// intGp == 6

					//-----------------------------------------------------------------------------------------------
				} else {

					//-----------------------------------------------------------------------------------------------
					// 작업예약ID로 조회
					//-----------------------------------------------------------------------------------------------
					szMsg = "[" + szOperationName + "] 명시적으로 작업예약ID["+szYdWbookId+"]으로 작업예약의 작업예약재료가 존재하는 BED정보를 조회";
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					recInTemp.setField("YD_WBOOK_ID", szYdWbookId);
					//com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getWorkBookNone
					
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recInTemp에 logId 추가 
					recInTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					
					intRtnVal = ydWrkbookMtlDao.getWorkBookNone(recInTemp, rsWrkbookmtl);		// intGp == 5
					
					//-----------------------------------------------------------------------------------------------
				}
				if (intRtnVal<=0) {
	    			if (intRtnVal == 0) {
	    				szRtnMsg = "작업예약재료가 존재하는 BED정보를 조회 시 data not found";
	    			} else if (intRtnVal == -2) {
	    				szRtnMsg = "작업예약재료가 존재하는 BED정보를 조회 시 parameter error";
	    			} else {
	    				szRtnMsg = "작업예약재료가 존재하는 BED정보를 조회 시 오류발생 - 반환값 : " + intRtnVal;
	    			}
    				szMsg = "[" + szOperationName + "] " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					throw new DAOException(szRtnMsg);
				}

			}//end of if

			szMsg = "[" + szOperationName + "] 크레인설비ID [" + szEqpId + "], 크레인스케줄코드[" + szSchCd + "], 작업예약ID[" + szYdWbookId + "]로 작업예약재료 BED정보 조회 완료 - 건수 : " + rsWrkbookmtl.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//-----------------------------------------------------------------------------------------------
			//	작업예약재료의 저장위치를 야드의 현 저장위치로 수정하기 위해서 작업예약재료 조회
			//-----------------------------------------------------------------------------------------------
			rsWrkbookmtl.first();

			recInTemp	 = rsWrkbookmtl.getRecord();

			szYdWbookId	 = ydDaoUtils.paraRecChkNull(recInTemp, "YD_WBOOK_ID");

			rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("ydTemp");
			recInTemp	 = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_WBOOK_ID", szYdWbookId);
			//com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getStockJoinByStlNo
			
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recInTemp에 logId 추가 
			recInTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			intRtnVal = ydWrkbookMtlDao.getStockJoinByStlNo(recInTemp, rsWrkbookmtl);		// intGp = 19
			
			szMsg = "[" + szOperationName + "] 현 저장위치로 작업예약의 정보를 수정하기 위해서 작업예약ID[" + szYdWbookId + "]의 작업예약재료 조회 완료 - 반환값 : " + Integer.toString(intRtnVal) + ", 건수 : " + rsWrkbookmtl.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			if (intRtnVal <= 0) {
				szRtnMsg = "작업예약ID[" + szYdWbookId + "]의 작업예약재료 조회 시 존재하지 않습니다. - 반환값 : " + Integer.toString(intRtnVal);
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				throw new DAOException(szRtnMsg);
			}

			//-----------------------------------------------------------------------------------------------

			JDTORecord recTemp		= JDTORecordFactory.getInstance().create();
			JDTORecord recOutTemp	= null;
			String     szStlNo		= null;

			//-----------------------------------------------------------------------------------------------
			//	각 작업예약재료의 저장위치를 현 저장위치로 수정
			//-----------------------------------------------------------------------------------------------

			for(int ii=1; ii<=rsWrkbookmtl.size(); ii++) {
				rsWrkbookmtl.absolute(ii);
				recInTemp 	= rsWrkbookmtl.getRecord();
				szStlNo		= ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");

				recTemp.setField("STL_NO", 					szStlNo);
				recTemp.setField("YD_STK_LYR_MTL_STAT", 	JPlateYdConst.YD_STK_LYR_MTL_STAT_STK);
				recTemp.setField("YD_GP", 					szYdGp);

				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByStlNoStat

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recTemp에 logId 추가 
				recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
				intRtnVal = ydStkLyrDao.getYdStklyrByStlNoStat(recTemp, rsResult);		// intGp == 3
				
				szMsg = "[" + szOperationName + "] 작업예약ID[" + szYdWbookId + "]의 적치중인 작업예약재료[" + szStlNo + "] 조회 완료 - 메세지 : " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				if (intRtnVal <= 0) {
					if (intRtnVal == 0) {

						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recTemp.setField("YD_STK_LYR_MTL_STAT", 	JPlateYdConst.YD_STK_LYR_MTL_STAT_UN_WAIT);

						//com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByStlNoStat

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recTemp에 logId 추가 
						recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
						
						intRtnVal = ydStkLyrDao.getYdStklyrByStlNoStat(recTemp, rsResult);		// intGp == 3
						
						szMsg = "[" + szOperationName + "] 작업예약ID[" + szYdWbookId + "]의 권상대기인 작업예약재료[" + szStlNo + "] 조회 완료 - 메세지 : " + Integer.toString(intRtnVal);
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

						if (intRtnVal <= 0) {
							szRtnMsg = "권상대기인 재료[" + szStlNo + "]를 적치단에서 조회 시 오류발생[2] - 반환값 : " + Integer.toString(intRtnVal);
		    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							throw new DAOException(szRtnMsg);
						}

					} else {
						szRtnMsg = "적치중인 재료[" + szStlNo + "]를 적치단에서 조회 시 오류발생[1] - 반환값 : " + Integer.toString(intRtnVal);
	    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						throw new DAOException(szRtnMsg);
					}
				}
				else if(intRtnVal>=2){  //2024.03.12 박종호  24.03.04 13:11에 발생한 오류 대응(작업재료에 중복insert) ORA-00001
					String sAPPPI5 =ydPICommDAO.ApplyYnPI("",szOperationName,"APPPI5","T","*");
					if(sAPPPI5.equals("Y")){
						szRtnMsg = "적치중인 재료[" + szStlNo + "]의 위치가 2개이상 조회되어 오류발생[3] - 반환값 : " + Integer.toString(intRtnVal);;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
						throw new DAOException(szRtnMsg);
					}
					else{
						szRtnMsg = "적치중인 재료[" + szStlNo + "]의 위치가 2개이상 조회됨. 별도처리skip(배포미적용) - 반환값 : " + Integer.toString(intRtnVal);;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					}
				}
				rsResult.first();
				recOutTemp 	= rsResult.getRecord();

				recInTemp.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP"));
				recInTemp.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO"));
				recInTemp.setField("YD_STK_LYR_NO", 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO"));
				recInTemp.setField("MODIFIER", 			szModifier);
				//com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdWrkbookMtl
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookMtl(recInTemp);			// intGp == 0
				
				szMsg = "[" + szOperationName + "] 작업예약ID[" + szYdWbookId + "]의 작업예약재료[" + szStlNo + "]의 현 저장위치 수정 완료 - 메세지 : " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				if (intRtnVal <= 0) {
					szRtnMsg = "작업예약[" + szYdWbookId + "]의 작업재료[" + szStlNo + "]의 위치정보를 수정 시 오류발생 - 반환값 : " + Integer.toString(intRtnVal);
					szMsg    = "[" + szOperationName + "] " + szRtnMsg;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					throw new DAOException(szRtnMsg);
				}
			}

			//-----------------------------------------------------------------------------------------------
			//	작업예약의 재료들이 존재하는 BED정보 조회
			//-----------------------------------------------------------------------------------------------
			rsWrkbookmtl = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp    = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_WBOOK_ID", szYdWbookId);

			//com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getWorkBookNone
			
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recInTemp에 logId 추가 
			recInTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			intRtnVal = ydWrkbookMtlDao.getWorkBookNone(recInTemp, rsWrkbookmtl);		// intGp == 5

			szMsg = "[" + szOperationName + "] 작업예약ID[" + szYdWbookId + "]의 작업예약재료가 존재하는 BED정보 조회 완료 - 메세지 : " + Integer.toString(intRtnVal) + ", 건수 : " + rsWrkbookmtl.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			rsResultRt.addAll(rsWrkbookmtl);
			return intRtnVal = 1;

		} catch(Exception e) {
			throw new DAOException(e.getMessage(), e);
		}	// end try catch문

	} // end of chkCrnSchEffectCondition()

	/**
     * 오퍼레이션명 : 1후판정정야드 스케줄링 크레인 스케줄 등록
     *
     * @param  ● vResult, msgRecord
     * @return ● intRtnVal
     * @throws ● DAOException
     */
    public String insCrnSch(Vector vResult, JDTORecord msgRecord, String szYD_WBOOK_ID) throws DAOException {

    	JPlateYdWrkbookMtlDAO 	ydWrkbookMtlDao = new JPlateYdWrkbookMtlDAO();
    	JPlateYdCrnSchDAO     	ydCrnschDao 	= new JPlateYdCrnSchDAO();
    	JPlateYdStkLyrDAO     	ydStklyrDao		= new JPlateYdStkLyrDAO();
    	JPlateYdCrnWrkMtlDAO    ydCrnwrkmtlDao	= new JPlateYdCrnWrkMtlDAO();

		JDTORecord    recIn       		= null;
		JDTORecord 	  recInTemp   		= null;
		JDTORecord    recInPara   		= null;

		JDTORecordSet rsResult 			= null;

		JDTORecordSet rsWrkBookMtl 		= JDTORecordFactory.getInstance().createRecordSet("Temp");

		int 	intRtnVal 		= 0;
		int 	rowcount  		= 0;
		int 	vSize	  		= 0;
		String 	szMsg 			= "";
		String 	szMethodName 	= "insCrnSch";
		String 	szOperationName = "1후판정정야드 스케줄링 크레인 스케줄 등록";
		String 	szEqpId 		= "";
		String 	szSchCd 		= "";
		String 	szWbookId 		= "";
		String 	szYdCrnSchId 	= "";
		String 	szRegister		= "";
		String	szYdGp			= "";
		String	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;

		String 	szYdSchPrior    = null;
		String 	szRegDdtt		= null;
		String 	szYdSchStGp		= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {

			szEqpId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szSchCd 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			szWbookId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
			szRegister	= ydDaoUtils.paraRecChkNull(msgRecord, "REGISTER");
			szYdGp		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD);

			if ("".equals(szRegister)) {
				szRegister	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_USER_ID");
			}
			if ("".equals(szRegister)) {
				szRegister	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			}

			//-----------------------------------------------------------------------------
			//	스케줄기준에 대한 작업크레인과 우선순위를 파라미터로 전달받아서 처리하는 것으로 변경
			//-----------------------------------------------------------------------------
			szYdSchPrior = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_PRIOR");
			//-----------------------------------------------------------------------------

			szMsg = "[" + szOperationName + "] ----------------------- 작업예약재료조회 -----------------------::" + szYD_WBOOK_ID;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//작업예약재료조회
			rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("");
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getStockByYdWbookId 

			SELECT B.STL_NO          AS STL_NO                                                                  
			      ,B.YD_WBOOK_ID     AS YD_WBOOK_ID                                                             
			      ,B.YD_STK_COL_GP   AS YD_STK_COL_GP                                                           
			      ,B.YD_STK_BED_NO   AS YD_STK_BED_NO                                                           
			      ,B.YD_STK_LYR_NO   AS YD_STK_LYR_NO                                                           
			      ,B.YD_UP_COLL_SEQ  AS YD_UP_COLL_SEQ   
			      ,TO_CHAR(B.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
			      ,A.YD_MTL_ITEM     AS YD_MTL_ITEM      
			      ,A.YD_MTL_T        AS YD_MTL_T
			      ,A.YD_MTL_W        AS YD_MTL_W
			      ,A.YD_MTL_L        AS YD_MTL_L
			      ,A.YD_MTL_WT       AS YD_MTL_WT
				  ,C.YD_TO_LOC_GUIDE AS YD_TO_LOC_GUIDE
			      ,C.YD_SCH_ST_GP    AS YD_SCH_ST_GP
			  FROM TB_YD_SHRSTOCK A                                                                               
			      ,(SELECT YD_WBOOK_ID    AS YD_WBOOK_ID                                                       
			              ,YD_STK_COL_GP  AS YD_STK_COL_GP                                                     
			              ,YD_STK_BED_NO  AS YD_STK_BED_NO                                                     
			              ,YD_STK_LYR_NO  AS YD_STK_LYR_NO                                                     
			              ,YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ                                                    
			              ,STL_NO         AS STL_NO
			              ,REG_DDTT       AS REG_DDTT
			          FROM TB_YD_WRKBOOKMTL                                                                      
			         WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			           AND DEL_YN='N'                         
			         GROUP BY YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, YD_UP_COLL_SEQ, STL_NO, REG_DDTT  
			         ORDER BY YD_UP_COLL_SEQ DESC) B,
			        TB_YD_WRKBOOK C
			 WHERE  B.STL_NO = A.STL_NO(+)
			   AND  B.YD_WBOOK_ID = C.YD_WBOOK_ID
			 ORDER  BY B.YD_STK_COL_GP ,B.YD_STK_BED_NO ,B.YD_STK_LYR_NO DESC
			 */
			intRtnVal = ydWrkbookMtlDao.getStockByYdWbookId(recInPara, rsWrkBookMtl);		// intGp = 8
			if (intRtnVal<=0) {
    			if (intRtnVal == 0) {
    				szRtnMsg = "작업예약재료조회 data not found";
    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    				return szRtnMsg;
    			} else if (intRtnVal == -2) {
    				szRtnMsg = "작업예약재료조회 parameter error";
    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    				return szRtnMsg;
    			}
    			throw new DAOException(szMsg);
			}

			rsWrkBookMtl.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsWrkBookMtl.getRecord());

			szWbookId 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_WBOOK_ID");
			szRegDdtt 	= ydDaoUtils.paraRecChkNull(recInTemp, "REG_DDTT");
			szYdSchStGp	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_ST_GP");

			szMsg = "[" + szOperationName + "] 크레인 스케줄에 Insert START >>>> " + vResult.size() + " 건";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//크레인 스케줄에 Insert 한다.
			vSize = vResult.size();
			for (int ii=0; ii<vSize; ii++) {

				//Vector 값을  가져온다.
				rsResult =(JDTORecordSet) vResult.get(ii);
				rowcount = rsResult.size();

				//크레인 스케줄 등록 마지막이 대표 정보임
				rsResult.last();
				recIn = JDTORecordFactory.getInstance().create();
				recIn.setRecord(rsResult.getRecord());

				//recIn 최초값 확인
				szMsg = "rsResult.last()값 확인!!";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				//크레인스케줄ID를 할당받는다
				szYdCrnSchId = ydCrnschDao.getSeqId();		// intGp = 9
	    		if ("".equals(szYdCrnSchId)) {
    				szRtnMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    				return szRtnMsg;
	    		}

	    		//할당받은 크레인 스케줄 아이디로 Insert
				recIn.setField("YD_CRN_SCH_ID",    			szYdCrnSchId);
				recIn.setField("YD_EQP_ID",        			szEqpId);
				recIn.setField("YD_GP",            			szYdGp);
				recIn.setField("YD_BAY_GP",        			ydUtils.substr(recIn.getFieldString("YD_STK_COL_GP"), 1, 1));
				recIn.setField("YD_SCH_CD",        			szSchCd);
				recIn.setField("REGISTER",         			szRegister);
				recIn.setField("MODIFIER",         			szRegister);
				recIn.setField("YD_CRN_GRAB_USE_RULE_ID",   recIn.getFieldString("UP_COLL_BASE"));

				//스케줄우선순위
				recIn.setField("YD_SCH_PRIOR",      		szYdSchPrior);
				recIn.setField("YD_WBOOK_DT",       		szRegDdtt);
				recIn.setField("YD_SCH_ST_GP",      		szYdSchStGp);

				szMsg = "야드To위치결정방법 :: " + recIn.getFieldString("STL_NO") + " , " + recIn.getFieldString("YD_TO_LOC_DCSN_MTD");
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				/*
				 * 	W:보조작업
				 * 	A:주작업
				 * 	M:보조작업/주작업				- 정정야드는 없음
				 * 	R:리버스주작업(모음작업)		- 정정야드는 없음
				 * 	B:베드의 최하단주작업(모음작업)	- 정정야드는 없음
				 * 	T:최종위치 주작업				- 정정야드는 없음
				 * 	S:베드의 최하단주작업(최종위치)	- 정정야드는 없음
				 */
				recIn.setField("YD_UP_WO_LOC",     recIn.getFieldString("YD_STK_COL_GP") + recIn.getFieldString("YD_STK_BED_NO"));
				recIn.setField("YD_UP_WO_LAYER",   recIn.getFieldString("YD_STK_LYR_NO"));
				if ("".equals(recIn.getFieldString("YD_UP_WO_LOC").trim())) {
    				szRtnMsg = "권상지시위치가 없습니다.";
    				szMsg    = "["+szOperationName+"] " + szRtnMsg;
    				return szRtnMsg;
				}

				recIn.setField("YD_WRK_PROG_STAT", "W");

				szMsg = "insYdCrnsch before index :: " + ii;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				recIn.setField("YD_AID_WRK_MTL_SH", Integer.toString(rowcount));
				
				/*com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.insYdCrnsch */
				intRtnVal = ydCrnschDao.insYdCrnsch(recIn);
				if (intRtnVal <= 0) {
    				szRtnMsg = "크레인 스케줄 등록중 parameter Error!! ErrorCode: " + intRtnVal;
    				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	                return szRtnMsg;
				}

				szMsg = "TO위치 결정방법이 (" + recIn.getFieldString("YD_TO_LOC_DCSN_MTD") + ") 인 경우";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				for(int jj=1; jj<=rowcount; jj++) {
					rsResult.absolute(jj);
					recIn.setRecord(rsResult.getRecord());

					szMsg = "[" + szOperationName + "] 크레인작업재료확인 .. idx :: " + Integer.toString(ii) + " , " + Integer.toString(jj);
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					//적치단의 재료상태를 권상대기로 변경
					recIn.setField("YD_STK_LYR_MTL_STAT",	"U");
					recIn.setField("MODIFIER",         		szRegister);
					/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat 

					UPDATE TB_YD_STKLYR
					   SET MODIFIER            = :V_MODIFIER
					     , MOD_DDTT            = SYSDATE
					     , STL_NO              = :V_STL_NO
					     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
					     , YD_OCPY_BED_GP      = ''
					     , YD_OCPY_STK_BED_NO  = ''
					     , YD_OCPY_STK_LYR_NO  = ''
					 WHERE YD_STK_COL_GP       = SUBSTR(:V_YD_STK_COL_GP,1,6)
					   AND YD_STK_BED_NO       = :V_YD_STK_BED_NO
					   AND YD_STK_LYR_NO       = :V_YD_STK_LYR_NO
					*/   

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recIn에 logId 추가 
					recIn.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					
			    	intRtnVal = ydStklyrDao.updYdStklyrStat(recIn);  //적치단의 재료정보 Clear
					if (intRtnVal <= 0) {
	    				szRtnMsg = "적치단 등록 실패 ..  ErrorCode: " + intRtnVal;
	    				szMsg    = "["+szOperationName+"] " + szRtnMsg;
	    				return szRtnMsg;
					}

					recIn.setField("YD_CRN_SCH_ID", 	szYdCrnSchId);
					/*
					 * 야드보조작업여부
					 * 기존의 MAIN_WRK_YN 은 주작업이 Y 보조작업이 N으로 들어옴 크레인작업재료에는 보조작업여부에 값은 보조작업인경우 Y 주작업인경우 N로 셋팅!
					 */
					if ("W".equals(recIn.getFieldString("YD_TO_LOC_DCSN_MTD"))) {
						recIn.setField("YD_AID_WRK_YN", "Y");
					} else {
						recIn.setField("YD_AID_WRK_YN", "N");
					}
					/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					//역순으로 등록
					recIn.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt("000", rowcount - jj + 1));
					recIn.setField("REGISTER", 		szRegister);
					recIn.setField("MOD_DDTT", 		"");
					if(szSchCd.startsWith("P")) {
						//1후판정정야드 스케줄이면 TB_YD_CRNWRKMTL의 YD_STK_LOT_TP 에 BED 정보를 설정한다.
						recIn.setField("YD_STK_LOT_TP", 		recIn.getFieldString("YD_STK_BED_NO")); 
					}

					//크레인작업재료insert
					/*com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.insYdCrnWrkMtl*/
					intRtnVal = ydCrnwrkmtlDao.insYdCrnWrkMtl(recIn);
					if (intRtnVal <= 0) {
	    				szRtnMsg = "크레인 스케줄 작업재료 등록 실패 ..  ErrorCode: " + intRtnVal;
	    				szMsg    = "["+szOperationName+"] " + szRtnMsg;
	    				return szRtnMsg;
					}

				}//end of in for

			}//end of out for

			szMsg = "[" + szOperationName + "] ----------------------- 저장위치결정MAIN호출 시작 -----------------------";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    	// 저장위치결정MAIN호출 TC : YDYDJ507, 설비ID, 작업예약ID
			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("MSG_ID",      		"YDYDJX52");			// 기존 :: YDYDJ507
			recIn.setField("YD_EQP_ID",   		szEqpId);
			recIn.setField("YD_WBOOK_ID", 		szWbookId);
			recIn.setField("MODIFIER", 			szRegister);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recIn에 logId 추가 
			recIn.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			szRtnMsg = this.procCrnStrLocDeciMain(recIn);

			szMsg = "[" + szOperationName + "] ----------------------- 저장위치결정MAIN호출 종료 -----------------------" + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }//end of try~catch

		szMsg = "[" + szOperationName + "] 크레인 스케줄에 Insert End";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;

    }//end of insCrnSch

    /**
	 * 1후판정정야드 저장위치등록Main
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public String procCrnStrLocDeciMain(JDTORecord inRecord)throws JDTOException  {

		//
		// 1후판정정야드 창고저장위치등록Main
		// TC :
		// 야드작업자로부터 저장위치등록수신
		//
		// 야드작업자로부터 저장위치 등록을 수신하면 저장위치를 검색하여 등록한다.
		// 크레인작업재료의 최하단 재료정보, 크레인작업재료의 총매수 중량 높이, 크레인스케줄의 야드구분 동구분 스케줄코드, 저장집합코드

		String 	szMethodName 		= "procCrnStrLocDeciMain";
		String 	szMsg 				= "";
		String 	szOperationName 	= "1후판정정야드 창고저장위치등록Main";
		String 	szWbookId 			= "";			// 작업예약Id
		String 	szEqpId 			= "";			// 설비Id
		String	szRtnMsg			= JPlateYdConst.RETN_CD_SUCCESS;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		szMsg = "[" + szOperationName + "] 시작 -------------------------------------";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		// 전문받아서 szRcvTcCode에 저장
		String 	szRcvTcCode 		= ydUtils.getTcCode(inRecord);
        if (szRcvTcCode==null) {
        	szRtnMsg = "TC Code Error(" + szRcvTcCode + ")";
			szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
        	return szRtnMsg;
        }

        if (bDebugFlag) {
			szMsg 	 = "[" + szOperationName + "] 전문수신1 : TCCODE=" +szRcvTcCode;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        }

		try {

			// 파라미터 Null Check
			szWbookId = ydDaoUtils.paraRecChkNull(inRecord, "YD_WBOOK_ID");
			szEqpId   = ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_ID");

			if ("".equals(szWbookId) || "".equals(szEqpId)) {
				szRtnMsg = "파라미터값이 잘못되었습니다.";
				szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
	        	return szRtnMsg;
			}

			// 1. 위치검색범위 조회 Data Setting
			szRtnMsg = this.setLocSrcRngData(inRecord);
			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szRtnMsg = "위치검색범위 조회 Data Setting 오류 .. 결과::" + szRtnMsg;
				szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			//	throw new JDTOException("<procCrnStrLocDeciMain> " + szMsg);
				return szRtnMsg;
			}

		} catch(Exception e) {

			szMsg = "[" + szOperationName + "] Exception발생 : ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

		//	m_ctx.setRollbackOnly();
		//	throw new JDTOException(szMsg);
			return e.getMessage();
		}

		szMsg = "[" + szOperationName + "] 종료 -------------------------------------";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return JPlateYdConst.RETN_CD_SUCCESS;
	} //end of procCrnStrLocDeciMain

    /**
     * 오퍼레이션명 : 위치검색범위 조회 DataSet
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return String msg
     * @throws
     */
    public String setLocSrcRngData(JDTORecord inRecord)throws JDTOException{

		// Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.

    	JPlateYdCrnSchDAO   	ydCrnSchDao 	= new JPlateYdCrnSchDAO();
    	JPlateYdCrnWrkMtlDAO 	ydCrnWrkMtlDao	= new JPlateYdCrnWrkMtlDAO();
    	JPlateYdWrkbookDAO  	ydWrkbookDao 	= new JPlateYdWrkbookDAO();

    	//크레인스케줄
    	JDTORecordSet rsCrnsch    	= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsCrnwrkmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");

    	//크레인작업재료
    	JDTORecord recWbook      	= null;
    	JDTORecord recCrnSch      	= null;

    	JDTORecord    recInTemp 	= null;
    	JDTORecordSet rsTemp 		= null;

    	String 	szMethodName 		= "setLocSrcRngData";
    	String 	szOperationName		= "1후판정정야드 위치검색조회";
    	String 	szMsg        		= "";

    	String 	szCrnSchId 			= "";		// 크레인스케줄id
    	String 	szSchCd    			= "";		// 스케줄코드
    	String 	szToLocDcsnMtd 		= "";		// To위치결정방법
    	String 	szToRtnMsg          = "N";
		String 	szWbookId  			= "";		// 작업예약Id
		String	szYdToLocGuide		= "";		// 작업예약의 TO위치 Guide
    	String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
    	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRecord, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
    	
    	int		intRtnVal			= 0;

        try {

			szMsg = "[" + szOperationName + "] 시작 ------------------------------------ ";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
			szWbookId = ydDaoUtils.paraRecChkNull(inRecord, "YD_WBOOK_ID");

			szMsg = "[" + szOperationName + "] 파라미터확인 ::" + szWbookId;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//-------------------------------------------------------------------------------------------------------------
			// 작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			//-------------------------------------------------------------------------------------------------------------
			rsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbook 
			-- 작업예약 조회 [조건:작업예약ID]

			SELECT A.YD_WBOOK_ID            AS YD_WBOOK_ID
			     , A.REGISTER               AS REGISTER
			     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
			     , A.MODIFIER               AS MODIFIER
			     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
			     , A.DEL_YN                 AS DEL_YN
			     , A.YD_GP                  AS YD_GP
			     , A.YD_BAY_GP              AS YD_BAY_GP
			     , A.YD_SCH_CD              AS YD_SCH_CD
			     , A.YD_SCH_PRIOR           AS YD_SCH_PRIOR
			     , A.YD_SCH_PROG_STAT       AS YD_SCH_PROG_STAT
			     , A.YD_SCH_ST_GP           AS YD_SCH_ST_GP
			     , A.YD_SCH_REQ_GP          AS YD_SCH_REQ_GP
			     , A.YD_AIM_YD_GP           AS YD_AIM_YD_GP
			     , A.YD_AIM_BAY_GP          AS YD_AIM_BAY_GP
			     , A.YD_CTS_RELAY_YN        AS YD_CTS_RELAY_YN
			     , A.YD_CTS_RELAY_BAY_GP    AS YD_CTS_RELAY_BAY_GP
			     , A.YD_TO_LOC_DCSN_MTD     AS YD_TO_LOC_DCSN_MTD
			     , A.YD_TO_LOC_GUIDE        AS YD_TO_LOC_GUIDE
			     , A.YD_WRK_PLAN_TCAR       AS YD_WRK_PLAN_TCAR
			     , A.YD_CAR_USE_GP          AS YD_CAR_USE_GP
			     , A.TRN_EQP_CD             AS TRN_EQP_CD
			     , A.CAR_NO                 AS CAR_NO
			     , A.CARD_NO                AS CARD_NO
			     , (SELECT COUNT(1)
			          FROM TB_YD_CRNSCH B
			         WHERE B.DEL_YN = 'N'
			           AND B.YD_SCH_CD = A.YD_SCH_CD
			       )                        AS REMAIN_SCH_CNT
			     , (SELECT MAX(B.YD_STK_COL_GP) FROM TB_YD_WRKBOOKMTL B
			         WHERE B.YD_WBOOK_ID = A.YD_WBOOK_ID
			       )                        AS YD_STK_COL_GP

			  FROM TB_YD_WRKBOOK A
			 WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID

			 */
			intRtnVal 	= ydWrkbookDao.getYdWrkbook(inRecord, rsTemp);		// intGp = 0
			if (intRtnVal <= 0) {
				szRtnMsg = "작업예약 조회 중 Error .. " + Integer.toString(intRtnVal);
				szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				return szRtnMsg;
			}
			rsTemp.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsTemp.getRecord());

			szYdToLocGuide = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE");

			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			rsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			//recInPara.setField("YD_EQP_ID", szEqpId);
			// 1후판정정야드는 명령선택을 차상국에서 실시한다.
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByWrkId 

			SELECT
				 YD_EQP_ID AS YD_EQP_ID
				,YD_CRN_SCH_ID AS YD_CRN_SCH_ID
				,REGISTER AS REGISTER
				,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
				,MODIFIER AS MODIFIER
				,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
				,DEL_YN AS DEL_YN
				,YD_WBOOK_ID AS YD_WBOOK_ID
				,YD_EQP_ID AS YD_EQP_ID
				,YD_GP AS YD_GP
				,YD_BAY_GP AS YD_BAY_GP
				,YD_SCH_CD AS YD_SCH_CD
				,YD_SCH_ST_GP AS YD_SCH_ST_GP
				,YD_SCH_REQ_GP AS YD_SCH_REQ_GP
				,YD_SCH_PRIOR AS YD_SCH_PRIOR
				,YD_EQP_WRK_STAT AS YD_EQP_WRK_STAT
				,YD_WRK_PROG_STAT AS YD_WRK_PROG_STAT
				,TO_CHAR(YD_WBOOK_DT, 'YYYYMMDDHH24MISS') AS YD_WBOOK_DT
				,TO_CHAR(YD_SCH_DT, 'YYYYMMDDHH24MISS') AS YD_SCH_DT
				,TO_CHAR(YD_WORD_DT, 'YYYYMMDDHH24MISS') AS YD_WORD_DT
				,TO_CHAR(YD_UP_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
				,TO_CHAR(YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
				,YD_WRK_HDS_DD AS YD_WRK_HDS_DD
				,YD_WRK_DUTY AS YD_WRK_DUTY
				,YD_WRK_PARTY AS YD_WRK_PARTY
				,YD_MAIN_WRK_MTL_SH AS YD_MAIN_WRK_MTL_SH
				,YD_AID_WRK_MTL_SH AS YD_AID_WRK_MTL_SH
				,YD_AID_WRK_UPDN_GP AS YD_AID_WRK_UPDN_GP
				,YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD
				,YD_TO_LOC_GUIDE AS YD_TO_LOC_GUIDE
				,YD_EQP_WRK_SH AS YD_EQP_WRK_SH
				,YD_EQP_WRK_WT AS YD_EQP_WRK_WT
				,YD_EQP_WRK_T AS YD_EQP_WRK_T
				,YD_EQP_WRK_MAX_W AS YD_EQP_WRK_MAX_W
				,YD_EQP_WRK_MAX_L AS YD_EQP_WRK_MAX_L
				,YD_CRN_SB_CTL_H AS YD_CRN_SB_CTL_H
				,YD_CRN_GRAB_USE_RULE_ID AS YD_CRN_GRAB_USE_RULE_ID
				,YD_UP_WO_LOC AS YD_UP_WO_LOC
				,YD_UP_WO_LAYER AS YD_UP_WO_LAYER
				,YD_UP_WO_LOC_XAXIS AS YD_UP_WO_LOC_XAXIS
				,YD_UP_WO_XAXIS_GAP_MAX AS YD_UP_WO_XAXIS_GAP_MAX
				,YD_UP_WO_XAXIS_GAP_MIN AS YD_UP_WO_XAXIS_GAP_MIN
				,YD_UP_WO_LOC_YAXIS AS YD_UP_WO_LOC_YAXIS
				,YD_UP_WO_LOC_YAXIS1 AS YD_UP_WO_LOC_YAXIS1
				,YD_UP_WO_LOC_YAXIS2 AS YD_UP_WO_LOC_YAXIS2
				,YD_UP_WO_YAXIS_GAP_MAX AS YD_UP_WO_YAXIS_GAP_MAX
				,YD_UP_WO_YAXIS_GAP_MIN AS YD_UP_WO_YAXIS_GAP_MIN
				,YD_UP_WO_LOC_ZAXIS AS YD_UP_WO_LOC_ZAXIS
				,YD_UP_WO_ZAXIS_GAP_MAX AS YD_UP_WO_ZAXIS_GAP_MAX
				,YD_UP_WO_ZAXIS_GAP_MIN AS YD_UP_WO_ZAXIS_GAP_MIN
				,YD_DN_WO_LOC AS YD_DN_WO_LOC
				,YD_DN_WO_LAYER AS YD_DN_WO_LAYER
				,YD_DN_WO_LOC_XAXIS AS YD_DN_WO_LOC_XAXIS
				,YD_DN_WO_XAXIS_GAP_MAX AS YD_DN_WO_XAXIS_GAP_MAX
				,YD_DN_WO_XAXIS_GAP_MIN AS YD_DN_WO_XAXIS_GAP_MIN
				,YD_DN_WO_LOC_YAXIS AS YD_DN_WO_LOC_YAXIS
				,YD_DN_WO_LOC_YAXIS1 AS YD_DN_WO_LOC_YAXIS1
				,YD_DN_WO_LOC_YAXIS2 AS YD_DN_WO_LOC_YAXIS2
				,YD_DN_WO_YAXIS_GAP_MAX AS YD_DN_WO_YAXIS_GAP_MAX
				,YD_DN_WO_YAXIS_GAP_MIN AS YD_DN_WO_YAXIS_GAP_MIN
				,YD_DN_WO_LOC_ZAXIS AS YD_DN_WO_LOC_ZAXIS
				,YD_DN_WO_ZAXIS_GAP_MAX AS YD_DN_WO_ZAXIS_GAP_MAX
				,YD_DN_WO_ZAXIS_GAP_MIN AS YD_DN_WO_ZAXIS_GAP_MIN
				,YD_UP_WR_LOC AS YD_UP_WR_LOC
				,YD_UP_WR_LAYER AS YD_UP_WR_LAYER
				,YD_UP_WRK_ACT_GP AS YD_UP_WRK_ACT_GP
				,YD_UP_WR_XAXIS AS YD_UP_WR_XAXIS
				,YD_UP_WR_YAXIS AS YD_UP_WR_YAXIS
				,YD_UP_WR_YAXIS1 AS YD_UP_WR_YAXIS1
				,YD_UP_WR_YAXIS2 AS YD_UP_WR_YAXIS2
				,YD_UP_WR_ZAXIS AS YD_UP_WR_ZAXIS
				,YD_DN_WR_LOC AS YD_DN_WR_LOC
				,YD_DN_WR_LAYER AS YD_DN_WR_LAYER
				,YD_DN_WRK_ACT_GP AS YD_DN_WRK_ACT_GP
				,YD_DN_WR_XAXIS AS YD_DN_WR_XAXIS
				,YD_DN_WR_YAXIS AS YD_DN_WR_YAXIS
				,YD_DN_WR_YAXIS1 AS YD_DN_WR_YAXIS1
				,YD_DN_WR_YAXIS2 AS YD_DN_WR_YAXIS2
				,YD_DN_WR_ZAXIS AS YD_DN_WR_ZAXIS
			FROM TB_YD_CRNSCH
			WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			    AND DEL_YN='N'
			ORDER BY YD_CRN_SCH_ID
			*/
			intRtnVal = ydCrnSchDao.getByWrkId(recInPara, rsCrnsch);		// intGp = 21

			szMsg = "[" + szOperationName  +"] 크레인스케줄의 권하지시위치 결정 .. START :: 건수 :: " + rsCrnsch.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
		    for(int ii=1; ii<=rsCrnsch.size(); ii++) {

        		rsCrnsch.absolute(ii);
        		recCrnSch  = rsCrnsch.getRecord();

        		//크레인스케줄Data저장
        		szCrnSchId     = recCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = recCrnSch.getFieldString("YD_SCH_CD");
        		szToLocDcsnMtd = recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");

        		boolean isPillingCode=false;
        		if("PFRTAPLM".equals(szSchCd) || "PFRTAPUM".equals(szSchCd))
        		{
        			isPillingCode=true;
        		}
        		
        		szMsg = "[" + szOperationName + "] [" + ii +"]번째 크레인 스케줄[" + szCrnSchId + "]에 대한 권하지시위치 결정 :: " + szToLocDcsnMtd;
        		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        		//크레인작업재료조회(쿼리등록 완료 : 수정요청 항목이 추가됨)
				rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		JDTORecord recInData = JDTORecordFactory.getInstance().create();

        		recInData.setField("YD_CRN_SCH_ID", szCrnSchId);
        		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtlBySchId 

        		SELECT A.STL_NO                                              AS STL_NO
        		     , A.YD_STK_LYR_NO                                       AS YD_STK_LYR_NO
        		     , A.YD_CRN_SCH_ID                                       AS YD_CRN_SCH_ID
        		     , A.REGISTER                                            AS REGISTER
        		     , A.REG_DDTT                                            AS REG_DDTT
        		     , A.MOD_DDTT                                            AS MOD_DDTT
        		     , A.MODIFIER                                            AS MODIFIER
        		     , A.DEL_YN                                              AS DEL_YN
        		     , A.YD_AID_WRK_YN                                       AS YD_AID_WRK_YN
        		     , A.HCR_GP                                              AS HCR_GP
        		     , A.STL_PROG_CD                                         AS STL_PROG_CD
        		     , A.YD_ROUTE_GP                                         AS YD_ROUTE_GP
        		     , B.YD_MTL_W                                            AS YD_MTL_W
        		     , B.YD_MTL_WT                                           AS YD_MTL_WT
        		     , B.YD_MTL_T                                            AS YD_MTL_T
        		     , B.YD_MTL_L                                            AS YD_MTL_L
        		     , B.YD_MTL_ITEM                                         AS YD_MTL_ITEM
        		     , B.YD_WBOOK_ID                                         AS YD_WBOOK_ID
        		     , C.YD_SCH_CD                                           AS YD_SCH_CD
        		     , SUM(B.YD_MTL_WT) OVER (ORDER BY A.YD_STK_LYR_NO DESC) AS SUM_MTL_WT
        		     , SUM(B.YD_MTL_T)  OVER (ORDER BY A.YD_STK_LYR_NO DESC) AS SUM_MTL_T
        		     , SUM(B.YD_MTL_L)  OVER (ORDER BY A.YD_STK_LYR_NO DESC) AS SUM_MTL_L
        		     , MAX(B.YD_MTL_W)  OVER (ORDER BY A.YD_STK_LYR_NO DESC) AS MAX_MTL_W
        		     , MAX(B.YD_MTL_L)  OVER (ORDER BY A.YD_STK_LYR_NO DESC) AS MAX_MTL_L
        		     , COUNT(A.STL_NO)  OVER (ORDER BY A.YD_STK_LYR_NO DESC) AS SH_CNT
        		  FROM TB_YD_CRNWRKMTL A
        		     , TB_YD_SHRSTOCK  B
        		     , TB_YD_CRNSCH    C
        		 WHERE A.STL_NO = B.STL_NO
        		   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
        		   AND A.YD_CRN_SCH_ID = C.YD_CRN_SCH_ID
        		   AND (A.DEL_YN IS NULL OR A.DEL_YN <> 'Y')
        		   AND (B.DEL_YN IS NULL OR B.DEL_YN <> 'Y')
        		   AND (C.DEL_YN IS NULL OR C.DEL_YN <> 'Y')
        		 ORDER BY A.YD_STK_LYR_NO
        		 */

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recInData에 logId 추가 
        		recInData.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
        		
        		intRtnVal = ydCrnWrkMtlDao.getYdCrnWrkMtlBySchId(recInData, rsCrnwrkmtl);		// intGp = 6
        		if (intRtnVal <= 0) {
    				szRtnMsg = "위치검색범위 조회 Data Setting 실패!! .. " + Integer.toString(intRtnVal);
    				szMsg 	 = "[" + szOperationName + "] " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    				return szRtnMsg;
        		}
        		
        		recWbook	= JDTORecordFactory.getInstance().create();
				recWbook.setRecord(recInTemp);
				
				//-----------------------------------------------------------------------------------------------
    			//	차량이 정지한 적치열 조회 ==> TO위치가 됨
    			//-----------------------------------------------------------------------------------------------
				String szYD_CAR_USE_GP		= ydDaoUtils.paraRecChkNull(recWbook, "YD_CAR_USE_GP");					//차량사용구분
        		

        		// 보조작업인 경우
            	if ("W".equals(szToLocDcsnMtd)) {

            		//-------------------------------------------------------------------------------------------------------------
            		//	보조작업인 경우 TO위치 결정
            		//-------------------------------------------------------------------------------------------------------------
            		szMsg = "[" + szOperationName + "] [" + ii +"]번째 크레인 스케줄[" + szCrnSchId + "]은 보조작업 스케줄의  To위치 결정 시작 .. " + inRecord.toString();
            		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        			recWbook = JDTORecordFactory.getInstance().create();
        			recWbook.setRecord(recInTemp);

        			//-------------------------------------------------------------------------------------------------------------
        			//	보조작업인 경우에는 비록 작업예약에 TO위치가이드가 등록되어 있어도 삭제를 하고 파라미터를 넘겨야 함.
        			//	==> 보조작업 TO위치결정 시 TO위치가이드를 SKIP시키기 위해서는 파라미터로 그냥 넘겨야 함.
        			//-------------------------------------------------------------------------------------------------------------
        			recWbook.setField("YD_TO_LOC_GUIDE", "");		// TO위치가이드 Clear

        			szRtnMsg = JPlateYdToLocUtil.procAidWrkToLocForPlateYdYdP(inRecord, rsCrnwrkmtl, recCrnSch, recWbook);

    				if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
    					szMsg = "[" + szOperationName + "] [" + ii +"]번째 크레인 스케줄[" + szCrnSchId + "]은 보조작업 스케줄의  To위치 결정 성공";
    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    				} else {
    					szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "]은 보조작업 스케줄의  To위치 결정 실패!!";
    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
    				}

    				szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "]은 보조작업 스케줄의 To위치 결정 완료 - 메세지 : " + szRtnMsg;
    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

            	} else {

            		//-------------------------------------------------------------------------------------------------------------
            		//	주작업인 경우 TO위치 결정                                                         (스케쥴코드가 Book-In인지 체크)
            		//-------------------------------------------------------------------------------------------------------------
            		//if (("RT".equals(szSchCd.substring(2,4)) && ydUtils.isBookInSchCd(szSchCd) && !isPillingCode) || ("RT".equals(ydUtils.substr(szYdToLocGuide,2,2))&& !isPillingCode)) {
            		if (("RT".equals(szSchCd.substring(2,4)) && ydUtils.isBookInSchCd(szSchCd) ) || ("RT".equals(ydUtils.substr(szYdToLocGuide,2,2)))) {
                		//if ("RT".equals(szSchCd.substring(2,4)) && ydUtils.isBookInSchCd(szSchCd)) {
            			//-----------------------------------------------------------------------------------------------
        				//	RT BOOK-IN - RT상으로 TO위치 결정 모듈 호출
        				//-----------------------------------------------------------------------------------------------
            			szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "] : RT상 To위치 결정 시작 :: " + szYdToLocGuide;
            			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    				recCrnSch.setField("YD_TO_LOC_GUIDE", szYdToLocGuide);		// TO위치가이드 Set

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.02 recCrnSch에 logId 추가 
	    				recCrnSch.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

	    				if("BS".equals(szSchCd.substring(2,4)) && "LM".equals(szSchCd.substring(6,8))) {
	    					// 보수장 추출이면서 TO위치가이드가 RT 인 경우.. 
	    					szRtnMsg = JPlateYdToLocUtil.procRtToLocForPlateYdYdP2(rsCrnwrkmtl, recCrnSch);
	    				} else if("CN".equals(szSchCd.substring(2,4)) && "LM".equals(szSchCd.substring(6,8))) {
	    					// 가스장 추출이면서 TO위치가이드가 RT 인 경우..
		    				szRtnMsg = JPlateYdToLocUtil.procRtToLocForPlateYdYdP2(rsCrnwrkmtl, recCrnSch);
	    				} else {
	    					szRtnMsg = JPlateYdToLocUtil.procRtToLocForPlateYdYdP(rsCrnwrkmtl, recCrnSch);
	    				}

        				if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        					szMsg = "[" + szOperationName + "] [" + ii +"]번째 크레인 스케줄[" + szCrnSchId + "] : RT상 To위치 결정 성공";
        					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        				} else {
        					szMsg = "[" + szOperationName + "] [" + ii +"]번째 크레인 스케줄[" + szCrnSchId + "]: RT상  To위치 결정 실패!!";
        					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
        				}

        				szMsg = "[" + szOperationName + "] [" + ii +"]번째 크레인 스케줄[" + szCrnSchId + "] : RT상 To위치 결정 완료 - 메세지 : " + szRtnMsg;
        				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        			}//니켈강 출하 추가 
            		else if( "PFPT01UM".equals(szSchCd) && szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) ){
            			//-----------------------------------------------------------------------------------------------
            			//	차량출고 
            			//-----------------------------------------------------------------------------------------------
            			
            			szRtnMsg = "";
            			String szYD_STK_COL_GP	= "";
            			String szYD_STK_BED_NO	= "";
            			
            			szMsg = "["+szOperationName+"] [" + ii+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 주작업[TO위치결정방법-"+szToLocDcsnMtd+"] To위치 결정 시작";
            			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    				
	    				recWbook	= JDTORecordFactory.getInstance().create();
	    				recWbook.setRecord(recInTemp);
	    				
	    				//-----------------------------------------------------------------------------------------------
            			//	차량이 정지한 적치열 조회 ==> TO위치가 됨
            			//-----------------------------------------------------------------------------------------------
	    				//String szYD_CAR_USE_GP		= ydDaoUtils.paraRecChkNull(recWbook, "YD_CAR_USE_GP");					//차량사용구분
	    				String szTRN_EQP_CD			= ydDaoUtils.paraRecChkNull(recWbook, "TRN_EQP_CD");					//운송장비코드
	    				String szCAR_NO				= ydDaoUtils.paraRecChkNull(recWbook, "CAR_NO");						//차량번호
	    				String szCARD_NO			= ydDaoUtils.paraRecChkNull(recWbook, "CARD_NO");						//카드번호
	    				
	    				szMsg = "["+szOperationName+"] [" + ii+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량상차작업예약["+szWbookId+"]의 차량정보[차량사용구분:"+szYD_CAR_USE_GP+", 운송장비코드:"+szTRN_EQP_CD+", 차량번호:"+szCAR_NO+", 카드번호:"+szCARD_NO+"]에 대한 적치베드 조회 시작";
	    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    				
	    				JDTORecordSet	rsBed		= JDTORecordFactory.getInstance().createRecordSet("");;
	    				recInPara = JDTORecordFactory.getInstance().create();
	    				
	    				if( szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) ) {		//출하차량
	    					
	    					recInPara.setField("YD_CAR_USE_GP", 	szYD_CAR_USE_GP);
		    				recInPara.setField("CAR_NO", 			szCAR_NO);
		    				recInPara.setField("CARD_NO", 			szCARD_NO);
		    				
//PIDEV_S :병행가동용:PI_YD
		    				recInPara.setField("PI_YD",  szSchCd.substring(0,1));			    				
		    				szRtnMsg = DaoManager.getYdStkbed(recInPara, rsBed, 20);
		    				
	    				}
	    				
	    				
	    				szMsg = "["+szOperationName+"] [" + ii+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량상차작업예약["+szWbookId+"]의 차량정보[차량사용구분:"+szYD_CAR_USE_GP+", 운송장비코드:"+szTRN_EQP_CD+", 차량번호:"+szCAR_NO+", 카드번호:"+szCARD_NO+"]에 대한 적치베드 조회 완료 - 메세지 : " + szRtnMsg;
	    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    				
	    				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    					
	    					rsBed.first();
	    					
	    					recInPara = rsBed.getRecord();
	    					
	    					szYD_STK_COL_GP		= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_COL_GP");			//차량정지위치 적치열
	    					szYD_STK_BED_NO		= ydDaoUtils.paraRecChkNull(recInPara, "YD_STK_BED_NO");			//차량정지위치 적치베드
	    					
	    					szMsg = "["+szOperationName+"] [" + ii+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량상차작업예약["+szWbookId+"]의 차량정보[차량사용구분:"+szYD_CAR_USE_GP+", 운송장비코드:"+szTRN_EQP_CD+", 차량번호:"+szCAR_NO+", 카드번호:"+szCARD_NO+"]에 대한 조회된 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]를 TO위치가이드로 사용";
	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    					
		    				recInTemp.setField("YD_TO_LOC_GUIDE", szYD_STK_COL_GP + szYD_STK_BED_NO);
	    					
	    				}else{
	    					
	    					szMsg = "["+szOperationName+"] [" + ii+"]번째 크레인 스케줄[" + szCrnSchId + "] : 차량상차작업예약["+szWbookId+"]의 차량정보[차량사용구분:"+szYD_CAR_USE_GP+", 운송장비코드:"+szTRN_EQP_CD+", 차량번호:"+szCAR_NO+", 카드번호:"+szCARD_NO+"]에 대한 조회된 적치열이 없으므로 TO위치가이드없이 처리";
	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		    				
		    				recInTemp.setField("YD_TO_LOC_GUIDE", "");
	    				}
	    				
	    				szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 주작업[TO위치결정방법-" + szToLocDcsnMtd + "] To위치 결정 시작";
        				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        				szRtnMsg = JPlateYdToLocUtil.procMainWrkToLocForPlateYdYdP(inRecord, rsCrnwrkmtl, recCrnSch, recInTemp);

        				szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 주작업[TO위치결정방법-" + szToLocDcsnMtd + "] To위치 결정 완료 - 메세지 : " + szRtnMsg;
        				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        				if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        					szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 주작업[TO위치결정방법-" + szToLocDcsnMtd + "] To위치 결정 성공";
        					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        				} else {
        					if (JPlateYdConst.RETN_BIG_NOT_EXIST_BED.equals(szRtnMsg)) {
        						szToRtnMsg = "Y";   // TO 위치를 XXYY0101로 SET
        					}
        					szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "] : 차량출고 주작업[TO위치결정방법-" + szToLocDcsnMtd + "] To위치 결정 실패!!";
        					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
        				}
	    				
	    				
        			} else {

        				//-----------------------------------------------------------------------------------------------
        				//	주작업 TO위치 결정 모듈 호출
        				//	1. RT입고, TF입고
        				//	2. 차량입고
        				//	3. 이적
        				//-----------------------------------------------------------------------------------------------
        				szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업[TO위치결정방법-" + szToLocDcsnMtd + "] To위치 결정 시작";
        				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        				szRtnMsg = JPlateYdToLocUtil.procMainWrkToLocForPlateYdYdP(inRecord, rsCrnwrkmtl, recCrnSch, recInTemp);

        				szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업[TO위치결정방법-" + szToLocDcsnMtd + "] To위치 결정 완료 - 메세지 : " + szRtnMsg;
        				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        				if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
        					szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업[TO위치결정방법-" + szToLocDcsnMtd + "] To위치 결정 성공";
        					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
        				} else {
        					if (JPlateYdConst.RETN_BIG_NOT_EXIST_BED.equals(szRtnMsg)) {
        						szToRtnMsg = "Y";   // TO 위치를 XXYY0101로 SET
        					}
        					szMsg = "[" + szOperationName + "] [" + ii + "]번째 크레인 스케줄[" + szCrnSchId + "] : 주작업[TO위치결정방법-" + szToLocDcsnMtd + "] To위치 결정 실패!!";
        					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
        				}
        			}
            	}
        	}//end of for

			szMsg = "[" + szOperationName + "] 크레인스케줄의 권하지시위치 결정 .. END";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        	//-------------------------------------------------------------------------------------------------------------
        	//String strDnWoLoc = "";
        	//-------------------------------------------------------------------------------------------------------------
    		// To위치 결정 실패시 default값으로 xx010101을 설정
		    // 2010.07.21 YJK 향후 제거해야 함.
        	//-------------------------------------------------------------------------------------------------------------
        	rsCrnsch 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
    		recInPara 	= JDTORecordFactory.getInstance().create();
    		recInPara.setField("YD_WBOOK_ID", szWbookId);
    		//recInPara.setField("YD_EQP_ID",   szEqpId);	// 1후판정정야드는 명령선택을 차상국에서 수행함으로 설비조건 제거
    		intRtnVal = ydCrnSchDao.getByWrkId(recInPara, rsCrnsch);		// intGp = 21

    		for(int ii=1; ii<=rsCrnsch.size(); ii++) {
				rsCrnsch.absolute(ii);
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setRecord(rsCrnsch.getRecord());
				
			   boolean isPillingCode=false;
			   if ("PFRTAPLM".equals(ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD")) || "PFRTAPUM".equals(ydDaoUtils.paraRecChkNull(recInPara, "YD_SCH_CD"))) {
				   isPillingCode=true;
			   }
				
				if ("".equals(ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LOC")) ||isPillingCode) {

					if ("Y".equals(szToRtnMsg)) {
						recInPara.setField("YD_DN_WO_LOC", "XXYY0101");
					} else {
						recInPara.setField("YD_DN_WO_LOC", "XX010101");
					}
					
					if(isPillingCode){  //크레인 파일링 모드시, TO위치를 FROM위치로 지정. 
						recInPara.setField("YD_DN_WO_LOC", ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_WO_LOC"));
						recInPara.setField("YD_DN_WO_LAYER", ydDaoUtils.paraRecChkNull(recInPara, "YD_UP_WO_LAYER"));
						
						//파일링모드시, 권상/권하위치가 동일한 관계로 to위치 결정 수행 중, 실패발생하고 바로 return되어, 총 작업매수, 총 중량등의 값을 셋팅하지 못하고 빠져나옴.
						//위의 관계로, 작업지시 생성시, 작업매수, 총중량등의 정보 없이 생성됨. 이를 보완하기 위해, 파일링시 해당 정보를 별도 셋팅해준다.
						//매수 셋팅이 안되면, 권상실적(Y2YDL008 수신 후, TY3ABC보낼때, 적치단을 제대로 못읽어온다. com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtl2
						
						JDTORecord	recParaTmp	= null;
						rsCrnwrkmtl.first();
						recParaTmp = rsCrnwrkmtl.getRecord();
						
						int    	intYdEqpWrkSh		= 0;						//야드설비작업매수
						int    	intYdEqpWrkWt		= 0;						//야드설비작업중량
						double 	dblYdEqpWrkT		= 0;						//야드설비작업총두께
						int		intYdEqpWrkL		= 0;						//야드설비작업총길이
						
						intYdEqpWrkSh   = ydDaoUtils.paraRecChkNullInt(recParaTmp, 		"SH_CNT"	);
						intYdEqpWrkWt   = ydDaoUtils.paraRecChkNullInt(recParaTmp, 		"SUM_MTL_WT");
						dblYdEqpWrkT    = ydDaoUtils.paraRecChkNullDouble(recParaTmp, 	"SUM_MTL_T"	);
						intYdEqpWrkL    = ydDaoUtils.paraRecChkNullInt(recParaTmp, 		"MAX_MTL_L"	);	
						
						recInPara.setField("YD_EQP_WRK_SH", 		String.valueOf(intYdEqpWrkSh)	);
						recInPara.setField("YD_EQP_WRK_WT", 		String.valueOf(intYdEqpWrkWt)	);
						recInPara.setField("YD_EQP_WRK_T", 			String.valueOf(dblYdEqpWrkT)	);
						recInPara.setField("YD_EQP_WRK_L", 			String.valueOf(intYdEqpWrkL)	);
						
					}

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 recInPara에 logId 추가 
					recInPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

					szRtnMsg = JPlateYdCrnSchUtil.uptCrnSchXYCordYdP(recInPara);
					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
	    				szMsg = "updDnWoInfo 크레인스케줄 To위치 Default값 등록 실패!!";
	    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
					}

					//트렌젝션 분리
	    	        EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
	    	        ejbConn.trx("updToLocHist", new Class[] { JDTORecord.class }, new Object[] { recInPara });

				}
				//strDnWoLoc = ydDaoUtils.paraRecChkNull(recInPara, "YD_DN_WO_LOC");
			}

			//-------------------------------------------------------------------------------------------------------------
    		//크레인작업지시 호출
        	//-------------------------------------------------------------------------------------------------------------
    		szMsg = "[" + szOperationName + "] 크레인작업지시 호출 .. START";
    		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			intRtnVal = this.chkCrnWrkOrdReq(inRecord, szSchCd);

    		szMsg = "[" + szOperationName + "] 크레인작업지시 호출 .. END :: " + Integer.toString(intRtnVal);
    		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        } catch(Exception e) {
        	szMsg = "[" + szOperationName + "] Exception발생 : ";
        	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		//	throw new JDTOException(szMsg);
			return e.getMessage();
    	}//end of try~catch

		szMsg = "[" + szOperationName + "] 종료 ------------------------------------ ";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

        return JPlateYdConst.RETN_CD_SUCCESS;

    } // end of setLocSrcRngData

	/**
	 * 1후판정정야드 TO위치  실패 LOG
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updToLocHist(JDTORecord inDto) throws DAOException {

		JPlateYdWrkHistDAO ydWrkHistDao = new JPlateYdWrkHistDAO();

		String 	szMsg        = "";
		String 	szMethodName = "updToLocHist";
		int    	intRtnVal    = 0;

		JDTORecord recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();

		try {

			recPara.setField("YD_SCH_CD",     	ydDaoUtils.paraRecChkNull(inDto, "YD_SCH_CD"));
			recPara.setField("YD_CRN_SCH_ID",   ydDaoUtils.paraRecChkNull(inDto, "YD_CRN_SCH_ID"));
			recPara.setField("YD_EQP_ID",    	ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID"));
			recPara.setField("YD_GP",   		ydDaoUtils.paraRecChkNull(inDto, "YD_GP", JPlateYdConst.YD_GP_P_PLATE_YARD));
			recPara.setField("CD_CONTENTS",   	"to위치 실패");
			recPara.setField("REGISTER",   		ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID", "YARDSYSTEM"));

	        intRtnVal = ydWrkHistDao.insYdCrnTolocHist(recPara);		// intGp = 5

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
			} // end of if

			outRecord.setField("RTN_CD", "1");
			return outRecord;

		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
	}	// end of updStrlocModMgt

    /**
     * 오퍼레이션명 : 크레인작업지시요구 판단
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● JDTOException
     */
    public int chkCrnWrkOrdReq(JDTORecord msgRecord,String szSchCd) throws JDTOException {

    	JPlateYdEqpDAO ydEqpDao = new JPlateYdEqpDAO();

    	JDTORecordSet rsResult 	= null;
    	JDTORecord recOutTemp  	= null;

    	int intRtnVal 			= 0 ;

    	String szMsg        	= "";
    	String szMethodName 	= "chkCrnWrkOrdReq";
    	String szOperationName  = "크레인작업지시요구 판단";

    	String szYdEqpStat 		= "";

		try {
			//설비Table조회
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal 	= ydEqpDao.getYdEqp(msgRecord, rsResult);

			recOutTemp 	= JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord(0));

			szYdEqpStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");

			//------------------------------------------------------------------------------------------
        	// 2012.07.09 윤재광----------
        	// 야드 작업 진행 상태가 'P'인경우
        	// : 크레인 파일링 권상상태를 표시한다.
        	//   - 'W'로 초기화시 비상입고스케쥴에서 다른 작업지시를 받지 못하도록 하기위해 처리
        	//------------------------------------------------------------------------------------------
        	if ("W".equals(szYdEqpStat)|| "P".equals(szYdEqpStat)) {

			} else {

				szMsg = "크레인설비의 상태가 Idle가 아닙니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}
		} catch(Exception e) {
			szMsg = "["+szOperationName+"] Exception발생 : ";
		//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new JDTOException(szMsg);
        } //end of try~catch

		intRtnVal = 1;
		return intRtnVal;

    }// end of chkCrnWrkOrdReq

    /**
     * 오퍼레이션명 : 1후판정정야드 스케줄링 Handling Data 크레인사양Check
     *
     * @param  ● szEqpId, vecHandledData, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int chkHandledDataCrnSpec(String szEqpId, Vector vecHandledData, Vector vecResult) throws JDTOException {

		JDTORecord    recPara    = null;
		JDTORecord    recCrnSpec = null;
		JDTORecordSet rsPara 	 = null;
		JDTORecordSet rsMain 	 = null;
		JDTORecordSet rsResult   = null;

		String szMsg             = "";
		String szMethodName      = "chkHandledDataCrnSpec";
		String szOperationName   = "1후판정정야드 스케줄링 Handling Data 크레인사양Check";

		int intRtnVal = 0;

		boolean blnRtnVal 		= false;

		double 	dblMaxWidth  	= 0;			// 최대 폭
		double 	dblCurrWidth 	= 0;			// 현재 폭
		double 	dblMaxThick  	= 0;			// 최대 두께
		double 	dblCurrThick 	= 0;			// 현재 두께
		long   	lngSumWt     	= 0;			// 중량의 합
		long   	lngCurrWt    	= 0;			// 현재 중량
		int    	intMtlSh      	= 0;			// 재료매수
		int    	intCrnWrkableSh	= 0;			// 크레인작업가능매수

		try {
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			//크레인 사양 Select
			blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
			if (!blnRtnVal) {
				szMsg = "크레인 사양 Select";
				throw new JDTOException("<chkHandledDataCrnSpec> " + szMsg);
			}

			rsResult.absolute(1);
			recCrnSpec = JDTORecordFactory.getInstance().create();
			recCrnSpec.setRecord(rsResult.getRecord());

			//크레인 사양과 비교 Check
			szMsg = "vecHandledData.size(): " + vecHandledData.size();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    		for(int ii=0; ii<vecHandledData.size(); ii++) {
    			//폭,중량,매수 초기화
    			dblCurrWidth = 0;
    			dblMaxWidth = 0;
    			dblMaxThick = 0;
    			lngCurrWt = 0;
    			lngSumWt = 0;
    			intMtlSh = 0;

    			rsPara =(JDTORecordSet)vecHandledData.get(ii) ;
    			rsPara.first();
    			rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
    			//새그룹 생성
    			vecResult.add(rsMain);

    			for(int jj = 0; jj < rsPara.size(); jj++) {
    				rsPara.absolute(jj+1);
    				//rsParac의 레코드를 읽어온다.
    				recPara = rsPara.getRecord();

    				//재료의 현재 폭
    				dblCurrWidth = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");
    				//재료의 현재 두께
    				dblCurrThick = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_T");

    				//최대 폭
    				if (dblCurrWidth > dblMaxWidth) {
    					dblMaxWidth = dblCurrWidth;
    				}

    				if (dblCurrThick > dblMaxThick) {
    					dblMaxThick = dblCurrThick;
    				}

    				//재료의 현재 중량
    				lngCurrWt = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
    				//누적중량
    				lngSumWt = lngSumWt + lngCurrWt;
    				//현재 재료 매수
    				intMtlSh++;

    				intCrnWrkableSh = JPlateYdGdsUtil.getCrnWrkableShBasedOnWT(dblMaxThick, dblMaxWidth);

    				//기존그룹 추가
    				if (intMtlSh <= intCrnWrkableSh) {

    					recPara.setField("SPEC_OVER", "N");
    					rsMain.addRecord(recPara);

    					szMsg = "기존그룹 LoopJ : " + jj + "intRtnVal : " + intRtnVal;
        				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    				//새그룹 생성
    				} else {

    					rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
    					recPara.setField("SPEC_OVER", "Y");

    					rsMain.addRecord(recPara);
    					vecResult.add(rsMain);
    					//누적중량에 현재중량 대입
    					lngSumWt = lngCurrWt;
    					//최대폭에 = 현재 폭 대입
    					dblMaxWidth = dblCurrWidth;
    					dblMaxThick = dblCurrThick;

    					intMtlSh = 1;

        				szMsg = "새그룹 LoopJ : " + jj + "intRtnVal : " + intRtnVal;
        				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    				}

    			}//end of infor

    		}//end of outfor

			return intRtnVal = 1;

		} catch(Exception e) {
			szMsg = "["+szOperationName+"] Exception발생 : ";
		//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new JDTOException(szMsg);
		}
    }//end of chkHandledDataCrnSpec

	/**
	 * 오퍼레이션명 : 크레인사양 유무체크 및 조회결과 데이터 반환
	 *
	 * @param  szEqpId, rsResult
	 * @return blnRtnVal       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCrnSpec(String szEqpId, JDTORecordSet rsResult)throws JDTOException {

		JPlateYdCrnSpecDAO ydCrnSpecDao = new JPlateYdCrnSpecDAO();		//크레인사양 DAO

		boolean blnRtnVal     	= false;

		int intRtnVal         	= 0;

		String szMethodName   	= "chkGetCrnSpec";
		String szOperationName  = "1후판정정야드 크레인사양 유무체크 및 조회결과 데이터 반환";
		String szMsg          	= null;

		//레코드 선언
		JDTORecord recPara      = null;

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//크레인 설비ID
			recPara.setField("YD_EQP_ID", szEqpId);

			//크레인사양 조회
			intRtnVal = ydCrnSpecDao.getYdCrnSpec(recPara, rsResult);		// intGp = 0

			//리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 중복되었습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 parameter error 발생!";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 오류 발생!";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e) {
			szMsg = "["+szOperationName+"] Exception발생 : ";
		//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new JDTOException(szMsg);
		}
		return blnRtnVal;
	} //end of chkGetCrnSpec

	/**
	 * 오퍼레이션명 : 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int crnSchSort(JDTORecordSet rsMinWrkBookMtl, JDTORecordSet rsReturn)throws JDTOException  {

		JPlateYdWrkbookMtlDAO ydWrkbookMtlDao = new JPlateYdWrkbookMtlDAO();
		JPlateYdStkLyrDAO     ydStkLyrDao     = new JPlateYdStkLyrDAO();

		JDTORecord recInPara     	= null;
		//적치Bed정보
		JDTORecord recPara       	= null;
		//적치단정보
		JDTORecord recStkLyr     	= null;

		JDTORecordSet rsResult 	 	= null;

		//작업예약재료
		JDTORecordSet rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");

		//결과 레코드셋
		JDTORecordSet rsCrnSchResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

		String 	szMsg			= "";
		String	szRtnMsg		= "";
		String 	szMethodName	= "crnSchSort";
		String 	szOperationName	= "1후판정정야드 크레인 그룹핑 파라미터설정/정렬";

		String 	szColGp  		= "";
		String 	szLyrNo   		= "";
		String 	szMtlStat 		= "";

		String 	szWbookId 		= "";

		int 	intRtnVal 		= 0;
		int 	intHandlingCnt 	= 1;

		try {
			//------------------------------------------------------------------------------------------------------------
			//	작업예약재료 조회
			//------------------------------------------------------------------------------------------------------------
			rsMinWrkBookMtl.absolute(1);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(rsMinWrkBookMtl.getRecord());
			szWbookId = recPara.getFieldString("YD_WBOOK_ID");

			rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("temp");
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getByWBookId 
			-- 작업예약 조회 [적치열로 GROUP]

			SELECT YD_WBOOK_ID          AS YD_WBOOK_ID
			     , YD_STK_COL_GP        AS YD_STK_COL_GP

			     , MIN(YD_STK_BED_NO)   AS YD_STK_BED_NO
			     , MIN(YD_STK_LYR_NO)   AS YD_STK_LYR_NO
			     
			  FROM TB_YD_WRKBOOKMTL
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN      = 'N'
			 GROUP BY YD_WBOOK_ID, YD_STK_COL_GP
			 ORDER BY YD_WBOOK_ID, YD_STK_COL_GP

			*/ 
			intRtnVal = ydWrkbookMtlDao.getByWBookId(recPara, rsWrkBookMtl);		// intGp = 7
			if (intRtnVal<=0) {
    			if (intRtnVal == 0) {
    				szMsg = "[" +szOperationName + "] getYdWrkbookmtl : data not found";
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			} else if (intRtnVal == -2) {
    				szMsg = "[" +szOperationName + "] getYdWrkbookmtl : parameter error";
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
    			}
    			throw new JDTOException(szMsg);
			} else {
    			szMsg = "[" +szOperationName + "] 작업예약 .... 조회 결과 .... 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			//------------------------------------------------------------------------------------------------------------
			//	Bed별로 작업예약재료를 조회해서 받는다. - 각 재료의 레코드에 주작업/보조작업, TO위치결정방법 파라미터 설정
			//------------------------------------------------------------------------------------------------------------
			for(int ii = 1; ii <= rsWrkBookMtl.size(); ii++) {
				rsWrkBookMtl.absolute(ii);
				//적치Bed를 조회한다.
				recPara = rsWrkBookMtl.getRecord();
				//현재 적치중인 것만 받는다.
				szColGp   = recPara.getFieldString("YD_STK_COL_GP");
				szLyrNo   = recPara.getFieldString("YD_STK_LYR_NO");
				szMtlStat = "C";

				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setField("YD_STK_LYR_NO1"		, szLyrNo);
				recInPara.setField("YD_STK_LYR_NO2"		, szLyrNo);
				recInPara.setField("YD_WBOOK_ID"		, szWbookId);
				recInPara.setField("YD_STK_COL_GP"		, szColGp);
				recInPara.setField("YD_STK_LYR_MTL_STAT", szMtlStat);

	    		rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getByWBookIdEtc 
	    		-- 작업예약 ID로 상단의 재료 정보를 조회 (주작업+보조작업)

	    		WITH TEMP_TABLE AS (

	    		    SELECT C.*
	    		      FROM
	    		    (
	    		        SELECT A.STL_NO                 AS STL_NO
	    		             , A.YD_STK_COL_GP          AS YD_STK_COL_GP
	    		             , A.YD_STK_BED_NO          AS YD_STK_BED_NO
	    		             , A.YD_STK_LYR_NO          AS YD_STK_LYR_NO
	    		             , A.YD_STK_LYR_MTL_STAT    AS YD_STK_LYR_MTL_STAT
	    		             , B.YD_STK_BED_L_MAX       AS YD_STK_BED_L_MAX
	    		             , C.YD_WBOOK_ID            AS YD_WBOOK_ID
	    		             , CASE WHEN C.YD_WBOOK_ID = :V_YD_WBOOK_ID THEN 'Y'
	    		                    WHEN C.YD_WBOOK_ID IS NULL AND A.YD_STK_LYR_NO > :V_YD_STK_LYR_NO1 THEN 'Y'
	    		                    ELSE 'N'
	    		               END                      AS OK_FLAG
	    		          FROM TB_YD_STKLYR     A
	    		             , TB_YD_STKBED     B
	    		             , TB_YD_WRKBOOKMTL C
	    		         WHERE A.YD_STK_COL_GP  = B.YD_STK_COL_GP
	    		           AND A.YD_STK_BED_NO  = B.YD_STK_BED_NO
	    		           AND A.YD_STK_COL_GP  = :V_YD_STK_COL_GP
	    		           AND A.YD_STK_LYR_NO >= :V_YD_STK_LYR_NO2
	    		           AND A.DEL_YN         = 'N'
	    		           AND B.DEL_YN         = 'N'
	    		           AND A.STL_NO IS NOT NULL
	    		--         AND A.YD_STK_LYR_MTL_STAT IN ('C', 'U')
	    		           AND A.YD_STK_LYR_MTL_STAT IN ('C')
	    		           AND A.STL_NO         = C.STL_NO(+)
	    		           AND C.DEL_YN(+)      = 'N'
	    		    ) C
	    		    WHERE C.OK_FLAG = 'Y'
	    		)
	    		SELECT X.STL_NO                     AS STL_NO
	    		     , X.YD_STK_COL_GP              AS YD_STK_COL_GP
	    		     , X.YD_STK_BED_NO              AS YD_STK_BED_NO
	    		     , X.YD_STK_LYR_NO              AS YD_STK_LYR_NO
	    		     , X.YD_STK_LYR_MTL_STAT        AS YD_STK_LYR_MTL_STAT
	    		     , X.YD_WBOOK_ID                AS YD_WBOOK_ID
	    		     , Y.YD_MTL_T                   AS YD_MTL_T
	    		     , Y.YD_MTL_W                   AS YD_MTL_W
	    		     , Y.YD_MTL_L                   AS YD_MTL_L
	    		     , CASE WHEN Y.YD_MTL_L < X.YD_STK_BED_L_MAX     THEN 'S'
	    		            WHEN Y.YD_MTL_L < (X.YD_STK_BED_L_MAX*2) THEN 'M'
	    		            ELSE 'L'
	    		       END                          AS YD_MTL_L_GP
	    		     , Y.YD_MTL_WT                  AS YD_MTL_WT
	    		     , Y.YD_MTL_ITEM                AS YD_MTL_ITEM
	    		     , Y.STL_PROG_CD                AS STL_PROG_CD
	    		     , Z.YD_STK_COL_BED_L_TP        AS YD_STK_COL_BED_L_TP

	    		  FROM TEMP_TABLE     X
	    		     , TB_YD_SHRSTOCK Y
	    		     , TB_YD_STKCOL   Z

	    		 WHERE X.STL_NO        = Y.STL_NO
	    		   AND X.YD_STK_COL_GP = Z.YD_STK_COL_GP
	    		   AND Y.DEL_YN        = 'N'

	    		 ORDER BY X.YD_STK_COL_GP, X.YD_STK_LYR_NO DESC, X.YD_STK_BED_NO
	    		 */
	    		 
	    		
	    		
	    		intRtnVal = ydStkLyrDao.getByWBookIdEtc(recInPara, rsResult);			// intGp  15
	    		if (intRtnVal <= 0) {
	    			if (intRtnVal == 0) {
	    				szRtnMsg = "Bed별로 작업예약재료 데이타 미존재";
	    			} else if (intRtnVal == -2) {
	    				szRtnMsg = "Bed별로 작업예약재료 조회 .. 파라미터 오류";
	    			}
	    			szMsg = "[" +szOperationName + "] " + szRtnMsg;
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
	    			throw new DAOException(szRtnMsg);
	    		} else {
	    			szMsg = "[" +szOperationName + "] Bed별로 작업예약재료 조회 결과 .... 건수 :: " + Integer.toString(intRtnVal);
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
	    		}

	    		for(int jj = 1; jj <= rsResult.size(); jj++) {
	    			rsResult.absolute(jj);
	    			recStkLyr = rsResult.getRecord();

	    			recStkLyr.setField("BED_CNT", 		Integer.toString(ii));
	    			recStkLyr.setField("HANDLING_CNT", 	Integer.toString(intHandlingCnt));

		    		intHandlingCnt++;		//핸들링 카운트 증가

	    			//주작업여부판단
	    			if (!"".equals(ydDaoUtils.paraRecChkNull(recStkLyr,"YD_WBOOK_ID"))) {
	    				// 주작업
	    				recStkLyr.setField("MAIN_WRK_YN"		, "Y");
	    				recStkLyr.setField("YD_TO_LOC_DCSN_MTD"	, "A");
	    			} else {
	    				// 보조작업
	    				recStkLyr.setField("MAIN_WRK_YN"		, "N");
	    				recStkLyr.setField("YD_TO_LOC_DCSN_MTD"	, "W");
	    			}

	    			// 보조작업도 주작업의 작업예약ID로 SET 한다 ..
		    		recStkLyr.setField("YD_WBOOK_ID", 	recPara.getFieldString("YD_WBOOK_ID"));

	    			rsCrnSchResult.addRecord(recStkLyr);

	    		}//end of for

			}//end of for

			szMsg = "=======================================  정렬하기전   ===========================================";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for(int ii = 1; ii <= rsCrnSchResult.size(); ii++) {
				rsCrnSchResult.absolute(ii);
				JDTORecord recCurrt = JDTORecordFactory.getInstance().create();
				recCurrt = rsCrnSchResult.getRecord();
				szMsg = recCurrt.toString();
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}
			szMsg = "==============================================================================================";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------------------
			//	레코드셋 정렬	- Handling Count순으로 정렬
			//------------------------------------------------------------------------------------------------------------
			JDTORecord recAfter = null;
			JDTORecord recCurrt = null;

			for(int ii = 1; ii < rsCrnSchResult.size(); ii++) {

				for(int jj = ii + 1; jj < rsCrnSchResult.size() + 1; jj++) {

					rsCrnSchResult.absolute(ii);
					recCurrt = rsCrnSchResult.getRecord();

					rsCrnSchResult.absolute(jj);
					recAfter = rsCrnSchResult.getRecord();
					//
					if (recCurrt.getFieldInt("HANDLING_CNT") > recAfter.getFieldInt("HANDLING_CNT")) {

						rsCrnSchResult = this.sortRs(ii, jj, rsCrnSchResult);
						if (intRtnVal == -1) {
							szMsg = "<rsSort> 정렬 중 Error ";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							throw new DAOException("<crnSchSort> " + szMsg);
						}
					}
				}//end of infor
			}//end of outfor

			//------------------------------------------------------------------------------------------------------------

			szMsg = "=======================================  정렬하기후   ===========================================";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for(int ii=1; ii<=rsCrnSchResult.size(); ii++) {
				rsCrnSchResult.absolute(ii);
				recCurrt = rsCrnSchResult.getRecord();
				rsReturn.addRecord(recCurrt);

				szMsg = recCurrt.toString();
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}
			szMsg = "==============================================================================================";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
		//	szMsg = "["+szOperationName+"] Exception발생 : ";
		//	ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			throw new DAOException(e.getMessage());
		}
		return intRtnVal = 1;
	} //end of crnSchSort

	/**
     * 오퍼레이션명 : 레코드 치환
     *
     * @param  ● iLoopI, iLoopJ, rsCrnSchResult
     * @return ● JDTORecordSet
     * @throws ● JDTOException
     */
    public JDTORecordSet sortRs(int iLoopI, int iLoopJ, JDTORecordSet rsCrnSchResult)throws DAOException {

    	JDTORecord 		recTemp = null;
    	JDTORecordSet 	rsTemp 	= null;

		try {
			rsTemp = JDTORecordFactory.getInstance().createRecordSet("Temp");

			for(int ii=1; ii<=rsCrnSchResult.size(); ii++) {
				if (ii == iLoopI) {
					rsCrnSchResult.absolute(iLoopJ);
					recTemp = rsCrnSchResult.getRecord();
				} else if (ii == iLoopJ) {
					rsCrnSchResult.absolute(iLoopI);
					recTemp = rsCrnSchResult.getRecord();
				} else {
					rsCrnSchResult.absolute(ii);
					recTemp = rsCrnSchResult.getRecord();
				}
				rsTemp.addRecord(recTemp);
			}

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }//end of try~catch

		return rsTemp;
    }//end of sortRs()

	/**
     * 오퍼레이션명 : 1후판정정야드 스케줄링 동일Handling Data Check 
     *
     * @param  ● msgRecSet, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int crnSchSameDataHandling(String szSchCd, String szWbookid, Vector vecResult, String logId) throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.11.18 로그 개선  START
// crnSchSameDataHandling argument 에 logId 항목 추가 개선
//  public int crnSchSameDataHandling(String szSchCd, String szWbookid, Vector vecResult) throws JDTOException {
// 2024.11.18 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////


    	JPlateYdWrkbookDAO ydWrkbookDao = new JPlateYdWrkbookDAO();
    	JDTORecord recPara       	= null;

		String szMsg             	= "";
		String szMethodName      	= "crnSchSameDataHandling";
		String szOperationName      = "1후판정정야드 크레인동일Handling Lot편성";

		int intRtnVal 				= 0;

		JDTORecord    inRecord 		= null;
		JDTORecord    outRecord 	= null;

		JDTORecordSet rsParaSet	 	= null;
		JDTORecordSet rsMain 	 	= null;
		JDTORecordSet outResult   	= null;

		String sYdSchCd        		= "";
		String sYdPilingCd     		= "";
		String sBigCustCd         	= "";
		String sBigCustYn         	= "N";

		String sBefYdPilingCd  		= "";
		String sMainWrkYn         	= "";
		String sBefBigCustCd     	= "";
		String sStlNo              	= "";
		String sYdToLocDcsnMtd  	= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		Vector vecInVector      	= new Vector();

		try {

			szMsg = "[" + szOperationName + "] 메소드 시작  - 대상재 Vector 건수[" + vecResult.size() + "]";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			ydUtils.printParam(szMsg, vecResult);
			//가상벡터에 등록
			vecInVector.addAll(vecResult);

			// 결과 return 벡터 CLEAR
			vecResult.clear();

			sYdSchCd 	= szSchCd;

			if ("".equals(sYdSchCd)) {
				sYdSchCd = "ZZZZZZZZ";
			}

			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 편성
			//------------------------------------------------------------------------------------------------------------
			// 작업예약을 조회한다.
			inRecord  	= JDTORecordFactory.getInstance().create();
			inRecord.setField("YD_WBOOK_ID", szWbookid);

			outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
			outRecord  	= JDTORecordFactory.getInstance().create();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 inRecord에 logId 추가 
			inRecord.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, outResult);		// intGp = 0
			if (intRtnVal > 0) {
				outResult.first();
				outRecord = outResult.getRecord();
				// 고객사 지정 여부
				sBigCustYn = outRecord.getFieldString("YD_CTS_RELAY_YN");
				sYdToLocDcsnMtd = outRecord.getFieldString("YD_TO_LOC_DCSN_MTD");
			}
			if ("".equals(sBigCustYn)) {
				sBigCustYn = "N";
			}

			szMsg = "[" + szOperationName + "] szWbookid : " + szWbookid + "고객사 지정 : " + sBigCustYn + " sYdToLocDcsnMtd :" + sYdToLocDcsnMtd;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//입고작업일 경우
			if ("L".equals(sYdSchCd.substring(6, 7))) {

				for(int ii = 0; ii < vecInVector.size(); ii++) {
	    			rsParaSet =(JDTORecordSet)vecInVector.get(ii) ;
	    			rsParaSet.first();
	    			rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    			//새그룹 생성
	    			vecResult.add(rsMain);

	    			szMsg = "[" + szOperationName + "] VECTOR 변경으로 인한  새그룹VECTOR("+ ii+ ")생성 ";
	    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    			for(int jj = 0; jj < rsParaSet.size(); jj++) {
	    				rsParaSet.absolute(jj+1);
	    				//rsParac의 레코드를 읽어온다.
	    				recPara = rsParaSet.getRecord();

	    				sYdPilingCd 	= ydDaoUtils.paraRecChkNull(recPara, "YD_PILING_CD");
	    				sStlNo 			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

	    				if (jj == 0) {
	    					rsMain.addRecord(recPara);
	    					szMsg = "[" + szOperationName + "]  새그룹VECTOR("+ ii+ ")에 추가"+ " 제품번호 : " + sStlNo;
	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

    					} else {
    						if (sYdPilingCd.equals(sBefYdPilingCd)) {
    	    					rsMain.addRecord(recPara);

    	    					szMsg = "[" + szOperationName + "] 동일 파일링 VECTOR 기존그룹 추가 적치단 : " + jj + " 제품번호 : " + sStlNo;
    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    						} else {

    							rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	    					rsMain.addRecord(recPara);
    	    					vecResult.add(rsMain);

    	    					szMsg = "[" + szOperationName + "]("+ ii+ ")VECTOR 분할  새그룹 생성  적치단 : " + jj + " 제품번호 : " + sStlNo;
    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
    						}
    					}
        				sBefYdPilingCd = sYdPilingCd;
	    			}
	    		}

				//------------------------------------------------------------------------------------------------------------
				//	1.입고작업
				//  1.1 1번 RECORD 추가
				//  1.2 2번RECORD 의 파일링 코드와 1번RECORD 파일링 코드 비교
				//     1.2.1 파일링코드가  동일하면 기존그룹에 추가
				//     1.2.2 파일링코드가 틀리면 새그룹 추가
				//
				//	2.이적작업
				//  2.1 1번 RECORD 추가
				//  2.2 주작업이면
				//    2.2.1 고객사 지정 이면 2번RECORD 의 파일링 코드와 1번RECORD 파일링 코드 비교
				//         2.2.1.1 파일링코드가  동일하면 기존그룹에 추가
				//         2.2.1.2 파일링코드가 틀리면 새그룹 추가
				//    2.2.2 고객사 지정이 아니면
				//         2.2.2.1  기존그룹에 추가
				//  2.3 주작업이 아니면
				//    2.3.1 -1 RECORD의  대형고객사유무와 현재 대형고객사유무가 틀리면 새그룹 추가
				//    2.3.2 -1 RECORD의  대형고객사유무와 현재 대형고객사유무가 동일하면
				//           2.3.2.1  고객사 지정 이면 2번RECORD 의 파일링 코드와 1번RECORD 파일링 코드 비교
				//				 2.3.2.1.1 파일링코드가  동일하면 기존그룹에 추가
				//				 2.3.2.1.2 파일링코드가 틀리면 새그룹 추가
				//           2.3.2.2 고객사 지정이 아니면
				//           2.2.2.2.1  기존그룹에 추가
				//------------------------------------------------------------------------------------------------------------

			//이적작업일 경우
			} else if ("M".equals(sYdSchCd.substring(6, 7))) {

				for(int ii = 0; ii < vecInVector.size(); ii++) {
	    			rsParaSet =(JDTORecordSet)vecInVector.get(ii) ;
	    			rsParaSet.first();
	    			rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    			//새그룹 생성
	    			vecResult.add(rsMain);

	    			szMsg = "[" + szOperationName + "] VECTOR 변경으로 인한  새그룹VECTOR("+ ii+ ")생성 ";
	    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    			for(int jj = 0; jj < rsParaSet.size(); jj++) {
	    				rsParaSet.absolute(jj+1);
	    				//rsParac의 레코드를 읽어온다.
	    				recPara = rsParaSet.getRecord();

	    				sYdPilingCd 	= ydDaoUtils.paraRecChkNull(recPara, "YD_PILING_CD");
	    				sBigCustCd 		= ydDaoUtils.paraRecChkNull(recPara, "BIG_CUST_CD");

	    				if ("".equals(sBigCustCd)) {
	    					sBigCustCd = "N";
	    				}
	    				sStlNo 		= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");


	    				szMsg = "[" + szOperationName + "] sYdPilingCd : " + sYdPilingCd + " sBigCustCd : "  + sBigCustCd
	    					  + " sBigCustYn : "  + sBigCustYn+ " //주작업 보조작업 구분 : " + ydDaoUtils.paraRecChkNull(recPara, "MAIN_WRK_YN");
	    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    				if (jj == 0) {
	    					sMainWrkYn 	= ydDaoUtils.paraRecChkNull(recPara, "MAIN_WRK_YN");	//주작업 보조작업 구분
	    					rsMain.addRecord(recPara);

	    					szMsg = "[" + szOperationName + "]  새그룹VECTOR("+ ii+ ")에 추가"+ " 제품번호 : " + sStlNo;
	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        				sBefYdPilingCd	= sYdPilingCd;
	        				sBefBigCustCd 	= sBigCustCd;
	    				} else {

	    					if ("Y".equals(sMainWrkYn)) {			//주작업

	    						if ("Y".equals(sBigCustYn)) {       //고객사 지정
	    							 //TO위치 지정일 경우  고객사 지정 무시함
	    							if (!"F".equals(sYdToLocDcsnMtd)) {          //TO위치 지정일 경우
			    						if (sYdPilingCd.equals(sBefYdPilingCd)) {
			    	    					rsMain.addRecord(recPara);

			    	    					szMsg = "[" + szOperationName + "] VECTOR 기존그룹 추가  적치단  : " + jj + " 제품번호 : " + sStlNo;
			    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    						} else {
			    							rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	    					rsMain.addRecord(recPara);
			    	    					vecResult.add(rsMain);

			    	    					szMsg = "[" + szOperationName + "]("+ ii+ ")VECTOR 분할  새그룹 생성  적치단 : " + jj + " 제품번호 : " + sStlNo;
			    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			    						}
	    							} else{
		    	    					rsMain.addRecord(recPara);

		    	    					szMsg = "[" + szOperationName + "] VECTOR 기존그룹 추가  적치단  : " + jj + " 제품번호 : " + sStlNo;
		    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    							}
	    						} else {
	    	    					rsMain.addRecord(recPara);

	    	    					szMsg = "[" + szOperationName + "] VECTOR 기존그룹 추가  적치단  : " + jj + " 제품번호 : " + sStlNo;
	    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    						}

	    					} else {								//보조작업
	    						// 대형고객사와 일반고객사 분리
	    						if (sBigCustCd.equals(sBefBigCustCd)) {			// 대형고객사
	    							if ("Y".equals(sBigCustCd)) {					// 고객사 지정여부
	    								if (sYdPilingCd.equals(sBefYdPilingCd)) {
	    	    	    					rsMain.addRecord(recPara);

	    	    	    					szMsg = "[" + szOperationName + "] VECTOR 기존그룹 추가  적치단  : " + jj + " 제품번호 : " + sStlNo;
	    	    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    								} else {
	    									rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    	    					rsMain.addRecord(recPara);
	    	    	    					vecResult.add(rsMain);

	    	    	    					szMsg = "[" + szOperationName + "]("+ ii+ ")VECTOR 분할  새그룹 생성  적치단 : " + jj + " 제품번호 : " + sStlNo;
	    	    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    								}
	    							} else {
	    	    	    					rsMain.addRecord(recPara);

	    	    	    					szMsg = "[" + szOperationName + "] VECTOR 기존그룹 추가 적치단  : " + jj + " 제품번호 : " + sStlNo;
	    	    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    							}
	    						} else {

	    							rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    					rsMain.addRecord(recPara);
	    	    					vecResult.add(rsMain);

	    	    					szMsg = "[" + szOperationName + "]("+ ii+ ")VECTOR 분할  새그룹 생성  적치단 : " + jj + " 제품번호 : " + sStlNo;
	    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    						}
	    					}
    						sBefYdPilingCd	= sYdPilingCd;
    						sBefBigCustCd	= sBigCustCd;
		    			}
	    			}
				}
			//반납/이송/출하
			} else {

				for(int ii = 0; ii < vecInVector.size(); ii++) {
	    			rsParaSet =(JDTORecordSet)vecInVector.get(ii) ;
	    			rsParaSet.first();
	    			rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    			//새그룹 생성
	    			vecResult.add(rsMain);

	    			szMsg = "[" + szOperationName + "] VECTOR 변경으로 인한  새그룹VECTOR("+ ii+ ")생성 ";
	    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);


	    			for(int jj = 0; jj < rsParaSet.size(); jj++) {
	    				rsParaSet.absolute(jj+1);
	    				//rsParac의 레코드를 읽어온다.
	    				recPara = rsParaSet.getRecord();

	    				sYdPilingCd 	= ydDaoUtils.paraRecChkNull(recPara, "YD_PILING_CD");
	    				sBigCustCd 		= ydDaoUtils.paraRecChkNull(recPara, "BIG_CUST_CD");
	    				if ("".equals(sBigCustCd)) {
	    					sBigCustCd = "N";
	    				}
	    				sStlNo 		= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

	    				szMsg = "[" + szOperationName + "] sYdPilingCd : " + sYdPilingCd + " sBigCustCd : "  + sBigCustCd+
	    				" sBigCustYn : "  + sBigCustYn+ " //주작업 보조작업 구분 : " + ydDaoUtils.paraRecChkNull(recPara, "MAIN_WRK_YN");
	    				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    				if (jj == 0) {
	    					sMainWrkYn 	= ydDaoUtils.paraRecChkNull(recPara, "MAIN_WRK_YN");	//주작업 보조작업 구분
	    					rsMain.addRecord(recPara);

	    					szMsg = "[" + szOperationName + "]  새그룹VECTOR("+ ii+ ")에 추가"+ " 제품번호 : " + sStlNo;
	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	        				sBefYdPilingCd	= sYdPilingCd;
	        				sBefBigCustCd 	= sBigCustCd;

	    				} else {

	    					if ("Y".equals(sMainWrkYn)) {	//주작업

    	    					rsMain.addRecord(recPara);

    	    					szMsg = "[" + szOperationName + "] 주작업은  기존그룹 추가  적치단  : " + jj + " 제품번호 : " + sStlNo;
    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    					} else {

	    						// 대형고객사와 일반고객사 분리
	    						if (sBigCustCd.equals(sBefBigCustCd)) {
	    							if ("Y".equals(sBigCustCd)) {					// 고객사 지정여부
	    								if (sYdPilingCd.equals(sBefYdPilingCd)) {
	    	    	    					rsMain.addRecord(recPara);

	    	    	    					szMsg = "[" + szOperationName + "] VECTOR 기존그룹 추가  적치단  : " + jj + " 제품번호 : " + sStlNo;
	    	    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    								} else {
	    									rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    	    					rsMain.addRecord(recPara);
	    	    	    					vecResult.add(rsMain);

	    	    	    					szMsg = "[" + szOperationName + "](" + ii+ ")VECTOR 분할  새그룹 생성  적치단 : " + jj + " 제품번호 : " + sStlNo;
	    	    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    								}
	    							} else {
	    	    	    					rsMain.addRecord(recPara);

	    	    	    					szMsg = "[" + szOperationName + "] VECTOR 기존그룹 추가  적치단  : " + jj + " 제품번호 : " + sStlNo;
	    	    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
	    							}
	    						} else {

	    							rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	    					rsMain.addRecord(recPara);
	    	    					vecResult.add(rsMain);

	    	    					szMsg = "[" + szOperationName + "]("+ ii+ ")VECTOR 분할  새그룹 생성  적치단 : " + jj + " 제품번호 : " + sStlNo;
	    	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    						}
	    					}
    						sBefYdPilingCd	= sYdPilingCd;
    						sBefBigCustCd	= sBigCustCd;
		    			}
	    			}
				}
			}
        } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }//end of try~catch

		szMsg = "[" + szOperationName + "] 메소드 끝 - Handling Lot 수(Vector) : " + vecResult.size();
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return intRtnVal = 1;

    }//end of crnSchSameDataHandling()

	/**
     * 오퍼레이션명 : 1후판정정야드 스케줄링 Handling Data Check
     *
     * @param  ● msgRecSet, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int crnSchDataHandling(JDTORecordSet msgRecSet, Vector vecResult) throws JDTOException {

		JDTORecord recPara       	= null;
		JDTORecordSet rsHandling 	= null;

		String 	szMsg             	= "";
		String 	szMethodName      	= "crnSchDataHandling";
		String 	szOperationName     = "1후판정정 크레인Handling Lot편성";

		int 	intCurrBedCnt 		= 0;
		int 	intBefoBedCnt 		= 0;
		int 	intRtnVal 			= 1;
		String	szCurrMainWrkYn		= "";		// 주작업구분
		String	szYdStkColBedLTp	= "";		// 야드적치열Bed길이Type (혼적베드체크 F1,F2일때 혼적임)

		try {
			szMsg = "["+szOperationName+"] 메소드 START  - 대상재건수["+msgRecSet.size()+"]";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 편성
			//------------------------------------------------------------------------------------------------------------
    		for (int ii = 1; ii <= msgRecSet.size(); ii++) {
    			msgRecSet.absolute(ii);
    			recPara = msgRecSet.getRecord();
    			
    			intCurrBedCnt 		= ydDaoUtils.paraRecChkNullInt(recPara, "BED_CNT");
    			szCurrMainWrkYn		= ydDaoUtils.paraRecChkNull(recPara, "MAIN_WRK_YN");
    			szYdStkColBedLTp	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_BED_L_TP");

				szMsg = "["+szOperationName+"] [" + ii + "]번째 베드카운트 : intCurrBedCnt : " + intCurrBedCnt;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				//-------------------------------------------------------------------------------------------------------------
    			//	처음에 새그룹 생성
				//-------------------------------------------------------------------------------------------------------------
    			if (ii == 1) {

    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
    				rsHandling.addRecord(recPara);
    				vecResult.add(rsHandling) ;
    				intBefoBedCnt = intCurrBedCnt;

    				szMsg = "["+szOperationName+"] [" + ii + "]번째 새그룹 생성";
    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    			} else {
    				//-------------------------------------------------------------------------------------------------------------
    				//	보조작업일경우 1매단위로 작업함 .. 2013.04.05 김현우 추가
    				//-------------------------------------------------------------------------------------------------------------
    				if ("N".equals(szCurrMainWrkYn)) {

    					szMsg = "["+szOperationName+"] [" + ii + "]번째 현재료는 보조작업임으로 새그룹 생성";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				rsHandling.addRecord(recPara);
	    				vecResult.add(rsHandling) ;
	    				intBefoBedCnt = intCurrBedCnt;

	    				continue;

    				//-------------------------------------------------------------------------------------------------------------
    				//	혼적베드일때 1매단위로 작업 
    				//-------------------------------------------------------------------------------------------------------------
    				} else if ("F1".equals(szYdStkColBedLTp) || "F2".equals(szYdStkColBedLTp)) {

    					szMsg = "["+szOperationName+"] [" + ii + "]번째  베드번호["+intBefoBedCnt+"]는 혼적베드임으로 새그룹 생성";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				rsHandling.addRecord(recPara);
	    				vecResult.add(rsHandling) ;
	    				intBefoBedCnt = intCurrBedCnt;

	    				continue;

    				//-------------------------------------------------------------------------------------------------------------
    				//	새그룹 생성 - 베드가 서로 다른 경우
    				//-------------------------------------------------------------------------------------------------------------
    				} else if (intCurrBedCnt != intBefoBedCnt) {

    					szMsg = "["+szOperationName+"] [" + ii + "]번째 현재료의 베드 번호["+intCurrBedCnt+"]와 이전재료의 베드번호["+intBefoBedCnt+"]가 다르므로 새그룹 생성";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				rsHandling.addRecord(recPara);
	    				vecResult.add(rsHandling) ;
	    				intBefoBedCnt = intCurrBedCnt;

	    				continue;

	    			//-------------------------------------------------------------------------------------------------------------
	    			//	기존 그룹에 추가
	    			//-------------------------------------------------------------------------------------------------------------
    				} else {

    					szMsg = "["+szOperationName+"] [" + ii + "]번째 .. 기존그룹에 추가";
	    				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

    					rsHandling.addRecord(recPara);
	    				intBefoBedCnt   = intCurrBedCnt;

	    				continue;
    				}
    			}
    		}//end of for
    		
    		ydUtils.printParam(szOperationName, vecResult);
    		
        } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }//end of try~catch

		szMsg = "["+szOperationName+"] 메소드 END - Handling Lot 수 : " + vecResult.size();
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;

    }//end of crnSchDataHandling()

	/**
     * 오퍼레이션명 : 1후판정정 창고 크레인 스펙  Check
     *
     * @param  ● msgRecSet, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int crnSchCrnSpecHandling(String szSchCd, String szYD_WRKABLE_CRN, Vector vecResult, Vector vecReResult, String logId) throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.11.18 로그 개선  START
// crnSchCrnSpecHandling argument 에 logId 항목 추가 개선
//  public int crnSchCrnSpecHandling(String szSchCd, String szYD_WRKABLE_CRN, Vector vecResult, Vector vecReResult) throws JDTOException {
// 2024.11.18 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

    	JDTORecord recPara       	= null;

		String szMsg             	= "";
		String szMethodName      	= "crnSchCrnSpecHandling";
		String szOperationName      = "1후판정정 크레인 스펙Handling Lot편성";

		int intRtnVal 				= 0;
		int iHandSetCnt 			= 0;
		int iHandCnt 				= 0;
		int iStartCnt              	= 0;
		int iSettingCnt             = 0;

		JDTORecordSet rsParaSet	 	= null;
		JDTORecordSet rsParaTempSet	= null;
		JDTORecordSet rsRtnTempSet	= null;

		String sYdSchCd        		= "";
		String sYdEqpId           	= "";
		String sYdBayGp     		= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {

			szMsg = "[" + szOperationName + "] 메소드 시작  - 대상재건수[" + vecResult.size() + "]";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			sYdSchCd 	= szSchCd;
			sYdEqpId	= szYD_WRKABLE_CRN;

			if ("".equals(sYdSchCd)) {
				sYdSchCd = "ZZZZZZZZ";
			}
			if ("".equals(sYdEqpId)) {
				sYdEqpId = "ZZZZZZZZ";
			}
			sYdBayGp = sYdEqpId.substring(1, 2);

			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 편성
			//------------------------------------------------------------------------------------------------------------

			szMsg = "스펙Handling.size(): " + vecResult.size();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 단위(HAND_CNT재 SETTING)
			//------------------------------------------------------------------------------------------------------------
			iHandSetCnt = 0;

			for(int ii = 0; ii < vecResult.size(); ii++) {

				iHandSetCnt ++ ;
    			rsParaSet =(JDTORecordSet)vecResult.get(ii) ;
    			rsParaSet.first();


    			for(int jj = 0; jj < rsParaSet.size(); jj++) {

    				rsParaTempSet = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    			for(int kk = 0; kk < rsParaSet.size(); kk++) {
	    				iStartCnt = 0;

	    				rsParaSet.absolute(kk+1);
	    				recPara = rsParaSet.getRecord();

	    				iHandCnt = ydDaoUtils.paraRecChkNullInt(recPara, "HAND_CNT");

	    				if (iHandCnt == 0) {

	    					if (iStartCnt == 0) {
	    						iStartCnt = kk ;
	    					}
	    					rsParaTempSet.addRecord(recPara);
	    			    }
	    			}

	    			szMsg = "[" + szOperationName + "] HAND_CNT 미 SETTING 건수 " + rsParaTempSet.size();
	    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    			if (rsParaTempSet.size() > 0) {								//세팅할 대상 있을 경우

	    				rsParaTempSet.first();

	    				for(int ll=0; ll<rsParaTempSet.size(); ll++) {

	    					szMsg = "검증용DATA1	[" + szOperationName + "] [" + ll + "]번째 확인 DATA";
	    					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    					rsParaTempSet.absolute(ll);
	        				JDTORecord recCurrt = JDTORecordFactory.getInstance().create();
	        				recCurrt = rsParaTempSet.getRecord();
	        				szMsg = recCurrt.toString();
	        				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

	    	    		}
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 this.crnSchCrnSpecCheck() argument logId 추가
//	    				intRtnVal = this.crnSchCrnSpecCheck(sYdBayGp, rsParaTempSet);
	    				intRtnVal = this.crnSchCrnSpecCheck(sYdBayGp, rsParaTempSet, logId);
//-------------------------------------------------------------------------------------------------------------------------

	    				// 가능수 만큼 setting
		    			szMsg = "[" + szOperationName + "]HAND_CNT 세팅건수: " + intRtnVal;
		    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		    			if (intRtnVal == 0) {     //셋팅이 안되면 무조건 1매로 강제  Set

		        			szMsg = "[" + szOperationName + "] 확인 해야 함 " + intRtnVal;
		        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

		    				intRtnVal = 1;
		    			}
	    				if (intRtnVal > 0) {
		        			rsRtnTempSet = JDTORecordFactory.getInstance().createRecordSet("Temp");

		        			rsParaSet.first();
		        			iSettingCnt = 0;
			    			for(int kk = 0; kk < rsParaSet.size(); kk++) {

			    				rsParaSet.absolute(kk+1);
			    				recPara = rsParaSet.getRecord();

			    				iHandCnt = ydDaoUtils.paraRecChkNullInt(recPara, "HAND_CNT");

				    			szMsg = "[" + szOperationName + "]HAND_CNT : " + iHandCnt;
				    			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			    				if (iHandCnt == 0) {

			    					recPara.setField("HAND_CNT", Integer.toString(iHandSetCnt));
			    					rsRtnTempSet.addRecord(recPara);
			    					iSettingCnt ++;
			    					if (iSettingCnt == intRtnVal) {
			    						kk = rsParaSet.size() + 1;
			    					}
			    			    }
			    			}

			    			vecReResult.add(rsRtnTempSet);

		        		} else if (intRtnVal < 0) {
		        			szMsg = "["  +szOperationName + "] 확인 해야 함 " + intRtnVal;
		        			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
		        			throw new JDTOException(szMsg);
		        		}
	    			} else {

	    				jj = rsParaSet.size() + 1;

	    			}
				}

    		}

        } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }//end of try~catch

		szMsg = "[" + szOperationName + "] 메소드 끝 - Handling Lot 수 : " + vecResult.size();
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return intRtnVal = 1;

    }//end of crnSchCrnSpecHandling()

	/**
     * 오퍼레이션명 : 크레인 스펙  Check 
     *
     * @param  ● msgRecSet, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int crnSchCrnSpecCheck(String sYdBayGp, JDTORecordSet rsParaTempSet, String logId) throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.11.18 로그 개선  START
// crnSchCrnSpecCheck argument 에 logId 항목 추가 개선
//  public int crnSchCrnSpecCheck(String sYdBayGp, JDTORecordSet rsParaTempSet) throws JDTOException {
// 2024.11.18 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		String szMsg             	= "";
		String szMethodName      	= "crnSchCrnSpecCheck";
		String szOperationName      = "1후판정정 크레인 스펙Handling CHECK";
		String sStlNo               = "";
		int iRecCnt 				= 0;
		int istatus             	= 0;
		int iHandingSeting_cnt  	= 0;
		int iTargetCnt         		= 0;
		JDTORecord    inRecord 		= null;

		double dblGdsThick 			= 0;
		double dblGdsWidth 			= 0;
		double dblGdsWeight 		= 0;
		double dblGdsLength 		= 0;

		double dblGdsFirstThick 	= 0;
		double dblGdsFirstMinusThick= 0;
		double dblGdsLastWidth 		= 0;
		double dblGdsMaxThick 		= 0;
		double dblGdsMinWidth 		= 0;
		double dblGdsMinWidthThick	= 0;
		double dblGdsMaxWidth 		= 0;
		double dblGdsMaxTotThick	= 0;
		double dblGdsMaxTotWeight	= 0;
		double dblGdsMaxLength 		= 0;
		double dblGdsAverageThick	= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {

			iRecCnt = rsParaTempSet.size();

			szMsg = "[" + szOperationName + "] 메소드 시작  - 대상재건수[" + rsParaTempSet.size() + "]";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 단위로 최대/최소값 SETTING
			//------------------------------------------------------------------------------------------------------------
			rsParaTempSet.first();
			for(int ii = iRecCnt  ; ii > 0; ii--) {     //Handling Lot -1 처리

				szMsg = "[" + szOperationName + "] Handling Lot -1 처리 하려감 ";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				// Handling Lot 단위로 최대/최소값 SETTING
				dblGdsFirstThick		= 0;		//최상단두께
				dblGdsFirstMinusThick= 0;			//최상단-1 두께
				dblGdsMaxThick 		= 0;
				dblGdsMaxWidth 		= 0;
				dblGdsMinWidth		= 0;
				dblGdsMinWidthThick  = 0;
				dblGdsMaxTotThick	= 0;
				dblGdsLastWidth		= 0;
				dblGdsMaxTotWeight	= 0;
				iTargetCnt 			= 0;

				for(int jj = 0 ; jj < ii  ; jj++) {

					inRecord =JDTORecordFactory.getInstance().create();
					rsParaTempSet.absolute(jj+1);
					inRecord = rsParaTempSet.getRecord();

					//DATA READ
					dblGdsThick 	= ydDaoUtils.paraRecChkNullDouble(inRecord, "YD_MTL_T");
					dblGdsWidth 	= ydDaoUtils.paraRecChkNullDouble(inRecord, "YD_MTL_W");
					dblGdsWeight 	= ydDaoUtils.paraRecChkNullDouble(inRecord, "YD_MTL_WT");
					dblGdsLength 	= ydDaoUtils.paraRecChkNullDouble(inRecord, "YD_MTL_L");
					sStlNo 			= ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");

					iTargetCnt ++;

					if (jj == 0) {

						dblGdsFirstThick	= dblGdsThick;		//최상단두께
						dblGdsMaxThick 		= dblGdsThick;
						dblGdsMaxWidth 		= dblGdsWidth;
						dblGdsMinWidth		= dblGdsWidth;
						dblGdsMinWidthThick = dblGdsThick;
						dblGdsLastWidth		= dblGdsWidth;
						dblGdsMaxTotThick	= dblGdsThick;
						dblGdsMaxTotWeight	= dblGdsWeight;
						dblGdsMaxLength		= dblGdsLength;

					} else {
						if (jj == 1) {
							dblGdsFirstMinusThick = dblGdsThick;//최상단-1두께
						}
						if (jj ==(ii - 1)) {
							dblGdsLastWidth	= dblGdsWidth;		//최하단폭
						}
						if (dblGdsMaxThick < dblGdsThick) {		//최대두께
							dblGdsMaxThick	= dblGdsThick;
						}
						if (dblGdsMinWidth > dblGdsWidth) {		//최소폭
							dblGdsMinWidth		= dblGdsWidth;
							dblGdsMinWidthThick  = dblGdsThick;
						}
						if (dblGdsMaxWidth < dblGdsWidth) {		//최대폭
							dblGdsMaxWidth	= dblGdsWidth;
						}
						if (dblGdsMaxLength < dblGdsLength) {		//최대길이
							dblGdsMaxLength	= dblGdsLength;
						}

						dblGdsMaxTotThick = dblGdsMaxTotThick  + dblGdsThick;	//총두께
						dblGdsMaxTotWeight= dblGdsMaxTotWeight + dblGdsWeight; 	//총중량
					}
				}

				dblGdsAverageThick = dblGdsMaxTotThick / iTargetCnt;

				szMsg = "[" + szOperationName + "] 1후판정정 창고 크레인 스펙상세  Check 처리 하려감 ";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 this.crnSchCrnSpecCheckDtl() argument logId 추가
//-------------------------------------------------------------------------------------------------------------------------
//			    istatus = this.crnSchCrnSpecCheckDtl(sStlNo, sYdBayGp, dblGdsFirstThick, dblGdsFirstMinusThick,dblGdsLastWidth,
//                        dblGdsMaxThick, dblGdsMinWidth, dblGdsMinWidthThick, dblGdsMaxWidth, dblGdsMaxLength,
//                        dblGdsMaxTotThick, dblGdsMaxTotWeight,
//                        dblGdsAverageThick, iTargetCnt);
				
			    istatus = this.crnSchCrnSpecCheckDtl(sStlNo, sYdBayGp, dblGdsFirstThick, dblGdsFirstMinusThick,dblGdsLastWidth,
			    		                             dblGdsMaxThick, dblGdsMinWidth, dblGdsMinWidthThick, dblGdsMaxWidth, dblGdsMaxLength,
			    		                             dblGdsMaxTotThick, dblGdsMaxTotWeight,
			    		                             dblGdsAverageThick, iTargetCnt, logId);
//-------------------------------------------------------------------------------------------------------------------------

				szMsg = "[" + szOperationName + "] 1후판정정 창고 크레인 스펙상세  Check 처리 결과 "+ istatus;
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			    if (istatus == 1) {   // 가능

			    	iHandingSeting_cnt = iTargetCnt;
			    	ii  = -1;

			    } else if (istatus == -2) { // 현재 Lot수에서 -1

			    	iHandingSeting_cnt = iTargetCnt - 1;
			    	ii  = -1;
			    }
    		}

        } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }//end of try~catch

		szMsg = "[" + szOperationName + "] 메소드 끝 - Handling Lot 수 : " + iHandingSeting_cnt;
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return iHandingSeting_cnt;

    }//end of crnSchCrnSpecCheck()

	/**
     * 오퍼레이션명 : 1후판정정 창고 크레인 스펙상세  Check 
     *
     * @param  ● msgRecSet, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int crnSchCrnSpecCheckDtl(String sStlNo,             String sYdBayGp,
    		                         double dblGdsFirstThick,   double dblGdsFirstMinusThick, double dblGdsLastWidth, double dblGdsMaxThick,
    		                         double dblGdsMinWidth,     double dblGdsMinWidthThick,   double dblGdsMaxWidth,  double dblGdsMaxLength,
    		                         double dblGdsMaxTotThick,  double dblGdsMaxTotWeight,
    		                         double dblGdsAverageThick, int iTargetCnt, String logId) throws JDTOException {
////////////////////////////////////////////////////////////////////////////////////////
// 2024.11.18 로그 개선  START
// crnSchCrnSpecCheckDtl argument 에 logId 항목 추가 개선
//  public int crnSchCrnSpecCheckDtl(String sStlNo,             String sYdBayGp,
//                                  double dblGdsFirstThick,   double dblGdsFirstMinusThick, double dblGdsLastWidth, double dblGdsMaxThick,
//                					double dblGdsMinWidth,     double dblGdsMinWidthThick,   double dblGdsMaxWidth,  double dblGdsMaxLength,
//                					double dblGdsMaxTotThick,  double dblGdsMaxTotWeight,
//                					double dblGdsAverageThick, int iTargetCnt) throws JDTOException {
//2024.11.18 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		JPlateYdEqpDAO ydEqpDao     = new JPlateYdEqpDAO();

		JDTORecord inRecord 		= null;
		JDTORecord outRecord 		= null;
		JDTORecordSet outResult 	= null;


    	int    intRtnVal            = 0;
    	double dblMagnetLength		= 0;
    	double dblGdsSh				= 0;
    	double dblMagnetSh			= 0;


    	String szMsg             	= "";
		String szMethodName      	= "crnSchCrnSpecCheckDtl";
		String szOperationName      = "1후판정정야드 크레인 스펙상세 CHECK";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {

        	szMsg = "제품:(" + sStlNo + ")대상동(" + sYdBayGp + ")=>>/최상단두께:" + dblGdsFirstThick + "/최상단-1두께:" + dblGdsFirstMinusThick + "/최하단폭:" + dblGdsLastWidth + "/최대두께:" + dblGdsMaxThick;
        	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.INFO, logId);
        	szMsg = "/최소폭:"+ dblGdsMinWidth+"/최소폭두께:"+ dblGdsMinWidthThick+ "/최대폭:"  + dblGdsMaxWidth+ "/최대길이 :"  + dblGdsMaxLength;
        	ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.INFO, logId);
			szMsg = "/총두께:"  + dblGdsMaxTotThick + "/총중량:"  + dblGdsMaxTotWeight  + "/평균두께:"  + dblGdsAverageThick	+ "/LOT 수:"  + iTargetCnt	;
			ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.INFO, logId);

        	// 1후판정정야드 Magnet 흡착기준 BRE 조회
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("W_GP", 		Double.toString(dblGdsMaxWidth));
			inRecord.setField("T_GP", 		Double.toString(dblGdsAverageThick));
			inRecord.setField("YD_GP", 		JPlateYdConst.YD_GP_P_PLATE_YARD);			// 1후판정정야드 구분
			inRecord.setField("YD_BAY_GP", 	sYdBayGp);

			outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
			outRecord  	= JDTORecordFactory.getInstance().create();

			// 1후판정정야드는 동별 크레인 사양이 동일하다.
			// A동 1대, B동 2대, C동 3대
			intRtnVal = ydEqpDao.getYdEqpMagnetBre(inRecord, outResult);

			if (intRtnVal > 0) {
				outResult.first();
				outRecord = outResult.getRecord();
				dblGdsSh = ydDaoUtils.paraRecChkNullDouble(outRecord, "GDS_SH"); //Magnet 흡착매수
			}

			//------------------------------------------------------------------------------------------------------------
			//	최상단 두께 CHECK로 HANDLING 매수 제한
			//------------------------------------------------------------------------------------------------------------
			if (dblGdsFirstThick >= 19) {
				if (dblGdsSh > 3) {
					dblMagnetSh = 3;
				} else {
					dblMagnetSh = dblGdsSh;
				}
			} else if (dblGdsFirstThick >= 15) {
				if (dblGdsSh > 4) {
					dblMagnetSh = 4;
				} else {
					dblMagnetSh = dblGdsSh;
				}
			} else {
				dblMagnetSh = dblGdsSh;
			}

			if (dblGdsFirstThick >= 30) {
				dblMagnetSh = 1;
			}

			dblMagnetLength = dblGdsMinWidth - (dblGdsMaxWidth /2 - 1000); //최소폭  MAGNET 부착길이

			//------------------------------------------------------------------------------------------------------------
			//	Magget 수와 Handling Lot 수
			//------------------------------------------------------------------------------------------------------------
			if (iTargetCnt > dblMagnetSh) {
				szMsg = "[" + szOperationName + "] Magget 수와 Handling Lot 수 조정 걸림1 ";
				ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				return intRtnVal = -1;
			}
			//------------------------------------------------------------------------------------------------------------
			//	2매 이상이고 최소폭 제품에 MAGNET부착길이가 1000 미만일 경우
			//------------------------------------------------------------------------------------------------------------
			if (iTargetCnt > 1) {

				if (dblGdsMaxWidth >= 2000 && dblMagnetLength < 1000) {
					szMsg = "[" + szOperationName + "] 2매 이상이고 최대폭 2000이상이고 최소폭 제품에 MAGNET부착길이가 1000 미만일 경우 조정 걸림1 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -1;
				}
			}

			//------------------------------------------------------------------------------------------------------------
			//	2매 이상이고 최소폭두께 > 10 이고 최소폭 제품에 MAGNET부착길이가 1200 미만일 경우
			//------------------------------------------------------------------------------------------------------------
			if (iTargetCnt > 1) {

				if (dblGdsMinWidthThick > 10 && dblMagnetLength < 1200) {
					szMsg = "[" + szOperationName + "] 2매 이상이고  최소폭두께 > 10 이고  최소폭 제품에 MAGNET부착길이가 1200 미만일 경우 조정 걸림1 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -1;
				}
			}
			//------------------------------------------------------------------------------------------------------------
			//	3매 이상이고 최소폭두께 > 10 이고 최소폭 제품에 MAGNET부착길이가 1400 미만일 경우
			//------------------------------------------------------------------------------------------------------------
			if (iTargetCnt > 2) {

				if (dblGdsMinWidthThick > 10 && dblMagnetLength < 1400) {
					szMsg = "["  +szOperationName + "] 3매 이상이고  최소폭두께 > 10 이고  최소폭 제품에 MAGNET부착길이가 1400 미만일 경우 조정 걸림1 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -1;
				}
			}
			//------------------------------------------------------------------------------------------------------------
			//	4매 이상이고 최소폭두께 > 10 이고 최소폭 제품에 MAGNET부착길이가 1600 미만일 경우
			//------------------------------------------------------------------------------------------------------------
			if (iTargetCnt > 3) {

				if (dblGdsMinWidthThick > 10 && dblMagnetLength < 1600) {
					szMsg = "[" + szOperationName + "] 4매 이상이고  최소폭두께 > 10 이고  최소폭 제품에 MAGNET부착길이가 1600 미만일 경우 조정 걸림1 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -1;
				}
			}
			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 총 두께 CHECK
			//------------------------------------------------------------------------------------------------------------
			if ("B".equals(sYdBayGp)) {
				if (dblGdsMaxTotThick >= 48) {
					szMsg = "[" + szOperationName + "] Handling Lot 총 두께 CHECK 조정 걸림1 =" + dblGdsMaxTotThick;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -1;
				}
			} else {
				if (dblGdsMaxTotThick > 50) {
					szMsg = "[" + szOperationName + "] Handling Lot 총 두께 CHECK 조정 걸림2 =" + dblGdsMaxTotThick;
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -1;
				}
			}

			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 편성수량 조정
			//------------------------------------------------------------------------------------------------------------
			if (iTargetCnt == 3) {
				if ((dblGdsFirstThick >= 16) &&(dblGdsLastWidth == dblGdsMaxWidth) &&(dblMagnetLength < 1500)) {
					szMsg = "[" + szOperationName + "] Handling Lot 편성수량 조정 걸림1 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					return intRtnVal = -1;
				}
			}

			if (iTargetCnt == 4) {
				if ((dblGdsFirstThick >= 16) &&(dblGdsLastWidth == dblGdsMaxWidth) &&(dblMagnetLength < 1600)) {
					szMsg = "[" + szOperationName + "] Handling Lot 편성수량 조정 걸림2 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -1;
				}
			}

			if (iTargetCnt > 2) {

				if (dblGdsFirstMinusThick >= 20) {
					szMsg = "[" + szOperationName + "] Handling Lot 편성수량 조정 걸림3 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -1;
				}
			}

			if (iTargetCnt > 2) {

				if (dblGdsMaxWidth <= 1600) {
	        		szMsg = "[" + szOperationName + "] Handling Lot 편성수량 조정 걸림4(현Lot수 -1) ";
	        		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -2;
	        	}
			}

			if (iTargetCnt > 1) {

				if ((dblGdsMaxWidth/2 - dblGdsMinWidth/2)/(dblGdsMaxWidth/2)*100 >= 50) {
					szMsg = "[" + szOperationName + "] Handling Lot 편성수량 조정 걸림5(현Lot수 -1) ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -2;
				}
	        }
			//------------------------------------------------------------------------------------------------------------
			//	크레인 권상 능력에 따른 Handling Lot 편성수량 조정
			//------------------------------------------------------------------------------------------------------------
			if ("B".equals(sYdBayGp)) {

				if (dblGdsMaxTotWeight > 30000) {
					szMsg = "[" + szOperationName + "] 크레인 권상 능력에 따른 Handling Lot 편성수량 조정 걸림6 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
					return intRtnVal = -1;
				} else if (dblGdsMaxLength > 21601) {
					if (dblGdsMaxTotWeight > 27000) {
						szMsg = "[" + szOperationName + "] 크레인 권상 능력에 따른 Handling Lot 편성수량 조정 걸림5 ";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						return intRtnVal = -1;
					}
				} else if (dblGdsMaxLength > 19501) {
					if (dblGdsMaxTotWeight > 23000) {
						szMsg = "[" + szOperationName + "] 크레인 권상 능력에 따른 Handling Lot 편성수량 조정 걸림4 ";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						return intRtnVal = -1;
					}
				} else if (dblGdsMaxLength > 17001) {
					if (dblGdsMaxTotWeight > 20000) {
						szMsg = "[" + szOperationName + "] 크레인 권상 능력에 따른 Handling Lot 편성수량 조정 걸림3 ";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						return intRtnVal = -1;
					}
				} else if (dblGdsMaxLength <= 14501) {
					if (dblGdsMaxTotWeight > 17000) {
						szMsg = "[" + szOperationName + "] 크레인 권상 능력에 따른 Handling Lot 편성수량 조정 걸림2 ";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						return intRtnVal = -1;
					}
				} else if (dblGdsMaxLength <= 12000) {
					if (dblGdsMaxTotWeight > 15000) {
						szMsg = "["  +szOperationName + "] 크레인 권상 능력에 따른 Handling Lot 편성수량 조정 걸림1 ";
						ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
						return intRtnVal = -1;
					}
				}
			} else {
				if (dblGdsMaxTotWeight > 25000) {
					szMsg = "[" + szOperationName + "] 크레인 권상 능력에 따른 Handling Lot 편성수량 조정 걸림5 ";
					ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

					return intRtnVal = -1;
				}
			}

        } catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }//end of try~catch

		szMsg = "[" + szOperationName + "] 메소드 끝 ";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return intRtnVal = 1;

    }//end of crnSchCrnSpecCheckDtl()

  //---------------------------------------------------------------------------
    
	 /**
	 * 오퍼레이션명 : 야드작업이력생성Main(2)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return void
	 * @throws DAOException
	 */
	public void procWorkHistoryCreate2(JDTORecord msgRecord)throws DAOException  {

		JPlateYdCrnWrkMtlDAO	ydCrnWrkMtlDao 	= new JPlateYdCrnWrkMtlDAO();
		JPlateYdWrkHistDAO 		ydWrkHistDao 	= new JPlateYdWrkHistDAO();

		int 	intRtnVal 			= 0;
		String 	sMsg        		= "";
		String 	sRtnMsg        		= "";
		String 	sMethodName 		= "procWorkHistoryCreate2";
		String 	sOperationName 		= "야드작업이력생성Main(2)";

		String 	sCrnSchId 			= "";     	// 크레인스케줄ID
		String 	sStlNo 				= "";
		String	sModifier			= "";
		String  sStlNos				= "";

		JDTORecordSet rsResult 		= null;
		JDTORecordSet rsCrnStock 	= null;
		JDTORecord recCrnStock 		= null;
		JDTORecord recMtl 			= null;
		
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(msgRecord, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			sMsg = "[" + sOperationName + "] --------------------- 시작";
			ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);

			sCrnSchId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID"	);
			sModifier = ydDaoUtils.paraRecModifier(msgRecord					);
			sStlNos   = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NOS"			);

			//레코드 생성
			JDTORecord recPara = JDTORecordFactory.getInstance().create();

			//크레인스케줄ID
			recPara.setField("YD_CRN_SCH_ID", sCrnSchId		);
			recPara.setField("STL_NOS", sStlNos				);

			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

			// 크레인스케줄의 작업재료들을 읽어온다. [실제권하한 재료만 (파라메터로 넘어온 값)]
			/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getByYdCrnSchIdWithDel2 

			SELECT YD_CRN_SCH_ID    AS YD_CRN_SCH_ID
			     , A.STL_NO         AS STL_NO
			     , A.REGISTER       AS REGISTER
			     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			     , A.MODIFIER       AS MODIFIER
			     , A.DEL_YN         AS DEL_YN
			     , A.YD_AID_WRK_YN  AS YD_AID_WRK_YN
			     , A.YD_STK_LYR_NO  AS YD_STK_LYR_NO
			     , A.YD_STK_LOT_TP  AS YD_STK_LOT_TP
			     , A.YD_STK_LOT_CD  AS YD_STK_LOT_CD
			     , A.HCR_GP         AS HCR_GP
			     , A.STL_PROG_CD    AS STL_PROG_CD
			     , A.YD_MTL_ITEM    AS YD_MTL_ITEM
			     , A.YD_ROUTE_GP    AS YD_ROUTE_GP
			     , B.YD_MTL_T
			     , B.YD_MTL_W
			     , B.YD_MTL_L
			     , B.YD_MTL_WT
			  FROM TB_YD_CRNWRKMTL A
			     , TB_YD_SHRSTOCK B
			 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND A.STL_NO = B.STL_NO
			   AND A.STL_NO IN (
			   
			        SELECT  SUBSTR (AA, INSTR (AA, ',', 1, LEVEL) + 1,
			                  INSTR (AA, ',', 1, LEVEL + 1) - INSTR (AA, ',', 1, LEVEL)
			                   - 1
			               ) COIL_NO
			          FROM (SELECT ',' || :V_STL_NOS || ',' AA FROM DUAL)
			        CONNECT BY LEVEL <= LENGTH (AA) - LENGTH (REPLACE (AA, ',')) - 1
			       )
			   
			 ORDER BY YD_STK_LYR_NO DESC
 			*/
        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
			recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
        	
			intRtnVal = ydCrnWrkMtlDao.getByYdCrnSchIdWithDel2(recPara, rsResult);

			if (intRtnVal <= 0) {
	  			if (intRtnVal == 0) {
	  				sMsg = "크레인스케줄ID(" + sCrnSchId + ")에 대한 재료데이터가 없습니다.";
	  				ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
	  				throw new DAOException(sMsg);
	  			} else if (intRtnVal == -2) {
	  				sMsg = "크레인스케줄ID(" + sCrnSchId + ")로 조회중 parameter error 발생!";
	  				ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
	  				throw new DAOException(sMsg);
	  			}
			}

			// 작업재료 수만큼 루프
			for(int ii=1; ii<=rsResult.size(); ii++) {

				rsResult.absolute(ii);
				recMtl = rsResult.getRecord();

				sStlNo = ydDaoUtils.paraRecChkNull(recMtl,"STL_NO");

				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", 	sCrnSchId);
				recPara.setField("STL_NO", 			sStlNo);

				rsCrnStock = JDTORecordFactory.getInstance().createRecordSet("Temp");

				// 재료유형에 따라 공통테이블, 저장품테이블, 크레인작업내역들을 읽어온다.
				/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.getYdWrkHistByPlate 

				SELECT Z.*
				  FROM
				(
				    SELECT
				           CR.YD_GP                                     AS YD_GP
				          ,ST.STL_NO                                    AS STL_NO
				          ,SUBSTR(CR.YD_SCH_CD, 7, 1)                   AS YD_GNT_GP
				          ,NULL                                         AS STL_APPEAR_GP
				          ,VW.ITEMNAME_CD                               AS ITEMNAME_CD
				          ,VW.ORD_YEOJAE_GP                             AS ORD_YEOJAE_GP
				          ,VW.ORD_NO                                    AS ORD_NO
				          ,VW.ORD_DTL                                   AS ORD_DTL
				          ,VW.SPEC_ABBSYM                               AS SPEC_ABBSYM
				          ,VW.DEMANDER_CD                               AS DEMANDER_CD
				          ,ST.STL_PROG_CD                               AS STL_PROG_CD
				          ,ST.YD_MTL_W_GP                               AS YD_MTL_W_GP
				          ,ST.YD_MTL_T_GP                               AS YD_MTL_T_GP
				          ,ST.YD_MTL_L_GP                               AS YD_MTL_L_GP
				          ,ST.YD_MTL_T                                  AS YD_MTL_T
				          ,ST.YD_MTL_W                                  AS YD_MTL_W
				          ,ST.YD_MTL_L                                  AS YD_MTL_L
				          ,ST.YD_MTL_WT                                 AS YD_MTL_WT
				          ,ST.REHEAT_SLAB_GP                            AS REHEAT_SLAB_GP
				          ,ST.COOL_DONE_GP                              AS COOL_DONE_GP
				          ,NULL                                         AS YD_PILING_CD
				          ,NULL                                         AS YD_BOOK_OUT_LOC
				          ,ST.FRTOMOVE_ORD_DATE                         AS FRTOMOVE_ORD_DATE
				          ,ST.URGENT_FRTOMOVE_WORD_GP                   AS URGENT_FRTOMOVE_WORD_GP
				          ,CR.YD_WBOOK_ID                               AS YD_WBOOK_ID
				          ,CR.YD_CRN_SCH_ID                             AS YD_CRN_SCH_ID
				          ,CR.YD_SCH_CD                                 AS YD_SCH_CD
				          ,CR.YD_SCH_ST_GP                              AS YD_SCH_ST_GP
				          ,CR.YD_SCH_REQ_GP                             AS YD_SCH_REQ_GP
				          ,CR.YD_SCH_PRIOR                              AS YD_SCH_PRIOR
				          ,TO_CHAR(CR.YD_WBOOK_DT,'YYYYMMDDHH24MISS')   AS YD_WBOOK_DT
				          ,WK.YD_AID_WRK_YN                             AS YD_AID_WRK_YN
				          ,CR.YD_TO_LOC_DCSN_MTD                        AS YD_TO_LOC_DCSN_MTD
				          ,CR.YD_TO_LOC_GUIDE                           AS YD_TO_LOC_GUIDE
				          ,TO_CHAR(CR.YD_SCH_DT,'YYYYMMDDHH24MISS')     AS YD_SCH_DT
				          ,TO_CHAR(CR.YD_WORD_DT,'YYYYMMDDHH24MISS')    AS YD_WORD_DT
				          ,CR.YD_UP_WO_LOC                              AS YD_UP_WO_LOC
				          ,CR.YD_UP_WO_LAYER                            AS YD_UP_WO_LAYER
				          ,CR.YD_UP_WR_LOC                              AS YD_UP_WR_LOC
				          ,CR.YD_UP_WR_LAYER                            AS YD_UP_WR_LAYER
				          ,CR.YD_UP_WRK_ACT_GP                          AS YD_UP_WRK_ACT_GP
				          ,TO_CHAR(CR.YD_UP_CMPL_DT,'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
				          
				          ,CR.YD_DN_WO_LOC                              AS YD_DN_WO_LOC
				          ,CR.YD_DN_WO_LAYER                            AS YD_DN_WO_LAYER

				          ,NVL((LY.YD_STK_COL_GP||LY.YD_STK_BED_NO), CR.YD_DN_WR_LOC)   AS YD_DN_WR_LOC
				          ,NVL(LY.YD_STK_LYR_NO, CR.YD_DN_WR_LAYER)                     AS YD_DN_WR_LAYER
				          ,NVL(SUBSTR(LY.YD_STK_COL_GP,1,1), CR.YD_GP)                  AS YD_DN_WR_YD_GP

				          ,CR.YD_DN_WRK_ACT_GP                          AS YD_DN_WRK_ACT_GP
				          ,TO_CHAR(CR.YD_DN_CMPL_DT,'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
				          ,CR.YD_WRK_HDS_DD                             AS YD_WRK_HDS_DD
				          ,CR.YD_WRK_DUTY                               AS YD_WRK_DUTY
				          ,CR.YD_WRK_PARTY                              AS YD_WRK_PARTY
				          ,CR.YD_EQP_ID                                 AS YD_EQP_ID
				          ,WK.YD_STK_LYR_NO                             AS YD_STK_LYR_NO
				      FROM TB_YD_CRNSCH    CR
				          ,TB_YD_CRNWRKMTL WK
				          ,TB_YD_SHRSTOCK  ST
				          ,VW_YD_SHRSTOCK  VW
				          ,TB_YD_STKLYR    LY
				     WHERE CR.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				       AND CR.YD_CRN_SCH_ID = WK.YD_CRN_SCH_ID
				       AND WK.STL_NO        = :V_STL_NO
				       AND WK.STL_NO        = ST.STL_NO
				       AND WK.STL_NO        = VW.STL_NO(+)
				       AND WK.STL_NO        = LY.STL_NO(+)
				       AND LY.YD_STK_LYR_MTL_STAT(+) = 'C'
				) Z
				WHERE Z.YD_GP = Z.YD_DN_WR_YD_GP
				*/
				intRtnVal = ydWrkHistDao.getYdWrkHistByPlate(recPara, rsCrnStock);

				if (intRtnVal <= 0) {
					if (intRtnVal == 0) {
						sRtnMsg = "재료번호(" + sStlNo + ")에 대한 데이터가 없습니다.";
						sMsg    = "[" + sOperationName + "] " + sRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						throw new DAOException(sRtnMsg);
					} else if (intRtnVal == -2) {
						sRtnMsg = "재료번호(" + sStlNo + ")로 조회중 parameter error 발생!";
						sMsg    = "[" + sOperationName + "] " + sRtnMsg;
						ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
						throw new DAOException(sRtnMsg);
					} //end if
				}//end if

				rsCrnStock.absolute(1);
				recCrnStock = rsCrnStock.getRecord();

				recCrnStock.setField("REGISTER",	sModifier);
				recCrnStock.setField("MODIFIER",	sModifier);
				recCrnStock.setField("DEL_YN", 		"N");

				// 이력테이블에 INSERT
				/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkHistDAO.insYdWrkHist

				INSERT INTO TB_YD_WRKHIST
				(
				       YD_WRK_HIST_ID               -- 야드작업이력ID
				      ,REGISTER                     -- 등록자
				      ,REG_DDTT                     -- 등록일시
				      ,MODIFIER                     -- 수정자
				      ,MOD_DDTT                     -- 수정일시
				      ,DEL_YN                       -- 삭제유무
				      ,YD_GP                        -- 야드구분
				      ,STL_NO                       -- 재료번호
				      ,YD_GNT_GP                    -- 재료수불구분
				      ,STL_APPEAR_GP                -- 재료외형구분
				      ,ITEMNAME_CD                  -- 품명코드
				      ,ORD_YEOJAE_GP                -- 주문여재구분
				      ,ORD_NO                       -- 주문번호
				      ,ORD_DTL                      -- 주문행번
				      ,STLKIND_CD                   -- 강종코드
				      ,SPEC_ABBSYM                  -- 규격약호
				      ,ORD_GP                       -- 수주구분
				      ,STL_PROG_CD                  -- 재료진도코드
				      ,GOODS_GRADE                  -- 제품등급
				      ,YD_MTL_W_GP                  -- 야드재료폭구분
				      ,YD_MTL_T_GP                  -- 야드재료두께구분
				      ,YD_MTL_L_GP                  -- 야드재료길이구분
				      ,YD_MTL_T                     -- 야드재료두께
				      ,YD_MTL_W                     -- 야드재료폭
				      ,YD_MTL_L                     -- 야드재료길이
				      ,YD_MTL_WT                    -- 야드재료중량
				      ,YD_COIL_OUTDIA_GRP_GP        -- 야드코일외경군구분
				      ,REHEAT_SLAB_GP               -- 재열재구분
				      ,COOL_DONE_GP                 -- 냉각완료구분
				      ,YD_PILING_CD                 -- 야드Piling코드
				      ,YD_BOOK_OUT_LOC              -- 야드BookOut위치
				      ,PL_RCPT_LN_GP                -- 후판입고Line구분
				      ,FRTOMOVE_ORD_DATE            -- 이송지시일자
				      ,URGENT_FRTOMOVE_WORD_GP      -- 긴급이송작업지시구분
				      ,SPOS_WLOC_CD                 -- 발지개소코드
				      ,ARR_WLOC_CD                  -- 착지개소코드
				      ,YD_CAR_USE_GP                -- 야드차량사용구분
				      ,CAR_NO                       -- 차량번호
				      ,TRN_EQP_CD                   -- 운송장비코드
				      ,CAR_KIND                     -- 차량종류
				      ,TRANS_EQUIPMENT_TYPE         -- 운송장비Type
				      ,CARD_NO                      -- 카드번호
				      ,YD_CAR_SCH_ID                -- 야드차량스케쥴ID
				      ,YD_TCAR_SCH_ID               -- 야드대차스케쥴ID
				      ,YD_WBOOK_ID                  -- 야드작업예약ID
				      ,YD_CRN_SCH_ID                -- 야드크레인스케쥴ID
				      ,YD_SCH_CD                    -- 야드스케쥴코드
				      ,YD_SCH_ST_GP                 -- 야드스케쥴기동구분
				      ,YD_SCH_REQ_GP                -- 야드스케쥴요청구분
				      ,YD_SCH_PRIOR                 -- 야드스케쥴우선순위
				      ,YD_WBOOK_DT                  -- 야드작업예약일시
				      ,YD_AID_WRK_YN                -- 야드보조작업여부
				      ,YD_TO_LOC_DCSN_MTD           -- 야드To위치결정방법
				      ,YD_TO_LOC_GUIDE              -- 야드To위치Guide
				      ,YD_SCH_DT                    -- 야드스케쥴일시
				      ,YD_WORD_DT                   -- 야드작업지시일시
				      ,YD_UP_WO_LOC                 -- 야드권상지시위치
				      ,YD_UP_WO_LAYER               -- 야드권상지시단
				      ,YD_UP_WR_LOC                 -- 야드권상실적위치
				      ,YD_UP_WR_LAYER               -- 야드권상실적단
				      ,YD_UP_WRK_ACT_GP             -- 야드권상작업수행구분
				      ,YD_UP_CMPL_DT                -- 야드권상완료일시
				      ,YD_DN_WO_LOC                 -- 야드권하지시위치
				      ,YD_DN_WO_LAYER               -- 야드권하지시단
				      ,YD_DN_WR_LOC                 -- 야드권하실적위치
				      ,YD_DN_WR_LAYER               -- 야드권하실적단
				      ,YD_DN_WRK_ACT_GP             -- 야드권하작업수행구분
				      ,YD_DN_CMPL_DT                -- 야드권하완료일시
				      ,YD_WRK_HDS_DD                -- 야드작업계상일자
				      ,YD_WRK_DUTY                  -- 야드작업근
				      ,YD_WRK_PARTY                 -- 야드작업조
				      ,YD_CARLD_LEV_LOC             -- 야드상차출발위치
				      ,YD_CARLD_LEV_DT              -- 야드상차출발일시
				      ,YD_CARLD_PNT_WO_DT           -- 야드상차Point지시일시
				      ,YD_PNT_CD1                   -- 야드포인트코드1
				      ,YD_PNT_CD2                   -- 야드포인트코드2
				      ,YD_CARLD_WRK_BOOK_ID         -- 야드상차작업예약ID
				      ,YD_CARLD_SCH_REQ_GP          -- 야드상차스케쥴요청구분
				      ,YD_CARLD_STOP_LOC            -- 야드상차정지위치
				      ,YD_CARLD_ARR_DT              -- 야드상차도착일시
				      ,YD_CARLD_ST_DT               -- 야드상차개시일시
				      ,YD_CARLD_CMPL_DT             -- 야드상차완료일시
				      ,YD_CARLD_WRK_ACT_GP          -- 야드상차작업수행구분
				      ,YD_CARLD_CHK_DT              -- 야드상차검수일시
				      ,YD_CARUD_LEV_DT              -- 야드하차출발일시
				      ,YD_CARUD_PNT_WO_DT           -- 야드하차Point지시일시
				      ,YD_PNT_CD3                   -- 야드포인트코드3
				      ,YD_PNT_CD4                   -- 야드포인트코드4
				      ,YD_CARUD_WRK_BOOK_ID         -- 야드하차작업예약ID
				      ,YD_CARUD_STOP_LOC            -- 야드하차정지위치
				      ,YD_CARUD_SCH_REQ_GP          -- 야드하차스케쥴요청구분
				      ,YD_CARUD_ARR_DT              -- 야드하차도착일시
				      ,YD_CARUD_CHK_DT              -- 야드하차검수일시
				      ,YD_CARUD_ST_DT               -- 야드하차개시일시
				      ,YD_CARUD_CMPL_DT             -- 야드하차완료일시
				      ,YD_CARUD_WRK_ACT_GP          -- 야드하차작업수행구분
				      ,YD_TRN_WRK_DELY_CD           -- 야드운송작업지연코드
				      ,YD_EQP_ID                    -- 야드설비ID
				      ,BOOK_OUT_RESN                -- Book-Out원인
				      ,PL_BOOK_INOUT_GP             -- 후판북인아웃구분
				)
				VALUES
				(
				       :V_YD_WRK_HIST_ID                                    -- 야드작업이력ID
				      ,:V_REGISTER                                          -- 등록자
				      ,SYSDATE                                              -- 등록일시
				      ,:V_MODIFIER                                          -- 수정자
				      ,SYSDATE                                              -- 수정일시
				      ,:V_DEL_YN                                            -- 삭제유무
				      ,:V_YD_GP                                             -- 야드구분
				      ,:V_STL_NO                                            -- 재료번호
				      ,:V_YD_GNT_GP                                         -- 재료수불구분
				      ,:V_STL_APPEAR_GP                                     -- 재료외형구분
				      ,:V_ITEMNAME_CD                                       -- 품명코드
				      ,:V_ORD_YEOJAE_GP                                     -- 주문여재구분
				      ,:V_ORD_NO                                            -- 주문번호
				      ,:V_ORD_DTL                                           -- 주문행번
				      ,:V_STLKIND_CD                                        -- 강종코드
				      ,:V_SPEC_ABBSYM                                       -- 규격약호
				      ,:V_ORD_GP                                            -- 수주구분
				      ,:V_STL_PROG_CD                                       -- 재료진도코드
				      ,:V_GOODS_GRADE                                       -- 제품등급
				      ,:V_YD_MTL_W_GP                                       -- 야드재료폭구분
				      ,:V_YD_MTL_T_GP                                       -- 야드재료두께구분
				      ,:V_YD_MTL_L_GP                                       -- 야드재료길이구분
				      ,:V_YD_MTL_T                                          -- 야드재료두께
				      ,:V_YD_MTL_W                                          -- 야드재료폭
				      ,:V_YD_MTL_L                                          -- 야드재료길이
				      ,:V_YD_MTL_WT                                         -- 야드재료중량
				      ,:V_YD_COIL_OUTDIA_GRP_GP                             -- 야드코일외경군구분
				      ,:V_REHEAT_SLAB_GP                                    -- 재열재구분
				      ,:V_COOL_DONE_GP                                      -- 냉각완료구분
				      ,:V_YD_PILING_CD                                      -- 야드Piling코드
				      ,:V_YD_BOOK_OUT_LOC                                   -- 야드BookOut위치
				      ,:V_PL_RCPT_LN_GP                                     -- 후판입고Line구분
				      ,:V_FRTOMOVE_ORD_DATE                                 -- 이송지시일자
				      ,:V_URGENT_FRTOMOVE_WORD_GP                           -- 긴급이송작업지시구분
				      ,:V_SPOS_WLOC_CD                                      -- 발지개소코드
				      ,:V_ARR_WLOC_CD                                       -- 착지개소코드
				      ,:V_YD_CAR_USE_GP                                     -- 야드차량사용구분
				      ,:V_CAR_NO                                            -- 차량번호
				      ,:V_TRN_EQP_CD                                        -- 운송장비코드
				      ,:V_CAR_KIND                                          -- 차량종류
				      ,:V_TRANS_EQUIPMENT_TYPE                              -- 운송장비Type
				      ,:V_CARD_NO                                           -- 카드번호
				      ,:V_YD_CAR_SCH_ID                                     -- 야드차량스케쥴ID
				      ,:V_YD_TCAR_SCH_ID                                    -- 야드대차스케쥴ID
				      ,:V_YD_WBOOK_ID                                       -- 야드작업예약ID
				      ,:V_YD_CRN_SCH_ID                                     -- 야드크레인스케쥴ID
				      ,:V_YD_SCH_CD                                         -- 야드스케쥴코드
				      ,:V_YD_SCH_ST_GP                                      -- 야드스케쥴기동구분
				      ,:V_YD_SCH_REQ_GP                                     -- 야드스케쥴요청구분
				      ,:V_YD_SCH_PRIOR                                      -- 야드스케쥴우선순위
				      ,TO_DATE(:V_YD_WBOOK_DT,'YYYYMMDDHH24MISS')           -- 야드작업예약일시
				      ,:V_YD_AID_WRK_YN                                     -- 야드보조작업여부
				      ,:V_YD_TO_LOC_DCSN_MTD                                -- 야드To위치결정방법
				      ,:V_YD_TO_LOC_GUIDE                                   -- 야드To위치Guide
				      ,TO_DATE(:V_YD_SCH_DT,'YYYYMMDDHH24MISS')             -- 야드스케쥴일시
				      ,TO_DATE(:V_YD_WORD_DT,'YYYYMMDDHH24MISS')            -- 야드작업지시일시
				      ,:V_YD_UP_WO_LOC                                      -- 야드권상지시위치
				      ,:V_YD_UP_WO_LAYER                                    -- 야드권상지시단
				      ,:V_YD_UP_WR_LOC                                      -- 야드권상실적위치
				      ,:V_YD_UP_WR_LAYER                                    -- 야드권상실적단
				      ,:V_YD_UP_WRK_ACT_GP                                  -- 야드권상작업수행구분
				      ,TO_DATE(:V_YD_UP_CMPL_DT,'YYYYMMDDHH24MISS')         -- 야드권상완료일시
				      ,:V_YD_DN_WO_LOC                                      -- 야드권하지시위치
				      ,:V_YD_DN_WO_LAYER                                    -- 야드권하지시단
				      ,:V_YD_DN_WR_LOC                                      -- 야드권하실적위치
				      ,:V_YD_DN_WR_LAYER                                    -- 야드권하실적단
				      ,:V_YD_DN_WRK_ACT_GP                                  -- 야드권하작업수행구분
				      ,TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS')         -- 야드권하완료일시
				      ,:V_YD_WRK_HDS_DD                                     -- 야드작업계상일자
				      ,:V_YD_WRK_DUTY                                       -- 야드작업근
				      ,:V_YD_WRK_PARTY                                      -- 야드작업조
				      ,:V_YD_CARLD_LEV_LOC                                  -- 야드상차출발위치
				      ,TO_DATE(:V_YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')       -- 야드상차출발일시
				      ,TO_DATE(:V_YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')    -- 야드상차Point지시일시
				      ,:V_YD_PNT_CD1                                        -- 야드포인트코드1
				      ,:V_YD_PNT_CD2                                        -- 야드포인트코드2
				      ,:V_YD_CARLD_WRK_BOOK_ID                              -- 야드상차작업예약ID
				      ,:V_YD_CARLD_SCH_REQ_GP                               -- 야드상차스케쥴요청구분
				      ,:V_YD_CARLD_STOP_LOC                                 -- 야드상차정지위치
				      ,TO_DATE(:V_YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')       -- 야드상차도착일시
				      ,TO_DATE(:V_YD_CARLD_ST_DT,'YYYYMMDDHH24MISS')        -- 야드상차개시일시
				      ,TO_DATE(:V_YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS')      -- 야드상차완료일시
				      ,:V_YD_CARLD_WRK_ACT_GP                               -- 야드상차작업수행구분
				      ,TO_DATE(:V_YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS')       -- 야드상차검수일시
				      ,TO_DATE(:V_YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')       -- 야드하차출발일시
				      ,TO_DATE(:V_YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS')    -- 야드하차Point지시일시
				      ,:V_YD_PNT_CD3                                        -- 야드포인트코드3
				      ,:V_YD_PNT_CD4                                        -- 야드포인트코드4
				      ,:V_YD_CARUD_WRK_BOOK_ID                              -- 야드하차작업예약ID
				      ,:V_YD_CARUD_STOP_LOC                                 -- 야드하차정지위치
				      ,:V_YD_CARUD_SCH_REQ_GP                               -- 야드하차스케쥴요청구분
				      ,TO_DATE(:V_YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')       -- 야드하차도착일시
				      ,TO_DATE(:V_YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS')       -- 야드하차검수일시
				      ,TO_DATE(:V_YD_CARUD_ST_DT,'YYYYMMDDHH24MISS')        -- 야드하차개시일시
				      ,TO_DATE(:V_YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS')      -- 야드하차완료일시
				      ,:V_YD_CARUD_WRK_ACT_GP                               -- 야드하차작업수행구분
				      ,:V_YD_TRN_WRK_DELY_CD                                -- 야드운송작업지연코드
				      ,:V_YD_EQP_ID                                         -- 야드설비ID
				      ,:V_BOOK_OUT_RESN                                     -- Book-Out원인
				      ,:V_PL_BOOK_INOUT_GP                                  -- 후판북인아웃구분
				)
				*/
				intRtnVal = ydWrkHistDao.insYdWrkHist(recCrnStock);

				if (intRtnVal <= 0) {
					sMsg = "재료번호(" + sStlNo + ")에 대한 INSERT가 실패하였습니다.";
				//	ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR);
					throw new DAOException(sMsg);
				}

			} // end of for

		}  catch(Exception e) {
			sMsg = "Exception 발생 >>>> " + e.getMessage();
			ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.ERROR, logId);
		//	throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	// end try catch문

		sMsg = "[" + sOperationName + "] --------------------- 종료";
		ydLogUtils.putLogNew(SZ_SESSION_NAME, sMethodName, sMsg, JPlateYdConst.DEBUG, logId);
	}
}