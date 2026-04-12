package com.bistu.clubsystembackend.config;

import com.bistu.clubsystembackend.entity.AuthSessionInfo;
import com.bistu.clubsystembackend.mapper.AuthMapper;
import com.bistu.clubsystembackend.service.CacheService;
import com.bistu.clubsystembackend.util.AuthContextHolder;
import com.bistu.clubsystembackend.util.BizCode;
import com.bistu.clubsystembackend.util.BusinessException;
import com.bistu.clubsystembackend.util.CurrentUser;
import com.bistu.clubsystembackend.util.JwtTokenUtil;
import com.bistu.clubsystembackend.util.RoleCode;
import com.bistu.clubsystembackend.util.UserStatusUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthMapper authMapper;
    private final CacheService cacheService;

    public AuthInterceptor(JwtTokenUtil jwtTokenUtil, AuthMapper authMapper, CacheService cacheService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.authMapper = authMapper;
        this.cacheService = cacheService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(BizCode.LOGIN_EXPIRED);
        }
        String token = authHeader.substring(7).trim();
        Claims claims = jwtTokenUtil.parseToken(token);
        if (!"ACCESS".equals(claims.get("tokenType"))) {
            throw new BusinessException(BizCode.LOGIN_EXPIRED);
        }

        Long userId = claims.get("userId", Long.class);
        AuthSessionInfo info = cacheService.getSessionInfo(userId);
        if (info == null) {
            info = authMapper.findSessionInfoByUserId(userId);
            if (info == null) {
                throw new BusinessException(BizCode.LOGIN_EXPIRED);
            }
            cacheService.cacheSessionInfo(userId, info);
        }

        String status = UserStatusUtil.toStatusText(info.getStatus(), info.getGraduateAt());
        if (!"ACTIVE".equals(status)) {
            throw new BusinessException(BizCode.ACCOUNT_STATUS_INVALID);
        }
        String roleCode = info.getRoleCode() == null ? RoleCode.STUDENT : info.getRoleCode();
        AuthContextHolder.set(new CurrentUser(
                info.getUserId(),
                info.getUsername(),
                info.getRealName(),
                roleCode,
                info.getClubId(),
                status
        ));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContextHolder.clear();
    }
}
