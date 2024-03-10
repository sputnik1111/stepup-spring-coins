package ru.stepup.spring.coins.core.services;

import com.github.sputnik1111.api.product.ProductDto;
import org.springframework.stereotype.Service;
import ru.stepup.spring.coins.core.api.ExecuteCoinsRequest;
import ru.stepup.spring.coins.core.api.ExecuteCoinsResponse;
import ru.stepup.spring.coins.core.exceptions.BadRequestException;
import ru.stepup.spring.coins.core.configurations.properties.CoreProperties;

import java.util.List;

@Service
public class CoinsService {
    private final CoreProperties coreProperties;
    private final ExecutorService executorService;
    private final ProductService productService;

    public CoinsService(CoreProperties coreProperties, ExecutorService executorService, ProductService productService) {
        this.coreProperties = coreProperties;
        this.executorService = executorService;
        this.productService = productService;
    }

    public ExecuteCoinsResponse execute(ExecuteCoinsRequest request,Long userId) {
        if (coreProperties.getNumbersBlockingEnabled()) {
            if (coreProperties.getBlockedNumbers().contains(request.number())) {
                throw new BadRequestException("Указан заблокированный номер кошелька", "BLOCKED_ACCOUNT_NUMBER");
            }
        }
        List<ProductDto> products = productService.findByUserId(userId);

        ProductDto requiredProduct = products.stream()
                        .filter(p->p.id()!=null)
                        .filter(p->p.id().toString().equals(request.productId()))
                        .findFirst()
                        .orElseThrow(()->new BadRequestException("Product doesn't found with id = "+request.productId(),"PRODUCT_NOT_FOUND"));

        if (requiredProduct.balance()<1)
            throw new BadRequestException("Product balance with id = "+request.productId()+" is empty","BALANCE_EMPTY");

        return executorService.execute(request);
    }
}
