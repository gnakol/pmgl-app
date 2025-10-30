package fr.mecanique.api.pmgl.pmgl_api.account.config;

import fr.mecanique.api.pmgl.pmgl_api.account.enums.AccountType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false) // on l’active explicitement sur le champ
public class AccountTypePgEnumConverter implements AttributeConverter<AccountType, String> {

    @Override
    public String convertToDatabaseColumn(AccountType attribute) {
        if (attribute == null) return null;
        return attribute.name().toLowerCase(); // "CUSTOMER" -> "customer"
    }

    @Override
    public AccountType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return AccountType.valueOf(dbData.toUpperCase()); // "customer" -> "CUSTOMER"
    }
}

