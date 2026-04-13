package com.otopart.domain.ai.service;

import com.otopart.domain.product.entity.Product;
import com.otopart.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAssistantService {

    private final WebClient.Builder webClientBuilder;
    private final ProductRepository productRepository;

    @Value("${ai.groq.api-url}")
    private String groqApiUrl;

    @Value("${ai.groq.api-key}")
    private String groqApiKey;

    @Value("${ai.groq.model}")
    private String groqModel;

    /**
     * Sase numarasini analiz et, uyumlu parcalari oner
     * Ustalar icin canli destek + AI entegrasyonu
     */
    public AiResponse analyzeChassisAndSuggest(String chassisNumber, String userQuery) {
        List<Product> products = productRepository
                .findAll(PageRequest.of(0, 30))
                .getContent();

        String productList = products.stream()
                .map(p -> "ID:%d | %s | %.2f TL | Stock:%d"
                        .formatted(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                .reduce("", (a, b) -> a + "\n" + b);

        String systemPrompt = """
                You are an expert automotive assistant for OtoPart platform.
                Respond in Turkish. Analyze the chassis number and suggest compatible parts.
                Choose from the available product list and specify their IDs.
                Be brief and professional.
                """;

        String userPrompt = """
                Chassis: %s
                Customer question: %s
                
                Available products:
                %s
                
                Suggest products with their IDs. Format:
                {"productIds": [1, 2], "message": "explanation"}
                """.formatted(chassisNumber, userQuery, productList);

        try {
            String aiText = callGroq(systemPrompt, userPrompt);
            return AiResponse.builder()
                    .message(aiText)
                    .suggestedProducts(products.subList(0, Math.min(5, products.size())))
                    .chassisNumber(chassisNumber)
                    .build();
        } catch (Exception e) {
            log.error("AI chassis analysis error: {}", e.getMessage());
            return AiResponse.builder()
                    .message("AI analizi yapılamıyor. Lütfen canlı destek ile iletişime geçin.")
                    .suggestedProducts(List.of())
                    .chassisNumber(chassisNumber)
                    .build();
        }
    }

    /** Genel otomotiv sorusu - canli destek */
    public String askSupport(String question, String vehicleInfo) {
        String systemPrompt = """
                You are an expert automotive assistant for OtoPart platform.
                Answer in Turkish. Help with parts, maintenance and repair questions.
                For safety-critical information, recommend an authorized service.
                Keep answers concise and practical.
                """;

        String userPrompt = vehicleInfo != null
                ? "Vehicle: %s\nQuestion: %s".formatted(vehicleInfo, question)
                : question;

        try {
            return callGroq(systemPrompt, userPrompt);
        } catch (Exception e) {
            log.error("AI support error: {}", e.getMessage());
            return "Şu an yanıt veremiyorum. Lütfen daha sonra tekrar deneyin.";
        }
    }

    @SuppressWarnings("unchecked")
    private String callGroq(String systemPrompt, String userPrompt) {
        WebClient client = webClientBuilder
                .baseUrl(groqApiUrl)
                .defaultHeader("Authorization", "Bearer " + groqApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        Map<String, Object> body = Map.of(
                "model", groqModel,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "max_tokens", 1024,
                "temperature", 0.3
        );

        Map<String, Object> response = client.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && response.containsKey("choices")) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        throw new RuntimeException("No response from AI");
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AiResponse {
        private String message;
        private List<Product> suggestedProducts;
        private String chassisNumber;
    }
}