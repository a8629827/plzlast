package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.dto.UserDTO;
import com.lyj.securitydomo.service.PostService;
import com.lyj.securitydomo.service.ReportService;
import com.lyj.securitydomo.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@Log4j2
public class AdminController {
    private final UserService userService;
    private final ReportService reportService;
    private final PostService postService;

    public AdminController(UserService userService, ReportService reportService, PostService postService) {
        this.userService = userService;
        this.reportService = reportService;
        this.postService = postService;
    }

    @GetMapping("/adminIndex")
    public String adminIndex(Model model) {
        return "admin/adminIndex";
    }

    @GetMapping("/reports")
    public String getReports(@RequestParam(value = "filter", required = false, defaultValue = "all") String filter, Model model) {
        List<ReportDTO> reportList;
        if ("in-progress".equals(filter)) {
            reportList = reportService.getReportsInProgress();
        } else {
            reportList = reportService.getAllReports();
        }
        model.addAttribute("reportList", reportList);
        model.addAttribute("filter", filter);
        return "admin/reportList";
    }

    @GetMapping("/reports/{postId}")
    public String getReportsByPostId(@PathVariable Long postId, Model model) {
        List<ReportDTO> reports = reportService.getReportsByPostId(postId);
        model.addAttribute("reports", reports);
        return "redirect:/post/read/" + postId;
    }

    @PostMapping("/reports/{reportId}/hide")
    public String hideReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        reportService.markAsHidden(reportId);
        Long postId = reportService.getPostIdByReportId(reportId);
        postService.makePostInvisible(postId);
        redirectAttributes.addFlashAttribute("message", "신고글과 게시글이 비공개 처리되었습니다.");
        return "redirect:/admin/reports";
    }

    @PostMapping("/reports/{reportId}/reveal")
    public String revealReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        reportService.markAsVisible(reportId);
        Long postId = reportService.getPostIdByReportId(reportId);
        postService.makePostVisible(postId);
        redirectAttributes.addFlashAttribute("message", "신고글과 게시글이 공개 처리되었습니다.");
        return "redirect:/admin/reports";
    }

    @PostMapping("/reports/{reportId}/toggle-visibility")
    public ResponseEntity<Void> toggleVisibility(@PathVariable Long reportId, @RequestBody Map<String, Boolean> request) {
        Boolean isVisible = request.get("visible");
        if (isVisible != null) {
            if (isVisible) {
                reportService.markAsVisible(reportId);
            } else {
                reportService.markAsHidden(reportId);
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
    /**
     * 모든 사용자 목록을 조회하여 관리자 페이지에 전달
     */
    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<UserDTO> userList = userService.getAllUsers();
        model.addAttribute("userList", userList);
        return "admin/users"; // 사용자 목록을 보여주는 view 페이지로 이동
    }
}