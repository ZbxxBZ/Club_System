package com.bistu.clubsystembackend.serviceImpl;

import com.bistu.clubsystembackend.entity.AuthSessionInfo;
import com.bistu.clubsystembackend.entity.AuthUser;
import com.bistu.clubsystembackend.entity.request.LoginRequest;
import com.bistu.clubsystembackend.entity.request.RefreshTokenRequest;
import com.bistu.clubsystembackend.entity.request.RegisterRequest;
import com.bistu.clubsystembackend.entity.request.SsoLoginRequest;
import com.bistu.clubsystembackend.entity.response.AuthMeResponseData;
import com.bistu.clubsystembackend.entity.response.LoginResponseData;
import com.bistu.clubsystembackend.entity.response.RegisterResponseData;
import com.bistu.clubsystembackend.mapper.AuthMapper;
import com.bistu.clubsystembackend.service.AuthService;
import com.bistu.clubsystembackend.util.BizCode;
import com.bistu.clubsystembackend.util.BusinessException;
import com.bistu.clubsystembackend.util.IdGenerator;
import com.bistu.clubsystembackend.util.JwtTokenUtil;
import com.bistu.clubsystembackend.util.RoleCode;
import com.bistu.clubsystembackend.util.UserStatusUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{4,30}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,64}$");

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final IdGenerator idGenerator;

    public AuthServiceImpl(AuthMapper authMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenUtil jwtTokenUtil,
                           IdGenerator idGenerator) {
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.idGenerator = idGenerator;
    }

    @Override
    public LoginResponseData login(LoginRequest request) {
        String username = normalize(request.getUsername());
        String password = request.getPassword();
        validateUsername(username);

        AuthUser user = authMapper.findUserByUsername(username);
        if (user == null) {
            throw new BusinessException(BizCode.ACCOUNT_NOT_FOUND);
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(BizCode.ACCOUNT_DISABLED);
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(BizCode.PASSWORD_INCORRECT);
        }

        return buildLoginResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResponseData register(RegisterRequest request) {
        String username = normalize(request.getUsername());
        String password = request.getPassword();
        validateUsername(username);
        validatePassword(password);

        if (authMapper.countUserByUsername(username) > 0) {
            throw new BusinessException(BizCode.ACCOUNT_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        AuthUser user = new AuthUser();
        user.setId(idGenerator.nextId());
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRealName(username);
        user.setStatus(1);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        authMapper.insertUser(user);

        Long roleId = authMapper.findRoleIdByCode(RoleCode.STUDENT);
        if (roleId == null) {
            long newRoleId = idGenerator.nextId();
            authMapper.insertRole(newRoleId, RoleCode.STUDENT, "学生", now, now);
            roleId = newRoleId;
        }
        authMapper.insertUserRole(idGenerator.nextId(), user.getId(), roleId, now, now);
        return new RegisterResponseData(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponseData ssoLogin(SsoLoginRequest request) {
        String username = resolveSsoUsername(request.getTicket());
        AuthUser user = authMapper.findUserByUsername(username);
        if (user == null) {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(username);
            registerRequest.setPassword("SsoDefault123");
            register(registerRequest);
            user = authMapper.findUserByUsername(username);
        }
        if (user == null) {
            throw new BusinessException(BizCode.SYSTEM_ERROR);
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(BizCode.ACCOUNT_DISABLED);
        }
        return buildLoginResponse(user);
    }

    @Override
    public AuthMeResponseData me(Long userId) {
        AuthSessionInfo info = authMapper.findSessionInfoByUserId(userId);
        if (info == null) {
            throw new BusinessException(BizCode.LOGIN_EXPIRED);
        }
        String roleCode = info.getRoleCode() == null ? RoleCode.STUDENT : info.getRoleCode();
        String status = UserStatusUtil.toStatusText(info.getStatus(), info.getGraduateAt());
        return new AuthMeResponseData(
                info.getUserId(),
                info.getUsername(),
                info.getRealName(),
                roleCode,
                status,
                info.getClubId(),
                permissionsByRole(roleCode)
        );
    }

    @Override
    public LoginResponseData refreshToken(RefreshTokenRequest request) {
        Claims claims = jwtTokenUtil.parseToken(request.getRefreshToken());
        if (!"REFRESH".equals(claims.get("tokenType"))) {
            throw new BusinessException(BizCode.LOGIN_EXPIRED);
        }
        Long userId = claims.get("userId", Long.class);
        AuthSessionInfo info = authMapper.findSessionInfoByUserId(userId);
        if (info == null) {
            throw new BusinessException(BizCode.LOGIN_EXPIRED);
        }
        String status = UserStatusUtil.toStatusText(info.getStatus(), info.getGraduateAt());
        if (!"ACTIVE".equals(status)) {
            throw new BusinessException(BizCode.ACCOUNT_STATUS_INVALID);
        }
        String roleCode = info.getRoleCode() == null ? RoleCode.STUDENT : info.getRoleCode();
        String accessToken = jwtTokenUtil.generateToken(info.getUserId(), info.getUsername(), roleCode);
        String refreshToken = jwtTokenUtil.generateRefreshToken(info.getUserId(), info.getUsername(), roleCode);
        return new LoginResponseData(
                accessToken,
                accessToken,
                refreshToken,
                info.getUserId(),
                info.getUsername(),
                info.getRealName(),
                roleCode,
                status,
                info.getClubId()
        );
    }

    @Override
    public void logout(Long userId) {
        AuthSessionInfo info = authMapper.findSessionInfoByUserId(userId);
        if (info == null) {
            throw new BusinessException(BizCode.LOGIN_EXPIRED);
        }
    }

    private void validateUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException(BizCode.PARAM_INVALID);
        }
    }

    private void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(BizCode.PASSWORD_WEAK);
        }
    }

    private String normalize(String text) {
        return text == null ? null : text.trim();
    }

    private String resolveSsoUsername(String ticket) {
        String normalized = normalize(ticket);
        if (normalized == null || normalized.isBlank()) {
            throw new BusinessException(BizCode.PARAM_INVALID);
        }
        if (normalized.contains(":")) {
            String[] parts = normalized.split(":");
            return parts[parts.length - 1];
        }
        return normalized;
    }

    private List<String> permissionsByRole(String roleCode) {
        return switch (roleCode) {
            case RoleCode.CLUB_ADMIN -> List.of("club:info:manage", "club:event:manage", "club:finance:manage");
            case RoleCode.SCHOOL_ADMIN ->
                    List.of("school:club:approval", "school:finance:audit", "school:data:stats", "school:user-status:manage");
            default -> List.of("student:club:browse", "student:club:join", "student:event:signup");
        };
    }

    private LoginResponseData buildLoginResponse(AuthUser user) {
        String roleCode = authMapper.findPrimaryRoleCode(user.getId());
        if (roleCode == null || roleCode.isBlank()) {
            roleCode = RoleCode.STUDENT;
        }
        Long clubId = authMapper.findPrimaryClubId(user.getId(), roleCode);
        String accessToken = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), roleCode);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getUsername(), roleCode);
        LocalDateTime now = LocalDateTime.now();
        authMapper.updateLastLoginAt(user.getId(), now, now);
        return new LoginResponseData(
                accessToken,
                accessToken,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                roleCode,
                UserStatusUtil.toStatusText(user.getStatus(), null),
                clubId
        );
    }
}
