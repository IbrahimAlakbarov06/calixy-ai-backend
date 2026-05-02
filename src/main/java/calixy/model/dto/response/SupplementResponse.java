package calixy.model.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplementResponse {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private Boolean isCustom;
}