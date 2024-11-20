package com.swproject.hereforus.service;

import com.swproject.hereforus.dto.GroupCodeDto;
import com.swproject.hereforus.dto.GroupDto;
import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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

        Group updatedGroup = groupRepository.save(existingGroup);

        return modelMapper.map(updatedGroup, GroupDto.class);
    }
}

