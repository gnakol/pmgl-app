package fr.mecanique.api.pmgl.pmgl_api.uuid.service;

import fr.mecanique.api.pmgl.pmgl_api.uuid.bean.UuidPmgl;
import fr.mecanique.api.pmgl.pmgl_api.uuid.mapper.UuidMapper;
import fr.mecanique.api.pmgl.pmgl_api.uuid.repositorie.UuidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UuidService {

    private final UuidRepository uuidRepository;

    private final UuidMapper uuidMapper;

    public String generateUuid()
    {
        String keyGenerate = UUID.randomUUID().toString();

        if (this.uuidRepository.existsByUuidGenerate(keyGenerate))
        {
            return generateUuid();
        }
        else
        {
            UuidPmgl uuid = new UuidPmgl();
            uuid.setUuidGenerate(keyGenerate);
            this.uuidRepository.save(uuid);

            return keyGenerate;
        }
    }
}

