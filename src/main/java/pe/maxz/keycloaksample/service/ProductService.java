package pe.maxz.keycloaksample.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.maxz.keycloaksample.entity.Product;

@Service
@Slf4j
public class ProductService {

    private List<Product> products;
    private int sequencialId=0;

    public ProductService(){
        products = new ArrayList<Product>();
    }

    public List<Product> getAll(){
        return products;
    }
    
    public Product create (Product product){
        sequencialId++;
        product.setId(sequencialId);
        products.add(product);
        return product;
    }

    public Product get(int id){
        return products.stream().filter(i->i.getId()==id).findFirst().orElse(null);
    }

    public boolean delete(int id){
        Product result = products.stream().filter(i->i.getId()==id).findFirst().orElse(null);
        if (result==null) return false;
        products.remove(result);
        return true;
    }
    public Product update (Product product){
        log.debug(product.toString());
        var result = products.stream().filter(i->i.getId()==product.getId()).findFirst().orElse(null);
        if (result==null) return null;
        result.setName(product.getName());
        result.setPrice(product.getPrice());
        return product;
    }
}
