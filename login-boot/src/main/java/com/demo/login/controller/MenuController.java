package com.demo.login.controller;

import com.demo.login.common.result.Result;
import com.demo.login.service.IMenuService;
import com.demo.login.vo.MenuTreeVO;
import com.demo.login.vo.UserMenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 菜单控制器
 *
 * @author Claude
 * @since 2024-03-06
 */
@Slf4j
@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    /**
     * 获取完整菜单树（用于权限分配）
     */
    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> getMenuTree() {
        List<MenuTreeVO> menuTree = menuService.getMenuTree();
        return Result.success(menuTree);
    }

    /**
     * 获取当前用户的菜单权限
     */
    @GetMapping("/user")
    public Result<List<UserMenuVO>> getUserMenus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<UserMenuVO> userMenus = menuService.getUserMenus(userId);
        return Result.success(userMenus);
    }
}
