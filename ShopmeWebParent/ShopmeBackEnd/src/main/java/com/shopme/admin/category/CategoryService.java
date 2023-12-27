package com.shopme.admin.category;

import com.shopme.admin.user.UserNotFoundException;
import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listByPage(CategoryPageInfo pageInfo, String sortDir, int pageNumber,
                                     String keyword) {
        Sort sort = Sort.by("name");

        if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNumber -1 , ROOT_CATEGORIES_PER_PAGE, sort );

        Page<Category> pageCategories = null;

        if(keyword != null && !keyword.isEmpty()) {
            pageCategories = categoryRepository.search(keyword, pageable);
        } else {
            pageCategories = categoryRepository.findRootCategories(pageable);
        }
        List<Category> rootCategories =  pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        if(keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult =  pageCategories.getContent();
            for (Category category : searchResult) {
                category.setHasChildren(category.getChildren().size() > 0);
            }
            return searchResult;
        } else {
            return listTheLevelCategories(rootCategories, sortDir);
        }
    }

    private List<Category> listTheLevelCategories(List<Category> rootCategories,String sortDir) {
        List<Category> theLevelCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            theLevelCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for (Category subCategory : children) {
                String name = "__" + subCategory.getName();
                theLevelCategories.add(Category.copyFull(subCategory, name));

                listSubTheLevelCategories(theLevelCategories, subCategory, 1, sortDir);
            }
        }

        return theLevelCategories;
    }

    private void listSubTheLevelCategories(List<Category> theLevelCategories,
                                           Category parent, int subLevel, String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);

        int newSubLevel = subLevel + 1;

        for (Category subCategory : children) {
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < newSubLevel; i++) {
                name.append("__");
            }
            name.append(subCategory.getName());
            theLevelCategories.add(Category.copyFull(subCategory, name.toString()));

            listSubTheLevelCategories(theLevelCategories, subCategory, newSubLevel, sortDir);
        }

    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInform = new ArrayList<>();
        Iterable<Category> categoriesInDb = categoryRepository.findRootCategories(Sort.by("name").ascending());

        for (Category category : categoriesInDb) {
            if (category.getParent() == null) {
                categoriesUsedInform.add(Category.copyIdAndName(category));

                Set<Category> children = sortSubCategories(category.getChildren());

                for (Category subCategory : children) {
                    String name = "__" + subCategory.getName();
                    categoriesUsedInform.add(Category.copyIdAndName(subCategory.getId(), name));
                    listSubCategoriesUsedInform(categoriesUsedInform, subCategory, 1);
                }
            }
        }
        return categoriesUsedInform;
    }

    private void listSubCategoriesUsedInform(List<Category> categoriesUsedInform,
                                             Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren());

        for (Category subCategory : children) {
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < newSubLevel; i++) {
                name.append("__");
            }
            name.append(subCategory.getName());

            categoriesUsedInform.add(Category.copyIdAndName(subCategory.getId(), name.toString()));

            listSubCategoriesUsedInform(categoriesUsedInform, subCategory, newSubLevel);
        }
    }

    public Category get(Integer id) throws UserNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException("Could not find any category with Id " + id);
        }
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = categoryRepository.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "DuplicateName";
            } else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);

                if (categoryByAlias != null) {
                    return "DuplicateAlias";
                }
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != id) {
                return "DuplicateName";
            }
            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if (categoryByAlias != null && categoryByAlias.getId() != id) {
                return "DuplicateAlias";
            }
        }

        return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if (sortDir.equals("asc")) {
                    return cat1.getName().compareTo(cat2.getName());
                } else {
                    return cat2.getName().compareTo(cat2.getName());
                }
            }
        });

        sortedChildren.addAll(children);
        return sortedChildren;
    }

    public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
        Category category = categoryRepository.findById(id).get();
        category.setEnabled(enabled);
        categoryRepository.save(category);
    }

    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);
        if (countById == null || countById==0) {
            throw new CategoryNotFoundException("Could not find any category with Id " + id);
        }
        categoryRepository.deleteById(id);
    }

    public List<Category> listAll() {
        return categoryRepository.findAll();
    }
}
