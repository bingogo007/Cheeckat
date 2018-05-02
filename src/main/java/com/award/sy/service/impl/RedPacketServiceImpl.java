package com.award.sy.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.award.core.beans.WherePrams;
import com.award.core.sql.where.C;
import com.award.sy.common.Constants;
import com.award.sy.common.DateUtil;
import com.award.sy.dao.RedPacketDao;
import com.award.sy.dao.WalletDao;
import com.award.sy.dao.WalletLogDao;
import com.award.sy.dao.WalletRecordDao;
import com.award.sy.entity.RedPacket;
import com.award.sy.entity.Wallet;
import com.award.sy.entity.WalletRecord;
import com.award.sy.service.RedPacketService;

/**
 * 红包service实现类
 * @ClassName:       RedPacketServiceImpl
 * @Description:     TODO
 * @author:          w77996
 * @date:            2018年4月25日        上午9:58:47
 */
@Service("redPacketService")
public class RedPacketServiceImpl implements RedPacketService {

	@Autowired
	private RedPacketDao redPacketDao;
	

	/**
	 * 通过Id获取红包
	 * Title: getRedPacketById
	 * Description: 
	 * @param redpacketId
	 * @return
	 * @see com.award.sy.service.RedPacketService#getRedPacketById(long)
	 */
	@Override
	public RedPacket getRedPacketById(long redpacketId) {
		return redPacketDao.get(redpacketId);
	}



    /**
     * 修改红包领取状态
     * @param record_sn
     * @param accept_id
     * @param fech_status
     * @return
     */
	@Override
	public boolean editRedPacketFetchStatus(String record_sn,long accept_id, int fech_status) {
		WherePrams where = new WherePrams();
		where.and("record_sn",C.EQ,record_sn);
		RedPacket redPacket = new RedPacket();
		redPacket.setAccept_id(accept_id);
		redPacket.setAccept_time(DateUtil.getNowTime());
		redPacket.setStatus(fech_status);
		int i = redPacketDao.updateLocal(redPacket,where);
		return 0 < i;
	}

	/**
	 * 生成一个红包记录
	 * @param record_sn
	 * @param userId
	 * @param money
	 * @return
	 */
	@Override
	public boolean addRedpacketRecord(String record_sn, String userId, String money,String to,String to_id) {
		RedPacket redPacket = new RedPacket();
		redPacket.setPublish_id(Long.parseLong(userId));
		redPacket.setRecord_sn(record_sn);
		redPacket.setMoney(Double.parseDouble(money));
		redPacket.setCreate_time(DateUtil.getNowTime());
		redPacket.setTo_type(Integer.parseInt(to));
		redPacket.setTo_id(Long.parseLong(to_id));
		int i = redPacketDao.addLocal(redPacket);
		return 0 < i;
	}

    @Override
    public List<Map<String, Object>> getRedPacketAndUserInfoByRedPacketId(String redpacketId) {
	    List<Map<String,Object>> list = redPacketDao.listBySql("select * from tb_red_packet where redpacket_id = "+redpacketId);
        return list;
    }

	/**
	 * 通过recordSn获取红包信息
	 * @param out_trade_no
	 * @return
	 */
	@Override
	public RedPacket getRedPacketByRecordSN(String out_trade_no) {
		WherePrams where = new WherePrams();
		where.and("record_sn",C.EQ,out_trade_no);
		return redPacketDao.get(where);
	}

	/**
	 * 修改红包支付状态
	 * @param record_sn
	 * @param pay_status
	 * @return
	 */
	@Override
	public int editRedPacketPayStatus(String record_sn, int pay_status) {
		WherePrams where = new WherePrams();
		where.and("record_sn",C.EQ,record_sn);
		RedPacket redPacket = new RedPacket();
		redPacket.setPay_status(pay_status);
		return redPacketDao.updateLocal(redPacket,where);
	}

	@Override
	public List<RedPacket> getExpiredRedPacket() {
		WherePrams where = new WherePrams();
		where.and("pay_status",C.EQ,Constants.PAY_STATUS_SUCCESS);
		where.and("status",C.EQ,0);
		where.and("create_time",C.IXAO,DateUtil.getDayBeginDate(System.currentTimeMillis()));
		return redPacketDao.list(where);
	}


}
