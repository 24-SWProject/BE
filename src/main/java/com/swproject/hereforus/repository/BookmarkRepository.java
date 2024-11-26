package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByTypeAndReferenceId(String type, Long referenceId);
    Page<Bookmark> findByGroupId(String group_id, Pageable pageable);
    Optional<Bookmark> findByTypeAndReferenceIdAndGroupId(String type, Long referenceId, String groupId);
}