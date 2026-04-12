package com.bistu.clubsystembackend.mapper;

import com.bistu.clubsystembackend.entity.AuthUser;
import com.bistu.clubsystembackend.entity.AuthSessionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface AuthMapper {

    AuthUser findUserByUsername(@Param("username") String username);

    long countUserByUsername(@Param("username") String username);

    int insertUser(AuthUser user);

    Long findRoleIdByCode(@Param("roleCode") String roleCode);

    int insertRole(@Param("id") Long id,
                   @Param("roleCode") String roleCode,
                   @Param("roleName") String roleName,
                   @Param("createdAt") LocalDateTime createdAt,
                   @Param("updatedAt") LocalDateTime updatedAt);

    int insertUserRole(@Param("id") Long id,
                       @Param("userId") Long userId,
                       @Param("roleId") Long roleId,
                       @Param("createdAt") LocalDateTime createdAt,
                       @Param("updatedAt") LocalDateTime updatedAt);

    String findPrimaryRoleCode(@Param("userId") Long userId);

    int updateLastLoginAt(@Param("userId") Long userId,
                          @Param("lastLoginAt") LocalDateTime lastLoginAt,
                          @Param("updatedAt") LocalDateTime updatedAt);

    AuthSessionInfo findSessionInfoByUserId(@Param("userId") Long userId);

    Long findPrimaryClubId(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    AuthUser findUserById(@Param("userId") Long userId);

    int updateUserRealName(@Param("userId") Long userId,
                           @Param("realName") String realName,
                           @Param("updatedAt") LocalDateTime updatedAt);

    int updateUserPassword(@Param("userId") Long userId,
                           @Param("passwordHash") String passwordHash,
                           @Param("updatedAt") LocalDateTime updatedAt);

    int updateUserBasicInfo(@Param("userId") Long userId,
                            @Param("realName") String realName,
                            @Param("studentNo") String studentNo,
                            @Param("updatedAt") LocalDateTime updatedAt);

    int updateUserContact(@Param("userId") Long userId,
                          @Param("phone") String phone,
                          @Param("email") String email,
                          @Param("updatedAt") LocalDateTime updatedAt);

    AuthUser findFullUserById(@Param("userId") Long userId);
}
