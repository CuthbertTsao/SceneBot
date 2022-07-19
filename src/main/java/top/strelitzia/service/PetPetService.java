package top.strelitzia.service;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.MemberPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaEvent;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.EventEnum;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.strelitzia.model.AdminUserInfo;
import top.strelitzia.util.ImageUtil;
import top.strelitzia.util.PetPetUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Random;

@Service
public class PetPetService {

    @Autowired
    private PetPetUtil petPetUtil;

    @Autowired
    private ImageUtil imageUtil;


    @AngelinaEvent(event = EventEnum.NudgeEvent, description = "发送头像的摸头动图")
    @AngelinaGroup(keyWords = {"摸头", "摸我", "摸摸"}, description = "发送头像的摸头动图")
    public ReplayInfo PetPet(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        BufferedImage userImage = ImageUtil.Base64ToImageBuffer(
                ImageUtil.getImageBase64ByUrl("http://q.qlogo.cn/headimg_dl?dst_uin=" + messageInfo.getQq() + "&spec=100"));
        String path = "runFile/petpet/frame.gif";
        petPetUtil.getGif(path, userImage);
        replayInfo.setReplayImg(new File(path));
        return replayInfo;
    }

    @AngelinaEvent(event = EventEnum.GroupRecall, description = "撤回事件回复")
    public ReplayInfo GroupRecall(MessageInfo messageInfo) {
        ReplayInfo replayInfo = PetPet(messageInfo);
        replayInfo.setReplayMessage("镜头已经拍下来了，撤回是没用的");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"口我", "透透","超我","超市我","艹我","透我","肏我","操我","草我"}, description = "禁言功能")
    public ReplayInfo MuteSomeOne(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        boolean a = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).getBotPermission().equals(MemberPermission.MEMBER);
        if (!a) {
            int p = (new Random().nextInt(5) + 1) * 60;
            replayInfo.setMuted(p);
        }
        int n = (int) (3 * Math.random() + 1);
        switch (n) {
            case 1:
                replayInfo.setReplayMessage("下头男滚啊！");
                break;

            case 2:
                replayInfo.setReplayMessage("死变态！");
                break;

            default:
                replayInfo.setReplayMessage("啥b");
                break;
        }

        return replayInfo;
    }

    @AngelinaEvent(event = EventEnum.MemberJoinEvent, description = "入群欢迎")
    public ReplayInfo memberJoin(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        replayInfo.setReplayMessage("欢迎新人入群，我是bot稀音，请通过【稀音菜单】查看稀音的功能"
                                    + "\n为了避免不必要的打扰，请在指令前面加上我的名字");
        return replayInfo;
    }

    @AngelinaEvent(event = EventEnum.MemberLeaveEvent, description = "退群提醒")
    public ReplayInfo memberLeaven(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        replayInfo.setReplayMessage("稀音系统提醒，有人退群了。");
        return replayInfo;
    }


}
