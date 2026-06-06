package com.demo.login.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.dto.NoticeDTO;
import com.demo.login.dto.NoticeQueryDTO;
import com.demo.login.entity.Notice;

/**
 * 公告服务接口
 *
 * @author Antigravity
 * @since 2026-06-06
 */
public interface INoticeService {

    /**
     * 分页查询公告列表
     *
     * @param queryDTO 查询条件
     * @param userId 当前用户ID
     * @return 公告分页数据
     */
    Page<Notice> getNoticeList(NoticeQueryDTO queryDTO, Long userId);

    /**
     * 根据ID查询公告详情
     *
     * @param id 公告ID
     * @return 公告详情
     */
    Notice getNoticeById(Long id);

    /**
     * 创建公告
     *
     * @param noticeDTO 公告信息
     * @param userId 操作人用户ID
     * @return 公告ID
     */
    Long createNotice(NoticeDTO noticeDTO, Long userId);

    /**
     * 更新公告
     *
     * @param id 公告ID
     * @param noticeDTO 公告信息
     * @param userId 操作人用户ID
     */
    void updateNotice(Long id, NoticeDTO noticeDTO, Long userId);

    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    void deleteNotice(Long id);

    /**
     * 更新公告状态
     *
     * @param id 公告ID
     * @param status 状态
     * @param userId 操作人用户ID
     */
    void updateNoticeStatus(Long id, Integer status, Long userId);
}
