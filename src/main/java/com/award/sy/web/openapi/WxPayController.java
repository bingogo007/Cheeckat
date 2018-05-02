package com.award.sy.web.openapi;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.award.core.util.ImUtils;
import com.award.sy.entity.*;
import com.award.sy.service.*;

import org.apache.commons.lang3.StringUtils;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.award.core.util.JsonUtils;
import com.award.sy.common.Constants;
import com.award.sy.common.PayCommonUtil;
import com.award.sy.common.XMLUtil;

@Controller
public class WxPayController {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private WalletRecordService walletRecordService;
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private RedPacketService redPacketService;

	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;
	
	@Autowired
	private MissionService missionService;
	
	@Autowired
	private FriendService friendService;
	
	/**
	 * 微信下单统一接口
	 * 
	 * @Title: wxPrePay
	 * @Description: TODO
	 * @param: @param request
	 * @param: @param response
	 * @param: @return
	 * @return: Map<String,Object>
	 * @throws
	 */
	@RequestMapping("/wxPrePay")
	@ResponseBody
	public String wxPrePay(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// GoodsTrade goodsTrade = goodsTradeService.queryGoodsTradeById(request
		// .getParameter("tradeId"));
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		// 获取订单，根据需要自己编写
		String record_sn = request.getParameter("record_sn");
		String user_id = request.getParameter("user_id");
		String price = request.getParameter("price");
		if(StringUtils.isBlank(record_sn)||StringUtils.isBlank(user_id)||StringUtils.isBlank(price)){
			return returnStr;
		}
		
		// String price = goodsTrade.getPrice();
	//	String price = request.getParameter("price");
		int price100 = new BigDecimal(price).multiply(new BigDecimal(100))
				.intValue();
		if (price100 <= 0) {
			//resultMap.put("msg", "付款金额错误");
			//resultMap.put("code", "500");
			return  JsonUtils.writeJson(0, 0, "付款金额错误");
		}
		// 设置回调地址-获取当前的地址拼接回调地址
		String url = request.getRequestURL().toString();
		String domain = url.substring(0, url.length() - 13);
		// 生产环境
		String notify_url = domain + "wxNotify";
		// 测试环境
		// String notify_url =
		// "http://1f504p5895.51mypc.cn/cia/app/wxNotify.html";

		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		parameters.put("appid", Constants.APPID);
		parameters.put("mch_id", Constants.MCH_ID);
		parameters.put("nonce_str", PayCommonUtil.CreateNoncestr());
		parameters.put("body", "购买测试");
		parameters.put("out_trade_no", ""); // 订单id
		parameters.put("fee_type", "CNY");
		parameters.put("total_fee", String.valueOf(price100));
		parameters.put("spbill_create_ip", PayCommonUtil.getIpAddress(request));
		parameters.put("notify_url", notify_url);
		parameters.put("trade_type", "APP");
		// 设置签名
		String sign = PayCommonUtil.createSign("UTF-8", parameters);
		parameters.put("sign", sign);
		// 封装请求参数结束
		String requestXML = PayCommonUtil.getRequestXml(parameters);
		// 调用统一下单接口
		String result = PayCommonUtil.httpsRequest(Constants.UNIFIED_ORDER_URL,
				"POST", requestXML);
		System.out.println("\n" + result);
		try {
			/**
			 * 统一下单接口返回正常的prepay_id，再按签名规范重新生成签名后，将数据传输给APP。参与签名的字段名为appId，
			 * partnerId
			 * ，prepayId，nonceStr，timeStamp，package。注意：package的值格式为Sign=WXPay
			 **/
			Map<String, String> map = XMLUtil.doXMLParse(result);
			SortedMap<Object, Object> parameterMap2 = new TreeMap<Object, Object>();
			parameterMap2.put("appid", Constants.APPID);
			parameterMap2.put("partnerid", Constants.MCH_ID);
			parameterMap2.put("prepayid", map.get("prepay_id"));
			parameterMap2.put("package", "Sign=WXPay");
			parameterMap2.put("noncestr", PayCommonUtil.CreateNoncestr());
			parameterMap2.put(
					"timestamp",
					Long.parseLong(String.valueOf(System.currentTimeMillis())
							.toString()));
			String sign2 = PayCommonUtil.createSign("UTF-8", parameterMap2);
			parameterMap2.put("sign", sign2);
			/*resultMap.put("code", "200");
			resultMap.put("msg", parameterMap2);*/
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonUtils.writeJson(1, "获取成功", resultMap, "object");
	}

	/**
	 * 微信异步通知
	 * @Title:           wxNotify
	 * @Description:     TODO
	 * @param:           @param request
	 * @param:           @param response
	 * @param:           @throws IOException
	 * @param:           @throws JDOMException   
	 * @return:          void   
	 * @throws
	 */
	@RequestMapping("/wxNotify")
	@ResponseBody
	public void wxNotify(HttpServletRequest request,
			HttpServletResponse response) throws IOException, JDOMException {
		// 读取参数
		InputStream inputStream;
		StringBuffer sb = new StringBuffer();
		inputStream = request.getInputStream();
		String s;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream, "UTF-8"));
		while ((s = in.readLine()) != null) {
			sb.append(s);
		}
		in.close();
		inputStream.close();
		// 解析xml成map
		Map<String, String> m = new HashMap<String, String>();
		m = XMLUtil.doXMLParse(sb.toString());
		for (Object keyValue : m.keySet()) {
			System.out.println(keyValue + "=" + m.get(keyValue));
		}
		// 过滤空 设置 TreeMap
		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
		Iterator it = m.keySet().iterator();
		while (it.hasNext()) {
			String parameter = (String) it.next();
			String parameterValue = m.get(parameter);

			String v = "";
			if (null != parameterValue) {
				v = parameterValue.trim();
			}
			packageParams.put(parameter, v);
		}

		// 判断签名是否正确
		String resXml = "";
		if (PayCommonUtil.isTenpaySign("UTF-8", packageParams)) {
			if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
				// 这里是支付成功
				// ////////执行自己的业务逻辑////////////////
				String mch_id = (String) packageParams.get("mch_id"); // 商户号
				String openid = (String) packageParams.get("openid"); // 用户标识
				String out_trade_no = (String) packageParams
						.get("out_trade_no"); // 商户订单号
				String total_fee = (String) packageParams.get("total_fee");
				String transaction_id = (String) packageParams
						.get("transaction_id"); // 微信支付订单号
				
				WalletRecord walletRecord = walletRecordService.getWallerOrderByRecordSN(out_trade_no);
				//Map<String, Object> walletRecordMap = list.get(0);
				Double money =  walletRecord.getMoney();
				int pay_status =  walletRecord.getPay_status();
				int type = walletRecord.getType();
				long from_uid = walletRecord.getFrom_uid();

				Wallet wallet = walletService.findWalletByUserId(from_uid);
				Double fee = wallet.getMoney()+money;
				if (!Constants.MCH_ID.equals(mch_id)
						|| walletRecord == null
						|| new Double(total_fee)
								.compareTo(new Double(money*100)) != 0) {
					logger.info("支付失败,错误信息：" + "参数错误");
					resXml = "<xml>"
							+ "<return_code><![CDATA[FAIL]]></return_code>"
							+ "<return_msg><![CDATA[参数错误]]></return_msg>"
							+ "</xml> ";

					walletRecordService.editWalletOrderPayStatus(out_trade_no,Constants.PAY_STATUS_FAIL);
					redPacketService.editRedPacketPayStatus(out_trade_no,Constants.PAY_STATUS_FAIL);
				} else {
					if (Constants.PAY_STATUS_WAIT == pay_status) {// 支付的价格
						// 订单状态的修改。根据实际业务逻辑执行
						if(Constants.ORDER_TYPE_TRADE == type){
							//充值成功，更新支付状态，修改余额

							walletService.editUserWalletPayBalance(out_trade_no,from_uid,Constants.LOG_RECHARGE, money,fee);
						}else if(Constants.ORDER_TYPE_REDPACKET == type){
							//微信发红包成功，更新支付状态，更新log
							boolean i = walletRecordService.editUserWalletPayElse(out_trade_no,from_uid,Constants.LOG_AWARD_REDPACKET,money,fee);
							if(true == i){
								//更新红包支付状态
								redPacketService.editRedPacketPayStatus(out_trade_no,Constants.PAY_STATUS_SUCCESS);
								//判断to类型是群发还是个人红包
								RedPacket redPacket = redPacketService.getRedPacketByRecordSN(out_trade_no);
								User fromUser = userService.getUserById(redPacket.getPublish_id());
								//个人,直接获取个人Id并发送至环信
								if(Constants.TO_TYPE_PRIVATE == redPacket.getTo_type()){
									User toUser = userService.getUserById(redPacket.getTo_id());

									ImUtils.sendTextMessage("users", new String[]{toUser.getUser_name()}, "WtwdRedPacketTxt:好友"+fromUser.getNick_name()+"发布了一个任务，点击查看:"+redPacket.getRedpacket_id());
									ImUtils.sendTextMessage("users", new String[]{fromUser.getUser_name()}, "WtwdRedPacketTxt:" + fromUser.getNick_name() + "发布了一个红包，点击查看:" + redPacket.getRedpacket_id());
								}else if (Constants.TO_TYPE_GROUP == redPacket.getTo_type()){
									//群发，获取群成员的名称，并发送
									Group group = groupService.getGroupById(redPacket.getTo_id());
									if(group != null) {
										ImUtils.sendTextMessage("chatgroups", new String[]{group.getIm_group_id()}, "WtwdRedPacketTxt:好友"+fromUser.getNick_name()+"发布了一个任务，点击查看:"+redPacket.getRedpacket_id());
									}
								}
							}
						}else if(Constants.ORDER_TYPE_TASK == type){

							//微信发红包成功，更新支付状态，更新log
							boolean tag = walletRecordService.editUserWalletPayElse(out_trade_no,from_uid,Constants.LOG_AWARD_TASK,money,fee);
							if(true == tag){
								Mission mission = missionService.getMissionByRecordSN(out_trade_no);
								User user = userService.getUserById(mission.getPublish_id());
						    	if(mission.getTo_type() == 0){//如果发给所有人
							    	List<Map<String,Object>> fList = friendService.getUserFriends(mission.getPublish_id());							    	
							    	if(fList.size() > 20) {//每次只能发送给20个人
							    		int count = fList.size() / 20;
							    		for(int i = 0; i < count; i++) {
							    			String userNames = "";
							    			for(int j = 20*i; j < 20*(i+1); j++) {
							    				Map<String,Object> map = fList.get(j);
							    				if(userNames.equals("")) {		    					
								    				userNames = (String)map.get("user_name");
								    			}else {
								    				userNames = userNames.concat(",").concat((String)map.get("user_name"));
								    			}
							    			}
							    			ImUtils.sendTextMessage("users", userNames.split(","), "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission.getMission_id());
							    		}
							    		int mod = fList.size() % 20;
							    		String userNames = "";
							    		for(int i = count*20; i < count*20+mod; i++) {
							    			Map<String,Object> map = fList.get(i);
						    				if(userNames.equals("")) {		    					
							    				userNames = (String)map.get("user_name");
							    			}else {
							    				userNames = userNames.concat(",").concat((String)map.get("user_name"));
							    			}
							    		}
							    		ImUtils.sendTextMessage("users", userNames.split(","), "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission.getMission_id());
							    	}else if(fList.size() > 0) {//每次只能发送给20个人
							    		String userNames = "";
							    		for(Map<String,Object> map : fList) {
							    			if(userNames.equals("")) {
							    				userNames = (String)map.get("user_name");
							    			}else {
							    				userNames = userNames.concat(",").concat((String)map.get("user_name"));
							    			}
							    		}
							    		ImUtils.sendTextMessage("users", userNames.split(","), "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission.getMission_id());
							    	}
							    }else if(mission.getTo_type() == 1) {//发给个人
							    	User toUser = userService.getUserById(mission.getTo_id());
							    	if(toUser != null) {
							    		ImUtils.sendTextMessage("users", new String[]{toUser.getUser_name()}, "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission.getMission_id());
							    	}		    	
							    }else {//发群
							    	Group group = groupService.getGroupById(mission.getTo_id());
							    	if(group != null) {
							    		ImUtils.sendTextMessage("chatgroups", new String[]{group.getIm_group_id()}, "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission.getMission_id());
							    	}		    	
							    }
								mission.setStatus(0);
								missionService.editMission(mission);
							}
						
						}

						resXml = "<xml>"
								+ "<return_code><![CDATA[SUCCESS]]></return_code>"
								+ "<return_msg><![CDATA[OK]]></return_msg>"
								+ "</xml> ";

					} else {
						resXml = "<xml>"
								+ "<return_code><![CDATA[SUCCESS]]></return_code>"
								+ "<return_msg><![CDATA[OK]]></return_msg>"
								+ "</xml> ";
						logger.info("订单已处理");
					}
				}

			} else {
				logger.info("支付失败,错误信息：" + packageParams.get("err_code"));
				resXml = "<xml>"
						+ "<return_code><![CDATA[FAIL]]></return_code>"
						+ "<return_msg><![CDATA[报文为空]]></return_msg>"
						+ "</xml> ";
				//redPacketService.editRedPacketPayStatus("",Constants.PAY_STATUS_FAIL);
			}

		} else {
			resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
					+ "<return_msg><![CDATA[通知签名验证失败]]></return_msg>"
					+ "</xml> ";
			logger.info("通知签名验证失败");
		}

		// ------------------------------
		// 处理业务完毕
		// ------------------------------
		BufferedOutputStream out = new BufferedOutputStream(
				response.getOutputStream());
		out.write(resXml.getBytes());
		out.flush();
		out.close();

	}
}
