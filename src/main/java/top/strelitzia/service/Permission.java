package top.strelitzia.service;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.MemberPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.strelitzia.dao.AdminUserMapper;
import top.strelitzia.model.AdminUserInfo;
import top.strelitzia.util.AdminUtil;

import java.util.List;

@Service public class Permission {

    @Autowired
    private AdminUserMapper adminUserMapper;



    @AngelinaFriend(keyWords = {"权限", "权限管理"}, description = "编辑权限")
    @AngelinaGroup(keyWords = {"权限", "权限管理"}, description = "编辑权限")
    public ReplayInfo permission(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        int ticket = 0;
        if (!messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            ticket = 1;
        }
        Long qq = messageInfo.getQq();
        List<AdminUserInfo> admins = adminUserMapper.selectAllAdmin();
        boolean b = AdminUtil.getSqlAdmin(qq, admins);
        if (b || qq == 1790967910L) {
            ticket = 2;
        }
        int size = messageInfo.getArgs().size();
        if (size == 1) {
            Integer sqlByQq = adminUserMapper.selectUserAdminSqlByQq(qq);
            if (sqlByQq == null) {
                replayInfo.setReplayMessage("可供群主与管理员更改的权限：" +
                            "\nfound（无限抽卡） " + b +
                            "\nsix（六星爆率拉满） " + b +
                            "\nintimate（彩蛋语音必触发） " + b +
                            "\n仅供sql修改的权限：" +
                            "\nsql " + b +
                            "\npicedit（图库编辑权限） " + b +
                            "\nstep（踹他/今日老婆编辑） " + b);
            } else {
                    replayInfo.setReplayMessage("可供群主与管理员更改的权限：" +
                            "\nfound（无限抽卡） " + AdminUtil.getFoundAdmin(qq, admins) +
                            "\nsix（六星爆率拉满） " + AdminUtil.getSixAdmin(qq, admins) +
                            "\nintimate（彩蛋语音必触发） " + AdminUtil.getIntimateAdmin(qq, admins) +
                            "\n仅供sql修改的权限：" +
                            "\nsql " + AdminUtil.getSqlAdmin(qq, admins) +
                            "\npicedit（图库编辑权限） " + AdminUtil.getPiceditAdmin(qq, admins) +
                            "\nstep（踹他/今日老婆编辑） " + AdminUtil.getStepAdmin(qq, admins));
            }
        } else if (size == 2 && ticket >= 1) {
            //没有数据则创建一个
            Integer sqlByQq = adminUserMapper.selectUserAdminSqlByQq(qq);
            if (sqlByQq == null){
                AdminUserInfo newAdminUserInfo = new AdminUserInfo();
                newAdminUserInfo.setQq(qq);
                newAdminUserInfo.setFound(0);
                newAdminUserInfo.setSix(0);
                newAdminUserInfo.setIntimate(0);
                newAdminUserInfo.setSql(0);
                newAdminUserInfo.setPicedit(0);
                newAdminUserInfo.setStep(0);
                newAdminUserInfo.setName(messageInfo.getName());
                adminUserMapper.insertUserAdmin(newAdminUserInfo);
            }
            //读取各项权限
            List<AdminUserInfo> adminByQq = adminUserMapper.selectAllAdminByQq(qq);
            int found = adminByQq.get(0).getFound();
            int six = adminByQq.get(0).getSix();
            int intimate = adminByQq.get(0).getIntimate();
            int sql = adminByQq.get(0).getSql();
            int picedit = adminByQq.get(0).getPicedit();
            int step = adminByQq.get(0).getStep();
            AdminUserInfo adminUserInfo = new AdminUserInfo();
            adminUserInfo.setQq(qq);
            adminUserInfo.setFound(found);
            adminUserInfo.setSix(six);
            adminUserInfo.setIntimate(intimate);
            adminUserInfo.setSql(sql);
            adminUserInfo.setPicedit(picedit);
            adminUserInfo.setStep(step);
            adminUserInfo.setName(messageInfo.getName());

            if (messageInfo.getArgs().get(1).equals("found")){
                if (found == 0){
                    adminUserInfo.setFound(1);
                    replayInfo.setReplayMessage( messageInfo.getName() + "的found已开启");
                }else {
                    adminUserInfo.setFound(0);
                    replayInfo.setReplayMessage( messageInfo.getName() + "的found已关闭");
                }
                adminUserMapper.updateUserAdmin(adminUserInfo);
            }else if (messageInfo.getArgs().get(1).equals("six")){
                if (six == 0){
                    adminUserInfo.setSix(1);
                    replayInfo.setReplayMessage( messageInfo.getName() + "的six已开启");
                }else {
                    adminUserInfo.setSix(0);
                    replayInfo.setReplayMessage( messageInfo.getName() + "的six已关闭");
                }
                adminUserMapper.updateUserAdmin(adminUserInfo);
            }else if (messageInfo.getArgs().get(1).equals("intimate")){
                if (intimate == 0){
                    adminUserInfo.setIntimate(1);
                    replayInfo.setReplayMessage( messageInfo.getName() + "的intimate已开启");
                }else {
                    adminUserInfo.setIntimate(0);
                    replayInfo.setReplayMessage( messageInfo.getName() + "的intimate已关闭");
                }
                adminUserMapper.updateUserAdmin(adminUserInfo);
            }

            else if (ticket == 2){
                if (messageInfo.getArgs().get(1).equals("sql")){
                    if (sql == 0){
                        adminUserInfo.setSql(1);
                        replayInfo.setReplayMessage( messageInfo.getName() + "的sql已开启");
                    }else {
                        adminUserInfo.setSql(0);
                        replayInfo.setReplayMessage( messageInfo.getName() + "的sql已关闭");
                    }
                    adminUserMapper.updateUserAdmin(adminUserInfo);
                }else if (messageInfo.getArgs().get(1).equals("picedit")){
                    if (picedit == 0){
                        adminUserInfo.setPicedit(1);
                        replayInfo.setReplayMessage( messageInfo.getName() + "的picedit已开启");
                    }else {
                        adminUserInfo.setPicedit(0);
                        replayInfo.setReplayMessage( messageInfo.getName() + "的picedit已关闭");
                    }
                    adminUserMapper.updateUserAdmin(adminUserInfo);
                }else if (messageInfo.getArgs().get(1).equals("step")){
                    if (step == 0){
                        adminUserInfo.setStep(1);
                        replayInfo.setReplayMessage( messageInfo.getName() + "的step已开启");
                    }else {
                        adminUserInfo.setStep(0);
                        replayInfo.setReplayMessage( messageInfo.getName() + "的step已关闭");
                    }
                    adminUserMapper.updateUserAdmin(adminUserInfo);
                }else {
                    //判断目标qq是否为纯数字
                    String str1 = messageInfo.getArgs().get(1);
                    int key = 0;
                    for (int i = str1.length(); --i >= 0; ) {
                        if (!Character.isDigit(str1.charAt(i))) {
                            replayInfo.setReplayMessage("目标qq应为纯数字");
                            key++;
                        }
                    }
                    //确定为纯数字，进行后续操作
                    if (key == 0) {
                        Long goal = Long.valueOf(str1);
                        String name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(goal).getNameCard();
                        if (name.isEmpty()) {
                            name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(goal).getRemark();
                        }
                        if (name.isEmpty()){
                            name = goal.toString();
                        }
                        replayInfo.setReplayMessage(name +
                                "\nfound=" + AdminUtil.getFoundAdmin(goal, admins) +
                                ",six=" + AdminUtil.getSixAdmin(goal, admins) +
                                ",intimate=" + AdminUtil.getIntimateAdmin(goal, admins) +
                                ",sql=" + AdminUtil.getSqlAdmin(goal, admins) +
                                ",picedit=" + AdminUtil.getPiceditAdmin(goal, admins) +
                                ",step=" + AdminUtil.getStepAdmin(goal, admins));
                    }
                }
            }

            else {
                replayInfo.setReplayMessage("无效操作，请检查您输入的指令是否正确，或是否有对应的操作权限");
            }
        }else if (size == 3 && ticket == 2) {
            //判断目标qq是否为纯数字
            String str1 = messageInfo.getArgs().get(1);
            int key = 0;
            for (int i = str1.length(); --i >= 0; ) {
                if (!Character.isDigit(str1.charAt(i))) {
                    replayInfo.setReplayMessage("目标qq应为纯数字");
                    key++;
                }
            }
            //确定为纯数字，进行后续操作
            if (key == 0) {
                Long goal = Long.valueOf(str1);
                String name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(goal).getNameCard();
                if (name.isEmpty()) {
                    name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(goal).getRemark();
                }
                if (name.isEmpty()){
                    name = goal.toString();
                }
                //没有数据则创建一个
                Integer sqlByQq = adminUserMapper.selectUserAdminSqlByQq(goal);
                if (sqlByQq == null) {
                    AdminUserInfo newAdminUserInfo = new AdminUserInfo();
                    newAdminUserInfo.setQq(goal);
                    newAdminUserInfo.setFound(0);
                    newAdminUserInfo.setSix(0);
                    newAdminUserInfo.setIntimate(0);
                    newAdminUserInfo.setSql(0);
                    newAdminUserInfo.setPicedit(0);
                    newAdminUserInfo.setStep(0);
                    newAdminUserInfo.setName(name);
                    adminUserMapper.insertUserAdmin(newAdminUserInfo);
                }
                //读取各项权限
                List<AdminUserInfo> adminByQq = adminUserMapper.selectAllAdminByQq(goal);
                int found = adminByQq.get(0).getFound();
                int six = adminByQq.get(0).getSix();
                int intimate = adminByQq.get(0).getIntimate();
                int sql = adminByQq.get(0).getSql();
                int picedit = adminByQq.get(0).getPicedit();
                int step = adminByQq.get(0).getStep();
                AdminUserInfo adminUserInfo = new AdminUserInfo();
                adminUserInfo.setQq(goal);
                adminUserInfo.setFound(found);
                adminUserInfo.setSix(six);
                adminUserInfo.setIntimate(intimate);
                adminUserInfo.setSql(sql);
                adminUserInfo.setPicedit(picedit);
                adminUserInfo.setStep(step);
                adminUserInfo.setName(name);

                if (messageInfo.getArgs().get(2).equals("found")) {
                    if (found == 0) {
                        adminUserInfo.setFound(1);
                        replayInfo.setReplayMessage(name + "的found已开启");
                    } else {
                        adminUserInfo.setFound(0);
                        replayInfo.setReplayMessage(name + "的found已关闭");
                    }
                    adminUserMapper.updateUserAdmin(adminUserInfo);
                } else if (messageInfo.getArgs().get(2).equals("six")) {
                    if (six == 0) {
                        adminUserInfo.setSix(1);
                        replayInfo.setReplayMessage(name + "的six已开启");
                    } else {
                        adminUserInfo.setSix(0);
                        replayInfo.setReplayMessage(name + "的six已关闭");
                    }
                    adminUserMapper.updateUserAdmin(adminUserInfo);
                } else if (messageInfo.getArgs().get(2).equals("intimate")) {
                    if (intimate == 0) {
                        adminUserInfo.setIntimate(1);
                        replayInfo.setReplayMessage(name + "的intimate已开启");
                    } else {
                        adminUserInfo.setIntimate(0);
                        replayInfo.setReplayMessage(name + "的intimate已关闭");
                    }
                    adminUserMapper.updateUserAdmin(adminUserInfo);
                }else if (messageInfo.getArgs().get(2).equals("sql")){
                    if (sql == 0){
                        adminUserInfo.setSql(1);
                        replayInfo.setReplayMessage(name + "的sql已开启");
                    }else {
                        adminUserInfo.setSql(0);
                        replayInfo.setReplayMessage(name + "的sql已关闭");
                    }
                    adminUserMapper.updateUserAdmin(adminUserInfo);
                }else if (messageInfo.getArgs().get(2).equals("picedit")){
                    if (picedit == 0){
                        adminUserInfo.setPicedit(1);
                        replayInfo.setReplayMessage(name + "的picedit已开启");
                    }else {
                        adminUserInfo.setPicedit(0);
                        replayInfo.setReplayMessage(name + "的picedit已关闭");
                    }
                    adminUserMapper.updateUserAdmin(adminUserInfo);
                }else if (messageInfo.getArgs().get(2).equals("step")) {
                    if (step == 0) {
                        adminUserInfo.setStep(1);
                        replayInfo.setReplayMessage(name + "的step已开启");
                    } else {
                        adminUserInfo.setStep(0);
                        replayInfo.setReplayMessage(name + "的step已关闭");
                    }
                    adminUserMapper.updateUserAdmin(adminUserInfo);
                }else {
                    replayInfo.setReplayMessage("无效操作，请检查您输入的指令是否正确，或是否有对应的操作权限");
                }

            }

        }else{
            replayInfo.setReplayMessage("无效操作，请检查您输入的指令是否正确，或是否有对应的操作权限");
        }


        return replayInfo;
    }

}