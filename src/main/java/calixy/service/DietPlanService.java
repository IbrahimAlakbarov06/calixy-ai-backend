package calixy.service;

import calixy.domain.entity.DietPlan;
import calixy.domain.entity.User;
import calixy.domain.entity.UserDietPlan;
import calixy.domain.repo.DietPlanRepository;
import calixy.domain.repo.UserDietPlanRepository;
import calixy.exception.AlreadyExistsException;
import calixy.exception.NotFoundException;
import calixy.mapper.DietPlanMapper;
import calixy.model.dto.request.CreateDietPlanRequest;
import calixy.model.dto.response.DietPlanResponse;
import calixy.model.dto.response.UserDietPlanResponse;
import calixy.model.enums.TargetGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietPlanService {

    private final DietPlanRepository dietPlanRepository;
    private final UserDietPlanRepository userDietPlanRepository;
    private final DietPlanMapper dietPlanMapper;

    @Cacheable(value = "dietPlans", key = "'all_' + #targetGoal")
    @Transactional(readOnly = true)
    public List<DietPlanResponse> getPlans(TargetGoal targetGoal) {
        List<DietPlan> plans = targetGoal != null
                ? dietPlanRepository.findByTargetGoalAndIsActiveTrue(targetGoal)
                : dietPlanRepository.findByIsActiveTrue();
        return dietPlanMapper.toResponseList(plans);
    }

    @Cacheable(value = "dietPlans", key = "#id")
    @Transactional(readOnly = true)
    public DietPlanResponse getPlanById(Long id) {
        return dietPlanMapper.toResponse(findActiveById(id));
    }

    @CacheEvict(value = "dietPlans", allEntries = true)
    @Transactional
    public DietPlanResponse createPlan(CreateDietPlanRequest request) {
        DietPlan plan = dietPlanMapper.toEntity(request);
        return dietPlanMapper.toResponse(dietPlanRepository.save(plan));
    }

    @CacheEvict(value = "dietPlans", allEntries = true)
    @Transactional
    public DietPlanResponse updatePlan(Long id, CreateDietPlanRequest request) {
        DietPlan plan = findActiveById(id);
        dietPlanMapper.applyUpdate(plan, request);
        return dietPlanMapper.toResponse(dietPlanRepository.save(plan));
    }

    @CacheEvict(value = "dietPlans", allEntries = true)
    @Transactional
    public void deletePlan(Long id) {
        DietPlan plan = findActiveById(id);
        plan.setIsActive(false);
        dietPlanRepository.save(plan);
    }

    @Transactional
    public UserDietPlanResponse addToMyPlans(User user, Long dietPlanId) {
        DietPlan plan = findActiveById(dietPlanId);

        if (userDietPlanRepository.existsByUserIdAndDietPlanId(user.getId(), dietPlanId)) {
            throw new AlreadyExistsException("This plan is already in your list");
        }

        UserDietPlan udp = UserDietPlan.builder()
                .user(user)
                .dietPlan(plan)
                .isActive(true)
                .build();

        return dietPlanMapper.toUserDietPlanResponse(userDietPlanRepository.save(udp));
    }

    @Transactional(readOnly = true)
    public List<UserDietPlanResponse> getMyPlans(User user) {
        return dietPlanMapper.toUserDietPlanResponseList(
                userDietPlanRepository.findByUserIdAndIsActiveTrue(user.getId())
        );
    }

    @Transactional
    public void removeFromMyPlans(User user, Long userDietPlanId) {
        UserDietPlan udp = userDietPlanRepository.findById(userDietPlanId)
                .orElseThrow(() -> new NotFoundException("Plan not found: " + userDietPlanId));

        if (!udp.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Plan not found: " + userDietPlanId);
        }

        udp.setIsActive(false);
        userDietPlanRepository.save(udp);
    }

    private DietPlan findActiveById(Long id) {
        DietPlan plan = dietPlanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Diet plan not found: " + id));
        if (!plan.getIsActive()) {
            throw new NotFoundException("Diet plan not found: " + id);
        }
        return plan;
    }
}