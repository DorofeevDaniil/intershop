package ru.custom.intershop.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.service.ItemService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.init.enabled", havingValue = "true", matchIfMissing = true)
public class ImageStartupInitializer {

    private final ItemService itemService;

    @Value("${app.upload-dir}")
    private String relativePath;

    private Random randomDouble = new Random();

    private static final Map<String, String> INIT_FILES = Map.of(
        "chicken", "Шапка",
        "dyson", "Dyson G5 Detect Fluffy Беспроводной вертикальный пылесос, фиолетовый, серый металлик, серебристый",
        "keychain", "Брелок для ключей, подарок сыну" ,
        "oven", "Духовой шкаф электрический встраиваемый Bosch HBA534EB0, 72 литра, 7 режимов, Гриль, Конвекция, Каталитическая очистка, Часы с таймером, Чёрный",
        "pig", "Шапка2",
        "shorts", "Шорты для дома",
        "statem", "Шапка Шапка",
        "termostat", "Aqara Умный терморегулятор батареи (термостат) E1 SRTS-A01",
        "washing-machine", "Cтиральная машина Samsung WW11CG604CLBLP, 11 кг, с увеличенным объемом барабана, электронным управлением, инверторным мотором, обработкой паром, системой управления на базе ИИ, черная",
        "ya-remote-controller", "Умный пульт Яндекс с Алисой, черный"
    );
    public ImageStartupInitializer(ItemService itemService) {
        this.itemService = itemService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void populateItems(ContextRefreshedEvent event) {
        itemService.getTotalCount()
            .filter(count -> count == 0)
            .flatMapMany(empty -> Flux.fromIterable(INIT_FILES.entrySet()))
            .flatMap(entry -> createAndSaveItem(entry.getKey(), entry.getValue()))
            .doOnError(e -> log.error("Error during initialization", e))
            .subscribe();
    }

    private Mono<Item> createAndSaveItem(String fileKey, String title) {
        return Mono.fromCallable(() -> saveImage(fileKey))
            .subscribeOn(Schedulers.boundedElastic())
            .zipWith(Mono.fromCallable(() -> getFileText(fileKey))
                .subscribeOn(Schedulers.boundedElastic()))
            .flatMap(tuple -> {
                String imagePath = tuple.getT1();
                String description = tuple.getT2();

                Item item = new Item();
                item.setTitle(title);
                item.setDescription(description);
                item.setImgPath(imagePath);
                item.setPrice(BigDecimal.valueOf(200 + (70000 - 200) * randomDouble.nextDouble())
                    .setScale(2, RoundingMode.DOWN));
                item.setCount(0);

                return itemService.save(item);
            });
    }

    public String getFileText(String textFileName) {
        StringBuilder builder = new StringBuilder();
        String resultFileName = textFileName + "-demo.txt";

        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new ClassPathResource("init_texts/" + resultFileName).getInputStream(),
                StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error(String.format("Failed to read text file %s. Got error: %s", resultFileName, e.getMessage()));
        }

        return builder.toString();
    }

    public String saveImage(String imageFileName) throws IOException {
        String initImageFullName = imageFileName + "-demo.jpg";
        Path uploadDir = Paths.get(relativePath).toAbsolutePath();

        String resultImagePath = "";

        try {
            if (Files.notExists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path targetImagePath = uploadDir.resolve(initImageFullName);
            resultImagePath = initImageFullName;

            if (Files.notExists(targetImagePath)) {
                try (InputStream in = new ClassPathResource("init_images/" + initImageFullName).getInputStream()) {
                    Files.copy(in, targetImagePath);
                }
            }
        } catch (Exception e) {
            log.error(String.format("Failed to initialize image data. Got error: %s", e.getMessage()));
        }

        return resultImagePath;
    }
}