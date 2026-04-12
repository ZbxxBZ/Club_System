package com.bistu.clubsystembackend.controller;

import com.bistu.clubsystembackend.entity.request.ClubApplySubmitRequest;
import com.bistu.clubsystembackend.entity.request.ClubJoinApplySubmitRequest;
import com.bistu.clubsystembackend.entity.request.StudentCheckinRequest;
import com.bistu.clubsystembackend.entity.request.UpdateProfileRequest;
import com.bistu.clubsystembackend.entity.response.ClubApplyMineItem;
import com.bistu.clubsystembackend.entity.response.ClubApplySubmitData;
import com.bistu.clubsystembackend.entity.response.ClubJoinApplyMineItem;
import com.bistu.clubsystembackend.entity.response.ClubJoinApplySubmitData;
import com.bistu.clubsystembackend.entity.response.MyJoinedClubItem;
import com.bistu.clubsystembackend.entity.response.ClubPublicItem;
import com.bistu.clubsystembackend.entity.response.EventDetailData;
import com.bistu.clubsystembackend.entity.response.EventOpenItem;
import com.bistu.clubsystembackend.entity.response.FileUploadData;
import com.bistu.clubsystembackend.entity.response.PageResponseData;
import com.bistu.clubsystembackend.entity.response.UserProfileData;
import com.bistu.clubsystembackend.service.StudentPermissionService;
import com.bistu.clubsystembackend.util.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class StudentController {

    private final StudentPermissionService userPermissionService;

    public StudentController(StudentPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }

    @GetMapping("/clubs/public")
    public ApiResponse<PageResponseData<ClubPublicItem>> listPublicClubs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isRecruiting,
            @RequestParam(defaultValue = "true") Boolean onlyApproved,
            @RequestParam(defaultValue = "4") Integer applyStatus,
            @RequestParam(defaultValue = "ACTIVE") String status) {
        return ApiResponse.success(userPermissionService.listPublicClubs(
                pageNum,
                pageSize,
                keyword,
                category,
                isRecruiting,
                onlyApproved,
                applyStatus,
                status
        ));
    }

    @PostMapping("/files/upload")
    public ApiResponse<FileUploadData> uploadFile(@RequestPart("file") MultipartFile file,
                                                  @RequestParam("bizType") String bizType) {
        return ApiResponse.success(userPermissionService.uploadClubApplyFile(file, bizType));
    }

    @PostMapping("/clubs/apply")
    public ApiResponse<ClubApplySubmitData> submitClubApply(@Valid @RequestBody ClubApplySubmitRequest request) {
        return ApiResponse.success("submit success", userPermissionService.submitClubApply(request));
    }

    @GetMapping("/clubs/apply/mine")
    public ApiResponse<PageResponseData<ClubApplyMineItem>> listMyClubApplyRecords(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyClubApplyRecords(pageNum, pageSize));
    }

    @PostMapping("/clubs/{clubId}/join")
    public ApiResponse<ClubJoinApplySubmitData> joinClub(@PathVariable Long clubId,
                                                          @Valid @RequestBody(required = false) ClubJoinApplySubmitRequest request) {
        ClubJoinApplySubmitRequest safeRequest = request == null ? new ClubJoinApplySubmitRequest() : request;
        return ApiResponse.success("加社申请已提交", userPermissionService.joinClub(clubId, safeRequest));
    }

    @GetMapping("/clubs/join/mine")
    public ApiResponse<PageResponseData<ClubJoinApplyMineItem>> listMyJoinApplies(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyJoinApplies(pageNum, pageSize));
    }

    @GetMapping("/clubs/joined/mine")
    public ApiResponse<PageResponseData<MyJoinedClubItem>> listMyJoinedClubs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyJoinedClubs(pageNum, pageSize));
    }

    @GetMapping("/events/open")
    public ApiResponse<PageResponseData<EventOpenItem>> listOpenEvents(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "8") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean onlyMyClubs) {
        return ApiResponse.success(userPermissionService.listOpenEvents(pageNum, pageSize, keyword, onlyMyClubs));
    }

    @PostMapping("/events/{eventId}/signup")
    public ApiResponse<Void> signupEvent(@PathVariable Long eventId) {
        userPermissionService.signupEvent(eventId);
        return ApiResponse.success("signup success", null);
    }

    @GetMapping("/events/{eventId}")
    public ApiResponse<EventDetailData> getEventDetail(@PathVariable Long eventId) {
        return ApiResponse.success(userPermissionService.getEventDetail(eventId));
    }

    @PostMapping("/events/{eventId}/cancel-signup")
    public ApiResponse<Void> cancelEventSignup(@PathVariable Long eventId) {
        userPermissionService.cancelEventSignup(eventId);
        return ApiResponse.success("取消报名成功", null);
    }

    @PostMapping("/events/checkin")
    public ApiResponse<Void> checkinByCode(@Valid @RequestBody StudentCheckinRequest request) {
        userPermissionService.checkinByCode(request);
        return ApiResponse.success("签到成功", null);
    }

    @PatchMapping("/user/profile")
    public ApiResponse<Void> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        userPermissionService.updateProfile(request);
        return ApiResponse.success("个人信息更新成功", null);
    }

    @GetMapping("/user/profile")
    public ApiResponse<UserProfileData> getProfile() {
        return ApiResponse.success(userPermissionService.getUserProfile());
    }

    @PatchMapping("/user/profile/basic")
    public ApiResponse<Void> updateBasicInfo(@Valid @RequestBody UpdateProfileRequest request) {
        userPermissionService.updateBasicInfo(request);
        return ApiResponse.success("基本信息更新成功", null);
    }

    @PatchMapping("/user/profile/password")
    public ApiResponse<Void> updatePassword(@Valid @RequestBody UpdateProfileRequest request) {
        userPermissionService.updatePassword(request);
        return ApiResponse.success("密码修改成功", null);
    }

    @PatchMapping("/user/profile/contact")
    public ApiResponse<Void> updateContact(@Valid @RequestBody UpdateProfileRequest request) {
        userPermissionService.updateContactInfo(request);
        return ApiResponse.success("联系方式更新成功", null);
    }

    @PostMapping("/clubs/{clubId}/view")
    public ApiResponse<Void> trackClubView(@PathVariable Long clubId) {
        userPermissionService.trackClubView(clubId);
        return ApiResponse.success("ok", null);
    }

    @GetMapping("/clubs/hot")
    public ApiResponse<List<ClubPublicItem>> listHotClubs() {
        return ApiResponse.success(userPermissionService.listHotClubs());
    }
}
