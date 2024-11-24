package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.repository.BookmarkRepository;
import com.swproject.hereforus.service.BookmarkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookmark", description = "그룹이 찜한 공연/전시 북마크에 대한 명세입니다.")
@RestController
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/{type}")
    public ResponseEntity<?> getBookmark(
            @PathVariable("type") String type,
            @RequestParam("id") Long id
    ) {
        try {
            System.out.println(type);
            System.out.println(id);
            Object result = bookmarkService.saveOrDeleteBookmark(type, id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
