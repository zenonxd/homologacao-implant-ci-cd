package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.CategoryDTO;
import com.devsuperior.dscommerce.dto.ProductDto;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    //como o service devolve um DTO para o controller,
    //a função retornará um DTO
    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {

        //busca no banco de dados, caso não ache, lançará uma exceção
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado."));


        //lembrar que no DTO foi criado um construtor
        //específico para receber um Product
        return new ProductDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductMinDTO> findAll(String name, Pageable pageable) {
        Page<Product> products = productRepository.searchByName(name, pageable);

        //pode fazer .map direto pois Page já é uma stream
        return products.map(x -> new ProductMinDTO(x));
    }

    @Transactional
    public ProductDto insert (ProductDto dto) {
        //criando um Product para receber os dados do DTO
        Product entity = new Product();


        //salvando os dados do DTO no Product
        copyDtoToEntity(dto, entity);


        //salvando entidade no banco de dados
        entity = productRepository.save(entity);

        //reconvertendo para DTO
        return new ProductDto(entity);
    }

    @Transactional //passando um Id como parâmetro pois é o que vai ser passado no postman
    public ProductDto update(Long id, ProductDto dto) {

        //instanciando um produto pela ID do banco de dados
        //o produto só será instanciado com a referência que passamos (id)
        Product entity = productRepository.getReferenceById(id);

        //settando novos valores num produto já existente
        copyDtoToEntity(dto, entity);

        //salvando no banco
        entity = productRepository.save(entity);

        //reconvertendo para DTO
        return new ProductDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    private void copyDtoToEntity(ProductDto dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());

        entity.getCategories().clear();
        for (CategoryDTO catDto : dto.getCategories()) {
            Category cat = new Category();
            cat.setId(catDto.getId());
            entity.getCategories().add(cat);
        }
    }

}
