package com.award.sy.service;

import java.util.List;
import java.util.Map;

import com.award.sy.entity.Location;
import com.award.sy.entity.bean.QueryCondition;
import com.award.sy.web.view.DatatablesView;

public interface LocationService {

	
	/**
	 * 功能描述：获取所有位置信息
	 * @return
	 */
	public List<Location> getAllLocation();
	
	/**
	 * 功能描述：根据条件获取位置信息
	 * @return
	 */
	public DatatablesView<Location> getLocationByCondition(QueryCondition query);
	
	/**
	 * 功能描述：根据ID获取位置信息
	 * @param AreaId
	 * @return
	 */
	public Location getLocationById(long locationId);
	
	public List<Map<String,Object>> getLocationByLatLng(Map<String,double[]> map,long userId);
	
	public List<Map<String,Object>> getLimitLocationByLatLng(double lat,double lng,int start,int count,long userId);
	
	/**
	 * 功能描述：添加位置信息
	 * @param Location
	 * @return
	 */
	public int addLocation(Location location);
	
	/**
	 * 功能描述：修改位置信息
	 * @param Location
	 * @return
	 */
	public int editLocation(Location location);
	
	/**
	 * @功能描述：删除位置信息
	 * @param locationId
	 * @return int
	 */
	public int removeLocation(long locationId);

	/**
	 * 通过id找到定位信息
	 * @param l
	 * @return
	 */
	Location getLocationByUserId(long l);
}
