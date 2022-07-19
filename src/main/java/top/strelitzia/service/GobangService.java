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
import top.angelinaBot.model.TextLine;
import top.angelinaBot.util.SendMessageUtil;
import top.strelitzia.dao.AdminUserMapper;
import top.strelitzia.util.AdminUtil;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.*;


@Service
public class GobangService {

    private static final Set<Long> groupList = new HashSet<>();

    @Autowired
    SendMessageUtil sendMessageUtil;

    @Autowired
    AdminUserMapper adminUserMapper;

    //private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GobangService.class);

    //@AngelinaGroup(keyWords = {"test"})
    public ReplayInfo testGobang(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        BigDecimal xy = new BigDecimal(messageInfo.getArgs().get(1));
        replayInfo.setReplayMessage(String.valueOf(getY(xy)));
        return replayInfo;
    }


    @AngelinaGroup(keyWords = {"重置棋盘" , "重置五子棋"} )
    public ReplayInfo resetGobang(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        boolean sqlAdmin = AdminUtil.getSqlAdmin(messageInfo.getQq(), adminUserMapper.selectAllAdmin());
        if(messageInfo.getUserAdmin() == MemberPermission.MEMBER && !sqlAdmin ){
            replayInfo.setReplayMessage("仅管理员有权重置棋盘。如果您为五子棋参赛者，请尝试直接发送“重置”。");
        } else {
            groupList.remove(messageInfo.getGroupId());
            replayInfo.setReplayMessage("重置完成");
        }
        return replayInfo;
    }


    @AngelinaGroup(keyWords = {"五子棋"})
    public ReplayInfo gobang(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        Long groupId = messageInfo.getGroupId();
        if (groupList.contains(groupId)) {
            replayInfo.setReplayMessage("本群正在进行五子棋，或五子棋匹配中" +
                    "\n加入请发送“加入五子棋”或“稀音加入五子棋”" +
                    "\n请如果遇到问题可以发送“稀音重置棋盘”");
            return replayInfo;
        } else {
            long circleQq = 0L;
            long squareQq = 0L;
            int r = new Random().nextInt(2);
            if (r == 0) {
                circleQq = messageInfo.getQq();
            } else {
                squareQq = messageInfo.getQq();
            }
            replayInfo.setReplayMessage(messageInfo.getName() + "加入成功！\n" + "[五子棋]正在等待二号玩家的加入......");
            sendMessageUtil.sendGroupMsg(replayInfo);
            replayInfo.setReplayMessage(null);
            groupList.add(messageInfo.getGroupId());
            //等待二号玩家加入
            AngelinaListener playerListener = new AngelinaListener() {
                @Override
                public boolean callback(MessageInfo message) {
                    return message.getGroupId().equals(messageInfo.getGroupId()) &&
                            (message.getText().equals("加入五子棋") || message.getText().equals("稀音加入五子棋") ||
                                    message.getText().equals("取消") || message.getText().equals("取消匹配"));
                }
            };
            playerListener.setGroupId(messageInfo.getGroupId());
            MessageInfo player2 = AngelinaEventSource.waiter(playerListener).getMessageInfo();
            if (player2 == null) {
                replayInfo.setReplayMessage(messageInfo.getName() + " 匹配超时\n[五子棋]已重置");
                groupList.remove(messageInfo.getGroupId());
                return replayInfo;
            }
            if ((player2.getText().equals("取消") || player2.getText().equals("取消匹配")) &&
            player2.getQq().equals(messageInfo.getQq())){
                replayInfo.setReplayMessage(messageInfo.getName() + " 取消匹配\n[五子棋]已重置");
                groupList.remove(messageInfo.getGroupId());
                return replayInfo;
            }
            if (r == 0){
                squareQq = player2.getQq();
            }else {
                circleQq = player2.getQq();
            }
            replayInfo.setReplayMessage(player2.getName() + "加入成功！\n" + "[五子棋]即将开始");
            sendMessageUtil.sendGroupMsg(replayInfo);
            replayInfo.setReplayMessage(null);
            //初始化circleTurn用于回合判定，surrender用于判断是否认输，result用于判定结果
            boolean circleTurn = true;
            boolean surrender = false;
            int result = 0;
            //生成一张棋盘并发送,0L用于记录上一枚棋子的落点
            List<Long> board = Arrays.asList(messageInfo.getGroupId(),
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L,
                    1000000000000000L);
            BigDecimal lastPiece = null;
            //圆圈名字
            String circleName = Bot.getInstance(messageInfo.getLoginQq()).getGroup(groupId).get(circleQq).getNameCard();
            if (circleName.isEmpty()) {
                circleName = Bot.getInstance(messageInfo.getLoginQq()).getGroup(groupId).get(circleQq).getRemark();
            }
            //方块名字
            String squareName = Bot.getInstance(messageInfo.getLoginQq()).getGroup(groupId).get(squareQq).getNameCard();
            if (squareName.isEmpty()) {
                squareName = Bot.getInstance(messageInfo.getLoginQq()).getGroup(groupId).get(squareQq).getRemark();
            }
            replayInfo.setReplayImg(DrawBoard(board, circleTurn, circleName, squareName, lastPiece));
            sendMessageUtil.sendGroupMsg(replayInfo);
            replayInfo.getReplayImg().clear();

            //进入循环
            for(int i = 0 ;  i<256 ;i++) {
                //listener等待指令
                boolean sqlAdmin = AdminUtil.getSqlAdmin(messageInfo.getQq(), adminUserMapper.selectAllAdmin());
                long finalCircleQq = circleQq;
                long finalSquareQq = squareQq;
                AngelinaListener angelinaListener = new AngelinaListener() {
                    @Override
                    public boolean callback(MessageInfo message) {
                        if (message.getText().equals("棋盘") || message.getText().equals("查看棋盘")) {
                            return true;
                            //接收本群参赛者发送的坐标，或重置指令
                        }else if (message.getGroupId().equals(messageInfo.getGroupId()) &&
                                (message.getQq() == finalCircleQq || message.getQq() == finalSquareQq)) {
                            if (isNumber(message.getText())) {
                                return true;
                            }else {
                                return message.getText().equals("重置") || message.getText().equals("重置棋盘")
                                        || message.getText().equals("投降") || message.getText().equals("认输")
                                        || message.getText().equals("悔棋");
                            }
                        } else {
                            //接收群主和管理员以及sql发送的重置指令
                            return (message.getGroupId().equals(messageInfo.getGroupId()) &&
                                    (message.getUserAdmin() != MemberPermission.MEMBER || sqlAdmin) &&
                                    (message.getText().equals("重置") || message.getText().equals("重置棋盘")));
                        }
                    }
                };
                angelinaListener.setGroupId(groupId);
                MessageInfo recall = AngelinaEventSource.waiter(angelinaListener).getMessageInfo();
                if (recall == null) {
                    groupList.remove(messageInfo.getGroupId());
                    replayInfo.setReplayMessage("落子超时，本群棋盘已重置");
                    return replayInfo;
                }

                if (recall.getText().equals("重置") || recall.getText().equals("重置棋盘")) {
                    groupList.remove(messageInfo.getGroupId());
                    replayInfo.setReplayMessage("本群棋盘已重置");
                    return replayInfo;
                }

                if (recall.getText().equals("棋盘") || recall.getText().equals("查看棋盘")) {
                    replayInfo.setReplayImg(DrawBoard(board, circleTurn, circleName, squareName, lastPiece));
                    sendMessageUtil.sendGroupMsg(replayInfo);
                    replayInfo.getReplayImg().clear();
                    i--;
                    continue;
                }

                if (recall.getText().equals("投降") || recall.getText().equals("认输")){
                    if (recall.getQq().equals(circleQq)){
                        surrender = true;
                        result = 2;
                        break;
                    }else if (recall.getQq().equals(squareQq)){
                        surrender = true;
                        result = 1;
                        break;
                    }
                }

                if (recall.getText().equals("悔棋")){
                    if (recall.getQq().equals(circleQq) || recall.getQq().equals(squareQq)) {
                        if (circleTurn && recall.getQq().equals(squareQq)) {
                            if (lastPiece == null) {
                                replayInfo.setReplayMessage("不能连续悔棋哦");
                                sendMessageUtil.sendGroupMsg(replayInfo);
                                replayInfo.setReplayMessage(null);
                                i--;
                                continue;
                            }
                            BigDecimal xy = lastPiece;
                            int x = getX(xy);
                            int y = getY(xy);
                            Long row = board.get(x);
                            int q = 15 - y;
                            long add = 2L;
                            while (q > 0) {
                                add = add * 10;
                                q--;
                            }
                            row = row - add;
                            board.set(x, row);
                            lastPiece = null;
                            replayInfo.setReplayMessage("■悔棋");
                            replayInfo.setReplayImg(DrawBoard(board, circleTurn, circleName, squareName, lastPiece));
                            sendMessageUtil.sendGroupMsg(replayInfo);
                            replayInfo.setReplayMessage(null);
                            replayInfo.getReplayImg().clear();
                            circleTurn = false;
                            i -= 2;
                            continue;
                        } else if (!circleTurn && recall.getQq().equals(circleQq)) {
                            if (lastPiece == null) {
                                replayInfo.setReplayMessage("不能连续悔棋哦");
                                sendMessageUtil.sendGroupMsg(replayInfo);
                                replayInfo.setReplayMessage(null);
                                i--;
                                continue;
                            }
                            BigDecimal xy = lastPiece;
                            int x = getX(xy);
                            int y = getY(xy);
                            Long row = board.get(x);
                            int q = 15 - y;
                            long add = 1L;
                            while (q > 0) {
                                add = add * 10;
                                q--;
                            }
                            row = row - add;
                            board.set(x, row);
                            lastPiece = null;
                            replayInfo.setReplayMessage("●悔棋");
                            replayInfo.setReplayImg(DrawBoard(board, circleTurn, circleName, squareName, lastPiece));
                            sendMessageUtil.sendGroupMsg(replayInfo);
                            replayInfo.setReplayMessage(null);
                            replayInfo.getReplayImg().clear();
                            circleTurn = true;
                            i -= 2;
                            continue;
                        }else {
                            replayInfo.setReplayMessage("只有在自己的回合才能进行悔棋哦");
                            sendMessageUtil.sendGroupMsg(replayInfo);
                            replayInfo.setReplayMessage(null);
                            i--;
                            continue;
                        }
                    }

                }


                //汉字指令在上面处理完毕，获取坐标
                BigDecimal xy;
                //如果为圆圈的回合
                if (circleTurn && recall.getQq().equals(circleQq)) {
                    try{
                        xy = new BigDecimal(recall.getText());
                    }catch (NumberFormatException e){
                        replayInfo.setReplayMessage("错误的落子指令\n指令应形如4.2或13.14（小数点前为行，小数点后为列），且行与列均属于范围[1,15]");
                        sendMessageUtil.sendGroupMsg(replayInfo);
                        replayInfo.setReplayMessage(null);
                        i--;
                        continue;
                    }
                    if (xy.compareTo(BigDecimal.valueOf(getX(xy)))==0 || !validXY(xy)){
                        replayInfo.setReplayMessage("错误的落子指令\n指令应形如4.2或13.14（小数点前为行，小数点后为列），且行与列均属于范围[1,15]");
                        sendMessageUtil.sendGroupMsg(replayInfo);
                        replayInfo.setReplayMessage(null);
                        i--;
                        continue;
                    }
                    //解析坐标
                    int x = getX(xy);
                    int y = getY(xy);
                    //判断落点是否有效
                    if (GetPieceByXY(xy,board) != 0){
                        replayInfo.setReplayMessage("无效落子●");
                        sendMessageUtil.sendGroupMsg(replayInfo);
                        replayInfo.setReplayMessage(null);
                        i--;
                    }else {
                        //落子有效，先计算落子后的row，并保存
                        Long row = board.get(x);
                        //这里需要乘方运算，如果用Math.pow需要int转double再转long，很可能会出事，所以用了这个办法
                        int q = 15 - y;
                        long add = 1L;
                        while (q > 0) {
                            add = add * 10;
                            q--;
                        }
                        row = row + add;
                        //将行写入board，并记录落子
                        board.set(x, row);
                        lastPiece = xy;
                        //判断是否五连,是则跳出循环
                        if (GobangOver(xy, board)) {
                            result = 1;
                            break;
                        }else {
                            //没有五连，更改turn并发送棋盘
                            circleTurn = false;
                            replayInfo.setReplayImg(DrawBoard(board, circleTurn, circleName, squareName, lastPiece));
                            sendMessageUtil.sendGroupMsg(replayInfo);
                            replayInfo.setReplayMessage(null);
                            replayInfo.getReplayImg().clear();
                        }
                    }
                }else
                //如果为方块的回合
                if (!circleTurn && recall.getQq().equals(squareQq)) {
                    try{
                        xy = new BigDecimal(recall.getText());
                    }catch (NumberFormatException e){
                        replayInfo.setReplayMessage("错误的落子指令\n指令应形如4.2或13.14（小数点前为行，小数点后为列），且行与列均属于范围[1,15]");
                        sendMessageUtil.sendGroupMsg(replayInfo);
                        replayInfo.setReplayMessage(null);
                        i--;
                        continue;
                    }
                    if (xy.compareTo(BigDecimal.valueOf(getX(xy)))==0){
                        replayInfo.setReplayMessage("错误的落子指令\n指令应形如4.2或13.14（小数点前为行，小数点后为列），且行与列均属于范围[1,15]");
                        sendMessageUtil.sendGroupMsg(replayInfo);
                        replayInfo.setReplayMessage(null);
                        i--;
                        continue;
                    }
                    //解析坐标
                    int x = getX(xy);
                    int y = getY(xy);
                    //判断落点是否有效
                    if (GetPieceByXY(xy,board) != 0){
                        replayInfo.setReplayMessage("无效落子■");
                        sendMessageUtil.sendGroupMsg(replayInfo);
                        replayInfo.setReplayMessage(null);
                        i--;
                    }else {
                        //落子有效，先计算落子后的row，并保存
                        Long row = board.get(x);
                        //这里需要乘方运算，如果用Math.pow需要int转double再转long，很可能会出事，所以用了这个办法
                        int q = 15 - y;
                        long add = 2L;
                        while (q > 0) {
                            add = add * 10;
                            q--;
                        }
                        row = row + add;
                        //将行写入board，并记录落子
                        board.set(x, row);
                        lastPiece = xy;
                        //判断是否五连,是则跳出循环
                        if (GobangOver(xy, board)) {
                            result = 2;
                            break;
                        }else {
                            //没有五连，更改turn并发送棋盘
                            circleTurn = true;
                            replayInfo.setReplayImg(DrawBoard(board, circleTurn, circleName, squareName, lastPiece));
                            sendMessageUtil.sendGroupMsg(replayInfo);
                            replayInfo.getReplayImg().clear();
                        }
                    }
                }
            }

            //循环结束
            TextLine textLine = new TextLine();
            switch (result){
                case 1:
                    //圆圈获胜
                    if (surrender){
                        textLine.addCenterStringLine("■认输");
                        textLine.nextLine();
                    }
                    textLine.addString("对局结束，获胜者是：");
                    textLine.nextLine();
                    textLine.addString(circleName);
                    textLine.nextLine();
                    textLine.addString("恭喜！");
                    replayInfo.setReplayImg(textLine.drawImage());
                    groupList.remove(messageInfo.getGroupId());
                    return replayInfo;
                case 2:
                    //方块获胜
                    if (surrender){
                        textLine.addCenterStringLine("●认输");
                        textLine.nextLine();
                    }
                    textLine.addString("对局结束，获胜者是：");
                    textLine.nextLine();
                    textLine.addString(squareName);
                    textLine.nextLine();
                    textLine.addString("恭喜！");
                    replayInfo.setReplayImg(textLine.drawImage());
                    groupList.remove(messageInfo.getGroupId());
                    return replayInfo;
                default:
                    //下满了
                    textLine.addString("对局结束，无人获胜");
                    textLine.nextLine();
                    textLine.addString("不得不佩服，");
                    if (circleQq==squareQq) {
                        textLine.addString("您很有耐心，");
                    }else {
                        textLine.addString("您二位很有耐心，");
                    }
                    textLine.addString("令人感慨");
                    textLine.nextLine();
                    textLine.addString("不过有这些时间，做点更有意义的事情，比如读一本好书，或者陪陪家人而不是陪我一个冷冰冰的bot，会不会更好呢？");
                    replayInfo.setReplayImg(textLine.drawImage());
                    groupList.remove(messageInfo.getGroupId());
                    return replayInfo;
            }
        }
    }


    /**
     * 图片绘制方法
     *
     * @param board 棋盘数据
     * @param circleTurn 回合信息
     */
    public BufferedImage DrawBoard(List<Long> board,Boolean circleTurn,String circleName,String squareName,BigDecimal lastPiece) {
        long groupId = board.get(0);
        TextLine textLine = new TextLine(25);
        //添加选手名字
        textLine.addString("选手信息：");
        textLine.nextLine();
        //姓名写入textLine
        textLine.addString(circleName + "（●）");
        textLine.nextLine();
        textLine.addString(squareName + "（■）");
        textLine.nextLine();
        textLine.nextLine();
        //获取回合情况以及上一枚落子信息
        textLine.addString("现在是");
        if (circleTurn) {
            textLine.addString("●的回合");
            textLine.nextLine();
            if (lastPiece != null) {
                textLine.addString("上枚落子为" + lastPiece + "■");
                textLine.nextLine();
            }
        }else {
            textLine.addString("■的回合");
            textLine.nextLine();
            if (lastPiece != null) {
                textLine.addString("上枚落子为" + lastPiece + "●");
                textLine.nextLine();
            }
        }

        //绘制棋盘
        textLine.addGobangBoard("   1 2 3 4 5 6 7 89101112131415");
        textLine.nextLine();
        //循环添加所有的行
        for (int a =1; a <= 15; a++){
            if (a<10){
                textLine.addString(" ");
            }
            textLine.addString(  a + "");
            long row = board.get(a);
            //循环添加一行里所有的列
            for (int b = 1; b<= 15; b++){
                int piece = intAt(row, b);
                switch (piece){
                    case 1:
                        textLine.addGobangBoard("●");
                        break;
                    case 2:
                        textLine.addGobangBoard("■");
                        break;
                    default:
                        textLine.addGobangBoard("□");
                        break;
                }
            }
            textLine.nextLine();
        }
        return textLine.drawGobangBoard();
    }




    /**
     * 根据点的坐标获取点的落子情况
     *
     * @param xy 点的坐标
     * @param board 棋盘数据
     */
    public Integer GetPieceByXY(BigDecimal xy, List<Long> board){
        int x = getX(xy);
        int y = getY(xy);
        long row = board.get(x);
        return intAt(row , y);
    }

    /**
     * 根据int类型的x与y获取点的落子情况
     */
    public Integer GetPieceByXAndY(int x, int y, List<Long> board){
        long row = board.get(x);
        return intAt(row , y);
    }


    /**
     * 从row提取坐标y的数值，通过转str的方式来提取，简化了不少步骤
     *
     * @param row 此行的数据
     * @param y 需要获取第几位，会跳过每行第一位的无关数字
     */
    public static Integer intAt(Long row,int y){
        String str = String.valueOf(row);
        return Integer.parseInt(str.charAt(y)+"");
    }




    /**
     * 结束判断算法。由于落子必是五连的组成部分，只需要判断落子是否构成五连即可判断获胜。
     *
     * @param xy 落子的坐标
     * @param board 棋盘数据
     */
    public boolean GobangOver(BigDecimal xy, List<Long> board){
        //p为落子类型（circle或square）
        int p = GetPieceByXY(xy, board);
        //将xy坐标解析为x坐标及y坐标
        int x = getX(xy);
        int y = getY(xy);
        //先判断横向
        int total = 1;
        long row = board.get(x);
        //向右，将连续的棋子数累计至total
        for (int yBy = y + 1; yBy >= 1 && yBy <= 15; yBy++ ){
            if (intAt(row,yBy) == p){
                total++;
            }else {
                break;
            }
        }
        //向左，将连续的棋子数累计至total
        for (int yBy = y -1; yBy >= 1 && yBy <= 15; yBy-- ){
            if (intAt(row,yBy) == p){
                total++;
            }else {
                break;
            }
        }
        //判断是否获胜，不是则重置total
        if (total >= 5){
            return true;
        }else {
            total = 1;
        }

        //纵向，先向上
        for (int xBy = x - 1; xBy >= 1 && xBy <= 15; xBy-- ){
            if (GetPieceByXAndY(xBy, y, board) == p){
                total++;
            }else {
                break;
            }
        }
        //向下
        for (int xBy = x + 1; xBy >= 1 && xBy <= 15; xBy++ ){
            if (GetPieceByXAndY(xBy, y, board) == p){
                total++;
            }else {
                break;
            }
        }
        //判断是否获胜，不是则重置total
        if (total >= 5){
            return true;
        }else {
            total = 1;
        }

        //斜向（右上至左下），先向右上。
        int yBy = y + 1;
        for (int xBy = x - 1 ;xBy>=1 && xBy<=15 && yBy>=1 && yBy<=15; xBy--  ){
            if (GetPieceByXAndY(xBy, yBy, board) == p){
                total++;
                yBy++;
            }else {
                break;
            }
        }
        //向左下
        yBy = y - 1;
        for (int xBy = x + 1 ;xBy>=1 && xBy<=15 && yBy>=1 && yBy<=15; xBy++){
            if (GetPieceByXAndY(xBy, yBy, board) == p){
                total++;
                yBy--;
            }else {
                break;
            }
        }
        //判断是否获胜，不是则重置total
        if (total >= 5){
            return true;
        }else {
            total = 1;
        }

        //斜向（左上至右下），先左上
        yBy = y - 1;
        for (int xBy = x - 1 ;xBy>=1 && xBy<=15 && yBy>=1 && yBy<=15; xBy--){
            if (GetPieceByXAndY(xBy, yBy, board) == p){
                total++;
                yBy--;
            }else {
                break;
            }
        }
        //向右下
        yBy = y + 1;
        for (int xBy = x + 1 ;xBy>=1 && xBy<=15 && yBy>=1 && yBy<=15; xBy++){
            if (GetPieceByXAndY(xBy, yBy, board) == p){
                total++;
                yBy++;
            }else {
                break;
            }
        }
        //判断是否获胜
        return total >= 5;
    }

    /**
    *判断坐标是否为有效坐标
    *
    * @param xy 输入的坐标
    */
    public boolean validXY(BigDecimal xy){
        int x = getX(xy);
        int y = getY(xy);
        return  (x>=1 && x<=15 && y>=1 && y<=15);
    }


    /**
     *使用正则判断str是否为数字
     */
    public static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }


    /**
     * 从坐标中获取x
     */
    public Integer getX(BigDecimal xy){
        return xy.setScale(0,BigDecimal.ROUND_DOWN).intValue();
    }

    /**
     *从坐标中获取y
     */
    public Integer getY(BigDecimal xy){
        BigDecimal x = xy.setScale(0,BigDecimal.ROUND_DOWN);
        BigDecimal y = xy.subtract(x);
        String inStr = String.valueOf(y);
        int bitPos=inStr.indexOf(".");
        for (int numOfBits=inStr.length()-bitPos-1; numOfBits > 0; numOfBits--){
            y = y.multiply(BigDecimal.valueOf(10));
        }
        return y.intValue();
    }




}
