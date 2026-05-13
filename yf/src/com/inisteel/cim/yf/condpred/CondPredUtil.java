package com.inisteel.cim.yf.condpred;

import jspeed.base.record.JDTORecord;

import com.inisteel.cim.yf.common.YfCommUtils;

public class CondPredUtil {
	private YfCommUtils commUtils = new YfCommUtils();

	
	/**
	 * 수신 EAI전문 ID를 송신 EAI전문 ID로 변경
	 * @param JDTORecord
	 *            rcvMsg
	 * @return String
	 */
	public String getRtnMsgId(JDTORecord rcvMsg) {
		String sYD_GP = commUtils.trim(rcvMsg.getFieldString("YD_GP"));
		String msgId = commUtils.nvl(commUtils.getMsgId(rcvMsg), "");

		String rtnMsgId = msgId;

		if (msgId.length() >= 5 || sYD_GP.length() >= 1) {
			rtnMsgId = msgId.substring(2, 4) + msgId.substring(0, 2) + msgId.substring(4);
		}

		return rtnMsgId;
	}
	
	public String convRN1(String rainStr) {
		if (rainStr == null) return "0";
		
		rainStr = rainStr.trim();
		
		if (rainStr.equals("1mm 미만") || rainStr.equals("강수없음")) {
			return "0";
		} else if (rainStr.equals("50.0mm 이상")) {
			return "50.0";
		}
		
		// "mm" 제거
		if (rainStr.contains("mm")) {
			try{
				String numPartRn1 = rainStr.replace("mm", "").trim();
				
				if (numPartRn1.contains("~")) {
					String[] parts = numPartRn1.split("~");
					
					if(parts.length == 2) {
						double val1 = Double.parseDouble(parts[0].trim());
						double val2 = Double.parseDouble(parts[1].trim());
						double avg = (val1 + val2) / 2.0;
						
						return String.format("%.1f", avg);
					}
				}
				double numRn1 = Double.parseDouble(numPartRn1);
				return String.format("%.1f", numRn1);
			} catch (NumberFormatException e) {
				return "0";
			}
		}
		
		return rainStr;
	}
}
