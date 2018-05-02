package com.award.sy.service.impl;

import com.award.core.beans.WherePrams;
import com.award.core.sql.where.C;
import com.award.sy.common.DateUtil;
import com.award.sy.dao.WalletDao;
import com.award.sy.dao.WalletLogDao;
import com.award.sy.entity.WalletLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.award.sy.service.WalletLogService;

import java.util.List;
import java.util.Map;

@Service("walletLogService")
public class WalletLogServiceImpl implements WalletLogService{


    @Autowired
    private WalletLogDao walletLogDao;

    /**
     * 插入余额变动log
     * @param record_sn 订单号
     * @param userId
     * @param type  交易类型 1充值 2提现 3发红包 4收红包 5发任务 6接任务 7退款
     * @param changemoney 变动金额 增+ 减-
     * @param moeny 变动后的金额
     * @return
     */
    @Override
    public int addWalletLog(String record_sn, long userId, int type, Double changemoney, Double moeny) {
        WalletLog walletLog = new WalletLog();
        walletLog.setRecord_sn(record_sn);
        walletLog.setUser_id(userId);
        walletLog.setChange_money(changemoney);
        walletLog.setMoney(moeny);
        walletLog.setCreate_time(DateUtil.getNowTime());
        return walletLogDao.addLocal(walletLog);
    }

    /**
     * 获取余额变动明细
     * @param l
     * @return
     */
    @Override
    public List<Map<String, Object>> getWalletLogByUserId(long l) {

        List<Map<String,Object>> list = walletLogDao.listBySql("select * from tb_wallet_log where user_id = "+l+"order by create_time desc");
        return list;
    }
}
