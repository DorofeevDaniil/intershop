package ru.custom.intershop.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
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

@Component
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
    private static final Logger logger = LoggerFactory.getLogger(ImageStartupInitializer.class);

    public ImageStartupInitializer(ItemService itemService) {
        this.itemService = itemService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void populatePosts(ContextRefreshedEvent event) throws IOException {
        if (itemService.getTotalCount() == 0 ) {
            for (Map.Entry<String, String> fileEntry : INIT_FILES.entrySet()) {

                String targetImagePath = saveImage(fileEntry.getKey());

                String text = getFileText(fileEntry.getKey());

                Item item = new Item();

                item.setTitle(fileEntry.getValue());
                item.setDescription(text);
                item.setImgPath(targetImagePath);
                item.setPrice(BigDecimal.valueOf(200 + (70000 - 200) * randomDouble.nextDouble()).setScale(2, RoundingMode.DOWN));
                item.setCount(0);

                itemService.save(item);
            }
        }
    }

    private String getFileText(String textFileName) {
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
            logger.error(String.format("Failed to read text file %s. Got error: %s", resultFileName, e.getMessage()));
        }

        return builder.toString();
    }

    private String saveImage(String imageFileName) throws IOException {
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
            logger.error(String.format("Failed to initialize image data. Got error: %s", e.getMessage()));
        }

        return resultImagePath;
    }
}