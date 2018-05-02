package com.award.sy.common.task;

import com.award.sy.common.Constants;
import com.award.sy.entity.RedPacket;
import com.award.sy.entity.Wallet;
import com.award.sy.entity.WalletLog;
import com.award.sy.entity.WalletRecord;
import com.award.sy.service.RedPacketService;
import com.award.sy.service.WalletLogService;
import com.award.sy.service.WalletRecordService;
import com.award.sy.service.WalletService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 
 * @描述：任务调度管理类
 * @作者：cyh
 * @版本：V1.0
 * @创建时间：：2016-11-21 下午10:54:10
 *
 */
@Component
public class TaskManager {

	@Resource
	private RedPacketService redPacketService;

	@Resource
	private WalletService walletService;


	@Scheduled(cron="0 0 2 * * ? ") //间隔5秒执行
	public void taskCycle(){
		final List<RedPacket> list = redPacketService.getExpiredRedPacket();
		if(null != list && list.size() > 0){
			for(int i = 0;i< list.size();i++){
				RedPacket redPacket = list.get(i);

				Wallet wallet = walletService.findWalletByUserId(redPacket.getPublish_id());
				Double total_moeny = wallet.getMoney()+redPacket.getMoney();
				redPacketService.editRedPacketPayStatus(redPacket.getRecord_sn(),Constants.PAY_STATUS_BACK);
				walletService.refund(redPacket.getRecord_sn(),redPacket.getPublish_id(), Constants.LOG_REFUND_READPACKET,redPacket.getMoney(),total_moeny);
			}
		}
		System.out.println("专注于前端开发技术和研究的技术博客</span>");
	}
	@Scheduled(cron = "0/10 * * * * ?")
	public void task1(){
		System.out.println("我每10分钟都要执行一次，不管是刮风还是下雨");
	}
	
	@Scheduled(cron = "0 5 0 * * ?")
	public void task2(){
		System.out.println("我每天凌晨两点执行");
	}
	
	@Scheduled(cron = "0 0 0 1 1 3")
	public void task3(){
		System.err.println("如果现在是2016年的话，我会在未来的3年后也就是2019年一月一日凌晨0点钟执行，且只一次，假如错过了那么我将永远失去执行的机会");
	}
	
	@Scheduled(cron = "0 0 0 1 1 3/4")
	public void task4(){
		System.err.println("如果现在是2016年的话，我会在未来的3年后也就是2019年一月一日凌晨0点钟执行，假如错过了我会等待一年然后再执行一次");
	}
	
}
