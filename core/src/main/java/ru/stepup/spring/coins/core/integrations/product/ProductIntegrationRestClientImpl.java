package ru.stepup.spring.coins.core.integrations.product;

import com.github.sputnik1111.api.product.ProductDto;
import com.github.sputnik1111.api.product.ProductIntegration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import ru.stepup.spring.coins.core.dtos.PageDto;
import ru.stepup.spring.coins.core.exceptions.BadRequestException;
import ru.stepup.spring.coins.core.exceptions.IntegrationErrorDto;
import ru.stepup.spring.coins.core.exceptions.IntegrationException;

import java.util.List;

@Service
public class ProductIntegrationRestClientImpl implements ProductIntegration {

    private final RestClient restClient;

    private final ProductIntegrationProperties props;


    public ProductIntegrationRestClientImpl(
            ProductIntegrationProperties props
    ) {
        this.restClient = RestClient.builder().build();
        this.props = props;
    }

    @Override
    public List<ProductDto> findByUserId(Long userId) {
        if (userId==null)
            throw new BadRequestException("userId == null","USER_EMPTY");

        try{
            PageDto<ProductDto> pageDto = restClient.get()
                    .uri(props.getClient().getUrl())
                    .header("USERID",userId.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new BadRequestException(response.getStatusText(),response.getStatusCode().toString());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new BadRequestException(response.getStatusText(),response.getStatusCode().toString());
                    })
                    .body(new ParameterizedTypeReference<>() {});
            return pageDto.getContent();
        }catch (ResourceAccessException e){
            throw  new IntegrationException(
                    e.getMessage(),
                    new IntegrationErrorDto(
                            "PRODUCT_INTEGRATION_NOT_AVAILABLE",
                            "ProductIntegration not available"
                    )
            );
        }



    }
}
