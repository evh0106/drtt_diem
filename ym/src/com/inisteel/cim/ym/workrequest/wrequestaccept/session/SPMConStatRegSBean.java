package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import java.util.ArrayList;
import java.util.List;

import java.rmi.RemoteException;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.po.YMPO161;
import com.inisteel.cim.common.jms.model.ym.POYM004;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="SPMConStatRegEJB" jndi-name="JNDISPMConStatReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SPMConStatRegSBean extends BaseSessionBean { 
	Logger logger=null;
	private YmCommDAO commDao = new YmCommDAO();
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger = new Logger(config);		
	}

	

    /**
	 * 보급요구  JDTORecord 방식으로 처리
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
 
	public boolean receiveSPMConStat2(JDTORecord msgRecord ) throws JDTOException  {
        logger.println(LogLevel.INFO,this,"보급요구  JDTORecord 방식으로 처리 Start");
		boolean isSuccess = false;
		boolean isConvSub = false;
		JDTORecord   outRecord   	= JDTORecordFactory.getInstance().create();
		String sRTN_CD	="";
		
        try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			logger.println(LogLevel.DEBUG,this, "Start-receiveSPMConStat()");
			
			YdStockDAO ydStockDAO = new YdStockDAO();
			
			int iResult = 0;
			String YardID    		= YmCommonUtil.paraRecChkNull(msgRecord, "YardId");
			String WorkID    		= YmCommonUtil.paraRecChkNull(msgRecord, "WorkId"); 
			String ProcessID 		= YmCommonUtil.paraRecChkNull(msgRecord, "ProcessId");
			String CoilNo    		= YmCommonUtil.paraRecChkNull(msgRecord, "CoilNo");
			String Position  		= YmCommonUtil.paraRecChkNull(msgRecord, "Position");
			String TakeOutProcess  	= YmCommonUtil.paraRecChkNull(msgRecord, "TakeOutProcess");
			
			LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "POYM004 수신");
			
			/**
			 * @param YardID : 야드구분
			 * @param WorkID : S SPM, H HFL , D HFL 결속대
			 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In 
			 * @param CoilNo :  'S' Scrap  'H' A열연 'K' B열연 byCGS
			 */
			
			//HFL 결속대 프로세스##################################################################
			if(WorkID.equals("D")){
				outRecord = this.receiveHFLConStat(msgRecord);
				sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
				
				if (!("1".equals(sRTN_CD))) {		        	
					return isSuccess;
				}
				
				return true;
			}
			//###################################################################################
			
			
			// 조업 Level-3에서 SPM, HFL 구분
			// 공정에 따라 분기. ProcessID기준.
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                           // 보급
				// 소재가 야드에 있을 경우 true, 소재가 그외 위치에 있을 경우엔 false 반환 
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 보급 isSuccess =" + isSuccess);
				
				if (isSuccess){	
					
//					/*
//					 * [기능 추가 : (2009.02.10 KBK)]
//					 * 현재 보급하고자 하는 코일이 1단에 위치할 경우
//					 * 상단(2단)의 코일이 정정 작업인지 체크
//					 */
//					logger.println(LogLevel.DEBUG,this,"상단의 코일 검사 = "+CoilNo);
//					// CGS
//					// 보조작업이지만 주작업 가능한 코일번호를 반환한다.
//					List sCoilNoList = checkPoJungWorkExist(CoilNo);
//					
//					logger.println(LogLevel.DEBUG,this,"sCoilNoList Size="+sCoilNoList.size());
//					logger.println(LogLevel.DEBUG,this,"sCoilNoList "+sCoilNoList);
//					
//					// CGS 추가 
//					// checkPoJungWorkExist() 결과  보급요구 소재가 2단에 적치된 경우는 List size 가 0
//					//                             보급요구 소재가 1단에 적치된 경우는 List size 가 최소 1이상이다.
//					
//					// 공통코일정보 테이블에서 보급 요구된 코일정보
//					String queryID      = "ym.common.dao.selectCommonCoilInfo";
//					JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
//					
//					for (int i =0; i < sCoilNoList.size() ; i++) 
//					{					
//						JDTORecord sCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ sCoilNoList.get(i) });
//						// 보급요구된 저장품의 두께, 폭,
//						
//						//int mCoilThickness = mCommonCoilinfo.getFieldInt("COIL_T");
//						//int mCoilWidth = mCommonCoilinfo.getFieldInt("COIL_W");
//						
//						float fCoilThickness = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_T"));
//						float fCoilWidth     = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_W"));
//						
//						// 시스템 판단 코일소재의 두께, 폭
//						//int sCoilThickness = sCommonCoilinfo.getFieldInt("COIL_T");
//						//int sCoilWidth = sCommonCoilinfo.getFieldInt("COIL_W");
//						float fCoilThickness_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_T"));
//						float fCoilWidth_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_W"));
//						
//						
//						// CGS
//						// 코일두께와 폭을 비교해서 허용 오차 범위에 있을 경우에만 보급.
//						// 오차범위 기준은 임시로 지정. 테스트시 정확한 기준을 받아서 수정한다.
//						String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilInfoStd";
////						JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"X2"});
//						JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"S"});
//						
//						// 입력 기준 데이터가 float 형이다. 소숫점 1자리까지.최규성 2009-12-02
//						int nStdThick = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
//						int nStdWidth = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
//						
//						
//						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
//						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
//						
//						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= nStdThick )  
//								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= nStdWidth) 
//							   )	
///*
//						float fStdThick = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
//						float fStdWidth = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
//						
//						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
//						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
//						
//						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= fStdThick )  
//								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= fStdWidth) 
//							   )	
//*/
//						{
//							// CGS 추가 
//							// 스케줄 호출하기전 To위치에 대한 정보를 검사한다.
//							
//							isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), (String)sCoilNoList.get(i));
//							LogMgProcess(YardID, WorkID, ProcessID, (String)sCoilNoList.get(i), "보급작업예약 편성완료-주/보조 구분");
//							logger.println(LogLevel.DEBUG,this,"보급작업예약 편성완료-주/보조 구분[" +(String)sCoilNoList.get(i)+ "]");
//							
//							if (isSuccess) isConvSub = true;
//						}
//					}	// for END

					// CGS 추가
					// 요구된 코일에 대한 작업예약 및 스케줄 생성한다.
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "요구된 보급 작업예약 편성완료");
					logger.println(LogLevel.DEBUG,this,"요구된 보급 작업예약 편성완료 ["+CoilNo+"]" );
					//isSuccess = SPMHFLLineIn(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
//					isSuccess = SPMHFLLineInRequestSearch(YardID.trim(), WorkID.trim(), ProcessID.trim());
			    }
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){                     // 취소
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 보급 취소 CoilNo="+CoilNo );
				
				//코일 번호로 크레인 스케줄을 가져 온다.
				String queryID      = "ym.common.dao.selectSchCoilInfo";
				JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
				
				if(mCommonCoilinfo.size()>0){
					String sCH_ID = mCommonCoilinfo.getFieldString("SCH_ID");
					
					//권상이전 크레인 스케줄/작업예약  취소 작업
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
					Boolean resultRes = (Boolean) ejbConn1.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {CoilNo});
				
				}else{
					logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 권상 이후에는 취소가 불가능 합니다." );
				}
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){                     // 추출
				
				
				//**************************보관매출 체크(보관매출인경우 추출요구 시 자동으로 실적 발생 후 추출 처리******************************정종균
				
				ymCommonDAO dao = ymCommonDAO.getInstance();
				
				String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilKeepstockInfo";
				List 	list = dao.getCommonList(sQueryIdStd,new Object[]{CoilNo.trim() });	
				
				if(list.size()> 0){
					//정정실적 처리 
					String Workchk="";
					if(WorkID.equals("H")){
						Workchk="HFL";
					} else {
						Workchk="SPM";
					}
					
	
					logger.println(LogLevel.DEBUG,this, ">>★★★★★★★★★보관매출 자동정정실적처리>>>" + CoilNo.trim());
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICoilInfoReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("setInnerIFCoilInfo_02",
												new  Class[]{String.class,String.class},
												new Object[]{ CoilNo.trim() ,Workchk});					 
					
				}
				//***************************************************************************************************************
				
				
				// SPM/HFL 여부 검사
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 추출 isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "추출 작업예약 편성완료");
				}
									
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){                     // Take-Out
				isSuccess = callTakeOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim(), TakeOutProcess.trim());
				LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-Out 작업예약 편성완료");
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                     // Take-In
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< Take-In isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-In 작업예약 편성완료");
				}
			}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveSPMConStat(POYM004)");
			
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
 	/**
	 * 오퍼레이션명 : 
	 *
	 *  조업 LEVEL3로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
     *  전문내용을 JDTORecord로 파싱한다.
     *  업무 로직
     *	1.TC_CD - POYM004 (I/F ID : YM-LIF-020 )
     *	2.조업 LEVEL3로부터 SPM / HFL 작업 요구 정보를 수신
     *
     *    조업에서 수신한 위치정보를 야드 적치열에 대한 위치로 변환 해야됨
     *    조업에서는 Saddle No로 정의 하고 있음.
     *    수신한 위치 정보는 Saddle No로 되어 있으니 각동에 위치한 Saddle No를 파악해서
     *    야드 Map 적치열에 맞게 변환할것.
     * 
     * A열연 EQL
     * 1. 보급(Line In)  : 적치열(1EQE01), Schedule Code(EQLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 2. Take-In       : 적치열(1EQE01), Schedule Code(EQTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 3. Take-Out      : 적치열(1EQE01), Schedule Code(EQTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 4. Take-Out      : 적치열(1GQD01), Schedule Code(EQTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 5. 추출(Line Off) : 적치열(1GQD01), Schedule Code(EQLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * A열연 SPM
     * 1. 보급(Line In)  : 적치열(1DKE01), Schedule Code(CKLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 2. Take-In       : 적치열(1EKE01), Schedule Code(CKTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 3. Take-Out      : 적치열(1EKE01), Schedule Code(CKTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 4. Take-Out      : 적치열(1EKD01), Schedule Code(CKTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 5. 추출(Line Off) : 적치열(1FKD01), Schedule Code(CKLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * B열연 SPM
     * 1. 보급(Line In)  : 적치열(3CKE01), Schedule Code(CKLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 2. Take-In       : 적치열(3BKE01), Schedule Code(CKTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 3. Take-Out      : 적치열(3BKE01), Schedule Code(CKTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 4. Take-Out      : 적치열(3BKD01), Schedule Code(CKTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경) 
     * 5. 추출(Line Off) : 적치열(3AKD01), Schedule Code(CKLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 
     * A열연 HFL
     * 1. 보급(Line In)  : 적치열(1BFE01), Schedule Code(CFLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 2. Take-In       : 적치열(1BFE01), Schedule Code(CFTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 3. Take-Out      : 적치열(1BFE01), Schedule Code(CFTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 4. Take-Out      : 적치열(1CFD01), Schedule Code(CFTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 5. 추출(Line Off) : 적치열(1CFD01), Schedule Code(CFLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * B열연 HFL
     * 1. 보급(Line In)  : 적치열(3AFE01), Schedule Code(CFLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 2. Take-In       : 적치열(3BFE01), Schedule Code(CFTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 3. Take-Out      : 적치열(3BFE01), Schedule Code(CFTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 4. Take-Out      : 적치열(3BFD01), Schedule Code(CFTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 5. 추출(Line Off) : 적치열(3CFD01), Schedule Code(CFLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
     * 
     * 
     *  << SPM 설비 현황 >>
     * 1. A열연 SPM
     *      보급(Line In)  : 적치열(1DKE01), Schedule Code(CSLI), 저장품이동조건(D2)
     *      추출(Line Off) : 적치열(1FKD01), Schedule Code(CSLO), 저장품이동조건(D3)
     *    B열연 SPM
     *      보급(Line In)  : 적치열(3CKE01), Schedule Code(CSLI), 저장품이동조건(D2)
     *      추출(Line Off) : 적치열(3AKD01), Schedule Code(CSLO), 저장품이동조건(D3)
     *   
     *    1-1.수신한 Coil No가 저장품(TB_YM_STOCK) Table 에 존재하는지 점검
     *    1-2.저장품 Table에 작업예약_ID가 존재한다면 Skip(Error)
     *    1-3.적치단(TB_YM_STACKLAYER) Table Read
     *    1-4.적치단 Table에 적치단상태(STACK_LAYER_STAT CHAR(1)) 작업요구상태='S'로 변경처리
     *    1-5.작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
     *    1-6.작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시)
     *    1-7.저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
     *    1-8.Coil Schedule EJB를 Call 한다.
     * 
     * 2. 보급취소
     *    2-1. 야드 화면에서 보급취소해서 조업으로 보급취소 전문을 송신한다. 2006.1.13 
     *       
     * 3. Take-In
     *    3-1.수신한 Coil No가 저장품(TB_YM_STOCK) Table 에 존재하는지 점검
     *    3-2.저장품 Table에 작업예약_ID가 존재한다면 Skip(Error)
     *    3-3.적치단(TB_YM_STACKLAYER) Table Read
     *    3-4.적치단 Table에 적치단상태(STACK_LAYER_STAT CHAR(1)) 작업요구상태='S'로 변경처리
     *    3-5.작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
     *    3-6.작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시)
     *    3-7.저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
     *    3-8.Coil Schedule EJB를 Call 한다.
     *  
     * 4. Take-Out
     *    4-1.Take-Out은 입측 또는 출측 전체에서 일어 날수 있다.
     *    4-2.먼저 Take-Out 요구가 일어나면 입측 2개, 출측 2개의 설비번지에서 검색을 한다.
     *    4-3.요구 발생 해당동에 대상재가 없다면 Take-Out 요구가 일어나는 위치로 옮겨서 정보 처리한다.      
     *    4-4.옮길때는 먼저 다른동의 설비번지에 있는 대상재를 Read 한다.
     *    4-5.옮겨야할 설비번지로 Insert한다.
     *    4-6.다른동의 설비번지에서 대상재를 Delete한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSPMConStat(POYM004 pOYM004) { 
		
		boolean isSuccess = false;
		boolean isConvSub = false;
		JDTORecord   outRecord   	= JDTORecordFactory.getInstance().create();
		String sRTN_CD	="";
		JDTORecord msgRecord     	= JDTORecordFactory.getInstance().create(); // 
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveSPMConStat()");
			
			YdStockDAO ydStockDAO = new YdStockDAO();
			
			int iResult = 0;
			
			String YardID    = pOYM004.getYardId();
			String WorkID    = pOYM004.getWorkId(); 
			String ProcessID = pOYM004.getProcessId();
			String CoilNo    = pOYM004.getCoilNo();
			String Position  = pOYM004.getPosition();  // 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String TakeOutProcess  = pOYM004.getTakeOutProcess();  // Take-Out시 1:결번, 2:임시보류처리(잠시 내려놨다가 Take-In할 Coil) 
			
			LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "POYM004 수신");
			
			
			//HFL 결속대 프로세스##################################################################
			if(WorkID.equals("D")){
				
				msgRecord.setField("YardID" , YardID);	
				msgRecord.setField("WorkId" , WorkID);	
				msgRecord.setField("ProcessId" , ProcessID);	
				msgRecord.setField("CoilNo" , CoilNo);	
				msgRecord.setField("Position" , Position);	
				msgRecord.setField("TakeOutProcess" , TakeOutProcess);	
				
				
				outRecord = this.receiveHFLConStat(msgRecord);
				sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
				
				if (!("1".equals(sRTN_CD))) {		        	
					return isSuccess;
				}
				
				return true;
			}
			//###################################################################################
			
			/**
			 * @param YardID : 야드구분
			 * @param WorkID : S SPM, H HFL ,E EQ1
			 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In 
			 * @param CoilNo :  'S' Scrap  'H' A열연 'K' B열연 byCGS
			 */
			
			// 조업 Level-3에서 SPM, HFL 구분
			// 공정에 따라 분기. ProcessID기준.
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                           // 보급
				// 소재가 야드에 있을 경우 true, 소재가 그외 위치에 있을 경우엔 false 반환 
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 보급 isSuccess =" + isSuccess);
				
				if (isSuccess){	
					
//					if(!WorkID.equals("E")){					
//					//////////////////////////////////////////////////////////////////////////////////////////
//						/*
//						 * [기능 추가 : (2009.02.10 KBK)]
//						 * 현재 보급하고자 하는 코일이 1단에 위치할 경우
//						 * 상단(2단)의 코일이 정정 작업인지 체크
//						 */
//						logger.println(LogLevel.DEBUG,this,"상단의 코일 검사 = "+CoilNo);
//						// CGS
//						// 보조작업이지만 주작업 가능한 코일번호를 반환한다.
//						List sCoilNoList = checkPoJungWorkExist(CoilNo);
//						
//						logger.println(LogLevel.DEBUG,this,"sCoilNoList Size="+sCoilNoList.size());
//						logger.println(LogLevel.DEBUG,this,"sCoilNoList "+sCoilNoList);
//						
//						// CGS 추가 
//						// checkPoJungWorkExist() 결과  보급요구 소재가 2단에 적치된 경우는 List size 가 0
//						//                             보급요구 소재가 1단에 적치된 경우는 List size 가 최소 1이상이다.
//						
//						// 공통코일정보 테이블에서 보급 요구된 코일정보
//						String queryID      = "ym.common.dao.selectCommonCoilInfo";
//						JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
//						
//						for (int i =0; i < sCoilNoList.size() ; i++) 
//						{					
//							JDTORecord sCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ sCoilNoList.get(i) });
//							// 보급요구된 저장품의 두께, 폭,
//							
//							//int mCoilThickness = mCommonCoilinfo.getFieldInt("COIL_T");
//							//int mCoilWidth = mCommonCoilinfo.getFieldInt("COIL_W");
//							
//							float fCoilThickness = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_T"));
//							float fCoilWidth     = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_W"));
//							
//							// 시스템 판단 코일소재의 두께, 폭
//							//int sCoilThickness = sCommonCoilinfo.getFieldInt("COIL_T");
//							//int sCoilWidth = sCommonCoilinfo.getFieldInt("COIL_W");
//							float fCoilThickness_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_T"));
//							float fCoilWidth_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_W"));
//							
//							
//							// CGS
//							// 코일두께와 폭을 비교해서 허용 오차 범위에 있을 경우에만 보급.
//							// 오차범위 기준은 임시로 지정. 테스트시 정확한 기준을 받아서 수정한다.
//							String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilInfoStd";
//	//						JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"X2"});
//							JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"S"});
//							
//							// 입력 기준 데이터가 float 형이다. 소숫점 1자리까지.최규성 2009-12-02
//							int nStdThick = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
//							int nStdWidth = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
//							
//							
//							logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
//							logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
//							
//							if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= nStdThick )  
//									&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= nStdWidth) 
//								   )	
//	/*
//							float fStdThick = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
//							float fStdWidth = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
//							
//							logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
//							logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
//							
//							if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= fStdThick )  
//									&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= fStdWidth) 
//								   )	
//	*/
//							{
//								// CGS 추가 
//								// 스케줄 호출하기전 To위치에 대한 정보를 검사한다.
//								
//								isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), (String)sCoilNoList.get(i));
//								LogMgProcess(YardID, WorkID, ProcessID, (String)sCoilNoList.get(i), "보급 보조작업예약 편성완료-주/보조 구분");
//								logger.println(LogLevel.DEBUG,this,"보급 보조작업예약 편성완료-주/보조 구분[" +(String)sCoilNoList.get(i)+ "]");
//								
//								if (isSuccess) isConvSub = true;
//							}
//						}	// for END
//					}//////////////////////////////////////////////////////////////////////////////////////////
					
					
					
					// CGS 추가
					// 요구된 코일에 대한 작업예약 및 스케줄 생성한다.
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "요구된 보급 작업예약 편성완료");
					logger.println(LogLevel.DEBUG,this,"요구된 보급 주작업예약 편성완료 ["+CoilNo+"]" );


//					if(!WorkID.equals("E")){
//						isSuccess = SPMHFLLineInRequestSearch(YardID.trim(), WorkID.trim(), ProcessID.trim());
//					}
			    }
			
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){                     // 취소
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 보급 취소 CoilNo="+CoilNo );
				
				//코일 번호로 크레인 스케줄을 가져 온다.
				String queryID      = "ym.common.dao.selectSchCoilInfo";
				JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
				
				if(mCommonCoilinfo.size()>0){
					String sCH_ID = mCommonCoilinfo.getFieldString("SCH_ID");
					
					//권상이전 크레인 스케줄/작업예약  취소 작업
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
					Boolean resultRes = (Boolean) ejbConn1.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {CoilNo});
				
				}else{
					logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 권상 이후에는 취소가 불가능 합니다." );
				}
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){                     // 추출
				
				if(!WorkID.equals("E")){
				//**************************보관매출 체크(보관매출인경우 추출요구 시 자동으로 실적 발생 후 추출 처리******************************정종균
				
				ymCommonDAO dao = ymCommonDAO.getInstance();
				
				String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilKeepstockInfo";
				List 	list = dao.getCommonList(sQueryIdStd,new Object[]{CoilNo.trim() });	
				
				if(list.size()> 0){
					//정정실적 처리 
					String Workchk="";
					if(WorkID.equals("H")){
						Workchk="HFL";
					} else {
						Workchk="SPM";
					}
					
	
					logger.println(LogLevel.DEBUG,this, ">>★★★★★★★★★보관매출 자동정정실적처리>>>" + CoilNo.trim());
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICoilInfoReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("setInnerIFCoilInfo_02",
												new  Class[]{String.class,String.class},
												new Object[]{ CoilNo.trim() ,Workchk});					 
					
				}
				//***************************************************************************************************************
				}
				
				// SPM/HFL 여부 검사
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 추출 isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "추출 작업예약 편성완료");
				}
									
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){                     // Take-Out
				isSuccess = callTakeOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim(), TakeOutProcess.trim());
				LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-Out 작업예약 편성완료");
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                     // Take-In
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< Take-In isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-In 작업예약 편성완료");
				}
			}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveSPMConStat(POYM004)");
			
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
//	        throw new EJBServiceException(e);
	        throw new EJBServiceException("=보급요구 입측존에 코일이 존재 합니다.");
	    }
	}
	
	/**
	 * 대상 Coil이 보급, 추출, Take-Out일때 설비위에 적치되어 있는지 야드에 적치되어 있는지 점검해서
	 * False 처리한다. 
	 * @param YardID : 야드구분
	 * @param WorkID : S SPM, H HFL
	 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * @param CoilNo
	 * @return
	 */
	private boolean SpmHflProcessCheck(String YardID, String WorkID, String ProcessID, String CoilNo){
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	logger.println(LogLevel.DEBUG,this, "Start-SpmHflProcessCheck()");
	    	
	    	logger.println(LogLevel.DEBUG,this, "para:YardID:    "+YardID);
	    	logger.println(LogLevel.DEBUG,this, "para:WorkID:    "+WorkID);
	    	logger.println(LogLevel.DEBUG,this, "para:ProcessID: "+ProcessID);
	    	logger.println(LogLevel.DEBUG,this, "para:CoilNo:    "+CoilNo);
	    	
	    	
	    	// CGS 추가
	    	// 스크랩에 대한 처리를 따로 추가한다.
	    	String sEmptyStackCol = "";
	    	String sEmptyStackBed = "";
	    	JDTORecord jtrStackInfoV = null;
	    	if (CoilNo.substring(0, 1).equals("S") && YardID.equals("3")) {
				// 스크랩 처리 방법 정리
	    		// 3AKD01이 비어 있는지 검사.
	    		logger.println(LogLevel.DEBUG,this, ">>>> Scrap처리 시작 :    "+CoilNo);

	    		//===================================================================================
				// 최규성 추가 2009-11-26
				// 사용하지 않음. 주석 처리 2009-11-30
				// SCRAP 처리 전. 아직 작업완료하지 않고 스케줄 UP상태에 존재하는 스케줄 정보는 삭제한다.
	    		/**
					1. Stock TBL 에서 WBOOK_ID를 가진 스크랩 저장품이 있는지 검사.
					2. WBOOK_ID로 생성되어 있는 스케줄 검사. (SCH TBL)
						2-1. STOCK TBL 의 WBOOK_ID 를 제거한다.
					3. 스케줄이 존재하면 SCH 취소 메소드를 호출한다. CraneSchReg.cancelCoilSchInfo()
					4. WBOOK TBL에서 작업예약정보를 삭제한다.
					5. 저장품MAP[StackLayer TBL]의 중복 위치를 검사한다.
				*/
//				boolean bCheckScrap = checkScrapInfoInFacility(YardID, WorkID, ProcessID, CoilNo);
//				if( bCheckScrap){
//					logger.println(LogLevel.DEBUG,this, ">>>> checkScrapInfoInFacility() true    ");
//				}else{
//					logger.println(LogLevel.DEBUG,this, ">>>> checkScrapInfoInFacility() false    ");
//				}
	    		//===================================================================================
	    		
	    		// 설비의 적치열/대/단을 검사한다.
	    		// 추출 위치에 비어있는 적치열이 있는지 검사한다.
	    		// 출측 위치는 STACK_COL_GP_3BKD01, STACK_COL_GP_3AKD01 이지만
	    		// STACK_COL_GP_3AKD01만 비어 있는지 검사한다.
	    		
	    		String sQueryId_checkstack = "ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO.getListBCSPMTrkCheck";
	    		/*
					SELECT A.STACK_COL_GP AS 적치열, B.STACK_BED_GP AS 적치대, B.STACK_LAYER_GP AS 적치단
					  FROM TB_YM_STACKCOL A , TB_YM_STACKLAYER B 
					 WHERE A.YD_GP = '3'         --야드구분(3)
					   AND A.STACK_COL_GP IN ('3BKD01','3AKD01')  -- 적치열
					   AND A.STACK_COL_GP = B.STACK_COL_GP
					   AND B.STOCK_ID IS NULL
					 ORDER BY A.STACK_COL_USAGE_CD DESC, A.STACK_COL_GP DESC, B.STACK_BED_GP
	    		*/
	    		List listCheckStack = ydStackLayerDAO.requestgetListData(sQueryId_checkstack, new Object[] {YmCommonConst.STACK_COL_GP_3AKD01,YmCommonConst.STACK_COL_GP_3AKD01});
	    		
	    		
	    		// listCheckStack.size() == 0 이면 Stack_Bed 생성하고 GBN_MAX 조건으로 MAX 적치대+1 하고 그곳에 코일번호 저장.
	    		// listCheckStack.size() >  0 이면 해당 Stack_Bed에 ScrapNo 생성.	: 쿼리에서 정렬을 하기 때문에 0번째 인덱스의 적치열만 가지고 정보를 처리한다.
	    		if (listCheckStack.size() == 0 )
	    		{
	    			logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<<ScrapNo["+CoilNo.trim()+"] listCheckStack 데이터 없음. ");
	    			// STACK_COL_GP_3AKD01 위치에 비어 있는 적치대/단이 없으므로 
	    			// 최대 단(MAXBED)에 +1 하고 그 위치에 ScrapNo를 생성한다.  
	    			// >>> 적치 위치는 항상 01로 한다. 소재가 존재할 경우 shift로 처리한 후 소재 생성.
	    			//int nSeq = YmCommonDB.insertConveyorInfo(YmCommonConst.STACK_COL_GP_3AKD01,CoilNo.trim(),YmCommonConst.GBN_MAX);
	    			
	    			int nSeq = YmCommonDB.insertConveyorInfo(YmCommonConst.STACK_COL_GP_3AKD01,CoilNo.trim(),YmCommonConst.GBN_MIN);
	    			logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<<ScrapNo["+CoilNo.trim()+"] 적치 대/단 수정함.");
	    		}else	// 빈 적치공간이 있는 경우 처리된다.
	    		{
	    			// 비어 있는 적치 열/대 정보를 가져온다.
//	    			for(int i=0;i<listCheckStack.size();i++)
//	    			{
//	    				jtrStackInfoV = (JDTORecord)listCheckStack.get(i);
	    				jtrStackInfoV = (JDTORecord)listCheckStack.get(0);
	    				sEmptyStackCol = StringHelper.evl(jtrStackInfoV.getFieldString("적치열"),""	 );
	    				sEmptyStackBed = StringHelper.evl(jtrStackInfoV.getFieldString("적치대"), "");
	    				
		    			// 수신받은 코일 정보를 테이블에 저장한다.
		    			// tb_YM_stacker, tb_Ym_stacklayer 테이블에 정보를 수정한다.
		    			String sQueryId_update = "";
		    			/*
		    			UPDATE TB_YM_STACKER
						SET STOCK_ID = ?
						,STACK_LAYER_STAT = 'L'
						,MODIFIER = 'SYSTEM'
						,MOD_DDTT = SYSDATE
						WHERE STACK_COL_GP = ?
						AND STACK_BED_GP = ?
		    			*/
		    			//String sScrapQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateScrapStacker";
				    	//int nRetVal = ydStackLayerDAO.requestupdateData(sScrapQueryId, new Object[] {CoilNo.trim(),sEmptyStackCol,sEmptyStackBed});
				    	logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<<ScrapNo["+CoilNo.trim()+"] 적치 대 수정함.");
				    	/*
				    	UPDATE TB_YM_STACKLAYER
						SET STOCK_ID = ?
						,STACK_LAYER_STAT = 'L'
						,MODIFIER = 'SYSTEM'
						,MOD_DDTT = SYSDATE
						WHERE STACK_COL_GP = ?
						AND STACK_BED_GP = ?
				    	*/
				    	String sQueryId_Scrap = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateScrapStackLayer";
				    	int nRetVal = ydStackLayerDAO.requestupdateData(sQueryId_Scrap, new Object[] {CoilNo.trim(),sEmptyStackCol,sEmptyStackBed});
				    	logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<<ScrapNo["+CoilNo.trim()+"] 적치 단 수정함.");
			    	
//	    			}
	    		}
	    		/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
	             * 저장품 Table에 작업예약_ID가 존재한다면 Error
	             * Select STOCK_ID, WBOOK_ID,STOCK_MOVE_TERM from TB_YM_STOCK Where STOCK_ID = ?
	             */ 
				
				// Scrap일 경우에는 Stock Table을 검사하지 않는다. CGS
				// 기존 소스를 최소한 수정하기 위해서 Scrap CoilNo일 경우에
				// TB_YM_STOCK에 Scrap에 대한 정보를 입력한다. INSERT

				// Scrap에 대한 데이터를 저장한다. 
				// 조업에서 정보를 올려 줄 수 없다.(이성진)
				// Scrap 추출요구시 일부 데이터를 Stock 테이블에 입력한다. 수동입력함.CGS
				/*
				 INSERT INTO TB_YB_STOCK(
									STOCK_ID, --저장품ID
									STOCK_ITEM,--저장품 품목  
									STOCK_MOVE_TERM, --저장품이동조건
									REGISTER,  -- 'SYSTEM'
									REG_DDTT,  -- SYSDATE
									MODIFIER,   -- 'SYSTEM'
									MOD_DDTT, -- SYSDATE
									DEL_YN
									)
				  VALUES ( :CoilNo,'CM','A2','SYSTEM',SYSDATE,'SYSTEM',SYSDATE,'N') 
				*/
				logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<< ScrapNo[" + CoilNo.trim() + "]에 대한 정보를 수동으로 저장함.TB_YM_STOCK" );
				String QueryId_getScrap = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
				String sScrapInfoQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.insertScrapInfo";
				/*
				 * UPDATE TB_YM_STOCK
				 * SET STOCK_ITEM = 'CM'
				 *   , STOCK_MOVE_TERM = 'A2'
				 *   , MODIFIER = 'SYSTEM'
				 *   , MOD_DDTT = SYSDATE
				 * WHERE STOCK_ID = :HJHJHJ
				 * */
				String QueryId_updateScrap = "ym.steelinfo.steelinforecv.YdStockDAO.updateScrapInfo";
				int nScrapVal = 0;
				JDTORecord jtrScrapStock=ydStockDAO.getData(QueryId_getScrap, new Object[] {CoilNo.trim()});
				if(jtrScrapStock == null) {
					nScrapVal = ydStockDAO.requestinsertData(sScrapInfoQueryId, new Object[] {CoilNo.trim()});
					logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<< Insert 실행: return val="+ String.valueOf(nScrapVal) );
				}else {
					nScrapVal = ydStockDAO.requestupdateData(QueryId_updateScrap, new Object[] {CoilNo.trim()});
					logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<< Update 실행: return val="+ String.valueOf(nScrapVal) );
				}
				
				/**
				 * 1. 현재 스크랩 정보가 있는지 검사한다.
				 * */
	    		// 스크랩관련 처리하기 전. 설비상에 스크랩과 관련된 정보가 있는 경우는 초기화 후 처리한다. 최규성 2009-11-26
//	    		EJBConnector ejbConn = new EJBConnector("default","JNDICoilInfoReg",this);
//				Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookid });
				
	    		
	    	}

    		/* 적치단(TB_YM_STACKLAYER) Table Read 
    		 * STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
             * Select STACK_COL_GP, substr(STACK_COL_GP,1,1) STACK_COL_GP1, substr(STACK_COL_GP,2,1) STACK_COL_GP2, 
             *        STACK_BED_GP, STACK_LAYER_STAT 
    		 *   From TB_YM_STACKLAYER 
    		 *  Where STOCK_ID = ? And (STACK_LAYER_STAT = "L" OR STACK_LAYER_STAT = "U") (적치단 상태 L:적치중, U:권상전)
    		 */     	
	    	if (CoilNo.equals("")){
				logger.println(LogLevel.DEBUG,this, "Coil No = Space Error");
				//throw new EJBServiceException("코일번호가 존재안함" + CoilNo);
				return false;
	    	}
	    	
			String stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
			JDTORecord StackColGp  = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ CoilNo });

			if (StackColGp == null){
				logger.println(LogLevel.DEBUG,this, "적치단(TB_YM_STACKLAYER) Table Read Error");
				//throw new EJBServiceException("보급가능한 적치단 존재하지 않음." + CoilNo);
//				return false;	
				
			 
				//저장위치 MAX 번지+1로 위치를 생성해 준다.
				if("S".equals(WorkID) ||"E".equals(WorkID)){
					int nLayerVal = 0;
					String QueryId_insertLayer = "ym.steelinfo.steelinforecv.YdStockDAO.insertLayerInfo";
					nLayerVal = ydStockDAO.requestinsertData(QueryId_insertLayer, new Object[] {WorkID,WorkID,CoilNo.trim()});
					logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<< insertLayerInfo 실행: return val="+ String.valueOf(nLayerVal) );
					
					String QueryId_insertBed = "ym.steelinfo.steelinforecv.YdStockDAO.insertBedInfo";
					nLayerVal = ydStockDAO.requestinsertData(QueryId_insertBed, new Object[] {WorkID,WorkID});
					logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<< insertBedInfo 실행: return val="+ String.valueOf(nLayerVal) );
					
					
					stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
					StackColGp  = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ CoilNo });
				}
				
			}
			
			
			String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");
			String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
			String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
			String stackStat = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
			
			
	    	/*
	    	 * 보급, 추출 요구가 왔을때 수신한 Coil이 보급이 왔는데 Coil이 출측에 있다면 Error 
	    	 * 추출이 왔는데 Coil이 입측에 있다면 Error Take-In이 왔는데 Coil이 출측에 있다면 Error 
			 */
			String TmpEquip = stackCol.substring(2,4); // 적치열정보에 따른 위치:STACK_COL_USAGE_CD
			// CGS 로그 추가
			logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<< stackCol(적치열(6))            ="+ stackCol);
			logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<< TmpEquip(STACK_COL_USAGE_CD)  ="+ TmpEquip);
			logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<< WorkID (E: EQL ,S:SPM, H:HFL, D: HFL2)="+ WorkID);
			logger.println(LogLevel.DEBUG,this, ">>SpmHflProcessCheck()<< ProcessID(1:보급,3:추출)       ="+ ProcessID);
	
			/**
			 * 저장품 Table에 SPM / HFL 정정보급구분(S/H), 보급일자 Update
			 * A열연 '4K' SPM, '2H' HFL, '1Q' EQ1
			 * B열연 '2K' SPM, '4H' HFL
			 * 정정 보급 요구 일시 SHEAR_SUPPLY_DEMAND_DDTT
			 * 정정 보급 구분     SHEAR_SUPPLY_GP
 			 */
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){  // 보급일때만 Update
				/* 
				 * select SHEAR_SUPPLY_GP, SHEAR_SUPPLY_DEMAND_DDTT From TB_YM_STOCK Where STOCK_ID = :CoilNo
				 */
				String selectshearsupply     = "ym.steelinfo.steelinforecv.YdStockDAO.SelectShearSupply";	
				JDTORecord shearsupply       = ydStockDAO.requestgetData(selectshearsupply, new Object[]{ CoilNo });
				
				String shearsupplygp         = StringHelper.evl(StackColGp.getFieldString("SHEAR_SUPPLY_GP"), ""); 			// 정정 보급 구분
				String shearsupplydemandddtt = StringHelper.evl(StackColGp.getFieldString("SHEAR_SUPPLY_DEMAND_DDTT"), ""); //정정 보급 요구 일시
				
				if (shearsupplygp == null || shearsupplygp.equals("")){
					/*
					 * Update TB_YM_STOCK 
				     *    Set SHEAR_SUPPLY_GP = WorkID, SHEAR_SUPPLY_DEMAND_DDTT = to_char(sysdate,'YYYYMMDDHH24MMSS') 
				     *  Where STOCK_ID = CoilNo
					 */
			    	String updateHFLSPMStockIdLineIn = "ym.steelinfo.steelinforecv.YdStockDAO.updateHFLSPMStockIdLineIn";
			    	
			    	if (WorkID.equals(YmCommonConst.WORK_SPM_E)){			// EQL 보급 WorkID == "E"
			    		if (YardID.equals(YmCommonConst.YD_GP_1)){       	// A열연 coil 야드
		    				if (TmpEquip.equals(YmCommonConst.WORK_EQL_IN_QE) || TmpEquip.equals(YmCommonConst.WORK_EQL_OUT_QD)){	// EQL 입측 || EQL 출측
		    					// A열연 EQL 설비위에 소재 위치.
		    			    }else{ 
		    			    	// A열연 '1Q' EQL
				    			int stkId = ydStockDAO.requestupdateData(updateHFLSPMStockIdLineIn, new Object[]{ 
			    					        YmCommonConst.SHEAR_SUPPLY_GP_1Q, CoilNo.trim() });		 
		    			    }	    								    			

			    		} 
			    	}else if (WorkID.equals(YmCommonConst.WORK_SPM_S)){			// SPM 보급 WorkID == "S"
			    		if (YardID.equals(YmCommonConst.YD_GP_1)){       	// A열연 coil 야드
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){	// SPM 입측 || SPM 출측
		    					// A열연 SPM 설비위에 소재 위치.
		    			    }else{ 
		    			    	// A열연 '4K' SPM
				    			int stkId = ydStockDAO.requestupdateData(updateHFLSPMStockIdLineIn, new Object[]{ 
			    					        YmCommonConst.SHEAR_SUPPLY_GP_1K, CoilNo.trim() });		 
		    			    }	    								    			

			    		}else if (YardID.equals(YmCommonConst.YD_GP_3)){ // A열연 '2H' HFL
		    				if (TmpEquip.equals(YmCommonConst.WORK_HFL_IN_FE) || TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){

		    			    }else{ 
				    			int stkId = ydStockDAO.requestupdateData(updateHFLSPMStockIdLineIn, new Object[]{ 
		    					            YmCommonConst.SHEAR_SUPPLY_GP_5K, CoilNo.trim() });			    			   
		    			    }		    								    			
			    		}
			    	}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
			    		if (YardID.equals(YmCommonConst.YD_GP_1)){       // B열연 '2K' SPM 
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){

		    			    }else{ 
				    			int stkId = ydStockDAO.requestupdateData(updateHFLSPMStockIdLineIn, new Object[]{ 
			    					        YmCommonConst.SHEAR_SUPPLY_GP_1H, CoilNo.trim() }); 
		    			    }	    								    			
			    		}else if (YardID.equals(YmCommonConst.YD_GP_3)){ // B열연 '4H' HFL
		    				if (TmpEquip.equals(YmCommonConst.WORK_HFL_IN_FE) || TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){

		    			    }else{ 
				    			int stkId = ydStockDAO.requestupdateData(updateHFLSPMStockIdLineIn, new Object[]{ 
		    					            YmCommonConst.SHEAR_SUPPLY_GP_5H, CoilNo.trim() });	    			   
		    			    }		    								    			
			    		}
			    	}						
				}
			}		// ProcessID 조건 END	
			
			
	    	if (YardID.equals(YmCommonConst.YD_GP_1)){                      // A열연 
	    		if (WorkID.equals(YmCommonConst.WORK_SPM_E)){				// EQL 작업
	    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){      // EQL 보급           : 적치열(1EQE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_E) || stackCol2.equals(YmCommonConst.BAY_GP_F)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_EQL_IN_QE) || TmpEquip.equals(YmCommonConst.WORK_EQL_OUT_QD)){
		    					// 보급이나 추출 위치에 있다는 것을 의미 
		    					isSuccess = false;
		    			    }else{ 
		    			    	// 보급 예정인 제품으로 적치단에 있음을 의미 
		    			    	isSuccess = true; 
		    			    }	    					
	    				}
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){//EQL 추출           : 적치열(1FQD01) -> 적치열(1GQD01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_F) || stackCol2.equals(YmCommonConst.BAY_GP_G)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_EQL_OUT_QD)){
		    					isSuccess = true; 
		    			    }else{ 
		    			    	isSuccess = false; 	    					
		    				}	    					
	    				}else{ 
	    			    	isSuccess = false; 	    					
	    				}	    				
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){//SPM Take-In  : 적치열(1EQE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_E)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_EQL_IN_QE) || TmpEquip.equals(YmCommonConst.WORK_EQL_OUT_QD)){
		    					isSuccess = false;
		    			    }else{ 
		    			    	isSuccess = true;   
		    				}	    					
	    				}
	    			}
	    		}else if (WorkID.equals(YmCommonConst.WORK_SPM_S)){				// SPM 작업
	    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){      // SPM 보급           : 적치열(1DKE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_D) || stackCol2.equals(YmCommonConst.BAY_GP_E)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
		    					// 보급이나 추출 위치에 있다는 것을 의미 
		    					isSuccess = false;
		    			    }else{ 
		    			    	// 보급 예정인 제품으로 적치단에 있음을 의미 
		    			    	isSuccess = true; 
		    			    }	    					
	    				}
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){//SPM 추출           : 적치열(1EKD01) -> 적치열(1FKD01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_E) || stackCol2.equals(YmCommonConst.BAY_GP_F)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
		    					isSuccess = true; 
		    			    }else{ 
		    			    	isSuccess = false; 	    					
		    				}	    					
	    				}else{ 
	    			    	isSuccess = false; 	    					
	    				}	    				
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){//SPM Take-In  : 적치열(1EKE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_E)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
		    					isSuccess = false;
		    			    }else{ 
		    			    	isSuccess = true;   
		    				}	    					
	    				}
	    			}
	    		}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
	    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){      //HFL 보급           : 적치열(1BFE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_B)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_HFL_IN_FE) || TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){
		    					isSuccess = false;
		    			    }else{ 
		    			    	isSuccess = true;   
		    			    }		    					
	    				}
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){//HFL 추출            : 적치열(1CFD01) -> 적치열(1CFD01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_C)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){ 
		    					isSuccess = true;  
		    			    }else{ 
		    			    	isSuccess = false;   
		    			    }		    					
	    				}else{ 
	    			    	isSuccess = false; 	    					
	    				}	    				
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){//HFL Take-In  : 적치열(1BFE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_B)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_HFL_IN_FE) || TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){
		    					isSuccess = false;
		    			    }else{ 
		    			    	isSuccess = true;   
		    			    }		    						    					
	    				}
	    			}
	    		}
	    	// B열연 Coil 야드(YD_GP_3 == "3" : B열연 COIL 야드)
	    	}else if (YardID.equals(YmCommonConst.YD_GP_3)){                // B열연
	    		if (WorkID.equals(YmCommonConst.WORK_SPM_S)){				// SPM 
	    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){      // SPM 보급           : 적치열(3CKE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_C) || stackCol2.equals(YmCommonConst.BAY_GP_B)){ // 동 구분: C동 || B 
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){ // SPM 입측 || SPM 출측
		    					isSuccess = false; 
		    			    }else{ 
		    			    	isSuccess = true;   
		    			    }		    					
	    				}
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){//SPM 추출           : 적치열(3BKD01) -> 적치열(3AKD01)
	    				
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_A) || stackCol2.equals(YmCommonConst.BAY_GP_B)){	// 동 구분 A동 || B동
	    					/*
	    					 * SPM 추출 요구시 
	    					 * */
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD) ){ //SPM 출측
		    					isSuccess = true;  
		    			    }else{ 
		    			    	isSuccess = false;   
		    			    }
	    				}else{
	    					isSuccess = false;
	    				}	    				
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){//SPM Take-In  : 적치열(3BKE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_B)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){ //SPM 출측
		    					isSuccess = false; 
		    			    }else{ 
		    			    	isSuccess = true;   
		    			    }		    					
	    				}
	    			}
	    		// HFL
	    		}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
	    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){      //HFL 보급            : 적치열(3AFE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_A) || stackCol2.equals(YmCommonConst.BAY_GP_B)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_HFL_IN_FE) || TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){
		    					isSuccess = false; 
		    			    }else{ 
		    			    	isSuccess = true;   
		    			    }		    					
	    				}
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){//HFL 추출            : 적치열(3BFD01) -> 적치열(3CFD01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_B) || stackCol2.equals(YmCommonConst.BAY_GP_C)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){
		    					isSuccess = true; 
		    			    }else{ 
		    			    	isSuccess = false;   
		    			    }		    					
	    				}else{ 
	    			    	isSuccess = false; 	    					
	    				}	    				
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){//HFL Take-In  : 적치열(3BFE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_B)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_HFL_IN_FE) || TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){
		    					isSuccess = false;
		    			    }else{ 
		    			    	isSuccess = true;   
		    			    }		    					
	    				}
	    			}   			
	    		}
	    	}
	    	
	    	logger.println(LogLevel.DEBUG,this, "End-SpmHflProcessCheck()");
	    	
	    	return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	

	/**
	 * 오퍼레이션명 : 
	 *
	 * 1. 조업 SPM / HFL 보급요구 수신
	 * 2. 저장품 Table에 SPM / HFL 저장품이동조건(보급요구), 보급일자 Update 
	 * 3. 야드 MAP에서 보급 요구 대상재 현재위치 조회
	 * 4. 수신 Coil 위치 파악 보급동이 아니면 Error
        *    A열연 SPM 보급(Line In)  : 적치열(1DKE01)
        *    B열연 SPM 보급(Line In)  : 적치열(3CKE01)
        *    A열연 HFL 보급(Line In)  : 적치열(1BFE01)
        *    B열연 HFL 보급(Line In)  : 적치열(3AFE01)
	 * 5. 보급동이면 callLineInOut(Coil No) Call
	 * 6. 저장품 Table에 SPM / HFL 저장품이동조건(보급요구), 보급요구일자(제일빠른일자) 검색
	 * 7. 잔량이 존재 하는지 점검. 존재한다면 3번으로 Loop
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean SPMHFLLineIn(String YardID, String WorkID, String ProcessID, String CoilNo){
		boolean isSuccess = false;

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			logger.println(LogLevel.DEBUG,this, "Start-SPMHFLLineIn()");
			
			int iResult = 0;
			
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
			CraneSchDAO craneSchDAO         = new CraneSchDAO();
			YdEquipDAO  ydEquipDAO          = new YdEquipDAO();
			
			
			/** 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             *  저장품 Table에 작업예약_ID가 존재한다면 Error
             *  Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ CoilNo.trim() });

			if (StockCoilNo == null){ 
				logger.println(LogLevel.DEBUG,this, "수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재하지 않습니다. Error");
				return false; 																																									
			}			
			
			String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), "");
			
			if (stockId == null || stockId.equals("")){
				logger.println(LogLevel.DEBUG,this, "보급요구한 CoilNo이 존재하지 않음  Error");
				return false; }

	    	if (wbookId != null && !wbookId.equals("")){
				logger.println(LogLevel.DEBUG,this, "보급요구한 CoilNo이 이미 작업예약되어 있슴  Error");
				isSuccess = false; 
			}

	    	
			isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
			logger.println(LogLevel.DEBUG,this, ">>SPMHFLLineIn()<< isSuccess ="+ isSuccess);
			
			if (isSuccess){
				isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());	
			}
			
			logger.println(LogLevel.DEBUG,this, "End-SPMHFLLineIn()");
			
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }			
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * SPM / HFL LineIn 요구시 작업여건이 맞지 않아 보급예약을 할 수 없어 잔량으로 남아 있는 Coil들을
	 * 검색해서 작업예약 및 Schedule Call
	 * param pOYM004
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean SPMHFLLineInRequestSearch(String YardID, String WorkID, String ProcessID){
		boolean isSuccess = false;

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-SPMHFLLineInRequestSearch()");
			
			int iResult = 0;
			
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
			CraneSchDAO craneSchDAO         = new CraneSchDAO();
			YdEquipDAO  ydEquipDAO          = new YdEquipDAO();
			
			/**
			 * 저장품 Table에 SPM / HFL 정정보급구분(S/H), 보급요구일자(제일빠른일자) 검색 잔량이 존재 하는지 점검
			 * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK  
			 * Where SHEAR_SUPPLY_GP = WorkID 
			 *   And (SHEAR_SUPPLY_DEMAND_DDTT Is Not Null OR SHEAR_SUPPLY_DEMAND_DDTT != '')
			 *   And not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id) 
			 * Order By 보급요구일자
			 */
			String TmpWorkID = "";
			/*
			Select STOCK_ID, WBOOK_ID 
			  From TB_YM_STOCK  a
			Where SHEAR_SUPPLY_GP = ?
			    And (SHEAR_SUPPLY_DEMAND_DDTT Is Not Null 
			           OR SHEAR_SUPPLY_DEMAND_DDTT != '' )
			    And not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id)
			  Order By SHEAR_SUPPLY_DEMAND_DDTT 
			*/
			String selectStockLineInRequest = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStockLineInRequest";
	    	if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
	    		if (YardID.equals(YmCommonConst.YD_GP_1)){	TmpWorkID= YmCommonConst.SHEAR_SUPPLY_GP_1K;  }
	    		if (YardID.equals(YmCommonConst.YD_GP_3)){	TmpWorkID= YmCommonConst.SHEAR_SUPPLY_GP_5K;  }
	    	}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
	    		if (YardID.equals(YmCommonConst.YD_GP_1)){	TmpWorkID= YmCommonConst.SHEAR_SUPPLY_GP_1H;  }	    		
	    		if (YardID.equals(YmCommonConst.YD_GP_3)){	TmpWorkID= YmCommonConst.SHEAR_SUPPLY_GP_5H;  }
	    	}
	    	List StockId = ydStockDAO.getListData(selectStockLineInRequest, new Object[]{ TmpWorkID.trim() });
			
			JDTORecord TmpSelStockid = null;
			int MaxRec               = StockId.size();	
			String[] tmpStockID      = new String[MaxRec];
			String[] tmpWBookId      = new String[MaxRec];
			
			for (int ii=0; ii<MaxRec; ii++){
				TmpSelStockid        = (JDTORecord) StockId.get(ii);
				tmpStockID[ii]       = StringHelper.evl(TmpSelStockid.getFieldString("STOCK_ID"), "");
				tmpWBookId[ii]       = StringHelper.evl(TmpSelStockid.getFieldString("WBOOK_ID"), "");
				
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), tmpStockID[ii].trim());
				
			    logger.println(LogLevel.DEBUG,this, ">>SPMHFLLineInRequestSearch()<< isSuccess ="+ isSuccess);
			
			    if (isSuccess){
			    	isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), YmCommonConst.PROCESS_ID_1, tmpStockID[ii].trim());
//			    	isSuccess = SPMHFLLineIn(YardID.trim(), WorkID.trim(), YmCommonConst.PROCESS_ID_1, tmpStockID[ii].trim());	
			    }
			    isSuccess = false;
			}
			
			logger.println(LogLevel.DEBUG,this, "End-SPMHFLLineInRequestSearch()");
			
			return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }					
	}
	
	/**
	 * 오퍼레이션명 : 야드동기화 코드 추가함. 메소드(SyncTrackingConv()) 호출. 최규성
	 *
	 * SPM / HFL 보급, 추출, Take-In
	 * param YardID : 야드구분
	 * param WorkID : S SPM, H HFL ,E EQL
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callLineInOut(String YardID, String WorkID, String ProcessID, String CoilNo){	

		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO craneSchDAO         = new CraneSchDAO();
		YdEquipDAO  ydEquipDAO          = new YdEquipDAO();
		
		String WBook_Str = "";
		String TmpstackCol   ="";
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this,"Start-callLineInOut()");				
			
			logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< CoilNo="+ CoilNo.trim());

			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ CoilNo.trim() });
			
			// CGS
			// TB_YM_STOCK에서 정보가 존재하지 않으면 Error.  CGS
			// Scrap에 대한 정보는 임시로 저장함.
			if (StockCoilNo == null){ 
				logger.println(LogLevel.DEBUG,this, "StockCoilNo Error");
				return false;				
			}	
			
			logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< StockCoilNo="+ StockCoilNo);
			
			String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), ""); // CoilNo
			String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), ""); // 작업예약 ID
            
			logger.print(LogLevel.DEBUG,this, "stockId="+ stockId);
			
			List list = new ArrayList();
			list.add(CoilNo.trim());
			
			/*
			 * SELECT 
					WBOOK_ID,STOCK_ITEM,STOCK_STAT,STOCK_COOL_STAT,STOCK_COOL_START_DDTT,		
					STOCK_COOL_START_TEMP,STACK_LOT_NO,STOCK_MOVE_TERM,CHARGE_LOT_NO,FRTOMOVE_WORD_NO,
					TRANS_WORD_NO,REGISTER,REG_DDTT,MODIFIER,MOD_DDTT,DEL_YN
				FROM TB_YM_STOCK
				WHERE STOCK_ID= :CoilNo
			 */
			boolean exsitCoilNo = ydStockDAO.isExistPrimaryKey(list);

			logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< exsitCoilNo="+ exsitCoilNo);
			
			if (stockId == null || stockId.equals("")){
				logger.println(LogLevel.DEBUG,this, "작업요구한 CoilNo이 존재하지 않음  Error");
				return false; 
			}

	    	if (wbookId != null && !wbookId.equals("")){
				logger.println(LogLevel.DEBUG,this, "작업요구한 CoilNo이 이미 작업예약되어 있슴  Error");
				isSuccess = false; 
			}
	    	
			if(exsitCoilNo) {

				logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< wbookId="+ wbookId);
				
		    	// 저장품  Table에 작업예약_ID(WBookID)가 존재한다면 Error
				// 저장품 Table(TB_YM_STOCK)에 작업예약ID가 존재하지 않는 경우.
		    	if (wbookId == null || wbookId.equals("")){
		    		
//		    		isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());		    		
//		    		if (!isSuccess){ return false;	}
		    		
		    		/* 적치단(TB_YM_STACKLAYER) Table Read 
		    		 * STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
                     * Select STACK_COL_GP, substr(STACK_COL_GP,1,1) STACK_COL_GP1, substr(STACK_COL_GP,2,1) STACK_COL_GP2, 
                     *        STACK_BED_GP, STACK_LAYER_STAT 
		    		 *   From TB_YM_STACKLAYER 
		    		 *  Where STOCK_ID = :stockId And (STACK_LAYER_STAT = "L" OR STACK_LAYER_STAT = "U") (적치단 상태 L:적치중, U:권상전)
		    		 */     	
					String stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
					JDTORecord StackColGp  = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ stockId });

					if (StackColGp == null){
						logger.println(LogLevel.DEBUG,this, "StackColGp Error");
						return false;										
					}	
					
					logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< StackColGp="+ StackColGp);

					String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");		// 적치열 정보(6자리)
					String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");	// 야드 구분(1자리)
					String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");	// 동 구분(1자리)
					String stackStat = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");	// 적치 단 상태
					String chk 		 = StringHelper.evl(StackColGp.getFieldString("CHK"), "");; //재작업 추출 
					logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< stackCol="+ stackCol + "/" + stackCol1 + "/" + stackCol2);
					logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< SPM재작업 구분="+ chk);
					
					if (stackCol != null && !stackCol.equals("")){

						
						// 추출요구가 수신되면 추출동에 있는 대상재를 추출요구 위치동으로 옮긴뒤 처리한다.
						if (WorkID.equals(YmCommonConst.WORK_SPM_E)){
							if (YardID.equals(YmCommonConst.YD_GP_1)){                //A열연
								if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){    //추출
									if (stackCol2.equals(YmCommonConst.BAY_GP_F)||stackCol2.equals(YmCommonConst.BAY_GP_G)){
										
										 if(stackCol2.equals(YmCommonConst.BAY_GP_F)){
											 TmpstackCol   = YmCommonConst.STACK_COL_GP_1FQD01;  //1FQD01  
										 }else{
											 TmpstackCol   = YmCommonConst.STACK_COL_GP_1GQD01;  //1GQD01  
										 }
										 
										//int iSeq1 = YmCommonDB.insertConveyorInfo(
										//	    TmpstackCol.trim(), stockId.trim(), YmCommonConst.GBN_MAX);
										//int iSeq2 = YmCommonDB.deleteConveyorInfo(stackCol.trim(), stockId.trim());										 

										stackCol  = TmpstackCol;
										stackCol1 = TmpstackCol.substring(0,1);
										stackCol2 = TmpstackCol.substring(1,2);										
									}
								}															
							} 
						// SPM 작업 실적 완료 되면 추출동에 Insert 한다.
						}else if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
							if (YardID.equals(YmCommonConst.YD_GP_1)){                //A열연
								if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){    //추출
									if (stackCol2.equals(YmCommonConst.BAY_GP_E)){
										
										if(chk.equals("J")){
											TmpstackCol   = YmCommonConst.STACK_COL_GP_1EKD01;  //1EKE01 
											int iSeq1 = YmCommonDB.insertConveyorInfo(YmCommonConst.STACK_COL_GP_1EKE02, "",  YmCommonConst.GBN_J05);
										}else{
											TmpstackCol   = YmCommonConst.STACK_COL_GP_1FKD01;  //1EKE01 -> 1FKD01
											
											int iSeq1 = YmCommonDB.insertConveyorInfo(
												    TmpstackCol.trim(), stockId.trim(), YmCommonConst.GBN_MAX);
											int iSeq2 = YmCommonDB.deleteConveyorInfo(stackCol.trim(), stockId.trim());
										}

										stackCol  = TmpstackCol;
										stackCol1 = TmpstackCol.substring(0,1);
										stackCol2 = TmpstackCol.substring(1,2);										
									}
								}															
							}else if (YardID.equals(YmCommonConst.YD_GP_3)){          //B열연
								// 일반 코일에 대한 처리.
								if (ProcessID.equals(YmCommonConst.PROCESS_ID_3) /*&& stockId.substring(0, 1) != "S"*/ ){    //추출 
									if (stackCol2.equals(YmCommonConst.BAY_GP_B)){
										 TmpstackCol   = YmCommonConst.STACK_COL_GP_3AKD01;  //3BKD01 -> 3AKD01 
										int iSeq1 = YmCommonDB.insertConveyorInfo(
												    TmpstackCol.trim(), stockId.trim(), YmCommonConst.GBN_MAX);
										int iSeq2 = YmCommonDB.deleteConveyorInfo(stackCol.trim(), stockId.trim());
										stackCol  = TmpstackCol;
										stackCol1 = TmpstackCol.substring(0,1);	// 이동할 야드 구분
										stackCol2 = TmpstackCol.substring(1,2);	// 이동할 동 구분	
										logger.println(LogLevel.DEBUG,this, ">>SPM callLineInOut()<< stockId="+ stockId.trim()+ " 이동할야드="+stackCol1 +"이동할 동="+stackCol2);
										
										
									}
								}
							}
						}else if (WorkID.equals(YmCommonConst.WORK_HFL_S)){						//HFL 결속대 보급

							//String TmpstackCol   = YmCommonConst.STACK_COL_GP_3DHS01;  //3DHS01
							TmpstackCol   = "3"+stackCol2+"HS01";  //3DHS01
							stackCol  = TmpstackCol;
							stackCol1 = TmpstackCol.substring(0,1);	// 이동할 야드 구분
							stackCol2 = TmpstackCol.substring(1,2);	// 이동할 동 구분	
							logger.println(LogLevel.DEBUG,this, ">>HFL 결속대 보급 callLineInOut()<< stockId="+ stockId.trim()+ " 이동할야드="+stackCol1 +"이동할 동="+stackCol2);
							
						}  

						
                        // 적치단  Table Update(작업요구상태='S'로 변경)
						/* UPDATE TB_YM_STACKLAYER 
						      SET STACK_LAYER_STAT = 'S' 
						    WHERE STOCK_ID = :stockId
						*/
						if (stackStat.trim().equals(YmCommonConst.STACK_LAYER_STAT_L)){
							String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
							int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
									       YmCommonConst.STACK_LAYER_STAT_S, stockId.trim() });

							logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< stockId="+ stockId.trim()+ "stkColGp="+stkColGp);
						}

						//===============================================================================================
						// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
						/*
						 * SELECT  TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI') || LPAD(YM_WBOOK_SEQ.NEXTVAL,6,'0') AS WBOOK_SELECT
						 *   FROM  DUAL
						 */
						String wBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
						JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);

						logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< wBookSel="+ wBookSel );
						
						String wBookid  = wBookSel.getFieldString("WBOOK_SELECT");
						
						logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< wBookid="+ wBookid );

						//=============================================================================================================================
						// CGS
						// 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시) 한다.
						//
						// INSERT INTO TB_YM_WBOOK (WBOOK_ID, YD_GP, BAY_GP, SCH_WORK_KIND, 
						//				            SCH_WORK_LOC_DECISION_METHOD, -- 'S'
						//				            CRANE_WORD_PUT_LOC,           -- null
						//				            WBOOK_DDTT,                   -- sysdate
						//				            WBOOK_DUTY, WBOOK_PARTY, 
						//				            WBOOK_SCH_TERM,               -- 'T'
						//				            WBOOK_SCH_ACT_DDTT,           -- sysdate
						//				            REGISTER,                     -- 'SYSTEM'
						//				            REG_DDTT,                     -- sysdate
						//				            MODIFIER,                     -- null
						//				            MOD_DDTT,                     -- null
						//				            DEL_YN)                       -- 'N'
						//				VALUES (?, ?, ?, ?, 'S', null, to_char(sysdate,'YYYYMMDDHH24MI'), ?, ?, 'T', to_char(sysdate,'YYYYMMDDHH24MI'),
						//				              'SYSTEM', sysdate, null, null, 'N')
						//
						String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
						
						//------------------------------------------------------------------------------------------------------------
						// CGS 추가
						// Scrap 처리를 위한 변수 추가 
						String sSWBookQueryId = "";	// 작업예약 저장 쿼리
						String sUserToLoc = "0101"; // TO위치의 적치 대, 단 정보 고정.
						//------------------------------------------------------------------------------------------------------------
						
						// 1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In
						int wbookstockId = 0;
						
						if (WorkID.equals(YmCommonConst.WORK_SPM_E)){								// EQL 작업
							/////////////////////////////////////////////////EQL ////////////////////////////////////////////////////////
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){						// 보급					 
								
								// 작성된 작업예약을 DB에 저장한다.
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil EQL 보급 
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_EQLI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
								
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){		
								if(stackCol1.equals("1") && (chk.equals("J")||chk.equals("Q"))){
									if (WorkID.equals(YmCommonConst.WORK_SPM_E)){
										wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil EQL 재작업 추출
											       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_EQLR, 
												   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
									}else{
										wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM 재작업 추출
											       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKLR, 
												   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
									}
									
								}else{
									// 작성된 작업예약을 DB에 저장한다.
									wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil EQL 추출
											       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_EQLO, 
												   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
									
								}
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){ 
								
								// 작성된 작업예약을 DB에 저장한다.
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM Take In
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_EQTI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
							}
							/////////////////////////////////////////////////EQL ////////////////////////////////////////////////////////
						}else if (WorkID.equals(YmCommonConst.WORK_SPM_S)){								// SPM 작업
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){						// 보급
								
								// 2009-03-31 CGS
								// 보급위치를 파악한다. SPM내의 소재가 이동할 경우 이동된 정보를 확인할 수 있는가?
								
								// 작업예약 하기 전에 Tracking동기화. >> 스케줄에서 To위치 정하기 전에 동기화.
								if (stackCol2.equals(YmCommonConst.BAY_GP_C)){
									boolean bReturnV = SyncTrackingConv(YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S, YmCommonConst.PROCESS_ID_1);
								}else if (stackCol2.equals(YmCommonConst.BAY_GP_B)){
									boolean bReturnV = SyncTrackingConv2(YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S, YmCommonConst.PROCESS_ID_1);
								}
								
								// 작성된 작업예약을 DB에 저장한다.
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM 보급 
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKLI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
								
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								// Scrap일 경우를 비교한다. 
								if (CoilNo.substring(0, 1).equals("S") ){								 // Scrap SPM 추출.
									// 적치단의 상태가 "E"가 아닌 경우 강제로 E로 변경한다. 위에서 'S'로 변경하고 있음.ㅡ..ㅡ..
									//UPDATE TB_YB_STACKLAYER
									//   SET STACK_LAYER_STAT = 'E'
									//     , MODIFIER='SYSTEM'
									//     , MOD_DDTT=SYSDATE
									// WHERE STACK_COL_GP = ?
									sSWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.UpdateScrapLayerStat";
									wbookstockId = ydWBookDAO.requestupdateData(sSWBookQueryId, new Object[]{ YmCommonConst.STACK_COL_GP_3ASP01} );
									
									logger.println(LogLevel.DEBUG,this, "3ASP01적치열에 대한 적치단의 상태를 E로 수정함.");
									
									// Scrap SPM 추출 작업예약 DB 등록
									/*
									INSERT INTO USRYMA.TB_YM_WBOOK(WBOOK_ID, YD_GP, BAY_GP, SCH_WORK_KIND, 
									SCH_WORK_LOC_DECISION_METHOD, CRANE_WORD_PUT_LOC,
									WBOOK_DDTT, WBOOK_DUTY, WBOOK_PARTY, 
									WBOOK_SCH_TERM, WBOOK_SCH_ACT_DDTT, REGISTER,
									REG_DDTT, MODIFIER, MOD_DDTT, DEL_YN)
									VALUES(?, ?, ?, ?, 'O', ?, to_char(sysdate,'YYYYMMDDHH24MI'), ?, ?, 'T', to_char(sysdate,'YYYYMMDDHH24MI'),
									              'SYSTEM', sysdate, 'SYSTEM', sysdate, 'N')
									*/
									sSWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertScrapYdWBook";
									
									// TO 위치를 지정한다. 3ACS010101
									sUserToLoc = YmCommonConst.STACK_COL_GP_3ASP01 + sUserToLoc;									

									wbookstockId = ydWBookDAO.requestinsertData(sSWBookQueryId, new Object[]{	// Scrap SPM 추출
											wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKLO,  sUserToLoc,
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
									logger.println(LogLevel.DEBUG, this, ">>callLineInOut()<< Scrap 작업예약처리"+ String.valueOf(wbookstockId));
									
									
								}else{	// 코일 제품 추
									if(stackCol1.equals("1") && (chk.equals("J")||chk.equals("Q"))){
										
										if (WorkID.equals(YmCommonConst.WORK_SPM_E)){
											wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil EQL 재작업 추출
												       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_EQLR, 
													   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
										}else{
											wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM 재작업 추출
												       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKLR, 
													   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
										}
									}else {
										wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM 추출
												       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKLO, 
													   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
									}
								}
								
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                      
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM Take In
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKTI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
							}
						}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){  //HFL 
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                          
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil HFL 보급 
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CFLI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil HFL 추출
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CFLO, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil HFL Take In
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CFTI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });								
							}
						}else if (WorkID.equals(YmCommonConst.WORK_HFL_S)){ //HFL 결속대 
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                          
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil HFL 결속대 보급 
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CFSI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil HFL 결속대 추출
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CFSO, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
							}
						}
						
						logger.println(LogLevel.DEBUG, this, ">>callLineInOut()<< stackCol1="+ stackCol1+ " stackCol2="+stackCol2);
						
						if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){	// 보급
							/**
							 * 저장품 Table에 SPM / HFL 정정보급구분(S/H), 보급일자 Update
							 * UPDATE TB_YM_STOCK 
							 *    SET SHEAR_SUPPLY_GP = '', 
							 *        SHEAR_SUPPLY_DEMAND_DDTT = '' 
							 *  WHERE STOCK_ID = :stockId
							 *  정정 보급 요구 일시 SHEAR_SUPPLY_DEMAND_DDTT
							 *  정정 보급 구분     SHEAR_SUPPLY_GP
							 */
					    	String updateHFLSPMStockIdLineInSpace = "ym.steelinfo.steelinforecv.YdStockDAO.updateHFLSPMStockIdLineInSpace";
			    			int updatehflspmStockIdLineInspace    = ydStockDAO.requestupdateData(updateHFLSPMStockIdLineInSpace, new Object[]{ 
			    					                                stockId.trim() });	
			    			logger.println(LogLevel.DEBUG, this, ">>callLineInOut()<< 보급:정정보급구분,보급일자 : ''");
						}
/*
						// 기존 소스
						// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
						// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
						String[] sStockInfo         = YmCommonUtil.getCoilCurrProgCd(stockId.trim(),""); // 공통 진도코드 Read해서 저장품 이동조건으로 변환
*/						
						//==========================================================================================================================
						// CGS 수정
						// Scrap에 대한 저장품이동조건 변환에 대한 코드 추가. 강제로 A2 설정.
						String[] sStockInfo = {"",""};
						if (WorkID.equals(YmCommonConst.WORK_HFL_S)){
							if(ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								// HFL결속대  대한 코드는 고정으로 적용한다.
								sStockInfo[1] = YmCommonConst.NEW_STOCK_MOVE_TERM_A7; //HFL 결속대 추출 							
							}else{
								sStockInfo[1] = YmCommonConst.NEW_STOCK_MOVE_TERM_CC; //HFL 결속대 보급
							}
							
							logger.println(LogLevel.DEBUG, this, ">>callLineInOut()<< 저장품 이동조건으로 변환 ["+sStockInfo[1]+"]");
							
						}else if ( !stockId.substring(0, 1).equals("S") ) 
						{
							sStockInfo  = YmCommonUtil.getCoilCurrProgCd(stockId.trim(),""); // 공통 진도코드 Read해서 저장품 이동조건으로 변환
							logger.println(LogLevel.DEBUG, this, ">>callLineInOut()<< 공통 진도코드 Read해서 저장품 이동조건으로 변환");
						}else
						{
							// Scrap에 대한 코드는 고정으로 적용한다.
							sStockInfo[1] = YmCommonConst.NEW_STOCK_MOVE_TERM_A2;
							logger.println(LogLevel.DEBUG, this, ">>callLineInOut()<< 저장품 이동조건으로 변환 ["+sStockInfo[1]+"]");
						}
						
						/** 
						 * 저장품 Table(TB_YM_STOCK)에 작업예약ID,저장품이동조건을 Update 한다.						
						 * UPDATE TB_YM_STOCK 
						 *    SET WBOOK_ID        = :wBookId, 
						 *        STOCK_MOVE_TERM = :sStockInfo
						 *  WHERE STOCK_ID = :stockId
						 *  
						 *  작업예약ID		WBOOK_ID
						 *  저장품이동조건		STOCK_MOVE_TERM
						 * */
						logger.println(LogLevel.DEBUG, this, ">>callLineInOut()<< 작업예약ID,저장품이동조건 수정(TB_YM_STOCK)");
						//==========================================================================================================================
						String updateYdStockStockId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						int updateydstockstockid    = ydStockDAO.requestupdateData(updateYdStockStockId, new Object[]{ 
		    		                                  wBookid, sStockInfo[1], stockId.trim() });	
						
						logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< WorkID   ="+ WorkID.trim());
						logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< ProcessID="+ ProcessID.trim());
						
						logger.println(LogLevel.DEBUG,this,"End-callLineInOut()");
						 
						
						String TmpSchcode = "";	// 스케줄 코드
						if (WorkID.equals(YmCommonConst.WORK_SPM_E)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_EQLI;  //Coil EQL 보급
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								
								if(stackCol1.equals("1") && (chk.equals("J")||chk.equals("Q"))){
									TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_EQLR; //Coil EQL 재작업 추출
								}else{
									TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_EQLO;  //Coil EQL 추출	
								}
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){   
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_EQTI;  //Coil EQL Take In
							}
						}else if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CKLI;  //Coil SPM 보급
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								// CGS
								// Scrap 추출시에도 동일한 코드를 사용.
								// To 위치만 강제로 지정.
								if(stackCol1.equals("1") && (chk.equals("J")||chk.equals("Q"))){					 
									TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CKLR;  //Coil SPM 재작업 추출	
								}else{
									TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CKLO;  //Coil SPM 추출	
								}
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){   
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CKTI;  //Coil SPM Take In
							}
						}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CFLI;  //Coil HFL 보급
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CFLO;  //Coil HFL 추출
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CFTI;  //Coil HFL Take In
							}
						}else if (WorkID.equals(YmCommonConst.WORK_HFL_S)){  //HFL결속대 
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CFSI;  //Coil HFL 보급
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CFSO;  //Coil HFL 추출
							}
						}
						
						/*
						 * SELECT count(SCH_ID) AS SchcodeCount 
						 *   FROM USRYMA.TB_YM_SCH 
						 *  WHERE SCH_WORK_KIND = :TmpSchcode 
						 *    AND YD_GP = :YardID
						 */
						String selectStockSchID     = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockSchIDCount";
						JDTORecord selectstockschid = ydStockDAO.getData(selectStockSchID, new Object[]{ TmpSchcode.trim(), YardID  });

						String tmpSchcodeCount      = StringHelper.evl(selectstockschid.getFieldString("SCHCODECOUNT"), "");
						
						int schedulecount           = Integer.parseInt(tmpSchcodeCount);
						
						logger.println(LogLevel.DEBUG,this, "CallLineInOut() 작업예약 완료 스케줄 = " + TmpSchcode.trim() + " / 스케줄테이블 스케줄 코드 갯수 = "+ schedulecount);
						
						
						/*
						 * A열연 HFL 추출은 HFL 실적 수신이 되면서 자동으로 추출작업요구 Call
						 */
						if (YardID.equals(YmCommonConst.YD_GP_1) && 
						    WorkID.equals(YmCommonConst.WORK_HFL_H) && 
							ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
							/* HFL 출측  Total Count
							 * Select A.STOCK_ID AS STOCK_ID, A.STACK_LAYER_STAT AS STACK_LAYER_STAT, B.WBOOK_ID AS WBOOK_ID 
							 *   From USRYMA.TB_YM_STACKLAYER A, USRYMA.TB_YM_STOCK B
							 *  Where A.STACK_COL_GP = '1CFD01'   
							 *    And A.STOCK_ID Is Not Null 
							 *    And A.STOCK_ID = B.STOCK_ID 
							 *  ORDER BY A.STACK_BED_GP DESC
							 */
							String selectHFLtotalcount = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.SelectHFLTotalCount";
					    	List   HFLtotalcount       = ydStockDAO.getListData(selectHFLtotalcount, new Object[]{ 
					    			                     YmCommonConst.STACK_COL_GP_1CFD01 });
					    	
							JDTORecord TmpSelStockid = null;
							int MaxHFLtotalRec       = HFLtotalcount.size();	
							String[] tmpStockID      = new String[MaxHFLtotalRec];
							String[] tmpWBookId      = new String[MaxHFLtotalRec];
							String[] tmpSL_Stat      = new String[MaxHFLtotalRec];
							int SC_Count             = 0;
							
							if (MaxHFLtotalRec > 0){
								for (int ii=MaxHFLtotalRec-1; ii<0; ii--){
									TmpSelStockid        = (JDTORecord) HFLtotalcount.get(ii);
									tmpStockID[ii]       = StringHelper.evl(TmpSelStockid.getFieldString("STOCK_ID"), "");
									tmpWBookId[ii]       = StringHelper.evl(TmpSelStockid.getFieldString("WBOOK_ID"), "");
									tmpSL_Stat[ii]       = StringHelper.evl(TmpSelStockid.getFieldString("STACK_LAYER_STAT"), "");
									if (tmpSL_Stat[ii].trim().equals(YmCommonConst.STACK_LAYER_STAT_U) || 
										tmpSL_Stat[ii].trim().equals(YmCommonConst.STACK_LAYER_STAT_P)){
										SC_Count = SC_Count + 1;									
									}else {
										if (SC_Count < 2){
											//Coil Schedule EJB Call
											logger.println(LogLevel.DEBUG,this, "HFL 추출 스케줄 Call="+ wBookid.trim() + " / stockId="+ stockId.trim());
											EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
											Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ 
													         tmpWBookId[ii] });
										}
									}								
								}	// for end														
							}

						}else {
							if (schedulecount < 2){
								//Coil Schedule EJB Call
//								/**
//								 * 2007.05.02 이정훈
//								 * 작업 예약  Table(TB_YM_WBOOK) : 작업예약 등록 우선 순위가 제일 빠른것 추출
//								 */
//								/*
//								-- 작업예약 Table Select 조건에 해당하는 작업예약ID 추출
//								-- 작업예약 등록 우선 순위가 제일 빠른것 추출
//
//								Select MIN(WBOOK_ID) as wbook_id
//								  From TB_YM_WBOOK  a 
//								Where YD_GP                  = ?
//								    And BAY_GP                = ?
//								    And SCH_WORK_KIND = ?
//								   AND not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id)
//								*/
//								String sSchwBook = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectSchWBook";
//								JDTORecord WBook = ydEquipDAO.getData(sSchwBook, new Object[]{ YardID.trim(), stackCol2.trim(), TmpSchcode.trim() });			
//								wBookid = StringHelper.evl(WBook.getFieldString("WBOOK_ID"), "");
								
								logger.println(LogLevel.DEBUG,this, "CallLineInOut() 스케줄 Call="+ wBookid.trim() + " / stockId="+ stockId.trim());
								//************************************************************************************************
								//*****************************스케줄이 존재 안하는 경우에만 크레인 스케줄 호출 *****************************
								//************************************************************************************************
								/*
									SELECT YD_GP, BAY_GP , SCH_WORK_KIND
									 FROM USRYMA.TB_YM_SCH
									 Where YD_GP                  = ?
									    And BAY_GP                = ?
									    And SCH_WORK_KIND = ? 
								 */
								String sSchwBook = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectSch";
								JDTORecord SCHCHK = ydEquipDAO.getData(sSchwBook, new Object[]{ YardID.trim(), stackCol2.trim(), TmpSchcode.trim() });
								
								if(YardID.equals("1")&& TmpSchcode.equals("CKLI") && TmpSchcode.equals("EQLI") && SCHCHK != null ){
									logger.println(LogLevel.DEBUG,this, "SPM/EQL보급 스케줄이 존재  함 생략 스케줄 Call="+ wBookid.trim() + " / stockId="+ stockId.trim());
								}else{
								
									if(SCHCHK == null || schedulecount < 2 ) {
										logger.println(LogLevel.DEBUG,this, "스케줄이 존재 안 함 CallLineInOut() 스케줄 Call="+ wBookid.trim() + " / stockId="+ stockId.trim());
									
										EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
										Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookid });														
									}
								}
							}
						}
					}
		    	}	
		    }				
			
			isSuccess = true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		    return isSuccess;
	} 
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * SPM / HFL 보급취소
	 * param YardID : 야드구분
	 * param WorkID : S SPM, H HFL
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callLineInOutCancle(String YardID, String WorkID, String ProcessID, String CoilNo){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			CraneSchDAO dao = new CraneSchDAO();
			
			/**
			 *	1.	보급작업예약이 걸려있는지를 체크
			 */
			
			String sWbookId = "";
			String sSchCode = "";
			
			JDTORecord stockJr = dao.getStockInfo(CoilNo);
			
			if(stockJr != null){
				sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
			}
			
			JDTORecord wbookJr = dao.getWbookInfo(sWbookId);
			
			if(wbookJr != null){
				sSchCode = StringHelper.evl(wbookJr.getFieldString("SCH_WORK_KIND"),"");
			}
			
			if(YmCommonUtil.isLineInWork(sSchCode)){
					
				/**
				 *	2.	보급작업예약이 있으면 작업예약 삭제
				 *		-	작업예약 삭제모듈에 스케쥴취소 및 
				 *			조업으로 취소전문 송신 및
				 *			예약FLAG 삭제 포함.
				 */ 
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
				Boolean isTemp  = (Boolean)ejbConn.trx("setWorkIdCancle",
											new  Class[]{String.class},
											new Object[]{CoilNo});
											
				logger.println(LogLevel.DEBUG, this, "보급작업예약취소=> 스케쥴 취소 모듈 CALL="+isTemp);		
			}else{	
				/**
				 *	3.	보급작업예약이 없으면 예약 FLAG만 삭제
				 */ 
				int iSeq = dao.updateStockSupplyGpWithStockId(CoilNo,
															  "",
															  "");
				logger.println(LogLevel.DEBUG, this, "보급작업예약취소=> 예약 FLAG 삭제");													  
			}
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		return isSuccess;
	} 
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * SPM / HFL 보급취소
	 * param YardID : 야드구분
	 * param WorkID : S SPM, H HFL
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callLineInOutCancle_backup(String YardID, String WorkID, String ProcessID, String CoilNo){
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO craneSchDAO         = new CraneSchDAO();

//		String WBook_Str = "";
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
    		/* 처리구분 : 보급취소
    		 * 1. 수신한 Coil No로 저장품 Table Select 
    		 *       Select WBOOK_ID From TB_YM_STOCK Where STOCK_ID = CoilNo
    		 */   
		
		    logger.println(LogLevel.DEBUG,this,"Start-callLineInOutCancle()");
			
			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID, STOCK_MOVE_TERM from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ CoilNo.trim() });

			if (StockCoilNo == null){
				logger.println(LogLevel.DEBUG,this, "수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재하지 않습니다. Error");
				return false;
			}	
			
			String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), "");
			String stockmoveterm = StringHelper.evl(StockCoilNo.getFieldString("STOCK_MOVE_TERM"), "");
            
			logger.print(LogLevel.DEBUG,this, "stockId="+ stockId);
			
			List list = new ArrayList();
			list.add(CoilNo.trim());
			boolean exsitCoilNo = ydStockDAO.isExistPrimaryKey(list);

			/*
			 * Schedule(TB_YM_SCH)에  해당하는  COIL이 SCHEDULE 작업상태(SCH_WORK_STAT)가 
			 * 2:UP실적, 3:PUT지시, 4:PUT실적 일경우  보급취소 불가
			 * 
			 * ym.facilitystatus.facilityinquiry.CraneSchDAO.selectWorkStockId 
			 * SELECT SCH_WORK_STAT
			 *   FROM TB_YM_SCH
			 *  WHERE STOCK_ID = ?
			 *    
			 */
			String scheduleQuery   = "ym.facilitystatus.facilityinquiry.CraneSchDAO.selectWorkStockId";
			JDTORecord schWorkStat = ydStackLayerDAO.requestgetData(scheduleQuery, new Object[]{ CoilNo.trim() });

			if (schWorkStat == null){
				//throw new EJBServiceException("schWorkStat Error");
			}else{
				String SchStat = StringHelper.evl(schWorkStat.getFieldString("SCH_WORK_STAT"), "");
				
				if (SchStat.trim().equals(YmCommonConst.SCH_WORK_STAT_2) || 
					SchStat.trim().equals(YmCommonConst.SCH_WORK_STAT_3) ||
					SchStat.trim().equals(YmCommonConst.SCH_WORK_STAT_4)){
					exsitCoilNo = false;
				}				
			}
			
			if(exsitCoilNo) {
				
    		    if (wbookId != null  && !wbookId.equals("")){
    		    	// 2.   예약ID에 해당하는  Coil No들 추출
    		    	//      Select STOCK_ID From TB_YM_STOCK Where WBOOK_ID = WBOOK_ID.trim()
    				String sListStockQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectListStockID";
    				List ListStockCoilNo     = ydStockDAO.getListData(sListStockQueryId, new Object[]{ wbookId.trim() });	    		    	
    				
    				JDTORecord TmpSelStock = null;
    				String SelStock        = null;
    				int MaxRec             = ListStockCoilNo.size();
					
    				if (MaxRec > 0 ){
						for (int ii=0; ii<MaxRec; ii++){
							TmpSelStock    = (JDTORecord) ListStockCoilNo.get(ii);
							SelStock       = StringHelper.evl(TmpSelStock.getFieldString("STOCK_ID"), "");
							
		    	    		// 2-1. 적치단  Table Update (적치단상태='L'로 변경:적치중)
		    	    		//      UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'L' WHERE STOCK_ID = ?
							String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
							int stkColGp            = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
									                  YmCommonConst.STACK_LAYER_STAT_L, SelStock.trim() });
							
		    	    		// 2-2. SCHEDULE(TB_YM_SCH) Table Delete
		    	    		//      DELETE TB_YM_SCH WHERE STOCK_ID = ? 
							String sSchQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteSchWBook";
							int delsch         = ydStackLayerDAO.requestdeleteData(sSchQueryId, new Object[]{ SelStock.trim() });
							
		    	    		// 2-3. 저장품(TB_YM_STOCK) Table Update
		    	    		//      UPDATE TB_YM_STOCK SET WBOOK_ID = null, STOCK_MOVE_TERM = null  WHERE STOCK_ID = ?								
							String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
							int stkId         = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ "", stockmoveterm.trim(), SelStock.trim() });
						}
    				}	
    		    	
    	    		/* 3. 작업예약(TB_YM_WBOOK) Table Delete
    	    		 *    DELETE TB_YM_WBOOK WHERE WBOOK_ID  = 추출 WBOOK_ID
					 *    String sdeleteQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.DeleteYdWBook";
					 *    int delsch = ydStackLayerDAO.requestdeleteData(sdeleteQueryId, new Object[]{ WBook_Str.trim() });
					 *    DELETE tb_ym_wbook	WHERE wbook_id = :wbook_id
					 */  
					int delsch = ydWBookDAO.deleteWbookInfo( wbookId.trim() );
					
					/*
					 * 보급취소 조업 YMPO161 Send
					 */
		    		
					YMPO161 model = new YMPO161();
					model.setTcCode(YmCommonConst.MODEL_YMPO161);
					model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					/* 권하일자	CHAR(8)  yyyymmdd	*/
					model.setdownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
					
					/* 권하시각     CHAR(6)  HHMMSS */
					model.setdownTime(YmCommonUtil.getCurDate("HHmmss"));
					
					/* 공장구분	CHAR(1)  A:A열연, B:B열연 */
					model.setplantGbn(YmCommonConst.YD_GP_1.equals(YardID)?YmCommonConst.YD_GP_A:YmCommonConst.YD_GP_B);
					
					/* 공정구분	CHAR(1)  H : Hot Filnal, S :  SkinPass	*/
					model.setprocGbn(WorkID.substring(0,1));
					
					/* COIL번호	CHAR(11) */
					model.setcoilNo(CoilNo);
					
					/* 처리구분     CHAR(1)  1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In */
					model.setProcessId("2");
					
					/* 위치포지션  CHAR(2)  */
					model.setpositionNo("");
					
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},new Object[]{ model });							
					
					logger.println(LogLevel.DEBUG,this,"End-callLineInOutCancle()");
	    		}	
	    	}
			
			isSuccess = true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		    return isSuccess;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 *  SPM / HFL Take-Out
	 * param YardID : 야드구분
	 * param WorkID : S SPM, H HFL
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callTakeOut(String YardID, String WorkID, String ProcessID, String CoilNo, String Position, String TakeOutProcess){	

		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO craneSchDAO         = new CraneSchDAO();

		String WBook_Str = "";
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/*
             * 6. Take-Out
             *    6-1.Take-Out은 입측 또는 출측 전체에서 일어 날수 있다.
             *    6-2.먼저 Take-Out 요구가 일어나면 입측 2개, 출측 2개의 설비번지에서 검색을 한다.
             *    6-3.요구 발생 해당동에 대상재가 없다면 Take-Out 요구가 일어나는 위치로 옮겨서 정보 처리한다.      
             *    6-4.옮길때는 먼저 다른동의 설비번지에 있는 대상재를 Read 한다.
             *    6-5.옮겨야할 설비번지로 Insert 한다.
             *    6-6.다른동의 설비번지에서 대상재를 Delete 한다. 
		     */ 

			logger.println(LogLevel.DEBUG,this,"Start-callTakeOut()");				
			logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< CoilNo="+ CoilNo.trim());

			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId  = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ CoilNo.trim() });

			if (StockCoilNo == null){
				logger.println(LogLevel.DEBUG,this, "StockCoilNo Error");
				return false;				
			}	
			
			logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< StockCoilNo="+ StockCoilNo);
			
			String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), "");
            
			logger.print(LogLevel.DEBUG,this, "stockId="+ stockId);
			
			List list = new ArrayList();
			list.add(CoilNo.trim());
			boolean exsitCoilNo = ydStockDAO.isExistPrimaryKey(list);

			logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< exsitCoilNo="+ exsitCoilNo);
			
			if(exsitCoilNo) {
				logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< wbookId="+ wbookId);
				
		    	// 저장품  Table에 작업예약_ID(WBookID)가 존재한다면 Error
		    	if (wbookId == null || wbookId.equals("")){
		    		/* 적치단(TB_YM_STACKLAYER) Table Read 
		    		 * STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
                     * Select STACK_COL_GP, substr(STACK_COL_GP,1,1) STACK_COL_GP1, substr(STACK_COL_GP,2,1) STACK_COL_GP2, 
                     *        STACK_BED_GP, STACK_LAYER_STAT 
		    		 *   From TB_YM_STACKLAYER 
		    		 *  Where STOCK_ID = ? And (STACK_LAYER_STAT = "L" OR STACK_LAYER_STAT = "U") (적치단 상태 L:적치중, U:권상전)
		    		 */     	
					String stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
					JDTORecord StackColGp  = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ stockId });

					if (StackColGp == null){
						logger.println(LogLevel.DEBUG,this, "적치단(TB_YM_STACKLAYER) Table에 존재하지 않습니다. Error");
						return false;										
					}	
					
  				    logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< StackColGp="+ StackColGp);

					String stackCol   = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"),  "");
					String stackCol1  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
					String stackCol2  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
					String stackBedGp = StringHelper.evl(StackColGp.getFieldString("STACK_BED_GP"), "");
					String stackStat  = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
					
			    	/*
			    	 * Take-Out 요구가 왔을때 수신한 Coil이 Take-Out이 왔는데 Coil이 야드 Skid에 있다면 Error 
					 */
					String TmpEquip = stackCol.substring(2,4); 
					
					logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< stackCol  ="+ stackCol);
					logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< TmpEquip  ="+ TmpEquip);
					logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< WorkID    ="+ WorkID);
					logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< ProcessID ="+ ProcessID);
					
			    	if (YardID.equals(YmCommonConst.YD_GP_1)){                      //A열연 
			    		if (WorkID.equals(YmCommonConst.WORK_SPM_E)){
			    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){      //EQL Take-Out STACK_COL_GP_1EQE01/STACK_COL_GP_1EKE01 
			    				if (stackCol.equals(YmCommonConst.STACK_COL_GP_1EQE01)||
			    					stackCol.equals(YmCommonConst.STACK_COL_GP_1FQE01)||
			    					stackCol.equals(YmCommonConst.STACK_COL_GP_1FQD01)||
									stackCol.equals(YmCommonConst.STACK_COL_GP_1GQD01)){ 
			    			    }else{ 
			    			    	return false; 
			    			    }
			    			}	
			    		}else if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
			    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){      //SPM Take-Out STACK_COL_GP_1DKE01/STACK_COL_GP_1EKE01/STACK_COL_GP_1EKD01
			    				if (stackCol.equals(YmCommonConst.STACK_COL_GP_1DKE01)||
			    					stackCol.equals(YmCommonConst.STACK_COL_GP_1EKE01)||
									stackCol.equals(YmCommonConst.STACK_COL_GP_1EKD01)){ 
			    			    }else{ 
			    			    	return false; 
			    			    }
			    			}	
			    		}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
			    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){      //HFL Take-Out STACK_COL_GP_1BFE01/STACK_COL_GP_1BFE01/STACK_COL_GP_1CFD01
			    				if (stackCol.equals(YmCommonConst.STACK_COL_GP_1BFE01) ||
			    					stackCol.equals(YmCommonConst.STACK_COL_GP_1CFD01)){ 
			    			    }else{ 
			    			    	return false;
			    			    }
			    			}	
			    		}
			    	}else if (YardID.equals(YmCommonConst.YD_GP_3)){                //B열연
			    		if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
			    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){      //SPM Take-Out STACK_COL_GP_3CKE01/STACK_COL_GP_3BKE01/STACK_COL_GP_3BKD01
			    				if (stackCol.equals(YmCommonConst.STACK_COL_GP_3CKE01) ||
			    					stackCol.equals(YmCommonConst.STACK_COL_GP_3BKE01) ||
		    						stackCol.equals(YmCommonConst.STACK_COL_GP_3BKD01)){ 
			    			    }else{ 
			    			    	return false;   
			    			    }
			    			}	
			    		}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
			    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){      //HFL Take-Out STACK_COL_GP_3AFE01/STACK_COL_GP_3BFE01/STACK_COL_GP_3BFD01
			    				if (stackCol.equals(YmCommonConst.STACK_COL_GP_3AFE01) ||
			    					stackCol.equals(YmCommonConst.STACK_COL_GP_3BFE01) ||	
		    						stackCol.equals(YmCommonConst.STACK_COL_GP_3BFD01)){
			    			    }else{ 
			    			    	return false;   
			    			    }
			    			}	
			    		}
			    	}
					
					String TmpstackCol = "";
					
					if (stackCol != null && !stackCol.equals("")){
						// 수신한 위치 정보와 DB에 있는 위치 정보가 다를때
						if (WorkID.equals(YmCommonConst.WORK_SPM_E)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								if (stackCol.equals(YmCommonConst.STACK_COL_GP_1EQE01)||stackCol.equals(YmCommonConst.STACK_COL_GP_1FQE01)){         //A열연 EQL 입측
									
									if(stackCol.equals(YmCommonConst.STACK_COL_GP_1EQE01)){
										TmpstackCol	= YmCommonConst.STACK_COL_GP_1EQE01;
										stackCol2 	= YmCommonConst.BAY_GP_E;
									}else{
										TmpstackCol	= YmCommonConst.STACK_COL_GP_1FQE01;
										stackCol2 	= YmCommonConst.BAY_GP_F;
									}
									
									int iSeq1 	= YmCommonDB.insertConveyorInfo(TmpstackCol.trim(), 
																				stockId.trim(), 
																				YmCommonConst.GBN_MIN);
									int iSeq2 	= YmCommonDB.deleteConveyorInfo(stackCol.trim(), 
																				stockId.trim());
									
									logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< A열연 EQL 입측 ="+ TmpstackCol);																					
								 
								} 								
							}

						}else if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								if (stackCol.equals(YmCommonConst.STACK_COL_GP_1DKE01)){         //A열연 SPM 입측
									
									if (Position.equals(YmCommonConst.WORK_SPM_5)){
										TmpstackCol	= YmCommonConst.STACK_COL_GP_1EKE01;
										stackCol2 	= YmCommonConst.BAY_GP_E;
										int iSeq1 	= YmCommonDB.insertConveyorInfo(TmpstackCol.trim(), 
																					stockId.trim(), 
																					YmCommonConst.GBN_MIN);
										int iSeq2 	= YmCommonDB.deleteConveyorInfo(stackCol.trim(), 
																					stockId.trim());
										
										logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< A열연 SPM 입측 ="+ TmpstackCol);																					
									}
								}else if (stackCol.equals(YmCommonConst.STACK_COL_GP_3CKE01)){   //B열연 SPM 입측
									/*
									 * 2006.12.06 SPM 5,6,7 Positin Take-Out시  B동 C/R 에 작업 지시 
									 */
									if ( Position.equals(YmCommonConst.WORK_SPM_5) || 
										 Position.equals(YmCommonConst.WORK_SPM_6) || 
										 Position.equals(YmCommonConst.WORK_SPM_7)){
										TmpstackCol	= YmCommonConst.STACK_COL_GP_3BKE01;
										stackCol2 	= YmCommonConst.BAY_GP_B;
										int iSeq1 	= YmCommonDB.insertConveyorInfo(TmpstackCol.trim(), 
																					stockId.trim(), 
																					YmCommonConst.GBN_MIN);
										int iSeq2 	= YmCommonDB.deleteConveyorInfo(stackCol.trim(), 
																					stockId.trim());																					
										logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< B열연 SPM 입측 ="+ TmpstackCol);										
									}
								}									
							}

						}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								if (stackCol.equals(YmCommonConst.STACK_COL_GP_1BFE01)){         //A열연 HFL 입측 
									TmpstackCol  = YmCommonConst.STACK_COL_GP_1BFE01;
									stackCol2 = YmCommonConst.BAY_GP_B;
//									int iSeq1 = YmCommonDB.insertConveyorInfo(
//											    TmpstackCol.trim(), stockId.trim(), YmCommonConst.GBN_MIN);
//									int iSeq2 = YmCommonDB.deleteConveyorInfo(stackCol.trim(), stockId.trim());
									
									logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< A열연 HFL 입측 ="+ TmpstackCol);
								}else if (stackCol.equals(YmCommonConst.STACK_COL_GP_3AFE01)){   //B열연 HFL 입측
									
									if (Position.equals(YmCommonConst.WORK_SPM_5)){
										TmpstackCol	= YmCommonConst.STACK_COL_GP_3BFE01;
										stackCol2 	= YmCommonConst.BAY_GP_B;
										int iSeq1 	= YmCommonDB.insertConveyorInfo(TmpstackCol.trim(), 
																					stockId.trim(), 
																					YmCommonConst.GBN_MIN);
										int iSeq2 	= YmCommonDB.deleteConveyorInfo(stackCol.trim(), 
																					stockId.trim());
										logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< B열연 HFL 입측 ="+ TmpstackCol);
									}
								}									
							}								
						}
						
                        // 적치단  Table Update(작업요구상태='S'로 변경)
						// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
						if (stackStat.trim().equals("L")){
							String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
							int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
									       YmCommonConst.STACK_LAYER_STAT_S, stockId.trim() });
							
							logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< stockId="+ stockId.trim()+ "stkColGp="+stkColGp);
						}
						
						// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
						/*
						 * SELECT  TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI') || YM_WBOOK_SEQ.NEXTVAL AS WBOOK_SELECT
						 *   FROM  DUAL
						 */
						String wBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
						JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);

						if (wBookSel == null){
							logger.println(LogLevel.DEBUG,this, "작업예약 ID 생성  Error");
							return false;																	
						}	
						
						logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< wBookSel="+ wBookSel );
						
						String wBookid  = wBookSel.getFieldString("WBOOK_SELECT");
						
						logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< wBookid="+ wBookid );
						
						// 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시) 한다.
						String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";

						// 4:Take-Out
						int wbookstockId = 0;

						if (WorkID.equals(YmCommonConst.WORK_SPM_E)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ // EQL Take-Out
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_EQTO, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
							}
						}else if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ // SPM Take-Out
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKTO, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
							}
						}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ // HFL Take-Out
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CFTO, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
							}
						}
						
						logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< stackCol1="+ stackCol1+ " stackCol2="+stackCol2);

						// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
						// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
						// A/B열연 SPM : TakeOutProcess  0,1:결번(Coil SPM지시대기 D1) 2:임시보류(TakeIn대기 D6)
						// A/B열연 HFL : TakeOutProcess  0,1:결번(Coil HFL지시대기 E1) 2:임시보류(TakeIn대기 E6)
						String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(stockId.trim(),"");
						 
						String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
							    	wBookid, sStockInfo[1], stockId.trim() });	
						
						logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< stockId="+ stockId.trim()+ "stkId="+stkId);
						
						logger.println(LogLevel.DEBUG,this,"End-callTakeOut()");

						String TmpSchcode = "";
						if (WorkID.equals(YmCommonConst.WORK_SPM_E)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_EQTO;  //Coil EQL Take-Out
							}
						}else if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CKTO;  //Coil SPM Take-Out
							}
						}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CFTO;  //Coil HFL Take-Out
							}
						}
						
						/*
						 *select count(SCH_ID) as SchcodeCount from USRYMA.TB_YM_SCH 
						 * Where SCH_WORK_KIND = TmpSchcode And YD_GP = YardID
						 */
						String selectStockSchID     = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockSchIDCount";
						JDTORecord selectstockschid = ydStockDAO.getData(selectStockSchID, new Object[]{ TmpSchcode.trim(), YardID });

						String tmpSchcodeCount      = StringHelper.evl(selectstockschid.getFieldString("SCHCODECOUNT"), "");
						
						int schedulecount           = Integer.parseInt(tmpSchcodeCount);
						
						logger.println(LogLevel.DEBUG,this, "callTakeOut() 작업예약 완료 스케줄 = " + TmpSchcode.trim() + " / 스케줄테이블 스케줄 코드 갯수 = "+ schedulecount);
						
						if (schedulecount < 2){
							//Coil Schedule EJB Call
							logger.println(LogLevel.DEBUG,this, "callTakeOut() 스케줄 Call="+ wBookid.trim() + " / stockId="+ stockId.trim());
							EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
							Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookid.trim() });														
						}							
					}
		    	}				
			}
			
			isSuccess = true;	
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		    return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 보조작업 -> 주작업 가능여부 Check (CGS)
	 *
	 * 현재 보급하고자 하는 코일이 1단에 위치할 경우
	 * 상단의 코일이 정정 작업인지 체크
	 * param YardID
	 * param sSchWorkKind
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws Exception 
	 * @throws RemoteException 
	 * @throws 
	 */ 
	//private String checkSubWorkExist(String YardID,  String sSchWorkKind, String CoilNo) throws RemoteException, Exception {
	public String checkSubWorkExist(String YardID,  String sSchWorkKind, String CoilNo) throws RemoteException, Exception {
		
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		YdStockDAO ydStockDAO 	= new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		CraneSchDAO craneDAO 	= new CraneSchDAO();
		YdWBookDAO ydWBookDAO 	= new YdWBookDAO();
		
		logger.println(LogLevel.DEBUG,this, "========SPM 보조작업 Check 함수 시작====");
		/*
		 * 전달된 CoilNo가 현재 보조작업으로 등록되어 있는지 확인
		 -- ym.steelinfo.steelinforecv.YdStockDAO.getSubSchSPM
			SELECT A.SCH_ID AS SCH_ID,
			       A.STOCK_ID AS COIL_NO,
			       A.WBOOK_ID AS WBOOK_ID,
			       A.WBOOK_ID AS WBOOK_ID,
			       A.SCH_WORK_KIND AS SCH_WORK_KIND,
			       A.SCH_WORK_AID_YN AS SCH_WORK_AID_YN
			FROM   TB_YM_SCH A
			WHERE  A.WBOOK_ID = 
			    (SELECT B.WBOOK_ID
			        FROM   TB_YM_SCH B,
			               TB_YM_STOCK C
			        WHERE  B.STOCK_ID = C.STOCK_ID(+)
			        AND    B.WBOOK_ID = C.WBOOK_ID(+)
			        AND    B.SCH_WORK_KIND = :sch_work_kind
			        AND    B.STOCK_ID = :stock_id )
		 */	
		String sQueryId	 = "ym.steelinfo.steelinforecv.YdStockDAO.getSubSchSPM";
		List listSch = dao.getCommonList(sQueryId, new Object[]{sSchWorkKind, CoilNo});
		
		boolean existSubSchWork = false;
		String mWorkCoilNo ="";  
		List schID = new ArrayList();
		List coilList = new ArrayList();
		
		for (int i = 0; i < listSch.size();  i++) {
				
			JDTORecord jdtSch = (JDTORecord)listSch.get(i);
			schID.add(jdtSch.getFieldString("SCH_ID"));
			coilList.add(jdtSch.getFieldString("COIL_NO"));
			logger.println(LogLevel.DEBUG,this, "SPM 보급 작업 SCH_ID="+jdtSch.getFieldString("SCH_ID"));
			
			/*
			 * 보조작업으로 등록되어 있으면 true
			 */
			if ((jdtSch.getFieldString("COIL_NO").equals(CoilNo)) && jdtSch.getFieldString("SCH_WORK_AID_YN").equals("S")) {
				existSubSchWork = true;
				
				logger.println(LogLevel.DEBUG,this, "existSubSchWork :"+existSubSchWork);
				
			}else if(jdtSch.getFieldString("SCH_WORK_AID_YN").equals("M")){
				mWorkCoilNo = jdtSch.getFieldString("COIL_NO");
			}
		}		
		
		/*
		 * 기존 등록된 스케줄 삭제
		 */
		EJBConnector ejbCon = new EJBConnector("default", "JNDICraneSchReg", this);
		
		if (existSubSchWork) {
			for(int j=0;j < schID.size() ; j++ ) {	
				logger.println(LogLevel.DEBUG,this, "삭제 SchID :"+schID.get(j));
				ejbCon.trx("cancelCoilSchInfo",new Class[]{ String.class}, new Object[]{ schID.get(j) });		
			}
			
			/*
			 * 작업 예약 삭제
			 */
			String sWbookId = "";
			String stockMoveTerm = "";
			String sCoilNo ="";
			for(int k=0; k < coilList.size(); k++) {
				sCoilNo = (String)coilList.get(k);
				
				JDTORecord stockJr = craneDAO.getStockInfo(sCoilNo);
				sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
				stockMoveTerm = StringHelper.evl(stockJr.getFieldString("STOCK_MOVE_TERM"), "");
				
				//적치단 업데이트
				String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
				int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
						                  YmCommonConst.STACK_LAYER_STAT_L, sCoilNo.trim() });
				
				//저장품 업데이트
				String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				int stkId  = ydStockDAO.requestupdateData(stkQueryId, new Object[]{"", stockMoveTerm.trim(), sCoilNo.trim() });
				
				//작업 예약 삭제
				ydWBookDAO.deleteWbookInfo(sWbookId.trim());
			}
		}
		
		/*
		 * CoilNo return
		 */
		logger.println(LogLevel.DEBUG,this, "========SPM 보급 보조작업 Check 함수 End (전달 CoilNo:"+mWorkCoilNo+")====");
		return mWorkCoilNo;
	}
	
	/**
	 * 오퍼레이션명 : 조업 정정작업 check (2009.02.20 KBK 추가)
	 *
	 *  현재 보급지시된 코일이 조업 SPM 정정작업으로 등록되어 있는지 Check
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws Exception 
	 * @throws RemoteException 
	 * @throws 
	 */ 
	public List checkPoJungWorkExist(String CoilNo) throws RemoteException, Exception {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		CraneSchDAO craneDAO 	= new CraneSchDAO();
		YdStockDAO ydDAO = new YdStockDAO();
		
		List coilList = new ArrayList();
		coilList.clear();
		
		logger.println(LogLevel.DEBUG,this, "===SPM 조업 정정Check 함수 시작===");
		/*
		 * getStackLayerListWithStockId 쿼리문
		   SELECT 
			   b.stack_col_gp	                                 as cur_stack_col_gp,      -- 현재 적치 열
			   decode(to_number(b.stack_bed_gp) - 1,
			   		  0, null, 
					  lpad(to_number(b.stack_bed_gp)-1,2,'0'))   as pre_stack_bed_gp,  
			   decode(b.stack_layer_gp, '01','02', null) 		 as pre_stack_layer_gp,
			   b.stack_bed_gp	    					         as cur_stack_bed_gp,	   -- 현재 적치 대
			   b.stack_layer_gp 					             as cur_stack_layer_gp,    -- 현재 적치 단
			 case
			      when to_number(b.stack_bed_gp) >=	
				 (
				    SELECT to_number(max(k.stack_bed_gp)) 
				    FROM tb_ym_stacklayer k
				    WHERE  k.stack_col_gp = b.stack_col_gp
				 ) 
			      then null
			      else  b.stack_bed_gp
			   end 		 			   		                     as back_stack_bed_gp,
			   decode(b.stack_layer_gp, '01','02', null) 		 as back_stack_layer_gp
			FROM  tb_ym_stacklayer b
			WHERE b.stock_id 	   		    = :stock_id
			AND   b.stack_layer_stat 		in( :stack_layer_stat,'U','L')
		 */
		JDTORecord stockLayerInfo = craneDAO.getStackLayerListWithStockId(CoilNo, "");
		if (stockLayerInfo.size() <= 0) {
			logger.println(LogLevel.DEBUG,this, "stack_layer_stat 상태가 이상한 경우 확인 요망 :"+coilList);
			return coilList;
		}
		
		String sStackColGp 			= StringHelper.evl(stockLayerInfo.getFieldString("CUR_STACK_COL_GP"), "");
		String sStackBedGp 			= StringHelper.evl(stockLayerInfo.getFieldString("CUR_STACK_BED_GP"), "");
		String sStackLayerGp 		= StringHelper.evl(stockLayerInfo.getFieldString("CUR_STACK_LAYER_GP"), "");
		String sPreStackBedGp 		= StringHelper.evl(stockLayerInfo.getFieldString("PRE_STACK_BED_GP"), "");
		String sPreStackLayerGp 	= StringHelper.evl(stockLayerInfo.getFieldString("PRE_STACK_LAYER_GP"), "");
		String sBackStackBedGp 		= StringHelper.evl(stockLayerInfo.getFieldString("BACK_STACK_BED_GP"), "");
		String sBackStackLayerGp 	= StringHelper.evl(stockLayerInfo.getFieldString("BACK_STACK_LAYER_GP"), "");
		
		logger.println(LogLevel.DEBUG,this, "CUR_STACK_COL_GP   : "+sStackColGp);
		logger.println(LogLevel.DEBUG,this, "CUR_STACK_BED_GP   : "+sStackBedGp);
		logger.println(LogLevel.DEBUG,this, "CUR_STACK_LAYER_GP : "+sStackLayerGp);
		logger.println(LogLevel.DEBUG,this, "PRE_STACK_BED_GP   : "+sPreStackBedGp);
		logger.println(LogLevel.DEBUG,this, "PRE_STACK_LAYER_GP : "+sPreStackLayerGp);
		logger.println(LogLevel.DEBUG,this, "BACK_STACK_BED_GP  : "+sBackStackBedGp);
		logger.println(LogLevel.DEBUG,this, "BACK_STACK_LAYER_GP: "+sBackStackLayerGp);
		/*
		 * 코일의 적치단이 2단일 경우 -> Skip
		 */
		if(sStackLayerGp.equals("02")) {
			//coilList = null;
			logger.println(LogLevel.DEBUG,this, "코일의 적치단이 2단일 경우 -> Skip. CoilNo :"+CoilNo);
			logger.println(LogLevel.DEBUG,this, "코일의 적치단이 2단일 경우 -> Skip. Return List :"+coilList);
			return coilList;
		/*
		 * 코일의 적치단이 1단일 경우
		 */
		}else if(sStackLayerGp.equals("01")) {
			
			String sPlantGp = YmCommonConst.YD_GP_B;	// 공장구분(A-A열연,B-B열연)
			String sProcGp = "K";						// 공정구분(K-SPM,H-HFL)
			
			/*
			 * 조업 정정 정보(작업지시단위명 : WORD_UNIT_NAME) 읽어오기
			 * 
			   SELECT  a.coil_no as goods_no,
				       a.step_no,			-- 차수
				       a.work_stat,			-- 작업 상태 
				       a.word_unit_name,    -- 작업지시 단위 명
				       a.plant_gp,          -- 공장구분(A-A열연,B-B열연)
				       a.proc_gp            -- 공정구분(K-SPM,H-HFL)
				FROM   USRPOA.TB_PO_SHEARORDPRIOR a,
				       (	SELECT coil_no, MAX(step_no) as step_no
				        	FROM   USRPOA.TB_PO_SHEARORDPRIOR
				        	WHERE  plant_gp = :plant_gp
				        	AND    proc_gp = :proc_gp
				        	AND    coil_no = :coil_no
				        	AND    work_stat NOT IN ('0','1')
				        	GROUP BY coil_no 
				       ) b
				WHERE  a.coil_no = b.coil_no
				AND    a.step_no = b.step_no
				AND    a.plant_gp = :plant_gp
				AND    a.proc_gp = :proc_gp
				AND    a.coil_no = :coil_no
				AND    a.work_stat NOT IN ('0','1')
			 */
			String queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getPmStockInfo_03";
			List poJungList = dao.getCommonList(queryCode,new Object[]{sPlantGp, sProcGp, CoilNo,
																		sPlantGp,sProcGp, CoilNo});		
			if (poJungList.size() > 0) {
				
				JDTORecord jdtPOJungList = (JDTORecord)poJungList.get(0);
				String sWordUnitName = jdtPOJungList.getFieldString("WORD_UNIT_NAME");
				logger.println(LogLevel.DEBUG,this, "조업 WORD_UNIT_NAME="+sWordUnitName);
				
				/*
				 * 정정 작업 정보 : 보급 요구된 코일의 작업단위의 Coil 리스트
				 * 
						SELECT 
						    a.coil_no as goods_no,    	-- 코일번호
						    a.step_no,    				-- 차수
						    a.work_stat,  				-- 작업상태(<> 0)
						    a.word_unit_name, 			-- 작업지시 단위 명
						    a.plant_gp, 				-- 공장구분(A-A열연,B-B열연)
						    a.proc_gp 					-- 공정구분(K-SPM,H-HFL)
						FROM USRPOA.TB_PO_SHEARORDPRIOR a,
						    (
							    SELECT coil_no, MAX(step_no) as step_no
							    FROM USRPOA.TB_PO_SHEARORDPRIOR
							    WHERE plant_gp       = :plant_gp
							    AND   proc_gp        = :proc_gp
							    AND   word_unit_name = :word_unit_name
							    AND   work_stat NOT IN ('0','1')
							    GROUP BY coil_no
						    )b
						WHERE a.coil_no = b.coil_no
						AND   a.step_no = b.step_no       
						AND   a.plant_gp       = :plant_gp
						AND   a.proc_gp        = :proc_gp
						AND   a.word_unit_name = :word_unit_name
						AND   a.work_stat NOT IN ('0','1')
				 */
				List poWorkStockList = ydDAO.getPoPmStockInfo(sPlantGp, sProcGp, sWordUnitName);
				int workStockSize = poWorkStockList.size();
				logger.println(LogLevel.DEBUG,this, "보조작업 COIL NO 검색. CNT="+String.valueOf(workStockSize) );
				/*
				 * 적치단 2단 1번지 보조작업 CoilNo가져오기(left 보조작업)
				 */
				if (!"".equals(sPreStackBedGp)) {
					
					String subLeftWorkCoilNo="";
					JDTORecord sLeftStackLayer= dao.readStackLayer(sStackColGp, sPreStackBedGp, sPreStackLayerGp);	
					
					if (sLeftStackLayer.size() > 0) {
						subLeftWorkCoilNo =  StringHelper.evl(sLeftStackLayer.getFieldString("STOCK_ID"),"");
						logger.println(LogLevel.DEBUG,this, "보조작업 COIL NO(left)="+subLeftWorkCoilNo);
					}
					
					if (!subLeftWorkCoilNo.equals("")) {
						
						logger.println(LogLevel.DEBUG,this,"보조작업 COIL NO(left) 존재. ");
						/*
						 * 정정작업에 포함된 Coil인지 확인(left)
						 */		
						boolean  isLeftStockPOExist = false;
						//SELECT COIL_NO, PROC_GP, WORD_UNIT_NAME, WORK_STAT
						//FROM USRPOA.TB_PO_SHEARORDPRIOR
						//WHERE COIL_NO=:coilno
						//AND PLANT_GP='B'
						//String sQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getPmStockInfo_04";
						for (int i = 0; i < workStockSize; i++) {
							JDTORecord jdtPOSubStockList = (JDTORecord)poWorkStockList.get(i);
							
							logger.println(LogLevel.DEBUG,this,"Record검사: "+jdtPOSubStockList);
							
							if (jdtPOSubStockList.getFieldString("WORK_STAT").equals("2") && 
									jdtPOSubStockList.getFieldString("GOODS_NO").equals(subLeftWorkCoilNo)) {
										isLeftStockPOExist = true;
										logger.println(LogLevel.DEBUG,this, "조업정정작업 Coil(Left적치) ="+isLeftStockPOExist);
							}
						}	
							
						if (isLeftStockPOExist) {		
							coilList.add(subLeftWorkCoilNo);
						}
					}				
				}
				
				/*
				 * 적치단 2단 1번지 보조작업 CoilNo가져오기(right 보조작업)
				 */
				if (!"".equals(sBackStackBedGp)) {
					
					String subRightWorkCoilNo="";
					JDTORecord sRightStackLayer= dao.readStackLayer(sStackColGp, sBackStackBedGp, sBackStackLayerGp);	
					
					if (sRightStackLayer.size() > 0) {
						subRightWorkCoilNo = StringHelper.evl(sRightStackLayer.getFieldString("STOCK_ID"),"");
						logger.println(LogLevel.DEBUG,this, "보조작업 COIL NO(right)="+subRightWorkCoilNo);
					}
					
					if (!subRightWorkCoilNo.equals("")) {
						/*
						 *  정정작업에 포함된 Coil인지 확인(right)
						 */
						boolean  isRightStockPOExist = false;									
						for (int i = 0; i < workStockSize; i++) {
							JDTORecord jdtPOSubStockList = (JDTORecord)poWorkStockList.get(i);
							logger.println(LogLevel.DEBUG,this,"Record검사: "+jdtPOSubStockList);
							
							if (jdtPOSubStockList.getFieldString("WORK_STAT").equals("2") && 
									jdtPOSubStockList.getFieldString("GOODS_NO").equals(subRightWorkCoilNo)) {	
										isRightStockPOExist = true;
										logger.println(LogLevel.DEBUG,this, "조업정정작업 Coil(right적치) ="+isRightStockPOExist);
							}
						}
				
					  if (isRightStockPOExist) {
						coilList.add(subRightWorkCoilNo);
					  }
					}
				}
			}
			
		}
		
		/*
		 * CoilNo(List) return
		 */
		
		logger.println(LogLevel.DEBUG,this, "return List "+ coilList);
		logger.println(LogLevel.DEBUG,this, "===SPM 조업 정정Check 함수 End ===");
		return coilList;
	}

	private void LogMgProcess(String YardID, String WorkID, String ProcessID, String CoilNo, String Msg) {
	    EJBConnector ejbCon = null;
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			if (YardID.equals(YmCommonConst.YD_GP_1)){                //A열연
				if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
					if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
						createLog(YmCommonConst.MODEL_POYM004, "A열연 SPM 보급          " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
						createLog(YmCommonConst.MODEL_POYM004, "A열연 SPM 추출          " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){
						createLog(YmCommonConst.MODEL_POYM004, "A열연 SPM Take In " + Msg.trim() + " Coil : " + CoilNo.trim());
					}
				}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
					if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
						createLog(YmCommonConst.MODEL_POYM004, "A열연 HFL 보급          " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
						createLog(YmCommonConst.MODEL_POYM004, "A열연 HFL 추출          " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){
						createLog(YmCommonConst.MODEL_POYM004, "A열연 HFL Take In " + Msg.trim() + " Coil : " + CoilNo.trim());
					}
				}
			}else if (YardID.equals(YmCommonConst.YD_GP_3)){          //B열연
				if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
					if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
						createLog(YmCommonConst.MODEL_POYM004, "B열연 SPM 보급        " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
						createLog(YmCommonConst.MODEL_POYM004, "B열연 SPM 추출         " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){
						createLog(YmCommonConst.MODEL_POYM004, "B열연 SPM Take In " + Msg.trim() + " Coil : " + CoilNo.trim());
					}					
				}else if (WorkID.equals(YmCommonConst.WORK_HFL_H)){
					if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
						createLog(YmCommonConst.MODEL_POYM004, "B열연 HFL 보급     " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
						createLog(YmCommonConst.MODEL_POYM004, "B열연 HFL 추출     " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){
						createLog(YmCommonConst.MODEL_POYM004, "B열연 HFL Take In " + Msg.trim() + " Coil : " + CoilNo.trim());
					}					
				}
			}	    	
	    }catch (Exception e) {	        
	    }
	}
	
	private void createLog(String tcId, String msg) {
	    EJBConnector ejbCon = null;
	    try {	      
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			ejbCon = new EJBConnector("default", "JNDIYMLog", this);
	        ejbCon.trx("createLog", new Class[]{ String.class, String.class }, new Object[]{ tcId, msg });
	    }catch (Exception e) {	        
	    }
	}
	
	// CGS 추가
	// 조업 DB를 읽어 조업의 설비의 보급과 추출 위치의 소재를 동기화한다.
	/**
	 * 오퍼레이션명 : 조업의 설비의 상황을 동기화한다.
	 *
	 *  SPM / HFL Line In, Line Out 위치를 동기화한다.
	 * param YardId : 야드구분
	 * param WorkId : S SPM, H HFL
	 * param ProcessId : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean SyncTrackingConv(String YardId,String WorkId,String ProcessId) /*throws RemoteException, Exception*/
	{
		logger.println(LogLevel.DEBUG,this, "==== SyncTrackingConv("+YardId+","+WorkId+","+ProcessId+") START ====");
		// SyncTrackingConv(YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S,YmCommonConst.PROCESS_ID_1)
		// 로직
		// 조업 데이터베이스에서 인자에 따라서 코일 번호 가져옴
		// 야드에서 설비의 01, 02의 데이터를 가져옴.
		// 두 위치를 비교한다.
/*		
		SPM
			비교한 위치가 서로 다른 경우 
			1. 스크랩일 경우 : 
								출측의 위치 판단 마지막 두 위치일 경우에 처리되도록 한다.? - 아닐 수도 있다. 
								스크랩의 위지 파악. 스크랩 번호에 따른 위치 파악. 
								현재 SPM 상의 야드 Stacklayer비교. 두 DB간 위치 동기화:해당스크랩번호의 위치를 동기화한다.
								작업예약시 시작위치 변경.
								
			
			2. 일반 코일 소재일 경우
					보급인 경우
							ECC1, ECC2를 비교한다.
								보급위치 조회 -> 보급위치 동기화 -> 보급위치 판단 -> 작업예약
					추출인 경우
							DCC8, DCC9를 비교한다.
								추출위치 조회 -> 추출위치 동기화 -> 추출 가능소재의 위치를 동기화:코일번호를 옮긴다.
								작업예약시 시작위치 변경.
			3. 보급여부판단 값을 리턴한다.    **** 일단 보류 ****
					1 : "01" 위치 가능
					2 : "01", "02" 위치 가능
*/			
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "==== 설비의 보급과 추출 위치 동기화 START ====");
			// 쿼리를 위한 인자
			String sProcGp 				= "";	// PROC_GP
			String sEquipGp01 		= "";	// EQUIP_GP 01
			String sEquipGp02 		= "";	// EQUIP_GP 02
			String sEquipGp03			= "";	// EQUIP_GP 03
			String sStackCol 			= "";	// 적치열
			String sStackLayer01 	= "";	// 적치대
			String sStackLayer02 	= "";	// 적치대
			String sStackLayer03  = "";	// 적치대
			
			CraneSchDAO craneDao = new CraneSchDAO();
			
			List listTrk_po = new ArrayList();
			listTrk_po.clear();
			
			List listTrk_ym = new ArrayList();
			listTrk_ym.clear();
			
			// 조업 DB의 위치정보 데이터 조회 쿼리
			/*
			select EQUIP_GP, STL_NO from TB_PO_ABHRTRACKING
			where equip_gp IN ( :sEquip_gp01, :sEquip_gp02 )
			and plant_gp = 'B'
			and proc_gp = :sProcGp
			order by equip_gp asc
			*/
			String sQueryId_po = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getTrackingLoc_po";
			
			// 야드 DB의 데이터 조회 쿼리
			/*
			select stack_col_gp,Stock_id, stack_bed_gp from tb_ym_stacklayer
			where stack_col_gp = :sStackCol
			and stack_bed_gp in ('01', '02')
			order by stack_Col_gp asc
			*/
			String sQueryId_ym = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getTrackingLoc_ym";
			//String sQueryId_ym_stat = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getTrackingLoc_ym_stat";
			// 결과 리턴 값
			JDTORecord jtrPo_val = null;
			JDTORecord jtrYm_val = null;
			
			// 조회된 결과 비교를 위해 사용되는 변수
			String[] sPO_CoilNo = {"","",""};
			
			String[] sYM_CoilNo = {"","",""};
			
			String[] sYM_LayerStat = {"","",""};		// 적치 단 상태 데이터 저장
			
			
			if (YardId.equals(YmCommonConst.YD_GP_3) )
			{
				if (WorkId.equals(YmCommonConst.WORK_SPM_S) && ProcessId.equals(YmCommonConst.PROCESS_ID_1 ) ) 					// #1 SPM 보급 
				{	
					sProcGp = "K";
					sEquipGp01 = "ECC1";
					sEquipGp02 = "ECC2";
					sStackCol = "3CKE01";
					sStackLayer01 = "01";
					sStackLayer02 = "02";
					logger.println(LogLevel.DEBUG,this, "#1 SPM 보급 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.WORK_SPM_S) && ProcessId.equals(YmCommonConst.PROCESS_ID_3 ))			// #1 SPM 추출
				{
					sProcGp = "K";
					sEquipGp01 = "DCC7";
					sEquipGp02 = "DCC8";
					sEquipGp03 = "DCC9";
					sStackCol = "3AKD01";
					sStackLayer01 = "01";
					sStackLayer02 = "02";
					sStackLayer03 = "03";
					logger.println(LogLevel.DEBUG,this, "#1 SPM 추출 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.NEW_WORK_SPM_N) && ProcessId.equals(YmCommonConst.PROCESS_ID_1))	// #2 SPM 보급
				{
					sProcGp = "N";
					sEquipGp01 = "";
					sEquipGp02 = "";
					sStackCol = "3DKE01";
					sStackLayer01 = "01";
					sStackLayer02 = "02";
					logger.println(LogLevel.DEBUG,this, "#2 SPM 보급 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.NEW_WORK_SPM_N) && ProcessId.equals(YmCommonConst.PROCESS_ID_3))	// #2 SPM 추출
				{
					sProcGp = "N";
					sEquipGp01 = "";
					sEquipGp02 = "";
					sStackCol = "";
					sStackLayer01 = "01";
					sStackLayer02 = "02";
					sStackLayer03 = "03";
					logger.println(LogLevel.DEBUG,this, "#2 SPM 추출 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.WORK_HFL_H) && ProcessId.equals(YmCommonConst.PROCESS_ID_1 )) 		// HFL 보급
				{
					sProcGp = "H";
					logger.println(LogLevel.DEBUG,this, "HFL 보급 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.WORK_HFL_H) && ProcessId.equals(YmCommonConst.PROCESS_ID_3 ))			// HFL 추출
				{
					sProcGp = "H";
					logger.println(LogLevel.DEBUG,this, "HFL 추출 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}
			}
			// 현재 설비의 적치열 상태를 가져온다.
			// 쿼리 실행.
			listTrk_po = craneDao.getListData(sQueryId_po,new Object[]{sEquipGp01,sEquipGp02,sProcGp});
			//listTrk_ym = craneDao.getListData(sQueryId_ym,new Object[]{sStackCol,sStackLayer01,sStackLayer02});
			listTrk_ym = craneDao.getListData(sQueryId_ym,new Object[]{sStackCol});
			
			// 조업DB에서 가져온 데이터 처리
			for(int idx=0;idx<listTrk_po.size();idx++)
			{
				jtrPo_val = (JDTORecord)listTrk_po.get(idx);
				sPO_CoilNo[idx] = StringHelper.evl(jtrPo_val.getFieldString("STL_NO"),"");
				logger.println(LogLevel.DEBUG,this, "조업DB 조회 "+String.valueOf(idx+1)+":"+sPO_CoilNo[idx]);
			}
			
			// 야드DB에서 가져온 데이터 처리
			for(int idx=0;idx<listTrk_ym.size();idx++)
			{
				jtrYm_val = (JDTORecord)listTrk_ym.get(idx);
				sYM_CoilNo[idx] = StringHelper.evl(jtrYm_val.getFieldString("STOCK_ID"),"");
				sYM_LayerStat[idx] = StringHelper.evl(jtrYm_val.getFieldString("STACK_LAYER_STAT"),"");
				logger.println(LogLevel.DEBUG,this, "야드DB 조회 "+String.valueOf(idx+1)+":"+sYM_CoilNo[idx]);
			}
			
			// 데이터를 비교한다.
			// 각 조건에 따라서 번지를 입력한 메소드로 각각에 맞게 호출하여 준다.
			logger.println(LogLevel.DEBUG,this, "위치 동기화 PO["+sPO_CoilNo[0]+" | "+sPO_CoilNo[1]+"]");
			logger.println(LogLevel.DEBUG,this, "위치 동기화 YM["+sYM_CoilNo[0]+" | "+sYM_CoilNo[1]+"]");
			
			if (ProcessId.equals(YmCommonConst.PROCESS_ID_1))	// 보급
//			if (sStackCol.substring(2,4).equals("KE"))					// 입측 비교
			{
				if (   ( sPO_CoilNo[0] == null || sPO_CoilNo[0].equals("")) 
					&& ( sPO_CoilNo[1] == null || sPO_CoilNo[1].equals("")) 	// 조업 01,02 모두 비었음.
				   )
				{
					logger.println(LogLevel.DEBUG,this, "위치 동기화 : 조업-소재존재안함.");
					if(    ( sYM_CoilNo[0] == null ||  sYM_CoilNo[0].equals("") )
						&& ( sYM_CoilNo[1] != null && !sYM_CoilNo[1].equals("") ) 	// 야드 02에만 소재 존재
					  )
					{
						if (sYM_LayerStat[1].equals("L") /*|| (sYM_LayerStat[0].equals("P")&& sYM_LayerStat[1].equals("L"))*/ ) {
							// shift 처리
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer02);
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 2번위치만 소재 존재, 2번만 이동");
							return true;
						}else {
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 2번위치만 소재 존재, 적치된 상태가 아님.적치상태(02):"+sYM_LayerStat[1]);
							return false;
						}
					}else if (   (sYM_CoilNo[0] != null && !sYM_CoilNo[0].equals("") )
							  && (sYM_CoilNo[1] == null ||  sYM_CoilNo[1].equals("") )	// 야드 01에만 소재 존재
							 )
					{
						if(sYM_LayerStat[0].equals("L")) {
							// shift 처리
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer01);
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer02);
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 1번위치만 소재 존재, 두 번(1,2) 이동 ");
							
							
							return true;
						}else {
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 1번위치만 소재 존재, 적치된 상태가 아님.적치상태(01):"+sYM_LayerStat[0]);
							return false;
						}
					}else if ( 
							    ( sYM_CoilNo[0] != null && !sYM_CoilNo[0].equals("") ) 
							&&  ( sYM_CoilNo[1] != null && !sYM_CoilNo[1].equals("")  ) 	// 야드 01,02 모두 소재 존재.
							)
					{
						if ( (sYM_LayerStat[0].equals("L") && sYM_LayerStat[1].equals("L")) ||  (sYM_LayerStat[0].equals("P") && sYM_LayerStat[1].equals("L"))) {
							// shift 처리
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer01);
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer02);
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 소재 모두 존재, 두 번(1,2) 이동. STAT:"+sYM_LayerStat[0] +"-"+ sYM_LayerStat[1] );
							return true;
						}else {
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 소재 모두 존재, 적치된 상태가 아님.적치상태(01):"+sYM_LayerStat[0]+"적치상태(02)"+sYM_LayerStat[1]);
							return false;
						}
					}
//					else
//					{
//						// PASS
//						logger.println(LogLevel.DEBUG,this, "위치 동기화 PASS: 조건에 맞지 않음.");
//						return true;
//					}
				}else if ( 	(sPO_CoilNo[0] == null ||  sPO_CoilNo[0].equals("") )
						&& 	(sPO_CoilNo[1] != null && !sPO_CoilNo[1].equals("") )		// 조업 01만 비었음.
						)
				{
					logger.println(LogLevel.DEBUG,this, "위치 동기화 : 조업-2번위치만 소재 존재");
					if (    ( sYM_CoilNo[0] != null && !sYM_CoilNo[0].equals("") )
						&&  ( sYM_CoilNo[1] != null && !sYM_CoilNo[1].equals("") ) 	// 야드 01,02 모두 소재 존재.
					   )
					{
						if (sYM_LayerStat[0].equals("L") || (sYM_LayerStat[0].equals("P") && sYM_LayerStat[1].equals("L"))) {
							logger.println(LogLevel.DEBUG, this, "위치 동기화 :야드 1번위치에 소재 존재, 1번위치 -> 2번위치 이동");
							// shift 처리
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer01);
							
							return true;
						}else {
							logger.println(LogLevel.DEBUG, this, "위치 동기화 :야드 1번위치만 소재 존재, 적치된 상태가 아님.적치상태(01):"+sYM_LayerStat[0]);
							return false;
						}
					}else if( sYM_CoilNo[0] == null && sYM_CoilNo[0].equals("") ){	// 1번위치 소재 없음. 적치 가능
						logger.println(LogLevel.DEBUG, this, "위치 동기화 :야드 1번위치 적치가능 ");
						return false;
					}
//					else
//					{
//						// pass
//						logger.println(LogLevel.DEBUG,this, "위치 동기화 PASS");
//						return true;
//					}
				}else if(	(sPO_CoilNo[0] != null || !sPO_CoilNo[0].equals("") )
						&& 	(sPO_CoilNo[1] != null && !sPO_CoilNo[1].equals("") )						
						)
				{								// 조업이 빈 곳이 없음.
					logger.println(LogLevel.DEBUG,this, "위치 동기화 : 조업- 빈 위치가 없음.");
					return true;
				}
			}
			else if (ProcessId.equals(YmCommonConst.PROCESS_ID_3))												// 추출측 비교 사용하지 않음.최규성
			{
				if(sPO_CoilNo[0]==null && sPO_CoilNo[1]==null && sPO_CoilNo[2] == null)
				{
				}else if (sPO_CoilNo[0] != null && sPO_CoilNo[1] == null && sPO_CoilNo[2]==null)
				{
				}else if (sPO_CoilNo[0] != null && sPO_CoilNo[1] != null && sPO_CoilNo[2]==null)
				{
				}else if (sPO_CoilNo[0] != null && sPO_CoilNo[1] != null && sPO_CoilNo[2]!=null)
				{
				}else if (sPO_CoilNo[0] != null && sPO_CoilNo[1] == null && sPO_CoilNo[2]!=null)
				{
				}else if (sPO_CoilNo[0] == null && sPO_CoilNo[1] != null && sPO_CoilNo[2]!=null)
				{
				}else if (sPO_CoilNo[0] == null && sPO_CoilNo[1] != null && sPO_CoilNo[2]==null)
				{
				}else if (sPO_CoilNo[0] == null && sPO_CoilNo[1] == null && sPO_CoilNo[2]!=null)
				{
				}
			}			
			return false;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 오퍼레이션명 : 조업의 설비의 상황을 동기화한다.
	 *
	 *  SPM / HFL Line In, Line Out 위치를 동기화한다.
	 * param YardId : 야드구분
	 * param WorkId : S SPM, H HFL
	 * param ProcessId : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean SyncTrackingConv2(String YardId,String WorkId,String ProcessId) /*throws RemoteException, Exception*/
	{
		logger.println(LogLevel.DEBUG,this, "==== SyncTrackingConv("+YardId+","+WorkId+","+ProcessId+") START ====");
		// SyncTrackingConv(YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S,YmCommonConst.PROCESS_ID_1)
		// 로직
		// 조업 데이터베이스에서 인자에 따라서 코일 번호 가져옴
		// 야드에서 설비의 01, 02의 데이터를 가져옴.
		// 두 위치를 비교한다.
/*		
		SPM
			비교한 위치가 서로 다른 경우 
			1. 스크랩일 경우 : 
								출측의 위치 판단 마지막 두 위치일 경우에 처리되도록 한다.? - 아닐 수도 있다. 
								스크랩의 위지 파악. 스크랩 번호에 따른 위치 파악. 
								현재 SPM 상의 야드 Stacklayer비교. 두 DB간 위치 동기화:해당스크랩번호의 위치를 동기화한다.
								작업예약시 시작위치 변경.
								
			
			2. 일반 코일 소재일 경우
					보급인 경우
							ECC1, ECC2를 비교한다.
								보급위치 조회 -> 보급위치 동기화 -> 보급위치 판단 -> 작업예약
					추출인 경우
							DCC8, DCC9를 비교한다.
								추출위치 조회 -> 추출위치 동기화 -> 추출 가능소재의 위치를 동기화:코일번호를 옮긴다.
								작업예약시 시작위치 변경.
			3. 보급여부판단 값을 리턴한다.    **** 일단 보류 ****
					1 : "01" 위치 가능
					2 : "01", "02" 위치 가능
*/			
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "==== 설비의 보급과 추출 위치 동기화 START ====");
			// 쿼리를 위한 인자
			String sProcGp 				= "";	// PROC_GP
			String sEquipGp01 		= "";	// EQUIP_GP 01
			String sEquipGp02 		= "";	// EQUIP_GP 02
			String sEquipGp03			= "";	// EQUIP_GP 03
			String sStackCol 			= "";	// 적치열
			String sStackLayer01 	= "";	// 적치대
			String sStackLayer02 	= "";	// 적치대
			String sStackLayer03  = "";	// 적치대
			
			CraneSchDAO craneDao = new CraneSchDAO();
			
			List listTrk_po = new ArrayList();
			listTrk_po.clear();
			
			List listTrk_ym = new ArrayList();
			listTrk_ym.clear();
			
			// 조업 DB의 위치정보 데이터 조회 쿼리
			/*
			select EQUIP_GP, STL_NO from TB_PO_ABHRTRACKING
			where equip_gp IN ( :sEquip_gp01, :sEquip_gp02 )
			and plant_gp = 'B'
			and proc_gp = :sProcGp
			order by equip_gp asc
			*/
			String sQueryId_po = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getTrackingLoc_po";
			
			// 야드 DB의 데이터 조회 쿼리
			/*
			select stack_col_gp,Stock_id, stack_bed_gp from tb_ym_stacklayer
			where stack_col_gp = :sStackCol
			and stack_bed_gp in ('01', '02')
			order by stack_Col_gp asc
			*/
			String sQueryId_ym = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getTrackingLoc_ym";
			//String sQueryId_ym_stat = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getTrackingLoc_ym_stat";
			// 결과 리턴 값
			JDTORecord jtrPo_val = null;
			JDTORecord jtrYm_val = null;
			
			// 조회된 결과 비교를 위해 사용되는 변수
			String[] sPO_CoilNo = {"","",""};
			
			String[] sYM_CoilNo = {"","",""};
			
			String[] sYM_LayerStat = {"","",""};		// 적치 단 상태 데이터 저장
			
			
			if (YardId.equals(YmCommonConst.YD_GP_3) )
			{
				if (WorkId.equals(YmCommonConst.WORK_SPM_S) && ProcessId.equals(YmCommonConst.PROCESS_ID_1 ) ) 					// #1 SPM 보급 
				{	
					sProcGp = "K";
					sEquipGp01 = "ECC7";
					sEquipGp02 = "ECC7";
					sStackCol = "3BKE01";
					sStackLayer01 = "01";
					sStackLayer02 = "02";
					logger.println(LogLevel.DEBUG,this, "#1 SPM 보급 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.WORK_SPM_S) && ProcessId.equals(YmCommonConst.PROCESS_ID_3 ))			// #1 SPM 추출
				{
					sProcGp = "K";
					sEquipGp01 = "DCC7";
					sEquipGp02 = "DCC8";
					sEquipGp03 = "DCC9";
					sStackCol = "3AKD01";
					sStackLayer01 = "01";
					sStackLayer02 = "02";
					sStackLayer03 = "03";
					logger.println(LogLevel.DEBUG,this, "#1 SPM 추출 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.NEW_WORK_SPM_N) && ProcessId.equals(YmCommonConst.PROCESS_ID_1))	// #2 SPM 보급
				{
					sProcGp = "N";
					sEquipGp01 = "";
					sEquipGp02 = "";
					sStackCol = "3DKE01";
					sStackLayer01 = "01";
					sStackLayer02 = "02";
					logger.println(LogLevel.DEBUG,this, "#2 SPM 보급 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.NEW_WORK_SPM_N) && ProcessId.equals(YmCommonConst.PROCESS_ID_3))	// #2 SPM 추출
				{
					sProcGp = "N";
					sEquipGp01 = "";
					sEquipGp02 = "";
					sStackCol = "";
					sStackLayer01 = "01";
					sStackLayer02 = "02";
					sStackLayer03 = "03";
					logger.println(LogLevel.DEBUG,this, "#2 SPM 추출 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.WORK_HFL_H) && ProcessId.equals(YmCommonConst.PROCESS_ID_1 )) 		// HFL 보급
				{
					sProcGp = "H";
					logger.println(LogLevel.DEBUG,this, "HFL 보급 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}else if (WorkId.equals(YmCommonConst.WORK_HFL_H) && ProcessId.equals(YmCommonConst.PROCESS_ID_3 ))			// HFL 추출
				{
					sProcGp = "H";
					logger.println(LogLevel.DEBUG,this, "HFL 추출 데이터 "+sProcGp+" "+sEquipGp01+" "+sEquipGp02+" "+sStackCol+" "+sStackLayer01+" "+sStackLayer02);
				}
			}
			// 현재 설비의 적치열 상태를 가져온다.
			// 쿼리 실행.
			listTrk_po = craneDao.getListData(sQueryId_po,new Object[]{sEquipGp01,sEquipGp02,sProcGp});
			//listTrk_ym = craneDao.getListData(sQueryId_ym,new Object[]{sStackCol,sStackLayer01,sStackLayer02});
			listTrk_ym = craneDao.getListData(sQueryId_ym,new Object[]{sStackCol});
			
			// 조업DB에서 가져온 데이터 처리
			for(int idx=0;idx<listTrk_po.size();idx++)
			{
				jtrPo_val = (JDTORecord)listTrk_po.get(idx);
				sPO_CoilNo[idx] = StringHelper.evl(jtrPo_val.getFieldString("STL_NO"),"");
				logger.println(LogLevel.DEBUG,this, "조업DB 조회 "+String.valueOf(idx+1)+":"+sPO_CoilNo[idx]);
			}
			
			// 야드DB에서 가져온 데이터 처리
			for(int idx=0;idx<listTrk_ym.size();idx++)
			{
				jtrYm_val = (JDTORecord)listTrk_ym.get(idx);
				sYM_CoilNo[idx] = StringHelper.evl(jtrYm_val.getFieldString("STOCK_ID"),"");
				sYM_LayerStat[idx] = StringHelper.evl(jtrYm_val.getFieldString("STACK_LAYER_STAT"),"");
				logger.println(LogLevel.DEBUG,this, "야드DB 조회 "+String.valueOf(idx+1)+":"+sYM_CoilNo[idx]);
			}
			
			// 데이터를 비교한다.
			// 각 조건에 따라서 번지를 입력한 메소드로 각각에 맞게 호출하여 준다.
			logger.println(LogLevel.DEBUG,this, "위치 동기화 PO["+sPO_CoilNo[0]+" | "+sPO_CoilNo[1]+"]");
			logger.println(LogLevel.DEBUG,this, "위치 동기화 YM["+sYM_CoilNo[0]+" | "+sYM_CoilNo[1]+"]");
			
			if (ProcessId.equals(YmCommonConst.PROCESS_ID_1))	// 보급
//			if (sStackCol.substring(2,4).equals("KE"))					// 입측 비교
			{
				if (   ( sPO_CoilNo[0] == null || sPO_CoilNo[0].equals("")) 
					&& ( sPO_CoilNo[1] == null || sPO_CoilNo[1].equals("")) 	// 조업 01,02 모두 비었음.
				   )
				{
					logger.println(LogLevel.DEBUG,this, "위치 동기화 : 조업-소재존재안함.");
					if(    ( sYM_CoilNo[0] == null ||  sYM_CoilNo[0].equals("") )
						&& ( sYM_CoilNo[1] != null && !sYM_CoilNo[1].equals("") ) 	// 야드 02에만 소재 존재
					  )
					{
						if (sYM_LayerStat[1].equals("L") /*|| (sYM_LayerStat[0].equals("P")&& sYM_LayerStat[1].equals("L"))*/ ) {
							// shift 처리
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer02);
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 2번위치만 소재 존재, 2번만 이동");
							return true;
						}else {
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 2번위치만 소재 존재, 적치된 상태가 아님.적치상태(02):"+sYM_LayerStat[1]);
							return false;
						}
					}else if (   (sYM_CoilNo[0] != null && !sYM_CoilNo[0].equals("") )
							  && (sYM_CoilNo[1] == null ||  sYM_CoilNo[1].equals("") )	// 야드 01에만 소재 존재
							 )
					{
						if(sYM_LayerStat[0].equals("L")) {
							// shift 처리
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer01);
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer02);
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 1번위치만 소재 존재, 두 번(1,2) 이동 ");
							
							
							return true;
						}else {
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 1번위치만 소재 존재, 적치된 상태가 아님.적치상태(01):"+sYM_LayerStat[0]);
							return false;
						}
					}else if ( 
							    ( sYM_CoilNo[0] != null && !sYM_CoilNo[0].equals("") ) 
							&&  ( sYM_CoilNo[1] != null && !sYM_CoilNo[1].equals("")  ) 	// 야드 01,02 모두 소재 존재.
							)
					{
						if ( (sYM_LayerStat[0].equals("L") && sYM_LayerStat[1].equals("L")) ||  (sYM_LayerStat[0].equals("P") && sYM_LayerStat[1].equals("L"))) {
							// shift 처리
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer01);
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer02);
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 소재 모두 존재, 두 번(1,2) 이동. STAT:"+sYM_LayerStat[0] +"-"+ sYM_LayerStat[1] );
							return true;
						}else {
							logger.println(LogLevel.DEBUG,this, "위치 동기화 :야드 소재 모두 존재, 적치된 상태가 아님.적치상태(01):"+sYM_LayerStat[0]+"적치상태(02)"+sYM_LayerStat[1]);
							return false;
						}
					}
//					else
//					{
//						// PASS
//						logger.println(LogLevel.DEBUG,this, "위치 동기화 PASS: 조건에 맞지 않음.");
//						return true;
//					}
				}else if ( 	(sPO_CoilNo[0] == null ||  sPO_CoilNo[0].equals("") )
						&& 	(sPO_CoilNo[1] != null && !sPO_CoilNo[1].equals("") )		// 조업 01만 비었음.
						)
				{
					logger.println(LogLevel.DEBUG,this, "위치 동기화 : 조업-2번위치만 소재 존재");
					if (    ( sYM_CoilNo[0] != null && !sYM_CoilNo[0].equals("") )
						&&  ( sYM_CoilNo[1] != null && !sYM_CoilNo[1].equals("") ) 	// 야드 01,02 모두 소재 존재.
					   )
					{
						if (sYM_LayerStat[0].equals("L") || (sYM_LayerStat[0].equals("P") && sYM_LayerStat[1].equals("L"))) {
							logger.println(LogLevel.DEBUG, this, "위치 동기화 :야드 1번위치에 소재 존재, 1번위치 -> 2번위치 이동");
							// shift 처리
							YmCommonDB.shiftConveyorInfo(sStackCol,sStackLayer01);
							
							return true;
						}else {
							logger.println(LogLevel.DEBUG, this, "위치 동기화 :야드 1번위치만 소재 존재, 적치된 상태가 아님.적치상태(01):"+sYM_LayerStat[0]);
							return false;
						}
					}else if( sYM_CoilNo[0] == null && sYM_CoilNo[0].equals("") ){	// 1번위치 소재 없음. 적치 가능
						logger.println(LogLevel.DEBUG, this, "위치 동기화 :야드 1번위치 적치가능 ");
						return false;
					}
//					else
//					{
//						// pass
//						logger.println(LogLevel.DEBUG,this, "위치 동기화 PASS");
//						return true;
//					}
				}else if(	(sPO_CoilNo[0] != null || !sPO_CoilNo[0].equals("") )
						&& 	(sPO_CoilNo[1] != null && !sPO_CoilNo[1].equals("") )						
						)
				{								// 조업이 빈 곳이 없음.
					logger.println(LogLevel.DEBUG,this, "위치 동기화 : 조업- 빈 위치가 없음.");
					return true;
				}
			}
			else if (ProcessId.equals(YmCommonConst.PROCESS_ID_3))												// 추출측 비교 사용하지 않음.최규성
			{
				if(sPO_CoilNo[0]==null && sPO_CoilNo[1]==null && sPO_CoilNo[2] == null)
				{
				}else if (sPO_CoilNo[0] != null && sPO_CoilNo[1] == null && sPO_CoilNo[2]==null)
				{
				}else if (sPO_CoilNo[0] != null && sPO_CoilNo[1] != null && sPO_CoilNo[2]==null)
				{
				}else if (sPO_CoilNo[0] != null && sPO_CoilNo[1] != null && sPO_CoilNo[2]!=null)
				{
				}else if (sPO_CoilNo[0] != null && sPO_CoilNo[1] == null && sPO_CoilNo[2]!=null)
				{
				}else if (sPO_CoilNo[0] == null && sPO_CoilNo[1] != null && sPO_CoilNo[2]!=null)
				{
				}else if (sPO_CoilNo[0] == null && sPO_CoilNo[1] != null && sPO_CoilNo[2]==null)
				{
				}else if (sPO_CoilNo[0] == null && sPO_CoilNo[1] == null && sPO_CoilNo[2]!=null)
				{
				}
			}			
			return false;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * scrap 테스트를 위한 임시 메소드
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSPMConStat(String YD,String WORK, String PROCESSID, String COILNO ) { 
		
		boolean isSuccess = false;
		boolean isConvSub = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveSPMConStat(String,String,String,String)");
			
			YdStockDAO ydStockDAO = new YdStockDAO();
			
			int iResult = 0;
/*			
			String YardID    = pOYM004.getYardId();
			String WorkID    = pOYM004.getWorkId(); 
			String ProcessID = pOYM004.getProcessId();
			String CoilNo    = pOYM004.getCoilNo();
			String Position  = pOYM004.getPosition();  // 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String TakeOutProcess  = pOYM004.getTakeOutProcess();  // Take-Out시 1:결번, 2:임시보류처리(잠시 내려놨다가 Take-In할 Coil) 
*/
			String YardID    = YD;
			String WorkID    = WORK; 
			String ProcessID = PROCESSID;
			String CoilNo    = COILNO;
			String Position  = "";  // 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String TakeOutProcess  = "";//pOYM004.getTakeOutProcess();  // Take-Out시 1:결번, 2:임시보류처리(잠시 내려놨다가 Take-In할 Coil) 
			
			//LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "POYM004 수신");
			
			/**
			 * @param YardID : 야드구분
			 * @param WorkID : S SPM, H HFL
			 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In 
			 * @param CoilNo :  'S' Scrap  'H' A열연 'K' B열연 byCGS
			 */
			
			// 조업 Level-3에서 SPM, HFL 구분
			// 공정에 따라 분기. ProcessID기준.
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                           // 보급
				// 소재가 야드에 있을 경우 true, 소재가 그외 위치에 있을 경우엔 false 반환 
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 보급 isSuccess =" + isSuccess);
				
				if (isSuccess){	
					
//					/*
//					 * [기능 추가 : (2009.02.10 KBK)]
//					 * 현재 보급하고자 하는 코일이 1단에 위치할 경우
//					 * 상단(2단)의 코일이 정정 작업인지 체크
//					 */
//					logger.println(LogLevel.DEBUG,this,"상단의 코일 검사 = "+CoilNo);
//					// CGS
//					// 보조작업이지만 주작업 가능한 코일번호를 반환한다.
//					List sCoilNoList = checkPoJungWorkExist(CoilNo);
//					
//					logger.println(LogLevel.DEBUG,this,"sCoilNoList Size=" + sCoilNoList.size());
//					logger.println(LogLevel.DEBUG,this,"sCoilNoList "+sCoilNoList);
//					
//					// CGS 추가 
//					// checkPoJungWorkExist() 결과  보급요구 소재가 2단에 적치된 경우는 List size 가 0
//					//                             보급요구 소재가 1단에 적치된 경우는 List size 가 최소 1이상이다.
//					
//					// 공통코일정보 테이블에서 보급 요구된 코일정보
//					String queryID      = "ym.common.dao.selectCommonCoilInfo";
//					JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
//					
//					for (int i =0; i < sCoilNoList.size() ; i++) 
//					{					
//						JDTORecord sCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ sCoilNoList.get(i) });
//						// 보급요구된 저장품의 두께, 폭,
//						
//						//int mCoilThickness = mCommonCoilinfo.getFieldInt("COIL_T");
//						//int mCoilWidth = mCommonCoilinfo.getFieldInt("COIL_W");
//						
//						float fCoilThickness = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_T"));
//						float fCoilWidth     = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_W"));
//						
//						// 시스템 판단 코일소재의 두께, 폭
//						//int sCoilThickness = sCommonCoilinfo.getFieldInt("COIL_T");
//						//int sCoilWidth = sCommonCoilinfo.getFieldInt("COIL_W");
//						float fCoilThickness_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_T"));
//						float fCoilWidth_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_W"));
//						
//						
//						// CGS
//						// 코일두께와 폭을 비교해서 허용 오차 범위에 있을 경우에만 보급.
//						// 오차범위 기준은 임시로 지정. 테스트시 정확한 기준을 받아서 수정한다.
//						// 기준 데이터 타입(int -> float) 을 변경한다.  최규성 2009-12-04
//						String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilInfoStd";
//						JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"X2"});
//						
//						int nStdThick = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
//						int nStdWidth = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
//
//						//float fStdThick = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
//						//float fStdWidth = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
//
//						
//						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
//						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
//						
//						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= nStdThick )  
//								&& (Math.abs(fCoilWidth - fCoilWidth_l)     <= nStdWidth) 
//							   )
//						//if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= fStdThick )  
//						//		&& (Math.abs(fCoilWidth - fCoilWidth_l)     <= fStdWidth) 
//						//	   )	
//						{
//							// CGS 추가 
//							// 스케줄 호출하기전 To위치에 대한 정보를 검사한다.
//							
//							isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), (String)sCoilNoList.get(i));
//							LogMgProcess(YardID, WorkID, ProcessID, (String)sCoilNoList.get(i), "보급작업예약 편성완료-주/보조 구분");
//							logger.println(LogLevel.DEBUG,this,"보급작업예약 편성완료-주/보조 구분[" +(String)sCoilNoList.get(i)+ "]");
//							
//							if (isSuccess) isConvSub = true;
//						}
//					}	// for END

					// CGS 추가
					// 요구된 코일에 대한 작업예약 및 스케줄 생성한다.
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "요구된 보급 작업예약 편성완료");
					logger.println(LogLevel.DEBUG,this,"요구된 보급 작업예약 편성완료 ["+CoilNo+"]" );
					//isSuccess = SPMHFLLineIn(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
//					isSuccess = SPMHFLLineInRequestSearch(YardID.trim(), WorkID.trim(), ProcessID.trim());
			    }
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){                     // 추출
				// SPM/HFL 여부 검사
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 추출 isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "추출 작업예약 편성완료");
				}
									
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){                     // Take-Out
				isSuccess = callTakeOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim(), TakeOutProcess.trim());
				LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-Out 작업예약 편성완료");
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                     // Take-In
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< Take-In isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-In 작업예약 편성완료");
				}
			}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveSPMConStat(POYM004)");
			
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	/*사용안함.최규성 2009-11-30*/
	public boolean checkScrapInfoInFacility(String YardID, String WorkID, String ProcessID, String CoilNo){
		String sMethodName= "checkScrapInfoInFacility()";
		boolean bIsSuccess = false;
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();

		logger.println(LogLevel.DEBUG,this, "Start - "+sMethodName);
		
		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sQueryId_scrap = "ym.steelinfo.steelinforecv.YdStockDAO.selectScrapInfo";
			List listScrapInfo = ydStockDAO.getListData(sQueryId_scrap, new Object[]{ CoilNo.substring(0,1) });

			int nScrapCnt = listScrapInfo.size();
			
			JDTORecord jtrScrap = null;
			String sWBookId = "";
			String sStockId = "";
			for(int i=0; i< nScrapCnt; i++)
			{
				jtrScrap = (JDTORecord)listScrapInfo.get(i);

				//sWBookId = jtrScrap.getFieldString("WBOOK_ID");
				sStockId = jtrScrap.getFieldString("STOCK_ID");
				
				bIsSuccess = deleteScrapLocInfo(sStockId);
				
			}

		}catch(DAOException daoe){
		        throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}



		logger.println(LogLevel.DEBUG,this, "End - "+sMethodName);
		return false;
	}
	/*사용안함. 최규성 2009-11-30*/
	public boolean deleteScrapLocInfo(String sStockId) {
		boolean isSuccess = false;
		CraneSchDAO dao	= new CraneSchDAO();

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iReq = 0;
			
			String sTmpStockId		= "";
			String sTmpStat			= "";
			
			String sWbookId = "";
			String sSchId   = "";
			
			JDTORecord stockV = dao.getStockInfo(sStockId);
			
			/**
			 *	1.	작업예약 유무 체크
			 */
			if(stockV != null){
				sWbookId = StringHelper.evl(stockV.getFieldString("WBOOK_ID"),"");
			}
			logger.println(LogLevel.DEBUG, this, "Scrap정보=> 작업예약ID="+sWbookId);
			
			if(!"".equals(sWbookId)){
			
				/*
				 *	1.1	저장품 Table의 Wbook_id 항목을 Update
				 *		tb_ym_stock Table wbook_id : ''(empty)
				 */	 
			
				iReq = dao.updateStockWbookId(sStockId,""); 
				/**
				 *	2.	스케쥴정보 있으면 삭제
				 */
				JDTORecord schV	= dao.getSchInfoWithWbookId(sWbookId,sStockId);
			 	 		
				if(schV != null){
					/**
					 *	2.1	스케쥴 취소 모듈 CALL
					 */
					sSchId = StringHelper.evl(schV.getFieldString("SCH_ID"),""); 
					logger.println(LogLevel.DEBUG, this, "Scrap정보=> 스케쥴ID="+sSchId);
					
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					Boolean isTemp  = (Boolean)ejbConn.trx("cancelCoilSchInfo",
												new  Class[]{String.class},
												new Object[]{sSchId});
				}
				
				/**
				 *	3.	작업예약정보 있으면 삭제
				 */
				int iSeq = dao.deleteWbookInfo(sWbookId);
				logger.println(LogLevel.DEBUG, this, "Scrap정보=> 작업예약 삭제="+iSeq);
			}
			
			String sUpStackColGp    = "";
			String sUpStackBedGp    = "";
			String sUpStackLayerGp  = "";
			String sUpUsageCd 		= "";
			
			/**
			 *	4.	저장품의 MAP정보를 가져온다.
			 *		중복위치도 체크한다.
			 */
			/*
			SELECT * 
			FROM tb_ym_stacklayer
			WHERE stock_id = :stock_id
			*/
			List stockL	= dao.getStackLayerInfoWithStockId_03(sStockId);
			
//			JDTORecord stackV = null;
//			JDTORecord upRc   = null;
//			 
//			if(stockL != null)
//			{	 
//				for(int inx = 0; inx < stockL.size() ; inx++)
//				{
//					stackV = (JDTORecord)stockL.get(inx);
//					
//					sUpStackColGp   = StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
//					sUpStackBedGp   = StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
//					sUpStackLayerGp = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");
//					
//					sUpUsageCd 		= YmCommonUtil.getStackColInfoWithPk(sUpStackColGp);
//					
//					if(YmCommonConst.STACK_COL_GP_1BDC01.equals(sUpStackColGp)  ||// A열연 COIL 분기콘베이어
//					   YmCommonConst.STACK_COL_GP_1CDC01.equals(sUpStackColGp)  ||// A열연 COIL 분기콘베이어	
//					   YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)	||// COIL 비상적치위치
//					   YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sUpUsageCd)	||// COIL HFL보급위치
//					   YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sUpUsageCd)	||// COIL HFLTAKEIN위치
//					   YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sUpUsageCd)	||// COIL HFL추출위치
//					   YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sUpUsageCd)	||// COIL SPM보급위치
//					   YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sUpUsageCd)	||// COIL SPMTAKEIN위치
//					   YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sUpUsageCd)	){// COIL SPM추출위치
	//
//						int iSeq = YmCommonDB.deleteConveyorInfo(sUpStackColGp,
//										  			 	 		 sStockId);
//						if(iSeq < 0){
//							//throw new EJBServiceException("=권상실적=>CONVEYOR DELETE FAIL.");
//							logger.println(LogLevel.DEBUG,this, "산적위치 수정=> 적치단 삭제 FAIL");
//						}	
//					}else{	
//					 	/* 
//						 * 적치단 UP위치 Clear
//						 * tb_ym_stacklayer Table : stock_id 		 = ''(Empty)
//						 * tb_ym_stacklayer Table : stack_layer_stat = 'E'(적치가능)
//						 */	
//						iReq = dao.updateCraneStackLayerStat(sUpStackColGp,
//															 sUpStackBedGp,
//															 sUpStackLayerGp,
//															 "",
//															 YmCommonConst.STACK_LAYER_STAT_E);
	//
//						/**
//						 *	FROM 위치 상단 적치상태 수정
//						 */
//					 	if(YmCommonConst.STACK_LAYER_GP_01.equals(sUpStackLayerGp)){
//					    	
//					    	/*
//					    	 * A.B열연 Coil 권상실적	
//					    	 * 상단 왼쪽 상태정보를 UPDATE
//					    	 * 상단 오른쪽 상태정보를 UPDATE
//					    	 */	
//					    	iReq = YmCommonDB.setCoilUpperState_V(sUpStackColGp,
//						    							 	   	  sUpStackBedGp,
//						    							 	   	  sUpStackLayerGp);
//						}	
//					}
//					logger.println(LogLevel.DEBUG, this, "정정실적=> FROM 위치 수정 = "+ iReq);	
//				} 
//			} 
			isSuccess = true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }     
		return isSuccess;
	}
	
	
	
 	/**
	 * 오퍼레이션명 : 
	 *
	 *  조업 LEVEL3로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
   	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  TREAT_GP :  1 보급, 2 보급취소, 3 추출
	 * @return
	 * @throws 
	 */ 
	public JDTORecord receiveHFLConStat(JDTORecord msgRecord) throws JDTOException  {	
		YdDaoUtils ydDaoUtils 	= new YdDaoUtils();
		JDTORecord outRecord     	= JDTORecordFactory.getInstance().create(); // 
		boolean isSuccess = false;
		boolean isConvSub = false;
		JDTORecord recEdit      = null;
		JDTORecord jtrScrap		= null;
		String CoilNo = "";
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveHFLConStat()");
			
			YdStockDAO ydStockDAO = new YdStockDAO();
			
			int iResult = 0;
			String YardID	 = YmCommonUtil.paraRecChkNull(msgRecord, "YardId");
			String ProcessID = ydDaoUtils.paraRecChkNull(msgRecord, "ProcessId");  // 1 보급 ,2보급취소 ,3추출
			String sEQP_GP    = ydDaoUtils.paraRecChkNull(msgRecord, "WorkId");//D
			String sCoilNo    = ydDaoUtils.paraRecChkNull(msgRecord, "CoilNo");
			String Position  		= YmCommonUtil.paraRecChkNull(msgRecord, "Position");

			
			
			logger.println(LogLevel.DEBUG,this, "C열연정정 보급 요구 - 설비 :: " + "[" + sEQP_GP +"]");

			
			// 공정에 따라 분기. ProcessID기준.
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                           // 보급
				
//				// HFL결속대로 보급 요구된 코일정보를 가져옴 (6H대상)
//				String queryID      = "com.inisteel.cim.ym.getYmStklyrHRShearWK";
//				List rsGetStock = ydStockDAO.getListData(queryID, new Object[]{ sEQP_GP.trim() });
//				
//				
//				for (int Loop_i = 0; Loop_i < rsGetStock.size(); Loop_i++){
//				
//					jtrScrap = (JDTORecord)rsGetStock.get(Loop_i);
//					
//					CoilNo = ydDaoUtils.paraRecChkNull(jtrScrap,"STL_NO");	
//					logger.println(LogLevel.DEBUG,this,"요구된 보급 주작업예약 편성 ["+CoilNo+"]" );
//					
//					// 요구된 코일에 대한 작업예약 및 스케줄 생성한다.
//					isSuccess = callLineInOut(YardID.trim(), "D" , ProcessID.trim(), CoilNo.trim());
//
//				} //for 문 종료
				
				
				logger.println(LogLevel.DEBUG,this,"요구된 보급 주작업예약 편성 ["+sCoilNo+"]" );
				
				// 요구된 코일에 대한 작업예약 및 스케줄 생성한다.
				isSuccess = callLineInOut(YardID.trim(), "D", ProcessID.trim(), sCoilNo.trim());
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){                     // 추출
				
				logger.println(LogLevel.DEBUG,this, ">>receiveHFLConStat()<< 추출 isSuccess =" + isSuccess);
				
				isSuccess = callLineInOut(YardID.trim(), "D", ProcessID.trim(), sCoilNo.trim());
									
			}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveHFLConStat");
			
			if (isSuccess){
				outRecord.setField("RTN_CD" , "1");	
			}else{
				outRecord.setField("RTN_CD" , "0");
			}
			return outRecord;
	    	
	    }catch(Exception e){
	    	outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", "조업열연정정지시실적작업관리 에러)");	
			return outRecord;
	    }
	}
//////////////////////////////////////////////////////////////////////////////
/////////////////////B열연수정시작///////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
	/**
	 * 오퍼레이션명 : 
	 *
	 *  조업 LEVEL3로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
	 *  전문내용을 JDTORecord로 파싱한다.
	 *  업무 로직
	 *	1.TC_CD - POYM004 (I/F ID : YM-LIF-020 )
	 *	2.조업 LEVEL3로부터 SPM / HFL 작업 요구 정보를 수신
	 *
	 *    조업에서 수신한 위치정보를 야드 적치열에 대한 위치로 변환 해야됨
	 *    조업에서는 Saddle No로 정의 하고 있음.
	 *    수신한 위치 정보는 Saddle No로 되어 있으니 각동에 위치한 Saddle No를 파악해서
	 *    야드 Map 적치열에 맞게 변환할것.
	 * 
	 * A열연 EQL
	 * 1. 보급(Line In)  : 적치열(1EQE01), Schedule Code(EQLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 2. Take-In       : 적치열(1EQE01), Schedule Code(EQTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 3. Take-Out      : 적치열(1EQE01), Schedule Code(EQTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 4. Take-Out      : 적치열(1GQD01), Schedule Code(EQTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 5. 추출(Line Off) : 적치열(1GQD01), Schedule Code(EQLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * A열연 SPM
	 * 1. 보급(Line In)  : 적치열(1DKE01), Schedule Code(CKLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 2. Take-In       : 적치열(1EKE01), Schedule Code(CKTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 3. Take-Out      : 적치열(1EKE01), Schedule Code(CKTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 4. Take-Out      : 적치열(1EKD01), Schedule Code(CKTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 5. 추출(Line Off) : 적치열(1FKD01), Schedule Code(CKLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * B열연 SPM
	 * 1. 보급(Line In)  : 적치열(3CKE01), Schedule Code(CKLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 2. Take-In       : 적치열(3BKE01), Schedule Code(CKTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 3. Take-Out      : 적치열(3BKE01), Schedule Code(CKTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 4. Take-Out      : 적치열(3BKD01), Schedule Code(CKTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경) 
	 * 5. 추출(Line Off) : 적치열(3AKD01), Schedule Code(CKLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 
	 * A열연 HFL
	 * 1. 보급(Line In)  : 적치열(1BFE01), Schedule Code(CFLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 2. Take-In       : 적치열(1BFE01), Schedule Code(CFTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 3. Take-Out      : 적치열(1BFE01), Schedule Code(CFTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 4. Take-Out      : 적치열(1CFD01), Schedule Code(CFTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 5. 추출(Line Off) : 적치열(1CFD01), Schedule Code(CFLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * B열연 HFL
	 * 1. 보급(Line In)  : 적치열(3AFE01), Schedule Code(CFLI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 2. Take-In       : 적치열(3BFE01), Schedule Code(CFTI), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 3. Take-Out      : 적치열(3BFE01), Schedule Code(CFTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 4. Take-Out      : 적치열(3BFD01), Schedule Code(CFTO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 5. 추출(Line Off) : 적치열(3CFD01), Schedule Code(CFLO), 저장품이동조건(공통 진도코드를 Read해서 이동조건을 변경)
	 * 
	 * 
	 *  << SPM 설비 현황 >>
	 * 1. A열연 SPM
	 *      보급(Line In)  : 적치열(1DKE01), Schedule Code(CSLI), 저장품이동조건(D2)
	 *      추출(Line Off) : 적치열(1FKD01), Schedule Code(CSLO), 저장품이동조건(D3)
	 *    B열연 SPM
	 *      보급(Line In)  : 적치열(3CKE01), Schedule Code(CSLI), 저장품이동조건(D2)
	 *      추출(Line Off) : 적치열(3AKD01), Schedule Code(CSLO), 저장품이동조건(D3)
	 *   
	 *    1-1.수신한 Coil No가 저장품(TB_YM_STOCK) Table 에 존재하는지 점검
	 *    1-2.저장품 Table에 작업예약_ID가 존재한다면 Skip(Error)
	 *    1-3.적치단(TB_YM_STACKLAYER) Table Read
	 *    1-4.적치단 Table에 적치단상태(STACK_LAYER_STAT CHAR(1)) 작업요구상태='S'로 변경처리
	 *    1-5.작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
	 *    1-6.작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시)
	 *    1-7.저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
	 *    1-8.Coil Schedule EJB를 Call 한다.
	 * 
	 * 2. 보급취소
	 *    2-1. 야드 화면에서 보급취소해서 조업으로 보급취소 전문을 송신한다. 2006.1.13 
	 *       
	 * 3. Take-In
	 *    3-1.수신한 Coil No가 저장품(TB_YM_STOCK) Table 에 존재하는지 점검
	 *    3-2.저장품 Table에 작업예약_ID가 존재한다면 Skip(Error)
	 *    3-3.적치단(TB_YM_STACKLAYER) Table Read
	 *    3-4.적치단 Table에 적치단상태(STACK_LAYER_STAT CHAR(1)) 작업요구상태='S'로 변경처리
	 *    3-5.작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
	 *    3-6.작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시)
	 *    3-7.저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
	 *    3-8.Coil Schedule EJB를 Call 한다.
	 *  
	 * 4. Take-Out
	 *    4-1.Take-Out은 입측 또는 출측 전체에서 일어 날수 있다.
	 *    4-2.먼저 Take-Out 요구가 일어나면 입측 2개, 출측 2개의 설비번지에서 검색을 한다.
	 *    4-3.요구 발생 해당동에 대상재가 없다면 Take-Out 요구가 일어나는 위치로 옮겨서 정보 처리한다.      
	 *    4-4.옮길때는 먼저 다른동의 설비번지에 있는 대상재를 Read 한다.
	 *    4-5.옮겨야할 설비번지로 Insert한다.
	 *    4-6.다른동의 설비번지에서 대상재를 Delete한다.
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receivePOYM004(JDTORecord rcvMsg) { 
// 	public boolean receiveSPMConStat(POYM004 pOYM004) { 		
		boolean isSuccess = false;
		boolean isConvSub = false;
		JDTORecord   outRecord   	= JDTORecordFactory.getInstance().create();
		String sRTN_CD	="";
		JDTORecord msgRecord     	= JDTORecordFactory.getInstance().create(); // 
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveSPMConStat:receivePOYM004()");
			
			YdStockDAO ydStockDAO = new YdStockDAO();
			
			int iResult = 0;
			
//S			String YardID    = pOYM004.getYardId();
//S			String WorkID    = pOYM004.getWorkId(); 
//S			String ProcessID = pOYM004.getProcessId();
//S			String CoilNo    = pOYM004.getCoilNo();
//S			String Position  = pOYM004.getPosition();  // 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
//S			String TakeOutProcess  = pOYM004.getTakeOutProcess();  // Take-Out시 1:결번, 2:임시보류처리(잠시 내려놨다가 Take-In할 Coil) 
			
//			String YardID			= StringHelper.evl(rcvMsg.getFieldString("YardId"),"").trim();
//			String WorkID 			= StringHelper.evl(rcvMsg.getFieldString("WorkId"),"").trim();
//			String ProcessID 		= StringHelper.evl(rcvMsg.getFieldString("ProcessId"),"").trim();
//			String CoilNo 			= StringHelper.evl(rcvMsg.getFieldString("coilNo"),"").trim();
//			String Position			= StringHelper.evl(rcvMsg.getFieldString("position"),"").trim();
//			String TakeOutProcess	= StringHelper.evl(rcvMsg.getFieldString("TakeOutProcess"),"").trim();
			
			String YardID			= StringHelper.evl(rcvMsg.getFieldString("YardId"),"").trim();
			String WorkID 			= StringHelper.evl(rcvMsg.getFieldString("WorkId"),"").trim();
			String ProcessID 		= StringHelper.evl(rcvMsg.getFieldString("ProcessId"),"").trim();
			String CoilNo 			= StringHelper.evl(rcvMsg.getFieldString("CoilNo"),"").trim();
			String Position			= StringHelper.evl(rcvMsg.getFieldString("Position"),"").trim();
			String TakeOutProcess	= StringHelper.evl(rcvMsg.getFieldString("TakeOutProcess"),"").trim();
			
			
			LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "POYM004 수신");
			
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 수신항목: YardID=" + YardID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 수신항목: WorkID=" + WorkID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 수신항목: ProcessID=" + ProcessID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 수신항목: CoilNo=" + CoilNo);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 수신항목: Position=" + Position);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 수신항목: TakeOutProcess=" + TakeOutProcess);			
			
			//HFL 결속대 프로세스##################################################################
			if(WorkID.equals("D")){
				
				msgRecord.setField("YardID" , YardID);	
				msgRecord.setField("WorkId" , WorkID);	
				msgRecord.setField("ProcessId" , ProcessID);	
				msgRecord.setField("CoilNo" , CoilNo);	
				msgRecord.setField("Position" , Position);	
				msgRecord.setField("TakeOutProcess" , TakeOutProcess);	
				
				
				outRecord = this.receiveHFLConStat(msgRecord);
				sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
				
				if (!("1".equals(sRTN_CD))) {		        	
					return isSuccess;
				}
				
				return true;
			}
			//###################################################################################
			
			/**
			 * @param YardID : 야드구분
			 * @param WorkID : S SPM, H HFL ,E EQ1
			 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In 
			 * @param CoilNo :  'S' Scrap  'H' A열연 'K' B열연 byCGS
			 */
			
			// 조업 Level-3에서 SPM, HFL 구분
			// 공정에 따라 분기. ProcessID기준.
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                           // 보급
				// 소재가 야드에 있을 경우 true, 소재가 그외 위치에 있을 경우엔 false 반환 
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 보급 isSuccess =" + isSuccess);
				
				if (isSuccess){
					
					//2018.07.11 CHITO : 구용모 주임 요구에 따른 2단 더미재 보급 작업 편성기능을 막음(SPM , HFL 모두다)
//					if(!WorkID.equals("E")){					
//					//////////////////////////////////////////////////////////////////////////////////////////
//						/*
//						 * [기능 추가 : (2009.02.10 KBK)]
//						 * 현재 보급하고자 하는 코일이 1단에 위치할 경우
//						 * 상단(2단)의 코일이 정정 작업인지 체크
//						 */
//						logger.println(LogLevel.DEBUG,this,"상단의 코일 검사 = "+CoilNo);
//						// CGS
//						// 보조작업이지만 주작업 가능한 코일번호를 반환한다.
//						List sCoilNoList = checkPoJungWorkExist(CoilNo);
//						
//						logger.println(LogLevel.DEBUG,this,"sCoilNoList Size="+sCoilNoList.size());
//						logger.println(LogLevel.DEBUG,this,"sCoilNoList "+sCoilNoList);
//						
//						// CGS 추가 
//						// checkPoJungWorkExist() 결과  보급요구 소재가 2단에 적치된 경우는 List size 가 0
//						//                             보급요구 소재가 1단에 적치된 경우는 List size 가 최소 1이상이다.
//						
//						// 공통코일정보 테이블에서 보급 요구된 코일정보
//						String queryID      = "ym.common.dao.selectCommonCoilInfo";
//						JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
//						
//						for (int i =0; i < sCoilNoList.size() ; i++) 
//						{					
//							JDTORecord sCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ sCoilNoList.get(i) });
//							// 보급요구된 저장품의 두께, 폭,
//							
//							//int mCoilThickness = mCommonCoilinfo.getFieldInt("COIL_T");
//							//int mCoilWidth = mCommonCoilinfo.getFieldInt("COIL_W");
//							
//							float fCoilThickness = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_T"));
//							float fCoilWidth     = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_W"));
//							
//							// 시스템 판단 코일소재의 두께, 폭
//							//int sCoilThickness = sCommonCoilinfo.getFieldInt("COIL_T");
//							//int sCoilWidth = sCommonCoilinfo.getFieldInt("COIL_W");
//							float fCoilThickness_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_T"));
//							float fCoilWidth_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_W"));
//							
//							
//							// CGS
//							// 코일두께와 폭을 비교해서 허용 오차 범위에 있을 경우에만 보급.
//							// 오차범위 기준은 임시로 지정. 테스트시 정확한 기준을 받아서 수정한다.
//							String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilInfoStd";
//	//						JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"X2"});
//							JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"S"});
//							
//							// 입력 기준 데이터가 float 형이다. 소숫점 1자리까지.최규성 2009-12-02
//							int nStdThick = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
//							int nStdWidth = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
//							
//							
//							logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
//							logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
//							
//							if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= nStdThick )  
//									&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= nStdWidth) 
//								   )	
//	/*
//							float fStdThick = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
//							float fStdWidth = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
//							
//							logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
//							logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
//							
//							if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= fStdThick )  
//									&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= fStdWidth) 
//								   )	
//	*/
//							{
//								// CGS 추가 
//								// 스케줄 호출하기전 To위치에 대한 정보를 검사한다.
//								
//								isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), (String)sCoilNoList.get(i));
//								LogMgProcess(YardID, WorkID, ProcessID, (String)sCoilNoList.get(i), "보급 보조작업예약 편성완료-주/보조 구분");
//								logger.println(LogLevel.DEBUG,this,"보급 보조작업예약 편성완료-주/보조 구분[" +(String)sCoilNoList.get(i)+ "]");
//								
//								if (isSuccess) isConvSub = true;
//							}
//						}	// for END
//					}//////////////////////////////////////////////////////////////////////////////////////////
					
					
					
					// CGS 추가
					// 요구된 코일에 대한 작업예약 및 스케줄 생성한다.
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "요구된 보급 작업예약 편성완료");
					logger.println(LogLevel.DEBUG,this,"요구된 보급 주작업예약 편성완료 ["+CoilNo+"]" );
	
	
//					if(!WorkID.equals("E")){
//						isSuccess = SPMHFLLineInRequestSearch(YardID.trim(), WorkID.trim(), ProcessID.trim());
//					}
			    }
			
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){                     // 취소
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 보급 취소 CoilNo="+CoilNo );
				
				//코일 번호로 크레인 스케줄을 가져 온다.
				//String queryID      = "ym.common.dao.selectSchCoilInfo";
				//JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() }); 
				
				JDTORecord jrecord = JDTORecordFactory.getInstance().create();
				
				/*
				select SCH_ID
				 from USRYMA.TB_YM_SCH
				where STOCK_ID =:V_STOCK_ID
				 and SCH_WORK_STAT in('1','S')
				 */
				jrecord.setField("STOCK_ID"             , CoilNo.trim());
				
				JDTORecordSet jsScrSchList = commDao.select(jrecord, "ym.common.dao.selectSchCoilInfo", "SYSTEM", "receiveSPMConStat", "스케쥴존재 여부 조회");
				
				if(jsScrSchList.size()>0){
					//String sCH_ID = mCommonCoilinfo.getFieldString("SCH_ID");
					String sCH_ID = jsScrSchList.getRecord(0).getFieldString("SCH_ID");
					
					//권상이전 크레인 스케줄/작업예약  취소 작업
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
					Boolean resultRes = (Boolean) ejbConn1.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {CoilNo});
				
				}else{
					logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 권상 이후에는 취소가 불가능 합니다." );
				}
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){                     // 추출
				
				if(!WorkID.equals("E")){
				//**************************보관매출 체크(보관매출인경우 추출요구 시 자동으로 실적 발생 후 추출 처리******************************정종균
				
				ymCommonDAO dao = ymCommonDAO.getInstance();
				
				String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilKeepstockInfo";
				List 	list = dao.getCommonList(sQueryIdStd,new Object[]{CoilNo.trim() });	
				
				if(list.size()> 0){
					//정정실적 처리 
					String Workchk="";
					if(WorkID.equals("H")){
						Workchk="HFL";
					} else {
						Workchk="SPM";
					}
					
	
					logger.println(LogLevel.DEBUG,this, ">>★★★★★★★★★보관매출 자동정정실적처리>>>" + CoilNo.trim());
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICoilInfoReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("setInnerIFCoilInfo_02",
												new  Class[]{String.class,String.class},
												new Object[]{ CoilNo.trim() ,Workchk});					 
					
				}
				//***************************************************************************************************************
				}
				
				// SPM/HFL 여부 검사
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 추출 isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "추출 작업예약 편성완료");
				}
									
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){                     // Take-Out
				isSuccess = callTakeOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim(), TakeOutProcess.trim());
				LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-Out 작업예약 편성완료");
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                     // Take-In
				isSuccess = SpmHflProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< Take-In isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-In 작업예약 편성완료");
				}
			}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveSPMConStat(POYM004)");
			
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	//        throw new EJBServiceException(e);
	        throw new EJBServiceException("=POYM004보급요구 입측존에 코일이 존재 합니다.");
	    }
	}	
	
	
	
}

