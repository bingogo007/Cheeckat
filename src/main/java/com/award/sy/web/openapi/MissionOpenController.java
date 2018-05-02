package com.award.sy.web.openapi;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.award.core.util.ImUtils;
import com.award.core.util.JsonUtils;
import com.award.sy.common.Constants;
import com.award.sy.common.DateUtil;
import com.award.sy.common.PayCommonUtil;
import com.award.sy.common.WxPayUtil;
import com.award.sy.entity.Group;
import com.award.sy.entity.Mission;
import com.award.sy.entity.User;
import com.award.sy.entity.Wallet;
import com.award.sy.service.FriendService;
import com.award.sy.service.GroupService;
import com.award.sy.service.MissionService;
import com.award.sy.service.UserService;
import com.award.sy.service.WalletRecordService;
import com.award.sy.service.WalletService;


@Controller
public class MissionOpenController {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Resource
	private UserService userService;
	
	@Resource
	private MissionService missionService;
	
	@Resource
	private FriendService friendService;
	
	@Resource
	private WalletService walletService;
	
	@Resource
	private WalletRecordService walletRecordService;
	
	@Resource
	private GroupService groupService;
	
	/**
	 * 查询附近的人
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/open/getAllMission", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getAllMission(HttpServletRequest request) {
		String start = request.getParameter("start");
		String count = request.getParameter("count");
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(!StringUtils.isBlank(start) && !StringUtils.isBlank(count)) {
			List<Mission> mList = missionService.getExpiredMission();
			for(Mission mission:mList) {
				mission.setStatus(4);
				missionService.editMission(mission);
				Wallet wallet = walletService.findWalletByUserId(mission.getPublish_id());
					if(null == wallet){
						return JsonUtils.writeJson(0, 0, "参数错误");
					}
					//修改金额,更新订单支付状态，插入余额记录
					Double total_fee = wallet.getMoney()+mission.getMoney();
					String changemoney = ""+mission.getMoney();
					boolean isWalletSuccess = walletService.refund(mission.getRecord_sn(),mission.getPublish_id(),Constants.LOG_REFUND_TASK,Double.parseDouble(changemoney),total_fee);
					if(false == isWalletSuccess){
						return JsonUtils.writeJson(0, 22, "余额更新失败");
					}
			}
			List<Map<String,Object>> list = missionService.getAllMissionLimit(Integer.parseInt(start), Integer.parseInt(count));
			returnStr = JsonUtils.writeJson(1, "获取成功", list, "object");
		}
		return returnStr;
	}
	
	@RequestMapping(value = "/open/getMyMission", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getMyMission(HttpServletRequest request) {
		String userId = request.getParameter("userId");
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(!StringUtils.isBlank(userId)) {
			User user = userService.getUserById(Long.parseLong(userId));
		    if(user != null) {
		    	List<Map<String,Object>> list = missionService.getMyMission(Long.parseLong(userId));			    
				returnStr = JsonUtils.writeJson(1, "获取成功", list, "object");
		    }else {
		    	returnStr = JsonUtils.writeJson(0, 4, "用户不存在");			
		    }
		}
		return returnStr;
	}
	
	@RequestMapping(value = "/open/acceptMission", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String acceptMission(HttpServletRequest request) {
		String userId =  request.getParameter("userId");
		String missionId = request.getParameter("missionId");
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(!StringUtils.isBlank(userId) && !StringUtils.isBlank(missionId)) {
			User user = userService.getUserById(Long.parseLong(userId));
		    if(user != null) {
		    	Mission mission = missionService.getMissionById(Long.parseLong(missionId));
		    	if(mission != null) {
		    		if(mission.getSex() == user.getSex()) {
		    			if(mission.getPublish_id() != user.getUser_id()) {
		    			if(mission.getAccept_id() == null) {
		    				mission.setAccept_id(user.getUser_id());
			    			mission.setStatus(1);
			    			mission.setAccept_time(DateUtil.getNowTime());
			 			    missionService.editMission(mission);
			 			    returnStr = JsonUtils.writeJson("设置成功", 1);
		    			}else {
		    				returnStr = JsonUtils.writeJson(0, 9, "该任务已被领取");
		    			}
		    			}else {
		    				returnStr = JsonUtils.writeJson(0, 11, "不能领取自己的任务");
		    			}
		    			
		    		}else {
		    			returnStr = JsonUtils.writeJson(0, 6, "该任务不适合你");
		    		}
		    	}else {
		    		returnStr = JsonUtils.writeJson(0, 8, "任务不存在");
		    	}
		    }else {
		    	returnStr = JsonUtils.writeJson(0, 4, "用户不存在");			
		    }
		   }
		return returnStr;
	}
	
	@RequestMapping(value = "/open/completeMission", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String completeMission(HttpServletRequest request) {
		String userId =  request.getParameter("userId");
		String missionId = request.getParameter("missionId");
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(!StringUtils.isBlank(userId) && !StringUtils.isBlank(missionId)) {
			User user = userService.getUserById(Long.parseLong(userId));
		    if(user != null) {
		    	Mission mission = missionService.getMissionById(Long.parseLong(missionId));
		    	if(mission != null) {
		    		if(mission.getAccept_id() == Long.parseLong(userId) || mission.getPublish_id() == Long.parseLong(userId)) {
		    			if(mission.getAccept_id() == Long.parseLong(userId)) {
		    				mission.setStatus(2);
		    			}else if(mission.getPublish_id() == Long.parseLong(userId) && mission.getStatus() == 2) {
		    				mission.setStatus(3);
		    				mission.setFinish_time(DateUtil.getNowTime());
		    			}else {
		    				returnStr = JsonUtils.writeJson(0, 12, "无法确认完成");
		    			}
		 			    missionService.editMission(mission);
		 			   Wallet wallet = walletService.findWalletByUserId(mission.getAccept_id());
						if(null == wallet){
							return JsonUtils.writeJson(0, 0, "参数错误");
						}
		 			    Double total_fee = wallet.getMoney()+mission.getMoney();
						String changemoney = ""+mission.getMoney();
						boolean isWalletSuccess = walletService.editUserWalletPayBalance(mission.getRecord_sn(),mission.getAccept_id(),Constants.LOG_FETCH_TASK,Double.parseDouble(changemoney),total_fee);
						if(false == isWalletSuccess){
							return JsonUtils.writeJson(0, 22, "余额更新失败");
						}
		 			    returnStr = JsonUtils.writeJson("设置成功", 1);
		    		}else {
		    			returnStr = JsonUtils.writeJson(0, 10, "无权限操作");
		    		}
		    	}else {
		    		returnStr = JsonUtils.writeJson(0, 8, "任务不存在");
		    	}
		    }else {
		    	returnStr = JsonUtils.writeJson(0, 4, "用户不存在");			
		    }
		   }
		return returnStr;
	}
	
	@RequestMapping(value = "/open/closeMission", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String closeMission(HttpServletRequest request) {
		String userId =  request.getParameter("userId");
		String missionId = request.getParameter("missionId");
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(!StringUtils.isBlank(userId) && !StringUtils.isBlank(missionId)) {
			User user = userService.getUserById(Long.parseLong(userId));
		    if(user != null) {
		    	Mission mission = missionService.getMissionById(Long.parseLong(missionId));
		    	if(mission != null) {
		    		if(mission.getPublish_id() == Long.parseLong(userId)) {
                        if(mission.getStatus() == 0) {
		    				mission.setStatus(4);
		    			}else {
		    				returnStr = JsonUtils.writeJson(0, 12, "无法关闭");
		    			}                        
		 			    missionService.editMission(mission);
		 			    Wallet wallet = walletService.findWalletByUserId(Long.parseLong(userId));
						if(null == wallet){
							return JsonUtils.writeJson(0, 0, "参数错误");
						}
						//修改金额,更新订单支付状态，插入余额记录
						Double total_fee = wallet.getMoney()+mission.getMoney();
						String changemoney = ""+mission.getMoney();
						boolean isWalletSuccess = walletService.refund(mission.getRecord_sn(),Long.parseLong(userId),Constants.LOG_REFUND_TASK,Double.parseDouble(changemoney),total_fee);
						if(false == isWalletSuccess){
							return JsonUtils.writeJson(0, 22, "余额更新失败");
						}
		 			    returnStr = JsonUtils.writeJson("设置成功", 1);
		    		}else {
		    			returnStr = JsonUtils.writeJson(0, 10, "无权限操作");
		    		}
		    	}else {
		    		returnStr = JsonUtils.writeJson(0, 8, "任务不存在");
		    	}
		    }else {
		    	returnStr = JsonUtils.writeJson(0, 4, "用户不存在");			
		    }
		   }
		return returnStr;
	}

	
	@RequestMapping(value = "/open/publishMission", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String publishMission(HttpServletRequest request) {
		String userId =  request.getParameter("userId");
		String content = request.getParameter("content");
		String type = request.getParameter("type");
		String sex = request.getParameter("sex");
		String money = request.getParameter("money");
		String address = request.getParameter("address");
		String startTime = request.getParameter("startTime");
		String to = request.getParameter("to");//0是发给所有人,1传用户,2是传群
		String anonymous = request.getParameter("anonymous");
		String pay_type = request.getParameter("payType");
		String toId = request.getParameter("toId"); //发送到群
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(!StringUtils.isBlank(userId) && !StringUtils.isBlank(content)&& !StringUtils.isBlank(type)&& !StringUtils.isBlank(sex)&& !StringUtils.isBlank(money)&& !StringUtils.isBlank(address) && !StringUtils.isBlank(startTime) && !StringUtils.isBlank(pay_type) && !StringUtils.isBlank(to)) {
			if(Integer.parseInt(to) > 0 && StringUtils.isBlank(toId)) {
				return returnStr;
			}
			User user = userService.getUserById(Long.parseLong(userId));
			String record_sn = "";
		    if(user != null) {
				if (Constants.PAY_TYPE_WECHAT == Integer.parseInt(pay_type)) {
					//微信支付发红包
					record_sn = walletRecordService.addWalletRecordOrder(Long.parseLong(userId),money,Constants.PAY_TYPE_WECHAT,Constants.ORDER_TYPE_TASK);
					if(null == record_sn){
						return JsonUtils.writeJson(0, 19, "订单生成失败");
					}
					SortedMap<Object, Object> map =  WxPayUtil.getPreperIdFromWX(record_sn, PayCommonUtil.getIpAddress(request),Constants.APP_NAME+Constants.TASK, Double.parseDouble(money));
					if(null == map){
						return JsonUtils.writeJson(0, 19, "订单生成失败");
					}
					Mission mission = new Mission();
			    	mission.setAddress(address);
			    	mission.setContent(content);
			    	mission.setCreate_time(DateUtil.getNowTime());
			    	mission.setMoney(Double.parseDouble(money));
			    	mission.setPublish_id(user.getUser_id());
			    	mission.setSex(Integer.parseInt(sex));
			    	mission.setStart_time(startTime);
			    	mission.setStatus(5);//等待微信返回支付成功
			    	mission.setType(Integer.parseInt(type));
			    	mission.setTo_type(Integer.parseInt(to));
			    	mission.setTo_id(Long.parseLong(toId));
			    	mission.setRecord_sn(record_sn);
			    	mission.setAnonymous(Integer.parseInt(anonymous));
			    	missionService.addMission(mission);
			}else if(Constants.PAY_TYPE_BALANCE == Integer.parseInt(pay_type)){
					Wallet wallet = walletService.findWalletByUserId(Long.parseLong(userId));
					if(null == wallet){
						return JsonUtils.writeJson(0, 0, "参数错误");
					}
					//余额支付发红包
					if(wallet.getMoney().compareTo(new Double(money)) < 0){
						return JsonUtils.writeJson(0, 21, "余额不足");
					}
					//生成订单
					record_sn = walletRecordService.addWalletRecordOrder(Long.parseLong(userId),money,Constants.PAY_TYPE_BALANCE,Constants.ORDER_TYPE_TASK);
					if(null == record_sn){
						return JsonUtils.writeJson(0, 22, "订单生成失败");
					}
					//修改金额,更新订单支付状态，插入余额记录
					Double total_fee = wallet.getMoney()-Double.parseDouble(money);
					String changemoney = "-"+money;
					boolean isWalletSuccess = walletService.editUserWalletPayBalance(record_sn,Long.parseLong(userId),Constants.LOG_AWARD_REDPACKET,Double.parseDouble(changemoney),total_fee);
					if(false == isWalletSuccess){
						return JsonUtils.writeJson(0, 22, "订单生成失败");
					}
					Mission mission = new Mission();
			    	mission.setAddress(address);
			    	mission.setContent(content);
			    	mission.setCreate_time(DateUtil.getNowTime());
			    	mission.setMoney(Double.parseDouble(money));
			    	mission.setPublish_id(user.getUser_id());
			    	mission.setSex(Integer.parseInt(sex));
			    	mission.setStart_time(startTime);
			    	mission.setStatus(0);
			    	mission.setType(Integer.parseInt(type));
			    	mission.setTo_type(Integer.parseInt(to));
			    	mission.setTo_id(Long.parseLong(toId));
			    	mission.setRecord_sn(record_sn);
			    	mission.setAnonymous(Integer.parseInt(anonymous));
			    	missionService.addMission(mission);
			    	Mission mission2 = missionService.getMissionByPubIdAndCreateTime(user.getUser_id(),mission.getCreate_time(),mission.getContent(),mission.getStart_time());
			    	if(Integer.parseInt(to) == 0){//如果发给所有人
			    	List<Map<String,Object>> fList = friendService.getUserFriends(Long.parseLong(userId));
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
			    			ImUtils.sendTextMessage("users", userNames.split(","), "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission2.getMission_id());
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
			    		ImUtils.sendTextMessage("users", userNames.split(","), "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission2.getMission_id());
			    	}else if(fList.size() > 0) {//每次只能发送给20个人
			    		String userNames = "";
			    		for(Map<String,Object> map : fList) {
			    			if(userNames.equals("")) {
			    				userNames = (String)map.get("user_name");
			    			}else {
			    				userNames = userNames.concat(",").concat((String)map.get("user_name"));
			    			}
			    		}
			    		ImUtils.sendTextMessage("users", userNames.split(","), "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission2.getMission_id());
			    	}
			    }else if(Integer.parseInt(to) == 1) {//发给个人
			    	User toUser = userService.getUserById(Long.parseLong(toId));
			    	if(toUser != null) {
			    		ImUtils.sendTextMessage("users", new String[]{toUser.getUser_name()}, "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission2.getMission_id());
			    	}		    	
			    }else {//发群
			    	Group group = groupService.getGroupById(Long.parseLong(toId));
			    	if(group != null) {
			    		ImUtils.sendTextMessage("chatgroups", new String[]{group.getIm_group_id()}, "WtwdMissionTxt:好友"+user.getNick_name()+"发布了一个任务，点击查看:"+mission2.getMission_id());
			    	}		    	
			    }
			}		    	
		    	returnStr = JsonUtils.writeJson("发布成功", 1);
		    }else {
		    	returnStr = JsonUtils.writeJson(0, 4, "用户不存在");			
		    }
		   }
		return returnStr;
	}


}
