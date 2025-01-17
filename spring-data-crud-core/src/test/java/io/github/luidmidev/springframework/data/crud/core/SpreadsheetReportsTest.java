package io.github.luidmidev.springframework.data.crud.core;

import io.github.luidmidev.springframework.data.crud.core.export.Exporter;
import io.github.luidmidev.springframework.data.crud.core.export.ExportConfig;
import io.github.luidmidev.springframework.data.crud.core.export.SpreadSheetExporter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpreadsheetReportsTest {

    private final Exporter service = new SpreadSheetExporter();

    @Test
    void generate() {
        var persons = getPeople();

        var config = ExportConfig.of(
                List.of("name", "lastName", "age", "car.brand", "car", "children.name", "children.age", "children"),
                List.of("Name", "Last Name", "Age", "Car Brand", "Car", "Child Name", "Child Age", "Children")
        );

        try {
            var timeMillis = System.currentTimeMillis();
            var report = service.export(persons, config).getBody();
            Assertions.assertNotNull(report);
            System.out.println("Time on generate report: " + (System.currentTimeMillis() - timeMillis));
            System.out.println("Report size: " + report.contentLength());

        } catch (Exception e) {
            fail(e);
        }


    }

    private static List<Person> getPeople() {
        var toyota = new Car("Toyota", "Corolla");
        var suzuki = new Car("Suzuki", "Swift");
        var persons = new ArrayList<>(List.of(
                new Person("John", "Doe", 30, toyota, List.of(new Child("Alice", 5), new Child("Bob", 10))),
                new Person("Jane", "Doe", 25, suzuki, List.of(new Child("Eve", 3))),
                new Person("Jack", "Doe", 35, null, List.of(new Child("Charlie", 7))),
                new Person("Jill", "Doe", 40, null, null),
                new Person("James", "Doe", 45, null, List.of(new Child("David", 15))),
                new Person("Jenny", "Doe", 50, null, List.of(new Child("Frank", 20))),
                new Person("Jorge", "Doe", 55, null, List.of(new Child("George", 25))),
                new Person("Javier", "Doe", 60, null, List.of(new Child("Helen", 30))),
                new Person("Jared", "Doe", 65, null, List.of(new Child("Ivan", 35), new Child("Jill", 40)))
        ));

        persons.addAll(getPeople(20000));
        return persons;
    }

    @SuppressWarnings("SameParameterValue")
    private static List<Person> getPeople(int size) {
        var persons = new java.util.ArrayList<Person>();
        for (var i = 0; i < size; i++) {
            persons.add(new Person("Juan", "Doe", 12, new Car("Toyota", "Corolla"), List.of(new Child("Alice", 5), new Child("Bob", 10))));
        }
        return persons;
    }


    @Data
    @AllArgsConstructor
    public static class Person {
        private String name;
        private String lastName;
        private int age;
        private Car car;
        private List<Child> children;
    }

    @Data
    @AllArgsConstructor
    public static class Car {
        private String brand;
        private String model;
    }

    @Data
    @AllArgsConstructor
    public static class Child {
        private String name;
        private int age;
    }
}