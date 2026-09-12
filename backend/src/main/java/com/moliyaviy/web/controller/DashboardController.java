package com.moliyaviy.web.controller;

import com.moliyaviy.web.dto.DashboardResponse;
import com.moliyaviy.web.exception.InvalidRequestException;
import com.moliyaviy.web.security.CurrentUser;
import com.moliyaviy.web.service.DashboardService;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse getDashboard(Authentication authentication, @RequestParam("month") String month) {
        return dashboardService.getDashboard(CurrentUser.id(authentication), parseMonth(month));
    }

    private YearMonth parseMonth(String value) {
        try {
            return YearMonth.parse(value);
        } catch (DateTimeParseException ex) {
            throw new InvalidRequestException("month must be in format YYYY-MM, e.g. 2026-09");
        }
    }

}
