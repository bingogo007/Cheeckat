package com.award.sy.entity;

import java.math.BigDecimal;

import com.award.core.annotation.ID;
import com.award.core.annotation.TableName;
import com.award.core.annotation.TempField;
import com.award.core.beans.Po;

/**
 * 钱包记录
 * @ClassName:       WalletRecord
 * @Description:     TODO
 * @author:          w77996
 * @date:            2018年4月24日        上午9:20:05
 */
@TableName(name = "tb_wallet_record")
public class WalletRecord extends Po{
	
	@ID
	private Long   wallet_record_id;//ID
	
	@TempField
	private String walletRecordIdStr;
	
	private String record_sn;//订单号
	
	private Long to_uid;//用户ID
	
	private Long from_uid;
	
	private Integer type;//交易类型
	
	private Double money;//交易金额
	
	private Integer pay_type;//支付方式
	
	private Integer pay_status;//支付状态
	
	private String pay_time;//交易时间
	
	private Integer fetch_status;//收款状态
	
	private String fetch_time;//收款时间

	public Long getWallet_record_id() {
		return wallet_record_id;
	}

	public void setWallet_record_id(Long wallet_record_id) {
		this.wallet_record_id = wallet_record_id;
	}

	public String getLongIdStr() {
		return walletRecordIdStr;
	}

	public void setLongIdStr(String walletRecordIdStr) {
		this.walletRecordIdStr = walletRecordIdStr;
	}

	public String getRecord_sn() {
		return record_sn;
	}

	public void setRecord_sn(String record_sn) {
		this.record_sn = record_sn;
	}

	public Long getTo_uid() {
		return to_uid;
	}

	public void setTo_uid(Long to_uid) {
		this.to_uid = to_uid;
	}

	public Long getFrom_uid() {
		return from_uid;
	}

	public void setFrom_uid(Long from_uid) {
		this.from_uid = from_uid;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Integer getPay_type() {
		return pay_type;
	}

	public void setPay_type(Integer pay_type) {
		this.pay_type = pay_type;
	}

	public Integer getPay_status() {
		return pay_status;
	}

	public void setPay_status(Integer pay_status) {
		this.pay_status = pay_status;
	}

	public String getPay_time() {
		return pay_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public Integer getFetch_status() {
		return fetch_status;
	}

	public void setFetch_status(Integer fetch_status) {
		this.fetch_status = fetch_status;
	}

	public String getFetch_time() {
		return fetch_time;
	}

	public void setFetch_time(String fetch_time) {
		this.fetch_time = fetch_time;
	}

	@Override
	public String toString() {
		return "WalletRecord [wallet_record_id=" + wallet_record_id
				+ ", walletRecordIdStr=" + walletRecordIdStr + ", record_sn=" + record_sn
				+ ", to_uid=" + to_uid + ", from_uid=" + from_uid + ", type="
				+ type + ", money=" + money + ", pay_type=" + pay_type
				+ ", pay_status=" + pay_status + ", pay_time=" + pay_time
				+ ", fetch_status=" + fetch_status + ", fetch_time="
				+ fetch_time + "]";
	}
	
	
	

}
