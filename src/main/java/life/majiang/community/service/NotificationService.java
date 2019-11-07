package life.majiang.community.service;

import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.enums.NotificationStatusEnum;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.NotificationMapper;
import life.majiang.community.model.Notification;
import life.majiang.community.model.NotificationExample;
import life.majiang.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    //查询专属user 的 封装page与notification合集
    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {

        //Notification集合
        List<Notification> notifications = new ArrayList<>();
        //封装了Notification与User关系集合
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        //封装了页码、NotificationDto
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        Integer offset = (page - 1) * size;
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        //根据创建时间倒序，最新回复将放在最前面
        notificationExample.setOrderByClause("gmt_create desc");
        notificationExample.setOrderByClause("status");

        //获取Notification数据-->总页数(*** 数据1 ***)
        long totalCount = notificationMapper.countByExample(notificationExample);
        //利用总页数，当前页，每页条数计算对paginationDTO进行赋值
        Integer extraPage = 2;
        paginationDTO.setPageQuestion((int)totalCount, extraPage, page, size);

        //查询专属user 的 notification合集(*** 数据2 ***)
        notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, size));
        if (notifications.size() == 0) {
            return paginationDTO;
        }

        //封装Notification 为 NotificationDTO
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }

        //将查询到 QuestionDTO 集合设置到 PaginationDTO 中
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;

    }

    //获取通知数量
    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());;
        long unreadCount = notificationMapper.countByExample(notificationExample);
        return unreadCount;
    }

    //用于判断当前user与信息id所属user是否一致，避免造成误读他人信息
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        //主题已删除
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        //不是你自己的通知
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        //设置改通知为已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        //更新通知消息
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
