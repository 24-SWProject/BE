package com.swproject.hereforus.service;

import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.GroupCodeDto;
import com.swproject.hereforus.dto.GroupDto;
import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
//        Optional<Group> group = groupRepository.findByInviter(Long.valueOf("1"));
        return group.map(g -> modelMapper.map(g, GroupCodeDto.class));
    }

    // 속해있는 그룹 프로필 조회
    public Optional<GroupDto> fetchGroupProfile() {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = findGroupForUser(user.getId());
//        Optional<Group> group = findGroupForUser(Long.valueOf("1"));
        return group.map(g -> modelMapper.map(g, GroupDto.class));
    }

    // 그룹 프로필 수정
    // inviter 찾아서 그 group에 저장
    public GroupDto saveGroupProfile(GroupDto groupDto) {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupRepository.findByInviter(user.getId());
//        Optional<Group> group = groupRepository.findByInviter(Long.valueOf("1"));
        Group existingGroup = group.get();

        existingGroup.setNickName(groupDto.getNickName());
        existingGroup.setAnniversary(groupDto.getAnniversary());
        existingGroup.setProfileImg(groupDto.getProfileImg());

        Group updatedProfile = groupRepository.save(existingGroup);

        return modelMapper.map(updatedProfile, GroupDto.class);
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

    // 그룹 참여 최대 2명 제한 확인
    public String checkNumberOfGroup(Group group, User user) {
        if (group.getInvitee() != null && group.getInvitee().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 그룹에 참여하였거나, 그룹 인원이 초과되었습니다.");
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
        //        User user = userDetailService.getAuthenticatedUserId();
        //        Optional<Group> group = findGroupForUser(user.getId());
        Optional<Group> group = findGroupForUser(Long.valueOf("1"));
        LocalDate anniversary = group.get().getAnniversary();
        LocalDate today = LocalDate.now();

        // 현재 D-Day 계산
        long dDay = ChronoUnit.DAYS.between(anniversary, today) + 1;

        // 다가올 주요 주기 계산
        List<Map<String, Object>> milestoneList = new ArrayList<>();
        int nextHundreds = ((int) ((dDay - 1) / 100) + 1) * 100;
        int nextFirstYear = ((int) ((dDay - 1) / 365) + 1) * 365;
        System.out.println(nextFirstYear);
        System.out.println(nextHundreds);

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
}

