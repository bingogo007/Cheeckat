package com.award.sy.service.impl;

import com.award.sy.common.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.award.core.beans.WherePrams;
import com.award.core.sql.where.C;
import com.award.sy.common.Constants;
import com.award.sy.common.PayCommonUtil;
import com.award.sy.dao.WalletLogDao;
import com.award.sy.dao.WalletRecordDao;
import com.award.sy.entity.WalletLog;
import com.award.sy.entity.WalletRecord;
import com.award.sy.service.WalletRecordService;
@Service("walletRecordService")
public class WalletRecordServiceImpl implements WalletRecordService{

	@Autowired
	private WalletRecordDao walletRecordDao;
	
	@Autowired
	private WalletLogDao walletLogDao;

	/**
	 * 通过record_sn查找订单
	 * Title: getWallerOrderByRecordSN
	 * Description: 
	 * @param record_sn
	 * @return
	 * @see com.award.sy.service.WalletRecordService#getWallerOrderByRecordSN(java.lang.String)
	 */
	@Override
	public WalletRecord getWallerOrderByRecordSN(String record_sn) {
		WherePrams where = new WherePrams();
		where.and("record_sn", C.EQ, record_sn);

		return walletRecordDao.get(where);
	}

	/**
	 * 更新记录支付状态
	 * @param record_sn
	 * @param status
	 * @return
	 */
	@Override
	public boolean editWalletOrderPayStatus(String record_sn, int status) {
		WalletRecord walletRecord = new WalletRecord();
		WherePrams where = new WherePrams();
		where.and("record_sn", C.EQ, record_sn);
		walletRecord.setPay_status(status);
		walletRecord.setPay_time(DateUtil.getNowTime());
		int i = walletRecordDao.updateLocal(walletRecord,where);


		return 0 < i ;
	}


	/**
	 * 生成订单
	 * @param user_id
	 * @param money
	 * @param pay_type
	 * @param type
	 * @return
	 */
	@Override
	public String addWalletRecordOrder(long user_id, String money, int pay_type, int type) {
		String record_sn = PayCommonUtil.createOutTradeNo();
		//生成订单记录
		WalletRecord walletRecord = new WalletRecord();
		walletRecord.setFrom_uid(user_id);
		walletRecord.setRecord_sn(record_sn);
		walletRecord.setType(type);
		walletRecord.setPay_type(pay_type);
		walletRecord.setMoney(Double.parseDouble(money));
		int i = walletRecordDao.addLocal(walletRecord);
		if(0 < i){
			return record_sn;
		}
		return null;
	}

	@Override
	public boolean editUserWalletPayElse(String record_sn, long user_id, int log_type, Double changemoney, Double money) {
		String date = DateUtil.getNowTime();
		//更新支付的状态
		WherePrams where = new WherePrams();
		where.and("record_sn", C.EQ, record_sn);
		WalletRecord walletRecord = new WalletRecord();
		walletRecord.setPay_status(Constants.PAY_STATUS_SUCCESS);
		walletRecord.setPay_time(date);
		int i = walletRecordDao.updateLocal(walletRecord, where);
		//更新日志
		WalletLog walletLog = new WalletLog();
		walletLog.setRecord_sn(record_sn);
		walletLog.setUser_id(user_id);
		walletLog.setType(log_type);
		walletLog.setChange_money(changemoney);
		walletLog.setMoney(money);
		walletLog.setCreate_time(date);
		int j = walletLogDao.addLocal(walletLog);



		if (0 < j && 0 < i) {
			return true;
		} else {
			return false;
		}

	}


}
