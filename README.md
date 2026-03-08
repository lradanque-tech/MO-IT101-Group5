Project Proposal: MotorPH (Core Payroll Automation)

To: MotorPH Management

From: Dev Guild (Software Development Team)




Project Overview: 

As the Dev Guild software development team, we have updated the MotorPH Phase 1 Project Plan to align with the specific 11-week client timeline. This update focuses strictly on the Phase 1 requirements: visualizing the application, presenting employee data, and implementing the automated salary calculation engine.


We have mapped our team members, Mignonette Ainne Deboma, Aviet Eugenio, Mark Neil Leynes, Carole Maye Lomboy, and Angelika Danque, to their specialized roles for this phase.

Problem Statement

Currently, MotorPH requires a more effective solution to manage the financial compensation and information of its expanding workforce.
- Manual Data Management: Lack of a centralized system to present and manage basic employee details.
- Inefficient Salary Calculation: Dependency on manual or non-integrated processes to calculate pay based on time rendered and deductions.
- Operational Scalability: The need for a system that can expand its requirements as the business grows and moves toward opening physical branches.

The Challenge vs. Our Solution

Dev Guild identified that manual payroll calculation is currently your team's biggest bottleneck. It is time-consuming and prone to human error.
- Our Solution: We are developing a high-performance Calculation Engine that automates employee data and salary computation through code. This eliminates the need for manual spreadsheets and ensures data integrity.


Individual Responsibilities & Workload Distribution:

The Design & Data Team (Aviet Eugenio & Mark Neil Leynes)

- Aviet Eugenio: Handles the data side of the UI, ensuring that employee records are correctly mapped to the presentation screen. Her structural layouts provide the essential technical "hooks" required for the calculation engine to display automated payroll results.
- Mark Neil Leynes: Focuses on the "Visualization" and "Prescribed Format" requirements, ensuring the user interface is clean and professional while maintaining the foundational payroll framework. Also makes significant contributions to QA for each team member’s work distribution and ensures it is correct and precise.
   
The Technical Engine Team (Carole Maye Lomboy)

- Carole Maye Lomboy: She is the sole owner of the Code Calculation Engine. Her distribution is heavy. During Weeks 6–8, she constructs the logic for tracking weekly attendance and develops the calculation engine that converts labor hours into gross pay. She is further responsible for automating the deduction formulas for SSS, PhilHealth, Pag-IBIG, and withholding taxes to ensure a precise net weekly salary.

Project Oversight & QA Team (Mignonette Ainne Deboma & Angelika Danque)

- Mignonette Ainne Deboma: Acts as the Project Coordinator to ensure the foundational payroll framework is maintained and aligned with client expectations. She oversees the final submission process and manages the critical revision phase during Weeks 9–10 and the team’s effort estimation.
- Angelika Danque: Serves as the QA Specialist, focusing on verifying that the calculation engine accurately processes payroll data. She is responsible for bug fixing and technical validation to ensure the final output is ready for deployment. As well as the team’s project planning data.



Program Details

The system starts by asking the user to log in using a username and password. Depending on the user type, the program provides different options. If the user logs in as an employee, they can enter their employee number to view their personal details stored in the employees.csv file. If the user logs in as payroll staff, they can process payroll either for a specific employee or for all employees.

The program reads employee information and attendance records from CSV files. It calculates the total working hours based on the employee’s time-in and time-out, considering standard working hours from 8:00 AM to 5:00 PM. The system then computes the gross salary using the employee’s hourly rate and separates the payroll into two cutoff periods: June 1–15 and June 16–30. For the second cutoff, deductions such as SSS, PhilHealth, Pag-IBIG, and tax are applied to determine the employee’s net salary. Finally, the program displays the payroll summary including hours worked, gross salary, deductions, and net salary.

Technical Highlights

As your developers, we are focusing on two "smart" features to ensure reliability:

- The Code-Based Engine: Our salary logic is hard-coded into the system's backend, unlike a spreadsheet, where formulas can be deleted accidentally.
- Centralized Presentation: A unified reference point. Whether you are searching for an employee ID number or name, the data is retrieved immediately and displayed in a neat, professional format.

Solution: Automated Foundation

The primary goal is to develop a functional "Basic Employee & Salary Management" interface.

- Employee Profile Presentation: A digital interface to display essential employee details clearly with user-friendly, organized components.
- Automated Calculation Logic: Implementation of a core code-based engine that automatically computes salary payrolls, including detailed automatic computation of employee attendance and payroll computation.
- Variable Input Integration: The system will calculate totals based on:
    - Weekly Attendance (Hours Worked): Automatic detailed breakdown of tracking or input of total labor hours.
    - Salary Computations (includes Basic Deductions): Standardized subtractions from gross pay to ensure accurate net salary, as well as salary deductions for SSS, PhilHealth, Pag-IBIG, and withholding tax for each employee. 

Prepared by: Dev Guild

Software Development Team

PROJECT PLAN LINK: https://docs.google.com/document/d/16GbEgJ9-S5xMjH30nSOXHnnYusSJmCNkx4JyduHTBcw/edit?usp=sharing
