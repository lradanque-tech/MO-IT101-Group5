
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
    - A 1-hour lunch break is deducted from daily working hours.
    - Payroll is displayed from June to December.
    - Each month has two cutoff periods:
        1–15
        16-end of month
    - Government deductions are calculated using the
      combined salary of both cutoffs.
*/

import java.util.Scanner; 
// Used for user input from the console

import java.io.BufferedReader;
// Used to efficiently read text from files

import java.io.FileReader;
// Used to open and read CSV files

import java.time.LocalTime;
// Used to handle time values (time-in and time-out)

import java.time.LocalDate;
// Used to handle dates from attendance records

import java.time.Duration;
// Used to compute time differences (hours worked)

import java.time.format.DateTimeFormatter;
// Used to convert string dates into LocalDate format

/*
    Main class of the payroll system.
    It handles user login, employee lookup,
    and payroll processing using CSV files.
*/
public class PayrollSystem {

    // ================= HELPER METHOD =================
    // This method ensures the user inputs a valid integer
    // If the input is not an integer, the program prints an error
    // and exits to prevent invalid data from breaking the system.  
    static int getValidIntegerInput(Scanner scanner, String errorMessage) {
        
        // Check if next input is NOT an integer
        // The program exits immediately to prevent invalid numeric input
        // from affecting menu selection or employee number processing
        if(!scanner.hasNextInt()){
            System.out.println(errorMessage);
            scanner.close(); // Close scanner to prevent resource leak
            System.exit(0); // Stop program execution immediately due to invalid input
        }
        return scanner.nextInt();
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Menu options stored as String variables
        String employeeOption1 = "Enter your employee number";
        String employeeOption2 = "Exit";

        String payrollOption1 = "Process Payroll";
        String payrollOption2 = "Exit";

        String payrollSubOption1 = "One employee";
        String payrollSubOption2 = "All employees";
        String payrollSubOption3 = "Exit";

        // ================= LOGIN =================
        // Verifies user credentials before allowing access to the system
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        // Only allow access if password is correct AND username is valid
        if(!(password.equals("12345") &&
           (username.equals("employee") || username.equals("payroll_staff")))){

            System.out.println("Incorrect username and/or password.");
            scanner.close();
            return;
        }

        // ================= EMPLOYEE MODE =================
        // Allows employee to view their personal information only
        if(username.equals("employee")){

            System.out.println("\n1. " + employeeOption1);
            System.out.println("2. " + employeeOption2);

            int mainMenuChoice = getValidIntegerInput(scanner, "Invalid option.");

            if(mainMenuChoice == 2){
                scanner.close();
                return;
            }

            System.out.print("Enter employee number: ");
            int employeeNumber = getValidIntegerInput(scanner, "Invalid employee number.");

            displayEmployee(employeeNumber);
        }

        // ================= PAYROLL STAFF MODE =================
        // Allows payroll staff to process salary computations
        // for one employee or all employees
        else{

            System.out.println("\n1. " + payrollOption1);
            System.out.println("2. " + payrollOption2);

            int mainMenuChoice = getValidIntegerInput(scanner, "Invalid option.");

            if(mainMenuChoice == 2){
                scanner.close();
                return;
            }

            System.out.println("\n1. " + payrollSubOption1);
            System.out.println("2. " + payrollSubOption2);
            System.out.println("3. " + payrollSubOption3);

            int payrollMenuChoice = getValidIntegerInput(scanner, "Invalid option.");

            if(payrollMenuChoice == 3){
                scanner.close();
                return;
            }

            if(payrollMenuChoice == 1){

                System.out.print("Enter employee number: ");
                int employeeNumber = getValidIntegerInput(scanner, "Invalid employee number.");

                processPayroll(employeeNumber);

            }else if(payrollMenuChoice == 2){

                processAllPayroll();

            }else{
                System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    /*
        Displays the basic employee information based on the employee number.
        This method reads the employees.csv file and prints the matching
        employee's number, name, and birthday.
    */
    static void displayEmployee(int employeeNumber){

        try(BufferedReader reader =
                new BufferedReader(new FileReader("employees.csv"))){

            reader.readLine(); // skip header

            String employeeLine;

            while((employeeLine = reader.readLine()) != null){
                 
                // Split CSV row into fields:
                // [0] Employee Number
                // [1] Employee Name
                // [2] Birthday
                String[] employeeData = employeeLine.split(",");

                int empNo = Integer.parseInt(employeeData[0]);

                if(empNo == employeeNumber){

                    System.out.println("\nEmployee Number: " + employeeData[0]);
                    System.out.println("Employee Name: " + employeeData[1]);
                    System.out.println("Birthday: " + employeeData[2]);

                    return;
                }
            }

            System.out.println("Employee number does not exist.");

        }catch(Exception e){
            System.out.println("Error reading employees file.");
        }
    }

    /*
        COMPUTE HOURS METHOD

        Computes the employee's worked hours for one attendance record.
        Time is limited to official work hours only, and lunch is deducted
        when the worked duration is long enough to include a break.
    */
    static double computeHours(String timeIn, String timeOut){

        // Convert string times into LocalTime objects
        LocalTime timeInValue = LocalTime.parse(timeIn);
        LocalTime timeOutValue = LocalTime.parse(timeOut);

        // Define official working hours
        LocalTime workStart = LocalTime.of(8,0);
        LocalTime workEnd = LocalTime.of(17,0);

        // Adjust times within allowed working hours
        if(timeInValue.isBefore(workStart)) timeInValue = workStart;
        if(timeOutValue.isAfter(workEnd)) timeOutValue = workEnd;

        // Compute total hours worked
        double hours =
                Duration.between(timeInValue,timeOutValue).toMinutes()/60.0;

        // Lunch is deducted only when the employee worked long enough
        // to reasonably include a lunch break in the shift
        if(hours >= 5) hours -= 1;

        // Prevent negative hours
        if(hours < 0) hours = 0;

        return hours;
    }

    /*
        Processes the payroll of one employee by reading employee details
        and attendance records, grouping worked hours by month and cutoff,
        and calculating salary, deductions, and net pay.
    */
    static void processPayroll(int employeeNumber){

        // Reads employees.csv and attendance.csv files
        try(BufferedReader employeeFile =
                new BufferedReader(new FileReader("employees.csv")); 
            BufferedReader attendanceFile =
                new BufferedReader(new FileReader("attendance.csv"))){

            employeeFile.readLine();

            String employeeLine;

            String employeeName = "";
            String birthday = "";
            double hourlyRate = 0;

            // Find employee details
            while((employeeLine = employeeFile.readLine()) != null){

                // [0] Employee Number
                // [1] Name
                // [2] Birthday
                // [3] Hourly Rate
                String[] employeeData = employeeLine.split(",");

                int empNo = Integer.parseInt(employeeData[0]);

                if(empNo == employeeNumber){

                    employeeName = employeeData[1];
                    birthday = employeeData[2];
                    hourlyRate = Double.parseDouble(employeeData[3]);
                    break;
                }
            }

            if(employeeName.isEmpty()){
                System.out.println("Employee number does not exist.");
                return;
            }

            System.out.println("\nEmployee #: " + employeeNumber);
            System.out.println("Employee Name: " + employeeName);
            System.out.println("Birthday: " + birthday);

            // Arrays have 12 elements because each index represents one month of the year
            // Index 0 = January, Index 11 = December
            // Only June (index 5) to December (index 11) are used for payroll output
            double[] firstCutoffHours = new double[12];
            double[] secondCutoffHours = new double[12];

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("MM/dd/yyyy");

            attendanceFile.readLine();

            String attendanceLine;

            while((attendanceLine = attendanceFile.readLine()) != null){
                
                // [0] Employee Number
                // [1] Date
                // [2] Time In
                // [3] Time Out
                String[] attendanceData = attendanceLine.split(",");

                int empNo = Integer.parseInt(attendanceData[0]);

                if(empNo == employeeNumber){

                    LocalDate date =
                            LocalDate.parse(attendanceData[1],formatter);

                    int monthIndex = date.getMonthValue()-1;

                    // Only June to December are processed because the payroll report
                    // required by the project starts from June and ends in December
                    if(monthIndex >= 5){

                        double workedHours =
                                computeHours(attendanceData[2],attendanceData[3]);

                        // Divide into cutoff periods
                        // 1–15 = first cutoff
                        // 16–end of month = second cutoff
                        if(date.getDayOfMonth() <= 15)
                            firstCutoffHours[monthIndex] += workedHours;
                        else
                            secondCutoffHours[monthIndex] += workedHours;
                    }
                }
            }

            String[] months={
                "January","February","March","April","May",
                "June","July","August","September","October","November","December"
            };

            for(int month=5; month<=11; month++){

                // Compute gross salary
                double gross1 = firstCutoffHours[month] * hourlyRate;
                double gross2 = secondCutoffHours[month] * hourlyRate;
                
                // Combined salary is used because monthly deductions are based
                // on the employee's total pay for both cutoff periods
                double combinedSalary = gross1 + gross2;

                // Initialize deductions
                double sss = 0, philhealth = 0, pagibig = 0, tax = 0;

                // Apply deductions only if salary exists
                if(combinedSalary > 0){
                    sss = combinedSalary * 0.045;
                    philhealth = combinedSalary * 0.03;
                    pagibig = 100;
                    tax = combinedSalary * 0.10;
                }

                double totalDeductions =
                        sss + philhealth + pagibig + tax;

                // First cutoff has no deductions
                double net1 = gross1;
                
                // All deductions applied to second cutoff
                // Deductions are subtracted only from the second cutoff
                // so they are not applied twice within the same month
                double net2 = gross2 - totalDeductions;
                
                // Net salary is set to zero if deductions are greater than the
                // second cutoff salary so the program will not display a negative payout
                if(net2 < 0){
                    net2 = 0;
                }

                System.out.println("\nMonth: " + months[month]);

                System.out.println("Cutoff Date: " + months[month] + " 1 to 15");
                System.out.println("Total Hours Worked: " + firstCutoffHours[month]);
                System.out.println("Gross Salary: " + gross1);
                System.out.println("Net Salary: " + net1);

                // Second cutoff includes all remaining days of the month (16–30 or 16–31)
                // depending on the number of days in the month
                System.out.println("\nCutoff Date: " + months[month] + " 16 to the end of the month");
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
        Processes payroll for all employees listed in employees.csv.
        This method reads each employee number from the file and
        calls processPayroll for each one.
    */
    static void processAllPayroll(){

        try(BufferedReader reader =
                new BufferedReader(new FileReader("employees.csv"))){

            reader.readLine();

            String employeeLine;

            while((employeeLine = reader.readLine()) != null){

                String[] employeeData = employeeLine.split(",");

                int employeeNumber =
                        Integer.parseInt(employeeData[0]);

                processPayroll(employeeNumber);
            }

        }catch(Exception e){
            System.out.println("Error reading employees.");
        }
    }
}
