package com.kushal.razorpay.merchant.service.impl;
import com.kushal.razorpay.common.exception.ResourceNotFoundException;
import com.kushal.razorpay.common.util.RandomizerUtil;
import com.kushal.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.kushal.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.kushal.razorpay.merchant.dto.response.ApiKeyResponse;
import com.kushal.razorpay.merchant.entity.ApiKey;
import com.kushal.razorpay.merchant.entity.Merchant;
import com.kushal.razorpay.merchant.repository.ApiKeyRepository;
import com.kushal.razorpay.merchant.repository.MerchantRepository;
import com.kushal.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant",merchantId));

        String keyId = "rzp_"+request.environment().name().toLowerCase()+"_"+RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(rawSecret) // TODO: Encode with BCryptPasswordEncoder
                .environment(request.environment())
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(),keyId,rawSecret,request.environment());
    }

    @Override //List of API KEYS used by Merchant
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {

        return apiKeyRepository.findByMerchant_Id(merchantId).stream()
                .map(apiKey -> new ApiKeyResponse(apiKey.getId(),
                        apiKey.getKeyId(),
                        apiKey.getEnvironment(),
                        apiKey.isEnabled(),
                        apiKey.getLastUsedAt(),
                        null))
                .toList();
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(key -> key.getMerchant().getId().equals(merchantId))
                .orElseThrow(()-> new ResourceNotFoundException("ApiKey",keyId));

        apiKey.setEnabled(false);
        apiKeyRepository.save(apiKey);  // explicitly adding, not require though due to @Transactional
    }
    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(key -> key.getMerchant().getId().equals(merchantId))
                .orElseThrow(()-> new ResourceNotFoundException("ApiKey",keyId));

        //we cannot rotate a disbled key
        if(!apiKey.isEnabled()) throw new RuntimeException("Cannot rotate a disabled key");

        //Now need to generate the new secret
        String newRawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash()); // setting up prev key to current secret hash

        apiKey.setKeySecretHash(newRawSecret); // TODO: Encode with BCryptPasswordEncoder
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));

        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponse(apiKey.getId(),apiKey.getKeyId(),newRawSecret,apiKey.getEnvironment());
    }
}
