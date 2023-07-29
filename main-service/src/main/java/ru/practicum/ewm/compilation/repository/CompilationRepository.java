package ru.practicum.ewm.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    default Compilation findCompilationById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Выборка с id: " + id + " не найдена"));
    }

    List<Compilation> getAllByPinned(Boolean pinned, Pageable pageable);
}
