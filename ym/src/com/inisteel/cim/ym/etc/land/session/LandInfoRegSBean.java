package com.inisteel.cim.ym.etc.land.session;

import java.util.List;
import java.io.*;
import java.util.ArrayList;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.util.CommonUtil;

import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.dao.CommonDAO;

import com.inisteel.cim.ym.etc.land.dao.LandDAO;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="LandInfoRegEJB" jndi-name="JNDILandInfoReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class LandInfoRegSBean extends BaseSessionBean {
	
	Logger logger = null;
	
	LandDAO dao = new LandDAO();
	 
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger = new Logger(config);
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 엑셀파일 업로드 데이타 생성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public JDTORecord insertUpLoadInfo(JDTORecord jrData) {
		
		JDTORecord jrRtn	=  null;
		
		PrintWriter out = null;
		try{
			
			String ITEM 	= "/app/webapps/inisteelApp/inisteelWebApp/ym/common/excel.log";

			File file = new File(ITEM);
			file.delete();
			
			out = new PrintWriter(new FileWriter(ITEM,true));
			
			String sRegister     		= StringHelper.evl(jrData.getFieldString("REGISTER"), "");
			String sListGbn     		= StringHelper.evl(jrData.getFieldString("LIST_GBN"), "");
			List listData 			= (List)jrData.getField("LIST_DATA");
			
			if("1".equals(sListGbn)){		//토지계획
				jrRtn = insertLandPlanInfo(listData, sRegister, out);
			}else if("2".equals(sListGbn)){	//토지실적
				jrRtn = insertLandWrsltInfo(listData, sRegister, out);
			}else if("3".equals(sListGbn)){	//지장물계획
				jrRtn = insertProcPlanInfo(listData, sRegister, out);
			}else if("4".equals(sListGbn)){	//지장물실적
				jrRtn = insertProcWrsltInfo(listData, sRegister, out);
			}		
			
			out.close();
		}catch(Exception e){
			out.println("==========================에러발생1================================");
			out.println(e);
			throw new EJBServiceException(e);
		}finally{
			out.close();
		}
		
		return jrRtn;
	}
	
	/*
	 *	LAND PLAN LIST
	 *	- 토지계획정보를 신규등록한다.
	 */ 
	private JDTORecord insertLandPlanInfo(List listData, String sRegister, PrintWriter out){
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try{
			for(int inx = 1; inx < listData.size(); inx++)
			{
				int intRx = insertLandPlanFullInfo((JDTORecord) listData.get(inx),sRegister, out);
			}
			
			jrRtn.setField("ERR_YN", "S");
			
		}catch(Exception e){ 
			out.println("==========================에러발생2================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		
		return jrRtn;
	}
	
	/*
	 *	LAND PLAN FULL DATA
	 *	- 토지계획정보를 신규등록한다.
	 */ 
	private int insertLandPlanFullInfo(JDTORecord data, String sRegister, PrintWriter out){
		
		int iResult = 0;
		
		try{		
			//양식순서:품의차수/품의일자/소재지/지번/지목/면적/지분/부지구분/취득농특세/등록농특세/산정금액/	소유자/소유자주소/조사번호/비고
			String strSanctionStepNo 	= convert(data.getFieldString("COL_0"));					// 품의차수
			String strSanctionDate 		= convertDate(convert(data.getFieldString("COL_1")));			// 품의일자
			String strAddress 			= convert(data.getFieldString("COL_2"));					// 소재지
			String strPlaceNo 			= convert(data.getFieldString("COL_3"));					// 지번
			String strLandClass 		= convert(data.getFieldString("COL_4"));					// 지목
			String strArea 				= convert(data.getFieldString("COL_5"));					// 면적
			String strShareF 			= convert(data.getFieldString("COL_6"));					// 지분
			String strGroundGp 			= convert(data.getFieldString("COL_7"));					// 부지구분
			String strAcqFarmFax 		= convertAmt(convert(data.getFieldString("COL_8")));			// 취득농특세
			String strRegFarmFax 		= convertAmt(convert(data.getFieldString("COL_9"))); 			// 등록농특세
			String strCalAmt 			= convertAmt(convert(data.getFieldString("COL_10")));			// 산정금액
			String strOwner 			= convert(data.getFieldString("COL_11")); 					// 소유자
			String strOwnerAdd 		= StringHelper.evl(data.getFieldString("COL_12"), ""); 			// 소유자주소
			String strCheckNo 			= convert(data.getFieldString("COL_13")); 					// 조사번호
			String strRemark 			= StringHelper.evl(data.getFieldString("COL_14"), ""); 			// 비고
			String strSanctionGp		= StringHelper.evl(data.getFieldString("COL_15"), "1"); 			// 계획구분
			String strRegister 			= sRegister;											// 등록자
			
			logger.println(LogLevel.INFO,"LAND PLAN> 품의차수		="+ strSanctionStepNo+"=");       
			logger.println(LogLevel.INFO,"LAND PLAN> 품의일자		="+ strSanctionDate+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 소재지		="+ strAddress+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 지번			="+ strPlaceNo+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 지목			="+ strLandClass+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 면적			="+ strArea+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 지분			="+ strShareF+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 부지구분		="+ strGroundGp+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 산정금액		="+ strCalAmt+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 취득농특세	="+ strAcqFarmFax+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 등록농특세	="+ strRegFarmFax+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 소유자		="+ strOwner+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 소유자주소	="+ strOwnerAdd+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 조사번호		="+ strCheckNo+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 비고			="+ strRemark+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 계획구분		="+ strSanctionGp+"=");              
			logger.println(LogLevel.INFO,"LAND PLAN> 등록자		="+ strRegister+"=");              
			
			//엑셀데이타 정합성 체크
			{
				if(strSanctionDate.length() != 8){
					// 에러파일정보 등록 => 품의일자 정보이상
					out.println("=====>"+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strOwner+"]"+"["+strSanctionDate+"]"+"[토지계획 품의일자정보 이상.]");
				}	
			}
			
			if(!"".equals(strSanctionStepNo.trim())){
				JDTORecord dataJr	= dao.getLandPlanInfo_01(strSanctionStepNo,	//품의차수
						  							  strAddress,			//소재지
						  							  strPlaceNo,			//지번
						  							  strOwner);			//소유자
				if(dataJr == null){
										
					List dataList = new ArrayList();
					
					dataList.add(strSanctionStepNo);		// 품의차수
					dataList.add(strAddress);			// 소재지
					dataList.add(strPlaceNo);			// 지번
					dataList.add(strOwner);				// 소유자
					dataList.add(strSanctionDate);		// 품의일자 
					dataList.add(strLandClass);			// 지목
					dataList.add(strArea);				// 면적
					dataList.add(strShareF);				// 지분
					dataList.add(strGroundGp);			// 부지구분
					dataList.add(strCalAmt);				// 산정금액
					dataList.add("");					// 주민/사업자번호
					dataList.add(strOwnerAdd); 			// 소유자주소
					dataList.add(strAcqFarmFax);			// 취득농특세
					dataList.add(strRegFarmFax); 		// 등록농특세
					dataList.add(strCheckNo); 			// 조사번호
					dataList.add(strRemark); 			// 비고
					dataList.add(strSanctionGp); 			// 계획구분
					dataList.add(strRegister);			// 등록자
					
					iResult =  dao.insertLandPlanInfo(dataList);
					
				}else{
					// 에러파일정보 등록 => 키값 중복
					out.println("실패="+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strOwner+"]"+"[이미 존재하는 토지계획정보입니다.]");
				}
			}else{
				// 에러파일정보 등록 => 키값 중복
				out.println("실패="+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strOwner+"]"+"[토지계획정보 주요 키값항목이 들어오지 않았습니다.]");
			}
		}catch(Exception e){ 
			out.println("==========================에러발생3================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		 
		return iResult;
	}
	
	/*
	 *	LAND WRSLT LIST
	 *	- 토지계획에 따른 실적정보를 등록한다.
	 */ 
	private JDTORecord insertLandWrsltInfo(List listData, String sRegister, PrintWriter out){
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try{
			/*
			 * 1. 신규계획 및 실적정보 등록
			 */
			for(int inx = 1; inx < listData.size(); inx++)
			{
				int intRx = insertLandWrsltMainInfo((JDTORecord) listData.get(inx),sRegister, out);
			}
			/*
			 * 2. 이력정보 등록
			 */
			 
			 updateLandLogMain(sRegister,out);
			 
			 
			jrRtn.setField("ERR_YN", "S");
			
		}catch(Exception e){ 
			out.println("==========================에러발생4================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		
		return jrRtn;
	}
	
	/*
	 *	LAND WRSLT MAIN
	 *	실적데이타를 가지고 실적정보를 등록하거나,
	 *					   신규계획 및 실적정보를 등록한다.
	 */ 
	private int insertLandWrsltMainInfo(JDTORecord data, String sRegister, PrintWriter out){
		
		int iResult = 0;
		
		try{
			//양식순서:품의차수/소재지/지번/면적/지분/소유자/지급자/주민/사업자번호/지급자주소/취득농특세/등록농특세/보상금액/지급일자
			String strSanctionStepNo 	= convert(data.getFieldString("COL_0"));					// 품의차수
			String strAddress 			= convert(data.getFieldString("COL_1"));					// 소재지
			String strPlaceNo 			= convert(data.getFieldString("COL_2"));					// 지번
			String strArea	 			= convert(data.getFieldString("COL_3"));					// 면적
			String strShareF 			= convert(data.getFieldString("COL_4"));					// 지분
			String strOwner 			= convert(data.getFieldString("COL_5"));					// 소유자
			String strPayer 			= convert(data.getFieldString("COL_6"));					// 지급자
			String strSpecialNo 			= convert(data.getFieldString("COL_7"));   					// 주민/사업자번호
			String strPayerAdd 			= StringHelper.evl(data.getFieldString("COL_8"), ""); 			// 지급자주소
			String strAcqFarmFax		= convertAmt(convert(data.getFieldString("COL_9")));			// 취득농특세
			String strRegFarmFax 		= convertAmt(convert(data.getFieldString("COL_10")));			// 등록농특세
			String strCompenMoney 		= convertAmt(convert(data.getFieldString("COL_11")));			// 보상금액
			String strPayDate 			= convertDate(convert(data.getFieldString("COL_12")));		// 지급일자
			String strUserId			= sRegister;											// 등록자
			/*
			logger.println(LogLevel.INFO,"LAND MAIN> 품의차수		="+ strSanctionStepNo+"=");       
			logger.println(LogLevel.INFO,"LAND MAIN> 소재지		="+ strAddress+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 지번			="+ strPlaceNo+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 면적			="+ strArea+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 지분			="+ strShareF+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 소유자		="+ strOwner+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 지급자		="+ strPayer+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 주민/사업번호	="+ strSpecialNo+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 지급자주소	="+ strPayerAdd+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 취득농특세	="+ strAcqFarmFax+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 등록농특세	="+ strRegFarmFax+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 보상금액		="+ strCompenMoney+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 지급일자		="+ strPayDate+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 등록자		="+ strUserId+"=");              
			*/		  							  													
			/*
			Case 1 계획 : 실적정보 일치시 (품의일자,소재지,지번,소유자/지급자,산정금액/보상금액)
			Case 2 계획 : 실적정보 소유자/지급자 불일치시(품의일자,소재지,지번,소유자/지급자,산정금액/보상금액)
					1. 지급자의 신규계획정보 생성
					2. 지급자의 실적정보 생성
					3. 지급자의 이력정보 생성(원소유자의 정보)
			Case 3 계획 : 실적정보 소유자(1)/지급자(N) 불일치시(품의일자,소재지,지번,소유자/지급자,산정금액/보상금액)
					1. 산정금액/보상금액 불일치시 추가 실적정보가 존재하는지 확인한다.(토지는 분할지급 없다.)
			Case 3 계획 : 실적정보 소유자/지급자 , 산정금액/보상금액 불일치시
					1. 지급자의 신규계획정보 생성
					2. 지급자의 실적정보 생성
					3. 지급자의 이력정보 생성(원소유자의 정보)
					4. 원소유자의 금액정보 수정
					5. 원소유자(수정)의 이력정보 생성(원소유자의 정보)
			*/
			if(!"".equals(strSanctionStepNo.trim())){
				JDTORecord dataJr	= dao.getLandPlanInfo_01(strSanctionStepNo,	//품의차수
						  							  strAddress,			//소재지
						  							  strPlaceNo,			//지번
						  							  strOwner);			//소유자
							  							  
				/*
				 *	소유자와 지급자가 일치하는지를 체크한다.
				 *	- 일치하면 일반 실적등록
				 *	- 불일치하면 경우의 수별 실적등록
				 */
				 
				if(strOwner.equals(strPayer)){
					
					if(dataJr != null){
						
						/*
						 *	1. 소유자의 계획정보 수정
						 *	   - 주민/사업자번호를 실적시 입력받아 등록한다.
						 */
						iResult = dao.updateLandPlanInfo_01(	strSpecialNo, 			// 주민/사업자번호
														strSanctionStepNo,		// 품의차수
						  							   	strAddress,			// 소재지
						  							  	strPlaceNo,			// 지번
						  							  	strOwner);			// 소유자
						
						/*
						 *	2. 소유자의 신규실적정보 생성
						 */
						iResult = insertLandWrsltInfo(data, strUserId, out);	
					}else{
						// 에러파일정보 등록 => 계획정보 존재안함.
						out.println("실패="+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strOwner+"]"+"[토지계획정보 존재안함]");
					}
				}else{
					
					if(dataJr != null){
						
						/*
						 *	1. 지급자의 신규계획정보 생성
						 */
						{
							String strSanctionDate		= StringHelper.evl(dataJr.getFieldString("SANCTION_DATE"), "");		//품의일자 			
							String strLandClass			= StringHelper.evl(dataJr.getFieldString("LAND_CLASS"), ""); 		//지목		
							String strGroundGp 			= StringHelper.evl(dataJr.getFieldString("GROUND_GP"), ""); 		//부지구분	
							String strCheckNo			= StringHelper.evl(dataJr.getFieldString("CHECK_NO"), ""); 			//조사번호
							String strRemark 			= StringHelper.evl(dataJr.getFieldString("REMARK"), ""); 			//비고
							String strSanctionGp		= StringHelper.evl(dataJr.getFieldString("SANCTION_GP"), ""); 		//계획구분
							
							List dataList = new ArrayList();
							
							dataList.add(strSanctionStepNo);		// 품의차수
							dataList.add(strAddress);			// 소재지
							dataList.add(strPlaceNo);			// 지번
							dataList.add(strPayer);				// 소유자(<= 지급자)
							dataList.add(strSanctionDate);		// 품의일자 
							dataList.add(strLandClass);			// 지목
							dataList.add(strArea);				// 면적
							dataList.add(strShareF);				// 지분
							dataList.add(strGroundGp);			// 부지구분
							dataList.add(strCompenMoney);		// 산정금액(<= 보상급액)
							dataList.add(strSpecialNo);			// 주민/사업자번호(<= 지급자)
							dataList.add(strPayerAdd); 			// 소유자주소(<= 지급자주소)
							dataList.add(strAcqFarmFax);			// 취득농특세
							dataList.add(strRegFarmFax); 		// 등록농특세
							dataList.add(strCheckNo); 			// 조사번호
							dataList.add(strRemark); 			// 비고
							dataList.add(strSanctionGp); 			// 계획구분
							dataList.add(strUserId);				// 등록자
							
							iResult =  dao.insertLandPlanInfo(dataList);
						}
						
						/*
						 *	2. 지급자의 신규실적정보 생성
						 */
							iResult = insertLandWrsltInfo(data, strUserId, out);	
						
						/*
						 *	3. 소유자의 계획정보 수정
						 *	   - 이력관리대상임을 표시한다.
						 */
							iResult = dao.updateLandPlanInfo_02(	"Y", 					// 이력관리대상 Flag
															strSanctionStepNo,		// 품의차수
							  							   	strAddress,			// 소재지
							  							  	strPlaceNo,			// 지번
							  							  	strOwner);			// 소유자
					}else{
						out.println("실패="+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strOwner+"]"+"[토지계획정보 존재안함]");
					}
				}
			}else{
				out.println("실패="+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strOwner+"]"+"[토지계획정보 주요키값 항목이 존재안함]");
			}
		}catch(Exception e){ 
			out.println("==========================에러발생5================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		
		return iResult;
	}
	
	/*
	 *	LAND WRSLT DATA
	 *	- 토지계획에 따른 실적정보를 등록한다.
	 */ 
	private int insertLandWrsltInfo(JDTORecord data, String sRegister, PrintWriter out){
		
		int iResult = 0;
		
		try{
			//양식순서:품의차수/소재지/지번/면적/지분/소유자/지급자/주민/사업자번호/지급자주소/취득농특세/등록농특세/보상금액/지급일자
			String strSanctionStepNo 	= convert(data.getFieldString("COL_0"));					// 품의차수
			String strAddress 			= convert(data.getFieldString("COL_1"));					// 소재지
			String strPlaceNo 			= convert(data.getFieldString("COL_2"));					// 지번
			String strArea	 			= convert(data.getFieldString("COL_3"));					// 면적
			String strShareF 			= convert(data.getFieldString("COL_4"));					// 지분
			String strOwner 			= convert(data.getFieldString("COL_5"));					// 소유자
			String strPayer 			= convert(data.getFieldString("COL_6"));					// 지급자
			String strSpecialNo 			= convert(data.getFieldString("COL_7"));   					// 주민/사업자번호
			String strPayerAdd 			= StringHelper.evl(data.getFieldString("COL_8"), ""); 			// 지급자주소
			String strAcqFarmFax		= convertAmt(convert(data.getFieldString("COL_9")));			// 취득농특세
			String strRegFarmFax 		= convertAmt(convert(data.getFieldString("COL_10")));			// 등록농특세
			String strCompenMoney 		= convertAmt(convert(data.getFieldString("COL_11")));			// 보상금액
			String strPayDate 			= convertDate(convert(data.getFieldString("COL_12")));		// 지급일자
			String strUserId			= sRegister;											// 등록자
			
			logger.println(LogLevel.INFO,"LAND MAIN> 품의차수		="+ strSanctionStepNo+"=");       
			logger.println(LogLevel.INFO,"LAND MAIN> 소재지		="+ strAddress+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 지번			="+ strPlaceNo+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 면적			="+ strArea+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 지분			="+ strShareF+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 소유자		="+ strOwner+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 지급자		="+ strPayer+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 주민/사업번호	="+ strSpecialNo+"=");               
			logger.println(LogLevel.INFO,"LAND MAIN> 지급자주소	="+ strPayerAdd+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 취득농특세	="+ strAcqFarmFax+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 등록농특세	="+ strRegFarmFax+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 보상금액		="+ strCompenMoney+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 지급일자		="+ strPayDate+"=");              
			logger.println(LogLevel.INFO,"LAND MAIN> 등록자		="+ strUserId+"=");              
			
			//엑셀데이타 정합성 체크
			{
				if(strPayDate.length() != 8){
					// 에러파일정보 등록 => 품의일자 정보이상
					out.println("=====>"+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strOwner+"]"+"["+strPayDate+"]"+"[토지계획실적 지급일자정보 이상.]");
				}	
			}
			
			if(!"".equals(strSanctionStepNo.trim())){
					
				JDTORecord dataJr	= dao.getLandWrsltInfo_01(strSanctionStepNo,	//품의차수
						  							  strAddress,			//소재지
						  							  strPlaceNo,			//지번
						  							  strPayer);			//소유자
				if(dataJr == null){
					
					
					JDTORecord dataOrgJr	= dao.getLandPlanInfo_01(strSanctionStepNo,	//품의차수
								  							  strAddress,			//소재지
								  							  strPlaceNo,			//지번
								  							  strOwner);			//소유자
										
					if(dataOrgJr != null){
						
						String strOrgAmt 	= StringHelper.evl(dataOrgJr.getFieldString("CAL_AMT"), ""); 			
						long OrgCalAmt 	= Long.parseLong(strOrgAmt);
						long NewCalAmt 	= Long.parseLong(strCompenMoney);
						long TotalAmt 		= OrgCalAmt - NewCalAmt;
						
						if(TotalAmt > 0){
							// 에러파일정보 등록 => 금액정보가 맞지않음
							out.println("=====>"+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strOwner+"]"+"["+strOrgAmt+"]"+"["+strCompenMoney+"]"); 
							out.println("=====>"+"금액정보가 맞지않음 :"+TotalAmt); 
						}else if(TotalAmt < 0){
							// 에러파일정보 등록 => 금액정보가 맞지않음
							out.println("=====>"+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strOwner+"]"+"["+strOrgAmt+"]"+"["+strCompenMoney+"]"); 
							out.println("=====>"+"금액정보가 맞지않음 :"+TotalAmt); 
						}
					}
					
					List dataList = new ArrayList();
				
				     	dataList.add(strSanctionStepNo);		// 품의차수
					dataList.add(strAddress);			// 소재지
					dataList.add(strPlaceNo);			// 지번
					dataList.add(strPayer);				// 지급자
					dataList.add(strOwner);				// 소유자
					dataList.add(strSpecialNo);			// 지급자 주민/사업번호
					dataList.add(strArea);				// 면적
					dataList.add(strAcqFarmFax);			// 취득농특세
					dataList.add(strRegFarmFax); 		// 등록농특세
					dataList.add(strCompenMoney); 		// 보상금액
					dataList.add(strPayDate); 			// 지급일자
					dataList.add(strUserId);				// 등록자
					
					//체크사항 : 이미등록된 실적정보 고려
					iResult =  dao.insertLandWrsltInfo(dataList);
					
				}else{
					// 에러파일정보 등록 => 키값 중복
					out.println("실패="+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strPayer+"]"+"[이미 존재하는 토지실적정보입니다.]");
				}
			}else{
				// 에러파일정보 등록 => 키값 존재안함
				out.println("실패="+"["+strSanctionStepNo+"]"+"["+strAddress+"]"+"["+strPlaceNo+"]"+"["+strPayer+"]"+"[토지실적정보 주요키값 항목 존재안함.]");
			}
		}catch(Exception e){ 
			out.println("==========================에러발생6================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		
		return iResult;
	}
	
	private void updateLandLogMain(String strUserId, PrintWriter out){
		try{
			int iResult = 0;
			
			List delData = dao.getLandPlanList_01();
			
			if(delData.size() > 0)
			{
				for(int inx = 0; inx < delData.size(); inx++)
				{
					JDTORecord dataJr = (JDTORecord) delData.get(inx);
					
					String strOrgSanctionStepNo	= StringHelper.evl(dataJr.getFieldString("SANCTION_STEP_NO"), "");	//품의차수 			
					String strOrgAddress		= StringHelper.evl(dataJr.getFieldString("ADDRESS"), ""); 			//소재지	
					String strOrgPlaceNo 		= StringHelper.evl(dataJr.getFieldString("PLACE_NO"), ""); 			//지번	
					String strOrgOwner			= StringHelper.evl(dataJr.getFieldString("OWNER"), ""); 			//소유자
					String strOrgCalAmt			= StringHelper.evl(dataJr.getFieldString("CAL_AMT"), "0"); 			//산정금액
					
					logger.println(LogLevel.INFO,"LAND LOG> 원품의차수		="+ strOrgSanctionStepNo+"=");              
					logger.println(LogLevel.INFO,"LAND LOG> 원소재지		="+ strOrgAddress+"=");              
					logger.println(LogLevel.INFO,"LAND LOG> 원지번			="+ strOrgPlaceNo+"=");              
					logger.println(LogLevel.INFO,"LAND LOG> 원소유자		="+ strOrgOwner+"=");              
					logger.println(LogLevel.INFO,"LAND LOG> 원산정금액		="+ strOrgCalAmt+"=");              
					
					long OrgCalAmt 	= Long.parseLong(strOrgCalAmt);
					long TotalAmt		= OrgCalAmt;
					
					//1. 품의차수,소재지,지번으로 Y가 아닌 신규등록된 정보를 가져온다.
					
					List newData = dao.getLandPlanList_02(	strOrgSanctionStepNo,
												      		strOrgAddress,
												      		strOrgPlaceNo);
					
					out.println("변경 ="+"["+strOrgSanctionStepNo+"]"+"["+strOrgAddress+"]"+"["+strOrgPlaceNo+"]"+"["+strOrgOwner+"]"+"["+strOrgCalAmt+"]");  						  	
					//2. 신규등록된 정보를 가지고 이력정보를 생성한다.
					for(int iny = 0; iny < newData.size(); iny++)
					{
						JDTORecord newJr = (JDTORecord) newData.get(iny);
						
						String strNewSanctionStepNo	= StringHelper.evl(newJr.getFieldString("SANCTION_STEP_NO"), "");	//품의차수 			
						String strNewAddress		= StringHelper.evl(newJr.getFieldString("ADDRESS"), ""); 			//소재지	
						String strNewPlaceNo 		= StringHelper.evl(newJr.getFieldString("PLACE_NO"), ""); 			//지번	
						String strNewOwner		= StringHelper.evl(newJr.getFieldString("OWNER"), ""); 			//소유자
						String strNewCalAmt		= StringHelper.evl(newJr.getFieldString("CAL_AMT"), "0"); 			//산정금액
						
						logger.println(LogLevel.INFO,"LAND LOG> 뉴품의차수		="+ strNewSanctionStepNo+"=");              
						logger.println(LogLevel.INFO,"LAND LOG> 뉴소재지		="+ strNewAddress+"=");              
						logger.println(LogLevel.INFO,"LAND LOG> 뉴지번			="+ strNewPlaceNo+"=");              
						logger.println(LogLevel.INFO,"LAND LOG> 뉴소유자		="+ strNewOwner+"=");              
						logger.println(LogLevel.INFO,"LAND LOG> 뉴산정금액		="+ strNewCalAmt+"=");              
						
						long NewCalAmt = Long.parseLong(strNewCalAmt);
						
						TotalAmt = TotalAmt - NewCalAmt;
						
						logger.println(LogLevel.INFO,"LAND LOG> 산정금액 계산 ="+ TotalAmt+"=");              
						
						iResult = dao.insertLandHistInfo(	strNewOwner,			//지급자
					 								strUserId,				//등록자
					 								strOrgSanctionStepNo,	//품의차수
						  						    	strOrgAddress,		//소재지
						  						    	strOrgPlaceNo,		//지번
						  						  	strOrgOwner,			//소유자
						  						  	strNewCalAmt);		//지급금액	
						  						  	
						out.println("=====>"+"["+strNewSanctionStepNo+"]"+"["+strNewAddress+"]"+"["+strNewPlaceNo+"]"+"["+strNewOwner+"]"+"["+strNewCalAmt+"]"); 
					}
					
					logger.println(LogLevel.INFO,"LAND LOG> 산정금액 계산결과 ="+ TotalAmt+"=");              
					//3. 오리지널 정보를 삭제한다.
					if(TotalAmt == 0){
						
						iResult = dao.deletetLandPlanInfo(	strOrgSanctionStepNo,	//품의차수
						  						    	strOrgAddress,		//소재지
						  						    	strOrgPlaceNo,		//지번
						  						  	strOrgOwner);			//소유자
						  						  	
					}else if(TotalAmt > 0){
						// 에러파일정보 등록 => 금액정보가 맞지않음
						out.println("=====>"+"금액정보가 맞지않음 :"+TotalAmt); 
					}else if(TotalAmt < 0){
						// 에러파일정보 등록 => 금액정보가 맞지않음
						out.println("=====>"+"금액정보가 맞지않음 :"+TotalAmt); 
					}
				}
				
				iResult = dao.updateLandPlanInfo_03();
				
			}
			
		}catch(Exception e){ 
			out.println("==========================에러발생7================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
	}
	
	/*
	 *	MATERIAL PLAN LIST
	 */ 
	private JDTORecord insertProcPlanInfo(List listData, String sRegister, PrintWriter out){
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try{
			for(int inx = 1; inx < listData.size(); inx++)
			{
				int intRx = insertProcPlanFullInfo((JDTORecord) listData.get(inx),sRegister, out);
			}
			
			jrRtn.setField("ERR_YN", "S");
			
		}catch(Exception e){ 
			out.println("==========================에러발생8================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		
		return jrRtn;
	}
	
	/*
	 *	MATERIAL PLAN DATA
	 */ 
	private int insertProcPlanFullInfo(JDTORecord data, String sRegister, PrintWriter out){
		
		int iResult = 0;
		
		try{
			//양식순서:품의차수/품의일자/소유자/소유자주소/산정금액/물건의종류/수량/식/소재지/지번/조사번호/비고
			String strSanctionStepNo 	= convert(data.getFieldString("COL_0"));				// 품의차수
			String strSanctionDate 		= convertDate(convert(data.getFieldString("COL_1")));		// 품의일자
			String strOwner 			= convert(data.getFieldString("COL_2"));				// 소유자
			String strOwnerAdd			= StringHelper.evl(data.getFieldString("COL_3"), "");		// 소유자주소
			String strCalAmt	 		= convertAmt(convert(data.getFieldString("COL_4")));		// 산정금액
			String strProcKind	 		= convert(data.getFieldString("COL_5"));				// 물건의종류
			String strQnty	 			= convert(data.getFieldString("COL_6")); 				// 수량
			String strProcUnit 			= convert(data.getFieldString("COL_7")); 				// 식
			String strAddress			= convert(data.getFieldString("COL_8"));				// 소재지
			String strPlaceNo 			= StringHelper.evl(data.getFieldString("COL_9"),"");		// 지번
			String strCheckNo 			= convert(data.getFieldString("COL_10"));				// 조사번호
			String strRemark 			= StringHelper.evl(data.getFieldString("COL_11"), "");		// 비고
			String strSanctionGp		= StringHelper.evl(data.getFieldString("COL_12"), "");		// 계획구분
			String strRegister 			= sRegister;										// 등록자
			
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 품의차수		="+ strSanctionStepNo+"=");       
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 품의일자		="+ strSanctionDate+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 소유자		="+ strOwner+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 소유자주소	="+ strOwnerAdd+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 산정금액		="+ strCalAmt+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 소재지		="+ strAddress+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 지번			="+ strPlaceNo+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 조사번호		="+ strCheckNo+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 비고			="+ strRemark+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 물건의종류	="+ strProcKind+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 수량			="+ strQnty+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 식			="+ strProcUnit+"=");              
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 계획구분		="+ strSanctionGp+"=");  
			logger.println(LogLevel.INFO,"MATERIAL PLAN> 등록자		="+ strRegister+"=");              
			
			//엑셀데이타 정합성 체크
			{
				if(strSanctionDate.length() != 8){
					// 에러파일정보 등록 => 품의일자 정보이상
					out.println("=====>"+"["+strSanctionStepNo+"]"+"["+strOwner+"]"+"["+strProcKind+"]"+"["+strSanctionDate+"]"+"[지장물계획 품의일자정보 이상.]");
				}	
			}
			
			if(!"".equals(strSanctionStepNo.trim())){
					
				JDTORecord dataJr	= dao.getProcPlanInfo_01(strSanctionStepNo,	//품의차수
						  							  strOwner,			//소유자
						  							  strProcKind);			//물건의종류
				if(dataJr == null){
														
					List dataList = new ArrayList();
					
					dataList.add(strSanctionStepNo);		// 품의차수
					dataList.add(strOwner);				// 소유자
					dataList.add(strProcKind);			// 물건의종류
					dataList.add(strSanctionDate);		// 품의일자
					dataList.add("");					// 주민/사업자번호 
					dataList.add(strOwnerAdd);			// 소유자주소
					dataList.add(strCalAmt);				// 산정금액
					dataList.add(strAddress);			// 소재지
					dataList.add(strPlaceNo);			// 지번
					dataList.add(strCheckNo);			// 조사번호
					dataList.add(strRemark);				// 비고
					dataList.add(strQnty); 				// 수량
					dataList.add(strProcUnit);			// 식
					dataList.add(strRegister);			// 등록자
					dataList.add("");					// 삭제금액
					dataList.add("");					// 구분자
					dataList.add(strSanctionGp);			// 계획구분
								
					iResult =  dao.insertProcPlanInfo(dataList); 
					
				}else{
					// 에러파일정보 등록 => 키값 중복
					out.println("실패="+"["+strSanctionStepNo+"]"+"["+strOwner+"]"+"["+strProcKind+"]"+"[이미 존재하는 지장물계획정보입니다.]");
				}
			}else{
				// 에러파일정보 등록 => 키값 존재안함
				out.println("실패="+"["+strSanctionStepNo+"]"+"["+strOwner+"]"+"["+strProcKind+"]"+"[지장물계획정보 주요키값 항목 존재안함.]");
			}
		}catch(Exception e){ 
			out.println("==========================에러발생9================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		 
		return iResult;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	
	/*
	 *	지장물 WRSLT LIST
	 *	- 지장물계획에 따른 실적정보를 등록한다.
	 */ 
	private JDTORecord insertProcWrsltInfo(List listData, String sRegister, PrintWriter out){
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try{
			/*
			 * 1. 신규계획 및 실적정보 등록
			 */
			for(int inx = 1; inx < listData.size(); inx++)
			{
				int intRx = insertProcWrsltMainInfo((JDTORecord) listData.get(inx),sRegister,inx+"", out);
			}
			/*
			 * 2. 이력정보 등록
			 */
			 
			 updateProcLogMain(sRegister, out);
			 
			jrRtn.setField("ERR_YN", "S");
			
		}catch(Exception e){ 
			out.println("==========================에러발생10================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		
		return jrRtn;
	}
	
	/*
	 *	지장물 WRSLT MAIN
	 *	실적데이타를 가지고 실적정보를 등록하거나,
	 *					   신규계획 및 실적정보를 등록한다.
	 */ 
	private int insertProcWrsltMainInfo(JDTORecord data, String sRegister, String strIndex, PrintWriter out){
		
		int iResult = 0;
		
		try{
			//양식순서:품의차수/소유자/지급자/주민/사업자번호/지급자주소/물건의종류/보상금액/지급일자
			String strSanctionStepNo 	= convert(data.getFieldString("COL_0"));				// 품의차수
			String strOwner 			= convert(data.getFieldString("COL_1"));				// 소유자
			String strPayer 			= convert(data.getFieldString("COL_2"));				// 지급자
			String strSpecialNo 			= convert(data.getFieldString("COL_3"));  				// 주민/사업자번호
			String strPayerAdd 			= StringHelper.evl(data.getFieldString("COL_4"), ""); 		// 지급자주소
			String strProcKind 			= convert(data.getFieldString("COL_5"));				// 물건의종류
			String strCompenMoney 		= convertAmt(convert(data.getFieldString("COL_6")));		// 보상금액
			String strPayDate 			= convertDate(convert(data.getFieldString("COL_7")));		// 지급일자
			String strUserId			= sRegister;										// 등록자
			
			if(!"".equals(strSanctionStepNo.trim())){
					
				JDTORecord dataJr	= dao.getProcPlanInfo_01(strSanctionStepNo,	//품의차수
						  							  strOwner,			//소유자
						  							  strProcKind);			//물건의종류
							  							  
				/*
				 *	소유자와 지급자가 일치하는지를 체크한다.
				 *	- 일치하면 일반 실적등록
				 *	- 불일치하면 경우의 수별 실적등록
				 */
				 
				if(strOwner.equals(strPayer)){
					
					if(dataJr != null){
						
						/*
						 *	1. 소유자의 계획정보 수정
						 *	   - 주민/사업자번호를 실적시 입력받아 등록한다.
						 */
						iResult = dao.updateProcPlanInfo_01(	strSpecialNo, 			// 주민/사업자번호
														strSanctionStepNo,		// 품의차수
						  							   	strOwner,				// 소유자
						  							  	strProcKind);			// 물건의종류
						
						/*
						 *	2. 소유자의 신규실적정보 생성
						 */
						iResult = insertProcWrsltInfo(data, strUserId, out);	
					}else{
						// 에러파일정보 등록 => 계획정보 존재안함.
						out.println("실패="+"["+strSanctionStepNo+"]"+"["+strOwner+"]"+"["+strProcKind+"]"+"[지장물계획정보 존재안함]");
					}
				}else{
					
					if(dataJr != null){
						
						String strDelSeq = StringHelper.evl(dataJr.getFieldString("DEL_SEQ"), strIndex); 					//구분자	
						
						JDTORecord manJr	= dao.getProcPlanInfo_01(strSanctionStepNo,	//품의차수
								  							  strPayer,			//소유자(<= 지급자)
								  							  strProcKind);			//물건의종류
						
						if(manJr != null){
							/*
							 *	1. 지급자의 계획정보 수정
							 */
							 String strOrgCalAmt = StringHelper.evl(manJr.getFieldString("CAL_AMT"), "0"); 			//산정금액
							 long OrgCalAmt 	= Long.parseLong(strOrgCalAmt);
							 long NewCalAmt 	= Long.parseLong(strCompenMoney);
							 
							//산정금액을 수정한다.
							iResult = dao.updateProcPlanInfo_03(	(OrgCalAmt+NewCalAmt)+"", 	// 산정금액
															strSanctionStepNo,			// 품의차수
							  							   	strPayer,					// 소유자
							  							  	strProcKind);				// 물건의종류  							 
						}else{
							
							/*
							 *	1. 지급자의 신규계획정보 생성
							 */
							String strSanctionDate		= StringHelper.evl(dataJr.getFieldString("SANCTION_DATE"), "");		//품의일자 	
							String strAddress			= StringHelper.evl(dataJr.getFieldString("ADDRESS"), ""); 			//소재지		
							String strPlaceNo 			= StringHelper.evl(dataJr.getFieldString("PLACE_NO"), ""); 			//지번	
							String strCheckNo			= StringHelper.evl(dataJr.getFieldString("CHECK_NO"), ""); 			//조사번호
							String strRemark 			= StringHelper.evl(dataJr.getFieldString("REMARK"), ""); 			//비고
							String strQnty				= StringHelper.evl(dataJr.getFieldString("QNTY"), ""); 				//수량		
							String strProcUnit 			= StringHelper.evl(dataJr.getFieldString("PROC_UNIT"), ""); 			//식	
							String strSanctionGp		= StringHelper.evl(dataJr.getFieldString("SANCTION_GP"), ""); 		//계획구분	
							
							List dataList = new ArrayList();
					
							dataList.add(strSanctionStepNo);		// 품의차수
							dataList.add(strPayer);				// 소유자(<= 지급자)
							dataList.add(strProcKind);			// 물건의종류
							dataList.add(strSanctionDate);		// 품의일자
							dataList.add(strSpecialNo);			// 주민/사업자번호(<= 지급자) 
							dataList.add(strPayerAdd);			// 소유자주소(<= 지급자주소)
							dataList.add(strCompenMoney);		// 산정금액(<= 보상급액)
							dataList.add(strAddress);			// 소재지
							dataList.add(strPlaceNo);			// 지번
							dataList.add(strCheckNo);			// 조사번호
							dataList.add(strRemark);				// 비고
							dataList.add(strQnty); 				// 수량
							dataList.add(strProcUnit);			// 식
							dataList.add(strUserId);				// 등록자
							dataList.add("");					// 삭제금액
							dataList.add("");					// 구분자
							dataList.add(strSanctionGp);			// 계획구분
							
							iResult =  dao.insertProcPlanInfo(dataList); 
						}
						{
							List dataList = new ArrayList();
							
							dataList.add(strDelSeq);				// 구분자
							dataList.add(strSanctionStepNo);		// 품의차수
							dataList.add(strPayer);				// 소유자(<= 지급자)
							dataList.add(strProcKind);			// 물건의종류
							dataList.add(strCompenMoney);		// 삭제금액
														
							iResult =  dao.insertProcHistInfo_02(dataList); 
							
						}
						/*
						 *	2. 지급자의 신규실적정보 생성
						 */
							iResult = insertProcWrsltInfo(data, strUserId, out);	
						
						/*
						 *	3. 소유자의 계획정보 수정
						 *	   - 이력관리대상임을 표시한다.
						 */
							iResult = dao.updateProcPlanInfo_02(	"Y", 					// 이력관리대상 Flag
															strDelSeq,			// 구분자
															strSanctionStepNo,		// 품의차수
						  							   		strOwner,				// 소유자
						  							  		strProcKind);			// 물건의종류
					}else{
						// 에러파일정보 등록 => 계획정보 존재안함.
						out.println("실패="+"["+strSanctionStepNo+"]"+"["+strOwner+"]"+"["+strProcKind+"]"+"[지장물계획정보 존재안함]");
					}
				}
			}else{
				// 에러파일정보 등록 => 키값 존재안함
				out.println("실패="+"["+strSanctionStepNo+"]"+"["+strOwner+"]"+"["+strProcKind+"]"+"[지장물계획정보 주요키값 항목 존재안함.]");
			}
		}catch(Exception e){ 
			out.println("==========================에러발생11================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		
		return iResult;
	}
	
	/*
	 *	지장물 WRSLT DATA
	 *	- 지장물계획에 따른 실적정보를 등록한다.
	 */ 
	private int insertProcWrsltInfo(JDTORecord data, String sRegister, PrintWriter out){
		
		int iResult = 0;
		
		try{
			//양식순서:품의차수/소유자/지급자/주민/사업자번호/지급자주소/물건의종류/보상금액/지급일자
			String strSanctionStepNo 	= convert(data.getFieldString("COL_0"));				// 품의차수
			String strOwner 			= convert(data.getFieldString("COL_1"));				// 소유자
			String strPayer 			= convert(data.getFieldString("COL_2"));				// 지급자
			String strSpecialNo 			= convert(data.getFieldString("COL_3"));   				// 주민/사업자번호
			String strPayerAdd 			= StringHelper.evl(data.getFieldString("COL_4"), ""); 		// 지급자주소
			String strProcKind 			= convert(data.getFieldString("COL_5"));				// 물건의종류
			String strCompenMoney 		= convertAmt(convert(data.getFieldString("COL_6")));		// 보상금액
			String strPayDate 			= convertDate(convert(data.getFieldString("COL_7")));		// 지급일자
			String strUserId			= sRegister;										// 등록자
			
			logger.println(LogLevel.INFO,"PROC MAIN> 품의차수		="+ strSanctionStepNo+"=");       
			logger.println(LogLevel.INFO,"PROC MAIN> 소유자		="+ strOwner+"=");              
			logger.println(LogLevel.INFO,"PROC MAIN> 지급자		="+ strPayer+"=");              
			logger.println(LogLevel.INFO,"PROC MAIN> 주민/사업번호	="+ strSpecialNo+"=");               
			logger.println(LogLevel.INFO,"PROC MAIN> 지급자주소	="+ strPayerAdd+"=");              
			logger.println(LogLevel.INFO,"PROC MAIN> 보상금액		="+ strCompenMoney+"=");              
			logger.println(LogLevel.INFO,"PROC MAIN> 지급일자		="+ strPayDate+"=");              
			logger.println(LogLevel.INFO,"PROC MAIN> 물건의종류	="+ strProcKind+"=");              
			logger.println(LogLevel.INFO,"PROC MAIN> 등록자		="+ strUserId+"=");              
			
			//엑셀데이타 정합성 체크
			{
				if(strPayDate.length() != 8){
					// 에러파일정보 등록 => 품의일자 정보이상
					out.println("=====>"+"["+strSanctionStepNo+"]"+"["+strOwner+"]"+"["+strProcKind+"]"+"["+strPayDate+"]"+"[지장물계획실적 지급일자정보 이상.]");
				}	
			}
			 
			 if(!"".equals(strSanctionStepNo.trim())){
				 	
				 JDTORecord seqJr = dao.getProcPlanInfo_03(	strSanctionStepNo,	//품의차수
							  						    	strPayer,			//소유자
							  						    	strProcKind);		//물건의종류
				
				String strSeqNum = "";
				if(seqJr != null){
					strSeqNum = StringHelper.evl(seqJr.getFieldString("SEQ_NUM"), "");	//일련번호 		
				}
				
				////////////////////////////////////////////////////////////////////////////////
				JDTORecord dataOrgJr	=  dao.getProcPlanInfo_04(	strSanctionStepNo,	//품의차수
							  						    		strPayer,			//소유자
							  						    		strProcKind);		//물건의종류
									
				if(dataOrgJr != null){
					
					String strOrgAmt 	= StringHelper.evl(dataOrgJr.getFieldString("CAL_AMT"), "0"); 			
					String strOrgMoney	= StringHelper.evl(dataOrgJr.getFieldString("COMPEN_MONEY"), "0"); 
					long OrgCalAmt 	= Long.parseLong(strOrgAmt);		// 계획금액
					long OrgComMny 	= Long.parseLong(strOrgMoney);		// 기 지급금액
					long NewCalAmt 	= Long.parseLong(strCompenMoney);	// 신규 지급금액
					long TotalAmt 		= OrgCalAmt - OrgComMny - NewCalAmt;
					
					if(TotalAmt < 0){
						// 에러파일정보 등록 => 금액정보가 맞지않음
						out.println("=====>"+"["+strSanctionStepNo+"]"+"["+strPayer+"]"+"["+strProcKind+"]"+"[산정금액="+strOrgAmt+"]"+"[기 지급금액"+strOrgMoney+"]"+"[신규 지급금액"+strCompenMoney+"]"); 
						out.println("=====>"+"금액정보가 맞지않음 :"+TotalAmt); 
					}
				}
				////////////////////////////////////////////////////////////////////////////////
									
				List dataList = new ArrayList();
				
			     	dataList.add(strSanctionStepNo);		// 품의차수
				dataList.add(strPayer);				// 지급자
				dataList.add(strProcKind);			// 물건의종류
				dataList.add(strSeqNum);			// 일련번호
				dataList.add(strOwner);				// 소유자
				dataList.add(strSpecialNo);			// 지급자 주민/사업번호
				dataList.add(strPayerAdd);			// 지급자 주소
				dataList.add(strCompenMoney); 		// 보상금액
				dataList.add(strPayDate); 			// 지급일자
				dataList.add(strUserId);				// 등록자
				
				//체크사항 : 이미등록된 실적정보 고려
				iResult =  dao.insertProcWrsltInfo(dataList);
			}else{
				// 에러파일정보 등록 => 키값 존재안함
				out.println("실패="+"["+strSanctionStepNo+"]"+"["+strOwner+"]"+"["+strProcKind+"]"+"[지장물실적정보 주요키값 항목 존재안함.]");
			}
		}catch(Exception e){ 
			out.println("==========================에러발생12================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
		
		return iResult; 
	}
	
	private void updateProcLogMain(String strUserId, PrintWriter out){
		try{
			int iResult = 0;
			
			List delData = dao.getProcPlanList_01();
			
			if(delData.size() > 0)
			{
				for(int inx = 0; inx < delData.size(); inx++)
				{
					JDTORecord dataJr = (JDTORecord) delData.get(inx);
					
					String strOrgSanctionStepNo	= StringHelper.evl(dataJr.getFieldString("SANCTION_STEP_NO"), "");	//품의차수 			
					String strOrgOwner			= StringHelper.evl(dataJr.getFieldString("OWNER"), ""); 			//소유자
					String strOrgProcKind 		= StringHelper.evl(dataJr.getFieldString("PROC_KIND"), "");			//물건의종류
					String strOrgDelSeq 		= StringHelper.evl(dataJr.getFieldString("DEL_SEQ"), "");			//구분자
					String strOrgCalAmt			= StringHelper.evl(dataJr.getFieldString("CAL_AMT"), "0"); 			//산정금액
					
					logger.println(LogLevel.INFO,"PROC LOG> 원품의차수	="+ strOrgSanctionStepNo+"=");              
					logger.println(LogLevel.INFO,"PROC LOG> 원소유자		="+ strOrgOwner+"="); 
					logger.println(LogLevel.INFO,"PROC LOG> 원물건의종류	="+ strOrgProcKind+"=");              
					logger.println(LogLevel.INFO,"PROC LOG> 구분자		="+ strOrgDelSeq+"=");          
					logger.println(LogLevel.INFO,"PROC LOG> 원산정금액	="+ strOrgCalAmt+"=");              
					
					long OrgCalAmt 	= Long.parseLong(strOrgCalAmt);
					long TotalAmt		= OrgCalAmt;
					
					//1. 품의차수,물건의종류,구분자 Y가 아닌 신규등록된 정보를 가져온다.
					
					List newData = dao.getProcPlanList_03(	strOrgDelSeq);
					
					out.println("변경 ="+"["+strOrgSanctionStepNo+"]"+"["+strOrgOwner+"]"+"["+strOrgProcKind+"]"+"["+strOrgCalAmt+"]");  		
												      	
					//2. 신규등록된 정보를 가지고 이력정보를 생성한다.
					for(int iny = 0; iny < newData.size(); iny++)
					{
						JDTORecord newJr = (JDTORecord) newData.get(iny);
						
						String strNewSanctionStepNo	= StringHelper.evl(newJr.getFieldString("SANCTION_STEP_NO"), "");	//품의차수 			
						String strNewOwner		= StringHelper.evl(newJr.getFieldString("OWNER"), ""); 			//소유자
						String strNewProcKind		= StringHelper.evl(newJr.getFieldString("PROC_KIND"), ""); 			//물건의종류	
						String strNewCalAmt		= StringHelper.evl(newJr.getFieldString("CAL_AMT"), "0"); 			//산정금액
						String strNewDelAmt		= StringHelper.evl(newJr.getFieldString("DEL_AMT"), "0"); 			//삭제금액
						
						logger.println(LogLevel.INFO,"PROC LOG> 뉴품의차수		="+ strNewSanctionStepNo+"=");              
						logger.println(LogLevel.INFO,"PROC LOG> 뉴소유자			="+ strNewOwner+"=");              
						logger.println(LogLevel.INFO,"PROC LOG> 뉴물건의종류		="+ strNewProcKind+"=");              
						logger.println(LogLevel.INFO,"PROC LOG> 뉴산정금액		="+ strNewCalAmt+"=");              
						logger.println(LogLevel.INFO,"PROC LOG> 뉴삭제금액		="+ strNewDelAmt+"=");              
						
						long NewCalAmt = 0;
						
						NewCalAmt = Long.parseLong(strNewDelAmt);
						
						TotalAmt = TotalAmt - NewCalAmt;
						
						logger.println(LogLevel.INFO,"PROC LOG> 산정금액 계산 ="+ TotalAmt+"=");              
						
						JDTORecord seqJr = dao.getProcPlanInfo_02(	strNewOwner,			//지급자
																strOrgSanctionStepNo,	//품의차수
									  						    	strOrgOwner,			//소유자
									  						    	strOrgProcKind);		//물건의종류
									  						    	
						
						String strSeqNum = "";
						if(seqJr != null){
							strSeqNum = StringHelper.evl(seqJr.getFieldString("SEQ_NUM"), "");	//일련번호 		
						}
						
						iResult = dao.insertProcHistInfo(	strNewOwner,			//지급자
													strSeqNum,			//일련번호
					 								strUserId,				//등록자
					 								strOrgSanctionStepNo,	//품의차수
						  						    	strOrgOwner,			//소유자
						  						    	strOrgProcKind,		//물건의종류
						  						    	strNewDelAmt);		//지급금액	
						
						out.println("=====>"+"["+strNewSanctionStepNo+"]"+"["+strNewOwner+"]"+"["+strNewProcKind+"]"+"["+strNewCalAmt+"]"); 
					}
					
					logger.println(LogLevel.INFO,"PROC LOG> 산정금액 계산결과 ="+ TotalAmt+"=");              
					//3. 오리지널 정보를 삭제한다.
					if(TotalAmt == 0){
						
						iResult = dao.deletetProcPlanInfo(	strOrgSanctionStepNo,	//품의차수
						  						    	strOrgOwner,			//소유자
						  						    	strOrgProcKind);		//물건의종류
						  						  	
					}else if(TotalAmt > 0){
						//원소유주의 히스토리정보를 생성한다.
						JDTORecord seqJr = dao.getProcPlanInfo_02(	strOrgOwner,			//지급자
																strOrgSanctionStepNo,	//품의차수
									  						    	strOrgOwner,			//소유자
									  						    	strOrgProcKind);		//물건의종류
						
						String strSeqNum = "";
						if(seqJr != null){
							strSeqNum = StringHelper.evl(seqJr.getFieldString("SEQ_NUM"), "");	//일련번호 		
						}
						
						
						iResult = dao.insertProcHistInfo(	strOrgOwner,			//지급자
													strSeqNum,			//일련번호
					 								strUserId,				//등록자
					 								strOrgSanctionStepNo,	//품의차수
						  						    	strOrgOwner,			//소유자
						  						    	strOrgProcKind,		//물건의종류
						  						    	TotalAmt+"");			//지급금액	
						//원소유주의 산정금액을 수정한다.
						iResult = dao.updateProcPlanInfo_03(	TotalAmt+"", 				// 산정금액
														strOrgSanctionStepNo,		// 품의차수
						  							   	strOrgOwner,				// 소유자
						  							  	strOrgProcKind);			// 물건의종류  						    	
					}else if(TotalAmt < 0){
						// 에러파일정보 등록 => 금액정보가 맞지않음
						out.println("=====>"+"금액정보가 맞지않음 :"+TotalAmt); 
					}
				}
				
				iResult = dao.updateProcPlanInfo_04();
				iResult = dao.deletetProcHistInfo();
			}
		
		}catch(Exception e){ 
			out.println("==========================에러발생13================================");
			out.println(e);
			throw new EJBServiceException(e);
		}
	}
		
	private String convert(String strVal)
	{
		strVal = StringHelper.replaceStr(StringHelper.evl(strVal, "")," ","");
		strVal = StringHelper.replaceStr(strVal,"\n","");
		strVal = StringHelper.replaceStr(strVal,"\r","");
		strVal = StringHelper.replaceStr(strVal,"\n\r","");
		
		return strVal;
	}
	
	private String convertAmt(String strVal)
	{
		strVal = StringHelper.replaceStr(StringHelper.evl(strVal, ""),"-","");
		strVal = StringHelper.replaceStr(StringHelper.evl(strVal, ""),",","");
		
		return strVal;
	}
	
	private String convertDate(String strVal)
	{
		strVal = StringHelper.replaceStr(StringHelper.evl(strVal, ""),"-","");
		strVal = StringHelper.replaceStr(StringHelper.evl(strVal, ""),"/","");
		strVal = StringHelper.replaceStr(StringHelper.evl(strVal, ""),".","");
		
		return strVal;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 개인의 토지보상 계획금액 합계 검색 쿼리 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public JDTORecord getLandPlanPersonInfo(String queryID, String s_owner) {
		LandDAO landDAO = null;	    
		JDTORecord personInfo = null;
	    try{
	    	landDAO = new LandDAO();
	    	personInfo = landDAO.requestgetData(queryID,new Object[]{s_owner});
	    	return personInfo;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}	
	

	/**
	 * 오퍼레이션명 : 
	 *
	 * 개인의 토지보상 계획 개인별 부지매입 검색 쿼리 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public List getLandPlanPlanInfo(String queryID, String s_owner) {
		LandDAO landDAO = null;	    
	    List landList = null;
	    try{
	    	landDAO = new LandDAO();
	    	landList = landDAO.getListData(queryID,new Object[]{s_owner});
	    	return landList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 개인의 토지보상 계획 주간보고서 검색 쿼리 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public List getLandPlanWeeklyInfo(String queryID, String s_date) {
		LandDAO landDAO = null;	    
	    List landList = null;
	    try{
	    	String tmpDate = s_date.substring(0, 4) + s_date.substring(5, 7) + s_date.substring(8, 10);
	    	landDAO = new LandDAO();
	    	landList = landDAO.getListData(queryID,new Object[]{tmpDate, tmpDate, tmpDate, tmpDate,tmpDate, tmpDate, tmpDate, tmpDate});
	    	return landList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	

	/**
	 * 오퍼레이션명 : 
	 *
	 * 개인별보상내역 검색 쿼리 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public List getCompensationPlanInfo(String queryID, String s_owner) {
		LandDAO landDAO = null;	    
	    List landList = null;
	    try{
	    	landDAO = new LandDAO();
	    	landList = landDAO.getListData(queryID,new Object[]{s_owner, s_owner});
	    	return landList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}	
}	