package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingLambdaService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VikingLambdaFrame extends JFrame {

    private final VikingLambdaService lambdaService;
    private final VikingService vikingService;

    public VikingLambdaFrame(VikingLambdaService lambdaService, VikingService vikingService) {
        this.lambdaService = lambdaService;
        this.vikingService = vikingService;
        buildUi();
    }

    private void buildUi() {
        setTitle("Лямбда-сервис викингов");
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Задание 1: Подсчёт", buildCountTab());
        tabs.addTab("Задание 2: Дисплей", buildDisplayTab());
        tabs.addTab("Задание 3: ID", buildIdsTab());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildCountTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextArea output = new JTextArea(14, 50);
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JTextField olderField = new JTextField("30", 5);
        JButton olderBtn = new JButton("Старше возраста");
        olderBtn.addActionListener(e -> {
            int age = parseIntSafe(olderField.getText(), 30);
            long cnt = lambdaService.countOlderThan(age);
            output.append(String.format("Старше %d лет: %d чел.%n", age, cnt));
        });

        JTextField youngerField = new JTextField("25", 5);
        JButton youngerBtn = new JButton("Моложе возраста");
        youngerBtn.addActionListener(e -> {
            int age = parseIntSafe(youngerField.getText(), 25);
            long cnt = lambdaService.countYoungerThan(age);
            output.append(String.format("Моложе %d лет: %d чел.%n", age, cnt));
        });

        JTextField minField = new JTextField("20", 4);
        JTextField maxField = new JTextField("40", 4);
        JButton rangeBtn = new JButton("В диапазоне");
        rangeBtn.addActionListener(e -> {
            int min = parseIntSafe(minField.getText(), 20);
            int max = parseIntSafe(maxField.getText(), 40);
            long inRange = lambdaService.countInAgeRange(min, max);
            long outOfRange = lambdaService.countOutsideAgeRange(min, max);
            output.append(String.format("В диапазоне [%d, %d]: %d чел.%n", min, max, inRange));
            output.append(String.format("Вне диапазона [%d, %d]: %d чел.%n", min, max, outOfRange));
        });

        JComboBox<BeardStyle> beardBox = new JComboBox<>(BeardStyle.values());
        JComboBox<HairColor> hairBox = new JComboBox<>(HairColor.values());
        JButton beardHairBtn = new JButton("Борода + цвет волос");
        beardHairBtn.addActionListener(e -> {
            BeardStyle beard = (BeardStyle) beardBox.getSelectedItem();
            HairColor hair = (HairColor) hairBox.getSelectedItem();
            long cnt = lambdaService.countByBeardAndHair(beard, hair);
            output.append(String.format("Борода: %s, волосы: %s → %d чел.%n", beard, hair, cnt));
        });

        JButton axesBtn = new JButton("1 или 2 топора (OR)");
        axesBtn.addActionListener(e ->
                output.append(String.format("С одним ИЛИ двумя топорами: %d чел.%n",
                        lambdaService.countWithOneOrTwoAxes())));

        JButton bulkBtn = new JButton("Сгенерировать 10 викингов");
        bulkBtn.addActionListener(e -> {
            vikingService.generateBulkVikings(10);
            output.append("Сгенерировано 10 новых викингов!\n");
        });

        JButton clearBtn = new JButton("Очистить");
        clearBtn.addActionListener(e -> output.setText(""));

        int row = 0;
        addRow(panel, gbc, row++, new JLabel("Старше:"), olderField, olderBtn);
        addRow(panel, gbc, row++, new JLabel("Моложе:"), youngerField, youngerBtn);
        addRow(panel, gbc, row++, new JLabel("Мин:"), minField, new JLabel("Макс:"));
        addRow(panel, gbc, row++, maxField, rangeBtn, new JLabel(""));
        addRow(panel, gbc, row++, new JLabel("Борода:"), beardBox, new JLabel("Волосы:"));
        addRow(panel, gbc, row++, hairBox, beardHairBtn, new JLabel(""));
        addRow(panel, gbc, row++, axesBtn, bulkBtn, clearBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(output), gbc);

        return panel;
    }

    private JPanel buildDisplayTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        String[] columns = {"Имя", "Возраст", "Рост", "Волосы", "Борода", "Снаряжение"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        JButton tallBtn = new JButton("Случайный высокий (>180 см)");
        tallBtn.addActionListener(e -> {
            model.setRowCount(0);
            lambdaService.getRandomTallViking().ifPresent(v -> addVikingRow(model, v));
        });

        JButton legendaryBtn = new JButton("Все с легендарным снаряжением");
        legendaryBtn.addActionListener(e -> {
            model.setRowCount(0);
            lambdaService.getAllWithLegendaryEquipment().forEach(v -> addVikingRow(model, v));
        });

        JButton redBtn = new JButton("Рыжебородые, по возрасту");
        redBtn.addActionListener(e -> {
            model.setRowCount(0);
            lambdaService.getRedBeardedSortedByAge().forEach(v -> addVikingRow(model, v));
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(tallBtn);
        buttons.add(legendaryBtn);
        buttons.add(redBtn);

        panel.add(buttons, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildIdsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextArea output = new JTextArea(8, 40);
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JButton maxBtn = new JButton("Max ID");
        maxBtn.addActionListener(e ->
                lambdaService.findMaxId().ifPresentOrElse(
                        max -> output.append("Max ID: " + max + "\n"),
                        () -> output.append("Викингов нет\n")
                ));

        JButton evenBtn = new JButton("Чётные ID");
        evenBtn.addActionListener(e ->
                output.append("Чётные ID: " + lambdaService.findEvenIds() + "\n"));

        JButton clearBtn = new JButton("Очистить");
        clearBtn.addActionListener(e -> output.setText(""));

        int row = 0;
        addRow(panel, gbc, row++, maxBtn, evenBtn, clearBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(output), gbc);

        return panel;
    }

    private void addVikingRow(DefaultTableModel model, Viking v) {
        String equipment = v.equipment().stream()
                .map(e -> e.name() + " (" + e.quality() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("—");
        model.addRow(new Object[]{
                v.name(), v.age(), v.heightCm() + " см",
                v.hairColor(), v.beardStyle(), equipment
        });
    }

    private void addRow(JPanel panel, GridBagConstraints gbc,
                        int row, Component c1, Component c2, Component c3) {
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        gbc.gridy = row;
        gbc.gridx = 0; panel.add(c1, gbc);
        gbc.gridx = 1; panel.add(c2, gbc);
        gbc.gridx = 2; panel.add(c3, gbc);
    }

    private int parseIntSafe(String text, int fallback) {
        try { return Integer.parseInt(text.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }
}