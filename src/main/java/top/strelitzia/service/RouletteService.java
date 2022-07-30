package top.strelitzia.service;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.MemberPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaEventSource;
import top.angelinaBot.container.AngelinaListener;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;
import top.strelitzia.dao.RouletteMapper;
import top.strelitzia.model.RouletteInfo;

import java.io.File;
import java.util.Random;



@Service
public class RouletteService {

    @Autowired
    private RouletteMapper rouletteMapper;

    @Autowired
    SendMessageUtil sendMessageUtil;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RouletteService.class);

    @AngelinaGroup(keyWords = {"给轮盘上子弹","上膛","拔枪吧","上子弹"}, description = "守护铳轮盘赌，看看谁是天命之子")
    public ReplayInfo roulette(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        int bullet = 1;
        if (messageInfo.getArgs().size() > 1 ) {
            if (isInteger(messageInfo.getArgs().get(1))) {
                bullet = Integer.parseInt(messageInfo.getArgs().get(1));
                if (bullet < 0){
                    replayInfo.setReplayMessage("博士，您是不是身体不太舒服？要不我带您去凯尔希医生那里看看吧？");
                    return replayInfo;
                }else if (bullet == 0){
                    replayInfo.setReplayMessage("英雄可不能临阵脱逃啊！博士，上子弹吧！");
                    return replayInfo;
                } else if (bullet == 6) {
                    replayInfo.setReplayMessage("这种事你也做的出来......不愧是巴别塔的恶灵......");
                    return replayInfo;
                } else if (bullet > 6) {
                    replayInfo.setReplayMessage("博士，我不是子弹批发商......");
                    return replayInfo;
                }
            }else {
                replayInfo.setReplayMessage("装填的子弹必须为整数");
                return replayInfo;
            }
        }
        replayInfo.setReplayMessage("这是一把充满荣耀与死亡的守护铳，六个弹槽中有" + bullet + "颗子弹，不幸者将再也发不出声音。勇士们啊，扣动你们的扳机！（直接发送开枪即可）感谢Outcast提供的守护铳！");
        sendMessageUtil.sendGroupMsg(replayInfo);
        replayInfo.setReplayMessage(null);
        //获得弹夹，1/10概率卡壳
        int clip = RandomClip(bullet);
        int stuck = new Random().nextInt(10);
        log.info("clip:" + clip + "stuck" + stuck);
        for (int i = 0; i < 6; i++){
            AngelinaListener angelinaListener = new AngelinaListener() {
                @Override
                public boolean callback(MessageInfo message) {
                    return message.getGroupId().equals(messageInfo.getGroupId()) &&
                            (message.getText().equals("开枪") || message.getText().equals("稀音开枪"));
                }
            };
            angelinaListener.setGroupId(messageInfo.getGroupId());
            MessageInfo recall = AngelinaEventSource.waiter(angelinaListener).getMessageInfo();
            if (recall == null) {
                replayInfo.setReplayMessage("居然临阵脱逃了......真是对我的手中的这把Outcastd的守护铳的亵渎！");
                return replayInfo;
            }

            if (slot(clip,i)==1) {
                if (stuck==0){
                    Long qq = recall.getQq();
                    String name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(qq).getNameCard();
                    if (name.isEmpty()) {
                        name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(qq).getRemark();
                    }
                    replayInfo.setReplayMessage("卡壳了......我的手中的这把Outcastd的守护铳，找了无数工匠都难以修缮如新。不......不该如此......" +
                            "\n" + name + "，算你走运！");
                    return replayInfo;
                }
                if (Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).getBotPermission().getLevel()>recall.getUserAdmin().getLevel()) {
                    //禁言通过获取qq号完成，所以这里要重设一下qq
                    replayInfo.setQq(recall.getQq());
                    replayInfo.setMuted((new Random().nextInt(3) + 1) * 60);
                    replayInfo.setReplayMessage("对不起，我也不想这样的......");
                } else {
                    Long qq = recall.getQq();
                    String name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(qq).getNameCard();
                    if (name.isEmpty()) {
                        name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(qq).getRemark();
                    }
                    replayInfo.setReplayMessage(name + "，你输了。可惜我没有权限禁言你，那作为惩罚就踹你一脚吧！");
                    File file = new File("runFile/Reply/Step");
                    File[] fileList = file.listFiles();
                    replayInfo.setReplayImg(fileList[new Random().nextInt(fileList.length)]);
                }
                return replayInfo;
            }
            switch (i){
                case 0:
                    replayInfo.setReplayMessage("无需退路。( 1 / 6 )");
                    break;
                case 1:
                    replayInfo.setReplayMessage("英雄们，为这最强大的信念，请站在我们这边。( 2 / 6 )");
                    break;
                case 2:
                    replayInfo.setReplayMessage("颤抖吧，在真正的勇敢面前。( 3 / 6 )");
                    break;
                case 3:
                    replayInfo.setReplayMessage("哭嚎吧，为你们不堪一击的信念。( 4 / 6 ) ");
                    break;
                case 4:
                    replayInfo.setReplayMessage("现在可没有后悔的余地了。( 5 / 6 )");
                    break;
                case 5:
                    replayInfo.setReplayMessage("我的手中的这把Outcastd的守护铳，找了无数工匠都难以修缮如新。不......不该如此......");
                    break;
            }
            sendMessageUtil.sendGroupMsg(replayInfo);
            replayInfo.setReplayMessage(null);
        }
        return null;
    }

    /**
     * 根据子弹数生成弹夹
     * @param bullet 子弹数
     */
    public Integer RandomClip(Integer bullet){
        int r = 5-(new Random().nextInt(6));
        int clip = 6000000;
        for (int i = 0; i < bullet; i++){
            if (r<0) r+=6;
            int j = r;
            int add = 1;
            while (j > 0) {
                add = add * 10;
                j--;
            }
            clip = clip + add;
            r--;
        }
        return clip;
    }



    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 从弹夹中提取弹槽的数据
     *
     * @param clip 需要获取的弹夹
     * @param y 需要获取第几位，会跳过每行第一位的无关数字
     */
    public static Integer slot(int clip,int y){
        String str = String.valueOf(clip);
        return Integer.parseInt(str.charAt(y+1)+"");
    }


    @AngelinaGroup(keyWords = {"轮盘赌对决参赛"}, description = "六人参赛，一人丧命")
    public ReplayInfo RouletteDuel(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        RouletteInfo rouletteInfo = this.rouletteMapper.rouletteDuelByGroup(messageInfo.getGroupId());
        int Num;
        try {
            Num = rouletteInfo.getParticipantNum();
        }catch (Exception e)
        {
            this.rouletteMapper.cleanRouletteDuel();
            rouletteInfo = this.rouletteMapper.rouletteDuelByGroup(messageInfo.getGroupId());
            Num = rouletteInfo.getParticipantNum();
        }

        if( Num==0||Num>7 ){
            this.rouletteMapper.cleanRouletteDuel();
            Num = rouletteInfo.getParticipantNum();
        }


        switch (Num){
            case 1:
                replayInfo.setReplayMessage("这是一把充满荣耀与死亡的守护铳，六个弹槽只有一颗子弹，六位参赛者也将会有一位不幸者将再也发不出声音。\n欢迎第一位挑战者"+ messageInfo.getName() +"\n愿主保佑你，我的勇士。");
                this.rouletteMapper.rouletteParticipant1(messageInfo.getGroupId(),messageInfo.getQq());
                break;
            case 2:
                replayInfo.setReplayMessage("欢迎第二位挑战者"+messageInfo.getName()+"\n愿主保佑你，我的勇士。");
                this.rouletteMapper.rouletteParticipant2(messageInfo.getGroupId(),messageInfo.getQq());
                break;
            case 3:
                replayInfo.setReplayMessage("欢迎第三位挑战者"+messageInfo.getName()+"\n愿主保佑你，我的勇士。");
                this.rouletteMapper.rouletteParticipant3(messageInfo.getGroupId(),messageInfo.getQq());
                break;
            case 4:
                replayInfo.setReplayMessage("欢迎第四位挑战者"+messageInfo.getName()+"\n愿主保佑你，我的勇士。");
                this.rouletteMapper.rouletteParticipant4(messageInfo.getGroupId(),messageInfo.getQq());
                break;
            case 5:
                replayInfo.setReplayMessage("欢迎第五位挑战者"+messageInfo.getName()+"\n愿主保佑你，我的勇士。");
                this.rouletteMapper.rouletteParticipant5(messageInfo.getGroupId(),messageInfo.getQq());
                break;
            case 6:
                replayInfo.setReplayMessage("欢迎第六位挑战者"+messageInfo.getName()+"\n愿主保佑你，我的勇士。");
                this.rouletteMapper.rouletteParticipant6(messageInfo.getGroupId(),messageInfo.getQq());
                break;
            default:
                replayInfo.setReplayMessage(messageInfo.getName()+"，参赛人数已满，请等待下一场参赛吧");
                break;
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"对决开始"}, description = "轮盘对决的生死抉择开始了")
    public ReplayInfo RouletteDuelBegging(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        boolean a = !Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).getBotPermission().equals(MemberPermission.MEMBER);
        RouletteInfo rouletteInfo = this.rouletteMapper.rouletteDuelByGroup(messageInfo.getGroupId());
        Integer Num = rouletteInfo.getParticipantNum();
        //查询次数决定能不能开始
        if(Num < 6){
            replayInfo.setReplayMessage("参赛人数还不足六人，还不能开始对决呢。");
        }else{
            this.rouletteMapper.cleanRoulette();
            double r = Math.random();
            double bullet = 6 * r;
            int finallyBullet = (int) Math.round(bullet);
            switch (finallyBullet){
                case 0:
                    replayInfo.setQq(rouletteInfo.getParticipantQQ1());
                    break;
                case 1:
                    replayInfo.setQq(rouletteInfo.getParticipantQQ2());
                    break;
                case 2:
                    replayInfo.setQq(rouletteInfo.getParticipantQQ3());
                    break;
                case 3:
                    replayInfo.setQq(rouletteInfo.getParticipantQQ4());
                    break;
                case 4:
                    replayInfo.setQq(rouletteInfo.getParticipantQQ5());
                    break;
                default:
                    replayInfo.setQq(rouletteInfo.getParticipantQQ6());
                    break;
            }
            if (!a) {
                //把获取到的禁言QQ带入禁言功能并且实现禁言
                if (a)
                    replayInfo.setMuted(5 * 60);
            }
            Long qq = replayInfo.getQq();
            String name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(qq).getNameCard();
            if (name.isEmpty()) {
                name = Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).get(qq).getRemark();
            }
            replayInfo.setReplayMessage("永别了，" + name +"......安息吧勇士......" );
            this.rouletteMapper.cleanRouletteDuel();
        }
        return replayInfo;
    }

}
