package com.award.sy.web.openapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import com.award.sy.entity.WalletRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.award.core.util.JsonUtils;
import com.award.sy.common.Constants;
import com.award.sy.common.PayCommonUtil;
import com.award.sy.common.WxPayUtil;
import com.award.sy.entity.RedPacket;
import com.award.sy.entity.User;
import com.award.sy.entity.Wallet;
import com.award.sy.service.RedPacketService;
import com.award.sy.service.UserService;
import com.award.sy.service.WalletRecordService;
import com.award.sy.service.WalletService;

@Controller
public class RedPacketOpenController {

	@Autowired
	private UserService userService;

	@Autowired
	private WalletRecordService walletRecordService;

	@Autowired
	private RedPacketService redPacketService;
	
	@Autowired
	private WalletService walletService;

	/**
	 * 抢红包
	 * 
	 * @Title: getRedPacket
	 * @Description: TODO
	 * @param: @param user_id
	 * @param: @param redpacket_id
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	@RequestMapping(value = "/open/getRedPacket", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getRedPacket(@RequestParam String userId,
			@RequestParam String redpacketId) {
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(redpacketId)) {
			return returnStr;
		}



		// 查询领取的用户信息
		User user = userService.getUserById(Long.parseLong(userId));
		if (null == user) {
			return JsonUtils.writeJson(0, 4, "用户不存在");
		}
		// 查询红包信息
		RedPacket redPacket = redPacketService.getRedPacketById(Long
				.parseLong(redpacketId));
		if (null == redPacket) {
			return JsonUtils.writeJson(0, 16, "红包id不存在");
		}
		WalletRecord walletRecord = walletRecordService.findWallerOrderByRecordSN(redPacket.getRecord_sn());
		if(walletRecord.getMoney().compareTo(redPacket.getMoney()) !=0 ){
			return JsonUtils.writeJson(0, 18, "红包领取失败");
		}
		if (redPacket.getStatus() == Constants.FETCH_SUCCESS) {
			if(redPacket.getPublish_id() == Long.parseLong(userId)){
				//查询被领取人的信息
				//List<Map<String,Object>> list = redPacketService.findAcceptUserByRedpacketId(re)
				User acceptUser = userService.getUserById(redPacket.getAccept_id());
				Map<String,Object> map = new HashMap<>();
				map.put("user",acceptUser);
				map.put("redpacket",redPacket);
				return JsonUtils.writeJson(1, "请求成功", map, "object");
			}
			return JsonUtils.writeJson(0, 17, "红包被领取");
		}
		if(redPacket.getPublish_id() == Long.parseLong(userId)){
			return JsonUtils.writeJson(0, 36, "不能领取自己的红包");
		}

		redPacket.setAccept_id(Long.parseLong(userId));
		redPacket.setStatus(Constants.FETCH_SUCCESS);
		// redPacket.setAccept_time(DateUtil.getNowTime());
		/*
		 * if(!redPacketService.getRedPacket(redPacket)){ return
		 * JsonUtils.writeJson(0, 18, "红包领取失败"); }
		 */
		List<Map<String, Object>> list = redPacketService
				.editRedPacket(redPacket);
		if (null != list) {
			return JsonUtils.writeJson(1, "领取成功", list, "object");
		} else {
			return JsonUtils.writeJson(0, 18, "红包领取失败");
		}
		// List<Map<String, Object>> list =
		// redPacketService.getRedPacketById(redpacketId)

	}
	/**
	 * 支付红包
	 * @Title:           payRedPacket
	 * @Description:     TODO
	 * @param:           @param userId
	 * @param:           @param payType
	 * @param:           @param money
	 * @param:           @param type
	 * @param:           @param request
	 * @param:           @return   
	 * @return:          String   
	 * @throws
	 */
	@RequestMapping(value = "/open/payRedPacket", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String payRedPacket(@RequestParam String userId,
			@RequestParam String payType, @RequestParam String money,
			@RequestParam String type, HttpServletRequest request) {
		System.out.println("进入");
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(payType)
				|| StringUtils.isBlank(money) || StringUtils.isBlank(type)) {
			return JsonUtils.writeJson(0, 0, "参数错误");
		}
		String result = JsonUtils.writeJson(0, 0, "参数错误");
		int pay_type = Integer.parseInt(payType);
		int taskType = Integer.parseInt(type);
		long user_id = Long.parseLong(userId);
		Double price = Double.parseDouble(money);



		User user = userService.getUserById(user_id);
		if (null == user) {
			return JsonUtils.writeJson(0, 4, "用户不存在");
		}
		if (Constants.PAY_TYPE_WECHAT == pay_type) {
			if (Constants.ORDER_TYPE_REDPACKET == taskType) {
				//微信支付发红包
				String record_sn = redPacketService.addRedPacketOrderRecord(user_id,money,pay_type);
				if(null == record_sn){
					return JsonUtils.writeJson(0, 19, "订单生成失败");
				}
				SortedMap<Object, Object> map =  WxPayUtil.getPreperIdFromWX(record_sn, PayCommonUtil.getIpAddress(request),Constants.APP_NAME+Constants.REDPACKET, price);
				if(null == map){
					return JsonUtils.writeJson(0, 19, "订单生成失败");
				}
				return  JsonUtils.writeJson(1, "请求成功", map, "object");
			}
		}else if(Constants.PAY_TYPE_BALANCE == pay_type){
			
			if(Constants.ORDER_TYPE_REDPACKET == taskType){
				
				Wallet wallet = walletService.findWalletByUserId(user_id);
				if(null == wallet){
					return JsonUtils.writeJson(0, 0, "参数错误");
				}
				//余额支付发红包
				//wallet.getMoney().compareTo(new Double(money));
				if(wallet.getMoney().compareTo(new Double(money)) < 0){
					return JsonUtils.writeJson(0, 21, "余额不足");
				}
				boolean i = redPacketService.addRedPacketPayByBalanceRecord(user_id,money,pay_type,wallet);
				if(true == i){
					//添加日志，发送红包扣除余额

					return  JsonUtils.writeJson("红包发送成功", 1);
				}else {
					return JsonUtils.writeJson(0, 22, "红包发送失败");
				}
			}
		}

		return JsonUtils.writeJson(0, 0, "参数错误");
	}
}
