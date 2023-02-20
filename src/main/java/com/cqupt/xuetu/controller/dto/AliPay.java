package com.cqupt.xuetu.controller.dto;

import lombok.Data;

@Data
public class AliPay {
    private String subject;
    private String traceNo;
    private String totalAmount;
    private String uuid;

    public String getTraceNo() {
        return traceNo;
    }

    public void getTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }
}



