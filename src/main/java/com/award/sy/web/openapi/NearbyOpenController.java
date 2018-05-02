package com.award.sy.web.openapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.award.sy.entity.Location;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.award.core.util.JsonUtils;
import com.award.core.util.MapUtil;
import com.award.sy.entity.User;
import com.award.sy.service.LocationService;
import com.award.sy.service.UserService;


@Controller
public class NearbyOpenController {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Resource
	private UserService userService;
	
	@Resource
	private LocationService locationService;
	
	/**
	 * 查询附近的人
	 * 
	 * @param request
	 * @param query
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/open/getNearBy", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getNearBy(HttpServletRequest request) {
		String lat = request.getParameter("lat");
		String lng = request.getParameter("lng");
		String distance = request.getParameter("distance");
		String userId = request.getParameter("userId");
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(!StringUtils.isBlank(userId) && !StringUtils.isBlank(lat) && !StringUtils.isBlank(lng) && !StringUtils.isBlank(distance)) {
			User user = userService.getUserById(Long.parseLong(userId));
		    if(user != null) {
		    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			   if(user.getInvisible() == 1) {//如果隐身了
				   returnStr = JsonUtils.writeJson(1, "获取成功", list, "object");
			   }else {
				    double latitude = Double.parseDouble(lat);
				    double longitude = Double.parseDouble(lng);
				    double dis = Double.parseDouble(distance);
				    Map<String,double[]> map = MapUtil.returnLLSquarePoint(longitude, latitude, dis);
				    list = locationService.getLocationByLatLng(map,Long.parseLong(userId));		
				    returnStr = JsonUtils.writeJson(1, "获取成功", list, "object");
			   }
		    }else {
		    	returnStr = JsonUtils.writeJson(0, 4, "用户不存在");			
		    }
		}
		return returnStr;
	}
	
	@RequestMapping(value = "/open/getAllNearBy", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getAllNearBy(HttpServletRequest request) {
		String lat = request.getParameter("lat");
		String lng = request.getParameter("lng");
		String userId = request.getParameter("userId");
		String start = request.getParameter("start");
		String count = request.getParameter("count");
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(!StringUtils.isBlank(userId) && !StringUtils.isBlank(lat) && !StringUtils.isBlank(lng) && !StringUtils.isBlank(start) && !StringUtils.isBlank(count)) {
			User user = userService.getUserById(Long.parseLong(userId));
		    if(user != null) {
		    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			   if(user.getInvisible() == 1) {//如果隐身了
				   returnStr = JsonUtils.writeJson(1, "获取成功", list, "object");
			   }else { 
				    list = locationService.getLimitLocationByLatLng(Double.parseDouble(lat), Double.parseDouble(lng), Integer.parseInt(start), Integer.parseInt(count), Long.parseLong(userId));
				    returnStr = JsonUtils.writeJson(1, "获取成功", list, "object");
			   }
		    }else {
		    	returnStr = JsonUtils.writeJson(0, 4, "用户不存在");			
		    }
		}
		return returnStr;
	}
	
	@RequestMapping(value = "/open/setInvisible", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String setInvisible(HttpServletRequest request) {
		String userId =  request.getParameter("userId");
		String invisible = request.getParameter("invisible");
		String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
		if(!StringUtils.isBlank(userId) && !StringUtils.isBlank(invisible)) {
			User user = userService.getUserById(Long.parseLong(userId));
		    if(user != null) {
			    user.setInvisible(Integer.parseInt(invisible));
			    userService.editUser(user);
			    returnStr = JsonUtils.writeJson("设置成功", 1);
		    }else {
		    	returnStr = JsonUtils.writeJson(0, 4, "用户不存在");			
		    }
		   }
		return returnStr;
	}

	@RequestMapping(value = "/open/uploadLocation", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String uploadLoaction(@RequestParam String userId, @RequestParam String lat,@RequestParam String lng){
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(lat)||StringUtils.isBlank(lng)){
			return JsonUtils.writeJson(0, 0, "参数为空");
		}
		User user = userService.getUserById(Long.parseLong(userId));
		if(null == user){
			return JsonUtils.writeJson(0, 4, "用户不存在");
		}
		Location location = locationService.getLocationByUserId(Long.parseLong(userId));
		int i = 0;
		if(null == location){
			Location newLocation = new Location();
			newLocation.setLng(Double.parseDouble(lng));
			newLocation.setLat(Double.parseDouble(lat));
			newLocation.setUser_id(Long.parseLong(userId));
			i = locationService.addLocation(newLocation);
		}else{
			location.setLng(Double.parseDouble(lng));
			location.setLat(Double.parseDouble(lat));
			i = locationService.editLocation(location);
		}
		if(0 < i){
			return JsonUtils.writeJson("设置成功", 1);
		}else{
			return JsonUtils.writeJson(0, 43, "定位上传失败");
		}
	}
}
