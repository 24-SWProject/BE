package com.swproject.hereforus.service;

import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.group.GroupCodeDto;
import com.swproject.hereforus.dto.group.GroupDto;
import com.swproject.hereforus.dto.group.GroupOutputDto;
import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Configuration
@RequiredArgsConstructor
@Service
public class GroupService {
    private final ModelMapper modelMapper;
    private final GroupRepository groupRepository;
    private final UserDetailService userDetailService;

    // 자신의 그룹 코드 조회
    public Optional<GroupCodeDto> fetchGroupCode() {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupRepository.findByInviter(user.getId());
        return group.map(g -> modelMapper.map(g, GroupCodeDto.class));
    }

    // 속해있는 그룹 프로필 조회
    public GroupOutputDto fetchGroupProfile() {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = findGroupForUser(user.getId());

        if (group.isPresent()) {
            Group groupEntity = group.get();
            GroupOutputDto dto = modelMapper.map(groupEntity, GroupOutputDto.class);

            // Base64로 변환된 이미지 설정
            if (groupEntity.getProfileImg() != null) {
                String base64ProfileImg = Base64.getEncoder().encodeToString(groupEntity.getProfileImg());
                dto.setProfileImg(base64ProfileImg);
            }

            return dto;
        }
        return null;
    }

    // 그룹 프로필 수정
    // inviter 찾아서 그 group에 저장
    public GroupOutputDto saveGroupProfile(GroupDto groupDto) throws IOException {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupRepository.findByInviter(user.getId());
        Group existingGroup = group.get();

        existingGroup.setNickName(groupDto.getNickName());
        existingGroup.setAnniversary(groupDto.getAnniversary());
        existingGroup.setProfileImg(groupDto.getProfileImg() != null ? groupDto.getProfileImg().getBytes() : null);

        Group updatedProfile = groupRepository.save(existingGroup);
        GroupOutputDto dto = modelMapper.map(updatedProfile, GroupOutputDto.class);

        if (updatedProfile.getProfileImg() != null) {
            String base64ProfileImg = Base64.getEncoder().encodeToString(updatedProfile.getProfileImg());
            dto.setProfileImg(base64ProfileImg);
        }
        return dto;
    }

    // 받은 초대코드로 그룹id 조회 및 참여
    public GroupDto saveInviteeByCode(String code) {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupRepository.findById(code);
        Group existingGroup = group.get();

        checkNumberOfGroup(existingGroup, user);

        // 현재 사용자를 invitee로 저장
        existingGroup.setInvitee(user);
        Group updatedInvitee = groupRepository.save(existingGroup);

        return modelMapper.map(updatedInvitee, GroupDto.class);
    }

    // 그룹 참여 확인
    public String checkNumberOfGroup(Group group, User user) {
        if (group.getInvitee() != null && group.getInvitee().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 그룹에 참여하였거나, 그룹 인원이 초과되었습니다.");
        } else if (group.getInviter().getGroup().equals(user.getId())) {
            throw new CustomException(HttpStatus.CONFLICT, "그룹의 초대자는 참여할 수 없습니다.");
        }
        return null;
    }


    /** 초대받은 그룹 ID 조회 */
    public Optional<Group> findGroupForUser(Long userId) {
        // invitee로 그룹 우선 조회
        Optional<Group> inviteeGroup = groupRepository.findByInvitee(userId);

        if (inviteeGroup.isPresent()) {
            return inviteeGroup;
        }

        // 없으면 inviter로 그룹 조회
        return groupRepository.findByInviter(userId);
    }

    public Map<String, Object> selectGroupAnniversary() {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = findGroupForUser(user.getId());
        LocalDate anniversary = group.get().getAnniversary();

        if (anniversary == null) {
            return Collections.emptyMap();
        }

        LocalDate today = LocalDate.now();

        // 현재 D-Day 계산
        long dDay = ChronoUnit.DAYS.between(anniversary, today) + 1;

        // 다가올 주요 주기 계산
        List<Map<String, Object>> milestoneList = new ArrayList<>();
        int nextHundreds = ((int) ((dDay - 1) / 100) + 1) * 100;
        int nextFirstYear = ((int) ((dDay - 1) / 365) + 1) * 365;

        // 100일 단위 추가 (최대 8개)
        for (int i = 0; i < 9; i++) {
            int milestoneDay = nextHundreds + (i * 100);
            LocalDate milestoneDate = anniversary.plusDays(milestoneDay - 1);
            long daysUntilMilestone = ChronoUnit.DAYS.between(today, milestoneDate);

            Map<String, Object> milestoneDetail = new LinkedHashMap<>();
            milestoneDetail.put("day", milestoneDay);
            milestoneDetail.put("date", milestoneDate);
            milestoneDetail.put("remain", daysUntilMilestone);

            milestoneList.add(milestoneDetail);
        }

        // 연 단위 추가 (최대 3년)
        for (int i = 0; i < 3; i++) {
            int milestoneDay = nextFirstYear + (i * 365);
            LocalDate milestoneDate = anniversary.plusDays(milestoneDay + 1);
            long daysUntilMilestone = ChronoUnit.DAYS.between(today, milestoneDate);

            Map<String, Object> milestoneDetail = new LinkedHashMap<>();
            milestoneDetail.put("day", milestoneDay);
            milestoneDetail.put("date", milestoneDate);
            milestoneDetail.put("remain", daysUntilMilestone);

            milestoneList.add(milestoneDetail);
        }

        // 날짜 기준으로 오름차순 정렬
        milestoneList.sort(Comparator.comparing(m -> (LocalDate) m.get("date")));

        // 결과 반환
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("currentDay", dDay);
        result.put("milestones", milestoneList);

        return result;
    }

    // 그룹 코드 생성
    public String generateCode() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+~";
        Integer codeLength = 8;
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }
}

