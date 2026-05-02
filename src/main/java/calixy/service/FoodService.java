package calixy.service;

import calixy.domain.entity.Food;
import calixy.domain.repo.FoodRepository;
import calixy.exception.NotFoundException;
import calixy.mapper.FoodMapper;
import calixy.model.dto.request.CreateFoodRequest;
import calixy.model.dto.request.UpdateFoodRequest;
import calixy.model.dto.response.FoodResponse;
import calixy.model.enums.FoodCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    private static final String LANG =
            "T(org.springframework.context.i18n.LocaleContextHolder).getLocale().getLanguage()";

    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;

//    @Cacheable(
//            value = "foods",
//            key = "'all_' + #category + '_' + #query + '_' + " + LANG
//    )
    @Transactional(readOnly = true)
    public List<FoodResponse> getFoods(FoodCategory category, String query) {
        List<Food> foods;

        if (category != null && query != null && !query.isBlank()) {
            foods = foodRepository.searchByCategoryAndName(category, query.trim());
        } else if (category != null) {
            foods = foodRepository.findByCategoryAndIsActiveTrue(category);
        } else if (query != null && !query.isBlank()) {
            foods = foodRepository.searchByName(query.trim());
        } else {
            foods = foodRepository.findByIsActiveTrue();
        }

        return foodMapper.toListResponse(foods);
    }

//    @Cacheable(
//            value = "foods",
//            key = "#id + '_' + " + LANG
//    )
    @Transactional(readOnly = true)
    public FoodResponse getFoodById(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Food not found with id: " + id));

        if (!food.getIsActive()) {
            throw new NotFoundException("Food not found with id: " + id);
        }

        return foodMapper.toResponse(food);
    }

    @CacheEvict(value = "foods", allEntries = true)
    @Transactional
    public FoodResponse createFood(CreateFoodRequest request) {
        Food food = foodMapper.toEntity(request);
        return foodMapper.toResponse(foodRepository.save(food));
    }

    @CacheEvict(value = "foods", allEntries = true)
    @Transactional
    public FoodResponse updateFood(Long id, UpdateFoodRequest request) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Food not found with id: " + id));

        foodMapper.applyUpdate(food, request);
        return foodMapper.toResponse(foodRepository.save(food));
    }

    @CacheEvict(value = "foods", allEntries = true)
    @Transactional
    public void deleteFood(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Food not found with id: " + id));

        food.setIsActive(false);
        foodRepository.save(food);
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> getAllFoodsForAdmin() {
        return foodMapper.toListResponse(foodRepository.findAll());
    }
}