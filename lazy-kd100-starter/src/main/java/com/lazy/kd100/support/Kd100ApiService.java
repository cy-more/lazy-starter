package com.lazy.kd100.support;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kuaidi100.sdk.api.AutoNum;
import com.kuaidi100.sdk.api.QueryTrack;
import com.kuaidi100.sdk.api.Subscribe;
import com.kuaidi100.sdk.contant.ApiInfoConstant;
import com.kuaidi100.sdk.contant.CompanyConstant;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.request.*;
import com.kuaidi100.sdk.utils.SignUtils;
import com.lazy.kd100.common.Kd100BizException;
import com.lazy.kd100.common.Kd100Constants;
import com.lazy.kd100.config.Kd100ApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;

import java.util.regex.Pattern;

/**
 * @author ：cy
 * @description：快递100-api
 * @date ：2022/4/13 10:10
 */
@Slf4j
public class Kd100ApiService {

    static final String WARN_MESSAGE_PREFIX = "warnMessage:";

    final Pattern pattern = Pattern.compile("^[A-Za-z0-9]{10,20}$");

    Kd100ApiProperties kd100ApiProperties;

    Kd100Handler kd100Handler;

    public Kd100ApiService(Kd100ApiProperties kd100ApiProperties, Kd100Handler kd100Handler) {
        this.kd100ApiProperties = kd100ApiProperties;
        this.kd100Handler = kd100Handler;
    }

    /**
     * 查询物流轨迹
     */
    public JSONObject queryTrack(String logisticsNo){
        if (StringUtils.isBlank(logisticsNo) || !pattern.matcher(logisticsNo).matches()){
            return new JSONObject();
        }

        QueryTrackParam queryTrackParam = new QueryTrackParam();
        queryTrackParam.setCom(getCompanyCode(logisticsNo));
        queryTrackParam.setNum(logisticsNo);
//        queryTrackParam.setResultv2("4");
        String param = JSON.toJSONString(queryTrackParam);

        String post = post(param);
        return JSONObject.parseObject(post);
    }

    private String post(String param){
        QueryTrackReq queryTrackReq = new QueryTrackReq();
        queryTrackReq.setParam(param);
        queryTrackReq.setCustomer(kd100ApiProperties.getCustomer());
        queryTrackReq.setSign(SignUtils.querySign(param, kd100ApiProperties.getKey(), kd100ApiProperties.getCustomer()));

        IBaseClient baseClient = new QueryTrack();
        HttpResult result;
        try {
            log.info("快递100实时查询，param:" + queryTrackReq.getParam());
            result = baseClient.execute(queryTrackReq);
            log.info("快递100实时查询，msg:" + JSON.toJSONString(result));
        } catch (Exception e) {
            log.error(WARN_MESSAGE_PREFIX + e.getMessage(), e);
            throw new Kd100BizException("快递100接口异常:" + e.getMessage());
        }
        return result.getBody();
    }

    /**
     * 推送加密，用于验签
     * warn:没达到效果
     * @param sign
     * @param param
     * @param logisticsNo
     */
    public void checkSign(String sign, String param, String logisticsNo){
        String salt = logisticsNo + kd100ApiProperties.getSaltKey();
        if(!StringUtils.equalsIgnoreCase(sign, SignUtils.sign(param + salt))) {
            throw new Kd100BizException("验签不通过");
        }
    }

    /**
     * 快递单订阅
     * @param logisticsNo
     */
    public void deliveryPoll(String logisticsNo, String phone, String callbackUrl){
        if (StringUtils.isBlank(logisticsNo) || !pattern.matcher(logisticsNo).matches()){
            return;
        }

        //订阅
        SubscribeParameters subscribeParameters = new SubscribeParameters();
        subscribeParameters.setCallbackurl(callbackUrl);
        subscribeParameters.setPhone(phone);
        subscribeParameters.setSalt(logisticsNo + kd100ApiProperties.getSaltKey());
//        subscribeParameters.setResultv2("4");

        SubscribeParam subscribeParam = new SubscribeParam();
        subscribeParam.setParameters(subscribeParameters);
        subscribeParam.setCompany(getCompanyCode(logisticsNo));
        subscribeParam.setNumber(logisticsNo);
        subscribeParam.setKey(kd100ApiProperties.getKey());

        SubscribeReq subscribeReq = new SubscribeReq();
        subscribeReq.setSchema(ApiInfoConstant.SUBSCRIBE_SCHEMA);
        subscribeReq.setParam(JSON.toJSONString(subscribeParam));

        IBaseClient subscribe = new Subscribe();
        HttpResult httpResult;
        try {
            log.info("快递100订阅发送，param:" + subscribeReq.getParam());
            httpResult = subscribe.execute(subscribeReq);
            log.info("快递100订阅响应，msg:" + JSON.toJSONString(httpResult));
        } catch (Exception e) {
            log.error(WARN_MESSAGE_PREFIX + e.getMessage(), e);
            throw new Kd100BizException("快递100接口异常：订阅失败，error:" + e.getMessage());
        }
        JSONObject body = JSONObject.parseObject(httpResult.getBody());
        if (!body.getBoolean("result")){
            String returnCode = body.getString("returnCode");
            //重复订阅
            if (Kd100Constants.RESULT_CODE_REPEAT.equals(returnCode)){
                log.warn(body.getString("message"));
            }else{
                throw new Kd100BizException(body.getString("message"));
            }
        }
    }

    /**
     * 查询快递公司
     * @param logisticsNo
     * @return
     */
    public String getCompanyCode(String logisticsNo){
        //kd100快递公司编码
        String comCode = null;
        //如果SF开头则判断为顺丰快递
        if (logisticsNo.startsWith("SF")){
            comCode = CompanyConstant.SF;
        }else {
            //查询智能识别
            AutoNumReq autoNumReq = new AutoNumReq();
            autoNumReq.setKey(kd100ApiProperties.getKey());
            autoNumReq.setNum(logisticsNo);
            IBaseClient baseClient = new AutoNum();
            try {
                JSONArray result = JSONArray.parseArray(baseClient.execute(autoNumReq).getBody());
                if(ObjectUtil.isNotEmpty(result)) {
                    comCode = result
                            .getJSONObject(0)
                            .getString("comCode");
                }
            } catch (Exception e) {
                log.error(WARN_MESSAGE_PREFIX + e.getMessage(), e);
                throw new Kd100BizException(String.format("快递100接口异常：智能识别失败,logisticsNo:[%s]，error:[%s]", logisticsNo, e.getMessage()));
            }
        }
        return comCode;
    }

    public void deliveryPollByKey(String logisticsNo, String phone, String key){
        deliveryPoll(logisticsNo, phone, kd100Handler.getPushURI(key));
    }

    @Async
    public void deliveryPollByKeyAsync(String logisticsNo, String phone, String key){
        deliveryPollByKey(logisticsNo, phone, key);
    }
}
