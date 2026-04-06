package calixy.service;

import calixy.domain.entity.DietPlan;
import calixy.domain.entity.FavoriteFood;
import calixy.domain.entity.Food;
import calixy.domain.entity.User;
import calixy.domain.repo.DietPlanRepository;
import calixy.domain.repo.FavoriteRepository;
import calixy.domain.repo.FoodRepository;
import calixy.exception.AlreadyExistsException;
import calixy.exception.BusinessException;
import calixy.exception.NotFoundException;
import calixy.mapper.FavoriteMapper;
import calixy.model.dto.response.FavoriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FoodRepository foodRepository;
    private final DietPlanRepository dietPlanRepository;
    private final FavoriteMapper favoriteMapper;

    @Cacheable(value = "favorites", key = "#user.id")
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getMyFavorites(User user) {
        return favoriteMapper.toResponseList(
                favoriteRepository.findByUserId(user.getId())
        );
    }

    @CacheEvict(value = "favorites", key = "#user.id")
    @Transactional
    public FavoriteResponse addFoodToFavorites(User user, Long foodId) {
        if (favoriteRepository.existsByUserIdAndFoodId(user.getId(), foodId)) {
            throw new AlreadyExistsException("Food is already in favorites");
        }

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new NotFoundException("Food not found: " + foodId));

        FavoriteFood fav = FavoriteFood.builder()
                .user(user)
                .food(food)
                .build();

        return favoriteMapper.toResponse(favoriteRepository.save(fav));
    }

    @CacheEvict(value = "favorites", key = "#user.id")
    @Transactional
    public FavoriteResponse addDietPlanToFavorites(User user, Long dietPlanId) {
        if (favoriteRepository.existsByUserIdAndDietPlanId(user.getId(), dietPlanId)) {
            throw new AlreadyExistsException("Diet plan is already in favorites");
        }

        DietPlan plan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new NotFoundException("Diet plan not found: " + dietPlanId));

        FavoriteFood fav = FavoriteFood.builder()
                .user(user)
                .dietPlan(plan)
                .build();

        return favoriteMapper.toResponse(favoriteRepository.save(fav));
    }

    @CacheEvict(value = "favorites", key = "#user.id")
    @Transactional
    public void removeFavorite(User user, Long favoriteId) {
        FavoriteFood fav = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NotFoundException("Favorite not found: " + favoriteId));

        if (!fav.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You can only remove your own favorites");
        }

        favoriteRepository.delete(fav);
    }
}