package com.swproject.hereforus.service;

import com.swproject.hereforus.entity.Bookmark;
import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.BookmarkRepository;
import com.swproject.hereforus.repository.GroupRepository;
import com.swproject.hereforus.repository.event.FestivalRepository;
import com.swproject.hereforus.repository.event.FoodRepository;
import com.swproject.hereforus.repository.event.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final FestivalRepository festivalRepository;
    private final PerformanceRepository performanceRepository;
    private final FoodRepository foodRepository;
    private final BookmarkRepository bookmarkRepository;
    private final GroupRepository groupRepository;
    private final UserDetailService userDetailService;
    private final GroupService groupService;

    public Object saveOrDeleteBookmark(String type, Long referenceId) {
        User user = userDetailService.getAuthenticatedUserId();
        Optional<Group> group = groupService.findGroupForUser(user.getId());
//        Optional<Group> group = groupService.findGroupForUser(Long.valueOf("1"));
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
//        Optional<Group> group = groupService.findGroupForUser(Long.valueOf("1"));
        Page<Bookmark> bookmarks = bookmarkRepository.findByGroupId(group.get().getId(), pageable);

        Page<Object> bookmarkDetails = bookmarks.map(bookmark -> {
            String type = bookmark.getType();
            Long referenceId = bookmark.getReferenceId();

            switch (type) {
                case "festival":
                    return festivalRepository.findById(referenceId).orElse(null);
                case "performance":
                    return performanceRepository.findById(referenceId).orElse(null);
                case "food":
                    return foodRepository.findById(referenceId).orElse(null);
                default:
                    return null;
            }
        });

        return bookmarkDetails;
    }
}
