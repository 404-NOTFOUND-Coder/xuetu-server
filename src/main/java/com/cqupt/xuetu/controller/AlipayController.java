package com.cqupt.xuetu.controller;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.cqupt.xuetu.config.AliPayConfig;
import com.cqupt.xuetu.controller.dto.AliPay;
import com.cqupt.xuetu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/alipay")
public class AlipayController {
    // uuid
    public static String uuid;
    // 请求网关  固定
    private static final String GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    //签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Autowired
    private UserService userService;

    @Resource
    private AliPayConfig aliPayConfig;

    /**
     * 支付接口
     *
     * @param aliPay 对象
     * @return form表单
     */
    @PostMapping("/pay")
    public String pay(AliPay aliPay) {
        // &subject=xxx&traceNo=xxx&totalAmount=xxx
        AlipayTradePagePayResponse response;
        try {
            uuid = aliPay.getUuid();
            //  发起API调用（以创建当面付收款二维码为例）
            response = Factory.Payment.Page()
                    .pay(aliPay.getSubject(), aliPay.getTraceNo(), aliPay.getTotalAmount(), "http://localhost:8080/");
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        return response.getBody();
    }

    /**
     * 支付接口
     *
     * @param request http请求返回的订单信息
     * @return success
     */
    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }

            String tradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");

            // 支付宝验签
            if (Factory.Payment.Common().verifyNotify(params)) {
                Map<String, String> params1 = new HashMap<String, String>();
                params1.put("out_trade_no", params.get("out_trade_no"));
                params1.put("subject", params.get("subject"));
                params1.put("buyer_id", uuid);
                params1.put("total_amount", params.get("total_amount"));
                params1.put("gmt_create", params.get("gmt_create"));
                params1.put("gmt_payment", params.get("gmt_payment"));
                params1.put("trade_status", params.get("trade_status"));
                userService.updateData("orderlist", params.get("out_trade_no"), "out_trade_no", params1);
            }
        }
        return "success";
    }
}

