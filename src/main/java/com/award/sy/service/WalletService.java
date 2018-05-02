package com.award.sy.service;

import java.math.BigDecimal;

import com.award.sy.entity.Wallet;
/**
 * 用户钱包借口
 * @ClassName:       WalletService
 * @Description:     TODO
 * @author:          w77996
 * @date:            2018年4月25日        下午1:22:32
 */
public interface WalletService {
	/**
	 * 修改用户钱包余额,支付状态
	 * @Title:           editUserWalletBalance
	 * @Description:     TODO
	 * @param:           @param user_id
	 * @param:           @param money
	 * @param:           @return   
	 * @return:          boolean   
	 * @throws
	 */
	boolean editUserWalletPayBalance(String record_sn,long user_id,int log_type,Double changemoney,Double money);
	/**
	 * 修改用户钱包余额,领取状态
	 * @Title:           editUserWalletBalance
	 * @Description:     TODO
	 * @param:           @param user_id
	 * @param:           @param money
	 * @param:           @return
	 * @return:          boolean
	 * @throws
	 */
	boolean editUserWalletFetchBalance(String record_sn,long user_id,int log_type,Double changemoney,Double money);
	/**
	 * 通过ID查找用户钱包数据
	 * @Title:           findWalletByUserId
	 * @Description:     TODO
	 * @param:           @param u_id
	 * @param:           @return   
	 * @return:          Wallet   
	 * @throws
	 */
	Wallet findWalletByUserId(long u_id);

	/**
	 * 提现申请
	 * @Title:           withdrawMoney
	 * @Description:     TODO
	 * @param:           @param userId
	 * @param:           @param price
	 * @param:           @return   
	 * @return:          boolean   
	 * @throws
	 */
	boolean withdrawMoney(long userId, Double price,Wallet wallet);
	
	boolean refund(String record_sn,long user_id,int log_type,Double changemoney,Double money);

	/**
	 * 添加新用户
	 * @param user_id
	 */
	int addNewUser(Long user_id);
}
