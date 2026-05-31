package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysPlayer;
import com.ruoyi.system.service.ISysPlayerService;

/**
 * 玩家管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/player")
public class SysPlayerController extends BaseController {

    @Autowired
    private ISysPlayerService playerService;

    /**
     * 获取玩家列表
     */
    @PreAuthorize("@ss.hasPermi('system:player:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPlayer player) {
        startPage();
        List<SysPlayer> list = playerService.selectPlayerList(player);
        return getDataTable(list);
    }

    /**
     * 获取所有玩家（下拉选择用）
     */
    @GetMapping("/all")
    public AjaxResult getAllPlayers() {
        return success(playerService.selectPlayerList(new SysPlayer()));
    }

    /**
     * 导出玩家列表
     */
    @PreAuthorize("@ss.hasPermi('system:player:export')")
    @Log(title = "玩家管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysPlayer player) {
        List<SysPlayer> list = playerService.selectPlayerList(player);
        ExcelUtil<SysPlayer> util = new ExcelUtil<SysPlayer>(SysPlayer.class);
        return util.exportExcel(list, "玩家数据");
    }

    /**
     * 获取玩家详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:player:query')")
    @GetMapping("/{playerId}")
    public AjaxResult getInfo(@PathVariable("playerId") Long playerId) {
        return success(playerService.selectPlayerById(playerId));
    }

    /**
     * 新增玩家
     */
    @PreAuthorize("@ss.hasPermi('system:player:add')")
    @Log(title = "玩家管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPlayer player) {
        return toAjax(playerService.insertPlayer(player));
    }

    /**
     * 修改玩家
     */
    @PreAuthorize("@ss.hasPermi('system:player:edit')")
    @Log(title = "玩家管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPlayer player) {
        return toAjax(playerService.updatePlayer(player));
    }

    /**
     * 删除玩家
     */
    @PreAuthorize("@ss.hasPermi('system:player:remove')")
    @Log(title = "玩家管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{playerIds}")
    public AjaxResult remove(@PathVariable Long[] playerIds) {
        return toAjax(playerService.deletePlayerByIds(playerIds));
    }
}
