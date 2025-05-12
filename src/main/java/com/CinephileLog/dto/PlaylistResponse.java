package com.CinephileLog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaylistResponse {
    private Long playlistId;
    private String name;
    private String description;
}
