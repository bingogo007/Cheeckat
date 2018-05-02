package com.award.sy.web.openapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.award.core.util.ImUtils;
import com.award.sy.entity.Group;
import com.award.sy.entity.GroupDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.award.core.util.JsonUtils;
import com.award.sy.entity.User;
import com.award.sy.service.GroupDetailsService;
import com.award.sy.service.GroupService;
import com.award.sy.service.UserService;

@Controller
public class GroupOpenController {
	
	@Autowired
	private GroupDetailsService groupDetailsService;
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 获取用户群聊列表
	 * <p>Title: getUserGroup</p>  
	 * <p>Description: </p>  
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/open/getGroupList",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getUserGroup(@RequestParam String userId){
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(StringUtils.isBlank(userId)){
			return returnStr;
		}
		long user_id = Long.parseLong(userId);
		//获取User下的群组
		List<Map<String,Object>> list = groupService.getUserGroup(user_id);
		if(null == list || 0 == list.size()){
			return  JsonUtils.writeJson(1, "获取成功,无群组", null, "object");
		}

		List<Map<String,Object>> resultList = new ArrayList<>();
		for(int i = 0;i < list.size();i++){
			Map<String,Object> result = new HashMap<String,Object>();
			long groupId = (long) list.get(i).get("group_id");
			result.put("groupId",groupId);
			result.put("groupName",list.get(i).get("group_name"));
			result.put("creatTime", list.get(i).get("create_time"));
			result.put("isAdmin",list.get(i).get("is_admin")+"");
			//获取groupId下的用户信息
			List<Map<String,Object>> userList = groupDetailsService.getUserGroupDetails(groupId);
			result.put("user",userList);
			resultList.add(result);
		}
		return  JsonUtils.writeJson(1, "获取成功", resultList, "object");
	}
	/**
	 * 用户退出群聊
	 * <p>Title: exitGroup</p>  
	 * <p>Description: </p>  
	 * @param userId
	 * @param groupId
	 * @return
	 */
	@RequestMapping(value="/open/exitGroup",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String exitGroup(@RequestParam String userId,@RequestParam String groupId,@RequestParam String isAdmin){
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(groupId)||StringUtils.isBlank(isAdmin)){
			return returnStr;
		}
		long user_id = Long.parseLong(userId);
		long group_id = Long.parseLong(groupId);
		int is_Admin = Integer.parseInt(isAdmin);


		GroupDetails groupDetails = groupDetailsService.getUserGroupDetailsIsAdmin(user_id,group_id,is_Admin);
		if(null == groupDetails){
			return JsonUtils.writeJson(0, 35, "权限错误");
		}
		Group group = groupService.getGroupById(group_id);
		if(null == group){
			return JsonUtils.writeJson(1, 0, "退出失败");
		}
		User user = userService.getUserById(user_id);
		if(null == user){
			return JsonUtils.writeJson(1, 0, "退出失败");
		}
		List<Map<String,Object>> userList = groupDetailsService.getUserGroupDetails(group_id);
		if(userList.size() ==  3){

			//删除这个群
			int i = groupService.deleteGroup(group_id);
			if(i > 0){
				ImUtils.deleteGroup(group.getIm_group_id());
				return  JsonUtils.writeJson(1, "退出成功", null, "object");
			}else {
				return JsonUtils.writeJson(1, 0, "退出失败");
			}
		}else if(userList.size() > 3){
			//删除用户在群中的信息
			if(is_Admin == 1){
				//作为管理员
				String userName =  groupDetailsService.deleteUserAdminFromGroup(user_id,group_id);
				if(null != userName){
					ImUtils.transferAdmin(group.getIm_group_id(),userName);
					return  JsonUtils.writeJson(1, "退出成功", null, "object");
				}else {
					return JsonUtils.writeJson(1, 0, "退出失败");
				}
			}else if(is_Admin == 0){
				//不作为管理员
				int i = groupDetailsService.deleteUserFromGroup(user_id,group_id);
				if(i > 0){
					ImUtils.delSingleMember(group.getIm_group_id(),user.getUser_name());
					return  JsonUtils.writeJson(1, "退出成功", null, "object");
				}else {
					return JsonUtils.writeJson(1, 0, "退出失败");
				}
			}
		}
		
		return JsonUtils.writeJson(0, 0, "未知错误");
	}
	/**
	 * 获取群聊中成员信息
	 * @Title:           getGroupDetailInfo
	 * @Description:     TODO
	 * @param:           @param userId
	 * @param:           @param groupId
	 * @param:           @return   
	 * @return:          String   
	 * @throws
	 */
	@RequestMapping(value="/open/getGroupDetailInfo",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getGroupDetailInfo(@RequestParam String userId,@RequestParam String groupId){
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(groupId)){
			return JsonUtils.writeJson(0, 0, "参数为空");
		}
		
		long user_id = Long.parseLong(userId);
		long group_id = Long.parseLong(groupId);
		
		User user = userService.getUserById(user_id);
		if(null == user){
			return JsonUtils.writeJson(0, 4, "用户不存在");	
		}
		Map<String,Object> result = new HashMap<String,Object>();
		//获取groupId下的用户信息
		List<Map<String,Object>> userList = groupDetailsService.getUserGroupDetails(group_id);

		return JsonUtils.writeJson(1, "获取成功", userList, "object");
	}

}
