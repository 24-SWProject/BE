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
import org.springframework.stereotype.Service;

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

    public Object saveOrDeleteBookmark(String type, Long referenceId) {
        User user = userDetailService.getAuthenticatedUserId();
//        Optional<Group> group = groupRepository.findByUserId(user.getId());
        Optional<Group> group = groupRepository.findByUserId(Long.valueOf("1"));
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

            return savedBookmark;
        }
    }
}
