package com.inisteel.cim.yd.jjyd.session;


//UTIL IMPORT
import java.util.List;
import java.util.Vector;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.Logger;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydPrepMtlDao.YdPrepMtlDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkHistDao.YdWrkHistDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.dao.ymEtcDao.YmEtcDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.crn.CrnSchUtil;
import com.inisteel.cim.yd.common.util.loc.CoilYdToLocDcsnUtil;
import com.inisteel.cim.yd.common.util.loc.YdStkLocVO;
import com.inisteel.cim.yd.common.util.loc.YdToLocDcsnUtil;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.jjyd.dao.PlateReviseDao;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;
import com.inisteel.cim.yd.jplateyd.session.JPlateYdJspSeEJBBean;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO;
import com.inisteel.cim.yd.ydSch.CraneSch.CrnSchSeEJBBean;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;





/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 * 클래스명 : 소재야드 Session Class
 *
 * @ejb.bean name="PlateReviseSeEJB" jndi-name="PlateReviseSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */

public class PlateReviseSeEJBBean extends BaseSessionBean {
	
	private Logger  logger = new Logger("yd");
	
    private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	
	private YdTcConst ydTcConst =new YdTcConst();
	
	private YdDBAssist ydDBAssist =new YdDBAssist();
	
	private YdDelegate ydDelegate = new YdDelegate();
	
	YDDataUtil  yddatautil = new YDDataUtil();
	// [DEBUG] message flag
	private boolean bDebugFlag=true;
	

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	



	/**
	 * 스판과 배드에 할당된 단정보 가져오기
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord getpPlateYdCrnDownListPDADanList(JDTORecord inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		JDTORecord 		tmpPara		= null;
		JDTORecord 		rtnPara		= null;
		String			szComboList = "";
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_EQP_GP", 	inDto.getFieldString("YD_EQP_GP").trim());	/*스판*/
			recPara.setField("V_YD_BED_GP", 	inDto.getFieldString("YD_BED_GP").trim());	/*배드*/

			
			// DAO 호출
			outRecSet = dao.getpPlateYdCrnDownListPDADanList(recPara);
			
			if(outRecSet != null && outRecSet.size() >0){
				for(int i=0; i<outRecSet.size(); i++){
					tmpPara = outRecSet.getRecord(i);
					szComboList += tmpPara.getFieldString("CODE").trim() +"||";
					szComboList += tmpPara.getFieldString("NAME").trim() +"**";
				}
			}
			if(szComboList.length() > 3){
				szComboList = szComboList.substring(0, szComboList.length()-2);
			}
			
			rtnPara = JDTORecordFactory.getInstance().create();
			rtnPara.setField("COMBOLIST", szComboList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnPara;
	}
	
	/**
	 * 후판정정야드 관리자 적치확인 처리화면 (PDA) LIST조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecordSet getpPlateYdCrnDownListPDA(JDTORecord inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			
			recPara.setField("V_YD_STK_LYR_NO", 	inDto.getFieldString("YD_STK_LYR_NO").trim());	/*단*/
			recPara.setField("V_YD_BED_GP", 		inDto.getFieldString("YD_STK_BED_NO").trim());	/*배드*/
			recPara.setField("V_YD_EQP_GP", 		inDto.getFieldString("YD_EQP_GP").trim());	/*스판*/
			recPara.setField("V_STL_NO", 			inDto.getFieldString("STL_NO").trim());			/*재료번호*/
			
			// DAO 호출
			outRecSet = dao.getpPlateYdCrnDownListPDA(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	/**
	 *  임가공입고실적등록 (조회)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.19
	 */
	public JDTORecordSet getCoilFromToResultList(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_RENTPROC_COMCD", 	inDto.getParam("RENTPROC_COMCD").trim());	    /*임가공업체코드*/
			recPara.setField("V_FRTOMOVE_STAT_CD", 	inDto.getParam("FRTOMOVE_STAT_CD").trim());		/*이송상태코드*/
			recPara.setField("V_PAGE_NO", 	inDto.getParam("page_no").trim());		
			recPara.setField("V_ROW_CNT", 	inDto.getParam("rowCount").trim());	
			
			// DAO 호출
			outRecSet = dao.getCoilFromToResultList(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	

	
	/**
	 *  후판정정야드 크레인스케쥴(BookIn/ 이적)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	
	public String pPlateCrnSchBookInMm (JDTORecord[] inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		JDTORecord recSeq = null;
		JDTORecord recIn = null;
		JDTORecordSet rsOut = null;
		int intRtnVal = 0;
		
		JDTORecord recBedprior = null;
		JDTORecordSet rsBedpriorOut = null;
		
		JspCommonDAO 	dao 		= new JspCommonDAO();
		ymCommonDAO ymdao = ymCommonDAO.getInstance();
		JDTORecord 		recPara		= null;
		JDTORecord 		recPara2		= null;
		
		String szR_msg ="";
		String szLogMsg = "";
		String szMethodName="pPlateCrnSchBookInMm";
		String szOperationName	= "크레인스케쥴(BookIn/ 이적)";	
		String szMsg = "";
		
		String YD_EQP_GP = "";
		String YD_BAY_GP = "";
		String YD_BED_GP = "";
		String YD_STK_ACT_GP = "";
		String YD_BED_END_GP = "";
		String YD_BED_END_GP_COL = "";
		String s_YD_STK_BED_CR_GP = "";
		String s_YD_SCH_CD = "";
		String s_CLOSE_GP = "";
		String szMTL_STAT_CD = "";
		String Book_In_Loc = "";
		
		List FrtostlList = null;
		List RollmatList = null;
		List PlatematList = null;
		
		String currProg = "";
		JDTORecord    recInTemp         = null;
			
		
		try {
			
			
			
			
		for(int x=0;x<inDto.length;x++){
			

			
			s_YD_SCH_CD = yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"), "");
			
			//이적스케쥴 생성
			if(s_YD_SCH_CD.equals(YdConstant.SCH_CD_PPLATE_FROM_TO_LOCCHANGE)){
			
			
			
		//			 파라미터 셋팅 
					recPara		= JDTORecordFactory.getInstance().create(); 	
					
					
					recPara.setField("YD_STK_BED_USG_GP", yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_USG_GP"), ""));
			
					YD_EQP_GP = yddatautil.setDataDefault(inDto[x].getField("YD_TO_YD_GP"), "");  //야드
					YD_BAY_GP = getBayeqpGp_CODE2(yddatautil.setDataDefault(inDto[x].getField("YD_TO_YD_GP"), "")); //동
			
		
					recSeq = JDTORecordFactory.getInstance().create();
					recSeq.setField("YD_CRN_SCH_ID", "1");
		
					//크레인스케줄ID를 할당받는다
					rsOut = JDTORecordFactory.getInstance().createRecordSet("Temp");
					intRtnVal = this.pPlateGetYdCrnsch(recSeq, rsOut, 9);
		    		if(intRtnVal <= 0) {
						szMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new JDTOException("<pPlateCrnSchMain> " + szMsg);
		    		}
		    		
				
		    		//할당받은 크레인 스케줄 아이디로 Insert
		    		rsOut.first() ; 
		    		recSeq = JDTORecordFactory.getInstance().create();
					recSeq.setRecord(rsOut.getRecord());
					
		
					
					
				    recPara.setField("YD_CRN_SCH_ID",    recSeq.getFieldString("YD_CRN_SCH_ID"));			
				    recPara.setField("YD_EQP_GP", YD_EQP_GP);
				    recPara.setField("YD_EQP_ID", 	yddatautil.setDataDefault(inDto[x].getField("YD_WRK_CRN"), ""));			
				    recPara.setField("YD_GP", 	"P");
				    recPara.setField("YD_BAY_GP", YD_BAY_GP);
				    recPara.setField("YD_SCH_CD", s_YD_SCH_CD);
				    recPara.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_IDLE);
				    recPara.setField("YD_USER_ID", 	"YDSystem");
				    recPara.setField("YD_UP_WO_LOC", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));
				    recPara.setField("YD_UP_WO_LAYER", yddatautil.setDataDefault(inDto[x].getField("YD_FR_LAYER_NO"), ""));	
				    recPara.setField("YD_UP_WO_BED", yddatautil.setDataDefault(inDto[x].getField("YD_FR_BED_NO"), ""));
				    recPara.setField("YD_UP_WO_ACT_GP", yddatautil.setDataDefault(inDto[x].getField("YD_FR_CR_GP_CD"), ""));
				    recPara.setField("YD_DN_WO_LOC", "P"+YD_BAY_GP+YD_EQP_GP+yddatautil.setDataDefault(inDto[x].getField("YD_TO_BED_GP"), ""));
				    recPara.setField("YD_DN_WO_LAYER", "");	
				    recPara.setField("YD_DN_WO_BED", 	"");
				    recPara.setField("YD_DN_WO_ACT_GP", "");
				    recPara.setField("LYR_CLOSE_YN", "");
				    recPara.setField("STL_NO", yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				    
				    
					// 크레인 스케쥴 등록
					szMsg="["+ szOperationName +"] ----------------- 후판정정야드 크레인스케쥴(BookIn/ 이적) 크레인스케쥴 등록 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					dao.insertpPlateCrnSch(recPara);
					
					szMsg="["+ szOperationName +"] ----------------- 후판정정야드 크레인스케쥴(BookIn/ 이적) 크레인스케쥴 등록완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
					
					
		//			 크레인 스케쥴재료 등록
					szMsg="["+ szOperationName +"] ----------------- 후판정정야드 크레인스케쥴(BookIn/ 이적) 크레인스케쥴재료 등록 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					dao.insertpPlateCrnSchMtl(recPara);
					
					szMsg="["+ szOperationName +"] ----------------- 후판정정야드 크레인스케쥴(BookIn/ 이적) 크레인스케쥴재료 등록완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
								
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
					
		
					
		//			해당 스케쥴 권상위치에 재료 삭제
					recPara.setField("STACK_LAYER_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK_ABLE);
					dao.updatepPlateYdStkLayer_Up(recPara);
					
			
						szMsg= "크레인스케쥴(BookIn/ 이적)";
						
						
						szLogMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
						//return szMsg;
				  }
			
			
			//Book-In 스케쥴 생성시
			 else if(s_YD_SCH_CD.equals(YdConstant.SCH_CD_PPLATE_BOOK_IN))
			 {
					//			 파라미터 셋팅 
					recPara		= JDTORecordFactory.getInstance().create(); 	
								
					Book_In_Loc = yddatautil.setDataDefault(inDto[x].getField("YD_TO_BOOK_IN_LOC"), "");  //야드

					recSeq = JDTORecordFactory.getInstance().create();
					recSeq.setField("YD_CRN_SCH_ID", "1");
		
					//크레인스케줄ID를 할당받는다
					rsOut = JDTORecordFactory.getInstance().createRecordSet("Temp");
					intRtnVal = this.pPlateGetYdCrnsch(recSeq, rsOut, 9);
		    		if(intRtnVal <= 0) {
						szMsg = "크레인스케줄 Id를 생성하지 못했습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new JDTOException("<pPlateCrnSchMain> " + szMsg);
		    		}
		    		
				
		    		//할당받은 크레인 스케줄 아이디로 Insert
		    		rsOut.first() ; 
		    		recSeq = JDTORecordFactory.getInstance().create();
					recSeq.setRecord(rsOut.getRecord());
					
				    recPara.setField("YD_CRN_SCH_ID",    recSeq.getFieldString("YD_CRN_SCH_ID"));			
				    recPara.setField("YD_EQP_GP", YD_EQP_GP);
				    recPara.setField("YD_EQP_ID", 	yddatautil.setDataDefault(inDto[x].getField("YD_WRK_CRN"), ""));			
				    recPara.setField("YD_GP", 	"P");
				    recPara.setField("YD_BAY_GP", YD_BAY_GP);
				    recPara.setField("YD_SCH_CD", s_YD_SCH_CD);
				    recPara.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_IDLE);
				    recPara.setField("YD_USER_ID", 	"YDSystem");
				    recPara.setField("YD_UP_WO_LOC", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));
				    recPara.setField("YD_UP_WO_LAYER", yddatautil.setDataDefault(inDto[x].getField("YD_FR_LAYER_NO"), ""));	
				    recPara.setField("YD_UP_WO_BED", yddatautil.setDataDefault(inDto[x].getField("YD_FR_BED_NO"), ""));
				    recPara.setField("YD_UP_WO_ACT_GP", yddatautil.setDataDefault(inDto[x].getField("YD_FR_CR_GP_CD"), ""));
				    recPara.setField("YD_DN_WO_LOC", Book_In_Loc);
				    recPara.setField("YD_DN_WO_LAYER", "");	
				    recPara.setField("YD_DN_WO_BED", 	"");
				    recPara.setField("YD_DN_WO_ACT_GP", "");
				    recPara.setField("LYR_CLOSE_YN", "");
				    recPara.setField("STL_NO", yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				    
				    
					// 크레인 스케쥴 등록
			//		szMsg="["+ szOperationName +"] ----------------- 후판정정야드 크레인스케쥴(BookIn/ 이적) 크레인스케쥴 등록 ----------------";
			//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			//		dao.insertpPlateCrnSch(recPara);
					
			//		szMsg="["+ szOperationName +"] ----------------- 후판정정야드 크레인스케쥴(BookIn/ 이적) 크레인스케쥴 등록완료 ----------------";
			//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
					
					
		//			 크레인 스케쥴재료 등록
			//		szMsg="["+ szOperationName +"] ----------------- 후판정정야드 크레인스케쥴(BookIn/ 이적) 크레인스케쥴재료 등록 ----------------";
			//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			//		dao.insertpPlateCrnSchMtl(recPara);
					
			///		szMsg="["+ szOperationName +"] ----------------- 후판정정야드 크레인스케쥴(BookIn/ 이적) 크레인스케쥴재료 등록완료 ----------------";
			//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
								
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
					
		
					
		//			해당 스케쥴 권상위치에 재료삭제
					recPara.setField("STACK_LAYER_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK_ABLE);
					dao.updatepPlateYdStkLayer_Up(recPara);
					
			
						szMsg= "크레인스케쥴(BookIn/ 이적)";
						
						
						szLogMsg = "JSP-SESSION ["+ szOperationName +"] 끝";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
						//return szMsg;
						
						
						
						
						
						
						
				        String STL_NO = yddatautil.setDataDefault(inDto[x].getField("STL_NO"), "");
						
						int strlen = STL_NO.length();
						szMsg="["+ szOperationName +"] -----------------재료번호 길이----------------"+strlen;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						boolean isPlate = false;
						
						if(strlen == 8)
						{
							
//							ROLL_MAT 조회
							
							
							String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
							RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
				    		
				    		szMsg = "RollmatList조회 완료";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
				    		int iSeqCnt 	= RollmatList.size();
				    		for(int i=0; i < iSeqCnt ; i++){
				    			
				    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
				    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
				    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();

				    		}

							szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							
							
						}
						else if(strlen == 10)
						{
							if(isInteger(STL_NO.substring(8,10)))
							{
//								PLATE_MAT 조회
								String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListPlatematMtl";
								PlatematList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
					    		
					    		szMsg = "PlatematList조회 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
					    		int iSeqCnt 	= PlatematList.size();
					    		for(int i=0; i < iSeqCnt ; i++){
					    			
					    			JDTORecord PlatematStlrec = (JDTORecord)PlatematList.get(i);
					    			szMTL_STAT_CD = StringHelper.evl(PlatematStlrec.getFieldString("MTL_STAT_CD"),"").trim();
					    			currProg = StringHelper.evl(PlatematStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
				
					    		}

								szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (PLATE_MAT) 조회완료 ----------------";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
						
								
							}
							else
							{
//								//				ROLL_MAT 조회
								String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
								RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
					    		
					    		szMsg = "RollmatList조회 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
					    		int iSeqCnt 	= RollmatList.size();
					    		for(int i=0; i < iSeqCnt ; i++){
					    			
					    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
					    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
					    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
				
					    		}

								szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
								
								isPlate = true;
							}
							
						}

						
						szMsg="조업 위치변경정보  송신 YDPRJ004";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    		recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID",        "YDPRJ004");
						
						recInTemp.setField("PL_MPL_NO",     STL_NO);
						recInTemp.setField("PL_WR_PRSNT_PROC_CD", getprYDLOC_CODE(Book_In_Loc));
						recInTemp.setField("PL_WR_ELE_PROC_CD", currProg);											
						recInTemp.setField("MTL_STAT_CD",         szMTL_STAT_CD);
						recInTemp.setField("MODIFIER",        "YDPRJ004");
						
						if(!getprYDLOC_CODE(Book_In_Loc).equals("") && !currProg.equals(""))
						{
							if(!getprYDLOC_CODE(Book_In_Loc).equals(currProg))
							{
								if(isPlate){ 
									//ydDelegate.sendMsg(recInTemp);
								}
							}
						}

			 }
	
	      }
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		
		
		}
		return szR_msg;
	}	// end of pPlateCrnSchBookInMm
	
	
	
	
	
	
	/**
	 * 후판정정야드 스케쥴조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 
	 * @작성일 : 
	 */
	public GridData getpPlateYdSchList(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("YD_EQP_ID", 	inDto.getParam("YD_CRN_GP").trim());
			
			// DAO 호출
			outRecSet = dao.getpPlateYdSchList(recPara);
			
			if(outRecSet != null){
				rtnGrd = CmUtil.genGridData(inDto, outRecSet);
			}else{
				rtnGrd.setStatus("false");
				rtnGrd.addParam("ret", "-1");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnGrd;
	}
	
	
	
	/**
	 * 후판정정야드 모니터링 총량조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 
	 * @작성일 : 
	 */
	public GridData getpPlateMonitoring_Tot(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			
			// DAO 호출
			outRecSet = dao.getpPlateMonitoring_Tot(recPara);
			
			if(outRecSet != null){
				rtnGrd = CmUtil.genGridData(inDto, outRecSet);
			}else{
				rtnGrd.setStatus("false");
				rtnGrd.addParam("ret", "-1");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnGrd;
	}
	
	
	
	/**
	 * 후판정정야드 작업대상조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 
	 * @작성일 : 
	 */
	public GridData getpPlateYdCrnWorkList(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("YD_EQP_GP", 	getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim()));
			
			// DAO 호출
			outRecSet = dao.getpPlateYdCrnWorkList(recPara);
			
			if(outRecSet != null){
				rtnGrd = CmUtil.genGridData(inDto, outRecSet);
			}else{
				rtnGrd.setStatus("false");
				rtnGrd.addParam("ret", "-1");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnGrd;
	}
	
	
	
	
    /**
     * 오퍼레이션명 : 크레인스케줄 Select
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal 
     * @throws ● JDTOException
     */
    public int pPlateGetYdCrnsch (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMsg            = "";
    	String szMethodName     = "pPlateGetYdCrnsch";
    	String szOperationName          = "후판정정야드크레인스케줄 Select";
        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<pPlateGetYdCrnsch> getYdCrnsch data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				}else if(intRtnVal == -2) {
					szMsg="<pPlateGetYdCrnsch> getYdCrnsch parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}
			
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
        	szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal = 1;
    	
    }//end of pPlateGetYdCrnsch()
    
    
    
    
	/**
	 * 후판정정야드 스케쥴정보수정 (크레인)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateYdSchCrn(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
				
		PlateReviseDao 	dao 		= new PlateReviseDao();
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();		
		String szRcvMsg = "";
		
		
		szMsg        = "";
		szMethodName = "updatepPlateYdSchCrn";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [후판정정야드 스케쥴정보수정 (크레인)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
				recPara.setField("YD_EQP_ID", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
				intRtnVal = dao.updatepPlateYdSchCrn(recPara);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					
				} // end of if
				
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					
				} // end of if
				
				 				
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [후판정정야드 스케쥴정보수정 (크레인)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
	}	// end of updslabYdSchStdMgt
	
	
	
    
	/**
	 * 후판정정야드 스케쥴삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void deletepPlateYdSchCrn(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
				
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JspCommonDAO 	Commondao 		= new JspCommonDAO();
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
		JDTORecord recPara3 = JDTORecordFactory.getInstance().create();
		String szRcvMsg = "";
		
		
		szMsg        = "";
		szMethodName = "deletepPlateYdSchCrn";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [후판정정야드 스케쥴삭제] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
				recPara.setField("YD_USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
				recPara.setField("STL_NO", yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				recPara.setField("SCH_YN", "N");
				recPara.setField("YD_UP_WO_LOC", yddatautil.setDataDefault(inDto[x].getField("YD_UP_WO_LOC"), ""));
			    recPara.setField("YD_UP_WO_LAYER", yddatautil.setDataDefault(inDto[x].getField("YD_UP_WO_LAYER"), ""));	
			    recPara.setField("YD_UP_WO_BED", yddatautil.setDataDefault(inDto[x].getField("YD_UP_WO_BED"), ""));
			    recPara.setField("YD_DN_WO_LOC", "");
			    recPara.setField("YD_DN_WO_LAYER", "");	
			    recPara.setField("YD_DN_WO_BED", 	"");
			    
				 
				
				szMsg="----------------- 후판정정야드 스케쥴삭제  시작----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//크레인 스케쥴 삭제
				intRtnVal = dao.deletepPlateYdSchCrn(recPara);
				
				szMsg="----------------- 후판정정야드 스케쥴삭제 완료  ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				
				

				szMsg="----------------- 후판정정야드 스케쥴재료삭제  시작----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//크레인 스케쥴 삭제
				intRtnVal = dao.deletepPlateYdSchCrn_Stl(recPara);
				
				szMsg="----------------- 후판정정야드 스케쥴재료삭제  완료----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				
				
				
				//권상지시위치 적치중 상태로 업데이트
//				해당 스케쥴 권상위치에 재료 및 권상대기상태 업데이트

             	recPara.setField("STACK_LAYER_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK);	
				Commondao.updatepPlateYdStkLayer_Up(recPara);
				
				
//				 Bookout정보 스케쥴 등록유무 업데이트
				szMsg="----------------- 후판정정야드  Bookout정보 스케쥴 등록유무 업데이트 ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				Commondao.updatepPlateCrnSchMtl(recPara);
				
				szMsg="----------------- 후판정정야드  Bookout정보 스케쥴 등록유무 업데이트완료 ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				

				 				
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [후판정정야드 스케쥴삭제] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
	}	// end of updslabYdSchStdMgt
    
	
	
	
    /**
     * 오퍼레이션명 : 후판정정야드 빈베드 조회
     *  
     * @param  ● msgRecord, outRecSet, intGp
     * @return ● intRtnVal 
     * @throws ● JDTOException
     */
    public int pPlateYdGetBedempty(JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
    	
    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMsg            = "";
    	String szMethodName     = "pPlateYdGetBedempty";
    	String szOperationName          = "후판정정야드 빈베드 조회";
        try{
        	
	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<pPlateYdGetBedempty> getYdCrnsch data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				}else if(intRtnVal == -2) {
					szMsg="<pPlateYdGetBedempty> getYdCrnsch parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}
			
	        outRecSet.addAll(getRecSet);

        }catch(Exception e){
        	szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
        }//end of try~catch

        return intRtnVal = 1;
    	
    }//end of pPlateYdGetBedempty()
    
	private String getYdeqpGp_CODE(String YdeqpGp){
		
		String YdeqpGp_CD	= "";
		
		if(YdeqpGp.equals("극후물")){ 
			YdeqpGp_CD = "01";
		}else if(YdeqpGp.equals("냉각대출측")){
			YdeqpGp_CD = "02";
		}else if(YdeqpGp.equals("#1GAS")){
			YdeqpGp_CD = "03";
		}else if(YdeqpGp.equals("#1전단")){
			YdeqpGp_CD = "04";
		}else if(YdeqpGp.equals("#2전단")){
			YdeqpGp_CD = "05";
		}else if(YdeqpGp.equals("#2GAS")){
			YdeqpGp_CD = "06";
		}else if(YdeqpGp.equals("극후물GAS")){
			YdeqpGp_CD = "07";
		}else if(YdeqpGp.equals("전단GAS")){
			YdeqpGp_CD = "08";
		}else if(YdeqpGp.equals("냉간교정야드")){
			YdeqpGp_CD = "09";
		}else if(YdeqpGp.equals("보수장")){
			YdeqpGp_CD = "10";
		}else if(YdeqpGp.equals("보수장GAS")){
			YdeqpGp_CD = "11";
		}else if(YdeqpGp.equals("열처리")){ 
			YdeqpGp_CD = "12";
		}else if(YdeqpGp.equals("ShotBlast")){
			YdeqpGp_CD = "13";
		}else if(YdeqpGp.equals("열처리GAS")){
			YdeqpGp_CD = "14";
		}else if(YdeqpGp.equals("제품창고#1GAS")){
			YdeqpGp_CD = "15";
		}else if(YdeqpGp.equals("제품창고#2GAS")){
			YdeqpGp_CD = "16";
		}else if(YdeqpGp.equals("제품창고#3GAS")){
			YdeqpGp_CD = "17";
		}else if(YdeqpGp.equals("제품창고#4GAS")){
			YdeqpGp_CD = "18";
		}
		
		return YdeqpGp_CD;
	}
	
	
	private String getBayeqpGp_CODE(String YdeqpGp){
		
		String BayGp_CD	= "";

		if(YdeqpGp.equals("#1GAS")||YdeqpGp.equals("#1전단")||YdeqpGp.equals("#2전단")||YdeqpGp.equals("#2GAS")||YdeqpGp.equals("극후물")){
			BayGp_CD = "A";
		}else if(YdeqpGp.equals("냉간교정야드")||YdeqpGp.equals("보수장")){
			BayGp_CD = "B";
		}else{ 
			BayGp_CD = "C";
		}
		
		return BayGp_CD;
	}
	
	private String getBayeqpGp_CODE2(String YdeqpGp){
		
		String BayGp_CD	= "";

		if(YdeqpGp.equals("01")||YdeqpGp.equals("02")||YdeqpGp.equals("03")||YdeqpGp.equals("04")||YdeqpGp.equals("05")){
			BayGp_CD = "A";
		}else if(YdeqpGp.equals("06")||YdeqpGp.equals("07")){
			BayGp_CD = "B";
		}else{ 
			BayGp_CD = "C";
		}
		
		return BayGp_CD;
	}
	
	
	
	/**
	 * 크레인작업실적등록(차상국) - 크레인번호로 판번호(재료번호) 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public JDTORecordSet getCrnNoAndStlNo(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_EQP_ID", 	inDto.getParam("YD_EQP_ID").trim());	/*크레인호기*/	
			
			// DAO 호출
			outRecSet = dao.getCrnNoAndStlNo(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public JDTORecordSet getCrnUpDownLocList(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_STL_NO", 	inDto.getParam("STL_NO").trim());	/*재료번호*/	
			
			// DAO 호출
			outRecSet = dao.getCrnUpDownLocList(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public JDTORecordSet getCrnUpDownList(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_STL_NO", 	inDto.getParam("STL_NO").trim());	/*재료번호*/	
			
			// DAO 호출
			outRecSet = dao.getCrnUpDownList(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public JDTORecordSet getCrnUpDownBedList(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			String s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_STL_NO", 	inDto.getParam("STL_NO").trim());	/*재료번호*/	
			recPara.setField("V_YD_EQP_GP", 	s_YD_EQP_GP);	/*재료번호*/	
			
			// DAO 호출
			outRecSet = dao.getCrnUpDownBedList(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public JDTORecordSet getCrnUpDownBedList2(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			String s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 				
			recPara.setField("V_YD_EQP_GP", 	s_YD_EQP_GP);	/*재료번호*/	
			
			// DAO 호출
			outRecSet = dao.getCrnUpDownBedList2(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	
	/**
	 * 크레인작업실적등록(차상국) - 권상위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecordSet
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.26
	 */
	public JDTORecordSet getCrnUpLocList(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("TD_YD_UP_WO_LOC", 	inDto.getParam("TD_YD_UP_WO_LOC").trim());	
			recPara.setField("TD_YD_UP_WO_LAYER", 	inDto.getParam("TD_YD_UP_WO_LAYER").trim());	/*재료번호*/	
			
			// DAO 호출
			outRecSet = dao.getCrnUpLocList(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	

	/**
	 * 크레인작업실적등록(차상국) - 권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecordSet
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.26
	 */
	public JDTORecordSet getCrnDownLocList(GridData inDto) {  
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("TD_YD_DN_WO_LOC", 	inDto.getParam("TD_YD_DN_WO_LOC").trim());	
			//recPara.setField("TD_YD_DN_WO_LAYER", 	inDto.getParam("TD_YD_DN_WO_LAYER").trim());	/*재료번호*/	
			
			// DAO 호출
			outRecSet = dao.getCrnDownLocList(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	/**
	 * 후판정정야드 별 소재현황 1 - 목록조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.24
	 */
	public JDTORecordSet getPlateYdlocList_1(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_GP", 			inDto.getParam("YD_GP").trim());			/*야드구분*/	
			recPara.setField("V_YD_BED_GP", 		inDto.getParam("YD_BED_GP").trim());		/*배드번호*/	
			recPara.setField("V_PAGE_NO", 			inDto.getParam("page_no").trim());		
			recPara.setField("V_ROW_CNT", 			inDto.getParam("rowCount").trim());	
			// DAO 호출
			outRecSet = dao.getPlateYdlocList_1(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	/**
	 * 후판정정야드 별 소재현황 2 - 그래픽 표현 목록조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.24
	 */
	public JDTORecordSet getPlateYdlocList_2(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_GP", 			inDto.getParam("YD_GP").trim());			/*야드구분*/	
			recPara.setField("V_YD_BED_GP", 		inDto.getParam("YD_STK_BED_NO").trim());	/*배드번호*/	

			// DAO 호출
			outRecSet = dao.getPlateYdlocList_2(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	
	/**
	 *  후판정정야드 북아웃코드 (조회)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.25
	 */
	public JDTORecordSet getpPlateYdBookoutCodeList(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_RENTPROC_COMCD", 	"");	    /*임가공업체코드*/
			
			// DAO 호출 
			outRecSet = dao.getpPlateYdBookoutCodeList(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	
	
	   
	/**
	 * 후판정정야드 북아웃코드 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void delYdBookoutCode(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
				
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JspCommonDAO 	Commondao 		= new JspCommonDAO();
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
		JDTORecord recPara3 = JDTORecordFactory.getInstance().create();
		String szRcvMsg = "";
		
		
		szMsg        = "";
		szMethodName = "delYdBookoutCode";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [후판정정야드 북아웃코드 삭제] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("BOOK_OUT_CODE", yddatautil.setDataDefault(inDto[x].getField("BOOK_OUT_CODE"), ""));
				recPara.setField("YD_USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));

				
				szMsg="----------------- 후판정정야드 북아웃코드 삭제  시작----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//크레인 스케쥴 삭제
				intRtnVal = dao.delYdBookoutCode(recPara);
				
				szMsg="----------------- 후판정정야드 북아웃코드 삭제 완료  ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				

			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [후판정정야드 북아웃코드 삭제] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
	}	// end of updslabYdSchStdMgt
	


	/**
	 * 후판정정야드 북아웃코드 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updateYdBookoutCode(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
				
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JspCommonDAO 	Commondao 		= new JspCommonDAO();
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
		JDTORecord recPara3 = JDTORecordFactory.getInstance().create();
		String szRcvMsg = "";
		
		
		szMsg        = "";
		szMethodName = "updateYdBookoutCode";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [후판정정야드 북아웃코드 수정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("BOOK_OUT_CODE", yddatautil.setDataDefault(inDto[x].getField("BOOK_OUT_CODE"), ""));
				recPara.setField("BOOK_OUT_CODE_NAME", yddatautil.setDataDefault(inDto[x].getField("BOOK_OUT_CODE_NAME"), ""));
				recPara.setField("YD_PRIOR", yddatautil.setDataDefault(inDto[x].getField("YD_PRIOR"), ""));
				recPara.setField("YD_USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
	
			    
				 
				
				szMsg="----------------- 후판정정야드 북아웃코드 수정  시작----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//크레인 스케쥴 삭제
				intRtnVal = dao.updateYdBookoutCode(recPara);
				
				szMsg="----------------- 후판정정야드 북아웃코드 수정 완료  ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [후판정정야드 북아웃코드 수정] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
	}	// end of updslabYdSchStdMgt
	
	
	
	/**
	 * 후판정정야드 북아웃코드 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void inspPlateYdBookoutCode(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
				
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JspCommonDAO 	Commondao 		= new JspCommonDAO();
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
		JDTORecord recPara3 = JDTORecordFactory.getInstance().create();
		String szRcvMsg = "";
		
		
		szMsg        = "";
		szMethodName = "inspPlateYdBookoutCode";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [후판정정야드 북아웃코드 등록] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("BOOK_OUT_CODE", yddatautil.setDataDefault(inDto[x].getField("BOOK_OUT_CODE"), ""));
				recPara.setField("BOOK_OUT_CODE_NAME", yddatautil.setDataDefault(inDto[x].getField("BOOK_OUT_CODE_NAME"), ""));
				recPara.setField("YD_USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
				recPara.setField("YD_PRIOR", yddatautil.setDataDefault(inDto[x].getField("YD_PRIOR"), ""));
				recPara.setField("DEL_YN","N");
	

				szMsg="----------------- 후판정정야드 북아웃코드 등록시작----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				intRtnVal = dao.inspPlateYdBookoutCode(recPara);
				
				szMsg="----------------- 후판정정야드 북아웃코드 등록 완료  ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [후판정정야드 북아웃코드 등록] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
	}	// end of updslabYdSchStdMgt
	
	
	
	
	/**
	 *  후판정정야드 권상실적처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	
	public String pPlateYdCrnUpWrk(JDTORecord[] inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		JDTORecord recSeq = null;
		JDTORecord recIn = null;
		JDTORecordSet rsOut = null;
		int intRtnVal = 0;
		
		JDTORecord recBedprior = null;
		JDTORecordSet rsBedpriorOut = null;
		
		JspCommonDAO 	dao 		= new JspCommonDAO();
		PlateReviseDao 	plateRevisedao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		JDTORecord 		recPara2		= null;
		
		String szR_msg ="";
		String szLogMsg = "";
		String szMethodName="pPlateYdCrnUpWrk";
		String szOperationName	= "후판정정야드 크레인스케쥴 권상실적처리";	
		String szMsg = "";
		
		String YD_EQP_GP = "";
		String YD_BAY_GP = "";
		String YD_BED_GP = "";
		String YD_STK_ACT_GP = "";
		String YD_BED_END_GP = "";
		String YD_BED_END_GP_COL = "";
		String s_YD_STK_BED_CR_GP = "";
		String s_YD_SCH_CD = "";
		String s_CLOSE_GP = "";
			
		
		try {
			
			szMsg = "JSP-SESSION [후판정정야드 크레인스케쥴 권상실적처리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				//권상지시위치 적치가능 상태로 업데이트
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
				recPara.setField("YD_USER_ID", yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"), ""));
				recPara.setField("STL_NO", "");
				recPara.setField("YD_DN_WO_LOC", yddatautil.setDataDefault(inDto[x].getField("YD_UP_WO_LOC"), ""));
				recPara.setField("YD_DN_WO_LAYER", yddatautil.setDataDefault(inDto[x].getField("YD_UP_WO_LAYER"), ""));	
			    recPara.setField("YD_DN_WO_BED", yddatautil.setDataDefault(inDto[x].getField("YD_UP_WO_BED"), ""));
				recPara.setField("STACK_LAYER_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK_ABLE);	
				recPara.setField("CONFIRM_YN", "");
				dao.updatepPlateYdStkLayer(recPara);
				
				//Book/In작업은 스케쥴 및 스케쥴재료 삭제한다
				if(yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"), "").equals(YdConstant.SCH_CD_PPLATE_BOOK_IN))
				{
//					스케쥴 정보 업데이트
					szMsg="----------------- 후판정정야드  스케쥴 정보 업데이트 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					plateRevisedao.pPlateYdCrnBoonInSchEnd(recPara);
				
					

					szMsg="----------------- 후판정정야드 스케쥴재료삭제  시작----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//크레인 스케쥴 삭제
					intRtnVal = plateRevisedao.deletepPlateYdSchCrn_Stl(recPara);
					
					szMsg="----------------- 후판정정야드 스케쥴재료삭제  완료----------------";

			
				}
					
			    
				else
				{
					
//					스케쥴 정보 업데이트
					szMsg="----------------- 후판정정야드  스케쥴 정보 업데이트 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					plateRevisedao.pPlateYdCrnSchUpWrk(recPara);
				}
	 				
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		
		
		}
		return szR_msg;
	}	// end of pPlateCrnSchBookInMm
	
	
	
	
	/**
	 *  후판정정야드 권하실적처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	
	public JDTORecordSet pPlateYdCrnDownWrk(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		JDTORecordSet rsWrkmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		JDTORecord recWrkMtl   = null;
		
		JDTORecord recSeq = null;
		JDTORecord recIn = null;
		JDTORecord    recInTemp         = null;
		JDTORecordSet rsOut = null;
		JDTORecord recInPara = null;
		int intRtnVal = 0;
		
		JDTORecord recBedprior = null;
		JDTORecordSet rsBedpriorOut = null;
		
		JspCommonDAO 	dao 		= new JspCommonDAO();
		PlateReviseDao 	plateRevisedao 		= new PlateReviseDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		ymCommonDAO ymdao = ymCommonDAO.getInstance();
		JDTORecord 		recPara		= null;
		JDTORecord 		recPara2		= null;
		
		String szR_msg ="";
		String szLogMsg = "";
		String szMethodName="pPlateYdCrnDownWrk";
		String szOperationName	= "후판정정야드 크레인스케쥴 권하실적처리";	
		String szMsg = "";
		
		String YD_EQP_GP = "";
		String YD_BED_GP = "";
		String YD_STK_ACT_GP = "";
		String YD_BED_END_GP = "";
		String YD_BED_END_GP_COL = "";
		String s_YD_STK_BED_CR_GP = "";
		String s_YD_SCH_CD = "";
		String s_CLOSE_GP = "";
		String STL_NO = "";
		String YD_SCH_CD = "";
		
		String s_YD_CRN_SCH_ID = "";
		String s_YD_EQP_GP = "";
		String s_YD_BAY_GP = "";
		String s_YD_STK_BED_NO = "";
		String s_DN_WORK_GP = "";
		String s_YD_STK_LYR_NO = "";
		String s_YD_STK_BED_END_GP = "";
		String s_USER_ID  = "";
		
		String s_YD_DN_WO_LOC = "";
		String s_YD_DN_WO_LAYER = "";
		String s_YD_DN_WO_BED = "";
		
		String YD_UP_WR_LOC = "";
		String YD_UP_WR_LAYER = "";
		String YD_UP_WR_BED = "";
		String YD_WORD_DT_DD = "";
		String YD_UP_CMPL_DT_DD = "";
		String YD_EQP_ID = "";
		String szMTL_STAT_CD = "";
		
		List FrtostlList = null;
		List RollmatList = null;
		List PlatematList = null;
			
		
		try {
			
			szMsg = "JSP-SESSION [후판정정야드 크레인스케쥴 권하실적처리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			

			s_YD_CRN_SCH_ID = inDto.getParam("YD_CRN_SCH_ID_DN").trim();
			s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
			s_YD_BAY_GP = getBayeqpGp_CODE2(s_YD_EQP_GP); //동
			s_YD_STK_BED_NO=inDto.getParam("YD_STK_BED_NO").trim();
			s_DN_WORK_GP=inDto.getParam("DN_WORK_GP").trim();
			
			if(s_DN_WORK_GP.equals("L"))
			{
				s_DN_WORK_GP = "C";
				
			}
					
			
			
			s_YD_STK_LYR_NO=inDto.getParam("YD_STK_LYR_NO").trim();
			s_YD_STK_BED_END_GP=inDto.getParam("YD_STK_BED_END_GP").trim();
			s_USER_ID = inDto.getParam("YD_USER_ID").trim();
			
			s_YD_DN_WO_LOC = "P"+s_YD_BAY_GP+s_YD_EQP_GP+s_YD_STK_BED_NO;
			s_YD_DN_WO_LAYER = s_YD_STK_LYR_NO;
			s_YD_DN_WO_BED = s_YD_STK_BED_END_GP;
			
			//스케쥴 정보 업데이트
			szMsg = s_YD_CRN_SCH_ID+"/"+s_YD_EQP_GP+"/"+s_YD_STK_BED_NO+"/"+s_DN_WORK_GP+"/"+s_YD_STK_LYR_NO+"/"+s_YD_STK_BED_END_GP;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
			
			String trnQueryId = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListCrnSchMtl";
    		FrtostlList = ymdao.getCommonList(trnQueryId, new Object[]{s_YD_CRN_SCH_ID});
    		
    		szMsg = "크레인스케쥴조회 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
    		JDTORecord tcRecord = null;
    		int iSeqCount 	= FrtostlList.size();
    		for(int x=0; x < iSeqCount ; x++){
    			
    			
    			JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(x);
				STL_NO = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"").trim();
				YD_SCH_CD = StringHelper.evl(FrtoSltrec.getFieldString("YD_SCH_CD"),"").trim();
				YD_UP_WR_LOC = StringHelper.evl(FrtoSltrec.getFieldString("YD_UP_WR_LOC"),"").trim();
				YD_UP_WR_LAYER = StringHelper.evl(FrtoSltrec.getFieldString("YD_UP_WR_LAYER"),"").trim();
				YD_UP_WR_BED = StringHelper.evl(FrtoSltrec.getFieldString("YD_UP_WR_BED"),"").trim();
				YD_WORD_DT_DD = StringHelper.evl(FrtoSltrec.getFieldString("YD_WORD_DT_DD"),"").trim();
				YD_UP_CMPL_DT_DD = StringHelper.evl(FrtoSltrec.getFieldString("YD_UP_CMPL_DT_DD"),"").trim();
				YD_EQP_ID = StringHelper.evl(FrtoSltrec.getFieldString("YD_EQP_ID"),"").trim();

							
				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();				
				recPara.setField("YD_CRN_SCH_ID", s_YD_CRN_SCH_ID);
				recPara.setField("YD_USER_ID", s_USER_ID);
				recPara.setField("STL_NO", STL_NO);
			    recPara.setField("YD_DN_WO_LOC", s_YD_DN_WO_LOC);
			    recPara.setField("YD_DN_WO_LAYER", s_YD_DN_WO_LAYER);	
			    recPara.setField("YD_DN_WO_BED", 	s_YD_DN_WO_BED);
			    recPara.setField("STACK_LAYER_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK);
			    recPara.setField("STACK_COL_ACTIVE_STAT", s_DN_WORK_GP);
			    recPara.setField("STACK_COL_GP_STAT", YdConstant.YD_STK_LYR_MTL_STAT_STK_ABLE);
			    recPara.setField("CONFIRM_YN", "N");
			    
			    
			    
			    
				 
				
//				스케쥴 정보 업데이트
				szMsg="----------------- 후판정정야드  스케쥴 정보 업데이트 ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				plateRevisedao.pPlateYdCrnSchDownWrk(recPara);
			
				

				szMsg="----------------- 후판정정야드 스케쥴재료삭제  시작----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//크레인 스케쥴 삭제
				intRtnVal = plateRevisedao.deletepPlateYdSchCrn_Stl(recPara);
				
				szMsg="----------------- 후판정정야드 스케쥴재료삭제  완료----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				szMsg="----------------- 후판정정야드 야드맵 업데이트   시작----------------";
				dao.updatepPlateYdStkLayer(recPara);
				szMsg="----------------- 후판정정야드 야드맵 업데이트   완료----------------";
				
				dao.updatepPlateYdStkCol_End(recPara);
				
				
				
				
				
				//수정할 항목 SETTING
				recPara2 = JDTORecordFactory.getInstance().create();				
				recPara2.setField("V_SCHEDULEID", s_YD_CRN_SCH_ID);
				recPara2.setField("V_STL_NO", STL_NO);
				recPara2.setField("V_YD_EQP_ID", YD_EQP_ID);
				recPara2.setField("V_SCH_WORK_KIND", YD_SCH_CD);
				recPara2.setField("V_CRANE_WORD_DDTT", YD_WORD_DT_DD);
				recPara2.setField("V_SCH_WDEMAND_DDTT", YD_WORD_DT_DD);
				recPara2.setField("V_CRANE_WORD_UP_LOC", YD_UP_WR_LOC+YD_UP_WR_LAYER+YD_UP_WR_BED);
				recPara2.setField("V_CRANE_WRSLT_UP_LOC", YD_UP_WR_LOC+YD_UP_WR_LAYER+YD_UP_WR_BED);
				recPara2.setField("V_CRANE_WRSLT_UP_DDTT", YD_UP_CMPL_DT_DD);
				recPara2.setField("V_CRANE_WORD_PUT_LOC", s_YD_DN_WO_LOC+s_YD_DN_WO_LAYER+s_YD_DN_WO_BED);
				recPara2.setField("V_CRANE_WRSLT_PUT_LOC", s_YD_DN_WO_LOC+s_YD_DN_WO_LAYER+s_YD_DN_WO_BED);
				recPara2.setField("V_YD_USER_ID", s_USER_ID);
				
				szMsg="----------------- 작업내역 등록시작----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				intRtnVal = plateRevisedao.inspPlatewrkResult(recPara2);
				
				szMsg="----------------- 작업내역드 등록 완료  ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
	
				int strlen = STL_NO.length();
				szMsg="["+ szOperationName +"] -----------------재료번호 길이----------------"+strlen;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if(strlen == 8)
				{
					
//					ROLL_MAT 조회
					
					
					String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
					RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
		    		
		    		szMsg = "RollmatList조회 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
		    		int iSeqCnt 	= RollmatList.size();
		    		for(int i=0; i < iSeqCnt ; i++){
		    			
		    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
		    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
	
		    		}

					szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
					
				}
				else if(strlen == 10)
				{
					if(isInteger(STL_NO.substring(8,10)))
					{
//						PLATE_MAT 조회
						String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListPlatematMtl";
						PlatematList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
			    		
			    		szMsg = "PlatematList조회 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
			    		int iSeqCnt 	= PlatematList.size();
			    		for(int i=0; i < iSeqCnt ; i++){
			    			
			    			JDTORecord PlatematStlrec = (JDTORecord)PlatematList.get(i);
			    			szMTL_STAT_CD = StringHelper.evl(PlatematStlrec.getFieldString("MTL_STAT_CD"),"").trim();
		
			    		}

						szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (PLATE_MAT) 조회완료 ----------------";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
				
						
					}
					else
					{
//						//				ROLL_MAT 조회
						String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
						RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
			    		
			    		szMsg = "RollmatList조회 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
			    		int iSeqCnt 	= RollmatList.size();
			    		for(int i=0; i < iSeqCnt ; i++){
			    			
			    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
			    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
		
			    		}

						szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
						
					}
					
				}

				
				

				szMsg="조업 위치변경정보  송신 YDPRJ004";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		recInTemp = JDTORecordFactory.getInstance().create();
    			recInTemp.setField("MSG_ID",        "YDPRJ004");
				
				recInTemp.setField("PL_MPL_NO",     STL_NO);
				recInTemp.setField("PL_WR_PRSNT_PROC_CD", getprCurrState_CODE(s_YD_DN_WO_LOC.substring(2,4)));
				
				
				if(YD_UP_WR_LOC.equals(""))
				{
					recInTemp.setField("PL_WR_ELE_PROC_CD", "");					
				}
				else
				{
					recInTemp.setField("PL_WR_ELE_PROC_CD", getprCurrState_CODE(YD_UP_WR_LOC.substring(2,4)));
				}
				
				recInTemp.setField("MTL_STAT_CD",         szMTL_STAT_CD);
				recInTemp.setField("MODIFIER",        "YDPRJ004");
	
		//		if(!getprYDLOC_CODE(Book_In_Loc).equals("") && !currProg.equals(""))
		//		{
		//			if(getprYDLOC_CODE(Book_In_Loc) != currProg)
		//			{
		//				ydDelegate.sendMsg(recInTemp); 	
		//			}
		//		}

				
 				
			}	
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		
		
		}
		return outRecSet;
	}	// end of pPlateCrnSchBookInMm
	
	

	/**
	 *  저장위치수정 팝업 조회 (화면)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecordSet getJjydPlateLocMgt(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_STL_NO", 	inDto.getParam("STL_NO").trim());	    /*재료번*/
			
			// DAO 호출
			outRecSet = dao.getJjydPlateLocMgt(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 *  저장위치수정 조회 (PDA)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecordSet getPDA_pPlateLocMgt(JDTORecord inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_STL_NO", 	inDto.getFieldString("STL_NO").trim());	    /*재료번*/
			
			// DAO 호출
			outRecSet = dao.getJjydPlateLocMgt(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	/**
	 * 저장위치수정 ( TO위치 조회)
	 * @ejb.interface-method
	 * @param inDto
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.07
	 */
	public JDTORecordSet getJjydPlateToLoc(JDTORecord inDto) {
		JDTORecordSet	outRecSet		= null;
		PlateReviseDao 	dao 			= new PlateReviseDao();
		
		try {
			
			outRecSet = dao.getJjydPlateToLoc(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	/**
	 * 입고정보조회
	 * @ejb.interface-method
	 * @param inDto
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.07
	 */
	public JDTORecordSet getPlateydLoc(JDTORecord inDto) {
		JDTORecordSet	outRecSet		= null;
		PlateReviseDao 	dao 			= new PlateReviseDao();
		
		try {
			
			outRecSet = dao.getPlateydLoc(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	/**
	 * ROLLMAT정보조회
	 * @ejb.interface-method
	 * @param inDto
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.07
	 */
	public JDTORecordSet getListRollMat(JDTORecord inDto) {
		JDTORecordSet	outRecSet		= null;
		PlateReviseDao 	dao 			= new PlateReviseDao();
		
		try {
			
			outRecSet = dao.getListRollMat(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	/**
	 * PLATEMAT정보조회
	 * @ejb.interface-method
	 * @param inDto
	 * @return JDTORecordSet
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.07
	 */
	public JDTORecordSet getListPlateMat(JDTORecord inDto) {
		JDTORecordSet	outRecSet		= null;
		PlateReviseDao 	dao 			= new PlateReviseDao();
		
		try {
			
			outRecSet = dao.getListPlateMat(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	
	/**
	 * 저장위치수정 수정(PDA)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecord updPDA_pPlateLocMgt(JDTORecord inDto){

		GridData 		gdRes = new GridData();
		JDTORecord	 	outRcd = null;
		try{
			
			gdRes.addParam("T_YD_EQP_GP", inDto.getFieldString("T_YD_EQP_GP"));
			gdRes.addParam("T_YD_BED_GP", inDto.getFieldString("T_YD_BED_GP"));
			gdRes.addParam("T_YD_STK_LYR_NO", inDto.getFieldString("T_YD_STK_LYR_NO"));
			gdRes.addParam("T_YD_LF_GP", inDto.getFieldString("T_YD_LF_GP"));
			gdRes.addParam("T_YD_FEVER_GP", inDto.getFieldString("T_YD_FEVER_GP"));
			gdRes.addParam("STL_NO", inDto.getFieldString("STL_NO"));
			gdRes.addParam("YD_USER_ID", inDto.getFieldString("YD_USER_ID"));
			
			outRcd = updJjydPlateLocMgt(gdRes);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return outRcd;
	}
	
	
	
	
	/**
	 * 북아웃정보삭제
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public GridData deleteBookoutStl(GridData inDto){

		GridData 		gdRes = new GridData();
		JDTORecord	 	outRcd = null;
		PlateReviseDao 	dao 		= new PlateReviseDao();
		int 			ret				= 0;
		GridData 		rtnGrd 		= new GridData();
		JDTORecord 		recPara			= null;
		
		
		try{
			recPara		= JDTORecordFactory.getInstance().create(); 		
			
			recPara.setField("SCH_YN", "Y");
			recPara.setField("YD_USER_ID", 	"frtoreg");
			recPara.setField("STL_NO", inDto.getParam("STL_NO").trim());
		
			
			// DAO 호출
			int rtnCd  = dao.deleteBookoutStlList(recPara);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return rtnGrd;
	}
	
	
	
	/**
	 * 저장위치수정 삭제/반입(PDA)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecord delPDA_pPlateLocMgt(JDTORecord inDto){
		
		JDTORecord 		recPara			= null;
		JDTORecord	 	outRcd 			= null;
		PlateReviseDao 	dao 			= new PlateReviseDao();
		int 			ret				= 0;
		try{
			
			outRcd  = JDTORecordFactory.getInstance().create();
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("V_STACK_COL_GP_STAT", 	"E"); /*적치열상태 - 적치가능*/
			recPara.setField("V_MODIFIER", 				inDto.getFieldString("YD_USER_ID"));/*수정자*/
			recPara.setField("V_YD_STK_COL_GP", 		inDto.getFieldString("YD_STK_COL_GP"));/*적치열구분*/
			recPara.setField("V_YD_STK_LYR_NO", 		inDto.getFieldString("YD_STK_LYR_NO"));/*적치단구분*/
			
			ret = dao.delPDA_pPlateLocMgtCol(recPara);
			if(ret <1){
				outRcd.setField("RTN_CD", "-1");
				outRcd.setField("RTN_MSG", "적치열 수정중 에러발생 ");
				this.m_ctx.setRollbackOnly();
				return outRcd;
			}
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("V_STACK_LAYER_STAT", 	"E");/*적치단상태 -적치가능*/
			recPara.setField("V_CONFIRM_YN", 		"N");/*적치확인유무*/
			recPara.setField("V_MODIFIER", 			inDto.getFieldString("YD_USER_ID"));/*수정자*/
			recPara.setField("V_YD_STK_COL_GP", 	inDto.getFieldString("YD_STK_COL_GP"));/*적치열구분*/
			recPara.setField("V_YD_STK_LYR_NO", 	inDto.getFieldString("YD_STK_LYR_NO"));/*단*/
			recPara.setField("V_YD_STK_BED_NO", 	inDto.getFieldString("YD_STK_BED_NO"));/*번지*/
			
			ret = dao.delPDA_pPlateLocMgtLyr(recPara);
			if(ret <1){
				outRcd.setField("RTN_CD", "-1");
				outRcd.setField("RTN_MSG", "적치단 수정중 에러발생 ");
				this.m_ctx.setRollbackOnly();
				return outRcd;
			}
			
			outRcd.setField("RTN_CD", "1");
			outRcd.setField("RTN_MSG", "저장위치 삭제 성공 ");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return outRcd;
	}
	
	
	
	/**
	 * 저장위치수정 BookIn(PDA)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecord bookinPDA_pPlateLocMgt(JDTORecord inDto){
		
		JDTORecord 		recPara			= null;
		JDTORecord	 	outRcd 			= null;
		PlateReviseDao 	dao 			= new PlateReviseDao();
		ymCommonDAO ymdao = ymCommonDAO.getInstance();
		int 			ret				= 0;
		JDTORecord    recInTemp         = null;
		
		String szMethodName="bookinPDA_pPlateLocMgt";
		String szOperationName	= "후판정정야드 저장위치BookIn";
		JDTORecordSet   outRecSet2  		= null;
		
		String szMsg = "";
		String szMTL_STAT_CD = "";
		String currProg = "";
		
		List FrtostlList = null;
		List RollmatList = null;
		List PlatematList = null;
		
		try{
			
			outRcd  = JDTORecordFactory.getInstance().create();
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("V_STACK_COL_GP_STAT", 	"E"); /*적치열상태 - 적치가능*/
			recPara.setField("V_MODIFIER", 				inDto.getFieldString("YD_USER_ID"));/*수정자*/
			recPara.setField("V_YD_STK_COL_GP", 		inDto.getFieldString("YD_STK_COL_GP"));/*적치열구분*/
			recPara.setField("V_YD_STK_LYR_NO", 		inDto.getFieldString("YD_STK_LYR_NO"));/*적치단구분*/
			
			ret = dao.delPDA_pPlateLocMgtCol(recPara);
			if(ret <1){
				outRcd.setField("RTN_CD", "-1");
				outRcd.setField("RTN_MSG", "적치열 수정중 에러발생 ");
				this.m_ctx.setRollbackOnly();
				return outRcd;
			}
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("V_STACK_LAYER_STAT", 	"E");/*적치단상태 -적치가능*/
			recPara.setField("V_CONFIRM_YN", 		"N");/*적치확인유무*/
			recPara.setField("V_MODIFIER", 			inDto.getFieldString("YD_USER_ID"));/*수정자*/
			recPara.setField("V_YD_STK_COL_GP", 	inDto.getFieldString("YD_STK_COL_GP"));/*적치열구분*/
			recPara.setField("V_YD_STK_LYR_NO", 	inDto.getFieldString("YD_STK_LYR_NO"));/*단*/
			recPara.setField("V_YD_STK_BED_NO", 	inDto.getFieldString("YD_STK_BED_NO"));/*번지*/
			
			ret = dao.delPDA_pPlateLocMgtLyr(recPara);
			if(ret <1){
				outRcd.setField("RTN_CD", "-1");
				outRcd.setField("RTN_MSG", "적치단 수정중 에러발생 ");
				this.m_ctx.setRollbackOnly();
				return outRcd;
			}
			
			outRcd.setField("RTN_CD", "1");
			outRcd.setField("RTN_MSG", "저장위치 삭제 성공 ");
			
			
			
			
	        String STL_NO = inDto.getFieldString("STL_NO");
			
			int strlen = STL_NO.length();
			szMsg="["+ szOperationName +"] -----------------재료번호 길이----------------"+strlen;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			if(strlen == 8)
			{
				
//				ROLL_MAT 조회
				
				
				String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
				RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
	    		
	    		szMsg = "RollmatList조회 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		int iSeqCnt 	= RollmatList.size();
	    		for(int i=0; i < iSeqCnt ; i++){
	    			
	    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
	    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
	    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();

	    		}

				szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				
			}
			else if(strlen == 10)
			{
				if(isInteger(STL_NO.substring(8,10)))
				{
//					PLATE_MAT 조회
					String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListPlatematMtl";
					PlatematList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
		    		
		    		szMsg = "PlatematList조회 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
		    		int iSeqCnt 	= PlatematList.size();
		    		for(int i=0; i < iSeqCnt ; i++){
		    			
		    			JDTORecord PlatematStlrec = (JDTORecord)PlatematList.get(i);
		    			szMTL_STAT_CD = StringHelper.evl(PlatematStlrec.getFieldString("MTL_STAT_CD"),"").trim();
		    			currProg = StringHelper.evl(PlatematStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
	
		    		}

					szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (PLATE_MAT) 조회완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			
					
				}
				else
				{
//					//				ROLL_MAT 조회
					String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
					RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
		    		
		    		szMsg = "RollmatList조회 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
		    		int iSeqCnt 	= RollmatList.size();
		    		for(int i=0; i < iSeqCnt ; i++){
		    			
		    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
		    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
		    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
	
		    		}

					szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
				}
				
			}

			
			

			szMsg="조업 위치변경정보  송신 YDPRJ004";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",        "YDPRJ004");
			
			recInTemp.setField("PL_MPL_NO",     STL_NO);
			recInTemp.setField("PL_WR_PRSNT_PROC_CD", getprYDLOC_CODE(inDto.getFieldString("YD_LOC_GP").trim()));
            recInTemp.setField("PL_WR_ELE_PROC_CD", currProg);							
			recInTemp.setField("MTL_STAT_CD",         szMTL_STAT_CD);
			recInTemp.setField("MODIFIER",        "YDPRJ004");

			if(!getprYDLOC_CODE(inDto.getFieldString("YD_LOC_GP").trim()).equals("") && !currProg.equals(""))
			{
				if(getprYDLOC_CODE(inDto.getFieldString("YD_LOC_GP").trim()) != currProg)
				{
					//ydDelegate.sendMsg(recInTemp); 	
				}
			}
			
			
			
			
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return outRcd;
	}
	
	
	/**
	 * 저장위치수정 등록(PDA)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecord insPDA_pPlateLocMgt1(JDTORecord inDto){
		JDTORecord 		recPara			= null;
		JDTORecord 		recPara2			= null;
		JDTORecord 		updPara			= null;
		JDTORecord	 	rtnPara			= null;
		JDTORecord	 	toRcd			= null;
		JDTORecordSet 	outRecSet		= null;
		
		JDTORecordSet   outRecSet2  		= null;
		JDTORecord    recInTemp         = null;
		List FrtostlList = null;
		List RollmatList = null;
		List PlatematList = null;
        String szMTL_STAT_CD = "";
        int 			rtnCd2			= 0;

		PlateReviseDao 	dao 			= new PlateReviseDao();
		ymCommonDAO ymdao = ymCommonDAO.getInstance();
		int				rtnCd			= 0;
		
		
		String szMethodName="insPDA_pPlateLocMgt";
		String szOperationName	= "후판정정야드 저장위치수정";	
         String szMsg = "";
        String currProg = "";
		
		try{
			
			rtnPara  = JDTORecordFactory.getInstance().create();
			recPara = JDTORecordFactory.getInstance().create();
			
			// 크레인 스케줄 조회 
			recPara.setField("V_STL_NO", inDto.getFieldString("STL_NO"));			
			outRecSet = dao.getLocMgtCrnSch(recPara);
			
			if(outRecSet != null && outRecSet.size()>0){
				// 크레인 스케줄이 잇을때 스케줄 삭제
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("V_YD_CRN_SCH_ID", outRecSet.getRecord(0).getFieldString("YD_CRN_SCH_ID"));

				rtnCd = dao.delLocMgtCrnSch(recPara);
				
				if(rtnCd < 1){
					rtnPara.setField("RTN_CD", "-1");
					rtnPara.setField("RTN_MSG", "스케쥴 삭제중 Error발생 ");
					m_ctx.setRollbackOnly();
					return rtnPara;
				}
				
				rtnCd = dao.delLocMgtCrnSchMtl(recPara);
				if(rtnCd < 1){
					rtnPara.setField("RTN_CD", "-1");
					rtnPara.setField("RTN_MSG", "스케쥴재료 삭제중 Error발생 ");
					m_ctx.setRollbackOnly();
					return rtnPara;
				}
				
			}
			
			
			
			// 저장위치 등록  			
			String 			T_YD_EQP_GP		= ""; // 스판
			String 			T_YD_BED_GP		= ""; // 배드
			String 			T_YD_STK_LYR_NO	= ""; // 단
			String 			T_YD_FEVER_GP	= ""; // 번지
			String 			T_LF_GP			= ""; // 행/열구분
			
			T_YD_EQP_GP		= inDto.getFieldString("T_YD_EQP_GP").trim(); 		// 스판
			T_YD_BED_GP		= inDto.getFieldString("T_YD_BED_GP").trim(); 		// 배드
			T_YD_STK_LYR_NO	= inDto.getFieldString("T_YD_STK_LYR_NO").trim(); 	// 단
			T_LF_GP			= inDto.getFieldString("T_YD_LF_GP").trim(); 			// 행/열구분
			T_YD_FEVER_GP	= inDto.getFieldString("T_YD_FEVER_GP").trim(); 		// 번지
			
			
			// ------------------------------------------------------------------------
			// TO위치 데이터 조회 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_EQP_GP", 		T_YD_EQP_GP);	    /*스판(야드)*/
			recPara.setField("V_YD_BED_GP", 		T_YD_BED_GP);	    /*배드*/
			recPara.setField("V_YD_STK_LYR_NO", 	T_YD_STK_LYR_NO);	/*단*/
			recPara.setField("V_YD_STK_BED_NO", 	T_YD_FEVER_GP);		/*행열*/
			outRecSet = this.getJjydPlateToLoc(recPara);
			
			if(outRecSet == null){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "TO 저장위치 확인중 Error발생.");
				m_ctx.setRollbackOnly();
				return rtnPara;
			}else if(outRecSet.size()==0){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "조회된 TO저장위치가 없습니다.");
				m_ctx.setRollbackOnly();
				return rtnPara;
			}
			
			toRcd = outRecSet.getRecord(0);
			if(!toRcd.getFieldString("STL_NO").equals("")){
				System.out.println("\n\n");
				System.out.println("==== TO위치에 재료번호 존재 하고 있음 ====");
				System.out.println("재료번호 : " + toRcd.getFieldString("STL_NO"));
				System.out.println("\n\n");
			}
			
			
			// ------------------------------------------------------------------------
			// TO위치 수정 (update)
			updPara = JDTORecordFactory.getInstance().create();
			
			updPara.setField("V_STL_NO", inDto.getFieldString("STL_NO").trim());/*재료번호*/
			updPara.setField("V_CONFIRM_YN", "N");/*적치확인여부*/
			updPara.setField("V_STK_LYR_STAT", "C");/*적치단상태*/
			updPara.setField("V_MODIFIER", inDto.getFieldString("YD_USER_ID").trim());/*수정자*/
			updPara.setField("V_YD_EQP_GP", T_YD_EQP_GP);/*스판(야드)*/
			updPara.setField("V_YD_BED_GP", T_YD_BED_GP);/*배드*/
			updPara.setField("V_YD_STK_LYR_NO", T_YD_STK_LYR_NO);/*단*/
			updPara.setField("V_YD_STK_BED_NO", T_YD_FEVER_GP);/*상세배드*/    

			rtnCd = dao.updJjydPlateLocMgt(updPara);
			
			if(rtnCd != 1){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "TO저장위치 수정 실패");
				this.m_ctx.setRollbackOnly();
				return rtnPara;
			}
			// TO적치열 수정 
			updPara = JDTORecordFactory.getInstance().create();
			// 적치열수정
			updPara.setField("V_STACK_COL_GP_STAT", "C");/*적치열상태*/
			updPara.setField("V_STACK_LAYER_ACTIVE_STAT", T_LF_GP);/*행/열 구분*/
			updPara.setField("V_MODIFIER", inDto.getFieldString("YD_USER_ID").trim());/*수정자*/
			updPara.setField("V_YD_STK_LYR_NO", T_YD_STK_LYR_NO);/*단*/
			updPara.setField("V_YD_EQP_GP", T_YD_EQP_GP);/*스판(야드)*/
			updPara.setField("V_YD_BED_GP", T_YD_BED_GP);/*배드*/

			rtnCd = dao.updJjydPlateLocMgtStkCol(updPara);
			
			if(rtnCd != 1){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "TO적치열 수정 실패");
				this.m_ctx.setRollbackOnly();
				return rtnPara;
			}
			

			String STL_NO = inDto.getFieldString("STL_NO").trim();
			
			int strlen = STL_NO.length();
			szMsg="["+ szOperationName +"] -----------------재료번호 길이----------------"+strlen;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			if(strlen == 8)
			{
				
//				ROLL_MAT 조회
				
				
				String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
				RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
	    		
	    		szMsg = "RollmatList조회 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		int iSeqCnt 	= RollmatList.size();
	    		for(int i=0; i < iSeqCnt ; i++){
	    			
	    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
	    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
	    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();

	    		}

				szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				
			}
			else if(strlen == 10)
			{
				if(isInteger(STL_NO.substring(8,10)))
				{
//					PLATE_MAT 조회
					String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListPlatematMtl";
					PlatematList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
		    		
		    		szMsg = "PlatematList조회 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
		    		int iSeqCnt 	= PlatematList.size();
		    		for(int i=0; i < iSeqCnt ; i++){
		    			
		    			JDTORecord PlatematStlrec = (JDTORecord)PlatematList.get(i);
		    			szMTL_STAT_CD = StringHelper.evl(PlatematStlrec.getFieldString("MTL_STAT_CD"),"").trim();
		    			currProg = StringHelper.evl(PlatematStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
	
		    		}

					szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (PLATE_MAT) 조회완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			
					
				}
				else
				{
//					//				ROLL_MAT 조회
					String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
					RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
		    		
		    		szMsg = "RollmatList조회 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
		    		int iSeqCnt 	= RollmatList.size();
		    		for(int i=0; i < iSeqCnt ; i++){
		    			
		    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
		    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
		    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
	
		    		}

					szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
				}
				
			}

			
			

			szMsg="조업 위치변경정보  송신 YDPRJ004";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",        "YDPRJ004");
			
			recInTemp.setField("PL_MPL_NO",     STL_NO);
			recInTemp.setField("PL_WR_PRSNT_PROC_CD", getprCurrState_CODE(T_YD_EQP_GP));
			
			
			recInTemp.setField("PL_WR_ELE_PROC_CD", currProg);

			
			recInTemp.setField("MTL_STAT_CD",         szMTL_STAT_CD);
			recInTemp.setField("MODIFIER",        "YDPRJ004");


			if(!getprCurrState_CODE(T_YD_EQP_GP).equals("") && !currProg.equals(""))
			{
				if(getprCurrState_CODE(T_YD_EQP_GP) != currProg)
				{
					//ydDelegate.sendMsg(recInTemp); 	
				}
			}  
			
			
			


			// --------------------------------------------------------
			// 수정이력등록
			//TODO....
			updPara = JDTORecordFactory.getInstance().create();
			updPara.setField("V_SCHEDULEID", "000000000000000000"); //크래인스케줄아이디
			updPara.setField("V_STL_NO", inDto.getFieldString("STL_NO").trim());//재료번호
			updPara.setField("V_YD_EQP_ID", "");//
			updPara.setField("V_SCH_WORK_KIND", "PAYD01MM");//스케줄코드
			updPara.setField("V_SCH_WPREFER", "");//
			updPara.setField("V_CRANE_WORK_DUTY", "");//
			updPara.setField("V_CRANE_WORK_PARTY", "");//
			updPara.setField("V_CRANE_WORD_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));//
			updPara.setField("V_SCH_WDEMAND_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));//
			updPara.setField("V_SCH_WDEMAND_DUTY", "");//
			updPara.setField("V_SCH_WDEMAND_PARTY", "");//
			updPara.setField("V_SCH_WDEMAND_TYPE", "");//
			updPara.setField("V_CRANE_WRSLT_CD", "");//
			updPara.setField("V_CRANE_WORD_UP_LOC", "");//
			updPara.setField("V_CRANE_WRSLT_UP_LOC", "");//
			updPara.setField("V_CRANE_WRSLT_UP_FUNC", "");//
			updPara.setField("V_CRANE_WRSLT_UP_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));//
			updPara.setField("V_CRANE_WORD_PUT_LOC", toRcd.getFieldString("YD_STK_COL_GP").trim()
												+ toRcd.getFieldString("YD_STK_LYR_NO").trim()
												+ T_YD_FEVER_GP);//
			updPara.setField("V_CRANE_WRSLT_PUT_LOC", toRcd.getFieldString("YD_STK_COL_GP").trim()
												+ toRcd.getFieldString("YD_STK_LYR_NO").trim()
												+ T_YD_FEVER_GP);//
			updPara.setField("V_CRANE_WRSLT_PUT_FUNC", "");//
			updPara.setField("V_YD_USER_ID", inDto.getFieldString("YD_USER_ID").trim());//
			                        
			rtnCd = dao.inspPlatewrkResult(updPara);
			if(rtnCd != 1){
				System.out.println("\n\nERROR -> 저장위치 이력등록 실패\n\n");
				//rtnPara.setField("RTN_CD", "-1");
				//rtnPara.setField("RTN_MSG", "저장위치 이력등록 실패");
				//this.m_ctx.setRollbackOnly();
				//return rtnPara;
			}
			rtnPara.setField("RTN_CD", "1");
			rtnPara.setField("RTN_MSG", "정상 처리되었습니다.");
			
			

		}catch (Exception e) {
			e.printStackTrace();
			m_ctx.setRollbackOnly();
		}
		
		
		return rtnPara;
	}
	
	
	
	
	
	/**
	 * 저장위치수정 등록(PDA)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecord insPDA_pPlateLocMgt(JDTORecord inDto){
		JDTORecord 		recPara			= null;
		JDTORecord 		recPara2			= null;
		JDTORecord 		bookoutrecPara			= null;
		JDTORecord 		updPara			= null;
		JDTORecord	 	rtnPara			= null;
		JDTORecord	 	toRcd			= null;
		JDTORecordSet 	outRecSet		= null;

		PlateReviseDao 	dao 			= new PlateReviseDao();
		ymCommonDAO ymdao = ymCommonDAO.getInstance();
		JspCommonDAO 	dao1 		= new JspCommonDAO();
		int				rtnCd			= 0;
		
		JDTORecordSet   outRecSet2  		= null;
		JDTORecordSet   outRecSet3  		= null;
		JDTORecordSet outRecSet4 = null;
		JDTORecordSet outRecSet5 = null;
		JDTORecord    recInTemp         = null;
		List FrtostlList = null;
		List RollmatList = null;
		List PlatematList = null;
        String szMTL_STAT_CD = "";
        int 			rtnCd2			= 0;

		String szMethodName="insPDA_pPlateLocMgt";
		String szOperationName	= "후판정정야드 저장위치수정";	
         String szMsg = "";
         String currProg = "";
		
		try{
			
			rtnPara  = JDTORecordFactory.getInstance().create();
			recPara = JDTORecordFactory.getInstance().create();
			
			// 크레인 스케줄 조회 
			recPara.setField("V_STL_NO", inDto.getFieldString("STL_NO"));			
			outRecSet = dao.getLocMgtCrnSch(recPara);
			
			szMsg="["+ szOperationName +"] -----------------크레인 스케쥴조회 ----------------"+outRecSet.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(outRecSet != null && outRecSet.size()>0){
				// 크레인 스케줄이 잇을때 스케줄 삭제
				
				
				int iSeqCnt 	= outRecSet.size();
	    		for(int i=0; i < iSeqCnt ; i++){
	
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("V_YD_CRN_SCH_ID", outRecSet.getRecord(i).getFieldString("YD_CRN_SCH_ID"));

					rtnCd = dao.delLocMgtCrnSch(recPara);	
					rtnCd = dao.delLocMgtCrnSchMtl(recPara);
				

	    		}
				
			}
			szMsg="["+ szOperationName +"] KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK+++++++++++++++++++++";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			
			recPara		= JDTORecordFactory.getInstance().create(); 
			recPara.setField("V_STL_NO", inDto.getFieldString("STL_NO"));
			outRecSet3 = this.getPlateydLoc(recPara);
			
			if(outRecSet3.size()>0){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "이미 입고된 재료입니다.");
				m_ctx.setRollbackOnly();
				return rtnPara;
			}
			
			
			
			
			//데이터정합성체크(Roll)
			recPara		= JDTORecordFactory.getInstance().create(); 
			recPara.setField("V_STL_NO", inDto.getFieldString("STL_NO"));
			outRecSet4 = this.getListRollMat(recPara);

			
			//데이터정합성체크(PLATE)
			recPara		= JDTORecordFactory.getInstance().create(); 
			recPara.setField("V_STL_NO", inDto.getFieldString("STL_NO"));
			outRecSet5 = this.getListPlateMat(recPara);
			
			if(outRecSet4.size() <= 0 && outRecSet5.size() <= 0){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "존재하지않는 재료번호입니다.");
				m_ctx.setRollbackOnly();
				return rtnPara;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			// 저장위치 등록  			
			String 			T_YD_EQP_GP		= ""; // 스판
			String 			T_YD_BED_GP		= ""; // 배드
			String 			T_YD_STK_LYR_NO	= ""; // 단
			String 			T_YD_FEVER_GP	= ""; // 번지
			String 			T_LF_GP			= ""; // 행/열구분
			
			T_YD_EQP_GP		= inDto.getFieldString("T_YD_EQP_GP").trim(); 		// 스판
			T_YD_BED_GP		= inDto.getFieldString("T_YD_BED_GP").trim(); 		// 배드
			T_YD_STK_LYR_NO	= inDto.getFieldString("T_YD_STK_LYR_NO").trim(); 	// 단
			T_LF_GP			= inDto.getFieldString("T_YD_LF_GP").trim(); 			// 행/열구분
			T_YD_FEVER_GP	= inDto.getFieldString("T_YD_FEVER_GP").trim(); 		// 번지
			
			
			// ------------------------------------------------------------------------
			// TO위치 데이터 조회 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_EQP_GP", 		T_YD_EQP_GP);	    /*스판(야드)*/
			recPara.setField("V_YD_BED_GP", 		T_YD_BED_GP);	    /*배드*/
			recPara.setField("V_YD_STK_LYR_NO", 	T_YD_STK_LYR_NO);	/*단*/
			recPara.setField("V_YD_STK_BED_NO", 	T_YD_FEVER_GP);		/*행열*/
			outRecSet = this.getJjydPlateToLoc(recPara);
			
			if(outRecSet == null){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "TO 저장위치 확인중 Error발생.");
				m_ctx.setRollbackOnly();
				return rtnPara;
			}else if(outRecSet.size()==0){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "조회된 TO저장위치가 없습니다.");
				m_ctx.setRollbackOnly();
				return rtnPara;
			}
			
			toRcd = outRecSet.getRecord(0);
			if(!toRcd.getFieldString("STL_NO").equals("")){
				System.out.println("\n\n");
				System.out.println("==== TO위치에 재료번호 존재 하고 있음 ====");
				System.out.println("재료번호 : " + toRcd.getFieldString("STL_NO"));
				System.out.println("\n\n");
			}
			
			
			// ------------------------------------------------------------------------
			// TO위치 수정 (update)
			updPara = JDTORecordFactory.getInstance().create();
			
			updPara.setField("V_STL_NO", inDto.getFieldString("STL_NO").trim());/*재료번호*/
			updPara.setField("V_CONFIRM_YN", "N");/*적치확인여부*/
			updPara.setField("V_STK_LYR_STAT", "C");/*적치단상태*/
			updPara.setField("V_MODIFIER", inDto.getFieldString("YD_USER_ID").trim());/*수정자*/
			updPara.setField("V_YD_EQP_GP", T_YD_EQP_GP);/*스판(야드)*/
			updPara.setField("V_YD_BED_GP", T_YD_BED_GP);/*배드*/
			updPara.setField("V_YD_STK_LYR_NO", T_YD_STK_LYR_NO);/*단*/
			updPara.setField("V_YD_STK_BED_NO", T_YD_FEVER_GP);/*상세배드*/    

			rtnCd = dao.updJjydPlateLocMgt(updPara);
			
			if(rtnCd != 1){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "TO저장위치 수정 실패");
				this.m_ctx.setRollbackOnly();
				return rtnPara;
			}
			// TO적치열 수정 
			updPara = JDTORecordFactory.getInstance().create();
			// 적치열수정
			updPara.setField("V_STACK_COL_GP_STAT", "C");/*적치열상태*/
			updPara.setField("V_STACK_LAYER_ACTIVE_STAT", T_LF_GP);/*행/열 구분*/
			updPara.setField("V_MODIFIER", inDto.getFieldString("YD_USER_ID").trim());/*수정자*/
			updPara.setField("V_YD_STK_LYR_NO", T_YD_STK_LYR_NO);/*단*/
			updPara.setField("V_YD_EQP_GP", T_YD_EQP_GP);/*스판(야드)*/
			updPara.setField("V_YD_BED_GP", T_YD_BED_GP);/*배드*/

			rtnCd = dao.updJjydPlateLocMgtStkCol(updPara);
			
			if(rtnCd != 1){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "TO적치열 수정 실패");
				this.m_ctx.setRollbackOnly();
				return rtnPara;
			}

			
			bookoutrecPara		= JDTORecordFactory.getInstance().create();
			
			bookoutrecPara.setField("SCH_YN", "Y");
			bookoutrecPara.setField("YD_USER_ID", 	"frtoreg");
			bookoutrecPara.setField("STL_NO", inDto.getFieldString("STL_NO").trim());
			
//			 Bookout정보 스케쥴 등록유무 업데이트
			szMsg="["+ szOperationName +"] ----------------- 후판정정야드  Bookout정보 스케쥴 등록유무 업데이트 ----------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			dao1.updatepPlateCrnSchMtl(bookoutrecPara);
			
			szMsg="["+ szOperationName +"] ----------------- 후판정정야드  Bookout정보 스케쥴 등록유무 업데이트완료 ----------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			

			
            String STL_NO = inDto.getFieldString("STL_NO").trim();
			
			int strlen = STL_NO.length();
			szMsg="["+ szOperationName +"] -----------------재료번호 길이----------------"+strlen;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			if(strlen == 8)
			{
				
//				ROLL_MAT 조회
				
				
				String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
				RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
				
				szMsg="["+ szOperationName +"] -----------------크레인 스케쥴조회 ----------------"+RollmatList.size();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if(RollmatList.size() > 0)
				{
		    		szMsg = "RollmatList조회 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
		    		int iSeqCnt 	= RollmatList.size();
		    		for(int i=0; i < iSeqCnt ; i++){
		    			
		    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
		    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
		    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
	
		    		}
				}
				else 
				{ 
					szMTL_STAT_CD = "";
					currProg = "";
				}

				szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				
			}
			else if(strlen == 10)
			{
				if(isInteger(STL_NO.substring(8,10)))
				{
//					PLATE_MAT 조회
					String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListPlatematMtl";
					PlatematList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
					
					szMsg="["+ szOperationName +"] -----------------크레인 스케쥴조회 ----------------"+PlatematList.size();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
		    		szMsg = "PlatematList조회 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if(PlatematList.size() > 0)
					{
					
			    		int iSeqCnt 	= PlatematList.size();
			    		for(int i=0; i < iSeqCnt ; i++){
			    			
			    			JDTORecord PlatematStlrec = (JDTORecord)PlatematList.get(i);
			    			szMTL_STAT_CD = StringHelper.evl(PlatematStlrec.getFieldString("MTL_STAT_CD"),"").trim();
			    			currProg = StringHelper.evl(PlatematStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
		
			    		}
					}
					else 
					{ 
						szMTL_STAT_CD = "";
						currProg = "";
					}

					szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (PLATE_MAT) 조회완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			
					
				}
				else
				{
//					//				ROLL_MAT 조회
					String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
					RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
					
					szMsg="["+ szOperationName +"] -----------------크레인 스케쥴조회 ----------------"+RollmatList.size();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
					if(RollmatList.size() > 0)
					{
			    		szMsg = "RollmatList조회 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
			    		int iSeqCnt 	= RollmatList.size();
			    		for(int i=0; i < iSeqCnt ; i++){
			    			
			    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
			    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
			    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
		
			    		}
					}
					else 
					{
						szMTL_STAT_CD = "";
						currProg = "";
					}

					szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
				}
				
			}

			
			

			szMsg="조업 위치변경정보  송신 YDPRJ004";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",        "YDPRJ004");
			
			recInTemp.setField("PL_MPL_NO",     STL_NO);
			recInTemp.setField("PL_WR_PRSNT_PROC_CD", getprCurrState_CODE(T_YD_EQP_GP));
			
			
			recInTemp.setField("PL_WR_ELE_PROC_CD", currProg);

			
			recInTemp.setField("MTL_STAT_CD",         szMTL_STAT_CD);
			recInTemp.setField("MODIFIER",        "YDPRJ004");

			if(!getprCurrState_CODE(T_YD_EQP_GP).equals("") && !currProg.equals(""))
			{
				if(getprCurrState_CODE(T_YD_EQP_GP) != currProg)
				{
					//ydDelegate.sendMsg(recInTemp); 	
				}
			} 
			


			// --------------------------------------------------------
			// 수정이력등록
			//TODO....
			updPara = JDTORecordFactory.getInstance().create();
			updPara.setField("V_SCHEDULEID", "000000000000000000"); //크래인스케줄아이디
			updPara.setField("V_STL_NO", inDto.getFieldString("STL_NO").trim());//재료번호
			updPara.setField("V_YD_EQP_ID", "");//
			updPara.setField("V_SCH_WORK_KIND", "PAYD01MM");//스케줄코드
			updPara.setField("V_SCH_WPREFER", "");//
			updPara.setField("V_CRANE_WORK_DUTY", "");//
			updPara.setField("V_CRANE_WORK_PARTY", "");//
			updPara.setField("V_CRANE_WORD_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));//
			updPara.setField("V_SCH_WDEMAND_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));//
			updPara.setField("V_SCH_WDEMAND_DUTY", "");//
			updPara.setField("V_SCH_WDEMAND_PARTY", "");//
			updPara.setField("V_SCH_WDEMAND_TYPE", "");//
			updPara.setField("V_CRANE_WRSLT_CD", "");//
			updPara.setField("V_CRANE_WORD_UP_LOC", "");//
			updPara.setField("V_CRANE_WRSLT_UP_LOC", "");//
			updPara.setField("V_CRANE_WRSLT_UP_FUNC", "");//
			updPara.setField("V_CRANE_WRSLT_UP_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));//
			updPara.setField("V_CRANE_WORD_PUT_LOC", toRcd.getFieldString("YD_STK_COL_GP").trim()
												+ toRcd.getFieldString("YD_STK_LYR_NO").trim()
												+ T_YD_FEVER_GP);//
			updPara.setField("V_CRANE_WRSLT_PUT_LOC", toRcd.getFieldString("YD_STK_COL_GP").trim()
												+ toRcd.getFieldString("YD_STK_LYR_NO").trim()
												+ T_YD_FEVER_GP);//
			updPara.setField("V_CRANE_WRSLT_PUT_FUNC", "");//
			updPara.setField("V_YD_USER_ID", inDto.getFieldString("YD_USER_ID").trim());//
			                        
			rtnCd = dao.inspPlatewrkResult(updPara);
			if(rtnCd != 1){
				System.out.println("\n\nERROR -> 저장위치 이력등록 실패\n\n");
				//rtnPara.setField("RTN_CD", "-1");
				//rtnPara.setField("RTN_MSG", "저장위치 이력등록 실패");
				//this.m_ctx.setRollbackOnly();
				//return rtnPara;
			}
			rtnPara.setField("RTN_CD", "1");
			rtnPara.setField("RTN_MSG", "정상 처리되었습니다.");
			
			

		}catch (Exception e) {
			e.printStackTrace();
			m_ctx.setRollbackOnly();
		}
		
		
		return rtnPara;
	}
	
	
	
	
	
	/**
	 * 저장위치수정 수정
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecord updJjydPlateLocMgt(GridData inDto) {
		JDTORecordSet   outRecSet  		= null;
		JDTORecordSet   outRecSet2  		= null;
		JDTORecordSet outRecSet3 = null;
		JDTORecord 		bookoutrecPara			= null;
		PlateReviseDao 	dao 			= new PlateReviseDao();
		JspCommonDAO 	dao1 		= new JspCommonDAO();
		ymCommonDAO ymdao = ymCommonDAO.getInstance();
		JDTORecord 		recPara			= null;
		JDTORecord 		recPara2			= null;
		JDTORecord 		fromRcd			= null;
		JDTORecord 		toRcd			= null;
		JDTORecord 		rtnPara			= null;
		JDTORecord 		updPara			= null;
		JDTORecord    recInTemp         = null;
		String szMethodName="updJjydPlateLocMgt";
		String szOperationName	= "후판정정야드 저장위치수정";	
		
		int 			rtnCd			= 0;
		int 			rtnCd2			= 0;
		String 			T_YD_EQP_GP		= ""; // 스판
		String 			T_YD_BED_GP		= ""; // 배드
		String 			T_YD_STK_LYR_NO	= ""; // 단
		String 			T_YD_FEVER_GP	= ""; // 번지
		String 			T_LF_GP			= ""; // 행/열구분
		String szMsg = "";
		
		List FrtostlList = null;
		List RollmatList = null;
		List PlatematList = null;
		
		String szMTL_STAT_CD = "";
		String currProg  = "";
		
		try {
			
			T_YD_EQP_GP		= inDto.getParam("T_YD_EQP_GP").trim(); 		// 스판
			T_YD_BED_GP		= inDto.getParam("T_YD_BED_GP").trim(); 		// 배드
			T_YD_STK_LYR_NO	= inDto.getParam("T_YD_STK_LYR_NO").trim(); 	// 단
			T_LF_GP			= inDto.getParam("T_YD_LF_GP").trim(); 			// 행/열구분
			T_YD_FEVER_GP	= inDto.getParam("T_YD_FEVER_GP").trim(); 		// 번지
			
			
			//------------------------------------------------------------------------
			// from위치 조회  
			rtnPara		= JDTORecordFactory.getInstance().create();
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_STL_NO", 	inDto.getParam("STL_NO").trim());	    /*재료번*/
			
			// DAO 호출
			outRecSet = dao.getJjydPlateLocMgt(recPara);
		
			if(outRecSet != null && outRecSet.size()>0){
				fromRcd = outRecSet.getRecord(0);
			}else{
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "FROM 저장위치 확인중 Error발생.");
				return rtnPara;
			}
			
			
			
			recPara		= JDTORecordFactory.getInstance().create(); 
			recPara.setField("V_STL_NO", inDto.getParam("STL_NO"));
			outRecSet3 = this.getPlateydLoc(recPara);
			
			if(outRecSet3.size()>0){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "이미 입고된 재료입니다. 스케쥴을 삭제하세요");
				m_ctx.setRollbackOnly();
				return rtnPara;
			}
			
//------------------------------------------------------------------------
			// TO위치 데이터 조회 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_EQP_GP", 		T_YD_EQP_GP);	    /*스판(야드)*/
			recPara.setField("V_YD_BED_GP", 		T_YD_BED_GP);	    /*배드*/
			recPara.setField("V_YD_STK_LYR_NO", 	T_YD_STK_LYR_NO);	/*단*/
			recPara.setField("V_YD_STK_BED_NO", 	T_YD_FEVER_GP);		/*행열*/
			outRecSet = this.getJjydPlateToLoc(recPara);
			
			if(outRecSet == null){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "TO 저장위치 확인중 Error발생.");
				return rtnPara;
			}else if(outRecSet.size()==0){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "조회된 TO저장위치가 없습니다.");
				return rtnPara;
			}
			
			toRcd = outRecSet.getRecord(0);
			if(!toRcd.getFieldString("STL_NO").equals("")){
				System.out.println("\n\n");
				System.out.println("==== TO위치에 재료번호 존재 하고 있음 ====");
				System.out.println("재료번호 : " + toRcd.getFieldString("STL_NO"));
				System.out.println("\n\n");
			}

			//------------------------------------------------------------------------
			// from위치 수정 (update)
			updPara = JDTORecordFactory.getInstance().create();
			// 레이어수정
			updPara.setField("V_STL_NO", "");/*재료번호*/
			updPara.setField("V_CONFIRM_YN", "N");/*적치확인여부*/
			updPara.setField("V_STK_LYR_STAT", "E");/*적치단상태*/
			updPara.setField("V_MODIFIER", inDto.getParam("YD_USER_ID").trim());/*수정자*/
			updPara.setField("V_YD_EQP_GP", fromRcd.getFieldString("YD_EQP_GP"));/*스판(야드)*/
			updPara.setField("V_YD_BED_GP", fromRcd.getFieldString("YD_BED_GP"));/*배드*/
			updPara.setField("V_YD_STK_LYR_NO", fromRcd.getFieldString("YD_STK_LYR_NO"));/*단*/
			updPara.setField("V_YD_STK_BED_NO", StringHelper.evl(fromRcd.getFieldString("YD_LINT_GP"), fromRcd.getFieldString("YD_FEVER_GP")) );/*상세배드*/    

			rtnCd = dao.updJjydPlateLocMgt(updPara);
		
			if(rtnCd != 1){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "from저장위치 수정 실패");
				this.m_ctx.setRollbackOnly();
				return rtnPara;
			}
			// from적치열 수정 
			updPara = JDTORecordFactory.getInstance().create();
			// 적치열수정
			updPara.setField("V_STACK_COL_GP_STAT", "E");/*적치열상태*/
			updPara.setField("V_STACK_LAYER_ACTIVE_STAT", fromRcd.getFieldString("STACK_LAYER_ACTIVE_STAT").trim());/*행/열 구분*/
			updPara.setField("V_MODIFIER", inDto.getParam("YD_USER_ID").trim());/*수정자*/
			updPara.setField("V_YD_STK_LYR_NO", fromRcd.getFieldString("YD_STK_LYR_NO").trim());/*단*/
			updPara.setField("V_YD_EQP_GP", fromRcd.getFieldString("YD_EQP_GP").trim());/*스판(야드)*/
			updPara.setField("V_YD_BED_GP", fromRcd.getFieldString("YD_BED_GP"));/*배드*/

			rtnCd = dao.updJjydPlateLocMgtStkCol(updPara);
			
			if(rtnCd != 1){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "from적치열 수정 실패");
				this.m_ctx.setRollbackOnly();
				return rtnPara;
			}
			
			
			//------------------------------------------------------------------------
			// TO위치 수정 (update)
			updPara = JDTORecordFactory.getInstance().create();
			
			updPara.setField("V_STL_NO", inDto.getParam("STL_NO").trim());/*재료번호*/
			updPara.setField("V_CONFIRM_YN", "N");/*적치확인여부*/
			updPara.setField("V_STK_LYR_STAT", "C");/*적치단상태*/
			updPara.setField("V_MODIFIER", inDto.getParam("YD_USER_ID").trim());/*수정자*/
			updPara.setField("V_YD_EQP_GP", T_YD_EQP_GP);/*스판(야드)*/
			updPara.setField("V_YD_BED_GP", T_YD_BED_GP);/*배드*/
			updPara.setField("V_YD_STK_LYR_NO", T_YD_STK_LYR_NO);/*단*/
			updPara.setField("V_YD_STK_BED_NO", T_YD_FEVER_GP);/*상세배드*/    

			rtnCd = dao.updJjydPlateLocMgt(updPara);
			
			if(rtnCd != 1){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "TO저장위치 수정 실패");
				this.m_ctx.setRollbackOnly();
				return rtnPara;
			}
			// TO적치열 수정 
			updPara = JDTORecordFactory.getInstance().create();
			// 적치열수정
			updPara.setField("V_STACK_COL_GP_STAT", "C");/*적치열상태*/
			updPara.setField("V_STACK_LAYER_ACTIVE_STAT", T_LF_GP);/*행/열 구분*/
			updPara.setField("V_MODIFIER", inDto.getParam("YD_USER_ID").trim());/*수정자*/
			updPara.setField("V_YD_STK_LYR_NO", T_YD_STK_LYR_NO);/*단*/
			updPara.setField("V_YD_EQP_GP", T_YD_EQP_GP);/*스판(야드)*/
			updPara.setField("V_YD_BED_GP", T_YD_BED_GP);/*배드*/

			rtnCd = dao.updJjydPlateLocMgtStkCol(updPara);
			
			if(rtnCd != 1){
				rtnPara.setField("RTN_CD", "-1");
				rtnPara.setField("RTN_MSG", "TO적치열 수정 실패");
				this.m_ctx.setRollbackOnly();
				return rtnPara;
			}

			
			
			//크레인 스케쥴 존재 시 삭제
			recPara2		= JDTORecordFactory.getInstance().create(); 
			recPara2.setField("V_STL_NO", inDto.getParam("STL_NO").trim());			
			outRecSet2 = dao.getLocMgtCrnSch(recPara2);
			
			if(outRecSet2 != null && outRecSet2.size()>0){
				// 크레인 스케줄이 잇을때 스케줄 삭제
		
				int iSeqCnt 	= outRecSet2.size();
	    		for(int i=0; i < iSeqCnt ; i++){
	
					recPara2 = JDTORecordFactory.getInstance().create();
					recPara2.setField("V_YD_CRN_SCH_ID", outRecSet2.getRecord(i).getFieldString("YD_CRN_SCH_ID"));

					rtnCd = dao.delLocMgtCrnSch(recPara2);
			        rtnCd = dao.delLocMgtCrnSchMtl(recPara2);
					

	    		}

			} 
			
			
			
			
			bookoutrecPara		= JDTORecordFactory.getInstance().create();
			
			bookoutrecPara.setField("SCH_YN", "Y");
			bookoutrecPara.setField("YD_USER_ID", 	"frtoreg");
			bookoutrecPara.setField("STL_NO", inDto.getParam("STL_NO").trim());
			
//			 Bookout정보 스케쥴 등록유무 업데이트
			szMsg="["+ szOperationName +"] ----------------- 후판정정야드  Bookout정보 스케쥴 등록유무 업데이트 ----------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			dao1.updatepPlateCrnSchMtl(bookoutrecPara);
			
			szMsg="["+ szOperationName +"] ----------------- 후판정정야드  Bookout정보 스케쥴 등록유무 업데이트완료 ----------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			

			
			
			String STL_NO = inDto.getParam("STL_NO").trim();
			
			int strlen = STL_NO.length();
			szMsg="["+ szOperationName +"] -----------------재료번호 길이----------------"+strlen;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			if(strlen == 8)
			{
				
//				ROLL_MAT 조회
				
				
				String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
				RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
	    		
	    		szMsg = "RollmatList조회 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		int iSeqCnt 	= RollmatList.size();
	    		for(int i=0; i < iSeqCnt ; i++){
	    			
	    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
	    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
	    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();

	    		}

				szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				
			}
			else if(strlen == 10)
			{
				if(isInteger(STL_NO.substring(8,10)))
				{
//					PLATE_MAT 조회
					String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListPlatematMtl";
					PlatematList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
		    		
		    		szMsg = "PlatematList조회 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
		    		int iSeqCnt 	= PlatematList.size();
		    		for(int i=0; i < iSeqCnt ; i++){
		    			
		    			JDTORecord PlatematStlrec = (JDTORecord)PlatematList.get(i);
		    			szMTL_STAT_CD = StringHelper.evl(PlatematStlrec.getFieldString("MTL_STAT_CD"),"").trim();
		    			currProg = StringHelper.evl(PlatematStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
	
		    		}

					szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (PLATE_MAT) 조회완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			
					
				}
				else
				{
//					//				ROLL_MAT 조회
					String QueryId1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollmatMtl";
					RollmatList = ymdao.getCommonList(QueryId1, new Object[]{STL_NO});
		    		
		    		szMsg = "RollmatList조회 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
		    		int iSeqCnt 	= RollmatList.size();
		    		for(int i=0; i < iSeqCnt ; i++){
		    			
		    			JDTORecord RollmatStlrec = (JDTORecord)RollmatList.get(i);
		    			szMTL_STAT_CD = StringHelper.evl(RollmatStlrec.getFieldString("MTL_STAT_CD"),"").trim();
		    			currProg = StringHelper.evl(RollmatStlrec.getFieldString("PL_WR_PRSNT_PROC_CD"),"").trim();
	
		    		}

					szMsg="["+ szOperationName +"] ----------------- 재료번호길이확인 --재료상태 날판 (ROLL_MAT) 조회완료 ----------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
				}
				
			}

			
			

			szMsg="조업 위치변경정보  송신 YDPRJ004";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",        "YDPRJ004");
			
			recInTemp.setField("PL_MPL_NO",     STL_NO);
			recInTemp.setField("PL_WR_PRSNT_PROC_CD", getprCurrState_CODE(toRcd.getFieldString("YD_STK_COL_GP").trim().substring(2,4)));
			recInTemp.setField("PL_WR_ELE_PROC_CD", currProg);
			recInTemp.setField("MTL_STAT_CD",         szMTL_STAT_CD);
			recInTemp.setField("MODIFIER",        "YDPRJ004");

			if(!getprCurrState_CODE(toRcd.getFieldString("YD_STK_COL_GP").trim().substring(2,4)).equals("") && !currProg.equals(""))
			{
				if(getprCurrState_CODE(toRcd.getFieldString("YD_STK_COL_GP").trim().substring(2,4)) != currProg)
				{
					//ydDelegate.sendMsg(recInTemp); 	
				}
			}
			
			

			// --------------------------------------------------------
			// 수정이력등록
			//TODO....
			updPara = JDTORecordFactory.getInstance().create();
			updPara.setField("V_SCHEDULEID", "000000000000000000"); //크래인스케줄아이디
			updPara.setField("V_STL_NO", inDto.getParam("STL_NO").trim());//재료번호
			updPara.setField("V_YD_EQP_ID", "");//
			updPara.setField("V_SCH_WORK_KIND", "PAYD01MM");//스케줄코드
			updPara.setField("V_SCH_WPREFER", "");//
			updPara.setField("V_CRANE_WORK_DUTY", "");//
			updPara.setField("V_CRANE_WORK_PARTY", "");//
			updPara.setField("V_CRANE_WORD_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));//
			updPara.setField("V_SCH_WDEMAND_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));//
			updPara.setField("V_SCH_WDEMAND_DUTY", "");//
			updPara.setField("V_SCH_WDEMAND_PARTY", "");//
			updPara.setField("V_SCH_WDEMAND_TYPE", "");//
			updPara.setField("V_CRANE_WRSLT_CD", "");//
			updPara.setField("V_CRANE_WORD_UP_LOC", fromRcd.getFieldString("YD_STK_COL_GP").trim()
												+ fromRcd.getFieldString("YD_STK_LYR_NO").trim()
												+ StringHelper.evl(fromRcd.getFieldString("YD_LINT_GP"), fromRcd.getFieldString("YD_FEVER_GP")));//
			updPara.setField("V_CRANE_WRSLT_UP_LOC", fromRcd.getFieldString("YD_STK_COL_GP").trim()
												+ fromRcd.getFieldString("YD_STK_LYR_NO").trim()
												+ StringHelper.evl(fromRcd.getFieldString("YD_LINT_GP"), fromRcd.getFieldString("YD_FEVER_GP")));//
			updPara.setField("V_CRANE_WRSLT_UP_FUNC", "");//
			updPara.setField("V_CRANE_WRSLT_UP_DDTT", YdUtils.getCurDate("yyyyMMddHHmmss"));//
			updPara.setField("V_CRANE_WORD_PUT_LOC", toRcd.getFieldString("YD_STK_COL_GP").trim()
												+ toRcd.getFieldString("YD_STK_LYR_NO").trim()
												+ T_YD_FEVER_GP);//
			updPara.setField("V_CRANE_WRSLT_PUT_LOC", toRcd.getFieldString("YD_STK_COL_GP").trim()
												+ toRcd.getFieldString("YD_STK_LYR_NO").trim()
												+ T_YD_FEVER_GP);//
			updPara.setField("V_CRANE_WRSLT_PUT_FUNC", "");//
			updPara.setField("V_YD_USER_ID", inDto.getParam("YD_USER_ID").trim());//
			                        
			rtnCd = dao.inspPlatewrkResult(updPara);
			if(rtnCd != 1){
				System.out.println("\n\nERROR -> 저장위치 이력등록 실패\n\n");
				//rtnPara.setField("RTN_CD", "-1");
				//rtnPara.setField("RTN_MSG", "저장위치 이력등록 실패");
				//this.m_ctx.setRollbackOnly();
				//return rtnPara;
			}
			rtnPara.setField("RTN_CD", "1");
			rtnPara.setField("RTN_MSG", "정상 처리되었습니다.");
			
		} catch (Exception e) {
			this.m_ctx.setRollbackOnly();
			e.printStackTrace();
		}
		
		return rtnPara;
	}
	
	
	/**
	 * 후판정정야드가스절단실적(PRYDJ007)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void rcvpPlateYdGascutresult(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		PlateReviseDao 	plateRevisedao 		= new PlateReviseDao();
		YdStockDao      ydStockDao      = new YdStockDao();

		// 레코드 선언
		JDTORecordSet rsGetCutStl  = null;
		JDTORecordSet rsOutRecSet 	 = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsResult   	 = null;
		JDTORecord recPara 		     = null;
		JDTORecord recPara2 		     = null;
		JDTORecord recSlab 		     = null;
		JDTORecord recEditRec	     = null;
		JDTORecord outRecTemp        = null;
		JDTORecord recGetVal         = null;
		
		JDTORecord recMtl = null;
		
		// 변수선언
		String szMethodName 		 = "rcvpPlateYdGascutresult";
		String szMsg 				 = "";
		String szOperationName       = "후판정정야드가스절단실적";
		String szRcvTcCode			 = ydUtils.getTcCode(inRecord);
		String szPARENT_SLAB_NO      = "";
		String szWORK_YD_GP = "";
		
		
		String szSTL_NO              = "";
		String szYdGp                = "";
		String szYD_STK_COL_GP       = "";
		String szSLAB_WO_RT_CD       = "";
		String szYD_STK_LOT_CD       = "";	
		String iTon = "";
		String szMTL_STAT_CD = "";
		int intRtnVal 				 = 0;
		int nRet                     = 0;
		
		
		if(szRcvTcCode==null){
			szMsg ="[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try{
			
			String szCUT_WRK_PROG = ydDaoUtils.paraRecChkNull(inRecord, "CUT_WRK_PROG");
			
			szPARENT_SLAB_NO = ydDaoUtils.paraRecChkNull(inRecord, "CUT_STL_NO");
			
			int strlen = szCUT_WRK_PROG.length();
			
			if(strlen == 4)
			{
				szWORK_YD_GP = getcutYdGp_CODE(szCUT_WRK_PROG.substring(2,4));
			}
			else if(strlen == 2)
			{
				szWORK_YD_GP = getcutYdGp_CODE(szCUT_WRK_PROG);
			}
			
			
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szPARENT_SLAB_NO);
			rsGetCutStl = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(recPara, rsGetCutStl, 502);
			rsGetCutStl.absolute(1);
			recMtl = rsGetCutStl.getRecord();
			szMTL_STAT_CD = ydDaoUtils.paraRecChkNull(recMtl,"MTL_STAT_CD");
			
			if(!szMTL_STAT_CD.equals("3"))
			{
				recPara2 = JDTORecordFactory.getInstance().create();
				recPara2.setField("STL_NO", szPARENT_SLAB_NO);
				recPara2.setField("STK_YD_GP", szWORK_YD_GP);
				
				szMsg = "[후판정정야드가스절단실적 등록데이터"+ szSTL_NO + szWORK_YD_GP + iTon;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				szMsg="----------------- 후판정정야드 야드맵 업데이트   시작----------------";
				plateRevisedao.updatepPlateYdGascutresult(recPara2);
				szMsg="----------------- 후판정정야드 야드맵 업데이트   완료----------------";
				
				
			}
			
		}catch(Exception e){
			szMsg = "[후판정정야드가스절단실적] Exception Error :: " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[후판정정야드가스절단실적]" + szMsg);
		} // end of try-catch
		ydUtils.putLog(szSessionName, szMethodName, "[후판정정야드가스절단실적] 처리("+szMethodName+") 완료",4);
	} // end of procCsShearWr()
	

	
	/**
	 * 후판정정야드가스절단실적/ Book/In 실적처리(PRYDJ007)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void rcvpPlateYdSetoutStl(JDTORecord inRecord)throws JDTOException  {
		// DAO 및 UTIL 객체 생성
		PlateReviseDao 	plateRevisedao 		= new PlateReviseDao();
		YdStockDao      ydStockDao      = new YdStockDao();

		// 레코드 선언
		JDTORecordSet rsGetCutStl  = null;
		JDTORecordSet rsOutRecSet 	 = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsResult   	 = null;
		JDTORecord recPara 		     = null;
		JDTORecord recPara2 		     = null;
		JDTORecord recSlab 		     = null;
		JDTORecord recEditRec	     = null;
		JDTORecord outRecTemp        = null;
		JDTORecord recGetVal         = null;
		
		JDTORecord recMtl = null;
		
		// 변수선언
		String szMethodName 		 = "rcvpPlateYdSetoutStl";
		String szMsg 				 = "";
		String szOperationName       = "후판정정야드가스절단실적/ Book/In 실적처리";
		String szRcvTcCode			 = ydUtils.getTcCode(inRecord);
		String szPARENT_SLAB_NO      = "";
		String szWORK_YD_GP = "";
		
		
		String szSTL_NO              = "";
		String szYdGp                = "";
		String szYD_STK_COL_GP       = "";
		String szSLAB_WO_RT_CD       = "";
		String szYD_STK_LOT_CD       = "";	
		String iTon = "";
		int intRtnVal 				 = 0;
		int nRet                     = 0;
		
		
		if(szRcvTcCode==null){
			szMsg ="[ERROR] " + szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		try{
			
			
			szWORK_YD_GP = getcutYdGp_CODE(ydDaoUtils.paraRecChkNull(inRecord, "CUT_WRK_PROG"));
			szPARENT_SLAB_NO = ydDaoUtils.paraRecChkNull(inRecord, "CUT_STL_NO");
			
			recPara2 = JDTORecordFactory.getInstance().create();
			recPara2.setField("STL_NO", szPARENT_SLAB_NO);

			
			szMsg = "[후판정정야드가스절단실적/ Book/In 실적처리"+ szSTL_NO + szWORK_YD_GP + iTon;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			szMsg="----------------- 후판정정야드 야드맵 업데이트   시작----------------";
			plateRevisedao.rcvpPlateYdSetoutStl(recPara2);
			szMsg="----------------- 후판정정야드 야드맵 업데이트   완료----------------";
			
		
			
		}catch(Exception e){
			szMsg = "[후판정정야드가스절단실적/ Book/In 실적처리] Exception Error :: " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException("[후판정정야드가스절단실적/ Book/In 실적처리]" + szMsg);
		} // end of try-catch
		ydUtils.putLog(szSessionName, szMethodName, "[후판정정야드가스절단실적/ Book/In 실적처리] 처리("+szMethodName+") 완료",4);
	} // end of procCsShearWr()
	
	
	
	/**
	 * 위치별적치현황 LIST (PDA)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.20
	 */
	public JDTORecordSet deleteBookoutStlList(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
	    int rtnCd = 0;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 		
			
			recPara.setField("SCH_YN", "Y");
			recPara.setField("YD_USER_ID", 	"frtoreg");
			recPara.setField("STL_NO", inDto.getParam("STL_NO").trim());
		
			
			// DAO 호출
			rtnCd  = dao.deleteBookoutStlList(recPara);
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	/**
	 * 위치별적치현황 야드별 방침 조회 
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.20
	 */
	public JDTORecordSet getPlateYdlocList_REMARK(GridData inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_GP", 		inDto.getParam("YD_GP").trim());	    /*야드*/
			
			// DAO 호출
			outRecSet = dao.getPlateYdlocList_REMARK(recPara);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	
	/**
	 * 단조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord getLocMgtCodeLayerList_L(JDTORecord inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		JDTORecord 		tmpPara		= null;
		JDTORecord 		rtnPara		= null;
		String			szComboList = "";
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_STK_COL_GP", 	inDto.getFieldString("T_YD_EQP_GP").trim()+
													inDto.getFieldString("T_YD_BED_GP").trim());	/*스판*/


			
			// DAO 호출
			outRecSet = dao.getLocMgtCodeLayerList_L(recPara);
			
			if(outRecSet != null && outRecSet.size() >0){
				for(int i=0; i<outRecSet.size(); i++){
					tmpPara = outRecSet.getRecord(i);
					szComboList += tmpPara.getFieldString("YD_STK_LYR_NO").trim() +"||";
					szComboList += tmpPara.getFieldString("YD_STK_LYR_NO").trim() +"**";
				}
			}
			if(szComboList.length() > 3){
				szComboList = szComboList.substring(0, szComboList.length()-2);
			}
			
			rtnPara = JDTORecordFactory.getInstance().create();
			rtnPara.setField("COMBOLIST", szComboList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnPara;
	}
	
	/**
	 * 단조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord getLocMgtCodeLayerList(JDTORecord inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		JDTORecord 		tmpPara		= null;
		JDTORecord 		rtnPara		= null;
		String			szComboList = "";
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_STK_COL_GP", 	inDto.getFieldString("T_YD_EQP_GP").trim()+
					inDto.getFieldString("T_YD_BED_GP").trim());	/*스판*/
			
			
			
			// DAO 호출
			outRecSet = dao.getLocMgtCodeLayerList(recPara);
			
			if(outRecSet != null && outRecSet.size() >0){
				for(int i=0; i<outRecSet.size(); i++){
					tmpPara = outRecSet.getRecord(i);
					szComboList += tmpPara.getFieldString("YD_STK_LYR_NO").trim() +"||";
					szComboList += tmpPara.getFieldString("YD_STK_LYR_NO").trim() +"**";
				}
			}
			if(szComboList.length() > 3){
				szComboList = szComboList.substring(0, szComboList.length()-2);
			}
			
			rtnPara = JDTORecordFactory.getInstance().create();
			rtnPara.setField("COMBOLIST", szComboList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnPara;
	}
	
	
	/**
	 * 번지조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord getpPlateYdLocationList(JDTORecord inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		JDTORecord 		tmpPara		= null;
		JDTORecord 		rtnPara		= null;
		String			szComboList = "";
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_STK_COL_GP", 	inDto.getFieldString("T_YD_EQP_GP").trim()+
													inDto.getFieldString("T_YD_BED_GP").trim());	/*스판*/
			recPara.setField("V_YD_STK_LYR_NO", 	inDto.getFieldString("T_YD_STK_LYR_NO").trim());	/*배드*/
			
			
			// DAO 호출
			outRecSet = dao.getpPlateYdLocationList(recPara);
			
			if(outRecSet != null && outRecSet.size() >0){
				for(int i=0; i<outRecSet.size(); i++){
					tmpPara = outRecSet.getRecord(i);
					szComboList += tmpPara.getFieldString("YD_STK_BED_NO_L").trim() +"||";
					szComboList += tmpPara.getFieldString("YD_STK_BED_NO_L").trim() +"**";
				}
			}
			if(szComboList.length() > 3){
				szComboList = szComboList.substring(0, szComboList.length()-2);
			}
			
			rtnPara = JDTORecordFactory.getInstance().create();
			rtnPara.setField("COMBOLIST", szComboList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnPara;
	}
	
	
	
	
	
	
	
	
    
	private String getcutYdGp_CODE(String cutYdGp){
		
		String cutYdGp_CD	= "";
		
		if(cutYdGp.equals("15")){
			cutYdGp_CD = "06";
		}else if(cutYdGp.equals("16")){
			cutYdGp_CD = "07";
		}else if(cutYdGp.equals("17")){
			cutYdGp_CD = "10";
		}else if(cutYdGp.equals("18")){
			cutYdGp_CD = "13";
		}else if(cutYdGp.equals("19")){
			cutYdGp_CD = "14";
		}else if(cutYdGp.equals("1T")){
			cutYdGp_CD = "01";
		}else if(cutYdGp.equals("1U")){
			cutYdGp_CD = "04";
		}
		
		return cutYdGp_CD;
	}
	
	
	//야드코드변환(조업공정코드로 변환)
	private String getprCurrState_CODE(String YdeqpGp){
		
		String prYdGp_CD	= "";
		
		if(YdeqpGp.equals("01")){
			prYdGp_CD = "1T";
		}else if(YdeqpGp.equals("02")){
			prYdGp_CD = "1W";
		}else if(YdeqpGp.equals("03")){
			prYdGp_CD = "1X";
		}else if(YdeqpGp.equals("04")){
			prYdGp_CD = "1U";
		}else if(YdeqpGp.equals("05")){
			prYdGp_CD = "1D";
		}else if(YdeqpGp.equals("06")){
			prYdGp_CD = "15";
		}else if(YdeqpGp.equals("07")){
			prYdGp_CD = "16";
		}else if(YdeqpGp.equals("08")){
			prYdGp_CD = "1P";
		}else if(YdeqpGp.equals("09")){
			prYdGp_CD = "1V";
		}else if(YdeqpGp.equals("10")){
			prYdGp_CD = "17";
		}else if(YdeqpGp.equals("11")){
			prYdGp_CD = "1R";
		}else if(YdeqpGp.equals("12")){
			prYdGp_CD = "1Q";
		}else if(YdeqpGp.equals("13")){
			prYdGp_CD = "18";
		}else if(YdeqpGp.equals("14")){
			prYdGp_CD = "19";
		}else if(YdeqpGp.equals("15")){
			prYdGp_CD = "19";
		}else if(YdeqpGp.equals("16")){
			prYdGp_CD = "19";
		}else if(YdeqpGp.equals("17")){
			prYdGp_CD = "19";
		}else if(YdeqpGp.equals("UST야드")){
			prYdGp_CD = "";
		}
		
		return prYdGp_CD;
	}
	
	
	//운전실코드변환(조업공정코드로 변환)
	private String getprYDLOC_CODE(String YdLocGp){
		
		String prYdLoc_CD	= "";
		
		if(YdLocGp.equals("HMD")){
			prYdLoc_CD = "";
		}else if(YdLocGp.equals("US")){
			prYdLoc_CD = "1G";
		}else if(YdLocGp.equals("CS")){
			prYdLoc_CD = "1H";
		}else if(YdLocGp.equals("DSS")){
			prYdLoc_CD = "1J";
		}else if(YdLocGp.equals("DS")){
			prYdLoc_CD = "1K";
		}else if(YdLocGp.equals("CMD")){
			prYdLoc_CD = "1P";
		}else if(YdLocGp.equals("GT")){
			prYdLoc_CD = "1M";
		}else if(YdLocGp.equals("WT")){
			prYdLoc_CD = "1R";
		}else if(YdLocGp.equals("SB")){
			prYdLoc_CD = "1Q";
		}else if(YdLocGp.equals("CR")){
			prYdLoc_CD = "1L";
		}else if(YdLocGp.equals("#1GAS")){
			prYdLoc_CD = "1T";
		}else if(YdLocGp.equals("#2GAS")){
			prYdLoc_CD = "1U";
		}else if(YdLocGp.equals("극후물GAS")){
			prYdLoc_CD = "15";
		}else if(YdLocGp.equals("전단GAS")){
			prYdLoc_CD = "16";
		}else if(YdLocGp.equals("보수장GAS")){
			prYdLoc_CD = "17";
		}else if(YdLocGp.equals("열처리GAS")){
			prYdLoc_CD = "18";
		}else if(YdLocGp.equals("제품창고#1GAS")){
			prYdLoc_CD = "19";
		}else if(YdLocGp.equals("제품창고#2GAS")){
			prYdLoc_CD = "";
		}else if(YdLocGp.equals("제품창고#3GAS")){
			prYdLoc_CD = "";
		}else if(YdLocGp.equals("제품창고#4GAS")){
			prYdLoc_CD = "";
		}
		
		
		
		return prYdLoc_CD;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////	
	

	/**
	 * 이적대상재 등록 (PDA) 
	 * @ejb.interface-method
	 * @param inDto
	 * @return
	 */
	public JDTORecord updpPlateYdCrnDownListPDA(JDTORecord inDto) {
	
		int 			intRtnVal 		= 0;
		int 			res		 		= 0;
		String 			szRtnMsg		= "";
		
		PlateReviseDao 	dao 			= new PlateReviseDao();
		
		JDTORecord 		retRcd			= JDTORecordFactory.getInstance().create(); 
		JDTORecord 		recPara			= null;
		try {
			
			// 파라미터 셋팅 
			String hval_chk		= "";
			String sModifier 	= ""; /*수정자*/
			String sFrYdColGp 	= ""; /*From 저장위치*/
			String sToYdCol_gp 	= ""; /*To 저장위치*/
			String sStlNo 		= ""; /*재료번호*/
			
			//내부 Process 연결
			EJBConnector ejbConn = new EJBConnector("default", this);
			
			String sTotalCnt = StringHelper.evl(inDto.getFieldString("total_list_cnt"),"");
			
			int    iTotalCnt = 0;
			if(!"".equals(sTotalCnt)){
				iTotalCnt = Integer.parseInt(sTotalCnt);
			} 
			
			for(int i=0; i < iTotalCnt; i++){
			
				hval_chk = inDto.getFieldString("hval_chk_"+i);
				
				if(!hval_chk.equals("")){
					
					sModifier	= inDto.getFieldString("YD_USER_ID");
					sFrYdColGp	= inDto.getFieldString("YD_COL_GP");
					sToYdCol_gp	= inDto.getFieldString("hval_to_loc_"+hval_chk);
					sStlNo		= inDto.getFieldString("hval_stl_"+hval_chk);
			
					recPara		= JDTORecordFactory.getInstance().create();
					recPara.setField("YD_GP", 	          	sFrYdColGp.substring(0, 1));
					recPara.setField("YD_BAY_GP", 	      	sFrYdColGp.substring(1, 2));
					recPara.setField("YD_SPAN_GP", 	      	sFrYdColGp.substring(2, 4));
					recPara.setField("YD_MAIN_WRK_GP", 	  	"1");	// 이적
					recPara.setField("YD_TO_LOC_GUIDE_GP",	"Y");	// Y
					recPara.setField("YD_AIM_YD_GP", 	  	sToYdCol_gp.substring(0, 1));
					recPara.setField("YD_AIM_BAY_GP", 	  	sToYdCol_gp.substring(1, 2));
					recPara.setField("YD_AIM_SPAN_GP", 	  	sToYdCol_gp.substring(2, 4));
					recPara.setField("YD_AIM_COL_GP", 	  	sToYdCol_gp.substring(4, 6));
					recPara.setField("YD_AIM_BED_NO", 	  	"01");	// 01베드 Default
					recPara.setField("YD_TO_LOC_GUIDE", 	sToYdCol_gp+"01");
					recPara.setField("YD_EQP_WRK_SH", 	  	"1");	// 1매 Default
					recPara.setField("STL_LIST", 	        sStlNo);
					recPara.setField("YD_USER_ID", 	        sModifier);
					
					szRtnMsg = (String)ejbConn.trx("PlateReviseSeEJB", "procPrepLotCompByCapa", recPara);
					
					res += 1;
				}
			}
			
			if(res > 0){
				retRcd.setField("RTN_CD", "1");
				retRcd.setField("RTN_MSG", "정상 처리되었습니다.");
			}else{
				retRcd.setField("RTN_CD", "-1");
				retRcd.setField("RTN_MSG", "이적등록 처리실패 ");
			}
			
		} catch (Exception e) {
			this.m_ctx.setRollbackOnly();
			e.printStackTrace();
		}
			 
		return retRcd;
	}
	
	/**
	 * 위치별적치현황 LIST (PDA)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 윤재광
	 * @작성일 : 2013.09.20
	 */
	public JDTORecordSet getPDApPlateYdLocList(JDTORecord inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_COL_GP",		inDto.getFieldString("YD_COL_GP").trim());	    /*스판(야드)*/
			recPara.setField("V_YD_BED_GP", 	inDto.getFieldString("YD_BED_GP").trim());	    /*배드*/
			
			// DAO 호출
			outRecSet = dao.getPDApPlateYdLocList(recPara);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRecSet;
	}
	
	/**
	 * 북인작업 대상재 정보 가져오기(PDA) 
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 윤재광
	 * @작성일 : 2013.08.17
	 */
	public JDTORecord getpPlateYdBookoutStlList(JDTORecord inDto) {
		JDTORecordSet   outRecSet  	= null;
		
		PlateReviseDao 	dao 		= new PlateReviseDao();
		JDTORecord 		recPara		= null;
		JDTORecord 		tmpPara		= null;
		JDTORecord 		rtnPara		= null;
		String			szComboList = "";
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_COL_GP", 	inDto.getFieldString("YD_COL_GP").trim());	/*스판*/
			recPara.setField("V_YD_BED_GP", 	inDto.getFieldString("YD_BED_GP").trim());	    /*배드*/
			
			// DAO 호출
			outRecSet = dao.getpPlateYdBookoutStlList(recPara);
			
			if(outRecSet != null && outRecSet.size() >0){
				for(int i=0; i<outRecSet.size(); i++){
					tmpPara = outRecSet.getRecord(i);
					
					szComboList += tmpPara.getFieldString("STL_NO").trim() +"||";					
					szComboList += tmpPara.getFieldString("STL_NO").trim() +"**";
				}
			}
			if(szComboList.length() > 3){
				szComboList = szComboList.substring(0, szComboList.length()-2);
			}
			
			rtnPara = JDTORecordFactory.getInstance().create();
			rtnPara.setField("COMBOLIST", szComboList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnPara;
	}
	
	/**
	 * 스트링이 정수인지를 확인한다.
	*/
    public static boolean isInteger(String str) {
        boolean rtn = false;
        try {
			Integer.parseInt(str);
			rtn = true;
		} catch(Exception ignore) {
			rtn = false;
		} 
		return rtn;
    }
	
    /** 
	 * 저장위치 [PM45호 및 산적 LOT 수정] 조회 (1후판정정야드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getPlateYdStkPosFix(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		String szStkCol = null;
		String szStkBed = null;
		String temp = null;
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();		
		String szMsg        = "";		
		String szMethodName = "getPlateYdStkPosFix";
		int intRtnVal = 0;
		
		try {
			
			temp = yddatautil.setDataDefault(inDto.getFieldString("STKPOS"), "");
			
			if(temp.length() == 8){
				szStkCol = temp.substring(0, 6);
				szStkBed = temp.substring(6, 8);
			}else{
				szStkCol = temp.substring(0, 6);
				szStkBed = "01";
			}
			recPara.setField("YD_STK_COL_GP", szStkCol);
			recPara.setField("YD_STK_BED_NO", szStkBed);
			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 55);
			
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}      
				//return outRecSet;
				return outRecSet;
			} // end of if
			
			outRecSet.first();  
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	// end of getPlateYdStkPosFix
    
	/** 
	 * [PM45호 산적 LOT 수정](1후판정정야드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String updPlateYdStkPosFix(JDTORecord [] inDto) {
		
		int intRtnVal 			= 0;
		String szMsg  			="";
		String szMethodName		="updPlateYdStkPosFix";
		String szOperationName	= "저장위치 수정";
		String szStlNo 			= null;
		String szStkColGp 		= null;
		String szStkBedNo 		= null;
		String szStkLyrNo 		= null;
		String szModifier 		= null;
		String szYdGp 			= null;
		String szModifyGbn		= null;
		
		JDTORecord      recPara      = null;
		JDTORecord      recL2Para    = null;

		JDTORecordSet   rsTemp  		= null;
		JDTORecordSet   rsBefoLyrInfo  	= null;
 
		YdStkLyrDao ydStkLyrDao 	= new YdStkLyrDao();

		JDTORecord recBefoLyrInfo  	= null;

		String szRtnValue = YdConstant.RETN_CD_SUCCESS;

		try{
			szMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작   ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			/*********************************************************
			 * 추가 로직(후판 제품 용)
			 * UPDATE 될 위치의 적치단이 없을 경우 먼저 체크해서
			 * 리턴해준다.(첫번째 온 정보(최상단 정보먼저 체크한다)
			 ********************************************************/

			// 처리 할 필요없는 경우
			if (inDto.length < 1 ){

				szMsg = "[Jsp Session : "+szOperationName+"] 변경된 정보가 없습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnValue = "변경된 정보가 없습니다";
				return szRtnValue ;
			}

			recPara = JDTORecordFactory.getInstance().create();
			recPara = inDto[0];

			rsTemp = JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");

			szMsg = "[Jsp Session : "+szOperationName+"] 적치단정보 조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp, 0);

			//단정보가 존재 하지않거나  단데이터 생성이 되지않는경우
			if(intRtnVal  < 1){

				szMsg = "[Jsp Session : "+szOperationName+"] 적치단 정보가 존재하지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnValue = "적치단 정보가 존재하지 않습니다";
				return szRtnValue;
			}

			/*********************(Check End)***************************************/

			for(int iLoop=0; iLoop<inDto.length; iLoop++){

				recPara = JDTORecordFactory.getInstance().create();
				// 1. 적치 단 정보 UPDATE
				szStlNo = yddatautil.setDataDefault(inDto[iLoop].getField("STL_NO"), "");
				recPara.setField("STL_NO", szStlNo);
				//적치열구분
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), ""));
				//적치베드
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"), ""));
				//적치단
				recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"), ""));

				szStkColGp = recPara.getFieldString("YD_STK_COL_GP");
				szStkBedNo = recPara.getFieldString("YD_STK_BED_NO");
				szStkLyrNo = recPara.getFieldString("YD_STK_LYR_NO");

				szMsg = "신규 재료["+szStlNo+"] 위치 정보 : 열["+szStkColGp+"], 베드["+szStkBedNo+"], 단[" + szStkLyrNo + "]";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

				szModifier 	= inDto[iLoop].getFieldString("MODIFIER");
				
				szModifyGbn = inDto[iLoop].getFieldString("MODIFY_GBN");

				recPara.setField("MODIFIER", szModifier);

				szYdGp =  szStkColGp.substring(0,1); //야드구분

				//적치 상태 [재료번호가 존재 :   "C" , 미존재 : "E"]
				if ( "".equals(szStlNo)){

					recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				} else {

					recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				}

				// 해당 베드의 위치정보가 기존정보의 재료정보랑 같은 경우는 PASS한다.[2009.12.01 이현성]
				rsBefoLyrInfo  = JDTORecordFactory.getInstance().createRecordSet("rsBefoLyrInfo");

				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBefoLyrInfo, 0);

				if(intRtnVal > 0){

					rsBefoLyrInfo.first();
					recBefoLyrInfo =  rsBefoLyrInfo.getRecord();

					if(ydDaoUtils.paraRecChkNull(recBefoLyrInfo, "STL_NO").equals(szStlNo)){
						//기존재료 정보와 해당 정보는 같은 정보 이므로 UPDATE 하지 않는다.

						szMsg = "변경되지  재료 정보는 UPDATE 하지 않습니다.";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						continue;
					}

				}

				//적치단 정보 UPDATE
				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
				
				String sL2StlNo = "";
				
				if("1".equals(szModifyGbn)){
					sL2StlNo = szStlNo;
					ydUtils.putLog(szSessionName, szMethodName, sL2StlNo+"/"+szModifyGbn, YdConstant.INFO);
				}else{
					sL2StlNo = yddatautil.setDataDefault(inDto[iLoop].getField("HIDDEN_STL_NO"), "");
					ydUtils.putLog(szSessionName, szMethodName, sL2StlNo+"/"+szModifyGbn, YdConstant.INFO);
				}
				
				   
			   /* 
			    * L2 전문송신 -  북 인/아웃 실적
	        	*/ 
	        	recL2Para = JDTORecordFactory.getInstance().create();
	        	recL2Para.setField("MSG_ID"         , "YDY9L001");
	        	recL2Para.setField("OPERATION_TYPE" , szModifyGbn);
	        	recL2Para.setField("STL_NO"         , sL2StlNo);
	        	recL2Para.setField("YD_STK_COL_GP"  , szStkColGp);
	        	recL2Para.setField("YD_STK_BED_NO"  , szStkBedNo);
	        	recL2Para.setField("YD_STK_LYR_NO"  , szStkLyrNo);
	        	
	        	ydDelegate.sendMsg(recL2Para);

	        }

			szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			return szRtnValue;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}	// end of updPlateYdStkPosFix
	
	/**
	 * 1후판정정야드 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String updpPlateYdStkPosFix(JDTORecord [] inDto) {

		int intRtnVal 			= 0;
		String szMsg  			="";
		String szMethodName		="updpPlateYdStkPosFix";
		String szOperationName	= "저장위치 수정";
		String szStlNo 			= null;
		String szStkColGpFrom 	= null;
		String szStkBedNoFrom 	= null;
		String szStkLyrNoFrom 	= null;
		String szStkColGp 		= null;
		String szStkBedNo 		= null;
		String szStkLyrNo 		= null;
		String szModifier 		= null;
		String szSTL_PROG_CD 	= null;

		JDTORecord      recPara      = null;
		JDTORecord      newPara      = null;
		JDTORecord      recL2Para    = null;
		JDTORecord      recTemp      = null;
		JDTORecord      recStock     = null;

		JDTORecordSet   slabCommRecSet  = null;
		JDTORecordSet   rsTemp  		= null;
		JDTORecordSet   rsBefoLyrInfo  	= null;

		YdStkLyrDao ydStkLyrDao 	= new YdStkLyrDao();
		JPlateYdStockDAO ydDao 		= new JPlateYdStockDAO();
		YdDelegate ydDelegate 		= new YdDelegate();
		YdWrkHistDao ydWrkHistDao 	= new YdWrkHistDao();
		YdSchRuleDao ydSchRuleDao  	= new YdSchRuleDao();

		boolean bHistFlag 			= false;
		JDTORecordSet rsDelInfo 	= null;
		JDTORecordSet rsStockInfo 	= null;
		JDTORecordSet rsCommInfo 	= null;

		JDTORecord recBefoLyrInfo  	= null;
		JDTORecord recCommInfo 		= null;
		JDTORecord logRecord 		= null;

		String szRtnValue = YdConstant.RETN_CD_SUCCESS;

		String szYdGp 		= null;
		String szLoc 		= null;
		String szLocColGp 	= null;
		String szLocLyrNo 	= null;

		try{
			szMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작   ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			// 처리 할 필요없는 경우
			if (inDto.length < 1 ){

				szMsg = "[Jsp Session : "+szOperationName+"] 변경된 정보가 없습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnValue = "변경된 정보가 없습니다";
				return szRtnValue ;
			}

			recPara = JDTORecordFactory.getInstance().create();
			recPara = inDto[0];

			rsTemp = JDTORecordFactory.getInstance().createRecordSet("YdSlabTemp");

			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsTemp, 0);

			//단정보가 존재 하지않거나  단데이터 생성이 되지않는경우
			if(intRtnVal  < 1){

				szMsg = "[Jsp Session : "+szOperationName+"] 적치단 정보가 존재하지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnValue = "적치단 정보가 존재하지 않습니다";
				return szRtnValue;
			}

			/*********************(Check End)***************************************/

			for(int iLoop=0; iLoop<inDto.length; iLoop++){

				recPara = JDTORecordFactory.getInstance().create();
				// 1. 적치 단 정보 UPDATE
				szStlNo = yddatautil.setDataDefault(inDto[iLoop].getField("STL_NO"), "");
				recPara.setField("STL_NO", szStlNo);
				//적치열구분
				recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), ""));
				//적치베드
				recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"), ""));
				//적치단
				recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"), ""));

				szStkColGp = recPara.getFieldString("YD_STK_COL_GP");
				szStkBedNo = recPara.getFieldString("YD_STK_BED_NO");
				szStkLyrNo = recPara.getFieldString("YD_STK_LYR_NO");

				szMsg = "신규 재료["+szStlNo+"] 위치 정보 : 열["+szStkColGp+"], 베드["+szStkBedNo+"], 단[" + szStkLyrNo + "]";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

				szModifier = inDto[iLoop].getFieldString("MODIFIER");

				recPara.setField("MODIFIER", szModifier);

				szYdGp =  szStkColGp.substring(0,1); //야드구분

				//적치 상태 [재료번호가 존재 :   "C" , 미존재 : "E"]
				if ( "".equals(szStlNo)){

					bHistFlag = false;
					recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				} else {

					bHistFlag = true;
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				}

				// 해당 베드의 위치정보가 기존정보의 재료정보랑 같은 경우는 PASS한다
				rsBefoLyrInfo  = JDTORecordFactory.getInstance().createRecordSet("rsBefoLyrInfo");

				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsBefoLyrInfo, 0);

				if(intRtnVal > 0){

					rsBefoLyrInfo.first();
					recBefoLyrInfo =  rsBefoLyrInfo.getRecord();

					if(ydDaoUtils.paraRecChkNull(recBefoLyrInfo, "STL_NO").equals(szStlNo)){
						//기존재료 정보와 해당 정보는 같은 정보 이므로 UPDATE 하지 않는다.

						szMsg = "변경되지  재료 정보는 UPDATE 하지 않습니다.";
						ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);

						bHistFlag = false;

						continue;
					}

				}

				/*
				 *  이적작업  이력정보에 추가
				 *  bHistFlag - 이력정보 생성 유무 Flag
				 */
				if(bHistFlag){

					JDTORecord recOldPos = 	JDTORecordFactory.getInstance().create();
					JDTORecord recStockInfo = JDTORecordFactory.getInstance().create();

					JDTORecord recWrkHistPara = JDTORecordFactory.getInstance().create();
					JDTORecord recWrkHistInfo = JDTORecordFactory.getInstance().create();
					JDTORecordSet outRecSet = null;
					
					{// 2후판 정정야드 재료정보 생성모듈 호출
						
						JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
						JDTORecord    recStlNo = JDTORecordFactory.getInstance().create();
						recStlNo  = JDTORecordFactory.getInstance().create();
						recStlNo.setField("STL_NO",		szStlNo);			// 재료번호
	
						intRtnVal = ydDao.getYdStockWithLoc(recStlNo, rsResult);
	
						// 재료 정보 존재여부 체크
						if (intRtnVal < 1) {
							intRtnVal = ydDao.insYdStockBookOut(recStlNo);
						}
					}
					
					rsDelInfo		=	JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
					String lszYdSchCd = "";
					
					recOldPos.setField("STL_NO", szStlNo);
					recOldPos.setField("YD_STK_LYR_MTL_STAT", "C");

					//	재료관련 정보로 이력정보를 추가적으로 넣을부분이 존재하면 재료정보를 조회한 후 이부분에 추가해줌
					rsStockInfo = JDTORecordFactory.getInstance().createRecordSet("rsStockInfo");

					intRtnVal = ydDao.getYdStock(recOldPos, rsStockInfo);

					// 재료 정보 이력정보에 추가 (2009.11.09)
					if(intRtnVal > 0){
						//STOCK 정보가 존재할 경우
						rsStockInfo.first();
						recStockInfo= rsStockInfo.getRecord();
					}
					
					
					intRtnVal  = ydStkLyrDao.getYdStklyr(recOldPos, rsDelInfo, 3);

					//작업이력 - 권상정보 관련 입력(적치단에 정보가 있을경우)
					if(intRtnVal > 0 ){
						rsDelInfo.first();

						recOldPos      = 	JDTORecordFactory.getInstance().create();
						recOldPos = rsDelInfo.getRecord();

						recStockInfo.setField("YD_UP_WR_LOC", ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_COL_GP")+  ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_BED_NO"));
						recStockInfo.setField("YD_UP_WR_LAYER", ydDaoUtils.paraRecChkNull(recOldPos, "YD_STK_LYR_NO") );
						//권상완료일시 --현재시간 구하는 정보
						recStockInfo.setField("YD_UP_CMPL_DT",YdUtils.getCurDate("yyyyMMddHHmmss") );
					}
					recStockInfo.setField("YD_GP", yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"), " ").substring(0, 1));
					recStockInfo.setField("STL_NO", szStlNo);

					//////////////////////////////////////////////////////////////////////////////

					// 작업이력 - 권하정보 입력
					recStockInfo.setField("YD_DN_WR_LOC",    yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"") +  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_BED_NO"),"") );
					recStockInfo.setField("YD_DN_WR_LAYER",  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_LYR_NO"),""));
					//권하완료일시 --현재시간 구하는 정보
					recStockInfo.setField("YD_DN_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));

					//이적 작업 스케줄 코드를 얻는다.
					lszYdSchCd = ydUtils.getMakeSchCdMM(  yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"      ").substring(0, 1) ,
						yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"      ").substring(1, 2),
						yddatautil.setDataDefault(inDto[iLoop].getField("YD_STK_COL_GP"),"      ").substring(2, 4) );

					szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 해당 이적작업 스케줄 구하기.["+ lszYdSchCd +"]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

					// 야드 스케줄 코드 - YD_SCH_CD
					recStockInfo.setField("YD_SCH_CD", lszYdSchCd );

					// 야드보조작업여부 - YD_AID_WRK_YN
					recStockInfo.setField("YD_AID_WRK_YN" , "N");

					outRecSet = JDTORecordFactory.getInstance().createRecordSet("outRecSet");

					//해당 스케줄 정보로 주작업 크레인 정보를 가지고 온다.
					recWrkHistPara.setField("YD_SCH_CD", lszYdSchCd);
					intRtnVal = ydSchRuleDao.getYdSchrule(recWrkHistPara, outRecSet, 0);

					if(intRtnVal > 0){
						outRecSet.first();
						recWrkHistInfo = outRecSet.getRecord();

					}else if (intRtnVal ==0){
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 해당 스케줄에 대한 기준정보가 존재 하지 않습니다.["+ lszYdSchCd +"]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}

					//스케줄 기준의 작업 크레인 정보를 넣어준다.
					recStockInfo.setField("YD_EQP_ID" , ydDaoUtils.paraRecChkNull(recWrkHistInfo, "YD_WRK_CRN"));

					// 야드수불구분 - YD_GNT_GP
					recStockInfo.setField("YD_GNT_GP", YdConstant.YD_GNT_GP_MVSTK);

					// 야드스케줄 기동 구분 "B" 로 넣어준다. -2009.12.10
					recStockInfo.setField("YD_SCH_ST_GP", "B");

					//이력정보 남기기
					intRtnVal = ydWrkHistDao.insYdWrkHistPosFix(recStockInfo);

					if(intRtnVal > 0){
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 이력정보를 로깅하였습니다." ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					}else{
						szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 이력정보를 로깅 실패 하였습니다" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				}
				
				JDTORecord recInTemp = null;
				String strLoc = "";

				// 해당 재료번호가 적치중일 경우 해당 적치 정보 삭제 기능 추가 ==============================================
				JDTORecord recDelPara 	= JDTORecordFactory.getInstance().create();
				rsDelInfo				= JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");

				if("".equals(szStlNo.trim())){
					//해당작업 필요없음

				}else{

					recDelPara.setField("STL_NO", szStlNo);
					recDelPara.setField("YD_STK_LYR_MTL_STAT", "");

					int nRtnVal  = ydStkLyrDao.getYdStklyr(recDelPara, rsDelInfo, 3);

					if(nRtnVal == 0 ){
						//해당 작업 필요없음

					}else if(nRtnVal > 0 ){

						//정보 존재시 해당 Map Clear
						rsDelInfo.first();
						
						do{
							recDelPara   = 	JDTORecordFactory.getInstance().create();
							recDelPara   =  rsDelInfo.getRecord();

							szStkColGpFrom = yddatautil.setDataDefault(recDelPara.getField("YD_STK_COL_GP"), "");
							szStkBedNoFrom = yddatautil.setDataDefault(recDelPara.getField("YD_STK_BED_NO"), "");
							szStkLyrNoFrom = yddatautil.setDataDefault(recDelPara.getField("YD_STK_LYR_NO"), "");

							szMsg = "기존 재료 위치 정보 : 열["+szStkColGpFrom+"], 베드["+szStkBedNoFrom+"], 단[" + szStkLyrNoFrom + "]";
							ydUtils.putLog("SlabJspSeEJB", szMethodName, szMsg, YdConstant.DEBUG);
							recDelPara.setField("STL_NO", "");
							recDelPara.setField("YD_STK_LYR_MTL_STAT", "E");

							ydStkLyrDao.updYdStklyr(recDelPara, 0);
							
							strLoc = szStkColGpFrom.substring(0,6);
							{
								/*
								 *  1후판압연전단 L2에 북아웃실적 전문 전송
								 */
								if("RT".equals(strLoc.substring(2,4))){
								    
								    this.procSmsSend(szStlNo,strLoc,1);
								}
								/*
								 *  1후판정정야드 L2에 북아웃실적 전문 전송
								 */ 
								if("PA0104".equals(strLoc)||"PB0401".equals(strLoc)){
								    
								    recInTemp = JDTORecordFactory.getInstance().create();
								    recInTemp.setField("MSG_ID"         , "YDY9L001");
								    recInTemp.setField("OPERATION_TYPE" , "2");
								    recInTemp.setField("STL_NO"         , szStlNo);
								    recInTemp.setField("YD_STK_COL_GP"  , szStkColGpFrom);
								    recInTemp.setField("YD_STK_BED_NO"  , szStkBedNoFrom);
								    recInTemp.setField("YD_STK_LYR_NO"  , szStkLyrNoFrom);
								    
								    ydDelegate.sendMsg(recInTemp);
								}
							}

						}while(rsDelInfo.next());
					}
				}

				//적치단 정보 UPDATE
				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
				
				{
				   /* 
					*  1후판압연전단 L2에 북인실적 전문 전송
					*/
					if("RT".equals(szStkColGp.substring(2,4))){
	
						this.procSmsSend(szStlNo,szStkColGp,0);
					}
	
				   /*
					*  1후판정정야드 L2에 북인실적 전문 전송
					*/
					if("PA0104".equals(szStkColGp)||"PB0401".equals(szStkColGp)){
	
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID"         , "YDY9L001");
						recInTemp.setField("OPERATION_TYPE" , "1");
						recInTemp.setField("STL_NO"         , szStlNo);
						recInTemp.setField("YD_STK_COL_GP"  , szStkColGp);
						recInTemp.setField("YD_STK_BED_NO"  , szStkBedNo);
						recInTemp.setField("YD_STK_LYR_NO"  , szStkLyrNo);
		
						ydDelegate.sendMsg(recInTemp);
					}  
				}

			}

			szMsg = "JSP-SESSION [저장위치[산적위치]수정 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			return szRtnValue;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}	// end of updpPlateYdStkPosFix
	/** 
	 * 저장위치 [산적 LOT 수정] 재료정보 요구 (1후판정정야드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void sendPlateYdStkPosFix(JDTORecord inDto) throws DAOException {
		JDTORecord       recL2Para         = JDTORecordFactory.getInstance().create();
		String temp = null;
		
		String szMsg        = "";		
		String szMethodName = "sendPlateYdStkPosFix";
		
		try {
			/* 
		    * L2 전문송신 -  저장품제원정보 요구
        	*/ 
        	recL2Para = JDTORecordFactory.getInstance().create();
        	recL2Para.setField("MSG_ID"     , "YDY9L002");
        	recL2Para.setField("YD_STR_LOC" , inDto.getFieldString("STKPOS")+"01");
        	
        	ydDelegate.sendMsg(recL2Para);
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of sendPlateYdStkPosFix
	
	/**
	 * 오퍼레이션명 :   1후판정정 PM45 북인/아웃 실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procPm45BookInOutWslt(JDTORecord msgRecord)throws JDTOException  {
		
		String szMsg		  	= "";
		String szMethodName	  	= "procPm45BookInOutWslt";
		/*
		OPERATION_TYPE	OPERATION_TYPE	CHAR	1	Y	1:Book In, 2:Book Out		
		PL_L2_TRK_NO	후판L2제품번호		CHAR	16				
		PL_MTL_NO		후판재료번호		CHAR	10				
		YD_STR_LOC		야드저장위치		CHAR	8	Y			
		YD_STK_LYR_NO	야드적치단번호		CHAR	3	Y			
		*/
		int intRtnVal 			= 0;		
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsPara    = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord recTmp 		= JDTORecordFactory.getInstance().create();
		
		YdStkLyrDao  ydStkLyrDao  = new YdStkLyrDao();
		
		PlateReviseDao commDao = new PlateReviseDao();	
		
		try{
			String szStlNo	= "";
			String szOPERATION_TYPE	= ydDaoUtils.paraRecChkNull(msgRecord,"OPERATION_TYPE");
			String szPL_L2_TRK_NO 	= ydDaoUtils.paraRecChkNull(msgRecord,"PL_L2_TRK_NO");
			String szPL_MTL_NO 		= ydDaoUtils.paraRecChkNull(msgRecord,"PL_MTL_NO");	
			String szYD_STR_LOC		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STR_LOC");
			String szYD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_LYR_NO");
			
			recPara.setField("STL_NO" , szPL_L2_TRK_NO);
			rsPara = commDao.getStlInfo(recPara);

			if(rsPara.size() > 0){

				rsPara.first();
				JDTORecord recStlInfo =  rsPara.getRecord();

				szStlNo = ydDaoUtils.paraRecChkNull(recStlInfo, "STL_NO");
				
				if("1".equals(szOPERATION_TYPE)){ //BOOK IN
					
					/*
					 * 북인시 수신받는 단정보를 활용하지 않음. 자체적으로 최상단 검색
					 */
					recPara 		= JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", szYD_STR_LOC.substring(0,6));
					recPara.setField("YD_STK_BED_NO", szYD_STR_LOC.substring(6,8));
			        
			        intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 98);
			        
			        if(intRtnVal >= 1) {
			        	outRecSet.first();
				        recTmp = outRecSet.getRecord();
				        
			        	szYD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(recTmp,"REAL_TOP_LYR");
			        }
				
					recPara 		= JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO" , szStlNo);
					recPara.setField("YD_STK_COL_GP" , szYD_STR_LOC.substring(0,6));
					recPara.setField("YD_STK_BED_NO" , szYD_STR_LOC.substring(6,8));
					recPara.setField("YD_STK_LYR_NO" , szYD_STK_LYR_NO);
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");

					//적치단 정보 UPDATE
					intRtnVal = commDao.updYdStklyr(recPara, 0);

				}else if("2".equals(szOPERATION_TYPE)){ //BOOK OUT
					
					/*
					 * 북아숫시 수신받는 저장위치 활용하지 않음. 재료번호 기준으로 저장위치 검색
					 */
					recPara 	= JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO" , szStlNo);
					recPara.setField("YD_STK_LYR_MTL_STAT", "C");
					
					intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 3);
					
					recPara   = JDTORecordFactory.getInstance().create();
					
					if(intRtnVal >= 1) {
						outRecSet.absolute(1);
						recTmp = outRecSet.getRecord();
						
						szYD_STR_LOC = ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_COL_GP");
						
						recPara.setField("STL_NO" , "");
						recPara.setField("YD_STK_COL_GP" , szYD_STR_LOC);
						recPara.setField("YD_STK_BED_NO" , ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_BED_NO"));
						recPara.setField("YD_STK_LYR_NO" , ydDaoUtils.paraRecChkNull(recTmp, "YD_STK_LYR_NO"));
						recPara.setField("YD_STK_LYR_MTL_STAT", "E");
						
						if(szYD_STR_LOC.startsWith("P")){// 1후판 정정야드에 있는 경우에만 삭제
							//적치단 정보 UPDATE
							intRtnVal = commDao.updYdStklyr(recPara, 0);
						}
						
					}else{
					
						recPara.setField("STL_NO" , "");
						recPara.setField("YD_STK_COL_GP" , szYD_STR_LOC.substring(0,6));
						recPara.setField("YD_STK_BED_NO" , szYD_STR_LOC.substring(6,8));
						recPara.setField("YD_STK_LYR_NO" , szYD_STK_LYR_NO);
						recPara.setField("YD_STK_LYR_MTL_STAT", "E");
						//적치단 정보 UPDATE
						intRtnVal = commDao.updYdStklyr(recPara, 0);
					}
				}
			}
			
		}catch(Exception e){
			
			szMsg = "[1후판정정 PM45 북인/아웃 실적] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

		szMsg = "[1후판정정 PM45 북인/아웃 실적] ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // procPm45BookInOutWslt
	
	/**
	 * 오퍼레이션명 :   1후판정정 PM45 저장품제원
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procPm45LocInfo(JDTORecord msgRecord)throws JDTOException  {
		  
		String szMsg		  	= "";
		String szMethodName	  	= "procPm45LocInfo";
		
		/*
		 	YD_STL_INFO_SND_SH	야드재료정보송신매수	NUMBER	3	Y
			YD_STL_INFO_SND_CNT	야드재료정보송신순번	NUMBER	3	Y
			PL_L2_TRK_NO		후판L2제품번호			CHAR	16	
			PL_MTL_NO			후판재료번호			CHAR	10	
			YD_STR_LOC			야드저장위치			CHAR	8	
			YD_STK_LYR_NO		야드적치단번호			CHAR	3	
		 */
		int intRtnVal 			= 0;		
		
		JDTORecordSet rsPara    = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		
		PlateReviseDao commDao = new PlateReviseDao();	
		
		try{
			String szStlNo = "";
			String szYD_STL_INFO_SND_SH		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STL_INFO_SND_SH");
			String szYD_STL_INFO_SND_CNT	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STL_INFO_SND_CNT");
			String szPL_L2_TRK_NO 			= ydDaoUtils.paraRecChkNull(msgRecord,"PL_L2_TRK_NO");
			String szPL_MTL_NO 				= ydDaoUtils.paraRecChkNull(msgRecord,"PL_MTL_NO");	
			String szYD_STR_LOC				= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STR_LOC");
			String szYD_STK_LYR_NO			= ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_LYR_NO");
			
			recPara.setField("STL_NO" , szPL_L2_TRK_NO);
			rsPara = commDao.getStlInfo(recPara);

			if(rsPara.size() > 0){

				rsPara.first();
				JDTORecord recStlInfo =  rsPara.getRecord();

				szStlNo = ydDaoUtils.paraRecChkNull(recStlInfo, "STL_NO");
					
				recPara 		= JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO" , szStlNo);
				recPara.setField("YD_STK_COL_GP" , szYD_STR_LOC.substring(0,6));
				recPara.setField("YD_STK_BED_NO" , szYD_STR_LOC.substring(6,8));
				recPara.setField("YD_STK_LYR_NO" , szYD_STK_LYR_NO);
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");

				//적치단 정보 UPDATE
				intRtnVal = commDao.updYdStklyr(recPara, 0);
			}
			
		}catch(Exception e){
			
			szMsg = "[1후판정정 PM45 저장품제원] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

		szMsg = "[1후판정정 PM45 저장품제원] ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // procPm45LocInfo
	
	/**
	 * 오퍼레이션명 :   1후판정정 PM45 L2 ID정보 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procPm45StlInfo(JDTORecord msgRecord)throws JDTOException  {
		  
		String szMsg		  	= "";
		String szMethodName	  	= "procPm45StlInfo";
		
		/*
		OPERATION_TYPE	OPERATION_TYPE	CHAR	1	Y	1:L2 ID정보 요구		
		PL_L2_TRK_NO	후판L2제품번호		CHAR	16				
		PL_MTL_NO		후판재료번호		CHAR	10				
		*/
		
		try{
			String szStlNo	= "";
			String szOPERATION_TYPE	= ydDaoUtils.paraRecChkNull(msgRecord,"OPERATION_TYPE");
			String szPL_L2_TRK_NO 	= ydDaoUtils.paraRecChkNull(msgRecord,"PL_L2_TRK_NO");
			String szPL_MTL_NO 		= ydDaoUtils.paraRecChkNull(msgRecord,"PL_MTL_NO");	
			
			JDTORecord recPara		= JDTORecordFactory.getInstance().create();
			recPara.setField("MSG_ID"         , "YDY9L003");
			recPara.setField("OPERATION_TYPE" , "1");
			recPara.setField("STL_NO" 		  , szPL_MTL_NO);
				
	        ydDelegate.sendMsg(recPara);
			
		}catch(Exception e){
			
			szMsg = "[1후판정정 PM45 L2 ID정보 요구] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

		szMsg = "[1후판정정 PM45 L2 ID정보 요구] ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // procPm45StlInfo
	
	/**
     * 오퍼레이션명 : 1후판정정야드 설비고장복구실적
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public void procY9EqpTrblRcvrWr(JDTORecord msgRecord) throws DAOException {
		
		// DAO객체 생성
    	YdEqpDao ydEqpDao   = new YdEqpDao();
    	
		// 레코드 선언
		JDTORecord recPara             = null;
        JDTORecord setCrnschRecord     = null;
        
        // 변수 선언
		String szMethodName         	= "procY9EqpTrblRcvrWr";
		String szMsg                   = "";
		String szOperationName         = "1후판정정야드 설비고장복구실적";
		String szRcvTcCode             = null;
		String szYD_EQP_ID             = "";
		String szYD_EQP_STAT           = "";
		String szYD_EQP_STAT_UPD       = "";
		int nRet                       = 0;

		
		try {
			
			setCrnschRecord = JDTORecordFactory.getInstance().create();

			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	        if(szYD_EQP_ID.equals("")){
	            szMsg = "설비ID가 존재하지 않습니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   	        
	        	return ;
	        }

	        szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_STAT");
	        if(szYD_EQP_STAT.equals("")){
	            szMsg = "설비상태가  존재하지 않습니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   	        
	        	return ;
	        }
	        
	        //============================================================================
			// 변환...
			//============================================================================
			if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
				// 고장
				szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_BREAK;
			}else if(szYD_EQP_STAT.equals("R") || 
					 szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_NORM)){
				// 복구
				szYD_EQP_STAT_UPD = YdConstant.YD_EQP_STAT_IDLE; 
			}
			//============================================================================
	        // 설비테이블에 야드설비상태 업데이트 
			//============================================================================
			ydUtils.putLog(szSessionName, szMethodName, szMethodName + ":: 설비 테이블에 업데이트 처리", YdConstant.DEBUG);			
 	        setCrnschRecord.setField("YD_EQP_ID"  , szYD_EQP_ID);   	// 설비ID
	        setCrnschRecord.setField("YD_EQP_STAT", szYD_EQP_STAT_UPD); // "B": 고장, "W": 대기 	
	            	
	    	nRet = ydEqpDao.updYdEqp(setCrnschRecord, 0);
	    	
	        if(nRet == -1){
                szMsg = "설비테이블 업데이트 중  Error : (YD_EQP_ID : " + szYD_EQP_ID + "),(YD_EQP_STAT : " + szYD_EQP_STAT + "), Ret : " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new JDTOException(szMsg);
	        }
	        
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szSessionName + e.getMessage(), e);
		}
		return ;
	} // end of procY9EqpTrblRcvrWr
	
	/**
     * 오퍼레이션명 : 1후판정정야드 크레인권상실적
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY9CrnLdWr(JDTORecord msgRecord) throws DAOException {
		
		YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
    	YdDelegate ydDelegate 	= new YdDelegate();
    	YdEqpDao   ydEqpDao   	= new YdEqpDao();
    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
    	PlateReviseDao ydDao    = new PlateReviseDao();
    	
    	//업데이트 할 크레인 스케줄 Data 항목 set
        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
        
        //스케줄Table의 컬럼을 저장하기위해 생성
        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        
        //레코드 셋의 레코드값을 받음
        JDTORecord getRecord = JDTORecordFactory.getInstance().create();
        JDTORecord recInTemp = null;
        JDTORecord crnRecord = null;
        
        int intRtnVal 					= 0 ;
        
        String szMsg					= "";
		String szMethodName				= "procY9CrnLdWr";  
		
		//크레인스케줄ID
		String szYD_CRN_SCH_ID 			= "";
		//야드스케줄코드
		String szYD_SCH_CD 				= null;
		//권상실적위치
		String szYD_UP_WR_LOC 			= null;
		//권상실적위치
		String szYD_UP_WR_LAYER			= null;
		//설비ID(크레인설비ID)
		String szYD_EQP_ID 				= null;
		//권하지시위치
		String szYD_DN_WO_LOC 			= null;
		
		String szRcvTcCode=ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode==null){
			szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return YdConstant.RETN_CD_TC_ERROR;
		}
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
        
        try{
			//크레인스케줄ID
	        szYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	        //야드스케줄코드
	        szYD_SCH_CD 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	        //권상실적위치
	        szYD_UP_WR_LOC 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
	        //권상실적단
	        szYD_UP_WR_LAYER= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LAYER");
	        //설비ID(크레인설비ID)
	        szYD_EQP_ID 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

	         //대상 데이터 SELECT
	        intRtnVal = ydDao.getLegacyYdInfo(msgRecord, getRecSet, 6);  
	        
	        if( intRtnVal <= 0 ) {
	        	szMsg = "스케쥴 Data가 존재하지 않습니다. ="+ intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new DAOException(szMsg);
	        }
	        
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            throw new DAOException(szMsg);
	        }
	        
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();
	        
	        //조회한 크레인 스케줄의 작업활성상태가 check를 한다.
	        if (getRecord.getFieldString("YD_WRK_PROG_STAT").equals("1")|| 
	        	getRecord.getFieldString("YD_WRK_PROG_STAT").equals("W")) {
	            
	            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
		        setCrnschRecord = JDTORecordFactory.getInstance().create();
		        setCrnschRecord.setField("YD_CRN_SCH_ID",      msgRecord.getFieldString("YD_CRN_SCH_ID"));
		        setCrnschRecord.setField("YD_WRK_PROG_STAT",   msgRecord.getFieldString("YD_WRK_PROG_STAT"));
		        setCrnschRecord.setField("YD_EQP_ID",     	   msgRecord.getFieldString("YD_EQP_ID"));		        
		        setCrnschRecord.setField("YD_UP_WR_LOC",       msgRecord.getFieldString("YD_UP_WR_LOC"));
		        setCrnschRecord.setField("YD_UP_WR_LAYER",     msgRecord.getFieldString("YD_UP_WR_LAYER"));
		        setCrnschRecord.setField("YD_UP_WRK_ACT_GP",   "A");
		        setCrnschRecord.setField("YD_UP_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss")); //권상완료일시
		        
		        //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
		        intRtnVal = ydCrnschDao.updYdCrnsch(setCrnschRecord, 0);		
		        
		        //설비Table의 상태 변경 (권상상태로 변경)
		        setCrnschRecord = JDTORecordFactory.getInstance().create();
		        setCrnschRecord.setField("YD_EQP_ID",     	   msgRecord.getFieldString("YD_EQP_ID"));
		        setCrnschRecord.setField("YD_EQP_STAT",        msgRecord.getFieldString("YD_WRK_PROG_STAT"));
		        
		        intRtnVal = ydEqpDao.updYdEqp(setCrnschRecord, 0);
		        
		        for(int i = 0; i < getRecSet.size() ; i++){
	    			
	    			getRecSet.absolute(i+1);
	            	getRecord = getRecSet.getRecord();
	            		
	    			//크레인에 UPDATE
	    			crnRecord = JDTORecordFactory.getInstance().create();
	    			crnRecord.setField("YD_STK_COL_GP",       ydDaoUtils.paraRecChkNull(getRecord,"YD_EQP_ID"));    
	    			crnRecord.setField("YD_STK_BED_NO",       "01");   
	    			crnRecord.setField("YD_STK_LYR_NO",       "00"+(i+1)) ;
	                crnRecord.setField("YD_STK_LYR_MTL_STAT", "C");
	                crnRecord.setField("STL_NO",              ydDaoUtils.paraRecChkNull(getRecord,"STL_NO"));
	                
	                intRtnVal = ydStklyrDao.updYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE
	                
	                //권상 지시위치 Clear
	                crnRecord = JDTORecordFactory.getInstance().create();
	                crnRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
	                crnRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
	                crnRecord.setField("YD_STK_LYR_NO",       ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i)) ;
	                crnRecord.setField("YD_STK_LYR_MTL_STAT", "E");
	                crnRecord.setField("STL_NO",              "");
	                
	                intRtnVal = ydStklyrDao.updYdStklyr(crnRecord, 0);  //적치단의 재료정보 Clear
	                
	                String strLoc = szYD_UP_WR_LOC.substring(0,6);
	                /*
	                 *  1후판압연전단 L2에 북아웃실적 전문 전송
	                 */
	                if("RT".equals(strLoc.substring(2,4))){
	                	
	                	this.procSmsSend(ydDaoUtils.paraRecChkNull(getRecord,"STL_NO"),strLoc,1);
	                }
	                /*
	                 *  1후판정정야드 L2에 북아웃실적 전문 전송
	                 */ 
	                if("PA0104".equals(strLoc)||"PB0401".equals(strLoc)){
	                	
	                	recInTemp = JDTORecordFactory.getInstance().create();
		                recInTemp.setField("MSG_ID"         , "YDY9L001");
		                recInTemp.setField("OPERATION_TYPE" , "2");
		                recInTemp.setField("STL_NO"         , ydDaoUtils.paraRecChkNull(getRecord,"STL_NO"));
		                recInTemp.setField("YD_STK_COL_GP"  , szYD_UP_WR_LOC.substring(0,6));
		                recInTemp.setField("YD_STK_BED_NO"  , szYD_UP_WR_LOC.substring(6,8));
		                recInTemp.setField("YD_STK_LYR_NO"  , ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i));
		 	        	
		 	        	ydDelegate.sendMsg(recInTemp);
	                }
	            } //end of for
	        	
		        /*
		         * 1후판정정야드L2 크레인작업실적응답 전송  - YDY9LX05 권상완료
		         */
		        szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
		        
		        recInTemp = JDTORecordFactory.getInstance().create();
		        
				if("PACRA1".equals(szYD_EQP_ID)) { 
			        recInTemp.setField("MSG_ID"          , "YDY9L405");
				} else if("PBCRB1".equals(szYD_EQP_ID)) {
			        recInTemp.setField("MSG_ID"          , "YDY9L105");
				} else if("PBCRB2".equals(szYD_EQP_ID)) {
			        recInTemp.setField("MSG_ID"          , "YDY9L205");
				} else if("PBCRB3".equals(szYD_EQP_ID)) {
			        recInTemp.setField("MSG_ID"          , "YDY9L305");
				}
		        recInTemp.setField("YD_EQP_ID"       , szYD_EQP_ID);			        	//야드설비ID
		        recInTemp.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_UP_CMPL);		//야드작업진행상태
		        recInTemp.setField("YD_SCH_CD"       , szYD_SCH_CD);				        //야드스케줄코드
		        recInTemp.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID);		            //야드크레인스케줄ID
		        recInTemp.setField("YD_L2_WR_GP"     , YdConstant.CRN_WRK_RE_LD_WR);		//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
		        recInTemp.setField("YD_L3_HD_RS_CD"  , YdConstant.CRN_WRK_RE_CD_NORMAL_HD);	//야드L3처리결과코드
				ydDelegate.sendMsg(recInTemp);
				szMsg = "[권상실적처리]1후판정정야드L2 크레인작업실적응답[YDY9LX05] 전송 완료" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    
	        }else{
	            szMsg = "YD_WRK_PROG_STAT data : '1' or 'W' not" ;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            throw new DAOException(szMsg);
	        }
	            
	        szMsg = "권상완료 실적처리 완료";
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        
	    }catch(Exception e) {
	    	szMsg="Error :  "+ e.getLocalizedMessage();
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        throw new DAOException(szMsg);
	    }//end of try~catch
	    
	    return YdConstant.RETN_CD_SUCCESS;
	} // end of procY9CrnLdWr
	
	/**
     * 오퍼레이션명 : 1후판정정야드 크레인권하실적
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
	 */
	public String procY9CrnUdWr(JDTORecord msgRecord) throws DAOException {
		
		EJBConnector ejbConn 		= null;
		String szMethodName         = "procY9CrnUdWr";
		String szRtnMsg             = "";		
		JDTORecord outRecord 		= JDTORecordFactory.getInstance().create();
		try {
			
			//1후판정정야드 권하실적처리
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRecord =(JDTORecord)ejbConn.trx("procY9CrnUdWrTX", new Class[] { JDTORecord.class}, new Object[] { msgRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), YdConstant.RETN_CD_FAILURE);
			
			if( sRTN_CD.equals(YdConstant.RETN_CD_FAILURE) ) {			
				szRtnMsg = "1후판정정야드 권하실적처리 오류 (" + szRtnMsg + ")";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, 1);
				return YdConstant.RETN_CD_FAILURE;
			}

			//크레인 작업지시 호출
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);			
			szRtnMsg = (String)ejbConn.trx("procY9CrnWrkOrdReq", new  Class[] { JDTORecord.class }, new Object[] { outRecord });
			/*
			String sMSG_ID	= StringHelper.evl(outRecord.getFieldString("MSG_ID"),"");
			if (sMSG_ID.equals("Y9YDL007")) {			
				//크레인작업지시 송신
				ydDelegate.sendMsg(outRecord);
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			}	
			*/
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szSessionName + e.getMessage(), e);
		}
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procY9CrnUdWr
	
	/**
     * 오퍼레이션명 : 1후판정정야드 크레인권하실적
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     * @ejb.transaction type="RequiresNew" 
	 */
	public JDTORecord procY9CrnUdWrTX(JDTORecord msgRecord) throws DAOException {
		
		YdDelegate      ydDelegate      = new YdDelegate();
    	YdEqpDao        ydEqpDao        = new YdEqpDao();
    	YdStockDao      ydStockDao      = new YdStockDao();
    	YdWrkbookDao 	ydWrkbookDao   	= new YdWrkbookDao(); 
    	YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
    	YdCrnWrkMtlDao  ydCrnWrkMtlDao  = new YdCrnWrkMtlDao(); 
    	YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();
    	YdCrnSchDao 	ydCrnschDao 	= new YdCrnSchDao();
    	PlateReviseDao 	ydDao    		= new PlateReviseDao();
    	
        int intRtnVal = 0;
        String szOperationName              = "1후판정정야드 권하실적처리";
        
        //DATA SETTING시 사용
        JDTORecord setRecord 				= JDTORecordFactory.getInstance().create();
        
        //레코드셋에서 레코드값을 읽어 올 때 사용
        JDTORecord getRecord                = JDTORecordFactory.getInstance().create(); ;
        
        //작업예약 업데이트 항목
        JDTORecord bookrecord               = JDTORecordFactory.getInstance().create();
        
        //크레인 스케줄 야드작업진행상태 비교		 (작업예약이 끝났는지 확인하기위해 카운터 값을 가져옴)
        JDTORecord outRecord                = JDTORecordFactory.getInstance().create();         
        
        JDTORecord recInTemp                = null;
        JDTORecord recOutTemp               = null;
        //타 메소드를 호출하여 레코드셋값을 받을때 사용..
        JDTORecordSet getRecSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
        
        JDTORecordSet rsResult              = null;
        
        String szMsg                        = "";
        String szMethodName                 = "procY9CrnUdWrTX";
        
        //WBOOK_ID 작업예약 완료 처리시 사용
        String szYdWbookId              	= "";
        String szCrnSchId                   = "";
        
        String szYD_SCH_CD                  = "";
        String szYD_EQP_ID                  = "";
        String szYD_DN_WR_LOC               = "";
        String szYD_DN_WR_LAYER				= "";
        String szYD_CAR_SCH_ID				= "";
        String szYD_WRK_PROG_STAT			= "";
        String szSTL_NO						= "";
        int intYD_EQP_WRK_SH				= 0;
        
        //-----------------------------------------------------------------------------------------
	    //실제Lyr를 검사하여 처리하기 위해 필요한 변수들
	    JDTORecordSet getLyrSet 			= JDTORecordFactory.getInstance().createRecordSet("temp");
	    JDTORecord recInPara = null;
	      
        String szREAL_TOP_LYR               = "";
	    int    intRealTopLyr                = 0;
	    int    intYdDnWrLayer               = 0;
	    //-----------------------------------------------------------------------------------------
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if( szRcvTcCode==null || szRcvTcCode.equals("") ){
            szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            throw new DAOException("<procY9CrnUdWr> " + szMsg);
        }
        
        if(bDebugFlag){
            szMsg="["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
        
        try{
	        szCrnSchId 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
	        szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	        szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	        szYD_DN_WR_LOC		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LOC");
	        szYD_DN_WR_LAYER	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_WR_LAYER");
	        szYD_WRK_PROG_STAT 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
	        
	        if( szCrnSchId.equals("") ) {
                szMsg = "["+szOperationName+"] 'YD_CRN_SCH_ID' Data Error	: 크레인스케줄ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new JDTOException("<procY9CrnUdWr> " + szMsg);
	        }
	        
	        //파라미터 레코드 편집
	        setRecord.setField("YD_CRN_SCH_ID",       	szCrnSchId);
	        setRecord.setField("YD_SCH_CD",           	szYD_SCH_CD);
	        setRecord.setField("YD_DN_WR_LOC",        	szYD_DN_WR_LOC);
	        setRecord.setField("YD_DN_WR_LAYER",      	szYD_DN_WR_LAYER);
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트	- 권하실적위치와 권하실적단을 업데이트
	        //-------------------------------------------------------------------------------------------------------------------
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        
            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
	        intRtnVal = ydCrnschDao.updYdCrnsch(setRecord, 0);		
	        
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치["+szYD_DN_WR_LOC+"]와 권하실적단["+szYD_DN_WR_LAYER+"]을 업데이트 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------------------------
            
            //-------------------------------------------------------------------------------------------------------------------
	        //	크레인스케줄 작업재료 조회
            //-------------------------------------------------------------------------------------------------------------------
            szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        intRtnVal = ydDao.getLegacyYdInfo(setRecord, getRecSet, 6);
	        
	        if(intRtnVal < 0){
	        	throw new JDTOException("<procY9CrnUdWr> Y9GetYdCrnsch ERROR CODE =" + intRtnVal);
            }
	        
	        /*
	         * 크레인 작업매수 셋팅. 이후단에서 사용.
	         */
	        intYD_EQP_WRK_SH = getRecSet.size();
	        
	        getRecSet.first();
	        getRecord = getRecSet.getRecord();
	        
	        szMsg="["+szOperationName+"] 크레인스케줄["+szCrnSchId+"]작업재료 조회 완료 - 대상재 건수 : " + getRecSet.size();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        //작업예약 테이블 데이터를 가져오기 위해 예약ID 저장
	        szYdWbookId 		= ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
	        //-------------------------------------------------------------------------------------------------------------------
            
	        //-------------------------------------------------------------------------------------------------------------------
	        // 작업진행상태 확인 후 상태가 맞지 않으면 롤백 처리
            //-------------------------------------------------------------------------------------------------------------------
	        if ((!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("2"))&& 
	        	(!ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT").equals("3"))) {
	        	szMsg = "["+szOperationName+"] 작업진행상태["+ydDaoUtils.paraRecChkNull(getRecord,"YD_WRK_PROG_STAT")+"]가   권상('2') 또는 권하대기('3')이 아닙니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            throw new JDTOException("<procY9CrnUdWr> " + szMsg);
	        }
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        // 권하지시위치와 권하실적위치가 다르면 권하지시위치의 맵정보를 Clear시킴
	        //-------------------------------------------------------------------------------------------------------------------
	        if (!getRecord.getFieldString("YD_DN_WR_LOC").equals(getRecord.getFieldString("YD_DN_WO_LOC"))) {
	        	szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	        	
	    		for(int i = 0; i < intYD_EQP_WRK_SH ; i++){
	    				
	    			String szYD_DN_WO_LOC   =  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC") ;
	                String szYD_DN_WO_LAYER =  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LAYER") ;
	                
	                setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
	                setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   
	                setRecord.setField("YD_STK_LYR_NO",       ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i)) ;
	                setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
	                setRecord.setField("STL_NO",              "");
	                
	                intRtnVal = ydStkLyrDao.updYdStklyr(setRecord, 0);  //적치단의 재료정보 Clear
	                
	                getRecSet.next();
	                getRecord = getRecSet.getRecord();
	            } //end of for
	    		
	        	szMsg = "[" + szOperationName + "] 권하실적위치와 권하지시위치가 다른 경우 맵정보 Clear 완료";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        }
	        //-------------------------------------------------------------------------------------------------------------------
	        
	        //-------------------------------------------------------------------------------------------------------------------
	        //권하실적LYR와 실제적치단의 최상위 위치가 다르면 권하실적LYR를 변경하고 적치단 정보를 수정한다.
	        //-------------------------------------------------------------------------------------------------------------------
	        intYdDnWrLayer 	= Integer.parseInt(szYD_DN_WR_LAYER);
	        	                
	        recInPara	= JDTORecordFactory.getInstance().create();
	        recInPara.setField("YD_STK_COL_GP", szYD_DN_WR_LOC.substring(0, 6));
	        recInPara.setField("YD_STK_BED_NO", szYD_DN_WR_LOC.substring(6, 8));
	        
	        intRtnVal = ydStkLyrDao.getYdStklyr(recInPara, getLyrSet, 98);
	        
	        if(intRtnVal < 0){
	        	throw new JDTOException("<procY9CrnUdWr> getYdStklyr ERROR CODE =" + intRtnVal);
            }
	        
	        getLyrSet.first();
	        recOutTemp = getLyrSet.getRecord();
	        
	        szREAL_TOP_LYR 	= ydDaoUtils.paraRecChkNull(recOutTemp,"REAL_TOP_LYR");
	        intRealTopLyr 	= Integer.parseInt(szREAL_TOP_LYR);
	        
	        if (intYdDnWrLayer != intRealTopLyr) {
	        	
	        	szMsg = "[" + szOperationName + "] 실적적치단[" + intYdDnWrLayer + "]과 실재야드적치단[" + intRealTopLyr + "]이 상이하여 실적적치단 변경 시작";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	
	            szYD_DN_WR_LAYER = szREAL_TOP_LYR;
    	    }
	        //-------------------------------------------------------------------------------------------------------------------	
	        
	        //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정
		    //-------------------------------------------------------------------------------------------------------------------	
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 변경을 하고 공통테이블에 현저장위치를 수정 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            getRecSet.first();
    		getRecord = getRecSet.getRecord();
    		
    		JDTORecord crnRecord = null;
    		
        	for(int i=0; i<getRecSet.size(); i++) {
        		
        		getRecSet.absolute(i+1);
        		getRecord 	= JDTORecordFactory.getInstance().create();
        		getRecord.setRecord(getRecSet.getRecord());
    	        
    	        szSTL_NO  = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO") ;
        		
        		//크레인에 UPDATE
    			crnRecord = JDTORecordFactory.getInstance().create();
    			crnRecord.setField("YD_STK_COL_GP",       szYD_EQP_ID);    
    			crnRecord.setField("YD_STK_BED_NO",       "01");   
    			crnRecord.setField("YD_STK_LYR_NO",       "00"+(i+1)) ;
                crnRecord.setField("YD_STK_LYR_MTL_STAT", "E");
                crnRecord.setField("STL_NO",              "");
                
                intRtnVal = ydStkLyrDao.updYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE
                
                /*
                 * 적치단에 존재하는 해당 저장품정보를 모두 CLEAR한다.
                 * 2013.08.14 윤재광 
                 * - 해당야드에 있는 대상에 대해 검색할 수 있도록 수정요
                 */
                crnRecord = JDTORecordFactory.getInstance().create();
                crnRecord.setField("STL_NO", szSTL_NO);               
            	//intRtnVal = (new YdStkLyrDao()).updYdStklyrWithStock(crnRecord);
            	
                String strLoc = szYD_DN_WR_LOC.substring(0,6);
                
                if("RT".equals(strLoc.substring(2,4))){
                	// R/T 북인시 R/T 저장위치에 강제삭제처리해야 함
                	
                	//적치단dao를 호출해서 업데이트를 한다.
	            	crnRecord = JDTORecordFactory.getInstance().create();
	            	crnRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6));   
	            	crnRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8));               
	            	crnRecord.setField("YD_STK_LYR_NO", 		ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i)) ;
	            	crnRecord.setField("STL_NO",              	"");                            
	            	crnRecord.setField("YD_STK_LYR_MTL_STAT", 	"E");           
	
	            	intRtnVal = ydStkLyrDao.updYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE
	            	
                }else{
	                //적치단dao를 호출해서 업데이트를 한다.
	            	crnRecord = JDTORecordFactory.getInstance().create();
	            	crnRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6));   
	            	crnRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8));               
	            	crnRecord.setField("YD_STK_LYR_NO", 		ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i)) ;
	            	crnRecord.setField("STL_NO",              	szSTL_NO);                            
	            	crnRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");           
	
	            	intRtnVal = ydStkLyrDao.updYdStklyr(crnRecord, 0);  //크레인 적치단의 재료정보 UPDATE
                }
            	/* 
                 *  1후판압연전단 L2에 북인실적 전문 전송
                 */
                if("RT".equals(strLoc.substring(2,4))){
                	
                	this.procSmsSend(ydDaoUtils.paraRecChkNull(getRecord,"STL_NO"),strLoc,0);
                }
            	
            	/*
                 *  1후판정정야드 L2에 북인실적 전문 전송
                 */
                if("PA0104".equals(strLoc)||"PB0401".equals(strLoc)){
                	
                	recInTemp = JDTORecordFactory.getInstance().create();
	                recInTemp.setField("MSG_ID"         , "YDY9L001");
	                recInTemp.setField("OPERATION_TYPE" , "1");
	                recInTemp.setField("STL_NO"         , szSTL_NO);
	                recInTemp.setField("YD_STK_COL_GP"  , szYD_DN_WR_LOC.substring(0,6));
	                recInTemp.setField("YD_STK_BED_NO"  , szYD_DN_WR_LOC.substring(6,8));
	                recInTemp.setField("YD_STK_LYR_NO"  , ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i));
	 	        	
	 	        	ydDelegate.sendMsg(recInTemp);
                }
    	    }
	        
	        szMsg = "[" + szOperationName + "] 크레인작업재료의 적치단을 적치중으로 수정 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
            //-------------------------------------------------------------------------------------------------------------------
	        //크레인스케줄 업데이트 및 삭제처리 - 권하실적위치와 권하완료일, 작업진행상태 업데이트
            //-------------------------------------------------------------------------------------------------------------------
            szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"] 업데이트 - 권하실적위치의 좌표와 권하완료일, 작업진행상태 업데이트 수정 시작";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      	szCrnSchId);
	        setRecord.setField("YD_DN_WR_LOC",       	szYD_DN_WR_LOC) ;
	        setRecord.setField("YD_DN_WR_LAYER",     	szYD_DN_WR_LAYER) ;
	        setRecord.setField("YD_DN_WRK_ACT_GP",   	"A");
	        setRecord.setField("YD_WRK_PROG_STAT",   	szYD_WRK_PROG_STAT);
	        setRecord.setField("YD_DN_CMPL_DT",      	YdUtils.getCurDate("yyyyMMddHHmmss"));
	        setRecord.setField("YD_GP",   				"P");
	        setRecord.setField("YD_WRK_HDS_DD",   		YdUtils.getDefaultHdsDate());
	        setRecord.setField("DEL_YN",             	"Y");
	        setRecord.setField("MODIFIER",           	szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
	        
	        intRtnVal = ydCrnschDao.updYdCrnsch(setRecord, 0);		     
	        
	        szMsg = "[" + szOperationName + "] 크레인스케줄["+szCrnSchId+"]에 권하실적정보 수정 완료 - 반환값 : " + intRtnVal;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        
            if(getRecord.getFieldString("YD_WBOOK_ID") == null ||
     	       getRecord.getFieldString("YD_WBOOK_ID").equals("")) {
                szMsg = "YD_WBOOK_ID  Data Error	: 작업예약 ID가 없습니다.";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                throw new JDTOException("<procY9CrnUdWr> " + szMsg);
	        }
	        
            //-------------------------------------------------------------------------------------------------------------------
		    //	크레인작업 및 재료, 작업예약 및 재료정보를 Clear
	        //-------------------------------------------------------------------------------------------------------------------
	        //크레인 작업재료 삭제처리
	        setRecord = JDTORecordFactory.getInstance().create();
	        setRecord.setField("YD_CRN_SCH_ID",      szCrnSchId);
	        setRecord.setField("DEL_YN",             "Y");
	        setRecord.setField("MODIFIER",           szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);

	        intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl_YD_CRN_SCH_ID(setRecord);
	        
	        if(intRtnVal <= 0) {
                szMsg = "[" + szOperationName + "] 크레인스케줄작업재료 삭제중 Error!! Code : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	        	throw new JDTOException("<procY9CrnUdWr> updYdCrnwrkmtl_YD_CRN_SCH_ID " + szMsg);
	        }
	        
	        //작업예약완료 CHECK
	        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");

	        intRtnVal = ydCrnschDao.getYdCrnsch(getRecord, getRecSet, 4);  
			
			if(intRtnVal < 0){
            	throw new JDTOException("<procY9CrnUdWr> procY9CrnUdWr ERROR CODE =" + intRtnVal);
            }
	        
	        /*
	         * 작업예약에 존재하는 크레인스케쥴 정보가 없을경우에 작업예약정보를 삭제한다.
	         */	
	        getRecSet.first();
	        //레코드셋의 사이즈값으로 ErrorCheck
	        if(getRecSet.size() == 0){
	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            throw new JDTOException("<procY9CrnUdWr> Y9GetYdCrnsch" + szMsg);
	        }

	        outRecord = getRecSet.getRecord();
	        
	        int schcnt = outRecord.getFieldInt("SCH_CNT");
	        int endcnt = outRecord.getFieldInt("END_CNT");
	                    
	        if (schcnt == endcnt) {
	        	
	            bookrecord.setField("YD_WBOOK_ID",        szYdWbookId) ;
	            bookrecord.setField("DEL_YN",             "Y") ;
	            bookrecord.setField("YD_SCH_PROG_STAT",   "E") ;
	            
	            intRtnVal = ydWrkbookDao.updYdWrkbook(bookrecord, 0);
	            
	            if(intRtnVal < 0){
	            	throw new JDTOException("<procY9CrnUdWr> Y9UpdYdWrkbook ERROR CODE =" + intRtnVal);
	            }
		        
				szMsg = "[" + szOperationName + "] 작업예약재료["+szSTL_NO+"]삭제 처리 시작";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("DEL_YN",      "Y");
				recInTemp.setField("MODIFIER",    szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
				recInTemp.setField("YD_WBOOK_ID", szYdWbookId);
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl1(recInTemp);
				if(intRtnVal < 0){ 
	            	throw new JDTOException("<procY9CrnUdWr> updYdWrkbookmtl1 ERROR CODE =" + intRtnVal);
	            }
	            szMsg = "[" + szOperationName + "] 작업예약재료["+szSTL_NO+"]삭제 처리 완료";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            
	        }
	        
	        //-------------------------------------------------------------------------------------------------------------------
		    //	크레인 설비상태 작업대기(W) 셋팅.
	        //-------------------------------------------------------------------------------------------------------------------
            recInTemp = JDTORecordFactory.getInstance().create();
	        recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	        recInTemp.setField("YD_EQP_STAT", "W");
            intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
            if(intRtnVal < 0){
            	throw new JDTOException("<procY9CrnUdWr> updYdEqp ERROR CODE =" + intRtnVal);
            }
			
			/*
			 * 이력테이블등록호출 
			 */
			{
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID",             "");
				recInTemp.setField("YD_WBOOK_ID",        szYdWbookId);
				recInTemp.setField("YD_CRN_SCH_ID",      szCrnSchId);
				
				this.procWorkHistoryCreate(recInTemp);
				
				szMsg="[" + szOperationName + "] 이력테이블등록호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
	        /*
	         * 1후판정정야드L2 크레인작업실적응답 전송  - YDY9LX05 권하완료
	         */
	        recInTemp = JDTORecordFactory.getInstance().create();
	        
			if("PACRA1".equals(szYD_EQP_ID)) { 
		        recInTemp.setField("MSG_ID"          , "YDY9L405");
			} else if("PBCRB1".equals(szYD_EQP_ID)) {
		        recInTemp.setField("MSG_ID"          , "YDY9L105");
			} else if("PBCRB2".equals(szYD_EQP_ID)) {
		        recInTemp.setField("MSG_ID"          , "YDY9L205");
			} else if("PBCRB3".equals(szYD_EQP_ID)) {
		        recInTemp.setField("MSG_ID"          , "YDY9L305");
			}
			recInTemp.setField("YD_EQP_ID"     		, szYD_EQP_ID);										//야드설비ID
	        recInTemp.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_DN_CMPL);					//야드작업진행상태
	        recInTemp.setField("YD_SCH_CD"   		, szYD_SCH_CD);										//야드스케줄코드
	        recInTemp.setField("YD_CRN_SCH_ID"   	, szCrnSchId);										//야드크레인스케줄ID
	        //==================================================================================
	        // 수신전문의 진행상태가 강제권하(5)일시 응답으로는 강제권하값(F)로 내려보내야 됨
	        //==================================================================================	        
	        if(szYD_WRK_PROG_STAT.equals("4")){
		        recInTemp.setField("YD_L2_WR_GP"	, YdConstant.CRN_WRK_RE_DN_WR);						//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
	        } else {
	        	recInTemp.setField("YD_L2_WR_GP"	, YdConstant.CRN_WRK_RE_FRCE_DN);					//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하	        	
	        }
	        
	        recInTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);					//야드L3처리결과코드
			ydDelegate.sendMsg(recInTemp);
			szMsg = "[권하실적처리]1후판정정야드L2 크레인작업실적응답[YDY9LX05] 전송 완료";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	                
			/*
			 * 크레인 작업지시 요구호출.
			 */
			{
				recInTemp = JDTORecordFactory.getInstance().create();

				recInTemp.setField("MSG_ID",           "Y9YDL007");
				recInTemp.setField("YD_EQP_ID",        szYD_EQP_ID);
				recInTemp.setField("YD_EQP_WRK_MODE",  "1");
				recInTemp.setField("YD_WRK_PROG_STAT", "W");
				recInTemp.setField("YD_SCH_CD",        szYD_SCH_CD);
				recInTemp.setField("YD_CRN_SCH_ID",    "");
				recInTemp.setField("RTN_CD",     YdConstant.RETN_CD_SUCCESS);
				// EJB CALL로 변경 검토(트랜잭션 문제 때문에)
				// ydDelegate.sendMsg(recInTemp);
				// szMsg = "[권하실적처리]1후판정정야드L2 크레인작업지시요구 전송 완료";
	            // ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
        }catch(Exception e) {
        	/*
        	 * Exception 발생시에도 작업실적 응답은 송신.
        	 */
        	try{
		        recInTemp = JDTORecordFactory.getInstance().create(); 
		        
		        if("PACRA1".equals(szYD_EQP_ID)) { 
			        recInTemp.setField("MSG_ID"          , "YDY9L405");
				} else if("PBCRB1".equals(szYD_EQP_ID)) {
			        recInTemp.setField("MSG_ID"          , "YDY9L105");
				} else if("PBCRB2".equals(szYD_EQP_ID)) {
			        recInTemp.setField("MSG_ID"          , "YDY9L205");
				} else if("PBCRB3".equals(szYD_EQP_ID)) {
			        recInTemp.setField("MSG_ID"          , "YDY9L305");
				}
		        recInTemp.setField("YD_EQP_ID"       , szYD_EQP_ID);						// 야드설비ID
		        recInTemp.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_DN_CMPL);		// 야드작업진행상태
		        recInTemp.setField("YD_SCH_CD"       , szYD_SCH_CD);						// 야드스케줄코드
		        recInTemp.setField("YD_CRN_SCH_ID"   , szCrnSchId);							// 야드크레인스케줄ID
		        if(szYD_WRK_PROG_STAT.equals("4")){
		        	recInTemp.setField("YD_L2_WR_GP", YdConstant.CRN_WRK_RE_DN_WR);			// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
		        } else {
		        	recInTemp.setField("YD_L2_WR_GP", YdConstant.CRN_WRK_RE_FRCE_DN);		// 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하	        	
		        }	        
	
		        recInTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NORMAL_HD);	// 야드L3처리결과코드
				ydDelegate.sendMsg(recInTemp);
				szMsg = "[권하실적처리]1후판정정야드L2 크레인작업실적응답[YDY9LX05] 전송 완료";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	}catch(Exception e1){}
        	szMsg = "[권하실적처리]1후판정정야드 권하실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
        }finally{
        }
		return recInTemp;
	} // end of procY9CrnUdWr
	
	/**
     * 오퍼레이션명 : 1후판정정야드 크레인작업지시요구 (Y9YDL007)
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws DAOException
     */
    public String procY9CrnWrkOrdReq(JDTORecord msgRecord)throws DAOException {

    	YdDelegate   ydDelegate   = new YdDelegate();
    	YdCrnSchDao  YdCrnSchDao  = new YdCrnSchDao();
    	YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
    	YdEqpDao     ydEqpDao     = new YdEqpDao();

    	JDTORecord recCrnSch 	= JDTORecordFactory.getInstance().create();
    	JDTORecord recInPara 	= null;
    	JDTORecord recOutTemp 	= null;
    	JDTORecord recIntTemp 	= null;
    	JDTORecord recPara     	= null;
		
        JDTORecordSet rsCrnSch 	= JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecordSet rsWrkBook = null;
        
        int intRtnVal 					= 0 ;
        
        String szMsg              		= "";
        String szMethodName       		= "procY9CrnWrkOrdReq";
        String szOperationName			= "1후판정정야드 크레인작업지시요구 ";
        
        String szEqpId                  = "";
        String szWrkProgStat            = "";
        	
        //스케쥴코드
        String szYD_SCH_CD				= "";
        
        boolean bRtnCheck               = true;
        boolean blnRtnVal				= true;
        
        String szRtnMsg					= null;
        /*
         * 	YD_EQP_ID			야드설비ID		CHAR	6		크레인설비 ID	
			YD_EQP_WRK_MODE		야드설비작업Mode	CHAR	1		"1": On-Line, "2": Off-Line 	
			YD_WRK_PROG_STAT	야드작업진행상태	CHAR	1		"""W"" 작업지시대기(작업없을때 요구),""1"" 권상작업(권상전),""3"" 권하작업(권하전) "	
			YD_SCH_CD			야드스케쥴코드		CHAR	8		현재 진행중인 작업이 있을경우 해당작업	
			YD_CRN_SCH_ID		야드크레인스케쥴ID	CHAR	18		작업지시를 재요구 하는 경우 사용	
         */
        try{
        	szEqpId     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");

        	//------------------------------------------------------------------------------------------
			//	설비상태 확인
			//------------------------------------------------------------------------------------------
			szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	
			JDTORecordSet rsCrnInfo = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = DaoManager.getYdEqp(msgRecord, rsCrnInfo, 0);
			
			//레코드 추출
			rsCrnInfo.first();
			recPara = rsCrnInfo.getRecord();
			
			//설비상태
			String szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			//야드설비작업Mode
			String szYD_EQP_WRK_MODE = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");
			
			//크레인의 상태가 'B'이면 false 리턴.
			if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {
				
				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CD_FAILURE;
			}
			//------------------------------------------------------------------------------------------
			
			//------------------------------------------------------------------------------------------
        	//	파라미터로 넘겨진 야드 작업 진행상태별 로직 분기   
			//------------------------------------------------------------------------------------------
			szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
        	
			if("".equals(szWrkProgStat)){
				szWrkProgStat = szYD_EQP_STAT;
				szMsg = "설비ID(" + szEqpId + ")의 진행상태 PARAM NO(" + szYD_EQP_STAT + ") 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
        	//------------------------------------------------------------------------------------------
        	//야드 작업 진행상태가 1,2인 경우 (작업지시를 재요구 하는 경우 사용한다.)
        	//------------------------------------------------------------------------------------------
        	if (szWrkProgStat.equals("1") || szWrkProgStat.equals("2")){
        		
				szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 1,2인 경우";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 2인 경우를 조회...
				rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	intRtnVal = YdCrnSchDao.getYdCrnsch(msgRecord, rsCrnSch, 16);
		    		
    			//현재크레인이 작업중인 스케줄의 값을 다시 재전송한다.
    			rsCrnSch.absolute(1);
    			recOutTemp = JDTORecordFactory.getInstance().create();
    			recOutTemp.setRecord(rsCrnSch.getRecord());
    			
            	recInPara = JDTORecordFactory.getInstance().create();
        		//작업지시 전문 전송 data setup
            	if("PACRA1".equals(szEqpId)) { 
            		recInPara.setField("MSG_ID"          , "YDY9L404");
				} else if("PBCRB1".equals(szEqpId)) {
					recInPara.setField("MSG_ID"          , "YDY9L104");
				} else if("PBCRB2".equals(szEqpId)) {
					recInPara.setField("MSG_ID"          , "YDY9L204");
				} else if("PBCRB3".equals(szEqpId)) {
					recInPara.setField("MSG_ID"          , "YDY9L304");
				}
            	recInPara.setField("YD_CRN_SCH_ID",    	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID"));
            	recInPara.setField("YD_WRK_PROG_STAT", 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT"));
            	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
            	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
            	recInPara.setField("MODIFIER", 			"YDSYSTEM");
            	recInPara.setField("MSG_GP", 			"U");
            	ydDelegate.sendMsg(recInPara);

            	return YdConstant.RETN_CD_SUCCESS;
    			
        	//------------------------------------------------------------------------------------------
        	// 야드 작업 진행 상태가 'W'인경우 (작업이 없을때 요구)	
        	//------------------------------------------------------------------------------------------
        	}else if(szWrkProgStat.equals("W")){
        		
				szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 'W'인경우";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
	        	
	        	intRtnVal = YdCrnSchDao.getYdCrnsch(msgRecord, rsCrnSch, 15);
		    	
        		szMsg="["+szOperationName+"] 야드작업진행상태[" + szWrkProgStat + "]가 'W'인경우 Y9ChkWrkProgStatW 호출 완료 - 반환값 : " + intRtnVal + ", 대상재 개수 : " + rsCrnSch.size();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		
				//등록된 스케쥴이 있는 경우
				if(intRtnVal > 0) {
    				
					//신규크레인이 작업스케줄의 값을 전송한다.
	    			rsCrnSch.absolute(1);
	    			recOutTemp = JDTORecordFactory.getInstance().create();
	    			recOutTemp.setRecord(rsCrnSch.getRecord());
	    			
	            	recInPara = JDTORecordFactory.getInstance().create();
	        		//작업지시 전문 전송 data setup
	            	if("PACRA1".equals(szEqpId)) { 
	            		recInPara.setField("MSG_ID"          , "YDY9L404");
					} else if("PBCRB1".equals(szEqpId)) {
						recInPara.setField("MSG_ID"          , "YDY9L104");
					} else if("PBCRB2".equals(szEqpId)) {
						recInPara.setField("MSG_ID"          , "YDY9L204");
					} else if("PBCRB3".equals(szEqpId)) {
						recInPara.setField("MSG_ID"          , "YDY9L304");
					}
	            	recInPara.setField("YD_CRN_SCH_ID",    	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID"));
	            	recInPara.setField("YD_WRK_PROG_STAT", 	"1");
	            	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
	            	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
	            	recInPara.setField("MODIFIER", 			"YDSYSTEM");
	            	recInPara.setField("MSG_GP", 			"I");
	            	ydDelegate.sendMsg(recInPara);
					
	            	//크레인스케줄의 작업진행 상태를 권상지시로 변경
	    			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkProgStat*/
	        		intRtnVal = YdCrnSchDao.updYdCrnschDelay(recInPara, 302);
	        		//크레인스케줄의 설비상태를 권상지시로 변경
	        		recIntTemp = JDTORecordFactory.getInstance().create();
	        		recIntTemp.setField("YD_EQP_ID", 		szEqpId);
	        		recIntTemp.setField("YD_EQP_STAT", 		"1");
	        		recIntTemp.setField("YD_WORD_DT",      	YdUtils.getCurDate("yyyyMMddHHmmss"));
	        		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
	        	
	        	//등록된 스케쥴이 없는 경우	
				}else{
					// 2013.08.19 윤재광 
					// 스케쥴모듈 신규개발이후 추후 수정요.
					//작업예약조회 
	    			recInPara = JDTORecordFactory.getInstance().create();
	    			recInPara.setField("YD_EQP_ID", szEqpId);
	    			rsWrkBook = JDTORecordFactory.getInstance().createRecordSet("");
	    			intRtnVal = ydWrkbookDao.getYdWrkbook(recInPara, rsWrkBook, 4);
	    			if(intRtnVal > 0) {
	    				//검색된 작업예약을 크레인스케줄을 생성한다. 생성하면 설비상태값에 따라서 작업지시를 내려보내도록되어있다.
	    				szMsg="["+szOperationName+"] 현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약을 조회하였습니다.  크레인스케줄Main호출";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    				//크레인 스케줄 호출 (설비id,스케줄코드);
	    				rsWrkBook.absolute(1);
	    				recOutTemp = JDTORecordFactory.getInstance().create();
	    				recOutTemp.setRecord(rsWrkBook.getRecord());

	    				recInPara = JDTORecordFactory.getInstance().create();
	    				
	    				//recInPara.setField("MSG_ID",    "YDYDJ710");
	    				recInPara.setField("JMS_TC_CD", "YDYDJ710");
	    				recInPara.setField("YD_EQP_ID", szEqpId);
	    				recInPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
	    				recInPara.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID")); //CHITO 2011.03.30 추가 
	    				 
	    	        	//크레인 스케줄 호출 메세지 전송
	    	    		ydDelegate.sendMsg(recInPara);
	    				//this.procY9CrnSchMain(recPara);
	    	    		//다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
	    	    		return YdConstant.RETN_CD_SUCCESS;	    			    				
	    			}
				}
        		return YdConstant.RETN_CD_SUCCESS;
			}
        	
		}catch(Exception e){
			szMsg="1후판정정야드 작업지시 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}

		return YdConstant.RETN_CD_SUCCESS;

	} //end of procY9CrnWrkOrdReq()
    
    /**
	 * 오퍼레이션명 : 1후판정정야드크레인스케줄Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public void procY9CrnSchMain(JDTORecord msgRecord)throws DAOException  {
		
		try{
			
			EJBConnector ydEjbCon = new EJBConnector("default", this);
			ydEjbCon.trx("PlateReviseSeEJB", "procY9CrnSchSub", msgRecord);	
			
			this.chkY9CrnWrkOrdReqSub(msgRecord);
			
		} catch (Exception e) {
			throw new DAOException("[procY9CrnSchMain] Exception발생 : " + e.getMessage());
		}	// end try catch문

	} // end of procY9CrnSchMain()
	
	/**
     * 오퍼레이션명 : 크레인작업지시요구 판단
     * @param  ● msgRecord, intGp
     * @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
     * @throws ● DAOException
     */
    public int chkY9CrnWrkOrdReqSub(JDTORecord msgRecord) throws DAOException {
    	YdEqpDao    ydEqpDao    = new YdEqpDao();
    	
    	JDTORecordSet rsResult = null;
    	JDTORecord recInTemp   = null;
    	JDTORecord recOutTemp  = null;
    	
    	int intRtnVal = 0 ;
    	
    	String szMsg        	= "";
    	String szMethodName 	= "chkY9CrnWrkOrdReqSub";
    	String szOperationName  = "크레인작업지시요구 판단";
    	
    	String szYD_EQP_STAT 	= "";
    	
		try{
			//설비Table조회
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal 	= ydEqpDao.getYdEqp(msgRecord, rsResult, 0);
			
			recOutTemp 	= JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord(0));
			
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
			
			if(szYD_EQP_STAT.equals("W")){
				
				recInTemp = JDTORecordFactory.getInstance().create();
			
				
				recInTemp.setField("JMS_TC_CD",        "YDYDJ720");
				recInTemp.setField("MSG_ID",           "Y9YDL007");
				recInTemp.setField("YD_EQP_ID",        ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID"));
				recInTemp.setField("YD_EQP_WRK_MODE",  ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE"));
				recInTemp.setField("YD_WRK_PROG_STAT", "W");
				recInTemp.setField("YD_SCH_CD",        "");
				recInTemp.setField("YD_CRN_SCH_ID",    "");
				
				//크레인작업지시 송신
				ydDelegate.sendMsg(recInTemp);
				//this.procY9CrnWrkOrdReq(recInTemp);
				
			}
		}catch(Exception e){
			szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
        }//end of try~catch
		
		return intRtnVal = 1;
		
    }// end of chkY9CrnWrkOrdReqSub
	
    /**
	 * 오퍼레이션명 : 1후판정정야드크레인스케줄SUB
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public void procY9CrnSchSub(JDTORecord msgRecord)throws DAOException  {
		
		YdCrnSchDao ydCrnschDao 		= new YdCrnSchDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		
		JDTORecordSet outRecset    	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsOut    		= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsWrkbookmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord recInTemp		= null;
		JDTORecord recLogMsg 		= null;
		JDTORecord recInPara 		= null;
		JDTORecord recSeq 			= null;
		JDTORecord recIn			= null;
		//
		String szMsg        		= "";
		String szMethodName 		= "procY9CrnSchSub";
		String szOperationName 		= "1후판정정야드크레인스케줄SUB";
		//설비Id
		String szEqpId      		= "";
		//스케줄코드
		String szSchCd      		= "";
		String szYdWbookId 			= "";
		
		String szYD_WRKABLE_CRN		= null;
		String szYD_SCH_PRIOR		= null;

		int intRtnVal       		= 0;
		
		//Vector 선언
		Vector vecResult      		= new Vector();
		Vector vecReResult     		= new Vector();
		
		try{
			//크레인설비ID
			szEqpId 		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID");
			//크레인스케줄코드
			szSchCd 		= ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD");
			//크레인작업예약
			szYdWbookId 	= ydDaoUtils.paraRecChkNull(msgRecord,"YD_WBOOK_ID");
 
			szMsg="[" + szOperationName + "] 파라미터 확인 : YD_EQP_ID : " + szEqpId + " , YD_SCH_CD : " + szSchCd + " ,작업예약ID["+szYdWbookId+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-----------------------------------------------------------------------------------------------
			//	작업예약의 재료들이 존재하는 BED정보 셋팅
			//-----------------------------------------------------------------------------------------------
			this.Y9ChkCrnSchEffectCondition(msgRecord);
			//-----------------------------------------------------------------------------------------------
			//	작업예약의 재료들이 존재하는 BED정보 조회
			//-----------------------------------------------------------------------------------------------
			rsWrkbookmtl 		= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp		 	= JDTORecordFactory.getInstance().create(); 
			recInTemp.setField("YD_WBOOK_ID", szYdWbookId);
			szMsg = DaoManager.getYdWrkbookmtl(recInTemp, rsWrkbookmtl, 5);
			
			szMsg="[" + szOperationName + "] 작업예약ID["+szYdWbookId+"]의 작업예약재료가 존재하는 BED정보 조회 완료 - 메세지 : " + szMsg + ", 건수 : " + rsWrkbookmtl.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-----------------------------------------------------------------------------------------------
			//	작업코드에 해당하는 작업크레인 정보 조회
			//-----------------------------------------------------------------------------------------------
			recInTemp 			= JDTORecordFactory.getInstance().create();
			String szRtnMsg		= YdCommonUtils.getWrkableCrnBySchRule(szSchCd, recInTemp);
			
			szYD_WRKABLE_CRN	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_WRKABLE_CRN");
			szYD_SCH_PRIOR      = ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_PRIOR"); 
			
			if(!"".equals(szEqpId)){
				szYD_WRKABLE_CRN = szEqpId; //2010.10.16 윤재광 대체 szYD_WRK_CRN;
			}
			szMsg="[" + szOperationName + "] 크레인스케줄을 수행할 작업예약["+szYdWbookId+"]의 스케줄코드["+szSchCd+"]의 스케줄기준 조회 완료 - 스케줄기준의 작업가능한 크레인설비ID["+szYD_WRKABLE_CRN+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-----------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 수행조건 판단 모듈 호출 - 스케줄금지유무와 주/대체크레인 교체 유무 판단
			//------------------------------------------------------------------------------------------------------------
			
			// 향후에 봐서 추가예정
			
			//------------------------------------------------------------------------------------------------------------
			//	그룹핑 파라미터 셋팅
			//------------------------------------------------------------------------------------------------------------
			szMsg="[" + szOperationName + "] 그룹핑 파라미터 셋팅  rsWrkbookmtl.SIZE() : " + rsWrkbookmtl.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			intRtnVal = this.Y9CrnSchSort(rsWrkbookmtl, outRecset);

			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 편성 : 주작업 보조작업구분
			//------------------------------------------------------------------------------------------------------------
			szMsg="[" + szOperationName + "] Handling Lot 편성 outRecset.SIZE() : " + outRecset.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			intRtnVal = this.Y9CrnSchDataHandling(outRecset, vecResult);
			
			//------------------------------------------------------------------------------------------------------------
			//	크레인사양 비교Check
			//------------------------------------------------------------------------------------------------------------
			szMsg="[" + szOperationName + "] 크레인사양 비교Check vecResult.SIZE() : " + vecResult.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			intRtnVal = this.Y9ChkHandledDataCrnSpec(szYD_WRKABLE_CRN, vecResult, vecReResult);

			//------------------------------------------------------------------------------------------------------------
			//	파라미터로 전달된 크레인설비ID를 스케줄기준 조회 후 작업가능한 크레인설비ID로 교체
			//------------------------------------------------------------------------------------------------------------
			msgRecord.setField("YD_EQP_ID", 			szYD_WRKABLE_CRN);
			msgRecord.setField("YD_SCH_PRIOR", 			szYD_SCH_PRIOR);
			msgRecord.setField("YD_SCH_CD", 			szSchCd);
			
			//------------------------------------------------------------------------------------------------------------
			//	크레인스케줄과 크레인작업재료 등록
			//------------------------------------------------------------------------------------------------------------
			szMsg="[" + szOperationName + "] 크레인스케줄 및 작업재료 등록!!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			intRtnVal = this.Y9CrnSchIns(vecReResult, msgRecord, szYdWbookId);
			
		} catch (Exception e) {
			
			szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}	// end try catch문
		
		szMsg="[" + szOperationName + "] 메소드 ("+szMethodName+") 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procY9CrnSchSub()
	
	/**
	 * 오퍼레이션명 : 1후판정정야드 작업예약재료 저장위치 셋팅
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szEqpId, szSchCd, rsResultRt
	 * @return intRtnVal 1: 성공, -1: 실패
	 * @throws JDTOException
	 */
	public void Y9ChkCrnSchEffectCondition(JDTORecord msgRecord) throws JDTOException  {
		PlateReviseDao ydDao 		= new PlateReviseDao();
		JDTORecordSet rsWrkbookmtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsResult 		= JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		JDTORecord recInTemp        = null;
		
		String szMsg        		= "";
		String szMethodName 		= "Y9ChkCrnSchEffectCondition";
		String szOperationName 		= "1후판정정야드 작업예약재료 저장위치 셋팅";
		String szYD_WBOOK_ID      	= "";
		String szRtnMsg				= "";
		
		try{
			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(msgRecord,"YD_WBOOK_ID");
			
			//-----------------------------------------------------------------------------------------------
			//	작업예약재료의 저장위치를 야드의 현 저장위치로 수정하기 위해서 작업예약재료 조회
			//-----------------------------------------------------------------------------------------------
			rsWrkbookmtl 		= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp		 	= JDTORecordFactory.getInstance().create(); 
			recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			
			ydDao.getLegacyYdInfo(recInTemp, rsWrkbookmtl, 10);
			//-----------------------------------------------------------------------------------------------
			
			JDTORecord recTemp				= JDTORecordFactory.getInstance().create();
			JDTORecord recOutTemp			= null;
			String szSTL_NO					= null;
			
			//-----------------------------------------------------------------------------------------------
			//	각 작업예약재료의 저장위치를 현 저장위치로 수정 
			//-----------------------------------------------------------------------------------------------
			for(int i = 1; i <= rsWrkbookmtl.size(); i++ ) {
				rsWrkbookmtl.absolute(i);
				recInTemp		= rsWrkbookmtl.getRecord();
				
				szSTL_NO		= ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
				
				recTemp.setField("STL_NO", 					szSTL_NO);
				recTemp.setField("YD_STK_LYR_MTL_STAT", 	YdConstant.YD_STK_LYR_MTL_STAT_STK);
				
				rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
				
				szRtnMsg		= DaoManager.getYdStklyr(recTemp, rsResult, 3);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
						
						rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
						
						recTemp.setField("YD_STK_LYR_MTL_STAT", 	YdConstant.YD_STK_LYR_MTL_STAT_UN_WAIT);
						
						szRtnMsg		= DaoManager.getYdStklyr(recTemp, rsResult, 3);
						
						if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
							szMsg="[" + szOperationName + "] 권상대기인 재료["+szSTL_NO+"]를 적치단에서 조회 시 오류발생[2] - 반환값 : " + szRtnMsg;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new JDTOException(szMsg);
						}
						
					}else{
						szMsg="[" + szOperationName + "] 적치중인 재료["+szSTL_NO+"]를 적치단에서 조회 시 오류발생[1] - 반환값 : " + szRtnMsg;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new JDTOException(szMsg);
					}
				}
				rsResult.first();
				recOutTemp 			= rsResult.getRecord();
				
				recInTemp.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP"));
				recInTemp.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_NO"));
				recInTemp.setField("YD_STK_LYR_NO", 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_LYR_NO"));
				recInTemp.setField("MODIFIER", 			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
				
				szRtnMsg			= DaoManager.updYdWrkbookmtl(recInTemp, 0);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg="[" + szOperationName + "] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"]의 위치정보를 수정 시 오류발생 - 반환값 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new JDTOException(szMsg);
				}
			}
			//-----------------------------------------------------------------------------------------------
			
		} catch (Exception e) {
			szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}	// end try catch문
	} // end of Y9ChkCrnSchEffectCondition()
	
	/**
	 * 오퍼레이션명 : 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int Y9CrnSchSort(JDTORecordSet rsMinWrkBookMtl, JDTORecordSet rsReturn)throws JDTOException  {
		
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		PlateReviseDao  ydDao 			= new PlateReviseDao();
		YdStkLyrDao     ydStkLyrDao     = new YdStkLyrDao();
		
		JDTORecord recInPara     	= null;
		//적치Bed정보
		JDTORecord recPara       	= null;
		//적치단정보
		JDTORecord recStkLyr     	= null;
		//현재적치단의 다음 재료정보
		JDTORecord recNextStkLyr 	= null;
		//현재적치단의 다음 권상모음순서  재료정보
		JDTORecord recNextUpCollSeq = null;
		JDTORecordSet rsResult 	 	= null;
		//작업예약재료 
		JDTORecordSet rsWrkBookMtl 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
		//결과 레코드셋
		JDTORecordSet rsCrnSchResult= JDTORecordFactory.getInstance().createRecordSet("Temp");
		//레코드셋 정렬 시
		JDTORecord recTemp     		= null;
		//Bed조회시 정렬
		JDTORecord rsMaxWrkBookMtl 	= null;
		JDTORecordSet rsSelBed 		= JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		String szMsg				= "";
		String szMethodName			= "Y9CrnSchSort";
		String szOperationName		= "1후판정정야드크레인그룹핑파라미터설정/정렬";
		
		String szColGp  	= "";
		String szBedNo   	= "";
		String szLyrNo   	= "";
		String szMtlStat 	= "";
		String szMaxCollGp  = "";
		String szMaxBedNo   = "";
		String szWbookId 	= "";
		//최하단 재료 구분
		String szLowestLyrNo = "";
		
		int intYdUpCollSeq 	= 0;
		int intRtnVal 		= 0;
		int intHandlingCnt 	= 1;

		try {
			//------------------------------------------------------------------------------------------------------------
			//	작업예약재료 조회 - 권상모음 Desc ==> 최하단재료에서 상단재료로 조회
			//------------------------------------------------------------------------------------------------------------
			rsMinWrkBookMtl.absolute(1);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(rsMinWrkBookMtl.getRecord());
			szWbookId = recPara.getFieldString("YD_WBOOK_ID");
			
			rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsWrkBookMtl, 7);
						
			//권상 모음 순서가 가장 큰 재료의 정보를 가져온다.
			rsWrkBookMtl.absolute(1);
			rsMaxWrkBookMtl = rsWrkBookMtl.getRecord();
			szMaxCollGp  = rsMaxWrkBookMtl.getFieldString("YD_STK_COL_GP");
			szMaxBedNo   = rsMaxWrkBookMtl.getFieldString("YD_STK_BED_NO");
			
			//------------------------------------------------------------------------------------------------------------
			//	조회한 작업예약 재료의 수만큼 반복해서 권상모음순서가 가장 큰재료의 BED 순으로 정렬
			//------------------------------------------------------------------------------------------------------------
			for(int Loop_i = 1; Loop_i <= rsMinWrkBookMtl.size(); Loop_i++) {
				rsMinWrkBookMtl.absolute(Loop_i);
				recPara = rsMinWrkBookMtl.getRecord();
				//권상모음 순서가 가장 큰 재료정보와 같은 BED를 조회한다.
				if(szMaxCollGp.equals(recPara.getFieldString("YD_STK_COL_GP")) 
				 && szMaxBedNo.equals(recPara.getFieldString("YD_STK_BED_NO")) ) {
					//BED가 같다면 rsSelBed의 첫번째에 등록한다.
					rsSelBed.addRecord(recPara);
				}
			}
			
			for(int Loop_i = 1; Loop_i <= rsMinWrkBookMtl.size(); Loop_i++) {
				rsMinWrkBookMtl.absolute(Loop_i);
				recPara = rsMinWrkBookMtl.getRecord();
				if(szMaxCollGp.equals(recPara.getFieldString("YD_STK_COL_GP")) 
				 && szMaxBedNo.equals(recPara.getFieldString("YD_STK_BED_NO")) ) {
					
				}else{
					//권상 모음 순서가 가장 큰 재료정보의  BED를 제외한 BED를 차례대로  등록한다.
					rsSelBed.addRecord(recPara);
				}
			}
			
			//------------------------------------------------------------------------------------------------------------
			//	Bed별로 작업예약재료를 조회해서 받는다. - 각 재료의 레코드에 주작업/보조작업, TO위치결정방법 파라미터 설정
			//------------------------------------------------------------------------------------------------------------
			for(int Loop_i = 1; Loop_i <= rsSelBed.size(); Loop_i++) {
				rsSelBed.absolute(Loop_i);
				//적치Bed를 조회한다.
				recPara = rsSelBed.getRecord();
				//현재 적치중인 것만 받는다.
				recPara.setField("YD_STK_LYR_MTL_STAT", "C");
				szColGp   = recPara.getFieldString("YD_STK_COL_GP");
				szBedNo   = recPara.getFieldString("YD_STK_BED_NO");
				szLyrNo   = recPara.getFieldString("YD_STK_LYR_NO");
				szMtlStat = recPara.getFieldString("YD_STK_LYR_MTL_STAT");
				
				recInPara = JDTORecordFactory.getInstance().create();
				recInPara.setField("YD_STK_LYR_NO1",       szLyrNo);
				recInPara.setField("YD_STK_LYR_NO2",       szLyrNo);
				recInPara.setField("YD_WBOOK_ID",         szWbookId);
				recInPara.setField("YD_STK_COL_GP",       szColGp);
				recInPara.setField("YD_STK_BED_NO",       szBedNo);
				recInPara.setField("YD_STK_LYR_MTL_STAT", szMtlStat);
				
	    		rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    		intRtnVal = ydDao.getLegacyYdInfo(recInPara, rsResult, 1);
	    		
	    		for(int Loop_j = 1; Loop_j <= rsResult.size(); Loop_j++) {
	    			rsResult.absolute(Loop_j);
	    			recStkLyr = rsResult.getRecord();
	    			//Bed순서
	    			recStkLyr.setField("BED_CNT", ""+Loop_i);
	    			recStkLyr.setField("YD_WBOOK_ID", recPara.getFieldString("YD_WBOOK_ID"));
	    			//HandlingCount
	    			recStkLyr.setField("HANDLING_CNT", ""+intHandlingCnt);
		    		//핸들링 카운트 증가
		    		intHandlingCnt++;
		    		//최하단 구분자
		    		szLowestLyrNo = recStkLyr.getFieldString("LOWEST_LYR_NO");
		    		
	    			//주작업여부판단
	    			if(ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ") > 0) {
	    				//주작업
	    				recStkLyr.setField("MAIN_WRK_YN", "Y");
	    				//권상모음순서
		    			intYdUpCollSeq = ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ");
		    			//권상모음Base
		    			recStkLyr.setField("UP_COLL_BASE", "" + (rsWrkBookMtl.size() - intYdUpCollSeq + 1));
		    			//현재작업재료의 다음 작업재료
		    			rsResult.next();
		    			recNextStkLyr = rsResult.getRecord();
		    			
		    			//주작업이적
		    			//최하단 구분자
		    			//최하단 재료인 경우
						if(szLowestLyrNo.equals("Y")){
	    					recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "B");
	    					//권상모음 순서를 권상모음 Base의 값으로 바꾼다. (그룹핑 시.. 같은 그룹이 되는 것을 방지하기 위해...)
	       					//다음작업쟤료번호
							rsWrkBookMtl.first();
							recNextUpCollSeq = rsWrkBookMtl.getRecord();
							//현재작업재료번호의 권상모음순서가 마지막인 경우 바로 S로 등록한다.
							if(recStkLyr.getFieldString("YD_UP_COLL_SEQ").equals(recNextUpCollSeq.getFieldString("YD_UP_COLL_SEQ"))) {
								recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "S");
								recStkLyr.setField("YD_UP_COLL_SEQ", ""+ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
							}						
	    				//최하단 재료가 아닌 경우
						}else{
	    					//다음재료가 보조작업인 경우
	    					if(ydDaoUtils.paraRecChkNullInt(recNextStkLyr,"YD_UP_COLL_SEQ") == 0){
	    						recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "M");
	    						recStkLyr.setField("UP_COLL_STL_NO", "");
	    						recStkLyr.setField("YD_UP_COLL_SEQ", ""+ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
	    					//다음재료가 주작업인 경우
	    					}else{
	    						recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "A");
	    						recStkLyr.setField("UP_COLL_STL_NO", "");
	    						recStkLyr.setField("YD_UP_COLL_SEQ", ""+intYdUpCollSeq);
	    					}
	    				}
		    			
	    			}else{
	    				//보조작업
	    				recStkLyr.setField("MAIN_WRK_YN", "N");
	    				recStkLyr.setField("YD_TO_LOC_DCSN_MTD", "W");
	    				recStkLyr.setField("UP_COLL_STL_NO","");
	    				recStkLyr.setField("UP_COLL_BASE","");
	    				recStkLyr.setField("YD_UP_COLL_SEQ", ""+ydDaoUtils.paraRecChkNullInt(recStkLyr,"YD_UP_COLL_SEQ"));
	    			}
	    			rsCrnSchResult.addRecord(recStkLyr);

	    		}//end of for
	    		
			}//end of for
			
			//------------------------------------------------------------------------------------------------------------
			
			szMsg = "=======================================  정렬하기전   ===========================================";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			for(int Loop_i = 1; Loop_i <= rsCrnSchResult.size(); Loop_i++) {
				rsCrnSchResult.absolute(Loop_i);
				JDTORecord recCurrt = JDTORecordFactory.getInstance().create();
				recCurrt = rsCrnSchResult.getRecord();
				szMsg = recCurrt.toString();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			szMsg = "==============================================================================================";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------------
			//	레코드셋 정렬	- Handling Count순으로 정렬
			//------------------------------------------------------------------------------------------------------------
			JDTORecord recAfter = null;
			JDTORecord recCurrt = null;
			for(int Loop_i = 1; Loop_i < rsCrnSchResult.size(); Loop_i++) {
			
				for (int Loop_j = Loop_i + 1; Loop_j < rsCrnSchResult.size() + 1; Loop_j++) {
					
					rsCrnSchResult.absolute(Loop_i);
					recCurrt = rsCrnSchResult.getRecord();
					
					rsCrnSchResult.absolute(Loop_j);
					recAfter = rsCrnSchResult.getRecord();

					if (recCurrt.getFieldInt("HANDLING_CNT") > recAfter.getFieldInt("HANDLING_CNT")) {
							
						rsCrnSchResult = this.Y9RsSort(Loop_i, Loop_j, rsCrnSchResult);
					}
				}//end of infor
			}//end of outfor
			
			szMsg = "=======================================  정렬하기후   ===========================================";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			for(int Loop_i = 1; Loop_i <= rsCrnSchResult.size(); Loop_i++) {
				rsCrnSchResult.absolute(Loop_i);
				recCurrt = rsCrnSchResult.getRecord();
				rsReturn.addRecord(recCurrt);
				
				szMsg = recCurrt.toString();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			szMsg = "==============================================================================================";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e) {
			szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		return intRtnVal = 1;
	} //end of Y9CrnSchSort
	
	/**
     * 오퍼레이션명 : 레코드 치환
     *  
     * @param  ● intLoop_i, intLoop_j, rsCrnSchResult
     * @return ● JDTORecordSet
     * @throws ● JDTOException
     */
    public JDTORecordSet Y9RsSort (int intLoop_i, int intLoop_j, JDTORecordSet rsCrnSchResult)throws JDTOException{

    	JDTORecord recTemp 		= null;
    	JDTORecordSet rsTemp 	= null; 
    	
		String szMsg 			= "";
		String szMethodName 	= "Y9RsSort";
		String szOperationName  = "1후판정정야드레코드 치환";
		try{
			rsTemp = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			for(int Loop_i = 1;  Loop_i <= rsCrnSchResult.size(); Loop_i++) {
				if(Loop_i == intLoop_i) {
					rsCrnSchResult.absolute(intLoop_j);
					recTemp = rsCrnSchResult.getRecord();
				}else if(Loop_i == intLoop_j) {
					rsCrnSchResult.absolute(intLoop_i);
					recTemp = rsCrnSchResult.getRecord();
				}else{
					rsCrnSchResult.absolute(Loop_i);
					recTemp = rsCrnSchResult.getRecord();
				}
				rsTemp.addRecord(recTemp);
			}
			
		}catch(Exception e){
			szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
        }//end of try~catch
		
		return rsTemp;
    }//end of Y9RsSort()
    
    /**
     * 오퍼레이션명 : 1후판정정야드스케줄링 Handling Data Check
     *  
     * @param  ● msgRecSet, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y9CrnSchDataHandling (JDTORecordSet msgRecSet, Vector vecResult) throws JDTOException {
		
		JDTORecord recPara       	= null;
		JDTORecordSet rsHandling 	= null;

		String szMsg             	= "";
		String szMethodName      	= "Y9CrnSchDataHandling";
		String szOperationName      = "1후판정정야드크레인Handling Lot편성";
		
		//이전 재료의 To위치 결정방법
		String szBefoToLocDcsnMtd 	= "";

		int intYdUpCollSeq 			= 0;
		int intCurrBedCnt 			= 0;
		int intBefoBedCnt 			= 0;
		
		int intBefoCollSeq 			= -1;		//이전작업의 권상 모음 순서
		int intRtnVal 				= 0;

		try{
			szMsg="[" + szOperationName + "] 메소드 시작  - 대상재건수["+msgRecSet.size()+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------------
			//	Handling Lot 편성
			//------------------------------------------------------------------------------------------------------------	
    		for (int Loop_i = 1; Loop_i <= msgRecSet.size(); Loop_i++) {
    			msgRecSet.absolute(Loop_i);
    			recPara = msgRecSet.getRecord();
    			//	권상모음순서
    			intYdUpCollSeq = ydDaoUtils.paraRecChkNullInt(recPara, "YD_UP_COLL_SEQ");
    			intCurrBedCnt = ydDaoUtils.paraRecChkNullInt(recPara, "BED_CNT");
    			
				szMsg = "[" + szOperationName + "] [" + Loop_i + "]번째 재료 권상모음순서 : intYdUpCollSeq : " + intYdUpCollSeq;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szMsg = "[" + szOperationName + "] [" + Loop_i + "]번째 베드카운트 : intCurrBedCnt : " + intCurrBedCnt;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szMsg = "[" + szOperationName + "] [" + Loop_i + "]번째 이전권상모음순서 : intBefoCollSeq : " + intBefoCollSeq;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//-------------------------------------------------------------------------------------------------------------
    			//	처음에 새그룹 생성
				//-------------------------------------------------------------------------------------------------------------
    			if (Loop_i == 1) {
    				
    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
    				rsHandling.addRecord(recPara);
    				vecResult.add(rsHandling) ;
    				intBefoCollSeq = intYdUpCollSeq;
    				intBefoBedCnt  = intCurrBedCnt;
    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
    				
    				szMsg = "[" + szOperationName + "] [" + Loop_i + "]번째 새그룹 생성";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				
    			} else {
    				//-------------------------------------------------------------------------------------------------------------
    				//	새그룹 생성 - 베드가 서로 다른 경우
    				//-------------------------------------------------------------------------------------------------------------
    				if (intCurrBedCnt != intBefoBedCnt) {
    					
    					szMsg = "[" + szOperationName + "] [" + Loop_i + "]번째 현재료의 베드 번호["+intCurrBedCnt+"]와 이전재료의 베드번호["+intBefoBedCnt+"]가 다르므로 새그룹 생성";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    				
	    				rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				rsHandling.addRecord(recPara);
	    				vecResult.add(rsHandling) ;
	    				intBefoCollSeq = intYdUpCollSeq;
	    				intBefoBedCnt  = intCurrBedCnt;
	    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
	    				
	    				continue;
    				
	    			//-------------------------------------------------------------------------------------------------------------
	    			//	새그룹 생성 - (현재 권상모음순서가 0보다 크고 이전 권상모음순서가 0인경우) : 현재 주작업재료, 이전 보조작업재료
	    			//-------------------------------------------------------------------------------------------------------------
    				}else if (intYdUpCollSeq > 0 && intBefoCollSeq == 0) {
    					
    					szMsg = "[" + szOperationName + "] [" + Loop_i + "]번째 현재 주작업재료, 이전 보조작업재료이므로 새그룹 생성";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					
    					rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				rsHandling.addRecord(recPara);
	    				vecResult.add(rsHandling);
	    				intBefoCollSeq = intYdUpCollSeq;
	    				intBefoBedCnt  = intCurrBedCnt;
	    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
	    				continue;
	    			//-------------------------------------------------------------------------------------------------------------
	    			//	새그룹 생성( 현재 권상모음순서가 0이고 이전 권상모음순서가 0보다 큰 경우) : 현재 보조작업재료, 이전 주작업재료
	    			//-------------------------------------------------------------------------------------------------------------
    				} else if (intYdUpCollSeq == 0 && intBefoCollSeq > 0) {
    					
    					szMsg = "[" + szOperationName + "] [" + Loop_i + "]번째 현재 보조작업재료, 이전 주작업재료이므로 새그룹 생성";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					
    					rsHandling = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    				rsHandling.addRecord(recPara);
	    				vecResult.add(rsHandling);
	    				intBefoCollSeq = intYdUpCollSeq;
	    				intBefoBedCnt  = intCurrBedCnt;
	    				szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
	    				continue;
	    			//-------------------------------------------------------------------------------------------------------------
	    			//	기존 그룹에 추가 (이전 권상모음순서가 0이고 현재 권상모음순서가 0인 경우) : 현재 보조작업재료, 이전 보조작업재료
	    			//-------------------------------------------------------------------------------------------------------------
    				} else if (intYdUpCollSeq == 0 && intBefoCollSeq == 0) {
    					
    					szMsg = "[" + szOperationName + "] [" + Loop_i + "]번째 현재 보조작업재료, 이전 보조작업재료이므로 기존그룹에 추가";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					
    					rsHandling.addRecord(recPara);
    					intBefoCollSeq = intYdUpCollSeq;
	    				intBefoBedCnt  = intCurrBedCnt;
    					szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
    					continue;
    				//-------------------------------------------------------------------------------------------------------------
    				//	기존 그룹에 추가 ( 현재 권상모음순서가 0보다 크고 이전권상모음순서도 0보다 클때 : 현재주작업재료, 이전 주작업재료
    				//-------------------------------------------------------------------------------------------------------------
    				} else if (intYdUpCollSeq > 0 && intBefoCollSeq > 0) {
    					
    					szMsg = "[" + szOperationName + "] [" + Loop_i + "]번째 현재주작업재료, 이전 주작업재료이므로 기존그룹에 추가";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					
    					rsHandling.addRecord(recPara);
						intBefoCollSeq = intYdUpCollSeq;
	    				intBefoBedCnt  = intCurrBedCnt;
						szBefoToLocDcsnMtd = recPara.getFieldString("YD_TO_LOC_DCSN_MTD");
						continue;
    					
    				//에러	
    				} else {
    					szMsg="[" + szOperationName + "] 주작업 보조작업 판단 중 - Error ";
    					throw new JDTOException("<Y9CrnSchDataHandling> " + szMsg);
    				}	    					
    			}
    		}//end of for
	
        }catch(Exception e){
        	szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
        }//end of try~catch
        
		szMsg = "[" + szOperationName + "] 메소드 끝 - Handling Lot 수 : " + vecResult.size();
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return intRtnVal = 1;
        
    }//end of Y9CrnSchDataHandling()
    
    /**
     * 오퍼레이션명 : 1후판정정야드스케줄링 Handling Data 크레인사양Check
     *  
     * @param  ● szEqpId, vecHandledData, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y9ChkHandledDataCrnSpec (String szEqpId, Vector vecHandledData, Vector vecResult) throws JDTOException {
    	
    	YdDaoUtils ydDaoUtils 	  = new YdDaoUtils();
    	//크레인사양 DAO
		YdCrnSpecDao ydCrnSpecDao = new YdCrnSpecDao();
		
		JDTORecord    recPara    = null;
		JDTORecord    recCrnSpec = null;
		JDTORecordSet rsPara 	 = null;
		JDTORecordSet rsMain 	 = null;
		JDTORecordSet rsResult   = null;
		
		String szMsg             = "";
		String szMethodName      = "Y9ChkHandledDataCrnSpec";
		String szOperationName   = "1후판정정야드스케줄링 Handling Data 크레인사양Check";
		String szYD_TO_LOC_DCSN_MTD = "";
		int intRtnVal = 0;
		
		boolean blnRtnVal = false;
		//최대 폭			
		double dblMaxWidth  = 0;
		//현재 폭
		double dblCurrWidth = 0;
		//최대 두께			
		double dblMaxThick  = 0;
		//현재 두께
		double dblCurrThick = 0;
		//중량의 합
		long lngSumWt     = 0;		
		//현재 중량
		long lngCurrWt    = 0;			
		//재료매수
		int intMtlSh      = 0;	
		//크레인작업가능매수
		int intCrnWrkableSh	= 0;
    	
		try{	
			
			//크레인 사양과 비교 Check
			szMsg="vecHandledData.size(): " + vecHandledData.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
    		for(int Loop_i = 0; Loop_i < vecHandledData.size(); Loop_i++) {
    			//폭,중량,매수 초기화
    			dblCurrWidth= 0;
    			dblMaxWidth = 0;
    			dblMaxThick = 0;
    			lngCurrWt 	= 0;
    			lngSumWt 	= 0;
    			intMtlSh 	= 0;
    			
    			rsPara = (JDTORecordSet)vecHandledData.get(Loop_i) ;
    			rsPara.first();
    			rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
    			//새그룹 생성
    			vecResult.add(rsMain);
    			int intGp = 0;
    			for(int Loop_j = 0; Loop_j < rsPara.size(); Loop_j++) {
    				rsPara.absolute(Loop_j+1);
    				//rsParac의 레코드를 읽어온다.
    				recPara = rsPara.getRecord();
    				//재료의 현재 폭
    				dblCurrWidth = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");
    				//재료의 현재 두께
    				dblCurrThick = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_T");
    				//최대 폭
    				if(dblCurrWidth > dblMaxWidth) dblMaxWidth = dblCurrWidth;
    				if(dblCurrThick > dblMaxThick) dblMaxThick = dblCurrThick;
    				//재료의 현재 중량
    				lngCurrWt = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
    				//누적중량
    				lngSumWt = lngSumWt + lngCurrWt;
    				//현재 재료 매수
    				intMtlSh++;
    				//2013.09.03 윤재광 - 후판제품창고 크레인사양정보 가지고 셋팅 : 추후 변경요
    				intCrnWrkableSh = PlateGdsYdUtil.getCrnWrkableShBasedOnWT(dblMaxThick, dblMaxWidth);
    				
    				//기존그룹 추가
    				if (intMtlSh <= intCrnWrkableSh) {
    					
    					recPara.setField("SPEC_OVER", "N");
    					rsMain.addRecord(recPara);

    					szMsg="기존그룹 LoopJ : " + Loop_j + "intRtnVal : " + intRtnVal;
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				
    				//새그룹 생성
    				} else {
    					intGp = 1;
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
    					
        				szMsg="새그룹 LoopJ : " + Loop_j + "intRtnVal : " + intRtnVal;
        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    					
    				}
    
    			}//end of infor
    			
    		}//end of outfor
    		
			return intRtnVal = 1;
	
		}catch (Exception e) {
			szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
    }//end of Y9ChkHandledDataCrnSpec()
	
    /**
     * 오퍼레이션명 : 1후판정정야드창고스케줄링 크레인 스케줄 등록
     *  
     * @param  ● vResult, msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int Y9CrnSchIns (Vector vResult, JDTORecord msgRecord, String szYD_WBOOK_ID) throws JDTOException {
    	
    	YdWrkbookDao ydWrkbookDao 		= new YdWrkbookDao();
    	PlateReviseDao ydDao 			= new PlateReviseDao();
    	YdCrnWrkMtlDao ydCrnwrkmtlDao 	= new YdCrnWrkMtlDao();
    	YdSchRuleDao ydSchRuleDao 		= new YdSchRuleDao();
    	YdCrnSchDao ydCrnschDao 		= new YdCrnSchDao();
    	YdStkLyrDao ydStklyrDao 		= new YdStkLyrDao();
    	Vector vecHandledData      = new Vector();
    	
		JDTORecord recIn       = null;
		JDTORecord recSeq      = null;
		JDTORecord recInTemp   = null;
		JDTORecord recInPara   = null;
		JDTORecord recOutTemp  = null;
		
		JDTORecordSet rsOut    = null;
		JDTORecordSet rsResult = null;
		JDTORecordSet rsSchRuleResult = null;
		
		JDTORecord recWrkBookMtl   = null;
		JDTORecordSet rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		JDTORecord recSchCd   		= null;
		JDTORecordSet rsSchCd 		= JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecordSet rsSchRuleChk 	= null;
		JDTORecordSet rsTemp 		= null;
		JDTORecord recPara 			= null;
		
		int intCnt    = 1;
		int intRtnVal = 0;
		int rowcount  = 0;
		int vSize	  = 0;
		
		String szName 				= "SYSTEM";
		String szMsg 				= "";
		String szMethodName 		= "Y9CrnSchIns";
		String szOperationName 		= "1후판정정야드창고스케줄링 크레인 스케줄 등록";
		String szEqpId 				= "";
		String szSchCd 				= "";
		String szWbookId 			= "";
		String szYdSchPrior 		= "";
		String szYD_TO_LOC_GUIDE 	= "";
		String szYD_TO_LOC_DCSN_MTD = "";
		String szCRN_NO 			= "";
		
		boolean bENABLE_MAIN_CRN	= true;
		boolean bENABLE_AID_CRN		= true;
		
		String szYD_SCH_PROH_EXN  = null;
		String szYD_WRK_CRN       = null;
		String szYD_WRK_CRN_PRIOR = null;
		String szYD_ALT_CRN_YN    = null; 
		String szYD_ALT_CRN       = null;
		String szYD_ALT_CRN_PRIOR = null;
		String szYD_SCH_PRIOR     = null;
		String szREG_DDTT         = null;
		String szYD_SCH_ST_GP	  = null;
		
		boolean blnRtnVal = false;
		
		try{
			szEqpId 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szSchCd 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			szWbookId 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
			szYD_SCH_PRIOR		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_PRIOR");
			
			//작업예약재료조회
			rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("");
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			intRtnVal = ydDao.getLegacyYdInfo(recInPara, rsWrkBookMtl, 2);
			
			rsWrkBookMtl.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsWrkBookMtl.getRecord());
			
			szWbookId 		= ydDaoUtils.paraRecChkNull(recInTemp, "YD_WBOOK_ID");
			szREG_DDTT 		= ydDaoUtils.paraRecChkNull(recInTemp, "REG_DDTT");
			szYD_SCH_ST_GP	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_ST_GP");
			
			//크레인 스케줄에 Insert한다.	
			vSize = vResult.size();
			for(int i = 0; i < vSize; i++) {
				
				//Vector 값을  가져온다.
				rsResult = (JDTORecordSet) vResult.get(i);
				rowcount = rsResult.size();
				
				//크레인 스케줄 등록 마지막이 대표 정보임
				rsResult.last();
				recIn = JDTORecordFactory.getInstance().create();
				recIn.setRecord(rsResult.getRecord());
				
				//recIn 최초값 확인
				szMsg = "rsResult.last()값 확인!!";
				ydUtils.putLog(szSessionName, szMethodName, "rsResult.last()값 확인!!", YdConstant.DEBUG);
				
				recSeq = JDTORecordFactory.getInstance().create();
				recSeq.setField("YD_CRN_SCH_ID", "1");

				//크레인스케줄ID를 할당받는다
				rsOut = JDTORecordFactory.getInstance().createRecordSet("Temp");
				intRtnVal = ydCrnschDao.getYdCrnsch(recSeq, rsOut, 9);  
				
	    		//할당받은 크레인 스케줄 아이디로 Insert
	    		rsOut.first() ; 
	    		recSeq = JDTORecordFactory.getInstance().create();
				recSeq.setRecord(rsOut.getRecord());
				recIn.setField("YD_CRN_SCH_ID",    recSeq.getFieldString("YD_CRN_SCH_ID"));
				recIn.setField("YD_EQP_ID",        szEqpId);
				recIn.setField("YD_GP",            recIn.getFieldString("YD_STK_COL_GP").substring(0,1));
				recIn.setField("YD_BAY_GP",        recIn.getFieldString("YD_STK_COL_GP").substring(1,2));
				recIn.setField("YD_SCH_CD",        szSchCd);	
				recIn.setField("REGISTER",         recIn.getFieldString("HANDLING_CNT"));	
				recIn.setField("YD_CRN_GRAB_USE_RULE_ID",        recIn.getFieldString("UP_COLL_BASE"));	
				recIn.setField("YD_SCH_PRIOR",      szYD_SCH_PRIOR);
				recIn.setField("YD_WBOOK_DT",       szREG_DDTT);
				recIn.setField("YD_SCH_ST_GP",      szYD_SCH_ST_GP);
				
				szMsg = "YD_TO_LOC_DCSN_MTD : " +  recIn.getFieldString("YD_TO_LOC_DCSN_MTD");
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("M") 
				 || recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("W") 
				 || recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("S")
				 || recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("B")
				 || recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("A")) {
					
					recIn.setField("YD_UP_WO_LOC",     recIn.getFieldString("YD_STK_COL_GP") + recIn.getFieldString("YD_STK_BED_NO"));
					recIn.setField("YD_UP_WO_LAYER",   recIn.getFieldString("YD_STK_LYR_NO"));
					
					if(recIn.getFieldString("YD_UP_WO_LOC").trim().equals("")){
						szMsg = "권상지시위치가 없습니다.";
						throw new JDTOException("<Y9CrnSchIns> " + szMsg);
					}
				}
								  
				recIn.setField("YD_WRK_PROG_STAT", "W");
				 
				szMsg = "Y9InsYdCrnsch before i : " + i;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recIn.setField("YD_AID_WRK_MTL_SH", "" + rowcount);
				
				intRtnVal = ydCrnschDao.insYdCrnsch(recIn);		
				
				szMsg = "TO위치 결정방법이  (" + recIn.getFieldString("YD_TO_LOC_DCSN_MTD") + ") 인 경우" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				for(int Loop_j = 1; Loop_j <= rowcount; Loop_j++) {
					rsResult.absolute(Loop_j);
					recIn.setRecord( rsResult.getRecord() );
					
					szMsg = "크레인작업재료확인" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//적치단의 재료상태를 권상대기로 변경
					recIn.setField("YD_STK_LYR_MTL_STAT", "U");
					
					intRtnVal = ydStklyrDao.updYdStklyr(recIn, 0); 
					
					recIn.setField("YD_CRN_SCH_ID", recSeq.getFieldString("YD_CRN_SCH_ID"));
					/*
					 * 기존의 MAIN_WRK_YN 은 주작업이 Y 보조작업이 N으로 들어옴 크레인작업재료에는 보조작업여부에 값은 보조작업인경우 Y 주작업인경우 N로 셋팅!
					 */
					if(recIn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("W")){
						recIn.setField("YD_AID_WRK_YN", "Y");
					}else{
						recIn.setField("YD_AID_WRK_YN", "N");
					}
					//역순으로 등록
					recIn.setField("YD_STK_LYR_NO", ydDaoUtils.stringPlusInt("000", rowcount - Loop_j + 1));
					recIn.setField("REGISTER", szName);
					recIn.setField("MOD_DDTT", "");

					intRtnVal = ydCrnwrkmtlDao.insYdCrnwrkmtl(recIn);		
					
				}//end of in for
				
				intCnt = 1;
			}//end of out for
			
	    	//저장위치결정MAIN호출 : 설비ID, 작업예약ID
			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("YD_EQP_ID",   szEqpId);
			recIn.setField("YD_WBOOK_ID", szWbookId);
			
			this.procY9CrnStrLocDeciMain(recIn);
	    	
			szMsg="["+szOperationName+"] ----------------------- 메소드 끝 -----------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
			return intRtnVal = 1;
       
		}catch(Exception e){
			szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		
        }//end of try~catch
        
    }//end of Y9CrnSchIns()
    
    /**
     * 오퍼레이션명 : 위치검색범위 조회 DataSet
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int procY9CrnStrLocDeciMain (JDTORecord inRecord)throws JDTOException{
    	//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
	
    	YdCrnSchDao   ydCrnSchDao 		= new YdCrnSchDao(); 
    	PlateReviseDao ydDao		 	= new PlateReviseDao();
    	YdWrkbookDao  ydWrkbookDao 		= new YdWrkbookDao();
    	YdUtils       ydUtils 			= new YdUtils();

    	//크레인스케줄
    	JDTORecordSet rsCrnsch    		= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet rsCrnwrkmtl 		= JDTORecordFactory.getInstance().createRecordSet("Temp");
    	
    	//크레인작업재료
    	JDTORecord recWbook      		= null;
    	JDTORecord recCrnSch      		= null;
    	
    	JDTORecord    recInTemp 		= null;
    	JDTORecordSet rsTemp 			= null;

    	String szMethodName 			= "procY9CrnStrLocDeciMain";
    	String szOperationName			= "1후판정정야드 위치검색조회";
    	String szMsg        			= "";     	  
    	
    	//크레인스케줄id,스케줄코드,야드구분,동구분,To위치결정방법
    	String szCrnSchId 			= "";
    	String szSchCd    			= "";
    	String szToLocDcsnMtd 		= "";
    	String szToRtnMsg           = "N";
		//작업예약Id
		String szWbookId  			= "";
		//설비Id
		String szEqpId    			= "";
    	
    	int intRtnVal 				= 0 ;
    	 
        try{
        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
			szWbookId = ydDaoUtils.paraRecChkNull(inRecord, "YD_WBOOK_ID");
			szEqpId   = ydDaoUtils.paraRecChkNull(inRecord, "YD_EQP_ID");
			//-------------------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------------------
			//작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			//-------------------------------------------------------------------------------------------------------------
			rsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal 	= ydWrkbookDao.getYdWrkbook(inRecord, rsTemp, 0);
			if(intRtnVal <= 0) {
				szMsg = "["+szOperationName+"] Y4LocSrcRngDataSet 작업예약 조회 중 Error : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			rsTemp.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsTemp.getRecord());
			//-------------------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			rsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_WBOOK_ID", szWbookId);
			recInPara.setField("YD_EQP_ID", szEqpId);
			intRtnVal = ydCrnSchDao.getYdCrnsch(recInPara, rsCrnsch, 21);
		    
			szMsg = "["+szOperationName+"] 작업예약 ID로 크레인스케줄 조회 스케줄의 횟수 : " + rsCrnsch.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
		    for(int Loop_i = 1; Loop_i <= rsCrnsch.size(); Loop_i++) {

        		rsCrnsch.absolute(Loop_i);
        		recCrnSch  = rsCrnsch.getRecord();
        		
        		//크레인스케줄Data저장
        		szCrnSchId     = recCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = recCrnSch.getFieldString("YD_SCH_CD");
        		szToLocDcsnMtd = recCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");
        		
        		szMsg = "["+szOperationName+"] [" + Loop_i+"]번째 크레인 스케줄[" + szCrnSchId + "]에 대한 권하지시위치 결정 ";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

        		//크레인작업재료조회(쿼리등록 완료 : 수정요청 항목이 추가됨)
				rsCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
        		JDTORecord recInData = JDTORecordFactory.getInstance().create();
        		
        		recInData.setField("YD_CRN_SCH_ID", szCrnSchId);
        		intRtnVal = ydDao.getLegacyYdInfo(recInData, rsCrnwrkmtl, 3);
        		
            	if(szToLocDcsnMtd.equals("W")) {	
            		//-----------------------------------------------------------------------------------------------------
	        		//	보조작업인 경우 TO위치 결정
	        		//-----------------------------------------------------------------------------------------------------
            		this.Y9GetUsrAppLoc(inRecord, rsCrnwrkmtl, recCrnSch, recInTemp, 1);
            	}else{
	        		//-----------------------------------------------------------------------------------------------------
	        		//	주작업인 경우 TO위치 결정
	        		//-----------------------------------------------------------------------------------------------------
	        		this.Y9GetUsrAppLoc(inRecord, rsCrnwrkmtl, recCrnSch, recInTemp, 2);
        		}
        	}//end of for        	
        				
			//-------------------------------------------------------------------------------------------------------------
    		//크레인작업지시 호출 - 트랜잭션분리
        	//-------------------------------------------------------------------------------------------------------------
			//intRtnVal = this.chkY9CrnWrkOrdReqSub(inRecord);
			//-------------------------------------------------------------------------------------------------------------
    		
        }catch(Exception e){
        	szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
    	}//end of try~catch
        
        return intRtnVal;
    	
    }//end of procY9CrnStrLocDeciMain()
    
    /* 오퍼레이션명 : 사용자 지정위치
	*  
	* @param  ● msgRecord, intGp
	* @return ● intRtnVal '1'이상: 성공   '0'이하: 실패
	* @throws ● JDTOException
	*/
	
	public int Y9GetUsrAppLoc(JDTORecord inRecord, JDTORecordSet rsCrnwrkmtl, JDTORecord recCrnSch, JDTORecord recInTemp, int iGbn) throws JDTOException {
		//상위 Method : DnLocInsMain
		//┏━┓
		//┃ Bed의 정보와 크레인작업재료의 정보를 비교하여 저장위치를 구한다.
		//┗━┛
		
		YdStkBedDao    ydStkBedDao    = new YdStkBedDao();
		PlateReviseDao ydDao = new PlateReviseDao(); 
		
		
		//적치Bed를 조회한 정보
		JDTORecordSet rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
		//적치단을 조회한 정보
		JDTORecord recStkLyr = null;
		JDTORecordSet rsGetStkLyr = null;
		//크레인작업재료 정보
		JDTORecordSet rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		 
		JDTORecordSet outRecSet = null;
		JDTORecord recResultCrnwrkmtl = null;
		JDTORecord recReturnData = null;
		
		JDTORecord recGetCrnWrkMtl = null;
		
		//파라미터 rsBed를 조회
		JDTORecord recStkBed      = null;
		//적치Bed를 조회한 정보
		JDTORecord recGetRsSet    = null;
		
		JDTORecordSet rsResult = null;
		
		
		int intRtnVal 			= 0 ;
		String szMsg        	= "";
		String szMethodName 	= "Y9GetUsrAppLoc";
		String szOperationName 	= "사용자 지정위치";
		
		String szStkColGp       = "";
		String szStkBedNo       = ""; 
		String szCrnSchId  		= "";
		String szYD_SCH_CD 		= "";
		
		try{
			
			//크레인 작업재료 조회
			szCrnSchId 	= recCrnSch.getFieldString("YD_CRN_SCH_ID");
			szYD_SCH_CD = recCrnSch.getFieldString("YD_SCH_CD");
			
			rsResultCrnwrkmtl = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = ydDao.getLegacyYdInfo(recCrnSch, rsResultCrnwrkmtl, 4);
			
			String szToLoc = "";
			
			if(iGbn == 1){// 보조작업 위치결정
				
				JDTORecordSet rsResultTmp = JDTORecordFactory.getInstance().createRecordSet("");
				
				JDTORecord recTmpRt = JDTORecordFactory.getInstance().create();
				
				JDTORecord recTmp = JDTORecordFactory.getInstance().create();
				recTmp.setField("YD_CRN_SCH_ID", szCrnSchId);
								
				intRtnVal = ydDao.getLegacyYdInfo(recTmp, rsResultTmp, 11);
				
				if(rsResultTmp.size() > 0) {
					rsResultTmp.absolute(1);
					recTmpRt = rsResultTmp.getRecord();
					szToLoc = ydDaoUtils.paraRecChkNull(recTmpRt, "YD_TO_LOC_GUIDE");
				}else{
					szToLoc = "PXXXXXXX";
				}
				
			}else{// 주작업 위치결정
				
				szToLoc = ydDaoUtils.paraRecChkNull(recInTemp, "YD_TO_LOC_GUIDE");
				
			}
			
			if(szToLoc.length() == 8){
				szStkColGp = szToLoc.substring(0,6);
				szStkBedNo = szToLoc.substring(6,8);
			}else{
				// 2013.09.56 윤재광
				// R/T 북인시 설비번호 6자리만 들어옴
				szStkColGp = szToLoc.substring(0,6);
				szStkBedNo = "01";
			}
			recStkBed = JDTORecordFactory.getInstance().create();
			recStkBed.setField("YD_STK_COL_GP", szStkColGp);
			recStkBed.setField("YD_STK_BED_NO", szStkBedNo);
			
			//적치Bed조회한다.
			rsGetStkBed = JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = ydStkBedDao.getYdStkbed(recStkBed, rsGetStkBed, 0) ;
			
			//적치베드의 Max단,중량,높이
			rsGetStkBed.first();
			recGetRsSet = rsGetStkBed.getRecord();
			
			//적치Bed의 적치중이거나 권하대기상태인 재료정보를 가져온다.
			//적치단의 재료의 합계정보
			recStkLyr = JDTORecordFactory.getInstance().create();
			recStkLyr.setField("YD_STK_COL_GP", szStkColGp);
			recStkLyr.setField("YD_STK_BED_NO", szStkBedNo);
			
			rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = ydDao.getLegacyYdInfo(recStkLyr, rsGetStkLyr, 5);
			
			if(intRtnVal == 0) {
				szMsg="공베드입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if(rsGetStkLyr.size() > 0) {
				rsGetStkLyr.absolute(1);
				recGetRsSet = JDTORecordFactory.getInstance().create();
				recGetRsSet.setRecord(rsGetStkLyr.getRecord());
			}else{
				recGetRsSet = JDTORecordFactory.getInstance().create();
				recGetRsSet.setField("YD_STK_COL_GP", szStkColGp);
				recGetRsSet.setField("YD_STK_BED_NO", szStkBedNo);
			}
			
			//적치단 등록  
			intRtnVal = this.Y9UpdGradStkLyr(rsResultCrnwrkmtl, recGetRsSet);	
			
		}catch(Exception e){
			szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}//end of try~catch
		
		return intRtnVal = 1;
	
	}// end of Y9GetUsrAppLoc 
	
	/**
	 * 오퍼레이션명 : 적치단 등록
	 *  
	 * @param  rsResultCrnwrkmtl, recGetRsSet, intGrade
	 * @return int 성공:1, 실패:-1
	 * @throws 
	 */
	public int Y9UpdGradStkLyr (JDTORecordSet rsResultCrnwrkmtl, JDTORecord recGetRsSet)throws JDTOException{
		//상위 Method : chkLocsrchbedCrnMtl
		//┏━┓
		//┃ 저장위치의 등급에 따라 적치단에 재료를 등록한다.
		//┗━┛
		
		int intRtnVal = 0;
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
		
		JDTORecord recUpdStkLyrData = null;
		JDTORecord recSetStkLyrData = null;
		JDTORecord recUpdCrnSchData = null;
		
	    String szMsg               = "";
	    String szMethodName        = "Y9UpdGradStkLyr";
	    String szOperationName     = "적치단 등록";
	    
	    String szYD_STK_COL_GP = "";
	    String szYD_STK_BED_NO = "";
	    String szYD_STK_LYR_NO = "";
	    
	    try{
	    	
	    	//크레인 작업재료의 수만큼  for문 반복
	    	for(int Loop_i = 0; Loop_i < rsResultCrnwrkmtl.size(); Loop_i++) {
	    		
	    		
	    		rsResultCrnwrkmtl.absolute(Loop_i+1);
	    		recSetStkLyrData = rsResultCrnwrkmtl.getRecord();
	
	    		if(Loop_i == 0) {
	    			//공베드 인 경우
	   				if ( ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LYR_NO").equals("")) {
	   					szYD_STK_LYR_NO = "000";
	   				}else{
	   				//공베드가 아닌 경우
	   					szYD_STK_LYR_NO = ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LYR_NO"), Loop_i);
	   				}
	    		}
	    		szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_COL_GP");
	    		szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_BED_NO");
	    		
				recUpdStkLyrData = JDTORecordFactory.getInstance().create() ;
				recUpdStkLyrData.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
				recUpdStkLyrData.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				//저장할 재료
				recUpdStkLyrData.setField("STL_NO",        ydDaoUtils.paraRecChkNull(recSetStkLyrData, "STL_NO")); 
				//권하대기상태로 적치단재료활성상태 변경
				recUpdStkLyrData.setField("YD_STK_LYR_MTL_STAT", "D");
				
				//공베드 인 경우
				if ( ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LYR_NO").equals("")) {
					recUpdStkLyrData.setField("YD_STK_LYR_NO",  ydDaoUtils.stringPlusInt("000",Loop_i+1) );
				}else{
				//공베드가 아닌 경우
					recUpdStkLyrData.setField("YD_STK_LYR_NO",  ydDaoUtils.stringPlusInt(ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LYR_NO"), Loop_i+1));
				}
				
				//적치단에 Update
				intRtnVal = ydStkLyrDao.updYdStklyr(recUpdStkLyrData, 0);
				
				if(Loop_i == 0 ) {
					
					//크레인 스케줄  권하지시위치 업데이트
					recUpdCrnSchData = JDTORecordFactory.getInstance().create();
					
					recUpdCrnSchData.setField("YD_CRN_SCH_ID",  recSetStkLyrData.getFieldString("YD_CRN_SCH_ID") );
					recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  "W" ) ;
					recUpdCrnSchData.setField("YD_DN_WO_LOC",   recUpdStkLyrData.getFieldString("YD_STK_COL_GP") + recUpdStkLyrData.getFieldString("YD_STK_BED_NO")) ;
					recUpdCrnSchData.setField("YD_DN_WO_LAYER", recUpdStkLyrData.getFieldString("YD_STK_LYR_NO") ) ;
					
					
					recUpdCrnSchData.setField("YD_EQP_WRK_SH",    ydDaoUtils.paraRecChkNull(recSetStkLyrData, "SH_CNT"));
					recUpdCrnSchData.setField("YD_EQP_WRK_WT",    ydDaoUtils.paraRecChkNull(recSetStkLyrData, "SUM_MTL_WT"));
					recUpdCrnSchData.setField("YD_EQP_WRK_T",     ydDaoUtils.paraRecChkNull(recSetStkLyrData, "SUM_MTL_T"));
					recUpdCrnSchData.setField("YD_EQP_WRK_MAX_W", ydDaoUtils.paraRecChkNull(recSetStkLyrData, "MAX_MTL_W"));
					recUpdCrnSchData.setField("YD_EQP_WRK_MAX_L", ydDaoUtils.paraRecChkNull(recSetStkLyrData, "MAX_MTL_L"));
					
					intRtnVal = ydCrnschDao.updYdCrnsch(recUpdCrnSchData, 0);		
						
				}
					
	    	}//END OF FOR
	    	
	
	    }catch(Exception e){
	    	szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
	    }//end of try~catch
		return intRtnVal = 1;
	}//end of Y9UpdGradStkLyr()

	
    /**
	 *      [A] 오퍼레이션명 :Routing Layout 재작업지시 송신
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException 
	 */
	public void procSmsSend(String sPlateNo,String sLoc,int intGbn)throws JDTOException  {
		
		// DAO 및 UTIL 객체 생성
		PlateReviseDao ydDao      = new PlateReviseDao();
		
		// 레코드 선언
		JDTORecordSet rsOutRecSet = null;
		JDTORecord recIn          = null;
		JDTORecord recGetVal      = null;
		
		// 변수 선언
		String szMethodName       = "procSmsSend";
		String szMsg              = "";
		String szOperationName    = "SMS L2 BOOK IN/OUT 실적";
		int intRtnVal             = 0;
		
		try{
			
			// 레코드 생성
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");

			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("PL_PLATE_NO", sPlateNo);
			recIn.setField("ZONE_NO"	, StringHelper.evl(YdCommonUtils.getY9ChgL2BookOutLoc(sLoc),""));
			
			intRtnVal = ydDao.getL2TelegramInfo(recIn, rsOutRecSet, intGbn);
			
			rsOutRecSet.first();
			recGetVal = rsOutRecSet.getRecord();
			
			if(intGbn == 0){
				String sMessage = recGetVal.getFieldString("T3ABIC");
				String sRetVal = this.sndSms(sMessage,"YDE5L001");
				szMsg = "BOOK IN실적처리(" + szMethodName + ") 완료["+sMessage+"]";
			}else if(intGbn == 1){
				String sMessage = recGetVal.getFieldString("T3ABOC");
				String sRetVal = this.sndSms(sMessage,"YDE5L002");
				szMsg = "BOOK OUT실적처리(" + szMethodName + ") 완료["+sMessage+"]";
			}
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			szMsg = "SMSD 재작업지시   Exception Error: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	}// end of procSmsSend()
	   
	/** sndSms  EAI (SMS) SEND 공통 
	 * @param   1. Message 내용, 2. TC코드 
	 * @return  String  정상일때 "Y"
	 * @throws DAOException
	 */	
	public String sndSms(String  message, String tccode) throws com.inisteel.cim.common.exception.DAOException { 
		
		String szMsg="";
		String szMethodName = "sndSms";
		com.inisteel.cim.common.jms.JmsQueueSender sender = null;
		String queueName = null; 
		JDTORecord inRecord = null;
		PropertyService propertyService=null;
		String   flag = "N";
		try {
	    // 프로퍼티 서비스 인스턴스를 취득합니다.
		propertyService = PropertyService.getInstance();

		// 큐 명칭을 프로퍼티로부터 취득합니다. [[ EAI = jms.queue.SMS_EAI_QUEUE ]]
		queueName = propertyService.getProperty("common.properties","jms.queue.SMSYD_EAI_QUEUE");
 
		sender = new com.inisteel.cim.common.jms.JmsQueueSender();
		// 큐에 연결할 리소스를 생성합니다.
		sender.initQueueService(queueName); 
		/*
		 * 큐에 넣을 데이터를 생성합니다.
		 * 1. LABEL2 에서 EAI 수신 정보를 생산 통데로 ByPass 한다.
		 */  
		inRecord = JDTORecordFactory.getInstance().create();
		//inRecord.setRecord(indo);		
		inRecord.setField("JMS_TC_CD", new String (tccode) );	
		inRecord.setField("JMS_TC_CREATE_DDTT", jspeed.base.util.DateHelper.format(
				new java.util.Date(System.currentTimeMillis()),
				"yyyyMMddHHmmss")); 		
		inRecord.setField("JMS_TC_MESSAGE", new String (message) );
 		
		// 큐에 데이터를 전송합니다.
		sender.send(inRecord);  

		szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + tccode  + ">   SEND FINISH ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		flag =  "Y";
		} catch(Exception e){  
			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + tccode  + ">   SEND Exception " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
              throw new com.inisteel.cim.common.exception.DAOException(getClass().getName() + e.getMessage(), e);
		}finally{
			   try{
			       sender.closeAll(); 
			   }catch(Exception e){
			        ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			   }
		}
	    return flag;
	} 
	
	/**
	 * 오퍼레이션명 :   1후판압연전단  북인요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procBookInReqInfo(JDTORecord msgRecord)throws JDTOException  {
		
		String szMsg		  	= "";
		String szMethodName	  	= "procBookInReqInfo";
		
		int intRtnVal 			= 0;		
		
		JDTORecordSet rsPara    = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		
		PlateReviseDao commDao = new PlateReviseDao();	
		
		try{
			String sCraneNo			= ydDaoUtils.paraRecChkNull(msgRecord,"CRANE_NO");
			String sYardNo 			= ydDaoUtils.paraRecChkNull(msgRecord,"YARD_NO");
			String sPlTrckZoneAsgn	= ydDaoUtils.paraRecChkNull(msgRecord,"PL_TRCK_ZONE_ASGN");	
			String sOperationMode	= ydDaoUtils.paraRecChkNull(msgRecord,"OPERATION_MODE");
			
			/*
			 * 2. 북인 R/T위치정보 가져오기
			 */
			String sYdStkColRt = StringHelper.evl(YdCommonUtils.getY9ChgL3BookOutLoc(sPlTrckZoneAsgn),"");
			/*
			 * 3. 크레인정보 가져오기
			 */
			String sEquipId = StringHelper.evl(YdCommonUtils.getY9ChgL3CraneInfo(sCraneNo),"");
			/*
			 * 4. 북인 야드저장위치 가져오기
			 */
			String sYdStkColYd = StringHelper.evl(YdCommonUtils.getY9ChgL3LocInfo(sYardNo),"");
			
			ydUtils.putLog(szSessionName, szMethodName, "[북인 야드저장위치] ("+sYdStkColYd+")", YdConstant.DEBUG);
			/*
			 * 5. 북인 작업예약 등록
			 */
			
			this.procY9CarryInOutReq("",sYdStkColRt,sEquipId,sYdStkColYd,1);
			
		}catch(Exception e){ 
			
			szMsg = "[1후판압연전단 북인 요구] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

		szMsg = "[1후판압연전단 북인 요구] ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // procBookInReqInfo
	
	/**
	 * 오퍼레이션명 :   1후판압연전단  북아웃요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procBookOutReqInfo(JDTORecord msgRecord)throws JDTOException  {
		
		String szMsg		  	= "";
		String szMethodName	  	= "procBookOutReqInfo";

		int intRtnVal 			= 0;		
		
		JDTORecordSet rsPara    = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		
		PlateReviseDao commDao = new PlateReviseDao();	
		
		try{
			String sPlL2TrkNo		= ydDaoUtils.paraRecChkNull(msgRecord,"PL_L2_TRK_NO");
			String sPlMplNo			= ydDaoUtils.paraRecChkNull(msgRecord,"PL_MPL_NO");
			String sPlTrckZoneAsgn	= ydDaoUtils.paraRecChkNull(msgRecord,"PL_TRCK_ZONE_ASGN");
			String sOperationMode	= ydDaoUtils.paraRecChkNull(msgRecord,"OPERATION_MODE");
			String sCraneNo			= ydDaoUtils.paraRecChkNull(msgRecord,"CRANE_NO");
			String sYardNo 			= ydDaoUtils.paraRecChkNull(msgRecord,"YARD_NO");
			
			/*
			 * 1. 재료번호 가져오기
			 */
			recPara.setField("PL_L2_TRK_NO" , sPlL2TrkNo);
			recPara.setField("PL_MPL_NO" , sPlMplNo);
			rsPara = commDao.getL3StlInfo(recPara);
			
			String szStlNo = "";
				
			if(rsPara.size() > 0){

				rsPara.first();
				JDTORecord recStlInfo =  rsPara.getRecord();

				szStlNo = ydDaoUtils.paraRecChkNull(recStlInfo, "STL_NO");
			}	
			
			if(!"".equals(szStlNo)){
				// 2후판 정정야드 재료정보 생성모듈 호출
				JPlateYdStockDAO    ydStockDao   	= new JPlateYdStockDAO();
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO",	szStlNo);  // 재료번호
				
				intRtnVal = ydStockDao.insYdStockBookOut(recPara);
			}
			/*
			 * 2. 북아웃 R/T위치정보 가져오기
			 */
			String sYdStkColRt = StringHelper.evl(YdCommonUtils.getY9ChgL3BookOutLoc(sPlTrckZoneAsgn),"");
			/*
			 * 3. 크레인정보 가져오기
			 */
			String sEquipId = StringHelper.evl(YdCommonUtils.getY9ChgL3CraneInfo(sCraneNo),"");
			/*
			 * 4. 북아웃 야드저장위치 가져오기
			 */
			String sYdStkColYd = StringHelper.evl(YdCommonUtils.getY9ChgL3LocInfo(sYardNo),"");
			
			ydUtils.putLog(szSessionName, szMethodName, "[북아웃 야드저장위치] ("+sYdStkColYd+")", YdConstant.DEBUG);
			/*
			 * 5. 북아웃 작업예약 등록
			 */
			this.procY9CarryInOutReq(szStlNo,sYdStkColRt,sEquipId,sYdStkColYd,2);
			
		}catch(Exception e){
			
			szMsg = "[1후판압연전단 북아웃 요구] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

		szMsg = "[1후판압연전단 북아웃 요구] ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // procBookOutReqInfo
	
	/**
	 * 오퍼레이션명 : 1후판정정야드 BOOK_IN요구(PDA화면 작업용)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY9CarryInOutReq(String sStl_no,		//재료번호
								    String sYdStkColRt,	//설비위치
								    String sEquipId,	//크레인번호
								    String sYdStkColYd	//야드위치
								   )throws JDTOException  {
		
		this.procY9CarryInOutReq(sStl_no,sYdStkColRt,sEquipId,sYdStkColYd,1);
	}
	/**
	 * 오퍼레이션명 : 1후판정정야드 BOOK_OUT요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY9CarryInOutReq(String sStl_no,		//재료번호
								    String sYdStkColRt,	//설비위치
								    String sEquipId,	//크레인번호
								    String sYdStkColYd,	//야드위치
								    int intBookInOutGbn	//1-북인요구,2-북아웃요구
			                       )throws JDTOException  {
		
		//설비 DAO
		YdEqpDao ydEqpDao = new YdEqpDao();
		//스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//적치단등록 DAO
		YdStkLyrDao ydStklyrDao 		= new YdStkLyrDao();
		//Delegate
		YdDelegate ydDelegate           = new YdDelegate();
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      		= false;
		//리턴값(int)
		int intRtnVal          		= 0;
		//메세지
		String szMsg           		= "";
		//메소드명
		String szMethodName     	= "procY9CarryInOutReq";
		String szOperationName		= "1후판정정야드 BOOK_IN/OUT작업요구";
		//사용자
		String szUser           	= "SYSTEM";
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//작업크레인우선순위
		String szYD_WRK_CRN_PRIOR  = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//대체크레인우선순위
		String szYD_ALT_CRN_PRIOR  = null;
		//선택크레인
		String szCrn               = null;
		//야드구분
		String szYD_GP             = null;
		//동구분
		String szYD_BAY_GP         = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR		= "";
		
		String sToGuideLoc 			= "";
		try {
			//=====================================================================================================================
			// 스케줄코드 생성 로직
			//=====================================================================================================================
			String szYD_SCH_CD_SUB = "";
			
			if(intBookInOutGbn == 1){
				szYD_SCH_CD_SUB = "01UM";
			}else{
				szYD_SCH_CD_SUB = "01LM";
			}
			
			szYD_SCH_CD = sYdStkColRt.substring(0, 4) + szYD_SCH_CD_SUB;
			
			szMsg = "["+szOperationName+"] 스케줄코드 생성 - ["+szYD_SCH_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//=====================================================================================================================
			
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			recPara = JDTORecordFactory.getInstance().create();
			
			//스케줄코드
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);

			//스케줄코드로 스케줄기준 Table 조회
			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult, 0);
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(sEquipId);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal) {
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")) {
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn 			= szYD_ALT_CRN;
					szYD_SCH_PRIOR 	= szYD_ALT_CRN_PRIOR;
				}
				
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다. 
				szCrn 			= sEquipId; //2010.10.16 윤재광 대체 szYD_WRK_CRN;
				szYD_SCH_PRIOR 	= szYD_WRK_CRN_PRIOR;
			}
			
			//BOOK IN 요구시 재료정보 가져오기
			if(intBookInOutGbn == 1){
				if("".equals(sStl_no)){
					sStl_no = this.getYdStlNoFromYd(sYdStkColYd);
				}else{
					//PDA 화면에서 북인요구시 재료번호가 넘어온다.
				}
				
				if("".equals(sStl_no)){
					szMsg = "북인작업대상재료 검색 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
			
			//다른 작업예약에 재료가 등록되어있는지 체크한다.
			blnRtnVal = this.chkYdWrkBookMtl(sStl_no);
			if(!blnRtnVal) return;
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if(!blnRtnVal) return;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			//작업예약 테이블 INSERT할 항목 레코드 생성
			recPara       = JDTORecordFactory.getInstance().create();
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
		    if(intBookInOutGbn == 1){
		    	sToGuideLoc = sYdStkColRt;
			}else{
				sToGuideLoc = sYdStkColYd;
			}
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID"			, szYD_WBOOK_ID);
			recPara.setField("YD_GP"				, szYD_GP);
			recPara.setField("YD_BAY_GP"			, szYD_BAY_GP);
			recPara.setField("YD_SCH_CD"			, szYD_SCH_CD);
			recPara.setField("YD_SCH_PRIOR"			, szYD_SCH_PRIOR);
			recPara.setField("YD_AIM_YD_GP"			, szYD_GP);
			recPara.setField("YD_AIM_BAY_GP"		, szYD_BAY_GP);
			recPara.setField("YD_TO_LOC_DCSN_MTD"	, "F");
			recPara.setField("YD_TO_LOC_GUIDE"		, sToGuideLoc+"01");
			recPara.setField("REGISTER"				, szUser);
			
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if(intRtnVal < 1) {
				szMsg = "작업예약 Table 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			//작업예약재료 테이블 조회 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",    szYD_WBOOK_ID);
			recPara.setField("REGISTER",       szUser);
			//재료번호
			recPara.setField("STL_NO", 		   sStl_no);
			if(intBookInOutGbn == 1){
		    	sToGuideLoc = sYdStkColYd;
			}else{
				sToGuideLoc = sYdStkColRt;
			}
			//적치열구분
			recPara.setField("YD_STK_COL_GP",  sToGuideLoc);
			//적치BED번호
			recPara.setField("YD_STK_BED_NO",  "01");
			//적치단번호
			recPara.setField("YD_STK_LYR_NO",  "001");
			//권상모음순서
			recPara.setField("YD_UP_COLL_SEQ", "1");
				
			// 작업예약재료 테이블에 등록한다.
			intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			
			//BOOK OUT 요구시 재료정보 R/T에 등록하기
			if(intBookInOutGbn == 2){
				/*
				 * R/T 설비위에 저장위치 등록한다.
				 */
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",       	sYdStkColRt);   
				recPara.setField("YD_STK_BED_NO",       	"01");               
				recPara.setField("YD_STK_LYR_NO", 			"001") ;
				recPara.setField("STL_NO",              	sStl_no);                            
				recPara.setField("YD_STK_LYR_MTL_STAT", 	"C");           
	
	        	intRtnVal = ydStklyrDao.updYdStklyr(recPara, 0);  //크레인 적치단의 재료정보 UPDATE
			}
			
			//크레인 스케줄 호출
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", 	"YDYDJ710");
			recPara.setField("YD_EQP_ID", 	szCrn);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	
			ydDelegate.sendMsg(recPara);
			//this.procY9CrnSchMain(recPara);
						
		} catch(Exception e) {
			szMsg = "1후판 정정야드 BOOK_IN/OUT 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	
	} // end of procY9CarryInOutReq()
	
	/**
	 * 오퍼레이션명 : 설비상태 체크
	 *  
	 * @param   String szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws JDTOException
	 */
	public boolean eqpStatCheck(String szEqpId)throws JDTOException  {

		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = null;
		//메소드명
		String szMethodName    = "eqpStatCheck";		
		//설비상태
		String szYD_EQP_STAT   = null;
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;		
		
		try {
			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);
			szMsg = "설비ID(" + szEqpId + ")입니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//설비 체크 및 데이터 조회
			blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
			if(!blnRtnVal) return blnRtnVal;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			
			//크레인의 상태가 'T'이면 false 리턴.
			//상수 수정 [2009.12.03] 이현성 
			if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {
				
				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
	
				blnRtnVal = true;
	
			}
		} catch(Exception e) {
			szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
		
	} //end of eqpStatCheck
	
	/**
	 * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szEqpId  설비ID
	 *         JDTORecordSet rsResult 결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
		
		//설비 DAO
		YdEqpDao ydEqpDao     = new YdEqpDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetEqp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			szMsg = "설비ID(" + szEqpId + ")입니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//설비ID
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//설비 테이블 조회
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 0);

			//리턴값 메세지처리
			if(intRtnVal > 1) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetEqp
    
	/**
	 * 오퍼레이션명 : 작업예약재료 등록여부 체크
	 *  
	 * @param   String szStlNo 재료번호
	 * @return boolean true(작업예약재료등록가능), false(작업예약재료등록불가)
	 * @throws JDTOException
	 */
	public boolean chkYdWrkBookMtl(String szStlNo)throws JDTOException  {
		
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		
		//메세지
		String szMsg              = null;
		//메소드명
		String szMethodName       = "chkYdWrkBookMtl";
		//리턴값(boolean)
		boolean blnRtnVal = false;
		//리턴값(int)
		int intRtnVal = 0;
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
			
		try {	
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//재료번호
			recPara.setField("STL_NO", szStlNo);
			
			//재료번호로 작업예약재료 테이블을 읽어온다.
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsResult, 2);
			
			//리턴값 메세지처리
			if(intRtnVal > 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				blnRtnVal = true;
				
			} else if(intRtnVal == -2) {
				
				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "작업예약재료 등록여부 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
		
	} //end of chkYdWrkBookMtl
	
	/**
	 * 오퍼레이션명 : 작업예약ID생성
	 *  
	 * @param  JDTORecordSet rsResult 결과 레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean getYdWbookId(JDTORecordSet rsResult)throws JDTOException  {
		
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		//메세지
		String szMsg              = null;
		//메소드명
		String szMethodName       = "getYdWbookId";
		//리턴값(int)
		int intRtnVal             = 0;
		//리턴값(boolean)
		boolean blnRtnVal         = false;
		//레코드 선언
		JDTORecord recPara        = null;
		
		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//================================================
			//파라미터를 설정하지 않으면 JSPEED에서 에러발생. 추후 수정요
			recPara.setField("YD_WBOOK_ID", "1");
			//================================================
			
			//작업예약 테이블의 시퀀스를 이용해 작업예약ID를 구해온다.
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult, 1);
			//리턴값 메세지처리
			if(intRtnVal > 1) {
				
				szMsg = "작업예약ID 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if(intRtnVal == 0) {
				
				szMsg = "작업예약ID를 구하지 못했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2) {
				
				szMsg = "작업예약ID 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "작업예약ID 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "작업예약ID생성 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of getYdWbookId
	
	/** 
	 * 북인작업요구시 요구저장위치의 적치단정보를 가져와서 최상단 재료를 리턴한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String getYdStlNoFromYd(String sLoc) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    rsResult       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String sReturnVal = "";
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();		
		String szMsg        = "";		
		String szMethodName = "getYdStlNoFromYd";
		int intRtnVal = 0;
		
		try {
			
			recPara.setField("YD_STK_COL_GP", sLoc);
			recPara.setField("YD_STK_BED_NO", "01");
			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 55);
			
			if (intRtnVal > 0) {
				//레코드 추출
				rsResult.first();
				recPara = rsResult.getRecord();
				
				String sTmpStlNo = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				String sTmpStat  = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
				
				//적치중일때만 북인이 가능한 재품을 넘겨준다.
				if("C".equals(sTmpStat)){
					sReturnVal = sTmpStlNo;
				}
			}   
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return sReturnVal;
	}	// end of getYdStlNoFromYd
	
	/**
	 * 오퍼레이션명 : 야드작업이력생성Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return void
	 * @throws JDTOException
	 */

	public void procWorkHistoryCreate(JDTORecord msgRecord)throws JDTOException  {
		
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		PlateReviseDao ydDao = new PlateReviseDao();
		
		int intRtnVal;
		String szMsg        	= "";
		String szMethodName 	= "procWorkHistoryCreate";
		String szOperationName 	= "야드작업이력생성Main";
		
		String szCrnSchId 		= ""; // 크레인스케줄ID
		String szStlNo 			= "";
		
		String szYD_STK_LYR_NO	= null;
		int intLYR_NO			= 0;
		
		JDTORecordSet rsResult = null;
		
		JDTORecordSet rsCrnStock = null;
		
		JDTORecord recCrnStock = null;
		
		JDTORecord    recMtl = null;
						
		try {
			szMsg="야드작업이력생성("+szMethodName+") 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szCrnSchId     = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID");
			
			//레코드 생성
			JDTORecord recPara  = JDTORecordFactory.getInstance().create();
			//크레인스케줄ID
			recPara.setField("YD_CRN_SCH_ID", szCrnSchId);
			
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			// 크레인스케줄의 작업재료들을 읽어온다.
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, rsResult, 1);
			
			// 작업재료 수만큼 루프
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				rsResult.absolute(Loop_i);
				recMtl = rsResult.getRecord();
				
				szStlNo = ydDaoUtils.paraRecChkNull(recMtl,"STL_NO");
				
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", szCrnSchId);
				recPara.setField("STL_NO", szStlNo);
				
				rsCrnStock  = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				intRtnVal = ydDao.getLegacyYdInfo(recPara, rsCrnStock, 8);
				
				if(intRtnVal<=0){
					return;
				}
				
				rsCrnStock.absolute(1);
				recCrnStock = rsCrnStock.getRecord();
				
				szYD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(recCrnStock, "YD_STK_LYR_NO");
				
				intLYR_NO = Integer.parseInt(szYD_STK_LYR_NO) - 1;
				
				//------------------------------------------------------------------------------------
				//	권상지시단, 권상실적단, 권하지시단, 권하실적단 정보를 각 재료별로 증가시킴
				//------------------------------------------------------------------------------------
				recCrnStock.setField("YD_UP_WO_LAYER", ydDaoUtils.stringPlusInt(recCrnStock.getFieldString("YD_UP_WO_LAYER"), intLYR_NO));
				recCrnStock.setField("YD_UP_WR_LAYER", ydDaoUtils.stringPlusInt(recCrnStock.getFieldString("YD_UP_WR_LAYER"), intLYR_NO));
				recCrnStock.setField("YD_DN_WO_LAYER", ydDaoUtils.stringPlusInt(recCrnStock.getFieldString("YD_DN_WO_LAYER"), intLYR_NO));
				recCrnStock.setField("YD_DN_WR_LAYER", ydDaoUtils.stringPlusInt(recCrnStock.getFieldString("YD_DN_WR_LAYER"), intLYR_NO));
				//------------------------------------------------------------------------------------
				
				// 이력테이블에 INSERT
				intRtnVal = ydWrkHistDao.insYdWrkHist(recCrnStock);
				
				if(intRtnVal<=0) {
					szMsg = "재료번호(" + szStlNo + ")에 대한 INSERT가 실패하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				
				
			} // end of for
					
		}  catch (Exception e) {
			szMsg="["+szOperationName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}	// end try catch문
			
		szMsg="야드작업이력생성("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return;
	}
	
	/**
	 * 크레인상태관리 - 명령선택기동
	 * 전문 ID : 
	 *  
	 * Input  : YD_EQP_ID			: 설비 ID 
	 *          YD_WRK_PROG_STAT 	: 작업진행상태
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String updCmdSelStart(JDTORecord[] inDto) throws DAOException {
		
		JDTORecord recPara 		= null;
		String szLogMsg 		= null;
		String szRtnMsg 		= null;
		String szMethodName 	= "updCmdSelStart";
		String szOperationName 	= "명령선택기동";
		String szYD_EQP_ID 		= null;
		
		try{
			szLogMsg = "JSP-SESSION [크레인상태관리 - 명령선택기동]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("JMS_TC_CD", 			"Y9YDL007");
			recPara.setField("YD_EQP_ID" , 			inDto[0].getFieldString("YD_EQP_ID"));
			recPara.setField("YD_WRK_PROG_STAT" , 	"W");
			
			this.procY9CrnWrkOrdReq(recPara);
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;

		}catch(Exception e){
			szLogMsg = "[JSP Session]명령선택기동 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [크레인상태관리 - 명령선택기동] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	/**
	 *  야드크레인 작업관리 POP_UP (권상실적 처리) -Y9YDL008
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String updCrnUpPrsBackUp(JDTORecord[] inDto) throws DAOException {
		
		String szLogMsg 		= null;
		String szRtnMsg 		= null;
		JDTORecord recPara 		= null;
		JDTORecordSet rsResult 	= null;
		String szMethodName 	= "updCrnUpPrsBackUp";
		String szYD_EQP_ID 		= null;
		String szYD_EQP_STAT 	= null;
		
		int intRtnVal 			= 0;
		
		try{
			szLogMsg = "JSP-SESSION [야드크레인 작업관리 POP_UP (권상실적 처리) ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				 
			szYD_EQP_ID = inDto[0].getFieldString("YD_EQP_ID");
			
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			* 크레인의 야드설비상태 - YD_EQP_STAT
			* W : 스케줄수행대기, 1 : 권상지시, 2 : 권상완료, 3 : 권하지시, 4 : 권하완료
			+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			//크레인의 야드설비상태를 먼저 확인
			rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			intRtnVal = YdCommonUtils.getYdEqp(szYD_EQP_ID, rsResult);
			if( intRtnVal == 0 ) {
				szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 - 크레인설비[" + szYD_EQP_ID + "]가 존재하지 않습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}else if( intRtnVal < 0 ) {
				szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 - 크레인설비[" + szYD_EQP_ID + "]조회시 오류 발생";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}
			
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			if( !szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_UP_WO) && !szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_IDLE) ) {
				//W : 스케줄수행대기, 1 : 권상지시 상태가 아닌 경우에는 에러메시지 반환
				szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 - 크레인의 야드설비상태[" + szYD_EQP_STAT + "]가 권상실적처리 가능한 상태[W : 스케줄수행대기, 1 : 권상지시]여야합니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CRN_STATUS_ERR;
			}
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("JMS_TC_CD", "Y9YDL008");
			recPara.setField("YD_EQP_ID",        szYD_EQP_ID);
			recPara.setField("YD_EQP_WRK_MODE",  "9");
			recPara.setField("YD_WRK_PROG_STAT", "2");
			recPara.setField("YD_CRN_SCH_ID",    inDto[0].getFieldString("YD_CRN_SCH_ID"));
			recPara.setField("YD_SCH_CD",        inDto[0].getFieldString("YD_SCH_CD"));
			recPara.setField("YD_UP_WR_LOC",     inDto[0].getFieldString("YD_UP_WO_LOC"));  
			recPara.setField("YD_UP_WR_LAYER",   inDto[0].getFieldString("YD_UP_WO_LAYER"));

			this.procY9CrnLdWr(recPara);
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				
		}catch(Exception e){
			e.printStackTrace();
			szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [야드크레인 작업관리 POP_UP (권상실적 처리) ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	/**
	 *  야드크레인 작업관리 POP_UP (권하실적 처리)-Y9YDL009
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String updCrnDnPrsBackUp(JDTORecord[] inDto) throws DAOException {
		
		String szLogMsg 		= null;
		String szRtnMsg 		= null;
		JDTORecord recPara 		= null;		
		JDTORecordSet rsResult 	= null;
		String szMethodName 	= "updCrnUpPrsBackUp";
		String szOperationName 	= "POP_UP (권하실적 처리)";
		String szYD_EQP_ID 		= null;
		int intRtnVal 			= 0;
		String szYD_EQP_STAT 	= null;
		
		try{
			
			szLogMsg = "JSP-SESSION [야드크레인 작업관리 POP_UP (권하실적 처리) ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			szYD_EQP_ID = inDto[0].getFieldString("YD_EQP_ID");
			
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			* 크레인의 야드설비상태 - YD_EQP_STAT
			* W : 스케줄수행대기, 1 : 권상지시, 2 : 권상완료, 3 : 권하지시, 4 : 권하완료
			+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			//크레인의 야드설비상태를 먼저 확인
			rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			intRtnVal = YdCommonUtils.getYdEqp(szYD_EQP_ID, rsResult);
			if( intRtnVal == 0 ) {
				szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 - 크레인설비[" + szYD_EQP_ID + "]가 존재하지 않습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}else if( intRtnVal < 0 ) {
				szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 - 크레인설비[" + szYD_EQP_ID + "]조회시 오류 발생";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}
			
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			if( !szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_UP_CMPL) && !szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_DN_WO) ) {
				//3 : 권하지시 상태가 아닌 경우에는 에러메시지 반환
				szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 - 크레인의 야드설비상태[" + szYD_EQP_STAT + "]가 권하실적처리 가능한 상태[2 : 권상완료, 3 : 권하지시]여야합니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				//return YdConstant.RETN_CRN_STATUS_ERR;
			}
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			recPara  = JDTORecordFactory.getInstance().create();
			
			recPara.setField("JMS_TC_CD", 		  "Y9YDL009");
			recPara.setField("YD_EQP_ID",         szYD_EQP_ID);
			recPara.setField("YD_EQP_WRK_MODE",   "9");//BAK_UP ,용도로 9로 세팅하여 보냄 
			recPara.setField("YD_WRK_PROG_STAT",  "4");
			recPara.setField("YD_SCH_CD",         inDto[0].getFieldString("YD_SCH_CD"));
			recPara.setField("YD_CRN_SCH_ID",     inDto[0].getFieldString("YD_CRN_SCH_ID"));
			recPara.setField("YD_DN_WR_LOC",      inDto[0].getFieldString("YD_DN_WR_LOC"));  
			recPara.setField("YD_DN_WR_LAYER",    inDto[0].getFieldString("YD_DN_WO_LAYER"));
			
			this.procY9CrnUdWr(recPara);
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			
		}catch(Exception e){
			szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [야드크레인 작업관리 POP_UP (권하실적 처리) ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	/**
	 * 권하위치 변경 (크레인작업관리 화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public Boolean updToPosFix(JDTORecord[] inDto) throws DAOException {
		
		int intRtnVal          = 0;				
		String szLogMsg        = null;
		String szMethodName    = "updToPosFix";		
		String szOperationName = "권하위치 변경 (크레인작업관리 화면)";
		
		String szStkPos        = null;
		String szStkColGp      = null;
		String szStkBedNo      = null;
		String szStkLyrNo      = null;
		
		JDTORecord    recPara  = null;
		JDTORecord    recInPara  = null;
		JDTORecord    recTemp  = null;
		JDTORecord    recSet   = null;
		JDTORecord    recSetTmp= null;
		JDTORecord    recInTemp= null;
		
		String szOldStkPos     = null;
		String szOldStkColGp   = null;
		String szOldStkBedNo   = null;
		String szOldStkLyrNo   = null;
		
		boolean bool = false;
			
		YdStkLyrDao ydStkLyrDao     = new YdStkLyrDao();
		YdCrnSchDao ydCrnSchDao     = new YdCrnSchDao();
		PlateReviseDao  ydDao 		= new PlateReviseDao();
		YdDelegate ydDelegate 		= new YdDelegate();
		YdStkColDao ydStkColDao     = new YdStkColDao();
		JDTORecordSet    outRecSet  = null;
		JDTORecordSet    outRecSetTmp = null;
		
		String szYdWrkProgStat 		="";		
		String szSendYdWrkProgStat 	="";
		String szYdGp  				="";
		String szYdSchCd 			="";
	
		String szYD_EQP_ID 			="";
	    String szRtnMsg 			="";
	    String szYdSchId 			="";
	    
	    String szYdGpTemp 			="";
	    String szEqpGp 				=""; // 변경 설비구분 
	    String szEqpGpBefo 			=""; // 기존 설비구분 
	    String szYdWbookId 			=""; //작업예약 ID
	    String szRtnMsg1			= null;
	    
	    EJBConnector ejbConn = null;
		
		try {
			szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++)
			{

				// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
				recPara   	= JDTORecordFactory.getInstance().create();
				outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
				
				szYdSchId =  yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), "");
				recPara.setField("YD_CRN_SCH_ID",szYdSchId);		
				
				szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회" ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
				
				if(intRtnVal < 0 )
				{
					szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
				}else if(intRtnVal == 0 ){
					szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄크레인스케줄 ID  정보: ["+ szYdSchId + "] 가 존재하지않습니다";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
            		
				}
				
				szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회 성공" ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				outRecSet.first();
				
				recTemp   = JDTORecordFactory.getInstance().create();			
				recTemp = outRecSet.getRecord();
				
				szOldStkPos   = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
				szOldStkLyrNo = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");
				
				szYdWbookId = ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
				
				//현 스케줄 작업 진행상태(DB) 
				szSendYdWrkProgStat =  ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
				
				szLogMsg = "[JSP Session] " + szOperationName + "현 스케줄 작업 진행상태(DB)[ " + szSendYdWrkProgStat  +" ]"  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
				szLogMsg = "[JSP Session] " + szOperationName + "DB 권하지시위치 [ " + szOldStkPos + "-" + szOldStkLyrNo +"]"  ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				szYdGpTemp = yddatautil.setDataDefault(inDto[x].getField("YD_GP"), "");
				
				szLogMsg = "[JSP Session] " + szOperationName + "야드구분 [ " + szYdGpTemp +"]" ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        			
				szStkPos = yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LOC"), "");
				
				szLogMsg = "[JSP Session] " + szOperationName + "입력받은 권하지시위치 [ " + szStkPos + "]"  ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				if ("".equals(szStkPos)){		
					szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 없습니다."  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
				}
				
				if(szOldStkPos.equals(szStkPos)){
					szLogMsg = "[JSP Session] " + szOperationName + "입력된 권하위치가 기존 권하위치와 같습니다."  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
				}
				
				szStkLyrNo = yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LAYER"), "");
				
				if ("".equals(szStkLyrNo)){
					szLogMsg = "[JSP Session] " + szOperationName +  "변경 권하지시단 정보가 없으나 재계산하여줍니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				if(szStkPos.length() >=8)
				{
					szStkColGp 	= szStkPos.substring(0, 6); 
					szStkBedNo 	= szStkPos.substring(6, 8);
					szEqpGp 	= szStkColGp.substring(2,4);
				}else{
					szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 맞지 않습니다"  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
				}
				//-----------------------------------------------------------------------
				
				//-----------------------------------------------------------------------
				//	권하지시위치 변경 시 베드의 TO위치 정합성 판단.
				//-----------------------------------------------------------------------
				YdStkLocVO	ydStkLocVO	= new YdStkLocVO();
				recInPara	= JDTORecordFactory.getInstance().create();
				
				/* 파라미터정의:	1) YD_STK_COL_GP	- 적치열
				 * 				2) YD_STK_BED_NO	- 적치베드
				 * 				3) YD_EQP_WRK_SH	- 작업총매수
				 * 				4) YD_EQP_WRK_WT	- 작업총중량
				 * 				5) YD_EQP_WRK_T		- 작업총두께
				 * 				6) YD_SCH_CD		- 스케줄코드
				 */
				recInPara.setField("YD_STK_COL_GP", szStkColGp);
				recInPara.setField("YD_STK_BED_NO", szStkBedNo);
				recInPara.setField("YD_EQP_WRK_SH", ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_SH"));
				recInPara.setField("YD_EQP_WRK_WT", ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_WT"));
				recInPara.setField("YD_EQP_WRK_T" , ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_T"));
				recInPara.setField("YD_SCH_CD"	  , ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD"));
				 				
				szRtnMsg1 = YdToLocDcsnUtil.procBedStackable(recInPara, ydStkLocVO, szMethodName);
				
				szLogMsg = "[JSP Session- " + szOperationName + "] 권하위치 Chekc Returen Value "+szRtnMsg1 ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
        		int intERR_CD = 0;
        		StringBuffer szSTATUS		= new StringBuffer();
        		
				
				if( !szRtnMsg1.equals(YdConstant.RETN_CD_SUCCESS) ) {
					if( szRtnMsg1.equals(YdConstant.RETN_CD_NOTEXIST) ) {
						
						intERR_CD = ydStkLocVO.getYdBedErrCd();
						
						if( intERR_CD >= YdConstant.YD_BED_ERR_CD_H_OVER ) {
							//해당하는 적치베드에 적치가능높이 OVER
							intERR_CD	-= YdConstant.YD_BED_ERR_CD_H_OVER;
							
							szSTATUS.append("적치가능높이 OVER");
						}
						
						if( intERR_CD >= YdConstant.YD_BED_ERR_CD_WT_OVER ) {
							//해당하는 적치베드에 적치가능중량 OVER
							intERR_CD	-= YdConstant.YD_BED_ERR_CD_WT_OVER;
							
							if( szSTATUS.length() > 0 ) szSTATUS.append(", ");
							
							szSTATUS.append("적치가능중량 OVER");
						}
						
						if( intERR_CD == YdConstant.YD_BED_ERR_CD_SH_OVER ) {
							//해당하는 적치베드에 적치가능매수 OVER
							
							if( szSTATUS.length() > 0 ) szSTATUS.append(", ");
							
							szSTATUS.append("적치가능매수 OVER");
						}
						
						szLogMsg = "해당크레인스케줄["+szYdSchId+"]의 권하지시적치열["+szStkColGp+"], 권하지시베드["+szStkBedNo+"]에 적치불가능합니다 - " + szSTATUS.toString();
						
					}else{
						
					}
					throw new DAOException(szLogMsg);
				}
				
				if( ydStkLocVO.getYdBedErrCd() != YdConstant.YD_BED_STACKABLE) {
					throw new DAOException(szLogMsg);
				}
				//-----------------------------------------------------------------------
				
				//-----------------------------------------------------------------------
				//	권하위치 변경할시에 작업지시 취소전문 발생 (데이터 변경전정보)
				//-----------------------------------------------------------------------
				szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
				szYdSchCd       = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
				
				// 신규 위치 적치단 정보
				szLogMsg = "[JSP Session] " + szOperationName +  "변경 권하지시단 정보 계산";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				recPara   	= JDTORecordFactory.getInstance().create();
				recTemp 	= outRecSet.getRecord();
				outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara.setField("YD_STK_COL_GP", szStkColGp);
				recPara.setField("YD_STK_BED_NO", szStkBedNo);
				
				szLogMsg = "[JSP Session] " + szOperationName +  "적치단 정보조회";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 29);
				
				if (intRtnVal == 0){
					szStkLyrNo ="001";
				}
				else if ( intRtnVal > 0 )
				{
					outRecSet.last();
					recTemp 	= outRecSet.getRecord();
					szStkLyrNo 	= ydDaoUtils.stringPlusInt(recTemp.getFieldString("YD_STK_LYR_NO"),1);					
				}
				
				szLogMsg =  "[JSP Session] " + szOperationName +  "신규위치정보 :"+ szStkColGp  + "-"+ szStkBedNo +":" +szStkLyrNo;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
				
				outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				intRtnVal = ydDao.getLegacyYdInfo(recPara, outRecSet, 9);
				
				szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회 리턴값 :[ "+  intRtnVal +" ]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				if (intRtnVal == 0)
				{
					szLogMsg =  "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 크레인 작업 재료가 없습니다.";
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException( szLogMsg );
				} else if (intRtnVal < 0){
					
					szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 재료 조회시 ERROR";
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException( szLogMsg );
				}

				//기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
				if( szOldStkPos.length() == 8 && (!szOldStkPos.equals("XX010101")) && (!szOldStkPos.equals("XXYY0101")))
				{	
					szOldStkColGp = szOldStkPos.substring(0, 6); 
					szOldStkBedNo = szOldStkPos.substring(6, 8);
					
					szLogMsg = "[JSP Session] " + szOperationName +  "기존위치정보 :"+ szOldStkColGp  + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;				
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
            		
					//실제로는 크레인작업재료의 개수만 필요함				
					outRecSet.first();
					for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
						
						recTemp =JDTORecordFactory.getInstance().create();
						recTemp = outRecSet.getRecord(nLoop);					
						
						// 기존 지시위치 에 쌓여 있는 정보 Clear
						recSet = JDTORecordFactory.getInstance().create();
		                recSet.setField("YD_STK_COL_GP",       szOldStkColGp);    
		                recSet.setField("YD_STK_BED_NO",       szOldStkBedNo); 
		                recSet.setField("STL_NO",              yddatautil.setDataDefault(recTemp.getField("STL_NO"),""));
		                
		                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear ";
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	            		
		                intRtnVal = ydStkLyrDao.updYdStklyrWithColStockStat(recSet);
		                
		                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
		            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		                
		            }		
					
				}else{
					szLogMsg = "[JSP Session] " + szOperationName +  "기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다";				
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				outRecSet.first();
				
				//작업재료는 밑에것부터 부터 위로 올라와있는 상태이므로 레코드셋을 역순으로 정렬한다
				outRecSet.reverseOrder();
				
				for (int nLoop =0 ; nLoop<outRecSet.size(); nLoop++ ){
					
					// 신규위치에 정보를 Setting
					recSet = JDTORecordFactory.getInstance().create();
					recTemp =JDTORecordFactory.getInstance().create();
					
					recTemp = outRecSet.getRecord(nLoop);					
					recSet.setField("YD_STK_COL_GP",       szStkColGp);    
		            recSet.setField("YD_STK_BED_NO",       szStkBedNo);   
		            recSet.setField("YD_STK_LYR_NO",       ydDaoUtils.stringPlusInt(szStkLyrNo, nLoop)) ;
		            recSet.setField("YD_STK_LYR_MTL_STAT", "D");
	                recSet.setField("STL_NO",              recTemp.getField("STL_NO"));
	                
	            	szLogMsg = "[JSP Session] " + szOperationName +   "신규위치에 정보를 UPDATE ";
	            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	            	
	                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
	                
	            	if (intRtnVal < 1)
					{
						//신규위치에 정보를 Setting 실패
	            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 실패 [ " + intRtnVal + " ]"  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	            		throw new DAOException(szLogMsg);
					}
	            	
	        		//신규위치에 정보를 Setting 실패
            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 성공 [ " + intRtnVal + " ]"  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	            	
				}
		
				// 권하위치 정보 스케줄 정보에서 변경
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
				recPara.setField("YD_DN_WO_LOC", szStkColGp+szStkBedNo);				
				recPara.setField("YD_DN_WO_LAYER", szStkLyrNo);
				
				szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보에서 변경(UPDATE)"  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
				
				if (intRtnVal < 1)
				{	
					szLogMsg = "[JSP Session] " + szOperationName +  "권하위치 스케줄 정보 변경 실패 [" +intRtnVal  + " ] " ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
				}
				
				szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보 UPDATE 성공 "  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				// 스케줄 변경 후 제원 위치정보를 맞춰준다.
        		// 2013.09.25 윤재광 필요없다 생각됨
        		/*
				szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 호출";
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
        		boolean lb_updYdCrnBed = false;        		
        		lb_updYdCrnBed = YdUtils.updYdCrnschBedData(recPara);
        		
        		if(!lb_updYdCrnBed){
        			szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 실패";
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		}
		
				szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 완료";
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				*/
        		
				//권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
				if( szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) || 
					szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_CMPL)){
					
					szLogMsg =  "[JSP Session] " + szOperationName +  "권하위치 변경후 작업지시정보를  재전송";
	        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	        		
					szLogMsg =  "[JSP Session] " + szOperationName + "   - 스케줄 진행상태 ["  + szSendYdWrkProgStat +" ]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					szYD_EQP_ID = inDto[x].getFieldString("YD_EQP_ID");
					
					szLogMsg = "[JSP Session] " + szOperationName +  "- 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					recPara.setField("JMS_TC_CD", 			"Y9YDL007");
					recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
					recPara.setField("YD_WRK_PROG_STAT" , 	szSendYdWrkProgStat);
					
					this.procY9CrnWrkOrdReq(recPara);
					
				}
        	}		
					
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}finally { }
		
		bool = true;
		
		szLogMsg = "[JSP Session] " + szOperationName  + " 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return new Boolean(bool);
	}	// end of updToPosFix
	
	/**
	 * 오퍼레이션명 : 스케줄 삭제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord PlateSchCncl(JDTORecord msgRecord)throws JDTOException  {
		
		YdEqpDao ydEqpDao 			= new YdEqpDao();
		YdCrnSchDao ydCrnSchDao  	= new YdCrnSchDao();
		
		YdStkLyrDao ydStkLyrDao 	= new YdStkLyrDao();
		YdStockDao ydStockDao 		= new YdStockDao();
		YdStkBedDao ydStkBedDao 	= new YdStkBedDao();
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao(); 
		
		JDTORecord recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord recDelPara   = JDTORecordFactory.getInstance().create();
		JDTORecord recParaStock = JDTORecordFactory.getInstance().create();
		JDTORecord recEqpPara   = JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsGetCrnSch 	= null;
		JDTORecordSet rsGetCrnMtl 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsGetBedInfo 	= JDTORecordFactory.getInstance().createRecordSet("YD");

		JDTORecord recGetCrnSch   	= null;
		JDTORecord recGetCrnMtl   	= null;
		JDTORecord recSetStkLyr 	= JDTORecordFactory.getInstance().create();
		JDTORecord recSetStkBed 	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord 	= JDTORecordFactory.getInstance().create();
		
		JDTORecord inRec 			= null;
		
		int intRtnVal 				= 0;
		int intRsGetCrnMtlSize 		= 0;
		String szStkLyrPlus 		= null;
		
		//파라미터 string
		String szV_YD_CRN_SCH_ID  	= null;
		String szV_YD_SCH_CD      	= null;
		String szV_DEL_YN         	= null;
		String szV_MODIFIER       	= null;
		String szV_YD_UP_WO_LOC   	= null;
		String szV_YD_UP_WO_LAYER 	= null;
		String szV_YD_DN_WO_LOC   	= null;
		String szV_YD_DN_WO_LAYER 	= null;
		String szCANCEL_SEND        = "N";
		String szMsg			= "";
		String szMethodName		= "PlateSchCncl";
		
		String szJMS_TC_CD 		= "";
		String szYdSchId 		= "";
		String szYdWrkProgStat 	= "";
		String szYdGp 			= "";
		String szEqpId 			= "";
		String szOperationName 	= "스케줄 삭제";
		
		String szUpdEqpstat 	= "";
		JDTORecordSet rsCrnSchInfo = null;
		String szWbookId 		= "";
		
		try{
			szMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작   ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//========크레인스케줄 삭제==========//
			
			//파라미터 null 체크
			szV_YD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			szV_YD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			szV_DEL_YN        = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_YN");
			szV_MODIFIER      = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			
			//파라미터 레코드 편집
			recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
			recPara.setField("YD_SCH_CD",     szV_YD_SCH_CD);
			recPara.setField("DEL_YN",        szV_DEL_YN);
			recPara.setField("MODIFIER",      szV_MODIFIER);
			
			//스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT ( 추가 : 스케줄 ID에 포함된 같은 작업예약정보에서만 추출)
			rsGetCrnSch = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			szMsg = "[Jsp Session : "+szOperationName+"] 스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschCrnIdOVERID*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsGetCrnSch, 5);
			
			//더 이상 삭제 작업이 없는경우
			if (intRtnVal < 1) {
				szMsg = "[Jsp Session : "+szOperationName+"] 삭제 작업이 완료되었습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				outRecord.setField("RTN_CD" , "1");	
				outRecord.setField("RTN_MSG", szMsg);
				return outRecord;
			}
			
			//레코드셋을 역순으로
			szMsg = "[Jsp Session : "+szOperationName+"] 레코드셋을 역순으로 정렬 - reverseOrder";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsGetCrnSch.reverseOrder();
			//레코드셋의 커서를 처음으로
			
			szMsg = "[Jsp Session : "+szOperationName+"] 레코드셋처음으로 이동";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			rsGetCrnSch.first();
			
			szMsg = "[Jsp Session : "+szOperationName+"] 선택된 건수 :" + rsGetCrnSch.size()  ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			
			//크레인스케줄 데이터 만큼 루프를 돌아서 크레인스케줄ID에 편성된 재료를 찾아 적치단을 CLEAR한다.
			for (int Loop_i = 0; Loop_i < rsGetCrnSch.size(); Loop_i++) {
				
				//크레인스케줄 데이터의 레코드를 추출(작업상태 체크를 위해 미리 추출)
				recGetCrnSch = JDTORecordFactory.getInstance().create();
				recGetCrnSch = rsGetCrnSch.getRecord(Loop_i);
			
				szYdSchId 			= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_CRN_SCH_ID");
				szWbookId 			= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WBOOK_ID");
				szYdWrkProgStat 	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WRK_PROG_STAT");
				szEqpId 			= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_EQP_ID");
				szV_YD_UP_WO_LOC   	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LOC");	//권상 지시위치
				szV_YD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LAYER");	//권상 지시단
				szV_YD_DN_WO_LOC   	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LOC");	//권하 지시위치
				szV_YD_DN_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LAYER");	//권하 지시단
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID",szYdSchId);
				
				szMsg = "[Jsp Session : "+szOperationName+"] 스케줄에 편성된 설비번호: " + szEqpId  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//해당 크레인스케줄ID로 크레인작업재료를 SELECT
				szMsg = "[Jsp Session : "+szOperationName+"] 해당 크레인스케줄ID로 크레인작업재료를 SELECT "  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsGetCrnMtl = JDTORecordFactory.getInstance().createRecordSet("temp");
				
				JPlateYdCrnSchDAO ydTmpDao = new JPlateYdCrnSchDAO();
				intRtnVal = ydTmpDao.getYdCrnWrkMtl(recGetCrnSch, rsGetCrnMtl);
				
				//에러리턴
				if (intRtnVal < 0) {
					
					szMsg = "[Jsp Session : "+szOperationName+"] 실패! 해당 작업재료 조회 ERROR :" +  intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);
					return outRecord;

				}
				//------------------------------------------------------------------------------------------------
				// 권상지시 상태(작업지시가 내려간경우) - 취소 처리 :YdConstant.YD_EQP_STAT_UP_WO ==1
				//------------------------------------------------------------------------------------------------
				if( szYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) ){
					
					//------------------------------------------------------------------------------------------------
					//  작업지시 취소 전문 : YD_CRN_SCH_ID,YD_WRK_PROG_STAT, MSG_GP = 'D'
					//------------------------------------------------------------------------------------------------
					szYdGp = szV_YD_SCH_CD.substring(0,1);	// 스케줄 코드에서 야드구분을 가져옴
	
					szMsg = "[Jsp Session : "+szOperationName+"] 작업지시취소전문 -  야드구분[" + szYdGp + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recDelPara = JDTORecordFactory.getInstance().create();
	        		//작업지시 전문 전송 data setup
	            	if("PACRA1".equals(szEqpId)) { 
	            		recDelPara.setField("MSG_ID"          , "YDY9L404");
					} else if("PBCRB1".equals(szEqpId)) {
						recDelPara.setField("MSG_ID"          , "YDY9L104");
					} else if("PBCRB2".equals(szEqpId)) {
						recDelPara.setField("MSG_ID"          , "YDY9L204");
					} else if("PBCRB3".equals(szEqpId)) {
						recDelPara.setField("MSG_ID"          , "YDY9L304");
					}
	            	
	            	recDelPara.setField("YD_CRN_SCH_ID",    	szYdSchId);
	            	recDelPara.setField("YD_WRK_PROG_STAT", 	szYdWrkProgStat);
	            	recDelPara.setField("YD_SCH_CD",        	szV_YD_SCH_CD);
	            	recDelPara.setField("YD_GP",            	"P");
	            	recDelPara.setField("MODIFIER", 			"YDSYSTEM");
	            	recDelPara.setField("MSG_GP", 				"D");
	            	
	            	ydDelegate.sendMsg(recDelPara);
	            		
					szCANCEL_SEND = "Y";
						
					szMsg = "[Jsp Session : "+szOperationName+"] 작업지시취소전문 송신] 취소전문" + szCANCEL_SEND;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//------------------------------------------------------------------------------------------------
				
				//------------------------------------------------------------------------------------------------
				// 권상/ 권하 위치 Log
				//------------------------------------------------------------------------------------------------
				szMsg= "권상지시위치 : "+szV_YD_UP_WO_LOC+"\n";
				szMsg+="권상 지시단   : "+szV_YD_UP_WO_LAYER+"\n";
				szMsg+="권하 지시위치: "+szV_YD_DN_WO_LOC+"\n";
				szMsg+="권하 지시단   : "+szV_YD_DN_WO_LAYER+"\n";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//------------------------------------------------------------------------------------------------
				
				//------------------------------------------------------------------------------------------------
				// 권하위치 원복
				//------------------------------------------------------------------------------------------------
				if (!( szV_YD_DN_WO_LOC.equals("")   || 
					   szV_YD_DN_WO_LAYER.equals("") || 
					   szV_YD_DN_WO_LOC.equals("XX010101") || 
					   szV_YD_DN_WO_LOC.equals("XXYY0101") )  ){
	
					//레코드의 커서를 처음으로
					szMsg = "[Jsp Session : "+szOperationName+"] 권상지시위치 " + szV_YD_DN_WO_LOC + "-" + szV_YD_DN_WO_LAYER ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szMsg = "[Jsp Session : "+szOperationName+"] 크레인 작업재료 매수  " + rsGetCrnMtl.size() ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
					rsGetCrnMtl.first();
									
					//레코드 갯수를 구한다.
					intRsGetCrnMtlSize = rsGetCrnMtl.size();
					
					//크레인스케줄의 작업 재료 만큼 루프를 돌아 권상권하 대기 정보를 초기화한다.
					for (int Loop_j = 0; Loop_j < intRsGetCrnMtlSize; Loop_j++) {
						
						//크레인작업재료 데이터의 레코드를 추출
						recGetCrnMtl = JDTORecordFactory.getInstance().create();					
						recGetCrnMtl = rsGetCrnMtl.getRecord(Loop_j);
						
						// 기존 지시위치 에 쌓여 있는 정보 Clear
						recSetStkLyr = JDTORecordFactory.getInstance().create();
						recSetStkLyr.setField("YD_STK_COL_GP",       szV_YD_DN_WO_LOC.substring(0, 6));    
						recSetStkLyr.setField("YD_STK_BED_NO",       szV_YD_DN_WO_LOC.substring(6, 8));   
						recSetStkLyr.setField("STL_NO",				 yddatautil.setDataDefault(recGetCrnMtl.getField("STL_NO"),""));
	
						szMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrWithColStockStat*/
						intRtnVal = ydStkLyrDao.updYdStklyrWithColStockStat(recSetStkLyr);
						
						//에러리턴
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 권하 지시 위치 CLEAR 실패";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						} else {
						
							szMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}					
					}
				}else{
					//권하위치가 올바르게 잡혀있지 않을때 에러처리를 원한다면 RollBack 을 시킬수 있다.				
					szMsg = "[Jsp Session : "+szOperationName+"] : 권하위치가 올바른형식이 아니라 원복시킬수 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);				
				}
					
				//------------------------------------------------------------------------------------------------
				//	권상위치 원복 
				//------------------------------------------------------------------------------------------------
				if (!( szV_YD_UP_WO_LOC.equals("") || szV_YD_UP_WO_LAYER.equals("") )){
	
					//레코드의 커서를 처음으로
					szMsg = "[Jsp Session : "+szOperationName+"] : 권상지시 정보  :" + szV_YD_UP_WO_LOC + "-" + szV_YD_UP_WO_LAYER;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					
					rsGetCrnMtl.first();
					
					//레코드 갯수를 구한다.
					intRsGetCrnMtlSize = rsGetCrnMtl.size();
					
					//------------------------------------------------------------------------------------------------
					//	크레인스케줄의 작업 재료 만큼 루프를 돌아 권상대기 정보를 초기화한다. 
					//------------------------------------------------------------------------------------------------
					for (int Loop_j = 0; Loop_j < intRsGetCrnMtlSize; Loop_j++) {
						
						recGetCrnMtl = JDTORecordFactory.getInstance().create();					
						recGetCrnMtl = rsGetCrnMtl.getRecord(Loop_j);
		
						//권상지시 적치열구분 (권상지시위치 = 적치열(6) + 적치BED(2))
						recSetStkLyr.setField("YD_STK_COL_GP",       szV_YD_UP_WO_LOC.substring(0, 6));
						recSetStkLyr.setField("YD_STK_BED_NO",       szV_YD_UP_WO_LOC.substring(6, 8));	//권상지시 적치BED번호
						
						//권상지시 적치단
						szStkLyrPlus = ydDaoUtils.stringPlusInt(szV_YD_UP_WO_LAYER, Loop_j);
						recSetStkLyr.setField("YD_STK_LYR_NO",       szStkLyrPlus);
			
						recSetStkLyr.setField("STL_NO", recGetCrnMtl.getField("STL_NO"));
						recSetStkLyr.setField("YD_STK_LYR_MTL_STAT", "C");
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 권상지시 정보  복원" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						///////////////////////////////////////////////////////
						JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("Temp1");
						intRtnVal = ydStkLyrDao.getYdStklyr(recSetStkLyr, outRecSet, 0);
						outRecSet.absolute(1);
						JDTORecord recReturnData = JDTORecordFactory.getInstance().create();
			        	recReturnData.setRecord(outRecSet.getRecord());
			     
						String sLyrStat = recReturnData.getFieldString("YD_STK_LYR_MTL_STAT"); 
						
						szMsg="("+szMethodName+") 적치단 권상 STATUS: " +sLyrStat;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						if("D".equals(sLyrStat)){
							// 권상위치가 권하대기인 경우는 SKIP
							intRtnVal = 1;
						}else{
							//적치단 테이블에 권상지시 CLEAR 업데이트 ('U' -> 'C')
							/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyr*/
							intRtnVal = ydStkLyrDao.updYdStklyr(recSetStkLyr, 0);
						}
						///////////////////////////////////////////////////////
						
						//에러리턴
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시 CLEAR 업데이트 실패" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시 CLEAR 업데이트 성공" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						//다음 스케줄 작업 재료 레코드로가기 전 작업한 스케줄 재료를 삭제 처리 해준다.
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recPara.setField("YD_CRN_SCH_ID",szYdSchId);
						recPara.setField("DEL_YN", "Y");
						recPara.setField("MODIFIER",szV_MODIFIER);
						recPara.setField("STL_NO", recGetCrnMtl.getField("STL_NO"));	
						
						intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl(recPara, 0);
						
						if(intRtnVal < 0){
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리시 ERROR" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if(intRtnVal == 0 ){
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리 대상 없음" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}else{
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리 성공" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						
						//------------------------------------------------------------------------------------------------
						// 스케줄  작업 재료 삭제시 저장품에 등록된 작업예약 ID, 스케줄 CD Clear  
						// 2013.09.25 윤재광 - 필요없어 보임
						//------------------------------------------------------------------------------------------------
						/*
						szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											
						recParaStock.setField("STL_NO",  recGetCrnMtl.getField("STL_NO"));	
						recParaStock.setField("MODIFIER",szV_MODIFIER);
						recParaStock.setField("YD_WBOOK_ID","" );
						recParaStock.setField("YD_SCH_CD","" );
						
						intRtnVal = ydStockDao.updYdStock(recParaStock, 0);
						
						if(intRtnVal < 0){
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR ERROR" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if(intRtnVal == 0 ){
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR 대상 없음" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}else{
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR 성공" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						*/
					}
					//------------------------------------------------------------------------------------------------
					//	크레인스케줄 삭제처리 
					//------------------------------------------------------------------------------------------------
					szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recPara.setField("DEL_YN", "Y");
					recPara.setField("MODIFIER", szV_MODIFIER);		
					
					intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
					
					if (intRtnVal > 0) {
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리 완료" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					} else {					
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리 실패" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					
					//------------------------------------------------------------------------------------------------
					// 설비 상태를 진행상태에 맞도록 변경 시킨다. 
					// 해당 작업 예약 ID으로 스케줄 정보 조회시에 하나도 존재 하지 않을경우에
					// - 해당 스케줄 코드로 전체 스케줄 조회시 남은스케줄 첫번째 진행상태 정보로 UPDATE 
					// - 해당 스케줄 코드로 전체 스케줄 조회시 남아있는것이 없을경우는 대기상태로 UPDAT 해준다.
					//------------------------------------------------------------------------------------------------
					recEqpPara   = JDTORecordFactory.getInstance().create();
					rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
					recEqpPara.setField("YD_WBOOK_ID", szWbookId);
					
					/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByWrkId*/
					intRtnVal = ydCrnSchDao.getYdCrnsch(recEqpPara, rsCrnSchInfo, 28);
					
					//설비 상태 UPDATE 유무 체크 FLAG 
					boolean lb_updEqpFlag  = false;
					
					if(intRtnVal < 0 ){
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 작업예약 정보에  남은 스케줄 조회시 ERROR" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						lb_updEqpFlag  = false;
						
					} else if (intRtnVal ==0){
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 작업예약 정보에  남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다." ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						//해당 스케줄 코드로 같은 편성된 스케줄이 없는지 확인한다.(다른작업예약 ID가 편성되었을경우)
						recEqpPara   = JDTORecordFactory.getInstance().create();
						rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
						recEqpPara.setField("YD_SCH_CD", szV_YD_SCH_CD);
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 스케줄 코드로 같은 편성된 스케줄이 없는지 확인한다." ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						intRtnVal = ydCrnSchDao.getYdCrnsch(recEqpPara, rsCrnSchInfo, 6);
						
						if(intRtnVal < 0 ){
							szMsg="[Jsp Session : "+szOperationName+"] :남은 스케줄코드로 스케줄 조회시 ERROR";				
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							lb_updEqpFlag  = false;
						
						}  else if (intRtnVal == 0){
							szMsg="[Jsp Session : "+szOperationName+"] :남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							szUpdEqpstat = YdConstant.YD_EQP_STAT_IDLE;
							lb_updEqpFlag  = true;
							
						} else{
							szMsg="[Jsp Session : "+szOperationName+"] :해당 스케줄 코드에 스케줄 정보가 남아 있어 설비정보는 남은스케줄 첫번째 진행상태 정보로 UPDATE 합니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							recEqpPara   = JDTORecordFactory.getInstance().create(); 
							rsCrnSchInfo.first();
							recEqpPara 	= rsCrnSchInfo.getRecord();
							szUpdEqpstat = ydDaoUtils.paraRecChkNull(recEqpPara, "YD_WRK_PROG_STAT");
							lb_updEqpFlag  = true;
						}
					
					} else{
						
						szMsg="[Jsp Session : "+szOperationName+"] 해당 작업예약 정보에 스케줄 정보가 남아 있어 설비정보는 UPDATE 하지 않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						lb_updEqpFlag  = false;
					}
					
                    if(lb_updEqpFlag){
						
						//설비정보 업데이트 하기전에 설비상태 체크해준다.
						JDTORecord recInfo   = JDTORecordFactory.getInstance().create();
				
						String szRtnMsg = YdCommonUtils.checkCrnStat(szEqpId, recInfo);
						
						if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
							/* 임시로 막음. 2013.10.28 윤재광 
							if( ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals("1")
							 || ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals("2")
							 || ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals("3")
							 || ydDaoUtils.paraRecChkNull(recInfo, "YD_EQP_STAT").equals("4")
							){
	
								szMsg="설비상태가 대기 상태가 아닌 작업상태이기때문에 값을 변경 할수 없습니다.";				
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
							}else{
							*/	
								recEqpPara   = JDTORecordFactory.getInstance().create();
								recEqpPara.setField("YD_EQP_ID", szEqpId);
								recEqpPara.setField("YD_EQP_STAT", szUpdEqpstat);
								recEqpPara.setField("MODIFIER",szV_MODIFIER);
								
								szMsg="++++++++++ 해당 스케줄 크레인("+ szEqpId +") 설비상태 [" + szUpdEqpstat +"]로 변경 ++++++++++++++++++";				
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								intRtnVal = ydEqpDao.updYdEqp(recEqpPara, 0);
								
								if(intRtnVal < 0 ){
									szMsg=szEqpId +"설비정보를 변경 실패 하였습니다.";	
									ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.ERROR);
									
								}
							//}
						}
					}
				}
			}
			
			//작업 예약 /재료 삭제
			//크레인 작업 재지시를 위하여  설비 아이디 , 스케줄 코드를 넘겨준다.
			outRecord.setField("YD_EQP_ID"		, szEqpId);
			outRecord.setField("YD_SCH_CD"		, szV_YD_SCH_CD);
			outRecord.setField("MODIFIER"		, szV_MODIFIER);
			outRecord.setField("YD_CRN_SCH_ID"	, szV_YD_CRN_SCH_ID);
			outRecord.setField("MODIFIER"		, szV_MODIFIER);
			outRecord.setField("DEL_YN"			, szV_DEL_YN);
			outRecord.setField("CANCEL_SEND"	, szCANCEL_SEND);
			
			outRecord.setField("RTN_CD" 	, "1");	
			outRecord.setField("RTN_MSG"	, szMsg);
			return outRecord;
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}finally { }
		
	}// end of schCncl()
	
	
	/**
	 * 오퍼레이션명 : 작업예약 삭제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (YD_CRN_SCH_ID)
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord PlateDelWBook(JDTORecord msgRecord) throws JDTOException  {
		
		
		YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
		YdWrkbookDao  ydWrkbookDao      = new  YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new  YdWrkbookMtlDao();
		YdDelegate ydDelegate 			= new YdDelegate();
		YdCarSchDao  ydCarSchDao 		= new YdCarSchDao();
		YdTcarSchDao  ydTcarSchDao 		= new YdTcarSchDao();
		
		JDTORecord outRecord 	= JDTORecordFactory.getInstance().create();
		
		//리턴레코드셋
		JDTORecordSet rsRtnVal 	= JDTORecordFactory.getInstance().createRecordSet("temRs");
		JDTORecord recPara  	= JDTORecordFactory.getInstance().create();
		JDTORecord recCheck  	= JDTORecordFactory.getInstance().create();
		JDTORecord inRec  		= JDTORecordFactory.getInstance().create();
		
		JDTORecord recTemp  	= JDTORecordFactory.getInstance().create();
		
		//파라미터 스크링 변수
		String szV_YD_CRN_SCH_ID   = null;	
		String szV_DEL_YN          = null;
		String szV_MODIFIER        = null;
		String szV_YD_WBOOK_ID     = null;
		String szOperationName	   = "작업예약 삭제";
		
		
		String szSchCd = null;
		String szCarGp = null;
		String szULGp = null;
		
		//리턴값
		int intRtnVal = 0;
		
		//체크 값
		String szC_YD_WRK_PROG_STAT= null;
		
		String szMsg="";
		String szMethodName="PlateDelWBook";
		String szStlNo = "";
		
		
		// 크레인 작업 지시 EJB Call 시 필요한 변수
		String szEjbConName = "";
		String szLogMsg = "";
		String szJMS_TC_CD = "";
		EJBConnector ejbConn = null;
		String szYdGp = "";
		JDTORecord recDelPara = null;
		String szEqpId = "";
		String szV_YD_SCH_CD = "";
		String szYD_USER_ID = "";
		
		
		szMsg="작업예약 삭제 처리 기능 시작";
	    ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
	 
		try{		 
		
			szYD_USER_ID = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			
			if (ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID").equals("")) {		
				
				szMsg="스케줄 ID 정보가 없어서 작업예약 삭제처리를 하지 못하였습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);
				return outRecord;
			}
			
			
			//크레인스케줄 ID
			szV_YD_CRN_SCH_ID  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			szSchCd            = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
					
			//파라미터 레코드 setting
			recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
			
			rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 0);
			
			if (intRtnVal < 1 ){
				szMsg="해당크레인 스케줄이 존재하지않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);
				return outRecord;
			}
			
			rsRtnVal.first();
			recCheck = rsRtnVal.getRecord();
			
			szV_YD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recCheck, "YD_WBOOK_ID");
			
			if (szV_YD_WBOOK_ID.equals("")){
		
				szMsg="해당크레인 스케줄에 작업예약 정보가 존재하지않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);
				return outRecord;
			}
			
			rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
			
			if (intRtnVal < 0){
				szMsg = YdConstant.RETN_CD_FAILURE;
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);
				return outRecord;
			} else  if (intRtnVal > 0){
				szMsg = "스케줄 정보가 남아 있습니다.";
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);
				return outRecord;
			}
			
			String szRtnMsg = "";
			//------------------------------------------------------------------------------------------------
			//	작업예약/재료 삭제
			//------------------------------------------------------------------------------------------------
			szRtnMsg = JPlateYdCommonUtils.delYdWrkbookNMtl(szV_YD_WBOOK_ID, szYD_USER_ID);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]삭제 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]삭제 실패 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			//------------------------------------------------------------------------------------------------
	
			szEqpId 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 
			szV_YD_SCH_CD 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			
			//설비 ID 정보와 스케줄 코드가 들어왔을때만 실행한다.
			if (   szEqpId.equals("")  || szV_YD_SCH_CD.equals("")) {
				
				szMsg = "[JSP Session : "+szOperationName+"] 설비 ID 정보" + szEqpId + "스케줄 코드 [" + szV_YD_SCH_CD + "]" 
				+ "중 누락된 정보가 발생하여 해당 크레인 작업지시를 호출하지 않고 마칩니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				outRecord.setField("RTN_CD" , "1");	
				outRecord.setField("RTN_SND", "N");
				outRecord.setField("RTN_MSG", "성공");
				return outRecord;
			}
			
			szMsg = "[JSP Session : "+szOperationName+"] 설비 ID 정보" + szEqpId + "스케줄 코드 [" + szV_YD_SCH_CD + "]" ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			outRecord.setField("YD_EQP_ID"				, szEqpId            );					   
			outRecord.setField("YD_WRK_PROG_STAT"		, YdConstant.YD_EQP_STAT_IDLE );
			outRecord.setField("YD_SCH_CD"				, szV_YD_SCH_CD );  
			outRecord.setField("RTN_CD" , "1");	
			outRecord.setField("RTN_SND", "Y");
			outRecord.setField("RTN_MSG", "성공");
			
			return outRecord;
			
		} catch (Exception e) {
			szMsg = "[JSP Session : "+szOperationName+"] 크레인 작업지시 정보 호출가 발생하였습니다"; 				
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
		}

		outRecord.setField("RTN_CD" , "1");	
		outRecord.setField("RTN_MSG", "성공");
		return outRecord;
	
		
	
	}// end of PlateDelWBook()
	
	/**
	 * 오퍼레이션명 :
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public boolean RequiresUpdYdEqp(JDTORecord recEqpPara){

		boolean isSuccess = false;
		int iReq = -1;
		int intRtnVal =0;
		String stkQueryId ="";
		YdEqpDao ydEqpDao = new YdEqpDao();
		try{

			intRtnVal = ydEqpDao.updYdEqp(recEqpPara, 0);

			if(intRtnVal > 0 ){
			isSuccess = true;
			}

	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	/**
	 *  작업예약등록(이적)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insMvWBookId(JDTORecord[] inDto) throws DAOException {
		
		String SZ_SESSION_NAME = PlateReviseSeEJB.class.getName();
		
    	JPlateYdStkLyrDAO   ydStkLyrDao = new JPlateYdStkLyrDAO();
    	
    	JPlateYdUtils    	jydUtils 	= new JPlateYdUtils();
        JPlateYdDaoUtils 	jydDaoUtils 	= new JPlateYdDaoUtils();
        
		JDTORecordSet 	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();

		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMethodName		= "insMvWBookId";
		String 	szOperationName 	= "작업예약등록(이적)";
		String 	szLogMsg 			= "";
		String	szYdMainWrkGp		= "";
		String	szYdStkColGp 		= "";
		String	szYdTcGp			= "";
		String	szYdGp				= "";
		String	szYdBayGp			= "";

		int		intRtnVal			= 0;

		try {

			szLogMsg = "JSP-SESSION ["+szOperationName+"] 시작 ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			//내부 Process 연결
			EJBConnector ejbConn = new EJBConnector("default", this);

			for (int ii=0; ii<inDto.length; ii++) {

				//---------------------------------------------------------------------------------------------
				// 이적 대상재 등록시 TO위치나 대차에 권상예약 정보 존재시 오류 처리
				// --> 대차를 선택하거나 TO위치 지정했을때 (동+스판+열까지 입력)
				//---------------------------------------------------------------------------------------------
				szYdGp		= jydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP");
				szYdBayGp	= jydDaoUtils.paraRecChkNull(inDto[ii], "YD_BAY_GP");
				szYdStkColGp= jydDaoUtils.paraRecChkNull(inDto[ii], "YD_TO_LOC_GUIDE_GP");
				szYdTcGp	= jydDaoUtils.paraRecChkNull(inDto[ii], "YD_TC_GP");

				if (!"".equals(szYdTcGp) || szYdStkColGp.length() >= 6) {

					if (!"".equals(szYdTcGp)) {
						szYdStkColGp = szYdGp + szYdBayGp + jydUtils.substr(szYdTcGp, 2, 4);
					}
					rsResult 	= JDTORecordFactory.getInstance().createRecordSet("ydTemp");
					recPara 	= JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP", 			szYdStkColGp);
					recPara.setField("YD_STK_LYR_MTL_STAT1", 	"U");
					recPara.setField("YD_STK_LYR_MTL_STAT2", 	"U");
					recPara.setField("YD_STK_LYR_MTL_STAT3", 	"U");

					intRtnVal 	= ydStkLyrDao.getByLocMtlStat(recPara, rsResult);
					if (intRtnVal > 0) {
						szRtnMsg = "TO위치[" + szYdStkColGp + "]에 권상예약 정보가 존재하여 작업불가!";
						szLogMsg = "[" + szOperationName + "]" + szRtnMsg;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
			}

			for (int ii=0; ii<inDto.length; ii++) {
				szYdMainWrkGp = jydDaoUtils.paraRecChkNull(inDto[ii], "YD_MAIN_WRK_GP");		// 1:이적, 2:북인, 3:보수장이적[저장위치수정]

				recPara = JDTORecordFactory.getInstance().create();
				// 해당 스케줄 기준정보에 야드 스케줄 금지유무를 "Y" Setting 한다.
				recPara.setField("YD_GP", 	          	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_GP"));
				recPara.setField("YD_BAY_GP", 	      	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_BAY_GP"));
				recPara.setField("YD_SPAN_GP", 	      	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_SPAN_GP"));
				recPara.setField("YD_AIM_RT_GP", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_RT_GP"));
				//  주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, M:보수장이적
				recPara.setField("YD_MAIN_WRK_GP", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_MAIN_WRK_GP"));
				recPara.setField("YD_BS_MV_GP",			jydDaoUtils.paraRecChkNull(inDto[ii], "YD_BS_MV_GP"));			// BS:보수장, 1:#1보수대기, 2:#2보수대기, 3:충당대기
				recPara.setField("YD_TO_LOC_GUIDE_GP",	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_TO_LOC_GUIDE_GP"));
				recPara.setField("YD_AIM_YD_GP", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_YD_GP"));
				recPara.setField("YD_AIM_BAY_GP", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_BAY_GP"));
				recPara.setField("YD_AIM_SPAN_GP", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_SPAN_GP"));
				recPara.setField("YD_AIM_COL_GP", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_COL_GP"));
				recPara.setField("YD_AIM_BED_NO", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_AIM_BED_NO"));
				recPara.setField("YD_STK_COL_GP", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_COL_GPS"));
				recPara.setField("YD_STK_BED_NO", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_STK_BED_NOS"));
				recPara.setField("YD_TO_LOC_GUIDE", 	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_TO_LOC_GUIDE"));
				recPara.setField("YD_TC_GP", 	  		jydDaoUtils.paraRecChkNull(inDto[ii], "YD_TC_GP"));

				//---------------------------------------------------------------------------------------------
				//	후판정정의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
				//---------------------------------------------------------------------------------------------
				recPara.setField("YD_EQP_WRK_SH", 	  	jydDaoUtils.paraRecChkNull(inDto[ii], "YD_EQP_WRK_SH"));

				//---------------------------------------------------------------------------------------------
				//	이송대기인 경우 착지개소코드도 파라미터로 전달됨
				//---------------------------------------------------------------------------------------------
				recPara.setField("ARR_WLOC_CD", 	    jydDaoUtils.paraRecChkNull(inDto[ii], "ARR_WLOC_CD"));
				//---------------------------------------------------------------------------------------------

				//---------------------------------------------------------------------------------------------
				//	작업재료리스트와 JMS_TC_CD
				//---------------------------------------------------------------------------------------------
				recPara.setField("STL_LIST", 	        jydDaoUtils.paraRecChkNull(inDto[ii], "STL_LIST"));
				recPara.setField("JMS_TC_CD", 	        jydDaoUtils.paraRecChkNull(inDto[ii], "JMS_TC_CD"));
				//---------------------------------------------------------------------------------------------
				recPara.setField("YD_USER_ID", 	        jydDaoUtils.paraRecModifier(inDto[ii]));

				szRtnMsg = (String)ejbConn.trx("PlateReviseSeEJB", "procPrepLotCompByCapa", recPara);
				
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		szLogMsg = "JSP-SESSION ["+szOperationName+"] 끝  .. 결과:" + szRtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return szRtnMsg;
	} // end of insMvWBookId
	
	/**
	 * 오퍼레이션명 : 준비스케줄 LOT편성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public String procPrepLotCompByCapa(JDTORecord msgRecord) throws DAOException {
		
		String SZ_SESSION_NAME = PlateReviseSeEJB.class.getName();
		// DAO 선언
		JPlateYdWrkbookDAO 		ydWrkbookDao 	= new JPlateYdWrkbookDAO();				// 작업예약 DAO
		JPlateYdWrkbookMtlDAO 	ydWrkbookMtlDao = new JPlateYdWrkbookMtlDAO();			// 작업예약 재료 DAO
		JPlateYdStockDAO 		ydStockDao 		= new JPlateYdStockDAO();				// 저장품DAO
		JPlateYdSchRuleDAO		ydSchRuleDao	= new JPlateYdSchRuleDAO();				// 스케줄기준DAO
		JPlateYdEqpDAO			ydEqpDao		= new JPlateYdEqpDAO();					// 야드설비DAO
		
		JPlateYdUtils    	jydUtils 		= new JPlateYdUtils();
        JPlateYdDaoUtils 	jydDaoUtils 	= new JPlateYdDaoUtils();
        
		//레코드 선언
		JDTORecord 		recPara     = null;
		JDTORecord 		recOutPara  = null;
		JDTORecord 		recSchPara  = null;
		JDTORecord 		recResult 	= null;
		JDTORecord 		recTemp 	= null;
		//레코드셋 선언
		JDTORecordSet 	rsResult 	= null;
		JDTORecordSet 	rsTemp 		= null;
		JDTORecordSet 	rsSchRule 	= JDTORecordFactory.getInstance().createRecordSet("");

		//리턴값(boolean)
		int 	intRtnVal 			= 0;

		String 	szMsg           	= "";
		String 	szMethodName    	= "procPrepLotCompByCapa";
		String 	szOperationName 	= "준비스케줄 LOT 편성";

		int 	intLotGpSh      	= 0;								//Lot 편성 재료 매수
		String 	szYD_GP				= null;								//야드구분
		String 	szYD_BAY_GP			= null;								//동구분
		String 	szYD_AIM_YD_GP		= null;								//목표야드구분
		String 	szYD_AIM_BAY_GP		= null;								//목표동구분
		String 	szYD_AIM_SPAN_GP	= null;								//목표스판구분
		String 	szYD_AIM_COL_GP		= null;								//목표적치열구분
		String 	szYD_AIM_BED_NO		= null;								//목표적치BED구분
		String 	szYD_MAIN_WRK_GP 	= null;								//주작업이적구분
		String 	szYD_TO_LOC_GUIDE_GP= null;								//TO위치가이드구분
		String 	szYD_SCH_CD         = "";								//스케줄코드
		String 	szPREV_YD_SCH_CD    = "";								//스케줄코드
		String 	szSTL_NO            = null;								//재료번호
		String[] arrYD_STK_COL_GP 	= null;								//적치열배열
		String[] arrYD_STK_BED_NO 	= null;								//적치베드 배열
		String 	szTCAR 				= "";								//대차
		String 	szPREV_TCAR 		= "";								//대차
		String 	szYD_STK_COL_GP 	= "";								//적치열구분
		String 	szYD_STK_COL_GP_CMP = "XX01";							//이전 적치열구분 - 비교를 위해서 임의의 값을 설정
		String 	szYD_TO_LOC_DCSN_MTD= "";								//야드 TO위치결정방법
		String 	szYD_TO_LOC_GUIDE 	= "";								//야드TO위치가이드
		String 	szSTL_LIST 			= "";								//야드TO위치가이드
		String	szREGISTER			= "";								//등록자
		String	szYD_CURR_BAY_GP	= "";								//대차현재동
		String	szYD_EQP_NAME		= "";								//대차설비명

		double 	dblCurrWidth		= 0;								//재료의 현재 폭
		long 	lngCurrWt			= 0;								//재료의 현재 중량
		long 	lngSumWt 			= 0;								//누적중량
		int 	intMtlSh			= 0;								//크레인작업가능매수
		double 	dblMaxWidth			= 0;								//크레인작업가능 폭
		boolean bIsInsideMv			= true;								//동내이적인 지 동간이적인 지 판단하는 변수
		String 	szYD_TC_GP			= null;
		String 	szYD_EQP_WRK_SH		= null;								//작업매수
		int 	intYD_EQP_WRK_SH	= -1;								//작업매수
		String 	szARR_WLOC_CD		= null;
		String	szYD_WBOOK_ID		= null;								//작업예약ID
		String	szYD_WRK_CRN		= null;								//크레인설비ID
		boolean bookInFlag 			= false;							//BOOK-IN 여부
		String 	ydWrkCrnPrior 		= "1"; 								//야드작업크레인우선순위
		int		insWrkMtlCnt 		= 0;
		int		iWBookInsCnt		= 0;								//작업예약등록 건수
		int		iCrnSchCnt			= 0;								//크레인스케쥴 호출 건수

		StringBuffer sbARR_WBOOK_ID = new StringBuffer();

		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;	//리턴메세지정의

		try {
			szMsg    = "["+szOperationName+"] ----------- START :: " + msgRecord.toString();
			jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//----------------------------
			// 1. 입력 파라미터 정합성 체크
			//----------------------------
			/*
			 * 야드구분, 동구분, 스판(선택), 목표행선구분, 목표동, 주작업이적구분, TO위치가이드구분
			 */
			szYD_GP = jydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if ("".equals(szYD_GP)) {

				szRtnMsg = "[전문 이상] 야드구분이 없습니다.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;

			}
			//동구분
			szYD_BAY_GP = jydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if ("".equals(szYD_BAY_GP)) {
				szRtnMsg = "[전문 이상] 동구분이 없습니다.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//적치열구분
			arrYD_STK_COL_GP = jydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP").split(";");
			if (arrYD_STK_COL_GP == null || arrYD_STK_COL_GP.length == 0) {
				szRtnMsg = "[전문 이상] 적치열구분이 없습니다.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//적치베드구분
			arrYD_STK_BED_NO = jydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO").split(";");
			if (arrYD_STK_BED_NO == null || arrYD_STK_BED_NO.length == 0) {
				szRtnMsg = "[전문 이상] 적치베드번호가 없습니다.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			//주작업이적구분
			szYD_MAIN_WRK_GP  = jydDaoUtils.paraRecChkNull(msgRecord, "YD_MAIN_WRK_GP");
			if ("".equals(szYD_MAIN_WRK_GP)) {
				// 1:이적 , 	2:RT BOOK-IN ,  3:GAS장 보급 , 4:보수장 보급, 5:TOD보급
				//			6:RT BOOK-OUT , 7:GAS장 추출 , 8:보수장 추출, 9:TOD추출
				//          A:이송상차,      	B:이송하차
				szRtnMsg = "[전문 이상] 주작업이적구분이 없습니다.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}
			//TO위치가이드구분
			szYD_TO_LOC_GUIDE_GP = jydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE_GP");
			if ("".equals(szYD_TO_LOC_GUIDE_GP)) {
				szRtnMsg = "[전문 이상] TO위치가이드구분이 없습니다.";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;

			} else {

				//목표야드구분
				szYD_AIM_YD_GP = jydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
				if ("".equals(szYD_AIM_YD_GP)) {
					szRtnMsg = "[전문 이상] 목표야드구분이 없습니다.";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				//목표동구분
				szYD_AIM_BAY_GP = jydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
				if ("".equals(szYD_AIM_BAY_GP)) {
					szRtnMsg = "[전문 이상] 목표동구분이 없습니다.";
					szMsg    = "["+szOperationName+"] " + szRtnMsg;
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}

				// To위치 Guide 일때
				if ("Y".equals(szYD_TO_LOC_GUIDE_GP)) {
					szYD_AIM_SPAN_GP = jydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_SPAN_GP");		//목표스판구분
					szYD_AIM_COL_GP  = jydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_COL_GP");		//목표적치열구분
					szYD_AIM_BED_NO  = jydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BED_NO");		//목표적치BED구분
				}
			}

			szREGISTER = jydDaoUtils.paraRecModifier(msgRecord);										//등록자, 수정자

			// BOOK-IN, GAS장보급, 보수장보급일경우  작업예약 ID를 건수만큼 생성
			if (JPlateYdConst.YD_MAIN_WRK_GP_RT_IN.equals(szYD_MAIN_WRK_GP) ||		// RT	Book-In
				JPlateYdConst.YD_MAIN_WRK_GP_GAS_IN.equals(szYD_MAIN_WRK_GP) ||		// GAS장	Book-In (보급)
				JPlateYdConst.YD_MAIN_WRK_GP_BS_IN.equals(szYD_MAIN_WRK_GP) ) {		// 보수장	Book-In (보급)

				bookInFlag = true;
			}

			//---------------------------------------------------------------------------------------------
			//	준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
			//---------------------------------------------------------------------------------------------
			szYD_EQP_WRK_SH = jydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_SH");
			if (!"".equals(szYD_EQP_WRK_SH)) {
				intYD_EQP_WRK_SH = Integer.parseInt(szYD_EQP_WRK_SH);
				szMsg = "사용자가 지정한 작업매수 : " + intYD_EQP_WRK_SH;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			szARR_WLOC_CD 	= jydDaoUtils.paraRecChkNull(msgRecord, "ARR_WLOC_CD");		//착지개소코드
			szYD_TC_GP 		= jydDaoUtils.paraRecChkNull(msgRecord, "YD_TC_GP");		//대차선택
			szYD_AIM_YD_GP 	= szYD_GP;													//목표야드구분
			szSTL_LIST 		= jydDaoUtils.paraRecChkNull(msgRecord, "STL_LIST");		//작업재료리스트

			// 2013.09.06 대차 작업일경우 대차의 현재동 체크 : 현재동에 대차가 없으면 오류로 처리
			if (!"".equals(szYD_TC_GP)) {
				//2019.09.25 윤재광 - 대차작업 삭제
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");

			// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차
			if (JPlateYdConst.YD_MAIN_WRK_GP_MV.equals(szYD_MAIN_WRK_GP)) {				// 이적인 경우

				//작업재료LIST 검색
				szRtnMsg = chkPrepLotGpStlList(szSTL_LIST, szARR_WLOC_CD, rsResult);

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			} else if (JPlateYdConst.YD_MAIN_WRK_GP_RT_IN.equals(szYD_MAIN_WRK_GP)
					|| JPlateYdConst.YD_MAIN_WRK_GP_RT_OUT.equals(szYD_MAIN_WRK_GP)) {		// BOOK-IN/OUT인 경우

				//작업재료LIST 검색
				szRtnMsg = chkPrepLotGpStlList(szSTL_LIST, szARR_WLOC_CD, rsResult);

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			} else {																		// 보급/추출인 경우

				//작업재료LIST 검색
				szRtnMsg = chkPrepLotGpStlList(szSTL_LIST, szARR_WLOC_CD, rsResult);

				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
					return szRtnMsg;
				}
			}

			intLotGpSh = rsResult.size();
			rsResult.first();

			Vector rsGroup = new Vector();

			//동일한 스판별로 대상재를 편성한다.
			//보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호  데이터를 세팅한다.
			for (int ii = 1; ii<=intLotGpSh; ii++) {

				//레코드 추출
				recPara = rsResult.getRecord();

				szSTL_NO 		= jydDaoUtils.paraRecChkNull(recPara, 		"STL_NO");			// 재료번호
				lngCurrWt 		= jydDaoUtils.paraRecChkNullLong(recPara, 	"YD_MTL_WT");		// 야드재료중량
				dblCurrWidth 	= jydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");		// 야드재료폭
				szYD_STK_COL_GP	= jydDaoUtils.paraRecChkNull(recPara, 		"YD_STK_COL_GP");	// 적치열구분

				szMsg = "작업재료확인 : 현재Count[" + ii + "] 현재 재료번호[" + szSTL_NO + "]";
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szRtnMsg = this.chkExistSchdule(recPara);
				if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {

					szMsg = "재료번호[" + szSTL_NO + "] 로 작업예약/크레인스케쥴 존재여부 체크 .. 실패 >>>>" + szRtnMsg;
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					return szRtnMsg;
				}

				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 스케줄코드, 대차설비 ID 구하기
				 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				szYD_TO_LOC_DCSN_MTD 	= "";		//야드 TO위치결정방법
				szYD_TO_LOC_GUIDE 		= "";		//야드TO위치가이드
				szTCAR 					= "";		//대차

				//TO위치GUIDE가 존재하고 이고 목표동이 다른 경우에는 대차상차스케줄을 생성하고 대차 배정
				if ("Y".equals(szYD_TO_LOC_GUIDE_GP)) {				//TO위치GUIDE 이적인 경우
					szYD_TO_LOC_DCSN_MTD = "F";
					szYD_TO_LOC_GUIDE	 = szYD_AIM_YD_GP + szYD_AIM_BAY_GP + szYD_AIM_SPAN_GP + szYD_AIM_COL_GP + szYD_AIM_BED_NO;

					szMsg = "TO위치GUIDE 이적인 경우 szYD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if (szYD_BAY_GP.equals(szYD_AIM_BAY_GP))	{	//목표동이 같은 경우 - 동내이적 스케줄
						bIsInsideMv = true;
						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, "");
						szMsg = ">> 목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					} else {											//목표동이 다른 경우 - 대차상차스케줄
						bIsInsideMv = false;
						szTCAR = szYD_TC_GP;
						if ("".equals(szTCAR)) {
							String[] retValue = JPlateYdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
							szTCAR = retValue[1];
						}
						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR);

						szMsg = ">>>> 목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}
				} else {			//위치검색기준으로 이적인 경우
					szYD_TO_LOC_GUIDE 	= szYD_AIM_YD_GP + szYD_AIM_BAY_GP;

					szMsg = "위치검색기준으로 이적인 경우 >>>> szYD_TO_LOC_GUIDE :: " + szYD_TO_LOC_GUIDE;
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					if (szYD_BAY_GP.equals(szYD_AIM_BAY_GP)) {		//목표동이 같은 경우 - 동내이적 스케줄
						bIsInsideMv = true;
						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, "");
						szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					} else {											//목표동이 다른 경우 - 대차상차스케줄
						bIsInsideMv = false;
						szTCAR = szYD_TC_GP;
						if ("".equals(szTCAR)) {
							String[] retValue = JPlateYdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
							szTCAR = retValue[1];
						}
						szYD_SCH_CD = getSchCdByWrkGp(szYD_MAIN_WRK_GP, szYD_STK_COL_GP, szYD_TO_LOC_GUIDE, szTCAR);

						szMsg = "위치검색기준 .. 목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					}
				}

				szMsg = "["+szOperationName+"] >>>>> TO위치결정방법[" + szYD_TO_LOC_DCSN_MTD + "], TO위치GUIDE[" + szYD_TO_LOC_GUIDE + "]";
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				szMsg = "["+szOperationName+"] >>>>> 재료번호 [" + szSTL_NO + "] : 야드재료중량[" + lngCurrWt + "], 야드재료폭[" + dblCurrWidth + "], 적치열구분[" + szYD_STK_COL_GP + "], 목표동[" + szYD_AIM_BAY_GP + "]";
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				/*
				 * 스케줄기준과 크레인 할당
				 */
				if (!szPREV_YD_SCH_CD.equals(szYD_SCH_CD)) {
					szMsg = "["+szOperationName+"] 스케줄코드비교 >>>>> YD_SCH_CD[" + szYD_SCH_CD + "], PREV_YD_SCH_CD[" + szPREV_YD_SCH_CD + "]";
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					recResult = JDTORecordFactory.getInstance().create();
					szRtnMsg = JPlateYdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult);
					if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
						return szRtnMsg;
					}
					// 크레인ID
					szYD_WRK_CRN = jydDaoUtils.paraRecChkNull(recResult, "YD_WRK_CRN");
				}

				//대차적치가능 중량 구하기
				if (!szPREV_TCAR.equals(szTCAR)) {
					szMsg = "["+szOperationName+"] 대차설비비교 >>>>> TCAR[" + szTCAR + "], PREV_TCAR[" + szPREV_TCAR + "]";
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}

				//동일한 스판이 아닌 경우
				if (!szYD_STK_COL_GP.substring(0, 4).equals(szYD_STK_COL_GP_CMP.substring(0, 4))) {
					rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
					rsGroup.add(rsTemp);
					dblMaxWidth = dblCurrWidth;
					lngSumWt = lngCurrWt;
					intMtlSh = 1;
					szMsg = "["+szOperationName+"] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					szMsg = "["+szOperationName+"] 스판비교 >>>>> YD_STK_COL_GP[" + szYD_STK_COL_GP + "], PREV_YD_STK_COL_GP[" + szYD_STK_COL_GP_CMP + "]";
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} else if (bIsInsideMv) {		//동내이적

					if (bookInFlag) {
						szMsg = "["+szOperationName+"] 동내이적 - BOOK-IN : ";
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
						rsGroup.add(rsTemp);

					} else {
						/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						 * 업무기준 : 주작업인 경우
						 * 		1. 작업재료를 동내이적 대상재는 크레인 작업능력만큼 LOT편성
						 * 		2. 작업재료를 동간이적 대상재는 대차 작업능력만큼 LOT편성
						 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
						szMsg = "["+szOperationName+"] 동내이적 - bIsInsideMv : " + bIsInsideMv;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						//주작업인 경우에만.
						lngSumWt += lngCurrWt;
						intMtlSh++;
						szMsg = "["+szOperationName+"] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						//크레인작업가능능력 체크
						intRtnVal = JPlateYdCommonUtils.chkGetCrnspec(dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recResult);
						szMsg = "["+szOperationName+"] 동내이적(크레인작업가능능력 체크 : intRtnVal >> "+ intRtnVal + ")";
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					// 2013.07.22 김현우 재료번 1건의 작업 예약 ID생성하도록 보완 ..
					//
					//	if (intRtnVal == -1 || intRtnVal == -2 || intRtnVal == -3) {
							rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
							rsGroup.add(rsTemp);
							dblMaxWidth = dblCurrWidth;
							lngSumWt = lngCurrWt;
							intMtlSh = 1;
							szMsg = "["+szOperationName+"] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
							jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					//		szMsg = "["+szOperationName+"] 크레인작업가능능력 초과";
					//		jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					//	}

					}

					if (dblMaxWidth < dblCurrWidth) {
						dblMaxWidth = dblCurrWidth;
					}

				} else {							//동간이적

					szMsg = "["+szOperationName+"] 동간이적(대차적치능력 - 크레인작업가능능력으로 체크) - bIsInsideMv : " + bIsInsideMv;
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					lngSumWt += lngCurrWt;
					intMtlSh++;
					szMsg = "["+szOperationName+"] 동간이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					//크레인작업가능능력 체크
					intRtnVal = JPlateYdCommonUtils.chkGetCrnspec(dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recResult);
					szMsg = "["+szOperationName+"] 동간이적(대차적치능력 - 크레인작업가능능력으로 체크 : intRtnVal "+ intRtnVal + ")";
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 2013.07.22 김현우 재료번 1건의 작업 예약 ID생성하도록 보완 ..
				//
				//	if (intRtnVal == -1 || intRtnVal == -2 || intRtnVal == -3) {

						rsTemp = JDTORecordFactory.getInstance().createRecordSet("yd");
						rsGroup.add(rsTemp);
						dblMaxWidth = dblCurrWidth;
						lngSumWt = lngCurrWt;
						intMtlSh = 1;
						szMsg = "["+szOperationName+"] 동간이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				//		szMsg = "["+szOperationName+"(대차적치능력)] 크레인작업가능능력으로 체크 후 초과";
				//		jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				//
				//	}

					if (dblMaxWidth < dblCurrWidth) {
						dblMaxWidth = dblCurrWidth;
					}
				}

				rsTemp.addRecord(recPara);
				szYD_STK_COL_GP_CMP = szYD_STK_COL_GP;

				//다음 레코드로
				rsResult.next();

				szPREV_YD_SCH_CD 	= szYD_SCH_CD;			//스케줄코드
				szPREV_TCAR 		= szTCAR;				//대차설비ID

				//---------------------------------------------------------------------------------------------
				//	후판정정의 준비스케줄 등록 시 지정한 작업매수만큼 처리된 후에는 루프 종료
				//---------------------------------------------------------------------------------------------
				if (!"".equals(szYD_EQP_WRK_SH)) {
					if (ii > intYD_EQP_WRK_SH)	{
						szMsg = "사용자가 지정한 작업매수[" + intYD_EQP_WRK_SH + "]와 같으므로 루프 종료 - 반복변수값["+ii+"]";
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						break;
					}
				}
			} // end for

			szMsg = "["+szOperationName+"] 대상재 작업예약 그룹 갯수 : " + rsGroup.size();
			jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//----------------------------
			// 2. 작업예약등록
			//----------------------------
			szMsg = "["+szOperationName+"] 작업예약등록 시작 ";
			jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			for(int ii = 0 ; ii < rsGroup.size(); ii++) {
				rsTemp = (JDTORecordSet)rsGroup.get(ii);

				for(int jj = 0; jj < rsTemp.size(); jj++) {
					rsTemp.absolute(jj + 1);
					recOutPara = rsTemp.getRecord();

					if (jj==0 || bookInFlag) {							// 첫번째거나  Book-In일 경우는 작업예약 ID를 매번 생성
			//		if ((ii==0 && jj==0) || bookInFlag) {				// 첫번째거나  Book-In일 경우는 작업예약 ID를 매번 생성
						//----------------------------
						// 2.1. 작업예약ID SELECT
						//----------------------------
						szYD_WBOOK_ID = ydWrkbookDao.getSeqId();

						szMsg = "["+szOperationName+"] >>>> 작업예약ID SELECT >>>> (" + ii + "," + jj + ")" + szYD_WBOOK_ID;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						if (iWBookInsCnt > 0) {
							sbARR_WBOOK_ID.append(";");
						}
						sbARR_WBOOK_ID.append(szYD_WBOOK_ID);
						iWBookInsCnt ++;

						//----------------------------
						// 2.2. 스케줄기준 정보 조회
						//----------------------------
						ydWrkCrnPrior = "1"; 							//야드작업크레인우선순위

						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_SCH_CD", szYD_SCH_CD); 	//야드스케쥴코드

						szMsg = "["+szOperationName+"] 야드스케쥴기준 조회 START";
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsSchRule);

						szMsg = "["+szOperationName+"] 야드스케쥴기준 조회 End :: " + Integer.toString(intRtnVal);
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						if (intRtnVal == 1) {
							rsSchRule.first();
							recPara = rsSchRule.getRecord();
							ydWrkCrnPrior = jydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");

							szMsg = "["+szOperationName+"] 야드스케쥴우선순위 :: " + ydWrkCrnPrior;
							jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						} else {
							szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]로 스케줄기준 조회 시 오류발생 ::"+Integer.toString(intRtnVal);
							jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						}

						//----------------------------
						// 2.3 작업예약 INSERT
						//----------------------------
						if ("Y".equals(szYD_TO_LOC_GUIDE_GP)) {
							szYD_AIM_YD_GP  = szYD_TO_LOC_GUIDE.substring(0, 1);
							szYD_AIM_BAY_GP = szYD_TO_LOC_GUIDE.substring(1, 2);
						}

						// 작업예약 레코드 생성
						recOutPara.setField("YD_LOT_GP_SH",       	Integer.toString(rsTemp.size()));	//Lot편성 매수
						recOutPara.setField("YD_GP",      			szYD_GP);							//야드구분
						recOutPara.setField("YD_BAY_GP",      		szYD_BAY_GP);						//동구분
						recOutPara.setField("YD_AIM_YD_GP",      	szYD_AIM_YD_GP);					//목표야드구분
						recOutPara.setField("YD_AIM_BAY_GP",      	szYD_AIM_BAY_GP);					//목표동구분
						//-----------------------------------------------------------------------------------------------------------
						//	대차상차스케줄인 경우에는
						//	1. 동까지만 선택하는 경우에 대차하차스케줄의 TO위치결정 시는 위치검색베드 적용
						//	2. 베드까지 선택하는 경우에 대차하차스케줄의 TO위치결정 시는 사용자지정위치로 적용
						//-----------------------------------------------------------------------------------------------------------
						//if ("F".equals(szYD_TO_LOC_DCSN_MTD) && szYD_TO_LOC_GUIDE.length() == 8) {
						//	recOutPara.setField("YD_TO_LOC_DCSN_MTD",	szYD_TO_LOC_DCSN_MTD);			//야드 TO위치결정방법
						//	recOutPara.setField("YD_TO_LOC_GUIDE",     	szYD_TO_LOC_GUIDE);				//야드TO위치가이드
						//}
						recOutPara.setField("YD_TO_LOC_DCSN_MTD",	szYD_TO_LOC_DCSN_MTD);				//야드 TO위치결정방법
						recOutPara.setField("YD_TO_LOC_GUIDE",     	szYD_TO_LOC_GUIDE);					//야드TO위치가이드
						recOutPara.setField("YD_WRK_PLAN_TCAR",     szTCAR);							//작업계획대차
						recOutPara.setField("YD_WBOOK_ID",      	szYD_WBOOK_ID);						//작업예약ID
						recOutPara.setField("REGISTER", 			szREGISTER);
						recOutPara.setField("MODIFIER", 			szREGISTER);
						recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD); 						//야드스케쥴코드
						recOutPara.setField("YD_SCH_PRIOR", 		ydWrkCrnPrior); 					//야드스케쥴우선순위
						recOutPara.setField("YD_SCH_PROG_STAT", 	"W"); 								//야드스케쥴진행상태(스케줄수행대기)
						recOutPara.setField("YD_SCH_ST_GP", 		"M"); 								//야드스케쥴기동구분 (A:Auto,B:BackUp,M:Manual)
						recOutPara.setField("YD_SCH_REQ_GP", 		"X"); 								//야드스케쥴요청구분
						recOutPara.setField("YD_UP_COLL_SEQ", 		(jj + 1)+""); 						//작업순서
						
						szMsg = "["+szOperationName+"] 2.3.작업예약 Insert :: ";	// + recOutPara.toString();
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
						// 작업예약 Insert
						intRtnVal = ydWrkbookDao.insYdWrkbook(recOutPara);
						if (intRtnVal < 1) {
							szRtnMsg = "작업예약 데이터 등록 중 에러 .." + Integer.toString(intRtnVal);
							szMsg    = "["+szOperationName+"] " + szRtnMsg;
							jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					} else {
						recOutPara.setField("YD_WBOOK_ID",      	szYD_WBOOK_ID);			//작업예약ID
						recOutPara.setField("REGISTER", 			szREGISTER);
						recOutPara.setField("MODIFIER", 			szREGISTER);
					}

					szSTL_NO = jydDaoUtils.paraRecChkNull(recOutPara, "STL_NO");				//재료번호

					//----------------------------
					// 2.4. 작업예약재료 INSERT
					//----------------------------
					szMsg = "["+szOperationName+"] 2.4.작업예약재료 Insert :: ";	// + recOutPara.toString();
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					intRtnVal = ydWrkbookMtlDao.insYdWrkbookMtl(recOutPara);
					if (intRtnVal < 1) {
						szRtnMsg = "작업예약재료 데이터 ["+szSTL_NO+"] 등록 중 에러 .. " + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}

					insWrkMtlCnt ++;

					//----------------------------------
					// 2.5. 저장품에 작업예약ID를 UPDATE
					//----------------------------------
					recOutPara.setField("STL_NO", 		szSTL_NO);
					recOutPara.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);
					recOutPara.setField("YD_SCH_CD", 	szYD_SCH_CD);

					szMsg = "["+szOperationName+"] 2.5.저장품에 작업예약ID를 UPDATE ";	// + recOutPara.toString();
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

					intRtnVal = ydStockDao.updYdStockWbook(recOutPara);
					if (intRtnVal < 1) {
						szRtnMsg = "저장품 데이터 ["+szSTL_NO+"] 수정 중 에러 .. " + Integer.toString(intRtnVal);
						szMsg    = "["+szOperationName+"] " + szRtnMsg;
						jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
					
				}//End Loop jj
			}//End Loop ii

			
			if (insWrkMtlCnt > 0) {

				String[] arrWBookId = sbARR_WBOOK_ID.toString().split(";");

				szMsg = "["+szOperationName+"] ----------- 3.1.이적 스케줄기동 START .... 재료건수 :: " + insWrkMtlCnt + " 작업예약ID :: " + sbARR_WBOOK_ID.toString();
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				for(int ii=0; ii < 1; ii++) {
					
					//크레인 스케줄 호출
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD", 	"YDYDJ710");
					recPara.setField("YD_EQP_ID", 	szYD_WRK_CRN);
					recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
					recPara.setField("YD_WBOOK_ID", arrWBookId[ii]);
			
					ydDelegate.sendMsg(recPara);

					szMsg    = "["+szOperationName+"] ----------- 3.2.이적 스케줄기동 START :: " + ii + " >>>> " + arrWBookId[ii];
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

	    	        //EJBConnector ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
	    	        //szRtnMsg = (String)ejbConn.trx("procY9CrnSchMain", new Class[] { JDTORecord.class }, new Object[] { recPara });

					szMsg    = "["+szOperationName+"] ----------- 3.3.이적 스케줄기동 END :: " + + ii + " >>>> " + szRtnMsg;
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				}
			}

		} catch(Exception e) {
			return  e.getMessage();
		}

		szMsg    = "["+szOperationName+"] ----------- END ";
		jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}  //end of procPrepLotCompByCapa
	
	/**
	 * 오퍼레이션명 : 재료번호로 작업예약/크레인작업지시 존재여부 체크
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String chkExistSchdule(JDTORecord pRecPara) {

		String SZ_SESSION_NAME = PlateReviseSeEJB.class.getName();
		
		JPlateYdWrkbookDAO 	ydWrkbookDao 	= new JPlateYdWrkbookDAO();
		JPlateYdCrnSchDAO  	ydCrnSchDao		= new JPlateYdCrnSchDAO();
			
		JPlateYdUtils    	jydUtils 		= new JPlateYdUtils();
        JPlateYdDaoUtils 	jydDaoUtils 	= new JPlateYdDaoUtils();
        
		// 레코드 선언
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		//레코드셋 선언
		JDTORecordSet 	rsResult 	= null;

		String 	szMethodName 		= "chkExistSchdule";
		String 	szOperationName 	= "스케쥴 존재여부 체크";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String	szStlNo				= "";

		int 	intRtnVal 			= 0;

		try {

			szStlNo = jydDaoUtils.paraRecChkNull(pRecPara, "STL_NO");

			// ------------------------------------------------------------------------
			// 1. 작업예약 존재여부 확인
			// ------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
			recPara.setField("YD_GP",		"P");

			intRtnVal = ydWrkbookDao.getExistByStlNo(recPara, rsResult);
			if (intRtnVal > 0) {
				szRtnMsg = "해당 재료["+szStlNo+"]로 작업예약이 존재!";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

			// ------------------------------------------------------------------------
			// 2. 크레인 작업지시 존재여부 확인
			// ------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("yd");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 		szStlNo);             	// 재료번호
			recPara.setField("YD_GP",		"P");

			intRtnVal = ydCrnSchDao.getExistByStlNo(recPara, rsResult);
			if (intRtnVal > 0) {
				szRtnMsg = "해당 재료"+szStlNo+"로 크레인 작업지시 존재!";
				szMsg    = "["+szOperationName+"] " + szRtnMsg;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

		} catch (Exception e) {
			szRtnMsg = "스케쥴 존재여부 체크 중 예외발생!";
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100;
			return "데이터 스케쥴 존재여부 체크 중 예외발생!" + e.getMessage();
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	/**
	 * 오퍼레이션명 : 준비스케줄 시 대상재 검색
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String chkPrepLotGpStlList(String szSTL_LIST, String szARR_WLOC_CD, JDTORecordSet rsResult) throws DAOException {
		// 저장품 DAO
		JPlateYdStockDAO ydStockDao = new JPlateYdStockDAO();
		// 레코드 선언
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		recStock 	= null;
		//레코드셋 선언
		JDTORecordSet 	outRecSet 	= null;
		
		String SZ_SESSION_NAME = PlateReviseSeEJB.class.getName();
		
		JPlateYdUtils    	jydUtils 		= new JPlateYdUtils();
        JPlateYdDaoUtils 	jydDaoUtils 	= new JPlateYdDaoUtils();
        
		int 	intRtnVal 			= 0;
		String 	szMethodName 		= "chkPrepLotGpStlList";
		String 	szOperationName 	= "준비스케줄 시 대상재 검색";
		String 	szMsg 				= "";
		String 	szRtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String	szYdStkLyrMtlStat	= "";

		String[] strArrStlNo    	= null;

		try {

			strArrStlNo = szSTL_LIST.split(";");

			//적치열,베드에 적치된 해당하는 대상재를 조회
			for (int ii=0; ii<strArrStlNo.length; ii++) {

				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("STL_NO", strArrStlNo[ii]);
				recPara.setField("YD_GP", "P");

				intRtnVal = ydStockDao.getYdStockWithLoc(recPara, outRecSet);		// intGp == 110

				//조회결과를 체크
				if (intRtnVal == 0 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재가 존재하지 않습니다.";
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					return szRtnMsg;
				} else if (intRtnVal == -2 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + ", 데이터 조회중 parameter error 발생!";
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				} else if (intRtnVal < 0 ) {
					szRtnMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재를 조회 시 에러가 발생했습니다.";
					jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szRtnMsg;
				}
				szMsg = "재료번호 = " + strArrStlNo[ii] + "에 대상재가 존재합니다.";
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

				// 재료적치상태 체크 : YD_STK_LYR_MTL_STAT
				outRecSet.first();
				recStock = outRecSet.getRecord();
				szYdStkLyrMtlStat = jydDaoUtils.paraRecChkNull(recStock, "YD_STK_LYR_MTL_STAT");
				if (!"C".equals(szYdStkLyrMtlStat)) {
					szRtnMsg = "재료번호 [" + strArrStlNo[ii] + "] 의 적치상태가 [" + szYdStkLyrMtlStat + "] 로 작업불가 합니다.";
					return szRtnMsg;
				}

				rsResult.addRecord(recStock);
			//	rsResult.addAll(outRecSet);
			}

			jydUtils.putLog(SZ_SESSION_NAME, szMethodName, rsResult.toString(), JPlateYdConst.DEBUG);

			intRtnVal = rsResult.size();

			// 리턴값 메세지처리
			if (intRtnVal <= 0) {
				szRtnMsg = "이적대상 데이터가 없습니다.";
				szMsg    = "[" + szOperationName + "] " + szRtnMsg;
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			} else {
				szMsg    = "[" + szOperationName + "] " + intRtnVal + "건의 이적대상 조회 완료";
				jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			}

		} catch (Exception e) {
			szRtnMsg = "데이터 유무체크 및 데이터 반환 중 예외발생!";
			szMsg    = "[" + szOperationName + "] " + szRtnMsg;
			jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100;
			return "데이터 유무체크 및 데이터 반환 중 예외발생!" + e.getMessage();
		}
		return szRtnMsg;
	} // end of chkPrepLotGpStlList


	/**
	 * 오퍼레이션명 : 작업구분에 따른 스케줄코드 Set
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws DAOException
	 */
	public String getSchCdByWrkGp(String pYD_MAIN_WRK_GP, String pFromYdStrLoc, String pToYdStrLoc, String pTCar) {

		// pYD_MAIN_WRK_GP  -- 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차, M:보수장이적
		// pFromYdStrLoc	-- 야드적치열구분 	(FROM위치)
		// pToYdStrLoc		-- 야드적치열구분 	(TO위치)
		// pTCar			-- 대차설비번호   	(FXTC01, FXTC02)

		// 이적은 FRom위치 기준으로 스케줄코드가 만들어 지고
		// 보급, Book-In은 To위치 기준으로 만들어짐
		String SZ_SESSION_NAME = PlateReviseSeEJB.class.getName();
		
		JPlateYdUtils    	jydUtils 		= new JPlateYdUtils();
        JPlateYdDaoUtils 	jydDaoUtils 	= new JPlateYdDaoUtils();
        
        
		String 	szYD_SCH_CD		= "";
		String	szMsg			= "";
		String	szOperationName	= "스케줄코드 Set";
		String	szMethodName	= "getSchCdByWrkGp";

		szMsg    = "[" + szOperationName + "] >>>> 파라미터 >>>> pYD_MAIN_WRK_GP:" + pYD_MAIN_WRK_GP + ", pFromYdStrLoc: " + pFromYdStrLoc
		         + ", pToYdStrLoc::"+pToYdStrLoc + ", pTCar::" + pTCar;
		jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		// 대차작업이면 대차 상차 스케쥴코드 SET
		if (!"".equals(pTCar)) {

			szYD_SCH_CD	= jydUtils.substr(pFromYdStrLoc, 0, 2) + jydUtils.substr(pTCar, 2, 4) + "UM";			// 대차상차

		//	이적일 경우
		} else if (JPlateYdConst.YD_MAIN_WRK_GP_MV.equals(pYD_MAIN_WRK_GP)) {

			// From위치 기준으로 스케쥴코드 생성하도록 변경
			// 대차일경우
			if ("TC".equals(jydUtils.substr(pFromYdStrLoc, 2, 2))) {
				szYD_SCH_CD	= jydUtils.substr(pFromYdStrLoc, 0, 6) + "LM";									// 대차하차
			} else {
				szYD_SCH_CD = jydUtils.substr(pFromYdStrLoc, 0, 2) + "YD" + jydUtils.substr(pFromYdStrLoc, 2, 2) + "MM";
			}

		} else {

			// BOOK-IN일 경우
			if (JPlateYdConst.YD_MAIN_WRK_GP_RT_IN.equals(pYD_MAIN_WRK_GP)) {								// RT BOOK-IN일 경우

			//	szYD_SCH_CD = jydUtils.substr(pToYdStrLoc, 0, 2) + "RT" + "00UM";
				szYD_SCH_CD = jydUtils.getRtSchCd(pToYdStrLoc, "UM");

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_GAS_IN.equals(pYD_MAIN_WRK_GP)) {						// GAS장보급일 경우 (BOOK-IN)

				szYD_SCH_CD = jydUtils.substr(pToYdStrLoc, 0, 2) + "CN" + jydUtils.substr(pToYdStrLoc, 4, 1) + "0UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_BS_IN.equals(pYD_MAIN_WRK_GP)) {						// 보수장보급일 경우 (BOOK-IN)

				szYD_SCH_CD = jydUtils.substr(pToYdStrLoc, 0, 2) + "BS" + "00UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_TOD_IN.equals(pYD_MAIN_WRK_GP)) {						// TOD보급일 경우 (BOOK-IN)

				szYD_SCH_CD = jydUtils.substr(pToYdStrLoc, 0, 2) + "TD" + "00UM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_PT_IN.equals(pYD_MAIN_WRK_GP)) {						// 이송차량 상차일 경우 [FCPT01UM]

				szYD_SCH_CD = jydUtils.substr(pToYdStrLoc, 0, 2) + "PT" + jydUtils.substr(pToYdStrLoc, 4, 2) + "UM";

			// BOOK-IN일 경우
			} else if (JPlateYdConst.YD_MAIN_WRK_GP_RT_OUT.equals(pYD_MAIN_WRK_GP)) {						// RT BOOK-OUT일 경우

			//	szYD_SCH_CD = jydUtils.substr(pFromYdStrLoc, 0, 2) + "RT" + "00LM";
				szYD_SCH_CD = jydUtils.getRtSchCd(pFromYdStrLoc, "LM");

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_GAS_OUT.equals(pYD_MAIN_WRK_GP)) {						// GAS장추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = jydUtils.substr(pFromYdStrLoc, 0, 2) + "CN" + jydUtils.substr(pFromYdStrLoc, 4, 1) + "0LM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_BS_OUT.equals(pYD_MAIN_WRK_GP)) {						// 보수장추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = jydUtils.substr(pFromYdStrLoc, 0, 2) + "BS" + "00LM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_TOD_OUT.equals(pYD_MAIN_WRK_GP)) {						// TOD추출일 경우 (BOOK-OUT)

				szYD_SCH_CD = jydUtils.substr(pFromYdStrLoc, 0, 2) + "TD" + "00LM";

			} else if (JPlateYdConst.YD_MAIN_WRK_GP_PT_OUT.equals(pYD_MAIN_WRK_GP)) {						// 이송차량 하차일 경우 [FCPT01LM]

				szYD_SCH_CD = jydUtils.substr(pFromYdStrLoc, 0, 2) + "PT" + jydUtils.substr(pFromYdStrLoc, 4, 2) + "LM";
			}
		}

		szMsg    = "[" + szOperationName + "] >>>> 스케쥴코드 >>>> " + szYD_SCH_CD;
		jydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szYD_SCH_CD;
	} // end of getSchCdByWrkGp

	
} // end Class

