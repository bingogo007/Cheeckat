package com.award.sy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.award.sy.entity.WalletRecord;

/**
 * 钱包记录接口
 * @ClassName:       WalletRecordService
 * @Description:     TODO
 * @author:          w77996
 * @date:            2018年4月24日        下午2:16:26
 */
public interface WalletRecordService {

	/**
	 * 通过SN查找记录
	 * @Title:           getWallerOrderByRecordSN
	 * @Description:     TODO
	 * @param:           @param record_sn
	 * @param:           @return   
	 * @return:          WalletRecord   
	 * @throws
	 */
	WalletRecord getWallerOrderByRecordSN(String record_sn);

	/**
	 * 修改记录表支付状态
	 * @param record_sn 单号
	 * @param status 支付状态
	 * @return
	 */
	boolean editWalletOrderPayStatus(String record_sn, int status);

	/**
	 * 添加交易记录
	 * @param user_id
	 * @param money 金额
	 * @param pay_type 支付方式
	 * @param type 交易类型
	 * @return
	 */
	String addWalletRecordOrder(long user_id,String money,int pay_type,int type);


	boolean editUserWalletPayElse(String out_trade_no, long from_uid, int type, Double changemoney, Double money);
}
