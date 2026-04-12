package com.bistu.clubsystembackend.service;

import com.bistu.clubsystembackend.entity.request.ClubApplySubmitRequest;
import com.bistu.clubsystembackend.entity.request.StudentCheckinRequest;
import com.bistu.clubsystembackend.entity.request.UpdateProfileRequest;
import com.bistu.clubsystembackend.entity.request.ClubJoinApplySubmitRequest;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentPermissionService {

    PageResponseData<ClubPublicItem> listPublicClubs(int pageNum,
                                                     int pageSize,
                                                     String keyword,
                                                     String category,
                                                     Boolean isRecruiting,
                                                     Boolean onlyApproved,
                                                     Integer applyStatus,
                                                     String status);

    FileUploadData uploadClubApplyFile(MultipartFile file, String bizType);

    ClubApplySubmitData submitClubApply(ClubApplySubmitRequest request);

    PageResponseData<ClubApplyMineItem> listMyClubApplyRecords(int pageNum, int pageSize);

    ClubJoinApplySubmitData joinClub(Long clubId, ClubJoinApplySubmitRequest request);

    PageResponseData<ClubJoinApplyMineItem> listMyJoinApplies(int pageNum, int pageSize);

    PageResponseData<MyJoinedClubItem> listMyJoinedClubs(int pageNum, int pageSize);

    PageResponseData<EventOpenItem> listOpenEvents(int pageNum, int pageSize, String keyword, Boolean onlyMyClubs);

    void signupEvent(Long eventId);

    void checkinByCode(StudentCheckinRequest request);

    EventDetailData getEventDetail(Long eventId);

    void cancelEventSignup(Long eventId);

    void updateProfile(UpdateProfileRequest request);

    UserProfileData getUserProfile();

    void updateBasicInfo(UpdateProfileRequest request);

    void updatePassword(UpdateProfileRequest request);

    void updateContactInfo(UpdateProfileRequest request);

    void trackClubView(Long clubId);

    List<ClubPublicItem> listHotClubs();
}
