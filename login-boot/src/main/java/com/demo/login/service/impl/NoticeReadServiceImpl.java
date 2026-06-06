package com.demo.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.login.entity.Notice;
import com.demo.login.entity.NoticeRead;
import com.demo.login.mapper.NoticeMapper;
import com.demo.login.mapper.NoticeReadMapper;
import com.demo.login.service.INoticeReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告已读关联服务实现类
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Slf4j
@Service
public class NoticeReadServiceImpl implements INoticeReadService {

    @Autowired
    private NoticeReadMapper noticeReadMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long userId, Long noticeId) {
        // 检查是否已经读过
        LambdaQueryWrapper<NoticeRead> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NoticeRead::getUserId, userId)
                .eq(NoticeRead::getNoticeId, noticeId);
        if (noticeReadMapper.selectCount(queryWrapper) > 0) {
            return; // 已经读过，直接返回
        }

        NoticeRead noticeRead = new NoticeRead();
        noticeRead.setUserId(userId);
        noticeRead.setNoticeId(noticeId);
        noticeReadMapper.insert(noticeRead);
        log.info("用户 {} 标记公告 {} 为已读", userId, noticeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        // 查询所有未读公告（未删除且status为1）
        LambdaQueryWrapper<Notice> noticeWrapper = new LambdaQueryWrapper<>();
        noticeWrapper.eq(Notice::getStatus, 1);
        List<Long> readNoticeIds = getReadNoticeIds(userId);
        if (!readNoticeIds.isEmpty()) {
            noticeWrapper.notIn(Notice::getId, readNoticeIds);
        }

        List<Notice> unreadNotices = noticeMapper.selectList(noticeWrapper);
        if (unreadNotices.isEmpty()) {
            return;
        }

        for (Notice notice : unreadNotices) {
            NoticeRead noticeRead = new NoticeRead();
            noticeRead.setUserId(userId);
            noticeRead.setNoticeId(notice.getId());
            noticeReadMapper.insert(noticeRead);
        }
        log.info("用户 {} 一键标记了所有公告为已读", userId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notice> noticeWrapper = new LambdaQueryWrapper<>();
        noticeWrapper.eq(Notice::getStatus, 1);
        List<Long> readNoticeIds = getReadNoticeIds(userId);
        if (readNoticeIds.isEmpty()) {
            return noticeMapper.selectCount(noticeWrapper).intValue();
        } else {
            noticeWrapper.notIn(Notice::getId, readNoticeIds);
            return noticeMapper.selectCount(noticeWrapper).intValue();
        }
    }

    @Override
    public List<Long> getReadNoticeIds(Long userId) {
        LambdaQueryWrapper<NoticeRead> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NoticeRead::getUserId, userId);
        List<NoticeRead> list = noticeReadMapper.selectList(queryWrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(NoticeRead::getNoticeId).collect(Collectors.toList());
    }
}
