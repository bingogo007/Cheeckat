package com.award.sy.entity;

import com.award.core.annotation.ID;
import com.award.core.annotation.TableName;
import com.award.core.annotation.TempField;
import com.award.core.beans.Po;

@TableName(name="tb_mission")
public class Mission extends Po {
	
	@ID
	private Long   mission_id;					//任务ID
	
	@TempField
	private String missionIdStr;				
	
	private String content;			//维度
	
    private Integer type;				//任务类型
	
	private Integer sex;          //限制接受者性别1是男，2是女，3是不限
	
	private String address;          //地址
	
	private Double money;                 //任务金额
	
	private Long publish_id;               //任务发布者
	
	private String create_time;   //任务发布时间
	
	private String start_time; //任务开始时间
	
	private Long accept_id;     //任务接收者，可以为空
	
	private Integer  status;    //任务状态，0是待领取，1是进行中，2是接受者确认完成，等待对方确认，3是已完成，4是已失效  5待支付
	
	private String accept_time;
	
	private String finish_time;
	
	private Integer anonymous;
	
	private String record_sn;
	
	private Long to_id;
	
	private Integer to_type;
	
	public Mission() {
		super();
	}
	

	public Long getMission_id() {
		return mission_id;
	}


	public void setMission_id(Long mission_id) {
		this.missionIdStr = mission_id.toString();
		this.mission_id = mission_id;
	}
    

	public String getMissionIdStr() {
		return missionIdStr;
	}


	public void setMissionIdStr(String missionIdStr) {
		this.missionIdStr = missionIdStr;
	}
	

	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public Integer getType() {
		return type;
	}


	public void setType(Integer type) {
		this.type = type;
	}
	
	public Integer getSex() {
		return sex;
	}


	public void setSex(Integer sex) {
		this.sex = sex;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public Double getMoney() {
		return money;
	}


	public void setMoney(Double money) {
		this.money = money;
	}


	public Long getPublish_id() {
		return publish_id;
	}


	public void setPublish_id(Long publish_id) {
		this.publish_id = publish_id;
	}


	public String getCreate_time() {
		return create_time;
	}


	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}


	public String getStart_time() {
		return start_time;
	}


	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}


	public Long getAccept_id() {
		return accept_id;
	}


	public void setAccept_id(Long accept_id) {
		this.accept_id = accept_id;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public String getAccept_time() {
		return accept_time;
	}


	public void setAccept_time(String accept_time) {
		this.accept_time = accept_time;
	}


	public String getFinish_time() {
		return finish_time;
	}


	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}


	public Integer getAnonymous() {
		return anonymous;
	}


	public void setAnonymous(Integer anonymous) {
		this.anonymous = anonymous;
	}

	public Long getTo_id() {
		return to_id;
	}


	public void setTo_id(Long to_id) {
		this.to_id = to_id;
	}


	public String getRecord_sn() {
		return record_sn;
	}


	public void setRecord_sn(String record_sn) {
		this.record_sn = record_sn;
	}


	public Integer getTo_type() {
		return to_type;
	}

	public void setTo_type(Integer to_type) {
		this.to_type = to_type;
	}

	@Override
	public String toString() {
		return "Mission [mission_id=" + mission_id + ", missionIdStr=" + missionIdStr
				+ ", content=" + content + ", type=" + type
				+ ", sex=" + sex + ", address=" + address
				+ ", money=" + money + ", publish_id=" + publish_id
				+ ", create_time=" + create_time + ", start_time=" + start_time
				+ ", accept_id=" + accept_id + ", status=" + status
				+ ", accept_time=" + accept_time + ", finish_time=" + finish_time 
				+ ", anonymous=" + anonymous + ", to_id=" + to_id
				+ ", record_sn=" + record_sn + ", to=" + to_type
				+ "]";
	}
	
}