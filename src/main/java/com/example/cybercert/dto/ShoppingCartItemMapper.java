package com.example.cybercert.dto;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.cybercert.models.Certification;
import com.example.cybercert.models.ShoppingCartItem;

@Mapper(componentModel = "spring")

public interface ShoppingCartItemMapper {
        @Mapping(source = "user.id", target = "userId")
        @Mapping(source = "certification.id", target = "certificationId")
        ShoppingCartItemDTO toDTO(ShoppingCartItem shoppingCartItem);
    
        List<ShoppingCartItemDTO> toDTOs(Collection<ShoppingCartItem> shoppingCartItems);
    
        @Mapping(target = "user", ignore = true)
        @Mapping(target = "certification", ignore = true)
        ShoppingCartItem toDomain(ShoppingCartItemDTO shoppingCartItemDTO);

    
}
