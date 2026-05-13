package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import java.util.ArrayList;
import java.util.List;

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
import com.inisteel.cim.common.jms.model.ym.POYM010;
import com.inisteel.cim.common.jms.model.ym.POYM001;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
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
 * @ejb.bean name="SPMConStatMRegEJB" jndi-name="JNDISPMConStatMReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SPMConStatMRegSBean extends BaseSessionBean {
	Logger logger=null;
	private YmCommDAO commDao = new YmCommDAO();
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger = new Logger(config);		
	}
	
	/**
	 * 오퍼레이션명 : JDTORecord 방식으로 처리
	 * 
	 *  조업 LEVEL3로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
     *  전문내용을 JDTORecord로 파싱한다.
     *  업무 로직
     *	1.TC_CD - POYM010 (I/F ID : YM-LIF-020 )
     *	2.조업 LEVEL3로부터 SPM / HFL 작업 요구 정보를 수신
     *
     *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSPMConStatM2(JDTORecord msgRecord) throws JDTOException  {
				   
		boolean isSuccess = false;
		boolean isConvSub = false; // 보조 작업을 주작업으로 변환했는지 여부 판정.

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveSPMConStatM2()");
			
			YdStockDAO ydStockDAO = new YdStockDAO();			// 보조작업 -> 주작업 쿼리 DAO
			
			int iResult = 0;
			
			String YardID    		= YmCommonUtil.paraRecChkNull(msgRecord, "YardId");
			String WorkID    		= YmCommonUtil.paraRecChkNull(msgRecord, "WorkId"); 
			String ProcessID 		= YmCommonUtil.paraRecChkNull(msgRecord, "ProcessId");
			String CoilNo    		= YmCommonUtil.paraRecChkNull(msgRecord, "CoilNo");
			String Position  		= YmCommonUtil.paraRecChkNull(msgRecord, "Position");
			String TakeOutProcess  	= YmCommonUtil.paraRecChkNull(msgRecord, "TakeOutProcess");
			
			LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "POYM010 수신");
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: YardID=" + YardID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: WorkID=" + WorkID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: ProcessID=" + ProcessID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: CoilNo=" + CoilNo);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: Position=" + Position);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: TakeOutProcess=" + TakeOutProcess);
			
			/**
			 * SPM2의 HFL기능 추가. 최규성 2010-02-08
			 * @param YardID : 야드구분
			 * @param WorkID : S 기존 SPM, N 신규 SPM, F 신규SPM내 HFL
			 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			 * @param CoilNo:  'S' Scrap  'H' A열연  'K' B열연 
			 */
			
			// 작업구분
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                           // 보급
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 보급 isSuccess =" + isSuccess);
				
				
				if (isSuccess){
					// 상단 코일의 정정 여부 판정.
					logger.println(LogLevel.DEBUG,this,">>receiveSPMConStatM2()<< 상단의 코일 검사 = "+CoilNo);
					// 최규
					// 보조작업이지만 주작업 가능한 코일번호를 반환한다.
					// 따로 함수를 만들지 않음.
					EJBConnector ejbConn = new EJBConnector("default","JNDISPMConStatReg",this);
					List sCoilNoList = new ArrayList();
					
					if (WorkID.equals("N")){		// SPM 작업일 경우에만 보조작업에 대한 정보를 처리한다.
						sCoilNoList = (List)ejbConn.trx("checkPoJungWorkExist",new Class[]{String.class},new Object[]{ CoilNo });	
					}
					
					logger.println(LogLevel.DEBUG,this,"sCoilNoList Size="+sCoilNoList.size());
					logger.println(LogLevel.DEBUG,this,"sCoilNoList "+sCoilNoList);
					// 공통코일정보 테이블에서 보급 요구된 코일정보
					String queryID      = "ym.common.dao.selectCommonCoilInfo";
					JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
					
					for (int i =0; i < sCoilNoList.size() ; i++) 
					{					
						JDTORecord sCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ sCoilNoList.get(i) });
						// 보급요구된 저장품의 두께, 폭,
						
						//int mCoilThickness = mCommonCoilinfo.getFieldInt("COIL_T");
						//int mCoilWidth = mCommonCoilinfo.getFieldInt("COIL_W");
						
						float fCoilThickness = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_T"));
						float fCoilWidth     = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_W"));
						
						// 시스템 판단 코일소재의 두께, 폭
						//int sCoilThickness = sCommonCoilinfo.getFieldInt("COIL_T");
						//int sCoilWidth = sCommonCoilinfo.getFieldInt("COIL_W");
						float fCoilThickness_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_T"));
						float fCoilWidth_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_W"));
						
						
						// CGS
						// 코일두께와 폭을 비교해서 허용 오차 범위에 있을 경우에만 보급.
						// 오차범위 기준은 임시로 지정. 테스트시 정확한 기준을 받아서 수정한다.
						String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilInfoStd";
						JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"S"});
						
						// 입력 기준 데이터가 float 형이다. 소숫점 1자리까지.최규성 2009-12-02
/*						int nStdThick = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
						int nStdWidth = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
						
						
						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
						
						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= nStdThick )  
								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= nStdWidth) 
							   )	
*/
						float fStdThick = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
						float fStdWidth = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
						
						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
						
						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= fStdThick )  
								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= fStdWidth) 
							   )	
						{
							// CGS 추가 
							// 스케줄 호출하기전 To위치에 대한 정보를 검사한다.

							isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), (String)sCoilNoList.get(i),Position);
							LogMgProcess(YardID, WorkID, ProcessID, (String)sCoilNoList.get(i), "보급 작업예약 편성완료-주/보조 구분");
							logger.println(LogLevel.DEBUG,this,"보급작업예약 편성완료-주/보조 구분[" +(String)sCoilNoList.get(i)+ "]");
							
							if (isSuccess) isConvSub = true;
						}
					}	// for END

					
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(),Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "보급 작업예약 편성완료");
					//isSuccess = SPMLineInRequestSearch(YardID.trim(), WorkID.trim(), ProcessID.trim(),Position.trim());
			    }
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){                     // 취소
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 보급 취소 CoilNo="+CoilNo );
				
				//코일 번호로 크레인 스케줄을 가져 온다.
//				String queryID      = "ym.common.dao.selectSchCoilInfo";
//				JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
//				
//				if(mCommonCoilinfo.size()>0){
//					String sCH_ID = mCommonCoilinfo.getFieldString("SCH_ID");
				JDTORecord jrecord = JDTORecordFactory.getInstance().create();
				
				/*
				select SCH_ID
				 from USRYMA.TB_YM_SCH
				where STOCK_ID =:V_STOCK_ID
				 and SCH_WORK_STAT in('1','S')
				 */
				jrecord.setField("STOCK_ID"             , CoilNo.trim());
				
				JDTORecordSet jsScrSchList = commDao.select(jrecord, "ym.common.dao.selectSchCoilInfo", "SYSTEM", "receiveSPMConStatM2", "스케쥴존재 여부 조회");
				
				if(jsScrSchList.size()>0){
					//String sCH_ID = mCommonCoilinfo.getFieldString("SCH_ID");
					String sCH_ID = jsScrSchList.getRecord(0).getFieldString("SCH_ID");
					
					//권상이전 크레인 스케줄/작업예약  취소 작업
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
					Boolean resultRes = (Boolean) ejbConn1.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {CoilNo});
				
				}else{
					logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 권상 이후에는 취소가 불가능 합니다." );
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
					} else if(WorkID.equals("S")){
						Workchk="SPM";
					} else if(WorkID.equals("N")){
						Workchk = "SPM2";
					}
					
	
					logger.println(LogLevel.DEBUG,this, ">>★★★★★★★★★보관매출 자동정정실적처리[SPM2]>>>" + CoilNo.trim());
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICoilInfoReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("setInnerIFCoilInfo_02",
												new  Class[]{String.class,String.class},
												new Object[]{ CoilNo.trim() ,Workchk});					 
					
				}
				//***************************************************************************************************************
				
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 추출 isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "추출 작업예약 편성완료");
				}
									
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){                     // Take-Out
				isSuccess = callSPMTakeOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim(), TakeOutProcess.trim());
				LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-Out 작업예약 편성완료");
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                     // Take-In
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< Take-In isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-In 작업예약 편성완료");
				}
			}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveSPMConStatM2(POYM010)");
			
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	throw new EJBServiceException("=receiveSPMConStatM2-POYM010추출요구 추출측존에 코일이 존재 안합니다.");
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 * 
	 *  조업 LEVEL3로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
     *  전문내용을 JDTORecord로 파싱한다.
     *  업무 로직
     *	1.TC_CD - POYM010 (I/F ID : YM-LIF-020 )
     *	2.조업 LEVEL3로부터 SPM / HFL 작업 요구 정보를 수신
     *
     *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSPMConStatM(POYM010 pOYM010) { 
				   
		boolean isSuccess = false;
		boolean isConvSub = false; // 보조 작업을 주작업으로 변환했는지 여부 판정.

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveSPMConStatM()");
			
			YdStockDAO ydStockDAO = new YdStockDAO();			// 보조작업 -> 주작업 쿼리 DAO
			
			int iResult = 0;
			
			String YardID    = pOYM010.getYardId();
			String WorkID    = pOYM010.getWorkId(); 
			String ProcessID = pOYM010.getProcessId();
			String CoilNo    = pOYM010.getCoilNo();
			String Position  = pOYM010.getPosition();  // 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String TakeOutProcess  = pOYM010.getTakeOutProcess();  // Take-Out시 1:결번, 2:임시보류처리(잠시 내려놨다가 Take-In할 Coil) 

			LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "POYM010 수신");
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: YardID=" + YardID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: WorkID=" + WorkID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: ProcessID=" + ProcessID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: CoilNo=" + CoilNo);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: Position=" + Position);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM2()<< 수신항목: TakeOutProcess=" + TakeOutProcess);
			
			/**
			 * SPM2의 HFL기능 추가. 최규성 2010-02-08
			 * @param YardID : 야드구분
			 * @param WorkID : S 기존 SPM, N 신규 SPM, F 신규SPM내 HFL
			 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			 * @param CoilNo:  'S' Scrap  'H' A열연  'K' B열연 
			 */
			
			// 작업구분
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                           // 보급
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 보급 isSuccess =" + isSuccess);
				
				
				if (isSuccess){
					// 상단 코일의 정정 여부 판정.
					logger.println(LogLevel.DEBUG,this,">>receiveSPMConStatM()<< 상단의 코일 검사 = "+CoilNo);
					// 최규
					// 보조작업이지만 주작업 가능한 코일번호를 반환한다.
					// 따로 함수를 만들지 않음.
					EJBConnector ejbConn = new EJBConnector("default","JNDISPMConStatReg",this);
					List sCoilNoList = new ArrayList();
					
					if (WorkID.equals("N")){		// SPM 작업일 경우에만 보조작업에 대한 정보를 처리한다.
						sCoilNoList = (List)ejbConn.trx("checkPoJungWorkExist",new Class[]{String.class},new Object[]{ CoilNo });	
					}
					
					logger.println(LogLevel.DEBUG,this,"sCoilNoList Size="+sCoilNoList.size());
					logger.println(LogLevel.DEBUG,this,"sCoilNoList "+sCoilNoList);
					// 공통코일정보 테이블에서 보급 요구된 코일정보
					String queryID      = "ym.common.dao.selectCommonCoilInfo";
					JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
					
					for (int i =0; i < sCoilNoList.size() ; i++) 
					{					
						JDTORecord sCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ sCoilNoList.get(i) });
						// 보급요구된 저장품의 두께, 폭,
						
						//int mCoilThickness = mCommonCoilinfo.getFieldInt("COIL_T");
						//int mCoilWidth = mCommonCoilinfo.getFieldInt("COIL_W");
						
						float fCoilThickness = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_T"));
						float fCoilWidth     = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_W"));
						
						// 시스템 판단 코일소재의 두께, 폭
						//int sCoilThickness = sCommonCoilinfo.getFieldInt("COIL_T");
						//int sCoilWidth = sCommonCoilinfo.getFieldInt("COIL_W");
						float fCoilThickness_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_T"));
						float fCoilWidth_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_W"));
						
						
						// CGS
						// 코일두께와 폭을 비교해서 허용 오차 범위에 있을 경우에만 보급.
						// 오차범위 기준은 임시로 지정. 테스트시 정확한 기준을 받아서 수정한다.
						String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilInfoStd";
						JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"S"});
						
						// 입력 기준 데이터가 float 형이다. 소숫점 1자리까지.최규성 2009-12-02
/*						int nStdThick = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
						int nStdWidth = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
						
						
						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
						
						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= nStdThick )  
								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= nStdWidth) 
							   )	
*/
						float fStdThick = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
						float fStdWidth = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
						
						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
						
						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= fStdThick )  
								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= fStdWidth) 
							   )	
						{
							// CGS 추가 
							// 스케줄 호출하기전 To위치에 대한 정보를 검사한다.

							isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), (String)sCoilNoList.get(i),Position);
							LogMgProcess(YardID, WorkID, ProcessID, (String)sCoilNoList.get(i), "보급 작업예약 편성완료-주/보조 구분");
							logger.println(LogLevel.DEBUG,this,"보급작업예약 편성완료-주/보조 구분[" +(String)sCoilNoList.get(i)+ "]");
							
							if (isSuccess) isConvSub = true;
						}
					}	// for END

					
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(),Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "보급 작업예약 편성완료");
					//isSuccess = SPMLineInRequestSearch(YardID.trim(), WorkID.trim(), ProcessID.trim(),Position.trim());
			    }
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){                     // 취소
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStat()<< 보급 취소 CoilNo="+CoilNo );
				
				//코일 번호로 크레인 스케줄을 가져 온다.
//				String queryID      = "ym.common.dao.selectSchCoilInfo";
//				JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
//				
//				if(mCommonCoilinfo.size()>0){
//					String sCH_ID = mCommonCoilinfo.getFieldString("SCH_ID");
				
				JDTORecord jrecord = JDTORecordFactory.getInstance().create();
				
				/*
				select SCH_ID
				 from USRYMA.TB_YM_SCH
				where STOCK_ID =:V_STOCK_ID
				 and SCH_WORK_STAT in('1','S')
				 */
				jrecord.setField("STOCK_ID"             , CoilNo.trim());
				
				JDTORecordSet jsScrSchList = commDao.select(jrecord, "ym.common.dao.selectSchCoilInfo", "SYSTEM", "receiveSPMConStatPOYMJ010", "스케쥴존재 여부 조회");
				
				if(jsScrSchList.size()>0){ 
					String sCH_ID = jsScrSchList.getRecord(0).getFieldString("SCH_ID");
					
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
					} else if(WorkID.equals("S")){
						Workchk="SPM";
					} else if(WorkID.equals("N")){
						Workchk = "SPM2";
					}
					
	
					logger.println(LogLevel.DEBUG,this, ">>★★★★★★★★★보관매출 자동정정실적처리[SPM2]>>>" + CoilNo.trim());
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICoilInfoReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("setInnerIFCoilInfo_02",
												new  Class[]{String.class,String.class},
												new Object[]{ CoilNo.trim() ,Workchk});					 
					
				}
				//***************************************************************************************************************
				
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 추출 isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "추출 작업예약 편성완료");
				}
									
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){                     // Take-Out
				isSuccess = callSPMTakeOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim(), TakeOutProcess.trim());
				LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-Out 작업예약 편성완료");
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                     // Take-In
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< Take-In isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-In 작업예약 편성완료");
				}
			}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveSPMConStatM(POYM010)");
			
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException("=(POYM010)추출요구 추출측존에 코일이 존재 안합니다.");
	    }
	}
	
	/**
	 * 대상 Coil이 보급, 추출, Take-Out일때 설비위에 적치되어 있는지 야드에 적치되어 있는지 점검해서
	 * False 처리한다. 
	 * @param YardID : 야드구분
	 * @param WorkID : S 기존 SPM, N 신규 SPM
	 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * @param CoilNo
	 * @return
	 */
	private boolean SPMProcessCheck(String YardID, String WorkID, String ProcessID, String CoilNo){
		boolean isSuccess = false;
		boolean bScrap = false;
		
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
			
	    	logger.println(LogLevel.DEBUG,this, "Start-SPMProcessCheck(): #2SPM");
	    	
	    	//================================================================================================================================
	    	bScrap = checkSpm2Scrap(YardID, WorkID, ProcessID, CoilNo);
	    	// SCRAP에 관련된 코드를 처리한다. 최규성 2009-12-11 
	    	
	    	if(bScrap){
	    		logger.println(LogLevel.DEBUG,this, "checkSpm2Scrap() true");
	    	}else{
	    		logger.println(LogLevel.DEBUG,this, "checkSpm2Scrap() false");
	    	}
	    	//================================================================================================================================
    		/* 적치단(TB_YM_STACKLAYER) Table Read 
    		 * STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
             * Select STACK_COL_GP, substr(STACK_COL_GP,1,1) STACK_COL_GP1, substr(STACK_COL_GP,2,1) STACK_COL_GP2, 
             *        STACK_BED_GP, STACK_LAYER_STAT 
    		 *   From TB_YM_STACKLAYER 
    		 *  Where STOCK_ID = ? And (STACK_LAYER_STAT = "L" OR STACK_LAYER_STAT = "U") (적치단 상태 L:적치중, U:권상전)
    		 */     	
	    	if (CoilNo.equals("")){
				logger.println(LogLevel.DEBUG,this, "CoilNo = Space Error");
				//throw new EJBServiceException("코일번호가 존재안함" + CoilNo);
				return false;
	    	}
	    	
			String stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
			JDTORecord StackColGp  = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ CoilNo });

			if (StackColGp == null){
				logger.println(LogLevel.DEBUG,this, "적치단(TB_YM_STACKLAYER) Table Read Error");
				//throw new EJBServiceException("보급가능한 적치단 존재하지 않음." + CoilNo);
				return false;										
			}	
			
			String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");
			String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
			String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
			String stackStat = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
			
			
	    	/*
	    	 * 보급, 추출 요구가 왔을때 수신한 Coil이 보급이 왔는데 Coil이 출측에 있다면 Error 
	    	 * 추출이 왔는데 Coil이 입측에 있다면 Error Take-In이 왔는데 Coil이 출측에 있다면 Error 
			 */
			String TmpEquip = stackCol.substring(2,4); 
			
			logger.println(LogLevel.DEBUG,this, ">>SPMProcessCheck()<< stackCol  ="+ stackCol);
			logger.println(LogLevel.DEBUG,this, ">>SPMProcessCheck()<< TmpEquip  ="+ TmpEquip);
			logger.println(LogLevel.DEBUG,this, ">>SPMProcessCheck()<< WorkID    ="+ WorkID);
			logger.println(LogLevel.DEBUG,this, ">>SPMProcessCheck()<< ProcessID ="+ ProcessID);
			
			/**
			 * 저장품 Table에 SPM / HFL 정정보급구분(S/H), 보급일자 Update
			 * A열연 '4K' SPM, '2H' HFL
			 * B열연 '2K' SPM, '4H' HFL  '6K' SPM2 
			 * 정정 보급 요구 일시 SHEAR_SUPPLY_DEMAND_DDTT
			 * 정정 보급 구분     SHEAR_SUPPLY_GP
 			 */
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){  // 보급일때만 Update
				/* 
				 * select SHEAR_SUPPLY_GP, SHEAR_SUPPLY_DEMAND_DDTT From TB_YM_STOCK Where STOCK_ID = CoilNo
				 */
				String selectshearsupply     = "ym.steelinfo.steelinforecv.YdStockDAO.SelectShearSupply";				
				JDTORecord shearsupply       = ydStockDAO.requestgetData(selectshearsupply, new Object[]{ CoilNo });
				String shearsupplygp         = StringHelper.evl(StackColGp.getFieldString("SHEAR_SUPPLY_GP"), "");
				String shearsupplydemandddtt = StringHelper.evl(StackColGp.getFieldString("SHEAR_SUPPLY_DEMAND_DDTT"), "");
				
				if (shearsupplygp == null || shearsupplygp.equals("")){
					/*
					 * Update TB_YM_STOCK 
				     *    Set SHEAR_SUPPLY_GP = WorkID, SHEAR_SUPPLY_DEMAND_DDTT = to_char(sysdate,'YYYYMMDDHH24MMSS') 
				     *  Where STOCK_ID = CoilNo
					 */
			    	String sQueryId_updateHFLSPMStockIdLineIn = "ym.steelinfo.steelinforecv.YdStockDAO.updateHFLSPMStockIdLineIn";
/*			    	
			    	if ((YardID.equals(YmCommonConst.YD_GP_1)) && (WorkID.equals(YmCommonConst.WORK_SPM_S))){
		    			if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
		    					
		    			}else{ 
				    		int stkId = ydStockDAO.requestupdateData(sQueryId_updateHFLSPMStockIdLineIn, new Object[]{ 
			    					        YmCommonConst.SHEAR_SUPPLY_GP_1K, CoilNo.trim() });		 // A열연 '4K' SPM
		    			}	    								    			

			    	}else if ((YardID.equals(YmCommonConst.YD_GP_3)) && (WorkID.equals(YmCommonConst.NEW_WORK_SPM_O))) {
			    		
			    		int stkId = ydStockDAO.requestupdateData(sQueryId_updateHFLSPMStockIdLineIn, new Object[]{ 
					            YmCommonConst.SHEAR_SUPPLY_GP_5K, CoilNo.trim() });			// B열연 '2K' SPM
			    	}else 
					//===============================================================================================================
			    	if ((YardID.equals(YmCommonConst.YD_GP_3)) && (WorkID.equals(YmCommonConst.NEW_WORK_SPM_N))) {	// #2 SPM

			    		int stkId = ydStockDAO.requestupdateData(sQueryId_updateHFLSPMStockIdLineIn, new Object[]{ 
					            YmCommonConst.SHEAR_SUPPLY_GP_6K, CoilNo.trim() });
			    	}
*/			    	
			    	//===============================================================================================================
			    	if (YardID.equals(YmCommonConst.YD_GP_3)  ){
			    		if ( WorkID.equals(YmCommonConst.NEW_WORK_SPM_N) ){		// SPM2 작업
			    			int stkId = ydStockDAO.requestupdateData(sQueryId_updateHFLSPMStockIdLineIn, new Object[]{ 
						            YmCommonConst.SHEAR_SUPPLY_GP_6K, CoilNo.trim() });
			    		}else if (WorkID.equals(YmCommonConst.NEW_WORK_HFL_F)){	// HFL2 작업: F
			    			int stkId = ydStockDAO.requestupdateData(sQueryId_updateHFLSPMStockIdLineIn, new Object[]{ 
						            YmCommonConst.SHEAR_SUPPLY_GP_6H, CoilNo.trim() });
			    		}
			    	}
			    	//===============================================================================================================
				}
			}			
/*			
	    	if (YardID.equals(YmCommonConst.YD_GP_1)){                      //A열연 
	    	
	    		if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){      //SPM 보급           : 적치열(1DKE01)
	    			if (stackCol2.equals(YmCommonConst.BAY_GP_D) || stackCol2.equals(YmCommonConst.BAY_GP_E)){
		    			if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
		    				isSuccess = false;
		    			}else{ 
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
	    	}else
*/
	    	if (YardID.equals(YmCommonConst.YD_GP_3)){                //B열연(#1 SPM)
/*	    		if (WorkID.equals(YmCommonConst.NEW_WORK_SPM_O)){
	    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){      //SPM 보급           : 적치열(3CKE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_C) || stackCol2.equals(YmCommonConst.BAY_GP_B)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){ 
		    					isSuccess = false; 
		    			    }else{ 
		    			    	isSuccess = true;   
		    			    }		    					
	    				}
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){//SPM 추출           : 적치열(3BKD01) -> 적치열(3AKD01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_A) || stackCol2.equals(YmCommonConst.BAY_GP_B)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){ 
		    					isSuccess = true;  
		    			    }else{ 
		    			    	isSuccess = false;   
		    			    }		    					
	    				}else{ 
	    			    	isSuccess = false; 	    					
	    				}	    				
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){//SPM Take-In  : 적치열(3BKE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_B)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
		    					isSuccess = false; 
		    			    }else{ 
		    			    	isSuccess = true;   
		    			    }		    					
	    				}
	    			}
	    			
	    		}else
*/	    		/*
	    		 *==============================================================
	    		 * #2 SPM 
	    		 *==============================================================
	    		 */
	    		if (WorkID.equals(YmCommonConst.NEW_WORK_SPM_N)){		// SPM2 작업
		    		if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){      //#2 SPM 보급           : 적치열(3DKE01)
		    			if (stackCol2.equals(YmCommonConst.BAY_GP_D) || stackCol2.equals(YmCommonConst.BAY_GP_E) ){
			    			if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
			    				LogMgProcess( YardID, WorkID, ProcessID, CoilNo, "#2 SPM 보급 가능 여부 검사 FALSE");
			    				logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 SPM 보급 가능 여부 검사 FALSE");
			    				isSuccess = false; 
			    			 }else{ 
			    				LogMgProcess( YardID, WorkID, ProcessID, CoilNo, "#2 SPM 보급 가능 여부 검사");
			    				logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 SPM 보급 가능 여부 검사 TRUE");
			    			    isSuccess = true;   
			    			 }		    					
		    			}
		    		}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){//#2 SPM 추출           : 적치열(3EKD01)
		    			if (stackCol2.equals(YmCommonConst.BAY_GP_E)){
		    				if (TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
			    				logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 SPM 추출 가능 여부 검사 TRUE");
			    				isSuccess = true;  
			    			}else{
			    				logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 SPM 추출 가능 여부 검사 FALSE");
			    			    isSuccess = false;   
			    			}		    					
		    			}else{ 
		    			    	isSuccess = false; 	    					
		    			}	    				
		    		}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){//#2 SPM Take-In  : 적치열(3EKE01)
		    			if (stackCol2.equals(YmCommonConst.BAY_GP_E)){
			    			if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) || TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
			    				logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 SPM Take-In 가능 여부 검사 FALSE");
			    				isSuccess = false; 
			    			}else{ 
			    				logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 SPM Take-In 가능 여부 검사: TRUE");
			    			    isSuccess = true;   
			    			}		    					
		    			}
		    		}
		    	// HFL 작업이지만 보급위치와 추출위치는 SPM의 코드를 따르도록 한다. 최규성 2010-02-08.
	    		}else if (WorkID.equals(YmCommonConst.NEW_WORK_HFL_F)){		// HFL2 작업		
	    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){      //HFL2 보급           : 적치열(1BFE01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_D) || stackCol2.equals(YmCommonConst.BAY_GP_E) ){ 
		    				if (TmpEquip.equals(YmCommonConst.WORK_HFL_IN_FE) || TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){
	    					//if (TmpEquip.equals(YmCommonConst.WORK_SPM_IN_KE) ){
		    					logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 HFL 보급 가능 여부 검사 FALSE");
		    					isSuccess = false;
		    			    }else{ 
		    			    	logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 HFL 보급 가능 여부 검사 TRUE");
		    			    	isSuccess = true;   
		    			    }		    					
	    				}
	    			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){//HFL 추출            : 적치열(1CFD01) -> 적치열(1CFD01)
	    				if (stackCol2.equals(YmCommonConst.BAY_GP_E)){
		    				//if (TmpEquip.equals(YmCommonConst.WORK_HFL_OUT_FD)){
	    					if (TmpEquip.equals(YmCommonConst.WORK_SPM_OUT_KD)){
		    					logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 HFL 추출 가능 여부 검사 TRUE");
		    					isSuccess = true;  
		    			    }else{ 
		    			    	logger.println(LogLevel.DEBUG,this, "SpmProcessCheck(): #2 HFL 추출 가능 여부 검사 FALSE");
		    			    	isSuccess = false;   
		    			    }		    					
	    				}	    				
	    			}
	    		}
	    	}
	    	
	    	logger.println(LogLevel.DEBUG,this, "End-SpmProcessCheck()");
	    	
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
	 * 1. 조업 SPM 보급요구 수신
	 * 2. 저장품 Table에 SPM 저장품이동조건(보급요구), 보급일자 Update 
	 * 3. 야드 MAP에서 보급 요구 대상재 현재위치 조회
	 * 4. 수신 Coil 위치 파악 보급동이 아니면 Error
        *    A열연 SPM 보급(Line In)  : 적치열(1DKE01)
        *    B열연 SPM 보급(Line In)  : 적치열(3CKE01)
	 * 5. 보급동이면 callLineInOut(Coil No) Call
	 * 6. 저장품 Table에 SPM 저장품이동조건(보급요구), 보급요구일자(제일빠른일자) 검색
	 * 7. 잔량이 존재 하는지 점검. 존재한다면 3번으로 Loop
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean SPMLineIn(String YardID, String WorkID, String ProcessID, String CoilNo, String Position){
		boolean isSuccess = false;

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-SPMLineIn()");
			
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
				logger.println(LogLevel.DEBUG,this, "수신한 CoilNo"+CoilNo.trim()+"가 저장품(TB_YM_STOCK) Table 에 존재하지 않습니다. Error");
				return false; 																																									
			}			
			
			String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), "");
			
			if (stockId == null || stockId.equals("")){
				logger.println(LogLevel.DEBUG,this, "#2SPM 보급요구한 CoilNo이 존재하지 않음  Error");
				return false; }

	    	if (wbookId != null && !wbookId.equals("")){
				logger.println(LogLevel.DEBUG,this, "#2SPM 보급요구한 CoilNo이 이미 작업예약되어 있슴  Error");
				isSuccess = false; 
			}

	    	
			isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
			logger.println(LogLevel.DEBUG,this, ">>SPMLineIn()<< isSuccess ="+ isSuccess);
			
			if (isSuccess){
				isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim());	
			}
			
			logger.println(LogLevel.DEBUG,this, "End-SPMLineIn()");
			
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
	 * SPM LineIn 요구시 작업여건이 맞지 않아 보급예약을 할 수 없어 잔량으로 남아 있는 Coil들을
	 * 검색해서 작업예약 및 Schedule Call
	 * param POYM010
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean SPMLineInRequestSearch(String YardID, String WorkID, String ProcessID, String Position){
		boolean isSuccess = false;

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-SPMLineInRequestSearch()");
			
			int iResult = 0;
			
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
			CraneSchDAO craneSchDAO         = new CraneSchDAO();
			YdEquipDAO  ydEquipDAO          = new YdEquipDAO();
			
			/**
			 * 저장품 Table에 SPM 정정보급구분(S/H), 보급요구일자(제일빠른일자) 검색 잔량이 존재 하는지 점검
			 * 
					SELECT STOCK_ID, WBOOK_ID 
					  FROM TB_YM_STOCK  A
					 WHERE SHEAR_SUPPLY_GP = :workID
					   AND (SHEAR_SUPPLY_DEMAND_DDTT IS NOT NULL 
					    OR SHEAR_SUPPLY_DEMAND_DDTT != '' )
					   AND NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
					 ORDER BY SHEAR_SUPPLY_DEMAND_DDTT --보급요구일자
			 */
			String TmpWorkID = "";
			
			String selectStockLineInRequest = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStockLineInRequest";
			
			/*
    		 * ==============================================================
    		 * #2 SPM 내용 추가
    		 *==============================================================
    		 */	
	    	//if ((WorkID.equals(YmCommonConst.WORK_SPM_S)) || (WorkID.equals(YmCommonConst.NEW_WORK_SPM_O)) || (WorkID.equals(YmCommonConst.NEW_WORK_SPM_N))){
			if ( WorkID.equals(YmCommonConst.NEW_WORK_SPM_N) ){
	    		//if (YardID.equals(YmCommonConst.YD_GP_1)){	TmpWorkID= YmCommonConst.SHEAR_SUPPLY_GP_1K;  }
	    		if (YardID.equals(YmCommonConst.YD_GP_3)){	TmpWorkID= YmCommonConst.SHEAR_SUPPLY_GP_6K;  }
	    	}else if (WorkID.equals(YmCommonConst.NEW_WORK_HFL_F) ){
	    		if (YardID.equals(YmCommonConst.YD_GP_3)){	TmpWorkID= YmCommonConst.SHEAR_SUPPLY_GP_6H;  }
	    	}
			
	    	List StockId = ydStockDAO.getListData(selectStockLineInRequest, new Object[]{ TmpWorkID.trim() });
	    	logger.println(LogLevel.DEBUG,this, "Stock TBL info="+StockId);
	    	
			JDTORecord TmpSelStockid = null;
			int MaxRec               = StockId.size();	
			String[] tmpStockID      = new String[MaxRec];
			String[] tmpWBookId      = new String[MaxRec];
			
			for (int ii=0; ii<MaxRec; ii++){
				TmpSelStockid        = (JDTORecord) StockId.get(ii);
				tmpStockID[ii]       = StringHelper.evl(TmpSelStockid.getFieldString("STOCK_ID"), "");
				tmpWBookId[ii]       = StringHelper.evl(TmpSelStockid.getFieldString("WBOOK_ID"), "");
				
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), tmpStockID[ii].trim());
				
			    logger.println(LogLevel.DEBUG,this, ">>SPMProcessCheck()<< isSuccess ="+ isSuccess);
			
			    if (isSuccess){
			    	logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut() 호출 ="+ YardID.trim()+","+ WorkID.trim()+","+YmCommonConst.PROCESS_ID_1+","+tmpStockID[ii].trim());
			    	isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), YmCommonConst.PROCESS_ID_1, tmpStockID[ii].trim(),Position.trim());
			    }
			    isSuccess = false;
			}
			
			logger.println(LogLevel.DEBUG,this, "End-SPMLineInRequestSearch()");
			
			return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }					
	}
	
	/**
	 * 오퍼레이션명 : 설비 보급과 추출, Take-In 기능 처리. 최규성 
	 *
	 * SPM 보급, 추출, Take-In
	 * param YardID : 야드구분
	 * param WorkID : S 기존 SPM, N SPM2, F SPM2 내의 HFL
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callSPMLineInOut(String YardID, String WorkID, String ProcessID, String CoilNo, String Position){	

		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO craneSchDAO         = new CraneSchDAO();
		YdEquipDAO  ydEquipDAO          = new YdEquipDAO();
		
		String WBook_Str = "";
		
		boolean bSwitch  				= false; 
		JDTORecord jtrLayerInfo 		= null;
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this,"Start-callSPMLineInOut()");				
			logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< YardID="+ YardID.trim());
			logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< WorkID="+ WorkID.trim());
			logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< ProcessID="+ ProcessID.trim());
			logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< CoilNo="+ CoilNo.trim());
			logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< Position="+ Position.trim());
			

			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ CoilNo.trim() });

			if (StockCoilNo == null){
				logger.println(LogLevel.DEBUG,this, "StockCoilNo Error:"+CoilNo.trim());
				return false;				
			}	
			
			logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< StockCoilNo="+ StockCoilNo);
			
			String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), "");
            
			logger.print(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< stockId="+ stockId);
			
			List list = new ArrayList();
			list.add(CoilNo.trim());
			boolean exsitCoilNo = ydStockDAO.isExistPrimaryKey(list);

			logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< exsitCoilNo="+ exsitCoilNo);
			
			if (stockId == null || stockId.equals("")){
				logger.println(LogLevel.DEBUG,this, "작업요구한 CoilNo"+CoilNo.trim()+"이 존재하지 않음  Error");
				return false; }

	    	if (wbookId != null && !wbookId.equals("")){
				logger.println(LogLevel.DEBUG,this, "작업요구한 CoilNo"+CoilNo.trim()+"이 이미 작업예약되어 있슴  Error");
				isSuccess = false; 
			}
	    	
	    	if(exsitCoilNo) {

				logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< wbookId="+ wbookId);
				
		    	// 저장품  Table에 작업예약_ID(WBookID)가 존재한다면 Error
		    	if (wbookId == null || wbookId.equals("")){
		    		    		
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
					
					logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< StackColGp="+ StackColGp);

					String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");
					String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
					String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
					String stackStat = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
					
					logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< stackCol="+ stackCol + "/" + stackCol1 + "/" + stackCol2);
					
					if (stackCol != null && !stackCol.equals("")){

						// SPM 작업 실적 완료 되면 추출동에 Insert 한다.
						// 추출요구가 수신되면 추출동에 있는 대상재를 추출요구 위치동으로 옮긴뒤 처리한다.
						/*
				    	 * ==============================================================
				    	 * #2 SPM 내용 추가
				    	 *==============================================================
				    	 */		
						if (  (WorkID.equals(YmCommonConst.NEW_WORK_SPM_N) || WorkID.equals(YmCommonConst.NEW_WORK_HFL_F)) && (YardID.equals(YmCommonConst.YD_GP_3))){     
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){    //추출 
								if (stackCol2.equals(YmCommonConst.BAY_GP_E) &&  !stackCol.equals(YmCommonConst.STACK_COL_GP_3EKD02) ){
									
									// SPM2 관련하여 추출위치를 추가함. 최규성 2009-12-11
									String TmpstackCol   = YmCommonConst.STACK_COL_GP_3EKD02;  //3EKD01 -> 3EKD02
									
									// 추출위치 및 포지션에 저장품이 있는지 검사한다. 최규성 2010-01-27
									String sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStackLayerInfo";
									jtrLayerInfo = ydStackLayerDAO.requestgetData(sQueryId, new Object[]{TmpstackCol, Position.trim()});
									
									String sStackLayerStat ="L";
									
									if (jtrLayerInfo != null){
										sStackLayerStat = jtrLayerInfo.getFieldString("STACK_LAYER_STAT");
									}
									
									logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< SPM2 StackLayerStat= "+sStackLayerStat);
									
									//if (jtrLayerInfo == null){
									if( (!"U".equals(sStackLayerStat) && !"S".equals(sStackLayerStat)) || jtrLayerInfo == null   ){
										
										//int iSeq1 = YmCommonDB.insertConveyorInfo(
										//			    TmpstackCol.trim(), stockId.trim(), YmCommonConst.GBN_MAX);
										int iSeq1 = YmCommonDB.insertConveyorInfo(
											    		TmpstackCol.trim(), stockId.trim(), Position.trim());
										logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< SPM2 stkColGp="+TmpstackCol);
										int iSeq2 = YmCommonDB.deleteConveyorInfo(stackCol.trim(), stockId.trim());
										stackCol  = TmpstackCol;
										stackCol1 = TmpstackCol.substring(0,1); //아드구분
										stackCol2 = TmpstackCol.substring(1,2);	//동구분
										
										if (iSeq1 == 99) bSwitch=true;
										
									}else if("U".equals(sStackLayerStat) || "S".equals(sStackLayerStat)){
									//	int iSeq3 = YmCommonDB.switchConveyorInfo_pub(TmpstackCol, Position.trim());

										logger.println(LogLevel.DEBUG,this, "=추출처리Exception :: 추출위치에 작업중인 저장품 존재"+stockId+"|"+TmpstackCol+Position);
										throw new EJBServiceException("=추출처리Exception :: 추출위치에 작업중인 저장품 존재"+stockId+"|"+TmpstackCol+Position);
									}
								}
							}	
						}
						
                        // 적치단  Table Update(작업요구상태='S'로 변경)
						// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
						if (stackStat.trim().equals(YmCommonConst.STACK_LAYER_STAT_L)){
							String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
							int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
									       YmCommonConst.STACK_LAYER_STAT_S, stockId.trim() });

							logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< stockId="+ stockId.trim()+ "stkColGp="+stkColGp);
						}

						
						// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
						String wBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
						JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);

						logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< wBookSel="+ wBookSel );
						
						String wBookid  = wBookSel.getFieldString("WBOOK_SELECT");
						
						logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< wBookid="+ wBookid );

						
						// 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시) 한다.
						String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";

						// SCRAP 코드 추가. 최규성 2009-12-11
						// Scrap 처리를 위한 변수 추가 
						String sSWBookQueryId = "";	// 작업예약 저장 쿼리
						String sUserToLoc = "0101"; // TO위치의 적치 대, 단 정보 고정.
						
						// 1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In
						int wbookstockId = 0;
						
						/*
						 * ==================================================
						 * #2 SPM내용 추가 
						 * ==================================================
						 */
						if ((WorkID.equals(YmCommonConst.WORK_SPM_S)) || (WorkID.equals(YmCommonConst.NEW_WORK_SPM_O)) ){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM 보급 
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKLI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
								
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM 추출
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKLO, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
								
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                      
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM Take In
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKTI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
							}
						}else if ( WorkID.equals(YmCommonConst.NEW_WORK_SPM_N) ) {
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM2 보급 
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CNLI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
								
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								if(CoilNo.substring(0,1).equals("S")){
									// 적치단의 상태가 "E"가 아닌 경우 강제로 E로 변경한다. 위에서 'S'로 변경하고 있음.ㅡ..ㅡ..
									//UPDATE TB_YB_STACKLAYER
									//   SET STACK_LAYER_STAT = 'E'
									//     , MODIFIER='SYSTEM'
									//     , MOD_DDTT=SYSDATE
									// WHERE STACK_COL_GP = ?
									sSWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.UpdateScrapLayerStat";
									wbookstockId = ydWBookDAO.requestupdateData(sSWBookQueryId, new Object[]{ YmCommonConst.STACK_COL_GP_3ESP01} );
									
									logger.println(LogLevel.DEBUG,this, "3ESP01적치열에 대한 적치단의 상태를 E로 수정함.");
									
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
									sUserToLoc = YmCommonConst.STACK_COL_GP_3ESP01 + sUserToLoc;									

									wbookstockId = ydWBookDAO.requestinsertData(sSWBookQueryId, new Object[]{	// Scrap SPM 추출
											wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CNLO,  sUserToLoc,
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
									logger.println(LogLevel.DEBUG, this, ">>callSPMLineInOut()<< Scrap 작업예약처리"+ String.valueOf(wbookstockId));
									
								}else{
									wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM2 추출
											       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CNLO, 
												   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
								}
								
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                      
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM2 Take In
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CNTI, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
							}
						}else if (WorkID.equals(YmCommonConst.NEW_WORK_HFL_F)){		// HFL2 
							// SPM2 내의 HFL기능 처리. 최규성 2010-02-08
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){				// HFL2 보급
								
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil HFL2 보급 
									       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CHLI, 
										   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
								
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){		// HFL2 추출
								
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil SPM2 추출
									       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CHLO, 
										   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });
								
							}
						}
						logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< wbookstockId="+ String.valueOf(wbookstockId) );
						
						logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< stackCol1="+ stackCol1+ " stackCol2="+stackCol2);
						
						if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){ 		// 보급일 
							/**
							 * Update TB_YM_STOCK Set SHEAR_SUPPLY_GP = '', SHEAR_SUPPLY_DEMAND_DDTT = '' Where STOCK_ID = CoilNo
							 *  정정 보급 요구 일시 SHEAR_SUPPLY_DEMAND_DDTT
							 *  정정 보급 구분     SHEAR_SUPPLY_GP
							 */
					    	String updateHFLSPMStockIdLineInSpace = "ym.steelinfo.steelinforecv.YdStockDAO.updateHFLSPMStockIdLineInSpace";
			    			int updatehflspmStockIdLineInspace    = ydStockDAO.requestupdateData(updateHFLSPMStockIdLineInSpace, new Object[]{ 
			    					                                stockId.trim() });		
						}
						//====================================================================================================================================
						// 최규성 수정
						// Scrap에 대한 저장품이동조건 변환에 대한 코드 추가. 강제로 A2 설정.
						String[] sStockInfo         =  {"",""};
						if ( !stockId.substring(0, 1).equals("S") ) 
						{
							// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
							// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
							sStockInfo         = YmCommonUtil.getCoilCurrProgCd(stockId.trim(),""); // 공통 진도코드 Read해서 저장품 이동조건으로 변환
						}else{
							// Scrap에 대한 코드는 고정으로 적용한다.
							sStockInfo[1] = YmCommonConst.NEW_STOCK_MOVE_TERM_A2;
							logger.println(LogLevel.DEBUG, this, ">>callSPMLineInOut()<< 저장품 이동조건으로 변환 ["+sStockInfo[1]+"]");

						}
						//====================================================================================================================================
					
						String updateYdStockStockId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						int updateydstockstockid    = ydStockDAO.requestupdateData(updateYdStockStockId, new Object[]{ 
		    		                                  wBookid, sStockInfo[1], stockId.trim() });	
						
						logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< WorkID   ="+ WorkID.trim());
						logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< ProcessID="+ ProcessID.trim());
						
						//logger.println(LogLevel.DEBUG,this,"End-callSPMLineInOut()");
						  
						String TmpSchcode = "";
						
						/*
						 * ==================================================
						 * #2 SPM내용 추가
						 * ==================================================
						 */
						if ((WorkID.equals(YmCommonConst.WORK_SPM_S)) || (WorkID.equals(YmCommonConst.NEW_WORK_SPM_O)) ){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CKLI;  //Coil SPM 보급\
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CKLO;  //Coil SPM 추출
								
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){   
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CKTI;  //Coil SPM Take-In
							}
						}else if ( WorkID.equals(YmCommonConst.NEW_WORK_SPM_N) ) {
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CNLI;  //Coil SPM2 보급
								logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< #2SPM 보급 스케줄 코드 변경"+ TmpSchcode.trim());
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CNLO;  //Coil SPM2 추출
								logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< #2SPM 추출 스케줄 코드 변경"+ TmpSchcode.trim());
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){   

								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CNTI;  //Coil SPM2 Take-In
								logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< #2SPM Take-In 스케줄 코드 변경"+ TmpSchcode.trim());
							}
						}else if ( WorkID.equals(YmCommonConst.NEW_WORK_HFL_F) ) {			// HFL 처리.
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CHLI;  //Coil HFL2 보급
								logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< #2SPM 보급 스케줄 코드 변경"+ TmpSchcode.trim());
							}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CHLO;  //Coil HFL2 추출
								logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< #2SPM 추출 스케줄 코드 변경"+ TmpSchcode.trim());
							}
						}
						
						/*
						 *select count(SCH_ID) as SchcodeCount from USRYMA.TB_YM_SCH 
						 * Where SCH_WORK_KIND = TmpSchcode And YD_GP = YardID
						 */
						String selectStockSchID     = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockSchIDCount";
						JDTORecord selectstockschid = ydStockDAO.getData(selectStockSchID, new Object[]{ TmpSchcode.trim(), YardID  });

						String tmpSchcodeCount      = StringHelper.evl(selectstockschid.getFieldString("SCHCODECOUNT"), "");
						
						int schedulecount           = Integer.parseInt(tmpSchcodeCount);
						
						logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< #2SPM 작업예약 완료 스케줄 = " + TmpSchcode.trim() + " / 스케줄테이블 스케줄 코드 갯수 = "+ schedulecount);
						
						// SPM2추출 방식의 변화로 추출스케줄코드 조건 추가. 최규성 2010-01-27
						if (schedulecount < 2 
								|| TmpSchcode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CNLO) 
								|| TmpSchcode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CHLO)) {
							//Coil Schedule EJB Call
							/**
							 * 2007.05.02 이정훈
							 * 작업 예약  Table(TB_YM_WBOOK) : 작업예약 등록 우선 순위가 제일 빠른것 추출
							 */
							String sSchwBook = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectSchWBook";
							JDTORecord WBook = ydEquipDAO.getData(sSchwBook, new Object[]{ YardID.trim(), stackCol2.trim(), TmpSchcode.trim() });			
							wBookid = StringHelper.evl(WBook.getFieldString("WBOOK_ID"), "");
								
							logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< #2SPM 스케줄 Call="+ wBookid.trim() + " / stockId="+ stockId.trim());
								
							EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
							Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookid });														
						}
						// 최규성 2010-02-04
						// 중복된 위치에 추출요구 시도시 처리.
						if(bSwitch){
							logger.println(LogLevel.DEBUG,this, ">>callSPMLineInOut()<< SPM2 insertConv결과= true");
							/*
							EJBConnector ejbCon1 	= new EJBConnector("default", "JNDICoilInfoReg", this);
							POYM001 tcRc1 			= new POYM001();
							tcRc1.setTcCode("POYM001");
							tcRc1.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
							tcRc1.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
							tcRc1.setProcessID("02");
							tcRc1.setCoilNo(jtrLayerInfo.getFieldString("STOCK_ID"));
							tcRc1.setYardID(YardID);
							tcRc1.setProcessCode("");
						
						    Boolean result =  (Boolean)ejbCon1.trx("receiveInCoilInfo",new Class[]{ POYM001.class }, new Object[]{ tcRc1 });
						    
						    */
						    int iSeq = YmCommonDB.insertConveyorInfo(YmCommonConst.STACK_COL_GP_3EKD01, 
						    									jtrLayerInfo.getFieldString("STOCK_ID"),
						    									YmCommonConst.GBN_MAX);
						    
						    
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
	 * SPM 보급취소
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
	public boolean callSPMLineInOutCancle(String YardID, String WorkID, String ProcessID, String CoilNo){
		
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
											
				logger.println(LogLevel.DEBUG, this, "callSPMLineInOutCancle()>> 보급작업예약취소=> 스케쥴 취소 모듈 CALL="+isTemp);		
			}else{	
				/**
				 *	3.	보급작업예약이 없으면 예약 FLAG만 삭제
				 */ 
				int iSeq = dao.updateStockSupplyGpWithStockId(CoilNo,
															  "",
															  "");
				logger.println(LogLevel.DEBUG, this, "callSPMLineInOutCancle()>> 보급작업예약취소=> 예약 FLAG 삭제");													  
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
	 * SPM 보급취소
	 * param YardID : 야드구분
	 * param WorkID : S SPM, N 신규 SPM
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callSPMLineInOutCancle_backup(String YardID, String WorkID, String ProcessID, String CoilNo){
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO craneSchDAO         = new CraneSchDAO();

		String YMPO161_WorkID  = "";
		if(WorkID.equals("N") ) YMPO161_WorkID = "S";
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
		
		    logger.println(LogLevel.DEBUG,this,"Start-callSPMLineInOutCancle()");
			
			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID, STOCK_MOVE_TERM from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ CoilNo.trim() });

			if (StockCoilNo == null){
				logger.println(LogLevel.DEBUG,this, "수신한 CoilNo가 저장품(TB_YM_STOCK) Table 에 존재하지 않습니다. Error");
				return false;
			}	
			
			String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), "");
			String stockmoveterm = StringHelper.evl(StockCoilNo.getFieldString("STOCK_MOVE_TERM"), "");
            
			logger.print(LogLevel.DEBUG,this, "callSPMLineInOutCancle()>>stockId="+ stockId);
			
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
					
					/* 공정구분	CHAR(1)  H : Hot Filnal, S :  SkinPass	*/ // 코드 수정. SPM2일경우에도 "S"로 전송.
					//model.setprocGbn(WorkID.substring(0,1));
					model.setprocGbn(YMPO161_WorkID.substring(0,1));
					
					/* COIL번호	CHAR(11) */
					model.setcoilNo(CoilNo);
					
					/* 처리구분     CHAR(1)  1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In */
					model.setProcessId("2");
					
					/* 위치포지션  CHAR(2)  */
					model.setpositionNo("");
					
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},new Object[]{ model });							
					
					logger.println(LogLevel.DEBUG,this,"End-callSPMLineInOutCancle()");
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
	 *  SPM Take-Out
	 * param YardID : 야드구분
	 * param WorkID : S 기존 SPM, N 신규 SPM
	 * param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
	 * param CoilNo
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callSPMTakeOut(String YardID, String WorkID, String ProcessID, String CoilNo, String Position, String TakeOutProcess){	

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

			logger.println(LogLevel.DEBUG,this,"Start-callSPMTakeOut()");				
			logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< CoilNo="+ CoilNo.trim());

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
            
			logger.print(LogLevel.DEBUG,this, "callSPMTakeOut()>>stockId="+ stockId);
			
			List list = new ArrayList();
			list.add(CoilNo.trim());
			boolean exsitCoilNo = ydStockDAO.isExistPrimaryKey(list);

			logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< exsitCoilNo="+ exsitCoilNo);
			
			if(exsitCoilNo) {
				logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< wbookId="+ wbookId);
				
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
						logger.println(LogLevel.DEBUG,this, "callSPMTakeOut() >> 적치단(TB_YM_STACKLAYER) Table에 존재하지 않습니다. Error");
						return false;										
					}	
					
  				    logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< StackColGp="+ StackColGp);

					String stackCol   = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"),  "");
					String stackCol1  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
					String stackCol2  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
					String stackBedGp = StringHelper.evl(StackColGp.getFieldString("STACK_BED_GP"), "");
					String stackStat  = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
					
			    	/*
			    	 * Take-Out 요구가 왔을때 수신한 Coil이 Take-Out이 왔는데 Coil이 야드 Skid에 있다면 Error 
					 */
					String TmpEquip = stackCol.substring(2,4); 
					
					logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< stackCol  ="+ stackCol);
					logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< TmpEquip  ="+ TmpEquip);
					logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< WorkID    ="+ WorkID);
					logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< ProcessID ="+ ProcessID);
					
			    	if (YardID.equals(YmCommonConst.YD_GP_1)){                      //A열연 
			    		if (WorkID.equals(YmCommonConst.WORK_SPM_S)){
			    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){      //SPM Take-Out STACK_COL_GP_1DKE01/STACK_COL_GP_1EKE01/STACK_COL_GP_1EKD01
			    				if (stackCol.equals(YmCommonConst.STACK_COL_GP_1DKE01)||
			    					stackCol.equals(YmCommonConst.STACK_COL_GP_1EKE01)||
									stackCol.equals(YmCommonConst.STACK_COL_GP_1EKD01)){ 
			    			    }else{ 
			    			    	return false; 
			    			    }
			    			}	
			    		}
			    	}else if (YardID.equals(YmCommonConst.YD_GP_3)){                //B열연
			    		if (WorkID.equals(YmCommonConst.NEW_WORK_SPM_O)){
			    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){      //SPM Take-Out STACK_COL_GP_3CKE01/STACK_COL_GP_3BKE01/STACK_COL_GP_3BKD01
			    				if (stackCol.equals(YmCommonConst.STACK_COL_GP_3CKE01) ||
			    					stackCol.equals(YmCommonConst.STACK_COL_GP_3BKE01) ||
		    						stackCol.equals(YmCommonConst.STACK_COL_GP_3BKD01)){ 
			    			    }else{ 
			    			    	return false;   
			    			    }
			    			}
			    		/*
				    	* ==============================================================
				    	* #2 SPM 항목
				    	*==============================================================
				    	*/	
			    		}else if (WorkID.equals(YmCommonConst.NEW_WORK_SPM_N)){
			    			if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
			    				if (stackCol.equals(YmCommonConst.STACK_COL_GP_3DKE01) ||
			    					stackCol.equals(YmCommonConst.STACK_COL_GP_3EKE01) ||
		    						stackCol.equals(YmCommonConst.STACK_COL_GP_3EKD01) ){ 
			    			    }else{ 
			    			    	return false;   
			    			    }
			    			}
		    			}
			    	}
					
					String TmpstackCol = "";
					
					if (stackCol != null && !stackCol.equals("")){
						// 수신한 위치 정보와 DB에 있는 위치 정보가 다를때
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
							}else if (stackCol.equals(YmCommonConst.STACK_COL_GP_3CKE01)){   //B열연 #1 SPM 입측
								/*
								 * 2006.12.06 SPM 5,6,7 Position Take-Out시  B동 C/R 에 작업 지시 
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
									logger.println(LogLevel.DEBUG,this, ">>callTakeOut()<< B열연 #1 SPM 입측 ="+ TmpstackCol);										
								}
							}else if (stackCol.equals(YmCommonConst.STACK_COL_GP_3DKE01)){   //B열연 #2 SPM 입측	
								/*=====================================================================
								 * #2 SPM 항목
								 * 미정 (임시로 기존값 사용)
								 * ====================================================================
								 */
								if ( Position.equals(YmCommonConst.WORK_SPM_5) || 
									Position.equals(YmCommonConst.WORK_SPM_6) || 
									Position.equals(YmCommonConst.WORK_SPM_7)){
									TmpstackCol	= YmCommonConst.STACK_COL_GP_3EKE01;
									stackCol2 	= YmCommonConst.BAY_GP_E;
									int iSeq1 	= YmCommonDB.insertConveyorInfo(TmpstackCol.trim(), 
																						stockId.trim(), 
																						YmCommonConst.GBN_MIN);
									int iSeq2 	= YmCommonDB.deleteConveyorInfo(stackCol.trim(), 
																						stockId.trim());																					
									logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< B열연 #2 SPM 입측 ="+ TmpstackCol);										
								}
							}
						}

						
                        // 적치단  Table Update(작업요구상태='S'로 변경)
						// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
						if (stackStat.trim().equals("L")){
							String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
							int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
									       YmCommonConst.STACK_LAYER_STAT_S, stockId.trim() });
							
							logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< stockId="+ stockId.trim()+ "stkColGp="+stkColGp);
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
						
						logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< wBookSel="+ wBookSel );
						
						String wBookid  = wBookSel.getFieldString("WBOOK_SELECT");
						
						logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< wBookid="+ wBookid );
						
						// 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시) 한다.
						String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";

						// 4:Take-Out
						int wbookstockId = 0;
						
						/*
				    	* ==============================================================
				    	* #2 SPM 항목 추가
				    	*==============================================================
				    	*/	
						if ((WorkID.equals(YmCommonConst.WORK_SPM_S)) || (WorkID.equals(YmCommonConst.NEW_WORK_SPM_O)) ){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< WorkID="+ WorkID );
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ // SPM Take-Out
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CKTO, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
							}
						}else if ( WorkID.equals(YmCommonConst.NEW_WORK_SPM_N) ) {
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< WorkID="+ WorkID );
								wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ // #2 SPM Take-Out
										       wBookid, stackCol1, stackCol2, YmCommonConst.NEW_SCH_WORK_KIND_CNTO, 
											   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
						}
						
						logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< stackCol1="+ stackCol1+ " stackCol2="+stackCol2);

						// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
						// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
						// A/B열연 SPM : TakeOutProcess  0,1:결번(Coil SPM지시대기 D1) 2:임시보류(TakeIn대기 D6)
						String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(stockId.trim(),"");
						 
						String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
							    	wBookid, sStockInfo[1], stockId.trim() });	
						
						logger.println(LogLevel.DEBUG,this, ">>callSPMTakeOut()<< stockId="+ stockId.trim()+ "stkId="+stkId);
						
						logger.println(LogLevel.DEBUG,this,"End-callSPMTakeOut()");

						String TmpSchcode = "";
						if ((WorkID.equals(YmCommonConst.WORK_SPM_S)) || (WorkID.equals(YmCommonConst.NEW_WORK_SPM_O)) ){
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CKTO;  //Coil SPM Take-Out
							}
						}else if ( WorkID.equals(YmCommonConst.NEW_WORK_SPM_N)) {
							if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){
								TmpSchcode = YmCommonConst.NEW_SCH_WORK_KIND_CNTO;  //Coil #2 SPM Take-Out
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
						
						logger.println(LogLevel.DEBUG,this, "#2SPM Take-Out 작업예약 완료 스케줄 = " + TmpSchcode.trim() + " / 스케줄테이블 스케줄 코드 갯수 = "+ schedulecount);
						
						if (schedulecount < 2){
							//Coil Schedule EJB Call
							logger.println(LogLevel.DEBUG,this, "#2SPM Take-Out 스케줄 Call="+ wBookid.trim() + " / stockId="+ stockId.trim());
							EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
							Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookid.trim() });														
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
				}
			}else if (YardID.equals(YmCommonConst.YD_GP_3)){          //B열연 #1 SPM
				if (WorkID.equals(YmCommonConst.NEW_WORK_SPM_O)){
					if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
						createLog(YmCommonConst.MODEL_POYM004, "B열연 #1 SPM 보급        " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
						createLog(YmCommonConst.MODEL_POYM004, "B열연 #1 SPM 추출         " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){
						createLog(YmCommonConst.MODEL_POYM004, "B열연 #1 SPM Take In " + Msg.trim() + " Coil : " + CoilNo.trim());
					}					
				}else if (WorkID.equals(YmCommonConst.NEW_WORK_SPM_N)) { //B열연 #2 SPM
					if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){
						createLog(YmCommonConst.MODEL_POYM010, "B열연 #2 SPM 보급        " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_3)){
						createLog(YmCommonConst.MODEL_POYM010, "B열연 #2 SPM 추출         " + Msg.trim() + " Coil : " + CoilNo.trim());
					}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){
						createLog(YmCommonConst.MODEL_POYM010, "B열연 #2 SPM Take In " + Msg.trim() + " Coil : " + CoilNo.trim());
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
	
	private boolean checkSpm2Scrap(String YardID, String WorkID, String ProcessID, String CoilNo){
		
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();

		String 		sEmptyStackCol = "";
    	String 		sEmptyStackBed = "";
    	JDTORecord 	jtrStackInfoV = null;

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			if( CoilNo.substring(0, 1).equals("S")) {
				logger.println(LogLevel.DEBUG,this, ">>>>checkSpm2Scrap() SPM2 Scrap처리 시작 :    "+CoilNo);

				String sQueryId_checkstack = "ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO.getListBCSPMTrkCheck";
				
				List listCheckStack = ydStackLayerDAO.requestgetListData(sQueryId_checkstack, new Object[] {YmCommonConst.STACK_COL_GP_3AKD01,YmCommonConst.STACK_COL_GP_3AKD01});
				
				if (listCheckStack.size() == 0 )
	    		{
	    			logger.println(LogLevel.DEBUG,this, ">>checkSpm2Scrap()<<ScrapNo["+CoilNo.trim()+"] listCheckStack 데이터 없음. ");
	    			// STACK_COL_GP_3EKD02 위치에 비어 있는 적치대/단이 없으므로 
	    			// 최대 단(MAXBED)에 +1 하고 그 위치에 ScrapNo를 생성한다.  
	    			// >>> 적치 위치는 항상 01로 한다. 소재가 존재할 경우 shift로 처리한 후 소재 생성.
	    			//int nSeq = YmCommonDB.insertConveyorInfo(YmCommonConst.STACK_COL_GP_3AKD01,CoilNo.trim(),YmCommonConst.GBN_MAX);
	    			
	    			int nSeq = YmCommonDB.insertConveyorInfo(YmCommonConst.STACK_COL_GP_3EKD02,CoilNo.trim(),YmCommonConst.GBN_MIN);
	    			logger.println(LogLevel.DEBUG,this, ">>checkSpm2Scrap()<<ScrapNo["+CoilNo.trim()+"] 적치 대/단 수정함.");
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
				    	logger.println(LogLevel.DEBUG,this, ">>checkSpm2Scrap()<<ScrapNo["+CoilNo.trim()+"] 적치 대 수정함.");
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
				    	logger.println(LogLevel.DEBUG,this, ">>checkSpm2Scrap()<<ScrapNo["+CoilNo.trim()+"] 적치 단 수정함.");
			    	
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
				logger.println(LogLevel.DEBUG,this, ">>checkSpm2Scrap()<< ScrapNo[" + CoilNo.trim() + "]에 대한 정보를 수동으로 저장함.TB_YM_STOCK" );
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
					logger.println(LogLevel.DEBUG,this, ">>checkSpm2Scrap()<< Insert 실행: return val="+ String.valueOf(nScrapVal) );
				}else {
					nScrapVal = ydStockDAO.requestupdateData(QueryId_updateScrap, new Object[] {CoilNo.trim()});
					logger.println(LogLevel.DEBUG,this, ">>checkSpm2Scrap()<< Update 실행: return val="+ String.valueOf(nScrapVal) );
				}
				
				/**
				 * 1. 현재 스크랩 정보가 있는지 검사한다.
				 * */
	    		// 스크랩관련 처리하기 전. 설비상에 스크랩과 관련된 정보가 있는 경우는 초기화 후 처리한다. 최규성 2009-11-26
//	    		EJBConnector ejbConn = new EJBConnector("default","JNDICoilInfoReg",this);
//				Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookid });
				
	    		
	    	}
		}catch(Exception e){
		}
		//isSuccess = true;
		
		return isSuccess;
	}
	//scrap 테스트용
	public boolean receiveSPMConStatM(String YD,String WORK, String PROCESSID, String COILNO, String POS ) { 
		
		boolean isSuccess = false;
		boolean isConvSub = false; // 보조 작업을 주작업으로 변환했는지 여부 판정.

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveSPMConStat(String,String,String,String) : ScrapTEST");
			
			YdStockDAO ydStockDAO = new YdStockDAO();			// 보조작업 -> 주작업 쿼리 DAO
			
			int iResult = 0;
/*			
			String YardID    = pOYM010.getYardId();
			String WorkID    = pOYM010.getWorkId(); 
			String ProcessID = pOYM010.getProcessId();
			String CoilNo    = pOYM010.getCoilNo();
			String Position  = pOYM010.getPosition();  // 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String TakeOutProcess  = pOYM010.getTakeOutProcess();  // Take-Out시 1:결번, 2:임시보류처리(잠시 내려놨다가 Take-In할 Coil) 

			LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "POYM010 수신");
*/
			String YardID    = YD;
			String WorkID    = WORK; 
			String ProcessID = PROCESSID;
			String CoilNo    = COILNO;
			String Position  = POS;  // 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
			String TakeOutProcess  = "";//pOYM004.getTakeOutProcess();  // Take-Out시 1:결번, 2:임시보류처리(잠시 내려놨다가 Take-In할 Coil) 
			/**
			 * @param YardID : 야드구분
			 * @param WorkID : S 기존 SPM, N 신규 SPM
			 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			 * @param CoilNo:  'S' Scrap  'H' A열연  'K' B열연 
			 */
			
			// 작업구분
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                           // 보급
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 보급 isSuccess =" + isSuccess);
				
				
				if (isSuccess){
					// 상단 코일의 정정 여부 판정.
					logger.println(LogLevel.DEBUG,this,">>receiveSPMConStatM()<< 상단의 코일 검사 = "+CoilNo);
					// CGS
					// 보조작업이지만 주작업 가능한 코일번호를 반환한다.
					// 따로 함수를 만들지 않음.
					EJBConnector ejbConn = new EJBConnector("default","JNDISPMConStatReg",this);
					List sCoilNoList = (List)ejbConn.trx("checkPoJungWorkExist",new Class[]{String.class},new Object[]{ CoilNo });	
					
					
					logger.println(LogLevel.DEBUG,this,"sCoilNoList Size="+sCoilNoList.size());
					logger.println(LogLevel.DEBUG,this,"sCoilNoList "+sCoilNoList);
					// 공통코일정보 테이블에서 보급 요구된 코일정보
					String queryID      = "ym.common.dao.selectCommonCoilInfo";
					JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
					
					for (int i =0; i < sCoilNoList.size() ; i++) 
					{					
						JDTORecord sCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ sCoilNoList.get(i) });
						// 보급요구된 저장품의 두께, 폭,
						
						//int mCoilThickness = mCommonCoilinfo.getFieldInt("COIL_T");
						//int mCoilWidth = mCommonCoilinfo.getFieldInt("COIL_W");
						
						float fCoilThickness = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_T"));
						float fCoilWidth     = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_W"));
						
						// 시스템 판단 코일소재의 두께, 폭
						//int sCoilThickness = sCommonCoilinfo.getFieldInt("COIL_T");
						//int sCoilWidth = sCommonCoilinfo.getFieldInt("COIL_W");
						float fCoilThickness_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_T"));
						float fCoilWidth_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_W"));
						
						
						// CGS
						// 코일두께와 폭을 비교해서 허용 오차 범위에 있을 경우에만 보급.
						// 오차범위 기준은 임시로 지정. 테스트시 정확한 기준을 받아서 수정한다.
						String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilInfoStd";
						JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"S"});
						
						// 입력 기준 데이터가 float 형이다. 소숫점 1자리까지.최규성 2009-12-02
/*						int nStdThick = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
						int nStdWidth = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
						
						
						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
						
						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= nStdThick )  
								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= nStdWidth) 
							   )	
*/
						float fStdThick = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
						float fStdWidth = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
						
						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
						
						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= fStdThick )  
								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= fStdWidth) 
							   )	
						{
							// CGS 추가 
							// 스케줄 호출하기전 To위치에 대한 정보를 검사한다.

							isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), (String)sCoilNoList.get(i),Position);
							LogMgProcess(YardID, WorkID, ProcessID, (String)sCoilNoList.get(i), "보급 작업예약 편성완료-주/보조 구분");
							logger.println(LogLevel.DEBUG,this,"보급작업예약 편성완료-주/보조 구분[" +(String)sCoilNoList.get(i)+ "]");
							
							if (isSuccess) isConvSub = true;
						}
					}	// for END

					
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(),Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "보급 작업예약 편성완료");
					//isSuccess = SPMLineInRequestSearch(YardID.trim(), WorkID.trim(), ProcessID.trim(),Position.trim());
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
					} else if(WorkID.equals("S")){
						Workchk="SPM";
					} else if(WorkID.equals("N")){
						Workchk = "SPM2";
					}
					
	
					logger.println(LogLevel.DEBUG,this, ">>★★★★★★★★★보관매출 자동정정실적처리[SPM2]>>>" + CoilNo.trim());
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICoilInfoReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("setInnerIFCoilInfo_02",
												new  Class[]{String.class,String.class},
												new Object[]{ CoilNo.trim() ,Workchk});					 
					
				}
				//***************************************************************************************************************
				
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 추출 isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "추출 작업예약 편성완료");
				}
									
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){                     // Take-Out
				isSuccess = callSPMTakeOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim(), TakeOutProcess.trim());
				LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-Out 작업예약 편성완료");
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                     // Take-In
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< Take-In isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-In 작업예약 편성완료");
				}
			}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveSPMConStatM(String,String,String,String)");
			
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 포장 line off
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procYardLineOffReqAB(JDTORecord msgRecord)throws DAOException  {
		
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO craneSchDAO         = new CraneSchDAO();
		YdEquipDAO  ydEquipDAO          = new YdEquipDAO();
		
		YdDaoUtils ydDaoUtils   = new YdDaoUtils();
		YdDelegate ydDelegate 	= new YdDelegate();
		String szMsg           	= "";
		String szMethodName    	= "procYardLineOffReqAB";
		
		//레코드 선언
		JDTORecord inRecord  	= null;
		JDTORecord outRecord  	= JDTORecordFactory.getInstance().create(); 
		JDTORecord recInTemp  	= JDTORecordFactory.getInstance().create(); 
		
		String szSTL_NO         = null;
		String szYD_SCH_CD      = null;
		String sRTN_CD			= null;
		String sRTN_MSG			= null;
		String sYD_WBOOK_ID		= null;
		String sYD_WRK_CRN		= null;
		String szYD_BAY_GP      = "";
		String szCHK		      = "";
		int wbookstockId = 0;
		EJBConnector ejbConn 	= null;		
		String  sStockInfo      = "";
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "포장재 LINE OFF";

			//보급추출(보급:1,추출:3)
			szCHK = ydDaoUtils.paraRecChkNull(msgRecord,"CHK");
			if(szCHK.equals("")) {
				szMsg = "[전문 이상] 보급추출구분이 없습니다.";
				logger.println(LogLevel.DEBUG,this, szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
			//설비ID
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord,"YD_BAY_GP");
			if(szYD_BAY_GP.equals("")) {
				szMsg = "[전문 이상] 동이 없습니다.";
				logger.println(LogLevel.DEBUG,this, szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}		
			
			//재료번호
			szSTL_NO 	= ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
			if(szSTL_NO.equals("")) {
				szMsg = "[전문 이상] 재료번호가 없습니다.";
				logger.println(LogLevel.DEBUG,this, szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);		
				return outRecord;
			}
			
			
			//스케줄 코드 생성 
			if (szCHK.equals("1")) {
				szYD_SCH_CD =  "CGSI"; //지포장 보급
				sStockInfo  = YmCommonConst.NEW_STOCK_MOVE_TERM_GC; //지포장 보급
			} else if (szCHK.equals("3")) {
				szYD_SCH_CD =  "CGSO"; //지포장 추출
				sStockInfo  = YmCommonConst.NEW_STOCK_MOVE_TERM_A8; //지포장 추출
			}	
			
			
			
			//TB_YM_STOCK에서 정보가 존재하지 않으면 Error.
			String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ szSTL_NO.trim() });
			
 
			// Scrap에 대한 정보는 임시로 저장함.
			if (StockCoilNo == null){ 
				szMsg = "[전문 이상] StockCoilNo Error 재료번호가 없습니다.";
				logger.println(LogLevel.DEBUG,this, szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);		
				return outRecord;			
			}else{	
			
				logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< StockCoilNo="+ StockCoilNo);
				
				String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), ""); // CoilNo
				String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), ""); // 작업예약 ID
				
				
				if (wbookId != null && !wbookId.equals("")){
					szMsg = "[전문 이상] 작업요구한 CoilNo이 이미 작업예약되어 있슴  Error";
					logger.println(LogLevel.DEBUG,this, szMsg);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);		
					return outRecord;
				}
			}
			
			
			
			//적치 상태 체크 
			String stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
			JDTORecord StackColGp  = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ szSTL_NO });

			if (StackColGp == null){
				szMsg = "[전문 이상] 재료번호가 없습니다.";
				logger.println(LogLevel.DEBUG,this, szMsg);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);		
				return outRecord;								
			}	
			
			logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< StackColGp="+ StackColGp);

			String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");		// 적치열 정보(6자리)
			String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");	// 야드 구분(1자리)
			String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");	// 동 구분(1자리)
			String stackStat = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");	// 적치 단 상태
			
			 // 적치단  Table Update(작업요구상태='S'로 변경)
			if (stackStat.trim().equals(YmCommonConst.STACK_LAYER_STAT_L)){
				String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
				int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
						       YmCommonConst.STACK_LAYER_STAT_S, szSTL_NO.trim() });

				logger.println(LogLevel.DEBUG,this, ">>callLineInOut()<< stockId="+ szSTL_NO.trim()+ "stkColGp="+stkColGp);
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

			//지포장 보급
			if(szYD_SCH_CD.equals("CGSI")) {
				String sWBookQueryId = "ym.common.dao.insertWBook";
				// 작성된 작업예약을 DB에 저장한다.
				wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil 지포장 보급 
						       wBookid, stackCol1, stackCol2, szYD_SCH_CD, "O", "3"+stackCol2+"GF01" ,
							   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
			}else {
				String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				// 작성된 작업예약을 DB에 저장한다.
				wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ //Coil 지포장 추출 
						       wBookid, stackCol1, stackCol2, szYD_SCH_CD, 
							   YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	
				
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
			
			String updateYdStockStockId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
			int updateydstockstockid    = ydStockDAO.requestupdateData(updateYdStockStockId, new Object[]{ 
		                                  wBookid, sStockInfo, szSTL_NO.trim() });	
			//===============================================================================================
			
			
			
			//===============================================================================================
			//Coil Schedule EJB Call
			logger.println(LogLevel.DEBUG,this, "지포장 스케줄 Call="+ wBookid.trim() + " / stockId="+ szSTL_NO.trim());
			ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ 
					wBookid });
			//===============================================================================================
			
			
			
			
			
			
			
			szMsg = "지포장 보급추출작업 완료";
			logger.println(LogLevel.DEBUG,this, szMsg);
			outRecord.setField("RTN_CD" , "1");	
			outRecord.setField("RTN_MSG", szMsg);		
			return outRecord;
			
		} catch (Exception e) {
		
			szMsg = "C포장재 LINE OFF 요구 처리중 ERROR : " + e.getMessage();
			logger.println(LogLevel.DEBUG,this, szMsg);
			//return;
			throw new DAOException(e);
		
		}	// end try catch
			
	
	} //end of procYardLineOffReqAB
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////B열연수정시작///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////	
	/**
	 * 오퍼레이션명 : 
	 * 
	 *  조업 LEVEL3로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
     *  전문내용을 JDTORecord로 파싱한다.
     *  업무 로직
     *	1.TC_CD - POYM010 (I/F ID : YM-LIF-020 )
     *	2.조업 LEVEL3로부터 SPM / HFL 작업 요구 정보를 수신
     *
     *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receivePOYM010(JDTORecord rcvMsg){
//	public boolean receiveSPMConStatM(POYM010 pOYM010) { 
				   
		boolean isSuccess = false;
		boolean isConvSub = false; // 보조 작업을 주작업으로 변환했는지 여부 판정.

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveSPM2ConStatM:receivePOYM010()");
			
			YdStockDAO ydStockDAO = new YdStockDAO();			// 보조작업 -> 주작업 쿼리 DAO
			
			int iResult = 0;
			
//S			String YardID    = pOYM010.getYardId();
//S			String WorkID    = pOYM010.getWorkId(); 
//S			String ProcessID = pOYM010.getProcessId();
//S			String CoilNo    = pOYM010.getCoilNo();
//S			String Position  = pOYM010.getPosition();  // 조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
//S			String TakeOutProcess  = pOYM010.getTakeOutProcess();  // Take-Out시 1:결번, 2:임시보류처리(잠시 내려놨다가 Take-In할 Coil) 

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

			
			LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "POYM010 수신");
			logger.println(LogLevel.DEBUG,this, ">>receiveSPM2ConStatM()<< 수신항목: YardID=" + YardID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPM2ConStatM()<< 수신항목: WorkID=" + WorkID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPM2ConStatM()<< 수신항목: ProcessID=" + ProcessID);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPM2ConStatM()<< 수신항목: CoilNo=" + CoilNo);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPM2ConStatM()<< 수신항목: Position=" + Position);
			logger.println(LogLevel.DEBUG,this, ">>receiveSPM2ConStatM()<< 수신항목: TakeOutProcess=" + TakeOutProcess);
			
			/**
			 * SPM2의 HFL기능 추가. 최규성 2010-02-08
			 * @param YardID : 야드구분
			 * @param WorkID : S 기존 SPM, N 신규 SPM, F 신규SPM내 HFL
			 * @param ProcessID : 1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
			 * @param CoilNo:  'S' Scrap  'H' A열연  'K' B열연 
			 */
			
			// 작업구분
			if (ProcessID.equals(YmCommonConst.PROCESS_ID_1)){                           // 보급
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPM2ConStatM()<< 보급 isSuccess =" + isSuccess);
				
				
				if (isSuccess){
					// 상단 코일의 정정 여부 판정.
					logger.println(LogLevel.DEBUG,this,">>receiveSPM2ConStatM()<< 상단의 코일 검사 = "+CoilNo);
					// 최규
					// 보조작업이지만 주작업 가능한 코일번호를 반환한다.
					// 따로 함수를 만들지 않음.
					EJBConnector ejbConn = new EJBConnector("default","JNDISPMConStatReg",this);
					List sCoilNoList = new ArrayList();
					
					if (WorkID.equals("N")){		// SPM 작업일 경우에만 보조작업에 대한 정보를 처리한다.
						sCoilNoList = (List)ejbConn.trx("checkPoJungWorkExist",new Class[]{String.class},new Object[]{ CoilNo });	
					}
					
					logger.println(LogLevel.DEBUG,this,"sCoilNoList Size="+sCoilNoList.size());
					logger.println(LogLevel.DEBUG,this,"sCoilNoList "+sCoilNoList);
					// 공통코일정보 테이블에서 보급 요구된 코일정보
					String queryID      = "ym.common.dao.selectCommonCoilInfo";
					JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
					
					for (int i =0; i < sCoilNoList.size() ; i++) 
					{					
						JDTORecord sCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ sCoilNoList.get(i) });
						// 보급요구된 저장품의 두께, 폭,
						
						//int mCoilThickness = mCommonCoilinfo.getFieldInt("COIL_T");
						//int mCoilWidth = mCommonCoilinfo.getFieldInt("COIL_W");
						
						float fCoilThickness = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_T"));
						float fCoilWidth     = Float.parseFloat(mCommonCoilinfo.getFieldString("COIL_W"));
						
						// 시스템 판단 코일소재의 두께, 폭
						//int sCoilThickness = sCommonCoilinfo.getFieldInt("COIL_T");
						//int sCoilWidth = sCommonCoilinfo.getFieldInt("COIL_W");
						float fCoilThickness_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_T"));
						float fCoilWidth_l = Float.parseFloat(sCommonCoilinfo.getFieldString("COIL_W"));
						
						
						// CGS
						// 코일두께와 폭을 비교해서 허용 오차 범위에 있을 경우에만 보급.
						// 오차범위 기준은 임시로 지정. 테스트시 정확한 기준을 받아서 수정한다.
						String sQueryIdStd = "ym.steelinfo.steelinforecv.YdStockDAO.getCoilInfoStd";
						JDTORecord jtrStdData = ydStockDAO.getData(sQueryIdStd,new Object[] {"S"});
						
						// 입력 기준 데이터가 float 형이다. 소숫점 1자리까지.최규성 2009-12-02
/*						int nStdThick = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
						int nStdWidth = Integer.parseInt(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
						
						
						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
						
						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= nStdThick )  
								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= nStdWidth) 
							   )	
*/
						float fStdThick = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("두께"), ""));
						float fStdWidth = Float.parseFloat(StringHelper.evl(jtrStdData.getFieldString("폭"),""));
						
						logger.println(LogLevel.DEBUG,this,"두께 비교" + String.valueOf(Math.abs(fCoilThickness - fCoilThickness_l) ) );
						logger.println(LogLevel.DEBUG,this,"폭폭 비교" + String.valueOf(Math.abs(fCoilWidth - fCoilWidth_l) ) );
						
						if (   (Math.abs(fCoilThickness - fCoilThickness_l) <= fStdThick )  
								&& (Math.abs(fCoilWidth - fCoilWidth_l)         <= fStdWidth) 
							   )	
						{
							// CGS 추가 
							// 스케줄 호출하기전 To위치에 대한 정보를 검사한다.

							isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), (String)sCoilNoList.get(i),Position);
							LogMgProcess(YardID, WorkID, ProcessID, (String)sCoilNoList.get(i), "보급 작업예약 편성완료-주/보조 구분");
							logger.println(LogLevel.DEBUG,this,"보급작업예약 편성완료-주/보조 구분[" +(String)sCoilNoList.get(i)+ "]");
							
							if (isSuccess) isConvSub = true;
						}
					}	// for END

					
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(),Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "보급 작업예약 편성완료");
					//isSuccess = SPMLineInRequestSearch(YardID.trim(), WorkID.trim(), ProcessID.trim(),Position.trim());
			    }
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_2)){                     // 취소
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPM2ConStatM()<< 보급 취소 CoilNo="+CoilNo );
				
				//코일 번호로 크레인 스케줄을 가져 온다.
//				String queryID      = "ym.common.dao.selectSchCoilInfo";
//				JDTORecord mCommonCoilinfo = ydStockDAO.getData(queryID, new Object[]{ CoilNo.trim() });
				
//				if(mCommonCoilinfo.size()>0){
//					String sCH_ID = mCommonCoilinfo.getFieldString("SCH_ID");
				
				JDTORecord jrecord = JDTORecordFactory.getInstance().create();
				
				/*
				select SCH_ID
				 from USRYMA.TB_YM_SCH
				where STOCK_ID =:V_STOCK_ID
				 and SCH_WORK_STAT in('1','S')
				 */
				jrecord.setField("STOCK_ID"             , CoilNo.trim());
				
				JDTORecordSet jsScrSchList = commDao.select(jrecord, "ym.common.dao.selectSchCoilInfo", "SYSTEM", "receivePOYM010", "스케쥴존재 여부 조회");
				
				if(jsScrSchList.size()>0){ 
					String sCH_ID = jsScrSchList.getRecord(0).getFieldString("SCH_ID");
					
					//권상이전 크레인 스케줄/작업예약  취소 작업
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
					Boolean resultRes = (Boolean) ejbConn1.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {CoilNo});
				
				}else{
					logger.println(LogLevel.DEBUG,this, ">>receiveSPM2ConStatPOYMJ010()<< 권상 이후에는 취소가 불가능 합니다." );
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
					} else if(WorkID.equals("S")){
						Workchk="SPM";
					} else if(WorkID.equals("N")){
						Workchk = "SPM2";
					}
					
	
					logger.println(LogLevel.DEBUG,this, ">>★★★★★★★★★보관매출 자동정정실적처리[SPM2]>>>" + CoilNo.trim());
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICoilInfoReg",this);
					Boolean isTemp = (Boolean)ejbConn1.trx("setInnerIFCoilInfo_02",
												new  Class[]{String.class,String.class},
												new Object[]{ CoilNo.trim() ,Workchk});					 
					
				}
				//***************************************************************************************************************
				
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< 추출 isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "추출 작업예약 편성완료");
				}
									
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_4)){                     // Take-Out
				isSuccess = callSPMTakeOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim(), TakeOutProcess.trim());
				LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-Out 작업예약 편성완료");
				
			}else if (ProcessID.equals(YmCommonConst.PROCESS_ID_5)){                     // Take-In
				isSuccess = SPMProcessCheck(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim());
				
				logger.println(LogLevel.DEBUG,this, ">>receiveSPMConStatM()<< Take-In isSuccess =" + isSuccess);
				
				if (isSuccess == true){
					isSuccess = callSPMLineInOut(YardID.trim(), WorkID.trim(), ProcessID.trim(), CoilNo.trim(), Position.trim());
					LogMgProcess(YardID, WorkID, ProcessID, CoilNo, "Take-In 작업예약 편성완료");
				}
			}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveSPMConStatM(POYM010)");
			
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException("=JDTORecord-POYM010추출요구 추출측존에 코일이 존재 안합니다.");
	    }
	}	
}



