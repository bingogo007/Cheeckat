package com.award.sy.service;

import java.util.List;
import java.util.Map;

public interface WalletLogService {
    /**
     * 插入余额变动log
     * @param record_sn
     * @param userId
     * @param type
     * @param changemoney
     * @param moeny
     * @return
     */
    int addWalletLog(String record_sn,long userId,int type,Double changemoney,Double moeny);

    /**
     * 获取余额变动明细
     * @param l
     * @return
     */
    List<Map<String,Object>> getWalletLogByUserId(long l);
}
