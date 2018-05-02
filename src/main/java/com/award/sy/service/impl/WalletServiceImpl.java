package com.award.sy.service.impl;

import java.math.BigDecimal;

import com.award.sy.dao.WalletLogDao;
import com.award.sy.entity.WalletLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.award.core.beans.WherePrams;
import com.award.core.sql.where.C;
import com.award.sy.common.Constants;
import com.award.sy.common.DateUtil;
import com.award.sy.common.PayCommonUtil;
import com.award.sy.dao.WalletDao;
import com.award.sy.dao.WalletRecordDao;
import com.award.sy.entity.RedPacket;
import com.award.sy.entity.Wallet;
import com.award.sy.entity.WalletRecord;
import com.award.sy.service.WalletRecordService;
import com.award.sy.service.WalletService;

/**
 * 钱包service借口实现类
 *
 * @ClassName: WalletServiceImpl
 * @Description: TODO
 * @author: w77996
 * @date: 2018年4月25日        下午1:24:52
 */
@Service("walletServiceImpl")
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private WalletRecordDao walletRecordDao;

    @Autowired
    private WalletLogDao walletLogDao;


    /**
     * 修改用户钱包余额,添加日志
     *
     * @param record_sn
     * @param user_id
     * @param log_type
     * @param changemoney
     * @param money
     * @return
     */
    @Transactional
    @Override
    public boolean editUserWalletPayBalance(String record_sn, long user_id, int log_type, Double changemoney, Double money) {

        String date = DateUtil.getNowTime();
        //更新支付的状态
        WherePrams where = new WherePrams();
        where.and("record_sn", C.EQ, record_sn);
        WalletRecord walletRecord = new WalletRecord();
        walletRecord.setPay_status(Constants.PAY_STATUS_SUCCESS);
        walletRecord.setPay_time(date);
        int x = walletRecordDao.updateLocal(walletRecord, where);
        //更新日志
        WalletLog walletLog = new WalletLog();
        walletLog.setRecord_sn(record_sn);
        walletLog.setUser_id(user_id);
        walletLog.setType(log_type);
        walletLog.setChange_money(changemoney);
        walletLog.setMoney(money);
        walletLog.setCreate_time(date);
        int j = walletLogDao.addLocal(walletLog);


        //更新金额
        int i = walletDao.excuse("update tb_wallet set money =" + money + " where user_id = " + user_id);

        if (0 < j && 0 < i && 0 < x) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param record_sn
     * @param user_id
     * @param type
     * @param changemoney
     * @param money
     * @return
     */
    @Override
    public boolean editUserWalletFetchBalance(String record_sn, long user_id, int type, Double changemoney, Double money) {
        String date = DateUtil.getNowTime();

        //更新收款的状态
        WherePrams where = new WherePrams();
        where.and("record_sn", C.EQ, record_sn);
        WalletRecord walletRecord = new WalletRecord();
        walletRecord.setTo_uid(user_id);
        walletRecord.setFetch_status(Constants.PAY_STATUS_SUCCESS);
        walletRecord.setFetch_time(date);
        int x = walletRecordDao.updateLocal(walletRecord, where);
        //更新日志
        WalletLog walletLog = new WalletLog();
        walletLog.setRecord_sn(record_sn);
        walletLog.setUser_id(user_id);
        walletLog.setType(type);
        walletLog.setChange_money(changemoney);
        walletLog.setMoney(money);
        walletLog.setCreate_time(date);
        int j = walletLogDao.addLocal(walletLog);
        //更新金额
        int i = walletDao.excuse("update tb_wallet set money =" + money + " where user_id = " + user_id);

        if (0 < j && 0 < i && 0 < x) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询用户钱包
     * Title: findWalletByUserId
     * Description:
     *
     * @param u_id
     * @return
     * @see com.award.sy.service.WalletService#findWalletByUserId(long)
     */
    @Override
    public Wallet findWalletByUserId(long u_id) {
        WherePrams where = new WherePrams();
        where.and("user_id", C.EQ, u_id);
        return walletDao.get(where);
    }


    /**
     * 申请提现
     * Title: withdrawMoney
     * Description:
     *
     * @param userId
     * @param price
     * @return
     * @see com.award.sy.service.WalletRecordService#(long, Double)
     */
    @Transactional
    @Override
    public boolean withdrawMoney(long userId, Double price, Wallet wallet) {
        // TODO Auto-generated method stub
        //生成订单编号
        String record_sn = PayCommonUtil.CreateNoncestr();
        //生成订单记录
        WalletRecord walletRecord = new WalletRecord();
        walletRecord.setFrom_uid(0L);
        walletRecord.setTo_uid(userId);
        walletRecord.setRecord_sn(record_sn);
        walletRecord.setType(Constants.ORDER_TYPE_WITHDRAWLS);
        walletRecord.setMoney(price);
        int i = walletRecordDao.addLocal(walletRecord);

        Double result = null;
        result = wallet.getMoney() - price;
        WherePrams where = new WherePrams();
        where.and("user_id", C.EQ, userId);
        int j = walletDao.update(wallet, where);
        return 0 < j;
    }
    
    @Transactional
    @Override
    public boolean refund(String record_sn,long user_id,int log_type,Double changemoney,Double money) {
    	 String date = DateUtil.getNowTime();
         //更新支付的状态
         WherePrams where = new WherePrams();
         where.and("record_sn", C.EQ, record_sn);
         WalletRecord walletRecord = new WalletRecord();
         walletRecord.setType(Constants.ORDER_TYPE_BACK);
         walletRecord.setFetch_time(date);
         walletRecord.setFetch_status(1);
         int x = walletRecordDao.updateLocal(walletRecord, where);
         //更新日志
         WalletLog walletLog = new WalletLog();
         walletLog.setRecord_sn(record_sn);
         walletLog.setUser_id(user_id);
         walletLog.setType(log_type);
         walletLog.setChange_money(changemoney);
         walletLog.setMoney(money);
         walletLog.setCreate_time(date);
         int j = walletLogDao.addLocal(walletLog);


         //更新金额
         int i = walletDao.excuse("update tb_wallet set money =" + money + " where user_id = " + user_id);

         if (0 < j && 0 < i && 0 < x) {
             return true;
         } else {
             return false;
         }
    }

    @Override
    public int addNewUser(Long user_id) {
        Wallet wallet = new Wallet();
        wallet.setUser_id(user_id);
        return walletDao.addLocal(wallet);
    }

}
