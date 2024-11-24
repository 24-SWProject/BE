package com.swproject.hereforus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkDto {
    private String group;
    private String type; // "festival", "performance", "food"
    private Long referenceId;
}
