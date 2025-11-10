package fr.mecanique.api.pmgl.pmgl_api.admin.mappers;

import fr.mecanique.api.pmgl.pmgl_api.account.mappers.AccountMapper;
import fr.mecanique.api.pmgl.pmgl_api.admin.bean.Admin;
import fr.mecanique.api.pmgl.pmgl_api.admin.dto.AdminDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {AccountMapper.class}, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AdminMapper {

    @Mapping(target = "accountId", source = "account.id")
    AdminDTO fromAdmin(Admin admin);

    @Mapping(target = "account.id", source = "accountId")
    Admin fromAdminDTO(AdminDTO adminDTO);
}
