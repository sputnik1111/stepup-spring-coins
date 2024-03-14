package ru.stepup.spring.coins.core.services;

import com.github.sputnik1111.api.product.ProductDto;
import org.springframework.stereotype.Service;
import ru.stepup.spring.coins.core.api.ExecuteCoinsRequest;
import ru.stepup.spring.coins.core.api.ExecuteCoinsResponse;
import ru.stepup.spring.coins.core.exceptions.BadRequestException;
import ru.stepup.spring.coins.core.configurations.properties.CoreProperties;


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

        ProductDto requiredProduct = productService.findByUserIdAndProductId(userId,request.productId())
                        .orElseThrow(()->new BadRequestException("Product doesn't found with id = "+request.productId(),"PRODUCT_NOT_FOUND"));

        if (requiredProduct.balance()<request.summ())
            throw new BadRequestException("Product balance with id = "+request.productId()+" is not enough","BALANCE_NOT_ENOUGH");

        return executorService.execute(request);
    }
}
