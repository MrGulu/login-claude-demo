package com.demo.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.dto.NoticeDTO;
import com.demo.login.dto.NoticeQueryDTO;
import com.demo.login.entity.Notice;
import com.demo.login.entity.User;
import com.demo.login.mapper.NoticeMapper;
import com.demo.login.mapper.UserMapper;
import com.demo.login.service.INoticeReadService;
import com.demo.login.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 公告服务实现类
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Slf4j
@Service
public class NoticeServiceImpl implements INoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private INoticeReadService noticeReadService;

    @Override
    public Page<Notice> getNoticeList(NoticeQueryDTO queryDTO, Long userId) {
        Page<Notice> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like(Notice::getTitle, queryDTO.getTitle());
        }
        if (StringUtils.hasText(queryDTO.getAuthor())) {
            wrapper.like(Notice::getAuthor, queryDTO.getAuthor());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Notice::getStatus, queryDTO.getStatus());
        }

        // 默认按创建时间倒序排列
        wrapper.orderByDesc(Notice::getCreateTime);
        Page<Notice> resultPage = noticeMapper.selectPage(page, wrapper);

        if (userId != null && resultPage.getRecords() != null && !resultPage.getRecords().isEmpty()) {
            List<Long> readNoticeIds = noticeReadService.getReadNoticeIds(userId);
            for (Notice notice : resultPage.getRecords()) {
                if (readNoticeIds.contains(notice.getId())) {
                    notice.setReadStatus(1);
                } else {
                    notice.setReadStatus(0);
                }
            }
        }
        return resultPage;
    }

    @Override
    public Notice getNoticeById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNotice(NoticeDTO noticeDTO, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("当前操作用户不存在");
        }

        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);

        // 设置作者，优先使用昵称，没有则使用用户名
        String authorName = StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
        notice.setAuthor(authorName);
        notice.setCreateBy(user.getUsername());
        notice.setUpdateBy(user.getUsername());

        noticeMapper.insert(notice);

        log.info("创建公告成功，公告ID: {}, 作者: {}", notice.getId(), authorName);
        return notice.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(Long id, NoticeDTO noticeDTO, Long userId) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("当前操作用户不存在");
        }

        BeanUtils.copyProperties(noticeDTO, notice);
        notice.setId(id);
        notice.setUpdateBy(user.getUsername());

        noticeMapper.updateById(notice);

        log.info("更新公告成功，公告ID: {}, 操作人: {}", id, user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }

        noticeMapper.deleteById(id);
        log.info("删除公告成功，公告ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNoticeStatus(Long id, Integer status, Long userId) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("当前操作用户不存在");
        }

        notice.setStatus(status);
        notice.setUpdateBy(user.getUsername());
        noticeMapper.updateById(notice);

        log.info("更新公告状态成功，公告ID: {}, 状态: {}, 操作人: {}", id, status, user.getUsername());
    }
}
