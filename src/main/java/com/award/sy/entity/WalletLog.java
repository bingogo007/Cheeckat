package com.award.sy.entity;

import java.math.BigDecimal;

import com.award.core.annotation.ID;
import com.award.core.annotation.TableName;
import com.award.core.annotation.TempField;
import com.award.core.beans.Po;
/**
 * 钱包日志
 * @ClassName:       WalletLog
 * @Description:     TODO
 * @author:          w77996
 * @date:            2018年4月24日        上午9:18:19
 */
@TableName(name="tb_wallet_log")
public class WalletLog extends Po{


	@ID
	private Long   log_id;//ID
	
	@TempField
	private String longIdStr;				
	
	private String record_sn;//订单号
	
	private Long user_id;//用户ID
	
	private BigDecimal change_money;//变动金额
	
	private BigDecimal money;//变动后的金额
	
	private String remark;//备注
	
	private String create_time;//创建时间

	public Long getLog_id() {
		return log_id;
	}

	public void setLog_id(Long log_id) {
		this.log_id = log_id;
	}

	public String getLongIdStr() {
		return longIdStr;
	}

	public void setLongIdStr(String longIdStr) {
		this.longIdStr = longIdStr;
	}

	public String getRecord_sn() {
		return record_sn;
	}

	public void setRecord_sn(String record_sn) {
		this.record_sn = record_sn;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public BigDecimal getChange_money() {
		return change_money;
	}

	public void setChange_money(BigDecimal change_money) {
		this.change_money = change_money;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	@Override
	public String toString() {
		return "WalletLog [log_id=" + log_id + ", longIdStr=" + longIdStr
				+ ", record_sn=" + record_sn + ", user_id=" + user_id
				+ ", change_money=" + change_money + ", money=" + money
				+ ", remark=" + remark + ", create_time=" + create_time + "]";
	}
	
	
	
}
