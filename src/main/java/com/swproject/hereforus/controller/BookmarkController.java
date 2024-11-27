package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.repository.BookmarkRepository;
import com.swproject.hereforus.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookmark", description = "그룹이 찜한 공연/전시 북마크에 대한 명세입니다.")
@RestController
@RequestMapping("/api/auth/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(
            summary = "북마크 생성 및 삭제",
            description = " 사용자가 특정 공연, 축제 데이터를 북마크에 저장하거나 삭제할 수 있는 API입니다.\n" +
                          " 동일한 요청을 반복하면 이미 북마크된 상태일 경우 삭제, 그렇지 않을 경우 생성됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "공연 데이터 조회 성공.",
                            content = @Content(examples = {
                                    @ExampleObject(name = "북마크를 생성한 경우", value = "북마크가 생성되었습니다."),
                                    @ExampleObject(name = "북마크를 삭제한 경우", value = "북마크가 삭제되었습니다.")}
                            )),
                    @ApiResponse(
                            responseCode = "403",
                            description = "리소스에 접근할 권한이 없거나 인증 정보가 유효하지 않음",
                            content = @Content(schema = @Schema(example = "{\"error\":\"사용자 인증에 실패하였습니다.\"}"))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"))
                    )
            },
            parameters = {
                    @Parameter(
                            name = "type",
                            description = "북마크 대상의 카테고리 (festival, performance, food)",
                            example = "festival",
                            required = true
                    ),
                    @Parameter(
                            name = "id",
                            description = "북마크 대상의 고유 ID",
                            example = "272",
                            required = true
                    )
            }
    )
    @GetMapping("/{type}/{id}")
    public ResponseEntity<?> createBookmark(
            @PathVariable("type") String type,
            @PathVariable("id") Long id
    ) {
        try {
            Object result = bookmarkService.saveOrDeleteBookmark(type, id);
            return ResponseEntity.ok(result);
        } catch (CustomException e) {
            ErrorDto errorResponse = new ErrorDto(e.getStatus().value(), e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(
            summary = "북마크 조회",
            description = " 현재 그룹 사용자가 찜한 북마크 데이터를 조회하는 API입니다.\n" +
                          " 반환 데이터는 사용자가 저장한 공연, 축제, 음식점 정보의 세부 데이터와 타입 형태(예: festival, performance)로 구성됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "북마크 조회 성공",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = "리소스에 접근할 권한이 없거나 인증 정보가 유효하지 않음",
                            content = @Content(schema = @Schema(example = "{\"error\":\"사용자 인증에 실패하였습니다.\"}"))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<?> getBookmarks(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Object> bookmarks = bookmarkService.selectBookmarksByGroupId(pageable);
            return ResponseEntity.ok(bookmarks);
        } catch (CustomException e) {
            ErrorDto errorResponse = new ErrorDto(e.getStatus().value(), e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
