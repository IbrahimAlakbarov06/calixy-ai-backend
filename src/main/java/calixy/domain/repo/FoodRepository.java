package calixy.domain.repo;

import calixy.domain.entity.Food;
import calixy.model.enums.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByCategoryAndIsActiveTrue(FoodCategory category);

    List<Food> findByIsActiveTrue();

    @Query("SELECT f FROM Food f WHERE f.isActive = true AND " +
            "(LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.nameAz) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Food> searchByName(@Param("query") String query);

    @Query("SELECT f FROM Food f WHERE f.isActive = true AND f.category = :category AND " +
            "(LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.nameAz) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Food> searchByCategoryAndName(@Param("category") FoodCategory category,
                                       @Param("query") String query);
}