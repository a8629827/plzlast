package com.lyj.securitydomo.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
public class SampleController {
    @GetMapping("/")
    public String home() {
        log.info("home");
        return "/home"; // index.html로 이동
    }

    @GetMapping("/user/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        log.info("login page accessed");
        if (error != null) {
            model.addAttribute("errorMessage", "로그인에 실패했습니다. 다시 시도해주세요.");
        }
        return "user/login";
    }

    @GetMapping("/all")
    public String exAll() {
        log.info("exAll");
        return "exAll"; // exAll.html로 이동
    }

    @GetMapping("/member")
    public void exMember() {
        log.info("exMember");
    }

    @GetMapping("sample/admin")
    public void exAdmin() {

        log.info("exAdmin");
    }
}