
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

    /*
        MAIN METHOD

        This is the starting point of the program.
        It asks the user to log in and determines whether
        the user is an employee or payroll staff.
    */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // =========================
        // LOGIN SECTION
        // =========================
        // The program asks for username and password.
        // Only "employee" and "payroll_staff" are valid usernames.

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Validate login credentials
        if(!(password.equals("12345") &&
           (username.equals("employee") || username.equals("payroll_staff")))){

            System.out.println("Incorrect username and/or password.");
            scanner.close();
            return;
        }

        // =========================
        // EMPLOYEE MODE
        // =========================
        // If the user logs in as "employee",
        // they can view their personal details.

        if(username.equals("employee")){

            System.out.println("\n1. Enter your employee number");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();

            if(choice == 2){
                scanner.close();
                return;
            }

            System.out.print("Enter employee number: ");
            int employeeNumber = scanner.nextInt();

            displayEmployee(employeeNumber);
        }

        // =========================
        // PAYROLL STAFF MODE
        // =========================
        // Payroll staff can process payroll
        // for one employee or for all employees.

        else{

            System.out.println("\n1. Process Payroll");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();

            if(choice == 2){
                scanner.close();
                return;
            }

            System.out.println("\n1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit");

            int subChoice = scanner.nextInt();

            if(subChoice == 3){
                scanner.close();
                return;
            }

            if(subChoice == 1){

                System.out.print("Enter employee number: ");
                int employeeNumber = scanner.nextInt();

                processPayroll(employeeNumber);
            }

            else{
                processAllPayroll();
            }
        }

        scanner.close();
    }

    /*
        DISPLAY EMPLOYEE METHOD

        This method reads the employees.csv file
        and displays the employee number, name,
        and birthday if the employee exists.
    */
    static void displayEmployee(int employeeNumber){

        try{

            BufferedReader reader =
                    new BufferedReader(new FileReader("employees.csv"));

            String line;

            reader.readLine(); // skip CSV header

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

        }
        catch(Exception e){
            System.out.println("Error reading employees file.");
        }
    }

    /*
        COMPUTE HOURS METHOD

        This method calculates the total hours worked
        based on login and logout time.

        RULES:
        - Work hours are only counted between 8:00 AM and 5:00 PM.
        - If the employee logs in earlier than 8:00 AM,
          the time is adjusted to 8:00 AM.
        - If the employee logs out later than 5:00 PM,
          the time is adjusted to 5:00 PM.
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

        double hours =
                Duration.between(in,out).toMinutes()/60.0;

        return hours;
    }

    /*
        PROCESS PAYROLL METHOD

        This method calculates payroll for a specific employee.

        Steps:
        1. Read employee information from employees.csv.
        2. Read attendance records from attendance.csv.
        3. Compute hours worked per cutoff.
        4. Calculate gross salary.
        5. Apply government deductions.
        6. Display payroll from June to December.
    */
    static void processPayroll(int employeeNumber){

        try{

            BufferedReader employeeFile =
                    new BufferedReader(new FileReader("employees.csv"));

            String line;

            employeeFile.readLine(); // skip header

            String employeeName = "";
            String birthday = "";
            double hourlyRate = 0;

            // Search for employee
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

            // Arrays to store hours worked for each month
            double firstCutoffHours[] = new double[12];
            double secondCutoffHours[] = new double[12];

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("MM/dd/yyyy");

            BufferedReader attendanceFile =
                    new BufferedReader(new FileReader("attendance.csv"));

            attendanceFile.readLine(); // skip header

            String record;

            while((record = attendanceFile.readLine()) != null){

                String[] data = record.split(",");

                int empNo = Integer.parseInt(data[0]);

                if(empNo == employeeNumber){

                    LocalDate date =
                            LocalDate.parse(data[1],formatter);

                    int monthIndex = date.getMonthValue()-1;

                    if(monthIndex >= 5){ // June onwards

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

            // Month names for display
            String months[]={
                    "January","February","March","April","May",
                    "June","July","August","September","October","November","December"
            };

            // Display payroll per month
            for(int month=5; month<=11; month++){

                double gross1 = firstCutoffHours[month] * hourlyRate;
                double gross2 = secondCutoffHours[month] * hourlyRate;

                double combinedSalary = gross1 + gross2;

                // Government deductions
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

        }
        catch(Exception e){
            System.out.println("Error processing payroll.");
        }
    }

    /*
        PROCESS ALL PAYROLL METHOD

        This method reads every employee from employees.csv
        and processes payroll for each employee.
    */
    static void processAllPayroll(){

        try{

            BufferedReader reader =
                    new BufferedReader(new FileReader("employees.csv"));

            String line;

            reader.readLine(); // skip header

            while((line = reader.readLine()) != null){

                String[] data = line.split(",");

                int employeeNumber =
                        Integer.parseInt(data[0]);

                processPayroll(employeeNumber);
            }

            reader.close();
        }

        catch(Exception e){
            System.out.println("Error reading employees.");
        }
    }
}