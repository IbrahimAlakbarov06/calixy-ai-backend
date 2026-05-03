package calixy.model.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSupplementRequest {

    private String name;
    private String nameAz;
    private String nameRu;
    private String nameTr;

    private String description;
    private String descriptionAz;
    private String descriptionRu;
    private String descriptionTr;

    private String iconUrl;
}