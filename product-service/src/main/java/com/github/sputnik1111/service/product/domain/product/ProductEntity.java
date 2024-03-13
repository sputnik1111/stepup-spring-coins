package com.github.sputnik1111.service.product.domain.product;

import com.github.sputnik1111.service.product.domain.user.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "user_product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_product_id_seq")
    @SequenceGenerator(name = "user_product_id_seq",
            sequenceName = "user_product_id_seq",
            initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne
    private UserEntity user;

    private String account;

    private Long balance;

    private TypeProduct typeProduct;

    public static ProductEntity newProduct(
            @NonNull UserEntity user,
            @NonNull String account,
            long balance,
            @NonNull TypeProduct typeProduct
    ){
        return new ProductEntity(
                null,
                user,
                account,
                balance,
                typeProduct
        );
    }


}
