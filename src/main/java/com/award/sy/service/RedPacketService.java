package com.award.sy.service;

import java.util.List;
import java.util.Map;

import com.award.sy.entity.RedPacket;
import com.award.sy.entity.Wallet;
/**
 * 红包service接口
 * @ClassName:       RedPacketService
 * @Description:     TODO
 * @author:          w77996
 * @date:            2018年4月25日        上午9:59:11
 */
public interface RedPacketService {
	/**
	 * 通过Id获取红包
	 * @Title:           getRedPacketById
	 * @Description:     TODO
	 * @param:           @param redpacketId
	 * @param:           @return   
	 * @return:          RedPacket   
	 * @throws
	 */
	RedPacket getRedPacketById(long redpacketId);


	 /**
	  * 更新领取状态
	  * @Title:           editRedPacket
	  * @Description:     TODO
	  * @param:           @param redPacket
	  * @return:          void
	  * @throws
	  */
	 boolean editRedPacketFetchStatus(String  record_sn,long accept_id, int fect_status);

	/**
	 * 生成一个红包
	 * @param record_sn
	 * @param userId
	 * @param money
	 * @return
	 */
	 boolean addRedpacketRecord(String record_sn, String userId, String money,String to,String to_id);

	/**
	 * 获取领取者信息及红包信息
	 * @param redpacketId
	 * @return
	 */
	List<Map<String,Object>> getRedPacketAndUserInfoByRedPacketId(String redpacketId);

	/**
	 * 通过recordSn获取红包信息
	 * @param out_trade_no
	 * @return
	 */
	RedPacket getRedPacketByRecordSN(String out_trade_no);

	/**
	 *修改红包支付状态
	 * @param record_sn
	 * @param pay_status
	 * @return
	 */
	int editRedPacketPayStatus(String record_sn, int pay_status);

	/**
	 * 获取失效的红包
	 * @return
	 */
	List<RedPacket> getExpiredRedPacket();
}
