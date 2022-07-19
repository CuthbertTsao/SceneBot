package top.strelitzia.service;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.MemberPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.strelitzia.dao.AdminUserMapper;
import top.strelitzia.model.AdminUserInfo;
import top.strelitzia.util.AdminUtil;

import java.io.File;
import java.util.List;
import java.util.Random;

@Service
public  class SceneReply {

    

    @Autowired
    private AdminUserMapper adminUserMapper;

    private static final org.slf4j.Logger log
            = org.slf4j.LoggerFactory.getLogger(SceneReply.class);

    //@AngelinaGroup(keyWords = {"test"})
    public ReplayInfo test666(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        log.warn(Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).getBotPermission().toString());
        if (Bot.getInstance(messageInfo.getLoginQq()).getGroup(messageInfo.getGroupId()).getBotPermission() != MemberPermission.MEMBER) {
            replayInfo.setReplayMessage("我是管理员");
        }else {
            replayInfo.setReplayMessage("我不是管理员");
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"三连","三联"}, description = "稀音三连")
    public ReplayInfo Triple(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        File file = new File("runFile/Reply/SceneTriple");
        File[] fileList = file.listFiles();
        replayInfo.setReplayImg(fileList[new Random().nextInt(fileList.length)]);
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"子三连","子三联"}, description = "猫子三连")
    public ReplayInfo CatTriple(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        File file = new File("runFile/Reply/CatTriple");
        File[] fileList = file.listFiles();
        replayInfo.setReplayImg(fileList[new Random().nextInt(fileList.length)]);
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"群主","strelitzia"})
    public ReplayInfo Strelitzia(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        File file = new File("runFile/Reply/SceneStrelitzia");
        File[] fileList = file.listFiles();
        replayInfo.setReplayImg(fileList[new Random().nextInt(fileList.length)]);
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"表情包","表情"}, description = "来自稀音的表情包")
    public ReplayInfo Emoji(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        File file = new File("runFile/Reply/SceneEmoji");
        File[] fileList = file.listFiles();
        replayInfo.setReplayImg(fileList[new Random().nextInt(fileList.length)]);
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"踩我","踩死我","老婆踩我","老婆踩死我","踢我"}, description = "不会真有变态发这个吧？")
    public ReplayInfo Step(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        File file = new File("runFile/Reply/Step");
        File[] fileList = file.listFiles();
        replayInfo.setReplayImg(fileList[new Random().nextInt(fileList.length)]);
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"踢他","踢她","踹他","踹她","踹它","踹死他","踹死她","踹死它","踹"}, description = "才不听陌生人的话哩！")
    public ReplayInfo ToStep(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        List<AdminUserInfo> admins = adminUserMapper.selectAllAdmin();
        boolean b = AdminUtil.getStepAdmin(messageInfo.getQq(), admins);
        if (b) {
            replayInfo.setReplayMessage("遵命");
            File file = new File("runFile/Reply/Step");
            File[] fileList = file.listFiles();
            replayInfo.setReplayImg(fileList[new Random().nextInt(fileList.length)]);
        }else{
            replayInfo.setReplayMessage(messageInfo.getName() + "？你谁啊？我才不听你的呢！笨蛋！");
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"早安","早上好","早"}, description = "稀音的早安问候（有5%概率触发问题发言）")
    public ReplayInfo goodmorning(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        double r = Math.random();
        List<AdminUserInfo> admins = adminUserMapper.selectAllAdmin();
        boolean b = AdminUtil.getIntimateAdmin(messageInfo.getQq(), admins);
        if(b){
            r = 0;
        }
        if (r < 0.05) {
            int n = (int) (3 * Math.random() + 1);
            switch (n) {
                case 1:
                    replayInfo.setReplayMessage("早安" + messageInfo.getName() + "，那个......唔~需要我帮忙处理晨勃嘛♡");
                    break;

                case 2:
                    replayInfo.setReplayMessage("早安" + messageInfo.getName() + "，我来帮你处理晨勃吧♡");
                    break;

                default:
                    replayInfo.setReplayMessage("早安" + messageInfo.getName() + "宝贝♡，昨晚表现真棒！今晚......要不叫上小虎鲸一起吧！");
                    break;

            }

        } else {
            int n = (int) (3 * Math.random() + 1);
            switch (n) {
                case 1:
                    replayInfo.setReplayMessage("早安，" + messageInfo.getName() + "。“正在播放音乐——《D.D.D.热门串烧》。”");
                    break;

                case 2:
                    replayInfo.setReplayMessage("早安，" + messageInfo.getName());
                    break;

                default:
                    replayInfo.setReplayMessage(messageInfo.getName() + "，早上好");
                    break;

            }
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"晚安","好梦"}, description = "稀音的晚安问候（有5%概率触发问题发言）")
    public ReplayInfo goodnight(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        double r = Math.random();
        List<AdminUserInfo> admins = adminUserMapper.selectAllAdmin();
        boolean b = AdminUtil.getIntimateAdmin(messageInfo.getQq(), admins);
        if(b){
            r = 0;
        }
        if (r < 0.05) {
            int n = (int) (3 * Math.random() + 1);
            switch (n) {
                case 1:
                    replayInfo.setReplayMessage(messageInfo.getName() + "......那个......今晚再来一次嘛♡");
                    break;

                case 2:
                    replayInfo.setReplayMessage("这就睡了嘛......" + messageInfo.getName() + "......我今天还特地用了个香香的沐浴乳的说......");
                    break;

                default:
                    replayInfo.setReplayMessage("晚安，" + messageInfo.getName() + "，小心夜袭哦♡啾咪♡");
                    break;

            }

        } else {
            int n = (int) (3 * Math.random() + 1);
            switch (n) {
                case 1:
                    replayInfo.setReplayMessage("晚安，" + messageInfo.getName() + "“请为稀音小姐安排吊床，谢谢。”");
                    break;

                case 2:
                    replayInfo.setReplayMessage("晚安，" + messageInfo.getName());
                    break;

                default:
                    replayInfo.setReplayMessage(messageInfo.getName() + "，晚安");
                    break;

            }
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"舔狗日记"}, description = "发送一篇舔狗日记")
    public ReplayInfo dogdiary(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);

            int n = (int) (21 * Math.random() + 1);
            switch (n) {
                case 1:
                    replayInfo.setReplayMessage("昨天你把我删了，我看着红色感叹号陷入了久久的沉思......我想这其中一定有什么含义。红色？红色！我明白了！红色代表热情你对我很热情！你想和我结婚！我愿意！！");
                    break;

                case 2:
                    replayInfo.setReplayMessage("今天我还是照常给你发消息，汇报日常工作，你终于回了我四个字：“嗯嗯，好的”你开始愿意敷衍我了，我太感动了受宠若惊。我愿意天天给你发消息。就算你天天骂我，我也不觉得烦。");
                    break;

                case 3:
                    replayInfo.setReplayMessage("你昨天晚上又没会我的消息，在我孜孜不倦的骚扰下，你终于舍得回我了，你说“滚”，这其中一定有什么含义，我想了很久，滚是三点水，这代表你对我的思念也如滚滚流水一样汹涌，我感动哭了，不知道你现在在干嘛，我很想你。");
                    break;

                case 4:
                    replayInfo.setReplayMessage("你说你想买口红，今天我去了叔叔的口罩厂做了一天的打包。拿到了两百块钱，加上我这几天省下的钱刚好能给你买一根小金条。即没有给我自己剩下一分钱，但你不用担心，因为厂里包吃包住。对了打包的时候，满脑子都是你，想着你哪天突然就接受我的橄榄枝了呢。而且今天我很棒呢，主管表扬我很能干，其实也有你的功劳啦，是你给了我无穷的力量。今天我比昨天多想你一点，比明天少想你一点。");
                    break;

                case 5:
                    replayInfo.setReplayMessage("你说你想买AJ，今天我去了叔叔的口罩厂做了一天的打包。拿到了两百块钱，加上我这几天省下的钱刚好能给你买一个鞋盒。即没有给我自己剩下一分钱，但你不用担心，因为厂里包吃包住。对了打包的时候，满脑子都是你，想着你哪天突然就接受我的橄榄枝了呢。而且今天我很棒呢，主管表扬我很能干，其实也有你的功劳啦，是你给了我无穷的力量。今天我比昨天多想你一点，比明天少想你一点。");
                    break;

                case 6:
                    replayInfo.setReplayMessage("听说你想要一套化妆品，我算了算，明天我去公司里面扫一天厕所，就可以拿到200块钱，再加上我上个月攒下来的零花钱，刚好给你买一套迪奥。");
                    break;

                case 7:
                    replayInfo.setReplayMessage("在我入职保安的那天，队长问我：你知道你要保护谁嘛？我嘴上说的是业主，心里却是你。over");
                    break;

                case 8:
                    replayInfo.setReplayMessage("我存了两个月钱，给你买了一双北卡蓝，你对我说一句谢谢，我好开心。这是你第一次对我说两个字，以前你都只对我说滚。今天晚上逛咸鱼，看到了你把我送你的北卡蓝发布上去了。我想你一定是在考验我，再次送给你，给你一个惊喜，我爱你。");
                    break;

                case 9:
                    replayInfo.setReplayMessage("昨天你领完红包就把我删了，我陷入久久地沉思。我想这其中一定有什么含义，原来你是在欲擒故纵，嫌我不够爱你。无理取闹的你变得更加可爱了，我会坚守我对你的爱的。你放心好啦！今天发工资了，发了1850，给你微信转了520，支付宝1314，还剩下16。给你发了很多消息你没回。剩下16块我在小卖部买了你爱吃的老坛酸菜牛肉面，给你寄过去了。希望你保护好食欲，我去上班了爱你~~");
                    break;

                case 10:
                    replayInfo.setReplayMessage("昨天给你发了99条约你一起植树的消息，今天你终于肯回我了，你说“你先去植发吧，死秃子。”我一下子就哭了，原来努力真的有用，你已经开始关心我了，你也是挺喜欢我的吧。");
                    break;

                case 11:
                    replayInfo.setReplayMessage("昨晚你和朋友大佬一晚上游戏，你破天荒的给我看了你的战绩，虽然我看不懂但我相信你一定是最厉害的，最棒的！我给你发了好多消息夸你，告诉你我多崇拜你，你回了我一句：啥b。我翻来覆去思考这是什么意思？sh-a傻，噢你的意思是说我傻，那b就是baby的意思了吧，原来你是在叫我傻宝，这么宠溺的语气，我竟一时不相信，其实你也是喜欢我的对吧");
                    break;

                case 12:
                    replayInfo.setReplayMessage("你想我了吧？可以回我消息了吗？我买了万通筋骨贴 你运动一个晚上腰很疼吧？今晚早点回家 我炖了排骨汤 累了一个晚上吧 没事我永远在家等你");
                    break;

                case 13:
                    replayInfo.setReplayMessage("今天你把我的微信删了，这下我终于解放了！以前我总担心太多消息会打扰你，现在我终于不用顾忌，不管我怎么给你发消息，都不会让你不开心了。等我攒够5201314条我就给你看，你一定会震惊得说不出话然后哭着说会爱我一辈子。哈哈");
                    break;

                case 14:
                    replayInfo.setReplayMessage("听网上说前些天的月亮最大最亮，没看成，今晚我说我想和你一起看月亮，你却回我你看你妈，我看见你发的消息我懂了你的意思，可能你是想告诉我月亮不如我妈重要吧，于是我听你的话看了我妈一个晚上。");
                    break;

                case 15:
                    replayInfo.setReplayMessage("你十分钟没有回我的消息，在我孜孜不倦的骚扰下，你终于舍得回我了。你说“憨憨”，这其中一定有什么含义，可能说在夸我傻傻很可爱吧？我上百度搜了也许你话没有说全，是不是你偷我这个憨憨的心所以变成敢敢呢？我感动哭了，原来是我自己感动了我自己。不知道你现在在干嘛呢？我很想你～");
                    break;

                case 16:
                    replayInfo.setReplayMessage("我爸说再敢网恋就打断我的腿，幸好不是胳膊，这样我还能继续和你打字聊天，就算连胳膊也打断了，我的心里也会有你位置。");
                    break;

                case 17:
                    replayInfo.setReplayMessage("疫情不能出门，现在是早上八点，你肯定饿了吧。我早起做好了早餐来到你小区。保安大哥不让进。我给你打了三个电话，你终于接了：“有病啊，我还睡觉呢，你小区门口等着吧。”啊，我高兴坏了。她终于愿意吃我做的早餐了，她让我等她，啊！啊！啊！");
                    break;

                case 18:
                    replayInfo.setReplayMessage("你扇了我一巴掌 我握着你的手说“怎么这么凉”");
                    break;

                case 19:
                    replayInfo.setReplayMessage("我给你打了一通电话，你终于接了。听到了你发出啊啊啊啊的声音，你说你脚痛，我想你一定是很难受吧。电话里还有个男的对你说“来换个姿势”，一定是在做理疗了。期待你早品康复!");
                    break;

                case 20:
                    replayInfo.setReplayMessage("刚从派出所出来，原因前几天14号情人节，我想送你礼物，我去偷东西的时候被抓了，我本来想反抗，警察说了一句老实点别动，我立刻就放弃了反抗，因为我记得你说过，你喜欢老实人。");
                    break;

                default:
                    replayInfo.setReplayMessage("你很久没回我的消息，在我孜孜不倦的骚扰之下你终于舍得回我了，你说“滚”这其中一定有什么含义，我想了很久，滚是三点水，这代表你对我的思念也如滚滚流水一样汹涌，我感动哭了，不知道你现在在干嘛！我很想你。");
                    break;

            }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"情话"}, description = "稀音教你说情话")
    public ReplayInfo honeyedWords(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        int n = (int) (20 * Math.random() + 1);
        switch (n) {
            case 1:
                replayInfo.setReplayMessage("你是我这一生只会遇见一次的惊喜");
                break;

            case 2:
                replayInfo.setReplayMessage("如果我会隐身就好了，那我一定藏在你的枕头里，藏在十二月的风里，藏在你的口袋里。");
                break;

            case 3:
                replayInfo.setReplayMessage("你是远方的风景，我是游走的旅人，我翻山越岭，长途跋涉，只为看你一眼。");
                break;

            case 4:
                replayInfo.setReplayMessage("我原以为我是个受得了寂寞的人。现在方明白我们自从在一起后，我就变成一个不能同你离开的人了。");
                break;

            case 5:
                replayInfo.setReplayMessage("情书给你一封，情话给你一句，余生给你一人。");
                break;

            case 6:
                replayInfo.setReplayMessage("突然很喜欢惊鸿一瞥这个词，一见钟情太肤浅，日久生情太苍白。别人眉来眼去，我呀，只偷看你一眼。");
                break;

            case 7:
                replayInfo.setReplayMessage("你会变成小星星，在别人的世界里一闪一闪亮晶晶，所以我要在睡着之前，把你藏进我的眼睛。");
                break;

            case 8:
                replayInfo.setReplayMessage("你的过去我不愿过问，那是你的事情。你的未来我希望参与，这是我的荣幸。");
                break;

            case 9:
                replayInfo.setReplayMessage("我听说十三月有你 便穷尽这一生去寻你 漫天柳絮倾覆了我情义");
                break;

            case 10:
                replayInfo.setReplayMessage("你的声音最好听，你身上的味道最好闻，你笑起来的样子最好看，你陪着我的时候，我从没羡慕过任何人，我喜欢你所有的样子。");
                break;

            case 11:
                replayInfo.setReplayMessage("我大约真的没有什么才华，只是因为有幸见着了你，于是这颗庸常的心中才凭空生出好些浪漫。");
                break;

            case 12:
                replayInfo.setReplayMessage("自从有了你的出现，我飘泊不定的心便有了停靠的港湾，找到了对你的思念，从此寂寞和孤独不会再占据我的心田，爱你永远。");
                break;

            case 13:
                replayInfo.setReplayMessage("如果可以和你在一起，我宁愿让天空所有的星光全部损落，因为你的眼睛，是我生命里最亮的光芒。");
                break;

            case 14:
                replayInfo.setReplayMessage("遇见你之后，我就愈发贪懒了，每日啊，只想着早早的把银河打烊，带着晚归的星星跟月亮全都栖进你眼睛里");
                break;

            case 15:
                replayInfo.setReplayMessage("我耳边听到的一切仿佛都是你在轻声细语，我心里所想的一切是你，目光触及到除你以外的地方都是荒野。");
                break;

            case 16:
                replayInfo.setReplayMessage("你是世间最可爱的小星星 我爱了整个宇宙只为了跟你碰头");
                break;

            case 17:
                replayInfo.setReplayMessage("noʎ ʇnq ǝʌol ɹǝɥʇo oN");
                break;

            case 18:
                replayInfo.setReplayMessage("“Mg+ZnS04=MgS04+Zn”你的镁偷走了我的锌");
                break;

            case 19:
                replayInfo.setReplayMessage("因为有你，连今天的落日都觉得可爱");
                break;

            default:
                replayInfo.setReplayMessage("万物枯荣皆为你眼，我目眩神驰。而你一笑清明，潦倒我的众生。");
                break;

        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"古诗"}, description = "来一句古诗")
    public ReplayInfo poetry(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        int n = (int) (30 * Math.random() + 1);
        switch (n) {
            case 1:
                replayInfo.setReplayMessage("疏影微香，下有幽人昼梦长。——苏轼《减字木兰花·双龙对起》");
                break;

            case 2:
                replayInfo.setReplayMessage("嫣然摇动，冷香飞上诗句。——姜夔《念奴娇·闹红一舸》");
                break;

            case 3:
                replayInfo.setReplayMessage("如梦，如梦，残月落花烟重。——李存勖《如梦令·曾宴桃源深洞》");
                break;

            case 4:
                replayInfo.setReplayMessage("梧桐叶上，点点露珠零。——尹鹗《临江仙·深秋寒夜银河静》");
                break;

            case 5:
                replayInfo.setReplayMessage("落花人独立，微雨燕双飞。——晏几道《临江仙》");
                break;

            case 6:
                replayInfo.setReplayMessage("人何在，桂影自婵娟。——蔡伸《苍梧谣·天》");
                break;

            case 7:
                replayInfo.setReplayMessage("彩笺书，红粉泪，两心知。——欧阳炯《三字令·春欲尽》");
                break;

            case 8:
                replayInfo.setReplayMessage("醉梦里、年华暗换。——卢祖皋《宴清都·初春》");
                break;

            case 9:
                replayInfo.setReplayMessage("云山万重，寸心千里。——佚名《鱼游春水·秦楼东风里》");
                break;

            case 10:
                replayInfo.setReplayMessage("思君令人老，岁月忽已晚。——佚名《行行重行行》");
                break;

            case 11:
                replayInfo.setReplayMessage("当时夜泊，温柔便入深乡。——吴文英《夜合花·自鹤江入京泊葑门外有感》");
                break;

            case 12:
                replayInfo.setReplayMessage("念故人，千里至此共明月。——寇准《踏莎行·寒草烟光阔》");
                break;

            case 13:
                replayInfo.setReplayMessage("树深时见鹿，溪午不闻钟。——李白《访戴天山道士不遇》");
                break;

            case 14:
                replayInfo.setReplayMessage("残灯孤枕梦，轻浪五更风。——徐昌图《临江仙》");
                break;

            case 15:
                replayInfo.setReplayMessage("脸慢笑盈盈，相看无限情。——李煜《菩萨蛮·蓬莱院闭天台女》");
                break;

            case 16:
                replayInfo.setReplayMessage("马萧萧，人去去，陇云愁。——孙光宪《酒泉子·无边》");
                break;

            case 17:
                replayInfo.setReplayMessage("夜来清梦好，应是发南枝。——李清照《临江仙·梅》");
                break;

            case 18:
                replayInfo.setReplayMessage("春思乱，芳心碎。——惠洪《千秋岁·半身屏外》");
                break;

            case 19:
                replayInfo.setReplayMessage("昨宵入梦，那人如玉，何处吹箫？——张可久《人月圆·春日湖上》");
                break;

            case 20:
                replayInfo.setReplayMessage("桐花半亩，静锁一庭愁雨。——周邦彦《琐窗寒·寒食》");
                break;

            case 21:
                replayInfo.setReplayMessage("短篷南浦雨，疏柳断桥烟。——赵长卿《临江仙·暮春》");
                break;

            case 22:
                replayInfo.setReplayMessage("杏花疏影里，吹笛到天明。——陈与义《临江仙·夜登小阁忆洛中旧游》");
                break;

            case 23:
                replayInfo.setReplayMessage("掩妾泪，听君歌。——李白《夜坐吟》");
                break;

            case 24:
                replayInfo.setReplayMessage("晓山眉样翠，秋水镜般明。——辛弃疾《临江仙·钟鼎山林都是梦》");
                break;

            case 25:
                replayInfo.setReplayMessage("青松影里，红藕香中。——张可久《普天乐·西湖即事》");
                break;

            case 26:
                replayInfo.setReplayMessage("寄相思，寒雨灯窗，芙蓉旧院。——吴文英《宴清都·秋感》");
                break;

            case 27:
                replayInfo.setReplayMessage("竹影和诗瘦，梅花入梦香。——王庭筠《绝句》");
                break;

            case 28:
                replayInfo.setReplayMessage("千里共如何，微风吹兰杜。——王昌龄《同从弟南斋玩月忆山阴崔少府》");
                break;

            case 29:
                replayInfo.setReplayMessage("微微风簇浪，散作满河星。——查慎行《舟夜书所见》");
                break;

            default:
                replayInfo.setReplayMessage("步转回廊，半落梅花婉娩香。——苏轼《减字木兰花·春月》");
                break;

        }
        return replayInfo;
    }





}