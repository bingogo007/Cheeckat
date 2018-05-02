package com.award.sy.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.award.core.beans.WherePrams;
import com.award.core.sql.where.C;
import com.award.sy.dao.MissionDao;
import com.award.sy.entity.Mission;
import com.award.sy.entity.bean.QueryCondition;
import com.award.sy.service.MissionService;
import com.award.sy.web.view.DatatablesView;

@Service("missionService")
public class MissionServiceImpl implements MissionService{
	
	@Resource
	private MissionDao missionDao;
	
	public DatatablesView<Mission> getMissionByCondition(QueryCondition query) {
		DatatablesView<Mission> dataView = new DatatablesView<Mission>();
		
		//构建查询条件
		WherePrams where = missionDao.structureConditon(query);
		
		Long count = missionDao.count(where);
		List<Mission> list = missionDao.list(where);
		
		dataView.setRecordsTotal(count.intValue());
		dataView.setData(list);
		
		return dataView;
	}
	
	public List<Mission> getAllMission(){
		return missionDao.list();
	}
	
	public Mission getMissionById(long missionId){
		return missionDao.get(missionId);
	}
	
	public Mission getMissionByPubIdAndCreateTime(long userId,String createTime,String content,String startTime) {
		WherePrams where = new WherePrams();
		where.and("publish_id", C.EQ, userId);
		where.and("create_time", C.EQ, createTime);
		where.and("start_time", C.EQ, startTime);
		where.and("content", C.EQ, content);
		return missionDao.get(where);
	}
	
	public Mission getMissionByRecordSN(String recordSN) {
		WherePrams where = new WherePrams();
		where.and("record_sn", C.EQ, recordSN);
		return missionDao.get(where);
	}
	
	public int addMission(Mission mission){
		return missionDao.addLocal(mission);
	}
	
	public int editMission(Mission mission){
		WherePrams where = new WherePrams();
		where.and("mission_id", C.EQ, mission.getMission_id());
		return missionDao.updateLocal(mission,where);
	}
	
	public List<Mission> getExpiredMission() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		WherePrams where = new WherePrams();
		where.and("start_time", C.IXAO, sdf.format(date));
		where.and("status", C.NE, 4);
		return missionDao.list(where);		
	}
	
	public int removeMission(long missionId){
		return missionDao.del(missionId);
	}

	@Override
	public List<Map<String,Object>> getAllMissionLimit(int start,int count) {
		List<Map<String,Object>> list = missionDao.listBySql("select a.*,b.head_img,b.birth,b.user_name,b.sex as user_sex,b.country,b.invisible from tb_mission a,tb_user b where a.publish_id = b.user_id and a.status=0 and a.to_type = 0 order by money desc limit "+start+","+count);
//		WherePrams where = new WherePrams();
//		where.and("status", C.EQ, 0);
//		where.and("to_user", C.EQ, 0);
//		where.limit(start, count);
//		where.orderBy("money DESC");
//		return missionDao.list(where);
		return list;
	}

	@Override
	public List<Map<String,Object>> getMyMission(Long userId) {
		List<Map<String,Object>> list = missionDao.listBySql("select a.*,b.head_img,b.birth,b.user_name,b.sex as user_sex,b.country,b.invisible from tb_mission a,tb_user b where a.publish_id = b.user_id and (a.publish_id = "+userId+" or a.accept_id = "+userId+" or (a.to_id = "+userId+" and a.to_type <> 2)) and a.status<>5 order by create_time desc");
//		WherePrams where = new WherePrams();
//		where.orStart();
//		where.or("publish_id", C.EQ, userId);
//		where.or("accept_id", C.EQ, userId);
//		where.or("to_user", C.EQ, userId);
//		where.orEnd();
//		where.orderBy("create_time DESC");
//		return missionDao.list(where);
		return list;
	}


}
