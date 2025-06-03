package org.example;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DateConverter {

    //Date format
    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("EEE MMM dd HH:mm:ss z yyyy")
                    .toFormatter(Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    public static String timestampToDate(long timestamp, int format) {

        DateTimeFormatter formatter = switch (format) {
            case 0 -> //Default format (dd/MM/yyyy)
                    FORMATTERS[0];
            case 1 -> //American format (MM/dd/yyyy)
                    FORMATTERS[1];
            case 2 -> //Extended format with time zone
                    FORMATTERS[2]; // Uses the predefined format with time zone
            case 3 -> //Opposite format than default one
                    FORMATTERS[3];
            default -> throw new IllegalArgumentException("Unknown format. Use 0, 1, or 2.");
        };

        if (format == 2) {
            //For extended format, include time and zone
            ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault());
            return zdt.format(formatter);
        } else {
            //For date-only formats
            LocalDate date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
            return date.format(formatter);
        }
    }

    public static long dateToTimestamp(String dateStr, int format) {
        try {
            DateTimeFormatter formatter = FORMATTERS[format];

            //For formats without time zone, parse as LocalDate
            if (format == 0 || format == 1 || format == 3) {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } else {
                //For format with time zone
                ZonedDateTime zdt = ZonedDateTime.parse(dateStr, formatter);
                return zdt.toInstant().toEpochMilli();
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date does not match expected format.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a date or timestamp:");
        String input = scanner.nextLine();

        try {
            if (input.matches("\\d+")) { //If it is a number, treat it as timestamp
                long timestamp = Long.parseLong(input);
                System.out.println("Choose your format according to the assigned number: 'dd/MM/yyyy'=0, 'MM/dd/yyyy'=1, 'EEE MMM dd HH:mm:ss z yyyy'=2, 'yyyy/MM/dd'=3");
                int format = scanner.nextInt();
                System.out.println("Equivalent date: " + timestampToDate(timestamp,format));
            } else { //If not, treat it as a date
                System.out.println("Specify the date format:");
                System.out.println("0: dd/MM/yyyy");
                System.out.println("1: MM/dd/yyyy");
                System.out.println("2: EEE MMM dd HH:mm:ss z yyyy");
                System.out.println("3: yyyy/MM/dd");
                int format = scanner.nextInt();
                System.out.println("Equivalent timestamp: " + dateToTimestamp(input,format));
            }
        } catch (Exception e) {
            System.out.println("Invalid entry. Be sure to enter a valid date or numeric timestamp.");
        }
        scanner.close();
    }
}