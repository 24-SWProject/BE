package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.GroupCodeDto;
import com.swproject.hereforus.dto.GroupDto;
import com.swproject.hereforus.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @ResponseBody
    @GetMapping("/code")
    public ResponseEntity<Optional<GroupCodeDto>> getGroupCode() {
        Optional<GroupCodeDto> code = groupService.fetchGroupCode();
        if (code.isPresent()) {
            return ResponseEntity.ok(code);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<Optional<GroupDto>> getGroupProfile() {
        Optional<GroupDto> profile = groupService.fetchGroupProfile();
        if (profile.isPresent()) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping
    public ResponseEntity<GroupDto> updateGroupProfile(@RequestBody GroupDto groupDto) {
        try {
            GroupDto updatedProfile = groupService.saveGroupProfile(groupDto);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
