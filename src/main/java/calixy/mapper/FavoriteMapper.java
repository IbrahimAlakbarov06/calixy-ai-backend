package calixy.mapper;

import calixy.domain.entity.FavoriteFood;
import calixy.model.dto.response.FavoriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FavoriteMapper {

    private final FoodMapper foodMapper;
    private final DietPlanMapper dietPlanMapper;

    public FavoriteResponse toResponse(FavoriteFood fav) {
        if (fav == null) return null;
        return FavoriteResponse.builder()
                .id(fav.getId())
                .food(fav.getFood() != null ? foodMapper.toResponse(fav.getFood()) : null)
                .dietPlan(fav.getDietPlan() != null ? dietPlanMapper.toResponse(fav.getDietPlan()) : null)
                .savedAt(fav.getSavedAt())
                .build();
    }

    public List<FavoriteResponse> toResponseList(List<FavoriteFood> favs) {
        return favs.stream().map(this::toResponse).collect(Collectors.toList());
    }
}