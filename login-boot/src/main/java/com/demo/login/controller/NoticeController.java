package com.demo.login.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.annotation.RequirePermission;
import com.demo.login.common.result.Result;
import com.demo.login.dto.NoticeDTO;
import com.demo.login.dto.NoticeQueryDTO;
import com.demo.login.entity.Notice;
import com.demo.login.service.INoticeReadService;
import com.demo.login.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 公告控制器
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/notices")
public class NoticeController {

    @Autowired
    private INoticeService noticeService;

    @Autowired
    private INoticeReadService noticeReadService;

    /**
     * 分页查询公告列表
     */
    @GetMapping
    @RequirePermission("system:notice:query")
    public Result<Page<Notice>> getNoticeList(NoticeQueryDTO queryDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Notice> page = noticeService.getNoticeList(queryDTO, userId);
        return Result.success(page);
    }

    /**
     * 根据ID查询公告详情
     */
    @GetMapping("/{id}")
    @RequirePermission("system:notice:query")
    public Result<Notice> getNoticeById(@PathVariable Long id) {
        Notice notice = noticeService.getNoticeById(id);
        return Result.success(notice);
    }

    /**
     * 创建公告
     */
    @PostMapping
    @RequirePermission("system:notice:add")
    public Result<Long> createNotice(@Valid @RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long noticeId = noticeService.createNotice(noticeDTO, userId);
        return Result.success(noticeId);
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    @RequirePermission("system:notice:edit")
    public Result<Void> updateNotice(@PathVariable Long id, @Valid @RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.updateNotice(id, noticeDTO, userId);
        return Result.success();
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @RequirePermission("system:notice:delete")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }

    /**
     * 更新公告状态
     */
    @PutMapping("/{id}/status")
    @RequirePermission("system:notice:edit")
    public Result<Void> updateNoticeStatus(@PathVariable Long id, @RequestBody Map<String, Integer> statusMap, HttpServletRequest request) {
        Integer status = statusMap.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        Long userId = (Long) request.getAttribute("userId");
        noticeService.updateNoticeStatus(id, status, userId);
        return Result.success();
    }

    /**
     * 获取当前用户的未读公告数
     */
    @GetMapping("/unread-count")
    @RequirePermission("system:notice:query")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        int count = noticeReadService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记单条公告为已读
     */
    @PostMapping("/read/{id}")
    @RequirePermission("system:notice:query")
    public Result<Void> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeReadService.markAsRead(userId, id);
        return Result.success();
    }

    /**
     * 一键标记所有未读公告为已读
     */
    @PostMapping("/read-all")
    @RequirePermission("system:notice:query")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeReadService.markAllAsRead(userId);
        return Result.success();
    }
}
