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

import java.util.Optional;


@Configuration
@RequiredArgsConstructor
@Service
public class GroupService {
    private final ModelMapper modelMapper;
    private final GroupRepository groupRepository;
    private final UserDetailService userDetailService;

    // 그룹 코드 조회
    public Optional<GroupCodeDto> fetchGroupCode() {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupRepository.findByUserId(user.getId());
        return group.map(g -> modelMapper.map(g, GroupCodeDto.class));
    }

    // 그룹 프로필 조회
    public Optional<GroupDto> fetchGroupProfile() {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupRepository.findByUserId(user.getId());
        return group.map(g -> modelMapper.map(g, GroupDto.class));
    }

    // 그룹 프로필 수정
    // inviter 찾아서 그 group에 저장
    public GroupDto saveGroupProfile(GroupDto groupDto) {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupRepository.findByUserId(user.getId());
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


    // 현재 사용자가 그룹의 초대자인지 확인
    public String checkIsInViter() {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupRepository.findByUserId(user.getId());
        if (!group.get().getInviter().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "그룹의 초대자만 접근할 수 있습니다.");
        }
        return null;
    }
}

