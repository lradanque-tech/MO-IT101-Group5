
package com.mycompany.payrollsystem;

/*
    BASIC PAYROLL SYSTEM

    This program reads employee information and attendance records
    from CSV files and computes payroll.

    USERS
    employee
    payroll_staff

    PASSWORD
    12345

    PROGRAM RULES
    - Work hours are counted only between 8:00 AM and 5:00 PM.
    - Payroll is displayed for months June to December.
    - Each month has two cutoff periods:
        1–15
        16–end of month
    - Government deductions are calculated using the
      combined salary of both cutoffs.
*/

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class PayrollSystem {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Menu options stored as Strings
        String employeeOption1 = "Enter your employee number";
        String employeeOption2 = "Exit";

        String payrollOption1 = "Process Payroll";
        String payrollOption2 = "Exit";

        String payrollSubOption1 = "One employee";
        String payrollSubOption2 = "All employees";
        String payrollSubOption3 = "Exit";

        // ================= LOGIN =================

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if(!(password.equals("12345") &&
           (username.equals("employee") || username.equals("payroll_staff")))){

            System.out.println("Incorrect username and/or password.");
            scanner.close();
            return;
        }

        // ================= EMPLOYEE MODE =================

        if(username.equals("employee")){

            System.out.println("\n1. " + employeeOption1);
            System.out.println("2. " + employeeOption2);

            if(!scanner.hasNextInt()){
                System.out.println("Invalid option.");
                return;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            if(choice == 2){
                scanner.close();
                return;
            }

            System.out.print("Enter employee number: ");

            if(scanner.hasNextInt()){
                int employeeNumber = scanner.nextInt();
                displayEmployee(employeeNumber);
            }else{
                System.out.println("Invalid employee number.");
            }
        }

        // ================= PAYROLL STAFF MODE =================

        else{

            System.out.println("\n1. " + payrollOption1);
            System.out.println("2. " + payrollOption2);

            if(!scanner.hasNextInt()){
                System.out.println("Invalid option.");
                return;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            if(choice == 2){
                scanner.close();
                return;
            }

            System.out.println("\n1. " + payrollSubOption1);
            System.out.println("2. " + payrollSubOption2);
            System.out.println("3. " + payrollSubOption3);

            if(!scanner.hasNextInt()){
                System.out.println("Invalid option.");
                return;
            }

            int subChoice = scanner.nextInt();
            scanner.nextLine();

            if(subChoice == 3){
                scanner.close();
                return;
            }

            if(subChoice == 1){

                System.out.print("Enter employee number: ");

                if(scanner.hasNextInt()){
                    int employeeNumber = scanner.nextInt();
                    processPayroll(employeeNumber);
                }else{
                    System.out.println("Invalid employee number.");
                }

            }else if(subChoice == 2){

                processAllPayroll();

            }else{
                System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    /*
        DISPLAY EMPLOYEE METHOD
        Displays employee information from employees.csv
    */
    static void displayEmployee(int employeeNumber){

        try{

            BufferedReader reader =
                    new BufferedReader(new FileReader("employees.csv"));

            reader.readLine(); // skip header

            String line;

            while((line = reader.readLine()) != null){

                String[] data = line.split(",");

                int empNo = Integer.parseInt(data[0]);

                if(empNo == employeeNumber){

                    System.out.println("\nEmployee Number: " + data[0]);
                    System.out.println("Employee Name: " + data[1]);
                    System.out.println("Birthday: " + data[2]);

                    reader.close();
                    return;
                }
            }

            System.out.println("Employee number does not exist.");
            reader.close();

        }catch(Exception e){
            System.out.println("Error reading employees file.");
        }
    }

    /*
        COMPUTE HOURS METHOD

        Calculates total hours worked.
        Only time between 8:00 AM and 5:00 PM is counted.
    */
    static double computeHours(String timeIn, String timeOut){

        LocalTime in = LocalTime.parse(timeIn);
        LocalTime out = LocalTime.parse(timeOut);

        LocalTime start = LocalTime.of(8,0);
        LocalTime end = LocalTime.of(17,0);

        if(in.isBefore(start))
            in = start;

        if(out.isAfter(end))
            out = end;

        return Duration.between(in,out).toMinutes()/60.0;
    }

    /*
        PROCESS PAYROLL METHOD

        Computes payroll for a specific employee
        from June to December.
    */
    static void processPayroll(int employeeNumber){

        try{

            BufferedReader employeeFile =
                    new BufferedReader(new FileReader("employees.csv"));

            employeeFile.readLine();

            String line;

            String employeeName = "";
            String birthday = "";
            double hourlyRate = 0;

            while((line = employeeFile.readLine()) != null){

                String[] data = line.split(",");

                int empNo = Integer.parseInt(data[0]);

                if(empNo == employeeNumber){

                    employeeName = data[1];
                    birthday = data[2];
                    hourlyRate = Double.parseDouble(data[3]);
                    break;
                }
            }

            employeeFile.close();

            if(employeeName.equals("")){
                System.out.println("Employee number does not exist.");
                return;
            }

            System.out.println("\nEmployee #: " + employeeNumber);
            System.out.println("Employee Name: " + employeeName);
            System.out.println("Birthday: " + birthday);

            double firstCutoffHours[] = new double[12];
            double secondCutoffHours[] = new double[12];

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("MM/dd/yyyy");

            BufferedReader attendanceFile =
                    new BufferedReader(new FileReader("attendance.csv"));

            attendanceFile.readLine();

            String record;

            while((record = attendanceFile.readLine()) != null){

                String[] data = record.split(",");

                int empNo = Integer.parseInt(data[0]);

                if(empNo == employeeNumber){

                    LocalDate date =
                            LocalDate.parse(data[1],formatter);

                    int monthIndex = date.getMonthValue()-1;

                    if(monthIndex >= 5){

                        double workedHours =
                                computeHours(data[2],data[3]);

                        if(date.getDayOfMonth() <= 15)
                            firstCutoffHours[monthIndex] += workedHours;
                        else
                            secondCutoffHours[monthIndex] += workedHours;
                    }
                }
            }

            attendanceFile.close();

            String months[]={
                    "January","February","March","April","May",
                    "June","July","August","September","October","November","December"
            };

            for(int month=5; month<=11; month++){

                double gross1 = firstCutoffHours[month] * hourlyRate;
                double gross2 = secondCutoffHours[month] * hourlyRate;

                double combinedSalary = gross1 + gross2;

                double sss = combinedSalary * 0.045;
                double philhealth = combinedSalary * 0.03;
                double pagibig = 100;
                double tax = combinedSalary * 0.10;

                double totalDeductions =
                        sss + philhealth + pagibig + tax;

                double net1 = gross1;
                double net2 = gross2 - totalDeductions;

                System.out.println("\nMonth: " + months[month]);

                System.out.println("Cutoff Date: 1 to 15");
                System.out.println("Total Hours Worked: " + firstCutoffHours[month]);
                System.out.println("Gross Salary: " + gross1);
                System.out.println("Net Salary: " + net1);

                System.out.println("\nCutoff Date: 16 to End");
                System.out.println("Total Hours Worked: " + secondCutoffHours[month]);
                System.out.println("Gross Salary: " + gross2);

                System.out.println("SSS: " + sss);
                System.out.println("PhilHealth: " + philhealth);
                System.out.println("Pag-IBIG: " + pagibig);
                System.out.println("Tax: " + tax);

                System.out.println("Total Deductions: " + totalDeductions);
                System.out.println("Net Salary: " + net2);

                System.out.println("--------------------------------");
            }

        }catch(Exception e){
            System.out.println("Error processing payroll.");
        }
    }

    /*
        PROCESS ALL PAYROLL METHOD

        Processes payroll for all employees.
    */
    static void processAllPayroll(){

        try{

            BufferedReader reader =
                    new BufferedReader(new FileReader("employees.csv"));

            reader.readLine();

            String line;

            while((line = reader.readLine()) != null){

                String[] data = line.split(",");

                int employeeNumber =
                        Integer.parseInt(data[0]);

                processPayroll(employeeNumber);
            }

            reader.close();

        }catch(Exception e){
            System.out.println("Error reading employees.");
        }
    }
}
