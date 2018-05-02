package com.award.sy.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.award.core.beans.WherePrams;
import com.award.core.sql.where.C;
import com.award.sy.dao.LocationDao;
import com.award.sy.entity.Location;
import com.award.sy.entity.bean.QueryCondition;
import com.award.sy.service.LocationService;
import com.award.sy.web.view.DatatablesView;

@Service("locationService")
public class LocationServiceImpl implements LocationService{
	
	@Resource
	private LocationDao locationDao;
	
	public DatatablesView<Location> getLocationByCondition(QueryCondition query) {
		DatatablesView<Location> dataView = new DatatablesView<Location>();
		
		//构建查询条件
		WherePrams where = locationDao.structureConditon(query);
		
		Long count = locationDao.count(where);
		List<Location> list = locationDao.list(where);
		
		dataView.setRecordsTotal(count.intValue());
		dataView.setData(list);
		
		return dataView;
	}
	
	public List<Location> getAllLocation(){
		return locationDao.list();
	}
	
	public Location getLocationById(long locationId){
		return locationDao.get(locationId);
	}
	
	public int addLocation(Location location){
		return locationDao.addLocal(location);
	}
	
	public int editLocation(Location location){
		WherePrams where = new WherePrams();
		where.and("location_id", C.EQ, location.getLoc_id());
		return locationDao.updateLocal(location,where);
	}
	
	public int removeLocation(long locationId){
		return locationDao.del(locationId);
	}

	@Override
	public Location getLocationByUserId(long l) {
		WherePrams wherePrams = new WherePrams();
		wherePrams.and("user_id",C.EQ,l);
		return locationDao.get(wherePrams);
	}

	@Override
	public List<Map<String,Object>> getLocationByLatLng(Map<String,double[]> map,long userId) {
		List<Map<String,Object>> list = locationDao.listBySql("select a.*,b.head_img,b.birth,b.user_name,b.sex,b.country,b.invisible from tb_location a, tb_user b where a.user_id = b.user_id and a.user_id <> "+userId+" and a.lat > "+ map.get("rightBottomPoint")[0]+" and a.lat < "+ map.get("leftTopPoint")[0] + " and a.lng > "+map.get("leftTopPoint")[1]+" and a.lng < "+map.get("rightBottomPoint")[1]);
		return list;
	}

	@Override
	public List<Map<String,Object>> getLimitLocationByLatLng(double lat, double lng,
			int start, int count, long userId) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> list = locationDao.listBySql("select a.*,b.head_img,b.birth,b.user_name,b.sex,b.country,b.invisible,(POWER(MOD(ABS(lng - "+lng+"),360),2) + POWER(ABS(lat - "+lat+"),2)) AS distance from tb_location a, tb_user b where a.user_id = b.user_id and a.user_id <> "+userId+" ORDER BY distance LIMIT "+start+","+count);
		//SELECT lng,lat, (POWER(MOD(ABS(lng - $lng),360),2) + POWER(ABS(lat - $lat),2)) AS distance FROM `user_location` ORDER BY distance LIMIT 100 
		return list;
	}

}

