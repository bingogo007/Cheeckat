package com.award.sy.web.openapi;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.award.core.util.ImUtils;
import com.award.core.util.JsonUtils;
import com.award.sy.common.Constants;
import com.award.sy.entity.User;
import com.award.sy.service.UserService;
import com.award.sy.service.WalletService;
/**
 * 登录接口
 * @ClassName:       LoginOpenController
 * @Description:     TODO
 * @author:          w77996
 * @date:            2018年4月26日        下午3:16:16
 */
@Controller
public class LoginOpenController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WalletService walletService;
	
	/**
	 * 用户登录
	 * @Title:           userLogin
	 * @Description:     TODO
	 * @param:           @return   
	 * @return:          String   
	 * @throws
	 */
	@RequestMapping(value="/open/userLogin",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String userLogin(@RequestParam(required=false) String phone,@RequestParam(required=true) String type,@RequestParam(required=false) String openId){
		if(StringUtils.isBlank(type)){
			return  JsonUtils.writeJson(0, 0, "参数为空");
		}
		Map<String,Object> map = new HashMap<String, Object>();
		if(Constants.LOGIN_TYPE_PHONE == Integer.parseInt(type)){
			//通过手机查找用户是否存在
			User user = userService.getUserByPhone(phone);
			if(null == user){
				map.put("isFirst", true);
				userService.addNewUserInfo(phone, Integer.parseInt(type));
				user = userService.getUserByPhone(phone);
			}
			map.put("user", user);
			return JsonUtils.writeJson(1, "请求成功", map, "object");
			
		}else if(Constants.LOGIN_TYPE_WECHAT == Integer.parseInt(type)){
			//判断参数
			if(StringUtils.isBlank(openId)){
				return JsonUtils.writeJson(0, 0, "参数为空");
			}
			//查询
			User user = userService.getUserByWxOpenId(openId);
			if(null == user){
				map.put("isFirst", true);
				userService.addNewUserInfo(openId,Integer.parseInt(type));
				user = userService.getUserByWxOpenId(openId);
			}
			map.put("user", user);
			return JsonUtils.writeJson(1, "请求成功", map, "object");
		}
		
		return  JsonUtils.writeJson(0, 0, "参数为空");
	}
	
	@RequestMapping("/open/test")
	public void test(){

		ImUtils.authRegister("wangyi", "123456", "wangyi");
		ImUtils.authRegister("lier","123456","lier");
	}
	
}
