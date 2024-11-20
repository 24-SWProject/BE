package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.dto.GroupCodeDto;
import com.swproject.hereforus.dto.GroupDto;
import com.swproject.hereforus.entity.Festival;
import com.swproject.hereforus.service.GroupService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Group", description = "그룹 관련 REST API에 대한 명세를 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;

    @Operation(
            summary = "그룹 코드 조회",
            description = "회원 등록 시 생성된 그룹 코드를 조회합니다. 그룹 코드를 통해 상대방을 초대하거나, 상대방으로부터 초대받을 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GroupDto.class))),
                    @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(example = "{\"error\":\"그룹 코드 조회를 실패하였습니다.\"}")))
            }
    )
    @ResponseBody
    @GetMapping("/code")
    public ResponseEntity<?> getGroupCode() {
        try {
            Optional<GroupCodeDto> code = groupService.fetchGroupCode();
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto("그룹 코드 조회를 실패하였습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @Operation(
            summary = "그룹 프로필 조회",
            description = "그룹의 프로필을 조회할 수 있습니다. 그룹 닉네임, 기념일, 대표 이미지 등이 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GroupDto.class))),
                    @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(example = "{\"error\":\"그룹 프로필 조회를 실패하였습니다.\"}")))
            }
    )
    @GetMapping
    public ResponseEntity<?> getGroupProfile() {
        try {
            Optional<GroupDto> profile = groupService.fetchGroupProfile();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto("그룹 프로필 조회를 실패하였습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @Operation(
            summary = "그룹 프로필 수정",
            description = "그룹의 프로필을 수정할 수 있습니다. 그룹을 생성한 사용자(초대자)가 그룹 닉네임, 기념일, 대표 이미지를 선택적으로 수정할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GroupDto.class))),
                    @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(example = "{\"error\":\"그룹 프로필 조회를 실패하였습니다.\"}")))
            }
    )
    @PutMapping
    public ResponseEntity<?> updateGroupProfile(@RequestBody GroupDto groupDto) {
        try {
            GroupDto updatedProfile = groupService.saveGroupProfile(groupDto);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto("그룹 프로필 조회를 실패하였습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @Hidden
    @Operation(
            summary = "그룹 참여",
            description = "초대자로부터 받은 코드를 통해 그룹에 참여할 수 있습니다. 최대 인원은 2명입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GroupDto.class))),
                    @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content(schema = @Schema(example = "{\"error\":\"그룹 프로필 조회를 실패하였습니다.\"}")))
            }
    )
    @PostMapping
    public ResponseEntity<?> joinGroupByCode() {
        return null;
    }
}
