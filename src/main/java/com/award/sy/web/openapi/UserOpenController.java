package com.award.sy.web.openapi;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.award.sy.entity.Wallet;
import com.award.sy.service.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.award.core.util.FileUtil;
import com.award.core.util.ImUtils;
import com.award.core.util.JsonUtils;
import com.award.sy.common.Constants;
import com.award.sy.common.PayCommonUtil;
import com.award.sy.entity.User;
import com.award.sy.entity.UserIndexImg;
import com.award.sy.service.FriendService;
import com.award.sy.service.UserIndexImgService;
import com.award.sy.service.UserService;

@Controller
public class UserOpenController {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private UserIndexImgService userIndexImgService;

    @Autowired
    private WalletService walletService;

    /**
     * 设置user信息
     * <p>Title: editUser</p>
     * <p>Description: </p>
     *
     * @param userId
     * @param headImg
     * @param userName
     * @param birth
     * @param height
     * @param sex
     * @return
     */
    @RequestMapping(value = "/open/editUser", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String editUser(@RequestParam String userId, @RequestParam String headImg, @RequestParam String userName, @RequestParam String birth, @RequestParam String height, @RequestParam String sex, HttpServletRequest request) {
        String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(headImg) || StringUtils.isBlank(userName) || StringUtils.isBlank(birth) || StringUtils.isBlank(height) || StringUtils.isBlank(sex)) {
            return returnStr;
        }
        long user_id = Long.parseLong(userId);
        User user = userService.getUserById(user_id);
        if (null == user) {
            return JsonUtils.writeJson(0, 4, "用户不存在");
        }
        //获取绝对路径
        String httpPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";

        String filePathName= request.getSession().getServletContext().getRealPath("/")+"/"+Constants.HEAD_IMG_PATH;;//存放路径
        System.out.println(filePathName);
        File file = new File(filePathName);
        if (!file.exists()) {
            file.mkdirs();
        }
        // System.out.println(filePathName);
        String fileName = System.currentTimeMillis() + String.valueOf((int) ((Math.random() * 9 + 1) * 100000)) + ".jpg";
        boolean isSuccess = FileUtil.CreateImgBase64(headImg, filePathName + fileName);
        if (!isSuccess) {
            return JsonUtils.writeJson(0, 24, "图片上传失败");
        }
        user.setBirth(birth);
        user.setHead_img(httpPath+Constants.HEAD_IMG_PATH  + fileName);
        user.setHeight(Integer.parseInt(height));
        user.setSex(Integer.parseInt(sex));
        user.setUser_id(user_id);
        user.setNick_name(userName);
        int i = userService.editUser(user);
        if (0 < i) {
            return JsonUtils.writeJson("修改成功", 1);
        }
        return JsonUtils.writeJson(0, 0, "修改失败");
    }

    /**
     * 新添加用户信息
     *
     * @param headImg
     * @param userName
     * @param birth
     * @param height
     * @param sex
     * @param type
     * @param phone
     * @param openId
     * @param request
     * @return
     */
    @RequestMapping(value = "/open/registUser", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String registUser(@RequestParam String headImg, @RequestParam String userName, @RequestParam String birth, @RequestParam String height, @RequestParam String sex, @RequestParam String type, @RequestParam(required = false) String phone, @RequestParam(required = false) String openId, HttpServletRequest request) {
        String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
        if (StringUtils.isBlank(headImg) || StringUtils.isBlank(userName) || StringUtils.isBlank(birth) || StringUtils.isBlank(height) || StringUtils.isBlank(sex)) {
            return returnStr;
        }


        if(Constants.LOGIN_TYPE_PHONE == Integer.parseInt(type)){
            //通过手机查找用户是否存在
            if(StringUtils.isBlank(phone)){
                return JsonUtils.writeJson(0, 0, "参数为空");
            }
            User user = userService.getUserByUserName(phone);
            if(null != user){
                return JsonUtils.writeJson(1, 4,"用户已存在");
            }
        }else if(Constants.LOGIN_TYPE_WECHAT == Integer.parseInt(type)){
            //判断参数
            if(StringUtils.isBlank(openId)){
                return JsonUtils.writeJson(0, 0, "参数为空");
            }
            User user = userService.getUserByUserName(openId);
            if(null != user){
                return JsonUtils.writeJson(0,4,"用户已存在");
            }
            //查询user是否存在
            User wechatUser = userService.getUserByOpenId(openId);
            if(null != wechatUser){
                return JsonUtils.writeJson(0,40,"微信已被绑定");
            }
        }

        //获取绝对路径
        String httpPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";

        String filePathName= request.getSession().getServletContext().getRealPath("/")+"/"+Constants.HEAD_IMG_PATH;;//存放路径
        System.out.println(filePathName);
        File file = new File(filePathName);
        if (!file.exists()) {
            file.mkdirs();
        }
       // System.out.println(filePathName);
        String fileName = System.currentTimeMillis() + String.valueOf((int) ((Math.random() * 9 + 1) * 100000)) + ".jpg";
        boolean isSuccess = FileUtil.CreateImgBase64(headImg, filePathName + fileName);
        if (!isSuccess) {
            return JsonUtils.writeJson(0, 24, "图片上传失败");
        }
        int login_type = Integer.parseInt(type);
        boolean addImSuccess = false;
        if (Constants.LOGIN_TYPE_PHONE == login_type) {
            addImSuccess = ImUtils.authRegister(phone, "123456", phone);
        } else if (Constants.LOGIN_TYPE_WECHAT == login_type) {
            addImSuccess = ImUtils.authRegister(openId, "123456", openId);
        } else {
            return JsonUtils.writeJson(0, 0, "参数为空");
        }
        if (!addImSuccess) {
            return JsonUtils.writeJson(0, 34, "注册失败");
        }
        User user = new User();
        user.setBirth(birth);
        user.setHead_img(httpPath+Constants.HEAD_IMG_PATH + fileName);
        user.setHeight(Integer.parseInt(height));
        user.setSex(Integer.parseInt(sex));
        user.setNick_name(userName);
        User userResult = null;
        if (Constants.LOGIN_TYPE_PHONE == login_type) {
            user.setUser_name(phone);
            userService.addNewUser(user);
            userResult = userService.getUserByUserName(phone);
        } else if (Constants.LOGIN_TYPE_WECHAT == login_type) {
            user.setOpen_id(openId);
            user.setUser_name(openId);
            userService.addNewUser(user);
            userResult = userService.getUserByUserName(openId);
        } else {
            return JsonUtils.writeJson(0, 0, "参数为空");
        }
        walletService.addNewUser(userResult.getUser_id());
        return JsonUtils.writeJson(1, "请求成功", userResult, "object");

    }

    /**
     * 用户上传主页头像
     *
     * @param userId
     * @param headImg
     * @param request
     * @return
     */
    @RequestMapping(value = "/open/uploadImg", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String uploadImg(@RequestParam String userId, @RequestParam String headImg, HttpServletRequest request) {
        String returnStr = JsonUtils.writeJson(0, 0, "参数为空");
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(headImg)) {
            return returnStr;
        }
        long user_id = Long.parseLong(userId);
        User userExist = userService.getUserById(user_id);
        if (null == userExist) {
            return JsonUtils.writeJson(0, 4, "用户不存在");
        }
        String path = Constants.USER_IMG_PATH;
        //获取绝对路径
       String uploadPath = request.getSession().getServletContext().getRealPath("/");
       // String uploadPath = "D:\\32+4";
        log.info("uploadPath路径：" + uploadPath);
        File file = new File(uploadPath + Constants.USER_IMG_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
       // String newimagepath=headImg.replaceAll("data:image/jpeg;base64,", "");
        String fileName = System.currentTimeMillis() + String.valueOf((int) ((Math.random() * 9 + 1) * 100000)) + ".jpg";
        log.info("fileName路径：" + fileName);
        boolean isSuccess = FileUtil.CreateImgBase64(headImg, uploadPath + Constants.USER_IMG_PATH + fileName);
        if (!isSuccess) {
            return JsonUtils.writeJson(0, 24, "图片上传失败");
        }
        int i = userIndexImgService.addUserIndexImg(user_id, Constants.CONTEXT_PATH+path + fileName);
        if (0 < i) {
            return JsonUtils.writeJson("上传成功", 1);
        } else {
            return JsonUtils.writeJson(0, 0, "上传失败");
        }
    }

    /**
     * 删除图片
     * @param userId
     * @param img
     * @return
     */
    @RequestMapping(value = "/open/deleteIndexImg", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String deleteIndexImg(@RequestParam String userId,@RequestParam String img){

        if(StringUtils.isBlank(userId)|| StringUtils.isBlank(img)){
            return JsonUtils.writeJson(0, 0, "参数为空");
        }
        User user = userService.getUserById(Long.parseLong(userId));
        if(null == user){
            return JsonUtils.writeJson(0,4,"用户不存在");
        }
        String[] imgpath = img.split(",");

        int i = userIndexImgService.delUserIndexImg(Long.parseLong(userId),imgpath);

        if(0 < i){
            return JsonUtils.writeJson("删除成功",1);
        }else{
            return JsonUtils.writeJson(0,41,"删除失败");
        }
    }
    /**
     * 获取用户主页信息
     *
     * @throws
     * @Title: getUserIndex
     * @Description: TODO
     * @param: @param userId
     * @param: @return
     * @return: String
     */
    @RequestMapping(value = "/open/getUserIndex", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getUserIndex(@RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return JsonUtils.writeJson(0, 0, "参数为空");
        }
        long user_id = Long.parseLong(userId);
        User user = userService.getUserById(user_id);
        if (null == user) {
            return JsonUtils.writeJson(0, 4, "用户不存在");
        }
        return JsonUtils.writeJson(1, "请求成功", user, "object");

    }

    /**
     * 获取用户主页信息图片
     *
     * @throws
     * @Title: getUserIndex
     * @Description: TODO
     * @param: @param userId
     * @param: @return
     * @return: String
     */
    @RequestMapping(value = "/open/getUserIndexImg", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getUserIndexImg(@RequestParam String userId, @RequestParam String start, @RequestParam String count) {
        if (StringUtils.isBlank(userId)) {
            return JsonUtils.writeJson(0, 0, "参数为空");
        }
        List<Map<String, Object>> list = userIndexImgService.getUserIndexImgList(Long.parseLong(userId), start, count);

        return JsonUtils.writeJson(1, "请求成功", list, "object");

    }

    /**
     * 绑定提现账号
     * @param userId
     * @param openId
     * @return
     */
    @RequestMapping(value = "/open/bindWithdraw", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String bindWithdraw(@RequestParam String userId, @RequestParam String openId) {
        if (StringUtils.isBlank(userId)||StringUtils.isBlank(openId)) {
            return JsonUtils.writeJson(0, 0, "参数为空");
        }
       boolean isBindSuccess = userService.editUserBindWeChat(Long.parseLong(userId),Long.parseLong(openId));
        if(true == isBindSuccess){
            return JsonUtils.writeJson("请求成功",1);
        }else{
            return JsonUtils.writeJson(0,41,"提现账号绑定失败");
        }
    }

}
