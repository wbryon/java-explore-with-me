package ru.practicum.ewm.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.exception.NotFoundException;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    default Category findCategoryById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Категория с id: " + id + " не найдена"));
    }

    Page<Category> findAll(Pageable pageable);
}
