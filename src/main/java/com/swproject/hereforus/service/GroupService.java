package com.swproject.hereforus.service;

import com.swproject.hereforus.dto.GroupDto;
import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.GroupRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GroupRepository groupRepository;

    public Optional<GroupDto> getGroup() {
        Long userId = getAuthenticatedUserId(); // 인증된 사용자 ID 가져오기
        Optional<Group> group  = groupRepository.findByOwnerId(userId);
        return group.map(g -> modelMapper.map(g, GroupDto.class));
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        return authenticatedUser.getId();
    }
}
