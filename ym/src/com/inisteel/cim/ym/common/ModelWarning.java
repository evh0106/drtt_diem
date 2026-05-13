/**
 * 
 * @(#)ModelWarning
 * 
 * @version    :
 * @author     : HanDong Data Systems
 * @date       : 2005. 7. 20
 *
 * @description :
 * 
 */
package com.inisteel.cim.ym.common;

import java.util.HashMap;
import java.util.Map;

import com.inisteel.cim.common.jms.model.CommonModel;

public class ModelWarning {
    private Map modelWarning 		= new HashMap();
    private static ModelWarning warn= new ModelWarning();
    
    private ModelWarning() {}
    
    public static ModelWarning getInstance() {
        return warn;
    }
    
    /**
     * 내부모델 에러 메시지를 저장한다.
     * @param model		내부모델
     * @param errMsg	에러내용
     */
    public void setWarning(CommonModel model, String errMsg) {
        modelWarning.put(model.getTcCode()+model.getTcDate()+model.getTcTime(), errMsg);
    }
    
    /**
     * 내부모델 에러 메시지를 리턴한다.
     * @param model		내부모델
     */
    public String getWarning(CommonModel model) {
        return (String)modelWarning.get(model.getTcCode()+model.getTcDate()+model.getTcTime());
    }  
    
    /**
     * 내부모델의 사이즈를 리턴한다.
     * @return
     */
    public int size() {
        return modelWarning.size();
    }
    
    /**
     * 내부모델을 삭제한다.
     * @param model		내부모델
     */
    public void deleteWarning(CommonModel model) {
        modelWarning.remove(model.getTcCode()+model.getTcDate()+model.getTcTime());
    }
}
