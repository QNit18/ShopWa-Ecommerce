package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository repo;

    @Test
    public void testCreateBrand1() {
        Category laptops = new Category(6);
        Brand acer = new Brand("Acer");
        acer.getCategories().add(laptops);

        Brand saveBrand = repo.save(acer);

        assertThat(saveBrand).isNotNull();
        assertThat(saveBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrand2() {
        Category cellphones = new Category(4);
        Category tablets = new Category(7);

        Brand apple = new Brand("Apple");
        apple.getCategories().add(cellphones);
        apple.getCategories().add(tablets);

        Brand saveBrand = repo.save(apple);

        assertThat(saveBrand).isNotNull();
        assertThat(saveBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrand3() {
        Brand samsung = new Brand("Samsung");
        samsung.getCategories().add(new Category(29)); // Category Memory
        samsung.getCategories().add(new Category(24)); // Category internal hard drive

        Brand saveBrand = repo.save(samsung);

        assertThat(saveBrand).isNotNull();
        assertThat(saveBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindAll() {
        Iterable<Brand> brands = repo.findAll();
        brands.forEach(System.out::println);

        assertThat(brands).isNotNull();
    }

    @Test
    public void testGetById() {
        Brand brand = repo.findById(1).get();

        assertThat(brand.getName()).isEqualTo("Acer");
    }

    @Test
    public void updateName() {
        String newName = "Samsung Electronics";
        Brand samsung = repo.findById(3).get();
        samsung.setName(newName);

        Brand savedBread = repo.save(samsung);
        assertThat(savedBread.getName()).isEqualTo(newName);
    }

    @Test
    public void testDelete() {
        Integer id = 2;
        repo.deleteById(id);

        Optional<Brand> result = repo.findById(id);
        assertThat(result.isEmpty());
    }
}