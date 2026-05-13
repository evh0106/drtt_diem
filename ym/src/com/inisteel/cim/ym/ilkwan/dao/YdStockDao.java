package com.inisteel.cim.ym.ilkwan.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.*;
import jspeed.base.util.StringHelper;

/**
 * [A] 클래스명 : 야드저장품 DAO
 * 
 */

public class YdStockDao {

	// Dao Name
	private String szDaoName = getClass().getName();

	/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String sTock_ID = StringHelper
				.evl(inRec.getFieldString("STOCK_ID"), "");
		String sTock_Move_Term = StringHelper.evl(
				inRec.getFieldString("STOCK_MOVE_TERM"), "");
		String smodifier = StringHelper.evl(inRec.getFieldString("MODIFIER"),
				"");
		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock";
			count = dao.updateData(sQueryId, new Object[] { sTock_Move_Term,
					smodifier, sTock_ID });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock

	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock2(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";
		String sDel_Yn = "";

		int count = 0;
		String sTock_ID = StringHelper
				.evl(inRec.getFieldString("STOCK_ID"), "");

		if (intGp == 0) {
			sDel_Yn = "Y";
		}

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock2";
			count = dao
					.updateData(sQueryId, new Object[] { sDel_Yn, sTock_ID });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock

	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock3(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String sTock_ID = StringHelper.evl(inRec.getFieldString("STOCK_ID") , "");
		String sTock_Move_Term = StringHelper.evl(inRec.getFieldString("STOCK_MOVE_TERM") , "");
		String smodifier = StringHelper.evl(inRec.getFieldString("MODIFIER") , "");
		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock3";
			count = dao.updateData(sQueryId, new Object[] { sTock_Move_Term,
					smodifier, sTock_ID });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock

	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock4(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String sTrans_Ord_Dt = StringHelper.evl(
				inRec.getFieldString("TRANS_ORD_DT"), "");
		String sTrans_Ord_Seqno = StringHelper.evl(
				inRec.getFieldString("TRANS_ORD_SEQNO"), "");
		String card_No = StringHelper.evl(inRec.getFieldString("CARD_NO"), "");
		String sTock_Move_Term = StringHelper.evl(
				inRec.getFieldString("STOCK_MOVE_TERM"), "");
		String sModifier = StringHelper.evl(inRec.getFieldString("MODIFIER"),
				"");

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock4";
			count = dao.updateData(sQueryId, new Object[] { sTock_Move_Term,
					card_No, sModifier, sTrans_Ord_Dt, sTrans_Ord_Seqno });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock4

	public int updYdStock5(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String sTock_ID = StringHelper.evl(inRec.getFieldString("STOCK_ID") , "");
		String sSHEAR_SUPPLY_SEQ = StringHelper.evl(inRec.getFieldString("SHEAR_SUPPLY_SEQ") , "");
		String sTRANS_ORD_DT = StringHelper.evl(inRec.getFieldString("TRANS_ORD_DT") , "");
		String sTRANS_ORD_SEQNO = StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO") , "");
		String sTock_Move_Term = StringHelper.evl(inRec.getFieldString("STOCK_MOVE_TERM") , "");
		String sMODIFIER = StringHelper.evl(inRec.getFieldString("MODIFIER") , "");

		String sCar_Kind = StringHelper.evl(inRec.getFieldString("CAR_KIND"), "");
		String sCAR_CARD_NO = StringHelper.evl(inRec.getFieldString("CAR_CARD_NO"), "");
		String sCAR_NO2 = StringHelper.evl(inRec.getFieldString("CAR_NO2"), "");
 
		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock57";
			count = dao.updateData(sQueryId, new Object[] {sSHEAR_SUPPLY_SEQ, sTock_Move_Term,
														   sTRANS_ORD_DT, sTRANS_ORD_SEQNO,
														   sTRANS_ORD_DT, sTRANS_ORD_SEQNO, 
														   sMODIFIER,sCar_Kind,sCAR_CARD_NO ,
														   sCAR_NO2 , sTock_ID });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock5

	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE(운송지시번호 삭제)
	 * 
	 * @param JDTORecord
	 *            inRec parameter record
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock6(JDTORecord inRec) throws DAOException, JDTOException {
		String sQueryId = "";

		int count = 0;
		String sTrans_Ord_Dt = StringHelper.evl(
				inRec.getFieldString("TRANS_ORD_DT"), "");
		String sTrans_Ord_Seqno = StringHelper.evl(
				inRec.getFieldString("TRANS_ORD_SEQNO"), "");
		String sTC_CODE = StringHelper.evl(inRec.getFieldString("TC_CODE"), "");

		String sTock_Move_Term = "";

		if (!YmCommonConst.DMYDR011.equals(sTC_CODE)) {
			sTock_Move_Term = StringHelper.evl(
					inRec.getFieldString("STOCK_MOVE_TERM"), "");
		}

		String sModifier = StringHelper.evl(inRec.getFieldString("MODIFIER"),
				"");

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock6";
			count = dao.updateData(sQueryId,
					new Object[] { sTock_Move_Term, sTock_Move_Term, sModifier,
							sTrans_Ord_Dt, sTrans_Ord_Seqno });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock6

	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE(카드번호 삭제)
	 * 
	 * @param JDTORecord
	 *            inRec parameter record
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock7(JDTORecord inRec) throws DAOException, JDTOException {
		String sQueryId = "";

		int count = 0;
		String sTRANS_ORD_DT = StringHelper.evl(
				inRec.getFieldString("TRANS_ORD_DT"), "");
		String sTRANS_ORD_SEQNO = StringHelper.evl(
				inRec.getFieldString("TRANS_ORD_SEQNO"), "");
		String sTock_Move_Term = StringHelper.evl(
				inRec.getFieldString("STOCK_MOVE_TERM"), "");
		String sModifier = StringHelper.evl(inRec.getFieldString("MODIFIER"),
				"");

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {
 
			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock7";
			count = dao.updateData(sQueryId, new Object[] { sTock_Move_Term,
					sModifier, sTRANS_ORD_DT, sTRANS_ORD_SEQNO });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock7

	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock8(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String sTock_ID = StringHelper
				.evl(inRec.getFieldString("STOCK_ID"), "");
		String sTrans_Ord_Dt = StringHelper.evl(
				inRec.getFieldString("TRANS_ORD_DT"), "");
		String sTrans_Ord_Seqno = StringHelper.evl(
				inRec.getFieldString("TRANS_ORD_SEQNO"), "");
		String card_No = StringHelper.evl(inRec.getFieldString("CARD_NO"), "");
		String sTock_Move_Term = StringHelper.evl(
				inRec.getFieldString("STOCK_MOVE_TERM"), "");
		String sModifier = StringHelper.evl(inRec.getFieldString("MODIFIER"),
				"");

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock8";
			count = dao.updateData(sQueryId, new Object[] { sTock_Move_Term,
					sTrans_Ord_Dt, sTrans_Ord_Seqno, card_No, sModifier,
					sTock_ID });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock8

	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock9(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String sArrWlocCd = StringHelper.evl(
				inRec.getFieldString("ARR_WLOC_CD"), "");
		String sArrYdPntCd = StringHelper.evl(
				inRec.getFieldString("ARR_YD_PNT_CD"), "");
		String card_No = StringHelper.evl(inRec.getFieldString("CARD_NO"), "");
		String sModifier = StringHelper.evl(inRec.getFieldString("MODIFIER"),
				"");

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock9";
			count = dao.updateData(sQueryId, new Object[] { card_No, sModifier,
					sArrWlocCd, sArrYdPntCd, card_No });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock9

	/**
	 * [A] 오퍼레이션명 : 검수테이블 등록 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock10(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String STL_NO = StringHelper.evl(inRec.getFieldString("STOCK_ID"), "");

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW";
			count = dao.updateData(sQueryId, new Object[] { STL_NO });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock10

	public int updYdStock11(JDTORecord inRec, int intGp) throws DAOException,
	JDTOException {
		String sQueryId = "";
		
		int count = 0;
		String sTock_ID = StringHelper.evl(inRec.getFieldString("STOCK_ID") , "");
		String sSHEAR_SUPPLY_SEQ = StringHelper.evl(inRec.getFieldString("SHEAR_SUPPLY_SEQ") , "");
		String sTRANS_ORD_DT = StringHelper.evl(inRec.getFieldString("TRANS_ORD_DT") , "");
		String sTRANS_ORD_SEQNO = StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO") , "");
		String sTock_Move_Term = StringHelper.evl(inRec.getFieldString("STOCK_MOVE_TERM") , "");
		String sMODIFIER = StringHelper.evl(inRec.getFieldString("MODIFIER") , "");
		
		String sCar_Kind = StringHelper.evl(inRec.getFieldString("CAR_KIND"), "");
		String sCAR_CARD_NO = StringHelper.evl(inRec.getFieldString("CAR_CARD_NO"), "");
		String sCAR_NO2 = StringHelper.evl(inRec.getFieldString("CAR_NO2"), "");
		String sCR_FRTOMOVE_GP = StringHelper.evl(inRec.getFieldString("CR_FRTOMOVE_GP"), "");
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		try {
		
			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock58";
			count = dao.updateData(sQueryId, new Object[] {sSHEAR_SUPPLY_SEQ, sTock_Move_Term,
														   sTRANS_ORD_DT, sTRANS_ORD_SEQNO,
														   sTRANS_ORD_DT, sTRANS_ORD_SEQNO, 
														   sMODIFIER,sCar_Kind,sCAR_CARD_NO ,
														   sCAR_NO2,sCR_FRTOMOVE_GP , sTock_ID });
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
		} // end of updYdStock11
	
	
	/**
	 * [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock12(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String sTock_ID = StringHelper.evl(inRec.getFieldString("STOCK_ID") , "");
		String sTock_Move_Term = StringHelper.evl(inRec.getFieldString("STOCK_MOVE_TERM") , "");
		String smodifier = StringHelper.evl(inRec.getFieldString("MODIFIER") , "");
		String sDel_yn = StringHelper.evl(inRec.getFieldString("DEL_YN") , "Y");
		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock12";
			count = dao.updateData(sQueryId, new Object[] { sTock_Move_Term,
					smodifier,sDel_yn, sTock_ID });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock12
	
	
	public int insYmStock(JDTORecord inRec, int intGp) throws DAOException,
	JDTOException {
		String sQueryId = "";
		
		int count = 0;
		String sTock_ID = StringHelper.evl(inRec.getFieldString("STOCK_ID") , "");
		String sSHEAR_SUPPLY_SEQ = StringHelper.evl(inRec.getFieldString("SHEAR_SUPPLY_SEQ") , "");
		String sTRANS_ORD_DT = StringHelper.evl(inRec.getFieldString("TRANS_ORD_DT") , "");
		String sTRANS_ORD_SEQNO = StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO") , "");
		String sTock_Move_Term = StringHelper.evl(inRec.getFieldString("STOCK_MOVE_TERM") , "");
		String sMODIFIER = StringHelper.evl(inRec.getFieldString("MODIFIER") , "");
		
		String sCar_Kind = StringHelper.evl(inRec.getFieldString("CAR_KIND"), "");
		String sCAR_CARD_NO = StringHelper.evl(inRec.getFieldString("CAR_CARD_NO"), "");
		String sCAR_NO2 = StringHelper.evl(inRec.getFieldString("CAR_NO2"), "");
		String sCR_FRTOMOVE_GP = StringHelper.evl(inRec.getFieldString("CR_FRTOMOVE_GP"), "");
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		try {
		
			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.insYmStock";
			count = dao.updateData(sQueryId, new Object[] {sTock_ID ,sMODIFIER,sMODIFIER, sSHEAR_SUPPLY_SEQ, sTock_Move_Term,
														   sTRANS_ORD_DT, sTRANS_ORD_SEQNO,
														   sTRANS_ORD_DT, sTRANS_ORD_SEQNO, 
														   sCar_Kind,sCAR_CARD_NO ,
														   sCAR_NO2,sCR_FRTOMOVE_GP   });
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
		} // end of insYmStock
} // end of class

