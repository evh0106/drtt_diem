/**
 * @(#)CondPredKmaSeEJBSBean
 *
 * @version		V1.00
 * @author		현대제철
 * @date		2025/02/27
 *
 * @description	열연 결로 예측(Condensation Prediction) 시스템 기상청 API
 * 
 * -------------------------------------------------------------------------------
 * Ver.		수정일자	요청자	수정자	내용
 * =======	==========	======	======	==========================================
 * V1.00	2025/02/27	정종균	양태호	최초 등록
 * 
 */

package com.inisteel.cim.yf.condpred.session;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.condpred.CondPredQueryIF;
import com.inisteel.cim.yf.condpred.CondPredUtil;

import org.xml.sax.SAXException;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

/**
 * [A] 클래스명 : 열연 결로 예측(Condensation Prediction) 시스템 기상청 API session EJB
 *
 * @ejb.bean name="CondPredKmaSeEJB" jndi-name="CondPredKmaSeEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class CondPredKmaSeEJBSBean extends BaseSessionBean implements CondPredQueryIF {
	private static final long serialVersionUID = 1L;
	private YfCommUtils commUtils = new YfCommUtils();
	private YfCommDAO commDao = new YfCommDAO();
	private CondPredUtil condpredUtil = new CondPredUtil();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * [A] 오퍼레이션명 : 기상청 API 메인 프로세스
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 *            rcvMsg
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord doMainActivity(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "기상청 데이터 시작[CondPredKmaSeEJBSBean.doMainActivity] < " + rcvMsg.getResultMsg();
		rcvMsg.setResultCode(commUtils.getLogId(YfConstant.YD_GP_1));
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); // 전문 Return

		try {
			commUtils.printLog(logId, methodNm, "S+");

			Calendar today = Calendar.getInstance();

			SimpleDateFormat chkTime = new SimpleDateFormat("yyyyMMddHHmm");
			String checkTime = chkTime.format(today.getTime());
			String nowDate = checkTime.substring(0, 8);
			String nowHour = checkTime.substring(8, 10);

			// 특정일 데이터 누락일때 화면에서 시간을 입력하여 기상청 데이터 가져오기 위한 날짜부분
			String f_date = commUtils.trim(rcvMsg.getFieldString("fromDate"));
			String s_time = commUtils.trim(rcvMsg.getFieldString("time"));

			String reqDate = nowDate;
			String reqHour = nowHour;

			if (!f_date.equals("") && !s_time.equals("")) {
				commUtils.printLog(logId, methodNm, "특정일시로 기상청 데이터 요청 시작");
				reqDate = f_date;
				reqHour = s_time;
			}

			commUtils.printLog(logId, methodNm, "단기요청일시 = " + reqDate);
			commUtils.printLog(logId, methodNm, "단기요청시간 = " + reqHour);
			commUtils.printLog(logId, methodNm, "초단기요청일시 = " + reqDate);
			commUtils.printLog(logId, methodNm, "초단기요청시간 = " + reqHour);

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			jrParam.setField("reqDate", reqDate);
			jrParam.setField("reqHour", reqHour);
			String[] cnt_Chk = searchChk(jrParam);

			if (cnt_Chk == null || cnt_Chk.length < 6) {
				return jrRtn;
			}
			
			// 서비스키 조회
			// 2025.10.27 기상청 API 대체
			JDTORecord row = getServiceKey(jrParam);
			String servicekey = row.getFieldString("SERVICEKEY");
			String useYN = row.getFieldString("USE_YN");
			rcvMsg.setField("serviceKey", servicekey); // API 인증키
			rcvMsg.setField("useYN", useYN); // 대체유무 Y - apihub.kma.go.kr / 대체유무 N - apis.data.go.kr

			// 기상청 날씨는 2시부터 3시간 간격으로 일 8회 발표한다. 기타 시간에는 데이터가 없음.
			if (reqHour.equals("02") || reqHour.equals("05") || reqHour.equals("08") || reqHour.equals("11") || reqHour.equals("14") || reqHour.equals("17") || reqHour.equals("20")
					|| reqHour.equals("23")) {

				// 당진 송산면 단기 예보 등록
				// (55,113)---------------------------------------------------------------------------------------------
				if (cnt_Chk[0].equals("0")) {
					commUtils.printLog(logId, methodNm, "당진 송산면 단기예보 시작");
					getVilageFcst(rcvMsg, 55, 113, reqDate, reqHour, "2000", "송산면");
					commUtils.printLog(logId, methodNm, "당진 송산면 단기예보 종료");
				}

				// 당진 송악읍 단기 예보 등록
				// (55,112)---------------------------------------------------------------------------------------------
				if (cnt_Chk[1].equals("0")) {
					commUtils.printLog(logId, methodNm, "당진 송악읍  단기예보 시작");
					getVilageFcst(rcvMsg, 55, 112, reqDate, reqHour, "2000", "송악읍");
					commUtils.printLog(logId, methodNm, "당진 송악읍  단기예보 종료");
				}
			}

			// 당진 송산면 초단기 실황 등록
			// ---------------------------------------------------------------------------------------------
			// 초단기는 1시간 간격으로 데이터가 있으나 1시간 이전 데이터까지만 있음
			if (cnt_Chk[2].equals("0")) {
				commUtils.printLog(logId, methodNm, "당진 송산면 초단기실황 시작");
				getUltraSrtNcst(rcvMsg, 55, 113, reqDate, reqHour, "2000", "송산면");
				commUtils.printLog(logId, methodNm, "당진 송산면 초단기실황 종료");
			}

			// 당진 송악읍 초단기 실황 등록
			// ---------------------------------------------------------------------------------------------
			// 초단기는 1시간 간격으로 데이터가 있으나 1시간 이전 데이터까지만 있음
			if (cnt_Chk[3].equals("0")) {
				commUtils.printLog(logId, methodNm, "당진 송악읍 초단기실황 시작");
				getUltraSrtNcst(rcvMsg, 55, 112, reqDate, reqHour, "2000", "송악읍");
				commUtils.printLog(logId, methodNm, "당진 송악읍 초단기실황 종료");
			}

			// 당진 송산면 초단기 예보 등록
			// ---------------------------------------------------------------------------------------------
			// 매시간 30분에 생성되고 10분마다 최신 정보로 업데이트(기온, 습도, 바람)
			if (cnt_Chk[4].equals("0")) {
				commUtils.printLog(logId, methodNm, "당진 송산면 초단기예보 시작");
				getUltraSrtFcst(rcvMsg, 55, 113, reqDate, reqHour, "2000", "송산면");
				commUtils.printLog(logId, methodNm, "당진 송산면 초단기예보 종료");
			}

			// 당진 송악읍 초단기 예보 등록
			// ---------------------------------------------------------------------------------------------
			// 매시간 30분에 생성되고 10분마다 최신 정보로 업데이트(기온, 습도, 바람)
			if (cnt_Chk[5].equals("0")) {
				commUtils.printLog(logId, methodNm, "당진 송악읍 초단기예보 시작");
				getUltraSrtFcst(rcvMsg, 55, 112, reqDate, reqHour, "2000", "송악읍");
				commUtils.printLog(logId, methodNm, "당진 송악읍 초단기예보 종료");
			}

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		}
	}

	public void getVilageFcst(JDTORecord rcvMsg, int x, int y, String dd, String dt, String plntTp, String loc) {
		String methodNm = "기상청 단기예보 조회 [CondPredKmaSeEJBSBean.getVilageFcst] < " + rcvMsg.getResultMsg() + " " + loc;
		String logId = rcvMsg.getResultCode();

		HttpURLConnection con = null;
		// 보안 구성이 적용된 DocumentBuilderFactory 생성
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		InputStream is = null;
		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printLog(logId, methodNm, "dd:" + dd + " dt:" + dt + " x:" + x + " y:" + y);
			
			String serviceKey = rcvMsg.getFieldString("serviceKey");
			String useYN = rcvMsg.getFieldString("useYN"); //2025.10.27 기상청 API 대체
			
			URL url = new URL("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"
					+ "?serviceKey=" + serviceKey // 서비스키
					+ "&pageNo=1" // 페이지번호 Default: 1
					+ "&numOfRows=1000" // 한 페이지 결과 수 (10개 카테고리값 * 6시간)
					+ "&dataType=XML" // 요청자료형식(XML/JSON) Default: XML
					+ "&base_date=" + dd // 발표 날짜
					+ "&base_time=" + dt + "00" // 발표 시각
					+ "&nx=" + x // 예보지점의 X 좌표값
					+ "&ny=" + y // 예보지점의 Y 좌표값
			);
			
			//2025.10.27 기상청 API 대체
			if ("Y".equals(useYN)) {
				url = new URL("http://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getVilageFcst"
						+ "?authKey=" + serviceKey // 서비스키
						+ "&pageNo=1" // 페이지번호 Default: 1
						+ "&numOfRows=1000" // 한 페이지 결과 수 (10개 카테고리값 * 6시간)
						+ "&dataType=XML" // 요청자료형식(XML/JSON) Default: XML
						+ "&base_date=" + dd // 발표 날짜
						+ "&base_time=" + dt + "00" // 발표 시각
						+ "&nx=" + x // 예보지점의 X 좌표값
						+ "&ny=" + y // 예보지점의 Y 좌표값
				);
			} 			
			
//			commUtils.printLog(logId, methodNm, "url:" + url);

			con = (HttpURLConnection) url.openConnection();
			
			// XXE 공격 방어 설정
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setXIncludeAware(false);
			dbf.setExpandEntityReferences(false);

			// 안전한 DocumentBuilder 생성
			DocumentBuilder db = dbf.newDocumentBuilder();
			is = con.getInputStream();
			Document doc = db.parse(is);
			
			NodeList headers = doc.getElementsByTagName("header"); // header의 갯수는 1개 이다.

			if (headers.getLength() == 0) {
				commUtils.printLog(logId, methodNm, "기상청 API 응답 실패 - header 없음");
				return;
			}

			Element header = (Element) headers.item(0);
			String resultCode = header.getElementsByTagName("resultCode").item(0).getTextContent();

			if (!"00".equals(resultCode)) {
				String errorMsg = header.getElementsByTagName("resultMsg").item(0).getTextContent();
				commUtils.printLog(logId, methodNm, "기상청 API 애러 코드 응답 수신됨(" + resultCode + ") " + errorMsg);
				return;
			}

			NodeList items = doc.getElementsByTagName("item");
			List list = new ArrayList();
			Map data = null;

			for (int i = 0; i < items.getLength(); i++) {
				Element e = (Element) items.item(i);

				String baseDate = e.getElementsByTagName("baseDate").item(0).getTextContent();
				String baseTime = e.getElementsByTagName("baseTime").item(0).getTextContent();
				String fcstDate = e.getElementsByTagName("fcstDate").item(0).getTextContent();
				String fcstTime = e.getElementsByTagName("fcstTime").item(0).getTextContent();
				String category = e.getElementsByTagName("category").item(0).getTextContent();
				String fcstValue = e.getElementsByTagName("fcstValue").item(0).getTextContent();

				commUtils.printLog(logId, methodNm, "baseDate:" + baseDate + ",baseTime:" + baseTime + ",fcstDate:" + fcstDate + ",fcstTime:" + fcstTime + " " + category + ":" + fcstValue);

				boolean isNewEntry = false;
				if (data == null) {
					isNewEntry = true;
				} else {
					String prevFcstDate = (String) data.get("fcstDate");
					String prevFcstTime = (String) data.get("fcstTime");
					
					if (!fcstDate.equals(prevFcstDate) || !fcstTime.equals(prevFcstTime)) {
						isNewEntry = true;
					}
				}
				
				if (isNewEntry) {
					if (data != null) {
						list.add(data);
					}
					data = new HashMap();
					data.put("baseDate", baseDate);
					data.put("baseTime", baseTime);
					data.put("fcstDate", fcstDate);
					data.put("fcstTime", fcstTime);
				}
				
				data.put(category, fcstValue);
				
				if (i == items.getLength() - 1) {
					list.add(data);
				}
			}

			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);

				String fcstDate = (String) map.get("fcstDate");
				String fcstTime = (String) map.get("fcstTime");
				String baseDate = (String) map.get("baseDate");
				String baseTime = (String) map.get("baseTime");
				String tmp = map.containsKey("TMP") ? (String) map.get("TMP") : null; // 1시간 기온
				String vec = map.containsKey("VEC") ? (String) map.get("VEC") : null; // 풍향
				String wsd = map.containsKey("WSD") ? (String) map.get("WSD") : null; // 풍속
				String sky = map.containsKey("SKY") ? (String) map.get("SKY") : null; // 하늘 상태
				String pty = map.containsKey("PTY") ? (String) map.get("PTY") : null; // 강수형태:없음(0),비(1),비/눈(2),눈(3),소니기(4)
				String pop = map.containsKey("POP") ? (String) map.get("POP") : null; // 강수확률
				String pcp = map.containsKey("PCP") ? (String) map.get("PCP") : null; // 1시간 강수량
				String reh = map.containsKey("REH") ? (String) map.get("REH") : null; // 습도

				if (tmp == null)
					tmp = "";
				if (vec == null)
					vec = "";
				if (wsd == null)
					wsd = "";
				if (sky == null)
					sky = "";
				if (pty == null)
					pty = "0";
				if (pop == null)
					pop = "";
				pcp = condpredUtil.convRN1(pcp);
				if (reh == null)
					reh = "";

				// DB Query 파라메터 설정
				JDTORecord jrParam = JDTORecordFactory.getInstance().create(); // Query 실행시 파라메터 전달용 JDTORecord
				jrParam.setResultCode(logId); // Log ID
				jrParam.setResultMsg(methodNm); // Log Method Name

				jrParam.setField("PLNT_TP", plntTp); // 야드 구분
				jrParam.setField("FCST_TP", "1"); // 1:단기예보,2:초단기예보,3:초단기실황
				jrParam.setField("FCST_LOC", loc); // 기상위치
				jrParam.setField("FCST_TIME", fcstDate + fcstTime.substring(0, 2)); // 시간
				jrParam.setField("FCST_MESTM", baseDate + baseTime); // 등록시간
				jrParam.setField("FCST_TEMP", tmp); // 현재시간온도
				jrParam.setField("FCST_SKY", sky); // 하늘상태코드
				jrParam.setField("FCST_PTY", pty); // 강수상태코드:없음(0),비(1),비/눈(2),눈(3),소니기(4)
				jrParam.setField("FCST_POP", pop); // 강수확률
				jrParam.setField("FCST_WSD", wsd); // 풍속
				jrParam.setField("FCST_VEC", vec); // 풍향
				jrParam.setField("FCST_REH", reh); // 습도
				jrParam.setField("FCST_RN1", pcp); // 강수량
				jrParam.setField("REGISTER", "usryfa"); // 등록자
				jrParam.setField("MODIFIER", "usryfa"); // 수정자

				commDao.insert(jrParam, insWthrFcst, logId, methodNm, "기상청 단기예보 등록");
			}

			commUtils.printLog(logId, methodNm, "S-");

		} catch (IOException e) {
			commUtils.printLog(logId, methodNm, "기상청 단기예보 조회 Error : " + e.getMessage());
		} catch (ParserConfigurationException e) {
			commUtils.printLog(logId, methodNm, "기상청 단기예보 조회 Error : " + e.getMessage());
		} catch (JDTOException e) {
			commUtils.printLog(logId, methodNm, "기상청 단기예보 조회 Error : " + e.getMessage());
		} catch (SAXException e) {
			commUtils.printLog(logId, methodNm, "기상청 단기예보 조회 Error : " + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					commUtils.printLog(logId, methodNm, "기상청 단기예보 조회 Error : " + e.getMessage());
				}
			}
			if (con != null)
				con.disconnect();
		}
	}

	public void getUltraSrtNcst(JDTORecord rcvMsg, int x, int y, String dd, String dt, String plntTp, String loc) {
		String methodNm = "기상청 초단기실황 조회 [CondPredKmaSeEJBSBean.getUltraSrtNcst] < " + rcvMsg.getResultMsg() + " " + loc;
		String logId = rcvMsg.getResultCode();

		HttpURLConnection con = null;
		// 보안 구성이 적용된 DocumentBuilderFactory 생성
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		InputStream is = null; 
		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printLog(logId, methodNm, "dd:" + dd + " dt:" + dt + " x:" + x + " y:" + y);
			
			String serviceKey = rcvMsg.getFieldString("serviceKey");
			String useYN = rcvMsg.getFieldString("useYN"); //2025.10.27 기상청 API 대체

			URL url = new URL("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"
					+ "?serviceKey=" + serviceKey // 서비스키(박정수책임)
					+ "&pageNo=1" // 페이지번호 Default: 1
					+ "&numOfRows=15" // 한 페이지 결과 수
					+ "&dataType=XML" // 요청자료형식(XML/JSON) Default: XML
					+ "&base_date=" + dd // 발표 날짜
					+ "&base_time=" + dt + "00" // 발표 시각
					+ "&nx=" + x // 예보지점의 X 좌표값
					+ "&ny=" + y // 예보지점의 Y 좌표값
			);
			
			//2025.10.27 기상청 API 대체
			if ("Y".equals(useYN)) {
				url = new URL("http://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getUltraSrtNcst"
						+ "?authKey=" + serviceKey // 서비스키
						+ "&pageNo=1" // 페이지번호 Default: 1
						+ "&numOfRows=15" // 한 페이지 결과 수
						+ "&dataType=XML" // 요청자료형식(XML/JSON) Default: XML
						+ "&base_date=" + dd // 발표 날짜
						+ "&base_time=" + dt + "00" // 발표 시각
						+ "&nx=" + x // 예보지점의 X 좌표값
						+ "&ny=" + y // 예보지점의 Y 좌표값
				);
			} 	
			
//			commUtils.printLog(logId, methodNm, "url:" + url);

			con = (HttpURLConnection) url.openConnection();
			
			// XXE 공격 방어 설정
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setXIncludeAware(false);
			dbf.setExpandEntityReferences(false);
			
			// 안전한 DocumentBuilder 생성
			DocumentBuilder db = dbf.newDocumentBuilder();
			is = con.getInputStream();
			Document doc = db.parse(is);
			
			NodeList headers = doc.getElementsByTagName("header"); // header의 갯수는 1개 이다.

			if (headers.getLength() == 0) {
				commUtils.printLog(logId, methodNm, "기상청 API 응답 실패 - header 없음");
				return;
			}

			Element header = (Element) headers.item(0);
			String resultCode = header.getElementsByTagName("resultCode").item(0).getTextContent();

			if (!"00".equals(resultCode)) {
				String errorMsg = header.getElementsByTagName("resultMsg").item(0).getTextContent();
				commUtils.printLog(logId, methodNm, "기상청 API 애러 코드 응답 수신됨(" + resultCode + ") " + errorMsg);
				return;
			}

			NodeList items = doc.getElementsByTagName("item");
			List list = new ArrayList();
			Map data = null;

			for (int i = 0; i < items.getLength(); i++) {
				Element e = (Element) items.item(i);

				String baseDate = e.getElementsByTagName("baseDate").item(0).getTextContent(); // 발표일자
				String baseTime = e.getElementsByTagName("baseTime").item(0).getTextContent(); // 발표시각
				String category = e.getElementsByTagName("category").item(0).getTextContent(); // 자료구분코드
				String obsrValue = e.getElementsByTagName("obsrValue").item(0).getTextContent(); // 예보 값

				commUtils.printLog(logId, methodNm, "baseDate:" + baseDate + ",baseTime:" + baseTime + " " + category + ":" + obsrValue);

				if (i == 0) {
					data = new HashMap();
					data.put("baseDate", baseDate);
					data.put("baseTime", baseTime);
				}
				
				if (data != null) {
					data.put(category, obsrValue);
				}
			}
			list.add(data);

			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);

				String baseDate = (String) map.get("baseDate");
				String baseTime = (String) map.get("baseTime");
				String pty = map.containsKey("PTY") ? (String) map.get("PTY") : null; // 강수형태:없음(0),비(1),비/눈(2),눈(3),빗방울(5),빗방울눈날림(6),눈날림(7)
				String rn1 = map.containsKey("RN1") ? (String) map.get("RN1") : null; // 1시간 강수량
				String tmp = map.containsKey("T1H") ? (String) map.get("T1H") : null; // 기온
				String reh = map.containsKey("PTY") ? (String) map.get("REH") : null; // 습도
				String vec = map.containsKey("REH") ? (String) map.get("VEC") : null; // 풍향
				String wsd = map.containsKey("WSD") ? (String) map.get("WSD") : null; // 풍속

				if (pty == null)
					pty = "0";
				rn1 = condpredUtil.convRN1(rn1);
				if (tmp == null)
					tmp = "";
				if (reh == null)
					reh = "";
				if (vec == null)
					vec = "";
				if (wsd == null)
					wsd = "";

				// DB Query 파라메터 설정
				JDTORecord jrParam = JDTORecordFactory.getInstance().create(); // Query 실행시 파라메터 전달용 JDTORecord
				jrParam.setResultCode(logId); // Log ID
				jrParam.setResultMsg(methodNm); // Log Method Name

				jrParam.setField("PLNT_TP", plntTp); // 야드 구분
				jrParam.setField("FCST_TP", "3"); // 단기,초단기_구분
				jrParam.setField("FCST_LOC", loc); // 기상위치
				jrParam.setField("FCST_TIME", baseDate + baseTime.substring(0, 2)); // 시간
				jrParam.setField("FCST_MESTM", baseDate + baseTime); // 등록시간
				jrParam.setField("FCST_TEMP", tmp); // 현재시간온도
				jrParam.setField("FCST_PTY", pty); // 강수상태코드:없음(0),비(1),비/눈(2),눈(3),빗방울(5),빗방울눈날림(6),눈날림(7)
				jrParam.setField("FCST_WSD", wsd); // 풍속
				jrParam.setField("FCST_VEC", vec); // 풍향
				jrParam.setField("FCST_REH", reh); // 습도
				jrParam.setField("FCST_RN1", rn1); // 강수량
				jrParam.setField("REGISTER", "usryfa"); // 등록자
				jrParam.setField("MODIFIER", "usryfa"); // 수정자

				commDao.insert(jrParam, insWthrFcst, logId, methodNm, "기상청 초단기실황 등록");
			}
			commUtils.printLog(logId, methodNm, "S-");
		} catch (IOException e) {
			commUtils.printLog(logId, methodNm, "기상청 초단기실황 조회 Error : " + e.getMessage());
		} catch (ParserConfigurationException e) {
			commUtils.printLog(logId, methodNm, "기상청 초단기실황 조회 Error : " + e.getMessage());
		} catch (JDTOException e) {
			commUtils.printLog(logId, methodNm, "기상청 초단기실황 조회 Error : " + e.getMessage());
		} catch (SAXException e) {
			commUtils.printLog(logId, methodNm, "기상청 초단기실황 조회 Error : " + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					commUtils.printLog(logId, methodNm, "기상청 초단기실황 조회 Error : " + e.getMessage());
				}
			}
			if (con != null)
				con.disconnect();
		}
	}

	public void getUltraSrtFcst(JDTORecord rcvMsg, int x, int y, String dd, String dt, String plntTp, String loc) {
		String methodNm = "기상청 초단기예보 조회 [CondPredKmaSeEJBSBean.getUltraSrtFcst] < " + rcvMsg.getResultMsg() + " " + loc;
		String logId = rcvMsg.getResultCode();

		HttpURLConnection con = null;
		// 보안 구성이 적용된 DocumentBuilderFactory 생성
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		InputStream is = null;
		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printLog(logId, methodNm, "dd:" + dd + " dt:" + dt + " x:" + x + " y:" + y);
			
			String serviceKey = rcvMsg.getFieldString("serviceKey");
			String useYN = rcvMsg.getFieldString("useYN"); //2025.10.27 기상청 API 대체

			URL url = new URL("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"
					+ "?serviceKey=" + serviceKey // 서비스키(박정수책임)
					+ "&pageNo=1" // 페이지번호 Default: 1
					+ "&numOfRows=200" // 한 페이지 결과 수
					+ "&dataType=XML" // 요청자료형식(XML/JSON) Default: XML
					+ "&base_date=" + dd // 발표 날짜
					+ "&base_time=" + dt + "00" // 발표 시각
					+ "&nx=" + x // 예보지점의 X 좌표값
					+ "&ny=" + y // 예보지점의 Y 좌표값
			);
			
			//2025.10.27 기상청 API 대체
			if ("Y".equals(useYN)) {
				url = new URL("http://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getUltraSrtFcst"
						+ "?authKey=" + serviceKey // 서비스키
						+ "&pageNo=1" // 페이지번호 Default: 1
						+ "&numOfRows=200" // 한 페이지 결과 수
						+ "&dataType=XML" // 요청자료형식(XML/JSON) Default: XML
						+ "&base_date=" + dd // 발표 날짜
						+ "&base_time=" + dt + "00" // 발표 시각
						+ "&nx=" + x // 예보지점의 X 좌표값
						+ "&ny=" + y // 예보지점의 Y 좌표값
				);
			} 	
			
//			commUtils.printLog(logId, methodNm, "url:" + url);

			con = (HttpURLConnection) url.openConnection();
			
			// XXE 공격 방어 설정
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setXIncludeAware(false);
			dbf.setExpandEntityReferences(false);
			
			// 안전한 DocumentBuilder 생성
			DocumentBuilder db = dbf.newDocumentBuilder();
			is = con.getInputStream();
			Document doc = db.parse(is);
			
			NodeList headers = doc.getElementsByTagName("header"); // header의 갯수는 1개 이다.

			if (headers.getLength() == 0) {
				commUtils.printLog(logId, methodNm, "기상청 API 응답 실패 - header 없음");
				return;
			}

			Element header = (Element) headers.item(0);
			String resultCode = header.getElementsByTagName("resultCode").item(0).getTextContent();

			if (!"00".equals(resultCode)) {
				String errorMsg = header.getElementsByTagName("resultMsg").item(0).getTextContent();
				commUtils.printLog(logId, methodNm, "기상청 API 애러 코드 응답 수신됨(" + resultCode + ") " + errorMsg);
				return;
			}

			NodeList items = doc.getElementsByTagName("item");
			List list = new ArrayList();
			Map data = null;

			for (int i = 0; i < items.getLength(); i++) {
				Element e = (Element) items.item(i);

				String baseDate = e.getElementsByTagName("baseDate").item(0).getTextContent(); // 발표일자
				String baseTime = e.getElementsByTagName("baseTime").item(0).getTextContent(); // 발표시각
				String fcstDate = e.getElementsByTagName("fcstDate").item(0).getTextContent();
				String fcstTime = e.getElementsByTagName("fcstTime").item(0).getTextContent();
				String category = e.getElementsByTagName("category").item(0).getTextContent(); // 자료구분코드
				String fcstValue = e.getElementsByTagName("fcstValue").item(0).getTextContent(); // 예보 값

				commUtils.printLog(logId, methodNm, "baseDate:" + baseDate + ",baseTime:" + baseTime + ",fcstDate:" + fcstDate + ",fcstTime:" + fcstTime + " " + category + ":" + fcstValue);

				data = new HashMap();
				data.put("baseDate", baseDate);
				data.put("baseTime", baseTime);
				data.put("fcstDate", fcstDate);
				data.put("fcstTime", fcstTime);
				data.put("baseDate", baseDate);
				data.put(category, fcstValue);

				list.add(data);
			}

			commUtils.printLog(logId, methodNm, "1-" + String.valueOf(list.size()));

			// 같은 날짜 별로 재 정의
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				String fcstDate = (String) map.get("fcstDate");
				String fcstTime = (String) map.get("fcstTime");
				String baseDate = (String) map.get("baseDate");
				String baseTime = (String) map.get("baseTime");

				for (int j = i + 1; j < list.size(); j++) {
					Map map2 = (Map) list.get(j);
					String fcstDate2 = (String) map2.get("fcstDate");
					String fcstTime2 = (String) map2.get("fcstTime");
					String baseDate2 = (String) map2.get("baseDate");
					String baseTime2 = (String) map2.get("baseTime");

					if (fcstDate.equals(fcstDate2) && fcstTime.equals(fcstTime2) && baseDate.equals(baseDate2) && baseTime.equals(baseTime2)) {
						map.putAll(map2);
						list.remove(j);
						j--;
					}
				}
			}

			commUtils.printLog(logId, methodNm, "2-" + String.valueOf(list.size()));

			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);

				String fcstDate = (String) map.get("fcstDate");
				String fcstTime = (String) map.get("fcstTime");
				String baseDate = (String) map.get("baseDate");
				String baseTime = (String) map.get("baseTime");
				String pty = map.containsKey("PTY") ? (String) map.get("PTY") : null; // 강수형태:없음(0),비(1),비/눈(2),눈(3),빗방울(5),빗방울눈날림(6),눈날림(7)
				String rn1 = map.containsKey("RN1") ? (String) map.get("RN1") : null; // 1시간 강수량
				String tmp = map.containsKey("T1H") ? (String) map.get("T1H") : null; // 기온
				String reh = map.containsKey("PTY") ? (String) map.get("REH") : null; // 습도
				String vec = map.containsKey("REH") ? (String) map.get("VEC") : null; // 풍향
				String wsd = map.containsKey("WSD") ? (String) map.get("WSD") : null; // 풍속

				if (pty == null)
					pty = "0";
				rn1 = condpredUtil.convRN1(rn1);
				if (tmp == null)
					tmp = "";
				if (reh == null)
					reh = "";
				if (vec == null)
					vec = "";
				if (wsd == null)
					wsd = "";

				// DB Query 파라메터 설정
				JDTORecord jrParam = JDTORecordFactory.getInstance().create(); // Query 실행시 파라메터 전달용 JDTORecord
				jrParam.setResultCode(logId); // Log ID
				jrParam.setResultMsg(methodNm); // Log Method Name

				jrParam.setField("PLNT_TP", plntTp); // 야드 구분
				jrParam.setField("FCST_TP", "2"); // 1:단기예보,2:초단기예보,3:초단기실황
				jrParam.setField("FCST_LOC", loc); // 기상위치
				jrParam.setField("FCST_TIME", fcstDate + fcstTime.substring(0, 2)); // 시간
				jrParam.setField("FCST_MESTM", baseDate + baseTime); // 등록시간
				jrParam.setField("FCST_TEMP", tmp); // 현재시간온도
				jrParam.setField("FCST_PTY", pty); // 강수상태코드:없음(0),비(1),비/눈(2),눈(3),빗방울(5),빗방울눈날림(6),눈날림(7)
				jrParam.setField("FCST_WSD", wsd); // 풍속
				jrParam.setField("FCST_VEC", vec); // 풍향
				jrParam.setField("FCST_REH", reh); // 습도
				jrParam.setField("FCST_RN1", rn1); // 강수량
				jrParam.setField("REGISTER", "usryfa"); // 등록자
				jrParam.setField("MODIFIER", "usryfa"); // 수정자

				commDao.insert(jrParam, insWthrFcst, logId, methodNm, "기상청 초단기예보 등록");
			}

			commUtils.printLog(logId, methodNm, "S-");

		} catch (IOException e) {
			commUtils.printLog(logId, methodNm, "기상청 초단기예보 조회 Error : " + e.getMessage());
		} catch (ParserConfigurationException e) {
			commUtils.printLog(logId, methodNm, "기상청 초단기예보 조회 Error : " + e.getMessage());
		} catch (JDTOException e) {
			commUtils.printLog(logId, methodNm, "기상청 초단기예보 조회 Error : " + e.getMessage());
		} catch (SAXException e) {
			commUtils.printLog(logId, methodNm, "기상청 초단기예보 조회 Error : " + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					commUtils.printLog(logId, methodNm, "기상청 초단기예보 조회 Error : " + e.getMessage());
				}
			}
			if (con != null)
				con.disconnect();
		}
	}

	// DB에 기상청 데이터를 조회하여 있으면 PASS 하기 위해서 조회
	public String[] searchChk(JDTORecord rcvMsg) {
		String methodNm = "############ searchChk 시작 ##############[CondPredKmaSeEJBSBean.searchChk] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String[] weather_Cnt = { "0", "0", "0", "0", "0", "0" };
			String reqDate = commUtils.trim(rcvMsg.getFieldString("reqDate"));
			String reqHour = commUtils.trim(rcvMsg.getFieldString("reqHour"));

			JDTORecord jrParam = commUtils.getParam("", methodNm, "");
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			jrParam.setField("MESTM", reqDate + reqHour + "00");

			JDTORecordSet jsWthrFcstCntList = commDao.select(jrParam, selWthrFcstCntList, logId, methodNm, "기상청 예보 데이터 수량 조회");

			if (jsWthrFcstCntList.size() > 0) {
				int li = jsWthrFcstCntList.size() - 1;
				JDTORecord lastRow = jsWthrFcstCntList.getRecord(li);
				weather_Cnt[0] = lastRow.getFieldString("VF1") != null ? lastRow.getFieldString("VF1") : "0";
				weather_Cnt[1] = lastRow.getFieldString("VF2") != null ? lastRow.getFieldString("VF2") : "0";
				weather_Cnt[2] = lastRow.getFieldString("UN1") != null ? lastRow.getFieldString("UN1") : "0";
				weather_Cnt[3] = lastRow.getFieldString("UN2") != null ? lastRow.getFieldString("UN2") : "0";
				weather_Cnt[4] = lastRow.getFieldString("UF1") != null ? lastRow.getFieldString("UF1") : "0";
				weather_Cnt[5] = lastRow.getFieldString("UF2") != null ? lastRow.getFieldString("UF2") : "0";
			}
			
			commUtils.printParam(logId + " CondPredKmaSeEJBSBean.searchChk", weather_Cnt);

			return weather_Cnt;
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return new String[] { "0", "0", "0", "0", "0", "0" };
		}
	}
	
	public JDTORecord getServiceKey(JDTORecord rcvMsg) {
		String methodNm = "CondPredKmaSeEJBSBean.getServiceKey < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = commUtils.getParam("", methodNm, "");
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			JDTORecordSet jsList = commDao.select(jrParam, selApisServiceKey, logId, methodNm, "열연 결로 예측 공공데이터포털 서비스키 조회");
			
			commUtils.printParam(logId + " CondPredKmaSeEJBSBean.getServiceKey", jsList);

			String servicekey = "";
			JDTORecord row = JDTORecordFactory.getInstance().create();
			
			if (jsList.size() > 0) {
				row = jsList.getRecord(0);
				servicekey = row.getFieldString("SERVICEKEY");
			} else {
				throw new Exception("서비스 키 조회에 실패 하였습니다.");
			}
			
			if (servicekey == null || servicekey.length() == 0) {
				throw new Exception("서비스 키가 존재하지 않습니다.");
			}
			
			return row;
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			JDTORecord row = JDTORecordFactory.getInstance().create();
			return row;
		}
	}
}
