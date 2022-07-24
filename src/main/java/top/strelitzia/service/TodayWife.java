package top.strelitzia.service;


import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.strelitzia.dao.AdminUserMapper;
import top.strelitzia.dao.GroupAdminInfoMapper;
import top.strelitzia.dao.UserFoundMapper;
import top.strelitzia.model.AdminUserInfo;
import top.strelitzia.model.UserFoundInfo;
import top.strelitzia.util.AdminUtil;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * @author Cuthbert
 * @Date 2022/6/21 22:56
 **/
@Service
public class TodayWife {

    private static final org.slf4j.Logger log
            = org.slf4j.LoggerFactory.getLogger(TodayWife.class);

    @Autowired
    private UserFoundMapper userFoundMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    /*编辑指令用法
    * *所有功能都需要在最前面加上bot名今日老婆，且需要有step权限
    * 重置：将你的wife以及wife的群号设置为0
    * qq号：将你的wife更改为输入的qq号，wife的群号自动填充当前群号
    * 重置+qq号：将指定qq的wife以及wife的群号设置为0
    * qq号+qq号：指定你输入的第一个qq，将他的wife更改为你输入的第二个qq，wife的群号自动填充当前群号
    * qq号+qq号+qq号：指定你输入的第一个qq，将他的wife更改为你输入的第二个qq，wife的群号更改为你输入的第三个q号
    * */
    @AngelinaFriend(keyWords = "老婆")
    @AngelinaGroup(keyWords = "今日老婆" , description = "从本群中随机挑选一位幸运群友作为你今日的老婆")
    public ReplayInfo todayWife(MessageInfo messageInfo) {
        //准备工作
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        StringBuilder reply = new StringBuilder("*警告：请勿用此功能刷屏*\n" + messageInfo.getName() + "，\n");
        UserFoundInfo userFoundInfo = userFoundMapper.selectUserFoundByQQ(messageInfo.getQq());
        if (userFoundInfo == null) {
            userFoundInfo = new UserFoundInfo();
            userFoundInfo.setQq(messageInfo.getQq());
            userFoundInfo.setFoundCount(0);
            userFoundInfo.setTodayCount(0);
            userFoundInfo.setTodayWife(0L);
            userFoundInfo.setWifeGroup(0L);
        }
        //判断是否为编辑指令
        if (messageInfo.getArgs().size() == 2) {
            String str = messageInfo.getArgs().get(1);
            if (str.equals("菜单")){
                reply = new StringBuilder(
                        "编辑指令用法\n" +
                                "    *所有功能都需要在最前面加上bot名今日老婆\n" +
                                "    *所有功能仅有群主和管理员，或step权限者才能使用\n" +
                                "    重置：将你的wife以及wife的群号设置为0\n" +
                                "    qq号：将你的wife更改为输入的qq号，wife的群号自动填充当前群号\n" +
                                "    重置+qq号：将指定qq的wife以及wife的群号设置为0\n" +
                                "    qq号+qq号：指定你输入的第一个qq，将他的wife更改为你输入的第二个qq，wife的群号自动填充当前群号\n" +
                                "    qq号+qq号+qq号：指定你输入的第一个qq，将他的wife更改为你输入的第二个qq，wife的群号更改为你输入的第三个q号\n" +
                                "eg.稀音今日老婆 重置 11111\n   稀音今日老婆 11111 22222"
                );
            }else {
                List<AdminUserInfo> admins = adminUserMapper.selectAllAdmin();
                boolean b = AdminUtil.getStepAdmin(messageInfo.getQq(), admins);
                if (b || !messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
                    if (str.equals("重置") || str.equals("reset")) {
                        userFoundMapper.updateTodayWifeByQq(messageInfo.getQq(), 0L, 0L);
                        reply.append("你的今日老婆已经重置啦");
                    } else if (str.equals("重置本群")) {
                        userFoundMapper.resetGroupWife(messageInfo.getGroupId());
                        reply = new StringBuilder("已重置本群");
                    } else {
                        Long qq = Long.valueOf(str);
                        userFoundMapper.updateTodayWifeByQq(messageInfo.getQq(), qq, messageInfo.getGroupId());
                        reply = new StringBuilder("设置完成");
                    }
                } else {
                    reply = new StringBuilder("您没有权限编辑哦");
                }
            }

        } else if (messageInfo.getArgs().size() == 3) {
            List<AdminUserInfo> admins = adminUserMapper.selectAllAdmin();
            boolean b = AdminUtil.getStepAdmin(messageInfo.getQq(), admins);
            if (b || !messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
                String str1 = messageInfo.getArgs().get(1);
                String str2 = messageInfo.getArgs().get(2);
                if (str1.equals("重置") || str1.equals("reset")){
                    Long qq = Long.valueOf(str2);
                    userFoundMapper.updateTodayWifeByQq(qq, 0L, 0L);
                    reply = new StringBuilder("重置完成");
                }else {
                    Long qq = Long.valueOf(str1);
                    Long wife = Long.valueOf(str2);
                    userFoundMapper.updateTodayWifeByQq(qq, wife, messageInfo.getGroupId());
                    reply = new StringBuilder("设置完成");
                }
            } else {
                reply = new StringBuilder("您没有权限编辑哦");
            }

        } else if (messageInfo.getArgs().size() == 4) {
            List<AdminUserInfo> admins = adminUserMapper.selectAllAdmin();
            boolean b = AdminUtil.getStepAdmin(messageInfo.getQq(), admins);
            if (b) {
                Long qq = Long.valueOf(messageInfo.getArgs().get(1));
                Long wife = Long.valueOf(messageInfo.getArgs().get(2));
                Long group = Long.valueOf(messageInfo.getArgs().get(3));
                userFoundMapper.updateTodayWifeByQq(qq, wife, group);
                reply = new StringBuilder("设置完成");
            } else {
                reply = new StringBuilder("您没有权限编辑哦");
            }


        }else {
            //查询今日是否抽取过老婆
            Long today = userFoundInfo.getTodayWife();
            Long group = userFoundInfo.getWifeGroup();
            if (group.equals(messageInfo.getGroupId()) || group.equals(0L)) {
                Long qq = null;
                if (group.equals(0L)) {
                    //今日未抽取
                    //获取MemberList并通过stream流提取qq，随机抽取一个
                    ContactList<NormalMember> qqList = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).getMembers();
                    List<Long> list = qqList.stream().map(Member::getId).collect(Collectors.toList());
                    list.add(messageInfo.getLoginQq());
                    Random random = new Random();
                    int n = random.nextInt(list.size());
                    qq = list.get(n);
                    reply.append("你今天的老婆是：");
                    userFoundMapper.updateTodayWifeByQq(messageInfo.getQq(), qq, messageInfo.getGroupId());
                } else if (group.equals(messageInfo.getGroupId())) {
                    //在本群抽取过了
                    qq = userFoundInfo.getTodayWife();
                    reply.append("你今天已经抽取过老婆啦！" + "你的老婆是：");
                }
                replayInfo.setReplayImg("http://q.qlogo.cn/headimg_dl?dst_uin=" + qq + "&spec=5");
                String name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(qq).getNameCard();
                if (name.isEmpty()) {
                    name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(qq).getRemark();
                }
                reply.append(name + "（" + qq + "）");
            } else {
                //在其他群抽取过了
                String groupName = Bot.getInstance(messageInfo.getLoginQq()).getGroup(group).getName();
                reply.append("你今天已经在别的群（" + groupName + "）抽取过老婆啦");
            }
        }
        replayInfo.setReplayMessage(reply.toString());
        return replayInfo;

    }






}


