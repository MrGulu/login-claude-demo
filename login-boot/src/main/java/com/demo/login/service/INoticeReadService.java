package com.demo.login.service;

import java.util.List;

/**
 * 公告已读关联服务接口
 *
 * @author Antigravity
 * @since 2026-06-06
 */
public interface INoticeReadService {

    /**
     * 标记单条公告为已读
     *
     * @param userId   用户ID
     * @param noticeId 公告ID
     */
    void markAsRead(Long userId, Long noticeId);

    /**
     * 一键标记所有公告为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

    /**
     * 获取当前用户的未读公告数
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    int getUnreadCount(Long userId);

    /**
     * 获取用户已读过的所有公告ID
     *
     * @param userId 用户ID
     * @return 已读的公告ID列表
     */
    List<Long> getReadNoticeIds(Long userId);
}
