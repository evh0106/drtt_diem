/**
 * @(#)YfCommCarMvSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/22
 *
 * @description      YF야드공통 차량이동 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/22   정종균      조병기      최초 등록
 * 		  2019/11/18
 *        2019/11/19
 */
package com.inisteel.cim.yf.acoilBak.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xlib.cmc.GridData;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSetImplList;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.CommonUtil;

import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;

import com.inisteel.cim.yf.acoilBak.YFUserException;
import com.inisteel.cim.yf.acoilBak.YfCommUtils;
import com.inisteel.cim.yf.acoilBak.YfConstant;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld2;
import com.inisteel.cim.yf.acoilBak.session.YfComm;
import com.inisteel.cim.yf.acoilBak.dao.YfCommDAO;

/**
 *      [A] 클래스명 : YF야드공통 차량이동 처리
 *
 * @ejb.bean name="YfCommCarMvBakSeEJB" jndi-name="YfCommCarMvBakSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/
public class YfCommCarMvSeEJBSBean extends BaseSessionBean implements YfQueryIFOld, YfQueryIFOld2
{
	private static final long serialVersionUID = 1L;
	private String		classNm			= getClass().getName();
	private YfCommDAO	commDao			= new YfCommDAO();
	private YfCommUtils	commUtils		= new YfCommUtils();
	private YdDelegate	ydDelegate		= new YdDelegate();
	private YfComm		yfComm			= new YfComm();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException
	{

	}

	/**
	 * 오퍼레이션명 : 차량포인트 통합관리 (기존형태 유지 yd와 동일)
	 * 1 : 설비코드로 초기화(구내운송)
	 * 2 : 저장위치로 초기화 하는 경우(구내운송)
	 * 3 : 저장위치로 차량 포인트 예약 하는 경우(구내운송)
	 * 4 : 개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)
	 * A : 설비코드로 초기화 하는 경우(출하)
	 * B : 저장위치로 초기화 하는 경우(출하)
	 * C : 저장위치로 차량 포인트 예약 하는 경우(출하)
	 * D : 개소코드,포인트로 차량 포인트 예약 하는 경우(출하)
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public boolean YfCarPointinforeg
	(
		String chk,
		String s_CAR_NO,
		String s_TRN_EQP_CD,
		String s_YD_STK_COL_GP,
		String szARR_WLOC_CD,
		String szARR_YD_PNT_CD,
		String s_STAT,
		String logId,
		String mthdNm
	)
	throws DAOException
	{
		String		methodNm	= "[YfCommCarMvSeEJB.YfCarPointinforeg] < " + mthdNm;
		boolean 	isSuccess	= false;
		int 		iSeq		= 0;
		String		szMsg		= "";

		try
		{
			commUtils.printLog(logId, methodNm, "S+");
			szMsg = "▣▣▣▣차량포인트 통합관리(START):"+chk+","+s_CAR_NO+","+s_TRN_EQP_CD+","+s_YD_STK_COL_GP+","+szARR_WLOC_CD+","+szARR_YD_PNT_CD+","+s_STAT+"▣▣▣▣▣" ;
    		szMsg = "▣▣▣▣차량포인트 통합관리(START):통합작업 시작 ▣▣▣▣▣";
			commUtils.printLog(logId, szMsg, "ST");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//쿼리돌릴Param담을 변수

			if ("1".equals(chk))
			{
				//설비코드로 초기화 하는 경우(구내운송)
				jrParam.setField("STAT",			s_STAT);
				jrParam.setField("TRN_EQP_CD",		s_TRN_EQP_CD);

				iSeq = commDao.update(jrParam, carpointtrneqpcdupdate, logId, methodNm, "설비코드로 초기화 하는 경우(구내운송)");
			}
			else if ("2".equals(chk))
			{
				//저장위치로 초기화 하는 경우(구내운송)
				jrParam.setField("STAT",			s_STAT);
				jrParam.setField("YD_STK_COL_GP",	s_YD_STK_COL_GP);

				iSeq = commDao.update(jrParam, carpointstackcolgpupdateCT, logId, methodNm, "저장위치로 초기화 하는 경우(구내운송)");
			}
			else if ("3".equals(chk))
			{
				//저장위치로 차량 포인트 예약 하는 경우(구내운송)
				jrParam.setField("STAT",			s_STAT);
				jrParam.setField("TRN_EQP_CD",		s_TRN_EQP_CD);
				jrParam.setField("YD_STK_COL_GP",	s_YD_STK_COL_GP);

				iSeq = commDao.update(jrParam, carpointtrneqpcdupdateC, logId, methodNm, "저장위치로 차량 포인트 예약 하는 경우(구내운송)");
			}
			else if ("4".equals(chk))
			{
				//개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)
				jrParam.setField("STAT",			s_STAT);
				jrParam.setField("TRN_EQP_CD",		s_TRN_EQP_CD);
				jrParam.setField("ARR_WLOC_CD",		szARR_WLOC_CD);
				jrParam.setField("ARR_YD_PNT_CD",	szARR_YD_PNT_CD);

				iSeq = commDao.update(jrParam, carpointWlocpntupdate, logId, methodNm, "개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)");
			}
			else if ("A".equals(chk))
			{
				//설비코드로 초기화 하는 경우(출하)
				jrParam.setField("STAT",			s_STAT);
				jrParam.setField("TRN_EQP_CD",		s_TRN_EQP_CD);

				iSeq = commDao.update(jrParam, carpointtrneqpcdupdatePT, logId, methodNm, "설비코드로 초기화 하는 경우(출하)");
			}
			else if ("B".equals(chk))
			{
				//저장위치로 초기화 하는 경우(출하)
				jrParam.setField("STAT",			s_STAT);
				jrParam.setField("YD_STK_COL_GP",	s_YD_STK_COL_GP);

				iSeq = commDao.update(jrParam, carpointstackcolgpupdateC, logId, methodNm, "저장위치로 초기화 하는 경우(출하)");
			}
			else if ("C".equals(chk))
			{
				//저장위치로 차량 포인트 예약 하는 경우(출하)
				jrParam.setField("STAT",			s_STAT);
				jrParam.setField("CAR_NO",			s_CAR_NO);
				jrParam.setField("TRN_EQP_CD",		s_TRN_EQP_CD);
				jrParam.setField("YD_STK_COL_GP",	s_YD_STK_COL_GP);

				iSeq = commDao.update(jrParam, carpointtrneqpcdupdateC2, logId, methodNm, "저장위치로 차량 포인트 예약 하는 경우(출하)");
			}
			else if ("D".equals(chk))
			{
				//개소코드,포인트로 차량 포인트 예약 하는 경우(출하)
				jrParam.setField("STAT",			s_STAT);
				jrParam.setField("CAR_NO",			s_CAR_NO);
				jrParam.setField("TRN_EQP_CD",		s_TRN_EQP_CD);
				jrParam.setField("ARR_WLOC_CD",		szARR_WLOC_CD);
				jrParam.setField("ARR_YD_PNT_CD",	szARR_YD_PNT_CD);

				iSeq = commDao.update(jrParam, carpointWlocpntupdatePT, logId, methodNm, "개소코드,포인트로 차량 포인트 예약 하는 경우(출하)");
			}

	    	szMsg =  "▣▣▣▣차량포인트 통합관리(END)COUNT:"+iSeq+"▣▣▣▣▣";
			isSuccess = true;
			commUtils.printLog(logId, methodNm, "S-");
	    }
		catch (DAOException daoe)
		{
	        throw daoe;
	    }
		catch (Exception e)
		{
	        throw new EJBServiceException(e);
	    }

		return isSuccess;
	} // End of YfCarPointinforeg()

	/**
	 * 오퍼레이션명 : A열연 출하차량출발 - 맵비활성화
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public boolean procOutCarLevWrAB(JDTORecord jRcd) throws DAOException
	{
		String	methodNm			= "[YfCommCarMvSeEJB.procOutCarLevWrAB] < " + jRcd.getResultMsg();
		boolean	isVal				= false;
		String	logId				= "";
		String	szMsg				= "";

		// 발지개소코드
	    String szSPOS_WLOC_CD		= "";

	    // 발지야드포인트코드
	    String szSPOS_YD_PNT_CD		= "";
	    String szYD_CARLD_LEV_LOC	= "";
	    String szTRANS_ORD_DT		= "";
	    String szTRANS_ORD_SEQNO	= "";
	    String szCAR_NO				= "";
	    String szCARD_NO			= "";

	    JDTORecord		recInTemp	= null;
	    JDTORecordSet	rsStkCol	= null;
		JDTORecord		recOutTemp	= null;
		JDTORecord		recInPara	= null;
		JDTORecord		recUpdPara	= null;

	    int intRtnVal				= 0;
	    int intLevLocGp				= 0;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

	    	szTRANS_ORD_DT    = commUtils.trim(jRcd.getFieldString("TRANS_ORD_DT"));	//운송지시일자
	    	if("".equals(szTRANS_ORD_DT))
	    	{
				szMsg = "운송지시일자가 없습니다.";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);

				throw new DAOException("TRANS_ORD_DT Error");
	    	}

	    	szTRANS_ORD_SEQNO = commUtils.trim(jRcd.getFieldString("TRANS_ORD_SEQNO"));	//운송지시순번
	    	if("".equals(szTRANS_ORD_SEQNO))
	    	{
				szMsg = "운송지시순번이 없습니다.";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);

				throw new DAOException("TRANS_ORD_SEQNO Error");
	    	}

	    	szCAR_NO          = commUtils.trim(jRcd.getFieldString("CAR_NO"));			//차량번호
	    	if("".equals(szCAR_NO))
	    	{
				szMsg = "차량번호가 없습니다.";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);

				throw new DAOException("CAR_NO Error");
	    	}

	    	szCARD_NO         = commUtils.trim(jRcd.getFieldString("CARD_NO"));			//카드번호
	    	if("".equals(szCARD_NO))
	    	{
				szMsg = "카드번호가 없습니다.";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);

				throw new DAOException("CARD_NO Error");
	    	}

	    	szSPOS_WLOC_CD    = commUtils.trim(jRcd.getFieldString("SPOS_WLOC_CD"));	//발지개소코드
	    	if("".equals(szSPOS_WLOC_CD))
	    	{
				szMsg = "발지개소코드가 없습니다.";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);

				throw new DAOException("SPOS_WLOC_CD Error");
	    	}

	    	szSPOS_YD_PNT_CD  = commUtils.trim(jRcd.getFieldString("SPOS_YD_PNT_CD"));	//발지포인트코드
	    	if("".equals(szSPOS_YD_PNT_CD))
	    	{
				szMsg = "발지포인트코드가 없습니다.";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);

				throw new DAOException("SPOS_YD_PNT_CD Error");
	    	}

	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("TRANS_ORD_DT",    szTRANS_ORD_DT);
	    	recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
	    	recInTemp.setField("CAR_NO",          szCAR_NO);
	    	recInTemp.setField("CARD_NO",         szCARD_NO);
	    	recInTemp.setField("SPOS_WLOC_CD",    szSPOS_WLOC_CD);
	    	recInTemp.setField("SPOS_YD_PNT_CD",  szSPOS_YD_PNT_CD);

	    	//TCCODE
	    	String sTC_CODE = commUtils.getMsgId(jRcd);

	    	//발지위치정보로 출발위치 Clear
	    	//열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	//업데이트값이 없다면 그냥 종료
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
	    	//착지개소코드와 착지야드포인트코드로 적치열을 조회한다.
	    	rsStkCol = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   szSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", szSPOS_YD_PNT_CD);
	    	intLevLocGp = commDao.getYdStkcol(recInTemp, rsStkCol, 4);

			if(intLevLocGp <= 0)
			{
				if(intRtnVal == 0)
				{
					szMsg = "<procOutCarLevWrAB> getYdStkcol data not found";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.WARNING);
				}
				else if(intRtnVal == -2)
				{
					szMsg = "<procOutCarLevWrAB> getYdStkcol parameter error";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);
				}

				intRtnVal = -1;
			}

			//조회된 값이 있을 경우
	    	if(intLevLocGp > 0)
	    	{
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord(0));

		    	szYD_CARLD_LEV_LOC = commUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP");	//열구분을 조회(도착지)

		    	//적치열  상태 비활성화
				recUpdPara = JDTORecordFactory.getInstance().create();
				recUpdPara.setField("YD_STK_COL_ACTIVE_STAT",	"C");
				recUpdPara.setField("YD_CAR_USE_GP",			"");
				recUpdPara.setField("TRN_EQP_CD",				"");
				recUpdPara.setField("CAR_NO",					"");
				recUpdPara.setField("MODIFIER",					sTC_CODE);
				recUpdPara.setField("YD_STK_COL_GP",			szYD_CARLD_LEV_LOC);
				intRtnVal = commDao.uptYmEtcDao(recUpdPara, 1);

				if(intRtnVal <= 0)
				{
					szMsg = "적치열  상태 비활성화등록 중 Error";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);
    				throw new DAOException("<updYdStkcol> " + szMsg);
				}

				//적치베드 상태 비활성화등록
				recUpdPara = JDTORecordFactory.getInstance().create();
				recUpdPara.setField("YD_STK_BED_ACTIVE_STAT",	"C");
				recUpdPara.setField("YD_STK_BED_WT_MAX",		new Integer(0));
				recUpdPara.setField("MODIFIER",					sTC_CODE);
				recUpdPara.setField("YD_STK_COL_GP",			szYD_CARLD_LEV_LOC);
				intRtnVal = commDao.uptYmEtcDao(recUpdPara, 2);

				if(intRtnVal <= 0)
				{
					szMsg = "적치베드 상태 비활성화등록 중 Error";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);
    				throw new DAOException("<updYdStkcol> " + szMsg);
				}

				//적치단 상태 비활성화
				recUpdPara = JDTORecordFactory.getInstance().create();
				recUpdPara.setField("YD_STK_LYR_ACTIVE_STAT",	"C");
				recUpdPara.setField("YD_STK_LYR_STAT",			"E");
				recUpdPara.setField("STL_NO",					"");
				recUpdPara.setField("MODIFIER",					sTC_CODE);
				recUpdPara.setField("YD_STK_COL_GP",			szYD_CARLD_LEV_LOC);
				intRtnVal = commDao.uptYmEtcDao(recUpdPara, 3);

				if(intRtnVal <= 0)
				{
					szMsg="적치단 상태 비활성화등록 중 Error";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.ERROR);
    				throw new DAOException("<updYdStkcol> " + szMsg);
				}

				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 구내운송 소재차량Point개폐 전송  - YDTSJ012
		         * 업무기준 Desc : 1. 외판슬라브 출하차량 도착 실적처리 후 구내운송에소재차량Point개폐 전송 전송
		         * 				 2. 출하관리와 구내운송간의 차량point 정보공유를 위해서, 구내운송에서 출하차량이 도착한 point를 사용하지
		         * 					않도록 하기 위해서.
		         * 				 3. +++++++++++ 출발 시는 포인트가 열렸다고 전송 +++++++++++++
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setField("MSG_ID",			"YDTSJ012");
				recInPara.setField("YD_STK_COL_GP",		szYD_CARLD_LEV_LOC);				//적치열구분
				recInPara.setField("PNT_UNIT_CL_GP",	YfConstant.PNT_UNIT_CL_GP_OPEN);	//포인트개폐구분
				ydDelegate.sendMsg(recInPara);
				szMsg = "[외판슬라브 출하차량출발]구내운송 소재차량Point개폐 전송 완료 - PNT_UNIT_CL_GP : " + YfConstant.PNT_UNIT_CL_GP_OPEN;
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	}//end of if

			isVal = true;
			commUtils.printLog(logId, methodNm, "S-");
	    }
		catch (DAOException daoe)
		{
	        throw daoe;
	    }
		catch (Exception e)
		{
	        throw new EJBServiceException(e);
	    }

		return isVal;
	} // End of procOutCarLevWrAB()

	/**
	 * 오퍼레이션명 : 차량 출발 지시를 처리.
	 *
	 * 차량 출발 지시를 처리한다.
     * 1.TC_CD	: 없음.
     * 2.I/F ID	: 없음.
     * param 	gp		이송상차1,이송하차2 구분
     * param 	cardNo	차량카드번호
     * param 	pos		정지위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 */
	public boolean carStartOrder(String gp, String cardNo, String pos)
	{
		String methodNm			= "[YfCommCarMvSeEJB.carStartOrder]";
		String szMsg			= "";

        JDTORecord inRecord		= JDTORecordFactory.getInstance().create();
        int intRtnVal			= 0;

		String sYD_CAR_SCH_ID	= "";
		String szCARD_NO_CHK	= "";
	    String szCHK_YN			= "N";

		List loadList 			= null;

	    try
	    {
	    	commUtils.printLog(classNm, methodNm, "S+");

	        /**
	         * 차량카드번호, 정지위치 체크
	         */
	        this.validCarArrivalAndStart(cardNo, pos);

	        //장비번호 가져오기
			loadList = commDao.getCommonList(getLoadendLayer, new Object[]{pos});

			if(loadList.size()>0)
			{
				JDTORecord FrtoProduct = (JDTORecord)loadList.get(0);
				szCARD_NO_CHK = StringHelper.evl(FrtoProduct.getFieldString("CARD_NO"),"");

				szMsg = "carStartOrder():다른 차량 존재여부 현재 카드체크:"+szCARD_NO_CHK+" , 입력 카드번호:"+cardNo;
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				//다른 차량이 존재 하는 경우
		    	if(!szCARD_NO_CHK.equals(cardNo) && !"".equals(szCARD_NO_CHK)&&  !"".equals(cardNo))
		    	{
		    		szCHK_YN ="Y";
		    	}
			}

			szMsg = "carStartOrder():다른 차량 존재여부 szCHK_YN:"+szCHK_YN;
			commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        //차량ID값을 가져 온다
	        List list = commDao.readcarinfoOfwloc(cardNo, pos);

			if(list.size()> 0)
			{
				JDTORecord jtR = (JDTORecord)list.get(0);
				sYD_CAR_SCH_ID 	= commUtils.paraRecChkNull(jtR,"YD_CAR_SCH_ID");
			}

			if("N".equals(szCHK_YN))
			{
		        /**
		         * 차량에 저장품이
		         * 1. 적치되어 있으면 하차동으로 출발
		         * 2. 적치되어 있지 않으면 하차 출발
		         */
		        List stocks 	= commDao.readStockOfCarLoad(pos);
		        int stocksCnt 	= stocks != null ? stocks.size() : 0;

		        szMsg = "carStartOrder(): "+String.valueOf(stocksCnt);
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		        if(stocksCnt > 0)
		        {
			        /**
			         * 상차완료인지 확인한다.
			         */
		        	szMsg = "carStartOrder():상차완료인지 확인한다.";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

					this.confirmCarStart(stocks, stocksCnt);

			        /**
			         * 저장품 정보 UPDATE
			         * 1. 설비 테이블 UPDATE
			         * 2. 적치열 차량CARD번호 CLEAR
			         * 3. 적치단 저장품ID CLEAR
			         */
			        this.editCardReferenceOfStock(pos);

			        //차량포인트통합관리(1구분, 2CAR_NO, 3 장비번호 or CARD_NO, 4 저장위치, 5 개소코드, 6 포인트, 7 상태, 클래스명, 메소드명)
			        this.YfCarPointinforeg("B", "", "", pos, "", "", "C", classNm,"carStartOrder");

			        /**
			         * 저장품에 현재 위치 셋팅
			         */
			        szMsg = "carStartOrder():저장품에 현재 위치 셋팅";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

					this.setCardReferenceOfStock(stocks, stocksCnt);

			        //A열연 SLAB야드 PALLET 출발시 맵정보 전송
			        if(YfConstant.YD_GP_0.equals(pos.substring(0, 1)))
			        {
			        	szMsg = "차량 출발 지시 처리 A열연 SLAB야드 일경우 MAP정보 전송";
						commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

			        	String sMsg = commUtils.setBSlabMapMsgInfo(pos + YfConstant.STACK_BED_GP_01 + YfConstant.STACK_LAYER_GP_01);

						EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);	//차후 수정해야함
						Boolean isSucf = (Boolean) ejbConn.trx("bsyYdMapInfo", new Class[] { String.class }, new Object[] { sMsg });
			        }
		        }
		        else
		        {
		        	szMsg = "stocksCnt <= 0";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		            // 맵 정리
		        	this.editUnloadState(pos);

					//차량포인트통합관리(1구분, 2CAR_NO, 3 장비번호 or CARD_NO, 4 저장위치, 5 개소코드, 6 포인트, 7 상태, 클래스명, 메소드명)
		        	this.YfCarPointinforeg("B", "", "", pos, "", "", "C", classNm,"carStartOrder");

			        /**
			         * 야드 L-2 송신
			         */
		            stocks = commDao.readStockInfoOfCardNo(cardNo);
		            this.unloadClear(stocks);

			        //A열연 SLAB야드 PALLET_NO로 입력할 경우도 있기때문에 DB에서 CAR_CARD_NO를 가져옴.(MCH)
			        if(stocks.size()>0)
			        {
			        	JDTORecord jrecrd = (JDTORecord)stocks.get(0);
			        	cardNo = StringHelper.evl(jrecrd.getFieldString("CAR_CARD_NO"), cardNo);
			        }

			        /*
		        	 * 차량카드번호가 '9990~9994(차량동간이적)'가 아닐때만 야드 L-2에 송신
		        	 */
		        	/* ======== Start ============*/
		        	szMsg = "carStartOrder():차량이적일 경우는 송신 않함";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		        	if
		        	(
		        		(
		        			!(YfConstant.CAR_BAY_TRANS_CARD_NO_6.equals(cardNo)) &&
		        			!(YfConstant.CAR_BAY_TRANS_CARD_NO_7.equals(cardNo)) &&
		        			!(YfConstant.CAR_BAY_TRANS_CARD_NO_8.equals(cardNo)) &&
		        			!(YfConstant.CAR_BAY_TRANS_CARD_NO_9.equals(cardNo)) &&
		        			!(YfConstant.CAR_BAY_TRANS_CARD_NO_0.equals(cardNo))
		        		)
		 	    	)
		        	{
		        		this.sendStartAndArrivalOrder(cardNo, pos, gp, YfConstant.CAR_GP_S);
		        	}
		        	/* ======== End ==============*/
		        }

		        /**
		         * 적치대의 적재능력을 초기화
		         */
		        commDao.modifyPossibleOfStacker(pos, YfConstant.STACK_BED_GP_01);
			}

			//차량스케줄 종료처리
	        szMsg = "carStartOrder():차량스케줄 종료처리:"+sYD_CAR_SCH_ID;
			commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        if(!"".equals(sYD_CAR_SCH_ID))
	        {
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("V_MODIFIER",			"carStart");
				inRecord.setField("V_YD_CAR_SCH_ID",	sYD_CAR_SCH_ID);

			    intRtnVal = commDao.delCarSchMtlLayer(inRecord);

			    intRtnVal = commDao.delCarWrMgtCarSchMtl(inRecord);

			    intRtnVal = commDao.delCarWrMgtCarSch(inRecord);

			    if (intRtnVal <= 0)
				{
					szMsg = "차량SCH 삭제중 ERROR 발생 ";
					commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
					return false;
				}
			}

	        commUtils.printLog(classNm, methodNm, "S-");
	    }
	    catch(DAOException daoe)
	    {
            throw daoe;
        }
	    catch(Exception e)
	    {
            throw new EJBServiceException(e);
        }

	    return true;
	}

	/**
     * 수신항목 '카드번호', '차량 정지 위치'를 체크
     * @param cardNo	카드번호
     * @param pos		차량 정지 위치
     * @return
     */
    private void validCarArrivalAndStart(String cardNo, String pos) throws Exception
    {
        if(cardNo == null || cardNo.length() > 10)
        {
            throw new Exception("카드번호 ERROR: "+ cardNo);
        }
        else if(pos == null || pos.length() != 6)
        {
            throw new Exception("정지위치 ERROR: "+ pos);
        }
    }

    /**
     * 차량 출발이 가능한지 저장품 정보를 확인한다.
     * CE	Coil소재 이송상차 완료
     * GE	Coil제품 이송상차 완료
     * H5	Coil 출하 상차완료
     * @param stocks	저장품 이동 조건 정보
     * @throws Exception
     */
    private void confirmCarStart(List stocks, int stocksCnt) throws Exception
    {
        String term 		= null;
        JDTORecord moveTerm = null;

        for(int i = 0; i < stocksCnt; i++)
        {
            moveTerm 	= (JDTORecord)stocks.get(i);
            term		= getField(moveTerm, "STOCK_MOVE_TERM");

            if
            (
            	! YfConstant.NEW_STOCK_MOVE_TERM_VL.equals(term) &&
                ! YfConstant.NEW_STOCK_MOVE_TERM_MG.equals(term) &&
                ! YfConstant.NEW_STOCK_MOVE_TERM_E1.equals(term) &&
                ! YfConstant.NEW_STOCK_MOVE_TERM_C1.equals(term)
            )
            {
                //throw new Exception("저장품 정보가 출발 완료 상태가 아닙니다");
            }
        }
    }

    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getField(JDTORecord data, String name)
    {
    	if(data == null)
    	{
    		return "";
    	}
    	else
    	{
    		return StringHelper.evl(data.getFieldString(name), "").trim();
    	}
    }

    /**
     * 설비/적치열/적치단 테이블의 '적재상태', '차량카드번호', '적치상태'를 UPDATE
     * @param pos	차량 정지 위치
     */
    private void editCardReferenceOfStock(String pos)
    {
    	commUtils.putLog(classNm, "editCardReferenceOfStock", "설비 테이블 UPDATE", YfConstant.DEBUG);
    	//commDao.modifyWBookAndLoadSchOfEquip(commUtils.getStringYMDHM(), "", pos);	//설비테이블 변경으로 항목이 사라져서 사용한함

        commUtils.putLog(classNm, "editCardReferenceOfStock", "적치열 차량CARD번호 CLEAR", YfConstant.DEBUG);
        commDao.modifyCardNoOfStackCol("", pos);

        commUtils.putLog(classNm, "editCardReferenceOfStock", "적치단 저장품ID CLEAR", YfConstant.DEBUG);
        commDao.modifyStockStatOfLayer("", YfConstant.STACK_LAYER_ACTIVE_STAT_C, YfConstant.STACK_LAYER_STAT_E, pos);
    }

    /**
     * 멀티동이면 현재MAP 정보를 저장품에 UPDATE 한다.
     * @param stocks		차량 출발 조건 정보
     */
    private void setCardReferenceOfStock(List stocks, int stocksCnt)
    {
    	commUtils.putLog(classNm, "setCardReferenceOfStock", "저장품에 현재 위치 설정:List"+stocks, YfConstant.DEBUG);
    	commUtils.putLog(classNm, "setCardReferenceOfStock", "저장품에 현재 위치 설정:INT"+stocksCnt, YfConstant.DEBUG);

        for(int i = 0; i < stocksCnt; i++)
        {
        	commDao.modifyTermAndMoveEquipOfStock
        	(
        		getField((JDTORecord)stocks.get(i), "YD_STK_COL_GP"),
        		getField((JDTORecord)stocks.get(i), "YD_STK_BED_NO"),
        		getField((JDTORecord)stocks.get(i), "YD_STK_LYR_NO"),
        		getField((JDTORecord)stocks.get(i), "STOCK_MOVE_TERM"),
        		getField((JDTORecord)stocks.get(i), "STL_NO")
        	);
        }
    }

    /**
     * TB_PM_SLABCOMM '부두 YARD 반출 일자', '부두 YARD 반출 시각' UPDATE
     * @param stocks	차량상차 저장품정보
     * @param ydGp		야드구분
     * @param carGp		차량도착/출발구분
     * @param stocksCnt	차량상차 저장품정보 개수
     */
    private void editTakeOutTimeOfSlabComm(List stocks, String ydGp, String carGp, int stocksCnt)
    {
        if(YfConstant.CAR_GP_D.equals(carGp))
        {
            for(int i = 0; i < stocksCnt; i++)
            {
            	commDao.modifyLieTakeOutTimeOfSlabComm
            	(
            		commUtils.getStringYMD(),
            		commUtils.getStringHMS(),
            		getField((JDTORecord)stocks.get(i), "STL_NO")
            	);
            }
        }
    }

    /**
	 * 차량 하차 출발시에 저장품/적치열/적치단 테이블의 관련 정보를 CLEAR
	 * @param pos 하차정지위치
	 */
	private void editUnloadState(String pos)
	{
		commDao.modifyCardNoOfStackCol("", pos);

		commDao.modifyStockStatOfLayer("", YfConstant.STACK_LAYER_ACTIVE_STAT_C, YfConstant.STACK_LAYER_STAT_E, pos);
	}

	/**
     * 차량 하차시 차량MAP정보, 차량카드번호를 CLEAR
     * @param stocks	차량도착정보
     */
    private void unloadClear(List stocks)
    {
        JDTORecord dto = null;
        int stocksCnt = stocks != null ? stocks.size() : 0;

        for(int i = 0; i < stocksCnt; i++)
        {
            dto = (JDTORecord)stocks.get(i);
            commDao.modifyUnloadInfoOfStock("", "", "", "", "", "", getField(dto, "STL_NO"));
        }
    }

    /**
     * 차량 출발/도착지시 전문을 편성한다.
     * 1. A열연 차량진입/출발 정보(구전문:THHC190 -> 신전문:YFF1L008)
     * 2. B열연
     *    2.1 차량 도착/출발 정보 COIL  CN1BP06
     *    2.2 차량 도착/출발 정보 SLAB	CM1BP06
     * @param cardNo	차량CARD번호
     * @param pos		차량 정지위치
     * @param carGp		도착출발구분
     */
    private void sendStartAndArrivalOrder(String cardNo, String pos, String gp, String carGp)
    {
        String ydGp = pos.substring(0, 1);

        if(YfConstant.YD_GP_4.equals(ydGp))
        {
            return;
        }

        String tcCd = "YFF1L008";	//YfConstant.TC_THHC190;	//차후 수정해야함
        StringBuffer sendMsg = new StringBuffer();
        JDTORecord dto = commDao.readCarNo(ydGp, cardNo);

        if(YfConstant.YD_GP_1.equals(ydGp))
        {
        	//A열연 COIL
            /**
             * THHC190
             * 1	전문코드			CHAR	07
             * 2	작업동			CHAR	01
             * 3	진입위치 SEQ NO	CHAR	01
             * 4	차량구분			CHAR	01		1:반입, 2:출하
             * 5	운송회사 코드		CHAR	05
             * 6	차량번호			CHAR	05
             * 7	작업대상 수량		CHAR	02
             * 8	CARD 번호		CHAR	04
             * 9	코일번호			CHAR	10		8회 반복
             * 10	권상, 권하 위치	CHAR	08		8회 반복
             * 11	SPARE			CHAR	30
             */
            Map tc = commDao.readColumnLenOfTc(tcCd);
            sendMsg.append(tcCd);
            sendMsg.append(pos.substring(1, 2));
            appendMsg(sendMsg, "", getFieldLen(tc, "진입위치SEQNO"));
            sendMsg.append(YfConstant.CAR_GP_2);
            appendMsg(sendMsg, "", getFieldLen(tc, "운송회사코드"));
            appendMsg(sendMsg, getField(dto, "CAR_NO"), getFieldLen(tc, "차량번호"));
            appendMsgNum(sendMsg, "", getFieldLen(tc, "작업대상수량"));
            appendMsg(sendMsg, cardNo, getFieldLen(tc, "CARD번호"));

            for(int i = 0; i < 8; i++)
            {
                appendMsg(sendMsg, "", getFieldLen(tc, "코일번호1"));
                appendMsg(sendMsg, "", getFieldLen(tc, "권상권하위치1"));
            }

            appendMsg(sendMsg, "", getFieldLen(tc, "SPARE"));

            sendQueue(tcCd, sendMsg.toString());
        }
//        else
//        {
//        	//B열연 COIL/SLAB
//            /**
//             * CM1BP06
//             * 1	전문코드	TC					CHAR	07
//             * 2	발생일자	Date				CHAR	10	YYYY-MM-DD
//             * 3	발생시간	Time				CHAR	08	HH-MM-SS
//             * 4	전문구분	Form				CHAR	01	I: Initialize, U: Update, D: Delete, R: Re-request
//             * 5	전문길이	Message_Length		CHAR	04
//             * 6	차량 번호	CarNo				CHAR	12
//             * 7	차량 TYPE	CarType				CHAR	01	조업기준
//             * 8	출발/도착 구분	ArriveId		CHAR	01	‘D’:도착 ‘S’:출발
//             * 9	차량 진입 위치	CarInPosition	CHAR	02	YARD구분(1)+동구분(1)+SPAN(2)+열 NO(2)
//             * 10	지시구분	OrderId				CHAR	01	1:장입지시, 2:이송지시, 3:출하지시
//             * 11	지시번호	OrderNo				CHAR	10
//             * 12	CARD 번호	CardNo			CHAR	06
//             * 13	작업 매수	WorkCount			CHAR	02	Slab 10매 정보를 보낼 때작업매수는 10매, 송신 Seq는 현재 보내는 건의 순서를 말함
//             * 14	송신 Seq	RecevieSeq			CHAR	01
//             * 15	작업 SLAB No	WorkSlabNo		CHAR	11
//             */
//        	tcCd = "YfConstant.TC_CM1BP06";		//차후 수정해야함
//
//            Map tc = commDao.readColumnLenOfTc(tcCd);
//            sendMsg.append(tcCd);							//전문코드
//            sendMsg.append(commUtils.getStringYMD("-"));	//발생일자
//            sendMsg.append(commUtils.getStringHMS("-"));	//발생시간
//            sendMsg.append("I");							//전문구분
//            appendMsgNum(sendMsg, ""+ (commUtils.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
//            appendMsg(sendMsg, getField(dto, "CAR_NO"), getFieldLen(tc, "차량번호"));
//
//            if("TR".equals(pos.substring(2, 4)))
//            {
//                appendMsg(sendMsg, "T",		getFieldLen(tc, "차량TYPE"));
//            }
//            else if("PT".equals(pos.substring(2, 4)))
//            {
//                //appendMsg(sendMsg, "P",		getFieldLen(tc, "차량TYPE"));
//                appendMsg(sendMsg, "T",		getFieldLen(tc, "차량TYPE"));
//            }
//            else
//            {
//                appendMsg(sendMsg, "",		getFieldLen(tc, "차량TYPE"));
//            }
//            sendMsg.append(carGp);							//출발도착구분
//            appendMsg(sendMsg, pos, 					getFieldLen(tc, "차량진입위치"));
//            appendMsg(sendMsg, getOrderGp(""), 			getFieldLen(tc, "지시구분"));
//            appendMsg(sendMsg, "", 						getFieldLen(tc, "지시번호"));
//            appendMsg(sendMsg, cardNo, getFieldLen(tc, "CARD번호"));
//            appendMsgNum(sendMsg, "", getFieldLen(tc, "작업매수"));
//            appendMsgNum(sendMsg, "", getFieldLen(tc, "송신Seq"));
//            appendMsg(sendMsg, "", getFieldLen(tc, "작업SLABNo"));
//
//            sendQueue(tcCd, sendMsg.toString());
//            sendMsg.setLength(0);
//        }
    }

    /**
     * name parameter에 대한 값을 반환한다.
     * @param data
     * @param name
     * @return
     */
    private int getFieldLen(Map data, String name)
    {
        return StringHelper.parseInt((String)data.get(name), 0);
    }

    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void appendMsg(StringBuffer buffer, String field, int cnt)
    {
	    try
	    {
	    	if("".equals(field))
	    	{
	            fillSpace(buffer, cnt);
	        }
	    	else if(CommonUtil.getLength(field) > cnt)
	    	{
	        	buffer.append(CommonUtil.substr(field, 0, cnt));
	        }
	    	else if(CommonUtil.getLength(field) < cnt)
	    	{
	            buffer.append(field);
	            fillSpace(buffer, cnt - CommonUtil.getLength(field));
	        }
	    	else
	    	{
	            buffer.append(field);
	        }
	    }catch(Exception e){}
    }

    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void appendMsgNum(StringBuffer buffer, String field, int cnt)
    {
	    try
	    {
	        if("".equals(field))
	        {
	            fillZeroSpace(buffer, cnt);
	        }
	        else if(CommonUtil.getLength(field) > cnt)
	        {
	            buffer.append(CommonUtil.substr(field, 0, cnt));
	        }
	        else if(CommonUtil.getLength(field) < cnt)
	        {
	            fillZeroSpace(buffer, cnt - CommonUtil.getLength(field));
	            buffer.append(field);
	        }
	        else
	        {
	            buffer.append(field);
	        }
	    }
	    catch(Exception e)
	    {

	    }
    }

    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void fillSpace(StringBuffer buffer, int cnt)
    {
        for(int i = 0; i < cnt; i++)
        {
            buffer.append(" ");
        }
    }

    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void fillZeroSpace(StringBuffer buffer, int cnt)
    {
        for(int i = 0; i < cnt; i++)
        {
            buffer.append("0");
        }
    }

    /**
     * @param sendMsg
     * @param stocks
     * @param tc
     */
    private void sendAMsg(StringBuffer sendMsg, List stocks, Map tc)
    {
        JDTORecord dto 	= null;
        int loofCnt		= 8;
        int stocksCnt 	= stocks != null ? stocks.size() : 0;

        if(stocksCnt > 0)
        {
            int i = 0;
            for(i = 0; i < stocksCnt; i++)
            {
                dto = (JDTORecord)stocks.get(i);
                appendMsg(sendMsg, getField(dto, "STL_NO"),	getFieldLen(tc, "코일번호1"));
                appendMsg(sendMsg, commUtils.setLegacyPositionWithCur(getField(dto, "UP_LOC")),	getFieldLen(tc, "권상권하위치1"));
            }

            for(int j = i; j < loofCnt; j++)
            {
                appendMsg(sendMsg, "", 	getFieldLen(tc, "코일번호1"));
                appendMsg(sendMsg, "", 	getFieldLen(tc, "권상권하위치1"));
            }
        }
        else
        {
            for(int i = 0; i < loofCnt; i++)
            {
                appendMsg(sendMsg, "", 	getFieldLen(tc, "코일번호1"));
                appendMsg(sendMsg, "", 	getFieldLen(tc, "권상권하위치1"));
            }
        }
    }

    /**
     * @param sendMsg
     * @param stocks
     * @param tc
     */
    private void sendBMsg(String tcCd, StringBuffer sendMsg, List stocks, Map tc, String field, int loofCnt)
    {
        JDTORecord dto = null;
        int stocksCnt = stocks != null ? stocks.size() : 0;

        if(stocksCnt > 0)
        {
            int i = 0;

            for(i = 0; i < stocksCnt; i++)
            {
                dto = (JDTORecord)stocks.get(i);
                appendMsgNum(sendMsg, getField(dto, "YD_STK_LYR_NO"), getFieldLen(tc, "송신Seq"));
                appendMsg(sendMsg, getField(dto, "STL_NO"), getFieldLen(tc, field));
                sendQueue(tcCd, sendMsg.toString());
                deleteSendMsg(sendMsg, loofCnt);
            }
        }
        else
        {
            for(int i = 0; i < loofCnt; i++)
            {
                appendMsgNum(sendMsg, ""+ (i + 1), getFieldLen(tc, "송신Seq"));
                appendMsg(sendMsg, "", getFieldLen(tc, field));
                sendQueue(tcCd, sendMsg.toString());
                deleteSendMsg(sendMsg, loofCnt);
            }
        }
    }

    /**
     * @param loofCnt
     */
    private void deleteSendMsg(StringBuffer sendMsg, int loofCnt)
    {
        if(loofCnt == 8)
        {
            sendMsg.delete(sendMsg.length() - 12, sendMsg.length());
        }
        else
        {
            sendMsg.delete(sendMsg.length() - 13, sendMsg.length());
        }
    }

    /**
     * 송신 EJB를 이용하여 송신데이터를 송신한다.
     * @param methodName	TC명
     * @param sendMsg		송신데이터
     * @throws Exception
     */
    private void sendQueue(String methodName, String sendMsg)
    {
        EJBConnector ejbConn = null;

    	try
    	{
    	    ejbConn = new EJBConnector("default", "JNDIYardWrkResReg",this);		//차후 수정해야함
            ejbConn.trx("send"+ methodName, new Class[]{ String.class }, new Object[]{ sendMsg });
        }
    	catch(Exception e)
    	{
            throw new EJBServiceException(e);
        }
    }

    /**
     * 지시구분을 리턴한다.
     * @param schKind
     * @param yd
     * @return
     */
    private String getOrderGp(String yd_sch_cd)
    {
    	if
    	(
    		YfConstant.NEW_SCH_WORK_KIND_GVML.equals(yd_sch_cd)|| // COIL 제품이송상차
    		YfConstant.NEW_SCH_WORK_KIND_GVM2.equals(yd_sch_cd)|| // COIL 제품이송상차
    		YfConstant.NEW_SCH_WORK_KIND_GVM3.equals(yd_sch_cd)|| // COIL 제품이송상차
    		YfConstant.NEW_SCH_WORK_KIND_GVMU.equals(yd_sch_cd)|| // COIL 제품이송하차
    		YfConstant.NEW_SCH_WORK_KIND_GVM4.equals(yd_sch_cd)|| // COIL 제품이송하차
    		YfConstant.NEW_SCH_WORK_KIND_GVM5.equals(yd_sch_cd)
    	)
    	{
    		return YfConstant.COIL_ORDER_GP_2;	// COIL 제품이송하차
    	}
    	else if
    	(
    		YfConstant.NEW_SCH_WORK_KIND_CVML.equals(yd_sch_cd)|| // COIL 소재이송상차
    		YfConstant.NEW_SCH_WORK_KIND_CVM2.equals(yd_sch_cd)|| // COIL 소재이송상차
    		YfConstant.NEW_SCH_WORK_KIND_CVM3.equals(yd_sch_cd)|| // COIL 소재이송상차
    		YfConstant.NEW_SCH_WORK_KIND_CVMU.equals(yd_sch_cd)|| // COIL 소재이송하차
    		YfConstant.NEW_SCH_WORK_KIND_CVM4.equals(yd_sch_cd)|| // COIL 소재이송하차
    		YfConstant.NEW_SCH_WORK_KIND_CVM5.equals(yd_sch_cd)
    	)
    	{
    		return YfConstant.COIL_ORDER_GP_1;	// COIL 소재이송하차
    	}
    	else if
    	(
    		YfConstant.NEW_SCH_WORK_KIND_GVFL.equals(yd_sch_cd)|| // COIL 제품출하상차
    		YfConstant.NEW_SCH_WORK_KIND_GVF1.equals(yd_sch_cd)|| // Coil 제품출하상차
    		YfConstant.NEW_SCH_WORK_KIND_GVF2.equals(yd_sch_cd)||

    		YfConstant.NEW_SCH_WORK_KIND_GTFL.equals(yd_sch_cd)|| // COIL 제품출하상차
    		YfConstant.NEW_SCH_WORK_KIND_GTF1.equals(yd_sch_cd)|| // Coil 제품출하상차
    		YfConstant.NEW_SCH_WORK_KIND_GTF2.equals(yd_sch_cd)||

    		YfConstant.NEW_SCH_WORK_KIND_GPFL.equals(yd_sch_cd)|| // COIL 제품출하상차
    		YfConstant.NEW_SCH_WORK_KIND_GPF1.equals(yd_sch_cd)|| // Coil 제품출하상차
    		YfConstant.NEW_SCH_WORK_KIND_GPF2.equals(yd_sch_cd)
    	)
    	{
    		return YfConstant.COIL_ORDER_GP_3;	// Coil 제품출하상차
        }
    	else if(YfConstant.NEW_SCH_WORK_KIND_SVFL.equals(yd_sch_cd))
    	{
            return YfConstant.SLAB_ORDER_GP_4;
        }
    	else if(YfConstant.NEW_SCH_WORK_KIND_SVML.equals(yd_sch_cd))
    	{
            return YfConstant.SLAB_ORDER_GP_4;
        }
    	else if(YfConstant.NEW_SCH_WORK_KIND_SVMU.equals(yd_sch_cd))
    	{
            return YfConstant.SLAB_ORDER_GP_2;
        }

        return "3";
    }

    /**
	 * 오퍼레이션명 :
	 *
	 * 차량도착 정보를 처리한다.
        * 1.TC_CD	: 없음.
        * 2.I/F ID	: 없음.
        * param 	moveGp	차량 출하'1', 이송'2', 이적'3' 구분
        * param 	cardNo	카드번호
        * param 	pos		정지위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 */
	public boolean carArrival(String moveGp, String cardNo, String pos)
	{
        String methodNm	= "[YfCommCarMvSeEJB.carArrival]";
		String szMsg	= "";

	    try
	    {
	    	commUtils.printLog(classNm, methodNm, "S+");

	    	szMsg = "차량도착 정보 처리 수신MSG: "+ (moveGp + cardNo + pos);
			commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	    	//육송인 경우에만 처리 함.
	    	if( !"T".equals(cardNo.substring(0, 1)) && !"P".equals(cardNo.substring(0, 1)) )
	    	{
	    		/**
	    		 * 차량카드번호, 정지위치 체크
	    		 */
	    		validCarArrivalAndStart(cardNo, pos);
	    	}

	    	//육송인 경우에만 처리 함.
    		if
    		(
    			!"T".equals(cardNo.substring(0, 1)) &&
    			!"P".equals(cardNo.substring(0, 1)) &&
    			!"A".equals(cardNo.substring(0, 1)) &&
    			!"B".equals(cardNo.substring(0, 1)) &&
    			!"C".equals(cardNo.substring(0, 1)) &&
    			!"K".equals(cardNo.substring(0, 1)) &&
    			!"S".equals(cardNo.substring(0, 1))
    		)
    		{
		       /*
		        * 카드번호에 따라서 if문의 처리를 다르게 한다.
		        * if문 조건 추가.
		        */
    			if(Integer.parseInt(cardNo) >= 9990 && Integer.parseInt(cardNo) <= 9994 )	//차량동간이적일때...
    			{
		        	szMsg = "차량이적에 대한 조건 처리 ";
		    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		        	if(isCarStart01(pos, cardNo))
		        	{
			        	arrivalOfShippingOrTrans(cardNo, pos, moveGp);
			        }
		        	else
		        	{
			        	return false;
			        }
		        }
    			else
    			{
		        	szMsg = "차량이적 외 조건 처리 ";
		    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		        	if(!"T".equals(moveGp)&&!"R".equals(moveGp))
		        	{
		        		//내수 출하인 경우
		        		szMsg = "내수 출하인 경우 ";
			    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				        if(isCarStartT(pos, cardNo))
				        {
				        	szMsg = "하이스코 2냉연 차량예약이 존재 안 합니다.";
				    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
				        }
				        else
				        {
				        	szMsg = "하이스코 2냉연 차량예약이 존재 합니다.";
				    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				    		return false;
				        }
		        	}

		        	if("R".equals(moveGp))
		        	{
		        		szMsg = "2냉연 트레일러 도착처리 ***********";
			    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		        		arrivalOfShippingOrTrans(cardNo, pos, moveGp);
		        	}
		        	else
		        	{
			        	if(isCarStart(pos, cardNo))
			        	{
				        	arrivalOfShippingOrTrans(cardNo, pos, moveGp);
				        }
			        	else
			        	{
				        	return false;
				        }
		        	}
		        }
	    	}
    		else
    		{
	        	szMsg = "차량이적 외 조건 처리2";
	    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		        if(isCarStart(pos, cardNo))
		        {
		        	arrivalOfShippingOrTrans(cardNo, pos, moveGp);
		        }
		        else
		        {
		        	return false;
		        }
	    	}

    		commUtils.printLog(classNm, methodNm, "S-");
	    }
	    catch(DAOException daoe)
	    {
            throw daoe;
        }
	    catch(Exception e)
	    {
            throw new EJBServiceException(e);
        }

	    return true;
	}

	/**
     * 차량 정지 위치의 차량카드번호가 존재하는지 리턴한다.
     * 차량 도착 위치의 카드번호를 검사한다.
     * 기능 추가. 현재 도착처리 동이 정확한 동인지 검사.
     *
     * @param pos 		차량정지위치
     * @param cardNo    차량카드번호
     * @return
     */
	private boolean isCarStart01(String pos, String cardNo)
	{
		String methodNm	= "[YfCommCarMvSeEJB.isCarStart01]";
		String szMsg	= "";

		szMsg = "isCarStart01() 차량 정지 위치의 차량카드번호 검사.";
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		JDTORecord dto = commDao.readCardNo( pos );

		if(! "".equals(getField(dto, "CAR_CARD_NO")))
		{
			//카드번호가 없으면
			return false;
		}
		else
		{
			//카드번호가 존재함
			//동일 차량카드번호 존재하는지 확인
			szMsg = "동일 차량카드번호 존재하는지 확인";
			commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

			dto = commDao.readCardNo(selectCardNo3, pos, cardNo);

			if(dto != null)
			{
				szMsg = "동일 차량카드번호 존재함. (dto != null)";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				return false;
			}

			szMsg = "동일 차량카드번호 존재안함";
			commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

			return true;
		}
	}

	/**
	 * 차량 정지 위치의 차량카드번호가 존재하는지 리턴한다.
     * @param pos		차량정지위치
     * @param cardNo	차량카드번호
     * @return
     */
    public boolean isCarStartT(String pos, String cardNo)
    {
    	String methodNm	= "[YfCommCarMvSeEJB.isCarStartT]";
		String szMsg	= "";

        //해당 정지포인트에 차량카드번호가 존재하는지 확인
        JDTORecord dto = commDao.readCardNoT(pos);

        szMsg = "isCarStartT() 점유차량 카드번호:"+getField(dto, "CAR_CARD_NO");
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

        if(! "".equals(getField(dto, "CAR_CARD_NO")))
        {
            return false;
        }
        else
        {
            //동일 차량카드번호 존재하는지 확인
            dto = commDao.readCardNo(pos.substring(0, 1), cardNo);

            if(dto != null)
            {
                return false;
            }

            return true;
        }
    }

    /**
	 * 차량 정지 위치의 차량카드번호가 존재하는지 리턴한다.
     * @param pos		차량정지위치
     * @param cardNo	차량카드번호
     * @return
     */
    public boolean isCarStart(String pos, String cardNo)
    {
    	String methodNm	= "[YfCommCarMvSeEJB.isCarStart]";
		String szMsg	= "";

        //해당 정지포인트에 차량카드번호가 존재하는지 확인
        JDTORecord dto = commDao.readCardNo(pos);

        szMsg = "isCarStart() 점유차량 카드번호:"+getField(dto, "CAR_CARD_NO");
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

        if(! "".equals(getField(dto, "CAR_CARD_NO")))
        {
            return false;
        }
        else
        {
            //동일 차량카드번호 존재하는지 확인
            dto = commDao.readCardNo(pos.substring(0, 1), cardNo);

            if(dto != null)
            {
                return false;
            }

            return true;
        }
    }

    /**
	 * 차량 및 팔레트 출하/이송/이적 도착을 처리한다.
	 *	- A/B열연 코일 출하/이송상차/이송하차
	 *	- B열연 슬라브 이송상차/이송하차/팔레트이적하차 도착처리
	 *  - 차량이적에 관련된 내용 추가    최규성
	 * @param cardNo	카드번호
	 * @param pos		차량정지위치
	 * @param moveGp
	 */
	private void arrivalOfShippingOrTrans(String cardNo, String pos, String moveGp) throws Exception
	{
		String methodNm	= "[YfCommCarMvSeEJB.arrivalOfShippingOrTrans]";
		String szMsg	= "";

		boolean isSendOrder		= true;
		JDTORecord recInTemp	= null;
		YdCarSchDao	ydCarSchDao	= new YdCarSchDao();

    	try
    	{
	        //팔레트번호 <=> 카드번호
	        cardNo = changpalletnocardno(cardNo);

	        szMsg = "도착처리 : 카드번호=>" + cardNo;
			commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        /**
	         *	카드번호로 복수동 도착처리인지 확인
	         *	=> 차량 출발시 저장품에 차량MAP 정보를 셋팅하므로 이를 기준으로 복수동인지 체크.
	         * 	=> 복수동이면 현재MAP에 저장품 정보를 UPDATE
	         */
	    	List multys = commDao.readMultyBay(cardNo);

	    	//육송인 경우에만 처리 함.
	    	if
	    	(
	    		!"T".equals(cardNo.substring(0, 1)) &&
	    		!"P".equals(cardNo.substring(0, 1)) &&
	    		!"A".equals(cardNo.substring(0, 1)) &&
	    		!"B".equals(cardNo.substring(0, 1)) &&
	    		!"C".equals(cardNo.substring(0, 1)) &&
	    		!"K".equals(cardNo.substring(0, 1)) &&
	    		!"S".equals(cardNo.substring(0, 1))
	    	)
	    	{
		    	//=============================================================================================
		    	// 차량이적을 위해 코드 추가.
		    	// 기존의 복수동 관련 쿼리가 변경되어 예전 쿼리로 조회하도록 추가함.
		    	if(Integer.parseInt(cardNo)>= 9990 && Integer.parseInt(cardNo) <= 9994)
		    	{
		    		multys = commDao.readMultyBay(cardNo, 18);
		    	}
		    	//=============================================================================================
	    	}

	        int multysCnt 	= multys != null ? multys.size() : 0;

	        szMsg = "도착처리 : 복수동 정보" + String.valueOf(multysCnt);
			commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        /**
	         *	차량 상차 저장품 정보를 가져온다.
	         *	=> 카드번호를 기준으로 작업예약된 정보를 가져온다.
	         */
	        String yd 	= pos.substring(0, 1);
	        String bay	= pos.substring(1, 2);
	        List stocks	= commDao.readStockOfCarLoad(cardNo, yd, bay);
	        int stocksCnt = stocks != null ? stocks.size() : 0;

	        JDTORecord stock3 = null;
			stock3 = (JDTORecord)stocks.get(0);
            String szCAR_NO = StringHelper.evl(getField(stock3, "CAR_NO"),"");

	        /**
	         *	현물의 위치와 차량 정지위치가 맞는지 확인한다.
	         */
	        if(stocksCnt > 0)
	        {
	        	//TT CAR인경우 15개 차상위치를 활성화
		        if("T".equals(moveGp))
		        {
		        	stocksCnt=15 ;
		        }

	        	considerHasStockOfPoint((JDTORecord)stocks.get(0), pos.substring(0, 2));
	        }
	        else if(multysCnt == 0)
	        {
	        	throw new Exception("### 산적위치를 확인 하십시요.");
	        }

	        /**
	         *	설비 MAP '적재상태', 적치열 '차량 CARD 번호' UPDATE
	         *	=> 차량이 도착 되었으므로 차량MAP을 활성화 하고 적치열에 카드번호를 MAPPING 한다.
	         */
	        editCardMapOpen(multysCnt, stocksCnt, cardNo, pos, yd);

			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
	        this.YfCarPointinforeg("C", szCAR_NO, cardNo, pos, "", "", "L", classNm, "arrivalOfShippingOrTrans");

	        /**
	         * 차량 도착 정보를 처리한다.
	         * 1. 작업예약을 오퍼레이터 지정으로 UPDATE
	         * 2. SCH CALL
	         */
	        JDTORecord stock = null;

	        if(stocksCnt == 0)
	        {
	        	szMsg = "하차작업 예약";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        	if(multysCnt > 0)
	        	{
	        		/*
  		        	 *  1. 차량을 이용한 동간 이적일 경우 목적동을 확인  9990~9994
  		        	 */
	        		/* ======== Start ============*/
	        		if
	        		(
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_6.equals(cardNo)) ||
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_7.equals(cardNo)) ||
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_8.equals(cardNo)) ||
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_9.equals(cardNo)) ||
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_0.equals(cardNo))
	        		)
	        		{
	        			szMsg = "====>Info : 차량 이적 도착 처리====";
	    				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        			List whereData = new ArrayList();
	        			whereData.add(cardNo);

	        			List cardNumList = commDao.getListData(getCarCardNo2, whereData);	// 차량카드번호 존재 여부를 검사한다.

	        			szMsg = "차량이적 List: " + cardNumList;
	    				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        			JDTORecord jrecrd = (JDTORecord)cardNumList.get(0);

	        			szMsg = "차량이적 Record:" + jrecrd;
	    				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        			if (!(bay.equals(jrecrd.getField("CARUNLOAD_BAY"))))
	        			{
	        				throw new Exception("[차량동간이적] ### 목적동이 아닙니다. 정지위치 ERROR");
	        			}

	        			isSendOrder = false;
	        		}

	        		/* ======== End ============*/
	        		unloadReservation(multys, pos, pos.substring(0, 1), cardNo);

	        		szMsg = "차량 상차 저장품을 확인한다.인자:"+cardNo+yd+bay;
    				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        		if
	        		(
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_6.equals(cardNo)) ||
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_7.equals(cardNo)) ||
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_8.equals(cardNo)) ||
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_9.equals(cardNo)) ||
	        			(YfConstant.CAR_BAY_TRANS_CARD_NO_0.equals(cardNo))
	        		)
	        		{
	        			stocks = commDao.readStockOfCarLoad3(cardNo, yd, bay);

	        			szMsg = "차량이적 데이터 확인 List:" + stocks;
	    				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        			stocksCnt = stocks != null ? stocks.size() : 0;

	        			szMsg = "Select List Count"+String.valueOf(stocksCnt);
	    				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        			stock 		= (JDTORecord)stocks.get(0);
	        			isSendOrder = false;
	        		}
	        		else
	        		{
	        			stocks = commDao.readStockOfCarLoad(cardNo, yd, bay);

	        			szMsg = "데이터확인 List:" + stocks;
	    				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        			stocksCnt = stocks != null ? stocks.size() : 0;

	        			szMsg = "Select List Count"+String.valueOf(stocksCnt);
	    				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        			stock 		= (JDTORecord)stocks.get(0);
	        			isSendOrder = true;
	        		}
	        	}
	        	else
	        	{
	        		throw new Exception("정지위치를 확인 하십시요.");
	        	}
	        }
	        else
	        {
	        	stock = (JDTORecord)stocks.get(0);
	        }
	        /**
	         * 출하/이송/이적 구분.
	         */
	        szMsg = "출하/이송/이적 구분";
			commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

	        doArrival(stocks, stock, cardNo, pos);

	        // 차량이적 카드번호(9990~9994) 일 땐 출하관련 전송 처리를 하지 않음.
	        // 차량동간 이적번호 확인
	        if
	        (
	        	(!YfConstant.CAR_BAY_TRANS_CARD_NO_6.equals(cardNo)) &&
	        	(!YfConstant.CAR_BAY_TRANS_CARD_NO_7.equals(cardNo)) &&
	        	(!YfConstant.CAR_BAY_TRANS_CARD_NO_8.equals(cardNo)) &&
	        	(!YfConstant.CAR_BAY_TRANS_CARD_NO_9.equals(cardNo)) &&
	        	(!YfConstant.CAR_BAY_TRANS_CARD_NO_0.equals(cardNo))
	        )
	        {
	        	//DMYDR036 ET차량도착처리인 경우 출하에 전송을 안함
	        	if(!"Y".equals(moveGp))
	        	{
	        		if("1".equals(yd) || "2".equals(yd)|| "3".equals(yd))
	        		{
	        			//AB열연
	        			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	        			//출하차량도착실적(YDDMR029)
	        			//YD_GP				야드구분
	        			//TRANS_ORD_DT		운송지시일자
	        			//TRANS_ORD_SEQNO	운송지시순번
	        			//CAR_NO			차량번호
	        			//CARD_NO			카드번호
	        			//ARR_WLOC_CD     	착지개소코드
	        			//ARR_YD_PNT_CD   	착지야드포인트코드

	        			List wloccd = commDao.readStockOfwloc(pos);

	        			JDTORecord stock2 = null;
	        			stock2 = (JDTORecord)wloccd.get(0);

	        			Boolean isSuccess = new Boolean(false);

			 			JDTORecord tcRecordDM = JDTORecordFactory.getInstance().create();
			 			tcRecordDM.setField("YD_GP",			yd);
			 			tcRecordDM.setField("TRANS_WORD_DATE",	StringHelper.evl(getField(stock, "TRANS_WORD_DATE"),""));
			 			tcRecordDM.setField("TRANS_WORD_SEQNO",	StringHelper.evl(getField(stock, "TRANS_WORD_SEQNO"),""));
			 			tcRecordDM.setField("CAR_NO",			StringHelper.evl(getField(stock, "CAR_NO"),""));
			 			tcRecordDM.setField("CARD_NO",			cardNo);
			 			tcRecordDM.setField("ARR_WLOC_CD",		StringHelper.evl(getField(stock2, "WLOC_CD"),""));
			 			tcRecordDM.setField("ARR_YD_PNT_CD",	StringHelper.evl(getField(stock2, "YD_PNT_CD"),""));

			 			//인터페이스 전문 호출
						EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);    //차후 수정해야함
			 			isSuccess = (Boolean)ejbConn1.trx("getYDDMR029",new Class[]{JDTORecord.class},
			 			  	  	 new Object[]{tcRecordDM});

			            szMsg = "내부IF호출=== 일관제철 AB열연 출하차량도착실적.===";
						commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

			            String szYD_EQP_WRK_STAT = StringHelper.evl(getField(stock2, "YD_EQP_WRK_STAT"),"");

			            //차량스케줄 도착등록,
			            recInTemp = JDTORecordFactory.getInstance().create();
			            recInTemp.setField("YD_CAR_SCH_ID", StringHelper.evl(getField(stock2, "YD_CAR_SCH_ID"),""));
			            recInTemp.setField("MODIFIER", 		"arrivalOf");

			            if("L".equals(szYD_EQP_WRK_STAT) )
			            {
							recInTemp.setField("YD_CAR_PROG_STAT", "B");									//하차도착상태
							recInTemp.setField("YD_CARUD_WRK_BOOK_ID", 	StringHelper.evl(getField(stock2, "WBOOK_ID"),""));
							recInTemp.setField("YD_CARUD_STOP_LOC", 	pos);
							recInTemp.setField("YD_CARUD_ARR_DT", 		StringHelper.evl(getField(stock2, "CURDATE"),""));
							recInTemp.setField("YD_PNT_CD3", 			StringHelper.evl(getField(stock2, "YD_PNT_CD"),""));
			            }
			            else
			            {
							recInTemp.setField("YD_CAR_PROG_STAT", "2");									//상차도착상태
							recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 	StringHelper.evl(getField(stock2, "WBOOK_ID"),""));
							recInTemp.setField("YD_CARLD_STOP_LOC", 	pos);
							recInTemp.setField("YD_CARLD_ARR_DT", 		StringHelper.evl(getField(stock2, "CURDATE"),""));
							recInTemp.setField("YD_PNT_CD1", 			StringHelper.evl(getField(stock2, "YD_PNT_CD"),""));
			            }

			            int intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);

			            if( intRtnVal <= 0 )
			            {
			            	szMsg = "[" + methodNm + "] 차량스케줄 도착등록 시 오류발생[반환값 : " + intRtnVal + "]";
							commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
			            }
	        		}
	        	}
	        }

	        /**
	         * 야드 L-2 송신(부두송신 안함)
	         *   ==> 차량동간 이적일때(송신안함)
	         *   isSendOrder == true;
	         */
	        if (isSendOrder)
	        {
	        	szMsg = "차량동간 이적일 때 송신 안함. 조건상태:TRUE";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
	        	sendStartAndArrivalOrder(stocks, stock, cardNo, pos, YfConstant.CAR_GP_D);
	        }
    	}
    	catch (Exception e)
    	{
    		szMsg = "차량 도착 처리중 에러 발생";
			commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
    		e.printStackTrace();
    	}
	}

	/**
	 * 오퍼레이션명 :
	 * 슬라브 팔레트번호로 도착처리시
	 *	=> 카드번호를 리턴한다.
	 *	=> 팔레트번호를 삭제한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	*/
	public String changpalletnocardno(String cardNo)
	{
		String methodNm	= "[YfCommCarMvSeEJB.changpalletnocardno]";
		String szMsg	= "";

		szMsg = "card_no가 Pallet_No인지 검색 시작" + cardNo;
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		List cardnoList 	= commDao.getCommonList(changpalletnocardno, new Object[]{cardNo});
		int stocksCnt 		= cardnoList != null ? cardnoList.size() : 0;

		if(stocksCnt > 0)
		{
			String query1 		= updatePalletNO;
			int count 			= commDao.updateData(query1, new Object[]{cardNo});
			JDTORecord jrecrd	= (JDTORecord)cardnoList.get(0);
			cardNo 				= StringHelper.evl(jrecrd.getFieldString("CAR_CARD_NO"),cardNo);
		}

		szMsg = "반환 되는 CARD_NO :" + cardNo;
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		return cardNo;
	}

	/**
     * 현물의 위치와 차량 정지위치가 맞는지 확인한다.
     * @param stocks
     */
    private void considerHasStockOfPoint(JDTORecord dto, String pos) throws Exception
    {
    	String methodNm	= "[YfCommCarMvSeEJB.considerHasStockOfPoint]";
		String szMsg	= "";

        if(! pos.equals(getField(dto, "CURR_STOCK_LOC")))
        {
        	szMsg = "반환 되는 CARD_NO :" + "현물의 위치: 차량 정지위치=>"+pos+":"+getField(dto, "CURR_STOCK_LOC");
    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

            throw new Exception("상차 위치가 다릅니다. 차량정지위치를 확인하십시요.");
        }
    }

    /**
     * 차량CARD번호와 설비정보를 UPDATE
     * @param multyCnt	복수동 차량상차 저장품 정보
     * @param stocks	차량상차 저장품 정보
     * @param stocksCnt	차량상차 저장품 수
     * @param cardNo	차량CARD번호
     * @param pos		차량정지위치
     */
    private void editCardMapOpen(int multyCnt, int stocksCnt, String cardNo, String pos, String yd)
    {
    	/**
    	 * COIL STACKER와 SLAB STACKER가 상이
    	 * -COIL: 적치대의 BED가 1씩 증가, 적치단은 '01'로 고정
    	 * -SLAB: 적치대는 '01'로 고정, 적치단의 단 수가 1씩 증가
    	 */
    	commDao.modifyCardNoOfStackCol(cardNo, pos);

    	//차량예약 포인트 지우기
    	commDao.modifyCardNoOfStackCol2(cardNo);

    	for(int i = 0; i < stocksCnt; i++)
    	{
    		if(YfConstant.YD_GP_0.equals(yd))
    		{
                //A열연 SLAB 야드 추가
               	commDao.modifyActiveStatOfLayer(YfConstant.STACK_LAYER_ACTIVE_STAT_O, pos, "01", "0"+ (i + 1));
    		}
    		else
    		{
    			/*
    			 * 차량 도착시 STL_NO is null 처리
    			 */
    			if(multyCnt > 0)
    			{
    				commDao.modifyActiveStatOfLayer_02(YfConstant.STACK_LAYER_ACTIVE_STAT_O, pos, "0"+ (multyCnt + (i + 1)));
    			}
    			else
    			{
    				commDao.modifyActiveStatOfLayer_02(YfConstant.STACK_LAYER_ACTIVE_STAT_O, pos, "0"+ (i + 1));
    			}
    		}
    	}
    }

    private void unloadReservation(List stocks, String pos, String yd, String cardNo) throws Exception
    {
    	String methodNm	= "[YfCommCarMvSeEJB.unloadReservation]";
		String szMsg	= "";

		szMsg = "Start - unloadReservation(List,String,string,string) :overloading "+stocks+":"+pos+":"+yd+":"+cardNo;
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

    	int stocksCnt = stocks != null ? stocks.size() : 0;

        if(YfConstant.YD_GP_0.equals(yd))
        {
        	szMsg = "카드번호 인자 추가." + cardNo;
    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

            slabUnloadWork(stocks, pos, stocksCnt);
        }
        else
        {
        	//차량동간이적 9990~9994
			if
			(
				(YfConstant.CAR_BAY_TRANS_CARD_NO_6.equals(cardNo)) ||
				(YfConstant.CAR_BAY_TRANS_CARD_NO_7.equals(cardNo)) ||
				(YfConstant.CAR_BAY_TRANS_CARD_NO_8.equals(cardNo)) ||
				(YfConstant.CAR_BAY_TRANS_CARD_NO_9.equals(cardNo)) ||
				(YfConstant.CAR_BAY_TRANS_CARD_NO_0.equals(cardNo))
			)
			{
				szMsg = "coilUnloadWork(stocks, pos, stocksCnt, 'Y') 실행";
	    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				coilUnloadWork(stocks, pos, stocksCnt, "Y");
			}
			else
			{
				szMsg = "coilUnloadWork(stocks, pos, stocksCnt, 'N') 실행";
	    		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				coilUnloadWork(stocks, pos, stocksCnt, "N");
			}
        }

        szMsg = "End - unloadReservation(List,String,string,string) : ";
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
    }

    /**
     * 슬라브 하차 작업 예약을 처리한다.
     * @param stocks	저장품정보
     * @param pos		차량정지위치
     * @param stocksCnt	저장품 개수
     * @throws Exception
     */
    private void slabUnloadWork(List stocks, String pos, int stocksCnt) throws Exception
    {
    	String methodNm	= "[YfCommCarMvSeEJB.slabUnloadWork]";
		String szMsg	= "";

        JDTORecord dto 		= null;
        String nextWBookId 	= null;

        szMsg = "slabUnloadWork(List, String, int) 슬라브 하차 작업 예약을 처리 ";
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

        for(int i = 0; i < stocksCnt; i++)
        {
            dto = (JDTORecord)stocks.get(i);
            nextWBookId = commDao.createWBook(pos, getUnloadSchKind(getField(dto, "STOCK_ITEM"), pos), YfConstant.SCH_WORK_LOC_DECISION_METHOD_S, "", dto, classNm, "slabUnloadWork");

            commDao.modifyTermAndWBookIdOfStock
            (
            	nextWBookId,
            	getStockMoveTerm(getField(dto, "STOCK_ITEM"),
            	getField(dto, "FRTOMOVE_EQUIP_GP").substring(0, 1)),
            	getField(dto, "STL_NO")
            );

            commDao.modifyLayerStatOfLayer
            (
            	YfConstant.STACK_LAYER_STAT_S,
            	pos,
            	getField(dto, "STL_NO")
            );
        }

        szMsg = "End - slabUnloadWork() ";
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
    }

    /**
     * 스케쥴코드를 리턴한다.
     * @param col	차량정지위치
     * @return
     */
    private String getUnloadSchKind(String item, String pos)
    {
    	String schCd	= "";
    	String LR		= "";
    	String MATL		= "";

        if(YfConstant.ITEM_SM.equals(item))
        {
            //return YfConstant.NEW_SCH_WORK_KIND_SVMU;
            if(YfConstant.YD_GP_0.equals(pos.substring(0, 1)))
            {
            	MATL	= "1";	//소재

            	if("01".equals(pos.substring(4, 6)) || "02".equals(pos.substring(4, 6)))	//좌우 구분
            	{
            		LR	= "2";	//R
            	}
            	else
            	{
            		LR	= "1";	//L
            	}

                schCd = pos.substring(0, 2) + "PT" + MATL + LR + "LM";
            }
        }
        else
        {
            if(YfConstant.YD_GP_1.equals(pos.substring(0, 1)))
            {
            	if(YfConstant.ITEM_CM.equals(item))	//소재구분
                {
                	MATL	= "1";	//소재
                }
                else if(YfConstant.ITEM_CG.equals(item))
                {
                	MATL	= "2";	//제품
                }
                else
                {
                	MATL	= "3";	//동간이적
                }

            	if("01".equals(pos.substring(4, 6)) || "02".equals(pos.substring(4, 6)))	//좌우 구분	1(L)/2(R)
            	{
            		LR	= "1";	//L
            	}
            	else
            	{
            		LR	= "2";	//R
            	}

                schCd = pos.substring(0, 2) + "PT" + MATL + LR + "LM";
            }
        }

        return schCd;
    }

    /**
     * 차량도착후 하차작업예약을 생성하는 시점에서
     * 저장품의 이동조건을 셋팅하는 부분.
     * 저장품 이동조건에 대한 코드를 수정한다.
     * 저장품 이동조건을 리턴한다.
     * @param item	저장품품목
     * @return
     */
    private String getStockMoveTerm(String item, String yd)
    {
    	//차후 수정해야함

        if(YfConstant.ITEM_CM.equals(item))
        {
            return YfConstant.NEW_STOCK_MOVE_TERM_CS;
        }
        else if(YfConstant.ITEM_CG.equals(item))
        {
        	return YfConstant.NEW_STOCK_MOVE_TERM_CS;
        }
        else if(YfConstant.ITEM_SM.equals(item))
        {
           if(YfConstant.YD_GP_0.equals(yd))
           {
        	   return YfConstant.NEW_STOCK_MOVE_TERM_VW;
           }
           else
           {
              return YfConstant.NEW_STOCK_MOVE_TERM_VM;
           }
        }

        return "";
    }

    /**
	 * 차량 도착 정보를 처리한다.
	 * 1. 작업예약 UPDATE(MCH)
	 * 2. SCH CALL
	 * @param stocks	차량 도착 정보
	 * @param pos		차량 정지 위치
	 * @param gp		출하,이송 구분
	 * @throws Exception
	 */
	private void doArrival(	List stocks, JDTORecord dto, String cardNo, String pos) throws Exception
	{
		String methodNm	= "[YfCommCarMvSeEJB.doArrival]";
		String szMsg	= "";

		String yd = pos.substring(0, 1);
		String sSchCode ="";

		szMsg = "작업예약의 스케쥴 수행 방법을 stocks - " + stocks;
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
		szMsg = "작업예약의 스케쥴 수행 방법을 YD_SCH_CD - " + getField(dto, "YD_SCH_CD");
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
		szMsg = "작업예약의 스케쥴 수행 방법을 pos - "+pos;
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
		szMsg = "작업예약의 스케쥴 수행 방법을 STOCK_ITEM - " + getField(dto, "STOCK_ITEM");
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
		szMsg = "작업예약의 스케쥴 수행 방법을 cardNo - " + cardNo;
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		sSchCode = getField(dto, "YD_SCH_CD");

    	//육송인 경우에만 처리 함.
		if
		(
			!"T".equals(cardNo.substring(0, 1)) &&
			!"P".equals(cardNo.substring(0, 1)) &&
			!"A".equals(cardNo.substring(0, 1)) &&
			!"B".equals(cardNo.substring(0, 1)) &&
			!"C".equals(cardNo.substring(0, 1)) &&
			!"K".equals(cardNo.substring(0, 1)) &&
			!"S".equals(cardNo.substring(0, 1))
		)
		{
			if(Integer.parseInt(cardNo)>= 9990 && Integer.parseInt(cardNo) <= 9995)	//차량동간이적
			{
				szMsg = "차량이적시 처리";
				commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

				editSchMethodOfWBook(stocks, getField(dto, "YD_SCH_CD"), pos, getField(dto, "STOCK_ITEM"), "Y");

				//commDao.modifyWBookAndLoadSchOfEquip(commUtils.getStringYMDHM(), getField(dto, "YD_SCH_CD"), pos);	//설비테이블 변경으로 항목이 사라져서 사용한함
			}
			else
			{
				//제품이송 하차인 경우 처리
				if(YfConstant.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode) || YfConstant.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode))	//차후 수정해야함
				{
					for(int i = 0; i < stocks.size(); i++)
					{
						commDao.modifyOperatorOfWBook(sSchCode, YfConstant.SCH_WORK_LOC_DECISION_METHOD_S, "", getField((JDTORecord)stocks.get(i), "WBOOK_ID"));
					}
				}
				else
				{
					editSchMethodOfWBook(stocks, getField(dto, "YD_SCH_CD"), pos, getField(dto, "STOCK_ITEM"));
				}

				//commDao.modifyWBookAndLoadSchOfEquip(commUtils.getStringYMDHM(), getField(dto, "YD_SCH_CD"), pos);	//설비테이블 변경으로 항목이 사라져서 사용한함
			}
    	}
		else
		{
			editSchMethodOfWBook(stocks, getField(dto, "YD_SCH_CD"), pos, getField(dto, "STOCK_ITEM"));

			//commDao.modifyWBookAndLoadSchOfEquip(commUtils.getStringYMDHM(), getField(dto, "YD_SCH_CD"), pos);	//설비테이블 변경으로 항목이 사라져서 사용한함
    	}

		szMsg = "차량 도착 후 예약된 작업의 스케쥴을 CALL";
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		callCarArrivalSch(stocks, stocks.size(), yd);
	}

	/** 차량이적
     * 코일 하차 작업 예약을 처리한다.
     * @param stocks	저장품정보
     * @param pos		차량정지위치
     * @param stocksCnt	저장품 개수
	 * @throws Exception
     */
    private void coilUnloadWork(List stocks, String pos, int stocksCnt,String carMoveYN) throws Exception
    {
    	String methodNm	= "[YfCommCarMvSeEJB.coilUnloadWork]";
		String szMsg	= "";

        JDTORecord dto 		= null;
        String nextWBookId 	= null;

        szMsg = "coilUnloadWork(List, String, int, String) 코일 하차 작업 예약을 처리 ";
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		for(int i = 0; i < stocksCnt; i++)
		{
            dto	= (JDTORecord)stocks.get(i);
            nextWBookId = commDao.createWBook(pos, getUnloadSchKind(getField(dto, "STOCK_ITEM"), pos, carMoveYN), YfConstant.SCH_WORK_LOC_DECISION_METHOD_S, "", dto, classNm, "coilUnloadWork");

            commDao.modifyTermAndWBookIdOfStock
            (
            	nextWBookId,
            	getStockMoveTerm(getField(dto, "STOCK_ITEM"),
            	getField(dto, "FRTOMOVE_EQUIP_GP").substring(0, 1)),
            	getField(dto, "STL_NO")
            );

            commDao.modifyLayerStatOfLayer
            (
            	YfConstant.STACK_LAYER_STAT_S,
            	pos,
            	getField(dto, "STL_NO")
            );
        }

		szMsg = "End - coilUnloadWork() ";
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
    }

    private String getUnloadSchKind(String item, String pos, String carMoveYN)
    {
    	String schCd	= "";
    	String LR		= "";
    	String MATL		= "";

        if(YfConstant.ITEM_SM.equals(item))
        {
            //return YfConstant.NEW_SCH_WORK_KIND_SVMU;
            if(YfConstant.YD_GP_0.equals(pos.substring(0, 1)))
            {
            	MATL	= "1";	//소재

            	if("01".equals(pos.substring(4, 6)) || "02".equals(pos.substring(4, 6)))	//좌우 구분
            	{
            		LR	= "2";	//R
            	}
            	else
            	{
            		LR	= "1";	//L
            	}

                schCd = pos.substring(0, 2) + "PT" + MATL + LR + "LM";
            }
        }
        else
        {
            if(YfConstant.YD_GP_1.equals(pos.substring(0, 1)))
            {
            	if(YfConstant.ITEM_CM.equals(item))	//소재구분
                {
                	MATL	= "1";	//소재
                }
                else if(YfConstant.ITEM_CG.equals(item))
                {
                	MATL	= "2";	//제품
                }
                else
                {
                	MATL	= "3";	//동간이적
                }

            	if("01".equals(pos.substring(4, 6)) || "02".equals(pos.substring(4, 6)))	//좌우 구분
            	{
            		LR	= "2";	//R
            	}
            	else
            	{
            		LR	= "1";	//L
            	}

                schCd = pos.substring(0, 2) + "PT" + MATL + LR + "LM";
            }
        }

        return schCd;
    }

    /**
	 * 작업예약의 스케쥴 수행 방법을 UPDATE
	 * @param carInfos	차량 도착 정보
	 * @param pos		차량 정지 위치
	*/
	private void editSchMethodOfWBook(List carInfos, String schKind, String pos, String item, String carMoveYN)
	{
		String methodNm	= "[YfCommCarMvSeEJB.editSchMethodOfWBook]";
		String szMsg	= "";

		szMsg = "Start - editSchMethodOfWBook(List,String,String,String,String) :overloading ";
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);

		if ("Y".equals(carMoveYN) )
		{
			if(! getUnloadSchKind(item, pos, carMoveYN).equals(schKind))
			{
				// 하차스케줄인지 검사. 아니면..
				for(int i = 0; i < carInfos.size(); i++)
				{
					commDao.modifyOperatorOfWBook
					(
						getSchWorkKind(schKind, pos, carMoveYN),
						YfConstant.SCH_WORK_LOC_DECISION_METHOD_O,
						pos,
						getField((JDTORecord)carInfos.get(i), "WBOOK_ID")
					);
				}
			}
		}

		szMsg = "End - editSchMethodOfWBook(List,String,String,String,String) :overloading ";
		commUtils.putLog(classNm, methodNm, szMsg, YfConstant.DEBUG);
	}

	private String getSchWorkKind(String item, String pos,String carMoveYN )
	{
		String schCd	= "";
    	String LR		= "";
    	String MATL		= "";

    	if(YfConstant.ITEM_SM.equals(item))
        {
            //return YfConstant.NEW_SCH_WORK_KIND_SVML;
            if(YfConstant.YD_GP_0.equals(pos.substring(0, 1)))
            {
            	MATL	= "1";	//소재

            	if("01".equals(pos.substring(4, 6)) || "02".equals(pos.substring(4, 6)))	//좌우 구분
            	{
            		LR	= "2";	//R
            	}
            	else
            	{
            		LR	= "1";	//L
            	}

                schCd = pos.substring(0, 2) + "PT" + MATL + LR + "UM";
            }
        }
        else
        {
            if(YfConstant.YD_GP_1.equals(pos.substring(0, 1)))
            {
            	if(YfConstant.ITEM_CM.equals(item))	//소재구분
                {
                	MATL	= "1";	//소재
                }
                else if(YfConstant.ITEM_CG.equals(item))
                {
                	MATL	= "2";	//제품
                }
                else
                {
                	MATL	= "3";	//동간이적
                }

            	if("01".equals(pos.substring(4, 6)) || "02".equals(pos.substring(4, 6)))	//좌우 구분
            	{
            		LR	= "2";	//R
            	}
            	else
            	{
            		LR	= "1";	//L
            	}

                schCd = pos.substring(0, 2) + "PT" + MATL + LR + "UM";
            }
        }

        return schCd;
    }

	/**
	 * 작업예약의 스케쥴 수행 방법을 UPDATE
	 * @param carInfos	차량 도착 정보
	 * @param pos		차량 정지 위치
	 */
	private void editSchMethodOfWBook(List carInfos, String schKind, String pos, String item)
	{
		if(! getUnloadSchKind(item, pos).equals(schKind))
		{
			// 하차스케줄인지 검사. 아니면..
			for(int i = 0; i < carInfos.size(); i++)
			{
				commDao.modifyOperatorOfWBook
		       	(
		       		getSchWorkKind(schKind, pos),
		       		YfConstant.SCH_WORK_LOC_DECISION_METHOD_O,
		       		pos,
		       		getField((JDTORecord)carInfos.get(i), "WBOOK_ID")
		       	);
			}
		}
	}

	/**
     * @param schKind
     * @param pos
     * @return
     */
    private String getSchWorkKind(String item, String pos)
    {
    	String schCd	= "";
    	String LR		= "";
    	String MATL		= "";

    	if(YfConstant.ITEM_SM.equals(item))
        {
    		//return YfConstant.NEW_SCH_WORK_KIND_SVML;
            if(YfConstant.YD_GP_0.equals(pos.substring(0, 1)))
            {
            	MATL	= "1";	//소재

            	if("01".equals(pos.substring(4, 6)) || "02".equals(pos.substring(4, 6)))	//좌우 구분
            	{
            		LR	= "2";	//R
            	}
            	else
            	{
            		LR	= "1";	//L
            	}

                schCd = pos.substring(0, 2) + "PT" + MATL + LR + "UM";
            }
        }
        else
        {
            if(YfConstant.YD_GP_1.equals(pos.substring(0, 1)))
            {
            	if(YfConstant.ITEM_CM.equals(item))	//소재구분
                {
                	MATL	= "1";	//소재
                }
                else if(YfConstant.ITEM_CG.equals(item))
                {
                	MATL	= "2";	//제품
                }
                else
                {
                	MATL	= "3";	//동간이적
                }

            	if("01".equals(pos.substring(4, 6)) || "02".equals(pos.substring(4, 6)))	//좌우 구분
            	{
            		LR	= "1";	//L
            	}
            	else
            	{
            		LR	= "2";	//R
            	}

                schCd = pos.substring(0, 2) + "PT" + MATL + LR + "UM";
            }
        }

        return schCd;
    }

    /**
	 * 차량 도착 후 예약된 작업의 스케쥴을 CALL 한다.
	 * @param stocks		차량 작업 예약 정보
	 * @throws Exception
	 */
	private void callCarArrivalSch(List stocks, int stocksCnt, String yd) throws Exception
	{
		//차후 수정해야함

		if(YfConstant.YD_GP_0.equals(yd))
		{
			callCRSchedule(stocks, "syCraneScheduleInfoInsert", stocksCnt, yd);
		}
		else
		{
			callCRSchedule(stocks, "callCraneSchInfo", stocksCnt, yd);
		}
	}

	/**A열연 SLAB야드 추가
     * 차량 하차 스케쥴을 콜한다.
     * @param stocks		저장품정보
     * @param methodName	메소드이름
     * @param stocksCnt		저장품 수
     */
    private void callCRSchedule(List stocks, String methodName, int stocksCnt, String yd) throws Exception
    {
    	//차후 수정해야함

        EJBConnector ejbConn = new EJBConnector("default", "JNDICraneSchReg", this);

        if(YfConstant.YD_GP_0.equals(yd))
        {
            StringBuffer wbIds = new StringBuffer();

            for(int i = 0; i < stocksCnt; i++)
            {
                wbIds.append(getField((JDTORecord)stocks.get(i), "WBOOK_ID")).append("-");
            }

            ejbConn.trx(methodName, new Class[]{ String.class }, new Object[]{ wbIds.toString() });
            wbIds.setLength(0);
        }
        else
        {
            for(int i = 0; i < stocksCnt; i++)
            {
                ejbConn.trx(methodName, new Class[]{ String.class }, new Object[]{ getField((JDTORecord)stocks.get(i), "WBOOK_ID") });
            }
        }
    }

    /**
     * 차량 출발/도착지시 전문을 편성한다.
     * 1. A열연 차량진입/출발 정보(구전문:THHC190 -> 신전문:YFF1L008)
     * 2. B열연
     *    2.1 차량 도착/출발 정보 COIL  CN1BP06
     *    2.2 차량 도착/출발 정보 SLAB	 CM1BP06
     * @param stocks	저장품 정보
     * @param cardNo	차량CARD번호
     * @param pos		차량 정지위치
     * @param gp		지시구분
     * @param carGp		도착출발구분
     */
    private void sendStartAndArrivalOrder(List stocks, JDTORecord stock, String cardNo, String pos, String carGp)
    {
    	//차후 수정해야함
        String ydGp = pos.substring(0, 1);
        String bay_gp = pos.substring(1, 2);
        int stocksCnt = stocks != null ? stocks.size() : 0;

        String tcCd 	= "YFF1L008";	//YfConstant.TC_THHC190;	//차후 수정해야함
        String schKind 	= getField(stock, "SCH_WORK_KIND");
        StringBuffer sendMsg 	= new StringBuffer();
        JDTORecord cardInfo 	= commDao.readCarNo(ydGp, cardNo);
        if(YfConstant.YD_GP_1.equals(ydGp))
        {
        	//A열연 COIL
            /**
             * THHC190
             * 1	전문코드			CHAR	07
             * 2	작업동			CHAR	01
             * 3	진입위치 SEQ NO	CHAR	01
             * 4	차량구분			CHAR	01		1:반입, 2:출하
             * 5	운송회사 코드		CHAR	05
             * 6	차량번호			CHAR	05
             * 7	작업대상 수량		CHAR	02
             * 8	CARD 번호		CHAR	04
             * 9	코일번호			CHAR	10		8회 반복
             * 10	권상, 권하 위치	CHAR	08
             * 11	SPARE			CHAR	30
             */
            Map tc = commDao.readColumnLenOfTc(tcCd);
            sendMsg.append(tcCd);									//전문코드
            sendMsg.append(pos.substring(1, 2));					//작업동
            appendMsg(sendMsg, "", 									getFieldLen(tc, "진입위치SEQNO"));

            if(YfConstant.NEW_SCH_WORK_KIND_CVRU.equals(schKind))
            {
                carGp = "1";
            }
            else
            {
                carGp = "2";
            }

            appendMsg(sendMsg, carGp, 								getFieldLen(tc, "차량구분"));
            appendMsg(sendMsg, getField(cardInfo, "TRANS_COM_CD"), 	getFieldLen(tc, "운송회사코드"));
            appendMsg(sendMsg, getField(cardInfo, "CAR_NO"), 		getFieldLen(tc, "차량번호"));
            appendMsgNum(sendMsg, ""+ stocksCnt,					getFieldLen(tc, "작업대상수량"));
            appendMsg(sendMsg, cardNo, 								getFieldLen(tc, "CARD번호"));
            sendAMsg(sendMsg, stocks, tc);							//코일번호
            appendMsg(sendMsg, "",									getFieldLen(tc, "SPARE"));
            sendQueue(tcCd, sendMsg.toString());
        }
    }

    /**
	 * [A] 오퍼레이션명 : 소재차량도착Point요구(TSYDJ002) - 기존소스:TsInfoRegSBean.procMatlCarArrPntRequest
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvTSYDJ002(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "소재차량도착Point요구[YfCommCarMvSeEJB.rcvTSYDJ002] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
	    JDTORecordSet rsResult	= null;

	    String szMsg			= "";

	    String szTRN_EQP_GP		= "";	//PT/TR 구분
	    String szYD_MSG_NM		= "";

	    String s_YD_GP			= "";
	    String s_BAY_GP			= "";
	    String s_YD_WBOOK_ID	= "";
	    String s_YD_STK_COL_GP	= "";
	    String s_YD_PNT_CD		= "0000";

	    try
	    {
	    	//기본 수신 항목 값
			String msgId    = commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ002");	//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============소재차량도착Point요구 시작========", "SL");
			commUtils.printParam(logId + "소재차량도착Point요구[TSYDJ002] 수신 ", rcvMsg);

	    	//수신항목 변수 저장
			String szTRN_EQP_CD				= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));			//운송장비코드
			String szWLOC_CD				= commUtils.trim(rcvMsg.getFieldString("WLOC_CD"));				//개소코드
			String szTRN_WRK_FULLVOID_GP	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP"));	//운송작업영공구분[상하차구분 E:공차상태(상차작업),F:영차(하차작업)]

			//운송장비코드길이가 3자리 이상인지 확인 (substring 전 에러 체크)
			if (szTRN_EQP_CD.length() < 3 )
			{
				szMsg = "운송장비코드 오류 [" + szTRN_EQP_CD + "] 운송장비 구분(PT/TR)정보가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");

				return jrRtn ;
			}

	    	szTRN_EQP_GP = "PT";	//szTRN_EQP_CD.substring(1, 3); //PT/TR 구분이 사라지고 PT로 통합됨...

	    	//개소코드가 박판열연 개소코드인지 검사한다.
	    	if(!getABLocationInfo_02(szWLOC_CD))
	    	{
				szMsg = "개소코드 오류 [" + szWLOC_CD + "]는 박판열연 개소코드가 아닙니다!" ;
				commUtils.printLog(logId, szMsg, "SL");

				return jrRtn ;
			}

			jrParam.setField("WLOC_CD",		szWLOC_CD);
			jrParam.setField("CAR_CARD_NO",	szTRN_EQP_CD);
			JDTORecordSet loadPointList = commDao.select(jrParam, getListloadStoppoint2, logId, methodNm, "장비코드로 포인트 재 요구 시 상차예약정지위치 검색");

			if (loadPointList.size() > 0)
			{
				szMsg = "[" + methodNm + "] 장비코드로 포인트 재 요구 시 상차예약정지위치 검색 결과 > 0 ---> 포인트 지시 재송신";
				commUtils.printLog(logId, szMsg, "SL");

				s_YD_STK_COL_GP	= commUtils.trim(loadPointList.getRecord(0).getFieldString("YD_STK_COL_GP"));
				s_YD_PNT_CD		= commUtils.nvl(loadPointList.getRecord(0).getFieldString("YD_PNT_CD"), "0000");
			}
			else
			{
				/**********************************************************
				* 운송작업영공구분이 E(공차) 인 경우 처리
				**********************************************************/
				if ("E".equals(szTRN_WRK_FULLVOID_GP))		// E:공차 --> 상차지 포인트를 찾아 포인트 지시 전송
				{
					szMsg = "[" + methodNm + "] TRN_WRK_FULLVOID_GP 가 'E':공차(상차작업) 포인트 요구 처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");

					/**********************************************************
					* 운송작업영공구분이 "E"(공차) + 개소코드가 "D2Y43" (박판SLAB야드) 인 경우 처리
					**********************************************************/
					if (YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szWLOC_CD))		//개소코드가 A연주-B Cast Slab Yard (D2Y43) BCast의 경우
					{
						//A열연 SLAB 야드인 경우(B Cast)
						szMsg = "[" + methodNm + "] A연주 - B Cast Slab Yard (D2Y43) BCast의 경우 ";
						commUtils.printLog(logId, szMsg, "SL");

						String aimBay_BCast = "";

						//a1)파레트 도착 목적동검색----------------------------
						rsResult = commDao.select(jrParam, getListAimbayBCast, logId, methodNm, "파레트 도착 목적동검색");

						if (rsResult.size() > 0)
						{
							aimBay_BCast = commUtils.trim(rsResult.getRecord(0).getFieldString("AIM_BAY"));

							szMsg = "[" + methodNm + "] BCast 목적동 검색결과 : " + aimBay_BCast;
							commUtils.printLog(logId, szMsg, "SL");
						}

						//a2)상차정지위치 검색-------------------------------
						jrParam.setField("WLOC_CD",		szWLOC_CD);
						jrParam.setField("YD_GP",		YfConstant.YD_GP_0);
						jrParam.setField("BAY_GP",		aimBay_BCast);
						jrParam.setField("TRN_EQP_GP",	szTRN_EQP_GP);
						rsResult = commDao.select(jrParam, getListloadStoppoint_1, logId, methodNm, "상차정지위치 검색");

						if (rsResult.size() > 0)
						{
							s_YD_STK_COL_GP	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));	//적치 열 구분
							s_YD_PNT_CD		= commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_PNT_CD"), "0000");	//야드포인트코드

							szMsg = "[" + methodNm + "] 상차정지위치 검색 결과 : " + s_YD_STK_COL_GP + "," + s_YD_PNT_CD;
							commUtils.printLog(logId, szMsg, "SL");
						}
						else
						{
							//a2-1)모든 포인트가 점유중이면 상차완료된 포인트를 찾음...2020.01.09 상하차완료 되었어도 포인트 내려주지 않음
//							jrParam.setField("YD_GP",	YfConstant.YD_GP_0);
//							jrParam.setField("BAY_GP",	aimBay_BCast);
//							jrParam.setField("SECT_GP",	szTRN_EQP_GP);
//							rsResult = commDao.select(jrParam, getListloadEndpoint, logId, methodNm, "상차완료된 포인트 검색");
//
//							if (rsResult.size() > 0)
//							{
//								s_YD_STK_COL_GP	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
//								s_YD_PNT_CD		= commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_PNT_CD"),"0000");
//
//								szMsg = "[" + methodNm + "] 상차완료된 포인트 검색 결과 : " + s_YD_STK_COL_GP + "," + s_YD_PNT_CD;
//								commUtils.printLog(logId, szMsg, "SL");
//							}
//							else
//							{
								//a2-1-1)포인트 없으면 포인트 없음으로 지시주고 하위처리 안함------------------------
								szMsg = "[" + methodNm + "] 상차정지위치 및 상차완료위치 찾지 못함 ";
								commUtils.printLog(logId, szMsg, "SL");

								//대기장 포인트 사유 가져오기 (재료외형구분N:바인코일 BIC생산(소형압연)~제품창고 입고 이전(종합판정)
								szYD_MSG_NM = this.getCarMsg("N", YfConstant.YD_GP_0, aimBay_BCast, szTRN_EQP_GP, szWLOC_CD, "", logId, methodNm);

								//0000 포인트지시 전송
								jrRtn = commUtils.addSndData(jrRtn, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, "0000", szYD_MSG_NM, logId));

								commUtils.printLog(logId, methodNm, "S-");
								return jrRtn;
//							}
						}
					}
					/**********************************************************
					* 운송작업영공구분이 "E"(공차) + 개소코드가 "D2Y44", "D2Y45" (박판COIL야드) 인 경우 처리
					**********************************************************/
					else if(YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szWLOC_CD) || YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szWLOC_CD))	//개소코드가 D2Y44 : A열연-#1 제품/소재 Coil Yard || D2Y45 : A열연-#2 제품/소재 Coil Yard
					{
						//A열연 Coil 야드인 경우
						szMsg = "[" + methodNm + "] A열연 COIL야드 인 경우 (WLOC_CD: " + szWLOC_CD + ")";
						commUtils.printLog(logId, szMsg, "SL");

			            //b1)공차의 경우 상차작업예약 ID를 이용하여 목적동을 알아온다------------------
						jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, getListStoppointE, logId, methodNm, "공차의 경우 상차작업예약 ID를 이용하여 목적동을 알아온다");

						if (rsResult.size() > 0)
						{
							s_YD_GP			= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
							s_BAY_GP		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_BAY_GP"));
							s_YD_WBOOK_ID	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));

							szMsg = "[" + methodNm + "] 검색 결과  >> YD_GP: " + s_YD_GP + ", BAY_GP: " + s_BAY_GP + ", YD_WBOOK_ID: " + s_YD_WBOOK_ID;
							commUtils.printLog(logId, szMsg, "SL");
						}
						else
						{
							//A열연 COIL 야드인 경우 대상재 조회
							jrParam.setField("WLOC_CD",		szWLOC_CD);
							jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
							rsResult = commDao.select(jrParam, getListFrtoStl_CoilNewA, logId, methodNm, "A열연 COIL 야드인 경우 대상재 조회");

							if (rsResult.size() > 0)
							{
								s_YD_GP		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
								s_BAY_GP	= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));
							}
						}

						//b2)상차정지위치 검색------------------------------------------------
						jrParam.setField("WLOC_CD",		szWLOC_CD);
						jrParam.setField("YD_GP",		s_YD_GP);
						jrParam.setField("BAY_GP",		s_BAY_GP);
						jrParam.setField("TRN_EQP_GP",	szTRN_EQP_GP);

						rsResult = commDao.select(jrParam, getListloadStoppoint, logId, methodNm, "상차정지위치 검색 - 검색대상재 동에 있는 차량위치정보");

						if (rsResult.size() > 0)
						{
							s_YD_STK_COL_GP	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));	//적치 열 구분
							s_YD_PNT_CD		= commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_PNT_CD"),"0000");	//야드포인트코드

							szMsg = "[" + methodNm + "] 상차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 검색 결과 : " + s_YD_STK_COL_GP + "," + s_YD_PNT_CD;
							commUtils.printLog(logId, szMsg, "SL");
						}
						else
						{
							//b2-1)포인트 없으면 포인트 없음으로 지시주고 하위처리 안함------------------------
							szMsg = "[" + methodNm + "] 상차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 찾지 못함 ";
							commUtils.printLog(logId, szMsg, "SL");

							//대기장 포인트 사유 가져오기 (재료외형구분N:바인코일 BIC생산(소형압연)~제품창고 입고 이전(종합판정)
							szYD_MSG_NM = this.getCarMsg("N", s_YD_GP, s_BAY_GP, szTRN_EQP_GP, szWLOC_CD, "", logId, methodNm);

							//0000 포인트지시 전송
							jrRtn = commUtils.addSndData(jrRtn, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, "0000", szYD_MSG_NM, logId));

							commUtils.printLog(logId, methodNm, "S-");
							return jrRtn;
						}
					}

	    		    //포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다.
					jrParam.setField("YD_STK_STAT",		"L");	//L:상차작업상태
					jrParam.setField("CAR_CARD_NO",		szTRN_EQP_CD);
					jrParam.setField("YD_STK_COL_GP",	s_YD_STK_COL_GP);
					commDao.update(jrParam, updateEquipcolStat, logId, methodNm, "TB_YF_STKCOL 예약정보등록");

					//차량포인트통합관리(1구분, 2 CAR_NO, 3 장비번호 OR CARD_NO, 4 저장위치, 5 개소코드, 6 포인트, 7 상태)
					this.YfCarPointinforeg("3", "", szTRN_EQP_CD, s_YD_STK_COL_GP, "", "", "R", logId, methodNm);

					//1-2-3.차량스케쥴 상차출발(1)로 UPDATE
					szMsg = "차량스케쥴 상차출발(1)로 UPDATE   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					jrParam.setField("YD_CAR_PROG_STAT",		"1");				//차량진행상태 (1:상차출발)
					jrParam.setField("YD_CAR_USE_GP",			"L");				//야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT",			"U");				//야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("SPOS_WLOC_CD",			szWLOC_CD);			//발지개소코드(상차지)
					jrParam.setField("YD_PNT_CD",				s_YD_PNT_CD);		//야드상차포인트코드(발지)
					jrParam.setField("YD_CARLD_WRK_BOOK_ID",	"");				//야드상차작업예약ID
					jrParam.setField("YD_CARLD_STOP_LOC",		s_YD_STK_COL_GP);	//야드하차정지위치
					jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드

					commDao.update(jrParam, updCarSchLdByTrnEqpCd, logId, methodNm, "TB_YD_CARSCH 상차출발(1)로 UPDATE ");
				}
				/**********************************************************
				* 운송작업영공구분이 "F"(영차) 인 경우 처리
				**********************************************************/
				else if ("F".equals(szTRN_WRK_FULLVOID_GP))		// F:영차 --> 하차지 포인트를 찾아 포인트 지시 전송
				{
					szMsg = "[" + methodNm + "] TRN_WRK_FULLVOID_GP 가 'F':영차(하차작업) 포인트 요구  처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");

					/**********************************************************
					* 운송작업영공구분이 "F"(영차) + 개소코드가 "D2Y44", "D2Y45" (박판COIL야드) 인 경우 처리
					**********************************************************/
					if(YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szWLOC_CD) || YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szWLOC_CD))	//개소코드가  D2Y44 : A열연-#1 제품/소재 Coil Yard || D2Y45 : A열연-#2 제품/소재 Coil Yard
					{
			            //c1)영차의 경우 하차작업예약 ID를 이용하여 목적동을 알아온다------------------
						jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, getListAimBay_ACoil2, logId, methodNm, "영차의 경우 운송설비코드를 이용하여 목적동을 알아온다");

						if (rsResult.size() > 0)
						{
							s_BAY_GP 		= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));

							szMsg = "[" + methodNm + "] 검색 결과  >> BAY_GP: " + s_BAY_GP;
							commUtils.printLog(logId, szMsg, "SL");
						}

						//c2)하차정지위치 검색------------------------------------------------
						jrParam.setField("WLOC_CD",		szWLOC_CD);
						jrParam.setField("BAY_GP",		s_BAY_GP);
						rsResult = commDao.select(jrParam, getListloadStoppointCM, logId, methodNm, "코일 소재 하차 정지위치 검색");

						if (rsResult.size() > 0)
						{
							s_YD_STK_COL_GP	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));	//적치 열 구분
							s_YD_PNT_CD		= commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_PNT_CD"), "0000");	//야드포인트코드

							szMsg="["+methodNm+"] 하차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 검색 결과 : " + s_YD_STK_COL_GP + "," + s_YD_PNT_CD;
							commUtils.printLog(logId, szMsg, "SL");
						}
						else
						{
							//c2-1)포인트 없으면 포인트 없음으로 지시주고 하위처리 안함------------------------
							szMsg="["+methodNm+"] 하차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 찾지 못함 ";
							commUtils.printLog(logId, szMsg, "SL");

							//대기장 포인트 사유 가져오기 (재료외형구분N:바인코일 BIC생산(소형압연)~제품창고 입고 이전(종합판정)
							szYD_MSG_NM = this.getCarMsg("N", s_YD_GP, s_BAY_GP, szTRN_EQP_GP, szWLOC_CD, "", logId, methodNm);

							//0000 포인트지시 전송
							jrRtn = commUtils.addSndData(jrRtn, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, "0000", szYD_MSG_NM, logId));

							commUtils.printLog(logId, methodNm, "S-");
							return jrRtn;
						}
					}
					/**********************************************************
					* 운송작업영공구분이 "F"(영차) + 개소코드가 "D2Y43" (박판SLAB야드) 인 경우 처리
					**********************************************************/
					else if(YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szWLOC_CD))	//개소코드가 A연주-B Cast Slab Yard (D2Y43) BCast의 경우
					{
						JDTORecordSet rsResult2    	= null;
					    String inCarspec;
					    String outCarspec;

						//야드구분 지정
					    s_YD_GP 	= "0";  //b-cast

						//운송장비코드로 차량스케줄ID 조회
						jrParam.setField("TRN_EQP_CD",		szTRN_EQP_CD);
						rsResult = commDao.select(jrParam,	getListFrtostlList_1, logId, methodNm, "운송장비코드로 차량스케줄ID 조회");

						String s_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));

						if ("".equals(s_CAR_SCH_ID) || s_CAR_SCH_ID == null)
						{
							throw new Exception("차량스케줄ID(YD_CAR_SCH_ID) 조회 실패!!!");
						}

						//차량스케줄 ID로 차량스케줄에서 SPOS_WLOC_CD 조회
						jrParam.setField("YD_CAR_SCH_ID",	s_CAR_SCH_ID);
						rsResult = commDao.select(jrParam, getYdCarSchBySchId, logId, methodNm, "차량스케줄id로 차량스케줄정보 조회");

						if (rsResult.size() <= 0)
						{
							throw new Exception("발지개소코드(SPOS_WLOC_CD) 조회 실패!!!");
						}

						String szSPOS_WLOC_CD = commUtils.trim(rsResult.getRecord(0).getFieldString("SPOS_WLOC_CD"));

						//이송목적동 조회
						if(YfConstant.WLOC_CD_B_HR_SLAB_YARD.equals(szSPOS_WLOC_CD) || YfConstant.WLOC_CD_B_HR_REFUR_SLAB_YARD.equals(szSPOS_WLOC_CD))
						{
							//1열연(B열연 SLAB)에서 박판 슬라브로 올때 목적동 검색
							jrParam.setField("WLOC_CD",	szWLOC_CD);
							jrParam.setField("YD_GP",	YfConstant.YD_GP_0);
							rsResult = commDao.select(jrParam, getListloadStoppointBCAST, logId, methodNm, "B-CAST(D2Y43) 하차정지위치 검색 ");

							if (rsResult.size() > 0)
							{
								s_YD_STK_COL_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));

								s_BAY_GP	= s_YD_STK_COL_GP.substring(1, 2);	//비어있는 동
							}
							else
							{
								s_BAY_GP	= "A";
							}
						}
						else
						{
							s_BAY_GP	= this.getSlabAimCd(s_CAR_SCH_ID, szSPOS_WLOC_CD, logId , methodNm);
						}

						if ("".equals(s_BAY_GP))
						{
							throw new Exception("이송목적동 조회 실패!!!");
						}

						szMsg=" 결과 >> 야드구분 : " + s_YD_GP +", 차량스케줄ID : " + s_CAR_SCH_ID + ", 이송목적동 : " + s_BAY_GP;
						commUtils.printLog(logId, szMsg, "SL");

						/**********************************************************
						* 1-1-1. 하차정지위치 검색
						**********************************************************/
						szMsg="하차정지위치 검색  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						//B-CAST(D2Y43)
						jrParam.setField("WLOC_CD",	szWLOC_CD);
						jrParam.setField("YD_GP",	s_YD_GP);
						rsResult = commDao.select(jrParam, getListloadStoppointBCAST, logId, methodNm, "B-CAST(D2Y43) 하차정지위치 검색 ");

						if (rsResult.size() > 0)
						{
							s_YD_STK_COL_GP	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));	//적치 열 구분
							s_YD_PNT_CD		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));		//야드포인트코드
						}
						else
						{
							if("PT".equals(szTRN_EQP_GP))
							{
								//포인트 모두 점유상태일때 하차완료된 포인트찾음...2020.01.09 상하차완료 되었어도 포인트 내려주지 않음
//								jrParam.setField("YD_GP",	s_YD_GP);
//								jrParam.setField("BAY_GP",	s_BAY_GP);
//								jrParam.setField("WLOC_CD",	szWLOC_CD);
//								rsResult = commDao.select(jrParam, getListUnloadEndpoint_slab, logId, methodNm, "포인트 모두 점유상태일때 하차완료된 포인트찾음");
//
//								if (rsResult.size() > 0)
//								{
//									jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
//									rsResult2 = commDao.select(jrParam, getListCarSpec, logId, methodNm, "YD_차량사양 테이블에서 운송장비코드로 CAR_NO 조회");
//
//									inCarspec = commUtils.trim(rsResult2.getRecord(0).getFieldString("CAR_NO"));
//
//									for(int ii = 0 ; ii < rsResult.size() ; ii++)
//									{
//										outCarspec = commUtils.trim(rsResult.getRecord(ii).getFieldString("CAR_NO"));
//
//										if (inCarspec.equals(outCarspec))
//										{
//											s_YD_STK_COL_GP	= commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_STK_COL_GP"));	//적치 열 구분
//											s_YD_PNT_CD		= commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_PNT_CD"));		//야드포인트코드
//										}
//									}
//								}
							}
						}

						if (rsResult.size() <= 0 || "".equals(s_YD_PNT_CD))
						{
							//포인트찾지 못함
							s_YD_STK_COL_GP	= "XXPTXX";
							s_YD_PNT_CD		= "0000";

							//c2-1)포인트 없으면 포인트 없음으로 지시주고 하위처리 안함------------------------
							szMsg="["+methodNm+"] 하차정지위치 검색 - 검색대상재 동에 있는 차량위치정보 찾지 못함 ";
							commUtils.printLog(logId, szMsg, "SL");

							//대기장 포인트 사유 가져오기 (재료외형구분N:바인코일 BIC생산(소형압연)~제품창고 입고 이전(종합판정)
							szYD_MSG_NM = this.getCarMsg("N", s_YD_GP, s_BAY_GP, szTRN_EQP_GP, szWLOC_CD, "", logId, methodNm);

							//0000 포인트지시 전송
							jrRtn = commUtils.addSndData(jrRtn, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, "0000", szYD_MSG_NM, logId));

							commUtils.printLog(logId, methodNm, "S-");
							return jrRtn;
						}
					}

	    		    // 포인트정보를 가져온 후 해당위치정보에 예약정보를 등록한다.
					jrParam.setField("YD_STK_STAT",		"U");	//U:하차작업상태
					jrParam.setField("CAR_CARD_NO",		szTRN_EQP_CD);
					jrParam.setField("YD_STK_COL_GP",	s_YD_STK_COL_GP);
					commDao.update(jrParam, updateEquipcolStat, logId, methodNm, "TB_YF_STKCOL 예약정보등록");

					//차량포인트통합관리(1구분, 2 CAR_NO, 3 장비번호 OR CARD_NO, 4 저장위치, 5 개소코드, 6 포인트, 7 상태)
					this.YfCarPointinforeg("3", "", szTRN_EQP_CD, s_YD_STK_COL_GP, "", "", "R", logId, methodNm);

					//**********************************************************************************
					//1-1-3.차량스케쥴 하차출발(A)로 UPDATE
					szMsg="차량스케쥴 하차출발(A)로 UPDATE   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					jrParam.setField("YD_CAR_PROG_STAT",		"A");				//차량진행상태 (A:하차출발)
					jrParam.setField("YD_CAR_USE_GP",			"L");				//야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT",			"L");				//야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("ARR_WLOC_CD",				szWLOC_CD);			//착지개소코드
					jrParam.setField("YD_PNT_CD",				s_YD_PNT_CD);		//야드포인트코드(착지) --> **YD_PNT_CD3 에 저장된다
					jrParam.setField("YD_CARUD_STOP_LOC",		s_YD_STK_COL_GP);	//야드하차정지위치
					jrParam.setField("YD_CARUD_WRK_BOOK_ID",	"");				//야드하차작업예약ID
					jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드

					commDao.update(jrParam, updCarSchUdByTrnEqpCd, logId, methodNm, "TB_YD_CARSCH 하차출발(A) 업데이트 ");
				}
			}

			//포인트지시 전송
			jrRtn = commUtils.addSndData(jrRtn, this.makeYDTSJ011(szTRN_EQP_CD, szWLOC_CD, s_YD_PNT_CD, szYD_MSG_NM, logId));

			commUtils.printLog(logId, "=============소재차량도착Point요구 종료========", "SL");

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
	 * [A] 오퍼레이션명 : AB열연 개소코드 인지 체크
	 * @param sWlocCd
	 * @return boolean (true: AB열연 개소코드)
	 */
	private boolean getABLocationInfo_03(String sWlocCd)
	{
		if
		(
			//박판열연 개소코드
			YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(sWlocCd)||	//D2Y43 : A연주-B Cast Slab Yard
			YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(sWlocCd)	||	//D2Y44 : A열연-#1 제품/소재 Coil Yard
			YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(sWlocCd)		//D2Y45 : A열연-#2 제품/소재 Coil Yard

			||

			//B열연 개소코드
			YfConstant.WLOC_CD_B_HR_NO1_COIL_YARD.equals(sWlocCd)	||	//D3Y41 : B열연-#1 제품/소재 Coil Yard
			YfConstant.WLOC_CD_B_HR_NO2_COIL_YARD.equals(sWlocCd)	||	//D3Y42 : B열연-#2 제품/소재 Coil Yard
			YfConstant.WLOC_CD_B_HR_SLAB_YARD.equals(sWlocCd)		||	//D3Y43 : B열연-Slab Yard
			YfConstant.WLOC_CD_B_HR_REFUR_SLAB_YARD.equals(sWlocCd)		//D3Y44 : B열연-가열로 Slab Yard
		)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * [A] 오퍼레이션명 : 박판열연 개소코드 인지 체크
	 * @param sWlocCd
	 * @return boolean (true: 박판열연 개소코드)
	 */
	private boolean getABLocationInfo_02(String sWlocCd)
	{
		if
		(
			//박판열연 개소코드
			YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(sWlocCd)||	//D2Y43 : A연주-B Cast Slab Yard
			YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(sWlocCd)	||	//D2Y44 : A열연-#1 제품/소재 Coil Yard
			YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(sWlocCd)		//D2Y45 : A열연-#2 제품/소재 Coil Yard
		)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * [A] 오퍼레이션명 : 야드 발지,착지개소코드로 이송방향 체크
	 * @param sWlocCd, sARR_WLOC_CD, sARR_WLOC_CD, logId
	 * @return String ("AA","AC","CA","CC")
	 */
	private String getABLocationInfo_01(String sSPOS_WLOC_CD, String sARR_WLOC_CD, String logId)
	{
		String szMsg = "<<<< getABLocationInfo_01 결과 : ";
		String sWorkGp;

		if (this.getABLocationInfo_03(sSPOS_WLOC_CD))		//발지개소코드가 AB열연
		{
			if (this.getABLocationInfo_03(sARR_WLOC_CD))	//착지개소가 AB열연이면
			{
				sWorkGp	= "AA";
				szMsg += sWorkGp + " AB열연에서 AB열연으로 이송 >>>>";
			}
			else											//착지개소가 AB열연이 아니면
			{
				sWorkGp	= "AC";
				szMsg += sWorkGp + " AB열연에서 일관제철로 이송 >>>>";
			}
		}
		else												//발지개소코드가 AB열연이 아니면
		{
			if (this.getABLocationInfo_03(sARR_WLOC_CD))	//착지개소가 AB열연이면
			{
				sWorkGp	= "CA";
				szMsg += sWorkGp + " 일관제철에서 AB열연으로 이송 >>>>";
			}
			else											//착지개소가 AB열연이 아니면
			{
				sWorkGp	= "CC";
				szMsg += sWorkGp + " 일관제철에서 일관제철로 이송 >>>>";
			}
		}

		commUtils.printLog(logId, szMsg, "SL");
		return sWorkGp;
	}

	/**
	 * 오퍼레이션명 : 대기장 도착 MSG 생성
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String getCarMsg(String s_STL_APPEAR_GP , String s_STACK_YD_GP, String  s_STACK_BAY_GP, String  szTRN_EQP_GP, String szARR_WLOC_CD, String szYD_POINT_CD, String logId, String methodNm)
	{
	    JDTORecordSet	rsResult	= null;
	    JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();//Query 실행시 파라메터 전달용 JDTORecord

    	try
    	{
    		//개소코드 체크
			if ("".equals(szARR_WLOC_CD))
			{
				return "입력된 개소코드가 존재 안함.";
			}

			//코일야드 만 해당됨
			if
			(
				"D2Y44".equals(szARR_WLOC_CD) || "D2Y45".equals(szARR_WLOC_CD)||
				"D3Y41".equals(szARR_WLOC_CD) || "D3Y42".equals(szARR_WLOC_CD)||
				"DJY21".equals(szARR_WLOC_CD) || "DJY22".equals(szARR_WLOC_CD)||
				"DJY1E".equals(szARR_WLOC_CD)
			)
			{
				//-------------------------------------코일야드-------------------------------------------
	    		if ("Y".equals(s_STL_APPEAR_GP))
	    		{
	    			//******************************제품 ***************************************
	    			//포인트코드 체크
	    			if ("".equals(szYD_POINT_CD))
	    			{
	    				return "입력된 포인트코드가 존재 안함.";
	    			}

	    			//개소코드 체크
					jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
					jrParam.setField("YD_PNT_CD",	szYD_POINT_CD);
					rsResult = commDao.select(jrParam, getListUnloadEndpointGoods_chk, logId, methodNm, "getCarMsg - COIL제품 - 개소코드체크");

					if (rsResult.size() <= 0)
					{
		    			return s_STACK_BAY_GP+"동  지시개소코드가 야드와 틀림.";
					}

		    		//포인트 체크
					jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
					jrParam.setField("YD_PNT_CD",	szYD_POINT_CD);
					rsResult = commDao.select(jrParam, getListUnloadEndpointGoods_chk2, logId, methodNm, "getCarMsg - COIL제품 - 포인트체크");

					if (rsResult.size() > 0)
					{
		    			return s_STACK_BAY_GP+"동 개소지의 야드포인트가 사용불가.";
					}

		    		//다른차량 체크
					jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
					jrParam.setField("YD_PNT_CD",	szYD_POINT_CD);
					rsResult = commDao.select(jrParam, getListUnloadEndpointGoods_chk4, logId, methodNm, "getCarMsg - COIL제품 - 다른차량체크");

					if (rsResult.size() > 0)
					{
		    			return s_STACK_BAY_GP+"동 해당개소에 다른 차량 점유.";
					}
	    		}
	    		else
	    		{
	    			//******************************소재 ***************************************
		    		//목적동 체크
					if ("".equals(s_STACK_BAY_GP))
					{
						return "목적동OR이송대상이 존재 안함.";
					}

					//야드 체크
					if ("".equals(s_STACK_YD_GP))
					{
						return "입력된 야드코드가 존재 안함.";
					}

					//TR/PT 체크
					if ("".equals(szTRN_EQP_GP))
					{
						return "입력된 장비구분(TR/PT)가 존재 안함.";
					}

					//개소코드 체크
					jrParam.setField("YD_GP",	s_STACK_YD_GP);
					jrParam.setField("BAY_GP",	s_STACK_BAY_GP);
					jrParam.setField("SECT_GP",	szTRN_EQP_GP);
					jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
					rsResult = commDao.select(jrParam, getListUnloadEndpoint_chk, logId, methodNm, "getCarMsg - COIL소재 - 개소코드체크");

					if (rsResult.size() <= 0)
					{
		    			return s_STACK_BAY_GP+"동  지시개소코드가 야드와 틀림.";
					}

		    		//포인트 체크
					jrParam.setField("YD_GP",	s_STACK_YD_GP);
					jrParam.setField("BAY_GP",	s_STACK_BAY_GP);
					jrParam.setField("SECT_GP",	szTRN_EQP_GP);
					jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
					rsResult = commDao.select(jrParam, getListUnloadEndpoint_chk2, logId, methodNm, "getCarMsg - COIL소재 - 포인트체크");

					if (rsResult.size() > 0)
					{
		    			return s_STACK_BAY_GP+"동 개소지의 야드포인트가 사용불가.";
					}

		    		//다른차량 체크
					jrParam.setField("YD_GP",	s_STACK_YD_GP);
					jrParam.setField("BAY_GP",	s_STACK_BAY_GP);
					jrParam.setField("SECT_GP",	szTRN_EQP_GP);
					jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
					rsResult = commDao.select(jrParam, getListUnloadEndpoint_chk3, logId, methodNm, "getCarMsg - COIL소재 - 다른차량 체크");

					if (rsResult.size() > 0)
					{
		    			return s_STACK_BAY_GP+"동 해당개소에 다른 차량 점유.";
					}
	    		}
	    		//-------------------------------------코일야드-------------------------------------------
			}
			else
			{
				//-------------------------------------슬라브야드-------------------------------------------
				return "";
				//-------------------------------------슬라브야드-------------------------------------------
			}
			//-------------------------------------------------------------------------

			return "시스템 담당자 확인 요망.";
    	}
    	catch (DAOException daoe)
    	{
	        throw daoe;
	    }
    	catch (Exception e)
	    {
	        throw new EJBServiceException(e);
	    }
	}

	/**
	 * [A] 오퍼레이션명 : 포인트지시(YDTSJ011) 전문 생성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord makeYDTSJ011(String szTRN_EQP_CD, String szWLOC_CD, String szYD_PNT_CD, String szYD_MSG_NM, String logId) throws DAOException
	{
		String methodNm = "포인트지시(YDTSJ011)전문 생성[YfCommCarMvSeEJB.makeYDTSJ011]";

		JDTORecord jrTemp	= null;

	    try
	    {
			commUtils.printLog(logId, methodNm, "S+");

			//포인트지시 메세지 전송
			jrTemp = JDTORecordFactory.getInstance().create();

			jrTemp.setResultCode(logId);	//Log ID
			jrTemp.setResultMsg(methodNm);	//Log Method Name
			jrTemp.setField("JMS_TC_CD",			"YDTSJ011");
			jrTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
			jrTemp.setField("TRN_EQP_CD",			szTRN_EQP_CD);
			jrTemp.setField("WLOC_CD",				szWLOC_CD);
			jrTemp.setField("YD_PNT_CD",			commUtils.nvl(szYD_PNT_CD, "0000"));
			jrTemp.setField("PNT_WO_GP",			"A");
			jrTemp.setField("PNT_WO_DT",			commUtils.getDateTime14());
			jrTemp.setField("YD_MSG_NM",			szYD_MSG_NM);
			jrTemp.setField("TRN_WRK_MTL_GP",		"");	//운송작업재료구분 (C:COIL제품,H:열연COIL소재,S:SLAB,L:냉연COIL소재) -- ??반드시 값을 전송해야 하는지? 한다면 어떻게 알 수 있는지?

			commUtils.printLog(logId, methodNm, "S-");

			return jrTemp;
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
	 * [A] 오퍼레이션명 : 소재차량도착(TSYDJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ003(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "소재차량도착[YfCommCarMvSeEJB.rcvTSYDJ003] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============소재차량도착 시작========", "SL");

			if (msgId==null || "".equals(msgId))
	        {
	        	return jrRtn;
	        }

	    	//수신항목 변수 저장
			String szARR_WLOC_CD 	= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"));		//착지개소코드

			if
			(
				YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	||	//착지개소코드(D2Y44) : A열연-#1 제품/소재 Coil Yard
				YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)		//착지개소코드(D2Y45) : A열연-#2 제품/소재 Coil Yard
			)
			{
				//A열연 Coil 야드인 경우
				jrRtn = this.rcvTSYDJ003_ACoil(rcvMsg);
			}
			else if(YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD))	//착지개소코드(D2Y43) : A연주-B Cast Slab Yard
			{
				//A열연 Slab 야드인 경우
				jrRtn = this.rcvTSYDJ003_ASlab(rcvMsg);
			}

			commUtils.printLog(logId, "=============소재차량도착 종료========", "SL");

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
	 * [A] 오퍼레이션명 : A열연 COIL야드 소재차량도착(TSYDJ003) 수신처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ003_ACoil(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "소재차량도착수신처리_A열연Coil[YfCommCarMvSeEJB.rcvTSYDJ003_ACoil] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

	    String		szTRN_EQP_CD;
	    String		szARR_WLOC_CD;
	    String		szARR_YD_PNT_CD;
	    String		szTRN_WRK_FULLVOID_GP;
	    String		szTRN_EQP_STK_CAPA;
	    String		szTRN_EQP_GP;
	    String		svTRN_EQP_GP;
	    String		s_YD_EQP_WRK_STAT	= "";

	    String		sStkClo;
	    String		s_STACK_YD_GP;
	    String		s_STACK_BAY_GP;
	    String		s_YD_STK_LYR_NO;
		String		s_STL_APPEAR_GP;
		String		s_YD_CAR_SCH_ID		= "";
		String		sSchCode			= "";
		String		wbook_ID;
		String		first_wbook_ID		= "";
		String		sYD_SCH_PRIOR;
		String		sFRTOMOVE_WORD_NO;	//이송작업지시 번호
		String		ydFrmYn		= "";	//차량형상사용유무
		String		dummyYn		= "";	//더미작업여부

		int			iLoadMax 	= 0;
    	int			iLoadCur 	= 0;

    	long		lCarMaxWt	= 0;
    	long		lPerWt		= 0;
    	long		lTotalWt	= 0;

		List		lWrkbookIdList01	= new ArrayList(); //1단 작업예약ID List
		List		lWrkbookIdList02	= new ArrayList(); //2단 작업예약ID List

		String		szMsg		= "";

		JDTORecord	jrTemp		= null; //임시  JDTORecord

		EJBConnector ejbConn;
		JDTORecordSet rsResult	= null;
	    JDTORecordSet rsResult2	= null;

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ003");	//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============소재차량도착수신처리_A열연Coil 시작========", "SL");

	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= "PT";	//szTRN_EQP_CD.substring(1, 3);							//PT/TR 구분이 사라지고 PT로 통합됨...
			svTRN_EQP_GP 			= szTRN_EQP_CD.substring(1, 3);									//PT/TR 구분

			if ("E".equals(szTRN_WRK_FULLVOID_GP))		//E:공차
			{
				s_YD_EQP_WRK_STAT = "U";	//야드설비작업상태(U) : 공차
			}
			else if ("F".equals(szTRN_WRK_FULLVOID_GP))	//F:영차
			{
				s_YD_EQP_WRK_STAT = "L";	//야드설비작업상태(L) : 영차
			}

	    	//개소코드가 박판열연 개소코드인지 검사한다.
	    	if (!getABLocationInfo_02(szARR_WLOC_CD))
	    	{
				throw new Exception("개소코드 오류 [" + szARR_WLOC_CD + "]는 박판열연 개소코드가 아닙니다!");
			}

	    	//도착 포인트코드가 대기장(1Z99)인 경우 도착처리 안함
	    	if ("1Z99".equals(szARR_YD_PNT_CD))
	    	{
				throw new Exception("도착포인트코드가 [1Z99]대기장으로 도착처리 안함.");
	    	}

	    	//차량포인트(TB_YD_CARPOINT)를 개소코드와 야드포인트로 조회하여 도착처리 운송장비 코드와 동일한지 체크한다.
			jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
			jrParam.setField("YD_PNT_CD",	szARR_YD_PNT_CD);
			rsResult = commDao.select(jrParam, getCarPointChk, logId, methodNm, "차량포인트 체크  ");

			if (rsResult.size() <= 0)
			{
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 차량포인트에 없는 위치입니다.");
			}
			else
			{
				//R:예약, L:사용중(도착불가), N:사용금지, C:비었음(도착가능)
				if ("R".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))
				{
					//예약일 경우 입력받은 운송장비코드와 동일한지 체크
					if (!szTRN_EQP_CD.equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD")))
					{
						throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 로 예약되어 있는데 " + szTRN_EQP_CD + " 로 도착처리 수신되었습니다.");
					}
				}
				else if ("L".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))
				{
					//상용중일경우 에러 처리
					throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  이미 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 가 입동되어 있는 위치입니다.");
				}
			}

			//차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear
			jrParam.setField("CAR_CARD_NO",	szTRN_EQP_CD);	//운송장비코드
			commDao.update(jrParam, updStackStatByTrnEqpCd, logId, methodNm, "TB_YF_STKCOL 차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear ");

			//차량 포인트 예약으로 잡혀있는정보 Clear
			jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);	//운송장비코드
			commDao.update(jrParam, updPlnInfoReSet, logId, methodNm, "TB_YD_CARPOINT 차량 포인트 예약으로 잡혀있는정보 Clear ");

			//차량저장위치 포인트에 입동유무 체크
			//착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인
			jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
			jrParam.setField("YD_PNT_CD",	szARR_YD_PNT_CD);
			jrParam.setField("TRN_EQP_GP",	szTRN_EQP_GP);
			rsResult = commDao.select(jrParam, stackcolpointchk, logId, methodNm, "TB_YF_STKCOL 착지개소코드와 착지야드포인트로 해당 적치열이 입동전 상태인지 확인  ");

			if (rsResult.size() <= 0)
			{
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 로 적치열 조회 결과 이미 입동되어 있는 위치입니다.");
			}

			//차량저장위치 점유
			jrParam.setField("YD_CAR_USE_GP",	"L"); //L:구내운송차량 , G:출하차량  	--야드차량사용구분
			jrParam.setField("TRN_EQP_CD",		szTRN_EQP_CD);
			jrParam.setField("CAR_NO",			"");
			jrParam.setField("CARD_NO",			"");
			jrParam.setField("WLOC_CD",			szARR_WLOC_CD);
			jrParam.setField("YD_PNT_CD",		szARR_YD_PNT_CD);
			commDao.update(jrParam, updateLayerstat_01, logId, methodNm, "TB_YF_STKCOL 차량저장위치 점유 ");

			//차량포인트통합관리 (1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			this.YfCarPointinforeg("4", "", szTRN_EQP_CD, "", szARR_WLOC_CD, szARR_YD_PNT_CD, "L", logId, methodNm);

			//운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기
			jrParam.setField("TRN_EQP_CD",		szTRN_EQP_CD);
			rsResult = commDao.select(jrParam, getListstlQty, logId, methodNm, "운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기  ");
			String szQTY = commUtils.trim(rsResult.getRecord(0).getFieldString("QTY"));

			//해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다.
			jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"E"); //적치가능
			jrParam.setField("WLOC_CD",					szARR_WLOC_CD);
			jrParam.setField("YD_PNT_CD",				szARR_YD_PNT_CD);
			jrParam.setField("TRN_EQP_GP",				szTRN_EQP_GP);

			if ("0".equals(szQTY))
			{
				jrParam.setField("QTY",		"6");
			}
			else
			{
				jrParam.setField("QTY",		szQTY);
			}

			commDao.update(jrParam, updateLayerstat_Qty, logId, methodNm, "TB_YF_STKLYR 해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다 ");

			//실제 도착위치 업데이트 및 영차도착시 맵정보 생성을 위해
			//개소코드와 야드포인트 코드로 적치열구분 조회 야드구분, 동 정보를 구한다.
			jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
			jrParam.setField("YD_PNT_CD",	szARR_YD_PNT_CD);
			jrParam.setField("TRN_EQP_GP",	szTRN_EQP_GP);
			rsResult = commDao.select(jrParam, getListStkColgp, logId, methodNm, "TB_YF_STKCOL 개소코드와 야드포인트 코드로 적치열구분 조회");

			if (rsResult.size() <= 0)
			{
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 로 적치열 조회 결과 : " + rsResult.size());
			}

			sStkClo			= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
			s_STACK_YD_GP	= sStkClo.substring(0, 1);
			s_STACK_BAY_GP  = sStkClo.substring(1, 2);

			if ("F".equals(szTRN_WRK_FULLVOID_GP))	//TRN_WRK_FULLVOID_GP(운송작업영공구분) F:영차 / E:공차
			{
				//1.운송작업영공구분이 F:영차 인 경우 처리
				szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				//이송재료 가 STOCK에 존재하지 않으면 STOCK을 생성한다.
				jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
				rsResult = commDao.select(jrParam, getIsExistStock, logId, methodNm, "TB_YD_CARFTMVMTL 이송재료 가 TB_YF_STOCK에 존재하는지 check");

				if (rsResult.size() > 0)
				{
					String sCoilNo = "";
					String sStockMv = "";

					for(int ii= 0; ii < rsResult.size(); ii++)
					{
						if ("".equals(commUtils.trim(rsResult.getRecord(ii).getFieldString("YF_STL_NO"))))
						{
							sCoilNo = commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO"));

							//코일공통의 차공정코드로 이동조건 설정, 리턴 값이 "" 이면 코일공통에 존재하지 않는 코일 번호로 Error 처리 한다.
							sStockMv = yfComm.getStockMv(logId, methodNm, sCoilNo);

							//TB_YF_STOCK에 존재 한지 않으면 생성한다.
							jrParam.setField("STL_NO",			sCoilNo);
							jrParam.setField("STOCK_ITEM",		YfConstant.ITEM_CM);
							jrParam.setField("STOCK_MOVE_TERM",	sStockMv);
							commDao.insert(jrParam, insStock, logId, methodNm, "TB_YF_STOCK 재료 생성");
						}
					}
				}

				//차량스케쥴ID로 이송재료 조회
				jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
				rsResult = commDao.select(jrParam, getListFrtostlListsangcha, logId, methodNm, "TB_YD_CARFTMVMTL 차량스케쥴ID로 이송재료 조회");

				if (rsResult.size() <= 0)
				{
					throw new Exception("영차(F) 도착처리 대상재가 존재 안함");
				}
				else
				{
					s_STL_APPEAR_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				}

				//스케줄코드 생성
	    		if("Y".equals(s_STL_APPEAR_GP))
	    		{
	    			sSchCode = getUnloadSchKind(YfConstant.ITEM_CG, sStkClo);	//제품
	    		}
	    		else
	    		{
	    			sSchCode = getUnloadSchKind(YfConstant.ITEM_CM, sStkClo);	//소재
	    		}

				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD",	sSchCode);
				rsResult2 = commDao.select(jrParam, getYdSchrule, logId, methodNm, "TB_YF_SCHRULE 스케줄 기준 조회");

				if(rsResult2 != null && rsResult2.size() > 0)
				{
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
				}
				else
				{
					throw new Exception("A열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}

				//이송 작업지시 번호 가져오기
				sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo");

				//하차대상 갯수 만큼 Looping...
				for(int ii= 0; ii < rsResult.size(); ii++)
				{
					//작업예약ID생성
					wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");

					if ("".equals(first_wbook_ID))
					{
						first_wbook_ID = wbook_ID; //첫번째 작업예약 ID
					}

					//단정보 가져오기
					s_YD_STK_LYR_NO = commUtils.nvl(rsResult.getRecord(ii).getFieldString("STK_LYR"),"01");

					//단정보에 따라 1단 또는 2단 작업예약ID 리스트에 추가
					if ("01".equals(s_YD_STK_LYR_NO))
					{
						lWrkbookIdList01.add(wbook_ID);
					}
					else
					{
						lWrkbookIdList02.add(wbook_ID);
					}

					/**********************************************************
					* 1-1. 작업예약(TB_YF_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID",			wbook_ID);
					jrParam.setField("YD_GP",				s_STACK_YD_GP);
					jrParam.setField("YD_BAY_GP",			s_STACK_BAY_GP);
					jrParam.setField("YD_SCH_CD",			sSchCode); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR",		sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT",	"W"); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP",		"A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_SCH_REQ_GP",		"C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
					jrParam.setField("TRN_EQP_CD",			szTRN_EQP_CD); //운송장비코드
					jrParam.setField("YD_CAR_USE_GP",		"L"); //야드차량사용구분 L:구내운송
					commDao.insert(jrParam, insWrkBook2, logId, methodNm, "작업예약(TB_YF_WRKBOOK) 생성");

					/**********************************************************
					* 1-. 작업예약재료(TB_YF_WRKBOOKMTL) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID",			wbook_ID);
					jrParam.setField("STL_NO",				commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("YD_STK_COL_GP",		sStkClo);
					jrParam.setField("YD_STK_BED_NO",		commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_STK_BED_NO")));
					jrParam.setField("YD_STK_LYR_NO",		commUtils.trim(rsResult.getRecord(ii).getFieldString("STK_LYR")));
					commDao.insert(jrParam, insYfWrkBookMtl, logId, methodNm, "작업예약재료(TB_YF_WRKBOOKMTL) 생성");

					/**********************************************************
					* 1-3. TB_YF_STOCK의이송작업지시번호(FRTOMOVE_WORD_NO) 등록
					**********************************************************/
					jrParam.setField("STL_NO",				commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("STOCK_MOVE_TERM",		YfConstant.NEW_STOCK_MOVE_TERM_CS ); //'CS'이송대기
					jrParam.setField("FRTOMOVE_WORD_NO",	sFRTOMOVE_WORD_NO); //이송작업지시번호
					commDao.update(jrParam, updStockTransWordNo, logId, methodNm, "TB_YF_STOCK의 이송작업지시번호(FRTOMOVE_WORD_NO), STOCK_MOVE_TERM(CS) 등록");

					/**********************************************************
					* 1-4. 영차도착 포인트 적치단(TB_YF_STKLYR)에 COIL정보 생성하기
					**********************************************************/
					jrParam.setField("STL_NO",				commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("YD_STK_COL_GP",		sStkClo);
					jrParam.setField("YD_STK_BED_NO",		commUtils.format(ii+1, 2)); //Integer.toString(ii+1));
					jrParam.setField("YD_STK_LYR_NO",		"01");
					commDao.insert(jrParam, setStackLayer, logId, methodNm, "TB_YF_STKLYR 영차도착 포인트 적치단에 COIL정보 생성하기");
				}

				/**********************************************************
				* 1-5. TB_YD_CARSCH 차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE, YD_CAR_PROG_STAT : B 하차도착)
				**********************************************************/
				jrParam.setField("YD_CARUD_STOP_LOC",		sStkClo);
				jrParam.setField("YD_PNT_CD3",				szARR_YD_PNT_CD);
				jrParam.setField("YD_CARUD_WRK_BOOK_ID",	first_wbook_ID);
				jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);
				jrParam.setField("FRTOMOVE_WORD_NO",		sFRTOMOVE_WORD_NO); //이송작업지시번호
				commDao.update(jrParam, updateArrDt5, logId, methodNm, "TB_YD_CARSCH 차량스케쥴(YD_CAR_PROG_STAT('B':하차도착), 도착시간, 하차 정지위치정보 UPDATE), 이송작업지시번호(FRTOMOVE_WORD_NO)");
			}
			else if ("E".equals(szTRN_WRK_FULLVOID_GP))
			{
				//2.운송작업영공구분이 E:공차 인 경우 처리
				szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				//상차대상재 조회 (순위 감안)
				jrParam.setField("TRN_EQP_CD",		szTRN_EQP_CD);
				jrParam.setField("YD_STK_COL_GP",	s_STACK_YD_GP + s_STACK_BAY_GP);
				jrParam.setField("WLOC_CD",			szARR_WLOC_CD);
				rsResult = commDao.select(jrParam, getListFrtoStl_CoilNewsangcha, logId, methodNm, "상차대상재 조회 (순위 감안)");

				if (rsResult.size() <= 0)
				{
					throw new Exception("공차(E) 도착처리 대상재가 존재 안함");
				}
				else
				{
					s_STL_APPEAR_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				}

				//스케줄코드 생성
		    	if("Y".equals(s_STL_APPEAR_GP))
		    	{
		    		sSchCode	= getSchWorkKind(YfConstant.ITEM_CG, sStkClo);	//제품
		    	}
		    	else
		    	{
		    		sSchCode	= getSchWorkKind(YfConstant.ITEM_CM, sStkClo);	//소재
		    	}

				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", sSchCode);
				rsResult2 = commDao.select(jrParam, getYdSchrule, logId, methodNm, "스케줄 기준 조회");

				if(rsResult2 != null && rsResult2.size() > 0)
				{
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
				}
				else
				{
					throw new Exception("A열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}

				//차량장비 MAX 중량정보 설정
				if ("TR".equals(svTRN_EQP_GP))
				{
	    			try
	    			{
	    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA.trim());
	    			}
	    			catch (Exception e)
	    			{
	    				lCarMaxWt = 60000;
	    			}

	    			iLoadMax = 4;
				}
				else
				{
	    			try
	    			{
	    				lCarMaxWt = Long.parseLong(szTRN_EQP_STK_CAPA.trim());
	    			}
	    			catch (Exception e)
	    			{
	    				lCarMaxWt = 180000;
	    			}

		    		iLoadMax = 6;
				}

				List lCarStockList	= new ArrayList();

				//상차Lot편성 (상차대상재정보를 가지고 Lot를 편성한다.)
				for(int index=0; index < rsResult.size() ; index++)
				{
					//같이 편성할 수 있는 Lot편성 대상재이면 차량Max중량과 비교해서 Over하는지 체크
					lPerWt = Long.parseLong(commUtils.trim(rsResult.getRecord(index).getFieldString("STK_WT")));

					//소재 상차LOT대상재 편성(차량 중량체크 )
					if (lCarMaxWt >= lTotalWt + lPerWt)
					{
						lCarStockList.add(rsResult.getRecord(index));
						lTotalWt += lPerWt;
						iLoadCur++;

		    			if (iLoadMax <= iLoadCur)
		    			{
		    				break;
		    			}
					}
					else
					{
						break;
					}
				}

				szMsg= "구내운송차량 : " + szTRN_EQP_CD + " (상차대상중량 : " + lTotalWt + ")";
				commUtils.printLog(logId, szMsg, "SL");

				//보안관리팀에 따른 트레일러 4매이상 상차 불가 처리
		    	if (iLoadCur >= 4 && "TR".equals(svTRN_EQP_GP))
		    	{
		    		iLoadCur = 4 ;
		    	}

				JDTORecord	FrtoProduct3	= null;
				String		s_ARR_WLOC_CD	= "";

				//이송 작업지시 번호 가져오기
				sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo");

				/*******************************************
				 * 상차도에 남아있는 작업예약 삭제
				 *******************************************/
				jrParam.setField("YD_STK_COL_GP", sStkClo);
				JDTORecordSet jsDelList = commDao.select(jrParam, getExistWrkList, logId, methodNm, "TB_YF_WRKBOOK 상차도 작업예약 조회");

				for(int ii = 0; ii < jsDelList.size(); ++ii)
				{
					jrParam.setField("YD_WBOOK_ID", jsDelList.getRecord(ii).getFieldString("YD_WBOOK_ID"));

					commDao.update(jrParam, updDelYnWrkBookMtl, logId, methodNm, "TB_YF_WRKBOOKMTL");	//작업예약재료 삭제

					commDao.update(jrParam, updDelYnWrkBook, logId, methodNm, "TB_YF_WRKBOOK");			//작업예약 삭제
				}

				//상차대상 갯수 만큼 Looping...
				for(int ii = 0; ii < iLoadCur; ii++)
				{
					FrtoProduct3 = (JDTORecord)lCarStockList.get(ii);

					//작업예약ID생성
					wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");

					if ("".equals(first_wbook_ID))
					{
						first_wbook_ID = wbook_ID; //첫번째 작업예약 ID
					}

					//단정보 가져오기
					s_YD_STK_LYR_NO = commUtils.nvl(FrtoProduct3.getFieldString("YD_STK_LYR_NO"), "01");

					//단정보에 따라 1단 또는 2단 작업예약ID 리스트에 추가
					if ("01".equals(s_YD_STK_LYR_NO))
					{
						lWrkbookIdList01.add(wbook_ID);
					}
					else
					{
						lWrkbookIdList02.add(wbook_ID);
					}

					/**********************************************************
					* 2-1. 작업예약(TB_YF_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID",			wbook_ID);
					jrParam.setField("YD_GP",				s_STACK_YD_GP);
					jrParam.setField("YD_BAY_GP",			s_STACK_BAY_GP);
					jrParam.setField("YD_SCH_CD",			sSchCode);		//야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR",		sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT",	"W");			//야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP",		"A");			//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_SCH_REQ_GP",		"F");			//야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
					jrParam.setField("TRN_EQP_CD",			szTRN_EQP_CD);	//운송장비코드
					jrParam.setField("YD_CAR_USE_GP",		"L");			//야드차량사용구분 L:구내운송 G:출하차량
					commDao.insert(jrParam, insWrkBook2, logId, methodNm, "TB_YF_WRKBOOK 작업예약 생성");

					/**********************************************************
					* 2-2. 작업예약재료(TB_YF_WRKBOOKMTL) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID",			wbook_ID);
					jrParam.setField("STL_NO",				commUtils.trim(FrtoProduct3.getFieldString("STL_NO")));
					jrParam.setField("YD_STK_COL_GP",		sStkClo);
					jrParam.setField("YD_STK_BED_NO",		commUtils.trim(FrtoProduct3.getFieldString("YD_STK_BED_NO")));
					jrParam.setField("YD_STK_LYR_NO",		commUtils.trim(FrtoProduct3.getFieldString("YD_STK_LYR_NO")));
					commDao.insert(jrParam, insYfWrkBookMtl, logId, methodNm, "TF_YF_WRKBOOKMTL 작업예약재료 생성");

					/**********************************************************
					* 2-3. TB_YF_STOCK의 이송작업지시번호(FRTOMOVE_WORD_NO) 등록
					**********************************************************/
					jrParam.setField("STL_NO",				commUtils.trim(FrtoProduct3.getFieldString("STL_NO")));
					jrParam.setField("STOCK_MOVE_TERM",		YfConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
					jrParam.setField("FRTOMOVE_WORD_NO",	sFRTOMOVE_WORD_NO); //이송작업지시번호
					commDao.update(jrParam, updStockTransWordNo, logId, methodNm, "TB_YF_STOCK 의 이송작업지시번호(FRTOMOVE_WORD_NO), STOCK_MOVE_TERM(CS:이송대기) 등록");

					s_ARR_WLOC_CD = commUtils.trim(FrtoProduct3.getFieldString("ARR_WLOC_CD"));
				}

				/**********************************************************
				* 2-4. TB_YD_CARSCH update(도착시간, 실제도착위치 업데이트처리, YD_CAR_PROG_STAT : 2 상차도착)
				**********************************************************/
				jrParam.setField("YD_CARLD_WRK_BOOK_ID",	first_wbook_ID);
				jrParam.setField("ARR_WLOC_CD",				s_ARR_WLOC_CD);
				jrParam.setField("YD_PNT_CD1",				szARR_YD_PNT_CD);
				jrParam.setField("YD_CARLD_STOP_LOC",		sStkClo);
				jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);
				jrParam.setField("FRTOMOVE_WORD_NO",		sFRTOMOVE_WORD_NO); //이송작업지시번호
				commDao.update(jrParam, updateArrDt4, logId, methodNm, "TB_YD_CARSCH 차량스케쥴(도착시간, 하차 정지위치정보 UPDATE)");
			}

			/**********************************************************
			* 저장위치제원정보 송신 (YFF1L001) -- 차량도착
			**********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);		//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",		"3");				//야드정보동기화코드
			sndL2Msg.setField("YD_STK_COL_GP",			sStkClo);
			sndL2Msg.setField("YD_STK_BED_NO",			"01");
			sndL2Msg.setField("YD_CAR_ARRSTRT_STAT",	"A");				//A:도착, S:출발
			sndL2Msg.setField("YD_CAR_USE_GP",			"L");				//L:구내운송, G:출하차량
			sndL2Msg.setField("YD_EQP_WRK_STAT",		s_YD_EQP_WRK_STAT);	//U:공차, L:영차
			sndL2Msg.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드
			sndL2Msg.setField("YD_CAR_AIM_YD_GP",		YfConstant.YD_GP_1);
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001_CarInfo", sndL2Msg));

			if ("F".equals(szTRN_WRK_FULLVOID_GP))
			{
				//운송작업영공구분이 F:영차 인 경우 처리
				/**********************************************************
				* 저장품제원(YFF1L002) 전문 생성
				**********************************************************/
				jrParam.setField("YD_INFO_SYNC_CD",		"3");					//야드정보동기화코드 (열)
				jrParam.setField("MSG_GP",				"I");					//전문구분
				jrParam.setField("YD_STK_COL_GP",		sStkClo);				//야드적치열구분
				jrParam.setField("YD_STK_BED_NO",		"");					//야드적치Bed번호
				jrParam.setField("YD_GP",				YfConstant.YD_GP_1);	//야드구분
				jrParam.setField("STL_NO",				"");					//재료번호
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", jrParam));
			}

			/**********************************************************
			* Crane스케줄 호출
			*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
			*  - 차량형상 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
			*  - 더미작업여부가 Y일 경우 차량형상 시스템이 있더라도 스케줄 기동
			**********************************************************/
			jrParam.setField("YD_STK_COL_GP",	sStkClo);
			JDTORecordSet rsResult3 = commDao.select(jrParam, getCarPntFrmYn,  logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
			JDTORecordSet rsResult4	= commDao.select(jrParam, getCarPntFrmYn3, logId, methodNm, "상차 주작업대상재의 더미작업여부 확인 ");
			
			commUtils.printLog(logId, "차량형상 시스템 사용 여부 : " + rsResult3.getRecord(0).getFieldString("YD_FRM_YN"), "SL");
			commUtils.printLog(logId, "더미작업여부 : " + rsResult4.getRecord(0).getFieldString("DUMMY_YN"), "SL");
			
			ydFrmYn = rsResult3.getRecord(0).getFieldString("YD_FRM_YN");
			dummyYn	= rsResult4.getRecord(0).getFieldString("DUMMY_YN");

			/**********************************************************
			* 차량작업 예정정보 송신 (YFF1L008)
			**********************************************************/
			//2020.02.18 정종균과장요청 형상유무 사용인 경우에만 차량예정정보 송신
			//if("Y".equals(ydFrmYn))
			//{
				//차량예정정보 송신
				jrParam.setField("SEARCH_FLAG",		"2");				//1:상차도, 2:차량스케쥴 ID
				jrParam.setField("YD_CAR_SCH_ID",	s_YD_CAR_SCH_ID); 	//야드차량스케쥴ID
				jrRtn = commUtils.addSndData(jrRtn,	yfComm.procCarPlanInfo(jrParam));	//YFF1L008 생성
			//}

			//차량형상사용 'N' 또는 더미작업유무 'Y'인 경우 크레인 스케줄 기동
			if("N".equals(ydFrmYn) || "Y".equals(dummyYn))
			{
				//차량형상 사용안하면 크레인 스케줄 기동
				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();

				//크레인 스케줄 기동 YFYFJ303 호출
				jrCrnSchMsg.setField("JMS_TC_CD",			"YFYFJ303");
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
				jrCrnSchMsg.setField("YD_SCH_CD",			sSchCode); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID",			""); //야드설비ID

				int pcnt = 0;

				commUtils.printLog(logId, "[lWrkbookIdList02.size()] 결과 건수: " + lWrkbookIdList02.size() , "LOG");
				commUtils.printLog(logId, "[lWrkbookIdList01.size()] 결과 건수: " + lWrkbookIdList01.size() , "LOG");

				//2단 적치된 대상 호출
				for(int ii = 0; ii < lWrkbookIdList02.size(); ii++)
				{
					jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt), (String)lWrkbookIdList02.get(ii)); //야드작업예약ID
				}

				//1단 적치된 대상 호출
				for(int ii = 0; ii < lWrkbookIdList01.size(); ii++)
				{
					jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt), (String)lWrkbookIdList01.get(ii)); //야드작업예약ID
				}

				jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));

				jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
			}

			commUtils.printLog(logId, "=============소재차량도착수신처리_A열연Coil 종료========", "SL");

			commUtils.printLog(logId, methodNm, "SL");

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
	 * [A] 오퍼레이션명 : A열연 SLAB야드 소재차량도착(TSYDJ003) 수신처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ003_ASlab(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "소재차량도착수신처리_A열연Slab[YfCommCarMvSeEJB.rcvTSYDJ003_ASlab] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

	    String		szTRN_EQP_CD;
	    String		szARR_WLOC_CD;
	    String		szARR_YD_PNT_CD;
	    String		szTRN_WRK_FULLVOID_GP;
	    String		szTRN_EQP_STK_CAPA;
	    String		szTRN_EQP_GP;

	    String		sStkClo;
	    String		s_STACK_YD_GP;
	    String		s_STACK_BAY_GP;
		String		s_YD_CAR_SCH_ID		= "";
		String		sSchCode;
		String		wbook_ID			= "";
		String		first_wbook_ID		= "";
		String		sYD_SCH_PRIOR;
		String		sFRTOMOVE_WORD_NO	= "";	//이송작업지시 번호
		String		sSCH_AUTO_RUN_MODE	= "M";
		String		sYD_WRK_CRN			= "";
		String		sYD_MULTI_WRK_YN	= "";	//야드멀티작업여부
		String		sYD_EQP_WRK_STAT	= "";
		String		sCURR_PROG_CD 		= "";
		String		sWO_MSLAB_RPR_MTD	= "";
		String		sSTOCK_MOVE_TERM	= "";

    	int			iWbook_ID_Cnt		= 0;

    	String		szMsg				= "";

	    JDTORecordSet rsResult    		= null;
	    JDTORecordSet rsResult2    		= null;

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ003");	//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============소재차량도착수신처리_A열연Slab 시작========", "SL");

	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분	F:영차 / E:공차
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= "PT";	//szTRN_EQP_CD.substring(1, 3); //PT/TR 구분이 사라지고 PT로 통합됨...

	    	//B-CAST 트레일러 이면서 공차인경우 운송설비구분을  PT 로  변경
			if ("TR".equals(szTRN_EQP_GP) && YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD) && "E".equals(szTRN_WRK_FULLVOID_GP))
			{
	    		szTRN_EQP_GP = "PT";
	    	}

	    	//개소코드가 AB열연 개소코드인지 검사한다.
	    	if (!getABLocationInfo_02(szARR_WLOC_CD))
	    	{
				throw new Exception("개소코드 오류 [" + szARR_WLOC_CD + "]는 A열연 개소코드가 아닙니다!");
			}

	    	//도착 포인트코드가 대기장(1Z99)인 경우 도착처리 안함
	    	if ("1Z99".equals(szARR_YD_PNT_CD))
	    	{
	    		//2020.05.15 대기장 도착시 오류를 내지 않고 종료 하도록 수정
				//throw new Exception("도착포인트코드가 [1Z99]대기장으로 도착처리 안함.");
	    		commUtils.printLog(logId, "***운송장비코드 : " + szTRN_EQP_CD + "(" + szTRN_WRK_FULLVOID_GP + ") / 도착포인트코드가 [1Z99]대기장으로 도착처리 안함***", "SL");
	    		
	    		return jrRtn;
	    	}

	    	//차량포인트(TB_YD_CARPOINT)를 개소코드와 야드포인트로 조회하여 도착처리 운송장비 코드와 동일한지 체크한다
			jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
			jrParam.setField("YD_PNT_CD",	szARR_YD_PNT_CD);
			rsResult = commDao.select(jrParam, getCarPointChk, logId, methodNm, "차량포인트 체크");

			if (rsResult.size() <= 0)
			{
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 차량포인트에 없는 위치입니다.");
			}
			else
			{
				//R:예약, L:사용중(도착불가), N:사용금지, C:비었음(도착가능)
				if ("R".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))
				{
//					//예약일 경우 입력받은 운송장비코드와 동일한지 체크
//					if (!szTRN_EQP_CD.equals(rsResult.getRecord(0).getFieldString("TRN_EQP_CD")))
//					{
//						throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 로 예약되어 있는데 " + szTRN_EQP_CD + " 로 도착처리 수신되었습니다.");
//					}
				}
				else if ("L".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))
				{
					//사용중일경우 에러 처리
					throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  이미 " + rsResult.getRecord(0).getFieldString("TRN_EQP_CD") + " 가 입동되어 있는 위치입니다.");
				}
				else if ("N".equals(rsResult.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT")))
				{
					//사용금지일경우 에러 처리
					throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 는  사용금지 상태입니다.");
				}
			}

			/**********************************************************
			* TB_YF_STKCOL 차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear
			**********************************************************/
			jrParam.setField("CAR_CARD_NO",	szTRN_EQP_CD);	//운송장비코드
			commDao.update(jrParam, updStackStatByTrnEqpCd, logId, methodNm, "차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear ");

			/**********************************************************
			* TB_YD_CARPOINT 차량 포인트 예약으로 잡혀있는정보 Clear
			**********************************************************/
			jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);	//운송장비코드
			commDao.update(jrParam, updPlnInfoReSet, logId, methodNm, "차량 포인트 예약으로 잡혀있는정보 Clear ");

			/**********************************************************
			* TB_YF_STKCOL 차량저장위치 점유
			**********************************************************/
			jrParam.setField("YD_CAR_USE_GP",	"L"); //L:구내운송차량 , G:출하차량  	--야드차량사용구분
			jrParam.setField("TRN_EQP_CD",		szTRN_EQP_CD);
			jrParam.setField("CAR_NO",			"");
			jrParam.setField("CARD_NO",			"");
			jrParam.setField("WLOC_CD",			szARR_WLOC_CD);
			jrParam.setField("YD_PNT_CD",		szARR_YD_PNT_CD);
			commDao.update(jrParam, updateLayerstat_01, logId, methodNm, "차량저장위치 점유 ");

			/**********************************************************
			* TB_YD_CARPOINT 4 : 개소코드,포인트로 차량 포인트 예약
			**********************************************************/
			//차량포인트통합관리 (1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			this.YfCarPointinforeg("4", "", szTRN_EQP_CD, "", szARR_WLOC_CD, szARR_YD_PNT_CD, "L", logId, methodNm);

			//운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발로 예약되어 있는 재료 수 구하기
			jrParam.setField("TRN_EQP_CD",		szTRN_EQP_CD);
			rsResult = commDao.select(jrParam, getListstlQty, logId, methodNm, "운송장비코드로 TB_YD_CARFTMVMTL(차량이송재료)에 하차출발(A)로 예약되어 있는 재료 수 구하기  ");

			String szQTY = commUtils.trim(rsResult.getRecord(0).getFieldString("QTY"));

			//해당 야드포인트이 적치단에서 QTY 이하 단의  활성상태를 변경한다.
			jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"E"); //적치가능
			jrParam.setField("WLOC_CD",					szARR_WLOC_CD);
			jrParam.setField("YD_PNT_CD",				szARR_YD_PNT_CD);

			if("0".equals(szQTY))
			{
				jrParam.setField("QTY",					"6");
			}
			else
			{
				jrParam.setField("QTY",					szQTY);
			}

			commDao.update(jrParam, updateLayerstat_Qty, logId, methodNm, "해당 야드포인트의 TB_YF_STKLYR(적치단)에서 QTY 이하 단의  활성상태를 변경한다 ");

			//실제 도착위치 업데이트 및 영차도착시 맵정보 생성을 위해
			//개소코드와 야드포인트 코드로 적치열구분 조회 야드구분, 동 정보를 구한다.
			jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
			jrParam.setField("YD_PNT_CD",	szARR_YD_PNT_CD);
			jrParam.setField("TRN_EQP_GP",	szTRN_EQP_GP);
			rsResult = commDao.select(jrParam, getListStkColgp, logId, methodNm, "개소코드와 야드포인트 코드로 적치열구분 조회");

			if (rsResult.size() <= 0)
			{
				throw new Exception("착지개소코드 [" + szARR_WLOC_CD + "], 착지야드포인트코드 [" + szARR_YD_PNT_CD + "] 로 적치열 조회 결과 : " + rsResult.size());
			}

			sStkClo			= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
			s_STACK_YD_GP	= sStkClo.substring(0, 1);
			s_STACK_BAY_GP  = sStkClo.substring(1, 2);

			if ("F".equals(szTRN_WRK_FULLVOID_GP))
			{
				//1.운송작업영공구분이 F:영차 인 경우 처리
				//**********************************************************************************
				szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				sYD_EQP_WRK_STAT = "L"; //L : 영차 , U: 공차

				//운송장비코드로 이송재료 조회 + 작업예약ID
				jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
				rsResult = commDao.select(jrParam, getListFrtostlList, logId, methodNm, "운송장비코드로 이송재료 조회");

				if (rsResult.size() <= 0)
				{
					throw new Exception("영차(F) 도착처리 대상재가 존재 안함");
				}
				else
				{
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				}

				/**********************************************************
				* 영차도착 포인트 적치단(TB_YF_STKLYR) CLEAR 하기
				**********************************************************/
			    jrParam.setField("YD_STK_COL_GP",		sStkClo);
			    jrParam.setField("YD_STK_BED_NO",		"01");
			    jrParam.setField("YD_STK_LYR_STAT1",	"%");
			    jrParam.setField("YD_STK_LYR_STAT2",	"E");
			    jrParam.setField("STL_NO",				"");
			    commDao.update(jrParam, updLyrByBedNo, logId, methodNm, "적치단 정보 Clear!");

				//하차대상 갯수 만큼 Looping...
				for(int ii= 0; ii < rsResult.size() ; ii++)
				{
					/**
					 * CSYDJ001(슬라브 연주전단실적)전문 대신 A열연 박판슬라브는 구내운송에서 소재차량 도착(F:영차)시 TB_YF_STOCK에 STL_NO를 등록한다.
					 */
					jrParam.setField("SLAB_NO",			commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					rsResult2 = commDao.select(jrParam, getInitSlabInfo, logId, methodNm, "소재차량도착(영차)시 SLAB공통정보 조회");

					if(rsResult2.size() > 0)
					{
						sCURR_PROG_CD 		= commUtils.trim(rsResult2.getRecord(0).getFieldString("CURR_PROG_CD"));		//현재진도코드
						sWO_MSLAB_RPR_MTD	= commUtils.trim(rsResult2.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD"));	//Scarfing Pattern

						sSTOCK_MOVE_TERM	= yfComm.getStockMoveTerm(sCURR_PROG_CD, sWO_MSLAB_RPR_MTD);
					}

					jrParam.setField("STL_NO",			commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("MODIFIER",		modifier);
					jrParam.setField("STOCK_ITEM",		"SM");		//SLAB소재
					jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
					commDao.update(jrParam, mergeStockInfo, logId, methodNm, "영차도착시 (TB_YF_STOCK)에 SLAB정보 등록수정하기");

					/**********************************************************
					* 영차도착 포인트 적치단(TB_YF_STKLYR)에 SLAB정보 생성하기
					**********************************************************/
					jrParam.setField("STL_NO",			commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
					jrParam.setField("YD_STK_COL_GP",	sStkClo);
					jrParam.setField("YD_STK_BED_NO",	"01");
					jrParam.setField("YD_STK_LYR_NO",	commUtils.format(ii+1, 2));
					commDao.update(jrParam, setStackLayer, logId, methodNm, "영차도착 포인트 적치단(TB_YF_STKLYR)에 SLAB정보 생성하기");
				}

//				//스케줄코드 생성  - 이송하차(L)...박판SLAB에서 스케쥴+워크북 생성안함
//				sSchCode = this.getUnloadSchKind(YfConstant.ITEM_SM, sStkClo);
//
//				//스케줄코드로 스케줄기준Table조회...박판SLAB에서 스케쥴+워크북 생성안함
//				jrParam.setField("YD_SCH_CD", sSchCode);
//				rsResult2 = commDao.select(jrParam, getYdSchrule, logId, methodNm, "스케줄 기준 조회");
//
//				if(rsResult2 != null && rsResult2.size() > 0)
//				{
//					sYD_WRK_CRN			= rsResult2.getRecord(0).getFieldString("YD_WRK_CRN");			//야드작업크레인
//					sYD_SCH_PRIOR		= rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR");	//야드스케쥴우선순위
//					sYD_MULTI_WRK_YN	= rsResult2.getRecord(0).getFieldString("YD_MULTI_WRK_YN");		//야드멀티작업여부
//				}
//				else
//				{
//					throw new Exception("A열연 SLAB 스케쥴 코드 이상 : [" + sSchCode + "]");
//				}

				//작업예약 슬라브 단위로 생성
				String sYD_FRM_YN = "N";

				/**********************************************************
				* 도착동 차량형상 시스템 사용여부 확인
				*  - TB_YF_RULE 테이블의 YM2006 기준으로 사용 여부 확인
				**********************************************************/
				jrParam.setField("REPR_CD_GP",	"YM2006");
				jrParam.setField("CD_GP",		s_STACK_YD_GP);
				jrParam.setField("ITEM",		s_STACK_BAY_GP);
				rsResult2 = commDao.select(jrParam, getYfRule, logId, methodNm, "야드 기준 조회");

				if (rsResult2.size() > 0)
				{
					sYD_FRM_YN = commUtils.nvl(rsResult2.getRecord(0).getFieldString("DTL_ITEM1"),"N");
				}

				commUtils.printLog(logId, "=======:::: YM2006 " + s_STACK_BAY_GP + "동 차량형상인식 사용여부: " +  sYD_FRM_YN , "SL");

				if ("N".equals(sYD_FRM_YN))
				{
					//이송 작업지시 번호 가져오기
					sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo");

//					if ("Y".equals(sYD_MULTI_WRK_YN))
//					{
//						jrParam.setField("YD_SCH_ST_GP",	"N"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
//
//						if("0".equals(s_STACK_YD_GP))
//						{
//							if ("A".equals(s_STACK_BAY_GP))
//							{
//								if ("0ACRA1".equals(sYD_WRK_CRN))
//								{
//									jrParam.setField("YD_WRK_PLAN_CRN",		"0ACRA1");	//야드작업계획크레인
//									jrParam.setField("YD_WRK_PLAN_CRN2",	"0ACRA1");	//야드작업계획크레인2
//								}
//							}
//							else if ("B".equals(s_STACK_BAY_GP))
//							{
//								if ("0BCRB1".equals(sYD_WRK_CRN))
//								{
//									jrParam.setField("YD_WRK_PLAN_CRN",		"0BCRB1");	//야드작업계획크레인
//									jrParam.setField("YD_WRK_PLAN_CRN2",	"0BCRB1");	//야드작업계획크레인2
//								}
//							}
//							else
//							{
//								jrParam.setField("YD_WRK_PLAN_CRN",		sYD_WRK_CRN);	//야드작업계획크레인
//								jrParam.setField("YD_WRK_PLAN_CRN2",	""); 			//야드작업계획크레인2
//							}
//						}
//					}
//					else
//					{
//						jrParam.setField("YD_SCH_ST_GP",		"A");			//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
//						jrParam.setField("YD_WRK_PLAN_CRN",		sYD_WRK_CRN);	//야드작업계획크레인
//						jrParam.setField("YD_WRK_PLAN_CRN2",	"");			//야드작업계획크레인2
//					}

					iWbook_ID_Cnt = 0;
					JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();

					//하차대상 갯수 만큼 Looping...
					for(int ii=(rsResult.size()-1) ; ii >= 0 ; ii--)
					{
						//작업예약ID생성
//						wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
//
//						iWbook_ID_Cnt++;
//
//						jrCrnSchMsg.setField("YD_WBOOK_ID"+(iWbook_ID_Cnt),wbook_ID);
//						jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(iWbook_ID_Cnt));
//
//						/**********************************************************
//						* 1-1. 작업예약(TB_YF_WRKBOOK) 생성
//						**********************************************************/
//						jrParam.setField("YD_WBOOK_ID",			wbook_ID);
//						jrParam.setField("YD_GP",				s_STACK_YD_GP);
//						jrParam.setField("YD_BAY_GP",			s_STACK_BAY_GP);
//						jrParam.setField("YD_SCH_CD",			sSchCode);		//야드스케쥴코드
//						jrParam.setField("YD_SCH_PRIOR",		sYD_SCH_PRIOR);	//야드스케쥴우선순위
//						jrParam.setField("YD_SCH_PROG_STAT",	"W");			//야드스케쥴진행상태(W:스케줄수행대기)
//						jrParam.setField("YD_SCH_REQ_GP",		"C");			//야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
//						jrParam.setField("TRN_EQP_CD",			szTRN_EQP_CD);	//운송장비코드
//						jrParam.setField("YD_CAR_USE_GP",		"L");			//야드차량사용구분 L:구내운송
//						commDao.insert(jrParam, insWrkBookSlab, logId, methodNm, "작업예약(TB_YF_WRKBOOK) 생성");
//
//						/**********************************************************
//						* 작업예약재료(TB_YF_WRKBOOKMTL) 생성
//						**********************************************************/
//						jrParam.setField("YD_WBOOK_ID",			wbook_ID);
//						jrParam.setField("STL_NO",				commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
//						jrParam.setField("YD_STK_COL_GP",		sStkClo);
//						jrParam.setField("YD_STK_BED_NO",		"01");
//						jrParam.setField("YD_STK_LYR_NO",		commUtils.format(ii+1, 2));
//						commDao.insert(jrParam, insYfWrkBookMtlSlab, logId, methodNm, "작업예약재료(TB_YF_WRKBOOKMTL) 생성");

						/**********************************************************
						* TB_YF_STOCK 의 이송작업지시번호(FRTOMOVE_WORD_NO) 등록
						**********************************************************/
						jrParam.setField("STL_NO",				commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
						jrParam.setField("STOCK_MOVE_TERM",		"" );				//영차출발수신시 지정됨.. 여기서는 변경하지 않고 이전 값을 그대로 설정함
						jrParam.setField("FRTOMOVE_WORD_NO",	sFRTOMOVE_WORD_NO);	//이송작업지시번호
						commDao.update(jrParam, updStockTransWordNo, logId, methodNm, "TB_YF_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");

						///**********************************************************
						//* 1-4. 영차도착 포인트 적치단(TB_YF_STKLYR)에 SLAB정보 생성하기
						//**********************************************************/
						//jrParam.setField("STL_NO",				commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO")));
						//jrParam.setField("YD_STK_COL_GP",		sStkClo);
						//jrParam.setField("YD_STK_BED_NO",		"01");
						//jrParam.setField("YD_STK_LYR_NO",		commUtils.format(ii+1, 2));
						//commDao.insert(jrParam, setStackLayer, logId, methodNm, "영차도착 포인트 적치단(TB_YF_STKLYR)에 SLAB정보 생성하기");
					}

					/**********************************************************
					* Pallet조회 (B)에서 지정한 크레인스케줄 생성 모드 확인
					*  - Auto 이면 크레인 스케줄을 호출 한다.
					**********************************************************/
//					jrParam.setField("REPR_CD_GP",	"YM2002");
//					jrParam.setField("CD_GP",		s_STACK_YD_GP);
//					jrParam.setField("ITEM",		s_STACK_BAY_GP);
//					rsResult2 = commDao.select(jrParam, getYfRule, logId, methodNm, "야드 기준 조회");
//
//					if (rsResult2.size() > 0)
//					{
//						sSCH_AUTO_RUN_MODE = rsResult2.getRecord(0).getFieldString("DTL_ITEM1");
//					}
//
//					if("A".equals(sSCH_AUTO_RUN_MODE))	//Auto 모드일 경우만 스케줄 호출
//					{
//						//크레인 스케줄 기동 YFYFJ203 호출
//						jrCrnSchMsg.setField("JMS_TC_CD"			, "YFYFJ203");
//						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
//						jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
//						jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
//
//						jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
//					}
				}

				/**********************************************************
				* 1-5. TB_YD_CARSCH 차량스케쥴(도착시간, 하차 정지위치정보 UPDATE)
				**********************************************************/
				jrParam.setField("YD_CARUD_STOP_LOC",		sStkClo);
				jrParam.setField("YD_PNT_CD3",				szARR_YD_PNT_CD);
				jrParam.setField("YD_CARUD_WRK_BOOK_ID",	wbook_ID);
				jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);
				jrParam.setField("FRTOMOVE_WORD_NO",		sFRTOMOVE_WORD_NO); //이송작업지시번호
				commDao.update(jrParam, updateArrDt5, logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");	//YD_CAR_PROG_STAT = 'B'로 변경(하차도착)
			}
			else if ("E".equals(szTRN_WRK_FULLVOID_GP))
			{
				//2.운송작업영공구분이 E:공차 인 경우 처리
				//**********************************************************************************
				szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				sYD_EQP_WRK_STAT = "U"; //공차

				/**********************************************************
				* 2-4. TB_YD_CARSCH 차량스케쥴 update(도착시간, 상차도착위치 업데이트처리)
				**********************************************************/
				jrParam.setField("YD_CARLD_STOP_LOC",	sStkClo);
				jrParam.setField("TRN_EQP_CD",			szTRN_EQP_CD);
				commDao.update(jrParam, updateArrDt_1, logId, methodNm, "차량스케쥴(도착시간, 상/하차 정지위치정보 UPDATE)");	//YD_CAR_PROG_STAT = '2'로 변경(상차도착)

				//차량스케줄 ,작업예약ID 조회
				jrParam.setField("TRN_EQP_CD",			szTRN_EQP_CD);
				rsResult = commDao.select(jrParam, getListtrnEqpschL_1, logId, methodNm, "차량스케줄 ,작업예약ID 조회");

				if (rsResult.size() > 0)
				{
					s_YD_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
					wbook_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("WBOOK_ID"));

					if (!"".equals(wbook_ID))
					{
						//작업예약이 있는경우(자동상차인 경우)
						/**********************************************************
						* Crane스케줄 호출
						*  - TB_YF_RULE 테이블의 YM2006 기준으로  차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						jrParam.setField("REPR_CD_GP",	"YM2006");
						jrParam.setField("CD_GP",		s_STACK_YD_GP);
						jrParam.setField("ITEM",		s_STACK_BAY_GP);
						rsResult = commDao.select(jrParam, getYfRule, logId, methodNm, "야드 기준 조회");

						String sYD_FRM_YN = "N";

						if (rsResult.size() > 0)
						{
							sYD_FRM_YN = rsResult.getRecord(0).getFieldString("DTL_ITEM1");
						}

						commUtils.printLog(logId, "=======:::: " + s_STACK_BAY_GP + "동 차량형상인식 사용여부: " +  sYD_FRM_YN , "SL");

						if ("N".equals(sYD_FRM_YN))
						{
							//ejbConn = new EJBConnector("default", "ACoilSchBakSeEJB", this);
//							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
//
//							//크레인 스케줄 기동 YFYFJ202 호출
//							jrCrnSchMsg.setField("JMS_TC_CD"			, "YFYFJ202");
//							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
//							jrCrnSchMsg.setField("YD_WBOOK_ID"  		, wbook_ID); //작업예약ID
//							jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
//							jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
//
//							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						}
					}
				}
			}

			/**********************************************************
			* 차량작업 예정정보 송신 (YFF0L008)	//박판SLAB는  L2가 없음...
			**********************************************************/
//			jrParam.setField("SEARCH_FLAG" 			, "1"				);	//1:상차도, 2:차량스케쥴 ID
//			jrParam.setField("PT_LOAD_LOC" 			, sStkClo			); 	//상차도 위치
//			jrRtn = commUtils.addSndData(jrRtn, yfComm.procCarPlanInfo_Slab(jrParam));	//YFF1L008 생성

			/**********************************************************
			* 저장위치제원정보 송신 (YFF0L001)	//박판SLAB는  L2가 없음...
			**********************************************************/
//			jrParam.setField("YD_INFO_SYNC_CD"		, "4"				); //야드정보동기화코드
//			jrParam.setField("YD_STK_COL_GP"    		, sStkClo			);
//			jrParam.setField("YD_STK_BED_NO"    		, "01"				);
//			jrParam.setField("YD_CAR_ARRSTRT_STAT" 	, "A"				); //A:도착, S:출발
//			jrParam.setField("YD_CAR_USE_GP"    	, "L"				); //L:구내운송, G:출하차량
//			jrParam.setField("YD_EQP_WRK_STAT"  	, sYD_EQP_WRK_STAT	); //U:공차, L:영차
//			jrParam.setField("TRN_EQP_CD"  			, szTRN_EQP_CD		); //운송장비코드
//			jrParam.setField("YD_CAR_AIM_YD_GP"		, s_STACK_YD_GP		);
//			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF0L001_CarInfo", jrParam));

			if ("F".equals(szTRN_WRK_FULLVOID_GP))	//운송작업영공구분이 F:영차
			{
				/**********************************************************
				* 저장품제원(YFF0L002) 전문 생성	//박판SLAB는  L2가 없음...
				**********************************************************/
//				jrParam.setField("YD_INFO_SYNC_CD",	"4");		//야드정보동기화코드
//				jrParam.setField("MSG_GP",			"I");		//전문구분
//				jrParam.setField("YD_STK_COL_GP",	sStkClo);	//야드적치열구분
//				jrParam.setField("YD_STK_BED_NO",	"01");		//야드적치Bed번호
//				jrParam.setField("YD_GP",			"2");		//야드구분
//				jrParam.setField("STL_NO",			"");		//재료번호
//				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF0L002", jrParam));
			}

			commUtils.printLog(logId, "=============소재차량도착수신처리_A열연Slab 종료========", "SL");

			commUtils.printLog(logId, methodNm, "SL");

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
	 * [A] 오퍼레이션명 : A열연 차량초기화 작업 (구내운송 차량 초기화)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public void initCarSch(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "A열연 차량초기화 작업[YfCommCarMvSeEJB.initCarSch] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		JDTORecordSet rsResult	= null;

	    String 		szMsg 		= "";

	    String		szTRN_EQP_CD;
	    String		szTRN_WRK_FULLVOID_GP;
	    String		szBACKUP_YN;
	    String		szWLOC_CD;

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============A열연 차량초기화 작업 시작========", "SL");

	    	//수신항목 변수 저장
			szTRN_EQP_CD			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));			//운송장비코드
			szTRN_WRK_FULLVOID_GP	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP"));	//운송작업영공구분  F:영차, E:공차
			szBACKUP_YN				= commUtils.nvl(rcvMsg.getFieldString("BACKUP_YN"), "N");		//BACKUP 구분 (화면에서 강제초기화시 Y)
			szWLOC_CD				= commUtils.nvl(rcvMsg.getFieldString("WLOC_CD"), "");			//개소코드

			if ("E".equals(szTRN_WRK_FULLVOID_GP) || "Y".equals(szBACKUP_YN))	//운송작업영공구분 공차"E" || BACKUP구분"Y"
			{
				if ("Y".equals(szBACKUP_YN))
				{
					/**
					 * 크레인 스케줄 편성 상태인지 체크
					 */
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					rsResult = commDao.select(jrParam, getCrnSchByTrnEqpCd, logId, methodNm, "크레인 스케줄 편성 상태인지 체크");

					if (rsResult.size() > 0)
					{
						szMsg = " 이송대상제가 크레인 스케줄 편성상태 입니다!!! 크레인스케줄을 취소한 후에 초기화를 실행하세요. << " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						throw new Exception(szMsg);
					}

					/**
					 * Layer(단) 저장품 상태를 'C:적치중'로 초기화  - ejb.transaction type="RequiresNew"
					 */
					jrParam.setField("TRN_EQP_CD",		szTRN_EQP_CD);	//운송장비코드
					jrParam.setField("YD_STK_LYR_STAT",	"C");			//적치단 상태 C:적치중
					commDao.update(jrParam, updLayerStatByTrnEqpCd, logId, methodNm, "TB_YF_STKLYR (단) 저장품 상태를 'C:적치중'로 초기화 ");

					/**
					 * 차량위치(적치열) 정리 작업
					 */
					//발지 차량정보 삭제 하기 - 적치단 정리 - ejb.transaction type="RequiresNew"
					jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"C");			//적치 단 활성 상태 C:비활성화
					jrParam.setField("WLOC_CD",					szWLOC_CD);		//개소코드
					jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);	//운송장비코드
					jrParam.setField("MODIFIER",				modifier);
					commDao.update(jrParam, updateLayerstat_02C, logId, methodNm, "TB_YF_STKLYR 발지 차량정보 삭제 하기 - 적치단 정리 ");
				}

				//차량위치 예정정보 삭제(상하차출발 위치) 정리 - ejb.transaction type="RequiresNew"
				jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD);	//운송장비코드
				commDao.update(jrParam, updStackStatByTrnEqpCd, logId, methodNm, "TB_YF_STKCOL 차량위치 예정정보 삭제(상하차출발 위치)정리 ");

				//차량위치정보 삭제(상하차개시/완료/도착 위치) 정리 - ejb.transaction type="RequiresNew"
				jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
				commDao.update(jrParam, updCarUseGpByTrnEqpCd, logId, methodNm, "TB_YF_STKCOL 차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");

				//설비코드로 초기화 하는 경우(구내운송)
				jrParam.setField("STAT",		"C");			//적치형 활성상태
				jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);	//운송장비코드
				commDao.updateTx(jrParam, carpointtrneqpcdupdate, logId, methodNm, "TB_YD_CARPOINT 차량포인트통합관리  << " + methodNm);

				if ("Y".equals(szBACKUP_YN))
				{
					/**
					 * 작업예약, 작업예약재료 삭제
					 */
					jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);	//운송장비코드
					rsResult = commDao.select(jrParam, getWrkBookByTrnEqpCd, logId, methodNm, "운송장비코드로 작업예약 유무 체크");

					if (rsResult.size() > 0)
					{
						szMsg = " 작업예약이 존재함으로 작업예약과 작업예약재료 를 삭제(DEL_YN='Y')처리  << " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						//작업예약(TB_YF_WRKBOOK) 삭제처리 (DEL_YN='Y')
						jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);	//운송장비코드
						jrParam.setField("DEL_YN",		"Y");			//삭제유무 Y:삭제
						commDao.update(jrParam, updDelYnWrkBookByTrnEqpCd, logId, methodNm, "TB_YF_WRKBOOK 작업예약 삭제(DEL_YN='Y')처리 ");

						//작업예약재료(TB_YF_WRKBOOKMTL) 삭제처리 (DEL_YN='Y')
						jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);	//운송장비코드
						jrParam.setField("DEL_YN",		"Y");			//삭제유무 Y:삭제
						commDao.update(jrParam, updDelYnWrkBookMtlByTrnEqpCd, logId, methodNm, "TB_YF_WRKBOOKMTL 작업예약재료 삭제(DEL_YN='Y')처리 ");
					}
				}

				/**
				 * 차량스케줄, 차량이송재료 삭제
				 */
				//차량이송재료(TB_YD_CARFTMVMTL) 삭제처리 (DEL_YN='Y')
				jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);	//운송장비코드
				jrParam.setField("DEL_YN",		"Y");			//삭제유무 Y:삭제
				commDao.update(jrParam, updDelYnCarFtMvMtlByTrnEqpCd, logId, methodNm, "TB_YD_CARFTMVMTL 차량이송재료 삭제(DEL_YN='Y')처리 ");

				//차량스케줄(TB_YD_CARSCH) 삭제처리 (DEL_YN='Y')
				jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);	//운송장비코드
				jrParam.setField("DEL_YN",		"Y");			//삭제유무 Y:삭제
				commDao.update(jrParam, updDelYnCarSchByTrnEqpCd, logId, methodNm, "TB_YD_CARSCH 차량스케줄 삭제(DEL_YN='Y')처리 ");
			}

			commUtils.printLog(logId, "=============A열연 차량초기화 작업 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");
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
	 * [A] 오퍼레이션명 : 소재차량출발(TSYDJ004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ004(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "소재차량출발[YfCommCarMvSeEJB.rcvTSYDJ004] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============소재차량출발 시작========", "SL");

	    	//수신항목 변수 저장
			String szSPOS_WLOC_CD	= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));	//발지개소코드
			String szARR_WLOC_CD	= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"));		//착지개소코드

			//운송지시 발지,착지개소코드로 방향을 판단한다.
			// AA : AB열연에서 AB열연으로 이송
			// AC : AB열연에서 일관제철로 이송
			// CA : 일관제철에서 AB열연으로 이송
			// CC : 일관제철에서 일관제철로 이송
			String sWorkGp = this.getABLocationInfo_01(szSPOS_WLOC_CD, szARR_WLOC_CD,logId);

			if (!"CC".equals(sWorkGp))
			{
				rcvMsg.setField("WORK_GP", sWorkGp);

				if
				(
					YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)		||	//착지개소(D2Y44) : A열연-#1 제품/소재 Coil Yard
					YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)		||	//착지개소(D2Y45) : A열연-#2 제품/소재 Coil Yard
					YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szSPOS_WLOC_CD)	||	//발지개소(D2Y44) : A열연-#1 제품/소재 Coil Yard
					YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szSPOS_WLOC_CD)		//발지개소(D2Y45) : A열연-#2 제품/소재 Coil Yard
				)
				{
					//A열연 Coil 야드인 경우
					jrRtn = this.rcvTSYDJ004_ACoil(rcvMsg);
				}
				else if
				(
					YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD)	||	//착지개소(D2Y43) : A연주-B Cast Slab Yard
					YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szSPOS_WLOC_CD)		//발지개소(D2Y43) : A연주-B Cast Slab Yard
				)
				{
					//A열연 Slab 야드인 경우
					jrRtn = this.rcvTSYDJ004_ASlab(rcvMsg);
				}
			}

			commUtils.printLog(logId, "=============소재차량출발 종료========", "SL");

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
	 *      [A] 오퍼레이션명 : A열연 COIL야드 소재차량출발(TSYDJ004) 수신처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYDJ004_ACoil(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "소재차량출발수신처리_A열연Coil[YfCommCarMvSeEJB.rcvTSYDJ004_ACoil] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

	    JDTORecord 	jrTemp		= null;
		JDTORecordSet rsResult  = null;
		String 		szMsg		= "";

	    String		szTRN_EQP_CD;
	    String		szSPOS_WLOC_CD;
	    String		szSPOS_YD_PNT_CD;
	    String		szARR_WLOC_CD;
	    String		szARR_YD_PNT_CD;
	    String		szTRN_WRK_FULLVOID_GP;
	    String		szTRN_EQP_STK_CAPA;
	    String		sWorkGp;
	    String		szTRN_EQP_GP;

	    String		szYD_MSG_NM			= "";
	    String		s_STL_APPEAR_GP 	= "";
	    String		unloadStopwloc		= "";
	    String		unloadStoppoint		= "";
		String		sloadStopTsCd 		= "";
		String		sunloadStoppoint 	= "";
		String		s_CAR_SCH_ID		= "";
		String		s_STACK_YD_GP 		= "";
		String		s_STACK_BAY_GP 		= "";
		String		s_ARR_WLOC_CD		= "";
		String		s_YD_EQP_WRK_STAT	= "";
		String		szREAL_TRN_EQP_CD	= "";

	    try
	    {
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ004");	//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============소재차량출발수신처리_A열연Coil 시작========", "SL");

	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szSPOS_WLOC_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")); 		//발지개소코드
			szSPOS_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")); 		//발지야드포인트코드
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= "PT";	//szTRN_EQP_CD.substring(1, 3);							//PT/TR 구분이 사라지고 PT로 통합됨...

			jrParam.setField("WLOC_CD",		szSPOS_WLOC_CD);	//발지개소코드(상차지)
			jrParam.setField("YD_PNT_CD",	szSPOS_YD_PNT_CD);	//발지개소코드(상차지)
			JDTORecordSet rsChk  = commDao.select(jrParam, getCarPointChk, logId, methodNm, "TB_YD_CARPOINT 에서 점유 중인 차량번호 조회");

			if (rsChk.size() > 0 )
			{
				szREAL_TRN_EQP_CD = commUtils.trim(rsChk.getRecord(0).getFieldString("TRN_EQP_CD"));
			}

			szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " ■ ■ ■ ";
			commUtils.printLog(logId, szMsg, "SL");

			//TRN_EQP_CD(운송장비코드)로 차량스케줄을 체크하여 남아있는 스케줄이 있다면 초기화 한다.
			this.initCarSch(rcvMsg);

			if ("DMY1P".equals(szARR_WLOC_CD))
			{
				//착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제
				szMsg="착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				//차량위치 예정정보 삭제(상하차출발 위치) 정리
				jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
				commDao.update(jrParam, updStackStatByTrnEqpCd, logId, methodNm, "TB_YF_STKCOL 차량위치 예정정보 삭제(상하차출발 위치)정리 ");

				//AC로 처리되서 하단 clearSposCarInfo 에서 처리함
				//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				//this.YfCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
			}

			if ("E".equals(szTRN_WRK_FULLVOID_GP))		//E:공차
			{
				s_YD_EQP_WRK_STAT = "U";	//야드설비작업상태(U) : 공차
			}
			else if ("F".equals(szTRN_WRK_FULLVOID_GP))	//F:영차
			{
				s_YD_EQP_WRK_STAT = "L";	//야드설비작업상태(L) : 영차
			}

			//운송지시 발지,착지개소코드로 방향을 판단한다.(AA:AB열연에서 AB열연으로 이송,AC:AB열연에서 일관제철로 이송,CA:일관제철에서 AB열연으로 이송)
			sWorkGp = commUtils.trim(rcvMsg.getFieldString("WORK_GP"));

			if ("AA".equals(sWorkGp)||"CA".equals(sWorkGp))
			{
				//**********************************************************************************
				//1.AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우

				szMsg="AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				if ("F".equals(szTRN_WRK_FULLVOID_GP))	//F:영차
				{
					//**********************************************************************************
					//1-1.운송작업영공구분이 F:영차 인 경우 처리
					szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					//코일 공통에서 STL_APPEAR_GP를 TS_소재이송지시(TB_TS_MATL_FTMV_WO)에서 착지개소코드(ARR_WLOC_CD), 착지야드포인트(ARR_YD_PNT_CD)를 조회한다.
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
					rsResult = commDao.select(jrParam, getListFrtostlList_CoilAppearGp, logId, methodNm, "코일 공통에서 STL_APPEAR_GP를 TS_소재이송지시(MTP)에서 착지개소코드, 착지야드포인트를 조회");

					if (rsResult.size() > 0)
					{
						s_STL_APPEAR_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));	//코일공통에서 조회된 재료외형구분(Y:제품, 그외:소재)
						unloadStopwloc  = commUtils.trim(rsResult.getRecord(0).getFieldString("ARR_WLOC_CD"));		//TS_소재이송지시(TB_TS_MATL_FTMV_WO) 에서 조회한 것으로 제품일 경우만 사용한다.
						unloadStoppoint = commUtils.trim(rsResult.getRecord(0).getFieldString("ARR_YD_PNT_CD"));	//TS_소재이송지시(TB_TS_MATL_FTMV_WO) 에서 조회한 것으로 제품일 경우만 사용한다. (동 정보)

						szMsg=" 검색결과 >> STL_APPEAR_GP : " + s_STL_APPEAR_GP +", ARR_WLOC_CD : " + unloadStopwloc + ", ARR_YD_PNT_CD : " + unloadStoppoint;
						commUtils.printLog(logId, szMsg, "SL");
					}

					if("AA".equals(sWorkGp) && (YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szSPOS_YD_PNT_CD) || YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szSPOS_YD_PNT_CD)))
					{
						szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리(발지가 박판Coil인경우)  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						//발지 차량정보 삭제처리
						//this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
						EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
						ejbConn1.trx("clearSposCarInfo", new Class[] { String.class,String.class,String.class,String.class,String.class }
										              , new Object[] { szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier });
					}

					if("Y".equals(s_STL_APPEAR_GP))
					{
						//**********************************************************************************
						//1-1-1.제품인 경우 처리
						szMsg="운송작업영공구분이 F:영차 이면서 제품인 경우 처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						//코일 제품 하차 정지위치 검색 (해당 목적동에 점유하지 않은 포인트 검색)
						jrParam.setField("WLOC_CD",		unloadStopwloc);	//TS_소재이송지시 (MTP)의 착지개소코드
						jrParam.setField("YD_PNT_CD",	unloadStoppoint);	//TS_소재이송지시 (MTP)의 착지야드포인트코드
						rsResult = commDao.select(jrParam, getListloadStoppointGD, logId, methodNm, "코일 제품 하차 정지위치 검색"); //**주의! A열연,B열연 분리 해야함!!

						if(rsResult.size() > 0)
						{
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
							sloadStopTsCd 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));

							szMsg=" 하차포인트 : " + sloadStopTsCd +", 야드하차위치 : " + sunloadStoppoint + " 에 예약";
							commUtils.printLog(logId, szMsg, "SL");

							//TB_YF_STKCOL 예약정보등록
							jrParam.setField("YD_STK_STAT",		"L");	//L:상차작업상태
							jrParam.setField("CAR_CARD_NO",		szTRN_EQP_CD);
							jrParam.setField("YD_STK_COL_GP",	sunloadStoppoint);
							commDao.update(jrParam, updateEquipcolStat, logId, methodNm, "TB_YF_STKCOL 예약정보등록");

							//차량포인트통합관리 - 예약
							this.YfCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
						}
						else
						{
							szMsg=" 검색결과 >> 포인트를 찾지 못함! '0000'";
							commUtils.printLog(logId, szMsg, "SL");

							sloadStopTsCd = "0000"; //포인트 없음

							//대기장 포인트 사유 가져오기
							szYD_MSG_NM = this.getCarMsg(s_STL_APPEAR_GP, "", "", szTRN_EQP_GP, unloadStopwloc, unloadStoppoint, logId, methodNm);
						}
					}
					else
					{
						//**********************************************************************************
						//1-1-2.소재인 경우 처리
						szMsg="운송작업영공구분이 F:영차 이면서 소재인 경우 처리  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD); //운송장비코드
						rsResult = commDao.select(jrParam, getCarSchByTrnEqpCd, logId, methodNm, "차량스케줄 존재여부 체크");

						if (rsResult.size() > 0)
						{
							s_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
							szMsg=" 검색결과 >> YD_CAR_SCH_ID : " + s_CAR_SCH_ID ;
							commUtils.printLog(logId, szMsg, "SL");
						}

						//소재인 경우는 코일공통에 계획공정 및 차공정(우선)으로 목적 동 정보를 구한다.
						if
						(
							YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	||	//A열연-#1 제품/소재 Coil Yard (D2Y44)
							YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)		//A열연-#2 제품/소재 Coil Yard (D2Y45)
						)
						{
				    		//A열연 COIL야드
			    			s_STACK_YD_GP 	= "1";
							jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID); //야드차량스케쥴ID
							rsResult = commDao.select(jrParam, getListAimBay_ACoil, logId, methodNm, "A열연 COIL야드 소재 하차 이송 목적동 검색");

							if (rsResult.size() > 0)
							{
						    	s_STACK_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("DEST_BAY")); //목적동
							}

							szMsg=" A열연 COIL야드 소재 하차 이송 목적동 검색결과 >> DEST_BAY : " + s_STACK_BAY_GP ;
							commUtils.printLog(logId, szMsg, "SL");
			    		}

						//-----------------------------------------------------------------------
						//코일 소재 하차 정지위치 검색 (해당 목적동에 점유하지 않은 포인트 검색)
						jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
						jrParam.setField("BAY_GP",	s_STACK_BAY_GP);
						rsResult = commDao.select(jrParam, getListloadStoppointCM, logId, methodNm, "코일 소재 하차 정지위치 검색"); //**주의! A열연,B열연 분리 해야함!!

						if(rsResult.size() > 0)
						{
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
							sloadStopTsCd 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));

							szMsg=" 하차포인트 : " + sloadStopTsCd +", 야드하차위치 : " + sunloadStoppoint + " 에 예약";
							commUtils.printLog(logId, szMsg, "SL");

							//TB_YF_STKCOL 예약정보등록
							jrParam.setField("YD_STK_STAT",		"L");	//L:상차작업상태
							jrParam.setField("CAR_CARD_NO",		szTRN_EQP_CD);
							jrParam.setField("YD_STK_COL_GP",	sunloadStoppoint);
							commDao.update(jrParam, updateEquipcolStat, logId, methodNm, "TB_YF_STKCOL 예약정보등록");

							//차량포인트통합관리 - 예약
							this.YfCarPointinforeg("3","",szTRN_EQP_CD,sunloadStoppoint,"","","R", logId, methodNm);
						}
						else
						{
							szMsg=" 검색결과 >> 포인트를 찾지 못함! '0000'";
							commUtils.printLog(logId, szMsg, "SL");

							sloadStopTsCd = "0000"; //포인트 없음

							//대기장 포인트 사유 가져오기
							szYD_MSG_NM = this.getCarMsg("N", s_STACK_YD_GP, s_STACK_BAY_GP, szTRN_EQP_GP, szARR_WLOC_CD, unloadStoppoint, logId, methodNm);
						}
					}

					//**********************************************************************************
					//1-1-3.차량스케쥴 하차출발(A)로 UPDATE
					szMsg="차량스케쥴 하차출발(A)로 UPDATE   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					jrParam.setField("YD_CAR_PROG_STAT",		"A");				//차량진행상태 (A:하차출발)
					jrParam.setField("YD_CAR_USE_GP",			"L");				//야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT",			"L");				//야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("ARR_WLOC_CD",				szARR_WLOC_CD);		//착지개소코드
					jrParam.setField("YD_PNT_CD",				sloadStopTsCd);		//야드포인트코드(착지)
					jrParam.setField("YD_CARUD_STOP_LOC",		sunloadStoppoint);	//야드하차정지위치
					jrParam.setField("YD_CARUD_WRK_BOOK_ID",	"");				//야드하차작업예약ID
					jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드
					commDao.update(jrParam, updCarSchUdByTrnEqpCd, logId, methodNm, "차량스케쥴 하차출발(A) 업데이트 ");
				}
				else if ("E".equals(szTRN_WRK_FULLVOID_GP))	//E:공차
				{
					//**********************************************************************************
					//1-2.운송작업영공구분이 E:공차 인 경우 처리
					szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					//**********************************************************************************
					//1-2-1.상차 대상재 조회 (목적동 찾기)
					if
					(
						YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szARR_WLOC_CD)	||	//A열연-#1 제품/소재 Coil Yard (D2Y44)
						YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szARR_WLOC_CD)		//A열연-#2 제품/소재 Coil Yard (D2Y45)
					)
					{
						//A열연 COIL 야드인 경우 대상재 조회
						jrParam.setField("WLOC_CD",		szARR_WLOC_CD);
						jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
						rsResult = commDao.select(jrParam, getListFrtoStl_CoilNewA, logId, methodNm, "A열연 COIL 야드인 경우 대상재 조회");

						if (rsResult.size() > 0)
						{
							s_STACK_YD_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_GP"));
							s_STACK_BAY_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("BAY_GP"));
						}
					}

					//**********************************************************************************
					//1-2-2.상차정지위치 검색
					jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
					jrParam.setField("YD_GP",	s_STACK_YD_GP);
					jrParam.setField("BAY_GP",	s_STACK_BAY_GP);
					rsResult = commDao.select(jrParam, getListloadStoppoint, logId, methodNm, "상차정지위치 검색 - 검색대상재 동에 있는 차량위치정보");

					if (rsResult.size() > 0)
					{
						sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
						sloadStopTsCd 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));

						szMsg=" 상차포인트 : " + sloadStopTsCd +", 야드상차위치 : " + sunloadStoppoint + " 에 예약";
						commUtils.printLog(logId, szMsg, "SL");

						//TB_YF_STKCOL 예약정보등록
						jrParam.setField("YD_STK_STAT",		"L");	//L:상차작업상태
						jrParam.setField("CAR_CARD_NO",		szTRN_EQP_CD);
						jrParam.setField("YD_STK_COL_GP",	sunloadStoppoint);
						commDao.update(jrParam, updateEquipcolStat, logId, methodNm, "TB_YF_STKCOL 예약정보등록");

						//차량포인트통합관리 - 예약
						this.YfCarPointinforeg("3", "", szTRN_EQP_CD, sunloadStoppoint, "", "", "R", logId, methodNm);
					}
					else
					{
						szMsg=" 검색결과 >> 포인트를 찾지 못함! '0000'";
						commUtils.printLog(logId, szMsg, "SL");

						sloadStopTsCd = "0000"; //포인트 없음

						//대기장 포인트 사유 가져오기
						szYD_MSG_NM = this.getCarMsg("N", s_STACK_YD_GP, s_STACK_BAY_GP, szTRN_EQP_GP, szARR_WLOC_CD, unloadStoppoint, logId, methodNm);
					}

					if("AA".equals(sWorkGp) && (YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szSPOS_YD_PNT_CD) || YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szSPOS_YD_PNT_CD)))
					{
						szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리(발지가 박판COIL인 경우  < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						//발지 차량정보 삭제처리
						//this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
						EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
						ejbConn1.trx("clearSposCarInfo", new Class[] { String.class,String.class,String.class,String.class,String.class }
										              , new Object[] { szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier });

						JDTORecord jrParam1			= JDTORecordFactory.getInstance().create();
						jrParam1.setResultCode(logId);		//Log ID
						jrParam1.setResultMsg(methodNm);	//Log Method Name
						jrParam1.setField("WLOC_CD",	szSPOS_WLOC_CD);	//발지개소코드(상차지)
						jrParam1.setField("YD_PNT_CD",	szSPOS_YD_PNT_CD);	//발지야드포인트코드(상차지)
						JDTORecordSet rsResult4  = commDao.select(jrParam1, getCarPointChk, logId, methodNm, "대상재조회");

						if(szREAL_TRN_EQP_CD.equals(szTRN_EQP_CD))
						{
							if(rsResult4.size() > 0)
							{
								sunloadStoppoint = commUtils.trim(rsResult4.getRecord(0).getFieldString("YD_STK_COL_GP"));

								/**********************************************************
								* 저장위치제원정보 송신 (YFF1L001) -- 차량출발
								**********************************************************/
								JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
								sndL2Msg.setResultCode(logId);		//Log ID
								sndL2Msg.setResultMsg(methodNm);	//Log Method Name
								sndL2Msg.setField("YD_INFO_SYNC_CD",		"3");				//야드정보동기화코드
								sndL2Msg.setField("YD_STK_COL_GP",			sunloadStoppoint);
								sndL2Msg.setField("YD_STK_BED_NO",			"01");
								sndL2Msg.setField("YD_CAR_ARRSTRT_STAT",	"S");				//A:도착, S:출발
								sndL2Msg.setField("YD_CAR_USE_GP",			"L");				//L:구내운송, G:출하차량
								sndL2Msg.setField("YD_EQP_WRK_STAT",		s_YD_EQP_WRK_STAT);	//U:공차, L:영차
								sndL2Msg.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드
								sndL2Msg.setField("YD_CAR_AIM_YD_GP",		"1");

								//전송 Data 생성
								jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001_CarInfo", sndL2Msg));
							}
						}
						else
						{
							szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " 가 동일하지 않아서  저장위치제원정보(YFF1L001) 차량출발을 전송하지 않는다! ■ ■ ■ ";
							commUtils.printLog(logId, szMsg, "SL");
						}
					}

					//**********************************************************************************
					//1-2-3.차량스케쥴 상차출발(1)로 INSERT
					szMsg="차량스케쥴 상차출발(1)로 INSERT   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					jrParam.setField("YD_CAR_SCH_ID",			commDao.getSeqId(logId, methodNm, "CarSch")); //야드차량스케쥴ID
					jrParam.setField("YD_CAR_PROG_STAT",		"1");				//차량진행상태 (1:상차출발)
					jrParam.setField("YD_CAR_USE_GP",			"L");				//야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT",			"U");				//야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("SPOS_WLOC_CD",			szARR_WLOC_CD);		//발지개소코드(상차지)
					jrParam.setField("ARR_WLOC_CD",				s_ARR_WLOC_CD);		//착지개소코드(하차지)
					jrParam.setField("YD_PNT_CD",				sloadStopTsCd);		//야드상차포인트코드(발지)
					jrParam.setField("YD_CARLD_WRK_BOOK_ID",	"");				//야드상차작업예약ID
					jrParam.setField("YD_CARLD_STOP_LOC",		sunloadStoppoint);	//야드하차정지위치
					jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드

					commDao.insert(jrParam, insCarSchLd, logId, methodNm, "차량스케쥴 상차출발(1)로 INSERT ");
				}

				szMsg="포인트 검색결과 : "+sloadStopTsCd + " < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				//**********************************************************************************
				//3. 포인트 지시 전송
				jrRtn = commUtils.addSndData(jrRtn, this.makeYDTSJ011(szTRN_EQP_CD, szARR_WLOC_CD, sloadStopTsCd, szYD_MSG_NM ,logId));

				if (!"0000".equals(sloadStopTsCd))
				{
					//도착할 포인트가 있으면..
					//**********************************************************************************
					//3-1. 포인트점유사항 출하송신 (YDDMR026)
					//  ==> 기존 소스(TsInfRegSBean.procMatlCarArrPntSenddm) 확인 결과 송신하지 않음

					//**********************************************************************************
					//3-2. L2 차량예정정보 전송 --> 전송시점 확인 필요!!
					//jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L008", jrParam));
				}
			}
			else if ("AC".equals(sWorkGp))
			{
				//**********************************************************************************
				//2.AB열연에서 일관제철로 이송인 경우
				szMsg="AB열연에서 일관제철로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				//발지 차량정보 삭제처리
				this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);
//				EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
//				ejbConn1.trx("clearSposCarInfo", new Class[] { String.class,String.class,String.class,String.class,String.class }
//								              , new Object[] { szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier });

				if(szREAL_TRN_EQP_CD.equals(szTRN_EQP_CD))
				{
					JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
					jrParam1.setResultCode(logId);		//Log ID
					jrParam1.setResultMsg(methodNm);	//Log Method Name
					jrParam1.setField("WLOC_CD",	szSPOS_WLOC_CD);	//발지개소코드(상차지)
					jrParam1.setField("YD_PNT_CD",	szSPOS_YD_PNT_CD);	//발지야드포인트코드(상차지)
					JDTORecordSet rsResult4  = commDao.select(jrParam1, getCarPointChk, logId, methodNm, "대상재조회");

					if (rsResult4.size() > 0 )
					{
						sunloadStoppoint = commUtils.trim(rsResult4.getRecord(0).getFieldString("YD_STK_COL_GP"));
						/**********************************************************
						* 저장위치제원정보 송신 (YFF1L001) -- 차량출발
						**********************************************************/
						JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
						sndL2Msg.setResultCode(logId);		//Log ID
						sndL2Msg.setResultMsg(methodNm);	//Log Method Name
						sndL2Msg.setField("YD_INFO_SYNC_CD",		"3");				//야드정보동기화코드
						sndL2Msg.setField("YD_STK_COL_GP",			sunloadStoppoint);
						sndL2Msg.setField("YD_STK_BED_NO",			"01");
						sndL2Msg.setField("YD_CAR_ARRSTRT_STAT",	"S");				//A:도착, S:출발
						sndL2Msg.setField("YD_CAR_USE_GP",			"L");				//L:구내운송, G:출하차량
						sndL2Msg.setField("YD_EQP_WRK_STAT",		s_YD_EQP_WRK_STAT);	//U:공차, L:영차
						sndL2Msg.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드
						sndL2Msg.setField("YD_CAR_AIM_YD_GP",		"1");

						//전송 Data 생성
						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001_CarInfo", sndL2Msg));
					}
				}
				else
				{
					szMsg="■ ■ ■ 수신된 TRN_EQP_CD : " + szTRN_EQP_CD + " , 실제 점유 중인 TRN_EQP_CD : " + szREAL_TRN_EQP_CD + " 가 동일하지 않아서  저장위치제원정보(YFF1L001) 차량출발을 전송하지 않는다! ■ ■ ■ ";
					commUtils.printLog(logId, szMsg, "SL");
				}
			}

			/********************************************************
			 * 이송차량 영차/공차 출발시 해당 위치대기차량 입동지시 요구
			 ********************************************************/
			String sAPPLY052 = yfComm.ACoilApplyYn("APP052", "1", "1");		//이송차랑 영차출발시 입동지시호출
			commUtils.printLog(logId,  "이송차랑 출발시 입동지시호출:" + sAPPLY052, "[APP052]");

			if ("Y".equals(sAPPLY052))
			{
				if (YfConstant.WLOC_CD_A_HR_NO1_COIL_YARD.equals(szSPOS_WLOC_CD) || YfConstant.WLOC_CD_A_HR_NO2_COIL_YARD.equals(szSPOS_WLOC_CD))
				{
					jrParam.setField("WLOC_CD",		szSPOS_WLOC_CD);	//발지개소코드(상차지)
					jrParam.setField("YD_PNT_CD",	szSPOS_YD_PNT_CD);	//발지야드포인트코드(상차지)
					JDTORecordSet jsRst = commDao.select(jrParam, getYdCarPntCd, logId, methodNm, "YD_CARPNT_CD 조회");

					if (jsRst.size() > 0)
					{
						JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
						jrMsg.setResultCode(logId);		//Log ID
						jrMsg.setResultMsg(methodNm);	//Log Method Name
						jrMsg.setField("JMS_TC_CD",				"YFYFJ662");                //차량입동지시 요구 기존:YDYDJ662
						jrMsg.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());	//JMSTC생성일시
						jrMsg.setField("YD_CARPNT_CD",			jsRst.getRecord(0).getFieldString("YD_CARPNT_CD"));

						jrRtn = commUtils.addSndData(jrRtn, jrMsg);
					}
				}
			}

			commUtils.printLog(logId, "=============소재차량출발수신처리_A열연Coil 종료========", "SL");

			commUtils.printLog(logId, methodNm, "SL");

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
	} //end of rcvTSYDJ004_ACoil()

	/**
	 * [A] 오퍼레이션명 : A열연 SLAB야드 소재차량출발(TSYDJ004) 수신처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ004_ASlab(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "소재차량출발수신처리_A열연Slab[YfCommCarMvSeEJB.rcvTSYDJ004_ASlab] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

	    String		szMsg		= "";
	    JDTORecord 	jrTemp		= null;
	    JDTORecordSet rsResult	= null;
	    JDTORecordSet rsResult2	= null;
	    JDTORecordSet rsResult3 = null;

	    String		szTRN_EQP_CD;
	    String		szSPOS_WLOC_CD;
	    String		szSPOS_YD_PNT_CD;
	    String		szARR_WLOC_CD;
	    String		szARR_YD_PNT_CD;
	    String		szTRN_WRK_FULLVOID_GP;
	    String		szTRN_EQP_STK_CAPA;
	    String		sWorkGp;
	    String		szTRN_EQP_GP;

	    String		ydWbookId			= "";
	    String		s_CAR_SCH_ID		= "";
	    String		s_YD_STK_COL_GP		= "";
	    String		s_STACK_YD_GP		= "";
	    String		s_STACK_BAY_GP		= "";
	    String		s_STACK_BAY_GP1		= "";
	    String		s_STACK_BAY_GP2		= "";
	    String		s_STACK_BAY_GP3		= "";
	    String		sunloadStoppoint 	= "";
	    String		sunloadStopTsCd		= "";
	    String		szYD_MSG_NM			= "";

	    String		s_STL_NO			= "";
	    String		s_C3_SCARF_TRF_YN	= "";
	    String		sYD_EQP_WRK_STAT	= "";

	    String		inCarspec;
	    String		outCarspec;

	    String		sMoveterm			= "";
	    String		ydSchPrior			= "";

	    try
	    {
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.nvl(commUtils.getMsgId(rcvMsg),"TSYDJ004");	//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============소재차량출발수신처리_A열연Slab 시작========", "SL");

			//TRN_EQP_CD(운송장비코드)로 차량스케줄을 체크하여 남아있는 스케줄이 있다면 초기화 한다.
			this.initCarSch(rcvMsg);

	    	//수신항목 변수 저장
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 			//운송장비코드
			szSPOS_WLOC_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")); 		//발지개소코드
			szSPOS_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")); 		//발지야드포인트코드
			szARR_WLOC_CD      		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 		//착지개소코드
			szARR_YD_PNT_CD      	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 		//착지야드포인트코드
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); //운송작업영공구분
			szTRN_EQP_STK_CAPA      = commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA")); 	//운송장비적재능력
			szTRN_EQP_GP 			= "PT";	//szTRN_EQP_CD.substring(1, 3);							//PT/TR 구분이 사라지고 PT로 통합됨...

	    	//B-CAST 트레일러 이면서 공차인경우 운송설비구분을  PT 로  변경
			if ("TR".equals(szTRN_EQP_GP) && YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD) && "E".equals(szTRN_WRK_FULLVOID_GP))
			{
	    		szTRN_EQP_GP = "PT";
	    	}

			if ("DMY1P".equals(szARR_WLOC_CD))
			{
				//착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제
				szMsg="착지개소가 중장비수리고(DMY1P)이면 예약정보 삭제 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				//TB_YF_STKCOL에서 차량위치 예정정보 삭제(상하차출발 위치) 정리
				jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD); //운송장비코드
				commDao.update(jrParam, updStackStatByTrnEqpCd, logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리 ");

				//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				this.YfCarPointinforeg("1", "", szTRN_EQP_CD, "", "", "", "C", logId, methodNm);
			}

			if ("F".equals(szTRN_WRK_FULLVOID_GP))	//운송작업영공구분(F:영차, E:공차)
			{
				sYD_EQP_WRK_STAT = "L";	//야드설비작업상태 : L(영차)
			}
			else
			{
				sYD_EQP_WRK_STAT = "U";	//야드설비작업상태 : U(공차)
			}

			//운송지시 발지,착지개소코드로 방향을 판단한다.
			//(AA:AB열연에서 AB열연으로 이송,AC:AB열연에서 일관제철로 이송,CA:일관제철에서 AB열연으로 이송)
			sWorkGp = commUtils.trim(rcvMsg.getFieldString("WORK_GP"));

			if("AA".equals(sWorkGp) || "CA".equals(sWorkGp))
			{
				/**********************************************************
				* 1. AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우
				**********************************************************/
				szMsg="AB열연에서 AB열연으로 이송, 일관제철에서 AB열연으로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				if ("F".equals(szTRN_WRK_FULLVOID_GP))
				{
					/**********************************************************
					* 1-1. 운송작업영공구분이 F:영차 인 경우 처리
					**********************************************************/
					szMsg="운송작업영공구분이 F:영차 인 경우 처리  < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					//야드구분 지정
					if (YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD))
					{
						s_STACK_YD_GP 	= "0";  //박판 SLAB b-cast
					}

					//운송장비코드로 차량스케줄ID 조회
					jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
					rsResult = commDao.select(jrParam, getListFrtostlList_1, logId, methodNm, "운송장비코드로 차량스케줄ID 조회");

					if (rsResult.size() <= 0)
					{
						throw new Exception("차량스케줄ID(YD_CAR_SCH_ID) 조회 실패!!!");
					}

					s_CAR_SCH_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));

					//이송목적동 조회
					if("AA".equals(sWorkGp) && YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD))
					{
						//AB열연에서 AB열연으로 이송 + 착지가 박판 SLAB b-cast 야드인경우
						jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
						jrParam.setField("YD_GP",	s_STACK_YD_GP);
						rsResult = commDao.select(jrParam, getListloadStoppointBCAST, logId, methodNm, "B-CAST(D2Y43) 하차정지위치 검색 ");

						if (rsResult.size() > 0)
						{
							s_YD_STK_COL_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));

							s_STACK_BAY_GP	= s_YD_STK_COL_GP.substring(1, 2);	//비어있는 동
						}
						else
						{
							s_STACK_BAY_GP = "A";
						}
					}
					else
					{
						s_STACK_BAY_GP = this.getSlabAimCd(s_CAR_SCH_ID , szSPOS_WLOC_CD , logId , methodNm);
					}

					if ("".equals(s_STACK_BAY_GP))
					{
						throw new Exception("이송목적동 조회 실패!!!");
					}

					szMsg=" 결과 >> 착지정보 : 야드구분 : " + s_STACK_YD_GP +", 차량스케줄ID : " + s_CAR_SCH_ID + ", 이송목적동 : " + s_STACK_BAY_GP;
					commUtils.printLog(logId, szMsg, "SL");

					/**********************************************************
					* 1-1-1. 하차정지위치 검색
					**********************************************************/
					szMsg="하차정지위치 검색  < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					rsResult = new JDTORecordSetImplList();	//rsResult (JDTORecordSet초기화)

					if(YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD))
					{
						//B-CAST(D2Y43)
						jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
						jrParam.setField("YD_GP",	s_STACK_YD_GP);
						rsResult = commDao.select(jrParam, getListloadStoppointBCAST, logId, methodNm, "B-CAST(D2Y43) 하차정지위치 검색 ");
					}

					if (rsResult.size() > 0)
					{
						sunloadStoppoint	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
						sunloadStopTsCd		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));

						s_STACK_BAY_GP		= sunloadStoppoint.substring(1, 2);	//비어있는 동
					}
					else
					{
						if ("PT".equals(szTRN_EQP_GP))
						{
							//포인트 모두 점유상태일때 하차완료된 포인트찾음...2020.01.09 상하차완료 되었어도 포인트 내려주지 않음
//							jrParam.setField("YD_GP",	s_STACK_YD_GP);
//							jrParam.setField("BAY_GP",	s_STACK_BAY_GP);
//							jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
//							rsResult = commDao.select(jrParam, getListUnloadEndpoint_slab, logId, methodNm, "포인트 모두 점유상태일때 하차완료된 포인트찾음");
//
//							if (rsResult.size() > 0)
//							{
//								jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
//								rsResult2 = commDao.select(jrParam, getListCarSpec, logId, methodNm, "YD_차량사양 테이블에서 운송장비코드로 CAR_NO 조회");
//
//								inCarspec = commUtils.trim(rsResult2.getRecord(0).getFieldString("CAR_NO"));
//
//								for(int ii = 0 ; ii < rsResult.size() ; ii++)
//								{
//									outCarspec = commUtils.trim(rsResult.getRecord(ii).getFieldString("CAR_NO"));
//
//									if (inCarspec.equals(outCarspec))
//									{
//										sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_STK_COL_GP"));
//										sunloadStopTsCd		= commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_PNT_CD"));
//									}
//								}
//							}
						}
					}

					if (rsResult.size() <= 0 || "".equals(sunloadStopTsCd))
					{
						//포인트찾지 못함
						sunloadStoppoint	= "XXPTXX";
						sunloadStopTsCd		= "0000";

						szYD_MSG_NM = this.getCarMsg("N", s_STACK_YD_GP, s_STACK_BAY_GP, szTRN_EQP_GP, szARR_WLOC_CD, "", logId, methodNm);	//대기장 포인트 사유 가져옴
					}

					if("AA".equals(sWorkGp) && YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szSPOS_WLOC_CD))
					{
						szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리...YF모듈호출 < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						//발지 차량정보 삭제처리
						this.clearSposCarInfo(szSPOS_WLOC_CD, szSPOS_YD_PNT_CD, szTRN_EQP_CD, logId, modifier);	//TB_YF_STKCOL, TB_YF_STKLYR, TB_YD_CARPOINT 초기화
					}

					/**********************************************************
					* 1-1-2. 차량이송재료 조회
					**********************************************************/
					szMsg="차량이송재료 조회  < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					//차량스케쥴ID로 이송재료 조회 (운송장비코드로 차량스케줄ID를 구하고 차량스케줄ID로 차량이송재료를 구한다.)
					jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
					rsResult = commDao.select(jrParam, getListFrtostlList2, logId, methodNm, "운송장비코드로 차량스케줄ID를 구하고 차량스케줄ID로 차량이송재료를 조회");

					if (rsResult.size() <= 0)
					{
						throw new Exception("차량이송재료 조회 실패!!!");
					}

					for(int ii = 0 ; ii < rsResult.size() ; ii++)
					{
						//검색된 이송재료 1건씩 읽어서 아래  처리
						s_STL_NO			= commUtils.trim(rsResult.getRecord(ii).getFieldString("STL_NO"));
						s_C3_SCARF_TRF_YN	= commUtils.trim(rsResult.getRecord(ii).getFieldString("C3_SCARF_TRF_YN")); //CA 항만스카핑 핸드구분

						if ("CA".equals(sWorkGp) && "Y".equals(s_C3_SCARF_TRF_YN))
						{
							//일관제철에서 AB열연으로 이송인 경우이면서 C3_SCARF_TRF_YN 이 'Y'인 경우
							sMoveterm = YfConstant.NEW_STOCK_MOVE_TERM_D3; //핸드스카핑작업대기
						}
						else
						{
							sMoveterm = YfConstant.NEW_STOCK_MOVE_TERM_CS; //이송대기

							//다음 재료진도와 지시주편손질방법을 조회하여 이동조건을 구한다.
							jrParam.setField("SLAB_NO", s_STL_NO);
						    rsResult3 = commDao.select(jrParam, getNextProcCd, logId, methodNm, "다음 재료진도와 지시주편손질방법을 조회");

						    if (rsResult3.size() > 0)
						    {
						    	sMoveterm = yfComm.getStockMoveTerm(rsResult3.getRecord(0).getFieldString("NEXT_PROC_CD"), rsResult3.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD"));
						    }
						}

						jrParam.setField("STL_NO",	s_STL_NO);
						rsResult2 = commDao.select(jrParam, getStockInfoWcrGp, logId, methodNm, "TB_YF_STOCK에 존재하는지 체크");

						if (rsResult2.size() <= 0)
						{
							//TB_YF_STOCK 에 STL_NO가 존재하지 않을 경우 TB_YF_STOCK에 STL_NO를 등록한다.
							/**********************************************************
							* 1-1-2-1. TB_YF_STOCK에 STL_NO를 신규생성
							**********************************************************/
							jrParam.setField("STL_NO",			s_STL_NO);
							jrParam.setField("STOCK_MOVE_TERM",	sMoveterm);	//저장품 이동 조건
							jrParam.setField("MODIFIER",		modifier);

							commDao.insert(jrParam, insStockInfo, logId, methodNm, "TB_YF_STOCK에 STL_NO를 신규생성");

							/**********************************************************
							* 1-1-2-2. 저장품제원정보(YFF0L002)  L2 송신	//박판SLAB는  L2가 없음...
							**********************************************************/
//							jrParam.setField("STL_NO",			s_STL_NO);	//재료번호(SLAB번호)
//							jrParam.setField("MSG_GP",			"I");		//정보구분(I:신규)
//							jrParam.setField("YD_INFO_SYNC_CD",	"A");		//야드정보동기화코드(A:생산실적)
//
//							jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF0L002", jrParam));
						}
						else
						{
							//TB_YF_STOCK 에 STL_NO가 존재하는 경우..
							//작업예약재료가 존재하면 에러처리
							if (!"".equals(commUtils.trim(rsResult2.getRecord(0).getFieldString("YD_WBOOK_ID"))))
							{
								throw new Exception("STL_NO : " + s_STL_NO + " 가 작업예약에 이미 잡혀 있습니다!!! YD_WBOOK_ID : " + rsResult2.getRecord(0).getFieldString("YD_WBOOK_ID"));
							}

							/**********************************************************
							* 1-1-2-3. TB_YF_STOCK의 STL_NO에 저장품 이동 조건 갱신
							**********************************************************/
							jrParam.setField("STL_NO",			s_STL_NO);
							jrParam.setField("STOCK_MOVE_TERM",	sMoveterm); //저장품 이동 조건
							jrParam.setField("MODIFIER",		modifier);

							commDao.update(jrParam, updStockMoveTerm, logId, methodNm, "TB_YF_STOCK의 STL_NO에 저장품 이동 조건 갱신");
						}
					}

					/**********************************************************
					* 1-1-4. TB_YD_CARSCH 차량스케쥴 하차출발(A)로 UPDATE
					**********************************************************/
					szMsg="차량스케쥴 하차출발(A)로 UPDATE < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					jrParam.setField("YD_CAR_PROG_STAT",		"A");				//차량진행상태 (A:하차출발)
					jrParam.setField("YD_CAR_USE_GP",			"L");				//야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT",			"L");				//야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("ARR_WLOC_CD",				szARR_WLOC_CD);		//착지개소코드
					jrParam.setField("YD_PNT_CD",				sunloadStopTsCd);	//야드포인트코드(착지)
					jrParam.setField("YD_CARUD_STOP_LOC",		sunloadStoppoint);	//야드하차정지위치
					jrParam.setField("YD_CARUD_WRK_BOOK_ID",	ydWbookId);			//야드하차작업예약ID
					jrParam.setField("WAIT_ARR_GP",				s_STACK_BAY_GP);	//이송목적동
					jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드
					commDao.update(jrParam, updCarSchUdByTrnEqpCdNew, logId, methodNm, "차량스케쥴 하차출발(A) 업데이트 ");

					if (!"0000".equals(sunloadStopTsCd))
					{
						/**********************************************************
						* 1-1-5. TB_YF_STKCOL 하차정지위치에 예약하기
						**********************************************************/
						szMsg=" 하차포인트 : " + sunloadStopTsCd +", 야드하차위치 : " + sunloadStoppoint + " 에 예약 < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						//TB_YF_STKCOL 예약정보등록
						jrParam.setField("YD_STK_STAT",		"L");	//L:상차작업상태
						jrParam.setField("CAR_CARD_NO",		szTRN_EQP_CD);
						jrParam.setField("YD_STK_COL_GP",	sunloadStoppoint);
						commDao.update(jrParam, updateEquipcolStat, logId, methodNm, "TB_YF_STKCOL 예약정보등록");

						//차량포인트통합관리 - 예약
						this.YfCarPointinforeg("3", "", szTRN_EQP_CD, sunloadStoppoint, "", "", "R", logId, methodNm);
					}
				}
				else if ("E".equals(szTRN_WRK_FULLVOID_GP))
				{
					/**********************************************************
					* 1-2. 운송작업영공구분이 E:공차 인 경우 처리
					**********************************************************/

					szMsg="운송작업영공구분이 E:공차 인 경우 처리 < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					//도착개소코들별 목적동 검색
					if (YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szARR_WLOC_CD))
					{
						//B Cast Slab Yard (D2Y43)
						rsResult = commDao.select(jrParam, getListAimbayBCast, logId, methodNm, "파레트 도착 목적동검색");

						if (rsResult.size() > 0)
						{
							s_STACK_YD_GP = "0"; //B Cast Slab Yard
							s_STACK_BAY_GP1 = commUtils.trim(rsResult.getRecord(0).getFieldString("AIM_BAY"));

							szMsg="["+methodNm+"] BCast 공차 목적동 검색결과 : " + s_STACK_BAY_GP1;
							commUtils.printLog(logId, szMsg, "SL");
						}
					}

					//이송지시대상재 조회
					jrParam.setField("SPOS_WLOC_CD",	szARR_WLOC_CD);
					rsResult = commDao.select(jrParam, getListFrtoStl_ASlab, logId, methodNm, "대상재조회");

					if(rsResult.size() > 0)
					{
						szMsg="야드에 적치된 이송지시대상재가 존재하는 경우 < E:공차 처리 < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						s_STACK_BAY_GP =  commUtils.trim(rsResult.getRecord(0).getFieldString("AIM_BAY")); //대상이 있는 목적동

						//상차정지위치 검색
						jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
						jrParam.setField("YD_GP",	s_STACK_YD_GP);
						jrParam.setField("BAY_GP",	s_STACK_BAY_GP);
						rsResult = commDao.select(jrParam, getListloadStoppoint, logId, methodNm, "A열연 SLAB야드 상차정지위치 조회");

						if(rsResult.size() > 0)
						{
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
							sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
						}
						else
						{
							//포인트 모두 점유상태일때 상차완료된 포인트찾음...2020.01.09 상하차완료 되었어도 포인트 내려주지 않음
//							jrParam.setField("YD_GP",		s_STACK_YD_GP);
//							jrParam.setField("BAY_GP",		s_STACK_BAY_GP);
//							jrParam.setField("SECT_GP ",	"PT");
//							rsResult = commDao.select(jrParam, getListloadEndpoint, logId, methodNm, "포인트 모두 점유상태일때 상차완료된 포인트찾음");
//
//							if (rsResult.size() > 0)
//							{
//								sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
//								sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
//							}
						}

						if (rsResult.size() <= 0 || "".equals(sunloadStopTsCd))
						{
							//포인트찾지 못함
							sunloadStoppoint	= "XXPTXX";
							sunloadStopTsCd		= "0000";

							szYD_MSG_NM = this.getCarMsg("N", s_STACK_YD_GP, s_STACK_BAY_GP, szTRN_EQP_GP, szARR_WLOC_CD, "", logId, methodNm);
						}
					}
					else if(rsResult.size() == 0)
					{
						szMsg="야드에 적치된 이송지시대상재가 없을 경우 < E:공차 처리 < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						//이송상차 동지정 1순위 상차정지위치 검색
						s_STACK_BAY_GP = s_STACK_BAY_GP1;
						jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
						jrParam.setField("YD_GP",	s_STACK_YD_GP);
						jrParam.setField("BAY_GP",	s_STACK_BAY_GP1);
						rsResult = commDao.select(jrParam, getListloadStoppoint, logId, methodNm, "A열연 SLAB야드 상차정지위치 조회");

						if(rsResult.size() > 0)
						{
							sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
							sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
						}
						else
						{
							//포인트 모두 점유상태일때 상차완료된 포인트찾음...2020.01.09 상하차완료 되었어도 포인트 내려주지 않음
//							jrParam.setField("YD_GP",	s_STACK_YD_GP);
//							jrParam.setField("BAY_GP",	s_STACK_BAY_GP1);
//							jrParam.setField("SECT_GP",	"PT");
//							rsResult = commDao.select(jrParam, getListloadEndpoint, logId, methodNm, "포인트 모두 점유상태일때 상차완료된 포인트찾음");
//
//							if (rsResult.size() > 0)
//							{
//								sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
//								sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
//							}
						}

						if("".equals(sunloadStopTsCd))
						{
							//이송상차 동지정 2순위 상차정지위치 검색
							s_STACK_BAY_GP = s_STACK_BAY_GP2;
							jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
							jrParam.setField("YD_GP",	s_STACK_YD_GP);
							jrParam.setField("BAY_GP",	s_STACK_BAY_GP2);
							rsResult = commDao.select(jrParam, getListloadStoppoint, logId, methodNm, "A열연 SLAB야드 상차정지위치 조회");

							if (rsResult.size() > 0)
							{
								sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
								sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							}
							else
							{
								//포인트 모두 점유상태일때 상차완료된 포인트찾음...2020.01.09 상하차완료 되었어도 포인트 내려주지 않음
//								jrParam.setField("YD_GP",	s_STACK_YD_GP);
//								jrParam.setField("BAY_GP",	s_STACK_BAY_GP2);
//								jrParam.setField("SECT_GP",	"PT");
//								rsResult = commDao.select(jrParam, getListloadEndpoint, logId, methodNm, "포인트 모두 점유상태일때 상차완료된 포인트찾음");
//
//								if(rsResult.size() > 0)
//								{
//									sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
//									sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
//								}
							}
						}

						if("".equals(sunloadStopTsCd))
						{
							//이송상차 동지정 3순위 상차정지위치 검색
							s_STACK_BAY_GP = s_STACK_BAY_GP3;
							jrParam.setField("WLOC_CD",	szARR_WLOC_CD);
							jrParam.setField("YD_GP",	s_STACK_YD_GP);
							jrParam.setField("BAY_GP",	s_STACK_BAY_GP3);
							rsResult = commDao.select(jrParam, getListloadStoppoint, logId, methodNm, "A열연 SLAB야드 상차정지위치 조회");

							if (rsResult.size() > 0)
							{
								sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
								sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
							}
							else
							{
								//포인트 모두 점유상태일때 상차완료된 포인트찾음...2020.01.09 상하차완료 되었어도 포인트 내려주지 않음
//								jrParam.setField("YD_GP",	s_STACK_YD_GP);
//								jrParam.setField("BAY_GP",	s_STACK_BAY_GP3);
//								jrParam.setField("SECT_GP",	"PT");
//								rsResult = commDao.select(jrParam, getListloadEndpoint, logId, methodNm, "포인트 모두 점유상태일때 상차완료된 포인트찾음");
//
//								if(rsResult.size() > 0)
//								{
//									sunloadStoppoint 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP"));
//									sunloadStopTsCd 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"));
//								}
							}
						}

						if(rsResult.size() <= 0 || "".equals(sunloadStopTsCd))
						{
							//포인트찾지 못함
							sunloadStoppoint	= "XXPTXX";
							sunloadStopTsCd		= "0000";

							szYD_MSG_NM = this.getCarMsg("N", s_STACK_YD_GP, s_STACK_BAY_GP, szTRN_EQP_GP, szARR_WLOC_CD, "", logId, methodNm);
						}
					}

					if("AA".equals(sWorkGp) && YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szSPOS_WLOC_CD))
					{
						szMsg="AB열연에서 AB열연으로 이송인 경우 발지 차량정보 삭제처리...YF모듈호출 < " + methodNm;
						commUtils.printLog(logId, szMsg, "SL");

						//발지 차량정보 삭제처리
						this.clearSposCarInfo(szSPOS_WLOC_CD, szSPOS_YD_PNT_CD, szTRN_EQP_CD, logId, modifier);	//TB_YF_STKCOL, TB_YF_STKLYR, TB_YD_CARPOINT 초기화
					}

					szMsg=" 상차포인트 : " + sunloadStopTsCd +", 야드상차위치 : " + sunloadStoppoint + " 에 예약";
					commUtils.printLog(logId, szMsg, "SL");

					/**********************************************************
					* TB_YF_STKCOL 예약정보등록
					**********************************************************/
					jrParam.setField("YD_STK_STAT",		"L");	//L:상차작업상태
					jrParam.setField("CAR_CARD_NO",		szTRN_EQP_CD);
					jrParam.setField("YD_STK_COL_GP",	sunloadStoppoint);
					commDao.update(jrParam, updateEquipcolStat, logId, methodNm, "TB_YF_STKCOL 예약정보등록");

					//차량포인트통합관리 - 예약
					this.YfCarPointinforeg("3", "", szTRN_EQP_CD, sunloadStoppoint, "", "", "R", logId, methodNm);

					/**********************************************************
					* 1-2-3. TB_YD_CARSCH 차량스케쥴 상차출발(1)로 INSERT
					**********************************************************/
					szMsg="차량스케쥴 상차출발(1)로 INSERT   < " + methodNm;
					commUtils.printLog(logId, szMsg, "SL");

					jrParam.setField("YD_CAR_SCH_ID",			commDao.getSeqId(logId, methodNm, "CarSch")); //야드차량스케쥴ID
					jrParam.setField("YD_CAR_PROG_STAT",		"1");				//차량진행상태 (1:상차출발)
					jrParam.setField("YD_CAR_USE_GP",			"L");				//야드차량사용구분 (L:구내운송, G:출하차량 )
					jrParam.setField("YD_EQP_WRK_STAT",			"U");				//야드설비작업상태 (L:영차, U:공차)
					jrParam.setField("SPOS_WLOC_CD",			szARR_WLOC_CD);		//발지개소코드(상차지)
					jrParam.setField("ARR_WLOC_CD",				"");				//착지개소코드(하차지)
					jrParam.setField("YD_PNT_CD",				sunloadStopTsCd);	//야드상차포인트코드(발지)
					jrParam.setField("YD_CARLD_WRK_BOOK_ID",	ydWbookId);			//야드상차작업예약ID
					jrParam.setField("YD_CARLD_STOP_LOC",		sunloadStoppoint);	//야드하차정지위치
					jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드
					commDao.insert(jrParam, insCarSchLd, logId, methodNm, "차량스케쥴 상차출발(1)로 INSERT ");
				}

				szMsg="포인트 검색결과 : "+sunloadStopTsCd + " < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				//**********************************************************************************
				//3. 포인트 지시 전송
				jrRtn = commUtils.addSndData(jrRtn, this.makeYDTSJ011(szTRN_EQP_CD,szARR_WLOC_CD, sunloadStopTsCd, szYD_MSG_NM, logId));

			}
			else if ("AC".equals(sWorkGp))
			{
				/**********************************************************
				* 2. AB열연에서 일관제철로 이송인 경우
				**********************************************************/
				szMsg="AB열연에서 일관제철로 이송인 경우 처리 시작  < " + methodNm;
				commUtils.printLog(logId, szMsg, "SL");

				//발지 차량정보 삭제처리
				this.clearSposCarInfo(szSPOS_WLOC_CD ,szSPOS_YD_PNT_CD ,szTRN_EQP_CD ,logId ,modifier);

				sunloadStoppoint = "0" + szSPOS_YD_PNT_CD.substring(1,2) + "PT" + szSPOS_YD_PNT_CD.substring(2,4);

				/**********************************************************
				* 저장위치제원정보 송신 (YFF0L001) --차량출발		//박판SLAB는  L2가 없음...
				**********************************************************/
//				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
//				sndL2Msg.setResultCode(logId);		//Log ID
//				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
//				sndL2Msg.setField("YD_INFO_SYNC_CD",		"4");				//야드정보동기화코드
//				sndL2Msg.setField("YD_STK_COL_GP",			sunloadStoppoint);
//				sndL2Msg.setField("YD_STK_BED_NO",			"01");
//				sndL2Msg.setField("YD_CAR_ARRSTRT_STAT",	"S");				//A:도착, S:출발
//				sndL2Msg.setField("YD_CAR_USE_GP",			"L");				//L:구내운송, G:출하차량
//				sndL2Msg.setField("YD_EQP_WRK_STAT",		sYD_EQP_WRK_STAT);	//U:공차, L:영차
//				sndL2Msg.setField("TRN_EQP_CD",				szTRN_EQP_CD);		//운송장비코드
//				sndL2Msg.setField("YD_CAR_AIM_YD_GP",		"0");
//
//				전송 Data 생성	//박판SLAB는  L2가 없음...
//				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF0L001_CarInfo", sndL2Msg));
			}

			commUtils.printLog(logId, "=============소재차량출발수신처리_A열연Slab 종료========", "SL");

			commUtils.printLog(logId, methodNm, "SL");

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
	 * [A] 오퍼레이션명 : 발지 차량정보 삭제처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord clearSposCarInfo(String szWLOC_CD, String szYD_PNT_CD, String szTRN_EQP_CD, String logId, String modifier) throws DAOException
	{
		String 		methodNm	= "발지 차량정보 삭제처리[YfCommCarMvSeEJB.clearSposCarInfo] ";

	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		JDTORecord	jrTemp		= JDTORecordFactory.getInstance().create(); //임시  JDTORecord

	    try
	    {
			commUtils.printLog(logId, methodNm, "S+");

			//발지 차량정보 삭제 하기 - 적치단 정리
			if(YfConstant.WLOC_CD_A_CC_B_CAST_SLAB_YARD.equals(szWLOC_CD))
			{
				jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"");		//박판SLAB스카핑장은 초기화시 기존상태유지
			}
			else
			{
				jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"C");		//적치 단 활성 상태 C:비활성화
			}

			jrParam.setField("WLOC_CD",					szWLOC_CD);		//개소코드
			jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);	//운송장비코드
			jrParam.setField("MODIFIER",				modifier);
			commDao.update(jrParam, updateLayerstat_02, logId, methodNm, "TB_YF_STKLYR 발지 차량정보 삭제 하기 - 적치단 정리 ");

			//발지 차량정보 삭제 하기 - 적치열 정리
			jrParam.setField("WLOC_CD",		szWLOC_CD);		//발지개소코드
			jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);	//운송장비코드
			jrParam.setField("MODIFIER",	modifier);
			commDao.update(jrParam, updateLayerstat_03, logId, methodNm, "TB_YF_STKCOL 발지 차량정보 삭제 하기 - 적치열 정리 ");

			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			this.YfCarPointinforeg("1", "", szTRN_EQP_CD, "", "", "", "C", logId, methodNm);

			commUtils.printLog(logId, methodNm, "S-");

			return jrTemp;
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
	 * [A] 오퍼레이션명 : 슬라브의 목적동
	 * @param s_CAR_SCH_ID,szSPOS_WLOC_CD
	 * @return String
	 */
	private String getSlabAimCd(String s_CAR_SCH_ID, String szSPOS_WLOC_CD, String logId, String methodNm)
	{
		JDTORecordSet	rsResult	= null;
		JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord
		String 			szMsg		= "<<<< getSlabAimCd 결과 : ";

		try
		{
			String	s_STACK_BAY_GP	= "";

			if("D3Y43".equals(szSPOS_WLOC_CD))
			{
				//B열연 SLAB야드
				jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, getListSposwlocCD, logId, methodNm, "YD_차량스케줄 테이블에서 차량스케줄ID로 발지개소코드 조회");

				if (rsResult.size() > 0)
				{
					szSPOS_WLOC_CD = commUtils.trim(rsResult.getRecord(0).getFieldString("SPOS_WLOC_CD"));
				}
			}

			if("D2Y43".equals(szSPOS_WLOC_CD))
			{
				//박판열연 BCast
				jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, getListAimBay_BCast, logId, methodNm, "B-Cast SLAB야드에서 이송시 목적동 조회");

				if (rsResult.size() > 0)
				{
					s_STACK_BAY_GP = commUtils.nvl(rsResult.getRecord(0).getFieldString("DEST_BAY"),"D");
				}
			}
			else if("DJY25".equals(szSPOS_WLOC_CD)||"DYY15".equals(szSPOS_WLOC_CD)||"BSY01".equals(szSPOS_WLOC_CD)||"BSY02".equals(szSPOS_WLOC_CD)||"BSY03".equals(szSPOS_WLOC_CD))
			{
				//(비상야드추가)
				jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, getListAimBay_Port, logId, methodNm, "통합야드에서 이송시 목적동 조회");

				if (rsResult.size() > 0)
				{
					s_STACK_BAY_GP = commUtils.nvl(rsResult.getRecord(0).getFieldString("DEST_BAY"),"D");
				}
			}
			else if ("C3S01".equals(szSPOS_WLOC_CD))
			{
				//C3스카핑야드
				jrParam.setField("YD_CAR_SCH_ID", s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, getListAimBay_C3Cast, logId, methodNm, "C3야드에서 이송시 목적동 조회");

				if (rsResult.size() > 0)
				{
					s_STACK_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("DEST_BAY"));
				}
			}
			else if ("DHY21".equals(szSPOS_WLOC_CD))
			{
				//C연주
				//해당 차량스케줄에서 WAIT_ARR_GP 항목을 체크한다. (WAIT_ARR_GP 출발실적 발생시 목적동 셋팅)
				//값이 있으면 그 값이 목적동이고 값이 없으면 아래 로직 수행
				jrParam.setField("YD_CAR_SCH_ID",	s_CAR_SCH_ID);
				rsResult = commDao.select(jrParam, getYdCarSchBySchId, logId, methodNm, "차량스케줄id로 차량스케줄정보 조회");

				if (rsResult.size() > 0)
				{
					s_STACK_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("WAIT_ARR_GP"));
				}

				if ("".equals(s_STACK_BAY_GP))
				{
					//먼저 PALLET 조회 화면에서 지정한 목적동이 있는지 확인하여 있으면 그 목적동을 사용한다.
					jrParam.setField("YD_CAR_SCH_ID",	s_CAR_SCH_ID);
					rsResult = commDao.select(jrParam, getListAimBay_CCast2, logId, methodNm, "C연주야드에서 이송시 YD야드적치열의 YD_STKBED_USG_CD 에 PALLET 조회 화면에서 지정한 목적동 조회");

					if (rsResult.size() > 0)
					{
						s_STACK_BAY_GP = commUtils.nvl(rsResult.getRecord(0).getFieldString("DEST_BAY"),"D");
					}
					else
					{
						jrParam.setField("YD_CAR_SCH_ID",	s_CAR_SCH_ID);
						rsResult = commDao.select(jrParam, getListAimBay_CCast, logId, methodNm, "C연주야드에서 이송시 목적동 조회");

						if (rsResult.size() > 0)
						{
							s_STACK_BAY_GP = commUtils.nvl(rsResult.getRecord(0).getFieldString("DEST_BAY"),"D");
						}
					}
				}
			}

			commUtils.printLog(logId, szMsg, "SL");

			return s_STACK_BAY_GP;
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
	 * [A] 오퍼레이션명 : 차량입동지시요구(YFYFJ662) -- 기존 procCarBayInOrdReqNEW (YDYDJ662) 참조
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvYFYFJ662(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "차량입동지시요구[YfCommCarMvSeEJB.rcvYFYFJ662] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

	    	commUtils.printParam(logId + " 차량입동지시요구(YFYFJ662) 수신 ", rcvMsg);

	    	jrRtn = commUtils.addSndData(jrRtn, this.procYFYFJ662(rcvMsg));

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
	 * [A] 오퍼레이션명 : 차량입동지시요구(YFYFJ662) -- 기존 procCarBayInOrdReqNEW (YDYDJ662) 참조
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procYFYFJ662(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "차량입동지시요구[YfCommCarMvSeEJB.procYFYFJ662] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		JDTORecordSet rsResult 	= null;
		JDTORecordSet jsCarSch  = null;
		JDTORecordSet rsResult2 = null;

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

	    	//기본 수신 항목 값
			String msgId    = commUtils.nvl(commUtils.getMsgId(rcvMsg));			//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============차량입동지시요구(YFYFJ662) 시작========", "SL");

			//수신 항목 값
			String	szCAR_KIND        	= commUtils.nvl (rcvMsg.getFieldString("CAR_KIND"), "TR"); 		//야드설비ID
			String	szYD_CARPNT_CD   	= commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"));  		//입동포인트	- 필수항목
			String	szYD_CAR_SCH_ID		= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); 		//차량스케줄ID	- 선택항목
			String	szCHK_YN  			= commUtils.nvl (rcvMsg.getFieldString("CHK_YN"),"Y"); 			//의미 없음
			String	szTRANS_FRTOMOVE_GP	= commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP")); 	//1 운송 2 이송
			String	szCR_FRTOMOVE_GP	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP"));		//냉연이송구분

			String	ydFrmYn				= "";	//차량형상사용유무
			String	dummyYn				= "";	//더미작업여부

			if("".equals(szYD_CARPNT_CD))
			{
				throw new Exception("차량포인트코드 오류 YD_CARPNT_CD : [" + szYD_CARPNT_CD + "] 차량포인트코드 정보가 없습니다.");
			}

			String szYD_GP = szYD_CARPNT_CD.substring(0,1); //차량포인트 첫자리가 야드구분

			String sAPPLY1 = yfComm.ACoilApplyYn("APP003","1","1");
			commUtils.printLog(logId,  "차량ERROR LOG 처리:" + sAPPLY1, "SL");

			//------------------------------------------------------------------------------------------------------------
			// 해당 포인트 입동가능 체크
			//  - 2냉연인 경우는 지정 포인트만 사용가능한지 체크
			//  - 그외 야드는 해당 통로에 포인트가 여러개일 경우 사용가능한 포인트가 있는지 체크
	    	//------------------------------------------------------------------------------------------------------------
			jrParam.setField("YD_CARPNT_CD",	szYD_CARPNT_CD);
			jrParam.setField("CAR_KIND",		szCAR_KIND );

			if ("N".equals(szCHK_YN))
			{
				//하이스코 2냉연 Trailer 일 경우
				rsResult = commDao.select(jrParam, getYdCarPointCHK2, logId, methodNm, "YF입동지시 TB_YD_CARPOINT 조회(냉연)");
			}
			else
			{
				rsResult = commDao.select(jrParam, getYdCarPointCHK, logId, methodNm, "YF입동지시 TB_YD_CARPOINT 조회(내수)");
			}

			if (rsResult.size() <= 0)
			{
				jrParam.setField("YD_CARPNT_CD",	szYD_CARPNT_CD);
				jrParam.setField("YD_CAR_SCH_ID",	szYD_CAR_SCH_ID);
				JDTORecordSet rsResultChk = commDao.select(jrParam, getCarPointCheck, logId, methodNm, "차량 입동여부 확인");

				if("1".equals(rsResultChk.getRecord(0).getFieldString("CT")))
				{
					commUtils.printLog(logId, "이미 해당 포인트["+szYD_CARPNT_CD+"]에  해당 차량스케줄[" + szYD_CAR_SCH_ID + "]의 차량이 입동되어있습니다." , "SL");

					return jrRtn;
				}

				/*******************
				 * 입동불가
				 *******************/
				commUtils.printLog(logId, "해당 포인트["+szYD_CARPNT_CD+"]에  입동이 불가능 합니다.", "SL");

		    	//차량스케줄 조회
				jrParam.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				jsCarSch = commDao.select(jrParam, getYdCarsch, logId, methodNm, "TB_YD_CARSCH 조회");

				if (jsCarSch.size() > 0)
				{
					//-------------------------------------------------------------------------------------------------------
			    	//	입동대기TC 전송
			    	//-------------------------------------------------------------------------------------------------------
					JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
					jrMsg.setResultCode(logId);		//Log ID
					jrMsg.setResultMsg(methodNm);	//Log Method Name

					if ("P".equals(commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE"))))	//운송장비타입 P : PDA...냉연출하차량이 P로 셋팅됨
					{
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						JDTORecord recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("TRANS_ORD_DATE",  commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE")));
						recPara.setField("TRANS_ORD_SEQNO", commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));

						// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB*/
						//YdStockDao ydStockDao  = new YdStockDao();
						//int nRet = ydStockDao.getYdStock(recPara, rsResult2, 731);
						int nRet = commDao.getYdStock(recPara, rsResult2, 731);

						if (nRet > 0)
						{
							rsResult2.first();
							JDTORecord recTemp	= rsResult2.getRecord();
							szCR_FRTOMOVE_GP	= StringHelper.evl(recTemp.getFieldString("CR_FRTOMOVE_GP") , "");	//냉연이송구분
						}

						jrMsg.setField("JMS_TC_CD",				"YDDMR070");
						jrMsg.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
						jrMsg.setField("TC_CODE",				"YDDMR070");
						jrMsg.setField("TC_CREATE_DDTT",		commUtils.getDateTime14());
						jrMsg.setField("CR_FRTOMOVE_GP",		szCR_FRTOMOVE_GP);
					}
					else
					{
						jrMsg.setField("JMS_TC_CD",				"YDDMR028");
						jrMsg.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
						jrMsg.setField("TC_CODE",				"YDDMR028");
						jrMsg.setField("TC_CREATE_DDTT",		commUtils.getDateTime14());
					}

					jrMsg.setField("TRANS_WORD_DATE",		commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE")));
					jrMsg.setField("TRANS_WORD_SEQNO",		commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
					jrMsg.setField("CARD_NO",				commUtils.trim(jsCarSch.getRecord(0).getFieldString("CARD_NO")));
					jrMsg.setField("CAR_NO",				commUtils.trim(jsCarSch.getRecord(0).getFieldString("CAR_NO")));
					jrMsg.setField("BAYIN_DDTT",			commUtils.getDateTime14());
					jrMsg.setField("WLOC_CD",				"");
					jrMsg.setField("YD_PNT_CD",				"");
					jrMsg.setField("YD_CARPNT_CD",			"");
					jrMsg.setField("LOAN_PULLOUT_ABLE_YN",	"N");

					jrRtn = commUtils.addSndData(jrRtn, jrMsg);
				}
				
				return jrRtn;
			}
			else
			{
				/*******************
				 * 입동가능
				 *******************/
				String szOLD_YD_CARPNT_CD = szYD_CARPNT_CD;	//입동포인트

		    	//------------------------------------------------------------------------------------------------------------
		    	//	해당 포인트 입동대상 차량스케줄 조회(좌우 통로 같이 검색)
		    	//------------------------------------------------------------------------------------------------------------
				jsCarSch = commDao.select(jrParam, getYdCarPointCarSchSelect, logId, methodNm, "TB_YD_CARSCH 조회");

				if (jsCarSch.size() <= 0)
				{
					commUtils.printLog(logId, "해당 포인트 ["+szYD_CARPNT_CD+"] 입동대상 차량스케줄이 없습니다.", "SL");
					jrRtn.setField("RTN_CD"	, "-1");

					return jrRtn;
				}

				jsCarSch.first();
				JDTORecord jrCarSch = jsCarSch.getRecord();

				String newYdCarSchId    = commUtils.nvl(jrCarSch.getFieldString("YD_CAR_SCH_ID"),"");
				String newYdCarKind		= commUtils.nvl(jrCarSch.getFieldString("CAR_KIND"),"TR");
				String newYdCarNo		= commUtils.nvl(jrCarSch.getFieldString("CAR_NO"),"");
				String newYdCardNo		= commUtils.nvl(jrCarSch.getFieldString("CARD_NO"),"");
				String newTrnEqpCd	   	= commUtils.nvl(jrCarSch.getFieldString("TRN_EQP_CD"),"");
				String newTransOrdDate 	= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_DATE"),"");
				String newTransOrdSeqNo	= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_SEQNO"),"");
				String ydCarWrkGp		= commUtils.nvl(jrCarSch.getFieldString("YD_CAR_WRK_GP"),"");
				String newYdCarPntCd    = commUtils.nvl(jrCarSch.getFieldString("YD_CARPNT_CD"),"");
				String newYdStackColGp  = commUtils.nvl(jrCarSch.getFieldString("YD_STK_COL_GP"),"");
				String newYdPntCd   	= commUtils.nvl(jrCarSch.getFieldString("YD_PNT_CD"),"");
				szYD_CARPNT_CD    	   	= commUtils.nvl(jrCarSch.getFieldString("OLD_YD_CARPNT_CD"),"");
				String ydCarldStopLoc   = commUtils.nvl(jrCarSch.getFieldString("OLD_YD_STK_COL_GP"),"");
				String ydPntCd    		= commUtils.nvl(jrCarSch.getFieldString("YD_PNT_CD1"),"");
				String newWlocCd   		= commUtils.nvl(jrCarSch.getFieldString("WLOC_CD"),"");
				String transEquipType	= commUtils.nvl(jrCarSch.getFieldString("TRANS_EQUIPMENT_TYPE"),"");
				String ydEqpWrkStat		= commUtils.nvl(jrCarSch.getFieldString("YD_EQP_WRK_STAT"),"");
				String ydCarProgStat	= commUtils.nvl(jrCarSch.getFieldString("YD_CAR_PROG_STAT"),"");

				//------------------------------------------------------------------------------------------------------------
				//	도착포인트 동에 야드 적치 여부 체크...동에 없으면 종료
				//------------------------------------------------------------------------------------------------------------
			    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
			    JDTORecordSet jsCarSchLoc = commDao.select(jrParam, getYdCarStlYardInfoChk, logId, methodNm, "도착포인트 동에 야드 적치 여부 체크 조회");

				if ( "0".equals(jsCarSchLoc.getRecord(0).getFieldString("CT")) && ("1".equals(ydCarProgStat) || "2".equals(ydCarProgStat)) )
				{
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+szYD_CARPNT_CD+"]동에 상차가능 대상이 존재 안합니다.★★★★★★", "SL");

					if ("Y".equals(sAPPLY1))
					{
						/***** 차량 log ****/
						JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
						jrLogMsg.setResultCode(logId);	//Log ID
						jrLogMsg.setResultMsg(methodNm);	//Log Method Name
						jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
						jrLogMsg.setField("YD_MSG_NM",		"[해당 포인트 ["+szYD_CARPNT_CD+"]동에 상차대상재료가 없습니다.: 대상재위치 확인바랍니다. "); //메세지
						jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴

						//EJBConnector ejbConnLog = new EJBConnector("default", "YfCommBakSeEJB", this);
						//ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
						this.updCarErrorLog(jrLogMsg);
					}

					return jrRtn;
				}

				//------------------------------------------------------------------------------------------------------------
				//	도착포인트 TB_YD_CARPOINT 스판 범위안에 야드 적치 여부 체크...범위에 없으면 종료
				//------------------------------------------------------------------------------------------------------------
				jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
			    jsCarSchLoc = commDao.select(jrParam, getYdCarStlYardInfoChk2, logId, methodNm, "도착포인트 TB_YD_CARPOINT 스판 범위안에 야드 적치 여부 체크 조회");

			    if ("0".equals(jsCarSchLoc.getRecord(0).getFieldString("CT")) && ("1".equals(ydCarProgStat) || "2".equals(ydCarProgStat)))
				{
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+szYD_CARPNT_CD+"]동의 SPAN영역에 상차가능 대상이 존재 안합니다.★★★★★★", "SL");

					if ("Y".equals(sAPPLY1))
					{
						/***** 차량 log ****/
						JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
						jrLogMsg.setResultCode(logId);	//Log ID
						jrLogMsg.setResultMsg(methodNm);	//Log Method Name
						jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
						jrLogMsg.setField("YD_MSG_NM",		"해당 동의 SPAN영역에 상차대상재료가 없습니다."); //메세지
						jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴

						//EJBConnector ejbConnLog = new EJBConnector("default", "YfCommBakSeEJB", this);
						//ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
						this.updCarErrorLog(jrLogMsg);
					}

					return jrRtn;
				}

				//------------------------------------------------------------------------------------------------------------
				//	종료안된 크레인스케줄이 있는지 체크...존재하면 종료
				//------------------------------------------------------------------------------------------------------------
			    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
			    jsCarSchLoc = commDao.select(jrParam, getYdCarStlYardInfoChk3, logId, methodNm, "대상재가 종료안된 크레인스케줄이 있는지 체크 조회");

			    if (jsCarSchLoc.size() > 0)
				{
					commUtils.printLog(logId, "TB_YD_CARSCH["+newYdCarSchId+"]에 대상재가 크레인작업 중입니다.★★★★★★", "SL");

					if ("Y".equals(sAPPLY1))
					{
						/***** 차량 log ****/
						JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
						jrLogMsg.setResultCode(logId);	//Log ID
						jrLogMsg.setResultMsg(methodNm);	//Log Method Name
						jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
						jrLogMsg.setField("YD_MSG_NM",		"출하대상재가 스케줄작업 중입니다."); //메세지
						jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴

						//EJBConnector ejbConnLog = new EJBConnector("default", "YfCommBakSeEJB", this);
						//ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
						this.updCarErrorLog(jrLogMsg);
					}

					return jrRtn;
				}
			    
			    //------------------------------------------------------------------------------------------------------------
				//	대상재가 작업예약이 있는지 체크...존재하면 삭제처리 후 진행
				//------------------------------------------------------------------------------------------------------------
			    jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId);
			    jsCarSchLoc = commDao.select(jrParam, getYdCarStlYardInfoChk4, logId, methodNm, "대상재가 작업예약이 있는지 체크 조회");
			    
			    if (jsCarSchLoc.size() > 0)
				{
					commUtils.printLog(logId, "TB_YD_CARSCH["+newYdCarSchId+"]에 대상재에 작업예약이 있습니다.★★★★★★", "SL");
					commUtils.printLog(logId, "TB_YD_CARSCH["+newYdCarSchId+"]에 대상재에 작업예약 삭제 후 계속 진행★★★★★★", "SL");
					
					for(int i2=0; i2 < jsCarSchLoc.size(); i2++)
					{
						jrParam.setField("YD_WBOOK_ID", jsCarSchLoc.getRecord(i2).getFieldString("YD_WBOOK_ID"));

						commDao.update(jrParam, updDelYnWrkBookMtl, logId, methodNm, "TB_YF_WRKBOOKMTL");	//작업예약재료 삭제

						commDao.update(jrParam, updDelYnWrkBook, logId, methodNm, "TB_YF_WRKBOOK");			//작업예약 삭제					}
					}
				}

				//------------------------------------------------------------------------------------------------------------
		    	//	1.차량포인트 입동 점유 , 2.차량상태 도착처리
		    	//------------------------------------------------------------------------------------------------------------
				commUtils.printLog(logId, "변경 포인트"+ydCarWrkGp+": ["+newYdCarPntCd+"] 이전포인트 ["+szYD_CARPNT_CD+"]******* "+ydEqpWrkStat, "SL");
				commUtils.printLog(logId, "변경 차상위치 ["+newYdStackColGp+"] 이전차상위치["+ydCarldStopLoc+"]******* " + newYdCarNo, "SL");
				
				//2냉연인경우 기존 차상위치와 포인트로 도착 처리를 한다...냉연차량도 동일동 동일통로인 경우 좌우 구분없이 입동지시에 들어갈수있도록 수정함
//				if ("9".equals(ydCarWrkGp) && "TR".equals(newYdCarKind))
//				{
//					commUtils.printLog(logId, "2냉연 TR 요구한 포인트와 틀린 경우 skip:"+szOLD_YD_CARPNT_CD+","+szYD_CARPNT_CD, "SL");
//
//					//입력 차량포인트 와 틀린 경우 return
//					if (!szOLD_YD_CARPNT_CD.equals(szYD_CARPNT_CD))
//					{
//						if ("Y".equals(sAPPLY1))
//						{
//							/***** 차량 log ****/
//							JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
//							jrLogMsg.setResultCode(logId);		//Log ID
//							jrLogMsg.setResultMsg(methodNm);	//Log Method Name
//							jrLogMsg.setField("MODIFIER",		modifier); //수정자 셋팅
//							jrLogMsg.setField("YD_MSG_NM",		"요구  차량포인트:"+ szYD_CARPNT_CD + "/기존포인트:" + szOLD_YD_CARPNT_CD + " 와 틀립니다."); //메세지
//							jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId); //차량스케쥴
//
//							//EJBConnector ejbConnLog = new EJBConnector("default", "YmCommBakSeEJB", this);
//							//ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
//							this.updCarErrorLog(jrLogMsg);
//						}
//
//						return jrRtn;
//					}
//				}

				//2냉연인경우 기존 차상위치와 포인트로 도착 처리를 한다...냉연차량도 동일동 동일통로인 경우 좌우 구분없이 입동지시에 들어갈수있도록 수정함
//			    if ("9".equals(ydCarWrkGp))
//			    {
//			    	newYdCarPntCd	= szYD_CARPNT_CD ;
//			    	newYdStackColGp	= ydCarldStopLoc;
//			    	newYdPntCd		= ydPntCd;
//			    }

				// 반입 /회송은 기존 화면에서 도착처리한 차상위치와 포인트로만 도착 처리를 한다.
			    int intNewTransOrdSeqNo = StringHelper.parseInt(newTransOrdSeqNo, 0);

			    if(intNewTransOrdSeqNo > 999000)
			 	{
			 		newYdCarPntCd	= szYD_CARPNT_CD;
			    	newYdStackColGp	= ydCarldStopLoc;
			    	newYdPntCd		= ydPntCd;
			 	}
			    
				if (intNewTransOrdSeqNo > 999000)
				{
					commUtils.printLog(logId, "포인트와 틀린 경우 skip:"+szOLD_YD_CARPNT_CD+","+szYD_CARPNT_CD, "SL");

					//입력 차량포인트 와 틀린 경우 return
					if (!szOLD_YD_CARPNT_CD.equals(szYD_CARPNT_CD))
					{
						commUtils.printLog(logId, "요구  차량포인트:"+ szYD_CARPNT_CD + "/기존포인트:" + szOLD_YD_CARPNT_CD + " 가 틀립니다.", "SL");
						jrRtn.setField("RTN_CD"	, "-1");

				 		return jrRtn;
					}
				}

			 	//------------------------------------------------------------------------------------------------------------
			 	//	작업 대상재 CHECK
			 	//------------------------------------------------------------------------------------------------------------
			    if ("9".equals(ydCarWrkGp) && "TR".equals(newYdCarKind))
			    {
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+szYD_CARPNT_CD+"] 2냉연 TR은 크레인 작업예약은 도착전문을 받고서 처리 한다.", "SL");
				}
			    else
			    {
					JDTORecord jrInTemp1 = JDTORecordFactory.getInstance().create();
					jrInTemp1.setResultCode(logId);		//Log ID
				    jrInTemp1.setResultMsg(methodNm);	//Log Method Name
					jrInTemp1.setField("MODIFIER",			modifier); //수정자
					jrInTemp1.setField("TRANS_WORD_DATE",	newTransOrdDate);
					jrInTemp1.setField("TRANS_WORD_SEQNO",	newTransOrdSeqNo);
					jrInTemp1.setField("CAR_NO",			newYdCarNo);
					jrInTemp1.setField("CARD_NO",			newYdCardNo);
					jrInTemp1.setField("YD_GP",				newYdStackColGp.substring(0, 1));
					jrInTemp1.setField("YD_BAY_GP",			newYdCarPntCd.substring(2, 3)); //동정보
					jrInTemp1.setField("YD_CARPNT_CD",		newYdCarPntCd);
					jrInTemp1.setField("YD_STK_COL_GP",		newYdStackColGp);
					jrInTemp1.setField("CAR_KIND",			szCAR_KIND);
					jrInTemp1.setField("TRANS_FRTOMOVE_GP",	szTRANS_FRTOMOVE_GP);
					jrInTemp1.setField("YD_EQP_WRK_STAT",	ydEqpWrkStat);
					jrInTemp1.setField("YD_CAR_SCH_ID",		newYdCarSchId);

					//작업 대상재 미리 CHECK
					JDTORecord jrOutCheck = this.procYfWbookInsertCheck(jrInTemp1);

					String sStockStat	= commUtils.trim(jrOutCheck.getFieldString("STAT"));
					String sYdMsg		= commUtils.trim(jrOutCheck.getFieldString("YD_MSG"));

					if (!"1".equals(sStockStat))
					{
						if ("Y".equals(sAPPLY1))
						{
							/***** 차량 log ****/
							JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
							jrLogMsg.setResultCode(logId);		//Log ID
							jrLogMsg.setResultMsg(methodNm);	//Log Method Name
							jrLogMsg.setField("MODIFIER",		modifier);		//수정자 셋팅
							jrLogMsg.setField("YD_MSG_NM",		sYdMsg); 		//메세지
							jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId);	//차량스케쥴

							//EJBConnector ejbConnLog = new EJBConnector("default", "YmCommBakSeEJB", this);
							//ejbConnLog.trx("updCarErrorLog", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
							this.updCarErrorLog(jrLogMsg);
						}

						return jrRtn;
					}
				}
			    
			    JDTORecord jrInTemp = JDTORecordFactory.getInstance().create();
			    jrInTemp.setResultCode(logId);		//Log ID
			    jrInTemp.setResultMsg(methodNm);	//Log Method Name
				jrInTemp.setField("MODIFIER",			modifier  );	//수정자
				jrInTemp.setField("TRN_EQP_CD",			newTrnEqpCd);
				jrInTemp.setField("CAR_NO",				newYdCarNo);
				jrInTemp.setField("CARD_NO",			newYdCardNo);
				jrInTemp.setField("YD_MAKECARPNT_CD",	newYdCarPntCd);

				//------------------------------------------------------------------------------------------------------------
				//	차량 POINT TABLE 점유상태
				//------------------------------------------------------------------------------------------------------------
			    EJBConnector ejbConn = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
			    ejbConn.trx("procUpdYdTransOrdChange", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });

				//------------------------------------------------------------------------------------------------------------
		    	//	차량스케줄 도착상태 변경 처리
		    	//------------------------------------------------------------------------------------------------------------
			    jrInTemp = JDTORecordFactory.getInstance().create();
			    jrInTemp.setResultCode(logId);		//Log ID
			    jrInTemp.setResultMsg(methodNm);	//Log Method Name
				jrInTemp.setField("MODIFIER",		modifier);		//수정자
				jrInTemp.setField("YD_CAR_SCH_ID",	newYdCarSchId);

				if ("L".equals(ydEqpWrkStat))
				{
					jrInTemp.setField("YD_CARUD_ARR_DT",	commUtils.getDateTime14());
					jrInTemp.setField("YD_CAR_PROG_STAT",	"B");	//하차도착상태
					
					jrInTemp.setField("ARR_WLOC_CD",		newWlocCd);	//착지개소코드
					jrInTemp.setField("YD_PNT_CD3",			newYdPntCd);
					jrInTemp.setField("YD_CARUD_STOP_LOC",	newYdStackColGp);
				}
				else
				{
					jrInTemp.setField("YD_CARLD_ARR_DT",	commUtils.getDateTime14());
					jrInTemp.setField("YD_CAR_PROG_STAT",	"2");	//상차도착상태
					
					jrInTemp.setField("SPOS_WLOC_CD",		newWlocCd);	//발지개소코드
					jrInTemp.setField("YD_PNT_CD1",			newYdPntCd);
					jrInTemp.setField("YD_CARLD_STOP_LOC",	newYdStackColGp);
				}

				commDao.update(jrInTemp, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 상태변경");

				jrInTemp = JDTORecordFactory.getInstance().create();
				jrInTemp.setResultCode(logId);		//Log ID
			    jrInTemp.setResultMsg(methodNm);	//Log Method Name
				jrInTemp.setField("MODIFIER",			modifier);			//수정자
				jrInTemp.setField("YD_STK_COL_GP",		newYdStackColGp);	//YD_STK_COL_GP
				jrInTemp.setField("CARD_NO",			newYdCardNo);
				jrInTemp.setField("CAR_NO",				newYdCarNo);
				jrInTemp.setField("TRN_EQP_CD",			newTrnEqpCd);
				jrInTemp.setField("YD_GP",				newYdStackColGp.substring(0, 1));
				jrInTemp.setField("TRANS_WORD_DATE",	newTransOrdDate);
				jrInTemp.setField("TRANS_WORD_SEQNO",	newTransOrdSeqNo);
				jrInTemp.setField("YD_EQP_WRK_STAT",	ydEqpWrkStat);		//'L': 하차작업,'U':상차적업

				//------------------------------------------------------------------------------------------------------------
				// YF 차량  STOCK 저장품 현위치 등록
				// 출하 MAP 활성화
				//------------------------------------------------------------------------------------------------------------
			    EJBConnector ejbConn9 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
			    ejbConn9.trx("procUpdYfStock", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });	//Yf차량 STOCK 저장품 현위치 등록

				EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
				ejbConn1.trx("procYfLayerOpen", new Class[] { JDTORecord.class }, new Object[] { jrInTemp });	//맵 활성화

	 			String ydWbookId	= "";
	 			String ydEqpId   	= "";
	 			String ydSchCd   	= "";

				//------------------------------------------------------------------------------------------------------------
		    	//	크레인 작업예약 스케줄 생성
		    	//------------------------------------------------------------------------------------------------------------
	 			if ("9".equals(ydCarWrkGp) && "TR".equals(newYdCarKind))
	 			{
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+szYD_CARPNT_CD+"] 2냉연 TR은 크레인 작업예약은 도착전문을 받고서 처리 한다.", "SL");
				}
	 			else
	 			{
					jrInTemp = JDTORecordFactory.getInstance().create();
					jrInTemp.setResultCode(logId);		//Log ID
				    jrInTemp.setResultMsg(methodNm);	//Log Method Name
					jrInTemp.setField("MODIFIER",			modifier  ); 	//수정자
					jrInTemp.setField("TRANS_WORD_DATE", 	newTransOrdDate);
					jrInTemp.setField("TRANS_WORD_SEQNO",	newTransOrdSeqNo);
					jrInTemp.setField("CAR_NO",				newYdCarNo);
					jrInTemp.setField("CARD_NO",			newYdCardNo);
					jrInTemp.setField("YD_GP",				newYdStackColGp.substring(0, 1));	//야드정보
					jrInTemp.setField("YD_BAY_GP",			newYdCarPntCd.substring(2, 3));		//동정보
					jrInTemp.setField("YD_CARPNT_CD",		newYdCarPntCd);
					jrInTemp.setField("YD_STK_COL_GP",		newYdStackColGp);
					jrInTemp.setField("CAR_KIND",			szCAR_KIND);
					jrInTemp.setField("TRANS_FRTOMOVE_GP",	szTRANS_FRTOMOVE_GP);
					jrInTemp.setField("YD_EQP_WRK_STAT",	ydEqpWrkStat);
					jrInTemp.setField("YD_CAR_SCH_ID",		newYdCarSchId);

					JDTORecord jrOutPara = this.procYfWbookInsert(jrInTemp);	//TB_YF_WRKBOOK 및 TB_YF_WRKBOOKMTL 생성

		 			ydWbookId	= commUtils.trim(jrOutPara.getFieldString("YD_WBOOK_ID"));
		 			ydEqpId   	= commUtils.trim(jrOutPara.getFieldString("YD_EQP_ID"));
		 			ydSchCd   	= commUtils.trim(jrOutPara.getFieldString("YD_SCH_CD"));

					//------------------------------------------------------------------------------------------------------------
			    	//	차량스케줄 도착상태 변경 처리
			    	//------------------------------------------------------------------------------------------------------------
		 			jrInTemp = JDTORecordFactory.getInstance().create();
		 			jrInTemp.setResultCode(logId);		//Log ID
				    jrInTemp.setResultMsg(methodNm);	//Log Method Name
					jrInTemp.setField("MODIFIER",				modifier);		//수정자
					jrInTemp.setField("YD_CAR_SCH_ID",			newYdCarSchId);
		 			jrInTemp.setField("YD_CARLD_WRK_BOOK_ID",	ydWbookId);
		 			jrInTemp.setField("YD_CARLD_ARR_DT",		commUtils.getDateTime14());

					if ("P".equals(transEquipType) && "9".equals(ydCarWrkGp))
					{
						jrInTemp.setField("YD_CAR_PROG_STAT",	"B");	//하차도착상태
					}
					else
					{
						int intTransOrdSeqNo = StringHelper.parseInt(newTransOrdSeqNo, 0);

						if (intTransOrdSeqNo > 999000)
						{
							//반품,회송,부분하차
							jrInTemp.setField("YD_CAR_PROG_STAT",		"B" );		//하차도착상태
							jrInTemp.setField("YD_CARUD_WRK_BOOK_ID",	ydWbookId);	//야드하차작업예약ID
							jrInTemp.setField("YD_CARLD_WRK_BOOK_ID",	""); 		//상단에 설정한 상차작업예약ID clear
							jrInTemp.setField("YD_CARUD_ARR_DT",		commUtils.getDateTime14()); //야드하차도착일시
						}
						else
						{
							jrInTemp.setField("YD_CAR_PROG_STAT",		"2");		//상차도착상태
						}
					}

					jrInTemp.setField("SPOS_WLOC_CD",	newWlocCd);		//발지개소코드
					commDao.update(jrInTemp, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 상차도착");

				}

				//------------------------------------------------------------------------------------------------------------
		    	//	재료정보 조회 (2단,1단 순)
		    	//------------------------------------------------------------------------------------------------------------
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				jrParam1.setResultCode(logId);		//Log ID
	 			jrParam1.setResultMsg(methodNm);	//Log Method Name
				jrParam1.setField("TRANS_ORD_DATE",		newTransOrdDate);
				jrParam1.setField("TRANS_ORD_SEQNO",	newTransOrdSeqNo);
				JDTORecordSet jsCarMtl = commDao.select(jrParam1, getYfStockOfCarLoad, logId, methodNm, "재료정보 조회");

				if("9".equals(ydCarWrkGp) && "TR".equals(newYdCarKind))
				{
					commUtils.printLog(logId, "TB_YD_CARSCH[해당 포인트 ["+szYD_CARPNT_CD+"] 2냉연 TR은 크레인 스케줄을 도착전문을 받고서 처리 한다.", "SL");

					szCHK_YN = "H";
				}
				else
				{
					//------------------------------------------------------------------------------------------------------------
			    	//	차량 크레인스케줄 호출(크레인 스케줄 , TO위치 결정 )
			    	//------------------------------------------------------------------------------------------------------------
					String szMsg= "차량스케줄ID[" + newYdCarSchId + "]의 출하차량[차량번호:" + newYdCarNo + ", 카드번호:" + newYdCardNo + ", 운송지시일자:" + newTransOrdDate + ", 운송지시순번:" + newTransOrdSeqNo + "]에 대해 차량도착 전문전송 시작";
					commUtils.printLog(logId, szMsg, "SL");

					/**********************************************************
					* Crane스케줄 호출
					*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
					*  - 차량형상 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
					*  - 더미작업여부가 Y일 경우 차량형상 시스템이 있더라도 스케줄 기동
					**********************************************************/
					jrParam.setField("YD_STK_COL_GP",	newYdStackColGp);
					JDTORecordSet rsResult3 = commDao.select(jrParam, getCarPntFrmYn,  logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
					JDTORecordSet rsResult4	= commDao.select(jrParam, getCarPntFrmYn2, logId, methodNm, "상차 주작업대상재의 더미작업여부 확인 ");

					commUtils.printLog(logId, "차량형상 시스템 사용 여부: " + rsResult3.getRecord(0).getFieldString("YD_FRM_YN"), "SL");
					commUtils.printLog(logId, "더미작업여부 : " + rsResult4.getRecord(0).getFieldString("DUMMY_YN"), "SL");

					ydFrmYn = rsResult3.getRecord(0).getFieldString("YD_FRM_YN");
					dummyYn	= rsResult4.getRecord(0).getFieldString("DUMMY_YN");

					//2020.02.25 정종균과장요청 형상유무 사용인 경우에만 차량예정정보 송신
					//if("Y".equals(ydFrmYn))
					//{
						//차량예정정보 송신
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
			 			jrParam.setField("MODIFIER",		modifier);		//수정자
						jrParam.setField("YD_CAR_SCH_ID",	newYdCarSchId); //야드차량스케쥴ID
						jrParam.setField("SEARCH_FLAG",		"2");			//1:상차도, 2:차량스케쥴 ID
						jrRtn = commUtils.addSndData(jrRtn, yfComm.procCarPlanInfo(jrParam));	//YFF1L008 생성
					//}
					
					//차량형상사용 'N' 또는 더미작업유무 'Y'인 경우 크레인 스케줄 기동
					if("N".equals(ydFrmYn) || "Y".equals(dummyYn))
					{
						//차량형상 사용안하면 크레인 스케줄 기동
						if (YfConstant.YD_GP_1.equals(szYD_GP))
						{
							JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();

							//크레인 스케줄 기동 YFYFJ303 호출
							jrCrnSchMsg.setField("JMS_TC_CD",			"YFYFJ303");
							jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
							jrCrnSchMsg.setField("YD_SCH_CD",			""); //야드스케쥴코드
							jrCrnSchMsg.setField("YD_EQP_ID",			""); //야드설비ID

							int pcnt = 0;

							//2단 적치된 대상
							for(int i = 0; i < jsCarMtl.size(); i++)
							{
								if ("02".equals(jsCarMtl.getRecord(i).getFieldString("YD_STK_LYR_NO")))
								{
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								}
							}

							//1단 적치된 대상
							for(int i = 0; i < jsCarMtl.size(); i++)
							{
								if ("01".equals(jsCarMtl.getRecord(i).getFieldString("YD_STK_LYR_NO")))
								{
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								}
							}

							jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));

							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						}
					}

					szCHK_YN = "Y";

					szMsg= "차량스케줄ID[" + newYdCarSchId + "]의 출하차량[차량번호:" + newYdCarNo + ", 카드번호:" + newYdCardNo + ", 운송지시일자:" + newTransOrdDate + ", 운송지시순번:" + newTransOrdSeqNo + "]에 대해 차량도착 전문전송 완료"+szCHK_YN;

					commUtils.printLog(logId, szMsg, "SL");
				}

				if("9".equals(ydCarWrkGp) && "TR".equals(newYdCarKind))
				{
					commUtils.printLog(logId, "2냉연 TR은 도착전문을 받고서 야드저장위치제원(YFF1L001)송신 처리 한다.", "SL");
				}
				else
				{
					//----------------------------------------------------------------------
					// 야드저장위치제원(YFF1L001) 전문전송
					//----------------------------------------------------------------------
					jrParam.setField("YD_INFO_SYNC_CD",	"3"); 				//야드정보동기화코드(3:열)
					jrParam.setField("YD_STK_COL_GP",	newYdStackColGp);	//야드적치열구분
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L001", jrParam));
				}

				if ("Y".equals(szCHK_YN) || "H".equals(szCHK_YN))
				{
					//------------------------------------------------------------------------------------------------------------
			    	//	입동TC 전송
			    	//------------------------------------------------------------------------------------------------------------
					JDTORecord recInTemp = JDTORecordFactory.getInstance().create();

					if ("P".equals(transEquipType))
					{
						YdStockDao ydStockDao  = new YdStockDao();

						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						JDTORecord recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("TRANS_ORD_DATE",  commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE")));
						recPara.setField("TRANS_ORD_SEQNO", commUtils.trim(jsCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));

						// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB*/
						//int nRet = ydStockDao.getYdStock(recPara, rsResult2, 731);
						int nRet = commDao.getYdStock(recPara, rsResult2, 731);

						if (nRet > 0)
						{
							rsResult2.first();
							JDTORecord recTemp	= rsResult2.getRecord();
							szCR_FRTOMOVE_GP	= StringHelper.evl(recTemp.getFieldString("CR_FRTOMOVE_GP") , "");	//냉연이송구분
						}

						recInTemp.setField("JMS_TC_CD",				"YDDMR070");
						recInTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
						recInTemp.setField("TC_CODE",				"YDDMR070");
						recInTemp.setField("TC_CREATE_DDTT",		commUtils.getDateTime14());
						recInTemp.setField("CR_FRTOMOVE_GP",		szCR_FRTOMOVE_GP);
					}
					else
					{
						recInTemp.setField("JMS_TC_CD",				"YDDMR028");
						recInTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
						recInTemp.setField("TC_CODE",				"YDDMR028");
						recInTemp.setField("TC_CREATE_DDTT",		commUtils.getDateTime14());
					}

					recInTemp.setField("TRANS_WORD_DATE",		newTransOrdDate);
					recInTemp.setField("TRANS_WORD_SEQNO",		newTransOrdSeqNo);
					recInTemp.setField("BAYIN_DDTT",			commUtils.getDateTime14());

					recInTemp.setField("CARD_NO",				newYdCardNo);
					recInTemp.setField("CAR_NO",				newYdCarNo);
					recInTemp.setField("WLOC_CD",				newWlocCd);
					recInTemp.setField("YD_PNT_CD",				newYdPntCd);
					recInTemp.setField("YD_CARPNT_CD",			newYdCarPntCd);
					recInTemp.setField("LOAN_PULLOUT_ABLE_YN",	"Y");

					jrRtn = commUtils.addSndData(jrRtn, recInTemp);

					//------------------------------------------------------------------------------------------------------------
					//입동지시 대상차량에 입동지시 송신여부 셋팅
					//------------------------------------------------------------------------------------------------------------
					jrParam.setField("YD_CAR_RCPT_CHK_YN",	"E");
					jrParam.setField("CAR_NO",				newYdCarNo);
					jrParam.setField("CARD_NO",				newYdCardNo);
					jrParam.setField("TRANS_ORD_DATE",		newTransOrdDate);
					jrParam.setField("TRANS_ORD_SEQNO",		newTransOrdSeqNo);
					commDao.update(jrParam, updYdCarschYdCarRcptChkYn, logId, methodNm, "차량스케줄에 입동지시 송신여부 셋팅");
				}

				if ("Y".equals(sAPPLY1))
		 		{
					/***** 차량log Claer ****/
					JDTORecord jrLogMsg = JDTORecordFactory.getInstance().create();
					jrLogMsg.setResultCode(logId);		//Log ID
					jrLogMsg.setResultMsg(methodNm);	//Log Method Name
					jrLogMsg.setField("MODIFIER",		modifier);		//수정자 셋팅
					jrLogMsg.setField("YD_CAR_SCH_ID",	newYdCarSchId);	//차량스케쥴

					//EJBConnector ejbConnLog = new EJBConnector("default", "YfCommBakSeEJB", this);
					//ejbConnLog.trx("updCarErrorLogClear", new Class[] { JDTORecord.class }, new Object[] { jrLogMsg });
					this.updCarErrorLogClear(jrLogMsg);
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
	 * [A] 오퍼레이션명 : 차량입동 ERROR LOG처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
     */
	public boolean updCarErrorLog(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "차량입동 ERROR LOG처리[YfCommCarMvSeEJB.updCarErrorLog] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			commDao.update(rcvMsg, insCarYdMsgNm, logId, methodNm, "차량 ERROR 메세지");

			commUtils.printLog(logId, methodNm, "S-");
		}
		catch(Exception e)
		{

		}

		return true;
	}

	/**
	 * [A] 오퍼레이션명 : 차량입동 ERROR LOG처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
     */
	public boolean updCarErrorLogNew(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "차량입동 ERROR LOG처리[YmCommSeEJB.updCarErrorLogNew] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			commDao.update(rcvMsg, insCarYdMsgNm, logId, methodNm, "차량 ERROR 메세지");

			commUtils.printLog(logId, methodNm, "S-");
		}
		catch(Exception e)
		{

		}

		return true;
	}

	/**
	 * [A] 오퍼레이션명 : 차량입동 ERROR LOG Clear 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
     */
	public boolean updCarErrorLogClear(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "차량입동 ERROR LOG처리 Clear[YfCommCarMvSeEJB..updCarErrorLogClear] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			commDao.update(rcvMsg, updCarYdMsgNmClear, logId, methodNm, "차량 ERROR 메세지 Clear");

			commUtils.printLog(logId, methodNm, "S-");
		}
		catch(Exception e)
		{

		}

		return true;
	}

	/**
	 * 오퍼레이션명 : Yf작업예약생성전 검사
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procYfWbookInsertCheck(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "Yf작업예약생성전 검사[YfCommCarMvSeEJB.procYfWbookInsertCheck] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
		 	String ydBayGp 			= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"));
		 	String ydGp 			= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
		 	String TransOrdDate 	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_DATE"));		//운송작업지시일자
		 	String TransOrdSeqNo 	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_SEQNO"));	//운송작업지시순번
		 	String ydCarPntCd 		= commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"));		//야드차량포인트코드
		 	String ydEqpWrkStat 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_STAT"));		// 'U':상차적업,'L': 하차작업
		 	String modifier        	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));			//수정자(Backup Only)

		 	String ydSchCd			= "";
		 	String sIPDONG_YN		= "N";

		 	jrRtn.setField("STAT",	"1");

		 	jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			/********************************
			 * 입동전 저장품 상태 체크
			 *******************************/
			jrParam.setField("TRANS_ORD_DATE",	TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
			jrParam.setField("YD_BAY_GP",		ydBayGp);
			JDTORecordSet rst = commDao.select(jrParam, getChkBayInYn, logId, methodNm, "입동전 저장품 상태 체크");

			if (rst.size() > 0)
			{
				sIPDONG_YN      = commUtils.trim(rst.getRecord(0).getFieldString("IPDONG_YN"));
			}

			if ("N".equals(sIPDONG_YN))
			{
				jrRtn.setField("STAT",		"-1");
				jrRtn.setField("YD_MSG",	"해당 저장품 상태 이상");

				return jrRtn;
			}

		 	/**********************************************************
			* 1. 스케쥴 코드 등록여부 CHECK
			**********************************************************/
		 	int intTransOrdSeqNo = StringHelper.parseInt(TransOrdSeqNo, 0);

		 	if (intTransOrdSeqNo > 999000)
		 	{
		 		//반품,회송,부분하차
				if ("2".equals(ydCarPntCd.substring(1, 2)))
				{
					//R
					ydSchCd = ydGp + ydBayGp + "PT02LM";
				}
				else
				{
					//L
					ydSchCd = ydGp + ydBayGp + "PT01LM";
				}
		 	}
		 	else if (intTransOrdSeqNo > 800000)
		 	{
				//제품이송
				if ("U".equals(ydEqpWrkStat))
				{
					//상차
					if ("2".equals(ydCarPntCd.substring(1, 2)))
					{
						//R
						ydSchCd = ydGp + ydBayGp + "PT22UM";
					}
					else
					{
						//L
						ydSchCd = ydGp + ydBayGp + "PT21UM";
					}
				}
				else
				{
					//하차
					if ("2".equals(ydCarPntCd.substring(1, 2)))
					{
						//R
						ydSchCd = ydGp + ydBayGp + "PT22LM";
					}
					else
					{
						//L
						ydSchCd = ydGp + ydBayGp + "PT21LM";
					}
				}
			}
		 	else if (intTransOrdSeqNo > 700000 && intTransOrdSeqNo <= 800000)
		 	{
				//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송
				if ("U".equals(ydEqpWrkStat))
				{
					//상차
					if ("2".equals(ydCarPntCd.substring(1, 2)))
					{
						//R
						ydSchCd = ydGp + ydBayGp + "PT12UM";
					}
					else
					{
						//L
						ydSchCd = ydGp + ydBayGp + "PT11UM";
					}
				}
				else
				{
					//하차
					if ("2".equals(ydCarPntCd.substring(1, 2)))
					{
						//R
						ydSchCd = ydGp + ydBayGp + "PT12LM";
					}
					else
					{
						//L
						ydSchCd = ydGp + ydBayGp + "PT11LM";
					}
				}
		 	}
		 	else
		 	{
				//출하
				if ("2".equals(ydCarPntCd.substring(1, 2)))
				{
					//R
					ydSchCd = ydGp + ydBayGp + "PT22UM";
				}
				else
				{
					//L
					ydSchCd = ydGp + ydBayGp + "PT21UM";
				}
			}

			//스케줄코드로 스케줄기준Table조회
			jrParam.setField("YD_SCH_CD", ydSchCd);
			JDTORecordSet rsResult = commDao.select(jrParam, getYdSchrule, logId, methodNm, "스케줄 기준 조회");

			if(rsResult != null && rsResult.size() > 0)
			{
				//??
			}
			else
			{
				jrRtn.setField("STAT",		"-1");
				jrRtn.setField("YD_MSG",	"A열연 코일 스케쥴 코드 이상 : [" + ydSchCd + "]");

				return jrRtn;
			}

		 	/**********************************************************
			* 2. 작업예약 존재 여부 CHECK
			**********************************************************/
			jrParam.setField("TRANS_ORD_DT", 	TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
			JDTORecordSet jsWbookMtl = commDao.select(jrParam, getYfStockWbookcheck, logId, methodNm, "작업예약 조회");

		 	if (jsWbookMtl.size() > 0)
		 	{
				commUtils.printLog(logId, "이적 및 대차출하 작업예약이 존재 합니다", "SL");

				for(int Loop_i = 1; Loop_i <= jsWbookMtl.size() ; Loop_i++)
				{
					jsWbookMtl.absolute(Loop_i);
					JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
					jrInPara.setRecord(jsWbookMtl.getRecord());
					jrInPara.setResultCode(logId);		//Log ID
					jrInPara.setResultMsg(methodNm);	//Log Method Name
					jrInPara.setField("MODIFIER",	modifier); //수정자

					//크레인 작업예약 삭제
					EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
					ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrInPara });
				}
			}

			jrParam.setField("TRANS_ORD_DATE", 	TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", TransOrdSeqNo);
			JDTORecordSet jsChk2 = commDao.select(jrParam, getYfwBookStockYN, logId, methodNm, "작업예약 등록여부");

			commUtils.printLog(logId, "작업 예약 편성여부:" + jsChk2.getRecord(0).getFieldString("WB_STL_YN") , "SL");

			if (jsChk2 != null && jsChk2.size() > 0)
			{
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN")))
				{
					jrRtn.setField("STAT",		"-1");
					jrRtn.setField("YD_MSG",	"이미 다른 크레인예약이  되어 있슴:크레인 스케쥴 확인");

					return jrRtn;
				}
			}

		 	/**********************************************************
			* 3. 운송지시 저장품대상 CHECK
			**********************************************************/
			JDTORecordSet jsStock  = JDTORecordFactory.getInstance().createRecordSet("Temp");

			//반품
			if(intTransOrdSeqNo > 999000)
			{
				//하차 대상 재료를 조회한다.
				jrParam.setField("TRANS_ORD_DT",	TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
				jrParam.setField("CAR_NO",			ydCarNo);
				jsStock = commDao.select(jrParam, getCarftmvmtl, logId, methodNm, "하차대상재 조회");

			 	if (jsStock.size() <= 0)
			 	{
					jrRtn.setField("STAT",		"-1");
					jrRtn.setField("YD_MSG",	"운송지시 저장품대상이 없습니다:코일정보 확인");

					return jrRtn;
				}
			}
			else
			{
				jrParam.setField("TRANS_ORD_DT",	TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
				jrParam.setField("YD_GP",			ydGp);
				jrParam.setField("YD_BAY_GP",		ydBayGp);
				jrParam.setField("YD_CARPNT_CD",	ydCarPntCd);
				jsStock = commDao.select(jrParam, getYdStockTransOrdDTWbook, logId, methodNm, "작업대상 조회");

			 	if (jsStock.size() <= 0)
			 	{
					jrRtn.setField("STAT",		"-1");
					jrRtn.setField("YD_MSG",	"운송지시 저장품대상이 없습니다:코일정보 확인");

					return jrRtn;
				}
			}

			if (intTransOrdSeqNo > 999000)
			{
				//??
			}
			else
			{
			 	/**********************************************************
				* 4. 운송지시갯수와 LAYER 갯수 확인  CHECK
				**********************************************************/
				jrParam.setField("TRANS_ORD_DT"		,TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	,TransOrdSeqNo);
				JDTORecordSet jsStockCnt = commDao.select(jrParam, getYdStockLayerCntChk, logId, methodNm, "작업대상갯수 조회");

				if (jsStockCnt.size() <= 0)
			 	{
					jrRtn.setField("STAT",		"-1");
					jrRtn.setField("YD_MSG",	"운송지시갯수와  저장위치 저장품갯수가 틀림:코일정보 확인");

					return jrRtn;
				}
			}

			//출력 값
	    	jrRtn.setField("STAT",		"1");
			jrRtn.setField("YD_MSG",	"정상");

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
	 * 오퍼레이션명 : 차량POINT 점유(procUpdYdTransOrdChange)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public String procUpdYdTransOrdChange(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "Yf 차량POINT 점유[YfCommCarMvSeEJB.procUpdYdTransOrdChange] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			String trnEqpCd			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));
			String ydCarNo			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			String ydCardNo			= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
			String ydMakeCarPntCd	= commUtils.trim(rcvMsg.getFieldString("YD_MAKECARPNT_CD"));
			String ydModifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			
			//차량번호로 TB_YD_CARPOINT 포인트 위치 초기화
//			JDTORecord 	jrParam = JDTORecordFactory.getInstance().create();
//			jrParam.setField("CAR_NO",				ydCarNo);
//			jrParam.setField("MODIFIER",				ydModifier);
//			commDao.update(jrParam, updPlnInfoReSet4Car, logId, methodNm, "TB_YD_CARPOINT 차량정보로 초기화");

		 	//------------------------------------------------------------------------------------------------------------
	    	//	차량포인트 도착상태 변경 처리
	    	//------------------------------------------------------------------------------------------------------------
			JDTORecord 	recInTemp = JDTORecordFactory.getInstance().create();
		    recInTemp.setField("YD_STK_COL_ACT_STAT",	"L");	//R:예약, L:사용중(도착불가), N:사용금지, C:비었음(도착가능)
		    recInTemp.setField("TRN_EQP_CD",			trnEqpCd);
		    recInTemp.setField("CAR_NO",				ydCarNo);
		    recInTemp.setField("CARD_NO",				ydCardNo);
		    recInTemp.setField("YD_CARPNT_CD",			ydMakeCarPntCd);
		    recInTemp.setField("MODIFIER",				ydModifier);
		    commDao.insert(recInTemp, updYdCarpointByCarpnt, logId, methodNm, "TB_YD_CARPOINT 갱신");

		    commUtils.printLog(logId, methodNm, "S-");

		    return YfConstant.RETN_CD_SUCCESS;
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
	 * 오퍼레이션명 : STOCK에 현재 저장품 관리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public String procUpdYfStock(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "Yf 차량  STOCK 저장품 현위치 등록[YfCommCarMvSeEJB.procUpdYfStock] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			String TransOrdDate		= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_DATE"));
			String TransOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_SEQNO"));
			String modifier		    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));

			String StlNo			= "";
			String YdStkColGp		= "";

		 	//------------------------------------------------------------------------------------------------------------
	    	//	저장품 에 현 위치 등록
	    	//------------------------------------------------------------------------------------------------------------
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", 		modifier);		//수정자 셋팅
			jrParam.setField("TRANS_ORD_DATE",	TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
			JDTORecordSet jsStock = commDao.select(jrParam, getYdStockExpect, logId, methodNm, "차량스케쥴 조회");

			if (jsStock.size() > 0)
			{
				for(int ii = 0; ii < jsStock.size(); ii++)
				{
					StlNo 		= commUtils.trim(jsStock.getRecord(ii).getFieldString("STL_NO"));
					YdStkColGp 	= commUtils.trim(jsStock.getRecord(ii).getFieldString("YD_STK_COL_GP"));

					jrParam.setField("STL_NO",			StlNo);
					jrParam.setField("YD_STK_COL_GP",	YdStkColGp);
					commDao.update(jrParam, updYdStockExpect, logId, methodNm, "저장품 제원");
				}
			}

		    commUtils.printLog(logId, methodNm, "S-");

		    return YfConstant.RETN_CD_SUCCESS;
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
	 * 오퍼레이션명 : 출하시 맵활성화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public String procYfLayerOpen(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "출하시 맵 활성화[YfCommCarMvSeEJB.procYfLayerOpen] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

		int			intRtnVal	= 0;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

	    	//------------------------------------------------------------------------------------------------------------
	    	//	파라미터 확인
	    	//------------------------------------------------------------------------------------------------------------
			String YdStkColGp		= commUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_GP"), "");
			String ydCarNo 			= commUtils.nvl(rcvMsg.getFieldString("CAR_NO"), "");
			String ydCardNo 		= commUtils.nvl(rcvMsg.getFieldString("CARD_NO"), "");
			String TrnEqpCd 		= commUtils.nvl(rcvMsg.getFieldString("TRN_EQP_CD"), "");
			String ydCarProgStat 	= commUtils.nvl(rcvMsg.getFieldString("YD_CAR_PROG_STAT"), "");
			String transEquipType 	= commUtils.nvl(rcvMsg.getFieldString("TRANS_EQUIPMENT_TYPE"), "N");
			String modifier     	= commUtils.nvl(rcvMsg.getFieldString("MODIFIER"), "");

			//------------------------------------------------------------------------------------------------------------
	    	//	적치열 테이블에 활성상태 처리
	    	//------------------------------------------------------------------------------------------------------------
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER",				modifier);	//수정자
			jrParam.setField("YD_STK_COL_ACTIVE_STAT",	"L");
			jrParam.setField("YD_CAR_USE_GP",			"G");
			jrParam.setField("TRN_EQP_CD",				TrnEqpCd);
			jrParam.setField("CAR_NO",					ydCarNo);
			jrParam.setField("CARD_NO",					ydCardNo);
			jrParam.setField("YD_STK_COL_GP",			YdStkColGp);
	    	intRtnVal = commDao.update(jrParam, updYdStkcol, logId, methodNm, "TB_YF_STKCOL 등록");

			/********************************
			 * 적치베드 상태 비활성화등록
			 *******************************/
	    	jrParam.setField("YD_STK_BED_ACTIVE_STAT",	"L");
	    	jrParam.setField("YD_STK_BED_WT_MAX",		YfConstant.YD_STK_BED_WT_MAX_DEFAULT);
			intRtnVal = commDao.update(jrParam, updYdStkbedYdStkColGp, logId, methodNm, "TB_YF_STKBED 활성상태수정");

			//PDA하차 출하인 경우 생략
			if ("P".equals(transEquipType) && ("A".equals(ydCarProgStat) || "B".equals(ydCarProgStat)))
			{
				commUtils.printLog(logId, methodNm+"PDA하차 출하 인 경우 적치단 활성화 생략", "SL");
			}
			else
			{
				/********************************
				 * 적치단 상태 활성화등록
				 *******************************/
				jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"E");
				jrParam.setField("YD_STK_LYR_STAT",			"E");
				intRtnVal = commDao.update(jrParam, updYdStkLyrYdStkColGpClear, logId, methodNm, "TB_YF_STKLYR 차량 적치단 활성상태수정");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return  "" + intRtnVal ;
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
	 * 오퍼레이션명 : Yf작업예약생성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procYfWbookInsert(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "Yf작업예약생성[YfCommCarMvSeEJB.procYfWbookInsert] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			String ydStkColGp		= commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_GP"));
			String ydCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
		 	String YdCardNo 		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
		 	String ydBayGp 			= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"));
		 	String ydGp 			= commUtils.trim(rcvMsg.getFieldString("YD_GP"));
		 	String TransOrdDate 	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_DATE"));
		 	String TransOrdSeqNo 	= commUtils.trim(rcvMsg.getFieldString("TRANS_WORD_SEQNO"));
		 	String ydCarPntCd 		= commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"));
		 	String transFrtoMoveGp	= commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP"));	//1 운송 2 이송
		 	String ydEqpWrkStat 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_STAT"));		//'U':상차적업,'L': 하차작업
		 	String modifier        	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));			//수정자(Backup Only)
		 	String ydCarSchId      	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));		//수정자(Backup Only)

		 	String ydSchCd			= "";

		 	jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

		 	//------------------------------------------------------------------------------------------------------------
	    	//	스케줄 코드 생성 하기
	    	//------------------------------------------------------------------------------------------------------------
			// 출하 1통로 2통로 구분
		 	int intTransOrdSeqNo = StringHelper.parseInt(TransOrdSeqNo, 0);

		 	if (intTransOrdSeqNo > 999000)
		 	{
		 		//반품,회송,부분하차
				if ("2".equals(ydCarPntCd.substring(1,2)))	//우측 2번 통로 R
				{
					ydSchCd = ydGp + ydBayGp + "PT02LM";
				}
				else
				{
					ydSchCd = ydGp + ydBayGp + "PT01LM";
				}
		 	}
		 	else if ((intTransOrdSeqNo > 800000))
		 	{
				//제품이송
				if ("U".equals(ydEqpWrkStat))
				{
					//상차
					if ("2".equals(ydCarPntCd.substring(1,2)))
					{
						ydSchCd = ydGp + ydBayGp + "PT22UM";
					}
					else
					{
						ydSchCd = ydGp + ydBayGp + "PT21UM";
					}
				}
				else
				{
					//하차
					if ("2".equals(ydCarPntCd.substring(1,2)))
					{
						ydSchCd = ydGp + ydBayGp + "PT22LM";
					}
					else
					{
						ydSchCd = ydGp + ydBayGp + "PT21LM";
					}
				}
			}
		 	else if ((intTransOrdSeqNo > 700000 && intTransOrdSeqNo <= 800000))
		 	{
				//(임가공이송 OR 순천 이송) 스케줄 구분 : 냉연이송(소재이송)
				if ("U".equals(ydEqpWrkStat))
				{
					//상차
					if ("2".equals(ydCarPntCd.substring(1,2)))
					{
						ydSchCd = ydGp + ydBayGp + "PT12UM";
					}
					else
					{
						ydSchCd = ydGp + ydBayGp + "PT11UM";
					}
				}
				else
				{
					//하차
					if ("2".equals(ydCarPntCd.substring(1,2)))
					{
						ydSchCd = ydGp + ydBayGp + "PT12LM";
					}
					else
					{
						ydSchCd = ydGp + ydBayGp + "PT11LM";
					}
				}
			}
		 	else
		 	{
				//제품이송
		 		if ("U".equals(ydEqpWrkStat))
				{
		 			//상차
		 			if ("2".equals(ydCarPntCd.substring(1,2)))
		 			{
		 				ydSchCd = ydGp + ydBayGp + "PT22UM";
		 			}
		 			else
		 			{
		 				ydSchCd = ydGp + ydBayGp + "PT21UM";
		 			}
				}
		 		else
		 		{
		 			//하차
					if ("2".equals(ydCarPntCd.substring(1,2)))
					{
						ydSchCd = ydGp + ydBayGp + "PT22LM";
					}
					else
					{
						ydSchCd = ydGp + ydBayGp + "PT21LM";
					}
		 		}
			}

			//스케줄코드로 스케줄기준Table조회
			jrParam.setField("YD_SCH_CD", ydSchCd);
			JDTORecordSet rsResult = commDao.select(jrParam, getYdSchrule, logId, methodNm, "스케줄 기준 조회");

			String ydSchPrior = "";
			String ydWrkCrn = "";

			if (rsResult != null && rsResult.size() > 0)
			{
				ydWrkCrn   = rsResult.getRecord(0).getFieldString("YD_WRK_CRN"); 		//작업 크레인
				ydSchPrior = rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR");	//야드스케쥴우선순위
			}
			else
			{
				throw new Exception("A열연 코일 스케쥴 코드 이상 : [" + ydSchCd + "]");
			}

			//------------------------------------------------------------------------------------------------------------
	    	//	작업예약 존재 여부 CHECK
	    	//------------------------------------------------------------------------------------------------------------
			jrParam.setField("TRANS_ORD_DT", 			TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
			JDTORecordSet jsWbookMtl = commDao.select(jrParam, getYfStockWbookcheck, logId, methodNm, "작업예약 조회");

			if (jsWbookMtl.size() > 0)
			{
				commUtils.printLog(logId, "이적 및 대차출하 작업예약이 존재 합니다", "SL");

				for (int Loop_i = 1; Loop_i <= jsWbookMtl.size() ; Loop_i++)
				{
					jsWbookMtl.absolute(Loop_i);
					JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
					jrInPara.setRecord(jsWbookMtl.getRecord());
					jrInPara.setResultCode(logId);		//Log ID
					jrInPara.setResultMsg(methodNm);	//Log Method Name
					jrInPara.setField("MODIFIER",	modifier);	//수정자

					//크레인 작업예약 삭제
					EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
					ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrInPara });
				}
			}

			/**********************************************************
			 * 3. 작업예약 등록여부 .. 재송신error
			 **********************************************************/
			jrParam.setField("TRANS_ORD_DATE",	TransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", TransOrdSeqNo);
			JDTORecordSet jsChk2 = commDao.select(jrParam, getYfwBookStockYN, logId, methodNm, "작업예약 등록여부");

			commUtils.printLog(logId, "작업 예약 편성여부:" + jsChk2.getRecord(0).getFieldString("WB_STL_YN") , "SL");

			if (jsChk2 != null && jsChk2.size() > 0)
			{
				if ("Y".equals(jsChk2.getRecord(0).getFieldString("WB_STL_YN")))
				{
					throw new Exception("이미 다른 크레인예약이  되어 있슴 ");
				}
			}

			String ydAimydGp	= ydGp;
			String ydAimBayGp	= ydBayGp;

			JDTORecordSet jsStock  = JDTORecordFactory.getInstance().createRecordSet("Temp");

			if (intTransOrdSeqNo > 999000)
			{
				//반품,회송,부분하차 일경우
				//차량 하차 위치(STKLYR)에 제품을 적치시킨다.
				jrParam.setField("CAR_NO",			ydCarNo);
				jrParam.setField("TRANS_ORD_DT",	TransOrdDate);
			 	jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
			 	jrParam.setField("MODIFIER",		modifier);
				commDao.update(jrParam, updCarftmvmtlToLyr);

				//작업예약재료를 생성하기 위해 rsResult 에 하차 대상 재료를 조회한다.
				jrParam.setField("TRANS_ORD_DT",	TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
				jrParam.setField("CAR_NO",			ydCarNo);
				jsStock = commDao.select(jrParam, getCarftmvmtl, logId, methodNm, "하차대상재 조회");

				if (jsStock.size() <= 0)
			 	{
					m_ctx.setRollbackOnly();

			 		throw new Exception("운송지시 저장품대상이 없습니다: [" + ydSchCd + "]");
				}
			}
			else
			{
				jrParam.setField("TRANS_ORD_DT",	TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
				jrParam.setField("YD_GP",			ydGp);
				jrParam.setField("YD_BAY_GP",		ydBayGp);
				jrParam.setField("YD_CARPNT_CD",	ydCarPntCd);
				jsStock = commDao.select(jrParam, getYdStockTransOrdDTWbook, logId, methodNm, "스케줄 기준 조회");

				if (jsStock.size() <= 0)
			 	{
					m_ctx.setRollbackOnly();

			 		throw new Exception("운송지시 저장품대상이 없습니다: [" + ydSchCd + "]");
				}
			}

			//------------------------------------------------------------------------------------------------------------
	    	//	작업예약 생성 하기
	    	//------------------------------------------------------------------------------------------------------------
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			JDTORecord jrInTemp  = JDTORecordFactory.getInstance().create();

			String first_wbook_ID = "";

	    	for(int Loop_i = 0; Loop_i < jsStock.size(); Loop_i++)
	    	{
				//작업예약 등록
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");

				if ("".equals(first_wbook_ID))
				{
					first_wbook_ID = ydWbookId; //첫번째 작업예약 ID
				}

				recInTemp.setField("YD_WBOOK_ID",		ydWbookId);		//야드작업예약ID
				recInTemp.setField("MODIFIER",			modifier);		//수정자
				recInTemp.setField("YD_GP",				ydGp);			//야드구분
				recInTemp.setField("YD_BAY_GP",			ydBayGp);		//야드동구분
				recInTemp.setField("YD_SCH_CD",			ydSchCd);		//야드스케쥴코드
				recInTemp.setField("YD_SCH_PRIOR",		ydSchPrior);	//야드스케쥴우선순위
				recInTemp.setField("YD_SCH_PROG_STAT",	"W");			//야드스케쥴진행상태(스케줄수행대기)
				recInTemp.setField("YD_SCH_ST_GP",		"O");			//야드스케쥴기동구분(Manual)
				recInTemp.setField("YD_SCH_REQ_GP",		"M");			//야드스케쥴요청구분(이적)
				recInTemp.setField("YD_CAR_USE_GP",		"G");
				recInTemp.setField("CAR_NO",			ydCarNo);
				recInTemp.setField("CARD_NO",			YdCardNo);
				recInTemp.setField("YD_AIM_YD_GP",		ydAimydGp);
				recInTemp.setField("YD_AIM_BAY_GP",		ydAimBayGp);
				commDao.insert(recInTemp, insWrkBook2, logId, methodNm, "TB_YF_WRKBOOK");

	    		//작업예약재료 등록
	    		jrInTemp.setField("YD_WBOOK_ID",		ydWbookId);
	    		jrInTemp.setField("MODIFIER",			modifier);
	    		jrInTemp.setField("YD_STK_COL_GP",		ydStkColGp);
	    		jrInTemp.setField("YD_STK_BED_NO",		commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("YD_STK_BED_NO")));
	    		jrInTemp.setField("YD_STK_LYR_NO",		commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("YD_STK_LYR_NO")));
	    		jrInTemp.setField("STL_NO",				commUtils.trim(jsStock.getRecord(Loop_i).getFieldString("STL_NO")));
	    		jrInTemp.setField("YD_UP_COLL_SEQ",		"" + Loop_i);
	    		commDao.insert(jrInTemp, insWrkBookMtl, logId, methodNm, "TB_YF_WRKBOOKMTL");
	    	}

			//출력 값
			jrRtn.setField("YD_WBOOK_ID",	first_wbook_ID);
			jrRtn.setField("YD_EQP_ID",		ydWrkCrn);
			jrRtn.setField("YD_SCH_CD",		ydSchCd);

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
	 * [A] 오퍼레이션명 : 소재차량출발취소(TSYDJ014)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvTSYDJ014(JDTORecord rcvMsg) throws DAOException
	{
		String		methodNm	= "소재차량출발취소[YfCommCarMvSeEJB.rcvTSYDJ014] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

	    String		szMsg		= "";

	    JDTORecordSet rsResult  = null;

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============소재차량출발취소 시작========", "SL");

			if (msgId==null || "".equals(msgId))
	        {
	        	return jrRtn;
	        }

	    	//수신항목 변수 저장
			String szTRN_EQP_CD	= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); //운송장비코드

			jrParam.setField("TRN_EQP_CD",	szTRN_EQP_CD);
			JDTORecordSet sposChklist = commDao.select(jrParam, getListSposYNchk_E, logId, methodNm, "기존 출발정보 유무 확인");

			if(sposChklist.size() > 0)
			{
				String s_SPOS_WLOC_CD 		= commUtils.trim(sposChklist.getRecord(0).getFieldString("SPOS_WLOC_CD"));
				String s_CARLD_WRK_BOOK_ID 	= commUtils.trim(sposChklist.getRecord(0).getFieldString("YD_CARLD_WRK_BOOK_ID"));
				String s_YD_CAR_SCH_ID		= commUtils.trim(sposChklist.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				String s_YD_CAR_PROG_STAT	= commUtils.trim(sposChklist.getRecord(0).getFieldString("YD_CAR_PROG_STAT"));

				szMsg="["+methodNm+"] 검색 결과  >> SPOS_WLOC_CD: " + s_SPOS_WLOC_CD + ", CARLD_WRK_BOOK_ID: " + s_CARLD_WRK_BOOK_ID + ", YD_CAR_SCH_ID: " + s_YD_CAR_SCH_ID + ", YD_CAR_PROG_STAT: " + s_YD_CAR_PROG_STAT;
				commUtils.printLog(logId, szMsg, "SL");

				//A열연스케쥴 존재여부 체크
				jrParam.setField("YD_WBOOK_ID",	s_CARLD_WRK_BOOK_ID);
				rsResult = commDao.select(jrParam, getListSchchkYN, logId, methodNm, "A열연스케쥴 존재여부 체크");

				if (rsResult.size() > 0)
				{
					szMsg = "["+methodNm+"] A열연스케쥴 존재여부 체크 결과 YD_CRN_SCH_ID : " + commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")) + " 검색되어 차량출발취소 처리가 불가합니다.";
					commUtils.printLog(logId, szMsg, "S-");

					return jrRtn;
				}

				//B열연스케쥴 존재여부 체크
				jrParam.setField("YD_WBOOK_ID",	s_CARLD_WRK_BOOK_ID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListSchchkYN", logId, methodNm, "B열연스케쥴 존재여부 체크");

				if (rsResult.size() > 0)
				{
					szMsg = "["+methodNm+"] B열연스케쥴 존재여부 체크 결과 YD_CRN_SCH_ID : " + commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")) + " 검색되어 차량출발취소 처리가 불가합니다.";
					commUtils.printLog(logId, szMsg, "S-");

					return jrRtn;
				}

				// 신규야드스케쥴 존재여부 체크
				jrParam.setField("YD_WBOOK_ID",	s_CARLD_WRK_BOOK_ID);
				rsResult = commDao.select(jrParam, getListYDSchchkYN, logId, methodNm, "신규야드스케쥴 존재여부 체크");

				if(rsResult.size() > 0)
				{
					szMsg = "["+methodNm+"] 신규야드스케쥴 존재여부 체크 결과 YD_CRN_SCH_ID : " + commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")) + " 검색되어 차량출발취소 처리가 불가합니다.";
					commUtils.printLog(logId, szMsg, "S-");

					return jrRtn;
				}

				//개소코드로 박판열연인지 판단한다.
				if (this.getABLocationInfo_02(s_SPOS_WLOC_CD))
				{
					//**********************************************************************************
					// 1.A열연으로 출발한 정보 취소처리
					szMsg="["+methodNm+"] A열연으로 출발한 정보 취소처리 ============================";
					commUtils.printLog(logId, szMsg, "SL");

					//**********************************************************************************
					// 1-1.A열연야드 작업예약삭제 (DEL_YN='Y')
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, deleteWBook, logId, methodNm, "A열연야드 작업예약삭제");

					//**********************************************************************************
					// 1-2.A열연야드 작업예약재료삭제 (DEL_YN='Y')
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, delYMStkBookDtl, logId, methodNm, "A열연야드 작업예약삭제");

					if ("1".equals(s_YD_CAR_PROG_STAT))
					{
						//**********************************************************************************
						// 1-3-1.A열연야드 예약위치정보삭제
						jrParam.setField("CAR_CARD_NO",	szTRN_EQP_CD);
						commDao.update(jrParam, updStackStatByTrnEqpCd, logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리");
					}
					else if ("2".equals(s_YD_CAR_PROG_STAT))
					{
						//**********************************************************************************
						// 1-3-2.A열연야드 적치단  Table Update(close 로 변경)
						jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"C");
						jrParam.setField("TRN_EQP_CD",				szTRN_EQP_CD);
						commDao.update(jrParam, updateStackLayerStatMark_empty, logId, methodNm, "A열연야드 적치단  Table Update(close 로 변경)");

						//**********************************************************************************
						// 1-3-3.A열연야드 현재위치정보삭제
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
						commDao.update(jrParam, updCarUseGpByTrnEqpCd, logId, methodNm, "차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");
					}

					//**********************************************************************************
					// 1-4.차량스케줄정보삭제
					jrParam.setField("YD_CAR_SCH_ID", s_YD_CAR_SCH_ID);
					commDao.update(jrParam, delCarschID, logId, methodNm, "차량스케줄정보삭제");

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YfCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
				}
				else if(this.getABLocationInfo_03(s_SPOS_WLOC_CD))
				{
					//**********************************************************************************
					// 1.AB열연으로 출발한 정보 취소처리
					szMsg="["+methodNm+"] AB열연으로 출발한 정보 취소처리 ============================";
					commUtils.printLog(logId, szMsg, "SL");

					//**********************************************************************************
					// 1-1.AB열연야드 작업예약삭제 (DEL_YN='Y')
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.deleteWBook", logId, methodNm, "B열연야드 작업예약삭제");

					//**********************************************************************************
					// 1-2.AB열연야드 작업예약재료삭제 (DEL_YN='Y')
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delYMStkBookDtl", logId, methodNm, "B열연야드 작업예약삭제");

					if ("1".equals(s_YD_CAR_PROG_STAT))
					{

						//**********************************************************************************
						// 1-3-1.AB열연야드 예약위치정보삭제
						jrParam.setField("CAR_CARD_NO", szTRN_EQP_CD);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackStatByTrnEqpCd", logId, methodNm, "차량위치 예정정보 삭제(상하차출발 위치)정리");

					}
					else if ("2".equals(s_YD_CAR_PROG_STAT))
					{

						//**********************************************************************************
						// 1-3-2.AB열연야드 적치단  Table Update(close 로 변경)
						jrParam.setField("STACK_LAYER_ACTIVE_STAT", "C");
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStackLayerStatMark_empty", logId, methodNm, "B열연야드 적치단  Table Update(close 로 변경)");

						//**********************************************************************************
						// 1-3-3.AB열연야드 현재위치정보삭제
						jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseGpByTrnEqpCd", logId, methodNm, "차량위치정보 삭제(상하차개시/완료/도착 위치)정리 ");
					}

					//**********************************************************************************
					// 1-4.차량스케줄정보삭제
					jrParam.setField("YD_CAR_SCH_ID", s_YD_CAR_SCH_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delCarschID", logId, methodNm, "차량스케줄정보삭제");

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YfCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
				}
				else
				{
					//**********************************************************************************
					// 2.신규야드로 출발한 정보 취소처리
					szMsg="["+methodNm+"] 신규야드로 출발한 정보 취소처리 ============================";
					commUtils.printLog(logId, szMsg, "SL");

					//**********************************************************************************
					// 2-1.신규야드 작업예약삭제
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, delYDStkBook, logId, methodNm, "신규야드 작업예약삭제");

					//**********************************************************************************
					// 2-2.신규야드 작업예약재료삭제
					jrParam.setField("YD_WBOOK_ID", s_CARLD_WRK_BOOK_ID);
					commDao.update(jrParam, delYDStkBookDtl, logId, methodNm, "신규야드 작업예약삭제");

					//**********************************************************************************
					// 2-3.신규야드 예약위치정보삭제
					jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
					commDao.update(jrParam, delYDStkBookLoc, logId, methodNm, "신규야드 예약위치정보삭제");

					//**********************************************************************************
					// 2-4.차량스케줄정보삭제
					jrParam.setField("YD_CAR_SCH_ID", s_YD_CAR_SCH_ID);
					commDao.update(jrParam, delCarschID, logId, methodNm, "차량스케줄정보삭제");

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YfCarPointinforeg("1","",szTRN_EQP_CD,"","","","C", logId, methodNm);
				}
			}
			else
			{
				szMsg="["+methodNm+"] 기존 출발정보 유무 확인 - 운송장비코드 : " + szTRN_EQP_CD + " 로 출발정보를 찾지 못했습니다!";
				commUtils.printLog(logId, szMsg, "SL");
			}

			commUtils.printLog(logId, "=============소재차량출발취소 종료========", "SL");

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
	 * 복수상차 처리 로직
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public JDTORecord procCmbnCarldYn(JDTORecord rcvMsg) throws DAOException
	{
		String			methodNm	=  "복수상차 처리 로직  [YfCommCarMvSeEJB.procCmbnCarldYn] < " + rcvMsg.getResultMsg();
		String			logId		= rcvMsg.getResultCode();
		JDTORecord		jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

		JDTORecord		jrRtn		= JDTORecordFactory.getInstance().create();
		JDTORecordSet	rsResult2	= null;

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============복수상차 처리 로직 시작========", "SL");

			//수신 항목 값
			String sYD_GP			= commUtils.trim(rcvMsg.getFieldString("YD_GP"));			//야드구분
			String sSTL_NO			= commUtils.trim(rcvMsg.getFieldString("STL_NO"));			//재료번호
			String szCR_FRTOMOVE_GP	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP"));	//냉연이송구분
			String CmbnBayChk		= ""; //복수동상차구분

			jrParam.setField("STL_NO",	sSTL_NO);

			//차량정보 존재여부 체크
			JDTORecordSet jsCarSch = commDao.select(jrParam, getYdCarwbookid, logId, methodNm, "설비상태조회");

			if (jsCarSch.size() <= 0)
			{
				commUtils.printLog(logId, methodNm+  "TB_YD_CARSCH[차량스케줄이 존재 안 합니다", "SL");
				return  jrRtn ;
			}

			jsCarSch.first();
			JDTORecord jrCarSch = jsCarSch.getRecord();
			String cmbnCarldYn  	= commUtils.nvl(jrCarSch.getFieldString("CMBN_CARLD_YN"),"N");
			String ydCarWrkGp		= commUtils.nvl(jrCarSch.getFieldString("YD_CAR_WRK_GP"),"");
			String TelNo        	= commUtils.nvl(jrCarSch.getFieldString("TEL_NO"),"");
			String ydCarNo	    	= commUtils.nvl(jrCarSch.getFieldString("CAR_NO"),"");
			String ydCardNo	    	= commUtils.nvl(jrCarSch.getFieldString("CARD_NO"),"");
			String ydStackColGp 	= commUtils.nvl(jrCarSch.getFieldString("YD_CARLD_STOP_LOC"),"");
			String TransOrdDate		= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_DATE"),"");
			String TransOrdSeqNo	= commUtils.nvl(jrCarSch.getFieldString("TRANS_ORD_SEQNO"),"");
			String transEquipType	= commUtils.nvl(jrCarSch.getFieldString("TRANS_EQUIPMENT_TYPE"),"");
			String DriverName      	= commUtils.nvl(jrCarSch.getFieldString("DRIVER_NAME"),"");

			//조합상차(시작:S , 종료: E ,  단일상차: N )
			if ("S".equals(cmbnCarldYn))
			{
				commUtils.printLog(logId, methodNm+  "★★★★★ 복수 상차 인 경우 ★★★★★", "SL");

				//자동차량출발 처리
				jrParam.setField("TRANS_ORD_DATE",	TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
				jrParam.setField("YD_STK_COL_GP",	ydStackColGp);
				jrParam.setField("DEL_YN",			"Y");
				jrParam.setField("STOCK_MOVE_TERM",	"MG");
				jrParam.setField("MODIFIER",		"복수상차");

				//저장품종료처리
				EJBConnector ejbConn = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
				ejbConn.trx("updYfStockTrnsOrdTX", new Class[] { JDTORecord.class }, new Object[] { jrParam });

				commUtils.printLog(logId, methodNm + "★자동차량출발", "SL");

				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);		//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("CARD_NO", 		ydCardNo);
				recInTemp.setField("YD_STK_COL_GP", ydStackColGp);

				//자동차량출발 처리
				JDTORecord jrRtn1 = this.procFrtoCarLevWr(recInTemp);
				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);

				jrParam.setField("TRANS_ORD_DATE",	TransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
				jrParam.setField("YD_GP",			sYD_GP);
				jrParam.setField("STL_NO",			sSTL_NO);
				JDTORecordSet jsCmbn = commDao.select(jrParam, getYdCarYdCmbnCarldGP, logId, methodNm, "복수창고 구분");

				if(jsCmbn.size() <= 0)
				{
					commUtils.printLog(logId, methodNm+  "☆☆☆☆☆ 복수 창고가 아닌 경우 ☆☆☆☆☆", "SL");

					//복수동 CHECK
					JDTORecordSet jsCmbnBay = commDao.select(jrParam, getYdCarschCarNoCardNoTransNoCHK, logId, methodNm, "복수동 구분");

					if(jsCmbnBay.size() > 0)
					{
						jsCmbnBay.first();
						CmbnBayChk     = StringHelper.evl(jsCmbnBay.getRecord(0).getFieldString("CHK"), "");
					}

					//입동지시 호출/////////////////////////////////////////////////////////////////////////////////////
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("TC_CODE",			"DMYDR061");
					recInTemp.setField("YD_GP",				sYD_GP);
					recInTemp.setField("WORK_GP",			ydCarWrkGp);
					recInTemp.setField("TEL_NO",			TelNo);
					recInTemp.setField("TRANS_ORD_DT",		TransOrdDate);
					recInTemp.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
					recInTemp.setField("CAR_NO",			ydCarNo);
					recInTemp.setField("CARD_NO",			ydCardNo);
					recInTemp.setField("WAIT_ARR_DDTT",		YfCommUtils.getCurDate("yyyyMMddHHmmss"));
					recInTemp.setField("WAIT_ARR_GP",		"B");
					recInTemp.setField("DRIVER_NAME",		DriverName);

					commUtils.printLog(logId, methodNm+  "복수동 존재 수량 체크"+CmbnBayChk, "SL");

					if("0".equals(CmbnBayChk)|| "1".equals(CmbnBayChk))
					{
						recInTemp.setField("CMBN_CARLD_YN"	, "E");
					}
					else
					{
						recInTemp.setField("CMBN_CARLD_YN"	, "S");
					}

					EJBConnector ejbConn2 = new EJBConnector("default", "ACoilRcvL3BakSeEJB", this);
					JDTORecord jrRtn2 = (JDTORecord)ejbConn2.trx("procDMYDR061", new Class[] { JDTORecord.class }, new Object[] { recInTemp });

					jrRtn = commUtils.addSndData(jrRtn, jrRtn2);
				}
				else
				{
					commUtils.printLog(logId, methodNm+  "☆☆☆☆☆ 복수 창고 인 경우 ☆☆☆☆☆", "SL");

					jsCmbn.first();
					JDTORecord	jRCmbn 		= jsCmbn.getRecord();
					String		ydGpNext	= commUtils.nvl(jRCmbn.getFieldString("NEXT_YD_GP"), "");

					jrParam.setField("YD_GP",	ydGpNext);
					JDTORecordSet jsNextYdGp = commDao.select(jrParam, getYdCarYdCmbnCarSch, logId, methodNm, "타 창고");

					if (jsNextYdGp.size() <= 0)
					{
						commUtils.printLog(logId, methodNm+  " 다음 창고 도착가능 포인트가 존재 안 합니다.", "SL");
						return  jrRtn ;
					}

					jsNextYdGp.first();
					JDTORecord jrNextYdGp = jsNextYdGp.getRecord();
					String WlocCd 	= commUtils.nvl(jrNextYdGp.getFieldString("WLOC_CD"), "");
					String ydPntCd	= commUtils.nvl(jrNextYdGp.getFieldString("YD_PNT_CD"), "");

					//다음 창고 입동TC 전송//////////////////////////////////////////////////////////////////////////////
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name

					if("P".equals(transEquipType))
					{
						YdStockDao ydStockDao  = new YdStockDao();

						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						JDTORecord recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("TRANS_ORD_DATE",  TransOrdDate);
						recPara.setField("TRANS_ORD_SEQNO", TransOrdSeqNo);

						// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB*/
						//int nRet = ydStockDao.getYdStock(recPara, rsResult2, 731);
						int nRet = commDao.getYdStock(recPara, rsResult2, 731);

						if(nRet > 0)
						{
							rsResult2.first();
							JDTORecord recTemp	= rsResult2.getRecord();
							szCR_FRTOMOVE_GP	=  StringHelper.evl(recTemp.getFieldString("CR_FRTOMOVE_GP") , "");	//냉연이송구분
						}

						recInTemp.setField("JMS_TC_CD",				"YDDMR070");
						recInTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
						recInTemp.setField("TC_CODE",				"YDDMR070");
						recInTemp.setField("TC_CREATE_DDTT",		commUtils.getDateTime14());
						recInTemp.setField("CR_FRTOMOVE_GP",		szCR_FRTOMOVE_GP);
					}
					else
					{
						recInTemp.setField("JMS_TC_CD",				"YDDMR028");
						recInTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());
						recInTemp.setField("TC_CODE",				"YDDMR028");
						recInTemp.setField("TC_CREATE_DDTT",		commUtils.getDateTime14());
					}

					recInTemp.setField("TRANS_WORD_DATE",		TransOrdDate);
					recInTemp.setField("TRANS_WORD_SEQNO",		TransOrdSeqNo);
					recInTemp.setField("CARD_NO",				ydCardNo);
					recInTemp.setField("CAR_NO",				ydCarNo);
					recInTemp.setField("WLOC_CD",				WlocCd);
					recInTemp.setField("YD_PNT_CD",				ydPntCd);
					recInTemp.setField("BAYIN_DDTT",			commUtils.getDateTime14());
					//복수창고인 경우 다음 창고로 대기 하기 위해서 다음과 같이 전송 한다.
					recInTemp.setField("YD_CARPNT_CD",			"");
					recInTemp.setField("LOAN_PULLOUT_ABLE_YN",	"Y");

					jrRtn = commUtils.addSndData(jrRtn, recInTemp);
				}
			}
			else
			{
				commUtils.printLog(logId, methodNm+  "★★★★★ 복수 상차가 아님 ★★★★★", "SL");
			}

			commUtils.printLog(logId, "=============복수상차 처리 로직 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

		return jrRtn;
	}

	/**
	 * [A] 오퍼레이션명 : 코일이송차량출발실적 처리 procFrtoCarLevWr
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procFrtoCarLevWr(JDTORecord rcvMsg) throws JDTOException
	{
		String		methodNm	= "코일이송차량출발실적[YfCommCarMvSeEJB.procFrtoCarLevWr] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();
		String		szLogMsg	= "";
		String		szMsg		= "";
	    String		ydCarDiffYn	= "N"; //위치 동일차량여부

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

	    	//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============코일이송차량출발실적 시작========", "SL");

			commUtils.printParam(logId + "이송차량출발실적 처리 수신 ", rcvMsg);

			//수신 항목 값
			String ydCardNo		= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));			//카드번호
	    	String ydStkColGp	= commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_GP"));	//위치

	    	if("".equals(ydCardNo))
	    	{
	    		szLogMsg="카드번호가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("CARD_NO Error");
	    	}

	    	if("".equals(ydStkColGp))
	    	{
	    		szLogMsg="출발위치가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
	    	}

			jrParam.setField("YD_STK_COL_GP",	ydStkColGp);
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, getYdStkcolStackColGp, logId, methodNm, "적치열 조회");

	    	if(jsStkCol == null || jsStkCol.size() <= 0)
	    	{
				szLogMsg = methodNm + "발지위치["+ydStkColGp+"] 및 카드번호 ["+ydCardNo+"]가 이상 합니다.";
				commUtils.printLog(logId, szLogMsg, "SL");

				return jrRtn;
	    	}
	    	else
	    	{
		    	//열구분을 조회(도착지)
		    	String ydCarldLevLoc    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_GP"));
		    	String ydCarPntCdChk 	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CARPNT_CD"));
		    	String ydCardNoChk 	    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("CARD_NO"));
		    	String ydStackColActStat= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));

		    	if(!ydCardNoChk.equals(ydCardNo))
		    	{
		 		   /**********************************************************
					* 다른 차량이 존재 하는 경우
					**********************************************************/
		    		ydCarDiffYn = "Y";

		    		szMsg="["+methodNm+"] 해당 위치에 다른 차량이 존재 합니다. ";
		    		commUtils.printLog(logId, szMsg, "SL");
		    	}
		    	else
		    	{
		    		/**********************************************************
					* 동일차량존재
					**********************************************************/
		    		//사용불가로 되어있으면 상태값을 바꾸지 않는다
		    		if (!"N".equals(ydStackColActStat))
		    		{
		    			ydStackColActStat = "C";
		    		}

		    		//YFF1L001 생성이 동일차량존재 맨 하단에 있었으나 출발실적시 TB_YF_STKCOL초기화 후에 생성하면 값이 잘 안맞아서 초반에 전문생성함
		    		JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
    				sndL2Msg.setResultCode(logId);		//Log ID
    				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
    				sndL2Msg.setField("YD_INFO_SYNC_CD",	"3");	//야드정보동기화코드
    				sndL2Msg.setField("MSG_GP",				"I");	//전문구분
    				sndL2Msg.setField("YD_STK_COL_GP",		ydCarldLevLoc);
    				sndL2Msg.setField("YD_STK_BED_NO",		"");
    				commUtils.printParam(logId, sndL2Msg);

    				//전송 Data 생성
    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001", sndL2Msg));
    				szMsg="[" + methodNm + "] 저장품제원 : 코일야드L2 로 송신 열구분[" + ydCarldLevLoc + "] - 저장품위치제원 : 코일야드L2 로 송신 호출 성공"+jrRtn.size();
    				commUtils.printLog(logId, szMsg, "SL");

		    		/********************************
					 * 적치열 상태 비활성화등록
					 *******************************/
		    		szLogMsg = methodNm + "출발야드의 적치열["+ydCarldLevLoc+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szLogMsg, "SL");

					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("YD_STK_COL_GP",			ydCarldLevLoc);
			    	jrParam.setField("YD_STK_COL_ACTIVE_STAT",	ydStackColActStat);
			    	jrParam.setField("YD_CAR_USE_GP",			"");
			    	jrParam.setField("TRN_EQP_CD",				"");
			    	jrParam.setField("CAR_NO",					"");
			    	jrParam.setField("CARD_NO",					"");
			    	jrParam.setField("MODIFIER",				modifier);
			    	int intRtnVal = commDao.update(jrParam, updYdStkcol, logId, methodNm, "TB_YF_STKCOL 수정");

			    	if (intRtnVal <= 0)
					{
						szLogMsg = methodNm + "적치열[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");

						m_ctx.setRollbackOnly();
						throw new DAOException(szLogMsg);
					}

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YfCarPointinforeg("B", "", "", ydCarldLevLoc, "", "", ydStackColActStat, logId, methodNm);

					/********************************
					 * 적치베드 상태 비활성화등록
					 *******************************/
					jrParam.setField("YD_STK_BED_WT_MAX",		YfConstant.YD_STK_BED_WT_MAX_DEFAULT);
		    		jrParam.setField("YD_STK_BED_ACTIVE_STAT",	"L");
    				intRtnVal = commDao.update(jrParam, updYdStkbedYdStkColGp, logId, methodNm, "TB_YF_STKBED 활성상태수정(C)");

    				if (intRtnVal <= 0)
    				{
						szLogMsg = methodNm + "적치BED[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);
					}

					/********************************
					 * 적치단 상태 비활성화등록
					 *******************************/
    				jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"E");
		    		jrParam.setField("YD_STK_LYR_STAT",			"E");
    				intRtnVal = commDao.update(jrParam, updYdStkLyrYdStkColGpClear, logId, methodNm, "TB_YF_STKLYR 차량 적치단 정보 비활성화(C)");

    				if (intRtnVal <= 0)
    				{
						szLogMsg = methodNm + " 적치단[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");
						throw new DAOException(szLogMsg);
					}

		    	}

				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 차량스케줄 삭제
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		    	jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
		    	jrParam.setField("CARD_NO",				ydCardNo);
		    	jrParam.setField("YD_CARLD_STOP_LOC",	ydCarldLevLoc);
				jrParam.setField("MODIFIER",			modifier);
				JDTORecordSet jsCarResult = commDao.select(jrParam, getreadcarinfoOfwloc, logId, methodNm, "TB_YD_CARSCH 차량스케줄  조회");

		    	if (jsCarResult.size() <= 0 )
		    	{
					szLogMsg = "차량스케쥴 조회 오류 + (" + ydCarldLevLoc + ", " + ydCardNo + ", 'G')";
					commUtils.printLog(logId, szLogMsg, "SL");
		    	}
		    	else
		    	{
		    		jsCarResult.first();
					JDTORecord recGetVal = jsCarResult.getRecord();
					String szCarSchId= commUtils.trim(recGetVal.getFieldString("YD_CAR_SCH_ID"     ));

					jrParam.setField("YD_CAR_SCH_ID",	szCarSchId);
					jrParam.setField("DEL_YN",			"Y");
					commDao.update(jrParam, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 스케줄삭제");

					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					// 차량 이송재료 삭제
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					commDao.update(jrParam, updDelYnCarSchMtl, logId, methodNm, "TB_YD_CARFTMVMTL 차량스케줄재료 삭제");
		    	}

		    	commUtils.printLog(logId, szLogMsg+ "현재위치 복수동 입동지시 여부 N 이면 입동지시"+ ydCarDiffYn + ":" + ydCarPntCdChk , "SL");

				if ("N".equals(ydCarDiffYn))
				{
					/****************************************************************************************************
					 * 차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 ***************************************************************************************************/
					JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setResultCode(logId);	//Log ID
					jrTemp.setResultMsg(methodNm);	//Log Method Name
					jrTemp.setField("JMS_TC_CD",			"YFYFJ662");				//차량입동지시 요구 기존:YDYDJ662
					jrTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
					jrTemp.setField("YD_CARPNT_CD",			ydCarPntCdChk);				//입동포인트

					jrRtn = commUtils.addSndData(jrRtn, jrTemp);
		    	}
	    	}

	    	commUtils.printLog(logId, "=============코일이송차량출발실적 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

		}
	    catch (Exception e)
		{
			szLogMsg="출하차량출발실적 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			throw new DAOException(e);
		}

		return jrRtn;
	}

	/**
	 * [A] 오퍼레이션명 : 출하차량출발실적 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procOutCarLevWr(JDTORecord rcvMsg) throws JDTOException
	{
		String		methodNm	= "코일제품출하차량출발실적[YfCommCarMvSeEJB.procOutCarLevWr] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		String		szLogMsg	= "";
		String		szMsg		= "";
	    String		ydCarDiffYn	= "N"; //위치 동일차량여부

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============출하차량출발실적 처리 시작========", "SL");

			commUtils.printParam(logId + "출하차량출발실적 처리 수신 ", rcvMsg);

			//수신 항목 값
			String transOrdDate = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"));  //운송지시일자
	    	String transOrdSeqNo= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
	    	String ydCarNo  	= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));          //차량번호
	    	String ydCardNo     = commUtils.trim(rcvMsg.getFieldString("CARD_NO"));         //카드번호
	    	String sposWlocCd   = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));    //발지개소코드
	    	String sposYdPntCd  = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));  //발지포인트코드
	    	String ydCarPntCd   = commUtils.trim(rcvMsg.getFieldString("YD_CARPNT_CD"));   	//PALLET 출하 차량포인트

	    	if("".equals(transOrdDate))
	    	{
	    		szLogMsg="운송지시일자가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("TRANS_ORD_DATE Error");
	    	}

	    	if("".equals(transOrdSeqNo))
	    	{
	    		szLogMsg="운송지시순번이 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("TRANS_ORD_SEQNO Error");
	    	}

	    	if("".equals(ydCarNo))
	    	{
	    		szLogMsg="차량번호가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("CAR_NO Error");
	    	}

	    	if("".equals(sposWlocCd))
	    	{
	    		szLogMsg="발지개소코드가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
	    	}

	    	if("".equals(sposYdPntCd))
	    	{
	    		szLogMsg="발지포인트코드가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("SPOS_YD_PNT_CD Error");
	    	}

			/**********************************************************
			* 2. PALLET 출하 차량여부
			**********************************************************/
	    	if(!"".equals(ydCarPntCd))
	    	{
	    		jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YD_CARPNT_CD"	, ydCarPntCd);

				szMsg="["+methodNm+"] PALLET 출하 차량포인트 조회  시작";
				commUtils.printLog(logId, szMsg, "SL");

				JDTORecordSet loadCarPnt = commDao.select(jrParam, selectQueryId_0102, logId, methodNm, "PALLET 출하 차량포인트 조회");

				if (loadCarPnt.size() <= 0 )
				{
					szMsg="["+methodNm+"] PALLET 출하 차량포인트 조회 시  SELECT Error ::  DO NOT EXIST"  ;
					commUtils.printLog(logId, szMsg, "SL");

					return jrRtn ;
				}
				else
				{
					sposWlocCd 	= commUtils.trim(loadCarPnt.getRecord(0).getFieldString("WLOC_CD"));
					sposYdPntCd	= commUtils.trim(loadCarPnt.getRecord(0).getFieldString("YD_PNT_CD"));
				}
	    	}

	    	//발지위치정보로 출발위치 Clear
	    	//열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	//업데이트값이 없다면 그냥 종료
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
			jrParam.setField("WLOC_CD",   sposWlocCd);
			jrParam.setField("YD_PNT_CD", sposYdPntCd);
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, getYdStkcolWLocCdandPntCd, logId, methodNm, "적치열 조회");

	    	if(jsStkCol == null || jsStkCol.size() <= 0)
	    	{
				szLogMsg = methodNm + "발지개소["+sposWlocCd+"] 및 포인트 코드["+sposYdPntCd+"]가 타공정코드가 아니고 대기장입니다.";
				commUtils.printLog(logId, szLogMsg, "SL");

				return jrRtn ;
	    	}
	    	else
	    	{
		    	//열구분을 조회(도착지)
		    	String ydCarldLevLoc    	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_GP"));
		    	String ydCarPntCdChk 		= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CARPNT_CD"));
		    	String ydCarNoChk 	    	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("CAR_NO"));
		    	String ydStackColActStat	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));

		    	if(!ydCarNoChk.equals(ydCarNo))
		    	{
		 		   /**********************************************************
					* 다른 차량이 존재 하는 경우
					**********************************************************/
		    		szMsg="["+methodNm+"] 해당 위치에 다른 차량이 존재 합니다. 포인트 차량:"+ydCarNoChk + "  취소대상 차량:"+ ydCarNo;
		    		commUtils.printLog(logId, szMsg, "SL");
		    		ydCarDiffYn = "Y";
		    	}
		    	else
		    	{
		    		/**********************************************************
					* 동일차량존재
					**********************************************************/
    				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
    				sndL2Msg.setResultCode(logId);		//Log ID
    				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
    				sndL2Msg.setField("YD_INFO_SYNC_CD",	"3");	//야드정보동기화코드
    				sndL2Msg.setField("MSG_GP",				"I");	//전문구분
    				sndL2Msg.setField("YD_STK_COL_GP",		ydCarldLevLoc);
    				sndL2Msg.setField("YD_STK_BED_NO",		"");

    				//전송 Data 생성
    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001", sndL2Msg));

    				szMsg="[" + methodNm + "] 저장위치 : 코일야드L2 로 송신 열구분[" + ydCarldLevLoc + "] - 저장위치 제원 : 코일야드L2 로 송신 호출 "+jrRtn.size();
    				commUtils.printLog(logId, szMsg, "SL");

		    		//사용불가로 되어있으면 상태값을 바꾸지 않는다
    				//R:예약, L:사용중(도착불가), N:사용금지, C:비었음(도착가능)
		    		if(!"N".equals(ydStackColActStat))	
		    		{
		    			ydStackColActStat = "C";	//출하차량 출발실적이라 'C'로 변경
		    		}

		    		/********************************
					 * 적치열 상태 비활성화등록
					 *******************************/
		    		szLogMsg = methodNm + "출발야드의 적치열["+ydCarldLevLoc+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szLogMsg, "SL");

					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("YD_STK_COL_GP",			ydCarldLevLoc);
			    	jrParam.setField("YD_STK_COL_ACTIVE_STAT",	ydStackColActStat);
			    	jrParam.setField("YD_CAR_USE_GP",			"");
			    	jrParam.setField("TRN_EQP_CD",				"");
			    	jrParam.setField("CAR_NO",					"");
			    	jrParam.setField("CARD_NO",					"");
			    	jrParam.setField("MODIFIER",				modifier);
			    	int intRtnVal = commDao.update(jrParam, updYdStkcol, logId, methodNm, "TB_YF_STKCOL 등록");

			    	if(intRtnVal <= 0)
			    	{
						szLogMsg = methodNm + "적치열[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");

						m_ctx.setRollbackOnly();
						throw new DAOException(szLogMsg);
					}

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YfCarPointinforeg("B","","",ydCarldLevLoc,"","",ydStackColActStat,logId,methodNm);

					/********************************
					 * 적치베드 상태 비활성화등록
					 *******************************/
					jrParam.setField("YD_STK_BED_WT_MAX",		YfConstant.YD_STK_BED_WT_MAX_DEFAULT);
		    		jrParam.setField("YD_STK_BED_ACTIVE_STAT",	"L");
    				intRtnVal = commDao.update(jrParam, updYdStkbedYdStkColGp, logId, methodNm, "TB_YF_STKBED 수정");

    				if (intRtnVal <= 0)
    				{
						szLogMsg = methodNm + "적치BED[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");

						throw new DAOException(szLogMsg);
					}

					/********************************
					 * 적치단 상태 비활성화등록
					 *******************************/
    				jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"E");
		    		jrParam.setField("YD_STK_LYR_STAT",			"E");
    				intRtnVal = commDao.update(jrParam, updYdStkLyrYdStkColGpClear, logId, methodNm, "TB_YF_STKLYR 수정");

    				if (intRtnVal <= 0)
    				{
						szLogMsg = methodNm + " 적치단[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");

						throw new DAOException(szLogMsg);
					}
		    	}

				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 차량스케줄 삭제
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		    	if (ydCarNo.startsWith("ET"))
		    	{
					ydCarNo = "ET";
				}

		    	jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
		    	jrParam.setField("TRANS_ORD_DATE",	transOrdDate);
		    	jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
				jrParam.setField("CAR_NO",			ydCarNo);
		    	jrParam.setField("CARD_NO",			ydCardNo);
				jrParam.setField("MODIFIER",		modifier);
				JDTORecordSet jsCarResult = commDao.select(jrParam, getYdCarschTransDTSeq2, logId, methodNm, "차량스케줄  조회");

		    	if(jsCarResult.size() <= 0 )
		    	{
					szLogMsg = "차량스케쥴 조회 오류 + ("+transOrdDate+", "+ ydCarNo + ", " + ydCardNo + ", 'G')";
					commUtils.printLog(logId, szLogMsg, "SL");
					return jrRtn ;
		    	}
		    	else
		    	{
		    		jsCarResult.first();
					JDTORecord recGetVal = jsCarResult.getRecord();
					String szCarSchId= commUtils.trim(recGetVal.getFieldString("YD_CAR_SCH_ID"));

					jrParam.setField("YD_CAR_SCH_ID",	szCarSchId);
					jrParam.setField("DEL_YN",			"Y");
					commDao.update(jrParam, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 스케줄삭제");

					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					// 차량 이송재료 삭제
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					commDao.update(jrParam, updDelYnCarSchMtl, logId, methodNm, "TB_YD_CARFTMVMTL 차량스케줄재료 삭제");
		    	}

				if ("N".equals(ydCarDiffYn))
				{
					/****************************************************************************************************
					 * 차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 ***************************************************************************************************/
					JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setResultCode(logId);	//Log ID
					jrTemp.setResultMsg(methodNm);	//Log Method Name
					jrTemp.setField("JMS_TC_CD",			"YFYFJ662");				//차량입동지시 요구 기존:YDYDJ662
					jrTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());	//JMSTC생성일시
					jrTemp.setField("YD_CARPNT_CD",			ydCarPntCdChk);				//입동포인트

					jrRtn = commUtils.addSndData(jrRtn, jrTemp);
		    	}
	    	}

	    	commUtils.printLog(logId, "=============출하차량출발실적 처리 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");
		}
	    catch (Exception e)
	    {
			szLogMsg="출하차량출발실적 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			throw new DAOException(e);
		}

		return jrRtn;
	}

	/**
     * 오퍼레이션명 : 차량동간이적기능(신)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return intRtnVal
     * @throws
     */
    public JDTORecord procTraillerMoveSch(JDTORecord rcvMsg) throws DAOException
    {
    	String		methodNm	= "차량동간이적기능 [YfCommCarMvSeEJB.procTraillerMoveSch] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============차량동간이적기능 시작========", "SL");

			//수신 항목 값
			String ydSchCd      = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //야드구분
			String stlNo		= commUtils.trim(rcvMsg.getFieldString("STL_NO"));
			String ydDnWrLoc	= commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"));
			String ydUpWrLoc	= commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"));
			String ydCrnSchId	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"));
			String ydEqpId		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));

			commUtils.printLog(logId, "ydCrnSchId:"+ydCrnSchId, "SL");

        	// 상차작업 인 경우(스케줄 코드로 구분)
        	// 1. 하차지 차량  작업예약을 생성 한다.
        	// 2. 상차매수를 체크 해서 하차지 작업예약ID로 하차지 스케줄을 호출 한다.
			//
        	// 하차작업 인 경우
        	// 1. 하차매수를 체크 해서 상차지 작업예약id로 상차지 스케줄을 호출 한다.

        	//************************************************************************************************************
        	// 상차 작업
        	if("U".equals(ydSchCd.substring(6, 7)))
        	{
    			jrParam.setField("YD_SCH_CD",		ydSchCd);
    			jrParam.setField("STL_NO",			stlNo);
    			jrParam.setField("YD_DN_WR_LOC",	ydDnWrLoc);
    			jrParam.setField("YD_CRN_SCH_ID",	ydCrnSchId);
    			jrParam.setField("YD_STK_COL_GP",	ydDnWrLoc.substring(0, 6));
    			jrParam.setField("YD_EQP_ID",		ydEqpId);
    			JDTORecordSet jsCarUpSch = commDao.select(jrParam, getAxYML009CarSchLd2, logId, methodNm, "상차 차량스케줄 조회 ");

    			if(jsCarUpSch.size() > 0)
    			{
    				JDTORecord jrCarUpSch = jsCarUpSch.getRecord(0);

    				String ydCarSchId  	= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_SCH_ID"));
    				String CarNo 		= commUtils.trim(jrCarUpSch.getFieldString("CAR_NO"));
    				String CardNo 		= commUtils.trim(jrCarUpSch.getFieldString("CARD_NO"));
    				String ydCarPntCd	= commUtils.trim(jrCarUpSch.getFieldString("YD_CARPNT_CD"));
    				String TransOrdDate	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_DATE"));
    				String TransOrdSeqNo= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_SEQNO"));
    				String WlocCd 		= commUtils.trim(jrCarUpSch.getFieldString("WLOC_CD"));
    				String ydPndCd 		= commUtils.trim(jrCarUpSch.getFieldString("YD_PNT_CD"));
    				String carLdCmplYn 	= commUtils.trim(jrCarUpSch.getFieldString("CAR_LD_CMPL_YN")); 		//차량상차완료여부
    				String ydCarProgStat= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_PROG_STAT")); 	//차량진행상태
    				String sToBayGp 	= commUtils.trim(jrCarUpSch.getFieldString("TO_BAY_GP")); 			//TO동
    				String sToYdSchCd 	= commUtils.trim(jrCarUpSch.getFieldString("TO_YD_SCH_CD")); 	    //TO스케쥴 코드
    				String sPT_LOC   	= commUtils.trim(jrCarUpSch.getFieldString("CTS_RELAY_SADDLE")); 	//방향

    				String ydSchPriorNew = "";
    				String ydEqpIdNew    = "";
    				commUtils.printLog(logId, methodNm+  "★★★★★  권하차량스케쥴:" + ydCarSchId + " 차량상차완료:" + carLdCmplYn+ " ★★★★", "SL");

    				//차량이송재료(TB_YD_CARFTMVMTL) 상차 등록
    				jrParam = JDTORecordFactory.getInstance().create();
    				jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);
    				jrParam.setField("YD_CRN_SCH_ID",	ydCrnSchId);
    				jrParam.setField("STL_NO",			stlNo);
    				jrParam.setField("MODIFIER",		modifier);
    				jrParam.setField("YD_DN_WR_LOC",	ydDnWrLoc);	//야드권하실적위치
    				commDao.update(jrParam, insAxYML009CarMtlIns2, logId, methodNm, "상차 이송재료 등록 ");

    				//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
    				jrParam = JDTORecordFactory.getInstance().create();
    				jrParam.setResultCode(logId);	//Log ID
    				jrParam.setResultMsg(methodNm);	//Log Method Name

    				if ("Y".equals(carLdCmplYn))
    				{
    					//해당동만 완료이면 차량상태 완료처리함
    					jrParam.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					}
    				else
    				{
						jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					}

    				jrParam.setField("YD_STK_COL_GP",	ydDnWrLoc.substring(0, 6));
    				jrParam.setField("WR_DT",			commUtils.getDateTime14());
    				jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);
    				jrParam.setField("MODIFIER",		modifier);
    				commDao.update(jrParam, updAxYML009CarSchLd, logId, methodNm, " 상차 차량스케줄 수정 ");

    				commUtils.printLog(logId, methodNm+  "상차지 완료 여부: " + carLdCmplYn, "SL");

    				if ("Y".equals(carLdCmplYn))
    				{
    					// 하차지 작업 예약 생성
						JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
						recInTemp1.setField("YD_SCH_CD", sToYdSchCd);
						JDTORecordSet jsResult = commDao.select(recInTemp1, getYdSchrule, logId, methodNm, "스케줄 기준 조회");

						if(jsResult != null && jsResult.size() > 0)
						{
							ydSchPriorNew = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
							ydEqpIdNew    = jsResult.getRecord(0).getFieldString("YD_WRK_CRN");
						}

						JDTORecordSet jsWbookSearch = commDao.select(jrParam, getDest, logId, methodNm, "목적동 대상재 조회");	//2020.04.17 하차지 작업예약 생성시 뒤쪽부터 빼도록 쿼리 수정함

						if(jsWbookSearch.size() > 0)
						{
							//작업예약ID 조회
							String ydWbookIdNew = "";
							JDTORecord jrWbookSearch= JDTORecordFactory.getInstance().create();
							JDTORecord jrWbook    	= JDTORecordFactory.getInstance().create();
							JDTORecord jrWbookMtl 	= JDTORecordFactory.getInstance().create();

							for(int Loop_i = 1; Loop_i <= jsWbookSearch.size() ; Loop_i++)
							{
								jsWbookSearch.absolute(Loop_i);
								jrWbookSearch = jsWbookSearch.getRecord();

								String sStockId = commUtils.trim(jrWbookSearch.getFieldString("STL_NO"));

								ydWbookIdNew = commDao.getSeqId(logId, methodNm, "WrkBook");

								if(!"".equals(ydWbookIdNew))
								{
									jrWbook    = JDTORecordFactory.getInstance().create();
									//작업예약 등록
									jrWbook.setField("YD_WBOOK_ID",			ydWbookIdNew);	//야드작업예약ID
									jrWbook.setField("MODIFIER",			modifier);		//수정자
									jrWbook.setField("YD_GP",				YfConstant.YD_GP_1);	//야드구분
									jrWbook.setField("YD_BAY_GP",			sToBayGp);		//야드동구분
									jrWbook.setField("YD_SCH_CD",			sToYdSchCd);	//야드스케쥴코드
									jrWbook.setField("YD_SCH_PRIOR",		ydSchPriorNew);	//야드스케쥴우선순위
									jrWbook.setField("YD_SCH_PROG_STAT",	"W");			//야드스케쥴진행상태(스케줄수행대기)
									jrWbook.setField("YD_SCH_ST_GP",		"O");			//야드스케쥴기동구분(Manual)
									jrWbook.setField("YD_SCH_REQ_GP",		"M");			//야드스케쥴요청구분(이적)
									jrWbook.setField("YD_WRK_PLAN_CRN",		ydEqpIdNew);	//작업예약 크레인
									jrWbook.setField("CAR_NO",				CarNo);
									jrWbook.setField("CARD_NO",				CardNo);
									jrWbook.setField("YD_CAR_USE_GP",		"G");
									int ins_cnt = commDao.insert(jrWbook, insWrkBook2, logId, methodNm, "TB_YF_WRKBOOK");

									if(ins_cnt > 0)
									{
										jrWbookMtl = JDTORecordFactory.getInstance().create();
										//작업예약 등록
										jrWbookMtl.setField("YD_WBOOK_ID",		ydWbookIdNew);	//야드작업예약ID
										jrWbookMtl.setField("MODIFIER",			modifier);		//수정자
										jrWbookMtl.setField("STL_NO",			sStockId);
										jrWbookMtl.setField("YD_STK_COL_GP",	ydDnWrLoc.substring(0, 6));
										jrWbookMtl.setField("YD_STK_BED_NO",	ydDnWrLoc.substring(6, 8));

										commDao.insert(jrWbookMtl, insYfWrkBookMtl, logId, methodNm, "작업예약재료(TB_YF_WRKBOOKMTL) 생성");
									}
								}
							}
						}

		    			// 마지막 작업 마치고 차량 정보 초기화 호출
						JDTORecord jrSnd = JDTORecordFactory.getInstance().create();
						jrSnd.setResultCode(logId);		//Log ID
						jrSnd.setResultMsg(methodNm);	//Log Method Name
						jrSnd.setField("TRANS_ORD_DATE",	TransOrdDate);
						jrSnd.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
						jrSnd.setField("CAR_NO",			CarNo);
						jrSnd.setField("CARD_NO",			CardNo);
						jrSnd.setField("SPOS_WLOC_CD",		WlocCd);
						jrSnd.setField("SPOS_YD_PNT_CD",	ydPndCd);
						jrSnd.setField("YD_CARPNT_CD",		ydCarPntCd);
						jrSnd.setField("MODIFIER",			modifier);
						jrSnd.setField("WRK_GP",			"U");  //상차작업

						jrRtn = commUtils.addSndData(jrRtn, this.procYDOutCarLevWr(jrSnd));

						/**********************************************************
						* Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						jrParam.setField("YD_GP",		YfConstant.YD_GP_1);
						jrParam.setField("YD_BAY_GP",	sToBayGp);
						jrParam.setField("PT_LOC",		sPT_LOC);

						JDTORecordSet jsPntFrm = commDao.select(jrParam, getCarPntFrmYnTo, logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");

						if (jsPntFrm.size() > 0)
						{
							String ydFrmYn = commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_FRM_YN"));		//형상여부

							if ("N".equals(ydFrmYn))
							{
								// 형상이 없는 경우 도착 미리 기동처리 함
								jrParam.setField("JMS_TC_CD",		"F1YFL018");
								jrParam.setField("PT_LOAD_LOC",		commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_STK_COL_GP")));
								jrParam.setField("CAR_NO",			CardNo);
								jrParam.setField("CAR_UPDN_GP",		"2");		//하차
								jrParam.setField("MODIFIER",		modifier);

								EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
								JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("procF1YFL018", new Class[] { JDTORecord.class }, new Object[] { jrParam });

								jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
							}
						}
		    		}
				}
        	}
        	else if("L".equals(ydSchCd.substring(6, 7)))
        	{
        		// 하차 작업
    			jrParam.setResultCode(logId);	//Log ID
    			jrParam.setResultMsg(methodNm);	//Log Method Name
    			jrParam.setField("YD_SCH_CD",		ydSchCd);
    			jrParam.setField("STL_NO",			stlNo);
    			jrParam.setField("YD_DN_WR_LOC",	ydDnWrLoc);
    			jrParam.setField("YD_UP_WR_LOC",	ydUpWrLoc);
    			jrParam.setField("YD_CRN_SCH_ID",	ydCrnSchId);
    			jrParam.setField("YD_STK_COL_GP",	ydUpWrLoc.substring(0, 6));
    			jrParam.setField("YD_EQP_ID",		ydEqpId);
    			JDTORecordSet jsCarUpSch = commDao.select(jrParam, getAxYML009CarSchUd, logId, methodNm, "상차 차량스케줄 조회 ");

    			if(jsCarUpSch.size() > 0)
    			{
    				JDTORecord jrCarUpSch = jsCarUpSch.getRecord(0);

    				String carUdCmplYn 	= commUtils.trim(jrCarUpSch.getFieldString("CAR_UD_CMPL_YN")); 		//차량하차완료여부
    				String ydCarSchId  	= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_SCH_ID"));
    				String CarNo 		= commUtils.trim(jrCarUpSch.getFieldString("CAR_NO"));
    				String CardNo 		= commUtils.trim(jrCarUpSch.getFieldString("CARD_NO"));
    				String ydCarPntCd	= commUtils.trim(jrCarUpSch.getFieldString("YD_CARPNT_CD"));
    				String TransOrdDate	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_DATE"));
    				String TransOrdSeqNo= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_SEQNO"));
    				String WlocCd 		= commUtils.trim(jrCarUpSch.getFieldString("WLOC_CD"));
    				String ydPndCd 		= commUtils.trim(jrCarUpSch.getFieldString("YD_PNT_CD"));

    				commUtils.printLog(logId, methodNm+  "★★★★★  권하차량스케쥴:" + ydCarSchId + " 차량하차완료:" + carUdCmplYn+ " ★★★★", "SL");

    				//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
    				jrParam = JDTORecordFactory.getInstance().create();
    				jrParam.setResultCode(logId);	//Log ID
    				jrParam.setResultMsg(methodNm);	//Log Method Name

    				if("Y".equals(carUdCmplYn))
    				{
    					//해당동만 완료이면 차량상태 완료처리함
    					jrParam.setField("YD_CAR_PROG_STAT", "E"); //야드차량진행상태(하차완료)
					}
    				else
    				{
						jrParam.setField("YD_CAR_PROG_STAT", "D"); //야드차량진행상태(하차개시)
					}

    				jrParam.setField("YD_STK_COL_GP",	ydUpWrLoc.substring(0, 6));
    				jrParam.setField("WR_DT",			commUtils.getDateTime14());
    				jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);
    				jrParam.setField("MODIFIER",		modifier);
    				commDao.update(jrParam, updAxYML009CarSchUd, logId, methodNm, " 하차 차량스케줄 수정 ");

					commUtils.printLog(logId, methodNm+  "하차지 완료 여부: " + carUdCmplYn, "SL");

    				if ("Y".equals(carUdCmplYn))
    				{
    					// 마지막 작업 마치고 차량 정보 초기화 호출
						JDTORecord jrSnd = JDTORecordFactory.getInstance().create();
						jrSnd.setResultCode(logId);	//Log ID
						jrSnd.setResultMsg(methodNm);	//Log Method Name
						jrSnd.setField("TRANS_ORD_DATE",	TransOrdDate);
						jrSnd.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
						jrSnd.setField("CAR_NO",			CarNo);
						jrSnd.setField("CARD_NO",			CardNo);
						jrSnd.setField("SPOS_WLOC_CD",		WlocCd);
						jrSnd.setField("SPOS_YD_PNT_CD",	ydPndCd);
						jrSnd.setField("YD_CARPNT_CD",		ydCarPntCd);
						jrSnd.setField("MODIFIER",			modifier);
						jrSnd.setField("WRK_GP",			"D");  //하차작업
						jrRtn = commUtils.addSndData(jrRtn, this.procYDOutCarLevWr(jrSnd));

						/**********************************************************
						* 상차지 작업 예약 호출 Crane스케줄 호출
						*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
						*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
						**********************************************************/
						jrParam.setField("CARD_NO",	CardNo);
						JDTORecordSet jsPntFrm = commDao.select(jrParam, getCarPntFrmYnToUp, logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");

						if (jsPntFrm.size() > 0)
						{
							String ydFrmYn = commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_FRM_YN"));
							// 형상여부
							if ("N".equals(ydFrmYn))
							{
								// 형상이 없는 경우 도착 미리 기동처리 함
								jrParam.setField("JMS_TC_CD",		"F1YFL018" );
								jrParam.setField("PT_LOAD_LOC",		commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_STK_COL_GP")));
								jrParam.setField("CAR_NO",			CardNo );
								jrParam.setField("CAR_UPDN_GP",		"1");  //상차
								jrParam.setField("MODIFIER",		modifier );

								EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
								JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("procF1YFL018", new Class[] { JDTORecord.class }, new Object[] { jrParam });

								jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
							}
						}
		    		}
				}
        	}

        	commUtils.printLog(logId, "=============차량동간이적기능 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");

        	return jrRtn;
        }
		catch (Exception e)
        {
			throw new DAOException(e);
        }
    }

    /**
	 *      [A] 오퍼레이션명 : 동간이적차량 출발 처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procYDOutCarLevWr(JDTORecord rcvMsg) throws JDTOException
	{
		String		methodNm	= "동간이적차량 출발 처리 [YfCommCarMvSeEJB.procYDOutCarLevWr] < " + rcvMsg.getResultMsg();
		String		logId		= rcvMsg.getResultCode();
	    JDTORecord	jrParam		= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord

	    JDTORecord	jrRtn		= JDTORecordFactory.getInstance().create();

		String		szLogMsg	= "";
		String		szMsg		= "";
	    String		ydCarDiffYn	= "N"; //위치 동일차량여부

	    try
	    {
	    	commUtils.printLog(logId, methodNm, "S+");

			//기본 수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String tcCode  	= commUtils.trim(rcvMsg.getFieldString("TC_CODE"));		//TC_CODE
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)

			if ("".equals(modifier))
			{
				modifier = msgId;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅

			commUtils.printLog(logId, "=============동간이적차량 출발 시작========", "SL");

			//수신 항목 값
			String transOrdDate = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"));  //운송지시일자
	    	String transOrdSeqNo= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
	    	String ydCarNo  	= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));          //차량번호
	    	String ydCardNo     = commUtils.trim(rcvMsg.getFieldString("CARD_NO"));         //카드번호
	    	String sposWlocCd   = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));    //발지개소코드
	    	String sposYdPntCd  = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));  //발지포인트코드
	    	String sWrkGp   	= commUtils.trim(rcvMsg.getFieldString("WRK_GP"));      //'U' 상차작업 ,'D' 하차작업

	    	if("".equals(transOrdDate))
	    	{
	    		szLogMsg="운송지시일자가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("TRANS_ORD_DATE Error");
	    	}

	    	if("".equals(transOrdSeqNo))
	    	{
	    		szLogMsg="운송지시순번이 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("TRANS_ORD_SEQNO Error");
	    	}

	    	if("".equals(ydCarNo))
	    	{
	    		szLogMsg="차량번호가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("CAR_NO Error");
	    	}

	    	if("".equals(sposWlocCd))
	    	{
	    		szLogMsg="발지개소코드가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
	    	}

	    	if("".equals(sposYdPntCd))
	    	{
	    		szLogMsg="발지포인트코드가 없습니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				throw new DAOException("SPOS_YD_PNT_CD Error");
	    	}

	    	//발지위치정보로 출발위치 Clear
	    	//열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	//업데이트값이 없다면 그냥 종료
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
    		jrParam.setField("WLOC_CD",   sposWlocCd);
			jrParam.setField("YD_PNT_CD", sposYdPntCd);
	    	JDTORecordSet jsStkCol = commDao.select(jrParam, getYdStkcolWLocCdandPntCd, logId, methodNm, "적치열 조회");

	    	if(jsStkCol == null || jsStkCol.size() <= 0)
	    	{
				szLogMsg = methodNm + "발지개소["+sposWlocCd+"] 및 포인트 코드["+sposYdPntCd+"]가 타공정코드가 아니고 대기장입니다.";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jrRtn ;
	    	}
	    	else
	    	{
		    	//열구분을 조회(도착지)
		    	String ydCarldLevLoc    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_GP"));
		    	String ydCarPntCdChk 	= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CARPNT_CD"));
		    	String ydCarNoChk 	    = commUtils.trim(jsStkCol.getRecord(0).getFieldString("CAR_NO"));
		    	String ydStackColActStat= commUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"));

		    	if(!ydCarNoChk.equals(ydCarNo))
		    	{
		 		   /**********************************************************
					* 다른 차량이 존재 하는 경우
					**********************************************************/
		    		ydCarDiffYn = "Y";

		    		szMsg="["+methodNm+"] 해당 위치에 다른 차량이 존재 합니다. 포인트 차량:"+ydCarNoChk + "  취소대상 차량:"+ ydCarNo;
		    		commUtils.printLog(logId, szMsg, "SL");
		    	}
		    	else
		    	{
		    		/**********************************************************
					* 동일차량존재
					**********************************************************/
    				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
    				sndL2Msg.setResultCode(logId);		//Log ID
    				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
    				sndL2Msg.setField("YD_INFO_SYNC_CD",	"3"); //야드정보동기화코드
    				sndL2Msg.setField("MSG_GP",				"I"); //전문구분
    				sndL2Msg.setField("YD_STK_COL_GP",		ydCarldLevLoc);
    				sndL2Msg.setField("YD_STK_BED_NO",		"");

    				//전송 Data 생성
    				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001", sndL2Msg));

    				szMsg="[" + methodNm + "] 저장위치 : 코일야드L2 로 송신 열구분[" + ydCarldLevLoc + "] - 저장위치 제원 : 코일야드L2 로 송신 호출 "+jrRtn.size();
    				commUtils.printLog(logId, szMsg, "SL");

		    		//사용불가로 되어있으면 상태값을 바꾸지 않는다
		    		if(!"N".equals(ydStackColActStat))
		    		{
		    			ydStackColActStat = "C";
		    		}

		    		/********************************
					 * 적치열 상태 비활성화등록
					 *******************************/
		    		szLogMsg = methodNm + "출발야드의 적치열["+ydCarldLevLoc+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szLogMsg, "SL");

					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("YD_STK_COL_GP",			ydCarldLevLoc);
			    	jrParam.setField("YD_STK_COL_ACTIVE_STAT",	ydStackColActStat);
			    	jrParam.setField("YD_CAR_USE_GP",			"");
			    	jrParam.setField("TRN_EQP_CD",				"");
			    	jrParam.setField("CAR_NO",					"");
			    	jrParam.setField("CARD_NO",					"");
			    	jrParam.setField("MODIFIER",				modifier);
			    	int intRtnVal = commDao.update(jrParam, updYdStkcol, logId, methodNm, "TB_YF_STKCOL 등록");

			    	if (intRtnVal <= 0)
					{
						szLogMsg = methodNm + "적치열[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");

						throw new DAOException(szLogMsg);
					}

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					this.YfCarPointinforeg("B","","",ydCarldLevLoc,"","",ydStackColActStat,logId,methodNm);

					/********************************
					 * 적치베드 상태 비활성화등록
					 *******************************/
					jrParam.setField("YD_STK_BED_WT_MAX",		YfConstant.YD_STK_BED_WT_MAX_DEFAULT);
		    		jrParam.setField("YD_STK_BED_ACTIVE_STAT",	"L");
    				intRtnVal = commDao.update(jrParam, updYdStkbedYdStkColGp, logId, methodNm, "TB_YF_STKBED 비활성화");

    				if (intRtnVal <= 0)
    				{
						szLogMsg = methodNm + "적치BED[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");

						throw new DAOException(szLogMsg);
					}

					/********************************
					 * 적치단 상태 비활성화등록
					 *******************************/
    				jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"E");
		    		jrParam.setField("YD_STK_LYR_STAT",			"E");
    				intRtnVal = commDao.update(jrParam, updYdStkLyrYdStkColGpClear, logId, methodNm, "TB_YF_STKLYR 비활성화");

    				if (intRtnVal <= 0)
    				{
						szLogMsg = methodNm + " 적치단[" + ydCarldLevLoc + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szLogMsg, "SL");

						throw new DAOException(szLogMsg);
					}
		    	}

		    	if ("D".equals(sWrkGp))
		    	{
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					// 하차 작업인 경우 차량스케줄 삭제
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			    	jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
			    	jrParam.setField("TRANS_ORD_DATE",	transOrdDate);
			    	jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
					jrParam.setField("CAR_NO",			ydCarNo);
			    	jrParam.setField("CARD_NO",			ydCardNo);
					jrParam.setField("MODIFIER",		modifier);
			    	JDTORecordSet jsCarResult = commDao.select(jrParam, getYdCarschTransDTSeq2, logId, methodNm, "차량스케줄  조회");

			    	if (jsCarResult.size() <= 0 )
			    	{
						szLogMsg = "차량스케쥴 조회 오류 + ("+transOrdDate+", "+ ydCarNo + ", " + ydCardNo + ", 'G')";
						commUtils.printLog(logId, szLogMsg, "SL");

						return jrRtn;
			    	}
			    	else
			    	{
			    		jsCarResult.first();
						JDTORecord recGetVal = jsCarResult.getRecord();
						String szCarSchId= commUtils.trim(recGetVal.getFieldString("YD_CAR_SCH_ID"));

						jrParam.setField("YD_CAR_SCH_ID",	szCarSchId);
						jrParam.setField("DEL_YN",			"Y");
						commDao.update(jrParam, updYdCarsch, logId, methodNm, "TB_YD_CARSCH 차량 스케줄삭제");

						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						// 차량 이송재료 삭제
						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						commDao.update(jrParam, updDelYnCarSchMtl, logId, methodNm, "차량스케줄재료 삭제");
			    	}
		    	}

				if ("N".equals(ydCarDiffYn))
				{
					/****************************************************************************************************
					 * 차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 ***************************************************************************************************/
					JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setResultCode(logId);	//Log ID
					jrTemp.setResultMsg(methodNm);	//Log Method Name
					jrTemp.setField("JMS_TC_CD",			"YFYFJ662");				//차량입동지시 요구 기존:YDYDJ662
					jrTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14());	//JMSTC생성일시
					jrTemp.setField("YD_CARPNT_CD",			ydCarPntCdChk);				//입동포인트

					jrRtn = commUtils.addSndData(jrRtn, jrTemp);
		    	}
	    	}

	    	commUtils.printLog(logId, "=============동간이적차량 출발 종료========", "SL");

			commUtils.printLog(logId, methodNm, "S-");
		}
	    catch (Exception e)
	    {
			szLogMsg="동간이적출발실적 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			throw new DAOException(e);
		}

		return jrRtn;
	}

	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return void
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public void updYfStockTrnsOrdTX(JDTORecord jrParam) throws DAOException
	{
		String methodNm = "야드저장품 UPDATE[YfCommCarMvSeEJB.updYfStockTrnsOrdTX] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try
		{
			commDao.update(jrParam, updYfStockTrnsOrd, logId, methodNm, "저장품 수정");
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
	 * [A] 오퍼레이션명 : Pallet조회 (B) - 박판 슬라브 하차위치변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procChangeUdLoc(GridData gdReq) throws DAOException
	{
		String methodNm = "박판 슬라브 하차위치변경 [YfCommCarMvSeEJB.procChangeUdLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try
		{
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult 	= null;
			JDTORecordSet rsResult2 = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			String sSTL_NO;
			String sYD_STK_LYR_STAT;
			String sYD_STK_LYR_ACTIVE_STAT;
			String sYD_STK_LYR_NO;

			String sSchCode			= "";
			String sYD_GP			= "";
			String sBAY_GP			= "";
			String sYD_SCH_PRIOR	= "";
			String sYD_MULTI_WRK_YN	= "";
			String sYD_WRK_CRN		= "";
			String sYD_WBOOK_ID		= "";
			String sYD_WBOOK_ID_OLD	= "";
			String sYD_CAR_PROG_STAT= "";

			String sFROM_LOC		= gdReq.getParam("FROM_LOC");
			String sTO_LOC			= gdReq.getParam("TO_LOC");
			String sTRN_EQP_CD		= gdReq.getParam("TRN_EQP_CD");

			String sWLOC_CD			= gdReq.getParam("WLOC_CD");
			String sYD_PNT_CD		= gdReq.getParam("YD_PNT_CD");
			String sMOD_WLOC_CD		= gdReq.getParam("MOD_WLOC_CD");
			String sMOD_YD_PNT_CD	= gdReq.getParam("MOD_YD_PNT_CD");


			/**********************************************************
			* 1. TB_YD_CARPOINT 변경
			**********************************************************/
			//FROM 위치 Clear
			jrParam.setField("YD_STK_COL_ACT_STAT",	"C");	//R:예약, L:사용중(도착불가), N:사용금지, C:비었음(도착가능)
			jrParam.setField("TRN_EQP_CD",			"");
			jrParam.setField("YD_STK_COL_GP",		sFROM_LOC);
			commDao.update(jrParam, updYdCarpointByYdStkColGp, logId, methodNm, "TB_YD_CARPOINT 변경 - FROM위치");

			//TO 위치에 차량 설정
			jrParam.setField("YD_STK_COL_ACT_STAT", "L");	//R:예약, L:사용중(도착불가), N:사용금지, C:비었음(도착가능)
			jrParam.setField("TRN_EQP_CD",			sTRN_EQP_CD);
			jrParam.setField("YD_STK_COL_GP",		sTO_LOC);
			commDao.update(jrParam, updYdCarpointByYdStkColGp, logId, methodNm, "TB_YD_CARPOINT 변경 - TO위치");

			/**********************************************************
			* 2. TB_YF_STKCOL 변경
			**********************************************************/
			//FROM 위치 Clear
			jrParam.setField("YD_CAR_USE_GP",	"");
			jrParam.setField("TRN_EQP_CD",		"");
			jrParam.setField("CAR_NO",			"");
			jrParam.setField("CARD_NO",			"");
			jrParam.setField("WLOC_CD",			"D2Y43");
			jrParam.setField("YD_PNT_CD",		"1" + sFROM_LOC.substring(1,2) + sFROM_LOC.substring(4,6));
			commDao.update(jrParam, updateLayerstat_01, logId, methodNm, "TB_YF_STKCOL 변경 - FROM위치");

			//TO 위치에 차량 설정
			jrParam.setField("YD_CAR_USE_GP",	"L");
			jrParam.setField("TRN_EQP_CD",		sTRN_EQP_CD);
			jrParam.setField("CAR_NO",			"");
			jrParam.setField("CARD_NO",			"");
			jrParam.setField("WLOC_CD",			"D2Y43");
			jrParam.setField("YD_PNT_CD",		"1" + sTO_LOC.substring(1,2) + sTO_LOC.substring(4,6));
			commDao.update(jrParam, updateLayerstat_01, logId, methodNm, "TB_YF_STKCOL 변경 - TO위치");

			/**********************************************************
			* 3. TB_YF_STKLYR 변경
			**********************************************************/
			jrParam.setField("YD_STK_COL_GP",	sFROM_LOC);
			jrParam.setField("YD_STK_BED_NO",	"01");
			jrParam.setField("YD_STK_LYR_NO",	"%");
			rsResult = commDao.select(jrParam, getStockIdByLoc, logId, methodNm, "TB_YF_STKLYR 정보 조회 - FROM위치 ");

			for(int ii = 0; ii < rsResult.size() ; ii++)
			{
				sSTL_NO					= rsResult.getRecord(ii).getFieldString("STL_NO");
				sYD_STK_LYR_STAT		= rsResult.getRecord(ii).getFieldString("YD_STK_LYR_STAT");
				sYD_STK_LYR_ACTIVE_STAT	= rsResult.getRecord(ii).getFieldString("YD_STK_LYR_ACTIVE_STAT");
				sYD_STK_LYR_NO			= rsResult.getRecord(ii).getFieldString("YD_STK_LYR_NO");

				//FROM 위치 Clear
				jrParam.setField("STL_NO",					"");
				jrParam.setField("YD_STK_LYR_STAT",			"E");
				//jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"C");
				jrParam.setField("YD_STK_COL_GP",			sFROM_LOC);
				jrParam.setField("YD_STK_BED_NO",			"01");
				jrParam.setField("YD_STK_LYR_NO",			sYD_STK_LYR_NO);
				commDao.update(jrParam, updLyrByLoc, logId, methodNm, "TB_YF_STKLYR 변경 - FROM위치 ");

				//TO 위치 적치단 설정
				jrParam.setField("STL_NO",					sSTL_NO);
				jrParam.setField("YD_STK_LYR_STAT",			sYD_STK_LYR_STAT);
				jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"O");
				jrParam.setField("YD_STK_COL_GP",			sTO_LOC);
				jrParam.setField("YD_STK_BED_NO",			"01");
				jrParam.setField("YD_STK_LYR_NO",			sYD_STK_LYR_NO);
				commDao.update(jrParam, updLyrByLoc, logId, methodNm, "TB_YF_STKLYR 변경 - TO위치 ");

			}

			/**********************************************************
			* 변경할 작업예약 조회 및 스케줄 코드 기준 조회
			**********************************************************/
//			jrParam.setField("YD_SCH_CD",	sFROM_LOC.substring(0,2) + "PT02LM");
//			jrParam.setField("TRN_EQP_CD",	sTRN_EQP_CD);
//			rsResult = commDao.select(jrParam, getWbookIdBySchCd, logId, methodNm, "작업예약ID 조회 - FROM위치 ");

			//TO위치 스케줄 코드및 기준
			sYD_GP	= sTO_LOC.substring(0, 1);
			sBAY_GP	= sTO_LOC.substring(1, 2);

			//스케줄코드 생성  - 이송하차(L)
			sSchCode = sYD_GP + sBAY_GP + "PT02LM";

			//스케줄코드로 스케줄기준Table조회
//			jrParam.setField("YD_SCH_CD", sSchCode);
//			rsResult2 = commDao.select(jrParam, getYdSchrule, logId, methodNm, "스케줄 기준 조회");

//			if (rsResult2 != null && rsResult2.size() > 0)
//			{
//				sYD_WRK_CRN			= rsResult2.getRecord(0).getFieldString("YD_WRK_CRN");			//야드작업크레인
//				sYD_SCH_PRIOR		= rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR");	//야드스케쥴우선순위
//				sYD_MULTI_WRK_YN	= rsResult2.getRecord(0).getFieldString("YD_MULTI_WRK_YN");		//야드멀티작업여부
//			}
//			else
//			{
//				throw new Exception("스케쥴 코드 이상 : [" + sSchCode + "]");
//			}

//			if("Y".equals(sYD_MULTI_WRK_YN))
//			{
//				jrParam.setField("YD_SCH_ST_GP",	"N");	//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
//
//				if("A".equals(sBAY_GP))
//				{
//					if("0ACRA1".equals(sYD_WRK_CRN))
//					{
//						jrParam.setField("YD_WRK_PLAN_CRN",		"0ACRA1");	//야드작업계획크레인
//						jrParam.setField("YD_WRK_PLAN_CRN2",	"0ACRA1");	//야드작업계획크레인2
//					}
//					else
//					{
//						jrParam.setField("YD_WRK_PLAN_CRN",		"0ACRA1");	//야드작업계획크레인
//						jrParam.setField("YD_WRK_PLAN_CRN2",	"0ACRA1");	//야드작업계획크레인2
//					}
//				}
//				else if("B".equals(sBAY_GP))
//				{
//					if("0BCRB1".equals(sYD_WRK_CRN))
//					{
//						jrParam.setField("YD_WRK_PLAN_CRN",		"0BCRB1");	//야드작업계획크레인
//						jrParam.setField("YD_WRK_PLAN_CRN2",	"0BCRB1");	//야드작업계획크레인2
//					}
//					else
//					{
//						jrParam.setField("YD_WRK_PLAN_CRN",		"0BCRB1");	//야드작업계획크레인
//						jrParam.setField("YD_WRK_PLAN_CRN2",	"0BCRB1");	//야드작업계획크레인2
//					}
//				}
//				else
//				{
//					jrParam.setField("YD_WRK_PLAN_CRN",		sYD_WRK_CRN);	//야드작업계획크레인
//					jrParam.setField("YD_WRK_PLAN_CRN2",	"");			//야드작업계획크레인2
//				}
//			}
//			else
//			{
//				jrParam.setField("YD_SCH_ST_GP",		"M");			//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
//				jrParam.setField("YD_WRK_PLAN_CRN",		sYD_WRK_CRN);	//야드작업계획크레인
//				jrParam.setField("YD_WRK_PLAN_CRN2",	"");			//야드작업계획크레인2
//			}

//			for(int ii = 0; ii < rsResult.size() ; ii++)
//			{
//				sYD_WBOOK_ID = rsResult.getRecord(ii).getFieldString("YD_WBOOK_ID");
//
//				if(!sYD_WBOOK_ID_OLD.equals(sYD_WBOOK_ID))
//				{
//					/**********************************************************
//					* 4. TB_YF_WRKBOOK 변경
//					**********************************************************/
//					jrParam.setField("YD_BAY_GP",		sBAY_GP);		//동구분
//					jrParam.setField("YD_SCH_CD",		sSchCode);		//스케줄코드
//					jrParam.setField("YD_SCH_PRIOR",	sYD_SCH_PRIOR);	//야드스케쥴우선순위
//					jrParam.setField("YD_WBOOK_ID",		sYD_WBOOK_ID); 	//작업예약ID
//					commDao.update(jrParam, updTbYfWrkBook, logId, methodNm, "작업예약(TB_YF_WRKBOOK) 변경");
//
//					/**********************************************************
//					* 5. TB_YF_WRKBOOKMTL 변경
//					**********************************************************/
//					jrParam.setField("YD_STK_COL_GP",	sTO_LOC);
//					jrParam.setField("YD_WBOOK_ID",		sYD_WBOOK_ID); //작업예약ID
//					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updWrkBookMtlStackColGp", logId, methodNm, "작업예약재료(TB_YF_WRKBOOKMTL) 변경");
//				}
//
//				sYD_WBOOK_ID_OLD = sYD_WBOOK_ID;
//			}

			/**********************************************************
			* 6. TB_YD_CARSCH 변경
			**********************************************************/
			jrParam.setField("TRN_EQP_CD",	sTRN_EQP_CD);
			rsResult = commDao.select(jrParam, getCarSchByTrnEqpCd, logId, methodNm, "TB_YD_CARSCH 정보 조회 - TRN_EQP_CD 기준 ");

			if (rsResult != null && rsResult.size() > 0)
			{
				sYD_CAR_PROG_STAT	= rsResult.getRecord(0).getFieldString("YD_CAR_PROG_STAT");	//야드차량진행상태

				commUtils.printLog(logId, "운송설비코드(" + sTRN_EQP_CD + ") 의 야드차량진행상태 : " + sYD_CAR_PROG_STAT, "SL");
			}
			else
			{
				throw new Exception("TB_YD_CARSCH 이상 : [운송설비코드(" + sTRN_EQP_CD + ")]");
			}

			jrParam.setField("YD_PNT_CD",	"1" + sBAY_GP + sTO_LOC.substring(4,6));
			jrParam.setField("TO_LOC",		sTO_LOC);
			jrParam.setField("FROM_LOC",	sFROM_LOC);
			jrParam.setField("TRN_EQP_CD",	sTRN_EQP_CD);

			if("A".equals(sYD_CAR_PROG_STAT) || "B".equals(sYD_CAR_PROG_STAT) || "C".equals(sYD_CAR_PROG_STAT) || "D".equals(sYD_CAR_PROG_STAT) || "E".equals(sYD_CAR_PROG_STAT))
			{
				//영차로 와서 하차인경우
				commDao.update(jrParam, updYdCarSchYdPntCd3, logId, methodNm, "차량스케줄(TB_YD_CARSCH) 하차위치변경");
			}
			else
			{
				//공차로 와서 상차인경우
				commDao.update(jrParam, updYdCarSchYdPntCd4, logId, methodNm, "차량스케줄(TB_YD_CARSCH) 상차위치변경");
			}

			/**********************************************************
			* 7. 차량작업 예정정보(YFF0L008->YFF0L001->YFF0L002) 전문 생성...박판슬라브 L2없음...주석처리
			**********************************************************/
//			jrParam.setField("PT_LOAD_LOC",	sTO_LOC);
//			jrParam.setField("TC_CODE",		"F0YFL016");

	    	//차량예정정보
//		    EJBConnector ejbConnPT2 = new EJBConnector("default", "ASlabRcvL2SeEJB", this);
//		    jrRtn = (JDTORecord)ejbConnPT2.trx("rcvF0YFL016", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			/**********************************************************
			* 7. 저장위치제원(YFF0L001) 전문 생성...박판슬라브 L2없음...주석처리
			**********************************************************/
			//FROM 위치 저장위치제원  전송 Data 생성
//			jrParam.setField("YD_INFO_SYNC_CD",		"4");			//야드정보동기화코드
//			jrParam.setField("YD_STK_COL_GP",		sFROM_LOC);		//야드적치열구분
//			jrParam.setField("YD_STK_BED_NO",		"01");			//야드적치Bed번호
//			jrParam.setField("YD_CAR_ARRSTRT_STAT",	"S");			//A:도착, S:출발
//			jrParam.setField("YD_CAR_USE_GP",		"L");			//L:구내운송, G:출하차량
//			jrParam.setField("YD_EQP_WRK_STAT",		"L");			//U:공차, L:영차
//			jrParam.setField("TRN_EQP_CD",			sTRN_EQP_CD);	//운송장비코드
//			jrParam.setField("YD_CAR_AIM_YD_GP",	sYD_GP);
//			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF0L001_CarInfo", jrParam));

			/**********************************************************
			* 8. 하차위치변경(YDTSJ017) 전문 생성
			**********************************************************/
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();

			jrTemp.setResultCode(logId);	//Log ID
			jrTemp.setResultMsg(methodNm);	//Log Method Name
			jrTemp.setField("JMS_TC_CD",			"YDTSJ017");
			jrTemp.setField("JMS_TC_CREATE_DDTT",	commUtils.getDateTime14()); //JMSTC생성일시
			jrTemp.setField("TRN_EQP_CD",			sTRN_EQP_CD);
			jrTemp.setField("WLOC_CD",				sWLOC_CD);
			jrTemp.setField("YD_PNT_CD",			sYD_PNT_CD);
			jrTemp.setField("MOD_WLOC_CD",			sMOD_WLOC_CD);
			jrTemp.setField("MOD_YD_PNT_CD",		sMOD_YD_PNT_CD);

			//전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn,jrTemp);

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch(DAOException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}



}
