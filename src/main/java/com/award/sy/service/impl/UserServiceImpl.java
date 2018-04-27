package com.award.sy.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.cache.decorators.WeakCache;
import org.springframework.stereotype.Service;

import com.award.core.beans.WherePrams;
import com.award.core.sql.where.C;
import com.award.sy.common.Constants;
import com.award.sy.common.DateUtil;
import com.award.sy.dao.UserDao;
import com.award.sy.entity.User;
import com.award.sy.entity.bean.QueryCondition;
import com.award.sy.service.UserService;
import com.award.sy.web.view.DatatablesView;

/**
 * @描述：用户信息service
 * @作者：bin
 * @版本：V1.0
 * @创建时间：：2018-04-10 下午3:42:45
 */
@Service("userService")
public class UserServiceImpl implements UserService{
	
	@Resource
	private UserDao userDao;
	
	public DatatablesView<User> getUserByCondition(QueryCondition query) {
		DatatablesView<User> dataView = new DatatablesView<User>();
		
		//构建查询条件
		WherePrams where = userDao.structureConditon(query);
		
		Long count = userDao.count(where);
		List<User> list = userDao.list(where);
		
		dataView.setRecordsTotal(count.intValue());
		dataView.setData(list);
		
		return dataView;
	}
	
	public List<User> getAllUser(){
		return userDao.list();
	}
	
	public User getUserById(long userId){
		return userDao.get(userId);
	}
	
	public int addUser(User user){
		return userDao.addLocal(user);
	}
	
	public int editUser(User user){
		WherePrams where = new WherePrams();
		where.and("user_id", C.EQ, user.getUser_id());
		return userDao.updateLocal(user,where);
	}
	
	public int removeUser(long userId){
		return userDao.del(userId);
	}
	/**
	 * 添加新的用户
	 * Title: addNewUserInfo
	 * Description: 
	 * @param id
	 * @param type
	 * @return
	 * @see com.award.sy.service.UserService#addNewUserInfo(java.lang.String, int)
	 *//*
	@Override
	public int addNewUserInfo(String id, int type) {
		User user = new User();
		user.setCreate_time(DateUtil.getNowTime());
		user.setUser_name(id);
		 if(Constants.LOGIN_TYPE_WECHAT == type){
			user.setOpen_id(id);
		}
		return userDao.addLocal(user);
	}*/
	/**
	 * 通过手机号获取用户信息
	 * Title: getUserByPhone
	 * Description: 
	 * @param phone
	 * @return
	 * @see com.award.sy.service.UserService#getUserByPhone(java.lang.String)
	 *//*
	@Override
	public User getUserByPhone(String phone) {
		WherePrams where = new WherePrams();
		where.and("phone", C.EQ, phone);
		return userDao.get(where);
	}
	*//**
	 * 通过微信open_id获取用户信息
	 * Title: getUserByWxOpenId
	 * Description: 
	 * @param phone
	 * @return
	 * @see com.award.sy.service.UserService#getUserByWxOpenId(java.lang.String)
	 *//*
	@Override
	public User getUserByWxOpenId(String openId) {
		WherePrams where = new WherePrams();
		where.and("open_id", C.EQ, openId);
		return userDao.get(where);
	}*/
	/**
	 * 添加新的用户
	 * Title: addNewUser
	 * Description: 
	 * @param user
	 * @return
	 * @see com.award.sy.service.UserService#addNewUser(com.award.sy.entity.User)
	 */
	@Override
	public int addNewUser(User user) {
		return userDao.addLocal(user);
	}

	@Override
	public User getUserByUserName(String userName) {
		// TODO Auto-generated method stub
		WherePrams where = new WherePrams();
		where.and("user_name", C.EQ, userName);
		return userDao.get(where);
	}

	


}
