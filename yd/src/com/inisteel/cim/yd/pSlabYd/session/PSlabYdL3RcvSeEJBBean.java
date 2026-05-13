/**
 * @(#)PSlabYdL3RcvSeEJBBean
 *
 * @version          V1.00
 * @author           염용선
 * @date             2020/05/06
 *
 * @description      Slab야드 L3수신 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2020/05/06   염용선      염용선      최초 등록
 */
package com.inisteel.cim.yd.pSlabYd.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.or.common.util.CmnUtil;
import com.inisteel.cim.yd.message.MessageSenderTalk;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;

/**
 *      [A] 클래스명 : Slab야드 L3수신 처리
 *
 * @ejb.bean name="PSlabYdL3RcvSeEJB" jndi-name="PSlabYdL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class PSlabYdL3RcvSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private PSlabYdUtils  slabUtils = new PSlabYdUtils();
	private PSlabYdCommDAO  commDao = new PSlabYdCommDAO();
	private PSlabYdComm    slabComm = new PSlabYdComm();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

		


	/**
	 *      [A] 오퍼레이션명 : (후판)압연지시확정(CTYDJ031)
	 *      염용선 2020-10-28
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCTYDJ031(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "압연지시확정[PSlabYdL3RcvSeEJB.rcvCTYDJ031] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		String rcvSeq	  = slabUtils.trim(rcvMsg.getFieldString("CT_RCV_SEQ"   )); //지시ID(NEW)
		String ptopPlntGp = slabUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP" )); //조업공장구분
		String msgId      = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		
		//수신 항목 값
		String modGp      = slabUtils.trim(rcvMsg.getFieldString("MOD_GP"       )); //수정구분
		String chgWoFrPnt = slabUtils.trim(rcvMsg.getFieldString("CHG_WO_FR_PNT")); //장입지시FromPoint
		String chgWoToPnt = slabUtils.trim(rcvMsg.getFieldString("CHG_WO_TO_PNT")); //장입지시ToPoint
		String stlNo      = slabUtils.trim(rcvMsg.getFieldString("SLAB_NO"      )); //재료(Slab)번호
		String endGp      = slabUtils.trim(rcvMsg.getFieldString("END_GP"       )); //종료구분
		String modifier   = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
		String woMtlCnt   = slabUtils.trim(rcvMsg.getFieldString("CT_WO_MTL_CNT")); //건수
		String millWoMd   = slabUtils.trim(rcvMsg.getFieldString("MILL_WO_MD")); //예정지시구분 :S
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			
			
			String ydGp       = "";	//야드구분
			String msgId3     = "";	//크레인작업계획 I/F ID
			if ("".equals(modifier)) { modifier = msgId; }

			if ("PA".equals(ptopPlntGp)) {
				ydGp     = "D";			//야드구분(후판Slab야드)
				msgId3   = "YDY3L003";	//크레인작업계획 I/F ID
				methodNm = "1후판" + methodNm;
			} else if ("PB".equals(ptopPlntGp)) {
				ydGp     = "D";			//야드구분(후판Slab야드)
				msgId3   = "YDY3L003";	//크레인작업계획 I/F ID
				methodNm = "2후판" + methodNm;
			}

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(modGp)) {
				slabUtils.printLog(logId, "수정구분(MOD_GP) 값이 없습니다.", "SL");
				throw new Exception("수정구분(MOD_GP) 값이 없습니다.");
			} else if ("".equals(ptopPlntGp)) {
				slabUtils.printLog(logId, "조업공장구분(PTOP_PLNT_GP) 값이 없습니다.", "SL");
				throw new Exception("조업공장구분(PTOP_PLNT_GP) 값이 없습니다.");
			}

			JDTORecord jrRtn = null;
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("PTOP_PLNT_GP", ptopPlntGp); //조업공장구분
			jrParam.setField("MODIFIER"    , modifier  ); //수정자

			if ("D".equals(modGp)) {
				/**********************************************************
				* 2. 지시취소이면 압연지시결번실적 처리
				**********************************************************/
				if ("".equals(stlNo) || stlNo.length() < 9) {
					slabUtils.printLog(logId, "잘못된 재료번호[" + stlNo + "] 입니다.", "SL");
					throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
				}

				/**********************************************************
				* 2.1 저장품 수정
				**********************************************************/
				jrParam.setField("STL_NO", stlNo); //재료번호

				//저장품 등록
				//commDao.insSlabYd("Stock", jrParam);
				/*
				 * --저장품등록(재료번호) 
					MERGE INTO TB_YD_STOCK ST USING (
					SELECT SC.*
					      ,DECODE(SC.YD_AIM_RT_GP2,'E2',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EL' ELSE 'E2' END),
					                            'E4',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EM' ELSE 'E4' END),
					                            'E5',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EN' ELSE 'E5' END),
					              SC.YD_AIM_RT_GP2) AS YD_AIM_RT_GP
					FROM
					    (SELECT SC.SLAB_NO                   AS STL_NO           --재료번호
					          ,:V_MODIFIER                  AS MODIFIER         --수정자
					          ,SYSDATE                      AS MOD_DDTT         --수정일시
					          ,'N'                          AS DEL_YN           --삭제유무
					          ,SC.PTOP_PLNT_GP                                  --조업공장구분
					          ,SF_SLAB_YD_MTL_ITEM(SC.STL_APPEAR_GP, SC.SLAB_WO_RT_CD) AS YD_MTL_ITEM --야드재료품목
					          ,SC.ITEMNAME_CD                                   --품명코드
					          ,'2'                          AS YD_MTL_STAT      --야드재료상태(현물)
					          ,SC.CURR_PROG_CD              AS STL_PROG_CD      --재료진도코드
					          ,SC.ORD_YEOJAE_GP                                 --주문여재구분
					          ,SC.ORD_NO                                        --주문번호
					          ,SC.ORD_DTL                                       --주문행번
					          ,SF_SLAB_YD_AIM_RT_GP (SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
					                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
					                                 SC.STL_APPEAR_GP   ,
					                                 (CASE WHEN SC.MSLAB_RPR_MC_GP='G' AND SC.SCARFING_DONE_YN='N' AND SC.SCARFING_YN='Y' THEN 'AG' ELSE (SELECT BANK_WORK_RT     
					                                                                               FROM VW_YD_F_MSLABWO A
					                                                                              WHERE A.PLN_MSLAB_NO=SC.PLAN_SLAB_NO
					                                                                                   AND ROWNUM<=1
					                                                                              )
					                                      END )
					                                 ) AS YD_AIM_RT_GP2  --야드목표행선구분
					          ,SF_SLAB_YD_AIM_YD_GP (NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE
					                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
					                                                        ELSE 'A' END
					                                              END),
					                                 SC.CURR_PROG_CD    , FT.ARR_WLOC_CD                          ) AS YD_AIM_YD_GP  --야드목표야드구분
					          ,SF_SLAB_YD_AIM_BAY_GP(NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE 
					                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
					                                                        ELSE 'A' END
					                                              END),
					                                 SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , FT.ARR_WLOC_CD     ,
					                                 SC.SCARFING_YN     , SC.SCARFING_DONE_YN, SC.HCR_GP          ,
					                                 SC.ORD_YEOJAE_GP   , SC.STL_APPEAR_GP                        ) AS YD_AIM_BAY_GP --야드목표동구분
					          ,SF_SLAB_YD_STK_LOT_TP(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
					                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
					                                 SC.STL_APPEAR_GP                                             ) AS YD_STK_LOT_TP --야드산적LotType
					          ,SF_SLAB_YD_STK_LOT_CD(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
					                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
					                                 SC.STL_APPEAR_GP   , OC.PROD_DUE_DATE   , SC.STACK_LOT_NO    ,
					                                 MO.YD_CHG_NO       , FT.ARR_WLOC_CD                          ) AS YD_STK_LOT_CD --야드산적Lot코드
					          ,SC.STL_APPEAR_GP                                 --재료외형구분
					          ,SC.PLNT_PROC_CD                                  --공장공정코드
					          ,SC.OVERALL_STAMP_GRADE                           --종합판정등급
					          ,SC.SLAB_T                    AS YD_MTL_T         --야드재료두께
					          ,SC.SLAB_W                    AS YD_MTL_W         --야드재료폭
					          ,SC.SLAB_LEN                  AS YD_MTL_L         --야드재료길이
					          ,SC.SLAB_WT                   AS YD_MTL_WT        --야드재료중량
					          ,SC.SLAB_WO_RT_CD                                 --Slab지시행선코드
					          ,SC.ORD_HCR_GP                                    --설계HCR구분
					          ,SC.HCR_GP                                        --HCR구분
					          ,NVL(SC.SCARFING_YN     ,'N') AS SCARFING_YN      --Scarfing여부
					          ,NVL(SC.SCARFING_DONE_YN,'N') AS SCARFING_DONE_YN --Scarfing완료유무
					          ,SC.HANDSCARFING_YN                               --HandScarfing유무
					          ,SC.WO_MSLAB_RPR_MTD                              --지시주편손질방법
					          ,SC.REHEAT_SLAB_GP                                --재열재구분
					          ,MO.ROLL_UNIT_GP                                  --Roll단위구분
					          ,MO.ROLL_UNIT_NAME                                --Roll단위명
					          ,MO.REFUR_CHG_LOT_NO                              --가열로장입Lot번호
					          ,MO.REFUR_CHG_PLN_SERNO                           --가열로장입예정일련번호
					          ,OC.ORD_GP                                        --수주구분
					          ,OC.CUST_CD                                       --고객코드
					          ,OC.DEST_CD                                       --목적지코드
					          ,SC.DEMANDER_CD                                   --수요가코드
					          ,OC.GOODS_GRADE                                   --제품등급
					          ,OC.YD_RCPT_STR_LOC                               --야드입고저장위치
					          ,OC.DIST_DUE_DATE                                 --출하기한일
					          ,OC.EXPORT_SHIP_SET_NO                            --수출재배선번호
					          ,OC.DELIVER_TERM_CD                               --인도조건코드
					          ,OC.DETAIL_ARR_CD                                 --상세착지코드
					          ,HC.STLKIND_CD                                    --강종코드
					          ,SC.SPEC_ABBSYM                                   --규격약호
					          ,SC.CCM_NO                    AS CC_CCM_NO        --연주CCM번호
					          ,SC.PARENT_SLAB_NO            AS MMATL_FEE_NO     --모재료번호
					          ,FT.WO_CAR_PLNT_PROC_CD                           --지시차공장공정코드
					          ,FT.ORD_BEFO_PROG_CD                              --지시전진도코드
					          ,FT.ARR_WLOC_CD                                   --착지개소코드
					          ,FT.URGENT_FRTOMOVE_WORD_GP                       --긴급이송작업지시구분
					          ,SC.SCARFING_DEPTH                                --Scarfing깊이
					          ,MO.YD_CHG_NO                                     --야드장입순위
					          ,MO.PL_MPL_NO                                     --후판날판번호
					          ,SL.YD_STK_COL_GP                                 --야드적치열구분
					          ,SL.YD_STK_BED_NO                                 --야드적치Bed번호
					          ,SL.YD_STK_LYR_NO                                 --야드적치단번호
					      FROM VW_YD_SLABCOMM SC
					          ,TB_PT_HEATCOMM HC
					          ,TB_PT_OSCOMM   OC
					          ,(SELECT STL_NO
					                  ,YD_STK_COL_GP
					                  ,YD_STK_BED_NO
					                  ,YD_STK_LYR_NO
					                  ,SUBSTR(YD_STK_COL_GP,1,1) AS YD_GP
					              FROM TB_YD_STKLYR
					             WHERE STL_NO = :V_STL_NO
					               AND YD_STK_LYR_MTL_STAT IN ('C','U')) SL
					          ,(SELECT STL_NO
					                  ,WO_CAR_PLNT_PROC_CD
					                  ,ORD_BEFO_PROG_CD
					                  ,ARR_WLOC_CD
					                  ,URGENT_FRTOMOVE_WORD_GP
					              FROM TB_PT_STLFRTOMOVE FT
					             WHERE STL_NO = :V_STL_NO
					               AND TRANSWORD_SEQNO = (SELECT MAX(MS.TRANSWORD_SEQNO)
					                                        FROM TB_PT_STLFRTOMOVE MS
					                                       WHERE MS.STL_NO = FT.STL_NO
					                                         AND MS.FRTOMOVE_STAT_CD IN ('1','3'))) FT --(이송지시확정,야드수신완료)
					          ,(SELECT STL_NO
					                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
					                  ,ROLL_UNIT_NAME
					                  ,REFUR_CHG_LOT_NO
					                  ,REFUR_CHG_PLN_SERNO
					                  ,YD_CHG_NO
					                  ,NULL AS PL_MPL_NO 
					              FROM USRCTA.TB_CT_L_HRMILLWO --CT_열연압연작업지시
					             WHERE STL_NO = :V_STL_NO
					               AND CT_MILL_SPEC_WRK_STAT_GP >= '3'
					             UNION ALL
					            SELECT STL_NO
					                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
					                  ,ROLL_UNIT_NAME
					                  ,REFUR_CHG_LOT_NO
					                  ,REFUR_CHG_PLN_SERNO
					                  ,YD_CHG_NO
					                  ,PL_MPL_NO
					              FROM USRCTA.TB_CT_N_PLMPLWO
					             WHERE STL_NO = :V_STL_NO
					               AND CT_MILL_SPEC_WRK_STAT_GP >= '3') MO
					     WHERE SC.SLAB_NO = SL.STL_NO(+)
					       AND SC.HEAT_NO = HC.HEAT_NO(+)
					       AND SC.ORD_NO  = OC.ORD_NO(+)
					       AND SC.ORD_DTL = OC.ORD_DTL(+)
					       AND SC.SLAB_NO = FT.STL_NO(+)
					       AND SC.SLAB_NO = MO.STL_NO(+)
					       AND SC.SLAB_NO = :V_STL_NO
					       AND ROWNUM     = 1
					    ) SC
					) DD ON (ST.STL_NO = DD.STL_NO)
					WHEN MATCHED THEN UPDATE SET
						    ST.MODIFIER                = DD.MODIFIER
					       ,ST.MOD_DDTT                = DD.MOD_DDTT
					       ,ST.DEL_YN                  = DD.DEL_YN
					       ,ST.PTOP_PLNT_GP            = DD.PTOP_PLNT_GP
					       ,ST.YD_MTL_ITEM             = DD.YD_MTL_ITEM
					       ,ST.ITEMNAME_CD             = DD.ITEMNAME_CD
					       ,ST.YD_MTL_STAT             = DD.YD_MTL_STAT
					       ,ST.STL_PROG_CD             = DD.STL_PROG_CD
					       ,ST.ORD_YEOJAE_GP           = DD.ORD_YEOJAE_GP
					       ,ST.ORD_NO                  = DD.ORD_NO
					       ,ST.ORD_DTL                 = DD.ORD_DTL
					       ,ST.YD_AIM_RT_GP            = DD.YD_AIM_RT_GP
					       ,ST.YD_AIM_YD_GP            = DD.YD_AIM_YD_GP
					       ,ST.YD_AIM_BAY_GP           = DD.YD_AIM_BAY_GP
					       ,ST.YD_STK_LOT_TP           = DD.YD_STK_LOT_TP
					       ,ST.YD_STK_LOT_CD           = DD.YD_STK_LOT_CD
					       ,ST.STL_APPEAR_GP           = DD.STL_APPEAR_GP
					       ,ST.PLNT_PROC_CD            = DD.PLNT_PROC_CD
					       ,ST.OVERALL_STAMP_GRADE     = DD.OVERALL_STAMP_GRADE
					       ,ST.YD_MTL_T                = DD.YD_MTL_T
					       ,ST.YD_MTL_W                = DD.YD_MTL_W
					       ,ST.YD_MTL_L                = DD.YD_MTL_L
					       ,ST.YD_MTL_WT               = DD.YD_MTL_WT
					       ,ST.SLAB_WO_RT_CD           = DD.SLAB_WO_RT_CD
					       ,ST.ORD_HCR_GP              = DD.ORD_HCR_GP
					       ,ST.HCR_GP                  = DD.HCR_GP
					       ,ST.SCARFING_YN             = DD.SCARFING_YN
					       ,ST.SCARFING_DONE_YN        = DD.SCARFING_DONE_YN
					       ,ST.HANDSCARFING_YN         = DD.HANDSCARFING_YN
					       ,ST.WO_MSLAB_RPR_MTD        = DD.WO_MSLAB_RPR_MTD
					       ,ST.REHEAT_SLAB_GP          = DD.REHEAT_SLAB_GP
					       ,ST.ROLL_UNIT_GP            = DD.ROLL_UNIT_GP
					       ,ST.ROLL_UNIT_NAME          = DD.ROLL_UNIT_NAME
					       ,ST.REFUR_CHG_LOT_NO        = DD.REFUR_CHG_LOT_NO
					       ,ST.REFUR_CHG_PLN_SERNO     = DD.REFUR_CHG_PLN_SERNO
					       ,ST.ORD_GP                  = DD.ORD_GP
					       ,ST.CUST_CD                 = DD.CUST_CD
					       ,ST.DEST_CD                 = DD.DEST_CD
					       ,ST.DEMANDER_CD             = DD.DEMANDER_CD
					       ,ST.GOODS_GRADE             = DD.GOODS_GRADE
					       ,ST.YD_RCPT_STR_LOC         = DD.YD_RCPT_STR_LOC
					       ,ST.DIST_DUE_DATE           = DD.DIST_DUE_DATE
					       ,ST.EXPORT_SHIP_SET_NO      = DD.EXPORT_SHIP_SET_NO
					       ,ST.DELIVER_TERM_CD         = DD.DELIVER_TERM_CD
					       ,ST.DETAIL_ARR_CD           = DD.DETAIL_ARR_CD
					       ,ST.STLKIND_CD              = DD.STLKIND_CD
					       ,ST.SPEC_ABBSYM             = DD.SPEC_ABBSYM
					       ,ST.CC_CCM_NO               = DD.CC_CCM_NO
					       ,ST.MMATL_FEE_NO            = DD.MMATL_FEE_NO
					       ,ST.WO_CAR_PLNT_PROC_CD     = DD.WO_CAR_PLNT_PROC_CD
					       ,ST.ORD_BEFO_PROG_CD        = DD.ORD_BEFO_PROG_CD
					       ,ST.ARR_WLOC_CD             = DD.ARR_WLOC_CD
					       ,ST.URGENT_FRTOMOVE_WORD_GP = DD.URGENT_FRTOMOVE_WORD_GP
					       ,ST.SCARFING_DEPTH          = DD.SCARFING_DEPTH
					       ,ST.YD_CHG_NO               = DD.YD_CHG_NO
					       ,ST.PL_MPL_NO               = DD.PL_MPL_NO
					       ,ST.YD_STK_COL_GP           = DD.YD_STK_COL_GP
					       ,ST.YD_STK_BED_NO           = DD.YD_STK_BED_NO
					       ,ST.YD_STK_LYR_NO           = DD.YD_STK_LYR_NO
					WHEN NOT MATCHED THEN
					INSERT (ST.STL_NO                 , ST.REGISTER           , ST.REG_DDTT           , ST.MODIFIER        , ST.MOD_DDTT     ,
					        ST.DEL_YN                 , ST.PTOP_PLNT_GP       , ST.YD_MTL_ITEM        , ST.ITEMNAME_CD     , ST.YD_MTL_STAT  ,
					        ST.STL_PROG_CD            , ST.ORD_YEOJAE_GP      , ST.ORD_NO             , ST.ORD_DTL         , ST.YD_AIM_RT_GP ,
					        ST.YD_AIM_YD_GP           , ST.YD_AIM_BAY_GP      , ST.YD_STK_LOT_TP      , ST.YD_STK_LOT_CD   , ST.STL_APPEAR_GP,
					        ST.PLNT_PROC_CD           , ST.OVERALL_STAMP_GRADE, ST.YD_MTL_T           , ST.YD_MTL_W        , ST.YD_MTL_L     ,
					        ST.YD_MTL_WT              , ST.SLAB_WO_RT_CD      , ST.ORD_HCR_GP         , ST.HCR_GP          , ST.SCARFING_YN  ,
					        ST.SCARFING_DONE_YN       , ST.HANDSCARFING_YN    , ST.WO_MSLAB_RPR_MTD   , ST.REHEAT_SLAB_GP  , ST.ROLL_UNIT_GP ,
					        ST.ROLL_UNIT_NAME         , ST.REFUR_CHG_LOT_NO   , ST.REFUR_CHG_PLN_SERNO, ST.ORD_GP          , ST.CUST_CD      ,
					        ST.DEST_CD                , ST.DEMANDER_CD        , ST.GOODS_GRADE        , ST.YD_RCPT_STR_LOC , ST.DIST_DUE_DATE,
					        ST.EXPORT_SHIP_SET_NO     , ST.DELIVER_TERM_CD    , ST.DETAIL_ARR_CD      , ST.STLKIND_CD      , ST.SPEC_ABBSYM  ,
					        ST.CC_CCM_NO              , ST.MMATL_FEE_NO       , ST.WO_CAR_PLNT_PROC_CD, ST.ORD_BEFO_PROG_CD, ST.ARR_WLOC_CD  ,
					        ST.URGENT_FRTOMOVE_WORD_GP, ST.SCARFING_DEPTH     , ST.YD_CHG_NO          , ST.PL_MPL_NO       , ST.YD_STK_COL_GP,
					        ST.YD_STK_BED_NO          , ST.YD_STK_LYR_NO)
					VALUES (DD.STL_NO                 , DD.MODIFIER           , DD.MOD_DDTT           , DD.MODIFIER        , DD.MOD_DDTT     ,
					        DD.DEL_YN                 , DD.PTOP_PLNT_GP       , DD.YD_MTL_ITEM        , DD.ITEMNAME_CD     , DD.YD_MTL_STAT  ,
					        DD.STL_PROG_CD            , DD.ORD_YEOJAE_GP      , DD.ORD_NO             , DD.ORD_DTL         , DD.YD_AIM_RT_GP ,
					        DD.YD_AIM_YD_GP           , DD.YD_AIM_BAY_GP      , DD.YD_STK_LOT_TP      , DD.YD_STK_LOT_CD   , DD.STL_APPEAR_GP,
					        DD.PLNT_PROC_CD           , DD.OVERALL_STAMP_GRADE, DD.YD_MTL_T           , DD.YD_MTL_W        , DD.YD_MTL_L     ,
					        DD.YD_MTL_WT              , DD.SLAB_WO_RT_CD      , DD.ORD_HCR_GP         , DD.HCR_GP          , DD.SCARFING_YN  ,
					        DD.SCARFING_DONE_YN       , DD.HANDSCARFING_YN    , DD.WO_MSLAB_RPR_MTD   , DD.REHEAT_SLAB_GP  , DD.ROLL_UNIT_GP ,
					        DD.ROLL_UNIT_NAME         , DD.REFUR_CHG_LOT_NO   , DD.REFUR_CHG_PLN_SERNO, DD.ORD_GP          , DD.CUST_CD      ,
					        DD.DEST_CD                , DD.DEMANDER_CD        , DD.GOODS_GRADE        , DD.YD_RCPT_STR_LOC , DD.DIST_DUE_DATE,
					        DD.EXPORT_SHIP_SET_NO     , DD.DELIVER_TERM_CD    , DD.DETAIL_ARR_CD      , DD.STLKIND_CD      , DD.SPEC_ABBSYM  ,
					        DD.CC_CCM_NO              , DD.MMATL_FEE_NO       , DD.WO_CAR_PLNT_PROC_CD, DD.ORD_BEFO_PROG_CD, DD.ARR_WLOC_CD  ,
					        DD.URGENT_FRTOMOVE_WORD_GP, DD.SCARFING_DEPTH     , DD.YD_CHG_NO          , DD.PL_MPL_NO       , DD.YD_STK_COL_GP,
					        DD.YD_STK_BED_NO          , DD.YD_STK_LYR_NO)
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.insSlabYdStock", logId, methodNm, "저장품(TB_YD_STOCK) 등록");				
				
				
				/**********************************************************
				* 2.1 저장품 메시지 비우기
				**********************************************************/
				JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
				jrParam2.setField("SNDBK_GP_ETC", ""); 
				jrParam2.setField("MODIFIER", 	 "");
				jrParam2.setField("STL_NO", 	  stlNo);
				//수정
				/*
				 * UPDATE USRYDA.TB_YD_STOCK
					SET SNDBK_GP_ETC=:V_SNDBK_GP_ETC
					 , SNDBK_REGISTER =:V_MODIFIER
					 , SNDBK_REG_DDTT =SYSDATE
					WHERE STL_NO=:V_STL_NO
				*/
				commDao.update(jrParam2, "com.inisteel.cim.yd.pslabyd.dao.PSlabL3RcvSeEJB.updMessage", logId, methodNm, "메시지 수정");
				
				/**********************************************************
				* 2.2 저장품제원, 크레인작업계획 전문을 전송
				**********************************************************/
				jrParam.setField("YD_GP"          , ydGp); //야드구분
				jrParam.setField("YD_INFO_SYNC_CD", "5" ); //야드정보동기화코드(지정저장품)

				//저장품제원(YDY3L002) 전송Data 조회
				jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L002", jrParam));
				
				/**********************************************************
				* 3.2 후판압연지시 취소이면서 마지만 구분자일경우..
				**********************************************************/
				if ("YDY3L003".equals(msgId3) && "*".equals(endGp)) {
					//후판제품야드 압연지시확정(YDYDJ031)처리 전송
					rcvMsg.setField("JMS_TC_CD", "YDYDJ031"); //JMSTC코드
					
					jrRtn = slabUtils.addSndData(jrRtn,rcvMsg);
				}
				
			} else {
				/**********************************************************
				* 3. 압연지시확정 처리
				**********************************************************/
				if ("".equals(chgWoFrPnt)) {
					slabUtils.printLog(logId, "장입지시FromPoint(CHG_WO_FR_PNT) 값이 없습니다.", "SL");
					throw new Exception("장입지시FromPoint(CHG_WO_FR_PNT) 값이 없습니다.");
				} else if ("".equals(chgWoToPnt)) {
					slabUtils.printLog(logId, "장입지시ToPoint(CHG_WO_TO_PNT) 값이 없습니다.", "SL");
					throw new Exception("장입지시ToPoint(CHG_WO_TO_PNT) 값이 없습니다.");
				}
				
				/**********************************************************
				* 3.1 저장품 등록
				**********************************************************/
				jrParam.setField("CHG_WO_FR_PNT", chgWoFrPnt); //장입지시FromPoint
				jrParam.setField("CHG_WO_TO_PNT", chgWoToPnt); //장입지시ToPoint
	
				if(!"S".equals(millWoMd)){
					//저장품(기존지시) 수정
					//rcv3Dao.updCTYDJ031("StockOld", jrParam);
					/*
					 * --압연지시확정 저장품(기존지시) 수정 - 
						MERGE INTO TB_YD_STOCK ST USING (
						WITH TEMP_PARAM AS (
						SELECT V_PTOP_PLNT_GP
						     , DECODE(V_PTOP_PLNT_GP,'HC','A','PA','D','PB','D') AS V_YD_GP
						 FROM (    
						SELECT :V_PTOP_PLNT_GP AS V_PTOP_PLNT_GP FROM DUAL
						      ) A
						 
						)
						SELECT //**+ INDEX(SC IX_PT_SLABCOMM_18)**
						      ST.STL_NO
						      ,DECODE(SC.SLAB_WO_RT_CD,'PA',DECODE(SC.HCR_GP,'C','B6','B5')
						                              ,'PB',DECODE(SC.HCR_GP,'C','BE','BD')) AS YD_AIM_RT_GP
						      ,'SB' AS YD_STK_LOT_TP --Slab지시대기
						      ,'Y'||SC.SLAB_WO_RT_CD||CASE WHEN LENGTH(SC.STACK_LOT_NO) > 13
						                                   THEN SUBSTR(SC.STACK_LOT_NO,3)
						                                   ELSE SC.STACK_LOT_NO END AS YD_STK_LOT_CD
						  FROM TB_YD_STOCK    ST
						      ,TB_PT_SLABCOMM SC
						 WHERE ST.STL_NO        = SC.SLAB_NO
						   AND ST.SLAB_WO_RT_CD = (SELECT V_PTOP_PLNT_GP FROM TEMP_PARAM)
						   AND ST.REFUR_CHG_LOT_NO LIKE ST.SLAB_WO_RT_CD||'%'
						   AND ST.DEL_YN        = 'N'
						   AND SC.RECORD_PROG_STAT = '2'
						   AND SC.YD_STR_LOC LIKE (SELECT V_YD_GP FROM TEMP_PARAM)  ||'%'
						) DD ON (ST.STL_NO = DD.STL_NO)
						WHEN MATCHED THEN UPDATE SET
							 ST.MODIFIER            = :V_MODIFIER
						    ,ST.MOD_DDTT            = SYSDATE
						    ,ST.YD_AIM_RT_GP        = DD.YD_AIM_RT_GP
						    ,ST.YD_STK_LOT_TP       = DD.YD_STK_LOT_TP
						    ,ST.YD_STK_LOT_CD       = DD.YD_STK_LOT_CD
						    ,ST.YD_CHG_NO           = NULL
						    ,ST.ROLL_UNIT_GP        = NULL
						    ,ST.ROLL_UNIT_NAME      = NULL
						    ,ST.REFUR_CHG_LOT_NO    = NULL
						    ,ST.REFUR_CHG_PLN_SERNO = NULL
						    ,ST.PL_MPL_NO           = NULL 
					 */
					
					commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL3RcvSeEJB.updCTYDJ031StockOld", logId, methodNm, "압연지시확정 저장품(기존지시) 수정");				
					
				}

				//저장품(신규지시) 수정
				//rcv3Dao.updCTYDJ031("StockNew", jrParam);
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL3RcvSeEJB.updCTYDJ031StockNew", logId, methodNm, "저장품(신규지시) 수정");				
				
				/**********************************************************
				* 3.2 후판압연지시 확정이면 후판제품야드 압연지시확정, 후판Slab야드 저장품제원 전문을 전송
				**********************************************************/
				if ("YDY3L003".equals(msgId3)) {
					//후판제품야드 동별저장계획(YDYDJ031)처리 전송
					rcvMsg.setField("JMS_TC_CD", "YDYDJ031"); //JMSTC코드
					
					jrRtn = slabUtils.addSndData(jrRtn,rcvMsg);
				}
				
				//크레인작업계획 전송을 위하여
				endGp = "*";
			}

			/**********************************************************
			* 4. 크레인작업계획(YDY3L003) 전문을 전송
			**********************************************************/
			if ("*".equals(endGp)) {
				JDTORecordSet jsL003 = commDao.getMsgL2("YDY3L003", jrParam);
				if(jsL003.size() <= 0){
					//throw new Exception("조업공장구분에 맞는 데이터 없음 [" + ptopPlntGp + "]");
					slabUtils.printLog(logId, "조업공장구분에 맞는 데이터 없음 [" + ptopPlntGp + "]", "S-");

				}else{
					jrRtn = slabUtils.addSndData(jrRtn,jsL003 );
				}
				
			}
			
			
			
			/**********************************************************
			* 5. 생산통제 작업여부 TC전송(YDCTJ037)
			**********************************************************/
          //전송할 Data가 있으면 전송 처리
			JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
			recPara2.setField("JMS_TC_CD","YDCTJ037");
			recPara2.setField("JMS_TC_CREATE_DDTT"		, slabUtils.getCurDate("yyyyMMddHHmmss"));
			
			if ("D".equals(modGp)) {
				recPara2.setField("MILL_WO_MD","C"); //취소인 경우 처리
			//}else if("I".equals(modGp) && "1".equals(woMtlCnt)){
			}else if("S".equals(millWoMd)){
				recPara2.setField("MILL_WO_MD","K");	//후판에정지시재 인 경우(2차 전단 시 발생)
			}else{
				recPara2.setField("MILL_WO_MD","B");
			}
			
			recPara2.setField("CHG_WO_FR_PNT",chgWoFrPnt); 
			recPara2.setField("CHG_WO_TO_PNT",chgWoToPnt); 
			recPara2.setField("SLAB_NO",""); 
			recPara2.setField("PLAN_SLAB_NO",""); 
			recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID 
			recPara2.setField("CT_WO_MTL_CNT",woMtlCnt); 
			recPara2.setField("WO_SND_YN", "Y"); 
				
			EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
			sndConn.trx("sndJMSInfo", new Class[] { JDTORecord.class }, new Object[] { recPara2 });
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			try {
				//생산통제 IT담당자에게 SMS전송 
				JDTORecord recPara2 = JDTORecordFactory.getInstance().create();				 
				recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID
				recPara2.setField("PTOP_PLNT_GP", ptopPlntGp); // PK: 공장구분 
				recPara2.setField("MODIFIER2", 	 msgId);
				recPara2.setField("CHG_WO_FR_PNT", 	 chgWoFrPnt);
				recPara2.setField("CHG_WO_TO_PNT", 	 chgWoToPnt);
				recPara2.setField("CT_WO_MTL_CNT",	 woMtlCnt); 

				EJBConnector SMSMSG = new EJBConnector("default", "PSlabYdL3RcvSeEJB", this);
				SMSMSG.trx( "rcvCTYDSMS" , new Class[] { JDTORecord.class }, new Object[] { recPara2 });
			
			} catch (Exception ex) {
				slabUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, ex));
			}
			
			throw e;
		} catch (Exception e) {
			try {
					//생산통제 IT담당자에게 SMS전송 
					JDTORecord recPara2 = JDTORecordFactory.getInstance().create();					
					recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID
					recPara2.setField("PTOP_PLNT_GP", ptopPlntGp); // PK: 공장구분 
					recPara2.setField("MODIFIER2", 	 msgId);
					recPara2.setField("CHG_WO_FR_PNT", 	 chgWoFrPnt);
					recPara2.setField("CHG_WO_TO_PNT", 	 chgWoToPnt);
					recPara2.setField("CT_WO_MTL_CNT",	 woMtlCnt); 


					EJBConnector SMSMSG = new EJBConnector("default", "PSlabYdL3RcvSeEJB", this);
					SMSMSG.trx( "rcvCTYDSMS" , new Class[] { JDTORecord.class }, new Object[] { recPara2 });
				
			} catch (Exception ex) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, ex));
			}
				
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	
	/**
	 *      [A] 오퍼레이션명 : 압연지시 실패 시 SMS 문자 전송 기능
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCTYDSMS(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C열연압연지시확정 SMS 문자 전송 기능[PSlabYdL3RcvSeEJB.rcvCTYDSMS] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;
		
		try {
			
			slabUtils.printLog(logId, methodNm, "S+");
			
			String rcvSeq       = slabUtils.trim(rcvMsg.getFieldString("CT_RCV_SEQ"   )); 
			String ptopPlntGp   = slabUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP" )); 
			String msgId      	= slabUtils.trim(rcvMsg.getFieldString("MODIFIER2"    )); 
			String chgWoFrPnt 	= slabUtils.trim(rcvMsg.getFieldString("CHG_WO_FR_PNT")); //장입지시FromPoint
			String chgWoToPnt 	= slabUtils.trim(rcvMsg.getFieldString("CHG_WO_TO_PNT")); //장입지시ToPoint
			String woMtlCnt   	= slabUtils.trim(rcvMsg.getFieldString("CT_WO_MTL_CNT")); //건수
			
			//생산통제 IT담당자에게 SMS전송
			String rtnMsg = "";
			String rtnMsg1 = "";
			String subJect = "";
			JDTORecord recPara1 = JDTORecordFactory.getInstance().create();	 
			JDTORecordSet smsListSet; 
			
			/**********************************************************
			* 1. SMS 전송 목록 조회
			**********************************************************/ 
			recPara1.setField("REGISTER" , msgId);
			smsListSet = commDao.select(recPara1, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.getCTSMSLog", logId, methodNm, "CT SMS 전송 목록 조회"); 
			
			if("CTYDJ033".equals(msgId)){
				rtnMsg =msgId+ " 2열연 압연지시 야드 수신 중 오류 발생\n 지시번호:" + rcvSeq;
				subJect = "2열연 압연지시 야드 수신 중 오류 발생";
			}else{
				if("PA".equals(ptopPlntGp)){
					rtnMsg =msgId+ " 1후판 압연지시 야드 수신 중 오류 발생\n 지시번호:" + rcvSeq;
					subJect = "1후판 압연지시 야드 수신 중 오류 발생";
				}else{
					rtnMsg =msgId+ " 2후판 압연지시 야드 수신 중 오류 발생\n 지시번호:" + rcvSeq;
					subJect = "2후판 압연지시 야드 수신 중 오류 발생";
				}
				
			}
			
			/**********************************************************
			* 2. SMS 전송
			**********************************************************/
			if(smsListSet != null && smsListSet.size() > 0) {
				
				for(int i=0; i<smsListSet.size(); i++) {
					JDTORecord recPara = JDTORecordFactory.getInstance().create();
//					recPara.setField("FROM_PHONE_NO", "0416801616");	
//					recPara.setField("TO_PHONE_NO"  , smsListSet.getRecord(i).getFieldString("HANDPHONE_NO")); // 010-XXXX-XXXX
//					recPara.setField("TO_CONTENT"   , rtnMsg);
//					rtnMsg1 = slabComm.updSmsMsgSend(recPara); // SMS 송신 
				
					
                    MessageSenderTalk    sender = new MessageSenderTalk();				
					
                    recPara.setField("PHONE_NUM", new String(smsListSet.getRecord(i).getFieldString("HANDPHONE_NO")));
                    recPara.setField("TMPL_CD", new String("CM1"));
                    recPara.setField("SND_MSG", new String("[현대제철 공지사항]\n" + rtnMsg));
                    recPara.setField("SUBJECT", new String(subJect));
                    recPara.setField("SMS_SND_NUM", new String("0416801667"));
                    recPara.setField("RECV_ID","1521612");
                    recPara.setField("GROUP_ID","KaKao");
                    recPara.setField("PROGRAM_ID","udttalk");
					sender.sendTalk(recPara);
					
				}
			}
			
		 
			/**********************************************************
			* 3. 생산통제 UPDATE
			**********************************************************/
			//생산통제 테이블 update
//			JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
//			recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID
//			recPara2.setField("PTOP_PLNT_GP", ptopPlntGp); // PK: 공장구분
//			recPara2.setField("WO_SND_YN2", "N");
//			recPara2.setField("MODIFIER2", 	 msgId);
			
//			commDao.update(recPara2, "com.inisteel.cim.yd.common.dao.YdCommDAO.updCtMillwoidxTbl", logId, methodNm, "작업지시 성공여부 update");
			
			 
			/**********************************************************
			* 4. 생산통제 작업여부 TC전송(YDCTJ037)
			**********************************************************/
          //전송할 Data가 있으면 전송 처리
			JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
			recPara2.setField("JMS_TC_CD","YDCTJ037");
			recPara2.setField("JMS_TC_CREATE_DDTT"		, slabUtils.getCurDate("yyyyMMddHHmmss"));
			
			if ("".equals(rcvSeq)) {
				recPara2.setField("MILL_WO_MD","C"); //취소인 경우 처리
			}else{
				recPara2.setField("MILL_WO_MD","B");
			}
			recPara2.setField("CHG_WO_FR_PNT",chgWoFrPnt); 
			recPara2.setField("CHG_WO_TO_PNT",chgWoToPnt); 
			recPara2.setField("SLAB_NO",""); 
			recPara2.setField("PLAN_SLAB_NO",""); 
			recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID 
			recPara2.setField("CT_WO_MTL_CNT",woMtlCnt); 
			recPara2.setField("WO_SND_YN", "N"); 
				
			EJBConnector sndConn = new EJBConnector("default", "PSlabYdCommEJB", this);
			sndConn.trx("sndJMSInfo", new Class[] { JDTORecord.class }, new Object[] { recPara2 });
			 
			
			
			slabUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 2후판압연지시결번실적(PPYDJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPPYDJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "2후판압연지시결번실적[PSlabYdL3RcvSeEJB.rcvPPYDJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String modifier = "PPYDJ001";
		try {
			slabUtils.printLog(logId, methodNm, "S+");
			JDTORecord jrRtn   = slabUtils.getParam(logId, methodNm, modifier);		//Return Value
			JDTORecord  recInTemp   = JDTORecordFactory.getInstance().create();
			//slabUtils.printLog(logId, "처리 내용 없음 : 재료번호[" + slabUtils.trim(rcvMsg.getFieldString("STL_NO")) + "]", "SL");
			String stlNo 				= slabUtils.trim(rcvMsg.getFieldString("STL_NO"));
			String missnoDt 			= slabUtils.trim(rcvMsg.getFieldString("MISSNO_DT"));//결번날짜
			String missnoCauseCd		= slabUtils.trim(rcvMsg.getFieldString("MISSNO_CAUSE_CD"));//결번원인코드
			
			slabUtils.printLog(logId, "재료번호: ["+stlNo+"] 결번원인코드: ["+missnoCauseCd+"]", "SL");
			
			//결번원인 코드가 A0중량이상, A3소재길이이상 인 경우에만 처리
			if(!"A0".equals(missnoCauseCd) && !"A3".equals(missnoCauseCd)){
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "[getYdStock]data not found!" );	
				slabUtils.printLog(logId, "재료번호: ["+stlNo+"] 결번원인코드: ["+missnoCauseCd+"] 는 자동 이상재 처리대상 아님.", "SL");
				return jrRtn;
			}
			
			String abmtlRsnCd = "";
			
			if("A0".equals(missnoCauseCd)){
				abmtlRsnCd = "K05";
			}
			else if("A3".equals(missnoCauseCd)){
				abmtlRsnCd = "K07";//길이 이상 --신설
			}
			
			String szCurDateTime 	= slabUtils.getCurDate("yyyyMMddHHmmss");
    		String sCurDate 	= szCurDateTime.substring(0, 8);
			
			JDTORecord  jrParam   = slabUtils.getParam(logId, methodNm, modifier);
			jrParam.setField("STL_NO", stlNo);

			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdStock_PIDEV", logId, methodNm, "저장품데이터확인");
			
			if(rsResult.size()<=0){
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "[getYdStock]data not found!" );	
				return jrRtn;
			}
			
			rsResult.first();
			recInTemp = rsResult.getRecord();
			
			String slabWoRtCd	   = slabUtils.paraRecChkNull(recInTemp,"SLAB_WO_RT_CD");
			
			recInTemp   = JDTORecordFactory.getInstance().create();
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			
			String currProgCd = "";
			String status1 = "";
			String status2 = "";
			/*
			SELECT 
				   A.CURR_PROG_CD  
				  ,B.CT_MILL_SPEC_WRK_STAT_GP
				  ,DECODE(B.CT_MILL_SCH_WRK_STAT_GP,'H','1',B.CT_MILL_SCH_WRK_STAT_GP) AS CT_MILL_SCH_WRK_STAT_GP 
				FROM  TB_PT_SLABCOMM A,
				      TB_CT_M_PLMPLSPEC B   --날판사양
				WHERE A.SLAB_NO = B.STL_NO(+)
				  AND A.SLAB_NO = :V_STL_NO
			 */ 
			rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getStockTbCtMPlmplspec", logId, methodNm, "저장품데이터확인");

			if(rsResult.size() > 0){
				rsResult.first();
				recInTemp = rsResult.getRecord();
				
				currProgCd	= slabUtils.paraRecChkNull(recInTemp,"CURR_PROG_CD");
				status1  	= slabUtils.paraRecChkNull(recInTemp,"CT_MILL_SPEC_WRK_STAT_GP");
				status2  	= slabUtils.paraRecChkNull(recInTemp,"CT_MILL_SCH_WRK_STAT_GP");
			}
			
			if(currProgCd.equals("C")){
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "CURR_PROG_CD :"+currProgCd );	
				return jrRtn;
			}
			
			if(currProgCd.equals("B")){
				if(status1.equals("1") && status2.equals("1")){
				}else{
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "CURR_PROG_CD :"+currProgCd );	
					return jrRtn;
				}
			}
			
			jrParam.setField("STL_NO", stlNo);							
			jrParam.setField("YD_ABMTL_RSN_CD"   , abmtlRsnCd);
			jrParam.setField("YD_ABMTL_HD_MTD_CD", "");
			jrParam.setField("YD_ABMTL_GRD"      , "");
			jrParam.setField("YD_ABMTL_REM"      , "");
			jrParam.setField("YD_ABMTL_ASGN_DD"  , sCurDate);
			/*
			 * UPDATE TB_YD_STOCK
				   SET YD_ABMTL_RSN_CD    = :V_YD_ABMTL_RSN_CD
				      ,YD_ABMTL_HD_MTD_CD = :V_YD_ABMTL_HD_MTD_CD
				      ,YD_ABMTL_GRD = :V_YD_ABMTL_GRD
				      ,YD_ABMTL_REM = :V_YD_ABMTL_REM
				      ,YD_ABMTL_ASGN_DD = :V_YD_ABMTL_ASGN_DD
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				WHERE STL_NO = :V_STL_NO
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updatecSlabYdStkHoldGp_02", logId, methodNm, "이상재 등록 수정");
			
			  
		    commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.insYdAbSlabHist", logId, methodNm, "이상재 등록 HIST등록");
		

			JDTORecord outRec035 	= slabUtils.getParam(logId, methodNm, modifier);	// YDCTJ035
			
			/* 2021.11.30 기준적용 ********************************
				1. 모든 결함은 YDCTJ035(생산통제) 송신
				2. H, K 결함을 제외하고 YDYDJ429(충당보류 SET) 추가 송신
			**************************************************/
			
			// 1. 모든 결함은 YDCTJ035(생산통제) 송신
			outRec035.setField("JMS_TC_CD"         , "YDCTJ035");
			outRec035.setField("JMS_TC_CREATE_DDTT", slabUtils.getCurDate("yyyyMMddHHmmss"));
			outRec035.setField("SLAB_NO"           , stlNo);
			outRec035.setField("PTOP_PLNT_GP"      , slabWoRtCd);
			outRec035.setField("AB_OCCR_RSN_CD"    , abmtlRsnCd);
			outRec035.setField("REGISTER"          , modifier);
			outRec035.setField("REG_DDTT"          , slabUtils.getCurDate("yyyyMMddHHmmss"));
			outRec035.setField("PROCESS_GP"        , "1");
			jrRtn = slabUtils.addSndData(jrRtn, outRec035);
			
				
			
			slabUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 후판압연실적(PRYDJ002, PPYDJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPRYDJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "후판압연실적[PSlabYdL3RcvSeEJB.rcvPRYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); 
			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg);  
			String stlNo        = slabUtils.trim(rcvMsg.getFieldString("STL_NO"        )); //재료번호
			String ptopPlntGp   = slabUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP"  )); //조업공장구분
			String reheatSlabGp = slabUtils.trim(rcvMsg.getFieldString("REHEAT_SLAB_GP")); //재열재구분
			String sModifier     = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"    )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			//조업공장구분 결정 : 생산통제 압연지시확정 전문 수신 시 값이 있음
			if ("".equals(ptopPlntGp) && ptopPlntGp.length() != 2) {
				if (msgId.startsWith("PR")) {
					ptopPlntGp = "PA";	//1후판
				} else if (msgId.startsWith("PP")) {
					ptopPlntGp = "PB";	//2후판
				}
			}

			if (!"".equals(ptopPlntGp)) {
				if ("PA".equals(ptopPlntGp)) {
					methodNm = "1" + methodNm;
				} else if ("PB".equals(ptopPlntGp)) {
					methodNm = "2" + methodNm;
				}
			}

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			
			if ("".equals(stlNo) || stlNo.length() < 9) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			} else if ("".equals(ptopPlntGp)) {
				throw new Exception("조업공장구분 값을 알 수 없습니다.");
			}
			/*
			 * 1 혹은 S 값이  원형재열재
			   2 혹은 J 값이  절단재열재
			   M : 미스롤
			*/
			if (!"1".equals(reheatSlabGp)) {
				slabUtils.printLog(logId, "원형재열재[재열재구분:" + reheatSlabGp + "]가 아니므로 종료합니다.", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				return null;
			}

			/**********************************************************
			* 2. 저장품 수정
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("STL_NO"  , stlNo   ); //재료번호

			//저장품 등록
			//commDao.insSlabYd("Stock", jrParam);
			/*
			 * --저장품등록(재료번호) 
				MERGE INTO TB_YD_STOCK ST USING (
				SELECT SC.*
				      ,DECODE(SC.YD_AIM_RT_GP2,'E2',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EL' ELSE 'E2' END),
				                            'E4',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EM' ELSE 'E4' END),
				                            'E5',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EN' ELSE 'E5' END),
				              SC.YD_AIM_RT_GP2) AS YD_AIM_RT_GP
				FROM
				    (SELECT SC.SLAB_NO                   AS STL_NO           --재료번호
				          ,:V_MODIFIER                  AS MODIFIER         --수정자
				          ,SYSDATE                      AS MOD_DDTT         --수정일시
				          ,'N'                          AS DEL_YN           --삭제유무
				          ,SC.PTOP_PLNT_GP                                  --조업공장구분
				          ,SF_SLAB_YD_MTL_ITEM(SC.STL_APPEAR_GP, SC.SLAB_WO_RT_CD) AS YD_MTL_ITEM --야드재료품목
				          ,SC.ITEMNAME_CD                                   --품명코드
				          ,'2'                          AS YD_MTL_STAT      --야드재료상태(현물)
				          ,SC.CURR_PROG_CD              AS STL_PROG_CD      --재료진도코드
				          ,SC.ORD_YEOJAE_GP                                 --주문여재구분
				          ,SC.ORD_NO                                        --주문번호
				          ,SC.ORD_DTL                                       --주문행번
				          ,SF_SLAB_YD_AIM_RT_GP (SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
				                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
				                                 SC.STL_APPEAR_GP   ,
				                                 (CASE WHEN SC.MSLAB_RPR_MC_GP='G' AND SC.SCARFING_DONE_YN='N' AND SC.SCARFING_YN='Y' THEN 'AG' ELSE (SELECT BANK_WORK_RT     
				                                                                               FROM VW_YD_F_MSLABWO A
				                                                                              WHERE A.PLN_MSLAB_NO=SC.PLAN_SLAB_NO
				                                                                                   AND ROWNUM<=1
				                                                                              )
				                                      END )
				                                 ) AS YD_AIM_RT_GP2  --야드목표행선구분
				          ,SF_SLAB_YD_AIM_YD_GP (NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE
				                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
				                                                        ELSE 'A' END
				                                              END),
				                                 SC.CURR_PROG_CD    , FT.ARR_WLOC_CD                          ) AS YD_AIM_YD_GP  --야드목표야드구분
				          ,SF_SLAB_YD_AIM_BAY_GP(NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE 
				                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
				                                                        ELSE 'A' END
				                                              END),
				                                 SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , FT.ARR_WLOC_CD     ,
				                                 SC.SCARFING_YN     , SC.SCARFING_DONE_YN, SC.HCR_GP          ,
				                                 SC.ORD_YEOJAE_GP   , SC.STL_APPEAR_GP                        ) AS YD_AIM_BAY_GP --야드목표동구분
				          ,SF_SLAB_YD_STK_LOT_TP(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
				                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
				                                 SC.STL_APPEAR_GP                                             ) AS YD_STK_LOT_TP --야드산적LotType
				          ,SF_SLAB_YD_STK_LOT_CD(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
				                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
				                                 SC.STL_APPEAR_GP   , OC.PROD_DUE_DATE   , SC.STACK_LOT_NO    ,
				                                 MO.YD_CHG_NO       , FT.ARR_WLOC_CD                          ) AS YD_STK_LOT_CD --야드산적Lot코드
				          ,SC.STL_APPEAR_GP                                 --재료외형구분
				          ,SC.PLNT_PROC_CD                                  --공장공정코드
				          ,SC.OVERALL_STAMP_GRADE                           --종합판정등급
				          ,SC.SLAB_T                    AS YD_MTL_T         --야드재료두께
				          ,SC.SLAB_W                    AS YD_MTL_W         --야드재료폭
				          ,SC.SLAB_LEN                  AS YD_MTL_L         --야드재료길이
				          ,SC.SLAB_WT                   AS YD_MTL_WT        --야드재료중량
				          ,SC.SLAB_WO_RT_CD                                 --Slab지시행선코드
				          ,SC.ORD_HCR_GP                                    --설계HCR구분
				          ,SC.HCR_GP                                        --HCR구분
				          ,NVL(SC.SCARFING_YN     ,'N') AS SCARFING_YN      --Scarfing여부
				          ,NVL(SC.SCARFING_DONE_YN,'N') AS SCARFING_DONE_YN --Scarfing완료유무
				          ,SC.HANDSCARFING_YN                               --HandScarfing유무
				          ,SC.WO_MSLAB_RPR_MTD                              --지시주편손질방법
				          ,SC.REHEAT_SLAB_GP                                --재열재구분
				          ,MO.ROLL_UNIT_GP                                  --Roll단위구분
				          ,MO.ROLL_UNIT_NAME                                --Roll단위명
				          ,MO.REFUR_CHG_LOT_NO                              --가열로장입Lot번호
				          ,MO.REFUR_CHG_PLN_SERNO                           --가열로장입예정일련번호
				          ,OC.ORD_GP                                        --수주구분
				          ,OC.CUST_CD                                       --고객코드
				          ,OC.DEST_CD                                       --목적지코드
				          ,SC.DEMANDER_CD                                   --수요가코드
				          ,OC.GOODS_GRADE                                   --제품등급
				          ,OC.YD_RCPT_STR_LOC                               --야드입고저장위치
				          ,OC.DIST_DUE_DATE                                 --출하기한일
				          ,OC.EXPORT_SHIP_SET_NO                            --수출재배선번호
				          ,OC.DELIVER_TERM_CD                               --인도조건코드
				          ,OC.DETAIL_ARR_CD                                 --상세착지코드
				          ,HC.STLKIND_CD                                    --강종코드
				          ,SC.SPEC_ABBSYM                                   --규격약호
				          ,SC.CCM_NO                    AS CC_CCM_NO        --연주CCM번호
				          ,SC.PARENT_SLAB_NO            AS MMATL_FEE_NO     --모재료번호
				          ,FT.WO_CAR_PLNT_PROC_CD                           --지시차공장공정코드
				          ,FT.ORD_BEFO_PROG_CD                              --지시전진도코드
				          ,FT.ARR_WLOC_CD                                   --착지개소코드
				          ,FT.URGENT_FRTOMOVE_WORD_GP                       --긴급이송작업지시구분
				          ,SC.SCARFING_DEPTH                                --Scarfing깊이
				          ,MO.YD_CHG_NO                                     --야드장입순위
				          ,MO.PL_MPL_NO                                     --후판날판번호
				          ,SL.YD_STK_COL_GP                                 --야드적치열구분
				          ,SL.YD_STK_BED_NO                                 --야드적치Bed번호
				          ,SL.YD_STK_LYR_NO                                 --야드적치단번호
				      FROM VW_YD_SLABCOMM SC
				          ,TB_PT_HEATCOMM HC
				          ,TB_PT_OSCOMM   OC
				          ,(SELECT STL_NO
				                  ,YD_STK_COL_GP
				                  ,YD_STK_BED_NO
				                  ,YD_STK_LYR_NO
				                  ,SUBSTR(YD_STK_COL_GP,1,1) AS YD_GP
				              FROM TB_YD_STKLYR
				             WHERE STL_NO = :V_STL_NO
				               AND YD_STK_LYR_MTL_STAT IN ('C','U')) SL
				          ,(SELECT STL_NO
				                  ,WO_CAR_PLNT_PROC_CD
				                  ,ORD_BEFO_PROG_CD
				                  ,ARR_WLOC_CD
				                  ,URGENT_FRTOMOVE_WORD_GP
				              FROM TB_PT_STLFRTOMOVE FT
				             WHERE STL_NO = :V_STL_NO
				               AND TRANSWORD_SEQNO = (SELECT MAX(MS.TRANSWORD_SEQNO)
				                                        FROM TB_PT_STLFRTOMOVE MS
				                                       WHERE MS.STL_NO = FT.STL_NO
				                                         AND MS.FRTOMOVE_STAT_CD IN ('1','3'))) FT --(이송지시확정,야드수신완료)
				          ,(SELECT STL_NO
				                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
				                  ,ROLL_UNIT_NAME
				                  ,REFUR_CHG_LOT_NO
				                  ,REFUR_CHG_PLN_SERNO
				                  ,YD_CHG_NO
				                  ,NULL AS PL_MPL_NO
				              FROM USRCTA.TB_CT_L_HRMILLWO
				             WHERE STL_NO = :V_STL_NO
				               AND CT_MILL_SPEC_WRK_STAT_GP >= '3'
				             UNION ALL
				            SELECT STL_NO
				                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
				                  ,ROLL_UNIT_NAME
				                  ,REFUR_CHG_LOT_NO
				                  ,REFUR_CHG_PLN_SERNO
				                  ,YD_CHG_NO
				                  ,PL_MPL_NO
				              FROM USRCTA.TB_CT_N_PLMPLWO
				             WHERE STL_NO = :V_STL_NO
				               AND CT_MILL_SPEC_WRK_STAT_GP >= '3') MO
				     WHERE SC.SLAB_NO = SL.STL_NO(+)
				       AND SC.HEAT_NO = HC.HEAT_NO(+)
				       AND SC.ORD_NO  = OC.ORD_NO(+)
				       AND SC.ORD_DTL = OC.ORD_DTL(+)
				       AND SC.SLAB_NO = FT.STL_NO(+)
				       AND SC.SLAB_NO = MO.STL_NO(+)
				       AND SC.SLAB_NO = :V_STL_NO
				       AND ROWNUM     = 1
				    ) SC
				) DD ON (ST.STL_NO = DD.STL_NO)
				WHEN MATCHED THEN UPDATE SET
					    ST.MODIFIER                = DD.MODIFIER
				       ,ST.MOD_DDTT                = DD.MOD_DDTT
				       ,ST.DEL_YN                  = DD.DEL_YN
				       ,ST.PTOP_PLNT_GP            = DD.PTOP_PLNT_GP
				       ,ST.YD_MTL_ITEM             = DD.YD_MTL_ITEM
				       ,ST.ITEMNAME_CD             = DD.ITEMNAME_CD
				       ,ST.YD_MTL_STAT             = DD.YD_MTL_STAT
				       ,ST.STL_PROG_CD             = DD.STL_PROG_CD
				       ,ST.ORD_YEOJAE_GP           = DD.ORD_YEOJAE_GP
				       ,ST.ORD_NO                  = DD.ORD_NO
				       ,ST.ORD_DTL                 = DD.ORD_DTL
				       ,ST.YD_AIM_RT_GP            = DD.YD_AIM_RT_GP
				       ,ST.YD_AIM_YD_GP            = DD.YD_AIM_YD_GP
				       ,ST.YD_AIM_BAY_GP           = DD.YD_AIM_BAY_GP
				       ,ST.YD_STK_LOT_TP           = DD.YD_STK_LOT_TP
				       ,ST.YD_STK_LOT_CD           = DD.YD_STK_LOT_CD
				       ,ST.STL_APPEAR_GP           = DD.STL_APPEAR_GP
				       ,ST.PLNT_PROC_CD            = DD.PLNT_PROC_CD
				       ,ST.OVERALL_STAMP_GRADE     = DD.OVERALL_STAMP_GRADE
				       ,ST.YD_MTL_T                = DD.YD_MTL_T
				       ,ST.YD_MTL_W                = DD.YD_MTL_W
				       ,ST.YD_MTL_L                = DD.YD_MTL_L
				       ,ST.YD_MTL_WT               = DD.YD_MTL_WT
				       ,ST.SLAB_WO_RT_CD           = DD.SLAB_WO_RT_CD
				       ,ST.ORD_HCR_GP              = DD.ORD_HCR_GP
				       ,ST.HCR_GP                  = DD.HCR_GP
				       ,ST.SCARFING_YN             = DD.SCARFING_YN
				       ,ST.SCARFING_DONE_YN        = DD.SCARFING_DONE_YN
				       ,ST.HANDSCARFING_YN         = DD.HANDSCARFING_YN
				       ,ST.WO_MSLAB_RPR_MTD        = DD.WO_MSLAB_RPR_MTD
				       ,ST.REHEAT_SLAB_GP          = DD.REHEAT_SLAB_GP
				       ,ST.ROLL_UNIT_GP            = DD.ROLL_UNIT_GP
				       ,ST.ROLL_UNIT_NAME          = DD.ROLL_UNIT_NAME
				       ,ST.REFUR_CHG_LOT_NO        = DD.REFUR_CHG_LOT_NO
				       ,ST.REFUR_CHG_PLN_SERNO     = DD.REFUR_CHG_PLN_SERNO
				       ,ST.ORD_GP                  = DD.ORD_GP
				       ,ST.CUST_CD                 = DD.CUST_CD
				       ,ST.DEST_CD                 = DD.DEST_CD
				       ,ST.DEMANDER_CD             = DD.DEMANDER_CD
				       ,ST.GOODS_GRADE             = DD.GOODS_GRADE
				       ,ST.YD_RCPT_STR_LOC         = DD.YD_RCPT_STR_LOC
				       ,ST.DIST_DUE_DATE           = DD.DIST_DUE_DATE
				       ,ST.EXPORT_SHIP_SET_NO      = DD.EXPORT_SHIP_SET_NO
				       ,ST.DELIVER_TERM_CD         = DD.DELIVER_TERM_CD
				       ,ST.DETAIL_ARR_CD           = DD.DETAIL_ARR_CD
				       ,ST.STLKIND_CD              = DD.STLKIND_CD
				       ,ST.SPEC_ABBSYM             = DD.SPEC_ABBSYM
				       ,ST.CC_CCM_NO               = DD.CC_CCM_NO
				       ,ST.MMATL_FEE_NO            = DD.MMATL_FEE_NO
				       ,ST.WO_CAR_PLNT_PROC_CD     = DD.WO_CAR_PLNT_PROC_CD
				       ,ST.ORD_BEFO_PROG_CD        = DD.ORD_BEFO_PROG_CD
				       ,ST.ARR_WLOC_CD             = DD.ARR_WLOC_CD
				       ,ST.URGENT_FRTOMOVE_WORD_GP = DD.URGENT_FRTOMOVE_WORD_GP
				       ,ST.SCARFING_DEPTH          = DD.SCARFING_DEPTH
				       ,ST.YD_CHG_NO               = DD.YD_CHG_NO
				       ,ST.PL_MPL_NO               = DD.PL_MPL_NO
				       ,ST.YD_STK_COL_GP           = DD.YD_STK_COL_GP
				       ,ST.YD_STK_BED_NO           = DD.YD_STK_BED_NO
				       ,ST.YD_STK_LYR_NO           = DD.YD_STK_LYR_NO
				WHEN NOT MATCHED THEN
				INSERT (ST.STL_NO                 , ST.REGISTER           , ST.REG_DDTT           , ST.MODIFIER        , ST.MOD_DDTT     ,
				        ST.DEL_YN                 , ST.PTOP_PLNT_GP       , ST.YD_MTL_ITEM        , ST.ITEMNAME_CD     , ST.YD_MTL_STAT  ,
				        ST.STL_PROG_CD            , ST.ORD_YEOJAE_GP      , ST.ORD_NO             , ST.ORD_DTL         , ST.YD_AIM_RT_GP ,
				        ST.YD_AIM_YD_GP           , ST.YD_AIM_BAY_GP      , ST.YD_STK_LOT_TP      , ST.YD_STK_LOT_CD   , ST.STL_APPEAR_GP,
				        ST.PLNT_PROC_CD           , ST.OVERALL_STAMP_GRADE, ST.YD_MTL_T           , ST.YD_MTL_W        , ST.YD_MTL_L     ,
				        ST.YD_MTL_WT              , ST.SLAB_WO_RT_CD      , ST.ORD_HCR_GP         , ST.HCR_GP          , ST.SCARFING_YN  ,
				        ST.SCARFING_DONE_YN       , ST.HANDSCARFING_YN    , ST.WO_MSLAB_RPR_MTD   , ST.REHEAT_SLAB_GP  , ST.ROLL_UNIT_GP ,
				        ST.ROLL_UNIT_NAME         , ST.REFUR_CHG_LOT_NO   , ST.REFUR_CHG_PLN_SERNO, ST.ORD_GP          , ST.CUST_CD      ,
				        ST.DEST_CD                , ST.DEMANDER_CD        , ST.GOODS_GRADE        , ST.YD_RCPT_STR_LOC , ST.DIST_DUE_DATE,
				        ST.EXPORT_SHIP_SET_NO     , ST.DELIVER_TERM_CD    , ST.DETAIL_ARR_CD      , ST.STLKIND_CD      , ST.SPEC_ABBSYM  ,
				        ST.CC_CCM_NO              , ST.MMATL_FEE_NO       , ST.WO_CAR_PLNT_PROC_CD, ST.ORD_BEFO_PROG_CD, ST.ARR_WLOC_CD  ,
				        ST.URGENT_FRTOMOVE_WORD_GP, ST.SCARFING_DEPTH     , ST.YD_CHG_NO          , ST.PL_MPL_NO       , ST.YD_STK_COL_GP,
				        ST.YD_STK_BED_NO          , ST.YD_STK_LYR_NO)
				VALUES (DD.STL_NO                 , DD.MODIFIER           , DD.MOD_DDTT           , DD.MODIFIER        , DD.MOD_DDTT     ,
				        DD.DEL_YN                 , DD.PTOP_PLNT_GP       , DD.YD_MTL_ITEM        , DD.ITEMNAME_CD     , DD.YD_MTL_STAT  ,
				        DD.STL_PROG_CD            , DD.ORD_YEOJAE_GP      , DD.ORD_NO             , DD.ORD_DTL         , DD.YD_AIM_RT_GP ,
				        DD.YD_AIM_YD_GP           , DD.YD_AIM_BAY_GP      , DD.YD_STK_LOT_TP      , DD.YD_STK_LOT_CD   , DD.STL_APPEAR_GP,
				        DD.PLNT_PROC_CD           , DD.OVERALL_STAMP_GRADE, DD.YD_MTL_T           , DD.YD_MTL_W        , DD.YD_MTL_L     ,
				        DD.YD_MTL_WT              , DD.SLAB_WO_RT_CD      , DD.ORD_HCR_GP         , DD.HCR_GP          , DD.SCARFING_YN  ,
				        DD.SCARFING_DONE_YN       , DD.HANDSCARFING_YN    , DD.WO_MSLAB_RPR_MTD   , DD.REHEAT_SLAB_GP  , DD.ROLL_UNIT_GP ,
				        DD.ROLL_UNIT_NAME         , DD.REFUR_CHG_LOT_NO   , DD.REFUR_CHG_PLN_SERNO, DD.ORD_GP          , DD.CUST_CD      ,
				        DD.DEST_CD                , DD.DEMANDER_CD        , DD.GOODS_GRADE        , DD.YD_RCPT_STR_LOC , DD.DIST_DUE_DATE,
				        DD.EXPORT_SHIP_SET_NO     , DD.DELIVER_TERM_CD    , DD.DETAIL_ARR_CD      , DD.STLKIND_CD      , DD.SPEC_ABBSYM  ,
				        DD.CC_CCM_NO              , DD.MMATL_FEE_NO       , DD.WO_CAR_PLNT_PROC_CD, DD.ORD_BEFO_PROG_CD, DD.ARR_WLOC_CD  ,
				        DD.URGENT_FRTOMOVE_WORD_GP, DD.SCARFING_DEPTH     , DD.YD_CHG_NO          , DD.PL_MPL_NO       , DD.YD_STK_COL_GP,
				        DD.YD_STK_BED_NO          , DD.YD_STK_LYR_NO)
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.insSlabYdStock", logId, methodNm, "저장품(TB_YD_STOCK) 등록");				
			
			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , "D"); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송Data 조회
			jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY3L002", jrParam));

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 2후판압연실적(PPYDJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPPYDJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "2후판압연실적[PSlabYdL3RcvSeEJB.rcvPPYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//후판압연실적 처리
			return this.rcvPRYDJ002(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 재열재 정보 수정 및 이적(rcvPPYDJ016)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public GridData rcvPPYDJ016(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "재열재 정보 수정 및 이적[PSlabYdL3RcvSeEJB.rcvPPYDJ016] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg);  
			String plMtlNo      = slabUtils.trim(rcvMsg.getFieldString("PL_MTL_NO"));		//날판번호
			String sModifier    = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"    ));	//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			slabUtils.printLog(logId, "plMtlNo:["+plMtlNo+"], sModifier:["+sModifier+"]", "SL");
			
			//조회 및 등록 용
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("PL_MTL_NO", plMtlNo); //날판번호

			/**********************************************************
			* 1. 슬라브 번호 조회
			**********************************************************/
			//날판공통에서 슬라브 번호 조회
			/*슬라브 번호 조회 - com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getSlabNoToSIZSLABCOMM */
			/*
				SELECT SLAB_NO 
			  	  FROM USRPTA.TB_PT_SIZSLABCOMM
			 	 WHERE PL_MPL_NO = :V_PL_MTL_NO
			*/
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getSlabNoToSIZSLABCOMM", logId, methodNm, "슬라브번호 조회");
	    	
			String slabNo = ""; //야드목표야드구분
			
			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);
				slabNo = slabUtils.trim(jrChk.getFieldString("SLAB_NO"));	//슬라브번호
			} else {
				throw new Exception("조판번호에 해당하는 슬라브번호가 존재하지 않습니다.");
			}

			slabUtils.printLog(logId, "slabNo:["+slabNo+"]", "SL");

			/**********************************************************
			* 2. 저장품 수정
			**********************************************************/
			jrParam.setField("STL_NO", slabNo); 	//슬라브번호
			
			//저장품 Table 산적LotType 등 Upsert
			/*
			 * --저장품등록(재료번호) 
				MERGE INTO TB_YD_STOCK ST USING (
				SELECT SC.*
				      ,DECODE(SC.YD_AIM_RT_GP2,'E2',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EL' ELSE 'E2' END),
				                            'E4',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EM' ELSE 'E4' END),
				                            'E5',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EN' ELSE 'E5' END),
				              SC.YD_AIM_RT_GP2) AS YD_AIM_RT_GP
				FROM
				    (SELECT SC.SLAB_NO                   AS STL_NO           --재료번호
				          ,:V_MODIFIER                  AS MODIFIER         --수정자
				          ,SYSDATE                      AS MOD_DDTT         --수정일시
				          ,'N'                          AS DEL_YN           --삭제유무
				          ,SC.PTOP_PLNT_GP                                  --조업공장구분
				          ,SF_SLAB_YD_MTL_ITEM(SC.STL_APPEAR_GP, SC.SLAB_WO_RT_CD) AS YD_MTL_ITEM --야드재료품목
				          ,SC.ITEMNAME_CD                                   --품명코드
				          ,'2'                          AS YD_MTL_STAT      --야드재료상태(현물)
				          ,SC.CURR_PROG_CD              AS STL_PROG_CD      --재료진도코드
				          ,SC.ORD_YEOJAE_GP                                 --주문여재구분
				          ,SC.ORD_NO                                        --주문번호
				          ,SC.ORD_DTL                                       --주문행번
				          ,SF_SLAB_YD_AIM_RT_GP (SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
				                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
				                                 SC.STL_APPEAR_GP   ,
				                                 (CASE WHEN SC.MSLAB_RPR_MC_GP='G' AND SC.SCARFING_DONE_YN='N' AND SC.SCARFING_YN='Y' THEN 'AG' ELSE (SELECT BANK_WORK_RT     
				                                                                               FROM VW_YD_F_MSLABWO A
				                                                                              WHERE A.PLN_MSLAB_NO=SC.PLAN_SLAB_NO
				                                                                                   AND ROWNUM<=1
				                                                                              )
				                                      END )
				                                 ) AS YD_AIM_RT_GP2  --야드목표행선구분
				          ,SF_SLAB_YD_AIM_YD_GP (NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE
				                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
				                                                        ELSE 'A' END
				                                              END),
				                                 SC.CURR_PROG_CD    , FT.ARR_WLOC_CD                          ) AS YD_AIM_YD_GP  --야드목표야드구분
				          ,SF_SLAB_YD_AIM_BAY_GP(NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE 
				                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
				                                                        ELSE 'A' END
				                                              END),
				                                 SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , FT.ARR_WLOC_CD     ,
				                                 SC.SCARFING_YN     , SC.SCARFING_DONE_YN, SC.HCR_GP          ,
				                                 SC.ORD_YEOJAE_GP   , SC.STL_APPEAR_GP                        ) AS YD_AIM_BAY_GP --야드목표동구분
				          ,SF_SLAB_YD_STK_LOT_TP(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
				                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
				                                 SC.STL_APPEAR_GP                                             ) AS YD_STK_LOT_TP --야드산적LotType
				          ,SF_SLAB_YD_STK_LOT_CD(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
				                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
				                                 SC.STL_APPEAR_GP   , OC.PROD_DUE_DATE   , SC.STACK_LOT_NO    ,
				                                 MO.YD_CHG_NO       , FT.ARR_WLOC_CD                          ) AS YD_STK_LOT_CD --야드산적Lot코드
				          ,SC.STL_APPEAR_GP                                 --재료외형구분
				          ,SC.PLNT_PROC_CD                                  --공장공정코드
				          ,SC.OVERALL_STAMP_GRADE                           --종합판정등급
				          ,SC.SLAB_T                    AS YD_MTL_T         --야드재료두께
				          ,SC.SLAB_W                    AS YD_MTL_W         --야드재료폭
				          ,SC.SLAB_LEN                  AS YD_MTL_L         --야드재료길이
				          ,SC.SLAB_WT                   AS YD_MTL_WT        --야드재료중량
				          ,SC.SLAB_WO_RT_CD                                 --Slab지시행선코드
				          ,SC.ORD_HCR_GP                                    --설계HCR구분
				          ,SC.HCR_GP                                        --HCR구분
				          ,NVL(SC.SCARFING_YN     ,'N') AS SCARFING_YN      --Scarfing여부
				          ,NVL(SC.SCARFING_DONE_YN,'N') AS SCARFING_DONE_YN --Scarfing완료유무
				          ,SC.HANDSCARFING_YN                               --HandScarfing유무
				          ,SC.WO_MSLAB_RPR_MTD                              --지시주편손질방법
				          ,SC.REHEAT_SLAB_GP                                --재열재구분
				          ,MO.ROLL_UNIT_GP                                  --Roll단위구분
				          ,MO.ROLL_UNIT_NAME                                --Roll단위명
				          ,MO.REFUR_CHG_LOT_NO                              --가열로장입Lot번호
				          ,MO.REFUR_CHG_PLN_SERNO                           --가열로장입예정일련번호
				          ,OC.ORD_GP                                        --수주구분
				          ,OC.CUST_CD                                       --고객코드
				          ,OC.DEST_CD                                       --목적지코드
				          ,SC.DEMANDER_CD                                   --수요가코드
				          ,OC.GOODS_GRADE                                   --제품등급
				          ,OC.YD_RCPT_STR_LOC                               --야드입고저장위치
				          ,OC.DIST_DUE_DATE                                 --출하기한일
				          ,OC.EXPORT_SHIP_SET_NO                            --수출재배선번호
				          ,OC.DELIVER_TERM_CD                               --인도조건코드
				          ,OC.DETAIL_ARR_CD                                 --상세착지코드
				          ,HC.STLKIND_CD                                    --강종코드
				          ,SC.SPEC_ABBSYM                                   --규격약호
				          ,SC.CCM_NO                    AS CC_CCM_NO        --연주CCM번호
				          ,SC.PARENT_SLAB_NO            AS MMATL_FEE_NO     --모재료번호
				          ,FT.WO_CAR_PLNT_PROC_CD                           --지시차공장공정코드
				          ,FT.ORD_BEFO_PROG_CD                              --지시전진도코드
				          ,FT.ARR_WLOC_CD                                   --착지개소코드
				          ,FT.URGENT_FRTOMOVE_WORD_GP                       --긴급이송작업지시구분
				          ,SC.SCARFING_DEPTH                                --Scarfing깊이
				          ,MO.YD_CHG_NO                                     --야드장입순위
				          ,MO.PL_MPL_NO                                     --후판날판번호
				          ,SL.YD_STK_COL_GP                                 --야드적치열구분
				          ,SL.YD_STK_BED_NO                                 --야드적치Bed번호
				          ,SL.YD_STK_LYR_NO                                 --야드적치단번호
				      FROM VW_YD_SLABCOMM SC
				          ,TB_PT_HEATCOMM HC
				          ,TB_PT_OSCOMM   OC
				          ,(SELECT STL_NO
				                  ,YD_STK_COL_GP
				                  ,YD_STK_BED_NO
				                  ,YD_STK_LYR_NO
				                  ,SUBSTR(YD_STK_COL_GP,1,1) AS YD_GP
				              FROM TB_YD_STKLYR
				             WHERE STL_NO = :V_STL_NO
				               AND YD_STK_LYR_MTL_STAT IN ('C','U')) SL
				          ,(SELECT STL_NO
				                  ,WO_CAR_PLNT_PROC_CD
				                  ,ORD_BEFO_PROG_CD
				                  ,ARR_WLOC_CD
				                  ,URGENT_FRTOMOVE_WORD_GP
				              FROM TB_PT_STLFRTOMOVE FT
				             WHERE STL_NO = :V_STL_NO
				               AND TRANSWORD_SEQNO = (SELECT MAX(MS.TRANSWORD_SEQNO)
				                                        FROM TB_PT_STLFRTOMOVE MS
				                                       WHERE MS.STL_NO = FT.STL_NO
				                                         AND MS.FRTOMOVE_STAT_CD IN ('1','3'))) FT --(이송지시확정,야드수신완료)
				          ,(SELECT STL_NO
				                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
				                  ,ROLL_UNIT_NAME
				                  ,REFUR_CHG_LOT_NO
				                  ,REFUR_CHG_PLN_SERNO
				                  ,YD_CHG_NO
				                  ,NULL AS PL_MPL_NO
				              FROM USRCTA.TB_CT_L_HRMILLWO
				             WHERE STL_NO = :V_STL_NO
				               AND CT_MILL_SPEC_WRK_STAT_GP >= '3'
				             UNION ALL
				            SELECT STL_NO
				                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
				                  ,ROLL_UNIT_NAME
				                  ,REFUR_CHG_LOT_NO
				                  ,REFUR_CHG_PLN_SERNO
				                  ,YD_CHG_NO
				                  ,PL_MPL_NO
				              FROM USRCTA.TB_CT_N_PLMPLWO
				             WHERE STL_NO = :V_STL_NO
				               AND CT_MILL_SPEC_WRK_STAT_GP >= '3') MO
				     WHERE SC.SLAB_NO = SL.STL_NO(+)
				       AND SC.HEAT_NO = HC.HEAT_NO(+)
				       AND SC.ORD_NO  = OC.ORD_NO(+)
				       AND SC.ORD_DTL = OC.ORD_DTL(+)
				       AND SC.SLAB_NO = FT.STL_NO(+)
				       AND SC.SLAB_NO = MO.STL_NO(+)
				       AND SC.SLAB_NO = :V_STL_NO
				       AND ROWNUM     = 1
				    ) SC
				) DD ON (ST.STL_NO = DD.STL_NO)
				WHEN MATCHED THEN UPDATE SET
					    ST.MODIFIER                = DD.MODIFIER
				       ,ST.MOD_DDTT                = DD.MOD_DDTT
				       ,ST.DEL_YN                  = DD.DEL_YN
				       ,ST.PTOP_PLNT_GP            = DD.PTOP_PLNT_GP
				       ,ST.YD_MTL_ITEM             = DD.YD_MTL_ITEM
				       ,ST.ITEMNAME_CD             = DD.ITEMNAME_CD
				       ,ST.YD_MTL_STAT             = DD.YD_MTL_STAT
				       ,ST.STL_PROG_CD             = DD.STL_PROG_CD
				       ,ST.ORD_YEOJAE_GP           = DD.ORD_YEOJAE_GP
				       ,ST.ORD_NO                  = DD.ORD_NO
				       ,ST.ORD_DTL                 = DD.ORD_DTL
				       ,ST.YD_AIM_RT_GP            = DD.YD_AIM_RT_GP
				       ,ST.YD_AIM_YD_GP            = DD.YD_AIM_YD_GP
				       ,ST.YD_AIM_BAY_GP           = DD.YD_AIM_BAY_GP
				       ,ST.YD_STK_LOT_TP           = DD.YD_STK_LOT_TP
				       ,ST.YD_STK_LOT_CD           = DD.YD_STK_LOT_CD
				       ,ST.STL_APPEAR_GP           = DD.STL_APPEAR_GP
				       ,ST.PLNT_PROC_CD            = DD.PLNT_PROC_CD
				       ,ST.OVERALL_STAMP_GRADE     = DD.OVERALL_STAMP_GRADE
				       ,ST.YD_MTL_T                = DD.YD_MTL_T
				       ,ST.YD_MTL_W                = DD.YD_MTL_W
				       ,ST.YD_MTL_L                = DD.YD_MTL_L
				       ,ST.YD_MTL_WT               = DD.YD_MTL_WT
				       ,ST.SLAB_WO_RT_CD           = DD.SLAB_WO_RT_CD
				       ,ST.ORD_HCR_GP              = DD.ORD_HCR_GP
				       ,ST.HCR_GP                  = DD.HCR_GP
				       ,ST.SCARFING_YN             = DD.SCARFING_YN
				       ,ST.SCARFING_DONE_YN        = DD.SCARFING_DONE_YN
				       ,ST.HANDSCARFING_YN         = DD.HANDSCARFING_YN
				       ,ST.WO_MSLAB_RPR_MTD        = DD.WO_MSLAB_RPR_MTD
				       ,ST.REHEAT_SLAB_GP          = DD.REHEAT_SLAB_GP
				       ,ST.ROLL_UNIT_GP            = DD.ROLL_UNIT_GP
				       ,ST.ROLL_UNIT_NAME          = DD.ROLL_UNIT_NAME
				       ,ST.REFUR_CHG_LOT_NO        = DD.REFUR_CHG_LOT_NO
				       ,ST.REFUR_CHG_PLN_SERNO     = DD.REFUR_CHG_PLN_SERNO
				       ,ST.ORD_GP                  = DD.ORD_GP
				       ,ST.CUST_CD                 = DD.CUST_CD
				       ,ST.DEST_CD                 = DD.DEST_CD
				       ,ST.DEMANDER_CD             = DD.DEMANDER_CD
				       ,ST.GOODS_GRADE             = DD.GOODS_GRADE
				       ,ST.YD_RCPT_STR_LOC         = DD.YD_RCPT_STR_LOC
				       ,ST.DIST_DUE_DATE           = DD.DIST_DUE_DATE
				       ,ST.EXPORT_SHIP_SET_NO      = DD.EXPORT_SHIP_SET_NO
				       ,ST.DELIVER_TERM_CD         = DD.DELIVER_TERM_CD
				       ,ST.DETAIL_ARR_CD           = DD.DETAIL_ARR_CD
				       ,ST.STLKIND_CD              = DD.STLKIND_CD
				       ,ST.SPEC_ABBSYM             = DD.SPEC_ABBSYM
				       ,ST.CC_CCM_NO               = DD.CC_CCM_NO
				       ,ST.MMATL_FEE_NO            = DD.MMATL_FEE_NO
				       ,ST.WO_CAR_PLNT_PROC_CD     = DD.WO_CAR_PLNT_PROC_CD
				       ,ST.ORD_BEFO_PROG_CD        = DD.ORD_BEFO_PROG_CD
				       ,ST.ARR_WLOC_CD             = DD.ARR_WLOC_CD
				       ,ST.URGENT_FRTOMOVE_WORD_GP = DD.URGENT_FRTOMOVE_WORD_GP
				       ,ST.SCARFING_DEPTH          = DD.SCARFING_DEPTH
				       ,ST.YD_CHG_NO               = DD.YD_CHG_NO
				       ,ST.PL_MPL_NO               = DD.PL_MPL_NO
				       ,ST.YD_STK_COL_GP           = DD.YD_STK_COL_GP
				       ,ST.YD_STK_BED_NO           = DD.YD_STK_BED_NO
				       ,ST.YD_STK_LYR_NO           = DD.YD_STK_LYR_NO
				WHEN NOT MATCHED THEN
				INSERT (ST.STL_NO                 , ST.REGISTER           , ST.REG_DDTT           , ST.MODIFIER        , ST.MOD_DDTT     ,
				        ST.DEL_YN                 , ST.PTOP_PLNT_GP       , ST.YD_MTL_ITEM        , ST.ITEMNAME_CD     , ST.YD_MTL_STAT  ,
				        ST.STL_PROG_CD            , ST.ORD_YEOJAE_GP      , ST.ORD_NO             , ST.ORD_DTL         , ST.YD_AIM_RT_GP ,
				        ST.YD_AIM_YD_GP           , ST.YD_AIM_BAY_GP      , ST.YD_STK_LOT_TP      , ST.YD_STK_LOT_CD   , ST.STL_APPEAR_GP,
				        ST.PLNT_PROC_CD           , ST.OVERALL_STAMP_GRADE, ST.YD_MTL_T           , ST.YD_MTL_W        , ST.YD_MTL_L     ,
				        ST.YD_MTL_WT              , ST.SLAB_WO_RT_CD      , ST.ORD_HCR_GP         , ST.HCR_GP          , ST.SCARFING_YN  ,
				        ST.SCARFING_DONE_YN       , ST.HANDSCARFING_YN    , ST.WO_MSLAB_RPR_MTD   , ST.REHEAT_SLAB_GP  , ST.ROLL_UNIT_GP ,
				        ST.ROLL_UNIT_NAME         , ST.REFUR_CHG_LOT_NO   , ST.REFUR_CHG_PLN_SERNO, ST.ORD_GP          , ST.CUST_CD      ,
				        ST.DEST_CD                , ST.DEMANDER_CD        , ST.GOODS_GRADE        , ST.YD_RCPT_STR_LOC , ST.DIST_DUE_DATE,
				        ST.EXPORT_SHIP_SET_NO     , ST.DELIVER_TERM_CD    , ST.DETAIL_ARR_CD      , ST.STLKIND_CD      , ST.SPEC_ABBSYM  ,
				        ST.CC_CCM_NO              , ST.MMATL_FEE_NO       , ST.WO_CAR_PLNT_PROC_CD, ST.ORD_BEFO_PROG_CD, ST.ARR_WLOC_CD  ,
				        ST.URGENT_FRTOMOVE_WORD_GP, ST.SCARFING_DEPTH     , ST.YD_CHG_NO          , ST.PL_MPL_NO       , ST.YD_STK_COL_GP,
				        ST.YD_STK_BED_NO          , ST.YD_STK_LYR_NO)
				VALUES (DD.STL_NO                 , DD.MODIFIER           , DD.MOD_DDTT           , DD.MODIFIER        , DD.MOD_DDTT     ,
				        DD.DEL_YN                 , DD.PTOP_PLNT_GP       , DD.YD_MTL_ITEM        , DD.ITEMNAME_CD     , DD.YD_MTL_STAT  ,
				        DD.STL_PROG_CD            , DD.ORD_YEOJAE_GP      , DD.ORD_NO             , DD.ORD_DTL         , DD.YD_AIM_RT_GP ,
				        DD.YD_AIM_YD_GP           , DD.YD_AIM_BAY_GP      , DD.YD_STK_LOT_TP      , DD.YD_STK_LOT_CD   , DD.STL_APPEAR_GP,
				        DD.PLNT_PROC_CD           , DD.OVERALL_STAMP_GRADE, DD.YD_MTL_T           , DD.YD_MTL_W        , DD.YD_MTL_L     ,
				        DD.YD_MTL_WT              , DD.SLAB_WO_RT_CD      , DD.ORD_HCR_GP         , DD.HCR_GP          , DD.SCARFING_YN  ,
				        DD.SCARFING_DONE_YN       , DD.HANDSCARFING_YN    , DD.WO_MSLAB_RPR_MTD   , DD.REHEAT_SLAB_GP  , DD.ROLL_UNIT_GP ,
				        DD.ROLL_UNIT_NAME         , DD.REFUR_CHG_LOT_NO   , DD.REFUR_CHG_PLN_SERNO, DD.ORD_GP          , DD.CUST_CD      ,
				        DD.DEST_CD                , DD.DEMANDER_CD        , DD.GOODS_GRADE        , DD.YD_RCPT_STR_LOC , DD.DIST_DUE_DATE,
				        DD.EXPORT_SHIP_SET_NO     , DD.DELIVER_TERM_CD    , DD.DETAIL_ARR_CD      , DD.STLKIND_CD      , DD.SPEC_ABBSYM  ,
				        DD.CC_CCM_NO              , DD.MMATL_FEE_NO       , DD.WO_CAR_PLNT_PROC_CD, DD.ORD_BEFO_PROG_CD, DD.ARR_WLOC_CD  ,
				        DD.URGENT_FRTOMOVE_WORD_GP, DD.SCARFING_DEPTH     , DD.YD_CHG_NO          , DD.PL_MPL_NO       , DD.YD_STK_COL_GP,
				        DD.YD_STK_BED_NO          , DD.YD_STK_LYR_NO)
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.insSlabYdStock", logId, methodNm, "저장품(TB_YD_STOCK) 등록");				
			
			/*재열재적치단 정보 수정 - com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updSlabYdStkLyrPS */
			/*
				UPDATE TB_YD_STKLYR
				   SET MODIFIER            = :V_MODIFIER
				      ,MOD_DDTT            = SYSDATE
				      ,STL_NO              = :V_STL_NO
				      ,YD_STK_LYR_MTL_STAT = 'C'
				 WHERE YD_STK_COL_GP = 'DBPS01'
				   AND YD_STK_BED_NO = '01'
				   AND YD_STK_LYR_NO = '001'
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.updSlabYdStkLyrPS", logId, methodNm, "적치단(TB_YD_STKLYR) 재열재 Shift");				

			/**********************************************************
			* 3. 작업예약 및 크레인스케쥴 생성
			**********************************************************/
			GridData gridReq = new GridData();
			gridReq.addParam("userid",          sModifier);
			gridReq.addParam("STL_NOS",         slabNo);
			gridReq.addParam("YD_STK_COL_GP",   "DBPS01");
			gridReq.addParam("YD_TO_LOC_GUIDE", "DB031206");
			gridReq.setNavigateValue(methodNm);	// 상위 Method 명
			gridReq.setIPAddress(logId);		// Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "PSlabYdJspFaEJB", this);
			GridData jrRtn = (GridData)ejbConn.trx("trtMvStkWrkBookReg", new Class[] { GridData.class }, new Object[] { gridReq });
			
			slabUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	
	/***************************************************************************
	 * 공정계획(PM)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 슬라브충당실적(PMYDJ001)
	 *      염용선 2020-10-28
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPMYDJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브충당실적[PSlabYdL3RcvSeEJB.rcvPMYDJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String wrkHdsDd = slabUtils.trim(rcvMsg.getFieldString("WRK_HDS_DD1")); //작업계상일자1
			String stepNo   = slabUtils.trim(rcvMsg.getFieldString("STEP_NO1"   )); //차수1
			String sModifier = slabUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(wrkHdsDd)) {
				slabUtils.printLog(logId, "작업계상일자1(WRK_HDS_DD1) 값이 없습니다.", "SL");
				throw new Exception("작업계상일자1(WRK_HDS_DD1) 값이 없습니다.");
			} else if ("".equals(stepNo)) {
				slabUtils.printLog(logId, "차수1(STEP_NO1) 값이 없습니다.", "SL");
				throw new Exception("차수1(STEP_NO1) 값이 없습니다.");
			}

			/**********************************************************
			* 2. 저장품 수정
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			
			jrParam.setField("WRK_HDS_DD", wrkHdsDd); //작업계상일자
			jrParam.setField("STEP_NO"   , stepNo  ); //차수
			
			//JDTORecordSet jsStl = rcv3Dao.getPMYDJ001("Stl", jrParam);
			/*
			 * SELECT STL_NO
				  FROM TB_PT_ORDERTRANSMATCHLOG
				 WHERE WRK_HDS_DD1 = :V_WRK_HDS_DD
				   AND STEP_NO1    = TO_NUMBER(:V_STEP_NO)
				ORDER BY STL_NO
			 */
			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL3RcvSeEJB.getPMYDJ001Stl", logId, methodNm, "충당재료 조회");
			
			if (jsStl != null && jsStl.size() > 0) {
				int stlSh = jsStl.size(); //재료매수

				for (int ii = 0; ii < stlSh; ii++) {
					jrParam.setField("STL_NO", slabUtils.trim(jsStl.getRecord(ii).getFieldString("STL_NO"))); //재료번호

					//저장품 등록 (※ 슬라브충당실적과 슬라브이송지시가 동시에 같은 재료번호로 수신되어 DeadLock이 발생)
					EJBConnector tranConn = new EJBConnector("default", "PSlabYdL3RcvSeEJB", this);
					tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
			} else {
				//throw new Exception("충당할 재료가 존재하지 않습니다.");
				slabUtils.printLog(logId, "충당할 재료가 존재하지 않습니다.", "SL");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 슬라브이송지시(PMYDJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPMYDJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브이송지시[PSlabYdL3RcvSeEJB.rcvPMYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId            = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String frtomoveWordDate = slabUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_DATE1")); //이송작업지시일자1
			String transwordSeqNo   = slabUtils.trim(rcvMsg.getFieldString("TRANSWORD_SEQNO1"   )); //이송지시차수1
			String sModifier         = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(frtomoveWordDate)) {
				slabUtils.printLog(logId, "이송작업지시일자1(FRTOMOVE_WORD_DATE1) 값이 없습니다.", "SL");
				throw new Exception("이송작업지시일자1(FRTOMOVE_WORD_DATE1) 값이 없습니다.");
			} else if ("".equals(transwordSeqNo)) {
				slabUtils.printLog(logId, "이송지시차수1(TRANSWORD_SEQNO1) 값이 없습니다.", "SL");
				throw new Exception("이송지시차수1(TRANSWORD_SEQNO1) 값이 없습니다.");
			}

			/* --이송상태코드에 따른  체크 추가 : FRTOMOVE_STAT_CD1
			 * '*' 이송작업완료  
				0 이송지시등록  
				2 운송지시편성  
				3 야드수신완료  
				1 이송지시확정       
				C 이송지시취소 
			*/
			 
			/**********************************************************
			* 2. 저장품, 소재이송지시 수정
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("FRTOMOVE_WORD_DATE", frtomoveWordDate); //이송작업지시일자1
			jrParam.setField("TRANSWORD_SEQNO"   , transwordSeqNo  ); //이송지시차수1
			
			/*
			 * SELECT STL_NO
				  FROM TB_PT_STLFRTOMOVE
				 WHERE FRTOMOVE_WORD_DATE1 = :V_FRTOMOVE_WORD_DATE
				   AND TRANSWORD_SEQNO1    = TO_NUMBER(:V_TRANSWORD_SEQNO)
				ORDER BY STL_NO
			 */
			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL3RcvSeEJB.getPMYDJ002Stl", logId, methodNm, "이송재료 조회");
			
			if (jsStl != null && jsStl.size() > 0) {
				int stlSh = jsStl.size(); //재료매수

				for (int ii = 0; ii < stlSh; ii++) {
					jrParam.setField("STL_NO", slabUtils.trim(jsStl.getRecord(ii).getFieldString("STL_NO"))); //재료번호

					//저장품 등록 (※ 슬라브충당실적과 슬라브이송지시가 동시에 같은 재료번호로 수신되어 DeadLock이 발생)
					EJBConnector tranConn = new EJBConnector("default", "PSlabYdL3RcvSeEJB", this);
					tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}

				//소재이송지시 Update
				/*
				 * --슬라브이송지시 소재이송지시 수정
					MERGE INTO TB_PT_STLFRTOMOVE FM USING (
					SELECT STL_NO
					      ,TRANSWORD_SEQNO
					      ,DECODE(FRTOMOVE_STAT_CD1,'1','3','C','D') AS FRTOMOVE_STAT_CD --야드확인
					  FROM TB_PT_STLFRTOMOVE
					 WHERE FRTOMOVE_WORD_DATE1 = :V_FRTOMOVE_WORD_DATE
					   AND TRANSWORD_SEQNO1    = TO_NUMBER(:V_TRANSWORD_SEQNO)
					   AND FRTOMOVE_STAT_CD1 IN ('1','C')
					) DD ON (FM.STL_NO = DD.STL_NO AND FM.TRANSWORD_SEQNO = DD.TRANSWORD_SEQNO)
					WHEN MATCHED THEN UPDATE SET
						 FM.MODIFIER         = :V_MODIFIER
					    ,FM.MOD_DDTT         = SYSDATE
					    ,FM.FRTOMOVE_STAT_CD = DD.FRTOMOVE_STAT_CD

				 */
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL3RcvSeEJB.updPMYDJ002FM", logId, methodNm, "소재이송지시(TB_PT_STLFRTOMOVE) 수정");				
				
			} else {
				
				slabUtils.printLog(logId, "이송지시 재료가 존재하지 않습니다.", "SL");
				//throw new Exception("이송지시 재료가 존재하지 않습니다.");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 슬라브진행변경(PMYDJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPMYDJ003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브진행변경[PSlabYdL3RcvSeEJB.rcvPMYDJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = slabUtils.trim(rcvMsg.getFieldString("STL_NO"    )); //재료번호
			String sModifier = slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) || stlNo.length() < 9) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("STL_NO"  , stlNo   ); //재료번호
			
			//저장품 등록
			EJBConnector tranConn = new EJBConnector("default", "PSlabYdL3RcvSeEJB", this);
			tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			/**********************************************************
			* 3. AB열연야드 슬라브진행변경 처리
			**********************************************************/
			slabUtils.printLog(logId, "슬라브진행변경 AB열연야드 수신 처리[JNDIInternal.receiveInternal] 시작", "SL");
			EJBConnector abConn = new EJBConnector("default", "JNDIInternal", this);
			abConn.trx("receiveInternal", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			slabUtils.printLog(logId, "슬라브진행변경 AB열연야드 수신 처리[JNDIInternal.receiveInternal] 종료", "SL");

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 저장품 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updStock(JDTORecord jrParam) throws DAOException {
		String methodNm = "저장품 수정[PSlabYdL3RcvSeEJB.updStock] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			//저장품 수정
			//commDao.insSlabYd("Stock", jrParam);
			/*
			 * --저장품등록(재료번호) 
				MERGE INTO TB_YD_STOCK ST USING (
				SELECT SC.*
				      ,DECODE(SC.YD_AIM_RT_GP2,'E2',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EL' ELSE 'E2' END),
				                            'E4',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EM' ELSE 'E4' END),
				                            'E5',(CASE WHEN SC.ARR_WLOC_CD = 'C3S01' THEN 'EN' ELSE 'E5' END),
				              SC.YD_AIM_RT_GP2) AS YD_AIM_RT_GP
				FROM
				    (SELECT SC.SLAB_NO                   AS STL_NO           --재료번호
				          ,:V_MODIFIER                  AS MODIFIER         --수정자
				          ,SYSDATE                      AS MOD_DDTT         --수정일시
				          ,'N'                          AS DEL_YN           --삭제유무
				          ,SC.PTOP_PLNT_GP                                  --조업공장구분
				          ,SF_SLAB_YD_MTL_ITEM(SC.STL_APPEAR_GP, SC.SLAB_WO_RT_CD) AS YD_MTL_ITEM --야드재료품목
				          ,SC.ITEMNAME_CD                                   --품명코드
				          ,'2'                          AS YD_MTL_STAT      --야드재료상태(현물)
				          ,SC.CURR_PROG_CD              AS STL_PROG_CD      --재료진도코드
				          ,SC.ORD_YEOJAE_GP                                 --주문여재구분
				          ,SC.ORD_NO                                        --주문번호
				          ,SC.ORD_DTL                                       --주문행번
				          ,SF_SLAB_YD_AIM_RT_GP (SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
				                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
				                                 SC.STL_APPEAR_GP   ,
				                                 (CASE WHEN SC.MSLAB_RPR_MC_GP='G' AND SC.SCARFING_DONE_YN='N' AND SC.SCARFING_YN='Y' THEN 'AG' ELSE (SELECT BANK_WORK_RT     
				                                                                               FROM VW_YD_F_MSLABWO A
				                                                                              WHERE A.PLN_MSLAB_NO=SC.PLAN_SLAB_NO
				                                                                                   AND ROWNUM<=1
				                                                                              )
				                                      END )
				                                 ) AS YD_AIM_RT_GP2  --야드목표행선구분
				          ,SF_SLAB_YD_AIM_YD_GP (NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE
				                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
				                                                        ELSE 'A' END
				                                              END),
				                                 SC.CURR_PROG_CD    , FT.ARR_WLOC_CD                          ) AS YD_AIM_YD_GP  --야드목표야드구분
				          ,SF_SLAB_YD_AIM_BAY_GP(NVL(SL.YD_GP,CASE WHEN LENGTH(SC.SLAB_NO) > 9 THEN 'D' ELSE 
				                                                   CASE WHEN FT.ARR_WLOC_CD = 'C3S01' THEN 'M'
				                                                        ELSE 'A' END
				                                              END),
				                                 SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , FT.ARR_WLOC_CD     ,
				                                 SC.SCARFING_YN     , SC.SCARFING_DONE_YN, SC.HCR_GP          ,
				                                 SC.ORD_YEOJAE_GP   , SC.STL_APPEAR_GP                        ) AS YD_AIM_BAY_GP --야드목표동구분
				          ,SF_SLAB_YD_STK_LOT_TP(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
				                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
				                                 SC.STL_APPEAR_GP                                             ) AS YD_STK_LOT_TP --야드산적LotType
				          ,SF_SLAB_YD_STK_LOT_CD(SC.SLAB_WO_RT_CD   , SC.CURR_PROG_CD    , SC.SCARFING_YN     ,
				                                 SC.SCARFING_DONE_YN, SC.HCR_GP          , SC.ORD_YEOJAE_GP   ,
				                                 SC.STL_APPEAR_GP   , OC.PROD_DUE_DATE   , SC.STACK_LOT_NO    ,
				                                 MO.YD_CHG_NO       , FT.ARR_WLOC_CD                          ) AS YD_STK_LOT_CD --야드산적Lot코드
				          ,SC.STL_APPEAR_GP                                 --재료외형구분
				          ,SC.PLNT_PROC_CD                                  --공장공정코드
				          ,SC.OVERALL_STAMP_GRADE                           --종합판정등급
				          ,SC.SLAB_T                    AS YD_MTL_T         --야드재료두께
				          ,SC.SLAB_W                    AS YD_MTL_W         --야드재료폭
				          ,SC.SLAB_LEN                  AS YD_MTL_L         --야드재료길이
				          ,SC.SLAB_WT                   AS YD_MTL_WT        --야드재료중량
				          ,SC.SLAB_WO_RT_CD                                 --Slab지시행선코드
				          ,SC.ORD_HCR_GP                                    --설계HCR구분
				          ,SC.HCR_GP                                        --HCR구분
				          ,NVL(SC.SCARFING_YN     ,'N') AS SCARFING_YN      --Scarfing여부
				          ,NVL(SC.SCARFING_DONE_YN,'N') AS SCARFING_DONE_YN --Scarfing완료유무
				          ,SC.HANDSCARFING_YN                               --HandScarfing유무
				          ,SC.WO_MSLAB_RPR_MTD                              --지시주편손질방법
				          ,SC.REHEAT_SLAB_GP                                --재열재구분
				          ,MO.ROLL_UNIT_GP                                  --Roll단위구분
				          ,MO.ROLL_UNIT_NAME                                --Roll단위명
				          ,MO.REFUR_CHG_LOT_NO                              --가열로장입Lot번호
				          ,MO.REFUR_CHG_PLN_SERNO                           --가열로장입예정일련번호
				          ,OC.ORD_GP                                        --수주구분
				          ,OC.CUST_CD                                       --고객코드
				          ,OC.DEST_CD                                       --목적지코드
				          ,SC.DEMANDER_CD                                   --수요가코드
				          ,OC.GOODS_GRADE                                   --제품등급
				          ,OC.YD_RCPT_STR_LOC                               --야드입고저장위치
				          ,OC.DIST_DUE_DATE                                 --출하기한일
				          ,OC.EXPORT_SHIP_SET_NO                            --수출재배선번호
				          ,OC.DELIVER_TERM_CD                               --인도조건코드
				          ,OC.DETAIL_ARR_CD                                 --상세착지코드
				          ,HC.STLKIND_CD                                    --강종코드
				          ,SC.SPEC_ABBSYM                                   --규격약호
				          ,SC.CCM_NO                    AS CC_CCM_NO        --연주CCM번호
				          ,SC.PARENT_SLAB_NO            AS MMATL_FEE_NO     --모재료번호
				          ,FT.WO_CAR_PLNT_PROC_CD                           --지시차공장공정코드
				          ,FT.ORD_BEFO_PROG_CD                              --지시전진도코드
				          ,FT.ARR_WLOC_CD                                   --착지개소코드
				          ,FT.URGENT_FRTOMOVE_WORD_GP                       --긴급이송작업지시구분
				          ,SC.SCARFING_DEPTH                                --Scarfing깊이
				          ,MO.YD_CHG_NO                                     --야드장입순위
				          ,MO.PL_MPL_NO                                     --후판날판번호
				          ,SL.YD_STK_COL_GP                                 --야드적치열구분
				          ,SL.YD_STK_BED_NO                                 --야드적치Bed번호
				          ,SL.YD_STK_LYR_NO                                 --야드적치단번호
				      FROM VW_YD_SLABCOMM SC
				          ,TB_PT_HEATCOMM HC
				          ,TB_PT_OSCOMM   OC
				          ,(SELECT STL_NO
				                  ,YD_STK_COL_GP
				                  ,YD_STK_BED_NO
				                  ,YD_STK_LYR_NO
				                  ,SUBSTR(YD_STK_COL_GP,1,1) AS YD_GP
				              FROM TB_YD_STKLYR
				             WHERE STL_NO = :V_STL_NO
				               AND YD_STK_LYR_MTL_STAT IN ('C','U')) SL
				          ,(SELECT STL_NO
				                  ,WO_CAR_PLNT_PROC_CD
				                  ,ORD_BEFO_PROG_CD
				                  ,ARR_WLOC_CD
				                  ,URGENT_FRTOMOVE_WORD_GP
				              FROM TB_PT_STLFRTOMOVE FT
				             WHERE STL_NO = :V_STL_NO
				               AND TRANSWORD_SEQNO = (SELECT MAX(MS.TRANSWORD_SEQNO)
				                                        FROM TB_PT_STLFRTOMOVE MS
				                                       WHERE MS.STL_NO = FT.STL_NO
				                                         AND MS.FRTOMOVE_STAT_CD IN ('1','3'))) FT --(이송지시확정,야드수신완료)
				          ,(SELECT STL_NO
				                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
				                  ,ROLL_UNIT_NAME
				                  ,REFUR_CHG_LOT_NO
				                  ,REFUR_CHG_PLN_SERNO
				                  ,YD_CHG_NO
				                  ,NULL AS PL_MPL_NO
				              FROM USRCTA.TB_CT_L_HRMILLWO
				             WHERE STL_NO = :V_STL_NO
				               AND CT_MILL_SPEC_WRK_STAT_GP >= '3'
				             UNION ALL
				            SELECT STL_NO
				                  ,SUBSTR(ROLL_UNIT_NAME,7,1) AS ROLL_UNIT_GP
				                  ,ROLL_UNIT_NAME
				                  ,REFUR_CHG_LOT_NO
				                  ,REFUR_CHG_PLN_SERNO
				                  ,YD_CHG_NO
				                  ,PL_MPL_NO
				              FROM USRCTA.TB_CT_N_PLMPLWO
				             WHERE STL_NO = :V_STL_NO
				               AND CT_MILL_SPEC_WRK_STAT_GP >= '3') MO
				     WHERE SC.SLAB_NO = SL.STL_NO(+)
				       AND SC.HEAT_NO = HC.HEAT_NO(+)
				       AND SC.ORD_NO  = OC.ORD_NO(+)
				       AND SC.ORD_DTL = OC.ORD_DTL(+)
				       AND SC.SLAB_NO = FT.STL_NO(+)
				       AND SC.SLAB_NO = MO.STL_NO(+)
				       AND SC.SLAB_NO = :V_STL_NO
				       AND ROWNUM     = 1
				    ) SC
				) DD ON (ST.STL_NO = DD.STL_NO)
				WHEN MATCHED THEN UPDATE SET
					    ST.MODIFIER                = DD.MODIFIER
				       ,ST.MOD_DDTT                = DD.MOD_DDTT
				       ,ST.DEL_YN                  = DD.DEL_YN
				       ,ST.PTOP_PLNT_GP            = DD.PTOP_PLNT_GP
				       ,ST.YD_MTL_ITEM             = DD.YD_MTL_ITEM
				       ,ST.ITEMNAME_CD             = DD.ITEMNAME_CD
				       ,ST.YD_MTL_STAT             = DD.YD_MTL_STAT
				       ,ST.STL_PROG_CD             = DD.STL_PROG_CD
				       ,ST.ORD_YEOJAE_GP           = DD.ORD_YEOJAE_GP
				       ,ST.ORD_NO                  = DD.ORD_NO
				       ,ST.ORD_DTL                 = DD.ORD_DTL
				       ,ST.YD_AIM_RT_GP            = DD.YD_AIM_RT_GP
				       ,ST.YD_AIM_YD_GP            = DD.YD_AIM_YD_GP
				       ,ST.YD_AIM_BAY_GP           = DD.YD_AIM_BAY_GP
				       ,ST.YD_STK_LOT_TP           = DD.YD_STK_LOT_TP
				       ,ST.YD_STK_LOT_CD           = DD.YD_STK_LOT_CD
				       ,ST.STL_APPEAR_GP           = DD.STL_APPEAR_GP
				       ,ST.PLNT_PROC_CD            = DD.PLNT_PROC_CD
				       ,ST.OVERALL_STAMP_GRADE     = DD.OVERALL_STAMP_GRADE
				       ,ST.YD_MTL_T                = DD.YD_MTL_T
				       ,ST.YD_MTL_W                = DD.YD_MTL_W
				       ,ST.YD_MTL_L                = DD.YD_MTL_L
				       ,ST.YD_MTL_WT               = DD.YD_MTL_WT
				       ,ST.SLAB_WO_RT_CD           = DD.SLAB_WO_RT_CD
				       ,ST.ORD_HCR_GP              = DD.ORD_HCR_GP
				       ,ST.HCR_GP                  = DD.HCR_GP
				       ,ST.SCARFING_YN             = DD.SCARFING_YN
				       ,ST.SCARFING_DONE_YN        = DD.SCARFING_DONE_YN
				       ,ST.HANDSCARFING_YN         = DD.HANDSCARFING_YN
				       ,ST.WO_MSLAB_RPR_MTD        = DD.WO_MSLAB_RPR_MTD
				       ,ST.REHEAT_SLAB_GP          = DD.REHEAT_SLAB_GP
				       ,ST.ROLL_UNIT_GP            = DD.ROLL_UNIT_GP
				       ,ST.ROLL_UNIT_NAME          = DD.ROLL_UNIT_NAME
				       ,ST.REFUR_CHG_LOT_NO        = DD.REFUR_CHG_LOT_NO
				       ,ST.REFUR_CHG_PLN_SERNO     = DD.REFUR_CHG_PLN_SERNO
				       ,ST.ORD_GP                  = DD.ORD_GP
				       ,ST.CUST_CD                 = DD.CUST_CD
				       ,ST.DEST_CD                 = DD.DEST_CD
				       ,ST.DEMANDER_CD             = DD.DEMANDER_CD
				       ,ST.GOODS_GRADE             = DD.GOODS_GRADE
				       ,ST.YD_RCPT_STR_LOC         = DD.YD_RCPT_STR_LOC
				       ,ST.DIST_DUE_DATE           = DD.DIST_DUE_DATE
				       ,ST.EXPORT_SHIP_SET_NO      = DD.EXPORT_SHIP_SET_NO
				       ,ST.DELIVER_TERM_CD         = DD.DELIVER_TERM_CD
				       ,ST.DETAIL_ARR_CD           = DD.DETAIL_ARR_CD
				       ,ST.STLKIND_CD              = DD.STLKIND_CD
				       ,ST.SPEC_ABBSYM             = DD.SPEC_ABBSYM
				       ,ST.CC_CCM_NO               = DD.CC_CCM_NO
				       ,ST.MMATL_FEE_NO            = DD.MMATL_FEE_NO
				       ,ST.WO_CAR_PLNT_PROC_CD     = DD.WO_CAR_PLNT_PROC_CD
				       ,ST.ORD_BEFO_PROG_CD        = DD.ORD_BEFO_PROG_CD
				       ,ST.ARR_WLOC_CD             = DD.ARR_WLOC_CD
				       ,ST.URGENT_FRTOMOVE_WORD_GP = DD.URGENT_FRTOMOVE_WORD_GP
				       ,ST.SCARFING_DEPTH          = DD.SCARFING_DEPTH
				       ,ST.YD_CHG_NO               = DD.YD_CHG_NO
				       ,ST.PL_MPL_NO               = DD.PL_MPL_NO
				       ,ST.YD_STK_COL_GP           = DD.YD_STK_COL_GP
				       ,ST.YD_STK_BED_NO           = DD.YD_STK_BED_NO
				       ,ST.YD_STK_LYR_NO           = DD.YD_STK_LYR_NO
				WHEN NOT MATCHED THEN
				INSERT (ST.STL_NO                 , ST.REGISTER           , ST.REG_DDTT           , ST.MODIFIER        , ST.MOD_DDTT     ,
				        ST.DEL_YN                 , ST.PTOP_PLNT_GP       , ST.YD_MTL_ITEM        , ST.ITEMNAME_CD     , ST.YD_MTL_STAT  ,
				        ST.STL_PROG_CD            , ST.ORD_YEOJAE_GP      , ST.ORD_NO             , ST.ORD_DTL         , ST.YD_AIM_RT_GP ,
				        ST.YD_AIM_YD_GP           , ST.YD_AIM_BAY_GP      , ST.YD_STK_LOT_TP      , ST.YD_STK_LOT_CD   , ST.STL_APPEAR_GP,
				        ST.PLNT_PROC_CD           , ST.OVERALL_STAMP_GRADE, ST.YD_MTL_T           , ST.YD_MTL_W        , ST.YD_MTL_L     ,
				        ST.YD_MTL_WT              , ST.SLAB_WO_RT_CD      , ST.ORD_HCR_GP         , ST.HCR_GP          , ST.SCARFING_YN  ,
				        ST.SCARFING_DONE_YN       , ST.HANDSCARFING_YN    , ST.WO_MSLAB_RPR_MTD   , ST.REHEAT_SLAB_GP  , ST.ROLL_UNIT_GP ,
				        ST.ROLL_UNIT_NAME         , ST.REFUR_CHG_LOT_NO   , ST.REFUR_CHG_PLN_SERNO, ST.ORD_GP          , ST.CUST_CD      ,
				        ST.DEST_CD                , ST.DEMANDER_CD        , ST.GOODS_GRADE        , ST.YD_RCPT_STR_LOC , ST.DIST_DUE_DATE,
				        ST.EXPORT_SHIP_SET_NO     , ST.DELIVER_TERM_CD    , ST.DETAIL_ARR_CD      , ST.STLKIND_CD      , ST.SPEC_ABBSYM  ,
				        ST.CC_CCM_NO              , ST.MMATL_FEE_NO       , ST.WO_CAR_PLNT_PROC_CD, ST.ORD_BEFO_PROG_CD, ST.ARR_WLOC_CD  ,
				        ST.URGENT_FRTOMOVE_WORD_GP, ST.SCARFING_DEPTH     , ST.YD_CHG_NO          , ST.PL_MPL_NO       , ST.YD_STK_COL_GP,
				        ST.YD_STK_BED_NO          , ST.YD_STK_LYR_NO)
				VALUES (DD.STL_NO                 , DD.MODIFIER           , DD.MOD_DDTT           , DD.MODIFIER        , DD.MOD_DDTT     ,
				        DD.DEL_YN                 , DD.PTOP_PLNT_GP       , DD.YD_MTL_ITEM        , DD.ITEMNAME_CD     , DD.YD_MTL_STAT  ,
				        DD.STL_PROG_CD            , DD.ORD_YEOJAE_GP      , DD.ORD_NO             , DD.ORD_DTL         , DD.YD_AIM_RT_GP ,
				        DD.YD_AIM_YD_GP           , DD.YD_AIM_BAY_GP      , DD.YD_STK_LOT_TP      , DD.YD_STK_LOT_CD   , DD.STL_APPEAR_GP,
				        DD.PLNT_PROC_CD           , DD.OVERALL_STAMP_GRADE, DD.YD_MTL_T           , DD.YD_MTL_W        , DD.YD_MTL_L     ,
				        DD.YD_MTL_WT              , DD.SLAB_WO_RT_CD      , DD.ORD_HCR_GP         , DD.HCR_GP          , DD.SCARFING_YN  ,
				        DD.SCARFING_DONE_YN       , DD.HANDSCARFING_YN    , DD.WO_MSLAB_RPR_MTD   , DD.REHEAT_SLAB_GP  , DD.ROLL_UNIT_GP ,
				        DD.ROLL_UNIT_NAME         , DD.REFUR_CHG_LOT_NO   , DD.REFUR_CHG_PLN_SERNO, DD.ORD_GP          , DD.CUST_CD      ,
				        DD.DEST_CD                , DD.DEMANDER_CD        , DD.GOODS_GRADE        , DD.YD_RCPT_STR_LOC , DD.DIST_DUE_DATE,
				        DD.EXPORT_SHIP_SET_NO     , DD.DELIVER_TERM_CD    , DD.DETAIL_ARR_CD      , DD.STLKIND_CD      , DD.SPEC_ABBSYM  ,
				        DD.CC_CCM_NO              , DD.MMATL_FEE_NO       , DD.WO_CAR_PLNT_PROC_CD, DD.ORD_BEFO_PROG_CD, DD.ARR_WLOC_CD  ,
				        DD.URGENT_FRTOMOVE_WORD_GP, DD.SCARFING_DEPTH     , DD.YD_CHG_NO          , DD.PL_MPL_NO       , DD.YD_STK_COL_GP,
				        DD.YD_STK_BED_NO          , DD.YD_STK_LYR_NO)
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.insSlabYdStock", logId, methodNm, "저장품(TB_YD_STOCK) 등록");				
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	


}
