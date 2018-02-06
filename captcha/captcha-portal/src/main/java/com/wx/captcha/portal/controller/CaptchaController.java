package com.wx.captcha.portal.controller;

import com.alibaba.fastjson.JSON;
import com.wx.captcha.common.ResultSupport;
import com.wx.captcha.data.mongoentity.AccessAppConf;
import com.wx.captcha.data.service.AccessAppConfService;
import com.wx.captcha.data.service.GeneralCacheService;
import com.wx.captcha.portal.config.CaptchaPortalConfig;
import com.wx.captcha.portal.constants.CaptchaResultStatus;
import com.wx.captcha.portal.domain.CaptchaCode;
import com.wx.captcha.portal.domain.CaptchaCodeStatus;
import com.wx.captcha.portal.javabean.*;
import com.wx.captcha.render.service.CaptchaRenderService;
import com.wx.captcha.utils.UUIDUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.wx.captcha.portal.constants.CaptchaResultStatus.*;

/**
 * 验证码服务
 *
 * @author xinquan.huangxq
 */
@Slf4j
@RequestMapping("/gateway")
@Controller
public class CaptchaController {

    @Autowired
    private AccessAppConfService accessAppConfService;

    @Autowired
    private GeneralCacheService generalCacheService;

    @Autowired
    private CaptchaPortalConfig captchaPortalConfig;

    @Autowired
    private CaptchaRenderService captchaRenderService;

    /**
     * 预初始化
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/pretreated", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResultSupport<PreTreatedResponse> pretreated(PreTreatedRequest request) {
        if (!captchaPortalConfig.getCaptchaPretreatedLimiter()
                .tryAcquire(captchaPortalConfig.getCaptchaPretreatedTimeout(), TimeUnit.MICROSECONDS)) {
            if (log.isWarnEnabled()) {
                log.warn("验证码预初始化超过系统负载，限流...");
            }

        }
        // 校验请求参数（签名信息）
        CaptchaResultStatus status = checkRequest(request);
        if (!status.success()) {
            return ResultSupport.newResult(status);
        }

        // 验证码请求标示
        String challengeId = UUIDUtil.randomUUIDWithoutSplit();

        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCaptchaId(request.getCaptchaId());
        captchaCode.setChallengeId(challengeId);
        captchaCode.setStatus(CaptchaCodeStatus.NEW);

        // 存储在缓存中
        generalCacheService.put(challengeId, JSON.toJSONString(captchaCode), captchaPortalConfig.getCaptchaAvailableTimeout());

        PreTreatedResponse response = new PreTreatedResponse();
        response.setCaptchaId(request.getCaptchaId());
        response.setChallengeId(challengeId);

        return ResultSupport.newSuccessResult(response);
    }


    /**
     * 第一次校验
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/validate/first", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String validate(HttpServletRequest httpServletRequest, FirstValidateRequest request) {
        // 校验请求参数（签名信息）
        CaptchaResultStatus status = checkRequest(request);
        if (!status.success()) {
            return jsonp(httpServletRequest, ResultSupport.newResult(status));
        }

        String challengeId = request.getChallengeId();
        if (StringUtils.isEmpty(challengeId)) {
            return jsonp(httpServletRequest, ResultSupport.newResult(CHALLENGE_ID_CANNOT_BE_EMPTY));
        }

        // 获取验证码信息
        String json = generalCacheService.get(challengeId);
        if (StringUtils.isEmpty(json)) {
            return jsonp(httpServletRequest, ResultSupport.newResult(CHALLENGE_ID_NOT_EXIST));
        }

        // 验证码状态必须是show状态才能进行一次校验
        CaptchaCode captchaCode = JSON.parseObject(json, CaptchaCode.class);
        if (captchaCode == null ||
                !challengeId.equals(captchaCode.getChallengeId()) ||
                !CaptchaCodeStatus.SHOW.equals(captchaCode.getStatus())) {
            return jsonp(httpServletRequest, ResultSupport.newResult(CHALLENGE_ILLEGAL));
        }

        FirstValidateResponse response = new FirstValidateResponse();
        response.setChallengeId(challengeId);

        // 校验用户轨迹
        if (checkTrajectory(request, captchaCode)) {
            response.setAccess(true);
            captchaCode.setStatus(CaptchaCodeStatus.VALID);
            generalCacheService.put(challengeId, JSON.toJSONString(captchaCode), captchaPortalConfig.getCaptchaAvailableTimeout());
        } else {
            response.setAccess(false);
        }

        return jsonp(httpServletRequest, ResultSupport.newSuccessResult(response));
    }

    /**
     * 第二次校验
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/validate/second", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResultSupport<SecondValidateResponse> validate(SecondValidateRequest request) {
        // 校验请求参数（签名信息）
        CaptchaResultStatus status = checkRequest(request);
        if (!SUCCESS.equals(status)) {
            return ResultSupport.newResult(status);
        }

        String challengeId = request.getChallengeId();
        if (StringUtils.isEmpty(challengeId)) {
            return ResultSupport.newResult(CHALLENGE_ID_CANNOT_BE_EMPTY);
        }

        // 获取验证码信息
        String json = generalCacheService.get(challengeId);
        if (StringUtils.isEmpty(json)) {
            return ResultSupport.newResult(CHALLENGE_ID_NOT_EXIST);
        }

        // 验证码状态必须是show状态才能进行一次校验
        CaptchaCode captchaCode = JSON.parseObject(json, CaptchaCode.class);
        if (captchaCode == null ||
                !challengeId.equals(captchaCode.getChallengeId()) ||
                !CaptchaCodeStatus.VALID.equals(captchaCode.getStatus())) {
            return ResultSupport.newResult(CHALLENGE_ILLEGAL);
        }

        // 校验成功后，清空校验码信息
        generalCacheService.invalid(challengeId);

        // 二次校验成功
        SecondValidateResponse response = new SecondValidateResponse();
        response.setAccess(true);

        return ResultSupport.newSuccessResult(response);
    }

    /**
     * 展示验证码
     *
     * @param challengeId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/show/{challengeId}")
    public void show(@PathVariable("challengeId") String challengeId, HttpServletResponse response) throws IOException {
        if (!captchaPortalConfig.getCaptchaRenderLimiter()
                .tryAcquire(captchaPortalConfig.getCaptchaAvailableTimeout(), TimeUnit.MICROSECONDS)) {
            if (log.isWarnEnabled()) {
                log.warn("请求验证码渲染超过负载，限流...");
            }
        }
        if (StringUtils.isEmpty(challengeId)) {
            if (log.isWarnEnabled()) {
                log.warn(CHALLENGE_ID_CANNOT_BE_EMPTY.errCode());
            }
            return;
        }
        String json = generalCacheService.get(challengeId);
        if (StringUtils.isEmpty(json)) {
            if (log.isWarnEnabled()) {
                log.warn(CHALLENGE_ID_NOT_EXIST.errCode());
            }
            return;
        }

        CaptchaCode captchaCode = JSON.parseObject(json, CaptchaCode.class);
        if (captchaCode == null ||
                !challengeId.equals(captchaCode.getChallengeId())) {
            if (log.isWarnEnabled()) {
                log.warn(CHALLENGE_ILLEGAL.errCode());
            }
            return;
        }

        captchaCode.setStatus(CaptchaCodeStatus.SHOW);
        // 更新缓存中验证码的状态
        generalCacheService.put(challengeId, JSON.toJSONString(captchaCode), captchaPortalConfig.getCaptchaAvailableTimeout());

        // 渲染图片
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        ServletOutputStream outputStream = response.getOutputStream();
        BufferedImage bufferedImage = (BufferedImage) captchaRenderService.getChallengeForID(challengeId);
        ImageIO.write(bufferedImage, "JPEG", outputStream);
        bufferedImage.flush();
        outputStream.flush();
    }

    /**
     * 展示验证码
     *
     * @param challengeId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/fresh/{challengeId}")
    public void fresh(@PathVariable("challengeId") String challengeId, HttpServletResponse response) throws IOException {
        if (StringUtils.isEmpty(challengeId)) {
            if (log.isWarnEnabled()) {
                log.warn(CHALLENGE_ID_CANNOT_BE_EMPTY.errCode());
            }
            return;
        }

        // 清理验证码
        captchaRenderService.fresh(challengeId);

        // 渲染验证码
        show(challengeId, response);
    }

    /**
     * 校验请求参数
     *
     * @param request
     * @return
     */
    private CaptchaResultStatus checkRequest(BaseRequest request) {
        Preconditions.checkNotNull(request);

        if (StringUtils.isEmpty(request.getCaptchaId())) {
            return CAPTCHA_ID_CANNOT_BE_EMPTY;
        }

        // 目前服务版本只支持v1
        if (!"v1".equals(request.getVersion())) {
            return VERSION_NOT_AVAILABLE;
        }

        // 获取接入网络应用的配置
        String captchaId = request.getCaptchaId();
        AccessAppConf accessAppConf = accessAppConfService.findById(captchaId);
        if (accessAppConf == null) {
            return CAPTCHA_ID_NOT_EXIST;
        }

        /* 压力测试暂时屏蔽验签

        // 验证签名
        // 一次校验是通过js sdk过来的请求，私钥其实是直接在js sdk里面写死的(JS-SDK)
        String secretKey = request instanceof FirstValidateRequest
                ? "JS-SDK" : accessAppConf.getSecretKey();
        String signature = MD5SignatureUtil.sign(secretKey, request);
        if (signature == null || !signature.equals(request.getSignature())) {
            return SIGNATURE_NOT_AVAILABLE;
        }

        */

        return SUCCESS;
    }

    /**
     * 转换jsonp代码片段
     *
     * @param request
     * @param resultSupport
     * @return
     */
    private static String jsonp(HttpServletRequest request, ResultSupport<?> resultSupport) {
        Preconditions.checkNotNull(request);

        String callback = request.getParameter("callback");
        if (StringUtils.isEmpty(callback)) {
            // 低版本jquery不带该请求
            callback = "jsonpCallback";
        }
        return String.format("%s(%s)", callback, JSON.toJSONString(resultSupport));
    }


    /**
     * 一次校验轨迹信息校验
     *
     * @param request
     * @param captchaCode
     * @return
     */
    private boolean checkTrajectory(FirstValidateRequest request, CaptchaCode captchaCode) {
        Preconditions.checkArgument(request != null && request.getTrajectory() != null && request.getTrajectory().length() > 5);

        String type = request.getTrajectory().substring(0, 3);
        if (!"000".equals(type)) {
            // 目前只支持类型000
            return false;
        }
        String trajectory = request.getTrajectory().substring(4);
        String[] pointStrArray = trajectory.split(";");
        Point.Double[] points = new Point.Double[pointStrArray.length];
        for (int i = 0; i < pointStrArray.length; ++i) {
            String pointStr = pointStrArray[i];
            int index = pointStr.indexOf(',');
            if (index == -1) {
                // 格式错误
                return false;
            }
            double x = Double.parseDouble(pointStr.substring(0, index));
            double y = Double.parseDouble(pointStr.substring(index + 1));
            points[i] = new Point.Double(x, y);
        }
        return captchaRenderService.validateResponseForID(captchaCode.getChallengeId(), points);
    }
}
