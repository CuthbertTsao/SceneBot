package top.strelitzia.service;


import lombok.SneakyThrows;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


@Service
public class BlackHistory {
    @Autowired
    private AdminUserMapper adminUserMapper;

    private void downloadOneFile(String fileName, String url) throws IOException {
        URL u = new URL(url);
        HttpURLConnection httpUrl = (HttpURLConnection) u.openConnection();
        httpUrl.connect();
        try (InputStream is = httpUrl.getInputStream(); FileOutputStream fs = new FileOutputStream(fileName)){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fs.write(buffer, 0, len);
            }
        }
        httpUrl.disconnect();
    }


    @AngelinaGroup(keyWords = {"黑历史","bh"}, description = "随机展现一张黑历史")
    public ReplayInfo bh(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        File file = new File("runFile/bh");
        File[] fileList = file.listFiles();
        if (messageInfo.getArgs().size() > 1) {
            String num = messageInfo.getArgs().get(1);
            File pic = new File("runFile/bh/bh" + num + ".jpg");
            if(pic.exists()) {
                replayInfo.setReplayMessage("图片编号" + num );
                replayInfo.setReplayImg(new File("runFile/bh/bh" + num + ".jpg"));
            }else {
                replayInfo.setReplayMessage("图片不存在，请输入正确的编号。可以通过“稀音黑历史库”来查询当前图库编号范围");
            }
        }else {
            int num = (int) (1+Math.random()*(fileList.length));
            replayInfo.setReplayImg(new File("runFile/bh/bh" + num + ".jpg"));
            replayInfo.setReplayMessage("图片编号" + num );
        }
        return replayInfo;
    }


    @SneakyThrows
    @AngelinaGroup(keyWords = {"黑历史库","bh库"}, description = "查看图库中的编号范围，或对图库进行快速编辑")
    public ReplayInfo bhRange(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        File file = new File("runFile/bh");
        File[] fileList = file.listFiles();
        //图库快捷编辑模块
        if (messageInfo.getArgs().size() > 1) {
            String code = messageInfo.getArgs().get(1);
            //先判断是否有图库编辑权限
            List<AdminUserInfo> admins = adminUserMapper.selectAllAdmin();
            boolean b = AdminUtil.getPiceditAdmin(messageInfo.getQq(), admins);
                switch (code) {

                    case "删除":
                        if (!b && messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
                            replayInfo.setReplayMessage("您无权删除");
                            return replayInfo;
                        }
                        //判断是否有指定编号
                        if (messageInfo.getArgs().size() > 2) {
                            String num = messageInfo.getArgs().get(2);
                            File pic = new File("runFile/bh/bh" + num + ".jpg");
                            //判断指定图片是否存在
                            if (pic.exists()) {
                                boolean value = pic.delete();
                                StringBuilder reply = new StringBuilder("图片" + num + "已成功删除");
                                //如果图片不为最后一张，自动填补编号的空缺
                                String lastpicnum = String.valueOf(fileList.length);
                                if (!num.equals(lastpicnum)) {
                                    int d = fileList.length;
                                    File lastpic = new File("runFile/bh/bh" + fileList.length + ".jpg");
                                    lastpic.renameTo(new File("runFile/bh/bh" + num + ".jpg"));
                                    reply.append("\n已自动将图片" + d + "填补至空缺：" + num);
                                }
                                replayInfo.setReplayMessage(reply.toString());
                            } else {
                                replayInfo.setReplayMessage("图片不存在，请输入正确的编号。可以通过“稀音黑历史库”来查询当前图库编号范围");
                            }
                        } else {
                            replayInfo.setReplayMessage("请输入要删除的图片编号，一次仅能删除一张。");
                        }
                        break;

                    case "添加":
                        //判断消息是否附带图片
                        int u = messageInfo.getImgUrlList().size();
                        if (u != 0) {
                            //判断消息是否附带目标编号
                            StringBuilder reply;
                            if (messageInfo.getArgs().size() > 2) {
                                String num = messageInfo.getArgs().get(2);
                                    //判断编号是否为纯数字
                                    for (int i = num.length(); --i >= 0; ) {
                                        if (!Character.isDigit(num.charAt(i))) {
                                            replayInfo.setReplayMessage("请输入纯数字编号");
                                            return replayInfo;
                                        }
                                    //编号为纯数字，进行后续操作
                                        //判断编号是否被占用
                                        File gpic = new File("runFile/bh/bh" + num + ".jpg");
                                        if (gpic.exists()) {
                                            replayInfo.setReplayMessage("该编号已被占用");
                                        } else {
                                            //如果带有多张图
                                            if (messageInfo.getImgUrlList().size() != 1) {
                                                replayInfo.setReplayMessage("将图片添加至特定编号时仅能操作一张图片");
                                            } else {
                                                //仅带一张图，下载至图库
                                                for (String url : messageInfo.getImgUrlList()) {
                                                    downloadOneFile("runFile/bh/bh" + num + ".jpg", url);
                                                }
                                                replayInfo.setReplayMessage("成功添加图片" + num);
                                            }
                                        }
                                }
                            } else {
                                //无指定编号，保存至最后一张（可一次保存多张）
                                int p = fileList.length;
                                reply = new StringBuilder("成功添加" + messageInfo.getImgUrlList().size() + "张图片");
                                for (String url: messageInfo.getImgUrlList()) {
                                    p++;
                                    downloadOneFile("runFile/bh/bh" + p + ".jpg", url);
                                    reply.append("\n编号").append(p);
                                }
                                replayInfo.setReplayMessage(reply.toString());
                            }

                        } else {
                            replayInfo.setReplayMessage("请加上需要添加的图片");
                        }
                        break;

                    case "查空":
                        int p = fileList.length;
                        int q = 0;
                        StringBuilder reply = null;
                        for (int i = 1; i <= p; i++) {
                            File gpic = new File("runFile/bh/bh" + i + ".jpg");
                            if (!gpic.exists()) {
                                if (q == 0) {
                                    reply = new StringBuilder("编号");
                                    reply.append(i).append("尚未使用");
                                }else {
                                    reply.append("\n编号").append(i).append("尚未使用");
                                }
                                q ++;
                            }
                        }
                        if (q == 0) {
                            reply = new StringBuilder("没有发现编号空位");
                        }
                        replayInfo.setReplayMessage(reply.toString());

                        break;

                    /*case "管理":
                        replayInfo.setReplayMessage("目前图库共有”删除“、”添加“、”查空“三种功能"
                                + "\n稀音图库 删除 编号，可以删除指定编号的图片，如果该图片非最后一张，将自动以最后一张填补其编号空缺"
                                + "\n稀音图库 添加，可以将一张或多张图片添加至图库并自动编号"
                                + "\n稀音图库 添加 编号，可以将一张图片添加至指定编号（请确保编号与图库中已有图片的编号连续，否则会出bug）"
                                + "\n稀音图库 查空，可以快速发现空缺的编号（保险起见建议多查空几次）"
                                + "\n*如首次使用请继续发送“稀音图库 注意事项”");
                        break;

                    case "注意事项":
                        replayInfo.setReplayMessage("*请在快捷编辑图库前一定利用”查空“功能确保没有编号空位！否则可能导致编辑时部分图片丢失！"
                                + "\n*如果发现编号空位请通过“添加”功能，或者手动将库中最后一张图片的名称改成空缺的编号"
                                + "\n*图库存储路径为runFile/gpic，手动添加图片需要确保图片命名格式为“gpic编号.jpg”，并确认编号连续"
                                + "\n*使用快捷编辑删除的图片不会进回收站，请谨慎操作"
                                + "\n在使用快捷添加图库功能，无法识别转发多张图片或一次粘贴多张图片，但是一张一张粘贴最后发送的话是可以识别的");
                        break;
                     */
                    default:
                        replayInfo.setReplayMessage("请输入正确的指令");
                        break;
                }
        }else {
            int num = fileList.length;
            replayInfo.setReplayMessage("当前黑历史库共有" + num + "张图片"
                    + "\n请发送“稀音黑历史 数字编号”来定向查找图片"
                    + "\n直接发送“稀音黑历史”会随机抽取一张图片，并显示其id"
                    + "\n*该功能仅能用来存储洁哥群各种聊天记录，且当事人有权要求删除");
        }
        return replayInfo;
    }







}