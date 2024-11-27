package com.swproject.hereforus.service;

import com.swproject.hereforus.entity.Bookmark;
import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.entity.event.Festival;
import com.swproject.hereforus.entity.event.Performance;
import com.swproject.hereforus.repository.BookmarkRepository;
import com.swproject.hereforus.repository.event.FestivalRepository;
import com.swproject.hereforus.repository.event.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final FestivalRepository festivalRepository;
    private final PerformanceRepository performanceRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserDetailService userDetailService;
    private final GroupService groupService;

    public Object saveOrDeleteBookmark(String type, Long referenceId) {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupService.findGroupForUser(user.getId());
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByTypeAndReferenceId(type, referenceId);

        if (group.isPresent() && existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return "북마크가 삭제되었습니다.";
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .group(group.get())
                    .type(type)
                    .ReferenceId(referenceId)
                    .build();

            Bookmark savedBookmark = bookmarkRepository.save(bookmark);

            return "북마크가 생성되었습니다.";
        }
    }

    public Page<Object> selectBookmarksByGroupId(Pageable pageable) {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupService.findGroupForUser(user.getId());
        Page<Bookmark> bookmarks = bookmarkRepository.findByGroupId(group.get().getId(), pageable);

        Page<Object> bookmarkDetails = bookmarks.map(bookmark -> {
            String type = bookmark.getType();
            Long referenceId = bookmark.getReferenceId();

            switch (type) {
                case "festival":
                    Optional<Festival> festival = festivalRepository.findById(referenceId);
                    festival.get().setType("festival");
                    festival.get().setBookmarked(true);

                    return festival;
                case "performance":
                    Optional<Performance> performance = performanceRepository.findById(referenceId);
                    performance.get().setType("performance");
                    performance.get().setBookmarked(true);

                    return performance;
                default:
                    return null;
            }
        });

        return bookmarkDetails;
    }
}
