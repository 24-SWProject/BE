package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.GroupDto;
import com.swproject.hereforus.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @ResponseBody
    @GetMapping
    public ResponseEntity<Optional<GroupDto>> getGroupCode() {
        Optional<GroupDto> group = groupService.getGroup();
        if (group.isPresent()) {
            return ResponseEntity.ok(group);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
