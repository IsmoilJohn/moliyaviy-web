package com.moliyaviy.web.service;

import com.moliyaviy.web.entity.Category;
import com.moliyaviy.web.entity.User;
import com.moliyaviy.web.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategorySeedService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void seedDefaultCategories(User user) {
        for (DefaultCategories.Seed seed : DefaultCategories.SEEDS) {
            boolean exists = categoryRepository
                    .existsByUserIdAndNameIgnoreCaseAndType(user.getId(), seed.name(), seed.type());
            if (exists) {
                continue;
            }

            Category category = new Category();
            category.setUser(user);
            category.setName(seed.name());
            category.setType(seed.type());
            category.setColor(seed.color());
            categoryRepository.save(category);
        }
    }

}
