package com.award.sy.web.openapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import com.award.core.util.ImUtils;
import com.award.sy.entity.*;
import com.award.sy.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.award.core.util.JsonUtils;
import com.award.sy.common.Constants;
import com.award.sy.common.PayCommonUtil;
import com.award.sy.common.WxPayUtil;

@Controller
public class RedPacketOpenController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletRecordService walletRecordService;

    @Autowired
    private RedPacketService redPacketService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private GroupService groupService;


    /**
     * 抢红包
     *
     * @throws
     * @Title: getRedPacket
     * @Description: TODO
     * @param: @param user_id
     * @param: @param redpacket_id
     * @param: @return
     * @return: String
     */
    @RequestMapping(value = "/open/getRedPacket", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String userGetRedPacket(@RequestParam String userId,
                               @RequestParam String redpacketId) {
        String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(redpacketId)) {
            return returnStr;
        }
        // 查询领取的用户信息
        User user = userService.getUserById(Long.parseLong(userId));
        if (null == user) {
            return JsonUtils.writeJson(0, 4, "用户不存在");
        }
        // 查询红包信息
        RedPacket redPacket = redPacketService.getRedPacketById(Long.parseLong(redpacketId));
        if (null == redPacket) {
            return JsonUtils.writeJson(0, 16, "红包id不存在");
        }
        WalletRecord walletRecord = walletRecordService.getWallerOrderByRecordSN(redPacket.getRecord_sn());
        System.out.println("wallerTecord "+walletRecord.toString());
        if (walletRecord.getMoney().compareTo(redPacket.getMoney()) != 0) {
            return JsonUtils.writeJson(0, 18, "红包领取失败");
        }
        if (redPacket.getStatus() == Constants.FETCH_SUCCESS) {
            if (redPacket.getPublish_id() == Long.parseLong(userId)) {
                //查询被领取人的信息
                User acceptUser = userService.getUserById(redPacket.getAccept_id());
                Map<String, Object> map = new HashMap<>();
                map.put("user", acceptUser);
                map.put("redpacket", redPacket);
                return JsonUtils.writeJson(1, "请求成功", map, "object");
            }
            return JsonUtils.writeJson(0, 17, "红包被领取");
        }
        if (redPacket.getPublish_id() == Long.parseLong(userId)) {
            return JsonUtils.writeJson(0, 36, "不能领取自己的红包");
        }
        //更新红包状态
        boolean isRedPacket = redPacketService.editRedPacketFetchStatus(redPacket.getRecord_sn(), Long.parseLong(userId), Constants.FETCH_SUCCESS);
        if (false == isRedPacket) {
            return JsonUtils.writeJson(0, 18, "红包领取失败");
        }
        //更新状态
        Wallet wallet = walletService.findWalletByUserId(Long.parseLong(userId));
        Double total_fee = wallet.getMoney() + redPacket.getMoney();
        boolean isWalletSuccess = walletService.editUserWalletFetchBalance(redPacket.getRecord_sn(), Long.parseLong(userId), Constants.LOG_FETCH_REDPACKET, redPacket.getMoney(), total_fee);
        if (false == isWalletSuccess) {
            return JsonUtils.writeJson(0, 18, "红包领取失败");
        }
        List<Map<String, Object>> list = redPacketService.getRedPacketAndUserInfoByRedPacketId(redpacketId);
        if (null != list) {
            return JsonUtils.writeJson(1, "领取成功", list, "object");
        } else {
            return JsonUtils.writeJson(0, 18, "红包领取失败");
        }
    }

    /**
     * 支付红包
     *
     * @throws
     * @Title: payRedPacket
     * @Description: TODO
     * @param: @param userId
     * @param: @param payType
     * @param: @param money
     * @param: @param type
     * @param: @param request
     * @param: @return
     * @return: String
     */
    @RequestMapping(value = "/open/payRedPacket", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String payRedPacket(@RequestParam String userId,
                               @RequestParam String payType, @RequestParam String money,
                               @RequestParam String type, @RequestParam String to, @RequestParam String to_id, HttpServletRequest request) {
        System.out.println("进入");
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(payType)
                || StringUtils.isBlank(money) || StringUtils.isBlank(type) || StringUtils.isBlank(to) || StringUtils.isBlank(to_id)) {
            return JsonUtils.writeJson(0, 0, "参数错误");
        }
        String result = JsonUtils.writeJson(0, 0, "参数错误");
        int pay_type = Integer.parseInt(payType);
        int taskType = Integer.parseInt(type);
        long user_id = Long.parseLong(userId);
        Double price = Double.parseDouble(money);
        User user = userService.getUserById(user_id);
        if (null == user) {
            return JsonUtils.writeJson(0, 4, "用户不存在");
        }
        if (Constants.PAY_TYPE_WECHAT == pay_type) {
            if (Constants.ORDER_TYPE_REDPACKET == taskType) {
                //微信支付发红包
                String record_sn = walletRecordService.addWalletRecordOrder(user_id, money, Constants.PAY_TYPE_WECHAT, Constants.ORDER_TYPE_REDPACKET);
                if (null == record_sn) {
                    return JsonUtils.writeJson(0, 19, "订单生成失败");
                }
                //生成红包
                boolean isRedPacketSuccess = redPacketService.addRedpacketRecord(record_sn, userId, money, to, to_id);
                if (false == isRedPacketSuccess) {
                    return JsonUtils.writeJson(0, 22, "红包发送失败");
                }
                //请求微信prepay发送给手机
                SortedMap<Object, Object> map = WxPayUtil.getPreperIdFromWX(record_sn, PayCommonUtil.getIpAddress(request), Constants.APP_NAME + Constants.REDPACKET, price);
                if (null == map) {
                    return JsonUtils.writeJson(0, 19, "订单生成失败");
                }
                return JsonUtils.writeJson(1, "请求成功", map, "object");
            }
        } else if (Constants.PAY_TYPE_BALANCE == pay_type) {
            if (Constants.ORDER_TYPE_REDPACKET == taskType) {
                Wallet wallet = walletService.findWalletByUserId(user_id);
                if (null == wallet) {
                    return JsonUtils.writeJson(0, 0, "参数错误");
                }
                //余额支付发红包
                if (wallet.getMoney().compareTo(new Double(money)) < 0) {
                    return JsonUtils.writeJson(0, 21, "余额不足");
                }
                //生成订单
                String record_sn = walletRecordService.addWalletRecordOrder(user_id, money, Constants.PAY_TYPE_BALANCE, Constants.ORDER_TYPE_REDPACKET);
                if (null == record_sn) {
                    return JsonUtils.writeJson(0, 22, "红包发送失败");
                }
                //生成红包，待支付
                boolean isRedPacketSuccess = redPacketService.addRedpacketRecord(record_sn, userId, money, to, to_id);
                if (false == isRedPacketSuccess) {
                    return JsonUtils.writeJson(0, 22, "红包发送失败");
                }
                //修改金额,更新订单支付状态，插入余额记录
                Double total_fee = wallet.getMoney() - Double.parseDouble(money);
                String changemoney = "-" + money;
                boolean isWalletSuccess = walletService.editUserWalletPayBalance(record_sn, user_id, Constants.LOG_AWARD_REDPACKET, Double.parseDouble(changemoney), total_fee);
                if (false == isWalletSuccess) {
                    return JsonUtils.writeJson(0, 22, "红包发送失败");
                } else {
                    System.out.println(record_sn);
                    //修改红包支付状态
                    redPacketService.editRedPacketPayStatus(record_sn,Constants.PAY_STATUS_SUCCESS);
                    //判断to类型是群发还是个人红包
                    RedPacket redPacket = redPacketService.getRedPacketByRecordSN(record_sn);
                    User fromUser = userService.getUserById(user_id);
                    //个人,直接获取个人Id并发送至环信
                    if (Constants.TO_TYPE_PRIVATE == redPacket.getTo_type()) {
                        User toUser = userService.getUserById(redPacket.getTo_id());
                        ImUtils.sendTextMessage("users", new String[]{toUser.getUser_name()}, "WtwdRedPacketTxt:好友" + fromUser.getNick_name()  + "发布了一个红包，点击查看:" + redPacket.getRedpacket_id());
                        ImUtils.sendTextMessage("users", new String[]{fromUser.getUser_name()}, "WtwdRedPacketTxt:" + fromUser.getNick_name()  + "发布了一个红包，点击查看:" + redPacket.getRedpacket_id());
                    } else if (Constants.TO_TYPE_GROUP == redPacket.getTo_type()) {
                        //群发，获取群成员的名称，并发送
                        Group group = groupService.getGroupById(redPacket.getTo_id());
                        if (null !=group) {
                            ImUtils.sendTextMessage("chatgroups", new String[]{group.getIm_group_id()}, "WtwdRedPacketTxt:好友" + fromUser.getNick_name()  + "发布了一个红包，点击查看:" + redPacket.getRedpacket_id());

                        }
                    }
                    return JsonUtils.writeJson("红包发送成功", 1);
                }
            }
        }
        return JsonUtils.writeJson(0, 0, "参数错误");
    }
}
