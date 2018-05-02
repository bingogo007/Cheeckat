package com.award.sy.web.openapi;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import com.award.core.util.JsonUtils;
import com.award.sy.common.Constants;
import com.award.sy.common.PayCommonUtil;
import com.award.sy.common.WxPayUtil;
import com.award.sy.entity.User;
import com.award.sy.entity.Wallet;
import com.award.sy.entity.WalletLog;
import com.award.sy.entity.WalletRecord;
import com.award.sy.service.UserService;
import com.award.sy.service.WalletLogService;
import com.award.sy.service.WalletRecordService;
import com.award.sy.service.WalletService;
import com.google.zxing.Result;
import com.sun.tools.javac.code.Attribute.Constant;

@Controller
public class WalletController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private WalletRecordService walletRecordService;

	@Autowired
	private WalletLogService walletLogService;

	@Autowired
	private WalletService walletService;

	@Autowired
	private UserService userService;

	/**
	 * 申请提现
	 * 
	 * @Title: withdrawMoney
	 * @Description: TODO
	 * @param: @param user_id
	 * @param: @param money
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	@RequestMapping(value = "/open/withdraw", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String withdrawMoney(@RequestParam String user_id,
			@RequestParam String money,@RequestParam String type) {
		String result = JsonUtils.writeJson(0, 0, "参数为空");
		if (StringUtils.isBlank(user_id) || StringUtils.isBlank(money)||StringUtils.isBlank(type)) {
			return result;
		}
		if(Constants.ORDER_TYPE_WITHDRAWLS != Integer.parseInt(type)){
			return result;
		}
		long userId = Long.parseLong(user_id);
		User user = userService.getUserById(userId);
		Double price = new Double(money);
		if (null == user) {
			return JsonUtils.writeJson(0, 4, "用户不存在");
		}
		if (price.doubleValue() < 100) {
			return JsonUtils.writeJson(0, 23, "提现额度错误");
		}
		Wallet wallet = walletService.findWalletByUserId(userId);
		if (wallet.getMoney().compareTo(price) < 0) {
			return JsonUtils.writeJson(0, 21, "余额不足");
		}
		String record_sn = walletRecordService.addWalletRecordOrder(userId,money,0,Constants.ORDER_TYPE_WITHDRAWLS);
		if(null == record_sn){
			return JsonUtils.writeJson(0, 23, "提现额度错误");
		}
		//修改金额,更新订单支付状态，插入余额记录
		Double total_fee = wallet.getMoney()-Double.parseDouble(money);
		String changemoney = "-"+money;
		boolean i = walletService.editUserWalletPayBalance(record_sn,userId,Constants.LOG_WITHDRAW,Double.parseDouble(changemoney),total_fee);
		if(true == i){
			return  JsonUtils.writeJson("申请成功", 1);
		}

		return null;
	}

	/**
	 * 充值
	 * 
	 * @Title: rechargMoney
	 * @Description: TODO
	 * @param: @param user_id
	 * @param: @param money
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	@RequestMapping(value = "/open/recharge",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeMoney(@RequestParam String userId,
			@RequestParam String payType, @RequestParam String money,
			@RequestParam String type, HttpServletRequest request) {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(payType)
				|| StringUtils.isBlank(money) || StringUtils.isBlank(type)) {
			return JsonUtils.writeJson(0, 0, "参数错误");
		}
		String result = JsonUtils.writeJson(0, 0, "参数错误");
		int pay_type = Integer.parseInt(payType);
		int taskType = Integer.parseInt(type);
		long user_id = Long.parseLong(userId);
		double price = Double.parseDouble(money);
		User user = userService.getUserById(user_id);
		if (null == user) {
			return JsonUtils.writeJson(0, 4, "用户不存在");
		}

		if (Constants.PAY_TYPE_WECHAT == pay_type) {
			 if (Constants.ORDER_TYPE_TRADE == taskType) {
				// 微信支付充值
				String record_sn = walletRecordService.addWalletRecordOrder(user_id,money,Constants.PAY_TYPE_WECHAT,Constants.ORDER_TYPE_TRADE);
				if (null == record_sn) {
					return JsonUtils.writeJson(0, 19, "订单生成失败");
				}
				SortedMap<Object, Object> map = WxPayUtil.getPreperIdFromWX(
						record_sn, PayCommonUtil.getIpAddress(request),
						Constants.APP_NAME + Constants.RECHARGE, price);
				if (null == map) {
					return JsonUtils.writeJson(0, 19, "订单生成失败");
				}
				return JsonUtils.writeJson(1, "请求成功", map, "object");
			} else {
				return JsonUtils.writeJson(0, 0, "参数错误");
			}
		}
		return JsonUtils.writeJson(0, 0, "参数错误");
	}
	/**
	 * 获取余额
	 * @Title:           getBalance
	 * @Description:     TODO
	 * @param:           @param userId
	 * @param:           @return   
	 * @return:          String   
	 * @throws
	 */
	@RequestMapping(value = "/open/getBalance", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getBalance(@RequestParam String userId){
		if (StringUtils.isBlank(userId)) {
			return JsonUtils.writeJson(0, 0, "参数错误");
		}
		
		long user_id = Long.parseLong(userId);
		User user = userService.getUserById(user_id);
		if(null == user){
			return JsonUtils.writeJson(0, 4, "用户不存在");
		}
		Wallet wallet = walletService.findWalletByUserId(user_id);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("balance",wallet.getMoney().doubleValue());
		return JsonUtils.writeJson(1, "请求成功", result, "object");
	}

	/**
	 * 查询user余额变动
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/open/getBalanceDetail",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getBalanceDetail(@RequestParam String userId){
		if(StringUtils.isBlank(userId)){
			return JsonUtils.writeJson(0, 0, "参数错误");
		}

		List<Map<String,Object>> list = walletLogService.getWalletLogByUserId(Long.parseLong(userId));
		return JsonUtils.writeJson(1, "请求成功", list, "object");
	}
}
