package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByTypeAndReferenceId(String type, String referenceId);
    Page<Bookmark> findByGroupId(String group_id, Pageable pageable);
    Optional<Bookmark> findByTypeAndReferenceIdAndGroupId(String type, String referenceId, String groupId);

    @Modifying
    @Query("DELETE FROM Bookmark f WHERE f.group.id = :groupId")
    void deleteByGroupId(@Param("groupId") String groupId);
}