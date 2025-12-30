package com.contect.hello.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home Page");
        return "home";
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("name", "스프링 부트");
        model.addAttribute("isAdmin", true);
        return "hello";
    }

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("title", "Contects Page");
        return "contacts";
    }
}
