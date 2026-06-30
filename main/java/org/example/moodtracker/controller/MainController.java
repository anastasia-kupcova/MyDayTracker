package org.example.moodtracker.controller;

import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Circle;
import org.example.moodtracker.dao.DataDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.example.moodtracker.dao.MonthEventDAO;
import org.example.moodtracker.database.DatabaseConnection;
import org.example.moodtracker.model.MonthEvent;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class MainController implements Initializable {

    @FXML private ComboBox<Integer> yearSelector;
    @FXML private Label currentYearLabel;

    @FXML private GridPane socialGrid;
    @FXML private VBox socialLegend;

    @FXML private GridPane ratingGrid;
    @FXML private VBox ratingLegend;

    @FXML private GridPane energyMorningGrid;
    @FXML private GridPane energyEveningGrid;
    @FXML private VBox energyLegend;

    @FXML private GridPane anxietyGrid;
    @FXML private VBox anxietyLegend;

    @FXML private GridPane weatherGrid;
    @FXML private VBox weatherLegend;

    @FXML private GridPane sleepGrid;
    @FXML private VBox sleepLegend;

    @FXML private GridPane dreamGrid;
    @FXML private VBox dreamLegend;

    // Статистика - элементы управления

    @FXML private ComboBox<Integer> statsYearCombo;
    @FXML private ComboBox<String> statsMonthCombo;
    @FXML private Button refreshStatsButton;

    // Статистика - радио-кнопки для периода
    @FXML private RadioButton weekRadio;
    @FXML private RadioButton monthRadio;
    @FXML private RadioButton yearRadio;

    // Статистика - графики
    @FXML private LineChart<String, Number> ratingLineChart;
    @FXML private LineChart<String, Number> energyLineChart;
    @FXML private PieChart anxietyPieChart;
    @FXML private PieChart weatherPieChart;
    @FXML private BarChart<String, Number> sleepBarChart;
    @FXML private BarChart<String, Number> socialBarChart;
    @FXML private BarChart<String, Number> dreamBarChart;
    // события месяца
    @FXML private GridPane monthEventsGrid;
    @FXML private VBox monthEventsLegend;

    private MonthEventDAO monthEventDAO;
    private Map<String, MonthEvent> monthEventsData = new HashMap<>();

    private ToggleGroup periodGroup = new ToggleGroup();
    private int currentYear;
    private DataDAO dataDAO;

    private final String[] monthLetters = {"Я", "Ф", "М", "А", "М", "И", "И", "А", "С", "О", "Н", "Д"};
    private final int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    // Хранилища данных
    private Map<String, Integer> socialData = new HashMap<>();
    private Map<String, Integer> ratingData = new HashMap<>();
    private Map<String, Integer> energyMorningData = new HashMap<>();
    private Map<String, Integer> energyEveningData = new HashMap<>();
    private Map<String, Integer> anxietyData = new HashMap<>();
    private Map<String, Integer> weatherData = new HashMap<>();
    private Map<String, Integer> sleepData = new HashMap<>();
    private Map<String, Integer> dreamData = new HashMap<>();

    // Цвета и легенды
    private final String[] socialColors = {"#000000", "#A69800", "#BFB330", "#FFF373", "#FFEF40", "#FFE900"};
    private final String[] socialLabels = {"0 - Нет контактов", "1 - Очень мало", "2 - Мало", "3 - Средне", "4 - Много", "5 - Очень много"};

    private final String[] ratingColors = {"#808080", "#FF6B6B", "#FF9F6B", "#FFE66B", "#A8E66B", "#6BCB6B"};
    private final String[] ratingLabels = {"0 ★", "1 ★", "2 ★★", "3 ★★★", "4 ★★★★", "5 ★★★★★"};

    private final String[] anxietyColors = {"#4CAF50", "#FFC107", "#FF9800", "#F44336"};
    private final String[] anxietyLabels = {"Не было", "Низкий", "Средний", "Высокий"};

    private final String[] weatherColors = {"#FFD700", "#B0C4DE", "#808080", "#4682B4", "#483D8B", "#778899", "#E0FFFF", "#A0522D"};
    private final String[] weatherLabels = {"☀ Солнечно", "☁ Облачно", "☁ Пасмурно", "☔ Дождь", "⛈ Дождь с грозой", "🌨 Облачно и снег", "❄ Снег", "💨 Ветер"};

    private final String[] sleepColors = {"#8B0000", "#CD5C5C", "#F4A460", "#FFD700", "#90EE90", "#006400"};
    private final String[] sleepLabels = {"≤4 ч", "5 ч", "6 ч", "7 ч", "8 ч", "9+ ч"};

    private final String[] dreamColors = {"#E0E0E0", "#C0C0C0", "#FFD700", "#FF69B4", "#4682B4", "#9370DB", "#FF8C00", "#8B0000", "#000000"};
    private final String[] dreamLabels = {"Не было", "Не запомнился", "Смешной", "Радостный", "Грустный", "Странный", "Абсурдный", "Страшный", "Кошмар"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataDAO = new DataDAO();
        monthEventDAO = new MonthEventDAO();
        System.out.println("dataDAO инициализирован: " + (dataDAO != null));
        currentYear = LocalDate.now().getYear();

        yearSelector.getItems().addAll(currentYear - 2, currentYear - 1, currentYear, currentYear + 1, currentYear + 2);
        yearSelector.setValue(currentYear);
        yearSelector.setOnAction(e -> loadYear(yearSelector.getValue()));

        createAllLegends();
        loadDataFromDatabase(currentYear);
        loadMonthEventsFromDatabase(currentYear);
        loadYear(currentYear);
        initStatistics();
    }

    private void loadDataFromDatabase(int year) {
        System.out.println("📂 Загрузка данных за " + year + " год...");

        socialData = dataDAO.loadDataForYear(year, "social", null);
        ratingData = dataDAO.loadDataForYear(year, "rating", null);
        anxietyData = dataDAO.loadDataForYear(year, "anxiety", null);
        weatherData = dataDAO.loadDataForYear(year, "weather", null);
        sleepData = dataDAO.loadDataForYear(year, "sleep", null);
        dreamData = dataDAO.loadDataForYear(year, "dream", null);
        energyMorningData = dataDAO.loadDataForYear(year, "energy", "morning");
        energyEveningData = dataDAO.loadDataForYear(year, "energy", "evening");

    }

    private void loadMonthEventsFromDatabase(int year) {
        System.out.println("📂 Загрузка событий месяца за " + year + " год...");
        List<MonthEvent> events = monthEventDAO.getAllForYear(year);
        monthEventsData.clear();
        for (MonthEvent event : events) {
            String key = event.getYear() + "-" + event.getMonth();
            monthEventsData.put(key, event);
            System.out.println("Загружено событие: " + key + " = " + event.getEventText());
        }
    }

    private void loadMonthEventsGrid() {
        monthEventsGrid.getChildren().clear();

        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

        for (int month = 1; month <= 12; month++) {
            int col = (month - 1) % 4;
            int row = (month - 1) / 4;

            String key = currentYear + "-" + month;
            MonthEvent event = monthEventsData.get(key);
            boolean hasEvent = event != null && event.isHasEvent();

            VBox monthCard = new VBox(10);
            monthCard.setAlignment(Pos.CENTER);
            monthCard.setPadding(new Insets(15));
            monthCard.setPrefSize(180, 130);

            // Стилизация
            String borderColor = hasEvent ? "#FFD700" : "#8B4513";
            String borderWidth = hasEvent ? "3px" : "2px";
            String bgColor = hasEvent ? "#FFF4B0" : "#FFF8DC";
            monthCard.setStyle(String.format(
                    "-fx-border-color: %s; -fx-border-width: %s; -fx-background-color: %s; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;",
                    borderColor, borderWidth, bgColor
            ));

            // Заголовок — месяц
            Label monthLabel = new Label(monthNames[month - 1]);
            monthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2C3E50;");

            // Текст события (превью)
            String eventPreview = "";
            if (hasEvent && event.getEventText() != null) {
                String text = event.getEventText();
                eventPreview = text.length() > 30 ? text.substring(0, 30) + "..." : text;
            }
            Label eventLabel = new Label(hasEvent ? eventPreview : "Нет события");
            eventLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
            eventLabel.setWrapText(true);
            eventLabel.setMaxWidth(160);
            eventLabel.setAlignment(Pos.CENTER);

            // Индикатор
            Circle indicator = new Circle(6);
            indicator.setFill(hasEvent ? Color.web("#FFD700") : Color.web("#D3D3D3"));
            indicator.setStroke(Color.web("#8B4513"));
            indicator.setStrokeWidth(1);

            monthCard.getChildren().addAll(indicator, monthLabel, eventLabel);

            // Клик — открыть диалог
            final int currentMonth = month;
            final String currentKey = key;
            monthCard.setOnMouseClicked(e -> openMonthEventDialog(currentMonth, currentKey, monthCard));

            monthEventsGrid.add(monthCard, col, row);
        }
    }

    private void openMonthEventDialog(int month, String key, VBox monthCard) {

        if (DatabaseConnection.isGuest()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Гостевой режим");
            alert.setHeaderText("Войдите, чтобы сохранять данные");
            alert.setContentText("В гостевом режиме можно только просматривать данные.");
            alert.showAndWait();
            return;
        }

        MonthEvent currentEvent = monthEventsData.get(key);
        String currentText = currentEvent != null ? currentEvent.getEventText() : "";

        Dialog<String> dialog = new Dialog<>();
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        dialog.setTitle("Событие месяца");
        dialog.setHeaderText(monthNames[month - 1] + " " + currentYear);

        ButtonType saveButton = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteButton = new ButtonType("Удалить", ButtonBar.ButtonData.LEFT);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, deleteButton, cancelButton);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20px;");

        Label label = new Label("Опишите главное событие месяца:");
        TextArea textArea = new TextArea(currentText);
        textArea.setPrefRowCount(5);
        textArea.setPrefColumnCount(30);
        textArea.setWrapText(true);

        Label counter = new Label("Символов: " + currentText.length());
        textArea.textProperty().addListener((obs, oldVal, newVal) ->
                counter.setText("Символов: " + newVal.length())
        );

        content.getChildren().addAll(label, textArea, counter);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return textArea.getText().trim();
            } else if (dialogButton == deleteButton) {
                return ""; // пустая строка = удаление
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
            MonthEvent event = new MonthEvent(currentYear, month);
            event.setEventText(text.isEmpty() ? null : text);

            monthEventDAO.saveOrUpdate(event);

            if (text.isEmpty()) {
                monthEventsData.remove(key);
            } else {
                monthEventsData.put(key, event);
            }

            loadMonthEventsGrid(); // Перерисовать
        });
    }

    private void saveToDatabase(LocalDate date, String trackerType, Integer value, String subType) {
        if (dataDAO == null) {
            System.err.println("dataDAO is NULL!");
            return;
        }
        System.out.println("saveToDatabase вызван: " + date + ", " + trackerType + ", " + value + ", " + subType);
        dataDAO.saveValue(date, trackerType, value, subType);
    }

    private void createAllLegends() {
        createLegend(socialLegend, socialColors, socialLabels);
        createLegend(ratingLegend, ratingColors, ratingLabels);
        createEnergyLegend();
        createLegend(anxietyLegend, anxietyColors, anxietyLabels);
        createLegend(weatherLegend, weatherColors, weatherLabels);
        createLegend(sleepLegend, sleepColors, sleepLabels);
        createLegend(dreamLegend, dreamColors, dreamLabels);
    }

    private void createLegend(VBox legendBox, String[] colors, String[] labels) {
        legendBox.getChildren().clear();
        Label title = new Label("📖 Справочник");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        legendBox.getChildren().add(title);

        for (int i = 0; i < colors.length; i++) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Rectangle colorBox = new Rectangle(30, 20);
            colorBox.setFill(Color.web(colors[i]));
            colorBox.setStroke(Color.GRAY);

            Label text = new Label(labels[i]);
            text.setStyle("-fx-font-size: 12px;");

            row.getChildren().addAll(colorBox, text);
            legendBox.getChildren().add(row);
        }
    }

    private void createEnergyLegend() {
        energyLegend.getChildren().clear();
        Label title = new Label("📖 Справочник");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        energyLegend.getChildren().add(title);

        Label gradientLabel = new Label("0% (Низкая) → 100% (Высокая)");
        gradientLabel.setStyle("-fx-font-size: 12px;");
        energyLegend.getChildren().add(gradientLabel);

        HBox gradientBox = new HBox(0);
        for (int i = 0; i <= 10; i++) {
            Rectangle rect = new Rectangle(25, 20);
            int red = (int)(255 * (1 - i/10.0));
            int green = (int)(255 * (i/10.0));
            rect.setFill(Color.rgb(red, green, 0));
            rect.setStroke(Color.GRAY);
            gradientBox.getChildren().add(rect);
        }
        energyLegend.getChildren().add(gradientBox);

        Label note = new Label("Утро (верхняя таблица) | Вечер (нижняя таблица)");
        note.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        energyLegend.getChildren().add(note);
    }

    private void loadYear(int year) {
        currentYear = year;
        currentYearLabel.setText(String.valueOf(year));

        loadGrid(socialGrid, socialData, socialColors, "social", null);
        loadGrid(ratingGrid, ratingData, ratingColors, "rating", null);
        loadGrid(anxietyGrid, anxietyData, anxietyColors, "anxiety", null);
        loadGrid(weatherGrid, weatherData, weatherColors, "weather", null);
        loadGrid(sleepGrid, sleepData, sleepColors, "sleep", null);
        loadGrid(dreamGrid, dreamData, dreamColors, "dream", null);
        loadEnergyGrids();
        loadMonthEventsGrid();
    }

    private void loadGrid(GridPane grid, Map<String, Integer> data, String[] colors, String trackerType, String subType) {
        grid.getChildren().clear();

        Text cornerText = new Text("");
        cornerText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        grid.add(cornerText, 0, 0);

        for (int col = 0; col < 12; col++) {
            Text monthText = new Text(monthLetters[col]);
            monthText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            monthText.setWrappingWidth(50);
            monthText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            grid.add(monthText, col + 1, 0);
        }

        for (int day = 1; day <= 31; day++) {
            Text dayText = new Text(String.valueOf(day));
            dayText.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            grid.add(dayText, 0, day);

            for (int month = 0; month < 12; month++) {
                int daysInMonth = getDaysInMonth(currentYear, month);

                if (day <= daysInMonth) {
                    String key = currentYear + "-" + (month + 1) + "-" + day;
                    if (subType != null) {
                        key += "-" + subType;
                    }
                    Integer value = data.get(key);

                    Rectangle cell = new Rectangle(50, 30);
                    cell.setStroke(Color.GRAY);
                    cell.setStrokeWidth(0.5);

                    // Для отображения цвета нужно преобразовать значение в индекс
                    int colorIndex = -1;
                    if (value != null) {
                        if ("weather".equals(trackerType)) {
                            // Значение погоды 1-8 -> индекс 0-7
                            if (value >= 1 && value <= colors.length) {
                                colorIndex = value - 1;
                            }
                        } else if ("dream".equals(trackerType)) {
                            // Сны 1-9 -> индекс 0-8
                            if (value >= 1 && value <= colors.length) {
                                colorIndex = value - 1;
                            }
                        } else if ("sleep".equals(trackerType)) {
                            // Сон 1-6 -> индекс 0-5
                            if (value >= 1 && value <= colors.length) {
                                colorIndex = value - 1;
                            }
                        } else {
                            // Остальные трекеры (значения 0-5, 0-3 и т.д.)
                            if (value >= 0 && value < colors.length) {
                                colorIndex = value;
                            }
                        }
                    }

                    if (colorIndex >= 0 && colorIndex < colors.length) {
                        cell.setFill(Color.web(colors[colorIndex]));
                    } else {
                        cell.setFill(Color.WHITE);
                    }

                    // СОЗДАЁМ FINAL ПЕРЕМЕННЫЕ ДЛЯ ЛЯМБДЫ
                    final int currentMonth = month;
                    final int currentDay = day;
                    final Rectangle currentCell = cell;
                    final Map<String, Integer> currentData = data;
                    final String[] currentColors = colors;
                    final String[] currentLabels = getLabelsForTracker(trackerType);
                    final String currentTrackerType = trackerType;
                    final String currentSubType = subType;
                    final String currentKey = key;

                    cell.setOnMouseClicked(e -> openDialogForTracker(
                            currentTrackerType, currentSubType, currentKey, currentMonth, currentDay,
                            currentCell, currentData, currentColors, currentLabels
                    ));

                    grid.add(cell, month + 1, day);
                } else {
                    Rectangle grayCell = new Rectangle(50, 30);
                    grayCell.setFill(Color.LIGHTGRAY);
                    grayCell.setStroke(Color.GRAY);
                    grayCell.setStrokeWidth(0.5);
                    grid.add(grayCell, month + 1, day);
                }
            }
        }
    }

    private String[] getLabelsForTracker(String trackerType) {
        return switch (trackerType) {
            case "social" -> socialLabels;
            case "rating" -> ratingLabels;
            case "anxiety" -> anxietyLabels;
            case "weather" -> weatherLabels;
            case "sleep" -> sleepLabels;
            case "dream" -> dreamLabels;
            default -> new String[]{};
        };
    }

    private void openDialogForTracker(String trackerType, String subType, String key, int month, int day,
                                      Rectangle cell, Map<String, Integer> data, String[] colors, String[] labels) {
        if (DatabaseConnection.isGuest()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Гостевой режим");
            alert.setHeaderText("Войдите, чтобы сохранять данные");
            alert.setContentText("В гостевом режиме можно только просматривать данные.");
            alert.showAndWait();
            return;
        }

        Integer currentValue = data.get(key);

        Dialog<Integer> dialog = new Dialog<>();
        String title = switch (trackerType) {
            case "social" -> "Социальный контакт";
            case "rating" -> "Рейтинг дня";
            case "anxiety" -> "Тревожность";
            case "weather" -> "Погода";
            case "sleep" -> "Сон (часы)";
            case "dream" -> "Сны";
            default -> "Выбор значения";
        };
        dialog.setTitle(title);
        dialog.setHeaderText(currentYear + "-" + (month + 1) + "-" + day);

        ButtonType saveButton = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20px;");

        ToggleGroup group = new ToggleGroup();

        for (int i = 0; i < colors.length; i++) {
            RadioButton radio = new RadioButton(labels[i]);
            radio.setUserData(i);
            radio.setToggleGroup(group);

            Rectangle colorPreview = new Rectangle(20, 20);
            colorPreview.setFill(Color.web(colors[i]));
            colorPreview.setStroke(Color.GRAY);

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().addAll(radio, colorPreview);

            content.getChildren().add(row);

            // Для отображения текущего значения погоды преобразуем его в индекс
            int compareValue = i;
            if ("weather".equals(trackerType) && currentValue != null && currentValue >= 1) {
                compareValue = currentValue - 1;  // значение 1-8 -> индекс 0-7
            }
            if (currentValue != null && compareValue == i) {
                radio.setSelected(true);
            }
        }

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                if (selected != null) {
                    int selectedIndex = (Integer) selected.getUserData();
                    // Для погоды преобразуем индекс в значение (1-8)
                    if ("weather".equals(trackerType) || "sleep".equals(trackerType) || "dream".equals(trackerType)) {
                        return selectedIndex + 1;
                    }
                    return selectedIndex;
                }
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(value -> {
            // Сохраняем в память
            data.put(key, value);

            // Вычисляем индекс цвета для отображения
            int displayIndex;
            if ("weather".equals(trackerType) || "sleep".equals(trackerType) || "dream".equals(trackerType)) {
                // Эти трекеры хранят значения 1-based, но colors 0-based
                displayIndex = value - 1;
            } else {
                displayIndex = value;
            }
            cell.setFill(Color.web(colors[displayIndex]));

            // Сохраняем в БД
            LocalDate date = LocalDate.of(currentYear, month + 1, day);
            System.out.println(">>> Сохраняем в БД: " + trackerType + ", date=" + date + ", value=" + value + ", subType=" + subType);
            saveToDatabase(date, trackerType, value, subType);

            refreshStatistics();
        });
    }

    private void loadEnergyGrids() {
        energyMorningGrid.getChildren().clear();
        energyEveningGrid.getChildren().clear();

        Text cornerMorning = new Text("Утро");
        cornerMorning.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        energyMorningGrid.add(cornerMorning, 0, 0);

        for (int col = 0; col < 12; col++) {
            Text monthText = new Text(monthLetters[col]);
            monthText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            monthText.setWrappingWidth(50);
            monthText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            energyMorningGrid.add(monthText, col + 1, 0);
        }

        Text cornerEvening = new Text("Вечер");
        cornerEvening.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        energyEveningGrid.add(cornerEvening, 0, 0);

        for (int col = 0; col < 12; col++) {
            Text monthText = new Text(monthLetters[col]);
            monthText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            monthText.setWrappingWidth(50);
            monthText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            energyEveningGrid.add(monthText, col + 1, 0);
        }

        for (int day = 1; day <= 31; day++) {
            Text dayText = new Text(String.valueOf(day));
            dayText.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            energyMorningGrid.add(dayText, 0, day);
            energyEveningGrid.add(new Text(String.valueOf(day)), 0, day);

            for (int month = 0; month < 12; month++) {
                int daysInMonth = getDaysInMonth(currentYear, month);

                if (day <= daysInMonth) {
                    String keyMorning = currentYear + "-" + (month + 1) + "-" + day + "-morning";
                    String keyEvening = currentYear + "-" + (month + 1) + "-" + day + "-evening";

                    Integer morningValue = energyMorningData.get(keyMorning);
                    Integer eveningValue = energyEveningData.get(keyEvening);

                    Rectangle morningCell = createEnergyCell(morningValue);
                    Rectangle eveningCell = createEnergyCell(eveningValue);

                    // СОЗДАЁМ FINAL ПЕРЕМЕННЫЕ ДЛЯ ЛЯМБД
                    final int currentMonth = month;
                    final int currentDay = day;
                    final String currentKeyMorning = keyMorning;
                    final String currentKeyEvening = keyEvening;
                    final Rectangle currentMorningCell = morningCell;
                    final Rectangle currentEveningCell = eveningCell;
                    final Map<String, Integer> currentMorningData = energyMorningData;
                    final Map<String, Integer> currentEveningData = energyEveningData;

                    morningCell.setOnMouseClicked(e -> openEnergyDialog(
                            currentKeyMorning, currentMonth, currentDay, currentMorningCell, currentMorningData, "morning"
                    ));
                    eveningCell.setOnMouseClicked(e -> openEnergyDialog(
                            currentKeyEvening, currentMonth, currentDay, currentEveningCell, currentEveningData, "evening"
                    ));

                    energyMorningGrid.add(morningCell, month + 1, day);
                    energyEveningGrid.add(eveningCell, month + 1, day);
                } else {
                    Rectangle grayCell = new Rectangle(50, 30);
                    grayCell.setFill(Color.LIGHTGRAY);
                    grayCell.setStroke(Color.GRAY);
                    energyMorningGrid.add(grayCell, month + 1, day);
                    energyEveningGrid.add(grayCell, month + 1, day);
                }
            }
        }
    }

    private Rectangle createEnergyCell(Integer value) {
        Rectangle cell = new Rectangle(50, 30);
        cell.setStroke(Color.GRAY);
        cell.setStrokeWidth(0.5);

        if (value != null) {
            int red = (int)(255 * (1 - value / 100.0));
            int green = (int)(255 * (value / 100.0));
            cell.setFill(Color.rgb(red, green, 0));
        } else {
            cell.setFill(Color.WHITE);
        }

        return cell;
    }

    private void openEnergyDialog(String key, int month, int day, Rectangle cell, Map<String, Integer> data, String timeOfDay) {
        if (DatabaseConnection.isGuest()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Гостевой режим");
            alert.setHeaderText("Войдите, чтобы сохранять данные");
            alert.setContentText("В гостевом режиме можно только просматривать данные.");
            alert.showAndWait();
            return;
        }

        Integer currentValue = data.get(key);

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Энергия - " + (timeOfDay.equals("morning") ? "Утро" : "Вечер"));
        dialog.setHeaderText(currentYear + "-" + (month + 1) + "-" + day);

        ButtonType saveButton = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        VBox content = new VBox(20);
        content.setStyle("-fx-padding: 20px;");

        Label label = new Label("Уровень энергии (0-100):");
        Slider slider = new Slider(0, 100, currentValue != null ? currentValue : 50);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(50);
        slider.setMinorTickCount(10);
        slider.setBlockIncrement(10);

        Label valueLabel = new Label("Текущее значение: " + (int)slider.getValue());
        slider.valueProperty().addListener((obs, oldVal, newVal) ->
                valueLabel.setText("Текущее значение: " + newVal.intValue())
        );

        Rectangle preview = new Rectangle(50, 30);
        preview.setStroke(Color.GRAY);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int val = newVal.intValue();
            int red = (int)(255 * (1 - val / 100.0));
            int green = (int)(255 * (val / 100.0));
            preview.setFill(Color.rgb(red, green, 0));
        });
        int initVal = currentValue != null ? currentValue : 50;
        int red = (int)(255 * (1 - initVal / 100.0));
        int green = (int)(255 * (initVal / 100.0));
        preview.setFill(Color.rgb(red, green, 0));

        content.getChildren().addAll(label, slider, valueLabel, preview);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return (int) slider.getValue();
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(value -> {
            data.put(key, value);
            int newRed = (int)(255 * (1 - value / 100.0));
            int newGreen = (int)(255 * (value / 100.0));
            cell.setFill(Color.rgb(newRed, newGreen, 0));

            // Сохраняем в БД
            LocalDate date = LocalDate.of(currentYear, month + 1, day);
            saveToDatabase(date, "energy", value, timeOfDay);
            refreshStatistics();
        });
    }

    private int getDaysInMonth(int year, int monthIndex) {
        if (monthIndex == 1) {
            boolean isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            return isLeap ? 29 : 28;
        }
        return monthDays[monthIndex];
    }

    /**
     * Инициализация статистики (вызвать в initialize())
     */
    private void initStatistics() {
        // Годы для выбора
        statsYearCombo.getItems().addAll(currentYear - 2, currentYear - 1, currentYear, currentYear + 1, currentYear + 2);
        statsYearCombo.setValue(currentYear);

        // Месяцы
        String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        statsMonthCombo.getItems().addAll(months);
        statsMonthCombo.setValue(months[LocalDate.now().getMonthValue() - 1]);

        // Объединяем радио-кнопки в ToggleGroup
        ToggleGroup periodToggleGroup = new ToggleGroup();
        weekRadio.setToggleGroup(periodToggleGroup);
        monthRadio.setToggleGroup(periodToggleGroup);
        yearRadio.setToggleGroup(periodToggleGroup);

        // Устанавливаем значение по умолчанию
        monthRadio.setSelected(true);

        // Обработчик переключения периода
        periodToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String period = (String) newVal.getUserData();
                boolean isMonthEnabled = "month".equals(period);
                statsMonthCombo.setDisable(!isMonthEnabled);
                refreshStatistics();
            }
        });

        // Обработчики для комбобоксов
        statsYearCombo.setOnAction(e -> refreshStatistics());
        statsMonthCombo.setOnAction(e -> refreshStatistics());

        // Первоначальное обновление
        refreshStatistics();
    }

    /**
     * Обновление всей статистики на основе выбранного периода
     */
    @FXML
    private void refreshStatistics() {
        String period = getSelectedPeriod();
        int year = statsYearCombo.getValue();
        int month = statsMonthCombo.getSelectionModel().getSelectedIndex() + 1;

        // ЗАГРУЖАЕМ ДАННЫЕ ДЛЯ ВЫБРАННОГО ГОДА, ЕСЛИ НУЖНО
        if (year != currentYear) {
            loadDataFromDatabase(year);
            currentYear = year;
            // Также обновляем основной календарь, если нужно
            yearSelector.setValue(year);
            loadYear(year);
        }

        // Получаем дни для выбранного периода
        List<String> days = getDaysForPeriod(period, year, month);

        System.out.println("\n=== ОБНОВЛЕНИЕ СТАТИСТИКИ ===");
        System.out.println("Период: " + period + ", Год: " + year + ", Месяц: " + month);
        System.out.println("Дней в периоде: " + days.size());

        // Отладочный вывод
        System.out.println("Статистика за период: " + period + ", год=" + year + ", месяц=" + month);
        System.out.println("Дней в периоде: " + days.size());
        System.out.println("Данных по рейтингу: " + ratingData.size());
        System.out.println("Данных по снам: " + dreamData.size());

        // Обновляем каждый график
        updateRatingLineChart(days);
        updateEnergyLineChart(days);
        updateAnxietyPieChart(days);
        updateWeatherPieChart(days);
        updateSleepBarChart(days);
        updateSocialBarChart(days);
        updateDreamBarChart(days);

        debugPrintData(days, "Сны (dreamData)", dreamData);
        debugPrintData(days, "Погода (weatherData)", weatherData);
        debugPrintData(days, "Сон (sleepData)", sleepData);
    }

    /**
     * Получение выбранного периода
     */
    private String getSelectedPeriod() {
        if (weekRadio.isSelected()) return "week";
        if (monthRadio.isSelected()) return "month";
        if (yearRadio.isSelected()) return "year";
        return "month"; // по умолчанию
    }

    /**
     * Вспомогательный метод для получения названия месяца
     */
    private String getMonthName(int month) {
        String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        return months[month - 1];
    }

    /**
     * Получение списка дней для выбранного периода
     */
    private List<String> getDaysForPeriod(String period, int year, int month) {
        List<String> days = new ArrayList<>();

        switch (period) {
            case "week": {
                // ИСПРАВЛЕНО: используем выбранный год и месяц, а не текущую дату
                // возьмем первый день выбранного месяца
                LocalDate startOfMonth = LocalDate.of(year, month, 1);
                // Ищем последнюю полную неделю в выбранном месяце
                // Для простоты: показываем последние 7 дней выбранного месяца
                int daysInMonth = getDaysInMonth(year, month - 1);
                LocalDate endDate = LocalDate.of(year, month, daysInMonth);
                LocalDate startDate = endDate.minusDays(6);

                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    days.add(date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth());
                }
                break;
            }
            case "month": {
                int daysInMonth = getDaysInMonth(year, month - 1);
                for (int d = 1; d <= daysInMonth; d++) {
                    days.add(year + "-" + month + "-" + d);
                }
                break;
            }
            case "year": {
                for (int m = 1; m <= 12; m++) {
                    int daysInMonth = getDaysInMonth(year, m - 1);
                    for (int d = 1; d <= daysInMonth; d++) {
                        days.add(year + "-" + m + "-" + d);
                    }
                }
                break;
            }
        }
        return days;
    }

    /**
     * График 1: Рейтинг дня (линия)
     */
    private void updateRatingLineChart(List<String> days) {
        ratingLineChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Рейтинг дня");

        for (String day : days) {
            Integer value = ratingData.get(day);
            String label = extractDayLabel(day);
            series.getData().add(new XYChart.Data<>(label, value != null ? value : 0));
        }

        ratingLineChart.getData().add(series);
    }

    /**
     * График 2: Энергия (утро и вечер - две линии)
     */
    private void updateEnergyLineChart(List<String> days) {
        energyLineChart.getData().clear();

        XYChart.Series<String, Number> morningSeries = new XYChart.Series<>();
        morningSeries.setName("Энергия утром");

        XYChart.Series<String, Number> eveningSeries = new XYChart.Series<>();
        eveningSeries.setName("Энергия вечером");

        for (String day : days) {
            String morningKey = day + "-morning";
            String eveningKey = day + "-evening";
            String label = extractDayLabel(day);

            Integer morningVal = energyMorningData.get(morningKey);
            Integer eveningVal = energyEveningData.get(eveningKey);

            morningSeries.getData().add(new XYChart.Data<>(label, morningVal != null ? morningVal : 0));
            eveningSeries.getData().add(new XYChart.Data<>(label, eveningVal != null ? eveningVal : 0));
        }

        energyLineChart.getData().addAll(morningSeries, eveningSeries);
    }

    /**
     * График 3: Тревожность (круговая)
     */
    private void updateAnxietyPieChart(List<String> days) {
        anxietyPieChart.getData().clear();

        int[] counts = new int[4]; // 0-не было, 1-низкий, 2-средний, 3-высокий
        String[] labels = {"Не было", "Низкий", "Средний", "Высокий"};
        String[] colors = {"#4CAF50", "#FFC107", "#FF9800", "#F44336"};

        for (String day : days) {
            Integer value = anxietyData.get(day);
            if (value != null && value >= 0 && value <= 3) {
                counts[value]++;
            }
        }

        for (int i = 0; i < labels.length; i++) {
            if (counts[i] > 0) {
                PieChart.Data slice = new PieChart.Data(labels[i], counts[i]);
                anxietyPieChart.getData().add(slice);
            }
        }
    }

    /**
     * График 4: Погода (круговая)
     */
    private void updateWeatherPieChart(List<String> days) {
        weatherPieChart.getData().clear();

        int[] counts = new int[8];
        String[] labels = {"Солнечно", "Облачно", "Пасмурно", "Дождь",
                "Дождь с грозой", "Облачно и снег", "Снег", "Ветер"};

        for (String day : days) {
            Integer value = weatherData.get(day);
            if (value != null && value >= 1 && value <= 8) {
                counts[value - 1]++;
                System.out.println("Погода для " + day + ": value=" + value + " -> " + labels[value - 1]);
            }
        }

        System.out.println("=== Погода (статистика) ===");
        for (int i = 0; i < labels.length; i++) {
            if (counts[i] > 0) {
                System.out.println(labels[i] + ": " + counts[i]);
            }
        }

        for (int i = 0; i < labels.length; i++) {
            if (counts[i] > 0) {
                weatherPieChart.getData().add(new PieChart.Data(labels[i], counts[i]));
            }
        }

        if (weatherPieChart.getData().isEmpty()) {
            weatherPieChart.setTitle("Нет данных за период");
        }
    }

    /**
     * График 5: Сон (столбцы)
     */
    private void updateSleepBarChart(List<String> days) {
        sleepBarChart.getData().clear();

        int[] counts = new int[6];
        String[] labels = {"≤4 ч", "5 ч", "6 ч", "7 ч", "8 ч", "9+ ч"};

        for (String day : days) {
            Integer value = sleepData.get(day);
            if (value != null && value >= 1 && value <= 6) {
                counts[value - 1]++;
                System.out.println("Сон для " + day + ": value=" + value + " -> " + labels[value - 1]);
            }
        }

        System.out.println("=== Сон (статистика) ===");
        for (int i = 0; i < labels.length; i++) {
            if (counts[i] > 0) {
                System.out.println(labels[i] + ": " + counts[i]);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Количество дней");

        for (int i = 0; i < labels.length; i++) {
            series.getData().add(new XYChart.Data<>(labels[i], counts[i]));
        }

        sleepBarChart.getData().add(series);
    }

    /**
     * График 6: Социальный контакт (столбцы)
     */
    private void updateSocialBarChart(List<String> days) {
        socialBarChart.getData().clear();

        int[] counts = new int[6];
        String[] labels = {"0", "1", "2", "3", "4", "5"};

        for (String day : days) {
            Integer value = socialData.get(day);
            if (value != null && value >= 0 && value <= 5) {
                counts[value]++;
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Количество дней");

        for (int i = 0; i < labels.length; i++) {
            series.getData().add(new XYChart.Data<>(labels[i], counts[i]));
        }

        socialBarChart.getData().add(series);
    }

    /**
     * График 7: Сны (столбцы)
     */
    private void updateDreamBarChart(List<String> days) {
        dreamBarChart.getData().clear();

        int[] counts = new int[9];
        String[] labels = {"Не было", "Не запомнился", "Смешной", "Радостный",
                "Грустный", "Странный", "Абсурдный", "Страшный", "Кошмар"};

        for (String day : days) {
            Integer value = dreamData.get(day);
            if (value != null && value >= 1 && value <= 9) {
                counts[value - 1]++;  // value=1 -> индекс 0, value=9 -> индекс 8
                System.out.println("Сон для " + day + ": value=" + value + " -> индекс=" + (value - 1));
            }
        }

        // Вывод для отладки
        System.out.println("=== Сны (статистика) ===");
        for (int i = 0; i < labels.length; i++) {
            if (counts[i] > 0) {
                System.out.println(labels[i] + ": " + counts[i]);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Количество дней");

        for (int i = 0; i < labels.length; i++) {
            series.getData().add(new XYChart.Data<>(labels[i], counts[i]));
        }

        dreamBarChart.getData().add(series);
    }

    /**
     * Извлечение короткой метки для оси X (день или месяц-день)
     */
    private String extractDayLabel(String dayKey) {
        String[] parts = dayKey.split("-");
        if (parts.length >= 3) {
            String period = getSelectedPeriod();
            if ("year".equals(period)) {
                return parts[1] + "/" + parts[2]; // месяц/день
            } else {
                return parts[2]; // только день
            }
        }
        return dayKey;
    }

    /**
     * Отладка: вывод всех данных по дням
     */
    private void debugPrintData(List<String> days, String dataName, Map<String, Integer> data) {
        System.out.println("--- " + dataName + " ---");
        for (String day : days) {
            Integer value = data.get(day);
            if (value != null) {
                System.out.println(day + " = " + value);
            }
        }
    }
}