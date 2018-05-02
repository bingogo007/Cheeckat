package com.award.sy.entity;

import java.math.BigDecimal;

import com.award.core.annotation.ID;
import com.award.core.annotation.TableName;
import com.award.core.annotation.TempField;
import com.award.core.beans.Po;

@TableName(name = "tb_red_packet")
public class RedPacket extends Po {


    @ID
    private Long redpacket_id;                    //任务ID

    @TempField
    private String redpacketIdStr;

    private String record_sn;//流水订单号

    private Integer type;                //任务类型

    private Double money;                 //金额

    private Long publish_id;               //发布者

    private String create_time;   //发布时间


    private Long accept_id;     //任务接收者，可以为空

    private Integer status;    //任务状态，0是待领取，1已领取，

    private String accept_time;

    private Integer to_type;

    private Long to_id;

    private Integer pay_status;


    public Long getRedpacket_id() {
        return redpacket_id;
    }

    public void setRedpacket_id(Long redpacket_id) {
        this.redpacket_id = redpacket_id;
    }

    public String getRedpacketIdStr() {
        return redpacketIdStr;
    }

    public void setRedpacketIdStr(String redpacketIdStr) {
        this.redpacketIdStr = redpacketIdStr;
    }

    public String getRecord_sn() {
        return record_sn;
    }

    public void setRecord_sn(String record_sn) {
        this.record_sn = record_sn;
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

    public Integer getTo_type() {
        return to_type;
    }

    public void setTo_type(Integer to_type) {
        this.to_type = to_type;
    }

    public Long getTo_id() {
        return to_id;
    }

    public void setTo_id(Long to_id) {
        this.to_id = to_id;
    }

    public Integer getPay_status() {
        return pay_status;
    }

    public void setPay_status(Integer pay_status) {
        this.pay_status = pay_status;
    }

    @Override
    public String toString() {
        return "RedPacket{" +
                "redpacket_id=" + redpacket_id +
                ", redpacketIdStr='" + redpacketIdStr + '\'' +
                ", record_sn='" + record_sn + '\'' +
                ", type=" + type +
                ", money=" + money +
                ", publish_id=" + publish_id +
                ", create_time='" + create_time + '\'' +
                ", accept_id=" + accept_id +
                ", status=" + status +
                ", accept_time='" + accept_time + '\'' +
                ", to_type=" + to_type +
                ", to_id=" + to_id +
                ", pay_status=" + pay_status +
                '}';
    }
}
