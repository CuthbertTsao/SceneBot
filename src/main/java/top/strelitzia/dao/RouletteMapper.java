package top.strelitzia.dao;

import org.apache.ibatis.annotations.Param;
import top.strelitzia.model.RouletteInfo;

public interface RouletteMapper {

    //根据群号查询子弹位置和开枪次数是否相符确定子弹会不会射出
    RouletteInfo rouletteByQQ(Long groupId);

    //根据群号更新子弹位置（子弹上膛）
    Integer rouletteTarget(@Param("groupId") Long groupId, @Param("bullet") double bullet);

    //更新开枪次数
    Integer rouletteShoot(@Param("groupId") Long groupId, @Param("trigger") Integer trigger);

    //清空清空轮盘赌所有数据
    Integer cleanRoulette();

    //根据群号记录参赛人员1的QQ号
    Integer rouletteParticipant1(@Param("groupId") Long groupId, @Param("participant") Long participant);

    //根据群号记录参赛人员2的QQ号
    Integer rouletteParticipant2(@Param("groupId") Long groupId, @Param("participant") Long participant);

    //根据群号记录参赛人员3的QQ号
    Integer rouletteParticipant3(@Param("groupId") Long groupId, @Param("participant") Long participant);

    //根据群号记录参赛人员4的QQ号
    Integer rouletteParticipant4(@Param("groupId") Long groupId, @Param("participant") Long participant);

    //根据群号记录参赛人员5的QQ号
    Integer rouletteParticipant5(@Param("groupId") Long groupId, @Param("participant") Long participant);

    //根据群号记录参赛人员6的QQ号
    Integer rouletteParticipant6(@Param("groupId") Long groupId, @Param("participant") Long participant);

    //根据群号查询轮盘赌对决信息
    RouletteInfo rouletteDuelByGroup(Long groupId);

    //清空清空轮盘赌对决所有数据
    Integer cleanRouletteDuel();

}
