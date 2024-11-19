package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.GroupCodeDto;
import com.swproject.hereforus.dto.GroupDto;
import com.swproject.hereforus.entity.Festival;
import com.swproject.hereforus.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@Tag(name = "Group", description = "그룹 관련 REST API에 대한 명세를 제공합니다.")
@RestController
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Operation(
            summary = "그룹 코드 조회",
            description = "회원 등록 시 생성된 그룹 코드를 조회합니다. 그룹 코드를 통해 상대방을 초대하거나, 상대방으로부터 초대받을 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 그룹 코드 데이터를 반환합니다.",
                            content = @Content(schema = @Schema(implementation = GroupCodeDto.class))),
            }
    )
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

    @Operation(
            summary = "그룹 프로필 조회",
            description = "그룹의 프로필을 조회할 수 있습니다. 그룹 닉네임, 기념일, 대표 이미지 등을       ",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 그룹 코드 데이터를 반환합니다.",
                            content = @Content(schema = @Schema(implementation = GroupCodeDto.class))),
            }
    )
    @GetMapping
    public ResponseEntity<Optional<GroupDto>> getGroupProfile() {
        Optional<GroupDto> profile = groupService.fetchGroupProfile();
        if (profile.isPresent()) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
            summary = "그룹 코드 조회",
            description = "회원 등록 시 생성된 그룹 코드를 조회합니다. 그룹 코드를 통해 상대방을 초대하거나, 상대방으로부터 초대받을 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 그룹 코드 데이터를 반환합니다.",
                            content = @Content(schema = @Schema(implementation = GroupCodeDto.class))),
            }
    )
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
