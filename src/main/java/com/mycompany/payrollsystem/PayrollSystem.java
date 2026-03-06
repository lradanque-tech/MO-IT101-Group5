/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.payrollsystem;

/**
 *
 * @author Angelika Danque
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

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if(!(password.equals("12345") && 
            (username.equals("employee") || username.equals("payroll_staff")))){

            System.out.println("Incorrect username and/or password.");
            return;
        }

        if(username.equals("employee")){

            System.out.println("\n1. Enter your employee number");
            System.out.println("2. Exit");

            int choice = sc.nextInt();

            if(choice == 2) return;

            System.out.print("Enter employee number: ");
            int empNo = sc.nextInt();

            displayEmployee(empNo);
        }

        else{

            while(true){

                System.out.println("\n1. Process Payroll");
                System.out.println("2. Exit");

                int choice = sc.nextInt();

                if(choice == 2) break;

                System.out.println("\n1. One employee");
                System.out.println("2. All employees");
                System.out.println("3. Exit");

                int sub = sc.nextInt();

                if(sub == 3) break;

                if(sub == 1){

                    System.out.print("Enter employee number: ");
                    int empNo = sc.nextInt();

                    processPayroll(empNo);

                }

                else{

                    processAllPayroll();

                }

            }

        }

    }

    static void displayEmployee(int empNo){

        try{

            BufferedReader br = new BufferedReader(new FileReader("employees.csv"));
            String line;

            while((line = br.readLine()) != null){

                String data[] = line.split(",");

                if(Integer.parseInt(data[0]) == empNo){

                    System.out.println("\nEmployee Number: " + data[0]);
                    System.out.println("Employee Name: " + data[1]);
                    System.out.println("Birthday: " + data[2]);
                    return;

                }

            }

            System.out.println("Employee number does not exist.");

        }
        catch(Exception e){
            System.out.println("Error reading employees file.");
        }

    }

    static double computeHours(String timeIn,String timeOut){

        LocalTime in = LocalTime.parse(timeIn);
        LocalTime out = LocalTime.parse(timeOut);

        LocalTime start = LocalTime.of(8,0);
        LocalTime end = LocalTime.of(17,0);

        if(in.isBefore(start)) in = start;
        if(out.isAfter(end)) out = end;

        double hours = Duration.between(in,out).toMinutes()/60.0;

        return hours;
    }

    static void processPayroll(int empNo){

        try{

            BufferedReader empFile = new BufferedReader(new FileReader("employees.csv"));
            String line;

            String name = "";
            String birthday = "";
            double rate = 0;

            while((line = empFile.readLine()) != null){

                String data[] = line.split(",");

                if(Integer.parseInt(data[0]) == empNo){

                    name = data[1];
                    birthday = data[2];
                    rate = Double.parseDouble(data[3]);
                }

            }

            if(name.equals("")){
                System.out.println("Employee number does not exist.");
                return;
            }

            System.out.println("\nEmployee #: " + empNo);
            System.out.println("Employee Name: " + name);
            System.out.println("Birthday: " + birthday);

            BufferedReader attFile = new BufferedReader(new FileReader("attendance.csv"));
            String att;

            double hours1 = 0;
            double hours2 = 0;

            DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            while((att = attFile.readLine()) != null){

                String data[] = att.split(",");

                if(Integer.parseInt(data[0]) == empNo){

                    LocalDate date = LocalDate.parse(data[1],df);

                    if(date.getMonthValue() >= 6){

                        double h = computeHours(data[2],data[3]);

                        if(date.getDayOfMonth() <= 15)
                            hours1 += h;
                        else
                            hours2 += h;

                    }

                }

            }

            double gross1 = hours1 * rate;
            double gross2 = hours2 * rate;

            double sss = gross2 * 0.045;
            double philhealth = gross2 * 0.03;
            double pagibig = 100;
            double tax = gross2 * 0.10;

            double deductions = sss + philhealth + pagibig + tax;

            double net1 = gross1;
            double net2 = gross2 - deductions;

            System.out.println("\nCutoff Date: June 1 to June 15");
            System.out.println("Total Hours Worked: " + hours1);
            System.out.println("Gross Salary: " + gross1);
            System.out.println("Net Salary: " + net1);

            System.out.println("\nCutoff Date: June 16 to June 30");
            System.out.println("Total Hours Worked: " + hours2);
            System.out.println("Gross Salary: " + gross2);
            System.out.println("SSS: " + sss);
            System.out.println("PhilHealth: " + philhealth);
            System.out.println("Pag-IBIG: " + pagibig);
            System.out.println("Tax: " + tax);
            System.out.println("Total Deductions: " + deductions);
            System.out.println("Net Salary: " + net2);

        }
        catch(Exception e){
            System.out.println("Error processing payroll.");
        }

    }

    static void processAllPayroll(){

        try{

            BufferedReader br = new BufferedReader(new FileReader("employees.csv"));
            String line;

            while((line = br.readLine()) != null){

                String data[] = line.split(",");

                int empNo = Integer.parseInt(data[0]);

                processPayroll(empNo);

                System.out.println("--------------------------------");

            }

        }
        catch(Exception e){
            System.out.println("Error reading employees.");
        }

    }

}